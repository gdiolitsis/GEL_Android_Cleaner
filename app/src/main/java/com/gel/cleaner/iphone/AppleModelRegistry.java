// GDiolitsis Engine Lab (GEL) — Author & Developer
package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

import com.gel.cleaner.iphone.specs.AppleDeviceSpec;

public final class AppleModelRegistry {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        DB.put("iPhone 8", new AppleDeviceSpec(
                "A11 Bionic", "Hexa-core", "2 GB", "4.7\" Retina",
                "12 MP", "Bluetooth 5.0", "Wi-Fi 802.11ac",
                "Touch ID", "Lightning", "Fast charge", "iOS 17"
        ));

        DB.put("iPhone X", new AppleDeviceSpec(
                "A11 Bionic", "Hexa-core", "3 GB", "5.8\" OLED",
                "12 MP Dual", "Bluetooth 5.0", "Wi-Fi 802.11ac",
                "Face ID", "Lightning", "Fast charge", "iOS 17"
        ));

        DB.put("iPhone 11", new AppleDeviceSpec(
                "A13 Bionic", "Hexa-core", "4 GB", "6.1\" Liquid Retina",
                "12 MP Dual", "Bluetooth 5.0", "Wi-Fi 6",
                "Face ID", "Lightning", "Fast charge", "iOS 17"
        ));

        DB.put("iPhone 13", new AppleDeviceSpec(
                "A15 Bionic", "Hexa-core", "4 GB", "6.1\" OLED",
                "12 MP Dual", "Bluetooth 5.0", "Wi-Fi 6",
                "Face ID", "Lightning", "Fast charge", "iOS 17"
        ));

        DB.put("iPhone 14", new AppleDeviceSpec(
                "A15 Bionic", "Hexa-core", "6 GB", "6.1\" OLED",
                "12 MP Dual", "Bluetooth 5.3", "Wi-Fi 6",
                "Face ID", "Lightning", "Fast charge", "iOS 17"
        ));

        DB.put("iPhone 15", new AppleDeviceSpec(
                "A16 Bionic", "Hexa-core", "6 GB", "6.1\" OLED",
                "48 MP", "Bluetooth 5.3", "Wi-Fi 6E",
                "Face ID", "USB-C", "Fast charge", "iOS 17"
        ));

        DB.put("iPad 9", new AppleDeviceSpec(
                "A13 Bionic", "Hexa-core", "3 GB", "10.2\" Retina",
                "8 MP", "Bluetooth 4.2", "Wi-Fi 5",
                "Touch ID", "Lightning", "Fast charge", "iPadOS 17"
        ));

        DB.put("iPad Air 5", new AppleDeviceSpec(
                "M1", "Octa-core", "8 GB", "10.9\" Liquid Retina",
                "12 MP", "Bluetooth 5.0", "Wi-Fi 6",
                "Touch ID", "USB-C", "Fast charge", "iPadOS 17"
        ));

        DB.put("iPad Pro 11", new AppleDeviceSpec(
                "M2", "Octa-core", "8 GB", "11\" Liquid Retina XDR",
                "12 MP", "Bluetooth 5.3", "Wi-Fi 6E",
                "Face ID", "USB-C / Thunderbolt", "Fast charge", "iPadOS 17"
        ));
    }

    public static AppleDeviceSpec get(String model) {
        AppleDeviceSpec s = DB.get(model);
        return s != null ? s : AppleDeviceSpec.unknown();
    }
}
