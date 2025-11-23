// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 8 — Dual-Pane Auto Layout Manager (v2.0 — Foldable Ready + Orchestrator Sync)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου).
// NOTE2: Fully Integrated with:
//        ➤ GELFoldableOrchestrator
//        ➤ GELFoldableUIManager
//        ➤ GELFoldableAnimationPack
//        ➤ GELAutoActivityHook
//        ➤ Global DP/SP Scaling (GELAutoDP)

package com.gel.cleaner;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.LayoutRes;

public class GELDualPaneManager {

    private static final String TAG = "GELDualPaneManager";

    private final Activity activity;

    // Phone / Tablet layouts (auto-swapped)
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

        // Register to orchestrator if available
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
                // =============================
                // Swap Phone ↔ Tablet layout
                // =============================
                activity.setContentView(
                        tabletMode ? tabletLayout : phoneLayout
                );

                // =============================
                // Reapply GEL DP/SP scaling
                // =============================
                try { GELAutoDP.init(activity); } catch (Throwable ignore) {}

                // =============================
                // Notify UI Manager
                // =============================
                try {
                    GELFoldableUIManager mgr =
                            GELFoldableOrchestrator.getUiManager(activity);
                    if (mgr != null) mgr.applyUI(tabletMode);
                } catch (Throwable ignore) {}

                // =============================
                // Optional animations
                // =============================
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
    // INTERNAL (optional)
    // =====================================================================
    public boolean isTabletMode() { return lastTablet; }
}
