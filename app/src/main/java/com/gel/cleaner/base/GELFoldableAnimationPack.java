// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/base/GELFoldableAnimationPack.java
// Foldable Animation Pack — Safe Stub v1.0
// ------------------------------------------------------------
// ✔ Exists in base package (fixes "cannot find symbol")
// ✔ No-op animations → no behavioral risk
// ✔ API hooks used by Orchestrator/DualPaneManager
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class GELFoldableAnimationPack {

    private static final String TAG = "GEL.AnimPack";

    private final Activity activity;

    public GELFoldableAnimationPack(Activity a) {
        this.activity = a;
    }

    // Static prepare hook (some callers use static)
    public static void prepare(Context ctx) {
        // no-op, safe
        Log.d(TAG, "prepare()");
    }

    // Called from GELDualPaneManager
    public static void animateCollapse(Activity a) {
        // no-op, safe
        if (a != null) Log.d(TAG, "animateCollapse()");
    }

    public static void animateExpand(Activity a) {
        // no-op, safe
        if (a != null) Log.d(TAG, "animateExpand()");
    }

    // Optional lifecycle hooks (reflection-safe)
    public void onCreate() { Log.d(TAG, "onCreate()"); }
    public void onResume() { Log.d(TAG, "onResume()"); }
    public void onPause()  { Log.d(TAG, "onPause()"); }

    public void onConfigurationChanged(android.content.res.Configuration c) {
        Log.d(TAG, "onConfigurationChanged()");
    }

    public void onMultiWindowModeChanged(boolean inMultiWindow) {
        Log.d(TAG, "onMultiWindowModeChanged(" + inMultiWindow + ")");
    }

    public void onScreenChanged(boolean isInner) {
        Log.d(TAG, "onScreenChanged(" + isInner + ")");
    }

    public void onPostureChanged(Posture posture) {
        Log.d(TAG, "onPostureChanged(" + posture + ")");
    }
}
