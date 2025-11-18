package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    private boolean isRooted = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_peripherals);

        TextView title = findViewById(R.id.txtTitleDevice);
        TextView info  = findViewById(R.id.txtDeviceInfo);

        if (title != null) {
            title.setText(getString(R.string.phone_info_peripherals));
        }

        // ROOT CHECK
        isRooted = isDeviceRooted();

        PackageManager pm = getPackageManager();
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        StringBuilder s = new StringBuilder();

        // =====================================================
        // CAMERA
        // =====================================================
        s.append("── CAMERA ──\n");
        s.append("Any camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO")
                .append("\n");
        s.append("Rear camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ? "YES" : "NO")
                .append("\n");
        s.append("Front camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? "YES" : "NO")
                .append("\n");
        s.append("Flash: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? "YES" : "NO")
                .append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            s.append("Full camera2 support: ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR)
                            ? "YES" : "NO")
                    .append("\n");
        }
        s.append("\n");

        // =====================================================
        // AUDIO
        // =====================================================
        s.append("── AUDIO ──\n");
        s.append("Microphone: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) ? "YES" : "NO")
                .append("\n");
        s.append("Audio output (speaker/headset): YES\n");
        s.append("\n");

        // =====================================================
        // BIOMETRICS
        // =====================================================
        s.append("── BIOMETRICS ──\n");
        boolean hasFingerprint =
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                        || pm.hasSystemFeature("android.hardware.fingerprint");
        s.append("Fingerprint sensor: ").append(hasFingerprint ? "YES" : "NO").append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasFace =
                    pm.hasSystemFeature(PackageManager.FEATURE_FACE)
                            || pm.hasSystemFeature("android.hardware.biometrics.face");
            s.append("Face unlock: ").append(hasFace ? "YES" : "NO").append("\n");
        }

        boolean hasIris =
                pm.hasSystemFeature("android.hardware.biometrics.iris")
                        || pm.hasSystemFeature("com.samsung.android.bio.iris");
        s.append("Iris scanner: ").append(hasIris ? "YES" : "NO").append("\n\n");

        // =====================================================
        // SENSORS (SUMMARY)
        // =====================================================
        s.append("── SENSORS (SUMMARY) ──\n");
        if (sm != null) {
            s.append("Accelerometer: ")
                    .append(hasSensor(sm, Sensor.TYPE_ACCELEROMETER) ? "YES" : "NO").append("\n");
            s.append("Gyroscope: ")
                    .append(hasSensor(sm, Sensor.TYPE_GYROSCOPE) ? "YES" : "NO").append("\n");
            s.append("Proximity: ")
                    .append(hasSensor(sm, Sensor.TYPE_PROXIMITY) ? "YES" : "NO").append("\n");
            s.append("Light: ")
                    .append(hasSensor(sm, Sensor.TYPE_LIGHT) ? "YES" : "NO").append("\n");
            s.append("Magnetometer: ")
                    .append(hasSensor(sm, Sensor.TYPE_MAGNETIC_FIELD) ? "YES" : "NO").append("\n");
            s.append("Barometer: ")
                    .append(hasSensor(sm, Sensor.TYPE_PRESSURE) ? "YES" : "NO").append("\n");
            s.append("Step counter: ")
                    .append(hasSensor(sm, Sensor.TYPE_STEP_COUNTER) ? "YES" : "NO").append("\n");
            s.append("Heart rate: ")
                    .append(hasSensor(sm, Sensor.TYPE_HEART_RATE) ? "YES" : "NO").append("\n");
        } else {
            s.append("SensorManager not available\n");
        }
        s.append("\n");

        // =====================================================
        // SENSORS (FULL LIST)
        // =====================================================
        s.append("── SENSORS (FULL LIST) ──\n");
        if (sm != null) {
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors != null && !sensors.isEmpty()) {
                for (Sensor sensor : sensors) {
                    s.append("• ").append(sensor.getName())
                            .append(" [type ").append(sensor.getType()).append("]\n");
                }
            } else {
                s.append("No sensors reported\n");
            }
        } else {
            s.append("SensorManager not available\n");
        }
        s.append("\n");

        // =====================================================
        // CONNECTIVITY
        // =====================================================
        s.append("── CONNECTIVITY ──\n");
        s.append("Wi-Fi: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO")
                .append("\n");
        s.append("Wi-Fi Direct: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT) ? "YES" : "NO")
                .append("\n");
        s.append("Bluetooth: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ? "YES" : "NO")
                .append("\n");
        s.append("BLE: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO")
                .append("\n");
        s.append("NFC: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO")
                .append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            s.append("NFC payment (HCE): ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
                            ? "YES" : "NO")
                    .append("\n");
        }
        s.append("Telephony (SIM): ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? "YES" : "NO")
                .append("\n");
        s.append("5G capable: ")
                .append(pm.hasSystemFeature("android.hardware.telephony.5g") ? "YES" : "NO")
                .append("\n");
        s.append("Ethernet: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_ETHERNET) ? "YES" : "NO")
                .append("\n\n");

        // =====================================================
        // LOCATION
        // =====================================================
        s.append("── LOCATION ──\n");
        s.append("GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                .append("\n");
        s.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO")
                .append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            s.append("GNSS dual-band: ")
                    .append(pm.hasSystemFeature("android.hardware.location.gnss") ? "YES" : "NO")
                    .append("\n");
        }
        s.append("\n");

        // =====================================================
        // INPUT / GAME / EXTRAS
        // =====================================================
        s.append("── INPUT & GAME ──\n");
        s.append("Hardware keyboard: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_HARDWARE_KEYBOARD) ? "YES" : "NO")
                .append("\n");
        s.append("Gamepad support: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_GAMEPAD) ? "YES" : "NO")
                .append("\n");
        s.append("VR mode: ")
                .append(pm.hasSystemFeature("android.hardware.vr.high_performance") ? "YES" : "NO")
                .append("\n\n");

        // =====================================================
        // OTHER PERIPHERALS
        // =====================================================
        s.append("── OTHER ──\n");
        s.append("USB host: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO")
                .append("\n");
        s.append("USB accessory: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY) ? "YES" : "NO")
                .append("\n");
        boolean hasVib = vib != null && vib.hasVibrator();
        s.append("Vibrator: ").append(hasVib ? "YES" : "NO").append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            s.append("Advanced vibration effects: ")
                    .append(hasVib && vib.hasAmplitudeControl() ? "YES" : "NO")
                    .append("\n");
        }
        s.append("\n");

        // =====================================================
        // EXTRA PERIPHERALS INFO FOR ROOTED DEVICES
        // =====================================================
        if (isRooted) {
            s.append("── ROOT EXTRA INFO ──\n");
            s.append("Build Tags: ").append(Build.TAGS).append("\n");
            s.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            s.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            s.append("SELinux: ").append(getSelinux()).append("\n");
            s.append("su path: ").append(checkSuPaths()).append("\n");
            s.append("\n");
        }

        if (info != null) {
            info.setText(s.toString());
        }
    }

    // ============================================================
    // SENSOR HELPER
    // ============================================================
    private boolean hasSensor(SensorManager sm, int type) {
        if (sm == null) return false;
        return sm.getDefaultSensor(type) != null;
    }

    // ============================================================
    // ROOT UTILS
    // ============================================================
    private boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;

        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su"
        };

        for (String p : paths) {
            if (new File(p).exists()) return true;
        }

        String debuggable = getProp("ro.debuggable");
        String secure = getProp("ro.secure");

        if ("1".equals(debuggable) || "0".equals(secure)) return true;

        return false;
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "[empty]";
        } catch (Exception e) {
            return "[error]";
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

        for (String p : paths) {
            if (new File(p).exists()) return p;
        }
        return "none";
    }
}
```0
