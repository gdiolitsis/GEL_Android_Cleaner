// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceSpec.java — LOCKED KNOWLEDGE BASE (FINAL)
// SERIES-AWARE / FUTURE-PROOF
// ============================================================

package com.gel.cleaner.iphone;

public class AppleDeviceSpec {

    // =========================================================
    // BASIC IDENTITY
    // =========================================================
    public String type;            // iphone / ipad
    public String model;           // e.g. "iPhone 15 Series"
    public String year;
    public String identifier;
    public String modelNumber;

    // =========================================================
    // SERIES / VARIANTS (CRITICAL)
    // =========================================================
    /**
     * Human-readable breakdown:
     * - Base
     * - Pro
     * - Pro Max
     * - Plus
     * - Mini
     * etc.
     */
    public String seriesVariants;   // textual list of variants

    // =========================================================
    // OS / PLATFORM
    // =========================================================
    public String os;
    public String charging;

    // =========================================================
    // SOC / CPU / GPU
    // =========================================================
    public String soc;             // A15 / A16 / A17
    public String chipset;         // alias
    public String arch;            // ARMv8 / ARMv9
    public String processNode;     // 5 nm / 3 nm
    public String cpu;
    public int    cpuCores;
    public String gpu;
    public int    gpuCores;
    public String metalFeatureSet;

    // Series-aware performance notes
    public String performanceVariants; // Pro vs base differences

    // =========================================================
    // MEMORY / STORAGE
    // =========================================================
    public String ram;
    public String ramType;
    public String storageBase;
    public String storageOptions;
    public String storageVariants;     // Pro models etc.

    // =========================================================
    // DISPLAY
    // =========================================================
    public String screen;          // generic
    public String display;
    public String resolution;
    public String refreshRate;
    public String displayOut;

    // Series-aware display differences
    public String displayVariants; // 60Hz vs 120Hz, sizes

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

    // Series-aware camera matrix
    public String cameraVariants;   // Pro vs base lenses / sensors

    // =========================================================
    // BIOMETRICS / FEATURES
    // =========================================================
    public boolean hasFaceID;
    public boolean hasTouchID;
    public String  biometrics;

    // =========================================================
    // POWER
    // =========================================================
    public boolean hasFastCharge;
    public boolean hasWirelessCharge;
    public String  batteryVariants;   // capacity differences

    // =========================================================
    // THERMAL / NOTES
    // =========================================================
    public String thermalNote;
    public String notes;

    // =========================================================
    // CONSTRUCTORS (DO NOT BREAK COMPATIBILITY)
    // =========================================================
    public AppleDeviceSpec() {}

    public AppleDeviceSpec(String type, String model) {
        this.type  = type;
        this.model = model;
    }

    // Legacy constructor — KEEP
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
    // UNKNOWN FALLBACK (MANDATORY, UI-SAFE)
    // =========================================================
    public static AppleDeviceSpec unknown() {
        AppleDeviceSpec d = new AppleDeviceSpec("unknown", "Unknown");

        d.os                 = "Unknown";
        d.soc                = "Unknown";
        d.cpu                = "Unknown";
        d.gpu                = "Unknown";
        d.ram                = "Unknown";
        d.storageOptions     = "Unknown";
        d.display            = "Unknown";
        d.modem              = "Unknown";
        d.wifi               = "Unknown";
        d.bluetooth          = "Unknown";
        d.speakers           = "Unknown";
        d.microphones        = "Unknown";
        d.port               = "Unknown";
        d.cameraMain         = "Unknown";
        d.cameraFront        = "Unknown";
        d.seriesVariants     = "Unknown";
        d.displayVariants    = "Unknown";
        d.cameraVariants     = "Unknown";

        return d;
    }
}
