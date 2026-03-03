// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerMiniHealthProbes.java — FINAL (Mini = Temp≥45 / Cache≥85 / CPU+Thermal + Moderate Escalation helper)

package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.io.RandomAccessFile;

public class OptimizerMiniHealthProbes {

    public static class Result {

        public boolean cpuSpike = false;
        public boolean thermalHigh = false;
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

        // "thermalHigh" = early warning (moderate tracking)
        if (temp >= 43.0) {
            r.thermalHigh = true;
        }

        if (cacheHigh) {
            r.cacheHigh = true;
        }

        // =========================
        // MINI CRITICAL LOGIC (FINAL)
        // =========================
        boolean thermalCritical = (!charging && temp >= 45.0);
        boolean cpuThermalCritical = (r.cpuSpike && thermalCritical);
        boolean cacheCritical = r.cacheHigh; // Cache ≥85% triggers mini

        r.critical = thermalCritical || cpuThermalCritical || cacheCritical;

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
        String line = reader.readLine();
        reader.close();

        if (line == null) return new long[0];

        String[] toks = line.split("\\s+");
        long[] vals = new long[Math.max(0, toks.length - 1)];

        for (int i = 1; i < toks.length; i++) {
            try { vals[i - 1] = Long.parseLong(toks[i]); }
            catch (Throwable ignore) { vals[i - 1] = 0L; }
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
}
