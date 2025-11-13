package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
        scroll = findViewById(R.id.scrollDeviceInfo);

        txtInfo.setText(getFullInfo());
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_UP));
    }

    /* ============================================================
     *   FULL DEVICE INFORMATION
     * ============================================================ */
    private String getFullInfo() {
        StringBuilder sb = new StringBuilder();

        /* --------------------------------------------------------
         * BASIC INFORMATION
         * -------------------------------------------------------- */
        sb.append("📱 DEVICE INFORMATION\n\n");
        sb.append(line("Brand", Build.BRAND));
        sb.append(line("Manufacturer", Build.MANUFACTURER));
        sb.append(line("Model", Build.MODEL));
        sb.append(line("Codename", Build.DEVICE));
        sb.append(line("Product", Build.PRODUCT));
        sb.append(line("Board", Build.BOARD));
        sb.append(line("Hardware", Build.HARDWARE));
        sb.append(line("Bootloader", Build.BOOTLOADER));
        sb.append(line("Build ID", Build.ID));
        sb.append(line("Build Tags", Build.TAGS));
        sb.append(line("Fingerprint", Build.FINGERPRINT));
        sb.append("\n");

        /* --------------------------------------------------------
         * ANDROID / SYSTEM
         * -------------------------------------------------------- */
        sb.append("🤖 ANDROID\n");
        sb.append(line("Android Version", Build.VERSION.RELEASE));
        sb.append(line("SDK", Build.VERSION.SDK_INT + ""));
        sb.append(line("Security Patch", safe(Build.VERSION.SECURITY_PATCH)));
        sb.append(line("Kernel", getKernel()));
        sb.append(line("Baseband", safe(Build.getRadioVersion())));
        sb.append(line("Build Type", Build.TYPE));
        sb.append(line("Build Time", Build.TIME + ""));
        sb.append(line("Java VM", System.getProperty("java.vm.version")));
        sb.append(line("Runtime", System.getProperty("java.runtime.version")));
        sb.append("\n");

        /* --------------------------------------------------------
         * SCREEN
         * -------------------------------------------------------- */
        sb.append("🖥️ SCREEN\n");
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay().getRealMetrics(dm);

        sb.append(line("Resolution", dm.widthPixels + " x " + dm.heightPixels));
        sb.append(line("Density DPI", dm.densityDpi + " dpi"));
        sb.append(line("Refresh Rate", getWindowManager().getDefaultDisplay().getRefreshRate() + " Hz"));
        sb.append("\n");

        /* --------------------------------------------------------
         * CPU
         * -------------------------------------------------------- */
        sb.append("🧠 CPU / SOC\n");
        sb.append(line("Cores", Runtime.getRuntime().availableProcessors() + ""));
        sb.append(line("Supported ABIs", join(Build.SUPPORTED_ABIS)));
        sb.append(line("Supported 64-bit ABIs", join(Build.SUPPORTED_64_BIT_ABIS)));
        sb.append(line("SoC / Processor", getCpuModel()));
        sb.append(line("Hardware (proc)", getCpuHardware()));
        sb.append(line("Min/Max Freq", getCpuFreq()));
        sb.append("\n");

        /* --------------------------------------------------------
         * GPU
         * -------------------------------------------------------- */
        sb.append("🎮 GPU\n");
        sb.append(line("Vendor", GLES10.glGetString(GLES10.GL_VENDOR)));
        sb.append(line("Renderer", GLES10.glGetString(GLES10.GL_RENDERER)));
        sb.append(line("Version", GLES10.glGetString(GLES10.GL_VERSION)));
        sb.append("\n");

        /* --------------------------------------------------------
         * RAM
         * -------------------------------------------------------- */
        sb.append("💾 RAM\n");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        sb.append(line("Total", formatGB(mi.totalMem)));
        sb.append(line("Free", formatGB(mi.availMem)));
        sb.append("\n");

        /* --------------------------------------------------------
         * STORAGE
         * -------------------------------------------------------- */
        sb.append("📂 STORAGE\n");
        StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long total = s.getBlockCountLong() * s.getBlockSizeLong();
        long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
        sb.append(line("Total", formatGB(total)));
        sb.append(line("Free", formatGB(free)));
        sb.append("\n");

        /* --------------------------------------------------------
         * BATTERY
         * -------------------------------------------------------- */
        sb.append("🔋 BATTERY\n");
        appendBattery(sb);
        sb.append("\n");

        /* --------------------------------------------------------
         * BOOT / UPTIME
         * -------------------------------------------------------- */
        sb.append("⏱️ SYSTEM\n");
        sb.append(line("Uptime", formatMs(android.os.SystemClock.uptimeMillis())));
        sb.append(line("Boot since", formatMs(android.os.SystemClock.elapsedRealtime())));
        sb.append("\n");

        return sb.toString();
    }

    /* ============================================================
     * HELPERS
     * ============================================================ */

    private void appendBattery(StringBuilder sb) {
        Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i == null) return;

        int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        sb.append(line("Level", (100 * level / scale) + "%"));
        sb.append(line("Temp", (i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0) + " °C"));
        sb.append(line("Voltage", (i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000.0) + " V"));
        sb.append(line("Tech", safe(i.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY))));
        sb.append(line("Health", batteryHealth(i.getIntExtra(BatteryManager.EXTRA_HEALTH, 0))));
    }

    private String batteryHealth(int h) {
        switch (h) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Failure";
            default: return "Unknown";
        }
    }

    private String getKernel() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/version"))) {
            return br.readLine();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getCpuModel() {
        return readCpuField("model name");
    }

    private String getCpuHardware() {
        return readCpuField("hardware");
    }

    private String readCpuField(String key) {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            key = key.toLowerCase();
            while ((line = br.readLine()) != null) {
                String low = line.toLowerCase();
                if (low.startsWith(key)) {
                    return line.split(":", 2)[1].trim();
                }
            }
        } catch (Exception ignored) {}
        return "Unknown";
    }

    private String getCpuFreq() {
        String min = read("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        String max = read("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (min.isEmpty() || max.isEmpty()) return "Unavailable";
        return (Integer.parseInt(min) / 1000) + " MHz → " + (Integer.parseInt(max) / 1000) + " MHz";
    }

    private String read(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String line(String key, String value) {
        return "• " + key + ": " + value + "\n";
    }

    private String join(String[] arr) {
        if (arr == null) return "None";
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) s.append(", ");
            s.append(arr[i]);
        }
        return s.toString();
    }

    private String formatGB(long b) {
        return String.format("%.2f GB", b / 1024.0 / 1024.0 / 1024.0);
    }

    private String formatMs(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        long hr = min / 60;
        long day = hr / 24;
        return day + "d " + (hr % 24) + "h " + (min % 60) + "m";
    }

    private String safe(String s) {
        return (s == null ? "Unknown" : s);
    }
}
