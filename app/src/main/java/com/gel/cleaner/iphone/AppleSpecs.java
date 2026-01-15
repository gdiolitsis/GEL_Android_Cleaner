package com.gel.cleaner.iphone;

import com.gel.cleaner.iphone.AppleDeviceSpec;

public class AppleSpecs {

    public static AppleDeviceSpec get(String model) {

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
