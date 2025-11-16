package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
    // CLEAN RAM (Smart Clean)
    // ====================================================================
    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            boolean launched = CleanLauncher.smartClean(ctx);

            if (launched) {
                ok(cb, "Smart RAM Cleaner ενεργοποιήθηκε.");
                return;
            }

            err(cb, "Δεν βρέθηκε RAM cleaner στη συσκευή.");

        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // DEEP CLEAN (OEM Cleaner)
    // ====================================================================
    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            boolean launched = CleanLauncher.openDeepCleaner(ctx);

            if (launched) {
                ok(cb, "Device Deep Cleaner ενεργοποιήθηκε.");
                return;
            }

            err(cb, "Δεν βρέθηκε deep cleaner στη συσκευή.");

        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // CLEAN APP CACHE
    // ====================================================================
    public static void cleanAppCache(Context ctx, LogCallback cb) {
        try {
            long before = folderSize(ctx.getCacheDir());
            deleteFolder(ctx.getCacheDir());
            ok(cb, "App cache cleaned: " + readable(before));
        } catch (Exception e) {
            err(cb, "cache clean failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // TEMP FILES — UNIVERSAL CLEANER (Root or Not)
    // ====================================================================
    public static void cleanTempFiles(Context ctx, LogCallback cb) {
        try {
            // ROOT MODE
            if (isDeviceRooted()) {
                info(cb, "Root detected — ενεργοποιώ GEL Root Temp Cleaner.");
                rootExtraTempCleanup(cb);
                rootExtendedCleanup(cb);       // <<< ΝΕΟ ΜΕΓΑΛΟ ROOT CLEANER
            } else {
                info(cb, "Device not rooted — τρέχει μόνο ο ασφαλής temp cleaner.");
            }

            // 1) Universal Storage Cleaner
            boolean launched = CleanLauncher.openTempStorageCleaner(ctx);

            if (launched) {
                ok(cb, "Άνοιξα Storage/Junk Cleaner της συσκευής.");
                return;
            }

            // 2) Fallback → Internal Storage settings
            try {
                Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);

                ok(cb, "Άνοιξα Storage Settings.");
                return;
            } catch (Exception ignored) {}

            // 3) OEM Deep Cleaner fallback
            boolean deep = CleanLauncher.openDeepCleaner(ctx);

            if (deep) {
                ok(cb, "Fallback: Άνοιξα OEM Cleaner.");
                return;
            }

            // 4) Last resort
            err(cb, "Δεν βρέθηκε cleaner για temp files.");

        } catch (Exception e) {
            err(cb, "cleanTempFiles failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // BROWSER CACHE
    // ====================================================================
    public static void browserCache(Context ctx, LogCallback cb) {
        try {
            PackageManager pm = ctx.getPackageManager();

            String[] browsers = {
                    "com.android.chrome", "org.mozilla.firefox", "com.opera.browser",
                    "com.microsoft.emmx", "com.brave.browser", "com.vivaldi.browser",
                    "com.duckduckgo.mobile.android", "com.sec.android.app.sbrowser",
                    "com.mi.globalbrowser", "com.android.browser", "com.miui.hybrid"
            };

            List<String> installed = new ArrayList<>();

            for (String pkg : browsers) {
                try { pm.getPackageInfo(pkg, 0); installed.add(pkg); }
                catch (PackageManager.NameNotFoundException ignored) {}
            }

            if (installed.isEmpty()) {
                err(cb, "No browser found.");
                return;
            }

            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + installed.get(0)));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);

            ok(cb, "Άνοιξα browser → Storage → Clear Cache.");

        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }

    // ====================================================================
    // RUNNING APPS
    // ====================================================================
    public static void openRunningApps(Context ctx, LogCallback cb) {
        try {
            Intent dev = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            dev.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(dev);

            ok(cb, "Developer menu opened.");
            info(cb, "➡ 'Running Services' για ενεργές εφαρμογές.");

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
        if (children != null) for (File c : children) size += folderSize(c);
        return size;
    }

    private static void deleteFolder(File f) {
        if (f == null || !f.exists()) return;
        if (f.isFile()) { f.delete(); return; }
        File[] children = f.listFiles();
        if (children != null) for (File c : children) deleteFolder(c);
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

    // ====================================================================
    // ROOT DETECTION (SAFE)
    // ====================================================================
    private static boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;

        String[] paths = {
                "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su",
                "/system/bin/.ext/su", "/system/usr/we-need-root/su"
        };

        for (String path : paths) {
            try { if (new File(path).exists()) return true; }
            catch (Throwable ignored) {}
        }

        return false;
    }

    private static boolean runSu(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            int code = p.waitFor();
            return code == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ====================================================================
    // ROOT EXTRA CLEANER (Basic)
    // ====================================================================
    private static void rootExtraTempCleanup(LogCallback cb) {
        String[] paths = {
                "/data/local/tmp",
                "/data/anr",
                "/data/tombstones",
                "/data/system/dropbox",
                "/cache"
        };

        for (String p : paths) {
            String cmd = "rm -rf " + p + "/*";
            if (runSu(cmd)) ok(cb, "Root cleaned: " + p);
            else info(cb, "Root skip: " + p);
        }

        ok(cb, "GEL Root Temp Cleaner ολοκληρώθηκε.");
    }

    // ====================================================================
    // ROOT EXTENDED CLEANER (SAFE — EXTRA MODULES)
    // ====================================================================
    private static void rootExtendedCleanup(LogCallback cb) {
        info(cb, "Root Extended Cleaner ενεργό…");

        String[] extraPaths = {
                "/data/system/usagestats/*",
                "/data/system/package_cache/*",
                "/data/system/procstats/*",
                "/data/system/uiderrors/*",
                "/data/log/*",
                "/data/vendor/log/*"
        };

        for (String p : extraPaths) {
            String cmd = "rm -rf " + p;
            if (runSu(cmd)) ok(cb, "Root extended cleaned: " + p);
            else info(cb, "Skip: " + p);
        }

        ok(cb, "Root Extended Cleaner — COMPLETE.");
    }
}
