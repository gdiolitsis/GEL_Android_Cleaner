package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    private TextView txtLogs;
    private ScrollView scroll;

    private ActivityResultLauncher<Intent> safPicker;

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

        initSafPicker();
        setupLangButtons();
        setupDonate();
        setupCleanerButtons();

        ensurePermissions();

        log(getString(R.string.device_ready), false);
    }


    /* =========================================================
     * SAF PICKER INIT
     * ========================================================= */
    private void initSafPicker() {
        safPicker =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getData() == null) return;
                            Uri tree = result.getData().getData();
                            if (tree != null) {
                                SAFCleaner.saveTreeUri(this, tree);
                                log("✅ SAF granted", false);
                            }
                        }
                );
    }

    /* =========================================================
     * LANGUAGE
     * ========================================================= */
    private void setupLangButtons() {
        View bGR = findViewById(R.id.btnLangGR);
        View bEN = findViewById(R.id.btnLangEN);

        if (bGR != null) {
            bGR.setOnClickListener(v -> {
                if (!"el".equals(getCurrentLang())) {
                    LocaleHelper.set(this, "el");
                    recreate();
                }
            });
        }

        if (bEN != null) {
            bEN.setOnClickListener(v -> {
                if (!"en".equals(getCurrentLang())) {
                    LocaleHelper.set(this, "en");
                    recreate();
                }
            });
        }
    }

    private String getCurrentLang() {
        Context c = LocaleHelper.apply(this);
        return c.getResources().getConfiguration().getLocales().get(0).getLanguage();
    }


    /* =========================================================
     * DONATE
     * ========================================================= */
    private void setupDonate() {
        View donateButton = findViewById(R.id.btnDonate);
        if (donateButton != null) {
            donateButton.setOnClickListener(v -> {
                try {
                    Intent i = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.paypal.com/paypalme/gdiolitsis")
                    );
                    startActivity(i);
                } catch (Exception e) {
                    log("❌ Cannot open browser: " + e.getMessage(), true);
                }
            });
        }
    }

    /* =========================================================
     * CLEAN BUTTONS
     * ========================================================= */
    private void setupCleanerButtons() {

        // CPU + RAM
        bind(R.id.btnCpuRamInfo, () -> GELCleaner.cpuInfo(this, this));
        bind(R.id.btnCpuRamLive, () -> GELCleaner.cpuLive(this, this));

        // Cleaner
        bind(R.id.btnCleanRam,  () -> GELCleaner.cleanRAM(this, this));
        bind(R.id.btnSafeClean, () -> GELCleaner.safeClean(this, this));
        bind(R.id.btnDeepClean, () -> GELCleaner.deepClean(this, this));

        // Junk
        bind(R.id.btnMediaJunk,   () -> GELCleaner.mediaJunk(this, this));
        bind(R.id.btnBrowserCache,() -> GELCleaner.browserCache(this, this));
        bind(R.id.btnTemp,        () -> GELCleaner.tempClean(this, this));

        // App Cache → open activity
        View appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {
            appCache.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, AppListActivity.class));
                } catch (Exception e) {
                    log("❌ Cannot open App List: " + e.getMessage(), true);
                }
            });
        }

        // Performance
        bind(R.id.btnBatteryBoost, () -> GELCleaner.boostBattery(this, this));
        bind(R.id.btnKillApps,     () -> GELCleaner.killApps(this, this));

        // All
        bind(R.id.btnCleanAll,     () -> GELCleaner.cleanAll(this, this));
    }

    private void bind(int id, Runnable fn) {
        View b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> {
            try { fn.run(); }
            catch (Throwable t) { log("❌ Action failed: " + t.getMessage(), true); }
        });
    }


    /* =========================================================
     * PERMISSIONS ENTRY
     * ========================================================= */
    private void ensurePermissions() {

        // === SAF ===
        if (!SAFCleaner.hasTree(this)) {
            log("⚠️ SAF missing → requesting…", false);

            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            try {
                safPicker.launch(i);
            } catch (Exception e) {
                log("❌ SAF picker failed: " + e.getMessage(), true);
            }
        }

        // === Usage Access (optional) ===
        if (!hasUsageAccess()) {
            log("⚠️ Usage access missing", false);
            try {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                log("❌ Cannot open Usage Access settings: " + e.getMessage(), true);
            }
        }

        // === Accessibility ===
        log("ℹ️ Accessibility optional (Settings → Accessibility → GEL Cleaner)", false);
    }

    private boolean hasUsageAccess() {
        try {
            android.app.AppOpsManager appOps =
                    (android.app.AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

            if (appOps != null) {
                int mode = appOps.unsafeCheckOpNoThrow(
                        "android:get_usage_stats",
                        android.os.Process.myUid(),
                        getPackageName()
                );
                return (mode == android.app.AppOpsManager.MODE_ALLOWED);
            }
        } catch (Exception ignore) {}
        return false;
    }


    /* =========================================================
     * LOG CALLBACK
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;

            String old = txtLogs.getText() == null ? "" : txtLogs.getText().toString();
            if (old.length() == 0) {
                txtLogs.setText(msg);
            } else {
                txtLogs.setText(old + "\n" + msg);
            }

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }
}
