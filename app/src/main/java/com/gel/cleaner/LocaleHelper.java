package com.gel.cleaner;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

// ============================================================
// LOCALE HELPER — GEL LOCKED FINAL
// Single Source of Truth for app language
// ============================================================
public final class LocaleHelper {

    private static final String PREFS = "gel_prefs";
    private static final String KEY_LANG = "app_lang";

    private LocaleHelper() {}

    // =========================================================
    // APPLY — called from attachBaseContext()
    // =========================================================
    public static Context apply(Context base) {
        String lang = getLang(base);
        return update(base, lang);
    }

    // =========================================================
    // SET LANGUAGE (persistent)
    // =========================================================
    public static void set(Context context, String code) {

        if (context == null) return;

        // accept ONLY "el" or "en"
        if (!"el".equals(code) && !"en".equals(code)) {
            code = "en";
        }

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANG, code)
                .apply();
    }

    // =========================================================
    // GET STORED LANGUAGE
    // =========================================================
    public static String getLang(Context context) {

        if (context == null) return "en";

        return context
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_LANG, "en");
    }

    // =========================================================
    // GET CURRENT LOCALE OBJECT
    // =========================================================
    public static Locale getLocale(Context context) {
        return new Locale(getLang(context));
    }

    // =========================================================
    // INTERNAL UPDATE
    // =========================================================
    private static Context update(Context context, String code) {

        if (!"el".equals(code) && !"en".equals(code)) {
            code = "en";
        }

        Locale locale = new Locale(code);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }
}
