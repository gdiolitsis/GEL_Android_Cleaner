package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ActivityInfo;
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
public class ProximityCheckActivity extends Activity implements SensorEventListener {

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

        FrameLayout root = new FrameLayout(this);

        TextView info = new TextView(this);
        info.setText("Cover the front sensor area with your hand");
        info.setTextSize(18f);
        info.setGravity(Gravity.CENTER);
        root.addView(info);

        Button end = new Button(this);
        end.setText("End Test");
        end.setAllCaps(false);

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lp.bottomMargin = dp(24);
        end.setLayoutParams(lp);

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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
