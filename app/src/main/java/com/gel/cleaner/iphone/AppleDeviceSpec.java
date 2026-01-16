// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceSpec.java — STABLE KNOWLEDGE BASE (FIELDS + COMPAT CONSTRUCTORS + unknown())
// NOTE: From now on, Activities may request any field without "cannot find symbol".

package com.gel.cleaner.iphone;

public class AppleDeviceSpec {

    // =========================================================
    // IDENTITY
    // =========================================================
    public String type;          // "iphone" | "ipad"
    public String model;         // "iPhone 15" etc
    public String identifier;    // e.g. "iPhone15,4" (optional)
    public String modelNumber;   // e.g. "A2846" (optional)
    public String year;          // e.g. "2023" (optional)

    // =========================================================
    // PLATFORM / OS
    // =========================================================
    public String os;            // e.g. "iOS 17+" / "iPadOS 17+"
    public String charging;      // e.g. "USB-C / Lightning / MagSafe / Qi"

    // =========================================================
    // CPU / GPU / RAM / STORAGE
    // =========================================================
    public String chipset;       // e.g. "A16 Bionic"
    public String cpu;           // optional detailed text
    public String gpu;           // optional detailed text
    public String neural;        // optional e.g. "16-core Neural Engine"
    public String ram;           // e.g. "6 GB"
    public String storageOptions;// e.g. "128/256/512 GB"

    // =========================================================
    // DISPLAY
    // =========================================================
    public String display;       // e.g. '6.1" OLED'
    public String resolution;    // e.g. "2556x1179"
    public String refreshRate;   // e.g. "60Hz/120Hz"
    public String displayOut;    // e.g. "AirPlay / USB-C Alt Mode / HDMI via adapter"

    // =========================================================
    // NETWORK / WIRELESS
    // =========================================================
    public String modem;         // e.g. "Qualcomm / Apple" (text)
    public String cellular;      // e.g. "5G / LTE"
    public String wifi;          // e.g. "Wi-Fi 6 / 6E"
    public String bluetooth;     // e.g. "5.3"
    public boolean hasNFC;
    public boolean hasAirDrop;
    public boolean hasAirPlay;
    public String usbStandard;   // e.g. "USB-C (USB 2)" / "Lightning (USB 2)"
    public String gps;           // e.g. "GPS/GNSS" (text)
    public String simSlots;      // e.g. "dual eSIM" / "nano-SIM + eSIM"
    public boolean hasESim;

    // =========================================================
    // SENSORS / HW
    // =========================================================
    public boolean hasCompass;
    public boolean hasGyro;
    public boolean hasAccel;
    public boolean hasBarometer;

    // =========================================================
    // AUDIO / PORTS
    // =========================================================
    public String speakers;      // keep as String (Activities logInfo expects String)
    public String microphones;   // keep as String (Activities logInfo expects String)
    public boolean hasDolby;
    public boolean hasJack;

    // =========================================================
    // CAMERAS (optional)
    // =========================================================
    public String cameraRear;
    public String cameraFront;

    // =========================================================
    // OTHER (optional)
    // =========================================================
    public String notes;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================
    public AppleDeviceSpec() {}

    public AppleDeviceSpec(String type, String model) {
        this.type = type;
        this.model = model;
    }

    // ---------------------------------------------------------
    // COMPAT CONSTRUCTOR (keeps AppleModelRegistry old calls working)
    // Signature required by build errors:
    // new AppleDeviceSpec(String,String,String,String,String,String,String,String,String,String,String)
    // ---------------------------------------------------------
    public AppleDeviceSpec(
            String type,
            String model,
            String chipset,
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
        this.chipset = chipset;
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
    // UNKNOWN FALLBACK (required by AppleModelRegistry + AppleSpecProvider)
    // =========================================================
    public static AppleDeviceSpec unknown() {
        AppleDeviceSpec s = new AppleDeviceSpec("unknown", "Unknown Model");
        s.os = "Unknown";
        s.charging = "Unknown";
        s.chipset = "Unknown";
        s.ram = "Unknown";
        s.storageOptions = "Unknown";
        s.display = "Unknown";
        s.modem = "Unknown";
        s.cellular = "Unknown";
        s.wifi = "Unknown";
        s.bluetooth = "Unknown";
        s.usbStandard = "Unknown";
        s.displayOut = "Unknown";
        s.gps = "Unknown";
        s.simSlots = "Unknown";
        s.speakers = "Unknown";
        s.microphones = "Unknown";
        return s;
    }

    // =========================================================
    // FLUENT SETTERS (OPTIONAL HELPERS)
    // =========================================================
    public AppleDeviceSpec setIdentifier(String v){ this.identifier = v; return this; }
    public AppleDeviceSpec setModelNumber(String v){ this.modelNumber = v; return this; }
    public AppleDeviceSpec setYear(String v){ this.year = v; return this; }

    public AppleDeviceSpec setOs(String v){ this.os = v; return this; }
    public AppleDeviceSpec setCharging(String v){ this.charging = v; return this; }

    public AppleDeviceSpec setChipset(String v){ this.chipset = v; return this; }
    public AppleDeviceSpec setCpu(String v){ this.cpu = v; return this; }
    public AppleDeviceSpec setGpu(String v){ this.gpu = v; return this; }
    public AppleDeviceSpec setNeural(String v){ this.neural = v; return this; }
    public AppleDeviceSpec setRam(String v){ this.ram = v; return this; }
    public AppleDeviceSpec setStorageOptions(String v){ this.storageOptions = v; return this; }

    public AppleDeviceSpec setDisplay(String v){ this.display = v; return this; }
    public AppleDeviceSpec setResolution(String v){ this.resolution = v; return this; }
    public AppleDeviceSpec setRefreshRate(String v){ this.refreshRate = v; return this; }
    public AppleDeviceSpec setDisplayOut(String v){ this.displayOut = v; return this; }

    public AppleDeviceSpec setModem(String v){ this.modem = v; return this; }
    public AppleDeviceSpec setCellular(String v){ this.cellular = v; return this; }
    public AppleDeviceSpec setWifi(String v){ this.wifi = v; return this; }
    public AppleDeviceSpec setBluetooth(String v){ this.bluetooth = v; return this; }
    public AppleDeviceSpec setUsbStandard(String v){ this.usbStandard = v; return this; }
    public AppleDeviceSpec setGps(String v){ this.gps = v; return this; }
    public AppleDeviceSpec setSimSlots(String v){ this.simSlots = v; return this; }

    public AppleDeviceSpec setHasNfc(boolean v){ this.hasNFC = v; return this; }
    public AppleDeviceSpec setHasAirDrop(boolean v){ this.hasAirDrop = v; return this; }
    public AppleDeviceSpec setHasAirPlay(boolean v){ this.hasAirPlay = v; return this; }
    public AppleDeviceSpec setHasEsim(boolean v){ this.hasESim = v; return this; }

    public AppleDeviceSpec setHasCompass(boolean v){ this.hasCompass = v; return this; }
    public AppleDeviceSpec setHasGyro(boolean v){ this.hasGyro = v; return this; }
    public AppleDeviceSpec setHasAccel(boolean v){ this.hasAccel = v; return this; }
    public AppleDeviceSpec setHasBarometer(boolean v){ this.hasBarometer = v; return this; }

    public AppleDeviceSpec setSpeakers(String v){ this.speakers = v; return this; }
    public AppleDeviceSpec setMicrophones(String v){ this.microphones = v; return this; }
    public AppleDeviceSpec setHasDolby(boolean v){ this.hasDolby = v; return this; }
    public AppleDeviceSpec setHasJack(boolean v){ this.hasJack = v; return this; }

    public AppleDeviceSpec setCameraRear(String v){ this.cameraRear = v; return this; }
    public AppleDeviceSpec setCameraFront(String v){ this.cameraFront = v; return this; }

    public AppleDeviceSpec setNotes(String v){ this.notes = v; return this; }
}

/*
REMINDER (per George rule):
Always deliver the whole fixed file ready for copy-paste, without extra questions.
*/
```0
