// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELDualPaneManager.java — GEL PATCH v4.1 (Legacy static stubs)
// NOTE: Full-file patch — πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο σου.

package com.gel.cleaner.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.LayoutRes;

import com.gel.cleaner.GELFoldableOrchestrator;
import com.gel.cleaner.base.GELFoldableCallback.Posture;

public class GELDualPaneManager {

    private static final String TAG = "GELDualPaneManager";

    private final Activity activity;

    @LayoutRes
    private final int phoneLayout;
    @LayoutRes
    private final int tabletLayout;

    private boolean lastTablet = false;

    public GELDualPaneManager(Activity act,
                              @LayoutRes int phoneLayout,
                              @LayoutRes int tabletLayout) {

        this.activity = act;
        this.phoneLayout = phoneLayout;
        this.tabletLayout = tabletLayout;

        try {
            GELFoldableOrchestrator.registerDualPaneManager(act, this);
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // LEGACY STATIC STUBS (fixes old callers)
    // ============================================================
    public static void openSide(Context ctx, Intent i) {
        try {
            if (ctx != null && i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }
        } catch (Throwable ignore) {}
    }

    public static void dispatchMode(Context ctx, boolean isInner) {
        // legacy no-op — new flow uses instance applyDualPane()
        Log.d(TAG, "dispatchMode(legacy) inner=" + isInner);
    }

    // ============================================================
    public void applyDualPane(boolean tabletMode) {

        if (tabletMode == lastTablet) {
            Log.d(TAG, "DualPane: no change → skip.");
            return;
        }

        lastTablet = tabletMode;

        activity.runOnUiThread(() -> {
            try {
                activity.setContentView(
                        tabletMode ? tabletLayout : phoneLayout
                );

                try {
                    GELFoldableUIManager mgr =
                            GELFoldableOrchestrator.getUiManager(activity);
                    if (mgr != null) mgr.applyUI(tabletMode);
                } catch (Throwable ignore) {}

                try {
                    if (tabletMode)
                        GELFoldableAnimationPack.animateExpand(activity);
                    else
                        GELFoldableAnimationPack.animateCollapse(activity);
                } catch (Throwable ignore) {}

                Log.d(TAG, "DualPane: applied → " +
                        (tabletMode ? "TABLET MODE" : "PHONE MODE"));

            } catch (Exception e) {
                Log.e(TAG, "DualPane: swap error", e);
            }
        });
    }

    // SAFE STUBS — Orchestrator compatibility
    public void onPostureChanged(Posture posture) { }

    public void onScreenChanged(boolean isInner) {
        applyDualPane(isInner);
    }

    public boolean isTabletMode() { return lastTablet; }
}
