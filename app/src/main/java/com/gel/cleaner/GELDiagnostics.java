// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELDiagnostics v3.1 — Foldable Ready + DualPane + UI Sync + Orchestrator Sync
// NOTE: Τελική PRO έκδοση — full rewrite πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.
// NOTE2: Συμβατό με:
//    • GELFoldableOrchestrator
//    • GELFoldableUIManager
//    • GELFoldableAnimationPack
//    • DualPaneManager
//    • GELAutoActivityHook
// NOTE3: Compile-Safe sync με τα πραγματικά APIs του base package (no phantom methods).

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Locale;

public class GELDiagnostics {

    // ============================================================
    // PUBLIC ENTRY
    // ============================================================
    public static void runFullDiagnostics(Context ctx, GELCleaner.LogCallback cb) {

        initFoldableRuntime(ctx);

        info(cb, "🔬 GEL PHONE DIAGNOSTICS STARTED");
        info(cb, "--------------------------------------");

        foldableLab(ctx, cb);
        deviceLab(ctx, cb);
        rootLab(ctx, cb);
        storageLab(ctx, cb);
        memoryLab(ctx, cb);
        cpuLab(cb);
        batteryLab(ctx, cb);
        networkLab(cb);

        info(cb, "--------------------------------------");
        ok(cb, "✅ Diagnostics finished.");
    }

    // ============================================================
    // FOLDABLE LAB
    // ============================================================
    private static void foldableLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n📁 FOLDABLE LAB");

        try {
            boolean supported = isFoldableSupportedLocal(ctx);
            boolean dual = DualPaneManager.isDualPaneActive(ctx);

            info(cb, "   Foldable API supported: " + supported);
            info(cb, "   Dual-Pane Mode Active: " + dual);

            // No direct posture API exposed by orchestrator in current base.
            if (supported) {
                info(cb, "   ➤ Posture: (runtime-driven, no static read)");
            }

            if (dual) ok(cb, "✔ Dual-pane active.");
            else info(cb, "ℹ Single screen mode");

        } catch (Exception e) {
            err(cb, "Foldable Lab error: " + e.getMessage());
        }
    }

    private static boolean isFoldableSupportedLocal(Context ctx) {
        try {
            SensorManager sm = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            if (sm == null) return false;
            Sensor hinge = sm.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE);
            return hinge != null;
        } catch (Throwable ignore) {
            return false;
        }
    }

    // ============================================================
    // DEVICE LAB (NEW)
    // ============================================================
    private static void deviceLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n📱 DEVICE LAB");

        info(cb, "   Model: " + Build.MANUFACTURER + " " + Build.MODEL);
        info(cb, "   Android: " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");

        if (Build.VERSION.SDK_INT < 28)
            warn(cb, "⚠ Το Android είναι παλιό. Service impact πιθανό.");
        else
            ok(cb, "✔ Android version OK.");
    }

    // ============================================================
    // INIT FOLDABLE RUNTIME (compile-safe)
    // ============================================================
    private static void initFoldableRuntime(Context ctx) {
        try {
            // Current base exposes only these static hooks:
            try { GELFoldableAnimationPack.prepare(ctx); } catch (Throwable ignore) {}
            try { DualPaneManager.prepareIfSupported(ctx); } catch (Throwable ignore) {}
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // ROOT LAB
    // ============================================================
    private static void rootLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🧪 ROOT LAB");

        boolean rooted = isRooted();

        if (rooted) {
            err(cb, "⚠ Η συσκευή φαίνεται ROOTED.");
            info(cb, "   ➤ Ενημέρωσε πελάτη, πιθανή αλλοίωση στοιχείων.");
        } else {
            ok(cb, "✔ Η συσκευή φαίνεται UNROOTED.");
        }

        if (rooted) {
            String[] cycleCandidates = {
                    "/sys/class/power_supply/battery/cycle_count",
                    "/sys/class/power_supply/bms/cycle_count"
            };
            boolean found = false;
            for (String p : cycleCandidates) {
                String line = safeReadFirstLine(p);
                if (line != null) {
                    info(cb, "   🔍 Battery cycles: " + line.trim());
                    found = true;
                    break;
                }
            }
            if (!found) info(cb, "   ℹ Δεν υπάρχει cycle_count για αυτή τη συσκευή.");
        }
    }

    // ============================================================
    // STORAGE LAB
    // ============================================================
    private static void storageLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n💾 STORAGE LAB");

        File data = ctx.getFilesDir();
        if (data == null) {
            err(cb, "❌ Internal storage not readable.");
            return;
        }

        long total = data.getTotalSpace();
        long free  = data.getFreeSpace();
        long used  = total - free;

        info(cb, String.format(Locale.US,
                "   Internal: used %s / %s (free %s)",
                human(used), human(total), human(free)));

        double pct = (total > 0) ? (free * 100.0 / total) : 0.0;

        if (pct < 5)
            err(cb, String.format(Locale.US, "❌ Free space %.1f%% — Κρίσιμα.", pct));
        else if (pct < 10)
            warn(cb, String.format(Locale.US, "⚠ Free space %.1f%% — Χαμηλό.", pct));
        else
            ok(cb, String.format(Locale.US, "✔ Free space %.1f%% — OK.", pct));
    }

    // ============================================================
    // MEMORY LAB
    // ============================================================
    private static void memoryLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🧠 MEMORY LAB");

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            err(cb, "❌ ActivityManager null");
            return;
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long total = mi.totalMem;
        long avail = mi.availMem;
        long used  = total - avail;

        double freePct = (total > 0) ? (avail * 100.0 / total) : 0.0;

        info(cb, String.format(Locale.US,
                "   RAM used: %s / %s (free %s)",
                human(used), human(total), human(avail)));

        if (freePct < 5)
            err(cb, "❌ RAM critically low.");
        else if (freePct < 15)
            warn(cb, "⚠ Low RAM.");
        else
            ok(cb, "✔ RAM OK.");

        if (mi.lowMemory)
            err(cb, "❌ LOW MEMORY mode active.");
    }

    // ============================================================
    // CPU LAB
    // ============================================================
    private static void cpuLab(GELCleaner.LogCallback cb) {
        info(cb, "\n🧮 CPU LAB");

        int cores = Runtime.getRuntime().availableProcessors();
        info(cb, "   CPU cores: " + cores);

        if (cores <= 4)
            warn(cb, "⚠ Low core count for modern workloads.");
        else
            ok(cb, "✔ Enough CPU cores.");

        String max = safeReadFirstLine("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        if (max != null) {
            try {
                long khz = Long.parseLong(max.trim());
                double ghz = khz / 1_000_000.0;
                info(cb, String.format(Locale.US, "   CPU0 max freq: %.2f GHz", ghz));
            } catch (Exception e) {
                info(cb, "   Raw freq: " + max);
            }
        } else info(cb, "   CPU freq unavailable.");
    }

    // ============================================================
    // BATTERY LAB
    // ============================================================
    private static void batteryLab(Context ctx, GELCleaner.LogCallback cb) {
        info(cb, "\n🔋 BATTERY LAB");

        IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent i = ctx.registerReceiver(null, f);

        if (i == null) {
            err(cb, "❌ Cannot read battery intent.");
            return;
        }

        int level  = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale  = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int temp   = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int health = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        float pct = (scale > 0) ? (level * 100f / scale) : -1;
        float celsius = (temp > 0) ? (temp / 10f) : -1;

        info(cb, String.format(Locale.US,
                "   Battery: %.1f%%   Temp: %.1f°C", pct, celsius));

        // HEALTH
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                err(cb, "❌ Battery health critical.");
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                ok(cb, "✔ Battery health GOOD.");
                break;
            default:
                info(cb, "   Health unknown.");
        }

        // TEMP
        if (celsius > 45)
            err(cb, "❌ Battery overheating!");
        else if (celsius > 40)
            warn(cb, "⚠ Battery hot.");
        else
            ok(cb, "✔ Temperature normal.");

        // STATUS
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                info(cb, "   Status: Charging");
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                info(cb, "   Status: Discharging");
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                info(cb, "   Status: Full");
                break;
            default:
                info(cb, "   Status: Unknown");
        }
    }

    // ============================================================
    // NETWORK LAB
    // ============================================================
    private static void networkLab(GELCleaner.LogCallback cb) {
        info(cb, "\n📡 NETWORK LAB");

        try {
            boolean reachable = InetAddress.getByName("8.8.8.8").isReachable(1500);
            if (reachable) ok(cb, "✔ Ping 8.8.8.8 OK.");
            else warn(cb, "⚠ Ping failed.");
        } catch (Exception e) {
            info(cb, "ℹ Ping blocked by OEM.");
        }
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
        String[] dirs = {
                "/system/bin/","/system/xbin/","/sbin/",
                "/system/sd/xbin/","/system/bin/failsafe/",
                "/data/local/","/data/local/bin/","/data/local/xbin/"
        };
        for (String d : dirs)
            if (new File(d + "su").exists()) return true;
        return false;
    }

    private static boolean checkSuperUserApk() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkWhichSu() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"which","su"});
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

    private static void warn(GELCleaner.LogCallback cb, String m) {
        if (cb != null) cb.log(m, false);
    }

    private static void err(GELCleaner.LogCallback cb, String m) {
        if (cb != null) cb.log(m, true);
    }
}
