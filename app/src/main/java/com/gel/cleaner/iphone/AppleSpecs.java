// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleSpecs.java — FINAL HARDCODED DATASET

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public final class AppleSpecs {

    private static final Map<String, AppleDeviceSpec> DB = new HashMap<>();

    static {

        // =========================================================
        // iPhone 12
        // =========================================================
        AppleDeviceSpec i12 = new AppleDeviceSpec();
        i12.model = "iPhone 12";
        i12.deviceType = "iphone";
        i12.releaseYear = 2020;

        i12.os = "iOS";
        i12.osBase = "iOS 14";
        i12.osLatest = "iOS 17";

        i12.chip = "Apple A14 Bionic";
        i12.arch = "ARMv8";
        i12.processNode = "5 nm";
        i12.cpuCores = 6;

        i12.gpu = "Apple GPU";
        i12.gpuCores = 4;
        i12.metalFeatureSet = "Metal 2.3";

        i12.ram = "4 GB";
        i12.ramType = "LPDDR4X";
        i12.storageBase = "64 GB";
        i12.storageOptions = "64 / 128 / 256 GB";

        i12.modem = "Qualcomm X55";
        i12.simSlots = "1 physical";
        i12.hasESim = true;
        i12.has5G = true;
        i12.hasLTE = true;

        i12.wifi = "Wi-Fi 6";
        i12.bluetooth = "Bluetooth 5.0";
        i12.hasAirDrop = true;
        i12.hasNFC = true;

        i12.gps = "GPS, GLONASS, Galileo, QZSS";
        i12.hasCompass = true;
        i12.hasGyro = true;
        i12.hasAccel = true;
        i12.hasBarometer = true;

        i12.speakers = 2;
        i12.microphones = 2;
        i12.hasDolby = true;
        i12.hasJack = false;

        i12.port = "Lightning";
        i12.usbStandard = "USB 2.0";
        i12.hasFastCharge = true;
        i12.hasWirelessCharge = true;

        i12.displayOut = "Lightning Digital AV";
        i12.hasAirPlay = true;

        i12.hasFaceID = true;
        i12.hasTouchID = false;

        i12.charging = "Fast charge supported";
        i12.thermalNote = "Passive cooling";

        DB.put(i12.model, i12);

        // =========================================================
        // iPhone 13
        // =========================================================
        AppleDeviceSpec i13 = new AppleDeviceSpec();
        i13.model = "iPhone 13";
        i13.deviceType = "iphone";
        i13.releaseYear = 2021;

        i13.os = "iOS";
        i13.osBase = "iOS 15";
        i13.osLatest = "iOS 17";

        i13.chip = "Apple A15 Bionic";
        i13.arch = "ARMv8";
        i13.processNode = "5 nm";
        i13.cpuCores = 6;

        i13.gpu = "Apple GPU";
        i13.gpuCores = 4;
        i13.metalFeatureSet = "Metal 2.3";

        i13.ram = "4 GB";
        i13.ramType = "LPDDR4X";
        i13.storageBase = "128 GB";
        i13.storageOptions = "128 / 256 / 512 GB";

        i13.modem = "Qualcomm X60";
        i13.simSlots = "1 physical";
        i13.hasESim = true;
        i13.has5G = true;
        i13.hasLTE = true;

        i13.wifi = "Wi-Fi 6";
        i13.bluetooth = "Bluetooth 5.0";
        i13.hasAirDrop = true;
        i13.hasNFC = true;

        i13.gps = "GPS, GLONASS, Galileo, QZSS";
        i13.hasCompass = true;
        i13.hasGyro = true;
        i13.hasAccel = true;
        i13.hasBarometer = true;

        i13.speakers = 2;
        i13.microphones = 2;
        i13.hasDolby = true;
        i13.hasJack = false;

        i13.port = "Lightning";
        i13.usbStandard = "USB 2.0";
        i13.hasFastCharge = true;
        i13.hasWirelessCharge = true;

        i13.displayOut = "Lightning Digital AV";
        i13.hasAirPlay = true;

        i13.hasFaceID = true;
        i13.hasTouchID = false;

        i13.charging = "Fast charge supported";
        i13.thermalNote = "Passive cooling";

        DB.put(i13.model, i13);
    }

    // ============================================================
    // PUBLIC ACCESS
    // ============================================================
    public static AppleDeviceSpec get(String model) {
        return DB.get(model);
    }

    private AppleSpecs() {}
}
