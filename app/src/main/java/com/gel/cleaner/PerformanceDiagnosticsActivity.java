package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
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

        logTitle("🔬 GEL Phone Diagnosis — Service Lab");
        logInfo("Μοντέλο: " + Build.MANUFACTURER + " " + Build.MODEL);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        logLine();

        runFullDiagnosis();
    }

    /* ============================================================
     * HTML LOG HELPERS
     * ============================================================ */
    private void appendHtmlLine(String html) {
        ui.post(() -> {
            CharSequence current = txtDiag.getText();
            String add = Html.fromHtml(html + "<br>") + "";
            txtDiag.setText(current + add);

            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    private void logTitle(String msg) { appendHtmlLine("<b>" + escape(msg) + "</b>"); }
    private void logSection(String msg) { appendHtmlLine("<br><b>▌ " + escape(msg) + "</b>"); }
    private void logInfo(String msg) { appendHtmlLine("ℹ️ " + escape(msg)); }
    private void logOk(String msg) { appendHtmlLine("<font color='#88FF88'>✅ " + escape(msg) + "</font>"); }
    private void logWarn(String msg) { appendHtmlLine("<font color='#FFD966'>⚠️ " + escape(msg) + "</font>"); }
    private void logError(String msg) { appendHtmlLine("<font color='#FF5555'>❌ " + escape(msg) + "</font>"); }
    private void logLine() { appendHtmlLine("<font color='#666666'>────────────────────────────────</font>"); }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /* ============================================================
     * MAIN FULL DIAG
     * ============================================================ */
    private void runFullDiagnosis() {
        new Thread(() -> {

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
            logOk("Διάγνωση ολοκληρώθηκε. Τα κόκκινα ❌ είναι οι πραγματικές βλάβες.");

        }).start();
    }

    /* ============================================================
     * LAB 1 — HARDWARE / OS
     * ============================================================ */
    private void labHardware() {
        logSection("LAB 1 — Hardware / OS");

        logInfo("Κατασκευαστής: " + Build.MANUFACTURER);
        logInfo("Μοντέλο: " + Build.MODEL);
        logInfo("Συσκευή: " + Build.DEVICE);
        logInfo("Product: " + Build.PRODUCT);
        logInfo("Board: " + Build.BOARD);

        int api = Build.VERSION.SDK_INT;
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + api + ")");

        if (api < 26) logError("Android < 8 — σοβαρές ελλείψεις ασφαλείας.");
        else if (api < 30) logWarn("Android < 11 — ίσως παλιά security patches.");
        else logOk("OS level OK.");

        logLine();
    }

    /* ============================================================
     * LAB 2 — CPU / RAM
     * ============================================================ */
    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM");

        int cores = Runtime.getRuntime().availableProcessors();
        logInfo("CPU Cores: " + cores);

        if (cores <= 4) logWarn("Λίγοι πυρήνες CPU — πιθανές καθυστερήσεις.");
        else logOk("CPU cores OK.");

        long totalMem = getTotalRam();
        if (totalMem > 0) logInfo("Συνολική RAM: " + readable(totalMem));

        if (totalMem < gb(2)) logError("RAM < 2GB — συχνά κολλήματα.");
        else if (totalMem < gb(4)) logWarn("RAM 2–4GB — οριακή για βαριά χρήση.");
        else logOk("RAM OK.");

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

            if (pct < 10) logError("Storage < 10% — βαρύ κόλλημα / crashes.");
            else if (pct < 20) logWarn("Storage < 20% — προτείνεται καθάρισμα.");
            else logOk("Storage OK.");

        } catch (Exception e) {
            logError("Αποτυχία ανάγνωσης storage: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 4 — BATTERY
     * ============================================================ */
    private void labBattery() {
        logSection("LAB 4 — Μπαταρία");

        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) {
                logError("Δεν μπόρεσα να διαβάσω μπαταρία.");
                logLine();
                return;
            }

            int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (100f * level / scale);

            int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int tempRaw = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = tempRaw / 10f;

            logInfo(String.format(Locale.US, "Φόρτιση: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Θερμοκρασία: %.1f°C", temp));

            if (temp > 45) logError("Μπαταρία πολύ ζεστή — πιθανή βλάβη.");
            else if (temp > 38) logWarn("Υψηλή θερμοκρασία μπαταρίας.");

            if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE)
                logError("Μπαταρία ΚΑΤΕΣΤΡΑΜΜΕΝΗ — αλλαγή άμεσα.");
            else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT)
                logError("Μπαταρία σε υπερθέρμανση!");
            else
                logOk("Battery health OK.");

        } catch (Exception e) {
            logError("Σφάλμα battery: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 5 — NETWORK
     * ============================================================ */
    private void labNetwork() {
        logSection("LAB 5 — Δίκτυο");

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) {
                logError("ConnectivityManager λείπει.");
                logLine();
                return;
            }

            boolean online = false;
            boolean wifi = false;
            boolean mobile = false;

            if (Build.VERSION.SDK_INT >= 23) {
                android.net.Network n = cm.getActiveNetwork();
                NetworkCapabilities nc = cm.getNetworkCapabilities(n);
                if (nc != null) {
                    online = nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    wifi = nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    mobile = nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            } else {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    online = true;
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI) wifi = true;
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) mobile = true;
                }
            }

            if (!online) logError("Καμία σύνδεση Internet.");
            else {
                if (wifi) logOk("WiFi ενεργό.");
                if (mobile) logOk("Mobile Data ενεργό.");
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
                    (android.net.wifi.WifiManager) getApplicationContext()
                            .getSystemService(WIFI_SERVICE);

            if (wm == null || !wm.isWifiEnabled()) {
                logWarn("WiFi κλειστό ή μη διαθέσιμο.");
                logLine();
                return;
            }

            int rssi = -100;
            try {
                rssi = wm.getConnectionInfo().getRssi();
            } catch (Exception ignored) {}

            logInfo("WiFi RSSI: " + rssi + " dBm");

            if (rssi > -60) logOk("Πολύ καλή λήψη.");
            else if (rssi > -75) logWarn("Μέτρια λήψη.");
            else logError("Κακή λήψη WiFi (< -75 dBm).");

        } catch (Exception e) {
            logError("WiFi error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 7 — SENSORS
     * ============================================================ */
    private void labSensors() {
        logSection("LAB 7 — Αισθητήρες");

        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sm == null) {
                logError("SensorManager λείπει.");
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
            if (type == Sensor.TYPE_ACCELEROMETER ||
                type == Sensor.TYPE_PROXIMITY)
                logError("Λείπει " + name + " — πιθανή βλάβη.");
            else
                logWarn(name + " δεν υπάρχει.");
        } else {
            logOk(name + " OK.");
        }
    }

    /* ============================================================
     * LAB 8 — DISPLAY
     * ============================================================ */
    private void labDisplay() {
        logSection("LAB 8 — Οθόνη");

        try {
            DisplayMetrics dm = new DisplayMetrics();

            if (Build.VERSION.SDK_INT >= 30) {
                Display disp = getDisplay();
                if (disp != null) disp.getRealMetrics(dm);
                else {
                    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                    wm.getDefaultDisplay().getMetrics(dm);
                }
            } else {
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(dm);
            }

            int w = dm.widthPixels;
            int h = dm.heightPixels;

            logInfo("Ανάλυση: " + w + " x " + h);

            if (Math.min(w, h) < 720)
                logWarn("Χαμηλή ανάλυση.");
            else
                logOk("Display OK.");

        } catch (Exception e) {
            logError("Display error: " + e.getMessage());
        }

        logLine();
    }

    /* ============================================================
     * LAB 9 — THERMAL
     * ============================================================ */
    private void labThermal() {
        logSection("LAB 9 — Θερμικά");

        if (Build.VERSION.SDK_INT >= 29) {
            try {
                HardwarePropertiesManager hpm =
                        (HardwarePropertiesManager) getSystemService(HARDWARE_PROPERTIES_SERVICE);

                if (hpm != null) {
                    float[] temps = hpm.getDeviceTemperatures(
                            HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
                            HardwarePropertiesManager.TEMPERATURE_CURRENT
                    );

                    if (temps != null && temps.length > 0) {
                        float t = temps[0];
                        logInfo("CPU Temp: " + t + "°C");

                        if (t > 80) logError("CPU ΠΟΛΥ ΖΕΣΤΟ — throttling.");
                        else if (t > 70) logWarn("CPU ζεστό — πιθανό throttling.");
                        else logOk("CPU θερμοκρασία OK.");
                    } else {
                        logWarn("Δεν δόθηκαν CPU θερμοκρασίες.");
                    }
                } else logWarn("HardwarePropertiesManager όχι διαθέσιμο.");

            } catch (Exception e) {
                logError("Thermal error: " + e.getMessage());
            }

        } else {
            logWarn("Thermal API δεν υποστηρίζεται (API < 29).");
        }

        logLine();
    }

    /* ============================================================
     * LAB 10 — SYSTEM HEALTH / TELEPHONY
     * ============================================================ */
    private void labSystemHealth() {
        logSection("LAB 10 — Σύστημα / Τηλεφωνία");

        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            if (tm != null) {
                String net = tm.getNetworkOperatorName();
                String sim = tm.getSimOperatorName();

                logInfo("Network operator: " + (net == null ? "N/A" : net));
                logInfo("SIM operator: " + (sim == null ? "N/A" : sim));
            } else {
                logWarn("TelephonyManager δεν υπάρχει (ίσως tablet).");
            }

        } catch (SecurityException se) {
            logWarn("Δεν έχω δικαίωμα Telephony info.");
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

            logInfo("Disponível RAM: " + readable(avail) + " (" + pct + "% free)");

            if (pct < 10) logError("ΠΟΛΥ χαμηλή RAM — κολλήματα σίγουρα.");
            else if (pct < 20) logWarn("Χαμηλή RAM — restart ίσως βοηθήσει.");
            else logOk("RAM live OK.");

        } catch (Exception e) {
            logError("System RAM error: " + e.getMessage());
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

    private long gb(int g) { return g * 1024L * 1024L * 1024L; }
}
