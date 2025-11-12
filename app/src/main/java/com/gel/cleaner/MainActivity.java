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

        if (bGR != null) {
            bGR.setOnClickListener(v -> {
                // άλλαξε μόνο αν χρειάζεται
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

        // Storage SAF (οδηγεί τον χρήστη στη δική σου PermissionsActivity)
        if (!SAFCleaner.hasTree(this)) {
            log("⚠️ SAF missing → open picker…", false);
            try {
                startActivity(new Intent(this, PermissionsActivity.class));
            } catch (Exception e) {
                log("❌ Cannot open SAF helper: " + e.getMessage(), true);
            }
        }

        // PACKAGE_USAGE_STATS (Settings screen)
        if (!PermissionHelper.hasUsageAccess(this)) {
            log("⚠️ Usage access missing", true);
            try {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                log("❌ Cannot open Usage Access settings: " + e.getMessage(), true);
            }
        }

        // Accessibility ενημέρωση (δεν ανοίγω αυτόματα settings για να είναι Play-safe)
        if (!PermissionHelper.hasAccessibility(this)) {
            log("⚠️ Accessibility not enabled (Settings → Accessibility → GEL Cleaner)", true);
        }
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
