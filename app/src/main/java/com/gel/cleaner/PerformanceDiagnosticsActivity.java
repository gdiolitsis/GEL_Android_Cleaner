package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.Looper;
import android.os.SELinux;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;
import java.util.Locale;

// ============================================================
// GEL Phone Diagnosis — Service Lab
// Περιλαμβάνει LAB R (Root / Security State Advanced)
// ============================================================
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

        // Νέος πελάτης → καθάρισμα log
        GELServiceLog.clear();

        logTitle("🔬 GEL Phone Diagnosis — Service Lab");
        logInfo("Μοντέλο: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        logLine();

        runFullDiagnosis();
    }

    /* ============================================================
     * HTML + GEL LOG MIRROR
     * ============================================================ */
    private void appendHtml(String html) {
        ui.post(() -> {
            CharSequence current = txtDiag.getText();
            txtDiag.setText(current + Html.fromHtml(html + "<br>"));
            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
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

    private void logOk(String msg) {
        appendHtml("<font color='#88FF88'>✅ " + escape(msg) + "</font>");
        GELServiceLog.ok(msg);
    }

    private void logWarn(String msg) {
        appendHtml("<font color='#FFD966'>⚠️ " + escape(msg) + "</font>");
        GELServiceLog.warn(msg);
    }

    private void logError(String msg) {
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
        GELServiceLog.error(msg);
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

            labHardware();
            labRootAdvanced();      // 🔥 LAB R — Root / Security State
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
            logOk("Διάγνωση ολοκληρώθηκε. Τα ❌ είναι οι πραγματικές βλάβες.");

        }).start();
    }

    /* ============================================================
     * LAB 1 — HARDWARE / OS
     * ============================================================ */
    private void labHardware() {
        logSection("LAB 1 — Hardware / OS");

        logInfo("Κατασκευαστής: " + Build.MANUFACTURER);
        logInfo("Μοντέλο: " + Build.MODEL);
        logInfo("Device: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);
        logInfo("Board: " + Build.BOARD);

        int api = Build.VERSION.SDK_INT;
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + api + ")");

        if (api < 26) {
            logError("Android < 8 — σοβαρές ελλείψεις ασφαλείας / παλιό σύστημα.");
        } else if (api < 30) {
            logWarn("Android < 11 — πιθανώς χωρίς τα τελευταία security patches.");
        } else {
            logOk("OS level: σύγχρονο.");
        }

        logLine();
    }

    /* ============================================================
     * LAB R — ROOT / SECURITY STATE (ADVANCED)
     * ============================================================ */
    private void labRootAdvanced() {
        logSection("LAB R — Root / Security State (Advanced)");

        boolean rooted = isDeviceRooted();

        if (!rooted) {
            // Καθαρή συσκευή: το γράφουμε καθαρά στο report
            logOk("Δεν εντοπίστηκε root / Magisk. Η συσκευή φαίνεται STOCK.");
            logLine();
            return;
        }

        // 🔴 Rooted συσκευή — πλήρες security report
        logError("ΕΝΤΟΠΙΣΤΗΚΕ ROOT / TAMPERED SYSTEM — υψηλός κίνδυνος για ασφάλεια / banking apps.");

        // 1) Build tags
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) {
            logWarn("Build tags: test-keys (πιθανό custom / rooted ROM).");
        } else {
            logInfo("Build tags: " + tags);
        }

        // 2) SU binaries
        checkPathFlag("/system/bin/su",       "su binary: /system/bin/su");
        checkPathFlag("/system/xbin/su",      "su binary: /system/xbin/su");
        checkPathFlag("/sbin/su",             "su binary: /sbin/su");
        checkPathFlag("/system/su",           "su binary: /system/su");
        checkPathFlag("/vendor/bin/su",       "su binary: /vendor/bin/su");
        checkPathFlag("/system/bin/.ext/su",  "su binary: /system/bin/.ext/su");
        checkPathFlag("/system/usr/we-need-root/su-backup", "su backup binary");

        // 3) Magisk presence
        checkPathFlag("/sbin/.magisk",           "Magisk core folder (/sbin/.magisk)");
        checkPathFlag("/data/adb/magisk",        "Magisk data folder (/data/adb/magisk)");
        checkPathFlag("/cache/magisk.log",       "Magisk log (/cache/magisk.log)");
        checkPathFlag("/data/adb/modules",       "Magisk modules (/data/adb/modules)");

        // 4) BusyBox presence
        checkPathFlag("/system/xbin/busybox",    "BusyBox binary (/system/xbin/busybox)");
        checkPathFlag("/system/bin/busybox",     "BusyBox binary (/system/bin/busybox)");
        checkPathFlag("/busybox",                "BusyBox binary (/busybox)");

        // 5) Γνωστές root apps
        String[] rootPkgs = new String[] {
                "com.topjohnwu.magisk",
                "eu.chainfire.supersu",
                "com.koushikdutta.superuser",
                "com.noshufou.android.su",
                "com.kingoapp.root",
                "com.kingroot.kinguser",
                "com.zachspong.temprootremovejb",
                "com.devadvance.rootcloak",
                "com.saurik.substrate",
                "eu.chainfire.mobileodin.pro"
        };

        boolean anyRootApp = false;
        for (String pkg : rootPkgs) {
            if (isPackageInstalled(pkg)) {
                anyRootApp = true;
                logError("Εντοπίστηκε root app / διαχείριση root: " + pkg);
            }
        }
        if (!anyRootApp) {
            logInfo("Δεν εντοπίστηκαν γνωστές root-management εφαρμογές (με απλό έλεγχο).");
        }

        // 6) SELinux state (όπου υποστηρίζεται)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            boolean enabled = SELinux.isSELinuxEnabled();
            boolean enforced = SELinux.isSELinuxEnforced();

            logInfo("SELinux enabled: " + enabled + " | enforced: " + enforced);
            if (enabled && !enforced) {
                logWarn("SELinux σε PERMISSIVE mode — χαμηλή προστασία kernel.");
            }
        } else {
            logWarn("SELinux info μη διαθέσιμο (πολύ παλιό Android).");
        }

        logLine();
    }

    // Boolean helper για root detection (συμπυκνωμένο)
    private boolean isDeviceRooted() {
        return checkTestKeys()
                || checkSuFiles()
                || checkMagiskFiles();
    }

    private boolean checkTestKeys() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private boolean checkSuFiles() {
        String[] paths = new String[] {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su",
                "/vendor/bin/su",
                "/system/bin/.ext/su",
                "/system/usr/we-need-root/su-backup"
        };
        for (String p : paths) {
            if (fileExists(p)) return true;
        }
        return false;
    }

    private boolean checkMagiskFiles() {
        String[] paths = new String[] {
                "/sbin/.magisk",
                "/data/adb/magisk",
                "/cache/magisk.log",
                "/data/adb/modules"
        };
        for (String p : paths) {
            if (fileExists(p)) return true;
        }
        return false;
    }

    private boolean fileExists(String path) {
        try {
            return new File(path).exists();
        } catch (Throwable t) {
            return false;
        }
    }

    private void checkPathFlag(String path, String description) {
        if (fileExists(path)) {
            logError("Εντοπίστηκε: " + description + "  [" + path + "]");
        } else {
            logInfo("Δεν βρέθηκε: " + description);
        }
    }

    private boolean isPackageInstalled(String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) return false;
        try {
            getPackageManager().getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /* ============================================================
     * LAB 2 — CPU / RAM
     * ============================================================ */
    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU Cores: " + cores);

        if (cores <= 4) {
            logWarn("Λίγοι CPU πυρήνες — πιθανές καθυστερήσεις σε βαριά χρήση.");
        } else {
            logOk("CPU cores: ικανοποιητικοί.");
        }

        long totalMem = getTotalRam();
        logInfo("Συνολική RAM: " + readable(totalMem));

        if (totalMem < gb(2)) {
            logError("RAM < 2GB — συνεχόμενα κολλήματα, προτείνεται ελαφριά χρήση / αλλαγή συσκευής.");
        } else if (totalMem < gb(4)) {
            logWarn("RAM 2–4GB — οριακή για πολλές εφαρμογές.");
        } else {
            logOk("RAM capacity: ΟΚ για καθημερινή χρήση.");
        }

        logLine();
    }

    private long getTotalRam() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.totalMem;
        } catch (Exception e) {
            return 0;
        }
    }

    /* ============================================================
     * LAB 3 — STORAGE
     * ============================================================ */
    private void labStorage() {
        logSection("LAB 3 — Storage");

        try {
            File data = android.os.Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();

            int pct = (int) ((free * 100L) / total);

            logInfo("Χώρος: " + readable(free) + " / " + readable(total) + " (" + pct + "% free)");

            if (pct < 10) {
                logError("Storage < 10% — υψηλός κίνδυνος κολλημάτων / crashes.");
            } else if (pct < 20) {
                logWarn("Storage < 20% — προτείνεται καθάρισμα.");
            } else {
                logOk("Storage: σε ασφαλή επίπεδα.");
            }

        } catch (Exception e) {
            logError("Storage error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 4 — BATTERY
     * ============================================================ */
    private void labBattery() {
        logSection("LAB 4 — Battery");

        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) {
                logError("Δεν μπορώ να διαβάσω στοιχεία μπαταρίας.");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (100f * lvl / scale);

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp / 10f;

            logInfo(String.format(Locale.US, "Battery level: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Battery temp: %.1f°C", temp));

            if (temp > 45) {
                logError("Πολύ υψηλή θερμοκρασία μπαταρίας (> 45°C).");
            } else if (temp > 38) {
                logWarn("Ζεστή μπαταρία (38–45°C).");
            } else {
                logOk("Θερμοκρασία μπαταρίας σε φυσιολογικά επίπεδα.");
            }

            if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                logError("Μπαταρία κατεστραμμένη — προτείνεται άμεση αντικατάσταση.");
            } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                logError("Υπερθέρμανση μπαταρίας (OVERHEAT flag).");
            } else {
                logOk("Battery health: OK σύμφωνα με Android flags.");
            }

        } catch (Exception e) {
            logError("Battery error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 5 — NETWORK
     * ============================================================ */
    private void labNetwork() {
        logSection("LAB 5 — Network");

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) {
                logError("ConnectivityManager λείπει — σοβαρό πρόβλημα συστήματος.");
                logLine();
                return;
            }

            boolean online = false;
            boolean wifi = false;
            boolean mobile = false;

            if (Build.VERSION.SDK_INT >= 23) {
                android.net.Network n = cm.getActiveNetwork();
                NetworkCapabilities caps = cm.getNetworkCapabilities(n);
                if (caps != null) {
                    online = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    mobile = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            } else {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    online = true;
                    wifi = ni.getType() == ConnectivityManager.TYPE_WIFI;
                    mobile = ni.getType() == ConnectivityManager.TYPE_MOBILE;
                }
            }

            if (!online) {
                logError("Καμία ενεργή σύνδεση Internet.");
            } else {
                if (wifi) logOk("WiFi σύνδεση ενεργή.");
                if (mobile) logOk("Mobile Data σύνδεση ενεργή.");
            }

        } catch (Exception e) {
            logError("Network error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 6 — WIFI SIGNAL
     * ============================================================ */
    private void labWifiSignal() {
        logSection("LAB 6 — WiFi Signal");

        try {
            android.net.wifi.WifiManager wm =
                    (android.net.wifi.WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

            if (wm == null || !wm.isWifiEnabled()) {
                logWarn("WiFi απενεργοποιημένο ή μη διαθέσιμο.");
                logLine();
                return;
            }

            int rssi = wm.getConnectionInfo().getRssi();
            logInfo("WiFi RSSI: " + rssi + " dBm");

            if (rssi > -60) {
                logOk("Πολύ καλή λήψη WiFi.");
            } else if (rssi > -75) {
                logWarn("Μέτρια λήψη WiFi.");
            } else {
                logError("Κακή λήψη WiFi (< -75 dBm).");
            }

        } catch (Exception e) {
            logError("WiFi error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 7 — SENSORS
     * ============================================================ */
    private void labSensors() {
        logSection("LAB 7 — Sensors");

        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sm == null) {
                logError("SensorManager λείπει — πιθανό σοβαρό πρόβλημα framework.");
                logLine();
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Σύνολο αισθητήρων: " + (all == null ? 0 : all.size()));

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

        if (!ok) {
            if (type == Sensor.TYPE_ACCELEROMETER || type == Sensor.TYPE_PROXIMITY) {
                logError(name + " λείπει — πιθανή βλάβη πλακέτας / flex.");
            } else {
                logWarn(name + " δεν υπάρχει (ή δεν αναφέρεται).");
            }
        } else {
            logOk(name + " OK.");
        }
    }

    /* ============================================================
     * LAB 8 — DISPLAY
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

            logInfo("Resolution: " + w + " × " + h);

            if (Math.min(w, h) < 720) {
                logWarn("Χαμηλή ανάλυση — πιθανώς οικονομική οθόνη.");
            } else {
                logOk("Display resolution: επαρκής.");
            }

        } catch (Exception e) {
            logError("Display error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 9 — THERMAL
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
                        float t = temps[0];
                        logInfo("CPU Temp: " + t + "°C");

                        if (t > 80) {
                            logError("Πολύ υψηλή θερμοκρασία CPU (> 80°C) — πιθανή βλάβη ψύξης / SoC.");
                        } else if (t > 70) {
                            logWarn("CPU ζεστό (70–80°C) — throttling / κολλήματα.");
                        } else {
                            logOk("CPU θερμοκρασία σε φυσιολογικά επίπεδα.");
                        }
                    } else {
                        logWarn("Δεν δόθηκαν θερμοκρασίες CPU από το σύστημα.");
                    }
                } else {
                    logWarn("HardwarePropertiesManager όχι διαθέσιμο — περιορισμένη thermal διάγνωση.");
                }

            } catch (Exception e) {
                logError("Thermal error: " + e.getMessage());
            }

        } else {
            logWarn("Thermal API δεν υποστηρίζεται (API < 29).");
        }

        logLine();
    }

    /* ============================================================
     * LAB 10 — SYSTEM HEALTH
     * ============================================================ */
    private void labSystemHealth() {
        logSection("LAB 10 — System Health / Telephony");

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (tm != null) {
                logInfo("Network operator: " + tm.getNetworkOperatorName());
                logInfo("SIM operator: " + tm.getSimOperatorName());
            } else {
                logWarn("TelephonyManager δεν υπάρχει (WiFi-only συσκευή ή σοβαρό σφάλμα).");
            }

        } catch (SecurityException se) {
            logWarn("Δεν έχω δικαίωμα για πλήρη telephony info (OK για τη διάγνωση).");
        } catch (Exception e) {
            logError("Telephony error: " + e.getMessage());
        }

        try {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long avail = mi.availMem;
            long total = mi.totalMem;
            int pct = (int) ((avail * 100L) / total);

            logInfo("Live RAM: " + readable(avail) + " (" + pct + "% free)");

            if (pct < 10) {
                logError("Πολύ χαμηλή διαθέσιμη RAM (< 10%) — προτείνεται κλείσιμο apps / reboot.");
            } else if (pct < 20) {
                logWarn("Χαμηλή διαθέσιμη RAM (< 20%) — οριακή κατάσταση.");
            } else {
                logOk("Live RAM status: αποδεκτό.");
            }

        } catch (Exception e) {
            logError("RAM error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * HELPERS
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

    private long gb(int g) {
        return g * 1024L * 1024L * 1024L;
    }
}
