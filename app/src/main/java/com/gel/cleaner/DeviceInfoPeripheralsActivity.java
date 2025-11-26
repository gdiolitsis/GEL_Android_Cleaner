// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL v7.4 PRO (Soft Expand v2.0)

package com.gel.cleaner;

import com.gel.cleaner.base.*;
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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    protected void onStart() {
        super.onStart();

        setTxt(R.id.txtCameraContent,        buildCameraInfo());
        setTxt(R.id.txtBiometricsContent,    buildBiometricsInfo());
        setTxt(R.id.txtSensorsContent,       buildSensorsInfo());
        setTxt(R.id.txtConnectivityContent,  buildConnectivityInfo());
        setTxt(R.id.txtLocationContent,      buildLocationInfo());
        setTxt(R.id.txtBluetoothContent,     buildBluetoothInfo());
        setTxt(R.id.txtNfcContent,           buildNfcInfo());
        setTxt(R.id.txtBatteryContent,       buildBatteryInfo());
        setTxt(R.id.txtUwbContent,           buildUwbInfo());
        setTxt(R.id.txtHapticsContent,       buildHapticsInfo());
        setTxt(R.id.txtGnssContent,          buildGnssInfo());
        setTxt(R.id.txtUsbContent,           buildUsbInfo());
        setTxt(R.id.txtMicsContent,          buildMicsInfo());
        setTxt(R.id.txtAudioHalContent,      buildAudioHalInfo());

        TextView txtRoot = findViewById(R.id.txtRootContent);
        if (txtRoot != null) {
            txtRoot.setText(isRooted ?
                    "ROOT: YES (su detected)" :
                    "DEVICE IS NOT ROOTED");
        }

        TextView txtOther = findViewById(R.id.txtOtherPeripheralsContent);
        if (txtOther != null) {
            boolean vib = getPackageManager().hasSystemFeature(PackageManager.FEATURE_VIBRATOR);
            txtOther.setText("Vibration Motor : " + (vib ? "Yes" : "No"));
        }
    }

    private void setTxt(int id, String txt) {
        TextView t = findViewById(id);
        if (t != null) t.setText(txt);
    }

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView open, TextView icon) {
        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == open) continue;
            animateCollapse(c);
            ic.setText("＋");
        }

        if (open.getVisibility() == View.VISIBLE) {
            animateCollapse(open);
            icon.setText("＋");
        } else {
            animateExpand(open);
            icon.setText("−");
        }
    }

    private void animateExpand(final View v) {
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int h = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        v.setAlpha(0f);

        v.animate().alpha(1f).setDuration(160)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> v.getLayoutParams().height = h)
                .start();
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        int h = v.getMeasuredHeight();
        v.animate().alpha(0f).setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = h;
                    v.setAlpha(1f);
                })
                .start();
    }

    private boolean isDeviceRooted() {
        try {
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su"
            };
            for (String p : paths) if (new File(p).exists()) return true;

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            return line != null && !line.isEmpty();
        } catch (Throwable ignore) {
            return false;
        }
    }

    @Override public int dp(int v) { return GELAutoDP.dp(v); }
    @Override public float sp(float v) { return GELAutoDP.sp(v); }

    private String buildCameraInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (cm != null) {
                for (String id : cm.getCameraIdList()) {
                    CameraCharacteristics cc = cm.getCameraCharacteristics(id);
                    sb.append("Camera ID ").append(id).append("\n");
                }
            }
        } catch (Throwable ignore) {}
        return sb.length() == 0 ? "No camera data" : sb.toString();
    }

    private String buildBiometricsInfo() {
        return "Fingerprint : " + yes(PackageManager.FEATURE_FINGERPRINT) + "\n" +
               "Face Unlock : " + yes("android.hardware.biometrics.face") + "\n" +
               "Iris Scan   : " + yes("android.hardware.biometrics.iris") + "\n";
    }

    private String yes(String feat) {
        return getPackageManager().hasSystemFeature(feat) ? "Yes" : "No";
    }

    private String buildSensorsInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            for (Sensor s : sm.getSensorList(Sensor.TYPE_ALL))
                sb.append(s.getName()).append("\n");
        } catch (Throwable ignore) {}
        return sb.length() == 0 ? "No sensors detected" : sb.toString();
    }

    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            if (cm != null) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (caps != null) {
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) sb.append("Wi-Fi\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) sb.append("Cellular\n");
                }
            }

            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi.getNetworkId() != -1) {
                    sb.append("SSID: ").append(wi.getSSID()).append("\n");
                    sb.append("Speed: ").append(wi.getLinkSpeed()).append(" Mbps\n");
                }
            }

        } catch (Throwable ignore) {}
        return sb.length() == 0 ? "No connectivity" : sb.toString();
    }

    private String buildLocationInfo() {
        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            return "GPS: " + (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? "On" : "Off") + "\n" +
                   "Network: " + (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? "On" : "Off") + "\n";
        } catch (Throwable e) {
            return "No location info";
        }
    }

    private String buildBluetoothInfo() {
        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
            BluetoothAdapter ba = bm.getAdapter();
            if (ba == null) return "Supported: No";
            return "Supported: Yes\nEnabled: " + (ba.isEnabled() ? "Yes" : "No") + "\nName: " + ba.getName();
        } catch (Throwable ignore) {}
        return "No BT info";
    }

    private String buildNfcInfo() {
        try {
            NfcManager nfc = (NfcManager) getSystemService(NFC_SERVICE);
            NfcAdapter a = nfc.getDefaultAdapter();
            if (a == null) return "NFC: Not supported";
            return "NFC: Supported\nEnabled: " + (a.isEnabled() ? "Yes" : "No");
        } catch (Throwable ignore) {}
        return "NFC error";
    }

    private String buildBatteryInfo() {
        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int temp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            return "Level: " + lvl + "%\nTemp: " + (temp / 10f) + "°C\n";
        } catch (Throwable e) {
            return "Battery info error";
        }
    }

    private String buildUwbInfo() {
        return "UWB: " + yes("android.hardware.uwb") + "\n";
    }

    private String buildHapticsInfo() {
        return "Vibration: " + yes(PackageManager.FEATURE_VIBRATOR) + "\n";
    }

    private String buildGnssInfo() {
        return "GNSS: " + yes(PackageManager.FEATURE_LOCATION_GNSS) + "\n";
    }

    private String buildUsbInfo() {
        return "OTG: " + yes("android.hardware.usb.host") + "\n";
    }

    private String buildMicsInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
            for (AudioDeviceInfo d : am.getDevices(AudioManager.GET_DEVICES_INPUTS))
                sb.append("Mic: ").append(d.getProductName()).append("\n");
        } catch (Throwable ignore) {}
        return sb.length() == 0 ? "No microphones" : sb.toString();
    }

    private String buildAudioHalInfo() {
        return "Audio HAL: " + getProp("ro.audio.hal.version") + "\n";
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            return line == null ? "" : line.trim();
        } catch (Throwable e) { return ""; }
    }
}
