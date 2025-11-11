package com.gel.cleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    private static final String KEY = "app_lang";

    public static Context apply(Context context) {
        SharedPreferences sp = context.getSharedPreferences("lang", Context.MODE_PRIVATE);
        String lang = sp.getString(KEY, "en");
        return setLocale(context, lang);
    }

    public static void set(Context context, String language) {
        SharedPreferences sp = context.getSharedPreferences("lang", Context.MODE_PRIVATE);
        sp.edit().putString(KEY, language).apply();
    }

    @SuppressLint("NewApi")
    private static Context setLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
