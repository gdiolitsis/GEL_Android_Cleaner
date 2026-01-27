package com.gel.cleaner;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ============================================================
 * AppTTS — GLOBAL Text-to-Speech (GEL)
 * • One engine for the whole app
 * • Uses APP language (not system)
 * • Safe init / speak / shutdown
 * ============================================================
 */
public final class AppTTS {

    private static TextToSpeech tts;
    private static final AtomicBoolean ready = new AtomicBoolean(false);

    private AppTTS() {} // no instances

    // ============================================================
    // INIT — call once (Application or MainActivity)
    // ============================================================
    public static void init(Context ctx) {
        if (tts != null) return;

        Context appCtx = ctx.getApplicationContext();

        tts = new TextToSpeech(appCtx, status -> {
            if (status == TextToSpeech.SUCCESS) {

                boolean gr = isGreek(appCtx);

                Locale locale = gr
                        ? new Locale("el", "GR")
                        : Locale.US;

                int res = tts.setLanguage(locale);
                ready.set(res != TextToSpeech.LANG_MISSING_DATA
                        && res != TextToSpeech.LANG_NOT_SUPPORTED);
            }
        });
    }

    // ============================================================
    // SPEAK — safe, silent if not ready
    // ============================================================
    public static void speak(Context ctx, String text) {
        if (text == null || text.trim().isEmpty()) return;

        if (tts == null) {
            init(ctx);
            return;
        }

        if (!ready.get()) return;

        try {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "GEL_TTS");
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // STOP (optional)
    // ============================================================
    public static void stop() {
        try {
            if (tts != null) tts.stop();
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // SHUTDOWN — call only on app exit (Application.onTerminate)
    // ============================================================
    public static void shutdown() {
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
                tts = null;
                ready.set(false);
            }
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // APP LANGUAGE (GEL) — NOT system language
    // ============================================================
    private static boolean isGreek(Context ctx) {
        return "gr".equalsIgnoreCase(
                ctx.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE)
                        .getString("app_lang", "en")
        );
    }
}
