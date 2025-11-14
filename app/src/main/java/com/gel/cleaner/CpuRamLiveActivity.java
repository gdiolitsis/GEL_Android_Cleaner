package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CpuRamLiveActivity extends AppCompatActivity {

    private TextView txtLive;
    private volatile boolean running = true;

    // previous cpu totals για τον υπολογισμό των deltas
    private long prevTotal = -1L;
    private long prevIdle  = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);
        if (txtLive != null) {
            txtLive.setText("Starting CPU / RAM live monitor...\n");
        }

        startLiveLoop();
    }

    private void startLiveLoop() {
        Thread t = new Thread(() -> {
            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            final int samples = 20;      // όπως πριν
            final long delayMs = 1000L;  // 1 sec

            for (int i = 1; i <= samples && running; i++) {

                double cpu = readCpuUsage();
                String cpuText = (cpu < 0) ? "N/A" : String.format("%.1f%%", cpu);

                String line = "📊 Live " + String.format("%02d", i);

                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    long totalMb = mi.totalMem / (1024L * 1024L);
                    long availMb = mi.availMem / (1024L * 1024L);
                    long usedMb  = totalMb - availMb;

                    line += " |  CPU: " + cpuText
                            + "  |  RAM: " + usedMb + " MB / " + totalMb + " MB";
                } else {
                    line += " |  CPU: " + cpuText + "  |  RAM: N/A";
                }

                final String out = line;

                runOnUiThread(() -> {
                    if (txtLive != null) {
                        String prev = txtLive.getText() == null
                                ? ""
                                : txtLive.getText().toString();
                        txtLive.setText(prev.isEmpty() ? out : prev + "\n" + out);
                    }
                });

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    break;
                }
            }

            runOnUiThread(() -> {
                if (txtLive != null) {
                    String prev = txtLive.getText() == null
                            ? ""
                            : txtLive.getText().toString();
                    txtLive.setText(prev + "\n\n✅ CPU+RAM live finished.");
                }
            });
        });

        t.setDaemon(true);
        t.start();
    }

    /**
     * Διαβάζει /proc/stat και υπολογίζει CPU usage % από τα deltas.
     * Επιστρέφει -1 αν κάτι πάει στραβά.
     */
    private double readCpuUsage() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/stat"));
            String line = reader.readLine(); // πρώτη γραμμή: cpu ...
            if (line == null || !line.startsWith("cpu ")) {
                return -1;
            }

            String[] toks = line.trim().split("\\s+");
            // cpu user nice system idle iowait irq softirq steal guest guest_nice
            //      1    2    3     4    5      6   7       8     9    10   11
            long user    = Long.parseLong(toks[1]);
            long nice    = Long.parseLong(toks[2]);
            long system  = Long.parseLong(toks[3]);
            long idle    = Long.parseLong(toks[4]);
            long iowait  = toks.length > 5 ? Long.parseLong(toks[5]) : 0L;
            long irq     = toks.length > 6 ? Long.parseLong(toks[6]) : 0L;
            long softirq = toks.length > 7 ? Long.parseLong(toks[7]) : 0L;
            long steal   = toks.length > 8 ? Long.parseLong(toks[8]) : 0L;

            long idleAll  = idle + iowait;
            long nonIdle  = user + nice + system + irq + softirq + steal;
            long total    = idleAll + nonIdle;

            if (prevTotal < 0 || prevIdle < 0) {
                // πρώτη μέτρηση: απλώς αποθήκευση
                prevTotal = total;
                prevIdle  = idleAll;
                return -1; // δεν έχουμε ακόμη delta
            }

            long totalDiff = total - prevTotal;
            long idleDiff  = idleAll - prevIdle;

            prevTotal = total;
            prevIdle  = idleAll;

            if (totalDiff <= 0) return -1;

            // usage = (totalDiff - idleDiff) / totalDiff
            return (totalDiff - idleDiff) * 100.0 / totalDiff;

        } catch (Exception e) {
            return -1;
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }
}
