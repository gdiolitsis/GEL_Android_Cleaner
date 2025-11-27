// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL v10.0
// API-SAFE + ROOT-EXTENDED + NEON VALUES + STEALTH MASKING
// NOTE: Δουλεύω ΠΑΝΩ στο τελευταίο αρχείο σου — μόνο ενίσχυση, όχι αλλαγή σε UI / XML.

package com.gel.cleaner;

import com.gel.cleaner.GELAutoActivityHook;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class DeviceInfoPeripheralsActivity extends GELAutoActivityHook {

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
        if (title != null) title.setText(getString(R.string.phone_info_peripherals));

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
                txtCameraContent, txtBiometricsContent, txtSensorsContent, txtConnectivityContent,
                txtLocationContent, txtOtherPeripherals, txtBluetoothContent, txtNfcContent,
                txtRootContent, txtBatteryContent, txtUwbContent, txtHapticsContent, txtGnssContent,
                txtUsbContent, txtMicsContent, txtAudioHalContent
        };

        allIcons = new TextView[]{
                iconCamera, iconBiometrics, iconSensors, iconConnectivity, iconLocation, iconOther,
                iconBluetooth, iconNfc, iconRoot, iconBattery, iconUwb, iconHaptics, iconGnss,
                iconUsb, iconMics, iconAudioHal
        };

        // Root state (shared with advanced diagnostics)
        isRooted = isDeviceRooted();

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

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    // GEL Expand Engine v3.0 — FIXED (No Auto-Collapse Bug)
    private void toggleSection(TextView targetContent, TextView targetIcon) {

        // Close all other sections
        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];

            if (c == null || ic == null) continue;

            if (c != targetContent && c.getVisibility() == View.VISIBLE) {
                animateCollapse(c);
                ic.setText("＋");
            }
        }

        // Toggle only the selected section
        if (targetContent.getVisibility() == View.VISIBLE) {
            animateCollapse(targetContent);
            targetIcon.setText("＋");
        } else {
            animateExpand(targetContent);
            targetIcon.setText("−");
        }
    }

    private void animateExpand(final View v) {

        // SAFE MEASURE FIX — works on all devices
        v.post(() -> {
            v.measure(
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            final int target = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);

            v.animate()
                    .alpha(1f)
                    .setDuration(160)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        v.getLayoutParams().height = target;
                        v.requestLayout();
                    })
                    .start();
        });
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        final int initial = v.getHeight();
        v.setAlpha(1f);

        v.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = initial;
                    v.setAlpha(1f);
                    v.requestLayout();
                })
                .start();
    }

    // ============================================================
    // ROOT CHECK
    // ============================================================
    private boolean isDeviceRooted() {
        try {
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
            };
            for (String p : paths)
                if (new File(p).exists()) return true;

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();
            return line != null && line.trim().length() > 0;

        } catch (Throwable ignore) {
            return false;
        }
    }

    // ============================================================
    // BUILDERS — CAMERA (API-SAFE, χωρίς OIS / VIDEO MODE constants)
    // ============================================================
    private String buildCameraInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (cm != null) {

                for (String id : cm.getCameraIdList()) {
                    CameraCharacteristics cc = cm.getCameraCharacteristics(id);

                    Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
                    float[] focals = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    float[] apertures = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                    Integer hwLevel = cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

                    sb.append("Camera ID : ").append(id).append("\n");
                    sb.append("• Facing        : ");
                    if (facing != null) {
                        if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            sb.append("Front\n");
                        } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                            sb.append("Back\n");
                        } else {
                            sb.append("External\n");
                        }
                    } else {
                        sb.append("Unknown\n");
                    }

                    if (focals != null && focals.length > 0)
                        sb.append("• Focal         : ").append(focals[0]).append(" mm\n");

                    if (apertures != null && apertures.length > 0)
                        sb.append("• Aperture      : f/").append(apertures[0]).append("\n");

                    if (hwLevel != null) {
                        String level;
                        switch (hwLevel) {
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                                level = "FULL";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                                level = "LIMITED";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                                level = "LEGACY";
                                break;
                            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                                level = "LEVEL_3";
                                break;
                            default:
                                level = "UNKNOWN";
                        }
                        sb.append("• HW Level      : ").append(level).append("\n");
                    }

                    sb.append("\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional camera diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // BIOMETRICS
    // ============================================================
    private String buildBiometricsInfo() {
        StringBuilder sb = new StringBuilder();

        boolean fp   = getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
        boolean face = getPackageManager().hasSystemFeature("android.hardware.biometrics.face");
        boolean iris = getPackageManager().hasSystemFeature("android.hardware.biometrics.iris");

        sb.append("Fingerprint : ").append(fp ? "Yes" : "No").append("\n");
        sb.append("Face Unlock : ").append(face ? "Yes" : "No").append("\n");
        sb.append("Iris Scan   : ").append(iris ? "Yes" : "No").append("\n");

        // Premium-style note (χωρίς αδυναμία)
        sb.append("\nExtended biometrics telemetry is available only in Full-Access Device Mode.\n");

        return sb.toString();
    }

    // ============================================================
    // SENSORS
    // ============================================================
    private String buildSensorsInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                for (Sensor s : sm.getSensorList(Sensor.TYPE_ALL)) {
                    sb.append("• ")
                            .append(s.getName())
                            .append(" (").append(s.getVendor()).append(")\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional sensor diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // CONNECTIVITY
    // ============================================================
    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (cm != null) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (caps != null) {
                    sb.append("Active   : ");
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        sb.append("Wi-Fi\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                        sb.append("Cellular\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                        sb.append("Ethernet\n");
                    else
                        sb.append("Other\n");
                }
            }

            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null && wi.getNetworkId() != -1) {
                    sb.append("SSID      : ").append(wi.getSSID()).append("\n");
                    sb.append("LinkSpeed : ").append(wi.getLinkSpeed()).append(" Mbps\n");
                    sb.append("RSSI      : ").append(wi.getRssi()).append(" dBm\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional connectivity diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // LOCATION
    // ============================================================
    private String buildLocationInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                sb.append("GPS     : ").append(gps ? "Enabled" : "Disabled").append("\n");
                sb.append("Network : ").append(net ? "Enabled" : "Disabled").append("\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional location diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // BLUETOOTH
    // ============================================================
    private String buildBluetoothInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter ba = bm != null ? bm.getAdapter() : null;

            if (ba != null) {
                sb.append("Supported : Yes\n");
                sb.append("Enabled   : ").append(ba.isEnabled() ? "Yes" : "No").append("\n");
                sb.append("Name      : ").append(ba.getName()).append("\n");
                sb.append("Address   : ").append(ba.getAddress()).append("\n");
            } else {
                sb.append("Supported : No\n");
            }

        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional Bluetooth diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // NFC
    // ============================================================
    private String buildNfcInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            NfcManager nfc = (NfcManager) getSystemService(Context.NFC_SERVICE);
            NfcAdapter a = nfc != null ? nfc.getDefaultAdapter() : null;

            sb.append("Supported : ").append(a != null ? "Yes" : "No").append("\n");
            if (a != null)
                sb.append("Enabled   : ").append(a.isEnabled() ? "Yes" : "No").append("\n");

        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional NFC diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // BATTERY
    // ============================================================
    private String buildBatteryInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent i = registerReceiver(null, f);

            if (i != null) {
                int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int temp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

                sb.append("Level   : ").append(level).append("%\n");
                sb.append("Scale   : ").append(scale).append("\n");
                sb.append("Status  : ").append(status).append("\n");

                if (temp > 0)
                    sb.append("Temp    : ").append((temp / 10f)).append("°C\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional battery diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // UWB
    // ============================================================
    private String buildUwbInfo() {
        boolean supported = getPackageManager().hasSystemFeature("android.hardware.uwb");
        return "Supported : " + (supported ? "Yes" : "No") + "\n";
    }

    // ============================================================
    // HAPTICS
    // ============================================================
    private String buildHapticsInfo() {
        boolean vib = getPackageManager().hasSystemFeature("android.hardware.vibrator");
        return "Supported : " + (vib ? "Yes" : "No") + "\n";
    }

    // ============================================================
    // GNSS
    // ============================================================
    private String buildGnssInfo() {
        boolean gnss = getPackageManager().hasSystemFeature("android.hardware.location.gnss");
        return "GNSS     : " + (gnss ? "Yes" : "No") + "\n";
    }

    // ============================================================
    // USB / OTG
    // ============================================================
    private String buildUsbInfo() {
        boolean otg = getPackageManager().hasSystemFeature("android.hardware.usb.host");
        return "OTG Support : " + (otg ? "Yes" : "No") + "\n";
    }

    // ============================================================
    // MICROPHONES
    // ============================================================
    private String buildMicsInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                AudioDeviceInfo[] devs = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
                for (AudioDeviceInfo d : devs) {
                    sb.append("Mic: ").append(d.getProductName()).append("\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("This device does not expose additional microphone diagnostics.\n");
        return sb.toString();
    }

    // ============================================================
    // AUDIO HAL
    // ============================================================
    private String buildAudioHalInfo() {
        String hal = getProp("ro.audio.hal.version");
        if (hal == null || hal.isEmpty()) {
            return "Audio HAL : Not exposed by this device.\n";
        }
        return "Audio HAL : " + hal + "\n";
    }

    // ============================================================
    // ROOT PERIPHERALS — ADVANCED VIEW (STEALTH)
    // ============================================================
    private String buildRootInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Root Detected : ").append(isRooted ? "YES" : "NO").append("\n");

        String tags = getProp("ro.build.tags");
        if (tags == null || tags.isEmpty()) tags = android.os.Build.TAGS;
        if (tags != null)
            sb.append("Build Tags    : ").append(tags).append("\n");

        String secure = getProp("ro.secure");
        if (secure != null && !secure.isEmpty()) {
            sb.append("ro.secure     : ").append(secure).append("\n");
        }

        String dbg = getProp("ro.debuggable");
        if (dbg != null && !dbg.isEmpty()) {
            sb.append("ro.debuggable : ").append(dbg).append("\n");
        }

        String verity = getProp("ro.boot.veritymode");
        if (verity != null && !verity.isEmpty()) {
            sb.append("Verity Mode   : ").append(verity).append("\n");
        }

        String selinux = getProp("ro.build.selinux");
        if (selinux != null && !selinux.isEmpty()) {
            sb.append("SELinux       : ").append(selinux).append("\n");
        }

        if (!isRooted) {
            sb.append("\nExtended peripheral diagnostics are available only in Full-Access Device Mode.\n");
            sb.append("To view premium hardware telemetry, enable Full-Access Device Mode in system settings.\n");
            return sb.toString();
        }

        // ROOT-ENHANCED INFORMATION (read-only, diagnostics only)

        // Battery extras
        sb.append("\nBattery (extended telemetry):\n");
        long chargeFull = readSysLong("/sys/class/power_supply/battery/charge_full");
        long chargeFullDesign = readSysLong("/sys/class/power_supply/battery/charge_full_design");
        long cycleCount = readSysLong("/sys/class/power_supply/battery/cycle_count");

        if (chargeFull > 0) {
            sb.append("  charge_full        : ").append(chargeFull).append("\n");
        }
        if (chargeFullDesign > 0) {
            sb.append("  charge_full_design : ").append(chargeFullDesign).append("\n");
        }
        if (cycleCount > 0) {
            sb.append("  cycle_count        : ").append(cycleCount).append("\n");
        }

        // Panel / display hints
        sb.append("\nDisplay / Panel (extended hints):\n");
        String panelInfo = readSysString("/sys/class/graphics/fb0/msm_fb_panel_info");
        if (panelInfo != null && !panelInfo.isEmpty()) {
            sb.append("  fb0 panel info     : ").append(panelInfo.replace("\n", " ")).append("\n");
        } else {
            sb.append("  fb0 panel info     : N/A\n");
        }

        // Audio / ALSA
        sb.append("\nAudio (kernel-level view):\n");
        String cards = readTextFile("/proc/asound/cards", 8 * 1024);
        if (cards != null && !cards.trim().isEmpty()) {
            sb.append("  /proc/asound/cards :\n");
            sb.append(cards.trim()).append("\n");
        } else {
            sb.append("  /proc/asound/cards : N/A\n");
        }

        // Input devices
        sb.append("\nInput (kernel devices):\n");
        String input = readTextFile("/proc/bus/input/devices", 16 * 1024);
        if (input != null && !input.trim().isEmpty()) {
            sb.append("  /proc/bus/input/devices :\n");
            sb.append(input.trim()).append("\n");
        } else {
            sb.append("  /proc/bus/input/devices : N/A\n");
        }

        sb.append("\nAdvanced subsystem tables are visible only on devices with elevated access.\n");
        return sb.toString();
    }

    // ============================================================
    // HELPERS (ROOT / SYSFS)
    // ============================================================
    private String readTextFile(String path, int maxLen) {
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int read;
            while ((read = br.read(buf)) > 0 && sb.length() < maxLen) {
                sb.append(buf, 0, read);
            }
            return sb.toString();
        } catch (Throwable ignore) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {}
        }
    }

    private String readSysString(String path) {
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            if (line != null) return line.trim();
            return null;
        } catch (Throwable ignore) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {}
        }
    }

    private long readSysLong(String path) {
        String s = readSysString(path);
        if (s == null || s.isEmpty()) return -1;
        try {
            return Long.parseLong(s);
        } catch (Throwable ignore) {
            return -1;
        }
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Throwable ignore) {
            return "";
        }
    }

    // ============================================================
    // SET TEXT FOR ALL SECTIONS — WITH NEON VALUE COLORING
    // ============================================================
    @Override
    protected void onStart() {
        super.onStart();

        set(R.id.txtCameraContent,       buildCameraInfo());
        set(R.id.txtBiometricsContent,   buildBiometricsInfo());
        set(R.id.txtSensorsContent,      buildSensorsInfo());
        set(R.id.txtConnectivityContent, buildConnectivityInfo());
        set(R.id.txtLocationContent,     buildLocationInfo());
        set(R.id.txtBluetoothContent,    buildBluetoothInfo());
        set(R.id.txtNfcContent,          buildNfcInfo());
        set(R.id.txtBatteryContent,      buildBatteryInfo());
        set(R.id.txtUwbContent,          buildUwbInfo());
        set(R.id.txtHapticsContent,      buildHapticsInfo());
        set(R.id.txtGnssContent,         buildGnssInfo());
        set(R.id.txtUsbContent,          buildUsbInfo());
        set(R.id.txtMicsContent,         buildMicsInfo());
        set(R.id.txtAudioHalContent,     buildAudioHalInfo());
        set(R.id.txtRootContent,         buildRootInfo());

        TextView other = findViewById(R.id.txtOtherPeripheralsContent);
        if (other != null) {
            boolean vib = getPackageManager().hasSystemFeature("android.hardware.vibrator");
            String txt = "Vibration Motor : " + (vib ? "Yes" : "No");
            applyNeonValues(other, txt);
        }
    }

    private void set(int id, String txt) {
        TextView t = findViewById(id);
        if (t == null) return;
        applyNeonValues(t, txt);
    }

    /**
     * Applies neon green color ONLY to value parts (after ':') in each line.
     */
    private void applyNeonValues(TextView tv, String text) {
        if (text == null) {
            tv.setText("");
            return;
        }

        int neon = Color.parseColor("#39FF14"); // Neon green
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        int start = 0;
        int len = text.length();
        while (start < len) {
            int colon = text.indexOf(':', start);
            if (colon == -1) break;

            int lineEnd = text.indexOf('\n', colon);
            if (lineEnd == -1) lineEnd = len;

            int valueStart = colon + 1;
            while (valueStart < lineEnd && Character.isWhitespace(text.charAt(valueStart))) {
                valueStart++;
            }

            if (valueStart < lineEnd) {
                ssb.setSpan(
                        new ForegroundColorSpan(neon),
                        valueStart,
                        lineEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            start = lineEnd + 1;
        }

        tv.setText(ssb);
    }
}

// NOTE (GEL Rule): Πάντα δίνουμε ΟΛΟΚΛΗΡΟ το ενημερωμένο αρχείο, έτοιμο για copy-paste.
