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
 * LAB 7 — Rotation Check (LOCKED)
 * ------------------------------------------------------------
 * Detects real device rotation via accelerometer
 *
 * Exit:
 *  - RESULT_OK       → rotation detected
 *  - RESULT_CANCELED → user pressed "End Test"
 *
 * No loops. No threads. No timers.
 * ============================================================
 */
public class RotationCheckActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float lastX = 0;
    private float lastY = 0;
    private boolean initialized = false;

    private static final float ROTATION_THRESHOLD = 4.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lock orientation to portrait (rotation detection via sensors, not UI)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null)
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        FrameLayout root = new FrameLayout(this);

        TextView info = new TextView(this);
        info.setText("Rotate the device to complete the test");
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
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
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
        float x = event.values[0];
        float y = event.values[1];

        if (!initialized) {
            lastX = x;
            lastY = y;
            initialized = true;
            return;
        }

        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);

        if (dx > ROTATION_THRESHOLD || dy > ROTATION_THRESHOLD) {
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
