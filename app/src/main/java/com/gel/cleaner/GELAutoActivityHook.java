// GDiolitsis Engine Lab (GEL) — Author & Developer
// UNIVERSAL MASTER HOOK
// 🔥 Combines GELAutoDP + FoldableDetector + UIManager
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class GELAutoActivityHook extends AppCompatActivity implements GELFoldableCallback {

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    private boolean lastInner = false;

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Universal DP scaling
        GELAutoDP.init(this);

        // Foldable subsystem
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
    // CONFIGURATION EVENTS: rotation / fold-unfold / locale / resize
    // ============================================================
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        GELAutoDP.init(this);

        // reapply foldable UI mode on orientation change
        uiManager.applyUI(lastInner);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        GELAutoDP.init(this);

        uiManager.applyUI(lastInner);
    }

    // ============================================================
    // FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // Optional debug
        // Log.d("GEL-Fold", "Posture: " + posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner == lastInner) return;
        lastInner = isInner;

        if (uiManager != null) uiManager.applyUI(isInner);
    }

    // ============================================================
    // HELPERS (unchanged)
    // ============================================================
    public int dp(int x) { return GELAutoDP.dp(x); }
    public float sp(float x) { return GELAutoDP.sp(x); }
    public int px(int x) { return GELAutoDP.px(x); }
}
