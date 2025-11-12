package com.gel.cleaner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CleanerActivity extends AppCompatActivity
        implements GELCleaner.LogCallback {

    public static final String EXTRA_MODE = "mode";

    private TextView txtProgress;
    private ProgressBar bar;
    private ScrollView scroll;

    private int cleanedMB = 0;
    private long startMs = 0;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cleaner);

        txtProgress = findViewById(R.id.txtProgress);
        bar         = findViewById(R.id.progressBar);
        scroll      = findViewById(R.id.scrollProgress);

        View btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        startMs = System.currentTimeMillis();

        String mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = "all";

        runMode(mode);
    }

    private void runMode(String mode) {
        log("🔥 Starting: " + mode, false);

        switch (mode) {
            case "safe":
                GELCleaner.safeClean(this, this);
                break;
            case "deep":
                GELCleaner.deepClean(this, this);
                break;
            default:
                GELCleaner.cleanAll(this, this);
                break;
        }

        // Mark done at end
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finishStats();
        }, 1200);
    }

    private void finishStats() {
        long ms = System.currentTimeMillis() - startMs;
        log("✅ DONE in " + (ms / 1000.0) + "s", false);
        bar.setProgress(100);
    }


    /* =========================================================
     * LOG CALLBACK
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            String old = txtProgress.getText().toString();
            txtProgress.setText(old + "\n" + msg);

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }

            // Optional: trigger fake progress
            int p = bar.getProgress();
            if (p < 95) bar.setProgress(p + 5);
        });
    }
}
