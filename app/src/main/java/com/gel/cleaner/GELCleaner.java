package com.gel.cleaner;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    /* ======================================================
     *   CPU+RAM INFO  (one-time info)
     * ====================================================== */
    public static void cpuInfo(Activity act, LogCallback cb) {
        try {
            String cpu = android.os.Build.HARDWARE;
            int cores = Runtime.getRuntime().availableProcessors();

            ActivityManager am = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long free = mi.availMem / (1024 * 1024);
            long total = mi.totalMem / (1024 * 1024);

            cb.log("CPU: " + cpu + " | Cores: " + cores, false);
            cb.log("RAM: " + free + "MB free / " + total + "MB total", false);

        } catch (Exception e) {
            cb.log("CPU/RAM error: " + e.getMessage(), true);
        }
    }


    /* ======================================================
     *   CPU+RAM LIVE (updates every second while screen alive)
     * ====================================================== */

    private static boolean liveRunning = false;

    public static void cpuLive(Activity act, LogCallback cb) {
        liveRunning = false;               // reset
        Handler handler = new Handler(Looper.getMainLooper());

        cb.log("🔄 Live CPU/RAM started…", false);

        liveRunning = true;
        Runnable loop = new Runnable() {
            @Override
            public void run() {

                if (!liveRunning) return;

                try {
                    int cores = Runtime.getRuntime().availableProcessors();
                    ActivityManager am = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    long free = mi.availMem / (1024 * 1024);
                    long total = mi.totalMem / (1024 * 1024);

                    cb.log("Live → CPU cores: " + cores + " | RAM free: " + free + "MB", false);

                } catch (Exception e) {
                    cb.log("Live error: " + e.getMessage(), true);
                }

                handler.postDelayed(this, 1000);    // 1sec
            }
        };

        handler.post(loop);
    }

    public static void stopLive() {
        liveRunning = false;
    }


    /* ======================================================
     *   STUBS — (ώστε να χτίζει χωρίς σφάλματα)
     *   Αυτές λειτουργούν όπως πριν
     * ====================================================== */

    public static void cleanRAM(Activity a, LogCallback cb) {
        cb.log("Clean RAM → stub OK", false);
    }

    public static void safeClean(Activity a, LogCallback cb) {
        cb.log("Safe clean → stub OK", false);
    }

    public static void deepClean(Activity a, LogCallback cb) {
        cb.log("Deep clean → stub OK", false);
    }

    public static void mediaJunk(Activity a, LogCallback cb) {
        cb.log("Media junk → stub OK", false);
    }

    public static void browserCache(Activity a, LogCallback cb) {
        cb.log("Browser clean → stub OK", false);
    }

    public static void tempClean(Activity a, LogCallback cb) {
        cb.log("Temp clean → stub OK", false);
    }

    public static void boostBattery(Activity a, LogCallback cb) {
        cb.log("Battery boost → stub OK", false);
    }

    public static void killApps(Activity a, LogCallback cb) {
        cb.log("Kill apps → stub OK", false);
    }

    public static void cleanAll(Activity a, LogCallback cb) {
        cb.log("Clean ALL → stub OK", false);
    }

}
