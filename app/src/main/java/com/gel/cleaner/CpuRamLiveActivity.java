// GDiolitsis Engine Lab (GEL) — FINAL v21.0
// CpuRamLiveActivity.java — Gold Title + CPU Neon + TEMP Neon + RAM USED Neon + Core Button Gold/Bordo

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

    private TextView txtLive, txtTitle;
    private boolean running = true;

    public native int getCpuUsageNative();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        txtLive  = findViewById(R.id.txtLiveInfo);
        txtTitle = findViewById(R.id.txtTitleLive);

        // 🔥 GOLD TITLE
        txtTitle.setTextColor(getColor(R.color.gel_gold));

        // 🔥 GOLD BUTTON WITH BORDEAU BORDER
        Button btnCore = findViewById(R.id.btnCoreMonitor);
        btnCore.setBackgroundResource(R.drawable.gel_btn_gold_bordo);
        btnCore.setTextColor(getColor(R.color.black));

        btnCore.setOnClickListener(v ->
                startActivity(new Intent(this, CoreMonitorActivity.class))
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

                String cpuColored =
                        "<font color='#00FF66'>" + info.percent + "%</font> [" + info.name + "]";

                String tempColored =
                        "<font color='#00FF66'>" + readCpuTemp() + "</font>";

                String ram = readRamUsage();
                String used = ram.split("/")[0].trim();

                String ramColored =
                        "<font color='#00FF66'>" + used + "</font> / " + ram.split("/")[1].trim();

                String html =
                        "Live " + String.format("%02d", counter) +
                        " | CPU: " + cpuColored +
                        " | Temp: " + tempColored +
                        " | RAM: " + ramColored;

                runOnUiThread(() ->
                        txtLive.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY))
                );

                counter = (counter + 1 > 999) ? 1 : counter + 1;

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }

        }).start();
    }

    private static class EngineInfo {
        public final int percent;
        public final String name;

        private EngineInfo(int p, String n) {
            percent = p;
            name = n;
        }

        public static EngineInfo decode(int v) {
            if (v >= 0 && v <= 100) return new EngineInfo(v, "RAW");
            if (v >= 1000 && v <= 1100) return new EngineInfo(v - 1000, "FREQ");
            if (v >= 2000 && v <= 2100) return new EngineInfo(v - 2000, "THERMAL");
            return new EngineInfo(0, "N/A");
        }
    }

    private String readCpuTemp() {
        try {
            Intent intent = registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (intent == null) return "N/A";

            int t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            return (t > 0) ? (t / 10f) + "°C" : "N/A";
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

            return used + " MB / " + total + " MB";
        } catch (Exception e) {
            return "N/A";
        }
    }
}
