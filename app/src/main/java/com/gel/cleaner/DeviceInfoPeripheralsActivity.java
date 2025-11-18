package com.gel.cleaner;

// PERIPHERALS REPORT v4.0 — Professional Edition
// Camera / Biometrics / Sensors / Connectivity / Location / Other / BT / NFC / Root
// Ολόκληρο κελί έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
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
        // REFERENCES (9+1 SECTIONS)
        // ============================
        TextView txtCameraContent        = findViewById(R.id.txtCameraContent);
        TextView txtBiometricsContent    = findViewById(R.id.txtBiometricsContent);
        TextView txtSensorsContent       = findViewById(R.id.txtSensorsContent);
        TextView txtConnectivityContent  = findViewById(R.id.txtConnectivityContent);
        TextView txtLocationContent      = findViewById(R.id.txtLocationContent);
        TextView txtOtherPeripherals     = findViewById(R.id.txtOtherPeripheralsContent);
        TextView txtBluetoothContent     = findViewById(R.id.txtBluetoothContent);    // NEW
        TextView txtNfcContent           = findViewById(R.id.txtNfcContent);          // NEW
        TextView txtRootContent          = findViewById(R.id.txtRootContent);

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

        allContents = new TextView[]{
                txtCameraContent,
                txtBiometricsContent,
                txtSensorsContent,
                txtConnectivityContent,
                txtLocationContent,
                txtOtherPeripherals,
                txtBluetoothContent,
                txtNfcContent,
                txtRootContent
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
                iconRoot
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

        // camera2 level (best-effort)
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
                        String levelText;
                        switch (level) {
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                                levelText = "Level 3 / Advanced";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                                levelText = "Full";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                                levelText = "Limited";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                                levelText = "Legacy";
                                break;
                            default:
                                levelText = "Unknown";
                        }
                        cam.append("Camera2 hardware level (back): ").append(levelText).append("\n");
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        cam.append("Manual sensor controls: ")
                .append(pm.hasSystemFeature("android.hardware.camera.capability.manual_sensor") ? "YES" : "NO")
                .append("\n");
        cam.append("RAW capture: ")
                .append(pm.hasSystemFeature("android.hardware.camera.capability.raw") ? "YES" : "NO")
                .append("\n");

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
        bio.append("Strong biometrics (approx): ");
        if (fp || face || iris) {
            bio.append("YES\n");
        } else {
            bio.append("NO\n");
        }

        txtBiometricsContent.setText(bio.toString());

        // ===========================
        // SENSORS
        // ===========================
        StringBuilder sens = new StringBuilder();
        sens.append("── SENSORS ──\n");

        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            Sensor acc   = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyro  = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            Sensor mag   = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor prox  = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
            Sensor step  = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            sens.append("Accelerometer: ").append(acc != null ? "YES" : "NO").append("\n");
            sens.append("Gyroscope: ").append(gyro != null ? "YES" : "NO").append("\n");
            sens.append("Magnetometer / Compass: ").append(mag != null ? "YES" : "NO").append("\n");
            sens.append("Proximity: ").append(prox != null ? "YES" : "NO").append("\n");
            sens.append("Light sensor: ").append(light != null ? "YES" : "NO").append("\n");
            sens.append("Step counter: ").append(step != null ? "YES" : "NO").append("\n");

            // μετράμε πόσοι συνολικά
            int total = sm.getSensorList(Sensor.TYPE_ALL).size();
            sens.append("\nTotal sensors visible: ").append(total).append("\n");
        } else {
            sens.append("SensorManager not available\n");
        }

        txtSensorsContent.setText(sens.toString());

        // ===========================
        // CONNECTIVITY (high-level)
        // ===========================
        StringBuilder conn = new StringBuilder();
        conn.append("── CONNECTIVITY ──\n");

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (caps != null) {
                    boolean wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    boolean cell = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    boolean eth  = caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

                    conn.append("Active network: ");
                    if (wifi) conn.append("Wi-Fi ");
                    if (cell) conn.append("Mobile ");
                    if (eth)  conn.append("Ethernet ");
                    if (!wifi && !cell && !eth) conn.append("None");
                    conn.append("\n");

                    conn.append("Metered: ")
                            .append(cm.isActiveNetworkMetered() ? "YES" : "NO")
                            .append("\n");
                } else {
                    conn.append("Active network: None\n");
                }
            } else {
                @SuppressWarnings("deprecation")
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    conn.append("Active type: ").append(ni.getTypeName()).append("\n");
                    conn.append("Roaming: ").append(ni.isRoaming() ? "YES" : "NO").append("\n");
                } else {
                    conn.append("Active network: None\n");
                }
            }
        } else {
            conn.append("ConnectivityManager not available\n");
        }

        conn.append("\nWi-Fi supported: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO")
                .append("\n");
        conn.append("Wi-Fi Direct: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT) ? "YES" : "NO")
                .append("\n");

        txtConnectivityContent.setText(conn.toString());

        // ===========================
        // LOCATION
        // ===========================
        StringBuilder loc = new StringBuilder();
        loc.append("── LOCATION ──\n");
        loc.append("GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                .append("\n");
        loc.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO")
                .append("\n");
        loc.append("GNSS (multi-constellation, approx): ")
                .append(pm.hasSystemFeature("android.hardware.location.gps") ? "YES" : "UNKNOWN")
                .append("\n");

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            loc.append("\nProviders: ").append(lm.getAllProviders()).append("\n");
        }

        txtLocationContent.setText(loc.toString());

        // ===========================
        // OTHER PERIPHERALS
        // ===========================
        StringBuilder other = new StringBuilder();
        other.append("── OTHER PERIPHERALS ──\n");

        other.append("Vibrator: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_VIBRATION) ? "YES" : "NO")
                .append("\n");
        other.append("Barometer: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER) ? "YES" : "NO")
                .append("\n");
        other.append("Step detector: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR) ? "YES" : "NO")
                .append("\n");
        other.append("Heart rate sensor: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE) ? "YES" : "NO")
                .append("\n");

        other.append("USB host: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO")
                .append("\n");
        other.append("USB accessory: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY) ? "YES" : "NO")
                .append("\n");

        other.append("Telephony: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? "YES" : "NO")
                .append("\n");
        other.append("Audio output (basic): YES\n"); // πρακτικά όλα

        txtOtherPeripherals.setText(other.toString());

        // ===========================
        // BLUETOOTH DETAILED
        // ===========================
        StringBuilder bt = new StringBuilder();
        bt.append("── BLUETOOTH ──\n");

        BluetoothAdapter adapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bm != null) adapter = bm.getAdapter();
        } else {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (adapter == null) {
            bt.append("Bluetooth adapter: NOT PRESENT\n");
        } else {
            bt.append("Bluetooth adapter: PRESENT\n");
            bt.append("Enabled: ").append(adapter.isEnabled() ? "YES" : "NO").append("\n");
            bt.append("Address: ").append(safe(adapter.getAddress())).append("\n");
            bt.append("Name: ").append(safe(adapter.getName())).append("\n");

            bt.append("\nProfiles / Features (approx):\n");
            bt.append("Classic (BR/EDR): ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ? "YES" : "NO")
                    .append("\n");
            bt.append("Bluetooth LE: ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO")
                    .append("\n");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bt.append("Offloaded filtering: ")
                        .append(adapter.isOffloadedFilteringSupported() ? "YES" : "NO")
                        .append("\n");
                bt.append("Offloaded scan batching: ")
                        .append(adapter.isOffloadedScanBatchingSupported() ? "YES" : "NO")
                        .append("\n");
            }

            bt.append("\nCodec / Audio (best-effort flags):\n");
            bt.append("Variable codec details require system-level APIs (best effort only)\n");
        }

        txtBluetoothContent.setText(bt.toString());

        // ===========================
        // NFC DETAILED
        // ===========================
        StringBuilder nfcSb = new StringBuilder();
        nfcSb.append("── NFC ──\n");

        NfcAdapter nfcAdapter = null;
        try {
            NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
            if (nfcManager != null) {
                nfcAdapter = nfcManager.getDefaultAdapter();
            }
        } catch (Throwable ignored) {
        }

        if (nfcAdapter == null) {
            nfcSb.append("NFC adapter: NOT PRESENT\n");
        } else {
            nfcSb.append("NFC adapter: PRESENT\n");
            nfcSb.append("Enabled: ").append(nfcAdapter.isEnabled() ? "YES" : "NO").append("\n");
            nfcSb.append("Reader mode support: YES (API-level dependent)\n");

            boolean hce = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
            boolean hceF = pm.hasSystemFeature("android.hardware.nfc.hce_any");

            nfcSb.append("\nHost Card Emulation: ")
                    .append(hce ? "YES" : "NO").append("\n");
            nfcSb.append("HCE (any type): ")
                    .append(hceF ? "YES" : "NO").append("\n");

            nfcSb.append("\nSecure element (approx):\n");
            nfcSb.append("Details depend on OEM; not fully exposed via public APIs.\n");
        }

        txtNfcContent.setText(nfcSb.toString());

        // ===========================
        // ROOT EXTRAS
        // ===========================
        StringBuilder rootSb = new StringBuilder();
        rootSb.append("── ROOT EXTRAS ──\n");
        if (isRooted) {
            rootSb.append("Device appears ROOTED\n\n");
            rootSb.append("Build Tags: ").append(Build.TAGS).append("\n");
            rootSb.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            rootSb.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            rootSb.append("SELinux: ").append(getSelinux()).append("\n");
            rootSb.append("su path: ").append(checkSuPaths()).append("\n");
        } else {
            rootSb.append("Device appears NOT rooted\n");
            rootSb.append("Root-level debug info disabled\n");
        }
        txtRootContent.setText(rootSb.toString());

        // ===========================
        // EXPANDABLE HEADERS
        // ===========================
        setupSection(findViewById(R.id.headerCamera),        txtCameraContent,       iconCamera);
        setupSection(findViewById(R.id.headerBiometrics),    txtBiometricsContent,   iconBiometrics);
        setupSection(findViewById(R.id.headerSensors),       txtSensorsContent,      iconSensors);
        setupSection(findViewById(R.id.headerConnectivity),  txtConnectivityContent, iconConnectivity);
        setupSection(findViewById(R.id.headerLocation),      txtLocationContent,     iconLocation);
        setupSection(findViewById(R.id.headerOtherPeripherals), txtOtherPeripherals, iconOther);
        setupSection(findViewById(R.id.headerBluetooth),     txtBluetoothContent,    iconBluetooth);
        setupSection(findViewById(R.id.headerNfc),           txtNfcContent,          iconNfc);
        setupSection(findViewById(R.id.headerRoot),          txtRootContent,         iconRoot);
    }

    // ===========================
    // ONE-OPEN-ONLY LOGIC
    // ===========================
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

    // ===========================
    // HELPERS
    // ===========================
    private String safe(String v) {
        if (v == null) return "[n/a]";
        if ("02:00:00:00:00:00".equals(v)) return "[masked]";
        return v;
    }

    private boolean isEmptySafe(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String readSmallFile(File f) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            return br.readLine();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {
            }
        }
    }

    // ===== ROOT UTILS =====
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
        for (String p : paths) {
            if (new File(p).exists()) return p;
        }
        return "none";
    }
}
