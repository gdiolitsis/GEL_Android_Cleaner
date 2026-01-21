// DeviceInfoPeripheralsActivity.java — MEGA UPGRADE v30
// Auto-Path Engine 5.3 + Root v5.1 + Permission Engine v25 (Manifest-Aware + Debug v24)

package com.gel.cleaner;

// ============================================================
// JAVA / UTIL
// ============================================================
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

// ============================================================
// ANDROID CORE
// ============================================================
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

// ============================================================
// ANDROID UI / VIEW
// ============================================================
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// ============================================================
// ANDROID MEDIA / AUDIO (MIC BENCH + LIVE MIC)
// ============================================================
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

// ============================================================
// ANDROID CONNECTIVITY
// ============================================================
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

// ============================================================
// ANDROID BLUETOOTH
// ============================================================
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;

// ============================================================
// ANDROID HARDWARE
// ============================================================
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

// ============================================================
// ANDROID LOCATION / NFC / TELEPHONY
// ============================================================
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.telecom.TelecomManager;
import android.telephony.CellInfo;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

// ============================================================
// ANDROIDX
// ============================================================
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Set;

// ============================================================
// STATIC
// ============================================================
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

// ============================================================  
// MAIN CLASS FIELDS  
// ============================================================  
private static final String NEON_GREEN = "#39FF14";  
private static final String GOLD_COLOR = "#FFD700";  
private static final int LINK_BLUE     = Color.parseColor("#1E90FF");  

private boolean isRooted = false;  

private View[] allContents;
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
private TextView iconBiometrics;
private TextView iconNfc;
private TextView iconGnss;
private TextView iconUwb;
private TextView iconUsb;
private TextView iconHaptics;
private TextView iconSystemFeatures;
private TextView iconSecurityFlags;
private TextView iconRoot;
private TextView iconOther;

// ============================================================
// TELEPHONY SNAPSHOT — GEL SINGLE SOURCE OF TRUTH
// ============================================================
private static class TelephonySnapshot {

boolean airplaneOn = false;  

    int simState = TelephonyManager.SIM_STATE_UNKNOWN;  
    boolean simReady = false;  

    int serviceState = ServiceState.STATE_OUT_OF_SERVICE;  
    boolean inService = false;  

    int dataState = TelephonyManager.DATA_UNKNOWN;  
}

// ============================================================
// attachBaseContext
// ============================================================
@Override
protected void attachBaseContext(Context base) {
super.attachBaseContext(LocaleHelper.apply(base));
}

// ============================================================
//  ON CREATE — FINAL CLEAN (AUDIO INCLUDED, NO LABS)
// ============================================================
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_info_peripherals);

    // ✅ ROOT FLAG — MUST BE HERE
    isRooted = isDeviceRooted();

// ------------------------------------------------------------  
// 1️⃣  TITLE  
// ------------------------------------------------------------  
TextView title = findViewById(R.id.txtTitleDevice);  
if (title != null)  
    title.setText(getString(R.string.phone_info_peripherals));  

// ------------------------------------------------------------  
// 2️⃣  BIND VIEWS (FULL UI READY)  
// ------------------------------------------------------------  
batteryContainer        = findViewById(R.id.batteryContainer);  
txtBatteryContent       = findViewById(R.id.txtBatteryContent);  
iconBattery             = findViewById(R.id.iconBatteryToggle);  
txtBatteryModelCapacity = findViewById(R.id.txtBatteryModelCapacity);  
initBatterySection();

txtScreenContent          = findViewById(R.id.txtScreenContent);  
txtCameraContent          = findViewById(R.id.txtCameraContent);  
txtConnectivityContent    = findViewById(R.id.txtConnectivityContent);  
txtLocationContent        = findViewById(R.id.txtLocationContent);  
txtThermalContent         = findViewById(R.id.txtThermalContent);  
txtModemContent           = findViewById(R.id.txtModemContent);  
txtWifiAdvancedContent    = findViewById(R.id.txtWifiAdvancedContent);  
txtAudioUnifiedContent    = findViewById(R.id.txtAudioUnifiedContent);  

txtSensorsContent         = findViewById(R.id.txtSensorsContent);  
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

// ------------------------------------------------------------  
// 3️⃣  MASTER ARRAYS (WITH AUDIO)  
// ------------------------------------------------------------  
allContents = new View[]{
        batteryContainer,            // ✅ Battery content = container
        txtScreenContent,
        txtCameraContent,
        txtConnectivityContent,
        txtLocationContent,
        txtThermalContent,
        txtModemContent,
        txtWifiAdvancedContent,
        txtAudioUnifiedContent,
        txtSensorsContent,
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

// ------------------------------------------------------------  
// 4️⃣  LOAD ALL SECTION TEXTS (LIGHT ONLY)  
// ------------------------------------------------------------  
populateAllSections();  

// ------------------------------------------------------------  
// 5️⃣  PERMISSIONS  
// ------------------------------------------------------------  
requestAllRuntimePermissions();  
requestPermissions(new String[]{  
        Manifest.permission.READ_PHONE_STATE,  
        Manifest.permission.READ_SMS,  
        Manifest.permission.READ_PHONE_NUMBERS  
}, 101);  

// ============================================================
// 5️⃣ BATTERY — MANUAL SECTION (FINAL, FIXED)
// ============================================================
LinearLayout headerBattery = findViewById(R.id.headerBattery);

if (headerBattery != null && batteryContainer != null) {
    headerBattery.setOnClickListener(v -> {

        boolean isOpen = batteryContainer.getVisibility() == View.VISIBLE;

        // 🔻 Κλείσε όλα τα άλλα sections
        if (allContents != null && allIcons != null) {
            for (int i = 1; i < allContents.length; i++) {
                if (allContents[i] != null)
                    allContents[i].setVisibility(View.GONE);
                if (allIcons[i] != null)
                    allIcons[i].setText("+");
            }
        }

        if (!isOpen) {
            // 🔺 ΑΝΟΙΓΜΑ BATTERY (ΚΑΙ ΕΝΕΡΓΟΠΟΙΗΣΗ)
            batteryContainer.setVisibility(View.VISIBLE);
            batteryContainer.setClickable(true);
            batteryContainer.setFocusable(true);
            batteryContainer.setFocusableInTouchMode(true);

            iconBattery.setText("-");
        } else {
            // 🔻 ΠΛΗΡΕΣ ΚΛΕΙΣΙΜΟ BATTERY
            batteryContainer.setVisibility(View.GONE);
            iconBattery.setText("+");
        }
    });
}

// ------------------------------------------------------------  
// 7️⃣  NORMAL SECTIONS (WITH AUDIO)  
// ------------------------------------------------------------  

setupSection(findViewById(R.id.headerScreen), txtScreenContent, iconScreen);  
setupSection(findViewById(R.id.headerCamera), txtCameraContent, iconCamera);  
setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);  
setupSection(findViewById(R.id.headerLocation), txtLocationContent, iconLocation);  
setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);  
setupSection(findViewById(R.id.headerModem), txtModemContent, iconModem);  
setupSection(findViewById(R.id.headerWifiAdvanced), txtWifiAdvancedContent, iconWifiAdvanced);  
setupSection(findViewById(R.id.headerAudioUnified), txtAudioUnifiedContent, iconAudioUnified);  
setupSection(findViewById(R.id.headerSensors), txtSensorsContent, iconSensors);  
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
// CONNECTIVITY INFO — SNAPSHOT BASED (FIXED)
// ============================================================
private String buildConnectivityInfo() {

    TelephonySnapshot s = getTelephonySnapshot();
    StringBuilder sb = new StringBuilder();

    sb.append("Airplane Mode: ")
      .append(s.airplaneOn ? "ON" : "OFF")
      .append("\n");

    sb.append("SIM State: ");
    switch (s.simState) {
        case TelephonyManager.SIM_STATE_READY:
            sb.append("READY");
            break;
        case TelephonyManager.SIM_STATE_ABSENT:
            sb.append("ABSENT");
            break;
        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            sb.append("PIN REQUIRED");
            break;
        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            sb.append("PUK REQUIRED");
            break;
        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            sb.append("NETWORK LOCKED");
            break;
        default:
            sb.append("UNKNOWN");
            break;
    }
    sb.append("\n");

    sb.append("Mobile Service: ")
      .append(s.inService ? "IN SERVICE" : "OUT OF SERVICE")
      .append("\n");

    sb.append("Mobile Data: ");
    switch (s.dataState) {
        case TelephonyManager.DATA_CONNECTED:
            sb.append("CONNECTED");
            break;
        case TelephonyManager.DATA_CONNECTING:
            sb.append("CONNECTING");
            break;
        case TelephonyManager.DATA_DISCONNECTED:
            sb.append("DISCONNECTED");
            break;
        default:
            sb.append("UNKNOWN");
            break;
    }
    sb.append("\n");
    
    sb.append(buildWifiAndBluetoothInfo());

    return sb.toString();
}

private TelephonySnapshot getTelephonySnapshot() {

    TelephonySnapshot s = new TelephonySnapshot();

    try {
        s.airplaneOn = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON,
                0
        ) == 1;
    } catch (Throwable ignore) {}

    TelephonyManager tm =
            (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

    if (tm != null) {

        try {
            s.simState = tm.getSimState();
            s.simReady = (s.simState == TelephonyManager.SIM_STATE_READY);
        } catch (Throwable ignore) {}

        try {
            ServiceState ss = tm.getServiceState();
            if (ss != null) {
                s.serviceState = ss.getState();
                s.inService = (s.serviceState == ServiceState.STATE_IN_SERVICE);
            }
        } catch (Throwable ignore) {}

        try {
            s.dataState = tm.getDataState();
        } catch (Throwable ignore) {}
    }

    return s;
}

// ============================================================
//  PERMISSION CALLBACK — FINAL CLEAN VERSION
// ============================================================
@Override
public void onRequestPermissionsResult(int requestCode,
@NonNull String[] permissions,
@NonNull int[] grantResults) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults);

// 🔹 GEL universal permissions  
if (requestCode == REQ_CODE_GEL_PERMISSIONS) {  
    // Δεν χρειάζεται κάτι άλλο εδώ προς το παρόν  
}  

// 🔹 TELEPHONY permissions (Active SIMs, IMSI, MSISDN)  
if (requestCode == 101) {  
    refreshModemInfo();   // Ξαναφορτώνει SIM + Modem block  
}

}

// ============================================================
// GEL Section Setup Engine — UNIVERSAL VERSION (Accordion Mode)
// Battery-Safe Edition (FINAL, FIXED — NO AUDIO)
// ============================================================
private void setupSection(View header, View content, TextView icon) {

    if (header == null || content == null || icon == null)
        return;

    // αρχική κατάσταση
    content.setVisibility(View.GONE);
    icon.setText("+"); // ΜΗΝ βάζεις unicode, μόνο ASCII

    header.setOnClickListener(v -> {

        boolean isOpen = (content.getVisibility() == View.VISIBLE);

        // ------------------------------------------------------------
        // 1️⃣ Κλείσε ΟΛΑ τα sections
        // ------------------------------------------------------------
        if (allContents != null && allIcons != null) {
            for (int i = 0; i < allContents.length; i++) {
                if (allContents[i] != null)
                    allContents[i].setVisibility(View.GONE);
                if (allIcons[i] != null)
                    allIcons[i].setText("+");
            }
        }

        // ------------------------------------------------------------
        // 2️⃣ Αν ήταν κλειστό → άνοιξέ το
        // ------------------------------------------------------------
        if (!isOpen) {
            content.setVisibility(View.VISIBLE);
            icon.setText("-");
        }
    });
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

        return "Settings → Battery";
    }

    // ============================================================
    // CAMERA / BIOMETRICS / SENSORS / CONNECTIVITY / LOCATION
    // ============================================================
   
// ============================================================
// CAMERA / FULL PHOTO + VIDEO CAPABILITY MAP
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

                // --------------------------------------------------
                // 📸 JPEG + 🎥 VIDEO STREAM CONFIGURATION
                // --------------------------------------------------
                try {
                    android.hardware.camera2.params.StreamConfigurationMap map =
                            cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    if (map != null) {

                        // JPEG
                        android.util.Size[] jpegSizes =
                                map.getOutputSizes(android.graphics.ImageFormat.JPEG);
                        if (jpegSizes != null && jpegSizes.length > 0) {
                            sb.append("• JPEG Modes     : ")
                              .append(jpegSizes.length)
                              .append(" available sizes\n");
                        }

                        // VIDEO (MediaRecorder)
                        android.util.Size[] videoSizes =
                                map.getOutputSizes(android.media.MediaRecorder.class);

                        if (videoSizes != null && videoSizes.length > 0) {

                            android.util.Size max = videoSizes[0];
                            for (android.util.Size s : videoSizes) {
                                if (s.getWidth() * s.getHeight() >
                                    max.getWidth() * max.getHeight()) {
                                    max = s;
                                }
                            }

                            sb.append("• Video Max      : ")
                              .append(max.getWidth()).append("x")
                              .append(max.getHeight()).append("\n");

                            sb.append("• Video Modes    : ")
                              .append(videoSizes.length)
                              .append(" resolutions\n");
                        }
                    }
                } catch (Throwable ignore) { }

                // --------------------------------------------------
                // 🎞 FPS RANGE
                // --------------------------------------------------
                android.util.Range<Integer>[] fpsRanges =
                        cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

                if (fpsRanges != null && fpsRanges.length > 0) {
                    int min = Integer.MAX_VALUE;
                    int max = 0;

                    for (android.util.Range<Integer> r : fpsRanges) {
                        min = Math.min(min, r.getLower());
                        max = Math.max(max, r.getUpper());
                    }

                    sb.append("• FPS Range      : ")
                      .append(min).append("–").append(max)
                      .append(" fps\n");
                }

                // --------------------------------------------------
                // 🎥 VIDEO STABILIZATION
                // --------------------------------------------------
                int[] stab =
                        cc.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);

                sb.append("• Stabilization  : ")
                  .append(stab != null && stab.length > 0 ? "Yes" : "No")
                  .append("\n");

                // --------------------------------------------------
                // 🌈 HDR VIDEO (10-bit capability)
                // --------------------------------------------------
                boolean hdr = false;

if (reqCaps != null && Build.VERSION.SDK_INT >= 33) {
    for (int c : reqCaps) {
        if (c ==
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT) {
            hdr = true;
            break;
        }
    }
}
                sb.append("• HDR Video      : ")
                  .append(hdr ? "Yes" : "No")
                  .append("\n");

                // --------------------------------------------------
                // 🔧 CAPABILITIES COUNT
                // --------------------------------------------------
                if (reqCaps != null) {
                    sb.append("• Capabilities   : ")
                      .append(reqCaps.length)
                      .append(" flags\n");
                }

                // --------------------------------------------------
                // ⚙️ HARDWARE LEVEL
                // --------------------------------------------------
                if (hwLevel != null) {
                    String level;
                    switch (hwLevel) {
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                            level = "FULL"; break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                            level = "LIMITED"; break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                            level = "LEGACY"; break;
                        case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                            level = "LEVEL_3"; break;
                        default:
                            level = "UNKNOWN";
                    }
                    sb.append("• HW Level       : ").append(level).append("\n");
                }

                sb.append("\n");
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
//   BIOMETRICS — GEL CLEAN EDITION (API29-SAFE)
// ============================================================
private String buildBiometricsInfo() {
    StringBuilder sb = new StringBuilder();

    PackageManager pm = getPackageManager();

    boolean hasFp   = pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
    boolean hasFace = pm.hasSystemFeature("android.hardware.biometrics.face");
    boolean hasIris = pm.hasSystemFeature("android.hardware.biometrics.iris");

    sb.append("Fingerprint        : ").append(hasFp   ? "Yes" : "No").append("\n");
    sb.append("Face Unlock        : ").append(hasFace ? "Yes" : "No").append("\n");
    sb.append("Iris Scan          : ").append(hasIris ? "Yes" : "No").append("\n");

    // ------------------------------------------------------------
    // Under-Display Fingerprint (UDFPS detection)
    // ------------------------------------------------------------
    boolean udFps = false;
    try {
        udFps = pm.hasSystemFeature("com.motorola.hardware.fingerprint.udfps")
              || pm.hasSystemFeature("com.samsung.hardware.fingerprint.udfps")
              || pm.hasSystemFeature("com.google.hardware.biometrics.udfps")
              || pm.hasSystemFeature("vendor.samsung.hardware.biometrics.fingerprint.udfps")
              || pm.hasSystemFeature("vendor.xiaomi.hardware.fingerprint.udfps");
    } catch (Throwable ignore) {}

    if (hasFp) {
        sb.append("Under-Display FP   : ")
          .append(udFps ? "Yes" : "No")
          .append("\n");
    }

    // ------------------------------------------------------------
    // Biometric Profile
    // ------------------------------------------------------------
    int modes = (hasFp ? 1 : 0) + (hasFace ? 1 : 0) + (hasIris ? 1 : 0);

    sb.append("Profile            : ");
    if (modes == 0) {
        sb.append("No biometric hardware\n");
    } else if (modes == 1) {
        sb.append("Single biometric\n");
    } else {
        sb.append("Multi-biometric (").append(modes).append(")\n");
    }

    return sb.toString();
}

// ------------------------------------------------------------
// SENSORS — CLEAN GEL DIAGNOSTIC SUMMARY
// ------------------------------------------------------------
private String buildSensorsInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {

            Sensor accelerometer   = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gyroscope       = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            Sensor magnetometer    = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor barometer       = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
            Sensor proximity       = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            Sensor light           = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

            Sensor stepCounter     = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            Sensor stepDetector    = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            Sensor rotationVector  = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            Sensor gameRotation    = sm.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            Sensor gravity         = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
            Sensor linearAccel     = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

            Sensor significantMot  = sm.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
            Sensor stationaryDet   = sm.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT);
            Sensor motionDet       = sm.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);

            sb.append("Summary:\n");
            sb.append("Accelerometer        : ").append(accelerometer  != null ? "Yes" : "No").append("\n");
            sb.append("Gyroscope            : ").append(gyroscope      != null ? "Yes" : "No").append("\n");
            sb.append("Magnetometer         : ").append(magnetometer   != null ? "Yes" : "No").append("\n");
            sb.append("Barometer            : ").append(barometer      != null ? "Yes" : "No").append("\n");
            sb.append("Proximity            : ").append(proximity      != null ? "Yes" : "No").append("\n");
            sb.append("Light Sensor         : ").append(light          != null ? "Yes" : "No").append("\n");

            sb.append("Step Counter         : ").append(stepCounter    != null ? "Yes" : "No").append("\n");
            sb.append("Step Detector        : ").append(stepDetector   != null ? "Yes" : "No").append("\n");

            sb.append("Rotation Vector      : ").append(rotationVector != null ? "Yes" : "No").append("\n");
            sb.append("Game Rotation Vector : ").append(gameRotation   != null ? "Yes" : "No").append("\n");
            sb.append("Gravity Sensor       : ").append(gravity        != null ? "Yes" : "No").append("\n");
            sb.append("Linear Acceleration  : ").append(linearAccel    != null ? "Yes" : "No").append("\n");

            sb.append("Significant Motion   : ").append(significantMot != null ? "Yes" : "No").append("\n");
            sb.append("Stationary Detect    : ").append(stationaryDet  != null ? "Yes" : "No").append("\n");
            sb.append("Motion Detect        : ").append(motionDet      != null ? "Yes" : "No").append("\n");
        }
    } catch (Throwable ignore) { }

    if (sb.length() == 0) {
        sb.append("No sensor information is exposed by this device.\n");
    }

    appendAccessInstructions(sb, "sensors");
    return sb.toString();
}

// ============================================================
// WIFI + BLUETOOTH INFO — CONNECTIVITY EXTENSION (FULL + ROOT)
// ============================================================
private String buildWifiAndBluetoothInfo() {

    StringBuilder sb = new StringBuilder();

    // ============================================================
    // WIFI (FULL DETAILS)
    // ============================================================
    try {

        WifiManager wm = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (wm != null) {
            WifiInfo wi = wm.getConnectionInfo();

            if (wi != null && wi.getNetworkId() != -1) {

                sb.append("\nWi-Fi Details:\n");
                sb.append("  SSID           : ").append(wi.getSSID()).append("\n");
                sb.append("  LinkSpeed      : ").append(wi.getLinkSpeed()).append(" Mbps\n");
                sb.append("  RSSI           : ").append(wi.getRssi()).append(" dBm\n");
                sb.append("  Frequency      : ").append(wi.getFrequency()).append(" MHz\n");

                // ------------------------------------------------------------
                // HUMAN SIGNAL QUALITY
                // ------------------------------------------------------------
                int rssi = wi.getRssi();
                String quality =
                        rssi >= -50 ? "Excellent" :
                        rssi >= -60 ? "Good" :
                        rssi >= -70 ? "Fair" :
                        "Weak";

                sb.append("  Signal Quality : ").append(quality).append("\n");

                // ------------------------------------------------------------
                // BAND NAME (SAFE)
                // ------------------------------------------------------------
                int freq = wi.getFrequency();
                String band =
                        freq >= 5925 ? "6 GHz (Wi-Fi 6E)" :
                        freq >= 4900 ? "5 GHz" :
                        "2.4 GHz";

                sb.append("  Band           : ").append(band).append("\n");

// ------------------------------------------------------------
// WIFI STANDARD (SAFE — SDK INDEPENDENT)
// ------------------------------------------------------------
if (android.os.Build.VERSION.SDK_INT >= 30) {

    int std = wi.getWifiStandard();
    String stdStr;

    switch (std) {
        case 6:  stdStr = "Wi-Fi 6 / 6E (802.11ax)"; break; // WIFI_STANDARD_11AX
        case 5:  stdStr = "Wi-Fi 5 (802.11ac)";     break; // WIFI_STANDARD_11AC
        case 4:  stdStr = "Wi-Fi 4 (802.11n)";      break; // WIFI_STANDARD_11N
        case 1:  stdStr = "802.11a";                break;
        case 2:  stdStr = "802.11b";                break;
        case 3:  stdStr = "802.11g";                break;
        default: stdStr = "Unknown";                break;
    }

    sb.append("  Wi-Fi Standard : ").append(stdStr).append("\n");
}

                // ------------------------------------------------------------
                // SAFE MAC (masked vs real, root-aware)
                // ------------------------------------------------------------
                String rawMac = wi.getMacAddress();
                String macLine;

                if (rawMac != null
                        && rawMac.length() > 0
                        && !"02:00:00:00:00:00".equals(rawMac)) {

                    macLine = rawMac;   // real MAC (root / older Android)

                } else {

                    if (!isDeviceRooted()) {
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

    BluetoothManager bm = null;
    BluetoothAdapter ba = null;

    try {
        bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ba = (bm != null) ? bm.getAdapter() : null;
    } catch (Throwable ignore) {}

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
            btName = isDeviceRooted()
                    ? "Unavailable"
                    : "Unavailable (requires root access)";
        }

        String btAddr = ba.getAddress();
        if (btAddr == null
                || btAddr.trim().isEmpty()
                || "02:00:00:00:00:00".equals(btAddr)) {

            btAddr = isDeviceRooted()
                    ? "Unavailable"
                    : "Masked by Android security (requires root access)";
        }

        sb.append("  Name           : ").append(btName).append("\n");
        sb.append("  Address        : ").append(btAddr).append("\n");

        // ------------------------------------------------------------
        // BLUETOOTH VERSION — REALITY CHECK (Android does NOT expose)
        // ------------------------------------------------------------
        if (isDeviceRooted()) {

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
                .append(ba.getScanMode() ==
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
                        ? "Yes" : "No")
                .append("\n");

        boolean le = getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        sb.append("  BLE Support    : ").append(le ? "Yes" : "No").append("\n");

        // ------------------------------------------------------------
        // TRANSPORT TYPE (SAFE INTERPRETATION)
        // ------------------------------------------------------------
        sb.append("  Transport Type : ");

        if (!ba.isEnabled()) {
            sb.append("Inactive\n");
        } else if (le) {
            sb.append("Classic + Low Energy (supported)\n");
        } else {
            sb.append("Classic only\n");
        }

        // ------------------------------------------------------------
        // HARDWARE CAPABILITIES
        // ------------------------------------------------------------
        sb.append("  Multiple Adv   : ");
        try {
            sb.append(ba.isMultipleAdvertisementSupported()
                    ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {
            sb.append("Unknown\n");
        }

        sb.append("  LE Scanner     : ");
        try {
            sb.append(ba.getBluetoothLeScanner() != null
                    ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {
            sb.append("Unknown\n");
        }

        sb.append("  Offloaded Filt.: ");
        try {
            sb.append(ba.isOffloadedFilteringSupported()
                    ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {
            sb.append("Unknown\n");
        }

        // ------------------------------------------------------------
        // CONNECTED DEVICES (GATT)
        // ------------------------------------------------------------
        try {
            List<BluetoothDevice> con =
                    (bm != null)
                            ? bm.getConnectedDevices(BluetoothProfile.GATT)
                            : null;

            sb.append("  GATT Devices   : ")
                    .append(con != null ? con.size() : 0)
                    .append("\n");

        } catch (Throwable ignore) {

            sb.append("  GATT Devices   : Unknown\n");
        }

        // ------------------------------------------------------------
        // CONNECTED PROFILES (HUMAN)
        // ------------------------------------------------------------
        try {
            List<BluetoothDevice> audio =
                    (bm != null) ? bm.getConnectedDevices(BluetoothProfile.A2DP) : null;

            sb.append("  Audio Devices  : ")
                    .append(audio != null ? audio.size() : 0)
                    .append("\n");
        } catch (Throwable ignore) {}

        try {
            List<BluetoothDevice> headset =
                    (bm != null) ? bm.getConnectedDevices(BluetoothProfile.HEADSET) : null;

            sb.append("  Headset Devices: ")
                    .append(headset != null ? headset.size() : 0)
                    .append("\n");
        } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // ROOT EXCLUSIVE PATHS (vendor logs / firmware info)
        // ------------------------------------------------------------
        sb.append("\n  Firmware Info  : ");

        if (isDeviceRooted()) {

            String fw = null;

            try {
                if (fw == null) fw = readSysString("/vendor/firmware/bt/default/bt_version.txt");
                if (fw == null) fw = readSysString("/system/etc/bluetooth/bt_stack.conf");
                if (fw == null) fw = readSysString("/vendor/etc/bluetooth/bt_stack.conf");
                if (fw == null) fw = readSysString("/proc/bluetooth/soc");
            } catch (Throwable ignore) {}

            sb.append(fw != null && !fw.isEmpty()
                    ? fw
                    : "Not exposed by vendor").append("\n");

        } else {

            sb.append("Requires root access\n");
        }

        // ------------------------------------------------------------
        // USER-CENTRIC INFO (NON-INTRUSIVE ADDITIONS)
        // ------------------------------------------------------------
        sb.append("\n  Usage summary   :\n");

        if (bm != null) {

            try {
                Set<BluetoothDevice> bonded = ba.getBondedDevices();
                sb.append("    Paired devices      : ")
                        .append(bonded != null ? bonded.size() : 0)
                        .append("\n");
            } catch (Throwable ignore) {}

            try {
                List<BluetoothDevice> gatt =
                        bm.getConnectedDevices(BluetoothProfile.GATT);

                sb.append("    Active connections  : ")
                        .append(gatt != null ? gatt.size() : 0)
                        .append("\n");
            } catch (Throwable ignore) {}
        }

        sb.append("    Audio usage         : Supports wireless audio devices ")
                .append("(headphones, car systems, speakers)\n");

        boolean le2 = getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        sb.append("    Low Energy usage    : ")
                .append(le2
                        ? "Supported (wearables, sensors, trackers)"
                        : "Not supported")
                .append("\n");

        sb.append("    Bluetooth generation: ")
                .append(isDeviceRooted()
                        ? "Controller-defined (vendor firmware)"
                        : "Managed by Android system")
                .append("\n");

        // ------------------------------------------------------------
        // ADVANCED INFO (green comment style)
        // ------------------------------------------------------------
        sb.append("\nDeep Stats       : Advanced interface counters, raw RF tables, ")
                .append("Bluetooth controller logs and HCI traces, requires root access.\n");
    }

    return sb.toString();
}
      
// ===================================================================
// MODEL CAPACITY STORAGE (SharedPreferences) — FINAL GEL EDITION
// ===================================================================
private static final String PREFS_NAME_BATTERY = "gel_prefs";
private static final String KEY_BATTERY_MODEL_CAPACITY = "battery_model_capacity";
private static final String KEY_BATTERY_DIALOG_SHOWN   = "battery_dialog_shown";

// ===================================================================
// BATTERY DATA STRUCT (ROOT-AWARE)
// ===================================================================
private static class BatteryInfo {
    int level = -1;
    int scale = -1;
    String status = "N/A";
    String chargingSource = "Unknown";
    float temperature = 0f;

    long currentChargeMah  = -1;   // Charge Counter / charge_now
    long estimatedFullMah  = -1;   // charge_full / derived
    long designFullMah     = -1;   // charge_full_design
    long cycleCount        = -1;   // root only
    long internalResistance= -1;   // root only (mΩ if available)

    String source          = "Unknown";
    boolean rootedData     = false;
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
// ROOT-AWARE SYSFS READ
// ===================================================================
private long readSysLongRootAware(String path) {
    try {
        long v = readSysLong(path);
        if (v > 0) return v;
    } catch (Throwable ignore) {}

    try {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat " + path});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        if (line != null) {
            long v = Long.parseLong(line.replaceAll("[^0-9]", ""));
            if (v > 0) return v;
        }
    } catch (Throwable ignore) {}

    return -1;
}

// ===================================================================
// UNIVERSAL BATTERY SCANNER — GEL v8.0 (ROOT UNLOCKED)
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

    // ---------- 2) ROOT OEM / BMS DATA ----------
    if (isDeviceRooted()) {

        bi.rootedData = true;

        bi.designFullMah = normalizeMah(readSysLongRootAware(
                "/sys/class/power_supply/battery/charge_full_design"));

        bi.estimatedFullMah = normalizeMah(readSysLongRootAware(
                "/sys/class/power_supply/battery/charge_full"));

        bi.currentChargeMah = normalizeMah(readSysLongRootAware(
                "/sys/class/power_supply/battery/charge_now"));

        bi.cycleCount = readSysLongRootAware(
                "/sys/class/power_supply/battery/cycle_count");

        bi.internalResistance = readSysLongRootAware(
                "/sys/class/power_supply/battery/resistance");

        if (bi.estimatedFullMah > 0) bi.source = "OEM (root)";
    }

    // ---------- 3) CHARGE COUNTER FALLBACK ----------
    if (bi.currentChargeMah <= 0) {
        try {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                long cc = normalizeMah(
                        bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));
                if (cc > 0) {
                    bi.currentChargeMah = cc;
                    bi.source = "Charge Counter";
                    if (bi.level > 0) {
                        long est = (long) (cc / (bi.level / 100f));
                        if (est > 0) bi.estimatedFullMah = est;
                    }
                }
            }
        } catch (Throwable ignore) {}
    }

    return bi;
}

// ===================================================================
// BATTERY INFO BUILDER — GEL PREMIUM + ROOT PRO
// ===================================================================
private String buildBatteryInfo() {

    BatteryInfo bi = getBatteryInfo();
    if (bi == null) bi = new BatteryInfo();

    long modelCap = getStoredModelCapacity();

    StringBuilder sb = new StringBuilder();

    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Level"),
            bi.level >= 0 ? bi.level + "%" : "N/A"));
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Status"), bi.status));
    sb.append(String.format(Locale.US, "%s : %s\n", padKey("Charging source"), bi.chargingSource));
    sb.append(String.format(Locale.US, "%s : %.1f°C\n\n", padKey("Temp"), bi.temperature));

    if (bi.currentChargeMah > 0)
        sb.append(String.format(Locale.US, "%s : %d mAh\n",
                padKey("Current charge"), bi.currentChargeMah));

    if (bi.estimatedFullMah > 0)
        sb.append(String.format(Locale.US, "%s : %d mAh\n",
                padKey("Estimated full"), bi.estimatedFullMah));

    if (modelCap > 0)
        sb.append(String.format(Locale.US, "%s : %d mAh\n",
                padKey("Model capacity"), modelCap));

    sb.append(String.format(Locale.US, "%s : %s\n",
            padKey("Source"), bi.source));

    // ---------- ROOT PRO SECTION ----------
    if (bi.rootedData) {

        sb.append("\n");
        sb.append("=== ROOT BATTERY DATA ===\n");

        if (bi.designFullMah > 0)
            sb.append(String.format(Locale.US,
                    "%s : %d mAh\n", padKey("Design capacity"), bi.designFullMah));

        if (bi.designFullMah > 0 && bi.estimatedFullMah > 0) {
            int soh = (int) Math.round(
                    (bi.estimatedFullMah * 100.0) / bi.designFullMah);
            sb.append(String.format(Locale.US,
                    "%s : %d %%\n", padKey("SOH (raw)"), soh));
        }

        if (bi.cycleCount > 0)
            sb.append(String.format(Locale.US,
                    "%s : %d\n", padKey("Cycle count"), bi.cycleCount));

        if (bi.internalResistance > 0)
            sb.append(String.format(Locale.US,
                    "%s : %d mΩ\n", padKey("Internal resistance"), bi.internalResistance));
    }
    else {
        sb.append("\n");
        sb.append(String.format(Locale.US,
                "%s : %s\n", padKey("Lifecycle"), "Requires root access"));
    }

    return sb.toString();
}

// ===================================================================
// REFRESH VIEW
// ===================================================================
private void refreshBatteryInfoView() {
    try {
        if (txtBatteryContent != null) {
            txtBatteryContent.setText(buildBatteryInfo());
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
        btn.setText(cap > 0
                ? "Set model capacity (" + cap + " mAh)"
                : "Set model capacity");
    }
}

// ===================================================================
// INIT BATTERY SECTION (DIAGNOSTIC MODE)
// ===================================================================
private void initBatterySection() {

    txtBatteryContent = findViewById(R.id.txtBatteryContent);
    TextView btnCapacity = findViewById(R.id.txtBatteryModelCapacity);

    refreshBatteryInfoView();

    if (btnCapacity != null) {
        btnCapacity.setOnClickListener(v -> showBatteryCapacityDialog());
    }

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
// HAPTICS — SAFE EDITION (API 29–34)
// ============================================================
private String buildHapticsInfo() {
    StringBuilder sb = new StringBuilder();

    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    if (v == null) {
        sb.append("Vibration Engine : Not available\n");
        return sb.toString();
    }

    sb.append("Vibration Engine : ");

    if (Build.VERSION.SDK_INT >= 29) {
        if (v.hasAmplitudeControl()) sb.append("Amplitude Control\n");
        else sb.append("Basic Engine\n");
    } else {
        sb.append("Legacy Engine\n");
    }

    sb.append("Advanced         : Low-level haptic patterns require root/kernel access.\n");

    return sb.toString();
}

// ============================================================
// GNSS / LOCATION — GEL CLEAN EDITION (API-SAFE)
// ============================================================
private String buildGnssInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        PackageManager pm = getPackageManager();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // ---------------------------------------------------
        // PROVIDERS STATUS
        // ---------------------------------------------------
        if (lm != null) {
            boolean gps = false;
            boolean net = false;
            try { gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER); } catch (Throwable ignore) {}
            try { net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER); } catch (Throwable ignore) {}

            sb.append("GPS Provider      : ").append(gps ? "Enabled" : "Disabled").append("\n");
            sb.append("Network Location  : ").append(net ? "Enabled" : "Disabled").append("\n");
        }

        // ---------------------------------------------------
        // CONSTELLATIONS (CAPABILITY MATRIX)
        // ---------------------------------------------------
        sb.append("\n=== Constellations ===\n");
        sb.append("\n"); // empty line for visual separation    
        sb.append("GPS               : ")
                .append(pm.hasSystemFeature("android.hardware.location.gps") ? "Yes" : "No").append("\n");
        sb.append("GLONASS           : ")
                .append(pm.hasSystemFeature("android.hardware.location.glonass") ? "Yes" : "No").append("\n");
        sb.append("Galileo           : ")
                .append(pm.hasSystemFeature("android.hardware.location.galileo") ? "Yes" : "No").append("\n");
        sb.append("BeiDou            : ")
                .append(pm.hasSystemFeature("android.hardware.location.beidou") ? "Yes" : "No").append("\n");
        sb.append("QZSS              : ")
                .append(pm.hasSystemFeature("android.hardware.location.qzss") ? "Yes" : "No").append("\n");
        sb.append("SBAS              : ")
                .append(pm.hasSystemFeature("android.hardware.location.sbas") ? "Yes" : "No").append("\n");
        sb.append("NavIC / IRNSS     : ")
                .append(pm.hasSystemFeature("android.hardware.location.irnss") ? "Yes" : "No").append("\n");

        // ---------------------------------------------------
        // CORE GNSS CAPABILITIES
        // ---------------------------------------------------
        boolean raw   = pm.hasSystemFeature("android.hardware.location.gnss.raw_measurement");
        boolean batch = pm.hasSystemFeature("android.hardware.location.gnss.batch");

        sb.append("\n=== Capabilities ===\n");
        sb.append("\n"); // empty line for visual separation    
        sb.append("Raw Measurements  : ").append(raw ? "Yes" : "No").append("\n");
        sb.append("GNSS Batching     : ").append(batch ? "Yes" : "No").append("\n");

        // ---------------------------------------------------
        // NMEA (BASIC SUPPORT INDICATOR)
        // ---------------------------------------------------
        sb.append("NMEA Support      : ").append(lm != null ? "Yes" : "No").append("\n");

    } catch (Throwable ignore) {
        sb.append("GNSS information is not exposed on this device.\n");
    }

    return sb.toString();
}
      
// ============================================================
// USB / OTG / POWER / ROLE ENGINE — GEL API29-SAFE EDITION
// ============================================================
private String buildUsbInfo() {
    StringBuilder sb = new StringBuilder();

    PackageManager pm = getPackageManager();

    // ------------------------------------------------------------
    // BASIC SUPPORT FLAGS
    // ------------------------------------------------------------
    boolean otg = pm.hasSystemFeature("android.hardware.usb.host");
    boolean acc = pm.hasSystemFeature("android.hardware.usb.accessory");

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
    // CONNECTED USB DEVICES (HOST MODE)
    // ------------------------------------------------------------
    try {
        HashMap<String, UsbDevice> devs = um.getDeviceList();

        if (devs != null && !devs.isEmpty()) {
            sb.append("\nConnected USB Devices:\n");

            for (UsbDevice d : devs.values()) {
                sb.append("  • ").append(d.getDeviceName()).append("\n");
                sb.append("    Vendor ID    : ").append(d.getVendorId()).append("\n");
                sb.append("    Product ID   : ").append(d.getProductId()).append("\n");
                sb.append("    Class/Subcls : ")
                        .append(d.getDeviceClass()).append("/")
                        .append(d.getDeviceSubclass()).append("\n");
                sb.append("    Interfaces   : ").append(d.getInterfaceCount()).append("\n");
                sb.append("    USB Speed    : Not exposed by public API\n");
            }
        } else {
            sb.append("Connected Dev.   : None\n");
        }

    } catch (Throwable ignore) {
        sb.append("Connected Dev.  : Error reading USB devices\n");
    }

    // ------------------------------------------------------------
    // USB ROLE / MODE
    // ------------------------------------------------------------
    sb.append("\n=== Mode / Role ===\n");
    sb.append("\n"); // empty line for visual separation    
    sb.append(" USB Role        : Vendor HAL not exposed\n");

    // ------------------------------------------------------------
    // POWER / CHARGING PROFILE
    // ------------------------------------------------------------
    sb.append("\n=== Power Profiles ===\n");
    sb.append("\n"); // empty line for visual separation    
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

            sb.append(" Charge Source   : ").append(srcLabel).append("\n");

            int volt = batt.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
            sb.append(" Voltage (mV)    : ").append(volt).append("\n");
        }

    } catch (Throwable ignore) {
        sb.append("  Power Info    : Error\n");
    }

    // ------------------------------------------------------------
// CHARGE CURRENT (mA) — ROOT ONLY
// ------------------------------------------------------------
Integer mA = getRootChargeCurrentMilliAmps();
if (mA != null) {
    sb.append(" Charge (mA)     : ").append(mA).append("\n");
} else {
    sb.append(" Charge (mA)     : N/A (requires root access)\n");
}

    // ------------------------------------------------------------
    // FINAL NOTE
    // ------------------------------------------------------------
    sb.append("\nAdvanced         : USB descriptors, role switching and power negotiation require root access.\n");

    return sb.toString();
}


// ============================================================
// ROOT HELPERS — CHARGE CURRENT (µA → mA)
// ============================================================
private Integer getRootChargeCurrentMilliAmps() {
    try {
        if (!isRootAvailable()) return null;

        String[] paths = new String[] {
                "/sys/class/power_supply/battery/current_now",
                "/sys/class/power_supply/battery/input_current_now",
                "/sys/class/power_supply/usb/current_now",
                "/sys/class/power_supply/usb/input_current_now",
                "/sys/class/power_supply/main/current_now",
                "/sys/class/power_supply/ac/current_now",
                "/sys/class/power_supply/charger/current_now"
        };

        for (String p : paths) {
            String out = suCatFirstLine(p);
            Integer uA = parseIntSafe(out);
            if (uA != null) {
                return (int) Math.round(uA / 1000.0); // µA → mA
            }
        }
        return null;

    } catch (Throwable ignore) {
        return null;
    }
}

private boolean isRootAvailable() {
    try {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        p.waitFor();
        return line != null && line.contains("uid=0");
    } catch (Throwable t) {
        return false;
    }
}

private String suCatFirstLine(String path) {
    try {
        Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat " + path});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        p.waitFor();
        return (line == null || line.trim().isEmpty()) ? null : line.trim();
    } catch (Throwable t) {
        return null;
    }
}

private Integer parseIntSafe(String s) {
    try {
        if (s == null) return null;
        s = s.replaceAll("[^0-9\\-+]", "");
        if (s.isEmpty()) return null;
        return Integer.parseInt(s);
    } catch (Throwable t) {
        return null;
    }
}

// ============================================================
//  Other Peripherals — CLEAN GEL EDITION
// ============================================================
private String buildOtherPeripheralsInfo() {
    StringBuilder sb = new StringBuilder();
    PackageManager pm = getPackageManager();

    sb.append("=== Other Peripherals ===\n");
sb.append("\n"); // empty line for visual separation
     
    boolean ir       = pm.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
    boolean fm       = pm.hasSystemFeature("android.hardware.fm");
    boolean hall     = pm.hasSystemFeature("android.hardware.sensor.hall");
    boolean hwkbd    = pm.hasSystemFeature("android.hardware.keyboard");
    boolean wireless = pm.hasSystemFeature("android.hardware.power.wireless_charging");
    boolean tv       = pm.hasSystemFeature("android.hardware.tv.tuner");
    boolean barcode  = pm.hasSystemFeature("android.hardware.barcodescanner");

    sb.append("IR Blaster        : ").append(ir ? "Yes" : "No").append("\n");
    sb.append("FM Radio          : ").append(fm ? "Yes" : "No").append("\n");
    sb.append("Hall Sensor       : ").append(hall ? "Yes" : "No").append("\n");
    sb.append("HW Keyboard       : ").append(hwkbd ? "Yes" : "No").append("\n");
    sb.append("Wireless Charging : ").append(wireless ? "Yes" : "No").append("\n");
    sb.append("TV Tuner          : ").append(tv ? "Yes" : "No").append("\n");
    sb.append("Barcode Module    : ").append(barcode ? "Yes" : "No").append("\n");

    sb.append("\nAdvanced          : Extended peripheral diagnostics require root access.\n");

    return sb.toString();
}

// ============================================================================
// AUDIO SYSTEM — CLEAN PERIPHERALS BLOCK (NO TESTS, NO HEAVY OPS)
// Speakers / Microphones / HAL / Extended
// GEL — Play Store Safe, Zero Lag
// ============================================================================

// ============================================================================
// 1) MICROPHONES — DETECTION ONLY (NO RECORDING)
// ============================================================================
private String buildMicsInfo() {

    StringBuilder sb = new StringBuilder();

    boolean hasBuiltin = false, hasTele = false, hasWired = false, hasBT = false, hasUSB = false;
    int wired = 0, bt = 0, usb = 0;

    try {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {

            AudioDeviceInfo[] devs = am.getDevices(AudioManager.GET_DEVICES_INPUTS);

            for (AudioDeviceInfo d : devs) {

                switch (d.getType()) {

                    case AudioDeviceInfo.TYPE_BUILTIN_MIC:
                        sb.append("• Built-in Microphone\n")
                          .append("   Role          : ")
                          .append(hasBuiltin ?   "noise-cancel mic" : "Primary microphone")
                          .append("\n   Present       : Yes\n\n");
                        hasBuiltin = true;
                        break;

                    case AudioDeviceInfo.TYPE_TELEPHONY:
    sb.append("• Telephony Microphone\n")
      .append("   Role          : Dedicated voice call\n")
      .append("   Present       : Yes\n\n");
    hasTele = true;
    break;

                    case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                    case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                        wired++; hasWired = true;
                        break;

                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                        bt++; hasBT = true;
                        break;

                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET:
                        usb++; hasUSB = true;
                        break;
                }
            }
        }
    } catch (Throwable ignore) {}

    sb.append("=== Summary ===\n");
    sb.append("\n"); // empty line for visual separation
    sb.append("Built-in Mic     : ").append(hasBuiltin ? "Yes" : "No").append("\n");
    sb.append("Telephony Mic    : ").append(hasTele    ? "Yes" : "No").append("\n");
    sb.append("Wired Mics       : ").append(hasWired   ? "Yes ("+wired+")" : "No").append("\n");
    sb.append("Bluetooth Mics   : ").append(hasBT      ? "Yes ("+bt+")"    : "No").append("\n");
    sb.append("USB Mics         : ").append(hasUSB     ? "Yes ("+usb+")"   : "No").append("\n");

    return sb.toString();
}

// ============================================================================
// 2) AUDIO OUTPUTS / HAL — DETECTION ONLY
// ============================================================================
private String buildAudioHalInfo() {

    StringBuilder sb = new StringBuilder();

    String hal = getProp("ro.audio.hal.version");
    sb.append("Audio HAL        : ")
      .append((hal != null && !hal.isEmpty()) ? hal : "Not exposed")
      .append("\n\n");

    boolean speaker=false, wired=false, bt=false, usb=false, hdmi=false;

    try {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {

            AudioDeviceInfo[] outs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

            for (AudioDeviceInfo o : outs) {
                switch (o.getType()) {
                    case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER: speaker=true; break;
                    case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                    case AudioDeviceInfo.TYPE_WIRED_HEADSET:   wired=true;   break;
                    case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                    case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:   bt=true;      break;
                    case AudioDeviceInfo.TYPE_USB_DEVICE:
                    case AudioDeviceInfo.TYPE_USB_HEADSET:     usb=true;     break;
                    case AudioDeviceInfo.TYPE_HDMI:            hdmi=true;    break;
                }
            }
        }
    } catch (Throwable ignore) {}

    sb.append("=== Summary ===\n");
    sb.append("\n"); // empty line for visual separation
    sb.append("Speaker Output   : ").append(speaker ? "Yes" : "No").append("\n");
    sb.append("Wired Output     : ").append(wired   ? "Yes" : "No").append("\n");
    sb.append("Bluetooth Output : ").append(bt      ? "Yes" : "No").append("\n");
    sb.append("USB Output       : ").append(usb     ? "Yes" : "No").append("\n");
    sb.append("HDMI Output      : ").append(hdmi    ? "Yes" : "No").append("\n");

    return sb.toString();
}

// ============================================================================
// 3) AUDIO EXTENDED — SAFE FLAGS
// ============================================================================
private String buildAudioExtendedInfo() {

    StringBuilder sb = new StringBuilder();

    try {
        boolean hw = getPackageManager().hasSystemFeature("android.hardware.audio.output");
        sb.append("Audio Output HW  : ").append(hw ? "Yes" : "No").append("\n");
    } catch (Throwable ignore) {}

    return sb.toString();
}

// ============================================================================
// 4) UNIFIED AUDIO BLOCK — PERIPHERALS VIEW (LIGHT ONLY)
// ============================================================================
private String buildAudioUnifiedInfo() {

    StringBuilder sb = new StringBuilder();

    sb.append("=== Microphones ===\n");
    sb.append("\n"); // empty line for visual separation    
    sb.append(buildMicsInfo()).append("\n");

    sb.append("=== Audio Outputs / HAL ===\n");
    sb.append("\n"); // empty line for visual separation
    sb.append(buildAudioHalInfo()).append("\n");

    sb.append("=== Extended Audio Paths ===\n");
    sb.append("\n"); // empty line for visual separation    
    sb.append(buildAudioExtendedInfo()).append("\n");

    return sb.toString();
}

 // ============================================================
// Root Info (root-ready, honest limits)
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

    // ------------------------------------------------------------
    // Fusion Layer
    // ------------------------------------------------------------
    sb.append("\nFusion Layer     : ");
    if (isRooted) {
        sb.append("Running with root access; extended diagnostics are enabled where supported.\n");
    } else {
        sb.append("Standard Android permission model.\n");
    }

    // ------------------------------------------------------------
    // Root-only sections
    // ------------------------------------------------------------
    if (isRooted) {
        sb.append("\nExtended diagnostics:\n");
        sb.append("  Status         : Enabled (root)\n");

        sb.append("\nRoot indicators:\n");
        String[] paths = {
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/su", "/system/bin/.ext/.su",
                "/system/usr/we-need-root/su-backup",
                "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
        };
        boolean any = false;
        for (String p : paths) {
            if (new File(p).exists()) {
                sb.append("  ").append(p).append("\n");
                any = true;
            }
        }
        if (!any) sb.append("  (no common su paths detected)\n");

        // Vendor diag paths (best-effort presence check; no promises)
        String[] vendorDiag = {
                "/dev/diag", "/dev/diag_qti",
                "/efs/imei/.msl", "/efs/imei/key_str", "/efs/FactoryApp",
                "/vendor/etc/diag_mdlog", "/system/vendor/bin/diag_mdlog",
                "/system/bin/mtk_agpsd", "/system/bin/mtk_engineering", "/system/bin/emdlogger"
        };

        boolean foundVendor = false;
        for (String p : vendorDiag) {
            try {
                if (new File(p).exists()) {
                    if (!foundVendor) {
                        sb.append("\nVendor diagnostics (presence check):\n");
                        foundVendor = true;
                    }
                    sb.append("  ").append(p).append("\n");
                }
            } catch (Throwable ignore) {}
        }

        if (!foundVendor) {
            sb.append("\nVendor diagnostics:\n");
            sb.append("  Not exposed to third-party apps; availability depends on OEM tooling.\n");
        }

    } else {
        // --------------------------------------------------------
        // Non-root: one concise, covering statement
        // --------------------------------------------------------
        sb.append("\nThis device is not rooted.\n");
        sb.append("Advanced subsystem tables and low-level hardware diagnostics are available only on rooted devices.\n");
    }

    return sb.toString();
}

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
        {
            "BatteryMain",
            "battery", "batt", "batt_therm", "battery_therm",
            "fuelgauge", "bms", "bms_therm"
        },
        {
            "BatteryShell",
            "skin", "skin-therm", "case", "case-therm",
            "batt_skin", "battery_skin", "rear_case", "shell",
            "backlight_therm", "backlight", "camera"
        },
        {
            "PMIC",
            "pmic", "pm8998", "pm8150", "pmx",
            "pmic-therm", "pmic_therm",
            "pm7250", "pm7250b", "pm6450",
            "bcl", "ibat"
        },
        {
            "Charger",
            "charger", "chg", "usb", "usb-therm",
            "usb_conn_therm", "bq", "charge-therm", "charge_pump"
        },
        {
            "ModemMain",
            "modem", "mdm", "mdmss", "xbl_modem",
            "modempa", "rf-therm", "rf",
            "modem-cfg", "sub1-modem-cfg",
            "pa_therm", "pa0_therm", "pa1_therm", "pa2_therm",
            "pa0", "pa1", "pa2"
        },
        {
            "ModemAux",
            "modem1", "mdm1", "mdm2",
            "xbl_modem1", "rf1",
            "mdmss-1", "mdmss-2",
            "sub1-modem-cfg", "modem_sub",
            "modem1_pa", "rf_sub"
        }
};

// Summary struct
private static class ThermalSummary {
    int zoneCount;          // μόνο REAL hardware zones
    int coolingDeviceCount; // μόνο REAL hardware cooling devices
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

    summary.zoneCount          = 0;
    summary.coolingDeviceCount = 0;

    // REAL hardware thermal zones
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

                // μετράμε μόνο ζώνες που τελικά ανήκουν σε hardware group
                summary.zoneCount++;

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

    // REAL hardware cooling devices (fan / blower / pump / heatsink)
    if (cools != null) {
        for (File c : cools) {
            try {
                String type = readFirstLineSafe(new File(c.getAbsolutePath(), "type"));
                if (isHardwareCoolingDevice(type)) {
                    summary.coolingDeviceCount++;
                }
            } catch (Throwable ignore) {}
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

    int shown = 0;

    if (cools != null) {
        for (File c : cools) {
            if (shown >= 5) break;

            try {
                String type = readFirstLineSafe(new File(c.getAbsolutePath(), "type"));
                if (!isHardwareCoolingDevice(type)) continue;

                sb.append("• ")
                  .append(c.getName())
                  .append(" → ")
                  .append(type)
                  .append("\n");

                shown++;

            } catch (Throwable ignore) {}
        }
    }

    // --- Αν δεν βρέθηκαν πραγματικά hardware cooling devices ---
    if (shown == 0) {
        sb.append("• (no hardware cooling devices found) (this device uses passive cooling only)\n");
    }
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
        float c = findTempByTypeKeywords(
                "batt_temp", "bat_therm", "battery-main",
                "battery_board", "batman"
        );
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) batteryMain.updateIfBetter("xiaomi:battery", c);
    }

    // Battery Shell
    if (!batteryShell.valid) {
        float c = findTempByTypeKeywords(
                "batt_shell", "battery_shell", "shell_therm",
                "case-therm", "skin", "backlight_therm", "backlight"
        );
        if (!isValidTemp(c)) c = findTempByTypeKeywords(
                "rear_case", "back_cover", "batt_surface", "camera"
        );
        if (isValidTemp(c)) batteryShell.updateIfBetter("fallback:battery_shell", c);
    }

    if (isXiaomi && !batteryShell.valid) {
        float c = findTempByTypeKeywords(
                "batt_skin", "batt_surface",
                "back_cover", "rear_case",
                "backlight_therm", "camera"
        );
        if (isValidTemp(c)) batteryShell.updateIfBetter("xiaomi:battery_shell", c);
    }

    // PMIC
    if (!pmic.valid) {
        float c = findTempByTypeKeywords(
                "pmic", "pmic_therm", "pmic-tz",
                "pm8998", "pm660", "pm7250", "pm7250b", "pm6450"
        );
        if (!isValidTemp(c)) c = findTempByTypeKeywords("bcl", "ibat");
        if (isValidTemp(c)) pmic.updateIfBetter("fallback:pmic", c);
    }

    if (isXiaomi && !pmic.valid) {
        float c = findTempByTypeKeywords(
                "pm6150l_tz", "pm8350", "pm7250b_tz",
                "pm7250b-ibat", "pm7250b-bcl"
        );
        if (isValidTemp(c)) pmic.updateIfBetter("xiaomi:pmic", c);
    }

    // Charger
    if (!charger.valid) {
        float c = findTempByTypeKeywords("charger", "chg", "usb-therm", "charge-temp");
        if (!isValidTemp(c)) c = findTempByTypeKeywords("charge_pump", "cp_therm", "usb_conn_therm");
        if (!isValidTemp(c)) c = readBatteryTempFallback();
        if (isValidTemp(c)) charger.updateIfBetter("fallback:charger", c);
    }

    // Modem main
    if (!modemMain.valid) {
        float c = findTempByTypeKeywords(
                "modem", "mdm", "mdmss", "mdmss-3", "mdmss-2",
                "rf-therm", "modempa", "pa_therm", "pa0_therm", "pa1_therm", "pa2_therm",
                "modem-cfg"
        );
        if (isValidTemp(c)) modemMain.updateIfBetter("fallback:modem_main", c);
    }

    if (isXiaomi && !modemMain.valid) {
        float c = findTempByTypeKeywords(
                "xo_therm_modem", "modem_pa", "modem_pa_0",
                "mdmss-3", "mdmss-2", "mdmss-1", "pa0", "pa1", "pa2"
        );
        if (isValidTemp(c)) modemMain.updateIfBetter("xiaomi:modem_main", c);
    }

    // Modem aux
    if (!modemAux.valid) {
        float c = findTempByTypeKeywords(
                "modem1", "mdm2", "xbl_modem1", "rf1",
                "mdmss-1", "mdmss-2", "sub1-modem-cfg"
        );
        if (isValidTemp(c)) modemAux.updateIfBetter("fallback:modem_aux", c);
    }

    if (isXiaomi && !modemAux.valid) {
        float c = findTempByTypeKeywords(
                "modem_sub", "modem1_pa", "rf_sub",
                "mdmss-1", "mdmss-2", "sub1-modem-cfg"
        );
        if (isValidTemp(c)) modemAux.updateIfBetter("xiaomi:modem_aux", c);
    }
}

// ===================================================================
// FINAL BUILDER — CLEAN OUTPUT (REAL HARDWARE SUMMARY + TABLE)
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

    ThermalSummary summary = scanThermalHardware(
            batteryMain, batteryShell, pmic, charger, modemMain, modemAux
    );

    applyThermalFallbacks(batteryMain, batteryShell, pmic, charger, modemMain, modemAux);

    // Top summary — μόνο αν υπάρχουν πραγματικά hardware στοιχεία
if (summary != null && (summary.zoneCount > 0 || summary.coolingDeviceCount > 0)) {

    sb.append(String.format(Locale.US, "%-17s: %d\n",
            "Thermal Zones", summary.zoneCount));

    if (summary.coolingDeviceCount == 0) {
        sb.append(String.format(Locale.US,
                "%-17s: 0 (This device uses passive cooling only)\n",
                "Cooling Devices"));
    } else {
        sb.append(String.format(Locale.US,
                "%-17s: %d\n",
                "Cooling Devices", summary.coolingDeviceCount));
    }

    sb.append("\n");
}

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
// 3. TELEPHONY / MODEM — ULTRA STABLE GEL EDITION + Xiaomi SimpleSimEntry Fallback
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

    sb.append(String.format(locale, "%s : %s\n",
            padKeyModem("Phone Type"), phoneTypeStr));

    // ------------------------------------------------------------
    // DATA NETWORK
    // ------------------------------------------------------------
    try {
        int net = (tm != null) ? tm.getDataNetworkType()
                               : TelephonyManager.NETWORK_TYPE_UNKNOWN;

        String netName =
                (net == TelephonyManager.NETWORK_TYPE_NR)  ? "5G NR"  :
                (net == TelephonyManager.NETWORK_TYPE_LTE) ? "4G LTE" :
                "Unknown";

        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Data Network"), netName));

        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("5G (NR) Active"),
                (net == TelephonyManager.NETWORK_TYPE_NR) ? "Yes" : "No"));
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // CARRIER / COUNTRY / OPERATOR CODE
    // ------------------------------------------------------------
    try {
        String carrier = (tm != null) ? tm.getNetworkOperatorName() : null;
        String iso     = (tm != null) ? tm.getNetworkCountryIso()   : null;
        String opCode  = (tm != null) ? tm.getNetworkOperator()     : null;

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
    // SIGNAL STRENGTH
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

    // ========================================================================
    // ACTIVE SIMS + FALLBACK
    // ========================================================================
    try {
        List<SubscriptionInfo> subs = null;

        if (sm != null) {
            try { subs = sm.getActiveSubscriptionInfoList(); } catch (Throwable ignore) {}
        }

        if ((subs == null || subs.isEmpty()) && sm != null) {
            try {
                Method m = sm.getClass().getMethod("getAvailableSubscriptionInfoList");
                Object result = m.invoke(sm);
                if (result instanceof List) subs = (List<SubscriptionInfo>) result;
            } catch (Throwable ignore) {}
        }

        if (subs == null || subs.isEmpty()) {
            try {
                SubscriptionManager alt = SubscriptionManager.from(this);
                if (alt != null) subs = alt.getActiveSubscriptionInfoList();
            } catch (Throwable ignore) {}
        }

        List<SimpleSimEntry> simpleList = new ArrayList<>();

        if (subs == null || subs.isEmpty()) {
            try {
                for (int slot = 0; slot < 2; slot++) {
                    int simState = tm.getSimState(slot);

                    if (simState == TelephonyManager.SIM_STATE_READY ||
                        simState == TelephonyManager.SIM_STATE_NETWORK_LOCKED ||
                        simState == TelephonyManager.SIM_STATE_PIN_REQUIRED ||
                        simState == TelephonyManager.SIM_STATE_PUK_REQUIRED) {

                        String name = null;
                        try { name = tm.getSimOperatorName(); } catch (Throwable ignore) {}
                        if (name == null || name.trim().isEmpty()) name = "Unknown";

                        simpleList.add(new SimpleSimEntry(slot, name));
                    }
                }
            } catch (Throwable ignore) {}
        }

        int count = 0;

        if (subs != null && !subs.isEmpty()) {
            boolean[] seen = new boolean[2];
            for (SubscriptionInfo si : subs) {
                try {
                    int slot = si.getSimSlotIndex();
                    if (slot >= 0 && slot <= 1 && !seen[slot]) {
                        seen[slot] = true;
                        count++;
                    }
                } catch (Throwable ignore) {}
            }
        } else if (!simpleList.isEmpty()) {
            count = simpleList.size();
        }

        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Active SIMs"),
                (count == 0 ? "N/A" : String.valueOf(count))));

        if (subs != null && !subs.isEmpty()) {
            boolean[] printed = new boolean[2];
            for (SubscriptionInfo si : subs) {
                try {
                    int slot = si.getSimSlotIndex();
                    if (slot < 0 || slot > 1 || printed[slot]) continue;
                    printed[slot] = true;

                    String name = (si.getCarrierName() != null)
                            ? si.getCarrierName().toString()
                            : "Unknown";

                    sb.append(String.format(locale, "%s : %s\n",
                            padKeyModem("SIM Slot " + (slot + 1)), name));
                } catch (Throwable ignore) {}
            }
        } else {
            for (SimpleSimEntry e : simpleList) {
                sb.append(String.format(locale, "%s : %s\n",
                        padKeyModem("SIM Slot " + (e.slot + 1)), e.carrier));
            }
        }

    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // ADVANCED MODEM TABLES (ROOT-AWARE, SINGLE LINE)
    // ------------------------------------------------------------
    if (isRooted) {
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Advanced"),
                "Advanced modem tables are available on this device."));
    } else {
        sb.append(String.format(locale, "%s : %s\n",
                padKeyModem("Advanced"),
                "Advanced modem tables require root access."));
    }

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
                sb.append("Wi-Fi RTT        : ").append(rtt ? "Yes" : "No").append(" (Indoor distance)\n");
            }

            // ------------------------------------------------------------
            // Wi-Fi Aware / NAN
            // ------------------------------------------------------------
            if (Build.VERSION.SDK_INT >= 26) {
                boolean aware = pm.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
                sb.append("Wi-Fi Aware      : ").append(aware ? "Yes" : "No").append(" (Device proximity)\n");
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
// 5. System Feature Matrix
// ============================

private String buildSystemFeaturesInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        PackageManager pm = getPackageManager();
        FeatureInfo[] feats = pm.getSystemAvailableFeatures();

        if (feats == null || feats.length == 0) {
            sb.append("No system-declared features exposed by PackageManager.\n");
            return sb.toString();
        }

        sb.append("Feature Count    : ").append(feats.length).append("\n\n");

        for (FeatureInfo fi : feats) {
            if (fi == null || fi.name == null) continue;
            sb.append("• ").append(fi.name).append("\n");
        }

        sb.append("\nNote:\n");
        sb.append("This list represents system-declared capabilities ");
        sb.append("(PackageManager features).\n");
        sb.append("It does NOT guarantee actual hardware presence, ");
        sb.append("availability or performance.\n");

    } catch (Throwable t) {
        sb.setLength(0);
        sb.append("System feature matrix is not accessible on this device.\n");
    }

    return sb.toString();
}
 
// ============================
// 6. SELinux / Security Flags
// ============================

private String buildSecurityFlagsInfo() {
    StringBuilder sb = new StringBuilder();

    String kernel = readSysString("/proc/version");
    if (kernel != null && !kernel.isEmpty()) {
        sb.append("Kernel           : ").append(kernel).append("\n");
    }

    String patch = Build.VERSION.SECURITY_PATCH;
    if (patch != null && !patch.isEmpty()) {
        sb.append("Security Patch   : ").append(patch).append("\n");
    }

    String vbState = getProp("ro.boot.verifiedbootstate");
    if (vbState != null && !vbState.isEmpty()) {
        sb.append("Verified Boot    : ").append(vbState).append("\n");
    }

    boolean strongBox =
            getPackageManager().hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE);
    sb.append("StrongBox        : ").append(strongBox ? "Yes" : "No").append("\n");

    boolean hce =
            getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
    sb.append("HCE / Secure NFC : ").append(hce ? "Yes" : "No").append("\n");

    sb.append(
        "Advanced         : SELinux policy details and keymaster internals are "
      + "hardware-isolated and not accessible to apps; inspection requires "
      + "root or kernel-level access.\n"
    );

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
// SET TEXT FOR ALL SECTIONS — COMPLETE & FIXED (FINAL)
// ============================================================
private void populateAllSections() {

    // =====================
    // BATTERY (CORE HARDWARE)
    // =====================
    String bat = buildBatteryInfo();
    set(R.id.txtBatteryContent, bat);
    applyNeonValues(findViewById(R.id.txtBatteryContent), bat);

    // =====================
    // SCREEN
    // =====================
    String scr = buildScreenInfo();
    set(R.id.txtScreenContent, scr);
    applyNeonValues(findViewById(R.id.txtScreenContent), scr);

    // =====================
    // CAMERA
    // =====================
    String cam = buildCameraInfo();
    set(R.id.txtCameraContent, cam);
    applyNeonValues(findViewById(R.id.txtCameraContent), cam);

    // =====================
    // CONNECTIVITY
    // =====================
    String con = buildConnectivityInfo();
    set(R.id.txtConnectivityContent, con);
    applyNeonValues(findViewById(R.id.txtConnectivityContent), con);

    // =====================
    // LOCATION
    // =====================
    String loc = getLocationCapabilities();
set(R.id.txtLocationContent, loc);
applyNeonValues(findViewById(R.id.txtLocationContent), loc);

    // =====================
    // THERMAL
    // =====================
    String th = buildThermalInfo();
    set(R.id.txtThermalContent, th);
    applyNeonValues(findViewById(R.id.txtThermalContent), th);

    // =====================
    // MODEM
    // =====================
    String mod = buildModemInfo();
    set(R.id.txtModemContent, mod);
    applyNeonValues(findViewById(R.id.txtModemContent), mod);

    // =====================
    // WIFI ADVANCED
    // =====================
    String wifi = buildWifiAdvancedInfo();
    set(R.id.txtWifiAdvancedContent, wifi);
    applyNeonValues(findViewById(R.id.txtWifiAdvancedContent), wifi);

    // =====================
    // AUDIO (UNIFIED)
    // =====================
    String aud = buildAudioUnifiedInfo();
    set(R.id.txtAudioUnifiedContent, aud);
    applyNeonValues(findViewById(R.id.txtAudioUnifiedContent), aud);

    // =====================
    // SENSORS
    // =====================
    String sen = buildSensorsInfo();
    set(R.id.txtSensorsContent, sen);
    applyNeonValues(findViewById(R.id.txtSensorsContent), sen);

    // =====================
    // BIOMETRICS
    // =====================
    String bio = buildBiometricsInfo();
    set(R.id.txtBiometricsContent, bio);
    applyNeonValues(findViewById(R.id.txtBiometricsContent), bio);

    // =====================
    // NFC
    // =====================
    String nfc = getNfcBasicInfo();
set(R.id.txtNfcContent, nfc);
applyNeonValues(findViewById(R.id.txtNfcContent), nfc);

    // =====================
    // GNSS
    // =====================
    String gnss = buildGnssInfo();
    set(R.id.txtGnssContent, gnss);
    applyNeonValues(findViewById(R.id.txtGnssContent), gnss);

    // =====================
    // UWB
    // =====================
    String uwb = buildUwbInfo();
    set(R.id.txtUwbContent, uwb);
    applyNeonValues(findViewById(R.id.txtUwbContent), uwb);

    // =====================
    // USB
    // =====================
    String usb = buildUsbInfo();
    set(R.id.txtUsbContent, usb);
    applyNeonValues(findViewById(R.id.txtUsbContent), usb);

    // =====================
    // HAPTICS
    // =====================
    String hap = buildHapticsInfo();
    set(R.id.txtHapticsContent, hap);
    applyNeonValues(findViewById(R.id.txtHapticsContent), hap);

    // =====================
    // SYSTEM FEATURES
    // =====================
    String sys = buildSystemFeaturesInfo();
    set(R.id.txtSystemFeaturesContent, sys);
    applyNeonValues(findViewById(R.id.txtSystemFeaturesContent), sys);

    // =====================
    // SECURITY FLAGS
    // =====================
    String sec = buildSecurityFlagsInfo();
    set(R.id.txtSecurityFlagsContent, sec);
    applyNeonValues(findViewById(R.id.txtSecurityFlagsContent), sec);

    // =====================
    // ROOT
    // =====================
    String root = buildRootInfo();
    set(R.id.txtRootContent, root);
    applyNeonValues(findViewById(R.id.txtRootContent), root);

    // =====================
    // OTHER PERIPHERALS
    // =====================
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

        sb.append("Network Locatio    : ")
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
            applyNeonValues(modemView, info);
        }
    } catch (Throwable ignore) {}
}

// ============================================================================
// LOCAL CLASS — Xiaomi SimpleSimEntry fallback
// ============================================================================
private static class SimpleSimEntry {
    int slot;
    String carrier;

    SimpleSimEntry(int s, String c) {
        slot = s;
        carrier = c;
    }
}

// ============================================================
// GEL UI HELPERS — REQUIRED
// ============================================================

private void setNeonSectionText(TextView tv, String text) {
    if (tv == null) return;
    tv.setText(text);
}

private void appendAccessInstructions(StringBuilder sb, String type) {
    if (sb == null) return;

    sb.append("\n");
    sb.append("Access Info       : ");

    switch (type) {
        case "camera":
            sb.append("Camera permission required\n");
            sb.append("Settings → Apps → Permissions → Camera\n");
            break;
        case "sensors":
            sb.append("Sensor access is system managed\n");
            break;
        default:
            sb.append("Additional permissions may be required\n");
    }
}

private void handleSettingsClick(Context ctx, String path) {
    try {
        Intent i = new Intent(Settings.ACTION_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);
    } catch (Throwable ignore) {}
}

private void animateCollapse(TextView v) {
    if (v == null) return;
    v.setVisibility(View.GONE);
}

// 🔥 END OF CLASS
}
