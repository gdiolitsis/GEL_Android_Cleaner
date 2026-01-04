package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Locale;

/**
 * ============================================================
 * LAB 6 — Display / Touch Grid Test (LOCKED)
 * ============================================================
 */
public class TouchGridTestActivity extends Activity {

    // ==========================
    // TEXT TO SPEECH
    // ==========================
    private TextToSpeech tts;
    private boolean ttsReady = false;

    private TouchGridView gridView;
    private Button endButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔒 Force portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 🔒 Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
            logIncompleteResult();
            setResult(RESULT_CANCELED);
            finish();
        });

        root.addView(endButton);
        setContentView(root);

        // ==========================
        // TTS INIT
        // ==========================
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int res = tts.setLanguage(Locale.US);
                ttsReady =
                        res != TextToSpeech.LANG_MISSING_DATA &&
                        res != TextToSpeech.LANG_NOT_SUPPORTED;

                if (ttsReady) {
                    tts.speak(
                            "Touch all dots to complete the test.",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "LAB6"
                    );
                }
            }
        });

        Toast.makeText(
                this,
                "Touch all dots to complete the test",
                Toast.LENGTH_SHORT
        ).show();
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
    // ACTIVITY-LEVEL LOG DELEGATE
    // ============================================================
    private void logIncompleteResult() {
        if (gridView != null) {
            gridView.logIncompleteResult();
        }
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
            super.onSizeChanged(w, h, oldw, oldh);
            cellW = w / (float) COLS;
            cellH = h / (float) ROWS;
            radius = Math.min(cellW, cellH) * 0.25f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

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
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (!cleared[r][c]) return false;
                }
            }
            return true;
        }

        private int countUncleared() {
            int n = 0;
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if (!cleared[r][c]) n++;
                }
            }
            return n;
        }

        // ========================================================
        // LOGGING (SERVICE REPORT)
        // ========================================================
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
            GELServiceLog.warn("Untouched zones detected: " + remaining + " / " + total);
            GELServiceLog.info("Possible causes:");
            GELServiceLog.warn("• User ended the test before completing all zones");
            GELServiceLog.warn("• These " + remaining + " screen areas did not register touch input");
            GELServiceLog.info("Manual re-test recommended.");
            GELServiceLog.ok("Lab 6 finished.");
            GELServiceLog.addLine(null);
        }
    }
}
