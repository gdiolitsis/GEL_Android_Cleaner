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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
            case "cleanMedia": {
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = cleanMedia();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("media_failed: " + e.getMessage());
                    }
                });
                return true;
            }
            case "cleanBrowser": {
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = cleanBrowser();
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("browser_failed: " + e.getMessage());
                    }
                });
                return true;
            }
            case "cleanApp": {
                final String pkg = (args != null && args.length() > 0) ? args.optString(0, "") : "";
                cordova.getThreadPool().execute(() -> {
                    try {
                        JSONObject o = cleanApp(pkg);
                        cb.success(o);
                    } catch (Exception e) {
                        cb.error("app_failed: " + e.getMessage());
                    }
                });
                return true;
            }
        }
        return false;
    }

    private JSONObject doSafeClean(Context ctx) throws Exception {
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
        doSafeClean(ctx);
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String[] hotSpots = new String[]{
                root + "/Android/data/",
                root + "/Android/media/",
                root + "/tmp",
                root + "/.cache"
        };
        for (String p : hotSpots) wipeDir(new File(p));
        int killed = killBackgroundApps(ctx);
        JSONObject o = new JSONObject();
        o.put("note", "Aggressive clean done");
        o.put("killed", killed);
        return o;
    }

    private JSONObject cleanMedia() throws JSONException {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String[] targets = new String[]{
                root + "/DCIM/.thumbnails",
                root + "/Pictures/.thumbnails",
                root + "/Download/.temp",
                root + "/Movies/.temp",
                root + "/.cache"
        };
        long bytes = 0;
        for (String p : targets) bytes += wipeDir(new File(p));
        JSONObject o = new JSONObject();
        o.put("freed_estimate", bytes);
        o.put("targets", targets.length);
        return o;
    }

    private JSONObject cleanBrowser() throws JSONException {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String[] pkgs = new String[]{
                "com.android.chrome",
                "org.mozilla.firefox",
                "com.brave.browser",
                "com.microsoft.emmx"
        };
        long bytes = 0;
        for (String pkg : pkgs) {
            bytes += wipeDir(new File(root + "/Android/data/" + pkg + "/cache"));
            bytes += wipeDir(new File(root + "/Android/data/" + pkg + "/files/Download/.temp"));
        }
        JSONObject o = new JSONObject();
        o.put("freed_estimate", bytes);
        o.put("browsers", pkgs.length);
        return o;
    }

    private JSONObject cleanApp(String pkg) throws JSONException {
        JSONObject o = new JSONObject();
        if (pkg == null || pkg.trim().isEmpty()) {
            o.put("note", "empty package");
            return o;
        }
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        long bytes = 0;
        bytes += wipeDir(new File(root + "/Android/data/" + pkg + "/cache"));
        bytes += wipeDir(new File(root + "/Android/data/" + pkg + "/files/.temp"));
        o.put("package", pkg);
        o.put("freed_estimate", bytes);
        return o;
    }

    // Deletes content; returns rough bytes estimate removed (best-effort)
    private long wipeDir(File f) {
        long freed = 0;
        if (f == null || !f.exists()) return 0;
        File[] list = f.listFiles();
        if (list == null) return 0;
        for (File child : list) freed += deleteRecursively(child);
        return freed;
    }

    private long deleteRecursively(File f) {
        long sz = 0;
        try {
            if (f.isDirectory()) {
                File[] kids = f.listFiles();
                if (kids != null) for (File k : kids) sz += deleteRecursively(k);
            } else {
                sz += f.length();
            }
            // best-effort
            f.delete();
        } catch (Throwable ignored) {}
        return sz;
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

    private JSONObject cleanRAMNow(Context ctx) throws JSONException {
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
        // try basic CPU % (delta from /proc/stat) — single snapshot (UI will poll)
        cpu.put("percent", readCpuPercentOnce());
        return cpu;
    }

    // Crude CPU% single read — UI will call repeatedly and plot a sparkline
    private int readCpuPercentOnce() {
        try {
            long[] a = readCpuStat();
            Thread.sleep(200);
            long[] b = readCpuStat();
            long idle = b[3] - a[3];
            long total = (b[0]+b[1]+b[2]+b[3]+b[4]+b[5]+b[6]) - (a[0]+a[1]+a[2]+a[3]+a[4]+a[5]+a[6]);
            if (total <= 0) return 0;
            return (int) Math.max(0, Math.min(100, (100*(total-idle)/total)));
        } catch (Throwable t) {
            return 0;
        }
    }

    // returns first 7 fields of 'cpu  ' line
    private long[] readCpuStat() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
        String line = br.readLine(); br.close();
        if (line == null || !line.startsWith("cpu")) return new long[]{0,0,0,0,0,0,0};
        String[] sp = line.trim().split("\\s+");
        long[] v = new long[7];
        for (int i=1; i<=7 && i<sp.length; i++) v[i-1] = Long.parseLong(sp[i]);
        return v;
    }
}
