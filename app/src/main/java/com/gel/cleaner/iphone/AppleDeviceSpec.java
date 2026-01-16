// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceSpec.java — FINAL HARDCODED APPLE DEVICE SPEC
// ASCII ONLY — PRODUCTION SAFE

package com.gel.cleaner.iphone;

public class AppleDeviceSpec {

    // ================================
    // BASIC IDENTITY
    // ================================
    public String model;
    public String deviceType; // iphone | ipad
    public int releaseYear;

    // ================================
    // OS
    // ================================
    public String os;        // iOS / iPadOS
    public String osBase;
    public String osLatest;

    // ================================
    // CHIP / CPU / GPU
    // ================================
    public String chip;
    public String arch;
    public String processNode;
    public int cpuCores;

    public String gpu;
    public int gpuCores;
    public String metalFeatureSet;

    // ================================
    // MEMORY / STORAGE
    // ================================
    public String ram;
    public String ramType;

    public String storageBase;
    public String storageOptions;

    // ================================
    // CONNECTIVITY
    // ================================
    public String modem;
    public String simSlots;
    public boolean hasESim;

    public boolean has5G;
    public boolean hasLTE;

    public String wifi;
    public String bluetooth;
    public boolean hasAirDrop;
    public boolean hasNFC;

    // ================================
    // SENSORS
    // ================================
    public String gps;
    public boolean hasCompass;
    public boolean hasGyro;
    public boolean hasAccel;
    public boolean hasBarometer;

    // ================================
    // AUDIO
    // ================================
    public int speakers;
    public int microphones;
    public boolean hasDolby;
    public boolean hasJack;

    // ================================
    // PORTS / I-O
    // ================================
    public String port;
    public String usbStandard;
    public boolean hasFastCharge;
    public boolean hasWirelessCharge;

    public String displayOut;
    public boolean hasAirPlay;

    // ================================
    // BIOMETRICS
    // ================================
    public boolean hasFaceID;
    public boolean hasTouchID;

    // ================================
    // POWER / THERMAL
    // ================================
    public String charging;
    public String thermalNote;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public AppleDeviceSpec() {}
}
