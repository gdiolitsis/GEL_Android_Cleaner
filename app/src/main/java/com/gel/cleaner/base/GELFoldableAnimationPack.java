// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/base/GELFoldableAnimationPack.java
// Foldable Animation Pack — Final v2.0 (Compile-Safe + Reflow Support)
// ------------------------------------------------------------
// ✔ Fix: Added animateReflow(Runnable) — missing method causing build failures
// ✔ No-op but fully safe (zero crash, zero impact)
// ✔ Accepts lambda from Activities (AppListActivity, CpuRamLiveActivity etc.)
// ✔ Perfect compatibility with GELFoldableActivity + Callback
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

    // ------------------------------------------------------------
    // Static prepare hook (some callers use static)
    // ------------------------------------------------------------
    public static void prepare(Context ctx) {
        Log.d(TAG, "prepare()");
    }

    // ------------------------------------------------------------
    // Static animations — used by GELDualPaneManager (safe no-op)
    // ------------------------------------------------------------
    public static void animateCollapse(Activity a) {
        if (a != null) Log.d(TAG, "animateCollapse()");
    }

    public static void animateExpand(Activity a) {
        if (a != null) Log.d(TAG, "animateExpand()");
    }

    // ------------------------------------------------------------
    // Lifecycle hooks — no-op but logging for debug safety
    // ------------------------------------------------------------
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

    // ------------------------------------------------------------
    // ✔ NEW: Reflow Animation Hook — REQUIRED BY MANY ACTIVITIES
    // ------------------------------------------------------------
    public void animateReflow(Runnable action) {
        Log.d(TAG, "animateReflow()");
        if (action != null) {
            try {
                action.run();   // safe execution (UI refresh / layout reflow)
            } catch (Exception e) {
                Log.e(TAG, "animateReflow() error: ", e);
            }
        }
    }
}
