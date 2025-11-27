// GDiolitsis Engine Lab (GEL) — v21.0 FINAL
// CPU/RAM LIVE — Vertical Layout + Neon Colors

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
                startActivity(new Intent(this, CoreMonitorActivity.class))
        );

        startLoop();
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }

    private void startLoop() {
        new Thread(() -> {

            int counter = 1;

            while (running) {

                int cpuVal = getCpuUsageNative();
                EngineInfo info = EngineInfo.decode(cpuVal);

                // CPU neon
                String cpu = "<font color='#00FF66'>" + info.percent + "%</font>";

                // TEMP neon
                String temp = "<font color='#00FF66'>" + readCpuTemp() + "</font>";

                // RAM neon (USED only)
                String ramRaw = readRamUsage();   // "2131 / 3479 MB"
                String[] p = ramRaw.split(" ");

                String used = "<font color='#00FF66'>" + p[0] + "</font>";
                String slash = p[1];
                String total = p[2];

                // ================================
                //  NEW VERTICAL FORMAT
                // ================================
                String html =
                        "Live " + counter + "<br><br>" +
                        "CPU: " + cpu + "<br>" +
                        "TEMP: " + temp + "<br>" +
                        "RAM: " + used + " " + slash + " " + total;

                runOnUiThread(() ->
                        txtLive.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY))
                );

                counter++;
                if (counter > 999) counter = 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }

        }).start();
    }

    // =============================
    // INTERNAL HELPERS
    // =============================

    private static class EngineInfo {
        public final int percent;
        public final String name;

        EngineInfo(int p, String n) {
            percent = p;
            name = n;
        }

        static EngineInfo decode(int v) {
            if (v >= 0 && v <= 100) return new EngineInfo(v, "RAW");
            if (v >= 1000 && v <= 1100) return new EngineInfo(v - 1000, "FREQ");
            if (v >= 2000 && v <= 2100) return new EngineInfo(v - 2000, "THERMAL");
            return new EngineInfo(0, "N/A");
        }
    }

    private String readCpuTemp() {
        try {
            Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (intent == null) return "N/A";

            int t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            if (t > 0) return (t / 10f) + "°C";
            return "N/A";

        } catch (Exception e) {
            return "N/A";
        }
    }

    private String readRamUsage() {
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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
