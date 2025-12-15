package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * ============================================================
 * LAB 8 — Proximity Sensor Check (LOCKED)
 * ------------------------------------------------------------
 * Detects proximity sensor state change (near / far)
 *
 * Exit:
 *  - RESULT_OK       → proximity change detected
 *  - RESULT_CANCELED → user pressed "End Test"
 *
 * No loops. No threads. No timers.
 * ============================================================
 */
public class ProximityCheckActivity extends Activity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximity;

    private boolean initialRead = false;
    private float initialValue = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null)
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // ============================================================
        // ROOT
        // ============================================================
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010); // GEL black

        // ============================================================
        // INFO TEXT
        // ============================================================
        TextView info = new TextView(this);
        info.setText(
                "Place your hand over the front sensor area\n" +
                "(near the earpiece / front camera)"
        );
        info.setTextSize(18f);
        info.setTextColor(Color.WHITE);
        info.setGravity(Gravity.CENTER);
        info.setPadding(dp(16), dp(16), dp(16), dp(16));

        FrameLayout.LayoutParams infoLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        infoLp.gravity = Gravity.CENTER;
        info.setLayoutParams(infoLp);

        root.addView(info);

        // ============================================================
        // END TEST BUTTON (RED — SAME AS LAB 6)
        // ============================================================
        Button end = new Button(this);
        end.setText("END TEST");
        end.setAllCaps(false);
        end.setTextColor(Color.WHITE);
        end.setTextSize(15f);
        end.setTypeface(null, Typeface.BOLD);

        GradientDrawable redBtn = new GradientDrawable();
        redBtn.setColor(0xFF8B0000);          // dark red
        redBtn.setCornerRadius(dp(14));
        redBtn.setStroke(dp(3), 0xFFFFD700);  // gold border
        end.setBackground(redBtn);

        FrameLayout.LayoutParams endLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        dp(56)
                );
        endLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        endLp.leftMargin = dp(24);
        endLp.rightMargin = dp(24);
        endLp.bottomMargin = dp(24);
        end.setLayoutParams(endLp);

        end.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        root.addView(end);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && proximity != null) {
            sensorManager.registerListener(
                    this,
                    proximity,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float v = event.values[0];

        if (!initialRead) {
            initialValue = v;
            initialRead = true;
            return;
        }

        // Any significant change = sensor responded
        if (Math.abs(v - initialValue) > 0.5f) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
    }
