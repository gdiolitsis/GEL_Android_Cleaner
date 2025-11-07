package com.gel.cleaner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvLogs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_GEL);
        setContentView(R.layout.activity_main);

        tvLogs = findViewById(R.id.tvLogs);

        // Language toggles (placeholders)
        ImageView el = findViewById(R.id.btnLangEL);
        ImageView en = findViewById(R.id.btnLangEN);
        el.setOnClickListener(v -> log("Language → EL"));
        en.setOnClickListener(v -> log("Language → EN"));

        // Donate
        findViewById(R.id.btnDonate).setOnClickListener(v -> log("Donate pressed"));

        // System
        bindClick(R.id.btnCpuInfo, "CPU INFO");
        bindClick(R.id.btnCpuLive, "CPU LIVE");

        // Cleaner
        bindClick(R.id.btnCleanRam, "CLEAN RAM");
        bindClick(R.id.btnSafeClean, "SAFE CLEAN");
        bindClick(R.id.btnDeepClean, "DEEP CLEAN");

        // Junk
        bindClick(R.id.btnMediaJunk, "MEDIA JUNK");
        bindClick(R.id.btnBrowserCache, "BROWSER CACHE");
        bindClick(R.id.btnTemp, "TEMP");

        // Performance
        bindClick(R.id.btnBattery, "BATTERY BOOST");
        bindClick(R.id.btnKillApps, "KILL APPS");
        bindClick(R.id.btnCleanAll, "CLEAN ALL");
    }

    private void bindClick(int id, String label) {
        Button b = findViewById(id);
        b.setOnClickListener(v -> {
            log(label + " • started");
            // TODO: connect with GELCleaner actions
        });
    }

    private void log(String msg) {
        String prev = tvLogs.getText() == null ? "" : tvLogs.getText().toString();
        tvLogs.setText("• " + msg + "\n" + prev);
    }
}
