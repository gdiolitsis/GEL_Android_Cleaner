// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v9.1 (Stable CPU% Snapshot + Temp + RAM)

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private boolean running = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);

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
                String cpu  = readCpuUsage();
                String temp = readCpuTemp();
                String ram  = readRamUsage();

                final String line =
                        "Live " + String.format("%02d", counter) +
                        " | CPU: " + cpu +
                        " | Temp: " + temp +
                        " | RAM: "  + ram;

                runOnUiThread(() -> txtLive.append(line + "\n"));

                counter++;
                if (counter > 999) counter = 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        }).start();
    }

    // ============================================================
    // CPU USAGE — Snapshot Method (Works Android 9–14)
    // ============================================================
    private String readCpuUsage() {
        try {
            long[] sample1 = readCpuSnapshot();
            Thread.sleep(200); // short delay gives accurate diff
            long[] sample2 = readCpuSnapshot();

            if (sample1 == null || sample2 == null) return "N/A";

            long idle  = sample2[0] - sample1[0];
            long total = sample2[1] - sample1[1];

            if (total <= 0) return "0%";

            int usage = (int) ((total - idle) * 100L / total);

            if (usage < 0) usage = 0;
            if (usage > 100) usage = 100;

            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    private long[] readCpuSnapshot() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = br.readLine();
            if (line == null || !line.startsWith("cpu")) return null;

            String[] p = line.trim().split("\\s+");
            if (p.length < 8) return null;

            long user = Long.parseLong(p[1]);
            long nice = Long.parseLong(p[2]);
            long sys  = Long.parseLong(p[3]);
            long idle = Long.parseLong(p[4]);
            long iow  = Long.parseLong(p[5]);
            long irq  = Long.parseLong(p[6]);
            long sirq = Long.parseLong(p[7]);

            long idleAll = idle + iow;
            long total   = user + nice + sys + idle + iow + irq + sirq;

            return new long[]{ idleAll, total };

        } catch (Exception e) {
            return null;
        }
    }

    // ============================================================
    // TEMP
    // ============================================================
    private String readCpuTemp() {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent = registerReceiver(null, filter);
            if (intent == null) return "N/A";

            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp > 0) return (temp / 10f) + "°C";

            return "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ============================================================
    // RAM
    // ============================================================
    private String readRamUsage() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return "N/A";

            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long total = mi.totalMem / (1024 * 1024);
            long free  = mi.availMem / (1024 * 1024);
            long used  = total - free;

            return used + " / " + total + " MB";

        } catch (Exception e) {
            return "N/A";
        }
    }
}
