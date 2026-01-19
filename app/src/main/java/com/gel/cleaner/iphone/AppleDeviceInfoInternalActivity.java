// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoInternalActivity — FINAL FULLY ENRICHED
// STRICT MODE: NO SECTION / ORDER CHANGES
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
 * • Sections & order LOCKED
 * • Fully enriched with AppleDeviceSpec data
 * • If a field is missing → not shown
 */
public class AppleDeviceInfoInternalActivity extends Activity {

    // =========================
    // SECTIONS (LOCKED)
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

        bindViews();

        setupToggle(secSystem, outSystem);
        setupToggle(secOS, outOS);
        setupToggle(secCPU, outCPU);
        setupToggle(secRAM, outRAM);
        setupToggle(secDisplay, outDisplay);
        setupToggle(secConnectivity, outConnectivity);

        String model = getSharedPreferences("gel_prefs", MODE_PRIVATE)
                .getString("apple_device_model", "iPhone 13");

        d = AppleSpecs.get(model);

        populateAll();
    }

    // ============================================================
    // BIND
    // ============================================================
    private void bindViews() {

        secSystem       = findViewById(R.id.headerSystem);
        secOS           = findViewById(R.id.headerAndroid);   // reused → iOS
        secCPU          = findViewById(R.id.headerCpu);
        secRAM          = findViewById(R.id.headerRam);
        secDisplay      = findViewById(R.id.headerGpu);       // reused → Display
        secConnectivity = findViewById(R.id.headerStorage);   // reused → Connectivity

        outSystem       = findViewById(R.id.txtSystemContent);
        outOS           = findViewById(R.id.txtAndroidContent);
        outCPU          = findViewById(R.id.txtCpuContent);
        outRAM          = findViewById(R.id.txtRamContent);
        outDisplay      = findViewById(R.id.txtGpuContent);
        outConnectivity = findViewById(R.id.txtStorageContent);
    }

    // ============================================================
    // POPULATE — FULL
    // ============================================================
    private void populateAll() {

        if (d == null) {
            hideAll();
            return;
        }

        // ---------------- SYSTEM ----------------
        show(secSystem);
        outSystem.setText(
                log("Manufacturer", "Apple") +
                log("Model", d.model) +
                log("Year", d.year) +
                log("Identifier", d.identifier) +
                log("Model Number", d.modelNumber) +
                log("SoC", d.soc) +
                log("Chipset", d.chipset)
        );

        // ---------------- OS ----------------
        show(secOS);
        outOS.setText(
                log("Operating System", d.os) +
                log("Charging Standard", d.charging) +
                log("Notes", d.notes)
        );

        // ---------------- CPU ----------------
        show(secCPU);
        outCPU.setText(
                log("SoC", d.soc) +
                log("CPU", d.cpu) +
                log("Architecture", d.arch) +
                log("CPU Cores", d.cpuCores > 0 ? String.valueOf(d.cpuCores) : null) +
                log("Process Node", d.processNode) +
                log("GPU", d.gpu) +
                log("GPU Cores", d.gpuCores > 0 ? String.valueOf(d.gpuCores) : null) +
                log("Metal Feature Set", d.metalFeatureSet)
        );

        // ---------------- RAM ----------------
        show(secRAM);
        outRAM.setText(
                log("Memory", d.ram) +
                log("Memory Type", d.ramType)
        );

        // ---------------- DISPLAY ----------------
        show(secDisplay);
        outDisplay.setText(
                log("Screen Size", d.screen) +
                log("Display", d.display) +
                log("Resolution", d.resolution) +
                log("Refresh Rate", d.refreshRate) +
                log("External Display", d.displayOut)
        );

        // ---------------- CONNECTIVITY ----------------
        show(secConnectivity);
        outConnectivity.setText(
                log("Wi-Fi", d.wifi) +
                log("Bluetooth", d.bluetooth) +
                log("Cellular", d.cellular) +
                log("5G Support", d.has5G ? "Yes" : null) +
                log("LTE Support", d.hasLTE ? "Yes" : null) +
                log("Biometrics", d.biometrics) +
                log("Port", d.port) +
                log("USB Standard", d.usbStandard) +
                log("Charging", d.charging)
        );
    }

    // ============================================================
    // TOGGLE
    // ============================================================
    private void setupToggle(LinearLayout header, TextView content) {
        if (header == null || content == null) return;

        content.setVisibility(View.GONE);

        header.setOnClickListener(v -> {
            content.setVisibility(
                    content.getVisibility() == View.VISIBLE
                            ? View.GONE
                            : View.VISIBLE
            );
        });
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String log(String key, String value) {
        if (value == null || value.trim().isEmpty()) return "";
        return "• " + key + ": " + value + "\n";
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
