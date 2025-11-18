package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Locale;

public class DeviceInfoInternalActivity extends AppCompatActivity {

    private boolean isRooted = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        TextView title = findViewById(R.id.txtTitleDevice);
        TextView info  = findViewById(R.id.txtDeviceInfo);

        if (title != null) {
            title.setText(getString(R.string.phone_info_internal));
        }

        // ===========================
        // ROOT CHECK
        // ===========================
        isRooted = isDeviceRooted();

        // Θα χτίσουμε ξεχωριστά sections για να τα δέσουμε με tabs
        StringBuilder secSystem      = new StringBuilder();
        StringBuilder secAndroid     = new StringBuilder();
        StringBuilder secCpuRam      = new StringBuilder();
        StringBuilder secStorage     = new StringBuilder();
        StringBuilder secScreen      = new StringBuilder();
        StringBuilder secBattery     = new StringBuilder();
        StringBuilder secRuntimeFs   = new StringBuilder();
        StringBuilder secRootExtras  = new StringBuilder();

        DecimalFormat df1 = new DecimalFormat("0.0");

        // ===========================
        // SYSTEM
        // ===========================
        secSystem.append("── SYSTEM ──\n");
        secSystem.append("Brand: ").append(Build.BRAND).append("\n");
        secSystem.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        secSystem.append("Model: ").append(Build.MODEL).append("\n");
        secSystem.append("Device: ").append(Build.DEVICE).append("\n");
        secSystem.append("Product: ").append(Build.PRODUCT).append("\n");
        secSystem.append("Board: ").append(Build.BOARD).append("\n");
        secSystem.append("Hardware: ").append(Build.HARDWARE).append("\n");
        secSystem.append("Build Type: ").append(Build.TYPE).append("\n");
        secSystem.append("Build ID: ").append(Build.ID).append("\n");
        secSystem.append("\n");

        // ===========================
        // ANDROID / KERNEL
        // ===========================
        secAndroid.append("── ANDROID ──\n");
        secAndroid.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        secAndroid.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            secAndroid.append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n");
        }
        secAndroid.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n");
        secAndroid.append("Tags: ").append(Build.TAGS).append("\n");
        secAndroid.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");
        secAndroid.append("Kernel (Linux): ").append(System.getProperty("os.version")).append("\n");
        secAndroid.append("\n");

        // ===========================
        // CPU INFO
        // ===========================
        secCpuRam.append("── CPU ──\n");
        String[] abis = Build.SUPPORTED_ABIS;
        if (abis != null && abis.length > 0) {
            secCpuRam.append("Primary ABI: ").append(abis[0]).append("\n");
        }
        secCpuRam.append("CPU ABIs: ");
        if (abis != null) {
            for (int i = 0; i < abis.length; i++) {
                secCpuRam.append(abis[i]);
                if (i < abis.length - 1) secCpuRam.append(", ");
            }
        }
        secCpuRam.append("\n");

        // Προσπάθεια ανάγνωσης /proc/cpuinfo (όπου επιτρέπεται)
        try {
            File cpuInfoFile = new File("/proc/cpuinfo");
            if (cpuInfoFile.canRead()) {
                BufferedReader br = new BufferedReader(new FileReader(cpuInfoFile));
                String line;
                int lines = 0;
                while ((line = br.readLine()) != null && lines < 15) {
                    secCpuRam.append(line).append("\n");
                    lines++;
                }
                br.close();
            }
        } catch (Throwable ignored) {}
        secCpuRam.append("\n");

        // ===========================
        // RAM
        // ===========================
        secCpuRam.append("── RAM ──\n");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long totalMb = mi.totalMem / (1024L * 1024L);
            long availMb = mi.availMem / (1024L * 1024L);
            long usedMb  = totalMb - availMb;

            secCpuRam.append("Total RAM: ").append(totalMb).append(" MB\n");
            secCpuRam.append("Used RAM: ").append(usedMb).append(" MB\n");
            secCpuRam.append("Free RAM: ").append(availMb).append(" MB\n");
            secCpuRam.append("Low RAM device: ").append(mi.lowMemory ? "YES" : "NO").append("\n");
        } else {
            secCpuRam.append("ActivityManager: N/A\n");
        }
        secCpuRam.append("\n");

        // ===========================
        // STORAGE (INTERNAL + EXTERNAL)
        // ===========================
        secStorage.append("── INTERNAL STORAGE ──\n");
        try {
            File dataDir = Environment.getDataDirectory();
            long totalBytes = dataDir.getTotalSpace();
            long freeBytes  = dataDir.getFreeSpace();
            long totalGb = totalBytes / (1024L * 1024L * 1024L);
            long freeGb  = freeBytes / (1024L * 1024L * 1024L);
            long usedGb  = totalGb - freeGb;

            secStorage.append("Total: ").append(totalGb).append(" GB\n");
            secStorage.append("Used: ").append(usedGb).append(" GB\n");
            secStorage.append("Free: ").append(freeGb).append(" GB\n");
        } catch (Throwable t) {
            secStorage.append("Internal Storage: N/A (").append(t.getMessage()).append(")\n");
        }
        secStorage.append("\n");

        secStorage.append("── EXTERNAL STORAGE ──\n");
        try {
            File ext = Environment.getExternalStorageDirectory();
            if (ext != null && ext.exists()) {
                long totalBytes = ext.getTotalSpace();
                long freeBytes  = ext.getFreeSpace();
                double totalGbD = totalBytes / (1024d * 1024d * 1024d);
                double freeGbD  = freeBytes / (1024d * 1024d * 1024d);
                double usedGbD  = totalGbD - freeGbD;

                secStorage.append("Path: ").append(ext.getAbsolutePath()).append("\n");
                secStorage.append("Total: ").append(df1.format(totalGbD)).append(" GB\n");
                secStorage.append("Used: ").append(df1.format(usedGbD)).append(" GB\n");
                secStorage.append("Free: ").append(df1.format(freeGbD)).append(" GB\n");
            } else {
                secStorage.append("External storage: Not available\n");
            }
        } catch (Throwable t) {
            secStorage.append("External Storage: N/A (").append(t.getMessage()).append(")\n");
        }
        secStorage.append("\n");

        // ===========================
        // SCREEN
        // ===========================
        secScreen.append("── SCREEN ──\n");
        DisplayMetrics dm = getResources().getDisplayMetrics();
        secScreen.append("Resolution: ").append(dm.widthPixels)
                .append(" x ").append(dm.heightPixels).append(" px\n");
        secScreen.append("Density: ").append(dm.densityDpi).append(" dpi\n");
        secScreen.append("Scaled density: ").append(dm.scaledDensity).append("\n");
        secScreen.append("Xdpi: ").append(df1.format(dm.xdpi)).append("\n");
        secScreen.append("Ydpi: ").append(df1.format(dm.ydpi)).append("\n");
        secScreen.append("\n");

        // ===========================
        // BATTERY (BASIC HEALTH INFO)
        // ===========================
        secBattery.append("── BATTERY ──\n");
        try {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                secBattery.append("Level: ").append(level).append(" %\n");
            } else {
                secBattery.append("BatteryManager: N/A\n");
            }
        } catch (Throwable t) {
            secBattery.append("Battery info: N/A (").append(t.getMessage()).append(")\n");
        }
        secBattery.append("\n");

        // ===========================
        // RUNTIME / UPTIME / FS
        // ===========================
        secRuntimeFs.append("── RUNTIME ──\n");
        long upMs = SystemClock.elapsedRealtime();
        long upSec = upMs / 1000;
        long upMin = upSec / 60;
        long upH   = upMin / 60;
        long upD   = upH / 24;

        secRuntimeFs.append("Uptime: ")
                .append(upD).append("d ")
                .append(upH % 24).append("h ")
                .append(upMin % 60).append("m\n");
        secRuntimeFs.append("\n");

        secRuntimeFs.append("── FILESYSTEM DIRECTORIES ──\n");
        secRuntimeFs.append("Data dir: ").append(Environment.getDataDirectory().getAbsolutePath()).append("\n");
        secRuntimeFs.append("Root dir: ").append(Environment.getRootDirectory().getAbsolutePath()).append("\n");
        secRuntimeFs.append("Download cache: ").append(Environment.getDownloadCacheDirectory().getAbsolutePath()).append("\n");
        File extDir = Environment.getExternalStorageDirectory();
        if (extDir != null) {
            secRuntimeFs.append("External: ").append(extDir.getAbsolutePath()).append("\n");
        }
        secRuntimeFs.append("\n");

        // ===========================
        // ROOT EXTRAS (ONLY IF ROOTED)
        // ===========================
        if (isRooted) {
            secRootExtras.append("── ROOT MODE EXTRA ──\n");
            secRootExtras.append("Build Tags: ").append(Build.TAGS).append("\n");
            secRootExtras.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            secRootExtras.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            secRootExtras.append("SELinux: ").append(getSelinux()).append("\n");
            secRootExtras.append("su paths: ").append(checkSuPaths()).append("\n");
            secRootExtras.append("\n");
        }

        // ===========================
        // FULL TEXT (ALL SECTIONS)
        // ===========================
        final String textSystem     = secSystem.toString();
        final String textAndroid    = secAndroid.toString();
        final String textCpuRam     = secCpuRam.toString();
        final String textStorage    = secStorage.toString();
        final String textScreen     = secScreen.toString();
        final String textBattery    = secBattery.toString();
        final String textRuntimeFs  = secRuntimeFs.toString();
        final String textRootExtras = secRootExtras.toString();

        final String textAll =
                textSystem +
                textAndroid +
                textCpuRam +
                textStorage +
                textScreen +
                textBattery +
                textRuntimeFs +
                textRootExtras;

        if (info != null) {
            info.setText(textAll);
        }

        // ===========================
        // TABS / BUTTON FILTERS
        // ===========================
        setupTabsInternal(textAll, textSystem, textAndroid, textCpuRam,
                textStorage, textScreen, textBattery, textRuntimeFs, textRootExtras);
    }

    // ------------------------------------------------------------
    // TAB BUTTONS HANDLER (INTERNAL INFO)
    // ------------------------------------------------------------
    private void setupTabsInternal(String all,
                                   String system,
                                   String androidInfo,
                                   String cpuRam,
                                   String storage,
                                   String screen,
                                   String battery,
                                   String runtimeFs,
                                   String rootExtras) {

        final TextView info = findViewById(R.id.txtDeviceInfo);
        if (info == null) return;

        Button tabAll      = findViewById(R.id.btnTabAllInternal);
        Button tabSystem   = findViewById(R.id.btnTabSystemInternal);
        Button tabAndroid  = findViewById(R.id.btnTabAndroidInternal);
        Button tabCpuRam   = findViewById(R.id.btnTabCpuRamInternal);
        Button tabStorage  = findViewById(R.id.btnTabStorageInternal);
        Button tabScreen   = findViewById(R.id.btnTabScreenInternal);
        Button tabBattery  = findViewById(R.id.btnTabBatteryInternal);
        Button tabRuntime  = findViewById(R.id.btnTabRuntimeInternal);
        Button tabRoot     = findViewById(R.id.btnTabRootInternal);

        // Αν δεν υπάρχουν στο XML, απλά δεν κάνουμε τίποτα (fallback = full text)
        if (tabAll == null) return;

        View.OnClickListener lAll = v -> info.setText(all);
        View.OnClickListener lSystem = v -> info.setText(system);
        View.OnClickListener lAndroid = v -> info.setText(androidInfo);
        View.OnClickListener lCpuRam = v -> info.setText(cpuRam);
        View.OnClickListener lStorage = v -> info.setText(storage);
        View.OnClickListener lScreen = v -> info.setText(screen);
        View.OnClickListener lBattery = v -> info.setText(battery);
        View.OnClickListener lRuntime = v -> info.setText(runtimeFs);
        View.OnClickListener lRoot = v -> info.setText(rootExtras.isEmpty() ? "No extra root info.\n" : rootExtras);

        tabAll.setOnClickListener(lAll);
        if (tabSystem  != null) tabSystem.setOnClickListener(lSystem);
        if (tabAndroid != null) tabAndroid.setOnClickListener(lAndroid);
        if (tabCpuRam  != null) tabCpuRam.setOnClickListener(lCpuRam);
        if (tabStorage != null) tabStorage.setOnClickListener(lStorage);
        if (tabScreen  != null) tabScreen.setOnClickListener(lScreen);
        if (tabBattery != null) tabBattery.setOnClickListener(lBattery);
        if (tabRuntime != null) tabRuntime.setOnClickListener(lRuntime);
        if (tabRoot    != null) tabRoot.setOnClickListener(lRoot);
    }

    // ============================================================
    // ROOT UTILITY METHODS
    // ============================================================
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

        if ("1".equals(debuggable) || "0".equals(secure)) return true;

        return false;
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
