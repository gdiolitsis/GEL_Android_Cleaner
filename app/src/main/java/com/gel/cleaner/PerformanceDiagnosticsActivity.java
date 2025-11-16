// ============================================================
// PerformanceDiagnosticsActivity
// GEL Phone Diagnosis — Service Lab
// Full-screen scroll log with color-coded levels
// NOTE: Όλο το αρχείο είναι έτοιμο για copy-paste (GEL rule).
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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
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

        scroll = new ScrollView(this);
        txtDiag = new TextView(this);

        txtDiag.setTextSize(14f);
        txtDiag.setTextColor(0xFFE0E0E0);
        txtDiag.setPadding(32, 32, 32, 32);
        txtDiag.setMovementMethod(new ScrollingMovementMethod());

        scroll.addView(txtDiag);
        setContentView(scroll);

        ui = new Handler(Looper.getMainLooper());

        GELServiceLog.clear();

        logTitle("🔬 GEL Phone Diagnosis — Service Lab");
        logInfo("Μοντέλο: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        logLine();

        runFullDiagnosis();
    }

    /* ============================================================
     * HTML FIX + MIRROR LOG
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

    // ✅ OK → Πράσινο
    private void logOk(String msg) {
        appendHtml("<font color='#66FF66'>✅ " + escape(msg) + "</font>");
        GELServiceLog.ok(msg);
    }

    // ⚠️ WARNING → Χρυσό
    private void logWarn(String msg) {
        appendHtml("<font color='#FFD700'>⚠️ " + escape(msg) + "</font>");
        GELServiceLog.warn(msg);
    }

    // ❌ ERROR → Κόκκινο
    private void logError(String msg) {
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
        GELServiceLog.error(msg);
    }

    // ⚠️ ACCESS DENIED — Firmware Restriction (χρησιμοποιεί το κίτρινο)
    private void logAccessDenied(String area) {
        String base = "Access Denied — Firmware Restriction";
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
     * MAIN DIAG FLOW
     * ============================================================ */
    private void runFullDiagnosis() {
        new Thread(() -> {

            logSection("LAB 0 — Root / System Integrity");

            boolean rooted = isDeviceRootedBasic();
            if (rooted) logError("Η συσκευή φαίνεται ROOTED — μειωμένη ασφάλεια.");
            else logOk("Δεν εντοπίζω εμφανή root.");

            if (rooted) {
                labRootOverview();
                labRootSecurityFlags();
                labRootDangerousProps();
                labRootMounts();
            }

            logLine();

            labHardware();
            labCpuRam();
            labStorage();
            labBattery();
            labNetwork();
            labWifiSignal();
            labSensors();
            labDisplay();
            labThermal();
            labSystemHealth();

            logLine();
            logOk("Διάγνωση ολοκληρώθηκε.");

        }).start();
    }

    /* ============================================================
     * ROOT CHECK BASIC
     * ============================================================ */
    private boolean isDeviceRootedBasic() {
        return hasTestKeys() || hasSuBinary();
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
        } catch (Exception ignored) {}
        return false;
    }

    /* ============================================================
     * ROOT LABS (SAFE)
     * ============================================================ */
    private void labRootOverview() {
        logInfo("Root overview ενεργό.");

        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys"))
            logWarn("Build tags: test-keys");
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
                    logWarn("Βρέθηκε su: " + p);
                    anySu = true;
                }
            } catch (Exception ignored) {}
        }

        if (!anySu) logInfo("Δεν βρέθηκε su.");

        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String out = r.readLine();
            try { p.destroy(); } catch (Exception ignored) {}

            if (out != null && out.contains("uid=0"))
                logError("su test → uid=0 FULL ROOT");
            else if (out != null)
                logWarn("su test → " + out);
            else
                logWarn("su test → No response");

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
                // Εδώ είναι κλασική περίπτωση firmware limitation
                logAccessDenied("SELinux status API");
            }

            logInfo("SELinux enabled: " + enabled + " | enforced: " + enforced);

            if (!enabled) logError("SELinux disabled.");
            else if (!enforced) logWarn("SELinux permissive.");
            else logOk("SELinux enforcing.");

        } catch (Throwable t) {
            logWarn("SELinux read failed: " + t.getMessage());
        }

        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            int dev = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            logInfo("ADB Enabled: " + (adb == 1));
            logInfo("Developer Options: " + (dev == 1));

            if (adb == 1) logWarn("ADB ενεργό.");
        } catch (Throwable t) {
            // Πολλά firmware μπλοκάρουν Global settings
            logAccessDenied("ADB / Developer Settings");
        }
    }

    /* ============================================================
     * LAB 0.3 — Dangerous Properties
     * ============================================================ */
    private void labRootDangerousProps() {
        logSection("LAB 0.3 — Dangerous Properties");

        checkProp("ro.debuggable", "1", "ro.debuggable=1 — Debug build.");
        checkProp("ro.secure", "0", "ro.secure=0 — Low security.");
        checkProp("ro.boot.verifiedbootstate", "orange", "VerifiedBoot ORANGE.");
        checkProp("ro.boot.verifiedbootstate", "red", "VerifiedBoot RED.");
    }

    private void checkProp(String key, String bad, String msg) {
        String val = readProp(key);
        if (val == null) {
            // Αν δεν επιστρέφει τίποτα, παίζει να είναι OEM lock, αλλά το αφήνουμε ως info
            logInfo(key + " = N/A");
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
        logSection("LAB 0.4 — Mounts");

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

            if (systemRW) logError("/system RW");
            else logOk("/system not RW");

        } catch (Exception e) {
            // Συνήθως permission issue
            logAccessDenied("mount table");
        } finally {
            try { if (r != null) r.close(); } catch (Exception ignored) {}
        }
    }

    /* ============================================================
     * LAB 1 — Hardware / OS
     * ============================================================ */
    private void labHardware() {
        logSection("LAB 1 — Hardware / OS");

        logInfo("Κατασκευαστής: " + Build.MANUFACTURER);
        logInfo("Μοντέλο: " + Build.MODEL);
        logInfo("Device: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);

        int api = Build.VERSION.SDK_INT;
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + api + ")");

        if (api < 26) logError("Android < 8");
        else if (api < 30) logWarn("Android < 11");
        else logOk("OS OK");

        logLine();
    }

    /* ============================================================
     * LAB 2 — CPU / RAM
     * ============================================================ */
    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU Cores: " + cores);

        long totalMem = getTotalRam();
        logInfo("RAM: " + readable(totalMem));

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
        logSection("LAB 3 — Storage");

        try {
            File data = Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
            int pct = (int)((free * 100L) / total);

            logInfo("Storage: " + readable(free) + " free (" + pct + "%)");

        } catch (Exception e) {
            logError("Storage error");
        }

        logLine();
    }

    /* ============================================================
     * LAB 4 — Battery
     * ============================================================ */
    private void labBattery() {
        logSection("LAB 4 — Battery");

        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) {
                // Δεν μας άφησε να διαβάσουμε battery stats
                logAccessDenied("Battery stats");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (100f * lvl / scale);

            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp / 10f;

            logInfo(String.format(Locale.US, "Battery: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Temp: %.1f°C", temp));

        } catch (Exception e) {
            logError("Battery error");
        }

        logLine();
    }

    /* ============================================================
     * LAB 5 — Network
     * ============================================================ */
    private void labNetwork() {
        logSection("LAB 5 — Network");

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

            if (!online) logError("No Internet");
            else logOk("Internet OK");

        } catch (Exception e) {
            logAccessDenied("Network state");
        }

        logLine();
    }

    /* ============================================================
     * LAB 6 — WiFi
     * ============================================================ */
    private void labWifiSignal() {
        logSection("LAB 6 — WiFi");

        try {
            android.net.wifi.WifiManager wm =
                    (android.net.wifi.WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            if (wm == null) {
                logAccessDenied("WiFi manager");
                logLine();
                return;
            }

            if (!wm.isWifiEnabled()) {
                logWarn("WiFi off");
                logLine();
                return;
            }

            int rssi = wm.getConnectionInfo().getRssi();
            logInfo("RSSI: " + rssi);

        } catch (Exception e) {
            logError("WiFi error");
        }

        logLine();
    }

    /* ============================================================
     * LAB 7 — Sensors
     * ============================================================ */
    private void labSensors() {
        logSection("LAB 7 — Sensors");

        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sm == null) {
                logAccessDenied("SensorManager");
                logLine();
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Sensors: " + (all == null ? 0 : all.size()));

            checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");

        } catch (Exception e) {
            logError("Sensor error");
        }

        logLine();
    }

    private void checkSensor(SensorManager sm, int type, String name) {
        boolean ok = sm.getDefaultSensor(type) != null;
        if (!ok) logWarn(name + " missing");
        else logOk(name + " OK");
    }

    /* ============================================================
     * LAB 8 — Display
     * ============================================================ */
    private void labDisplay() {
        logSection("LAB 8 — Display");

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

            logInfo("Resolution: " + w + "x" + h);

        } catch (Exception e) {
            logError("Display error");
        }

        logLine();
    }

    /* ============================================================
     * LAB 9 — Thermal
     * ============================================================ */
    private void labThermal() {
        logSection("LAB 9 — Thermal");

        if (Build.VERSION.SDK_INT >= 29) {
            try {
                HardwarePropertiesManager hpm =
                        (HardwarePropertiesManager) getSystemService(HARDWARE_PROPERTIES_SERVICE);

                if (hpm != null) {
                    float[] temps = hpm.getDeviceTemperatures(
                            HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
                            HardwarePropertiesManager.TEMPERATURE_CURRENT);

                    if (temps != null && temps.length > 0) {
                        logInfo("CPU Temp: " + temps[0]);
                    } else {
                        logAccessDenied("Thermal sensors (no data)");
                    }
                } else {
                    logAccessDenied("HardwarePropertiesManager");
                }

            } catch (Exception e) {
                logAccessDenied("Thermal sensors");
            }
        } else {
            // Κλασική περίπτωση firmware/OS limitation
            logAccessDenied("Thermal API < 29");
        }

        logLine();
    }

    /* ============================================================
     * LAB 10 — System Health
     * ============================================================ */
    private void labSystemHealth() {
        logSection("LAB 10 — System Health");

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (tm != null) {
                logInfo("Network operator: " + tm.getNetworkOperatorName());
            } else {
                logAccessDenied("TelephonyManager");
            }

        } catch (Exception e) {
            logAccessDenied("Telephony service");
        }

        logLine();
    }

    private String readable(long bytes) {
        if (bytes <= 0) return "0B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb);
        float gb = mb / 1024f;
        return String.format(Locale.US, "%.2f GB", gb);
    }
}
