// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoPeripheralsActivity — FINAL UNIFIED
// STRICT MODE: XML IS SOURCE OF TRUTH
// ============================================================

package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;

public class AppleDeviceInfoPeripheralsActivity extends Activity {

    // ============================================================
    // HEADERS (AS IN XML)
    // ============================================================
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
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_device_info_peripherals);

        bind();

        d = AppleSpecs.get(
                getSharedPreferences("gel_prefs", MODE_PRIVATE)
                        .getString("apple_device_model", "iPhone 13")
        );

        populate();
    }

    // ============================================================
    // BIND
    // ============================================================
    private void bind() {

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

    // ============================================================
    // POPULATE
    // ============================================================
    private void populate() {

        section(headerScreen, txtScreen,
                log("Display", d.display) +
                log("Resolution", d.resolution) +
                log("Refresh Rate", d.refreshRate)
        );

        section(headerCamera, txtCamera,
                log("Main", d.cameraMain) +
                log("Ultra-Wide", d.cameraUltraWide) +
                log("Telephoto", d.cameraTele) +
                log("Front", d.cameraFront) +
                log("Video", d.cameraVideo)
        );

        section(headerConnectivity, txtConnectivity,
                log("Wi-Fi", d.wifi) +
                log("Bluetooth", d.bluetooth) +
                log("AirDrop", yes(d.hasAirDrop)) +
                log("AirPlay", yes(d.hasAirPlay))
        );

        section(headerLocation, txtLocation,
                log("GPS", d.gps)
        );

        section(headerThermal, txtThermal,
                log("Thermal", d.thermalNote)
        );

        section(headerModem, txtModem,
                log("Modem", d.modem) +
                log("5G", yes(d.has5G)) +
                log("LTE", yes(d.hasLTE)) +
                log("SIM", d.simSlots)
        );

        section(headerWifiAdvanced, txtWifiAdvanced,
                log("Wi-Fi", d.wifi)
        );

        section(headerAudioUnified, txtAudio,
                log("Speakers", d.speakers) +
                log("Microphones", d.microphones) +
                log("Dolby", yes(d.hasDolby))
        );

        section(headerSensors, txtSensors,
                log("Gyroscope", yes(d.hasGyro)) +
                log("Accelerometer", yes(d.hasAccel)) +
                log("Barometer", yes(d.hasBarometer))
        );

        section(headerBiometrics, txtBiometrics,
                log("Biometrics", d.biometrics)
        );

        section(headerNfc, txtNfc,
                log("NFC", yes(d.hasNFC))
        );

        section(headerGnss, txtGnss,
                log("GNSS", d.gps)
        );

        section(headerUwb, txtUwb,
                log("Ultra-Wideband", "Supported on select models")
        );

        section(headerUsb, txtUsb,
                log("Port", d.port) +
                log("USB", d.usbStandard)
        );

        section(headerHaptics, txtHaptics,
                log("Haptics", "Taptic Engine")
        );

        section(headerSystemFeatures, txtSystemFeatures,
                log("SoC", d.soc) +
                log("Secure Enclave", "Yes")
        );

        section(headerSecurityFlags, txtSecurityFlags,
                log("Encrypted Storage", "Yes")
        );

        section(headerRoot, txtRoot,
                log("Root", "Not applicable (iOS)")
        );

        section(headerOtherPeripherals, txtOther,
                log("Notes", d.notes)
        );
    }

    // ============================================================
    // SECTION HELPER
    // ============================================================
    private void section(LinearLayout h, TextView t, String content) {
        if (content.trim().isEmpty()) {
            h.setVisibility(View.GONE);
            t.setVisibility(View.GONE);
            return;
        }

        t.setText(content);
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
    // HELPERS
    // ============================================================
    private String log(String k, String v) {
        if (v == null || v.trim().isEmpty()) return "";
        return "• " + k + ": " + v + "\n";
    }

    private String yes(boolean b) {
        return b ? "Yes" : null;
    }
}
