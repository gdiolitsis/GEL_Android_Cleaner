// ============================================================
// PerformanceDiagnosticsActivity
// GEL Phone Diagnosis — Service Lab (Hospital Edition)
// Full-screen scroll log with color-coded levels
// All logs / comments in EN for international use.
// NOTE: Entire file is ready for copy-paste (GEL rule).
// ============================================================
package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.Looper;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class PerformanceDiagnosticsActivity extends AppCompatActivity {

    private TextView txtDiag;
    private ScrollView scroll;
    private Handler ui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simple full-screen scrollable text log
        scroll = new ScrollView(this);
        txtDiag = new TextView(this);

        txtDiag.setTextSize(14f);
        txtDiag.setTextColor(0xFFE0E0E0);
        txtDiag.setPadding(32, 32, 32, 32);
        txtDiag.setMovementMethod(new ScrollingMovementMethod());

        scroll.addView(txtDiag);
        setContentView(scroll);

        ui = new Handler(Looper.getMainLooper());

        // Clear previous service log (for fresh Service Report)
        GELServiceLog.clear();

        logTitle("🔬 GEL Phone Diagnosis — Service Lab (Hospital Edition)");
        logInfo("Device: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        logInfo("Build: " + Build.DISPLAY);
        logLine();

        runFullDiagnosis();
    }

    /* ============================================================
     * HTML APPEND + MIRROR TO GELServiceLog
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
        appendHtml("<font color='#FFD700'>⚠️ " + escape(msg) + "</font>");
        GELServiceLog.warn(msg);
    }

    // ❌ ERROR → Red
    private void logError(String msg) {
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
        GELServiceLog.error(msg);
    }

    // ACCESS DENIED — Firmware / Security Restriction
    private void logAccessDenied(String area) {
        String base = "Access denied — firmware / security restriction";
        if (area != null && !area.isEmpty()) {
            base = base + " (" + area + ")";
        }
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
     * MAIN DIAG FLOW (ROOT-AWARE, HOSPITAL EDITION)
     * ============================================================ */
    private void runFullDiagnosis() {
        new Thread(() -> {

            // LAB 0 — Root / System Integrity (High-level)
            logSection("LAB 0 — Root / System Integrity");

            boolean rooted = isDeviceRootedBasic();
            if (rooted) {
                logError("Device appears to be ROOTED — reduced security from Play Store perspective.");
                logInfo("For service technicians this is OK, but it should be clearly explained to the customer.");
            } else {
                logOk("No clear root indicators detected (typical Play Store device).");
            }

            // Run deep root labs only if root indicators exist
            if (rooted) {
                labRootOverview();        // LAB 0.1
                labRootSecurityFlags();   // LAB 0.2
                labRootDangerousProps();  // LAB 0.3
                labRootMounts();          // LAB 0.4
            } else {
                logInfo("Skipping deep Root LABs because device does not look rooted.");
            }

            logLine();

            // LAB 1–10 (Core system metrics)
            labHardware();      // LAB 1 — Hardware / OS
            labCpuRam();        // LAB 2 — CPU / RAM static
            labStorage();       // LAB 3 — Internal storage
            labBattery();       // LAB 4 — Battery health & temperature
            labNetwork();       // LAB 5 — Network connectivity + basic latency
            labWifiSignal();    // LAB 6 — Wi-Fi status
            labSensors();       // LAB 7 — Sensor inventory
            labDisplay();       // LAB 8 — Display resolution profile
            labThermal();       // LAB 9 — CPU thermal sensors (if available)
            labSystemHealth();  // LAB 10 — Telephony + live RAM

            // LAB 11 — Uptime / Boot profile
            labUptime();

            // LAB 12 — Installed apps footprint (approximate load)
            labAppsFootprint();

            logLine();
            logOk("Auto Diagnosis finished. ❌ marks critical or abnormal findings. " +
                    "⚠️ marks warnings/risks. ✅ marks values within normal range.");

        }).start();
    }

    /* ============================================================
     * ROOT CHECK BASIC
     * ============================================================ */
    private boolean isDeviceRootedBasic() {
        return hasTestKeys() || hasSuBinary() || hasWhichSu();
    }

    private boolean hasTestKeys() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private boolean hasSuBinary() {
        String[] paths = new String[]{
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/app/Superuser.apk", "/system/app/Magisk.apk",
                "/system/bin/.ext/su", "/system/usr/we-need-root/su"
        };
        try {
            for (String p : paths) {
                if (new File(p).exists()) return true;
            }
        } catch (Exception ignored) { }
        return false;
    }

    private boolean hasWhichSu() {
        BufferedReader in = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"which", "su"});
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            return line != null && !line.trim().isEmpty();
        } catch (Exception ignored) {
            return false;
        } finally {
            try { if (in != null) in.close(); } catch (Exception ignored) {}
        }
    }

    /* ============================================================
     * ROOT LABS (SAFE)
     * ============================================================ */
    private void labRootOverview() {
        logSection("LAB 0.1 — Root Overview");

        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys"))
            logWarn("Build tags: test-keys (typical for rooted / custom ROM).");
        else
            logInfo("Build tags: " + tags);

        String[] paths = new String[]{
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/bin/.ext/su", "/system/usr/we-need-root/su"
        };

        boolean anySu = false;
        for (String p : paths) {
            try {
                if (new File(p).exists()) {
                    logWarn("su binary present: " + p);
                    anySu = true;
                }
            } catch (Exception ignored) {}
        }

        if (!anySu) logInfo("No su binary found in common locations.");

        // Check actual su privileges
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String out = r.readLine();
            try { p.destroy(); } catch (Exception ignored) {}

            if (out != null && out.contains("uid=0"))
                logError("su test → uid=0 (FULL ROOT access confirmed).");
            else if (out != null)
                logWarn("su test → " + out);
            else
                logWarn("su test → no response (root manager may be blocking).");

        } catch (Exception e) {
            logWarn("su test exception: " + e.getMessage());
        }
    }

    /* ============================================================
     * LAB 0.2 — Security / SELinux / Debug
     * ============================================================ */
    private void labRootSecurityFlags() {
        logSection("LAB 0.2 — Security / SELinux / Debug");

        try {
            boolean enabled = false;
            boolean enforced = false;

            try {
                Class<?> clazz = Class.forName("android.os.SELinux");
                enabled = (boolean) clazz.getMethod("isSELinuxEnabled").invoke(null);
                enforced = (boolean) clazz.getMethod("isSELinuxEnforced").invoke(null);
            } catch (Throwable ignored) {
                logAccessDenied("SELinux status API");
            }

            logInfo("SELinux enabled: " + enabled + " | enforced: " + enforced);

            if (!enabled) logError("SELinux is DISABLED — low security profile.");
            else if (!enforced) logWarn("SELinux is PERMISSIVE — weaker security policy.");
            else logOk("SELinux is ENFORCING — normal for modern Android.");

        } catch (Throwable t) {
            logWarn("SELinux read failed: " + t.getMessage());
        }

        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            int dev = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            logInfo("ADB enabled: " + (adb == 1));
            logInfo("Developer options: " + (dev == 1));

            if (adb == 1)
                logWarn("ADB is enabled — recommend disabling for non-developer customers.");
        } catch (Throwable t) {
            logAccessDenied("ADB / Developer Settings");
        }
    }

    /* ============================================================
     * LAB 0.3 — Dangerous Properties
     * ============================================================ */
    private void labRootDangerousProps() {
        logSection("LAB 0.3 — Dangerous Properties");

        checkProp("ro.debuggable", "1", "ro.debuggable=1 — debug build (not production).");
        checkProp("ro.secure", "0", "ro.secure=0 — low security build.");
        checkProp("ro.boot.verifiedbootstate", "orange", "VerifiedBoot = ORANGE (boot chain modified).");
        checkProp("ro.boot.verifiedbootstate", "red", "VerifiedBoot = RED (boot chain compromised).");
    }

    private void checkProp(String key, String bad, String msg) {
        String val = readProp(key);
        if (val == null) {
            logInfo(key + " = N/A (OEM restricted or not set).");
            return;
        }
        logInfo(key + " = " + val);
        if (val.trim().equalsIgnoreCase(bad)) logError(msg);
    }

    private String readProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = r.readLine();
            try { p.destroy(); } catch (Exception ignored) {}
            return line;
        } catch (Exception e) { return null; }
    }

    /* ============================================================
     * LAB 0.4 — MOUNTS
     * ============================================================ */
    private void labRootMounts() {
        logSection("LAB 0.4 — System Mounts");

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

            if (systemRW) logError("/system is mounted READ-WRITE — not safe for normal customers.");
            else logOk("/system is NOT mounted read-write (more secure).");

        } catch (Exception e) {
            logAccessDenied("mount table");
        } finally {
            try { if (r != null) r.close(); } catch (Exception ignored) {}
        }
    }

    /* ============================================================
     * LAB 1 — Hardware / OS
     * ============================================================ */
    private void labHardware() {
        logSection("LAB 1 — Hardware / OS Profile");

        logInfo("Manufacturer: " + Build.MANUFACTURER);
        logInfo("Model: " + Build.MODEL);
        logInfo("Device: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);
        logInfo("Fingerprint: " + Build.FINGERPRINT);

        int api = Build.VERSION.SDK_INT;
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + api + ")");
        logInfo("Security patch: " + Build.VERSION.SECURITY_PATCH);

        if (api < 26) logError("Android < 8 — below modern security baseline.");
        else if (api < 30) logWarn("Android < 11 — still supported but older generation.");
        else logOk("Android version is modern for everyday use.");

        logLine();
    }

    /* ============================================================
     * LAB 2 — CPU / RAM (Static)
     * ============================================================ */
    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM Capacity");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU cores reported: " + cores);

        long totalMem = getTotalRam();
        logInfo("Total RAM (system): " + readable(totalMem));

        if (cores <= 4) {
            logWarn("Low core count (≤ 4) for heavy multitasking / modern apps.");
        } else {
            logOk("CPU core count is adequate for typical workloads.");
        }

        if (totalMem < 2L * 1024 * 1024 * 1024L) {
            logWarn("RAM < 2 GB — device may struggle with modern apps.");
        } else if (totalMem < 4L * 1024 * 1024 * 1024L) {
            logInfo("RAM between 2–4 GB — acceptable but not ideal for heavy users.");
        } else {
            logOk("RAM ≥ 4 GB — good for daily use.");
        }

        // Optional: read max CPU freq if available
        String maxFreq = safeReadFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (maxFreq != null) {
            try {
                long khz = Long.parseLong(maxFreq.trim());
                double ghz = khz / 1_000_000.0;
                logInfo(String.format(Locale.US, "CPU0 max frequency: %.2f GHz", ghz));
            } catch (Exception e) {
                logInfo("CPU0 max frequency (raw): " + maxFreq.trim());
            }
        } else {
            logInfo("CPU frequency info not exposed (OEM restriction).");
        }

        logLine();
    }

    private long getTotalRam() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.totalMem;
        } catch (Exception e) { return 0; }
    }

    /* ============================================================
     * LAB 3 — Storage
     * ============================================================ */
    private void labStorage() {
        logSection("LAB 3 — Internal Storage");

        try {
            File data = Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
            long used = total - free;
            int pctFree = (int) ((free * 100L) / total);

            logInfo("Total internal storage: " + readable(total));
            logInfo("Used: " + readable(used) + " | Free: " + readable(free) + " (" + pctFree + "% free)");

            if (pctFree < 5) {
                logError("Free space < 5% — critical. Immediate cleanup / backup recommended.");
            } else if (pctFree < 10) {
                logWarn("Free space < 10% — possible slowdowns, update problems, app crashes.");
            } else {
                logOk("Free space is acceptable for normal use.");
            }

        } catch (Exception e) {
            logError("Storage error while reading internal stats: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 4 — Battery
     * ============================================================ */
    private void labBattery() {
        logSection("LAB 4 — Battery Health & Temperature");

        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) {
                logAccessDenied("Battery stats");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (scale > 0 ? (100f * lvl / scale) : -1f);

            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp > 0 ? (rawTemp / 10f) : -1f;

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
            int status = i.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);

            logInfo(String.format(Locale.US, "Battery level: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Battery temperature: %.1f°C", temp));

            // Health
            String healthStr;
            boolean bad = false;
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthStr = "DEAD";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthStr = "OVERHEAT / OVERVOLTAGE";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthStr = "UNSPECIFIED FAILURE";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthStr = "GOOD";
                    break;
                default:
                    healthStr = "UNKNOWN";
                    break;
            }

            logInfo("Battery health (reported by Android): " + healthStr);
            if (bad)
                logError("Battery health is outside normal range — replacement strongly recommended.");
            else if (health == BatteryManager.BATTERY_HEALTH_GOOD)
                logOk("Battery health looks good according to Android.");
            else
                logWarn("Battery health is not clearly GOOD, monitoring recommended.");

            // Temperature thresholds
            if (temp > 45f) {
                logError(String.format(Locale.US,
                        "Battery temperature is very high (%.1f°C). Risk of damage, overheating or shutdown.",
                        temp));
            } else if (temp > 40f) {
                logWarn(String.format(Locale.US,
                        "Battery temperature is elevated (%.1f°C). May be caused by heavy use / charging / hot environment.",
                        temp));
            } else if (temp > 0) {
                logOk(String.format(Locale.US,
                        "Battery temperature in normal range (%.1f°C).", temp));
            }

            // Charging status
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
                    break;
            }
            logInfo("Battery status: " + statusStr);

        } catch (Exception e) {
            logError("Battery error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 5 — Network
     * ============================================================ */
    private void labNetwork() {
        logSection("LAB 5 — Network Connectivity");

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) {
                logAccessDenied("ConnectivityManager");
                logLine();
                return;
            }

            boolean online = false;
            if (Build.VERSION.SDK_INT >= 23) {
                android.net.Network n = cm.getActiveNetwork();
                NetworkCapabilities caps = cm.getNetworkCapabilities(n);
                if (caps != null) online = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                online = ni != null && ni.isConnected();
            }

            if (!online) {
                logError("No active internet capability detected.");
            } else {
                logOk("Internet capability detected (active network).");
            }

            // Basic latency test (if possible) in background thread context
            if (online) {
                try {
                    long start = SystemClock.elapsedRealtime();
                    Process p = Runtime.getRuntime().exec("ping -c 1 8.8.8.8");
                    int rc = p.waitFor();
                    long end = SystemClock.elapsedRealtime();
                    long ms = end - start;

                    if (rc == 0) {
                        logInfo("Ping 8.8.8.8 success, approx latency: " + ms + " ms.");
                    } else {
                        logWarn("Ping 8.8.8.8 failed — network may be filtered or unstable.");
                    }
                } catch (Exception e) {
                    logAccessDenied("ICMP ping (OS/network restriction)");
                }
            }

        } catch (Exception e) {
            logAccessDenied("Network state");
        }

        logLine();
    }

    /* ============================================================
     * LAB 6 — WiFi
     * ============================================================ */
    private void labWifiSignal() {
        logSection("LAB 6 — Wi-Fi Status");

        try {
            android.net.wifi.WifiManager wm =
                    (android.net.wifi.WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            if (wm == null) {
                logAccessDenied("WiFi manager");
                logLine();
                return;
            }

            if (!wm.isWifiEnabled()) {
                logWarn("Wi-Fi is OFF.");
                logLine();
                return;
            }

            int rssi = wm.getConnectionInfo().getRssi();
            logInfo("Wi-Fi RSSI: " + rssi + " dBm");

            if (rssi > -60)
                logOk("Wi-Fi signal is strong for normal use.");
            else if (rssi > -75)
                logWarn("Wi-Fi signal is medium — possible drops in crowded networks.");
            else
                logError("Wi-Fi signal is weak — user may experience frequent disconnects.");

        } catch (Exception e) {
            logError("Wi-Fi error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 7 — Sensors
     * ============================================================ */
    private void labSensors() {
        logSection("LAB 7 — Sensors Inventory");

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
            checkSensor(sm, Sensor.TYPE_LIGHT, "Light Sensor");
            checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");
            checkSensor(sm, Sensor.TYPE_PRESSURE, "Barometer");
            checkSensor(sm, Sensor.TYPE_GRAVITY, "Gravity");
            checkSensor(sm, Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration");

        } catch (Exception e) {
            logError("Sensor error: " + e.getMessage());
        }

        logLine();
    }

    private void checkSensor(SensorManager sm, int type, String name) {
        boolean ok = sm.getDefaultSensor(type) != null;
        if (!ok) logWarn(name + " sensor is missing or not exposed.");
        else logOk(name + " sensor is present.");
    }

    /* ============================================================
     * LAB 8 — Display
     * ============================================================ */
    private void labDisplay() {
        logSection("LAB 8 — Display Profile");

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

            logInfo("Resolution: " + w + " × " + h);
            logInfo(String.format(Locale.US, "Logical density: %.2f", density));

            if (w >= 1080 && h >= 1920)
                logOk("Display resolution is modern and suitable for most apps.");
            else if (w >= 720 && h >= 1280)
                logWarn("Display resolution is mid-range. UI may be tight for some modern apps.");
            else
                logError("Low display resolution for modern applications. UI may feel cramped or blurry.");

        } catch (Exception e) {
            logError("Display error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 9 — Thermal
     * ============================================================ */
    private void labThermal() {
        logSection("LAB 9 — Thermal Sensors (CPU)");

        if (Build.VERSION.SDK_INT >= 29) {
            try {
                HardwarePropertiesManager hpm =
                        (HardwarePropertiesManager) getSystemService(HARDWARE_PROPERTIES_SERVICE);

                if (hpm != null) {
                    float[] temps = hpm.getDeviceTemperatures(
                            HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
                            HardwarePropertiesManager.TEMPERATURE_CURRENT);

                    if (temps != null && temps.length > 0) {
                        float cpuTemp = temps[0];
                        logInfo(String.format(Locale.US, "CPU temperature (reported): %.1f°C", cpuTemp));

                        if (cpuTemp > 85f)
                            logError("CPU temperature is extremely high — heavy throttling or shutdown risk.");
                        else if (cpuTemp > 75f)
                            logWarn("CPU temperature is high — performance throttling likely.");
                        else
                            logOk("CPU temperature is within normal operating range.");
                    } else {
                        logAccessDenied("Thermal sensors returned no data.");
                    }
                } else {
                    logAccessDenied("HardwarePropertiesManager not available.");
                }

            } catch (Exception e) {
                logAccessDenied("Thermal sensors: " + e.getMessage());
            }
        } else {
            logAccessDenied("Thermal API requires Android 10+ (API 29+).");
        }

        logLine();
    }

    /* ============================================================
     * LAB 10 — System / Telephony / Live RAM
     * ============================================================ */
    private void labSystemHealth() {
        logSection("LAB 10 — System / Telephony / Live RAM");

        // Telephony / Operator info
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (tm != null) {
                String netOp = tm.getNetworkOperatorName();
                String simOp = tm.getSimOperatorName();

                logInfo("Network operator: " + (netOp == null ? "N/A" : netOp));
                logInfo("SIM operator: " + (simOp == null ? "N/A" : simOp));
            } else {
                logAccessDenied("TelephonyManager");
            }

        } catch (Exception e) {
            logAccessDenied("Telephony service: " + e.getMessage());
        }

        // Live RAM snapshot
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
                        "Live RAM now: %s free (%.0f%% of total)",
                        readable(free),
                        pct
                );
                logInfo(ramLine);

                if (pct >= 25f)
                    logOk("Live RAM status is healthy at the moment.");
                else if (pct >= 15f)
                    logWarn("Live RAM is getting low — closing background apps is recommended.");
                else
                    logError("Live RAM is critically low — system may kill apps aggressively.");
            }
        } catch (Exception e) {
            logError("Live RAM error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 11 — Uptime / Boot Profile
     * ============================================================ */
    private void labUptime() {
        logSection("LAB 11 — Uptime / Boot Profile");

        long uptimeMs = SystemClock.elapsedRealtime();
        long sec = uptimeMs / 1000;
        long min = sec / 60;
        long hrs = min / 60;
        long days = hrs / 24;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hrs % 24 > 0) sb.append(hrs % 24).append("h ");
        if (min % 60 > 0) sb.append(min % 60).append("m ");

        logInfo("System uptime since last boot: " + sb.toString().trim());

        if (days >= 7) {
            logWarn("Device has not been rebooted for a long time (≥ 7 days). A restart may improve stability.");
        } else if (days == 0 && hrs < 2) {
            logInfo("Recent reboot detected — good for troubleshooting.");
        } else {
            logOk("Uptime is reasonable for daily use.");
        }

        logLine();
    }

    /* ============================================================
     * LAB 12 — Installed Apps Footprint (basic)
     * ============================================================ */
    private void labAppsFootprint() {
        logSection("LAB 12 — Installed Apps Footprint");

        try {
            int count = getPackageManager().getInstalledApplications(0).size();
            logInfo("Installed applications (approx): " + count);

            if (count > 200) {
                logWarn("Very high number of installed apps — may affect RAM, battery and performance.");
            } else if (count > 120) {
                logInfo("Above average number of installed apps — monitor performance and storage.");
            } else {
                logOk("Installed app count is within a normal range.");
            }
        } catch (Exception e) {
            logAccessDenied("Installed apps list: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * SMALL HELPERS
     * ============================================================ */
    private String safeReadFirstLine(String path) {
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
}
