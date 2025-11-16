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
    // TEMP FILES — UNIVERSAL JUNK CLEANER
    // ====================================================================
    public static void cleanTempFiles(Context ctx, LogCallback cb) {
        try {
            // 1) MIUI / HyperOS Cleaner (όπως στη φωτογραφία σου)
            if (tryLaunch(ctx,
                    "com.miui.cleaner",
                    "com.miui.cleaner.MainActivity")) {
                ok(cb, "Άνοιξα MIUI Cleaner → Εκκαθάριση.");
                info(cb, "➡ Επέλεξε ό,τι θέλεις και πάτα 'Εκκαθάριση'.");
                return;
            }

            if (tryLaunch(ctx,
                    "com.miui.securitycenter",
                    "com.miui.securityscan.MainActivity")) {
                ok(cb, "Άνοιξα MIUI Security Cleaner.");
                return;
            }

            // 2) Samsung Device Care
            if (tryLaunch(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.lool.MainActivity")
             || tryLaunch(ctx,
                    "com.samsung.android.devicecare",
                    "com.samsung.android.devicecare.ui.DeviceCareActivity")) {
                ok(cb, "Άνοιξα Samsung Device Care → Storage / Clean.");
                return;
            }

            // 3) Oppo / Realme
            if (tryLaunch(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.main.MainActivity")
             || tryLaunch(ctx,
                    "com.coloros.oppoguardelf",
                    "com.coloros.oppoguardelf.OppoGuardElfMainActivity")) {
                ok(cb, "Άνοιξα Oppo/Realme Phone Manager Cleaner.");
                return;
            }

            // 4) OnePlus
            if (tryLaunch(ctx,
                    "com.oneplus.security",
                    "com.oneplus.security.chaincleaner.ChainCleanerActivity")) {
                ok(cb, "Άνοιξα OnePlus Cleaner.");
                return;
            }

            // 5) Vivo / iQOO
            if (tryLaunch(ctx,
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity")) {
                ok(cb, "Άνοιξα Vivo Phone Optimizer.");
                return;
            }

            // 6) Huawei / Honor
            if (tryLaunch(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.spaceclean.SpaceCleanActivity")) {
                ok(cb, "Άνοιξα Huawei Space Cleaner.");
                return;
            }

            // 7) Fallback → Storage settings (Pixel / Motorola / λοιποί)
            Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);

            ok(cb, "Άνοιξα Storage → Free up space / Clean.");
            info(cb, "➡ Εκεί θα βρεις τα temporary / junk files της συσκευής.");

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
                    "com.android.chrome",
                    "org.mozilla.firefox",
                    "com.opera.browser",
                    "com.microsoft.emmx",
                    "com.brave.browser",
                    "com.vivaldi.browser",
                    "com.duckduckgo.mobile.android",
                    "com.sec.android.app.sbrowser",
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
            try {
                Intent dev = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                dev.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(dev);

                ok(cb, "Developer menu opened.");
                info(cb, "➡ 'Running Services' για ενεργές εφαρμογές.");
                return;
            } catch (Exception ignored) {}

            Intent i = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);

            ok(cb, "Άνοιξα λίστα εφαρμογών.");
            info(cb, "⚠ Running Apps απαιτεί Developer Options.");

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

    // helper για OEM cleaners
    private static boolean tryLaunch(Context ctx, String pkg, String cls) {
        try {
            Intent i = new Intent();
            i.setClassName(pkg, cls);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
