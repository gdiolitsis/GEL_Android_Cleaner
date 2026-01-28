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
import android.widget.Button;
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

private volatile boolean userCanceled = false;

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

    // dummy root ώστε το Activity να έχει window token
    FrameLayout dummy = new FrameLayout(this);
    dummy.setBackgroundColor(Color.BLACK);
    setContentView(dummy);

    // δείξε popup ΜΟΝΟ αφού “δέσει” το window
    dummy.post(() -> {
        if (!isFinishing() && !isDestroyed() && activityAlive) {
            showOledWarning();
        }
    });
}

// ============================================================
// SAFE CANCEL — USER ABORT (GLOBAL)
// ============================================================

private void safeCancel() {
    if (userCanceled) return;
    userCanceled = true;
    endTest(false, false);
}

@Override
protected void onDestroy() {
    activityAlive = false;
    h.removeCallbacksAndMessages(null);
    AppTTS.stop();
    super.onDestroy();
}

private void endTest(boolean completed, boolean issues) {

    if (!activityAlive) return;

    activityAlive = false;
    try { h.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    if (userCanceled) {
        GELServiceLog.logInfo(
                "LAB Display Pro Test — CANCELED by user"
        );
        setResult(RESULT_CANCELED);
        finish();
        return;
    }

    if (completed) {
        if (issues) {
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
}

    // ============================================================
    // BACK = EXIT TEST
    // ============================================================
@Override
public void onBackPressed() {
    safeCancel();
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

        // =========================
        // EXIT BUTTON
        // =========================
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

        exitBtn.setOnClickListener(v -> {
    safeCancel();
});

        root.addView(exitBtn);
        setContentView(root);

        startTimeMs = System.currentTimeMillis();
        stepIndex = 0;
        loopIndex = 0;

        runStep();
    }

    private void runStep() {

    if (userCanceled || !activityAlive || isFinishing() || isDestroyed()) {
        return;
    }

// MAX RUNTIME
if (System.currentTimeMillis() - startTimeMs > MAX_RUNTIME_MS) {
    finishTest();   // ❌ ΟΧΙ endTest
    return;
}

// LOOPS
if (stepIndex >= steps.length) {
    stepIndex = 0;
    loopIndex++;

    if (loopIndex >= LOOP_COUNT) {
        finishTest();   // ❌ ΟΧΙ endTest
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
// POPUP 2 — FINAL QUESTION
// ============================================================
private void finishTest() {

    if (userCanceled) {
        endTest(false, false);
        return;
    }

    if (isFinishing() || isDestroyed()) {
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

    if (!isFinishing() && !isDestroyed()) {
        d.show();
    } else {
        return;
    }

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (activityAlive && !AppTTS.isMuted()) {
            AppTTS.ensureSpeak(this, text);
        }
    }, 120);

    no.setOnClickListener(v -> endTest(true, false));
yes.setOnClickListener(v -> endTest(true, true));
}

    // ============================================================
    // UI HELPERS (LOCAL)
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

    private LinearLayout buildHeaderWithMute(String titleText) {

        final boolean gr = AppLang.isGreek(this);

        LinearLayout h = new LinearLayout(this);
        h.setOrientation(LinearLayout.HORIZONTAL);
        h.setGravity(Gravity.CENTER_VERTICAL);
        h.setPadding(0, 0, 0, dp(12));

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        );

        Button mute = new Button(this);
        mute.setAllCaps(false);
        mute.setTextSize(14f);
        mute.setPadding(dp(16), dp(8), dp(16), dp(8));
        mute.setMinWidth(0);
        mute.setMinimumWidth(0);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF444444);
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(2), 0xFFFFD700);
        mute.setBackground(bg);

        mute.setText(
                AppTTS.isMuted()
                        ? (gr ? "Ήχος ON" : "Unmute")
                        : (gr ? "Σίγαση" : "Mute")
        );

        mute.setOnClickListener(v -> {
            boolean m = !AppTTS.isMuted();
            AppTTS.setMuted(this, m);
            mute.setText(
                    m
                            ? (gr ? "Ήχος ON" : "Unmute")
                            : (gr ? "Σίγαση" : "Mute")
            );
            if (m) AppTTS.stop();
        });

        h.addView(title);
        h.addView(mute);
        return h;
    }

    private TextView buildMessage(CharSequence text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(0xFF39FF14);
        tv.setTextSize(15f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, 0, 0, dp(16));
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
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
