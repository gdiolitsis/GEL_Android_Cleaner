// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 9 — Foldable Posture Handler (Auto UI Refresh)
// COMPLETE FILE (Base Activity) — Ready for copy/paste

package com.gel.cleaner.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gel.cleaner.GELFoldableDetector;
import com.gel.cleaner.GELFoldableUIManager;
import com.gel.cleaner.GELFoldableCallback;

public abstract class GELFoldableActivity extends AppCompatActivity {

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    // Current state (prevent useless refresh)
    private boolean lastInnerState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiManager = new GELFoldableUIManager(this);

        // Detector with callback
        foldDetector = new GELFoldableDetector(this, new GELFoldableCallback() {
            @Override
            public void onPostureChanged(Posture posture) {
                handlePosture(posture);
            }
        });
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

    /**
     * Convert hinge posture → UI state
     */
    private void handlePosture(GELFoldableCallback.Posture posture) {

        boolean isInner;

        switch (posture) {

            case CLOSED:
            case HALF_OPEN:
            case OUTER_SCREEN:
                isInner = false;
                break;

            case FLAT:
            case TABLE_MODE:
            case FULLY_OPEN:
                isInner = true;
                break;

            default:
                isInner = false;
        }

        if (isInner != lastInnerState) {
            lastInnerState = isInner;
            uiManager.applyUI(isInner);
            onFoldableUIChanged(isInner);   // optional override
        }
    }

    /**
     * Optional — subclasses can override
     */
    protected void onFoldableUIChanged(boolean isInnerScreen) {
        // Example: refresh recycler adapters, change layout thresholds, etc.
    }
}
