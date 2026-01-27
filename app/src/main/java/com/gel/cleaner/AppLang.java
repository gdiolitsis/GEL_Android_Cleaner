package com.gel.cleaner;

import android.content.Context;
import java.util.Locale;

// ============================================================
// APP LANGUAGE HELPER — GEL (LOCKED)
// Source of truth: LocaleHelper (app language)
// ============================================================
public final class AppLang {

    private AppLang() {}

    // ------------------------------------------------------------
    // TRUE = Greek, FALSE = English
    // ------------------------------------------------------------
    public static boolean isGreek(Context c) {
        if (c == null) return false;

        try {
            Locale l = LocaleHelper.getLocale(c);
            return l != null && "el".equalsIgnoreCase(l.getLanguage());
        } catch (Throwable t) {
            return false;
        }
    }

    // ------------------------------------------------------------
    // Canonical language
    // ------------------------------------------------------------
    public static String lang(Context c) {
        return isGreek(c) ? "el" : "en";
    }
}
