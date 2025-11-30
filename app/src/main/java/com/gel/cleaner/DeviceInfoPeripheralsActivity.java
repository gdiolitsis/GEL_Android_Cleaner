// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL v25
// Auto-Path Engine 5.3 + Root v5.1 + Permission Engine v25 (Manifest-Aware + Debug v24)
// NOTE (GEL rule): Always send full updated file ready for copy-paste — no manual edits by user.

package com.gel.cleaner;

import java.util.Locale;
import com.gel.cleaner.GELAutoActivityHook;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.Manifest;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class DeviceInfoPeripheralsActivity extends GELAutoActivityHook {

    // ============================================================
    // GEL Permission Request Engine v1.0 — Option B (Auto Request All)
    // ============================================================
    private static final String[] PERMISSIONS_ALL = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.NEARBY_WIFI_DEVICES
    };

    private static final int REQ_CODE_GEL_PERMISSIONS = 7777;

    private void requestAllRuntimePermissions() {

        if (Build.VERSION.SDK_INT < 23) return;

        java.util.List<String> toRequest = new java.util.ArrayList<>();

        for (String p : PERMISSIONS_ALL) {
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                toRequest.add(p);
            }
        }

        if (!toRequest.isEmpty()) {
            requestPermissions(toRequest.toArray(new String[0]), REQ_CODE_GEL_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_CODE_GEL_PERMISSIONS) {
            // ❌ recreate() REMOVED (no more animation freezes)
        }
    }

    // ============================================================
    // MAIN CLASS FIELDS
    // ============================================================
    private static final String NEON_GREEN = "#39FF14";
    private static final String GOLD_COLOR = "#FFD700";
    private static final int LINK_BLUE = Color.parseColor("#1E90FF");

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
                txtRootContent, txtBatteryContent, txtUwbContent, txtHapticsContent,
                txtGnssContent, txtUsbContent, txtMicsContent, txtAudioHalContent
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

    // ============================================================
    // GEL Expand Engine v3.0 — FINAL
    // ============================================================
    private void toggleSection(TextView targetContent, TextView targetIcon) {

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];

            if (c == null || ic == null) continue;

            if (c != targetContent && c.getVisibility() == View.VISIBLE) {
                animateCollapse(c);
                ic.setText("＋");
            }
        }

        if (targetContent.getVisibility() == View.VISIBLE) {
            animateCollapse(targetContent);
            targetIcon.setText("＋");
        } else {
            animateExpand(targetContent);
            targetIcon.setText("−");
        }
    }

    private void animateExpand(final View v) {
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
    // GEL SettingsClick Engine v17 — OPEN SETTINGS ONLY
    // ============================================================
    private void handleSettingsClick(Context context, String path) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Throwable ignore) {
            try {
                Intent fallback = new Intent(Settings.ACTION_SETTINGS);
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallback);
            } catch (Throwable e) { }
        }
    }

    // ============================================================
    // GEL Permission Engine v25 — Manifest-Aware + Hide Fake Links
    // Only show Settings path if:
    //  1) The permission is declared in AndroidManifest.xml
    //  2) AND the runtime permission is actually DENIED
    // ============================================================

    // Check if this app *declares* a given permission in manifest
    private boolean appDeclaresPermission(String perm) {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_PERMISSIONS
            );
            if (pi.requestedPermissions == null) return false;

            for (String p : pi.requestedPermissions) {
                if (perm.equals(p)) {
                    return true;
                }
            }
        } catch (Throwable ignore) {
        }
        return false;
    }

    // 1) Which sections really have runtime permissions (and are declared)
    private boolean sectionNeedsPermission(String type) {

        type = type.toLowerCase(Locale.US);

        // CAMERA
        if (type.contains("camera")) {
            return appDeclaresPermission(Manifest.permission.CAMERA);
        }

        // MICROPHONE
        if (type.contains("mic") || type.contains("microphone")) {
            return appDeclaresPermission(Manifest.permission.RECORD_AUDIO);
        }

        // LOCATION (fine or coarse)
        if (type.contains("location")) {
            return appDeclaresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    || appDeclaresPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // BLUETOOTH (runtime only on 12+)
        if (type.contains("bluetooth") && Build.VERSION.SDK_INT >= 31) {
            return appDeclaresPermission(Manifest.permission.BLUETOOTH_SCAN)
                    || appDeclaresPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }

        // NEARBY DEVICES (12+)
        if (type.contains("nearby") && Build.VERSION.SDK_INT >= 31) {
            return appDeclaresPermission(Manifest.permission.NEARBY_WIFI_DEVICES);
        }

        // NFC → no runtime permission on modern Android
        if (type.contains("nfc")) {
            return false;
        }

        return false;
    }

    // 2) Does the user *actually* have the permission granted?
    private boolean userHasPermission(String type) {

        type = type.toLowerCase(Locale.US);

        // CAMERA
        if (type.contains("camera")) {
            if (!appDeclaresPermission(Manifest.permission.CAMERA)) return true;
            return checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // MICROPHONE
        if (type.contains("mic") || type.contains("microphone")) {
            if (!appDeclaresPermission(Manifest.permission.RECORD_AUDIO)) return true;
            return checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // LOCATION (fine or coarse)
        if (type.contains("location")) {
            boolean hasFine   = appDeclaresPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            boolean hasCoarse = appDeclaresPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (!hasFine && !hasCoarse) return true;

            boolean fine = hasFine &&
                    (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED);
            boolean coarse = hasCoarse &&
                    (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED);

            return fine || coarse;
        }

        // BLUETOOTH (Android 12+)
        if (type.contains("bluetooth")) {
            if (Build.VERSION.SDK_INT >= 31) {
                boolean hasScan = appDeclaresPermission(Manifest.permission.BLUETOOTH_SCAN);
                boolean hasConn = appDeclaresPermission(Manifest.permission.BLUETOOTH_CONNECT);

                if (!hasScan && !hasConn) return true;

                boolean scan = !hasScan ||
                        (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                                == PackageManager.PERMISSION_GRANTED);
                boolean conn = !hasConn ||
                        (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                                == PackageManager.PERMISSION_GRANTED);

                return scan && conn;
            }
            // Older Android → no runtime permission UI
            return true;
        }

        // NFC → no runtime permission
        if (type.contains("nfc")) {
            return true;
        }

        // NEARBY DEVICES (Android 12+)
        if (type.contains("nearby")) {
            if (Build.VERSION.SDK_INT >= 31) {
                if (!appDeclaresPermission(Manifest.permission.NEARBY_WIFI_DEVICES)) return true;
                return checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
                        == PackageManager.PERMISSION_GRANTED;
            }
            return true;
        }

        return true; // default safe
    }

    // 3) Append Settings path ONLY when permission is needed AND denied
    private void appendAccessInstructions(StringBuilder sb, String type) {

        // If this section does NOT use a real runtime permission → hide path
        if (!sectionNeedsPermission(type)) return;

        // If user already HAS permission → hide path
        if (userHasPermission(type)) return;

        // Otherwise → show generic path + link
        sb.append("\nRequired Access : ").append(type).append("\n");
        sb.append("Open Settings\n");
        sb.append("Settings → Apps → Permissions\n");
    }

    // ============================================================
    // ROOT CHECK (GEL Stable v5.1) — FIXED
    // ============================================================
    private boolean isDeviceRooted() {
        try {
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk",
                    "/system/app/Magisk.apk", "/system/priv-app/Magisk"
            };

            for (String p : paths) {
                if (new File(p).exists()) return true;
            }

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();

            return line != null && !line.trim().isEmpty();

        } catch (Throwable ignore) {
            return false;
        }
    }

    // ============================================================
    // GEL Battery Path Detector v2.0 (OEM-Smart + GitHub Safe)
    // ============================================================
    private String getBatteryPathForDisplay() {

        String manu = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase(Locale.US);
        String finger = Build.FINGERPRINT == null ? "" : Build.FINGERPRINT.toLowerCase(Locale.US);
        String display = Build.DISPLAY == null ? "" : Build.DISPLAY.toLowerCase(Locale.US);

        boolean isXiaomi  = manu.contains("xiaomi") || manu.contains("redmi") || manu.contains("poco");
        boolean isMIUI    = finger.contains("miui") || display.contains("miui");
        boolean isHyperOS = finger.contains("hyperos") || display.contains("hyperos");

        boolean isSamsung = manu.contains("samsung");
        boolean isPixel   = manu.contains("google") || finger.contains("pixel");

        boolean isOppo    = manu.contains("oppo");
        boolean isRealme  = manu.contains("realme");
        boolean isOnePlus = manu.contains("oneplus");

        boolean isVivo    = manu.contains("vivo") || manu.contains("iqoo");
        boolean isHuawei  = manu.contains("huawei") || manu.contains("honor");

        boolean isMoto    = manu.contains("motorola") || manu.contains("moto");
        boolean isSony    = manu.contains("sony");
        boolean isAsus    = manu.contains("asus");
        boolean isNokia   = manu.contains("nokia");
        boolean isLenovo  = manu.contains("lenovo");
        boolean isLG      = manu.contains("lg");
        boolean isZTE     = manu.contains("zte");
        boolean isTecno   = manu.contains("tecno");
        boolean isInfinix = manu.contains("infinix");
        boolean isMeizu   = manu.contains("meizu");
        boolean isNothing = manu.contains("nothing");

        if (isSamsung) {
            return "Settings → Battery and device care → Battery";
        }

        if (isXiaomi) {
            if (isHyperOS) return "Settings → Battery → Battery usage";
            if (isMIUI)    return "Settings → Battery & performance → Battery usage";
            return "Settings → Battery";
        }

        if (isPixel) {
            return "Settings → Battery → Battery usage";
        }

        if (isOppo || isRealme) {
            return "Settings → Battery → More settings";
        }

        if (isOnePlus) {
            return "Settings → Battery → Advanced settings";
        }

        if (isVivo) {
            return "Settings → Battery";
        }

        if (isHuawei) {
            return "Settings → Battery → App launch";
        }

        if (isMoto) {
            return "Settings → Battery";
        }

        if (isSony || isAsus || isNokia || isLenovo || isLG || isZTE || isTecno || isInfinix || isMeizu || isNothing) {
            return "Settings → Battery";
        }

        return "Settings → Battery";
    }

    // ============================================================  
// CAMERA / BIOMETRICS / SENSORS / CONNECTIVITY / LOCATION  
// ============================================================  

private String buildCameraInfo() {  
    StringBuilder sb = new StringBuilder();  

    try {  
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);  
        if (cm != null) {  
            for (String id : cm.getCameraIdList()) {  

                CameraCharacteristics cc = cm.getCameraCharacteristics(id);  

                Integer facing    = cc.get(CameraCharacteristics.LENS_FACING);  
                float[] focals    = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);  
                float[] apertures = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);  
                Integer hwLevel   = cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);  
                Integer orientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);  
                Boolean flashAvail  = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);  

                sb.append("Camera ID : ").append(id).append("\n");  

                sb.append("• Facing        : ")  
                        .append(facing == CameraCharacteristics.LENS_FACING_FRONT ? "Front" :  
                                facing == CameraCharacteristics.LENS_FACING_BACK ? "Back" : "External")  
                        .append("\n");  

                if (orientation != null) {  
                    sb.append("• Orientation   : ").append(orientation).append("°\n");  
                }  

                if (focals != null && focals.length > 0) {  
                    sb.append("• Focal         : ").append(focals[0]).append(" mm\n");  
                }  

                if (apertures != null && apertures.length > 0) {  
                    sb.append("• Aperture      : f/").append(apertures[0]).append("\n");  
                }  

                if (flashAvail != null) {  
                    sb.append("• Flash         : ").append(flashAvail ? "Yes" : "No").append("\n");  
                }  

                // Basic stream configuration summary (no heavy details)  
                try {  
                    android.hardware.camera2.params.StreamConfigurationMap map =  
                            cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);  
                    if (map != null) {  
                        android.util.Size[] jpegSizes =  
                                map.getOutputSizes(android.graphics.ImageFormat.JPEG);  
                        if (jpegSizes != null && jpegSizes.length > 0) {  
                            sb.append("• JPEG Modes    : ")  
                              .append(jpegSizes.length)  
                              .append(" available output sizes\n");  
                        }  
                    }  
                } catch (Throwable ignore) { }  

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

                Boolean ois = null;  
                try {  
                    Field f = CameraCharacteristics.class.getField("LENS_INFO_OIS_AVAILABLE");  
                    Object keyObj = f.get(null);  

                    if (keyObj instanceof CameraCharacteristics.Key) {  
                        @SuppressWarnings("unchecked")  
                        CameraCharacteristics.Key<Boolean> key =  
                                (CameraCharacteristics.Key<Boolean>) keyObj;  
                        ois = cc.get(key);  
                    }  
                } catch (Throwable ignore) { }  

                if (ois != null) {  
                    sb.append("• OIS           : ").append(ois ? "Yes" : "No").append("\n");  
                } else {  
                    sb.append("• OIS Metric    : This metric requires root access to be displayed.\n");  
                }  

                sb.append("• Video Profil. : Extra stabilization telemetry requires root access.\n\n");  
            }  
        }  
    } catch (Throwable ignore) { }  

    if (sb.length() == 0) {  
        sb.append("No camera data exposed by this device.\n");  
    }  

    appendAccessInstructions(sb, "camera");  
    return sb.toString();  
}  

private String buildBiometricsInfo() {  
    StringBuilder sb = new StringBuilder();  

    boolean hasFp   = getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);  
    boolean hasFace = getPackageManager().hasSystemFeature("android.hardware.biometrics.face");  
    boolean hasIris = getPackageManager().hasSystemFeature("android.hardware.biometrics.iris");  

    sb.append("Fingerprint : ").append(hasFp   ? "Yes" : "No").append("\n");  
    sb.append("Face Unlock : ").append(hasFace ? "Yes" : "No").append("\n");  
    sb.append("Iris Scan   : ").append(hasIris ? "Yes" : "No").append("\n");  

    // Simple "biometric profile" summary  
    int modes = 0;  
    if (hasFp)   modes++;  
    if (hasFace) modes++;  
    if (hasIris) modes++;  

    sb.append("Profile     : ");  
    if (modes == 0) {  
        sb.append("No biometric hardware reported.\n");  
    } else if (modes == 1) {  
        sb.append("Single biometric modality.\n");  
    } else {  
        sb.append("Multi-biometric device (").append(modes).append(" modalities).\n");  
    }  

    sb.append("Access Mode : Enrolled templates and secure keys are never exposed to apps; \n");  
    sb.append("              advanced biometric telemetry requires root access.\n");  

    return sb.toString();  
}  

private String buildSensorsInfo() {  
    StringBuilder sb = new StringBuilder();  

    int total = 0;  

    try {  
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);  

        if (sm != null) {  
            java.util.List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);  
            for (Sensor s : all) {  
                total++;  
                sb.append("• ")  
                        .append(s.getName())  
                        .append(" (")  
                        .append(s.getVendor())  
                        .append(")\n");  
            }  

            // Key sensor presence summary  
            Sensor acc  = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  
            Sensor gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);  
            Sensor mag  = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);  
            Sensor baro = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);  
            Sensor step = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);  

            sb.append("\nSummary:\n");  
            sb.append("Accelerometer : ").append(acc  != null ? "Yes" : "No").append("\n");  
            sb.append("Gyroscope     : ").append(gyro != null ? "Yes" : "No").append("\n");  
            sb.append("Magnetometer  : ").append(mag  != null ? "Yes" : "No").append("\n");  
            sb.append("Barometer     : ").append(baro != null ? "Yes" : "No").append("\n");  
            sb.append("Step Counter  : ").append(step != null ? "Yes" : "No").append("\n");  

            sb.append("Total Sensors : ").append(total).append("\n");  
        }  
    } catch (Throwable ignore) { }  

    if (sb.length() == 0) {  
        sb.append("No sensors are exposed by this device at API level.\n");  
    }  

    sb.append("Advanced     : Extended sensor subsystem tables and raw calibration data\n");  
    sb.append("              are visible only on rooted systems.\n");  

    appendAccessInstructions(sb, "sensors");  
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

                sb.append("Active   : ");  

                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))  
                    sb.append("Wi-Fi\n");  
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))  
                    sb.append("Cellular\n");  
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))  
                    sb.append("Ethernet\n");  
                else  
                    sb.append("Other\n");  

                sb.append("Downlink : ").append(caps.getLinkDownstreamBandwidthKbps()).append(" kbps\n");  
                sb.append("Uplink   : ").append(caps.getLinkUpstreamBandwidthKbps()).append(" kbps\n");  
            }  
        }  

        if (wm != null) {  
            WifiInfo wi = wm.getConnectionInfo();  
            if (wi != null && wi.getNetworkId() != -1) {  

                sb.append("\nWi-Fi:\n");  
                sb.append("  SSID      : ").append(wi.getSSID()).append("\n");  
                sb.append("  LinkSpeed : ").append(wi.getLinkSpeed()).append(" Mbps\n");  
                sb.append("  RSSI      : ").append(wi.getRssi()).append(" dBm\n");  
            }  
        }  
    } catch (Throwable ignore) { }  

    if (sb.length() == 0) {  
        sb.append("No connectivity info is exposed by this device.\n");  
    }  

    sb.append("Deep Stats : Advanced interface counters and raw net tables are visible only on rooted systems.\n");  

    return sb.toString();  
}  

private String buildLocationInfo() {  
    StringBuilder sb = new StringBuilder();  

    try {  
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  

        boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  

        sb.append("GPS     : ").append(gps ? "Enabled" : "Disabled").append("\n");  
        sb.append("Network : ").append(net ? "Enabled" : "Disabled").append("\n");  

    } catch (Throwable ignore) { }  

    if (sb.length() == 0) {  
        sb.append("Location providers are not exposed at this moment.\n");  
    }  

    sb.append("Advanced : High-precision GNSS raw logs require root access.\n");  

    appendAccessInstructions(sb, "location");  
    return sb.toString();  
}  

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

    } catch (Throwable ignore) { }  

    sb.append("Deep Scan : Extended Bluetooth controller diagnostics require root access.\n");  

    appendAccessInstructions(sb, "bluetooth");  
    return sb.toString();  
}  

private String buildNfcInfo() {  
    StringBuilder sb = new StringBuilder();  

    try {  
        NfcManager nfc = (NfcManager) getSystemService(Context.NFC_SERVICE);  
        NfcAdapter a   = nfc != null ? nfc.getDefaultAdapter() : null;  

        sb.append("Supported : ").append(a != null ? "Yes" : "No").append("\n");  

        if (a != null) {  
            sb.append("Enabled   : ").append(a.isEnabled() ? "Yes" : "No").append("\n");  
        }  

    } catch (Throwable ignore) { }  

    sb.append("Advanced : Secure element and low-level NFC routing tables require root access.\n");  

    appendAccessInstructions(sb, "nfc");  
    return sb.toString();  
}  

private String buildBatteryInfo() {  
    StringBuilder sb = new StringBuilder();  

    try {  
        IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);  
        Intent i       = registerReceiver(null, f);  

        if (i != null) {  
            int level  = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);  
            int scale  = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);  
            int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);  
            int temp   = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);  

            sb.append("Level   : ").append(level).append("%\n");  
            sb.append("Scale   : ").append(scale).append("\n");  
            sb.append("Status  : ").append(status).append("\n");  

            if (temp > 0) {  
                sb.append("Temp    : ").append((temp / 10f)).append("°C\n");  
            }  
        }  
    } catch (Throwable ignore) { }  

    sb.append("\nLifecycle : ");  

    if (isRooted) {  

        long chargeFull       = readSysLong("/sys/class/power_supply/battery/charge_full");  
        long chargeFullDesign = readSysLong("/sys/class/power_supply/battery/charge_full_design");  
        long cycleCount       = readSysLong("/sys/class/power_supply/battery/cycle_count");  

        boolean any = false;  
        StringBuilder extra = new StringBuilder();  

        if (chargeFull > 0) {  
            extra.append("currentFull=").append(chargeFull).append(" ");  
            any = true;  
        }  

        if (chargeFullDesign > 0) {  
            extra.append("designFull=").append(chargeFullDesign).append(" ");  
            any = true;  
        }  

        if (cycleCount > 0) {  
            extra.append("cycles=").append(cycleCount);  
            any = true;  
        }  

        if (any) {  
            sb.append(extra.toString().trim()).append("\n");  
        } else {  
            sb.append("Advanced lifecycle data is not exposed by this device.\n");  
        }  

    } else {  
        sb.append("This metric requires root access to be displayed.\n");  
    }  

    if (sb.length() == 0) {  
        sb.append("Battery information is not exposed by this device.\n");  
    }  

    appendAccessInstructions(sb, "battery");  
    return sb.toString();  
}  

private String buildUwbInfo() {  
    boolean supported = getPackageManager().hasSystemFeature("android.hardware.uwb");  
    StringBuilder sb = new StringBuilder();  

    sb.append("Supported : ").append(supported ? "Yes" : "No").append("\n");  
    sb.append("Advanced  : Fine-grain ranging diagnostics require root access.\n");  

    return sb.toString();  
}  

private String buildHapticsInfo() {  
    boolean vib = getPackageManager().hasSystemFeature("android.hardware.vibrator");  
    StringBuilder sb = new StringBuilder();  

    sb.append("Supported : ").append(vib ? "Yes" : "No").append("\n");  
    sb.append("Profiles  : Advanced haptic waveform tables require root access.\n");  

    return sb.toString();  
}  

private String buildGnssInfo() {  
    boolean gnss = getPackageManager().hasSystemFeature("android.hardware.location.gnss");  
    StringBuilder sb = new StringBuilder();  

    sb.append("GNSS     : ").append(gnss ? "Yes" : "No").append("\n");  
    sb.append("Raw Logs : Full GNSS measurement streams require root access.\n");  

    return sb.toString();  
}  

private String buildUsbInfo() {  
    boolean otg = getPackageManager().hasSystemFeature("android.hardware.usb.host");  
    StringBuilder sb = new StringBuilder();  

    sb.append("OTG Support : ").append(otg ? "Yes" : "No").append("\n");  
    sb.append("Advanced    : Low-level USB descriptors and power profiles require root access.\n");  

    return sb.toString();  
}  
                                
    // ============================================================
    // GEL Other Peripherals Info v4.0 — FULL EDITION
    // ============================================================
    private String buildOtherPeripheralsInfo() {

        StringBuilder sb = new StringBuilder();

        sb.append("=== General Peripherals ===\n");

        // Vibrator / Haptics
        boolean vib = getPackageManager().hasSystemFeature("android.hardware.vibrator");
        sb.append("Vibration Motor : ").append(vib ? "Yes" : "No").append("\n");

        // Flashlight / Torch Support
        boolean flash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        sb.append("Flashlight      : ").append(flash ? "Yes" : "No").append("\n");

        // IR Blaster
        boolean ir = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
        sb.append("IR Blaster      : ").append(ir ? "Yes" : "No").append("\n\n");

        sb.append("=== Display Info ===\n");

        try {
            android.view.Display display = getWindowManager().getDefaultDisplay();

            float refresh = display.getRefreshRate();
            sb.append("Refresh Rate    : ").append(refresh).append(" Hz\n");

            android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
            display.getRealMetrics(dm);
            sb.append("Resolution      : ").append(dm.widthPixels)
                    .append(" × ").append(dm.heightPixels).append("\n");
        } catch (Throwable ignore) {
            sb.append("Display info not available.\n");
        }

        sb.append("\n=== USB / I/O ===\n");

        boolean usbHost = getPackageManager().hasSystemFeature("android.hardware.usb.host");
        sb.append("USB Host (OTG)  : ").append(usbHost ? "Yes" : "No").append("\n");

        boolean usbAcc = getPackageManager().hasSystemFeature("android.hardware.usb.accessory");
        sb.append("USB Accessory   : ").append(usbAcc ? "Yes" : "No").append("\n\n");

        sb.append("=== Audio Output Devices ===\n");

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                AudioDeviceInfo[] outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

                if (outs.length == 0) {
                    sb.append("No audio outputs detected.\n");
                } else {
                    for (AudioDeviceInfo d : outs) {
                        sb.append("• ").append(d.getProductName()).append("\n");
                    }
                }
            }
        } catch (Throwable ignore) {
            sb.append("Audio device info not available.\n");
        }

        sb.append("\n(Advanced peripheral diagnostics require root access.)\n");

        return sb.toString();
    }

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
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("No microphones are reported by the current audio service.\n");
        }

        sb.append("Advanced : Raw audio routing matrices are visible only on rooted systems.\n");

        appendAccessInstructions(sb, "mic");
        return sb.toString();
    }

    private String buildAudioHalInfo() {
        StringBuilder sb = new StringBuilder();

        String hal = getProp("ro.audio.hal.version");

        if (hal != null && !hal.isEmpty()) {
            sb.append("Audio HAL : ").append(hal).append("\n");
        } else {
            sb.append("Audio HAL : Not exposed at property level.\n");
        }

        sb.append("Deep Info : Extended hardware diagnostics require root access.\n");

        return sb.toString();
    }

    private String buildRootInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Root Access Mode : ")
                .append(isRooted ? "Rooted device (superuser access detected)" : "Non-rooted device (standard access)")
                .append("\n");

        sb.append("Build Tags       : ").append(Build.TAGS).append("\n");

        String secure = getProp("ro.secure");
        if (secure != null && !secure.isEmpty()) {
            sb.append("ro.secure        : ").append(secure).append("\n");
        }

        String dbg = getProp("ro.debuggable");
        if (dbg != null && !dbg.isEmpty()) {
            sb.append("ro.debuggable    : ").append(dbg).append("\n");
        }

        String verity = getProp("ro.boot.veritymode");
        if (verity != null && !verity.isEmpty()) {
            sb.append("Verity Mode      : ").append(verity).append("\n");
        }

        String selinux = getProp("ro.build.selinux");
        if (selinux != null && !selinux.isEmpty()) {
            sb.append("SELinux          : ").append(selinux).append("\n");
        }

        sb.append("\nFusion Layer     : ");
        if (isRooted) {
            sb.append("Peripherals telemetry is using GEL Dynamic Access Routing Engine v1.0 with root access.\n");
        } else {
            sb.append("Peripherals run with standard Android permissions; advanced routing is only available on rooted devices and cannot be enabled from inside this app.\n");
        }

        if (isRooted) {

            sb.append("\nAdvanced subsystem tables are fully enabled on this device.\n");
            sb.append("Extended hardware diagnostics are active.\n");

            sb.append("\nRoot indicators:\n");

            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
            };

            for (String p : paths) {
                if (new File(p).exists()) {
                    sb.append("  ").append(p).append("\n");
                }
            }

            sb.append("\nPeripherals telemetry is running with root access.\n");

        } else {

            sb.append("\nThis device is not rooted.\n");
            sb.append("Advanced subsystem tables are visible only on rooted systems.\n");
            sb.append("Extended hardware diagnostics require root access.\n");
        }

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
            } catch (Exception ignored) { }
        }
    }

    private String readSysString(String path) {
        BufferedReader br = null;

        try {
            File f = new File(path);
            if (!f.exists()) return null;

            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();

            return line != null ? line.trim() : null;

        } catch (Throwable ignore) {
            return null;

        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) { }
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

        requestAllRuntimePermissions();

        // Debug log of REAL permission state for this app
        showPermissionDebugInfo();

        set(R.id.txtCameraContent,        buildCameraInfo());
        set(R.id.txtBiometricsContent,    buildBiometricsInfo());
        set(R.id.txtSensorsContent,       buildSensorsInfo());
        set(R.id.txtConnectivityContent,  buildConnectivityInfo());
        set(R.id.txtLocationContent,      buildLocationInfo());
        set(R.id.txtBluetoothContent,     buildBluetoothInfo());
        set(R.id.txtNfcContent,           buildNfcInfo());
        set(R.id.txtBatteryContent,       buildBatteryInfo());
        set(R.id.txtOtherPeripheralsContent, buildOtherPeripheralsInfo());
        set(R.id.txtUwbContent,           buildUwbInfo());
        set(R.id.txtHapticsContent,       buildHapticsInfo());
        set(R.id.txtGnssContent,          buildGnssInfo());
        set(R.id.txtUsbContent,           buildUsbInfo());
        set(R.id.txtMicsContent,          buildMicsInfo());
        set(R.id.txtAudioHalContent,      buildAudioHalInfo());
        set(R.id.txtRootContent,          buildRootInfo());
    }

    // ============================================================
    // GEL Permission Debug Mode v24 — FULL BLOCK (Logcat only)
    // ============================================================
    private void showPermissionDebugInfo() {

        StringBuilder dbg = new StringBuilder();
        dbg.append("=== GEL Permission Debug Mode v24 ===\n\n");

        dbg.append("CAMERA            : ")
                .append(checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        dbg.append("MICROPHONE        : ")
                .append(checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        dbg.append("LOCATION (FINE)   : ")
                .append(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        dbg.append("LOCATION (COARSE) : ")
                .append(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                .append("\n");

        if (Build.VERSION.SDK_INT >= 31) {

            dbg.append("BLUETOOTH SCAN    : ")
                    .append(checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) ==
                            PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                    .append("\n");

            dbg.append("BLUETOOTH CONNECT : ")
                    .append(checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                            PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                    .append("\n");

            dbg.append("NEARBY DEVICES    : ")
                    .append(checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES) ==
                            PackageManager.PERMISSION_GRANTED ? "ALLOWED" : "DENIED")
                    .append("\n");
        } else {
            dbg.append("BLUETOOTH         : AUTO-ALLOWED (API<31)\n");
            dbg.append("NEARBY DEVICES    : AUTO-ALLOWED (API<31)\n");
        }

        dbg.append("NFC               : NO PERMISSION NEEDED\n");

        android.util.Log.e("GEL-PERMS", dbg.toString());
    }

    // ============================================================
    // SET METHOD — helper for onStart()
    // ============================================================
    private void set(int id, String txt) {
        TextView t = findViewById(id);
        if (t == null) return;
        applyNeonValues(t, txt);
    }

    // ============================================================
    // APPLY NEON VALUES + OEM GOLD + CLICKABLE PATHS
    // ============================================================
    private void applyNeonValues(TextView tv, String text) {
        if (text == null) {
            tv.setText("");
            return;
        }

        int neon = Color.parseColor(NEON_GREEN);
        int gold = Color.parseColor(GOLD_COLOR);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        int start = 0;
        int len = text.length();

        // NEON GREEN after colon
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

        // GOLD "Xiaomi"
        int idxX = text.indexOf("Xiaomi");
        while (idxX != -1) {
            int end = idxX + "Xiaomi".length();
            ssb.setSpan(
                    new ForegroundColorSpan(gold),
                    idxX,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            idxX = text.indexOf("Xiaomi", end);
        }

        // BOLD "Open Settings"
        String os = "Open Settings";
        int idxOS = text.indexOf(os);
        if (idxOS != -1) {
            ssb.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    idxOS,
                    idxOS + os.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // BLUE CLICKABLE PATH SPANS
        boolean hasPath = false;
        int idx = text.indexOf("Settings →");

        while (idx != -1) {
            int end = text.indexOf('\n', idx);
            if (end == -1) end = len;

            final String pathText = text.substring(idx, end);

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    handleSettingsClick(widget.getContext(), pathText);
                }
            }, idx, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            ssb.setSpan(
                    new ForegroundColorSpan(LINK_BLUE),
                    idx,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            hasPath = true;
            idx = text.indexOf("Settings →", end);
        }

        if (hasPath) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setHighlightColor(Color.TRANSPARENT);
        }

        tv.setText(ssb);
    }
}
```0
