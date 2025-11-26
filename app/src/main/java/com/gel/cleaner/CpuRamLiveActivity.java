// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v5.2 FIXED

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
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

        // FIXED → correct ID
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

    private String readCpuLoad() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/loadavg"));
            String line = br.readLine();
            br.close();

            if (line == null) return "N/A";

            float load = Float.parseFloat(line.split(" ")[0]);
            int percent = (int) (load * 100f);

            return percent + "%";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String readCpuTemp() {
        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);

            // FIXED → use int property fallback (GitHub API 23 support)
            int t = bm.getIntProperty(4); // 4 = TEMPERATURE

            if (t > 0) return (t / 10f) + "°C";
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
