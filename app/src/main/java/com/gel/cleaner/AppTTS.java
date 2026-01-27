// ============================================================
// AppTTS.java — FINAL (GEL)
// • App language aware (via AppLang)
// • Greek TTS detect with EN fallback
// • Persistent mute
// • Zero UI dependency
// ============================================================

package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public final class AppTTS {

    private static final String PREF_NAME = "GEL_TTS_PREF";
    private static final String KEY_MUTE  = "tts_muted";
    private static final String KEY_WARNED_NO_GR = "warned_no_gr";

    private static TextToSpeech tts;
    private static boolean ready = false;
    private static boolean muted = false;
    private static boolean prefsLoaded = false;

    private AppTTS() {}

    // ============================================================
    // INIT
    // ============================================================
    private static void init(Context ctx) {

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
    // SPEAK — GR with fallback EN
    // ============================================================
    public static void speak(Context ctx, String text) {

        if (text == null || text.trim().isEmpty()) return;
        if (muted) return;

        init(ctx);

        if (!ready || tts == null) return;

        try {
            tts.stop();

             boolean wantGreek = AppLang.isGreek(ctx);
boolean greekOk = false;

if (wantGreek) {
    int res = tts.setLanguage(new Locale("el", "GR"));
    greekOk =
            res != TextToSpeech.LANG_MISSING_DATA &&
            res != TextToSpeech.LANG_NOT_SUPPORTED;

    if (greekOk) {
        greekEverWorked = true; // ✅ realtime upgrade detected
    }
}

if (!greekOk) {
    tts.setLanguage(Locale.US);

    // ⚠️ δείξε toast ΜΟΝΟ αν ΠΟΤΕ δεν έχει δουλέψει ελληνικό TTS
    if (wantGreek && !greekEverWorked) {
        Toast.makeText(
                ctx,
                "Δεν υπάρχει εγκατεστημένη Ελληνική φωνή.\n" +
                "Οι οδηγίες δίνονται προσωρινά στα Αγγλικά.",
                Toast.LENGTH_LONG
        ).show();
    }
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
