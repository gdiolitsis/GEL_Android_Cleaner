package com.gel.cleaner.iphone.specs;

public class AppleDeviceSpec {

    // =========================
    // INTERNALS
    // =========================
    public String soc;
    public String cpu;
    public String ram;
    public String screen;
    public String wifi;
    public String bluetooth;
    public String biometrics;
    public String port;
    public String charging;
    public String os;

    // =========================
    // PERIPHERALS — CAMERA
    // =========================
    public String cameraMain;
    public String cameraUltraWide;
    public String cameraTele;
    public String cameraFront;
    public String cameraVideo;

    // =========================
    // PERIPHERALS — MODEM / CELLULAR
    // =========================
    public String modem;
    public boolean has5G;
    public boolean hasLTE;
    public String simSlots;
    public boolean hasESim;

    // =========================
    // PERIPHERALS — CONNECTIVITY
    // =========================
    public boolean hasAirDrop;
    public boolean hasNFC;

    // =========================
    // PERIPHERALS — SENSORS
    // =========================
    public String gps;
    public boolean hasCompass;
    public boolean hasGyro;
    public boolean hasAccel;
    public boolean hasBarometer;

    // =========================
    // PERIPHERALS — AUDIO
    // =========================
    public String speakers;
    public boolean hasDolby;
    public String microphones;
    public boolean hasJack;

    // =========================
    // PERIPHERALS — PORTS
    // =========================
    public String usbStandard;
    public boolean hasFastCharge;
    public boolean hasWirelessCharge;

    // =========================
    // PERIPHERALS — BIOMETRICS
    // =========================
    public boolean hasFaceID;
    public boolean hasTouchID;

    // =========================
    // PERIPHERALS — DISPLAY I/O
    // =========================
    public String displayOut;
    public boolean hasAirPlay;

    // =========================
    // PERIPHERALS — STORAGE
    // =========================
    public String storageOptions;

    // =========================================================
    // CONSTRUCTOR (FULL)
    // =========================================================
    public AppleDeviceSpec(
            String soc, String cpu, String ram, String screen,
            String wifi, String bluetooth, String biometrics,
            String port, String charging, String os,

            String cameraMain, String cameraUltraWide,
            String cameraTele, String cameraFront, String cameraVideo,

            String modem, boolean has5G, boolean hasLTE,
            String simSlots, boolean hasESim,

            boolean hasAirDrop, boolean hasNFC,

            String gps, boolean hasCompass, boolean hasGyro,
            boolean hasAccel, boolean hasBarometer,

            String speakers, boolean hasDolby,
            String microphones, boolean hasJack,

            String usbStandard, boolean hasFastCharge,
            boolean hasWirelessCharge,

            boolean hasFaceID, boolean hasTouchID,

            String displayOut, boolean hasAirPlay,

            String storageOptions
    ) {

        // ---- internals ----
        this.soc = soc;
        this.cpu = cpu;
        this.ram = ram;
        this.screen = screen;
        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.biometrics = biometrics;
        this.port = port;
        this.charging = charging;
        this.os = os;

        // ---- camera ----
        this.cameraMain = cameraMain;
        this.cameraUltraWide = cameraUltraWide;
        this.cameraTele = cameraTele;
        this.cameraFront = cameraFront;
        this.cameraVideo = cameraVideo;

        // ---- modem ----
        this.modem = modem;
        this.has5G = has5G;
        this.hasLTE = hasLTE;
        this.simSlots = simSlots;
        this.hasESim = hasESim;

        // ---- connectivity ----
        this.hasAirDrop = hasAirDrop;
        this.hasNFC = hasNFC;

        // ---- sensors ----
        this.gps = gps;
        this.hasCompass = hasCompass;
        this.hasGyro = hasGyro;
        this.hasAccel = hasAccel;
        this.hasBarometer = hasBarometer;

        // ---- audio ----
        this.speakers = speakers;
        this.hasDolby = hasDolby;
        this.microphones = microphones;
        this.hasJack = hasJack;

        // ---- ports ----
        this.usbStandard = usbStandard;
        this.hasFastCharge = hasFastCharge;
        this.hasWirelessCharge = hasWirelessCharge;

        // ---- biometrics ----
        this.hasFaceID = hasFaceID;
        this.hasTouchID = hasTouchID;

        // ---- display ----
        this.displayOut = displayOut;
        this.hasAirPlay = hasAirPlay;

        // ---- storage ----
        this.storageOptions = storageOptions;
    }

    // =========================================================
    // UNKNOWN
    // =========================================================
    public static AppleDeviceSpec unknown() {
        return new AppleDeviceSpec(
                "Unknown","Unknown","Unknown","Unknown",
                "Unknown","Unknown","Unknown",
                "Unknown","Unknown","Unknown",

                "Unknown","Unknown","Unknown","Unknown","Unknown",

                "Unknown",false,false,
                "Unknown",false,

                false,false,

                "Unknown",false,false,
                false,false,

                "Unknown",false,
                "Unknown",false,

                "Unknown",false,
                false,

                "Unknown",false,

                "Unknown"
        );
    }
}
