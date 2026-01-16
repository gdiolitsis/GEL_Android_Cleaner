// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoPeripheralsActivity — FINAL STABLE (WITH TOGGLES)
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
 * • Κουμπιά (toggles) όπως στο Android Peripherals
 * • Hardcoded δεδομένα από AppleSpecs → AppleDeviceSpec
 * • Ό,τι δεν υπάρχει στο spec → δεν εμφανίζεται
 */
public class AppleDeviceInfoPeripheralsActivity extends Activity {

    // =========================
    // SECTIONS
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

        // -------- bind views --------
        bindViews();

        // -------- toggles --------
        setupToggle(secCamera, outCamera);
        setupToggle(secModem, outModem);
        setupToggle(secConnectivity, outConnectivity);
        setupToggle(secSensors, outSensors);
        setupToggle(secAudio, outAudio);
        setupToggle(secPorts, outPorts);
        setupToggle(secBiometrics, outBiometrics);
        setupToggle(secDisplayOut, outDisplayOut);
        setupToggle(secStorage, outStorage);

        // -------- load selected model --------
        String model = getSharedPreferences("gel_prefs", MODE_PRIVATE)
                .getString("apple_device_model", "iPhone 13");

        d = AppleSpecs.get(model);

        // -------- fill UI --------
        populateAll();
    }

    // ============================================================
    // BIND
    // ============================================================
    private void bindViews() {

        // sections
        secCamera        = findViewById(R.id.headerCamera);
        secModem         = findViewById(R.id.headerModem);
        secConnectivity  = findViewById(R.id.headerConnectivity);
        secSensors       = findViewById(R.id.headerSensors);
        secAudio         = findViewById(R.id.headerAudio);
        secPorts         = findViewById(R.id.headerPorts);
        secBiometrics    = findViewById(R.id.headerBiometrics);
        secDisplayOut    = findViewById(R.id.headerDisplayOut);
        secStorage       = findViewById(R.id.headerStorageIO);

        // outputs
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
    // POPULATE
    // ============================================================
    private void populateAll() {

        if (d == null) {
            hideAll();
            return;
        }

        // ---------------- CAMERA ----------------
        show(secCamera);
        outCamera.setText(
                logInfo("Main", d.cameraMain) +
                logInfo("Ultra-Wide", d.cameraUltraWide) +
                logInfo("Telephoto", d.cameraTele) +
                logInfo("Front", d.cameraFront) +
                logInfo("Video", d.cameraVideo)
        );

        // ---------------- MODEM ----------------
        show(secModem);
        outModem.setText(
                logInfo("Modem", d.modem) +
                logInfo("5G", yesNo(d.has5G)) +
                logInfo("LTE", yesNo(d.hasLTE)) +
                logInfo("SIM Slots", d.simSlots) +
                logInfo("eSIM", yesNo(d.hasESim))
        );

        // ---------------- CONNECTIVITY ----------------
        show(secConnectivity);
        outConnectivity.setText(
                logInfo("Wi-Fi", d.wifi) +
                logInfo("Bluetooth", d.bluetooth) +
                logInfo("AirDrop", yesNo(d.hasAirDrop)) +
                logInfo("NFC", yesNo(d.hasNFC))
        );

        // ---------------- SENSORS ----------------
        show(secSensors);
        outSensors.setText(
                logInfo("GPS", d.gps) +
                logInfo("Compass", yesNo(d.hasCompass)) +
                logInfo("Gyroscope", yesNo(d.hasGyro)) +
                logInfo("Accelerometer", yesNo(d.hasAccel)) +
                logInfo("Barometer", yesNo(d.hasBarometer))
        );

        // ---------------- AUDIO ----------------
        show(secAudio);
        outAudio.setText(
                logInfo("Speakers", d.speakers) +
                logInfo("Dolby", yesNo(d.hasDolby)) +
                logInfo("Microphones", d.microphones) +
                logInfo("Headphone Jack", yesNo(d.hasJack))
        );

        // ---------------- PORTS ----------------
        show(secPorts);
        outPorts.setText(
                logInfo("Charging Port", d.port) +
                logInfo("USB Standard", d.usbStandard) +
                logInfo("Fast Charge", yesNo(d.hasFastCharge)) +
                logInfo("Wireless Charge", yesNo(d.hasWirelessCharge))
        );

        // ---------------- BIOMETRICS ----------------
        show(secBiometrics);
        outBiometrics.setText(
                logInfo("Face ID", yesNo(d.hasFaceID)) +
                logInfo("Touch ID", yesNo(d.hasTouchID))
        );

        // ---------------- DISPLAY OUT ----------------
        show(secDisplayOut);
        outDisplayOut.setText(
                logInfo("Screen Output", d.displayOut) +
                logInfo("AirPlay", yesNo(d.hasAirPlay))
        );

        // ---------------- STORAGE ----------------
        show(secStorage);
        outStorage.setText(
                logInfo("Internal Storage", d.storageOptions) +
                logInfo("External SD", "Not supported (Apple design)")
        );
    }

    // ============================================================
    // TOGGLE
    // ============================================================
    private void setupToggle(LinearLayout header, TextView content) {
        if (header == null || content == null) return;

        content.setVisibility(View.GONE);

        header.setOnClickListener(v -> {
            if (content.getVisibility() == View.VISIBLE) {
                content.setVisibility(View.GONE);
            } else {
                content.setVisibility(View.VISIBLE);
            }
        });
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
