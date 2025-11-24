// GDiolitsis Engine Lab (GEL) — Author & Developer
// DualPaneManager — GEL Dummy Safe v2.0
// ------------------------------------------------------------
// ✔ Removes ALL references to non-existent GELDualPaneManager
// ✔ Keeps API compatible with the rest of the project
// ✔ Zero-crash, zero-dependency, works everywhere
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

    // ---------------------------------------------------------
    // STATIC API (kept for compatibility)
    // ---------------------------------------------------------
    public static void prepareIfSupported(Context ctx) {
        Log.d(TAG, "prepareIfSupported(): no-op (DualPane not supported)");
    }

    public static boolean isDualPaneActive(Context ctx) {
        return false; // safe fallback
    }

    public static void openSide(Context ctx, Intent i) {
        try {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Throwable t) {
            Log.w(TAG, "openSide fallback failed: " + t.getMessage());
        }
    }

    // ---------------------------------------------------------
    // INSTANCE API
    // ---------------------------------------------------------
    public void dispatchMode(boolean isInner) {
        Log.d(TAG, "dispatchMode(" + isInner + "): no-op");
    }

    // Optional hooks (kept for orchestrator compatibility)
    public void onCreate() {}
    public void onResume() {}
    public void onPause() {}
    public void onConfigurationChanged(android.content.res.Configuration c) {}
    public void onMultiWindowModeChanged(boolean inMultiWindow) {}
    public void onScreenChanged(boolean isInner) {}
    public void onPostureChanged(Object posture) {}
}
