// GDiolitsis Engine Lab (GEL) — Author & Developer
// DeviceInfoInternalActivity.java — GEL FINAL v5.1 (Foldable Unified Edition)
// NOTE: Δουλεύω ΠΑΝΩ στο τελευταίο αρχείο σου — ποτέ πίσω.

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
import android.telephony.*;
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

public class DeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private boolean isRooted = false;

    // Foldable system
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager foldUI;

    // One-open-only logic
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

        // FOLDABLE INIT
        foldUI = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // TITLE
        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) title.setText(getString(R.string.phone_info_internal));

        // ---------------------------------------------
        // CONTENT REFERENCES
        // ---------------------------------------------
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

        // ICON REFERENCES
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

        // ONE-OPEN-ONLY ARRAYS
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

        // ROOT CHECK
        isRooted = isDeviceRooted();

        // ----------------------------------------
        // ALL 14 SECTIONS  (UNCHANGED — SAFE)
        // ----------------------------------------
        // NOTE: All original blocks remain EXACTLY as your file.
        // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

        // ===============================
        // SYSTEM
        // ===============================
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

        // ===============================
        // ANDROID / OS
        // ===============================
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

        // ===============================
        // CPU
        // ===============================
        if (txtCpuContent != null) {
            StringBuilder cpu = new StringBuilder();
            cpu.append("── CPU ──\n");

            String[] abis = Build.SUPPORTED_ABIS;
            if (abis != null && abis.length > 0)
                cpu.append("Primary ABI: ").append(abis[0]).append("\n");
            else
                cpu.append("Primary ABI: N/A\n");

            cpu.append("Supported ABIs: ");
            if (abis != null) {
                for (int i = 0; i < abis.length; i++) {
                    cpu.append(abis[i]);
                    if (i < abis.length - 1) cpu.append(", ");
                }
            } else cpu.append("N/A");
            cpu.append("\n");

            cpu.append("Logical Cores: ")
                    .append(Runtime.getRuntime().availableProcessors())
                    .append("\n\n");

            cpu.append("CPU Info (/proc/cpuinfo):\n");
            cpu.append(readCpuInfo()).append("\n");

            txtCpuContent.setText(cpu.toString());
        }

        // ===============================
        // GPU
        // ===============================
        if (txtGpuContent != null) {
            StringBuilder gpu = new StringBuilder();
            gpu.append("── GPU INFO ──\n");
            gpu.append(readGpuAdvanced());
            txtGpuContent.setText(gpu.toString());
        }

        // ===============================
        // THERMAL
        // ===============================
        if (txtThermalContent != null) {
            StringBuilder thermal = new StringBuilder();
            thermal.append("── THERMAL SENSORS ──\n");
            thermal.append(readThermalPrimary());
            txtThermalContent.setText(thermal.toString());
        }

        // ===============================
        // THERMAL ZONES
        // ===============================
        if (txtThermalZonesContent != null) {
            StringBuilder thermalZones = new StringBuilder();
            thermalZones.append("── THERMAL ZONES ──\n");
            thermalZones.append(readThermalZonesFull());
            txtThermalZonesContent.setText(thermalZones.toString());
        }

        // ===============================
        // VULKAN
        // ===============================
        if (txtVulkanContent != null) {
            StringBuilder vk = new StringBuilder();
            vk.append("── VULKAN / GPU DRIVER ──\n");
            vk.append(readVulkanInfo());
            txtVulkanContent.setText(vk.toString());
        }

        // ===============================
        // THERMAL PROFILES
        // ===============================
        if (txtThermalProfilesContent != null) {
            StringBuilder thermProfiles = new StringBuilder();
            thermProfiles.append("── THERMAL ENGINE PROFILES ──\n");
            thermProfiles.append(readThermalProfiles());
            txtThermalProfilesContent.setText(thermProfiles.toString());
        }

        // ===============================
        // FPS + CLOCKS
        // ===============================
        if (txtFpsGovernorContent != null) {
            StringBuilder fpsGov = new StringBuilder();
            fpsGov.append("── FPS / GPU GOVERNOR & CLOCKS ──\n");
            fpsGov.append(readFpsAndClocksSummary());
            txtFpsGovernorContent.setText(fpsGov.toString());
        }

        // ===============================
        // RAM
        // ===============================
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

        // ===============================
        // INTERNAL STORAGE
        // ===============================
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

        // ===============================
        // SCREEN
        // ===============================
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
                    refresh = getWindowManager().getDefaultDisplay().getRefreshRate();
                }
                sc.append("Refresh rate: ").append(String.format("%.1f", refresh)).append(" Hz\n");
            } catch (Throwable ignored) {}

            txtScreenContent.setText(sc.toString());
        }

        // ===============================
        // CONNECTIVITY
        // ===============================
        if (txtConnectivityContent != null) {
            txtConnectivityContent.setText(buildConnectivityInfo());
        }

        // ===============================
        // ROOT
        // ===============================
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

        // ===============================
        // EXPANDABLE HEADERS
        // ===============================
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
    // FOLDABLE CALLBACK — UPDATED to GELFoldablePosture
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull GELFoldablePosture posture) {
        // Future hinge-based animations here
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (foldUI != null) foldUI.applyUI(isInner);
    }

    // ============================================================
    // ONE-OPEN-ONLY
    // ============================================================
    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {
        if (allContents == null || allIcons == null) return;

        for (int i = 0; i < allContents.length; i++) {
            TextView c  = allContents[i];
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
    // HELPERS (UNCHANGED)
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

    private String readGpuAdvanced() {
        StringBuilder sb = new StringBuilder();

        String soc          = getProp("ro.board.platform");
        String egl          = getProp("ro.hardware.egl");
        String glEs         = getProp("ro.opengles.version");
        String gfxDriver0   = getProp("ro.gfx.driver.0");
        String gfxVendor    = getProp("ro.gfx.vendor");
        String hwuiRenderer = getProp("debug.hwui.renderer");
        String hwuiVendor   = getProp("debug.hwui.vendor");

        sb.append("SoC / board: ").append(empty(soc)).append("\n");
        sb.append("EGL hardware: ").append(empty(egl)).append("\n");
        sb.append("OpenGL ES: ").append(empty(glEs)).append("\n");
        sb.append("GPU driver: ").append(empty(gfxDriver0)).append("\n");
        sb.append("GPU vendor: ").append(empty(gfxVendor)).append("\n");
        sb.append("HWUI renderer: ").append(empty(hwuiRenderer)).append("\n");
        sb.append("HWUI vendor: ").append(empty(hwuiVendor)).append("\n");

        String gpuFamily = detectGpuFamily(egl, gfxVendor, soc);
        if (!gpuFamily.isEmpty()) sb.append("GPU family: ").append(gpuFamily).append("\n");

        String clocks = readGpuClocks();
        if (!clocks.isEmpty()) sb.append("Base GPU clocks: ").append(clocks).append("\n");

        return sb.toString();
    }

    private String empty(String s) {
        return (s == null || s.trim().isEmpty()) ? "N/A" : s;
    }

    private String detectGpuFamily(String egl, String gfxVendor, String soc) {
        StringBuilder all = new StringBuilder();
        if (egl != null) all.append(egl).append(" ");
        if (gfxVendor != null) all.append(gfxVendor).append(" ");
        if (soc != null) all.append(soc);

        String s = all.toString().toLowerCase();
        if (s.contains("adreno")) return "Qualcomm Adreno";
        if (s.contains("mali")) return "ARM Mali";
        if (s.contains("powervr")) return "PowerVR (Imagination)";
        if (s.contains("apple")) return "Apple GPU";
        if (s.contains("intel")) return "Intel GPU";
        return "";
    }

    private String readThermalPrimary() {
        StringBuilder out = new StringBuilder();
        try {
            File base = new File("/sys/class/thermal");
            if (!base.exists()) return "Thermal info: not available\n";

            File[] zones = base.listFiles();
            if (zones == null) return "Thermal zones: none\n";

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

                primary.add(type + " : " + String.format("%.1f", celsius) + " °C");
            }

            if (primary.isEmpty()) return "No primary thermal sensors readable\n";

            for (String s : primary) out.append("• ").append(s).append("\n");

        } catch (Throwable t) {
            out.append("Thermal info error: ").append(t.getMessage()).append("\n");
        }
        return out.toString();
    }

    private Double parseThermalTemp(String raw) {
        try {
            long v = Long.parseLong(raw);
            if (v > 1000) return v / 1000.0;
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
                || typeLower.contains("modem")
                || typeLower.contains("pa-therm")
                || typeLower.contains("xo");
    }

    private String readThermalZonesFull() {
        StringBuilder out = new StringBuilder();
        try {
            File base = new File("/sys/class/thermal");
            if (!base.exists()) return "Thermal info: not available\n";

            File[] zones = base.listFiles();
            if (zones == null) return "No thermal zones\n";

            int shown = 0;
            for (File z : zones) {
                if (!z.getName().startsWith("thermal_zone")) continue;

                File typeFile = new File(z, "type");
                File tempFile = new File(z, "temp");

                String type = typeFile.exists() ? readSmallFile(typeFile) : null;
                String tempRaw = tempFile.exists() ? readSmallFile(tempFile) : null;

                if (type == null && tempRaw == null) continue;

                String typeLabel = (type != null) ? type.trim() : "[no type]";
                String tempLabel = "[no temp]";

                if (tempRaw != null) {
                    Double c = parseThermalTemp(tempRaw.trim());
                    if (c != null) tempLabel = String.format("%.1f °C", c);
                }

                out.append(z.getName()).append(" — ").append(typeLabel)
                        .append(" : ").append(tempLabel).append("\n");

                shown++;
                if (shown >= 24) break;
            }

            if (shown == 0) return "No readable thermal zones\n";

        } catch (Throwable t) {
            out.append("Thermal zones error: ").append(t.getMessage()).append("\n");
        }
        return out.toString();
    }

    private String readVulkanInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Hardware tag: ").append(empty(getProp("ro.hardware.vulkan"))).append("\n");
        sb.append("Vulkan version (vendor): ").append(empty(getProp("ro.vendor.vulkan.version"))).append("\n");
        sb.append("Vulkan version (system): ").append(empty(getProp("ro.vulkan.version"))).append("\n");
        sb.append("EGL hardware: ").append(empty(getProp("ro.hardware.egl"))).append("\n");
        sb.append("GPU driver 0: ").append(empty(getProp("ro.gfx.driver.0"))).append("\n");
        sb.append("GPU vendor/tag: ").append(empty(getProp("ro.gfx.vendor"))).append("\n");
        sb.append("Board platform: ").append(empty(getProp("ro.board.platform"))).append("\n");

        return sb.toString();
    }

    private String readThermalProfiles() {
        StringBuilder sb = new StringBuilder();
        try {
            File[] roots = { new File("/vendor/etc"), new File("/system/etc") };
            boolean found = false;

            for (File root : roots) {
                if (!root.exists()) continue;
                File[] files = root.listFiles();
                if (files == null) continue;

                for (File f : files) {
                    String n = f.getName().toLowerCase();
                    if (!n.startsWith("thermal") || !n.endsWith(".conf")) continue;

                    found = true;
                    sb.append("File: ").append(f.getAbsolutePath()).append("\n");

                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        String line;
                        int c = 0;
                        while ((line = br.readLine()) != null && c < 30) {
                            String low = line.toLowerCase();
                            if (low.contains("profile") || low.contains("sensor") || low.contains("gpu"))
                            {
                                sb.append("  ").append(line.trim()).append("\n");
                                c++;
                            }
                        }
                    } catch (Throwable ignored) {}

                    sb.append("\n");
                }
            }

            if (!found) sb.append("No thermal-engine config files found\n");

        } catch (Throwable t) {
            sb.append("Thermal profiles error: ").append(t.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private String readFpsAndClocksSummary() {
        StringBuilder sb = new StringBuilder();

        String gov = readFpsGovernor();
        String clocks = readGpuClocks();

        if (gov.isEmpty() && clocks.isEmpty()) {
            return "No FPS governor or GPU clock info exposed\n";
        }

        if (!gov.isEmpty()) sb.append("Governor: ").append(gov).append("\n");
        if (!clocks.isEmpty()) sb.append("GPU clocks: ").append(clocks).append("\n");

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
            if (val != null && !val.trim().isEmpty()) return val.trim();
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

            for (String[] p : pairs) {
                String minRaw = readSmallFile(new File(p[0]));
                String maxRaw = readSmallFile(new File(p[1]));
                if (minRaw == null || maxRaw == null) continue;

                double min = toMhz(minRaw.trim());
                double max = toMhz(maxRaw.trim());
                if (min > 0 && max > 0)
                    return String.format("%.0f–%.0f MHz", min, max);
            }
        } catch (Throwable ignored) {}

        return "";
    }

    private double toMhz(String raw) {
        try {
            long v = Long.parseLong(raw);
            if (v > 100_000_000L) return v / 1_000_000.0; 
            if (v > 100_000L)     return v / 1_000.0;
            return v;
        } catch (Exception e) {
            return -1;
        }
    }

    private String readSmallFile(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private String buildConnectivityInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("── CONNECTIVITY ──\n");

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // ACTIVE
        NetworkCapabilities caps = null;
        try {
            if (cm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network active = cm.getActiveNetwork();
                if (active != null) caps = cm.getNetworkCapabilities(active);

                if (caps == null) sb.append("Active: NONE\n");
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) sb.append("Active: Wi-Fi\n");
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) sb.append("Active: Mobile Data\n");
                else sb.append("Active: Other\n");
            } else sb.append("Active: Unknown\n");
        } catch (Throwable t) {
            sb.append("Active: Unknown\n");
        }

        // METERED
        try {
            if (cm != null)
                sb.append("Metered: ").append(cm.isActiveNetworkMetered() ? "YES" : "NO").append("\n");
            else sb.append("Metered: Unknown\n");
        } catch (Throwable ignored) {}

        // WIFI
        sb.append("\n[Wi-Fi]\n");
        try {
            if (wm != null) {
                boolean wifiActive = (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                WifiInfo wi = wm.getConnectionInfo();

                if (wifiActive) {
                    sb.append("State: CONNECTED\n");

                    String ssid = (wi != null) ? wi.getSSID() : "[unknown]";
                    if (ssid.equals("<unknown ssid>")) ssid = "[restricted]";
                    sb.append("SSID: ").append(ssid).append("\n");

                    String bssid = (wi != null) ? wi.getBSSID() : "[restricted]";
                    sb.append("BSSID: ").append(bssid).append("\n");

                    sb.append("Link speed: ").append(wi != null ? wi.getLinkSpeed() : -1).append(" Mbps\n");
                    int freq = (wi != null) ? wi.getFrequency() : -1;
                    sb.append("Band: ").append(describeWifiBand(freq)).append("\n");

                } else sb.append("State: NOT CONNECTED\n");
            }
        } catch (Throwable ignored) {}

        // BLUETOOTH
        sb.append("\n[Bluetooth]\n");
        try {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bt = (bm != null) ? bm.getAdapter() : BluetoothAdapter.getDefaultAdapter();
            if (bt != null) {
                sb.append("Enabled: ").append(bt.isEnabled() ? "YES" : "NO").append("\n");
                sb.append("Name: ").append(bt.getName()).append("\n");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
                    sb.append("Address: ").append(bt.getAddress()).append("\n");
                else sb.append("Address: [hidden]\n");
            } else sb.append("Adapter: Not available\n");
        } catch (Throwable ignored) {}

        // NFC
        sb.append("\n[NFC]\n");
        try {
            NfcManager nfcMgr = (NfcManager) getSystemService(Context.NFC_SERVICE);
            NfcAdapter nfc = (nfcMgr != null) ? nfcMgr.getDefaultAdapter() : null;
            sb.append("State: ").append(nfc != null && nfc.isEnabled() ? "ON" : "OFF").append("\n");
        } catch (Throwable ignored) {}

        // AIRPLANE
        try {
            boolean airplaneOn =
                    Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
            sb.append("\nAirplane mode: ").append(airplaneOn ? "ON" : "OFF").append("\n");
        } catch (Throwable ignored) {}

        // HOTSPOT
        try {
            int hotspot = Settings.Global.getInt(getContentResolver(), "tether_dun_required", 0);
            sb.append("Hotspot: State ").append(hotspot).append("\n");
        } catch (Throwable ignored) {}

        // MOBILE RADIO
        appendMobileRadioInfo(sb);

        return sb.toString();
    }

    private void appendMobileRadioInfo(StringBuilder sb) {
        sb.append("\n[Mobile Radio]\n");
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm == null) {
                sb.append("TelephonyManager: unavailable\n");
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

    private void appendCellInfoDetails(StringBuilder sb, TelephonyManager tm) {
        try {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                sb.append("Cells: permission denied (needs location)\n");
                return;
            }

            List<CellInfo> cells = tm.getAllCellInfo();
            if (cells == null || cells.isEmpty()) {
                sb.append("Cells: [no cell info]\n");
                return;
            }

            int regLte = 0;
            int regNr = 0;

            for (CellInfo ci : cells) {
                boolean reg = ci.isRegistered();

                if (ci instanceof CellInfoLte) {
                    CellInfoLte l = (CellInfoLte) ci;
                    CellIdentityLte id = l.getCellIdentity();

                    int earfcn = (id != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            ? id.getEarfcn() : -1;
                    String band = (earfcn >= 0) ? lteBandFromEarfcn(earfcn) : "Unknown";

                    if (reg) regLte++;

                    sb.append("LTE cell").append(reg ? " [REG] " : " ")
                            .append(": EARFCN=").append(earfcn)
                            .append(" → Band ").append(band)
                            .append("\n");
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ci instanceof CellInfoNr) {
                    CellInfoNr nr = (CellInfoNr) ci;
                    CellIdentity id = nr.getCellIdentity();

                    long nrarfcn = -1;
                    if (id instanceof CellIdentityNr)
                        nrarfcn = ((CellIdentityNr) id).getNrarfcn();

                    String band = (nrarfcn >= 0) ? nrBandFromNrarfcn(nrarfcn) : "Unknown";

                    if (reg) regNr++;

                    sb.append("NR cell").append(reg ? " [REG] " : " ")
                            .append(": NR-ARFCN=").append(nrarfcn)
                            .append(" → Band ").append(band)
                            .append("\n");
                }
            }

            if (regLte > 1)
                sb.append("LTE CA: probable YES (").append(regLte).append(" carriers)\n");
            else
                sb.append("LTE CA: not detected\n");

            if (regNr > 0)
                sb.append("5G NR: ACTIVE (").append(regNr).append(" cells)\n");

        } catch (Throwable t) {
            sb.append("Cells error: ").append(t.getMessage()).append("\n");
        }
    }

    private String networkTypeToString(int t) {
        switch (t) {
            case TelephonyManager.NETWORK_TYPE_LTE: return "4G / LTE";
            case TelephonyManager.NETWORK_TYPE_NR: return "5G NR";
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_UMTS: return "3G";
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM: return "2G";
        }
        return "Unknown(" + t + ")";
    }

    private String lteBandFromEarfcn(int f) {
        if (f <= 599) return "1 (2100MHz)";
        if (f >= 1200 && f <= 1949) return "3 (1800MHz)";
        if (f >= 2750 && f <= 3449) return "7 (2600MHz)";
        if (f >= 6150 && f <= 6449) return "20 (800MHz)";
        if (f >= 9210 && f <= 9659) return "28 (700MHz)";
        return "Unknown (EARFCN " + f + ")";
    }

    private String nrBandFromNrarfcn(long f) {
        if (f >= 422000 && f <= 434000) return "n3 (1800MHz)";
        if (f >= 151600 && f <= 160600) return "n28 (700MHz)";
        if (f >= 620000 && f <= 653333) return "n78 (3.5GHz)";
        if (f >= 693334 && f <= 733333) return "n79 (4.7GHz)";
        return "Unknown (NR-ARFCN " + f + ")";
    }

    // ROOT
    private boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;

        String[] paths = { "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su" };
        for (String p : paths) if (new File(p).exists()) return true;

        return "1".equals(getProp("ro.debuggable"))
                || "0".equals(getProp("ro.secure"));
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
