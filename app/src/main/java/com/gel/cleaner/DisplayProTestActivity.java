// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// LAB 6 PRO — Display Advanced Diagnostics (FINAL / STABLE)
// • Localized (GR / EN)
// • Proper layout (no squeezed buttons)
// • AppTTS language-aware
// • No broken popups
// ============================================================

package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayProTestActivity extends Activity {

    // ------------------------------------------------------------
    // CONFIG
    // ------------------------------------------------------------
    private static final int STEP_DURATION_MS = 2500;
    private static final int LOOP_COUNT = 3;
    private static final long MAX_RUNTIME_MS = 5 * 60 * 1000;

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
    private TestStep[] steps;

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showOledWarning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppTTS.stop(); // ensure fresh language
    }

    @Override
    public void onBackPressed() {
        // disabled
    }

    // ============================================================
    // OLED WARNING POPUP
    // ============================================================
    private void showOledWarning() {

        final boolean gr = AppLang.isGreek(this);

        String text = gr
                ? "Η δοκιμή αυτή οδηγεί την οθόνη στη μέγιστη φωτεινότητα\n"
                + "και μπορεί να καταπονήσει προσωρινά πάνελ OLED.\n\n"
                + "Συνέχισε μόνο αν κατανοείς και αποδέχεσαι τον κίνδυνο."
                : "This test drives the display at maximum brightness\n"
                + "and may temporarily stress OLED panels.\n\n"
                + "Proceed only if you understand and accept this.";

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);

        root.addView(buildHeader(this,
                gr ? "Δοκιμή Καταπόνησης Οθόνης" : "Display Stress Test"));

        TextView msg = buildMessage(this, text);
        root.addView(msg);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);

        Button cancel = gelButton(this, gr ? "ΑΚΥΡΩΣΗ" : "CANCEL", 0xFFB00020);
        Button start  = gelButton(this, gr ? "ΕΝΑΡΞΗ" : "START",  0xFF0F8A3B);

        setDualButtons(cancel, start, buttons);
        root.addView(buttons);

        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();

        AppTTS.speak(this, text);

        cancel.setOnClickListener(v -> {
            AppTTS.stop();
            d.dismiss();
            finish();
        });

        start.setOnClickListener(v -> {
            AppTTS.stop();
            d.dismiss();
            initAndStart();
        });
    }

    // ============================================================
    // INIT TEST
    // ============================================================
    private void initAndStart() {

        boolean gr = AppLang.isGreek(this);

        steps = new TestStep[]{

                new SolidStep(Color.BLACK,
                        gr ? "Μαύρο — φωτεινά pixels" : "Black — bright pixels"),
                new SolidStep(Color.WHITE,
                        gr ? "Λευκό — σκοτεινά σημεία" : "White — dark spots"),
                new SolidStep(Color.RED,
                        gr ? "Κόκκινο — burn-in / tint" : "Red — burn-in / tint"),
                new SolidStep(Color.GREEN,
                        gr ? "Πράσινο — ομοιομορφία" : "Green — uniformity"),
                new SolidStep(Color.BLUE,
                        gr ? "Μπλε — ομοιομορφία" : "Blue — uniformity"),

                new DrawableStep(
                        DisplayPatterns.makeGradient(),
                        gr ? "Διαβάθμιση — banding" : "Gradient — banding"),

                new DrawableStep(
                        DisplayPatterns.makeCheckerboard(),
                        gr ? "Σκακιέρα — mura / stains" : "Checkerboard — mura"),

                new DrawableStep(
                        DisplayPatterns.makeBurnInCycle(),
                        gr ? "Κύκλος καταπόνησης OLED" : "Burn-in stress cycle")
        };

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

        boolean gr = AppLang.isGreek(this);

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
                s.label + "\n\n" +
                        (gr ? "Κύκλος " : "Cycle ") +
                        (loopIndex + 1) + " / " + LOOP_COUNT
        );

        stepIndex++;
        h.postDelayed(this::runStep, STEP_DURATION_MS);
    }

    // ============================================================
    // FINAL QUESTION
    // ============================================================
    private void finishTest() {

        final boolean gr = AppLang.isGreek(this);

        String text = gr
                ? "Παρατήρησες κάποιο πρόβλημα στην οθόνη;\n\n"
                + "• Burn-in / αποτύπωση\n"
                + "• Ζώνες χρώματος\n"
                + "• Κηλίδες / mura\n"
                + "• Ανομοιομορφία"
                : "Did you notice any display issues?\n\n"
                + "• Burn-in\n"
                + "• Color banding\n"
                + "• Stains / mura\n"
                + "• Uneven brightness";

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);
        root.addView(buildHeader(this,
                gr ? "Οπτικός Έλεγχος" : "Visual Inspection"));

        SpannableString span = new SpannableString(text);
        span.setSpan(new ForegroundColorSpan(0xFF39FF14),
                0,
                text.indexOf("\n"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView msg = buildMessage(this, span);
        root.addView(msg);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);

        Button no  = gelButton(this,
                gr ? "ΟΧΙ\nΗ οθόνη είναι ΟΚ" : "NO\nScreen OK",
                0xFF0F8A3B);

        Button yes = gelButton(this,
                gr ? "ΝΑΙ\nΥπάρχουν προβλήματα" : "YES\nIssues found",
                0xFFB00020);

        setDualButtons(no, yes, buttons);
        root.addView(buttons);

        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();

        AppTTS.speak(this, text);

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
    private LinearLayout buildPopupRoot(Context ctx) {
        LinearLayout l = new LinearLayout(ctx);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(24), dp(22), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        l.setBackground(bg);

        return l;
    }

    private TextView buildHeader(Context ctx, String text) {
        TextView t = new TextView(ctx);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setTextSize(18f);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 0, 0, dp(12));
        return t;
    }

    private TextView buildMessage(Context ctx, CharSequence text) {
        TextView t = new TextView(ctx);
        t.setText(text);
        t.setTextColor(0xFF39FF14);
        t.setTextSize(15f);
        t.setGravity(Gravity.CENTER);
        t.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return t;
    }

    private Button gelButton(Context ctx, String text, int color) {
        Button b = new Button(ctx);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15f);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(3), 0xFFFFD700);
        b.setBackground(bg);

        return b;
    }

    private void setDualButtons(Button l, Button r, LinearLayout parent) {
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(52), 1f);
        l.setLayoutParams(lp);
        r.setLayoutParams(lp);

        parent.addView(l);
        parent.addView(space(dp(12)));
        parent.addView(r);
    }

    private View space(int w) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(w, 1));
        return v;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
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
