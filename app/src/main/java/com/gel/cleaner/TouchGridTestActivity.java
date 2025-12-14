package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * LAB 6 — Touch Grid Diagnostic
 * ------------------------------------------------------------
 * Full-screen touch grid.
 * User must pass finger over ALL dots.
 * If all dots cleared → PASS automatically.
 * If any dead zone exists → user must END TEST → FAIL.
 *
 * Author: GDiolitsis Engine Lab (GEL)
 * ============================================================
 */
public class TouchGridTestActivity extends AppCompatActivity {

    private TouchGridView gridView;
    private boolean passed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Root
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        // Grid view
        gridView = new TouchGridView(this, () -> {
            // ALL DOTS CLEARED
            passed = true;
            finishWithResult(Activity.RESULT_OK);
        });

        root.addView(gridView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        // Overlay controls (top + bottom)
        LinearLayout overlay = new LinearLayout(this);
        overlay.setOrientation(LinearLayout.VERTICAL);
        overlay.setGravity(Gravity.CENTER_HORIZONTAL);
        overlay.setPadding(dp(12), dp(12), dp(12), dp(12));

        // Title
        TextView title = new TextView(this);
        title.setText("LAB 6 — Touch Grid Test");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        overlay.addView(title);

        // Instruction
        TextView info = new TextView(this);
        info.setText("Swipe your finger across the entire screen.\nAll dots must disappear.");
        info.setTextColor(0xFF39FF14); // GEL green
        info.setTextSize(14f);
        info.setGravity(Gravity.CENTER_HORIZONTAL);
        info.setPadding(0, dp(6), 0, dp(12));
        overlay.addView(info);

        // END TEST button (always visible)
        Button end = new Button(this);
        end.setText("END TEST");
        end.setAllCaps(false);
        end.setTextColor(Color.WHITE);
        end.setBackgroundColor(0xFF8B0000); // dark red
        end.setOnClickListener(v -> finishWithResult(Activity.RESULT_CANCELED));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lp.topMargin = dp(12);
        overlay.addView(end, lp);

        FrameLayout.LayoutParams olp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP
                );

        root.addView(overlay, olp);

        setContentView(root);
    }

    private void finishWithResult(int result) {
        setResult(result);
        finish();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    // ============================================================
    // TOUCH GRID VIEW
    // ============================================================
    private static class TouchGridView extends View {

        interface OnGridComplete {
            void onComplete();
        }

        private static class Dot {
            float x, y;
            boolean cleared = false;
        }

        private final List<Dot> dots = new ArrayList<>();
        private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint clearedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float radius;
        private final OnGridComplete callback;

        TouchGridView(Context ctx, OnGridComplete cb) {
            super(ctx);
            this.callback = cb;
            radius = dp(ctx, 14);

            dotPaint.setColor(Color.RED);
            clearedPaint.setColor(0xFF39FF14); // green
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            dots.clear();

            // GRID SIZE (LOCKED)
            int cols = 6;
            int rows = 10;

            float dx = w / (float) (cols + 1);
            float dy = h / (float) (rows + 1);

            for (int r = 1; r <= rows; r++) {
                for (int c = 1; c <= cols; c++) {
                    Dot d = new Dot();
                    d.x = c * dx;
                    d.y = r * dy;
                    dots.add(d);
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (Dot d : dots) {
                canvas.drawCircle(
                        d.x,
                        d.y,
                        radius,
                        d.cleared ? clearedPaint : dotPaint
                );
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_MOVE ||
                event.getAction() == MotionEvent.ACTION_DOWN) {

                float x = event.getX();
                float y = event.getY();

                boolean anyChange = false;

                for (Dot d : dots) {
                    if (!d.cleared) {
                        float dx = x - d.x;
                        float dy = y - d.y;
                        if (dx * dx + dy * dy <= radius * radius) {
                            d.cleared = true;
                            anyChange = true;
                        }
                    }
                }

                if (anyChange) {
                    invalidate();
                    if (allCleared()) {
                        callback.onComplete();
                    }
                }
            }
            return true;
        }

        private boolean allCleared() {
            for (Dot d : dots) {
                if (!d.cleared) return false;
            }
            return true;
        }

        private static float dp(Context c, int v) {
            return v * c.getResources().getDisplayMetrics().density;
        }
    }
}
