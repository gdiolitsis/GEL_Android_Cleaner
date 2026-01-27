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

import java.util.Locale;

public class DisplayProTestActivity extends Activity {

    // ============================================================
    // CONFIG
    // ============================================================
    private static final int STEP_DURATION_MS = 2500;
    private static final int LOOP_COUNT = 3;
    private static final long MAX_RUNTIME_MS = 5 * 60 * 1000;

    // ============================================================
    // STATE
    // ============================================================
    private FrameLayout root;
    private TextView hint;
    private int stepIndex = 0;
    private int loopIndex = 0;
    private long startTimeMs;

    private TestStep[] steps;

    private final Handler h = new Handler(Looper.getMainLooper());

    // ============================================================
    // 🔴 CRITICAL — APPLY APP LANGUAGE
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

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
    // OLED WARNING POPUP
    // ============================================================
    private void showOledWarning() {

        final boolean gr = AppLang.isGreek(this);

        final String text =
                gr
                        ? "Η δοκιμή αυτή οδηγεί την οθόνη στη μέγιστη φωτεινότητα\n"
                        + "και μπορεί να καταπονήσει προσωρινά πάνελ OLED.\n\n"
                        + "Συνέχισε μόνο αν κατανοείς και αποδέχεσαι τον κίνδυνο."
                        : "This test drives the display at maximum brightness\n"
                        + "and may temporarily stress OLED panels.\n\n"
                        + "Proceed only if you understand and accept this.";

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);

        // HEADER + MUTE
        root.addView(
                buildPopupHeaderWithMute(
                        this,
                        gr ? "Δοκιμή Καταπόνησης Οθόνης" : "Display Stress Test"
                )
        );

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

        AppTTS.stop();
        h.postDelayed(() -> AppTTS.speak(this, text), 120);

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

        final boolean gr = AppLang.isGreek(this);

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

        final boolean gr = AppLang.isGreek(this);

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

        final String text =
                gr
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
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);

        root.addView(
                buildPopupHeaderWithMute(
                        this,
                        gr ? "Οπτικός Έλεγχος" : "Visual Inspection"
                )
        );

        SpannableString span = new SpannableString(text);
        span.setSpan(
                new ForegroundColorSpan(0xFF39FF14),
                0,
                text.indexOf("\n"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

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

        AppTTS.stop();
        h.postDelayed(() -> AppTTS.speak(this, text), 120);

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
    // UI HELPERS
    // ============================================================
    private LinearLayout buildPopupRoot(Context ctx) {

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(22), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        return root;
    }

    private LinearLayout buildPopupHeaderWithMute(Context ctx, String titleText) {

        LinearLayout header = new LinearLayout(ctx);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, dp(12));

        TextView title = new TextView(ctx);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        );

        Button mute = gelButton(
                ctx,
                AppTTS.isMuted()
                        ? (AppLang.isGreek(ctx) ? "Ήχος ON" : "Unmute")
                        : (AppLang.isGreek(ctx) ? "Σίγαση" : "Mute"),
                0xFF444444
        );

        mute.setOnClickListener(v -> {
            boolean m = !AppTTS.isMuted();
            AppTTS.setMuted(ctx, m);
            mute.setText(
                    m
                            ? (AppLang.isGreek(ctx) ? "Ήχος ON" : "Unmute")
                            : (AppLang.isGreek(ctx) ? "Σίγαση" : "Mute")
            );
            if (m) AppTTS.stop();
        });

        header.addView(title);
        header.addView(mute);

        return header;
    }

    private TextView buildMessage(Context ctx, CharSequence text) {

        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(0xFF39FF14);
        tv.setTextSize(15f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 0, 0, dp(16));

        tv.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        return tv;
    }

    private Button gelButton(Context ctx, String text, int bgColor) {

        Button b = new Button(ctx);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15f);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(3), 0xFFFFD700);
        b.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lp.setMargins(dp(6), 0, dp(6), 0);
        b.setLayoutParams(lp);

        return b;
    }

    private void setDualButtons(Button left, Button right, LinearLayout parent) {
        parent.removeAllViews();
        parent.addView(left);
        parent.addView(space(dp(12)));
        parent.addView(right);
    }

    private View space(int w) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(w, 1));
        return v;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
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
