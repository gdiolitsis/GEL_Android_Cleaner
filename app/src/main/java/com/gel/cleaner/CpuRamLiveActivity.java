package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private volatile boolean running = true;
    private boolean isRooted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);
        txtLive.setText("CPU / RAM Live Monitor started…\n");

        // ROOT AUTO-DETECTION
        isRooted = isDeviceRooted();

        startLive();
    }

    private void startLive() {

        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            int i = 1;
            while (running) {

                double cpu;
                if (isRooted) cpu = getCpuRootAccurate();    // ← FULL root CPU
                else cpu = getCpuTotalAvgPercent();          // ← NORMAL mode

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
                        .append(" | RAM: ").append(usedMb).append(" MB / ").append(totalMb).append(" MB");

                // EXTRA ROOT-ONLY INFO
                if (isRooted) {
                    String gov = getCpuGovernor();
                    if (gov != null) line.append("\nGovernor: ").append(gov);

                    String freq = getCpuFreqRoot();
                    if (freq != null) line.append("\nFreq: ").append(freq);
                }

                String output = line.toString();

                runOnUiThread(() -> txtLive.append("\n" + output));

                i++;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // ======================================================================
    // ROOT: TRUE CPU USAGE FROM /proc/stat
    // ======================================================================
    private double getCpuRootAccurate() {
        try {
            long[] t1 = readCpuStat();
            Thread.sleep(200);
            long[] t2 = readCpuStat();

            long idle = t2[3] - t1[3];
            long busy = (t2[0] - t1[0]) + (t2[1] - t1[1]) + (t2[2] - t1[2]);

            long total = idle + busy;
            if (total <= 0) return -1;

            return busy * 100.0 / total;

        } catch (Exception e) {
            return -1;
        }
    }

    private long[] readCpuStat() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = br.readLine();
            if (line == null) return null;
            String[] p = line.split("\\s+");

            return new long[]{
                    Long.parseLong(p[1]), // user
                    Long.parseLong(p[2]), // nice
                    Long.parseLong(p[3]), // system
                    Long.parseLong(p[4])  // idle
            };
        } catch (Exception e) {
            return new long[]{0,0,0,0};
        }
    }

    // ======================================================================
    // GOVERNOR (root only)
    // ======================================================================
    private String getCpuGovernor() {
        try {
            File gov = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
            if (!gov.exists()) return null;

            BufferedReader br = new BufferedReader(new FileReader(gov));
            String r = br.readLine();
            br.close();
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    // ======================================================================
    // CPU FREQ (root path)
    // ======================================================================
    private String getCpuFreqRoot() {
        try {
            File cur = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            if (!cur.exists()) return null;

            BufferedReader br = new BufferedReader(new FileReader(cur));
            long f = Long.parseLong(br.readLine());
            br.close();
            return (f / 1000) + " MHz";

        } catch (Exception e) {
            return null;
        }
    }

    // ======================================================================
    // NON-ROOT CPU
    // ======================================================================
    private double getCpuTotalAvgPercent() {
        try {
            int cores = new File("/sys/devices/system/cpu/")
                    .listFiles((f, n) -> n.matches("cpu[0-9]+")).length;

            if (cores <= 0) return -1;

            double sum = 0;
            int valid = 0;

            for (int c = 0; c < cores; c++) {
                long cur = readLong("/sys/devices/system/cpu/cpu" + c + "/cpufreq/scaling_cur_freq");
                long max = readLong("/sys/devices/system/cpu/cpu" + c + "/cpufreq/scaling_max_freq");

                if (cur > 0 && max > 0) {
                    sum += (cur * 100.0) / max;
                    valid++;
                }
            }

            if (valid == 0) return -1;

            return sum / valid;

        } catch (Exception e) {
            return -1;
        }
    }

    private long readLong(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return Long.parseLong(br.readLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    // ======================================================================
    // CPU TEMPERATURE
    // ======================================================================
    private String getCpuTemp() {

        String[] paths = new String[]{
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/class/thermal/thermal_zone1/temp",
                "/sys/class/hwmon/hwmon0/temp1_input"
        };

        for (String p : paths) {
            long v = readLong(p);
            if (v > 0) {
                if (v > 1000) return String.format("%.1f°C", v / 1000f);
                return String.format("%.1f°C", (float) v);
            }
        }

        return "N/A";
    }

    // ======================================================================
    // ROOT DETECTION (SAME AS OTHER ACTIVITIES)
    // ======================================================================
    private boolean isDeviceRooted() {

        try {
            // Test-keys build tag
            String tags = android.os.Build.TAGS;
            if (tags != null && tags.contains("test-keys")) return true;

            // su existence
            String[] paths = { "/system/bin/su", "/system/xbin/su", "/sbin/su", "/system/su" };
            for (String p : paths) if (new File(p).exists()) return true;

            // getprop
            String secure = getProp("ro.secure");
            String debug = getProp("ro.debuggable");

            if ("0".equals(secure) || "1".equals(debug)) return true;

        } catch (Exception ignored) {}

        return false;
    }

    private String getProp(String key) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"getprop", key});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            br.close();
            return line != null ? line.trim() : "";
        } catch (Exception e) { return ""; }
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }
}
