package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import java.util.Locale;

public class CleanLauncher {

    // ============================================================
    // HELPERS
    // ============================================================
    private static boolean tryComponent(Context ctx, String pkg, String cls) {
        try {
            Intent i = new Intent();
            i.setClassName(pkg, cls);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception ignored) { return false; }
    }

    private static boolean tryAction(Context ctx, String action) {
        try {
            Intent i = new Intent(action);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception ignored) { return false; }
    }

    private static String low(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    // ============================================================
    // UNIVERSAL BROWSER CACHE CLEANER (NEW!)
    // ============================================================
    public static boolean openBrowserCleaner(Context ctx) {

        // Chrome
        if (tryComponent(ctx,
                "com.android.chrome",
                "com.google.android.apps.chrome.settings.MainSettingsActivity"))
            return true;

        // Samsung Internet
        if (tryComponent(ctx,
                "com.sec.android.app.sbrowser",
                "com.sec.android.app.sbrowser.SBrowserMainActivity"))
            return true;

        // Edge
        if (tryComponent(ctx,
                "com.microsoft.emmx",
                "com.microsoft.ruby.Main"))
            return true;

        // Opera
        if (tryComponent(ctx,
                "com.opera.browser",
                "com.opera.Opera"))
            return true;

        // Firefox
        if (tryComponent(ctx,
                "org.mozilla.firefox",
                "org.mozilla.fenix.HomeActivity"))
            return true;

        // Brave
        if (tryComponent(ctx,
                "com.brave.browser",
                "com.brave.browser.settings.MainPreferences"))
            return true;

        // Universal fallback: App Info for browsers only
        try {
            Intent i = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    // ============================================================
    // UNIVERSAL TEMP FILES CLEANER (NEW!)
    // ============================================================
    public static boolean openTempCleaner(Context ctx) {

        // Android 12+ has Storage Cleanup panel
        if (Build.VERSION.SDK_INT >= 31) {
            if (tryAction(ctx, Settings.ACTION_STORAGE_MANAGER_SETTINGS))
                return true;
        }

        // Generic Storage panel (works in ALL brands)
        if (tryAction(ctx, Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
            return true;

        return false;
    }

    // ============================================================
    // OEM MEMORY CLEANER (RAM)
    // ============================================================
    public static boolean openMemoryCleaner(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isSamsung = brand.contains("samsung") || manu.contains("samsung");
        boolean isXiaomi  = brand.contains("xiaomi")  || brand.contains("redmi")
                || brand.contains("poco") || manu.contains("xiaomi") || manu.contains("redmi");
        boolean isHuawei  = brand.contains("huawei") || manu.contains("huawei");
        boolean isOppo    = brand.contains("oppo")   || manu.contains("oppo")
                || manu.contains("realme") || brand.contains("realme");
        boolean isVivo    = brand.contains("vivo")   || manu.contains("vivo");
        boolean isOnePlus = brand.contains("oneplus")|| manu.contains("oneplus");
        boolean isMoto    = brand.contains("motorola")|| manu.contains("motorola");

        boolean launched = false;

        // ------------------------------------------------------------
        // SAMSUNG → Device Care
        // ------------------------------------------------------------
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.ram.RamActivity");

            if (!launched) launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (!launched) launched = tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // XIAOMI / REDMI / POCO → MIUI Cleaner
        // ------------------------------------------------------------
        if (isXiaomi && !launched) {
            launched = tryComponent(ctx,
                    "com.miui.securitycenter",
                    "com.miui.optimizecenter.MainActivity");

            if (!launched) launched = tryComponent(ctx,
                    "com.miui.cleaner",
                    "com.miui.cleaner.MainActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // HUAWEI → System Manager
        // ------------------------------------------------------------
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // OPPO / REALME
        // ------------------------------------------------------------
        if (isOppo && !launched) {
            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.MainActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // VIVO
        // ------------------------------------------------------------
        if (isVivo && !launched) {
            launched = tryComponent(ctx,
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // ONEPLUS
        // ------------------------------------------------------------
        if (isOnePlus && !launched) {
            launched = tryComponent(ctx,
                    "com.oneplus.security",
                    "com.oneplus.security.cleaner.CleanerActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // MOTOROLA
        // ------------------------------------------------------------
        if (isMoto && !launched) {
            launched = tryComponent(ctx,
                    "com.motorola.ccc",
                    "com.motorola.ccc.notification.CccSettingsActivity");

            if (launched) return true;
        }

        return false;
    }

    // ============================================================
    // OEM DEEP CLEANER
    // ============================================================
    public static boolean openDeepCleaner(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isSamsung = brand.contains("samsung") || manu.contains("samsung");
        boolean isXiaomi  = brand.contains("xiaomi")  || brand.contains("redmi")
                || manu.contains("xiaomi");
        boolean isHuawei  = brand.contains("huawei") || manu.contains("huawei");

        boolean launched = false;

        // Samsung Device Care Dashboard
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (!launched) launched = tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (launched) return true;
        }

        // Xiaomi Security Center
        if (isXiaomi && !launched) {
            launched = tryComponent(ctx,
                    "com.miui.securitycenter",
                    "com.miui.securityscan.MainActivity");

            if (launched) return true;
        }

        // Huawei System Manager
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity");

            if (launched) return true;
        }

        return false;
    }
}
