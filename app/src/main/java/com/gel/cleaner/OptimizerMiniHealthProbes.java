package com.gel.cleaner;

import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import java.io.RandomAccessFile;
import java.util.List;

public class OptimizerMiniHealthProbes {

    public static class Result {

        public boolean cpuSpike = false;
        public boolean thermalHigh = false;
        public boolean crashSignal = false;
        public boolean cacheHigh = false;

        public double temperature = 0;
        public boolean charging = false;

        public boolean critical = false;
    }

    public static Result run(Context ctx, boolean cacheHigh) {

        Result r = new Result();

        double temp = getBatteryTemperature(ctx);
        boolean charging = isCharging(ctx);

        r.temperature = temp;
        r.charging = charging;

        if (isCpuSpike()) {
            r.cpuSpike = true;
        }

        if (temp >= 43.0) {
            r.thermalHigh = true;
        }

        if (hasRecentSystemCrash(ctx)) {
            r.crashSignal = true;
        }

        if (cacheHigh) {
            r.cacheHigh = true;
        }

        // =========================
        // CRITICAL LOGIC
        // =========================

        // Crash always critical
        if (r.crashSignal) {
            r.critical = true;
        }

        // Thermal critical (only if not charging)
        if (!charging && temp >= 45.0) {
            r.critical = true;
        }

        // CPU + Thermal critical
        if (!charging && temp >= 45.0 && r.cpuSpike) {
            r.critical = true;
        }

        return r;
    }

    // ======================================================
    // CPU SPIKE CHECK
    // ======================================================

    private static boolean isCpuSpike() {
        try {
            long[] a = readCpu();
            Thread.sleep(300);
            long[] b = readCpu();

            long idle = b[3] - a[3];
            long total = 0;

            for (int i = 0; i < b.length; i++) {
                total += (b[i] - a[i]);
            }

            if (total <= 0) return false;

            double usage = (total - idle) * 100.0 / total;

            return usage >= 70.0;

        } catch (Throwable ignore) {
            return false;
        }
    }

    private static long[] readCpu() throws Exception {

        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
        String[] toks = reader.readLine().split("\\s+");
        reader.close();

        long[] vals = new long[toks.length - 1];

        for (int i = 1; i < toks.length; i++) {
            vals[i - 1] = Long.parseLong(toks[i]);
        }

        return vals;
    }

    // ======================================================
    // TEMPERATURE
    // ======================================================

    private static double getBatteryTemperature(Context ctx) {
        try {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = ctx.registerReceiver(null, iFilter);

            if (batteryStatus == null) return 0;

            int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp <= 0) return 0;

            return temp / 10.0;

        } catch (Throwable ignore) {
            return 0;
        }
    }

    private static boolean isCharging(Context ctx) {
        try {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = ctx.registerReceiver(null, iFilter);

            if (batteryStatus == null) return false;

            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            return status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;

        } catch (Throwable ignore) {
            return false;
        }
    }

    // ======================================================
    // CRASH DETECTION
    // ======================================================

    private static boolean hasRecentSystemCrash(Context ctx) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            return false;

        try {

            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

            if (am == null)
                return false;

            List<ApplicationExitInfo> exits =
                    am.getHistoricalProcessExitReasons(null, 0, 20);

            long now = System.currentTimeMillis();

            for (ApplicationExitInfo info : exits) {

                long timestamp = info.getTimestamp();
                long diff = now - timestamp;

                if (diff > 24L * 60L * 60L * 1000L)
                    continue;

                int reason = info.getReason();

                if (reason == ApplicationExitInfo.REASON_CRASH
                        || reason == ApplicationExitInfo.REASON_ANR
                        || reason == ApplicationExitInfo.REASON_LOW_MEMORY) {

                    return true;
                }
            }

        } catch (Throwable ignore) {}

        return false;
    }
}
