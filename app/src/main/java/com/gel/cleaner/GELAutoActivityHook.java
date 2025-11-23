// GDiolitsis Engine Lab (GEL) — Author & Developer
// UNIVERSAL MASTER HOOK (GEL v4.3)
// 🔥 Combines: GELAutoDP + FoldableDetector + UIManager + Locale Hook
// NOTE: Full-file patch — πάντα δούλευε πάνω στο ΤΕΛΕΥΤΑΙΟ αρχείο.

package com.gel.cleaner;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class GELAutoActivityHook extends AppCompatActivity implements GELFoldableCallback {

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    private boolean lastInner = false;

    // ============================================================
    // CONTEXT HOOK (Locale + Scaling-safe)
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        // LocaleHelper first (your standard rule)
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Universal dp/sp scaling
        GELAutoDP.init(this);

        // 2) Foldable systems
        uiManager = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foldDetector != null) foldDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (foldDetector != null) foldDetector.stop();
    }

    // ============================================================
    // CONFIGURATION EVENTS: rotation / fold-unfold / locale change
    // ============================================================
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Always rescale DP/SP
        GELAutoDP.init(this);

        // Re-apply foldable UI
        if (uiManager != null) uiManager.applyUI(lastInner);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        // Rescale again
        GELAutoDP.init(this);

        // Re-apply foldable mode
        if (uiManager != null) uiManager.applyUI(lastInner);
    }

    // ============================================================
    // FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // Optional debug
        // Log.d("GEL-Fold", "Posture = " + posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner == lastInner) return;

        lastInner = isInner;

        if (uiManager != null)
            uiManager.applyUI(isInner);
    }

    // ============================================================
    // HELPERS (GLOBAL DP/SP)
    // ============================================================
    public int dp(int x)  { return GELAutoDP.dp(x); }
    public float sp(float x) { return GELAutoDP.sp(x); }
    public int px(int x)  { return GELAutoDP.px(x); }
}
