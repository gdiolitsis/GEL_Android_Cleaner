// ============================================================
// GEL SMART CLEAN — Silent Auto-Detect Mode
// ============================================================
public static boolean smartClean(Context ctx) {

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
    // 1) OEMs με ξεχωριστό RAM Cleaner → RAM FIRST
    // ------------------------------------------------------------
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

    if (isHuawei && !launched) {
        launched = tryComponent(ctx,
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.optimize.process.ProtectActivity");

        if (launched) return true;
    }

    // ------------------------------------------------------------
    // 2) Xiaomi / OPPO / VIVO / Realme / OnePlus → Cleanup ONLY
    //    (όλοι πάνε στο ίδιο panel)
    // ------------------------------------------------------------
    if ((isXiaomi || isOppo || isVivo || isRealme || isOnePlus) && !launched) {

        launched = tryComponent(ctx,
                "com.miui.cleaner",
                "com.miui.cleaner.MainActivity");

        if (!launched)
            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.CleanupActivity");

        if (!launched)
            launched = tryComponent(ctx,
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.PhoneCleanActivity");

        if (launched) return true;
    }

    // ------------------------------------------------------------
    // 3) Motorola / Sony / Pixel → Storage / Device Help
    // ------------------------------------------------------------
    if (isMotorola && !launched) {
        launched = tryComponent(ctx,
                "com.motorola.ccc",
                "com.motorola.ccc.settings.CleanerActivity");
        if (launched) return true;
    }

    if (isSony && !launched) {
        launched = tryComponent(ctx,
                "com.sonymobile.settings",
                "com.sonymobile.settings.cleanup.CleanupActivity");
        if (launched) return true;
    }

    if (isPixel && !launched) {
        launched = tryComponent(ctx,
                "com.google.android.settings.intelligence",
                "com.google.android.settings.intelligence.modules.storage.StorageActivity");
        if (launched) return true;
    }

    // ------------------------------------------------------------
    // 4) Fallback → Universal Cleaner Panel
    // ------------------------------------------------------------
    return openDeepCleaner(ctx);
}
