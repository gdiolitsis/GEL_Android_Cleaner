package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Locale;

public class CleanLauncher {

    // ============================================================
    //  HELPERS
    // ============================================================
    private static boolean tryComponent(Context ctx, String pkg, String cls) {
        try {
            Intent i = new Intent();
            i.setClassName(pkg, cls);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean tryAction(Context ctx, String action) {
        try {
            Intent i = new Intent(action);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String low(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    // ============================================================
    //  PUBLIC API
    // ============================================================

    /**
     * Προσπαθεί να ανοίξει τον "μνήμη / cleaner" πίνακα της συσκευής.
     * Επιστρέφει true αν άνοιξε κάποιο OEM panel, false αν ΠΡΕΠΕΙ
     * να πάμε σε generic Android Settings.
     */
    public static boolean openMemoryCleaner(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isSamsung = brand.contains("samsung") || manu.contains("samsung");
        boolean isXiaomi  = brand.contains("xiaomi") || brand.contains("redmi")
                || brand.contains("poco") || manu.contains("xiaomi") || manu.contains("redmi");
        boolean isHuawei  = brand.contains("huawei") || manu.contains("huawei");
        boolean isOppo    = brand.contains("oppo") || manu.contains("oppo")
                || manu.contains("realme") || brand.contains("realme");
        boolean isVivo    = brand.contains("vivo") || manu.contains("vivo");
        boolean isOnePlus = brand.contains("oneplus") || manu.contains("oneplus");
        boolean isMoto    = brand.contains("motorola") || manu.contains("motorola") || brand.contains("moto");

        boolean launched = false;

        // ------------------------------------------------------------
        // SAMSUNG → Device Care / Smart Manager (διαφορετικές εκδόσεις)
        // ------------------------------------------------------------
        if (isSamsung && !launched) {
            // Νεότερα OneUI (Device Care / Device Maintenance)
            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.ram.RamActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.samsung.android.lool",
                        "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");
            }

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");
            }

            if (!launched) {
                // Παλαιότερα Smart Manager
                launched = tryComponent(ctx,
                        "com.samsung.android.sm_cn",
                        "com.samsung.android.sm.ui.ram.RamActivity");
            }

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // XIAOMI / REDMI / POCO → MIUI Cleaner / Security Center
        // ------------------------------------------------------------
        if (isXiaomi && !launched) {
            launched = tryComponent(ctx,
                    "com.miui.cleaner",
                    "com.miui.cleaner.MainActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.miui.securitycenter",
                        "com.miui.optimizecenter.MainActivity");
            }

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.miui.securitycenter",
                        "com.miui.securityscan.MainActivity");
            }

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // HUAWEI → Phone Manager / Optimizer
        // ------------------------------------------------------------
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity");
            }

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // OPPO / REALME → Phone Manager / Security Center
        // ------------------------------------------------------------
        if (isOppo && !launched) {
            launched = tryComponent(ctx,
                    "com.coloros.phonemanager",
                    "com.coloros.phonemanager.MainActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.coloros.oppoguardelf",
                        "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
            }

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.coloros.safe",
                        "com.coloros.safe.securityvirus.SecurityVirusScanActivity");
            }

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // VIVO → iManager
        // ------------------------------------------------------------
        if (isVivo && !launched) {
            launched = tryComponent(ctx,
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.vivo.space",
                        "com.vivo.space.ui.MainActivity");
            }

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // ONEPLUS → Device Manager / Cleaner (κάποιες ROMs)
        // ------------------------------------------------------------
        if (isOnePlus && !launched) {
            launched = tryComponent(ctx,
                    "com.oneplus.security",
                    "com.oneplus.security.cleaner.CleanerActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.oneplus.systemui",
                        "com.oneplus.systemui.DeviceMaintenanceActivity");
            }

            if (launched) return true;
        }

        // ------------------------------------------------------------
        // MOTOROLA → Device Help / Device Manager
        // ------------------------------------------------------------
        if (isMoto && !launched) {
            launched = tryComponent(ctx,
                    "com.motorola.ccc",
                    "com.motorola.ccc.notification.CccSettingsActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.motorola.devicehelp",
                        "com.motorola.devicehelp.HomeActivity");
            }

            if (launched) return true;
        }

        // Αν φτάσουμε εδώ → δεν βρέθηκε OEM cleaner
        return false;
    }

    /**
     * Deep clean: πιο "γενικό" panel. Χρησιμοποιούμε OEM panels
     * όπου υπάρχουν, αλλιώς αφήνουμε GELCleaner να πάει σε Settings.
     */
    public static boolean openDeepCleaner(Context ctx) {

        String brand = low(Build.BRAND);
        String manu  = low(Build.MANUFACTURER);

        boolean isSamsung = brand.contains("samsung") || manu.contains("samsung");
        boolean isXiaomi  = brand.contains("xiaomi") || brand.contains("redmi")
                || brand.contains("poco") || manu.contains("xiaomi") || manu.contains("redmi");
        boolean isHuawei  = brand.contains("huawei") || manu.contains("huawei");

        boolean launched = false;

        // Samsung Device Care main dashboard
        if (isSamsung && !launched) {
            launched = tryComponent(ctx,
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.samsung.android.sm",
                        "com.samsung.android.sm.ui.dashboard.SmartManagerDashBoardActivity");
            }

            if (launched) return true;
        }

        // Xiaomi main Security Center
        if (isXiaomi && !launched) {
            launched = tryComponent(ctx,
                    "com.miui.securitycenter",
                    "com.miui.securitycenter.SecurityCenter");

            if (!launched) {
                launched = tryComponent(ctx,
                        "com.miui.securitycenter",
                        "com.miui.securityscan.MainActivity");
            }

            if (launched) return true;
        }

        // Huawei main System Manager
        if (isHuawei && !launched) {
            launched = tryComponent(ctx,
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.mainscreen.MainScreenActivity");

            if (launched) return true;
        }

        return false;
    }
}
