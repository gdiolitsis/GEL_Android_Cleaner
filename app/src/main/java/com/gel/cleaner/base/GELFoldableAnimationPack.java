// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableAnimationPack — Final v2.3 (Context-Safe + Posture Import FIX)
// ------------------------------------------------------------
// ✔ FIX: Added missing Posture import (build blocker)
// ✔ Context constructor (adapters)
// ✔ applyListItemFade stub
// ✔ animateReflow stable
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

// REQUIRED — FIX for build:
import com.gel.cleaner.base.GELFoldableCallback.Posture;

public class GELFoldableAnimationPack {

    private static final String TAG = "GEL.AnimPack";

    private final Activity activity; // may be null

    public GELFoldableAnimationPack(Activity a) {
        this.activity = a;
    }

    // Context ctor (for adapters) — safe
    public GELFoldableAnimationPack(Context ctx) {
        this.activity = (ctx instanceof Activity) ? (Activity) ctx : null;
    }

    public static void prepare(Context ctx) { Log.d(TAG, "prepare()"); }

    public static void animateCollapse(Activity a) {
        if (a != null) Log.d(TAG, "animateCollapse()");
    }

    public static void animateExpand(Activity a) {
        if (a != null) Log.d(TAG, "animateExpand()");
    }

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

    // REQUIRED by Activities
    public void animateReflow(Runnable action) {
        Log.d(TAG, "animateReflow()");
        if (action != null) {
            try { action.run(); } catch (Throwable e) {
                Log.e(TAG, "animateReflow error", e);
            }
        }
    }

    // REQUIRED by AppListAdapter
    public void applyListItemFade(View v) {
        if (v != null) Log.d(TAG, "applyListItemFade()");
    }
}
