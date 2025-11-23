package com.gel.cleaner;

import android.app.Activity;
import android.util.DisplayMetrics;

public class GELAutoDP {

    private static float scaleFactor = 1f;
    private static boolean initialized = false;

    // ------------------------------------------------------------
    // Init once per Activity (call from onCreate)
    // ------------------------------------------------------------
    public static void init(Activity a) {
        if (initialized) return;
        initialized = true;

        DisplayMetrics dm = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(dm);

        float width = dm.widthPixels / dm.density;

        // ------------------------------------------------------------
        // BASELINE GEL WIDTH = 360dp
        // ------------------------------------------------------------
        scaleFactor = width / 360f;

        if (scaleFactor < 0.85f) scaleFactor = 0.85f;   // avoid tiny UI
        if (scaleFactor > 1.45f) scaleFactor = 1.45f;   // avoid huge UI
    }

    // ------------------------------------------------------------
    // Convert dp to scaled dp
    // ------------------------------------------------------------
    public static int dp(int dp) {
        return Math.round(dp * scaleFactor);
    }

    // ------------------------------------------------------------
    // Convert text sizes uniformly
    // ------------------------------------------------------------
    public static float sp(float sp) {
        return sp * scaleFactor;
    }

    // ------------------------------------------------------------
    // Useful if needed in XML programmatic settings
    // ------------------------------------------------------------
    public static float factor() {
        return scaleFactor;
    }
}
