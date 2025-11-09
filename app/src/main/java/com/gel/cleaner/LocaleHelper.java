package com.gel.cleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    @SuppressLint("ApplySharedPref")
    public static void set(Context ctx, String lang) {
        SharedPreferences sp = ctx.getSharedPreferences("locale", Context.MODE_PRIVATE);
        sp.edit().putString("lang", lang).commit();
    }

    public static Context apply(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences("locale", Context.MODE_PRIVATE);
        String lang = sp.getString("lang", "en");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration cfg = new Configuration();
        cfg.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return ctx.createConfigurationContext(cfg);
        } else {
            ctx.getResources().updateConfiguration(cfg, ctx.getResources().getDisplayMetrics());
            return ctx;
        }
    }
}
