// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/base/GELFoldableAnimationPack.java
// STEP 6 — GELFoldableAnimationPack v1.2 (Backward Compatible + Fully Integrated)
// ------------------------------------------------------------
// ✔ Keeps v1.1 animateReflow()
// ✔ Adds legacy APIs used by Activities / Orchestrator
// ✔ All legacy calls are NO-OP safe or light-safe animations
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

public class GELFoldableAnimationPack {

    private static final String TAG = "GELFoldAnim";

    private final Activity activity;
    private final long durationMs;

    public GELFoldableAnimationPack(@NonNull Activity act) {
        this(act, 240); // default duration
    }

    public GELFoldableAnimationPack(@NonNull Activity act, long durationMs) {
        this.activity = act;
        this.durationMs = Math.max(120, durationMs);
    }

    // ============================================================
    // NEW CORE API (v1.1+)
    // ============================================================

    /**
     * Animate UI reflow with GEL-smooth hinge transition.
     */
    public void animateReflow(@NonNull Runnable reflowAction) {

        View root = activity.findViewById(android.R.id.content);

        if (!(root instanceof ViewGroup)) {
            reflowAction.run();
            return;
        }

        ViewGroup vg = (ViewGroup) root;

        // 1) Delayed Transition (bounds changes + minor fades)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AutoTransition t = new AutoTransition();
            t.setDuration(durationMs);
            t.setInterpolator(new DecelerateInterpolator());
            TransitionManager.beginDelayedTransition(vg, t);
        }

        // 2) Pre-scale shrink — absorb hinge jump
        preScale(vg, 0.985f);

        // 3) Execute UI reflow
        reflowAction.run();

        // 4) Restore scale smoothly
        postScale(vg, 1.0f);
    }

    // ============================================================
    // LEGACY / STUB-SAFE API (Activities still call these)
    // ============================================================

    // Static prepare hook (some callers use static)
    public static void prepare(Context ctx) {
        Log.d(TAG, "prepare()");
    }

    // Old Orchestrator / DualPane calls
    public static void animateCollapse(Activity a) {
        if (a != null) Log.d(TAG, "animateCollapse()");
    }

    public static void animateExpand(Activity a) {
        if (a != null) Log.d(TAG, "animateExpand()");
    }

    // Old Activity calls: animPack.onPostureChanged(posture)
    public void onPostureChanged(@NonNull Posture posture) {
        // Lightweight pulse / no-op safe
        applyHingePulse(posture);
    }

    // Compatibility alias used by older patches
    public void onPosture(@NonNull Posture posture) {
        onPostureChanged(posture);
    }

    // Old calls: applyHingePulse(GELFoldablePosture/ Posture)
    public void applyHingePulse(@NonNull Posture posture) {
        // safe micro-animation (or no-op if root not found)
        try {
            View root = activity.findViewById(android.R.id.content);
            if (root == null) return;
            root.animate()
                    .scaleX(0.995f)
                    .scaleY(0.995f)
                    .setDuration(80)
                    .withEndAction(() -> root.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(120).start())
                    .start();
        } catch (Throwable ignore) {}
    }

    // Old list fade helper
    public void applyListItemFade(@NonNull View row) {
        try {
            row.setAlpha(0f);
            row.animate().alpha(1f).setDuration(180).start();
        } catch (Throwable ignore) {}
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

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================

    private void preScale(ViewGroup root, float scale) {
        try {
            root.setPivotX(root.getWidth() / 2f);
            root.setPivotY(root.getHeight() / 2f);

            root.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .alpha(0.98f)
                    .setDuration(durationMs / 3)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        } catch (Throwable ignore) {}

        softenChildren(root, scale);
    }

    private void postScale(ViewGroup root, float scale) {
        try {
            root.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .alpha(1f)
                    .setDuration(durationMs)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        } catch (Throwable ignore) {}

        softenChildren(root, scale);
    }

    private void softenChildren(ViewGroup vg, float scale) {
        if (vg == null) return;
        int n = vg.getChildCount();

        for (int i = 0; i < n; i++) {
            View c = vg.getChildAt(i);
            if (c == null) continue;

            try {
                c.setPivotX(c.getWidth() / 2f);
                c.setPivotY(c.getHeight() / 2f);
                c.setScaleX(scale);
                c.setScaleY(scale);
            } catch (Throwable ignore) {}

            if (c instanceof ViewGroup)
                softenChildren((ViewGroup) c, scale);
        }
    }
}
