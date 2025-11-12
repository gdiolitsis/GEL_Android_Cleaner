package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

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

        ensurePermissions();

        log(getString(R.string.device_ready), false);
    }

    /* =========================================================
     * LANGUAGE
     * ========================================================= */
    private void setupLangButtons() {

        View bGR = findViewById(R.id.btnLangGR);
        View bEN = findViewById(R.id.btnLangEN);

        if (bGR != null)
            bGR.setOnClickListener(v -> {
                LocaleHelper.set(this, "el");
                recreate();
            });

        if (bEN != null)
            bEN.setOnClickListener(v -> {
                LocaleHelper.set(this, "en");
                recreate();
            });
    }


    /* =========================================================
     * DONATE
     * ========================================================= */
    private void setupDonate() {
        View donateButton = findViewById(R.id.btnDonate);
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


    /* =========================================================
     * CLEAN BUTTONS
     * ========================================================= */
    private void setupCleanerButtons() {

        // CPU + RAM
        bind(R.id.btnCpuRamInfo,
                () -> GELCleaner.cpuInfo(this, this));

        bind(R.id.btnCpuRamLive,
                () -> GELCleaner.cpuLive(this, this));

        // Cleaner
        bind(R.id.btnCleanRam,
                () -> GELCleaner.cleanRAM(this, this));

        bind(R.id.btnSafeClean,
                () -> GELCleaner.safeClean(this, this));

        bind(R.id.btnDeepClean,
                () -> GELCleaner.deepClean(this, this));

        // Junk
        bind(R.id.btnMediaJunk,
                () -> GELCleaner.mediaJunk(this, this));

        bind(R.id.btnBrowserCache,
                () -> GELCleaner.browserCache(this, this));

        bind(R.id.btnTemp,
                () -> GELCleaner.tempClean(this, this));

        // App Cache → open activity
        View appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {
            appCache.setOnClickListener(v ->
                    startActivity(new Intent(this, AppListActivity.class)));
        }

        // Performance
        bind(R.id.btnBatteryBoost,
                () -> GELCleaner.boostBattery(this, this));

        bind(R.id.btnKillApps,
                () -> GELCleaner.killApps(this, this));

        // All
        bind(R.id.btnCleanAll,
                () -> GELCleaner.cleanAll(this, this));
    }


    private void bind(int id, Runnable fn) {
        View b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> fn.run());
    }


    /* =========================================================
     * PERMISSIONS ENTRY
     * ========================================================= */
    private void ensurePermissions() {

        // Storage SAF
        if (!SAFCleaner.hasTree(this)) {
            log("⚠️ SAF missing → open picker…", false);
            startActivity(new Intent(this, PermissionsActivity.class));
        }

        // PACKAGE_USAGE_STATS
        if (!PermissionHelper.hasUsageAccess(this)) {
            log("⚠️ Usage access missing", true);
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        // Accessibility
        if (!PermissionHelper.hasAccessibility(this)) {
            log("⚠️ Accessibility not enabled", true);
        }
    }


    /* =========================================================
     * LOG CALLBACK
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;

            String old = txtLogs.getText().toString();
            txtLogs.setText(old + "\n" + msg);

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }
}
