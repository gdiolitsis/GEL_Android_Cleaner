package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    private boolean isRooted = false;

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

        // refs
        TextView txtCameraContent      = findViewById(R.id.txtCameraContent);
        TextView txtBiometricsContent  = findViewById(R.id.txtBiometricsContent);
        TextView txtSensorsContent     = findViewById(R.id.txtSensorsContent);
        TextView txtConnectivityContent= findViewById(R.id.txtConnectivityContent);
        TextView txtLocationContent    = findViewById(R.id.txtLocationContent);
        TextView txtOtherContent       = findViewById(R.id.txtOtherContent);
        TextView txtRootContent        = findViewById(R.id.txtRootContent);

        TextView iconCamera      = findViewById(R.id.iconCameraToggle);
        TextView iconBiometrics  = findViewById(R.id.iconBiometricsToggle);
        TextView iconSensors     = findViewById(R.id.iconSensorsToggle);
        TextView iconConnectivity= findViewById(R.id.iconConnectivityToggle);
        TextView iconLocation    = findViewById(R.id.iconLocationToggle);
        TextView iconOther       = findViewById(R.id.iconOtherToggle);
        TextView iconRoot        = findViewById(R.id.iconRootToggle);

        allContents = new TextView[]{
                txtCameraContent, txtBiometricsContent, txtSensorsContent,
                txtConnectivityContent, txtLocationContent, txtOtherContent, txtRootContent
        };
        allIcons = new TextView[]{
                iconCamera, iconBiometrics, iconSensors,
                iconConnectivity, iconLocation, iconOther, iconRoot
        };

        PacketManagerData(txtCameraContent, txtBiometricsContent,
                txtSensorsContent, txtConnectivityContent,
                txtLocationContent, txtOtherContent, txtRootContent);

        // expand/collapse
        setupSection(findViewById(R.id.headerCamera),       txtCameraContent,       iconCamera);
        setupSection(findViewById(R.id.headerBiometrics),   txtBiometricsContent,   iconBiometrics);
        setupSection(findViewById(R.id.headerSensors),      txtSensorsContent,      iconSensors);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);
        setupSection(findViewById(R.id.headerLocation),     txtLocationContent,     iconLocation);
        setupSection(findViewById(R.id.headerOther),        txtOtherContent,        iconOther);
        setupSection(findViewById(R.id.headerRoot),         txtRootContent,         iconRoot);
    }

    private void PacketManagerData(TextView txtCameraContent,
                                   TextView txtBiometricsContent,
                                   TextView txtSensorsContent,
                                   TextView txtConnectivityContent,
                                   TextView txtLocationContent,
                                   TextView txtOtherContent,
                                   TextView txtRootContent) {

        isRooted = isDeviceRooted();

        PackageManager pm = getPackageManager();
        StringBuilder sb;

        // CAMERA
        sb = new StringBuilder();
        sb.append("── CAMERA ──\n");
        sb.append("Any camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO")
                .append("\n");
        sb.append("Flash: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? "YES" : "NO")
                .append("\n");
        sb.append("Front camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? "YES" : "NO")
                .append("\n");
        txtCameraContent.setText(sb.toString());

        // BIOMETRICS
        sb = new StringBuilder();
        sb.append("── BIOMETRICS ──\n");
        boolean hasFingerprint =
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                        || pm.hasSystemFeature("android.hardware.fingerprint");
        sb.append("Fingerprint sensor: ").append(hasFingerprint ? "YES" : "NO").append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasFace =
                    pm.hasSystemFeature(PackageManager.FEATURE_FACE)
                            || pm.hasSystemFeature("android.hardware.biometrics.face");
            sb.append("Face unlock: ").append(hasFace ? "YES" : "NO").append("\n");
        } else {
            sb.append("Face unlock: N/A (API < 29)\n");
        }
        txtBiometricsContent.setText(sb.toString());

        // SENSORS
        sb = new StringBuilder();
        sb.append("── SENSORS ──\n");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors != null && !sensors.isEmpty()) {
                for (Sensor sensor : sensors) {
                    sb.append("• ").append(sensor.getName()).append("\n");
                }
            } else {
                sb.append("No sensors reported\n");
            }
        } else {
            sb.append("SensorManager not available\n");
        }
        txtSensorsContent.setText(sb.toString());

        // CONNECTIVITY
        sb = new StringBuilder();
        sb.append("── CONNECTIVITY ──\n");
        sb.append("NFC: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO")
                .append("\n");
        sb.append("Bluetooth: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ? "YES" : "NO")
                .append("\n");
        sb.append("BLE: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO")
                .append("\n");
        sb.append("Wi-Fi: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO")
                .append("\n");
        sb.append("5G: ")
                .append(pm.hasSystemFeature("android.hardware.telephony.5g") ? "YES" : "NO")
                .append("\n");
        txtConnectivityContent.setText(sb.toString());

        // LOCATION
        sb = new StringBuilder();
        sb.append("── LOCATION ──\n");
        sb.append("GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                .append("\n");
        sb.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO")
                .append("\n");
        txtLocationContent.setText(sb.toString());

        // OTHER
        sb = new StringBuilder();
        sb.append("── OTHER ──\n");
        sb.append("USB host: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO")
                .append("\n");
        sb.append("USB accessory: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY) ? "YES" : "NO")
                .append("\n");

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVib = vib != null && vib.hasVibrator();
        sb.append("Vibrator: ").append(hasVib ? "YES" : "NO").append("\n");
        txtOtherContent.setText(sb.toString());

        // ROOT EXTRAS
        sb = new StringBuilder();
        sb.append("── ROOT EXTRA INFO ──\n");
        if (isRooted) {
            sb.append("Device appears ROOTED.\n\n");
            sb.append("Build Tags: ").append(Build.TAGS).append("\n");
            sb.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            sb.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            sb.append("SELinux: ").append(getSelinux()).append("\n");
            sb.append("su path: ").append(checkSuPaths()).append("\n");
        } else {
            sb.append("Device appears NOT rooted.\n");
            sb.append("Extra low-level peripheral debug is disabled.\n");
        }
        txtRootContent.setText(sb.toString());
    }

    // ========== expand / collapse ==========
    private void setupSection(View header, final TextView content, final TextView icon) {
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView contentToOpen, TextView iconToUpdate) {
        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == contentToOpen) continue;
            c.setVisibility(View.GONE);
            ic.setText("＋");
        }

        if (contentToOpen.getVisibility() == View.VISIBLE) {
            contentToOpen.setVisibility(View.GONE);
            iconToUpdate.setText("＋");
        } else {
            contentToOpen.setVisibility(View.VISIBLE);
            iconToUpdate.setText("−");
        }
    }

    // ========== ROOT UTILS ==========
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

        return "1".equals(debuggable) || "0".equals(secure);
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
