package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.StatFs;

import java.io.File;
import java.util.List;
import java.util.Locale;

// ============================================================
// GEL Phone Diagnosis — PerformanceDiagnosticsActivity
// Full "Service Lab" diagnostic, universal για όλες τις συσκευές
// ============================================================
public class PerformanceDiagnosticsActivity extends AppCompatActivity {

    private TextView txtDiag;
    private ScrollView scroll;
    private Handler ui;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Απλό generic layout: full-screen TextView με Scroll
        scroll = new ScrollView(this);
        txtDiag = new TextView(this);
        txtDiag.setTextSize(14f);
        txtDiag.setTextColor(0xFFE0E0E0); // light grey
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

    // ============================================================
    // LOG HELPERS (με κόκκινα errors)
    // ============================================================
    private void appendHtmlLine(final String html) {
        ui.post(() -> {
            CharSequence current = txtDiag.getText();
            String add = Html.fromHtml(html + "<br>") + "";
            txtDiag.setText(current + add);

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }

    private void logTitle(String msg) {
        appendHtmlLine("<b>" + escape(msg) + "</b>");
    }

    private void logSection(String msg) {
        appendHtmlLine("<br><b>▌ " + escape(msg) + "</b>");
    }

    private void logInfo(String msg) {
        appendHtmlLine("ℹ️ " + escape(msg));
    }

    private void logOk(String msg) {
        appendHtmlLine("<font color='#88FF88'>✅ " + escape(msg) + "</font>");
    }

    private void logError(String msg) {
        appendHtmlLine("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
    }

    private void logWarn(String msg) {
        appendHtmlLine("<font color='#FFD966'>⚠️ " + escape(msg) + "</font>");
    }

    private void logLine() {
        appendHtmlLine("<font color='#666666'>────────────────────────────────</font>");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    // ============================================================
    // MAIN DIAG FLOW
    // ============================================================
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
            logOk("Διάγνωση ολοκληρώθηκε. Χρησιμοποίησε τα errors (κόκκινα) για service report.");
        }).start();
    }

    // ============================================================
    // LAB 1 — Hardware / OS Info
    // ============================================================
    private void labHardware() {
        logSection("LAB 1 — Hardware / OS");

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String device = Build.DEVICE;
        String product = Build.PRODUCT;
        String board = Build.BOARD;

        logInfo("Κατασκευαστής: " + manufacturer);
        logInfo("Μοντέλο: " + model);
        logInfo("Συσκευή: " + device);
        logInfo("Product: " + product);
        logInfo("Board: " + board);
        logInfo("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");

        if (Build.VERSION.SDK_INT < 26) {
            logWarn("Παλιό Android (< 8.0) — πιθανή γενική αστάθεια / έλλειψη updates.");
        } else if (Build.VERSION.SDK_INT < 30) {
            logWarn("Android < 11 — ίσως δεν υπάρχουν τα τελευταία security patches.");
        } else {
            logOk("OS level: OK για σύγχρονη χρήση.");
        }

        logLine();
    }

    // ============================================================
    // LAB 2 — CPU / RAM
    // ============================================================
    private void labCpuRam() {
        logSection("LAB 2 — CPU / RAM");

        int cores = Runtime.getRuntime().availableProcessors();
        long maxMem = Runtime.getRuntime().maxMemory();     // app heap
        long totalMem = getTotalRam();                      // device RAM
        long usedHeap = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        logInfo("CPU Cores: " + cores);
        logInfo("App heap used: " + readable(usedHeap) + " / " + readable(maxMem));
        if (totalMem > 0) {
            logInfo("Συνολική RAM συσκευής: " + readable(totalMem));
        }

        if (totalMem > 0 && totalMem < gb(2)) {
            logError("Πολύ λίγη RAM (< 2 GB) — η συσκευή θα κολλάει με πολλές εφαρμογές.");
        } else if (totalMem > 0 && totalMem < gb(4)) {
            logWarn("RAM ~2–4 GB — οριακά για βαριά χρήση / πολλά apps.");
        } else if (totalMem > 0) {
            logOk("RAM capacity: Ικανοποιητική για καθημερινή χρήση.");
        }

        if (cores <= 4) {
            logWarn("CPU με ≤ 4 πυρήνες — περιορισμένη απόδοση σε βαριά tasks.");
        } else {
            logOk("CPU cores count: OK.");
        }

        logLine();
    }

    private long getTotalRam() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(mi);
                return mi.totalMem;
            }
        } catch (Exception ignored) {}
        return 0;
    }

    // ============================================================
    // LAB 3 — Storage
    // ============================================================
    private void labStorage() {
        logSection("LAB 3 — Storage");

        try {
            File dataDir = Environment.getDataDirectory();
            StatFs statFs = new StatFs(dataDir.getAbsolutePath());

            long total, free;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                total = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
                free  = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
            } else {
                total = (long) statFs.getBlockCount() * statFs.getBlockSize();
                free  = (long) statFs.getAvailableBlocks() * statFs.getBlockSize();
            }

            long used = total - free;
            int percentFree = (int) ((free * 100L) / total);

            logInfo("Εσωτερική αποθήκευση: " + readable(used) + " / " + readable(total));
            logInfo("Ελεύθερος χώρος: " + percentFree + "%");

            if (percentFree < 10) {
                logError("Πολύ λίγο Storage (< 10%) — υψηλός κίνδυνος κολλημάτων / σφαλμάτων.");
            } else if (percentFree < 20) {
                logWarn("Ελεύθερο Storage < 20% — προτείνεται καθαρισμός.");
            } else {
                logOk("Storage: OK.");
            }
        } catch (Exception e) {
            logError("Αδυναμία ανάγνωσης Storage: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // LAB 4 — Battery / Charging
    // ============================================================
    private void labBattery() {
        logSection("LAB 4 — Μπαταρία / Φόρτιση");

        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);

            if (batteryStatus == null) {
                logError("Δεν μπόρεσα να διαβάσω την κατάσταση μπαταρίας.");
                logLine();
                return;
            }

            int level  = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale  = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int temp10 = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1); // σε δέκατα °C

            float pct  = (level >= 0 && scale > 0) ? (100f * level / scale) : -1f;
            float temp = (temp10 > 0) ? (temp10 / 10f) : -1f;

            if (pct >= 0) logInfo(String.format(Locale.US, "Φόρτιση: %.1f%%", pct));
            if (temp > 0) logInfo(String.format(Locale.US, "Θερμοκρασία μπαταρίας: %.1f°C", temp));

            String healthStr;
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_GOOD:        healthStr = "GOOD"; break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:    healthStr = "OVERHEAT"; break;
                case BatteryManager.BATTERY_HEALTH_DEAD:        healthStr = "DEAD"; break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:healthStr = "OVER_VOLTAGE"; break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: healthStr = "UNSPECIFIED_FAILURE"; break;
                default: healthStr = "UNKNOWN"; break;
            }
            logInfo("Κατάσταση υγείας: " + healthStr);

            if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                logError("Η μπαταρία φαίνεται ΚΑΤΕΣΤΡΑΜΜΕΝΗ — πρότεινε αλλαγή μπαταρίας.");
            } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                logError("Υπερθέρμανση μπαταρίας — πιθανός κίνδυνος, έλεγχος hardware.");
            } else {
                logOk("Battery health: ΟΚ (σύμφωνα με τα Android flags).");
            }

            if (temp > 45f) {
                logError("Υψηλή θερμοκρασία μπαταρίας (> 45°C) — έλεγχος φορτιστή / πλακέτας.");
            } else if (temp > 38f) {
                logWarn("Ζεστή μπαταρία (38–45°C) — πιθανή έντονη χρήση ή θερμικό θέμα.");
            }

        } catch (Exception e) {
            logError("Σφάλμα στη διάγνωση μπαταρίας: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // LAB 5 — Network Connectivity
    // ============================================================
    private void labNetwork() {
        logSection("LAB 5 — Δίκτυο / Internet");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            logError("Δεν βρέθηκε ConnectivityManager — πιθανό σοβαρό πρόβλημα συστήματος.");
            logLine();
            return;
        }

        boolean hasInternet = false;
        boolean wifi = false;
        boolean mobile = false;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.net.Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                    if (caps != null) {
                        hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                        mobile = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    }
                }
            } else {
                @SuppressWarnings("deprecation")
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    hasInternet = true;
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI) wifi = true;
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) mobile = true;
                }
            }
        } catch (Exception e) {
            logError("Σφάλμα στον έλεγχο δικτύου: " + e.getMessage());
        }

        if (!hasInternet) {
            logError("Δεν φαίνεται ενεργή σύνδεση Internet — έλεγξε WiFi / Data / κεραίες.");
        } else {
            if (wifi) logOk("WiFi σύνδεση ενεργή.");
            if (mobile) logOk("Mobile data σύνδεση ενεργή.");
        }

        logLine();
    }

    // ============================================================
    // LAB 6 — WiFi Signal (basic)
    // ============================================================
    private void labWifiSignal() {
        logSection("LAB 6 — WiFi Signal (Basic)");

        try {
            android.net.wifi.WifiManager wm =
                    (android.net.wifi.WifiManager) getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);

            if (wm == null || !wm.isWifiEnabled()) {
                logWarn("WiFi απενεργοποιημένο ή μη διαθέσιμο.");
                logLine();
                return;
            }

            @SuppressWarnings("deprecation")
            int rssi = wm.getConnectionInfo().getRssi(); // dBm

            logInfo("WiFi RSSI: " + rssi + " dBm");

            if (rssi == 0) {
                logWarn("Δεν είμαι σίγουρος για τη λήψη WiFi (RSSI=0).");
            } else if (rssi > -60) {
                logOk("Πολύ καλή λήψη WiFi.");
            } else if (rssi > -75) {
                logWarn("Μέτρια λήψη WiFi (πιθανά disconnects).");
            } else {
                logError("Κακή λήψη WiFi (< -75 dBm) — πιθανό θέμα router / κεραίας / τοποθεσίας.");
            }

        } catch (Exception e) {
            logError("Σφάλμα στη διάγνωση WiFi: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // LAB 7 — Sensors
    // ============================================================
    private void labSensors() {
        logSection("LAB 7 — Αισθητήρες");

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm == null) {
                logError("Δεν βρέθηκε SensorManager — πιθανό σοβαρό πρόβλημα framework.");
                logLine();
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Σύνολο αισθητήρων: " + (all == null ? 0 : all.size()));

            boolean hasAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
            boolean hasGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
            boolean hasMag  = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
            boolean hasLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null;
            boolean hasProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null;

            if (!hasAccel) logError("Λείπει accelerometer — πιθανό θέμα πλακέτας / βασικών αισθητήρων.");
            if (!hasGyro)  logWarn("Δεν υπάρχει gyroscope — περιορισμένα motion features.");
            if (!hasMag)   logWarn("Δεν υπάρχει magnetometer — προβλήματα σε πυξίδα / navigation.");
            if (!hasLight) logWarn("Δεν υπάρχει light sensor — auto-brightness δεν θα λειτουργεί σωστά.");
            if (!hasProx)  logError("Δεν υπάρχει proximity — πιθανές βλάβες σε κλείσιμο οθόνης σε κλήσεις.");

            if (hasAccel && hasGyro && hasProx) {
                logOk("Βασικοί αισθητήρες (accelerometer/gyro/proximity) υπάρχουν.");
            }

        } catch (Exception e) {
            logError("Σφάλμα στη διάγνωση αισθητήρων: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // LAB 8 — Display / Screen
    // ============================================================
    private void labDisplay() {
        logSection("LAB 8 — Οθόνη");

        try {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) {
                logError("Δεν βρέθηκε WindowManager για την οθόνη.");
                logLine();
                return;
            }

            DisplayMetrics dm = new DisplayMetrics();
            if (Build.VERSION.SDK_INT >= 30) {
                getDisplay().getRealMetrics(dm);
            } else {
                //noinspection deprecation
                wm.getDefaultDisplay().getMetrics(dm);
            }

            int width = dm.widthPixels;
            int height = dm.heightPixels;
            float density = dm.density;
            float dpiX = dm.xdpi;
            float dpiY = dm.ydpi;

            logInfo("Ανάλυση: " + width + " x " + height + " px");
            logInfo(String.format(Locale.US, "Density: %.2f  |  DPI: %.1f x %.1f", density, dpiX, dpiY));

            if (Math.min(width, height) < 720) {
                logWarn("Χαμηλή ανάλυση οθόνης — ίσως «θολά» γράμματα / icons.");
            } else {
                logOk("Display resolution: OK για καθημερινή χρήση.");
            }

        } catch (Exception e) {
            logError("Σφάλμα στη διάγνωση οθόνης: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // LAB 9 — Thermal / Throttling (Basic)
    // ============================================================
    private void labThermal() {
        logSection("LAB 9 — Θερμική Συμπεριφορά (Basic)");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                android.os.Temperature tempCpu = null;
                android.os.HardwarePropertiesManager hpm =
                        (android.os.HardwarePropertiesManager)
                                getSystemService(Context.HARDWARE_PROPERTIES_SERVICE);

                if (hpm != null) {
                    float[] cpuTemps = hpm.getDeviceTemperatures(
                            android.os.HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
                            android.os.HardwarePropertiesManager.TEMPERATURE_CURRENT);

                    if (cpuTemps != null && cpuTemps.length > 0) {
                        float t = cpuTemps[0];
                        logInfo(String.format(Locale.US, "Θερμοκρασία CPU: %.1f°C", t));

                        if (t > 80f) {
                            logError("Πολύ υψηλή CPU θερμοκρασία (> 80°C) — πιθανή βλάβη ψύξης / SoC.");
                        } else if (t > 70f) {
                            logWarn("Ψηλή CPU θερμοκρασία (70–80°C) — throttling / κολλήματα.");
                        } else {
                            logOk("CPU temperature: εντός φυσιολογικών ορίων.");
                        }
                    } else {
                        logWarn("Δεν διατέθηκαν θερμοκρασίες CPU από το σύστημα.");
                    }
                } else {
                    logWarn("Δεν διατέθηκε HardwarePropertiesManager — περιορισμένη thermal διάγνωση.");
                }

            } catch (Throwable t) {
                logError("Σφάλμα thermal check: " + t.getMessage());
            }
        } else {
            logWarn("Thermal APIs δεν υποστηρίζονται (API < 29).");
        }

        logLine();
    }

    // ============================================================
    // LAB 10 — System Health / Telephony
    // ============================================================
    private void labSystemHealth() {
        logSection("LAB 10 — System / Telephony");

        // Telephony basic info
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String opName = tm.getNetworkOperatorName();
                String simOp = tm.getSimOperatorName();

                logInfo("Network operator: " + (opName == null || opName.isEmpty() ? "N/A" : opName));
                logInfo("SIM operator: " + (simOp == null || simOp.isEmpty() ? "N/A" : simOp));
            } else {
                logWarn("Δεν βρέθηκε TelephonyManager (WiFi-only συσκευή ή σοβαρό σφάλμα).");
            }
        } catch (SecurityException se) {
            logWarn("Δεν έχω δικαίωμα για πλήρη telephony info (OK για τη διάγνωση).");
        } catch (Exception e) {
            logError("Σφάλμα Telephony: " + e.getMessage());
        }

        // Basic check for low system resources
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(mi);

                long avail = mi.availMem;
                long total = mi.totalMem;
                int pctFree = (int) ((avail * 100L) / total);

                logInfo("Διαθέσιμη RAM τώρα: " + readable(avail) +
                        " (" + pctFree + "% ελεύθερα)");

                if (pctFree < 10) {
                    logError("ΠΟΛΥ χαμηλή διαθέσιμη RAM (< 10%) — πρότεινε κλείσιμο apps / πιθανή επανεκκίνηση.");
                } else if (pctFree < 20) {
                    logWarn("Χαμηλή διαθέσιμη RAM (< 20%) — οριακή κατάσταση.");
                } else {
                    logOk("Live RAM status: OK.");
                }
            }
        } catch (Exception e) {
            logError("Σφάλμα RAM live check: " + e.getMessage());
        }

        logLine();
    }

    // ============================================================
    // UTILITIES
    // ============================================================
    private String readable(long bytes) {
        if (bytes <= 0) return "0 B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.2f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.2f MB", mb);
        float gb = mb / 1024f;
        return String.format(Locale.US, "%.2f GB", gb);
    }

    private long gb(int g) {
        return g * 1024L * 1024L * 1024L;
    }
}
