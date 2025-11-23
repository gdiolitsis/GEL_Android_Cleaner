// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 3 — Dynamic UI Reflow Manager for Foldables
// NOTE: Ολόκληρο αρχείο, κανόνας Γιώργου για copy-paste.

package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class GELFoldableUIManager {

    private final Activity activity;

    public GELFoldableUIManager(@NonNull Activity act) {
        this.activity = act;
    }

    /**
     * Reflow UI depending on inner/outer display
     * Called when hinge posture changes detection
     */
    public void applyUI(boolean isInnerScreen) {

        if (isInnerScreen) {
            // 📱📖 Big screen mode — unfolded tablet-like view
            applyFontScale(1.15f);
            applyPaddingScale(1.20f);
        } else {
            // 📱 Outer screen / normal mode
            applyFontScale(1.0f);
            applyPaddingScale(1.0f);
        }

        // Μπορείς να καλέσεις εδώ και custom reflows:
        // expand to two columns, bigger cards, etc.
    }

    /**
     * Scale all TextViews in the activity
     */
    private void applyFontScale(float factor) {
        scaleTextViews(activity.findViewById(android.R.id.content), factor);
    }

    private void scaleTextViews(View root, float factor) {
        if (root == null) return;

        if (root instanceof TextView) {
            TextView t = (TextView) root;
            float current = t.getTextSize();
            float scaled = current * factor;
            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaled);
        }

        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scaleTextViews(vg.getChildAt(i), factor);
            }
        }
    }

    /**
     * Scale padding for all views
     */
    private void applyPaddingScale(float factor) {
        scalePadding(activity.findViewById(android.R.id.content), factor);
    }

    private void scalePadding(View root, float factor) {
        if (root == null) return;

        int left   = (int) (root.getPaddingLeft() * factor);
        int top    = (int) (root.getPaddingTop() * factor);
        int right  = (int) (root.getPaddingRight() * factor);
        int bottom = (int) (root.getPaddingBottom() * factor);

        root.setPadding(left, top, right, bottom);

        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scalePadding(vg.getChildAt(i), factor);
            }
        }
    }

}
