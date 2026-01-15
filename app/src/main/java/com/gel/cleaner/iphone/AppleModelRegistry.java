σεpackage com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

import com.gel.cleaner.iphone.specs.AppleDeviceSpec;

public final class AppleModelRegistry {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        // =========================================================
        // iPhone 8
        // =========================================================
        DB.put("iPhone 8", new AppleDeviceSpec(
                // ---- internals ----
                "A11 Bionic", "Hexa-core", "2 GB", "4.7\" Retina",
                "Wi-Fi 802.11ac", "Bluetooth 5.0", "Touch ID",
                "Lightning", "Fast charge", "iOS 17",

                // ---- camera ----
                "12 MP", "—", "—", "7 MP", "4K@60fps",

                // ---- modem ----
                "Intel LTE", false, true,
                "1", false,

                // ---- connectivity ----
                true, true,

                // ---- sensors ----
                "GPS, GLONASS", true, true,
                true, true,

                // ---- audio ----
                "Stereo", false,
                "Dual mic", true,

                // ---- ports ----
                "USB 2.0", true,
                true,

                // ---- biometrics ----
                false, true,

                // ---- display ----
                "Lightning Digital AV", true,

                // ---- storage ----
                "64 / 128 / 256 GB"
        ));

        // =========================================================
        // iPhone X
        // =========================================================
        DB.put("iPhone X", new AppleDeviceSpec(
                "A11 Bionic", "Hexa-core", "3 GB", "5.8\" OLED",
                "Wi-Fi 802.11ac", "Bluetooth 5.0", "Face ID",
                "Lightning", "Fast charge", "iOS 17",

                "12 MP", "—", "12 MP Tele", "7 MP", "4K@60fps",

                "Intel LTE", false, true,
                "1", false,

                true, true,

                "GPS, GLONASS", true, true,
                true, true,

                "Stereo", false,
                "Dual mic", true,

                "USB 2.0", true,
                true,

                true, false,

                "Lightning Digital AV", true,

                "64 / 256 GB"
        ));

        // =========================================================
        // iPhone 11
        // =========================================================
        DB.put("iPhone 11", new AppleDeviceSpec(
                "A13 Bionic", "Hexa-core", "4 GB", "6.1\" Liquid Retina",
                "Wi-Fi 6", "Bluetooth 5.0", "Face ID",
                "Lightning", "Fast charge", "iOS 17",

                "12 MP", "12 MP Ultra-Wide", "—", "12 MP", "4K@60fps",

                "Intel LTE", false, true,
                "1", true,

                true, true,

                "GPS, GLONASS", true, true,
                true, true,

                "Stereo", true,
                "Dual mic", true,

                "USB 2.0", true,
                true,

                true, false,

                "Lightning Digital AV", true,

                "64 / 128 / 256 GB"
        ));

        // =========================================================
        // iPhone 13
        // =========================================================
        DB.put("iPhone 13", new AppleDeviceSpec(
                "A15 Bionic", "Hexa-core", "4 GB", "6.1\" OLED",
                "Wi-Fi 6", "Bluetooth 5.0", "Face ID",
                "Lightning", "Fast charge", "iOS 17",

                "12 MP", "12 MP Ultra-Wide", "—", "12 MP", "4K@60fps",

                "Qualcomm 5G", true, true,
                "1", true,

                true, true,

                "GPS, GLONASS", true, true,
                true, true,

                "Stereo", true,
                "Dual mic", true,

                "USB 2.0", true,
                true,

                true, false,

                "Lightning Digital AV", true,

                "128 / 256 / 512 GB"
        ));

        // =========================================================
        // iPhone 14
        // =========================================================
        DB.put("iPhone 14", new AppleDeviceSpec(
                "A15 Bionic", "Hexa-core", "6 GB", "6.1\" OLED",
                "Wi-Fi 6", "Bluetooth 5.3", "Face ID",
                "Lightning", "Fast charge", "iOS 17",

                "12 MP", "12 MP Ultra-Wide", "—", "12 MP", "4K@60fps",

                "Qualcomm 5G", true, true,
                "1", true,

                true, true,

                "GPS, GLONASS", true, true,
                true, true,

                "Stereo", true,
                "Dual mic", true,

                "USB 2.0", true,
                true,

                true, false,

                "Lightning Digital AV", true,

                "128 / 256 / 512 GB"
        ));

        // =========================================================
        // iPhone 15
        // =========================================================
        DB.put("iPhone 15", new AppleDeviceSpec(
                "A16 Bionic", "Hexa-core", "6 GB", "6.1\" OLED",
                "Wi-Fi 6E", "Bluetooth 5.3", "Face ID",
                "USB-C", "Fast charge", "iOS 17",

                "48 MP", "12 MP Ultra-Wide", "—", "12 MP", "4K@60fps",

                "Qualcomm 5G", true, true,
                "1", true,

                true, true,

                "GPS, GLONASS", true, true,
                true, true,

                "Stereo", true,
                "Dual mic", true,

                "USB-C", true,
                true,

                true, false,

                "USB-C DisplayPort", true,

                "128 / 256 / 512 GB"
        ));
    }

// =========================================================
// iPad Pro 11" (M2)
// =========================================================
DB.put("iPad Pro 11", new AppleDeviceSpec(
        // ---- internals ----
        "M2", "Octa-core", "8 GB", "11\" Liquid Retina",
        "Wi-Fi 6E", "Bluetooth 5.3", "Face ID",
        "USB-C / Thunderbolt", "Fast charge", "iPadOS 17",

        // ---- camera ----
        "12 MP", "10 MP Ultra-Wide", "—", "12 MP", "4K@60fps",

        // ---- modem ----
        "Qualcomm 5G", true, true,
        "1", true,

        // ---- connectivity ----
        true, true,

        // ---- sensors ----
        "GPS, GLONASS", true, true,
        true, true,

        // ---- audio ----
        "Quad speakers", true,
        "Dual mic", false,

        // ---- ports ----
        "Thunderbolt 4", true,
        true,

        // ---- biometrics ----
        true, false,

        // ---- display ----
        "USB-C DisplayPort", true,

        // ---- storage ----
        "128 / 256 / 512 / 1TB / 2TB"
));
    
    public static AppleDeviceSpec get(String model) {
        AppleDeviceSpec s = DB.get(model);
        return s != null ? s : AppleDeviceSpec.unknown();
    }
}
