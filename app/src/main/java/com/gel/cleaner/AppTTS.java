package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

// ============================================================
// AppTTS — GLOBAL TTS MANAGER (LEGACY SAFE)
// • Dual route: SPEAKER (media) / EARPIECE (voice comm)
// • Global persistent mute
// ============================================================
public final class AppTTS {

    private static final String PREFS_NAME = "gel_prefs";
    private static final String PREF_TTS_MUTED = "tts_muted_global";

    private static final TextToSpeech[] tts = new TextToSpeech[1];
    private static final boolean[] ttsReady = { false };

    private static boolean muted = false;
    private static String pendingSpeakText = null;

    // 🔒 attrs
    private static final AudioAttributes ATTR_EARPIECE =
            new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

    private static final AudioAttributes ATTR_SPEAKER =
            new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

    private AppTTS() {}

    // ============================================================
    // INIT — CALL SAFELY ANYTIME
    // ============================================================
    public static void init(Context ctx) {

        if (tts[0] != null) return;

        Context appCtx = ctx.getApplicationContext();

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

                // default route = EARPIECE (safe for labs)
                try { tts[0].setAudioAttributes(ATTR_EARPIECE); } catch (Throwable ignore) {}

                ttsReady[0] = true;

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
    // INTERNAL SPEAK
    // ============================================================
    private static void speakInternal(Context ctx, String text, String utteranceId) {

        if (text == null || text.trim().isEmpty()) return;

        init(ctx);

        if (muted) return;

        if (!ttsReady[0] || tts[0] == null) {
            pendingSpeakText = text;
            return;
        }

        try {
            tts[0].speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    utteranceId
            );
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // SPEAK — DEFAULT (EARPIECE)
    // ============================================================
    public static void ensureSpeak(Context ctx, String text) {
        // default: earpiece route
        try {
            init(ctx);
            if (tts[0] != null) tts[0].setAudioAttributes(ATTR_EARPIECE);
        } catch (Throwable ignore) {}
        speakInternal(ctx, text, "GEL_TTS");
    }

    // ============================================================
    // SPEAK ON SPEAKER (OPEN LISTENING)
    // ============================================================
    public static void ensureSpeakSpeaker(Context ctx, String text) {
        init(ctx);
        if (muted) return;

        try {
            if (tts[0] != null) tts[0].setAudioAttributes(ATTR_SPEAKER);
        } catch (Throwable ignore) {}

        speakInternal(ctx, text, "GEL_TTS_SPK");

        // restore to earpiece shortly after (so labs stay earpiece-safe)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                if (tts[0] != null) tts[0].setAudioAttributes(ATTR_EARPIECE);
            } catch (Throwable ignore) {}
        }, 250);
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
            try { tts[0].stop(); } catch (Throwable ignore) {}
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
        if (tts[0] != null) {
            try { tts[0].stop(); } catch (Throwable ignore) {}
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
