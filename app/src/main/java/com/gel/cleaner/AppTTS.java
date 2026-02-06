package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/*
 ============================================================
 AppTTS — GLOBAL TEXT TO SPEECH MANAGER
 FINAL • SAFE • LEGACY AUDIO PATH
 ------------------------------------------------------------
 • One global TextToSpeech instance
 • Persistent global mute
 • NO AudioAttributes
 • NO audio routing
 • NO media usage
 • SAFE for mic capture (LABs 1–29)
 ============================================================
*/
public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";

    private static TextToSpeech tts = null;
    private static boolean ready = false;

    private static boolean muted = false;
    private static String pendingText = null;

    private AppTTS() {}

    // ============================================================
    // INIT — SAFE TO CALL ANYTIME
    // ============================================================
    public static synchronized void init(Context ctx) {

        if (tts != null) return;

        Context appCtx = ctx.getApplicationContext();

        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        muted = prefs.getBoolean(PREF_TTS_MUTED, false);

        tts = new TextToSpeech(appCtx, status -> {

            if (status != TextToSpeech.SUCCESS) return;

            int res = tts.setLanguage(Locale.US);
            if (res == TextToSpeech.LANG_MISSING_DATA ||
                res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.ENGLISH);
            }

            // ❗ NO AudioAttributes here — legacy safe path
            ready = true;

            if (!muted && pendingText != null) {
                try {
                    tts.speak(
                            pendingText,
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                    );
                } catch (Throwable ignore) {}
            }

            pendingText = null;
        });
    }

    // ============================================================
    // SPEAK — GENERIC (SAFE)
    // ============================================================
    public static void speak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return;

        if (!ready || tts == null) {
            pendingText = text;
            return;
        }

        try {
            tts.speak(
                    text,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // LEGACY COMPAT — REQUIRED BY LABs
    // ============================================================
    public static void ensureSpeak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return;

        if (!ready || tts == null) {
            pendingText = text;
            return;
        }

        try {
            tts.speak(
                    text,
                    TextToSpeech.QUEUE_ADD,
                    null,
                    "APP_TTS"
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // STOP — IMMEDIATE
    // ============================================================
    public static void stop() {
        if (tts != null) {
            try {
                tts.stop();
            } catch (Throwable ignore) {}
        }
    }

    // ============================================================
    // GLOBAL MUTE (PERSISTENT)
    // ============================================================
    public static void setMuted(Context ctx, boolean m) {

        muted = m;

        SharedPreferences prefs =
                ctx.getApplicationContext()
                        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().putBoolean(PREF_TTS_MUTED, m).apply();

        if (m && tts != null) {
            try { tts.stop(); } catch (Throwable ignore) {}
        }
    }

    public static boolean isMuted(Context ctx) {
        init(ctx);
        return muted;
    }

    // ============================================================
    // FULL RELEASE (ON DESTROY)
    // ============================================================
    public static synchronized void shutdown() {

        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Throwable ignore) {}
        }

        tts = null;
        ready = false;
        pendingText = null;
    }
}
