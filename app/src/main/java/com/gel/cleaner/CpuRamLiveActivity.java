package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private volatile boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);

        if (txtLive != null) {
            txtLive.setText("CPU / RAM Live Monitor started…\n");
        }

        startLive();
    }

    private void startLive() {
        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            int i = 1;
            while (running) {

                double cpu = getCpuPercent();
                String cpuTxt = cpu < 0 ? "N/A" : String.format("%.1f%%", cpu);

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
                        + " | Temp: " + readCpuTemp()
                        + " | RAM: " + usedMb + " MB / " + totalMb + " MB";

                String finalLine = line;

                runOnUiThread(() -> {
                    if (txtLive != null) {
                        txtLive.append("\n" + finalLine);
                    }
                });

                i++;

                try { Thread.sleep(1000); }
                catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // -------------------------------------------------------------------
    // REAL CPU USAGE (/proc/stat)
    // -------------------------------------------------------------------
    private double getCpuPercent() {
        try {
            long[] t1 = readCpuStat();
            if (t1 == null) return -1;

            Thread.sleep(360);

            long[] t2 = readCpuStat();
            if (t2 == null) return -1;

            long idleDiff = t2[0] - t1[0];
            long cpuDiff  = t2[1] - t1[1];

            if (cpuDiff <= 0) return -1;

            return (cpuDiff - idleDiff) * 100.0 / cpuDiff;

        } catch (Exception e) {
            return -1;
        }
    }

    private long[] readCpuStat() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/stat"))) {

            String line = br.readLine();
            if (line == null || !line.startsWith("cpu ")) return null;

            String[] p = line.split("\\s+");

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

    // -------------------------------------------------------------------
    // CPU TEMPERATURE (universal paths)
    // -------------------------------------------------------------------
    private String readCpuTemp() {
        String[] paths = new String[]{
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/class/thermal/thermal_zone1/temp",
                "/sys/class/thermal/thermal_zone2/temp",
                "/sys/devices/virtual/thermal/thermal_zone0/temp"
        };

        for (String p : paths) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(p));
                String s = br.readLine();
                br.close();

                if (s != null) {
                    float temp = Float.parseFloat(s);
                    if (temp > 0) {
                        if (temp > 1000) temp /= 1000f;
                        return String.format("%.1f°C", temp);
                    }
                }
            } catch (Exception ignored) {}
        }

        return "N/A";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
