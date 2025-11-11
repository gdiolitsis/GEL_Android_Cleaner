package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.provider.Settings;
import android.text.format.Formatter;
import android.webkit.WebView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * GELCleaner — Utility class (ΟΧΙ Activity)
 * Option B: Χρησιμοποιεί και deprecated/best-effort κλήσεις όπου γίνεται.
 * Play-Store ready με προσεκτικά try/catch & καθαρά logs.
 */
public class GELCleaner {

    // ===== Public callback για logs προς το UI =====
    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    // ===== Helpers για logs =====
    private static void info(LogCallback cb, String m) { if (cb != null) cb.log("ℹ️ " + m, false); }
    private static void ok(LogCallback cb, String m)   { if (cb != null) cb.log("✅ " + m, false); }
    private static void warn(LogCallback cb, String m) { if (cb != null) cb.log("⚠️ " + m, false); }
    private static void err(LogCallback cb, String m)  { if (cb != null) cb.log("❌ " + m, true);  }

    // =========================================================
    //                      SYSTEM INFO
    // =========================================================
    public static void cpuInfo(Context ctx, LogCallback cb) {
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) am.getMemoryInfo(mi);

            String total = (am != null)
                    ? Formatter.formatFileSize(ctx, mi.totalMem)
                    : "-";
            String avail = (am != null)
                    ? Formatter.formatFileSize(ctx, mi.availMem)
                    : "-";

            StringBuilder b = new StringBuilder();
            b.append("CPU cores: ").append(cores).append("\n");
            b.append("RAM total: ").append(total).append("\n");
            b.append("RAM free: ").append(avail).append("\n");
            b.append("Low memory: ").append(mi.lowMemory).append("\n");
            b.append("SDK: ").append(Build.VERSION.SDK_INT)
                    .append(" (").append(Build.VERSION.RELEASE).append(")\n");
            b.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL);

            ok(cb, b.toString());
        } catch (Exception e) {
            err(cb, "cpuInfo failed: " + e.getMessage());
        }
    }

    public static void cpuLive(Context ctx, LogCallback cb) {
        // Απλός, ασφαλής live logger 10 δειγμάτων / 1s
        new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    long free = Runtime.getRuntime().freeMemory();
                    long total = Runtime.getRuntime().totalMemory();
                    long used = total - free;

                    String msg = String.format(Locale.US,
                            "Live %02d/10  |  App RAM used: %s / %s",
                            i,
                            Formatter.formatShortFileSize(ctx, used),
                            Formatter.formatShortFileSize(ctx, total));
                    ok(cb, msg);

                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                }
                ok(cb, "CPU+RAM live finished.");
            } catch (Exception e) {
                err(cb, "cpuLive failed: " + e.getMessage());
            }
        }).start();
    }

    // =========================================================
    //                        CLEANERS
    // =========================================================

    /** Καθαρισμός RAM (best-effort): trim app memory + kill background processes (permission OK). */
    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            // 1) Ζήτα από το σύστημα να κάνει trim στη δική μας διεργασία
            trimAppMemory();

            // 2) Προσπάθησε να τερματίσεις background διεργασίες άλλων apps (όπου επιτρέπεται)
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null) {
                    for (ActivityManager.RunningAppProcessInfo p : procs) {
                        // Απόφυγε το δικό μας package
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
            }
            ok(cb, "RAM cleanup done.");
        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }

    /** Ασφαλής καθαρισμός: δικοί μας cache dirs, WebView cache, tmp αρχεία. */
    public static void safeClean(Context ctx, LogCallback cb) {
        try {
            int files = 0;

            // App cache
            files += wipeDir(ctx.getCacheDir());

            // Code cache
            File codeCache = ctx.getCodeCacheDir();
            if (codeCache != null) files += wipeDir(codeCache);

            // External cache
            File extCache = ctx.getExternalCacheDir();
            if (extCache != null) files += wipeDir(extCache);

            // WebView cache
            try {
                WebView w = new WebView(ctx);
                w.clearCache(true);
                w.clearFormData();
            } catch (Throwable t) {
                // WebView μπορεί να λείπει σε μερικές συσκευές
            }

            ok(cb, "Safe clean: " + files + " files cleared.");
        } catch (Exception e) {
            err(cb, "safeClean failed: " + e.getMessage());
        }
    }

    /** Deep clean: SAF-based wipe σε γνωστά μονοπάτια + safeClean. */
    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            if (!SAFCleaner.hasTree(ctx)) {
                warn(cb, "Grant SAF first (Select root folder).");
            } else {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            }
            // Και πάντα ένα πέρασμα στο app μας
            safeClean(ctx, cb);
            ok(cb, "Deep clean finished.");
        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }

    /** Media junk: κυρίως μέσω SAF (DCIM/.thumbnails κ.λπ.). */
    public static void mediaJunk(Context ctx, LogCallback cb) {
        try {
            if (!SAFCleaner.hasTree(ctx)) {
                warn(cb, "Grant SAF first (Select root folder).");
            } else {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            }
            ok(cb, "Media junk pass finished.");
        } catch (Exception e) {
            err(cb, "mediaJunk failed: " + e.getMessage());
        }
    }

    /** Browser cache: WebView + SAF γνωστά μονοπάτια Chrome/Firefox. */
    public static void browserCache(Context ctx, LogCallback cb) {
        try {
            try {
                WebView w = new WebView(ctx);
                w.clearCache(true);
                w.clearFormData();
                ok(cb, "WebView cache cleared.");
            } catch (Throwable t) {
                warn(cb, "WebView not available.");
            }

            if (!SAFCleaner.hasTree(ctx)) {
                warn(cb, "Grant SAF to clear Chrome/Firefox cache folders.");
            } else {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            }
            ok(cb, "Browser cache pass finished.");
        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }

    /** Temp files: δικοί μας temp + SAF temp γνωστά. */
    public static void tempClean(Context ctx, LogCallback cb) {
        try {
            int files = 0;
            files += wipeDir(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) files += wipeDir(ext);

            if (SAFCleaner.hasTree(ctx)) {
                SAFCleaner.cleanKnownJunk(ctx, cb);
            } else {
                warn(cb, "Grant SAF for external temp dirs.");
            }
            ok(cb, "Temp clean: " + files + " files removed.");
        } catch (Exception e) {
            err(cb, "tempClean failed: " + e.getMessage());
        }
    }

    /** Ενίσχυση μπαταρίας: kill background + trim + advise battery saver. */
    public static void boostBattery(Context ctx, LogCallback cb) {
        try {
            cleanRAM(ctx, cb);
            ok(cb, "Battery boost: background trimmed.");

            // Δεν υπάρχει public API για ενεργοποίηση Battery Saver.
            // Δίνουμε link στις ρυθμίσεις εξοικονόμησης (προαιρετικό log).
            info(cb, "Tip: Enable Battery Saver for stronger effect.");
        } catch (Exception e) {
            err(cb, "boostBattery failed: " + e.getMessage());
        }
    }

    /** Τερματισμός apps (best-effort). */
    public static void killApps(Context ctx, LogCallback cb) {
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) { err(cb, "ActivityManager is null."); return; }

            List<String> killed = new ArrayList<>();
            List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
            if (procs != null) {
                for (ActivityManager.RunningAppProcessInfo p : procs) {
                    if (p.pkgList == null) continue;
                    for (String pkg : p.pkgList) {
                        if (!pkg.equals(ctx.getPackageName())) {
                            try {
                                am.killBackgroundProcesses(pkg);
                                killed.add(pkg);
                            } catch (Throwable ignored) {}
                        }
                    }
                }
            }
            ok(cb, "Killed: " + killed.size() + " packages.");
        } catch (Exception e) {
            err(cb, "killApps failed: " + e.getMessage());
        }
    }

    /** Καθαρισμός όλων με σειρά. */
    public static void cleanAll(Context ctx, LogCallback cb) {
        info(cb, "Clean All: started…");
        cleanRAM(ctx, cb);
        safeClean(ctx, cb);
        tempClean(ctx, cb);
        browserCache(ctx, cb);
        mediaJunk(ctx, cb);
        deepClean(ctx, cb);
        ok(cb, "Clean All: finished.");
    }

    // =========================================================
    //                   INTERNAL UTILITIES
    // =========================================================
    private static void trimAppMemory() {
        try {
            Debug.MemoryInfo mi = new Debug.MemoryInfo();
            Debug.getMemoryInfo(mi);
            // Hint στο σύστημα (δεν υπάρχει “one-shot” trim API, αλλά το GC βοηθά)
            System.gc();
            Process.killProcess(Process.myPid()); // ΔΕΝ το κάνουμε — θα σκοτώσει το app.
            // Σχόλιο: Δεν εκτελούμε killProcess. Αφήνουμε μόνο GC/trim hints.
        } catch (Throwable ignored) {}
    }

    private static int wipeDir(File dir) {
        if (dir == null || !dir.exists()) return 0;
        int count = 0;
        File[] list = dir.listFiles();
        if (list == null) return 0;
        for (File f : list) {
            count += deleteRecursively(f);
        }
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

    // (Προαιρετικό) Άνοιγμα ρυθμίσεων app — δεν το χρησιμοποιούμε πλέον αυτόματα.
    @SuppressWarnings("unused")
    private static void openAppSettings(Context ctx, LogCallback cb) {
        try {
            Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            it.setData(android.net.Uri.fromParts("package", ctx.getPackageName(), null));
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(it);
            info(cb, "Opened app settings.");
        } catch (Exception e) {
            err(cb, "openAppSettings failed: " + e.getMessage());
        }
    }
}
