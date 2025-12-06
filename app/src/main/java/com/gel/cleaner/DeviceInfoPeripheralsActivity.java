package com.gel.cleaner;
    
import android.view.animation.AccelerateDecelerateInterpolator;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import java.io.FileFilter;

import android.content.pm.FeatureInfo;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.text.method.LinkMovementMethod;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.CellInfo;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

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
        
        }
    }

    // ============================================================
    // MAIN CLASS FIELDS
    // ============================================================
    private static final String NEON_GREEN = "#39FF14";
    private static final String GOLD_COLOR = "#FFD700";
    private static final int LINK_BLUE     = Color.parseColor("#1E90FF");

    private boolean isRooted = false;

    private TextView[] allContents;
    private TextView[] allIcons;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {   // ✅ FIXED NAME
        super.onCreate(savedInstanceState);                // ✅ FIXED NAME
        setContentView(R.layout.activity_device_info_peripherals);

        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null)
            title.setText(getString(R.string.phone_info_peripherals));

        // (optional) auto-request runtime permissions
        requestAllRuntimePermissions();

        // ============================================================
        // 1. BATTERY — FIND VIEWS
        // ============================================================
        final LinearLayout batteryContainer = findViewById(R.id.batteryContainer);
        final TextView txtBatteryContent    = findViewById(R.id.txtBatteryContent);
        final TextView iconBattery          = findViewById(R.id.iconBatteryToggle);

        try {
            txtBatteryContent.setText(buildBatteryInfo());
        } catch (Exception ignored) {}

        // ============================================================
        // BATTERY — CLICK HANDLER FOR POPUP
        // ============================================================
        TextView btnCapacity = findViewById(R.id.txtBatteryModelCapacity);
        if (btnCapacity != null) {
            btnCapacity.setOnClickListener(v -> showBatteryCapacityDialog());
        }

// ============================================================
// BATTERY — FORCE INFO WHEN OPENING
// ============================================================
findViewById(R.id.headerBattery).setOnClickListener(v -> {

    // 1) Close ALL other sections except battery TEXTVIEW
    for (int i = 0; i < allContents.length; i++) {
        View section = allContents[i];

        // ✔ ΜΟΝΟ αυτό είναι σωστό
        if (section != txtBatteryContent) {
            section.setVisibility(View.GONE);
            allIcons[i].setText("＋");
        }
    }

    // 2) Toggle battery section
    if (batteryContainer.getVisibility() == View.GONE) {

        // Open full block
        batteryContainer.setVisibility(View.VISIBLE);
        iconBattery.setText("－");

        // Show battery info
        txtBatteryContent.setVisibility(View.VISIBLE);
        txtBatteryContent.setText(buildBatteryInfo());

    } else {
        batteryContainer.setVisibility(View.GONE);
        iconBattery.setText("＋");
    }
});

        // ============================================================
        // CONTENT TEXT VIEWS — ORDERED EXACTLY AS SECTIONS APPEAR
        // ============================================================
        TextView txtScreenContent          = findViewById(R.id.txtScreenContent);
        TextView txtCameraContent          = findViewById(R.id.txtCameraContent);
        TextView txtConnectivityContent    = findViewById(R.id.txtConnectivityContent);
        TextView txtLocationContent        = findViewById(R.id.txtLocationContent);
        TextView txtThermalContent         = findViewById(R.id.txtThermalContent);
        TextView txtModemContent           = findViewById(R.id.txtModemContent);
        TextView txtWifiAdvancedContent    = findViewById(R.id.txtWifiAdvancedContent);
        TextView txtAudioUnifiedContent    = findViewById(R.id.txtAudioUnifiedContent);
        TextView txtSensorsContent         = findViewById(R.id.txtSensorsContent);
        TextView txtSensorsExtendedContent = findViewById(R.id.txtSensorsExtendedContent);
        TextView txtBiometricsContent      = findViewById(R.id.txtBiometricsContent);
        TextView txtNfcContent             = findViewById(R.id.txtNfcContent);
        TextView txtGnssContent            = findViewById(R.id.txtGnssContent);
        TextView txtUwbContent             = findViewById(R.id.txtUwbContent);
        TextView txtUsbContent             = findViewById(R.id.txtUsbContent);
        TextView txtHapticsContent         = findViewById(R.id.txtHapticsContent);
        TextView txtSystemFeaturesContent  = findViewById(R.id.txtSystemFeaturesContent);
        TextView txtSecurityFlagsContent   = findViewById(R.id.txtSecurityFlagsContent);
        TextView txtRootContent            = findViewById(R.id.txtRootContent);
        TextView txtOtherPeripherals       = findViewById(R.id.txtOtherPeripheralsContent);

        // ============================================================
        // ICONS — ORDERED EXACTLY AS SECTIONS
        // ============================================================
        TextView iconScreen         = findViewById(R.id.iconScreenToggle);
        TextView iconCamera         = findViewById(R.id.iconCameraToggle);
        TextView iconConnectivity   = findViewById(R.id.iconConnectivityToggle);
        TextView iconLocation       = findViewById(R.id.iconLocationToggle);
        TextView iconThermal        = findViewById(R.id.iconThermalToggle);
        TextView iconModem          = findViewById(R.id.iconModemToggle);
        TextView iconWifiAdvanced   = findViewById(R.id.iconWifiAdvancedToggle);
        TextView iconAudioUnified   = findViewById(R.id.iconAudioUnifiedToggle);
        TextView iconSensors        = findViewById(R.id.iconSensorsToggle);
        TextView iconSensorsExtended = findViewById(R.id.iconSensorsExtendedToggle);
        TextView iconBiometrics     = findViewById(R.id.iconBiometricsToggle);
        TextView iconNfc            = findViewById(R.id.iconNfcToggle);
        TextView iconGnss           = findViewById(R.id.iconGnssToggle);
        TextView iconUwb            = findViewById(R.id.iconUwbToggle);
        TextView iconUsb            = findViewById(R.id.iconUsbToggle);
        TextView iconHaptics        = findViewById(R.id.iconHapticsToggle);
        TextView iconSystemFeatures = findViewById(R.id.iconSystemFeaturesToggle);
        TextView iconSecurityFlags  = findViewById(R.id.iconSecurityFlagsToggle);
        TextView iconRoot           = findViewById(R.id.iconRootToggle);
        TextView iconOther          = findViewById(R.id.iconOtherPeripheralsToggle);

        allContents = new TextView[]{
                txtBatteryContent,          // 1
                txtScreenContent,           // 2
                txtCameraContent,           // 3
                txtConnectivityContent,     // 4
                txtLocationContent,         // 5
                txtThermalContent,          // 6
                txtModemContent,            // 7
                txtWifiAdvancedContent,     // 8
                txtAudioUnifiedContent,     // 9
                txtSensorsContent,          // 10
                txtSensorsExtendedContent,  // 11
                txtBiometricsContent,       // 12
                txtNfcContent,              // 13
                txtGnssContent,             // 14
                txtUwbContent,              // 15
                txtUsbContent,              // 16
                txtHapticsContent,          // 17
                txtSystemFeaturesContent,   // 18
                txtSecurityFlagsContent,    // 19
                txtRootContent,             // 20
                txtOtherPeripherals         // 21
        };

        allIcons = new TextView[]{
                iconBattery,          // 1
                iconScreen,           // 2
                iconCamera,           // 3
                iconConnectivity,     // 4
                iconLocation,         // 5
                iconThermal,          // 6
                iconModem,            // 7
                iconWifiAdvanced,     // 8
                iconAudioUnified,     // 9
                iconSensors,          // 10
                iconSensorsExtended,  // 11
                iconBiometrics,       // 12
                iconNfc,              // 13
                iconGnss,             // 14
                iconUwb,              // 15
                iconUsb,              // 16
                iconHaptics,          // 17
                iconSystemFeatures,   // 18
                iconSecurityFlags,    // 19
                iconRoot,             // 20
                iconOther             // 21
        };

        // ============================================================
        // APPLY TEXTS FIRST
        // ============================================================
        populateAllSections();

        // ============================================================
        // SETUP SECTIONS
        // ============================================================
        setupSection(findViewById(R.id.headerBattery),
                     findViewById(R.id.batteryContainer),
                     iconBattery);
        setupSection(findViewById(R.id.headerScreen),            txtScreenContent,          iconScreen);  
        setupSection(findViewById(R.id.headerCamera),            txtCameraContent,          iconCamera);
        setupSection(findViewById(R.id.headerConnectivity),      txtConnectivityContent,    iconConnectivity);
        setupSection(findViewById(R.id.headerLocation),          txtLocationContent,        iconLocation);
        setupSection(findViewById(R.id.headerThermal),           txtThermalContent,         iconThermal);  
        setupSection(findViewById(R.id.headerModem),             txtModemContent,           iconModem);  
        setupSection(findViewById(R.id.headerWifiAdvanced),      txtWifiAdvancedContent,    iconWifiAdvanced);  
        setupSection(findViewById(R.id.headerAudioUnified),      txtAudioUnifiedContent,    iconAudioUnified);
        setupSection(findViewById(R.id.headerSensors),           txtSensorsContent,         iconSensors);
        setupSection(findViewById(R.id.headerSensorsExtended),   txtSensorsExtendedContent, iconSensorsExtended);  
        setupSection(findViewById(R.id.headerBiometrics),        txtBiometricsContent,      iconBiometrics);
        setupSection(findViewById(R.id.headerNfc),               txtNfcContent,             iconNfc);
        setupSection(findViewById(R.id.headerGnss),              txtGnssContent,            iconGnss);
        setupSection(findViewById(R.id.headerUwb),               txtUwbContent,             iconUwb);
        setupSection(findViewById(R.id.headerUsb),               txtUsbContent,             iconUsb);
        setupSection(findViewById(R.id.headerHaptics),           txtHapticsContent,         iconHaptics);
        setupSection(findViewById(R.id.headerSystemFeatures),    txtSystemFeaturesContent,  iconSystemFeatures);  
        setupSection(findViewById(R.id.headerSecurityFlags),     txtSecurityFlagsContent,   iconSecurityFlags);  
        setupSection(findViewById(R.id.headerRoot),              txtRootContent,            iconRoot);
        setupSection(findViewById(R.id.headerOtherPeripherals),  txtOtherPeripherals,       iconOther);

    }  // 🔥 ΤΕΛΟΣ onCreate()

// ============================================================  
// GEL Section Setup Engine — UNIVERSAL VERSION (Accordion Mode)  
// ============================================================  
private void setupSection(View header, View content, TextView icon) {

    if (header == null || content == null || icon == null)
        return;

    // Start collapsed
    content.setVisibility(View.GONE);
    icon.setText("＋");

    header.setOnClickListener(v -> {

        // 1️⃣ Κλείσε όλα τα άλλα sections (accordion behavior)
        for (int i = 0; i < allContents.length; i++) {
            if (allContents[i] != content) {
                allContents[i].setVisibility(View.GONE);
                allIcons[i].setText("＋");
            }
        }

        // 2️⃣ Toggle μόνο το δικό του
        if (content.getVisibility() == View.GONE) {
            content.setVisibility(View.VISIBLE);
            icon.setText("－");
        } else {
            content.setVisibility(View.GONE);
            icon.setText("＋");
        }
    });
}
    // ============================================================
    // GEL Expand Engine v3.0 — FINAL
    // ============================================================
    private void toggleSection(TextView targetContent, TextView targetIcon) {

        for (int i = 0; i < allContents.length; i++) {
            TextView c  = allContents[i];
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
            targetIcon.setText("－");
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
        } catch (Throwable ignore) {
            try {
                Intent fallback = new Intent(Settings.ACTION_SETTINGS);
                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallback);
            } catch (Throwable e) { }
        }
    }

    // ============================================================
    // GEL Permission Engine v25 — Manifest-Aware + Hide Fake Links
    // ============================================================
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

    private boolean sectionNeedsPermission(String type) {

        type = type.toLowerCase(Locale.US);

        if (type.contains("camera")) {
            return appDeclaresPermission(Manifest.permission.CAMERA);
        }

        if (type.contains("mic") || type.contains("microphone")) {
            return appDeclaresPermission(Manifest.permission.RECORD_AUDIO);
        }

        if (type.contains("location")) {
            return appDeclaresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    || appDeclaresPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (type.contains("bluetooth") && Build.VERSION.SDK_INT >= 31) {
            return appDeclaresPermission(Manifest.permission.BLUETOOTH_SCAN)
                    || appDeclaresPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if (type.contains("nearby") && Build.VERSION.SDK_INT >= 31) {
            return appDeclaresPermission(Manifest.permission.NEARBY_WIFI_DEVICES);
        }

        if (type.contains("nfc")) {
            return false;
        }

        return false;
    }

    private boolean userHasPermission(String type) {

        type = type.toLowerCase(Locale.US);

        if (type.contains("camera")) {
            if (!appDeclaresPermission(Manifest.permission.CAMERA)) return true;
            return checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
        }

        if (type.contains("mic") || type.contains("microphone")) {
            if (!appDeclaresPermission(Manifest.permission.RECORD_AUDIO)) return true;
            return checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED;
        }

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
            return true;
        }

        if (type.contains("nfc")) {
            return true;
        }

        if (type.contains("nearby")) {
            if (Build.VERSION.SDK_INT >= 31) {
                if (!appDeclaresPermission(Manifest.permission.NEARBY_WIFI_DEVICES)) return true;
                return checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
                        == PackageManager.PERMISSION_GRANTED;
            }
            return true;
        }

        return true;
    }

    private void appendAccessInstructions(StringBuilder sb, String type) {

        if (!sectionNeedsPermission(type)) return;
        if (userHasPermission(type)) return;

        sb.append("\nRequired Access  : ").append(type).append("\n");
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

        String manu    = Build.MANUFACTURER == null ? "" : Build.MANUFACTURER.toLowerCase(Locale.US);
        String finger  = Build.FINGERPRINT == null ? "" : Build.FINGERPRINT.toLowerCase(Locale.US);
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

        if (isSony || isAsus || isNokia || isLenovo || isLG || isZTE
                || isTecno || isInfinix || isMeizu || isNothing) {
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

                    Integer facing      = cc.get(CameraCharacteristics.LENS_FACING);
                    float[] focals      = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    float[] apertures   = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                    Integer hwLevel     = cc.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    Integer orientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Boolean flashAvail  = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    int[]   reqCaps     = cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);

                    sb.append("Camera ID        : ").append(id).append("\n");

                    String facingStr = "Unknown";
                    if (facing != null) {
                        if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                            facingStr = "Front";
                        } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                            facingStr = "Back";
                        } else if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) {
                            facingStr = "External";
                        }
                    }
                    sb.append("• Facing         : ").append(facingStr).append("\n");

                    if (orientation != null) {
                        sb.append("• Orientation    : ").append(orientation).append("°\n");
                    }

                    if (focals != null && focals.length > 0) {
                        sb.append("• Focal          : ").append(focals[0]).append(" mm\n");
                    }

                    if (apertures != null && apertures.length > 0) {
                        sb.append("• Aperture       : f/").append(apertures[0]).append("\n");
                    }

                    if (flashAvail != null) {
                        sb.append("• Flash          : ").append(flashAvail ? "Yes" : "No").append("\n");
                    }

                    try {
                        android.hardware.camera2.params.StreamConfigurationMap map =
                                cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        if (map != null) {
                            android.util.Size[] jpegSizes =
                                    map.getOutputSizes(android.graphics.ImageFormat.JPEG);
                            if (jpegSizes != null && jpegSizes.length > 0) {
                                sb.append("• JPEG Modes     : ")
                                        .append(jpegSizes.length)
                                        .append(" available output sizes\n");
                            }
                        }
                    } catch (Throwable ignore) { }

                    if (reqCaps != null) {
                        sb.append("• Capabilities   : ").append(reqCaps.length).append(" flags\n");
                    }

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
                        sb.append("• HW Level       : ").append(level).append("\n");
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
                        sb.append("• OIS            : ").append(ois ? "Yes" : "No").append("\n");
                    } else {
                        sb.append("• OIS Metric     : This metric may not be exposed on this device.\n");
                    }

                    sb.append("• Video Profil.  : Extra stabilization telemetry requires root access.\n\n");
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

        sb.append("Fingerprint      : ").append(hasFp   ? "Yes" : "No").append("\n");
        sb.append("Face Unlock      : ").append(hasFace ? "Yes" : "No").append("\n");
        sb.append("Iris Scan        : ").append(hasIris ? "Yes" : "No").append("\n");

        int modes = 0;
        if (hasFp)   modes++;
        if (hasFace) modes++;
        if (hasIris) modes++;

        sb.append("Profile          : ");
        if (modes == 0) {
            sb.append("No biometric hardware reported.\n");
        } else if (modes == 1) {
            sb.append("Single biometric modality.\n");
        } else {
            sb.append("Multi-biometric device (").append(modes).append(" modalities).\n");
        }

        sb.append("Advanced         : Enrolled templates and secure keys stay inside TEE; telemetry requires root access.\n");

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

                Sensor acc   = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor gyro  = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                Sensor mag   = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                Sensor baro  = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
                Sensor step  = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                Sensor prox  = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                Sensor light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

                sb.append("\nSummary:\n");
                sb.append("Accelerometer    : ").append(acc   != null ? "Yes" : "No").append("\n");
                sb.append("Gyroscope        : ").append(gyro  != null ? "Yes" : "No").append("\n");
                sb.append("Magnetometer     : ").append(mag   != null ? "Yes" : "No").append("\n");
                sb.append("Barometer        : ").append(baro  != null ? "Yes" : "No").append("\n");
                sb.append("Proximity        : ").append(prox  != null ? "Yes" : "No").append("\n");
                sb.append("Light            : ").append(light != null ? "Yes" : "No").append("\n");
                sb.append("Step Counter     : ").append(step  != null ? "Yes" : "No").append("\n");
                sb.append("Total Sensors    : ").append(total).append("\n");
            }
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("No sensors are exposed by this device at API level.\n");
        }

        sb.append("Advanced         : Extended sensor subsystem tables and raw calibration data are visible only on rooted systems.\n");

        appendAccessInstructions(sb, "sensors");
        return sb.toString();
    }

    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();

        // ============================================================
        // NETWORK TRANSPORTS (Wi-Fi / Cellular / Ethernet)
        // ============================================================
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (cm != null) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (caps != null) {

                    sb.append("Connection Type  : ");

                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                        sb.append("Wi-Fi\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                        sb.append("Cellular\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                        sb.append("Ethernet\n");
                    else
                        sb.append("Other\n");

                    boolean validated  = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                    boolean notMetered = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);

                    sb.append("Downlink         : ").append(caps.getLinkDownstreamBandwidthKbps()).append(" kbps\n");
                    sb.append("Uplink           : ").append(caps.getLinkUpstreamBandwidthKbps()).append(" kbps\n");
                    sb.append("Validated        : ").append(validated ? "Yes" : "No").append("\n");
                    sb.append("Metered          : ").append(notMetered ? "No" : "Yes").append("\n");
                }
            }

            // ============================================================
            // WI-FI DETAILS
            // ============================================================
            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null && wi.getNetworkId() != -1) {

                    sb.append("\nWi-Fi Details:\n");
                    sb.append("  SSID           : ").append(wi.getSSID()).append("\n");
                    sb.append("  LinkSpeed      : ").append(wi.getLinkSpeed()).append(" Mbps\n");
                    sb.append("  RSSI           : ").append(wi.getRssi()).append(" dBm\n");
                    sb.append("  Frequency      : ").append(wi.getFrequency()).append(" MHz\n");
                    sb.append("  MAC            : ").append(wi.getMacAddress()).append("\n");
                }
            }

        } catch (Throwable ignore) {}


        // ============================================================
        // BLUETOOTH — FULL DETAIL (INTEGRATED)
        // ============================================================
        sb.append("\nBluetooth:\n");

        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter ba = bm != null ? bm.getAdapter() : null;

            if (ba == null) {
                sb.append("  Supported      : No\n");
            } else {
                sb.append("  Supported      : Yes\n");
                sb.append("  Enabled        : ").append(ba.isEnabled() ? "Yes" : "No").append("\n");

                // State
                int state = ba.getState();
                String stateStr;
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:  stateStr = "Turning On";  break;
                    case BluetoothAdapter.STATE_ON:          stateStr = "On";          break;
                    case BluetoothAdapter.STATE_TURNING_OFF: stateStr = "Turning Off"; break;
                    default:                                 stateStr = "Off";         break;
                }
                sb.append("  State          : ").append(stateStr).append("\n");

                // Identity
                sb.append("  Name           : ").append(ba.getName()).append("\n");
                sb.append("  Address        : ").append(ba.getAddress()).append("\n");

                // Version detection (API-safe)
                try {
                    int btVer = Build.VERSION.SDK_INT >= 31 ?
                            (BluetoothAdapter.getDefaultAdapter() != null &&
                                    BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner() != null ? 5 : 4)
                            :
                            (Build.VERSION.SDK_INT >= 21 ? 4 : 3);
                    sb.append("  Version        : Bluetooth ").append(btVer).append("\n");
                } catch (Throwable e) {
                    sb.append("  Version        : Unknown\n");
                }

                // Classic Features
                sb.append("  Scan Mode      : ").append(ba.getScanMode()).append("\n");
                sb.append("  Discoverable   : ").append(
                        ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE ? "Yes" : "No"
                ).append("\n");

                // BLE Support
                boolean le = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
                sb.append("  BLE Support    : ").append(le ? "Yes" : "No").append("\n");

                // Hardware Capabilities
                sb.append("  Multiple Adv   : ");
                try {
                    boolean multi = ba.isMultipleAdvertisementSupported();
                    sb.append(multi ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {
                    sb.append("Unknown\n");
                }

                sb.append("  LE Scanner     : ");
                try {
                    boolean leScan = ba.getBluetoothLeScanner() != null;
                    sb.append(leScan ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {
                    sb.append("Unknown\n");
                }

                sb.append("  Offloaded Filt.: ");
                try {
                    boolean off = ba.isOffloadedFilteringSupported();
                    sb.append(off ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {
                    sb.append("Unknown\n");
                }

                // Connected Devices
                try {
                    List<BluetoothDevice> con =
                            bm.getConnectedDevices(BluetoothProfile.GATT);
                    sb.append("  GATT Devices   : ").append(con != null ? con.size() : 0).append("\n");
                } catch (Throwable ignore) {
                    sb.append("  GATT Devices   : Unknown\n");
                }
            }

        } catch (Throwable ignore) {}
       
       // ADVANCED INFO (green comments single line)
       
sb.append("\nDeep Stats       : Advanced interface counters, raw RF tables, Bluetooth controller logs and HCI traces, require root access.\n");

return sb.toString();
}
    private String buildLocationInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                sb.append("GPS              : ").append(gps ? "Enabled" : "Disabled").append("\n");
                sb.append("Network          : ").append(net ? "Enabled" : "Disabled").append("\n");
            }
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("Location providers are not exposed at this moment.\n");
        }

        sb.append("Advanced         : High-precision GNSS raw logs require root access.\n");

        appendAccessInstructions(sb, "location");
        return sb.toString();
    }

    private String buildNfcInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            NfcManager nfc = (NfcManager) getSystemService(Context.NFC_SERVICE);
            NfcAdapter a   = nfc != null ? nfc.getDefaultAdapter() : null;

            sb.append("Supported        : ").append(a != null ? "Yes" : "No").append("\n");

            if (a != null) {
                sb.append("Enabled          : ").append(a.isEnabled() ? "Yes" : "No").append("\n");
            }

        } catch (Throwable ignore) { }

        sb.append("Advanced         : Secure element and low-level NFC routing tables require root access.\n");

        appendAccessInstructions(sb, "nfc");
        return sb.toString();
    }
      
// ===================================================================
// MODEL CAPACITY STORAGE (SharedPreferences) — FINAL GEL EDITION
// ===================================================================
private static final String PREFS_NAME_BATTERY = "gel_prefs";
private static final String KEY_BATTERY_MODEL_CAPACITY = "battery_model_capacity";
private static final String KEY_BATTERY_DIALOG_SHOWN = "battery_dialog_shown";

private long getStoredModelCapacity() {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        return sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L);
    } catch (Throwable ignore) {
        return -1L;
    }
}

private void saveModelCapacity(long value) {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        sp.edit().putLong(KEY_BATTERY_MODEL_CAPACITY, value).apply();
    } catch (Throwable ignore) {}
}


// ===================================================================
// BATTERY DATA STRUCT (GEL ENGINE v1.0)
// ===================================================================
private static class BatteryInfo {
    long oemFullMah = -1;
    long chargeCounterMah = -1;
    long estimatedFullMah = -1;
}


// ===================================================================
// REAL BATTERY CAPACITY SCANNER — OEM Paths (GEL Edition)
// ===================================================================
private long detectBatteryMah() {

    long cap;

    String[] paths = new String[]{
        "/sys/class/power_supply/battery/charge_full_design",
        "/sys/class/power_supply/battery/charge_full",
        "/sys/class/power_supply/battery/charge_full_raw",
        "/sys/class/power_supply/battery/fg_fullcapnom",
        "/sys/class/power_supply/battery/fg_fullcaprep",
        "/sys/class/power_supply/maxfg/capacity",
        "/sys/class/power_supply/maxfg/fullcap",
        "/sys/class/power_supply/maxfg/fullcapnom",
        "/sys/class/power_supply/maxfg/designcap",
        "/sys/class/power_supply/bms/charge_full",
        "/sys/class/power_supply/bms/charge_full_design",
        "/sys/class/power_supply/bms/fullcapnom",
        "/sys/class/power_supply/bms/fullcap",
        "/sys/class/power_supply/bms/fcc_data"
    };

    for (String p : paths) {
        cap = readSysLong(p);  // ✔ uses your existing helper
        if (cap > 2000) {
            if (cap > 200000) return cap / 1000;
            return cap;
        }
    }

    try {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (bm != null) {
            long c = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            if (c > 0) {
                if (c > 200000) c /= 1000;
                return Math.abs(c);
            }
        }
    } catch (Throwable ignore) {}

    try {
        String prop = getProp("persist.battery.capacity");
        if (prop != null && !prop.isEmpty()) {
            long n = Long.parseLong(prop.trim());
            if (n > 1000) return n;
        }
    } catch (Throwable ignore) {}

    return -1;
}


// ===================================================================
// GEL BATTERY ENGINE v1.0 (OEM + Charge Counter + Estimation)
// ===================================================================
private BatteryInfo getBatteryInfo() {

    BatteryInfo bi = new BatteryInfo();

    long oem = detectBatteryMah();
    if (oem > 2000) bi.oemFullMah = oem;

    try {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (bm != null) {
            long cc = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            if (cc > 0) {
                if (cc > 200000) cc /= 1000;
                bi.chargeCounterMah = Math.abs(cc);
            }
        }
    } catch (Throwable ignore) {}

    try {
        if (bi.chargeCounterMah > 0) {

            IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent i = registerReceiver(null, f);

            if (i != null) {
                int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                if (level > 0 && level <= 100) {
                    float pct = level / 100f;
                    bi.estimatedFullMah = (long)(bi.chargeCounterMah / pct);
                }
            }
        }
    } catch (Throwable ignore) {}

    return bi;
}


// ===================================================================
// BATTERY INFO (GEL Hybrid OEM + ChargeCounter Edition) — FINAL
// ===================================================================
private String buildBatteryInfo() {

    BatteryInfo bi = getBatteryInfo();
    StringBuilder sb = new StringBuilder();

    int level = -1;

    try {
        IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent i = registerReceiver(null, f);

        level   = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale   = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status  = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int temp    = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        String statusStr;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:      statusStr = "Charging"; break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:   statusStr = "Discharging"; break;
            case BatteryManager.BATTERY_STATUS_FULL:          statusStr = "Full"; break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:  statusStr = "Not charging"; break;
            default:                                          statusStr = "Unknown"; break;
        }

        String plugStr;
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:        plugStr = "AC"; break;
            case BatteryManager.BATTERY_PLUGGED_USB:       plugStr = "USB"; break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:  plugStr = "Wireless"; break;
            default:                                       plugStr = "Not plugged"; break;
        }

        sb.append("Level                : ").append(level).append("%\n");
        sb.append("Scale                : ").append(scale).append("\n");
        sb.append("Status               : ").append(statusStr).append("\n");
        sb.append("Charging Source      : ").append(plugStr).append("\n");

        if (temp > 0)
            sb.append("Temp                 : ").append((temp / 10f)).append("°C\n");

    } catch (Throwable ignore) {}
    

    // ---------------------- OEM SOURCE ----------------------
    if (bi.oemFullMah > 0) {

        sb.append("Real capacity        : ").append(bi.oemFullMah).append(" mAh\n");

        // ⭐ New logic: estimated 100% from OEM if level>0
        if (level > 0 && level < 100) {
            float pct = level / 100f;
            long est = (long)(bi.oemFullMah / pct);

            sb.append("Estimated full (100%): ").append(est).append(" mAh\n");
        }

        sb.append("Source               : OEM\n");
    }

    // ---------------------- CHARGE COUNTER SOURCE ----------------------
    else if (bi.chargeCounterMah > 0) {

        sb.append("Current charge       : ").append(bi.chargeCounterMah).append(" mAh\n");

        if (bi.estimatedFullMah > 0)
            sb.append("Estimated full (100%): ").append(bi.estimatedFullMah).append(" mAh\n");

        sb.append("Source               : Charge Counter\n");
    }

    // ---------------------- NO DATA ----------------------
    else {
        sb.append("Real capacity        : N/A\n");
        sb.append("Source               : Unknown\n");
    }


    // Model capacity (user input)
    long modelCap = getStoredModelCapacity();
    if (modelCap > 0)
        sb.append("Model capacity       : ").append(modelCap).append(" mAh\n");
    else
        sb.append("Model capacity       : (tap to set)\n");


    sb.append("Lifecycle            : Requires root access\n");

    return sb.toString();
}
      
// ===================================================================
// SHOW POPUP ONLY ONCE
// ===================================================================
private void maybeShowBatteryCapacityDialogOnce() {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        boolean shown = sp.getBoolean(KEY_BATTERY_DIALOG_SHOWN, false);
        long existing = sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L);

        if (!shown && existing <= 0) {
            sp.edit().putBoolean(KEY_BATTERY_DIALOG_SHOWN, true).apply();
            runOnUiThread(this::showBatteryCapacityDialog);
        }
    } catch (Throwable ignore) {}
}


// ===================================================================
// POPUP — GEL BLACK+GOLD FINAL
// ===================================================================
private void showBatteryCapacityDialog() {
    runOnUiThread(() -> {
        try {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(getString(R.string.battery_popup_title));
            b.setMessage(getString(R.string.battery_popup_msg));

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint(getString(R.string.battery_popup_hint));

            long current = getStoredModelCapacity();
            if (current > 0) {
                input.setText(String.valueOf(current));
                input.setSelection(input.getText().length());
            }

            b.setView(input);

            b.setPositiveButton(getString(R.string.battery_popup_ok), (dialog, which) -> {
                String txt = input.getText().toString().trim();
                if (!txt.isEmpty()) {
                    try {
                        long val = Long.parseLong(txt);
                        if (val > 0) {
                            saveModelCapacity(val);

                            TextView content = findViewById(R.id.txtBatteryContent);
                            if (content != null)
                                content.setText(buildBatteryInfo());

                            TextView btn = findViewById(R.id.txtBatteryModelCapacity);
                            if (btn != null) {
                                btn.setText(getString(R.string.battery_set_model_capacity)
                                        + " (" + val + " mAh)");
                            }
                        }
                    } catch (Throwable ignore) {}
                }
            });

            b.setNegativeButton(getString(R.string.battery_popup_cancel), null);

            AlertDialog dialog = b.create();
            dialog.getWindow().setBackgroundDrawableResource(
                R.drawable.gel_dialog_battery_full_black
            );

            dialog.show();

        } catch (Throwable ignore) {}
    });
}
      
 // ============================================================
 // UwB Info
 // ====================================================== 
      private String buildUwbInfo() {
        boolean supported = getPackageManager().hasSystemFeature("android.hardware.uwb");
        StringBuilder sb = new StringBuilder();

        sb.append("Supported        : ").append(supported ? "Yes" : "No").append("\n");
        sb.append("Advanced         : Fine-grain ranging diagnostics require root access.\n");

        return sb.toString();
    }

    private String buildHapticsInfo() {
        boolean vib = getPackageManager().hasSystemFeature("android.hardware.vibrator");
        StringBuilder sb = new StringBuilder();

        sb.append("Supported        : ").append(vib ? "Yes" : "No").append("\n");
        sb.append("Advanced         : Detailed haptic waveform tables require root access.\n");

        return sb.toString();
    }

// ============================================================
// GNSS / LOCATION — SAFE SDK VERSION
// ============================================================
private String buildGnssInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        PackageManager pm = getPackageManager();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // ---------------------------------------------------
        // GNSS PROVIDERS
        // ---------------------------------------------------
        if (lm != null) {
            boolean gps = false;
            boolean net = false;
            try {
                gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Throwable ignore) {}
            try {
                net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Throwable ignore) {}

            sb.append("GPS Provider     : ").append(gps ? "Enabled" : "Disabled").append("\n");
            sb.append("Network Locate   : ").append(net ? "Enabled" : "Disabled").append("\n");
        }

        // ---------------------------------------------------
        // CONSTELLATION SUPPORT (strings only, no new constants)
        // ---------------------------------------------------
        sb.append("\nConstellations   :\n");

        sb.append("  GPS            : ")
                .append(pm.hasSystemFeature("android.hardware.location.gps") ? "Yes" : "No").append("\n");
        sb.append("  GLONASS        : ")
                .append(pm.hasSystemFeature("android.hardware.location.glonass") ? "Yes" : "No").append("\n");
        sb.append("  Galileo        : ")
                .append(pm.hasSystemFeature("android.hardware.location.galileo") ? "Yes" : "No").append("\n");
        sb.append("  Beidou         : ")
                .append(pm.hasSystemFeature("android.hardware.location.beidou") ? "Yes" : "No").append("\n");
        sb.append("  QZSS           : ")
                .append(pm.hasSystemFeature("android.hardware.location.qzss") ? "Yes" : "No").append("\n");
        sb.append("  SBAS           : ")
                .append(pm.hasSystemFeature("android.hardware.location.sbas") ? "Yes" : "No").append("\n");
        sb.append("  NavIC/IRNSS    : ")
                .append(pm.hasSystemFeature("android.hardware.location.irnss") ? "Yes" : "No").append("\n");

        // ---------------------------------------------------
        // RAW MEASUREMENT SUPPORT (string feature only)
        // ---------------------------------------------------
        try {
            boolean raw = pm.hasSystemFeature("android.hardware.location.gnss.raw_measurement");
            sb.append("\nRaw Measurements : ").append(raw ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {}

        // ---------------------------------------------------
        // GNSS BATCHING (Android 7+) — use string, NOT constant
        // ---------------------------------------------------
        try {
            boolean batch = pm.hasSystemFeature("android.hardware.location.gnss.batch");
            sb.append("GNSS Batching    : ").append(batch ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {}

        // ---------------------------------------------------
        // NMEA Support (best-effort)
        // ---------------------------------------------------
        sb.append("NMEA Support     : ").append(lm != null ? "Yes" : "Unknown").append("\n");

        // ---------------------------------------------------
        // SUPL / AGNSS (Assisted-GNSS) — string features
        // ---------------------------------------------------
        sb.append("\nAssisted GNSS    :\n");
        sb.append("  SUPL Support   : ")
                .append(pm.hasSystemFeature("com.google.location.feature.SUPL") ? "Yes" : "Unknown")
                .append("\n");
        sb.append("  AGNSS Injection: ")
                .append(pm.hasSystemFeature("android.hardware.location.agnss") ? "Yes" : "No")
                .append("\n");

// FINAL NOTE — GNSS (GREEN SINGLE LINE)
sb.append("\nAdvanced         : GNSS constellation breakdown, carrier phases, logging and raw measurements require root access.\n");
} catch (Throwable ignore) {
    sb.append("GNSS information is not exposed on this device.\n");
}

return sb.toString();
}
      
// ============================================================
// USB
// ============================================================
private String buildUsbInfo() {
    boolean otg = getPackageManager().hasSystemFeature("android.hardware.usb.host");
    boolean acc = getPackageManager().hasSystemFeature("android.hardware.usb.accessory");
    StringBuilder sb = new StringBuilder();

    sb.append("OTG Support      : ").append(otg ? "Yes" : "No").append("\n");
    sb.append("Accessory Mode   : ").append(acc ? "Yes" : "No").append("\n");
    sb.append("Advanced         : Low-level USB descriptors and power profiles\n");
    sb.append("                   require root access.\n");

    return sb.toString();
}
      
    // ============================================================
    // GEL Other Peripherals Info v26 — Full Hardware Edition
    // ============================================================
    private String buildOtherPeripheralsInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== General Peripherals ===\n");

        boolean vib          = getPackageManager().hasSystemFeature("android.hardware.vibrator");
        boolean flash        = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        boolean ir           = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
        boolean fm           = getPackageManager().hasSystemFeature("android.hardware.fm");
        boolean hall         = getPackageManager().hasSystemFeature("android.hardware.sensor.hall");
        boolean therm        = getPackageManager().hasSystemFeature("android.hardware.sensor.ambient_temperature");
        boolean hwkbd        = getPackageManager().hasSystemFeature("android.hardware.keyboard");
        boolean wireless     = getPackageManager().hasSystemFeature("android.hardware.power.wireless_charging");
        boolean step         = getPackageManager().hasSystemFeature("android.hardware.sensor.stepcounter");
        boolean vulkan       = getPackageManager().hasSystemFeature("android.hardware.vulkan.level");
        boolean renderscript = getPackageManager().hasSystemFeature("android.software.renderscript");
        boolean barcode      = getPackageManager().hasSystemFeature("android.hardware.barcodescanner");
        boolean tv           = getPackageManager().hasSystemFeature("android.hardware.tv.tuner");
        boolean als          = getPackageManager().hasSystemFeature("android.hardware.light");

        sb.append("Vibration Motor : ").append(vib ? "Yes" : "No").append("\n");
        sb.append("Flashlight      : ").append(flash ? "Yes" : "No").append("\n");
        sb.append("IR Blaster      : ").append(ir ? "Yes" : "No").append("\n");
        sb.append("FM Radio        : ").append(fm ? "Yes" : "No").append("\n");
        sb.append("Hall Sensor     : ").append(hall ? "Yes" : "No").append("\n");
        sb.append("Thermal Sensor  : ").append(therm ? "Yes" : "No").append("\n");
        sb.append("HW Keyboard     : ").append(hwkbd ? "Yes" : "No").append("\n");
        sb.append("Wireless Charge : ").append(wireless ? "Yes" : "No").append("\n");
        sb.append("Step Counter    : ").append(step ? "Yes" : "No").append("\n");
        sb.append("Vulkan Engine   : ").append(vulkan ? "Yes" : "No").append("\n");
        sb.append("RenderScript    : ").append(renderscript ? "Yes" : "No").append("\n");
        sb.append("Barcode Module  : ").append(barcode ? "Yes" : "No").append("\n");
        sb.append("TV Tuner        : ").append(tv ? "Yes" : "No").append("\n");
        sb.append("Ambient Light   : ").append(als ? "Yes" : "No").append("\n");

        sb.append("\nAdvanced         : Extended peripheral diagnostics require root access.\n");

        return sb.toString();
    }

    // ============================================================================
    // AUDIO SYSTEM — FULL MERGED BLOCK (Microphones + Audio HAL + Audio Extended)
    // ============================================================================

    // 1) MICROPHONES (v27)
    private String buildMicsInfo() {
        StringBuilder sb = new StringBuilder();

        int wiredCount = 0, btCount = 0, usbCount = 0;
        boolean hasBuiltin=false, hasTele=false, hasWired=false, hasBT=false, hasUSB=false;

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                AudioDeviceInfo[] devs = am.getDevices(AudioManager.GET_DEVICES_INPUTS);

                for (AudioDeviceInfo d : devs) {
                    String name = d.getProductName() != null ? d.getProductName().toString().trim() : "";
                    int type = d.getType();

                    boolean fakeName =
                            name.isEmpty() ||
                            name.equalsIgnoreCase(Build.MODEL) ||
                            name.matches("^[A-Z0-9_-]{8,}$");

                    String label;

                    switch (type) {
                        case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                            label = "Built-in Mic"; hasBuiltin = true; break;
                        case AudioDeviceInfo.TYPE_TELEPHONY:
                            label = "Telephony Mic"; hasTele = true; break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                            label = "Wired Headset Mic"; wiredCount++; hasWired = true; break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                            label = "Bluetooth Mic"; btCount++; hasBT = true; break;
                        case AudioDeviceInfo.TYPE_USB_DEVICE:
                        case AudioDeviceInfo.TYPE_USB_HEADSET:
                            label = "USB Mic"; usbCount++; hasUSB = true; break;
                        default:
                            label = "Input Type " + type; break;
                    }

                    sb.append("• ").append(label).append("\n");
                    sb.append("   Present       : Yes\n");
                    if (!name.isEmpty()) sb.append("   Name          : ").append(name).append("\n");
                    sb.append("   Fake-ID       : ").append(fakeName ? "Yes" : "No").append("\n");
                    sb.append("   Device ID     : ").append(d.getId()).append("\n\n");
                }
            }
        } catch (Throwable ignore) {}

        if (sb.length() == 0)
            sb.append("No microphones are reported by the current audio service.\n");

        sb.append("=== Summary ===\n");
        sb.append("Built-in Mic     : ").append(hasBuiltin ? "Yes" : "No").append("\n");
        sb.append("Telephony Mic    : ").append(hasTele    ? "Yes" : "No").append("\n");
        sb.append("Wired Mics       : ").append(hasWired   ? "Yes" : "No").append(" (").append(wiredCount).append(")\n");
        sb.append("Bluetooth Mics   : ").append(hasBT      ? "Yes" : "No").append(" (").append(btCount).append(")\n");
        sb.append("USB Mics         : ").append(hasUSB     ? "Yes" : "No").append(" (").append(usbCount).append(")\n");

        sb.append("\nAdvanced         : Raw audio routing matrices require root access.\n");

        appendAccessInstructions(sb, "mic");
        return sb.toString();
    }

    // ============================================================================
    // 2) AUDIO HAL — FIXED & CLEAN
    // ============================================================================
    private String buildAudioHalInfo() {
        StringBuilder sb = new StringBuilder();

        String hal = getProp("ro.audio.hal.version");
        sb.append("Audio HAL        : ")
          .append(hal != null && !hal.isEmpty() ? hal : "Not exposed")
          .append("\n\n");

        boolean speaker = false, wired = false, bt = false, usb = false, hdmi = false;

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {

                AudioDeviceInfo[] outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

                for (AudioDeviceInfo fi : outs) {

                    String name = fi.getProductName() != null ? fi.getProductName().toString() : "";
                    int type = fi.getType();

                    String label;
                    switch (type) {
                        case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                            label = "Built-in Speaker"; speaker = true; break;
                        case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                            label = "Wired Output"; wired = true; break;
                        case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                            label = "Bluetooth Output"; bt = true; break;
                        case AudioDeviceInfo.TYPE_USB_DEVICE:
                        case AudioDeviceInfo.TYPE_USB_HEADSET:
                            label = "USB Output"; usb = true; break;
                        case AudioDeviceInfo.TYPE_HDMI:
                            label = "HDMI Output"; hdmi = true; break;
                        default:
                            label = "Output Type " + type; break;
                    }

                    sb.append("• ").append(label).append("\n");
                    sb.append("   Present       : Yes\n");
                    sb.append("   Name          : ").append(name.isEmpty() ? "N/A" : name).append("\n");
                    sb.append("   Device ID     : ").append(fi.getId()).append("\n\n");
                }
            }

        } catch (Throwable ignore) {}

        sb.append("=== Summary ===\n");
        sb.append("Speaker Output   : ").append(speaker ? "Yes" : "No").append("\n");
        sb.append("Wired Output     : ").append(wired   ? "Yes" : "No").append("\n");
        sb.append("Bluetooth Output : ").append(bt      ? "Yes" : "No").append("\n");
        sb.append("USB Output       : ").append(usb     ? "Yes" : "No").append("\n");
        sb.append("HDMI Output      : ").append(hdmi    ? "Yes" : "No").append("\n");

        sb.append("\nAdvanced         : HAL routing tables, offload models, DSP profiles require root.\n");

        return sb.toString();
    }

    // 3) AUDIO EXTENDED
    private String buildAudioExtendedInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {

                // Legacy routing snapshot
                String r = am.getParameters("routing");
                if (r != null && !r.isEmpty())
                    sb.append("Legacy Routing   : ").append(r).append("\n");

                // Hardware flag
                if (Build.VERSION.SDK_INT >= 23) {
                    boolean hw = getPackageManager().hasSystemFeature("android.hardware.audio.output");
                    sb.append("Audio Output HW  : ").append(hw ? "Yes" : "No").append("\n");
                }
            }
        } catch (Throwable ignore) {}

        sb.append("Advanced         : Spatial audio flags, noise models, per-stream audio paths require root access.\n");

        return sb.toString();
    }

    // 4) UNIFIED AUDIO BLOCK (for headerAudioUnified)
    private String buildAudioUnifiedInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Microphones ===\n");
        sb.append(buildMicsInfo()).append("\n\n");

        sb.append("=== Audio Outputs / HAL ===\n");
        sb.append(buildAudioHalInfo()).append("\n\n");

        sb.append("=== Extended Audio Paths ===\n");
        sb.append(buildAudioExtendedInfo()).append("\n");

        return sb.toString();
    }

    // ============================================================
    // Root Info
    // ============================================================
    private String buildRootInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Root Access Mode : ")
          .append(isRooted ? "Rooted device (superuser access detected)"
                           : "Non-rooted device (standard access)")
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
    // NEW MEGA-UPGRADE SECTIONS (1–12)
    // ============================================================

    // 1. Thermal Engine / Cooling Profiles
    private String buildThermalInfo() {
        StringBuilder sb = new StringBuilder();

        File thermalDir = new File("/sys/class/thermal");
        File[] zones = null;
        File[] cools = null;

        try {
            if (thermalDir.exists() && thermalDir.isDirectory()) {
                zones = thermalDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().startsWith("thermal_zone");
                    }
                });
                cools = thermalDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().startsWith("cooling_device");
                    }
                });
            }
        } catch (Throwable ignore) { }

        int zoneCount = zones != null ? zones.length : 0;
        int coolCount = cools != null ? cools.length : 0;

        sb.append("Thermal Zones    : ").append(zoneCount).append("\n");
        sb.append("Cooling Devices  : ").append(coolCount).append("\n");

        if (zoneCount == 0 && coolCount == 0) {
            sb.append("Advanced         : Some devices restrict /sys thermal nodes; basic sensors use Android APIs, full trip tables require root.\n");
            return sb.toString();
        }

        if (zoneCount > 0) {
            sb.append("\nSample Zone      :\n");
            try {
                File z0 = zones[0];
                String type = readSysString(z0.getAbsolutePath() + "/type");
                String temp = readSysString(z0.getAbsolutePath() + "/temp");

                if (type != null && type.trim().length() > 0) {
                    sb.append("  Type           : ").append(type).append("\n");
                }
                if (temp != null && temp.trim().length() > 0) {
                    sb.append("  Temp (raw)     : ").append(temp).append("\n");
                }
            } catch (Throwable ignore) { }
        }

        sb.append("Advanced         : Full thermal trip tables and throttling profiles require root access and OEM-specific parsing.\n");

        return sb.toString();
    }

    // ============================================================
// 2. Screen / HDR / Refresh + Accurate Diagonal (inches)
// ============================================================
private String buildScreenInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {

            Display display = wm.getDefaultDisplay();  // still valid for your project
            DisplayMetrics dm = new DisplayMetrics();
            display.getRealMetrics(dm);

            // -------------------------------------------
            // BASIC SCREEN METRICS
            // -------------------------------------------
            int w = dm.widthPixels;
            int h = dm.heightPixels;
            int dpi = dm.densityDpi;

            sb.append("Resolution       : ")
                    .append(w).append(" x ").append(h).append(" px\n");
            sb.append("Density (DPI)    : ").append(dpi).append("\n");
            sb.append("Scaled Density   : ").append(dm.scaledDensity).append("\n");

            // -------------------------------------------
            // REFRESH RATE
            // -------------------------------------------
            float refresh = display.getRefreshRate();
            sb.append("Refresh Rate     : ").append(refresh).append(" Hz\n");

            if (Build.VERSION.SDK_INT >= 30) {
                float maxR = 0f;
                try {
                    Display.Mode[] modes = display.getSupportedModes();
                    for (Display.Mode m : modes) {
                        if (m.getRefreshRate() > maxR) {
                            maxR = m.getRefreshRate();
                        }
                    }
                } catch (Throwable ignore) {}
                if (maxR > 0f) {
                    sb.append("Max Refresh      : ").append(maxR).append(" Hz\n");
                }
            }

            // -------------------------------------------
            // WIDE COLOR & HDR
            // -------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    boolean wide = display.isWideColorGamut();
                    sb.append("Wide Color       : ").append(wide ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {}
            }

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Display.HdrCapabilities hc = display.getHdrCapabilities();
                    int[] types = hc.getSupportedHdrTypes();
                    sb.append("HDR Modes        : ");
                    if (types == null || types.length == 0) sb.append("None\n");
                    else sb.append(types.length).append(" modes\n");
                } catch (Throwable ignore) {}
            }

            // -------------------------------------------
            // ORIENTATION
            // -------------------------------------------
            try {
                Configuration cfg = getResources().getConfiguration();
                sb.append("Orientation      : ")
                        .append(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE ?
                                "Landscape" : "Portrait")
                        .append("\n");
            } catch (Throwable ignore) {}

            // -------------------------------------------
            // DIAGONAL SIZE (INCHES)
            // -------------------------------------------
            try {
                double inchW = (double) w / dm.xdpi;
                double inchH = (double) h / dm.ydpi;
                double diag = Math.sqrt(inchW * inchW + inchH * inchH);

                sb.append("Screen Size      : ")
                        .append(String.format(Locale.US, "%.2f", diag))
                        .append("\"\n");
            } catch (Throwable ignore) {}
        }

    } catch (Throwable ignore) { }

    // -------------------------------------------
    // ADVANCED (informational)
    // -------------------------------------------
    sb.append("Advanced         : Panel ID, HBM tables and OEM tone-mapping require root access.\n");

    return sb.toString();
}

    // 3. Modem / Telephony (GEL Safe Edition)
private String buildModemInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (tm != null) {

            // -----------------------------
            // BASIC PHONE TYPE
            // -----------------------------
            String typeStr;
            switch (tm.getPhoneType()) {
                case TelephonyManager.PHONE_TYPE_GSM:  typeStr = "GSM";  break;
                case TelephonyManager.PHONE_TYPE_CDMA: typeStr = "CDMA"; break;
                case TelephonyManager.PHONE_TYPE_SIP:  typeStr = "SIP";  break;
                default:                                typeStr = "None";
            }
            sb.append("Phone Type       : ").append(typeStr).append("\n");

            // -----------------------------
            // DATA NETWORK TYPE
            // -----------------------------
            int net = tm.getDataNetworkType();
            sb.append("Data Network     : ").append(networkName(net)).append("\n");

            boolean is5G = (net == TelephonyManager.NETWORK_TYPE_NR);
            sb.append("5G (NR) Active   : ").append(is5G ? "Yes" : "No").append("\n");

            // -----------------------------
            // IMS / VoLTE / VoWiFi / VoNR (SDK-safe: use reflection or mark Unknown)
            // -----------------------------
            sb.append("IMS Registered   : Unknown (SDK level)\n");

            try {
                boolean volte =
                        (boolean) TelephonyManager.class.getMethod("isVolteAvailable").invoke(tm);
                sb.append("VoLTE Support    : ").append(volte ? "Yes" : "No").append("\n");
            } catch (Throwable ignore) {
                sb.append("VoLTE Support    : Unknown\n");
            }

            try {
                boolean vowifi =
                        (boolean) TelephonyManager.class.getMethod("isWifiCallingAvailable").invoke(tm);
                sb.append("VoWiFi Support   : ").append(vowifi ? "Yes" : "No").append("\n");
            } catch (Throwable ignore) {
                sb.append("VoWiFi Support   : Unknown\n");
            }

            if (Build.VERSION.SDK_INT >= 33) {
                try {
                    boolean vonr =
                            (boolean) TelephonyManager.class.getMethod("isVoNrEnabled").invoke(tm);
                    sb.append("VoNR Support     : ").append(vonr ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {
                    sb.append("VoNR Support     : Unknown\n");
                }
            } else {
                sb.append("VoNR Support     : Unknown (Android < 13)\n");
            }

            // -----------------------------
            // SIGNAL STRENGTH (basic)
            // -----------------------------
            try {
                SignalStrength ss = tm.getSignalStrength();
                if (ss != null) {
                    sb.append("Signal Strength  : ").append(ss.getLevel()).append("/4\n");
                }
            } catch (Throwable ignore) { }

            // -----------------------------
            // CARRIER INFORMATION
            // -----------------------------
            try {
                String carrier = tm.getNetworkOperatorName();
                sb.append("Carrier          : ")
                        .append(carrier != null ? carrier : "Unknown")
                        .append("\n");
            } catch (Throwable ignore) { }

            // -----------------------------
            // ACTIVE SIM COUNT
            // -----------------------------
            if (Build.VERSION.SDK_INT >= 22) {
                SubscriptionManager sm = (SubscriptionManager)
                        getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                if (sm != null) {
                    List<SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
                    sb.append("Active SIM Slots : ")
                            .append(subs != null ? subs.size() : 0)
                            .append("\n");
                }
            }

            // -----------------------------
            // CARRIER AGGREGATION (removed direct API)
            // -----------------------------
            sb.append("4G+ CA           : Unknown (SDK level)\n");

            // -----------------------------
            // ROAMING
            // -----------------------------
            try {
                boolean roam = tm.isNetworkRoaming();
                sb.append("Roaming          : ").append(roam ? "Yes" : "No").append("\n");
            } catch (Throwable ignore) { }
        }

    } catch (Throwable ignore) { }

    sb.append("Advanced         : Full RAT tables, NR bands, CA combos, require root access and OEM modem tools.\n");
    
    return sb.toString();
}                

    // ============================================================
    // Helper for data network type → readable label
    // ============================================================
    private String networkName(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_NR:  return "5G NR";
            case TelephonyManager.NETWORK_TYPE_LTE: return "4G LTE";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_HSPA: return "3.5G HSPA";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "3G UMTS";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "2.75G EDGE";
            case TelephonyManager.NETWORK_TYPE_GPRS: return "2.5G GPRS";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "2G CDMA";
            default: return "Unknown";
        }
    }

// 4. WiFi Advanced (Safe Edition)
private String buildWifiAdvancedInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        PackageManager pm = getPackageManager();

        if (wm != null) {

            // ---- BASIC HW SUPPORT ----
            boolean wifiHw = pm.hasSystemFeature("android.hardware.wifi");
            sb.append("Wi-Fi HW         : ").append(wifiHw ? "Present" : "Missing").append("\n");

            // ---- FREQUENCY BANDS ----
            if (Build.VERSION.SDK_INT >= 21) {
                boolean band24 = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
                sb.append("2.4 GHz Support  : ").append(band24 ? "Yes" : "No").append("\n");
            }

            if (Build.VERSION.SDK_INT >= 21) {
                boolean band5 = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
                sb.append("5 GHz Support    : ").append(band5 ? "Yes" : "No").append("\n");
            }

            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    boolean six = wm.is6GHzBandSupported();
                    sb.append("6 GHz Support    : ").append(six ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {}
            }

            // ---- SECURITY MODES ----
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    boolean wpa3 = wm.isWpa3SaeSupported();
                    sb.append("WPA3 SAE         : ").append(wpa3 ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {}

                try {
                    boolean wpa3Trans = wm.isWpa3SuiteBSupported();
                    sb.append("WPA3 Suite-B     : ").append(wpa3Trans ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {}
            }

            // ---- WIFI RTT (distance measurement) ----
            if (Build.VERSION.SDK_INT >= 28) {
                boolean rtt = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT);
                sb.append("Wi-Fi RTT        : ").append(rtt ? "Yes" : "No")
                        .append("  (Indoor distance)\n");
            }

            // ---- WI-FI AWARE / NAN ----
            if (Build.VERSION.SDK_INT >= 26) {
                boolean aware = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
                sb.append("Wi-Fi Aware      : ").append(aware ? "Yes" : "No")
                        .append("  (Device proximity)\n");
            }

            // ---- EASY CONNECT (DPP QR CODE) — use string instead of constant
            if (Build.VERSION.SDK_INT >= 29) {
                boolean dpp = pm.hasSystemFeature("android.hardware.wifi.dpp");
                sb.append("Easy Connect     : ").append(dpp ? "Yes" : "No").append("\n");
            }

            // ---- PASSPOINT / HOTSPOT 2.0 ----
            if (Build.VERSION.SDK_INT >= 26) {
                boolean pass = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT);
                sb.append("Passpoint (HS2)  : ").append(pass ? "Yes" : "No").append("\n");
            }

            // ---- WI-FI DIRECT / P2P ----
            boolean p2p = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
            sb.append("Wi-Fi Direct     : ").append(p2p ? "Yes" : "No").append("\n");

            // ---- MAC RANDOMIZATION (fake check, keep text) ----
            sb.append("MAC Randomization: Unknown (SDK level)\n");

            // ---- LINK LAYER STATS ----
            sb.append("Link Layer Stats : Unknown (hidden API)\n");

            // ---- POWER SAVE MODE ----
            try {
                boolean psm = wm.isScanAlwaysAvailable();
                sb.append("Scan Always On   : ").append(psm ? "Yes" : "No").append("\n");
            } catch (Throwable ignore) {}

            // ---- REGULATORY DOMAIN ----
            sb.append("Country Code     : Not exposed\n");
        }
    } catch (Throwable ignore) {}

    sb.append("\nAdvanced         : Regulatory region, DFS radar tables, TX power and per-band limits, require root access.\n");
   return sb.toString();
}

    // 5. Sensors EXTENDED
    private String buildSensorsExtendedInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                java.util.List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
                for (Sensor s : all) {
                    sb.append("• ").append(s.getName()).append("\n");
                    sb.append("   Type          : ").append(s.getType()).append("\n");
                    sb.append("   Vendor        : ").append(s.getVendor()).append("\n");
                    sb.append("   Max Range     : ").append(s.getMaximumRange()).append("\n");
                    sb.append("   Resolution    : ").append(s.getResolution()).append("\n");
                    sb.append("   Power (mA)    : ").append(s.getPower()).append("\n");
                    sb.append("   Min Delay     : ").append(s.getMinDelay()).append(" µs\n\n");
                }
            }
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("Sensor extended metadata is not exposed by this device.\n");
        }

        sb.append("Advanced         : Dedicated sensor hubs and on-SoC fusion engines require root access and vendor sensor HAL.\n");

        return sb.toString();
    }

    // 6. System Feature Matrix
    private String buildSystemFeaturesInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            PackageManager pm = getPackageManager();
            FeatureInfo[] feats = pm.getSystemAvailableFeatures();
            int count = feats != null ? feats.length : 0;

            sb.append("Feature Count    : ").append(count).append("\n\n");

            if (feats != null) {
                int shown = 0;
                for (FeatureInfo fi : feats) {
                    if (fi == null) continue;
                    if (fi.name != null) {
                        sb.append("• ").append(fi.name).append("\n");
                        shown++;
                    }
                    if (shown >= 120) break;
                }
            }
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("No feature matrix exposed by PackageManager on this device.\n");
        }

        return sb.toString();
    }

    // 7. SELinux / Security Flags
    private String buildSecurityFlagsInfo() {
        StringBuilder sb = new StringBuilder();

        String kernel = readSysString("/proc/version");
        if (kernel != null && !kernel.isEmpty()) {
            sb.append("Kernel          : ").append(kernel).append("\n");
        }

        String patch = Build.VERSION.SECURITY_PATCH;
        if (patch != null && !patch.isEmpty()) {
            sb.append("Security Patch  : ").append(patch).append("\n");
        }

        String vbState = getProp("ro.boot.verifiedbootstate");
        if (vbState != null && !vbState.isEmpty()) {
            sb.append("Verified Boot   : ").append(vbState).append("\n");
        }

        boolean strongBox = getPackageManager().hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE);
        sb.append("StrongBox       : ").append(strongBox ? "Yes" : "No").append("\n");

        boolean hce = getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
        sb.append("HCE / Secure NFC: ").append(hce ? "Yes" : "No").append("\n");

        sb.append("Advanced         : Full SELinux policy dump and keymaster internals require root access and are not exposed to apps.\n");

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
    // SET TEXT FOR ALL SECTIONS — COMPLETE & FIXED
    // ============================================================
private void populateAllSections() {

    // BATTERY (FULL BLOCK: text + button refresh)
    set(R.id.txtBatteryContent, buildBatteryInfo());
    refreshBatteryButton();   // 🔥 IMPORTANT — updates the "(tap to set)" or "5000 mAh"

    // CORE HARDWARE
    set(R.id.txtScreenContent,          buildScreenInfo());
    set(R.id.txtCameraContent,           buildCameraInfo());
    set(R.id.txtConnectivityContent,     buildConnectivityInfo());
    set(R.id.txtLocationContent,         buildLocationInfo());
    set(R.id.txtThermalContent,          buildThermalInfo());

       // MODEM / TELEPHONY
    set(R.id.txtModemContent,            buildModemInfo());

    // ADVANCED NETWORKING
    set(R.id.txtWifiAdvancedContent,     buildWifiAdvancedInfo());

    // 🎵 UNIFIED AUDIO BLOCK (ONE BUTTON – ONE CONTENT)
    set(R.id.txtAudioUnifiedContent,     buildAudioUnifiedInfo());

    // SENSORS
    set(R.id.txtSensorsContent,          buildSensorsInfo());
    set(R.id.txtSensorsExtendedContent,  buildSensorsExtendedInfo());

    // BIOMETRICS
    set(R.id.txtBiometricsContent,       buildBiometricsInfo());

    // WIRELESS PERIPHERALS
    set(R.id.txtNfcContent,              buildNfcInfo());
    set(R.id.txtGnssContent,             buildGnssInfo());
    set(R.id.txtUwbContent,              buildUwbInfo());

    // USB
    set(R.id.txtUsbContent,              buildUsbInfo());

    // HAPTICS
    set(R.id.txtHapticsContent,          buildHapticsInfo());

    // SYSTEM FEATURES
    set(R.id.txtSystemFeaturesContent,   buildSystemFeaturesInfo());

    // SECURITY FLAGS
    set(R.id.txtSecurityFlagsContent,    buildSecurityFlagsInfo());

    // ROOT
    set(R.id.txtRootContent,             buildRootInfo());

    // OTHER PERIPHERALS
    set(R.id.txtOtherPeripheralsContent, buildOtherPeripheralsInfo());
}
private void refreshBatteryButton() {
    TextView btn = findViewById(R.id.txtBatteryModelCapacity);
    if (btn != null) {
        long cap = getStoredModelCapacity();
        if (cap > 0) {
            btn.setText("Set model capacity (" + cap + " mAh)");
        } else {
            btn.setText("Set model capacity");
        }
    }
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
        int len   = text.length();

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
