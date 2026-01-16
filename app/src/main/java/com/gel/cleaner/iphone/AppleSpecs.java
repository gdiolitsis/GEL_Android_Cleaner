// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleSpecs.java — LOCKED KNOWLEDGE BASE (FINAL)

package com.gel.cleaner.iphone;

public final class AppleSpecs {

    // =========================================================
    // ======================= IPHONE ==========================
    // =========================================================

    public static AppleDeviceSpec iPhone8() {
        AppleDeviceSpec d = baseIPhone("iPhone 8", "2017");
        d.soc = "A11 Bionic";
        d.cpu = "Apple A11";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 3;
        d.ram = "2 GB";
        d.display = "Retina HD LCD";
        d.screen = "LCD";
        d.resolution = "1334x750";
        d.refreshRate = "60 Hz";
        d.port = "Lightning";
        d.hasTouchID = true;
        d.hasFaceID = false;
        return d;
    }

    public static AppleDeviceSpec iPhoneX() {
        AppleDeviceSpec d = baseIPhone("iPhone X", "2017");
        d.soc = "A11 Bionic";
        d.cpu = "Apple A11";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 3;
        d.ram = "3 GB";
        d.display = "Super Retina OLED";
        d.screen = "OLED";
        d.resolution = "2436x1125";
        d.hasFaceID = true;
        return d;
    }

    public static AppleDeviceSpec iPhoneXR() {
        AppleDeviceSpec d = baseIPhone("iPhone XR", "2018");
        d.soc = "A12 Bionic";
        d.cpu = "Apple A12";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 4;
        d.ram = "3 GB";
        d.display = "Liquid Retina LCD";
        d.screen = "LCD";
        return d;
    }

    public static AppleDeviceSpec iPhone11() {
        AppleDeviceSpec d = baseIPhone("iPhone 11", "2019");
        d.soc = "A13 Bionic";
        d.cpu = "Apple A13";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 4;
        d.ram = "4 GB";
        return d;
    }

    public static AppleDeviceSpec iPhone12() {
        AppleDeviceSpec d = baseIPhone("iPhone 12", "2020");
        d.soc = "A14 Bionic";
        d.cpu = "Apple A14";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 4;
        d.has5G = true;
        return d;
    }

    public static AppleDeviceSpec iPhone13() {
        AppleDeviceSpec d = baseIPhone("iPhone 13", "2021");
        d.soc = "A15 Bionic";
        d.cpu = "Apple A15";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 4;
        d.has5G = true;
        return d;
    }

    public static AppleDeviceSpec iPhone14() {
        AppleDeviceSpec d = baseIPhone("iPhone 14", "2022");
        d.soc = "A15 Bionic";
        d.cpu = "Apple A15";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 5;
        d.has5G = true;
        return d;
    }

    public static AppleDeviceSpec iPhone15() {
        AppleDeviceSpec d = baseIPhone("iPhone 15", "2023");
        d.soc = "A16 Bionic";
        d.cpu = "Apple A16";
        d.gpu = "Apple GPU";
        d.cpuCores = 6;
        d.gpuCores = 5;
        d.port = "USB-C";
        d.has5G = true;
        return d;
    }

    // =========================================================
    // ======================== IPAD ===========================
    // =========================================================

    public static AppleDeviceSpec iPad7() {
        AppleDeviceSpec d = baseIPad("iPad 7", "2019");
        d.soc = "A10 Fusion";
        d.ram = "3 GB";
        return d;
    }

    public static AppleDeviceSpec iPad8() {
        AppleDeviceSpec d = baseIPad("iPad 8", "2020");
        d.soc = "A12 Bionic";
        d.ram = "3 GB";
        return d;
    }

    public static AppleDeviceSpec iPad9() {
        AppleDeviceSpec d = baseIPad("iPad 9", "2021");
        d.soc = "A13 Bionic";
        d.ram = "3 GB";
        return d;
    }

    public static AppleDeviceSpec iPadAir4() {
        AppleDeviceSpec d = baseIPad("iPad Air 4", "2020");
        d.soc = "A14 Bionic";
        d.ram = "4 GB";
        return d;
    }

    public static AppleDeviceSpec iPadAir5() {
        AppleDeviceSpec d = baseIPad("iPad Air 5", "2022");
        d.soc = "M1";
        d.ram = "8 GB";
        return d;
    }

    public static AppleDeviceSpec iPadPro11() {
        AppleDeviceSpec d = baseIPad("iPad Pro 11", "2022");
        d.soc = "M2";
        d.ram = "8 / 16 GB";
        return d;
    }

    public static AppleDeviceSpec iPadPro129() {
        AppleDeviceSpec d = baseIPad("iPad Pro 12.9", "2022");
        d.soc = "M2";
        d.ram = "8 / 16 GB";
        return d;
    }

    // =========================================================
    // ======================= BASE ============================
    // =========================================================

    private static AppleDeviceSpec baseIPhone(String model, String year) {
        AppleDeviceSpec d = new AppleDeviceSpec("iphone", model);
        d.year = year;
        d.os = "iOS";
        d.ramType = "LPDDR";
        d.storageOptions = "64 / 128 / 256 / 512";
        d.wifi = "Wi-Fi";
        d.bluetooth = "Bluetooth";
        d.hasLTE = true;
        d.hasFastCharge = true;
        d.hasWirelessCharge = true;
        d.speakers = "Stereo";
        d.microphones = "Dual";
        d.hasDolby = true;
        d.hasAirDrop = true;
        d.hasAirPlay = true;
        d.hasNFC = true;
        d.hasAccel = true;
        d.hasGyro = true;
        d.hasCompass = true;
        return d;
    }

    private static AppleDeviceSpec baseIPad(String model, String year) {
        AppleDeviceSpec d = new AppleDeviceSpec("ipad", model);
        d.year = year;
        d.os = "iPadOS";
        d.wifi = "Wi-Fi";
        d.bluetooth = "Bluetooth";
        d.speakers = "Stereo";
        d.microphones = "Dual";
        d.hasAccel = true;
        d.hasGyro = true;
        d.hasCompass = true;
        d.hasAirDrop = true;
        d.hasAirPlay = true;
        return d;
    }
}
