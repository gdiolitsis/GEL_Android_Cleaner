package com.gel.cleaner;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    TextView txtLogs;
    private boolean live = false;
    private Thread liveThread;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLogs = findViewById(R.id.txtLogs);

        setupLang();
        setupButtons();

        log("✅ Device ready", false);
    }

    private void setupLang(){
        Button bGR = findViewById(R.id.btnLangGR);
        Button bEN = findViewById(R.id.btnLangEN);

        if (bGR != null)
            bGR.setOnClickListener(v -> { LocaleHelper.set(this,"el"); recreate(); });

        if (bEN != null)
            bEN.setOnClickListener(v -> { LocaleHelper.set(this,"en"); recreate(); });
    }

    private void setupButtons(){
        bind(R.id.btnCpuInfo, () ->
                GELCleaner.cpuRamInfo(this, this));

        bind(R.id.btnCpuLive, this::startLive);

        bind(R.id.btnSafeClean,    () -> GELCleaner.safeClean(this, this));
        bind(R.id.btnDeepClean,    () -> GELCleaner.deepClean(this, this));
        bind(R.id.btnMediaJunk,    () -> GELCleaner.mediaJunk(this, this));
        bind(R.id.btnBrowserCache, () -> GELCleaner.browserCache(this, this));
        bind(R.id.btnTemp,         () -> GELCleaner.tempClean(this, this));
        bind(R.id.btnCleanRam,     () -> GELCleaner.cleanRAM(this, this));
        bind(R.id.btnBatteryBoost, () -> GELCleaner.boostBattery(this, this));
        bind(R.id.btnKillApps,     () -> GELCleaner.killApps(this, this));
        bind(R.id.btnCleanAll,     () -> GELCleaner.cleanAll(this, this));
    }

    private void bind(int id, Runnable fn){
        Button b = findViewById(id);
        if (b != null) b.setOnClickListener(v -> fn.run());
    }

    private void startLive(){
        live = true;
        if (liveThread != null && liveThread.isAlive()) return;

        liveThread = new Thread(() -> {
            while (live){
                GELCleaner.cpuRamInfo(this, this);
                try { Thread.sleep(1000); } catch (Exception ignored){}
            }
        });
        liveThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        live = false;
    }

    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            txtLogs.append("\n" + msg);
        });
    }
}
