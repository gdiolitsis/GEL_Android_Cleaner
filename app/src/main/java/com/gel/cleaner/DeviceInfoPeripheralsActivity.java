package com.gel.cleaner;

// PERIPHERALS REPORT v5.0 — Professional Edition
// Camera / Biometrics / Sensors / Connectivity / Location / Other / BT / NFC / Root
// + Battery Health / UWB / Vibrator Amplitude / Haptics Class / GNSS Constellations
// + USB OTG Speed Modes / Microphone Count / Audio HAL Level
// Ολόκληρο κελί έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    private boolean isRooted = false;

    // για το "άνοιγε μόνο ένα-ένα"
    private TextView[] allContents;
    private TextView[] allIcons;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_peripherals);

        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) {
            title.setText(getString(R.string.phone_info_peripherals));
        }

        // ============================
        // REFERENCES (16 SECTIONS)
        // ============================
        TextView txtCameraContent        = findViewById(R.id.txtCameraContent);
        TextView txtBiometricsContent    = findViewById(R.id.txtBiometricsContent);
        TextView txtSensorsContent       = findViewById(R.id.txtSensorsContent);
        TextView txtConnectivityContent  = findViewById(R.id.txtConnectivityContent);
        TextView txtLocationContent      = findViewById(R.id.txtLocationContent);
        TextView txtOtherPeripherals     = findViewById(R.id.txtOtherPeripheralsContent);
        TextView txtBluetoothContent     = findViewById(R.id.txtBluetoothContent);    
        TextView txtNfcContent           = findViewById(R.id.txtNfcContent);          
        TextView txtRootContent          = findViewById(R.id.txtRootContent);

        TextView txtBatteryContent       = findViewById(R.id.txtBatteryContent);
        TextView txtUwbContent           = findViewById(R.id.txtUwbContent);
        TextView txtHapticsContent       = findViewById(R.id.txtHapticsContent);
        TextView txtGnssContent          = findViewById(R.id.txtGnssContent);
        TextView txtUsbContent           = findViewById(R.id.txtUsbContent);
        TextView txtMicsContent          = findViewById(R.id.txtMicsContent);
        TextView txtAudioHalContent      = findViewById(R.id.txtAudioHalContent);

        // ICONS
        TextView iconCamera        = findViewById(R.id.iconCameraToggle);
        TextView iconBiometrics    = findViewById(R.id.iconBiometricsToggle);
        TextView iconSensors       = findViewById(R.id.iconSensorsToggle);
        TextView iconConnectivity  = findViewById(R.id.iconConnectivityToggle);
        TextView iconLocation      = findViewById(R.id.iconLocationToggle);
        TextView iconOther         = findViewById(R.id.iconOtherPeripheralsToggle);
        TextView iconBluetooth     = findViewById(R.id.iconBluetoothToggle);
        TextView iconNfc           = findViewById(R.id.iconNfcToggle);
        TextView iconRoot          = findViewById(R.id.iconRootToggle);

        TextView iconBattery       = findViewById(R.id.iconBatteryToggle);
        TextView iconUwb           = findViewById(R.id.iconUwbToggle);
        TextView iconHaptics       = findViewById(R.id.iconHapticsToggle);
        TextView iconGnss          = findViewById(R.id.iconGnssToggle);
        TextView iconUsb           = findViewById(R.id.iconUsbToggle);
        TextView iconMics          = findViewById(R.id.iconMicsToggle);
        TextView iconAudioHal      = findViewById(R.id.iconAudioHalToggle);

        allContents = new TextView[]{
                txtCameraContent,
                txtBiometricsContent,
                txtSensorsContent,
                txtConnectivityContent,
                txtLocationContent,
                txtOtherPeripherals,
                txtBluetoothContent,
                txtNfcContent,
                txtRootContent,
                txtBatteryContent,
                txtUwbContent,
                txtHapticsContent,
                txtGnssContent,
                txtUsbContent,
                txtMicsContent,
                txtAudioHalContent
        };

        allIcons = new TextView[]{
                iconCamera,
                iconBiometrics,
                iconSensors,
                iconConnectivity,
                iconLocation,
                iconOther,
                iconBluetooth,
                iconNfc,
                iconRoot,
                iconBattery,
                iconUwb,
                iconHaptics,
                iconGnss,
                iconUsb,
                iconMics,
                iconAudioHal
        };

        // ROOT detection
        isRooted = isDeviceRooted();
        PackageManager pm = getPackageManager();

        // ===========================
        // CAMERA
        // ===========================
        StringBuilder cam = new StringBuilder();
        cam.append("── CAMERA ──\n");
        boolean anyCam   = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        boolean backCam  = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        boolean frontCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        boolean extCam   = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL);
        boolean flash    = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        cam.append("Any camera: ").append(anyCam ? "YES" : "NO").append("\n");
        cam.append("Back camera: ").append(backCam ? "YES" : "NO").append("\n");
        cam.append("Front camera: ").append(frontCam ? "YES" : "NO").append("\n");
        cam.append("External camera: ").append(extCam ? "YES" : "NO").append("\n");
        cam.append("Flash: ").append(flash ? "YES" : "NO").append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                CameraManager cmgr = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (cmgr != null) {
                    String[] ids = cmgr.getCameraIdList();
                    Integer level = null;
                    for (String id : ids) {
                        CameraCharacteristics c = cmgr.getCameraCharacteristics(id);
                        Integer facing = c.get(CameraCharacteristics.LENS_FACING);
                        if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                            level = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                            break;
                        }
                    }
                    if (level != null) {
                        cam.append("Camera2 HW level: ").append(level).append("\n");
                    }
                }
            } catch (Throwable ignored) {}
        }

        txtCameraContent.setText(cam.toString());

        // ===========================
        // BIOMETRICS
        // ===========================
        StringBuilder bio = new StringBuilder();
        bio.append("── BIOMETRICS ──\n");
        boolean fp   = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
        boolean face = pm.hasSystemFeature("android.hardware.biometrics.face");
        boolean iris = pm.hasSystemFeature("android.hardware.biometrics.iris");

        bio.append("Fingerprint sensor: ").append(fp ? "YES" : "NO").append("\n");
        bio.append("Face unlock HW: ").append(face ? "YES" : "NO").append("\n");
        bio.append("Iris scanner: ").append(iris ? "YES" : "NO").append("\n");

        txtBiometricsContent.setText(bio.toString());

        // ===========================
        // SENSORS
        // ===========================
        StringBuilder sens = new StringBuilder();
        sens.append("── SENSORS ──\n");

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            sens.append("Total sensors: ")
                    .append(sm.getSensorList(Sensor.TYPE_ALL).size()).append("\n");
        }

        txtSensorsContent.setText(sens.toString());

        // ===========================
        // CONNECTIVITY
        // ===========================
        StringBuilder conn = new StringBuilder();
        conn.append("── CONNECTIVITY ──\n");

        txtConnectivityContent.setText(conn.toString());

        // ===========================
        // LOCATION
        // ===========================
        StringBuilder loc = new StringBuilder();
        loc.append("── LOCATION ──\n");

        loc.append("\nGNSS constellations:\n");
        loc.append(readGnssConstellations());

        txtLocationContent.setText(loc.toString());

        // ===========================
        // OTHER PERIPHERALS
        // ===========================
        StringBuilder other = new StringBuilder();
        other.append("── OTHER PERIPHERALS ──\n");

        txtOtherPeripherals.setText(other.toString());

        // ===========================
        // BLUETOOTH
        // ===========================
        txtBluetoothContent.setText("── BLUETOOTH ──\n");

        // ===========================
        // NFC
        // ===========================
        txtNfcContent.setText("── NFC ──\n");

        // ===========================
        // ROOT
        // ===========================
        txtRootContent.setText("── ROOT ──\n");

        // ===========================
        // BATTERY
        // ===========================
        txtBatteryContent.setText("── BATTERY HEALTH ──\n" + readBatteryHealth());

        // ===========================
        // UWB
        // ===========================
        txtUwbContent.setText("── UWB ──\n" + detectUwbSupport(pm));

        // ===========================
        // HAPTICS
        // ===========================
        txtHapticsContent.setText("── HAPTICS ──\n\nAmplitude: "
                + detectVibratorAmplitude()
                + "\nClass: " + detectHapticsClass());

        // ===========================
        // GNSS
        // ===========================
        txtGnssContent.setText("── GNSS CONSTELLATIONS ──\n" + readGnssConstellations());

        // ===========================
        // USB SPEED
        // ===========================
        txtUsbContent.setText("── USB SPEED ──\n" + detectUsbSpeedModes());

        // ===========================
        // MICROPHONES
        // ===========================
        txtMicsContent.setText("── MICROPHONES ──\n" + describeMicrophones());

        // ===========================
        // AUDIO HAL
        // ===========================
        txtAudioHalContent.setText("── AUDIO HAL ──\n" + detectAudioHalLevel());

        // ===========================
        // EXPANDERS
        // ===========================
        setupSection(findViewById(R.id.headerCamera), txtCameraContent, iconCamera);
        setupSection(findViewById(R.id.headerBiometrics), txtBiometricsContent, iconBiometrics);
        setupSection(findViewById(R.id.headerSensors), txtSensorsContent, iconSensors);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);
        setupSection(findViewById(R.id.headerLocation), txtLocationContent, iconLocation);
        setupSection(findViewById(R.id.headerOtherPeripherals), txtOtherPeripherals, iconOther);
        setupSection(findViewById(R.id.headerBluetooth), txtBluetoothContent, iconBluetooth);
        setupSection(findViewById(R.id.headerNfc), txtNfcContent, iconNfc);
        setupSection(findViewById(R.id.headerRoot), txtRootContent, iconRoot);
        setupSection(findViewById(R.id.headerBattery), txtBatteryContent, iconBattery);
        setupSection(findViewById(R.id.headerUwb), txtUwbContent, iconUwb);
        setupSection(findViewById(R.id.headerHaptics), txtHapticsContent, iconHaptics);
        setupSection(findViewById(R.id.headerGnss), txtGnssContent, iconGnss);
        setupSection(findViewById(R.id.headerUsb), txtUsbContent, iconUsb);
        setupSection(findViewById(R.id.headerMics), txtMicsContent, iconMics);
        setupSection(findViewById(R.id.headerAudioHal), txtAudioHalContent, iconAudioHal);
    }

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {
        if (allContents == null || allIcons == null) return;

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == null || ic == null) continue;
            if (c == toOpen) continue;
            c.setVisibility(View.GONE);
            ic.setText("＋");
        }

        boolean visible = (toOpen.getVisibility() == View.VISIBLE);
        toOpen.setVisibility(visible ? View.GONE : View.VISIBLE);
        iconToUpdate.setText(visible ? "＋" : "−");
    }

    private String safe(String v) {
        if (v == null) return "[n/a]";
        if ("02:00:00:00:00:00".equals(v)) return "[masked]";
        return v;
    }

    private boolean isEmptySafe(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String readSmallFile(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private String readBatteryHealth() {
        StringBuilder sb = new StringBuilder();

        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    float pct = (level * 100f / scale);
                    sb.append("Current level: ").append(String.format("%.0f", pct)).append(" %\n");
                }
            }

            String base = "/sys/class/power_supply/battery";
            String cycleStr = readSmallFile(new File(base, "cycle_count"));
            String fullStr  = readSmallFile(new File(base, "charge_full"));
            String designStr= readSmallFile(new File(base, "charge_full_design"));

            if (cycleStr != null)
                sb.append("Cycle count: ").append(cycleStr.trim()).append("\n");
            else
                sb.append("Cycle count: [not exposed]\n");

            if (fullStr != null && designStr != null) {
                try {
                    long full   = Long.parseLong(fullStr.trim());
                    long design = Long.parseLong(designStr.trim());
                    double fullMah   = (full   > 100000) ? (full   / 1000.0) : full;
                    double designMah = (design > 100000) ? (design / 1000.0) : design;

                    double healthPct = (designMah > 0) ? (fullMah * 100.0 / designMah) : -1.0;

                    sb.append(String.format("Estimated full capacity: %.0f mAh\n", fullMah));
                    sb.append(String.format("Design capacity: %.0f mAh\n", designMah));
                    if (healthPct > 0) {
                        sb.append(String.format("Health vs design: %.1f %%\n", healthPct));
                    }
                } catch (Exception ignored) {}
            } else {
                sb.append("Capacity info: [not exposed]\n");
            }
        } catch (Throwable t) {
            sb.append("Battery health error: ").append(t.getMessage()).append("\n");
        }

        return sb.toString();
    }

    private String detectUwbSupport(PackageManager pm) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                return pm.hasSystemFeature(PackageManager.FEATURE_UWB) ? "SUPPORTED" : "NOT supported";
            else
                return pm.hasSystemFeature("android.hardware.uwb") ? "SUPPORTED (legacy)" : "NOT supported";
        } catch (Throwable t) {
            return "Unknown";
        }
    }

    private String detectVibratorAmplitude() {
        try {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib == null) return "No vibrator service";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                return vib.hasAmplitudeControl() ? "Supported" : "Not supported";
            return "API < 26 (unknown)";
        } catch (Throwable t) {
            return "Error";
        }
    }

    private String detectHapticsClass() {
        String h = getProp("ro.product.haptics_level");
        if (isEmptySafe(h)) h = getProp("ro.vibrator.haptic.feedback");
        return isEmptySafe(h) ? "Unknown" : h;
    }

    private String readGnssConstellations() {
        StringBuilder sb = new StringBuilder();
        try {
            String[] paths = {
                    "/system/etc/gps.conf",
                    "/vendor/etc/gps.conf",
                    "/system/etc/gnss.conf",
                    "/vendor/etc/gnss.conf"
            };
            boolean found = false;
            for (String p : paths) {
                File f = new File(p);
                if (!f.exists()) continue;
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    String lower = line.toLowerCase();
                    if (lower.contains("glonass") || lower.contains("bds")
                            || lower.contains("galileo") || lower.contains("qzss")) {
                        sb.append(line).append("\n");
                        found = true;
                    }
                }
                br.close();
            }
            if (!found) sb.append("No constellations listed\n");
        } catch (Throwable ignored) {}
        return sb.toString();
    }

    private String detectUsbSpeedModes() {
        StringBuilder sb = new StringBuilder();
        String[] keys = {
                "sys.usb.speed",
                "sys.usb.controller",
                "vendor.usb.speed",
                "ro.usb_speed",
                "ro.vendor.usb.speed"
        };
        boolean any = false;
        for (String k : keys) {
            String v = getProp(k);
            if (!isEmptySafe(v)) {
                sb.append(k).append(" = ").append(v).append("\n");
                any = true;
            }
        }
        if (!any)
            sb.append("No USB speed info\n");
        return sb.toString();
    }

    private String describeMicrophones() {
        StringBuilder sb = new StringBuilder();
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am == null) return "AudioManager unavailable\n";

            AudioDeviceInfo[] inputs = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
            if (inputs == null || inputs.length == 0) return "No input devices\n";

            int builtIn = 0, bt = 0, usb = 0, other = 0;

            for (AudioDeviceInfo d : inputs) {
                switch (d.getType()) {
                    case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                    case AudioDeviceInfo.TYPE_TELEPHONY: builtIn++; break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP: bt++; break;
                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET: usb++; break;
                    default: other++; break;
                }
            }

            sb.append("Built-in mics: ").append(builtIn).append("\n");
            sb.append("Bluetooth: ").append(bt).append("\n");
            sb.append("USB: ").append(usb).append("\n");
            sb.append("Other: ").append(other).append("\n");

        } catch (Throwable t) {
            sb.append("Mic error\n");
        }
        return sb.toString();
    }

    private String detectAudioHalLevel() {
        String v = getProp("ro.audio.hal.version");
        if (isEmptySafe(v)) v = getProp("audio_hal.version");
        return isEmptySafe(v) ? "Unknown" : v;
    }

    private boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;
        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su"
        };
        for (String p : paths) if (new File(p).exists()) return true;
        String dbg = getProp("ro.debuggable");
        String sec = getProp("ro.secure");
        return "1".equals(dbg) || "0".equals(sec);
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getSelinux() {
        try {
            Process p = Runtime.getRuntime().exec("getenforce");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String checkSuPaths() {
        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su"
        };
        for (String p : paths) if (new File(p).exists()) return p;
        return "none";
    }
}
