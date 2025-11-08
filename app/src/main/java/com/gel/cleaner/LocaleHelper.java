package com.gel.cleaner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, "en");
        return setLocale(context, lang);
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        return updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context, String defaultLang) {
        return context.getSharedPreferences("GEL", Context.MODE_PRIVATE)
                .getString("lang", defaultLang);
    }

    private static void persist(Context context, String language) {
        context.getSharedPreferences("GEL", Context.MODE_PRIVATE)
                .edit()
                .putString("lang", language)
                .apply();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;

        res.updateConfiguration(config, res.getDisplayMetrics());
        return context;
    }
}
