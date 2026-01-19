// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleSpecs.java — iPhone 15 Series (LOCKED DATASET)
// ============================================================


package com.gel.cleaner.iphone;

public final class AppleSpecs {

    public static AppleDeviceSpec get(String model) {
        if (model == null) return AppleDeviceSpec.unknown();

        String m = model.toLowerCase();

        if (m.startsWith("iphone"))
            return iPhoneSpecs.get(m);

        if (m.startsWith("ipad"))
            return iPadSpecs.get(m);

        return AppleDeviceSpec.unknown();
    }

    private AppleSpecs() {}
}
