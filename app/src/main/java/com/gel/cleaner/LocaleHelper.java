package com.gel.cleaner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    private static final String LANG_EN = "en";
    private static final String LANG_EL = "el";

    public static Context setLocale(Context context, boolean greek) {
        String lang = greek ? LANG_EL : LANG_EN;
        return updateResources(context, lang);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResources(Context context, String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());

        // ✅ For older devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }

        // ✅ For newer devices
        return updateResourcesModern(context, language);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResourcesModern(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
