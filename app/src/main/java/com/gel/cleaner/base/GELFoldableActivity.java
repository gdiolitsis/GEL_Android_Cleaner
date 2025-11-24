// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELFoldableActivity — Final Foldable Base v3.0 (Compile-Safe)
// ------------------------------------------------------------
// ✔ Fix: implements BOTH callback methods
// ✔ Fix: posture mapping uses the REAL enum of the project
// ✔ Zero-crash on posture / multi-window
// ✔ Fully compatible with GELFoldableDetector + UIManager
// ------------------------------------------------------------

package com.gel.cleaner.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gel.cleaner.GELFoldableDetector;
import com.gel.cleaner.GELFoldableUIManager;
import com.gel.cleaner.GELFoldableCallback;

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

        boolean isInner;

        switch (posture) {

            case OUTER_SCREEN:
                isInner = false;
                break;

            case INNER_SCREEN:
            case TABLE_MODE:
            case FULLY_OPEN:
                isInner = true;
                break;

            case HALF_OPENED:
            default:
                // keep previous state to avoid flicker
                isInner = lastInnerState;
        }

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
    // OPTIONAL — Activity override hook
    // ------------------------------------------------------------
    protected void onFoldableUIChanged(boolean isInnerScreen) {
        // override if needed (e.g. refresh lists, adjust layout)
    }
}
