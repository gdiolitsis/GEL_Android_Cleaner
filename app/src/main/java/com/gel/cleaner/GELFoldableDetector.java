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
    private boolean isInnerScreen = false;

    public GELFoldableDetector(@NonNull Context context,
                               @NonNull GELFoldableCallback cb) {

        this.ctx = context;
        this.callback = cb;

        sm = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

        // Android 12L hinge angle sensor
        hingeSensor = sm.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE);
    }

    /**
     * Call inside onResume()
     */
    public void start() {
        if (hingeSensor != null) {
            sm.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Call inside onPause()
     */
    public void stop() {
        if (hingeSensor != null) {
            sm.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor == null) return;
        if (event.sensor.getType() != Sensor.TYPE_HINGE_ANGLE) return;

        float angle = event.values[0];
        if (angle == lastAngle) return;
        lastAngle = angle;

        GELFoldableCallback.Posture posture = mapAngleToPosture(angle);

        // Determine if inner (big) screen is active
        boolean newIsInner = angle > 150;  // 150–180° → unfolded

        if (newIsInner != isInnerScreen) {
            isInnerScreen = newIsInner;
            callback.onScreenChanged(isInnerScreen);
        }

        callback.onPostureChanged(posture);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not required
    }

    /**
     * Map hinge angle to foldable posture.
     */
    private GELFoldableCallback.Posture mapAngleToPosture(float angle) {
        if (angle < 10) {
            return GELFoldableCallback.Posture.CLOSED;     // approx. 0–10°
        }
        if (angle < 45) {
            return GELFoldableCallback.Posture.TENT_MODE;  // approx. 10–45°
        }
        if (angle < 110) {
            return GELFoldableCallback.Posture.HALF_OPEN;  // approx. 45–110°
        }
        if (angle < 150) {
            return GELFoldableCallback.Posture.TABLE_MODE; // approx. 110–150°
        }
        return GELFoldableCallback.Posture.FLAT;           // 150–180° fully open
    }

}
