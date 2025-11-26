// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL FIXED v7.4.1

package com.gel.cleaner;

import com.gel.cleaner.base.*;

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
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileReader;

public class DeviceInfoPeripheralsActivity extends GELAutoActivityHook {

    private boolean isRooted = false;

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager foldUI;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    private TextView[] allContents;
    private TextView[] allIcons;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_device_info_peripherals);

        GELAutoDP.init(this);

        foldUI       = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) {
            title.setText(getString(R.string.phone_info_peripherals));
            title.setTextSize(sp(20f));
        }

        // ================================
        // FIND CONTENT AREAS
        // ================================
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

        // EXPANDERS
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

    @Override
    protected void onStart() {
        super.onStart();

        setText(R.id.txtCameraContent, buildCameraInfo());
        setText(R.id.txtBiometricsContent, buildBiometricsInfo());
        setText(R.id.txtSensorsContent, buildSensorsInfo());
        setText(R.id.txtConnectivityContent, buildConnectivityInfo());
        setText(R.id.txtLocationContent, buildLocationInfo());
        setText(R.id.txtBluetoothContent, buildBluetoothInfo());
        setText(R.id.txtNfcContent, buildNfcInfo());
        setText(R.id.txtBatteryContent, buildBatteryInfo());
        setText(R.id.txtUwbContent, buildUwbInfo());
        setText(R.id.txtHapticsContent, buildHapticsInfo());
        setText(R.id.txtGnssContent, buildGnssInfo());
        setText(R.id.txtUsbContent, buildUsbInfo());
        setText(R.id.txtMicsContent, buildMicsInfo());
        setText(R.id.txtAudioHalContent, buildAudioHalInfo());

        setText(R.id.txtRootContent,
                isRooted ? "Root Detected: YES" : "Device is NOT rooted");

        setText(R.id.txtOtherPeripheralsContent,
                "Vibration Motor : " +
                        yes("android.hardware.vibrator"));
    }

    // SMALL helper
    private void setText(int id, String txt) {
        TextView t = findViewById(id);
        if (t != null) t.setText(txt);
    }

    // BUILDERS ============================

    private String yes(String feature) {
        return getPackageManager().hasSystemFeature(feature) ? "Yes" : "No";
    }

    private String buildCameraInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (cm != null) {
                for (String id : cm.getCameraIdList()) {
                    CameraCharacteristics cc = cm.getCameraCharacteristics(id);

                    sb.append("Camera ").append(id).append(":\n");

                    Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null) {
                        sb.append("  Facing: ")
                                .append(facing == CameraCharacteristics.LENS_FACING_BACK ? "Back" :
                                        facing == CameraCharacteristics.LENS_FACING_FRONT ? "Front" :
                                                "External")
                                .append("\n");
                    }

                    float[] apertures = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                    if (apertures != null && apertures.length > 0)
                        sb.append("  Aperture: f/").append(apertures[0]).append("\n");

                    sb.append("\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No camera data.\n";
        return sb.toString();
    }

    private String buildBiometricsInfo() {
        return  "Fingerprint: " + yes(PackageManager.FEATURE_FINGERPRINT) + "\n" +
                "Face: "        + yes("android.hardware.biometrics.face") + "\n";
    }

    private String buildSensorsInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                for (Sensor s : sm.getSensorList(Sensor.TYPE_ALL))
                    sb.append(s.getName()).append("\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No sensors detected.\n";
        return sb.toString();
    }

    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (cm != null) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (caps != null) {
                    sb.append("Active: ");
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) sb.append("Wi-Fi\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) sb.append("Cellular\n");
                    else sb.append("Other\n");
                }
            }

            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null && wi.getNetworkId() != -1) {
                    sb.append("SSID: ").append(wi.getSSID()).append("\n");
                    sb.append("Speed: ").append(wi.getLinkSpeed()).append(" Mbps\n");
                }
            }

        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No connectivity info.\n";
        return sb.toString();
    }

    private String buildLocationInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            sb.append("GPS: ").append(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? "Enabled" : "Disabled");
        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No location data.\n";
        return sb.toString();
    }

    private String buildBluetoothInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            BluetoothAdapter ba = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            if (ba != null) {
                sb.append("Supported: Yes\n");
                sb.append("Enabled: ").append(ba.isEnabled() ? "Yes" : "No").append("\n");
                sb.append("Name: ").append(ba.getName()).append("\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No Bluetooth data.\n";
        return sb.toString();
    }

    private String buildNfcInfo() {
        try {
            NfcAdapter a = ((NfcManager)getSystemService(Context.NFC_SERVICE)).getDefaultAdapter();
            return "NFC: " + (a != null ? "Supported" : "Not supported") + "\n";
        } catch (Throwable ignore) {}
        return "NFC: Not supported\n";
    }

    private String buildBatteryInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i != null) {
                int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int temp  = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);

                sb.append("Level: ").append(level).append("%\n");
                if (temp > 0) sb.append("Temp: ").append(temp / 10f).append("°C\n");
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No battery info.\n";
        return sb.toString();
    }

    private String buildUwbInfo() {
        return "UWB: " + yes("android.hardware.uwb") + "\n";
    }

    private String buildHapticsInfo() {
        return "Vibration: " + yes("android.hardware.vibrator") + "\n";
    }

    private String buildGnssInfo() {
        return "GNSS: " + yes("android.hardware.location.gnss") + "\n";
    }

    private String buildUsbInfo() {
        return "OTG: " + yes("android.hardware.usb.host") + "\n";
    }

    private String buildMicsInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceInfo[] devs = am.getDevices(AudioManager.GET_DEVICES_INPUTS);
            for (AudioDeviceInfo d : devs)
                sb.append("Mic: ").append(d.getProductName()).append("\n");
        } catch (Throwable ignore) {}

        if (sb.length() == 0) return "No microphones detected.\n";
        return sb.toString();
    }

    private String buildAudioHalInfo() {
        return "Audio HAL: " + getProp("ro.audio.hal.version") + "\n";
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Exception e) { return ""; }
    }

    // ROOT
    private boolean isDeviceRooted() {
        try {
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su"
            };
            for (String p : paths) if (new File(p).exists()) return true;

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            return in.readLine() != null;

        } catch (Throwable ignore) { return false; }
    }

    // UI DP/SP
    @Override public int dp(int v) { return GELAutoDP.dp(v); }
    @Override public float sp(float v) { return GELAutoDP.sp(v); }
}
