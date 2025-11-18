package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLES20;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

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

        StringBuilder s = new StringBuilder();

        // ===========================
        // SYSTEM
        // ===========================
        s.append("── SYSTEM ──\n");
        s.append("Brand: ").append(Build.BRAND).append("\n");
        s.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        s.append("Model: ").append(Build.MODEL).append("\n");
        s.append("Device: ").append(Build.DEVICE).append("\n");
        s.append("Product: ").append(Build.PRODUCT).append("\n");
        s.append("Board: ").append(Build.BOARD).append("\n");
        s.append("Hardware: ").append(Build.HARDWARE).append("\n\n");

        // ===========================
        // ANDROID
        // ===========================
        s.append("── ANDROID ──\n");
        s.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        s.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            s.append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n");
        }
        s.append("Build ID: ").append(Build.ID).append("\n");
        s.append("Build Type: ").append(Build.TYPE).append("\n");
        s.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");
        s.append("Kernel: ").append(System.getProperty("os.version")).append("\n\n");

        // ===========================
        // UPTIME
        // ===========================
        s.append("── UPTIME ──\n");
        long uptime = SystemClock.elapsedRealtime();
        s.append("Device Uptime: ").append(formatDuration(uptime)).append("\n\n");

        // ===========================
        // CPU
        // ===========================
        s.append("── CPU ──\n");
        String[] abis = Build.SUPPORTED_ABIS;
        if (abis != null && abis.length > 0) {
            s.append("Primary ABI: ").append(abis[0]).append("\n");
        }
        s.append("CPU ABIs: ");
        if (abis != null) {
            for (int i = 0; i < abis.length; i++) {
                s.append(abis[i]);
                if (i < abis.length - 1) s.append(", ");
            }
        }
        s.append("\n");

        // CPU Frequencies snapshot
        s.append("\n[CPU Frequencies]\n");
        s.append(getCpuFrequenciesSnapshot());

        // ===========================
        // RAM
        // ===========================
        s.append("\n── RAM ──\n");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long totalMb = mi.totalMem / (1024L * 1024L);
            long availMb = mi.availMem / (1024L * 1024L);
            long usedMb  = totalMb - availMb;

            s.append("Total RAM: ").append(totalMb).append(" MB\n");
            s.append("Used RAM: ").append(usedMb).append(" MB\n");
            s.append("Free RAM: ").append(availMb).append(" MB\n");
            s.append("Low RAM device: ").append(mi.lowMemory ? "YES" : "NO").append("\n\n");
        }

        // ===========================
        // INTERNAL STORAGE
        // ===========================
        s.append("── INTERNAL STORAGE ──\n");
        try {
            File dataDir = Environment.getDataDirectory();
            long[] stats = getStorageStats(dataDir);
            long totalGb = stats[0];
            long usedGb  = stats[1];
            long freeGb  = stats[2];

            s.append("Total: ").append(totalGb).append(" GB\n");
            s.append("Used: ").append(usedGb).append(" GB\n");
            s.append("Free: ").append(freeGb).append(" GB\n\n");
        } catch (Throwable t) {
            s.append("Storage info: N/A (").append(t.getMessage()).append(")\n\n");
        }

        // ===========================
        // I/O SPEED SNAPSHOT
        // ===========================
        s.append("── STORAGE SPEED (SNAPSHOT) ──\n");
        s.append(getIoSpeedSnapshot()).append("\n");

        // ===========================
        // SCREEN
        // ===========================
        s.append("\n── SCREEN ──\n");
        DisplayMetrics dm = getResources().getDisplayMetrics();
        s.append("Resolution: ").append(dm.widthPixels)
                .append(" x ").append(dm.heightPixels).append(" px\n");
        s.append("Density: ").append(dm.densityDpi).append(" dpi\n");
        s.append("Scaled density: ").append(dm.scaledDensity).append("\n\n");

        // ===========================
        // GPU
        // ===========================
        s.append("── GPU ──\n");
        s.append(getGpuInfo()).append("\n");

        // ===========================
        // BATTERY
        // ===========================
        s.append("── BATTERY ──\n");
        s.append(getBatteryInfo()).append("\n");

        // ===========================
        // THERMAL ZONES
        // ===========================
        s.append("── THERMAL SENSORS ──\n");
        s.append(getThermalInfo()).append("\n");

        // ===========================
        // ROOT EXTRAS (ONLY IF ROOTED)
        // ===========================
        if (isRooted) {
            s.append("── ROOT MODE ACTIVE ──\n");
            s.append("Build Tags: ").append(Build.TAGS).append("\n");
            s.append("ro.debuggable: ").append(getProp("ro.debuggable")).append("\n");
            s.append("ro.secure: ").append(getProp("ro.secure")).append("\n");
            s.append("SELinux: ").append(getSelinux()).append("\n");
            s.append("su paths: ").append(checkSuPaths()).append("\n");
        }

        if (info != null) {
            info.setText(s.toString());
        }
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

    // ============================================================
    // STORAGE HELPERS
    // ============================================================
    private long[] getStorageStats(File dir) {
        long[] out = new long[3];
        StatFs statFs = new StatFs(dir.getAbsolutePath());
        long blockSize, totalBlocks, availableBlocks;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();
            totalBlocks = statFs.getBlockCountLong();
            availableBlocks = statFs.getAvailableBlocksLong();
        } else {
            blockSize = statFs.getBlockSize();
            totalBlocks = statFs.getBlockCount();
            availableBlocks = statFs.getAvailableBlocks();
        }

        long totalBytes = totalBlocks * blockSize;
        long freeBytes = availableBlocks * blockSize;
        long totalGb = totalBytes / (1024L * 1024L * 1024L);
        long freeGb = freeBytes / (1024L * 1024L * 1024L);
        long usedGb = totalGb - freeGb;

        out[0] = totalGb;
        out[1] = usedGb;
        out[2] = freeGb;
        return out;
    }

    // ============================================================
    // I/O SPEED SNAPSHOT
    // ============================================================
    private String getIoSpeedSnapshot() {
        File cacheDir = getCacheDir();
        File testFile = new File(cacheDir, "gel_io_test.bin");
        int sizeKb = 256; // 256 KB
        byte[] buffer = new byte[sizeKb * 1024];

        long writeMs = -1;
        long readMs = -1;

        try {
            long start = SystemClock.elapsedRealtime();
            FileOutputStream fos = new FileOutputStream(testFile);
            fos.write(buffer);
            fos.flush();
            fos.close();
            writeMs = SystemClock.elapsedRealtime() - start;
        } catch (Throwable t) {
            return "I/O test: N/A (" + t.getMessage() + ")";
        }

        try {
            long start = SystemClock.elapsedRealtime();
            FileInputStream fis = new FileInputStream(testFile);
            while (fis.read(buffer) != -1) {
                // read loop
            }
            fis.close();
            readMs = SystemClock.elapsedRealtime() - start;
        } catch (Throwable t) {
            // ignore read error
        }

        testFile.delete();

        StringBuilder sb = new StringBuilder();
        sb.append("Test size: ").append(sizeKb).append(" KB\n");
        if (writeMs >= 0) {
            sb.append("Write time: ").append(writeMs).append(" ms\n");
        } else {
            sb.append("Write time: N/A\n");
        }

        if (readMs >= 0) {
            sb.append("Read time: ").append(readMs).append(" ms");
        } else {
            sb.append("Read time: N/A");
        }

        return sb.toString();
    }

    // ============================================================
    // GPU INFO
    // ============================================================
    private String getGpuInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            String renderer = GLES20.glGetString(GLES20.GL_RENDERER);
            String vendor   = GLES20.glGetString(GLES20.GL_VENDOR);
            String version  = GLES20.glGetString(GLES20.GL_VERSION);

            sb.append("Renderer: ").append(renderer != null ? renderer : "N/A").append("\n");
            sb.append("Vendor: ").append(vendor != null ? vendor : "N/A").append("\n");
            sb.append("OpenGL ES: ").append(version != null ? version : "N/A");
        } catch (Throwable t) {
            sb.append("GPU info: N/A (").append(t.getMessage()).append(")");
        }
        return sb.toString();
    }

    // ============================================================
    // BATTERY INFO
    // ============================================================
    private String getBatteryInfo() {
        StringBuilder sb = new StringBuilder();
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

                int percent = -1;
                if (level >= 0 && scale > 0) {
                    percent = (int) ((level * 100f) / scale);
                }

                sb.append("Level: ").append(percent >= 0 ? percent + " %" : "N/A").append("\n");
                sb.append("Status: ").append(getBatteryStatusString(status)).append("\n");
                sb.append("Plugged: ").append(getBatteryPluggedString(plugged)).append("\n");
                sb.append("Health: ").append(getBatteryHealthString(health)).append("\n");
            } else {
                sb.append("Battery broadcast: N/A\n");
            }

            // Battery capacity (best-effort)
            long capacityMah = getBatteryCapacityMah();
            if (capacityMah > 0) {
                sb.append("Design Capacity: ").append(capacityMah).append(" mAh");
            } else {
                sb.append("Design Capacity: N/A");
            }

        } catch (Throwable t) {
            sb.append("Battery info: N/A (").append(t.getMessage()).append(")");
        }
        return sb.toString();
    }

    private String getBatteryStatusString(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default:
                return "Unknown";
        }
    }

    private String getBatteryPluggedString(int plugged) {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Wireless";
            default:
                return "No";
        }
    }

    private String getBatteryHealthString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Failure";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default:
                return "Unknown";
        }
    }

    private long getBatteryCapacityMah() {
        // best-effort: read common sysfs paths
        String[] candidates = {
                "/sys/class/power_supply/battery/charge_full_design",
                "/sys/class/power_supply/battery/charge_full",
                "/sys/class/power_supply/battery/energy_full_design",
                "/sys/class/power_supply/battery/energy_full"
        };

        for (String path : candidates) {
            try {
                String line = readFirstLine(path);
                if (line == null) continue;
                long raw = Long.parseLong(line.trim());

                // Heuristic: some paths are in uAh or uWh
                if (raw > 100000) {
                    long mah = raw / 1000L;
                    if (mah > 0 && mah < 20000) {
                        return mah;
                    }
                } else if (raw > 100 && raw < 20000) {
                    return raw;
                }
            } catch (Throwable ignored) {
            }
        }

        return -1;
    }

    private String readFirstLine(String path) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            return br.readLine();
        } catch (Throwable ignored) {
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored2) {
            }
        }
    }

    // ============================================================
    // THERMAL INFO
    // ============================================================
    private String getThermalInfo() {
        StringBuilder sb = new StringBuilder();
        File base = new File("/sys/class/thermal");
        if (!base.exists() || !base.isDirectory()) {
            sb.append("No thermal sensors directory.");
            return sb.toString();
        }

        File[] zones = base.listFiles();
        if (zones == null || zones.length == 0) {
            sb.append("No thermal zones found.");
            return sb.toString();
        }

        for (File f : zones) {
            if (!f.getName().startsWith("thermal_zone")) continue;
            try {
                String type = readFirstLine(new File(f, "type").getAbsolutePath());
                String tempStr = readFirstLine(new File(f, "temp").getAbsolutePath());
                if (tempStr == null) continue;
                tempStr = tempStr.trim();
                if (tempStr.isEmpty()) continue;

                float tempC;
                if (tempStr.length() > 3) {
                    tempC = Float.parseFloat(tempStr) / 1000f;
                } else {
                    tempC = Float.parseFloat(tempStr);
                }

                sb.append(type != null ? type : f.getName())
                        .append(": ")
                        .append(String.format("%.1f °C", tempC))
                        .append("\n");
            } catch (Throwable ignored) {
            }
        }

        if (sb.length() == 0) {
            sb.append("Thermal sensors: N/A");
        }

        return sb.toString();
    }

    // ============================================================
    // CPU FREQ SNAPSHOT
    // ============================================================
    private String getCpuFrequenciesSnapshot() {
        StringBuilder sb = new StringBuilder();
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores <= 0) cores = 1;

        for (int i = 0; i < cores; i++) {
            String path = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq";
            try {
                String line = readFirstLine(path);
                if (line == null) continue;
                long khz = Long.parseLong(line.trim());
                long mhz = khz / 1000L;
                sb.append("CPU").append(i).append(": ").append(mhz).append(" MHz\n");
            } catch (Throwable ignored) {
            }
        }

        if (sb.length() == 0) {
            sb.append("Current frequencies: N/A\n");
        }

        return sb.toString();
    }

    // ============================================================
    // TIME FORMAT
    // ============================================================
    private String formatDuration(long millis) {
        long seconds = millis / 1000L;
        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0 || days > 0) sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0 || days > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString();
    }
}
```0
