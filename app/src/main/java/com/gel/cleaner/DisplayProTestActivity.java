package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ============================================================
 * LAB 6 PRO — DISPLAY INSPECTION
 * ------------------------------------------------------------
 * • Color test (auto)
 * • Gradient / banding
 * • Checkerboard
 * • Burn-in reveal
 * • Brightness lock
 * • Fullscreen / no back
 * ============================================================
 */
public class DisplayProTestActivity extends Activity {

    // ------------------------------------------------------------
    // MODES
    // ------------------------------------------------------------
    private enum Mode {
        COLOR_BASIC,
        GRADIENT,
        CHECKERBOARD,
        BURN_IN,
        DONE
    }

    private Mode mode = Mode.COLOR_BASIC;

    // ------------------------------------------------------------
    // UI
    // ------------------------------------------------------------
    private FrameLayout root;
    private TextView hint;

    // ------------------------------------------------------------
    // HANDLER
    // ------------------------------------------------------------
    private final Handler h = new Handler(Looper.getMainLooper());

    // ------------------------------------------------------------
    // COLOR TEST
    // ------------------------------------------------------------
    private final int[] colors = {
            Color.BLACK,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.GRAY
    };
    private int colorIndex = 0;

    // ------------------------------------------------------------
    // TIMING (ms)
    // ------------------------------------------------------------
    private static final int STEP_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ------------------------------------------------------------
        // BRIGHTNESS LOCK + FULLSCREEN
        // ------------------------------------------------------------
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1f; // max brightness
        getWindow().setAttributes(lp);

        // ------------------------------------------------------------
        // ROOT
        // ------------------------------------------------------------
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        hint = new TextView(this);
        hint.setTextColor(Color.WHITE);
        hint.setTextSize(16f);
        hint.setPadding(24, 24, 24, 24);
        hint.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        root.addView(hint);
        setContentView(root);

        startMode();
    }

    // ------------------------------------------------------------
    // MODE DISPATCH
    // ------------------------------------------------------------
    private void startMode() {
        h.removeCallbacksAndMessages(null);

        switch (mode) {

            case COLOR_BASIC:
                hint.setText(
                        "COLOR TEST\n\n" +
                        "Look for dead pixels, color tint, or uneven areas."
                );
                colorIndex = 0;
                root.setBackgroundColor(colors[colorIndex]);
                h.postDelayed(colorRunnable, STEP_DELAY);
                break;

            case GRADIENT:
                hint.setText(
                        "GRADIENT / BANDING\n\n" +
                        "Look for visible steps or lines in smooth gradients."
                );
                root.setBackground(
                        DisplayPatterns.makeGradient()
                );
                h.postDelayed(this::nextMode, STEP_DELAY * 2L);
                break;

            case CHECKERBOARD:
                hint.setText(
                        "CHECKERBOARD\n\n" +
                        "Look for uniformity issues or subpixel defects."
                );
                root.setBackground(
                        DisplayPatterns.makeCheckerboard()
                );
                h.postDelayed(this::nextMode, STEP_DELAY * 2L);
                break;

            case BURN_IN:
                hint.setText(
                        "BURN-IN REVEAL\n\n" +
                        "Look for ghost images or retained UI elements."
                );
                root.setBackground(
                        DisplayPatterns.makeBurnInCycle()
                );
                h.postDelayed(this::nextMode, STEP_DELAY * 3L);
                break;

            case DONE:
                setResult(RESULT_OK, new Intent());
                finish();
                break;
        }
    }

    // ------------------------------------------------------------
    // COLOR SEQUENCE
    // ------------------------------------------------------------
    private final Runnable colorRunnable = new Runnable() {
        @Override
        public void run() {
            colorIndex++;
            if (colorIndex >= colors.length) {
                nextMode();
                return;
            }
            root.setBackgroundColor(colors[colorIndex]);
            h.postDelayed(this, STEP_DELAY);
        }
    };

    // ------------------------------------------------------------
    // NEXT MODE
    // ------------------------------------------------------------
    private void nextMode() {
        h.removeCallbacksAndMessages(null);

        if (mode == Mode.COLOR_BASIC) mode = Mode.GRADIENT;
        else if (mode == Mode.GRADIENT) mode = Mode.CHECKERBOARD;
        else if (mode == Mode.CHECKERBOARD) mode = Mode.BURN_IN;
        else mode = Mode.DONE;

        startMode();
    }

    // ------------------------------------------------------------
    // NO BACK
    // ------------------------------------------------------------
    @Override
    public void onBackPressed() {
        // blocked intentionally
    }

    // ------------------------------------------------------------
    // CLEANUP
    // ------------------------------------------------------------
    @Override
    protected void onDestroy() {
        h.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
