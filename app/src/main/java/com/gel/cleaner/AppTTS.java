package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 ============================================================
 AppTTS — GLOBAL Text-to-Speech (FINAL STABLE)
 ------------------------------------------------------------
 • One single global TTS instance
 • Shared mute state across whole app
 • Respects App language (app_lang)
 • Safe init (idempotent)
 • No routing tricks
 • No side effects
 ============================================================
*/
public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";
    private static final String PREF_APP_LANG = "app_lang";

    private static TextToSpeech tts;
    private static final AtomicBoolean ready = new AtomicBoolean(false);
    private static boolean muted = false;

    private AppTTS() {}

    // ============================================================
    // INIT (SAFE / IDEMPOTENT)
    // ============================================================
    public static void init(Context ctx) {

        if (tts != null) return;

        Context appCtx = ctx.getApplicationContext();

        SharedPreferences prefs =
                appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        muted = prefs.getBoolean(PREF_TTS_MUTED, false);

        tts = new TextToSpeech(appCtx, status -> {

            if (status != TextToSpeech.SUCCESS) {
                ready.set(false);
                return;
            }

            applyLanguage(appCtx);
            ready.set(true);
        });
    }

    // ============================================================
    // APPLY LANGUAGE FROM APP SETTINGS
    // ============================================================
    private static void applyLanguage(Context ctx) {

        if (tts == null) return;

        SharedPreferences prefs =
                ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String code = prefs.getString(PREF_APP_LANG, "en");

        Locale locale =
                "el".equalsIgnoreCase(code)
                        ? new Locale("el", "GR")
                        : Locale.US;

        try {
            tts.setLanguage(locale);
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // SPEAK
    // ============================================================
    public static void speak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return;
        if (!ready.get() || tts == null) return;

        try {
            tts.stop();
            applyLanguage(ctx);

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "APP_TTS"
            );

        } catch (Throwable ignore) {}
    }

    // ============================================================
    // LEGACY SAFE ALIAS (LAB COMPATIBILITY)
    // ============================================================
    public static void ensureSpeak(Context ctx, String text) {
        speak(ctx, text);
    }

    // ============================================================
    // MUTE CONTROL (GLOBAL)
    // ============================================================
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
    // STOP
    // ============================================================
    public static void stop() {
        if (tts != null) {
            try { tts.stop(); } catch (Throwable ignore) {}
        }
    }

    // ============================================================
    // SHUTDOWN (OPTIONAL)
    // ============================================================
    public static void shutdown() {

        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Throwable ignore) {}
            tts = null;
        }

        ready.set(false);
    }

    // ============================================================
    // STATE CHECK
    // ============================================================
    public static boolean isSpeaking() {
        try {
            return tts != null && tts.isSpeaking();
        } catch (Throwable ignore) {
            return false;
        }
    }
}
