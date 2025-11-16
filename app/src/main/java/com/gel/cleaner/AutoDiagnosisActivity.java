package com.gel.cleaner;

import android.content.Intent;
import android.content.IntentFilter;
import android.app.ActivityManager;
import android.content.Context;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

// ============================================================
// AutoDiagnosisActivity
// Full Auto Diagnosis (NON-ROOT + extra ROOT LABS)
// Γράφει τα πάντα και στο GELServiceLog για export
// ============================================================
public class AutoDiagnosisActivity extends AppCompatActivity {

    private TextView txtDiag;
    private ScrollView scroll;
    private Handler ui;

    private boolean isRooted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        txtDiag = new TextView(this);
        txtDiag.setTextSize(14f);
        txtDiag.setTextColor(0xFFE0E0E0);
        txtDiag.setPadding(dp(16), dp(16), dp(16), dp(16));
        txtDiag.setMovementMethod(new ScrollingMovementMethod());

        scroll.addView(txtDiag);
        setContentView(scroll);

        ui = new Handler(Looper.getMainLooper());

        // Κάθε νέα αυτόματη διάγνωση ξεκινά με καθαρό log
        GELServiceLog.clear();

        // Header + Root status
        logTitle("🤖 GEL Auto Diagnosis — Service Lab");
        logInfo("Συσκευή: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");

        isRooted = isDeviceRooted();
        if (isRooted) {
            logWarn("Root status: ROOTED (βρέθηκαν ενδείξεις root).");
        } else {
            logInfo("Root status: NOT ROOTED / LOCKED (δεν βρέθηκαν σαφείς ενδείξεις).");
        }
        logLine();

        runFullDiagnosis();
    }

    // ============================================================
    // HTML + GELServiceLog mirroring
    // ============================================================
    private void appendHtml(String html) {
        ui.post(() -> {
            CharSequence current = txtDiag.getText();
            String add = Html.fromHtml(html + "<br>") + "";
            txtDiag.setText(current + add);
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

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }

    // ============================================================
    // MAIN AUTO DIAG
    // ============================================================
    private void runFullDiagnosis() {
        new Thread(() -> {

            // Βασικά LABS (όλες οι συσκευές)
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

            // Επιπλέον LABS μόνο αν η συσκευή είναι rooted
            if (isRooted) {
                logLine();
                logSection("LAB R0 — Extra Root Diagnostics ενεργά");
                logInfo("Εκτελούνται επιπλέον έλεγχοι ασφαλείας για rooted συσκευές.");
                logLine();

                labRootFiles();
                labRootProps();
                labSelinux();
                labRootPackages();
            } else {
                logInfo("Παράλειψη Root LABS (η συσκευή δεν φαίνεται rooted).");
            }

            logLine();
            logOk("Auto Diagnosis ολοκληρώθηκε. Τα ❌ είναι πραγματικές βλάβες ή σοβαρές ενδείξεις.");

        }).start();
    }

    // ============================================================
    // QUICK ROOT DETECTION
    // ============================================================
    private boolean isDeviceRooted() {
        // 1) build tags
        String tags = Build.TAGS;
        if (tags != null && tags.contains("test-keys")) {
            return true;
        }

        // 2) SU binary σε γνωστά μονοπάτια
        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su",
                "/system/bin/.ext/su",
                "/system/usr/we-need-root/su",
                "/system/xbin/mu"
        };
        for (String path : paths) {
            try {
                if (new File(path).exists()) {
                    return true;
                }
            } catch (Throwable ignored) {}
        }

        // 3) γρήγορο getprop check (χωρίς να μπλέκουμε με su -c)
        try {
            String debuggable = runGetProp("ro.debuggable");
            String secure = runGetProp("ro.secure");
            if ("1".equals(debuggable) && "0".equals(secure)) {
                return true;
            }
        } catch (Throwable ignored) {}

        return false;
    }

    private String runGetProp(String key) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            if (line == null) line = "";
            return line.trim();
        } catch (Exception e) {
            return "";
        } finally {
            try { if (br != null) br.close(); } catch (Exception ignored) {}
        }
    }

    // ============================================================
    // LABS — COMMON (NON-ROOT)
    // ============================================================
    private void labHardware() {
        logSection("LAB 1 — Hardware / OS");

        logInfo("Κατασκευαστής: " + Build.MANUFACTURER);
        logInfo("Μοντέλο: " + Build.MODEL);
        logInfo("Device: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);
        logInfo("Board: " + Build.BOARD);

        int api = Build.VERSION.SDK_INT;
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + api + ")");

        if (api < 26) logError("Android < 8 — σοβαρές ελλείψεις ασφαλείας.");
        else if (api < 30) logWarn("Android < 11 — πιθανόν παλιά security patches.");
        else logOk("OS level OK για καθημερινή χρήση.");

        logLine();
    }

    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU Cores: " + cores);
        if (cores <= 4) logWarn("Λίγοι CPU πυρήνες — πιθανές καθυστερήσεις σε βαριά χρήση.");
        else logOk("CPU cores OK.");

        long totalMem = getTotalRam();
        logInfo("Συνολική RAM: " + readable(totalMem));

        if (totalMem < gb(2)) logError("RAM < 2GB — συνεχόμενα κολλήματα, απαραίτητος καθαρισμός / αλλαγή συσκευής.");
        else if (totalMem < gb(4)) logWarn("RAM 2–4GB — οριακή για πολλές εφαρμογές.");
        else logOk("RAM capacity OK.");

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

    private void labStorage() {
        logSection("LAB 3 — Storage");

        try {
            File data = Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();

            int pct = (int) ((free * 100L) / total);

            logInfo("Χώρος: " + readable(free) + " / " + readable(total) + " (" + pct + "% free)");

            if (pct < 10) logError("Storage < 10% — αναμένονται κολλήματα / crashes, απαιτείται καθάρισμα.");
            else if (pct < 20) logWarn("Storage < 20% — προτείνεται καθάρισμα χώρου.");
            else logOk("Storage status OK.");

        } catch (Exception e) {
            logError("Storage error: " + e.getMessage());
        }

        logLine();
    }

    private void labBattery() {
        logSection("LAB 4 — Battery");

        try {
            Intent i = registerReceiver(null, new IntentFilter(BatteryManager.ACTION_CHARGING));
        } catch (Exception ignored) {}

        try {
            android.content.Intent i =
                    registerReceiver(null, new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED));
            if (i == null) {
                logError("Δεν μπορώ να διαβάσω στοιχεία μπαταρίας.");
                logLine();
                return;
            }

            int lvl = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (scale > 0) ? (100f * lvl / scale) : -1f;

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = rawTemp / 10f;

            if (pct >= 0) {
                logInfo(String.format(Locale.US, "Battery: %.1f%%", pct));
            }
            logInfo(String.format(Locale.US, "Θερμοκρασία: %.1f°C", temp));

            if (temp > 45) logError("Μπαταρία πολύ ζεστή — πιθανή βλάβη ή κακός φορτιστής.");
            else if (temp > 38) logWarn("Ζεστή μπαταρία — έλεγχος χρήσης / εφαρμογών.");

            if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                    health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
                logError("Battery health: DEAD / FAILURE — προτείνεται αλλαγή.");
            else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
                logError("Battery health: OVERHEAT — επικίνδυνη κατάσταση.");
            else
                logOk("Battery health εντός φυσιολογικών ορίων (σύμφωνα με Android).");

        } catch (Exception e) {
            logError("Battery error: " + e.getMessage());
        }

        logLine();
    }

    private void labNetwork() {
        logSection("LAB 5 — Network");

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) {
                logError("ConnectivityManager λείπει από το σύστημα.");
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

            if (!online) logError("Καμία ενεργή σύνδεση Internet αυτή τη στιγμή.");
            else {
                if (wifi) logOk("WiFi ενεργό.");
                if (mobile) logOk("Mobile Data ενεργά.");
            }

        } catch (Exception e) {
            logError("Network error: " + e.getMessage());
        }

        logLine();
    }

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

            if (rssi > -60) logOk("Πολύ καλή λήψη.");
            else if (rssi > -75) logWarn("Μέτρια λήψη.");
            else logError("Κακή WiFi λήψη (< -75 dBm).");

        } catch (Exception e) {
            logError("WiFi error: " + e.getMessage());
        }

        logLine();
    }

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
            if (type == Sensor.TYPE_ACCELEROMETER || type == Sensor.TYPE_PROXIMITY)
                logError(name + " λείπει — πιθανή βλάβη hardware.");
            else
                logWarn(name + " δεν υπάρχει ή δεν αναφέρεται.");
        } else {
            logOk(name + " OK.");
        }
    }

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

            if (Math.min(w, h) < 720)
                logWarn("Χαμηλή ανάλυση οθόνης (κάτω από HD).");
            else
                logOk("Display resolution OK.");

        } catch (Exception e) {
            logError("Display error: " + e.getMessage());
        }

        logLine();
    }

    private void labThermal() {
        logSection("LAB 9 — Thermal (CPU)");

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

                        if (t > 80) logError("Πολύ υψηλή θερμοκρασία CPU — throttling / πιθανή βλάβη.");
                        else if (t > 70) logWarn("CPU ζεστό — πιθανό throttling.");
                        else logOk("CPU θερμοκρασία OK.");
                    } else {
                        logWarn("Δεν δόθηκαν τιμές θερμοκρασίας CPU.");
                    }
                } else {
                    logWarn("HardwarePropertiesManager όχι διαθέσιμο.");
                }

            } catch (Exception e) {
                logError("Thermal error: " + e.getMessage());
            }
        } else {
            logWarn("Thermal APIs δεν υποστηρίζονται (API < 29).");
        }

        logLine();
    }

    private void labSystemHealth() {
        logSection("LAB 10 — System / Telephony / Live RAM");

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (tm != null) {
                logInfo("Network operator: " + tm.getNetworkOperatorName());
                logInfo("SIM operator: " + tm.getSimOperatorName());
            } else {
                logWarn("TelephonyManager δεν υπάρχει (ίσως tablet).");
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

            if (pct < 10) logError("Πολύ χαμηλή διαθέσιμη RAM — αναμενόμενα κολλήματα.");
            else if (pct < 20) logWarn("Χαμηλή διαθέσιμη RAM — προτείνεται restart & καθάρισμα.");
            else logOk("RAM live status OK.");

        } catch (Exception e) {
            logError("System RAM error: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // LABS — ONLY IF ROOTED
    // ============================================================
    private void labRootFiles() {
        logSection("LAB R1 — Root Binaries / Paths");

        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/system/su",
                "/system/bin/.ext/su",
                "/system/xbin/mu"
        };

        boolean any = false;
        for (String p : paths) {
            try {
                if (new File(p).exists()) {
                    any = true;
                    logError("Βρέθηκε root binary: " + p);
                }
            } catch (Throwable t) {
                // ignore
            }
        }

        if (!any) {
            logWarn("Δεν βρέθηκαν su binaries στα κλασικά paths, αλλά η συσκευή είναι ήδη marked ως rooted.");
        }

        logLine();
    }

    private void labRootProps() {
        logSection("LAB R2 — System Properties (getprop)");

        String[] keys = {
                "ro.debuggable",
                "ro.secure",
                "ro.build.type",
                "ro.build.tags"
        };

        for (String k : keys) {
            String v = runGetProp(k);
            logInfo("prop " + k + " = " + (v.isEmpty() ? "[empty]" : v));
        }

        String debuggable = runGetProp("ro.debuggable");
        String secure = runGetProp("ro.secure");

        if ("1".equals(debuggable) && "0".equals(secure)) {
            logError("ro.debuggable=1 & ro.secure=0 — συσκευή σε πολύ ανοιχτό / ανασφαλές mode.");
        } else if ("1".equals(debuggable)) {
            logWarn("ro.debuggable=1 — build πιθανόν debug / engineer.");
        }

        logLine();
    }

    private void labSelinux() {
        logSection("LAB R3 — SELinux Status");

        String mode = "[unknown]";
        try {
            // Προσπαθούμε απλό getenforce
            Process p = Runtime.getRuntime().exec("getenforce");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            if (line != null) mode = line.trim();
            br.close();
        } catch (Exception ignored) {}

        logInfo("SELinux mode: " + mode);

        if ("Permissive".equalsIgnoreCase(mode)) {
            logError("SELinux Permissive — πολύ χαλαρή προστασία, υψηλός κίνδυνος.");
        } else if ("Disabled".equalsIgnoreCase(mode)) {
            logError("SELinux Disabled — σύστημα εντελώς απροστάτευτο.");
        } else if ("Enforcing".equalsIgnoreCase(mode)) {
            logOk("SELinux Enforcing — τουλάχιστον βασική προστασία παραμένει.");
        } else {
            logWarn("SELinux mode άγνωστο / μη αναγνωρίσιμο.");
        }

        logLine();
    }

    private void labRootPackages() {
        logSection("LAB R4 — Γνωστές Root / Hacking Εφαρμογές");

        List<String> suspects = Arrays.asList(
                "com.topjohnwu.magisk",
                "eu.chainfire.supersu",
                "com.koushikdutta.superuser",
                "com.noshufou.android.su",
                "com.thirdparty.superuser",
                "eu.chainfire.cfroot",
                "com.yellowes.su"
        );

        android.content.pm.PackageManager pm = getPackageManager();
        boolean any = false;

        for (String pkg : suspects) {
            try {
                pm.getPackageInfo(pkg, 0);
                any = true;
                logError("Εντοπίστηκε εγκατεστημένη root/hack app: " + pkg);
            } catch (Exception ignored) {}
        }

        if (!any) {
            logOk("Δεν βρέθηκαν γνωστές root/hacking εφαρμογές από τη λίστα ελέγχου.");
        }

        logLine();
    }

    // ============================================================
    // UTILS
    // ============================================================
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
