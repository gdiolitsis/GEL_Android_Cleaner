// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceInfoInternalActivity — CARBON COPY of DeviceInfoInternalActivity (APPLE HARDCODED)

package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.gel.cleaner.base.GELAutoActivityHook;
import com.gel.cleaner.iphone.AppleDeviceSpec;
import com.gel.cleaner.iphone.AppleSpecProvider;

import java.util.Locale;

public class AppleDeviceInfoInternalActivity extends GELAutoActivityHook {

    // =========================
    // UI
    // =========================
    private TextView txtTitleDevice;

    private LinearLayout headerSystem, headerCpu, headerGpu, headerRam, headerStorage,
            headerThermal, headerVulkan, headerConnectivity, headerAndroid;

    private TextView iconSystem, iconCpu, iconGpu, iconRam, iconStorage,
            iconThermal, iconVulkan, iconConnectivity, iconAndroid;

    private LinearLayout contentSystem, contentCpu, contentGpu, contentRam, contentStorage,
            contentThermal, contentVulkan, contentConnectivity, contentAndroid;

    private TextView txtSystemContent, txtCpuContent, txtGpuContent, txtRamContent, txtStorageContent,
            txtThermalContent, txtVulkanContent, txtConnectivityContent, txtAndroidContent;

    private TextView[] allContents;
    private TextView[] allIcons;

    // =========================
    // APPLE DEVICE
    // =========================
    private AppleDeviceSpec d;

    // =========================
    // COLORS (CARBON)
    // =========================
    private static final String NEON_GREEN = "#39FF14";

    // =========================
    // DIMEN
    // =========================
    private int dp(float v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        // =========================
        // LOAD SELECTED APPLE DEVICE
        // =========================
        d = AppleSpecProvider.getSelectedDevice(this);

        // =========================
        // BIND VIEWS (SAME IDS)
        // =========================
        txtTitleDevice = findViewById(R.id.txtTitleDevice);

        headerSystem = findViewById(R.id.headerSystem);
        headerCpu = findViewById(R.id.headerCpu);
        headerGpu = findViewById(R.id.headerGpu);
        headerRam = findViewById(R.id.headerRam);
        headerStorage = findViewById(R.id.headerStorage);
        headerThermal = findViewById(R.id.headerThermal);
        headerVulkan = findViewById(R.id.headerVulkan);
        headerConnectivity = findViewById(R.id.headerConnectivity);
        headerAndroid = findViewById(R.id.headerAndroid);

        iconSystem = findViewById(R.id.iconSystemToggle);
        iconCpu = findViewById(R.id.iconCpuToggle);
        iconGpu = findViewById(R.id.iconGpuToggle);
        iconRam = findViewById(R.id.iconRamToggle);
        iconStorage = findViewById(R.id.iconStorageToggle);
        iconThermal = findViewById(R.id.iconThermalToggle);
        iconVulkan = findViewById(R.id.iconVulkanToggle);
        iconConnectivity = findViewById(R.id.iconConnectivityToggle);
        iconAndroid = findViewById(R.id.iconAndroidToggle);

        // NOTE: στο original activity_device_info_internal τα content containers είναι τα parent των TextView.
        // Εδώ το κρατάμε όπως στο Android activity: toggle με βάση το TextView content (accordion).
        txtSystemContent = findViewById(R.id.txtSystemContent);
        txtCpuContent = findViewById(R.id.txtCpuContent);
        txtGpuContent = findViewById(R.id.txtGpuContent);
        txtRamContent = findViewById(R.id.txtRamContent);
        txtStorageContent = findViewById(R.id.txtStorageContent);
        txtThermalContent = findViewById(R.id.txtThermalContent);
        txtVulkanContent = findViewById(R.id.txtVulkanContent);
        txtConnectivityContent = findViewById(R.id.txtConnectivityContent);
        txtAndroidContent = findViewById(R.id.txtAndroidContent);

        // =========================
        // TITLE (APPLE)
        // =========================
        if (txtTitleDevice != null) {
            if (d != null && d.model != null && !d.model.trim().isEmpty()) {
                txtTitleDevice.setText("🍎 " + d.model);
            } else {
                txtTitleDevice.setText("🍎 Apple Device — Not Selected");
            }
            txtTitleDevice.setTextColor(Color.WHITE);
            txtTitleDevice.setTypeface(null, Typeface.BOLD);
            txtTitleDevice.setGravity(Gravity.CENTER);
        }

        // =========================
        // INITIAL STATE (ALL CLOSED)
        // =========================
        if (txtSystemContent != null) txtSystemContent.setVisibility(View.GONE);
        if (txtCpuContent != null) txtCpuContent.setVisibility(View.GONE);
        if (txtGpuContent != null) txtGpuContent.setVisibility(View.GONE);
        if (txtRamContent != null) txtRamContent.setVisibility(View.GONE);
        if (txtStorageContent != null) txtStorageContent.setVisibility(View.GONE);
        if (txtThermalContent != null) txtThermalContent.setVisibility(View.GONE);
        if (txtVulkanContent != null) txtVulkanContent.setVisibility(View.GONE);
        if (txtConnectivityContent != null) txtConnectivityContent.setVisibility(View.GONE);
        if (txtAndroidContent != null) txtAndroidContent.setVisibility(View.GONE);

        if (iconSystem != null) iconSystem.setText("＋");
        if (iconCpu != null) iconCpu.setText("＋");
        if (iconGpu != null) iconGpu.setText("＋");
        if (iconRam != null) iconRam.setText("＋");
        if (iconStorage != null) iconStorage.setText("＋");
        if (iconThermal != null) iconThermal.setText("＋");
        if (iconVulkan != null) iconVulkan.setText("＋");
        if (iconConnectivity != null) iconConnectivity.setText("＋");
        if (iconAndroid != null) iconAndroid.setText("＋");

        // =========================
        // ARRAYS (CARBON)
        // =========================
        allContents = new TextView[]{
                txtSystemContent,
                txtCpuContent,
                txtGpuContent,
                txtRamContent,
                txtStorageContent,
                txtThermalContent,
                txtVulkanContent,
                txtConnectivityContent,
                txtAndroidContent
        };

        allIcons = new TextView[]{
                iconSystem,
                iconCpu,
                iconGpu,
                iconRam,
                iconStorage,
                iconThermal,
                iconVulkan,
                iconConnectivity,
                iconAndroid
        };

        // =========================
        // SET SECTION TEXTS (APPLE HARDCODED)
        // =========================
        setNeonSectionText(txtSystemContent, buildSystemInfo());
        setNeonSectionText(txtCpuContent, buildCpuInfo());
        setNeonSectionText(txtGpuContent, buildGpuInfo());
        setNeonSectionText(txtRamContent, buildRamInfo());
        setNeonSectionText(txtStorageContent, buildStorageInfo());
        setNeonSectionText(txtThermalContent, buildThermalInternalReport());
        setNeonSectionText(txtVulkanContent, buildVulkanInfo());
        setNeonSectionText(txtConnectivityContent, buildConnectivityInfo());
        setNeonSectionText(txtAndroidContent, buildAndroidInfo()); // θα δείχνει "N/A" για Apple

        // =========================
        // ACCORDION (CARBON)
        // =========================
        setupSection(headerSystem, txtSystemContent, iconSystem, 0);
        setupSection(headerCpu, txtCpuContent, iconCpu, 1);
        setupSection(headerGpu, txtGpuContent, iconGpu, 2);
        setupSection(headerRam, txtRamContent, iconRam, 3);
        setupSection(headerStorage, txtStorageContent, iconStorage, 4);
        setupSection(headerThermal, txtThermalContent, iconThermal, 5);
        setupSection(headerVulkan, txtVulkanContent, iconVulkan, 6);
        setupSection(headerConnectivity, txtConnectivityContent, iconConnectivity, 7);
        setupSection(headerAndroid, txtAndroidContent, iconAndroid, 8);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // reload selection (αν άλλαξε από Device Declaration)
        d = AppleSpecProvider.getSelectedDevice(this);

        if (txtTitleDevice != null) {
            if (d != null && d.model != null && !d.model.trim().isEmpty()) {
                txtTitleDevice.setText("🍎 " + d.model);
            } else {
                txtTitleDevice.setText("🍎 Apple Device — Not Selected");
            }
        }

        setNeonSectionText(txtSystemContent, buildSystemInfo());
        setNeonSectionText(txtCpuContent, buildCpuInfo());
        setNeonSectionText(txtGpuContent, buildGpuInfo());
        setNeonSectionText(txtRamContent, buildRamInfo());
        setNeonSectionText(txtStorageContent, buildStorageInfo());
        setNeonSectionText(txtThermalContent, buildThermalInternalReport());
        setNeonSectionText(txtVulkanContent, buildVulkanInfo());
        setNeonSectionText(txtConnectivityContent, buildConnectivityInfo());
        setNeonSectionText(txtAndroidContent, buildAndroidInfo());
    }

    // ============================================================
    // SECTION TOGGLE (CARBON)
    // ============================================================
    private void setupSection(View header, TextView contentText, TextView icon, int index) {
        if (header == null || contentText == null || icon == null) return;

        header.setOnClickListener(v -> toggleSection(index));
    }

    private void toggleSection(int index) {

        // close all others
        if (allContents != null && allIcons != null) {
            for (int i = 0; i < allContents.length; i++) {
                if (i == index) continue;
                if (allContents[i] != null && allContents[i].getVisibility() == View.VISIBLE) {
                    animateCollapse(allContents[i]);
                }
                if (allIcons[i] != null) allIcons[i].setText("＋");
            }
        }

        // toggle selected
        TextView target = allContents[index];
        TextView icon = allIcons[index];

        if (target == null || icon == null) return;

        boolean isOpen = (target.getVisibility() == View.VISIBLE);
        if (isOpen) {
            animateCollapse(target);
            icon.setText("＋");
        } else {
            animateExpand(target);
            icon.setText("－");
        }
    }

    private void animateExpand(View v) {
        if (v == null) return;
        v.setAlpha(0f);
        v.setVisibility(View.VISIBLE);
        v.animate()
                .alpha(1f)
                .setDuration(180)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateCollapse(View v) {
        if (v == null) return;
        v.animate()
                .alpha(0f)
                .setDuration(160)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> v.setVisibility(View.GONE))
                .start();
    }

    // ============================================================
    // APPLE INTERNAL SECTIONS (HARDCODED VIA AppleDeviceSpec)
    // ============================================================
    private String buildSystemInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "Device", "Not selected"));
            sb.append(String.format(Locale.US, FMT, "Tip", "Use 🍎 Device Declaration"));
            return sb.toString();
        }

        sb.append(String.format(Locale.US, FMT, "Model", safe(d.model)));
        sb.append(String.format(Locale.US, FMT, "Release", safe(d.releaseYear)));
        sb.append(String.format(Locale.US, FMT, "Chip", safe(d.chip)));
        sb.append(String.format(Locale.US, FMT, "Architecture", safe(d.arch)));
        sb.append(String.format(Locale.US, FMT, "RAM", safe(d.ram)));

        // iOS version είναι δυναμικό στην πραγματικότητα → εδώ hardcoded δεν έχει νόημα.
        sb.append(String.format(Locale.US, FMT, "OS", "iOS/iPadOS (varies)"));

        return sb.toString();
    }

    private String buildCpuInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "CPU", "N/A"));
            return sb.toString();
        }

        sb.append(String.format(Locale.US, FMT, "Chip", safe(d.chip)));
        sb.append(String.format(Locale.US, FMT, "Arch", safe(d.arch)));

        // Αν έχεις πεδία για cores μέσα στο spec μελλοντικά, τα βάζουμε.
        sb.append(String.format(Locale.US, FMT, "CPU Cores", "See chip spec"));
        sb.append(String.format(Locale.US, FMT, "Process", "See chip spec"));

        return sb.toString();
    }

    private String buildGpuInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "GPU", "N/A"));
            return sb.toString();
        }

        // Αν έχεις d.gpu μέσα στο AppleDeviceSpec, βάλε το. Αλλιώς “Apple GPU (integrated)”
        String gpu = (hasField("gpu") ? safe(getFieldValue("gpu")) : "Apple GPU (integrated)");
        sb.append(String.format(Locale.US, FMT, "GPU", gpu));
        sb.append(String.format(Locale.US, FMT, "Metal", "Supported (varies)"));

        return sb.toString();
    }

    private String buildRamInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "RAM", "N/A"));
            return sb.toString();
        }

        sb.append(String.format(Locale.US, FMT, "RAM", safe(d.ram)));
        sb.append(String.format(Locale.US, FMT, "Type", "LPDDR (varies)"));

        return sb.toString();
    }

    private String buildStorageInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "Storage", "N/A"));
            return sb.toString();
        }

        // Hardcoded storage tiers είναι “varies”
        sb.append(String.format(Locale.US, FMT, "Storage", "Varies by configuration"));
        sb.append(String.format(Locale.US, FMT, "NVMe", "Apple internal flash"));

        return sb.toString();
    }

    private String buildThermalInternalReport() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "Thermal", "N/A"));
            return sb.toString();
        }

        // Δεν διαβάζουμε sensors (hardcoded mode)
        sb.append(String.format(Locale.US, FMT, "Cooling", "Passive (typical)"));
        sb.append(String.format(Locale.US, FMT, "Sensors", "Internal (not exposed)"));
        sb.append(String.format(Locale.US, FMT, "Note", "Hardcoded profile"));

        return sb.toString();
    }

    private String buildVulkanInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "Graphics", "N/A"));
            return sb.toString();
        }

        // Apple: Metal, όχι Vulkan native
        sb.append(String.format(Locale.US, FMT, "Vulkan", "No (Metal native)"));
        sb.append(String.format(Locale.US, FMT, "Metal", "Yes"));
        sb.append(String.format(Locale.US, FMT, "OpenGL ES", "Deprecated (varies)"));

        return sb.toString();
    }

    private String buildConnectivityInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        if (d == null) {
            sb.append(String.format(Locale.US, FMT, "Connectivity", "N/A"));
            return sb.toString();
        }

        sb.append(String.format(Locale.US, FMT, "Wi-Fi", safe(d.wifi)));
        sb.append(String.format(Locale.US, FMT, "GPS", safe(d.gps)));
        sb.append(String.format(Locale.US, FMT, "Modem", safe(d.modem)));

        return sb.toString();
    }

    private String buildAndroidInfo() {
        final String FMT = "%-14s : %s\n";
        StringBuilder sb = new StringBuilder();

        // κρατάμε το section για καρμπόν UI, αλλά δείχνουμε ότι δεν αφορά Apple
        sb.append(String.format(Locale.US, FMT, "Android", "N/A (Apple Mode)"));
        sb.append(String.format(Locale.US, FMT, "Note", "This section is Android-only"));
        return sb.toString();
    }

    // ============================================================
    // NEON (CARBON COPY)
    // ============================================================
    private void setNeonSectionText(TextView tv, String text) {
        if (tv == null) return;
        if (text == null) text = "";
        tv.setText(applyNeonToValues(text));
    }

    private CharSequence applyNeonToValues(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        String[] lines = text.split("\n", -1); // keep empty lines
        int offset = 0;
        boolean previousLabelOnly = false;

        for (String line : lines) {
            int len = line.length();
            if (len > 0) {
                int colonIdx = line.indexOf(':');
                if (colonIdx >= 0) {
                    // Label-only line (ends with ':')
                    if (colonIdx == len - 1) {
                        previousLabelOnly = true;
                    } else {
                        // Color from first non-space after ':' to end of line
                        int valueStart = offset + colonIdx + 1;
                        while (valueStart < offset + len &&
                                Character.isWhitespace(line.charAt(valueStart - offset))) {
                            valueStart++;
                        }
                        int valueEnd = offset + len;
                        if (valueStart < valueEnd) {
                            ssb.setSpan(
                                    new ForegroundColorSpan(Color.parseColor(NEON_GREEN)),
                                    valueStart,
                                    valueEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                        previousLabelOnly = false;
                    }
                } else if (previousLabelOnly) {
                    // Entire line is a value for the previous label-only line
                    int valueStart = offset;
                    int valueEnd = offset + len;
                    ssb.setSpan(
                            new ForegroundColorSpan(Color.parseColor(NEON_GREEN)),
                            valueStart,
                            valueEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    previousLabelOnly = false;
                } else {
                    previousLabelOnly = false;
                }
            } else {
                previousLabelOnly = false;
            }

            offset += len + 1; // +1 for '\n'
        }

        return ssb;
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String safe(Object v) {
        if (v == null) return "N/A";
        String s = String.valueOf(v);
        if (s.trim().isEmpty()) return "N/A";
        return s;
    }

    // Optional: graceful access if spec evolves (gpu field etc.)
    private boolean hasField(String name) {
        if (d == null) return false;
        try {
            return d.getClass().getField(name) != null;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private Object getFieldValue(String name) {
        if (d == null) return null;
        try {
            return d.getClass().getField(name).get(d);
        } catch (Throwable ignore) {
            return null;
        }
    }
}
