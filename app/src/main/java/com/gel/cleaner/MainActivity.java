package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    TextView txtLogs;
    ScrollView scroll;

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

        log("✅ Device is ready • Dark-Gold Edition", false);
    }


    /* =========================================
     *              LANGUAGE
     * ========================================= */
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


    /* =========================================
     *                DONATE
     * ========================================= */
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


    /* =========================================
     *              CLEANER BUTTONS
     * ========================================= */
    private void setupCleanerButtons() {

        // SYSTEM
        bind(R.id.btnCpuRamInfo, () ->
                GELCleaner.cpuInfo(this, this));

        bind(R.id.btnCpuRamLive, () ->
                GELCleaner.cpuLive(this, this));


        // CLEANER
        bind(R.id.btnCleanRam, () ->
                GELCleaner.cleanRAM(this, this));

        bind(R.id.btnSafeClean, () ->
                GELCleaner.safeClean(this, this));

        bind(R.id.btnDeepClean, () ->
                GELCleaner.deepClean(this, this));


        // JUNK
        bind(R.id.btnMediaJunk, () ->
                GELCleaner.mediaJunk(this, this));

        bind(R.id.btnBrowserCache, () ->
                GELCleaner.browserCache(this, this));

        bind(R.id.btnTemp, () ->
                GELCleaner.tempClean(this, this));


        // ✅ APP CACHE LIST
        bind(R.id.btnAppCache, () ->
                startActivity(new Intent(this, AppListActivity.class)));


        // PERFORMANCE
        bind(R.id.btnBatteryBoost, () ->
                GELCleaner.boostBattery(this, this));

        bind(R.id.btnKillApps, () ->
                GELCleaner.killApps(this, this));


        // ALL
        bind(R.id.btnCleanAll, () ->
                GELCleaner.cleanAll(this, this));
    }


    private void bind(int id, Runnable fn) {
        Button b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> fn.run());
    }


    /* =========================================
     *             LOG CALLBACK
     * ========================================= */
    @Override
    public void log(String msg, boolean isError) {

        runOnUiThread(() -> {
            String old = txtLogs.getText().toString();
            txtLogs.setText(old + "\n" + msg);

            // ✅ AUTO-SCROLL
            if (scroll != null)
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }
}
