// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceSpec.java — FULL FIELDS (Internal + Peripherals)
// Backward-compatible constructor (11 args) + Full constructor
// ============================================================

package com.gel.cleaner.iphone.specs;

public class AppleDeviceSpec {

    // ============================================================
    // CORE IDENTITY
    // ============================================================
    public String model;         // e.g. "iPhone 15"
    public String type;          // "iphone" | "ipad"
    public String board;         // optional
    public int    releaseYear;   // optional

    // ============================================================
    // OS
    // ============================================================
    public String osBase;        // e.g. "iOS 17"
    public String osLatest;      // e.g. "iOS 18"
    public String os;            // legacy single-field (kept)

    // ============================================================
    // CHIP / CPU
    // ============================================================
    public String chip;          // e.g. "A16 Bionic"
    public String arch;          // e.g. "ARMv8.6-A"
    public String cpuCores;      // e.g. "6 (2P+4E)"
    public String processNode;   // e.g. "4nm"
    public String cpu;           // legacy single-field (kept)

    // ============================================================
    // GPU / GRAPHICS
    // ============================================================
    public String gpu;               // e.g. "Apple GPU"
    public String gpuCores;          // e.g. "5-core"
    public String metalFeatureSet;   // optional
    public String thermalNote;       // optional

    // ============================================================
    // MEMORY / STORAGE
    // ============================================================
    public String ram;           // e.g. "6 GB"
    public String ramType;       // e.g. "LPDDR5"
    public String storageBase;   // e.g. "128 GB"
    public String storageOptions;// e.g. "128/256/512"

    // ============================================================
    // CONNECTIVITY (INTERNAL VIEW)
    // ============================================================
    public String simSlots;      // e.g. "1 nano-SIM + eSIM"
    public String esim;          // e.g. "Yes"
    public String net5g;         // e.g. "Yes"
    public String wifi;          // e.g. "Wi-Fi 6E"
    public String bt;            // e.g. "Bluetooth 5.3"
    public String gps;           // e.g. "GPS, GLONASS, Galileo"

    // ============================================================
    // CAMERA (PERIPHERALS)
    // ============================================================
    public String cameraMain;
    public String cameraUltraWide;
    public String cameraTele;
    public String cameraFront;
    public String cameraVideo;

    // legacy single-field (kept)
    public String camera;

    // ============================================================
    // MODEM / CELLULAR (PERIPHERALS)
    // ============================================================
    public String  modem;
    public boolean has5G;
    public boolean hasLTE;
    public boolean hasESim;

    // ============================================================
    // WIRELESS FEATURES (PERIPHERALS)
    // ============================================================
    public boolean hasAirDrop;
    public boolean hasNFC;

    // legacy single-field (kept)
    public String bluetooth; // legacy
    public String charging;  // legacy

    // ============================================================
    // SENSORS (PERIPHERALS)
    // ============================================================
    public boolean hasCompass;
    public boolean hasGyro;
    public boolean hasAccel;
    public boolean hasBarometer;

    // ============================================================
    // AUDIO (PERIPHERALS)
    // ============================================================
    public String  speakers;
    public boolean hasDolby;
    public String  microphones;
    public boolean hasJack;

    // ============================================================
    // PORTS / POWER (PERIPHERALS)
    // ============================================================
    public String port;            // e.g. "USB-C"
    public String usbStandard;     // e.g. "USB 2.0"
    public boolean hasFastCharge;
    public boolean hasWirelessCharge;

    // ============================================================
    // BIOMETRICS (PERIPHERALS)
    // ============================================================
    public boolean hasFaceID;
    public boolean hasTouchID;

    // legacy single-field (kept)
    public String biometrics;

    // ============================================================
    // DISPLAY I/O (PERIPHERALS)
    // ============================================================
    public String  displayOut;
    public boolean hasAirPlay;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    // ------------------------------------------------------------
    // FULL CONSTRUCTOR (use this for "perfect from now")
    // ------------------------------------------------------------
    public AppleDeviceSpec(
            String model,
            String type,
            String board,
            int releaseYear,

            String osBase,
            String osLatest,

            String chip,
            String arch,
            String cpuCores,
            String processNode,

            String gpu,
            String gpuCores,
            String metalFeatureSet,
            String thermalNote,

            String ram,
            String ramType,
            String storageBase,
            String storageOptions,

            String simSlots,
            String esim,
            String net5g,
            String wifi,
            String bt,
            String gps,

            String cameraMain,
            String cameraUltraWide,
            String cameraTele,
            String cameraFront,
            String cameraVideo,

            String modem,
            boolean has5G,
            boolean hasLTE,
            boolean hasESim,

            boolean hasAirDrop,
            boolean hasNFC,

            boolean hasCompass,
            boolean hasGyro,
            boolean hasAccel,
            boolean hasBarometer,

            String speakers,
            boolean hasDolby,
            String microphones,
            boolean hasJack,

            String port,
            String usbStandard,
            boolean hasFastCharge,
            boolean hasWirelessCharge,

            boolean hasFaceID,
            boolean hasTouchID,

            String displayOut,
            boolean hasAirPlay
    ) {

        this.model = nz(model);
        this.type  = nz(type);
        this.board = nz(board);
        this.releaseYear = releaseYear;

        this.osBase   = nz(osBase);
        this.osLatest = nz(osLatest);
        this.os       = nz(osLatest); // legacy mirror

        this.chip        = nz(chip);
        this.arch        = nz(arch);
        this.cpuCores    = nz(cpuCores);
        this.processNode = nz(processNode);
        this.cpu         = nz(cpuCores); // legacy mirror

        this.gpu            = nz(gpu);
        this.gpuCores        = nz(gpuCores);
        this.metalFeatureSet = nz(metalFeatureSet);
        this.thermalNote     = nz(thermalNote);

        this.ram      = nz(ram);
        this.ramType  = nz(ramType);
        this.storageBase    = nz(storageBase);
        this.storageOptions = nz(storageOptions);

        this.simSlots = nz(simSlots);
        this.esim     = nz(esim);
        this.net5g    = nz(net5g);
        this.wifi     = nz(wifi);
        this.bt       = nz(bt);
        this.gps      = nz(gps);

        this.cameraMain      = nz(cameraMain);
        this.cameraUltraWide = nz(cameraUltraWide);
        this.cameraTele      = nz(cameraTele);
        this.cameraFront     = nz(cameraFront);
        this.cameraVideo     = nz(cameraVideo);

        // legacy mirror
        this.camera = pickFirst(this.cameraMain, this.cameraUltraWide, this.cameraTele, this.cameraFront);

        this.modem   = nz(modem);
        this.has5G   = has5G;
        this.hasLTE  = hasLTE;
        this.hasESim = hasESim;

        this.hasAirDrop = hasAirDrop;
        this.hasNFC     = hasNFC;

        // legacy mirrors
        this.bluetooth = nz(this.bt);
        this.charging  = ""; // legacy, not used by your new UI

        this.hasCompass   = hasCompass;
        this.hasGyro      = hasGyro;
        this.hasAccel     = hasAccel;
        this.hasBarometer = hasBarometer;

        this.speakers     = nz(speakers);
        this.hasDolby     = hasDolby;
        this.microphones  = nz(microphones);
        this.hasJack      = hasJack;

        this.port            = nz(port);
        this.usbStandard      = nz(usbStandard);
        this.hasFastCharge    = hasFastCharge;
        this.hasWirelessCharge= hasWirelessCharge;

        this.hasFaceID = hasFaceID;
        this.hasTouchID = hasTouchID;

        // legacy mirror
        this.biometrics = hasFaceID ? "Face ID" : (hasTouchID ? "Touch ID" : "None");

        this.displayOut = nz(displayOut);
        this.hasAirPlay = hasAirPlay;
    }

    // ------------------------------------------------------------
    // LEGACY CONSTRUCTOR (11 args) — keeps your registry compiling
    // soc, cpu, ram, screen, camera, bluetooth, wifi, biometrics, port, charging, os
    // ------------------------------------------------------------
    public AppleDeviceSpec(
            String soc,
            String cpu,
            String ram,
            String screen,
            String camera,
            String bluetooth,
            String wifi,
            String biometrics,
            String port,
            String charging,
            String os
    ) {
        // map legacy → new fields (best effort)
        this.model = "Unknown";
        this.type  = "unknown";
        this.board = "";
        this.releaseYear = 0;

        this.osBase   = nz(os);
        this.osLatest = nz(os);
        this.os       = nz(os);

        this.chip = nz(soc);
        this.arch = "Unknown";
        this.cpuCores = nz(cpu);
        this.processNode = "Unknown";
        this.cpu = nz(cpu);

        this.gpu = "Apple GPU";
        this.gpuCores = "Unknown";
        this.metalFeatureSet = "Unknown";
        this.thermalNote = "";

        this.ram = nz(ram);
        this.ramType = "Unknown";
        this.storageBase = "Unknown";
        this.storageOptions = "Unknown";

        this.simSlots = "Unknown";
        this.esim = "Unknown";
        this.net5g = "Unknown";
        this.wifi = nz(wifi);
        this.bt = nz(bluetooth);
        this.gps = "Unknown";

        this.camera = nz(camera);
        this.cameraMain = nz(camera);
        this.cameraUltraWide = "Unknown";
        this.cameraTele = "Unknown";
        this.cameraFront = "Unknown";
        this.cameraVideo = "Unknown";

        this.modem = "Unknown";
        this.has5G = false;
        this.hasLTE = true;   // safe guess
        this.hasESim = false;

        this.hasAirDrop = true; // safe guess for modern Apple
        this.hasNFC = false;

        this.bluetooth = nz(bluetooth);
        this.charging  = nz(charging);

        this.hasCompass = false;
        this.hasGyro = false;
        this.hasAccel = false;
        this.hasBarometer = false;

        this.speakers = "Unknown";
        this.hasDolby = false;
        this.microphones = "Unknown";
        this.hasJack = false;

        this.port = nz(port);
        this.usbStandard = "Unknown";
        this.hasFastCharge = true;       // safe guess
        this.hasWirelessCharge = true;   // safe guess

        this.hasFaceID = "Face ID".equalsIgnoreCase(nz(biometrics));
        this.hasTouchID = "Touch ID".equalsIgnoreCase(nz(biometrics));
        this.biometrics = nz(biometrics);

        this.displayOut = "Unknown";
        this.hasAirPlay = true; // safe guess
    }

    // ============================================================
    // UNKNOWN HELPERS (both signatures to avoid future errors)
    // ============================================================
    public static AppleDeviceSpec unknown() {
        return new AppleDeviceSpec(
                "Unknown", "unknown", "", 0,
                "Unknown", "Unknown",
                "Unknown", "Unknown", "Unknown", "Unknown",
                "Unknown", "Unknown", "Unknown", "",
                "Unknown", "Unknown", "Unknown", "Unknown",
                "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown",
                "Unknown", "Unknown", "Unknown", "Unknown", "Unknown",
                "Unknown", false, false, false,
                false, false,
                false, false, false, false,
                "Unknown", false, "Unknown", false,
                "Unknown", "Unknown", false, false,
                false, false,
                "Unknown", false
        );
    }

    public static AppleDeviceSpec unknown(String model) {
        AppleDeviceSpec x = unknown();
        x.model = nz(model);
        return x;
    }

    // ============================================================
    // INTERNAL SMALL UTILS
    // ============================================================
    private static String nz(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String pickFirst(String... arr) {
        if (arr == null) return "";
        for (String s : arr) {
            if (s != null && !s.trim().isEmpty() && !"Unknown".equalsIgnoreCase(s.trim()))
                return s.trim();
        }
        return "";
    }
}
