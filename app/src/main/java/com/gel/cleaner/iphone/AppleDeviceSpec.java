// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceSpec.java — SCHEMA LOCK (FINAL, NO MORE FIELDS EVER)

package com.gel.cleaner.iphone;

public class AppleDeviceSpec {

    // =========================================================
    // BASIC IDENTITY
    // =========================================================
    public String type;            // iphone / ipad
    public String model;           // iPhone 13
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
    public String chipset;         // alias of soc
    public String arch;            // ARMv8 / ARMv9
    public String processNode;     // 5 nm
    public String cpu;             // generic cpu string
    public int cpuCores;
    public String gpu;             // Apple GPU
    public int gpuCores;
    public String metalFeatureSet;

    // =========================================================
    // MEMORY / STORAGE
    // =========================================================
    public String ram;             // 4 GB
    public String ramType;         // LPDDR4X
    public String storageBase;     // 128 GB
    public String storageOptions;  // 128 / 256 / 512
    public String storageType;

    // =========================================================
    // DISPLAY
    // =========================================================
    public String screen;          // OLED / LCD
    public String display;         // Super Retina XDR
    public String resolution;
    public String refreshRate;
    public String displayOut;

    // =========================================================
    // NETWORK / WIRELESS
    // =========================================================
    public boolean has5G;
    public boolean hasLTE;
    public String cellular;
    public String modem;
    public String wifi;
    public String bluetooth;
    public boolean hasNFC;
    public boolean hasAirDrop;
    public boolean hasAirPlay;
    public String gps;
    public boolean hasCompass;
    public boolean hasGyro;
    public boolean hasAccel;
    public boolean hasBarometer;

    // =========================================================
    // SIM / PORTS
    // =========================================================
    public String simSlots;
    public boolean hasESim;
    public String port;
    public String usbStandard;

    // =========================================================
    // AUDIO
    // =========================================================
    public String speakers;        // STRING ONLY
    public String microphones;     // STRING ONLY
    public boolean hasDolby;
    public boolean hasJack;

    // =========================================================
    // BIOMETRICS / FEATURES
    // =========================================================
    public boolean hasFaceID;
    public boolean hasTouchID;
    public String biometrics;

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
    // CONSTRUCTORS (COMPATIBILITY LOCK)
    // =========================================================
    public AppleDeviceSpec() {}

    public AppleDeviceSpec(String type, String model) {
        this.type = type;
        this.model = model;
    }

    // Used by AppleModelRegistry (legacy 11 params)
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
        this.type = type;
        this.model = model;
        this.soc = soc;
        this.chipset = soc;
        this.ram = ram;
        this.storageOptions = storageOptions;
        this.display = display;
        this.modem = modem;
        this.cellular = cellular;
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.charging = charging;
    }

    // =========================================================
    // UNKNOWN FALLBACK (MANDATORY)
    // =========================================================
    public static AppleDeviceSpec unknown() {
        AppleDeviceSpec d = new AppleDeviceSpec("unknown", "Unknown");
        d.os = "Unknown";
        d.soc = "Unknown";
        d.cpu = "Unknown";
        d.gpu = "Unknown";
        d.ram = "Unknown";
        d.storageOptions = "Unknown";
        d.display = "Unknown";
        d.modem = "Unknown";
        d.wifi = "Unknown";
        d.bluetooth = "Unknown";
        d.speakers = "Unknown";
        d.microphones = "Unknown";
        d.port = "Unknown";
        return d;
    }
}
