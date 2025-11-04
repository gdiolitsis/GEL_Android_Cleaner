package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class GELCleaner extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext cb) throws JSONException {
        switch (action) {
            case "clean":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject result = doAggressiveClean(cordova.getContext());
                        cb.success(result);
                    } catch (Exception e) {
                        cb.error("clean_failed: " + e.getMessage());
                    }
                });
                return true;

            case "kill":
                cordova.getThreadPool().execute(() -> {
                    try {
                        int killed = killBackgroundApps(cordova.getContext());
                        JSONObject o = new JSONObject();
                        o.put("killed", killed);
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("kill_failed: " + e.getMessage());
                    }
                });
                return true;

            case "stats":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject s = storageStats();
                        cb.success(s);
                    } catch (Exception e) {
                        cb.error("stats_failed: " + e.getMessage());
                    }
                });
                return true;
        }
        return false;
    }

    private JSONObject doAggressiveClean(Context ctx) throws Exception {
        long before = dirSize(getExternalRoot());

        // 1) App cache dirs
        wipeDir(ctx.getCacheDir());
        if (ctx.getExternalCacheDir() != null) wipeDir(ctx.getExternalCacheDir());

        // 2) Common temp/cache paths (external). Requires MANAGE_EXTERNAL_STORAGE on A11+ for full effect.
        String[] hotSpots = new String[]{
                Environment.getExternalStorageDirectory() + "/Android/data/",
                Environment.getExternalStorageDirectory() + "/Android/media/",
                Environment.getExternalStorageDirectory() + "/DCIM/.thumbnails",
                Environment.getExternalStorageDirectory() + "/Download/.temp",
                Environment.getExternalStorageDirectory() + "/Pictures/.thumbnails",
                Environment.getExternalStorageDirectory() + "/tmp",
                Environment.getExternalStorageDirectory() + "/.cache"
        };
        for (String p : hotSpots) {
            wipeDir(new File(p));
        }

        // 3) Kill background processes (best-effort)
        int killed = killBackgroundApps(ctx);

        long after = dirSize(getExternalRoot());
        long freed = Math.max(0, before - after);

        JSONObject o = new JSONObject();
        o.put("freedBytes_estimate", freed);
        o.put("killed", killed);
        o.put("sdk", Build.VERSION.SDK_INT);
        o.put("note", "Aggressive clean done (best-effort; scoped storage may limit)");
        return o;
    }

    private File getExternalRoot() {
        return Environment.getExternalStorageDirectory();
    }

    private void wipeDir(File f) {
        if (f == null || !f.exists()) return;
        if (f.isFile()) {
            // no-op
            return;
        }
        File[] list = f.listFiles();
        if (list == null) return;
        for (File child : list) {
            deleteRecursively(child);
        }
    }

    private void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) for (File k : kids) deleteRecursively(k);
        }
        try {
            // Best-effort: ignore failures
            f.delete();
        } catch (Throwable ignored) {}
    }

    private int killBackgroundApps(Context ctx) {
        int count = 0;
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (ActivityManager.RunningAppProcessInfo pi : am.getRunningAppProcesses()) {
                    try {
                        am.killBackgroundProcesses(pi.processName);
                        count++;
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    private long dirSize(File f) {
        if (f == null || !f.exists()) return 0;
        if (f.isFile()) return f.length();
        long s = 0;
        File[] kids = f.listFiles();
        if (kids != null) {
            for (File k : kids) s += dirSize(k);
        }
        return s;
    }

    private JSONObject storageStats() throws JSONException {
        JSONObject o = new JSONObject();
        File root = getExternalRoot();
        if (root != null) {
            long total = root.getTotalSpace();
            long free  = root.getFreeSpace();
            long used  = Math.max(0, total - free);
            o.put("total", total);
            o.put("free", free);
            o.put("used", used);
        }
        return o;
    }
}
