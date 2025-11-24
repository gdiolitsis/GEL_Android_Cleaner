// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoInternalActivity.java — GEL FINAL v5.0 (Ultra-Formatted GEL Edition)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste. Δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// DEVICE INFO — INTERNAL REPORT v5.0
// GPU ADV + THERMAL SPLIT + CONNECTIVITY FULL MAP
// FOLDABLE AUTO-DETECT + DYNAMIC UI REFLOW
// ============================================================
public class DeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private boolean isRooted = false;

    // Foldable system
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager foldUI;

    // One-open-only logic
    private TextView[] allContents;
    private TextView[] allIcons;

    // ============================================================
    // LOCALE
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        // --------------------------------------------------------
        // FOLDABLE INIT (safe for non-foldables)
        // --------------------------------------------------------
        foldUI = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // --------------------------------------------------------
        // TITLE
        // --------------------------------------------------------
        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) {
            title.setText(getString(R.string.phone_info_internal));
        }

        // --------------------------------------------------------
        // CONTENT REFERENCES (14 SECTIONS)
        // --------------------------------------------------------
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

        // --------------------------------------------------------
        // ICON REFERENCES
        // --------------------------------------------------------
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

        // --------------------------------------------------------
        // ONE-OPEN-ONLY ARRAYS
        // --------------------------------------------------------
        allContents = new TextView[]{
                txtSystemContent,
                txtAndroidContent,
                txtCpuContent,
                txtGpuContent,
                txtThermalContent,
                txtThermalZonesContent,
                txtVulkanContent,
                txtThermalProfilesContent,
                txtFpsGovernorContent,
                txtRamContent,
                txtStorageContent,
                txtScreenContent,
                txtConnectivityContent,
                txtRootContent
        };

        allIcons = new TextView[]{
                iconSystem,
                iconAndroid,
                iconCpu,
                iconGpu,
                iconThermal,
                iconThermalZones,
                iconVulkan,
                iconThermalProfiles,
                iconFpsGovernor,
                iconRam,
                iconStorage,
                iconScreen,
                iconConnectivity,
                iconRoot
        };

        // --------------------------------------------------------
        // ROOT DETECTION
        // --------------------------------------------------------
        isRooted = isDeviceRooted();

        // ========================================================
        // SECTION 1 — SYSTEM / HARDWARE
        // ========================================================
        if (txtSystemContent != null) {
            StringBuilder sys = new StringBuilder();
            sys.append("── SYSTEM ──\n");
            sys.append("Brand: ").append(Build.BRAND).append("\n");
            sys.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            sys.append("Model: ").append(Build.MODEL).append("\n");
            sys.append("Device: ").append(Build.DEVICE).append("\n");
            sys.append("Product: ").append(Build.PRODUCT).append("\n");
            sys.append("Board: ").append(Build.BOARD).append("\n");
            sys.append("Hardware: ").append(Build.HARDWARE).append("\n");
            sys.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");
            try {
                sys.append("Radio Version: ").append(Build.getRadioVersion()).append("\n");
            } catch (Throwable ignored) {}
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                sys.append("SoC Manufacturer: ").append(Build.SOC_MANUFACTURER).append("\n");
                sys.append("SoC Model: ").append(Build.SOC_MODEL).append("\n");
            }
            sys.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n");
            sys.append("Build Time: ").append(Build.TIME).append(" ms\n");
            txtSystemContent.setText(sys.toString());
        }

        // ========================================================
        // SECTION 2 — ANDROID / OS
        // ========================================================
        if (txtAndroidContent != null) {
            StringBuilder os = new StringBuilder();
            os.append("── ANDROID / OS ──\n");
            os.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
            os.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
            os.append("Build ID: ").append(Build.ID).append("\n");
            os.append("Build Type: ").append(Build.TYPE).append("\n");
            os.append("Build Tags: ").append(Build.TAGS).append("\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                os.append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n");
            }
            os.append("Kernel (os.version): ").append(System.getProperty("os.version")).append("\n");
            txtAndroidContent.setText(os.toString());
        }

        // ========================================================
        // SECTION 3 — CPU / ABI
        // ========================================================
        if (txtCpuContent != null) {
            StringBuilder cpu = new StringBuilder();
            cpu.append("── CPU ──\n");

            String[] abis = Build.SUPPORTED_ABIS;
            if (abis != null && abis.length > 0) {
                cpu.append("Primary ABI: ").append(abis[0]).append("\n");
            } else {
                cpu.append("Primary ABI: N/A\n");
            }

            cpu.append("Supported ABIs: ");
            if (abis != null) {
                for (int i = 0; i < abis.length; i++) {
                    cpu.append(abis[i]);
                    if (i < abis.length - 1) cpu.append(", ");
                }
            } else {
                cpu.append("N/A");
            }
            cpu.append("\n");

            cpu.append("Logical Cores: ")
                    .append(Runtime.getRuntime().availableProcessors())
                    .append("\n\n");

            cpu.append("CPU Info (/proc/cpuinfo):\n");
            cpu.append(readCpuInfo()).append("\n");

            txtCpuContent.setText(cpu.toString());
        }

        // ========================================================
        // SECTION 4 — GPU ADVANCED
        // ========================================================
        if (txtGpuContent != null) {
            StringBuilder gpu = new StringBuilder();
            gpu.append("── GPU INFO ──\n");
            gpu.append(readGpuAdvanced());
            txtGpuContent.setText(gpu.toString());
        }

        // ========================================================
        // SECTION 5 — THERMAL (PRIMARY)
        // ========================================================
        if (txtThermalContent != null) {
            StringBuilder thermal = new StringBuilder();
            thermal.append("── THERMAL SENSORS ──\n");
            thermal.append(readThermalPrimary());
            txtThermalContent.setText(thermal.toString());
        }

        // ========================================================
        // SECTION 6 — THERMAL ZONES (FULL)
        // ========================================================
        if (txtThermalZonesContent != null) {
            StringBuilder thermalZones = new StringBuilder();
            thermalZones.append("── THERMAL ZONES ──\n");
            thermalZones.append(readThermalZonesFull());
            txtThermalZonesContent.setText(thermalZones.toString());
        }

        // ========================================================
        // SECTION 7 — VULKAN / GPU DRIVER
        // ========================================================
        if (txtVulkanContent != null) {
            StringBuilder vk = new StringBuilder();
            vk.append("── VULKAN / GPU DRIVER ──\n");
            vk.append(readVulkanInfo());
            txtVulkanContent.setText(vk.toString());
        }

        // ========================================================
        // SECTION 8 — THERMAL ENGINE PROFILES
        // ========================================================
        if (txtThermalProfilesContent != null) {
            StringBuilder thermProfiles = new StringBuilder();
            thermProfiles.append("── THERMAL ENGINE PROFILES ──\n");
            thermProfiles.append(readThermalProfiles());
            txtThermalProfilesContent.setText(thermProfiles.toString());
        }

        // ========================================================
        // SECTION 9 — FPS / GOVERNOR / GPU CLOCKS
        // ========================================================
        if (txtFpsGovernorContent != null) {
            StringBuilder fpsGov = new StringBuilder();
            fpsGov.append("── FPS / GPU GOVERNOR & CLOCKS ──\n");
            fpsGov.append(readFpsAndClocksSummary());
            txtFpsGovernorContent.setText(fpsGov.toString());
        }

        // ========================================================
        // SECTION 10 — RAM
        // ========================================================
        if (txtRamContent != null) {
            StringBuilder ram = new StringBuilder();
            ram.append("── RAM ──\n");

            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);

                long totalMb = mi.totalMem / (1024L * 1024L);
                long freeMb  = mi.availMem / (1024L * 1024L);
                long usedMb  = totalMb - freeMb;

                ram.append("Total RAM: ").append(totalMb).append(" MB\n");
                ram.append("Used RAM: ").append(usedMb).append(" MB\n");
                ram.append("Free RAM: ").append(freeMb).append(" MB\n");
                ram.append("Low RAM device: ").append(mi.lowMemory ? "YES" : "NO").append("\n");
            } else {
                ram.append("RAM info: N/A\n");
            }

            txtRamContent.setText(ram.toString());
        }

        // ========================================================
        // SECTION 11 — INTERNAL STORAGE
        // ========================================================
        if (txtStorageContent != null) {
            StringBuilder st = new StringBuilder();
            st.append("── INTERNAL STORAGE ──\n");
            try {
                File dataDir = Environment.getDataDirectory();
                long totalBytes = dataDir.getTotalSpace();
                long freeBytes  = dataDir.getFreeSpace();

                long totalGb = totalBytes / (1024L * 1024L * 1024L);
                long freeGb  = freeBytes  / (1024L * 1024L * 1024L);
                long usedGb  = totalGb - freeGb;

                st.append("Total: ").append(totalGb).append(" GB\n");
                st.append("Used: ").append(usedGb).append(" GB\n");
                st.append("Free: ").append(freeGb).append(" GB\n");
                st.append("\nRaw total bytes: ").append(totalBytes).append("\n");
                st.append("Raw free bytes: ").append(freeBytes).append("\n");

            } catch (Throwable e) {
                st.append("Storage info: N/A\n");
            }
            txtStorageContent.setText(st.toString());
        }

        // ========================================================
        // SECTION 12 — SCREEN / DISPLAY
        // ========================================================
        if (txtScreenContent != null) {
            StringBuilder sc = new StringBuilder();
            sc.append("── SCREEN ──\n");

            DisplayMetrics dm = getResources().getDisplayMetrics();
            sc.append("Resolution: ")
                    .append(dm.widthPixels)
                    .append(" x ")
                    .append(dm.heightPixels)
                    .append(" px\n");
            sc.append("Density: ").append(dm.densityDpi).append(" dpi\n");
            sc.append("Scaled density: ").append(dm.scaledDensity).append("\n");

            try {
                float xInches = dm.widthPixels / dm.xdpi;
                float yInches = dm.heightPixels / dm.ydpi;
                double diagonal = Math.sqrt(xInches * xInches + yInches * yInches);
                sc.append("Diagonal: ").append(String.format("%.1f", diagonal)).append(" inches\n");
            } catch (Throwable ignored) {}

            try {
                float refresh;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && getDisplay() != null) {
                    refresh = getDisplay().getRefreshRate();
                } else {
                    //noinspection deprecation
                    refresh = getWindowManager().getDefaultDisplay().getRefreshRate();
                }
                sc.append("Refresh rate: ").append(String.format("%.1f", refresh)).append(" Hz\n");
            } catch (Throwable ignored) {}

            txtScreenContent.setText(sc.toString());
        }

        // ========================================================
        // SECTION 13 — CONNECTIVITY (FULL MAP)
        // ========================================================
        if (txtConnectivityContent != null) {
            txtConnectivityContent.setText(buildConnectivityInfo());
        }

        // ========================================================
        // SECTION 14 — ROOT EXTRAS
        // ========================================================
        if (txtRootContent != null) {
            StringBuilder rootSb = new StringBuilder();
            rootSb.append("── ROOT EXTRAS ──\n");
            if (isRooted) {
                rootSb.append("Device appears ROOTED\n\n");
                rootSb.append("Build Tags: ").append(Build.TAGS).append("\n");
                rootSb.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
                rootSb.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
                rootSb.append("SELinux: ").append(getSelinux()).append("\n");
                rootSb.append("su path: ").append(checkSuPaths()).append("\n");
            } else {
                rootSb.append("Device appears NOT rooted\n");
                rootSb.append("Root-level debug info disabled\n");
            }
            txtRootContent.setText(rootSb.toString());
        }

        // ========================================================
        // EXPANDABLE HEADERS (14)
        // ========================================================
        setupSection(findViewById(R.id.headerSystem),          txtSystemContent,          iconSystem);
        setupSection(findViewById(R.id.headerAndroid),         txtAndroidContent,         iconAndroid);
        setupSection(findViewById(R.id.headerCpu),             txtCpuContent,             iconCpu);
        setupSection(findViewById(R.id.headerGpu),             txtGpuContent,             iconGpu);
        setupSection(findViewById(R.id.headerThermal),         txtThermalContent,         iconThermal);
        setupSection(findViewById(R.id.headerThermalZones),    txtThermalZonesContent,    iconThermalZones);
        setupSection(findViewById(R.id.headerVulkan),          txtVulkanContent,          iconVulkan);
        setupSection(findViewById(R.id.headerThermalProfiles), txtThermalProfilesContent, iconThermalProfiles);
        setupSection(findViewById(R.id.headerFpsGovernor),     txtFpsGovernorContent,     iconFpsGovernor);
        setupSection(findViewById(R.id.headerRam),             txtRamContent,             iconRam);
        setupSection(findViewById(R.id.headerStorage),         txtStorageContent,         iconStorage);
        setupSection(findViewById(R.id.headerScreen),          txtScreenContent,          iconScreen);
        setupSection(findViewById(R.id.headerConnectivity),    txtConnectivityContent,    iconConnectivity);
        setupSection(findViewById(R.id.headerRoot),            txtRootContent,            iconRoot);
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

    // ============================================================
    // GELFoldableCallback
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // κρατάμε hook για μελλοντικά posture-specific layouts
        // (HALF_OPEN / TABLE_MODE / TENT_MODE / FLAT / CLOSED)
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (foldUI != null) foldUI.applyUI(isInner);
    }

    // ============================================================
    // ONE-OPEN-ONLY LOGIC
    // ============================================================
    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {
        if (allContents == null || allIcons == null) return;

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == null || ic == null) continue;
            if (c == toOpen) continue;

            c.setVisibility(View.GONE);
            ic.setText("＋");
        }

        boolean visible = (toOpen.getVisibility() == View.VISIBLE);
        toOpen.setVisibility(visible ? View.GONE : View.VISIBLE);
        iconToUpdate.setText(visible ? "＋" : "−");
    }

    // ============================================================
    // GENERIC HELPERS
    // ============================================================
    private boolean isEmptySafe(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ============================================================
    // CPU INFO (/proc/cpuinfo)
    // ============================================================
    private String readCpuInfo() {
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            int lines = 0;
            while ((line = br.readLine()) != null && lines < 40) {
                sb.append(line).append("\n");
                lines++;
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "[unavailable]";
        }
    }

    // ============================================================
    // GPU ADVANCED (family + driver + clocks)
    // ============================================================
    private String readGpuAdvanced() {
        StringBuilder sb = new StringBuilder();

        String soc          = getProp("ro.board.platform");
        String egl          = getProp("ro.hardware.egl");
        String glEs         = getProp("ro.opengles.version");
        String gfxDriver0   = getProp("ro.gfx.driver.0");
        String gfxVendor    = getProp("ro.gfx.vendor");
        String hwuiRenderer = getProp("debug.hwui.renderer");
        String hwuiVendor   = getProp("debug.hwui.vendor");

        sb.append("SoC / board: ").append(isEmptySafe(soc) ? "N/A" : soc).append("\n");
        sb.append("EGL hardware: ").append(isEmptySafe(egl) ? "N/A" : egl).append("\n");
        sb.append("OpenGL ES (ro.opengles.version): ")
                .append(isEmptySafe(glEs) ? "N/A" : glEs)
                .append("\n");
        sb.append("GPU driver (ro.gfx.driver.0): ")
                .append(isEmptySafe(gfxDriver0) ? "N/A" : gfxDriver0)
                .append("\n");
        sb.append("GPU vendor (ro.gfx.vendor): ")
                .append(isEmptySafe(gfxVendor) ? "N/A" : gfxVendor)
                .append("\n");
        sb.append("HWUI renderer: ")
                .append(isEmptySafe(hwuiRenderer) ? "N/A" : hwuiRenderer)
                .append("\n");
        sb.append("HWUI vendor: ")
                .append(isEmptySafe(hwuiVendor) ? "N/A" : hwuiVendor)
                .append("\n");

        String gpuFamily = detectGpuFamily(egl, gfxVendor, soc);
        if (!isEmptySafe(gpuFamily)) {
            sb.append("GPU family: ").append(gpuFamily).append("\n");
        }

        String clocks = readGpuClocks();
        if (!isEmptySafe(clocks)) {
            sb.append("Base GPU clocks: ").append(clocks).append("\n");
        }

        return sb.toString();
    }

    private String detectGpuFamily(String egl, String gfxVendor, String soc) {
        StringBuilder all = new StringBuilder();
        if (egl != null) all.append(egl).append(" ");
        if (gfxVendor != null) all.append(gfxVendor).append(" ");
        if (soc != null) all.append(soc);

        String s = all.toString().toLowerCase();
        if (s.contains("adreno") || s.contains("qcom") || s.contains("qualcomm")) {
            return "Qualcomm Adreno";
        }
        if (s.contains("mali") || s.contains("exynos") || s.contains("samsung")) {
            return "ARM Mali";
        }
        if (s.contains("powervr") || s.contains("imagination")) {
            return "PowerVR (Imagination)";
        }
        if (s.contains("apple")) {
            return "Apple GPU";
        }
        if (s.contains("intel")) {
            return "Intel GPU";
        }
        return "";
    }

    // ============================================================
    // THERMAL HELPERS
    // ============================================================
    private String readThermalPrimary() {
        StringBuilder out = new StringBuilder();
        try {
            File base = new File("/sys/class/thermal");
            if (!base.exists() || !base.isDirectory()) {
                out.append("Thermal info: not available on this device\n");
                return out.toString();
            }

            File[] zones = base.listFiles();
            if (zones == null || zones.length == 0) {
                out.append("Thermal zones: none visible\n");
                return out.toString();
            }

            List<String> primary = new ArrayList<>();

            for (File z : zones) {
                String name = z.getName();
                if (!name.startsWith("thermal_zone")) continue;

                File typeFile = new File(z, "type");
                File tempFile = new File(z, "temp");
                if (!typeFile.exists() || !tempFile.exists()) continue;

                String type = readSmallFile(typeFile);
                String tempRaw = readSmallFile(tempFile);
                if (type == null || tempRaw == null) continue;

                type = type.trim();
                tempRaw = tempRaw.trim();

                Double celsius = parseThermalTemp(tempRaw);
                if (celsius == null) continue;

                if (celsius < -10 || celsius > 150) continue;

                String lower = type.toLowerCase();
                if (!isPrimaryThermal(lower)) continue;

                String entry = type + " : " + String.format("%.1f", celsius) + " °C";
                primary.add(entry);
            }

            if (primary.isEmpty()) {
                out.append("No primary thermal sensors readable\n");
                return out.toString();
            }

            for (String s : primary) {
                out.append("• ").append(s).append("\n");
            }

        } catch (Throwable t) {
            out.append("Thermal info error: ").append(t.getMessage()).append("\n");
        }
        return out.toString();
    }

    private String readThermalZonesFull() {
        StringBuilder out = new StringBuilder();
        try {
            File base = new File("/sys/class/thermal");
            if (!base.exists() || !base.isDirectory()) {
                out.append("Thermal info: not available on this device\n");
                return out.toString();
            }

            File[] zones = base.listFiles();
            if (zones == null || zones.length == 0) {
                out.append("Thermal zones: none visible\n");
                return out.toString();
            }

            int shown = 0;
            for (File z : zones) {
                String name = z.getName();
                if (!name.startsWith("thermal_zone")) continue;

                File typeFile = new File(z, "type");
                File tempFile = new File(z, "temp");

                String type = typeFile.exists() ? readSmallFile(typeFile) : null;
                String tempRaw = tempFile.exists() ? readSmallFile(tempFile) : null;

                if (type == null && tempRaw == null) continue;

                String typeLabel = (type != null) ? type.trim() : "[no type]";
                String tempLabel = "[no temp]";

                if (tempRaw != null) {
                    tempRaw = tempRaw.trim();
                    Double celsius = parseThermalTemp(tempRaw);
                    if (celsius != null && !(celsius < -10 || celsius > 150)) {
                        tempLabel = String.format("%.1f °C", celsius);
                    } else {
                        tempLabel = tempRaw;
                    }
                }

                out.append(name)
                        .append(" — ")
                        .append(typeLabel)
                        .append(" : ")
                        .append(tempLabel)
                        .append("\n");

                shown++;
                if (shown >= 24) break;
            }

            if (shown == 0) {
                out.append("No readable thermal zones\n");
            }

        } catch (Throwable t) {
            out.append("Thermal zones error: ").append(t.getMessage()).append("\n");
        }
        return out.toString();
    }

    private Double parseThermalTemp(String raw) {
        try {
            long v = Long.parseLong(raw.trim());
            if (v == 0L) return null;
            if (v > 1000L) return v / 1000.0;
            return (double) v;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isPrimaryThermal(String typeLower) {
        if (typeLower == null) return false;
        return typeLower.contains("cpu")
                || typeLower.contains("gpu")
                || typeLower.contains("battery")
                || typeLower.contains("batt")
                || typeLower.contains("xo_therm")
                || typeLower.contains("xo-therm")
                || typeLower.contains("modem")
                || typeLower.contains("pa-therm")
                || typeLower.contains("wlan")
                || typeLower.contains("wifi")
                || typeLower.contains("usbc")
                || typeLower.contains("chg")
                || typeLower.contains("charger");
    }

    // ============================================================
    // VULKAN / THERMAL PROFILES / FPS GOV
    // ============================================================
    private String readVulkanInfo() {
        StringBuilder sb = new StringBuilder();

        String vulkanHw     = getProp("ro.hardware.vulkan");
        String vulkanVendor = getProp("ro.vendor.vulkan.version");
        String vulkanSys    = getProp("ro.vulkan.version");
        String eglHw        = getProp("ro.hardware.egl");
        String gfxDriver0   = getProp("ro.gfx.driver.0");
        String gpuVendor    = getProp("ro.gfx.vendor");
        String board        = getProp("ro.board.platform");

        sb.append("Hardware tag: ").append(isEmptySafe(vulkanHw) ? "N/A" : vulkanHw).append("\n");
        sb.append("Vulkan version (vendor): ").append(isEmptySafe(vulkanVendor) ? "N/A" : vulkanVendor).append("\n");
        sb.append("Vulkan version (system): ").append(isEmptySafe(vulkanSys) ? "N/A" : vulkanSys).append("\n");
        sb.append("EGL hardware: ").append(isEmptySafe(eglHw) ? "N/A" : eglHw).append("\n");
        sb.append("GPU driver 0: ").append(isEmptySafe(gfxDriver0) ? "N/A" : gfxDriver0).append("\n");
        sb.append("GPU vendor/tag: ").append(isEmptySafe(gpuVendor) ? "N/A" : gpuVendor).append("\n");
        if (!isEmptySafe(board)) sb.append("Board platform: ").append(board).append("\n");

        return sb.toString();
    }

    private String readThermalProfiles() {
        StringBuilder sb = new StringBuilder();
        try {
            File[] roots = new File[]{
                    new File("/vendor/etc"),
                    new File("/system/etc")
            };

            boolean anyFile = false;
            for (File root : roots) {
                if (!root.exists() || !root.isDirectory()) continue;
                File[] files = root.listFiles();
                if (files == null) continue;

                for (File f : files) {
                    String name = f.getName().toLowerCase();
                    if (!name.startsWith("thermal") || !name.endsWith(".conf")) continue;

                    anyFile = true;
                    sb.append("File: ").append(f.getAbsolutePath()).append("\n");
                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        String line;
                        int printed = 0;
                        while ((line = br.readLine()) != null && printed < 30) {
                            String lower = line.toLowerCase();
                            if (lower.contains("profile")
                                    || lower.contains("trip")
                                    || lower.contains("algo")
                                    || lower.contains("sensor")
                                    || lower.contains("cluster")
                                    || lower.contains("cpu")
                                    || lower.contains("gpu")) {
                                sb.append("  ").append(line.trim()).append("\n");
                                printed++;
                            }
                        }
                    } catch (Throwable ignored) {
                        sb.append("  [error reading]\n");
                    }
                    sb.append("\n");
                }
            }

            if (!anyFile) sb.append("No thermal-engine config files found\n");

        } catch (Throwable t) {
            sb.append("Thermal profiles error: ").append(t.getMessage()).append("\n");
        }

        if (sb.length() == 0) sb.append("No thermal profile data\n");
        return sb.toString();
    }

    private String readFpsAndClocksSummary() {
        StringBuilder sb = new StringBuilder();

        String gov = readFpsGovernor();
        String clocks = readGpuClocks();

        if (isEmptySafe(gov) && isEmptySafe(clocks)) {
            sb.append("No FPS governor or GPU clock info exposed via sysfs\n");
            return sb.toString();
        }

        if (!isEmptySafe(gov)) sb.append("Governor: ").append(gov).append("\n");
        if (!isEmptySafe(clocks)) sb.append("GPU clocks: ").append(clocks).append("\n");

        return sb.toString();
    }

    private String readFpsGovernor() {
        String[] paths = {
                "/sys/class/kgsl/kgsl-3d0/devfreq/governor",
                "/sys/class/devfreq/kgsl-3d0/governor",
                "/sys/class/devfreq/gpufreq/governor"
        };
        for (String p : paths) {
            String val = readSmallFile(new File(p));
            if (!isEmptySafe(val)) return val.trim();
        }
        return "";
    }

    private String readGpuClocks() {
        try {
            String[][] pairs = {
                    {"/sys/class/kgsl/kgsl-3d0/devfreq/min_freq", "/sys/class/kgsl/kgsl-3d0/devfreq/max_freq"},
                    {"/sys/class/devfreq/kgsl-3d0/min_freq", "/sys/class/devfreq/kgsl-3d0/max_freq"},
                    {"/sys/class/devfreq/gpufreq/min_freq", "/sys/class/devfreq/gpufreq/max_freq"}
            };

            for (String[] pair : pairs) {
                String minRaw = readSmallFile(new File(pair[0]));
                String maxRaw = readSmallFile(new File(pair[1]));
                if (isEmptySafe(minRaw) || isEmptySafe(maxRaw)) continue;

                double minMhz = toMhz(minRaw.trim());
                double maxMhz = toMhz(maxRaw.trim());
                if (minMhz <= 0 || maxMhz <= 0) continue;

                return String.format("%.0f–%.0f MHz", minMhz, maxMhz);
            }
        } catch (Throwable ignored) {}
        return "";
    }

    private double toMhz(String raw) {
        try {
            long v = Long.parseLong(raw);
            if (v <= 0) return -1;
            if (v > 100_000_000L) return v / 1_000_000.0; // Hz → MHz
            if (v > 100_000L) return v / 1_000.0;         // kHz → MHz
            return (double) v;                            // MHz
        } catch (Exception e) {
            return -1;
        }
    }

    private String readSmallFile(File f) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            return br.readLine();
        } catch (Exception e) {
            return null;
        } finally {
            try { if (br != null) br.close(); } catch (Exception ignored) {}
        }
    }

    // ============================================================
    // CONNECTIVITY (v5.0 FULL MAP)
    // ============================================================
    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("── CONNECTIVITY ──\n");

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // ACTIVE NETWORK
        NetworkCapabilities caps = null;
        try {
            if (cm == null) {
                sb.append("Active: [no ConnectivityManager]\n");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network active = cm.getActiveNetwork();
                if (active != null) caps = cm.getNetworkCapabilities(active);

                if (caps == null) sb.append("Active: NONE\n");
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) sb.append("Active: Wi-Fi\n");
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) sb.append("Active: Mobile Data\n");
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) sb.append("Active: Ethernet\n");
                else sb.append("Active: Other\n");
            } else {
                //noinspection deprecation
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) sb.append("Active: ").append(ni.getTypeName()).append("\n");
                else sb.append("Active: NONE\n");
            }
        } catch (Throwable t) {
            sb.append("Active: Unknown\n");
        }

        // METERED
        try {
            if (cm != null) {
                boolean metered = cm.isActiveNetworkMetered();
                sb.append("Metered: ").append(metered ? "YES" : "NO").append("\n");
            } else {
                sb.append("Metered: Unknown\n");
            }
        } catch (Throwable t) {
            sb.append("Metered: Unknown\n");
        }

        // ===== Wi-Fi =====
        sb.append("\n[Wi-Fi]\n");
        try {
            if (wm == null) {
                sb.append("Wi-Fi manager unavailable\n");
            } else {
                boolean wifiActive = (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                WifiInfo wi = wm.getConnectionInfo();

                if (wifiActive) {
                    sb.append("State: CONNECTED\n");

                    String ssid = (wi != null) ? wi.getSSID() : null;
                    if (ssid == null || ssid.equals("<unknown ssid>") || ssid.equals("0x")) {
                        ssid = "[restricted on Android 12+]";
                    }

                    String bssid = (wi != null) ? wi.getBSSID() : null;
                    if (bssid == null) bssid = "[restricted]";

                    sb.append("SSID: ").append(ssid).append("\n");
                    sb.append("BSSID: ").append(bssid).append("\n");

                    int speed = (wi != null) ? wi.getLinkSpeed() : -1;
                    sb.append("Link speed: ").append(speed > 0 ? speed + " Mbps" : "n/a").append("\n");

                    int freq = (wi != null) ? wi.getFrequency() : -1;
                    sb.append("Band: ").append(freq > 0 ? describeWifiBand(freq) : "Unknown").append("\n");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && wi != null) {
                        sb.append("Standard: ").append(describeWifiStandard(wi.getWifiStandard())).append("\n");
                    } else {
                        sb.append("Standard: Legacy (Android <11)\n");
                    }

                } else {
                    sb.append("State: NOT CONNECTED\n");
                }
            }
        } catch (Throwable t) {
            sb.append("Wi-Fi error: ").append(t.getMessage()).append("\n");
        }

        // ===== BLUETOOTH =====
        sb.append("\n[Bluetooth]\n");
        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bt = (bm != null) ? bm.getAdapter() : BluetoothAdapter.getDefaultAdapter();

            if (bt == null) {
                sb.append("Adapter: Not available\n");
            } else {
                sb.append("Enabled: ").append(bt.isEnabled() ? "YES" : "NO").append("\n");
                try {
                    sb.append("Name: ").append(bt.getName()).append("\n");
                } catch (SecurityException se) {
                    sb.append("Name: [permission denied]\n");
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    sb.append("Address: ").append(bt.getAddress()).append("\n");
                } else {
                    sb.append("Address: [hidden on Android 12+]\n");
                }
            }
        } catch (Throwable t) {
            sb.append("Bluetooth error: ").append(t.getMessage()).append("\n");
        }

        // ===== NFC =====
        sb.append("\n[NFC]\n");
        try {
            NfcManager nfcMgr = (NfcManager) getSystemService(Context.NFC_SERVICE);
            NfcAdapter nfc = (nfcMgr != null) ? nfcMgr.getDefaultAdapter() : null;

            if (nfc == null) sb.append("State: Not available\n");
            else sb.append("State: ").append(nfc.isEnabled() ? "ON" : "OFF").append("\n");
        } catch (Throwable t) {
            sb.append("State: Unknown\n");
        }

        // ===== AIRPLANE / HOTSPOT =====
        try {
            boolean airplaneOn = Settings.Global.getInt(
                    getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
            sb.append("\nAirplane mode: ").append(airplaneOn ? "ON" : "OFF").append("\n");
        } catch (Exception e) {
            sb.append("\nAirplane mode: Unknown\n");
        }

        try {
            int hotspot = Settings.Global.getInt(getContentResolver(), "tether_dun_required", 0);
            sb.append("Hotspot: State ").append(hotspot).append("\n");
        } catch (Exception e) {
            sb.append("Hotspot: Unknown\n");
        }

        // ===== MOBILE RADIO (FULL DETAIL) =====
        appendMobileRadioInfo(sb);

        return sb.toString();
    }

    private void appendMobileRadioInfo(StringBuilder sb) {
        sb.append("\n[Mobile Radio]\n");
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm == null) {
                sb.append("TelephonyManager: [unavailable]\n");
                return;
            }

            sb.append("SIM state: ").append(tm.getSimState()).append("\n");
            sb.append("Operator: ").append(tm.getNetworkOperatorName()).append("\n");
            sb.append("Roaming: ").append(tm.isNetworkRoaming() ? "YES" : "NO").append("\n");

            int dataType = tm.getDataNetworkType();
            sb.append("Data network: ").append(networkTypeToString(dataType)).append("\n");

            appendCellInfoDetails(sb, tm);

        } catch (Throwable t) {
            sb.append("Mobile radio error: ").append(t.getMessage()).append("\n");
        }
    }

    @SuppressWarnings("MissingPermission")
    private void appendCellInfoDetails(StringBuilder sb, TelephonyManager tm) {
        try {
            List<CellInfo> cells = tm.getAllCellInfo();
            if (cells == null || cells.isEmpty()) {
                sb.append("Cells: [no cell info or permission]\n");
                return;
            }

            int regLte = 0;
            int regNr = 0;

            for (CellInfo ci : cells) {
                if (ci == null) continue;

                boolean registered = ci.isRegistered();
                if (ci instanceof CellInfoLte) {
                    CellInfoLte lte = (CellInfoLte) ci;
                    CellIdentityLte id = lte.getCellIdentity();

                    int earfcn = (id != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            ? id.getEarfcn() : -1;
                    String band = (earfcn >= 0) ? lteBandFromEarfcn(earfcn) : "Unknown band";

                    if (registered) regLte++;

                    sb.append("LTE cell")
                            .append(registered ? " [REG] " : " ")
                            .append(": EARFCN=")
                            .append(earfcn >= 0 ? earfcn : "[n/a]")
                            .append(" → Band ")
                            .append(band)
                            .append("\n");
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ci instanceof CellInfoNr) {
                    CellInfoNr nr = (CellInfoNr) ci;
                    CellIdentity id = nr.getCellIdentity();

                    long nrarfcn = -1L;
                    if (id instanceof CellIdentityNr) {
                        nrarfcn = ((CellIdentityNr) id).getNrarfcn();
                    }
                    String band = (nrarfcn >= 0) ? nrBandFromNrarfcn(nrarfcn) : "Unknown band";

                    if (registered) regNr++;

                    sb.append("NR cell")
                            .append(registered ? " [REG] " : " ")
                            .append(": NR-ARFCN=")
                            .append(nrarfcn >= 0 ? nrarfcn : "[n/a]")
                            .append(" → Band ")
                            .append(band)
                            .append("\n");
                }
            }

            if (regLte > 1) {
                sb.append("LTE CA: probable YES (")
                        .append(regLte)
                        .append(" registered LTE carriers)\n");
            } else {
                sb.append("LTE CA: not detected (single LTE carrier)\n");
            }

            if (regNr > 0) {
                sb.append("5G NR: ACTIVE (")
                        .append(regNr)
                        .append(" registered NR cells)\n");
            }

        } catch (SecurityException se) {
            sb.append("Cells: permission denied (needs location)\n");
        } catch (Throwable t) {
            sb.append("Cells error: ").append(t.getMessage()).append("\n");
        }
    }

    // ============================================================
    // WIFI / RAT / BANDS HELPERS
    // ============================================================
    private String describeWifiStandard(int std) {
        switch (std) {
            case 1:  return "Legacy (11a/b/g)";
            case 4:  return "Wi-Fi 4 (11n)";
            case 5:  return "Wi-Fi 5 (11ac)";
            case 6:  return "Wi-Fi 6/6E (11ax)";
            case 7:  return "Wi-Fi 7 (11be)";
            default: return "Unknown";
        }
    }

    private String describeWifiBand(int freqMHz) {
        if (freqMHz >= 2400 && freqMHz < 2500) return "2.4 GHz";
        if (freqMHz >= 4900 && freqMHz < 5900) return "5 GHz";
        if (freqMHz >= 5925 && freqMHz < 7125) return "6 GHz";
        return "Unknown";
    }

    private String networkTypeToString(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G / LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G NR";
            default:
                return "Unknown(" + type + ")";
        }
    }

    private String lteBandFromEarfcn(int earfcn) {
        if (earfcn < 0) return "Unknown";
        if (earfcn >= 0 && earfcn <= 599)      return "1 (2100 MHz)";
        if (earfcn >= 1200 && earfcn <= 1949) return "3 (1800 MHz)";
        if (earfcn >= 2750 && earfcn <= 3449) return "7 (2600 MHz)";
        if (earfcn >= 6150 && earfcn <= 6449) return "20 (800 MHz)";
        if (earfcn >= 9210 && earfcn <= 9659) return "28 (700 MHz)";
        return "Unknown (EARFCN " + earfcn + ")";
    }

    private String nrBandFromNrarfcn(long nrarfcn) {
        if (nrarfcn < 0) return "Unknown";
        if (nrarfcn >= 422000 && nrarfcn <= 434000) return "n3 (1800 MHz)";
        if (nrarfcn >= 151600 && nrarfcn <= 160600) return "n28 (700 MHz)";
        if (nrarfcn >= 620000 && nrarfcn <= 653333) return "n78 (3.5 GHz)";
        if (nrarfcn >= 693334 && nrarfcn <= 733333) return "n79 (4.7 GHz)";
        return "Unknown (NR-ARFCN " + nrarfcn + ")";
    }

    // ============================================================
    // ROOT UTILS
    // ============================================================
    private boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;

        String[] paths = { "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su" };
        for (String p : paths) if (new File(p).exists()) return true;

        String debuggable = getProp("ro.debuggable");
        String secure = getProp("ro.secure");
        return "1".equals(debuggable) || "0".equals(secure);
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getSelinux() {
        try {
            Process p = Runtime.getRuntime().exec("getenforce");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String checkSuPaths() {
        String[] paths = { "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su" };
        for (String p : paths) if (new File(p).exists()) return p;
        return "none";
    }
}
