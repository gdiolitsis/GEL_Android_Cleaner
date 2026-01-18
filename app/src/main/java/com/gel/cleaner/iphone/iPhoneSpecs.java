// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// iPhoneSpecs.java — FACTORY KNOWLEDGE BASE (iPhone 15 Series)
// ============================================================

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public final class iPhoneSpecs {

    private static final Map<String, AppleDeviceSpec> MAP = new HashMap<>();

    static {

        // =====================================================
        // 📱 iPhone 15
        // =====================================================
        AppleDeviceSpec iphone15 = new AppleDeviceSpec("iphone", "iPhone 15");
        iphone15.year           = "2023";
        iphone15.identifier     = "iPhone15,4";
        iphone15.os             = "iOS";
        iphone15.soc            = "A16 Bionic";
        iphone15.chipset        = "A16 Bionic";
        iphone15.arch           = "ARMv8.6-A";
        iphone15.processNode    = "4 nm";
        iphone15.cpu            = "Apple CPU";
        iphone15.cpuCores       = 6;
        iphone15.gpu            = "Apple GPU";
        iphone15.gpuCores       = 5;
        iphone15.metalFeatureSet= "Metal 3";

        iphone15.ram            = "6 GB";
        iphone15.ramType        = "LPDDR5";
        iphone15.storageBase    = "128 GB";
        iphone15.storageOptions = "128 / 256 / 512 GB";
        iphone15.display        = "6.1\" OLED Super Retina XDR";
        iphone15.resolution     = "2556 × 1179";
        iphone15.refreshRate   = "60 Hz";
        iphone15.displayOut     = "DisplayPort (USB-C)";

        iphone15.has5G          = true;
        iphone15.hasLTE         = true;
        iphone15.cellular       = "5G NR, LTE";
        iphone15.modem          = "Qualcomm Snapdragon X70";
        iphone15.wifi           = "Wi-Fi 6";
        iphone15.bluetooth      = "Bluetooth 5.3";
        iphone15.hasNFC         = true;
        iphone15.hasAirDrop     = true;
        iphone15.hasAirPlay     = true;
        iphone15.gps            = "GPS, GLONASS, Galileo, QZSS";
        iphone15.hasCompass     = true;
        iphone15.hasGyro        = true;
        iphone15.hasAccel       = true;
        iphone15.hasBarometer   = true;

        iphone15.simSlots       = "Dual eSIM";
        iphone15.hasESim        = true;
        iphone15.port           = "USB-C";
        iphone15.usbStandard    = "USB 2";

        iphone15.speakers       = "Stereo speakers";
        iphone15.microphones   = "Multiple microphones";
        iphone15.hasDolby       = true;
        iphone15.hasJack        = false;

        iphone15.cameraMain     = "48 MP Wide";
        iphone15.cameraUltraWide= "12 MP Ultra-Wide";
        iphone15.cameraTele     = null;
        iphone15.cameraFront    = "12 MP TrueDepth";
        iphone15.cameraVideo    = "4K@60fps HDR";

        iphone15.hasFaceID      = true;
        iphone15.biometrics     = "Face ID";
        iphone15.hasFastCharge  = true;
        iphone15.hasWirelessCharge = true;

        iphone15.thermalNote =
                "No public access to thermal sensors (Apple restriction)";
        iphone15.notes =
                "Factory specifications only. No runtime system probing.";

        MAP.put("iphone 15", iphone15);


        // =====================================================
        // 📱 iPhone 15 Plus
        // =====================================================
        AppleDeviceSpec iphone15Plus = new AppleDeviceSpec("iphone", "iPhone 15 Plus");
        iphone15Plus.year            = "2023";
        iphone15Plus.identifier      = "iPhone15,5";
        iphone15Plus.os              = "iOS";
        iphone15Plus.soc             = "A16 Bionic";
        iphone15Plus.chipset         = "A16 Bionic";
        iphone15Plus.arch            = "ARMv8.6-A";
        iphone15Plus.processNode     = "4 nm";
        iphone15Plus.cpu             = "Apple CPU";
        iphone15Plus.cpuCores        = 6;
        iphone15Plus.gpu             = "Apple GPU";
        iphone15Plus.gpuCores        = 5;

        iphone15Plus.ram             = "6 GB";
        iphone15Plus.storageOptions  = "128 / 256 / 512 GB";
        iphone15Plus.display         = "6.7\" OLED Super Retina XDR";
        iphone15Plus.refreshRate     = "60 Hz";
        iphone15Plus.port            = "USB-C";
        iphone15Plus.usbStandard     = "USB 2";
        iphone15Plus.cameraMain      = "48 MP Wide";
        iphone15Plus.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15Plus.cameraFront     = "12 MP TrueDepth";
        iphone15Plus.biometrics      = "Face ID";
        iphone15Plus.thermalNote     =
                "Thermal data not accessible (Apple restriction)";

        MAP.put("iphone 15 plus", iphone15Plus);


        // =====================================================
        // 📱 iPhone 15 Pro
        // =====================================================
        AppleDeviceSpec iphone15Pro = new AppleDeviceSpec("iphone", "iPhone 15 Pro");
        iphone15Pro.year            = "2023";
        iphone15Pro.identifier      = "iPhone16,1";
        iphone15Pro.os              = "iOS";
        iphone15Pro.soc             = "A17 Pro";
        iphone15Pro.chipset         = "A17 Pro";
        iphone15Pro.arch            = "ARMv9";
        iphone15Pro.processNode     = "3 nm";
        iphone15Pro.cpu             = "Apple CPU";
        iphone15Pro.cpuCores        = 6;
        iphone15Pro.gpu             = "Apple GPU";
        iphone15Pro.gpuCores        = 6;
        iphone15Pro.metalFeatureSet = "Metal 3 (Ray Tracing)";

        iphone15Pro.ram             = "8 GB";
        iphone15Pro.storageOptions  = "128 / 256 / 512 GB / 1 TB";
        iphone15Pro.display         = "6.1\" OLED ProMotion";
        iphone15Pro.refreshRate     = "120 Hz";
        iphone15Pro.port            = "USB-C";
        iphone15Pro.usbStandard     = "USB 3";

        iphone15Pro.cameraMain      = "48 MP Pro";
        iphone15Pro.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15Pro.cameraTele      = "12 MP Telephoto";
        iphone15Pro.cameraVideo     = "4K ProRes";
        iphone15Pro.biometrics      = "Face ID";

        iphone15Pro.notes =
                "Pro model includes hardware ray tracing and USB-C high-speed data.";

        MAP.put("iphone 15 pro", iphone15Pro);


        // =====================================================
        // 📱 iPhone 15 Pro Max
        // =====================================================
        AppleDeviceSpec iphone15ProMax = new AppleDeviceSpec("iphone", "iPhone 15 Pro Max");
        iphone15ProMax.year            = "2023";
        iphone15ProMax.identifier      = "iPhone16,2";
        iphone15ProMax.os              = "iOS";
        iphone15ProMax.soc             = "A17 Pro";
        iphone15ProMax.arch            = "ARMv9";
        iphone15ProMax.processNode     = "3 nm";
        iphone15ProMax.cpuCores        = 6;
        iphone15ProMax.gpuCores        = 6;

        iphone15ProMax.ram             = "8 GB";
        iphone15ProMax.storageOptions  = "256 / 512 GB / 1 TB";
        iphone15ProMax.display         = "6.7\" OLED ProMotion";
        iphone15ProMax.refreshRate     = "120 Hz";
        iphone15ProMax.cameraTele      = "12 MP Telephoto (5×)";
        iphone15ProMax.port            = "USB-C";
        iphone15ProMax.usbStandard     = "USB 3";
        iphone15ProMax.biometrics      = "Face ID";

        iphone15ProMax.notes =
                "Exclusive 5× telephoto lens. Highest factory configuration.";

        MAP.put("iphone 15 pro max", iphone15ProMax);
    }

// =========================================================
// 📱 iPHONE 14 SERIES — FULL SPECS
// =========================================================

// ---------------------------------------------------------
// iPhone 14
// ---------------------------------------------------------
AppleDeviceSpec iphone14 = new AppleDeviceSpec("iphone", "iPhone 14");
iphone14.year            = "2022";
iphone14.identifier      = "iPhone14,7";
iphone14.os              = "iOS";
iphone14.soc             = "A15 Bionic";
iphone14.chipset         = "A15 Bionic";
iphone14.arch            = "ARMv8.5-A";
iphone14.processNode     = "5 nm";
iphone14.cpu             = "Apple CPU";
iphone14.cpuCores        = 6;
iphone14.gpu             = "Apple GPU";
iphone14.gpuCores        = 5;
iphone14.metalFeatureSet = "Metal 3";

iphone14.ram             = "6 GB";
iphone14.ramType         = "LPDDR4X";
iphone14.storageBase     = "128 GB";
iphone14.storageOptions  = "128 / 256 / 512 GB";

iphone14.screen          = "6.1\"";
iphone14.display         = "OLED Super Retina XDR";
iphone14.resolution      = "2532 × 1170";
iphone14.refreshRate     = "60 Hz";
iphone14.displayOut      = "Lightning Digital AV (adapter)";

iphone14.has5G           = true;
iphone14.hasLTE          = true;
iphone14.cellular        = "5G NR / LTE";
iphone14.modem           = "Qualcomm Snapdragon X65";
iphone14.wifi            = "Wi-Fi 6 (802.11ax)";
iphone14.bluetooth       = "Bluetooth 5.3";
iphone14.hasNFC          = true;
iphone14.hasAirDrop      = true;
iphone14.hasAirPlay      = true;
iphone14.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone14.hasCompass      = true;
iphone14.hasGyro         = true;
iphone14.hasAccel        = true;
iphone14.hasBarometer    = true;

iphone14.simSlots        = "eSIM (US) / Nano-SIM + eSIM";
iphone14.hasESim         = true;
iphone14.port            = "Lightning";
iphone14.usbStandard     = "USB 2.0";

iphone14.speakers        = "Stereo";
iphone14.microphones     = "Multiple";
iphone14.hasDolby        = true;
iphone14.hasJack         = false;

iphone14.cameraMain      = "12 MP Wide (OIS)";
iphone14.cameraUltraWide = "12 MP Ultra-Wide";
iphone14.cameraTele      = null;
iphone14.cameraFront     = "12 MP TrueDepth";
iphone14.cameraVideo     = "4K@60fps HDR";

iphone14.hasFaceID       = true;
iphone14.hasTouchID      = false;
iphone14.biometrics      = "Face ID";

iphone14.hasFastCharge       = true;
iphone14.hasWirelessCharge  = true;
iphone14.charging           = "Lightning / MagSafe";

iphone14.thermalNote    = "No public thermal sensor access (Apple restriction)";
iphone14.notes          = "Same SoC as iPhone 13 Pro, improved GPU & safety features";

MAP.put("iphone 14", iphone14);

// ---------------------------------------------------------
// iPhone 14 Plus
// ---------------------------------------------------------
AppleDeviceSpec iphone14Plus = new AppleDeviceSpec("iphone", "iPhone 14 Plus");
iphone14Plus.year            = "2022";
iphone14Plus.identifier      = "iPhone14,8";
iphone14Plus.os              = "iOS";
iphone14Plus.soc             = "A15 Bionic";
iphone14Plus.chipset         = "A15 Bionic";
iphone14Plus.arch            = "ARMv8.5-A";
iphone14Plus.processNode     = "5 nm";
iphone14Plus.cpu             = "Apple CPU";
iphone14Plus.cpuCores        = 6;
iphone14Plus.gpu             = "Apple GPU";
iphone14Plus.gpuCores        = 5;
iphone14Plus.metalFeatureSet = "Metal 3";

iphone14Plus.ram             = "6 GB";
iphone14Plus.ramType         = "LPDDR4X";
iphone14Plus.storageBase     = "128 GB";
iphone14Plus.storageOptions  = "128 / 256 / 512 GB";

iphone14Plus.screen          = "6.7\"";
iphone14Plus.display         = "OLED Super Retina XDR";
iphone14Plus.resolution      = "2778 × 1284";
iphone14Plus.refreshRate     = "60 Hz";
iphone14Plus.displayOut      = "Lightning Digital AV (adapter)";

iphone14Plus.has5G           = true;
iphone14Plus.hasLTE          = true;
iphone14Plus.cellular        = "5G NR / LTE";
iphone14Plus.modem           = "Qualcomm Snapdragon X65";
iphone14Plus.wifi            = "Wi-Fi 6";
iphone14Plus.bluetooth       = "Bluetooth 5.3";
iphone14Plus.hasNFC          = true;
iphone14Plus.hasAirDrop      = true;
iphone14Plus.hasAirPlay      = true;
iphone14Plus.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone14Plus.hasCompass      = true;
iphone14Plus.hasGyro         = true;
iphone14Plus.hasAccel        = true;
iphone14Plus.hasBarometer    = true;

iphone14Plus.simSlots        = "eSIM (US) / Nano-SIM + eSIM";
iphone14Plus.hasESim         = true;
iphone14Plus.port            = "Lightning";
iphone14Plus.usbStandard     = "USB 2.0";

iphone14Plus.speakers        = "Stereo";
iphone14Plus.microphones     = "Multiple";
iphone14Plus.hasDolby        = true;
iphone14Plus.hasJack         = false;

iphone14Plus.cameraMain      = "12 MP Wide (OIS)";
iphone14Plus.cameraUltraWide = "12 MP Ultra-Wide";
iphone14Plus.cameraTele      = null;
iphone14Plus.cameraFront     = "12 MP TrueDepth";
iphone14Plus.cameraVideo     = "4K@60fps HDR";

iphone14Plus.hasFaceID       = true;
iphone14Plus.hasTouchID      = false;
iphone14Plus.biometrics      = "Face ID";

iphone14Plus.hasFastCharge       = true;
iphone14Plus.hasWirelessCharge  = true;
iphone14Plus.charging           = "Lightning / MagSafe";

iphone14Plus.thermalNote    = "No public thermal sensor access (Apple restriction)";
iphone14Plus.notes          = "Largest non-Pro iPhone display";

MAP.put("iphone 14 plus", iphone14Plus);

// ---------------------------------------------------------
// iPhone 14 Pro
// ---------------------------------------------------------
AppleDeviceSpec iphone14Pro = new AppleDeviceSpec("iphone", "iPhone 14 Pro");
iphone14Pro.year            = "2022";
iphone14Pro.identifier      = "iPhone15,2";
iphone14Pro.os              = "iOS";
iphone14Pro.soc             = "A16 Bionic";
iphone14Pro.chipset         = "A16 Bionic";
iphone14Pro.arch            = "ARMv9-A";
iphone14Pro.processNode     = "4 nm";
iphone14Pro.cpu             = "Apple CPU";
iphone14Pro.cpuCores        = 6;
iphone14Pro.gpu             = "Apple GPU";
iphone14Pro.gpuCores        = 5;
iphone14Pro.metalFeatureSet = "Metal 3";

iphone14Pro.ram             = "6 GB";
iphone14Pro.ramType         = "LPDDR5";
iphone14Pro.storageBase     = "128 GB";
iphone14Pro.storageOptions  = "128 / 256 / 512 GB / 1 TB";

iphone14Pro.screen          = "6.1\"";
iphone14Pro.display         = "OLED Super Retina XDR (ProMotion)";
iphone14Pro.resolution      = "2556 × 1179";
iphone14Pro.refreshRate     = "1–120 Hz";
iphone14Pro.displayOut      = "Lightning Digital AV (adapter)";

iphone14Pro.has5G           = true;
iphone14Pro.hasLTE          = true;
iphone14Pro.cellular        = "5G NR / LTE";
iphone14Pro.modem           = "Qualcomm Snapdragon X65";
iphone14Pro.wifi            = "Wi-Fi 6";
iphone14Pro.bluetooth       = "Bluetooth 5.3";
iphone14Pro.hasNFC          = true;
iphone14Pro.hasAirDrop      = true;
iphone14Pro.hasAirPlay      = true;
iphone14Pro.gps             = "Dual-frequency GPS";
iphone14Pro.hasCompass      = true;
iphone14Pro.hasGyro         = true;
iphone14Pro.hasAccel        = true;
iphone14Pro.hasBarometer    = true;

iphone14Pro.simSlots        = "eSIM (US) / Nano-SIM + eSIM";
iphone14Pro.hasESim         = true;
iphone14Pro.port            = "Lightning";
iphone14Pro.usbStandard     = "USB 2.0";

iphone14Pro.speakers        = "Stereo";
iphone14Pro.microphones     = "Multiple";
iphone14Pro.hasDolby        = true;
iphone14Pro.hasJack         = false;

iphone14Pro.cameraMain      = "48 MP Wide (Sensor-shift OIS)";
iphone14Pro.cameraUltraWide = "12 MP Ultra-Wide";
iphone14Pro.cameraTele      = "12 MP Telephoto (3×)";
iphone14Pro.cameraFront     = "12 MP TrueDepth";
iphone14Pro.cameraVideo     = "4K@60fps ProRes / HDR";

iphone14Pro.hasFaceID       = true;
iphone14Pro.hasTouchID      = false;
iphone14Pro.biometrics      = "Face ID";

iphone14Pro.hasFastCharge       = true;
iphone14Pro.hasWirelessCharge  = true;
iphone14Pro.charging           = "Lightning / MagSafe";

iphone14Pro.thermalNote    = "Thermal sensors not accessible via iOS";
iphone14Pro.notes          = "First iPhone with Dynamic Island";

MAP.put("iphone 14 pro", iphone14Pro);

// ---------------------------------------------------------
// iPhone 14 Pro Max
// ---------------------------------------------------------
AppleDeviceSpec iphone14ProMax = new AppleDeviceSpec("iphone", "iPhone 14 Pro Max");
iphone14ProMax.year            = "2022";
iphone14ProMax.identifier      = "iPhone15,3";
iphone14ProMax.os              = "iOS";
iphone14ProMax.soc             = "A16 Bionic";
iphone14ProMax.chipset         = "A16 Bionic";
iphone14ProMax.arch            = "ARMv9-A";
iphone14ProMax.processNode     = "4 nm";
iphone14ProMax.cpu             = "Apple CPU";
iphone14ProMax.cpuCores        = 6;
iphone14ProMax.gpu             = "Apple GPU";
iphone14ProMax.gpuCores        = 5;
iphone14ProMax.metalFeatureSet = "Metal 3";

iphone14ProMax.ram             = "6 GB";
iphone14ProMax.ramType         = "LPDDR5";
iphone14ProMax.storageBase     = "128 GB";
iphone14ProMax.storageOptions  = "128 / 256 / 512 GB / 1 TB";

iphone14ProMax.screen          = "6.7\"";
iphone14ProMax.display         = "OLED Super Retina XDR (ProMotion)";
iphone14ProMax.resolution      = "2796 × 1290";
iphone14ProMax.refreshRate     = "1–120 Hz";
iphone14ProMax.displayOut      = "Lightning Digital AV (adapter)";

iphone14ProMax.has5G           = true;
iphone14ProMax.hasLTE          = true;
iphone14ProMax.cellular        = "5G NR / LTE";
iphone14ProMax.modem           = "Qualcomm Snapdragon X65";
iphone14ProMax.wifi            = "Wi-Fi 6";
iphone14ProMax.bluetooth       = "Bluetooth 5.3";
iphone14ProMax.hasNFC          = true;
iphone14ProMax.hasAirDrop      = true;
iphone14ProMax.hasAirPlay      = true;
iphone14ProMax.gps             = "Dual-frequency GPS";
iphone14ProMax.hasCompass      = true;
iphone14ProMax.hasGyro         = true;
iphone14ProMax.hasAccel        = true;
iphone14ProMax.hasBarometer    = true;

iphone14ProMax.simSlots        = "eSIM (US) / Nano-SIM + eSIM";
iphone14ProMax.hasESim         = true;
iphone14ProMax.port            = "Lightning";
iphone14ProMax.usbStandard     = "USB 2.0";

iphone14ProMax.speakers        = "Stereo";
iphone14ProMax.microphones     = "Multiple";
iphone14ProMax.hasDolby        = true;
iphone14ProMax.hasJack         = false;

iphone14ProMax.cameraMain      = "48 MP Wide (Sensor-shift OIS)";
iphone14ProMax.cameraUltraWide = "12 MP Ultra-Wide";
iphone14ProMax.cameraTele      = "12 MP Telephoto (3×)";
iphone14ProMax.cameraFront     = "12 MP TrueDepth";
iphone14ProMax.cameraVideo     = "4K@60fps ProRes / HDR";

iphone14ProMax.hasFaceID       = true;
iphone14ProMax.hasTouchID      = false;
iphone14ProMax.biometrics      = "Face ID";

iphone14ProMax.hasFastCharge       = true;
iphone14ProMax.hasWirelessCharge  = true;
iphone14ProMax.charging           = "Lightning / MagSafe";

iphone14ProMax.thermalNote    = "Thermal sensors not accessible via iOS";
iphone14ProMax.notes          = "Largest Pro model with maximum battery";

MAP.put("iphone 14 pro max", iphone14ProMax);

// =========================================================
// 📱 iPHONE 13 SERIES — FULL SPECS
// =========================================================

// ---------------------------------------------------------
// iPhone 13 mini
// ---------------------------------------------------------
AppleDeviceSpec iphone13Mini = new AppleDeviceSpec("iphone", "iPhone 13 mini");
iphone13Mini.year            = "2021";
iphone13Mini.identifier      = "iPhone14,4";
iphone13Mini.os              = "iOS";
iphone13Mini.soc             = "A15 Bionic";
iphone13Mini.chipset         = "A15 Bionic";
iphone13Mini.arch            = "ARMv8.5-A";
iphone13Mini.processNode     = "5 nm";
iphone13Mini.cpu             = "Apple CPU";
iphone13Mini.cpuCores        = 6;
iphone13Mini.gpu             = "Apple GPU";
iphone13Mini.gpuCores        = 4;
iphone13Mini.metalFeatureSet = "Metal 3";

iphone13Mini.ram             = "4 GB";
iphone13Mini.ramType         = "LPDDR4X";
iphone13Mini.storageBase     = "128 GB";
iphone13Mini.storageOptions  = "128 / 256 / 512 GB";

iphone13Mini.screen          = "5.4\"";
iphone13Mini.display         = "OLED Super Retina XDR";
iphone13Mini.resolution      = "2340 × 1080";
iphone13Mini.refreshRate     = "60 Hz";
iphone13Mini.displayOut      = "Lightning Digital AV (adapter)";

iphone13Mini.has5G           = true;
iphone13Mini.hasLTE          = true;
iphone13Mini.cellular        = "5G NR / LTE";
iphone13Mini.modem           = "Qualcomm Snapdragon X60";
iphone13Mini.wifi            = "Wi-Fi 6";
iphone13Mini.bluetooth       = "Bluetooth 5.0";
iphone13Mini.hasNFC          = true;
iphone13Mini.hasAirDrop      = true;
iphone13Mini.hasAirPlay      = true;
iphone13Mini.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone13Mini.hasCompass      = true;
iphone13Mini.hasGyro         = true;
iphone13Mini.hasAccel        = true;
iphone13Mini.hasBarometer    = true;

iphone13Mini.simSlots        = "Nano-SIM + eSIM";
iphone13Mini.hasESim         = true;
iphone13Mini.port            = "Lightning";
iphone13Mini.usbStandard     = "USB 2.0";

iphone13Mini.speakers        = "Stereo";
iphone13Mini.microphones     = "Multiple";
iphone13Mini.hasDolby        = true;
iphone13Mini.hasJack         = false;

iphone13Mini.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13Mini.cameraUltraWide = "12 MP Ultra-Wide";
iphone13Mini.cameraTele      = null;
iphone13Mini.cameraFront     = "12 MP TrueDepth";
iphone13Mini.cameraVideo     = "4K@60fps HDR";

iphone13Mini.hasFaceID       = true;
iphone13Mini.hasTouchID      = false;
iphone13Mini.biometrics      = "Face ID";

iphone13Mini.hasFastCharge       = true;
iphone13Mini.hasWirelessCharge  = true;
iphone13Mini.charging           = "Lightning / MagSafe";

iphone13Mini.thermalNote    = "No public thermal sensor access (Apple restriction)";
iphone13Mini.notes          = "Smallest iPhone with A15 Bionic";

MAP.put("iphone 13 mini", iphone13Mini);

// ---------------------------------------------------------
// iPhone 13
// ---------------------------------------------------------
AppleDeviceSpec iphone13 = new AppleDeviceSpec("iphone", "iPhone 13");
iphone13.year            = "2021";
iphone13.identifier      = "iPhone14,5";
iphone13.os              = "iOS";
iphone13.soc             = "A15 Bionic";
iphone13.chipset         = "A15 Bionic";
iphone13.arch            = "ARMv8.5-A";
iphone13.processNode     = "5 nm";
iphone13.cpu             = "Apple CPU";
iphone13.cpuCores        = 6;
iphone13.gpu             = "Apple GPU";
iphone13.gpuCores        = 4;
iphone13.metalFeatureSet = "Metal 3";

iphone13.ram             = "4 GB";
iphone13.ramType         = "LPDDR4X";
iphone13.storageBase     = "128 GB";
iphone13.storageOptions  = "128 / 256 / 512 GB";

iphone13.screen          = "6.1\"";
iphone13.display         = "OLED Super Retina XDR";
iphone13.resolution      = "2532 × 1170";
iphone13.refreshRate     = "60 Hz";
iphone13.displayOut      = "Lightning Digital AV (adapter)";

iphone13.has5G           = true;
iphone13.hasLTE          = true;
iphone13.cellular        = "5G NR / LTE";
iphone13.modem           = "Qualcomm Snapdragon X60";
iphone13.wifi            = "Wi-Fi 6";
iphone13.bluetooth       = "Bluetooth 5.0";
iphone13.hasNFC          = true;
iphone13.hasAirDrop      = true;
iphone13.hasAirPlay      = true;
iphone13.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone13.hasCompass      = true;
iphone13.hasGyro         = true;
iphone13.hasAccel        = true;
iphone13.hasBarometer    = true;

iphone13.simSlots        = "Nano-SIM + eSIM";
iphone13.hasESim         = true;
iphone13.port            = "Lightning";
iphone13.usbStandard     = "USB 2.0";

iphone13.speakers        = "Stereo";
iphone13.microphones     = "Multiple";
iphone13.hasDolby        = true;
iphone13.hasJack         = false;

iphone13.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13.cameraUltraWide = "12 MP Ultra-Wide";
iphone13.cameraTele      = null;
iphone13.cameraFront     = "12 MP TrueDepth";
iphone13.cameraVideo     = "4K@60fps HDR";

iphone13.hasFaceID       = true;
iphone13.hasTouchID      = false;
iphone13.biometrics      = "Face ID";

iphone13.hasFastCharge       = true;
iphone13.hasWirelessCharge  = true;
iphone13.charging           = "Lightning / MagSafe";

iphone13.thermalNote    = "No public thermal sensor access (Apple restriction)";
iphone13.notes          = "Base model of iPhone 13 lineup";

MAP.put("iphone 13", iphone13);

// ---------------------------------------------------------
// iPhone 13 Pro
// ---------------------------------------------------------
AppleDeviceSpec iphone13Pro = new AppleDeviceSpec("iphone", "iPhone 13 Pro");
iphone13Pro.year            = "2021";
iphone13Pro.identifier      = "iPhone14,2";
iphone13Pro.os              = "iOS";
iphone13Pro.soc             = "A15 Bionic";
iphone13Pro.chipset         = "A15 Bionic";
iphone13Pro.arch            = "ARMv8.5-A";
iphone13Pro.processNode     = "5 nm";
iphone13Pro.cpu             = "Apple CPU";
iphone13Pro.cpuCores        = 6;
iphone13Pro.gpu             = "Apple GPU";
iphone13Pro.gpuCores        = 5;
iphone13Pro.metalFeatureSet = "Metal 3";

iphone13Pro.ram             = "6 GB";
iphone13Pro.ramType         = "LPDDR4X";
iphone13Pro.storageBase     = "128 GB";
iphone13Pro.storageOptions  = "128 / 256 / 512 GB / 1 TB";

iphone13Pro.screen          = "6.1\"";
iphone13Pro.display         = "OLED Super Retina XDR (ProMotion)";
iphone13Pro.resolution      = "2532 × 1170";
iphone13Pro.refreshRate     = "10–120 Hz";
iphone13Pro.displayOut      = "Lightning Digital AV (adapter)";

iphone13Pro.has5G           = true;
iphone13Pro.hasLTE          = true;
iphone13Pro.cellular        = "5G NR / LTE";
iphone13Pro.modem           = "Qualcomm Snapdragon X60";
iphone13Pro.wifi            = "Wi-Fi 6";
iphone13Pro.bluetooth       = "Bluetooth 5.0";
iphone13Pro.hasNFC          = true;
iphone13Pro.hasAirDrop      = true;
iphone13Pro.hasAirPlay      = true;
iphone13Pro.gps             = "Dual-frequency GPS";
iphone13Pro.hasCompass      = true;
iphone13Pro.hasGyro         = true;
iphone13Pro.hasAccel        = true;
iphone13Pro.hasBarometer    = true;

iphone13Pro.simSlots        = "Nano-SIM + eSIM";
iphone13Pro.hasESim         = true;
iphone13Pro.port            = "Lightning";
iphone13Pro.usbStandard     = "USB 2.0";

iphone13Pro.speakers        = "Stereo";
iphone13Pro.microphones     = "Multiple";
iphone13Pro.hasDolby        = true;
iphone13Pro.hasJack         = false;

iphone13Pro.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13Pro.cameraUltraWide = "12 MP Ultra-Wide";
iphone13Pro.cameraTele      = "12 MP Telephoto (3×)";
iphone13Pro.cameraFront     = "12 MP TrueDepth";
iphone13Pro.cameraVideo     = "4K@60fps ProRes / HDR";

iphone13Pro.hasFaceID       = true;
iphone13Pro.hasTouchID      = false;
iphone13Pro.biometrics      = "Face ID";

iphone13Pro.hasFastCharge       = true;
iphone13Pro.hasWirelessCharge  = true;
iphone13Pro.charging           = "Lightning / MagSafe";

iphone13Pro.thermalNote    = "Thermal sensors not accessible via iOS";
iphone13Pro.notes          = "First Pro iPhone with ProMotion display";

MAP.put("iphone 13 pro", iphone13Pro);

// ---------------------------------------------------------
// iPhone 13 Pro Max
// ---------------------------------------------------------
AppleDeviceSpec iphone13ProMax = new AppleDeviceSpec("iphone", "iPhone 13 Pro Max");
iphone13ProMax.year            = "2021";
iphone13ProMax.identifier      = "iPhone14,3";
iphone13ProMax.os              = "iOS";
iphone13ProMax.soc             = "A15 Bionic";
iphone13ProMax.chipset         = "A15 Bionic";
iphone13ProMax.arch            = "ARMv8.5-A";
iphone13ProMax.processNode     = "5 nm";
iphone13ProMax.cpu             = "Apple CPU";
iphone13ProMax.cpuCores        = 6;
iphone13ProMax.gpu             = "Apple GPU";
iphone13ProMax.gpuCores        = 5;
iphone13ProMax.metalFeatureSet = "Metal 3";

iphone13ProMax.ram             = "6 GB";
iphone13ProMax.ramType         = "LPDDR4X";
iphone13ProMax.storageBase     = "128 GB";
iphone13ProMax.storageOptions  = "128 / 256 / 512 GB / 1 TB";

iphone13ProMax.screen          = "6.7\"";
iphone13ProMax.display         = "OLED Super Retina XDR (ProMotion)";
iphone13ProMax.resolution      = "2778 × 1284";
iphone13ProMax.refreshRate     = "10–120 Hz";
iphone13ProMax.displayOut      = "Lightning Digital AV (adapter)";

iphone13ProMax.has5G           = true;
iphone13ProMax.hasLTE          = true;
iphone13ProMax.cellular        = "5G NR / LTE";
iphone13ProMax.modem           = "Qualcomm Snapdragon X60";
iphone13ProMax.wifi            = "Wi-Fi 6";
iphone13ProMax.bluetooth       = "Bluetooth 5.0";
iphone13ProMax.hasNFC          = true;
iphone13ProMax.hasAirDrop      = true;
iphone13ProMax.hasAirPlay      = true;
iphone13ProMax.gps             = "Dual-frequency GPS";
iphone13ProMax.hasCompass      = true;
iphone13ProMax.hasGyro         = true;
iphone13ProMax.hasAccel        = true;
iphone13ProMax.hasBarometer    = true;

iphone13ProMax.simSlots        = "Nano-SIM + eSIM";
iphone13ProMax.hasESim         = true;
iphone13ProMax.port            = "Lightning";
iphone13ProMax.usbStandard     = "USB 2.0";

iphone13ProMax.speakers        = "Stereo";
iphone13ProMax.microphones     = "Multiple";
iphone13ProMax.hasDolby        = true;
iphone13ProMax.hasJack         = false;

iphone13ProMax.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13ProMax.cameraUltraWide = "12 MP Ultra-Wide";
iphone13ProMax.cameraTele      = "12 MP Telephoto (3×)";
iphone13ProMax.cameraFront     = "12 MP TrueDepth";
iphone13ProMax.cameraVideo     = "4K@60fps ProRes / HDR";

iphone13ProMax.hasFaceID       = true;
iphone13ProMax.hasTouchID      = false;
iphone13ProMax.biometrics      = "Face ID";

iphone13ProMax.hasFastCharge       = true;
iphone13ProMax.hasWirelessCharge  = true;
iphone13ProMax.charging           = "Lightning / MagSafe";

iphone13ProMax.thermalNote    = "Thermal sensors not accessible via iOS";
iphone13ProMax.notes          = "Largest iPhone 13 with maximum battery";

MAP.put("iphone 13 pro max", iphone13ProMax);

