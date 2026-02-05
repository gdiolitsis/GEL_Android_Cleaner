package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.media.AudioAttributes;

import java.util.Locale;

/*
 ============================================================
 AppTTS — GLOBAL TEXT TO SPEECH MANAGER
 FINAL • CLEAN • NO BRANDING • NO MAGIC
 ------------------------------------------------------------
 • One global TextToSpeech instance
 • Persistent global mute
 • NO audio routing here (routing is LAB responsibility)
 • NO project names in utteranceId
 • SAFE for MainActivity + LABs
 ============================================================
*/
public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";

    // single instance (legacy-safe pattern)
    private static TextToSpeech[] tts = new TextToSpeech[1];
    private static boolean[] ready = new boolean[]{ false };

    private static boolean muted = false;
    private static String pendingText = null;

    private AppTTS() {}

    // ============================================================
    // INIT — SAFE TO CALL ANYTIME
    // ============================================================
    public static void init(Context ctx) {

        if (tts[0] != null) return;

        Context appCtx = ctx.getApplicationContext();

        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        muted = prefs.getBoolean(PREF_TTS_MUTED, false);

        tts[0] = new TextToSpeech(appCtx, status -> {

            if (status != TextToSpeech.SUCCESS) return;

            // language (default EN)
            int res = tts[0].setLanguage(Locale.US);
            if (res == TextToSpeech.LANG_MISSING_DATA ||
                res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts[0].setLanguage(Locale.ENGLISH);
            }

            // 🔑 IMPORTANT:
            // AppTTS does NOT decide speaker / earpiece.
            // AudioAttributes are neutral.
            tts[0].setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
            );

            ready[0] = true;

            // speak pending text if any
            if (!muted && pendingText != null) {
                try {
                    tts[0].speak(
                            pendingText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                    );
                } catch (Throwable ignore) {}
            }

            pendingText = null;
        });
    }

    // ============================================================
    // SPEAK — GENERIC (NO ROUTING)
    // ============================================================
    public static void speak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return;

        if (!ready[0] || tts[0] == null) {
            pendingText = text;
            return;
        }

        try {
            tts[0].speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // STOP (IMMEDIATE)
    // ============================================================
    public static void stop() {
        if (tts[0] != null) {
            try {
                tts[0].stop();
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

        if (m && tts[0] != null) {
            try { tts[0].stop(); } catch (Throwable ignore) {}
        }
    }

    public static boolean isMuted(Context ctx) {
        init(ctx);
        return muted;
    }

    // ============================================================
    // FULL RELEASE (ON DESTROY)
    // ============================================================
    public static void shutdown() {

        if (tts[0] != null) {
            try {
                tts[0].stop();
                tts[0].shutdown();
            } catch (Throwable ignore) {}
        }

        tts[0] = null;
        ready[0] = false;
        pendingText = null;
    }
}
