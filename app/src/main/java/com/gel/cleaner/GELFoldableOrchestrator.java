// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/GELFoldableOrchestrator.java
// GELFoldableOrchestrator — FINAL Core v4.1 (Compile-Safe + Unified)
// ------------------------------------------------------------
// ✔ ZERO hard dependencies on optional foldable classes (UIManager / DualPane / Bridge)
// ✔ Works whether UIManager lives in:
//      • com.gel.cleaner.GELFoldableUIManager  (cleaner)
//      • com.gel.cleaner.base.GELFoldableUIManager (base)
// ✔ registerBridge / registerDualPaneManager accept Object (keeps old calls compiling)
// ✔ Posture mapping supports BOTH vocabularies (FLAT/TABLETOP + FULLY_OPEN/TABLE_MODE)
// ✔ Safe no-op on non-foldables
// ------------------------------------------------------------
// NOTE TO GIORGOS: πάντα δουλεύουμε πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο που έχει σταλεί.

package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gel.cleaner.base.GELFoldableAnimationPack;
import com.gel.cleaner.base.GELFoldableCallback;
import com.gel.cleaner.base.GELFoldableDetector;
import com.gel.cleaner.base.GELFoldableCallback.Posture;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
    private Object uiManager; // reflection-safe (can be in cleaner or base)
    private GELFoldableAnimationPack animator;

    // Optional components — NO hard types (compile-safe)
    private Object dualPaneManager; // can be DualPaneManager or GELDualPaneManager
    private Object bridge;          // can be GELFoldableActivityBridge

    private boolean lastInnerState = false;
    private boolean initialized = false;

    public GELFoldableOrchestrator(@NonNull Activity activity) {
        this.activity = activity;
    }

    // =====================================================================
    // STATIC COMPATIBILITY APIS (called from Activities / Managers)
    // =====================================================================

    public static void initIfPossible(Context ctx) {
        // no-op, foldable init happens per-activity
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
    }

    public static void unregister(@NonNull Activity act, @NonNull GELFoldableCallback cb) {
        GELFoldableOrchestrator orch = get(act);
        if (orch != null) {
            Log.d(TAG, "unregister(activity, callback)");
            orch.stop();
        }
    }

    // Accept Object to avoid missing-class errors.
    public static void registerBridge(@NonNull Activity act, @NonNull Object b) {
        GELFoldableOrchestrator orch = getOrCreate(act);
        orch.bridge = b;
        orch.start();
        Log.d(TAG, "registerBridge()");
    }

    // Accept Object to avoid missing-class errors.
    public static void registerDualPaneManager(@NonNull Activity act, @NonNull Object dpm) {
        GELFoldableOrchestrator orch = getOrCreate(act);
        orch.dualPaneManager = dpm;
        orch.start();
        Log.d(TAG, "registerDualPaneManager()");
    }

    // Return Object (caller may cast if it knows the type).
    public static Object getUiManager(@NonNull Activity act) {
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

        // UIManager can be in two packages. Try both.
        uiManager = tryNew(
                "com.gel.cleaner.GELFoldableUIManager",
                new Class[]{Activity.class},
                new Object[]{activity}
        );
        if (uiManager == null) {
            uiManager = tryNew(
                    "com.gel.cleaner.base.GELFoldableUIManager",
                    new Class[]{Activity.class},
                    new Object[]{activity}
            );
        }

        animator = new GELFoldableAnimationPack(activity);

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
            animator.animateReflow(() -> safeCall(uiManager, "applyUI",
                    new Class[]{boolean.class}, new Object[]{isInner}));
        } else if (uiManager != null) {
            safeCall(uiManager, "applyUI",
                    new Class[]{boolean.class}, new Object[]{isInner});
        }

        // notify optional components (safe no-op if null)
        safeCall(dualPaneManager, "onPostureChanged",
                new Class[]{Posture.class}, new Object[]{posture});
        safeCall(bridge, "onPostureChanged",
                new Class[]{Posture.class}, new Object[]{posture});
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner == lastInnerState) return;
        lastInnerState = isInner;

        if (animator != null && uiManager != null) {
            animator.animateReflow(() -> safeCall(uiManager, "applyUI",
                    new Class[]{boolean.class}, new Object[]{isInner}));
        } else if (uiManager != null) {
            safeCall(uiManager, "applyUI",
                    new Class[]{boolean.class}, new Object[]{isInner});
        }

        safeCall(dualPaneManager, "onScreenChanged",
                new Class[]{boolean.class}, new Object[]{isInner});
        safeCall(bridge, "onScreenChanged",
                new Class[]{boolean.class}, new Object[]{isInner});
    }

    // =====================================================================
    // Decide inner/outer UI based on posture (supports old+new enum names)
    // =====================================================================
    private boolean isBigScreen(Posture p) {
        if (p == null) return false;
        String n = p.name();

        // New vocabulary (legacy code may still reference these names)
        if ("INNER_SCREEN".equals(n) ||
            "TABLE_MODE".equals(n) ||
            "FULLY_OPEN".equals(n)) return true;

        // Unified vocabulary (current GELFoldableCallback v1.2)
        if ("FLAT".equals(n) ||
            "TABLETOP".equals(n) ||
            "HALF_OPEN".equals(n) ||
            "HALF_OPENED".equals(n)) return true;

        return false;
    }

    // =====================================================================
    // REFLECTION UTILITIES (internal)
    // =====================================================================
    private Object tryNew(String name, Class<?>[] sig, Object[] args) {
        try {
            Class<?> c = Class.forName(name);
            Constructor<?> ctor = c.getConstructor(sig);
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (Throwable ignore) {
            try {
                Class<?> c = Class.forName(name);
                Constructor<?> ctor = c.getDeclaredConstructor();
                ctor.setAccessible(true);
                return ctor.newInstance();
            } catch (Throwable ignored) {
                return null;
            }
        }
    }

    private void safeCall(Object target, String method) {
        safeCall(target, method, null, null);
    }

    private void safeCall(Object target, String method,
                          Class<?>[] sig, Object[] args) {
        if (target == null || method == null) return;
        try {
            Method m;
            if (sig == null) {
                m = target.getClass().getMethod(method);
                m.setAccessible(true);
                m.invoke(target);
            } else {
                m = target.getClass().getMethod(method, sig);
                m.setAccessible(true);
                m.invoke(target, args);
            }
        } catch (Throwable ignored) {}
    }
}
