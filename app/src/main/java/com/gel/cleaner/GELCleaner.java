package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.Locale;

public class GELCleaner {

    /* ===========================================================
     * LOG CALLBACK
     * =========================================================== */
    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void info(LogCallback cb, String m) { if (cb != null) cb.log("ℹ️ " + m, false); }
    private static void ok  (LogCallback cb, String m) { if (cb != null) cb.log("✅ " + m, false); }
    private static void err (LogCallback cb, String m) { if (cb != null) cb.log("❌ " + m, true ); }


    /* ===========================================================
     * CPU/RAM LIVE — REAL DEVICE CPU
     * =========================================================== */
    public static void cpuLive(Context ctx, LogCallback cb) {

        new Thread(() -> {
            try {

                for (int i = 1; i <= 10; i++) {

                    double cpu = readCpuUsage();
                    String cpuTxt = cpu < 0 ? "N/A" : String.format(Locale.US, "%.1f%%", cpu);

                    // RAM
                    ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    long totalMb = mi.totalMem / (1024 * 1024);
                    long availMb = mi.availMem / (1024 * 1024);
                    long usedMb  = totalMb - availMb;

                    String msg = String.format(Locale.US,
                            "Live %02d | CPU: %s | RAM: %d MB / %d MB",
                            i, cpuTxt, usedMb, totalMb);

                    info(cb, msg);
                    Thread.sleep(1000);
                }

                ok(cb, "CPU+RAM live finished.");

            } catch (Exception e) {
                err(cb, "cpuLive failed: " + e.getMessage());
            }
        }).start();
    }


    // =====================================================================
    // REAL CPU USAGE FOR ANDROID (/proc/stat method)
    // =====================================================================
    private static long[] readCpuStat() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = br.readLine();
            if (line == null || !line.startsWith("cpu ")) return null;

            String[] parts = line.split("\\s+");

            long user = Long.parseLong(parts[1]);
            long nice = Long.parseLong(parts[2]);
            long system = Long.parseLong(parts[3]);
            long idle = Long.parseLong(parts[4]);
            long iowait = Long.parseLong(parts[5]);
            long irq = Long.parseLong(parts[6]);
            long softirq = Long.parseLong(parts[7]);

            long idleAll = idle + iowait;
            long cpuAll = user + nice + system + idle + iowait + irq + softirq;

            return new long[]{ idleAll, cpuAll };

        } catch (Exception e) {
            return null;
        }
    }

    private static double readCpuUsage() {
        try {
            long[] t1 = readCpuStat();
            if (t1 == null) return -1;

            Thread.sleep(360);

            long[] t2 = readCpuStat();
            if (t2 == null) return -1;

            long idle = t2[0] - t1[0];
            long cpu  = t2[1] - t1[1];

            if (cpu == 0) return -1;

            return ((cpu - idle) * 100.0) / cpu;

        } catch (Exception e) {
            return -1;
        }
    }



    /* ===========================================================
     * PHONE INFO (δεν αλλάχτηκε)
     * =========================================================== */
    public static void phoneInfo(Context ctx, LogCallback cb) {
        try {
            StringBuilder b = new StringBuilder();

            b.append("📱 DEVICE INFO\n\n")
             .append("Brand: ").append(Build.BRAND).append("\n")
             .append("Model: ").append(Build.MODEL).append("\n")
             .append("Android: ").append(Build.VERSION.RELEASE).append("\n\n");

            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            String total = Formatter.formatFileSize(ctx, mi.totalMem);
            String free  = Formatter.formatFileSize(ctx, mi.availMem);

            b.append("RAM Total: ").append(total).append("\n")
             .append("RAM Free:  ").append(free).append("\n");

            ok(cb, b.toString());

        } catch (Exception e) {
            err(cb, "phoneInfo failed: " + e.getMessage());
        }
    }


    /* ===========================================================
     * CLEAN RAM / DEEP CLEAN / ETC
     * (όλα τα υπόλοιπα μένουν ίδια)
     * =========================================================== */

    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening system RAM menu…");
        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }

    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening system cleaner…");
        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }

    public static void browserCache(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Choose your browser → Storage → Clear Cache.");
        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }

    public static void tempFiles(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening Temporary Files section…");
        } catch (Exception e) {
            err(cb, "tempFiles failed: " + e.getMessage());
        }
    }

    public static void openRunningApps(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening Running Apps…");
        } catch (Exception e) {
            err(cb, "openRunningApps failed: " + e.getMessage());
        }
    }
}
