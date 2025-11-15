package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private volatile boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);
        txtLive.setText("CPU / RAM Live Monitor started…\n");

        startLive();
    }

    private void startLive() {

        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            int i = 1;
            while (running) {   // ← UNLIMITED LOOP

                double cpu = getCpuTotalAvgPercent();   // MULTI-CORE CPU LOGIC
                String cpuTxt = cpu < 0 ? "N/A" : String.format("%.1f%%", cpu);

                long usedMb = 0, totalMb = 0;

                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    totalMb = mi.totalMem / (1024 * 1024);
                    long availMb = mi.availMem / (1024 * 1024);
                    usedMb = totalMb - availMb;
                }

                String temp = getCpuTemp(); // CPU temperature

                String line =
                        "Live " + String.format("%02d", i) +
                        " | CPU: " + cpuTxt +
                        " | Temp: " + temp +
                        " | RAM: " + usedMb + " MB / " + totalMb + " MB";

                runOnUiThread(() -> txtLive.append("\n" + line));

                i++;

                try { Thread.sleep(1000); }
                catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // ======================================================================
    // MULTI-CORE CPU PERCENT (Universal Android 10–14)
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
    // CPU TEMPERATURE (Universal)
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

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }
}
