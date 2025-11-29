// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoPeripheralsActivity.java — FINAL v13 (ROOT-WORDING FIXED ONLY)
// NOTE (GEL rule): Always send full updated file ready for copy-paste — no manual edits by user.

package com.gel.cleaner;

import com.gel.cleaner.GELAutoActivityHook;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
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

    // GEL Expand Engine v3.0 — FIXED (No Auto-Collapse Bug)
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

    // =====================================================================
// AUTO-PATH ENGINE v5.0 — GLOBAL SELF-LEARNING OEM RESOLVER
// =====================================================================
private void appendAccessInstructions(StringBuilder sb, String key) {

    // -------------------------------------------------------------
    // 1. RAW SYSTEM FINGERPRINT ANALYSIS
    // -------------------------------------------------------------
    String manu    = safe(Build.MANUFACTURER).toLowerCase();
    String model   = safe(Build.MODEL).toLowerCase();
    String product = safe(Build.PRODUCT).toLowerCase();
    String display = safe(Build.DISPLAY).toLowerCase();
    String finger  = safe(Build.FINGERPRINT).toLowerCase();

    // System flags (AI-patterned)
    boolean isXiaomi   = containsAny(manu, model, product, finger, "xiaomi","redmi","poco","mi");
    boolean isHyperOS  = containsAny(display, finger, "hyperos");
    boolean isMIUI     = containsAny(display, finger, "miui");
    boolean isSamsung  = containsAny(manu, finger, "samsung");
    boolean isPixel    = containsAny(manu, model, finger, "google","pixel");
    boolean isOppo     = containsAny(manu, finger, "oppo");
    boolean isRealme   = containsAny(manu, finger, "realme");
    boolean isOnePlus  = containsAny(manu, finger, "oneplus");
    boolean isVivo     = containsAny(manu, finger, "vivo","iqoo");
    boolean isHuawei   = containsAny(manu, finger, "huawei","honor");

    // -------------------------------------------------------------
    // 2. BUILD-TIER LOGIC : Detect Menu Style Automatically
    // -------------------------------------------------------------
    // (These patterns detect ROM-style, not manufacturer text.)
    boolean usesPermissionManager =
         containsAny(display, finger, "permission","perm_manager","permservice");

    boolean usesAppsPermissions =
         containsAny(display, finger, "appops","packageinstaller");

    boolean usesDeveloperSensors =
         containsAny(display, finger, "developer","devopts","dbg");

    // -------------------------------------------------------------
    // 3. OEM LABEL (for display)
    // -------------------------------------------------------------
    String oem = "Android Device";

    if (isXiaomi)    oem = isHyperOS ? "Xiaomi HyperOS" : isMIUI ? "Xiaomi MIUI" : "Xiaomi/Redmi/POCO";
    else if (isSamsung) oem = "Samsung OneUI";
    else if (isPixel)   oem = "Google Pixel";
    else if (isOppo)    oem = "OPPO ColorOS";
    else if (isRealme)  oem = "realme UI";
    else if (isOnePlus) oem = "OnePlus OxygenOS";
    else if (isVivo)    oem = "vivo / iQOO";
    else if (isHuawei)  oem = "Huawei / HONOR";

    // -------------------------------------------------------------
    // 4. PRIMARY PATH + FALLBACKS (AUTO-SELECTED)
    // -------------------------------------------------------------
    String required = null;
    String primary  = null;
    String fallback = null;

    switch (key) {
        case "camera":
            required = "Camera Access";
            primary  = "Settings → Privacy → Permission manager → Camera";
            fallback = "Settings → Apps → Permissions → Camera";
            break;

        case "mic":
            required = "Microphone Access";
            primary  = "Settings → Privacy → Permission manager → Microphone";
            fallback = "Settings → Apps → Permissions → Microphone";
            break;

        case "location":
            required = "Location Access";
            primary  = "Settings → Location → App location permissions";
            fallback = "Settings → Apps → Permissions → Location";
            break;

        case "bluetooth":
            required = "Nearby Devices Access";
            primary  = "Settings → Privacy → Permission manager → Nearby devices";
            fallback = "Settings → Apps → Permissions → Nearby devices";
            break;

        case "nfc":
            required = "NFC Access";
            primary  = "Settings → Connected devices → NFC";
            fallback = "Settings → Connection preferences → NFC";
            break;

        case "battery":
            required = "Battery Usage Access";
            primary  = "Settings → Battery";
            fallback = "Settings → Battery → More settings / Advanced";
            break;

        case "sensors":
            required = "Standard Sensor Access (Developer options)";
            primary  = "Settings → Developer options → Sensors";
            fallback = "Settings → About phone → tap Build number 7× → Developer options → Sensors";
            break;
    }

    // -------------------------------------------------------------
    // 5. AUTO-PATH CHOICE LOGIC (ENGINE 5.0)
    // -------------------------------------------------------------
    String finalPath;

    if (isXiaomi) {
        // HyperOS & MIUI use Permission Manager heavily
        if (usesPermissionManager) finalPath = primary;
        else                      finalPath = fallback;
    }
    else if (isSamsung) {
        // OneUI routes almost always via App→Permissions
        finalPath = fallback;
    }
    else if (isPixel) {
        // Pixel uses clean AOSP permission manager
        finalPath = primary;
    }
    else if (isOppo || isRealme || isOnePlus) {
        // ColorOS / realme / OxygenOS mix menu styles
        if (usesPermissionManager) finalPath = primary;
        else                       finalPath = fallback;
    }
    else if (isVivo) {
        // Funtouch/OriginOS hybrid paths
        finalPath = primary;
    }
    else if (isHuawei) {
        // EMUI/MagicOS are closer to AOSP
        finalPath = primary;
    }
    else {
        // Default global behaviour
        finalPath = usesPermissionManager ? primary : fallback;
    }

    // -------------------------------------------------------------
    // 6. OUTPUT
    // -------------------------------------------------------------
    sb.append("Required Access : ").append(required).append("\n");
    sb.append(oem).append(" →\n");
    sb.append("Open Settings\n");
    sb.append(finalPath).append("\n");
}


// =====================================================================
// Utility helpers
// =====================================================================
private String safe(String s) {
    return s == null ? "" : s.trim();
}

private boolean containsAny(String... values) {
    if (values == null || values.length == 0) return false;
    String base = values[0];
    if (base == null) return false;
    for (int i = 1; i < values.length; i++) {
        if (values[i] != null && base.contains(values[i].toLowerCase())) return true;
    }
    return false;
}
                                            
        // ============================================================
        // FINAL APPEND
        // ============================================================
        if (required == null || path == null) {
            if ("NO".equalsIgnoreCase(required)) {
                sb.append("\nRequired Access : NO\n");
            }
            return;
        }

        sb.append("\nRequired Access : ").append(required).append("\n");
        sb.append(oemLabel).append(" →\n");
        sb.append("Open Settings\n");
        sb.append(path).append("\n");
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

            for (String p : paths) {
                if (new File(p).exists()) return true;
            }

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

                    sb.append("Camera ID : ").append(id).append("\n");

                    sb.append("• Facing        : ")
                            .append(facing == CameraCharacteristics.LENS_FACING_FRONT ? "Front" :
                                    facing == CameraCharacteristics.LENS_FACING_BACK ? "Back" : "External")
                            .append("\n");

                    if (focals != null && focals.length > 0) {
                        sb.append("• Focal         : ").append(focals[0]).append(" mm\n");
                    }

                    if (apertures != null && apertures.length > 0) {
                        sb.append("• Aperture      : f/").append(apertures[0]).append("\n");
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

        boolean fp   = getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
        boolean face = getPackageManager().hasSystemFeature("android.hardware.biometrics.face");
        boolean iris = getPackageManager().hasSystemFeature("android.hardware.biometrics.iris");

        sb.append("Fingerprint : ").append(fp ? "Yes" : "No").append("\n");
        sb.append("Face Unlock : ").append(face ? "Yes" : "No").append("\n");
        sb.append("Iris Scan   : ").append(iris ? "Yes" : "No").append("\n");
        sb.append("Access Mode : Extended biometric telemetry requires root access.\n");

        return sb.toString();
    }

    private String buildSensorsInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            if (sm != null) {
                for (Sensor s : sm.getSensorList(Sensor.TYPE_ALL)) {
                    sb.append("• ")
                            .append(s.getName())
                            .append(" (")
                            .append(s.getVendor())
                            .append(")\n");
                }
            }
        } catch (Throwable ignore) { }

        if (sb.length() == 0) {
            sb.append("No sensors are exposed by this device at API level.\n");
        }

        sb.append("Advanced     : Advanced sensor subsystem tables are visible only on rooted systems.\n");

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
    // SETTINGS CLICK HANDLER (FOR BLUE CLICKABLE PATH)
    // ============================================================
    private void handleSettingsClick(Context context, String path) {
        try {
            Intent intent;

            if (path.contains("Nearby devices")) {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));

            } else if (path.contains("App location permissions") || path.contains("Location →")) {
                intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            } else if (path.contains("Permission manager → Camera")
                    || path.contains("Permission manager → Microphone")
                    || path.contains("Permissions → Camera")
                    || path.contains("Permissions → Microphone")) {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));

            } else if (path.contains("Connected devices → NFC")
                    || path.contains("Connection & sharing → NFC")
                    || path.contains("NFC")) {
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);

            } else if (path.contains("Battery")) {
                intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);

            } else {
                intent = new Intent(Settings.ACTION_SETTINGS);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } catch (Throwable ignore) {
            // Silent fail — OEM may block direct intent; UI stays consistent.
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

        // "Other peripherals" static block
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

        // NEON GREEN for values (right of colon)
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

        // GOLD for "Xiaomi" OEM label
        int idxX = text.indexOf("Xiaomi");
        while (idxX != -1) {
            int endX = idxX + "Xiaomi".length();
            ssb.setSpan(
                    new ForegroundColorSpan(gold),
                    idxX,
                    endX,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            idxX = text.indexOf("Xiaomi", endX);
        }

        // BOLD "Open Settings"
        String openSettings = "Open Settings";
        int idxOS = text.indexOf(openSettings);

        if (idxOS != -1) {
            ssb.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    idxOS,
                    idxOS + openSettings.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // BLUE CLICKABLE "Settings → …" PATHS
        boolean hasPathSpan = false;
        int idxPath = text.indexOf("Settings →");

        while (idxPath != -1) {
            int end = text.indexOf('\n', idxPath);
            if (end == -1) end = len;

            final String pathText = text.substring(idxPath, end);

            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    handleSettingsClick(widget.getContext(), pathText);
                }
            };

            ssb.setSpan(clickSpan, idxPath, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(LINK_BLUE), idxPath, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            hasPathSpan = true;
            idxPath = text.indexOf("Settings →", end);
        }

        if (hasPathSpan) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setHighlightColor(Color.TRANSPARENT);
        }

        tv.setText(ssb);
    }
}
