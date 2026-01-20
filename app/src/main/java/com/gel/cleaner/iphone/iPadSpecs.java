// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// iPadSpecs.java — LOCKED KNOWLEDGE BASE (PRODUCTION)
// Covers modern iPad models only (2018+)
// ============================================================

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public final class iPadSpecs {

    // =========================================================
    // INTERNAL MAP
    // =========================================================
    private static final Map<String, AppleDeviceSpec> MAP = new HashMap<>();

    // =========================================================
    // STATIC INIT
    // =========================================================
    static {

        // -----------------------------------------------------
        // iPad Pro 12.9" (M2)
        // -----------------------------------------------------
        AppleDeviceSpec ipadPro129M2 = new AppleDeviceSpec();
        ipadPro129M2.type = "ipad";
        ipadPro129M2.model = "iPad Pro 12.9 (M2)";
        ipadPro129M2.year = "2022";
        ipadPro129M2.identifier = "iPad14,6 / iPad14,5";
        ipadPro129M2.modelNumber = "A2436 / A2437 / A2764";

        ipadPro129M2.os = "iPadOS";
        ipadPro129M2.soc = "Apple M2";
        ipadPro129M2.chipset = "M2";
        ipadPro129M2.processNode = "5 nm";
        ipadPro129M2.cpu = "Apple CPU";
        ipadPro129M2.cpuCores = 8;
        ipadPro129M2.gpu = "Apple GPU";
        ipadPro129M2.gpuCores = 10;
        ipadPro129M2.metalFeatureSet = "Metal 3";

        ipadPro129M2.ram = "8 GB / 16 GB";
        ipadPro129M2.ramType = "Unified Memory";
        ipadPro129M2.storageBase = "128 GB";
        ipadPro129M2.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

        ipadPro129M2.screen = "12.9\"";
        ipadPro129M2.display = "Liquid Retina XDR (mini-LED)";
        ipadPro129M2.resolution = "2732 × 2048";
        ipadPro129M2.refreshRate = "ProMotion 120 Hz";
        ipadPro129M2.displayOut = "Thunderbolt / USB-C DisplayPort";

        ipadPro129M2.has5G = true;
        ipadPro129M2.hasLTE = true;
        ipadPro129M2.cellular = "5G / LTE";
        ipadPro129M2.modem = "Apple / Qualcomm";
        ipadPro129M2.wifi = "Wi-Fi 6E";
        ipadPro129M2.bluetooth = "Bluetooth 5.3";
        ipadPro129M2.hasNFC = false;
        ipadPro129M2.hasAirDrop = true;
        ipadPro129M2.hasAirPlay = true;
        ipadPro129M2.gps = "GPS / GNSS (Cellular models)";
        ipadPro129M2.hasCompass = true;
        ipadPro129M2.hasGyro = true;
        ipadPro129M2.hasAccel = true;
        ipadPro129M2.hasBarometer = true;

        ipadPro129M2.simSlots = "Nano-SIM + eSIM";
        ipadPro129M2.hasESim = true;
        ipadPro129M2.port = "USB-C / Thunderbolt";
        ipadPro129M2.usbStandard = "Thunderbolt 4";

        ipadPro129M2.speakers = "4-speaker audio";
        ipadPro129M2.microphones = "Studio-quality microphones";
        ipadPro129M2.hasDolby = true;
        ipadPro129M2.hasJack = false;

        ipadPro129M2.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
        ipadPro129M2.cameraFront = "12 MP Ultra-Wide (Center Stage)";
        ipadPro129M2.cameraVideo = "4K@60fps HDR";

        ipadPro129M2.hasFaceID = true;
        ipadPro129M2.hasTouchID = false;
        ipadPro129M2.biometrics = "Face ID";

        ipadPro129M2.hasFastCharge = true;
        ipadPro129M2.hasWirelessCharge = false;

        ipadPro129M2.thermalNote =
                "No public access to thermal sensors (Apple restriction)";
        ipadPro129M2.notes =
                "Performance and thermal data limited by iPadOS sandboxing";

        MAP.put("ipad pro 12.9 m2", ipadPro129M2);

// -----------------------------------------------------
// iPad Pro 11" (M2)
// -----------------------------------------------------
AppleDeviceSpec ipadPro11M2 = new AppleDeviceSpec();
ipadPro11M2.type = "ipad";
ipadPro11M2.model = "iPad Pro 11 (M2)";
ipadPro11M2.year = "2022";
ipadPro11M2.identifier = "iPad14,4 / iPad14,3";
ipadPro11M2.modelNumber = "A2435 / A2759 / A2761";

ipadPro11M2.os = "iPadOS";
ipadPro11M2.soc = "Apple M2";
ipadPro11M2.chipset = "M2";
ipadPro11M2.processNode = "5 nm";
ipadPro11M2.cpu = "Apple CPU";
ipadPro11M2.cpuCores = 8;
ipadPro11M2.gpu = "Apple GPU";
ipadPro11M2.gpuCores = 10;
ipadPro11M2.metalFeatureSet = "Metal 3";

ipadPro11M2.ram = "8 GB / 16 GB";
ipadPro11M2.ramType = "Unified Memory";
ipadPro11M2.storageBase = "128 GB";
ipadPro11M2.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

ipadPro11M2.screen = "11\"";
ipadPro11M2.display = "Liquid Retina (IPS)";
ipadPro11M2.resolution = "2388 × 1668";
ipadPro11M2.refreshRate = "ProMotion 120 Hz";
ipadPro11M2.displayOut = "Thunderbolt / USB-C DisplayPort";

ipadPro11M2.has5G = true;
ipadPro11M2.hasLTE = true;
ipadPro11M2.cellular = "5G / LTE";
ipadPro11M2.modem = "Apple / Qualcomm";
ipadPro11M2.wifi = "Wi-Fi 6E";
ipadPro11M2.bluetooth = "Bluetooth 5.3";
ipadPro11M2.hasNFC = false;
ipadPro11M2.hasAirDrop = true;
ipadPro11M2.hasAirPlay = true;
ipadPro11M2.gps = "GPS / GNSS (Cellular models)";
ipadPro11M2.hasCompass = true;
ipadPro11M2.hasGyro = true;
ipadPro11M2.hasAccel = true;
ipadPro11M2.hasBarometer = true;

ipadPro11M2.simSlots = "Nano-SIM + eSIM";
ipadPro11M2.hasESim = true;
ipadPro11M2.port = "USB-C / Thunderbolt";
ipadPro11M2.usbStandard = "Thunderbolt 4";

ipadPro11M2.speakers = "4-speaker audio";
ipadPro11M2.microphones = "Studio-quality microphones";
ipadPro11M2.hasDolby = true;
ipadPro11M2.hasJack = false;

ipadPro11M2.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
ipadPro11M2.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadPro11M2.cameraVideo = "4K@60fps HDR";

ipadPro11M2.hasFaceID = true;
ipadPro11M2.hasTouchID = false;
ipadPro11M2.biometrics = "Face ID";

ipadPro11M2.hasFastCharge = true;
ipadPro11M2.hasWirelessCharge = false;

ipadPro11M2.thermalNote =
        "No public access to thermal sensors (Apple restriction)";
ipadPro11M2.notes =
        "Performance and thermals constrained by iPadOS sandboxing";

MAP.put("ipad pro 11 m2", ipadPro11M2);

// -----------------------------------------------------
// iPad Pro 11" (M1)
// -----------------------------------------------------
AppleDeviceSpec ipadPro11M1 = new AppleDeviceSpec();
ipadPro11M1.type = "ipad";
ipadPro11M1.model = "iPad Pro 11 (M1)";
ipadPro11M1.year = "2021";
ipadPro11M1.identifier = "iPad13,4 / iPad13,5 / iPad13,6 / iPad13,7";
ipadPro11M1.modelNumber = "A2377 / A2459 / A2301 / A2460";

ipadPro11M1.os = "iPadOS";
ipadPro11M1.soc = "Apple M1";
ipadPro11M1.chipset = "M1";
ipadPro11M1.processNode = "5 nm";
ipadPro11M1.cpu = "Apple CPU";
ipadPro11M1.cpuCores = 8;
ipadPro11M1.gpu = "Apple GPU";
ipadPro11M1.gpuCores = 8;
ipadPro11M1.metalFeatureSet = "Metal 3";

ipadPro11M1.ram = "8 GB / 16 GB";
ipadPro11M1.ramType = "Unified Memory";
ipadPro11M1.storageBase = "128 GB";
ipadPro11M1.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

ipadPro11M1.screen = "11\"";
ipadPro11M1.display = "Liquid Retina (IPS)";
ipadPro11M1.resolution = "2388 × 1668";
ipadPro11M1.refreshRate = "ProMotion 120 Hz";
ipadPro11M1.displayOut = "USB-C DisplayPort";

ipadPro11M1.has5G = true;
ipadPro11M1.hasLTE = true;
ipadPro11M1.cellular = "5G / LTE";
ipadPro11M1.modem = "Qualcomm Snapdragon X55";
ipadPro11M1.wifi = "Wi-Fi 6";
ipadPro11M1.bluetooth = "Bluetooth 5.0";
ipadPro11M1.hasNFC = false;
ipadPro11M1.hasAirDrop = true;
ipadPro11M1.hasAirPlay = true;
ipadPro11M1.gps = "GPS / GNSS (Cellular models)";
ipadPro11M1.hasCompass = true;
ipadPro11M1.hasGyro = true;
ipadPro11M1.hasAccel = true;
ipadPro11M1.hasBarometer = true;

ipadPro11M1.simSlots = "Nano-SIM + eSIM";
ipadPro11M1.hasESim = true;
ipadPro11M1.port = "USB-C";
ipadPro11M1.usbStandard = "USB 4 (limited by iPadOS)";

ipadPro11M1.speakers = "4-speaker audio";
ipadPro11M1.microphones = "5 microphones";
ipadPro11M1.hasDolby = true;
ipadPro11M1.hasJack = false;

ipadPro11M1.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
ipadPro11M1.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadPro11M1.cameraVideo = "4K@60fps HDR";

ipadPro11M1.hasFaceID = true;
ipadPro11M1.hasTouchID = false;
ipadPro11M1.biometrics = "Face ID";

ipadPro11M1.hasFastCharge = true;
ipadPro11M1.hasWirelessCharge = false;

ipadPro11M1.thermalNote =
        "Thermal sensors not accessible to apps (Apple restriction)";
ipadPro11M1.notes =
        "M1 performance heavily sandboxed by iPadOS limitations";

MAP.put("ipad pro 11 m1", ipadPro11M1);

// -----------------------------------------------------
// iPad Pro 12.9" (M1)
// -----------------------------------------------------
AppleDeviceSpec ipadPro129M1 = new AppleDeviceSpec();
ipadPro129M1.type = "ipad";
ipadPro129M1.model = "iPad Pro 12.9 (M1)";
ipadPro129M1.year = "2021";
ipadPro129M1.identifier = "iPad13,8 / iPad13,9 / iPad13,10 / iPad13,11";
ipadPro129M1.modelNumber = "A2378 / A2461 / A2379 / A2462";

ipadPro129M1.os = "iPadOS";
ipadPro129M1.soc = "Apple M1";
ipadPro129M1.chipset = "M1";
ipadPro129M1.processNode = "5 nm";
ipadPro129M1.cpu = "Apple CPU";
ipadPro129M1.cpuCores = 8;
ipadPro129M1.gpu = "Apple GPU";
ipadPro129M1.gpuCores = 8;
ipadPro129M1.metalFeatureSet = "Metal 3";

ipadPro129M1.ram = "8 GB / 16 GB";
ipadPro129M1.ramType = "Unified Memory";
ipadPro129M1.storageBase = "128 GB";
ipadPro129M1.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

ipadPro129M1.screen = "12.9\"";
ipadPro129M1.display = "Liquid Retina XDR (mini-LED)";
ipadPro129M1.resolution = "2732 × 2048";
ipadPro129M1.refreshRate = "ProMotion 120 Hz";
ipadPro129M1.displayOut = "USB-C DisplayPort";

ipadPro129M1.has5G = true;
ipadPro129M1.hasLTE = true;
ipadPro129M1.cellular = "5G / LTE";
ipadPro129M1.modem = "Qualcomm Snapdragon X55";
ipadPro129M1.wifi = "Wi-Fi 6";
ipadPro129M1.bluetooth = "Bluetooth 5.0";
ipadPro129M1.hasNFC = false;
ipadPro129M1.hasAirDrop = true;
ipadPro129M1.hasAirPlay = true;
ipadPro129M1.gps = "GPS / GNSS (Cellular models)";
ipadPro129M1.hasCompass = true;
ipadPro129M1.hasGyro = true;
ipadPro129M1.hasAccel = true;
ipadPro129M1.hasBarometer = true;

ipadPro129M1.simSlots = "Nano-SIM + eSIM";
ipadPro129M1.hasESim = true;
ipadPro129M1.port = "USB-C";
ipadPro129M1.usbStandard = "USB 4 (limited by iPadOS)";

ipadPro129M1.speakers = "4-speaker audio";
ipadPro129M1.microphones = "5 microphones";
ipadPro129M1.hasDolby = true;
ipadPro129M1.hasJack = false;

ipadPro129M1.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
ipadPro129M1.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadPro129M1.cameraVideo = "4K@60fps HDR";

ipadPro129M1.hasFaceID = true;
ipadPro129M1.hasTouchID = false;
ipadPro129M1.biometrics = "Face ID";

ipadPro129M1.hasFastCharge = true;
ipadPro129M1.hasWirelessCharge = false;

ipadPro129M1.thermalNote =
        "Mini-LED display introduces localized thermal zones";
ipadPro129M1.notes =
        "Highest sustained brightness among iPads, OS-limited performance";

MAP.put("ipad pro 12.9 m1", ipadPro129M1);

// -----------------------------------------------------
// iPad Air (M1)
// -----------------------------------------------------
AppleDeviceSpec ipadAirM1 = new AppleDeviceSpec();
ipadAirM1.type = "ipad";
ipadAirM1.model = "iPad Air (M1)";
ipadAirM1.year = "2022";
ipadAirM1.identifier = "iPad13,16 / iPad13,17";
ipadAirM1.modelNumber = "A2588 / A2589 / A2591";

ipadAirM1.os = "iPadOS";
ipadAirM1.soc = "Apple M1";
ipadAirM1.chipset = "M1";
ipadAirM1.processNode = "5 nm";
ipadAirM1.cpu = "Apple CPU";
ipadAirM1.cpuCores = 8;
ipadAirM1.gpu = "Apple GPU";
ipadAirM1.gpuCores = 8;
ipadAirM1.metalFeatureSet = "Metal 3";

ipadAirM1.ram = "8 GB";
ipadAirM1.ramType = "Unified Memory";
ipadAirM1.storageBase = "64 GB";
ipadAirM1.storageOptions = "64 / 256 GB";

ipadAirM1.screen = "10.9\"";
ipadAirM1.display = "Liquid Retina (IPS)";
ipadAirM1.resolution = "2360 × 1640";
ipadAirM1.refreshRate = "60 Hz";
ipadAirM1.displayOut = "USB-C DisplayPort";

ipadAirM1.has5G = true;
ipadAirM1.hasLTE = true;
ipadAirM1.cellular = "5G / LTE";
ipadAirM1.modem = "Qualcomm Snapdragon X55";
ipadAirM1.wifi = "Wi-Fi 6";
ipadAirM1.bluetooth = "Bluetooth 5.0";
ipadAirM1.hasNFC = false;
ipadAirM1.hasAirDrop = true;
ipadAirM1.hasAirPlay = true;
ipadAirM1.gps = "GPS / GNSS (Cellular models)";
ipadAirM1.hasCompass = true;
ipadAirM1.hasGyro = true;
ipadAirM1.hasAccel = true;
ipadAirM1.hasBarometer = true;

ipadAirM1.simSlots = "Nano-SIM + eSIM";
ipadAirM1.hasESim = true;
ipadAirM1.port = "USB-C";
ipadAirM1.usbStandard = "USB 3.1 Gen 1";

ipadAirM1.speakers = "Stereo speakers";
ipadAirM1.microphones = "Dual microphones";
ipadAirM1.hasDolby = true;
ipadAirM1.hasJack = false;

ipadAirM1.cameraMain = "12 MP Wide";
ipadAirM1.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadAirM1.cameraVideo = "4K@60fps";

ipadAirM1.hasFaceID = false;
ipadAirM1.hasTouchID = true;
ipadAirM1.biometrics = "Touch ID (Top Button)";

ipadAirM1.hasFastCharge = true;
ipadAirM1.hasWirelessCharge = false;

ipadAirM1.thermalNote =
        "Thermal sensors not accessible to applications (Apple restriction)";
ipadAirM1.notes =
        "M1 performance class, limited by iPadOS multitasking constraints";

MAP.put("ipad air m1", ipadAirM1);

// -----------------------------------------------------
// iPad Air 11 (M2)
// -----------------------------------------------------
AppleDeviceSpec ipadAir11M2 = new AppleDeviceSpec();
ipadAir11M2.type = "ipad";
ipadAir11M2.model = "iPad Air 11 (M2)";
ipadAir11M2.year = "2024";
ipadAir11M2.identifier = "iPad14,8 / iPad14,9";
ipadAir11M2.modelNumber = "A2902 / A2903";

ipadAir11M2.os = "iPadOS";
ipadAir11M2.soc = "Apple M2";
ipadAir11M2.chipset = "M2";
ipadAir11M2.processNode = "5 nm";
ipadAir11M2.cpu = "Apple CPU";
ipadAir11M2.cpuCores = 8;
ipadAir11M2.gpu = "Apple GPU";
ipadAir11M2.gpuCores = 9;
ipadAir11M2.metalFeatureSet = "Metal 3";

ipadAir11M2.ram = "8 GB";
ipadAir11M2.ramType = "Unified Memory";
ipadAir11M2.storageBase = "128 GB";
ipadAir11M2.storageOptions = "128 / 256 / 512 GB / 1 TB";

ipadAir11M2.screen = "11\"";
ipadAir11M2.display = "Liquid Retina (IPS)";
ipadAir11M2.resolution = "2360 × 1640";
ipadAir11M2.refreshRate = "60 Hz";
ipadAir11M2.displayOut = "USB-C DisplayPort";

ipadAir11M2.has5G = true;
ipadAir11M2.hasLTE = true;
ipadAir11M2.cellular = "5G / LTE";
ipadAir11M2.modem = "Qualcomm Snapdragon X65";
ipadAir11M2.wifi = "Wi-Fi 6E";
ipadAir11M2.bluetooth = "Bluetooth 5.3";
ipadAir11M2.hasNFC = false;
ipadAir11M2.hasAirDrop = true;
ipadAir11M2.hasAirPlay = true;
ipadAir11M2.gps = "GPS / GNSS (Cellular models)";
ipadAir11M2.hasCompass = true;
ipadAir11M2.hasGyro = true;
ipadAir11M2.hasAccel = true;
ipadAir11M2.hasBarometer = true;

ipadAir11M2.simSlots = "eSIM only";
ipadAir11M2.hasESim = true;
ipadAir11M2.port = "USB-C";
ipadAir11M2.usbStandard = "USB 3.1 Gen 2";

ipadAir11M2.speakers = "Stereo speakers";
ipadAir11M2.microphones = "Dual microphones";
ipadAir11M2.hasDolby = true;
ipadAir11M2.hasJack = false;

ipadAir11M2.cameraMain = "12 MP Wide";
ipadAir11M2.cameraFront = "12 MP Landscape Ultra-Wide (Center Stage)";
ipadAir11M2.cameraVideo = "4K@60fps";

ipadAir11M2.hasFaceID = false;
ipadAir11M2.hasTouchID = true;
ipadAir11M2.biometrics = "Touch ID (Top Button)";

ipadAir11M2.charging = "USB-C fast charge";
ipadAir11M2.hasWirelessCharge = false;

ipadAir11M2.thermalNote =
        "Thermal sensors not accessible to applications (Apple restriction)";
ipadAir11M2.notes =
        "M2 performance class without ProMotion or LiDAR; iPadOS limits pro workflows";

MAP.put("ipad air 11 m2", ipadAir11M2);

// -----------------------------------------------------
// iPad Air 13 (M2)
// -----------------------------------------------------
AppleDeviceSpec ipadAir13M2 = new AppleDeviceSpec();
ipadAir13M2.type = "ipad";
ipadAir13M2.model = "iPad Air 13 (M2)";
ipadAir13M2.year = "2024";
ipadAir13M2.identifier = "iPad14,10 / iPad14,11";
ipadAir13M2.modelNumber = "A2904 / A2905";

// OS / PLATFORM
ipadAir13M2.os = "iPadOS";

// SOC / CPU / GPU
ipadAir13M2.soc = "Apple M2";
ipadAir13M2.chipset = "M2";
ipadAir13M2.processNode = "5 nm";
ipadAir13M2.cpu = "Apple CPU";
ipadAir13M2.cpuCores = 8;
ipadAir13M2.gpu = "Apple GPU";
ipadAir13M2.gpuCores = 9;
ipadAir13M2.metalFeatureSet = "Metal 3";

// MEMORY / STORAGE
ipadAir13M2.ram = "8 GB";
ipadAir13M2.ramType = "Unified Memory";
ipadAir13M2.storageBase = "128 GB";
ipadAir13M2.storageOptions = "128 / 256 / 512 GB / 1 TB";

// DISPLAY
ipadAir13M2.screen = "13\"";
ipadAir13M2.display = "Liquid Retina (IPS)";
ipadAir13M2.resolution = "2732 × 2048";
ipadAir13M2.refreshRate = "60 Hz";
ipadAir13M2.displayOut = "USB-C DisplayPort";

// NETWORK / WIRELESS
ipadAir13M2.has5G = true;
ipadAir13M2.hasLTE = true;
ipadAir13M2.cellular = "5G / LTE";
ipadAir13M2.modem = "Qualcomm Snapdragon X65";
ipadAir13M2.wifi = "Wi-Fi 6E";
ipadAir13M2.bluetooth = "Bluetooth 5.3";
ipadAir13M2.hasNFC = false;
ipadAir13M2.hasAirDrop = true;
ipadAir13M2.hasAirPlay = true;
ipadAir13M2.gps = "GPS / GNSS (Cellular models)";
ipadAir13M2.hasCompass = true;
ipadAir13M2.hasGyro = true;
ipadAir13M2.hasAccel = true;
ipadAir13M2.hasBarometer = true;

// SIM / PORTS
ipadAir13M2.simSlots = "eSIM only";
ipadAir13M2.hasESim = true;
ipadAir13M2.port = "USB-C";
ipadAir13M2.usbStandard = "USB 3.1 Gen 2";

// AUDIO
ipadAir13M2.speakers = "Stereo speakers";
ipadAir13M2.microphones = "Dual microphones";
ipadAir13M2.hasDolby = true;
ipadAir13M2.hasJack = false;

// CAMERA
ipadAir13M2.cameraMain  = "12 MP Wide";
ipadAir13M2.cameraFront = "12 MP Landscape Ultra-Wide (Center Stage)";
ipadAir13M2.cameraVideo = "4K@60fps";

// BIOMETRICS
ipadAir13M2.hasFaceID = false;
ipadAir13M2.hasTouchID = true;
ipadAir13M2.biometrics = "Touch ID (Top Button)";

// POWER
ipadAir13M2.charging = "USB-C fast charge";
ipadAir13M2.hasWirelessCharge = false;

// THERMAL / NOTES
ipadAir13M2.thermalNote =
        "Thermal sensors not accessible to applications (Apple restriction)";
ipadAir13M2.notes =
        "13-inch Air fills the gap between Air and Pro; large display without ProMotion";

MAP.put("ipad air 13 m2", ipadAir13M2);

// ============================================================
// iPad mini (6th generation) — 2021
// ============================================================

AppleDeviceSpec ipadMini6 = new AppleDeviceSpec("ipad", "iPad mini 6");

ipadMini6.year          = "2021";
ipadMini6.identifier    = "iPad14,1 / iPad14,2";
ipadMini6.modelNumber   = "A2567 / A2568 / A2569";

// OS / PLATFORM
ipadMini6.os            = "iPadOS";
ipadMini6.charging      = "USB-C (20W fast charge)";

// SOC / CPU / GPU
ipadMini6.soc           = "A15 Bionic";
ipadMini6.chipset       = "A15 Bionic";
ipadMini6.arch          = "ARMv8.5-A";
ipadMini6.processNode   = "5 nm";
ipadMini6.cpu           = "Apple CPU";
ipadMini6.cpuCores      = 6;
ipadMini6.gpu           = "Apple GPU";
ipadMini6.gpuCores      = 5;
ipadMini6.metalFeatureSet = "Metal 3";

// MEMORY / STORAGE
ipadMini6.ram           = "4 GB";
ipadMini6.ramType       = "LPDDR4X";
ipadMini6.storageBase   = "64 GB";
ipadMini6.storageOptions= "64 GB / 256 GB";

// DISPLAY
ipadMini6.screen        = "8.3\"";
ipadMini6.display       = "Liquid Retina (IPS LCD)";
ipadMini6.resolution    = "2266 × 1488";
ipadMini6.refreshRate   = "60 Hz";
ipadMini6.displayOut    = "USB-C DisplayPort";

// NETWORK / WIRELESS
ipadMini6.has5G         = true;
ipadMini6.hasLTE        = true;
ipadMini6.cellular      = "5G (sub-6 GHz), LTE";
ipadMini6.modem         = "Qualcomm Snapdragon X60";
ipadMini6.wifi          = "Wi-Fi 6 (802.11ax)";
ipadMini6.bluetooth     = "Bluetooth 5.0";
ipadMini6.hasNFC        = false;
ipadMini6.hasAirDrop    = true;
ipadMini6.hasAirPlay    = true;
ipadMini6.gps           = "GPS / GNSS";
ipadMini6.hasCompass    = true;
ipadMini6.hasGyro       = true;
ipadMini6.hasAccel      = true;
ipadMini6.hasBarometer  = true;

// SIM / PORTS
ipadMini6.simSlots      = "Single SIM";
ipadMini6.hasESim       = true;
ipadMini6.port          = "USB-C";
ipadMini6.usbStandard   = "USB-C 3.1 Gen 1";

// AUDIO
ipadMini6.speakers      = "Stereo speakers";
ipadMini6.microphones   = "Dual microphones";
ipadMini6.hasDolby      = true;
ipadMini6.hasJack       = false;

// CAMERA
ipadMini6.cameraMain    = "12 MP Wide";
ipadMini6.cameraUltraWide = null;
ipadMini6.cameraTele    = null;
ipadMini6.cameraFront   = "12 MP Ultra-Wide (Center Stage)";
ipadMini6.cameraVideo   = "4K@60fps HDR";

// BIOMETRICS / FEATURES
ipadMini6.hasFaceID     = false;
ipadMini6.hasTouchID    = true;
ipadMini6.biometrics    = "Touch ID (Power button)";

// POWER
ipadMini6.hasFastCharge     = true;
ipadMini6.hasWirelessCharge= false;

// THERMAL / NOTES
ipadMini6.thermalNote  = "No public thermal sensors (Apple restriction)";
ipadMini6.notes        = "Apple Pencil 2 support, flat-edge design";

    }

    // GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// iPadSpecs.java — LOCKED KNOWLEDGE BASE (PRODUCTION)
// Covers modern iPad models only (2018+)
// ============================================================

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public final class iPadSpecs {

    // =========================================================
    // INTERNAL MAP
    // =========================================================
    private static final Map<String, AppleDeviceSpec> MAP = new HashMap<>();

    // =========================================================
    // STATIC INIT
    // =========================================================
    static {

        // -----------------------------------------------------
        // iPad Pro 12.9" (M2)
        // -----------------------------------------------------
        AppleDeviceSpec ipadPro129M2 = new AppleDeviceSpec();
        ipadPro129M2.type = "ipad";
        ipadPro129M2.model = "iPad Pro 12.9 (M2)";
        ipadPro129M2.year = "2022";
        ipadPro129M2.identifier = "iPad14,6 / iPad14,5";
        ipadPro129M2.modelNumber = "A2436 / A2437 / A2764";

        ipadPro129M2.os = "iPadOS";
        ipadPro129M2.soc = "Apple M2";
        ipadPro129M2.chipset = "M2";
        ipadPro129M2.processNode = "5 nm";
        ipadPro129M2.cpu = "Apple CPU";
        ipadPro129M2.cpuCores = 8;
        ipadPro129M2.gpu = "Apple GPU";
        ipadPro129M2.gpuCores = 10;
        ipadPro129M2.metalFeatureSet = "Metal 3";

        ipadPro129M2.ram = "8 GB / 16 GB";
        ipadPro129M2.ramType = "Unified Memory";
        ipadPro129M2.storageBase = "128 GB";
        ipadPro129M2.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

        ipadPro129M2.screen = "12.9\"";
        ipadPro129M2.display = "Liquid Retina XDR (mini-LED)";
        ipadPro129M2.resolution = "2732 × 2048";
        ipadPro129M2.refreshRate = "ProMotion 120 Hz";
        ipadPro129M2.displayOut = "Thunderbolt / USB-C DisplayPort";

        ipadPro129M2.has5G = true;
        ipadPro129M2.hasLTE = true;
        ipadPro129M2.cellular = "5G / LTE";
        ipadPro129M2.modem = "Apple / Qualcomm";
        ipadPro129M2.wifi = "Wi-Fi 6E";
        ipadPro129M2.bluetooth = "Bluetooth 5.3";
        ipadPro129M2.hasNFC = false;
        ipadPro129M2.hasAirDrop = true;
        ipadPro129M2.hasAirPlay = true;
        ipadPro129M2.gps = "GPS / GNSS (Cellular models)";
        ipadPro129M2.hasCompass = true;
        ipadPro129M2.hasGyro = true;
        ipadPro129M2.hasAccel = true;
        ipadPro129M2.hasBarometer = true;

        ipadPro129M2.simSlots = "Nano-SIM + eSIM";
        ipadPro129M2.hasESim = true;
        ipadPro129M2.port = "USB-C / Thunderbolt";
        ipadPro129M2.usbStandard = "Thunderbolt 4";

        ipadPro129M2.speakers = "4-speaker audio";
        ipadPro129M2.microphones = "Studio-quality microphones";
        ipadPro129M2.hasDolby = true;
        ipadPro129M2.hasJack = false;

        ipadPro129M2.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
        ipadPro129M2.cameraFront = "12 MP Ultra-Wide (Center Stage)";
        ipadPro129M2.cameraVideo = "4K@60fps HDR";

        ipadPro129M2.hasFaceID = true;
        ipadPro129M2.hasTouchID = false;
        ipadPro129M2.biometrics = "Face ID";

        ipadPro129M2.hasFastCharge = true;
        ipadPro129M2.hasWirelessCharge = false;

        ipadPro129M2.thermalNote =
                "No public access to thermal sensors (Apple restriction)";
        ipadPro129M2.notes =
                "Performance and thermal data limited by iPadOS sandboxing";

        MAP.put("ipad pro 12.9 m2", ipadPro129M2);

// -----------------------------------------------------
// iPad Pro 11" (M2)
// -----------------------------------------------------
AppleDeviceSpec ipadPro11M2 = new AppleDeviceSpec();
ipadPro11M2.type = "ipad";
ipadPro11M2.model = "iPad Pro 11 (M2)";
ipadPro11M2.year = "2022";
ipadPro11M2.identifier = "iPad14,4 / iPad14,3";
ipadPro11M2.modelNumber = "A2435 / A2759 / A2761";

ipadPro11M2.os = "iPadOS";
ipadPro11M2.soc = "Apple M2";
ipadPro11M2.chipset = "M2";
ipadPro11M2.processNode = "5 nm";
ipadPro11M2.cpu = "Apple CPU";
ipadPro11M2.cpuCores = 8;
ipadPro11M2.gpu = "Apple GPU";
ipadPro11M2.gpuCores = 10;
ipadPro11M2.metalFeatureSet = "Metal 3";

ipadPro11M2.ram = "8 GB / 16 GB";
ipadPro11M2.ramType = "Unified Memory";
ipadPro11M2.storageBase = "128 GB";
ipadPro11M2.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

ipadPro11M2.screen = "11\"";
ipadPro11M2.display = "Liquid Retina (IPS)";
ipadPro11M2.resolution = "2388 × 1668";
ipadPro11M2.refreshRate = "ProMotion 120 Hz";
ipadPro11M2.displayOut = "Thunderbolt / USB-C DisplayPort";

ipadPro11M2.has5G = true;
ipadPro11M2.hasLTE = true;
ipadPro11M2.cellular = "5G / LTE";
ipadPro11M2.modem = "Apple / Qualcomm";
ipadPro11M2.wifi = "Wi-Fi 6E";
ipadPro11M2.bluetooth = "Bluetooth 5.3";
ipadPro11M2.hasNFC = false;
ipadPro11M2.hasAirDrop = true;
ipadPro11M2.hasAirPlay = true;
ipadPro11M2.gps = "GPS / GNSS (Cellular models)";
ipadPro11M2.hasCompass = true;
ipadPro11M2.hasGyro = true;
ipadPro11M2.hasAccel = true;
ipadPro11M2.hasBarometer = true;

ipadPro11M2.simSlots = "Nano-SIM + eSIM";
ipadPro11M2.hasESim = true;
ipadPro11M2.port = "USB-C / Thunderbolt";
ipadPro11M2.usbStandard = "Thunderbolt 4";

ipadPro11M2.speakers = "4-speaker audio";
ipadPro11M2.microphones = "Studio-quality microphones";
ipadPro11M2.hasDolby = true;
ipadPro11M2.hasJack = false;

ipadPro11M2.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
ipadPro11M2.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadPro11M2.cameraVideo = "4K@60fps HDR";

ipadPro11M2.hasFaceID = true;
ipadPro11M2.hasTouchID = false;
ipadPro11M2.biometrics = "Face ID";

ipadPro11M2.hasFastCharge = true;
ipadPro11M2.hasWirelessCharge = false;

ipadPro11M2.thermalNote =
        "No public access to thermal sensors (Apple restriction)";
ipadPro11M2.notes =
        "Performance and thermals constrained by iPadOS sandboxing";

MAP.put("ipad pro 11 m2", ipadPro11M2);

// -----------------------------------------------------
// iPad Pro 11" (M1)
// -----------------------------------------------------
AppleDeviceSpec ipadPro11M1 = new AppleDeviceSpec();
ipadPro11M1.type = "ipad";
ipadPro11M1.model = "iPad Pro 11 (M1)";
ipadPro11M1.year = "2021";
ipadPro11M1.identifier = "iPad13,4 / iPad13,5 / iPad13,6 / iPad13,7";
ipadPro11M1.modelNumber = "A2377 / A2459 / A2301 / A2460";

ipadPro11M1.os = "iPadOS";
ipadPro11M1.soc = "Apple M1";
ipadPro11M1.chipset = "M1";
ipadPro11M1.processNode = "5 nm";
ipadPro11M1.cpu = "Apple CPU";
ipadPro11M1.cpuCores = 8;
ipadPro11M1.gpu = "Apple GPU";
ipadPro11M1.gpuCores = 8;
ipadPro11M1.metalFeatureSet = "Metal 3";

ipadPro11M1.ram = "8 GB / 16 GB";
ipadPro11M1.ramType = "Unified Memory";
ipadPro11M1.storageBase = "128 GB";
ipadPro11M1.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

ipadPro11M1.screen = "11\"";
ipadPro11M1.display = "Liquid Retina (IPS)";
ipadPro11M1.resolution = "2388 × 1668";
ipadPro11M1.refreshRate = "ProMotion 120 Hz";
ipadPro11M1.displayOut = "USB-C DisplayPort";

ipadPro11M1.has5G = true;
ipadPro11M1.hasLTE = true;
ipadPro11M1.cellular = "5G / LTE";
ipadPro11M1.modem = "Qualcomm Snapdragon X55";
ipadPro11M1.wifi = "Wi-Fi 6";
ipadPro11M1.bluetooth = "Bluetooth 5.0";
ipadPro11M1.hasNFC = false;
ipadPro11M1.hasAirDrop = true;
ipadPro11M1.hasAirPlay = true;
ipadPro11M1.gps = "GPS / GNSS (Cellular models)";
ipadPro11M1.hasCompass = true;
ipadPro11M1.hasGyro = true;
ipadPro11M1.hasAccel = true;
ipadPro11M1.hasBarometer = true;

ipadPro11M1.simSlots = "Nano-SIM + eSIM";
ipadPro11M1.hasESim = true;
ipadPro11M1.port = "USB-C";
ipadPro11M1.usbStandard = "USB 4 (limited by iPadOS)";

ipadPro11M1.speakers = "4-speaker audio";
ipadPro11M1.microphones = "5 microphones";
ipadPro11M1.hasDolby = true;
ipadPro11M1.hasJack = false;

ipadPro11M1.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
ipadPro11M1.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadPro11M1.cameraVideo = "4K@60fps HDR";

ipadPro11M1.hasFaceID = true;
ipadPro11M1.hasTouchID = false;
ipadPro11M1.biometrics = "Face ID";

ipadPro11M1.hasFastCharge = true;
ipadPro11M1.hasWirelessCharge = false;

ipadPro11M1.thermalNote =
        "Thermal sensors not accessible to apps (Apple restriction)";
ipadPro11M1.notes =
        "M1 performance heavily sandboxed by iPadOS limitations";

MAP.put("ipad pro 11 m1", ipadPro11M1);

// -----------------------------------------------------
// iPad Pro 12.9" (M1)
// -----------------------------------------------------
AppleDeviceSpec ipadPro129M1 = new AppleDeviceSpec();
ipadPro129M1.type = "ipad";
ipadPro129M1.model = "iPad Pro 12.9 (M1)";
ipadPro129M1.year = "2021";
ipadPro129M1.identifier = "iPad13,8 / iPad13,9 / iPad13,10 / iPad13,11";
ipadPro129M1.modelNumber = "A2378 / A2461 / A2379 / A2462";

ipadPro129M1.os = "iPadOS";
ipadPro129M1.soc = "Apple M1";
ipadPro129M1.chipset = "M1";
ipadPro129M1.processNode = "5 nm";
ipadPro129M1.cpu = "Apple CPU";
ipadPro129M1.cpuCores = 8;
ipadPro129M1.gpu = "Apple GPU";
ipadPro129M1.gpuCores = 8;
ipadPro129M1.metalFeatureSet = "Metal 3";

ipadPro129M1.ram = "8 GB / 16 GB";
ipadPro129M1.ramType = "Unified Memory";
ipadPro129M1.storageBase = "128 GB";
ipadPro129M1.storageOptions = "128 / 256 / 512 GB / 1 TB / 2 TB";

ipadPro129M1.screen = "12.9\"";
ipadPro129M1.display = "Liquid Retina XDR (mini-LED)";
ipadPro129M1.resolution = "2732 × 2048";
ipadPro129M1.refreshRate = "ProMotion 120 Hz";
ipadPro129M1.displayOut = "USB-C DisplayPort";

ipadPro129M1.has5G = true;
ipadPro129M1.hasLTE = true;
ipadPro129M1.cellular = "5G / LTE";
ipadPro129M1.modem = "Qualcomm Snapdragon X55";
ipadPro129M1.wifi = "Wi-Fi 6";
ipadPro129M1.bluetooth = "Bluetooth 5.0";
ipadPro129M1.hasNFC = false;
ipadPro129M1.hasAirDrop = true;
ipadPro129M1.hasAirPlay = true;
ipadPro129M1.gps = "GPS / GNSS (Cellular models)";
ipadPro129M1.hasCompass = true;
ipadPro129M1.hasGyro = true;
ipadPro129M1.hasAccel = true;
ipadPro129M1.hasBarometer = true;

ipadPro129M1.simSlots = "Nano-SIM + eSIM";
ipadPro129M1.hasESim = true;
ipadPro129M1.port = "USB-C";
ipadPro129M1.usbStandard = "USB 4 (limited by iPadOS)";

ipadPro129M1.speakers = "4-speaker audio";
ipadPro129M1.microphones = "5 microphones";
ipadPro129M1.hasDolby = true;
ipadPro129M1.hasJack = false;

ipadPro129M1.cameraMain = "12 MP Wide + 10 MP Ultra-Wide + LiDAR";
ipadPro129M1.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadPro129M1.cameraVideo = "4K@60fps HDR";

ipadPro129M1.hasFaceID = true;
ipadPro129M1.hasTouchID = false;
ipadPro129M1.biometrics = "Face ID";

ipadPro129M1.hasFastCharge = true;
ipadPro129M1.hasWirelessCharge = false;

ipadPro129M1.thermalNote =
        "Mini-LED display introduces localized thermal zones";
ipadPro129M1.notes =
        "Highest sustained brightness among iPads, OS-limited performance";

MAP.put("ipad pro 12.9 m1", ipadPro129M1);

// -----------------------------------------------------
// iPad Air (M1)
// -----------------------------------------------------
AppleDeviceSpec ipadAirM1 = new AppleDeviceSpec();
ipadAirM1.type = "ipad";
ipadAirM1.model = "iPad Air (M1)";
ipadAirM1.year = "2022";
ipadAirM1.identifier = "iPad13,16 / iPad13,17";
ipadAirM1.modelNumber = "A2588 / A2589 / A2591";

ipadAirM1.os = "iPadOS";
ipadAirM1.soc = "Apple M1";
ipadAirM1.chipset = "M1";
ipadAirM1.processNode = "5 nm";
ipadAirM1.cpu = "Apple CPU";
ipadAirM1.cpuCores = 8;
ipadAirM1.gpu = "Apple GPU";
ipadAirM1.gpuCores = 8;
ipadAirM1.metalFeatureSet = "Metal 3";

ipadAirM1.ram = "8 GB";
ipadAirM1.ramType = "Unified Memory";
ipadAirM1.storageBase = "64 GB";
ipadAirM1.storageOptions = "64 / 256 GB";

ipadAirM1.screen = "10.9\"";
ipadAirM1.display = "Liquid Retina (IPS)";
ipadAirM1.resolution = "2360 × 1640";
ipadAirM1.refreshRate = "60 Hz";
ipadAirM1.displayOut = "USB-C DisplayPort";

ipadAirM1.has5G = true;
ipadAirM1.hasLTE = true;
ipadAirM1.cellular = "5G / LTE";
ipadAirM1.modem = "Qualcomm Snapdragon X55";
ipadAirM1.wifi = "Wi-Fi 6";
ipadAirM1.bluetooth = "Bluetooth 5.0";
ipadAirM1.hasNFC = false;
ipadAirM1.hasAirDrop = true;
ipadAirM1.hasAirPlay = true;
ipadAirM1.gps = "GPS / GNSS (Cellular models)";
ipadAirM1.hasCompass = true;
ipadAirM1.hasGyro = true;
ipadAirM1.hasAccel = true;
ipadAirM1.hasBarometer = true;

ipadAirM1.simSlots = "Nano-SIM + eSIM";
ipadAirM1.hasESim = true;
ipadAirM1.port = "USB-C";
ipadAirM1.usbStandard = "USB 3.1 Gen 1";

ipadAirM1.speakers = "Stereo speakers";
ipadAirM1.microphones = "Dual microphones";
ipadAirM1.hasDolby = true;
ipadAirM1.hasJack = false;

ipadAirM1.cameraMain = "12 MP Wide";
ipadAirM1.cameraFront = "12 MP Ultra-Wide (Center Stage)";
ipadAirM1.cameraVideo = "4K@60fps";

ipadAirM1.hasFaceID = false;
ipadAirM1.hasTouchID = true;
ipadAirM1.biometrics = "Touch ID (Top Button)";

ipadAirM1.hasFastCharge = true;
ipadAirM1.hasWirelessCharge = false;

ipadAirM1.thermalNote =
        "Thermal sensors not accessible to applications (Apple restriction)";
ipadAirM1.notes =
        "M1 performance class, limited by iPadOS multitasking constraints";

MAP.put("ipad air m1", ipadAirM1);

// -----------------------------------------------------
// iPad Air 11 (M2)
// -----------------------------------------------------
AppleDeviceSpec ipadAir11M2 = new AppleDeviceSpec();
ipadAir11M2.type = "ipad";
ipadAir11M2.model = "iPad Air 11 (M2)";
ipadAir11M2.year = "2024";
ipadAir11M2.identifier = "iPad14,8 / iPad14,9";
ipadAir11M2.modelNumber = "A2902 / A2903";

ipadAir11M2.os = "iPadOS";
ipadAir11M2.soc = "Apple M2";
ipadAir11M2.chipset = "M2";
ipadAir11M2.processNode = "5 nm";
ipadAir11M2.cpu = "Apple CPU";
ipadAir11M2.cpuCores = 8;
ipadAir11M2.gpu = "Apple GPU";
ipadAir11M2.gpuCores = 9;
ipadAir11M2.metalFeatureSet = "Metal 3";

ipadAir11M2.ram = "8 GB";
ipadAir11M2.ramType = "Unified Memory";
ipadAir11M2.storageBase = "128 GB";
ipadAir11M2.storageOptions = "128 / 256 / 512 GB / 1 TB";

ipadAir11M2.screen = "11\"";
ipadAir11M2.display = "Liquid Retina (IPS)";
ipadAir11M2.resolution = "2360 × 1640";
ipadAir11M2.refreshRate = "60 Hz";
ipadAir11M2.displayOut = "USB-C DisplayPort";

ipadAir11M2.has5G = true;
ipadAir11M2.hasLTE = true;
ipadAir11M2.cellular = "5G / LTE";
ipadAir11M2.modem = "Qualcomm Snapdragon X65";
ipadAir11M2.wifi = "Wi-Fi 6E";
ipadAir11M2.bluetooth = "Bluetooth 5.3";
ipadAir11M2.hasNFC = false;
ipadAir11M2.hasAirDrop = true;
ipadAir11M2.hasAirPlay = true;
ipadAir11M2.gps = "GPS / GNSS (Cellular models)";
ipadAir11M2.hasCompass = true;
ipadAir11M2.hasGyro = true;
ipadAir11M2.hasAccel = true;
ipadAir11M2.hasBarometer = true;

ipadAir11M2.simSlots = "eSIM only";
ipadAir11M2.hasESim = true;
ipadAir11M2.port = "USB-C";
ipadAir11M2.usbStandard = "USB 3.1 Gen 2";

ipadAir11M2.speakers = "Stereo speakers";
ipadAir11M2.microphones = "Dual microphones";
ipadAir11M2.hasDolby = true;
ipadAir11M2.hasJack = false;

ipadAir11M2.cameraMain = "12 MP Wide";
ipadAir11M2.cameraFront = "12 MP Landscape Ultra-Wide (Center Stage)";
ipadAir11M2.cameraVideo = "4K@60fps";

ipadAir11M2.hasFaceID = false;
ipadAir11M2.hasTouchID = true;
ipadAir11M2.biometrics = "Touch ID (Top Button)";

ipadAir11M2.charging = "USB-C fast charge";
ipadAir11M2.hasWirelessCharge = false;

ipadAir11M2.thermalNote =
        "Thermal sensors not accessible to applications (Apple restriction)";
ipadAir11M2.notes =
        "M2 performance class without ProMotion or LiDAR; iPadOS limits pro workflows";

MAP.put("ipad air 11 m2", ipadAir11M2);

// -----------------------------------------------------
// iPad Air 13 (M2)
// -----------------------------------------------------
AppleDeviceSpec ipadAir13M2 = new AppleDeviceSpec();
ipadAir13M2.type = "ipad";
ipadAir13M2.model = "iPad Air 13 (M2)";
ipadAir13M2.year = "2024";
ipadAir13M2.identifier = "iPad14,10 / iPad14,11";
ipadAir13M2.modelNumber = "A2904 / A2905";

// OS / PLATFORM
ipadAir13M2.os = "iPadOS";

// SOC / CPU / GPU
ipadAir13M2.soc = "Apple M2";
ipadAir13M2.chipset = "M2";
ipadAir13M2.processNode = "5 nm";
ipadAir13M2.cpu = "Apple CPU";
ipadAir13M2.cpuCores = 8;
ipadAir13M2.gpu = "Apple GPU";
ipadAir13M2.gpuCores = 9;
ipadAir13M2.metalFeatureSet = "Metal 3";

// MEMORY / STORAGE
ipadAir13M2.ram = "8 GB";
ipadAir13M2.ramType = "Unified Memory";
ipadAir13M2.storageBase = "128 GB";
ipadAir13M2.storageOptions = "128 / 256 / 512 GB / 1 TB";

// DISPLAY
ipadAir13M2.screen = "13\"";
ipadAir13M2.display = "Liquid Retina (IPS)";
ipadAir13M2.resolution = "2732 × 2048";
ipadAir13M2.refreshRate = "60 Hz";
ipadAir13M2.displayOut = "USB-C DisplayPort";

// NETWORK / WIRELESS
ipadAir13M2.has5G = true;
ipadAir13M2.hasLTE = true;
ipadAir13M2.cellular = "5G / LTE";
ipadAir13M2.modem = "Qualcomm Snapdragon X65";
ipadAir13M2.wifi = "Wi-Fi 6E";
ipadAir13M2.bluetooth = "Bluetooth 5.3";
ipadAir13M2.hasNFC = false;
ipadAir13M2.hasAirDrop = true;
ipadAir13M2.hasAirPlay = true;
ipadAir13M2.gps = "GPS / GNSS (Cellular models)";
ipadAir13M2.hasCompass = true;
ipadAir13M2.hasGyro = true;
ipadAir13M2.hasAccel = true;
ipadAir13M2.hasBarometer = true;

// SIM / PORTS
ipadAir13M2.simSlots = "eSIM only";
ipadAir13M2.hasESim = true;
ipadAir13M2.port = "USB-C";
ipadAir13M2.usbStandard = "USB 3.1 Gen 2";

// AUDIO
ipadAir13M2.speakers = "Stereo speakers";
ipadAir13M2.microphones = "Dual microphones";
ipadAir13M2.hasDolby = true;
ipadAir13M2.hasJack = false;

// CAMERA
ipadAir13M2.cameraMain  = "12 MP Wide";
ipadAir13M2.cameraFront = "12 MP Landscape Ultra-Wide (Center Stage)";
ipadAir13M2.cameraVideo = "4K@60fps";

// BIOMETRICS
ipadAir13M2.hasFaceID = false;
ipadAir13M2.hasTouchID = true;
ipadAir13M2.biometrics = "Touch ID (Top Button)";

// POWER
ipadAir13M2.charging = "USB-C fast charge";
ipadAir13M2.hasWirelessCharge = false;

// THERMAL / NOTES
ipadAir13M2.thermalNote =
        "Thermal sensors not accessible to applications (Apple restriction)";
ipadAir13M2.notes =
        "13-inch Air fills the gap between Air and Pro; large display without ProMotion";

MAP.put("ipad air 13 m2", ipadAir13M2);

// ============================================================
// iPad mini (6th generation) — 2021
// ============================================================

AppleDeviceSpec ipadMini6 = new AppleDeviceSpec("ipad", "iPad mini 6");

ipadMini6.year          = "2021";
ipadMini6.identifier    = "iPad14,1 / iPad14,2";
ipadMini6.modelNumber   = "A2567 / A2568 / A2569";

// OS / PLATFORM
ipadMini6.os            = "iPadOS";
ipadMini6.charging      = "USB-C (20W fast charge)";

// SOC / CPU / GPU
ipadMini6.soc           = "A15 Bionic";
ipadMini6.chipset       = "A15 Bionic";
ipadMini6.arch          = "ARMv8.5-A";
ipadMini6.processNode   = "5 nm";
ipadMini6.cpu           = "Apple CPU";
ipadMini6.cpuCores      = 6;
ipadMini6.gpu           = "Apple GPU";
ipadMini6.gpuCores      = 5;
ipadMini6.metalFeatureSet = "Metal 3";

// MEMORY / STORAGE
ipadMini6.ram           = "4 GB";
ipadMini6.ramType       = "LPDDR4X";
ipadMini6.storageBase   = "64 GB";
ipadMini6.storageOptions= "64 GB / 256 GB";

// DISPLAY
ipadMini6.screen        = "8.3\"";
ipadMini6.display       = "Liquid Retina (IPS LCD)";
ipadMini6.resolution    = "2266 × 1488";
ipadMini6.refreshRate   = "60 Hz";
ipadMini6.displayOut    = "USB-C DisplayPort";

// NETWORK / WIRELESS
ipadMini6.has5G         = true;
ipadMini6.hasLTE        = true;
ipadMini6.cellular      = "5G (sub-6 GHz), LTE";
ipadMini6.modem         = "Qualcomm Snapdragon X60";
ipadMini6.wifi          = "Wi-Fi 6 (802.11ax)";
ipadMini6.bluetooth     = "Bluetooth 5.0";
ipadMini6.hasNFC        = false;
ipadMini6.hasAirDrop    = true;
ipadMini6.hasAirPlay    = true;
ipadMini6.gps           = "GPS / GNSS";
ipadMini6.hasCompass    = true;
ipadMini6.hasGyro       = true;
ipadMini6.hasAccel      = true;
ipadMini6.hasBarometer  = true;

// SIM / PORTS
ipadMini6.simSlots      = "Single SIM";
ipadMini6.hasESim       = true;
ipadMini6.port          = "USB-C";
ipadMini6.usbStandard   = "USB-C 3.1 Gen 1";

// AUDIO
ipadMini6.speakers      = "Stereo speakers";
ipadMini6.microphones   = "Dual microphones";
ipadMini6.hasDolby      = true;
ipadMini6.hasJack       = false;

// CAMERA
ipadMini6.cameraMain    = "12 MP Wide";
ipadMini6.cameraUltraWide = null;
ipadMini6.cameraTele    = null;
ipadMini6.cameraFront   = "12 MP Ultra-Wide (Center Stage)";
ipadMini6.cameraVideo   = "4K@60fps HDR";

// BIOMETRICS / FEATURES
ipadMini6.hasFaceID     = false;
ipadMini6.hasTouchID    = true;
ipadMini6.biometrics    = "Touch ID (Power button)";

// POWER
ipadMini6.hasFastCharge     = true;
ipadMini6.hasWirelessCharge= false;

// THERMAL / NOTES
ipadMini6.thermalNote  = "No public thermal sensors (Apple restriction)";
ipadMini6.notes        = "Apple Pencil 2 support, flat-edge design";

    }

public static AppleDeviceSpec get(String modelName) {

    if (modelName == null)
        return AppleDeviceSpec.unknown();

    String m = modelName.trim().toLowerCase();

    // =====================================================
    // SERIES RESOLUTION — CRITICAL
    // =====================================================

    if (m.contains("ipad pro")) {
        if (m.contains("12.9")) return MAP.get("ipad pro 12.9");
        if (m.contains("11"))   return MAP.get("ipad pro 11");
        return MAP.get("ipad pro");
    }

    if (m.contains("ipad air")) {
        return MAP.get("ipad air");
    }

    if (m.contains("ipad mini")) {
        return MAP.get("ipad mini");
    }

    if (m.contains("ipad")) {
        return MAP.get("ipad");
    }

    // =====================================================
    // FALLBACK (exact key)
    // =====================================================
    AppleDeviceSpec d = MAP.get(m);
    return d != null ? d : AppleDeviceSpec.unknown();
}
