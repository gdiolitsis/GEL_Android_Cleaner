package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;
import android.provider.Settings;

/*
 * ============================================================
 * CleanLauncher — Universal Temp Cleaner (1-Tap Edition)
 * ============================================================
 * Καλύπτει:
 * Xiaomi / Redmi / Poco
 * Samsung
 * Huawei / Honor
 * Oppo / Realme / Vivo / OnePlus
 * Motorola / Sony
 * Pixel
 * AOSP
 * Και fallback OEM Deep Cleaner όταν δεν υπάρχει Storage menu.
 * ============================================================
 */

public class CleanLauncher {

    private static String low(String s) {
        return (s == null) ? "" : s.toLowerCase().trim();
    }

    private static boolean tryComponent(Context ctx, String pkg, String cls) {
        try {
            Intent i = new Intent();
            i.setComponent(new ComponentName(pkg, cls));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean tryIntent(Context ctx, Intent i) {
        try {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // UNIVERSAL 1-TAP TEMP STORAGE CLEAN
    // ============================================================
    public static boolean openTempStorageCleaner(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isXiaomi   = brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")
                || manu.contains("xiaomi") || manu.contains("redmi") || manu.contains("poco");

        boolean isSamsung  = brand.contains("samsung") || manu.contains("samsung");
        boolean isHuawei   = brand.contains("huawei")  || manu.contains("huawei");
        boolean isOppo     = brand.contains("oppo")    || manu.contains("oppo");
        boolean isVivo     = brand.contains("vivo")    || manu.contains("vivo");
        boolean isOnePlus  = brand.contains("oneplus") || manu.contains("oneplus");
        boolean isRealme   = brand.contains("realme")  || manu.contains("realme");
        boolean isSony     = brand.contains("sony")    || manu.contains("sony");
        boolean isMoto     = brand.contains("motorola")|| manu.contains("motorola");
        boolean isPixel    = brand.contains("google")  || manu.contains("google");

        boolean launched = false;

        // ============================================================
        // XIAOMI / REDMI / POCO — MIUI Storage (φέτες)
        // ============================================================
        if (isXiaomi && !launched) {

            launched = tryComponent(ctx,
                    "com.miui.securitycenter",
                    "com.miui.storage.ui.StorageSettingsActivity");

            if (!launched)
                launched = tryComponent(ctx,
                        "com.android.settings",
                        "com.android.settings.Settings$StorageSettingsActivity");

            if (launched) return true;
        }

        // ============================================================
        // SAMSUNG — Device Care → Storage
        // ============================================================
        if (isSamsung && !launched) {

            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.storage.StorageActivity");

            if (!launched)
                launched = tryComponent(ctx,
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (launched) return true;
        }

        // ============================================================
        // HUAWEI / HONOR
        // ============================================================
        if (isHuawei && !launched) {

            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.spacecleanner.ui.SpaceCleanActivity");

            if (!launched)
                launched = tryComponent(ctx,
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.mainscreen.MainScreenActivity");

            if (launched) return true;
        }

        // ============================================================
        // OPPO / REALME / VIVO / ONEPLUS (ColorOS / Funtouch / OxygenOS)
        // ============================================================
        if (!launched && (isOppo || isRealme || isVivo || isOnePlus)) {

            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.cleanup.CleanupActivity");

            if (!launched)
                launched = tryComponent(ctx,
                        "com.coloros.phonemanager",
                        "com.coloros.phonemanager.space.SpaceManagerActivity");

            if (launched) return true;
        }

        // ============================================================
        // SONY
        // ============================================================
        if (isSony && !launched) {

            launched = tryComponent(ctx,
                    "com.sonymobile.settings",
                    "com.sonymobile.settings.storage.StorageActivity");

            if (launched) return true;
        }

        // ============================================================
        // MOTOROLA
        // ============================================================
        if (isMoto && !launched) {

            launched = tryComponent(ctx,
                    "com.motorola.ccc",
                    "com.motorola.ccc.settings.StorageActivity");

            if (launched) return true;
        }

        // ============================================================
        // PIXEL — Storage Dashboard
        // ============================================================
        if (isPixel && !launched) {
            Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            launched = tryIntent(ctx, i);
            if (launched) return true;
        }

        // ============================================================
        // AOSP — Generic storage
        // ============================================================
        if (!launched) {
            Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            launched = tryIntent(ctx, i);
            if (launched) return true;
        }

        // ============================================================
        // FINAL FALLBACK — OEM Deep Cleaner (ΑΣΦΑΛΕΣ)
        // ============================================================
        return openDeepCleaner(ctx);
    }

    // ============================================================
    // EXISTING FUNCTION — OEM Deep Cleaner (ασφαλές)
    // ============================================================
    public static boolean openDeepCleaner(Context ctx) {

        boolean launched = false;

        launched = tryComponent(ctx,
                "com.miui.cleaner",
                "com.miui.cleaner.MainActivity");

        if (!launched)
            launched = tryComponent(ctx,
                    "com.miui.securitycenter",
                    "com.miui.securityscan.MainActivity");

        if (!launched)
            launched = tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

        return launched;
    }
}
