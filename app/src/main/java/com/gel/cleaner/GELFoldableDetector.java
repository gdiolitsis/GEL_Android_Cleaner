// GDiolitsis Engine Lab (GEL) — Author & Developer
// STEP 2 — Auto Foldable Detector (Hinge Angle + Posture Mapper)
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

        // Android 12L hinge angle sensor
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

        // Avoid noise
        if (angle == lastAngle) return;
        lastAngle = angle;

        // Map angle → posture
        GELFoldableCallback.Posture posture = mapAngleToPosture(angle);

        // Detect screen mode
        boolean isInner = angle > 150; // unfolded → inner screen active

        // FIRE CALLBACKS ONLY IF CHANGED
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
        // Not used
    }

    // ============================================================
    // POSTURE LOGIC
    // ============================================================
    private GELFoldableCallback.Posture mapAngleToPosture(float angle) {

        // 0° → Closed
        if (angle <= 10f) {
            return GELFoldableCallback.Posture.CLOSED;
        }

        // 10°–45° → Tent (Samsung uses this range)
        if (angle > 10f && angle <= 45f) {
            return GELFoldableCallback.Posture.TENT_MODE;
        }

        // 45°–110° → Half-open
        if (angle > 45f && angle <= 110f) {
            return GELFoldableCallback.Posture.HALF_OPEN;
        }

        // 110°–150° → Table mode
        if (angle > 110f && angle <= 150f) {
            return GELFoldableCallback.Posture.TABLE_MODE;
        }

        // 150°–180° → Flat (unfolded)
        if (angle > 150f) {
            return GELFoldableCallback.Posture.FLAT;
        }

        return GELFoldableCallback.Posture.UNKNOWN;
    }
}
