package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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

        if (bGR != null) bGR.setOnClickListener(v -> { LocaleHelper.set(this, "el"); recreate(); });
        if (bEN != null) bEN.setOnClickListener(v -> { LocaleHelper.set(this, "en"); recreate(); });
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

        // CPU + RAM LIVE
        bind(R.id.btnCpuRamLive,
                () -> startActivity(new Intent(this, CpuRamLiveActivity.class)));

        // NEW — SINGLE CLEANER BUTTON
        bind(R.id.btnCleanAll,
                () -> GELCleaner.deepClean(this, this));

        // 👉 NEW POPUP FOR BROWSERS
        bind(R.id.btnBrowserCache,
                this::showBrowserPicker);

        // TEMP FILES
        bind(R.id.btnTemp,
                () -> GELCleaner.tempFiles(this, this));

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

        // BATTERY BOOST (opens Running Apps)
        bind(R.id.btnBatteryBoost,
                () -> GELCleaner.openRunningApps(this, this));

        // KILL APPS
        bind(R.id.btnKillApps,
                () -> GELCleaner.openRunningApps(this, this));
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
     * POPUP BROWSER LIST
     * ========================================================= */
    private void showBrowserPicker() {

        PackageManager pm = getPackageManager();
        String[] candidates = {
                "com.android.chrome",
                "com.chrome.beta",
                "org.mozilla.firefox",
                "com.opera.browser",
                "com.microsoft.emmx",
                "com.brave.browser",
                "com.vivaldi.browser",
                "com.duckduckgo.mobile.android",
                "com.sec.android.app.sbrowser"
        };

        List<String> installed = new ArrayList<>();

        for (String pkg : candidates) {
            try {
                pm.getPackageInfo(pkg, 0);
                installed.add(pkg);
            } catch (Exception ignored) {}
        }

        if (installed.isEmpty()) {
            Toast.makeText(this, "No browsers installed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Only 1 browser → go directly
        if (installed.size() == 1) {
            openAppInfo(installed.get(0));
            return;
        }

        // Many browsers → create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Browser");

        String[] labels = installed.toArray(new String[0]);

        builder.setItems(labels, (dialog, which) -> {
            openAppInfo(installed.get(which));
        });

        builder.show();
    }

    private void openAppInfo(String pkg) {
        try {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + pkg));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open App Info", Toast.LENGTH_SHORT).show();
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
