package com.gel.cleaner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CleanerActivity extends AppCompatActivity
        implements GELCleaner.LogCallback {

    public static final String EXTRA_MODE = "mode";

    private TextView txtProgress;
    private ProgressBar bar;
    private ScrollView scroll;

    private long startMs = 0;
    private final Handler h = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_cleaner);

        txtProgress = findViewById(R.id.txtProgress);
        bar         = findViewById(R.id.progressBar);
        scroll      = findViewById(R.id.scrollProgress);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        startMs = System.currentTimeMillis();

        String mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = "deep";   // default = deep clean

        runMode(mode);
    }


    /* =========================================================
     * RUN SELECTED CLEAN METHOD
     * ========================================================= */
    private void runMode(String mode) {
        log("🔥 Starting: " + mode, false);

        switch (mode) {

            case "deep":
                GELCleaner.deepClean(this, this);
                break;

            case "ram":
                GELCleaner.cleanRAM(this, this);
                break;

            case "temp":
                GELCleaner.tempFiles(this, this);
                break;

            case "browser":
                GELCleaner.browserCache(this, this);
                break;

            default:
                GELCleaner.deepClean(this, this);
                break;
        }

        // Fake realistic finishing animation
        h.postDelayed(this::finishStats, 1200);
    }


    /* =========================================================
     * COMPLETE
     * ========================================================= */
    private void finishStats() {
        long ms = System.currentTimeMillis() - startMs;
        log("✅ DONE in " + (ms / 1000.0) + "s", false);

        if (bar != null) {
            bar.setProgress(100);
        }

        // auto-close after short delay
        h.postDelayed(this::finish, 800);
    }


    /* =========================================================
     * LOG CALLBACK
     * ========================================================= */
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {

            if (txtProgress != null) {
                String old = txtProgress.getText().toString();
                txtProgress.setText(old + "\n" + msg);
            }

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }

            // Smooth progress fill
            if (bar != null) {
                int p = bar.getProgress();
                if (p < 95) bar.setProgress(p + 3);
            }
        });
    }
}
