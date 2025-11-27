// GDiolitsis Engine Lab (GEL) — Author & Developer
// CpuRamLiveActivity.java — FINAL v13.0 (JNI RAW /proc/stat CPU% + Temp + RAM)
// NOTE: Full ready-to-paste file per GEL rule — no manual edits needed in this file.

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CpuRamLiveActivity extends AppCompatActivity {

    static {
        // φορτώνει τη native βιβλιοθήκη libcpustat.so
        System.loadLibrary("cpustat");
    }

    // Native method: επιστρέφει 0–100 ή -1 σε σφάλμα
    private native int getCpuUsageNative();

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

                int cpuVal = getCpuUsageNative();
                String cpu = (cpuVal >= 0 && cpuVal <= 100) ? (cpuVal + "%") : "N/A";

                String temp = readCpuTemp();
                String ram  = readRamUsage();

                final String line =
                        "Live " + String.format("%02d", counter) +
                        " | CPU: " + cpu +
                        " | Temp: " + temp +
                        " | RAM: " + ram;

                runOnUiThread(() -> txtLive.append(line + "\n"));

                counter++;
                if (counter > 999) counter = 1;

                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    // ============================================================
    // CPU TEMP (Battery temperature)
    // ============================================================
    private String readCpuTemp() {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent = registerReceiver(null, filter);
            if (intent == null) return "N/A";

            int temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (temp > 0) {
                float c = temp / 10f;
                return c + "°C";
            } else {
                return "N/A";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ============================================================
    // RAM USAGE (used / total MB)
    // ============================================================
    private String readRamUsage() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) return "N/A";

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
