package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;

// ============================================================
// CleanLauncher — Universal Smart Cleaner (GEL Edition)
// Fully patched for:
// Xiaomi / Redmi / Poco / Mi Browser
// Samsung / Huawei / Oppo / Vivo / Realme / OnePlus
// Motorola / Sony / Pixel
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
    // 1) OEM DEEP CLEAN (Universal)
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
        // **Xiaomi / Redmi / Poco → TRUE CLEANER (and Mi Browser Cleaner)**
        // ------------------------------------------------------------
        if (isXiaomi && !launched) {

            // Main Cleaner UI
            launched = tryComponent(ctx,
                    "com.miui.cleaner",
                    "com.miui.cleaner.MainActivity");

            // SecurityCenter Cleaner
            if (!launched)
                launched = tryComponent(ctx,
                        "com.miui.securitycenter",
                        "com.miui.securityscan.MainActivity");

            // NEW — Mi Browser Cleaner
            if (!launched)
                launched = tryComponent(ctx,
                        "com.miui.miservice",
                        "com.miui.miservice.settings.ClearStorageActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Samsung → Device Care
        // ------------------------------------------------------------
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Huawei
        // ------------------------------------------------------------
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Oppo / Vivo / Realme / OnePlus
        // ------------------------------------------------------------
        if (!launched) {
            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.CleanupActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Motorola
        // ------------------------------------------------------------
        if (isMotorola && !launched) {
            launched = tryComponent(ctx,
                    "com.motorola.ccc",
                    "com.motorola.ccc.settings.CleanerActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Sony
        // ------------------------------------------------------------
        if (isSony && !launched) {
            launched = tryComponent(ctx,
                    "com.sonymobile.settings",
                    "com.sonymobile.settings.cleanup.CleanupActivity");
            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Pixel → Storage Cleaner
        // ------------------------------------------------------------
        if (isPixel && !launched) {
            launched = tryComponent(ctx,
                    "com.google.android.settings.intelligence",
                    "com.google.android.settings.intelligence.modules.storage.StorageActivity");
            if (launched) return true;
        }

        return false;
    }

    // ============================================================
    // 2) SMART CLEAN (Auto-Detect RAM Cleaner)
    // ============================================================
    public static boolean smartClean(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isSamsung  = brand.contains("samsung") || manu.contains("samsung");
        boolean isHuawei   = brand.contains("huawei")  || manu.contains("huawei");

        boolean launched = false;

        // Samsung RAM Cleaner
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.ram.RamActivity");

            if (!launched)
                launched = tryComponent(ctx,
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.ui.memory.MemoryActivity");

            if (launched) return true;
        }

        // Huawei RAM Cleaner
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (launched) return true;
        }

        // Fallback → Universal Deep Cleaner
        return openDeepCleaner(ctx);
    }
}
