// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoPeripheralsActivity — FINAL FULLY ENRICHED
// STRICT MODE: SAME SECTIONS / SAME ORDER / NO UI CHANGES
// ============================================================

package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;

/**
 * Apple Peripherals — FINAL
 * ------------------------------------------------------------
 * • Sections & order LOCKED
 * • Fully enriched with AppleDeviceSpec (iPhone + iPad)
 * • Missing data → not shown
 */
public class AppleDeviceInfoPeripheralsActivity extends Activity {

    // =========================
    // SECTIONS (LOCKED)
    // =========================
    private LinearLayout secCamera;
    private LinearLayout secModem;
    private LinearLayout secConnectivity;
    private LinearLayout secSensors;
    private LinearLayout secAudio;
    private LinearLayout secPorts;
    private LinearLayout secBiometrics;
    private LinearLayout secDisplayOut;
    private LinearLayout secStorage;

    // =========================
    // OUTPUTS
    // =========================
    private TextView outCamera;
    private TextView outModem;
    private TextView outConnectivity;
    private TextView outSensors;
    private TextView outAudio;
    private TextView outPorts;
    private TextView outBiometrics;
    private TextView outDisplayOut;
    private TextView outStorage;

    private AppleDeviceSpec d;

    // ============================================================
    // LIFECYCLE
    // ============================================================
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
        setupToggle(secPorts, outPorts);
        setupToggle(secBiometrics, outBiometrics);
        setupToggle(secDisplayOut, outDisplayOut);
        setupToggle(secStorage, outStorage);

        String model = getSharedPreferences("gel_prefs", MODE_PRIVATE)
                .getString("apple_device_model", "iPhone 13");

        d = AppleSpecs.get(model);

        populateAll();
    }

    // ============================================================
    // BIND
    // ============================================================
    private void bindViews() {

        secCamera        = findViewById(R.id.headerCamera);
        secModem         = findViewById(R.id.headerModem);
        secConnectivity  = findViewById(R.id.headerConnectivity);
        secSensors       = findViewById(R.id.headerSensors);
        secAudio         = findViewById(R.id.headerAudio);
        secPorts         = findViewById(R.id.headerPorts);
        secBiometrics    = findViewById(R.id.headerBiometrics);
        secDisplayOut    = findViewById(R.id.headerDisplayOut);
        secStorage       = findViewById(R.id.headerStorageIO);

        outCamera        = findViewById(R.id.txtCameraContent);
        outModem         = findViewById(R.id.txtModemContent);
        outConnectivity  = findViewById(R.id.txtConnectivityContent);
        outSensors       = findViewById(R.id.txtSensorsContent);
        outAudio         = findViewById(R.id.txtAudioContent);
        outPorts         = findViewById(R.id.txtPortsContent);
        outBiometrics    = findViewById(R.id.txtBiometricsContent);
        outDisplayOut    = findViewById(R.id.txtDisplayOutContent);
        outStorage       = findViewById(R.id.txtStorageContent);
    }

    // ============================================================
    // POPULATE — FULL
    // ============================================================
    private void populateAll() {

        if (d == null) {
            hideAll();
            return;
        }

        // ---------------- CAMERA ----------------
        show(secCamera);
        outCamera.setText(
                log("Main Camera", d.cameraMain) +
                log("Ultra-Wide Camera", d.cameraUltraWide) +
                log("Telephoto Camera", d.cameraTele) +
                log("Front Camera", d.cameraFront) +
                log("Video Recording", d.cameraVideo)
        );

        // ---------------- MODEM ----------------
        show(secModem);
        outModem.setText(
                log("Modem", d.modem) +
                log("5G Support", yesNo(d.has5G)) +
                log("LTE Support", yesNo(d.hasLTE)) +
                log("SIM Slots", d.simSlots) +
                log("eSIM", yesNo(d.hasESim))
        );

        // ---------------- CONNECTIVITY ----------------
        show(secConnectivity);
        outConnectivity.setText(
                log("Wi-Fi", d.wifi) +
                log("Bluetooth", d.bluetooth) +
                log("AirDrop", yesNo(d.hasAirDrop)) +
                log("AirPlay", yesNo(d.hasAirPlay)) +
                log("NFC", yesNo(d.hasNFC))
        );

        // ---------------- SENSORS ----------------
        show(secSensors);
        outSensors.setText(
                log("GPS", d.gps) +
                log("Compass", yesNo(d.hasCompass)) +
                log("Gyroscope", yesNo(d.hasGyro)) +
                log("Accelerometer", yesNo(d.hasAccel)) +
                log("Barometer", yesNo(d.hasBarometer)) +
                log("Thermal", d.thermalNote)
        );

        // ---------------- AUDIO ----------------
        show(secAudio);
        outAudio.setText(
                log("Speakers", d.speakers) +
                log("Microphones", d.microphones) +
                log("Dolby Audio", yesNo(d.hasDolby)) +
                log("Headphone Jack", yesNo(d.hasJack))
        );

        // ---------------- PORTS ----------------
        show(secPorts);
        outPorts.setText(
                log("Port", d.port) +
                log("USB Standard", d.usbStandard) +
                log("Fast Charging", yesNo(d.hasFastCharge)) +
                log("Wireless Charging", yesNo(d.hasWirelessCharge))
        );

        // ---------------- BIOMETRICS ----------------
        show(secBiometrics);
        outBiometrics.setText(
                log("Biometrics", d.biometrics) +
                log("Face ID", yesNo(d.hasFaceID)) +
                log("Touch ID", yesNo(d.hasTouchID))
        );

        // ---------------- DISPLAY OUT ----------------
        show(secDisplayOut);
        outDisplayOut.setText(
                log("External Display", d.displayOut)
        );

        // ---------------- STORAGE ----------------
        show(secStorage);
        outStorage.setText(
                log("Internal Storage", d.storageOptions) +
                log("Base Storage", d.storageBase) +
                log("Expansion", "Not supported (Apple design)")
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
                        content.getVisibility() == View.VISIBLE
                                ? View.GONE
                                : View.VISIBLE
                )
        );
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String yesNo(boolean b) {
        return b ? "Yes" : null;
    }

    private String log(String key, String value) {
        if (value == null || value.trim().isEmpty()) return "";
        return "• " + key + ": " + value + "\n";
    }

    private void hideAll() {
        hide(secCamera);
        hide(secModem);
        hide(secConnectivity);
        hide(secSensors);
        hide(secAudio);
        hide(secPorts);
        hide(secBiometrics);
        hide(secDisplayOut);
        hide(secStorage);
    }

    private void hide(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void show(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }
}
