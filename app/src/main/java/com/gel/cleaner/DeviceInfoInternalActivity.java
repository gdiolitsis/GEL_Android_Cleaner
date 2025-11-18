package com.gel.cleaner;

// FULL UPGRADE MODE — 7 SECTIONS + 3 NEW + ROOT MODE
// NOTE: Ολόκληρο κελί έτοιμο για copy-paste. Μην σπας κομμάτια, δούλευε πάντα πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class DeviceInfoInternalActivity extends AppCompatActivity {

    private boolean isRooted = false;

    // για το "άνοιγε μόνο ένα-ένα"
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

        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) {
            title.setText(getString(R.string.phone_info_internal));
        }

        // ============================
        // REFERENCES (ALL 10 SECTIONS)
        // ============================
        TextView txtSystemContent       = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent      = findViewById(R.id.txtAndroidContent);
        TextView txtCpuContent          = findViewById(R.id.txtCpuContent);
        TextView txtGpuContent          = findViewById(R.id.txtGpuContent);          // NEW
        TextView txtThermalContent      = findViewById(R.id.txtThermalContent);      // NEW
        TextView txtRamContent          = findViewById(R.id.txtRamContent);
        TextView txtStorageContent      = findViewById(R.id.txtStorageContent);
        TextView txtScreenContent       = findViewById(R.id.txtScreenContent);
        TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent); // NEW
        TextView txtRootContent         = findViewById(R.id.txtRootContent);

        // ICONS
        TextView iconSystem       = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid      = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu          = findViewById(R.id.iconCpuToggle);
        TextView iconGpu          = findViewById(R.id.iconGpuToggle);
        TextView iconThermal      = findViewById(R.id.iconThermalToggle);
        TextView iconRam          = findViewById(R.id.iconRamToggle);
        TextView iconStorage      = findViewById(R.id.iconStorageToggle);
        TextView iconScreen       = findViewById(R.id.iconScreenToggle);
        TextView iconConnectivity = findViewById(R.id.iconConnectivityToggle);
        TextView iconRoot         = findViewById(R.id.iconRootToggle);

        // για το "μόνο ένα ανοιχτό κάθε φορά"
        allContents = new TextView[]{
                txtSystemContent,
                txtAndroidContent,
                txtCpuContent,
                txtGpuContent,
                txtThermalContent,
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
                iconRam,
                iconStorage,
                iconScreen,
                iconConnectivity,
                iconRoot
        };

        // ROOT detection
        isRooted = isDeviceRooted();

        // ===========================
        // SYSTEM / HARDWARE
        // ===========================
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
        } catch (Throwable ignored) {
            // όχι κρίσιμο
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sys.append("SoC Manufacturer: ").append(Build.SOC_MANUFACTURER).append("\n");
            sys.append("SoC Model: ").append(Build.SOC_MODEL).append("\n");
        }
        sys.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n");
        sys.append("Build Time: ").append(Build.TIME).append(" ms\n");
        txtSystemContent.setText(sys.toString());

        // ===========================
        // ANDROID / OS
        // ===========================
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

        // ===========================
        // CPU
        // ===========================
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

        // ===========================
        // GPU (BEST-EFFORT)
        // ===========================
        StringBuilder gpu = new StringBuilder();
        gpu.append("── GPU ──\n");

        String egl = getProp("ro.hardware.egl");
        String glVersion = getProp("ro.opengles.version");
        String renderer = getProp("ro.gfx.driver.0");

        gpu.append("EGL / GPU: ")
                .append(isEmptySafe(egl) ? "N/A" : egl)
                .append("\n");

        gpu.append("OpenGL ES (ro.opengles.version): ")
                .append(isEmptySafe(glVersion) ? "N/A" : glVersion)
                .append("\n");

        gpu.append("GPU Driver: ")
                .append(isEmptySafe(renderer) ? "N/A" : renderer)
                .append("\n");

        txtGpuContent.setText(gpu.toString());

        // ===========================
        // THERMAL (BEST-EFFORT)
        // ===========================
        StringBuilder thermal = new StringBuilder();
        thermal.append("── THERMAL ──\n");
        thermal.append(readThermalSummary());
        txtThermalContent.setText(thermal.toString());

        // ===========================
        // RAM
        // ===========================
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

        // ===========================
        // INTERNAL STORAGE
        // ===========================
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

        // ===========================
        // SCREEN / DISPLAY
        // ===========================
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

        // Υπολογισμός περίπου διαγωνίου
        try {
            float xInches = dm.widthPixels / dm.xdpi;
            float yInches = dm.heightPixels / dm.ydpi;
            double diagonal = Math.sqrt(xInches * xInches + yInches * yInches);
            sc.append("Diagonal: ").append(String.format("%.1f", diagonal)).append(" inches\n");
        } catch (Throwable ignored) {
        }

        // Refresh rate
        try {
            float refresh = getWindowManager().getDefaultDisplay().getRefreshRate();
            sc.append("Refresh rate: ").append(String.format("%.1f", refresh)).append(" Hz\n");
        } catch (Throwable ignored) {
        }

        txtScreenContent.setText(sc.toString());

        // ===========================
        // CONNECTIVITY
        // ===========================
        StringBuilder conn = new StringBuilder();
        conn.append("── CONNECTIVITY ──\n");

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (caps != null) {
                    boolean wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    boolean cellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    boolean ethernet = caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

                    conn.append("Active: ");
                    if (wifi) conn.append("Wi-Fi ");
                    if (cellular) conn.append("Mobile ");
                    if (ethernet) conn.append("Ethernet ");
                    if (!wifi && !cellular && !ethernet) conn.append("None");
                    conn.append("\n");

                    conn.append("Metered: ")
                            .append(cm.isActiveNetworkMetered() ? "YES" : "NO")
                            .append("\n");
                } else {
                    conn.append("Active network: None\n");
                }
            } else {
                @SuppressWarnings("deprecation")
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    conn.append("Active type: ").append(ni.getTypeName()).append("\n");
                    conn.append("Roaming: ").append(ni.isRoaming() ? "YES" : "NO").append("\n");
                } else {
                    conn.append("Active network: None\n");
                }
            }
        } else {
            conn.append("Connectivity info: N/A\n");
        }

        txtConnectivityContent.setText(conn.toString());

        // ===========================
        // ROOT EXTRAS
        // ===========================
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

        // ===========================
        // EXPANDABLE HEADERS
        // ===========================
        setupSection(findViewById(R.id.headerSystem),       txtSystemContent,        iconSystem);
        setupSection(findViewById(R.id.headerAndroid),      txtAndroidContent,       iconAndroid);
        setupSection(findViewById(R.id.headerCpu),          txtCpuContent,           iconCpu);
        setupSection(findViewById(R.id.headerGpu),          txtGpuContent,           iconGpu);
        setupSection(findViewById(R.id.headerThermal),      txtThermalContent,       iconThermal);
        setupSection(findViewById(R.id.headerRam),          txtRamContent,           iconRam);
        setupSection(findViewById(R.id.headerStorage),      txtStorageContent,       iconStorage);
        setupSection(findViewById(R.id.headerScreen),       txtScreenContent,        iconScreen);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent,  iconConnectivity);
        setupSection(findViewById(R.id.headerRoot),         txtRootContent,          iconRoot);
    }

    // ===========================
    // ONE-OPEN-ONLY LOGIC
    // ===========================
    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView toOpen, TextView iconToUpdate) {
        if (allContents == null || allIcons == null) return;

        // κλείνουμε όλα τα άλλα
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

    // ===========================
    // HELPERS
    // ===========================

    private boolean isEmptySafe(String s) {
        return s == null || s.trim().isEmpty();
    }

    // CPU info από /proc/cpuinfo
    private String readCpuInfo() {
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            int lines = 0;
            // Δεν χρειάζεται να γράψουμε ΟΛΟ το αρχείο, κρατάμε λίγες πρώτες γραμμές
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

    // Thermal info (best-effort: ρίχνουμε μια ματιά στα thermal_zone* )
    private String readThermalSummary() {
        StringBuilder sb = new StringBuilder();
        try {
            File base = new File("/sys/class/thermal");
            if (!base.exists() || !base.isDirectory()) {
                sb.append("Thermal info: not available on this device\n");
                return sb.toString();
            }

            File[] zones = base.listFiles();
            if (zones == null || zones.length == 0) {
                sb.append("Thermal zones: none visible\n");
                return sb.toString();
            }

            int shown = 0;
            for (File z : zones) {
                if (!z.getName().startsWith("thermal_zone")) continue;
                File typeFile = new File(z, "type");
                File tempFile = new File(z, "temp");
                if (!typeFile.exists() || !tempFile.exists()) continue;

                String type = readSmallFile(typeFile);
                String tempRaw = readSmallFile(tempFile);

                if (type == null || tempRaw == null) continue;

                double celsius;
                try {
                    long v = Long.parseLong(tempRaw.trim());
                    // πολλά SoC δίνουν millidegree C
                    if (v > 1000) {
                        celsius = v / 1000.0;
                    } else {
                        celsius = v;
                    }
                } catch (NumberFormatException nfe) {
                    continue;
                }

                sb.append(type.trim())
                        .append(": ")
                        .append(String.format("%.1f", celsius))
                        .append(" °C\n");

                shown++;
                if (shown >= 6) break; // δεν θέλουμε να γεμίσουμε σελίδες
            }

            if (shown == 0) {
                sb.append("Thermal sensors found, but readable data not available\n");
            }
        } catch (Throwable t) {
            sb.append("Thermal info error: ").append(t.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private String readSmallFile(File f) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            return br.readLine();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {
            }
        }
    }

    // ===== ROOT UTILS =====

    private boolean isDeviceRooted() {
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) return true;

        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su"
        };
        for (String p : paths) {
            if (new File(p).exists()) return true;
        }

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
        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su"
        };
        for (String p : paths) {
            if (new File(p).exists()) return p;
        }
        return "none";
    }
}
