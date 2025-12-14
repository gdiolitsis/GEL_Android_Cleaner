// ============================================================
// MicDiagnosticEngine — Hospital Grade
// GEL Android Doctor / GEL Phone Diagnosis
// Author & Developer: GDiolitsis Engine Lab (GEL)
// ============================================================
//
// Covers:
// ✔ Live Mic Indicator
// ✔ Active Mic Detect
// ✔ AudioRecord Bench
// ✔ RMS Analysis
// ✔ Confidence Score
// ✔ OK / WARN / ERROR grading
//
// Pure native Java — no SDKs
// ============================================================

package com.gel.cleaner;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

public final class MicDiagnosticEngine {

    // ------------------------------------------------------------
    // CONFIG
    // ------------------------------------------------------------
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT  = AudioFormat.ENCODING_PCM_16BIT;

    private static final int TEST_DURATION_MS = 3000; // 3 sec
    private static final int RMS_MIN_SIGNAL   = 120;  // silence threshold
    private static final int RMS_CLIP_LEVEL   = 25000;

    private MicDiagnosticEngine() {}

    // ------------------------------------------------------------
    // RESULT MODEL
    // ------------------------------------------------------------
    public static class Result {
        public boolean micActive;
        public boolean micBusy;
        public double rmsAvg;
        public double rmsPeak;
        public int confidence; // 0–100
        public String grade;   // OK / WARN / ERROR
        public String note;
    }

    // ------------------------------------------------------------
    // MAIN ENTRY
    // ------------------------------------------------------------
    public static Result run(Context ctx) {

        Result r = new Result();

        // --------------------------------------------------------
        // 1️⃣ Active Mic / Busy Detect
        // --------------------------------------------------------
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        r.micActive = !am.isMicrophoneMute();
        r.micBusy   = (am.getMode() == AudioManager.MODE_IN_CALL
                     || am.getMode() == AudioManager.MODE_IN_COMMUNICATION);

        if (!r.micActive) {
            r.grade = "ERROR";
            r.note  = "Microphone muted by system";
            r.confidence = 0;
            return r;
        }

        // --------------------------------------------------------
        // 2️⃣ AudioRecord Bench
        // --------------------------------------------------------
        int minBuf = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, CHANNEL, FORMAT);

        if (minBuf <= 0) {
            r.grade = "ERROR";
            r.note  = "AudioRecord unsupported on device";
            r.confidence = 0;
            return r;
        }

        AudioRecord rec = null;

        try {
            rec = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL,
                    FORMAT,
                    minBuf * 2
            );

            if (rec.getState() != AudioRecord.STATE_INITIALIZED) {
                r.grade = "ERROR";
                r.note  = "AudioRecord init failed";
                r.confidence = 0;
                return r;
            }

            short[] buffer = new short[minBuf];
            long start = SystemClock.elapsedRealtime();

            double rmsSum = 0;
            double rmsMax = 0;
            int samples = 0;

            rec.startRecording();

            while (SystemClock.elapsedRealtime() - start < TEST_DURATION_MS) {

                int read = rec.read(buffer, 0, buffer.length);
                if (read <= 0) continue;

                double rms = calcRms(buffer, read);
                rmsSum += rms;
                rmsMax = Math.max(rmsMax, rms);
                samples++;
            }

            rec.stop();

            if (samples == 0) {
                r.grade = "ERROR";
                r.note  = "No audio samples captured";
                r.confidence = 0;
                return r;
            }

            r.rmsAvg  = rmsSum / samples;
            r.rmsPeak = rmsMax;

        } catch (Throwable t) {

            r.grade = "ERROR";
            r.note  = "Mic access error: " + t.getClass().getSimpleName();
            r.confidence = 0;
            return r;

        } finally {
            try { if (rec != null) rec.release(); } catch (Throwable ignore) {}
        }

        // --------------------------------------------------------
        // 3️⃣ RMS → Confidence → Grade
        // --------------------------------------------------------
        if (r.rmsAvg < RMS_MIN_SIGNAL) {
            r.grade = "ERROR";
            r.note  = "No usable microphone signal";
            r.confidence = 5;
            return r;
        }

        if (r.rmsPeak >= RMS_CLIP_LEVEL) {
            r.grade = "WARN";
            r.note  = "Clipping detected (distortion)";
            r.confidence = 55;
            return r;
        }

        // Normal signal
        r.confidence = mapConfidence(r.rmsAvg);

        if (r.confidence >= 80) {
            r.grade = "OK";
            r.note  = "Microphone signal clean and stable";
        } else {
            r.grade = "WARN";
            r.note  = "Microphone signal weak or noisy";
        }

        return r;
    }

    // ------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------
    private static double calcRms(short[] buf, int len) {
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += buf[i] * buf[i];
        }
        return Math.sqrt(sum / len);
    }

    private static int mapConfidence(double rms) {
        // Simple, deterministic mapping
        if (rms >= 8000) return 95;
        if (rms >= 4000) return 85;
        if (rms >= 2000) return 75;
        if (rms >= 1000) return 65;
        return 50;
    }
}
