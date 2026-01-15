// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceInfoInternalActivity.java — APPLE INTERNAL PRO v1.0
// Carbon copy logic of DeviceInfoInternalActivity (ANDROID)
// All data HARD-CODED from AppleSpecs (NO runtime probing)

package com.gel.cleaner.iphone;

import com.gel.cleaner.*;
import com.gel.cleaner.base.*;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class AppleDeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private TextView[] allContents;
    private TextView[] allIcons;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        // ---------------- TITLE ----------------
        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null)
            title.setText("Apple Device — Internal Info");

        // ---------------- CONTENT ----------------
        TextView txtSystemContent       = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent      = findViewById(R.id.txtAndroidContent);   // θα δείχνει iOS εδώ
        TextView txtCpuContent          = findViewById(R.id.txtCpuContent);
        TextView txtGpuContent          = findViewById(R.id.txtGpuContent);
        TextView txtThermalContent      = findViewById(R.id.txtThermalContent);
        TextView txtVulkanContent       = findViewById(R.id.txtVulkanContent);    // θα δείχνει Metal εδώ
        TextView txtRamContent          = findViewById(R.id.txtRamContent);
        TextView txtStorageContent      = findViewById(R.id.txtStorageContent);
        TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent);

        // ---------------- ICONS ----------------
        TextView iconSystem       = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid      = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu          = findViewById(R.id.iconCpuToggle);
        TextView iconGpu          = findViewById(R.id.iconGpuToggle);
        TextView iconThermal      = findViewById(R.id.iconThermalToggle);
        TextView iconVulkan       = findViewById(R.id.iconVulkanToggle);
        TextView iconRam          = findViewById(R.id.iconRamToggle);
        TextView iconStorage      = findViewById(R.id.iconStorageToggle);
        TextView iconConnectivity = findViewById(R.id.iconConnectivityToggle);

        allContents = new TextView[]{
                txtSystemContent, txtAndroidContent, txtCpuContent, txtGpuContent,
                txtThermalContent, txtVulkanContent, txtRamContent,
                txtStorageContent, txtConnectivityContent
        };

        allIcons = new TextView[]{
                iconSystem, iconAndroid, iconCpu, iconGpu,
                iconThermal, iconVulkan, iconRam,
                iconStorage, iconConnectivity
        };

        // ---------------- FOLD ENGINE ----------------
        setupSection(findViewById(R.id.headerSystem),       txtSystemContent,       iconSystem);
        setupSection(findViewById(R.id.headerAndroid),      txtAndroidContent,      iconAndroid);
        setupSection(findViewById(R.id.headerCpu),          txtCpuContent,          iconCpu);
        setupSection(findViewById(R.id.headerGpu),          txtGpuContent,          iconGpu);
        setupSection(findViewById(R.id.headerThermal),      txtThermalContent,      iconThermal);
        setupSection(findViewById(R.id.headerVulkan),       txtVulkanContent,       iconVulkan);
        setupSection(findViewById(R.id.headerRam),          txtRamContent,          iconRam);
        setupSection(findViewById(R.id.headerStorage),      txtStorageContent,      iconStorage);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);

        // ---------------- LOAD APPLE DEVICE ----------------
        String model = AppleSelectionStore.getSelectedModel(this);
        AppleDeviceSpec d = AppleSpecProvider.get(model);

        // ---------------- FILL DATA ----------------
        set(txtSystemContent,        buildSystemInfo(d));
        set(txtAndroidContent,       buildIosInfo(d));
        set(txtCpuContent,           buildCpuInfo(d));
        set(txtGpuContent,           buildGpuInfo(d));
        set(txtThermalContent,       buildThermalInfo(d));
        set(txtVulkanContent,        buildMetalInfo(d));
        set(txtRamContent,           buildRamInfo(d));
        set(txtStorageContent,       buildStorageInfo(d));
        set(txtConnectivityContent,  buildConnectivityInfo(d));
    }

    // ============================================================
    // SECTION ENGINE — ίδιο με Android
    // ============================================================
    private void setupSection(android.view.View header,
                              android.view.View content,
                              TextView icon) {

        if (header == null || content == null || icon == null)
            return;

        content.setVisibility(android.view.View.GONE);
        icon.setText("+");

        header.setOnClickListener(v -> {

            boolean isOpen = (content.getVisibility() == android.view.View.VISIBLE);

            // κλείσε όλα
            if (allContents != null && allIcons != null) {
                for (int i = 0; i < allContents.length; i++) {
                    if (allContents[i] != null)
                        allContents[i].setVisibility(android.view.View.GONE);
                    if (allIcons[i] != null)
                        allIcons[i].setText("+");
                }
            }

            // άνοιξε μόνο αυτό
            if (!isOpen) {
                content.setVisibility(android.view.View.VISIBLE);
                icon.setText("-");
            }
        });
    }

    // ============================================================
    // APPLY GEL COLORS (ίδιο μοτίβο)
    // ============================================================
    private void set(TextView t, String txt) {
        if (t == null) return;
        applyNeonValues(t, txt);
    }

    // Χρησιμοποιούμε ΤΟ ΙΔΙΟ helper που έχεις στο Android activity
    private void applyNeonValues(TextView tv, String text) {
        GELTextColorizer.apply(tv, text);
    }

    // ============================================================
    // BUILDERS — HARD CODED από AppleSpecs
    // ============================================================
    private String buildSystemInfo(AppleDeviceSpec d) {
        if (d == null) return "❌ No Apple device selected";

        return ""
                + "Model        : " + d.model + "\n"
                + "Series       : " + d.series + "\n"
                + "Release Year : " + d.releaseYear + "\n"
                + "Architecture : " + d.arch + "\n"
                + "SoC          : " + d.chip + "\n";
    }

    private String buildIosInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "OS Family    : iOS / iPadOS\n"
                + "Initial OS   : " + d.initialOS + "\n"
                + "Last Support : " + d.maxOS + "\n";
    }

    private String buildCpuInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "Chip         : " + d.chip + "\n"
                + "CPU Cores    : " + d.cpuCores + "\n"
                + "Process      : " + d.process + "\n";
    }

    private String buildGpuInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "GPU          : " + d.gpu + "\n"
                + "GPU Cores    : " + d.gpuCores + "\n";
    }

    private String buildThermalInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "Cooling      : Passive\n"
                + "Thermal Class: " + d.thermalClass + "\n";
    }

    private String buildMetalInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "Graphics API : Metal\n"
                + "Metal Level  : " + d.metalLevel + "\n";
    }

    private String buildRamInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "RAM Size     : " + d.ram + "\n"
                + "RAM Type     : " + d.ramType + "\n";
    }

    private String buildStorageInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "Storage      : " + d.storageOptions + "\n"
                + "Type         : NVMe\n";
    }

    private String buildConnectivityInfo(AppleDeviceSpec d) {
        if (d == null) return "";

        return ""
                + "Wi-Fi        : " + d.wifi + "\n"
                + "Bluetooth    : " + d.bluetooth + "\n"
                + "Cellular     : " + d.modem + "\n";
    }
}
