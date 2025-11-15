package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;

// ============================================================
// CleanLauncher — Universal Deep Cleaner Launcher (GEL Edition)
// ============================================================
public class CleanLauncher {

    private static String low(String s) {
        return (s == null) ? "" : s.toLowerCase().trim();
    }

    private static boolean tryComponent(Context ctx, String pkg, String cls) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkg, cls));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // UNIVERSAL OEM DEEP CLEANER ENTRY
    // ============================================================
    public static boolean openDeepCleaner(Context ctx) {

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
        boolean isMotorola = brand.contains("motorola") || manu.contains("motorola");
        boolean isSony     = brand.contains("sony")     || manu.contains("sony");
        boolean isPixel    = brand.contains("google")   || manu.contains("google");

        boolean launched = false;

        // ------------------------------------------------------------
        // Xiaomi / Redmi / Poco
        // ------------------------------------------------------------
        if (isXiaomi && !launched) {
            launched = tryComponent(ctx, "com.miui.cleaner", "com.miui.cleaner.MainActivity");
            if (!launched) launched = tryComponent(ctx, "com.miui.securitycenter", "com.miui.securityscan.MainActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Samsung Device Care
        // ------------------------------------------------------------
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");
            if (!launched)
                launched = tryComponent(ctx,
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Huawei System Manager
        // ------------------------------------------------------------
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // OPPO — Phone Manager
        // ------------------------------------------------------------
        if (isOppo && !launched) {
            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.CleanupActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Vivo — iManager
        // ------------------------------------------------------------
        if (isVivo && !launched) {
            launched = tryComponent(ctx,
                    "com.vivo.abe",
                    "com.vivo.applicationbehaviorengine.DeepCleanActivity");
            if (!launched)
                launched = tryComponent(ctx,
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.PhoneCleanActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // OnePlus — Deep Clean (uses OPPO/ColorOS components)
        // ------------------------------------------------------------
        if (isOnePlus && !launched) {
            launched = tryComponent(ctx,
                    "com.oneplus.security",
                    "com.oneplus.security.cleaner.CleanerActivity");
            if (!launched)
                launched = tryComponent(ctx,
                        "com.coloros.phonemanager",
                        "com.coloros.phonemanager.CleanupActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Realme — Realme Phone Manager
        // ------------------------------------------------------------
        if (isRealme && !launched) {
            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.CleanupActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Motorola — Device Help / Smart Manager
        // ------------------------------------------------------------
        if (isMotorola && !launched) {
            launched = tryComponent(ctx,
                    "com.motorola.ccc",
                    "com.motorola.ccc.settings.CleanerActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Sony — Xperia Care / Smart Cleaner
        // ------------------------------------------------------------
        if (isSony && !launched) {
            launched = tryComponent(ctx,
                    "com.sonymobile.settings",
                    "com.sonymobile.settings.cleanup.CleanupActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Google Pixel — Storage Manager
        // ------------------------------------------------------------
        if (isPixel && !launched) {
            launched = tryComponent(ctx,
                    "com.google.android.settings.intelligence",
                    "com.google.android.settings.intelligence.modules.storage.StorageActivity");
            if (!launched)
                launched = tryComponent(ctx,
                        "com.google.android.apps.nexuslauncher",
                        "com.google.android.apps.nexuslauncher.StorageActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // NOTHING WORKED → Return false
        // ------------------------------------------------------------
        return false;
    }
}
