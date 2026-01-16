// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleModelRegistry.java — LOCKED

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public class AppleModelRegistry {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        // =========================
        // iPHONE
        // =========================
        DB.put("iPhone 8",  AppleSpecs.iPhone8());
        DB.put("iPhone X",  AppleSpecs.iPhoneX());
        DB.put("iPhone XR", AppleSpecs.iPhoneXR());
        DB.put("iPhone 11", AppleSpecs.iPhone11());
        DB.put("iPhone 12", AppleSpecs.iPhone12());
        DB.put("iPhone 13", AppleSpecs.iPhone13());
        DB.put("iPhone 14", AppleSpecs.iPhone14());
        DB.put("iPhone 15", AppleSpecs.iPhone15());

        // =========================
        // iPAD
        // =========================
        DB.put("iPad 7",      AppleSpecs.iPad7());
        DB.put("iPad 8",      AppleSpecs.iPad8());
        DB.put("iPad 9",      AppleSpecs.iPad9());
        DB.put("iPad Air 4",  AppleSpecs.iPadAir4());
        DB.put("iPad Air 5",  AppleSpecs.iPadAir5());
        DB.put("iPad Pro 11", AppleSpecs.iPadPro11());
        DB.put("iPad Pro 12.9", AppleSpecs.iPadPro129());
    }

    public static AppleDeviceSpec get(String model) {
        AppleDeviceSpec s = DB.get(model);
        return s != null ? s : AppleDeviceSpec.unknown();
    }
}
