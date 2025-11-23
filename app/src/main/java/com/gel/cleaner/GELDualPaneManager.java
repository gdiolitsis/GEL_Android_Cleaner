// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 8 — Dual-Pane Auto Layout Manager (v1.0)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste.

package com.gel.cleaner;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;

public class GELDualPaneManager {

    private static final String TAG = "GELDualPaneManager";

    private final Activity activity;

    // Layouts που θα αλλάζουν ανάλογα με το fold state
    @LayoutRes
    private final int phoneLayout;
    @LayoutRes
    private final int tabletLayout;

    private boolean lastModeTablet = false;

    public GELDualPaneManager(Activity act,
                              @LayoutRes int phoneLayout,
                              @LayoutRes int tabletLayout) {

        this.activity = act;
        this.phoneLayout = phoneLayout;
        this.tabletLayout = tabletLayout;
    }

    /**
     * Καλείται από τον Orchestrator όταν αλλάζει posture
     */
    public void applyDualPane(boolean isTabletMode) {

        if (isTabletMode == lastModeTablet) {
            Log.d(TAG, "Dual-pane unchanged → skip.");
            return;
        }

        lastModeTablet = isTabletMode;

        activity.runOnUiThread(() -> {
            try {
                activity.setContentView(
                        isTabletMode ? tabletLayout : phoneLayout
                );
                Log.d(TAG, "Dual-pane applied → " +
                        (isTabletMode ? "TABLET MODE" : "PHONE MODE"));

            } catch (Exception e) {
                Log.e(TAG, "Dual-pane swap error", e);
            }
        });
    }
}
