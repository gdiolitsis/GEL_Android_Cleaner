// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FIXED v7.1

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private boolean running = true;

    private long lastIdle = 0;
    private long lastTotal = 0;

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
                String cpu = readCpuLoad();
                String temp = readCpuTemp();
                String ram = readRamUsage();

                final String line =
                        "Live " + String.format("%02d", counter) +
                                " | CPU: " + cpu +
                                " | Temp: " + temp +
                                " | RAM: " + ram;

                runOnUiThread(() -> txtLive.append(line + "\n"));

                counter++;
                if (counter > 999) counter = 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        }).start();
    }

    // REAL CPU %
    private String readCpuLoad() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
            String line = br.readLine();
            br.close();

            if (line == null || !line.startsWith("cpu")) return "N/A";

            String[] parts = line.trim().split("\\s+");

            long user = Long.parseLong(parts[1]);
            long nice = Long.parseLong(parts[2]);
            long system = Long.parseLong(parts[3]);
            long idle = Long.parseLong(parts[4]);

            long total = user + nice + system + idle;

            long diffIdle = idle - lastIdle;
            long diffTotal = total - lastTotal;

            lastIdle = idle;
            lastTotal = total;

            if (diffTotal == 0) return "0%";

            int usage = (int) ((diffTotal - diffIdle) * 100L / diffTotal);
            if (usage < 0) usage = 0;
            if (usage > 100) usage = 100;

            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    // UNIVERSAL SAFE TEMP FOR ALL APIS
    private String readCpuTemp() {
        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    int t = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE);
                    if (t > 0) return (t / 10f) + "°C";
                } catch (Exception ignored) {}
            }

            return "N/A";

        } catch (Exception e) {
            return "N/A";
        }
    }

    private String readRamUsage() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
