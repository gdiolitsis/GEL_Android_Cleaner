package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.format.Formatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void info(LogCallback cb, String m) { if (cb != null) cb.log("ℹ️ " + m, false); }
    private static void ok  (LogCallback cb, String m) { if (cb != null) cb.log("✅ " + m, false); }
    private static void err (LogCallback cb, String m) { if (cb != null) cb.log("❌ " + m, true ); }

    // ====================================================================
    // CPU LIVE
    // ====================================================================
    public static void cpuLive(Context ctx, LogCallback cb) {

        new Thread(() -> {
            try {
                int i = 1;
                while (i <= 10) {

                    long free = Runtime.getRuntime().freeMemory();
                    long total = Runtime.getRuntime().totalMemory();
                    long used = total - free;

                    String msg = String.format(Locale.US,
                            "Live %02d | App RAM used: %s / %s",
                            i,
                            Formatter.formatShortFileSize(ctx, used),
                            Formatter.formatShortFileSize(ctx, total));

                    info(cb, msg);
                    Thread.sleep(1000);
                    i++;
                }

                ok(cb, "CPU+RAM live finished.");

            } catch (Exception e) {
                err(cb, "cpuLive failed: " + e.getMessage());
            }
        }).start();
    }

    // ====================================================================
    // CLEAN RAM → SmartClean
    // ====================================================================
    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            boolean launched = CleanLauncher.smartClean(ctx);

            if (launched) {
                ok(cb, "Smart RAM Cleaner ενεργοποιήθηκε.");
                return;
            }

            // fallback
            try {
                Intent i = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening device info…");
                return;
            } catch (Exception ignored) {}

            err(cb, "No compatible RAM/cleaner screen found on this device.");

        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // DEEP CLEAN → SmartClean ΠΡΩΤΑ, μετά OEM deep cleaner
    // ====================================================================
    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            // 1) Προσπάθεια με SmartClean (RAM / OEM optimized)
            boolean launched = CleanLauncher.smartClean(ctx);

            if (launched) {
                ok(cb, "Smart Cleaner ενεργοποιήθηκε (RAM / OEM optimization).");
                return;
            }

            // 2) Fallback → OEM Deep Cleaner
            launched = CleanLauncher.openDeepCleaner(ctx);

            if (launched) {
                ok(cb, "Opening device deep cleaner…");
                return;
            }

            // 3) Τελικό fallback → System Info
            try {
                Intent i = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening system cleaner…");
            } catch (Exception e2) {
                err(cb, "deepClean fallback failed: " + e2.getMessage());
            }

        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // CLEAN APP CACHE — με report τι καθαρίστηκε
    // ====================================================================
    public static void cleanAppCache(Context ctx, LogCallback cb) {
        try {
            long before = folderSize(ctx.getCacheDir());
            deleteFolder(ctx.getCacheDir());
            long after = 0;
            long diff = before - after;

            ok(cb, "Cache cleaned: " + readable(diff));

        } catch (Exception e) {
            err(cb, "cache clean failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // TEMP FILES WITH REPORT + ανοίγει Storage Settings
    // ====================================================================
    public static void cleanTempFiles(Context ctx, LogCallback cb) {
        try {
            File temp = new File(ctx.getFilesDir(), "temp");
            long before = folderSize(temp);

            deleteFolder(temp);

            ok(cb, "Temp files removed: " + readable(before));

            // Επιπλέον: άνοιγμα Storage Settings, ώστε "να σε πάει κάπου"
            try {
                Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            } catch (Exception ignored) {}

        } catch (Exception e) {
            err(cb, "tempFiles failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // BROWSER CACHE — universal + Mi Browser support
    // ====================================================================
    public static void browserCache(Context ctx, LogCallback cb) {
        try {
            PackageManager pm = ctx.getPackageManager();

            String[] browsers = {
                    "com.android.chrome",
                    "org.mozilla.firefox",
                    "com.opera.browser",
                    "com.microsoft.emmx",
                    "com.brave.browser",
                    "com.vivaldi.browser",
                    "com.duckduckgo.mobile.android",
                    "com.sec.android.app.sbrowser",
                    // MI Browser variations
                    "com.mi.globalbrowser",
                    "com.android.browser",
                    "com.miui.hybrid"
            };

            List<String> installed = new ArrayList<>();

            for (String pkg : browsers) {
                try { pm.getPackageInfo(pkg, 0); installed.add(pkg); }
                catch (PackageManager.NameNotFoundException ignored) {}
            }

            if (installed.isEmpty()) {
                err(cb, "No browsers found on your device.");
                return;
            }

            if (installed.size() == 1) {
                String pkg = installed.get(0);
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.setData(Uri.parse("package:" + pkg));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening browser storage → Clear Cache.");
                return;
            }

            Intent i = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Select a browser → Storage → Clear Cache.");

        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // RUNNING APPS → όσο επιτρέπει η Google
    // ====================================================================
    public static void openRunningApps(Context ctx, LogCallback cb) {
        try {
            // 1) Developer / Running Services (όπου υπάρχει)
            try {
                Intent dev = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                dev.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(dev);
                ok(cb, "Opening Developer / Running Services (where supported)...");
                return;
            } catch (Exception ignored) {}

            // 2) Fallback → Application Settings (λίστα εφαρμογών)
            Intent i = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening Applications list (running apps visibility is restricted by Android).");
        } catch (Exception e) {
            err(cb, "openRunningApps failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // HELPERS
    // ====================================================================
    private static long folderSize(File f) {
        if (f == null || !f.exists()) return 0;
        if (f.isFile()) return f.length();

        long size = 0;
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) size += folderSize(c);
        }
        return size;
    }

    private static void deleteFolder(File f) {
        if (f == null || !f.exists()) return;
        if (f.isFile()) { f.delete(); return; }

        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) deleteFolder(c);
        }
        f.delete();
    }

    private static String readable(long bytes) {
        if (bytes <= 0) return "0 KB";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.2f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.2f MB", mb);
        float gb = mb / 1024f;
        return String.format(Locale.US, "%.2f GB", gb);
    }
}
