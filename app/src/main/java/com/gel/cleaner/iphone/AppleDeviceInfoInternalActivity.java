// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoInternalActivity — FINAL STABLE
// ============================================================
package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;

/**
 * Apple Internals — FINAL
 * ------------------------------------------------------------
 * • ΚΑΡΜΠΟΝ σε sections με Android Internals
 * • Παίρνει δεδομένα από AppleSpecs → AppleDeviceSpec
 * • Ό,τι δεν υπάρχει στο spec → δεν εμφανίζεται
 */
public class AppleDeviceInfoInternalActivity extends Activity {

    // =========================
    // SECTIONS
    // =========================
    private LinearLayout secSystem;
    private LinearLayout secOS;
    private LinearLayout secCPU;
    private LinearLayout secRAM;
    private LinearLayout secDisplay;
    private LinearLayout secConnectivity;

    // =========================
    // OUTPUTS
    // =========================
    private TextView outSystem;
    private TextView outOS;
    private TextView outCPU;
    private TextView outRAM;
    private TextView outDisplay;
    private TextView outConnectivity;

    private AppleDeviceSpec d;

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        // -------- bind views --------
        bindViews();

        // -------- toggles --------
        setupToggle(secSystem, outSystem);
        setupToggle(secOS, outOS);
        setupToggle(secCPU, outCPU);
        setupToggle(secRAM, outRAM);
        setupToggle(secDisplay, outDisplay);
        setupToggle(secConnectivity, outConnectivity);

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

        // sections (ίδια ids με Android layout)
        secSystem       = findViewById(R.id.headerSystem);
        secOS           = findViewById(R.id.headerAndroid);   // reuse → iOS
        secCPU          = findViewById(R.id.headerCpu);
        secRAM          = findViewById(R.id.headerRam);
        secDisplay      = findViewById(R.id.headerGpu);       // reuse → Display
        secConnectivity = findViewById(R.id.headerStorage);   // reuse → Connectivity

        // outputs
        outSystem       = findViewById(R.id.txtSystemContent);
        outOS           = findViewById(R.id.txtAndroidContent);
        outCPU          = findViewById(R.id.txtCpuContent);
        outRAM          = findViewById(R.id.txtRamContent);
        outDisplay      = findViewById(R.id.txtGpuContent);
        outConnectivity = findViewById(R.id.txtStorageContent);
    }

    // ============================================================
    // POPULATE
    // ============================================================
    private void populateAll() {

        if (d == null) {
            hideAll();
            return;
        }

        // ---------------- SYSTEM ----------------
        show(secSystem);
        outSystem.setText(
                logInfo("Manufacturer", "Apple") +
                logInfo("SoC", d.soc)
        );

        // ---------------- OS ----------------
        show(secOS);
        outOS.setText(
                logInfo("Operating System", d.os)
        );

        // ---------------- CPU ----------------
        show(secCPU);
        outCPU.setText(
                logInfo("CPU", d.cpu)
        );

        // ---------------- RAM ----------------
        show(secRAM);
        outRAM.setText(
                logInfo("Memory", d.ram)
        );

        // ---------------- DISPLAY ----------------
        show(secDisplay);
        outDisplay.setText(
                logInfo("Screen", d.screen)
        );

        // ---------------- CONNECTIVITY ----------------
        show(secConnectivity);
        outConnectivity.setText(
                logInfo("Wi-Fi", d.wifi) +
                logInfo("Bluetooth", d.bluetooth) +
                logInfo("Biometrics", d.biometrics) +
                logInfo("Port", d.port) +
                logInfo("Charging", d.charging)
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
    private String logInfo(String k, String v) {
        if (v == null || v.trim().isEmpty()) return "";
        return "• " + k + ": " + v + "\n";
    }

    private void hideAll() {
        hide(secSystem);
        hide(secOS);
        hide(secCPU);
        hide(secRAM);
        hide(secDisplay);
        hide(secConnectivity);
    }

    private void hide(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void show(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }
}
