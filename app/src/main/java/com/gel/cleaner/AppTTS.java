package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";

    private static TextToSpeech tts;
    private static boolean ttsReady = false;
    private static boolean muted = false;

    private static String pendingSpeakText = null;

    private AppTTS() {}

    // ============================================================
    // INIT — CALL ONCE (SAFE)
    // ============================================================
    public static void init(Context ctx) {

        if (tts != null) return;

        // 🔒 φόρτωμα mute ΜΙΑ ΦΟΡΑ
        SharedPreferences prefs =
                ctx.getApplicationContext()
                   .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        muted = prefs.getBoolean(PREF_TTS_MUTED, false);

        tts = new TextToSpeech(ctx.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {

                int res = tts.setLanguage(Locale.US);
                if (res == TextToSpeech.LANG_MISSING_DATA ||
                    res == TextToSpeech.LANG_NOT_SUPPORTED) {

                    tts.setLanguage(Locale.ENGLISH);
                }

                ttsReady = true;

                // 🔑 μιλάει ΜΟΝΟ αν ΔΕΝ είναι muted
                if (!muted && pendingSpeakText != null) {
                    tts.speak(
                            pendingSpeakText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "GEL_TTS_PENDING"
                    );
                }

                pendingSpeakText = null;
            }
        });
    }

    // ============================================================
    // SPEAK — GLOBAL MUTE RESPECTED
    // ============================================================
    public static void ensureSpeak(Context ctx, String text) {

        if (text == null || text.isEmpty()) return;

        init(ctx);

        if (muted) return; // 🔇 GLOBAL SILENCE

        if (!ttsReady) {
            pendingSpeakText = text;
            return;
        }

        tts.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "GEL_TTS"
        );
    }

    // ============================================================
    // MUTE CONTROL — GLOBAL & PERSISTENT
    // ============================================================
    public static void setMuted(Context ctx, boolean m) {

        muted = m;

        SharedPreferences prefs =
                ctx.getApplicationContext()
                   .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().putBoolean(PREF_TTS_MUTED, m).apply();

        if (muted && tts != null) {
            tts.stop(); // κόβει άμεσα τον ήχο
        }
    }

    public static boolean isMuted(Context ctx) {
        init(ctx);
        return muted;
    }

    // ============================================================
    // STOP
    // ============================================================
    public static void stop() {
        if (tts != null) {
            tts.stop();
        }
    }

    // ============================================================
    // RELEASE (OPTIONAL)
    // ============================================================
    public static void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
            ttsReady = false;
            pendingSpeakText = null;
        }
    }
}
