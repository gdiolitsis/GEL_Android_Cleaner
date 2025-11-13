package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;

import java.io.File;
import java.util.Locale;

/**
 * GELCleaner — FINAL v4.0
 * GDiolitsis Engine Lab (GEL)
 *
 * Συμβατό με:
 * - MainActivity (νέα κουμπιά)
 * - SAFCleaner v3.3
 * - Νέο UI layout
 *
 * Δεν χρησιμοποιεί καθόλου παλιές Deep Clean/Safe Clean μηχανές.
 * Όλα τα "μενού καθαρισμού" ανοίγουν το default system cleaner.
 */

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
     * PHONE INFO (νέο κουμπί)
     * =========================================================== */
    public static void phoneInfo(Context ctx, LogCallback cb) {
        try {
            StringBuilder b = new StringBuilder();

            b.append("📱 DEVICE INFO\n\n")
             .append("Brand: ").append(Build.BRAND).append("\n")
             .append("Model: ").append(Build.MODEL).append("\n")
             .append("Manufacturer: ").append(Build.MANUFACTURER).append("\n")
             .append("Device: ").append(Build.DEVICE).append("\n")
             .append("Product: ").append(Build.PRODUCT).append("\n")
             .append("Board: ").append(Build.BOARD).append("\n")
             .append("Hardware: ").append(Build.HARDWARE).append("\n")
             .append("Bootloader: ").append(Build.BOOTLOADER).append("\n\n")

             .append("Android: ").append(Build.VERSION.RELEASE)
             .append("  (SDK ").append(Build.VERSION.SDK_INT).append(")\n")
             .append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n\n");

            // RAM
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            String total = Formatter.formatFileSize(ctx, mi.totalMem);
            String free  = Formatter.formatFileSize(ctx, mi.availMem);

            b.append("RAM Total: ").append(total).append("\n")
             .append("RAM Free:  ").append(free).append("\n\n");

            // Storage
            File data = ctx.getFilesDir();
            long freeBytes = data.getFreeSpace();
            long totalBytes = data.getTotalSpace();

            b.append("Internal Storage:\n")
             .append("Total: ").append(Formatter.formatFileSize(ctx, totalBytes)).append("\n")
             .append("Free:  ").append(Formatter.formatFileSize(ctx, freeBytes)).append("\n");

            ok(cb, b.toString());

        } catch (Exception e) {
            err(cb, "phoneInfo failed: " + e.getMessage());
        }
    }


    /* ===========================================================
     * CPU/RAM LIVE
     * =========================================================== */
    public static void cpuLive(Context ctx, LogCallback cb) {
        new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    long free = Runtime.getRuntime().freeMemory();
                    long total = Runtime.getRuntime().totalMemory();
                    long used = total - free;

                    String msg = String.format(Locale.US,
                            "Live %02d/10 | App RAM used: %s / %s",
                            i,
                            Formatter.formatShortFileSize(ctx, used),
                            Formatter.formatShortFileSize(ctx, total));

                    info(cb, msg);
                    Thread.sleep(1000);
                }
                ok(cb, "CPU+RAM live finished.");
            } catch (Exception e) {
                err(cb, "cpuLive failed: " + e.getMessage());
            }
        }).start();
    }


    /* ===========================================================
     * CLEAN RAM → system memory menu
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


    /* ===========================================================
     * DEEP CLEAN → default system cleaner
     * =========================================================== */
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


    /* ===========================================================
     * BROWSER CACHE → browser settings
     * =========================================================== */
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


    /* ===========================================================
     * TEMP FILES → system temporary cache
     * =========================================================== */
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


    /* ===========================================================
     * OPEN RUNNING APPS (Battery Boost / Kill Apps)
     * =========================================================== */
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
