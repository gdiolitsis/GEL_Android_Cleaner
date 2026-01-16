package com.gel.cleaner.iphone;

public class AppleSpecs {

    public static AppleDeviceSpec get(String model) {
        
        if ("iPhone 12".equals(model)) {
    return new AppleDeviceSpec(
        "A14 Bionic", "Hexa-core", "4 GB", "6.1\" OLED",
        "Wi-Fi 6", "Bluetooth 5.0", "Face ID",
        "Lightning", "Fast charge", "iOS 17",

        "12 MP", "12 MP", "—", "12 MP", "4K@60fps",

        "Qualcomm X55", true, true,
        "1 SIM", true,

        true, true,

        "GPS + GLONASS", true, true,
        true, true,

        "Stereo", true,
        "Dual", false,

        "USB 2.0", true,
        true,

        true, false,

        "Lightning Digital AV", true,

        "64/128/256 GB"
    );
}
        
        if ("iPhone 13".equals(model)) {
            return new AppleDeviceSpec(
                "Apple A15 Bionic",
                "6-core CPU (2P + 4E)",
                "4 GB",
                "Super Retina XDR OLED",
                "12 MP Dual Camera",
                "Bluetooth 5.0",
                "Wi-Fi 6",
                "Face ID",
                "Lightning",
                "MagSafe",
                "iOS 15+"
            );
        }

        if ("iPhone 14".equals(model)) {
            return new AppleDeviceSpec(
                "Apple A15 Bionic",
                "6-core CPU (2P + 4E)",
                "6 GB",
                "Super Retina XDR OLED",
                "12 MP Dual Camera",
                "Bluetooth 5.3",
                "Wi-Fi 6",
                "Face ID",
                "Lightning",
                "MagSafe",
                "iOS 16+"
            );
        }

        // fallback
        return AppleDeviceSpec.unknown();
    }
}
