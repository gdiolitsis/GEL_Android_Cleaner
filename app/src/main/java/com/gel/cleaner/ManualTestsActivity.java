// ============================================================
// ManualTestsActivity
// GEL Manual Tests — Foldable Ready Hospital Edition
// Full-screen scroll log with color-coded levels + summary
// NOTE: Όλο το αρχείο είναι έτοιμο για copy-paste (GEL rule).
// ============================================================

package com.gel.cleaner;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.Looper;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ManualTestsActivity — Foldable Ready Edition
 * • Uses GELAutoActivityHook for dp/sp auto-scaling
 * • All text sizes routed through sp()
 * • No local dp() override (so scaling works)
 */
public class ManualTestsActivity extends GELAutoActivityHook {

    private TextView txtDiag;
    private ScrollView scroll;
    private Handler ui;

    // Counters for final summary
    private int warnCount = 0;
    private int errorCount = 0;
    private boolean rooted = false;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scroll = new ScrollView(this);
        txtDiag = new TextView(this);

        txtDiag.setTextSize(sp(14f));
        txtDiag.setTextColor(0xFFE0E0E0);
        txtDiag.setPadding(dp(16), dp(16), dp(16), dp(16));
        txtDiag.setMovementMethod(new ScrollingMovementMethod());

        scroll.addView(txtDiag);
        setContentView(scroll);

        ui = new Handler(Looper.getMainLooper());

        GELServiceLog.clear();

        logTitle("🧪 GEL Manual Tests — Hospital Edition");
        logInfo("Device: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        logLine();

        runFullManualTests();
    }

    /* ============================================================
     * HTML + MIRROR LOG
     * ============================================================ */
    private void appendHtml(String html) {
        ui.post(() -> {
            CharSequence current = txtDiag.getText();
            CharSequence extra = Html.fromHtml(html + "<br>");
            txtDiag.setText(TextUtils.concat(current, extra));
            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    private void logTitle(String msg) {
        appendHtml("<b>" + escape(msg) + "</b>");
        GELServiceLog.info(msg);
    }

    private void logSection(String msg) {
        appendHtml("<br><b>▌ " + escape(msg) + "</b>");
        GELServiceLog.info("SECTION: " + msg);
    }

    private void logInfo(String msg) {
        appendHtml("ℹ️ " + escape(msg));
        GELServiceLog.info(msg);
    }

    // ✅ OK → Green
    private void logOk(String msg) {
        appendHtml("<font color='#66FF66'>✅ " + escape(msg) + "</font>");
        GELServiceLog.ok(msg);
    }

    // ⚠️ WARNING → Gold
    private void logWarn(String msg) {
        warnCount++;
        appendHtml("<font color='#FFD700'>⚠️ " + escape(msg) + "</font>");
        GELServiceLog.warn(msg);
    }

    // ❌ ERROR → Red
    private void logError(String msg) {
        errorCount++;
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
        GELServiceLog.error(msg);
    }

    private void logAccessDenied(String area) {
        String base = "Access denied or restricted (" + area + ")";
        logWarn(base);
    }

    private void logLine() {
        appendHtml("<font color='#666666'>────────────────────────────────</font>");
        GELServiceLog.info("------------------------------");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /* ============================================================
     * MAIN FLOW — MANUAL TESTS
     * ============================================================ */
    private void runFullManualTests() {
        new Thread(() -> {

            // LAB 0–3: Root & security baseline
            lab0RootIntegrity();
            lab1SelinuxAndDebug();
            lab2DangerousProperties();
            lab3MountsAndFs();

            // LAB 4–7: Hardware, CPU, RAM, Storage (+ I/O)
            lab4HardwareOs();
            lab5CpuCoresAndAbi();
            lab6RamStatus();
            lab7InternalStorageAndIo();

            // LAB 8–10: External storage, battery core, battery health
            lab8ExternalStorage();
            lab9BatteryCore();
            lab10BatteryHealth();

            // LAB 11–13: Thermal, network, WiFi
            lab11Thermals();
            lab12NetworkConnectivity();
            lab13WifiDetails();

            // LAB 14–16: Mobile radio, Bluetooth, sensors
            lab14MobileRadio();
            lab15Bluetooth();
            lab16SensorsOverview();

            // LAB 17–19: Display, GPU, Audio / vibration
            lab17Display();
            lab18GpuRenderer();
            lab19AudioAndVibration();

            // LAB 20–22: Camera, location (GPS + NFC), uptime
            lab20CameraSummary();
            lab21LocationGpsAndNfc();
            lab22SystemUptime();

            // LAB 23–25: Apps footprint, security patch age, power optimizations
            lab23AppsFootprint();
            lab24SecurityPatch();
            lab25PowerOptimizations();

            // LAB 26–28: Accessibility, special permissions, live RAM / pressure
            lab26AccessibilityServices();
            lab27SpecialPermissions();
            lab28LiveRamPressure();

            // LAB 29: Final clinical summary
            lab29FinalSummary();

            logLine();
            logOk("Manual tests completed. Review all ⚠️ and ❌ entries with the customer.");

        }).start();
    }

/* ============================================================
     * LAB 0 — Root / System Integrity
     * ============================================================ */
    private void lab0RootIntegrity() {
        logSection("LAB 0 — Root / System Integrity");

        rooted = isDeviceRootedBasic();
        if (rooted) {
            logError("Device appears ROOTED or modified (test-keys / su binary / Superuser traces).");
            logInfo("From a service-lab perspective this is acceptable, but system security is reduced.");
        } else {
            logOk("No direct root indicators found. Device looks locked / non-rooted.");
        }
        logLine();
    }

    /* ============================================================
     * LAB 1 — SELinux / Debug / ADB
     * ============================================================ */
    private void lab1SelinuxAndDebug() {
        logSection("LAB 1 — SELinux / Debug / ADB");

        try {
            boolean enabled = false;
            boolean enforced = false;

            try {
                Class<?> clazz = Class.forName("android.os.SELinux");
                enabled = (boolean) clazz.getMethod("isSELinuxEnabled").invoke(null);
                enforced = (boolean) clazz.getMethod("isSELinuxEnforced").invoke(null);
            } catch (Throwable ignored) {
                logAccessDenied("SELinux API");
            }

            logInfo("SELinux enabled: " + enabled + " | enforced: " + enforced);

            if (!enabled) logError("SELinux is disabled — security isolation is weak.");
            else if (!enforced) logWarn("SELinux is permissive — weaker policy enforcement.");
            else logOk("SELinux is enforcing — expected on modern Android.");
        } catch (Throwable t) {
            logWarn("Failed to read SELinux status: " + t.getMessage());
        }

        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            int dev = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            logInfo("ADB enabled: " + (adb == 1));
            logInfo("Developer options enabled: " + (dev == 1));

            if (adb == 1) logWarn("ADB is enabled — only recommended for developers/service usage.");
        } catch (Throwable t) {
            logAccessDenied("ADB / Developer settings");
        }

        logLine();
    }

    /* ============================================================
     * LAB 2 — Dangerous System Properties
     * ============================================================ */
    private void lab2DangerousProperties() {
        logSection("LAB 2 — Dangerous System Properties");

        checkProp("ro.debuggable", "1", "ro.debuggable=1 — debug build; not recommended for production.");
        checkProp("ro.secure", "0", "ro.secure=0 — low security mode.");
        checkProp("ro.boot.verifiedbootstate", "orange", "Verified Boot state: ORANGE (integrity warnings).");
        checkProp("ro.boot.verifiedbootstate", "red", "Verified Boot state: RED (integrity compromised).");

        logLine();
    }

    private void checkProp(String key, String expected, String msg) {
        try {
            Process p = Runtime.getRuntime().exec("getprop " + key);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String value = br.readLine();
            br.close();

            if (value == null) {
                logWarn("Property " + key + " returned NULL.");
                return;
            }

            if (value.equalsIgnoreCase(expected)) {
                logError(msg);  // intentionally error-level if unsafe
            } else {
                logOk(key + "=" + value + " (normal)");
            }
        } catch (Exception e) {
            logError("checkProp error for " + key + ": " + e.getMessage());
        }
    }

    /* ============================================================
     * LAB 3 — Mounts / File System Flags
     * ============================================================ */
    private void lab3MountsAndFs() {
        logSection("LAB 3 — System Mounts / File System Flags");

        BufferedReader r = null;
        try {
            Process p;
            try {
                p = Runtime.getRuntime().exec(new String[]{"su", "-c", "mount"});
            } catch (Exception e) {
                p = Runtime.getRuntime().exec("mount");
            }

            r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            boolean systemRW = false;

            while ((line = r.readLine()) != null) {
                String low = line.toLowerCase(Locale.US);
                if (low.contains(" /system ") && low.contains("(rw,")) systemRW = true;
            }

            if (systemRW)
                logError("/system is mounted read-write — strong sign of modification/root.");
            else
                logOk("/system is not mounted read-write (expected for stock builds).");

        } catch (Exception e) {
            logAccessDenied("mount table");
        } finally {
            try { if (r != null) r.close(); } catch (Exception ignored) {}
        }

        logLine();
    }

    /* ============================================================
     * LAB 4 — Hardware / OS
     * ============================================================ */
    private void lab4HardwareOs() {
        logSection("LAB 4 — Hardware / OS Overview");

        logInfo("Manufacturer: " + Build.MANUFACTURER);
        logInfo("Model: " + Build.MODEL);
        logInfo("Device: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);

        if (Build.VERSION.SDK_INT >= 31) {
            String soc = Build.SOC_MODEL != null ? Build.SOC_MODEL : "N/A";
            logInfo("SoC model (reported): " + soc);
        }

        int api = Build.VERSION.SDK_INT;
        logInfo("Android version: " + Build.VERSION.RELEASE + " (API " + api + ")");

        if (api < 26) logError("Android < 8 — heavily outdated and insecure.");
        else if (api < 30) logWarn("Android < 11 — may miss modern privacy and security features.");
        else logOk("Android version is modern for daily usage.");

        logLine();
    }

    /* ============================================================
     * LAB 5 — CPU Cores / ABI
     * ============================================================ */
    private void lab5CpuCoresAndAbi() {
        logSection("LAB 5 — CPU / Cores / ABI");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU cores detected: " + cores);

        if (cores <= 4)
            logWarn("Low core count (≤4) for heavy multitasking and modern workloads.");
        else
            logOk("CPU core count is adequate for everyday usage.");

        String abi = (Build.SUPPORTED_ABIS != null && Build.SUPPORTED_ABIS.length > 0)
                ? Build.SUPPORTED_ABIS[0]
                : Build.CPU_ABI;
        logInfo("Primary ABI: " + abi);

        String maxFreq = readFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (maxFreq != null) {
            try {
                long khz = Long.parseLong(maxFreq.trim());
                double ghz = khz / 1_000_000.0;
                logInfo(String.format(Locale.US, "CPU0 max frequency: %.2f GHz (reported)", ghz));
            } catch (Exception e) {
                logInfo("CPU0 max frequency (raw): " + maxFreq.trim());
            }
        } else {
            logAccessDenied("CPU frequency sysfs");
        }

        logLine();
    }

    /* ============================================================
     * LAB 6 — RAM / Memory Pressure
     * ============================================================ */
    private void lab6RamStatus() {
        logSection("LAB 6 — RAM / Memory Pressure");

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                logError("Cannot access ActivityManager — RAM check unavailable.");
                logLine();
                return;
            }

            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long total = mi.totalMem;
            long avail = mi.availMem;
            long used = total - avail;

            double freePercent = (total > 0) ? (avail * 100.0 / total) : 0;

            logInfo(String.format(Locale.US,
                    "RAM used: %s / %s (free %s)",
                    readable(used), readable(total), readable(avail)));

            if (freePercent < 5.0) {
                logError(String.format(Locale.US,
                        "Very low free RAM (%.1f%%) — heavy lag and app kills expected.", freePercent));
            } else if (freePercent < 15.0) {
                logWarn(String.format(Locale.US,
                        "Low free RAM (%.1f%%) — performance may be degraded under load.", freePercent));
            } else {
                logOk(String.format(Locale.US,
                        "Free RAM %.1f%% — acceptable for normal use.", freePercent));
            }

            if (mi.lowMemory)
                logError("System reports LOW MEMORY state — Android is aggressively killing apps.");

            int memClass = am.getMemoryClass();
            int largeClass = am.getLargeMemoryClass();
            logInfo("App memory class: " + memClass + " MB (large heap class: " + largeClass + " MB)");

        } catch (Exception e) {
            logError("RAM diagnostics error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 7 — Internal Storage + I/O micro benchmark
     * ============================================================ */
    private void lab7InternalStorageAndIo() {
        logSection("LAB 7 — Internal Storage + I/O");

        try {
            File data = Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
            long used = total - free;

            int pctFree = (int) ((free * 100L) / total);

            logInfo("Internal storage used: " + readable(used) + " / " + readable(total) +
                    " (free " + readable(free) + ", " + pctFree + "%)");

            if (pctFree < 5)
                logError("Free space below 5% — critical risk of crashes and failed updates.");
            else if (pctFree < 10)
                logWarn("Free space below 10% — slow performance and update issues possible.");
            else
                logOk("Internal storage free space is within safe limits.");

        } catch (Exception e) {
            logError("Internal storage error: " + e.getMessage());
        }

        runInternalIoBenchmark();
        logLine();
    }

    private void runInternalIoBenchmark() {
        File cache = getCacheDir();
        if (cache == null) {
            logAccessDenied("cache dir (I/O benchmark)");
            return;
        }

        File testFile = new File(cache, "gel_io_benchmark.tmp");
        int mbToTest = 4;
        byte[] buf = new byte[1024 * 1024];

        try {
            long totalBytes = mbToTest * 1024L * 1024L;

            long start = System.nanoTime();
            FileOutputStream fos = new FileOutputStream(testFile);
            for (int i = 0; i < mbToTest; i++) {
                fos.write(buf);
            }
            fos.flush();
            fos.close();
            long writeMs = (System.nanoTime() - start) / 1_000_000L;

            float writeSpeed = (writeMs > 0)
                    ? (totalBytes / 1024f / 1024f) / (writeMs / 1000f)
                    : 0f;

            logInfo(String.format(Locale.US,
                    "I/O write: %d MB in %d ms (%.1f MB/s)", mbToTest, writeMs, writeSpeed));

            start = System.nanoTime();
            FileInputStream fis = new FileInputStream(testFile);
            while (fis.read(buf) != -1) { }
            fis.close();
            long readMs = (System.nanoTime() - start) / 1_000_000L;

            float readSpeed = (readMs > 0)
                    ? (totalBytes / 1024f / 1024f) / (readMs / 1000f)
                    : 0f;

            logInfo(String.format(Locale.US,
                    "I/O read: %d MB in %d ms (%.1f MB/s)", mbToTest, readMs, readSpeed));

            if (writeSpeed < 5f || readSpeed < 5f) {
                logWarn("Internal I/O speed is relatively low — installs and updates may feel slow.");
            } else {
                logOk("Internal I/O performance is acceptable for daily use.");
            }

        } catch (Exception e) {
            logAccessDenied("I/O benchmark (" + e.getMessage() + ")");
        } finally {
            try { if (testFile.exists()) testFile.delete(); } catch (Exception ignored) {}
        }
    }

/* ============================================================
     * LAB 8 — External Storage (if any)
     * ============================================================ */
    private void lab8ExternalStorage() {
        logSection("LAB 8 — External Storage (SD / Secondary)");

        try {
            File ext = getExternalFilesDir(null);
            if (ext == null) {
                logInfo("No external storage directory reported for this app.");
                logLine();
                return;
            }

            StatFs s = new StatFs(ext.getAbsolutePath());
            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();

            logInfo("External (app) storage: " + readable(free) + " free / " + readable(total) + " total.");
            logOk("External storage is accessible for this application.");

        } catch (Exception e) {
            logAccessDenied("External storage stats");
        }

        logLine();
    }

    /* ============================================================
     * LAB 9 — Battery Core Metrics
     * ============================================================ */
    private void lab9BatteryCore() {
        logSection("LAB 9 — Battery Core Metrics");

        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent i = registerReceiver(null, filter);
            if (i == null) {
                logAccessDenied("Battery status broadcast");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (scale > 0) ? (100f * lvl / scale) : -1f;

            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp > 0 ? (rawTemp / 10f) : -1f;

            logInfo(String.format(Locale.US, "Battery level: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Battery temperature: %.1f°C", temp));

            if (pct >= 0 && pct <= 5)
                logError("Battery almost empty — risk of sudden shutdown.");
            else if (pct <= 15)
                logWarn("Battery low — user should charge soon.");

            if (temp > 45f)
                logError("Battery temperature is very high — possible damage or poor cooling.");
            else if (temp > 40f)
                logWarn("Battery temperature is high — monitor under heavy use.");

        } catch (Exception e) {
            logError("Battery core metrics error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 10 — Battery Health & Status
     * ============================================================ */
    private void lab10BatteryHealth() {
        logSection("LAB 10 — Battery Health & Status");

        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent i = registerReceiver(null, filter);
            if (i == null) {
                logAccessDenied("Battery health");
                logLine();
                return;
            }

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
            int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            String healthStr;
            boolean bad = false;

            switch (health) {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthStr = "GOOD";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthStr = "DEAD";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthStr = "OVERHEAT / OVERVOLTAGE";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthStr = "UNSPECIFIED FAILURE";
                    bad = true;
                    break;
                default:
                    healthStr = "UNKNOWN or OEM-specific";
                    break;
            }

            logInfo("Battery health (Android): " + healthStr);
            if (bad)
                logError("Battery health reported outside normal range — replacement may be required.");
            else if (health == BatteryManager.BATTERY_HEALTH_GOOD)
                logOk("Battery health is within normal range (as reported by Android).");

            String statusStr;
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusStr = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusStr = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusStr = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusStr = "Not charging";
                    break;
                default:
                    statusStr = "Unknown";
            }
            logInfo("Battery status: " + statusStr);

        } catch (Exception e) {
            logError("Battery health diagnostics error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 11 — Thermal Sensors
     * ============================================================ */
    private void lab11Thermals() {
        logSection("LAB 11 — Thermal Sensors");

        if (Build.VERSION.SDK_INT >= 29) {
            try {
                HardwarePropertiesManager hpm =
                        (HardwarePropertiesManager) getSystemService(HARDWARE_PROPERTIES_SERVICE);

                if (hpm != null) {
                    float[] cpuTemps = hpm.getDeviceTemperatures(
                            HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
                            HardwarePropertiesManager.TEMPERATURE_CURRENT);

                    if (cpuTemps != null && cpuTemps.length > 0) {
                        float t = cpuTemps[0];
                        logInfo("CPU temperature: " + t + "°C (reported)");

                        if (t > 80f)
                            logError("CPU temperature is extremely high — throttling or damage possible.");
                        else if (t > 70f)
                            logWarn("CPU temperature is high — device may throttle under load.");
                        else
                            logOk("CPU temperature appears within acceptable range.");
                    } else {
                        logAccessDenied("CPU thermal sensors (no data)");
                    }
                } else {
                    logAccessDenied("HardwarePropertiesManager");
                }

            } catch (Exception e) {
                logAccessDenied("Thermal sensors");
            }
        } else {
            logAccessDenied("Thermal API < 29");
        }

        logLine();
    }

    /* ============================================================
     * LAB 12 — Network Connectivity
     * ============================================================ */
    private void lab12NetworkConnectivity() {
        logSection("LAB 12 — Network Connectivity");

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) {
                logAccessDenied("ConnectivityManager");
                logLine();
                return;
            }

            boolean online = false;
            String transport = "UNKNOWN";

            if (Build.VERSION.SDK_INT >= 23) {
                android.net.Network n = cm.getActiveNetwork();
                NetworkCapabilities caps = cm.getNetworkCapabilities(n);
                if (caps != null) {
                    online = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        transport = "WIFI";
                    } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        transport = "CELLULAR";
                    } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        transport = "ETHERNET";
                    }
                }
            } else {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                online = ni != null && ni.isConnected();
                if (ni != null) transport = ni.getTypeName();
            }

            if (!online) {
                logError("No active Internet connectivity detected.");
            } else {
                logOk("Internet connectivity: ACTIVE (" + transport + ")");
            }

        } catch (Exception e) {
            logAccessDenied("Network state");
        }

        logLine();
    }

    /* ============================================================
     * LAB 13 — WiFi Details
     * ============================================================ */
    private void lab13WifiDetails() {
        logSection("LAB 13 — WiFi Signal & Link");

        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wm == null) {
                logAccessDenied("WifiManager");
                logLine();
                return;
            }

            if (!wm.isWifiEnabled()) {
                logWarn("WiFi is disabled.");
                logLine();
                return;
            }

            WifiInfo info = wm.getConnectionInfo();
            if (info == null || info.getNetworkId() == -1) {
                logWarn("WiFi enabled but not connected to any access point.");
                logLine();
                return;
            }

            int rssi = info.getRssi();
            int linkSpeed = info.getLinkSpeed();

            logInfo("SSID: " + info.getSSID());
            logInfo("RSSI: " + rssi + " dBm");
            logInfo("Link speed: " + linkSpeed + " Mbps");

            if (rssi > -65)
                logOk("WiFi signal is strong for normal usage.");
            else if (rssi > -80)
                logWarn("WiFi signal is moderate — possible instability at distance.");
            else
                logError("WiFi signal is very weak — disconnections and slow speeds expected.");

        } catch (Exception e) {
            logError("WiFi diagnostics error: " + e.getMessage());
        }

        logLine();
    }

/* ============================================================
     * LAB 14 — Mobile Radio
     * ============================================================ */
    private void lab14MobileRadio() {
        logSection("LAB 14 — Mobile Radio / Operator");

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm == null) {
                logAccessDenied("TelephonyManager");
                logLine();
                return;
            }

            String netOp = tm.getNetworkOperatorName();
            String simOp = tm.getSimOperatorName();

            logInfo("Network operator: " + (netOp == null ? "N/A" : netOp));
            logInfo("SIM operator: " + (simOp == null ? "N/A" : simOp));

            logOk("Mobile radio basic information collected.");

        } catch (Exception e) {
            logAccessDenied("Telephony service");
        }

        logLine();
    }

    /* ============================================================
     * LAB 15 — Bluetooth Status
     * ============================================================ */
    private void lab15Bluetooth() {
        logSection("LAB 15 — Bluetooth Status");

        try {
            BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
            if (bt == null) {
                logInfo("This device reports no Bluetooth adapter.");
                logLine();
                return;
            }

            boolean enabled;
            try {
                enabled = bt.isEnabled();
            } catch (SecurityException se) {
                logAccessDenied("Bluetooth status (permissions)");
                logLine();
                return;
            }

            logInfo("Bluetooth present: YES");
            logInfo("Bluetooth enabled: " + enabled);

            if (enabled)
                logOk("Bluetooth is available and enabled.");
            else
                logWarn("Bluetooth is disabled at the moment.");

        } catch (Exception e) {
            logError("Bluetooth diagnostics error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 16 — Sensors Overview
     * ============================================================ */
    private void lab16SensorsOverview() {
        logSection("LAB 16 — Sensors Overview");

        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sm == null) {
                logAccessDenied("SensorManager");
                logLine();
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            int count = (all == null ? 0 : all.size());
            logInfo("Total sensors reported: " + count);

            checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");
            checkSensor(sm, Sensor.TYPE_GYROSCOPE, "Gyroscope");
            checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer");
            checkSensor(sm, Sensor.TYPE_LIGHT, "Light sensor");
            checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");
            checkSensor(sm, Sensor.TYPE_PRESSURE, "Barometer (pressure)");
            checkSensor(sm, Sensor.TYPE_GRAVITY, "Gravity sensor");

        } catch (Exception e) {
            logError("Sensor diagnostics error: " + e.getMessage());
        }

        logLine();
    }

    private void checkSensor(SensorManager sm, int type, String name) {
        boolean ok = sm.getDefaultSensor(type) != null;
        if (!ok) logWarn(name + " not reported by this device.");
        else logOk(name + " reported as available.");
    }

    /* ============================================================
     * LAB 17 — Display / Resolution / Density
     * ============================================================ */
    private void lab17Display() {
        logSection("LAB 17 — Display / Resolution / Density");

        try {
            DisplayMetrics dm = new DisplayMetrics();

            if (Build.VERSION.SDK_INT >= 30) {
                Display disp = getDisplay();
                if (disp != null) disp.getRealMetrics(dm);
                else getWindowManager().getDefaultDisplay().getMetrics(dm);
            } else {
                getWindowManager().getDefaultDisplay().getMetrics(dm);
            }

            int w = dm.widthPixels;
            int h = dm.heightPixels;
            float density = dm.density;
            int dpi = dm.densityDpi;

            logInfo("Resolution: " + w + " × " + h + " px");
            logInfo("Density: " + density + " (DPI " + dpi + ")");

            if (w >= 1080 && h >= 1920)
                logOk("Display resolution is suitable for modern apps.");
            else if (w >= 720 && h >= 1280)
                logWarn("Display resolution is mid-range — some UI elements may look compact.");
            else
                logError("Very low display resolution for modern applications.");

        } catch (Exception e) {
            logError("Display diagnostics error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 18 — GPU / Renderer Info
     * ============================================================ */
    private void lab18GpuRenderer() {
        logSection("LAB 18 — GPU / Renderer (Best-effort)");

        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (am != null) {
                ConfigurationInfo ci = am.getDeviceConfigurationInfo();
                int gl = ci.reqGlEsVersion;
                int major = ((gl & 0xffff0000) >> 16);
                int minor = (gl & 0x0000ffff);
                logInfo("Reported OpenGL ES level: " + major + "." + minor);
            } else {
                logAccessDenied("ActivityManager (GPU config)");
            }
        } catch (Exception e) {
            logAccessDenied("GPU / GL ES info");
        }

        logInfo("For advanced GPU profiling, run a dedicated benchmark or profiling tool.");
        logOk("GPU information lab recorded (informational only).");
        logLine();
    }

    /* ============================================================
     * LAB 19 — Audio / Vibration Capability
     * ============================================================ */
    private void lab19AudioAndVibration() {
        logSection("LAB 19 — Audio / Vibration Capability");

        try {
            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (am == null) {
                logAccessDenied("AudioManager");
            } else {
                int music = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                int ring = am.getStreamVolume(AudioManager.STREAM_RING);
                int maxMusic = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int maxRing = am.getStreamMaxVolume(AudioManager.STREAM_RING);

                logInfo("Music volume: " + music + " / " + maxMusic);
                logInfo("Ring volume: " + ring + " / " + maxRing);

                if (music == 0 && ring == 0)
                    logWarn("Both music and ring volumes are at 0 — user may think speaker is faulty.");
                else
                    logOk("Audio volumes are non-zero.");
            }
        } catch (Exception e) {
            logError("Audio diagnostics error: " + e.getMessage());
        }

        logInfo("Vibration motor test is available in Manual Tests module (not executed here).");
        logLine();
    }

    /* ============================================================
     * LAB 20 — Camera Hardware Summary
     * ============================================================ */
    private void lab20CameraSummary() {
        logSection("LAB 20 — Camera Hardware Summary (Best-effort)");

        try {
            PackageManager pm = getPackageManager();
            boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
            boolean hasFront = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

            logInfo("Any camera present: " + hasCamera);
            logInfo("Front camera present: " + hasFront);

            if (!hasCamera)
                logError("No camera hardware reported by system features.");
            else
                logOk("Camera hardware is reported as present by the system.");

        } catch (Exception e) {
            logError("Camera feature check error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 21 — Location / GPS / NFC
     * ============================================================ */
    private void lab21LocationGpsAndNfc() {
        logSection("LAB 21 — Location / GPS / NFC");

        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (lm == null) {
                logAccessDenied("LocationManager");
            } else {
                boolean gps, network;
                try {
                    gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch (Exception e) {
                    logAccessDenied("Location provider status");
                    gps = false;
                    network = false;
                }

                logInfo("GPS provider enabled: " + gps);
                logInfo("Network location enabled: " + network);

                if (!gps && !network)
                    logWarn("All location providers are disabled — location-based apps may fail.");
                else
                    logOk("At least one location provider is enabled.");
            }
        } catch (Exception e) {
            logError("Location diagnostics error: " + e.getMessage());
        }

        try {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
            if (nfc == null) {
                logInfo("NFC hardware: not reported by this device.");
            } else {
                logInfo("NFC supported: YES, enabled=" + nfc.isEnabled());
            }
        } catch (Exception e) {
            logAccessDenied("NFC state");
        }

        logLine();
    }

    /* ============================================================
     * LAB 22 — System Uptime / Reboot
     * ============================================================ */
    private void lab22SystemUptime() {
        logSection("LAB 22 — System Uptime");

        long upMs = SystemClock.elapsedRealtime();
        String upStr = formatDuration(upMs);

        logInfo("System uptime since last boot: " + upStr);

        if (upMs < 2 * 60 * 60 * 1000L) {
            logWarn("System was rebooted recently (≤ 2 hours) — some issues may be transient.");
        } else {
            logOk("System has been running for a reasonable amount of time.");
        }

        logLine();
    }

    /* ============================================================
     * LAB 23 — Installed Apps Footprint
     * ============================================================ */
    private void lab23AppsFootprint() {
        logSection("LAB 23 — Installed Apps Footprint");

        try {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            if (apps == null) {
                logAccessDenied("Installed applications list");
                logLine();
                return;
            }

            int userApps = 0;
            int systemApps = 0;
            int disabledApps = 0;

            for (ApplicationInfo ai : apps) {
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    systemApps++;
                else
                    userApps++;

                if (!ai.enabled) disabledApps++;
            }

            logInfo("User-installed apps: " + userApps);
            logInfo("System apps: " + systemApps);
            logInfo("Disabled apps: " + disabledApps);
            logInfo("Total installed packages: " + apps.size());

            if (userApps > 120)
                logError("Very high number of user apps — strong risk of background drain and slowdowns.");
            else if (userApps > 80)
                logWarn("High number of user apps — possible performance impact.");
            else
                logOk("App footprint is within normal range.");

        } catch (Exception e) {
            logError("Apps footprint diagnostics error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 24 — Security Patch / OS Age
     * ============================================================ */
    private void lab24SecurityPatch() {
        logSection("LAB 24 — Security Patch Level");

        try {
            String patch = Build.VERSION.SECURITY_PATCH;
            if (patch == null || patch.trim().isEmpty()) {
                logInfo("Security patch level: not reported (OEM-specific or pre-6.0).");
                logWarn("Cannot verify security patch level — treat as unknown risk.");
            } else {
                logInfo("Security patch level (from Android): " + patch);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date patchDate = sdf.parse(patch);
                    if (patchDate != null) {
                        long diffMs = System.currentTimeMillis() - patchDate.getTime();
                        long diffDays = diffMs / (1000L * 60L * 60L * 24L);

                        logInfo("Security patch age (approx): " + diffDays + " days");

                        if (diffDays > 730) {
                            logError("Security patch is older than ~2 years — high security risk.");
                        } else if (diffDays > 365) {
                            logWarn("Security patch is older than ~1 year — consider updating.");
                        } else {
                            logOk("Security patch is relatively recent for typical use.");
                        }
                    }
                } catch (Exception ignored) {
                    logWarn("Could not parse security patch date for age calculation.");
                }
            }
        } catch (Exception e) {
            logAccessDenied("Security patch property");
        }

        logLine();
    }

    /* ============================================================
     * LAB 25 — Power Optimizations / Doze
     * ============================================================ */
    private void lab25PowerOptimizations() {
        logSection("LAB 25 — Power Optimizations / Doze (Best-effort)");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            logInfo("Doze & app standby are supported on this Android version.");

            try {
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (pm != null) {
                    boolean ignoring = pm.isIgnoringBatteryOptimizations(getPackageName());
                    logInfo("This app ignoring battery optimizations: " + ignoring);
                }
            } catch (Exception e) {
                logAccessDenied("Battery optimizations state for this app");
            }

            logOk("Power optimization framework is available; per-app whitelists must be reviewed manually.");
        } else {
            logWarn("This Android version does not support modern Doze / standby optimizations.");
        }

        logLine();
    }

    /* ============================================================
     * LAB 26 — Accessibility Services
     * ============================================================ */
    private void lab26AccessibilityServices() {
        logSection("LAB 26 — Accessibility Services (Best-effort)");

        try {
            String enabled = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );

            if (enabled == null || enabled.isEmpty()) {
                logInfo("No accessibility services reported as enabled.");
                logOk("Accessibility services baseline appears normal.");
            } else {
                logInfo("Enabled accessibility services (raw): " + enabled);
                logWarn("At least one accessibility service is enabled — verify that all are trusted.");
            }
        } catch (Exception e) {
            logAccessDenied("Accessibility services list");
        }

        try {
            float scale = Settings.Global.getFloat(
                    getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1f
            );
            logInfo("Animator duration scale: " + scale);
        } catch (Exception e) {
            logAccessDenied("Animator duration scale");
        }

        logLine();
    }

    /* ============================================================
     * LAB 27 — Special Permissions Snapshot
     * ============================================================ */
    private void lab27SpecialPermissions() {
        logSection("LAB 27 — Special Permissions Snapshot");

        try {
            boolean usageStatsGranted = false;
            try {
                int mode = Settings.Secure.getInt(
                        getContentResolver(),
                        "usage_stats_enabled", 0
                );
                usageStatsGranted = (mode == 1);
            } catch (Exception ignored) { }

            if (usageStatsGranted) {
                logWarn("Usage stats access appears enabled — verify which apps hold this permission.");
            } else {
                logInfo("No obvious usage stats permission flags detected from this context.");
            }

        } catch (Exception e) {
            logAccessDenied("Usage stats permission flags");
        }

        logLine();
    }

    /* ============================================================
     * LAB 28 — Live RAM Snapshot
     * ============================================================ */
    private void lab28LiveRamPressure() {
        logSection("LAB 28 — Live RAM Snapshot (Second Pass)");

        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);
                long free = mi.availMem;
                long total = mi.totalMem;
                float pct = total > 0 ? (100f * free / total) : -1f;

                String ramLine = String.format(
                        Locale.US,
                        "Live RAM now: %s (%.0f%% free)",
                        readable(free),
                        pct
                );
                logInfo(ramLine);

                if (pct >= 25f)
                    logOk("Live RAM status acceptable at the moment of this test.");
                else
                    logWarn("Live RAM is relatively low at this moment.");
            } else {
                logAccessDenied("ActivityManager (live RAM)");
            }
        } catch (Exception e) {
            logError("Live RAM check error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 29 — Final Summary / Recommendations
     * ============================================================ */
    private void lab29FinalSummary() {
        logSection("LAB 29 — Final Summary & Recommendations");

        logInfo("Total warnings detected: " + warnCount);
        logInfo("Total critical issues detected: " + errorCount);

        if (errorCount == 0 && warnCount == 0) {
            logOk("No warnings or critical issues detected. Device is in excellent condition.");
        } else if (errorCount == 0 && warnCount > 0) {
            logWarn("No critical failures, but there are warnings that should be explained to the customer.");
        } else if (errorCount > 0) {
            logError("Critical issues present. Recommend detailed service consultation and, if needed, hardware checks.");
        }

        if (rooted) {
            logWarn("Since the device appears rooted, some protections are bypassed. Document this clearly in the report.");
        }

        logInfo("Use this automatic report together with Manual Tests (audio, display, sensors, input, charging) for a complete service diagnosis.");
        logLine();
    }

    /* ============================================================
     * ROOT HELPERS
     * ============================================================ */
    private boolean isDeviceRootedBasic() {
        return hasTestKeys() || hasSuBinary() || hasSuperUserApk() || whichSu();
    }

    private boolean hasTestKeys() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private boolean hasSuBinary() {
        String[] paths = new String[]{
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/bin/.ext/su", "/system/usr/we-need-root/su"
        };
        try {
            for (String p : paths) {
                if (new File(p).exists()) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean hasSuperUserApk() {
        try {
            return new File("/system/app/Superuser.apk").exists();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean whichSu() {
        BufferedReader in = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"which", "su"});
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            return line != null;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (in != null) try { in.close(); } catch (Exception ignored) {}
        }
    }

    /* ============================================================
     * SMALL HELPERS
     * ============================================================ */
    private String readFirstLine(String path) {
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            br = new BufferedReader(new FileReader(f));
            return br.readLine();
        } catch (Exception e) {
            return null;
        } finally {
            if (br != null) try { br.close(); } catch (Exception ignored) {}
        }
    }

    private String readable(long bytes) {
        if (bytes <= 0) return "0B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb);
        float gb = mb / 1024f;
        if (gb < 1024) return String.format(Locale.US, "%.2f GB", gb);
        float tb = gb / 1024f;
        return String.format(Locale.US, "%.2f TB", tb);
    }

    private String formatDuration(long ms) {
        long seconds = ms / 1000;
        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0 || days > 0) sb.append(hours).append("h ");
        sb.append(minutes).append("m");
        return sb.toString().trim();
    }
}
