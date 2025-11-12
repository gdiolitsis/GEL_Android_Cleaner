package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * GELCleaner — Utility class (NOT Activity)
 * SAFE + Play-Store acceptable
 * FULL real logs: before → after → freed
 * (Rule: πάντα στέλνουμε ολόκληρο το τελικό αρχείο έτοιμο για copy-paste)
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

            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) am.getMemoryInfo(mi);

            String total = (am != null)
                    ? Formatter.formatFileSize(ctx, mi.totalMem)
                    : "-";
            String avail = (am != null)
                    ? Formatter.formatFileSize(ctx, mi.availMem)
                    : "-";
            long usedBytes = (am != null) ? (mi.totalMem - mi.availMem) : 0;
            String used = (am != null)
                    ? Formatter.formatFileSize(ctx, usedBytes)
                    : "-";

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
            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
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
                                try {
                                    am.killBackgroundProcesses(pkg);
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }

                ActivityManager.MemoryInfo miAfter = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(miAfter);
                afterFree = miAfter.availMem;
            }

            long freed = Math.max(0L, afterFree - beforeFree);

            if (beforeFree > 0 && afterFree > 0) {
                ok(cb,
                        "RAM cleanup done.\n" +
                        " • Before free: " + Formatter.formatFileSize(ctx, beforeFree) + "\n" +
                        " • After free:  " + Formatter.formatFileSize(ctx, afterFree) + "\n" +
                        " • Freed:       " + Formatter.formatFileSize(ctx, freed)
                );
            } else {
                ok(cb, "RAM cleanup done.");
            }
        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * SAFE CLEAN
     * ========================================================= */
    public static void safeClean(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            int files = 0;
            files += wipeDir(ctx.getCacheDir());

            File codeCache = ctx.getCodeCacheDir();
            if (codeCache != null) files += wipeDir(codeCache);

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
     * DEEP CLEAN
     * ========================================================= */
    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            if (!SAFCleaner.hasTree(ctx)) {
                warn(cb, "Grant SAF first.");
            } else {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            }

            safeClean(ctx, cb);

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "Deep Clean\n" +
                    " • Before: " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:  " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed:  " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * MEDIA JUNK
     * ========================================================= */
    public static void mediaJunk(Context ctx, LogCallback cb) {
        try {
            long before = getTotalCacheSize(ctx);

            if (SAFCleaner.hasTree(ctx)) {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            } else {
                warn(cb, "Grant SAF first.");
            }

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "Media Junk\n" +
                    " • Before: " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:  " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed:  " + Formatter.formatFileSize(ctx, freed)
            );

        } catch (Exception e) {
            err(cb, "mediaJunk failed: " + e.getMessage());
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
                warn(cb, "Grant SAF to clear Chrome/Firefox cache.");
            }

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "Browser Cache\n" +
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

            int files = 0;
            files += wipeDir(ctx.getCacheDir());

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
                    "Temp Clean\n" +
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
     * BATTERY
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
            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                err(cb, "ActivityManager is null.");
                return;
            }

            List<String> killed = new ArrayList<>();
            List<ActivityManager.RunningAppProcessInfo> procs =
                    am.getRunningAppProcesses();

            if (procs != null) {
                for (ActivityManager.RunningAppProcessInfo p : procs) {
                    if (p.pkgList == null) continue;
                    for (String pkg : p.pkgList) {
                        if (!pkg.equals(ctx.getPackageName())) {
                            try {
                                am.killBackgroundProcesses(pkg);
                                killed.add(pkg);
                            } catch (Throwable ignore) {}
                        }
                    }
                }
            }

            ok(cb, "Killed: " + killed.size() + " packages.");
        } catch (Exception e) {
            err(cb, "killApps failed: " + e.getMessage());
        }
    }


    /* =========================================================
     * CLEAN ALL
     * ========================================================= */
    public static void cleanAll(Context ctx, LogCallback cb) {
        info(cb, "Clean All started…");

        cleanRAM(ctx, cb);
        safeClean(ctx, cb);
        tempClean(ctx, cb);
        browserCache(ctx, cb);
        mediaJunk(ctx, cb);
        deepClean(ctx, cb);

        ok(cb, "Clean All finished.");
    }


    /* =========================================================
     * INTERNAL FS
     * ========================================================= */
    private static void trimAppMemory() {
        try { System.gc(); } catch (Throwable ignore) {}
    }


    /* ------- size / wipe ------- */

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
}
