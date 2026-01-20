// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceSpec.java — LOCKED KNOWLEDGE BASE (FINAL)
// SERIES-AWARE / FUTURE-PROOF
// ============================================================

package com.gel.cleaner.iphone;

import java.util.Locale;

public class AppleDeviceSpec {

    // =========================================================
    // BASIC IDENTITY
    // =========================================================
    public String type;            // iphone / ipad
    public String model;           // e.g. "iPhone 15 Pro Max"
    public String year;
    public String identifier;
    public String modelNumber;

    // =========================================================
    // SERIES / VARIANTS (CRITICAL — LOCKED FLAGS)
    // =========================================================
    public boolean isPro;
    public boolean isProMax;
    public boolean isPlus;
    public boolean isMini;

    // Human-readable info (UI only)
    public String seriesVariants;   // e.g. "Base / Pro / Pro Max"

    // =========================================================
    // OS / PLATFORM
    // =========================================================
    public String os;
    public String charging;

    // =========================================================
    // SOC / CPU / GPU
    // =========================================================
    public String soc;
    public String chipset;
    public String arch;
    public String processNode;
    public String cpu;
    public int    cpuCores;
    public String gpu;
    public int    gpuCores;
    public String metalFeatureSet;

    public String performanceVariants; // Pro vs base notes

    // =========================================================
    // MEMORY / STORAGE
    // =========================================================
    public String ram;
    public String ramType;
    public String storageBase;
    public String storageOptions;
    public String storageVariants;

    // =========================================================
    // DISPLAY
    // =========================================================
    public String screen;
    public String display;
    public String resolution;
    public String refreshRate;
    public String displayOut;
    public String displayVariants;

    // =========================================================
    // NETWORK / WIRELESS
    // =========================================================
    public boolean has5G;
    public boolean hasLTE;
    public String  cellular;
    public String  modem;
    public String  wifi;
    public String  bluetooth;
    public boolean hasNFC;
    public boolean hasAirDrop;
    public boolean hasAirPlay;
    public String  gps;
    public boolean hasCompass;
    public boolean hasGyro;
    public boolean hasAccel;
    public boolean hasBarometer;

    // =========================================================
    // SIM / PORTS
    // =========================================================
    public String  simSlots;
    public boolean hasESim;
    public String  port;
    public String  usbStandard;

    // =========================================================
    // AUDIO
    // =========================================================
    public String  speakers;
    public String  microphones;
    public boolean hasDolby;
    public boolean hasJack;

    // =========================================================
    // CAMERA
    // =========================================================
    public String cameraMain;
    public String cameraUltraWide;
    public String cameraTele;
    public String cameraFront;
    public String cameraVideo;
    public String cameraVariants;

    // =========================================================
    // BIOMETRICS / FEATURES
    // =========================================================
    public boolean hasFaceID;
    public boolean hasTouchID;
    public String  biometrics;

    // =========================================================
    // POWER (GENERIC FLAGS)
    // =========================================================
    public boolean hasFastCharge;
    public boolean hasWirelessCharge;
    public String  batteryVariants;

    // =========================================================
    // BATTERY — SERVICE / REFERENCE DATA (MODEL-BASED)
    // =========================================================
    // Design capacity (from teardowns / service manuals)
    public Integer batteryMah;           // e.g. 4422

    // Nominal voltage (Apple standard ≈ 3.82V)
    public Float   batteryVoltage;       // e.g. 3.82f

    // Cached energy (Wh) — optional
    public Float   batteryWh;             // (mAh * V) / 1000

    // Chemistry
    public String  batteryChemistry;      // Lithium-Ion (pouch)

    // Design lifecycle
    public Integer batteryDesignCycles;   // ~500 full cycles to ~80%

    // Charging capabilities (human-readable)
    public String  batteryCharging;       // Fast wired / MagSafe / Qi

    // Thermal / aging notes
    public String  batteryNotes;          // Pro vs Base differences

    // =========================================================
    // THERMAL / NOTES
    // =========================================================
    public String thermalNote;
    public String notes;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================
    public AppleDeviceSpec() {}

    public AppleDeviceSpec(String type, String model) {
        this.type  = type;
        this.model = model;
    }

    // Legacy constructor — KEEP (do not break old code)
    public AppleDeviceSpec(
            String type,
            String model,
            String soc,
            String ram,
            String storageOptions,
            String display,
            String modem,
            String cellular,
            String wifi,
            String bluetooth,
            String charging
    ) {
        this.type           = type;
        this.model          = model;
        this.soc            = soc;
        this.chipset        = soc;
        this.ram            = ram;
        this.storageOptions = storageOptions;
        this.display        = display;
        this.modem          = modem;
        this.cellular       = cellular;
        this.wifi           = wifi;
        this.bluetooth      = bluetooth;
        this.charging       = charging;
    }

    // =========================================================
    // SAFE HELPERS (UI / SERVICE USE)
    // =========================================================
    public boolean isAnyPro() {
        return isPro || isProMax;
    }

    public boolean hasBatterySpec() {
        return batteryMah != null && batteryVoltage != null;
    }

    public String getBatteryEnergyWh() {
        if (batteryMah == null || batteryVoltage == null) return null;
        float wh = (batteryMah * batteryVoltage) / 1000f;
        return String.format(Locale.US, "%.2f Wh", wh);
    }

    // =========================================================
    // UNKNOWN FALLBACK (MANDATORY)
    // =========================================================
    public static AppleDeviceSpec unknown() {
        AppleDeviceSpec d = new AppleDeviceSpec("unknown", "Unknown");

        d.os              = "Unknown";
        d.soc             = "Unknown";
        d.cpu             = "Unknown";
        d.gpu             = "Unknown";
        d.ram             = "Unknown";
        d.storageOptions  = "Unknown";
        d.display         = "Unknown";
        d.modem           = "Unknown";
        d.wifi            = "Unknown";
        d.bluetooth       = "Unknown";
        d.speakers        = "Unknown";
        d.microphones     = "Unknown";
        d.port            = "Unknown";
        d.cameraMain      = "Unknown";
        d.cameraFront     = "Unknown";
        d.seriesVariants  = "Unknown";
        d.displayVariants = "Unknown";
        d.cameraVariants  = "Unknown";
        d.batteryNotes    = "Unknown";

        return d;
    }
}
