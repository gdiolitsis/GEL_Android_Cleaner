// ============================================================
// AppTTS.java — FINAL (GEL)
// Unified Text-To-Speech helper for popups & labs
// • App language aware (GR / EN)
// • Global mute respected
// • Safe init / stop / reuse
// ============================================================

package com.gel.cleaner;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public final class AppTTS {

    private static TextToSpeech tts;
    private static boolean ready = false;
    private static boolean muted = false;

    private AppTTS() {
        // no instances
    }

    // ============================================================
    // INIT (call once, lazy-safe)
    // ============================================================
    private static void init(Context ctx) {
        if (tts != null) return;

        try {
            tts = new TextToSpeech(ctx.getApplicationContext(), status -> {
                ready = (status == TextToSpeech.SUCCESS);
            });
        } catch (Throwable t) {
            ready = false;
            tts = null;
        }
    }

    // ============================================================
    // SPEAK (MAIN ENTRY)
    // ============================================================
    public static void speak(Context ctx, String text, boolean greek) {

        if (muted) return;
        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (!ready || tts == null) return;

        try {
            tts.stop();

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

        } catch (Throwable ignore) { }
    }

    // ============================================================
    // STOP (ON DISMISS / MUTE)
    // ============================================================
    public static void stop() {
        try {
            if (tts != null) tts.stop();
        } catch (Throwable ignore) { }
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

    public static void setMuted(boolean m) {
        muted = m;
        if (muted) stop();
    }

    public static boolean isMuted() {
        return muted;
    }

    // ============================================================
    // RELEASE (OPTIONAL — on app destroy)
    // ============================================================
    public static void shutdown() {
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Throwable ignore) { }
        tts = null;
        ready = false;
    }
}
