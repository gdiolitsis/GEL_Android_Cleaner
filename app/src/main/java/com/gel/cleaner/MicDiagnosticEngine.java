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
 * Used by:
 *  - Manual Labs (Mic check, Speaker feedback, Earpiece check)
 *  - Auto Diagnosis (later)
 *
 * Measures:
 *  - AudioRecord availability
 *  - RMS amplitude
 *  - Peak amplitude
 *  - Silence detection
 *  - Confidence grading
 *
 * Author: GDiolitsis Engine Lab (GEL)
 * ============================================================
 */
public final class MicDiagnosticEngine {

    // =========================
    // CONFIG (LOCKED)
    // =========================
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int TEST_DURATION_MS = 1800; // ~1.8 sec
    private static final double SILENCE_RMS_THRESHOLD = 200.0;
    private static final double GOOD_RMS_THRESHOLD = 800.0;
    private static final double STRONG_RMS_THRESHOLD = 1500.0;

    // Prevent instantiation
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

        public final boolean audioRecordStarted;
        public final double rms;
        public final double peak;
        public final boolean silenceDetected;
        public final String confidence;
        public final Status status;
        public final String note;

        private Result(boolean audioRecordStarted,
                       double rms,
                       double peak,
                       boolean silenceDetected,
                       String confidence,
                       Status status,
                       String note) {

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
                    "MIC CHECK → %s | RMS: %.0f | PEAK: %.0f | SILENCE: %s | CONFIDENCE: %s",
                    status.name(), rms, peak, silenceDetected ? "YES" : "NO", confidence);
        }
    }

    // =========================
    // MAIN ENTRY POINT
    // =========================
    public static Result run(Context context) {

        int minBuffer = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, CHANNEL, FORMAT);

        if (minBuffer <= 0) {
            return errorResult("AudioRecord buffer unavailable");
        }

        AudioRecord record = null;

        try {
            record = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL,
                    FORMAT,
                    minBuffer * 2
            );

            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                return errorResult("AudioRecord not initialized");
            }

            record.startRecording();

            if (record.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                return errorResult("Microphone not recording");
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
                return errorResult("No microphone samples captured");
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
                    true,
                    rms,
                    peak,
                    silence,
                    confidence,
                    status,
                    note
            );

        } catch (Throwable t) {
            return errorResult("Mic exception: " + t.getClass().getSimpleName());
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
    // INTERNAL HELPERS
    // =========================
    private static Result errorResult(String note) {
        return new Result(
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
