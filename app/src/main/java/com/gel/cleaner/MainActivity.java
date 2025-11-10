package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    private TextView txtLogs;

    @Override
    protected void attachBaseContext(Context base) { super.attachBaseContext(LocaleHelper.apply(base)); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLogs = findViewById(R.id.txtLogs);

        setupLangButtons();
        setupDonate();
        setupCleanerButtons();
        log("✅ Device ready", false);
    }

    private void setupLangButtons() {
        Button bGR = findViewById(R.id.btnLangGR);
        Button bEN = findViewById(R.id.btnLangEN);
        if (bGR != null) bGR.setOnClickListener(v -> { LocaleHelper.set(this, "el"); recreate(); });
        if (bEN != null) bEN.setOnClickListener(v -> { LocaleHelper.set(this, "en"); recreate(); });
    }

    private void setupDonate() {
        Button donate = findViewById(R.id.btnDonate);
        if (donate != null) donate.setOnClickListener(v ->
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/paypalme/gdiolitsis")))
        );
    }

    private void setupCleanerButtons() {
        bind(R.id.btnCpuInfo,      () -> GELCleaner.cpuInfo(this, this));
        bind(R.id.btnCpuLive,      () -> GELCleaner.cpuLive(this, this));
        bind(R.id.btnSafeClean,    () -> GELCleaner.safeClean(this, this));
        bind(R.id.btnDeepClean,    () -> { ensureSAF(); GELCleaner.enhancedCleanAndroidData(this, this); });
        bind(R.id.btnMediaJunk,    () -> GELCleaner.mediaJunk(this, this));
        bind(R.id.btnBrowserCache, () -> startActivity(new Intent(this, AppListActivity.class)));
        bind(R.id.btnTemp,         () -> GELCleaner.tempClean(this, this));
        bind(R.id.btnCleanRam,     () -> GELCleaner.cleanRAM(this, this));
        bind(R.id.btnBatteryBoost, () -> GELCleaner.boostBattery(this, this));
        bind(R.id.btnKillApps,     () -> GELCleaner.killApps(this, this));
        bind(R.id.btnCleanAll,     () -> { ensureSAF(); GELCleaner.cleanAll(this, this); });
    }

    private void bind(int id, Runnable fn){ Button b=findViewById(id); if (b!=null) b.setOnClickListener(v -> fn.run()); }

    private void ensureSAF() {
        if (StorageHelper.getSavedTreeUri(this) == null) {
            log("ℹ️ Δώσε πρόσβαση στο Android/data για Enhanced Clean", false);
            StorageHelper.requestAndroidDataAccess(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9911 && resultCode == RESULT_OK) {
            StorageHelper.persistResult(this, data);
            log("✅ Άδεια SAF αποθηκεύτηκε: Android/data", false);
        }
    }

    @Override public void log(String msg, boolean isError) {
        runOnUiThread(() -> txtLogs.setText(txtLogs.getText().toString() + "\n" + msg));
    }
}
