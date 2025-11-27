// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v8.0 (Core CPU% + Temp + RAM)
// NOTE: Full ready-to-paste file per GEL rule — no manual edits needed.

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

    private long lastIdle = 0L;
    private long lastTotal = 0L;

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
                String cpu  = readCpuLoad();
                String temp = readCpuTemp();
                String ram  = readRamUsage();

                final String line =
                        "Live " + String.format("%02d", counter) +
                        " | CPU: " + cpu +
                        " | Temp: " + temp +
                        " | RAM: " + ram;

                runOnUiThread(() -> txtLive.append(line + "\n"));

                counter++;
                if (counter > 999) counter = 1;

                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    // ============================================================
    // CPU LOAD (from /proc/stat, percentage 0–100)
    // ============================================================
    private String readCpuLoad() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/stat"));
            String line = br.readLine();
            if (line == null || !line.startsWith("cpu")) {
                return "N/A";
            }

            String[] parts = line.trim().split("\\s+");
            if (parts.length < 5) {
                return "N/A";
            }

            long user   = Long.parseLong(parts[1]);
            long nice   = Long.parseLong(parts[2]);
            long system = Long.parseLong(parts[3]);
            long idle   = Long.parseLong(parts[4]);

            long total = user + nice + system + idle;

            if (lastTotal == 0L && lastIdle == 0L) {
                // first sample, just store and return 0% to avoid spike
                lastTotal = total;
                lastIdle = idle;
                return "0%";
            }

            long diffTotal = total - lastTotal;
            long diffIdle  = idle - lastIdle;

            lastTotal = total;
            lastIdle  = idle;

            if (diffTotal <= 0L) {
                return "0%";
            }

            long active = diffTotal - diffIdle;
            int usage = (int) (active * 100L / diffTotal);

            if (usage < 0) usage = 0;
            if (usage > 100) usage = 100;

            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    // ============================================================
    // CPU TEMP (Battery temperature via ACTION_BATTERY_CHANGED)
    // ============================================================
    private String readCpuTemp() {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent = registerReceiver(null, filter);
            if (intent == null) return "N/A";

            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp > 0) {
                float c = temp / 10f;
                return c + "°C";
            } else {
                return "N/A";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ============================================================
    // RAM USAGE (used / total MB)
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
