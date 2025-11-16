package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.Locale;

// ============================================================
// GEL Phone Diagnosis — PerformanceDiagnosticsActivity
// "Πανεπιστημιακό νοσοκομείο" διάγνωση συσκευής
// ============================================================
public class PerformanceDiagnosticsActivity extends AppCompatActivity {

    private TextView txtDiag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Δημιουργία layout ΠΡΟΓΡΑΜΜΑΤΙΚΑ (χωρίς XML)
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        txtDiag = new TextView(this);
        txtDiag.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        txtDiag.setTextSize(14f);
        txtDiag.setPadding(24, 24, 24, 24);
        txtDiag.setTextIsSelectable(true);
        txtDiag.setTypeface(android.graphics.Typeface.MONOSPACE);

        scrollView.addView(txtDiag);

        setContentView(scrollView);

        runDiagnostics();
    }

    // ============================================================
    // ΚΥΡΙΑ ΡΟΗ ΔΙΑΓΝΩΣΗΣ
    // ============================================================
    private void runDiagnostics() {
        new Thread(() -> {
            StringBuilder sb = new StringBuilder();

            sb.append("📋 GEL Phone Diagnosis\n");
            sb.append("Date: ").append(DateFormat.format("yyyy-MM-dd HH:mm", new Date())).append("\n");
            sb.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
            sb.append("Android: ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
            sb.append("Board: ").append(Build.BOARD).append("\n");
            sb.append("Hardware: ").append(Build.HARDWARE).append("\n");
            sb.append("Fingerprint: ").append(Build.FINGERPRINT).append("\n\n");

            sb.append("====================================================\n");
            sb.append("1) CPU / SOC\n");
            sb.append("====================================================\n");
            sb.append(cpuReport()).append("\n\n");

            sb.append("====================================================\n");
            sb.append("2) RAM / Μνήμη\n");
            sb.append("====================================================\n");
            sb.append(memoryReport()).append("\n\n");

            sb.append("====================================================\n");
            sb.append("3) Αποθήκευση (Storage)\n");
            sb.append("====================================================\n");
            sb.append(storageReport()).append("\n\n");

            sb.append("====================================================\n");
            sb.append("4) Μπαταρία\n");
            sb.append("====================================================\n");
            sb.append(batteryReport()).append("\n\n");

            sb.append("====================================================\n");
            sb.append("5) Συμπέρασμα GEL (Auto Diagnosis)\n");
            sb.append("====================================================\n");
            sb.append(autoDiagnosis());

            runOnUiThread(() -> txtDiag.setText(sb.toString()));
        }).start();
    }

    // ============================================================
    // CPU REPORT
    // ============================================================
    private String cpuReport() {
        StringBuilder sb = new StringBuilder();
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            sb.append("CPU cores: ").append(cores).append("\n");

            // Συχνότητα (CPU0 όπου γίνεται)
            String maxFreq = readFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            String minFreq = readFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
            String curFreq = readFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");

            if (maxFreq != null) sb.append("Max freq (cpu0): ").append(formatKHz(maxFreq)).append("\n");
            if (minFreq != null) sb.append("Min freq (cpu0): ").append(formatKHz(minFreq)).append("\n");
            if (curFreq != null) sb.append("Cur freq (cpu0): ").append(formatKHz(curFreq)).append("\n");

            // /proc/cpuinfo (μονάχα τα βασικά)
            String cpuInfo = readCpuInfoModel();
            if (cpuInfo != null) {
                sb.append("CPU model: ").append(cpuInfo).append("\n");
            }

        } catch (Exception e) {
            sb.append("CPU report error: ").append(e.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private String readCpuInfoModel() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase(Locale.US);
                if (line.startsWith("hardware") || line.startsWith("model name")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) return parts[1].trim();
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (br != null) try { br.close(); } catch (Exception ignored) {}
        }
        return null;
    }

    private String formatKHz(String raw) {
        try {
            long khz = Long.parseLong(raw.trim());
            long mhz = khz / 1000;
            long ghzInt = mhz / 1000;
            double ghz = mhz / 1000.0;
            if (ghzInt > 0) {
                return String.format(Locale.US, "%.2f GHz (%d MHz)", ghz, mhz);
            } else {
                return mhz + " MHz";
            }
        } catch (Exception e) {
            return raw.trim() + " kHz";
        }
    }

    private String readFirstLine(String path) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            return br.readLine();
        } catch (Exception ignored) {
            return null;
        } finally {
            if (br != null) try { br.close(); } catch (Exception ignored) {}
        }
    }

    // ============================================================
    // RAM REPORT
    // ============================================================
    private String memoryReport() {
        StringBuilder sb = new StringBuilder();
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(mi);

                long total = mi.totalMem;
                long avail = mi.availMem;
                long used = total - avail;
                double usedPct = total > 0 ? (used * 100.0 / total) : 0.0;

                sb.append("Total RAM : ").append(human(total)).append("\n");
                sb.append("Used RAM  : ").append(human(used))
                  .append("  (").append(String.format(Locale.US, "%.1f", usedPct)).append("%)").append("\n");
                sb.append("Free RAM  : ").append(human(avail)).append("\n");
                sb.append("Low memory flag: ").append(mi.lowMemory).append("\n");
                sb.append("System low mem threshold: ").append(human(mi.threshold)).append("\n");

                if (mi.lowMemory || usedPct > 85.0) {
                    sb.append("⚠ Suspicious: Very high RAM usage (").append(String.format(Locale.US, "%.1f", usedPct)).append("%)\n");
                } else {
                    sb.append("✓ RAM status: OK\n");
                }
            } else {
                sb.append("ActivityManager not available.\n");
            }
        } catch (Exception e) {
            sb.append("Memory report error: ").append(e.getMessage()).append("\n");
        }
        return sb.toString();
    }

    // ============================================================
    // STORAGE REPORT
    // ============================================================
    private String storageReport() {
        StringBuilder sb = new StringBuilder();
        try {
            // Internal storage
            File dataDir = Environment.getDataDirectory();
            StatFs stat = new StatFs(dataDir.getAbsolutePath());

            long blockSize, totalBlocks, availBlocks;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
                availBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
                availBlocks = stat.getAvailableBlocks();
            }

            long total = totalBlocks * blockSize;
            long free = availBlocks * blockSize;
            long used = total - free;
            double usedPct = total > 0 ? (used * 100.0 / total) : 0.0;

            sb.append("Internal storage (data):\n");
            sb.append("  Total : ").append(human(total)).append("\n");
            sb.append("  Used  : ").append(human(used))
              .append("  (").append(String.format(Locale.US, "%.1f", usedPct)).append("%)\n");
            sb.append("  Free  : ").append(human(free)).append("\n");

            if (usedPct > 90.0) {
                sb.append("⚠ Suspicious: Very low free space (<10%).\n");
            } else if (usedPct > 80.0) {
                sb.append("ℹ Suggestion: Clean junk / media (storage >80%).\n");
            } else {
                sb.append("✓ Storage status: OK\n");
            }

            // External (if exists)
            File external = getExternalFilesDir(null);
            if (external != null) {
                try {
                    StatFs statExt = new StatFs(external.getAbsolutePath());
                    long bs2, tb2, ab2;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bs2 = statExt.getBlockSizeLong();
                        tb2 = statExt.getBlockCountLong();
                        ab2 = statExt.getAvailableBlocksLong();
                    } else {
                        bs2 = statExt.getBlockSize();
                        tb2 = statExt.getBlockCount();
                        ab2 = statExt.getAvailableBlocks();
                    }
                    long total2 = tb2 * bs2;
                    long free2 = ab2 * bs2;
                    long used2 = total2 - free2;
                    double usedPct2 = total2 > 0 ? (used2 * 100.0 / total2) : 0.0;

                    sb.append("\nExternal / SD (app area):\n");
                    sb.append("  Total : ").append(human(total2)).append("\n");
                    sb.append("  Used  : ").append(human(used2))
                      .append("  (").append(String.format(Locale.US, "%.1f", usedPct2)).append("%)\n");
                    sb.append("  Free  : ").append(human(free2)).append("\n");
                } catch (Exception ignore) {
                    sb.append("\nExternal storage: not fully accessible.\n");
                }
            }

        } catch (Exception e) {
            sb.append("Storage report error: ").append(e.getMessage()).append("\n");
        }
        return sb.toString();
    }

    // ============================================================
    // BATTERY REPORT
    // ============================================================
    private String batteryReport() {
        StringBuilder sb = new StringBuilder();
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = registerReceiver(null, ifilter);

            if (batteryStatus == null) {
                sb.append("Battery info not available.\n");
                return sb.toString();
            }

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

            float pct = (level >= 0 && scale > 0) ? (level * 100f / scale) : -1f;

            sb.append("Level     : ");
            if (pct >= 0) sb.append(String.format(Locale.US, "%.1f", pct)).append("% (").append(level).append("/").append(scale).append(")\n");
            else sb.append("N/A\n");

            sb.append("Status    : ").append(batteryStatusToString(status)).append("\n");
            sb.append("Plugged   : ").append(batteryPluggedToString(plugged)).append("\n");
            sb.append("Health    : ").append(batteryHealthToString(health)).append("\n");

            if (temp > 0) {
                float c = temp / 10f;
                sb.append("Temperature: ").append(String.format(Locale.US, "%.1f °C", c)).append("\n");
                if (c >= 45.0f) {
                    sb.append("⚠ HIGH temperature — Possible thermal issues.\n");
                } else if (c >= 40.0f) {
                    sb.append("ℹ Warm battery — check heavy apps / charging.\n");
                }
            }

            if (voltage > 0) {
                sb.append("Voltage   : ").append(voltage / 1000.0f).append(" V\n");
            }

            if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                health == BatteryManager.BATTERY_HEALTH_OVERHEAT ||
                health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE ||
                health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                sb.append("⚠ Suspicious: Battery health is not good — πιθανή φθορά / βλάβη.\n");
            } else {
                sb.append("✓ Battery health: OK (σύμφωνα με Android).\n");
            }

        } catch (Exception e) {
            sb.append("Battery report error: ").append(e.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private String batteryStatusToString(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING: return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING: return "Discharging";
            case BatteryManager.BATTERY_STATUS_FULL: return "Full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING: return "Not charging";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default: return "Unknown";
        }
    }

    private String batteryPluggedToString(int plugged) {
        if (plugged == BatteryManager.BATTERY_PLUGGED_AC) return "AC";
        if (plugged == BatteryManager.BATTERY_PLUGGED_USB) return "USB";
        if (plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) return "Wireless";
        if (plugged == 0) return "Not plugged";
        return "Other";
    }

    private String batteryHealthToString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD: return "Cold";
            case BatteryManager.BATTERY_HEALTH_DEAD: return "Dead";
            case BatteryManager.BATTERY_HEALTH_GOOD: return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return "Overheat";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return "Over-voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: return "Unspecified failure";
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default: return "Unknown";
        }
    }

    // ============================================================
    // AUTO DIAGNOSIS (ΣΥΜΠΕΡΑΣΜΑΤΑ ΓΙΑ ΤΕΧΝΙΚΟ)
    // ============================================================
    private String autoDiagnosis() {
        StringBuilder sb = new StringBuilder();

        // Εδώ δεν έχουμε όλα τα raw δεδομένα (είναι πάνω στη στιγμή),
        // οπότε δίνουμε γενικές οδηγίες για τεχνικό.

        sb.append("• Αν η RAM είναι συνεχώς >85% και η συσκευή είναι αργή:\n");
        sb.append("  → Ύποπτη ύπαρξη βαριάς εφαρμογής / διαρροής μνήμης.\n");
        sb.append("  → Έλεγχος: uninstall/disable άγνωστες εφαρμογές,\n");
        sb.append("    δοκιμή σε Safe Mode, έλεγχος για malware.\n\n");

        sb.append("• Αν η εσωτερική αποθήκευση έχει <10% ελεύθερο χώρο:\n");
        sb.append("  → Πιθανές καθυστερήσεις / κόλλημα σε updates.\n");
        sb.append("  → Έλεγχος: μεγάλα βίντεο/φωτογραφίες, cache social apps,\n");
        sb.append("    κλωνοποιημένες εφαρμογές, WhatsApp backups κ.λπ.\n\n");

        sb.append("• Αν η θερμοκρασία μπαταρίας συχνά >45°C:\n");
        sb.append("  → Πιθανό thermal throttling, φθορά μπαταρίας ή CPU stress.\n");
        sb.append("  → Έλεγχος: φορτιστής/καλώδιο, χρήση κατά τη φόρτιση,\n");
        sb.append("    βαριά games, φουσκωμένη μπαταρία, βραχυκύκλωμα.\n\n");

        sb.append("• Αν το Battery Health δεν είναι GOOD:\n");
        sb.append("  → Πιθανή ανάγκη αντικατάστασης μπαταρίας.\n");
        sb.append("  → Έλεγχος: γρήγορη πτώση %, τυχαία shutdowns, boot-loops.\n\n");

        sb.append("• Αν Storage / RAM είναι ΟΚ αλλά η συσκευή κολλάει:\n");
        sb.append("  → Ύποπτο πρόβλημα firmware ή κατεστραμμένος αποθηκευτικός χώρος.\n");
        sb.append("  → Έλεγχος: factory reset (με πλήρες backup),\n");
        sb.append("    official ROM reflash, έλεγχος eMMC/UFS με εργαλεία του κατασκευαστή.\n\n");

        sb.append("• Αν παρουσιαστούν συχνά freezes + επανεκκινήσεις:\n");
        sb.append("  → Πιθανό hardware (RAM chips, PMIC, motherboard).\n");
        sb.append("  → Έλεγχος με service menu του κατασκευαστή + θερμοκρασίες/τάσεις.\n\n");

        sb.append("Συνοπτικά:\n");
        sb.append("  - Χρησιμοποίησε τα παραπάνω νούμερα σαν screening.\n");
        sb.append("  - Συνδύασέ τα με δικά σου service tools (JTAG, vendor tools,\n");
        sb.append("    full logs, baseband, modem, sensors, touchscreen tests κ.λπ.).\n");

        return sb.toString();
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private String human(long bytes) {
        return Formatter.formatFileSize(this, bytes);
    }
}
```0
