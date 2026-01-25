package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ============================================================
 * LAB 6 PRO — Display Advanced Diagnostics
 * ------------------------------------------------------------
 * • Solid colors (dead pixels / stains)
 * • Gradient (banding)
 * • Checkerboard (mura / pixel structure)
 * • Burn-in stress cycle
 * • Auto cycle (no touch)
 * • Back disabled
 * • Brightness locked
 * ============================================================
 */
public class DisplayProTestActivity extends Activity {

    // ------------------------------------------------------------
    // CONFIG
    // ------------------------------------------------------------
    private static final int STEP_DURATION_MS = 2500;

    // ------------------------------------------------------------
    // STATE
    // ------------------------------------------------------------
    private FrameLayout root;
    private TextView hint;
    private int step = 0;

    private final Handler h = new Handler(Looper.getMainLooper());

    // ------------------------------------------------------------
    // TEST STEPS
    // ------------------------------------------------------------
    private final TestStep[] steps = new TestStep[]{

            // SOLID COLORS
            new SolidStep(Color.BLACK, "Black — look for bright pixels"),
            new SolidStep(Color.WHITE, "White — look for dark spots"),
            new SolidStep(Color.RED,   "Red — burn-in / tint"),
            new SolidStep(Color.GREEN, "Green — uniformity"),
            new SolidStep(Color.BLUE,  "Blue — uniformity"),

            // GRADIENT (BANDING)
            new DrawableStep(
                    DisplayPatterns.makeGradient(),
                    "Gradient — look for banding"
            ),

            // CHECKERBOARD (MURA)
            new DrawableStep(
                    DisplayPatterns.makeCheckerboard(),
                    "Checkerboard — look for stains / mura"
            ),

            // BURN-IN STRESS
            new DrawableStep(
                    DisplayPatterns.makeBurnInCycle(),
                    "Burn-in stress pattern"
            )
    };

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen + brightness lock
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1f;
        getWindow().setAttributes(lp);

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        hint = new TextView(this);
        hint.setTextColor(Color.WHITE);
        hint.setTextSize(16f);
        hint.setPadding(24, 24, 24, 24);
        hint.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        root.addView(hint);
        setContentView(root);

        startCycle();
    }

    @Override
    public void onBackPressed() {
        // Disabled on purpose — test must complete
    }

    // ============================================================
    // TEST FLOW
    // ============================================================
    private void startCycle() {
        step = 0;
        runStep();
    }

    private void runStep() {
        if (step >= steps.length) {
            finishTest();
            return;
        }

        TestStep s = steps[step];
        s.apply(root);
        hint.setText(s.label);

        step++;

        h.postDelayed(this::runStep, STEP_DURATION_MS);
    }

    private void finishTest() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    // ============================================================
    // STEP TYPES
    // ============================================================
    private abstract static class TestStep {
        final String label;
        TestStep(String l) { label = l; }
        abstract void apply(FrameLayout root);
    }

    private static final class SolidStep extends TestStep {
        final int color;
        SolidStep(int c, String l) {
            super(l);
            color = c;
        }

        @Override
        void apply(FrameLayout root) {
            root.setBackgroundColor(color);
        }
    }

    private static final class DrawableStep extends TestStep {
        final Drawable d;
        DrawableStep(Drawable dr, String l) {
            super(l);
            d = dr;
        }

        @Override
        void apply(FrameLayout root) {
            root.setBackground(d);
        }
    }
}
