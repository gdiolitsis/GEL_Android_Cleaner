package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;

// ============================================================
// APP LANGUAGE HELPER — GEL STYLE (LOCKED)
// • App-level language ONLY (not system)
// • Application-context safe
// • Android / TTS compatible ("el" / "en")
// ============================================================
public final class AppLang {

    private static final String PREFS   = "gel_prefs";
    private static final String KEY_LANG = "app_lang"; // "en" | "el"

    private AppLang() {} // no instances

    // ------------------------------------------------------------
    // TRUE = Greek, FALSE = English
    // ------------------------------------------------------------
    public static boolean isGreek(Context c) {
        if (c == null) return false;

        Context appCtx = c.getApplicationContext();

        SharedPreferences sp =
                appCtx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String lang = sp.getString(KEY_LANG, "en");

        return "el".equalsIgnoreCase(lang) || "gr".equalsIgnoreCase(lang);
    }

    // ------------------------------------------------------------
    // Canonical language code for Android / TTS
    // ------------------------------------------------------------
    public static String lang(Context c) {
        return isGreek(c) ? "el" : "en";
    }
}
