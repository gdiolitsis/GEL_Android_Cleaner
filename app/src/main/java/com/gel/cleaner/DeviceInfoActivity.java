package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DeviceInfoActivity extends AppCompatActivity {

    private TextView txtInfo;
    private ScrollView scroll;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        txtInfo = findViewById(R.id.txtDeviceInfo);
        scroll  = findViewById(R.id.scrollDeviceInfo);

        String info = buildDeviceInfo();
        txtInfo.setText(info);

        // Scroll to bottom (just in case)
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_UP));
    }

    /* =========================================================
     * BUILD FULL DEVICE INFO
     * ========================================================= */
    private String buildDeviceInfo() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("📱 DEVICE INFO\n\n");

        /* -------- BASIC DEVICE -------- */
        sb.append("• Brand: ").append(Build.BRAND).append("\n");
        sb.append("• Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        sb.append("• Model: ").append(Build.MODEL).append("\n");
        sb.append("• Device codename: ").append(Build.DEVICE).append("\n");
        sb.append("• Product: ").append(Build.PRODUCT).append("\n");
        sb.append("• Board: ").append(Build.BOARD).append("\n");
        sb.append("• Hardware: ").append(Build.HARDWARE).append("\n");
        sb.append("• Bootloader: ").append(Build.BOOTLOADER).append("\n");
        sb.append("• Build ID: ").append(Build.ID).append("\n");
        sb.append("• Build Type: ").append(Build.TYPE).append("\n");
        sb.append("• Build Tags: ").append(Build.TAGS).append("\n\n");

        /* -------- ANDROID / OS -------- */
        sb.append("🤖 ANDROID\n");
        sb.append("• Version: ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("• SDK: ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("• Security Patch: ").append(getSecurityPatch()).append("\n");
        sb.append("• Kernel: ").append(getKernelVersion()).append("\n\n");

        /* -------- CPU / ABI -------- */
        sb.append("🧠 CPU\n");
        sb.append("• Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        sb.append("• ABIs: ").append(join(Build.SUPPORTED_ABIS)).append("\n");

        String cpuFromProc = getCpuFromProc();
        if (!cpuFromProc.isEmpty()) {
            sb.append("• CPU / SoC: ").append(cpuFromProc).append("\n");
        }
        sb.append("\n");

        /* -------- RAM -------- */
        sb.append("💾 RAM\n");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            sb.append("• Total: ").append(formatGB(mi.totalMem)).append("\n");
            sb.append("• Free:  ").append(formatGB(mi.availMem)).append("\n");
        }
        sb.append("\n");

        /* -------- STORAGE -------- */
        sb.append("📂 INTERNAL STORAGE\n");
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long total = stat.getBlockCountLong() * stat.getBlockSizeLong();
        long free  = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        sb.append("• Total: ").append(formatGB(total)).append("\n");
        sb.append("• Free:  ").append(formatGB(free)).append("\n\n");

        /* -------- BATTERY -------- */
        sb.append("🔋 BATTERY\n");
        appendBatteryInfo(sb);
        sb.append("\n");

        return sb.toString();
    }

    /* =========================================================
     * HELPERS
     * ========================================================= */

    private String getSecurityPatch() {
        try {
            return Build.VERSION.SECURITY_PATCH;
        } catch (Throwable t) {
            return "Unknown";
        }
    }

    private String getKernelVersion() {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/version"))) {
            line = br.readLine();
        } catch (IOException e) {
            return System.getProperty("os.version", "Unknown");
        }
        return line != null ? line : "Unknown";
    }

    private String getCpuFromProc() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            String hardware = "";
            String model = "";
            while ((line = br.readLine()) != null) {
                String lower = line.toLowerCase();
                if (lower.startsWith("hardware")) {
                    int idx = line.indexOf(':');
                    if (idx != -1) hardware = line.substring(idx + 1).trim();
                } else if (lower.startsWith("model name") || lower.startsWith("processor")) {
                    int idx = line.indexOf(':');
                    if (idx != -1) model = line.substring(idx + 1).trim();
                }
            }
            if (!model.isEmpty()) return model;
            return hardware;
        } catch (IOException e) {
            return "";
        }
    }

    private void appendBatteryInfo(StringBuilder sb) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);
            if (batteryStatus == null) {
                sb.append("• Battery info: unavailable\n");
                return;
            }

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int temp  = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int volt  = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            String tech = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            float pct = (scale > 0 && level >= 0) ? (100f * level / scale) : -1f;

            if (pct >= 0) sb.append("• Level: ").append(String.format("%.0f%%", pct)).append("\n");
            if (temp > 0) sb.append("• Temperature: ").append(temp / 10.0f).append(" °C\n");
            if (volt > 0) sb.append("• Voltage: ").append(volt / 1000.0f).append(" V\n");
            if (tech != null) sb.append("• Technology: ").append(tech).append("\n");
            sb.append("• Health: ").append(batteryHealthToString(health)).append("\n");

        } catch (Throwable t) {
            sb.append("• Battery info: error\n");
        }
    }

    private String batteryHealthToString(int h) {
        switch (h) {
            case BatteryManager.BATTERY_HEALTH_COLD: return "Cold";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Failure";
            default: return "Unknown";
        }
    }

    private String formatGB(long bytes) {
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.2f GB", gb);
    }

    private String join(String[] arr) {
        if (arr == null || arr.length == 0) return "Unknown";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
