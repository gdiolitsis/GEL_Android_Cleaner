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

            for (int i = 1; i <= 10 && running; i++) {

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
                        + " | RAM: " + usedMb + " MB / " + totalMb + " MB";

                String finalLine = line;

                runOnUiThread(() -> {
                    if (txtLive != null) {
                        txtLive.append("\n" + finalLine);
                    }
                });

                try { Thread.sleep(1000); }
                catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // -------------------------------------------------------------------
    // REAL CPU USAGE (universal Android method using /proc/stat)
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

            String[] parts = line.split("\\s+");

            long user = Long.parseLong(parts[1]);
            long nice = Long.parseLong(parts[2]);
            long system = Long.parseLong(parts[3]);
            long idle = Long.parseLong(parts[4]);
            long iowait = Long.parseLong(parts[5]);
            long irq = Long.parseLong(parts[6]);
            long softirq = Long.parseLong(parts[7]);

            long idleAll = idle + iowait;
            long cpuAll = user + nice + system + idle + iowait + irq + softirq;

            return new long[]{ idleAll, cpuAll };

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
