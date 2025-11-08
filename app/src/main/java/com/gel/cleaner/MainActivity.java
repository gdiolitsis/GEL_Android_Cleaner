package com.gel.cleaner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    TextView txtLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ✅ Apply locale BEFORE super
        boolean greek = getSharedPreferences("cfg", MODE_PRIVATE)
                .getBoolean("gr", false);
        setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtLogs = findViewById(R.id.txtLogs);

        // ✅ DONATE → PayPal
        Button donateButton = findViewById(R.id.btnDonate);
        if (donateButton != null) {
            donateButton.setOnClickListener(v -> {
                Intent i = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.paypal.com/paypalme/gdiolitsis")
                );
                startActivity(i);
            });
        }

        // ✅ LANGUAGE BUTTONS
        Button gr = findViewById(R.id.btnLangGR);
        Button en = findViewById(R.id.btnLangEN);

        if (gr != null) {
            gr.setOnClickListener(v -> {
                getSharedPreferences("cfg", MODE_PRIVATE)
                        .edit().putBoolean("gr", true).apply();
                recreate();
            });
        }

        if (en != null) {
            en.setOnClickListener(v -> {
                getSharedPreferences("cfg", MODE_PRIVATE)
                        .edit().putBoolean("gr", false).apply();
                recreate();
            });
        }

        // ✅ Bind buttons
        bind(R.id.btnCpuInfo, () -> GELCleaner.cpuInfo(this, this));
        bind(R.id.btnCpuLive, () -> GELCleaner.cpuLive(this, this));
        bind(R.id.btnSafeClean, () -> GELCleaner.safeClean(this, this));
        bind(R.id.btnDeepClean, () -> GELCleaner.deepClean(this, this));
        bind(R.id.btnMediaJunk, () -> GELCleaner.mediaJunk(this, this));
        bind(R.id.btnBrowserCache, () -> GELCleaner.browserCache(this, this));
        bind(R.id.btnTemp, () -> GELCleaner.tempClean(this, this));
        bind(R.id.btnCleanRam, () -> GELCleaner.cleanRAM(this, this));
        bind(R.id.btnBatteryBoost, () -> GELCleaner.boostBattery(this, this));
        bind(R.id.btnKillApps, () -> GELCleaner.killApps(this, this));
        bind(R.id.btnCleanAll, () -> GELCleaner.cleanAll(this, this));
    }

    private void bind(int id, Runnable fn) {
        Button b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> fn.run());
    }

    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            String old = txtLogs.getText().toString();
            txtLogs.setText(old + "\n" + msg);
            if (isError) {
                txtLogs.setTextColor(getColor(android.R.color.holo_red_light));
            }
        });
    }
}
