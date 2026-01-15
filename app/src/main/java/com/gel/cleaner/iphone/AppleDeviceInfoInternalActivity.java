// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceInfoInternalActivity — CARBON INFO EDITION

package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;
import com.gel.cleaner.base.AppleSpecProvider;
import com.gel.cleaner.iphone.specs.AppleDeviceSpec;

public class AppleDeviceInfoInternalActivity extends Activity {

    // -------- UI roots (ίδια ids με Android layout) --------
    private LinearLayout secSystem;
    private LinearLayout secOS;
    private LinearLayout secCPU;
    private LinearLayout secGPU;
    private LinearLayout secThermal;
    private LinearLayout secMetal;
    private LinearLayout secRAM;
    private LinearLayout secStorage;
    private LinearLayout secConnectivity;

    // -------- Output views --------
    private TextView outSystem;
    private TextView outOS;
    private TextView outCPU;
    private TextView outGPU;
    private TextView outThermal;
    private TextView outMetal;
    private TextView outRAM;
    private TextView outStorage;
    private TextView outConnectivity;

    private AppleDeviceSpec d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal); // ίδιο layout με Android

        bindViews();
        d = AppleSpecProvider.getSelectedDevice(this);
        populateAll();
    }

    // ============================================================
    // BIND
    // ============================================================
    private void bindViews() {

        // Sections (όλα ΥΠΑΡΧΟΥΝ στο Android layout)
        secSystem       = findViewById(R.id.section_system);
        secOS           = findViewById(R.id.section_android); // reuse → iOS
        secCPU          = findViewById(R.id.section_cpu);
        secGPU          = findViewById(R.id.section_gpu);
        secThermal      = findViewById(R.id.section_thermal);
        secMetal        = findViewById(R.id.section_vulkan);  // reuse → Metal
        secRAM          = findViewById(R.id.section_ram);
        secStorage      = findViewById(R.id.section_storage);
        secConnectivity = findViewById(R.id.section_connectivity);

        // Outputs
        outSystem       = findViewById(R.id.txtSystemContent);
        outOS           = findViewById(R.id.txtAndroidContent); // reuse → iOS
        outCPU          = findViewById(R.id.txtCpuContent);
        outGPU          = findViewById(R.id.txtGpuContent);
        outThermal      = findViewById(R.id.txtThermalContent);
        outMetal        = findViewById(R.id.txtVulkanContent);  // reuse → Metal
        outRAM          = findViewById(R.id.txtRamContent);
        outStorage      = findViewById(R.id.txtStorageContent);
        outConnectivity = findViewById(R.id.txtConnectivityContent);
    }

    // ============================================================
    // POPULATE — ΚΑΡΜΠΟΝ ΠΛΗΡΟΦΟΡΙΩΝ
    // ============================================================
    private void populateAll() {
        if (d == null) {
            hideAll();
            return;
        }

        // -------- 1) SYSTEM --------
        show(secSystem);
        outSystem.setText(
                logInfo("Manufacturer", "Apple") +
                logInfo("Model", d.model) +
                opt("Board", d.board) +
                opt("Release year", String.valueOf(d.releaseYear))
        );

        // -------- 2) iOS (αντί Android) --------
        show(secOS);
        outOS.setText(
                logInfo("OS", "iOS / iPadOS") +
                opt("Base version", d.osBase) +
                opt("Latest supported", d.osLatest)
        );

        // -------- 3) CPU --------
        show(secCPU);
        outCPU.setText(
                logInfo("Chip", d.chip) +
                opt("Architecture", d.arch) +
                opt("Cores", d.cpuCores) +
                opt("Process", d.processNode)
        );

        // -------- 4) GPU --------
        show(secGPU);
        outGPU.setText(
                logInfo("GPU", d.gpu) +
                opt("Cores", d.gpuCores) +
                opt("API", "Metal")
        );

        // -------- 5) THERMAL --------
        if (has(d.thermalNote)) {
            show(secThermal);
            outThermal.setText(
                    logWarn("Thermal", "Estimated") +
                    opt("Note", d.thermalNote)
            );
        } else hide(secThermal);

        // -------- 6) METAL (αντί Vulkan) --------
        show(secMetal);
        outMetal.setText(
                logInfo("Graphics API", "Metal") +
                opt("Feature set", d.metalFeatureSet)
        );

        // -------- 7) RAM --------
        show(secRAM);
        outRAM.setText(
                logInfo("RAM", d.ram) +
                opt("Type", d.ramType)
        );

        // -------- 8) STORAGE --------
        show(secStorage);
        outStorage.setText(
                logInfo("Base storage", d.storageBase) +
                opt("Options", d.storageOptions)
        );

        // -------- 9) CONNECTIVITY --------
        show(secConnectivity);
        outConnectivity.setText(
                opt("SIM slots", d.simSlots) +
                opt("eSIM", d.esim) +
                opt("5G", d.net5g) +
                opt("Wi-Fi", d.wifi) +
                opt("Bluetooth", d.bt) +
                opt("GPS", d.gps)
        );
    }

    // ============================================================
    // HELPERS — ίδια φιλοσοφία log styles
    // ============================================================
    private String logInfo(String k, String v)  { return "• " + k + ": " + v + "\n"; }
    private String logOk(String k, String v)    { return "• " + k + ": " + v + "\n"; }
    private String logWarn(String k, String v)  { return "• " + k + ": " + v + "\n"; }
    private String logError(String k, String v) { return "• " + k + ": " + v + "\n"; }

    private String opt(String k, String v) {
        if (!has(v)) return "";
        return logInfo(k, v);
    }

    private boolean has(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private void hideAll() {
        hide(secSystem); hide(secOS); hide(secCPU); hide(secGPU);
        hide(secThermal); hide(secMetal); hide(secRAM);
        hide(secStorage); hide(secConnectivity);
    }

    private void hide(View v) { if (v != null) v.setVisibility(View.GONE); }
    private void show(View v) { if (v != null) v.setVisibility(View.VISIBLE); }
}
