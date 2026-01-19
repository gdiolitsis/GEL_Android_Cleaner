// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoPeripheralsActivity — FINAL UNIFIED
// STRICT MODE: XML IS SOURCE OF TRUTH
// ============================================================

package com.gel.cleaner.iphone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;

public class AppleDeviceInfoPeripheralsActivity extends Activity {

    // ============================================================
    // HEADERS (AS IN XML)
    // ============================================================

    private TextView[] allContents;
    private LinearLayout headerBattery;
    private LinearLayout headerScreen;
    private LinearLayout headerCamera;
    private LinearLayout headerConnectivity;
    private LinearLayout headerLocation;
    private LinearLayout headerThermal;
    private LinearLayout headerModem;
    private LinearLayout headerWifiAdvanced;
    private LinearLayout headerAudioUnified;
    private LinearLayout headerSensors;
    private LinearLayout headerBiometrics;
    private LinearLayout headerNfc;
    private LinearLayout headerGnss;
    private LinearLayout headerUwb;
    private LinearLayout headerUsb;
    private LinearLayout headerHaptics;
    private LinearLayout headerSystemFeatures;
    private LinearLayout headerSecurityFlags;
    private LinearLayout headerRoot;
    private LinearLayout headerOtherPeripherals;

    // ============================================================
    // CONTENT
    // ============================================================

    private TextView txtBattery;
    private TextView txtScreen;
    private TextView txtCamera;
    private TextView txtConnectivity;
    private TextView txtLocation;
    private TextView txtThermal;
    private TextView txtModem;
    private TextView txtWifiAdvanced;
    private TextView txtAudio;
    private TextView txtSensors;
    private TextView txtBiometrics;
    private TextView txtNfc;
    private TextView txtGnss;
    private TextView txtUwb;
    private TextView txtUsb;
    private TextView txtHaptics;
    private TextView txtSystemFeatures;
    private TextView txtSecurityFlags;
    private TextView txtRoot;
    private TextView txtOther;

    private AppleDeviceSpec d;

// ============================================================
// LIFECYCLE
// ============================================================
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_info_peripherals);

    bind();

    allContents = new TextView[] {
            txtBattery,
            txtScreen,
            txtCamera,
            txtConnectivity,
            txtLocation,
            txtThermal,
            txtModem,
            txtWifiAdvanced,
            txtAudio,
            txtSensors,
            txtBiometrics,
            txtNfc,
            txtGnss,
            txtUwb,
            txtUsb,
            txtHaptics,
            txtSystemFeatures,
            txtSecurityFlags,
            txtRoot,
            txtOther
    };

    setupPeripheralsToggles();

    SharedPreferences prefs =
            getSharedPreferences("gel_prefs", MODE_PRIVATE);

    String model = prefs.getString("apple_model", null);
    if (model == null) {
        finish();
        return;
    }

    d = AppleSpecs.get(model);
    populate();
}

    // ============================================================
    // BIND
    // ============================================================
    private void bind() {

        headerBattery         = findViewById(R.id.headerBattery);
        headerScreen          = findViewById(R.id.headerScreen);
        headerCamera          = findViewById(R.id.headerCamera);
        headerConnectivity    = findViewById(R.id.headerConnectivity);
        headerLocation        = findViewById(R.id.headerLocation);
        headerThermal         = findViewById(R.id.headerThermal);
        headerModem           = findViewById(R.id.headerModem);
        headerWifiAdvanced    = findViewById(R.id.headerWifiAdvanced);
        headerAudioUnified    = findViewById(R.id.headerAudioUnified);
        headerSensors         = findViewById(R.id.headerSensors);
        headerBiometrics      = findViewById(R.id.headerBiometrics);
        headerNfc             = findViewById(R.id.headerNfc);
        headerGnss            = findViewById(R.id.headerGnss);
        headerUwb             = findViewById(R.id.headerUwb);
        headerUsb             = findViewById(R.id.headerUsb);
        headerHaptics         = findViewById(R.id.headerHaptics);
        headerSystemFeatures  = findViewById(R.id.headerSystemFeatures);
        headerSecurityFlags   = findViewById(R.id.headerSecurityFlags);
        headerRoot            = findViewById(R.id.headerRoot);
        headerOtherPeripherals= findViewById(R.id.headerOtherPeripherals);

        txtBattery        = findViewById(R.id.txtBatteryContent);
        txtScreen         = findViewById(R.id.txtScreenContent);
        txtCamera         = findViewById(R.id.txtCameraContent);
        txtConnectivity   = findViewById(R.id.txtConnectivityContent);
        txtLocation       = findViewById(R.id.txtLocationContent);
        txtThermal        = findViewById(R.id.txtThermalContent);
        txtModem          = findViewById(R.id.txtModemContent);
        txtWifiAdvanced   = findViewById(R.id.txtWifiAdvancedContent);
        txtAudio          = findViewById(R.id.txtAudioUnifiedContent);
        txtSensors        = findViewById(R.id.txtSensorsContent);
        txtBiometrics     = findViewById(R.id.txtBiometricsContent);
        txtNfc            = findViewById(R.id.txtNfcContent);
        txtGnss           = findViewById(R.id.txtGnssContent);
        txtUwb            = findViewById(R.id.txtUwbContent);
        txtUsb            = findViewById(R.id.txtUsbContent);
        txtHaptics        = findViewById(R.id.txtHapticsContent);
        txtSystemFeatures = findViewById(R.id.txtSystemFeaturesContent);
        txtSecurityFlags  = findViewById(R.id.txtSecurityFlagsContent);
        txtRoot           = findViewById(R.id.txtRootContent);
        txtOther          = findViewById(R.id.txtOtherPeripheralsContent);
    }

private void setupPeripheralsToggles() {

    setupToggle(headerBattery,         txtBattery,        allContents);
    setupToggle(headerScreen,          txtScreen,         allContents);
    setupToggle(headerCamera,          txtCamera,         allContents);
    setupToggle(headerConnectivity,    txtConnectivity,   allContents);
    setupToggle(headerLocation,        txtLocation,       allContents);
    setupToggle(headerThermal,         txtThermal,        allContents);
    setupToggle(headerModem,           txtModem,          allContents);
    setupToggle(headerWifiAdvanced,    txtWifiAdvanced,   allContents);
    setupToggle(headerAudioUnified,    txtAudio,          allContents);
    setupToggle(headerSensors,         txtSensors,        allContents);
    setupToggle(headerBiometrics,      txtBiometrics,     allContents);
    setupToggle(headerNfc,             txtNfc,            allContents);
    setupToggle(headerGnss,            txtGnss,           allContents);
    setupToggle(headerUwb,             txtUwb,            allContents);
    setupToggle(headerUsb,             txtUsb,            allContents);
    setupToggle(headerHaptics,         txtHaptics,        allContents);
    setupToggle(headerSystemFeatures,  txtSystemFeatures, allContents);
    setupToggle(headerSecurityFlags,   txtSecurityFlags,  allContents);
    setupToggle(headerRoot,            txtRoot,           allContents);
    setupToggle(headerOtherPeripherals,txtOther,          allContents);
}

// ============================================================
// TOGGLE ENGINE — SHARED
// ============================================================
private void setupToggle(LinearLayout header, TextView content, TextView[] all) {
    if (header == null || content == null) return;

    content.setVisibility(View.GONE);

    header.setOnClickListener(v -> {

        boolean willOpen = content.getVisibility() != View.VISIBLE;

        // 🔒 κλείσε ΟΛΑ
        for (TextView tv : all) {
            if (tv != null) tv.setVisibility(View.GONE);
        }

        // ✅ άνοιξε μόνο αυτό αν πρέπει
        if (willOpen) {
            content.setVisibility(View.VISIBLE);
        }
    });
}

// ============================================================
// POPULATE — FINAL (SERIES + PRO / PRO MAX DIFFERENTIATION)
// ============================================================
private void populate() {

    if (d == null) {
        hideAll();
        return;
    }

    // ------------------------------------------------------------
    // SCREEN / DISPLAY
    // ------------------------------------------------------------
    section(headerScreen, txtScreen,
            log("Display Type", d.display) +
            log("Resolution", d.resolution) +
            log("Refresh Rate",
                    (isPro() || isProMax())
                            ? "Up to 120 Hz (ProMotion)"
                            : (d.refreshRate != null ? d.refreshRate : "60 Hz"))
    );

    // ------------------------------------------------------------
    // CAMERA SYSTEM
    // ------------------------------------------------------------
    section(headerCamera, txtCamera,
            log("Main Camera", d.cameraMain) +
            log("Ultra-Wide Camera", d.cameraUltraWide) +
            log("Telephoto Camera",
                    (isPro() || isProMax())
                            ? (d.cameraTele != null ? d.cameraTele : "Present")
                            : "Not available") +
            log("Front Camera", d.cameraFront) +
            log("Video Recording", d.cameraVideo)
    );

    // ------------------------------------------------------------
    // CONNECTIVITY
    // ------------------------------------------------------------
    section(headerConnectivity, txtConnectivity,
            log("Wi-Fi", d.wifi) +
            log("Bluetooth", d.bluetooth) +
            log("AirDrop", yes(d.hasAirDrop)) +
            log("AirPlay", yes(d.hasAirPlay))
    );

    // ------------------------------------------------------------
    // LOCATION
    // ------------------------------------------------------------
    section(headerLocation, txtLocation,
            log("GNSS", d.gps)
    );

    // ------------------------------------------------------------
    // THERMAL / SUSTAINED PERFORMANCE
    // ------------------------------------------------------------
    section(headerThermal, txtThermal,
            log("Thermal Design",
                    isProMax()
                            ? "Enhanced heat dissipation (larger chassis)"
                            : isPro()
                                ? "Improved sustained performance"
                                : (d.thermalNote != null
                                        ? d.thermalNote
                                        : "Standard thermal profile"))
    );

    // ------------------------------------------------------------
    // MODEM / TELEPHONY
    // ------------------------------------------------------------
    section(headerModem, txtModem,
            log("Modem", d.modem) +
            log("Cellular", d.cellular) +
            log("5G", yes(d.has5G)) +
            log("LTE", yes(d.hasLTE)) +
            log("SIM", d.simSlots)
    );

    // ------------------------------------------------------------
    // Wi-Fi ADVANCED
    // ------------------------------------------------------------
    section(headerWifiAdvanced, txtWifiAdvanced,
            log("Wi-Fi Standard", d.wifi)
    );

    // ------------------------------------------------------------
    // AUDIO (UNIFIED)
    // ------------------------------------------------------------
    section(headerAudioUnified, txtAudio,
            log("Speakers",
                    (isPro() || isProMax())
                            ? (d.speakers != null
                                ? d.speakers + " (higher output tuning)"
                                : "Enhanced stereo speakers")
                            : d.speakers) +
            log("Microphones", d.microphones) +
            log("Dolby Audio", yes(d.hasDolby))
    );

    // ------------------------------------------------------------
    // SENSORS
    // ------------------------------------------------------------
    section(headerSensors, txtSensors,
            log("Gyroscope", yes(d.hasGyro)) +
            log("Accelerometer", yes(d.hasAccel)) +
            log("Barometer", yes(d.hasBarometer)) +
            log("Compass", yes(d.hasCompass))
    );

    // ------------------------------------------------------------
    // BIOMETRICS
    // ------------------------------------------------------------
    section(headerBiometrics, txtBiometrics,
            log("Biometric System", d.biometrics)
    );

    // ------------------------------------------------------------
    // NFC
    // ------------------------------------------------------------
    section(headerNfc, txtNfc,
            log("NFC", yes(d.hasNFC))
    );

    // ------------------------------------------------------------
    // GNSS (DETAIL)
    // ------------------------------------------------------------
    section(headerGnss, txtGnss,
            log("Supported Constellations", d.gps)
    );

    // ------------------------------------------------------------
    // UWB
    // ------------------------------------------------------------
    section(headerUwb, txtUwb,
            log("Ultra-Wideband",
                    (isPro() || isProMax())
                            ? "Supported (precision ranging)"
                            : "Not supported")
    );

    // ------------------------------------------------------------
    // USB / PORT
    // ------------------------------------------------------------
    section(headerUsb, txtUsb,
            log("Port", d.port) +
            log("USB Standard", d.usbStandard)
    );

    // ------------------------------------------------------------
    // HAPTICS
    // ------------------------------------------------------------
    section(headerHaptics, txtHaptics,
            log("Haptics Engine", "Taptic Engine")
    );

    // ------------------------------------------------------------
    // SYSTEM FEATURES
    // ------------------------------------------------------------
    section(headerSystemFeatures, txtSystemFeatures,
            log("SoC", d.soc) +
            log("Secure Enclave", "Yes")
    );

    // ------------------------------------------------------------
    // SECURITY FLAGS
    // ------------------------------------------------------------
    section(headerSecurityFlags, txtSecurityFlags,
            log("Encrypted Storage", "Yes") +
            log("Biometric Protection", yes(d.hasFaceID || d.hasTouchID))
    );

    // ------------------------------------------------------------
    // ROOT
    // ------------------------------------------------------------
    section(headerRoot, txtRoot,
            log("Root Access", "Not applicable (iOS)")
    );

    // ------------------------------------------------------------
    // OTHER PERIPHERALS / NOTES
    // ------------------------------------------------------------
    section(headerOtherPeripherals, txtOther,
            log("Notes", d.notes)
    );
}
   
// ============================================================
// COLOR HELPERS — PERIPHERALS (HTML SAFE)
// ============================================================

private String log(String label, String value) {
    if (value == null || value.trim().isEmpty()) return "";
    return "<font color=\"#FFFFFF\"><b>• " + label + ":</b></font> " +
           "<font color=\"#00FF7F\">" + value + "</font><br>";
}

private boolean isPro() {
    return d != null
            && d.model != null
            && d.model.toLowerCase().contains("pro")
            && !isProMax();
}

private boolean isProMax() {
    return d != null
            && d.model != null
            && (d.model.toLowerCase().contains("pro max")
                || d.model.toLowerCase().endsWith("max"));
}

// ============================================================
// SECTION HELPER
// ============================================================
private void section(LinearLayout h, TextView t, String content) {

    if (content == null || content.trim().isEmpty()) {
        h.setVisibility(View.GONE);
        t.setVisibility(View.GONE);
        return;
    }

    t.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
t.setVisibility(View.GONE);

    h.setOnClickListener(v ->
            t.setVisibility(
                    t.getVisibility() == View.VISIBLE
                            ? View.GONE
                            : View.VISIBLE
            )
    );
}

// ============================================================
// HIDE ALL
// ============================================================
private void hideAll() {
    if (txtScreen != null) txtScreen.setVisibility(View.GONE);
    if (txtCamera != null) txtCamera.setVisibility(View.GONE);
    if (txtConnectivity != null) txtConnectivity.setVisibility(View.GONE);
    if (txtLocation != null) txtLocation.setVisibility(View.GONE);
    if (txtThermal != null) txtThermal.setVisibility(View.GONE);
    if (txtModem != null) txtModem.setVisibility(View.GONE);
    if (txtWifiAdvanced != null) txtWifiAdvanced.setVisibility(View.GONE);
    if (txtAudio != null) txtAudio.setVisibility(View.GONE);
    if (txtSensors != null) txtSensors.setVisibility(View.GONE);
    if (txtBiometrics != null) txtBiometrics.setVisibility(View.GONE);
    if (txtNfc != null) txtNfc.setVisibility(View.GONE);
    if (txtGnss != null) txtGnss.setVisibility(View.GONE);
    if (txtUwb != null) txtUwb.setVisibility(View.GONE);
    if (txtUsb != null) txtUsb.setVisibility(View.GONE);
    if (txtHaptics != null) txtHaptics.setVisibility(View.GONE);
    if (txtSystemFeatures != null) txtSystemFeatures.setVisibility(View.GONE);
    if (txtSecurityFlags != null) txtSecurityFlags.setVisibility(View.GONE);
    if (txtRoot != null) txtRoot.setVisibility(View.GONE);
    if (txtOther != null) txtOther.setVisibility(View.GONE);
}

private void safeSet(TextView tv, String v) {
        if (tv == null || v == null) return;
        tv.setText(v);
    }

// ============================================================
// HELPERS
// ============================================================
private String yes(boolean value) {
    return value ? "Yes" : null;
}

} // ⬅️ ΜΟΝΑΔΙΚΟ κλείσιμο κλάσης
