// ==========================================================
// GDiolitsis Engine Lab (GEL) — Author & Developer  
// DeviceInfoInternalActivity.java — FULL PRO v7.0 (PART 1/2)  
// ==========================================================

package com.gel.cleaner;

import com.gel.cleaner.base.GELAutoActivityHook;
import com.gel.cleaner.base.GELFoldableCallback;
import com.gel.cleaner.base.GELFoldableDetector;
import com.gel.cleaner.base.GELFoldableUIManager;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class DeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

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

        if (txtSystemContent != null)          txtSystemContent.setText(buildSystemInfo());
        if (txtAndroidContent != null)         txtAndroidContent.setText(buildAndroidInfo());
        if (txtCpuContent != null)             txtCpuContent.setText(buildCpuInfo());
        if (txtGpuContent != null)             txtGpuContent.setText(buildGpuInfo());
        if (txtThermalContent != null)         txtThermalContent.setText(buildThermalSensorsInfo());
        if (txtThermalZonesContent != null)    txtThermalZonesContent.setText(buildThermalZonesInfo());
        if (txtVulkanContent != null)          txtVulkanContent.setText(buildVulkanInfo());
        if (txtThermalProfilesContent != null) txtThermalProfilesContent.setText(buildThermalProfilesInfo());
        if (txtFpsGovernorContent != null)     txtFpsGovernorContent.setText(buildFpsGovernorInfo());
        if (txtRamContent != null)             txtRamContent.setText(buildRamInfo());
        if (txtStorageContent != null)         txtStorageContent.setText(buildStorageInfo());
        if (txtScreenContent != null)          txtScreenContent.setText(buildScreenInfo());
        if (txtConnectivityContent != null)    txtConnectivityContent.setText(buildConnectivityInfo());
        if (txtRootContent != null)            txtRootContent.setText(buildRootInfo());

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

// ==========================================================
// DeviceInfoInternalActivity.java — FULL PRO v7.0 (PART 2/2)
// ==========================================================

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
    // EXPANDERS — Soft Expand v2.0
    // ============================================================
    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {

        for (int i = 0; i < allContents.length; i++) {
            TextView c  = allContents[i];
            TextView ic = allIcons[i];
            if (c == null || ic == null) continue;
            if (c == toOpen) continue;

            animateCollapse(c);
            ic.setText("＋");
        }

        boolean visible = (toOpen.getVisibility() == View.VISIBLE);

        if (visible) {
            animateCollapse(toOpen);
            iconToUpdate.setText("＋");
        } else {
            animateExpand(toOpen);
            iconToUpdate.setText("−");
        }
    }

    private void animateExpand(final View v) {
        v.measure(
                View.MeasureSpec.makeMeasureSpec(
                        ((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
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
                .withEndAction(() -> v.getLayoutParams().height = target)
                .start();
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        final int initial = v.getMeasuredHeight();
        v.setAlpha(1f);

        v.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = initial;
                    v.setAlpha(1f);
                })
                .start();
    }

    // ============================================================
    // BUILDERS — FULL PRO
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

        sb.append("Fingerprint:\n").append(Build.FINGERPRINT).append("\n\n");

        String androidId = "";
        try {
            androidId = Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable ignore) {}
        if (androidId != null) sb.append("Android ID   : ").append(androidId).append("\n");

        sb.append("Device Type  : ")
                .append(getResources().getConfiguration().smallestScreenWidthDp >= 600
                        ? "Tablet\n" : "Phone\n");

        String region = getProp("ro.product.locale.region");
        if (!region.isEmpty()) sb.append("Region       : ").append(region).append("\n");

        String vendor = getProp("ro.product.vendor.name");
        if (!vendor.isEmpty()) sb.append("Vendor Name  : ").append(vendor).append("\n");

        return sb.toString();
    }

    private String buildAndroidInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Android      : ").append(Build.VERSION.RELEASE)
                .append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sb.append("Security Pch : ").append(Build.VERSION.SECURITY_PATCH).append("\n");

        sb.append("Build ID     : ").append(Build.ID).append("\n");
        sb.append("Build Type   : ").append(Build.TYPE).append("\n");
        sb.append("Build Tags   : ").append(Build.TAGS).append("\n\n");

        if (Build.VERSION.INCREMENTAL != null)
            sb.append("Incremental  : ").append(Build.VERSION.INCREMENTAL).append("\n");

        String baseband = getProp("gsm.version.baseband");
        if (!baseband.isEmpty()) sb.append("Baseband     : ").append(baseband).append("\n");

        String vendorRel = getProp("ro.vendor.build.version.release");
        if (!vendorRel.isEmpty()) sb.append("Vendor Rel   : ").append(vendorRel).append("\n");

        String miui = getProp("ro.miui.ui.version.name");
        if (!miui.isEmpty()) sb.append("MIUI         : ").append(miui).append("\n");

        String hyper = getProp("ro.mi.os.version.name");
        if (!hyper.isEmpty()) sb.append("HyperOS      : ").append(hyper).append("\n");

        return sb.toString();
    }

    private String buildCpuInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("ABI          : ");
        if (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0) {
            for (int i = 0; i < Build.SUPPORTED_ABIS.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(Build.SUPPORTED_ABIS[i]);
            }
        } else sb.append(Build.CPU_ABI);
        sb.append("\n");

        int cores = Runtime.getRuntime().availableProcessors();
        sb.append("CPU Cores    : ").append(cores).append("\n");

        String cpuinfo = readTextFile("/proc/cpuinfo", 32768);
        if (cpuinfo != null) {
            for (String line : cpuinfo.split("\n")) {
                String low = line.toLowerCase();
                if (low.startsWith("hardware") ||
                    low.startsWith("model name") ||
                    low.startsWith("processor")) {
                    sb.append(line.trim()).append("\n");
                }
            }
        }

        String gov = readSysString("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        if (gov != null) sb.append("Governor     : ").append(gov).append("\n");

        long cur = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        long min = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        long max = readSysLong("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");

        if (cur > 0 || min > 0 || max > 0) {
            sb.append("Freq (MHz)   : ");
            if (cur > 0) sb.append("cur=").append(cur / 1000).append(" ");
            if (min > 0) sb.append("min=").append(min / 1000).append(" ");
            if (max > 0) sb.append("max=").append(max / 1000);
            sb.append("\n");
        }

        String policy0 = readSysString("/sys/devices/system/cpu/cpufreq/policy0/cpuinfo_min_freq");
        String policy7 = readSysString("/sys/devices/system/cpu/cpufreq/policy7/cpuinfo_max_freq");
        if ((policy0 != null && !policy0.isEmpty()) ||
            (policy7 != null && !policy7.isEmpty())) {
            sb.append("Cluster Hint : big.LITTLE detected\n");
        }

        return sb.toString();
    }

    private String buildGpuInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ConfigurationInfo ci = am.getDeviceConfigurationInfo();
                if (ci != null) sb.append("OpenGL ES    : ").append(ci.getGlEsVersion()).append("\n");
            }
        } catch (Throwable ignore) {}

        String egl = getProp("ro.hardware.egl");
        if (!egl.isEmpty()) sb.append("EGL HW       : ").append(egl).append("\n");

        String driver = getProp("ro.gfx.driver.0");
        if (!driver.isEmpty()) sb.append("GPU Driver   : ").append(driver).append("\n");

        String perf = getProp("ro.gpu.uv");
        if (!perf.isEmpty()) sb.append("GPU Mode     : ").append(perf).append("\n");

        if (sb.length() == 0) sb.append("No GPU information available.\n");

        return sb.toString();
    }

    private String buildThermalSensorsInfo() {
        StringBuilder sb = new StringBuilder();

        long batt = readSysLong("/sys/class/power_supply/battery/temp");
        if (batt > 0) {
            double c = (batt > 1000) ? batt / 1000.0 : batt / 10.0;
            sb.append("Battery      : ").append(String.format("%.1f°C", c)).append("\n");
        }

        File dir = new File("/sys/class/thermal");
        if (dir.exists()) {
            File[] zones = dir.listFiles();
            if (zones != null) {
                for (File z : zones) {
                    if (!z.getName().startsWith("thermal_zone")) continue;

                    String type = readSysString(z.getAbsolutePath() + "/type");
                    if (type == null) continue;
                    String low = type.toLowerCase();

                    if (low.contains("cpu") || low.contains("gpu") ||
                        low.contains("soc") || low.contains("skin")) {

                        long t = readSysLong(z.getAbsolutePath() + "/temp");
                        if (t <= 0) continue;

                        double c = (t > 1000) ? t / 1000.0 : t / 10.0;

                        sb.append(padRight(type, 12)).append(": ")
                                .append(String.format("%.1f°C", c))
                                .append("  (").append(z.getName()).append(")\n");
                    }
                }
            }
        }

        if (sb.length() == 0) sb.append("No readable thermal sensors.\n");

        return sb.toString();
    }

    private String buildThermalZonesInfo() {
        StringBuilder sb = new StringBuilder();

        File dir = new File("/sys/class/thermal");
        if (!dir.exists()) {
            sb.append("Thermal directory not accessible.\n");
            return sb.toString();
        }

        File[] zones = dir.listFiles();
        if (zones == null) {
            sb.append("No thermal zones found.\n");
            return sb.toString();
        }

        for (File z : zones) {
            if (!z.getName().startsWith("thermal_zone")) continue;

            String type = readSysString(z.getAbsolutePath() + "/type");
            long temp   = readSysLong(z.getAbsolutePath() + "/temp");

            sb.append(z.getName()).append(" | ");

            if (type != null) sb.append(type.trim()).append(" | ");

            if (temp > 0) {
                double c = (temp > 1000) ? temp / 1000.0 : temp / 10.0;
                sb.append(String.format("%.1f°C", c));
            } else sb.append("N/A");

            sb.append("\n");
        }

        return sb.toString();
    }

    private String buildVulkanInfo() {
        StringBuilder sb = new StringBuilder();

        boolean lvl = getPackageManager().hasSystemFeature("android.hardware.vulkan.level");
        boolean ver = getPackageManager().hasSystemFeature("android.hardware.vulkan.version");

        sb.append("Feature Level : ").append(lvl ? "Yes" : "No").append("\n");
        sb.append("Feature Vers  : ").append(ver ? "Yes" : "No").append("\n");

        String hw = getProp("ro.hardware.vulkan");
        if (!hw.isEmpty()) sb.append("Vulkan HW     : ").append(hw).append("\n");

        String enable = getProp("ro.vulkan.enable");
        if (!enable.isEmpty()) sb.append("Vulkan Enable : ").append(enable).append("\n");

        String layers = getProp("debug.vulkan.layers");
        if (!layers.isEmpty()) sb.append("Debug Layers  : ").append(layers).append("\n");

        return sb.length()==0 ? "No Vulkan info.\n" : sb.toString();
    }

    private String buildThermalProfilesInfo() {
        StringBuilder sb = new StringBuilder();

        String[] keys = {
                "ro.vendor.thermal.config",
                "ro.hardware.thermal",
                "ro.thermal_config",
                "persist.vendor.thermal.config",
                "persist.sys.thermal.config"
        };

        for (String k : keys) {
            String v = getProp(k);
            if (!v.isEmpty()) sb.append(k).append(" = ").append(v).append("\n");
        }

        return sb.length()==0 ? "No thermal profile properties.\n" : sb.toString();
    }

    private String buildFpsGovernorInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            sb.append("Resolution    : ").append(dm.widthPixels)
                    .append(" x ").append(dm.heightPixels).append("\n");
            sb.append("Density       : ").append(dm.densityDpi).append(" dpi\n");
        } catch (Throwable ignore) {}

        String refresh = getProp("ro.surface_flinger.refresh_rate");
        if (!refresh.isEmpty()) sb.append("Default Ref   : ").append(refresh).append(" Hz\n");

        String peak = getProp("ro.surface_flinger.max_refresh_rate");
        if (!peak.isEmpty()) sb.append("Max Refresh   : ").append(peak).append(" Hz\n");

        String mode = getProp("ro.display.mode");
        if (!mode.isEmpty()) sb.append("Display Mode  : ").append(mode).append("\n");

        String gov = readSysString("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
        if (gov != null) sb.append("CPU Governor  : ").append(gov).append("\n");

        return sb.length()==0 ? "No FPS / governor info.\n" : sb.toString();
    }

    private String buildRamInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);

                long total = mi.totalMem / (1024 * 1024);
                long avail = mi.availMem / (1024 * 1024);
                long used  = total - avail;

                sb.append("Total RAM     : ").append(total).append(" MB\n");
                sb.append("Used RAM      : ").append(used).append(" MB\n");
                sb.append("Free RAM      : ").append(avail).append(" MB\n");
                sb.append("Low Memory    : ").append(mi.lowMemory ? "Yes" : "No").append("\n");
                sb.append("Threshold     : ").append(mi.threshold / (1024 * 1024)).append(" MB\n");
            }
        } catch (Throwable ignore) {}

        return sb.length()==0 ? "Unable to read RAM.\n" : sb.toString();
    }

    private String buildStorageInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            appendStorageBlock(sb, "Internal", Environment.getDataDirectory());

            File ext = Environment.getExternalStorageDirectory();
            if (ext != null && ext.exists()) appendStorageBlock(sb, "External", ext);

        } catch (Throwable ignore) {}

        return sb.length()==0 ? "Unable to read storage.\n" : sb.toString();
    }

    private void appendStorageBlock(StringBuilder sb, String label, File path) {
        try {
            StatFs stat = new StatFs(path.getAbsolutePath());

            long bs  = stat.getBlockSizeLong();
            long tot = stat.getBlockCountLong();
            long avi = stat.getAvailableBlocksLong();

            long total = (bs * tot) / (1024L * 1024L * 1024L);
            long free  = (bs * avi) / (1024L * 1024L * 1024L);
            long used  = total - free;

            sb.append(label).append(":\n");
            sb.append("  Path   : ").append(path.getAbsolutePath()).append("\n");
            sb.append("  Total  : ").append(total).append(" GB\n");
            sb.append("  Used   : ").append(used).append(" GB\n");
            sb.append("  Free   : ").append(free).append(" GB\n\n");

        } catch (Throwable ignore) {}
    }

    private String buildScreenInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int w = dm.widthPixels;
            int h = dm.heightPixels;
            int dpi = dm.densityDpi;

            sb.append("Resolution    : ").append(w).append(" x ").append(h).append("\n");
            sb.append("Density       : ").append(dm.density)
                    .append(" (").append(dpi).append(" dpi)\n");

            double wi = w / (double) dpi;
            double hi = h / (double) dpi;
            double diag = Math.sqrt(wi * wi + hi * hi);

            sb.append("Approx. Size  : ").append(String.format("%.1f\"", diag)).append("\n");

        } catch (Throwable ignore) {}

        return sb.length()==0 ? "Unable to read screen.\n" : sb.toString();
    }

    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();

        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                Network active = cm.getActiveNetwork();
                NetworkCapabilities caps = cm.getNetworkCapabilities(active);

                if (caps != null) {
                    sb.append("Active        : ");
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) sb.append("Wi-Fi\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) sb.append("Cellular\n");
                    else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) sb.append("Ethernet\n");
                    else sb.append("Other\n");

                    sb.append("Downlink      : ")
                            .append(caps.getLinkDownstreamBandwidthKbps()).append(" kbps\n");
                    sb.append("Uplink        : ")
                            .append(caps.getLinkUpstreamBandwidthKbps()).append(" kbps\n");
                }
            }

            WifiManager wm = (WifiManager) getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null && wi.getNetworkId() != -1) {
                    sb.append("\nWi-Fi:\n");
                    sb.append("  SSID        : ").append(wi.getSSID()).append("\n");
                    sb.append("  Link speed  : ").append(wi.getLinkSpeed()).append(" Mbps\n");
                    sb.append("  RSSI        : ").append(wi.getRssi()).append(" dBm\n");
                    sb.append("  Band        : ").append(describeWifiBand(wi.getFrequency())).append("\n");
                }
            }

            TelephonyManager tm =
                    (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                sb.append("\nCellular:\n");

                String op = tm.getNetworkOperatorName();
                if (op != null && !op.isEmpty())
                    sb.append("  Operator    : ").append(op).append("\n");

                int netType = tm.getDataNetworkType();
                sb.append("  Network     : ").append(describeNetworkType(netType)).append("\n");
            }

        } catch (Throwable ignore) {}

        return sb.length()==0 ? "No connectivity info.\n" : sb.toString();
    }

    private String buildRootInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Root Detected : ").append(isRooted ? "YES" : "NO").append("\n");

        sb.append("Build Tags    : ").append(Build.TAGS).append("\n");

        String secure = getProp("ro.secure");
        if (!secure.isEmpty()) sb.append("ro.secure     : ").append(secure).append("\n");

        String dbg = getProp("ro.debuggable");
        if (!dbg.isEmpty()) sb.append("ro.debuggable : ").append(dbg).append("\n");

        String verity = getProp("ro.boot.veritymode");
        if (!verity.isEmpty()) sb.append("Verity Mode   : ").append(verity).append("\n");

        String selinux = getProp("ro.build.selinux");
        if (!selinux.isEmpty()) sb.append("SELinux       : ").append(selinux).append("\n");

        if (isRooted) {
            sb.append("\nRoot paths found:\n");
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
            };
            for (String p : paths) {
                if (new File(p).exists()) sb.append("  ").append(p).append("\n");
            }
        }

        return sb.toString();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String readTextFile(String path, int maxLen) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int read;
            while ((read = br.read(buf)) > 0 && sb.length() < maxLen) {
                sb.append(buf, 0, read);
            }
            return sb.toString();
        } catch (Throwable ignore) {
            return null;
        }
    }

    private String readSysString(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            return line != null ? line.trim() : null;
        } catch (Throwable ignore) {
            return null;
        }
    }

    private long readSysLong(String path) {
        String s = readSysString(path);
        if (s == null) return -1;
        try { return Long.parseLong(s); }
        catch (Throwable ignore) { return -1; }
    }

    private boolean isDeviceRooted() {
        try {
            if (Build.TAGS != null && Build.TAGS.contains("test-keys")) return true;

            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
            };

            for (String p : paths) {
                if (new File(p).exists()) return true;
            }

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();

            return line != null && !line.trim().isEmpty();

        } catch (Throwable ignore) {
            return false;
        }
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Throwable ignore) {
            return "";
        }
    }

    private String describeWifiBand(int freq) {
        if (freq >= 2400 && freq < 2500) return "2.4 GHz";
        if (freq >= 4900 && freq < 5900) return "5 GHz";
        if (freq >= 5925 && freq < 7125) return "6 GHz";
        return "Unknown";
    }

    private String describeNetworkType(int t) {
        switch (t) {
            case TelephonyManager.NETWORK_TYPE_GPRS: return "2G (GPRS)";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "2G (EDGE)";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "3G (UMTS)";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "3G (HSDPA)";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "3G (HSUPA)";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "3G (HSPA)";
            case TelephonyManager.NETWORK_TYPE_LTE: return "4G (LTE)";
            case TelephonyManager.NETWORK_TYPE_NR: return "5G (NR)";
            default: return "Unknown";
        }
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        while (s.length() < n) s += " ";
        return s;
    }
}

