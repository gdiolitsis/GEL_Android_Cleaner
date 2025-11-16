package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class GELDiagnostics {

    // ====================================================================
    // PUBLIC ENTRY
    // ====================================================================
    public static void runFullDiagnostics(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "🔬 GEL PHONE DIAGNOSTICS STARTED");
        info(cb, "--------------------------------------");

        rootLab(ctx, cb);
        storageLab(ctx, cb);
        memoryLab(ctx, cb);
        cpuLab(cb);
        batteryLab(ctx, cb);
        networkLab(ctx, cb);

        info(cb, "--------------------------------------");
        ok(cb, "✅ Diagnostics finished.");
    }

    // ====================================================================
    // ROOT LAB
    // ====================================================================
    private static void rootLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "🧪 ROOT LAB");

        boolean rooted = isRooted();

        if (rooted) {
            err(cb, "⚠ Η συσκευή φαίνεται ROOTED (test-keys / su binary).");
            info(cb, "   ➤ Από την πλευρά service αυτό είναι ΟΚ, αλλά ενημέρωσε τον πελάτη.");
        } else {
            ok(cb, "✔ Η συσκευή φαίνεται UNROOTED (τυπική για Play Store).");
        }

        // PRO LAB μόνο αν είναι rooted
        if (rooted) {
            // Cycle count (αν υπάρχει)
            String[] cycleCandidates = {
                    "/sys/class/power_supply/battery/cycle_count",
                    "/sys/class/power_supply/bms/cycle_count"
            };
            boolean cycleFound = false;
            for (String p : cycleCandidates) {
                String line = safeReadFirstLine(p);
                if (line != null && !line.isEmpty()) {
                    info(cb, "   🔍 Battery cycle_count: " + line.trim());
                    cycleFound = true;
                    break;
                }
            }
            if (!cycleFound) {
                info(cb, "   ℹ Δεν βρέθηκε cycle_count (εξαρτάται από OEM).");
            }
        }
    }

    // ====================================================================
    // STORAGE LAB
    // ====================================================================
    private static void storageLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n💾 STORAGE LAB");

        File dataDir = ctx.getFilesDir();
        if (dataDir == null) {
            err(cb, "❌ Δεν μπορώ να διαβάσω internal storage dir.");
            return;
        }

        long total = dataDir.getTotalSpace();
        long free  = dataDir.getFreeSpace();
        long used  = total - free;

        String totalStr = human(total);
        String usedStr  = human(used);
        String freeStr  = human(free);

        info(cb, String.format(Locale.US,
                "   Internal: used %s / %s (free %s)", usedStr, totalStr, freeStr));

        double freePercent = (total > 0) ? (free * 100.0 / total) : 0;

        if (freePercent < 5.0) {
            err(cb, String.format(Locale.US,
                    "❌ Ελεύθερος χώρος %.1f%% — Κρίσιμα χαμηλός. Συνιστάται άμεσος καθαρισμός / backup.",
                    freePercent));
        } else if (freePercent < 10.0) {
            err(cb, String.format(Locale.US,
                    "⚠ Ελεύθερος χώρος %.1f%% — Χαμηλός, πιθανές επιβραδύνσεις & προβλήματα update.",
                    freePercent));
        } else {
            ok(cb, String.format(Locale.US,
                    "✔ Ελεύθερος χώρος %.1f%% — αποδεκτός για καθημερινή χρήση.",
                    freePercent));
        }
    }

    // ====================================================================
    // MEMORY LAB
    // ====================================================================
    private static void memoryLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🧠 MEMORY LAB");

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            err(cb, "❌ ActivityManager = null (δεν μπορώ να ελέγξω RAM).");
            return;
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long total = mi.totalMem;
        long avail = mi.availMem;
        long used  = total - avail;

        double freePercent = (total > 0) ? (avail * 100.0 / total) : 0;

        info(cb, String.format(Locale.US,
                "   RAM used: %s / %s (free %s)",
                human(used), human(total), human(avail)));

        if (freePercent < 5.0) {
            err(cb, String.format(Locale.US,
                    "❌ Ελεύθερη RAM %.1f%% — πολύ χαμηλή, έντονα κολλήματα / κλεισίματα.", freePercent));
        } else if (freePercent < 15.0) {
            err(cb, String.format(Locale.US,
                    "⚠ Ελεύθερη RAM %.1f%% — πιθανές επιβραδύνσεις, προτείνεται κλείσιμο apps.", freePercent));
        } else {
            ok(cb, String.format(Locale.US,
                    "✔ Ελεύθερη RAM %.1f%% — αποδεκτή.", freePercent));
        }

        if (mi.lowMemory) {
            err(cb, "❌ Το σύστημα είναι σε LOW MEMORY mode (Android αρχίζει να σκοτώνει εφαρμογές).");
        }
    }

    // ====================================================================
    // CPU LAB
    // ====================================================================
    private static void cpuLab(GELCleaner.LogCallback cb) {
        info(cb, "\n🧮 CPU LAB");

        int cores = Runtime.getRuntime().availableProcessors();
        info(cb, "   CPU cores detected: " + cores);

        if (cores <= 4) {
            err(cb, "⚠ Λίγοι πυρήνες (≤4) για σύγχρονα workloads. Πιθανές καθυστερήσεις σε multitasking.");
        } else {
            ok(cb, "✔ Αρκετοί πυρήνες για καθημερινή χρήση.");
        }

        String maxFreq = safeReadFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (maxFreq != null) {
            try {
                long khz = Long.parseLong(maxFreq.trim());
                double ghz = khz / 1_000_000.0;
                info(cb, String.format(Locale.US, "   CPU0 max freq: %.2f GHz", ghz));
            } catch (Exception e) {
                info(cb, "   CPU0 max freq raw: " + maxFreq.trim());
            }
        } else {
            info(cb, "   CPU freq info: not available (OEM restricted).");
        }
    }

    // ====================================================================
    // BATTERY LAB
    // ====================================================================
    private static void batteryLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🔋 BATTERY LAB");

        IntentFilter ifilt = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batt = ctx.registerReceiver(null, ifilt);

        if (batt == null) {
            err(cb, "❌ Δεν μπόρεσα να διαβάσω battery intent.");
            return;
        }

        int level  = batt.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale  = batt.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batt.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int health = batt.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int temp   = batt.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1); // tenth of °C

        float pct = (scale > 0) ? (level * 100f / scale) : -1f;
        float celsius = (temp > 0) ? (temp / 10f) : -1f;

        info(cb, String.format(Locale.US,
                "   Battery level: %.1f%%   Temp: %.1f°C", pct, celsius));

        // Health
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                err(cb, "❌ Battery health: DEAD — απαιτείται άμεση αντικατάσταση.");
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                err(cb, "❌ Battery health: OVERHEAT / OVERVOLTAGE — επικίνδυνες ενδείξεις.");
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                err(cb, "⚠ Battery health: UNSPECIFIED FAILURE — πιθανό ελάττωμα μπαταρίας.");
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                ok(cb, "✔ Battery health: GOOD (σύμφωνα με Android).");
                break;
            default:
                info(cb, "   Battery health: UNKNOWN / OEM-specific.");
                break;
        }

        // Θερμοκρασία
        if (celsius > 45f) {
            err(cb, String.format(Locale.US,
                    "❌ Πολύ υψηλή θερμοκρασία μπαταρίας: %.1f°C — πιθανή ζημιά / κακή ψύξη.", celsius));
        } else if (celsius > 40f) {
            err(cb, String.format(Locale.US,
                    "⚠ Υψηλή θερμοκρασία μπαταρίας: %.1f°C — παρατεταμένη χρήση σε φόρτιση / ζέστη.", celsius));
        } else {
            ok(cb, String.format(Locale.US,
                    "✔ Θερμοκρασία μπαταρίας φυσιολογική: %.1f°C", celsius));
        }

        // Status (charging / not)
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                info(cb, "   Status: Charging.");
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                info(cb, "   Status: Discharging.");
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                info(cb, "   Status: Full.");
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                info(cb, "   Status: Not charging.");
                break;
            default:
                info(cb, "   Status: Unknown.");
                break;
        }
    }

    // ====================================================================
    // NETWORK LAB
    // ====================================================================
    private static void networkLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n📡 NETWORK LAB");

        // Απλό ping σε Google DNS για basic συνδεσιμότητα
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 8.8.8.8");
            int rc = p.waitFor();
            if (rc == 0) {
                ok(cb, "✔ Βασική δικτυακή συνδεσιμότητα: OK (ping 8.8.8.8).");
            } else {
                err(cb, "⚠ Αποτυχία ping 8.8.8.8 — πιθανό θέμα δικτύου / firewall / δεδομένων.");
            }
        } catch (Exception e) {
            info(cb, "ℹ Δεν μπόρεσα να εκτελέσω ping (περιορισμός συσκευής).");
        }

        // Extra info για έκδοση Android
        info(cb, "   Android version: " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
        info(cb, "   Device: " + Build.MANUFACTURER + " " + Build.MODEL);
    }

    // ====================================================================
    // ROOT DETECTION HELPERS (SAFE)
    // ====================================================================
    private static boolean isRooted() {
        return checkTestKeys() || checkSuBinary() || checkSuperUserApk() || checkWhichSu();
    }

    private static boolean checkTestKeys() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkSuBinary() {
        String[] paths = {
                "/system/bin/", "/system/xbin/", "/sbin/",
                "/system/sd/xbin/", "/system/bin/failsafe/",
                "/data/local/", "/data/local/bin/", "/data/local/xbin/"
        };
        for (String path : paths) {
            File f = new File(path + "su");
            if (f.exists()) return true;
        }
        return false;
    }

    private static boolean checkSuperUserApk() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkWhichSu() {
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

    // ====================================================================
    // SMALL HELPERS
    // ====================================================================
    private static String safeReadFirstLine(String path) {
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

    private static String human(long bytes) {
        if (bytes <= 0) return "0 B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb);
        float gb = mb / 1024f;
        if (gb < 1024) return String.format(Locale.US, "%.1f GB", gb);
        float tb = gb / 1024f;
        return String.format(Locale.US, "%.2f TB", tb);
    }

    private static void info(GELCleaner.LogCallback cb, String m) {
        if (cb != null) cb.log(m, false);
    }

    private static void ok(GELCleaner.LogCallback cb, String m) {
        if (cb != null) cb.log(m, false);
    }

    private static void err(GELCleaner.LogCallback cb, String m) {
        if (cb != null) cb.log(m, true);
    }
}
