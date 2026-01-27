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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.AppLang;

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
// OLED WARNING — GEL STYLE (APP LANGUAGE + AppTTS + MUTE)
// ============================================================
private void showOledWarning() {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    // ROOT (helper)
    LinearLayout root = buildGELPopupRoot(this);

    // HEADER (helper)
    root.addView(
            buildPopupHeaderWithMute(
                    this,
                    gr ? "Δοκιμή Καταπόνησης Οθόνης" : "Display Stress Test",
                    AppTTS::stop
            )
    );

    // MESSAGE
    final String text =
            gr
            ? "Η δοκιμή αυτή οδηγεί την οθόνη στη μέγιστη φωτεινότητα\n"
              + "και μπορεί να καταπονήσει προσωρινά πάνελ OLED.\n\n"
              + "Συνέχισε μόνο αν κατανοείς και αποδέχεσαι τον κίνδυνο."
            : "This test drives the display at maximum brightness\n"
              + "and may temporarily stress OLED panels.\n\n"
              + "Proceed only if you understand and accept this.";

    TextView msg = new TextView(this);
    msg.setText(text);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(16));
    root.addView(msg);

    // BUTTONS
    LinearLayout buttons = new LinearLayout(this);
    buttons.setOrientation(LinearLayout.HORIZONTAL);
    buttons.setGravity(Gravity.CENTER);

    // CANCEL
    Button cancel = gelButton(
            this,
            gr ? "ΑΚΥΡΩΣΗ" : "CANCEL",
            0xFFB00020
    );

    // START
    Button start = gelButton(
            this,
            gr ? "ΕΝΑΡΞΗ" : "START",
            0xFF0F8A3B
    );

    setDualButtonLayout(cancel, start, buttons);
    root.addView(buttons);

    b.setView(root);

    AlertDialog d = b.create();
    if (d.getWindow() != null)
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

    d.show();

    // TTS
    AppTTS.speak(this, text);

    cancel.setOnClickListener(v -> {
        AppTTS.stop();
        d.dismiss();
        finish();
    });

    start.setOnClickListener(v -> {
        AppTTS.stop();
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
// FINAL USER QUESTION — GEL STYLE (HELPERS + AppTTS)
// ============================================================
private void finishTest() {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    // ROOT (helper)
    LinearLayout root = buildGELPopupRoot(this);

    // HEADER + MUTE (helper)
    root.addView(
            buildPopupHeaderWithMute(
                    this,
                    gr ? "Αποτέλεσμα Οπτικού Ελέγχου" : "Visual Inspection Result",
                    AppTTS::stop
            )
    );

    // =========================
    // QUESTION TEXT
    // =========================
    final String text =
            gr
            ? "Παρατήρησες κάποιο από τα παρακάτω;\n\n"
              + "• Burn-in / αποτύπωση εικόνας\n"
              + "• Ζώνες χρώματος ή απότομες μεταβάσεις\n"
              + "• Κηλίδες / mura στην οθόνη\n"
              + "• Ανομοιόμορφη φωτεινότητα ή απόχρωση"
            : "Did you notice any of the following?\n\n"
              + "• Burn-in / image retention\n"
              + "• Color banding or gradient steps\n"
              + "• Screen stains / mura\n"
              + "• Uneven brightness or tint";

    SpannableString span = new SpannableString(text);

    int titleLen = gr
            ? "Παρατήρησες κάποιο από τα παρακάτω;".length()
            : "Did you notice any of the following?".length();

    span.setSpan(
            new ForegroundColorSpan(0xFF39FF14), // 🟢 neon green
            0,
            titleLen,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    TextView msg = new TextView(this);
    msg.setText(span);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(16));
    root.addView(msg);

    // =========================
    // BUTTONS (helper style)
    // =========================
    LinearLayout buttons = new LinearLayout(this);
    buttons.setOrientation(LinearLayout.HORIZONTAL);
    buttons.setGravity(Gravity.CENTER);

    Button no = gelButton(
            this,
            gr ? "ΟΧΙ\nΗ οθόνη είναι ΟΚ" : "NO\nScreen OK",
            0xFF0F8A3B
    );

    Button yes = gelButton(
            this,
            gr ? "ΝΑΙ\nΠαρατηρήθηκαν προβλήματα" : "YES\nIssues noticed",
            0xFFB00020
    );

    setDualButtonLayout(no, yes, buttons);
    root.addView(buttons);

    b.setView(root);

    AlertDialog d = b.create();
    if (d.getWindow() != null)
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

    d.show();

    // =========================
    // TTS
    // =========================
    AppTTS.speak(this, text);

    // =========================
    // ACTIONS
    // =========================
    no.setOnClickListener(v -> {
        AppTTS.stop();
        Intent i = new Intent();
        i.putExtra("display_issues", false);
        setResult(RESULT_OK, i);
        finish();
    });

    yes.setOnClickListener(v -> {
        AppTTS.stop();
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
