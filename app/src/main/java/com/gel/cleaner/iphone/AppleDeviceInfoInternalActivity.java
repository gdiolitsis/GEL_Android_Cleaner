// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoInternalActivity — FINAL STABLE
// XML-DRIVEN | SAME LOGIC AS PERIPHERALS
// ============================================================

package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;

public class AppleDeviceInfoInternalActivity extends Activity {

    // =========================
    // SECTIONS (FROM XML)
    // =========================
    private LinearLayout secSystem;
    private LinearLayout secAndroid;
    private LinearLayout secCpu;
    private LinearLayout secGpu;
    private LinearLayout secThermal;
    private LinearLayout secVulkan;
    private LinearLayout secRam;
    private LinearLayout secStorage;

    // =========================
    // CONTENT
    // =========================
    private TextView outSystem;
    private TextView outAndroid;
    private TextView outCpu;
    private TextView outGpu;
    private TextView outThermal;
    private TextView outVulkan;
    private TextView outRam;
    private TextView outStorage;

    private AppleDeviceSpec d;

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        bindViews();
        setupToggles();

        String model = getSharedPreferences("gel_prefs", MODE_PRIVATE)
                .getString("apple_device_model", "iPhone 13");

        d = AppleSpecs.get(model);

        populateAll();
    }

    // ============================================================
    // BIND VIEWS (XML IS KING)
    // ============================================================
    private void bindViews() {

        secSystem   = findViewById(R.id.headerSystem);
        secAndroid  = findViewById(R.id.headerAndroid);
        secCpu      = findViewById(R.id.headerCpu);
        secGpu      = findViewById(R.id.headerGpu);
        secThermal  = findViewById(R.id.headerThermal);
        secVulkan   = findViewById(R.id.headerVulkan);
        secRam      = findViewById(R.id.headerRam);
        secStorage  = findViewById(R.id.headerStorage);

        outSystem   = findViewById(R.id.txtSystemContent);
        outAndroid  = findViewById(R.id.txtAndroidContent);
        outCpu      = findViewById(R.id.txtCpuContent);
        outGpu      = findViewById(R.id.txtGpuContent);
        outThermal  = findViewById(R.id.txtThermalContent);
        outVulkan   = findViewById(R.id.txtVulkanContent);
        outRam      = findViewById(R.id.txtRamContent);
        outStorage  = findViewById(R.id.txtStorageContent);
    }

    // ============================================================
    // TOGGLES (SAME AS PERIPHERALS)
    // ============================================================
    private void setupToggles() {

        setupToggle(secSystem,  outSystem);
        setupToggle(secAndroid, outAndroid);
        setupToggle(secCpu,     outCpu);
        setupToggle(secGpu,     outGpu);
        setupToggle(secThermal, outThermal);
        setupToggle(secVulkan,  outVulkan);
        setupToggle(secRam,     outRam);
        setupToggle(secStorage, outStorage);
    }

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
                log("Manufacturer", "Apple") +
                log("Model", d.model) +
                log("Identifier", d.identifier) +
                log("Model Number", d.modelNumber) +
                log("Year", d.year)
        );

        // ---------------- ANDROID / iOS ----------------
        show(secAndroid);
        outAndroid.setText(
                log("Operating System", d.os) +
                log("Notes", d.notes)
        );

        // ---------------- CPU ----------------
        show(secCpu);
        outCpu.setText(
                log("SoC", d.soc) +
                log("CPU", d.cpu) +
                log("Architecture", d.arch) +
                log("CPU Cores", d.cpuCores > 0 ? String.valueOf(d.cpuCores) : null) +
                log("Process Node", d.processNode)
        );

        // ---------------- GPU ----------------
        show(secGpu);
        outGpu.setText(
                log("GPU", d.gpu) +
                log("GPU Cores", d.gpuCores > 0 ? String.valueOf(d.gpuCores) : null) +
                log("Metal Feature Set", d.metalFeatureSet)
        );

        // ---------------- THERMAL ----------------
        show(secThermal);
        outThermal.setText(
                log("Thermal Notes", d.thermalNote)
        );

        // ---------------- VULKAN ----------------
        show(secVulkan);
        outVulkan.setText(
                log("Graphics API", "Metal") +
                log("Vulkan", "Not supported on iOS")
        );

        // ---------------- RAM ----------------
        show(secRam);
        outRam.setText(
                log("Memory", d.ram) +
                log("Memory Type", d.ramType)
        );

        // ---------------- STORAGE ----------------
        show(secStorage);
        outStorage.setText(
                log("Storage Options", d.storageOptions) +
                log("Base Storage", d.storageBase)
        );
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
        hide(secAndroid);
        hide(secCpu);
        hide(secGpu);
        hide(secThermal);
        hide(secVulkan);
        hide(secRam);
        hide(secStorage);
    }

    private void hide(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void show(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }
}
