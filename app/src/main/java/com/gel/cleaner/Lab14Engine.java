package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * ============================================================
 * Lab14Engine — GEL Battery Diagnostics Core
 * Single Source of Truth for ALL battery-related labs
 *
 * - Root-aware
 * - ChargeCounter-first
 * - Snapshot-based
 * - No UI / No dialogs / No threads
 *
 * Used by:
 *  - LAB 14 (Battery Health Stress Test)
 *  - LAB 15 (Charging / Thermal correlation)
 *  - Future battery-related labs
 *
 * Author: GDiolitsis Engine Lab (GEL)
 * ============================================================
 *
 * NOTE (GEL RULE): Ό,τι σου στέλνω είναι έτοιμο copy-paste — χωρίς “μπλα μπλα”.
 */
public final class Lab14Engine {

    // ------------------------------------------------------------
    // PREFS (single storage for runs + last drains)
    // ------------------------------------------------------------
    private static final String LAB14_PREFS = "lab14_prefs";
    private static final String KEY_LAB14_RUNS = "lab14_run_count";
    private static final String KEY_LAB14_LAST_DRAIN_1 = "lab14_drain_1";
    private static final String KEY_LAB14_LAST_DRAIN_2 = "lab14_drain_2";
    private static final String KEY_LAB14_LAST_DRAIN_3 = "lab14_drain_3";

    private final Context ctx;

    public Lab14Engine(Context context) {
        this.ctx = context.getApplicationContext();
    }

    // ============================================================
    // SNAPSHOT (AUTHORITATIVE BATTERY STATE)
    // ============================================================
    public static final class GelBatterySnapshot {

        // basic
        public int level = -1;
        public int scale = -1;
        public float temperature = Float.NaN;
        public boolean charging = false;

        // capacity / charge
        public long chargeNowMah = -1;
        public long chargeFullMah = -1;
        public long chargeDesignMah = -1;
        public long cycleCount = -1;

        // meta
        public boolean rooted = false;
        public String source = "Unknown";
    }

    // ============================================================
    // PUBLIC ENTRY — READ SNAPSHOT
    // ============================================================
    public GelBatterySnapshot readSnapshot() {

        GelBatterySnapshot s = new GelBatterySnapshot();

        // --------------------------------------------------------
        // 1) BATTERY_CHANGED (SAFE)
        // --------------------------------------------------------
        try {
            Intent i = ctx.registerReceiver(
                    null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );

            if (i != null) {
                s.level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                s.scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                int plug = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                s.charging = plug == BatteryManager.BATTERY_PLUGGED_AC
                        || plug == BatteryManager.BATTERY_PLUGGED_USB
                        || plug == BatteryManager.BATTERY_PLUGGED_WIRELESS;

                int rawTemp = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                if (rawTemp > 0) {
                    s.temperature = rawTemp / 10f;
                }
            }
        } catch (Throwable ignore) {}

        // --------------------------------------------------------
        // 2) ROOT / SYSFS (IF AVAILABLE)
        // --------------------------------------------------------
        s.rooted = isDeviceRooted();

        if (s.rooted) {

            s.chargeDesignMah = normalizeMah(
                    readSysLongRootAware("/sys/class/power_supply/battery/charge_full_design")
            );

            s.chargeFullMah = normalizeMah(
                    readSysLongRootAware("/sys/class/power_supply/battery/charge_full")
            );

            s.chargeNowMah = normalizeMah(
                    readSysLongRootAware("/sys/class/power_supply/battery/charge_now")
            );

            s.cycleCount = readBatteryCycleCountRoot();

            if (s.chargeFullMah > 0 || s.chargeNowMah > 0 || s.chargeDesignMah > 0) {
                s.source = "OEM (root)";
            }
        }

        // --------------------------------------------------------
        // 3) CHARGE COUNTER FALLBACK (PRIMARY FOR UNROOTED)
        // --------------------------------------------------------
        if (s.chargeNowMah <= 0) {
            try {
                BatteryManager bm =
                        (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);

                if (bm != null) {
                    long cc = normalizeMah(
                            bm.getLongProperty(
                                    BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
                            )
                    );

                    if (cc > 0) {
                        s.chargeNowMah = cc;
                        s.source = "Charge Counter";

                        if (s.level > 0) {
                            s.chargeFullMah =
                                    (long) (cc / (s.level / 100f));
                        }
                    }
                }
            } catch (Throwable ignore) {}
        }

        return s;
    }

    // ============================================================
    // CONFIDENCE ENGINE (RUNS-BASED, 1/2/3+)
    // ============================================================
    public enum ConfidenceTier {
        NONE,
        PRELIMINARY, // 1 run
        MEDIUM,      // 2 runs
        HIGH         // 3+ runs (certainty)
    }

    public static final class ConfidenceResult {
        public int percent;       // kept for existing gating (e.g., >=70)
        public int validRuns;     // number of stored valid drains used
        public ConfidenceTier tier;
        public double relVar;     // 0.. (only meaningful if validRuns>=2)
    }

    /**
     * Confidence philosophy (LOCKED):
     * 1 run  -> Preliminary (looks OK)
     * 2 runs -> Medium (opinion forming)
     * 3+     -> High (certainty) + variance refines %
     */
    public ConfidenceResult computeConfidence() {

        ConfidenceResult r = new ConfidenceResult();
        r.percent = 0;
        r.validRuns = 0;
        r.tier = ConfidenceTier.NONE;
        r.relVar = -1;

        try {
            SharedPreferences sp =
                    ctx.getSharedPreferences(LAB14_PREFS, Context.MODE_PRIVATE);

            double[] v = new double[]{
                    Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(-1))),
                    Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(-1))),
                    Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_3, Double.doubleToLongBits(-1)))
            };

            double sum = 0;
            int n = 0;
            for (double d : v) {
                if (d > 0) {
                    sum += d;
                    n++;
                }
            }

            r.validRuns = n;

            // ----------------------------------------------------
            // Runs-based tiers (LOCKED)
            // ----------------------------------------------------
            if (n <= 0) {
                r.tier = ConfidenceTier.NONE;
                r.percent = 0;
                return r;
            }

            if (n == 1) {
                r.tier = ConfidenceTier.PRELIMINARY;
                r.percent = 50; // informational only
                return r;
            }

            if (n == 2) {
                r.tier = ConfidenceTier.MEDIUM;
                r.percent = 70; // opinion-forming (meets aging gate)
                // compute relVar too (for informational use)
            } else {
                r.tier = ConfidenceTier.HIGH;
                r.percent = 90; // certainty baseline (variance may refine)
            }

            // ----------------------------------------------------
            // Variance (consistency) — refines percent for 2+ runs
            // ----------------------------------------------------
            double mean = sum / n;
            if (mean <= 0) {
                // keep tier-based % as-is
                r.relVar = -1;
                return r;
            }

            double var = 0;
            for (double d : v) {
                if (d > 0) var += (d - mean) * (d - mean);
            }
            var /= n;

            double rel = Math.sqrt(var) / mean;
            r.relVar = rel;

            // Variance refinement (LOCKED thresholds)
            if (n == 2) {
                // For 2 runs, keep MEDIUM tier, refine 70..80
                if (rel < 0.08) r.percent = 80;
                else if (rel < 0.15) r.percent = 75;
                else r.percent = 70;
            } else {
                // 3+ runs, keep HIGH tier, refine 80..95
                if (rel < 0.08) r.percent = 95;
                else if (rel < 0.15) r.percent = 90;
                else r.percent = 80; // still HIGH tier, but unstable conditions
            }

        } catch (Throwable ignore) {
            r.percent = 0;
            r.validRuns = 0;
            r.tier = ConfidenceTier.NONE;
            r.relVar = -1;
        }

        return r;
    }

    // ============================================================
    // VARIANCE CONSISTENCY (NO UI)
    // ============================================================
    public enum VarianceTier {
        NONE,
        CONSISTENT,
        MINOR_VARIABILITY,
        HIGH_VARIABILITY
    }

    public static final class VarianceResult {
        public VarianceTier tier;
        public int validRuns;
        public double relVar;
        public String message;
    }

    public VarianceResult computeVarianceConsistency() {

        VarianceResult out = new VarianceResult();
        out.tier = VarianceTier.NONE;
        out.validRuns = 0;
        out.relVar = -1;
        out.message = "N/A";

        try {
            SharedPreferences sp =
                    ctx.getSharedPreferences(LAB14_PREFS, Context.MODE_PRIVATE);

            double[] vals = new double[]{
                    Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(-1))),
                    Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(-1))),
                    Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_3, Double.doubleToLongBits(-1)))
            };

            double sum = 0;
            int n = 0;
            for (double v : vals) {
                if (v > 0) {
                    sum += v;
                    n++;
                }
            }

            out.validRuns = n;

            if (n < 2) {
                out.tier = VarianceTier.NONE;
                out.message = "Not available (requires at least 2 runs).";
                return out;
            }

            double mean = sum / n;
            if (mean <= 0) {
                out.tier = VarianceTier.NONE;
                out.message = "Not available (invalid mean).";
                return out;
            }

            double var = 0;
            for (double v : vals) {
                if (v > 0) var += (v - mean) * (v - mean);
            }
            var /= n;

            double std = Math.sqrt(var);
            double relVar = std / mean;

            out.relVar = relVar;

            if (relVar < 0.08) {
                out.tier = VarianceTier.CONSISTENT;
                out.message = "Results are consistent across runs.";
            } else if (relVar < 0.15) {
                out.tier = VarianceTier.MINOR_VARIABILITY;
                out.message = "Minor variability detected between runs.";
            } else {
                out.tier = VarianceTier.HIGH_VARIABILITY;
                out.message = "High variability detected. Run tests under similar conditions for best accuracy.";
            }

        } catch (Throwable ignore) {
            out.tier = VarianceTier.NONE;
            out.validRuns = 0;
            out.relVar = -1;
            out.message = "N/A";
        }

        return out;
    }

    // ============================================================
    // BATTERY PROFILE
    // ============================================================
    public enum BatteryProfileType {
        NEW_EARLY_LIFE,
        NORMAL_AGING,
        UNKNOWN
    }

    public static final class BatteryProfile {
        public BatteryProfileType type;
        public String label;
    }

    public BatteryProfile detectProfile(
            GelBatterySnapshot snap,
            ConfidenceResult conf
    ) {

        BatteryProfile p = new BatteryProfile();
        p.type = BatteryProfileType.UNKNOWN;
        p.label = "Unknown battery profile";

        if (snap.cycleCount > 0
                && snap.cycleCount <= 20
                && conf != null
                && conf.validRuns <= 3) {

            p.type = BatteryProfileType.NEW_EARLY_LIFE;
            p.label = "New / early-life battery (cycle count ≤ 20)";
            return p;
        }

        p.type = BatteryProfileType.NORMAL_AGING;
        p.label = "Normal aging battery profile";
        return p;
    }

    // ============================================================
    // AGING INDEX
    // ============================================================
    public static final class AgingResult {
        public boolean severe;
        public String description;
    }

    public AgingResult computeAging(
            double mahPerHour,
            ConfidenceResult conf,
            long cycleCount,
            Float tBefore,
            Float tAfter
    ) {

        AgingResult r = new AgingResult();

        if (mahPerHour <= 0 || conf == null || conf.percent < 70) {
            r.severe = false;
            r.description = "Insufficient data for aging evaluation.";
            return r;
        }

        boolean thermalStress = false;
        if (tBefore != null && tAfter != null) {
            thermalStress = (tAfter - tBefore) > 7.0;
        }

        r.severe =
                mahPerHour > 800
                        && thermalStress
                        && cycleCount > 200;

        r.description = r.severe
                ? "Heavy aging indicators detected."
                : "Aging within expected limits.";

        return r;
    }

    // ============================================================
    // THRESHOLDS (PROFILE & ROOT AWARE)
    // ============================================================
    public double getWarnThreshold(
            BatteryProfile profile,
            boolean rooted,
            long cycles
    ) {
        if (profile != null && profile.type == BatteryProfileType.NEW_EARLY_LIFE) return 900;
        if (rooted && cycles > 300) return 700;
        return 750;
    }

    public double getReplaceThreshold(
            BatteryProfile profile,
            boolean rooted,
            long cycles
    ) {
        if (!rooted) return Double.MAX_VALUE;
        if (cycles > 400) return 900;
        return 1000;
    }

    // ============================================================
    // RUN HISTORY STORAGE
    // ============================================================
    public void saveDrainValue(double mahPerHour) {

        if (mahPerHour <= 0) return;

        try {
            SharedPreferences sp =
                    ctx.getSharedPreferences(LAB14_PREFS, Context.MODE_PRIVATE);

            double d1 = Double.longBitsToDouble(
                    sp.getLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(-1))
            );
            double d2 = Double.longBitsToDouble(
                    sp.getLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(-1))
            );

            sp.edit()
                    .putLong(KEY_LAB14_LAST_DRAIN_3, Double.doubleToLongBits(d2))
                    .putLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(d1))
                    .putLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(mahPerHour))
                    .apply();

        } catch (Throwable ignore) {}
    }

    public void saveRun() {
        try {
            SharedPreferences sp =
                    ctx.getSharedPreferences(LAB14_PREFS, Context.MODE_PRIVATE);
            int runs = sp.getInt(KEY_LAB14_RUNS, 0);
            sp.edit().putInt(KEY_LAB14_RUNS, runs + 1).apply();
        } catch (Throwable ignore) {}
    }

    public int getRunCount() {
        try {
            SharedPreferences sp =
                    ctx.getSharedPreferences(LAB14_PREFS, Context.MODE_PRIVATE);
            return sp.getInt(KEY_LAB14_RUNS, 0);
        } catch (Throwable ignore) {
            return 0;
        }
    }

    // ============================================================
    // ROOT / SYSFS HELPERS
    // ============================================================
    private boolean isDeviceRooted() {

        String[] paths = {
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/vendor/bin/su"
        };

        for (String p : paths) {
            try {
                if (new File(p).exists()) return true;
            } catch (Throwable ignore) {}
        }

        try {
            Process p =
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
            int rc = p.waitFor();
            return rc == 0;
        } catch (Throwable ignore) {}

        return false;
    }

    private long readBatteryCycleCountRoot() {

        String[] paths = {
                "/sys/class/power_supply/battery/cycle_count",
                "/sys/class/power_supply/bms/cycle_count",
                "/sys/class/power_supply/battery/bms/cycle_count",
                "/sys/class/power_supply/battery/charge_cycles"
        };

        for (String p : paths) {
            long v = readSysLongRootAware(p);
            if (v > 0) return v;
        }
        return -1;
    }

    private long readSysLongRootAware(String path) {

        // direct
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String s = br.readLine();
            br.close();
            if (s != null) {
                s = s.trim();
                if (!s.isEmpty())
                    return Long.parseLong(s.replaceAll("[^0-9]", ""));
            }
        } catch (Throwable ignore) {}

        // su fallback
        Process p = null;
        BufferedReader br = null;
        try {
            p = Runtime.getRuntime()
                    .exec(new String[]{"su", "-c", "cat " + path});
            br = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );
            String s = br.readLine();
            if (s != null) {
                s = s.trim();
                if (!s.isEmpty())
                    return Long.parseLong(s.replaceAll("[^0-9]", ""));
            }
        } catch (Throwable ignore) {
        } finally {
            try { if (br != null) br.close(); } catch (Throwable ignore) {}
            try { if (p != null) p.destroy(); } catch (Throwable ignore) {}
        }

        return -1;
    }

    private long normalizeMah(long raw) {
        if (raw <= 0) return -1;
        if (raw > 200_000) return raw / 1000;
        return raw;
    }
}
