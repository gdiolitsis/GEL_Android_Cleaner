package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREF = "gel_locale";
    private static final String KEY  = "lang";

    public static Context apply(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String lang = sp.getString(KEY, "en");
        return setLocale(context, lang);
    }

    public static void set(Context context, String lang) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, lang).apply();
        setLocale(context, lang);
    }

    private static Context setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration cfg = new Configuration(context.getResources().getConfiguration());
        cfg.setLocale(locale);
        return context.createConfigurationContext(cfg);
    }
}
