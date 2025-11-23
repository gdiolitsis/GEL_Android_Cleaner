// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableUIManager — Official Foldable Reflow Engine v1.1 (SAFE EDITION)
// *********************************************************************************
// • SAFE column reflow (1 → 2 columns → back to 1)
// • SAFE text scaling
// • SAFE padding scaling
// • Compatible with ScrollView / NestedScrollView
// • Idempotent (δεν ξαναμετατρέπει layout που έχει ήδη αλλάξει)
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
            applyFontScale(1.15f);
            applyPaddingScale(1.20f);
            applyColumnReflow(true);
        } else {
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
    // SAFE COLUMN REFLOW (dual = 2 columns)
    // ============================================================
    private void applyColumnReflow(boolean dual) {
        View root = act.findViewById(android.R.id.content);

        if (!(root instanceof ViewGroup)) return;

        ViewGroup parent = (ViewGroup) root;

        // ScrollView / NestedScrollView case
        if (parent.getChildCount() == 1 && parent.getChildAt(0) instanceof ViewGroup) {
            parent = (ViewGroup) parent.getChildAt(0);
        }

        if (!dual) {
            restoreLinear(parent);
        } else {
            convertToGrid(parent);
        }
    }

    // ============================================================
    // CONVERT → GRID (2 columns)
    // ============================================================
    private void convertToGrid(ViewGroup parent) {

        // Already grid? Skip.
        if (parent instanceof GridLayout) return;

        if (!(parent instanceof LinearLayout)) return;

        LinearLayout ll = (LinearLayout) parent;

        GridLayout grid = new GridLayout(act);
        grid.setColumnCount(2);
        grid.setUseDefaultMargins(true);
        grid.setOrientation(GridLayout.HORIZONTAL);

        // Move children safely
        while (ll.getChildCount() > 0) {
            View c = ll.getChildAt(0);
            ll.removeViewAt(0);
            grid.addView(c);
        }

        // Replace in parent-of-parent
        ViewGroup superP = (ViewGroup) ll.getParent();
        if (superP == null) return;

        int index = superP.indexOfChild(ll);
        superP.removeView(ll);
        superP.addView(grid, index);
    }

    // ============================================================
    // RESTORE → LINEAR (1 column)
    // ============================================================
    private void restoreLinear(ViewGroup parent) {

        // Already linear → nothing to do
        if (parent instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) parent;
            if (ll.getOrientation() != LinearLayout.VERTICAL)
                ll.setOrientation(LinearLayout.VERTICAL);
            return;
        }

        // Only revert GridLayout → LinearLayout
        if (!(parent instanceof GridLayout)) return;

        GridLayout grid = (GridLayout) parent;

        LinearLayout ll = new LinearLayout(act);
        ll.setOrientation(LinearLayout.VERTICAL);

        while (grid.getChildCount() > 0) {
            View c = grid.getChildAt(0);
            grid.removeViewAt(0);
            ll.addView(c);
        }

        ViewGroup superP = (ViewGroup) grid.getParent();
        if (superP == null) return;

        int idx = superP.indexOfChild(grid);
        superP.removeView(grid);
        superP.addView(ll, idx);
    }
}
