// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableAnimationPack — Final v2.2 (Context-Safe + List Fade Stub)
// ------------------------------------------------------------
// ✔ Added Context constructor (used by adapters)
// ✔ Added applyListItemFade(View) stub
// ✔ animateReflow(...) kept
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

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
        // safe no-op (keeps behavior stable)
        if (v != null) Log.d(TAG, "applyListItemFade()");
    }
}
