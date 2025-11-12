package com.gel.cleaner;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.provider.Settings;

public class PermissionHelper {

    /* =========================================================
     *  SAF (wrapper πάνω από SAFCleaner)
     * ========================================================= */
    public static boolean hasSAF(Context ctx) {
        return SAFCleaner.hasTree(ctx);
    }


    /* =========================================================
     *  USAGE ACCESS
     * ========================================================= */
    public static boolean hasUsageAccess(Context ctx) {
        try {
            AppOpsManager appOps =
                    (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);

            if (appOps == null) return false;

            int mode = appOps.unsafeCheckOpNoThrow(
                    "android:get_usage_stats",
                    Process.myUid(),
                    ctx.getPackageName()
            );

            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (Exception ignore) {
            return false;
        }
    }

    public static void requestUsageAccess(Context ctx) {
        try {
            Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception ignored) {}
    }


    /* =========================================================
     *  ACCESSIBILITY
     * ========================================================= */
    public static boolean hasAccessibility(Context ctx) {
        try {
            return Settings.Secure.getInt(
                    ctx.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            ) == 1;
        } catch (Exception ignore) {
            return false;
        }
    }

    public static void requestAccessibility(Context ctx) {
        try {
            Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception ignored) {}
    }

}
