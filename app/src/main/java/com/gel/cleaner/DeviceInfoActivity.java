package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.opengl.GLES10;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

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

        txtInfo.setText(buildFullInfo());
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_UP));
    }

    /* =========================================================
     * MAIN INFO BUILDER
     * ========================================================= */
    private String buildFullInfo() {
        StringBuilder sb = new StringBuilder();

        // -----------------------------------------------------
        // DEVICE
        // -----------------------------------------------------
        sb.append("📱 DEVICE\n\n");
        sb.append(line("Brand", Build.BRAND));
        sb.append(line("Manufacturer", Build.MANUFACTURER));
        sb.append(line("Model", Build.MODEL));
        sb.append(line("Codename", Build.DEVICE));
        sb.append(line("Product", Build.PRODUCT));
        sb.append(line("Board", Build.BOARD));
        sb.append(line("Hardware", Build.HARDWARE));
        sb.append(line("Bootloader", Build.BOOTLOADER));
        sb.append(line("Build ID", Build.ID));
        sb.append(line("Build Type", Build.TYPE));
        sb.append(line("Build Tags", Build.TAGS));
        sb.append(line("Fingerprint", Build.FINGERPRINT));
        sb.append("\n");

        // -----------------------------------------------------
        // ANDROID / OS
        // -----------------------------------------------------
        sb.append("🤖 ANDROID\n\n");
        sb.append(line("Android Version", Build.VERSION.RELEASE));
        sb.append(line("SDK", String.valueOf(Build.VERSION.SDK_INT)));
        sb.append(line("Security Patch", safe(getSecurityPatch())));
        sb.append(line("Kernel", getKernelVersion()));
        sb.append(line("Radio/Baseband", safe(getRadioVersionSafe())));
        sb.append(line("Java VM", System.getProperty("java.vm.version")));
        sb.append(line("Runtime", System.getProperty("java.runtime.version")));
        sb.append(line("Boot Time (ms)", String.valueOf(Build.TIME)));
        sb.append("\n");

        // -----------------------------------------------------
        // SCREEN
        // -----------------------------------------------------
        sb.append("🖥️ SCREEN\n\n");
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (wm != null) {
            wm.getDefaultDisplay().getRealMetrics(dm);
            sb.append(line("Resolution", dm.widthPixels + " x " + dm.heightPixels));
            sb.append(line("Density", dm.densityDpi + " dpi"));
            float refresh = wm.getDefaultDisplay().getRefreshRate();
            sb.append(line("Refresh Rate", String.format("%.1f Hz", refresh)));
        } else {
            sb.append(line("Resolution", "Unknown"));
        }
        sb.append("\n");

        // -----------------------------------------------------
        // CPU / SoC
        // -----------------------------------------------------
        sb.append("🧠 CPU / SoC\n\n");
        sb.append(line("Cores", String.valueOf(Runtime.getRuntime().availableProcessors())));
        sb.append(line("Supported ABIs", join(Build.SUPPORTED_ABIS)));
        sb.append(line("Supported 64-bit ABIs", join(Build.SUPPORTED_64_BIT_ABIS)));
        sb.append(line("CPU Model", getCpuField("model name", "processor")));
        sb.append(line("CPU Hardware", getCpuField("hardware", null)));
        sb.append(line("CPU Freq (cpu0)", getCpuFreqCpu0()));
        sb.append("\n");

        // -----------------------------------------------------
        // GPU
        // -----------------------------------------------------
        sb.append("🎮 GPU\n\n");
        String gpuVendor   = safeGlesString(GLES10.GL_VENDOR);
        String gpuRenderer = safeGlesString(GLES10.GL_RENDERER);
        String gpuVersion  = safeGlesString(GLES10.GL_VERSION);
        sb.append(line("Vendor", gpuVendor));
        sb.append(line("Renderer", gpuRenderer));
        sb.append(line("Version", gpuVersion));
        sb.append("\n");

        // -----------------------------------------------------
        // RAM
        // -----------------------------------------------------
        sb.append("💾 RAM\n\n");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            sb.append(line("Total", formatGB(mi.totalMem)));
            sb.append(line("Free", formatGB(mi.availMem)));
        } else {
            sb.append(line("Total", "Unknown"));
            sb.append(line("Free", "Unknown"));
        }
        sb.append("\n");

        // -----------------------------------------------------
        // STORAGE
        // -----------------------------------------------------
        sb.append("📂 STORAGE (Internal)\n\n");
        try {
            StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            long total = stat.getBlockCountLong() * stat.getBlockSizeLong();
            long free  = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            sb.append(line("Total", formatGB(total)));
            sb.append(line("Free", formatGB(free)));
        } catch (Throwable t) {
            sb.append(line("Total", "Unknown"));
            sb.append(line("Free", "Unknown"));
        }
        sb.append("\n");

        // -----------------------------------------------------
        // BATTERY
        // -----------------------------------------------------
        sb.append("🔋 BATTERY\n\n");
        appendBatteryInfo(sb);
        sb.append("\n");

        // -----------------------------------------------------
        // NETWORK (ΧΩΡΙΣ IMEI, ΧΩΡΙΣ MAC)
        // -----------------------------------------------------
        sb.append("📡 NETWORK\n\n");
        appendNetworkInfo(sb);
        sb.append("\n");

        // -----------------------------------------------------
        // SENSORS
        // -----------------------------------------------------
        sb.append("📟 SENSORS\n\n");
        appendSensors(sb);
        sb.append("\n");

        // -----------------------------------------------------
        // SYSTEM / UPTIME
        // -----------------------------------------------------
        sb.append("⏱️ SYSTEM\n\n");
        long up = android.os.SystemClock.uptimeMillis();
        long sinceBoot = android.os.SystemClock.elapsedRealtime();
        sb.append(line("Uptime", formatDuration(up)));
        sb.append(line("Since Boot", formatDuration(sinceBoot)));

        return sb.toString();
    }

    /* =========================================================
     * ANDROID HELPERS
     * ========================================================= */

    private String getSecurityPatch() {
        try {
            return Build.VERSION.SECURITY_PATCH;
        } catch (Throwable t) {
            return "Unknown";
        }
    }

    private String getRadioVersionSafe() {
        try {
            return Build.getRadioVersion();
        } catch (Throwable t) {
            return "Unknown";
        }
    }

    private String getKernelVersion() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/version"))) {
            String line = br.readLine();
            return line != null ? line : "Unknown";
        } catch (IOException e) {
            return "Unknown";
        }
    }

    /* =========================================================
     * CPU HELPERS
     * ========================================================= */

    private String getCpuField(String key1, String key2) {
        key1 = key1 == null ? null : key1.toLowerCase();
        key2 = key2 == null ? null : key2.toLowerCase();
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String lower = line.toLowerCase();
                if ((key1 != null && lower.startsWith(key1))
                        || (key2 != null && lower.startsWith(key2))) {
                    int idx = line.indexOf(':');
                    if (idx != -1) {
                        return line.substring(idx + 1).trim();
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return "Unknown";
    }

    private String getCpuFreqCpu0() {
        String min = readSys("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        String max = readSys("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (min.isEmpty() || max.isEmpty()) return "Unavailable";
        try {
            int minK = Integer.parseInt(min) / 1000;
            int maxK = Integer.parseInt(max) / 1000;
            return minK + " MHz → " + maxK + " MHz";
        } catch (NumberFormatException e) {
            return "Unavailable";
        }
    }

    private String readSys(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String l = br.readLine();
            return l != null ? l.trim() : "";
        } catch (IOException e) {
            return "";
        }
    }

    /* =========================================================
     * GPU HELPERS
     * ========================================================= */

    private String safeGlesString(int what) {
        try {
            String s = GLES10.glGetString(what);
            return s != null ? s : "Unknown";
        } catch (Throwable t) {
            return "Unknown";
        }
    }

    /* =========================================================
     * BATTERY HELPERS
     * ========================================================= */

    private void appendBatteryInfo(StringBuilder sb) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent i = registerReceiver(null, ifilter);
            if (i == null) {
                sb.append(line("Battery", "Unavailable"));
                return;
            }

            int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int temp  = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int volt  = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            String tech = i.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            if (level >= 0 && scale > 0) {
                float pct = 100f * level / scale;
                sb.append(line("Level", String.format("%.0f%%", pct)));
            }
            if (temp > 0) {
                sb.append(line("Temperature", String.format("%.1f °C", temp / 10.0f)));
            }
            if (volt > 0) {
                sb.append(line("Voltage", String.format("%.3f V", volt / 1000.0f)));
            }
            sb.append(line("Technology", safe(tech)));
            sb.append(line("Health", batteryHealthToText(health)));
            sb.append(line("Status", batteryStatusToText(status)));

        } catch (Throwable t) {
            sb.append(line("Battery", "Error reading info"));
        }
    }

    private String batteryHealthToText(int h) {
        switch (h) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Failure";
            case BatteryManager.BATTERY_HEALTH_COLD: return "Cold";
            default: return "Unknown";
        }
    }

    private String batteryStatusToText(int s) {
        switch (s) {
            case BatteryManager.BATTERY_STATUS_CHARGING: return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL: return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return "Not charging";
            default: return "Unknown";
        }
    }

    /* =========================================================
     * NETWORK (SAFE – ΧΩΡΙΣ IMEI/MAC)
     * ========================================================= */

    private void appendNetworkInfo(StringBuilder sb) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            sb.append(line("Active Network", "Unknown"));
            return;
        }

        Network active = cm.getActiveNetwork();
        if (active == null) {
            sb.append(line("Active Network", "None"));
            return;
        }

        NetworkCapabilities caps = cm.getNetworkCapabilities(active);
        if (caps == null) {
            sb.append(line("Active Network", "Unknown"));
            return;
        }

        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            sb.append(line("Active Network", "Wi-Fi"));
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            sb.append(line("Active Network", "Mobile Data"));
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            sb.append(line("Active Network", "Ethernet"));
        } else {
            sb.append(line("Active Network", "Other"));
        }
    }

    /* =========================================================
     * SENSORS
     * ========================================================= */

    private void appendSensors(StringBuilder sb) {
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm == null) {
            sb.append(line("Sensors", "Unavailable"));
            return;
        }

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        sb.append(line("Total Sensors", String.valueOf(sensors.size())));

        int limit = Math.min(sensors.size(), 25); // να μην γίνει βιβλίο 😄
        for (int i = 0; i < limit; i++) {
            Sensor s = sensors.get(i);
            sb.append("• ")
              .append(s.getName())
              .append(" (")
              .append(s.getVendor())
              .append(") type=")
              .append(s.getType())
              .append("\n");
        }
        if (sensors.size() > limit) {
            sb.append("• ... +").append(sensors.size() - limit).append(" more\n");
        }
    }

    /* =========================================================
     * SMALL UTILITIES
     * ========================================================= */

    private String line(String k, String v) {
        return "• " + k + ": " + safe(v) + "\n";
    }

    private String safe(String v) {
        return v == null || v.isEmpty() ? "Unknown" : v;
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

    private String formatGB(long bytes) {
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.2f GB", gb);
    }

    private String formatDuration(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        long hr  = min / 60;
        long day = hr / 24;
        return day + "d " + (hr % 24) + "h " + (min % 60) + "m";
    }
}
