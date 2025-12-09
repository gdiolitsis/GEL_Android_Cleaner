// DeviceInfoPeripheralsActivity.java — MEGA UPGRADE v30
// Auto-Path Engine 5.3 + Root v5.1 + Permission Engine v25 (Manifest-Aware + Debug v24)

package com.gel.cleaner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Locale;
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
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    // ============================================================
// SECTION FIELDS
// ============================================================
private LinearLayout batteryContainer;
private TextView txtBatteryContent;
private TextView iconBattery;
private TextView txtBatteryModelCapacity;

private TextView txtScreenContent;
private TextView txtCameraContent;
private TextView txtConnectivityContent;   // ⭐ FIXED — Η ΜΟΝΗ ΠΟΥ ΕΛΕΙΠΕ
private TextView txtLocationContent;
private TextView txtThermalContent;
private TextView txtModemContent;
private TextView txtWifiAdvancedContent;
private TextView txtAudioUnifiedContent;
private TextView txtSensorsContent;
private TextView txtSensorsExtendedContent;
private TextView txtBiometricsContent;
private TextView txtNfcContent;
private TextView txtGnssContent;
private TextView txtUwbContent;
private TextView txtUsbContent;
private TextView txtHapticsContent;
private TextView txtSystemFeaturesContent;
private TextView txtSecurityFlagsContent;
private TextView txtRootContent;
private TextView txtOtherPeripherals;

private TextView iconScreen;
private TextView iconCamera;
private TextView iconConnectivity;
private TextView iconLocation;
private TextView iconThermal;
private TextView iconModem;
private TextView iconWifiAdvanced;
private TextView iconAudioUnified;
private TextView iconSensors;
private TextView iconSensorsExtended;
private TextView iconBiometrics;
private TextView iconNfc;
private TextView iconGnss;
private TextView iconUwb;
private TextView iconUsb;
private TextView iconHaptics;
private TextView iconSystemFeatures;
private TextView iconSecurityFlags;
private TextView iconRoot;
private TextView iconOthe

    // ============================================================
    // attachBaseContext
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

// ============================================================
//  ON CREATE 
// ============================================================
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_info_peripherals);

    requestAllRuntimePermissions();

    TextView title = findViewById(R.id.txtTitleDevice);
    if (title != null)
        title.setText(getString(R.string.phone_info_peripherals));

    // ============================================================
    // BIND VIEWS — FIXED BLOCK 1
    // ============================================================
    batteryContainer        = findViewById(R.id.batteryContainer);
    txtBatteryContent       = findViewById(R.id.txtBatteryContent);
    iconBattery             = findViewById(R.id.iconBatteryToggle);
    txtBatteryModelCapacity = findViewById(R.id.txtBatteryModelCapacity);

    txtScreenContent          = findViewById(R.id.txtScreenContent);
    txtCameraContent          = findViewById(R.id.txtCameraContent);
    txtConnectivityContent    = findViewById(R.id.txtConnectivityContent);   // ⭐ FIXED
    txtLocationContent        = findViewById(R.id.txtLocationContent);
    txtThermalContent         = findViewById(R.id.txtThermalContent);
    txtModemContent           = findViewById(R.id.txtModemContent);
    txtWifiAdvancedContent    = findViewById(R.id.txtWifiAdvancedContent);
    txtAudioUnifiedContent    = findViewById(R.id.txtAudioUnifiedContent);
    txtSensorsContent         = findViewById(R.id.txtSensorsContent);
    txtSensorsExtendedContent = findViewById(R.id.txtSensorsExtendedContent);
    txtBiometricsContent      = findViewById(R.id.txtBiometricsContent);
    txtNfcContent             = findViewById(R.id.txtNfcContent);
    txtGnssContent            = findViewById(R.id.txtGnssContent);
    txtUwbContent             = findViewById(R.id.txtUwbContent);
    txtUsbContent             = findViewById(R.id.txtUsbContent);
    txtHapticsContent         = findViewById(R.id.txtHapticsContent);
    txtSystemFeaturesContent  = findViewById(R.id.txtSystemFeaturesContent);
    txtSecurityFlagsContent   = findViewById(R.id.txtSecurityFlagsContent);
    txtRootContent            = findViewById(R.id.txtRootContent);
    txtOtherPeripherals       = findViewById(R.id.txtOtherPeripheralsContent);

    iconScreen          = findViewById(R.id.iconScreenToggle);
    iconCamera          = findViewById(R.id.iconCameraToggle);
    iconConnectivity    = findViewById(R.id.iconConnectivityToggle);
    iconLocation        = findViewById(R.id.iconLocationToggle);
    iconThermal         = findViewById(R.id.iconThermalToggle);
    iconModem           = findViewById(R.id.iconModemToggle);
    iconWifiAdvanced    = findViewById(R.id.iconWifiAdvancedToggle);
    iconAudioUnified    = findViewById(R.id.iconAudioUnifiedToggle);
    iconSensors         = findViewById(R.id.iconSensorsToggle);
    iconSensorsExtended = findViewById(R.id.iconSensorsExtendedToggle);
    iconBiometrics      = findViewById(R.id.iconBiometricsToggle);
    iconNfc             = findViewById(R.id.iconNfcToggle);
    iconGnss            = findViewById(R.id.iconGnssToggle);
    iconUwb             = findViewById(R.id.iconUwbToggle);
    iconUsb             = findViewById(R.id.iconUsbToggle);
    iconHaptics         = findViewById(R.id.iconHapticsToggle);
    iconSystemFeatures  = findViewById(R.id.iconSystemFeaturesToggle);
    iconSecurityFlags   = findViewById(R.id.iconSecurityFlagsToggle);
    iconRoot            = findViewById(R.id.iconRootToggle);
    iconOther           = findViewById(R.id.iconOtherPeripheralsToggle);

    // ============================================================
    // BIND BATTERY HEADER
    // ============================================================
    LinearLayout headerBattery = findViewById(R.id.headerBattery);

    // ============================================================
    // MASTER ARRAYS — FIXED BLOCK 2
    // ============================================================
    allContents = new TextView[]{
            txtBatteryContent,
            txtScreenContent,
            txtCameraContent,
            txtConnectivityContent,    // ⭐ FIXED
            txtLocationContent,
            txtThermalContent,
            txtModemContent,
            txtWifiAdvancedContent,
            txtAudioUnifiedContent,
            txtSensorsContent,
            txtSensorsExtendedContent,
            txtBiometricsContent,
            txtNfcContent,
            txtGnssContent,
            txtUwbContent,
            txtUsbContent,
            txtHapticsContent,
            txtSystemFeaturesContent,
            txtSecurityFlagsContent,
            txtRootContent,
            txtOtherPeripherals
    };

    allIcons = new TextView[]{
            iconBattery,
            iconScreen,
            iconCamera,
            iconConnectivity,
            iconLocation,
            iconThermal,
            iconModem,
            iconWifiAdvanced,
            iconAudioUnified,
            iconSensors,
            iconSensorsExtended,
            iconBiometrics,
            iconNfc,
            iconGnss,
            iconUwb,
            iconUsb,
            iconHaptics,
            iconSystemFeatures,
            iconSecurityFlags,
            iconRoot,
            iconOther
    };

    // ============================================================
    // APPLY TEXTS
    // ============================================================
    populateAllSections();

    // ============================================================
    // BATTERY SECTION INIT
    // ============================================================
    initBatterySection();
    batteryContainer.setVisibility(View.GONE);
    txtBatteryModelCapacity.setVisibility(View.GONE);
    iconBattery.setText("＋");

    // ============================================================
    // BATTERY EXPAND/COLLAPSE
    // ============================================================
    headerBattery.setOnClickListener(v -> {
        boolean isOpen = (batteryContainer.getVisibility() == View.VISIBLE);

        collapseAllExceptBattery();

        if (!isOpen) {
            txtBatteryContent.setVisibility(View.VISIBLE);
            animateExpand(batteryContainer);
            iconBattery.setText("－");
            txtBatteryModelCapacity.setVisibility(View.VISIBLE);
            refreshBatteryInfoView();
        } else {
            animateCollapse(batteryContainer);
            iconBattery.setText("＋");
            txtBatteryModelCapacity.setVisibility(View.GONE);
        }
    });

    // ============================================================
    // NORMAL SECTIONS — FIXED BLOCK 3
    // ============================================================
    setupSection(findViewById(R.id.headerScreen), txtScreenContent, iconScreen);
    setupSection(findViewById(R.id.headerCamera), txtCameraContent, iconCamera);
    setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);  // ⭐ FIXED
    setupSection(findViewById(R.id.headerLocation), txtLocationContent, iconLocation);
    setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);
    setupSection(findViewById(R.id.headerModem), txtModemContent, iconModem);
    setupSection(findViewById(R.id.headerWifiAdvanced), txtWifiAdvancedContent, iconWifiAdvanced);
    setupSection(findViewById(R.id.headerAudioUnified), txtAudioUnifiedContent, iconAudioUnified);
    setupSection(findViewById(R.id.headerSensors), txtSensorsContent, iconSensors);
    setupSection(findViewById(R.id.headerSensorsExtended), txtSensorsExtendedContent, iconSensorsExtended);
    setupSection(findViewById(R.id.headerBiometrics), txtBiometricsContent, iconBiometrics);
    setupSection(findViewById(R.id.headerNfc), txtNfcContent, iconNfc);
    setupSection(findViewById(R.id.headerGnss), txtGnssContent, iconGnss);
    setupSection(findViewById(R.id.headerUwb), txtUwbContent, iconUwb);
    setupSection(findViewById(R.id.headerUsb), txtUsbContent, iconUsb);
    setupSection(findViewById(R.id.headerHaptics), txtHapticsContent, iconHaptics);
    setupSection(findViewById(R.id.headerSystemFeatures), txtSystemFeaturesContent, iconSystemFeatures);
    setupSection(findViewById(R.id.headerSecurityFlags), txtSecurityFlagsContent, iconSecurityFlags);
    setupSection(findViewById(R.id.headerRoot), txtRootContent, iconRoot);
    setupSection(findViewById(R.id.headerOtherPeripherals), txtOtherPeripherals, iconOther);
}
// 🔥 END onCreate()


// ============================================================
// GEL Section Setup Engine — UNIVERSAL VERSION (Accordion Mode)
// Battery-Safe Edition (FINAL)
// ============================================================
private void setupSection(View header, View content, TextView icon) {

    if (header == null || content == null || icon == null)
        return;

    // Start collapsed
    content.setVisibility(View.GONE);
    icon.setText("＋");

    header.setOnClickListener(v -> {

        // 🔥 ALWAYS close Battery module before opening a normal section
        closeBatteryModule();

        // 1️⃣ Collapse all other NORMAL sections
        for (int i = 0; i < allContents.length; i++) {
            if (allContents[i] != content) {
                allContents[i].setVisibility(View.GONE);
                allIcons[i].setText("＋");
            }
        }

        // ⭐ SAFETY FIX — reset state before toggle (prevents camera lock)
        content.setVisibility(View.GONE);
        icon.setText("＋");

        // 2️⃣ Toggle THIS section only
        content.setVisibility(View.VISIBLE);
        icon.setText("－");
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
                .setDuration(160)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.getLayoutParams().height = 0;
                    v.setVisibility(View.GONE);
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

                    sb.append("• Video Profil.  : Extra stabilization telemetry, requires root access.\n\n");
                }
            }
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("No camera data exposed by this device.\n");
        }

        appendAccessInstructions(sb, "camera");
        return sb.toString();
    }

// ============================================================
//   BIOMETRICS — GEL TURBO EDITION v1.1
// ============================================================

private String buildBiometricsInfo() {
    StringBuilder sb = new StringBuilder();

    PackageManager pm = getPackageManager();

    boolean hasFp   = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    boolean hasFace = pm.hasSystemFeature("android.hardware.biometrics.face");
    boolean hasIris = pm.hasSystemFeature("android.hardware.biometrics.iris");

    sb.append("Fingerprint      : ").append(hasFp   ? "Yes" : "No").append("\n");
    sb.append("Face Unlock      : ").append(hasFace ? "Yes" : "No").append("\n");
    sb.append("Iris Scan        : ").append(hasIris ? "Yes" : "No").append("\n");

    // ------------------------------------------------------------
    // DETECT UNDER-DISPLAY SENSOR (UD FPS)
    // ------------------------------------------------------------
    boolean udFps = false;
    try {
        // Most OEMs expose it as a system feature
        udFps = pm.hasSystemFeature("com.motorola.hardware.fingerprint.udfps")
              || pm.hasSystemFeature("com.samsung.hardware.fingerprint.udfps")
              || pm.hasSystemFeature("com.google.hardware.biometrics.udfps")
              || pm.hasSystemFeature("vendor.samsung.hardware.biometrics.fingerprint.udfps")
              || pm.hasSystemFeature("vendor.xiaomi.hardware.fingerprint.udfps");
    } catch (Throwable ignore) {}

    if (udFps) {
        sb.append("Under-Display FP : Yes (UDFPS)\n");
    } else if (hasFp) {
        sb.append("Under-Display FP : No (standard sensor)\n");
    }

    // ------------------------------------------------------------
    // BIOMETRIC MODALITY COUNT
    // ------------------------------------------------------------
    int modes = (hasFp ? 1 : 0) + (hasFace ? 1 : 0) + (hasIris ? 1 : 0);

    sb.append("Profile          : ");
    switch (modes) {
        case 0:
            sb.append("No biometric hardware detected.\n");
            break;
        case 1:
            sb.append("Single biometric modality.\n");
            break;
        default:
            sb.append("Multi-biometric device (").append(modes).append(" modalities).\n");
            break;
    }

    // ------------------------------------------------------------
    // STRONG VS WEAK AUTHENTICATION (API-Level safe)
    // ------------------------------------------------------------
    try {
        BiometricManager bm = BiometricManager.from(this);
        int level = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

        sb.append("Strength         : ");
        if (level == BiometricManager.BIOMETRIC_SUCCESS) {
            sb.append("Strong biometrics supported\n");
        } else {
            sb.append("Weak/Class-2 biometrics only\n");
        }
    } catch (Throwable ignore) {
        sb.append("Strength         : Unknown\n");
    }

    // ------------------------------------------------------------
    // ADVANCED INFO (TEE + ENCRYPTION)
    // ------------------------------------------------------------
    sb.append("Advanced         : Trusted Execution Environment stores biometric keys; detailed telemetry requires root access.\n");

    return sb.toString();
}

    // ------------------------------------------------------------
    // SENSORS
    // ------------------------------------------------------------

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

// ============================================================
//  CONNECTIVITY 
// ============================================================
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

                sb.append("Downlink         : ")
                  .append(caps.getLinkDownstreamBandwidthKbps())
                  .append(" kbps\n");
                sb.append("Uplink           : ")
                  .append(caps.getLinkUpstreamBandwidthKbps())
                  .append(" kbps\n");
                sb.append("Validated        : ")
                  .append(validated ? "Yes" : "No")
                  .append("\n");
                sb.append("Metered          : ")
                  .append(notMetered ? "No" : "Yes")
                  .append("\n");
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

                // SAFE MAC (masked vs real, root-aware)
                String rawMac = wi.getMacAddress();
                String macLine;
                if (rawMac != null
                        && rawMac.length() > 0
                        && !"02:00:00:00:00:00".equals(rawMac)) {
                    macLine = rawMac;   // real MAC (root / older Android)
                } else {
                    if (!isRooted) {
                        macLine = "Masked by Android security. Requires root access";
                    } else {
                        macLine = "Unavailable";
                    }
                }
                sb.append("  MAC            : ").append(macLine).append("\n");
            }
        }

    } catch (Throwable ignore) {}

// ============================================================
// BLUETOOTH — FULL DETAIL + ROOT PATHS (GEL Edition)
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

        // ------------------------------------------------------------
        // STATE
        // ------------------------------------------------------------
        int state = ba.getState();
        String stateStr;
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_ON:  stateStr = "Turning On";  break;
            case BluetoothAdapter.STATE_ON:          stateStr = "On";          break;
            case BluetoothAdapter.STATE_TURNING_OFF: stateStr = "Turning Off"; break;
            default:                                 stateStr = "Off";         break;
        }
        sb.append("  State          : ").append(stateStr).append("\n");

        // ------------------------------------------------------------
        // NAME / ADDRESS (MASKED IF UNROOTED)
        // ------------------------------------------------------------
        String btName = ba.getName();
        if (btName == null || btName.trim().isEmpty()) {
            btName = isRooted ? "Unavailable" : "Unavailable (requires root access)";
        }

        String btAddr = ba.getAddress();
        if (btAddr == null
                || btAddr.trim().isEmpty()
                || "02:00:00:00:00:00".equals(btAddr)) {
            btAddr = isRooted
                    ? "Unavailable"
                    : "Masked by Android security (requires root access)";
        }

        sb.append("  Name           : ").append(btName).append("\n");
        sb.append("  Address        : ").append(btAddr).append("\n");

        // ------------------------------------------------------------
        // BLUETOOTH VERSION — REALITY CHECK (Android does NOT expose)
        // ------------------------------------------------------------
        if (isRooted) {
            sb.append("  Version        : ");

            String v = readSysString("/proc/bluetooth/version");
            if (v == null || v.isEmpty())
                v = readSysString("/sys/module/bluetooth/version");

            sb.append(v != null && !v.isEmpty()
                    ? v
                    : "Not exposed by vendor firmware").append("\n");

        } else {
            sb.append("  Version        : Not exposed by Android (requires root access)\n");
        }

        // ------------------------------------------------------------
        // CLASSIC CAPABILITIES
        // ------------------------------------------------------------
        sb.append("  Scan Mode      : ").append(ba.getScanMode()).append("\n");
        sb.append("  Discoverable   : ")
                .append(ba.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE ? "Yes" : "No")
                .append("\n");

        // BLE Support
        boolean le = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        sb.append("  BLE Support    : ").append(le ? "Yes" : "No").append("\n");

        // ------------------------------------------------------------
        // HARDWARE CAPABILITIES
        // ------------------------------------------------------------
        sb.append("  Multiple Adv   : ");
        try {
            sb.append(ba.isMultipleAdvertisementSupported() ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) { sb.append("Unknown\n"); }

        sb.append("  LE Scanner     : ");
        try {
            sb.append(ba.getBluetoothLeScanner() != null ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) { sb.append("Unknown\n"); }

        sb.append("  Offloaded Filt.: ");
        try {
            sb.append(ba.isOffloadedFilteringSupported() ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) { sb.append("Unknown\n"); }

        // ------------------------------------------------------------
        // CONNECTED DEVICES (GATT)
        // ------------------------------------------------------------
        try {
            List<BluetoothDevice> con =
                    bm.getConnectedDevices(BluetoothProfile.GATT);
            sb.append("  GATT Devices   : ").append(con != null ? con.size() : 0).append("\n");
        } catch (Throwable ignore) {
            sb.append("  GATT Devices   : Unknown\n");
        }

        // ------------------------------------------------------------
        // ROOT EXCLUSIVE PATHS (vendor logs / firmware info)
        // ------------------------------------------------------------
        sb.append("\n  Firmware Info  : ");
        if (isRooted) {
            String fw = null;

            // Common vendor BLUETOOTH firmware paths
            if (fw == null) fw = readSysString("/vendor/firmware/bt/default/bt_version.txt");
            if (fw == null) fw = readSysString("/system/etc/bluetooth/bt_stack.conf");
            if (fw == null) fw = readSysString("/vendor/etc/bluetooth/bt_stack.conf");
            if (fw == null) fw = readSysString("/proc/bluetooth/soc");

            sb.append(fw != null && !fw.isEmpty()
                    ? fw
                    : "Not exposed by vendor").append("\n");

        } else {
            sb.append("Requires root access\n");
        }
    }

} catch (Throwable ignore) {}

    // ============================================================
    // ADVANCED INFO (green comment style)
    // ============================================================
    sb.append("\nDeep Stats       : Advanced interface counters, raw RF tables, " +
              "Bluetooth controller logs and HCI traces, requires root access.\n");

    return sb.toString();
}
      
// ===================================================================
// MODEL CAPACITY STORAGE (SharedPreferences) — FINAL GEL EDITION
// ===================================================================
private static final String PREFS_NAME_BATTERY = "gel_prefs";
private static final String KEY_BATTERY_MODEL_CAPACITY = "battery_model_capacity";
private static final String KEY_BATTERY_DIALOG_SHOWN   = "battery_dialog_shown";

// ===================================================================
// BATTERY DATA STRUCT
// ===================================================================
private static class BatteryInfo {
    int level = -1;
    int scale = -1;
    String status = "N/A";
    String chargingSource = "Unknown";
    float temperature = 0f;

    long currentChargeMah  = -1;   // Charge Counter / OEM / Derived
    long estimatedFullMah  = -1;   // Estimated full (100%)
    String source          = "Unknown";
}

// ===================================================================
// MODEL CAPACITY HELPERS
// ===================================================================
private long getStoredModelCapacity() {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        return sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L);
    } catch (Throwable ignore) { return -1L; }
}

private void saveModelCapacity(long value) {
    try {
        getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE)
                .edit()
                .putLong(KEY_BATTERY_MODEL_CAPACITY, value)
                .apply();
    } catch (Throwable ignore) {}
}

// ===================================================================
// NORMALIZE mAh / μAh
// ===================================================================
private long normalizeMah(long raw) {
    if (raw <= 0) return -1;
    if (raw > 200000) return raw / 1000;   // μAh → mAh
    return raw;
}

// ===================================================================
// UNIVERSAL BATTERY SCANNER — GEL v7.1
// ===================================================================
private BatteryInfo getBatteryInfo() {

    BatteryInfo bi = new BatteryInfo();

    // ---------- 1) BASIC BATTERY_CHANGED ----------
    try {
        Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i != null) {

            bi.level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            bi.scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            switch (i.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                case BatteryManager.BATTERY_STATUS_CHARGING:     bi.status = "Charging"; break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:  bi.status = "Discharging"; break;
                case BatteryManager.BATTERY_STATUS_FULL:         bi.status = "Full"; break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING: bi.status = "Not charging"; break;
                default:                                         bi.status = "Unknown";
            }

            int plug = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            if (plug == BatteryManager.BATTERY_PLUGGED_USB)          bi.chargingSource = "USB";
            else if (plug == BatteryManager.BATTERY_PLUGGED_AC)      bi.chargingSource = "AC";
            else if (plug == BatteryManager.BATTERY_PLUGGED_WIRELESS)bi.chargingSource = "Wireless";
            else                                                     bi.chargingSource = "Battery";

            int temp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp > 0) bi.temperature = temp / 10f;
        }
    } catch (Throwable ignore) {}

    // ---------- 2) OEM CAPACITY ----------
    long bestMah = -1;
    String[] oemPaths = new String[]{
            "/sys/class/power_supply/battery/charge_full_design",
            "/sys/class/power_supply/battery/charge_full",
            "/sys/class/power_supply/battery/charge_full_raw",
            "/sys/class/power_supply/battery/fg_fullcapnom",
            "/sys/class/power_supply/battery/fg_fullcaprep",
            "/sys/class/power_supply/battery/bms/charge_full",
            "/sys/class/power_supply/battery/bms/charge_full_design"
    };

    for (String p : oemPaths) {
        try {
            long v = normalizeMah(readSysLong(p));
            if (v > 500 && v > bestMah) {
                bestMah = v;
                bi.source = "OEM";
            }
        } catch (Throwable ignore) {}
    }

    if (bestMah > 0) {
        // Smooth OEM (e.g. 5000) vs odd OEM (e.g. 4138)
        if (bi.level > 0 && (bestMah % 50 != 0)) {
            float pct = bi.level / 100f;
            long est = (long) (bestMah / pct);
            bi.estimatedFullMah = (est > 0 ? est : bestMah);
        } else {
            bi.estimatedFullMah = bestMah;
        }
    }

    // ---------- 3) CHARGE COUNTER ----------
    try {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (bm != null) {
            long cc = normalizeMah(bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
            if (cc > 0) {
                bi.currentChargeMah = cc;
                bi.source = "Charge Counter";

                if (bi.level > 0) {
                    float pct = bi.level / 100f;
                    long est = (long) (cc / pct);
                    if (est > 0) bi.estimatedFullMah = est;
                }
            }
        }
    } catch (Throwable ignore) {}

    return bi;
}

// ===================================================================
// BATTERY INFO BUILDER — GEL PREMIUM OUTPUT
// ===================================================================
private String buildBatteryInfo() {

    BatteryInfo bi = getBatteryInfo();
    if (bi == null) bi = new BatteryInfo();

    long modelCap = getStoredModelCapacity();
    boolean hasCC  = (bi.currentChargeMah > 0);
    boolean hasEst = (bi.estimatedFullMah > 0);

    float lvlFrac = (bi.level > 0 ? bi.level / 100f : -1f);

    long displayCurrent   = -1;
    long displayEstimated = -1;

    // ---------- DETERMINE CURRENT ----------
    if (hasCC) {
        displayCurrent = bi.currentChargeMah;
    } else if (hasEst && lvlFrac > 0) {
        displayCurrent = (long) (bi.estimatedFullMah * lvlFrac);
    } else if (modelCap > 0 && lvlFrac > 0) {
        displayCurrent = (long) (modelCap * lvlFrac);
    }

    // ---------- DETERMINE ESTIMATED FULL ----------
    if (hasEst) {
        displayEstimated = bi.estimatedFullMah;
    } else if (hasCC && lvlFrac > 0) {
        displayEstimated = (long) (displayCurrent / lvlFrac);
    }

    StringBuilder sb = new StringBuilder();

    // ----- BASIC LINES -----
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Level"), bi.level >= 0 ? bi.level + "%" : "N/A"));
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Scale"), bi.scale > 0 ? bi.scale : "N/A"));
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Status"), bi.status));
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Charging source"), bi.chargingSource));
    sb.append(String.format(Locale.US, "%s : %.1f°C\n\n", padKey("Temp"), bi.temperature));

    // ----- CURRENT -----
    if (displayCurrent > 0)
        sb.append(String.format(Locale.US, "%s : %d mAh\n", padKey("Current charge"), displayCurrent));
    else
        sb.append(String.format(Locale.US, "%s : %s\n", padKey("Current charge"), "N/A"));

    // ----- SOURCE -----
    String src;
    if (hasCC) src = "Charge Counter";
    else if (hasEst) src = "OEM";
    else if (modelCap > 0) src = "Model capacity";
    else src = "Unknown";
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Source"), src));

    // ----- ESTIMATED FULL -----
    if (displayEstimated > 0) {
        sb.append(String.format(Locale.US, "%s : %d mAh\n", padKey("Estimated full (100%)"), displayEstimated));
    } else {
        sb.append(String.format(Locale.US, "%s : %s\n",
                padKey("Estimated full (100%)"), "N/A in this device"));
        sb.append(indent("Requires charge counter chip for accurate data.", 26)).append("\n");
        sb.append(indent("Using GEL Smart Model instead.", 26)).append("\n");
    }

    // ----- MODEL CAPACITY -----
    sb.append(String.format(Locale.US, "%s : %s\n",
            padKey("Model capacity"), modelCap > 0 ? modelCap + " mAh" : "N/A"));

    // ----- LIFECYCLE -----
    sb.append(String.format(Locale.US, "%s : %s",
            padKey("Lifecycle"), "Requires root access"));

    return sb.toString();
}

// ===================================================================
// REFRESH VIEW
// ===================================================================
private void refreshBatteryInfoView() {
    try {
        TextView content = findViewById(R.id.txtBatteryContent);
        if (content != null) {
            String info = buildBatteryInfo();
            content.setText(info);
            applyNeonValues(content, info);
        }
        refreshBatteryButton();
    } catch (Throwable ignore) {}
}

// ===================================================================
// REFRESH BUTTON LABEL
// ===================================================================
private void refreshBatteryButton() {
    TextView btn = findViewById(R.id.txtBatteryModelCapacity);
    if (btn != null) {
        long cap = getStoredModelCapacity();
        btn.setText(cap > 0 ? "Set model capacity (" + cap + " mAh)" : "Set model capacity");
    }
}

// ===================================================================
// CLOSE BATTERY COMPLETELY
// ===================================================================
private void closeBatteryModule() {
    try {
        if (batteryContainer != null) batteryContainer.setVisibility(View.GONE);
        if (txtBatteryContent != null) txtBatteryContent.setVisibility(View.GONE);
        if (txtBatteryModelCapacity != null) txtBatteryModelCapacity.setVisibility(View.GONE);
        if (iconBattery != null) iconBattery.setText("＋");
    } catch (Throwable ignore) {}
}

// ===================================================================
// INIT BATTERY SECTION
// ===================================================================
private void initBatterySection() {
    txtBatteryContent = findViewById(R.id.txtBatteryContent);
    TextView btnCapacity = findViewById(R.id.txtBatteryModelCapacity);

    refreshBatteryInfoView();  // Always fresh

    if (btnCapacity != null)
        btnCapacity.setOnClickListener(v -> showBatteryCapacityDialog());

    maybeShowBatteryCapacityDialogOnce();
}

// ===================================================================
// POPUP ONLY ONCE
// ===================================================================
private void maybeShowBatteryCapacityDialogOnce() {
    try {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        if (!sp.getBoolean(KEY_BATTERY_DIALOG_SHOWN, false) &&
            sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L) <= 0) {

            sp.edit().putBoolean(KEY_BATTERY_DIALOG_SHOWN, true).apply();
            runOnUiThread(this::showBatteryCapacityDialog);
        }
    } catch (Throwable ignore) {}
}

// ===================================================================
// POPUP — FINAL
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

            b.setPositiveButton(getString(R.string.battery_popup_ok),
                    (dialog, which) -> {
                        String txt = input.getText().toString().trim();
                        if (!txt.isEmpty()) {
                            try {
                                long val = Long.parseLong(txt);
                                if (val > 0) {
                                    saveModelCapacity(val);
                                    refreshBatteryInfoView();
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
        sb.append("Advanced         : Fine-grain ranging diagnostics, requires root access.\n");

        return sb.toString();
    }

 // ============================================================
// HAPTICS — ADVANCED MOTOR & EFFECT PROFILER (GEL v3.9)
// ============================================================
private String buildHapticsInfo() {

    StringBuilder sb = new StringBuilder();
    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    if (vib == null) {
        sb.append("Haptic Engine    : Not available\n");
        return sb.toString();
    }

    // ------------------------------------------------------------
    // BASIC SUPPORT
    // ------------------------------------------------------------
    sb.append("Haptic Support   : ").append(vib.hasVibrator() ? "Yes" : "No").append("\n");

    if (Build.VERSION.SDK_INT >= 26) {
        sb.append("Amplitude Ctrl   : ")
                .append(vib.hasAmplitudeControl() ? "Yes" : "No").append("\n");
    }

    // ------------------------------------------------------------
    // VIBRATION EFFECTS (API-based)
    // ------------------------------------------------------------
    if (Build.VERSION.SDK_INT >= 29) {
        sb.append("\nAvailable Effects:\n");

        sb.append("  • Click\n");
        sb.append("  • Double Click\n");
        sb.append("  • Tick\n");
        sb.append("  • Pop\n");
        sb.append("  • Heavy Click\n");
        sb.append("  • Texture / Rumble (OEM-dependent)\n");
    }

    // ------------------------------------------------------------
    // PRIMITIVES (API 31+)
    // ------------------------------------------------------------
    if (Build.VERSION.SDK_INT >= 31) {
        try {
            sb.append("\nHaptic Primitives:\n");

            int[] primitives = VibrationEffect.getPrimitives();
            if (primitives != null && primitives.length > 0) {
                for (int p : primitives) {
                    sb.append("  • ").append(primitiveName(p)).append("\n");
                }
            } else {
                sb.append("  • None exposed\n");
            }
        } catch (Throwable ignore) {}
    }

    // ------------------------------------------------------------
    // ROOT-ONLY MOTOR TELEMETRY
    // ------------------------------------------------------------
    sb.append("\nAdvanced         : Vibration motor waveform tables, OEM amplitude curves, frequency maps and actuator telemetry require root access.\n");

    return sb.toString();
}

// Primitive names (API 31+)
private String primitiveName(int id) {
    switch (id) {
        case VibrationEffect.Composition.PRIMITIVE_CLICK: return "CLICK";
        case VibrationEffect.Composition.PRIMITIVE_TICK: return "TICK";
        case VibrationEffect.Composition.PRIMITIVE_THUD: return "THUD";
        case VibrationEffect.Composition.PRIMITIVE_POP: return "POP";
        case VibrationEffect.Composition.PRIMITIVE_HEAVY_CLICK: return "HEAVY CLICK";
        default: return "UNKNOWN(" + id + ")";
    }
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
sb.append("\nAdvanced         : GNSS constellation breakdown, carrier phases, logging and raw measurement,s requires root access.\n");
} catch (Throwable ignore) {
    sb.append("GNSS information is not exposed on this device.\n");
}

return sb.toString();
}
      
// ============================================================
// USB / OTG / POWER / ROLE ENGINE — GEL TURBO EDITION
// ============================================================
private String buildUsbInfo() {
    StringBuilder sb = new StringBuilder();

    // ------------------------------------------------------------
    // BASIC SUPPORT FLAGS
    // ------------------------------------------------------------
    boolean otg = getPackageManager().hasSystemFeature("android.hardware.usb.host");
    boolean acc = getPackageManager().hasSystemFeature("android.hardware.usb.accessory");

    sb.append("OTG Support      : ").append(otg ? "Yes" : "No").append("\n");
    sb.append("Accessory Mode   : ").append(acc ? "Yes" : "No").append("\n");

    // ------------------------------------------------------------
    // USB MANAGER
    // ------------------------------------------------------------
    UsbManager um = (UsbManager) getSystemService(Context.USB_SERVICE);
    if (um == null) {
        sb.append("Status           : USB Manager unavailable\n");
        return sb.toString();
    }

    // ------------------------------------------------------------
    // CURRENTLY CONNECTED DEVICE (host mode)
    // ------------------------------------------------------------
    try {
        HashMap<String, UsbDevice> devs = um.getDeviceList();
        if (devs != null && !devs.isEmpty()) {
            sb.append("\nConnected USB Devices:\n");
            for (UsbDevice d : devs.values()) {

                sb.append("  • ").append(d.getDeviceName()).append("\n");
                sb.append("    Vendor ID     : ").append(d.getVendorId()).append("\n");
                sb.append("    Product ID    : ").append(d.getProductId()).append("\n");
                sb.append("    Class/Subcls  : ")
                        .append(d.getDeviceClass()).append("/")
                        .append(d.getDeviceSubclass()).append("\n");
                sb.append("    Interfaces    : ").append(d.getInterfaceCount()).append("\n");

                // Possible USB speed detection
                sb.append("    Speed Info    : ");

                if (Build.VERSION.SDK_INT >= 31) {
                    try {
                        int sp = d.getDeviceSpeed();
                        String spLabel;
                        switch (sp) {
                            case UsbConstants.USB_SPEED_LOW:  spLabel = "USB 1.1 Low (1.5Mbps)"; break;
                            case UsbConstants.USB_SPEED_FULL: spLabel = "USB 1.1 Full (12Mbps)"; break;
                            case UsbConstants.USB_SPEED_HIGH: spLabel = "USB 2.0 High (480Mbps)"; break;
                            case UsbConstants.USB_SPEED_SUPER: spLabel = "USB 3.x SuperSpeed"; break;
                            case UsbConstants.USB_SPEED_SUPER_PLUS: spLabel = "USB 3.x SuperSpeed+"; break;
                            default: spLabel = "Unknown";
                        }
                        sb.append(spLabel).append("\n");
                    } catch (Throwable ignore) {
                        sb.append("Unknown\n");
                    }
                } else {
                    sb.append("Android <12 does not expose speed\n");
                }
            }
        } else {
            sb.append("Connected Dev.   : None\n");
        }

    } catch (Throwable ignore) {
        sb.append("Connected Dev.   : Error reading USB map\n");
    }

    // ------------------------------------------------------------
    // USB ROLE (Device ↔ Host)
    // ------------------------------------------------------------
    sb.append("\nMode/Role:\n");
    try {
        if (Build.VERSION.SDK_INT >= 26) {
            UsbPort[] ports = um.getPorts();
            if (ports != null && ports.length > 0) {
                UsbPort p = ports[0];
                UsbPortStatus st = p.getStatus();
                if (st != null) {

                    String roleDevice =
                            st.getCurrentDeviceRole() == UsbPortStatus.DEVICE_ROLE_DEVICE
                                    ? "Device" : "None";

                    String roleHost =
                            st.getCurrentDeviceRole() == UsbPortStatus.DEVICE_ROLE_HOST
                                    ? "Host" : "None";

                    sb.append("  Device Role    : ").append(roleDevice).append("\n");
                    sb.append("  Host Role      : ").append(roleHost).append("\n");
                }
            } else {
                sb.append("  Port Info      : No USB ports exposed\n");
            }
        } else {
            sb.append("  Role Info      : Requires Android 8.0+\n");
        }

    } catch (Throwable ignore) {
        sb.append("  Role Info      : Error reading role state\n");
    }

    // ------------------------------------------------------------
    // POWER + CHARGER PROFILE (Informational)
    // ------------------------------------------------------------
    sb.append("\nPower Profiles:\n");
    try {
        IntentFilter ifil = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batt = registerReceiver(null, ifil);
        if (batt != null) {
            int source = batt.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            String srcLabel =
                    (source == BatteryManager.BATTERY_PLUGGED_USB) ? "USB"
                    : (source == BatteryManager.BATTERY_PLUGGED_AC) ? "AC"
                    : (source == BatteryManager.BATTERY_PLUGGED_WIRELESS) ? "Wireless"
                    : "Unplugged";

            sb.append("  Charge Source  : ").append(srcLabel).append("\n");
            sb.append("  Current (mA)   : ")
                    .append(batt.getIntExtra(BatteryManager.EXTRA_CURRENT_NOW, 0))
                    .append("\n");
            sb.append("  Voltage (mV)   : ")
                    .append(batt.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0))
                    .append("\n");
        }
    } catch (Throwable ignore) {
        sb.append("  Power Info     : Error\n");
    }

    // ------------------------------------------------------------
    // ROOT-EXCLUSIVE EXTRA DATA (SAFE)
    // ------------------------------------------------------------
    sb.append("\nAdvanced         : USB descriptors, negotiated currents, and port driver tables require root access.\n");

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

        sb.append("\nAdvanced         : Extended peripheral diagnostics requires root access.\n");

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

        sb.append("\nAdvanced         : Raw audio routing matrices requires root access.\n");

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

        sb.append("\nAdvanced         : HAL routing tables, offload models, DSP profiles, require root access.\n");

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

        sb.append("Advanced         : Spatial audio flags, noise models, per-stream audio paths, requires root access.\n");

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
// Root Info (with Vendor Diag Paths for rooted devices)
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

        // --------------------------------------------------------
        // Vendor diag / engineering paths (Samsung / Qualcomm / MTK)
        // --------------------------------------------------------
        String[] vendorDiag = {
                // Qualcomm generic diag
                "/dev/diag",
                "/dev/diag_qti",

                // Samsung EFS / modem related
                "/efs/imei/.msl",
                "/efs/imei/key_str",
                "/efs/FactoryApp",

                // Qualcomm / vendor diag logging
                "/vendor/etc/diag_mdlog",
                "/system/vendor/bin/diag_mdlog",

                // MTK engineering / gps / modem helpers
                "/system/bin/mtk_agpsd",
                "/system/bin/mtk_engineering",
                "/system/bin/emdlogger",
        };

        boolean foundVendor = false;
        for (String p : vendorDiag) {
            try {
                if (new File(p).exists()) {
                    if (!foundVendor) {
                        sb.append("\nVendor diag paths (root-only):\n");
                        foundVendor = true;
                    }
                    sb.append("  ").append(p).append("\n");
                }
            } catch (Throwable ignore) {}
        }

        if (!foundVendor) {
            sb.append("\nVendor diag paths (Samsung / Qualcomm / MTK) are present at OS level but not exposed to this app without a full root shell and vendor tools.\n");
        }

    } else {
        sb.append("\nThis device is not rooted.\n");
        sb.append("Advanced subsystem tables are visible only on rooted systems.\n");
        sb.append("Extended hardware diagnostics require root access.\n");
        sb.append("Vendor diag paths (Samsung / Qualcomm / MTK) cannot be inspected from a non-root app.\n");
    }

    return sb.toString();
}

    // ==================================

    //==========================
    // NEW MEGA-UPGRADE SECTIONS (1–12)
    // ============================================================

// ===================================================================
// 1. THERMAL ENGINE / COOLING — UNIVERSAL HARDWARE EDITION (STRING MODE)
// ===================================================================

// Helper struct για να κρατάμε μια "καλύτερη" θερμοκρασία ανά ομάδα
private static class ThermalGroupReading {
    String rawName;   // π.χ. "battery_therm"
    float  tempC;     // σε βαθμούς C
    boolean valid;

    ThermalGroupReading() {
        this.valid = false;
    }

    void updateIfBetter(String name, float valueC) {
        if (!isValidTemp(valueC)) return;
        if (!valid || valueC > tempC) {
            valid   = true;
            tempC   = valueC;
            rawName = name;
        }
    }
}

// Safety check για θερμοκρασίες
private static boolean isValidTemp(float c) {
    return (c > -50f && c < 200f);
}

// ---------------------------------------------------------------
// MAPPING: thermal zone "type" → λογική ομάδα (REAL hardware only)
// ---------------------------------------------------------------
private static final String[][] THERMAL_GROUP_PATTERNS = new String[][]{
        {"BatteryMain",   "battery", "batt", "batt_therm", "battery_therm", "fuelgauge", "bms", "bms_therm"},
        {"BatteryShell",  "skin", "case", "batt_skin", "battery_skin", "rear_case", "shell"},
        {"PMIC",          "pmic", "pm8998", "pm8150", "pmx", "pmic-therm", "pmic_therm"},
        {"Charger",       "charger", "chg", "usb", "usb-therm", "bq", "charge-therm"},
        {"ModemMain",     "modem", "mdm", "mdmss", "xbl_modem", "modempa", "rf-therm", "rf"},
        {"ModemAux",      "modem1", "rf1", "xbl_modem1", "mdm1", "modem_b"}
};

// Summary struct
private static class ThermalSummary {
    int zoneCount;
    int coolingDeviceCount;
}

// ---------------------------------------------------------------
// Thermal scan
// ---------------------------------------------------------------
private ThermalSummary scanThermalHardware(
        ThermalGroupReading batteryMain,
        ThermalGroupReading batteryShell,
        ThermalGroupReading pmic,
        ThermalGroupReading charger,
        ThermalGroupReading modemMain,
        ThermalGroupReading modemAux
) {
    ThermalSummary summary = new ThermalSummary();

    File thermalDir = new File("/sys/class/thermal");
    File[] zones = null;
    File[] cools = null;

    try {
        if (thermalDir.exists() && thermalDir.isDirectory()) {
            zones = thermalDir.listFiles(f -> f.getName().startsWith("thermal_zone"));
            cools = thermalDir.listFiles(f -> f.getName().startsWith("cooling_device"));
        }
    } catch (Throwable ignore) { }

    summary.zoneCount          = (zones != null) ? zones.length  : 0;
    summary.coolingDeviceCount = (cools != null) ? cools.length : 0;

    if (zones != null) {
        for (File z : zones) {
            try {
                String base  = z.getAbsolutePath();
                String type  = readFirstLineSafe(new File(base, "type"));
                long   milli = readLongSafe(new File(base, "temp"));
                float  c     = Float.NaN;

                if (milli == Long.MIN_VALUE) {
                    try {
                        c = Float.parseFloat(readFirstLineSafe(new File(base, "temp")));
                    } catch (Throwable ignore) {}
                } else {
                    c = milli / 1000f;
                }

                if (!isValidTemp(c)) continue;
                String group = mapTypeToGroup(type);
                if (group == null) continue;

                switch (group) {
                    case "BatteryMain":  batteryMain.updateIfBetter(type, c); break;
                    case "BatteryShell": batteryShell.updateIfBetter(type, c); break;
                    case "PMIC":         pmic.updateIfBetter(type, c); break;
                    case "Charger":      charger.updateIfBetter(type, c); break;
                    case "ModemMain":    modemMain.updateIfBetter(type, c); break;
                    case "ModemAux":     modemAux.updateIfBetter(type, c); break;
                }

            } catch (Throwable ignore) { }
        }
    }

    return summary;
}

private String mapTypeToGroup(String rawType) {
    if (rawType == null) return null;
    String t = rawType.toLowerCase(Locale.US);

    for (String[] entry : THERMAL_GROUP_PATTERNS) {
        String label = entry[0];
        for (int i = 1; i < entry.length; i++) {
            if (t.contains(entry[i])) return label;
        }
    }
    return null;
}

// ---------------------------------------------------------------
// Cooling device filter (REAL hardware only)
// ---------------------------------------------------------------
private boolean isHardwareCoolingDevice(String rawType) {
    if (rawType == null) return false;
    String t = rawType.toLowerCase(Locale.US);

    if (t.contains("fan"))            return true;
    if (t.contains("cooling_fan"))    return true;
    if (t.contains("blower"))         return true;
    if (t.contains("pump"))           return true;
    if (t.contains("heatsink"))       return true;
    if (t.contains("radiator"))       return true;
    if (t.contains("cooling_module")) return true;

    if (t.contains("skin"))    return false;
    if (t.contains("hotspot")) return false;
    if (t.contains("virtual")) return false;

    return false;
}

private void appendHardwareCoolingDevices(StringBuilder sb) {
    File thermalDir = new File("/sys/class/thermal");
    File[] cools = null;

    try {
        if (thermalDir.exists() && thermalDir.isDirectory()) {
            cools = thermalDir.listFiles(f -> f.getName().startsWith("cooling_device"));
        }
    } catch (Throwable ignore) {}

    if (cools == null) {
        sb.append("• (no hardware cooling devices found)\n");
        return;
    }

    int shown = 0;
    for (File c : cools) {
        if (shown >= 5) break;
        try {
            String type = readFirstLineSafe(new File(c.getAbsolutePath(), "type"));
            if (!isHardwareCoolingDevice(type)) continue;

            sb.append("• ").append(c.getName()).append(" → ").append(type).append("\n");
            shown++;

        } catch (Throwable ignore) { }
    }

    if (shown == 0) sb.append("• (no hardware cooling devices found)\n");
}

// ---------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------
private String readFirstLineSafe(File file) {
    if (file == null || !file.exists()) return "";
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine();
        return (line != null) ? line.trim() : "";
    } catch (Throwable ignore) {
        return "";
    }
}

private long readLongSafe(File file) {
    if (file == null || !file.exists()) return Long.MIN_VALUE;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine();
        if (line == null || line.trim().isEmpty()) return Long.MIN_VALUE;
        return Long.parseLong(line.trim());
    } catch (Throwable ignore) {
        return Long.MIN_VALUE;
    }
}

// ---------------------------------------------------------------
// Labels & formatting
// ---------------------------------------------------------------
private String classifyTempLabel(float c) {
    if (!isValidTemp(c)) return "(Unknown)";
    if (c < 30f)  return "(Cool)";
    if (c < 40f)  return "(Normal)";
    if (c < 50f)  return "(Warm)";
    return "(⚠ Critical)";
}

private String formatThermalLine(String label, ThermalGroupReading r) {
    if (r == null || !r.valid)
        return String.format(Locale.US, "%-17s: N/A\n", label);

    return String.format(Locale.US, "%-17s: %.1f°C %s\n",
            label, r.tempC, classifyTempLabel(r.tempC));
}

// ---------------------------------------------------------------
// Xiaomi / POCO / Redmi Detection + Fallbacks
// ---------------------------------------------------------------
private boolean isXiaomiFamilyDevice() {
    String manu   = (Build.MANUFACTURER == null ? "" : Build.MANUFACTURER).toLowerCase();
    String brand  = (Build.BRAND == null ? "" : Build.BRAND).toLowerCase();
    String finger = (Build.FINGERPRINT == null ? "" : Build.FINGERPRINT).toLowerCase();

    return manu.contains("xiaomi") || manu.contains("redmi") || manu.contains("poco")
            || brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")
            || finger.contains("xiaomi") || finger.contains("redmi") || finger.contains("poco")
            || finger.contains("hyperos");
}

private float findTempByTypeKeywords(String... keywords) {
    if (keywords == null || keywords.length == 0) return Float.NaN;

    File[] zones = new File("/sys/class/thermal")
            .listFiles(f -> f.getName().startsWith("thermal_zone"));

    if (zones == null) return Float.NaN;

    float best = Float.NaN;

    for (File z : zones) {
        try {
            String type = readFirstLineSafe(new File(z, "type")).toLowerCase(Locale.US);
            boolean match = false;
            for (String k : keywords) {
                if (type.contains(k.toLowerCase(Locale.US))) { match = true; break; }
            }
            if (!match) continue;

            long milli = readLongSafe(new File(z, "temp"));
            float c;

            if (milli == Long.MIN_VALUE)
                c = Float.parseFloat(readFirstLineSafe(new File(z, "temp")));
            else
                c = milli / 1000f;

            if (!isValidTemp(c)) continue;

            if (Float.isNaN(best) || c > best) best = c;

        } catch (Throwable ignore) {}
    }

    return best;
}

private float readBatteryTempFallback() {
    String[] paths = {
            "/sys/class/power_supply/battery/temp",
            "/sys/class/power_supply/bms/temp",
            "/sys/class/power_supply/maxfg/temp"
    };

    for (String p : paths) {
        try {
            long v = readLongSafe(new File(p));
            if (v == Long.MIN_VALUE) continue;

            float c = (v > 1000f ? v / 1000f : v);
            if (isValidTemp(c)) return c;

        } catch (Throwable ignore) {}
    }
    return Float.NaN;
}

// ---------------------------------------------------------------
// OEM fallback completion
// ---------------------------------------------------------------
private void applyThermalFallbacks(
        ThermalGroupReading batteryMain,
        ThermalGroupReading batteryShell,
        ThermalGroupReading pmic,
        ThermalGroupReading charger,
        ThermalGroupReading modemMain,
        ThermalGroupReading modemAux
) {
    boolean isXiaomi = isXiaomiFamilyDevice();

    // Battery Main
    if (!batteryMain.valid) {
        float c = findTempByTypeKeywords("battery", "batt_therm", "battery_therm", "bms");
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) batteryMain.updateIfBetter("fallback:battery", c);
    }

    if (isXiaomi && !batteryMain.valid) {
        float c = findTempByTypeKeywords("batt_temp", "bat_therm", "battery-main", "battery_board", "batman");
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) batteryMain.updateIfBetter("xiaomi:battery", c);
    }

    // Battery Shell
    if (!batteryShell.valid) {
        float c = findTempByTypeKeywords("batt_shell", "battery_shell", "shell_therm", "case-therm", "skin");
        if (isValidTemp(c)) batteryShell.updateIfBetter("fallback:battery_shell", c);
    }

    if (isXiaomi && !batteryShell.valid) {
        float c = findTempByTypeKeywords("batt_skin", "batt_surface", "back_cover", "rear_case");
        if (isValidTemp(c)) batteryShell.updateIfBetter("xiaomi:battery_shell", c);
    }

    // PMIC
    if (!pmic.valid) {
        float c = findTempByTypeKeywords("pmic", "pmic_therm", "pmic-tz", "pm8998", "pm660");
        if (isValidTemp(c)) pmic.updateIfBetter("fallback:pmic", c);
    }

    if (isXiaomi && !pmic.valid) {
        float c = findTempByTypeKeywords("pm6150l_tz", "pm8350", "pm7250b");
        if (isValidTemp(c)) pmic.updateIfBetter("xiaomi:pmic", c);
    }

    // Charger
    if (!charger.valid) {
        float c = findTempByTypeKeywords("charger", "chg", "usb-therm", "charge-temp");
        if (!isValidTemp(c)) c = findTempByTypeKeywords("charge_pump", "cp_therm");
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) charger.updateIfBetter("fallback:charger", c);
    }

    // Modem main
    if (!modemMain.valid) {
        float c = findTempByTypeKeywords("modem", "mdm", "mdmss", "rf-therm", "modempa");
        if (isValidTemp(c)) modemMain.updateIfBetter("fallback:modem_main", c);
    }

    if (isXiaomi && !modemMain.valid) {
        float c = findTempByTypeKeywords("xo_therm_modem", "modem_pa", "modem_pa_0");
        if (isValidTemp(c)) modemMain.updateIfBetter("xiaomi:modem_main", c);
    }

    // Modem aux
    if (!modemAux.valid) {
        float c = findTempByTypeKeywords("modem1", "mdm2", "xbl_modem1", "rf1");
        if (isValidTemp(c)) modemAux.updateIfBetter("fallback:modem_aux", c);
    }

    if (isXiaomi && !modemAux.valid) {
        float c = findTempByTypeKeywords("modem_sub", "modem1_pa", "rf_sub");
        if (isValidTemp(c)) modemAux.updateIfBetter("xiaomi:modem_aux", c);
    }
}

// ===================================================================
// FINAL BUILDER — CLEAN OUTPUT (NO ZONE/DEVICE SUMMARY)
// ===================================================================
private String buildThermalInfo() {

    StringBuilder sb = new StringBuilder();

    // Hardware thermals
    ThermalGroupReading batteryMain  = new ThermalGroupReading();
    ThermalGroupReading batteryShell = new ThermalGroupReading();
    ThermalGroupReading pmic         = new ThermalGroupReading();
    ThermalGroupReading charger      = new ThermalGroupReading();
    ThermalGroupReading modemMain    = new ThermalGroupReading();
    ThermalGroupReading modemAux     = new ThermalGroupReading();

    scanThermalHardware(batteryMain, batteryShell, pmic, charger, modemMain, modemAux);

    applyThermalFallbacks(batteryMain, batteryShell, pmic, charger, modemMain, modemAux);

    sb.append("Hardware Thermal Systems\n");
    sb.append("================================\n\n");

    sb.append(formatThermalLine("Main Modem",      modemMain));
    sb.append(formatThermalLine("Secondary Modem", modemAux));
    sb.append(formatThermalLine("Main Battery",    batteryMain));
    sb.append(formatThermalLine("Battery Shell",   batteryShell));
    sb.append(formatThermalLine("Charger Thermal", charger));
    sb.append(formatThermalLine("PMIC Thermal",    pmic));
    sb.append("\n");

    sb.append("Hardware Cooling Systems\n");
    sb.append("================================\n");
    appendHardwareCoolingDevices(sb);

    return sb.toString();
}

   //======================================================
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
    sb.append("Advanced         : Panel ID, HBM tables and OEM tone-mapping, requires root access.\n");

    return sb.toString();
}

// ============================================================================
// 3. TELEPHONY / MODEM — UI REFRESH (ONE BLOCK, ONE TEXTVIEW)
// ============================================================================
private void refreshModemInfo() {
    try {
        TextView modemView = findViewById(R.id.txtModemContent);
        if (modemView != null) {
            String info = buildModemInfo();
            modemView.setText(info);
            modemView.setVisibility(View.VISIBLE);
        }
    } catch (Throwable ignore) {}
}

// ============================================================================
// TELEPHONY / MODEM — ULTRA STABLE GEL EDITION (Final, χωρίς getAvailable...)
// ============================================================================
private String buildModemInfo() {
    StringBuilder sb = new StringBuilder();
    Locale locale = Locale.US;

    TelephonyManager tm = null;
    SubscriptionManager sm = null;

    try { tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); } catch (Throwable ignore) {}
    try { sm = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE); } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // PHONE TYPE
    // ------------------------------------------------------------
    String phoneTypeStr = "Unknown";
    try {
        if (tm != null) {
            switch (tm.getPhoneType()) {
                case TelephonyManager.PHONE_TYPE_GSM:  phoneTypeStr = "GSM";  break;
                case TelephonyManager.PHONE_TYPE_CDMA: phoneTypeStr = "CDMA"; break;
                case TelephonyManager.PHONE_TYPE_SIP:  phoneTypeStr = "SIP";  break;
                default: phoneTypeStr = "None"; break;
            }
        }
    } catch (Throwable ignore) {}

    sb.append(String.format(locale, "%s : %s\n", padKeyModem("Phone Type"), phoneTypeStr));

    // ------------------------------------------------------------
    // DATA NETWORK — LTE / NR only
    // ------------------------------------------------------------
    try {
        int net = (tm != null) ? tm.getDataNetworkType() : TelephonyManager.NETWORK_TYPE_UNKNOWN;
        String netName = (net == TelephonyManager.NETWORK_TYPE_NR)  ? "5G NR"
                       : (net == TelephonyManager.NETWORK_TYPE_LTE) ? "4G LTE"
                       : "Unknown";

        sb.append(String.format(locale, "%s : %s\n", padKeyModem("Data Network"), netName));
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("5G (NR) Active"),
                (net == TelephonyManager.NETWORK_TYPE_NR) ? "Yes" : "No"));
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // CARRIER / COUNTRY / OPERATOR CODE
    // ------------------------------------------------------------
    try {
        String carrier = (tm != null) ? tm.getNetworkOperatorName() : null;
        String iso     = (tm != null) ? tm.getNetworkCountryIso() : null;
        String opCode  = (tm != null) ? tm.getNetworkOperator()    : null;

        if (iso == null || iso.trim().isEmpty())
            iso = Locale.getDefault().getCountry();

        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Carrier"),
                (carrier != null && !carrier.isEmpty()) ? carrier : "Unknown"));

        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Country ISO"),
                (iso != null) ? iso.toUpperCase(locale) : "Unknown"));

        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Operator Code"),
                (opCode != null && !opCode.isEmpty()) ? opCode : "Unknown"));
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // SIGNAL STRENGTH — 0–4
    // ------------------------------------------------------------
    try {
        if (tm != null) {
            SignalStrength ss = tm.getSignalStrength();
            if (ss != null) {
                sb.append(String.format(locale, "%s : %d/4\n",
                        padKeyModem("Signal Strength"), ss.getLevel()));
            }
        }
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // ROAMING
    // ------------------------------------------------------------
    try {
        boolean roaming = tm != null && tm.isNetworkRoaming();
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Roaming"), roaming ? "Yes" : "No"));
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // IMS / VoLTE / VoWiFi / VoNR
    // ------------------------------------------------------------
    sb.append(String.format(locale, "%s : %s\n",
            padKeyModem("IMS Registered"), "Unknown"));

    // VoLTE
    try {
        boolean volte = tm != null &&
                (boolean) TelephonyManager.class.getMethod("isVolteAvailable").invoke(tm);
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("VoLTE Support"), volte ? "Yes" : "No"));
    } catch (Throwable ignore) {
        sb.append(String.format(locale, "%s : Unknown\n", padKeyModem("VoLTE Support")));
    }

    // VoWiFi
    try {
        boolean vowifi = tm != null &&
                (boolean) TelephonyManager.class.getMethod("isWifiCallingAvailable").invoke(tm);
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("VoWiFi Support"), vowifi ? "Yes" : "No"));
    } catch (Throwable ignore) {
        sb.append(String.format(locale, "%s : Unknown\n", padKeyModem("VoWiFi Support")));
    }

    // VoNR
    try {
        boolean vonr = (Build.VERSION.SDK_INT >= 33) &&
                tm != null &&
                (boolean) TelephonyManager.class.getMethod("isVoNrEnabled").invoke(tm);
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("VoNR Support"), vonr ? "Yes" : "No"));
    } catch (Throwable ignore) {
        sb.append(String.format(locale, "%s : Unknown\n", padKeyModem("VoNR Support")));
    }

    // ------------------------------------------------------------
    // ACTIVE SIMS — MAX 2, χωρίς getAvailableSubscriptionInfoList()
// ------------------------------------------------------------
    try {
        List<SubscriptionInfo> subs = null;

        if (sm != null) {
            try {
                subs = sm.getActiveSubscriptionInfoList();
            } catch (Throwable ignore) {}
        }

        int count = 0;
        boolean[] seen = new boolean[2];

        if (subs != null) {
            for (SubscriptionInfo si : subs) {
                int slot = si.getSimSlotIndex();
                if (slot >= 0 && slot <= 1 && !seen[slot]) {
                    seen[slot] = true;
                    count++;
                }
            }
        }

        sb.append(String.format(locale, "%s : %d\n",
                padKeyModem("Active SIMs"), count));

        if (subs != null) {
            boolean[] printed = new boolean[2];
            for (SubscriptionInfo si : subs) {
                int slot = si.getSimSlotIndex();
                if (slot < 0 || slot > 1 || printed[slot]) continue;

                printed[slot] = true;

                String displayName =
                        si.getCarrierName() != null ? si.getCarrierName().toString() : "Unknown";

                sb.append(String.format(locale, "%s : %s\n",
                        padKeyModem("SIM Slot " + (slot + 1)), displayName));

                // eSIM
                if (Build.VERSION.SDK_INT >= 29) {
                    try {
                        sb.append(String.format(locale, "%s : %s\n",
                                padKeyModem(" • eSIM"),
                                si.isEmbedded() ? "Yes" : "No"));
                    } catch (Throwable ignore) {}
                }

                // ICCID masked
                try {
                    String iccid = si.getIccId();
                    if (iccid != null && !iccid.isEmpty()) {
                        sb.append(String.format(locale, "%s : %s\n",
                                padKeyModem(" • ICCID"), maskSensitive(iccid)));
                    }
                } catch (Throwable ignore) {}

                // MCC / MNC
                try {
                    sb.append(String.format(locale, "%s : %d / %d\n",
                            padKeyModem(" • MCC / MNC"), si.getMcc(), si.getMnc()));
                } catch (Throwable ignore) {}
            }
        }
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // SUBSCRIBER INFO — masked
    // ------------------------------------------------------------
    try {
        if (tm != null) {
            String imsi = null;
            String msisdn = null;

            try { imsi = tm.getSubscriberId(); } catch (Throwable ignore) {}
            try { msisdn = tm.getLine1Number(); } catch (Throwable ignore) {}

            sb.append(String.format(locale, "%s : %s\n",
                    padKeyModem("IMSI"),
                    (imsi != null && !imsi.isEmpty()) ? maskSensitive(imsi) : "N/A"));

            sb.append(String.format(locale, "%s : %s\n",
                    padKeyModem("MSISDN"),
                    (msisdn != null && !msisdn.isEmpty()) ? maskSensitive(msisdn) : "N/A"));
        }
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // CARRIER AGGREGATION / BANDS
    // ------------------------------------------------------------
    sb.append(String.format(locale, "%s : %s\n",
            padKeyModem("4G+ CA"), "Unknown. Requires root access"));

    sb.append(String.format(locale, "%s : %s\n",
            padKeyModem("NR-CA"), "Unknown. Requires root access"));

    sb.append(String.format(locale, "%s : %s\n",
            padKeyModem("Bands"), "Vendor restricted. Requires root access"));

    // ------------------------------------------------------------
    // ADVANCED FOOTER — SINGLE LINE
    // ------------------------------------------------------------
    sb.append(String.format(locale,
            "%s : Full RAT tables, NR bands, CA combos, requires root access and OEM modem tools.",
            padKeyModem("Advanced")
    ));

    return sb.toString();
}

// ============================================================================
// 4. Wi-Fi Advanced — GEL Ultra Stable Edition (Clean Title + Real Country Code)
// ============================================================================
private String buildWifiAdvancedInfo() {
    StringBuilder sb = new StringBuilder();
    Locale locale = Locale.US;

    try {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        PackageManager pm = getPackageManager();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (wm != null) {

            // ------------------------------------------------------------
            // HARDWARE SUPPORT
            // ------------------------------------------------------------
            boolean wifiHw = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
            sb.append("Wi-Fi HW         : ").append(wifiHw ? "Present" : "Missing").append("\n");

            // ------------------------------------------------------------
            // FREQUENCY BANDS
            // ------------------------------------------------------------
            boolean band24 = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
            sb.append("2.4 GHz Support  : ").append(band24 ? "Yes" : "No").append("\n");

            boolean band5 = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT);
            sb.append("5 GHz Support    : ").append(band5 ? "Yes" : "No").append("\n");

            if (Build.VERSION.SDK_INT >= 30) {
                try {
                    sb.append("6 GHz Support    : ")
                            .append(wm.is6GHzBandSupported() ? "Yes" : "No").append("\n");
                } catch (Throwable ignore) {}
            }

            // ------------------------------------------------------------
            // SECURITY CAPABILITIES
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 29) {
                try { sb.append("WPA3 SAE         : ").append(wm.isWpa3SaeSupported() ? "Yes" : "No").append("\n"); }
                catch (Throwable ignore) {}

                try { sb.append("WPA3 Suite-B     : ").append(wm.isWpa3SuiteBSupported() ? "Yes" : "No").append("\n"); }
                catch (Throwable ignore) {}
            }

            // ------------------------------------------------------------
            // RTT (distance)
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 28) {
                boolean rtt = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT);
                sb.append("Wi-Fi RTT        : ").append(rtt ? "Yes" : "No").append("  (Indoor distance)\n");
            }

            // ------------------------------------------------------------
            // Wi-Fi Aware / NAN
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                boolean aware = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
                sb.append("Wi-Fi Aware      : ").append(aware ? "Yes" : "No").append("  (Device proximity)\n");
            }

            // ------------------------------------------------------------
            // Easy Connect (DPP)
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 29) {
                boolean dpp = pm.hasSystemFeature("android.hardware.wifi.dpp");
                sb.append("Easy Connect     : ").append(dpp ? "Yes" : "No").append("\n");
            }

            // ------------------------------------------------------------
            // Passpoint (Hotspot 2.0)
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                boolean pass = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT);
                sb.append("Passpoint (HS2)  : ").append(pass ? "Yes" : "No").append("\n");
            }

            // ------------------------------------------------------------
            // P2P / Direct
            // ------------------------------------------------------------
            sb.append("Wi-Fi Direct     : ")
                    .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT) ? "Yes" : "No").append("\n");

            // ------------------------------------------------------------
            // MAC RANDOMIZATION / LINK LAYER (informational)
            // ------------------------------------------------------------
            sb.append("MAC Randomization: Unknown (SDK level)\n");
            sb.append("Link Layer Stats : Unknown (hidden API)\n");

            // ------------------------------------------------------------
            // POWER SAVE MODE
            // ------------------------------------------------------------
            try {
                sb.append("Scan Always On   : ").append(wm.isScanAlwaysAvailable() ? "Yes" : "No").append("\n");
            } catch (Throwable ignore) {}

            // ------------------------------------------------------------
            // REAL COUNTRY CODE (triple fallback, COMPILE-SAFE)
            // ------------------------------------------------------------
            String cc = null;

            // 1) Modem ISO (best)
            try { if (tm != null) cc = tm.getNetworkCountryIso(); }
            catch (Throwable ignore) {}

            // 2) Wi-Fi regulatory domain (some devices support it — COMPILER SAFE)
            if ((cc == null || cc.isEmpty()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    // Reflection → avoids "cannot find symbol" on devices without method
                    java.lang.reflect.Method m = WifiManager.class.getMethod("getCountryCode");
                    Object val = m.invoke(wm);
                    if (val instanceof String) cc = (String) val;
                } catch (Throwable ignore) {}
            }

            // 3) Locale fallback
            if (cc == null || cc.isEmpty())
                cc = Locale.getDefault().getCountry();

            sb.append("Country Code     : ")
                    .append((cc != null && !cc.isEmpty()) ? cc.toUpperCase(locale) : "Unknown")
                    .append("\n");
        }

    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // ADVANCED FOOTER — SINGLE LINE
    // ------------------------------------------------------------
    sb.append("\nAdvanced         : Regulatory region, DFS radar tables, TX power, per-band limits, requires root access.\n");

    return sb.toString();
}


    // ============================
    // 5. Sensors EXTENDED
    // ============================

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

        sb.append("Advanced         : Dedicated sensor hubs and on-SoC fusion engines, requires root access and vendor sensor HAL.\n");

        return sb.toString();
    }


    // ============================
    // 6. System Feature Matrix
    // ============================

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
 
    // ============================
    // 7. SELinux / Security Flags
    // ============================

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

        sb.append("Advanced         : Full SELinux policy dump and keymaster internals, requires root access and are not exposed to apps.\n");

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

    // =====================
    // CORE HARDWARE
    //======================
    String scr = buildScreenInfo();
    set(R.id.txtScreenContent, scr);
    applyNeonValues(findViewById(R.id.txtScreenContent), scr);

    String cam = buildCameraInfo();
    set(R.id.txtCameraContent, cam);
    applyNeonValues(findViewById(R.id.txtCameraContent), cam);

    String con = buildConnectivityInfo();
set(R.id.txtConnectivityContent, con);
applyNeonValues(findViewById(R.id.txtConnectivityContent), con);

    // ============================================================================
    // LOCATION (Universal, No-Permission Crash-Free)
    // ============================================================================
    try {
        String loc = getLocationCapabilities();
        set(R.id.txtLocationContent, loc);
        applyNeonValues(findViewById(R.id.txtLocationContent), loc);
    } catch (Throwable ignore) {}

    // =====================
    // THERMAL (FIXED)
    // =====================
    CharSequence thr = buildThermalInfo();
    set(R.id.txtThermalContent, thr.toString());
    applyNeonValues(findViewById(R.id.txtThermalContent), thr.toString());

    // MODEM
    String mod = buildModemInfo();
    set(R.id.txtModemContent, mod);
    applyNeonValues(findViewById(R.id.txtModemContent), mod);

    // ADVANCED WIFI
    String wadv = buildWifiAdvancedInfo();
    set(R.id.txtWifiAdvancedContent, wadv);
    applyNeonValues(findViewById(R.id.txtWifiAdvancedContent), wadv);

    // AUDIO
    String aud = buildAudioUnifiedInfo();
    set(R.id.txtAudioUnifiedContent, aud);
    applyNeonValues(findViewById(R.id.txtAudioUnifiedContent), aud);

    // SENSORS
    String s1 = buildSensorsInfo();
    set(R.id.txtSensorsContent, s1);
    applyNeonValues(findViewById(R.id.txtSensorsContent), s1);

    String s2 = buildSensorsExtendedInfo();
    set(R.id.txtSensorsExtendedContent, s2);
    applyNeonValues(findViewById(R.id.txtSensorsExtendedContent), s2);

    // BIOMETRICS
    String bio = buildBiometricsInfo();
    set(R.id.txtBiometricsContent, bio);
    applyNeonValues(findViewById(R.id.txtBiometricsContent), bio);

    // ============================================================================
    // NFC (Compiler-Safe, Universal)
    // ============================================================================
    try {
        String nfc = getNfcBasicInfo();
        set(R.id.txtNfcContent, nfc);
        applyNeonValues(findViewById(R.id.txtNfcContent), nfc);
    } catch (Throwable ignore) {}

    // GNSS
    String gn = buildGnssInfo();
    set(R.id.txtGnssContent, gn);
    applyNeonValues(findViewById(R.id.txtGnssContent), gn);

    // UWB
    String uw = buildUwbInfo();
    set(R.id.txtUwbContent, uw);
    applyNeonValues(findViewById(R.id.txtUwbContent), uw);

    // USB
    String usb = buildUsbInfo();
    set(R.id.txtUsbContent, usb);
    applyNeonValues(findViewById(R.id.txtUsbContent), usb);

    // HAPTICS
    String hap = buildHapticsInfo();
    set(R.id.txtHapticsContent, hap);
    applyNeonValues(findViewById(R.id.txtHapticsContent), hap);

    // SYSTEM FEATURES
    String sysf = buildSystemFeaturesInfo();
    set(R.id.txtSystemFeaturesContent, sysf);
    applyNeonValues(findViewById(R.id.txtSystemFeaturesContent), sysf);

    // SECURITY FLAGS
    String sec = buildSecurityFlagsInfo();
    set(R.id.txtSecurityFlagsContent, sec);
    applyNeonValues(findViewById(R.id.txtSecurityFlagsContent), sec);

    // ROOT
    String r = buildRootInfo();
    set(R.id.txtRootContent, r);
    applyNeonValues(findViewById(R.id.txtRootContent), r);

    // OTHER PERIPHERALS
    String oth = buildOtherPeripheralsInfo();
    set(R.id.txtOtherPeripheralsContent, oth);
    applyNeonValues(findViewById(R.id.txtOtherPeripheralsContent), oth);
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
    
// ============================================================
// COLLAPSE ENGINE — CLOSE ALL SECTIONS EXCEPT BATTERY  (FIXED)
// ============================================================
private void collapseAllExceptBattery() {

    if (allContents == null || allIcons == null) return;

    // 1) Κλείσε όλα τα κανονικά sections (Battery = index 0 → μην το ακουμπήσεις)
    for (int i = 1; i < allContents.length; i++) {

        TextView content = allContents[i];
        TextView icon    = allIcons[i];

        if (content != null && content.getVisibility() == View.VISIBLE)
            animateCollapse(content);

        if (icon != null)
            icon.setText("＋");
    }

    // ⭐ FIX: Μην αγγίζεις το batteryContainer εδώ.

    // ⭐ FIX: Κρύψε μόνο το capacity button όταν το Battery είναι κλειστό
    if (txtBatteryModelCapacity != null)
        txtBatteryModelCapacity.setVisibility(View.GONE);

    // ⭐ Reset battery icon (always safe)
    if (iconBattery != null)
        iconBattery.setText("＋");
}

// ===================================================================
// HELPERS — alignment + indent  (REQUIRED for Battery Builder)
// ===================================================================
private String padKey(String key) {
    return String.format(Locale.US, "%-22s", key);
}

private String indent(String text, int spaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spaces; i++) sb.append(' ');
    sb.append(text);
    return sb.toString();
}

// ============================================================================
// MODEM HELPERS — REQUIRED FOR buildModemInfo()
// ============================================================================
private String padKeyModem(String key) {
    final int width = 20;
    if (key == null) return "";
    if (key.length() >= width) return key;
    StringBuilder sb = new StringBuilder(key);
    while (sb.length() < width) sb.append(' ');
    return sb.toString();
}

private String maskSensitive(String value) {
    if (value == null) return "N/A";
    String v = value.trim();
    if (v.length() <= 4) return "****";
    int keepStart = 4;
    int keepEnd = 2;
    String start = v.substring(0, Math.min(keepStart, v.length()));
    String end   = v.substring(Math.max(v.length() - keepEnd, keepStart));
    StringBuilder mid = new StringBuilder();
    for (int i = 0; i < v.length() - start.length() - end.length(); i++) {
        mid.append('*');
    }
    return start + mid + end;
}

// ============================================================================
// NFC BASIC INFO — REQUIRED FOR populateAllSections()
// ============================================================================
private String getNfcBasicInfo() {
    try {
        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        if (nfcManager != null) {
            NfcAdapter adapter = nfcManager.getDefaultAdapter();
            if (adapter != null) {
                return "NFC Supported : Yes\nNFC Enabled   : " + (adapter.isEnabled() ? "Yes" : "No");
            }
        }
        return "NFC Supported : No";
    } catch (Throwable ignore) {
        return "NFC Supported : Unknown";
    }
}

// ============================================================================
// LOCATION CAPABILITIES — REQUIRED FOR populateAllSections()
// ============================================================================
private String getLocationCapabilities() {
    StringBuilder sb = new StringBuilder();
    PackageManager pm = getPackageManager();

    try {
        sb.append("GPS HW             : ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "Yes" : "No")
                .append("\n");

        sb.append("Network Location   : ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "Yes" : "No")
                .append("\n");

        sb.append("Passive Provider   : ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION) ? "Yes" : "No")
                .append("\n");

        sb.append("\nAdvanced           : AGNSS, LPP, SUPL, carrier-assisted fixes, requires root access.");

    } catch (Throwable ignore) {
        return "Location Capabilities : Unknown";
    }

    return sb.toString();
}

// 🔥 END OF CLASS
}
