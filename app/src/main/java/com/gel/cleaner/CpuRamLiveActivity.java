// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v9.0 (Universal CPU/RAM Monitor)

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;

public class CpuRamLiveActivity extends GELAutoActivityHook {

    private TextView txtOutput;
    private Handler handler = new Handler();
    private int index = 1;

    private long lastIdle = 0;
    private long lastTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtOutput = findViewById(R.id.txtOutput);

        txtOutput.setText("CPU / RAM Live Monitor started...\n");

        startLiveLoop();
    }

    private void startLiveLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                int cpu = getCpuUsage();
                float temp = getBatteryTemp();
                String ram = getRamInfo();

                txtOutput.append(
                        "Live " + (index < 10 ? "0" + index : index)
                                + "  |  CPU: " + (cpu < 0 ? "N/A" : cpu + "%")
                                + "  |  Temp: " + (temp < 0 ? "N/A" : temp + "°C")
                                + "  |  RAM: " + ram + "\n"
                );

                index++;
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    // ============================================================
    // UNIVERSAL CPU USAGE (WORKS ON ALL DEVICES)
    // ============================================================
    private int getCpuUsage() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/stat"));
            String line = br.readLine();
            br.close();

            if (line == null || !line.startsWith("cpu")) return -1;

            String[] toks = line.trim().split("\\s+");
            long user = Long.parseLong(toks[1]);
            long nice = Long.parseLong(toks[2]);
            long system = Long.parseLong(toks[3]);
            long idle = Long.parseLong(toks[4]);

            long total = user + nice + system + idle;

            long diffIdle = idle - lastIdle;
            long diffTotal = total - lastTotal;

            lastIdle = idle;
            lastTotal = total;

            if (diffTotal == 0) return 0;

            return (int) (100 * (diffTotal - diffIdle) / diffTotal);

        } catch (Exception e) {
            return -1;
        }
    }

    // ============================================================
    // RAM INFO — UNIVERSAL
    // ============================================================
    private String getRamInfo() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long total = mi.totalMem / 1048576;
            long avail = mi.availMem / 1048576;

            return avail + " / " + total + " MB";
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ============================================================
    // TEMPERATURE — UNIVERSAL
    // ============================================================
    private float getBatteryTemp() {
        try {
            long t = readTemp("/sys/class/power_supply/battery/temp");
            if (t < 0) return -1;

            if (t > 1000) return t / 1000f; // m°C
            else return t / 10f;           // 1/10°C

        } catch (Exception e) {
            return -1;
        }
    }

    private long readTemp(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String s = br.readLine();
            br.close();
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
