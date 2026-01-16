// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceSpec.java — FINAL STABLE EDITION
// Scope: SoC/CPU/RAM/Screen, iOS support, Camera basics,
//        Connectivity, Ports, Biometrics
// ============================================================

package com.gel.cleaner.iphone;

public class AppleDeviceSpec {

    // =========================
    // CORE
    // =========================
    public String soc;
    public String cpu;
    public String ram;
    public String screen;
    public String osSupport;

    // =========================
    // CAMERA (basics)
    // =========================
    public String cameraMain;
    public String cameraUltraWide;
    public String cameraTele;
    public String cameraFront;
    public String cameraVideo;

    // =========================
    // CONNECTIVITY
    // =========================
    public String wifi;
    public String bluetooth;
    public boolean has5G;
    public boolean hasLTE;

    // =========================
    // PORTS
    // =========================
    public String port;
    public boolean hasFastCharge;
    public boolean hasWirelessCharge;

// =========================================================
// PERIPHERALS & FEATURES (REQUIRED BY ACTIVITIES)
// =========================================================
public boolean hasAccel;
public boolean hasBarometer;
public boolean hasDolby;
public boolean hasJack;
public boolean hasAirPlay;

public int speakers;
public int microphones;

public String usbStandard;
public String displayOut;
public String storageOptions;
public String os;
public String charging;

    // =========================
    // BIOMETRICS
    // =========================
    public String biometrics; // "Face ID" | "Touch ID" | "None"
    public boolean hasFaceID;
    public boolean hasTouchID;

    // =========================================================
    // CONSTRUCTOR (FULL — STABLE)
    // =========================================================
    public AppleDeviceSpec(
            String soc,
            String cpu,
            String ram,
            String screen,
            String osSupport,

            String cameraMain,
            String cameraUltraWide,
            String cameraTele,
            String cameraFront,
            String cameraVideo,

            String wifi,
            String bluetooth,
            boolean has5G,
            boolean hasLTE,

            String port,
            boolean hasFastCharge,
            boolean hasWirelessCharge,

            String biometrics,
            boolean hasFaceID,
            boolean hasTouchID
    ) {
        this.soc = soc;
        this.cpu = cpu;
        this.ram = ram;
        this.screen = screen;
        this.osSupport = osSupport;

        this.cameraMain = cameraMain;
        this.cameraUltraWide = cameraUltraWide;
        this.cameraTele = cameraTele;
        this.cameraFront = cameraFront;
        this.cameraVideo = cameraVideo;

        this.wifi = wifi;
        this.bluetooth = bluetooth;
        this.has5G = has5G;
        this.hasLTE = hasLTE;

        this.port = port;
        this.hasFastCharge = hasFastCharge;
        this.hasWirelessCharge = hasWirelessCharge;

        this.biometrics = biometrics;
        this.hasFaceID = hasFaceID;
        this.hasTouchID = hasTouchID;
    }

    // =========================================================
    // CONSTRUCTOR (LIGHT — 11 params, backward compatible)
    // =========================================================
    public AppleDeviceSpec(
            String soc,
            String cpu,
            String ram,
            String screen,
            String camera,     // main camera
            String bluetooth,
            String wifi,
            String biometrics,
            String port,
            String charging,   // "Fast charge" / "MagSafe" / etc
            String osSupport
    ) {
        this(
                // ---- core ----
                soc,
                cpu,
                ram,
                screen,
                osSupport,

                // ---- camera ----
                camera,        // main
                "Unknown",     // ultra-wide
                "Unknown",     // tele
                "Unknown",     // front
                "Unknown",     // video

                // ---- connectivity ----
                wifi,
                bluetooth,
                false,         // 5G default
                true,          // LTE default

                // ---- ports ----
                port,
                charging != null && charging.toLowerCase().contains("fast"),
                charging != null && charging.toLowerCase().contains("wireless"),

                // ---- biometrics ----
                biometrics,
                biometrics != null && biometrics.equalsIgnoreCase("Face ID"),
                biometrics != null && biometrics.equalsIgnoreCase("Touch ID")
        );
    }

    // =========================================================
    // UNKNOWN (SAFE FALLBACK)
    // =========================================================
    public static AppleDeviceSpec unknown() {
        return new AppleDeviceSpec(
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",

                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",

                "Unknown",
                "Unknown",
                false,
                false,

                "Unknown",
                false,
                false,

                "None",
                false,
                false
        );
    }
}
