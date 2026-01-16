// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleSpecs.java — FINAL DEVICE DATABASE
// ============================================================

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public final class AppleSpecs {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        // =====================================================
        // iPHONE SERIES
        // =====================================================

        DB.put("iPhone 8", new AppleDeviceSpec(
                "A11 Bionic",
                "Hexa-core",
                "2 GB",
                "4.7\" Retina HD",
                "iOS 17",

                "12 MP",
                "—",
                "—",
                "7 MP",
                "4K60",

                "Wi-Fi 5",
                "Bluetooth 5.0",
                false,
                true,

                "Lightning",
                true,
                false,

                "Touch ID",
                false,
                true
        ));

        DB.put("iPhone X", new AppleDeviceSpec(
                "A11 Bionic",
                "Hexa-core",
                "3 GB",
                "5.8\" OLED",
                "iOS 17",

                "12 MP Dual",
                "—",
                "—",
                "7 MP",
                "4K60",

                "Wi-Fi 5",
                "Bluetooth 5.0",
                false,
                true,

                "Lightning",
                true,
                false,

                "Face ID",
                true,
                false
        ));

        DB.put("iPhone 11", new AppleDeviceSpec(
                "A13 Bionic",
                "Hexa-core",
                "4 GB",
                "6.1\" Liquid Retina",
                "iOS 18",

                "12 MP Dual",
                "12 MP Ultra-Wide",
                "—",
                "12 MP",
                "4K60",

                "Wi-Fi 6",
                "Bluetooth 5.0",
                false,
                true,

                "Lightning",
                true,
                false,

                "Face ID",
                true,
                false
        ));

        DB.put("iPhone 12", new AppleDeviceSpec(
                "A14 Bionic",
                "Hexa-core",
                "4 GB",
                "6.1\" OLED",
                "iOS 18",

                "12 MP Dual",
                "12 MP Ultra-Wide",
                "—",
                "12 MP",
                "4K60",

                "Wi-Fi 6",
                "Bluetooth 5.0",
                true,
                true,

                "Lightning",
                true,
                true,

                "Face ID",
                true,
                false
        ));

        DB.put("iPhone 13", new AppleDeviceSpec(
                "A15 Bionic",
                "Hexa-core",
                "4 GB",
                "6.1\" OLED",
                "iOS 18",

                "12 MP Dual",
                "12 MP Ultra-Wide",
                "—",
                "12 MP",
                "4K60",

                "Wi-Fi 6",
                "Bluetooth 5.0",
                true,
                true,

                "Lightning",
                true,
                true,

                "Face ID",
                true,
                false
        ));

        DB.put("iPhone 14", new AppleDeviceSpec(
                "A15 Bionic",
                "Hexa-core",
                "6 GB",
                "6.1\" OLED",
                "iOS 18",

                "12 MP Dual",
                "12 MP Ultra-Wide",
                "—",
                "12 MP",
                "4K60",

                "Wi-Fi 6",
                "Bluetooth 5.3",
                true,
                true,

                "Lightning",
                true,
                true,

                "Face ID",
                true,
                false
        ));

        DB.put("iPhone 15", new AppleDeviceSpec(
                "A16 Bionic",
                "Hexa-core",
                "6 GB",
                "6.1\" OLED",
                "iOS 18",

                "48 MP",
                "12 MP Ultra-Wide",
                "—",
                "12 MP",
                "4K60",

                "Wi-Fi 6E",
                "Bluetooth 5.3",
                true,
                true,

                "USB-C",
                true,
                true,

                "Face ID",
                true,
                false
        ));

        // =====================================================
        // iPAD SERIES
        // =====================================================

        DB.put("iPad 9", new AppleDeviceSpec(
                "A13 Bionic",
                "Hexa-core",
                "3 GB",
                "10.2\" Retina",
                "iPadOS 18",

                "8 MP",
                "—",
                "—",
                "12 MP",
                "1080p",

                "Wi-Fi 5",
                "Bluetooth 4.2",
                false,
                true,

                "Lightning",
                true,
                false,

                "Touch ID",
                false,
                true
        ));

        DB.put("iPad Air 5", new AppleDeviceSpec(
                "M1",
                "Octa-core",
                "8 GB",
                "10.9\" Liquid Retina",
                "iPadOS 18",

                "12 MP",
                "—",
                "—",
                "12 MP",
                "4K",

                "Wi-Fi 6",
                "Bluetooth 5.0",
                true,
                true,

                "USB-C",
                true,
                false,

                "Touch ID",
                false,
                true
        ));

        DB.put("iPad Pro 11", new AppleDeviceSpec(
                "M2",
                "Octa-core",
                "8 GB",
                "11\" Liquid Retina XDR",
                "iPadOS 18",

                "12 MP",
                "10 MP Ultra-Wide",
                "—",
                "12 MP",
                "4K",

                "Wi-Fi 6E",
                "Bluetooth 5.3",
                true,
                true,

                "USB-C / Thunderbolt",
                true,
                false,

                "Face ID",
                true,
                false
        ));
    }

    // =====================================================
    // PUBLIC API
    // =====================================================
    public static AppleDeviceSpec get(String model) {
        AppleDeviceSpec s = DB.get(model);
        return s != null ? s : AppleDeviceSpec.unknown();
    }
}
