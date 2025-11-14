package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.RandomAccessFile;

public class CpuRamLiveActivity extends AppCompatActivity {

    TextView live;
    Handler handler = new Handler();
    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        live = findViewById(R.id.txtLiveInfo);

        startMonitoring();
    }

    private void startMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String ramText = getRamInfo();
                String cpuText = getCpuUsage();

                String line = "📊 Live " + String.format("%02d", counter) +
                        " | CPU: " + cpuText +
                        " | RAM: " + ramText + "\n";

                live.append(line);
                counter++;

                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // ---------------- RAM ----------------
    private String getRamInfo() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long total = mi.totalMem / (1024 * 1024);
        long free = mi.availMem / (1024 * 1024);
        long used = total - free;

        return used + " MB / " + total + " MB";
    }

    // ---------------- CPU ----------------
    private String getCpuUsage() {
        try {
            long[] first = readCpuStat();
            Thread.sleep(240);
            long[] second = readCpuStat();

            long idle = second[0] - first[0];
            long cpu  = (second[1] - first[1]) + idle;

            if (cpu == 0) return "0%";

            int usage = (int)((second[1] - first[1]) * 100L / cpu);
            if (usage < 0) usage = 0;
            if (usage > 100) usage = 100;

            return usage + "%";

        } catch (Exception e) {
            return "N/A";
        }
    }

    private long[] readCpuStat() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            reader.close();

            String[] t = load.split("\\s+");

            long idle = Long.parseLong(t[4]);
            long nonIdle =
                    Long.parseLong(t[1]) +
                    Long.parseLong(t[2]) +
                    Long.parseLong(t[3]) +
                    Long.parseLong(t[5]) +
                    Long.parseLong(t[6]) +
                    Long.parseLong(t[7]);

            return new long[]{idle, nonIdle};

        } catch (Exception e) {
            return new long[]{0, 0};
        }
    }
}
