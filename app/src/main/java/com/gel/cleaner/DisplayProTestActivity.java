package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
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
 * LAB 6 PRO — Display Advanced Diagnostics (FINAL)
 * ------------------------------------------------------------
 * • Solid colors (dead pixels / stains)
 * • Gradient (banding)
 * • Checkerboard (mura / pixel structure)
 * • Burn-in stress cycle
 * • Auto cycle (no touch)
 * • Brightness locked
 * • OLED safeguard timer
 * • User warning before start
 * ============================================================
 */
public class DisplayProTestActivity extends Activity {

    // ------------------------------------------------------------
    // CONFIG (LOCKED)
    // ------------------------------------------------------------
    private static final int STEP_DURATION_MS = 2500;
    private static final int LOOP_COUNT = 3;

    // OLED safeguard: max total runtime (ms)
    private static final long MAX_RUNTIME_MS = 5 * 60 * 1000; // 5 minutes

    // ------------------------------------------------------------
    // STATE
    // ------------------------------------------------------------
    private FrameLayout root;
    private TextView hint;
    private int stepIndex = 0;
    private int loopIndex = 0;
    private long startTimeMs;

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

        showOledWarning();
    }

    @Override
    public void onBackPressed() {
        // Disabled intentionally — test must complete
    }

    // ============================================================
// OLED WARNING — GEL STYLE
// ============================================================
private void showOledWarning() {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    // ROOT
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(22), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);              // GEL black
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);      // GEL gold
    root.setBackground(bg);

    // TITLE
    TextView title = new TextView(this);
    title.setText("Display Stress Test");
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // MESSAGE
    TextView msg = new TextView(this);
    msg.setText(
            "This test drives the display at maximum brightness\n" +
            "and may temporarily stress OLED panels.\n\n" +
            "Proceed only if you understand and accept this."
    );
    msg.setTextColor(0xFFDDDDDD);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(16));
    root.addView(msg);

    // BUTTON ROW
    LinearLayout buttons = new LinearLayout(this);
    buttons.setOrientation(LinearLayout.HORIZONTAL);
    buttons.setGravity(Gravity.END);

    // CANCEL
    Button cancel = new Button(this);
    cancel.setText("CANCEL");
    cancel.setAllCaps(false);
    cancel.setTextColor(0xFFFFD700);

    GradientDrawable cancelBg = new GradientDrawable();
    cancelBg.setColor(0xFF202020);
    cancelBg.setCornerRadius(dp(12));
    cancelBg.setStroke(dp(2), 0xFFFFD700);
    cancel.setBackground(cancelBg);

    // START
    Button start = new Button(this);
    start.setText("START");
    start.setAllCaps(false);
    start.setTextColor(0xFFFFFFFF);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF39FF14);         // GEL green
    startBg.setCornerRadius(dp(12));
    startBg.setStroke(dp(3), 0xFFFFD700);
    start.setBackground(startBg);

    buttons.addView(cancel);
    buttons.addView(space(dp(12)));
    buttons.addView(start);
    root.addView(buttons);

    b.setView(root);

    AlertDialog d = b.create();
    if (d.getWindow() != null)
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    d.show();

    cancel.setOnClickListener(v -> {
        d.dismiss();
        finish();
    });

    start.setOnClickListener(v -> {
        d.dismiss();
        initUiAndStart();
    });
}

    // ============================================================
    // INIT + START
    // ============================================================
    private void initUiAndStart() {

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

        startTimeMs = System.currentTimeMillis();
        loopIndex = 0;
        stepIndex = 0;

        runStep();
    }

    // ============================================================
    // TEST FLOW
    // ============================================================
    private void runStep() {

        // OLED safeguard
        if (System.currentTimeMillis() - startTimeMs > MAX_RUNTIME_MS) {
            finishTest();
            return;
        }

        if (stepIndex >= steps.length) {
            stepIndex = 0;
            loopIndex++;

            if (loopIndex >= LOOP_COUNT) {
                finishTest();
                return;
            }
        }

        TestStep s = steps[stepIndex];
        s.apply(root);
        hint.setText(
                s.label +
                "\n\nCycle " + (loopIndex + 1) + " / " + LOOP_COUNT
        );

        stepIndex++;
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
