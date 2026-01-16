// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleSpecs.java — FINAL STATIC REGISTRY

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public class AppleSpecs {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        // =====================================================
        // iPhone
        // =====================================================
        DB.put("iPhone 13", iPhone13());
        DB.put("iPhone 14", iPhone14());
        DB.put("iPhone 15", iPhone15());

        // =====================================================
        // iPad
        // =====================================================
        DB.put("iPad 9", iPad9());
        DB.put("iPad Air 5", iPadAir5());
        DB.put("iPad Pro 11", iPadPro11());
        DB.put("iPad Pro 12.9", iPadPro129());
    }

    // =====================================================
    // PUBLIC ACCESS
    // =====================================================
    public static AppleDeviceSpec get(String model) {
        AppleDeviceSpec d = DB.get(model);
        return d != null ? d : AppleDeviceSpec.unknown();
    }

    // =====================================================
    // iPHONE MODELS
    // =====================================================
    private static AppleDeviceSpec iPhone13() {
        AppleDeviceSpec d = new AppleDeviceSpec("iphone", "iPhone 13");
        d.year = "2021";
        d.os = "iOS";
        d.soc = "A15 Bionic";
        d.arch = "ARMv8";
        d.processNode = "5 nm";
        d.cpu = "Hexa-core";
        d.cpuCores = 6;
        d.gpu = "Apple GPU";
        d.gpuCores = 4;
        d.ram = "4 GB";
        d.storageOptions = "128 / 256 / 512 GB";
        d.display = "6.1 OLED";
        d.resolution = "2532x1170";
        d.refreshRate = "60 Hz";
        d.has5G = true;
        d.modem = "Qualcomm X60";
        d.wifi = "Wi-Fi 6";
        d.bluetooth = "5.0";
        d.speakers = "Stereo";
        d.microphones = "2";
        d.port = "Lightning";
        d.hasFaceID = true;
        d.cameraMain = "12 MP";
        d.cameraUltraWide = "12 MP";
        d.cameraFront = "12 MP";
        d.cameraVideo = "4K60";
        return d;
    }

    private static AppleDeviceSpec iPhone14() {
        AppleDeviceSpec d = iPhone13();
        d.model = "iPhone 14";
        d.year = "2022";
        return d;
    }

    private static AppleDeviceSpec iPhone15() {
        AppleDeviceSpec d = new AppleDeviceSpec("iphone", "iPhone 15");
        d.year = "2023";
        d.os = "iOS";
        d.soc = "A16 Bionic";
        d.arch = "ARMv8";
        d.processNode = "4 nm";
        d.cpu = "Hexa-core";
        d.cpuCores = 6;
        d.gpu = "Apple GPU";
        d.gpuCores = 5;
        d.ram = "6 GB";
        d.storageOptions = "128 / 256 / 512 GB";
        d.display = "6.1 OLED";
        d.refreshRate = "60 Hz";
        d.has5G = true;
        d.port = "USB-C";
        d.speakers = "Stereo";
        d.microphones = "2";
        d.cameraMain = "48 MP";
        d.cameraUltraWide = "12 MP";
        d.cameraFront = "12 MP";
        d.cameraVideo = "4K60";
        return d;
    }

    // =====================================================
    // iPAD MODELS
    // =====================================================
    private static AppleDeviceSpec iPad9() {
        AppleDeviceSpec d = new AppleDeviceSpec("ipad", "iPad 9");
        d.year = "2021";
        d.soc = "A13 Bionic";
        d.ram = "3 GB";
        d.display = "10.2 LCD";
        d.port = "Lightning";
        d.speakers = "Stereo";
        return d;
    }

    private static AppleDeviceSpec iPadAir5() {
        AppleDeviceSpec d = new AppleDeviceSpec("ipad", "iPad Air 5");
        d.year = "2022";
        d.soc = "M1";
        d.ram = "8 GB";
        d.display = "10.9 Liquid Retina";
        d.port = "USB-C";
        return d;
    }

    private static AppleDeviceSpec iPadPro11() {
        AppleDeviceSpec d = new AppleDeviceSpec("ipad", "iPad Pro 11");
        d.soc = "M2";
        d.display = "11 Liquid Retina";
        d.port = "USB-C / Thunderbolt";
        return d;
    }

    private static AppleDeviceSpec iPadPro129() {
        AppleDeviceSpec d = new AppleDeviceSpec("ipad", "iPad Pro 12.9");
        d.soc = "M2";
        d.display = "12.9 Mini-LED";
        d.port = "USB-C / Thunderbolt";
        return d;
    }
}
