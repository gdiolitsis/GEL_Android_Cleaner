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

            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            int i = 1;
            while (running) {

                double cpu = getRealCpu();
                String cpuTxt = cpu < 0 ? "N/A" : String.format("%.1f%%", cpu);

                String tempTxt = readCpuTemp();

                long usedMb = 0, totalMb = 0;

                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    totalMb = mi.totalMem / (1024 * 1024);
                    long availMb = mi.availMem / (1024 * 1024);
                    usedMb = totalMb - availMb;
                }

                String line = "Live " + String.format("%02d", i)
                        + " | CPU: " + cpuTxt
                        + " | Temp: " + tempTxt
                        + " | RAM: " + usedMb + " MB / " + totalMb + " MB";

                String finalLine = line;

                runOnUiThread(() -> txtLive.append("\n" + finalLine));

                i++;

                try { Thread.sleep(900); } catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // ----------------------------------------------------------
    // REAL CPU (Ultra Stable v3.1)
    // ----------------------------------------------------------
    private double getRealCpu() {
        try {
            long[] t1 = readCpuStat();
            if (t1 == null) return -1;

            Thread.sleep(250); // πιο safe timing

            long[] t2 = readCpuStat();
            if (t2 == null) return -1;

            long idle = t2[0] - t1[0];
            long cpu = t2[1] - t1[1];

            if (cpu <= 0) return -1;

            return (cpu - idle) * 100.0 / cpu;

        } catch (Exception e) {
            return -1;
        }
    }

    private long[] readCpuStat() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
            String line = br.readLine();
            br.close();

            if (line == null || !line.startsWith("cpu ")) return null;

            String[] p = line.trim().split("\\s+");

            long user = Long.parseLong(p[1]);
            long nice = Long.parseLong(p[2]);
            long system = Long.parseLong(p[3]);
            long idle = Long.parseLong(p[4]);
            long iowait = Long.parseLong(p[5]);
            long irq = Long.parseLong(p[6]);
            long softirq = Long.parseLong(p[7]);

            long idleAll = idle + iowait;
            long cpuAll = user + nice + system + idle + iowait + irq + softirq;

            return new long[]{ idleAll, cpuAll };

        } catch (Exception e) {
            return null;
        }
    }

    // ----------------------------------------------------------
    // UNIVERSAL CPU TEMPERATURE (auto-detect zone)
    // ----------------------------------------------------------
    private String readCpuTemp() {

        File thermalRoot = new File("/sys/class/thermal");

        if (thermalRoot.exists()) {
            File[] zones = thermalRoot.listFiles();
            if (zones != null) {
                for (File z : zones) {
                    if (z.getName().startsWith("thermal_zone")) {
                        try {
                            File tempFile = new File(z, "temp");
                            if (tempFile.exists()) {
                                BufferedReader br = new BufferedReader(new FileReader(tempFile));
                                String s = br.readLine();
                                br.close();
                                if (s != null) {
                                    float t = Float.parseFloat(s);
                                    if (t > 1000) t /= 1000f;
                                    if (t > 0 && t < 150) return String.format("%.1f°C", t);
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
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
