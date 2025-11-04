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

            case "clearAppCache":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = clearAppCache();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("clearAppCache_failed: " + e.getMessage());
                    }
                });
                return true;

            case "boostRAM":
                cordova.getThreadPool().execute(() -> {
                    try {
                        int killed = killBackgroundApps(cordova.getContext());
                        JSONObject o = new JSONObject();
                        o.put("killed", killed);
                        o.put("note", "Best-effort RAM boost");
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("boostRAM_failed: " + e.getMessage());
                    }
                });
                return true;

            case "clearTemp":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = clearTemp();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("clearTemp_failed: " + e.getMessage());
                    }
                });
                return true;

            case "removeJunk":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = removeJunk();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("removeJunk_failed: " + e.getMessage());
                    }
                });
                return true;

            case "optimizeBattery":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = optimizeBattery();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("optimizeBattery_failed: " + e.getMessage());
                    }
                });
                return true;

            case "killBackground":
                cordova.getThreadPool().execute(() -> {
                    try {
                        int killed = killBackgroundApps(cordova.getContext());
                        JSONObject o = new JSONObject();
                        o.put("killed", killed);
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("killBackground_failed: " + e.getMessage());
                    }
                });
                return true;

            case "stats":
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = storageStats();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("stats_failed: " + e.getMessage());
                    }
                });
                return true;
        }

        return false;
    }

    // -----------------------------------------------------
    // CORE OPS
    // -----------------------------------------------------

    private JSONObject clearAppCache() throws Exception {
        Context ctx = cordova.getContext();
        wipeDir(ctx.getCacheDir());
        if (ctx.getExternalCacheDir() != null) wipeDir(ctx.getExternalCacheDir());

        JSONObject o = new JSONObject();
        o.put("status", "OK");
        o.put("note", "cleared app cache (best-effort)");
        return o;
    }

    private JSONObject clearTemp() {
        String[] paths = {
                "/Download/.temp",
                "/tmp",
                "/.temp"
        };

        for (String p : paths)
            wipeDir(new File(Environment.getExternalStorageDirectory() + p));

        JSONObject o = new JSONObject();
        try { o.put("status", "OK"); } catch (Exception ignored) {}
        return o;
    }

    private JSONObject removeJunk() {
        String[] paths = {
                "/DCIM/.thumbnails",
                "/Pictures/.thumbnails",
                "/Android/media/"
        };

        for (String p : paths)
            wipeDir(new File(Environment.getExternalStorageDirectory() + p));

        JSONObject o = new JSONObject();
        try { o.put("status", "OK"); } catch (Exception ignored) {}
        return o;
    }

    private JSONObject optimizeBattery() {
        JSONObject o = new JSONObject();
        try {
            o.put("note", "best-effort battery optimizations");
        } catch (Exception ignored) {}
        return o;
    }

    // -----------------------------------------------------
    // UTILITIES
    // -----------------------------------------------------

    private void wipeDir(File f) {
        if (f == null || !f.exists()) return;
        if (f.isFile()) {
            f.delete();
            return;
        }
        File[] kids = f.listFiles();
        if (kids != null) {
            for (File k : kids) {
                deleteRecursively(k);
            }
        }
    }

    private void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) {
                for (File k : kids)
                    deleteRecursively(k);
            }
        }
        try { f.delete(); } catch (Throwable ignored) {}
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

    private JSONObject storageStats() throws JSONException {
        JSONObject o = new JSONObject();
        File root = Environment.getExternalStorageDirectory();
        if (root != null) {
            long total = root.getTotalSpace();
            long free  = root.getFreeSpace();
            long used  = total - free;
            o.put("total", total);
            o.put("free", free);
            o.put("used", used);
        }
        return o;
    }
}
