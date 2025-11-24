// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableOrchestrator — v1.0 + Compatibility Patch v1.3
// ------------------------------------------------------------
// ✔ Adds missing static APIs:
//      • initIfPossible(Context)
//      • isFoldableSupported(Context)
//      • getCurrentPostureName()
// ✔ Full compatibility with GELFoldablePosture + Diagnostics
// ✔ Safe fallbacks (no crashes on non-foldables)
// ✔ Keeps original behavior 100% intact
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.app.Activity;
import android.content.Context;
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

    // Last known posture for Diagnostics
    private static Posture lastStaticPosture = Posture.UNKNOWN;

    public GELFoldableOrchestrator(@NonNull Activity activity) {
        this.activity = activity;
    }

    // =====================================================================
    // STATIC PATCH — Diagnostics Support
    // =====================================================================

    public static void initIfPossible(Context ctx) {
        // Nothing to init globally — safe stub
        Log.d(TAG, "initIfPossible()");
    }

    public static boolean isFoldableSupported(Context ctx) {
        try {
            // Device supports hinge angle sensor?
            android.hardware.SensorManager sm =
                    (android.hardware.SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

            return sm != null &&
                   sm.getDefaultSensor(android.hardware.Sensor.TYPE_HINGE_ANGLE) != null;
        } catch (Throwable e) {
            return false;
        }
    }

    public static String getCurrentPostureName() {
        return lastStaticPosture.toString();
    }

    // =====================================================================
    // START (call from Activity.onCreate)
    // =====================================================================
    public void start() {
        if (initialized) return;

        uiManager = new GELFoldableUIManager(activity);
        animator  = new GELFoldableAnimationPack(activity);

        detector = new GELFoldableDetector(activity, this);
        detector.start();

        initialized = true;
        Log.d(TAG, "Foldable Orchestrator started.");
    }

    // =====================================================================
    // STOP
    // =====================================================================
    public void stop() {
        try {
            if (detector != null) detector.stop();
        } catch (Exception e) {
            Log.e(TAG, "Stop error", e);
        }
    }

    // =====================================================================
    // CALLBACK from GELFoldableDetector
    // =====================================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {

        lastStaticPosture = posture;

        Log.d(TAG, "Posture changed → " + posture);

        boolean isInner = isBigScreen(posture);

        if (isInner == lastInnerState) {
            Log.d(TAG, "Duplicate posture event → ignored.");
            return;
        }

        lastInnerState = isInner;

        animator.animateReflow(() -> uiManager.applyUI(isInner));
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        // Orchestrator uses posture only — ignore
    }

    // =====================================================================
    // Decide inner/outer UI based on posture
    // =====================================================================
    private boolean isBigScreen(Posture p) {
        switch (p) {
            case FLAT:         // fully 180° open
            case HALF_OPEN:    // book/laptop mode
            case TABLETOP:     // L-shaped
                return true;

            case CLOSED:       // folded shut
            case TENT:         // inverted V
            case UNKNOWN:
            default:
                return false;
        }
    }
}
