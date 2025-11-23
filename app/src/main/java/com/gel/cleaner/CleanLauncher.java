// GDiolitsis Engine Lab (GEL) — Author & Developer
// CleanLauncher — Foldable Ready (GEL Edition v2.0)
// Universal OEM Cleaner Launcher — Safe, Fast, Production Ready
// NOTE: Full file ready for copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;

// Foldable dependencies
import androidx.annotation.NonNull;

public class CleanLauncher implements GELFoldableCallback {

    private final Context ctx;
    private final GELFoldableDetector foldDetector;
    private final GELFoldableUIManager uiManager;
    private final GELFoldableAnimationPack animPack;
    private final DualPaneManager dualPane;

    public CleanLauncher(Context ctx) {
        this.ctx = ctx;

        // Foldable engine init
        uiManager    = new GELFoldableUIManager(ctx);
        animPack     = new GELFoldableAnimationPack(ctx);
        dualPane     = new DualPaneManager(ctx);
        foldDetector = new GELFoldableDetector(ctx, this);
    }

    // ============================================================
    // START / STOP FOLDABLE LISTENER (call from Activity)
    // ============================================================
    public void start() {
        foldDetector.start();
    }

    public void stop() {
        foldDetector.stop();
    }

    // ============================================================
    // FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // Light hinge animation only
        animPack.applyHingePulse(posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        uiManager.applyUI(isInner);
        dualPane.dispatchMode(isInner);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private static String low(String s) {
        return (s == null ? "" : s.toLowerCase().trim());
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
    // TEMP STORAGE CLEANER
    // ============================================================
    public boolean openTempStorageCleaner() {

        // Xiaomi
        if (tryComponent(ctx, "com.miui.cleaner", "com.miui.cleaner.MainActivity")) return true;
        if (tryComponent(ctx, "com.miui.securitycenter", "com.miui.securityscan.MainActivity")) return true;

        // Samsung
        if (tryComponent(ctx, "com.samsung.android.lool", "com.samsung.android.lool.MainActivity")) return true;
        if (tryComponent(ctx, "com.samsung.android.devicecare",
                "com.samsung.android.devicecare.ui.DeviceCareActivity")) return true;

        // Oppo / Realme
        if (tryComponent(ctx, "com.coloros.phonemanager",
                "com.coloros.phonemanager.main.MainActivity")) return true;

        if (tryComponent(ctx, "com.coloros.oppoguardelf",
                "com.coloros.oppoguardelf.OppoGuardElfMainActivity")) return true;

        // Huawei
        if (tryComponent(ctx, "com.huawei.systemmanager",
                "com.huawei.systemmanager.spaceclean.SpaceCleanActivity")) return true;

        // Vivo
        if (tryComponent(ctx, "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity")) return true;

        // OnePlus
        if (tryComponent(ctx, "com.oneplus.security",
                "com.oneplus.security.chaincleaner.ChainCleanerActivity")) return true;

        return false;
    }

    // ============================================================
    // DEEP CLEAN
    // ============================================================
    public boolean openDeepCleaner() {

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

        // Xiaomi
        if (isXiaomi) {
            if (tryComponent(ctx, "com.miui.cleaner", "com.miui.cleaner.MainActivity")) return true;
            if (tryComponent(ctx, "com.miui.securitycenter", "com.miui.securityscan.MainActivity")) return true;
            if (tryComponent(ctx, "com.miui.miservice", "com.miui.misservice.settings.ClearStorageActivity")) return true;
        }

        // Samsung
        if (isSamsung) {
            if (tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity")) return true;
        }

        // Huawei
        if (isHuawei) {
            if (tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity")) return true;
        }

        // Oppo / Realme / Vivo / OnePlus
        if (tryComponent(ctx,
                "com.coloros.phonemanager",
                "com.coloros.phonemanager.CleanupActivity")) return true;

        // Motorola
        if (isMotorola) {
            if (tryComponent(ctx,
                    "com.motorola.ccc",
                    "com.motorola.ccc.settings.CleanerActivity")) return true;
        }

        // Sony
        if (isSony) {
            if (tryComponent(ctx,
                    "com.sonymobile.settings",
                    "com.sonymobile.settings.cleanup.CleanupActivity")) return true;
        }

        // Pixel
        if (isPixel) {
            if (tryComponent(ctx,
                    "com.google.android.settings.intelligence",
                    "com.google.android.settings.intelligence.modules.storage.StorageActivity")) return true;
        }

        return false;
    }

    // ============================================================
    // SMART CLEAN (RAM Cleaner)
    // ============================================================
    public boolean smartClean() {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isSamsung = brand.contains("samsung") || manu.contains("samsung");
        boolean isHuawei  = brand.contains("huawei")  || manu.contains("huawei");

        // Samsung RAM Cleaner
        if (isSamsung) {
            if (tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.ram.RamActivity")) return true;

            if (tryComponent(ctx,
                    "com.samsung.android.sm",
                    "com.samsung.android.sm.ui.memory.MemoryActivity")) return true;
        }

        // Huawei RAM Cleaner
        if (isHuawei) {
            if (tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity")) return true;
        }

        return openDeepCleaner();
    }
}
