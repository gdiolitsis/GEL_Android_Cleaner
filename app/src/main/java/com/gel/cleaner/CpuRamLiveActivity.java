// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v10.0 (Freq-Based CPU%)

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private boolean running = true;
    private int coreCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);

        coreCount = detectCores();

        startLiveLoop();
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }

    private void startLiveLoop() {
        new Thread(() -> {
            int counter = 1;

            while (running) {
                String cpu  = getCpuUsageFreqBased();
                String temp = readCpuTemp();
                String ram  = readRamUsage();

                String line =
                        "Live " + String.format("%02d", counter) +
                        " | CPU: " + cpu +
                        " | Temp: " + temp +
                        " | RAM: " + ram;

                runOnUiThread(() -> txtLive.append(line + "\n"));

                if (++counter > 999) counter = 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        }).start();
    }

    // -------------------------------------------------------------------
    // 1) CPU USAGE BASED ON FREQUENCIES (works on ALL ANDROID 8–14)
    // -------------------------------------------------------------------
    private int detectCores() {
        int cores = 0;
        while (true) {
            File f = new File("/sys/devices/system/cpu/cpu" + cores);
            if (!f.exists()) break;
            cores++;
        }
        return Math.max(1, cores);
    }

    private String getCpuUsageFreqBased() {
        try {
            long totalPercent = 0;

            for (int i = 0; i < coreCount; i++) {
                long cur = readLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq");
                long max = readLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");

                if (cur <= 0 || max <= 0) continue;

                long p = (cur * 100) / max;
                if (p > 100) p = 100;
                if (p < 0)   p = 0;

                totalPercent += p;
            }

            long usage = totalPercent / coreCount;
            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    private long readLong(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            br.close();
            return Long.parseLong(line.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    // -------------------------------------------------------------------
    // TEMP
    // -------------------------------------------------------------------
    private String readCpuTemp() {
        try {
            Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (intent == null) return "N/A";

            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp > 0) return (temp / 10f) + "°C";

            return "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    // -------------------------------------------------------------------
    // RAM
    // -------------------------------------------------------------------
    private String readRamUsage() {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);

            long total = mi.totalMem / (1024 * 1024);
            long free  = mi.availMem / (1024 * 1024);
            long used  = total - free;

            return used + " / " + total + " MB";

        } catch (Exception e) {
            return "N/A";
        }
    }
}
