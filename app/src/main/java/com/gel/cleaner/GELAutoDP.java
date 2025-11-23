// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELAutoDP v4.3 — Universal DP/SP Auto-Scaling Core
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;

public final class GELAutoDP {

    private GELAutoDP() {}

    // Baseline design smallest-width in dp (Pixel-ish reference)
    private static final float BASE_SW_DP = 360f;

    private static float factor = 1f;      // dp/px scale
    private static float textFactor = 1f;  // sp scale (softer)
    private static boolean inited = false;

    // ------------------------------------------------------------
    // PUBLIC INIT
    // Call in every Activity onCreate + onConfigurationChanged.
    // Safe to call many times.
    // ------------------------------------------------------------
    public static void init(Activity a) {
        if (a == null) return;

        float swDp = readSmallestWidthDp(a);
        if (swDp <= 0) swDp = BASE_SW_DP;

        // Core scale relative to baseline
        float f = swDp / BASE_SW_DP;

        // Hard limits for sanity across tiny phones / huge tablets
        if (f < 0.85f) f = 0.85f;   // protect small screens
        if (f > 2.10f) f = 2.10f;   // tablets/foldables max

        factor = f;

        // Text scaling: softer so fonts don't explode on tablets
        textFactor = lerp(1f, factor, 0.75f);
        if (textFactor < 0.90f) textFactor = 0.90f;
        if (textFactor > 1.80f) textFactor = 1.80f;

        inited = true;
    }

    // ------------------------------------------------------------
    // DP / SP / PX helpers
    // ------------------------------------------------------------
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

    // ------------------------------------------------------------
    // INTERNALS
    // ------------------------------------------------------------
    private static void ensure() {
        if (!inited) {
            factor = 1f;
            textFactor = 1f;
            inited = true;
        }
    }

    /**
     * Reads smallest width dp from configuration.
     * GOLD standard for tablets/foldables.
     */
    private static float readSmallestWidthDp(Activity a) {
        try {
            Configuration c = a.getResources().getConfiguration();
            if (c != null && c.smallestScreenWidthDp > 0) {
                return c.smallestScreenWidthDp;
            }

            // Fallback if OEM returns 0: compute from real pixels
            DisplayMetrics baseDm = a.getResources().getDisplayMetrics();
            float density = (baseDm != null && baseDm.density > 0f) ? baseDm.density : 1f;

            int wPx;
            int hPx;

            if (Build.VERSION.SDK_INT >= 30) {
                WindowMetrics wm = a.getWindowManager().getCurrentWindowMetrics();
                Rect b = wm.getBounds();
                wPx = b.width();
                hPx = b.height();
            } else {
                DisplayMetrics dm = new DisplayMetrics();
                //noinspection deprecation
                a.getWindowManager().getDefaultDisplay().getMetrics(dm);
                wPx = dm.widthPixels;
                hPx = dm.heightPixels;
            }

            float wDp = wPx / density;
            float hDp = hPx / density;
            return Math.min(wDp, hDp);

        } catch (Throwable t) {
            return BASE_SW_DP;
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
