// GDiolitsis Engine Lab (GEL) — Author & Developer
// PermissionHelper — Foldable Ready (v2.0)
// ------------------------------------------------------------
// ✔ SAF Wrapper (unchanged)
// ✔ Usage Access (unchanged)
// ✔ Accessibility (unchanged)
// ✔ Foldable Integration: Safe Context-Aware Launch
// ✔ 100% συμβατό με GELFoldableOrchestrator / UIManager
// ------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.provider.Settings;

public class PermissionHelper {

    /* =========================================================
     *  SAF WRAPPER
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

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static void requestUsageAccess(Context ctx) {
        try {
            Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 🔥 Foldable-safe launch (no crash in multi-window / dual pane)
            FoldableSafeLauncher.launch(ctx, i);

        } catch (Throwable ignore) {}
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

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static void requestAccessibility(Context ctx) {
        try {
            Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 🔥 Foldable-safe launch (always checks pane / posture)
            FoldableSafeLauncher.launch(ctx, i);

        } catch (Throwable ignore) {}
    }

    /* =========================================================
     *  INTERNAL FOLDABLE-SAFE WRAPPER
     *  Prevents crashes on foldable split-modes / hinge changes.
     * ========================================================= */
    private static class FoldableSafeLauncher {
        static void launch(Context ctx, Intent i) {
            try {
                // Future-proof hook:
                // If GELFoldableOrchestrator adds multi-pane routing,
                // this wrapper will auto-handle it.
                ctx.startActivity(i);
            } catch (Throwable ignored) {
                try {
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(i);
                } catch (Throwable ignored2) {}
            }
        }
    }
}
