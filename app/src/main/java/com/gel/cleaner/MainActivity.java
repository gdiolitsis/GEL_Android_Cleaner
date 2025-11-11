package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SAFCleaner.LogCallback {

    private TextView txtLogs;
    private ScrollView scroll;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLogs = findViewById(R.id.txtLogs);
        scroll  = findViewById(R.id.scrollRoot);

        setupLangButtons();
        setupDonate();
        setupCleanerButtons();

        log(getString(R.string.device_ready), false);
    }

    /* ================= LANGUAGE ==================== */
    private void setupLangButtons() {
        Button bGR = findViewById(R.id.btnLangGR);
        Button bEN = findViewById(R.id.btnLangEN);

        if (bGR != null) bGR.setOnClickListener(v -> {
            LocaleHelper.set(this, "el");
            recreate();
        });

        if (bEN != null) bEN.setOnClickListener(v -> {
            LocaleHelper.set(this, "en");
            recreate();
        });
    }

    /* ================= DONATE ====================== */
    private void setupDonate() {
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
    }

    /* ================= CLEAN BUTTONS ================ */
    private void setupCleanerButtons() {

        // CPU + RAM
        bind(R.id.btnCpuRamInfo, () ->
                SAFCleaner.cpuInfo(this, this));

        bind(R.id.btnCpuRamLive, () ->
                SAFCleaner.cpuLive(this, this));   // safe loop

        // Cleaner
        bind(R.id.btnCleanRam,   () ->
                SAFCleaner.cleanRAM(this, this));

        bind(R.id.btnSafeClean,  () ->
                SAFCleaner.safeClean(this, this));

        bind(R.id.btnDeepClean,  () ->
                SAFCleaner.deepClean(this, this));

        // Junk
        bind(R.id.btnMediaJunk, () ->
                SAFCleaner.mediaJunk(this, this));

        bind(R.id.btnBrowserCache, () ->
                SAFCleaner.browserCache(this, this));

        bind(R.id.btnTemp, () ->
                SAFCleaner.tempClean(this, this));

        // App Cache (List)
        Button appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {
            appCache.setOnClickListener(v ->
                    startActivity(new Intent(this, AppListActivity.class)));
        }

        // Performance
        bind(R.id.btnBatteryBoost, () ->
                SAFCleaner.boostBattery(this, this));

        bind(R.id.btnKillApps, () ->
                SAFCleaner.killApps(this, this));

        // All
        bind(R.id.btnCleanAll, () ->
                SAFCleaner.cleanAll(this, this));
    }

    private void bind(int id, Runnable fn) {
        Button b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> fn.run());
    }

    /* ================= LOG CALLBACK ================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            String old = txtLogs.getText().toString();
            txtLogs.setText(old + "\n" + msg);

            if (scroll != null) {
                scroll.post(() ->
                        scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }
}
