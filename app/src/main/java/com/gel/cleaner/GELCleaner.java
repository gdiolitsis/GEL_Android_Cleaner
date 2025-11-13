package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * GELCleaner — FINAL v3.7
 * Compatible with SAFCleaner v3.2
 * GDiolitsis Engine Lab (GEL)
 *
 * SAFE-ish, using all allowed tricks.
 * Always deliver the full file, ready for copy-paste.
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
    private static void warn(LogCallback cb, String m) { if (cb != null) cb.log("⚠️ " + m, false); }
    private static void err (LogCallback cb, String m) { if (cb != null) cb.log("❌ " + m, true ); }


    /* =========================================================
     * CPU + RAM INFO
     * ========================================================= */
    public static void cpuInfo(Context ctx, LogCallback cb) {
        try {
            int cores = Runtime.getRuntime().availableProcessors();

            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) am.getMemoryInfo(mi);

            String total = (am != null) ? Formatter.formatFileSize(ctx, mi.totalMem) : "-";
            String avail = (am != null) ? Formatter.formatFileSize(ctx, mi.availMem) : "-";
            long usedBytes = (am != null) ? (mi.totalMem - mi.availMem) : 0;
            String used = Formatter.formatFileSize(ctx, usedBytes);

            StringBuilder b = new StringBuilder()
                    .append("CPU cores: ").append(cores).append("\n")
                    .append("RAM total: ").append(total).append("\n")
                    .append("RAM used:  ").append(used).append("\n")
                    .append("RAM free:  ").append(avail).append("\n")
                    .append("Low memory: ").append(mi.lowMemory).append("\n")
                    .append("SDK: ").append(Build.VERSION.SDK_INT)
                    .append(" (").append(Build.VERSION.RELEASE).append(")\n")
                    .append("Device: ")
                    .append(Build.MANUFACTURER).append(" ").append(Build.MODEL);

            ok(cb, b.toString());

        } catch (Exception e) {
            err(cb, "cpuInfo failed: " + e.getMessage());
        }
    }


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


    /* =========================================================
     * CLEAN — RAM
     * ========================================================= */
    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

            long beforeFree = 0L;
            long afterFree  = 0L;

            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                beforeFree = mi.availMem;
            }

            trimAppMemory();

            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs =
                        am.getRunningAppProcesses();

                if (procs != null) {
                    for (ActivityManager.RunningAppProcessInfo p : procs) {
                        if (p.pkgList == null) continue;
                        for (String pkg : p.pkgList) {
                            if (!pkg.equals(ctx.getPackageName())) {
                                try { am.killBackgroundProcesses(pkg); } catch (Exception ignore) {}
                            }
                        }
                    }
                }

                ActivityManager.MemoryInfo miAfter = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(miAfter);
                afterFree = miAfter.availMem;
            }

            long freed = Math.max(0L, afterFree - beforeFree);

            ok(cb,
                    "RAM cleanup\n" +
                    " • Before free: " + Formatter.formatFileSize(ctx, beforeFree) + "\n" +
                    " • After free:  " + Formatter.formatFileSize(ctx, afterFree) + "\n" +
                    " • Freed:       " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * SAFE CLEAN (internal cache + WebView)
     * ========================================================= */
    public static void safeClean(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            int files = 0;
            files += wipeDir(ctx.getCacheDir());

            File code = ctx.getCodeCacheDir();
            if (code != null) files += wipeDir(code);

            File ext = ctx.getExternalCacheDir();
            if (ext != null) files += wipeDir(ext);

            try {
                WebView w = new WebView(ctx);
                w.clearCache(true);
                w.clearFormData();
            } catch (Throwable ignore) {}

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "Safe Clean\n" +
                    " • Files removed: " + files + "\n" +
                    " • Before: " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:  " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed:  " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "safeClean failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * DEEP CLEAN (Internal + SAF junk)
     * ========================================================= */
    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            if (SAFCleaner.hasTree(ctx)) {
                SAFCleaner.cleanKnownJunk(ctx, cb);   // SAF γνωστές διαδρομές
            } else {
                warn(cb, "Grant SAF first.");
            }

            safeClean(ctx, cb);                       // εσωτερικά cache

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "GEL Deep Clean\n" +
                    " • Before (internal caches): " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:                    " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed (internal view):    " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * BROWSER CACHE
     * ========================================================= */
    public static void browserCache(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            try {
                WebView w = new WebView(ctx);
                w.clearCache(true);
                w.clearFormData();
                ok(cb, "WebView cache cleared.");
            } catch (Throwable t) {
                warn(cb, "WebView not available.");
            }

            if (SAFCleaner.hasTree(ctx)) {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            } else {
                warn(cb, "Grant SAF for browser dirs.");
            }

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "Browser Cache (internal view)\n" +
                    " • Before: " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:  " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed:  " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * TEMP CLEAN
     * ========================================================= */
    public static void tempClean(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            int files = wipeDir(ctx.getCacheDir());

            File ext = ctx.getExternalCacheDir();
            if (ext != null) files += wipeDir(ext);

            if (SAFCleaner.hasTree(ctx)) {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            } else {
                warn(cb, "Grant SAF for external temp dirs.");
            }

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "Temp Clean (internal view)\n" +
                    " • Files removed: " + files + "\n" +
                    " • Before: " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:  " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed:  " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "tempClean failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * BATTERY BOOST
     * ========================================================= */
    public static void boostBattery(Context ctx, LogCallback cb) {
        try {
            cleanRAM(ctx, cb);
            info(cb, "Tip: Enable Battery Saver for stronger effect.");
            ok(cb, "Battery boost done.");
        } catch (Exception e) {
            err(cb, "boostBattery failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * KILL APPS
     * ========================================================= */
    public static void killApps(Context ctx, LogCallback cb) {
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                err(cb, "ActivityManager is null.");
                return;
            }

            List<String> killed = new ArrayList<>();
            List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();

            if (procs != null) {
                for (ActivityManager.RunningAppProcessInfo p : procs) {
                    if (p.pkgList == null) continue;
                    for (String pkg : p.pkgList) {
                        if (!pkg.equals(ctx.getPackageName())) {
                            try { am.killBackgroundProcesses(pkg); killed.add(pkg); }
                            catch (Throwable ignore) {}
                        }
                    }
                }
            }

            ok(cb, "Killed " + killed.size() + " packages.");

        } catch (Exception e) {
            err(cb, "killApps failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * GEL DEEP CLEAN PRO ENGINE (Clean All button)
     * ========================================================= */
    public static void cleanAll(Context ctx, LogCallback cb) {
        info(cb, "🔥 GEL Deep Clean Pro started…");

        // ΣΥΝΟΛΙΚΗ ΧΡΗΣΗ ΣΥΣΚΕΥΗΣ ΠΡΙΝ
        StorageStats before = getStorageStats();

        // 1) RAM
        cleanRAM(ctx, cb);

        // 2) Internal cache
        safeClean(ctx, cb);

        // 3) Temp (internal + SAF temp)
        tempClean(ctx, cb);

        // 4) Browser / WebView
        browserCache(ctx, cb);

        // 5) Media junk μέσω SAF (WhatsApp, Telegram, κ.λπ.)
        if (SAFCleaner.hasTree(ctx)) {
            SAFCleaner.mediaJunk(ctx, cb);
        } else {
            warn(cb, "Grant SAF for media junk.");
        }

        // 6) Deep Clean (SAF known dirs + internal recap)
        deepClean(ctx, cb);

        // ΣΥΝΟΛΙΚΗ ΧΡΗΣΗ ΣΥΣΚΕΥΗΣ ΜΕΤΑ
        StorageStats after = getStorageStats();

        if (before != null && after != null) {
            long usedBefore = before.total - before.free;
            long usedAfter  = after.total - after.free;
            long freed      = Math.max(0L, usedBefore - usedAfter);

            ok(cb,
                    "🔥 GEL Deep Clean Pro — Device summary\n" +
                    " • Used before: " + Formatter.formatFileSize(ctx, usedBefore) + "\n" +
                    " • Used after:  " + Formatter.formatFileSize(ctx, usedAfter) + "\n" +
                    " • Freed total: " + Formatter.formatFileSize(ctx, freed)
            );
        } else {
            warn(cb, "Storage stats not available for global summary.");
        }

        ok(cb, "🔥 GEL Deep Clean Pro finished.");
    }


    /* =========================================================
     * INTERNAL FS HELPERS
     * ========================================================= */
    private static void trimAppMemory() {
        try { System.gc(); } catch (Throwable ignore) {}
    }


    private static long getTotalCacheSize(Context ctx) {
        long sum = 0;

        File c1 = ctx.getCacheDir();
        if (c1 != null) sum += folderSize(c1);

        File c2 = ctx.getCodeCacheDir();
        if (c2 != null) sum += folderSize(c2);

        File c3 = ctx.getExternalCacheDir();
        if (c3 != null) sum += folderSize(c3);

        return sum;
    }


    private static long folderSize(File f) {
        if (f == null || !f.exists()) return 0;
        long total = 0;

        File[] kids = f.listFiles();
        if (kids == null) return 0;

        for (File k : kids) {
            if (k.isDirectory()) total += folderSize(k);
            else total += k.length();
        }
        return total;
    }


    private static int wipeDir(File dir) {
        if (dir == null || !dir.exists()) return 0;
        int count = 0;

        File[] list = dir.listFiles();
        if (list == null) return 0;

        for (File f : list) count += deleteRecursively(f);
        return count;
    }


    private static int deleteRecursively(File f) {
        int c = 0;
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) {
                for (File k : kids) c += deleteRecursively(k);
            }
        }
        if (f.delete()) c++;
        return c;
    }


    /* =========================================================
     * GLOBAL STORAGE STATS (Internal storage root)
     * ========================================================= */
    private static class StorageStats {
        long total;
        long free;
    }

    private static StorageStats getStorageStats() {
        try {
            File path = Environment.getExternalStorageDirectory();
            if (path == null) return null;

            StatFs stat = new StatFs(path.getAbsolutePath());
            StorageStats s = new StorageStats();
            s.total = stat.getBlockCountLong() * stat.getBlockSizeLong();
            s.free  = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            return s;
        } catch (Throwable t) {
            return null;
        }
    }
}
