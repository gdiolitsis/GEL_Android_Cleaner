// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableUIManager — Official Foldable Reflow Engine v1.3 (Context-Safe + Static Compat)
// *********************************************************************************
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

public class GELFoldableUIManager {

    private static final String TAG = "GEL.FoldableUI";

    private final Activity act; // may be null if constructed from non-activity context
    private boolean lastInner = false;

    private static final int TAG_ORIG_TEXT_PX     = 0x7F0A0F01;
    private static final int TAG_ORIG_PAD_LEFT    = 0x7F0A0F02;
    private static final int TAG_ORIG_PAD_TOP     = 0x7F0A0F03;
    private static final int TAG_ORIG_PAD_RIGHT   = 0x7F0A0F04;
    private static final int TAG_ORIG_PAD_BOTTOM  = 0x7F0A0F05;

    // Activity ctor (preferred)
    public GELFoldableUIManager(Activity activity) {
        this.act = activity;
    }

    // Context ctor (for adapters etc.) — safe no-op if not Activity
    public GELFoldableUIManager(Context ctx) {
        this.act = (ctx instanceof Activity) ? (Activity) ctx : null;
    }

    // Static compatibility
    public static void freezeTransitions(Activity a) { Log.d(TAG, "freezeTransitions()"); }
    public static void unfreezeTransitions(Activity a) { Log.d(TAG, "unfreezeTransitions()"); }

    // ============================================================
    // PUBLIC ENTRY POINT
    // ============================================================
    public void applyUI(boolean isInnerScreen) {
        if (act == null) return; // constructed from non-activity context -> no-op

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

        if (isInnerScreen && !lastInner) playOvershoot();
        lastInner = isInnerScreen;
    }

    private void applyFontScale(float factor) {
        View root = act.findViewById(android.R.id.content);
        scaleTextRecursive(root, factor);
    }

    private void scaleTextRecursive(View v, float f) {
        if (v == null) return;

        if (v instanceof TextView) {
            TextView t = (TextView) v;

            Object tag = t.getTag(TAG_ORIG_TEXT_PX);
            float origPx;
            if (tag instanceof Float) origPx = (Float) tag;
            else {
                origPx = t.getTextSize();
                t.setTag(TAG_ORIG_TEXT_PX, origPx);
            }

            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, origPx * f);
        }

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++)
                scaleTextRecursive(vg.getChildAt(i), f);
        }
    }

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

        v.setPadding((int)(origL*f), (int)(origT*f), (int)(origR*f), (int)(origB*f));

        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++)
                scalePaddingRecursive(vg.getChildAt(i), f);
        }
    }

    private int cachePad(View v, int key, int current) {
        Object tag = v.getTag(key);
        if (tag instanceof Integer) return (Integer) tag;
        v.setTag(key, current);
        return current;
    }

    private void applyColumnReflow(int columns) {
        View root = act.findViewById(android.R.id.content);
        if (!(root instanceof ViewGroup)) return;

        ViewGroup parent = (ViewGroup) root;
        if (parent.getChildCount() == 1 && parent.getChildAt(0) instanceof ViewGroup)
            parent = (ViewGroup) parent.getChildAt(0);

        if (columns <= 1) restoreLinear(parent);
        else convertToGrid(parent, columns);
    }

    private void convertToGrid(ViewGroup parent, int cols) {
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

    private void restoreLinear(ViewGroup parent) {
        if (parent instanceof LinearLayout) {
            ((LinearLayout) parent).setOrientation(LinearLayout.VERTICAL);
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

    private void playOvershoot() {
        try {
            View root = act.findViewById(android.R.id.content);
            if (root == null) return;

            root.setScaleX(0.98f);
            root.setScaleY(0.98f);
            root.animate()
                    .scaleX(1.0f).scaleY(1.0f)
                    .setDuration(260)
                    .setInterpolator(new OvershootInterpolator(0.9f))
                    .start();
        } catch (Throwable ignored) {}
    }

    private int readSmallestWidthDp() {
        try {
            Configuration c = act.getResources().getConfiguration();
            if (c != null && c.smallestScreenWidthDp > 0) return c.smallestScreenWidthDp;
        } catch (Throwable ignored) {}
        return 360;
    }
}
