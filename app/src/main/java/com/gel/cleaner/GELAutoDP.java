// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELAutoDP v4.4 — Foldable-Aware Universal DP/SP Auto-Scaling Core
// 🔥 Fully Integrated with: GELFoldableOrchestrator + UIManager + DualPane
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;

public final class GELAutoDP {

    private GELAutoDP() {}

    // ------------------------------------------------------------
    // BASELINES
    // ------------------------------------------------------------
    private static final float BASE_SW_DP_PHONE = 360f;   // Portrait phone baseline
    private static final float BASE_SW_DP_TABLET = 600f;  // Tablet / big foldable baseline

    private static float factor = 1f;
    private static float textFactor = 1f;
    private static boolean inited = false;

    private static boolean lastInner = false; // from foldable manager

    // ============================================================
    // PUBLIC INIT
    // Called from every Activity (onCreate + onConfigurationChanged)
    // ============================================================
    public static void init(Activity a) {
        if (a == null) return;

        float sw = readSmallestWidthDp(a);
        if (sw <= 0) sw = BASE_SW_DP_PHONE;

        boolean isInner = detectCurrentInnerMode(a);
        lastInner = isInner;

        float base = isInner ? BASE_SW_DP_TABLET : BASE_SW_DP_PHONE;

        float f = sw / base;

        if (f < 0.80f) f = 0.80f;
        if (f > 2.40f) f = 2.40f;

        factor = f;

        textFactor = lerp(1f, factor, isInner ? 0.70f : 0.55f);
        if (textFactor < 0.85f) textFactor = 0.85f;
        if (textFactor > 2.00f) textFactor = 2.00f;

        inited = true;
    }

    // ============================================================
    // DP/SP/PX HELPERS
    // ============================================================
    public static int dp(int dp) {
        ensure();
        return Math.round(dp * factor);
    }

    public static float sp(float sp) {
        ensure();
        return sp * textFactor;
    }

    public static int px(int px) {
        ensure();
        return Math.round(px * factor);
    }

    public static float factor() {
        ensure();
        return factor;
    }

    public static float textFactor() {
        ensure();
        return textFactor;
    }

    // ============================================================
    // INTERNALS
    // ============================================================
    private static void ensure() {
        if (!inited) {
            factor = 1f;
            textFactor = 1f;
            inited = true;
        }
    }

    /**
     * Foldable-aware smallestWidthDp.
     * Inner-Screen = more generous base; outer screen = compact.
     */
    private static float readSmallestWidthDp(Activity a) {
        try {
            Configuration c = a.getResources().getConfiguration();
            if (c != null && c.smallestScreenWidthDp > 0) {
                return c.smallestScreenWidthDp;
            }

            DisplayMetrics baseDm = a.getResources().getDisplayMetrics();
            float density = baseDm != null && baseDm.density > 0 ? baseDm.density : 1f;

            int wPx, hPx;

            if (Build.VERSION.SDK_INT >= 30) {
                WindowMetrics wm = a.getWindowManager().getCurrentWindowMetrics();
                Rect b = wm.getBounds();
                wPx = b.width();
                hPx = b.height();
            } else {
                DisplayMetrics dm = new DisplayMetrics();
                a.getWindowManager().getDefaultDisplay().getMetrics(dm);
                wPx = dm.widthPixels;
                hPx = dm.heightPixels;
            }

            float wDp = wPx / density;
            float hDp = hPx / density;
            return Math.min(wDp, hDp);

        } catch (Throwable t) {
            return BASE_SW_DP_PHONE;
        }
    }

    /**
     * Detect inner/outer mode via GELFoldableOrchestrator (if active)
     * + fallback screen size logic.
     */
    private static boolean detectCurrentInnerMode(Activity a) {
        try {
            Class<?> clazz = Class.forName("com.gel.cleaner.GELFoldableOrchestrator");
            Object inst = GELFoldableGlobalHolder.get(clazz);
            if (inst != null) {
                try {
                    return (boolean) clazz
                            .getMethod("isInnerScreen")
                            .invoke(inst);
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}

        // Fallback: treat large screens as inner-mode
        float sw = readSmallestWidthDp(a);
        return sw >= 520f;  // pretty safe threshold for inner foldable screen
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
