package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * ============================================================
 * GEL TTS HELPER — GLOBAL / SAFE / MUTABLE
 * ------------------------------------------------------------
 * ✔ One instance per Activity
 * ✔ Persistent mute (SharedPreferences)
 * ✔ No leaks
 * ✔ No double speak
 * ✔ No routing side-effects
 *
 * Author: GDiolitsis Engine Lab (GEL)
 * ============================================================
 */
public class GELTtsHelper {

    private static final String PREF_NAME = "GEL_TTS_PREF";
    private static final String KEY_MUTE  = "tts_muted";

    private final Context ctx;
    private final SharedPreferences prefs;

    private TextToSpeech tts;
    private boolean ready = false;
    private boolean muted = false;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public GELTtsHelper(Context context) {
        this.ctx = context.getApplicationContext();
        this.prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.muted = prefs.getBoolean(KEY_MUTE, false);

        tts = new TextToSpeech(ctx, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int res = tts.setLanguage(Locale.US);
                ready =
                        res != TextToSpeech.LANG_MISSING_DATA &&
                        res != TextToSpeech.LANG_NOT_SUPPORTED;
            }
        });
    }

    // ============================================================
    // SPEAK (SAFE)
    // ============================================================
    public void speakOnce(String text, String utteranceId) {
        if (!ready || muted || tts == null) return;

        try {
            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    utteranceId
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // MUTE CONTROL
    // ============================================================
    public void setMuted(boolean value) {
        muted = value;
        prefs.edit().putBoolean(KEY_MUTE, value).apply();

        if (muted && tts != null) {
            try { tts.stop(); } catch (Throwable ignore) {}
        }
    }

    public void toggleMute() {
        setMuted(!muted);
    }

    public boolean isMuted() {
        return muted;
    }

    // ============================================================
    // CLEANUP (MANDATORY)
    // ============================================================
    public void release() {
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Throwable ignore) {}
        tts = null;
    }
}
