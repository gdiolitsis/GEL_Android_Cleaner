// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleSpecs.java — iPhone 15 Series (LOCKED DATASET)
// ============================================================

package com.gel.cleaner.iphone;

import java.util.HashMap;
import java.util.Map;

public class AppleSpecs {

    public static final Map<String, AppleDeviceSpec> DEVICES = new HashMap<>();

    static {

        // =========================================================
        // iPhone 15
        // =========================================================
        AppleDeviceSpec iphone15 = new AppleDeviceSpec("iphone", "iPhone 15");
        iphone15.year = "2023";
        iphone15.identifier = "iPhone15,4";
        iphone15.modelNumber = "A3090 / A2846";
        iphone15.os = "iOS";
        iphone15.soc = "A16 Bionic";
        iphone15.chipset = "A16 Bionic";
        iphone15.arch = "ARMv8.6-A";
        iphone15.processNode = "4 nm";
        iphone15.cpu = "Apple CPU";
        iphone15.cpuCores = 6;
        iphone15.gpu = "Apple GPU";
        iphone15.gpuCores = 5;
        iphone15.metalFeatureSet = "Metal 3";
        iphone15.ram = "6 GB";
        iphone15.ramType = "Unified LPDDR5";
        iphone15.storageBase = "128 GB";
        iphone15.storageOptions = "128 / 256 / 512 GB";
        iphone15.screen = "6.1\"";
        iphone15.display = "OLED Super Retina XDR";
        iphone15.resolution = "2556 × 1179";
        iphone15.refreshRate = "60 Hz";
        iphone15.displayOut = "DisplayPort over USB-C (limited)";
        iphone15.has5G = true;
        iphone15.hasLTE = true;
        iphone15.cellular = "5G NR / LTE";
        iphone15.modem = "Qualcomm Snapdragon X70";
        iphone15.wifi = "Wi-Fi 6";
        iphone15.bluetooth = "Bluetooth 5.3";
        iphone15.hasNFC = true;
        iphone15.hasAirDrop = true;
        iphone15.hasAirPlay = true;
        iphone15.gps = "GPS, GLONASS, Galileo, QZSS, BeiDou";
        iphone15.hasCompass = true;
        iphone15.hasGyro = true;
        iphone15.hasAccel = true;
        iphone15.hasBarometer = true;
        iphone15.simSlots = "Dual eSIM";
        iphone15.hasESim = true;
        iphone15.port = "USB-C";
        iphone15.usbStandard = "USB 2";
        iphone15.speakers = "Stereo";
        iphone15.microphones = "Dual";
        iphone15.hasDolby = true;
        iphone15.hasJack = false;
        iphone15.cameraMain = "48 MP Wide";
        iphone15.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15.cameraTele = null;
        iphone15.cameraFront = "12 MP TrueDepth";
        iphone15.cameraVideo = "4K@60fps HDR";
        iphone15.hasFaceID = true;
        iphone15.hasTouchID = false;
        iphone15.biometrics = "Face ID";
        iphone15.hasFastCharge = true;
        iphone15.hasWirelessCharge = true;
        iphone15.thermalNote =
                "No public thermal sensor access. Thermal management controlled by iOS.";
        iphone15.notes =
                "Storage partitions, filesystem layout and live performance metrics are restricted by Apple.";

        DEVICES.put("iPhone 15", iphone15);

        // =========================================================
        // iPhone 15 Plus
        // =========================================================
        AppleDeviceSpec iphone15Plus = new AppleDeviceSpec("iphone", "iPhone 15 Plus");
        iphone15Plus.year = "2023";
        iphone15Plus.identifier = "iPhone15,5";
        iphone15Plus.modelNumber = "A3094 / A2847";
        iphone15Plus.os = "iOS";
        iphone15Plus.soc = "A16 Bionic";
        iphone15Plus.chipset = "A16 Bionic";
        iphone15Plus.arch = "ARMv8.6-A";
        iphone15Plus.processNode = "4 nm";
        iphone15Plus.cpu = "Apple CPU";
        iphone15Plus.cpuCores = 6;
        iphone15Plus.gpu = "Apple GPU";
        iphone15Plus.gpuCores = 5;
        iphone15Plus.metalFeatureSet = "Metal 3";
        iphone15Plus.ram = "6 GB";
        iphone15Plus.ramType = "Unified LPDDR5";
        iphone15Plus.storageBase = "128 GB";
        iphone15Plus.storageOptions = "128 / 256 / 512 GB";
        iphone15Plus.screen = "6.7\"";
        iphone15Plus.display = "OLED Super Retina XDR";
        iphone15Plus.resolution = "2796 × 1290";
        iphone15Plus.refreshRate = "60 Hz";
        iphone15Plus.displayOut = "DisplayPort over USB-C (limited)";
        iphone15Plus.has5G = true;
        iphone15Plus.hasLTE = true;
        iphone15Plus.cellular = "5G NR / LTE";
        iphone15Plus.modem = "Qualcomm Snapdragon X70";
        iphone15Plus.wifi = "Wi-Fi 6";
        iphone15Plus.bluetooth = "Bluetooth 5.3";
        iphone15Plus.hasNFC = true;
        iphone15Plus.hasAirDrop = true;
        iphone15Plus.hasAirPlay = true;
        iphone15Plus.gps = iphone15.gps;
        iphone15Plus.hasCompass = true;
        iphone15Plus.hasGyro = true;
        iphone15Plus.hasAccel = true;
        iphone15Plus.hasBarometer = true;
        iphone15Plus.simSlots = "Dual eSIM";
        iphone15Plus.hasESim = true;
        iphone15Plus.port = "USB-C";
        iphone15Plus.usbStandard = "USB 2";
        iphone15Plus.speakers = "Stereo";
        iphone15Plus.microphones = "Dual";
        iphone15Plus.hasDolby = true;
        iphone15Plus.hasJack = false;
        iphone15Plus.cameraMain = "48 MP Wide";
        iphone15Plus.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15Plus.cameraTele = null;
        iphone15Plus.cameraFront = "12 MP TrueDepth";
        iphone15Plus.cameraVideo = "4K@60fps HDR";
        iphone15Plus.hasFaceID = true;
        iphone15Plus.hasTouchID = false;
        iphone15Plus.biometrics = "Face ID";
        iphone15Plus.hasFastCharge = true;
        iphone15Plus.hasWirelessCharge = true;
        iphone15Plus.thermalNote = iphone15.thermalNote;
        iphone15Plus.notes = iphone15.notes;

        DEVICES.put("iPhone 15 Plus", iphone15Plus);

        // =========================================================
        // iPhone 15 Pro
        // =========================================================
        AppleDeviceSpec iphone15Pro = new AppleDeviceSpec("iphone", "iPhone 15 Pro");
        iphone15Pro.year = "2023";
        iphone15Pro.identifier = "iPhone16,1";
        iphone15Pro.modelNumber = "A3101 / A2848";
        iphone15Pro.os = "iOS";
        iphone15Pro.soc = "A17 Pro";
        iphone15Pro.chipset = "A17 Pro";
        iphone15Pro.arch = "ARMv9";
        iphone15Pro.processNode = "3 nm";
        iphone15Pro.cpu = "Apple CPU";
        iphone15Pro.cpuCores = 6;
        iphone15Pro.gpu = "Apple GPU";
        iphone15Pro.gpuCores = 6;
        iphone15Pro.metalFeatureSet = "Metal 3 (Hardware Ray Tracing)";
        iphone15Pro.ram = "8 GB";
        iphone15Pro.ramType = "Unified LPDDR5";
        iphone15Pro.storageBase = "128 GB";
        iphone15Pro.storageOptions = "128 / 256 / 512 / 1 TB";
        iphone15Pro.screen = "6.1\"";
        iphone15Pro.display = "OLED Super Retina XDR (ProMotion)";
        iphone15Pro.resolution = "2556 × 1179";
        iphone15Pro.refreshRate = "120 Hz";
        iphone15Pro.displayOut = "DisplayPort over USB-C";
        iphone15Pro.has5G = true;
        iphone15Pro.hasLTE = true;
        iphone15Pro.cellular = "5G NR / LTE";
        iphone15Pro.modem = "Qualcomm Snapdragon X70";
        iphone15Pro.wifi = "Wi-Fi 6E";
        iphone15Pro.bluetooth = "Bluetooth 5.3";
        iphone15Pro.hasNFC = true;
        iphone15Pro.hasAirDrop = true;
        iphone15Pro.hasAirPlay = true;
        iphone15Pro.gps = iphone15.gps;
        iphone15Pro.hasCompass = true;
        iphone15Pro.hasGyro = true;
        iphone15Pro.hasAccel = true;
        iphone15Pro.hasBarometer = true;
        iphone15Pro.simSlots = "Dual eSIM";
        iphone15Pro.hasESim = true;
        iphone15Pro.port = "USB-C";
        iphone15Pro.usbStandard = "USB 3";
        iphone15Pro.speakers = "Stereo";
        iphone15Pro.microphones = "Triple";
        iphone15Pro.hasDolby = true;
        iphone15Pro.hasJack = false;
        iphone15Pro.cameraMain = "48 MP Wide";
        iphone15Pro.cameraUltraWide = "12 MP Ultra-Wide";
        iphone15Pro.cameraTele = "12 MP Telephoto (3×)";
        iphone15Pro.cameraFront = "12 MP TrueDepth";
        iphone15Pro.cameraVideo = "4K@60fps HDR / ProRes";
        iphone15Pro.hasFaceID = true;
        iphone15Pro.hasTouchID = false;
        iphone15Pro.biometrics = "Face ID";
        iphone15Pro.hasFastCharge = true;
        iphone15Pro.hasWirelessCharge = true;
        iphone15Pro.thermalNote =
                "Improved thermal efficiency but no public sensor access.";
        iphone15Pro.notes =
                "Hardware ray tracing available only on Pro models.";

        DEVICES.put("iPhone 15 Pro", iphone15Pro);

        // =========================================================
        // iPhone 15 Pro Max
        // =========================================================
        AppleDeviceSpec iphone15ProMax = iphone15Pro;
        iphone15ProMax.model = "iPhone 15 Pro Max";
        iphone15ProMax.identifier = "iPhone16,2";
        iphone15ProMax.screen = "6.7\"";
        iphone15ProMax.resolution = "2796 × 1290";
        iphone15ProMax.cameraTele = "12 MP Telephoto (5× tetraprism)";
        iphone15ProMax.storageBase = "256 GB";

        DEVICES.put("iPhone 15 Pro Max", iphone15ProMax);
    }
}
