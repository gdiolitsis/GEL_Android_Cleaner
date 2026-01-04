package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * ============================================================
 * LAB 6 — Display / Touch Grid Test
 * FINAL — NO POPUPS • NO TTS • NO MUTE
 * ============================================================
 */
public class TouchGridTestActivity extends Activity {

    private TouchGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        gridView = new TouchGridView(this);
        root.addView(gridView);

        Button endButton = new Button(this);
        endButton.setText("END TEST");
        endButton.setAllCaps(false);
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

        TouchGridView(Activity ctx) {
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

        // ========================================================
        // LOGGING
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
