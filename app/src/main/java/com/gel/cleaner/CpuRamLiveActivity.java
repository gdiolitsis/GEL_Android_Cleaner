// GDiolitsis Engine Lab (GEL) — FINAL v20.0
// CpuRamLiveActivity.java — CPU Neon + TEMP Neon + RAM USED Only Neon

package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CpuRamLiveActivity extends AppCompatActivity {

    static {
        System.loadLibrary("cpustat");
    }

    private TextView txtLive;
    private boolean running = true;

    public native int getCpuUsageNative();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive = findViewById(R.id.txtLiveInfo);

        Button btnCore = findViewById(R.id.btnCoreMonitor);
        btnCore.setOnClickListener(v ->
                startActivity(new Intent(CpuRamLiveActivity.this, CoreMonitorActivity.class))
        );

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
                EngineInfo info = EngineInfo.decode(cpuVal);

                // CPU % NEON
                String cpuColored =
                        "<font color='#00FF66'>" + info.percent + "%</font>" +
                                " [" + info.name + "]";

                // TEMP NEON
                String tempColored =
                        "<font color='#00FF66'>" + readCpuTemp() + "</font>";

                // ================================
                // RAM USED ONLY → NEON GREEN
                // ================================
                String ramRaw = readRamUsage();         // e.g. "2234 / 3479 MB"
                String[] parts = ramRaw.split(" ");

                String used = parts[0];                 // 2234
                String slash = parts[1];                // /
                String total = parts[2];                // 3479

                String ramColored =
                        "<font color='#00FF66'>" + used + "</font> " +
                                slash + " " + total;

                String htmlLine =
                        "Live " + String.format("%02d", counter) +
                                " | CPU: " + cpuColored +
                                " | Temp: " + tempColored +
                                " | RAM: " + ramColored;

                runOnUiThread(() ->
                        txtLive.setText(Html.fromHtml(htmlLine, Html.FROM_HTML_MODE_LEGACY))
                );

                counter++;
                if (counter > 999) counter = 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }

        }).start();
    }

    private static class EngineInfo {
        public final int percent;
        public final String name;

        private EngineInfo(int p, String n) {
            this.percent = p;
            this.name = n;
        }

        public static EngineInfo decode(int cpuVal) {

            if (cpuVal >= 0 && cpuVal <= 100) {
                return new EngineInfo(cpuVal, "RAW");
            }

            if (cpuVal >= 1000 && cpuVal <= 1100) {
                return new EngineInfo(cpuVal - 1000, "FREQ");
            }

            if (cpuVal >= 2000 && cpuVal <= 2100) {
                return new EngineInfo(cpuVal - 2000, "THERMAL");
            }

            return new EngineInfo(0, "N/A");
        }
    }

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
