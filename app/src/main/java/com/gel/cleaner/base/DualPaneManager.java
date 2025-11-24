// GDiolitsis Engine Lab (GEL) — Author & Developer
// DualPaneManager v3.0 — Final Unified Edition
// ------------------------------------------------------------
// ✔ Single file (no GELDualPaneManager needed)
// ✔ Supports ALL legacy calls:
//       • dispatchMode(isInner)
//       • prepareIfSupported(context)
//       • isDualPaneActive(context)
//       • openSide(context, intent)
// ✔ Zero-crash fallbacks (no tablet layouts required)
// ✔ Fully compatible with GELFoldableDetector / UIManager / AnimationPack
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

    // =====================================================================
    // STATIC SAFE HELPERS (used by Activities + Diagnostics)
    // =====================================================================

    /** Always safe — simply logs support */
    public static void prepareIfSupported(Context ctx) {
        Log.d(TAG, "prepareIfSupported() — no-op safe.");
    }

    /** Return false always (no tablet split unless Activity handles it) */
    public static boolean isDualPaneActive(Context ctx) {
        return false; // safe default
    }

    /** Always safe — fallback to normal activity launch */
    public static void openSide(Context ctx, Intent i) {
        try {
            if (ctx == null || i == null) return;
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Throwable ignore) {
        }
    }

    // =====================================================================
    // INSTANCE API (Activities still call these)
    // =====================================================================

    /** Legacy API → simply triggers prepare */
    public void dispatchMode(boolean isInner) {
        try {
            prepareIfSupported(ctx);
        } catch (Throwable ignore) {}
    }

    // Optional lifecycle hooks (safe no-op)
    public void onCreate() {}
    public void onResume() {}
    public void onPause() {}
    public void onConfigurationChanged(android.content.res.Configuration c) {}
    public void onMultiWindowModeChanged(boolean inMultiWindow) {}
    public void onScreenChanged(boolean isInner) {}
    public void onPostureChanged(Posture posture) {}
}
