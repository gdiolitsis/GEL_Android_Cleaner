package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 ============================================================
 AppTTS — GLOBAL Text-to-Speech (LEGACY STABLE + ADAPTER)
 ------------------------------------------------------------
 • Keeps OLD stable behavior (LAB 1–2 safe)
 • Adds legacy API expected by newer LABs
 • NO audio routing
 • NO DSP tricks
 • NO side effects
 ============================================================
*/
public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";

    private static TextToSpeech tts;
    private static final AtomicBoolean ready = new AtomicBoolean(false);

    private static boolean muted = false;

    private AppTTS() {}

    // ============================================================
    // INIT — SAFE, IDPOTENT
    // ============================================================
    public static void init(Context ctx) {
        if (tts != null) return;

        Context appCtx = ctx.getApplicationContext();

        // load mute state (legacy compatibility)
        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        muted = prefs.getBoolean(PREF_TTS_MUTED, false);

        tts = new TextToSpeech(appCtx, status -> {
            if (status != TextToSpeech.SUCCESS) return;

            boolean gr = isGreek(appCtx);

            Locale locale = gr
                    ? new Locale("el", "GR")
                    : Locale.US;

            int res = tts.setLanguage(locale);
            ready.set(
                    res != TextToSpeech.LANG_MISSING_DATA &&
                    res != TextToSpeech.LANG_NOT_SUPPORTED
            );
        });
    }

    // ============================================================
    // CORE SPEAK (OLD BEHAVIOR)
    // ============================================================
    public static void speak(Context ctx, String text) {
        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return;
        if (!ready.get() || tts == null) return;

        try {
            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "APP_TTS"
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // LEGACY API — REQUIRED BY LABs
    // ============================================================

    /** alias for speak() — DO NOT CHANGE LAB CALLS */
    public static void ensureSpeak(Context ctx, String text) {
        speak(ctx, text);
    }

    public static boolean isMuted(Context ctx) {
        init(ctx);
        return muted;
    }

    public static void setMuted(Context ctx, boolean m) {
        muted = m;

        Context appCtx = ctx.getApplicationContext();
        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        prefs.edit().putBoolean(PREF_TTS_MUTED, m).apply();

        if (m && tts != null) {
            try { tts.stop(); } catch (Throwable ignore) {}
        }
    }

    // ============================================================
    // STOP / SHUTDOWN
    // ============================================================
    public static void stop() {
        if (tts != null) {
            try { tts.stop(); } catch (Throwable ignore) {}
        }
    }

    public static void shutdown() {
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Throwable ignore) {}
            tts = null;
            ready.set(false);
        }
    }

    // ============================================================
    // APP LANGUAGE (NOT SYSTEM)
    // ============================================================
    private static boolean isGreek(Context ctx) {
        return "gr".equalsIgnoreCase(
                ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .getString("app_lang", "en")
        );
    }
}
