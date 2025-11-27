// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL RAW v12.0
// True RAW CPU% from /proc/stat — No smoothing, no filters.

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
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private boolean running = true;

    private long lastIdle = -1;
    private long lastTotal = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);

        startLoop();
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }

    private void startLoop() {
        new Thread(() -> {
            int counter = 1;

            while (running) {
                String cpu  = readCpuRaw();
                String temp = readCpuTemp();
                String ram  = readRamUsage();

                String line =
                        "Live " + String.format("%02d", counter) +
                        " | CPU: " + cpu +
                        " | Temp: " + temp +
                        " | RAM: " + ram;

                int finalCounter = counter;
                runOnUiThread(() -> txtLive.append(line + "\n"));

                if (++counter > 999) counter = 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        }).start();
    }

    // ============================================================
    // RAW TRUE CPU% FROM /proc/stat
    // ============================================================
    private String readCpuRaw() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = br.readLine();
            if (line == null || !line.startsWith("cpu")) return "N/A";

            String[] t = line.trim().split("\\s+");
            if (t.length < 8) return "N/A";

            long user = Long.parseLong(t[1]);
            long nice = Long.parseLong(t[2]);
            long system = Long.parseLong(t[3]);
            long idle = Long.parseLong(t[4]);
            long iow = Long.parseLong(t[5]);
            long irq = Long.parseLong(t[6]);
            long sirq = Long.parseLong(t[7]);

            long idleAll = idle + iow;
            long total = user + nice + system + idle + iow + irq + sirq;

            if (lastIdle < 0 || lastTotal < 0) {
                lastIdle = idleAll;
                lastTotal = total;
                return "0%";
            }

            long diffIdle = idleAll - lastIdle;
            long diffTotal = total - lastTotal;

            lastIdle = idleAll;
            lastTotal = total;

            if (diffTotal == 0) return "0%";

            long diffUsed = diffTotal - diffIdle;
            int usage = (int) ((diffUsed * 100L) / diffTotal);

            if (usage < 0) usage = 0;
            if (usage > 100) usage = 100;

            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    // ============================================================
    private String readCpuTemp() {
        try {
            Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (intent == null) return "N/A";
            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            return temp > 0 ? (temp / 10f) + "°C" : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

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
