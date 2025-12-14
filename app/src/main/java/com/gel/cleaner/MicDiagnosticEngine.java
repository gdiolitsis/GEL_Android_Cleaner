package com.gel.cleaner;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

import java.util.Locale;

/**
 * ============================================================
 * MicDiagnosticEngine — Hospital Edition (LOCKED)
 * ------------------------------------------------------------
 * Pure diagnostic engine (NO UI)
 *
 * Supports:
 *  - Bottom microphone (primary)
 *  - Top microphone (secondary / noise-cancel)
 *
 * Used by:
 *  - Manual Labs (Mic, Speaker auto verify)
 *  - Auto Diagnosis (later)
 *
 * Author: GDiolitsis Engine Lab (GEL)
 * ============================================================
 */
public final class MicDiagnosticEngine {

    // =========================
    // MIC TYPE (LOCKED)
    // =========================
    public enum MicType {
        BOTTOM, // Primary mic (calls, voice)
        TOP     // Secondary mic (noise cancel)
    }

    // =========================
    // CONFIG (LOCKED)
    // =========================
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int TEST_DURATION_MS = 1800;
    private static final double SILENCE_RMS_THRESHOLD = 200.0;
    private static final double GOOD_RMS_THRESHOLD = 800.0;
    private static final double STRONG_RMS_THRESHOLD = 1500.0;

    private MicDiagnosticEngine() {}

    // =========================
    // RESULT MODEL
    // =========================
    public static final class Result {

        public enum Status {
            OK,
            WARN,
            ERROR
        }

        public final MicType micType;
        public final boolean audioRecordStarted;
        public final double rms;
        public final double peak;
        public final boolean silenceDetected;
        public final String confidence;
        public final Status status;
        public final String note;

        private Result(MicType micType,
                       boolean audioRecordStarted,
                       double rms,
                       double peak,
                       boolean silenceDetected,
                       String confidence,
                       Status status,
                       String note) {

            this.micType = micType;
            this.audioRecordStarted = audioRecordStarted;
            this.rms = rms;
            this.peak = peak;
            this.silenceDetected = silenceDetected;
            this.confidence = confidence;
            this.status = status;
            this.note = note;
        }

        public String toReportLine() {
            return String.format(Locale.US,
                    "%s MIC → %s | RMS: %.0f | PEAK: %.0f | SILENCE: %s | CONFIDENCE: %s",
                    micType.name(),
                    status.name(),
                    rms,
                    peak,
                    silenceDetected ? "YES" : "NO",
                    confidence
            );
        }
    }

    // =========================
    // PUBLIC ENTRY POINTS
    // =========================

    /** Backward compatibility → defaults to BOTTOM mic */
    public static Result run(Context context) {
        return run(context, MicType.BOTTOM);
    }

    /** Explicit mic selection */
    public static Result run(Context context, MicType micType) {

        int audioSource = resolveAudioSource(micType);

        int minBuffer = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, CHANNEL, FORMAT);

        if (minBuffer <= 0) {
            return errorResult(micType, "AudioRecord buffer unavailable");
        }

        AudioRecord record = null;

        try {
            record = new AudioRecord(
                    audioSource,
                    SAMPLE_RATE,
                    CHANNEL,
                    FORMAT,
                    minBuffer * 2
            );

            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                return errorResult(micType, "AudioRecord not initialized");
            }

            record.startRecording();

            if (record.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                return errorResult(micType, "Microphone not recording");
            }

            short[] buffer = new short[minBuffer];
            long start = SystemClock.elapsedRealtime();

            double sumSquares = 0;
            double peak = 0;
            long samples = 0;

            while (SystemClock.elapsedRealtime() - start < TEST_DURATION_MS) {
                int read = record.read(buffer, 0, buffer.length);
                if (read > 0) {
                    for (int i = 0; i < read; i++) {
                        double v = buffer[i];
                        sumSquares += v * v;
                        if (Math.abs(v) > peak)
                            peak = Math.abs(v);
                        samples++;
                    }
                }
            }

            if (samples == 0) {
                return errorResult(micType, "No microphone samples captured");
            }

            double rms = Math.sqrt(sumSquares / samples);
            boolean silence = rms < SILENCE_RMS_THRESHOLD;

            String confidence;
            Result.Status status;
            String note;

            if (silence) {
                confidence = "LOW";
                status = Result.Status.ERROR;
                note = "No usable microphone signal detected";
            } else if (rms >= STRONG_RMS_THRESHOLD) {
                confidence = "HIGH";
                status = Result.Status.OK;
                note = "Microphone signal strong and clear";
            } else if (rms >= GOOD_RMS_THRESHOLD) {
                confidence = "MEDIUM";
                status = Result.Status.OK;
                note = "Microphone signal detected normally";
            } else {
                confidence = "LOW";
                status = Result.Status.WARN;
                note = "Weak microphone signal detected";
            }

            return new Result(
                    micType,
                    true,
                    rms,
                    peak,
                    silence,
                    confidence,
                    status,
                    note
            );

        } catch (Throwable t) {
            return errorResult(micType,
                    "Mic exception: " + t.getClass().getSimpleName());
        } finally {
            try {
                if (record != null) {
                    record.stop();
                    record.release();
                }
            } catch (Throwable ignore) {}
        }
    }

    // =========================
    // INTERNAL HELPERS (LOCKED)
    // =========================
    private static int resolveAudioSource(MicType micType) {
        switch (micType) {
            case TOP:
                // Secondary mic / noise cancel
                return MediaRecorder.AudioSource.CAMCORDER;
            case BOTTOM:
            default:
                // Primary mic
                return MediaRecorder.AudioSource.MIC;
        }
    }

    private static Result errorResult(MicType micType, String note) {
        return new Result(
                micType,
                false,
                0,
                0,
                true,
                "LOW",
                Result.Status.ERROR,
                note
        );
    }
}
