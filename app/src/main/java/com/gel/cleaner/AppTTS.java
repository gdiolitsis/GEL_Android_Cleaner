package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

// ============================================================
// AppTTS — GLOBAL TTS MANAGER (LEGACY SAFE)
// • SAME STYLE AS ManualTestsActivity
// • Uses TextToSpeech[1] + boolean[1]
// • Global persistent mute
// ============================================================
public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";

    // 🔒 SAME PATTERN AS LEGACY
    private static TextToSpeech[] tts = new TextToSpeech[1];
    private static boolean[] ttsReady = { false };

    private static boolean muted = false;
    private static String pendingSpeakText = null;

    private AppTTS() {}

    // ============================================================
    // INIT — CALL SAFELY ANYTIME
    // ============================================================
    public static void init(Context ctx) {

        if (tts[0] != null) return;

        Context appCtx = ctx.getApplicationContext();

        // load mute state ONCE
        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        muted = prefs.getBoolean(PREF_TTS_MUTED, false);

        tts[0] = new TextToSpeech(appCtx, status -> {
            if (status == TextToSpeech.SUCCESS) {

                int res = tts[0].setLanguage(Locale.US);
                if (res == TextToSpeech.LANG_MISSING_DATA ||
                    res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts[0].setLanguage(Locale.ENGLISH);
                }

                ttsReady[0] = true;

                // 🔑 speak pending ONLY if not muted
                if (!muted && pendingSpeakText != null) {
                    try {
                        tts[0].speak(
                                pendingSpeakText,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "GEL_TTS_PENDING"
                        );
                    } catch (Throwable ignore) {}
                }

                pendingSpeakText = null;
            }
        });
    }

    // ============================================================
    // SPEAK — RESPECTS GLOBAL MUTE
    // ============================================================
    public static void ensureSpeak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return; // 🔇 absolute silence

        if (!ttsReady[0] || tts[0] == null) {
            pendingSpeakText = text;
            return;
        }

        try {
            tts[0].speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "GEL_TTS"
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // MUTE — GLOBAL + PERSISTENT
    // ============================================================
    public static void setMuted(Context ctx, boolean m) {

        muted = m;

        Context appCtx = ctx.getApplicationContext();
        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().putBoolean(PREF_TTS_MUTED, m).apply();

        if (m && tts[0] != null) {
            try {
                tts[0].stop();
            } catch (Throwable ignore) {}
        }
    }

    public static boolean isMuted(Context ctx) {
        init(ctx);
        return muted;
    }

    // ============================================================
    // STOP (NO SHUTDOWN)
    // ============================================================
    public static void stop() {
        if (tts[0] != null) {
            try {
                tts[0].stop();
            } catch (Throwable ignore) {}
        }
    }

    // ============================================================
    // FULL RELEASE (OPTIONAL)
    // ============================================================
    public static void shutdown() {
        if (tts[0] != null) {
            try {
                tts[0].stop();
                tts[0].shutdown();
            } catch (Throwable ignore) {}
            tts[0] = null;
            ttsReady[0] = false;
            pendingSpeakText = null;
        }
    }
}
