// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/GELFoldableOrchestrator.java
// GELFoldableOrchestrator — Final Foldable Core v4.0 (Full Compatibility)
// ------------------------------------------------------------
// ✔ Correct package (com.gel.cleaner) for all Activities imports
// ✔ Adds ALL missing static APIs used across project:
//      • register(Activity, GELFoldableCallback)
//      • unregister(Activity, GELFoldableCallback)
//      • registerBridge(Activity, GELFoldableActivityBridge)
//      • registerDualPaneManager(Activity, GELDualPaneManager)
//      • getUiManager(Activity)
//      • initIfPossible(Context)
//      • isFoldableSupported(Context)
//      • getCurrentPostureName()
// ✔ Safe no-op fallbacks (zero crash on non-foldables)
// ✔ Works with BOTH Posture vocabularies (INNER_SCREEN / FULLY_OPEN etc. + FLAT / TABLETOP etc.)
// ------------------------------------------------------------
// NOTE TO GIORGOS: πάντα δουλεύουμε πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο που έχει σταλεί.

package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gel.cleaner.base.GELDualPaneManager;
import com.gel.cleaner.base.GELFoldableActivityBridge;
import com.gel.cleaner.base.GELFoldableAnimationPack;
import com.gel.cleaner.base.GELFoldableCallback;
import com.gel.cleaner.base.GELFoldableDetector;
import com.gel.cleaner.base.GELFoldableCallback.Posture;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class GELFoldableOrchestrator implements GELFoldableCallback {

    private static final String TAG = "GELFoldOrchestrator";

    // Per-Activity registry (weak to avoid leaks)
    private static final Map<Activity, WeakReference<GELFoldableOrchestrator>> REGISTRY =
            new WeakHashMap<>();

    // Last known posture for diagnostics
    private static Posture lastStaticPosture = Posture.UNKNOWN;

    private final Activity activity;

    private GELFoldableDetector detector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animator;

    private GELDualPaneManager dualPaneManager;
    private GELFoldableActivityBridge bridge;

    private boolean lastInnerState = false;
    private boolean initialized = false;

    public GELFoldableOrchestrator(@NonNull Activity activity) {
        this.activity = activity;
    }

    // =====================================================================
    // STATIC COMPATIBILITY APIS (called from Activities / Managers)
    // =====================================================================

    public static void initIfPossible(Context ctx) {
        Log.d(TAG, "initIfPossible()");
    }

    public static boolean isFoldableSupported(Context ctx) {
        try {
            android.hardware.SensorManager sm =
                    (android.hardware.SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            return sm != null &&
                    sm.getDefaultSensor(android.hardware.Sensor.TYPE_HINGE_ANGLE) != null;
        } catch (Throwable e) {
            return false;
        }
    }

    public static String getCurrentPostureName() {
        return lastStaticPosture != null ? lastStaticPosture.toString() : "UNKNOWN";
    }

    public static void register(@NonNull Activity act, @NonNull GELFoldableCallback cb) {
        GELFoldableOrchestrator orch = getOrCreate(act);
        orch.start();
        Log.d(TAG, "register(activity, callback)");
        // callbacks are routed through orchestrator; no extra list needed now
    }

    public static void unregister(@NonNull Activity act, @NonNull GELFoldableCallback cb) {
        GELFoldableOrchestrator orch = get(act);
        if (orch != null) {
            Log.d(TAG, "unregister(activity, callback)");
            orch.stop();
        }
    }

    public static void registerBridge(@NonNull Activity act, @NonNull GELFoldableActivityBridge b) {
        GELFoldableOrchestrator orch = getOrCreate(act);
        orch.bridge = b;
        orch.start();
        Log.d(TAG, "registerBridge()");
    }

    public static void registerDualPaneManager(@NonNull Activity act, @NonNull GELDualPaneManager dpm) {
        GELFoldableOrchestrator orch = getOrCreate(act);
        orch.dualPaneManager = dpm;
        orch.start();
        Log.d(TAG, "registerDualPaneManager()");
    }

    public static GELFoldableUIManager getUiManager(@NonNull Activity act) {
        GELFoldableOrchestrator orch = getOrCreate(act);
        orch.start();
        return orch.uiManager;
    }

    private static GELFoldableOrchestrator getOrCreate(@NonNull Activity act) {
        GELFoldableOrchestrator existing = get(act);
        if (existing != null) return existing;

        GELFoldableOrchestrator orch = new GELFoldableOrchestrator(act);
        REGISTRY.put(act, new WeakReference<>(orch));
        return orch;
    }

    private static GELFoldableOrchestrator get(@NonNull Activity act) {
        WeakReference<GELFoldableOrchestrator> ref = REGISTRY.get(act);
        return ref != null ? ref.get() : null;
    }

    // =====================================================================
    // START / STOP (instance)
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

    public void stop() {
        try {
            if (detector != null) detector.stop();
        } catch (Exception e) {
            Log.e(TAG, "Stop error", e);
        }
        initialized = false;
    }

    // =====================================================================
    // CALLBACKS from GELFoldableDetector
    // =====================================================================

    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        lastStaticPosture = posture;

        boolean isInner = isBigScreen(posture);

        if (isInner == lastInnerState) return;
        lastInnerState = isInner;

        if (animator != null && uiManager != null) {
            animator.animateReflow(() -> uiManager.applyUI(isInner));
        } else if (uiManager != null) {
            uiManager.applyUI(isInner);
        }

        // notify optional components (safe no-op if null)
        try { if (dualPaneManager != null) dualPaneManager.onPostureChanged(posture); } catch (Throwable ignore) {}
        try { if (bridge != null) bridge.onPostureChanged(posture); } catch (Throwable ignore) {}
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner == lastInnerState) return;
        lastInnerState = isInner;

        if (animator != null && uiManager != null) {
            animator.animateReflow(() -> uiManager.applyUI(isInner));
        } else if (uiManager != null) {
            uiManager.applyUI(isInner);
        }

        try { if (dualPaneManager != null) dualPaneManager.onScreenChanged(isInner); } catch (Throwable ignore) {}
        try { if (bridge != null) bridge.onScreenChanged(isInner); } catch (Throwable ignore) {}
    }

    // =====================================================================
    // Decide inner/outer UI based on posture (supports old+new enum names)
    // =====================================================================
    private boolean isBigScreen(Posture p) {
        if (p == null) return false;
        String n = p.name();

        // New vocabulary
        if ("INNER_SCREEN".equals(n) ||
            "TABLE_MODE".equals(n) ||
            "FULLY_OPEN".equals(n)) return true;

        // Old vocabulary
        if ("FLAT".equals(n) ||
            "HALF_OPEN".equals(n) ||
            "HALF_OPENED".equals(n) ||
            "TABLETOP".equals(n)) return true;

        return false;
    }
}
