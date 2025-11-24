package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CleanerActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private TextView txtLog;

    // Foldable core
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);

        // Foldable Engine
        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);   // stub-safe
        dualPane     = new DualPaneManager(this);            // wrapper
        foldDetector = new GELFoldableDetector(this, this);  // v1.2

        // UI Binding
        txtLog = findViewById(R.id.txtCleanerLog);
        txtLog.setMovementMethod(new ScrollingMovementMethod());

        Button btnCleanRam   = findViewById(R.id.btnCleanRam);
        Button btnDeepClean  = findViewById(R.id.btnDeepClean);
        Button btnTempClean  = findViewById(R.id.btnTempFiles);
        Button btnBrowser    = findViewById(R.id.btnBrowserClean);
        Button btnRunning    = findViewById(R.id.btnRunningApps);

        log("🧹 GEL Cleaner loaded.\n");

        btnCleanRam.setOnClickListener(v ->
                GELCleaner.cleanRAM(getBaseContext(), this::log));

        btnDeepClean.setOnClickListener(v ->
                GELCleaner.deepClean(getBaseContext(), this::log));

        btnTempClean.setOnClickListener(v ->
                GELCleaner.cleanTempFiles(getBaseContext(), this::log));

        btnBrowser.setOnClickListener(v ->
                GELCleaner.browserCache(getBaseContext(), this::log));

        btnRunning.setOnClickListener(v ->
                GELCleaner.openRunningApps(getBaseContext(), this::log));
    }

    @Override
    protected void onResume() {
        super.onResume();
        foldDetector.start();
    }

    @Override
    protected void onPause() {
        foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS (Unified v1.2)
    // ============================================================

    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // Safe stub call (no errors)
        animPack.onPostureChanged(posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        uiManager.applyUI(isInner);

        // DualPaneManager has no dispatchMode() in the new system
        // Replace with safe supported call:
        try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}
    }

    // ============================================================
    // LOGGING
    // ============================================================

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
