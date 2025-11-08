package com.gel.cleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREF = "locale_pref";
    private static final String KEY_LANG = "lang";

    public static Context onAttach(Context ctx) {
        String lang = getLanguage(ctx);
        return setLocale(ctx, lang);
    }

    public static String getLanguage(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(KEY_LANG, "en");   // default English
    }

    @SuppressLint("ApplySharedPref")
    public static void setLanguage(Context ctx, String lang) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_LANG, lang).commit();
    }

    public static Context setLocale(Context ctx, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return ctx.createConfigurationContext(config);
    }
}
