// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 2 — Auto Foldable Detector (Hinge Angle + Unified Callback Bridge)
// v2.1 — Fully Compatible with GELFoldableCallback v1.2
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας Γιώργου).

package com.gel.cleaner;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

public class GELFoldableDetector implements SensorEventListener {

    private final Context ctx;
    private final GELFoldableCallback callback;
    private final SensorManager sm;
    private final Sensor hingeSensor;

    private float lastAngle = -1f;
    private boolean lastInner = false;
    private GELFoldableCallback.Posture lastPosture =
            GELFoldableCallback.Posture.UNKNOWN;

    public GELFoldableDetector(@NonNull Context context,
                               @NonNull GELFoldableCallback cb) {

        this.ctx = context;
        this.callback = cb;
        this.sm = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

        // Android 12L+ hinge angle sensor (if exists)
        hingeSensor = sm.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE);
    }

    // ============================================================
    // START / STOP
    // ============================================================
    public void start() {
        if (hingeSensor != null) {
            sm.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (hingeSensor != null) {
            sm.unregisterListener(this);
        }
    }

    // ============================================================
    // SENSOR UPDATE
    // ============================================================
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor == null) return;
        if (event.sensor.getType() != Sensor.TYPE_HINGE_ANGLE) return;

        float angle = event.values[0];

        // avoid unnecessary noise
        if (angle == lastAngle) return;
        lastAngle = angle;

        // ------ Unified posture via GEL v1.2 mapper ------
        GELFoldableCallback.Posture posture =
                GELFoldableCallback.postureFrom(angle >= 150f, angle);

        // ------ Screen mode logic ------
        boolean isInner = (posture == GELFoldableCallback.Posture.FLAT);

        // ------ FIRE CALLBACKS ONLY IF CHANGED ------
        if (posture != lastPosture) {
            lastPosture = posture;
            callback.onPostureChanged(posture);
        }

        if (isInner != lastInner) {
            lastInner = isInner;
            callback.onScreenChanged(isInner);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not used
    }
}

// Παππού Γιώργο δώσε μου το επόμενο αρχείο να το κάνω Foldable Ready (Fully Integrated).
