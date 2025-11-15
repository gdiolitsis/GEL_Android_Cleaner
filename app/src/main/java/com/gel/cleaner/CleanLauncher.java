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
    // XIAOMI / REDMI / POCO → TRUE CLEAN PANEL (the one in your photos)
    // ------------------------------------------------------------
    if (isXiaomi && !launched) {

        // This is the real Cleanup screen (your left screenshot)
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
