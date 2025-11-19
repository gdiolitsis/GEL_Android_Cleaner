package com.gel.cleaner;

// PERIPHERALS REPORT v6.4 — FINAL CLEAN EDITION (GEL)
// 16 Sections Only — Fully Synced With XML Layout
// Camera / Biometrics / Sensors / Connectivity / Location / Bluetooth / NFC / Root
// Battery / UWB / Haptics / GNSS / USB / Microphones / Audio HAL / Other Peripherals
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. Δούλευε ΠΑΝΤΑ πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    private boolean isRooted = false;

    // lists for "only one open at a time"
    private ArrayList<TextView> allContents = new ArrayList<>();
    private ArrayList<TextView> allIcons = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_peripherals);

        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) title.setText(getString(R.string.phone_info_peripherals));

        // ============================================================
        // REFERENCES (16 SECTIONS)
        // ============================================================
        TextView txtCameraContent       = findViewById(R.id.txtCameraContent);
        TextView txtBiometricsContent   = findViewById(R.id.txtBiometricsContent);
        TextView txtSensorsContent      = findViewById(R.id.txtSensorsContent);
        TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent);
        TextView txtLocationContent     = findViewById(R.id.txtLocationContent);
        TextView txtBluetoothContent    = findViewById(R.id.txtBluetoothContent);
        TextView txtNfcContent          = findViewById(R.id.txtNfcContent);
        TextView txtRootContent         = findViewById(R.id.txtRootContent);

        TextView txtBatteryContent      = findViewById(R.id.txtBatteryContent);
        TextView txtUwbContent          = findViewById(R.id.txtUwbContent);
        TextView txtHapticsContent      = findViewById(R.id.txtHapticsContent);
        TextView txtGnssContent         = findViewById(R.id.txtGnssContent);
        TextView txtUsbContent          = findViewById(R.id.txtUsbContent);
        TextView txtMicsContent         = findViewById(R.id.txtMicsContent);
        TextView txtAudioHalContent     = findViewById(R.id.txtAudioHalContent);
        TextView txtOtherPeripherals    = findViewById(R.id.txtOtherPeripheralsContent);

        // ============================================================
        // ICON REFERENCES
        // ============================================================
        TextView iconCamera        = findViewById(R.id.iconCameraToggle);
        TextView iconBiometrics    = findViewById(R.id.iconBiometricsToggle);
        TextView iconSensors       = findViewById(R.id.iconSensorsToggle);
        TextView iconConnectivity  = findViewById(R.id.iconConnectivityToggle);
        TextView iconLocation      = findViewById(R.id.iconLocationToggle);
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
        TextView iconOther         = findViewById(R.id.iconOtherPeripheralsToggle);

        // ============================================================
        // BUILD expand/collapse arrays
        // ============================================================
        allContents.clear();
        allIcons.clear();

        allContents.add(txtCameraContent);
        allContents.add(txtBiometricsContent);
        allContents.add(txtSensorsContent);
        allContents.add(txtConnectivityContent);
        allContents.add(txtLocationContent);
        allContents.add(txtBluetoothContent);
        allContents.add(txtNfcContent);
        allContents.add(txtRootContent);

        allContents.add(txtBatteryContent);
        allContents.add(txtUwbContent);
        allContents.add(txtHapticsContent);
        allContents.add(txtGnssContent);
        allContents.add(txtUsbContent);
        allContents.add(txtMicsContent);
        allContents.add(txtAudioHalContent);
        allContents.add(txtOtherPeripherals);

        allIcons.add(iconCamera);
        allIcons.add(iconBiometrics);
        allIcons.add(iconSensors);
        allIcons.add(iconConnectivity);
        allIcons.add(iconLocation);
        allIcons.add(iconBluetooth);
        allIcons.add(iconNfc);
        allIcons.add(iconRoot);

        allIcons.add(iconBattery);
        allIcons.add(iconUwb);
        allIcons.add(iconHaptics);
        allIcons.add(iconGnss);
        allIcons.add(iconUsb);
        allIcons.add(iconMics);
        allIcons.add(iconAudioHal);
        allIcons.add(iconOther);

        // ============================================================
        // ROOT detection
        // ============================================================
        isRooted = isDeviceRooted();
        PackageManager pm = getPackageManager();

        // ============================================================
        // CAMERA
        // ============================================================
        StringBuilder cam = new StringBuilder();
        cam.append("── CAMERA ──\n");
        cam.append("Any camera: ").append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO").append("\n");
        cam.append("Back camera: ").append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ? "YES" : "NO").append("\n");
        cam.append("Front camera: ").append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? "YES" : "NO").append("\n");
        cam.append("External camera: ").append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL) ? "YES" : "NO").append("\n");
        cam.append("Flash: ").append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? "YES" : "NO").append("\n");

        // Camera2 level
        try {
            CameraManager cmgr = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (cmgr != null) {
                for (String id : cmgr.getCameraIdList()) {
                    CameraCharacteristics c = cmgr.getCameraCharacteristics(id);
                    Integer facing = c.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        Integer lvl = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                        if (lvl != null) {
                            cam.append("Camera2 HW level: ").append(lvl).append("\n");
                        }
                        break;
                    }
                }
            }
        } catch (Throwable ignored) {}

        txtCameraContent.setText(cam.toString());

        // ============================================================
        // BIOMETRICS
        // ============================================================
        StringBuilder bio = new StringBuilder();
        bio.append("── BIOMETRICS ──\n");
        bio.append("Fingerprint: ").append(pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ? "YES" : "NO").append("\n");
        bio.append("Face unlock HW: ").append(pm.hasSystemFeature("android.hardware.biometrics.face") ? "YES" : "NO").append("\n");
        bio.append("Iris scanner: ").append(pm.hasSystemFeature("android.hardware.biometrics.iris") ? "YES" : "NO").append("\n");
        txtBiometricsContent.setText(bio.toString());

        // ============================================================
        // SENSORS
        // ============================================================
        StringBuilder sens = new StringBuilder();
        sens.append("── SENSORS ──\n");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            sens.append("Total sensors: ").append(sm.getSensorList(Sensor.TYPE_ALL).size()).append("\n");
            sens.append("Accelerometer: ").append(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null ? "YES" : "NO").append("\n");
            sens.append("Gyroscope: ").append(sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null ? "YES" : "NO").append("\n");
            sens.append("Magnetometer: ").append(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null ? "YES" : "NO").append("\n");
            sens.append("Proximity: ").append(sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null ? "YES" : "NO").append("\n");
            sens.append("Light sensor: ").append(sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null ? "YES" : "NO").append("\n");
        }
        txtSensorsContent.setText(sens.toString());

        // ============================================================
        // CONNECTIVITY
        // ============================================================
        txtConnectivityContent.setText(readConnectivityInfo(pm));

        // ============================================================
        // LOCATION / GPS
        // ============================================================
        txtLocationContent.setText(readLocationInfo(pm));

        // ============================================================
        // BLUETOOTH
        // ============================================================
        txtBluetoothContent.setText(readBluetoothInfo(pm));

        // ============================================================
        // NFC
        // ============================================================
        txtNfcContent.setText(readNfcInfo(pm));

        // ============================================================
        // ROOT
        // ============================================================
        txtRootContent.setText(readRootInfo());

        // ============================================================
        // BATTERY
        // ============================================================
        txtBatteryContent.setText("── BATTERY HEALTH ──\n" + readBatteryHealth());

        // ============================================================
        // UWB
        // ============================================================
        txtUwbContent.setText("── UWB ──\n" + detectUwbSupport(pm));

        // ============================================================
        // HAPTICS
        // ============================================================
        txtHapticsContent.setText("── HAPTICS ──\n" +
                "Amplitude: " + detectVibratorAmplitude() + "\n" +
                "Class: " + detectHapticsClass());

        // ============================================================
        // GNSS
        // ============================================================
        txtGnssContent.setText("── GNSS ──\n" + readGnssConstellations());

        // ============================================================
        // USB
        // ============================================================
        txtUsbContent.setText("── USB SPEED ──\n" + detectUsbSpeedModes());

        // ============================================================
        // MICROPHONES
        // ============================================================
        txtMicsContent.setText("── MICROPHONES ──\n" + describeMicrophones());

        // ============================================================
        // AUDIO HAL
        // ============================================================
        txtAudioHalContent.setText("── AUDIO HAL ──\n" + detectAudioHalLevel());

        // ============================================================
        // OTHER PERIPHERALS
        // ============================================================
        txtOtherPeripherals.setText(readOtherPeripherals(pm));

        // ============================================================
        // SETUP EXPANDERS (16)
        // ============================================================
        setupSection(findViewById(R.id.headerCamera), txtCameraContent, iconCamera);
        setupSection(findViewById(R.id.headerBiometrics), txtBiometricsContent, iconBiometrics);
        setupSection(findViewById(R.id.headerSensors), txtSensorsContent, iconSensors);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);
        setupSection(findViewById(R.id.headerLocation), txtLocationContent, iconLocation);
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
        setupSection(findViewById(R.id.headerOtherPeripherals), txtOtherPeripherals, iconOther);
    }

    // ============================================================
    // EXPANDER LOGIC
    // ============================================================
    private void setupSection(View header, TextView content, TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {
        for (int i = 0; i < allContents.size(); i++) {
            TextView c = allContents.get(i);
            TextView ic = allIcons.get(i);
            if (c == toOpen) continue;
            c.setVisibility(View.GONE);
            ic.setText("＋");
        }
        boolean vis = toOpen.getVisibility() == View.VISIBLE;
        toOpen.setVisibility(vis ? View.GONE : View.VISIBLE);
        iconToUpdate.setText(vis ? "＋" : "−");
    }

    // ============================================================
    // CONNECTIVITY
    // ============================================================
    private String readConnectivityInfo(PackageManager pm) {
        StringBuilder sb = new StringBuilder();
        sb.append("── CONNECTIVITY ──\n");
        try {
            sb.append("Telephony: ").append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? "YES" : "NO").append("\n");
            sb.append("5G NR flag: ").append(pm.hasSystemFeature("android.hardware.telephony.nr") ? "YES" : "NO").append("\n");
            sb.append("IMS / VoLTE flag: ").append(pm.hasSystemFeature("android.hardware.telephony.ims") ? "YES" : "NO").append("\n");
            sb.append("WiFi: ").append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO").append("\n");

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    if (caps != null) {
                        sb.append("Active (WiFi): ").append(caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ? "YES" : "NO").append("\n");
                        sb.append("Active (Cellular): ").append(caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ? "YES" : "NO").append("\n");
                    }
                } else {
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    sb.append("Active type: ").append(ni != null ? ni.getTypeName() : "[none]").append("\n");
                }
            }
        } catch (Throwable t) {
            sb.append("Connectivity error\n");
        }
        return sb.toString();
    }

    // ============================================================
    // LOCATION
    // ============================================================
    private String readLocationInfo(PackageManager pm) {
        StringBuilder sb = new StringBuilder();
        sb.append("── LOCATION ──\n");
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            sb.append("GPS provider: ").append(lm != null && lm.getProvider(LocationManager.GPS_PROVIDER) != null ? "YES" : "NO").append("\n");
            sb.append("Network provider: ").append(lm != null && lm.getProvider(LocationManager.NETWORK_PROVIDER) != null ? "YES" : "NO").append("\n");
        } catch (Throwable t) {
            sb.append("Location error\n");
        }
        sb.append("HW GPS: ").append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO").append("\n");
        return sb.toString();
    }

    // ============================================================
    // BLUETOOTH
    // ============================================================
    private String readBluetoothInfo(PackageManager pm) {
        StringBuilder sb = new StringBuilder();
        sb.append("── BLUETOOTH ──\n");
        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter ba = bm != null ? bm.getAdapter() : BluetoothAdapter.getDefaultAdapter();
            if (ba == null) {
                sb.append("Adapter: [none]\n");
            } else {
                sb.append("Enabled: ").append(ba.isEnabled() ? "YES" : "NO").append("\n");
                sb.append("LE support: ").append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO").append("\n");
            }
        } catch (Throwable t) {
            sb.append("Bluetooth error\n");
        }
        return sb.toString();
    }

    // ============================================================
    // NFC
    // ============================================================
    private String readNfcInfo(PackageManager pm) {
        StringBuilder sb = new StringBuilder();
        sb.append("── NFC ──\n");
        try {
            NfcManager nfm = (NfcManager) getSystemService(Context.NFC_SERVICE);
            NfcAdapter na = nfm != null ? nfm.getDefaultAdapter() : null;
            sb.append("NFC present: ").append(na != null ? "YES" : "NO").append("\n");
            if (na != null) sb.append("NFC enabled: ").append(na.isEnabled() ? "YES" : "NO").append("\n");
            sb.append("HCE: ").append(pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) ? "YES" : "NO").append("\n");
        } catch (Throwable t) {
            sb.append("NFC error\n");
        }
        return sb.toString();
    }

    // ============================================================
    // ROOT
    // ============================================================
    private String readRootInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("── ROOT / SECURITY ──\n");
        sb.append("Rooted: ").append(isRooted ? "YES" : "NO").append("\n");
        sb.append("SELinux: ").append(getSelinux()).append("\n");
        sb.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
        sb.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
        return sb.toString();
    }

    // ============================================================
    // BATTERY HEALTH
    // ============================================================
    private String readBatteryHealth() {
        StringBuilder sb = new StringBuilder();
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent b = registerReceiver(null, ifilter);
            if (b != null) {
                int level = b.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = b.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    float pct = (level * 100f / scale);
                    sb.append("Level: ").append(String.format("%.0f%%", pct)).append("\n");
                }
                int temp = b.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
                if (temp != Integer.MIN_VALUE) sb.append("Temp: ").append(String.format("%.1f °C", temp / 10f)).append("\n");
            }
        } catch (Throwable ignored) {}
        return sb.toString();
    }

    // ============================================================
    // VIBRATOR / HAPTICS
    // ============================================================
    private String detectVibratorAmplitude() {
        try {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib == null) return "No vibrator service";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                return vib.hasAmplitudeControl() ? "Programmable" : "Fixed";
            return "Present (API<26)";
        } catch (Throwable t) {
            return "Unknown";
        }
    }

    private String detectHapticsClass() {
        String[] keys = {
                "ro.product.haptics_level",
                "ro.vibrator.haptic.feedback",
                "ro.vendor.vibrator.haptic"
        };
        for (String k : keys) {
            String v = getProp(k);
            if (v != null && !v.isEmpty()) return v;
        }
        return "Unknown";
    }

    // ============================================================
    // GNSS
    // ============================================================
    private String readGnssConstellations() {
        StringBuilder sb = new StringBuilder();
        sb.append("HW GNSS: ").append(getPackageManager().hasSystemFeature("android.hardware.location.gnss") ? "YES" : "NO").append("\n");
        try {
            String[] files = {
                    "/system/etc/gps.conf",
                    "/vendor/etc/gps.conf"
            };
            boolean any = false;
            for (String p : files) {
                File f = new File(p);
                if (!f.exists()) continue;
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.toLowerCase().contains("glonass") ||
                        line.toLowerCase().contains("bds") ||
                        line.toLowerCase().contains("galileo") ||
                        line.toLowerCase().contains("qzss")) {
                        sb.append(line).append("\n");
                        any = true;
                    }
                }
                br.close();
            }
            if (!any) sb.append("No constellations listed\n");
        } catch (Throwable ignored) {}
        return sb.toString();
    }

    // ============================================================
    // USB SPEED / OTG
    // ============================================================
    private String detectUsbSpeedModes() {
        StringBuilder sb = new StringBuilder();
        sb.append("USB host mode: ").append(getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO").append("\n");

        String[] keys = {
                "sys.usb.speed",
                "vendor.usb.speed",
                "ro.vendor.usb.speed"
        };
        boolean any = false;
        for (String k : keys) {
            String v = getProp(k);
            if (v != null && !v.isEmpty()) {
                sb.append(k).append(" = ").append(v).append("\n");
                any = true;
            }
        }
        if (!any) sb.append("No USB speed props\n");
        return sb.toString();
    }

    // ============================================================
    // MICROPHONES
    // ============================================================
    private String describeMicrophones() {
        StringBuilder sb = new StringBuilder();
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceInfo[] ins = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
            if (ins == null) return "No input devices\n";

            int builtin = 0, bt = 0, usb = 0, other = 0;
            for (AudioDeviceInfo d : ins) {
                switch (d.getType()) {
                    case AudioDeviceInfo.TYPE_BUILTIN_MIC: builtin++; break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP: bt++; break;
                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET: usb++; break;
                    default: other++; break;
                }
            }
            sb.append("Total: ").append(ins.length).append("\n");
            sb.append("Built-in: ").append(builtin).append("\n");
            sb.append("BT: ").append(bt).append("\n");
            sb.append("USB: ").append(usb).append("\n");
            sb.append("Other: ").append(other).append("\n");
        } catch (Throwable t) {
            sb.append("Mic error\n");
        }
        return sb.toString();
    }

    // ============================================================
    // AUDIO HAL
    // ============================================================
    private String detectAudioHalLevel() {
        String[] keys = {
                "ro.vendor.audio.hal.version",
                "ro.audio.hal.version",
                "audio_hal.version"
        };
        for (String k : keys) {
            String v = getProp(k);
            if (v != null && !v.isEmpty()) return k + " = " + v;
        }
        return "Unknown";
    }

    // ============================================================
    // OTHER PERIPHERALS
    // ============================================================
    private String readOtherPeripherals(PackageManager pm) {
        StringBuilder sb = new StringBuilder();
        sb.append("── OTHER PERIPHERALS ──\n");
        sb.append("IR blaster: ").append(pm.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR) ? "YES" : "NO").append("\n");
        sb.append("Barometer: ").append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER) ? "YES" : "NO").append("\n");
        sb.append("Step counter: ").append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) ? "YES" : "NO").append("\n");
        sb.append("Heart-rate sensor: ").append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE) ? "YES" : "NO").append("\n");
        return sb.toString();
    }

    // ============================================================
    // ROOT HELPERS
    // ============================================================
    private boolean isDeviceRooted() {
        if (Build.TAGS != null && Build.TAGS.contains("test-keys")) return true;
        String[] paths = { "/system/bin/su", "/system/xbin/su", "/sbin/su" };
        for (String p : paths) if (new File(p).exists()) return true;
        return "1".equals(getProp("ro.debuggable")) || "0".equals(getProp("ro.secure"));
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
}
