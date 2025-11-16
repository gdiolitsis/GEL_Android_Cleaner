package com.gel.cleaner;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CleanerActivity extends AppCompatActivity {

    private TextView txtLog;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);

        txtLog = findViewById(R.id.txtCleanerLog);
        txtLog.setMovementMethod(new ScrollingMovementMethod());

        Button btnCleanRam   = findViewById(R.id.btnCleanRam);
        Button btnDeepClean  = findViewById(R.id.btnDeepClean);
        Button btnTempClean  = findViewById(R.id.btnTempFiles);
        Button btnBrowser    = findViewById(R.id.btnBrowserClean);
        Button btnRunning    = findViewById(R.id.btnRunningApps);

        // FIXED: now valid
        log("🧹 GEL Cleaner loaded.\n");

        // ====================================================================
        // 1) CLEAN RAM (Smart Clean)
        // ====================================================================
        btnCleanRam.setOnClickListener(v ->
                GELCleaner.cleanRAM(getBaseContext(), this::log));

        // ====================================================================
        // 2) DEEP CLEAN (OEM Cleaner)
        // ====================================================================
        btnDeepClean.setOnClickListener(v ->
                GELCleaner.deepClean(getBaseContext(), this::log));

        // ====================================================================
        // 3) TEMP FILES
        // ====================================================================
        btnTempClean.setOnClickListener(v ->
                GELCleaner.cleanTempFiles(getBaseContext(), this::log));

        // ====================================================================
        // 4) BROWSER CACHE
        // ====================================================================
        btnBrowser.setOnClickListener(v ->
                GELCleaner.browserCache(getBaseContext(), this::log));

        // ====================================================================
        // 5) RUNNING APPS
        // ====================================================================
        btnRunning.setOnClickListener(v ->
                GELCleaner.openRunningApps(getBaseContext(), this::log));
    }

    // ========================================================================
    // LOG PRINTER (CORE)
    // ========================================================================
    private void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            txtLog.append(msg + "\n");

            ScrollView scroll = findViewById(R.id.scrollCleaner);
            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
        });
    }

    // ========================================================================
    // OVERLOAD FOR SIMPLE CALLS  (FIX)
    // ========================================================================
    private void log(String msg) {
        log(msg, false);
    }
}
