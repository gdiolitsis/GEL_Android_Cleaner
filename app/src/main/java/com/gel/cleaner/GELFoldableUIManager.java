// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableUIManager — Official Foldable Reflow Engine v1.2 (ULTRA SAFE EDITION)
// *********************************************************************************
// • SAFE column reflow with breakpoints:
//      sw < 600dp  → 1 column
//      600–719dp   → 2 columns
//      ≥ 720dp     → 3 columns
// • SAFE text scaling with ORIGINAL size caching (no drift)
// • SAFE padding scaling with ORIGINAL padding caching (no drift)
// • Idempotent layout swaps (won’t re-convert already swapped containers)
// • Soft overshoot animation when entering inner/large mode
// • Compatible with ScrollView / NestedScrollView / FrameLayout wrappers
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

package com.gel.cleaner;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

public class GELFoldableUIManager {

    private final Activity act;

    // last-known state to avoid repeating animations/reflows
    private boolean lastInner = false;

    // Unique tag keys (hardcoded safe ints)
    private static final int TAG_ORIG_TEXT_PX     = 0x7F0A0F01;
    private static final int TAG_ORIG_PAD_LEFT    = 0x7F0A0F02;
    private static final int TAG_ORIG_PAD_TOP     = 0x7F0A0F03;
    private static final int TAG_ORIG_PAD_RIGHT   = 0x7F0A0F04;
    private static final int TAG_ORIG_PAD_BOTTOM  = 0x7F0A0F05;

    public GELFoldableUIManager(Activity activity) {
        this.act = activity;
    }

    // ============================================================
    // PUBLIC ENTRY POINT
    // ============================================================
    public void applyUI(boolean isInnerScreen) {

        // Breakpoints based on smallest width dp
        int sw = readSmallestWidthDp();
        int cols = 1;
        if (sw >= 720) cols = 3;
        else if (sw >= 600) cols = 2;

        if (isInnerScreen) {
            applyFontScale(1.15f);
            applyPaddingScale(1.20f);
            applyColumnReflow(cols);
        } else {
            applyFontScale(1.00f);
            applyPaddingScale(1.00f);
            applyColumnReflow(1);
        }

        // Overshoot only on transition to inner screen
        if (isInnerScreen && !lastInner) {
            playOvershoot();
        }

        lastInner = isInnerScreen;
    }

    // ============================================================
    // FONT SCALE (SAFE + CACHED ORIGINAL PX)
    // ============================================================
    private void applyFontScale(float factor) {
        View root = act.findViewById(android.R.id.content);
        scaleTextRecursive(root, factor);
    }

    private void scaleTextRecursive(View v, float f) {
        if (v == null) return;

        if (v instanceof TextView) {
            TextView t = (TextView) v;

            // cache original size once
            Object tag = t.getTag(TAG_ORIG_TEXT_PX);
            float origPx;
            if (tag instanceof Float) {
                origPx = (Float) tag;
            } else {
                origPx = t.getTextSize();
                t.setTag(TAG_ORIG_TEXT_PX, origPx);
            }

            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, origPx * f);
        }

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scaleTextRecursive(vg.getChildAt(i), f);
            }
        }
    }

    // ============================================================
    // PADDING SCALE (SAFE + CACHED ORIGINAL PADDING)
    // ============================================================
    private void applyPaddingScale(float factor) {
        View root = act.findViewById(android.R.id.content);
        scalePaddingRecursive(root, factor);
    }

    private void scalePaddingRecursive(View v, float f) {
        if (v == null) return;

        int origL = cachePad(v, TAG_ORIG_PAD_LEFT,   v.getPaddingLeft());
        int origT = cachePad(v, TAG_ORIG_PAD_TOP,    v.getPaddingTop());
        int origR = cachePad(v, TAG_ORIG_PAD_RIGHT,  v.getPaddingRight());
        int origB = cachePad(v, TAG_ORIG_PAD_BOTTOM, v.getPaddingBottom());

        int l = (int) (origL * f);
        int t = (int) (origT * f);
        int r = (int) (origR * f);
        int b = (int) (origB * f);

        v.setPadding(l, t, r, b);

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                scalePaddingRecursive(vg.getChildAt(i), f);
            }
        }
    }

    private int cachePad(View v, int key, int current) {
        Object tag = v.getTag(key);
        if (tag instanceof Integer) return (Integer) tag;
        v.setTag(key, current);
        return current;
    }

    // ============================================================
    // SAFE COLUMN REFLOW (1 / 2 / 3 cols)
    // ============================================================
    private void applyColumnReflow(int columns) {
        View root = act.findViewById(android.R.id.content);
        if (!(root instanceof ViewGroup)) return;

        ViewGroup parent = (ViewGroup) root;

        // Skip wrapper: ScrollView/NestedScrollView/FrameLayout etc.
        if (parent.getChildCount() == 1 && parent.getChildAt(0) instanceof ViewGroup) {
            parent = (ViewGroup) parent.getChildAt(0);
        }

        if (columns <= 1) {
            restoreLinear(parent);
        } else {
            convertToGrid(parent, columns);
        }
    }

    // ============================================================
    // CONVERT → GRID
    // ============================================================
    private void convertToGrid(ViewGroup parent, int cols) {

        // already grid? just update columns
        if (parent instanceof GridLayout) {
            ((GridLayout) parent).setColumnCount(cols);
            return;
        }

        if (!(parent instanceof LinearLayout)) return;
        LinearLayout ll = (LinearLayout) parent;

        GridLayout grid = new GridLayout(act);
        grid.setColumnCount(cols);
        grid.setUseDefaultMargins(true);
        grid.setOrientation(GridLayout.HORIZONTAL);

        // Move children safely
        while (ll.getChildCount() > 0) {
            View c = ll.getChildAt(0);
            ll.removeViewAt(0);
            grid.addView(c);
        }

        ViewGroup superP = (ViewGroup) ll.getParent();
        if (superP == null) return;

        int index = superP.indexOfChild(ll);
        superP.removeView(ll);
        superP.addView(grid, index);
    }

    // ============================================================
    // RESTORE → LINEAR
    // ============================================================
    private void restoreLinear(ViewGroup parent) {

        // already linear → enforce vertical
        if (parent instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) parent;
            if (ll.getOrientation() != LinearLayout.VERTICAL) {
                ll.setOrientation(LinearLayout.VERTICAL);
            }
            return;
        }

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

    // ============================================================
    // OVERSHOOT ANIMATION (inner-enter only)
    // ============================================================
    private void playOvershoot() {
        try {
            View root = act.findViewById(android.R.id.content);
            if (root == null) return;

            root.setScaleX(0.98f);
            root.setScaleY(0.98f);
            root.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(260)
                    .setInterpolator(new OvershootInterpolator(0.9f))
                    .start();
        } catch (Throwable ignored) {}
    }

    // ============================================================
    // READ SMALLEST WIDTH DP (breakpoints)
    // ============================================================
    private int readSmallestWidthDp() {
        try {
            Configuration c = act.getResources().getConfiguration();
            if (c != null && c.smallestScreenWidthDp > 0) {
                return c.smallestScreenWidthDp;
            }
        } catch (Throwable ignored) {}
        return 360; // safe fallback baseline
    }
}
