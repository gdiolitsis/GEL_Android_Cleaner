package com.gel.cleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    private static final String KEY = "app_lang";

    /** Load + Apply persisted language */
    public static Context apply(Context base) {
        SharedPreferences sp = base.getSharedPreferences("lang", Context.MODE_PRIVATE);
        String lang = sp.getString(KEY, "en");
        return update(base, lang);
    }

    /** Persist language only — activity must recreate() */
    public static void set(Context ctx, String lang) {
        SharedPreferences sp = ctx.getSharedPreferences("lang", Context.MODE_PRIVATE);
        sp.edit().putString(KEY, lang).apply();
    }

    /* =========================================================
     * INTERNAL
     * ========================================================= */
    private static Context update(Context context, String lang) {
        if (lang == null || lang.trim().isEmpty()) {
            lang = "en";
        }

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }
}
