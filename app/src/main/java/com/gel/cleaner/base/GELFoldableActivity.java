// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableActivity — Final Foldable Base v4.0 (FULL / Compile-Safe)
// ------------------------------------------------------------
// ✔ Correct imports/packages
// ✔ No enum-switch compile traps (uses name() mapping)
// ✔ Zero-crash on posture / multi-window
// ✔ Fully compatible with GELFoldableDetector + UIManager + Orchestrator
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gel.cleaner.GELFoldableUIManager;
import com.gel.cleaner.base.GELFoldableCallback.Posture;

public abstract class GELFoldableActivity extends AppCompatActivity
        implements GELFoldableCallback {

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    private boolean lastInnerState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiManager = new GELFoldableUIManager(this);

        foldDetector = new GELFoldableDetector(this, new GELFoldableCallback() {
            @Override
            public void onPostureChanged(Posture posture) {
                GELFoldableActivity.this.onPostureChanged(posture);
            }

            @Override
            public void onScreenChanged(boolean isInner) {
                GELFoldableActivity.this.onScreenChanged(isInner);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foldDetector != null) foldDetector.start();
    }

    @Override
    protected void onPause() {
        if (foldDetector != null) foldDetector.stop();
        super.onPause();
    }

    // ------------------------------------------------------------
    // REQUIRED CALLBACK 1 — Posture changed
    // ------------------------------------------------------------
    @Override
    public void onPostureChanged(Posture posture) {

        boolean isInner = isInnerFromPosture(posture);

        if (isInner != lastInnerState) {
            lastInnerState = isInner;
            uiManager.applyUI(isInner);
            onFoldableUIChanged(isInner);
        }
    }

    // ------------------------------------------------------------
    // REQUIRED CALLBACK 2 — OEM screen switching
    // ------------------------------------------------------------
    @Override
    public void onScreenChanged(boolean isInner) {
        if (isInner != lastInnerState) {
            lastInnerState = isInner;
            uiManager.applyUI(isInner);
            onFoldableUIChanged(isInner);
        }
    }

    // ------------------------------------------------------------
    // Robust posture mapping (supports old + new vocab)
    // ------------------------------------------------------------
    private boolean isInnerFromPosture(Posture p) {
        if (p == null) return false;
        String n = p.name();

        // New vocab
        if ("INNER_SCREEN".equals(n) ||
            "TABLE_MODE".equals(n) ||
            "FULLY_OPEN".equals(n)) return true;

        // Old vocab
        if ("FLAT".equals(n) ||
            "HALF_OPEN".equals(n) ||
            "HALF_OPENED".equals(n) ||
            "TABLETOP".equals(n)) return true;

        return false;
    }

    // ------------------------------------------------------------
    // OPTIONAL — Activity override hook
    // ------------------------------------------------------------
    protected void onFoldableUIChanged(boolean isInnerScreen) {
        // override if needed (e.g. refresh lists, adjust layout)
    }
}
