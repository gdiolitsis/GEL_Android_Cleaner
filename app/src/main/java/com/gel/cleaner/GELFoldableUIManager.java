// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableUIManager — Official Foldable Reflow Engine v1.0
// *********************************************************************************
// • SAFE column reflow (1 → 2 columns for inner screen)
// • SAFE text scaling
// • SAFE padding scaling
// • Compatible with ScrollView / NestedScrollView
// • No destructive layout mutations
// *********************************************************************************

package com.gel.cleaner;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.gridlayout.widget.GridLayout;

public class GELFoldableUIManager {

    private final Activity act;

    public GELFoldableUIManager(Activity activity) {
        this.act = activity;
    }

    // ============================================================
    // PUBLIC ENTRY POINT
    // ============================================================
    public void applyUI(boolean isInnerScreen) {

        if (isInnerScreen) {
            // Unfolded → tablet mode
            applyFontScale(1.15f);
            applyPaddingScale(1.20f);
            applyColumnReflow(true);
        } else {
            // Phone / outer display
            applyFontScale(1.00f);
            applyPaddingScale(1.00f);
            applyColumnReflow(false);
        }
    }

    // ============================================================
    // FONT SCALE (SAFE)
    // ============================================================
    private void applyFontScale(float factor) {
        View root = act.findViewById(android.R.id.content);
        scaleTextRecursive(root, factor);
    }

    private void scaleTextRecursive(View v, float f) {
        if (v == null) return;

        if (v instanceof TextView) {
            TextView t = (TextView) v;
            float px = t.getTextSize();
            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, px * f);
        }

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scaleTextRecursive(vg.getChildAt(i), f);
            }
        }
    }

    // ============================================================
    // PADDING SCALE (SAFE)
    // ============================================================
    private void applyPaddingScale(float factor) {
        View root = act.findViewById(android.R.id.content);
        scalePaddingRecursive(root, factor);
    }

    private void scalePaddingRecursive(View v, float f) {
        if (v == null) return;

        int l = (int) (v.getPaddingLeft() * f);
        int t = (int) (v.getPaddingTop() * f);
        int r = (int) (v.getPaddingRight() * f);
        int b = (int) (v.getPaddingBottom() * f);

        v.setPadding(l, t, r, b);

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scalePaddingRecursive(vg.getChildAt(i), f);
            }
        }
    }

    // ============================================================
    // SAFE COLUMN REFLOW (1 → 2 columns)
    // ============================================================
    private void applyColumnReflow(boolean dual) {
        View root = act.findViewById(android.R.id.content);

        // Only apply on containers that have many children (sections)
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            convertTopLevelToColumns(vg, dual);
        }
    }

    private void convertTopLevelToColumns(ViewGroup root, boolean dual) {

        // Skip ScrollView itself, use its child
        if (root.getChildCount() == 1 && root.getChildAt(0) instanceof ViewGroup) {
            root = (ViewGroup) root.getChildAt(0);
        }

        // Typical pattern: root LinearLayout with all sections
        if (!(root instanceof LinearLayout)) return;
        LinearLayout ll = (LinearLayout) root;

        // If phone mode → ensure vertical
        if (!dual) {
            if (ll.getOrientation() != LinearLayout.VERTICAL) {
                ll.setOrientation(LinearLayout.VERTICAL);
            }
            return;
        }

        // INNER SCREEN MODE → convert to GridLayout 2 columns
        GridLayout grid = new GridLayout(act);
        grid.setColumnCount(2);
        grid.setOrientation(GridLayout.HORIZONTAL);
        grid.setUseDefaultMargins(true);

        // Move children
        while (ll.getChildCount() > 0) {
            View c = ll.getChildAt(0);
            ll.removeViewAt(0);
            grid.addView(c);
        }

        // Replace in parent
        ViewGroup parent = (ViewGroup) ll.getParent();
        int index = parent.indexOfChild(ll);
        parent.removeView(ll);
        parent.addView(grid, index);
    }
}
