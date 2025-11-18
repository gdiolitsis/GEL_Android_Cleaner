package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

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

        StringBuilder secCamera      = new StringBuilder();
        StringBuilder secBiometrics  = new StringBuilder();
        StringBuilder secSensors     = new StringBuilder();
        StringBuilder secConnectivity= new StringBuilder();
        StringBuilder secLocation    = new StringBuilder();
        StringBuilder secOther       = new StringBuilder();
        StringBuilder secRootExtras  = new StringBuilder();

        // =====================================================
        // CAMERA
        // =====================================================
        secCamera.append("── CAMERA ──\n");
        secCamera.append("Any camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO").append("\n");
        secCamera.append("Back camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ? "YES" : "NO").append("\n");
        secCamera.append("Front camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? "YES" : "NO").append("\n");
        secCamera.append("Flash: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? "YES" : "NO").append("\n");
        secCamera.append("\n");

        // =====================================================
        // BIOMETRICS
        // =====================================================
        secBiometrics.append("── BIOMETRICS ──\n");
        boolean hasFingerprint =
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                        || pm.hasSystemFeature("android.hardware.fingerprint");
        secBiometrics.append("Fingerprint sensor: ").append(hasFingerprint ? "YES" : "NO").append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasFace =
                    pm.hasSystemFeature(PackageManager.FEATURE_FACE)
                            || pm.hasSystemFeature("android.hardware.biometrics.face");
            secBiometrics.append("Face unlock: ").append(hasFace ? "YES" : "NO").append("\n");
        } else {
            secBiometrics.append("Face unlock: N/A (Android < 10)\n");
        }
        secBiometrics.append("\n");

        // =====================================================
        // SENSORS
        // =====================================================
        secSensors.append("── SENSORS ──\n");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors != null && !sensors.isEmpty()) {
                for (Sensor sensor : sensors) {
                    secSensors.append("• ")
                            .append(sensor.getName())
                            .append(" (type=")
                            .append(sensor.getType())
                            .append(", vendor=")
                            .append(sensor.getVendor())
                            .append(")\n");
                }
            } else {
                secSensors.append("No sensors reported\n");
            }
        } else {
            secSensors.append("SensorManager not available\n");
        }
        secSensors.append("\n");

        // =====================================================
        // CONNECTIVITY
        // =====================================================
        secConnectivity.append("── CONNECTIVITY ──\n");
        secConnectivity.append("Wi-Fi: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO").append("\n");
        secConnectivity.append("Wi-Fi Direct: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT) ? "YES" : "NO").append("\n");
        secConnectivity.append("Bluetooth: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ? "YES" : "NO").append("\n");
        secConnectivity.append("BLE: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO").append("\n");
        secConnectivity.append("NFC: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO").append("\n");
        secConnectivity.append("NFC Host Card Emulation: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) ? "YES" : "NO").append("\n");

        secConnectivity.append("Telephony: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? "YES" : "NO").append("\n");
        secConnectivity.append("Telephony GSM: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM) ? "YES" : "NO").append("\n");
        secConnectivity.append("Telephony CDMA: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA) ? "YES" : "NO").append("\n");
        secConnectivity.append("5G (generic flag): ")
                .append(pm.hasSystemFeature("android.hardware.telephony.5g") ? "YES" : "NO").append("\n");
        secConnectivity.append("\n");

        // =====================================================
        // LOCATION
        // =====================================================
        secLocation.append("── LOCATION ──\n");
        secLocation.append("GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO").append("\n");
        secLocation.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO").append("\n");
        secLocation.append("GNSS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GNSS) ? "YES" : "NO").append("\n");
        secLocation.append("\n");

        // =====================================================
        // OTHER PERIPHERALS
        // =====================================================
        secOther.append("── OTHER PERIPHERALS ──\n");
        secOther.append("USB host: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO").append("\n");
        secOther.append("USB accessory: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY) ? "YES" : "NO").append("\n");
        secOther.append("Microphone: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE) ? "YES" : "NO").append("\n");

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVib = vib != null && vib.hasVibrator();
        secOther.append("Vibrator: ").append(hasVib ? "YES" : "NO").append("\n");

        secOther.append("Gamepad support: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_GAMEPAD) ? "YES" : "NO").append("\n");

        secOther.append("\n");

        // =====================================================
        // ROOT EXTRA INFO
        // =====================================================
        if (isRooted) {
            secRootExtras.append("── ROOT EXTRA INFO ──\n");
            secRootExtras.append("Build Tags: ").append(Build.TAGS).append("\n");
            secRootExtras.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            secRootExtras.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            secRootExtras.append("SELinux: ").append(getSelinux()).append("\n");
            secRootExtras.append("su path: ").append(checkSuPaths()).append("\n");
            secRootExtras.append("\n");
        }

        // FULL TEXT
        final String textCamera      = secCamera.toString();
        final String textBiometrics  = secBiometrics.toString();
        final String textSensors     = secSensors.toString();
        final String textConnectivity= secConnectivity.toString();
        final String textLocation    = secLocation.toString();
        final String textOther       = secOther.toString();
        final String textRootExtras  = secRootExtras.toString();

        final String textAll =
                textCamera +
                textBiometrics +
                textSensors +
                textConnectivity +
                textLocation +
                textOther +
                textRootExtras;

        if (info != null) {
            info.setText(textAll);
        }

        setupTabsPeripherals(textAll, textCamera, textBiometrics, textSensors,
                textConnectivity, textLocation, textOther, textRootExtras);
    }

    // ------------------------------------------------------------
    // TAB BUTTONS HANDLER (PERIPHERALS)
    // ------------------------------------------------------------
    private void setupTabsPeripherals(String all,
                                      String camera,
                                      String biometrics,
                                      String sensors,
                                      String connectivity,
                                      String location,
                                      String other,
                                      String rootExtras) {

        final TextView info = findViewById(R.id.txtDeviceInfo);
        if (info == null) return;

        Button tabAll        = findViewById(R.id.btnTabAllPeriph);
        Button tabCamera     = findViewById(R.id.btnTabCameraPeriph);
        Button tabBiometrics = findViewById(R.id.btnTabBiometricsPeriph);
        Button tabSensors    = findViewById(R.id.btnTabSensorsPeriph);
        Button tabConn       = findViewById(R.id.btnTabConnPeriph);
        Button tabLocation   = findViewById(R.id.btnTabLocationPeriph);
        Button tabOther      = findViewById(R.id.btnTabOtherPeriph);
        Button tabRoot       = findViewById(R.id.btnTabRootPeriph);

        if (tabAll == null) return;

        tabAll.setOnClickListener(v -> info.setText(all));
        if (tabCamera     != null) tabCamera.setOnClickListener(v -> info.setText(camera));
        if (tabBiometrics != null) tabBiometrics.setOnClickListener(v -> info.setText(biometrics));
        if (tabSensors    != null) tabSensors.setOnClickListener(v -> info.setText(sensors));
        if (tabConn       != null) tabConn.setOnClickListener(v -> info.setText(connectivity));
        if (tabLocation   != null) tabLocation.setOnClickListener(v -> info.setText(location));
        if (tabOther      != null) tabOther.setOnClickListener(v -> info.setText(other));
        if (tabRoot       != null) tabRoot.setOnClickListener(v -> info.setText(
                rootExtras.isEmpty() ? "No extra root info.\n" : rootExtras
        ));
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
