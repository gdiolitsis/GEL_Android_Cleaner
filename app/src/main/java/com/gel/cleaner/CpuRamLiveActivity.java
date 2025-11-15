package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private volatile boolean running = true;

    private long lastCpuTime = 0;
    private long lastAppTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);

        if (txtLive != null) {
            txtLive.setText("Starting CPU / RAM live monitor...\n");
        }

        startLive();
    }

    private void startLive() {
        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            // αρχική μέτρηση
            lastCpuTime = readAppCpuTime();
            lastAppTime = SystemClock.elapsedRealtime();

            for (int i = 1; i <= 20 && running; i++) {

                double cpu = getAppCpuPercent();
                String cpuTxt = cpu < 0 ? "N/A" : String.format("%.1f%%", cpu);

                String line = "📊 Live " + String.format("%02d", i);

                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    long totalMb = mi.totalMem / (1024 * 1024);
                    long availMb = mi.availMem / (1024 * 1024);
                    long usedMb  = totalMb - availMb;

                    line += "  |  CPU: " + cpuTxt +
                            "  |  RAM: " + usedMb + " MB / " + totalMb + " MB";
                }

                String finalLine = line;

                runOnUiThread(() -> {
                    if (txtLive != null) {
                        String prev = txtLive.getText().toString();
                        txtLive.setText(prev + "\n" + finalLine);
                    }
                });

                try { Thread.sleep(1000); }
                catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    // ---------------------------------------------------------
    //      REAL CPU USAGE OF OUR APP  →  Works Everywhere
    // ---------------------------------------------------------
    private long readAppCpuTime() {
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/self/stat"))) {
            String[] parts = br.readLine().split(" ");
            long utime = Long.parseLong(parts[13]);
            long stime = Long.parseLong(parts[14]);
            return utime + stime;
        } catch (Exception e) {
            return -1;
        }
    }

    private double getAppCpuPercent() {
        long cpuNow = readAppCpuTime();
        long timeNow = SystemClock.elapsedRealtime();

        if (cpuNow < 0) return -1;

        long cpuDiff = cpuNow - lastCpuTime;
        long timeDiff = timeNow - lastAppTime;

        lastCpuTime = cpuNow;
        lastAppTime = timeNow;

        if (timeDiff == 0) return 0;

        // 1 tick = 10ms συνήθως (100Hz kernel)
        double cpuPercent = (cpuDiff * 10.0) / timeDiff * 100.0;

        if (cpuPercent < 0) cpuPercent = 0;
        if (cpuPercent > 100) cpuPercent = 100;

        return cpuPercent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
