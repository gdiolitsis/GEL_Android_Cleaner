package com.gel.cleaner;

import android.content.Context;
import java.util.Locale;

// ============================================================
// APP LANGUAGE HELPER — GEL (LOCKED FINAL)
// Source of truth: LocaleHelper
// This class NEVER changes language.
// It only reads the active app locale.
// ============================================================
public final class AppLang {

    private AppLang() {}

    // ------------------------------------------------------------
    // TRUE = Greek, FALSE = English
    // ------------------------------------------------------------
    public static boolean isGreek(Context c) {
        return "el".equals(lang(c));
    }

    // ------------------------------------------------------------
    // Canonical language code ("el" or "en")
    // ------------------------------------------------------------
    public static String lang(Context c) {
        if (c == null) return "en";

        try {
            Locale l = LocaleHelper.getLocale(c);
            if (l == null) return "en";

            String code = l.getLanguage();
            return "el".equalsIgnoreCase(code) ? "el" : "en";

        } catch (Throwable ignore) {
            return "en";
        }
    }
}
