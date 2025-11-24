// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELCleaner.java — v2.7 Service-Pro Foldable Edition (FINAL)
// 🔥 Fully Integrated with:
//    • GELFoldableOrchestrator
//    • GELFoldableUIManager
//    • GELFoldableAnimationPack
//    • DualPaneManager
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)
// NOTE2: Πλήρης Foldable-Ready αρχιτεκτονική — χωρίς ημιτελή patches.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GELCleaner {

    // ============================================================
    // LOGGING
    // ============================================================
    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void info(LogCallback cb, String m) { if (cb != null) cb.log("ℹ️ " + m, false); }
    private static void ok  (LogCallback cb, String m) { if (cb != null) cb.log("✅ " + m, false); }
    private static void warn(LogCallback cb, String m){ if (cb != null) cb.log("⚠️ " + m, false); }
    private static void err (LogCallback cb, String m) { if (cb != null) cb.log("❌ " + m, true ); }

    // ============================================================
    // GLOBAL FOLDABLE RUNTIME INIT
    // ============================================================
    private static void initFoldableRuntime(Context ctx) {
        try {
            GELFoldableOrchestrator.initIfPossible(ctx);
            GELFoldableAnimationPack.prepare(ctx);
            DualPaneManager.prepareIfSupported(ctx);
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // CLEAN RAM (Smart/OEM)
    // ============================================================
    public static void cleanRAM(Context ctx, LogCallback cb) {
        initFoldableRuntime(ctx);
        try {
            boolean launched = CleanLauncher.smartClean(ctx);
            if (launched) ok(cb, "Smart RAM Cleaner ενεργοποιήθηκε.");
            else err(cb, "Δεν βρέθηκε RAM Cleaner.");
        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }

    // ============================================================
    // DEEP CLEAN (OEM)
    // ============================================================
    public static void deepClean(Context ctx, LogCallback cb) {
        initFoldableRuntime(ctx);
        try {
            boolean launched = CleanLauncher.openDeepCleaner(ctx);
            if (launched) ok(cb, "Device Deep Cleaner ενεργοποιήθηκε.");
            else err(cb, "Δεν βρέθηκε deep cleaner στη συσκευή.");
        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }

    // ============================================================
    // APP CACHE
    // ============================================================
    public static void cleanAppCache(Context ctx, LogCallback cb) {
        initFoldableRuntime(ctx);
        try {
            long before = folderSize(ctx.getCacheDir());
            deleteFolder(ctx.getCacheDir());
            ok(cb, "App cache cleaned: " + readable(before));
        } catch (Exception e) {
            err(cb, "cache clean failed: " + e.getMessage());
        }
    }

    // ============================================================
    // TEMP FILES CLEANER (Root + Non-Root)
    // ============================================================
    public static void cleanTempFiles(Context ctx, LogCallback cb) {
        initFoldableRuntime(ctx);

        try {
            ensureAllFilesAccessIfNeeded(ctx, cb);

            if (isDeviceRooted()) {
                info(cb, "Root detected — ενεργοποίηση GEL Root Temp Cleaner.");
                rootExtraTempCleanup(cb);
                rootExtendedCleanup(cb);
            } else {
                info(cb, "Non-root device — ασφαλής καθαρισμός.");
            }

            boolean launched = CleanLauncher.openTempStorageCleaner(ctx);
            if (launched) {
                ok(cb, "OEM Storage/Junk Cleaner άνοιξε.");
                return;
            }

            try {
                Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Άνοιξα Storage Settings.");
                return;
            } catch (Exception ignored) {}

            if (CleanLauncher.openDeepCleaner(ctx)) {
                ok(cb, "Fallback Deep Cleaner άνοιξε.");
                return;
            }

            err(cb, "No compatible Temp Cleaner found.");

        } catch (Exception e) {
            err(cb, "cleanTempFiles failed: " + e.getMessage());
        }
    }

    // ============================================================
    // BROWSER CACHE
    // ============================================================
    public static void browserCache(Context ctx, LogCallback cb) {
        initFoldableRuntime(ctx);

        try {
            PackageManager pm = ctx.getPackageManager();

            String[] browsers = {
                    "com.android.chrome","com.chrome.beta",
                    "org.mozilla.firefox","org.mozilla.fenix",
                    "com.opera.browser","com.opera.mini.native",
                    "com.microsoft.emmx","com.brave.browser",
                    "com.vivaldi.browser","com.duckduckgo.mobile.android",
                    "com.sec.android.app.sbrowser",
                    "com.mi.globalbrowser","com.android.browser",
                    "com.miui.hybrid"
            };

            List<String> installed = new ArrayList<>();
            for (String pkg : browsers) {
                try { pm.getPackageInfo(pkg, 0); installed.add(pkg); }
                catch (PackageManager.NameNotFoundException ignored) {}
            }

            if (installed.isEmpty()) {
                err(cb, "No installed browsers.");
                return;
            }

            if (installed.size() == 1) {
                openAppDetails(ctx, installed.get(0));
                ok(cb, "Άνοιξα browser → Cache settings.");
                return;
            }

            try {
                Intent chooser = new Intent(ctx, BrowserListActivity.class);
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (DualPaneManager.isDualPaneActive(ctx)) {
                    DualPaneManager.openSide(ctx, chooser);
                    ok(cb, "Browser chooser σε dual-pane mode.");
                } else {
                    ctx.startActivity(chooser);
                    ok(cb, "Άνοιξα Browser Chooser list.");
                }

                info(cb, "Επίλεξε browser → Storage → Clear Cache.");
                return;

            } catch (Exception e) {
                openAppDetails(ctx, installed.get(0));
                warn(cb, "Chooser failed — άνοιξα τον 1ο browser.");
            }

        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }

    // ============================================================
    // RUNNING APPS (Developer Menu)
    // ============================================================
    public static void openRunningApps(Context ctx, LogCallback cb) {
        initFoldableRuntime(ctx);

        try {
            Intent dev = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            dev.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(dev);

            ok(cb, "Developer menu opened.");
            info(cb, "➡ Άνοιξε 'Running Services'.");
        } catch (Exception e) {
            err(cb, "openRunningApps failed: " + e.getMessage());
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private static void openAppDetails(Context ctx, String pkg) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + pkg));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (DualPaneManager.isDualPaneActive(ctx)) {
                DualPaneManager.openSide(ctx, i);
            } else {
                ctx.startActivity(i);
            }
        } catch (Exception ignored) {}
    }

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
        if (f.isFile()) { try { f.delete(); } catch (Throwable ignored) {} return; }
        File[] children = f.listFiles();
        if (children != null) for (File c : children) deleteFolder(c);
        try { f.delete(); } catch (Throwable ignored) {}
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

    // ============================================================
    // PERMISSION SELF-REPAIR (Android 11+)
    // ============================================================
    private static void ensureAllFilesAccessIfNeeded(Context ctx, LogCallback cb) {
        if (ctx == null) return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return;

        try {
            if (!Environment.isExternalStorageManager()) {
                warn(cb, "Android 11+ περιορισμός. Χρειάζεται All-Files Access.");
                info(cb, "➡ Άνοιγμα Settings για δικαίωμα αρχείων.");

                Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                i.setData(Uri.parse("package:" + ctx.getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (DualPaneManager.isDualPaneActive(ctx)) {
                    DualPaneManager.openSide(ctx, i);
                } else {
                    ctx.startActivity(i);
                }
            }
        } catch (Throwable ignored) {}
    }

    // ============================================================
    // ROOT CHECK
    // ============================================================
    private static boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;

        String[] paths = {
                "/system/bin/su","/system/xbin/su","/sbin/su","/system/su",
                "/system/bin/.ext/su","/system/usr/we-need-root/su"
        };
        for (String p : paths)
            try { if (new File(p).exists()) return true; }
            catch (Throwable ignored) {}

        return false;
    }

    private static boolean runSu(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su","-c",cmd});
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // ROOT CLEANERS
    // ============================================================
    private static void rootExtraTempCleanup(LogCallback cb) {
        String[] paths = {
                "/data/local/tmp",
                "/data/anr",
                "/data/tombstones",
                "/data/system/dropbox",
                "/cache"
        };
        for (String p : paths) {
            if (runSu("rm -rf " + p + "/*"))
                ok(cb, "Root cleaned: " + p);
            else
                info(cb, "Skip: " + p);
        }
        ok(cb, "GEL Root Temp Cleaner ολοκληρώθηκε.");
    }

    private static void rootExtendedCleanup(LogCallback cb) {
        info(cb, "Root Extended Cleaner...");
        String[] extra = {
                "/data/system/usagestats/*",
                "/data/system/package_cache/*",
                "/data/system/procstats/*",
                "/data/system/uiderrors/*",
                "/data/log/*",
                "/data/vendor/log/*"
        };
        for (String p : extra) {
            if (runSu("rm -rf " + p))
                ok(cb, "Root cleaned: " + p);
            else
                info(cb, "Skip: " + p);
        }
        ok(cb, "Root Extended Cleaner COMPLETE.");
    }
}
