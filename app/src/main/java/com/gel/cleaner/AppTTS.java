package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/* ============================================================
   AppTTS — GLOBAL TTS MANAGER (FINAL • LOCKED)
   • SUPPORTS SPEAKER + EARPIECE
   • RESPECTS AUDIO ROUTING FROM LABS
   • NO AUTO MODE CHANGES
   ============================================================ */

public final class AppTTS {

    private static final String PREFS = "gel_prefs";
    private static final String PREF_MUTED = "tts_muted_global";

    private static TextToSpeech tts;
    private static boolean ready = false;
    private static boolean muted = false;

    private AppTTS() {}

    // =========================================================
    // INIT
    // =========================================================
    public static synchronized void init(Context ctx) {

        if (tts != null) return;

        Context app = ctx.getApplicationContext();

        SharedPreferences sp =
                app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        muted = sp.getBoolean(PREF_MUTED, false);

        tts = new TextToSpeech(app, status -> {
            if (status == TextToSpeech.SUCCESS) {
                try {
                    tts.setLanguage(Locale.US);
                } catch (Throwable ignore) {}
                ready = true;
            }
        });
    }

    // =========================================================
    // SPEAK — USE CURRENT AUDIO ROUTE (NO OVERRIDES)
    // =========================================================
    public static void ensureSpeak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);
        if (muted || !ready || tts == null) return;

        try {
            tts.stop();

            tts.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
            );

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "GEL_TTS"
            );

        } catch (Throwable ignore) {}
    }

    // =========================================================
    // SPEAKER FORCE (STAGE 1)
    // =========================================================
    public static void ensureSpeakSpeaker(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);
        if (muted || !ready || tts == null) return;

        try {
            AudioManager am =
                    (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

            if (am != null) {
                try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
                try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
            }

            tts.stop();

            tts.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
            );

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "GEL_TTS_SPEAKER"
            );

        } catch (Throwable ignore) {}
    }

    // =========================================================
    // STOP
    // =========================================================
    public static void stop() {
        try {
            if (tts != null) tts.stop();
        } catch (Throwable ignore) {}
    }

    // =========================================================
    // MUTE (GLOBAL)
    // =========================================================
    public static void setMuted(Context ctx, boolean m) {

        muted = m;

        Context app = ctx.getApplicationContext();
        app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_MUTED, m)
                .apply();

        if (m) stop();
    }

    public static boolean isMuted(Context ctx) {
        init(ctx);
        return muted;
    }

    // =========================================================
    // FULL RELEASE (ON DESTROY)
    // =========================================================
    public static void shutdown() {
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Throwable ignore) {}
        tts = null;
        ready = false;
    }
}
