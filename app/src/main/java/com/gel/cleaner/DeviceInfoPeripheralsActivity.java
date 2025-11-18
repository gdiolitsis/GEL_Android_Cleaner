package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
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
import java.util.List;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    private boolean isRooted = false;

    // για το "άνοιξε ένα-ένα"
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
        TextView txtCameraContent       = findViewById(R.id.txtCameraContent);
        TextView txtBiometricsContent   = findViewById(R.id.txtBiometricsContent);
        TextView txtSensorsContent      = findViewById(R.id.txtSensorsContent);
        TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent);
        TextView txtLocationContent     = findViewById(R.id.txtLocationContent);
        TextView txtOtherContent        = findViewById(R.id.txtOtherContent);
        TextView txtRootContent         = findViewById(R.id.txtRootContent);

        TextView iconCamera       = findViewById(R.id.iconCameraToggle);
        TextView iconBiometrics   = findViewById(R.id.iconBiometricsToggle);
        TextView iconSensors      = findViewById(R.id.iconSensorsToggle);
        TextView iconConnectivity = findViewById(R.id.iconConnectivityToggle);
        TextView iconLocation     = findViewById(R.id.iconLocationToggle);
        TextView iconOther        = findViewById(R.id.iconOtherToggle);
        TextView iconRoot         = findViewById(R.id.iconRootToggle);

        allContents = new TextView[]{
                txtCameraContent,
                txtBiometricsContent,
                txtSensorsContent,
                txtConnectivityContent,
                txtLocationContent,
                txtOtherContent,
                txtRootContent
        };

        allIcons = new TextView[]{
                iconCamera,
                iconBiometrics,
                iconSensors,
                iconConnectivity,
                iconLocation,
                iconOther,
                iconRoot
        };

        // Γεμίζουμε ΟΛΑ τα expandable με πραγματικά δεδομένα
        PacketManagerData(
                txtCameraContent,
                txtBiometricsContent,
                txtSensorsContent,
                txtConnectivityContent,
                txtLocationContent,
                txtOtherContent,
                txtRootContent
        );

        // expand/collapse (ένα-ένα)
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

        // =====================================================
        // CAMERA (αναλυτικό)
        // =====================================================
        sb = new StringBuilder();
        sb.append("── CAMERA ──\n");
        sb.append("Any camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO")
                .append("\n");
        sb.append("Back camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ? "YES" : "NO")
                .append("\n");
        sb.append("Front camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? "YES" : "NO")
                .append("\n");
        sb.append("External camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL) ? "YES" : "NO")
                .append("\n");
        sb.append("Flash: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? "YES" : "NO")
                .append("\n");
        sb.append("Autofocus: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS) ? "YES" : "NO")
                .append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    sb.append("Manual sensor controls: ")
            .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR) ? "YES" : "NO")
            .append("\n");
    sb.append("RAW capture: ")
            .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_RAW) ? "YES" : "NO")
            .append("\n");
    sb.append("Depth output: ")
            .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_CAPABILITY_DEPTH_OUTPUT) ? "YES" : "NO")
            .append("\n");
}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sb.append("Level 3 / Advanced (approx): ")
                    .append(pm.hasSystemFeature("android.hardware.camera.level.full") ? "YES" : "NO")
                    .append("\n");
        }

        txtCameraContent.setText(sb.toString());

        // =====================================================
        // BIOMETRICS (αναλυτικό)
        // =====================================================
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean hasIris =
                    pm.hasSystemFeature("android.hardware.biometrics.iris");
            sb.append("Iris recognition: ").append(hasIris ? "YES" : "NO").append("\n");
        }

        sb.append("\nBiometrics summary:\n");
        sb.append("• Strong biometric: ");
        if (hasFingerprint) {
            sb.append("Fingerprint\n");
        } else {
            sb.append("Unknown / Not available\n");
        }

        txtBiometricsContent.setText(sb.toString());

        // =====================================================
        // SENSORS (full list + basic summary)
        // =====================================================
        sb = new StringBuilder();
        sb.append("── SENSORS ──\n");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors != null && !sensors.isEmpty()) {
                sb.append("Total sensors: ").append(sensors.size()).append("\n\n");

                int accel = 0, gyro = 0, mag = 0, light = 0, prox = 0, step = 0;
                for (Sensor sensor : sensors) {
                    int type = sensor.getType();
                    if (type == Sensor.TYPE_ACCELEROMETER) accel++;
                    if (type == Sensor.TYPE_GYROSCOPE) gyro++;
                    if (type == Sensor.TYPE_MAGNETIC_FIELD) mag++;
                    if (type == Sensor.TYPE_LIGHT) light++;
                    if (type == Sensor.TYPE_PROXIMITY) prox++;
                    if (type == Sensor.TYPE_STEP_COUNTER) step++;

                    sb.append("• ")
                            .append(sensor.getName())
                            .append(" (type=")
                            .append(type)
                            .append(")\n   Vendor: ")
                            .append(sensor.getVendor())
                            .append("\n   Max range: ")
                            .append(sensor.getMaximumRange())
                            .append("\n   Resolution: ")
                            .append(sensor.getResolution())
                            .append("\n   Power: ")
                            .append(sensor.getPower())
                            .append(" mA\n\n");
                }

                sb.append("Summary:\n");
                sb.append("Accelerometer: ").append(accel > 0 ? "YES" : "NO").append("\n");
                sb.append("Gyroscope: ").append(gyro > 0 ? "YES" : "NO").append("\n");
                sb.append("Magnetometer: ").append(mag > 0 ? "YES" : "NO").append("\n");
                sb.append("Light sensor: ").append(light > 0 ? "YES" : "NO").append("\n");
                sb.append("Proximity sensor: ").append(prox > 0 ? "YES" : "NO").append("\n");
                sb.append("Step counter: ").append(step > 0 ? "YES" : "NO").append("\n");
            } else {
                sb.append("No sensors reported\n");
            }
        } else {
            sb.append("SensorManager not available\n");
        }
        txtSensorsContent.setText(sb.toString());

        // =====================================================
        // CONNECTIVITY (πιο βαθιά)
        // =====================================================
        sb = new StringBuilder();
        sb.append("── CONNECTIVITY ──\n");
        sb.append("NFC: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO")
                .append("\n");
        sb.append("NFC - HCE: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION) ? "YES" : "NO")
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
        sb.append("Wi-Fi Direct: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT) ? "YES" : "NO")
                .append("\n");
        sb.append("5G: ")
                .append(pm.hasSystemFeature("android.hardware.telephony.5g") ? "YES" : "NO")
                .append("\n");
        sb.append("Telephony: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ? "YES" : "NO")
                .append("\n");
        sb.append("Telephony CDMA: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA) ? "YES" : "NO")
                .append("\n");
        sb.append("Telephony GSM: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM) ? "YES" : "NO")
                .append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sb.append("Ultra Wideband (UWB): ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_UWB) ? "YES" : "NO")
                    .append("\n");
        }

        // προαιρετικά ethernet / wifi aware αν υπάρχουν
        sb.append("Ethernet (hardware): ")
                .append(pm.hasSystemFeature("android.hardware.ethernet") ? "YES" : "NO")
                .append("\n");
        sb.append("Wi-Fi Aware: ")
                .append(pm.hasSystemFeature("android.hardware.wifi.aware") ? "YES" : "NO")
                .append("\n");

        txtConnectivityContent.setText(sb.toString());

        // =====================================================
        // LOCATION (providers & δυνατότητες)
        // =====================================================
        sb = new StringBuilder();
        sb.append("── LOCATION ──\n");
        sb.append("GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                .append("\n");
        sb.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO")
                .append("\n");
        sb.append("Fused provider (Play Services based): POSSIBLE\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sb.append("GNSS: ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                    .append(" (GNSS stack on newer devices)\n");
        }
        sb.append("\nNote: Detailed satellites / accuracy require location permission and are not shown here.\n");
        txtLocationContent.setText(sb.toString());

        // =====================================================
        // OTHER PERIPHERALS (IO, Audio, Thermal κ.λπ.)
        // =====================================================
        sb = new StringBuilder();
        sb.append("── OTHER PERIPHERALS ──\n");
        sb.append("USB host: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO")
                .append("\n");
        sb.append("USB accessory: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY) ? "YES" : "NO")
                .append("\n");

        boolean hasMic = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        sb.append("Microphone: ").append(hasMic ? "YES" : "NO").append("\n");

        sb.append("Audio output: ")
                .append(pm.hasSystemFeature("android.hardware.audio.output") ? "YES" : "NO")
                .append("\n");
        sb.append("Low-latency audio: ")
                .append(pm.hasSystemFeature("android.hardware.audio.low_latency") ? "YES" : "NO")
                .append("\n");
        sb.append("Pro audio: ")
                .append(pm.hasSystemFeature("android.hardware.audio.pro") ? "YES" : "NO")
                .append("\n");

        // Vibrator
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        boolean hasVib = vib != null && vib.hasVibrator();
        sb.append("Vibrator: ").append(hasVib ? "YES" : "NO").append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vib != null) {
            sb.append("Amplitude control: ")
                    .append(vib.hasAmplitudeControl() ? "YES" : "NO")
                    .append("\n");
        }

        // Advanced hardware capabilities
        sb.append("\n── ADVANCED HARDWARE ──\n");
        sb.append("OpenGL ES AEP (3.x+ features): ")
                .append(pm.hasSystemFeature("android.hardware.opengles.aep") ? "YES" : "NO/Unknown")
                .append("\n");
        sb.append("Vulkan support: ")
                .append(pm.hasSystemFeature("android.hardware.vulkan.level") ? "YES" : "NO/Unknown")
                .append("\n");
        sb.append("Neural Networks API: ")
                .append(pm.hasSystemFeature("android.hardware.neuralnetworks") ? "YES" : "NO")
                .append("\n");

        // Thermal / Temperature
        sb.append("\n── THERMAL / TEMPERATURE (best effort) ──\n");
        float battC = getBatteryTemperatureC();
        if (!Float.isNaN(battC)) {
            sb.append("Battery: ")
                    .append(String.format("%.1f °C", battC))
                    .append("\n");
        } else {
            sb.append("Battery: N/A\n");
        }
        sb.append(getThermalSummary());

        // Audio codecs summary
        sb.append("\n── AUDIO CODECS (summary) ──\n");
        sb.append(getAudioCodecSummary());

        txtOtherContent.setText(sb.toString());

        // =====================================================
        // ROOT EXTRAS (μόνο αν rooted)
        // =====================================================
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

    // ========== expand / collapse (ένα ανοιχτό κάθε φορά) ==========
    private void setupSection(View header, final TextView content, final TextView icon) {
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView contentToOpen, TextView iconToUpdate) {
        if (allContents == null || allIcons == null) return;

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == null || ic == null) continue;

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

    // ========== BATTERY / THERMAL HELPERS ==========

    private float getBatteryTemperatureC() {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);
            if (batteryStatus == null) return Float.NaN;
            int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
            if (temp == Integer.MIN_VALUE) return Float.NaN;
            return temp / 10f;
        } catch (Throwable t) {
            return Float.NaN;
        }
    }

    private String getThermalSummary() {
        StringBuilder out = new StringBuilder();
        try {
            File dir = new File("/sys/class/thermal");
            if (!dir.exists() || !dir.isDirectory()) {
                out.append("Thermal info: /sys/class/thermal not available.\n");
                return out.toString();
            }
            File[] zones = dir.listFiles();
            if (zones == null || zones.length == 0) {
                out.append("Thermal info: no thermal zones found.\n");
                return out.toString();
            }
            int count = 0;
            for (File z : zones) {
                if (!z.getName().startsWith("thermal_zone")) continue;
                File typeFile = new File(z, "type");
                File tempFile = new File(z, "temp");
                String type = readFirstLine(typeFile);
                String tempStr = readFirstLine(tempFile);
                if (tempStr == null) continue;

                try {
                    tempStr = tempStr.trim();
                    if (tempStr.isEmpty()) continue;
                    float cVal = Float.parseFloat(tempStr);
                    if (cVal > 1000f) cVal = cVal / 1000f;
                    out.append("• ")
                            .append(type != null ? type : z.getName())
                            .append(": ")
                            .append(String.format("%.1f °C", cVal))
                            .append("\n");
                    count++;
                    if (count >= 6) break; // περιορίζουμε λίγο για να μην γίνει τεράστιο
                } catch (NumberFormatException ignored) {
                }
            }
            if (count == 0) {
                out.append("Thermal zones present but temperatures not readable.\n");
            }
        } catch (Throwable t) {
            out.append("Thermal info error: ").append(t.getMessage()).append("\n");
        }
        return out.toString();
    }

    private String readFirstLine(File f) {
        if (f == null || !f.exists()) return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            return br.readLine();
        } catch (Exception e) {
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    // ========== AUDIO CODEC SUMMARY ==========
    private String getAudioCodecSummary() {
        StringBuilder out = new StringBuilder();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MediaCodecList mcl = new MediaCodecList(MediaCodecList.ALL_CODECS);
                MediaCodecInfo[] codecs = mcl.getCodecInfos();
                int decoders = 0;
                int encoders = 0;
                int listed = 0;

                out.append("Audio decoders (sample):\n");
                for (MediaCodecInfo info : codecs) {
                    String[] types = info.getSupportedTypes();
                    boolean isAudio = false;
                    if (types != null) {
                        for (String t : types) {
                            if (t != null && t.startsWith("audio/")) {
                                isAudio = true;
                                break;
                            }
                        }
                    }
                    if (!isAudio) continue;

                    if (info.isEncoder()) {
                        encoders++;
                    } else {
                        decoders++;
                        if (listed < 6) {
                            out.append("• ").append(info.getName()).append("\n");
                            listed++;
                        }
                    }
                }
                out.append("Total audio decoders: ").append(decoders).append("\n");
                out.append("Total audio encoders: ").append(encoders).append("\n");
            } else {
                out.append("Audio codec list: N/A (API < 21)\n");
            }
        } catch (Throwable t) {
            out.append("Audio codec info error: ").append(t.getMessage()).append("\n");
        }
        return out.toString();
    }
}
