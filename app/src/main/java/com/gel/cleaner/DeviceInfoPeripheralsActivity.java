package com.gel.cleaner;

// PERIPHERALS REPORT v6.3 — SERVICE-PRO EDITION (GEL)
// Camera / Biometrics / Sensors / Connectivity / Location / Other / BT / NFC / Root
// + Battery Health / Temperature / Charging Type / UWB / Vibrator Amplitude / Haptics Class
// + GNSS Constellations + L5 hint / USB OTG Speed Modes (SuperSpeed flag) / Microphone Map
// + Audio HAL Level (multi-source: props + audio_policy_configuration.xml)
// + WiFi 6/6E (standard + band) / VoLTE-IMS / VoWiFi / 5G NR flag
// + IR / Barometer / Steps / HR Sensor
// Ολόκληρο κελί έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

import android.app.ActivityManager;
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
import java.util.ArrayList;
import java.util.List;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    private boolean isRooted = false;

    // για το "άνοιγε μόνο ένα-ένα"
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
                        cam.append("Camera2 class: ").append(describeCamera2Level(level)).append("\n");
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

            Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyr = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            Sensor mag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

            sens.append("Accelerometer: ").append(acc != null ? "YES" : "NO").append("\n");
            sens.append("Gyroscope: ").append(gyr != null ? "YES" : "NO").append("\n");
            sens.append("Magnetometer: ").append(mag != null ? "YES" : "NO").append("\n");
            sens.append("Proximity: ").append(prox != null ? "YES" : "NO").append("\n");
            sens.append("Light sensor: ").append(light != null ? "YES" : "NO").append("\n");
        } else {
            sens.append("SensorManager: [unavailable]\n");
        }

        txtSensorsContent.setText(sens.toString());

        // ===========================
        // CONNECTIVITY
        // ===========================
        StringBuilder conn = new StringBuilder();
        conn.append("── CONNECTIVITY ──\n");

        // Basic feature flags
        conn.append("Telephony: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? "YES" : "NO")
                .append("\n");
        conn.append("5G NR flag: ")
                .append(pm.hasSystemFeature("android.hardware.telephony.nr") ? "YES" : "NO")
                .append("\n");
        conn.append("IMS / VoLTE flag: ")
                .append(pm.hasSystemFeature("android.hardware.telephony.ims") ? "YES" : "NO")
                .append("\n");
        conn.append("WiFi: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO")
                .append("\n");
        conn.append("WiFi Direct: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT) ? "YES" : "NO")
                .append("\n");
        conn.append("Ethernet: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_ETHERNET) ? "YES" : "NO")
                .append("\n");

        // Active network info
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    if (caps != null) {
                        boolean hasWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                        boolean hasCell = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                        boolean hasEth  = caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

                        conn.append("Active (WiFi): ").append(hasWifi ? "YES" : "NO").append("\n");
                        conn.append("Active (Cellular): ").append(hasCell ? "YES" : "NO").append("\n");
                        conn.append("Active (Ethernet): ").append(hasEth ? "YES" : "NO").append("\n");
                    } else {
                        conn.append("Active network: [none]\n");
                    }
                } else {
                    @SuppressWarnings("deprecation")
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (ni != null && ni.isConnected()) {
                        conn.append("Active type: ").append(ni.getTypeName()).append("\n");
                    } else {
                        conn.append("Active network: [none]\n");
                    }
                }
            } else {
                conn.append("ConnectivityManager: [unavailable]\n");
            }
        } catch (Throwable t) {
            conn.append("Connectivity error: ").append(t.getMessage()).append("\n");
        }

        // WiFi details
        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null) {
                    int linkSpeed = wi.getLinkSpeed(); // Mbps
                    int freq = wi.getFrequency();      // MHz
                    conn.append("\n[WiFi link]\n");
                    conn.append("Link speed: ").append(linkSpeed).append(" Mbps\n");
                    conn.append("Frequency: ").append(freq).append(" MHz\n");

                    // BAND (2.4 / 5 / 6 GHz)
                    conn.append("Band: ").append(describeWifiBand(freq)).append("\n");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        int std = wi.getWifiStandard();
                        String stdStr;
                        switch (std) {
                            case 1:  // Legacy a/b/g
                                stdStr = "Legacy (11a/b/g)";
                                break;
                            case 4:  // WiFi 4 (11n)
                                stdStr = "WiFi 4 (11n)";
                                break;
                            case 5:  // WiFi 5 (11ac)
                                stdStr = "WiFi 5 (11ac)";
                                break;
                            case 6:  // WiFi 6/6E (11ax)
                                stdStr = "WiFi 6/6E (11ax)";
                                break;
                            default:
                                stdStr = "Unknown";
                                break;
                        }
                        conn.append("WiFi standard: ").append(stdStr).append("\n");
                        conn.append("PHY mode hint: ").append(describeWifiPhy(std)).append("\n");
                    } else {
                        conn.append("WiFi standard: [API < 30]\n");
                    }

                } else {
                    conn.append("\n[WiFi] No connection info\n");
                }
            } else {
                conn.append("\n[WiFi] WifiManager unavailable\n");
            }
        } catch (Throwable t) {
            conn.append("\nWiFi error: ").append(t.getMessage()).append("\n");
        }

        // Baseband / VoLTE / VoWiFi
        String baseband = getProp("gsm.version.baseband");
        if (!isEmptySafe(baseband)) {
            conn.append("\n[Modem]\n");
            conn.append("Baseband: ").append(baseband).append("\n");
        }

        String volteProv  = getVolteStatus();
        String vowifiProv = getVowifiStatus();
        if (!isEmptySafe(volteProv) || !isEmptySafe(vowifiProv)) {
            conn.append("[IMS Provisioning]\n");
            if (!isEmptySafe(volteProv)) {
                conn.append("VoLTE: ").append(volteProv).append("\n");
            }
            if (!isEmptySafe(vowifiProv)) {
                conn.append("VoWiFi: ").append(vowifiProv).append("\n");
            }
        }

        txtConnectivityContent.setText(conn.toString());

        // ===========================
        // LOCATION
        // ===========================
        StringBuilder loc = new StringBuilder();
        loc.append("── LOCATION ──\n");

        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                boolean gpsProv = lm.getProvider(LocationManager.GPS_PROVIDER) != null;
                boolean netProv = lm.getProvider(LocationManager.NETWORK_PROVIDER) != null;

                loc.append("GPS provider: ").append(gpsProv ? "YES" : "NO").append("\n");
                loc.append("Network provider: ").append(netProv ? "YES" : "NO").append("\n");
            } else {
                loc.append("LocationManager: [unavailable]\n");
            }
        } catch (Throwable t) {
            loc.append("Location error: ").append(t.getMessage()).append("\n");
        }

        loc.append("HW GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                .append("\n");
        loc.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO")
                .append("\n");

        loc.append("\nGNSS constellations:\n");
        loc.append(readGnssConstellations());

        txtLocationContent.setText(loc.toString());

        // ===========================
        // OTHER PERIPHERALS
        // ===========================
        StringBuilder other = new StringBuilder();
        other.append("── OTHER PERIPHERALS ──\n");

        other.append("IR blaster: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR) ? "YES" : "NO")
                .append("\n");

        other.append("Barometer: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER) ? "YES" : "NO")
                .append("\n");

        other.append("Step counter: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) ? "YES" : "NO")
                .append("\n");

        other.append("Step detector: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR) ? "YES" : "NO")
                .append("\n");

        other.append("Heart-rate sensor: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE) ? "YES" : "NO")
                .append("\n");

        other.append("Ambient temperature sensor: ")
                .append(pm.hasSystemFeature("android.hardware.sensor.ambient_temperature") ? "YES" : "NO")
                .append("\n");

        txtOtherPeripherals.setText(other.toString());

        // ===========================
        // BLUETOOTH
        // ===========================
        StringBuilder bt = new StringBuilder();
        bt.append("── BLUETOOTH ──\n");
        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter ba = null;
            if (bm != null) {
                ba = bm.getAdapter();
            }
            if (ba == null) {
                ba = BluetoothAdapter.getDefaultAdapter();
            }

            if (ba == null) {
                bt.append("Adapter: [none]\n");
            } else {
                bt.append("Enabled: ").append(ba.isEnabled() ? "YES" : "NO").append("\n");
                try {
                    bt.append("Name: ").append(safe(ba.getName())).append("\n");
                } catch (SecurityException se) {
                    bt.append("Name: [permission denied]\n");
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    bt.append("Address: ").append(safe(ba.getAddress())).append("\n");
                } else {
                    bt.append("Address: [hidden on Android 12+]\n");
                }

                bt.append("LE support: ")
                        .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO")
                        .append("\n");
            }

            String codecProp = getProp("persist.bluetooth.a2dp_offload.cap");
            if (!isEmptySafe(codecProp)) {
                bt.append("A2DP offload caps: ").append(codecProp).append("\n");
                String codecList = parseBtCodecs(codecProp);
                if (!isEmptySafe(codecList)) {
                    bt.append("Audio codecs hint: ").append(codecList).append("\n");
                }
            }

        } catch (Throwable t) {
            bt.append("Bluetooth error: ").append(t.getMessage()).append("\n");
        }

        txtBluetoothContent.setText(bt.toString());

        // ===========================
        // NFC
        // ===========================
        StringBuilder nfc = new StringBuilder();
        nfc.append("── NFC ──\n");

        try {
            NfcManager nfm = (NfcManager) getSystemService(Context.NFC_SERVICE);
            NfcAdapter na = null;
            if (nfm != null) {
                na = nfm.getDefaultAdapter();
            }

            if (na == null) {
                nfc.append("NFC adapter: [none]\n");
            } else {
                nfc.append("NFC present: YES\n");
                nfc.append("NFC enabled: ").append(na.isEnabled() ? "YES" : "NO").append("\n");
            }

            nfc.append("Reader/Writer: ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO")
                    .append("\n");
            nfc.append("Host Card Emulation (HCE): ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) ? "YES" : "NO")
                    .append("\n");

            String nfcHw = getProp("ro.hardware.nfc");
            if (!isEmptySafe(nfcHw)) {
                nfc.append("NFC hardware: ").append(nfcHw).append("\n");
            }

        } catch (Throwable t) {
            nfc.append("NFC error: ").append(t.getMessage()).append("\n");
        }

        txtNfcContent.setText(nfc.toString());

        // ===========================
        // ROOT
        // ===========================
        StringBuilder root = new StringBuilder();
        root.append("── ROOT / SECURITY ──\n");
        root.append("Rooted (heuristic): ").append(isRooted ? "YES" : "NO").append("\n");
        root.append("su path: ").append(checkSuPaths()).append("\n");
        root.append("SELinux: ").append(getSelinux()).append("\n");
        root.append("Build tags: ").append(safe(Build.TAGS)).append("\n");
        root.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
        root.append("ro.secure: ").append(getProp("ro.secure")).append("\n");

        txtRootContent.setText(root.toString());

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
        setupSection(findViewById(R.id.headerThermalZones),
             findViewById(R.id.txtThermalZonesContent),
             findViewById(R.id.iconThermalZonesToggle));

setupSection(findViewById(R.id.headerVulkan),
             findViewById(R.id.txtVulkanContent),
             findViewById(R.id.iconVulkanToggle));

setupSection(findViewById(R.id.headerThermalProfiles),
             findViewById(R.id.txtThermalProfilesContent),
             findViewById(R.id.iconThermalProfilesToggle));

allContents.add(findViewById(R.id.txtThermalZonesContent));
allContents.add(findViewById(R.id.txtVulkanContent));
allContents.add(findViewById(R.id.txtThermalProfilesContent));

allIcons.add(findViewById(R.id.iconThermalZonesToggle));
allIcons.add(findViewById(R.id.iconVulkanToggle));
allIcons.add(findViewById(R.id.iconThermalProfilesToggle));
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

                // Temperature
                int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
                if (temp != Integer.MIN_VALUE) {
                    float c = temp / 10f;
                    sb.append("Temperature: ").append(String.format("%.1f", c)).append(" °C\n");
                }

                // Charging type
                int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                String pluggedStr;
                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        pluggedStr = "AC charger";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        pluggedStr = "USB power";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        pluggedStr = "Wireless charging";
                        break;
                    default:
                        pluggedStr = "Not charging / battery only";
                        break;
                }
                sb.append("Charging type: ").append(pluggedStr).append("\n");
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return vib.hasAmplitudeControl()
                        ? "Supported (programmable)"
                        : "Present (fixed strength)";
            }
            // API < 26: δεν υπάρχει amplitude API, αλλά ο δονητής είναι παρών
            return "Present (API < 26)";
        } catch (Throwable t) {
            return "Error";
        }
    }

    private String detectHapticsClass() {
        // Προσπαθούμε πρώτα από διάφορα system properties
        String[] keys = {
                "ro.product.haptics_level",
                "ro.vibrator.haptic.feedback",
                "ro.vendor.vibrator.haptic",
                "ro.vendor.product.haptics_level"
        };
        for (String k : keys) {
            String v = getProp(k);
            if (!isEmptySafe(v)) return v;
        }

        // Προσπαθούμε να βρούμε τύπο από sysfs (ERM / LRA κ.λπ.)
        String sysfsType = detectHapticsFromSysfs();
        if (!isEmptySafe(sysfsType)) return sysfsType;

        // Fallback: έλεγχος ύπαρξης vibrator στο sysfs
        try {
            File f1 = new File("/sys/class/leds/vibrator");
            File f2 = new File("/sys/class/timed_output/vibrator");
            if (f1.exists() || f2.exists()) {
                return "Basic haptics (sysfs)";
            }
        } catch (Throwable ignored) {}

        return "Unknown";
    }

    private String detectHapticsFromSysfs() {
        try {
            String[] candidates = {
                    "/sys/class/leds/vibrator/device/type",
                    "/sys/class/leds/vibrator/device/vibrator_type",
                    "/sys/class/leds/vibrator/vibrator_type",
                    "/sys/class/timed_output/vibrator/device/type"
            };
            for (String path : candidates) {
                File f = new File(path);
                if (!f.exists()) continue;
                String val = readSmallFile(f);
                if (isEmptySafe(val)) continue;
                String lower = val.toLowerCase();
                if (lower.contains("lra")) {
                    return "LRA haptics (" + val.trim() + ")";
                }
                if (lower.contains("erm")) {
                    return "ERM haptics (" + val.trim() + ")";
                }
                return val.trim();
            }
        } catch (Throwable ignored) {}
        return "";
    }

    private String readGnssConstellations() {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        boolean hasL5Hint = false;

        // Hardware feature summary
        try {
            PackageManager pm = getPackageManager();
            sb.append("HW GNSS feature: ")
                    .append(pm.hasSystemFeature("android.hardware.location.gnss") ? "YES" : "NO")
                    .append("\n");
            sb.append("Legacy GPS flag: ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                    .append("\n\n");
        } catch (Throwable ignored) {}

        try {
            String[] paths = {
                    "/system/etc/gps.conf",
                    "/vendor/etc/gps.conf",
                    "/system/etc/gnss.conf",
                    "/vendor/etc/gnss.conf"
            };
            for (String p : paths) {
                File f = new File(p);
                if (!f.exists()) continue;
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    String lower = line.toLowerCase();
                    if (lower.contains("glonass") || lower.contains("bds")
                            || lower.contains("galileo") || lower.contains("qzss")
                            || lower.contains("gps")) {
                        sb.append(line).append("\n");
                        found = true;
                    }
                    if (lower.contains("l5") || lower.contains("e5") || lower.contains("b2a")) {
                        hasL5Hint = true;
                    }
                }
                br.close();
            }
            if (!found) sb.append("No constellations listed\n");
            if (hasL5Hint) sb.append("L5/E5 band hints present in config\n");
        } catch (Throwable ignored) {
            if (!found) sb.append("No constellations listed\n");
        }
        return sb.toString();
    }

    private String detectUsbSpeedModes() {
        StringBuilder sb = new StringBuilder();
        PackageManager pm = getPackageManager();

        // Capabilities
        boolean host = pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST);
        boolean accessory = pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY);

        sb.append("USB host mode: ").append(host ? "YES" : "NO").append("\n");
        sb.append("USB accessory mode: ").append(accessory ? "YES" : "NO").append("\n");

        // Common USB-related properties
        String[] keys = {
                "sys.usb.speed",
                "sys.usb.controller",
                "vendor.usb.speed",
                "ro.usb_speed",
                "ro.vendor.usb.speed",
                "sys.usb.typec.mode",
                "sys.usb.config"
        };
        boolean anyProp = false;
        for (String k : keys) {
            String v = getProp(k);
            if (!isEmptySafe(v)) {
                sb.append(k).append(" = ").append(v).append("\n");
                anyProp = true;
            }
        }

        boolean anySpeed = false;
        boolean hasSuperSpeed = false;

        // Sysfs: live USB device speeds (αν υπάρχει κάτι συνδεδεμένο)
        try {
            File bus = new File("/sys/bus/usb/devices");
            if (bus.exists() && bus.isDirectory()) {
                File[] devs = bus.listFiles();
                if (devs != null) {
                    for (File d : devs) {
                        File speedFile = new File(d, "speed");
                        if (speedFile.exists()) {
                            String sp = readSmallFile(speedFile);
                            if (!isEmptySafe(sp)) {
                                sb.append("Device ")
                                        .append(d.getName())
                                        .append(" speed: ")
                                        .append(sp.trim())
                                        .append(" Mb/s\n");
                                anySpeed = true;
                                try {
                                    double val = Double.parseDouble(sp.trim());
                                    if (val > 480.0) {
                                        hasSuperSpeed = true; // πάνω από USB2.0 High-Speed
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}

        if (hasSuperSpeed) {
            sb.append("SuperSpeed link detected (> 480 Mb/s)\n");
        } else if (anySpeed) {
            sb.append("Only USB2.x speeds observed so far\n");
        }

        if (!anyProp && !anySpeed) {
            sb.append("No USB speed info\n");
        }

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
            boolean hasTelephony = false;

            for (AudioDeviceInfo d : inputs) {
                switch (d.getType()) {
                    case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                        builtIn++;
                        break;
                    case AudioDeviceInfo.TYPE_TELEPHONY:
                        builtIn++;
                        hasTelephony = true;
                        break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        bt++;
                        break;
                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET:
                        usb++;
                        break;
                    default:
                        other++;
                        break;
                }
            }

            sb.append("Total input devices: ").append(inputs.length).append("\n");
            sb.append("Built-in mics: ").append(builtIn).append("\n");
            sb.append("Bluetooth: ").append(bt).append("\n");
            sb.append("USB: ").append(usb).append("\n");
            sb.append("Other: ").append(other).append("\n");
            sb.append("Telephony-capable input: ").append(hasTelephony ? "YES" : "NO").append("\n");

        } catch (Throwable t) {
            sb.append("Mic error\n");
        }
        return sb.toString();
    }

    private String detectAudioHalLevel() {
        String[] keys = {
                "ro.vendor.audio.hal.version",
                "ro.audio.hal.version",
                "audio_hal.version",
                "ro.odm.audio.hal.version"
        };
        for (String k : keys) {
            String v = getProp(k);
            if (!isEmptySafe(v)) return "Property " + k + " = " + v;
        }

        String cfg = readAudioHalFromConfig();
        if (!isEmptySafe(cfg)) return cfg;

        return "Unknown";
    }

    private String readAudioHalFromConfig() {
        try {
            String[] paths = {
                    "/odm/etc/audio_policy_configuration.xml",
                    "/vendor/etc/audio_policy_configuration.xml",
                    "/system/etc/audio_policy_configuration.xml"
            };
            for (String p : paths) {
                File f = new File(p);
                if (!f.exists()) continue;
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    String lower = line.toLowerCase();
                    if (lower.contains("audio_hal_version")
                            || lower.contains("halversion")
                            || lower.contains("version=\"")) {
                        br.close();
                        return "From " + p + ": " + line.trim();
                    }
                }
                br.close();
            }
        } catch (Throwable ignored) {}
        return "";
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

    // ===========================
    // EXTRA HELPERS (SERVICE-PRO)
    // ===========================

    private String describeCamera2Level(int level) {
        switch (level) {
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                return "LEGACY (limited Camera2, HAL1-style)";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                return "LIMITED (partial Camera2)";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                return "FULL (full Camera2, good for manual controls)";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                return "LEVEL_3 (advanced features, YUV_420_888, RAW, etc.)";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
                return "EXTERNAL (USB / external cameras)";
            default:
                return "Unknown level";
        }
    }

    private String describeWifiBand(int freqMHz) {
        if (freqMHz >= 2400 && freqMHz < 2500) return "2.4 GHz";
        if (freqMHz >= 4900 && freqMHz < 5900) return "5 GHz";
        if (freqMHz >= 5925 && freqMHz < 7125) return "6 GHz";
        return "Unknown";
    }

    private String describeWifiPhy(int std) {
        switch (std) {
            case 1:
                return "Legacy 11a/b/g (20 MHz)";
            case 4:
                return "HT (WiFi 4, 20/40 MHz)";
            case 5:
                return "VHT (WiFi 5, 20/40/80 MHz)";
            case 6:
                return "HE (WiFi 6/6E, up to 160 MHz)";
            default:
                return "Unknown / vendor-specific";
        }
    }

    private String getVolteStatus() {
        // Common debug / vendor flags
        String[] keys = {
                "persist.dbg.volte_avail_ovr",
                "persist.dbg.vt_avail_ovr",
                "persist.dbg.ims_avail_ovr",
                "persist.vendor.dbg.volte_avail_ovr"
        };
        boolean any = false;
        boolean on = false;
        StringBuilder dbg = new StringBuilder();
        for (String k : keys) {
            String v = getProp(k);
            if (!isEmptySafe(v)) {
                any = true;
                dbg.append(k).append("=").append(v).append(" ");
                if ("1".equals(v) || "true".equalsIgnoreCase(v)) {
                    on = true;
                }
            }
        }
        if (!any) return "";
        if (on) return "Enabled (dbg flags: " + dbg.toString().trim() + ")";
        return "Present flags: " + dbg.toString().trim();
    }

    private String getVowifiStatus() {
        String[] keys = {
                "persist.dbg.wfc_avail_ovr",
                "persist.vendor.dbg.wfc_avail_ovr"
        };
        boolean any = false;
        boolean on = false;
        StringBuilder dbg = new StringBuilder();
        for (String k : keys) {
            String v = getProp(k);
            if (!isEmptySafe(v)) {
                any = true;
                dbg.append(k).append("=").append(v).append(" ");
                if ("1".equals(v) || "true".equalsIgnoreCase(v)) {
                    on = true;
                }
            }
        }
        if (!any) return "";
        if (on) return "Enabled (dbg flags: " + dbg.toString().trim() + ")";
        return "Present flags: " + dbg.toString().trim();
    }

    private String parseBtCodecs(String caps) {
        String lower = caps.toLowerCase();
        StringBuilder out = new StringBuilder();
        if (lower.contains("sbc"))  out.append("SBC, ");
        if (lower.contains("aac"))  out.append("AAC, ");
        if (lower.contains("aptx")) out.append("aptX, ");
        if (lower.contains("ldac")) out.append("LDAC, ");
        if (lower.contains("lhdc")) out.append("LHDC, ");
        if (out.length() == 0) return "";
        // remove last comma+space
        return out.substring(0, out.length() - 2);
    }
    // ===========================
// THERMAL ZONES (ULTRA DIAG)
// ===========================
private String readThermalZones() {
    StringBuilder sb = new StringBuilder();
    try {
        File base = new File("/sys/class/thermal");
        if (!base.exists() || !base.isDirectory()) {
            sb.append("Thermal sysfs: [not available]\n");
            return sb.toString();
        }

        File[] zones = base.listFiles();
        if (zones == null || zones.length == 0) {
            sb.append("No thermal zones listed\n");
            return sb.toString();
        }

        for (File z : zones) {
            if (!z.getName().startsWith("thermal_zone")) continue;

            File typeFile = new File(z, "type");
            File tempFile = new File(z, "temp");

            String type = readSmallFile(typeFile);
            String tempRaw = readSmallFile(tempFile);

            if (type == null && tempRaw == null) continue;

            sb.append(z.getName()).append(" — ");
            if (type != null) {
                sb.append(type.trim());
            } else {
                sb.append("[no type]");
            }

            if (tempRaw != null) {
                tempRaw = tempRaw.trim();
                try {
                    double val = Double.parseDouble(tempRaw);
                    // Συνήθως σε millidegrees (π.χ. 42000 = 42.0 °C)
                    if (val > 1000) {
                        val = val / 1000.0;
                    }
                    sb.append(" : ").append(String.format("%.1f", val)).append(" °C");
                } catch (Throwable ignored) {
                    sb.append(" : ").append(tempRaw);
                }
            }
            sb.append("\n");
        }
    } catch (Throwable t) {
        sb.append("Thermal read error: ").append(t.getMessage()).append("\n");
    }

    if (sb.length() == 0) {
        sb.append("No thermal zones listed\n");
    }

    return sb.toString();
}
    // =====================================
// VULKAN / GPU EXTRA (DRIVER & TAGS)
// =====================================
private String readVulkanInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        sb.append("Hardware Vulkan: ")
                .append(safe(getProp("ro.hardware.vulkan")))
                .append("\n");

        String verVendor = getProp("ro.vendor.vulkan.version");
        String verSys    = getProp("ro.vulkan.version");

        if (!isEmptySafe(verVendor) || !isEmptySafe(verSys)) {
            sb.append("Vulkan version (vendor): ")
                    .append(isEmptySafe(verVendor) ? "[n/a]" : verVendor)
                    .append("\n");
            sb.append("Vulkan version (system): ")
                    .append(isEmptySafe(verSys) ? "[n/a]" : verSys)
                    .append("\n");
        } else {
            sb.append("Vulkan version: [no props]\n");
        }

        // GPU / renderer hints
        String eglHw   = getProp("ro.hardware.egl");
        String gfxDrv0 = getProp("ro.gfx.driver.0");
        String gpuName = getProp("ro.gfx.vendor");

        if (!isEmptySafe(eglHw) || !isEmptySafe(gfxDrv0) || !isEmptySafe(gpuName)) {
            sb.append("EGL hardware: ")
                    .append(isEmptySafe(eglHw) ? "[n/a]" : eglHw)
                    .append("\n");
            sb.append("GPU driver 0: ")
                    .append(isEmptySafe(gfxDrv0) ? "[n/a]" : gfxDrv0)
                    .append("\n");
            sb.append("GPU vendor/tag: ")
                    .append(isEmptySafe(gpuName) ? "[n/a]" : gpuName)
                    .append("\n");
        }

        // Board / SoC hint
        String board = getProp("ro.board.platform");
        if (!isEmptySafe(board)) {
            sb.append("Board platform: ").append(board).append("\n");
        }

    } catch (Throwable t) {
        sb.append("Vulkan info error: ").append(t.getMessage()).append("\n");
    }

    if (sb.length() == 0) {
        sb.append("No Vulkan / GPU info\n");
    }

    return sb.toString();
}
    // ======================================
// THERMAL ENGINE PROFILES (CONFIG SCAN)
// ======================================
private String readThermalProfiles() {
    StringBuilder sb = new StringBuilder();

    try {
        // Συνήθεις διαδρομές vendor / system
        File[] roots = new File[] {
                new File("/vendor/etc"),
                new File("/system/etc")
        };

        boolean anyFile = false;

        for (File root : roots) {
            if (!root.exists() || !root.isDirectory()) continue;

            File[] files = root.listFiles();
            if (files == null) continue;

            for (File f : files) {
                String name = f.getName().toLowerCase();
                if (!name.startsWith("thermal") || !name.endsWith(".conf")) continue;

                anyFile = true;
                sb.append("File: ").append(f.getAbsolutePath()).append("\n");

                // Διαβάζουμε λίγες πρώτες “significant” γραμμές
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    int printed = 0;
                    while ((line = br.readLine()) != null && printed < 30) {
                        String lower = line.toLowerCase();
                        if (lower.contains("profile")
                                || lower.contains("trip")
                                || lower.contains("algo")
                                || lower.contains("sensor")
                                || lower.contains("cluster")
                                || lower.contains("cpu")
                                || lower.contains("gpu")) {

                            sb.append("  ").append(line.trim()).append("\n");
                            printed++;
                        }
                    }
                } catch (Throwable ignored) {
                    sb.append("  [error reading]\n");
                }

                sb.append("\n");
            }
        }

        if (!anyFile) {
            sb.append("No thermal-engine config files found\n");
        }

    } catch (Throwable t) {
        sb.append("Thermal profiles error: ").append(t.getMessage()).append("\n");
    }

    if (sb.length() == 0) {
        sb.append("No thermal profile data\n");
    }

    return sb.toString();
}
}
