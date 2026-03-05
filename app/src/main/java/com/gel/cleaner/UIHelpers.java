package com.gel.cleaner;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class UIHelpers {

    // ============================================================
    // SINGLE VIEW PRESS EFFECT
    // ============================================================
    public static void applyPressEffect(View v) {

        if (v == null) return;

        v.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    view.animate()
                            .scaleX(0.97f)
                            .scaleY(0.97f)
                            .setDuration(80)
                            .start();
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .start();
                    break;
            }

            return false;
        });
    }

    public static View.OnTouchListener pressEffect() {

    return (v, event) -> {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setAlpha(0.6f);
            v.setScaleX(0.97f);
            v.setScaleY(0.97f);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP ||
                 event.getAction() == MotionEvent.ACTION_CANCEL) {

            v.setAlpha(1f);
            v.setScaleX(1f);
            v.setScaleY(1f);
        }

        return false;
    };
}

    // ============================================================
    // RECURSIVE PRESS EFFECT FOR WHOLE LAYOUT
    // ============================================================
    public static void applyPressEffectRecursive(View root) {

        if (root == null) return;

        if (root.isClickable()) {
            applyPressEffect(root);
        }

        if (root instanceof ViewGroup) {

            ViewGroup vg = (ViewGroup) root;

            for (int i = 0; i < vg.getChildCount(); i++) {
                applyPressEffectRecursive(vg.getChildAt(i));
            }
        }
    }
}
