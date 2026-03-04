// GDiolitsis Engine Lab (GEL) — Author & Developer
// UIHelpers.java — Global UI Utilities

package com.gel.cleaner;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class UIHelpers {

    // ============================================================
    // PRESS EFFECT (SINGLE VIEW)
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

    // ============================================================
    // PRESS EFFECT (RECURSIVE FOR ENTIRE LAYOUT)
    // ============================================================
    public static void applyPressEffectRecursive(View root) {

        if (root == null) return;

        try {

            if (root.isClickable()) {
                applyPressEffect(root);
            }

            if (root instanceof ViewGroup) {

                ViewGroup vg = (ViewGroup) root;

                for (int i = 0; i < vg.getChildCount(); i++) {

                    applyPressEffectRecursive(vg.getChildAt(i));

                }
            }

        } catch (Throwable ignore) {}
    }
}
