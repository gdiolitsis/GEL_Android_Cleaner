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

    // FULL UPGRADE MODE — 7 SECTIONS + 3 NEW + ROOT MODE
    private boolean isRooted = false;

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
        if (title != null)
            title.setText(getString(R.string.phone_info_internal));

        // ============================
        // REFERENCES (ALL 10 SECTIONS)
        // ============================

        TextView txtSystemContent      = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent     = findViewById(R.id.txtAndroidContent);
        TextView txtCpuContent         = findViewById(R.id.txtCpuContent);
        TextView txtGpuContent         = findViewById(R.id.txtGpuContent);        // NEW
        TextView txtThermalContent     = findViewById(R.id.txtThermalContent);    // NEW
        TextView txtRamContent         = findViewById(R.id.txtRamContent);
        TextView txtStorageContent     = findViewById(R.id.txtStorageContent);
        TextView txtScreenContent      = findViewById(R.id.txtScreenContent);
        TextView txtConnectivityContent= findViewById(R.id.txtConnectivityContent); // NEW
        TextView txtRootContent        = findViewById(R.id.txtRootContent);

        // ICON REFS
        TextView iconSystem        = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid       = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu           = findViewById(R.id.iconCpuToggle);
        TextView iconGpu           = findViewById(R.id.iconGpuToggle);
        TextView iconThermal       = findViewById(R.id.iconThermalToggle);
        TextView iconRam           = findViewById(R.id.iconRamToggle);
        TextView iconStorage       = findViewById(R.id.iconStorageToggle);
        TextView iconScreen        = findViewById(R.id.iconScreenToggle);
        TextView iconConnectivity  = findViewById(R.id.iconConnectivityToggle);
        TextView iconRoot          = findViewById(R.id.iconRootToggle);

        // Store all for one-open mode
        allContents = new TextView[]{
                txtSystemContent, txtAndroidContent, txtCpuContent,
                txtGpuContent, txtThermalContent, txtRamContent,
                txtStorageContent, txtScreenContent, txtConnectivityContent,
                txtRootContent
        };

        allIcons = new TextView[]{
                iconSystem, iconAndroid, iconCpu,
                iconGpu, iconThermal, iconRam,
                iconStorage, iconScreen, iconConnectivity,
                iconRoot
        };

        // Root detection
        isRooted = isDeviceRooted();

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
        os.append("Kernel: ").append(System.getProperty("os.version")).append("\n");
        txtAndroidContent.setText(os.toString());

        // ===========================
        // CPU
        // ===========================
        StringBuilder cpu = new StringBuilder();
        cpu.append("── CPU ──\n");

        String[] abis = Build.SUPPORTED_ABIS;
        cpu.append("Primary ABI: ").append((abis != null && abis.length > 0) ? abis[0] : "N/A").append("\n");

        cpu.append("Supported ABIs: ");
        if (abis != null) {
            for (int i = 0; i < abis.length; i++) {
                cpu.append(abis[i]);
                if (i < abis.length - 1) cpu.append(", ");
            }
        }
        cpu.append("\n");

        cpu.append("Logical Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");

        cpu.append("CPU Info (/proc/cpuinfo):\n");
        cpu.append(readCpuInfo()).append("\n");

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

            long total = mi.totalMem / (1024L * 1024L);
            long free = mi.availMem / (1024L * 1024L);
            long used = total - free;

            ram.append("Total RAM: ").append(total).append(" MB\n");
            ram.append("Used RAM: ").append(used).append(" MB\n");
            ram.append("Free RAM: ").append(free).append(" MB\n");
            ram.append("Low RAM Device: ").append(mi.lowMemory ? "YES" : "NO").append("\n");
        } else {
            ram.append("RAM info unavailable\n");
        }

        txtRamContent.setText(ram.toString());

        // ===========================
        // INTERNAL STORAGE
        // ===========================
        StringBuilder st = new StringBuilder();
        st.append("── INTERNAL STORAGE ──\n");

        try {
            File dir = Environment.getDataDirectory();
            long total = dir.getTotalSpace();
            long free  = dir.getFreeSpace();
            long used  = total - free;

            st.append("Total: ").append(total / (1024L*1024L*1024L)).append(" GB\n");
            st.append("Used: ").append(used / (1024L*1024L*1024L)).append(" GB\n");
            st.append("Free: ").append(free / (1024L*1024L*1024L)).append(" GB\n");
        } catch (Exception e) {
            st.append("Storage info unavailable\n");
        }

        txtStorageContent.setText(st.toString());

    // ===========================
    // STORAGE
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
    // SCREEN
    // ===========================
    DisplayMetrics dm = getResources().getDisplayMetrics();
    StringBuilder sc = new StringBuilder();
    sc.append("── SCREEN ──\n");
    sc.append("Resolution: ").append(dm.widthPixels).append(" x ").append(dm.heightPixels).append(" px\n");
    sc.append("Density: ").append(dm.densityDpi).append(" dpi\n");
    sc.append("Scaled Density: ").append(dm.scaledDensity).append("\n");

    try {
        float xInches = dm.widthPixels / dm.xdpi;
        float yInches = dm.heightPixels / dm.ydpi;
        double diagonal = Math.sqrt(xInches * xInches + yInches * yInches);
        sc.append("Diagonal: ").append(String.format("%.1f", diagonal)).append(" inches\n");
    } catch (Exception ignored) {}

    try {
        float refreshRate = getWindowManager().getDefaultDisplay().getRefreshRate();
        sc.append("Refresh Rate: ").append(String.format("%.1f", refreshRate)).append(" Hz\n");
    } catch (Exception ignored) {}

    txtScreenContent.setText(sc.toString());

    // ===========================
    // ROOT EXTRAS
    // ===========================
    StringBuilder root = new StringBuilder();
    root.append("── ROOT EXTRAS ──\n");

    if (isRooted) {
        root.append("Device: ROOTED\n\n");
        root.append("Build Tags: ").append(Build.TAGS).append("\n");
        root.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
        root.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
        root.append("SELinux: ").append(getSelinux()).append("\n");
        root.append("su path: ").append(checkSuPaths()).append("\n");
    } else {
        root.append("Device appears NOT rooted\n");
        root.append("Root-level debugging is disabled\n");
    }

    txtRootContent.setText(root.toString());

    // ===========================
    // EXPANDABLE SECTIONS
    // ===========================
    setupSection(findViewById(R.id.headerSystem), txtSystemContent, iconSystem);
    setupSection(findViewById(R.id.headerAndroid), txtAndroidContent, iconAndroid);
    setupSection(findViewById(R.id.headerCpu), txtCpuContent, iconCpu);
    setupSection(findViewById(R.id.headerRam), txtRamContent, iconRam);
    setupSection(findViewById(R.id.headerStorage), txtStorageContent, iconStorage);
    setupSection(findViewById(R.id.headerScreen), txtScreenContent, iconScreen);
    setupSection(findViewById(R.id.headerRoot), txtRootContent, iconRoot);
}

private void setupSection(View header, TextView content, TextView icon) {
    header.setOnClickListener(v -> toggleSection(content, icon));
}

private void toggleSection(TextView open, TextView icon) {
    for (int i = 0; i < allContents.length; i++) {
        if (allContents[i] != open) {
            allContents[i].setVisibility(View.GONE);
            allIcons[i].setText("＋");
        }
    }

    boolean visible = open.getVisibility() == View.VISIBLE;
    open.setVisibility(visible ? View.GONE : View.VISIBLE);
    icon.setText(visible ? "＋" : "−");
}

// ========== CPU INFO ==========
private String readCpuInfo() {
    try {
        Process p = Runtime.getRuntime().exec("cat /proc/cpuinfo");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    } catch (Exception e) {
        return "[unavailable]";
    }
}

// ========== ROOT CHECK ==========
private boolean isDeviceRooted() {
    if (Build.TAGS != null && Build.TAGS.contains("test-keys"))
        return true;

    String[] paths = {
            "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su"
    };

    for (String p : paths) {
        if (new File(p).exists()) return true;
    }

    return "1".equals(getProp("ro.debuggable")) || "0".equals(getProp("ro.secure"));
}

private String getProp(String key) {
    try {
        Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine();
        br.close();
        return line != null ? line : "";
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
        return line != null ? line : "unknown";
    } catch (Exception e) {
        return "unknown";
    }
}

private String checkSuPaths() {
    String[] paths = {
            "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su"
    };
    for (String p : paths) {
        if (new File(p).exists()) return p;
    }
    return "none";
}
}

