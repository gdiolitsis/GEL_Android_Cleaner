package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class GELCleaner extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext cb) throws JSONException {
        switch (action) {
            case "clean": {
                final String mode = (args != null && args.length() > 0) ? args.optString(0, "safe") : "safe";
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject result = "aggressive".equalsIgnoreCase(mode)
                                ? doAggressiveClean(cordova.getContext())
                                : doSafeClean(cordova.getContext());
                        result.put("mode", mode);
                        cb.success(result);
                    } catch (Exception e) {
                        cb.error("clean_failed: " + e.getMessage());
                    }
                });
                return true;
            }
            case "requestAllFiles": {
                cordova.getThreadPool().execute(() -> {
                    try {
                        if (Build.VERSION.SDK_INT >= 30) {
                            if (!Environment.isExternalStorageManager()) {
                                Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                i.setData(Uri.parse("package:" + cordova.getContext().getPackageName()));
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                cordova.getActivity().startActivity(i);
                                cb.success("REQUESTED");
                            } else {
                                cb.success("ALREADY_GRANTED");
                            }
                        } else {
                            cb.success("OK_OLD_SDK");
                        }
                    } catch (Exception e) {
                        cb.error("req_failed: " + e.getMessage());
                    }
                });
                return true;
            }
            case "cleanRAM": {
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = cleanRAMNow(cordova.getContext());
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("ram_failed: " + e.getMessage());
                    }
                });
                return true;
            }
            case "cpuInfo": {
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = cpuInfo();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("cpu_failed: " + e.getMessage());
                    }
                });
                return true;
            }
        }
        return false;
    }

    private JSONObject doSafeClean(Context ctx) throws Exception {
        // App cache only + known external cache dirs (non-destructive)
        if (ctx.getCacheDir() != null) wipeDir(ctx.getCacheDir());
        if (ctx.getExternalCacheDir() != null) wipeDir(ctx.getExternalCacheDir());

        String[] softSpots = new String[]{
                Environment.getExternalStorageDirectory() + "/Download/.temp",
                Environment.getExternalStorageDirectory() + "/Pictures/.thumbnails",
                Environment.getExternalStorageDirectory() + "/DCIM/.thumbnails"
        };
        for (String p : softSpots) wipeDir(new File(p));

        JSONObject o = new JSONObject();
        o.put("note", "Safe clean done");
        return o;
    }

    private JSONObject doAggressiveClean(Context ctx) throws Exception {
        // Everything from safe + broader Android/data & cache-like roots (best-effort)
        doSafeClean(ctx);

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String[] hotSpots = new String[]{
                root + "/Android/data/",
                root + "/Android/media/",
                root + "/tmp",
                root + "/.cache"
        };
        for (String p : hotSpots) wipeDir(new File(p));

        // Light background kill
        int killed = killBackgroundApps(ctx);

        JSONObject o = new JSONObject();
        o.put("note", "Aggressive clean done");
        o.put("killed", killed);
        return o;
    }

    private void wipeDir(File f) {
        if (f == null || !f.exists()) return;
        if (f.isFile()) return;
        File[] list = f.listFiles();
        if (list == null) return;
        for (File child : list) deleteRecursively(child);
    }

    private void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) for (File k : kids) deleteRecursively(k);
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
                    try { am.killBackgroundProcesses(pi.processName); count++; } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return count;
    }

    private JSONObject cleanRAMNow(Context ctx) throws Exception {
        int killed = killBackgroundApps(ctx);
        JSONObject o = new JSONObject();
        o.put("killed", killed);
        o.put("status", "done");
        return o;
    }

    private JSONObject cpuInfo() throws JSONException {
        JSONObject cpu = new JSONObject();
        cpu.put("cores", Runtime.getRuntime().availableProcessors());
        cpu.put("abi", (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0) ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI);
        cpu.put("sdk", Build.VERSION.SDK_INT);
        return cpu;
    }
}
