package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DeviceInfoInternalActivity extends AppCompatActivity {

    private boolean isRooted = false;

    // για το "άνοιξε ένα-ένα"
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

        // ========== refs ==========
        TextView txtSystemContent   = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent  = findViewById(R.id.txtAndroidContent);
        TextView txtCpuContent      = findViewById(R.id.txtCpuContent);
        TextView txtRamContent      = findViewById(R.id.txtRamContent);
        TextView txtStorageContent  = findViewById(R.id.txtStorageContent);
        TextView txtScreenContent   = findViewById(R.id.txtScreenContent);
        TextView txtRootContent     = findViewById(R.id.txtRootContent);

        TextView iconSystem   = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid  = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu      = findViewById(R.id.iconCpuToggle);
        TextView iconRam      = findViewById(R.id.iconRamToggle);
        TextView iconStorage  = findViewById(R.id.iconStorageToggle);
        TextView iconScreen   = findViewById(R.id.iconScreenToggle);
        TextView iconRoot     = findViewById(R.id.iconRootToggle);

        allContents = new TextView[]{
                txtSystemContent, txtAndroidContent, txtCpuContent,
                txtRamContent, txtStorageContent, txtScreenContent, txtRootContent
        };
        allIcons = new TextView[]{
                iconSystem, iconAndroid, iconCpu,
                iconRam, iconStorage, iconScreen, iconRoot
        };

        // ========== data build ==========
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sys.append("SoC Manufacturer: ").append(Build.SOC_MANUFACTURER).append("\n");
            sys.append("SoC Model: ").append(Build.SOC_MODEL).append("\n");
        }
        sys.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n");
        sys.append("Build Time: ").append(Build.TIME).append(" (ms)\n");
        txtSystemContent.setText(sys.toString());

        // ===========================
        // ANDROID / OS
        // ===========================
        StringBuilder os = new StringBuilder();
        os.append("── ANDROID ──\n");
        os.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        os.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            os.append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n");
        }
        os.append("Build ID: ").append(Build.ID).append("\n");
        os.append("Build Type: ").append(Build.TYPE).append("\n");
        os.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");
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
        }
        cpu.append("CPU ABIs: ");
        if (abis != null) {
            for (int i = 0; i < abis.length; i++) {
                cpu.append(abis[i]);
                if (i < abis.length - 1) cpu.append(", ");
            }
        }
        cpu.append("\n");
        cpu.append("Logical cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");

        // μικρή προσπάθεια για /proc/cpuinfo (δεν σπάει αν αποτύχει)
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String hardwareLine = null;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains("hardware")) {
                    hardwareLine = line.trim();
                }
            }
            br.close();
            if (hardwareLine != null) {
                cpu.append(hardwareLine).append("\n");
            }
        } catch (Exception ignored) {
            // δεν μας νοιάζει, απλά δεν δείχνουμε extra info
        }
        txtCpuContent.setText(cpu.toString());

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
            long availMb = mi.availMem / (1024L * 1024L);
            long usedMb  = totalMb - availMb;

            ram.append("Total RAM: ").append(totalMb).append(" MB\n");
            ram.append("Used RAM: ").append(usedMb).append(" MB\n");
            ram.append("Free RAM: ").append(availMb).append(" MB\n");
            ram.append("Low RAM device: ").append(mi.lowMemory ? "YES" : "NO").append("\n");
            if (mi.threshold > 0) {
                long thresholdMb = mi.threshold / (1024L * 1024L);
                ram.append("Low RAM threshold: ").append(thresholdMb).append(" MB\n");
            }
        } else {
            ram.append("Memory info: N/A\n");
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
            st.append("\nRaw bytes total: ").append(totalBytes).append("\n");
            st.append("Raw bytes free: ").append(freeBytes).append("\n");
        } catch (Throwable t) {
            st.append("Storage info: N/A (").append(t.getMessage()).append(")\n");
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

        // Υπολογισμός περίπου διαγωνίου σε ίντσες (best effort)
        try {
            float xInches = dm.widthPixels / dm.xdpi;
            float yInches = dm.heightPixels / dm.ydpi;
            double diagonal = Math.sqrt(xInches * xInches + yInches * yInches);
            sc.append("Approx. diagonal: ")
                    .append(String.format("%.1f", diagonal))
                    .append(" inches\n");
        } catch (Throwable ignored) {
        }

        // Refresh rate (best effort, μπορεί να διαφέρει ανά συσκευή)
        try {
            float refresh = getWindowManager().getDefaultDisplay().getRefreshRate();
            sc.append("Refresh rate: ")
                    .append(String.format("%.1f", refresh))
                    .append(" Hz\n");
        } catch (Throwable ignored) {
        }

        txtScreenContent.setText(sc.toString());

        // ===========================
        // ROOT EXTRAS
        // ===========================
        StringBuilder rootSb = new StringBuilder();
        rootSb.append("── ROOT EXTRAS ──\n");
        if (isRooted) {
            rootSb.append("Device appears ROOTED.\n\n");
            rootSb.append("Build Tags: ").append(Build.TAGS).append("\n");
            rootSb.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            rootSb.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            rootSb.append("SELinux: ").append(getSelinux()).append("\n");
            rootSb.append("su paths: ").append(checkSuPaths()).append("\n");
        } else {
            rootSb.append("Device appears NOT rooted.\n");
            rootSb.append("Extra low-level debug info is disabled.\n");
        }
        txtRootContent.setText(rootSb.toString());

        // ========== expand / collapse listeners ==========
        setupSection(findViewById(R.id.headerSystem),   txtSystemContent,   iconSystem);
        setupSection(findViewById(R.id.headerAndroid),  txtAndroidContent,  iconAndroid);
        setupSection(findViewById(R.id.headerCpu),      txtCpuContent,      iconCpu);
        setupSection(findViewById(R.id.headerRam),      txtRamContent,      iconRam);
        setupSection(findViewById(R.id.headerStorage),  txtStorageContent,  iconStorage);
        setupSection(findViewById(R.id.headerScreen),   txtScreenContent,   iconScreen);
        setupSection(findViewById(R.id.headerRoot),     txtRootContent,     iconRoot);
    }

    // ανοίγει μόνο ένα section κάθε φορά
    private void setupSection(View header, final TextView content, final TextView icon) {
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView contentToOpen, TextView iconToUpdate) {
        if (allContents == null || allIcons == null) return;

        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];
            if (c == null || ic == null) continue;
            if (c == contentToOpen) continue;
            c.setVisibility(View.GONE);
            ic.setText("＋");
        }

        if (contentToOpen.getVisibility() == View.VISIBLE) {
            contentToOpen.setVisibility(View.GONE);
            iconToUpdate.setText("＋");
        } else {
            contentToOpen.setVisibility(View.VISIBLE);
            iconToUpdate.setText("−");
        }
    }

    // ===== ROOT UTILS (ίδια λογική με πριν) =====
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
            return line != null ? line.trim() : "[empty]";
        } catch (Exception e) {
            return "[error]";
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
