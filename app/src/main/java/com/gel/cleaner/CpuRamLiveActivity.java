// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v12.0 (MIUI SAFE)

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

                String cpu = readCpuLoad();     // REAL CPU %
                String temp = readCpuTemp();    // Battery temp
                String ram = readRamUsage();    // RAM usage

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


    // ---------------------------
    // REAL CPU LOAD (MIUI SAFE)
    // ---------------------------
    private String readCpuLoad() {
        try {
            long[] s1 = readCpuStat();
            Thread.sleep(300);
            long[] s2 = readCpuStat();

            long idle = s2[3] - s1[3];
            long total = (s2[0] - s1[0]) +
                         (s2[1] - s1[1]) +
                         (s2[2] - s1[2]) +
                         (s2[3] - s1[3]);

            if (total <= 0) return "0%";

            int usage = (int) ((total - idle) * 100L / total);
            if (usage < 0) usage = 0;
            if (usage > 100) usage = 100;

            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    private long[] readCpuStat() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
            String line = br.readLine();
            br.close();

            if (line == null || !line.startsWith("cpu"))
                return new long[]{0, 0, 0, 0};

            String[] p = line.trim().split("\\s+");

            long user = Long.parseLong(p[1]);
            long nice = Long.parseLong(p[2]);
            long system = Long.parseLong(p[3]);
            long idle = Long.parseLong(p[4]);

            return new long[]{user, nice, system, idle};

        } catch (Exception e) {
            return new long[]{0, 0, 0, 0};
        }
    }


    // ---------------------------
    // CPU TEMP (BATTERY SENSOR)
    // ---------------------------
    private String readCpuTemp() {
        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            int t = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE);
            if (t > 0) return (t / 10f) + "°C";
            return "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }


    // ---------------------------
    // RAM USAGE
    // ---------------------------
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
