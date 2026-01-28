package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayProTestActivity extends Activity {

    // ============================================================
    // CONFIG
    // ============================================================
    private static final int STEP_DURATION_MS = 2500;
    private static final int LOOP_COUNT = 3;
    private static final long MAX_RUNTIME_MS = 5 * 60 * 1000;

    // ============================================================
    // GLOBAL STATE (LOCKED FLOW)
    // ============================================================
    private volatile boolean testFinished = false;
    private volatile boolean activityAlive = true;

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
    // APPLY APP LANGUAGE
    // ============================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAlive = true;

        FrameLayout dummy = new FrameLayout(this);
        dummy.setBackgroundColor(Color.BLACK);
        setContentView(dummy);

        dummy.post(() -> {
            if (!isFinishing() && !isDestroyed() && activityAlive) {
                showOledWarning();
            }
        });
    }

    // ============================================================
    // SAFE CANCEL — THE ONLY CANCEL PATH
    // ============================================================
    private void safeCancel() {

        if (testFinished) return;
        testFinished = true;

        try { h.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}
        try { AppTTS.stop(); } catch (Throwable ignore) {}

        GELServiceLog.logInfo(
                "LAB Display Pro Test — CANCELED by user"
        );

        setResult(RESULT_CANCELED);
        finish();
    }

@Override
public void onBackPressed() {
    safeCancel();
}

    @Override
    protected void onDestroy() {
        activityAlive = false;
        h.removeCallbacksAndMessages(null);
        AppTTS.stop();
        super.onDestroy();
    }

    // ============================================================
    // POPUP 1 — OLED WARNING
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
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);
        root.addView(buildHeaderWithMute(
                gr ? "Δοκιμή Καταπόνησης Οθόνης" : "Display Stress Test"
        ));
        root.addView(buildMessage(text));

// 👇 ΕΔΩ ΜΠΑΙΝΕΙ ΤΟ MUTE
root.addView(buildMuteRow());

LinearLayout buttons = new LinearLayout(this);
buttons.setOrientation(LinearLayout.HORIZONTAL);
buttons.setGravity(Gravity.CENTER);

Button cancel = gelButton(gr ? "ΑΚΥΡΩΣΗ" : "CANCEL", 0xFFB00020);
Button start  = gelButton(gr ? "ΕΝΑΡΞΗ" : "START",  0xFF0F8A3B);

        setDualButtons(cancel, start, buttons);
        root.addView(buttons);

        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.show();

        new Handler(Looper.getMainLooper()).postDelayed(
                () -> AppTTS.ensureSpeak(this, text),
                120
        );

        cancel.setOnClickListener(v -> {
            d.dismiss();
            safeCancel();
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
                new SolidStep(Color.BLACK, gr ? "Μαύρο — φωτεινά pixels" : "Black — bright pixels"),
                new SolidStep(Color.WHITE, gr ? "Λευκό — σκοτεινά σημεία" : "White — dark spots"),
                new SolidStep(Color.RED,   gr ? "Κόκκινο — burn-in"       : "Red — burn-in"),
                new SolidStep(Color.GREEN, gr ? "Πράσινο — ομοιομορφία"  : "Green — uniformity"),
                new SolidStep(Color.BLUE,  gr ? "Μπλε — ομοιομορφία"     : "Blue — uniformity"),
                new DrawableStep(DisplayPatterns.makeGradient(),
                        gr ? "Διαβάθμιση — banding" : "Gradient — banding"),
                new DrawableStep(DisplayPatterns.makeCheckerboard(),
                        gr ? "Σκακιέρα — mura" : "Checkerboard — mura"),
                new DrawableStep(DisplayPatterns.makeBurnInCycle(),
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

        Button exitBtn = new Button(this);
        exitBtn.setAllCaps(false);
        exitBtn.setText(gr ? "ΕΞΟΔΟΣ" : "EXIT");
        exitBtn.setTextColor(Color.WHITE);
        exitBtn.setTextSize(15f);

        GradientDrawable exitBg = new GradientDrawable();
        exitBg.setColor(0xFF8B0000);
        exitBg.setCornerRadius(dp(14));
        exitBg.setStroke(dp(3), 0xFFFFD700);
        exitBtn.setBackground(exitBg);

        FrameLayout.LayoutParams lpExit =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        lpExit.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lpExit.bottomMargin = dp(24);
        exitBtn.setLayoutParams(lpExit);

        exitBtn.setOnClickListener(v -> safeCancel());

        root.addView(exitBtn);
        setContentView(root);

        startTimeMs = System.currentTimeMillis();
        stepIndex = 0;
        loopIndex = 0;

        runStep();
    }

    // ============================================================
    // MAIN LOOP
    // ============================================================
    private void runStep() {

        if (testFinished || !activityAlive
                || isFinishing() || isDestroyed()) {
            return;
        }

        if (System.currentTimeMillis() - startTimeMs > MAX_RUNTIME_MS) {
            showFinalQuestion();
            return;
        }

        if (stepIndex >= steps.length) {
            stepIndex = 0;
            loopIndex++;
            if (loopIndex >= LOOP_COUNT) {
                showFinalQuestion();
                return;
            }
        }

        TestStep s = steps[stepIndex];
        s.apply(root);

        hint.setText(
                s.label + "\n\n" +
                (AppLang.isGreek(this) ? "Κύκλος " : "Cycle ") +
                (loopIndex + 1) + " / " + LOOP_COUNT
        );

        stepIndex++;
        h.postDelayed(this::runStep, STEP_DURATION_MS);
    }

    // ============================================================
    // FINAL QUESTION (NOT TERMINATION)
    // ============================================================
    private void showFinalQuestion() {

        if (testFinished || !activityAlive
                || isFinishing() || isDestroyed()) {
            return;
        }

        final boolean gr = AppLang.isGreek(this);

        final String text =
                gr
                        ? "Παρατήρησες κάποιο πρόβλημα στην οθόνη;\n\n"
                        + "• Burn-in;\n• Ζώνες χρώματος;\n• Κηλίδες / mura;\n• Ανομοιομορφία;"
                        : "Did you notice any display issues?\n\n"
                        + "• Burn-in?\n• Color banding?\n• Stains / mura?\n• Uneven brightness?";

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_NoActionBar);
        b.setCancelable(false);

        LinearLayout root = buildPopupRoot(this);
        root.addView(buildHeaderWithMute(
                gr ? "Οπτικός Έλεγχος" : "Visual Inspection"
        ));

        SpannableString span = new SpannableString(text);
        span.setSpan(
                new ForegroundColorSpan(0xFF39FF14),
                0,
                text.indexOf("\n"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        root.addView(buildMessage(span));

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);

        Button no  = gelButton(gr ? "ΟΧΙ\nΟΚ" : "NO\nOK", 0xFF0F8A3B);
        Button yes = gelButton(gr ? "ΝΑΙ\nΠρόβλημα" : "YES\nIssue", 0xFFB00020);

        setDualButtons(no, yes, buttons);
        root.addView(buttons);

        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (!testFinished && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, text);
    }
}, 120);

no.setOnClickListener(v -> endTest(false));
yes.setOnClickListener(v -> endTest(true));
}

    // ============================================================
    // FINAL TERMINATION
    // ============================================================
    private void endTest(boolean issuesDetected) {

        if (testFinished) return;
        testFinished = true;

        try { h.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}
        try { AppTTS.stop(); } catch (Throwable ignore) {}

        if (issuesDetected) {
            GELServiceLog.logInfo(
                    "LAB Display Pro Test — COMPLETED (ISSUES DETECTED)"
            );
        } else {
            GELServiceLog.logInfo(
                    "LAB Display Pro Test — COMPLETED"
            );
        }

        setResult(RESULT_OK);
        finish();
    }

// ============================================================
// UI HELPERS
// ============================================================

private LinearLayout buildPopupRoot(Context ctx) {
    LinearLayout r = new LinearLayout(ctx);
    r.setOrientation(LinearLayout.VERTICAL);
    r.setPadding(dp(24), dp(22), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    r.setBackground(bg);

    return r;
}

// ------------------------------------------------------------
// HEADER (TITLE ONLY — NO MUTE HERE)
// ------------------------------------------------------------
private LinearLayout buildHeader(String titleText) {

    LinearLayout h = new LinearLayout(this);
    h.setOrientation(LinearLayout.VERTICAL);
    h.setPadding(0, 0, 0, dp(12));

    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);

    h.addView(title);
    return h;
}

// ------------------------------------------------------------
// MUTE ROW (CHECKBOX + LABEL — ABOVE BUTTONS)
// ------------------------------------------------------------
private LinearLayout buildMuteRow() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);
    row.setPadding(0, dp(8), 0, dp(16));

    CheckBox muteCheck = new CheckBox(this);
    muteCheck.setChecked(AppTTS.isMuted(this));
    muteCheck.setPadding(0, 0, dp(6), 0);

    TextView label = new TextView(this);
    label.setText(
            gr
                    ? "Σίγαση φωνητικών οδηγιών"
                    : "Mute voice instructions"
    );
    label.setTextColor(0xFFAAAAAA);
    label.setTextSize(14f);

    // ένα σημείο αλήθειας: AppTTS
    View.OnClickListener toggle = v -> {
        boolean newState = !AppTTS.isMuted(this);
        AppTTS.setMuted(this, newState);
        muteCheck.setChecked(newState);
    };

    row.setOnClickListener(toggle);
    label.setOnClickListener(toggle);

    muteCheck.setOnCheckedChangeListener((b, checked) -> {
        if (checked != AppTTS.isMuted(this)) {
            AppTTS.setMuted(this, checked);
        }
    });

    row.addView(muteCheck);
    row.addView(label);

    return row;
}

    private TextView buildMessage(CharSequence text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF39FF14);
        tv.setTextSize(15f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 0, 0, dp(16));
        return tv;
    }

    private Button gelButton(String text, int color) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15f);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(3), 0xFFFFD700);
        b.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(52), 1f);
        lp.setMargins(dp(6), 0, dp(6), 0);
        b.setLayoutParams(lp);
        return b;
    }

    private void setDualButtons(Button l, Button r, LinearLayout p) {
        p.removeAllViews();
        p.addView(l);
        p.addView(r);
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
