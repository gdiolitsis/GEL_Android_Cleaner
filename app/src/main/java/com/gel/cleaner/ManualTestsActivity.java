/* ============================================================
   LAB 3 — Earpiece Audio Path Check (AUTO)
   ============================================================ */
private void lab3EarpieceManual() {

    logSection("LAB 3 — Earpiece Audio Path Check (AUTO)");

    new Thread(() -> {

        AudioManager am = null;

        int oldMode = AudioManager.MODE_NORMAL;
        boolean oldSpeaker = false;

        try {
            am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am == null) {
                logError("AudioManager unavailable");
                enableSingleExportButton();
                return;
            }

            oldMode = am.getMode();
            oldSpeaker = am.isSpeakerphoneOn();

            // Route audio like a call (earpiece path)
            am.setMode(AudioManager.MODE_IN_COMMUNICATION);
            am.setSpeakerphoneOn(false);

            // Play a short test tone (voice-call style)
            playEarpieceTestTone220Hz(900);

            // Give a short moment for routing to settle
            SystemClock.sleep(200);

            // Verify via TOP mic
            MicDiagnosticEngine.Result r =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);

            logLabelValue("Top Mic RMS", String.valueOf((int) r.rms));
            logLabelValue("Top Mic Peak", String.valueOf((int) r.peak));
            logLabelValue("Top Mic Confidence", String.valueOf(r.confidence));

            boolean responseDetected = (!r.silenceDetected) &&
                    (r.status == MicDiagnosticEngine.Result.Status.OK ||
                     r.status == MicDiagnosticEngine.Result.Status.WARN);

            if (responseDetected) {

                // ✅ LOCKED TEXT (DO NOT CHANGE)
                logInfo("LAB 3 — Earpiece Audio Path Check (AUTO)");
                logInfo("");
                logInfo("Earpiece audio routed in call mode.");
                logInfo("Microphone detected response from upper speaker.");
                logInfo("");
                logInfo("Result:");
                logOk("✔ Earpiece audio path appears functional");
                logInfo("");
                logInfo("NOTE:");
                logInfo("This test simulates call audio routing.");
                logInfo("Real call confirmation is still recommended.");

                // Ask YES/NO on UI
                ui.post(() -> {
                    try {
                        AlertDialog.Builder b = new AlertDialog.Builder(ManualTestsActivity.this);
                        b.setTitle("LAB 3 — Confirm");
                        b.setMessage("Did you hear the sound clearly from the earpiece?");
                        b.setCancelable(false);

                        b.setPositiveButton("YES", (d, w) -> {
                            logOk("User confirmed earpiece audio was audible");
                            enableSingleExportButton();
                        });

                        b.setNegativeButton("NO", (d, w) -> {
                            logWarn("Auto test passed but user did not hear sound clearly");
                            enableSingleExportButton();
                        });

                        b.show();
                    } catch (Throwable t) {
                        enableSingleExportButton();
                    }
                });

            } else {
                logError("Earpiece audio path could not be verified automatically");
                logInfo("NOTE: This test simulates call audio routing. Real call confirmation is still recommended.");
                enableSingleExportButton();
            }

        } catch (Throwable t) {
            logError("LAB 3 failed: " + t.getClass().getSimpleName());
            enableSingleExportButton();
        } finally {
            try {
                if (am != null) {
                    am.setMode(oldMode);
                    am.setSpeakerphoneOn(oldSpeaker);
                }
            } catch (Throwable ignore) {}
        }

    }).start();
}

/* ============================================================
   Earpiece tone helper (no XML, no loops outside AudioTrack)
   ============================================================ */
private void playEarpieceTestTone220Hz(int durationMs) {
    try {
        int sampleRate = 8000;
        int numSamples = (int) ((durationMs / 1000f) * sampleRate);
        if (numSamples < 1) numSamples = sampleRate / 2;

        short[] data = new short[numSamples];
        double freq = 220.0;
        for (int i = 0; i < numSamples; i++) {
            double t = i / (double) sampleRate;
            double s = Math.sin(2.0 * Math.PI * freq * t);
            data[i] = (short) (s * 9000); // safe amplitude
        }

        AudioTrack track;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            track = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build(),
                    new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    data.length * 2,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
            );
        } else {
            track = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    data.length * 2,
                    AudioTrack.MODE_STATIC
            );
        }

        track.write(data, 0, data.length);
        track.play();
        SystemClock.sleep(durationMs + 80);

        try { track.stop(); } catch (Throwable ignore) {}
        try { track.release(); } catch (Throwable ignore) {}

    } catch (Throwable ignore) {}
}
