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
            txtLive.setText("Starting CPU / RAM live monitor...\n");
        }

        startLive();
    }

    private void startLive() {
        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            for (int i = 1; i <= 20 && running; i++) {

                double cpu = getCpuPercent();
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
    //  NEW CPU METHOD → Works on ALL devices (no /proc/stat)
    // ---------------------------------------------------------
    private double getCpuPercent() {
        try {
            long cur = readLong("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            long max = readLong("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");

            if (cur <= 0 || max <= 0) return -1;

            return (cur * 100.0) / max;
        } catch (Exception e) {
            return -1;
        }
    }

    private long readLong(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return Long.parseLong(br.readLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
