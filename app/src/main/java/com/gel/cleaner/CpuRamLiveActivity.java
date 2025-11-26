// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — GEL CPU/RAM Live v3.2 (Safe Mode + Battery Temp)
// NOTE: Πάντα δίνουμε ολόκληρο το αρχείο έτοιμο για copy-paste.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class CpuRamLiveActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private TextView txtLive;
    private volatile boolean running = true;
    private boolean isRooted = false;

    // Foldable system
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    // CPU state
    private long[] lastCpuStat = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        txtLive = findViewById(R.id.txtLiveInfo);

        txtLive.setTextSize(sp(14f));
        txtLive.setLineSpacing(dp(2), 1.0f);
        txtLive.setPadding(dp(12), dp(12), dp(12), dp(12));
        txtLive.setText("CPU / RAM Live Monitor started…\n");

        isRooted = isDeviceRooted();
        startLive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foldDetector != null) foldDetector.start();
    }

    @Override
    protected void onPause() {
        if (foldDetector != null) foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS (Unified v1.2)
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {

        final boolean isInner =
                (posture == Posture.FLAT ||
                 posture == Posture.TABLE_MODE ||
                 posture == Posture.FULLY_OPEN);

        if (animPack != null && uiManager != null) {
            animPack.animateReflow(() -> uiManager.applyUI(isInner));
        }
    }

    @Override
    public void onScreenChanged(boolean isInner) {

        if (animPack != null && uiManager != null) {
            animPack.animateReflow(() -> uiManager.applyUI(isInner));
        }

        try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}

        if (isInner) {
            txtLive.setTextSize(sp(17f));
            txtLive.setPadding(dp(18), dp(18), dp(18), dp(18));
        } else {
            txtLive.setTextSize(sp(14f));
            txtLive.setPadding(dp(12), dp(12), dp(12), dp(12));
        }
    }

    // ============================================================
    // SAFE LIVE LOOP
    // ============================================================
    private void startLive() {

        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            int i = 1;
            while (running) {

                double cpu = isRooted ? getCpuRootAccurate() : getCpuTotalAvgPercent();
                String cpuTxt = cpu < 0 ? "N/A" : String.format("%.1f%%", cpu);

                long usedMb = 0, totalMb = 0;
                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    totalMb = mi.totalMem / (1024 * 1024);
                    long availMb = mi.availMem / (1024 * 1024);
                    usedMb = totalMb - availMb;
                }

                String temp = getCpuTemp();

                StringBuilder line = new StringBuilder();
                line.append("Live ").append(String.format("%02d", i))
                        .append(" | CPU: ").append(cpuTxt)
                        .append(" | Temp: ").append(temp)
                        .append(" | RAM: ").append(usedMb).append(" / ").append(totalMb).append(" MB");

                if (isRooted) {
                    String gov = getCpuGovernor();
                    if (gov != null && !gov.isEmpty()) {
                        line.append("\nGovernor: ").append(gov);
                    }

                    String freq = getCpuFreqRoot();
                    if (freq != null && !freq.isEmpty()) {
                        line.append("\nFreq: ").append(freq);
                    }
                }

                runOnUiThread(() -> appendSafe(line.toString()));

                i++;
                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void appendSafe(String s) {
        if (txtLive == null) return;

        String current = txtLive.getText().toString();
        String[] lines = current.split("\n");

        if (lines.length > 300) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = lines.length - 250; i < lines.length; i++) {
                trimmed.append(lines[i]).append("\n");
            }
            txtLive.setText(trimmed.toString());
        }

        txtLive.append(s + "\n");
    }

    // ============================================================
    // CPU HELPERS — SAFE MODE
    // ============================================================

    // Για root συσκευές μπορούμε να το κάνουμε πιο “βαθύ”,
    // αλλά για τώρα επιστρέφουμε την ίδια ασφαλή μέτρηση.
    private double getCpuRootAccurate() {
        return getCpuTotalAvgPercent();
    }

    private long[] readCpuStat() {
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            if (load == null || !load.startsWith("cpu")) return null;

            String[] toks = load.trim().split("\\s+");
            if (toks.length < 5) return null;

            long user = Long.parseLong(toks[1]);
            long nice = Long.parseLong(toks[2]);
            long system = Long.parseLong(toks[3]);
            long idle = Long.parseLong(toks[4]);

            return new long[]{user, nice, system, idle};

        } catch (Throwable ignore) {
            return null;
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) {}
        }
    }

    private double getCpuTotalAvgPercent() {
        try {
            long[] cur = readCpuStat();
            if (cur == null) return -1;

            if (lastCpuStat == null) {
                lastCpuStat = cur;
                return -1; // πρώτο δείγμα -> δεν έχουμε diff ακόμη
            }

            long userDiff   = cur[0] - lastCpuStat[0];
            long niceDiff   = cur[1] - lastCpuStat[1];
            long sysDiff    = cur[2] - lastCpuStat[2];
            long idleDiff   = cur[3] - lastCpuStat[3];

            long total = userDiff + niceDiff + sysDiff + idleDiff;
            if (total <= 0) {
                lastCpuStat = cur;
                return -1;
            }

            long busy = total - idleDiff;
            lastCpuStat = cur;

            return (busy * 100.0d) / total;

        } catch (Throwable ignore) {
            return -1;
        }
    }

    private String getCpuGovernor() {
        String path = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        String gov = readString(path);
        if (gov == null) return null;
        return gov.trim();
    }

    private String getCpuFreqRoot() {
        String path = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
        String val = readString(path);
        if (val == null) return null;

        try {
            long hz = Long.parseLong(val.trim());
            // Συνήθως είναι σε KHz
            long mhz = hz / 1000;
            return mhz + " MHz";
        } catch (Throwable ignore) {
            return val.trim();
        }
    }

    private long readLong(String path) {
        String s = readString(path);
        if (s == null) return -1;
        try {
            return Long.parseLong(s.trim());
        } catch (Throwable ignore) {
            return -1;
        }
    }

    private String readString(String path) {
        BufferedReader br = null;
        try {
            File f = new File(path);
            if (!f.exists()) return null;

            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            return line;
        } catch (Throwable ignore) {
            return null;
        } finally {
            try { if (br != null) br.close(); } catch (Exception ignored) {}
        }
    }

    // ============================================================
    // TEMP (BATTERY FALLBACK)
    // ============================================================
    private String getCpuTemp() {
        // Προσπαθούμε πρώτα battery temp (ασφαλές σε όλες τις συσκευές)
        long milli = readLong("/sys/class/power_supply/battery/temp");
        if (milli > 0) {
            // Μπορεί να είναι σε δεκάτα ή χιλιοστά
            double c;
            if (milli > 1000) {
                c = milli / 1000.0;
            } else {
                c = milli / 10.0;
            }
            return String.format("%.1f°C (battery)", c);
        }

        // Fallback σε thermal_zone0 αν επιτρέπεται
        long t = readLong("/sys/class/thermal/thermal_zone0/temp");
        if (t > 0) {
            double c;
            if (t > 1000) {
                c = t / 1000.0;
            } else {
                c = t / 10.0;
            }
            return String.format("%.1f°C", c);
        }

        return "N/A";
    }

    // ============================================================
    // ROOT DETECTION (ίδιο στυλ με τα άλλα activities)
    // ============================================================
    private boolean isDeviceRooted() {
        try {
            String tags = android.os.Build.TAGS;
            if (tags != null && tags.contains("test-keys")) return true;

            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/su", "/system/bin/.ext/.su",
                    "/system/usr/we-need-root/su-backup",
                    "/system/app/Superuser.apk", "/system/app/SuperSU.apk"
            };
            for (String p : paths) {
                if (new File(p).exists()) return true;
            }

            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", "which su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = in.readLine();
            in.close();
            return line != null && line.trim().length() > 0;

        } catch (Throwable ignore) {
            return false;
        }
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }
}
