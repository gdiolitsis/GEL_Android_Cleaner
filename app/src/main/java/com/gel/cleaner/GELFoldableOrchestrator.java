// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 7 — GELFoldableOrchestrator v1.0
// Master controller that binds: Detector → Callback → UI Manager → Animations
// NOTE: Ολόκληρο αρχείο, 100% έτοιμο για copy-paste.

package com.gel.cleaner;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;

public class GELFoldableOrchestrator implements GELFoldableCallback {

    private static final String TAG = "GELFoldOrchestrator";

    private final Activity activity;
    private GELFoldableDetector detector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animator;

    private boolean lastInnerState = false;
    private boolean initialized = false;

    public GELFoldableOrchestrator(@NonNull Activity activity) {
        this.activity = activity;
    }

    // ============================================================
    // START (call from Activity.onCreate)
    // ============================================================
    public void start() {
        if (initialized) return;

        uiManager = new GELFoldableUIManager(activity);
        animator  = new GELFoldableAnimationPack(activity);

        detector = new GELFoldableDetector(activity, this);
        detector.start();

        initialized = true;
        Log.d(TAG, "Foldable Orchestrator started.");
    }

    // ============================================================
    // STOP (call from Activity.onDestroy)
    // ============================================================
    public void stop() {
        if (detector != null) detector.stop();
    }

    // ============================================================
    // CALLBACK from GELFoldableDetector
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull GELFoldableCallback.Posture posture) {
        Log.d(TAG, "Posture changed → " + posture.name());

        boolean isInner = isBigScreen(posture);

        if (isInner == lastInnerState) {
            Log.d(TAG, "Ignoring duplicate posture event.");
            return;
        }

        lastInnerState = isInner;

        // Smooth animation → change UI
        animator.animateReflow(() -> uiManager.applyUI(isInner));
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        // Not needed here — orchestration happens via posture logic
    }

    // ============================================================
    // Logic: Decide if inner display UI should activate
    // ============================================================
    private boolean isBigScreen(GELFoldableCallback.Posture p) {

        switch (p) {
            case FLAT:       // fully open
            case HALF_OPEN:  // laptop/book mode
            case TABLETOP:   // table L-shaped mode
                return true;

            case CLOSED:     // folded - outer display
            case TENT:       // tent mode
            case UNKNOWN:
            default:
                return false;
        }
    }
}
