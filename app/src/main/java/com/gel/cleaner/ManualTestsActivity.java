// ============================================================
// LAB 14 — Battery Health Stress Test
// FINAL CLEAN VERSION — GEL EDITION
// FULL METHOD / COPY-PASTE READY
// ============================================================
private void lab14BatteryHealthStressTest() {
    
    final boolean gr = AppLang.isGreek(this);
    
    // 🔋 SNAPSHOT ΜΠΑΤΑΡΙΑΣ
    Lab14Engine.GelBatterySnapshot snap =
            lab14Engine.readSnapshot();

    if (lab14Running) {
        logWarn(gr
                ? "Το LAB 14 εκτελείται ήδη."
                : "LAB 14 already running.");
        return;
    }
    
    resetBatteryDiagnostics();

    lab14Running = true;

    final Lab14Engine engine = new Lab14Engine(this);

    try {

        // ------------------------------------------------------------
        // 1) INITIAL SNAPSHOT
        // ------------------------------------------------------------
        final Lab14Engine.GelBatterySnapshot snapStart = engine.readSnapshot();

        if (snapStart.charging) {
            logError(gr
                    ? "Η δοκιμή καταπόνησης απαιτεί η συσκευή να ΜΗΝ φορτίζει."
                    : "Stress test requires device NOT charging.");
            lab14Running = false;
            return;
        }

        if (snapStart.chargeNowMah <= 0) {
            logError(gr
                    ? "Ο Charge Counter δεν είναι διαθέσιμος. Το LAB 14 δεν μπορεί να εκτελεστεί."
                    : "Charge Counter unavailable. LAB 14 cannot run.");
            lab14Running = false;
            return;
        }

        final long startMah = snapStart.chargeNowMah;
        final boolean rooted = snapStart.rooted;
        final long cycles = snapStart.cycleCount;
        final float tempStart = snapStart.temperature;
        final int batteryPercent = getBatteryPercentSafe();

        final float voltageStart = getBatteryVoltageFiltered();

        final Float cpuTempStart = readCpuTempSafe();
        final Float gpuTempStart = readGpuTempSafe();

        final int durationSec = LAB14_TOTAL_SECONDS;
        lastSelectedStressDurationSec = durationSec;

        final long baselineFullMah =
                (snapStart.chargeFullMah > 0)
                        ? snapStart.chargeFullMah
                        : -1;

        // ------------------------------------------------------------
        // 2) HEADER LOGS
        // ------------------------------------------------------------
        appendHtml("<br>");
        logLine();
        logInfo(gr
                ? "LAB 14 — Δοκιμή Καταπόνησης & Υγείας Μπαταρίας"
                : "LAB 14 — Battery Health Stress Test");
        logLine();

logLabelOkValue(
        gr ? "Λειτουργία" : "Mode",
        rooted
                ? (gr ? "Προηγμένη (Root access)" : "Advanced (Rooted)")
                : (gr ? "Τυπική (Χωρίς Root)" : "Standard (Unrooted)")
);

logLabelOkValue(
        gr ? "Διάρκεια δοκιμής" : "Duration",
        durationSec + (gr
                ? " δευτ. (εργαστηριακή λειτουργία)"
                : " sec (laboratory mode)")
);

logLabelOkValue(
        gr ? "Προφίλ καταπόνησης" : "Stress profile",
        gr
                ? "Fast stress + GEL C Mode + vibration + video + memory bandwidth"
                : "Fast stress + GEL C Mode + vibration + video + memory bandwidth"
);

logLabelOkValue(
        gr ? "Αρχικές συνθήκες" : "Start conditions",
        String.format(
                Locale.US,
                gr
                        ? "φόρτιση=%d mAh, ποσοστό=%d%%, κατάσταση=Αποφόρτιση, θερμοκρασία=%.1f°C"
                        : "charge=%d mAh, level=%d%%, status=Discharging, temp=%.1f°C",
                startMah,
                Math.max(0, batteryPercent),
                (Float.isNaN(tempStart) ? 0f : tempStart)
        )
);

logLabelOkValue(
        gr ? "Πηγή δεδομένων" : "Data source",
        snapStart.source
);

        if (baselineFullMah > 0) {
            logLabelOkValue(
                    gr ? "Αναφερόμενη πλήρης χωρητικότητα" : "Battery capacity baseline",
                    baselineFullMah + (gr
                            ? " mAh (από fuel-gauge counter)"
                            : " mAh (counter-based)")
            );
        } else {
            logLabelWarnValue(
                    gr ? "Αναφερόμενη πλήρης χωρητικότητα" : "Battery capacity baseline",
                    gr
                            ? "Μη διαθέσιμη (δεν εκτίθεται counter)"
                            : "N/A (counter-based)"
            );
        }

        logLabelOkValue(
                gr ? "Κύκλοι φόρτισης" : "Cycle count",
                cycles > 0
                        ? String.valueOf(cycles)
                        : (gr ? "Μη διαθέσιμο" : "N/A")
        );

        logLabelOkValue(
                gr ? "Κατάσταση οθόνης" : "Screen state",
                gr
                        ? "Φωτεινότητα στο ΜΕΓΙΣΤΟ, keep screen on ενεργό"
                        : "Brightness forced to MAX, keep screen on active"
        );

        int cores = Runtime.getRuntime().availableProcessors();

        logLabelOkValue(
                gr ? "Νήματα καταπόνησης CPU" : "CPU stress threads",
                cores + (gr
                        ? " (λογικοί πυρήνες=" + cores + ")"
                        : " (cores=" + cores + ")")
        );

        if (cpuTempStart != null) {
            logLabelOkValue(
                    gr ? "Θερμοκρασία CPU (έναρξη)" : "CPU temperature (start)",
                    String.format(Locale.US, "%.1f°C", cpuTempStart)
            );
        } else {
            logLabelWarnValue(
                    gr ? "Θερμοκρασία CPU (έναρξη)" : "CPU temperature (start)",
                    gr ? "Μη διαθέσιμη" : "N/A"
            );
        }

        if (gpuTempStart != null) {
            logLabelOkValue(
                    gr ? "Θερμοκρασία GPU (έναρξη)" : "GPU temperature (start)",
                    String.format(Locale.US, "%.1f°C", gpuTempStart)
            );
        } else {
            logLabelWarnValue(
                    gr ? "Θερμοκρασία GPU (έναρξη)" : "GPU temperature (start)",
                    gr ? "Μη διαθέσιμη" : "N/A"
            );
        }

        logLabelOkValue(
                gr ? "Παρακολουθούμενα θερμικά πεδία" : "Thermal domains",
                "CPU / GPU / SKIN / PMIC / BATT"
        );

        logLine();

        // ------------------------------------------------------------
        // 3) MAIN DIALOG
        // ------------------------------------------------------------
        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        TextView title = new TextView(this);
        title.setText(
                gr
                        ? "LAB 14 — Δοκιμή Καταπόνησης Υγείας Μπαταρίας"
                        : "LAB 14 — Battery Health Stress Test"
        );
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        root.addView(title);

        final TextView statusText = new TextView(this);
        statusText.setText(
                gr
                        ? "Η δοκιμή βρίσκεται σε εξέλιξη…"
                        : "Stress test running…"
        );
        statusText.setTextColor(0xFF39FF14);
        statusText.setTextSize(15f);
        statusText.setGravity(Gravity.CENTER);
        root.addView(statusText);

        final TextView dotsView = new TextView(this);
        dotsView.setText("•");
        dotsView.setTextColor(0xFF39FF14);
        dotsView.setTextSize(22f);
        dotsView.setGravity(Gravity.CENTER);
        root.addView(dotsView);

        final TextView counterText = new TextView(this);
        counterText.setText(
                gr
                        ? "Πρόοδος: 0 / " + durationSec + " δευτ."
                        : "Progress: 0 / " + durationSec + " sec"
        );
        counterText.setTextColor(0xFF39FF14);
        counterText.setGravity(Gravity.CENTER);
        root.addView(counterText);

        final LinearLayout progressBar = new LinearLayout(this);
        progressBar.setOrientation(LinearLayout.HORIZONTAL);
        progressBar.setGravity(Gravity.CENTER);

        for (int i = 0; i < 10; i++) {
            View seg = new View(this);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(0, dp(10), 1f);
            lp.setMargins(dp(3), 0, dp(3), 0);
            seg.setLayoutParams(lp);
            seg.setBackgroundColor(0xFF333333);
            progressBar.addView(seg);
        }
        root.addView(progressBar);

        Button exitBtn = new Button(this);
        exitBtn.setText(gr ? "Έξοδος τεστ" : "Exit test");
        exitBtn.setAllCaps(false);
        exitBtn.setTextColor(0xFFFFFFFF);
        exitBtn.setTypeface(null, Typeface.BOLD);

        GradientDrawable exitBg = new GradientDrawable();
        exitBg.setColor(0xFF8B0000);
        exitBg.setCornerRadius(dp(10));
        exitBg.setStroke(dp(3), 0xFFFFD700);
        exitBtn.setBackground(exitBg);

        LinearLayout.LayoutParams lpExit =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lpExit.setMargins(0, dp(14), 0, 0);
        exitBtn.setLayoutParams(lpExit);

        exitBtn.setOnClickListener(v -> {
    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

            try {
                if (lab14StressVideo != null) {
                    lab14StressVideo.stopPlayback();
                    ViewParent parent = lab14StressVideo.getParent();
                    if (parent instanceof ViewGroup) {
                        ((ViewGroup) parent).removeView(lab14StressVideo);
                    }
                    lab14StressVideo = null;
                }
            } catch (Throwable ignore) {}

            lab14Running = false;

            try {
                if (lab14Dialog != null && lab14Dialog.isShowing())
                    lab14Dialog.dismiss();
            } catch (Throwable ignore) {}
            lab14Dialog = null;

            logWarn(
                    gr
                            ? "LAB 14 ακυρώθηκε από τον χρήστη."
                            : "LAB 14 cancelled by user."
            );
        });

        root.addView(exitBtn);

        b.setView(root);
        lab14Dialog = b.create();
        if (lab14Dialog.getWindow() != null) {
            lab14Dialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        lab14Dialog.show();

// ------------------------------------------------------------
// 5) FAST BATTERY STRESS (45 sec) — BACKGROUND THREAD FIX
// ------------------------------------------------------------
final long t0 = SystemClock.elapsedRealtime();

new Thread(() -> {

    vStart[0] = getBatteryVoltageFiltered();

startCpuBurn_C_Mode();
SystemClock.sleep(15000);
SystemClock.sleep(250);
vLoad1[0] = getBatteryVoltageFiltered();

stopCpuBurn();
SystemClock.sleep(15000);
SystemClock.sleep(250);
vRecover[0] = getBatteryVoltageFiltered();

startCpuBurn_C_Mode();
SystemClock.sleep(15000);
SystemClock.sleep(250);
vLoad2[0] = getBatteryVoltageFiltered();

stopCpuBurn();

if (!Float.isNaN(vStart[0]) && !Float.isNaN(vLoad1[0]))
    sag1[0] = vStart[0] - vLoad1[0];

if (!Float.isNaN(vRecover[0]) && !Float.isNaN(vLoad2[0]))
    sag2[0] = vRecover[0] - vLoad2[0];

// ----------------------------------------------------
// PMIC RAIL STABILITY CHECK
// ----------------------------------------------------
if (!Float.isNaN(sag1[0]) && !Float.isNaN(sag2[0])) {

    float railDrop = Math.abs(sag1[0] - sag2[0]);

    if (railDrop > 0.08f) {

        collapseRisk[0] = true;

        logLabelWarnValue(
                gr ? "Αστάθεια γραμμής τροφοδοσίας"
                   : "Power rail instability",
                gr
                        ? "Ασύμμετρη πτώση τάσης μεταξύ κύκλων φορτίου."
                        : "Asymmetric voltage drop between load cycles."
        );
    }

    if (railDrop > 0.20f)
        swellingRisk[0] = true;
}

if (!Float.isNaN(sag1[0]) && !Float.isNaN(sag2[0]))
    sagAvg[0] = (sag1[0] + sag2[0]) / 2f;
        
// ----------------------------------------------------
// CELL IMBALANCE DETECTOR
// ----------------------------------------------------
if (!Float.isNaN(sag1[0]) && !Float.isNaN(sag2[0])) {

    float sagDiff = Math.abs(sag1[0] - sag2[0]);

    if (sagDiff > 0.05f)
        cellImbalanceRisk[0] = true;
}

    if (!Float.isNaN(vStart[0])
        && !Float.isNaN(vLoad1[0])
        && !Float.isNaN(vRecover[0])
        && !Float.isNaN(vLoad2[0])) {

    float variance =
            Math.abs(vStart[0] - vLoad1[0])
                    + Math.abs(vRecover[0] - vLoad2[0]);

    voltageStability[0] =
            Math.max(0f, 100f - variance * 120f);
    }

    runOnUiThread(() -> {

    if (!Float.isNaN(sag1[0]) && !Float.isNaN(sag2[0])) {

        logLabelValue(
                gr ? "Γρήγορη δοκιμή καταπόνησης"
                   : "Fast stress test",
                String.format(
                        Locale.US,
                        "Sag1=%.3fV | Sag2=%.3fV",
                        sag1[0],
                        sag2[0]
                )
        );

        if (sag1[0] > 0.35f || sag2[0] > 0.40f) {

            logLabelWarnValue(
                    gr ? "Διάγνωση" : "Diagnosis",
                    gr
                            ? "Έντονη πτώση τάσης — πιθανή φθορά κυψελών"
                            : "Severe voltage sag — degraded battery cells"
            );

            } else {

                logLabelOkValue(
                        gr ? "Διάγνωση" : "Diagnosis",
                        gr
                                ? "Δεν εντοπίστηκε ανωμαλία"
                                : "No abnormal sag detected"
                );
            }
        }
        
// ----------------------------------------------------
// BATTERY FAILURE PREDICTOR
// ----------------------------------------------------
if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(sagAvg[0])) {

    float sagScore = Math.min(1f, sagAvg[0] / 0.22f);
    float rScore   = Math.min(1f, internalResistance[0] / 0.25f);

    float thermalScore = 0f;

    if (!Float.isNaN(thermalImpedance[0])) {
        thermalScore = Math.min(1f, thermalImpedance[0] / 18f);
    }

    float failureIndex =
            (0.45f * rScore) +
            (0.35f * sagScore) +
            (0.20f * thermalScore);

    if (failureIndex > 0.75f)
        batteryFailureRisk[0] = true;
}
        
        startMainStressPhase(
                durationSec,
                t0,
                dotsView,
                counterText,
                progressBar
        );

    });

}).start();

// ------------------------------------------------------------
// 6) MAIN STRESS START
// ------------------------------------------------------------

final String[] dotFrames = {"•", "• •", "• • •"};

applyMaxBrightnessAndKeepOn();
startCpuBurn_C_Mode();
startMemoryStress();
startGpuStress();

ui.postDelayed(() -> {
    voltageUnderLoad[0] = getBatteryVoltageFiltered();
}, 5250);

        final Vibrator vib =
                (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ui.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!lab14Running) return;

                try {
                    if (vib != null && vib.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vib.vibrate(VibrationEffect.createOneShot(
                                    80,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            ));
                        } else {
                            vib.vibrate(80);
                        }
                    }
                } catch (Throwable ignore) {}

                ui.postDelayed(this, 1500);
            }
        }, 1500);

        try {

            lab14StressVideo = new VideoView(this);

            lab14StressVideo.setLayoutParams(
                    new ViewGroup.LayoutParams(1, 1)
            );

            lab14StressVideo.setVideoURI(
                    Uri.parse(
                            "android.resource://" +
                                    getPackageName() +
                                    "/" +
                                    R.raw.battery_stress_loop
                    )
            );

            lab14StressVideo.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.setVolume(0f, 0f);
            });

            ((ViewGroup) findViewById(android.R.id.content))
                    .addView(lab14StressVideo);

            lab14StressVideo.start();

        } catch (Throwable ignore) {}
        
try {

    ui.post(new Runnable() {

    int dotStep = 0;
    int lastSeg = -1;

    @Override
public void run() {

    if (!lab14Running) {
        ui.removeCallbacks(this);
        return;
    }

                long now = SystemClock.elapsedRealtime();
                int elapsed = (int) ((now - t0) / 1000);

                dotsView.setText(dotFrames[dotStep++ % dotFrames.length]);
                counterText.setText(
                        gr
                                ? "Πρόοδος: " + Math.min(elapsed, durationSec) + " / " + durationSec + " δευτ."
                                : "Progress: " + Math.min(elapsed, durationSec) + " / " + durationSec + " sec"
                );

                int segSpan = Math.max(1, durationSec / 10);
                int seg = Math.min(10, elapsed / segSpan);

                if (seg != lastSeg) {
                    lastSeg = seg;
                    for (int i = 0; i < progressBar.getChildCount(); i++) {
                        progressBar.getChildAt(i)
                                .setBackgroundColor(i < seg ? 0xFF39FF14 : 0xFF333333);
                    }
                }

if (elapsed < durationSec) {

    if (!lab14Running) {
        ui.removeCallbacks(this);
        return;
    }

    ui.postDelayed(this, 1000);
    return;
}

                // ----------------------------------------------------
                // 7) STOP STRESS / CLEANUP
                // ----------------------------------------------------
                try {
                    if (lab14StressVideo != null) {
                        lab14StressVideo.stopPlayback();
                        ViewParent parent = lab14StressVideo.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(lab14StressVideo);
                        }
                        lab14StressVideo = null;
                    }
                } catch (Throwable ignore) {}

                lab14Running = false;

try { stopCpuBurn(); } catch (Throwable ignore) {}
try { stopMemoryStress(); } catch (Throwable ignore) {}
try { stopGpuStress(); } catch (Throwable ignore) {}
try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

                try {
                    if (lab14Dialog != null && lab14Dialog.isShowing())
                        lab14Dialog.dismiss();
                } catch (Throwable ignore) {}
                lab14Dialog = null;

                // ----------------------------------------------------
                // 8) POST-LOAD RECOVERY
                // ----------------------------------------------------
                if (!Float.isNaN(voltageUnderLoad[0])) {
                    SystemClock.sleep(3000);
                    float vr = getBatteryVoltageFiltered();
                    if (!Float.isNaN(vr)) {
                        voltageRecovery[0] =
        Math.max(0f, vr - voltageUnderLoad[0]);
                    }
                }
                
// ----------------------------------------------------
// VOLTAGE RECOVERY SPEED (electrochemical response)
// ----------------------------------------------------
if (!Float.isNaN(vLoad1[0]) && !Float.isNaN(vRecover[0])) {

    float recoveryDelta = vRecover[0] - vLoad1[0];

    // rest window = 15 sec
    voltageRecoverySpeed[0] = recoveryDelta / 15f;
}

// ----------------------------------------------------
// 9) FINAL SNAPSHOT
// ----------------------------------------------------

// force PMIC fuel gauge refresh
SystemClock.sleep(800);

final Lab14Engine.GelBatterySnapshot snapEnd = engine.readSnapshot();

                if (snapEnd.chargeNowMah <= 0) {
                    logWarn(gr
                            ? "Αδυναμία ανάγνωσης τελικού charge counter."
                            : "Unable to read final charge counter.");
                    return;
                }

                final long endMah = snapEnd.chargeNowMah;
                final float tempEnd = snapEnd.temperature;

                final Float cpuTempEnd = readCpuTempSafe();
                final Float gpuTempEnd = readGpuTempSafe();

                final long dtMs = Math.max(1, SystemClock.elapsedRealtime() - t0);
                final long drainMah = Math.max(0, startMah - endMah);

// ----------------------------------------------------
// DECLARED CAPACITY VALIDATION
// ----------------------------------------------------
if (baselineFullMah > 0 && drainMah > 0) {

    float drainRatio =
            (float) drainMah / (float) baselineFullMah;

    if (drainRatio > 0.12f) {

        logLabelWarnValue(
                gr ? "Έλεγχος δηλωμένης χωρητικότητας"
                   : "Declared capacity check",
                gr
                        ? "Η δηλωμένη χωρητικότητα πιθανόν δεν είναι ρεαλιστική."
                        : "Declared battery capacity may be unrealistic."
        );

    } else {

        logLabelOkValue(
                gr ? "Έλεγχος δηλωμένης χωρητικότητας"
                   : "Declared capacity check",
                gr
                        ? "Η δηλωμένη χωρητικότητα φαίνεται ρεαλιστική."
                        : "Declared capacity appears realistic."
        );
    }
}

                final boolean validDrain =
                        drainMah > 0 &&
                                !(baselineFullMah > 0 && drainMah > (long) (baselineFullMah * 0.30));

                final double mahPerHour =
                        validDrain ? (drainMah * 3600000.0) / dtMs : -1;

                double drainPercentPerHour = -1;
                if (baselineFullMah > 0 && mahPerHour > 0) {
                    drainPercentPerHour = (mahPerHour / baselineFullMah) * 100.0;
                }
                
// ------------------------------------------------------------
// BATTERY CALIBRATION DRIFT DETECTION
// ------------------------------------------------------------
if (baselineFullMah > 0 && startMah > 0) {

    expectedPercent[0] =
            (float) startMah / (float) baselineFullMah * 100f;
}

if (!Float.isNaN(expectedPercent[0]) && batteryPercent >= 0) {

    percentDeviation[0] =
            Math.abs(expectedPercent[0] - batteryPercent);
}

if (!Float.isNaN(percentDeviation[0]) && percentDeviation[0] > 15f) {

    calibrationDrift[0] = true;
}

// ----------------------------------------------------
// 10) ELECTRICAL ANALYSIS
// ----------------------------------------------------

float estimatedESR = Float.NaN;

if (!Float.isNaN(voltageStart) &&
        !Float.isNaN(voltageUnderLoad[0])) {

    float sag = voltageStart - voltageUnderLoad[0];

    if (sag < 0.015f)
        sag = 0f;

    float sagFiltered = sag;

    if (!Float.isNaN(sagAvg[0]))
        sagFiltered = (sag + sagAvg[0]) / 2f;

    float currentNow = getBatteryCurrentNowSafe();

    if (!Float.isNaN(currentNow)) {

        float currentAmp = Math.abs(currentNow) / 1000000f;

        if (currentAmp > 0.1f && currentAmp < 8f) {

            // ESR estimation
            estimatedESR = sagFiltered / currentAmp;

            // clamp unrealistic ESR (PMIC artefacts)
            if (estimatedESR > 0.5f)
                estimatedESR = Float.NaN;

            // internal resistance estimation
            internalResistance[0] = estimatedESR;
        }
    }
}
    
// ----------------------------------------------------
// BATTERY ENERGY EFFICIENCY INDEX
// ----------------------------------------------------
float energyEfficiency = Float.NaN;

if (!Float.isNaN(voltageStart) &&
    !Float.isNaN(voltageUnderLoad[0]) &&
    drainMah > 0) {

    float voltageDrop = voltageStart - voltageUnderLoad[0];

    if (voltageDrop > 0.01f && voltageDrop < 0.6f) {

    energyEfficiency =
            (float) drainMah / voltageDrop;
}
}

                if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(voltageRecovery[0])) {

    if (internalResistance[0] > 0.18f &&
        voltageRecovery[0] < 0.07f) {

        collapseRisk[0] = true;
    }
}

// ----------------------------------------------------
// THERMAL IMPEDANCE (°C per Amp)
// ----------------------------------------------------
if (!Float.isNaN(tempStart) &&
    !Float.isNaN(tempEnd)) {

    float currentNow = getBatteryCurrentNowSafe();

    if (!Float.isNaN(currentNow)) {

        float currentAmp = Math.abs(currentNow) / 1000000f;

        if (currentAmp > 0.05f) {

            float tempRise = tempEnd - tempStart;

            if (tempRise > 0.1f) {

                thermalImpedance[0] = tempRise / currentAmp;

            }
        }
    }
}

// ----------------------------------------------------
// CELL ELASTICITY INDEX
// ----------------------------------------------------
if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(voltageRecoverySpeed[0]) &&
    !Float.isNaN(sagAvg[0])) {

    float rFactor = Math.min(1f, internalResistance[0] / 0.25f);
    float sagFactor = Math.min(1f, sagAvg[0] / 0.25f);
    float recFactor = Math.min(1f, voltageRecoverySpeed[0] / 0.012f);

    cellElasticityIndex[0] =
        Math.max(
                0f,
                Math.min(
                        100f,
                        100f * (0.5f * recFactor + 0.3f * (1f - rFactor) + 0.2f * (1f - sagFactor))
                )
        );
}

// ----------------------------------------------------
// POWER STABILITY FACTOR
// ----------------------------------------------------
if (!Float.isNaN(vStart[0]) &&
    !Float.isNaN(vLoad1[0]) &&
    !Float.isNaN(vRecover[0]) &&
    !Float.isNaN(vLoad2[0])) {

    float d1 = Math.abs(vStart[0] - vLoad1[0]);
    float d2 = Math.abs(vRecover[0] - vLoad2[0]);

    float diff = (d1 + d2) / 2f;

powerStabilityFactor[0] =
        Math.max(0f, Math.min(100f, 100f - diff * 400f));
}

// ----------------------------------------------------
// BATTERY STRESS SIGNATURE
// ----------------------------------------------------
if (!Float.isNaN(sag1[0]) &&
    !Float.isNaN(sag2[0]) &&
    !Float.isNaN(voltageRecovery[0])) {

    float asym = Math.abs(sag1[0] - sag2[0]);

    float recoveryNorm = Math.min(1f, voltageRecovery[0] / 0.15f);

    stressSignature[0] =
            Math.max(0f,
                    Math.min(
                            100f,
                            100f * (1f - asym * 2f) * recoveryNorm
                    )
            );
}

// ----------------------------------------------------
// CELL BALANCE CHECK
// ----------------------------------------------------
if (cellImbalanceRisk[0]) {

    logLabelWarnValue(
            gr ? "Ισορροπία κυψελών μπαταρίας"
               : "Battery cell balance",
            gr
                    ? "Εντοπίστηκε πιθανή ασυμμετρία κυψελών"
                    : "Possible lithium cell imbalance detected"
    );

} else {

    logLabelOkValue(
            gr ? "Ισορροπία κυψελών μπαταρίας"
               : "Battery cell balance",
            gr
                    ? "Δεν εντοπίστηκε ανισορροπία"
                    : "No imbalance detected"
    );
}

// ----------------------------------------------------
// BATTERY FAILURE PREDICTION
// ----------------------------------------------------
if (batteryFailureRisk[0]) {

    logLabelErrorValue(
            gr ? "Πρόβλεψη αποτυχίας μπαταρίας"
               : "Battery failure prediction",
            gr
                    ? "Υψηλή πιθανότητα αστάθειας μπαταρίας στους επόμενους κύκλους."
                    : "High probability of battery instability in upcoming cycles."
    );

} else {

    logLabelOkValue(
            gr ? "Πρόβλεψη αποτυχίας μπαταρίας"
               : "Battery failure prediction",
            gr
                    ? "Δεν εντοπίστηκε άμεσος κίνδυνος."
                    : "No imminent battery failure detected."
    );
}

// ----------------------------------------------------
// BATTERY STRUCTURAL INTEGRITY INDEX
// ----------------------------------------------------
if (!Float.isNaN(cellElasticityIndex[0]) &&
    !Float.isNaN(stressSignature[0])) {

    float thermalFactor = Float.NaN;

    if (!Float.isNaN(thermalImpedance[0])) {
        thermalFactor = Math.max(0f, Math.min(100f, 100f - (thermalImpedance[0] * 4f)));
    }

    if (!Float.isNaN(thermalFactor)) {
        structuralIntegrityIndex[0] =
                Math.max(
                        0f,
                        Math.min(
                                100f,
                                (cellElasticityIndex[0] * 0.4f) +
                                (stressSignature[0] * 0.4f) +
                                (thermalFactor * 0.2f)
                        )
                );
    } else {
        structuralIntegrityIndex[0] =
                Math.max(
                        0f,
                        Math.min(
                                100f,
                                (cellElasticityIndex[0] * 0.5f) +
                                (stressSignature[0] * 0.5f)
                        )
                );
    }
}

if (mahPerHour > 1200)
    collapseRisk[0] = true;

if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(tempStart) &&
    !Float.isNaN(tempEnd)) {

                    float tempRise = tempEnd - tempStart;

                    if (internalResistance[0] > 0.20f &&
                            tempRise > 8.0f) {
                        swellingRisk[0] = true;
                    }
                }

                if (!Float.isNaN(voltageRecovery[0]) &&
                        voltageRecovery[0] < 0.04f) {
                    swellingRisk[0] = true;
                }

// ----------------------------------------------------
// BATTERY STATE OF HEALTH (SOH)
// ----------------------------------------------------
if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(sagAvg[0])) {

    float rFactor =
            Math.min(1f, internalResistance[0] / 0.25f);

    float sagFactor =
            Math.min(1f, sagAvg[0] / 0.25f);

    float thermalFactor = 0f;

    if (!Float.isNaN(thermalImpedance[0])) {

        thermalFactor =
                Math.min(1f, thermalImpedance[0] / 20f);
    }

    float drainFactor = 0f;

    if (drainPercentPerHour > 0) {

        drainFactor =
                Math.min(1f, (float)drainPercentPerHour / 35f);
    }

    batterySOH[0] =
            Math.max(
                    0f,
                    Math.min(
                            100f,
                            100f * (
                                    1f
                                    - (0.35f * rFactor)
                                    - (0.30f * sagFactor)
                                    - (0.20f * thermalFactor)
                                    - (0.15f * drainFactor)
                            )
                    )
            );
}

// ----------------------------------------------------
// BATTERY AUTHENTICITY CHECK (counterfeit detection)
// ----------------------------------------------------
boolean batteryAuthenticitySuspicion = false;

if (!Float.isNaN(internalResistance[0]) &&
    !Float.isNaN(voltageRecovery[0]) &&
    baselineFullMah > 0) {

    float ir = internalResistance[0];
    float rec = voltageRecovery[0];

    boolean highResistance =
            ir > 0.22f;

    boolean weakRecovery =
            rec < 0.06f;

    boolean suspiciousCapacity =
            baselineFullMah > 6000;

    boolean poorCellDynamics =
            !Float.isNaN(cellElasticityIndex[0]) &&
            cellElasticityIndex[0] < 55f;

    if ((highResistance && weakRecovery) ||
        suspiciousCapacity ||
        poorCellDynamics) {

        batteryAuthenticitySuspicion = true;

        logLabelWarnValue(
                gr ? "Έλεγχος γνησιότητας μπαταρίας"
                   : "Battery authenticity check",
                gr
                        ? "Ενδείξεις πιθανής μη γνήσιας ή χαμηλής ποιότητας μπαταρίας"
                        : "Indicators of possible non-original or low-quality battery"
        );

    } else {

        logLabelOkValue(
                gr ? "Έλεγχος γνησιότητας μπαταρίας"
                   : "Battery authenticity check",
                gr
                        ? "Δεν εντοπίστηκαν ενδείξεις μη γνήσιας μπαταρίας"
                        : "No indicators of non-original battery detected"
        );
    }
}

                // ----------------------------------------------------
                // 11) SAVE RUN / CONFIDENCE
                // ----------------------------------------------------
                if (validDrain) engine.saveDrainValue(mahPerHour);
                engine.saveRun();

                final Lab14Engine.ConfidenceResult conf = engine.computeConfidence();

                boolean variabilityDetected =
                        !validDrain || conf.percent < 60;

                // ----------------------------------------------------
                // 12) AGING
                // ----------------------------------------------------
                final Lab14Engine.AgingResult aging =
                        engine.computeAging(
                                mahPerHour,
                                conf,
                                cycles,
                                tempStart,
                                tempEnd
                        );

                int agingIndex = -1;
                String agingInterp = "N/A";

                if (validDrain &&
                        conf.percent >= 70 &&
                        !Float.isNaN(tempStart) &&
                        !Float.isNaN(tempEnd)) {

                    double tempRise = Math.max(0.0, (double) tempEnd - (double) tempStart);

                    double idx = 0;

                    double d = Math.max(0.0, mahPerHour - 600.0);
                    idx += Math.min(55.0, d / 800.0 * 55.0);

                    double tr = Math.max(0.0, tempRise - 3.0);
                    idx += Math.min(25.0, tr / 11.0 * 25.0);

                    if (cycles > 0) {
                        double cy = Math.max(0.0, cycles - 150.0);
                        idx += Math.min(15.0, cy / 350.0 * 15.0);
                    }

                    idx += Math.min(10.0, (100 - conf.percent) / 5.0);

                    agingIndex =
                            (int) Math.round(Math.max(0.0, Math.min(100.0, idx)));

                    if (agingIndex < 15) agingInterp = "Excellent (very low aging indicators)";
                    else if (agingIndex < 30) agingInterp = "Good (low aging indicators)";
                    else if (agingIndex < 50) agingInterp = "Moderate (watch trend)";
                    else if (agingIndex < 70) agingInterp = "High (aging signs detected)";
                    else agingInterp = "Severe (strong aging indicators)";

                } else {
                    agingIndex = -1;
                    agingInterp = "Insufficient data (need stable runs with confidence >=70%)";
                }

                // ----------------------------------------------------
                // 13) LIFESPAN ESTIMATE
                // ----------------------------------------------------
                float monthsTo70 = Float.NaN;

                if (agingIndex >= 0) {

                    float agingSpeed = 0f;

                    agingSpeed += agingIndex * 0.5f;

                    if (cycles > 0)
                        agingSpeed += Math.min(40f, cycles * 0.05f);

                    if (!Float.isNaN(tempEnd) && tempEnd > 40f)
                        agingSpeed += (tempEnd - 40f) * 2f;

                    if (agingSpeed > 0) {
                        monthsTo70 = Math.max(3f, 36f - agingSpeed);
                    }
                }

                // ----------------------------------------------------
                // 14) FINAL SCORE
                // ----------------------------------------------------
                int finalScore = 100;

                if (!validDrain) {
                    finalScore = 0;
                } else {

                    if (drainPercentPerHour >= 35) finalScore -= 45;
                    else if (drainPercentPerHour >= 25) finalScore -= 30;
                    else if (drainPercentPerHour >= 18) finalScore -= 18;
                    else if (drainPercentPerHour >= 12) finalScore -= 8;

                    if (!Float.isNaN(tempEnd)) {
                        if (tempEnd >= 55f) finalScore -= 35;
                        else if (tempEnd >= 45f) finalScore -= 18;
                        else if (tempEnd >= 40f) finalScore -= 8;
                    }

                    if (!Float.isNaN(tempStart) && !Float.isNaN(tempEnd)) {
                        float rise = Math.max(0f, tempEnd - tempStart);
                        if (rise >= 12f) finalScore -= 18;
                        else if (rise >= 8f) finalScore -= 10;
                        else if (rise >= 5f) finalScore -= 5;
                    }

                    if (cycles > 0) {
                        if (cycles >= 600) finalScore -= 20;
                        else if (cycles >= 400) finalScore -= 12;
                        else if (cycles >= 250) finalScore -= 6;
                    }

                    if (cpuTempEnd != null) {
                        if (cpuTempEnd >= 85f) finalScore -= 8;
                        else if (cpuTempEnd >= 75f) finalScore -= 4;
                    }

                    if (gpuTempEnd != null) {
                        if (gpuTempEnd >= 80f) finalScore -= 6;
                        else if (gpuTempEnd >= 70f) finalScore -= 3;
                    }

                    if (!Float.isNaN(internalResistance[0])) {
                        if (internalResistance[0] >= 0.25f) finalScore -= 15;
                        else if (internalResistance[0] >= 0.18f) finalScore -= 8;
                    }

                    if (collapseRisk[0]) finalScore -= 10;
                    if (calibrationDrift[0]) finalScore -= 5;

                    if (finalScore < 0) finalScore = 0;
                    if (finalScore > 100) finalScore = 100;
                }

                String finalLabel;
                if (!validDrain) finalLabel = "Informational";
                else if (finalScore >= 90) finalLabel = "Strong";
                else if (finalScore >= 80) finalLabel = "Excellent";
                else if (finalScore >= 70) finalLabel = "Very good";
                else if (finalScore >= 60) finalLabel = "Normal";
                else finalLabel = "Weak";

                String healthClass;
                if (finalScore >= 92) healthClass = "A+";
                else if (finalScore >= 85) healthClass = "A";
                else if (finalScore >= 75) healthClass = "B";
                else if (finalScore >= 60) healthClass = "C";
                else healthClass = "D";
                
// ----------------------------------------------------
// MEASUREMENT CONFIDENCE ENGINE
// ----------------------------------------------------
float measurementConfidence = 100f;

// missing voltage metrics
if (Float.isNaN(vStart[0]) || Float.isNaN(vLoad1[0]) || Float.isNaN(vRecover[0]))
    measurementConfidence -= 20f;

// missing sag
if (Float.isNaN(sagAvg[0]))
    measurementConfidence -= 15f;

// missing internal resistance
if (Float.isNaN(internalResistance[0]))
    measurementConfidence -= 15f;

// missing recovery
if (Float.isNaN(voltageRecovery[0]))
    measurementConfidence -= 10f;

// missing temperature
if (Float.isNaN(tempStart) || Float.isNaN(tempEnd))
    measurementConfidence -= 10f;

// missing recovery speed
if (Float.isNaN(voltageRecoverySpeed[0]))
    measurementConfidence -= 5f;

// clamp
if (measurementConfidence < 0f) measurementConfidence = 0f;

String confidenceLabel;

if (measurementConfidence >= 90)
    confidenceLabel = gr ? "Πολύ υψηλή αξιοπιστία" : "Very high confidence";
else if (measurementConfidence >= 75)
    confidenceLabel = gr ? "Υψηλή αξιοπιστία" : "High confidence";
else if (measurementConfidence >= 60)
    confidenceLabel = gr ? "Μέτρια αξιοπιστία" : "Moderate confidence";
else
    confidenceLabel = gr ? "Χαμηλή αξιοπιστία — απαιτείται επανάληψη τεστ"
                         : "Low confidence — repeat test recommended";

logLabelValue(
        gr ? "Αξιοπιστία διάγνωσης μπαταρίας"
           : "Battery diagnostic confidence",
        String.format(
                Locale.US,
                "%.0f%% (%s)",
                measurementConfidence,
                confidenceLabel
        )
);

                startBatteryTemp = tempStart;
                endBatteryTemp = tempEnd;

                // ----------------------------------------------------
                // 15) RESULTS
                // ----------------------------------------------------
                appendHtml("<br>");
                logLine();
                logInfo(gr
                        ? "LAB 14 — Αποτέλεσμα καταπόνησης"
                        : "LAB 14 — Stress result");
                logLine();

                // fast stress summary
                if (!Float.isNaN(sagAvg[0])) {
                    logLabelValue(
                            gr ? "Γρήγορη καταπόνηση (μέσο sag)"
                               : "Fast stress (avg sag)",
                            String.format(Locale.US, "%.3f V", sagAvg[0])
                    );
                }

// sag under long load
if (!Float.isNaN(voltageStart) && !Float.isNaN(voltageUnderLoad[0])) {

    float sag = voltageStart - voltageUnderLoad[0];

    // ignore micro sag noise
    if (sag < 0.015f)
        sag = 0f;

    // BMS throttling detection
    boolean bmsThrottling =
            sag < 0.12f &&
            voltageStability[0] > 90f &&
            endBatteryTemp < 42;

    if (bmsThrottling)
        lab14_systemLimited = true;

    String sagLabel;

    if (sag < 0.05f)       sagLabel = "Excellent";
    else if (sag < 0.12f)  sagLabel = "Normal";
    else if (sag < 0.20f)  sagLabel = "Weak";
    else                   sagLabel = "Severe";

    logLabelValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            String.format(Locale.US, "%.3f V (%s)", sag, sagLabel)
    );

    // BMS throttling log
    if (lab14_systemLimited) {

        logLabelWarnValue(
                gr ? "Έλεγχος συστήματος" : "System control",
                gr
                        ? "Το σύστημα περιορίζει το ρεύμα (BMS throttling)"
                        : "BMS current throttling detected"
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// ----------------------------------------------------
// INTERNAL RESISTANCE DIAGNOSIS
// ----------------------------------------------------
if (!Float.isNaN(internalResistance[0])) {

    float r = internalResistance[0];
    float rMilli = r * 1000f;

    String label;

    if (r < 0.08f) label = "Excellent";
    else if (r < 0.15f) label = "Normal";
    else if (r < 0.25f) label = "Worn";
    else label = "Failing";

    if (r < 0.15f) {

        logLabelOkValue(
                gr ? "Εσωτερική αντίσταση μπαταρίας"
                   : "Battery internal resistance",
                String.format(Locale.US, "%.0f mΩ (%s)", rMilli, label)
        );

    } else {

        logLabelWarnValue(
                gr ? "Εσωτερική αντίσταση μπαταρίας"
                   : "Battery internal resistance",
                String.format(Locale.US, "%.0f mΩ (%s)", rMilli, label)
        );

        logLabelWarnValue(
                gr ? "Διάγνωση αντίστασης"
                   : "Resistance diagnosis",
                gr
                        ? "Υψηλή εσωτερική αντίσταση μπαταρίας."
                        : "Elevated battery internal resistance detected."
        );

        collapseRisk[0] = true;
    }

} else {

    logLabelWarnValue(
            gr ? "Εσωτερική αντίσταση μπαταρίας"
               : "Battery internal resistance",
            gr ? "Μη διαθέσιμη" : "Unavailable"
    );
}

// ----------------------------------------------------
// BATTERY ESR ESTIMATION
// ----------------------------------------------------
if (!Float.isNaN(estimatedESR)) {

    String esrLabel;

    if (estimatedESR < 0.08f)
        esrLabel = "Excellent ESR";
    else if (estimatedESR < 0.15f)
        esrLabel = "Normal ESR";
    else if (estimatedESR < 0.25f)
        esrLabel = "High ESR (aging)";
    else
        esrLabel = "Critical ESR";

    logLabelOkValue(
            gr ? "Ηλεκτροχημική αντίσταση κυψελών (ESR)"
               : "Battery ESR estimation",
            String.format(Locale.US, "%.3f Ω (%s)", estimatedESR, esrLabel)
    );

} else {

    logLabelWarnValue(
            gr ? "Ηλεκτροχημική αντίσταση κυψελών (ESR)"
               : "Battery ESR estimation",
            gr ? "Μη διαθέσιμη" : "Unavailable"
    );
}

                // voltage recovery
if (!Float.isNaN(voltageRecovery[0])) {

    String label;

    if (voltageRecovery[0] > 0.18f) label = "Excellent";
    else if (voltageRecovery[0] > 0.10f) label = "Normal";
    else if (voltageRecovery[0] > 0.05f) label = "Weak";
    else label = "Unstable";

                    logLabelValue(
                            gr ? "Ανάκαμψη τάσης μετά το φορτίο"
                               : "Voltage recovery after load",
                            String.format(
        Locale.US,
        "%.3f V (%s)",
        voltageRecovery[0],
        label
)
                    );

                } else {

                    logLabelWarnValue(
                            gr ? "Ανάκαμψη τάσης μετά το φορτίο"
                               : "Voltage recovery after load",
                            gr ? "Μη διαθέσιμο" : "Unavailable"
                    );
                }
                
// ----------------------------------------------------
// VOLTAGE RECOVERY SPEED
// ----------------------------------------------------
if (!Float.isNaN(voltageRecoverySpeed[0])) {

    String speedLabel;

    if (voltageRecoverySpeed[0] > 0.010f)
        speedLabel = "Excellent";
    else if (voltageRecoverySpeed[0] > 0.006f)
        speedLabel = "Healthy";
    else if (voltageRecoverySpeed[0] > 0.003f)
        speedLabel = "Aging";
    else
        speedLabel = "Degraded";

    logLabelValue(
            gr ? "Ταχύτητα ανάκαμψης τάσης"
               : "Voltage recovery speed",
            String.format(
                    Locale.US,
                    "%.4f V/sec (%s)",
                    voltageRecoverySpeed[0],
                    speedLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Ταχύτητα ανάκαμψης τάσης"
               : "Voltage recovery speed",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

                    // voltage stability
if (!Float.isNaN(voltageStability[0])) {

    String recLabel;

    if (voltageStability[0] >= 85f)
        recLabel = gr ? "Πολύ σταθερή τάση" : "Very stable voltage";
    else if (voltageStability[0] >= 70f)
        recLabel = gr ? "Σταθερή τάση" : "Stable voltage";
    else if (voltageStability[0] >= 50f)
        recLabel = gr ? "Μέτρια αστάθεια τάσης" : "Moderate instability";
    else
        recLabel = gr ? "Ασταθής τάση" : "Unstable voltage";

                    logLabelValue(
                            gr ? "Σταθερότητα τάσης μπαταρίας"
                               : "Battery voltage stability",
                            String.format(
        Locale.US,
        "%.0f / 100 (%s)",
        voltageStability[0],
        recLabel
)
                    );

                } else {

                    logLabelWarnValue(
                            gr ? "Σταθερότητα τάσης μπαταρίας"
                               : "Battery voltage stability",
                            gr ? "Μη διαθέσιμο" : "Unavailable"
                    );
                }
                
// ----------------------------------------------------
// CELL ELASTICITY INDEX
// ----------------------------------------------------
if (!Float.isNaN(cellElasticityIndex[0])) {

    String eLabel;

    if (cellElasticityIndex[0] >= 85)
        eLabel = "Excellent cell dynamics";
    else if (cellElasticityIndex[0] >= 70)
        eLabel = "Healthy cells";
    else if (cellElasticityIndex[0] >= 50)
        eLabel = "Moderate aging";
    else
        eLabel = "Degraded electrochemical response";

    logLabelValue(
            gr ? "Δείκτης ελαστικότητας κυψελών"
               : "Cell elasticity index",
            String.format(
                    Locale.US,
                    "%.0f / 100 (%s)",
                    cellElasticityIndex[0],
                    eLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Δείκτης ελαστικότητας κυψελών"
               : "Cell elasticity index",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// ----------------------------------------------------
// THERMAL IMPEDANCE
// ----------------------------------------------------
if (!Float.isNaN(thermalImpedance[0])) {

    String tLabel;

    if (thermalImpedance[0] < 6f)
        tLabel = "Excellent thermal response";
    else if (thermalImpedance[0] < 12f)
        tLabel = "Normal thermal response";
    else if (thermalImpedance[0] < 20f)
        tLabel = "High thermal resistance";
    else
        tLabel = "Severe thermal stress";

    logLabelValue(
            gr ? "Θερμική αντίσταση μπαταρίας"
               : "Battery thermal impedance",
            String.format(
                    Locale.US,
                    "%.1f °C/A (%s)",
                    thermalImpedance[0],
                    tLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Θερμική αντίσταση μπαταρίας"
               : "Battery thermal impedance",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// ----------------------------------------------------
// POWER STABILITY FACTOR
// ----------------------------------------------------
if (!Float.isNaN(powerStabilityFactor[0])) {

    String pLabel;

    if (powerStabilityFactor[0] >= 85)
        pLabel = "Excellent power stability";
    else if (powerStabilityFactor[0] >= 70)
        pLabel = "Stable power delivery";
    else if (powerStabilityFactor[0] >= 50)
        pLabel = "Moderate instability";
    else
        pLabel = "Power instability detected";

    logLabelValue(
            gr ? "Σταθερότητα παροχής ισχύος"
               : "Power stability factor",
            String.format(
                    Locale.US,
                    "%.0f / 100 (%s)",
                    powerStabilityFactor[0],
                    pLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Σταθερότητα παροχής ισχύος"
               : "Power stability factor",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// ----------------------------------------------------
// BATTERY STRESS SIGNATURE
// ----------------------------------------------------
if (!Float.isNaN(stressSignature[0])) {

    String sLabel;

    if (stressSignature[0] >= 85)
        sLabel = "Stable electrochemical response";
    else if (stressSignature[0] >= 70)
        sLabel = "Healthy response";
    else if (stressSignature[0] >= 50)
        sLabel = "Possible cell imbalance";
    else
        sLabel = "Irregular battery behaviour";

    logLabelValue(
            gr ? "Υπογραφή καταπόνησης μπαταρίας"
               : "Battery stress signature",
            String.format(
                    Locale.US,
                    "%.0f / 100 (%s)",
                    stressSignature[0],
                    sLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Υπογραφή καταπόνησης μπαταρίας"
               : "Battery stress signature",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// ----------------------------------------------------
// CELL IMBALANCE DETECTOR v2
// ----------------------------------------------------
boolean advancedCellImbalance = false;

if (!Float.isNaN(sag1[0]) &&
    !Float.isNaN(sag2[0]) &&
    !Float.isNaN(voltageRecovery[0])) {

    float sagDiff = Math.abs(sag1[0] - sag2[0]);

    float recoveryRatio =
            voltageRecovery[0] /
            Math.max(0.01f, Math.max(sag1[0], sag2[0]));

    // asymmetry check
    if (sagDiff > 0.045f)
        advancedCellImbalance = true;

    // slow recovery after sag
    if (recoveryRatio < 0.25f)
        advancedCellImbalance = true;
}

// ----------------------------------------------------
// RESULT
// ----------------------------------------------------
if (advancedCellImbalance) {

    logLabelWarnValue(
            gr ? "Προχωρημένη ανάλυση κυψελών"
               : "Advanced cell analysis",
            gr
                    ? "Εντοπίστηκε πιθανή ασυμμετρία λιθίου μεταξύ κυψελών"
                    : "Possible lithium cell imbalance detected"
    );

} else {

    logLabelOkValue(
            gr ? "Προχωρημένη ανάλυση κυψελών"
               : "Advanced cell analysis",
            gr
                    ? "Οι κυψέλες φαίνονται ηλεκτροχημικά ισορροπημένες"
                    : "Cells appear electrochemically balanced"
    );
}

// ----------------------------------------------------
// BATTERY STRUCTURAL INTEGRITY INDEX
// ----------------------------------------------------
if (!Float.isNaN(structuralIntegrityIndex[0])) {

    String siLabel;

    if (structuralIntegrityIndex[0] >= 85f)
        siLabel = gr ? "Εξαιρετική δομική ακεραιότητα" : "Excellent structural integrity";
    else if (structuralIntegrityIndex[0] >= 70f)
        siLabel = gr ? "Καλή δομική κατάσταση" : "Good structural condition";
    else if (structuralIntegrityIndex[0] >= 50f)
        siLabel = gr ? "Μέτρια δομική φθορά" : "Moderate structural wear";
    else
        siLabel = gr ? "Πιθανή εσωτερική υποβάθμιση" : "Possible internal degradation";

    logLabelValue(
            gr ? "Δομική ακεραιότητα μπαταρίας"
               : "Battery structural integrity",
            String.format(
                    Locale.US,
                    "%.0f / 100 (%s)",
                    structuralIntegrityIndex[0],
                    siLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Δομική ακεραιότητα μπαταρίας"
               : "Battery structural integrity",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// ----------------------------------------------------
// BATTERY STATE OF HEALTH
// ----------------------------------------------------
if (!Float.isNaN(batterySOH[0])) {

    String sohLabel;

    if (batterySOH[0] >= 90)
        sohLabel = "Excellent";
    else if (batterySOH[0] >= 80)
        sohLabel = "Healthy";
    else if (batterySOH[0] >= 70)
        sohLabel = "Moderate wear";
    else if (batterySOH[0] >= 60)
        sohLabel = "Aging";
    else
        sohLabel = "Degraded";

    logLabelValue(
            gr ? "Κατάσταση υγείας μπαταρίας"
               : "Battery state of health",
            String.format(
                    Locale.US,
                    "%.0f%% (%s)",
                    batterySOH[0],
                    sohLabel
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση υγείας μπαταρίας"
               : "Battery state of health",
            gr ? "Μη διαθέσιμο"
               : "Unavailable"
    );
}

                // collapse risk
                if (collapseRisk[0]) {
                    logLabelWarnValue(
                            gr ? "Κίνδυνος κατάρρευσης μπαταρίας"
                               : "Battery collapse risk",
                            gr
                                    ? "Υψηλός — πιθανή απότομη πτώση ποσοστού"
                                    : "High — sudden percentage drop likely"
                    );
                } else {
                    logLabelOkValue(
                            gr ? "Κίνδυνος κατάρρευσης μπαταρίας"
                               : "Battery collapse risk",
                            gr ? "Δεν εντοπίστηκε" : "Not detected"
                    );
                }

                // swelling
                if (swellingRisk[0]) {
                    logLabelWarnValue(
                            gr ? "Πιθανή διόγκωση μπαταρίας"
                               : "Possible battery swelling",
                            gr
                                    ? "Ανιχνεύθηκαν ενδείξεις εσωτερικής πίεσης κυψελών"
                                    : "Signs of internal cell pressure detected"
                    );
                } else {
                    logLabelOkValue(
                            gr ? "Έλεγχος διόγκωσης μπαταρίας"
                               : "Battery swelling check",
                            gr ? "Δεν εντοπίστηκαν ενδείξεις"
                               : "No swelling indicators detected"
                    );
                }

                // calibration drift
                if (calibrationDrift[0]) {
                    logLabelWarnValue(
                            gr ? "Απόκλιση βαθμονόμησης μπαταρίας"
                               : "Battery calibration drift",
                            String.format(
                                    Locale.US,
                                    gr
                                            ? "Απόκλιση %.1f%% μεταξύ fuel-gauge και πραγματικής χωρητικότητας"
                                            : "Deviation %.1f%% between fuel gauge and real capacity",
                                    percentDeviation[0]
                            )
                    );
                } else {
                    logLabelOkValue(
                            gr ? "Βαθμονόμηση μπαταρίας"
                               : "Battery calibration",
                            gr
                                    ? "Δεν εντοπίστηκε σημαντική απόκλιση"
                                    : "No significant calibration drift detected"
                    );
                }

                // lifespan
                if (!Float.isNaN(monthsTo70) &&
                        agingIndex >= 0 &&
                        conf.percent >= 70) {

                    logLabelValue(
                            gr ? "Εκτίμηση διάρκειας ζωής μπαταρίας"
                               : "Estimated battery lifespan",
                            String.format(
                                    Locale.US,
                                    gr
                                            ? "%.0f μήνες μέχρι ~70%% health"
                                            : "%.0f months until ~70%% health",
                                    monthsTo70
                            )
                    );

                } else {

                    logLabelWarnValue(
                            gr ? "Εκτίμηση διάρκειας ζωής μπαταρίας"
                               : "Estimated battery lifespan",
                            gr
                                    ? "Η πρόβλεψη δεν είναι αξιόπιστη (χαμηλή στατιστική συνέπεια)"
                                    : "Prediction not reliable (low measurement consistency)"
                    );
                }

                // thermal
                logLabelValue(
                        gr ? "Τελική θερμοκρασία" : "End temperature",
                        String.format(Locale.US, "%.1f°C", endBatteryTemp)
                );

                if (!Float.isNaN(startBatteryTemp) && !Float.isNaN(endBatteryTemp)) {

    float delta = endBatteryTemp - startBatteryTemp;

    if (delta >= 3.0f) {

        logLabelWarnValue(
                gr ? "Θερμική μεταβολή" : "Thermal change",
                String.format(Locale.US, "+%.1f°C", delta)
        );

    } else {

        logLabelOkValue(
                gr ? "Θερμική μεταβολή" : "Thermal change",
                String.format(Locale.US, "%.1f°C", delta)
        );

    }

} else {

    logLabelWarnValue(
            gr ? "Θερμική μεταβολή" : "Thermal change",
            gr ? "Μη διαθέσιμα δεδομένα θερμοκρασίας"
               : "Temperature data unavailable"
    );
}

                if (!Float.isNaN(voltageStart)
                        && !Float.isNaN(voltageUnderLoad[0])
                        && !Float.isNaN(startBatteryTemp)
                        && !Float.isNaN(endBatteryTemp)) {

                    float sag = voltageStart - voltageUnderLoad[0];

// ignore micro sag noise
if (sag < 0.015f)
    sag = 0f;
                    
float rise = endBatteryTemp - startBatteryTemp;

boolean highSag = sag > 0.18f;
boolean highThermalRise = rise > 6f;
boolean highResistance =
        !Float.isNaN(internalResistance[0]) &&
        internalResistance[0] > 0.22f;

if (highSag && highThermalRise && highResistance) {

    logLabelWarnValue(
            gr ? "Ένδειξη πιθανής διόγκωσης μπαταρίας"
               : "Possible battery swelling indicator",
            gr
                    ? "Υψηλή πτώση τάσης, θερμική αύξηση και αυξημένη εσωτερική αντίσταση."
                    : "High voltage sag, thermal rise and elevated internal resistance."
    );

}
                }

                // battery behaviour
                logLabelValue(
                        gr ? "Συμπεριφορά μπαταρίας" : "Battery behaviour",
                        String.format(
                                Locale.US,
                                gr
                                        ? "Έναρξη: %d mAh | Τέλος: %d mAh | Πτώση: %d mAh | Χρόνος: %.1f δευτ."
                                        : "Start: %d mAh | End: %d mAh | Drop: %d mAh | Time: %.1f sec",
                                startMah,
                                endMah,
                                Math.max(0, drainMah),
                                dtMs / 1000.0
                        )
                );

                // drain rate
                if (validDrain) {
                    logLabelOkValue(
                            gr ? "Ρυθμός αποφόρτισης" : "Drain rate",
                            String.format(
                                    Locale.US,
                                    "%.0f mAh/hour (counter-based)",
                                    mahPerHour
                            )
                    );

                    if (drainPercentPerHour > 0) {
                        logLabelValue(
                                gr ? "Κανονικοποιημένη αποφόρτιση" : "Normalized drain",
                                String.format(
                                        Locale.US,
                                        "%.1f%% / hour",
                                        drainPercentPerHour
                                )
                        );
                    }

                } else {
                    logLabelWarnValue(
                            gr ? "Ρυθμός αποφόρτισης" : "Drain rate",
                            gr
                                    ? "Μη έγκυρο (ανωμαλία counter ή μηδενική πτώση)"
                                    : "Invalid (counter anomaly or no drop)"
                    );

                    logLabelWarnValue(
                            gr ? "Σημείωση αποφόρτισης" : "Drain note",
                            gr
                                    ? "Ανιχνεύθηκε ανωμαλία fuel-gauge (PMIC / system-level). Επανέλαβε μετά από επανεκκίνηση."
                                    : "Counter anomaly detected (PMIC / system-level behavior). Repeat test after reboot."
                    );
                }
                
if (!Float.isNaN(energyEfficiency)) {

    String effLabel;

    if (energyEfficiency > 8000)
        effLabel = "High efficiency";
    else if (energyEfficiency > 5000)
        effLabel = "Normal efficiency";
    else if (energyEfficiency > 3000)
        effLabel = "Low efficiency";
    else
        effLabel = "Energy loss detected";

    logLabelValue(
            gr ? "Ενεργειακή αποδοτικότητα μπαταρίας"
               : "Battery energy efficiency",
            String.format(
                    Locale.US,
                    "%.0f mAh/V (%s)",
                    energyEfficiency,
                    effLabel
            )
    );
}

                // confidence
                logLabelOkValue(
                        gr ? "Συνέπεια μετρήσεων" : "Measurement consistency",
                        String.format(
                                Locale.US,
                                "%d%% (%d valid runs)",
                                conf.percent,
                                conf.validRuns
                        )
                );
               
               logLab14VarianceInfo();

// ----------------------------------------------------
// BATTERY AGING INDEX
// ----------------------------------------------------
if (agingIndex >= 0) {

    logLabelOkValue(
            gr ? "Δείκτης γήρανσης μπαταρίας"
               : "Battery aging index",
            String.format(
                    Locale.US,
                    "%d / 100 — %s",
                    agingIndex,
                    agingInterp
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Δείκτης γήρανσης μπαταρίας"
               : "Battery aging index",
            gr ? "Ανεπαρκή δεδομένα"
               : "Insufficient data"
    );
}

// ----------------------------------------------------
// AGING INTERPRETATION
// ----------------------------------------------------
if (aging != null && aging.description != null) {

    logLabelValue(
            gr ? "Ανάλυση γήρανσης"
               : "Aging analysis",
            aging.description
    );

} else {

    logLabelWarnValue(
            gr ? "Ανάλυση γήρανσης"
               : "Aging analysis",
            gr ? "Μη διαθέσιμη"
               : "Unavailable"
    );
}

                // final score
                String scoreText = String.format(
                        Locale.US,
                        "%d%% (%s)  •  Class %s",
                        finalScore,
                        finalLabel,
                        healthClass
                );

                if (finalScore >= 85) {
                    logLabelOkValue(
                            gr ? "Τελικός δείκτης υγείας μπαταρίας"
                               : "Final battery health score",
                            scoreText
                    );
                } else if (finalScore >= 65) {
                    logLabelWarnValue(
                            gr ? "Τελικός δείκτης υγείας μπαταρίας"
                               : "Final battery health score",
                            scoreText
                    );
                } else {
                    logLabelErrorValue(
                            gr ? "Τελικός δείκτης υγείας μπαταρίας"
                               : "Final battery health score",
                            scoreText
                    );
                }

// ----------------------------------------------------
// 16) SAVE FLAGS
// ----------------------------------------------------
p.edit()
        .putBoolean("lab14_unstable_measurement", variabilityDetected)
        .putBoolean("lab14_collapse_risk", collapseRisk[0])
        .putBoolean("lab14_swelling_risk", swellingRisk[0])
        .putBoolean("lab14_calibration_drift", calibrationDrift[0])
        .putBoolean("lab14_battery_auth_suspect", batteryAuthenticitySuspicion)
        .putFloat("lab14_health_score", finalScore)
        .putInt("lab14_aging_index", agingIndex)
        .putLong("lab14_last_ts", System.currentTimeMillis())
        .apply();

                logLabelOkValue(
        gr ? "Αποθήκευση αποτελέσματος" : "Result storage",
        gr ? "Το αποτέλεσμα αποθηκεύτηκε επιτυχώς"
           : "Result stored successfully"
);

                logLab14Confidence();

appendHtml("<br>");
logOk(gr ? "Το Lab 14 ολοκληρώθηκε." : "Lab 14 finished.");
logLine();

}   // run()

} catch (Throwable t) {

    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { stopMemoryStress(); } catch (Throwable ignore) {}
    try { stopGpuStress(); } catch (Throwable ignore) {}
    try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

        try {
            if (lab14StressVideo != null) {
                lab14StressVideo.stopPlayback();
                ViewParent parent = lab14StressVideo.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(lab14StressVideo);
                }
                lab14StressVideo = null;
            }
        } catch (Throwable ignore) {}

        try {
            if (lab14Dialog != null && lab14Dialog.isShowing())
                lab14Dialog.dismiss();
        } catch (Throwable ignore) {}
        lab14Dialog = null;

        lab14Running = false;

        String errMsg = (t != null && t.getMessage() != null)
                ? t.getMessage()
                : "Unknown runtime error";

        logLabelErrorValue(
                "LAB14_ERR_RUNTIME",
                gr
                        ? "Απροσδόκητη αποτυχία κατά την εκτέλεση"
                        : "Unexpected runtime failure"
        );

        logLabelWarnValue(
                gr ? "Τεχνική λεπτομέρεια" : "Technical detail",
                errMsg
        );

        logWarn(gr
                ? "Πιθανή αιτία: υπερθέρμανση, σφάλμα μέτρησης fuel-gauge ή περιορισμός λειτουργίας από το σύστημα."
                : "Possible cause: Thermal limit, fuel-gauge anomaly or system restriction.");
    }
}
