// GDiolitsis Engine Lab (GEL) — Author & Developer
// CleanerActivity (GEL Auto-Scaling + Locale + Universal UI)

package com.gel.cleaner;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class CleanerActivity extends GELAutoActivityHook {

    private TextView txtLog;

    @Override
    protected void attachBaseContext(Context base) {
        // Locale + AutoDP work together correctly
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

        log("🧹 GEL Cleaner loaded.\n");

        // Smart RAM Clean
        btnCleanRam.setOnClickListener(v ->
                GELCleaner.cleanRAM(getBaseContext(), this::log));

        // OEM Deep Clean
        btnDeepClean.setOnClickListener(v ->
                GELCleaner.deepClean(getBaseContext(), this::log));

        // Temp / Junk Files Cleaner
        btnTempClean.setOnClickListener(v ->
                GELCleaner.cleanTempFiles(getBaseContext(), this::log));

        // Browser Cache Cleaner
        btnBrowser.setOnClickListener(v ->
                GELCleaner.browserCache(getBaseContext(), this::log));

        // Running Apps Manager
        btnRunning.setOnClickListener(v ->
                GELCleaner.openRunningApps(getBaseContext(), this::log));
    }

    // ========================================================================
    // LOG PRINTER
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

    private void log(String msg) {
        log(msg, false);
    }
}
