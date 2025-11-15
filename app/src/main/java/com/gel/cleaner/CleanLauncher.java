package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;

// ============================================================
// CleanLauncher (OEM Deep Cleaner Launcher)
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
    // OEM DEEP CLEANER  (FIXED FOR MIUI)
    // ============================================================
    public static boolean openDeepCleaner(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isXiaomi = brand.contains("xiaomi")
                || brand.contains("redmi")
                || brand.contains("poco")
                || manu.contains("xiaomi")
                || manu.contains("redmi")
                || manu.contains("poco");

        boolean isSamsung = brand.contains("samsung") || manu.contains("samsung");
        boolean isHuawei  = brand.contains("huawei")  || manu.contains("huawei");

        boolean launched = false;

        // ------------------------------------------------------------
        // XIAOMI / REDMI / POCO → TRUE CLEAN PANEL
        // ------------------------------------------------------------
        if (isXiaomi && !launched) {

            launched = tryComponent(ctx,
                    "com.miui.cleaner",
                    "com.miui.cleaner.MainActivity");

            if (!launched)
                launched = tryComponent(ctx,
                        "com.miui.securitycenter",
                        "com.miui.securityscan.MainActivity");

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // Samsung Device Care Dashboard
        // ------------------------------------------------------------
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (!launched) launched = tryComponent(ctx,
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

        return false;
    }
}
