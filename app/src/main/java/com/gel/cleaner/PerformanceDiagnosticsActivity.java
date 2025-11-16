// ============================================================
// PerformanceDiagnosticsActivity
// GEL Phone Diagnosis — Service Lab
// Full-screen scroll log with color-coded levels
// NOTE: Όλο το αρχείο είναι έτοιμο για copy-paste (GEL rule).
// ============================================================
package com.gel.cleaner;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
     * MAIN DIAG FLOW (20+ LABS)
     * ============================================================ */
    private void runFullDiagnosis() {
        new Thread(() -> {

            // ---------- ROOT & SECURITY ----------
            logSection("LAB 0 — Root / System Integrity");

            boolean rooted = isDeviceRootedBasic();

            if (rooted) {
                logError("Η συσκευή φαίνεται ROOTED — μειωμένη ασφάλεια (όχι τυπική για Play Store).");
                labRootOverview();
                labRootSecurityFlags();
                labRootDangerousProps();
                labRootMounts();
            } else {
                logOk("Root status: NOT ROOTED / LOCKED (δεν βρέθηκαν σαφείς ενδείξεις).");
                logInfo("Παράλειψη προχωρημένων Root LABS (η συσκευή δεν φαίνεται rooted).");
            }

            logLine();

            // ---------- ΚΛΑΣΙΚΑ LABS ----------
            labHardware();       // LAB 1
            labCpuRam();         // LAB 2
            labStorage();        // LAB 3
            labBattery();        // LAB 4
            labNetwork();        // LAB 5
            labWifiSignal();     // LAB 6
            labSensors();        // LAB 7
            labDisplay();        // LAB 8
            labThermal();        // LAB 9
            labSystemHealth();   // LAB 10

            // ---------- ΠΡΟΧΩΡΗΜΕΝΑ LABS ----------
            labIOPerformance();   // LAB 11 — I/O benchmark
            labAppsAndProcesses(); // LAB 12 — apps/processes load
            labSecurityPatch();   // LAB 13 — security patch age
            labPowerDetails();    // LAB 14 — power / capacity details
            labRadioType();       // LAB 15 — mobile network type
            labBluetooth();       // LAB 16 — BT hardware & state
            labLocationGPS();     // LAB 17 — GPS / location services
            labExternalStorage(); // LAB 18 — external / SAF status
            labSystemToggles();   // LAB 19 — accessibility / dev toggles
            labUptimeSummary();   // LAB 20 — uptime & final summary

            logLine();
            logOk("Auto Diagnosis ολοκληρώθηκε. Τα ❌ δείχνουν σοβαρές ενδείξεις / βλάβες. " +
                    "Το report είναι έτοιμο για Export (TXT / PDF).");

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
            logWarn("Build tags: test-keys (firmware σε debug mode).");
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
                    logWarn("Βρέθηκε su binary: " + p);
                    anySu = true;
                }
            } catch (Exception ignored) {}
        }

        if (!anySu) logInfo("Δεν βρέθηκε su binary στους κλασικούς paths.");

        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String out = r.readLine();
            try { p.destroy(); } catch (Exception ignored) {}

            if (out != null && out.contains("uid=0"))
                logError("su test → uid=0 FULL ROOT (system-level πρόσβαση).");
            else if (out != null)
                logWarn("su test → " + out);
            else
                logWarn("su test → No response (μπλοκάρεται από firmware).");

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

            if (!enabled) logError("SELinux disabled (χαμηλή απομόνωση).");
            else if (!enforced) logWarn("SELinux permissive (χαλαρή πολιτική).");
            else logOk("SELinux enforcing (κανονική προστασία).");

        } catch (Throwable t) {
            logWarn("SELinux read failed: " + t.getMessage());
        }

        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            int dev = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            logInfo("ADB Enabled: " + (adb == 1));
            logInfo("Developer Options: " + (dev == 1));

            if (adb == 1) logWarn("ADB ενεργό — για service είναι χρήσιμο, αλλά αυξάνει ρίσκο ασφαλείας.");
        } catch (Throwable t) {
            logAccessDenied("ADB / Developer Settings");
        }
    }

    /* ============================================================
     * LAB 0.3 — Dangerous Properties
     * ============================================================ */
    private void labRootDangerousProps() {
        logSection("LAB 0.3 — Dangerous Properties");

        checkProp("ro.debuggable", "1", "ro.debuggable=1 — Debug build ενεργό.");
        checkProp("ro.secure", "0", "ro.secure=0 — Low security mode.");
        checkProp("ro.boot.verifiedbootstate", "orange", "VerifiedBoot ORANGE (προειδοποίηση).");
        checkProp("ro.boot.verifiedbootstate", "red", "VerifiedBoot RED (μη αξιόπιστο image).");
    }

    private void checkProp(String key, String bad, String msg) {
        String val = readProp(key);
        if (val == null) {
            logInfo(key + " = N/A (μπλοκάρεται από OEM ή δεν υπάρχει).");
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

            if (systemRW) logError("/system mounted RW — δυνατότητα τροποποίησης συστήματος.");
            else logOk("/system not RW — τυπικό locked firmware.");

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
        logSection("LAB 1 — Hardware / OS");

        logInfo("Κατασκευαστής: " + Build.MANUFACTURER);
        logInfo("Μοντέλο: " + Build.MODEL);
        logInfo("Device: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);

        if (Build.VERSION.SDK_INT >= 31) {
            String soc = Build.SOC_MODEL != null ? Build.SOC_MODEL : "N/A";
            logInfo("SoC Model: " + soc);
        }

        int api = Build.VERSION.SDK_INT;
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + api + ")");

        if (api < 26) logError("Android < 8 — εκτός σύγχρονης υποστήριξης.");
        else if (api < 30) logWarn("Android < 11 — περιορισμένη μελλοντική υποστήριξη.");
        else logOk("OS version κατάλληλη για σύγχρονες εφαρμογές.");

        logLine();
    }

    /* ============================================================
     * LAB 2 — CPU / RAM
     * ============================================================ */
    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU cores: " + cores);

        long totalMem = getTotalRam();
        logInfo("Total RAM: " + readable(totalMem));

        if (totalMem > 0) {
            if (totalMem < 2L * 1024 * 1024 * 1024L) {
                logWarn("RAM < 2GB — χαμηλή για multitasking / σύγχρονα games.");
            } else if (totalMem < 4L * 1024 * 1024 * 1024L) {
                logOk("RAM σε μεσαίο επίπεδο (2–4GB).");
            } else {
                logOk("RAM σε υψηλό επίπεδο (≥4GB).");
            }
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
        logSection("LAB 3 — Storage");

        try {
            File data = Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
            long used = total - free;
            int pct = (int) ((free * 100L) / total);

            logInfo("Total internal: " + readable(total));
            logInfo("Used internal: " + readable(used));
            logInfo("Free internal: " + readable(free) + " (" + pct + "%)");

            if (pct < 5) {
                logError("Ελεύθερος χώρος <5% — κρίσιμα χαμηλός, πιθανές αποτυχίες update / boot.");
            } else if (pct < 10) {
                logWarn("Ελεύθερος χώρος <10% — χαμηλός, συνιστάται καθαρισμός.");
            } else {
                logOk("Ελεύθερος χώρος σε αποδεκτά επίπεδα.");
            }

        } catch (Exception e) {
            logError("Storage error: " + e.getMessage());
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
                logAccessDenied("Battery stats");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (100f * lvl / scale);

            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp / 10f;

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);

            logInfo(String.format(Locale.US, "Battery level: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Temperature: %.1f°C", temp));

            String healthStr;
            boolean bad = false;
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthStr = "GOOD";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthStr = "OVERHEAT";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthStr = "DEAD";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthStr = "OVER_VOLTAGE";
                    bad = true;
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthStr = "FAILURE";
                    bad = true;
                    break;
                default:
                    healthStr = "UNKNOWN";
                    break;
            }

            logInfo("Health: " + healthStr);
            if (bad)
                logError("Battery health εκτός φυσιολογικών ορίων (σύμφωνα με Android).");
            else
                logOk("Battery health εντός φυσιολογικών ορίων (σύμφωνα με Android).");

        } catch (Exception e) {
            logError("Battery error: " + e.getMessage());
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

            if (!online) logError("Internet: OFFLINE (δεν υπάρχει ενεργή σύνδεση).");
            else logOk("Internet: ONLINE.");

        } catch (Exception e) {
            logAccessDenied("Network state: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 6 — WiFi
     * ============================================================ */
    private void labWifiSignal() {
        logSection("LAB 6 — WiFi");

        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            if (wm == null) {
                logAccessDenied("WiFiManager");
                logLine();
                return;
            }

            if (!wm.isWifiEnabled()) {
                logWarn("WiFi: OFF.");
                logLine();
                return;
            }

            WifiInfo info = wm.getConnectionInfo();
            if (info != null) {
                int rssi = info.getRssi();
                int linkSpeed = info.getLinkSpeed(); // Mbps

                logInfo("WiFi RSSI: " + rssi + " dBm");
                logInfo("WiFi link speed: " + linkSpeed + " Mbps");

                if (rssi > -65) {
                    logOk("WiFi σήμα: Ισχυρό.");
                } else if (rssi > -80) {
                    logWarn("WiFi σήμα: Μέτριο — πιθανές πτώσεις ταχύτητας.");
                } else {
                    logError("WiFi σήμα: Αδύναμο — πιθανές αποσυνδέσεις.");
                }
            } else {
                logWarn("Δεν βρέθηκαν λεπτομέρειες σύνδεσης WiFi (χωρίς active link).");
            }

        } catch (SecurityException se) {
            logAccessDenied("WiFi details (location permission)");
        } catch (Exception e) {
            logError("WiFi error: " + e.getMessage());
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
            int count = (all == null ? 0 : all.size());
            logInfo("Σύνολο αισθητήρων: " + count);

            checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");
            checkSensor(sm, Sensor.TYPE_GYROSCOPE, "Gyroscope");
            checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer");
            checkSensor(sm, Sensor.TYPE_LIGHT, "Light Sensor");
            checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");

        } catch (Exception e) {
            logError("Sensor error: " + e.getMessage());
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
            float density = dm.density;
            int dpi = dm.densityDpi;

            logInfo("Resolution: " + w + " × " + h);
            logInfo("Density: " + density + "  (" + dpi + " dpi)");

            if (w >= 720 && h >= 1280)
                logOk("Display resolution OK για σύγχρονες εφαρμογές.");
            else
                logWarn("Χαμηλή ανάλυση οθόνης για νεότερα UI.");

        } catch (Exception e) {
            logError("Display error: " + e.getMessage());
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
                        float cpuTemp = temps[0];
                        logInfo(String.format(Locale.US, "CPU Temp: %.1f°C", cpuTemp));

                        if (cpuTemp > 80f)
                            logError("CPU θερμοκρασία πολύ υψηλή — πιθανό thermal throttling / βλάβη ψύξης.");
                        else if (cpuTemp > 70f)
                            logWarn("CPU θερμοκρασία αυξημένη — έντονο φορτίο ή κακός αερισμός.");
                        else
                            logOk("CPU θερμοκρασία σε φυσιολογικά όρια.");
                    } else {
                        logAccessDenied("Thermal sensors (no data)");
                    }
                } else {
                    logAccessDenied("HardwarePropertiesManager");
                }

            } catch (Exception e) {
                logAccessDenied("Thermal sensors: " + e.getMessage());
            }
        } else {
            logAccessDenied("Thermal API < 29");
        }

        logLine();
    }

    /* ============================================================
     * LAB 10 — System Health / Telephony / Live RAM
     * ============================================================ */
    private void labSystemHealth() {
        logSection("LAB 10 — System / Telephony / Live RAM");

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
                        "Live RAM: %s free / %s total (%.0f%% free)",
                        readable(free),
                        readable(total),
                        pct
                );
                logInfo(ramLine);

                if (pct >= 25f)
                    logOk("Live RAM status: OK.");
                else if (pct >= 15f)
                    logWarn("Live RAM status: Μέτριο — πιθανές καθυστερήσεις με βαριά apps.");
                else
                    logError("Live RAM status: Χαμηλό — έντονα κολλήματα, προτείνεται καθαρισμός.");
            }
        } catch (Exception e) {
            logError("Live RAM error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 11 — I/O Performance (Internal Storage)
     * ============================================================ */
    private void labIOPerformance() {
        logSection("LAB 11 — I/O Performance (Internal)");

        File cache = getCacheDir();
        if (cache == null) {
            logAccessDenied("cache dir");
            logLine();
            return;
        }

        File testFile = new File(cache, "gel_io_benchmark.tmp");
        int mbToTest = 4; // 4MB
        byte[] buf = new byte[1024 * 1024]; // 1MB buffer

        long writeMs = -1;
        long readMs = -1;

        try {
            long start = System.nanoTime();
            FileOutputStream fos = new FileOutputStream(testFile);
            for (int i = 0; i < mbToTest; i++) {
                fos.write(buf);
            }
            fos.flush();
            fos.close();
            writeMs = (System.nanoTime() - start) / 1_000_000L;

            long totalBytes = mbToTest * 1024L * 1024L;
            float writeSpeed = (writeMs > 0)
                    ? (totalBytes / 1024f / 1024f) / (writeMs / 1000f)
                    : 0f;

            logInfo(String.format(Locale.US,
                    "Write test: %d MB in %d ms (%.1f MB/s)", mbToTest, writeMs, writeSpeed));

            start = System.nanoTime();
            FileInputStream fis = new FileInputStream(testFile);
            while (fis.read(buf) != -1) {
                // discard
            }
            fis.close();
            readMs = (System.nanoTime() - start) / 1_000_000L;

            float readSpeed = (readMs > 0)
                    ? (totalBytes / 1024f / 1024f) / (readMs / 1000f)
                    : 0f;

            logInfo(String.format(Locale.US,
                    "Read test: %d MB in %d ms (%.1f MB/s)", mbToTest, readMs, readSpeed));

            if (writeSpeed < 5f || readSpeed < 5f) {
                logWarn("Εσωτερική ταχύτητα I/O χαμηλή — πιθανές καθυστερήσεις σε installs / updates.");
            } else {
                logOk("I/O performance σε αποδεκτά επίπεδα για service χρήση.");
            }

        } catch (Exception e) {
            logAccessDenied("I/O benchmark (" + e.getMessage() + ")");
        } finally {
            try { if (testFile.exists()) testFile.delete(); } catch (Exception ignored) {}
        }

        logLine();
    }

    /* ============================================================
     * LAB 12 — Apps / Processes
     * ============================================================ */
    private void labAppsAndProcesses() {
        logSection("LAB 12 — Apps / Processes");

        try {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            int totalApps = (apps == null) ? 0 : apps.size();
            logInfo("Installed apps (approx): " + totalApps);

            if (totalApps > 200) {
                logWarn("Πολύ μεγάλος αριθμός εφαρμογών — αυξημένη πιθανότητα καθυστερήσεων.");
            } else {
                logOk("Αριθμός εγκατεστημένων εφαρμογών σε λογικά επίπεδα.");
            }
        } catch (Exception e) {
            logAccessDenied("Installed apps (" + e.getMessage() + ")");
        }

        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs =
                        am.getRunningAppProcesses();
                int running = (procs == null) ? 0 : procs.size();
                logInfo("Running app processes (approx): " + running);
            }
        } catch (Exception e) {
            logAccessDenied("Running processes (" + e.getMessage() + ")");
        }

        logLine();
    }

    /* ============================================================
     * LAB 13 — Security Patch / OS Age
     * ============================================================ */
    private void labSecurityPatch() {
        logSection("LAB 13 — Security Patch");

        if (Build.VERSION.SDK_INT >= 23) {
            String patch = Build.VERSION.SECURITY_PATCH;
            logInfo("Android Security Patch: " + patch);

            // Δεν κάνουμε ακριβή ημερομηνία, απλά ενημέρωση
            if (patch == null || patch.isEmpty()) {
                logWarn("Δεν δηλώνεται security patch — πιθανό παλαιό ή custom firmware.");
            } else {
                logOk("Security patch δηλωμένο από το σύστημα.");
            }
        } else {
            logAccessDenied("Security patch (<23)");
        }

        logLine();
    }

    /* ============================================================
     * LAB 14 — Power Details (Capacity / Charging)
     * ============================================================ */
    private void labPowerDetails() {
        logSection("LAB 14 — Power Details");

        try {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                int capacity = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (capacity > 0) {
                    logInfo("Reported capacity (Android): " + capacity + "%");
                }

                long energy = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
                if (energy > 0) {
                    logInfo("Energy counter (μWh): " + energy);
                }
            }
        } catch (Exception e) {
            logAccessDenied("BatteryManager advanced (" + e.getMessage() + ")");
        }

        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i != null) {
                int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String s;
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING: s = "Charging"; break;
                    case BatteryManager.BATTERY_STATUS_FULL: s = "Full"; break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING: s = "Discharging"; break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING: s = "Not Charging"; break;
                    default: s = "Unknown"; break;
                }
                logInfo("Battery status: " + s);
            }
        } catch (Exception e) {
            logAccessDenied("Battery status (LAB14)");
        }

        logLine();
    }

    /* ============================================================
     * LAB 15 — Mobile Radio Type (2G/3G/4G/5G)
     * ============================================================ */
    private void labRadioType() {
        logSection("LAB 15 — Mobile Radio");

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm == null) {
                logAccessDenied("TelephonyManager (radio)");
                logLine();
                return;
            }

            int type;
            try {
                type = tm.getDataNetworkType();
            } catch (SecurityException se) {
                logAccessDenied("Data network type (permission)");
                logLine();
                return;
            }

            String label = networkTypeToString(type);
            logInfo("Data network type: " + label);

        } catch (Exception e) {
            logAccessDenied("Radio type (" + e.getMessage() + ")");
        }

        logLine();
    }

    private String networkTypeToString(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_LTE: return "4G LTE";
            case TelephonyManager.NETWORK_TYPE_NR: return "5G NR";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "3G HSPA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS: return "2G";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
            default: return "Other (" + type + ")";
        }
    }

    /* ============================================================
     * LAB 16 — Bluetooth
     * ============================================================ */
    private void labBluetooth() {
        logSection("LAB 16 — Bluetooth");

        try {
            BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
            if (bt == null) {
                logWarn("Η συσκευή δεν αναφέρει Bluetooth adapter (ή είναι απενεργοποιημένος από OEM).");
            } else {
                logInfo("Bluetooth supported: YES");
                logInfo("Bluetooth enabled: " + bt.isEnabled());

                int bonded = bt.getBondedDevices() != null ? bt.getBondedDevices().size() : 0;
                logInfo("Paired BT devices: " + bonded);

                if (!bt.isEnabled()) {
                    logWarn("Bluetooth είναι OFF (για έλεγχο handsfree απαιτείται ενεργοποίηση).");
                } else {
                    logOk("Bluetooth hardware OK.");
                }
            }
        } catch (Exception e) {
            logAccessDenied("Bluetooth (" + e.getMessage() + ")");
        }

        logLine();
    }

    /* ============================================================
     * LAB 17 — Location / GPS
     * ============================================================ */
    private void labLocationGPS() {
        logSection("LAB 17 — Location / GPS");

        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (lm == null) {
                logAccessDenied("LocationManager");
                logLine();
                return;
            }

            boolean gpsEnabled = false;
            boolean netEnabled = false;
            try {
                gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {}
            try {
                netEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {}

            logInfo("GPS provider enabled: " + gpsEnabled);
            logInfo("Network location enabled: " + netEnabled);

            if (!gpsEnabled && !netEnabled) {
                logWarn("Κανένας provider τοποθεσίας ενεργός — apps με χάρτες ίσως δεν λειτουργούν σωστά.");
            } else {
                logOk("Location services διαθέσιμες (τουλάχιστον ένας provider ενεργός).");
            }

        } catch (SecurityException se) {
            logAccessDenied("Location state (permission)");
        } catch (Exception e) {
            logAccessDenied("Location services (" + e.getMessage() + ")");
        }

        // NFC quick check
        try {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
            if (nfc == null) {
                logInfo("NFC hardware: not reported.");
            } else {
                logInfo("NFC supported: YES, enabled=" + nfc.isEnabled());
            }
        } catch (Exception e) {
            logAccessDenied("NFC state (" + e.getMessage() + ")");
        }

        logLine();
    }

    /* ============================================================
     * LAB 18 — External Storage / SAF
     * ============================================================ */
    private void labExternalStorage() {
        logSection("LAB 18 — External Storage");

        try {
            String state = Environment.getExternalStorageState();
            logInfo("External storage state: " + state);

            if (!Environment.MEDIA_MOUNTED.equals(state)) {
                logWarn("External storage δεν είναι σε πλήρη λειτουργία (MOUNTED).");
            } else {
                logOk("External storage mounted (MEDIA_MOUNTED).");
            }
        } catch (Exception e) {
            logAccessDenied("External storage (" + e.getMessage() + ")");
        }

        logLine();
    }

    /* ============================================================
     * LAB 19 — System Toggles (Accessibility / Dev)
     * ============================================================ */
    private void labSystemToggles() {
        logSection("LAB 19 — System Toggles");

        try {
            String enabledServices =
                    Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            boolean anyService = enabledServices != null && !enabledServices.trim().isEmpty();
            logInfo("Accessibility services enabled: " + anyService);
            if (anyService) {
                logInfo("Enabled services (raw): " + enabledServices);
            }
        } catch (Exception e) {
            logAccessDenied("Accessibility services (" + e.getMessage() + ")");
        }

        try {
            int animScale =
                    Settings.Global.getInt(getContentResolver(),
                            Settings.Global.ANIMATOR_DURATION_SCALE, 1);
            logInfo("Animator duration scale: " + animScale);
        } catch (Exception e) {
            logAccessDenied("Animator scale (" + e.getMessage() + ")");
        }

        logLine();
    }

    /* ============================================================
     * LAB 20 — Uptime / Summary
     * ============================================================ */
    private void labUptimeSummary() {
        logSection("LAB 20 — Uptime / Summary");

        try {
            long upMs = SystemClock.uptimeMillis();
            long realMs = SystemClock.elapsedRealtime();

            float upHours = upMs / 1000f / 3600f;
            float realHours = realMs / 1000f / 3600f;

            logInfo(String.format(Locale.US,
                    "Device uptime (screen-on time base): %.1f ώρες", upHours));
            logInfo(String.format(Locale.US,
                    "Elapsed time since last boot: %.1f ώρες", realHours));

            if (realHours > 72f) {
                logWarn("Η συσκευή δεν έχει γίνει reboot για >3 ημέρες — προτείνεται επανεκκίνηση για σταθερότητα.");
            } else {
                logOk("Uptime σε φυσιολογικά επίπεδα.");
            }
        } catch (Exception e) {
            logAccessDenied("Uptime (" + e.getMessage() + ")");
        }

        logInfo("ΣΥΝΟΨΗ: Χρησιμοποίησε τα ❌ ως αφορμή για άμεση παρέμβαση, " +
                "τα ⚠️ ως παρακολούθηση / σύσταση, και τα ✅ ως επιβεβαίωση καλής λειτουργίας.");

        logLine();
    }

    /* ============================================================
     * UTIL — READABLE BYTES
     * ============================================================ */
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
