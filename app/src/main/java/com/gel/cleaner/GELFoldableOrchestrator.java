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

    // last-known state to avoid duplicate UI refreshes
    private boolean lastInnerState = false;
    private boolean initialized = false;

    public GELFoldableOrchestrator(@NonNull Activity activity) {
        this.activity = activity;
    }

    /**
     * Must be called from Activity.onCreate()
     */
    public void start() {
        if (initialized) return;

        uiManager = new GELFoldableUIManager(activity);
        animator  = new GELFoldableAnimationPack(activity);

        detector = new GELFoldableDetector(activity, this);
        detector.start();

        initialized = true;
        Log.d(TAG, "Foldable Orchestrator started.");
    }

    /**
     * Must be called from Activity.onDestroy()
     */
    public void stop() {
        if (detector != null) detector.stop();
    }

    // ----------------------------------------------------
    // CALLBACK from GELFoldableDetector
    // ----------------------------------------------------
    @Override
    public void onPostureChanged(GELFoldableCallback.Posture posture) {

        Log.d(TAG, "Posture changed → " + posture.name());

        boolean isInner = isBigScreen(posture);

        if (isInner == lastInnerState) {
            Log.d(TAG, "Ignoring duplicate posture event.");
            return;
        }

        lastInnerState = isInner;

        // Animate → apply UI scaling
        animator.animateReflow(() -> uiManager.applyUI(isInner));
    }

    // ----------------------------------------------------
    // Logic: When is the device considered “inner screen”?
    // ----------------------------------------------------
    private boolean isBigScreen(GELFoldableCallback.Posture p) {

        switch (p) {
            case FLAT:
            case HALF_OPEN:
            case TABLETOP:
                return true;    // unfolded / semi-open → inner screen UI

            case CLOSED:
            case TENT:
            default:
                return false;   // outer screen / folded
        }
    }

}
