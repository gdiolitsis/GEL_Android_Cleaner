// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 4 — Foldable Activity Bridge (Universal Plug-in)

package com.gel.cleaner;

import android.app.Activity;
import androidx.annotation.NonNull;

public class GELFoldableActivityBridge implements GELFoldableCallback {

    private final Activity activity;
    private final GELFoldableDetector detector;
    private final GELFoldableUIManager uiManager;

    public GELFoldableActivityBridge(@NonNull Activity act) {
        this.activity = act;
        this.uiManager = new GELFoldableUIManager(act);
        this.detector = new GELFoldableDetector(act, this);
    }

    // =========================================================
    // LIFECYCLE HOOKS (call these from the Activity)
    // =========================================================
    public void onResume() {
        detector.start();
    }

    public void onPause() {
        detector.stop();
    }

    // =========================================================
    // CALLBACK FROM POSTURE CHANGES
    // =========================================================
    @Override
    public void onPostureChanged(boolean isUnfolded, float hingeAngle) {

        boolean isInnerScreen = false;

        // Logic: if big angle → open → inner display
        if (isUnfolded || hingeAngle > 140f) {
            isInnerScreen = true;
        }

        // Inform UI manager
        uiManager.applyUI(isInnerScreen);
    }
}
