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

    // =====================================================
    // PUBLIC ACCESS
    // =====================================================
    public static AppleDeviceSpec get(String modelName) {
        if (modelName == null) return AppleDeviceSpec.unknown();
        AppleDeviceSpec d = MAP.get(modelName.toLowerCase());
        return d != null ? d : AppleDeviceSpec.unknown();
    }

    private iPhoneSpecs() {}
}
