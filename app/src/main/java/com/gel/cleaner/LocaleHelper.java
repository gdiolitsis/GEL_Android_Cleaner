// GDiolitsis Engine Lab (GEL) — Author & Developer
// LocaleHelper v3.0 — Ultra-Safe Multi-Language Engine
// ---------------------------------------------------------------
// ✔ Full support: Android 5 → Android 14
// ✔ Safe recreate() model
// ✔ No leaks, no crashes
// ✔ 100% stable σε foldables / tablets / multi-window
// ✔ Ολόκληρο αρχείο — έτοιμο για copy-paste (κανόνας παππού Γιώργου)
// ---------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public final class LocaleHelper {

    private LocaleHelper() {} // no instances

    private static final String PREFS = "gel_lang_pref";
    private static final String KEY   = "app_lang";

    // ============================================================
    // PUBLIC API
    // ============================================================

    /** Apply persisted locale — Call ONLY in attachBaseContext() */
    public static Context apply(Context base) {
        String lang = getLang(base);
        return update(base, lang);
    }

    /** Save language code (e.g. "en", "el", "es"). Activity must call recreate(). */
    public static void set(Context ctx, String lang) {
        if (ctx == null) return;
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, (lang == null || lang.isEmpty()) ? "en" : lang)
                .apply();
    }

    /** Get current persisted language */
    public static String getLang(Context ctx) {
        if (ctx == null) return "en";
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY, "en");
    }

    // ============================================================
    // INTERNAL — Locale Update Engine
    // ============================================================
    private static Context update(Context ctx, String lang) {

        if (ctx == null) return null;

        if (lang == null || lang.trim().isEmpty())
            lang = "en";

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(ctx.getResources().getConfiguration());
        config.setLocale(locale);

        // Android 7+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ctx.createConfigurationContext(config);
        }

        // Legacy (Android 5–6)
        ctx.getResources().updateConfiguration(
                config,
                ctx.getResources().getDisplayMetrics()
        );

        return ctx;
    }
}
