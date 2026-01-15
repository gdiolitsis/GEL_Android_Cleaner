package com.gel.cleaner.iphone;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppleSpecProvider {

    private static final String PREFS = "gel_prefs";
    private static final String KEY_TYPE  = "apple_device_type";
    private static final String KEY_MODEL = "apple_device_model";

    private AppleSpecProvider(){}

    public static AppleDeviceSpec get(Context ctx){

        SharedPreferences p =
                ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String type  = p.getString(KEY_TYPE,  "iphone");
        String model = p.getString(KEY_MODEL, "iPhone 13");

        AppleDeviceSpec spec =
                AppleModelRegistry.get(type, model);

        if(spec == null){
            spec = AppleModelRegistry.get("iphone","iPhone 13");
        }

        return spec;
    }
}
