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
        iphone15.year            = "2023";
        iphone15.identifier      = "iPhone15,4";
        iphone15.os              = "iOS";
        iphone15.soc             = "A16 Bionic";
        iphone15.chipset         = "A16 Bionic";
        iphone15.arch            = "ARMv8.6-A";
        iphone15.processNode     = "4 nm";
        iphone15.cpu             = "Apple CPU";
        iphone15.cpuCores        = 6;
        iphone15.gpu             = "Apple GPU";
        iphone15.gpuCores        = 5;
        iphone15.metalFeatureSet = "Metal 3";

        iphone15.ram             = "6 GB";
        iphone15.ramType         = "LPDDR5";
        iphone15.storageBase     = "128 GB";
        iphone15.storageOptions  = "128 / 256 / 512 GB";
        iphone15.screen          = "6.1\"";
        iphone15.display         = "OLED Super Retina XDR";
        iphone15.resolution      = "2556 × 1179";
        iphone15.refreshRate     = "60 Hz";
        iphone15.displayOut      = "DisplayPort (USB-C)";
        iphone15.displayVariants = "Standard 60 Hz (no ProMotion)";

        iphone15.has5G           = true;
        iphone15.hasLTE          = true;
        iphone15.cellular        = "5G NR, LTE";
        iphone15.modem           = "Qualcomm Snapdragon X70";
        iphone15.wifi            = "Wi-Fi 6";
        iphone15.bluetooth       = "Bluetooth 5.3";
        iphone15.hasNFC          = true;
        iphone15.hasAirDrop      = true;
        iphone15.hasAirPlay      = true;
        iphone15.gps             = "GPS, GLONASS, Galileo, QZSS";
        iphone15.hasCompass      = true;
        iphone15.hasGyro         = true;
        iphone15.hasAccel        = true;
        iphone15.hasBarometer    = true;

        iphone15.simSlots        = "Dual eSIM";
        iphone15.hasESim         = true;
        iphone15.port            = "USB-C";
        iphone15.usbStandard     = "USB 2";

        iphone15.speakers        = "Stereo speakers";
        iphone15.microphones    = "Multiple microphones";
        iphone15.hasDolby        = true;
        iphone15.hasJack         = false;

        iphone15.cameraMain      = "48 MP Wide";
        iphone15.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15.cameraTele      = null;
        iphone15.cameraFront     = "12 MP TrueDepth";
        iphone15.cameraVideo     = "4K@60fps HDR";
        iphone15.cameraVariants  =
                "Dual-camera system (Wide + Ultra-Wide)";

        iphone15.hasFaceID       = true;
        iphone15.biometrics      = "Face ID";
        iphone15.hasFastCharge   = true;
        iphone15.hasWirelessCharge = true;

        // =====================================================
        // 🔋 BATTERY — SERVICE / REFERENCE DATA
        // =====================================================
        iphone15.batteryMah          = 3349;   // model-based reference
        iphone15.batteryVoltage      = 3.82f;  // Apple nominal
        iphone15.batteryWh           =
                (iphone15.batteryMah * iphone15.batteryVoltage) / 1000f;
        iphone15.batteryChemistry    = "Lithium-Ion (pouch)";
        iphone15.batteryDesignCycles = 500;
        iphone15.batteryCharging     =
                "Fast wired charging, MagSafe 15W, Qi wireless";
        iphone15.batteryNotes        =
                "Integrated battery; capacity varies slightly by production batch";

        iphone15.thermalNote =
                "No public access to thermal sensors (Apple restriction)";
        iphone15.notes =
                "Factory specifications only. No runtime system probing.";

        MAP.put("iphone 15", iphone15);

        // =====================================================
        // 📱 iPhone 15 Plus
        // =====================================================
        AppleDeviceSpec iphone15Plus = new AppleDeviceSpec("iphone", "iPhone 15 Plus");
        iphone15Plus.year           = "2023";
        iphone15Plus.identifier     = "iPhone15,5";
        iphone15Plus.os             = "iOS";
        iphone15Plus.soc            = "A16 Bionic";
        iphone15Plus.chipset        = "A16 Bionic";
        iphone15Plus.arch           = "ARMv8.6-A";
        iphone15Plus.processNode    = "4 nm";
        iphone15Plus.cpu            = "Apple CPU";
        iphone15Plus.cpuCores       = 6;
        iphone15Plus.gpu            = "Apple GPU";
        iphone15Plus.gpuCores       = 5;
        iphone15Plus.metalFeatureSet= "Metal 3";

        iphone15Plus.ram            = "6 GB";
        iphone15Plus.ramType        = "LPDDR5";
        iphone15Plus.storageBase    = "128 GB";
        iphone15Plus.storageOptions = "128 / 256 / 512 GB";
        iphone15Plus.screen         = "6.7\"";
        iphone15Plus.display        = "OLED Super Retina XDR";
        iphone15Plus.resolution     = "2796 × 1290";
        iphone15Plus.refreshRate    = "60 Hz";
        iphone15Plus.displayOut     = "DisplayPort (USB-C)";

        iphone15Plus.has5G          = true;
        iphone15Plus.hasLTE         = true;
        iphone15Plus.cellular       = "5G NR, LTE";
        iphone15Plus.modem          = "Qualcomm Snapdragon X70";
        iphone15Plus.wifi           = "Wi-Fi 6";
        iphone15Plus.bluetooth      = "Bluetooth 5.3";
        iphone15Plus.hasNFC         = true;
        iphone15Plus.hasAirDrop     = true;
        iphone15Plus.hasAirPlay     = true;
        iphone15Plus.gps            = "GPS, GLONASS, Galileo, QZSS";
        iphone15Plus.hasCompass     = true;
        iphone15Plus.hasGyro        = true;
        iphone15Plus.hasAccel       = true;
        iphone15Plus.hasBarometer   = true;

        iphone15Plus.simSlots       = "Dual eSIM";
        iphone15Plus.hasESim        = true;
        iphone15Plus.port           = "USB-C";
        iphone15Plus.usbStandard    = "USB 2";

        iphone15Plus.speakers       = "Stereo speakers";
        iphone15Plus.microphones   = "Multiple microphones";
        iphone15Plus.hasDolby       = true;
        iphone15Plus.hasJack        = false;

        iphone15Plus.cameraMain      = "48 MP Wide";
        iphone15Plus.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15Plus.cameraTele      = null;
        iphone15Plus.cameraFront     = "12 MP TrueDepth";
        iphone15Plus.cameraVideo     = "4K@60fps HDR";

        iphone15Plus.hasFaceID       = true;
        iphone15Plus.biometrics      = "Face ID";
        iphone15Plus.hasFastCharge   = true;
        iphone15Plus.hasWirelessCharge = true;

        // =====================================================
        // 🔋 BATTERY — SERVICE / REFERENCE DATA
        // =====================================================
        iphone15Plus.batteryMah          = 4383;   // model-based reference
        iphone15Plus.batteryVoltage      = 3.82f;  // Apple nominal
        iphone15Plus.batteryChemistry    = "Lithium-Ion (pouch)";
        iphone15Plus.batteryDesignCycles = 500;
        iphone15Plus.batteryCharging     =
                "Fast wired charging, MagSafe 15W, Qi wireless";
        iphone15Plus.batteryNotes        =
                "Larger chassis allows higher capacity and improved endurance";

        iphone15Plus.thermalNote =
                "Larger enclosure provides better passive thermal dissipation";
        iphone15Plus.notes =
                "Factory specifications only. No runtime system probing.";

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
        iphone15Pro.metalFeatureSet = "Metal 3 (Hardware Ray Tracing)";

        iphone15Pro.ram             = "8 GB";
        iphone15Pro.ramType         = "LPDDR5";
        iphone15Pro.storageBase     = "128 GB";
        iphone15Pro.storageOptions  = "128 / 256 / 512 GB / 1 TB";
        iphone15Pro.screen          = "6.1\"";
        iphone15Pro.display         = "OLED Super Retina XDR (ProMotion)";
        iphone15Pro.resolution      = "2556 × 1179";
        iphone15Pro.refreshRate     = "120 Hz";
        iphone15Pro.displayOut      = "DisplayPort (USB-C)";

        iphone15Pro.has5G            = true;
        iphone15Pro.hasLTE           = true;
        iphone15Pro.cellular         = "5G NR, LTE";
        iphone15Pro.modem            = "Qualcomm Snapdragon X70";
        iphone15Pro.wifi             = "Wi-Fi 6E";
        iphone15Pro.bluetooth        = "Bluetooth 5.3";
        iphone15Pro.hasNFC           = true;
        iphone15Pro.hasAirDrop       = true;
        iphone15Pro.hasAirPlay       = true;
        iphone15Pro.gps              = "GPS, GLONASS, Galileo, QZSS";
        iphone15Pro.hasCompass       = true;
        iphone15Pro.hasGyro          = true;
        iphone15Pro.hasAccel         = true;
        iphone15Pro.hasBarometer     = true;

        iphone15Pro.simSlots         = "Dual eSIM";
        iphone15Pro.hasESim          = true;
        iphone15Pro.port             = "USB-C";
        iphone15Pro.usbStandard      = "USB 3 (up to 10 Gbps)";

        iphone15Pro.speakers         = "Stereo speakers";
        iphone15Pro.microphones     = "Multiple microphones";
        iphone15Pro.hasDolby         = true;
        iphone15Pro.hasJack          = false;

        iphone15Pro.cameraMain       = "48 MP Pro";
        iphone15Pro.cameraUltraWide  = "12 MP Ultra-Wide";
        iphone15Pro.cameraTele       = "12 MP Telephoto (3×)";
        iphone15Pro.cameraFront      = "12 MP TrueDepth";
        iphone15Pro.cameraVideo      = "4K ProRes, Spatial Video support";

        iphone15Pro.hasFaceID        = true;
        iphone15Pro.biometrics       = "Face ID";
        iphone15Pro.hasFastCharge    = true;
        iphone15Pro.hasWirelessCharge= true;

        // =====================================================
        // 🔋 BATTERY — SERVICE / REFERENCE DATA
        // =====================================================
        iphone15Pro.batteryMah          = 3274;   // model-based reference
        iphone15Pro.batteryVoltage      = 3.82f;  // Apple nominal
        iphone15Pro.batteryChemistry    = "Lithium-Ion (pouch)";
        iphone15Pro.batteryDesignCycles = 500;
        iphone15Pro.batteryCharging     =
                "Fast wired charging, MagSafe 15W, Qi wireless";
        iphone15Pro.batteryNotes        =
                "Pro chassis with improved thermal efficiency vs base models";

        iphone15Pro.thermalNote =
                "Titanium frame improves heat dissipation under sustained load";
        iphone15Pro.notes =
                "Pro model with A17 Pro, hardware ray tracing and high-speed USB-C data.";

        MAP.put("iphone 15 pro", iphone15Pro);


        // =====================================================
        // 📱 iPhone 15 Pro Max
        // =====================================================
        AppleDeviceSpec iphone15ProMax = new AppleDeviceSpec("iphone", "iPhone 15 Pro Max");
        iphone15ProMax.year            = "2023";
        iphone15ProMax.identifier      = "iPhone16,2";
        iphone15ProMax.os              = "iOS";
        iphone15ProMax.soc             = "A17 Pro";
        iphone15ProMax.chipset         = "A17 Pro";
        iphone15ProMax.arch            = "ARMv9";
        iphone15ProMax.processNode     = "3 nm";
        iphone15ProMax.cpu             = "Apple CPU";
        iphone15ProMax.cpuCores        = 6;
        iphone15ProMax.gpu             = "Apple GPU";
        iphone15ProMax.gpuCores        = 6;
        iphone15ProMax.metalFeatureSet = "Metal 3 (Hardware Ray Tracing)";

        iphone15ProMax.ram             = "8 GB";
        iphone15ProMax.ramType         = "LPDDR5";
        iphone15ProMax.storageBase     = "256 GB";
        iphone15ProMax.storageOptions  = "256 / 512 GB / 1 TB";
        iphone15ProMax.screen          = "6.7\"";
        iphone15ProMax.display         = "OLED Super Retina XDR (ProMotion)";
        iphone15ProMax.resolution      = "2796 × 1290";
        iphone15ProMax.refreshRate     = "120 Hz";
        iphone15ProMax.displayOut      = "DisplayPort (USB-C)";

        iphone15ProMax.has5G            = true;
        iphone15ProMax.hasLTE           = true;
        iphone15ProMax.cellular         = "5G NR, LTE";
        iphone15ProMax.modem            = "Qualcomm Snapdragon X70";
        iphone15ProMax.wifi             = "Wi-Fi 6E";
        iphone15ProMax.bluetooth        = "Bluetooth 5.3";
        iphone15ProMax.hasNFC           = true;
        iphone15ProMax.hasAirDrop       = true;
        iphone15ProMax.hasAirPlay       = true;
        iphone15ProMax.gps              = "GPS, GLONASS, Galileo, QZSS";
        iphone15ProMax.hasCompass       = true;
        iphone15ProMax.hasGyro          = true;
        iphone15ProMax.hasAccel         = true;
        iphone15ProMax.hasBarometer     = true;

        iphone15ProMax.simSlots         = "Dual eSIM";
        iphone15ProMax.hasESim          = true;
        iphone15ProMax.port             = "USB-C";
        iphone15ProMax.usbStandard      = "USB 3 (up to 10 Gbps)";

        iphone15ProMax.speakers         = "Stereo speakers";
        iphone15ProMax.microphones     = "Multiple microphones";
        iphone15ProMax.hasDolby         = true;
        iphone15ProMax.hasJack          = false;

        iphone15ProMax.cameraMain       = "48 MP Pro";
        iphone15ProMax.cameraUltraWide  = "12 MP Ultra-Wide";
        iphone15ProMax.cameraTele       = "12 MP Telephoto (5×)";
        iphone15ProMax.cameraFront      = "12 MP TrueDepth";
        iphone15ProMax.cameraVideo      = "4K ProRes, Spatial Video support";

        iphone15ProMax.hasFaceID        = true;
        iphone15ProMax.biometrics       = "Face ID";
        iphone15ProMax.hasFastCharge    = true;
        iphone15ProMax.hasWirelessCharge= true;

        // =====================================================
        // 🔋 BATTERY — SERVICE / REFERENCE DATA
        // =====================================================
        iphone15ProMax.batteryMah          = 4422;   // model-based reference
        iphone15ProMax.batteryVoltage      = 3.82f;  // Apple nominal
        iphone15ProMax.batteryChemistry    = "Lithium-Ion (pouch)";
        iphone15ProMax.batteryDesignCycles = 500;
        iphone15ProMax.batteryCharging     =
                "Fast wired charging, MagSafe 15W, Qi wireless";
        iphone15ProMax.batteryNotes        =
                "Largest capacity in the series; improved endurance and thermal headroom";

        iphone15ProMax.thermalNote =
                "Titanium frame and larger chassis provide better sustained thermal performance";
        iphone15ProMax.notes =
                "Top-tier configuration with exclusive 5× telephoto and maximum performance headroom.";

        MAP.put("iphone 15 pro max", iphone15ProMax);

// =========================================================
// 📱 iPHONE 14 SERIES — FULL SPECS
// =========================================================

// ---------------------------------------------------------
// 📱 iPhone 14
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
iphone14.displayVariants = "Standard 60 Hz (no ProMotion)";

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

iphone14.speakers        = "Stereo speakers";
iphone14.microphones    = "Multiple microphones";
iphone14.hasDolby        = true;
iphone14.hasJack         = false;

iphone14.cameraMain      = "12 MP Wide (OIS)";
iphone14.cameraUltraWide = "12 MP Ultra-Wide";
iphone14.cameraTele      = null;
iphone14.cameraFront     = "12 MP TrueDepth";
iphone14.cameraVideo     = "4K@60fps HDR";
iphone14.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide)";

iphone14.hasFaceID       = true;
iphone14.hasTouchID      = false;
iphone14.biometrics      = "Face ID";

iphone14.hasFastCharge      = true;
iphone14.hasWirelessCharge = true;
iphone14.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone14.batteryMah          = 3279;   // model-based reference
iphone14.batteryVoltage      = 3.82f;  // Apple nominal
iphone14.batteryWh           =
        (iphone14.batteryMah * iphone14.batteryVoltage) / 1000f;
iphone14.batteryChemistry    = "Lithium-Ion (pouch)";
iphone14.batteryDesignCycles = 500;
iphone14.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone14.batteryNotes        =
        "Similar capacity to iPhone 13; optimized efficiency with A15 Bionic";

iphone14.thermalNote =
        "No public thermal sensor access (Apple restriction)";
iphone14.notes =
        "Same SoC generation as iPhone 13 Pro with incremental GPU and safety improvements.";

MAP.put("iphone 14", iphone14);
        
// ---------------------------------------------------------
// ---------------------------------------------------------
// 📱 iPhone 14 Plus
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
iphone14Plus.displayVariants = "Standard 60 Hz (no ProMotion)";

iphone14Plus.has5G           = true;
iphone14Plus.hasLTE          = true;
iphone14Plus.cellular        = "5G NR / LTE";
iphone14Plus.modem           = "Qualcomm Snapdragon X65";
iphone14Plus.wifi            = "Wi-Fi 6 (802.11ax)";
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

iphone14Plus.speakers        = "Stereo speakers";
iphone14Plus.microphones    = "Multiple microphones";
iphone14Plus.hasDolby        = true;
iphone14Plus.hasJack         = false;

iphone14Plus.cameraMain      = "12 MP Wide (OIS)";
iphone14Plus.cameraUltraWide = "12 MP Ultra-Wide";
iphone14Plus.cameraTele      = null;
iphone14Plus.cameraFront     = "12 MP TrueDepth";
iphone14Plus.cameraVideo     = "4K@60fps HDR";
iphone14Plus.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide)";

iphone14Plus.hasFaceID       = true;
iphone14Plus.hasTouchID      = false;
iphone14Plus.biometrics      = "Face ID";

iphone14Plus.hasFastCharge      = true;
iphone14Plus.hasWirelessCharge = true;
iphone14Plus.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone14Plus.batteryMah          = 4325;   // model-based reference
iphone14Plus.batteryVoltage      = 3.82f;  // Apple nominal
iphone14Plus.batteryWh           =
        (iphone14Plus.batteryMah * iphone14Plus.batteryVoltage) / 1000f;
iphone14Plus.batteryChemistry    = "Lithium-Ion (pouch)";
iphone14Plus.batteryDesignCycles = 500;
iphone14Plus.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone14Plus.batteryNotes        =
        "Very large capacity; excellent endurance for non-Pro model";

iphone14Plus.thermalNote =
        "No public thermal sensor access (Apple restriction)";
iphone14Plus.notes =
        "Largest non-Pro iPhone; prioritizes battery life over refresh rate.";

MAP.put("iphone 14 plus", iphone14Plus);

// ---------------------------------------------------------
// 📱 iPhone 14 Pro
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
iphone14Pro.displayVariants = "ProMotion with adaptive refresh (1–120 Hz)";

iphone14Pro.has5G           = true;
iphone14Pro.hasLTE          = true;
iphone14Pro.cellular        = "5G NR / LTE";
iphone14Pro.modem           = "Qualcomm Snapdragon X65";
iphone14Pro.wifi            = "Wi-Fi 6 (802.11ax)";
iphone14Pro.bluetooth       = "Bluetooth 5.3";
iphone14Pro.hasNFC          = true;
iphone14Pro.hasAirDrop      = true;
iphone14Pro.hasAirPlay      = true;
iphone14Pro.gps             = "Dual-frequency GPS (L1 + L5)";
iphone14Pro.hasCompass      = true;
iphone14Pro.hasGyro         = true;
iphone14Pro.hasAccel        = true;
iphone14Pro.hasBarometer    = true;

iphone14Pro.simSlots        = "eSIM (US) / Nano-SIM + eSIM";
iphone14Pro.hasESim         = true;
iphone14Pro.port            = "Lightning";
iphone14Pro.usbStandard     = "USB 2.0";

iphone14Pro.speakers        = "Stereo speakers";
iphone14Pro.microphones    = "Multiple microphones";
iphone14Pro.hasDolby        = true;
iphone14Pro.hasJack         = false;

iphone14Pro.cameraMain      = "48 MP Wide (Sensor-shift OIS)";
iphone14Pro.cameraUltraWide = "12 MP Ultra-Wide";
iphone14Pro.cameraTele      = "12 MP Telephoto (3×)";
iphone14Pro.cameraFront     = "12 MP TrueDepth";
iphone14Pro.cameraVideo     = "4K@60fps ProRes / HDR";
iphone14Pro.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto)";

iphone14Pro.hasFaceID       = true;
iphone14Pro.hasTouchID      = false;
iphone14Pro.biometrics      = "Face ID";

iphone14Pro.hasFastCharge      = true;
iphone14Pro.hasWirelessCharge = true;
iphone14Pro.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone14Pro.batteryMah          = 3200;   // model-based reference
iphone14Pro.batteryVoltage      = 3.82f;  // Apple nominal
iphone14Pro.batteryWh           =
        (iphone14Pro.batteryMah * iphone14Pro.batteryVoltage) / 1000f;
iphone14Pro.batteryChemistry    = "Lithium-Ion (pouch)";
iphone14Pro.batteryDesignCycles = 500;
iphone14Pro.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone14Pro.batteryNotes        =
        "Balanced capacity; ProMotion and A16 efficiency optimize daily endurance";

iphone14Pro.thermalNote =
        "Thermal sensors not accessible via iOS; stainless steel frame";
iphone14Pro.notes =
        "First iPhone with Dynamic Island; ProMotion display and Pro camera system.";

MAP.put("iphone 14 pro", iphone14Pro);

// ---------------------------------------------------------
// ---------------------------------------------------------
// 📱 iPhone 14 Pro Max
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
iphone14ProMax.displayVariants = "ProMotion with adaptive refresh (1–120 Hz)";

iphone14ProMax.has5G           = true;
iphone14ProMax.hasLTE          = true;
iphone14ProMax.cellular        = "5G NR / LTE";
iphone14ProMax.modem           = "Qualcomm Snapdragon X65";
iphone14ProMax.wifi            = "Wi-Fi 6 (802.11ax)";
iphone14ProMax.bluetooth       = "Bluetooth 5.3";
iphone14ProMax.hasNFC          = true;
iphone14ProMax.hasAirDrop      = true;
iphone14ProMax.hasAirPlay      = true;
iphone14ProMax.gps             = "Dual-frequency GPS (L1 + L5)";
iphone14ProMax.hasCompass      = true;
iphone14ProMax.hasGyro         = true;
iphone14ProMax.hasAccel        = true;
iphone14ProMax.hasBarometer    = true;

iphone14ProMax.simSlots        = "eSIM (US) / Nano-SIM + eSIM";
iphone14ProMax.hasESim         = true;
iphone14ProMax.port            = "Lightning";
iphone14ProMax.usbStandard     = "USB 2.0";

iphone14ProMax.speakers        = "Stereo speakers";
iphone14ProMax.microphones    = "Multiple microphones";
iphone14ProMax.hasDolby        = true;
iphone14ProMax.hasJack         = false;

iphone14ProMax.cameraMain      = "48 MP Wide (Sensor-shift OIS)";
iphone14ProMax.cameraUltraWide = "12 MP Ultra-Wide";
iphone14ProMax.cameraTele      = "12 MP Telephoto (3×)";
iphone14ProMax.cameraFront     = "12 MP TrueDepth";
iphone14ProMax.cameraVideo     = "4K@60fps ProRes / HDR";
iphone14ProMax.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto)";

iphone14ProMax.hasFaceID       = true;
iphone14ProMax.hasTouchID      = false;
iphone14ProMax.biometrics      = "Face ID";

iphone14ProMax.hasFastCharge      = true;
iphone14ProMax.hasWirelessCharge = true;
iphone14ProMax.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone14ProMax.batteryMah          = 4323;   // model-based reference
iphone14ProMax.batteryVoltage      = 3.82f;  // Apple nominal
iphone14ProMax.batteryWh           =
        (iphone14ProMax.batteryMah * iphone14ProMax.batteryVoltage) / 1000f;
iphone14ProMax.batteryChemistry    = "Lithium-Ion (pouch)";
iphone14ProMax.batteryDesignCycles = 500;
iphone14ProMax.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone14ProMax.batteryNotes        =
        "Largest battery in the iPhone 14 series; excellent endurance for Pro workloads";

iphone14ProMax.thermalNote =
        "Thermal sensors not accessible via iOS; larger chassis aids passive heat dissipation";
iphone14ProMax.notes =
        "Largest Pro model with maximum battery capacity and sustained performance headroom.";

MAP.put("iphone 14 pro max", iphone14ProMax);
        
// =========================================================
// 📱 iPHONE 13 SERIES — FULL SPECS
// =========================================================

// ---------------------------------------------------------
// 📱 iPhone 13 mini
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
iphone13Mini.displayVariants = "Compact display, standard 60 Hz (no ProMotion)";

iphone13Mini.has5G           = true;
iphone13Mini.hasLTE          = true;
iphone13Mini.cellular        = "5G NR / LTE";
iphone13Mini.modem           = "Qualcomm Snapdragon X60";
iphone13Mini.wifi            = "Wi-Fi 6 (802.11ax)";
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

iphone13Mini.speakers        = "Stereo speakers";
iphone13Mini.microphones    = "Multiple microphones";
iphone13Mini.hasDolby        = true;
iphone13Mini.hasJack         = false;

iphone13Mini.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13Mini.cameraUltraWide = "12 MP Ultra-Wide";
iphone13Mini.cameraTele      = null;
iphone13Mini.cameraFront     = "12 MP TrueDepth";
iphone13Mini.cameraVideo     = "4K@60fps HDR";
iphone13Mini.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide)";

iphone13Mini.hasFaceID       = true;
iphone13Mini.hasTouchID      = false;
iphone13Mini.biometrics      = "Face ID";

iphone13Mini.hasFastCharge      = true;
iphone13Mini.hasWirelessCharge = true;
iphone13Mini.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone13Mini.batteryMah          = 2406;   // model-based reference
iphone13Mini.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone13Mini.batteryWh           =
        (iphone13Mini.batteryMah * iphone13Mini.batteryVoltage) / 1000f;
iphone13Mini.batteryChemistry    = "Lithium-Ion (pouch)";
iphone13Mini.batteryDesignCycles = 500;
iphone13Mini.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone13Mini.batteryNotes        =
        "Smallest capacity in the lineup; efficiency-focused A15 mitigates size constraints";

iphone13Mini.thermalNote =
        "No public thermal sensor access (Apple restriction)";
iphone13Mini.notes =
        "Smallest iPhone with A15 Bionic; prioritizes compact size over endurance.";

MAP.put("iphone 13 mini", iphone13Mini);

// ---------------------------------------------------------
// 📱 iPhone 13
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
iphone13.displayVariants = "Standard 60 Hz (no ProMotion)";

iphone13.has5G           = true;
iphone13.hasLTE          = true;
iphone13.cellular        = "5G NR / LTE";
iphone13.modem           = "Qualcomm Snapdragon X60";
iphone13.wifi            = "Wi-Fi 6 (802.11ax)";
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

iphone13.speakers        = "Stereo speakers";
iphone13.microphones    = "Multiple microphones";
iphone13.hasDolby        = true;
iphone13.hasJack         = false;

iphone13.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13.cameraUltraWide = "12 MP Ultra-Wide";
iphone13.cameraTele      = null;
iphone13.cameraFront     = "12 MP TrueDepth";
iphone13.cameraVideo     = "4K@60fps HDR";
iphone13.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide)";

iphone13.hasFaceID       = true;
iphone13.hasTouchID      = false;
iphone13.biometrics      = "Face ID";

iphone13.hasFastCharge      = true;
iphone13.hasWirelessCharge = true;
iphone13.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone13.batteryMah          = 3240;   // model-based reference
iphone13.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone13.batteryWh           =
        (iphone13.batteryMah * iphone13.batteryVoltage) / 1000f;
iphone13.batteryChemistry    = "Lithium-Ion (pouch)";
iphone13.batteryDesignCycles = 500;
iphone13.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone13.batteryNotes        =
        "Balanced capacity; noticeably better endurance than iPhone 12";

iphone13.thermalNote =
        "No public thermal sensor access (Apple restriction)";
iphone13.notes =
        "Base model of iPhone 13 lineup; improved battery life over predecessor.";

MAP.put("iphone 13", iphone13);

// ---------------------------------------------------------
// ---------------------------------------------------------
// 📱 iPhone 13 Pro
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
iphone13Pro.displayVariants = "ProMotion with adaptive refresh (10–120 Hz)";

iphone13Pro.has5G           = true;
iphone13Pro.hasLTE          = true;
iphone13Pro.cellular        = "5G NR / LTE";
iphone13Pro.modem           = "Qualcomm Snapdragon X60";
iphone13Pro.wifi            = "Wi-Fi 6 (802.11ax)";
iphone13Pro.bluetooth       = "Bluetooth 5.0";
iphone13Pro.hasNFC          = true;
iphone13Pro.hasAirDrop      = true;
iphone13Pro.hasAirPlay      = true;
iphone13Pro.gps             = "Dual-frequency GPS (L1 + L5)";
iphone13Pro.hasCompass      = true;
iphone13Pro.hasGyro         = true;
iphone13Pro.hasAccel        = true;
iphone13Pro.hasBarometer    = true;

iphone13Pro.simSlots        = "Nano-SIM + eSIM";
iphone13Pro.hasESim         = true;
iphone13Pro.port            = "Lightning";
iphone13Pro.usbStandard     = "USB 2.0";

iphone13Pro.speakers        = "Stereo speakers";
iphone13Pro.microphones    = "Multiple microphones";
iphone13Pro.hasDolby        = true;
iphone13Pro.hasJack         = false;

iphone13Pro.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13Pro.cameraUltraWide = "12 MP Ultra-Wide";
iphone13Pro.cameraTele      = "12 MP Telephoto (3×)";
iphone13Pro.cameraFront     = "12 MP TrueDepth";
iphone13Pro.cameraVideo     = "4K@60fps ProRes / HDR";
iphone13Pro.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto)";

iphone13Pro.hasFaceID       = true;
iphone13Pro.hasTouchID      = false;
iphone13Pro.biometrics      = "Face ID";

iphone13Pro.hasFastCharge      = true;
iphone13Pro.hasWirelessCharge = true;
iphone13Pro.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone13Pro.batteryMah          = 3095;   // model-based reference
iphone13Pro.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone13Pro.batteryWh           =
        (iphone13Pro.batteryMah * iphone13Pro.batteryVoltage) / 1000f;
iphone13Pro.batteryChemistry    = "Lithium-Ion (pouch)";
iphone13Pro.batteryDesignCycles = 500;
iphone13Pro.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone13Pro.batteryNotes        =
        "ProMotion display increases power demand; efficiency gains offset usage";

iphone13Pro.thermalNote =
        "Thermal sensors not accessible via iOS; stainless steel frame";
iphone13Pro.notes =
        "First Pro iPhone with ProMotion display and enhanced camera system.";

MAP.put("iphone 13 pro", iphone13Pro);

// ---------------------------------------------------------
// 📱 iPhone 13 Pro Max
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
iphone13ProMax.displayVariants = "ProMotion with adaptive refresh (10–120 Hz)";

iphone13ProMax.has5G           = true;
iphone13ProMax.hasLTE          = true;
iphone13ProMax.cellular        = "5G NR / LTE";
iphone13ProMax.modem           = "Qualcomm Snapdragon X60";
iphone13ProMax.wifi            = "Wi-Fi 6 (802.11ax)";
iphone13ProMax.bluetooth       = "Bluetooth 5.0";
iphone13ProMax.hasNFC          = true;
iphone13ProMax.hasAirDrop      = true;
iphone13ProMax.hasAirPlay      = true;
iphone13ProMax.gps             = "Dual-frequency GPS (L1 + L5)";
iphone13ProMax.hasCompass      = true;
iphone13ProMax.hasGyro         = true;
iphone13ProMax.hasAccel        = true;
iphone13ProMax.hasBarometer    = true;

iphone13ProMax.simSlots        = "Nano-SIM + eSIM";
iphone13ProMax.hasESim         = true;
iphone13ProMax.port            = "Lightning";
iphone13ProMax.usbStandard     = "USB 2.0";

iphone13ProMax.speakers        = "Stereo speakers";
iphone13ProMax.microphones    = "Multiple microphones";
iphone13ProMax.hasDolby        = true;
iphone13ProMax.hasJack         = false;

iphone13ProMax.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone13ProMax.cameraUltraWide = "12 MP Ultra-Wide";
iphone13ProMax.cameraTele      = "12 MP Telephoto (3×)";
iphone13ProMax.cameraFront     = "12 MP TrueDepth";
iphone13ProMax.cameraVideo     = "4K@60fps ProRes / HDR";
iphone13ProMax.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto)";

iphone13ProMax.hasFaceID       = true;
iphone13ProMax.hasTouchID      = false;
iphone13ProMax.biometrics      = "Face ID";

iphone13ProMax.hasFastCharge      = true;
iphone13ProMax.hasWirelessCharge = true;
iphone13ProMax.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone13ProMax.batteryMah          = 4352;   // model-based reference
iphone13ProMax.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone13ProMax.batteryWh           =
        (iphone13ProMax.batteryMah * iphone13ProMax.batteryVoltage) / 1000f;
iphone13ProMax.batteryChemistry    = "Lithium-Ion (pouch)";
iphone13ProMax.batteryDesignCycles = 500;
iphone13ProMax.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone13ProMax.batteryNotes        =
        "Largest battery in iPhone 13 lineup; excellent endurance even with ProMotion";

iphone13ProMax.thermalNote =
        "Thermal sensors not accessible via iOS; larger chassis aids passive dissipation";
iphone13ProMax.notes =
        "Largest iPhone 13 model; maximum battery life and sustained Pro performance.";

MAP.put("iphone 13 pro max", iphone13ProMax);
        
// =========================================================
// 📱 iPHONE 12 SERIES — FULL SPECS
// =========================================================

// ---------------------------------------------------------
// 📱 iPhone 12 mini
// ---------------------------------------------------------
AppleDeviceSpec iphone12Mini = new AppleDeviceSpec("iphone", "iPhone 12 mini");
iphone12Mini.year            = "2020";
iphone12Mini.identifier      = "iPhone13,1";
iphone12Mini.os              = "iOS";
iphone12Mini.soc             = "A14 Bionic";
iphone12Mini.chipset         = "A14 Bionic";
iphone12Mini.arch            = "ARMv8.5-A";
iphone12Mini.processNode     = "5 nm";
iphone12Mini.cpu             = "Apple CPU";
iphone12Mini.cpuCores        = 6;
iphone12Mini.gpu             = "Apple GPU";
iphone12Mini.gpuCores        = 4;
iphone12Mini.metalFeatureSet = "Metal 3";

iphone12Mini.ram             = "4 GB";
iphone12Mini.ramType         = "LPDDR4X";
iphone12Mini.storageBase     = "64 GB";
iphone12Mini.storageOptions  = "64 / 128 / 256 GB";

iphone12Mini.screen          = "5.4\"";
iphone12Mini.display         = "OLED Super Retina XDR";
iphone12Mini.resolution      = "2340 × 1080";
iphone12Mini.refreshRate     = "60 Hz";
iphone12Mini.displayOut      = "Lightning Digital AV (adapter)";
iphone12Mini.displayVariants = "Compact display, standard 60 Hz (no ProMotion)";

iphone12Mini.has5G           = true;
iphone12Mini.hasLTE          = true;
iphone12Mini.cellular        = "5G NR / LTE";
iphone12Mini.modem           = "Qualcomm Snapdragon X55";
iphone12Mini.wifi            = "Wi-Fi 6 (802.11ax)";
iphone12Mini.bluetooth       = "Bluetooth 5.0";
iphone12Mini.hasNFC          = true;
iphone12Mini.hasAirDrop      = true;
iphone12Mini.hasAirPlay      = true;
iphone12Mini.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone12Mini.hasCompass      = true;
iphone12Mini.hasGyro         = true;
iphone12Mini.hasAccel        = true;
iphone12Mini.hasBarometer    = true;

iphone12Mini.simSlots        = "Nano-SIM + eSIM";
iphone12Mini.hasESim         = true;
iphone12Mini.port            = "Lightning";
iphone12Mini.usbStandard     = "USB 2.0";

iphone12Mini.speakers        = "Stereo speakers";
iphone12Mini.microphones    = "Multiple microphones";
iphone12Mini.hasDolby        = true;
iphone12Mini.hasJack         = false;

iphone12Mini.cameraMain      = "12 MP Wide (OIS)";
iphone12Mini.cameraUltraWide = "12 MP Ultra-Wide";
iphone12Mini.cameraTele      = null;
iphone12Mini.cameraFront     = "12 MP TrueDepth";
iphone12Mini.cameraVideo     = "4K@60fps HDR";
iphone12Mini.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide)";

iphone12Mini.hasFaceID       = true;
iphone12Mini.hasTouchID      = false;
iphone12Mini.biometrics      = "Face ID";

iphone12Mini.hasFastCharge      = true;
iphone12Mini.hasWirelessCharge = true;
iphone12Mini.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone12Mini.batteryMah          = 2227;   // model-based reference
iphone12Mini.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone12Mini.batteryWh           =
        (iphone12Mini.batteryMah * iphone12Mini.batteryVoltage) / 1000f;
iphone12Mini.batteryChemistry    = "Lithium-Ion (pouch)";
iphone12Mini.batteryDesignCycles = 500;
iphone12Mini.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone12Mini.batteryNotes        =
        "Smallest capacity in the lineup; first mini with 5G impacts endurance";

iphone12Mini.thermalNote =
        "No public thermal sensor access (Apple restriction)";
iphone12Mini.notes =
        "First mini iPhone with 5G; compact form factor prioritizes size over battery life.";

MAP.put("iphone 12 mini", iphone12Mini);

// ---------------------------------------------------------
// 📱 iPhone 12
// ---------------------------------------------------------
AppleDeviceSpec iphone12 = new AppleDeviceSpec("iphone", "iPhone 12");
iphone12.year            = "2020";
iphone12.identifier      = "iPhone13,2";
iphone12.os              = "iOS";
iphone12.soc             = "A14 Bionic";
iphone12.chipset         = "A14 Bionic";
iphone12.arch            = "ARMv8.5-A";
iphone12.processNode     = "5 nm";
iphone12.cpu             = "Apple CPU";
iphone12.cpuCores        = 6;
iphone12.gpu             = "Apple GPU";
iphone12.gpuCores        = 4;
iphone12.metalFeatureSet = "Metal 3";

iphone12.ram             = "4 GB";
iphone12.ramType         = "LPDDR4X";
iphone12.storageBase     = "64 GB";
iphone12.storageOptions  = "64 / 128 / 256 GB";

iphone12.screen          = "6.1\"";
iphone12.display         = "OLED Super Retina XDR";
iphone12.resolution      = "2532 × 1170";
iphone12.refreshRate     = "60 Hz";
iphone12.displayOut      = "Lightning Digital AV (adapter)";
iphone12.displayVariants = "Standard 60 Hz (no ProMotion)";

iphone12.has5G           = true;
iphone12.hasLTE          = true;
iphone12.cellular        = "5G NR / LTE";
iphone12.modem           = "Qualcomm Snapdragon X55";
iphone12.wifi            = "Wi-Fi 6 (802.11ax)";
iphone12.bluetooth       = "Bluetooth 5.0";
iphone12.hasNFC          = true;
iphone12.hasAirDrop      = true;
iphone12.hasAirPlay      = true;
iphone12.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone12.hasCompass      = true;
iphone12.hasGyro         = true;
iphone12.hasAccel        = true;
iphone12.hasBarometer    = true;

iphone12.simSlots        = "Nano-SIM + eSIM";
iphone12.hasESim         = true;
iphone12.port            = "Lightning";
iphone12.usbStandard     = "USB 2.0";

iphone12.speakers        = "Stereo speakers";
iphone12.microphones    = "Multiple microphones";
iphone12.hasDolby        = true;
iphone12.hasJack         = false;

iphone12.cameraMain      = "12 MP Wide (OIS)";
iphone12.cameraUltraWide = "12 MP Ultra-Wide";
iphone12.cameraTele      = null;
iphone12.cameraFront     = "12 MP TrueDepth";
iphone12.cameraVideo     = "4K@60fps HDR";
iphone12.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide)";

iphone12.hasFaceID       = true;
iphone12.hasTouchID      = false;
iphone12.biometrics      = "Face ID";

iphone12.hasFastCharge      = true;
iphone12.hasWirelessCharge = true;
iphone12.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone12.batteryMah          = 2815;   // model-based reference
iphone12.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone12.batteryWh           =
        (iphone12.batteryMah * iphone12.batteryVoltage) / 1000f;
iphone12.batteryChemistry    = "Lithium-Ion (pouch)";
iphone12.batteryDesignCycles = 500;
iphone12.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone12.batteryNotes        =
        "Moderate capacity; first 5G generation impacts endurance vs 11 series";

iphone12.thermalNote =
        "Thermal data not exposed by iOS; aluminum frame aids passive dissipation";
iphone12.notes =
        "First iPhone with A14 and OLED across the lineup; introduces 5G support.";

MAP.put("iphone 12", iphone12);
        
// ---------------------------------------------------------
// ---------------------------------------------------------
// 📱 iPhone 12 Pro
// ---------------------------------------------------------
AppleDeviceSpec iphone12Pro = new AppleDeviceSpec("iphone", "iPhone 12 Pro");
iphone12Pro.year            = "2020";
iphone12Pro.identifier      = "iPhone13,3";
iphone12Pro.os              = "iOS";
iphone12Pro.soc             = "A14 Bionic";
iphone12Pro.chipset         = "A14 Bionic";
iphone12Pro.arch            = "ARMv8.5-A";
iphone12Pro.processNode     = "5 nm";
iphone12Pro.cpu             = "Apple CPU";
iphone12Pro.cpuCores        = 6;
iphone12Pro.gpu             = "Apple GPU";
iphone12Pro.gpuCores        = 4;
iphone12Pro.metalFeatureSet = "Metal 3";

iphone12Pro.ram             = "6 GB";
iphone12Pro.ramType         = "LPDDR4X";
iphone12Pro.storageBase     = "128 GB";
iphone12Pro.storageOptions  = "128 / 256 / 512 GB";

iphone12Pro.screen          = "6.1\"";
iphone12Pro.display         = "OLED Super Retina XDR";
iphone12Pro.resolution      = "2532 × 1170";
iphone12Pro.refreshRate     = "60 Hz";
iphone12Pro.displayOut      = "Lightning Digital AV (adapter)";
iphone12Pro.displayVariants = "Standard 60 Hz (no ProMotion)";

iphone12Pro.has5G           = true;
iphone12Pro.hasLTE          = true;
iphone12Pro.cellular        = "5G NR / LTE";
iphone12Pro.modem           = "Qualcomm Snapdragon X55";
iphone12Pro.wifi            = "Wi-Fi 6 (802.11ax)";
iphone12Pro.bluetooth       = "Bluetooth 5.0";
iphone12Pro.hasNFC          = true;
iphone12Pro.hasAirDrop      = true;
iphone12Pro.hasAirPlay      = true;
iphone12Pro.gps             = "Dual-frequency GPS (L1 + L5)";
iphone12Pro.hasCompass      = true;
iphone12Pro.hasGyro         = true;
iphone12Pro.hasAccel        = true;
iphone12Pro.hasBarometer    = true;

iphone12Pro.simSlots        = "Nano-SIM + eSIM";
iphone12Pro.hasESim         = true;
iphone12Pro.port            = "Lightning";
iphone12Pro.usbStandard     = "USB 2.0";

iphone12Pro.speakers        = "Stereo speakers";
iphone12Pro.microphones    = "Multiple microphones";
iphone12Pro.hasDolby        = true;
iphone12Pro.hasJack         = false;

iphone12Pro.cameraMain      = "12 MP Wide (OIS)";
iphone12Pro.cameraUltraWide = "12 MP Ultra-Wide";
iphone12Pro.cameraTele      = "12 MP Telephoto (2×)";
iphone12Pro.cameraFront     = "12 MP TrueDepth";
iphone12Pro.cameraVideo     = "4K@60fps HDR + Dolby Vision";
iphone12Pro.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto) + LiDAR";

iphone12Pro.hasFaceID       = true;
iphone12Pro.hasTouchID      = false;
iphone12Pro.biometrics      = "Face ID";

iphone12Pro.hasFastCharge      = true;
iphone12Pro.hasWirelessCharge = true;
iphone12Pro.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone12Pro.batteryMah          = 2815;   // model-based reference
iphone12Pro.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone12Pro.batteryWh           =
        (iphone12Pro.batteryMah * iphone12Pro.batteryVoltage) / 1000f;
iphone12Pro.batteryChemistry    = "Lithium-Ion (pouch)";
iphone12Pro.batteryDesignCycles = 500;
iphone12Pro.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone12Pro.batteryNotes        =
        "Similar capacity to iPhone 12; Pro features increase power demand";

iphone12Pro.thermalNote =
        "Thermal sensors not accessible via iOS; stainless steel frame aids heat spreading";
iphone12Pro.notes =
        "Introduced LiDAR scanner; Pro camera system with Dolby Vision recording.";

MAP.put("iphone 12 pro", iphone12Pro);

// ---------------------------------------------------------
// 📱 iPhone 12 Pro Max
// ---------------------------------------------------------
AppleDeviceSpec iphone12ProMax = new AppleDeviceSpec("iphone", "iPhone 12 Pro Max");
iphone12ProMax.year            = "2020";
iphone12ProMax.identifier      = "iPhone13,4";
iphone12ProMax.os              = "iOS";
iphone12ProMax.soc             = "A14 Bionic";
iphone12ProMax.chipset         = "A14 Bionic";
iphone12ProMax.arch            = "ARMv8.5-A";
iphone12ProMax.processNode     = "5 nm";
iphone12ProMax.cpu             = "Apple CPU";
iphone12ProMax.cpuCores        = 6;
iphone12ProMax.gpu             = "Apple GPU";
iphone12ProMax.gpuCores        = 4;
iphone12ProMax.metalFeatureSet = "Metal 3";

iphone12ProMax.ram             = "6 GB";
iphone12ProMax.ramType         = "LPDDR4X";
iphone12ProMax.storageBase     = "128 GB";
iphone12ProMax.storageOptions  = "128 / 256 / 512 GB";

iphone12ProMax.screen          = "6.7\"";
iphone12ProMax.display         = "OLED Super Retina XDR";
iphone12ProMax.resolution      = "2778 × 1284";
iphone12ProMax.refreshRate     = "60 Hz";
iphone12ProMax.displayOut      = "Lightning Digital AV (adapter)";
iphone12ProMax.displayVariants = "Largest display in the series; standard 60 Hz (no ProMotion)";

iphone12ProMax.has5G           = true;
iphone12ProMax.hasLTE          = true;
iphone12ProMax.cellular        = "5G NR / LTE";
iphone12ProMax.modem           = "Qualcomm Snapdragon X55";
iphone12ProMax.wifi            = "Wi-Fi 6 (802.11ax)";
iphone12ProMax.bluetooth       = "Bluetooth 5.0";
iphone12ProMax.hasNFC          = true;
iphone12ProMax.hasAirDrop      = true;
iphone12ProMax.hasAirPlay      = true;
iphone12ProMax.gps             = "Dual-frequency GPS (L1 + L5)";
iphone12ProMax.hasCompass      = true;
iphone12ProMax.hasGyro         = true;
iphone12ProMax.hasAccel        = true;
iphone12ProMax.hasBarometer    = true;

iphone12ProMax.simSlots        = "Nano-SIM + eSIM";
iphone12ProMax.hasESim         = true;
iphone12ProMax.port            = "Lightning";
iphone12ProMax.usbStandard     = "USB 2.0";

iphone12ProMax.speakers        = "Stereo speakers";
iphone12ProMax.microphones    = "Multiple microphones";
iphone12ProMax.hasDolby        = true;
iphone12ProMax.hasJack         = false;

iphone12ProMax.cameraMain      = "12 MP Wide (Sensor-shift OIS)";
iphone12ProMax.cameraUltraWide = "12 MP Ultra-Wide";
iphone12ProMax.cameraTele      = "12 MP Telephoto (2.5×)";
iphone12ProMax.cameraFront     = "12 MP TrueDepth";
iphone12ProMax.cameraVideo     = "4K@60fps HDR + Dolby Vision";
iphone12ProMax.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto) + LiDAR";

iphone12ProMax.hasFaceID       = true;
iphone12ProMax.hasTouchID      = false;
iphone12ProMax.biometrics      = "Face ID";

iphone12ProMax.hasFastCharge      = true;
iphone12ProMax.hasWirelessCharge = true;
iphone12ProMax.charging          = "Lightning / MagSafe";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone12ProMax.batteryMah          = 3687;   // model-based reference
iphone12ProMax.batteryVoltage      = 3.83f;  // Apple nominal (generation-specific)
iphone12ProMax.batteryWh           =
        (iphone12ProMax.batteryMah * iphone12ProMax.batteryVoltage) / 1000f;
iphone12ProMax.batteryChemistry    = "Lithium-Ion (pouch)";
iphone12ProMax.batteryDesignCycles = 500;
iphone12ProMax.batteryCharging     =
        "Fast wired charging, MagSafe 15W, Qi wireless";
iphone12ProMax.batteryNotes        =
        "Largest battery in the iPhone 12 lineup; improved endurance over 12 / 12 Pro";

iphone12ProMax.thermalNote =
        "Thermal sensors not accessible via iOS; larger chassis aids passive heat dissipation";
iphone12ProMax.notes =
        "Largest iPhone 12 with enhanced camera system and LiDAR scanner.";

MAP.put("iphone 12 pro max", iphone12ProMax);

// =========================================================
// 📱 iPHONE 11 SERIES — FULL SPECS
// =========================================================

// ---------------------------------------------------------
// 📱 iPhone 11
// ---------------------------------------------------------
AppleDeviceSpec iphone11 = new AppleDeviceSpec("iphone", "iPhone 11");
iphone11.year            = "2019";
iphone11.identifier      = "iPhone12,1";
iphone11.os              = "iOS";
iphone11.soc             = "A13 Bionic";
iphone11.chipset         = "A13 Bionic";
iphone11.arch            = "ARMv8.4-A";
iphone11.processNode     = "7 nm+";
iphone11.cpu             = "Apple CPU";
iphone11.cpuCores        = 6;
iphone11.gpu             = "Apple GPU";
iphone11.gpuCores        = 4;
iphone11.metalFeatureSet = "Metal 3";

iphone11.ram             = "4 GB";
iphone11.ramType         = "LPDDR4X";
iphone11.storageBase     = "64 GB";
iphone11.storageOptions  = "64 / 128 / 256 GB";

iphone11.screen          = "6.1\"";
iphone11.display         = "Liquid Retina IPS LCD";
iphone11.resolution      = "1792 × 828";
iphone11.refreshRate     = "60 Hz";
iphone11.displayOut      = "Lightning Digital AV (adapter)";
iphone11.displayVariants = "LCD panel; standard 60 Hz (no OLED / ProMotion)";

iphone11.has5G           = false;
iphone11.hasLTE          = true;
iphone11.cellular        = "LTE";
iphone11.modem           = "Intel LTE modem";
iphone11.wifi            = "Wi-Fi 6 (802.11ax)";
iphone11.bluetooth       = "Bluetooth 5.0";
iphone11.hasNFC          = true;
iphone11.hasAirDrop      = true;
iphone11.hasAirPlay      = true;
iphone11.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone11.hasCompass      = true;
iphone11.hasGyro         = true;
iphone11.hasAccel        = true;
iphone11.hasBarometer    = true;

iphone11.simSlots        = "Nano-SIM + eSIM";
iphone11.hasESim         = true;
iphone11.port            = "Lightning";
iphone11.usbStandard     = "USB 2.0";

iphone11.speakers        = "Stereo speakers";
iphone11.microphones    = "Multiple microphones";
iphone11.hasDolby        = true;
iphone11.hasJack         = false;

iphone11.cameraMain      = "12 MP Wide (OIS)";
iphone11.cameraUltraWide = "12 MP Ultra-Wide";
iphone11.cameraTele      = null;
iphone11.cameraFront     = "12 MP TrueDepth";
iphone11.cameraVideo     = "4K@60fps HDR";
iphone11.cameraVariants  =
        "Dual-camera system (Wide + Ultra-Wide) with Night Mode";

iphone11.hasFaceID       = true;
iphone11.hasTouchID      = false;
iphone11.biometrics      = "Face ID";

iphone11.hasFastCharge      = true;
iphone11.hasWirelessCharge = true;
iphone11.charging          = "Lightning / Qi wireless";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone11.batteryMah          = 3110;   // model-based reference
iphone11.batteryVoltage      = 3.83f;  // Apple nominal
iphone11.batteryWh           =
        (iphone11.batteryMah * iphone11.batteryVoltage) / 1000f;
iphone11.batteryChemistry    = "Lithium-Ion (pouch)";
iphone11.batteryDesignCycles = 500;
iphone11.batteryCharging     =
        "Fast wired charging, Qi wireless";
iphone11.batteryNotes        =
        "Large capacity for LCD model; excellent endurance despite non-OLED panel";

iphone11.thermalNote =
        "Thermal sensors not accessible via iOS; aluminum frame aids passive dissipation";
iphone11.notes =
        "First base iPhone with dual camera and Night Mode; strong battery life.";

MAP.put("iphone 11", iphone11);

// ---------------------------------------------------------
// 📱 iPhone 11 Pro
// ---------------------------------------------------------
AppleDeviceSpec iphone11Pro = new AppleDeviceSpec("iphone", "iPhone 11 Pro");
iphone11Pro.year            = "2019";
iphone11Pro.identifier      = "iPhone12,3";
iphone11Pro.os              = "iOS";
iphone11Pro.soc             = "A13 Bionic";
iphone11Pro.chipset         = "A13 Bionic";
iphone11Pro.arch            = "ARMv8.4-A";
iphone11Pro.processNode     = "7 nm+";
iphone11Pro.cpu             = "Apple CPU";
iphone11Pro.cpuCores        = 6;
iphone11Pro.gpu             = "Apple GPU";
iphone11Pro.gpuCores        = 4;
iphone11Pro.metalFeatureSet = "Metal 3";

iphone11Pro.ram             = "4 GB";
iphone11Pro.ramType         = "LPDDR4X";
iphone11Pro.storageBase     = "64 GB";
iphone11Pro.storageOptions  = "64 / 256 / 512 GB";

iphone11Pro.screen          = "5.8\"";
iphone11Pro.display         = "OLED Super Retina XDR";
iphone11Pro.resolution      = "2436 × 1125";
iphone11Pro.refreshRate     = "60 Hz";
iphone11Pro.displayOut      = "Lightning Digital AV (adapter)";
iphone11Pro.displayVariants = "OLED panel; standard 60 Hz (no ProMotion)";

iphone11Pro.has5G           = false;
iphone11Pro.hasLTE          = true;
iphone11Pro.cellular        = "LTE";
iphone11Pro.modem           = "Intel LTE modem";
iphone11Pro.wifi            = "Wi-Fi 6 (802.11ax)";
iphone11Pro.bluetooth       = "Bluetooth 5.0";
iphone11Pro.hasNFC          = true;
iphone11Pro.hasAirDrop      = true;
iphone11Pro.hasAirPlay      = true;
iphone11Pro.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone11Pro.hasCompass      = true;
iphone11Pro.hasGyro         = true;
iphone11Pro.hasAccel        = true;
iphone11Pro.hasBarometer    = true;

iphone11Pro.simSlots        = "Nano-SIM + eSIM";
iphone11Pro.hasESim         = true;
iphone11Pro.port            = "Lightning";
iphone11Pro.usbStandard     = "USB 2.0";

iphone11Pro.speakers        = "Stereo speakers";
iphone11Pro.microphones    = "Multiple microphones";
iphone11Pro.hasDolby        = true;
iphone11Pro.hasJack         = false;

iphone11Pro.cameraMain      = "12 MP Wide (OIS)";
iphone11Pro.cameraUltraWide = "12 MP Ultra-Wide";
iphone11Pro.cameraTele      = "12 MP Telephoto (2×)";
iphone11Pro.cameraFront     = "12 MP TrueDepth";
iphone11Pro.cameraVideo     = "4K@60fps HDR";
iphone11Pro.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto)";

iphone11Pro.hasFaceID       = true;
iphone11Pro.hasTouchID      = false;
iphone11Pro.biometrics      = "Face ID";

iphone11Pro.hasFastCharge      = true;
iphone11Pro.hasWirelessCharge = true;
iphone11Pro.charging          = "Lightning / Qi wireless";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone11Pro.batteryMah          = 3046;   // model-based reference
iphone11Pro.batteryVoltage      = 3.83f;  // Apple nominal
iphone11Pro.batteryWh           =
        (iphone11Pro.batteryMah * iphone11Pro.batteryVoltage) / 1000f;
iphone11Pro.batteryChemistry    = "Lithium-Ion (pouch)";
iphone11Pro.batteryDesignCycles = 500;
iphone11Pro.batteryCharging     =
        "Fast wired charging, Qi wireless";
iphone11Pro.batteryNotes        =
        "Good endurance for compact Pro model; OLED efficiency offsets size";

iphone11Pro.thermalNote =
        "Thermal sensors not accessible via iOS; stainless steel frame aids heat spreading";
iphone11Pro.notes =
        "First triple-camera iPhone; compact Pro form factor with OLED display.";

MAP.put("iphone 11 pro", iphone11Pro);
        
// ---------------------------------------------------------
// 📱 iPhone 11 Pro Max
// ---------------------------------------------------------
AppleDeviceSpec iphone11ProMax = new AppleDeviceSpec("iphone", "iPhone 11 Pro Max");
iphone11ProMax.year            = "2019";
iphone11ProMax.identifier      = "iPhone12,5";
iphone11ProMax.os              = "iOS";
iphone11ProMax.soc             = "A13 Bionic";
iphone11ProMax.chipset         = "A13 Bionic";
iphone11ProMax.arch            = "ARMv8.4-A";
iphone11ProMax.processNode     = "7 nm+";
iphone11ProMax.cpu             = "Apple CPU";
iphone11ProMax.cpuCores        = 6;
iphone11ProMax.gpu             = "Apple GPU";
iphone11ProMax.gpuCores        = 4;
iphone11ProMax.metalFeatureSet = "Metal 3";

iphone11ProMax.ram             = "4 GB";
iphone11ProMax.ramType         = "LPDDR4X";
iphone11ProMax.storageBase     = "64 GB";
iphone11ProMax.storageOptions  = "64 / 256 / 512 GB";

iphone11ProMax.screen          = "6.5\"";
iphone11ProMax.display         = "OLED Super Retina XDR";
iphone11ProMax.resolution      = "2688 × 1242";
iphone11ProMax.refreshRate     = "60 Hz";
iphone11ProMax.displayOut      = "Lightning Digital AV (adapter)";
iphone11ProMax.displayVariants =
        "OLED panel; standard 60 Hz (no ProMotion)";

iphone11ProMax.has5G           = false;
iphone11ProMax.hasLTE          = true;
iphone11ProMax.cellular        = "LTE";
iphone11ProMax.modem           = "Intel LTE modem";
iphone11ProMax.wifi            = "Wi-Fi 6 (802.11ax)";
iphone11ProMax.bluetooth       = "Bluetooth 5.0";
iphone11ProMax.hasNFC          = true;
iphone11ProMax.hasAirDrop      = true;
iphone11ProMax.hasAirPlay      = true;
iphone11ProMax.gps             = "GPS, GLONASS, Galileo, QZSS";
iphone11ProMax.hasCompass      = true;
iphone11ProMax.hasGyro         = true;
iphone11ProMax.hasAccel        = true;
iphone11ProMax.hasBarometer    = true;

iphone11ProMax.simSlots        = "Nano-SIM + eSIM";
iphone11ProMax.hasESim         = true;
iphone11ProMax.port            = "Lightning";
iphone11ProMax.usbStandard     = "USB 2.0";

iphone11ProMax.speakers        = "Stereo speakers";
iphone11ProMax.microphones    = "Multiple microphones";
iphone11ProMax.hasDolby        = true;
iphone11ProMax.hasJack         = false;

iphone11ProMax.cameraMain      = "12 MP Wide (OIS)";
iphone11ProMax.cameraUltraWide = "12 MP Ultra-Wide";
iphone11ProMax.cameraTele      = "12 MP Telephoto (2×)";
iphone11ProMax.cameraFront     = "12 MP TrueDepth";
iphone11ProMax.cameraVideo     = "4K@60fps HDR";
iphone11ProMax.cameraVariants  =
        "Triple-camera Pro system (Wide + Ultra-Wide + Telephoto)";

iphone11ProMax.hasFaceID       = true;
iphone11ProMax.hasTouchID      = false;
iphone11ProMax.biometrics      = "Face ID";

iphone11ProMax.hasFastCharge      = true;
iphone11ProMax.hasWirelessCharge = true;
iphone11ProMax.charging          = "Lightning / Qi wireless";


// =====================================================
// 🔋 BATTERY — SERVICE / REFERENCE DATA
// =====================================================
iphone11ProMax.batteryMah          = 3969;   // model-based reference
iphone11ProMax.batteryVoltage      = 3.83f;  // Apple nominal
iphone11ProMax.batteryWh           =
        (iphone11ProMax.batteryMah * iphone11ProMax.batteryVoltage) / 1000f;
iphone11ProMax.batteryChemistry    = "Lithium-Ion (pouch)";
iphone11ProMax.batteryDesignCycles = 500;
iphone11ProMax.batteryCharging     =
        "Fast wired charging, Qi wireless";
iphone11ProMax.batteryNotes        =
        "Excellent endurance; one of the best battery performers in iPhone history";

iphone11ProMax.thermalNote =
        "Thermal sensors not accessible via iOS; larger chassis improves passive cooling";
iphone11ProMax.notes =
        "Largest iPhone 11; best battery life of the series and flagship endurance.";

MAP.put("iphone 11 pro max", iphone11ProMax);  
}

public static AppleDeviceSpec get(String modelName) {

    if (modelName == null)
        return AppleDeviceSpec.unknown();

    String m = modelName.trim().toLowerCase();

    // =====================================================
    // SERIES RESOLUTION — CRITICAL
    // =====================================================

    if (m.contains("iphone 15")) {
        if (m.contains("pro max")) return MAP.get("iphone 15 pro max");
        if (m.contains("pro"))     return MAP.get("iphone 15 pro");
        if (m.contains("plus"))    return MAP.get("iphone 15 plus");
        return MAP.get("iphone 15");
    }

    if (m.contains("iphone 14")) {
        if (m.contains("pro max")) return MAP.get("iphone 14 pro max");
        if (m.contains("pro"))     return MAP.get("iphone 14 pro");
        if (m.contains("plus"))    return MAP.get("iphone 14 plus");
        return MAP.get("iphone 14");
    }

    if (m.contains("iphone 13")) {
        if (m.contains("pro max")) return MAP.get("iphone 13 pro max");
        if (m.contains("pro"))     return MAP.get("iphone 13 pro");
        if (m.contains("mini"))    return MAP.get("iphone 13 mini");
        return MAP.get("iphone 13");
    }

    if (m.contains("iphone 12")) {
        if (m.contains("pro max")) return MAP.get("iphone 12 pro max");
        if (m.contains("pro"))     return MAP.get("iphone 12 pro");
        if (m.contains("mini"))    return MAP.get("iphone 12 mini");
        return MAP.get("iphone 12");
    }

    if (m.contains("iphone 11")) {
        if (m.contains("pro max")) return MAP.get("iphone 11 pro max");
        if (m.contains("pro"))     return MAP.get("iphone 11 pro");
        return MAP.get("iphone 11");
    }

    // =====================================================
    // FALLBACK (exact key)
    // =====================================================
    AppleDeviceSpec d = MAP.get(m);
    return d != null ? d : AppleDeviceSpec.unknown();
}
}
