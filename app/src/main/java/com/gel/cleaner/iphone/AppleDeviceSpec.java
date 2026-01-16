// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceSpec.java — LOCKED KNOWLEDGE BASE (FINAL)
// ============================================================

package com.gel.cleaner.iphone;

public class AppleDeviceSpec {

    // =========================================================
    // BASIC IDENTITY
    // =========================================================
    public String type;            // iphone / ipad
    public String model;
    public String year;
    public String identifier;
    public String modelNumber;

    // =========================================================
    // OS / PLATFORM
    // =========================================================
    public String os;
    public String charging;

    // =========================================================
    // SOC / CPU / GPU
    // =========================================================
    public String soc;             // A15 Bionic
    public String chipset;         // alias
    public String arch;            // ARMv8
    public String processNode;     // 5 nm
    public String cpu;             // Apple CPU
    public int    cpuCores;
    public String gpu;             // Apple GPU
    public int    gpuCores;
    public String metalFeatureSet;

    // =========================================================
    // MEMORY / STORAGE
    // =========================================================
    public String ram;
    public String ramType;
    public String storageBase;
    public String storageOptions;

    // =========================================================
    // DISPLAY
    // =========================================================
    public String screen;
    public String display;
    public String resolution;
    public String refreshRate;
    public String displayOut;

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
    public String  speakers;       // STRING by design
    public String  microphones;    // STRING by design
    public boolean hasDolby;
    public boolean hasJack;

    // =========================================================
    // CAMERA
    // =========================================================
    public String cameraMain;        // e.g. 12 MP Wide
    public String cameraUltraWide;   // e.g. 12 MP Ultra-Wide
    public String cameraTele;        // e.g. Telephoto / null
    public String cameraFront;       // e.g. 12 MP TrueDepth
    public String cameraVideo;       // e.g. 4K@60fps HDR

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

    // =========================================================
    // THERMAL / NOTES
    // =========================================================
    public String thermalNote;
    public String notes;

    // =========================================================
    // CONSTRUCTORS (KEEP COMPATIBILITY)
    // =========================================================
    public AppleDeviceSpec() {}

    public AppleDeviceSpec(String type, String model) {
        this.type  = type;
        this.model = model;
    }

    // Legacy constructor — AppleModelRegistry compatibility
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

        return d;
    }
}
