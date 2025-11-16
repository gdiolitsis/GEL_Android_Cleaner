package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PerformanceDiagnosticsActivity extends AppCompatActivity {

    private TextView txtDiag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);

        setTitle(R.string.diagnostics); // "GEL Phone Diagnosis"

        txtDiag = findViewById(R.id.txtDiagnostics);
        if (txtDiag != null) {
            txtDiag.setMovementMethod(new ScrollingMovementMethod());
        }

        runFullDiagnostics();
    }

    // ============================================================
    // MAIN DIAGNOSTICS ENTRY
    // ============================================================
    private void runFullDiagnostics() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== DEVICE OVERVIEW ===\n");
        sb.append(getDeviceOverview());
        sb.append("\n");

        sb.append("=== CPU & PERFORMANCE ===\n");
        sb.append(getCpuInfo());
        sb.append("\n");

        sb.append("=== MEMORY (RAM) ===\n");
        sb.append(getRamInfo());
        sb.append("\n");

        sb.append("=== STORAGE ===\n");
        sb.append(getStorageInfo());
        sb.append("\n");

        sb.append("=== BATTERY ===\n");
        sb.append(getBatteryInfo());
        sb.append("\n");

        sb.append("=== THERMAL / TEMPERATURE (best effort) ===\n");
        sb.append(getThermalInfo());
        sb.append("\n");

        sb.append("=== SENSORS ===\n");
        sb.append(getSensorsInfo());
        sb.append("\n");

        sb.append("=== NETWORK (basic) ===\n");
        sb.append(getNetworkInfo());
        sb.append("\n");

        sb.append("=== UPTIME ===\n");
        sb.append(getUptimeInfo());
        sb.append("\n");

        if (txtDiag != null) {
            txtDiag.setText(sb.toString());
        }
    }

    // ============================================================
    // DEVICE OVERVIEW
    // ============================================================
    private String getDeviceOverview() {
        StringBuilder sb = new StringBuilder();
        sb.append("Brand: ").append(Build.BRAND).append("\n");
        sb.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        sb.append("Model: ").append(Build.MODEL).append("\n");
        sb.append("Device: ").append(Build.DEVICE).append("\n");
        sb.append("Board: ").append(Build.BOARD).append("\n");
        sb.append("Hardware: ").append(Build.HARDWARE).append("\n");
        sb.append("Product: ").append(Build.PRODUCT).append("\n");
        sb.append("Android: ").append(Build.VERSION.RELEASE)
                .append(" (SDK ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("Build ID: ").append(Build.ID).append("\n");
        return sb.toString();
    }

    // ============================================================
    // CPU INFO
    // ============================================================
    private String getCpuInfo() {
        StringBuilder sb = new StringBuilder();
        int cores = Runtime.getRuntime().availableProcessors();
        sb.append("Cores: ").append(cores).append("\n");

        // /proc/cpuinfo (best effort)
        try {
            String cpuInfo = readCpuInfoShort();
            if (cpuInfo != null && !cpuInfo.isEmpty()) {
                sb.append(cpuInfo).append("\n");
            }
        } catch (Exception ignored) {}

        // Frequencies per core (best effort)
        for (int i = 0; i < cores; i++) {
            String basePath = "/sys/devices/system/cpu/cpu" + i + "/cpufreq/";
            long min = readLongFromFile(basePath + "cpuinfo_min_freq");
            long max = readLongFromFile(basePath + "cpuinfo_max_freq");
            long cur = readLongFromFile(basePath + "scaling_cur_freq");

            sb.append("CPU").append(i).append(": ");
            if (min > 0 || max > 0 || cur > 0) {
                if (min > 0) sb.append("min=").append(min / 1000).append(" MHz, ");
                if (max > 0) sb.append("max=").append(max / 1000).append(" MHz, ");
                if (cur > 0) sb.append("cur=").append(cur / 1000).append(" MHz");
            } else {
                sb.append("frequency info: N/A (restricted by device)");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String readCpuInfoShort() {
        File f = new File("/proc/cpuinfo");
        if (!f.exists()) return "";
        StringBuilder sb = new StringBuilder();
        int lines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null && lines < 8) {
                sb.append(line).append("\n");
                lines++;
            }
        } catch (IOException ignored) {}
        return sb.toString();
    }

    private long readLongFromFile(String path) {
        File f = new File(path);
        if (!f.exists()) return -1;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String s = br.readLine();
            if (s != null) {
                s = s.trim();
                return Long.parseLong(s);
            }
        } catch (Exception ignored) {
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {}
            }
        }
        return -1;
    }

    // ============================================================
    // RAM
    // ============================================================
    private String getRamInfo() {
        StringBuilder sb = new StringBuilder();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            sb.append("RAM info not available.\n");
            return sb.toString();
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        sb.append("Total RAM: ").append(formatBytes(mi.totalMem)).append("\n");
        sb.append("Available RAM: ").append(formatBytes(mi.availMem)).append("\n");
        sb.append("Used RAM: ").append(formatBytes(mi.totalMem - mi.availMem)).append("\n");
        sb.append("Low memory: ").append(mi.lowMemory).append("\n");
        sb.append("Threshold: ").append(formatBytes(mi.threshold)).append("\n");

        double usedPercent = 0;
        if (mi.totalMem > 0) {
            usedPercent = (mi.totalMem - mi.availMem) * 100.0 / mi.totalMem;
        }
        sb.append("Usage: ").append(String.format(Locale.US, "%.1f%%", usedPercent)).append("\n");

        return sb.toString();
    }

    // ============================================================
    // STORAGE
    // ============================================================
    private String getStorageInfo() {
        StringBuilder sb = new StringBuilder();

        // Internal
        File internal = Environment.getDataDirectory();
        appendStorageLine(sb, "Internal", internal);

        // External (primary)
        File external = Environment.getExternalStorageDirectory();
        if (external != null) {
            appendStorageLine(sb, "External (primary)", external);
        } else {
            sb.append("External storage: not accessible\n");
        }

        return sb.toString();
    }

    private void appendStorageLine(StringBuilder sb, String label, File path) {
        try {
            android.os.StatFs stat = new android.os.StatFs(path.getAbsolutePath());
            long blockSize, totalBlocks, availableBlocks;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availableBlocks = stat.getAvailableBlocks();
            }

            long total = totalBlocks * blockSize;
            long avail = availableBlocks * blockSize;
            long used = total - avail;

            sb.append(label).append(":\n");
            sb.append("  Total: ").append(formatBytes(total)).append("\n");
            sb.append("  Used:  ").append(formatBytes(used)).append("\n");
            sb.append("  Free:  ").append(formatBytes(avail)).append("\n");
        } catch (Exception e) {
            sb.append(label).append(": error reading storage (")
                    .append(e.getMessage()).append(")\n");
        }
    }

    // ============================================================
    // BATTERY
    // ============================================================
    private String getBatteryInfo() {
        StringBuilder sb = new StringBuilder();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        if (batteryStatus == null) {
            sb.append("Battery info not available.\n");
            return sb.toString();
        }

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        String tech = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

        float pct = (level >= 0 && scale > 0) ? (level * 100f / scale) : -1f;

        sb.append("Level: ").append(level).append(" / ").append(scale);
        if (pct >= 0) sb.append(" (").append(String.format(Locale.US, "%.1f%%", pct)).append(")");
        sb.append("\n");

        sb.append("Status: ").append(decodeBatteryStatus(status)).append("\n");
        sb.append("Health: ").append(decodeBatteryHealth(health)).append("\n");
        sb.append("Plugged: ").append(decodeBatteryPlugged(plugged)).append("\n");

        if (temp > 0) {
            sb.append("Temperature: ")
                    .append(temp / 10f).append(" °C\n");
        }

        if (voltage > 0) {
            sb.append("Voltage: ")
                    .append(voltage / 1000f).append(" V\n");
        }

        if (tech != null) {
            sb.append("Technology: ").append(tech).append("\n");
        }

        // Battery capacity (approximate, not all devices support this cleanly)
        try {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (bm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                long capacityMicroAh = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                if (capacityMicroAh > 0) {
                    sb.append("Charge counter: ")
                            .append(capacityMicroAh / 1000).append(" mAh (approx)\n");
                }
            }
        } catch (Exception ignored) {}

        return sb.toString();
    }

    private String decodeBatteryStatus(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING: return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL: return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return "Not charging";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default: return "Unknown";
        }
    }

    private String decodeBatteryHealth(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Failure";
            case BatteryManager.BATTERY_HEALTH_COLD: return "Cold";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default: return "Unknown";
        }
    }

    private String decodeBatteryPlugged(int plugged) {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC: return "AC";
            case BatteryManager.BATTERY_PLUGGED_USB: return "USB";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS: return "Wireless";
            default: return "Not plugged";
        }
    }

    // ============================================================
    // THERMAL (BEST EFFORT)
    // ============================================================
    private String getThermalInfo() {
        StringBuilder sb = new StringBuilder();

        // Best-effort: read some thermal zones if available
        File thermalDir = new File("/sys/class/thermal");
        if (!thermalDir.exists() || !thermalDir.isDirectory()) {
            sb.append("Thermal zones not accessible on this device.\n");
            return sb.toString();
        }

        File[] zones = thermalDir.listFiles();
        if (zones == null || zones.length == 0) {
            sb.append("No thermal zone entries found.\n");
            return sb.toString();
        }

        int count = 0;
        for (File zone : zones) {
            if (!zone.getName().startsWith("thermal_zone")) continue;
            File tempFile = new File(zone, "temp");
            if (!tempFile.exists()) continue;

            String type = readFirstLineSafe(new File(zone, "type"));
            String tempStr = readFirstLineSafe(tempFile);
            if (tempStr == null) continue;

            try {
                float value = Float.parseFloat(tempStr.trim());
                // Most devices: value in millidegrees
                if (value > 100) value = value / 1000f;
                sb.append(zone.getName()).append(" (").append(type).append("): ")
                        .append(String.format(Locale.US, "%.1f °C", value))
                        .append("\n");
                count++;
            } catch (Exception ignored) {}
        }

        if (count == 0) {
            sb.append("No readable thermal sensors (restricted by vendor).\n");
        }

        return sb.toString();
    }

    private String readFirstLineSafe(File f) {
        if (f == null || !f.exists()) return null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            return br.readLine();
        } catch (Exception ignored) {
            return null;
        } finally {
            if (br != null) {
                try { br.close(); } catch (IOException ignored) {}
            }
        }
    }

    // ============================================================
    // SENSORS
    // ============================================================
    private String getSensorsInfo() {
        StringBuilder sb = new StringBuilder();
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm == null) {
            sb.append("SensorManager not available.\n");
            return sb.toString();
        }

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        sb.append("Total sensors: ").append(sensors.size()).append("\n");

        for (Sensor s : sensors) {
            sb.append("- ").append(s.getName())
                    .append(" (type ").append(s.getType()).append(")\n");
            sb.append("  Vendor: ").append(s.getVendor()).append("\n");
            sb.append("  Version: ").append(s.getVersion()).append("\n");
            sb.append("  Max range: ").append(s.getMaximumRange()).append("\n");
            sb.append("  Power: ").append(s.getPower()).append(" mA\n");
        }

        return sb.toString();
    }

    // ============================================================
    // NETWORK (BASIC)
    // ============================================================
    private String getNetworkInfo() {
        StringBuilder sb = new StringBuilder();

        long rxMobile = TrafficStats.getMobileRxBytes();
        long txMobile = TrafficStats.getMobileTxBytes();
        long rxTotal = TrafficStats.getTotalRxBytes();
        long txTotal = TrafficStats.getTotalTxBytes();

        if (rxTotal == TrafficStats.UNSUPPORTED || txTotal == TrafficStats.UNSUPPORTED) {
            sb.append("Traffic stats unsupported on this device.\n");
        } else {
            sb.append("Mobile RX: ").append(formatBytes(rxMobile)).append("\n");
            sb.append("Mobile TX: ").append(formatBytes(txMobile)).append("\n");
            sb.append("Total RX : ").append(formatBytes(rxTotal)).append("\n");
            sb.append("Total TX : ").append(formatBytes(txTotal)).append("\n");
        }

        return sb.toString();
    }

    // ============================================================
    // UPTIME
    // ============================================================
    private String getUptimeInfo() {
        long uptimeMs = SystemClock.elapsedRealtime();
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        hours = hours % 24;
        minutes = minutes % 60;
        seconds = seconds % 60;

        return String.format(Locale.US,
                "Uptime: %d days, %02d:%02d:%02d\n",
                days, hours, minutes, seconds);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String formatBytes(long bytes) {
        if (bytes < 0) return "N/A";
        if (bytes < 1024) return bytes + " B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.2f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.2f MB", mb);
        float gb = mb / 1024f;
        if (gb < 1024) return String.format(Locale.US, "%.2f GB", gb);
        float tb = gb / 1024f;
        return String.format(Locale.US, "%.2f TB", tb);
    }
}
