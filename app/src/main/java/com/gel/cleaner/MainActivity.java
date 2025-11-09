package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    private TextView txtLogs;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Μην αλλάζεις theme εδώ — χρησιμοποιούμε αυτό από styles.xml
        setContentView(R.layout.activity_main);

        txtLogs = findViewById(R.id.txtLogs);
        log("✅ DEVICE READY • Dark-Gold Edition v4", false);

        setupLanguageButtons();
        setupDonate();
        setupCleanerButtons();
    }

    private void setupLanguageButtons() {
        View bGR = findViewById(R.id.btnLangGR);
        View bEN = findViewById(R.id.btnLangEN);

        if (bEN != null) {
            bEN.setOnClickListener(v -> {
                LocaleHelper.set(this, "en");
                recreate();
            });
        }
        if (bGR != null) {
            bGR.setOnClickListener(v -> {
                LocaleHelper.set(this, "el");
                recreate();
            });
        }
    }

    private void setupDonate() {
        Button donate = findViewById(R.id.btnDonate);
        if (donate != null) {
            donate.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.paypal.com/paypalme/gdiolitsis"));
                startActivity(i);
            });
        }
    }

    private void setupCleanerButtons() {
        bindClick(R.id.btnCpuInfo,      () -> GELCleaner.cpuInfo(this, this));
        bindClick(R.id.btnCpuLive,      () -> GELCleaner.cpuLive(this, this));

        bindClick(R.id.btnCleanRam,     () -> GELCleaner.cleanRAM(this, this));
        bindClick(R.id.btnSafeClean,    () -> GELCleaner.safeClean(this, this));
        bindClick(R.id.btnDeepClean,    () -> GELCleaner.deepClean(this, this));

        bindClick(R.id.btnMediaJunk,    () -> GELCleaner.mediaJunk(this, this));
        bindClick(R.id.btnBrowserCache, () -> GELCleaner.browserCache(this, this));
        bindClick(R.id.btnTemp,         () -> GELCleaner.tempClean(this, this));

        bindClick(R.id.btnBatteryBoost, () -> GELCleaner.boostBattery(this, this));
        bindClick(R.id.btnKillApps,     () -> GELCleaner.killApps(this, this));

        bindClick(R.id.btnCleanAll,     () -> GELCleaner.cleanAll(this, this));
    }

    private void bindClick(int id, Runnable fn) {
        View v = findViewById(id);
        if (v != null) v.setOnClickListener(x -> fn.run());
    }

    // ===== GELCleaner.LogCallback =====
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;
            String old = txtLogs.getText() == null ? "" : txtLogs.getText().toString();
            txtLogs.setText(old + (old.isEmpty() ? "" : "\n") + msg);
            // Προαιρετικά: χρωματίζουμε μόνο όταν έρχεται error
            // (κρατάμε το default χρώμα για OK)
            if (isError) {
                txtLogs.setTextColor(getColor(android.R.color.holo_red_light));
            }
        });
    }
}
