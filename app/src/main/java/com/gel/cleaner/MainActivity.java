package com.gel.cleaner;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

        checkPermissions(); // μόνο ενημέρωση log

        log(getString(R.string.device_ready), false);
    }

    /* =========================================================
     * SAF PICKER INIT
     * ========================================================= */
    private void initSafPicker() {
        safPicker = registerForActivityResult(
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
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.paypal.com/paypalme/gdiolitsis"));
                    startActivity(i);
                } catch (Exception e) {
                    log("❌ Cannot open browser: " + e.getMessage(), true);
                }
            });
        }
    }

    /* =========================================================
     * CLEAN BUTTONS (με smart permission handling)
     * ========================================================= */
    private void setupCleanerButtons() {

        bindWithCheck(R.id.btnCpuRamInfo, PermissionType.USAGE, () -> GELCleaner.cpuInfo(this, this));
        bindWithCheck(R.id.btnCpuRamLive, PermissionType.USAGE, () -> GELCleaner.cpuLive(this, this));

        bindWithCheck(R.id.btnCleanRam,  PermissionType.SAF, () -> GELCleaner.cleanRAM(this, this));
        bindWithCheck(R.id.btnSafeClean, PermissionType.SAF, () -> GELCleaner.safeClean(this, this));
        bindWithCheck(R.id.btnDeepClean, PermissionType.SAF, () -> GELCleaner.deepClean(this, this));

        bindWithCheck(R.id.btnMediaJunk,   PermissionType.SAF, () -> GELCleaner.mediaJunk(this, this));
        bindWithCheck(R.id.btnBrowserCache,PermissionType.SAF, () -> GELCleaner.browserCache(this, this));
        bindWithCheck(R.id.btnTemp,        PermissionType.SAF, () -> GELCleaner.tempClean(this, this));

        View appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {
            appCache.setOnClickListener(v -> {
                if (!SAFCleaner.hasTree(this)) {
                    requestStorageAccess();
                } else {
                    try {
                        startActivity(new Intent(this, AppListActivity.class));
                    } catch (Exception e) {
                        log("❌ Cannot open App List: " + e.getMessage(), true);
                    }
                }
            });
        }

        bindWithCheck(R.id.btnBatteryBoost, PermissionType.USAGE, () -> GELCleaner.boostBattery(this, this));
        bindWithCheck(R.id.btnKillApps,     PermissionType.USAGE, () -> GELCleaner.killApps(this, this));
        bindWithCheck(R.id.btnCleanAll,     PermissionType.SAF,   () -> GELCleaner.cleanAll(this, this));
    }

    /* =========================================================
     * SMART BIND
     * ========================================================= */
    private enum PermissionType { NONE, SAF, USAGE }

    private void bindWithCheck(int id, PermissionType type, Runnable fn) {
        View b = findViewById(id);
        if (b == null) return;

        b.setOnClickListener(v -> {
            switch (type) {
                case SAF:
                    if (!SAFCleaner.hasTree(this)) {
                        Toast.makeText(this, "Please grant Storage Access", Toast.LENGTH_SHORT).show();
                        requestStorageAccess();
                        return;
                    }
                    break;
                case USAGE:
                    if (!hasUsageAccess()) {
                        Toast.makeText(this, "Please enable Usage Access", Toast.LENGTH_SHORT).show();
                        requestUsageAccess();
                        return;
                    }
                    break;
                default:
                    break;
            }

            try {
                fn.run();
            } catch (Throwable t) {
                log("❌ Action failed: " + t.getMessage(), true);
            }
        });
    }

    /* =========================================================
     * PERMISSION REQUESTS
     * ========================================================= */
    private void requestStorageAccess() {
        try {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            safPicker.launch(i);
        } catch (Exception e) {
            log("❌ Cannot open Storage Access: " + e.getMessage(), true);
        }
    }

    private void requestUsageAccess() {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            log("❌ Cannot open Usage Access settings: " + e.getMessage(), true);
        }
    }

    /* =========================================================
     * PERMISSIONS CHECK ONLY (log only)
     * ========================================================= */
    private void checkPermissions() {
        if (!SAFCleaner.hasTree(this)) {
            log("⚠️ Storage Access missing — enable manually in Settings → Files access", false);
        }
        if (!hasUsageAccess()) {
            log("⚠️ Usage Access missing — enable manually in Settings → Usage Access", false);
        }
        log("ℹ️ Optional: Settings → Accessibility → GEL Cleaner", false);
    }

    private boolean hasUsageAccess() {
        try {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
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

    /* =========================================================
     * LOG CALLBACK
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;
            String old = txtLogs.getText() == null ? "" : txtLogs.getText().toString();
            txtLogs.setText(old.isEmpty() ? msg : old + "\n" + msg);

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }
}
