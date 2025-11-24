// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 8 — Dual-Pane Auto Layout Manager (v4.0 FULL — Foldable Ready + Orchestrator Sync)
// ✔ Fixed packages/imports to match FULL Foldable System
// ✔ Removed dead GELAutoDP references (class doesn't exist)
// ✔ Added safe Orchestrator callback stubs
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου).

package com.gel.cleaner.base;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.LayoutRes;

import com.gel.cleaner.GELFoldableOrchestrator;
import com.gel.cleaner.GELFoldableUIManager;
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

    // =====================================================================
    // PUBLIC — CALLED BY ORCHESTRATOR WHEN POSTURE / SCREEN CHANGES
    // =====================================================================
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

                // Notify UI Manager (re-apply rules on swapped layout)
                try {
                    GELFoldableUIManager mgr =
                            GELFoldableOrchestrator.getUiManager(activity);
                    if (mgr != null) mgr.applyUI(tabletMode);
                } catch (Throwable ignore) {}

                // Optional animations
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

    // =====================================================================
    // SAFE STUBS — Orchestrator compatibility
    // =====================================================================
    public void onPostureChanged(Posture posture) {
        // No-op. Orchestrator decides tabletMode and calls applyDualPane().
    }

    public void onScreenChanged(boolean isInner) {
        applyDualPane(isInner);
    }

    // =====================================================================
    public boolean isTabletMode() { return lastTablet; }
}
