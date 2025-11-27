// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL v9.0 (ROOT-AWARE ULTRA PRO)
// GEL Rule: Single, full file — ready for direct copy-paste. No manual patching.

package com.gel.cleaner;

import com.gel.cleaner.GELAutoActivityHook;

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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Size;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

    // ============================================================
    // EXPAND ENGINE — ONE-OPEN SECTION, FOLDABLE-SAFE
    // ============================================================

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

        // SAFE MEASURE FIX — works on all screens & foldables
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
    // ROOT CHECK (FULL MODE)
    // ============================================================
    private boolean isDeviceRooted() {
        try {
            // Build tags hint
            String tags = Build.TAGS;
            if (tags != null && tags.contains("test-keys")) return true;

            // su paths
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
            };
            for (String p : paths) {
                if (new File(p).exists()) return true;
            }

            // which su
            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();
            return line != null && line.trim().length() > 0;

        } catch (Throwable ignore) {
            return false;
        }
    }

    // Small helper: whenever value is empty and would normally need deeper/system access,
    // we surface explicit root-related fallback text.
    private String formatValue(String value) {
        if (value == null || value.isEmpty()) {
            return isRooted ? "N/A" : "N/A (Device is NOT rooted)";
        }
        return value;
    }

    // ============================================================
    // BUILDERS — CAMERA
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
                    Boolean flash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer orientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Size pixelArray = cc.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                    Integer hwLevel = cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

                    sb.append("Camera ID ").append(id).append(":\n");

                    // Facing
                    sb.append("• Facing       : ")
                            .append(facing == CameraCharacteristics.LENS_FACING_FRONT ? "Front" :
                                    facing == CameraCharacteristics.LENS_FACING_BACK ? "Back" :
                                            "External")
                            .append("\n");

                    // Focal + aperture
                    if (focals != null && focals.length > 0)
                        sb.append("• Focal        : ").append(focals[0]).append(" mm\n");

                    if (apertures != null && apertures.length > 0)
                        sb.append("• Aperture     : f/").append(apertures[0]).append("\n");

                    // Sensor orientation
                    if (orientation != null)
                        sb.append("• Orientation  : ").append(orientation).append("°\n");

                    // Pixel array (raw panel resolution of that sensor)
                    if (pixelArray != null)
                        sb.append("• Pixel Array  : ")
                                .append(pixelArray.getWidth())
                                .append(" x ")
                                .append(pixelArray.getHeight())
                                .append("\n");

                    // Flash
                    if (flash != null)
                        sb.append("• Flash        : ").append(flash ? "Available" : "No flash").append("\n");

                    // Hardware level
                    if (hwLevel != null) {
                        sb.append("• HW Level     : ").append(describeCameraHardwareLevel(hwLevel)).append("\n");
                    }

                    sb.append("\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No camera data.\n");
        return sb.toString();
    }

    private String describeCameraHardwareLevel(int level) {
        switch (level) {
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                return "LEGACY";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                return "LIMITED";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                return "FULL";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                return "LEVEL_3";
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL:
                return "EXTERNAL";
            default:
                return "UNKNOWN";
        }
    }

    // ============================================================
    // BIOMETRICS
    // ============================================================
    private String buildBiometricsInfo() {
        StringBuilder sb = new StringBuilder();

        boolean fp   = getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
        boolean face = getPackageManager().hasSystemFeature("android.hardware.biometrics.face");
        boolean iris = getPackageManager().hasSystemFeature("android.hardware.biometrics.iris");

        boolean strongFace = getPackageManager().hasSystemFeature("android.hardware.biometrics.face.strength");
        boolean underDisplayFp = getPackageManager().hasSystemFeature("android.hardware.fingerprint.under_display");

        sb.append("Fingerprint   : ").append(fp ? "Yes" : "No").append("\n");
        sb.append("Face Unlock   : ").append(face ? "Yes" : "No").append("\n");
        sb.append("Iris Scan     : ").append(iris ? "Yes" : "No").append("\n");
        sb.append("Strong Face   : ").append(strongFace ? "Yes" : "No").append("\n");
        sb.append("UD Fingerprint: ").append(underDisplayFp ? "Yes" : "No").append("\n");

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
                int count = 0;
                for (Sensor s : sm.getSensorList(Sensor.TYPE_ALL)) {
                    count++;
                    sb.append("• ")
                            .append(s.getName())
                            .append(" (").append(s.getVendor()).append(")\n")
                            .append("  Type        : ").append(s.getStringType()).append("\n")
                            .append("  Version     : ").append(s.getVersion()).append("\n")
                            .append("  Power       : ").append(s.getPower()).append(" mA\n")
                            .append("  Min Delay   : ").append(s.getMinDelay()).append(" µs\n\n");
                }
                sb.insert(0, "Total Sensors : " + count + "\n\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No sensors detected.\n");
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
                    sb.append("Active        : ");
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        sb.append("Wi-Fi");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                        sb.append("Cellular");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                        sb.append("Ethernet");
                    else
                        sb.append("Other");
                    sb.append("\n");

                    sb.append("Metered       : ")
                            .append(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                                    ? "No" : "Yes").append("\n");

                    sb.append("Downlink      : ")
                            .append(caps.getLinkDownstreamBandwidthKbps())
                            .append(" kbps\n");
                    sb.append("Uplink        : ")
                            .append(caps.getLinkUpstreamBandwidthKbps())
                            .append(" kbps\n");
                }
            }

            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null && wi.getNetworkId() != -1) {
                    sb.append("\nWi-Fi:\n");
                    sb.append("  SSID        : ").append(wi.getSSID()).append("\n");
                    sb.append("  Link Speed  : ").append(wi.getLinkSpeed()).append(" Mbps\n");
                    sb.append("  RSSI        : ").append(wi.getRssi()).append(" dBm\n");
                    sb.append("  Frequency   : ").append(wi.getFrequency()).append(" MHz\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No connectivity info.\n");
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

                sb.append("GPS Provider  : ").append(gps ? "Enabled" : "Disabled").append("\n");
                sb.append("Network Prov. : ").append(net ? "Enabled" : "Disabled").append("\n");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    sb.append("Location Mode : ").append(lm.isLocationEnabled() ? "ON" : "OFF").append("\n");
                }
            }

        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No location data.\n");
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

            boolean btFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
            boolean bleFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

            sb.append("Supported     : ").append(btFeature ? "Yes" : "No").append("\n");
            sb.append("BLE Support   : ").append(bleFeature ? "Yes" : "No").append("\n");

            if (ba != null) {
                sb.append("Enabled       : ").append(ba.isEnabled() ? "Yes" : "No").append("\n");
                sb.append("Name          : ").append(ba.getName()).append("\n");
                sb.append("Address       : ").append(ba.getAddress()).append("\n");
            }

        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No Bluetooth info.\n");
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

            sb.append("Supported     : ").append(a != null ? "Yes" : "No").append("\n");
            if (a != null) {
                sb.append("Enabled       : ").append(a.isEnabled() ? "Yes" : "No").append("\n");
            }

        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No NFC info.\n");
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
                int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                int voltage = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

                sb.append("Level         : ").append(level).append("%\n");
                sb.append("Scale         : ").append(scale).append("\n");
                sb.append("Status        : ").append(describeBatteryStatus(status)).append("\n");
                sb.append("Plugged       : ").append(describeBatteryPlugged(plugged)).append("\n");
                sb.append("Health        : ").append(describeBatteryHealth(health)).append("\n");

                if (voltage > 0)
                    sb.append("Voltage       : ").append(voltage).append(" mV\n");

                if (temp > 0)
                    sb.append("Temp          : ").append((temp / 10f)).append("°C\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No battery data.\n");
        return sb.toString();
    }

    private String describeBatteryStatus(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING: return "CHARGING";
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return "DISCHARGING";
            case BatteryManager.BATTERY_STATUS_FULL: return "FULL";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return "NOT CHARGING";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default: return "UNKNOWN";
        }
    }

    private String describeBatteryPlugged(int plugged) {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC: return "AC";
            case BatteryManager.BATTERY_PLUGGED_USB: return "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS: return "WIRELESS";
            default: return "UNPLUGGED";
        }
    }

    private String describeBatteryHealth(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return "GOOD";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "OVERHEAT";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "DEAD";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "OVER VOLTAGE";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "FAILURE";
            case BatteryManager.BATTERY_HEALTH_COLD: return "COLD";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default: return "UNKNOWN";
        }
    }

    // ============================================================
    // UWB
    // ============================================================
    private String buildUwbInfo() {
        boolean supported = getPackageManager().hasSystemFeature("android.hardware.uwb");
        return "Supported     : " + (supported ? "Yes" : "No") + "\n";
    }

    // ============================================================
    // HAPTICS
    // ============================================================
    private String buildHapticsInfo() {
        StringBuilder sb = new StringBuilder();

        boolean vibFeature = getPackageManager().hasSystemFeature("android.hardware.vibrator");
        sb.append("Vibrator HW   : ").append(vibFeature ? "Yes" : "No").append("\n");

        try {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean amp = vib.hasAmplitudeControl();
                sb.append("Amp Control   : ").append(amp ? "Yes" : "No").append("\n");
            }
        } catch (Throwable ignore) {}

        return sb.toString();
    }

    // ============================================================
    // GNSS
    // ============================================================
    private String buildGnssInfo() {
        boolean gnss = getPackageManager().hasSystemFeature("android.hardware.location.gnss");
        boolean gps = getPackageManager().hasSystemFeature("android.hardware.location.gps");
        boolean netLoc = getPackageManager().hasSystemFeature("android.hardware.location.network");

        StringBuilder sb = new StringBuilder();
        sb.append("GNSS Core     : ").append(gnss ? "Yes" : "No").append("\n");
        sb.append("GPS Support   : ").append(gps ? "Yes" : "No").append("\n");
        sb.append("Network Loc   : ").append(netLoc ? "Yes" : "No").append("\n");

        return sb.toString();
    }

    // ============================================================
    // USB / OTG
    // ============================================================
    private String buildUsbInfo() {
        boolean otg = getPackageManager().hasSystemFeature("android.hardware.usb.host");
        boolean accessory = getPackageManager().hasSystemFeature("android.hardware.usb.accessory");

        StringBuilder sb = new StringBuilder();
        sb.append("OTG Support   : ").append(otg ? "Yes" : "No").append("\n");
        sb.append("Accessory Mode: ").append(accessory ? "Yes" : "No").append("\n");

        return sb.toString();
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
                int count = 0;
                for (AudioDeviceInfo d : devs) {
                    count++;
                    sb.append("Mic #").append(count).append(":\n");
                    sb.append("  Name        : ").append(d.getProductName()).append("\n");
                    sb.append("  Type        : ").append(describeAudioDeviceType(d.getType())).append("\n\n");
                }
                if (count > 0) {
                    sb.insert(0, "Total Mics    : " + count + "\n\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No microphones detected.\n");
        return sb.toString();
    }

    private String describeAudioDeviceType(int type) {
        switch (type) {
            case AudioDeviceInfo.TYPE_BUILTIN_MIC: return "BUILT-IN MIC";
            case AudioDeviceInfo.TYPE_WIRED_HEADSET: return "WIRED HEADSET";
            case AudioDeviceInfo.TYPE_WIRED_HEADPHONES: return "WIRED HEADPHONES (MIC)";
            case AudioDeviceInfo.TYPE_BLUETOOTH_SCO: return "BT SCO";
            case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP: return "BT A2DP";
            case AudioDeviceInfo.TYPE_USB_DEVICE: return "USB DEVICE";
            case AudioDeviceInfo.TYPE_USB_HEADSET: return "USB HEADSET";
            default: return "OTHER";
        }
    }

    // ============================================================
    // AUDIO HAL
    // ============================================================
    private String buildAudioHalInfo() {
        String hal = getProp("ro.audio.hal.version");
        String vendorHal = getProp("ro.vendor.audio.hal.version");

        StringBuilder sb = new StringBuilder();

        if ((hal == null || hal.isEmpty()) && (vendorHal == null || vendorHal.isEmpty())) {
            String v = formatValue("");
            sb.append("Audio HAL     : ").append(v).append("\n");
        } else {
            if (hal != null && !hal.isEmpty())
                sb.append("Audio HAL     : ").append(hal).append("\n");
            if (vendorHal != null && !vendorHal.isEmpty())
                sb.append("Vendor HAL    : ").append(vendorHal).append("\n");
        }

        return sb.toString();
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Throwable ignore) {
            // If system blocks us from reading deep props, expose root fallback text.
            return isRooted ? "" : "N/A (Device is NOT rooted)";
        }
    }

    // ============================================================
    // SET TEXT FOR ALL SECTIONS
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

        TextView root = findViewById(R.id.txtRootContent);
        if (root != null) {
            root.setText(isRooted ? "ROOT STATUS : YES" : "DEVICE IS NOT ROOTED");
        }

        TextView other = findViewById(R.id.txtOtherPeripheralsContent);
        if (other != null) {
            boolean vib = getPackageManager().hasSystemFeature("android.hardware.vibrator");
            other.setText("Vibration Motor : " + (vib ? "Yes" : "No"));
        }
    }

    private void set(int id, String txt) {
        TextView t = findViewById(id);
        if (t != null) t.setText(txt);
    }
}
