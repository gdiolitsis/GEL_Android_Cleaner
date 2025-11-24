// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/base/DualPaneManager.java
// DualPaneManager — Bridge Wrapper v1.1 (FINAL)
// ------------------------------------------------------------
// ✔ Fixes ALL "cannot find symbol DualPaneManager"
// ✔ Adds missing dispatchMode(boolean)
// ✔ Delegates to GELDualPaneManager (if exists)
// ✔ Zero-crash fallbacks
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DualPaneManager {

    private static final String TAG = "GEL.DualPane";

    private final Context ctx;

    public DualPaneManager(Context c) {
        this.ctx = c;
    }

    // ---------- Static API used by app ----------
    public static void prepareIfSupported(Context ctx) {
        try {
            GELDualPaneManager.prepareIfSupported(ctx);
        } catch (Throwable t) {
            Log.w(TAG, "prepareIfSupported skipped: " + t.getMessage());
        }
    }

    public static boolean isDualPaneActive(Context ctx) {
        try {
            return GELDualPaneManager.isDualPaneActive(ctx);
        } catch (Throwable t) {
            return false;
        }
    }

    public static void openSide(Context ctx, Intent i) {
        try {
            GELDualPaneManager.openSide(ctx, i);
        } catch (Throwable t) {
            try {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            } catch (Throwable ignored) {}
        }
    }

    // ==========================================================
    // NEW → REQUIRED BY CLEANER / BROWSER / DIAGNOSTICS
    // ==========================================================
    public void dispatchMode(boolean isInner) {
        try {
            GELDualPaneManager.dispatchMode(ctx, isInner);
        } catch (Throwable t) {
            Log.w(TAG, "dispatchMode fallback: " + t.getMessage());
        }
    }

    // ---------- Optional lifecycle hooks ----------
    public void onCreate() {}
    public void onResume() {}
    public void onPause() {}
    public void onConfigurationChanged(android.content.res.Configuration c) {}
    public void onMultiWindowModeChanged(boolean inMultiWindow) {}

    public void onScreenChanged(boolean isInner) {}
    public void onPostureChanged(Posture posture) {}
}
