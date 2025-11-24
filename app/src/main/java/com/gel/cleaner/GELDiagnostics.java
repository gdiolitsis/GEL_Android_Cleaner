// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELDiagnostics v3.0 — Foldable Ready + DualPane + UI Sync + GELOrchestrator
// NOTE: Full-file rewrite — πάντα δουλεύω πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.
// NOTE2: Απόλυτα συμβατό με: GELFoldableOrchestrator, GELFoldableUIManager,
//        GELFoldableAnimationPack, DualPaneManager, GELAutoActivityHook.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

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

    // ============================================================
    // PUBLIC ENTRY (FULL DIAGNOSTICS)
    // ============================================================
    public static void runFullDiagnostics(Context ctx, GELCleaner.LogCallback cb) {

        initFoldableRuntime(ctx);

        info(cb, "🔬 GEL PHONE DIAGNOSTICS STARTED");
        info(cb, "--------------------------------------");

        foldableLab(ctx, cb);
        rootLab(ctx, cb);
        storageLab(ctx, cb);
        memoryLab(ctx, cb);
        cpuLab(cb);
        batteryLab(ctx, cb);
        networkLab(ctx, cb);

        info(cb, "--------------------------------------");
        ok(cb, "✅ Diagnostics finished.");
    }

    // ============================================================
    // FOLDABLE LAB — New for v3.0
    // ============================================================
    private static void foldableLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n📁 FOLDABLE LAB");

        try {
            boolean supported = GELFoldableOrchestrator.isFoldableSupported(ctx);
            boolean dual = DualPaneManager.isDualPaneActive(ctx);

            info(cb, "   Foldable API supported: " + supported);
            info(cb, "   Dual-Pane Mode Active: " + dual);

            if (supported) {
                info(cb, "   ➤ Foldable posture: " +
                        GELFoldableOrchestrator.getCurrentPostureName());
            }

            if (dual) {
                ok(cb, "✔ Device is in dual-pane mode — optimized layout active.");
            } else {
                info(cb, "ℹ Single-screen mode.");
            }

        } catch (Exception e) {
            err(cb, "Foldable Lab error: " + e.getMessage());
        }
    }

    // ============================================================
    // INIT FOLDABLE RUNTIME
    // ============================================================
    private static void initFoldableRuntime(Context ctx) {
        try {
            GELFoldableOrchestrator.initIfPossible(ctx);
            GELFoldableAnimationPack.prepare(ctx);
            DualPaneManager.prepareIfSupported(ctx);
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // ROOT LAB
    // ============================================================
    private static void rootLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🧪 ROOT LAB");

        boolean rooted = isRooted();

        if (rooted) {
            err(cb, "⚠ Η συσκευή φαίνεται ROOTED (test-keys / su binary).");
            info(cb, "   ➤ Από πλευρά service είναι ΟΚ — ενημέρωσε τον πελάτη.");
        } else {
            ok(cb, "✔ Η συσκευή φαίνεται UNROOTED.");
        }

        if (rooted) {
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

    // ============================================================
    // STORAGE LAB
    // ============================================================
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

        info(cb, String.format(Locale.US,
                "   Internal: used %s / %s (free %s)",
                human(used), human(total), human(free)));

        double pct = (total > 0) ? (free * 100.0 / total) : 0;

        if (pct < 5) {
            err(cb, String.format(Locale.US,
                    "❌ Free space %.1f%% — Κρίσιμα χαμηλό.", pct));
        } else if (pct < 10) {
            err(cb, String.format(Locale.US,
                    "⚠ Free space %.1f%% — Χαμηλό.", pct));
        } else {
            ok(cb, String.format(Locale.US,
                    "✔ Free space %.1f%% — OK.", pct));
        }
    }

    // ============================================================
    // MEMORY LAB
    // ============================================================
    private static void memoryLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🧠 MEMORY LAB");

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            err(cb, "❌ ActivityManager = null");
            return;
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long total = mi.totalMem;
        long avail = mi.availMem;
        long used  = total - avail;

        double pct = avail * 100.0 / total;

        info(cb, String.format(Locale.US,
                "   RAM used: %s / %s (free %s)",
                human(used), human(total), human(avail)));

        if (pct < 5) {
            err(cb, String.format(Locale.US,
                    "❌ Free RAM %.1f%% — πολύ χαμηλή.", pct));
        } else if (pct < 15) {
            err(cb, String.format(Locale.US,
                    "⚠ Free RAM %.1f%% — πιθανές καθυστερήσεις.", pct));
        } else {
            ok(cb, String.format(Locale.US,
                    "✔ Free RAM %.1f%% — OK.", pct));
        }

        if (mi.lowMemory) {
            err(cb, "❌ LOW MEMORY mode ενεργό.");
        }
    }

    // ============================================================
    // CPU LAB
    // ============================================================
    private static void cpuLab(GELCleaner.LogCallback cb) {
        info(cb, "\n🧮 CPU LAB");

        int cores = Runtime.getRuntime().availableProcessors();
        info(cb, "   CPU cores detected: " + cores);

        if (cores <= 4) {
            err(cb, "⚠ Λίγοι πυρήνες για σύγχρονα workloads.");
        } else {
            ok(cb, "✔ Αρκετοί πυρήνες.");
        }

        String maxFreq = safeReadFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (maxFreq != null) {
            try {
                long khz = Long.parseLong(maxFreq.trim());
                double ghz = khz / 1_000_000.0;
                info(cb, String.format(Locale.US,
                        "   CPU0 max freq: %.2f GHz", ghz));
            } catch (Exception e) {
                info(cb, "   CPU0 max freq raw: " + maxFreq);
            }
        } else {
            info(cb, "   CPU freq info unavailable.");
        }
    }

    // ============================================================
    // BATTERY LAB
    // ============================================================
    private static void batteryLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🔋 BATTERY LAB");

        Intent batt = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batt == null) {
            err(cb, "❌ Cannot read battery intent.");
            return;
        }

        int level  = batt.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale  = batt.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int temp   = batt.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1); // 1/10°C
        int health = batt.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int status = batt.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        float pct = (scale > 0) ? (level * 100f / scale) : -1;
        float celsius = (temp > 0) ? (temp / 10f) : -1;

        info(cb, String.format(Locale.US,
                "   Battery: %.1f%%   Temp: %.1f°C", pct, celsius));

        // HEALTH
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                err(cb, "❌ Battery health: DEAD.");
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                err(cb, "❌ Battery danger: OVERHEAT / OVERVOLTAGE.");
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                ok(cb, "✔ Battery health: GOOD.");
                break;
            default:
                info(cb, "   Battery health: Unknown.");
        }

        // TEMP
        if (celsius > 45)
            err(cb, "❌ Πολύ υψηλή θερμοκρασία μπαταρίας!");
        else if (celsius > 40)
            err(cb, "⚠ Υψηλή θερμοκρασία μπαταρίας.");
        else
            ok(cb, "✔ Θερμοκρασία φυσιολογική.");

        // STATUS
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
            default:
                info(cb, "   Status: Unknown.");
        }
    }

    // ============================================================
    // NETWORK LAB
    // ============================================================
    private static void networkLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n📡 NETWORK LAB");

        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 8.8.8.8");
            int rc = p.waitFor();
            if (rc == 0)
                ok(cb, "✔ Ping 8.8.8.8 OK.");
            else
                err(cb, "⚠ Ping failed.");
        } catch (Exception e) {
            info(cb, "ℹ Ping not allowed on this device.");
        }

        info(cb, "   Android " + Build.VERSION.RELEASE +
                " (SDK " + Build.VERSION.SDK_INT + ")");
        info(cb, "   Device: " + Build.MANUFACTURER + " " + Build.MODEL);
    }

    // ============================================================
    // ROOT CHECK
    // ============================================================
    private static boolean isRooted() {
        return checkTestKeys() || checkSuBinary() || checkSuperUserApk() || checkWhichSu();
    }

    private static boolean checkTestKeys() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private static boolean checkSuBinary() {
        String[] paths = {
                "/system/bin/", "/system/xbin/", "/sbin/",
                "/system/sd/xbin/", "/system/bin/failsafe/",
                "/data/local/", "/data/local/bin/", "/data/local/xbin/"
        };
        for (String p : paths) if (new File(p + "su").exists()) return true;
        return false;
    }

    private static boolean checkSuperUserApk() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkWhichSu() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private static String safeReadFirstLine(String p) {
        try (BufferedReader br = new BufferedReader(new FileReader(p))) {
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private static String human(long bytes) {
        if (bytes <= 0) return "0 B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb);
        float gb = mb / 1024f;
        return String.format(Locale.US, "%.1f GB", gb);
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
