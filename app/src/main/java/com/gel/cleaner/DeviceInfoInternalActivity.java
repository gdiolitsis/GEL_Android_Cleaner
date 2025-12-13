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

        // CONTENT
        TextView txtSystemContent           = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent          = findViewById(R.id.txtAndroidContent);
        TextView txtCpuContent              = findViewById(R.id.txtCpuContent);
        TextView txtGpuContent              = findViewById(R.id.txtGpuContent);
        TextView txtThermalContent          = findViewById(R.id.txtThermalContent);
        TextView txtThermalZonesContent     = findViewById(R.id.txtThermalZonesContent);
        TextView txtVulkanContent           = findViewById(R.id.txtVulkanContent);
        TextView txtThermalProfilesContent  = findViewById(R.id.txtThermalProfilesContent);
        TextView txtFpsGovernorContent      = findViewById(R.id.txtFpsGovernorContent);
        TextView txtRamContent              = findViewById(R.id.txtRamContent);
        TextView txtStorageContent          = findViewById(R.id.txtStorageContent);
        TextView txtScreenContent           = findViewById(R.id.txtScreenContent);
        TextView txtConnectivityContent     = findViewById(R.id.txtConnectivityContent);
        TextView txtRootContent             = findViewById(R.id.txtRootContent);

        // ICONS
        TextView iconSystem           = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid          = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu              = findViewById(R.id.iconCpuToggle);
        TextView iconGpu              = findViewById(R.id.iconGpuToggle);
        TextView iconThermal          = findViewById(R.id.iconThermalToggle);
        TextView iconThermalZones     = findViewById(R.id.iconThermalZonesToggle);
        TextView iconVulkan           = findViewById(R.id.iconVulkanToggle);
        TextView iconThermalProfiles  = findViewById(R.id.iconThermalProfilesToggle);
        TextView iconFpsGovernor      = findViewById(R.id.iconFpsGovernorToggle);
        TextView iconRam              = findViewById(R.id.iconRamToggle);
        TextView iconStorage          = findViewById(R.id.iconStorageToggle);
        TextView iconScreen           = findViewById(R.id.iconScreenToggle);
        TextView iconConnectivity     = findViewById(R.id.iconConnectivityToggle);
        TextView iconRoot             = findViewById(R.id.iconRootToggle);

        allContents = new TextView[]{
                txtSystemContent, txtAndroidContent, txtCpuContent, txtGpuContent,
                txtThermalContent, txtThermalZonesContent, txtVulkanContent,
                txtThermalProfilesContent, txtFpsGovernorContent, txtRamContent,
                txtStorageContent, txtScreenContent, txtConnectivityContent,
                txtRootContent
        };

        allIcons = new TextView[]{
                iconSystem, iconAndroid, iconCpu, iconGpu, iconThermal, iconThermalZones,
                iconVulkan, iconThermalProfiles, iconFpsGovernor, iconRam,
                iconStorage, iconScreen, iconConnectivity, iconRoot
        };

        isRooted = isDeviceRooted();

        // ============================================================
        // FULL PRO CONTENT BUILD (values in neon green via spans)
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
            setNeonSectionText(txtThermalContent, buildThermalSensorsInfo());
        if (txtThermalZonesContent != null)
            setNeonSectionText(txtThermalZonesContent, buildThermalZonesInfo());
        if (txtVulkanContent != null)
            setNeonSectionText(txtVulkanContent, buildVulkanInfo());
        if (txtThermalProfilesContent != null)
            setNeonSectionText(txtThermalProfilesContent, buildThermalProfilesInfo());
        if (txtFpsGovernorContent != null)
            setNeonSectionText(txtFpsGovernorContent, buildFpsGovernorInfo());
        if (txtRamContent != null)
            setNeonSectionText(txtRamContent, buildRamInfo());
        if (txtStorageContent != null)
            setNeonSectionText(txtStorageContent, buildStorageInfo());
        if (txtScreenContent != null)
            setNeonSectionText(txtScreenContent, buildScreenInfo());
        if (txtConnectivityContent != null)
            setNeonSectionText(txtConnectivityContent, buildConnectivityInfo());
        if (txtRootContent != null)
            setNeonSectionText(txtRootContent, buildRootInfo());

        // EXPANDERS
        setupSection(findViewById(R.id.headerSystem), txtSystemContent, iconSystem);
        setupSection(findViewById(R.id.headerAndroid), txtAndroidContent, iconAndroid);
        setupSection(findViewById(R.id.headerCpu), txtCpuContent, iconCpu);
        setupSection(findViewById(R.id.headerGpu), txtGpuContent, iconGpu);
        setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);
        setupSection(findViewById(R.id.headerThermalZones), txtThermalZonesContent, iconThermalZones);
        setupSection(findViewById(R.id.headerVulkan), txtVulkanContent, iconVulkan);
        setupSection(findViewById(R.id.headerThermalProfiles), txtThermalProfilesContent, iconThermalProfiles);
        setupSection(findViewById(R.id.headerFpsGovernor), txtFpsGovernorContent, iconFpsGovernor);
        setupSection(findViewById(R.id.headerRam), txtRamContent, iconRam);
        setupSection(findViewById(R.id.headerStorage), txtStorageContent, iconStorage);
        setupSection(findViewById(R.id.headerScreen), txtScreenContent, iconScreen);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);
        setupSection(findViewById(R.id.headerRoot), txtRootContent, iconRoot);
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

        sb.append("=== Fingerprint ===\n").append(Build.FINGERPRINT).append("\n\n");
        sb.append("\n"); // empty line for visual separation    
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
    // Android Info
    // ============================================================

    private String buildAndroidInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Android      : ").append(Build.VERSION.RELEASE)
                .append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append("Security Pch : ").append(Build.VERSION.SECURITY_PATCH).append("\n");
        }

        sb.append("Build ID     : ").append(Build.ID).append("\n");
        sb.append("Build Type   : ").append(Build.TYPE).append("\n");
        sb.append("Build Tags   : ").append(Build.TAGS).append("\n\n");

        String incr = Build.VERSION.INCREMENTAL;
        if (incr != null) {
            sb.append("Incremental  : ").append(incr).append("\n");
        }

        String baseband = getProp("gsm.version.baseband");
        if (baseband != null && !baseband.isEmpty()) {
            sb.append("Baseband     : ").append(baseband).append("\n");
        }

        String vendorRel = getProp("ro.vendor.build.version.release");
        if (vendorRel != null && !vendorRel.isEmpty()) {
            sb.append("Vendor Rel   : ").append(vendorRel).append("\n");
        }

        String miui = getProp("ro.miui.ui.version.name");
        if (miui != null && !miui.isEmpty()) {
            sb.append("MIUI         : ").append(miui).append("\n");
        }

        String hyper = getProp("ro.mi.os.version.name");
        if (hyper != null && !hyper.isEmpty()) {
            sb.append("HyperOS      : ").append(hyper).append("\n");
        }

        // Small root-extended flavour: SELinux props
        String seEnforce = getProp("ro.boot.selinux");
        if (seEnforce != null && !seEnforce.isEmpty()) {
            sb.append("SELinux Boot : ").append(seEnforce).append("\n");
        }

        return sb.toString();
    }

    private String buildCpuInfo() {
        StringBuilder sb = new StringBuilder();

        // ABI
        sb.append("ABI          : ");
        if (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0) {
            for (int i = 0; i < Build.SUPPORTED_ABIS.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(Build.SUPPORTED_ABIS[i]);
            }
        } else {
            sb.append(Build.CPU_ABI);
        }
        sb.append("\n");

        int cores = Runtime.getRuntime().availableProcessors();
        sb.append("CPU Cores : ").append(cores).append("\n");

        // /proc/cpuinfo key lines
        String cpuinfo = readTextFile("/proc/cpuinfo", 32 * 1024);
        if (cpuinfo != null && !cpuinfo.isEmpty()) {
            String[] lines = cpuinfo.split("\n");
            for (String line : lines) {
                String low = line.toLowerCase();
                if (low.startsWith("hardware")) {
                    sb.append(line.trim()).append("\n");
                } else if (low.startsWith("model name")) {
                    sb.append(line.trim()).append("\n");
                } else if (low.startsWith("processor")) {
                    sb.append(line.trim()).append("\n");
                }
            }
        }

        // Governor & frequency (if exposed)
        String gov = readSysString("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        if (gov != null && !gov.isEmpty()) {
            sb.append("Governor     : ").append(gov.trim()).append("\n");
        }

        long curFreq = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        long minFreq = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        long maxFreq = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");

        if (curFreq > 0 || minFreq > 0 || maxFreq > 0) {
            sb.append("Freq (MHz)   : ");
            if (curFreq > 0) sb.append("cur=").append(curFreq / 1000).append(" ");
            if (minFreq > 0) sb.append("min=").append(minFreq / 1000).append(" ");
            if (maxFreq > 0) sb.append("max=").append(maxFreq / 1000);
            sb.append("\n");
        }

        // big.LITTLE hint from clusters (if exist)
        String policy0 = readSysString("/sys/devices/system/cpu/cpufreq/policy0/cpuinfo_min_freq");
        String policy7 = readSysString("/sys/devices/system/cpu/cpufreq/policy7/cpuinfo_max_freq");
        if ((policy0 != null && !policy0.isEmpty()) ||
                (policy7 != null && !policy7.isEmpty())) {
            sb.append("Cluster Hint : big.LITTLE detected\n");
        }

        // ROOT-EXTENDED CPU DETAILS
        boolean addedRootCpu = false;
        if (isRooted) {
            sb.append("\n[Root CPU tables]\n");
            try {
                for (int i = 0; i < cores; i++) {
                    String base = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/";
                    long rMin = readSysLong(base + "cpuinfo_min_freq");
                    long rMax = readSysLong(base + "cpuinfo_max_freq");
                    long rCur = readSysLong(base + "scaling_cur_freq");
                    if (rMin > 0 || rMax > 0 || rCur > 0) {
                        sb.append("cpu").append(i).append("        : ");
                        if (rCur > 0) sb.append("cur=").append(rCur / 1000).append("MHz ");
                        if (rMin > 0) sb.append("min=").append(rMin / 1000).append("MHz ");
                        if (rMax > 0) sb.append("max=").append(rMax / 1000).append("MHz");
                        sb.append("\n");
                        addedRootCpu = true;
                    }
                    String avail = readSysString(base + "scaling_available_frequencies");
                    if (avail != null && !avail.isEmpty()) {
                        sb.append("cpu").append(i).append(" avail   : ").append(avail.trim()).append("\n");
                        addedRootCpu = true;
                    }
                }
            } catch (Throwable ignore) {
            }

            if (!addedRootCpu) {
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

// ============================================================
// Thermal Sensors (Internal)
// ============================================================
private String buildThermalSensorsInfo() {

    StringBuilder sb = new StringBuilder();

    // ------------------------------------------------------------
    // BATTERY TEMPERATURE
    // ------------------------------------------------------------
    long batt = readSysLong("/sys/class/power_supply/battery/temp");
    if (batt > 0) {
        double c = (batt > 1000) ? batt / 1000.0 : batt / 10.0;
        sb.append("Battery        : ")
          .append(String.format(Locale.US, "%.1f°C", c))
          .append("\n");
    }

    // ------------------------------------------------------------
    // CORE THERMAL SENSORS (CPU / GPU / SOC / SKIN)
    // ------------------------------------------------------------
    File dir = new File("/sys/class/thermal");
    if (dir.exists() && dir.isDirectory()) {

        File[] zones = dir.listFiles();
        if (zones != null) {

            for (File z : zones) {
                if (!z.getName().startsWith("thermal_zone")) continue;

                String type = readSysString(z.getAbsolutePath() + "/type");
                if (type == null) continue;

                String low = type.toLowerCase(Locale.US);

                // κρατάμε ΜΟΝΟ ουσιαστικά sensors
                if (!(low.contains("cpu")
                   || low.contains("gpu")
                   || low.contains("soc")
                   || low.contains("skin")
                   || low.contains("battery")
                   || low.contains("modem"))) {
                    continue;
                }

                long t = readSysLong(z.getAbsolutePath() + "/temp");
                if (t <= 0) continue;

                double c = (t > 1000) ? t / 1000.0 : t / 10.0;

                sb.append(padRight(type.trim(), 14))
                  .append(": ")
                  .append(String.format(Locale.US, "%.1f°C", c))
                  .append("  (")
                  .append(z.getName())
                  .append(")\n");
            }
        }
    }

    // ------------------------------------------------------------
    // FALLBACK
    // ------------------------------------------------------------
    if (sb.length() == 0) {
        sb.append("Thermal sensors are not exposed by this device/firmware.\n");
    }

    // ------------------------------------------------------------
    // ADVANCED NOTE
    // ------------------------------------------------------------
    sb.append("\nAdvanced        : Raw thermal zones, cooling tables and trip points ")
      .append("are available only with elevated (root) access.\n");

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

    private String buildRamInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);

                long totalMb = mi.totalMem / (1024 * 1024);
                long availMb = mi.availMem / (1024 * 1024);
                long usedMb = totalMb - availMb;

                sb.append("Total RAM     : ").append(totalMb).append(" MB\n");
                sb.append("Used RAM      : ").append(usedMb).append(" MB\n");
                sb.append("Free RAM      : ").append(availMb).append(" MB\n");
                sb.append("Low Memory    : ").append(mi.lowMemory ? "Yes" : "No").append("\n");
                sb.append("Threshold     : ").append(mi.threshold / (1024 * 1024)).append(" MB\n");
            }
        } catch (Throwable ignore) {
        }

        // /proc/meminfo parsing (user + kernel view)
        String meminfo = readTextFile("/proc/meminfo", 8 * 1024);
        if (meminfo != null && !meminfo.isEmpty()) {
            sb.append("\n/proc/meminfo (core):\n");
            String[] lines = meminfo.split("\n");
            for (String line : lines) {
                if (line.startsWith("MemTotal:")
                        || line.startsWith("MemFree:")
                        || line.startsWith("Buffers:")
                        || line.startsWith("Cached:")
                        || line.startsWith("SwapTotal:")
                        || line.startsWith("SwapFree:")
                        || line.startsWith("Inactive:")
                        || line.startsWith("Active:")) {
                    sb.append(line.trim()).append("\n");
                }
            }
        }

        // ZRAM / swap advanced
        boolean anyZram = false;
        try {
            String zramSize = readSysString("/sys/block/zram0/disksize");
            if (zramSize != null && !zramSize.isEmpty()) {
                sb.append("ZRAM Size     : ").append(zramSize).append(" bytes\n");
                anyZram = true;
            }
            String zramStat = readTextFile("/sys/block/zram0/mm_stat", 1024);
            if (zramStat != null && !zramStat.isEmpty()) {
                sb.append("ZRAM mm_stat  : ").append(zramStat.replace("\n", " ")).append("\n");
                anyZram = true;
            }
        } catch (Throwable ignore) {
        }
        if (!anyZram) {
            sb.append("ZRAM Details  : Not exposed by this device.\n");
        }

        if (sb.length() == 0) {
            sb.append("Unable to read RAM information.\n");
        }

        return sb.toString();
    }

    private String buildStorageInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            File internal = Environment.getDataDirectory();
            appendStorageBlock(sb, "Internal", internal);

            File ext = Environment.getExternalStorageDirectory();
            if (ext != null && ext.exists()) {
                appendStorageBlock(sb, "External (primary)", ext);
            }
        } catch (Throwable ignore) {
        }

        // /proc/mounts core partitions
        String mounts = readTextFile("/proc/mounts", 32 * 1024);
        if (mounts != null && !mounts.isEmpty()) {
            sb.append("Core Mounts   :\n");
            String[] lines = mounts.split("\n");
            String[] interesting = {"/", "/system", "/vendor", "/product", "/data", "/cache", "/metadata", "/sdcard"};
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
                    sb.append("  ").append(mountPoint)
                            .append(" : ").append(parts[2]) // fstype
                            .append(" (").append(parts[0]).append(")\n");
                }
            }
        } else {
            sb.append("Mount table   : Not exposed by this device.\n");
        }

        // /proc/partitions snapshot
        String parts = readTextFile("/proc/partitions", 8 * 1024);
        if (parts != null && !parts.isEmpty()) {
            sb.append("\n/proc/partitions (snapshot):\n");
            sb.append(parts.trim()).append("\n");
        } else {
            sb.append("Partitions    : Not exposed by this device.\n");
        }

        if (sb.length() == 0) {
            sb.append("Unable to read storage partitions.\n");
        }

        return sb.toString();
    }

    private void appendStorageBlock(StringBuilder sb, String label, File path) {
        try {
            StatFs stat = new StatFs(path.getAbsolutePath());
            long blockSize, totalBlocks, availBlocks;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availBlocks = stat.getAvailableBlocks();
            }

            long totalBytes = blockSize * totalBlocks;
            long availBytes = blockSize * availBlocks;
            long usedBytes = totalBytes - availBytes;

            long totalGb = totalBytes / (1024 * 1024 * 1024);
            long usedGb = usedBytes / (1024 * 1024 * 1024);
            long freeGb = availBytes / (1024 * 1024 * 1024);

            sb.append(label).append(":\n");
            sb.append("  Path   : ").append(path.getAbsolutePath()).append("\n");
            sb.append("  Total  : ").append(totalGb).append(" GB\n");
            sb.append("  Used   : ").append(usedGb).append(" GB\n");
            sb.append("  Free   : ").append(freeGb).append(" GB\n\n");

        } catch (Throwable ignore) {
        }
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
