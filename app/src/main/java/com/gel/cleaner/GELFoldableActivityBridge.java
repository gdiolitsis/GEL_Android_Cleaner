// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 4 — Foldable Activity Bridge (v2.0 PRO)
// Fully Integrated: Orchestrator + DualPane + UIManager + AutoDP
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

public class GELFoldableActivityBridge implements GELFoldableCallback {

    private static final String TAG = "GELFoldBridge";

    private final Activity activity;
    private final GELFoldableDetector detector;
    private final GELFoldableUIManager uiManager;
    private GELDualPaneManager dualPane;   // optional, auto-linked

    private boolean lastInner = false;

    // =====================================================================
    // CONSTRUCTOR — auto-registers to the Orchestrator
    // =====================================================================
    public GELFoldableActivityBridge(@NonNull Activity act) {
        this.activity = act;

        this.uiManager = new GELFoldableUIManager(act);
        this.detector  = new GELFoldableDetector(act, this);

        try {
            GELFoldableOrchestrator.registerBridge(act, this);
        } catch (Throwable ignore) {}
    }

    // =====================================================================
    // OPTIONAL — attach dual-pane manager
    // =====================================================================
    public void attachDualPane(GELDualPaneManager dp) {
        this.dualPane = dp;
    }

    // =====================================================================
    // LIFECYCLE (call these from Activity)
    // =====================================================================
    public void onResume() {
        try { detector.start(); } catch (Throwable ignore) {}
    }

    public void onPause() {
        try { detector.stop(); } catch (Throwable ignore) {}
    }

    // =====================================================================
    // CALLBACKS — From Detector → Orchestrator → Bridge
    // =====================================================================

    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // Debug info
        Log.d(TAG, "Posture: " + posture.toString());
    }

    @Override
    public void onScreenChanged(boolean isInnerScreen) {
        if (isInnerScreen == lastInner) return;
        lastInner = isInnerScreen;

        Log.d(TAG, "ScreenChanged → inner=" + isInnerScreen);

        activity.runOnUiThread(() -> {
            // 1) Apply UI rules
            try { uiManager.applyUI(isInnerScreen); } catch (Throwable ignore) {}

            // 2) Apply Dual-Pane mode if attached
            if (dualPane != null) {
                try { dualPane.applyDualPane(isInnerScreen); } catch (Throwable ignore) {}
            }

            // 3) Re-init scaling (safe)
            try { GELAutoDP.init(activity); } catch (Throwable ignore) {}
        });
    }

    // =====================================================================
    // HELPERS
    // =====================================================================
    public boolean isInner() {
        return lastInner;
    }
}
