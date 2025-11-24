// GDiolitsis Engine Lab (GEL) — Author & Developer
// CleanerActivity — Foldable Ready (GEL Edition v3.0)
// GEL Auto-Scaling + Locale + Foldable Engine + Dual Pane UI
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

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

        // ============================================================
        // FOLDABLE ENGINE INIT
        // ============================================================
        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // ============================================================
        // UI BINDING
        // ============================================================
        txtLog = findViewById(R.id.txtCleanerLog);
        txtLog.setMovementMethod(new ScrollingMovementMethod());

        Button btnCleanRam   = findViewById(R.id.btnCleanRam);
        Button btnDeepClean  = findViewById(R.id.btnDeepClean);
        Button btnTempClean  = findViewById(R.id.btnTempFiles);
        Button btnBrowser    = findViewById(R.id.btnBrowserClean);
        Button btnRunning    = findViewById(R.id.btnRunningApps);

        log("🧹 GEL Cleaner loaded.\n");

        // GEL CLEAN ACTIONS
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

    // ============================================================
    // FOLDABLE LIFE CYCLE
    // ============================================================
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
    // FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        animPack.applyHingePulse(posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        uiManager.applyUI(isInner);
        dualPane.dispatchMode(isInner);
    }

    // ============================================================
    // LOG PRINTER
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
