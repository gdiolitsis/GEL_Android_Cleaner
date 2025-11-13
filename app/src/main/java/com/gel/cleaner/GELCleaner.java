package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * GELCleaner — FINAL v4.0
 * Compatible with SAFCleaner v3.2
 * GDiolitsis Engine Lab (GEL)
 *
 * GEL Deep Clean Pro Engine:
 *  - RAM cleanup + process kill
 *  - Internal cache + temp
 *  - Browser / WebView cache
 *  - SAF junk (WhatsApp / Telegram / Browsers / Streaming)
 *  - File-engine on external storage (big junk + thumbnails)
 *
 * ΠΑΝΤΑ στέλνουμε ολόκληρο το αρχείο, έτοιμο για copy-paste.
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
     * SAFE CLEAN (internal cache dirs)
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
                // SAF γνωστές διαδρομές (Android/data, Android/media, WhatsApp, Telegram κ.λπ.)
                SAFCleaner.cleanKnownJunk(ctx, cb);
            } else {
                warn(cb, "Grant SAF first for full deep clean.");
            }

            // Internal cache
            safeClean(ctx, cb);

            long after = getTotalCacheSize(ctx);
            long freed = Math.max(0, before - after);

            ok(cb,
                    "GEL Deep Clean\n" +
                    " • Before: " + Formatter.formatFileSize(ctx, before) + "\n" +
                    " • After:  " + Formatter.formatFileSize(ctx, after) + "\n" +
                    " • Freed:  " + Formatter.formatFileSize(ctx, freed)
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
                SAFCleaner.browserCache(ctx, cb);   // SAF browser dirs
            } else {
                warn(cb, "Grant SAF for browser dirs.");
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

            int files = wipeDir(ctx.getCacheDir());

            File ext = ctx.getExternalCacheDir();
            if (ext != null) files += wipeDir(ext);

            if (SAFCleaner.hasTree(ctx)) {
                SAFCleaner.tempClean(ctx, cb); // SAF temp/logs
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

        // 1) RAM
        cleanRAM(ctx, cb);

        // 2) Internal cache
        safeClean(ctx, cb);

        // 3) Temp (internal + SAF temp)
        tempClean(ctx, cb);

        // 4) Browser / WebView + SAF browser junk
        browserCache(ctx, cb);

        // 5) Media junk μέσω SAF (WhatsApp, Telegram, κ.λπ.)
        if (SAFCleaner.hasTree(ctx)) {
            SAFCleaner.mediaJunk(ctx, cb);
        } else {
            warn(cb, "Grant SAF for media junk.");
        }

        // 6) Deep Clean (SAF known dirs + internal recap)
        deepClean(ctx, cb);

        // 7) File-Engine πάνω στο πραγματικό filesystem (MANAGE_EXTERNAL_STORAGE)
        runFileEngine(ctx, cb);

        ok(cb, "🔥 GEL Deep Clean Pro finished.");
    }


    /* =========================================================
     * FILE ENGINE (external storage)
     * ========================================================= */

    // Αν είναι πολύ συντηρητικό, μπορούμε να χαμηλώσουμε όριο ή να προσθέσουμε φακέλους.
    private static final long BIG_FILE_MIN_BYTES = 30L * 1024L * 1024L; // 30 MB
    private static final long BIG_FILE_MIN_AGE_MS = 14L * 24L * 60L * 60L * 1000L; // 14 μέρες

    private static class DeleteStats {
        long bytes = 0;
        int  files = 0;
    }

    private static void runFileEngine(Context ctx, LogCallback cb) {
        File root = Environment.getExternalStorageDirectory();
        if (root == null || !root.exists()) {
            warn(cb, "External storage not available for file-engine.");
            return;
        }

        long now = System.currentTimeMillis();
        long totalBytes = 0;
        int  totalFiles = 0;

        // 1) Thumbnails folders (DCIM / Pictures / WhatsApp / Movies)
        String[] thumbDirs = new String[]{
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "Movies/.thumbnails",
                "WhatsApp/.thumbnails",
                "Android/DCIM/.thumbnails"
        };

        for (String rel : thumbDirs) {
            File dir = new File(root, rel);
            DeleteStats s = deleteAllChildren(dir);
            if (s.files > 0) {
                info(cb, "🗑 Thumbs " + rel + " → " +
                        s.files + " files, " +
                        Formatter.formatFileSize(ctx, s.bytes));
            }
            totalFiles += s.files;
            totalBytes += s.bytes;
        }

        // 2) Big media in γνωστούς φακέλους εφαρμογών
        String[] bigMediaDirs = new String[]{
                // WhatsApp
                "WhatsApp/Media/WhatsApp Video",
                "WhatsApp/Media/WhatsApp Animated Gifs",
                "WhatsApp/Media/WhatsApp Documents",
                "Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video",
                "Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Animated Gifs",
                "Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Documents",

                // Telegram
                "Telegram/Telegram Video",
                "Telegram/Telegram Documents",
                "Android/media/org.telegram.messenger/Telegram/Telegram Video",
                "Android/media/org.telegram.messenger/Telegram/Telegram Documents",

                // Viber
                "Android/data/com.viber.voip/files",

                // TikTok / Insta / FB (cache-like / export)
                "Android/data/com.ss.android.ugc.trill/cache",
                "Android/data/com.instagram.android/cache",
                "Android/data/com.facebook.katana/cache",
                "Android/data/com.facebook.orca/cache"
        };

        for (String rel : bigMediaDirs) {
            File dir = new File(root, rel);
            DeleteStats s = deleteBigOldFiles(dir, BIG_FILE_MIN_BYTES, BIG_FILE_MIN_AGE_MS, now);
            if (s.files > 0) {
                info(cb, "🧹 Big media " + rel + " → " +
                        s.files + " files, " +
                        Formatter.formatFileSize(ctx, s.bytes));
            }
            totalFiles += s.files;
            totalBytes += s.bytes;
        }

        if (totalFiles == 0) {
            info(cb, "ℹ️ File-engine: no extra junk found.");
        } else {
            ok(cb,
                    "GEL File-Engine summary\n" +
                    " • Files deleted: " + totalFiles + "\n" +
                    " • Freed: " + Formatter.formatFileSize(ctx, totalBytes)
            );
        }
    }

    /**
     * Σβήνει ΟΛΑ τα παιδιά ενός φακέλου (όχι τον ίδιο τον φάκελο).
     */
    private static DeleteStats deleteAllChildren(File dir) {
        DeleteStats stats = new DeleteStats();
        if (dir == null || !dir.exists()) return stats;

        File[] list = dir.listFiles();
        if (list == null) return stats;

        for (File f : list) {
            stats.bytes += deleteRecursivelyBytes(f);
        }
        // files μετρήθηκαν μέσα στο deleteRecursivelyBytes
        // αλλά πρέπει να το επιστρέφει κι αυτό → άρα:
        // το deleteRecursivelyBytes θα μετράει μόνο bytes,
        // κι εδώ θα βάλουμε ξεχωριστό counter:

        // Για απλότητα: δεύτερο πέρασμα μόνο για count
        int count = 0;
        list = dir.listFiles();
        if (list != null) {
            for (File f : list) {
                if (!f.exists()) count++; // ήδη σβησμένο
            }
        }
        stats.files = count;
        return stats;
    }

    /**
     * Σβήνει μεγάλα & παλιά αρχεία σε έναν φάκελο (δεν σβήνει τον φάκελο).
     */
    private static DeleteStats deleteBigOldFiles(File dir,
                                                 long minBytes,
                                                 long minAgeMs,
                                                 long nowMs) {
        DeleteStats stats = new DeleteStats();
        if (dir == null || !dir.exists()) return stats;

        File[] list = dir.listFiles();
        if (list == null) return stats;

        for (File f : list) {
            if (f.isDirectory()) {
                DeleteStats child = deleteBigOldFiles(f, minBytes, minAgeMs, nowMs);
                stats.bytes += child.bytes;
                stats.files += child.files;
                continue;
            }

            long size = f.length();
            long age  = nowMs - f.lastModified();

            if (size >= minBytes && age >= minAgeMs) {
                if (f.delete()) {
                    stats.bytes += size;
                    stats.files += 1;
                }
            }
        }
        return stats;
    }

    /**
     * Διαγραφή με μέτρηση bytes (για file-engine).
     */
    private static long deleteRecursivelyBytes(File f) {
        long total = 0;
        if (f == null || !f.exists()) return 0;

        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) {
                for (File k : kids) {
                    total += deleteRecursivelyBytes(k);
                }
            }
        } else {
            long size = f.length();
            if (f.delete()) {
                total += size;
            }
            return total;
        }

        // προσπάθησε να σβήσει και τον ίδιο τον φάκελο
        try { f.delete(); } catch (Throwable ignore) {}
        return total;
    }


    /* =========================================================
     * INTERNAL FS HELPERS (παλιά κομμάτια)
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
}
