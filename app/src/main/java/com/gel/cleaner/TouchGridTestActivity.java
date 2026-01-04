package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * ============================================================
 * LAB 6 — Display / Touch Grid Test (FINAL / LOCKED)
 * ============================================================
 */
public class TouchGridTestActivity extends Activity {

    // ==========================
    // TEXT TO SPEECH
    // ==========================
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean ttsMuted = false;

    private TouchGridView gridView;
    private Button endButton;

    private boolean testStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showIntroPopup();
    }

    // ============================================================
    // INTRO POPUP (BEFORE TEST)
    // ============================================================
    private void showIntroPopup() {

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("LAB 6 — Touch Test");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        root.addView(title);

        TextView msg = new TextView(this);
        msg.setText(
                "Touch all dots on the screen.\n\n" +
                "All areas must respond to touch input."
        );
        msg.setTextColor(0xFFDDDDDD);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        root.addView(msg);

        CheckBox muteBox = new CheckBox(this);
        muteBox.setText("Mute voice instructions");
        muteBox.setTextColor(0xFFDDDDDD);
        muteBox.setChecked(false);
        muteBox.setGravity(Gravity.CENTER);
        muteBox.setPadding(0, dp(10), 0, dp(10));
        root.addView(muteBox);

        Button startBtn = new Button(this);
        startBtn.setText("START TEST");
        startBtn.setAllCaps(false);
        startBtn.setTextColor(Color.WHITE);

        GradientDrawable startBg = new GradientDrawable();
        startBg.setColor(0xFF39FF14);
        startBg.setCornerRadius(dp(14));
        startBg.setStroke(dp(3), 0xFFFFD700);
        startBtn.setBackground(startBg);

        root.addView(startBtn);

        b.setView(root);

        AlertDialog d = b.create();
        d.getWindow().setBackgroundDrawable(null);
        d.show();

        // ==========================
        // TTS INIT + SPEAK (BEFORE START)
        // ==========================
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int res = tts.setLanguage(Locale.US);
                ttsReady =
                        res != TextToSpeech.LANG_MISSING_DATA &&
                        res != TextToSpeech.LANG_NOT_SUPPORTED;

                if (ttsReady && !ttsMuted) {
                    tts.speak(
                            "Touch all dots on the screen to complete the test.",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "LAB6_INTRO"
                    );
                }
            }
        });

        muteBox.setOnCheckedChangeListener((v, checked) -> {
            ttsMuted = checked;
            if (checked && tts != null) tts.stop();
        });

        startBtn.setOnClickListener(v -> {
            if (tts != null) tts.stop();
            d.dismiss();
            startTestUI();
        });
    }

    // ============================================================
    // MAIN TEST UI
    // ============================================================
    private void startTestUI() {

        if (testStarted) return;
        testStarted = true;

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        gridView = new TouchGridView(this);
        root.addView(gridView);

        endButton = new Button(this);
        endButton.setText("End Test");
        endButton.setAllCaps(false);
        endButton.setTextSize(16f);
        endButton.setTextColor(Color.WHITE);
        endButton.setBackgroundColor(0xFF8B0000);

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = dp(20);
        endButton.setLayoutParams(lp);

        endButton.setOnClickListener(v -> {
            gridView.logIncompleteResult();
            setResult(RESULT_CANCELED);
            finish();
        });

        root.addView(endButton);
        setContentView(root);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Throwable ignore) {}
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }

    // ============================================================
    // TOUCH GRID VIEW
    // ============================================================
    private class TouchGridView extends View {

        private final int COLS = 8;
        private final int ROWS = 12;

        private boolean[][] cleared;
        private Paint dotPaint;

        private float cellW, cellH;
        private float radius;

        public TouchGridView(Activity ctx) {
            super(ctx);
            cleared = new boolean[ROWS][COLS];
            dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dotPaint.setColor(Color.YELLOW);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            cellW = w / (float) COLS;
            cellH = h / (float) ROWS;
            radius = Math.min(cellW, cellH) * 0.25f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (!cleared[r][c]) {
                        float cx = c * cellW + cellW / 2f;
                        float cy = r * cellH + cellH / 2f;
                        canvas.drawCircle(cx, cy, radius, dotPaint);
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_MOVE) {

                int c = (int) (event.getX() / cellW);
                int r = (int) (event.getY() / cellH);

                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    if (!cleared[r][c]) {
                        cleared[r][c] = true;
                        invalidate();

                        if (allCleared()) {
                            logSuccessResult();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
                return true;
            }
            return false;
        }

        private boolean allCleared() {
            for (int r = 0; r < ROWS; r++)
                for (int c = 0; c < COLS; c++)
                    if (!cleared[r][c]) return false;
            return true;
        }

        private int countUncleared() {
            int n = 0;
            for (int r = 0; r < ROWS; r++)
                for (int c = 0; c < COLS; c++)
                    if (!cleared[r][c]) n++;
            return n;
        }

private void logSuccessResult() {

    GELServiceLog.section("LAB 6 — Display / Touch");

    GELServiceLog.ok("Touch grid test completed.");
    GELServiceLog.ok("All screen zones responded to touch input.");
    GELServiceLog.ok("No dead touch zones detected.");

    GELServiceLog.ok("Lab 6 finished.");
    GELServiceLog.addLine(null);
}

private void logIncompleteResult() {

    int total = ROWS * COLS;
    int remaining = countUncleared();

    GELServiceLog.section("LAB 6 — Display / Touch");

    GELServiceLog.warn("Touch grid test incomplete.");

    GELServiceLog.warn(
            "These " + remaining +
            " screen zones did not respond to touch input (" +
            remaining + " / " + total + ")."
    );

    GELServiceLog.info("This may indicate:");
    GELServiceLog.error("• Localized digitizer dead zones");
    GELServiceLog.warn("Manual re-test is recommended to confirm behavior.");

    GELServiceLog.ok("Lab 6 finished.");
    GELServiceLog.addLine(null);
}
    }
}
