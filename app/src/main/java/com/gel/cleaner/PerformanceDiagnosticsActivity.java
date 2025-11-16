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

        // Νέος πελάτης → καθάρισμα προηγούμενου Service Log
        GELServiceLog.clear();

        logTitle("🔬 GEL Phone Diagnosis — Service Lab");
        logInfo("Μοντέλο: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        logLine();

        runFullDiagnosis();
    }

    /* ============================================================
     * HTML + NEW GEL LOGGING (καθρέφτης στο Service Log)
     * ============================================================ */
    private void appendHtml(String html) {
        ui.post(() -> {
            CharSequence current = txtDiag.getText();
            txtDiag.setText(current + Html.fromHtml(html + "<br>"));
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
     * MAIN DIAG (με αυτόματο root-aware flow)
     * ============================================================ */
    private void runFullDiagnosis() {
        new Thread(() -> {

            labHardware();

            // LAB 0 — Root status & extra διαγνώσεις
            boolean rooted = isDeviceRooted();
            if (rooted) {
                logSection("LAB 0 — Root Status / Security (PRO)");
                logWarn("Η συσκευή φαίνεται ROOTED — ενεργοποίηση επιπλέον ελέγχων.");
                labRootAdvanced();
            } else {
                logSection("LAB 0 — Root Status (SAFE)");
                logOk("Η συσκευή δεν φαίνεται rooted με τους γνωστούς ελέγχους.");
                labRootBasic();
            }

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
            logOk("Διάγνωση ολοκληρώθηκε. Τα ❌ είναι οι βλάβες / σοβαρά προβλήματα.");

        }).start();
    }

    /* ============================================================
     * ROOT DETECTION CORE
     * ============================================================ */
    private boolean isDeviceRooted() {
        return checkRootBuildTags() || checkRootPaths() || checkSuCommand();
    }

    private boolean checkRootBuildTags() {
        try {
            String tags = Build.TAGS;
            return tags != null && tags.contains("test-keys");
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean checkRootPaths() {
        String[] paths = new String[] {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/bin/.ext/.su",
                "/system/app/Superuser.apk",
                "/system/app/SuperSU.apk",
                "/system/xbin/daemonsu",
                "/system/xbin/busybox",
                "/su/bin/su",
                "/magisk/.core/bin/su",
                "/data/adb/magisk.db",
                "/data/adb/magisk",
                "/data/adb/modules"
        };
        try {
            for (String path : paths) {
                if (new File(path).exists()) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean checkSuCommand() {
        Process p = null;
        BufferedReader in = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            return (line != null);
        } catch (Exception ignored) {
            return false;
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception ignored) {}
            if (p != null) p.destroy();
        }
    }

    /* ============================================================
     * LAB 0 — ROOT BASIC / ADVANCED
     * ============================================================ */
    private void labRootBasic() {
        logInfo("Safe mode diagnostics — καμία ένδειξη root με τους βασικούς ελέγχους.");
    }

    private void labRootAdvanced() {
        logInfo("Εντοπίστηκαν ενδείξεις root (build tags / su / root paths).");

        // Έλεγχος κλασικών root components
        checkRootFile("/system/app/Superuser.apk", "Superuser.apk");
        checkRootFile("/system/app/SuperSU.apk", "SuperSU.apk");
        checkRootFile("/system/xbin/daemonsu", "daemonsu binary");
        checkRootFile("/system/xbin/busybox", "busybox binary");
        checkRootFile("/su/bin/su", "su binary (/su)");
        checkRootFile("/data/adb/magisk.db", "Magisk database");
        checkRootFile("/data/adb/magisk", "Magisk core");
        checkRootFile("/data/adb/modules", "Magisk modules folder");

        // SELinux state (όπου υποστηρίζεται)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                boolean enforced = SELinux.isSELinuxEnforced();
                if (!enforced) {
                    logWarn("SELinux σε PERMISSIVE — χαμηλή ασφάλεια συστήματος.");
                } else {
                    logOk("SELinux Enforced.");
                }
            } else {
                logWarn("SELinux state δεν είναι διαθέσιμο σε αυτή την έκδοση Android.");
            }
        } catch (Throwable t) {
            logWarn("Δεν ήταν δυνατή η ανάγνωση SELinux state: " + t.getMessage());
        }

        // Απλό "Bootloader / custom" hint (όσο γίνεται από εδώ)
        try {
            String bootloader = Build.BOOTLOADER;
            if (bootloader != null && !"unknown".equalsIgnoreCase(bootloader)) {
                logInfo("Bootloader string: " + bootloader);
            }
        } catch (Exception ignored) {}

        logLine();
    }

    private void checkRootFile(String path, String label) {
        try {
            File f = new File(path);
            if (f.exists()) {
                logWarn("Root component εντοπίστηκε: " + label + " (" + path + ")");
            }
        } catch (Exception ignored) {}
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
            logError("Android < 8 — σοβαρές ελλείψεις ασφαλείας.");
        } else if (api < 30) {
            logWarn("Android < 11 — ίσως χωρίς σύγχρονα security patches.");
        } else {
            logOk("OS level OK για σύγχρονη χρήση.");
        }

        logLine();
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
            logOk("CPU cores OK.");
        }

        long totalMem = getTotalRam();
        logInfo("Συνολική RAM: " + readable(totalMem));

        if (totalMem < gb(2)) {
            logError("RAM < 2GB — συνεχόμενα κολλήματα σε απλή χρήση.");
        } else if (totalMem < gb(4)) {
            logWarn("RAM 2–4GB — οριακή για βαριές εφαρμογές.");
        } else {
            logOk("RAM capacity OK.");
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
            File data = Environment.getDataDirectory();
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
                logOk("Storage σε καλά επίπεδα.");
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
                logError("Δεν μπορώ να διαβάσω μπαταρία (ACTION_BATTERY_CHANGED=null).");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (100f * lvl / scale);

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp / 10f;

            logInfo(String.format(Locale.US, "Battery: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Temp: %.1f°C", temp));

            if (temp > 45) {
                logError("Πολύ υψηλή θερμοκρασία μπαταρίας — πιθανή βλάβη / φορτιστής.");
            } else if (temp > 38) {
                logWarn("Ζεστή μπαταρία (>38°C) — έντονη χρήση ή θερμικό θέμα.");
            }

            if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                    health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                logError("Μπαταρία κατεστραμμένη — προτείνεται άμεση αντικατάσταση.");
            } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                logError("Υπερθέρμανση μπαταρίας (Android flag)!");
            } else {
                logOk("Battery health OK (σύμφωνα με Android).");
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
                logError("ConnectivityManager λείπει — πιθανό σοβαρό σφάλμα συστήματος.");
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
                logError("Καμία ενεργή σύνδεση Internet αυτή τη στιγμή.");
            } else {
                if (wifi) logOk("WiFi ενεργό.");
                if (mobile) logOk("Mobile Data ενεργά.");
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
                logWarn("WiFi κλειστό ή μη διαθέσιμο.");
                logLine();
                return;
            }

            int rssi = wm.getConnectionInfo().getRssi();
            logInfo("WiFi RSSI: " + rssi + " dBm");

            if (rssi > -60) {
                logOk("Πολύ καλή λήψη WiFi.");
            } else if (rssi > -75) {
                logWarn("Μέτρια λήψη WiFi (πιθανά disconnects).");
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
                logError("SensorManager λείπει — πιθανό πρόβλημα framework.");
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
                logError(name + " λείπει — πιθανή βλάβη / ελλιπής πλακέτα.");
            } else {
                logWarn(name + " δεν υπάρχει σε αυτή τη συσκευή.");
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
                if (disp != null) {
                    disp.getRealMetrics(dm);
                } else {
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                }
            } else {
                getWindowManager().getDefaultDisplay().getMetrics(dm);
            }

            int w = dm.widthPixels;
            int h = dm.heightPixels;

            logInfo("Resolution: " + w + " × " + h);

            if (Math.min(w, h) < 720) {
                logWarn("Χαμηλή ανάλυση οθόνης — πιθανή «θολή» εμπειρία.");
            } else {
                logOk("Display ανάλυση OK.");
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
                            logError("Πολύ υψηλή θερμοκρασία CPU (>80°C) — πιθανή βλάβη ψύξης / SoC.");
                        } else if (t > 70) {
                            logWarn("Υψηλή θερμοκρασία CPU (70–80°C) — throttling / κολλήματα.");
                        } else {
                            logOk("CPU θερμοκρασία εντός φυσιολογικών ορίων.");
                        }
                    } else {
                        logWarn("Δεν δόθηκαν CPU θερμοκρασίες από το σύστημα.");
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
                String net = tm.getNetworkOperatorName();
                String sim = tm.getSimOperatorName();

                logInfo("Network operator: " + (net == null ? "N/A" : net));
                logInfo("SIM operator: " + (sim == null ? "N/A" : sim));
            } else {
                logWarn("TelephonyManager δεν υπάρχει (ίσως WiFi-only συσκευή).");
            }

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
                logError("Πολύ χαμηλή διαθέσιμη RAM (<10%) — σχεδόν σίγουρα κολλήματα.");
            } else if (pct < 20) {
                logWarn("Χαμηλή διαθέσιμη RAM (<20%) — προτείνεται restart / κλείσιμο apps.");
            } else {
                logOk("RAM live status OK.");
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
