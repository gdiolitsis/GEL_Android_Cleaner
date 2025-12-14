package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * ============================================================
 * LAB 8 — Proximity Sensor Diagnostic
 * ------------------------------------------------------------
 * Detects NEAR event via proximity sensor.
 * PASS: sensor changes when covered.
 * FAIL: no change detected.
 * ============================================================
 */
public class ProximityCheckActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sm;
    private Sensor proximity;
    private boolean detected = false;
    private Button okBtn;
    private float maxRange = -1f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximity = sm != null ? sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) : null;

        // ---------- UI ----------
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(24), dp(24), dp(24), dp(24));

        TextView title = new TextView(this);
        title.setText("LAB 8 — Proximity Check");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setGravity(Gravity.CENTER);
        box.addView(title);

        TextView info = new TextView(this);
        info.setText("Cover the front camera / proximity area with your palm.");
        info.setTextColor(0xFF39FF14);
        info.setTextSize(14f);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, dp(12), 0, dp(20));
        box.addView(info);

        okBtn = new Button(this);
        okBtn.setText("OK");
        okBtn.setEnabled(false);
        okBtn.setAllCaps(false);
        okBtn.setOnClickListener(v -> finishWithResult(Activity.RESULT_OK));
        box.addView(okBtn,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dp(52)));

        Button end = new Button(this);
        end.setText("END TEST");
        end.setAllCaps(false);
        end.setBackgroundColor(0xFF8B0000);
        end.setTextColor(Color.WHITE);
        end.setOnClickListener(v -> finishWithResult(Activity.RESULT_CANCELED));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        lp.topMargin = dp(12);
        box.addView(end, lp);

        root.addView(box,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sm != null && proximity != null) {
            maxRange = proximity.getMaximumRange();
            sm.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sm != null)
            sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        if (detected) return;

        float value = e.values[0];

        // NEAR detection
        if (value < maxRange) {
            detected = true;
            okBtn.setEnabled(true);
            okBtn.setText("OK (Proximity detected)");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void finishWithResult(int r) {
        setResult(r);
        finish();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
