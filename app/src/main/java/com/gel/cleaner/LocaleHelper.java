package com.gel.cleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import java.util.Locale;

public class LocaleHelper {

    private static final String PREF_NAME = "gel_locale";
    private static final String KEY = "lang";

    public static Context apply(Context ctx){
        String lang = get(ctx);
        return update(ctx, lang);
    }

    public static void set(Context ctx, String lang){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, lang).apply();
    }

    public static String get(Context ctx){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY, "en");
    }

    @SuppressLint("NewApi")
    private static Context update(Context ctx, String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(ctx.getResources().getConfiguration());
        config.setLocale(locale);
        return ctx.createConfigurationContext(config);
    }
}
