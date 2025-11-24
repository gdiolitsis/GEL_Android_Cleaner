// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 7 — GELFoldableOrchestrator v1.0 (Final GEL-Ready Edition)
// Master controller: Detector → Callback → AnimationPack → UI Manager
// NOTE: Ολόκληρο αρχείο, 100% έτοιμο για copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner.base;

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
        try {
            if (detector != null) detector.stop();
        } catch (Exception e) {
            Log.e(TAG, "Stop error", e);
        }
    }

    // ============================================================
    // CALLBACK from GELFoldableDetector
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {

        Log.d(TAG, "Posture changed → " + posture);

        boolean isInner = isBigScreen(posture);

        if (isInner == lastInnerState) {
            Log.d(TAG, "Duplicate posture event → ignored.");
            return;
        }

        lastInnerState = isInner;

        // Smooth animation + apply UI transition
        animator.animateReflow(() -> uiManager.applyUI(isInner));
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        // Orchestrator uses only posture → ignore direct screen callbacks
    }

    // ============================================================
    // Decide inner/outer UI based on posture
    // ============================================================
    private boolean isBigScreen(Posture p) {
        switch (p) {
            case FLAT:       // fully 180° open
            case HALF_OPEN:  // book/laptop mode
            case TABLETOP:   // L-shaped on desk
                return true;

            case CLOSED:     // folded shut — cover display
            case TENT:       // tent mode
            case UNKNOWN:
            default:
                return false;
        }
    }
}

