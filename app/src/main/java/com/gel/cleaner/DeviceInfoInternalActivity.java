// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoInternalActivity.java — GEL INTERNAL PRO v9.0
// Full Report + Soft Expand v3.0 + Neon Values + Root Fallback + Root-Extended Internals + Stealth Masking
// NOTE: Δουλεύω ΠΑΝΩ στο τελευταίο αρχείο σου — χωρίς αλλαγές σε UI / XML.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.LinkedHashMap;

public class DeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private static final String NEON_GREEN = "#39FF14";

    private boolean isRooted = false;

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager foldUI;

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

    foldUI = new GELFoldableUIManager(this);
    foldDetector = new GELFoldableDetector(this, this);

    TextView title = findViewById(R.id.txtTitleDevice);
    if (title != null) title.setText(getString(R.string.phone_info_internal));

    // CONTENT (ONLY IDs THAT EXIST IN CLEAN XML)
    TextView txtSystemContent       = findViewById(R.id.txtSystemContent);
    TextView txtAndroidContent      = findViewById(R.id.txtAndroidContent);
    TextView txtCpuContent          = findViewById(R.id.txtCpuContent);
    TextView txtGpuContent          = findViewById(R.id.txtGpuContent);
    TextView txtThermalContent      = findViewById(R.id.txtThermalContent);
    TextView txtVulkanContent       = findViewById(R.id.txtVulkanContent);
    TextView txtRamContent          = findViewById(R.id.txtRamContent);
    TextView txtStorageContent      = findViewById(R.id.txtStorageContent);
    TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent);

    // ICONS (ONLY IDs THAT EXIST IN CLEAN XML)
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
            txtStorageContent
    };

    allIcons = new TextView[]{
            iconSystem, iconAndroid, iconCpu, iconGpu, iconThermal,
            iconVulkan, iconRam, iconStorage
    };

    // ============================================================
    // CONTENT BUILD (values in neon green via spans)
    // ============================================================
    if (txtSystemContent != null)
        setNeonSectionText(txtSystemContent, buildSystemInfo());
    if (txtAndroidContent != null)
        setNeonSectionText(txtAndroidContent, buildAndroidInfo());
    if (txtCpuContent != null)
        setNeonSectionText(txtCpuContent, buildCpuInfo());
    if (txtGpuContent != null)
        setNeonSectionText(txtGpuContent, buildGpuInfo());
    if (txtThermalContent != null)
        setNeonSectionText(txtThermalContent, buildThermalInternalReport());
    if (txtVulkanContent != null)
        setNeonSectionText(txtVulkanContent, buildVulkanInfo());
    if (txtRamContent != null)
        setNeonSectionText(txtRamContent, buildRamInfo());
    if (txtStorageContent != null)
        setNeonSectionText(txtStorageContent, buildStorageInfo());
    
    // EXPANDERS (ONLY HEADERS THAT EXIST IN CLEAN XML)
    setupSection(findViewById(R.id.headerSystem), txtSystemContent, iconSystem);
    setupSection(findViewById(R.id.headerAndroid), txtAndroidContent, iconAndroid);
    setupSection(findViewById(R.id.headerCpu), txtCpuContent, iconCpu);
    setupSection(findViewById(R.id.headerGpu), txtGpuContent, iconGpu);
    setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);
    setupSection(findViewById(R.id.headerVulkan), txtVulkanContent, iconVulkan);
    setupSection(findViewById(R.id.headerRam), txtRamContent, iconRam);
    setupSection(findViewById(R.id.headerStorage), txtStorageContent, iconStorage);
}

@Override
protected void onResume() {
    super.onResume();
    if (foldDetector != null) foldDetector.start();
}

@Override
protected void onPause() {
    if (foldDetector != null) foldDetector.stop();
    super.onPause();
}

@Override
public void onPostureChanged(@NonNull Posture posture) {}

@Override
public void onScreenChanged(boolean isInner) {
    if (foldUI != null) foldUI.applyUI(isInner);
}

    // ============================================================
    // EXPANDER LOGIC WITH ANIMATION (GEL Expand Engine v3.0 — FIXED)
    // ============================================================

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView targetContent, TextView targetIcon) {

        // Close all other sections
        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];

            if (c == null || ic == null) continue;

            if (c != targetContent && c.getVisibility() == View.VISIBLE) {
                animateCollapse(c);
                ic.setText("＋");
            }
        }

        // Toggle only selected section
        if (targetContent.getVisibility() == View.VISIBLE) {
            animateCollapse(targetContent);
            targetIcon.setText("＋");
        } else {
            animateExpand(targetContent);
            targetIcon.setText("−");
        }
    }

    private void animateExpand(final View v) {

        // SAFE POST-MEASURE FIX — prevents auto-collapse on Android 11–14
        v.post(() -> {
            v.measure(
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            final int target = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);

            v.animate()
                    .alpha(1f)
                    .setDuration(160)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        v.getLayoutParams().height = target;
                        v.requestLayout();
                    })
                    .start();
        });
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        final int initial = v.getHeight();
        v.setAlpha(1f);

        v.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = initial;
                    v.setAlpha(1f);
                    v.requestLayout();
                })
                .start();
    }

    // ============================================================
    // NEON VALUE COLOR ENGINE (only values, not labels)
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
    // SECTION BUILDERS — FULL PRO + ROOT EXTENDED + STEALTH
    // ============================================================

    // ============================================================
    // System Info
    // ============================================================

    private String buildSystemInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Manufacturer : ").append(Build.MANUFACTURER).append("\n");
        sb.append("Brand        : ").append(Build.BRAND).append("\n");
        sb.append("Model        : ").append(Build.MODEL).append("\n");
        sb.append("Device       : ").append(Build.DEVICE).append("\n");
        sb.append("Product      : ").append(Build.PRODUCT).append("\n");
        sb.append("Hardware     : ").append(Build.HARDWARE).append("\n");
        sb.append("Board        : ").append(Build.BOARD).append("\n");
        sb.append("Bootloader   : ").append(Build.BOOTLOADER).append("\n\n");

        sb.append("=== System Fingerprint ===\n\n");
        sb.append(Build.FINGERPRINT).append("\n\n");
        
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable ignore) {
        }
        if (androidId != null) {
            sb.append("Android ID   : ").append(androidId).append("\n");
        }

        sb.append("Device Type  : ");
        if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            sb.append("Tablet\n");
        } else {
            sb.append("Phone\n");
        }

        String region = getProp("ro.product.locale.region");
        if (region != null && !region.isEmpty()) {
            sb.append("Region       : ").append(region).append("\n");
        }

        String vendor = getProp("ro.product.vendor.name");
        if (vendor != null && !vendor.isEmpty()) {
            sb.append("Vendor Name  : ").append(vendor).append("\n");
        }

        // Root-extended boot / verified state (props are readable even without su on many devices)
        String vbState = getProp("ro.boot.verifiedbootstate");
        if (vbState != null && !vbState.isEmpty()) {
            sb.append("VB State     : ").append(vbState).append("\n");
        }
        String vbDevice = getProp("ro.boot.vbmeta.device_state");
        if (vbDevice != null && !vbDevice.isEmpty()) {
            sb.append("VB Device    : ").append(vbDevice).append("\n");
        }
        String flashLock = getProp("ro.boot.flash.locked");
        if (flashLock != null && !flashLock.isEmpty()) {
            sb.append("Flash Lock   : ").append(flashLock).append("\n");
        }

        return sb.toString();
    }

// ============================================================
// Android Build Info
// ============================================================
private String buildAndroidInfo() {
    StringBuilder sb = new StringBuilder();

    // ------------------------------------------------------------
    // ANDROID CORE
    // ------------------------------------------------------------
    sb.append("Android        : ")
      .append(Build.VERSION.RELEASE)
      .append(" (SDK ")
      .append(Build.VERSION.SDK_INT)
      .append(")\n");

    if (Build.VERSION.SECURITY_PATCH != null) {
        sb.append("Security Patch : ")
          .append(Build.VERSION.SECURITY_PATCH)
          .append("\n");
    }

    sb.append("Build ID       : ").append(Build.ID).append("\n");
    sb.append("Build Type     : ").append(Build.TYPE).append("\n");
    sb.append("Build Tags     : ").append(Build.TAGS).append("\n");
    sb.append("Incremental    : ").append(Build.VERSION.INCREMENTAL).append("\n");

    // ------------------------------------------------------------
    // BASEBAND
    // ------------------------------------------------------------
    sb.append("\n=== Baseband ===\n\n");

    String baseband = Build.getRadioVersion();
    if (baseband != null && !baseband.isEmpty()) {
        sb.append("Release        : ")
          .append(baseband)
          .append("\n");
    }

    // ------------------------------------------------------------
    // VENDOR / OEM
    // ------------------------------------------------------------
    sb.append("\n=== Vendor Release ===\n\n");

    String miui = getProp("ro.miui.ui.version.name");
    if (miui != null && !miui.isEmpty()) {
        sb.append("MIUI           : ")
          .append(miui)
          .append("\n");
    } else if (Build.VERSION.BASE_OS != null && !Build.VERSION.BASE_OS.isEmpty()) {
        sb.append("Base OS        : ")
          .append(Build.VERSION.BASE_OS)
          .append("\n");
    }

    return sb.toString();
}

// ============================================================
// CPU Info
// ============================================================
private String buildCpuInfo() {

    StringBuilder sb = new StringBuilder();
    final String FMT = "%-10s : %s\n";   // 👈 ΤΟ ΜΥΣΤΙΚΟ ΤΗΣ ΣΤΟΙΧΙΣΗΣ

    // ------------------------------------------------------------------------
    // ABI
    // ------------------------------------------------------------------------
    StringBuilder abi = new StringBuilder();
    if (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0) {
        for (int i = 0; i < Build.SUPPORTED_ABIS.length; i++) {
            if (i > 0) abi.append(", ");
            abi.append(Build.SUPPORTED_ABIS[i]);
        }
    } else {
        abi.append(Build.CPU_ABI);
    }
    sb.append(String.format(Locale.US, FMT, "ABI", abi.toString()));

    // ------------------------------------------------------------------------
    // CPU CORES
    // ------------------------------------------------------------------------
    int cores = Runtime.getRuntime().availableProcessors();
    sb.append(String.format(Locale.US, FMT, "CPU Cores", cores));

    // ------------------------------------------------------------------------
    // /proc/cpuinfo (selected lines)
    // ------------------------------------------------------------------------
    String cpuinfo = readTextFile("/proc/cpuinfo", 32 * 1024);
    if (cpuinfo != null && !cpuinfo.isEmpty()) {
        String[] lines = cpuinfo.split("\n");
        for (String line : lines) {
            String low = line.toLowerCase(Locale.US);
            if (low.startsWith("processor")) {
                sb.append(String.format(Locale.US, FMT, "processor", line.split(":",2)[1].trim()));
            } else if (low.startsWith("model name")) {
                sb.append(String.format(Locale.US, FMT, "model name", line.split(":",2)[1].trim()));
            } else if (low.startsWith("hardware")) {
                sb.append(String.format(Locale.US, FMT, "hardware", line.split(":",2)[1].trim()));
            }
        }
    }

    // ------------------------------------------------------------------------
    // GOVERNOR
    // ------------------------------------------------------------------------
    String gov = readSysString("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
    if (gov != null && !gov.isEmpty()) {
        sb.append(String.format(Locale.US, FMT, "Governor", gov.trim()));
    }

    // ------------------------------------------------------------------------
    // FREQUENCIES
    // ------------------------------------------------------------------------
    long curFreq = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
    long minFreq = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
    long maxFreq = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");

    if (curFreq > 0 || minFreq > 0 || maxFreq > 0) {
        StringBuilder freq = new StringBuilder();
        if (curFreq > 0) freq.append("cur=").append(curFreq / 1000).append(" ");
        if (minFreq > 0) freq.append("min=").append(minFreq / 1000).append(" ");
        if (maxFreq > 0) freq.append("max=").append(maxFreq / 1000);
        sb.append(String.format(Locale.US, FMT, "Freq (MHz)", freq.toString().trim()));
    }

    // ------------------------------------------------------------------------
    // CLUSTER HINT
    // ------------------------------------------------------------------------
    String policy0 = readSysString("/sys/devices/system/cpu/cpufreq/policy0/cpuinfo_min_freq");
    String policy7 = readSysString("/sys/devices/system/cpu/cpufreq/policy7/cpuinfo_max_freq");
    if ((policy0 != null && !policy0.isEmpty()) ||
        (policy7 != null && !policy7.isEmpty())) {
        sb.append(String.format(Locale.US, FMT, "Cluste", "big.LITTLE detected"));
    }

    // ------------------------------------------------------------------------
    // CPU CHIP TEMP (estimated)
    // ------------------------------------------------------------------------
    Double socTemp = getSocTempCpuAverage();
    if (socTemp != null) {
        sb.append(String.format(
                Locale.US,
                FMT,
                "SPU CHIP",
                String.format(Locale.US, "%.1f°C (estimated)", socTemp)
        ));
    }

    // ------------------------------------------------------------------------
    // ROOT CPU DETAILS
    // ------------------------------------------------------------------------
    if (isRooted) {
        sb.append("\n[Root CPU tables]\n");
        boolean added = false;

        for (int i = 0; i < cores; i++) {
            String base = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/";
            long rCur = readSysLong(base + "scaling_cur_freq");
            long rMin = readSysLong(base + "cpuinfo_min_freq");
            long rMax = readSysLong(base + "cpuinfo_max_freq");

            if (rCur > 0 || rMin > 0 || rMax > 0) {
                StringBuilder row = new StringBuilder();
                if (rCur > 0) row.append("cur=").append(rCur / 1000).append("MHz ");
                if (rMin > 0) row.append("min=").append(rMin / 1000).append("MHz ");
                if (rMax > 0) row.append("max=").append(rMax / 1000).append("MHz");

                sb.append(String.format(Locale.US, FMT, "cpu" + i, row.toString().trim()));
                added = true;
            }

            String avail = readSysString(base + "scaling_available_frequencies");
            if (avail != null && !avail.isEmpty()) {
                sb.append(String.format(Locale.US, FMT, "cpu" + i + " avail", avail.trim()));
                added = true;
            }
        }

        if (!added) {
            sb.append("Root CPU details not exposed by current kernel.\n");
        }
    }

    return sb.toString();
}

    // ============================================================
    // Gpu Info
    // ============================================================

    private String buildGpuInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ConfigurationInfo ci = am.getDeviceConfigurationInfo();
                if (ci != null) {
                    sb.append("OpenGL ES     : ").append(ci.getGlEsVersion()).append("\n");
                }
            }
        } catch (Throwable ignore) {
        }

        String egl = getProp("ro.hardware.egl");
        if (egl != null && !egl.isEmpty()) {
            sb.append("EGL HW        : ").append(egl).append("\n");
        }

        String driver0 = getProp("ro.gfx.driver.0");
        if (driver0 != null && !driver0.isEmpty()) {
            sb.append("GPU Driver   : ").append(driver0).append("\n");
        }

        String perf = getProp("ro.gpu.uv");
        if (perf != null && !perf.isEmpty()) {
            sb.append("GPU Mode     : ").append(perf).append("\n");
        }

        if (sb.length() == 0) {
            sb.append("No GPU information available via system properties.\n");
        }

        // ROOT-EXTENDED GPU (KGSL / devfreq where available)
        boolean addedRootGpu = false;
        if (isRooted) {
            sb.append("\n[Root GPU tables]\n");
            try {
                long cur = readSysLong("/sys/class/kgsl/kgsl-3d0/devfreq/cur_freq");
                long min = readSysLong("/sys/class/kgsl/kgsl-3d0/devfreq/min_freq");
                long max = readSysLong("/sys/class/kgsl/kgsl-3d0/devfreq/max_freq");
                if (cur > 0 || min > 0 || max > 0) {
                    sb.append("Freq (MHz)   : ");
                    if (cur > 0) sb.append("cur=").append(cur / 1000000).append(" ");
                    if (min > 0) sb.append("min=").append(min / 1000000).append(" ");
                    if (max > 0) sb.append("max=").append(max / 1000000);
                    sb.append("\n");
                    addedRootGpu = true;
                }

                String avail = readSysString("/sys/class/kgsl/kgsl-3d0/devfreq/available_frequencies");
                if (avail != null && !avail.isEmpty()) {
                    sb.append("Avail Freq   : ").append(avail.trim()).append("\n");
                    addedRootGpu = true;
                }

                String busy = readSysString("/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage");
                if (busy != null && !busy.isEmpty()) {
                    sb.append("Busy GPU     : ").append(busy.trim()).append(" %\n");
                    addedRootGpu = true;
                }
            } catch (Throwable ignore) {
            }

            if (!addedRootGpu) {
                sb.append("Root GPU metrics not exposed by current driver.\n");
            }
        }

        return sb.toString();
    }

// ============================================================================
// THERMAL SENSORS — INTERNAL
// Human Readable • Internal-Only • Aggregated • GEL Edition
// ============================================================================
private String buildThermalInternalReport() {

    StringBuilder sb = new StringBuilder();

    sb.append("THERMAL SENSORS (INTERNAL)\n");
    sb.append("──────────────────────────\n");

    File thermalDir = new File("/sys/class/thermal");
    if (!thermalDir.exists() || !thermalDir.isDirectory()) {
        sb.append("Thermal sensors not available on this device.\n");
        return sb.toString();
    }

    File[] zones = thermalDir.listFiles((dir, name) -> name.startsWith("thermal_zone"));
    if (zones == null || zones.length == 0) {
        sb.append("No thermal zones detected.\n");
        return sb.toString();
    }

    Map<String, Float> maxTemps = new java.util.HashMap<>();

    // ------------------------------------------------------------------------
    // BASIC / HUMAN REPORT (ALL DEVICES)
    // ------------------------------------------------------------------------
    for (File zone : zones) {
        try {
            String type = readSysFile(zone, "type");
            String tempRaw = readSysFile(zone, "temp");
            if (type == null || tempRaw == null) continue;

            float tempC = Float.parseFloat(tempRaw.trim()) / 1000f;
            if (tempC <= -100f || tempC == 0f) continue;

            String label = mapThermalType(type);

            boolean isInternal =
                    label.contains("CPU") ||
                    label.equals("GPU") ||
                    label.equals("Battery") ||
                    label.contains("Backlight") ||
                    label.contains("DDR");

            if (!isInternal) continue;

            Float prev = maxTemps.get(label);
            if (prev == null || tempC > prev) {
                maxTemps.put(label, tempC);
            }

        } catch (Throwable ignore) {}
    }

    final String FMT = "%-18s : %5.1f°C  (%s)\n";
    String[] order = {
            "CPU Core",
            "CPU Cluster 0",
            "CPU Cluster 1",
            "GPU",
            "DDR Memory",
            "Battery",
            "Backlight"
    };

    for (String key : order) {
        Float t = maxTemps.get(key);
        if (t != null) {
            sb.append(String.format(
                    Locale.US,
                    FMT,
                    key,
                    t,
                    thermalState(t)
            ));
        }
    }

    // ------------------------------------------------------------------------
    // ROOT SECTION — REAL KERNEL DATA
    // ------------------------------------------------------------------------
    if (isRooted) {

        sb.append("\nAdvanced Thermal (Root)\n");
        sb.append("──────────────────────\n");

        // ---- Thermal Zones with Trip Points ----
        for (File zone : zones) {
            try {
                String type = readSysFile(zone, "type");
                String tempRaw = readSysFile(zone, "temp");
                if (type == null || tempRaw == null) continue;

                float tempC = Float.parseFloat(tempRaw.trim()) / 1000f;

                sb.append("\n")
                  .append(zone.getName())
                  .append(" [")
                  .append(type.trim())
                  .append("]\n");

                sb.append("  Current Temp : ")
                  .append(String.format(Locale.US, "%.1f°C", tempC))
                  .append("\n");

                // trip points
                for (int i = 0; i < 10; i++) {
                    String tp = readSysFile(zone, "trip_point_" + i + "_temp");
                    String tpType = readSysFile(zone, "trip_point_" + i + "_type");
                    if (tp == null || tpType == null) break;

                    float tpC = Float.parseFloat(tp.trim()) / 1000f;
                    sb.append("  Trip ")
                      .append(i)
                      .append(" (")
                      .append(tpType.trim())
                      .append(") : ")
                      .append(String.format(Locale.US, "%.1f°C", tpC))
                      .append("\n");
                }

            } catch (Throwable ignore) {}
        }

        // ---- Cooling Devices ----
        File[] cooling =
                thermalDir.listFiles((d, n) -> n.startsWith("cooling_device"));

        if (cooling != null && cooling.length > 0) {
            sb.append("\nCooling Devices\n");
            sb.append("────────────────\n");

            for (File cd : cooling) {
                try {
                    String type = readSysFile(cd, "type");
                    String cur = readSysFile(cd, "cur_state");
                    String max = readSysFile(cd, "max_state");

                    sb.append(cd.getName());
                    if (type != null) sb.append(" [").append(type.trim()).append("]");
                    sb.append("\n");

                    if (cur != null && max != null) {
                    sb.append("  State : ")
                      .append(cur.trim())
                      .append(" / ")
                      .append(max.trim())
                      .append("\n");
                }

            } catch (Throwable ignore) {}
        }
    }

} else {
    // --------------------------------------------------------------------
    // ADVANCED ACCESS (INFO ONLY)
    // --------------------------------------------------------------------
    sb.append("\nAdvanced Info: For detailed thermal and cooling information, requires root access\n");
}

return sb.toString();
}

    // ============================================================
    // Vulkan Info
    // ============================================================

    private String buildVulkanInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            boolean hasLevel = getPackageManager().hasSystemFeature(
                    "android.hardware.vulkan.level");
            boolean hasVersion = getPackageManager().hasSystemFeature(
                    "android.hardware.vulkan.version");
            sb.append("Feature Level : ").append(hasLevel ? "Yes" : "No").append("\n");
            sb.append("Feature Vers  : ").append(hasVersion ? "Yes" : "No").append("\n");
        } catch (Throwable ignore) {
        }

        String hw = getProp("ro.hardware.vulkan");
        if (hw != null && !hw.isEmpty()) {
            sb.append("Vulkan HW     : ").append(hw).append("\n");
        }

        String enable = getProp("ro.vulkan.enable");
        if (enable != null && !enable.isEmpty()) {
            sb.append("Vulkan Enable : ").append(enable).append("\n");
        }

        String layers = getProp("debug.vulkan.layers");
        if (layers != null && !layers.isEmpty()) {
            sb.append("Debug Layers  : ").append(layers).append("\n");
        }

        if (sb.length() == 0) {
            sb.append("No Vulkan information available.\n");
        }

        return sb.toString();
    }

// ============================================================
// RAM Info
// ============================================================
private String buildRamInfo() {
    StringBuilder sb = new StringBuilder();

    // ------------------------------------------------------------
    // RAM Info (Framework) — LEAVE AS IS (MB)
    // ------------------------------------------------------------
    try {
        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if (am != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long totalMb = mi.totalMem / (1024 * 1024);
            long availMb = mi.availMem / (1024 * 1024);
            long usedMb  = totalMb - availMb;

            sb.append(padRight("Total RAM", 14)).append(": ")
              .append(totalMb).append(" MB\n");
            sb.append(padRight("Used RAM", 14)).append(": ")
              .append(usedMb).append(" MB\n");
            sb.append(padRight("Free RAM", 14)).append(": ")
              .append(availMb).append(" MB\n");
            sb.append(padRight("Low Memory", 14)).append(": ")
              .append(mi.lowMemory ? "Yes" : "No").append("\n");
            sb.append(padRight("Threshold", 14)).append(": ")
              .append(mi.threshold / (1024 * 1024)).append(" MB\n");
        }
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // /proc/meminfo (core) — CONVERT TO MB (Buffers stays kB)
    // ------------------------------------------------------------
    String meminfo = readTextFile("/proc/meminfo", 8 * 1024);
    if (meminfo != null && !meminfo.isEmpty()) {

        sb.append("\n/proc/meminfo (core):\n\n");

        String[] lines = meminfo.split("\n");
        for (String line : lines) {

            if (line.startsWith("MemTotal:")
                    || line.startsWith("MemFree:")
                    || line.startsWith("Cached:")
                    || line.startsWith("Active:")
                    || line.startsWith("Inactive:")
                    || line.startsWith("SwapTotal:")
                    || line.startsWith("SwapFree:")) {

                String[] parts = line.split(":");
                if (parts.length == 2) {
                    long kb = parseKb(parts[1]);
                    sb.append(padRight(parts[0], 14))
                      .append(": ")
                      .append(kb / 1024)
                      .append(" MB\n");
                }
            }

            // Buffers — stay in kB
            if (line.startsWith("Buffers:")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    sb.append(padRight(parts[0], 14))
                      .append(": ")
                      .append(parts[1].trim())
                      .append("\n");
                }
            }
        }
    }

    // ------------------------------------------------------------
    // ZRAM (advanced)
    // ------------------------------------------------------------
    if (!isRooted) {
        sb.append(padRight("ZRAM Details", 14))
          .append(": Requires Root access\n");
    }

    return sb.toString();
}

// ------------------------------------------------------------
// Helper
// ------------------------------------------------------------
private long parseKb(String raw) {
    try {
        return Long.parseLong(raw.replaceAll("[^0-9]", ""));
    } catch (Throwable t) {
        return 0;
    }
}
 
// ============================================================
// STORAGE Info
// ============================================================
private String buildStorageInfo() {
    StringBuilder sb = new StringBuilder();

    try {
        File internal = Environment.getDataDirectory();
        appendStorageBlock(sb, "Internal", internal);

        File ext = Environment.getExternalStorageDirectory();
        if (ext != null && ext.exists()) {
            appendStorageBlock(sb, "External (primary)", ext);
        }
    } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // CORE MOUNTS
    // ------------------------------------------------------------
    String mounts = readTextFile("/proc/mounts", 32 * 1024);
    if (mounts != null && !mounts.isEmpty()) {

        sb.append("\n=== Core Mounts ===\n\n");

        String[] lines = mounts.split("\n");
        String[] interesting = {
                "/", "/system", "/vendor", "/product",
                "/data", "/cache", "/metadata"
        };

        for (String line : lines) {
            String[] parts = line.split("\\s+");
            if (parts.length < 3) continue;

            String mountPoint = parts[1];
            boolean hit = false;
            for (String it : interesting) {
                if (mountPoint.equals(it)) {
                    hit = true;
                    break;
                }
            }

            if (hit) {
                sb.append("  ")
                  .append(padRight(mountPoint, 10))
                  .append(": ")
                  .append(parts[2])          // fs type
                  .append(" (")
                  .append(parts[0])          // device
                  .append(")\n");
            }
        }

    } else {
        sb.append("\n=== Core Mounts ===\n\n");
        sb.append("  Not exposed by this device.\n");
    }

    // ------------------------------------------------------------
    // PARTITIONS SNAPSHOT
    // ------------------------------------------------------------
    String parts = readTextFile("/proc/partitions", 8 * 1024);
    if (parts != null && !parts.isEmpty()) {
        sb.append("\n=== Partitions ===\n\n");
        sb.append(parts.trim()).append("\n");
    } else {
        sb.append("\n=== Partitions ===\n\n");
        sb.append("  Not exposed by this device.\n");
    }

    if (sb.length() == 0) {
        sb.append("Unable to read storage information.\n");
    }

    return sb.toString();
}

private void appendStorageBlock(StringBuilder sb, String label, File path) {
    try {
        StatFs stat = new StatFs(path.getAbsolutePath());

        long blockSize, totalBlocks, availBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize   = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize   = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availBlocks = stat.getAvailableBlocks();
        }

        long totalBytes = blockSize * totalBlocks;
        long availBytes = blockSize * availBlocks;
        long usedBytes  = totalBytes - availBytes;

        long totalGb = totalBytes / (1024 * 1024 * 1024);
        long usedGb  = usedBytes  / (1024 * 1024 * 1024);
        long freeGb  = availBytes / (1024 * 1024 * 1024);

        sb.append(label).append(":\n");
        sb.append("  ").append(padRight("Path", 10))
          .append(": ").append(path.getAbsolutePath()).append("\n");
        sb.append("  ").append(padRight("Total", 10))
          .append(": ").append(totalGb).append(" GB\n");
        sb.append("  ").append(padRight("Used", 10))
          .append(": ").append(usedGb).append(" GB\n");
        sb.append("  ").append(padRight("Free", 10))
          .append(": ").append(freeGb).append(" GB\n\n");

    } catch (Throwable ignore) {}
}

// ============================================================
// SoC Temperature — CPU average (non-root, safe)
// ============================================================
private Double getSocTempCpuAverage() {
    try {
        File dir = new File("/sys/class/thermal");
        if (!dir.exists() || !dir.isDirectory()) return null;

        File[] zones = dir.listFiles();
        if (zones == null) return null;

        double sum = 0;
        int count = 0;

        for (File z : zones) {
            if (!z.getName().startsWith("thermal_zone")) continue;

            String type = readSysString(z.getAbsolutePath() + "/type");
            if (type == null) continue;

            String low = type.toLowerCase(java.util.Locale.US);
            if (!low.contains("cpu")) continue;

            long t = readSysLong(z.getAbsolutePath() + "/temp");
            if (t <= 0) continue;

            double c = (t > 1000) ? t / 1000.0 : t / 10.0;
            sum += c;
            count++;
        }

        if (count == 0) return null;
        return sum / count;

    } catch (Throwable ignore) {
        return null;
    }
}

// ============================================================
// THERMAL HELPERS — INTERNAL
// ============================================================

private String readSysFile(File base, String name) {
    if (base == null) return null;
    File f = new File(base, name);
    if (!f.exists()) return null;

    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        return line != null ? line.trim() : null;
    } catch (Throwable ignore) {
        return null;
    } finally {
        try {
            if (br != null) br.close();
        } catch (Exception ignored) {}
    }
}

private String thermalState(float tempC) {
    if (tempC < 30f) return "COOL";
    if (tempC < 45f) return "NORMAL";
    if (tempC < 60f) return "WARM";
    if (tempC < 75f) return "HOT";
    return "CRITICAL";
}

private String mapThermalType(String type) {

    if (type == null) return "";

    String t = type.toLowerCase(Locale.US);

    // Battery
    if (t.contains("battery_therm") || t.contains("batt_therm"))
        return "Battery Shell";
    if (t.contains("battery"))
        return "Battery";

    // CPU
    if (t.matches(".*cpu[-_]?0.*"))
        return "CPU Cluster 0";
    if (t.matches(".*cpu[-_]?1.*"))
        return "CPU Cluster 1";
    if (t.contains("cpu"))
        return "CPU Core";

    // Main silicon
    if (t.contains("gpu"))
        return "GPU";
    if (t.contains("soc"))
        return "SoC";

    // Surface / internal
    if (t.contains("skin"))
        return "Device Skin";
    if (t.contains("backlight"))
        return "Backlight";

    // Memory
    if (t.contains("ddr"))
        return "DDR Memory";
    if (t.contains("mem"))
        return "Memory";

    // fallback
    return type;
}

// ============================================================
// GENERIC HELPERS (NON-ROOT)
// ============================================================

private String readTextFile(String path, int maxLen) {
    BufferedReader br = null;
    try {
        File f = new File(path);
        if (!f.exists()) return null;

        br = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int read;

        while ((read = br.read(buf)) > 0 && sb.length() < maxLen) {
            sb.append(buf, 0, read);
        }
        return sb.toString();

    } catch (Throwable ignore) {
        return null;

    } finally {
        try {
            if (br != null) br.close();
        } catch (Exception ignored) {}
    }
}

private String readSysString(String path) {
    BufferedReader br = null;
    try {
        File f = new File(path);
        if (!f.exists()) return null;

        br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        return line != null ? line.trim() : null;

    } catch (Throwable ignore) {
        return null;

    } finally {
        try {
            if (br != null) br.close();
        } catch (Exception ignored) {}
    }
}

private long readSysLong(String path) {
    String s = readSysString(path);
    if (s == null || s.isEmpty()) return -1;
    try {
        return Long.parseLong(s);
    } catch (Throwable ignore) {
        return -1;
    }
}

private String getProp(String key) {
    try {
        Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
        BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        return line != null ? line.trim() : "";
    } catch (Exception e) {
        return "";
    }
}

private String describeWifiBand(int freq) {
    if (freq >= 2400 && freq < 2500) return "2.4 GHz";
    if (freq >= 4900 && freq < 5900) return "5 GHz";
    if (freq >= 5925 && freq < 7125) return "6 GHz";
    return "Unknown";
}

private String describeNetworkType(int type) {
    switch (type) {
        case TelephonyManager.NETWORK_TYPE_GPRS:   return "2G (GPRS)";
        case TelephonyManager.NETWORK_TYPE_EDGE:   return "2G (EDGE)";
        case TelephonyManager.NETWORK_TYPE_UMTS:   return "3G (UMTS)";
        case TelephonyManager.NETWORK_TYPE_HSDPA:  return "3G (HSDPA)";
        case TelephonyManager.NETWORK_TYPE_HSUPA:  return "3G (HSUPA)";
        case TelephonyManager.NETWORK_TYPE_HSPA:   return "3G (HSPA)";
        case TelephonyManager.NETWORK_TYPE_LTE:    return "4G (LTE)";
        case TelephonyManager.NETWORK_TYPE_NR:     return "5G (NR)";
        default: return "Unknown";
    }
}

private String padRight(String s, int n) {
    if (s == null) s = "";
    if (s.length() >= n) return s;
    StringBuilder sb = new StringBuilder(s);
    while (sb.length() < n) sb.append(' ');
    return sb.toString();
}
}
