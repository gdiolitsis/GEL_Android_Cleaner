package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    TextView txtLogs;

    // SAF picker
    private ActivityResultLauncher<Uri> pickTreeLauncher;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLogs = findViewById(R.id.txtLogs);

        // SAF launcher
        pickTreeLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(),
                uri -> {
                    if (uri != null) {
                        // persist permission
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        SAFCleaner.saveTreeUri(this, uri);
                        log("✅ SAF granted", false);
                    } else {
                        log("❌ SAF cancelled", true);
                    }
                });

        setupLangButtons();
        setupDonate();
        setupCleanerButtons();

        log("✅ Device ready", false);
    }

    private void requireSAFThen(Runnable action) {
        if (SAFCleaner.hasTree(this)) {
            action.run();
        } else {
            log("ℹ️ Select storage root to enable cleaning…", false);
            pickTreeLauncher.launch(null);
        }
    }

    private void setupLangButtons() {
        Button bGR = findViewById(R.id.btnLangGR);
        Button bEN = findViewById(R.id.btnLangEN);

        if (bGR != null) bGR.setOnClickListener(v -> { LocaleHelper.set(this, "el"); recreate(); });
        if (bEN != null) bEN.setOnClickListener(v -> { LocaleHelper.set(this, "en"); recreate(); });
    }

    private void setupDonate() {
        Button donateButton = findViewById(R.id.btnDonate);
        if (donateButton != null) {
            donateButton.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.paypal.com/paypalme/gdiolitsis"));
                startActivity(i);
            });
        }
    }

    private void setupCleanerButtons() {
        bind(R.id.btnCpuInfo,      () -> GELCleaner.cpuInfo(this, this));
        bind(R.id.btnCpuLive,      () -> GELCleaner.cpuLive(this, this));
        bind(R.id.btnSafeClean,    () -> GELCleaner.safeClean(this, this));
        bind(R.id.btnDeepClean,    () -> GELCleaner.deepClean(this, this));

        bind(R.id.btnMediaJunk,    () -> requireSAFThen(() -> GELCleaner.mediaJunk(this, this)));
        bind(R.id.btnBrowserCache, () -> requireSAFThen(() -> GELCleaner.browserCache(this, this)));
        bind(R.id.btnTemp,         () -> requireSAFThen(() -> GELCleaner.tempClean(this, this)));
        bind(R.id.btnCleanAll,     () -> requireSAFThen(() -> GELCleaner.cleanAll(this, this)));

        bind(R.id.btnCleanRam,     () -> GELCleaner.cleanRAM(this, this));
        bind(R.id.btnBatteryBoost, () -> GELCleaner.boostBattery(this, this));

        // Προαιρετικό: άνοιγμα λίστας apps για manual cache clear
        // bind(R.id.btnKillApps,   () -> startActivity(new Intent(this, AppListActivity.class)));
        bind(R.id.btnKillApps,     () -> GELCleaner.killApps(this, this));
    }

    private void bind(int id, Runnable fn){
        Button b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> fn.run());
    }

    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            String old = txtLogs.getText().toString();
            txtLogs.setText(old + "\n" + msg);
        });
    }
}
