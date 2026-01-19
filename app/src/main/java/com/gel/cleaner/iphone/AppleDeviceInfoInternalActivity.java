// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoInternalActivity — FINAL STABLE
// XML-DRIVEN | SAME LOGIC AS PERIPHERALS
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

    bind();   // ✔ σωστό όνομα
    setupToggles(); 

    SharedPreferences prefs =
            getSharedPreferences("gel_prefs", MODE_PRIVATE);

    String model = prefs.getString("apple_model", null);

    if (model == null) {
        finish();   // ⛔ εδώ ΔΕΝ δείχνουμε popup
        return;
    }

    d = AppleSpecs.get(model);

    populate();   // ✔ σωστό method
}

    // ============================================================
    // BIND VIEWS (XML IS KING)
    // ============================================================
    private void bind() {

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

TextView[] allInternalContents = new TextView[] {
        outSystem,
        outAndroid,
        outCpu,
        outGpu,
        outThermal,
        outVulkan,
        outRam,
        outStorage
};

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
// POPULATE — FINAL (SERIES + PRO / PRO MAX AWARE)
// ============================================================
private void populate() {

    if (d == null) {
        hideAll();
        return;
    }

    // ---------------- SYSTEM ----------------
    show(secSystem);
    outSystem.setText(
            Html.fromHtml(
                    log("Manufacturer", "Apple") +
                    log("Model", d.model) +
                    log("Identifier", d.identifier) +
                    log("Model Number", d.modelNumber) +
                    log("Year", d.year),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- OS / PLATFORM ----------------
    show(secAndroid);
    outAndroid.setText(
            Html.fromHtml(
                    log("Operating System", d.os) +
                    log("Platform Tier",
                            isProMax() ? "Pro Max — highest bin"
                          : isPro()    ? "Pro — enhanced configuration"
                                       : "Standard"),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- CPU ----------------
    show(secCpu);
    outCpu.setText(
            Html.fromHtml(
                    log("SoC", d.soc) +
                    log("CPU", d.cpu) +
                    log("Architecture", d.arch) +
                    log("CPU Cores",
                            d.cpuCores > 0 ? String.valueOf(d.cpuCores) : null) +
                    log("Process Node", d.processNode) +
                    log("CPU Tier",
                            isProMax() ? "High (best silicon bin)"
                          : isPro()    ? "Enhanced"
                                       : "Standard"),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- GPU ----------------
    show(secGpu);
    outGpu.setText(
            Html.fromHtml(
                    log("GPU", d.gpu) +
                    log("GPU Cores",
                            d.gpuCores > 0 ? String.valueOf(d.gpuCores) : null) +
                    log("Metal Feature Set", d.metalFeatureSet) +
                    log("GPU Tier",
                            isProMax() ? "Max GPU configuration"
                          : isPro()    ? "Pro GPU configuration"
                                       : "Standard GPU"),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- THERMAL ----------------
    show(secThermal);
    outThermal.setText(
            Html.fromHtml(
                    log("Thermal Design",
                            isProMax() ? "Improved heat dissipation (larger chassis)"
                          : isPro()    ? "Enhanced thermal envelope"
                                       : "Standard thermal design") +
                    log("Thermal Notes", d.thermalNote),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- GRAPHICS API ----------------
    show(secVulkan);
    outVulkan.setText(
            Html.fromHtml(
                    log("Primary Graphics API", "Metal") +
                    log("Vulkan", "Not supported on iOS"),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- RAM ----------------
    show(secRam);
    outRam.setText(
            Html.fromHtml(
                    log("Memory", d.ram) +
                    log("Memory Type", d.ramType) +
                    log("Memory Tier",
                            isProMax() ? "Higher RAM capacity"
                          : isPro()    ? "Mid-High RAM capacity"
                                       : "Base RAM capacity"),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );

    // ---------------- STORAGE ----------------
    show(secStorage);
    outStorage.setText(
            Html.fromHtml(
                    log("Storage Options", d.storageOptions) +
                    log("Base Storage", d.storageBase) +
                    log("Storage Tier",
                            isProMax() ? "Higher maximum capacity available"
                          : isPro()    ? "Expanded capacity options"
                                       : "Standard capacity range"),
                    Html.FROM_HTML_MODE_LEGACY
            )
    );
}

// ============================================================
// COLOR HELPERS — INTERNALS (HTML SAFE)
// ============================================================

private String log(String label, String value) {
    if (value == null || value.trim().isEmpty()) return "";
    return "<font color=\"#FFFFFF\"><b>• " + label + ":</b></font> " +
           "<font color=\"#00FF7F\">" + value + "</font><br>";
}

private String yes(boolean v) {
    return v ? "Yes" : null;
}

    // ============================
    // SERIES HELPERS (LOCKED)
    // ============================
    private boolean isPro() {
    return d != null && d.model != null &&
           d.model.toLowerCase().contains("pro")
           && !isProMax();
}

    private boolean isProMax() {
        return d != null && d.model != null &&
               (d.model.toLowerCase().contains("pro max")
                || d.model.toLowerCase().contains("max"));
    }

    // ============================================================
    // HELPERS
    // ============================================================
    
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

private void safeSet(TextView tv, String v) {
        if (tv == null || v == null) return;
        tv.setText(v);
    }

    private void hide(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void show(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }
}
