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

    // -------- HEADERS (όπως υπάρχουν στο XML) --------
    private LinearLayout headerSystem;
    private LinearLayout headerOS;        // headerAndroid → iOS
    private LinearLayout headerCPU;
    private LinearLayout headerGPU;
    private LinearLayout headerThermal;
    private LinearLayout headerMetal;     // headerVulkan → Metal
    private LinearLayout headerRAM;
    private LinearLayout headerStorage;

    // -------- CONTENT --------
    private TextView outSystem;
    private TextView outOS;
    private TextView outCPU;
    private TextView outGPU;
    private TextView outThermal;
    private TextView outMetal;
    private TextView outRAM;
    private TextView outStorage;

    private AppleDeviceSpec d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        bindViews();
        d = AppleSpecProvider.getSelectedDevice(this);

        populateAll();
    }

    // ============================================================
    // BIND — ΜΟΝΟ ids που ΥΠΑΡΧΟΥΝ στο XML
    // ============================================================
    private void bindViews() {

        headerSystem   = findViewById(R.id.headerSystem);
        headerOS       = findViewById(R.id.headerAndroid); // reuse → iOS
        headerCPU      = findViewById(R.id.headerCpu);
        headerGPU      = findViewById(R.id.headerGpu);
        headerThermal  = findViewById(R.id.headerThermal);
        headerMetal    = findViewById(R.id.headerVulkan);  // reuse → Metal
        headerRAM      = findViewById(R.id.headerRam);
        headerStorage  = findViewById(R.id.headerStorage);

        outSystem   = findViewById(R.id.txtSystemContent);
        outOS       = findViewById(R.id.txtAndroidContent); // reuse → iOS
        outCPU      = findViewById(R.id.txtCpuContent);
        outGPU      = findViewById(R.id.txtGpuContent);
        outThermal  = findViewById(R.id.txtThermalContent);
        outMetal    = findViewById(R.id.txtVulkanContent);  // reuse → Metal
        outRAM      = findViewById(R.id.txtRamContent);
        outStorage  = findViewById(R.id.txtStorageContent);
    }

    // ============================================================
    // POPULATE — ΚΑΡΜΠΟΝ ΠΛΗΡΟΦΟΡΙΩΝ
    // ============================================================
    private void populateAll() {
        if (d == null) {
            hideAll();
            return;
        }

        // -------- SYSTEM --------
        show(headerSystem, outSystem);
        outSystem.setText(
                logInfo("Manufacturer", "Apple") +
                logInfo("Model", d.model) +
                opt("Board", d.board) +
                opt("Release year", String.valueOf(d.releaseYear))
        );

        // -------- iOS --------
        show(headerOS, outOS);
        outOS.setText(
                logInfo("OS", "iOS / iPadOS") +
                opt("Base version", d.osBase) +
                opt("Latest supported", d.osLatest)
        );

        // -------- CPU --------
        show(headerCPU, outCPU);
        outCPU.setText(
                logInfo("Chip", d.chip) +
                opt("Architecture", d.arch) +
                opt("Cores", d.cpuCores) +
                opt("Process", d.processNode)
        );

        // -------- GPU --------
        show(headerGPU, outGPU);
        outGPU.setText(
                logInfo("GPU", d.gpu) +
                opt("Cores", d.gpuCores) +
                opt("API", "Metal")
        );

        // -------- THERMAL --------
        if (has(d.thermalNote)) {
            show(headerThermal, outThermal);
            outThermal.setText(
                    logWarn("Thermal", "Estimated") +
                    opt("Note", d.thermalNote)
            );
        } else hide(headerThermal, outThermal);

        // -------- METAL --------
        show(headerMetal, outMetal);
        outMetal.setText(
                logInfo("Graphics API", "Metal") +
                opt("Feature set", d.metalFeatureSet)
        );

        // -------- RAM --------
        show(headerRAM, outRAM);
        outRAM.setText(
                logInfo("RAM", d.ram) +
                opt("Type", d.ramType)
        );

        // -------- STORAGE --------
        show(headerStorage, outStorage);
        outStorage.setText(
                logInfo("Base storage", d.storageBase) +
                opt("Options", d.storageOptions)
        );
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String logInfo(String k, String v) {
        return "• " + k + ": " + v + "\n";
    }

    private String logWarn(String k, String v) {
        return "• " + k + ": " + v + "\n";
    }

    private String opt(String k, String v) {
        if (!has(v)) return "";
        return logInfo(k, v);
    }

    private boolean has(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private void hideAll() {
        hide(headerSystem, outSystem);
        hide(headerOS, outOS);
        hide(headerCPU, outCPU);
        hide(headerGPU, outGPU);
        hide(headerThermal, outThermal);
        hide(headerMetal, outMetal);
        hide(headerRAM, outRAM);
        hide(headerStorage, outStorage);
    }

    private void hide(View h, View c) {
        if (h != null) h.setVisibility(View.GONE);
        if (c != null) c.setVisibility(View.GONE);
    }

    private void show(View h, View c) {
        if (h != null) h.setVisibility(View.VISIBLE);
        if (c != null) c.setVisibility(View.VISIBLE);
    }
}
