package com.gel.cleaner;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
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
        setupCleanerButtons();

        // Δεν ζητάμε καμία άδεια στην εκκίνηση.
        log(getString(R.string.device_ready), false);
    }

    /* =========================================================
     * FULL STORAGE ACCESS — ONLY WHEN NEEDED (κουμπιά STORAGE)
     * ========================================================= */
    private void ensureFullStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));  // direct-to-GEL
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        }
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
        return c.getResources()
                .getConfiguration()
                .getLocales()
                .get(0)
                .getLanguage();
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
                    Toast.makeText(this, "Cannot open browser", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /* =========================================================
     * CLEAN BUTTONS
     * ========================================================= */
    private void setupCleanerButtons() {

        // CPU / RAM info & live → χρειάζονται USAGE
        bindWithCheck(R.id.btnCpuRamInfo, PermissionType.USAGE,
                () -> GELCleaner.cpuInfo(this, this));

        bindWithCheck(R.id.btnCpuRamLive, PermissionType.USAGE,
                () -> GELCleaner.cpuLive(this, this));

        // RAM + Safe / Deep / Junk / Temp / All → STORAGE
        bindWithCheck(R.id.btnCleanRam,  PermissionType.STORAGE,
                () -> GELCleaner.cleanRAM(this, this));

        bindWithCheck(R.id.btnSafeClean, PermissionType.STORAGE,
                () -> GELCleaner.safeClean(this, this));

        bindWithCheck(R.id.btnDeepClean, PermissionType.STORAGE,
                () -> GELCleaner.deepClean(this, this));

        // 🔥 Media Junk → κατευθείαν SAFCleaner (όχι GELCleaner.mediaJunk)
        bindWithCheck(R.id.btnMediaJunk, PermissionType.STORAGE,
                () -> SAFCleaner.mediaJunk(this, this));

        bindWithCheck(R.id.btnBrowserCache, PermissionType.STORAGE,
                () -> GELCleaner.browserCache(this, this));

        bindWithCheck(R.id.btnTemp, PermissionType.STORAGE,
                () -> GELCleaner.tempClean(this, this));

        // App cache list
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

        // Battery / Kill → USAGE
        bindWithCheck(R.id.btnBatteryBoost, PermissionType.USAGE,
                () -> GELCleaner.boostBattery(this, this));

        bindWithCheck(R.id.btnKillApps, PermissionType.USAGE,
                () -> GELCleaner.killApps(this, this));

        // 🔴 CLEAN ALL → GEL Deep Clean Pro
        bindWithCheck(R.id.btnCleanAll, PermissionType.STORAGE,
                () -> GELCleaner.gelDeepCleanPro(this, this));
    }

    private enum PermissionType { NONE, STORAGE, USAGE }

    private void bindWithCheck(int id, PermissionType type, Runnable fn) {
        View b = findViewById(id);
        if (b == null) return;

        b.setOnClickListener(v -> {

            // STORAGE → MANAGE_EXTERNAL_STORAGE panel (Android 11+)
            if (type == PermissionType.STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    ensureFullStorageAccess();
                    return;
                }
            }

            // USAGE → Usage Access panel
            if (type == PermissionType.USAGE && !hasUsageAccess()) {
                requestUsageAccess();
                return;
            }

            try {
                fn.run();
            } catch (Throwable t) {
                Toast.makeText(
                        this,
                        "Action failed: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    /* =========================================================
     * USAGE ACCESS
     * ========================================================= */
    private boolean hasUsageAccess() {
        try {
            AppOpsManager appOps =
                    (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            if (appOps != null) {
                int mode = appOps.unsafeCheckOpNoThrow(
                        "android:get_usage_stats",
                        Process.myUid(),
                        getPackageName()
                );
                return (mode == AppOpsManager.MODE_ALLOWED);
            }
        } catch (Exception ignore) {}
        return false;
    }

    private void requestUsageAccess() {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName())); // direct to GEL
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Cannot open Usage Access settings",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /* =========================================================
     * LOGGING
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;

            String old = txtLogs.getText() == null
                    ? "" : txtLogs.getText().toString();
            txtLogs.setText(old.isEmpty() ? msg : old + "\n" + msg);

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }
}
