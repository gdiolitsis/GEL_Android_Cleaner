package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;

// ============================================================
// APP LANGUAGE HELPER — GEL STYLE
// ============================================================
public final class AppLang {

    private static final String PREFS = "gel_prefs";
    private static final String KEY_LANG = "app_lang"; // "en" | "gr"

    private AppLang() {} // no instances

    // ------------------------------------------------------------
    // TRUE = Greek, FALSE = English
    // ------------------------------------------------------------
    public static boolean isGreek(Context c) {
        if (c == null) return false;

        SharedPreferences sp =
                c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        return "gr".equalsIgnoreCase(
                sp.getString(KEY_LANG, "en")
        );
    }

    // ------------------------------------------------------------
    // Convenience
    // ------------------------------------------------------------
    public static String lang(Context c) {
        return isGreek(c) ? "gr" : "en";
    }
}
