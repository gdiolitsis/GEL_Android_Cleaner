// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELAutoDP v4.5 — Foldable-Aware Universal DP/SP Auto-Scaling Core
// Fully Integrated with: GELAutoActivityHook + Foldable Orchestrator (reflection-safe)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import java.lang.reflect.Method;
import android.app.Activity;
import android.content.Context;
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
    private static final float BASE_SW_DP_PHONE  = 360f;  // Portrait phone baseline
    private static final float BASE_SW_DP_TABLET = 600f;  // Tablet / inner foldable baseline

    // clamps
    private static final float MIN_FACTOR = 0.80f;
    private static final float MAX_FACTOR = 2.40f;
    private static final float MIN_TEXT  = 0.85f;
    private static final float MAX_TEXT  = 2.00f;

    private static float factor     = 1f;
    private static float textFactor = 1f;
    private static boolean inited   = false;

    private static boolean lastInner = false;

    // ============================================================
    // PUBLIC INIT
    // Called from every Activity (onCreate + onConfigurationChanged)
    // ============================================================
    public static void init(Activity a) {
        if (a == null) return;

        float sw = readSmallestWidthDp(a);
        if (sw <= 0) sw = BASE_SW_DP_PHONE;

        boolean isInner = detectCurrentInnerMode(a, sw);
        lastInner = isInner;

        applyFrom(sw, isInner);

        inited = true;
    }

    /**
     * Optional direct posture push from orchestrator / detector.
     * Safe if you ever want instant rescale on unfold without waiting config change.
     */
    public static void setInnerMode(Activity a, boolean isInner) {
        if (a == null) return;
        float sw = readSmallestWidthDp(a);
        if (sw <= 0) sw = BASE_SW_DP_PHONE;

        lastInner = isInner;
        applyFrom(sw, isInner);

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

    public static boolean isLastInner() {
        ensure();
        return lastInner;
    }

    // ============================================================
    // INTERNALS
    // ============================================================
    private static void ensure() {
        if (!inited) {
            factor = 1f;
            textFactor = 1f;
            lastInner = false;
            inited = true;
        }
    }

    private static void applyFrom(float sw, boolean isInner) {
        float base = isInner ? BASE_SW_DP_TABLET : BASE_SW_DP_PHONE;

        float f = sw / base;
        f = clamp(f, MIN_FACTOR, MAX_FACTOR);
        factor = f;

        float t = isInner ? 0.70f : 0.55f;
        float tf = lerp(1f, factor, t);
        tf = clamp(tf, MIN_TEXT, MAX_TEXT);
        textFactor = tf;
    }

    /**
     * Foldable-aware smallestWidthDp.
     */
    private static float readSmallestWidthDp(Activity a) {
        try {
            Configuration c = a.getResources().getConfiguration();
            if (c != null && c.smallestScreenWidthDp > 0) {
                return c.smallestScreenWidthDp;
            }

            DisplayMetrics baseDm = a.getResources().getDisplayMetrics();
            float density = (baseDm != null && baseDm.density > 0) ? baseDm.density : 1f;

            int wPx, hPx;

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
            return BASE_SW_DP_PHONE;
        }
    }

    /**
     * Detect inner/outer mode via GELFoldableOrchestrator (if active)
     * Reflection-safe, no hard dependency.
     */
    private static boolean detectCurrentInnerMode(Activity a, float sw) {

        // 1) Try orchestrator singleton/static
        try {
            Class<?> clazz = Class.forName("com.gel.cleaner.GELFoldableOrchestrator");

            // a) static isInnerScreen() ?
            try {
                Method mStatic = clazz.getMethod("isInnerScreen");
                if (java.lang.reflect.Modifier.isStatic(mStatic.getModifiers())) {
                    Object r = mStatic.invoke(null);
                    if (r instanceof Boolean) return (Boolean) r;
                }
            } catch (Throwable ignored) {}

            // b) getInstance(Context) / get(Context) / instance()
            Object inst = null;

            inst = tryInvokeFactory(clazz, "getInstance", new Class[]{Context.class}, new Object[]{a});
            if (inst == null) {
                inst = tryInvokeFactory(clazz, "get", new Class[]{Context.class}, new Object[]{a});
            }
            if (inst == null) {
                inst = tryInvokeFactory(clazz, "instance", null, null);
            }

            if (inst != null) {
                try {
                    Method m = clazz.getMethod("isInnerScreen");
                    Object r = m.invoke(inst);
                    if (r instanceof Boolean) return (Boolean) r;
                } catch (Throwable ignored) {}
            }

        } catch (Throwable ignored) {}

        // 2) Fallback: treat large screens as inner-mode
        return sw >= 520f;
    }

    private static Object tryInvokeFactory(Class<?> clazz, String name, Class<?>[] sig, Object[] args) {
        try {
            Method m = (sig == null)
                    ? clazz.getMethod(name)
                    : clazz.getMethod(name, sig);

            if (!java.lang.reflect.Modifier.isStatic(m.getModifiers())) return null;

            return (sig == null) ? m.invoke(null) : m.invoke(null, args);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float clamp(float v, float min, float max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}
