// ============================================================
// AppTTS.java — FINAL (GEL)
// Unified Text-To-Speech helper (GLOBAL)
// • App language aware (GR / EN via AppLang)
// • Persistent mute (SharedPreferences)
// • Safe init / stop / reuse
// • No system language dependency
// ============================================================

package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public final class AppTTS {

    private static final String PREF_NAME = "GEL_TTS_PREF";
    private static final String KEY_MUTE  = "tts_muted";

    private static TextToSpeech tts;
    private static boolean ready = false;
    private static boolean muted = false;
    private static boolean prefsLoaded = false;

    private AppTTS() {
        // no instances
    }

    // ============================================================
    // INIT (lazy / safe)
    // ============================================================
    private static void init(Context ctx) {

        if (!prefsLoaded) {
            try {
                SharedPreferences p =
                        ctx.getApplicationContext()
                           .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                muted = p.getBoolean(KEY_MUTE, false);
            } catch (Throwable ignore) {}
            prefsLoaded = true;
        }

        if (tts != null) return;

        try {
            tts = new TextToSpeech(
                    ctx.getApplicationContext(),
                    status -> ready = (status == TextToSpeech.SUCCESS)
            );
        } catch (Throwable t) {
            tts = null;
            ready = false;
        }
    }

    // ============================================================
    // SPEAK (MAIN ENTRY)
    // ============================================================
    public static void speak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;
        if (muted) return;

        init(ctx);

        if (!ready || tts == null) return;

        try {
            tts.stop();

            boolean greek = AppLang.isGreek(ctx);

            if (greek) {
                tts.setLanguage(new Locale("el", "GR"));
            } else {
                tts.setLanguage(Locale.US);
            }

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "GEL_TTS"
            );

        } catch (Throwable ignore) {}
    }

    // ============================================================
    // STOP (on dismiss / mute)
    // ============================================================
    public static void stop() {
        try {
            if (tts != null) tts.stop();
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // MUTE CONTROL (PERSISTENT)
    // ============================================================
    public static void setMuted(Context ctx, boolean value) {
        muted = value;

        try {
            SharedPreferences p =
                    ctx.getApplicationContext()
                       .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            p.edit().putBoolean(KEY_MUTE, value).apply();
        } catch (Throwable ignore) {}

        if (muted) stop();
    }

    public static void toggleMute(Context ctx) {
        setMuted(ctx, !muted);
    }

    public static boolean isMuted() {
        return muted;
    }

    // ============================================================
    // STATE HELPERS
    // ============================================================
    public static boolean isSpeaking() {
        try {
            return tts != null && tts.isSpeaking();
        } catch (Throwable t) {
            return false;
        }
    }

    // ============================================================
    // SHUTDOWN (optional — app destroy)
    // ============================================================
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
