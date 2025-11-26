// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL v8.0 (API-SAFE)

package com.gel.cleaner;

import com.gel.cleaner.GELAutoActivityHook;

import android.Manifest;
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

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == null || ic == null) continue;
            if (c == toOpen) continue;

            animateCollapse(c);
            ic.setText("＋");
        }

        boolean visible = (toOpen.getVisibility() == View.VISIBLE);

        if (visible) {
            animateCollapse(toOpen);
            iconToUpdate.setText("＋");
        } else {
            animateExpand(toOpen);
            iconToUpdate.setText("−");
        }
    }

    private void animateExpand(final View v) {
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final int target = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);

        v.animate()
                .alpha(1f)
                .setDuration(160)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> v.getLayoutParams().height = target)
                .start();
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        final int initial = v.getMeasuredHeight();
        v.setAlpha(1f);

        v.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = initial;
                    v.setAlpha(1f);
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

                    sb.append("Camera ID ").append(id).append(":\n");
                    sb.append("• Facing: ")
                            .append(facing == CameraCharacteristics.LENS_FACING_FRONT ? "Front" :
                                    facing == CameraCharacteristics.LENS_FACING_BACK ? "Back" :
                                            "External").append("\n");

                    if (focals != null && focals.length > 0)
                        sb.append("• Focal: ").append(focals[0]).append(" mm\n");

                    if (apertures != null && apertures.length > 0)
                        sb.append("• Aperture: f/").append(apertures[0]).append("\n");

                    sb.append("\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) sb.append("No camera data.\n");
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
                    sb.append("Active: ");
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        sb.append("Wi-Fi\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                        sb.append("Cellular\n");
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

            boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            sb.append("GPS     : ").append(gps ? "Enabled" : "Disabled").append("\n");
            sb.append("Network : ").append(net ? "Enabled" : "Disabled").append("\n");

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

            if (ba != null) {
                sb.append("Supported : Yes\n");
                sb.append("Enabled   : ").append(ba.isEnabled() ? "Yes" : "No").append("\n");
                sb.append("Name      : ").append(ba.getName()).append("\n");
                sb.append("Address   : ").append(ba.getAddress()).append("\n");
            } else {
                sb.append("Supported : No\n");
            }

        } catch (Throwable ignore) {}

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

        if (sb.length() == 0) sb.append("No battery data.\n");
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

        if (sb.length() == 0) sb.append("No microphones detected.\n");
        return sb.toString();
    }

    // ============================================================
    // AUDIO HAL
    // ============================================================
    private String buildAudioHalInfo() {
        return "Audio HAL : " + getProp("ro.audio.hal.version") + "\n";
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
