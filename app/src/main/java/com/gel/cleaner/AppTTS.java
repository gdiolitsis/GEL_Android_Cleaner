// ============================================================
// AppTTS.java — FINAL (GEL) — LOCKED
// • App language aware (via AppLang)
// • Realtime Greek TTS detect with EN fallback
// • Persistent mute
// • SAFE global entry point (ensureSpeak)
// • Zero UI dependency
// ============================================================

package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.os.Handler;
import android.os.Looper;

import java.util.Locale;

public final class AppTTS {

    private static final String PREF_NAME = "GEL_TTS_PREF";
    private static final String KEY_MUTE  = "tts_muted";

    private static TextToSpeech tts;
    private static boolean ready = false;
    private static boolean muted = false;
    private static boolean prefsLoaded = false;

    // 🔑 realtime capability flag
    private static boolean greekEverWorked = false;

    private AppTTS() {}

    // ============================================================
    // INIT (SAFE / IDLE)
    // ============================================================
    private static synchronized void init(Context ctx) {

        if (!prefsLoaded) {
            SharedPreferences p =
                    ctx.getApplicationContext()
                       .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            muted = p.getBoolean(KEY_MUTE, false);
            prefsLoaded = true;
        }

        if (tts != null) return;

        tts = new TextToSpeech(
                ctx.getApplicationContext(),
                status -> ready = (status == TextToSpeech.SUCCESS)
        );
    }

    // ============================================================
    // 🔒 GLOBAL ENTRY POINT — USE ONLY THIS
    // ============================================================
    public static void ensureSpeak(Context ctx, String text) {

        if (ctx == null || text == null || text.trim().isEmpty())
            return;

        if (muted)
            return;

        init(ctx);

        if (!ready || tts == null)
            return;

        // force to main thread (TTS is sensitive)
        new Handler(Looper.getMainLooper()).post(() -> speakInternal(ctx, text));
    }

    // ============================================================
    // INTERNAL SPEAK (DO NOT CALL DIRECTLY)
    // ============================================================
    private static void speakInternal(Context ctx, String text) {

        try {
            boolean wantGreek = AppLang.isGreek(ctx);
            boolean greekOk = false;

            if (wantGreek) {
                int res = tts.setLanguage(new Locale("el", "GR"));
                greekOk =
                        res != TextToSpeech.LANG_MISSING_DATA &&
                        res != TextToSpeech.LANG_NOT_SUPPORTED;

                if (greekOk) {
                    greekEverWorked = true;
                }
            }

            if (!greekOk) {
                tts.setLanguage(Locale.US);
            }

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "GEL_TTS_" + System.currentTimeMillis()
            );

        } catch (Throwable ignore) {}
    }

    // ============================================================
    // MUTE
    // ============================================================
    public static void setMuted(Context ctx, boolean value) {

        muted = value;

        SharedPreferences p =
                ctx.getApplicationContext()
                   .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        p.edit().putBoolean(KEY_MUTE, value).apply();

        if (muted && tts != null) tts.stop();
    }

    public static boolean isMuted() {
        return muted;
    }

    // ============================================================
    // CONTROL
    // ============================================================
    public static void stop() {
        if (tts != null) tts.stop();
    }

    public static void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        tts = null;
        ready = false;
    }
}
