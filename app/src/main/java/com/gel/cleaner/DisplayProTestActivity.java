package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ============================================================
 * LAB 6 PRO — Display Advanced Diagnostics (FINAL / LOCKED)
 * ------------------------------------------------------------
 * • Solid colors (dead pixels / stains)
 * • Gradient (banding)
 * • Checkerboard (mura / pixel structure)
 * • Burn-in stress cycle
 * • Auto cycle (no touch)
 * • Brightness locked
 * • OLED safeguard timer
 * • User confirmation at the end
 * ============================================================
 */
public class DisplayProTestActivity extends Activity {

    // ------------------------------------------------------------
    // CONFIG (LOCKED)
    // ------------------------------------------------------------
    private static final int STEP_DURATION_MS = 2500;
    private static final int LOOP_COUNT = 3;
    private static final long MAX_RUNTIME_MS = 5 * 60 * 1000; // 5 min

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

            new SolidStep(Color.BLACK, "Black — look for bright pixels"),
            new SolidStep(Color.WHITE, "White — look for dark spots"),
            new SolidStep(Color.RED,   "Red — burn-in / tint"),
            new SolidStep(Color.GREEN, "Green — uniformity"),
            new SolidStep(Color.BLUE,  "Blue — uniformity"),

            new DrawableStep(
                    DisplayPatterns.makeGradient(),
                    "Gradient — look for banding"
            ),

            new DrawableStep(
                    DisplayPatterns.makeCheckerboard(),
                    "Checkerboard — look for stains / mura"
            ),

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
        // disabled intentionally
    }

    // ============================================================
    // OLED WARNING — GEL STYLE
    // ============================================================
    private void showOledWarning() {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);

        b.setCancelable(false);

        LinearLayout rootBox = new LinearLayout(this);
        rootBox.setOrientation(LinearLayout.VERTICAL);
        rootBox.setPadding(dp(24), dp(22), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        rootBox.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("Display Stress Test");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        rootBox.addView(title);

        TextView msg = new TextView(this);
        msg.setText(
                "This test drives the display at maximum brightness\n" +
                "and may temporarily stress OLED panels.\n\n" +
                "Proceed only if you understand and accept this."
        );
        msg.setTextColor(0xFF39FF14); // 🟢 neon green
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 0, 0, dp(16));
        rootBox.addView(msg);

        LinearLayout buttons = new LinearLayout(this);
buttons.setOrientation(LinearLayout.HORIZONTAL);
buttons.setGravity(Gravity.CENTER);
buttons.setPadding(0, dp(8), 0, 0);
rootBox.addView(buttons);

        Button cancel = new Button(this);
cancel.setText("CANCEL");
cancel.setAllCaps(false);
cancel.setTextColor(Color.WHITE);
cancel.setTextSize(15f);

GradientDrawable cancelBg = new GradientDrawable();
cancelBg.setColor(0xFFB00020); // 🔴 κόκκινο
cancelBg.setCornerRadius(dp(12));
cancelBg.setStroke(dp(3), 0xFFFFD700);
cancel.setBackground(cancelBg);

LinearLayout.LayoutParams lpCancel =
        new LinearLayout.LayoutParams(0, dp(56), 1f);
lpCancel.setMargins(0, 0, dp(8), 0);
cancel.setLayoutParams(lpCancel);

        Button start = new Button(this);
start.setText("START");
start.setAllCaps(false);
start.setTextColor(Color.WHITE);
start.setTextSize(15f);

GradientDrawable startBg = new GradientDrawable();
startBg.setColor(0xFF0F8A3B); // 🟢 σκούρο πράσινο
startBg.setCornerRadius(dp(12));
startBg.setStroke(dp(3), 0xFFFFD700);
start.setBackground(startBg);

LinearLayout.LayoutParams lpStart =
        new LinearLayout.LayoutParams(0, dp(56), 1f);
lpStart.setMargins(dp(8), 0, 0, 0);
start.setLayoutParams(lpStart);

        buttons.addView(cancel);
        buttons.addView(start);

        rootBox.addView(buttons);
        b.setView(rootBox);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

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
    // INIT + RUN
    // ============================================================
    private void initUiAndStart() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1f;
        getWindow().setAttributes(lp);

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        hint = new TextView(this);
        hint.setTextColor(Color.WHITE);
        hint.setTextSize(16f);
        hint.setPadding(dp(24), dp(24), dp(24), dp(24));
        hint.setGravity(Gravity.CENTER);

        root.addView(hint);
        setContentView(root);

        startTimeMs = System.currentTimeMillis();
        stepIndex = 0;
        loopIndex = 0;

        runStep();
    }

    private void runStep() {

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
                s.label + "\n\nCycle " + (loopIndex + 1) + " / " + LOOP_COUNT
        );

        stepIndex++;
        h.postDelayed(this::runStep, STEP_DURATION_MS);
    }

    // ============================================================
    // FINAL USER QUESTION — GEL STYLE
    // ============================================================
    private void finishTest() {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);

        b.setCancelable(false);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(24), dp(22), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        box.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("Visual Inspection Result");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        box.addView(title);

        TextView msg = new TextView(this);
        msg.setText(
                "Did you notice any of the following?\n\n" +
                "• Burn-in / image retention\n" +
                "• Color banding or gradient steps\n" +
                "• Screen stains / mura\n" +
                "• Uneven brightness or tint"
        );
        msg.setTextColor(0xFFDDDDDD);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 0, 0, dp(16));
        box.addView(msg);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.END);

        Button no = new Button(this);
no.setText("NO\nScreen OK");
no.setAllCaps(false);
no.setSingleLine(false);
no.setMaxLines(2);
no.setGravity(Gravity.CENTER);
no.setTextColor(Color.WHITE);
no.setTextSize(15f);

GradientDrawable noBg = new GradientDrawable();
noBg.setColor(0xFF0F8A3B); // 🟢 ΠΡΑΣΙΝΟ
noBg.setCornerRadius(dp(12));
noBg.setStroke(dp(3), 0xFFFFD700);
no.setBackground(noBg);

LinearLayout.LayoutParams lpNo =
        new LinearLayout.LayoutParams(0, dp(56), 1f);
no.setLayoutParams(lpNo);

        Button yes = new Button(this);
yes.setText("YES\nIssues noticed");
yes.setAllCaps(false);
yes.setSingleLine(false);
yes.setMaxLines(2);
yes.setGravity(Gravity.CENTER);
yes.setTextColor(Color.WHITE);
yes.setTextSize(15f);

GradientDrawable yesBg = new GradientDrawable();
yesBg.setColor(0xFFB00020); // 🔴 ΚΟΚΚΙΝΟ
yesBg.setCornerRadius(dp(12));
yesBg.setStroke(dp(3), 0xFFFFD700);
yes.setBackground(yesBg);

LinearLayout.LayoutParams lpYes =
        new LinearLayout.LayoutParams(0, dp(56), 1f);
yes.setLayoutParams(lpYes);

        buttons.addView(no);
        buttons.addView(yes);
        box.addView(buttons);

        b.setView(box);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

        d.show();

        no.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra("display_issues", false);
            setResult(RESULT_OK, i);
            finish();
        });

        yes.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra("display_issues", true);
            setResult(RESULT_OK, i);
            finish();
        });
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }

    private View space(int w) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(w, 1));
        return v;
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
        SolidStep(int c, String l) { super(l); color = c; }
        @Override void apply(FrameLayout root) {
            root.setBackgroundColor(color);
        }
    }

    private static final class DrawableStep extends TestStep {
        final Drawable d;
        DrawableStep(Drawable dr, String l) { super(l); d = dr; }
        @Override void apply(FrameLayout root) {
            root.setBackground(d);
        }
    }
}
