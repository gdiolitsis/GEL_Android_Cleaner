// GDiolitsis Engine Lab (GEL) — Author & Developer
// CPU_RAM_LiveActivity.java — FINAL v5.0 (Universal CPU Load Engine)

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CPU_RAM_LiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private boolean running = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveData);

        startLiveLoop();
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }

    // ======================================================
    // LIVE LOOP
    // ======================================================
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

    // ======================================================
    // CPU LOAD — works on ALL phones (Android 7–14)
    // ======================================================
    private String readCpuLoad() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/loadavg"));
            String line = br.readLine();
            br.close();
            if (line == null) return "N/A";

            String[] parts = line.split(" ");
            float load = Float.parseFloat(parts[0]);

            // 0.00 – 1.00 ~= 0–100%
            int percent = (int) (load * 100f);

            if (percent < 0) percent = 0;
            if (percent > 100) percent = 100;

            return percent + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    // ======================================================
    // CPU TEMP — many devices return battery temp only
    // ======================================================
    private String readCpuTemp() {
        try {
            // Most devices expose thermal zones locked — fallback to battery sensor
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            int t = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE);
            if (t > 0) return (t / 10f) + "°C";

            return "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ======================================================
    // RAM INFO
    // ======================================================
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
