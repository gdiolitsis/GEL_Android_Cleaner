// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v6.0

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

public class CpuRamLiveActivity extends AppCompatActivity {

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
            long[] v1 = readCpuStat();
            Thread.sleep(360);
            long[] v2 = readCpuStat();

            long idle = v2[3] - v1[3];
            long total = (v2[0] - v1[0]) + (v2[1] - v1[1]) + (v2[2] - v1[2]) + idle;

            if (total <= 0) return "N/A";

            int usage = (int) ((100L * (total - idle)) / total);
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

            if (line == null || !line.startsWith("cpu ")) return new long[]{0,0,0,0};

            String[] parts = line.split("\\s+");
            long user = Long.parseLong(parts[1]);
            long nice = Long.parseLong(parts[2]);
            long system = Long.parseLong(parts[3]);
            long idle = Long.parseLong(parts[4]);

            return new long[]{user, nice, system, idle};

        } catch (Exception e) {
            return new long[]{0,0,0,0};
        }
    }

    private String readCpuTemp() {
        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            int t = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE);
            if (t > 0) return (t / 10f) + "°C";
        } catch (Exception ignore) {}

        try {
            File dir = new File("/sys/class/thermal");
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.getName().contains("thermal_zone")) {
                            File tf = new File(f, "temp");
                            if (tf.exists()) {
                                BufferedReader br = new BufferedReader(new FileReader(tf));
                                String line = br.readLine();
                                br.close();
                                if (line != null) {
                                    float v = Float.parseFloat(line.trim());
                                    if (v > 1000) v /= 1000f;
                                    if (v > 0) return v + "°C";
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignore) {}

        return "N/A";
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
