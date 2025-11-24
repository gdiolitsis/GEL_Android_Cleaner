// GDiolitsis Engine Lab (GEL) — Author & Developer
// PermissionHelper — Foldable Ready (v2.0) — GEL FINAL PATCH
// ------------------------------------------------------------
// ✔ SAF Wrapper (unchanged API)
// ✔ Usage Access (unchanged API)
// ✔ Accessibility (unchanged API)
// ✔ Foldable Integration: DualPane-aware + Multi-window safe launch
// ✔ Ultra-safe null guards / API fallbacks
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.
// ------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;

public class PermissionHelper {

    /* =========================================================
     *  SAF WRAPPER (unchanged)
     * ========================================================= */
    public static boolean hasSAF(Context ctx) {
        try {
            return ctx != null && SAFCleaner.hasTree(ctx);
        } catch (Throwable ignore) {
            return false;
        }
    }

    /* =========================================================
     *  USAGE ACCESS (unchanged)
     * ========================================================= */
    public static boolean hasUsageAccess(Context ctx) {
        if (ctx == null) return false;

        try {
            AppOpsManager appOps =
                    (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);

            if (appOps == null) return false;

            int mode;

            // Android Q+ has unsafeCheckOpNoThrow
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mode = appOps.unsafeCheckOpNoThrow(
                        "android:get_usage_stats",
                        Process.myUid(),
                        ctx.getPackageName()
                );
            } else {
                // legacy safe fallback
                //noinspection deprecation
                mode = appOps.checkOpNoThrow(
                        "android:get_usage_stats",
                        Process.myUid(),
                        ctx.getPackageName()
                );
            }

            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static void requestUsageAccess(Context ctx) {
        if (ctx == null) return;

        try {
            Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 🔥 Foldable-safe launch (no crash in multi-window / dual pane)
            FoldableSafeLauncher.launch(ctx, i);

        } catch (Throwable ignore) {}
    }

    /* =========================================================
     *  ACCESSIBILITY (unchanged)
     * ========================================================= */
    public static boolean hasAccessibility(Context ctx) {
        if (ctx == null) return false;

        try {
            return Settings.Secure.getInt(
                    ctx.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED, 0
            ) == 1;

        } catch (Throwable ignore) {
            return false;
        }
    }

    public static void requestAccessibility(Context ctx) {
        if (ctx == null) return;

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
     *  DualPane-aware without hard dependency.
     * ========================================================= */
    private static class FoldableSafeLauncher {

        static void launch(Context ctx, Intent i) {
            if (ctx == null || i == null) return;

            // 1) Try DualPane side-open if available/active
            try {
                if (DualPaneManager.isDualPaneActive(ctx)) {
                    DualPaneManager.openSide(ctx, i);
                    return;
                }
            } catch (Throwable ignored) {
                // DualPane not present → continue normal path
            }

            // 2) Normal launch
            try {
                ctx.startActivity(i);
                return;
            } catch (Throwable ignored) {}

            // 3) Last fallback
            try {
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(i);
            } catch (Throwable ignored2) {}
        }
    }
}

// Παππού Γιώργο δώσε μου το επόμενο αρχείο να το κάνω Foldable Ready (Fully Integrated).
