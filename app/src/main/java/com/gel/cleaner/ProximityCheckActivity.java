package com.gel.cleaner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * ============================================================
 * LAB 8 — Proximity Sensor Check (LOCKED)
 * ============================================================
 */
public class ProximityCheckActivity extends Activity
        implements SensorEventListener {

    // ==========================
    // TEXT TO SPEECH
    // ==========================
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean ttsMuted = false;

    private SensorManager sensorManager;
    private Sensor proximity;

    private boolean initialRead = false;
    private float initialValue = 0f;
    private boolean loggedFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences prefs =
                getSharedPreferences("GEL_DIAG", MODE_PRIVATE);
        ttsMuted = prefs.getBoolean("lab8_tts_muted", false);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(0xFF101010);

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
        root.addView(info, infoLp);

        // ==========================
        // 🔇 MUTE TOGGLE
        // ==========================
        CheckBox muteBox = new CheckBox(this);
        muteBox.setText("Mute voice");
        muteBox.setTextColor(Color.WHITE);
        muteBox.setChecked(ttsMuted);

        FrameLayout.LayoutParams muteLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        muteLp.gravity = Gravity.TOP | Gravity.END;
        muteLp.topMargin = dp(12);
        muteLp.rightMargin = dp(12);
        muteBox.setLayoutParams(muteLp);

        muteBox.setOnCheckedChangeListener((b, checked) -> {
            ttsMuted = checked;
            prefs.edit()
                    .putBoolean("lab8_tts_muted", checked)
                    .apply();
            if (checked && tts != null) {
                tts.stop();
            }
        });

        root.addView(muteBox);

        Button end = new Button(this);
        end.setText("END TEST");
        end.setAllCaps(false);
        end.setTextColor(Color.WHITE);
        end.setTextSize(15f);
        end.setTypeface(null, Typeface.BOLD);

        GradientDrawable redBtn = new GradientDrawable();
        redBtn.setColor(0xFF8B0000);
        redBtn.setCornerRadius(dp(14));
        redBtn.setStroke(dp(3), 0xFFFFD700);
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

            if (!loggedFinish) {
                loggedFinish = true;

                GELServiceLog.section("LAB 8 — Proximity Sensor");
                GELServiceLog.warn("Proximity test was cancelled by user.");
                GELServiceLog.warn("No proximity state change was detected during the test.");
                GELServiceLog.ok("Manual re-test recommended.");
                GELServiceLog.ok("Lab 8 finished.");
                GELServiceLog.addLine(null);
            }

            setResult(RESULT_CANCELED);
            finish();
        });

        root.addView(end);
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

                if (ttsReady && !ttsMuted) {
                    tts.speak(
                            "Place your hand over the front sensor near the earpiece.",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "LAB8"
                    );
                }
            }
        });
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
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float v = event.values[0];

        if (!initialRead) {
            initialValue = v;
            initialRead = true;
            return;
        }

        if (Math.abs(v - initialValue) > 0.5f) {

            if (!loggedFinish) {
                loggedFinish = true;

                GELServiceLog.section("LAB 8 — Proximity Sensor");
                GELServiceLog.ok("Proximity sensor state change detected.");
                GELServiceLog.ok("Near/Far response confirmed.");
                GELServiceLog.ok("Front proximity sensing path responding normally.");
                GELServiceLog.ok("Lab 8 finished.");
                GELServiceLog.addLine(null);
            }

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
