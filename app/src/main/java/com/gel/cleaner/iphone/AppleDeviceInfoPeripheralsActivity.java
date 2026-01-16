// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoPeripheralsActivity — FINAL STABLE (XML SYNCED)
// ============================================================
package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;

public class AppleDeviceInfoPeripheralsActivity extends Activity {

    // =========================
    // SECTIONS (MATCH XML)
    // =========================
    private LinearLayout secCamera;
    private LinearLayout secModem;
    private LinearLayout secConnectivity;
    private LinearLayout secSensors;
    private LinearLayout secAudio;
    private LinearLayout secBiometrics;

    // =========================
    // OUTPUTS (MATCH XML)
    // =========================
    private TextView outCamera;
    private TextView outModem;
    private TextView outConnectivity;
    private TextView outSensors;
    private TextView outAudio;
    private TextView outBiometrics;

    private AppleDeviceSpec d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_peripherals);

        bindViews();

        setupToggle(secCamera, outCamera);
        setupToggle(secModem, outModem);
        setupToggle(secConnectivity, outConnectivity);
        setupToggle(secSensors, outSensors);
        setupToggle(secAudio, outAudio);
        setupToggle(secBiometrics, outBiometrics);

        String model = getSharedPreferences("gel_prefs", MODE_PRIVATE)
                .getString("apple_device_model", "iPhone 13");

        d = AppleSpecs.get(model);
        populateAll();
    }

    // ============================================================
    // BIND (ONLY EXISTING IDS)
    // ============================================================
    private void bindViews() {

        secCamera       = findViewById(R.id.headerCamera);
        secModem        = findViewById(R.id.headerModem);
        secConnectivity = findViewById(R.id.headerConnectivity);
        secSensors      = findViewById(R.id.headerSensors);
        secAudio        = findViewById(R.id.headerAudioUnified);
        secBiometrics   = findViewById(R.id.headerBiometrics);

        outCamera       = findViewById(R.id.txtCameraContent);
        outModem        = findViewById(R.id.txtModemContent);
        outConnectivity = findViewById(R.id.txtConnectivityContent);
        outSensors      = findViewById(R.id.txtSensorsContent);
        outAudio        = findViewById(R.id.txtAudioUnifiedContent);
        outBiometrics   = findViewById(R.id.txtBiometricsContent);
    }

    // ============================================================
    // POPULATE
    // ============================================================
    private void populateAll() {

        if (d == null) {
            hideAll();
            return;
        }

        show(secCamera);
        outCamera.setText(
                logInfo("Main", d.cameraMain) +
                logInfo("Ultra-Wide", d.cameraUltraWide) +
                logInfo("Telephoto", d.cameraTele) +
                logInfo("Front", d.cameraFront) +
                logInfo("Video", d.cameraVideo)
        );

        show(secModem);
        outModem.setText(
                logInfo("Modem", d.modem) +
                logInfo("5G", yesNo(d.has5G)) +
                logInfo("LTE", yesNo(d.hasLTE)) +
                logInfo("SIM Slots", d.simSlots) +
                logInfo("eSIM", yesNo(d.hasESim))
        );

        show(secConnectivity);
        outConnectivity.setText(
                logInfo("Wi-Fi", d.wifi) +
                logInfo("Bluetooth", d.bluetooth) +
                logInfo("AirDrop", yesNo(d.hasAirDrop)) +
                logInfo("NFC", yesNo(d.hasNFC))
        );

        show(secSensors);
        outSensors.setText(
                logInfo("GPS", d.gps) +
                logInfo("Compass", yesNo(d.hasCompass)) +
                logInfo("Gyroscope", yesNo(d.hasGyro)) +
                logInfo("Accelerometer", yesNo(d.hasAccel)) +
                logInfo("Barometer", yesNo(d.hasBarometer))
        );

        show(secAudio);
        outAudio.setText(
                logInfo("Speakers", d.speakers) +
                logInfo("Microphones", d.microphones) +
                logInfo("Dolby", yesNo(d.hasDolby)) +
                logInfo("Headphone Jack", yesNo(d.hasJack))
        );

        show(secBiometrics);
        outBiometrics.setText(
                logInfo("Face ID", yesNo(d.hasFaceID)) +
                logInfo("Touch ID", yesNo(d.hasTouchID))
        );
    }

    // ============================================================
    // TOGGLE
    // ============================================================
    private void setupToggle(LinearLayout header, TextView content) {
        if (header == null || content == null) return;
        content.setVisibility(View.GONE);
        header.setOnClickListener(v ->
                content.setVisibility(
                        content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
                )
        );
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String yesNo(boolean b) {
        return b ? "Yes" : "No";
    }

    private String logInfo(String k, String v) {
        if (v == null || v.trim().isEmpty()) return "";
        return "• " + k + ": " + v + "\n";
    }

    private void hideAll() {
        hide(secCamera);
        hide(secModem);
        hide(secConnectivity);
        hide(secSensors);
        hide(secAudio);
        hide(secBiometrics);
    }

    private void hide(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void show(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }
}
