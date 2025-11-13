package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.WindowManager;

import java.io.RandomAccessFile;
import java.util.Locale;

/**
 * GELCleaner v4.0
 * GDiolitsis Engine Lab (GEL)
 *
 * FULL INTENT CLEANER
 * No file cleaning – only system navigation & system info.
 */
public class GELCleaner {

    /* =========================================================
     * LOG CALLBACK
     * ========================================================= */
    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void info(LogCallback cb, String m) { if (cb != null) cb.log("ℹ️ " + m, false); }
    private static void ok  (LogCallback cb, String m) { if (cb != null) cb.log("✅ " + m, false); }
    private static void err (LogCallback cb, String m) { if (cb != null) cb.log("❌ " + m, true ); }


    /* =========================================================
     * PHONE INFO (Super Report)
     * ========================================================= */
    public static void phoneInfo(Context ctx, LogCallback cb) {
        try {

            // RAM
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            String ramTotal = Formatter.formatFileSize(ctx, mi.totalMem);
            String ramAvail = Formatter.formatFileSize(ctx, mi.availMem);

            // Storage
            long total = new android.os.StatFs("/").getTotalBytes();
            long free  = new android.os.StatFs("/").getAvailableBytes();

            // CPU Model (≈)
            String cpuModel = readCpuModel();

            // GPU (best-effort)
            String gpu = android.os.Build.HARDWARE;

            // Kernel
            String kernel = System.getProperty("os.version");

            String info = ""
                    + "📱 DEVICE INFO\n"
                    + "• Manufacturer: " + Build.MANUFACTURER + "\n"
                    + "• Model: " + Build.MODEL + "\n"
                    + "• Board: " + Build.BOARD + "\n"
                    + "• Hardware: " + Build.HARDWARE + "\n\n"

                    + "⚙️ SYSTEM\n"
                    + "• Android: " + Build.VERSION.RELEASE + "\n"
                    + "• SDK: " + Build.VERSION.SDK_INT + "\n"
                    + "• Kernel: " + kernel + "\n"
                    + "• Security patch: " + Build.VERSION.SECURITY_PATCH + "\n\n"

                    + "🧠 CPU\n"
                    + "• Model: " + cpuModel + "\n"
                    + "• Cores: " + Runtime.getRuntime().availableProcessors() + "\n\n"

                    + "🎮 GPU (approx)\n"
                    + "• GPU: " + gpu + "\n\n"

                    + "🔥 RAM\n"
                    + "• Total: " + ramTotal + "\n"
                    + "• Free:  " + ramAvail + "\n\n"

                    + "💾 STORAGE\n"
                    + "• Total: " + Formatter.formatFileSize(ctx, total) + "\n"
                    + "• Free:  " + Formatter.formatFileSize(ctx, free) + "\n\n"

                    + "🌐 NETWORK\n"
                    + "• Type: auto-detect\n\n"

                    + "⏱ Uptime\n"
                    + "• " + formatUptime() + "\n";

            ok(cb, info);

        } catch (Exception e) {
            err(cb, "phoneInfo failed: " + e.getMessage());
        }
    }

    private static String readCpuModel() {
        try {
            RandomAccessFile f = new RandomAccessFile("/proc/cpuinfo", "r");
            String line;
            while ((line = f.readLine()) != null) {
                if (line.startsWith("Hardware") || line.startsWith("model name"))
                    return line.split(":")[1].trim();
            }
        } catch (Exception ignored) {}
        return "Unknown CPU";
    }

    private static String formatUptime() {
        long up = android.os.SystemClock.elapsedRealtime() / 1000;
        long h = up / 3600;
        long m = (up % 3600) / 60;
        return h + "h " + m + "m";
    }


    /* =========================================================
     * CPU + RAM LIVE
     * ========================================================= */
    public static void cpuLive(Context ctx, LogCallback cb) {
        new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    long free = Runtime.getRuntime().freeMemory();
                    long total = Runtime.getRuntime().totalMemory();
                    long used = total - free;

                    info(cb, String.format(Locale.US,
                            "Live %02d/10 → RAM: %s / %s",
                            i,
                            Formatter.formatShortFileSize(ctx, used),
                            Formatter.formatShortFileSize(ctx, total)));

                    Thread.sleep(1000);
                }
                ok(cb, "CPU+RAM Live finished.");
            } catch (Exception e) {
                err(cb, "cpuLive failed: " + e.getMessage());
            }
        }).start();
    }


    /* =========================================================
     * CLEAN RAM → Opens RAM management
     * ========================================================= */
    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            ok(cb, "Opening system RAM screen…");

            Intent i = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);

        } catch (Exception e) {
            err(cb, "RAM screen failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * DEEP CLEAN → Opens device default cleaner
     * ========================================================= */
    public static void deepClean(Context ctx, LogCallback cb) {
        ok(cb, "Opening system cleaner…");

        Intent[] intents = new Intent[]{

                // Xiaomi / Poco / Redmi
                new Intent("miui.intent.action.GARBAGE_CLEANUP"),

                // Samsung
                new Intent("com.samsung.android.sm.ACTION_CLEANUP"),

                // Huawei
                new Intent("com.huawei.systemmanager.optimize.START"),

                // Oppo / Realme
                new Intent("oppo.intent.action.OPPO_CLEANER"),

                // OnePlus
                new Intent("oneplus.intent.action.ONEPLUS_CLEANER"),

                // Vivo / iQOO
                new Intent("com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity"),

                // Motorola
                new Intent("com.motorola.ccc.OPTIMIZE"),
        };

        launchFirstWorking(ctx, intents, cb);
    }


    /* =========================================================
     * Browser cache settings
     * ========================================================= */
    public static void browserCache(Context ctx, LogCallback cb) {
        ok(cb, "Opening browser cache…");

        Intent[] intents = new Intent[]{
                browserSettings("com.android.chrome"),
                browserSettings("org.mozilla.firefox"),
                browserSettings("com.opera.browser"),
                browserSettings("com.microsoft.emmx"),
                browserSettings("com.brave.browser"),
                browserSettings("com.duckduckgo.mobile.android")
        };

        launchFirstWorking(ctx, intents, cb);
    }

    private static Intent browserSettings(String pkg) {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:" + pkg));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }


    /* =========================================================
     * TEMP FILES → Cached data
     * ========================================================= */
    public static void tempFiles(Context ctx, LogCallback cb) {
        ok(cb, "Opening temp/cache page…");

        Intent i = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }


    /* =========================================================
     * Battery Boost / Kill → Running apps
     * ========================================================= */
    public static void openRunningApps(Context ctx, LogCallback cb) {
        ok(cb, "Opening running apps…");

        Intent i = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    }


    /* =========================================================
     * HELPER for multi-intent clean
     * ========================================================= */
    private static void launchFirstWorking(Context ctx, Intent[] list, LogCallback cb) {
        for (Intent i : list) {
            try {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                return;
            } catch (Exception ignored) {}
        }
        err(cb, "No supported cleaner found for this device.");
    }
}
