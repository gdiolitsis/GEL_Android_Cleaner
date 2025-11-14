package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
        setupButtons();

        log("📱 Device ready", false);
    }

    /* =========================================================
     * LANGUAGE
     * ========================================================= */
    private void setupLangButtons() {
        View bGR = findViewById(R.id.btnLangGR);
        View bEN = findViewById(R.id.btnLangEN);

        if (bGR != null) bGR.setOnClickListener(v -> { LocaleHelper.set(this,"el"); recreate(); });
        if (bEN != null) bEN.setOnClickListener(v -> { LocaleHelper.set(this,"en"); recreate(); });
    }

    /* =========================================================
     * DONATE
     * ========================================================= */
    private void setupDonate() {
        View b = findViewById(R.id.btnDonate);
        if (b != null) {
            b.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.paypal.com/paypalme/gdiolitsis")));
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open browser", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /* =========================================================
     * BUTTON MAPPING
     * ========================================================= */
    private void setupButtons() {

        // INTERNAL DEVICE INFO
        bind(R.id.btnPhoneInfoInternal,
                () -> startActivity(new Intent(this, DeviceInfoInternalActivity.class)));

        // PERIPHERALS DEVICE INFO
        bind(R.id.btnPhoneInfoPeripherals,
                () -> startActivity(new Intent(this, DeviceInfoPeripheralsActivity.class)));

        // CPU/RAM LIVE
        bind(R.id.btnCpuRamLive,
                () -> GELCleaner.cpuLive(this, this));

        // CLEANING ACTIONS
        bind(R.id.btnCleanRam,
                () -> GELCleaner.cleanRAM(this, this));

        bind(R.id.btnDeepClean,
                () -> GELCleaner.deepClean(this, this));

        bind(R.id.btnBrowserCache,
                () -> GELCleaner.browserCache(this, this));

        bind(R.id.btnTemp,
                () -> GELCleaner.tempFiles(this, this));

        // PERFORMANCE
        bind(R.id.btnBatteryBoost,
                () -> GELCleaner.openRunningApps(this, this));

        bind(R.id.btnKillApps,
                () -> GELCleaner.openRunningApps(this, this));

        // APP CACHE LIST
        View appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {
            appCache.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, AppListActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open App List", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void bind(int id, Runnable fn) {
        View b = findViewById(id);
        if (b != null) {
            b.setOnClickListener(v -> {
                try { fn.run(); }
                catch (Throwable t) {
                    Toast.makeText(this, "Action failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /* =========================================================
     * LOGGING
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;

            String prev = txtLogs.getText() == null ? "" : txtLogs.getText().toString();
            txtLogs.setText(prev.isEmpty() ? msg : prev + "\n" + msg);

            if (scroll != null)
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }
}
