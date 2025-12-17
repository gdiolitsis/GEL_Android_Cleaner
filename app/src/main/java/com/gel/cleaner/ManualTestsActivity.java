// ============================================================
// ManualTestsActivity
// GEL Manual Tests — Hospital Edition (30 Manual Labs)
// Single-screen Accordion UI + detailed English service logs
// NOTE (GEL RULE): Whole file ready for copy-paste.
// IMPORTANT (Lab 11 SSID SAFE MODE):
//   Add in AndroidManifest.xml:
//     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
//     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
//     <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
//     <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
//   On Android 8.1+ / 10+ SSID requires:
//     1) Location permission granted
//     2) Location services ON (GPS/Location toggle)
//   Lab 11 will auto-send user to Location Settings if needed.
// ============================================================
package com.gel.cleaner;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.media.AudioTrack;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.ServiceState;
import java.text.SimpleDateFormat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class ManualTestsActivity extends AppCompatActivity {

    // ============================================================
    // GLOBAL FINAL SCORE FIELDS (used by Lab 29 PDF Report)
    // ============================================================
    private String lastScoreHealth      = "N/A";
    private String lastScorePerformance = "N/A";
    private String lastScoreSecurity    = "N/A";
    private String lastScorePrivacy     = "N/A";
    private String lastFinalVerdict     = "N/A";

    // ============================================================
    // CORE UI
    // ============================================================
    private ScrollView scroll;
    private TextView txtLog;
    private Handler ui;

    // ============================================================
    // SECTION STATE TRACKING (AUTO-CLOSE GROUPS)
    // ============================================================
    private final List<LinearLayout> allSectionBodies  = new ArrayList<>();
    private final List<Button>       allSectionHeaders = new ArrayList<>();

    // ============================================================
    // Battery stress internals
    // ============================================================
    private volatile boolean cpuBurnRunning = false;
    private final List<Thread> cpuBurnThreads = new ArrayList<>();
    private float oldWindowBrightness = -2f; // sentinel
    private boolean oldKeepScreenOn = false;

    // ============================================================
    // Lab 10 location permission internals
    // ============================================================
    private static final int REQ_LOCATION_LAB10 = 11012;
    private Runnable pendingLab10AfterPermission = null;

    /* =========================================================
     * FIX: APPLY SAVED LANGUAGE TO THIS ACTIVITY
     * ========================================================= */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ui = new Handler(Looper.getMainLooper());

    // ============================================================
    // ROOT SCROLL + LAYOUT
    // ============================================================
    scroll = new ScrollView(this);
    scroll.setFillViewport(true);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    int pad = dp(16);
    root.setPadding(pad, pad, pad, pad);
    root.setBackgroundColor(0xFF101010); // GEL black

    // ============================================================
    // TITLE
    // ============================================================
    TextView title = new TextView(this);
    title.setText(getString(R.string.manual_hospital_title));
    title.setTextSize(20f);
    title.setTextColor(0xFFFFD700);
    title.setGravity(Gravity.CENTER_HORIZONTAL);
    title.setPadding(0, 0, 0, dp(6));
    root.addView(title);

    // ============================================================
    // SUBTITLE
    // ============================================================
    TextView sub = new TextView(this);
    sub.setText(getString(R.string.manual_hospital_sub));
    sub.setTextSize(13f);
    sub.setTextColor(0xFF39FF14);
    sub.setGravity(Gravity.CENTER_HORIZONTAL);
    sub.setPadding(0, 0, 0, dp(12));
    root.addView(sub);

    // ============================================================
    // SECTION TITLE
    // ============================================================
    TextView sec1 = new TextView(this);
    sec1.setText(getString(R.string.manual_section1));
    sec1.setTextSize(17f);
    sec1.setTextColor(0xFFFFD700);
    sec1.setGravity(Gravity.CENTER_HORIZONTAL);
    sec1.setPadding(0, dp(10), 0, dp(6));
    root.addView(sec1);

    // ============================================================
    // SECTION 1: AUDIO & VIBRATION — LABS 1–5
    // ============================================================
    LinearLayout body1 = makeSectionBody();
    Button header1 = makeSectionHeader(getString(R.string.manual_cat_1), body1);
    root.addView(header1);
    root.addView(body1);

    body1.addView(makeTestButton("1. Speaker Tone Test", this::lab1SpeakerTone));
    body1.addView(makeTestButton("2. Speaker Frequency Sweep Test", this::lab2SpeakerSweep));
    body1.addView(makeTestButton("3. Earpiece Call Check", this::lab3EarpieceManual));
    body1.addView(makeTestButton("4. Microphone Recording Check", this::lab4MicManual));
    body1.addView(makeTestButton("5. Vibration Motor Test", this::lab5Vibration));

    // ============================================================
    // SECTION 2: DISPLAY & SENSORS — LABS 6–9
    // ============================================================
    LinearLayout body2 = makeSectionBody();
    Button header2 = makeSectionHeader(getString(R.string.manual_cat_2), body2);
    root.addView(header2);
    root.addView(body2);

    body2.addView(makeTestButton("6. Display / Touch Basic Inspection", this::lab6DisplayTouch));
    body2.addView(makeTestButton("7. Rotation / Auto-Rotate Check", this::lab7RotationManual));
    body2.addView(makeTestButton("8. Proximity During Call Test", this::lab8ProximityCall));
    body2.addView(makeTestButton("9. Sensors Check", this::lab9SensorsCheck));

    // ============================================================
    // SECTION 3: WIRELESS & CONNECTIVITY — LABS 10–13
    // ============================================================
    LinearLayout body3 = makeSectionBody();
    Button header3 = makeSectionHeader(getString(R.string.manual_cat_3), body3);
    root.addView(header3);
    root.addView(body3);

    body3.addView(makeTestButton("10. Wi-Fi Link Snapshot", this::lab10WifiSnapshot));
    body3.addView(makeTestButton("11. Mobile Data Checklist", this::lab11MobileDataChecklist));
    body3.addView(makeTestButton("12. Basic Call Test Guidelines", this::lab12CallGuidelines));
    body3.addView(makeTestButton("13. Internet Access Quick Check", this::lab13InternetQuickCheck));

    // ============================================================
    // SECTION 4: BATTERY & THERMAL — LABS 14–17
    // ============================================================
    LinearLayout body4 = makeSectionBody();
    Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);
    root.addView(header4);
    root.addView(body4);

    body4.addView(makeTestButtonRedGold("14. Battery Health Stress Test", this::lab14BatteryHealthStressTest));
    body4.addView(makeTestButton("15. Charging Port Inspection", this::lab15ChargingPortManual));
    body4.addView(makeTestButton("16. Thermal Snapshot", this::lab16ThermalSnapshot));
    body4.addView(makeTestButton("17. AUTO Battery Reliability", this::lab17RunAuto));

    // ============================================================
    // SECTION 5: STORAGE & PERFORMANCE — LABS 18–21
    // ============================================================
    LinearLayout body5 = makeSectionBody();
    Button header5 = makeSectionHeader(getString(R.string.manual_cat_5), body5);
    root.addView(header5);
    root.addView(body5);

    body5.addView(makeTestButton("18. Internal Storage Snapshot", this::lab18StorageSnapshot));
    body5.addView(makeTestButton("19. Installed Apps Footprint", this::lab19AppsFootprint));
    body5.addView(makeTestButton("20. Live RAM Snapshot", this::lab20RamSnapshot));
    body5.addView(makeTestButton("21. Uptime / Reboot Hints", this::lab21UptimeHints));

    // ============================================================
    // SECTION 6: SECURITY & SYSTEM HEALTH — LABS 22–25
    // ============================================================
    LinearLayout body6 = makeSectionBody();
    Button header6 = makeSectionHeader(getString(R.string.manual_cat_6), body6);
    root.addView(header6);
    root.addView(body6);

    body6.addView(makeTestButton("22. Screen Lock / Biometrics", this::lab22ScreenLock));
    body6.addView(makeTestButton("23. Security Patch Check", this::lab23SecurityPatchManual));
    body6.addView(makeTestButton("24. Developer Options Risk", this::lab24DevOptions));
    body6.addView(makeTestButton("25. Root / Bootloader Suspicion", this::lab25RootSuspicion));

    // ============================================================
    // SECTION 7: ADVANCED / LOGS — LABS 26–29
    // ============================================================
    LinearLayout body7 = makeSectionBody();
    Button header7 = makeSectionHeader(getString(R.string.manual_cat_7), body7);
    root.addView(header7);
    root.addView(body7);

    body7.addView(makeTestButton("26. Crash / Freeze History", this::lab26CrashHistory));
    body7.addView(makeTestButton("27. App Permissions & Privacy", this::lab27PermissionsPrivacy));
    body7.addView(makeTestButton("28. DEVICE SCORES Summary", this::lab28CombineFindings));
    body7.addView(makeTestButton("29. FINAL TECH SUMMARY", this::lab29FinalSummary));

    // ============================================================
    // LOG AREA
    // ============================================================
    txtLog = new TextView(this);
    txtLog.setTextSize(13f);
    txtLog.setTextColor(0xFFEEEEEE);
    txtLog.setPadding(0, dp(16), 0, dp(8));
    txtLog.setMovementMethod(new ScrollingMovementMethod());
    txtLog.setText(Html.fromHtml("<b>" + getString(R.string.manual_log_title) + "</b><br>"));
    root.addView(txtLog);

// ============================================================
// EXPORT SERVICE REPORT BUTTON
// ============================================================

Button btnExport = new Button(this);
btnExport.setText(getString(R.string.export_report_title));
btnExport.setAllCaps(false);
btnExport.setBackgroundResource(R.drawable.gel_btn_outline_selector);
btnExport.setTextColor(0xFFFFFFFF);

LinearLayout.LayoutParams lpExp =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpExp.setMargins(dp(4), dp(12), dp(4), dp(20));
btnExport.setLayoutParams(lpExp);

btnExport.setOnClickListener(v -> {
    Intent i = new Intent(this, ServiceReportActivity.class);
    startActivity(i);
});

root.addView(btnExport);

// ============================================================
// FINAL BIND
// ============================================================

scroll.addView(root);
setContentView(scroll);

// First log entry
GELServiceLog.clear();
logInfo(getString(R.string.manual_log_desc));
}  // onCreate ends here

// ============================================================
// GEL legacy aliases (LOCKED)
// ============================================================

private void logYellow(String msg) { logWarn(msg); }
private void logGreen(String msg)  { logOk(msg); }
private void logRed(String msg)    { logError(msg); }

private void logSection(String msg) {
    logLine();
    logInfo(msg);
}

// ============================================================
// UI HELPERS (GEL LOCKED)
// ============================================================

private LinearLayout makeSectionBody() {
    LinearLayout body = new LinearLayout(this);
    body.setOrientation(LinearLayout.VERTICAL);
    body.setVisibility(View.GONE);
    body.setPadding(0, dp(4), 0, dp(4));

    allSectionBodies.add(body);
    return body;
}

private Button makeSectionHeader(String text, LinearLayout bodyToToggle) {

    Button b = new Button(this);
    allSectionHeaders.add(b);

    b.setText(text);
    b.setAllCaps(false);
    b.setTextSize(15f);
    b.setTextColor(0xFF39FF14); // neon green
    b.setBackgroundResource(R.drawable.gel_btn_outline_selector);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
    lp.setMargins(0, dp(6), 0, dp(4));
    b.setLayoutParams(lp);
    b.setGravity(Gravity.CENTER);

    b.setOnClickListener(v -> {

        boolean willOpen = bodyToToggle.getVisibility() != View.VISIBLE;

        // close ALL sections
        for (LinearLayout body : allSectionBodies) {
            body.setVisibility(View.GONE);
        }

        if (willOpen) {
            bodyToToggle.setVisibility(View.VISIBLE);
            scroll.post(() -> scroll.smoothScrollTo(0, b.getTop()));
        }
    });

    return b;
}

private Button makeTestButton(String text, Runnable action) {
    Button b = new Button(this);
    b.setText(text);
    b.setAllCaps(false);
    b.setTextSize(14f);
    b.setTextColor(0xFFFFFFFF);
    b.setBackgroundResource(R.drawable.gel_btn_outline_selector);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(48));
    lp.setMargins(0, dp(4), 0, dp(4));
    b.setLayoutParams(lp);
    b.setGravity(Gravity.CENTER);
    b.setOnClickListener(v -> action.run());

    return b;
}

private Button makeTestButtonRedGold(String text, Runnable action) {
    Button b = new Button(this);
    b.setText(text);
    b.setAllCaps(false);
    b.setTextSize(14f);
    b.setTextColor(0xFFFFFFFF);
    b.setTypeface(null, Typeface.BOLD);

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF8B0000);
    bg.setCornerRadius(dp(12));
    bg.setStroke(dp(3), 0xFFFFD700);
    b.setBackground(bg);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52));
    lp.setMargins(0, dp(6), 0, dp(6));
    b.setLayoutParams(lp);
    b.setGravity(Gravity.CENTER);
    b.setOnClickListener(v -> action.run());

    return b;
}

// ============================================================
// LOGGING (GEL CANONICAL)
// ============================================================

private void appendHtml(String html) {
    ui.post(() -> {
        CharSequence cur = txtLog.getText();
        CharSequence add = Html.fromHtml(html + "<br>");
        txtLog.setText(TextUtils.concat(cur, add));
        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    });
}

private void logInfo(String msg) {
    GELServiceLog.info(msg);
    appendHtml("ℹ️ " + escape(msg));
}

private void logOk(String msg) {
    GELServiceLog.ok(msg);
    appendHtml("<font color='#88FF88'>✅ " + escape(msg) + "</font>");
}

private void logWarn(String msg) {
    GELServiceLog.warn(msg);
    appendHtml("<font color='#FFD966'>⚠️ " + escape(msg) + "</font>");
}

private void logError(String msg) {
    GELServiceLog.error(msg);
    appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
}

// ============================================================
// LABEL : VALUE (WHITE LABEL / GREEN VALUE)
// ============================================================

private void logLabelValue(String label, String value) {
    appendHtml(
            escape(label) + ": "
                    + "<font color='#39FF14'>" + escape(value) + "</font>"
    );
}

private void logLabelValue(String text) {
    int idx = text.indexOf(":");
    if (idx <= 0 || idx >= text.length() - 1) {
        logInfo(text);
        return;
    }

    logLabelValue(
            text.substring(0, idx).trim(),
            text.substring(idx + 1).trim()
    );
}

// ============================================================
// CORE HELPERS (SINGLE SOURCE)
// ============================================================

private void logLine() {
    GELServiceLog.addLine("----------------------------------------");
    appendHtml("<font color='#666666'>----------------------------------------</font>");
}

private String escape(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
}

private int dp(int v) {
    float d = getResources().getDisplayMetrics().density;
    return (int) (v * d + 0.5f);
}

// ============================================================
// SHARED FORMAT HELPERS
// ============================================================

private String humanBytes(long bytes) {
    if (bytes <= 0) return "0 B";
    float kb = bytes / 1024f;
    if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb);
    float mb = kb / 1024f;
    if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb);
    float gb = mb / 1024f;
    return String.format(Locale.US, "%.2f GB", gb);
}

private String formatUptime(long ms) {
    long s = ms / 1000;
    long d = s / (24 * 3600);
    s %= (24 * 3600);
    long h = s / 3600;
    s %= 3600;
    long m = s / 60;
    return String.format(Locale.US, "%dd %dh %dm", d, h, m);
}

private String cleanSsid(String raw) {
    if (raw == null) return "Unknown";
    raw = raw.trim();
    if (raw.equalsIgnoreCase("<unknown ssid>") || raw.equalsIgnoreCase("unknown ssid"))
        return "Unknown";
    if (raw.startsWith("\"") && raw.endsWith("\"") && raw.length() > 1)
        return raw.substring(1, raw.length() - 1);
    return raw;
}

private String ipToStr(int ip) {
    return (ip & 0xFF) + "." +
           ((ip >> 8) & 0xFF) + "." +
           ((ip >> 16) & 0xFF) + "." +
           ((ip >> 24) & 0xFF);
}

// ============================================================
// LABS 1–5: AUDIO & VIBRATION
// ============================================================

// ============================================================
// LAB 1 — Speaker Tone Test (AUTO)
// ============================================================
private void lab1SpeakerTone() {

    logSection("LAB 1 — Speaker Tone Test");

    new Thread(() -> {

        ToneGenerator tg = null;

        try {
            tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);
            tg.startTone(ToneGenerator.TONE_DTMF_1, 1200);
            SystemClock.sleep(1400);

            MicDiagnosticEngine.Result r =
                    MicDiagnosticEngine.run(this);

            logLabelValue("Mic RMS", String.valueOf((int) r.rms));
            logLabelValue("Mic Peak", String.valueOf((int) r.peak));
            logLabelValue("Confidence", String.valueOf(r.confidence));

            boolean hasSignal =
                    !r.silenceDetected && (r.rms > 0 || r.peak > 0);

            if (hasSignal) {

                logOk("Speaker output detected");
                logLabelValue(
                        "Note",
                        "Signal detected successfully. Low confidence may be caused by system noise cancellation or device acoustic design."
                );

            } else {

                logError("No usable speaker signal detected");
                logLabelValue(
                        "Note",
                        "Absolute silence detected. This indicates a speaker, microphone, or audio subsystem failure."
                );
            }

        } catch (Throwable t) {
            logError("Speaker tone test failed");
        } finally {
            if (tg != null) tg.release();
        }

    }).start();
}

// ============================================================
// LAB 2 — Speaker Frequency Sweep
// ============================================================
private void lab2SpeakerSweep() {

    logSection("LAB 2 — Speaker Frequency Sweep");

    new Thread(() -> {

        ToneGenerator tg = null;

        try {
            tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);

            int[] tones = {
                    ToneGenerator.TONE_DTMF_1,
                    ToneGenerator.TONE_DTMF_3,
                    ToneGenerator.TONE_DTMF_6,
                    ToneGenerator.TONE_DTMF_9
            };

            for (int t : tones) {
                tg.startTone(t, 500);
                SystemClock.sleep(550);
            }

            MicDiagnosticEngine.Result r =
                    MicDiagnosticEngine.run(this);

            logLabelValue("Mic RMS", String.valueOf((int) r.rms));
            logLabelValue("Mic Peak", String.valueOf((int) r.peak));
            logLabelValue("Confidence", String.valueOf(r.confidence));

            boolean hasSignal =
                    !r.silenceDetected && (r.rms > 0 || r.peak > 0);

            if (hasSignal) {

                logOk("Frequency sweep detected");
                logLabelValue(
                        "Note",
                        "Sweep detected successfully. Low confidence may be caused by system noise cancellation or acoustic design."
                );

            } else {

                logError("No usable frequency sweep signal detected");
                logLabelValue(
                        "Note",
                        "Absolute silence detected. This indicates a speaker or audio subsystem failure."
                );
            }

        } catch (Throwable t) {
            logError("Speaker frequency sweep failed");
        } finally {
            if (tg != null) tg.release();
        }

    }).start();
}

/* ============================================================
   LAB 3 — Earpiece Audio Path Check
   ============================================================ */
private void lab3EarpieceManual() {

    logSection("LAB 3 — Earpiece Audio Path Check");

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

            am.setMode(AudioManager.MODE_IN_COMMUNICATION);
            am.setSpeakerphoneOn(false);

            playEarpieceTestTone220Hz(900);
            SystemClock.sleep(200);

            MicDiagnosticEngine.Result r =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);

            logLabelValue("Top Mic RMS", String.valueOf((int) r.rms));
            logLabelValue("Top Mic Peak", String.valueOf((int) r.peak));
            logLabelValue("Confidence", String.valueOf(r.confidence));

            boolean hasSignal =
                    !r.silenceDetected && (r.rms > 0 || r.peak > 0);

            if (hasSignal) {

                logOk("Earpiece audio path detected");
                logLabelValue(
                        "Note",
                        "Signal detected successfully. This test simulates call audio routing. Real call confirmation is recommended."
                );

                askUserEarpieceConfirmation();

            } else {

                logError("No usable earpiece signal detected");
                logLabelValue(
                        "Note",
                        "Absolute silence detected. This indicates an earpiece or audio routing failure."
                );
                enableSingleExportButton();
            }

        } catch (Throwable t) {
            logError("LAB 3 failed");
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
   LAB 4 — Microphone Recording Check (BOTTOM + TOP)
   ============================================================ */
private void lab4MicManual() {

    logSection("LAB 4 — Microphone Recording Check (BOTTOM + TOP)");

    new Thread(() -> {

        MicDiagnosticEngine.Result bottom =
                MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.BOTTOM);

        logLabelValue("Bottom Mic RMS", String.valueOf((int) bottom.rms));
        logLabelValue("Bottom Mic Peak", String.valueOf((int) bottom.peak));
        logLabelValue("Bottom Mic Confidence", bottom.confidence);

        MicDiagnosticEngine.Result top =
                MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);

        logLabelValue("Top Mic RMS", String.valueOf((int) top.rms));
        logLabelValue("Top Mic Peak", String.valueOf((int) top.peak));
        logLabelValue("Top Mic Confidence", top.confidence);

        boolean bottomHasSignal =
                !bottom.silenceDetected && (bottom.rms > 0 || bottom.peak > 0);

        boolean topHasSignal =
                !top.silenceDetected && (top.rms > 0 || top.peak > 0);

        if (bottomHasSignal || topHasSignal) {

            logOk("Microphone signal detected");
            logLabelValue(
                    "Note",
                    "At least one microphone detected signal successfully. Low confidence may be caused by environment or noise suppression."
            );

        } else {

            logError("No usable microphone signal detected");
            logLabelValue(
                    "Note",
                    "Absolute silence detected. This indicates a microphone, permission, or audio subsystem failure."
            );
        }

        enableSingleExportButton();

    }).start();
}

/* ============================================================
   LAB 5 — Vibration Motor Test (AUTO)
   ============================================================ */
private void lab5Vibration() {

    logSection("LAB 5 — Vibration Motor Test");

    try {
        Vibrator v;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm =
                    (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            v = (vm != null) ? vm.getDefaultVibrator() : null;
        } else {
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (v == null || !v.hasVibrator()) {
            logError("No vibration motor detected");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] pattern = {0, 300, 150, 300, 150, 450};
            int[] amps = {0, 255, 0, 255, 0, 255};
            v.vibrate(VibrationEffect.createWaveform(pattern, amps, -1));
        } else {
            v.vibrate(new long[]{0, 300, 150, 300, 150, 450}, -1);
        }

        logOk("Vibration pattern executed");

    } catch (Throwable t) {
        logError("Vibration test failed");
    }
}

// ============================================================  
// LABS 6–9: DISPLAY & SENSORS  
// ============================================================  

/* ============================================================
   LAB 6 — Display / Touch Basic Inspection (manual)
   ============================================================ */

private void lab6DisplayTouch() {

    logSection("LAB 6 — Display / Touch Basic Inspection");

    startActivityForResult(
            new Intent(this, TouchGridTestActivity.class),
            6006
    );
}

/* ============================================================
   LAB 7 — Rotation / Auto-Rotate Check (manual)
   ============================================================ */

private void lab7RotationManual() {

    logSection("LAB 7 — Rotation / Auto-Rotate Check");

    startActivityForResult(
            new Intent(this, RotationCheckActivity.class),
            7007
    );
}

/* ============================================================
   LAB 8 — Proximity During Call (manual)
   ============================================================ */

private void lab8ProximityCall() {

    logSection("LAB 8 — Proximity During Call");

    startActivityForResult(
            new Intent(this, ProximityCheckActivity.class),
            8008
    );
}

/* ============================================================
   LAB 9 — Sensors Check 
   ============================================================ */

private void lab9SensorsCheck() {

    logLine();
    logInfo("LAB 9 — Sensors Presence & Full Analysis");

    try {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm == null) {
            logError("SensorManager not available — framework issue.");
            return;
        }

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        int total = (sensors == null ? 0 : sensors.size());
        logInfo("Total sensors reported: " + total);

        // ------------------------------------------------------------
        // QUICK PRESENCE CHECK (former LAB 9)
        // ------------------------------------------------------------
        checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");
        checkSensor(sm, Sensor.TYPE_GYROSCOPE, "Gyroscope");
        checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer / Compass");
        checkSensor(sm, Sensor.TYPE_LIGHT, "Ambient Light");
        checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");

        if (sensors == null || sensors.isEmpty()) {
            logError("No sensors reported by the system.");
            return;
        }

        logLine();
        logInfo("Full Sensor List:");

        // ------------------------------------------------------------
        // RAW SENSOR LIST (former LAB 10)
        // ------------------------------------------------------------
        for (Sensor s : sensors) {
            String line = "• type=" + s.getType()
                    + " | name=" + s.getName()
                    + " | vendor=" + s.getVendor();
            logInfo(line);
        }

        // ------------------------------------------------------------
        // INTERPRETATION LOGIC
        // ------------------------------------------------------------
        boolean hasVirtualGyro = false;
        boolean hasDualALS = false;
        int alsCount = 0;
        boolean hasSAR = false;
        boolean hasPickup = false;
        boolean hasLargeTouch = false;
        boolean hasGameRotation = false;

        for (Sensor s : sensors) {
            String name   = s.getName()   != null ? s.getName().toLowerCase(Locale.US)   : "";
            String vendor = s.getVendor() != null ? s.getVendor().toLowerCase(Locale.US) : "";

            if (name.contains("virtual") && name.contains("gyro"))
                hasVirtualGyro = true;

            if (name.contains("gyroscope") && vendor.contains("xiaomi"))
                hasVirtualGyro = true;

            if (name.contains("ambient") && name.contains("light"))
                alsCount++;

            if (name.contains("sar") || name.contains("rf"))
                hasSAR = true;

            if (name.contains("pickup"))
                hasPickup = true;

            if (name.contains("touch") && name.contains("large"))
                hasLargeTouch = true;

            if (name.contains("game") && name.contains("rotation"))
                hasGameRotation = true;
        }

        if (alsCount >= 2) hasDualALS = true;

        // ------------------------------------------------------------
        // SUMMARY
        // ------------------------------------------------------------
        logLine();
        logInfo("Sensor Interpretation Summary:");

        if (hasVirtualGyro)
            logOk("Detected Xiaomi Virtual Gyroscope — expected behavior (sensor fusion instead of hardware gyro).");

        if (hasDualALS)
            logOk("Dual Ambient Light Sensors detected — OK. Device uses front + rear ALS for better auto-brightness.");
        else
            logWarn("Only one Ambient Light Sensor detected — auto-brightness may be less accurate.");

        if (hasSAR)
            logOk("SAR Detectors detected — normal. Used for proximity + radio tuning (Xiaomi/QTI platforms).");

        if (hasPickup)
            logOk("Pickup Sensor detected — supports 'lift to wake' and motion awareness.");

        if (hasLargeTouch)
            logOk("Large Area Touch Sensor detected — improved palm rejection and touch accuracy.");

        if (hasGameRotation)
            logOk("Game Rotation Vector sensor detected — smoother gaming orientation response.");

        logOk("Sensor suite appears complete and healthy for this device.");

    } catch (Exception e) {
        logError("Sensors analysis error: " + e.getMessage());
    }
}

/* ============================================================
   Helper — Sensor Presence
   ============================================================ */
private void checkSensor(SensorManager sm, int type, String name) {
    boolean ok = sm.getDefaultSensor(type) != null;
    if (ok)
        logOk(name + " is reported as available.");
    else
        logWarn(name + " is NOT reported — features depending on it may be limited or missing.");
}

// ============================================================  
// LAB 10: Wi-Fi Snapshot (SAFE SSID + DeepScan) — NO PASSWORD / NO QR  
// ============================================================  
private void lab10WifiSnapshot() {  
    logLine();  
    logInfo("LAB 10 — Wi-Fi Link Snapshot + SSID Safe Mode + DeepScan (NO password).");  

    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);  
    if (wm == null) {  
        logError("WifiManager not available.");  
        return;  
    }  

    if (!wm.isWifiEnabled()) {  
        logWarn("Wi-Fi is OFF — please enable and retry.");  
        return;  
    }  

    // 1) Runtime Location Permission (required for SSID/BSSID on Android 8.1+/10+)  
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
        boolean fineGranted =  
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  
                        == PackageManager.PERMISSION_GRANTED;  
        boolean coarseGranted =  
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)  
                        == PackageManager.PERMISSION_GRANTED;  

        if (!fineGranted && !coarseGranted) {  
            logWarn("Location permission required to read SSID/BSSID (Android policy).");  
            pendingLab10AfterPermission = this::lab10WifiSnapshot;  

            ActivityCompat.requestPermissions(  
                    this,  
                    new String[]{  
                            Manifest.permission.ACCESS_FINE_LOCATION,  
                            Manifest.permission.ACCESS_COARSE_LOCATION  
                    },  
                    REQ_LOCATION_LAB10  
            );  

            logInfo("Grant permission, then Lab 10 will auto-retry.");  
            return;  
        }  

        // 2) Location services ON check  
        try {  
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);  
            boolean gpsOn = lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);  
            boolean netOn = lm != null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  

            if (!gpsOn && !netOn) {  
                logWarn("Location services are OFF. SSID may show UNKNOWN.");  
                logWarn("Opening Location Settings… enable Location and come back.");  
                try {  
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));  
                } catch (Exception ignored) {}  
                return;  
            }  
        } catch (Exception e) {  
            logWarn("Location services check failed: " + e.getMessage());  
        }  
    }  

    // 3) Read basic WifiInfo  
    WifiInfo info = wm.getConnectionInfo();  
    if (info == null) {  
        logError("Wi-Fi info not available.");  
        return;  
    }  

    String ssid = cleanSsid(info.getSSID());  
    String bssid = info.getBSSID();  
    int rssi  = info.getRssi();  
    int speed = info.getLinkSpeed();  
    int freqMhz = 0;  
    try { freqMhz = info.getFrequency(); } catch (Throwable ignored) {}  
    String band = (freqMhz > 3000) ? "5 GHz" : "2.4 GHz";  

    logInfo("SSID: " + ssid);  
    if (bssid != null) logInfo("BSSID: " + bssid);  
    if (freqMhz > 0) logInfo("Band: " + band + " (" + freqMhz + " MHz)");  
    else logInfo("Band: " + band);  

    logInfo("Link speed: " + speed + " Mbps");  
    logInfo("RSSI: " + rssi + " dBm");  

    if ("Unknown".equalsIgnoreCase(ssid)) {  
        logWarn("SSID is UNKNOWN due to Android privacy policy.");  
        logWarn("Fix: grant Location permission + turn Location ON, then re-run Lab 11.");  
    } else {  
        logOk("SSID read OK.");  
    }  

    if (rssi > -65)  
        logOk("Wi-Fi signal is strong.");  
    else if (rssi > -80)  
        logWarn("Moderate Wi-Fi signal.");  
    else  
        logError("Very weak Wi-Fi signal — expect drops.");  

    // 4) DHCP / IP details  
    try {  
        DhcpInfo dh = wm.getDhcpInfo();  
        if (dh != null) {  
            logInfo("IP: " + ipToStr(dh.ipAddress));  
            logInfo("Gateway: " + ipToStr(dh.gateway));  
            logInfo("DNS1: " + ipToStr(dh.dns1));  
            logInfo("DNS2: " + ipToStr(dh.dns2));  
        } else {  
            logWarn("DHCP info not available.");  
        }  
    } catch (Exception e) {  
        logWarn("DHCP read failed: " + e.getMessage());  
    }  

    // 5) DeepScan  
    runWifiDeepScan(wm);  
}  

@Override  
public void onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults) {  
    super.onRequestPermissionsResult(requestCode, perms, grantResults);  

    if (requestCode == REQ_LOCATION_LAB10) {  
        boolean granted = false;  
        if (grantResults != null) {  
            for (int r : grantResults) {  
                if (r == PackageManager.PERMISSION_GRANTED) {  
                    granted = true;  
                    break;  
                }  
            }  
        }  

        if (granted) {  
            logOk("Location permission granted.");  
            if (pendingLab10AfterPermission != null) pendingLab10AfterPermission.run();  
        } else {  
            logWarn("Location permission denied. SSID/BSSID may remain UNKNOWN.");  
        }  
        pendingLab10AfterPermission = null;  
    }  
}  

// ============================================================  
// LAB 10 — DEEPSCAN v3.0  
// ============================================================  
private void runWifiDeepScan(WifiManager wm) {  
    new Thread(() -> {  
        try {  
            logLine();  
            logInfo("GEL Network DeepScan v3.0 started...");  

            String gatewayStr = null;  
            try {  
                DhcpInfo dh = wm.getDhcpInfo();  
                if (dh != null) gatewayStr = ipToStr(dh.gateway);  
            } catch (Exception ignored) {}  

            // 1) Ping latency to 8.8.8.8 using TCP connect (works non-root)  
            float pingMs = tcpLatencyMs("8.8.8.8", 53, 1500);  
            if (pingMs > 0)  
                logOk(String.format(Locale.US, "Ping latency to 8.8.8.8: %.1f ms", pingMs));  
            else  
                logWarn("Ping latency test failed (network blocked).");  

            // 2) DNS resolve time  
            float dnsMs = dnsResolveMs("google.com");  
            if (dnsMs > 0)  
                logOk(String.format(Locale.US, "DNS resolve google.com: %.0f ms", dnsMs));  
            else  
                logWarn("DNS resolve failed.");  

            // 3) Gateway ping (TCP to 80)  
            if (gatewayStr != null) {  
                float gwMs = tcpLatencyMs(gatewayStr, 80, 1200);  
                if (gwMs > 0)  
                    logOk(String.format(Locale.US, "Gateway ping (%s): %.1f ms", gatewayStr, gwMs));  
                else  
                    logWarn("Gateway ping failed.");  
            } else {  
                logWarn("Gateway not detected.");  
            }  

            // 4) SpeedSim heuristic  
            WifiInfo info = wm.getConnectionInfo();  
            int link = info != null ? info.getLinkSpeed() : 0;  
            int rssi = info != null ? info.getRssi() : -80;  
            float speedSim = estimateSpeedSimMbps(link, rssi);  
            logOk(String.format(Locale.US, "SpeedSim: ~%.2f Mbps (heuristic)", speedSim));  

            logOk("DeepScan finished.");  

        } catch (Exception e) {  
            logError("DeepScan error: " + e.getMessage());  
        }  
    }).start();  
}  

private float tcpLatencyMs(String host, int port, int timeoutMs) {  
    long t0 = SystemClock.elapsedRealtime();  
    Socket s = new Socket();  
    try {  
        s.connect(new InetSocketAddress(host, port), timeoutMs);  
        long t1 = SystemClock.elapsedRealtime();  
        return (t1 - t0);  
    } catch (Exception e) {  
        return -1f;  
    } finally {  
        try { s.close(); } catch (Exception ignored) {}  
    }  
}  

private float dnsResolveMs(String host) {  
    long t0 = SystemClock.elapsedRealtime();  
    try {  
        InetAddress.getByName(host);  
        long t1 = SystemClock.elapsedRealtime();  
        return (t1 - t0);  
    } catch (Exception e) {  
        return -1f;  
    }  
}  

private float estimateSpeedSimMbps(int linkSpeedMbps, int rssiDbm) {  
    if (linkSpeedMbps <= 0) linkSpeedMbps = 72;  
    float rssiFactor;  
    if (rssiDbm > -55) rssiFactor = 1.2f;  
    else if (rssiDbm > -65) rssiFactor = 1.0f;  
    else if (rssiDbm > -75) rssiFactor = 0.7f;  
    else rssiFactor = 0.4f;  
    return Math.max(5f, linkSpeedMbps * rssiFactor);  
}  

private void lab11MobileDataChecklist() {  
    logLine();  
    logInfo("LAB 11 — Mobile Data / Airplane Mode Checklist + SIM detection.");  

    try {  
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);  
        if (tm == null) {  
            logWarn("TelephonyManager not available — cannot detect SIM.");  
        } else {  
            int simState = tm.getSimState();  
            switch (simState) {  
                case TelephonyManager.SIM_STATE_READY:  
                    logOk("SIM detected and READY.");  
                    break;  
                case TelephonyManager.SIM_STATE_ABSENT:  
                    logError("NO SIM detected (SIM_STATE_ABSENT).");  
                    break;  
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:  
                    logWarn("SIM detected but locked (PIN required).");  
                    break;  
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:  
                    logWarn("SIM detected but locked (PUK required).");  
                    break;  
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:  
                    logWarn("SIM detected but network-locked.");  
                    break;  
                case TelephonyManager.SIM_STATE_NOT_READY:  
                    logWarn("SIM present but not ready yet.");  
                    break;  
                default:  
                    logWarn("SIM state unknown: " + simState);  
                    break;  
            }  

            // Service state (best-effort, may be blocked on some OEMs)  
            try {  
                ServiceState ss = tm.getServiceState();  
                if (ss != null) {  
                    int state = ss.getState();  
                    if (state == ServiceState.STATE_IN_SERVICE) {  
                        logOk("Mobile service: IN SERVICE.");  
                    } else if (state == ServiceState.STATE_OUT_OF_SERVICE) {  
                        logWarn("Mobile service: OUT OF SERVICE.");  
                    } else if (state == ServiceState.STATE_EMERGENCY_ONLY) {  
                        logWarn("Mobile service: EMERGENCY ONLY.");  
                    } else if (state == ServiceState.STATE_POWER_OFF) {  
                        logWarn("Mobile service: RADIO OFF / AIRPLANE?");  
                    } else {  
                        logWarn("Mobile service state: " + state);  
                    }  
                } else {  
                    logWarn("ServiceState not available.");  
                }  
            } catch (SecurityException se) {  
                logWarn("ServiceState blocked by OS/OEM (no permission).");  
            } catch (Exception ignored) {}  
        }  

    } catch (Exception e) {  
        logWarn("SIM detection error: " + e.getMessage());  
    }  

    logInfo("1) Check that Airplane mode is OFF and mobile data is enabled.");  
    logInfo("2) Ensure a valid SIM with active data plan is inserted.");  
    logWarn("If the device shows signal bars but mobile data never works -> APN/carrier/modem issue.");  
    logError("If there is no mobile network at all in known-good coverage -> SIM, antenna or baseband problem.");  
}  

private void lab12CallGuidelines() {  
    logLine();  
    logInfo("LAB 12 — Basic Call Test Guidelines (manual).");  
    logInfo("1) Place a normal call to a known-good number.");  
    logInfo("2) Verify both directions: you hear them AND they hear you clearly.");  
    logWarn("If only one direction fails -> isolate earpiece vs microphone path.");  
    logError("If calls always drop or never connect while data works -> telephony/carrier registration issue.");  
}  

private void lab13InternetQuickCheck() {  
    logLine();  
    logInfo("LAB 13 — Internet Access Quick Check.");  
    try {  
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);  
        if (cm == null) {  
            logError("ConnectivityManager not available.");  
            return;  
        }  

        boolean hasInternet = false;  
        String transport = "UNKNOWN";  

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  
            android.net.Network n = cm.getActiveNetwork();  
            NetworkCapabilities caps = cm.getNetworkCapabilities(n);  
            if (caps != null) {  
                hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);  
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))  
                    transport = "Wi-Fi";  
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))  
                    transport = "Cellular";  
            }  
        } else {  
            @SuppressWarnings("deprecation")  
            NetworkInfo ni = cm.getActiveNetworkInfo();  
            if (ni != null && ni.isConnected()) {  
                hasInternet = true;  
                transport = ni.getTypeName();  
            }  
        }  

        if (!hasInternet)  
            logError("No active Internet connection detected at OS level.");  
        else  
            logOk("Internet connectivity is reported as active (" + transport + ").");  

    } catch (Exception e) {  
        logError("Internet quick check error: " + e.getMessage());  
    }  
}  

// ============================================================
// GEL THERMAL ENGINE — UNIVERSAL AUTO-SCALE (FINAL)
// Compatible with all Android devices (Pixel, Samsung, Xiaomi, POCO, Huawei,
// OnePlus, Oppo, Vivo, Realme, Motorola, Infinix, Tecno, MTK, Snapdragon).
// ============================================================

// ------------------------------
// READ ALL THERMAL ZONES
// ------------------------------
private Map<String, Float> readThermalZones() {
    Map<String, Float> out = new HashMap<>();
    File base = new File("/sys/class/thermal");
    File[] zones = base.listFiles();
    if (zones == null) return out;

    for (File f : zones) {
        if (f == null) continue;
        String name = f.getName();
        if (!name.startsWith("thermal_zone")) continue;

        File typeFile = new File(f, "type");
        File tempFile = new File(f, "temp");
        if (!tempFile.exists()) continue;

        try {
            // Read type
            String type = name;
            if (typeFile.exists()) {
                String t = readFirstLine(typeFile);
                if (t != null && !t.trim().isEmpty())
                    type = t.trim();
            }

            // Read raw temperature
            String tRaw = readFirstLine(tempFile);
            if (tRaw == null) continue;
            float v = Float.parseFloat(tRaw.trim());

            // -------------------------------------------
            // AUTO-SCALE (handles every Android variant)
            // -------------------------------------------
            if (v > 1000f)       v = v / 1000f;  // millidegree
            else if (v > 200f)  v = v / 100f;   // centidegree
            else if (v > 20f)   v = v / 10f;    // deci-degree
            // else already °C

            out.put(type.toLowerCase(Locale.US), v);

        } catch (Throwable ignore) {}
    }

    return out;
}


// ------------------------------
// PICK the correct zone by keywords
// ------------------------------
private Float pickZone(Map<String, Float> zones, String... keys) {
    if (zones == null || zones.isEmpty()) return null;
    if (keys == null || keys.length == 0) return null;

    for (Map.Entry<String, Float> e : zones.entrySet()) {
        String z = e.getKey().toLowerCase(Locale.US);
        for (String k : keys) {
            if (k == null) continue;
            String kk = k.toLowerCase(Locale.US);
            if (z.equals(kk) || z.contains(kk))
                return e.getValue();
        }
    }
    return null;
}


// ------------------------------
// READ FIRST LINE
// ------------------------------
private String readFirstLine(File file) {
    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(file));
        return br.readLine();
    } catch (Exception e) {
        return null;
    } finally {
        try { if (br != null) br.close(); } catch (Exception ignore) {}
    }
}
    
// ============================================================
// LAB 14 — Battery Health Stress Test (GEL Full Mode)
// ============================================================
private void lab14BatteryHealthStressTest() {

    float pct = getCurrentBatteryPercent();
    if (pct < 0f) {
        logError("Unable to read battery level.");
        return;
    }

    // BLOCK TEST IF BATTERY < 50%
    if (pct < 50f) {
        logLine();
        logError("Battery level too low (<50%). Please charge the battery to run a reliable stress test.");
        return;
    }

    showBatteryHealthTestDialog();
}

private void showBatteryHealthTestDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(true);
    builder.setTitle("Battery Health Stress Test");

    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    int pad = dp(16);
    layout.setPadding(pad, pad, pad, pad);

    TextView info = new TextView(this);
    info.setText("GEL Stress Test burns CPU + max brightness and checks real battery % drop.\nSelect duration then start.");
    info.setTextSize(13f);
    info.setTextColor(0xFFFFFFFF);
    info.setPadding(0, 0, 0, dp(8));
    layout.addView(info);

    TextView durLabel = new TextView(this);
    durLabel.setText("Duration (minutes):");
    durLabel.setTextSize(13f);
    durLabel.setTextColor(0xFFFFD700);
    durLabel.setPadding(0, dp(8), 0, 0);
    layout.addView(durLabel);

    final TextView durValue = new TextView(this);
    durValue.setTextSize(13f);
    durValue.setTextColor(0xFF39FF14);
    layout.addView(durValue);

    final SeekBar seek = new SeekBar(this);
    seek.setMax(4); // 1–5 minutes
    layout.addView(seek);

    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
            int minutes = 1 + progress;
            durValue.setText("Selected: " + minutes + " min (1–5 min)");
        }
        @Override public void onStartTrackingTouch(SeekBar sb) {}
        @Override public void onStopTrackingTouch(SeekBar sb) {}
    });

    seek.setProgress(0);
    durValue.setText("Selected: 1 min (1–5 min)");

    Button start = new Button(this);
    start.setText("Start Stress Test (GEL Full Mode)");
    start.setAllCaps(false);
    start.setTextSize(15f);
    start.setTextColor(0xFFFFFFFF);
    start.setTypeface(null, Typeface.BOLD);

    GradientDrawable redBtn = new GradientDrawable();
    redBtn.setColor(0xFF8B0000);
    redBtn.setCornerRadius(dp(12));
    redBtn.setStroke(dp(3), 0xFFFFD700);
    start.setBackground(redBtn);

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(48));
    lp.setMargins(0, dp(12), 0, 0);
    start.setLayoutParams(lp);

    layout.addView(start);

    builder.setView(layout);
    AlertDialog dialog = builder.create();

    if (dialog.getWindow() != null)
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xFF000000));

    start.setOnClickListener(v -> {
        int durationMin = 1 + seek.getProgress();
        int durationSec = durationMin * 60;
        dialog.dismiss();
        runBatteryHealthTest_C_Mode(durationSec);
    });

    dialog.show();
}

private void runBatteryHealthTest_C_Mode(int durationSec) {

    float startPct = getCurrentBatteryPercent();
    if (startPct < 0f) {
        logWarn("Battery Stress Test: unable to read initial battery level.");
        return;
    }

    // ---- READ THERMALS BEFORE ----
    Map<String,Float> z0 = readThermalZones();
    Float cpu0  = pickZone(z0,"cpu","soc","big","little");
    Float gpu0  = pickZone(z0,"gpu");
    Float skin0 = pickZone(z0,"skin","pa_therm");
    Float pmic0 = pickZone(z0,"pmic","pmic_therm");
    Float batt0 = pickZone(z0,"battery","batt","bat");

    logLine();
    logInfo("LAB 14 — Battery Health Stress Test started.");
    logInfo("Mode: GEL Full Mode (CPU burn + MAX brightness).");
    logInfo("Duration: " + durationSec + " seconds.");

    long startTime = SystemClock.elapsedRealtime();

    applyMaxBrightnessAndKeepOn();
    startCpuBurn_C_Mode();

    ui.postDelayed(() -> {

        stopCpuBurn();
        restoreBrightnessAndKeepOn();

        float endPct = getCurrentBatteryPercent();
        if (endPct < 0f) {
            logWarn("Battery Stress Test: unable to read final battery level.");
            return;
        }

        long endTime = SystemClock.elapsedRealtime();
        long dtMs = endTime - startTime;
        if (dtMs <= 0) dtMs = durationSec * 1000L;

        float delta = startPct - endPct;
        float perHour = (delta * 3600000f) / dtMs;

        logInfo(String.format(Locale.US,
                "Stress result: start=%.1f%%, end=%.1f%%, drop=%.2f%% over %.1f sec.",
                startPct, endPct, delta, dtMs / 1000f));

        // ---- READ THERMALS AFTER ----
        Map<String,Float> z1 = readThermalZones();
        Float cpu1  = pickZone(z1,"cpu","soc","big","little");
        Float gpu1  = pickZone(z1,"gpu");
        Float skin1 = pickZone(z1,"skin","pa_therm");
        Float pmic1 = pickZone(z1,"pmic","pmic_therm");
        Float batt1 = pickZone(z1,"battery","batt","bat");

        float dCPU  = (cpu1  != null && cpu0  != null) ? cpu1  - cpu0  : 0f;
        float dGPU  = (gpu1  != null && gpu0  != null) ? gpu1  - gpu0  : 0f;
        float dSKIN = (skin1 != null && skin0 != null) ? skin1 - skin0 : 0f;
        float dPMIC = (pmic1 != null && pmic0 != null) ? pmic1 - pmic0 : 0f;
        float dBATT = (batt1 != null && batt0 != null) ? batt1 - batt0 : 0f;

        logInfo(String.format(Locale.US,
                "Thermal change during stress: CPU=%.1f°C, GPU=%.1f°C, SKIN=%.1f°C, PMIC=%.1f°C, BATT=%.1f°C.",
                dCPU, dGPU, dSKIN, dPMIC, dBATT));

        // ---- DRAIN BEHAVIOR ----
        if (delta <= 0.1f) {
            logOk("Almost zero drain in stress window — battery behavior looks strong.");
        } else if (perHour <= 12f) {
            logOk(String.format(Locale.US,
                    "Estimated drain ≈ %.1f%%/hour under stress — strong.", perHour));
        } else if (perHour <= 20f) {
            logWarn(String.format(Locale.US,
                    "Estimated drain ≈ %.1f%%/hour under stress — borderline.", perHour));
        } else {
            logError(String.format(Locale.US,
                    "Estimated drain ≈ %.1f%%/hour under stress — heavy wear.", perHour));
        }

        // ---- HEALTH CATEGORY (CHECKBOX MAP) ----
        String health;

        if (perHour <= 6f)      health = "Strong";       // NEW highest level  
        else if (perHour <= 8f) health = "Excellent";
        else if (perHour <=12f) health = "Very good";
        else if (perHour <=20f) health = "Normal";
        else                    health = "Weak";

        printHealthCheckboxMap(health);

    }, durationSec * 1000L);
}

// ============================================================
// CHECKBOX MAP
// ============================================================
private void printHealthCheckboxMap(String health) {

    String neon = "#39FF14";
    String white = "#FFFFFF";

    boolean strong   = health.equals("Strong");
    boolean excel    = health.equals("Excellent");
    boolean verygood = health.equals("Very good");
    boolean normal   = health.equals("Normal");
    boolean weak     = health.equals("Weak");

    logRaw( cb("Strong",    strong,   neon, white) );
    logRaw( cb("Excellent", excel,    neon, white) );
    logRaw( cb("Very good", verygood, neon, white) );
    logRaw( cb("Normal",    normal,   neon, white) );
    logRaw( cb("Weak",      weak,     neon, white) );
}

private String cb(String label, boolean active, String neon, String white) {
    if (active)
        return "✔ " + color(label, neon);
    else
        return "☐ " + color(label, white);
}

// ============================================================
// HELPERS (ίδιοι όπως πριν, για να μη λείπει τίποτα)
// ============================================================
private void applyMaxBrightnessAndKeepOn() {
    try {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        oldWindowBrightness = lp.screenBrightness;
        oldKeepScreenOn = (getWindow().getAttributes().flags &
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0;

        lp.screenBrightness = 1f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        logInfo("Stress: brightness set to MAX and screen locked ON.");
    } catch (Exception e) {
        logWarn("Stress: brightness/keep-on failed: " + e.getMessage());
    }
}

private void restoreBrightnessAndKeepOn() {
    try {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (oldWindowBrightness != -2f) {
            lp.screenBrightness = oldWindowBrightness;
            getWindow().setAttributes(lp);
        }

        if (!oldKeepScreenOn) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        logInfo("Stress: brightness and screen flags restored.");
    } catch (Exception e) {
        logWarn("Stress: restore brightness failed: " + e.getMessage());
    }
}

private void startCpuBurn_C_Mode() {
    stopCpuBurn();
    cpuBurnRunning = true;

    int cores = Runtime.getRuntime().availableProcessors();
    int threadsToRun = Math.min(8, cores);
    logInfo("Stress: starting CPU burn threads: " + threadsToRun + " (cores=" + cores + ").");

    cpuBurnThreads.clear();
    for (int i = 0; i < threadsToRun; i++) {
        Thread t = new Thread(() -> {
            double x = 1.000001;
            while (cpuBurnRunning) {
                x = x * 1.0000001 + Math.sqrt(x) + Math.sin(x) + Math.cos(x);
                if (x > 1e9) x = 1.000001;
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
        cpuBurnThreads.add(t);
    }
}

private void stopCpuBurn() {
    cpuBurnRunning = false;
    for (Thread t : cpuBurnThreads) {
        try { 
            t.join(50); 
        } catch (Exception ignored) {}
    }
    cpuBurnThreads.clear();
    logInfo("Stress: CPU burn stopped.");
}

private float getCurrentBatteryPercent() {
    try {
        IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent i = registerReceiver(null, f);
        if (i == null) return -1f;

        int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level < 0 || scale <= 0) return -1f;

        return 100f * level / scale;

    } catch (Exception e) {
        return -1f;
    }
}

// ============================================================
// LOG RAW + COLOR HELPERS (ΑΠΑΡΑΙΤΗΤΟΙ ΓΙΑ CHECKBOXES)
// ============================================================

private void logRaw(String s) {
    // raw HTML output inside log (χωρίς prefix)
    logInfo(s);
}

private String color(String text, String hex) {
    // επιτρέπει neon/white χρώματα στα labels των checkboxes
    return "<font color='" + hex + "'>" + text + "</font>";
}
        
//=============================================================
// LAB 15 — Charging Port & Charger Inspection (manual)  
// ============================================================  
private void lab15ChargingPortManual() {  
    logLine();  
    logInfo("LAB 15 — Charging Port & Charger Inspection (manual).");  
    logInfo("1) Inspect the charging port with a flashlight for dust, lint or corrosion.");  
    logWarn("If the cable fits loosely or disconnects easily -> worn port or bent contacts.");  
    logError("If the device does not charge with known-good chargers -> possible port or board-level power issue.");  
}  

// ============================================================

// GEL Battery Temperature Reader (Universal)
// ============================================================
private float getBatteryTemperature() {
try {
Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
if (i == null) return 0f;

int raw = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);  
    if (raw <= 0) return 0f;  

    return raw / 10f;  // Android gives tenths of °C  
} catch (Exception e) {  
    return 0f;  
}

}

// ============================================================

// LAB 16 — Thermal Snapshot (GEL Universal Edition — SAFE)
// ============================================================
private void lab16ThermalSnapshot() {
logLine();
logInfo("LAB 16 — Thermal Snapshot (ASCII thermal map)");

// 1) Read thermal zones (CPU/GPU/PMIC/Skin)  
Map<String, Float> zones = readThermalZones();  

// 2) Battery ALWAYS from BatteryManager  
float batt = getBatteryTemperature();  

if (zones == null || zones.isEmpty()) {  
    logWarn("Device exposes NO thermal zones. Printing battery only.");  
    printZoneAscii("Battery", batt);  
    logOk("Lab 16 finished.");  
    return;  
}  

// Auto-detect CPU/GPU/SKIN/PMIC  
Float cpu  = pickZone(zones, "cpu", "cpu-therm", "big", "little", "tsens", "mtktscpu");  
Float gpu  = pickZone(zones, "gpu", "gpu-therm", "gpuss", "mtkgpu");  
Float skin = pickZone(zones, "skin", "xo-therm", "shell", "surface");  
Float pmic = pickZone(zones, "pmic", "pmic-therm", "power-thermal", "charger", "chg");  

logOk("Thermal Zones found: " + zones.size());  

// Snapshot print  
if (cpu  != null) printZoneAscii("CPU", cpu);  
if (gpu  != null) printZoneAscii("GPU", gpu);  

// Battery ALWAYS printed  
printZoneAscii("Battery", batt);  

if (skin != null) printZoneAscii("Skin", skin);  
if (pmic != null) printZoneAscii("PMIC", pmic);  

logOk("Lab 16 finished.");

}

// ============================================================
// ASCII BAR (100 chars — 5sp monospace via HTML)
// ============================================================
private void printZoneAscii(String label, float t) {

// Color icon  
String color;  
if (t < 45)        color = "🟩";  
else if (t < 60)   color = "🟨";  
else               color = "🟥";  

// Temperature scale → 0–100 chars  
float maxT = 80f;  
float pct = Math.min(1f, t / maxT);  
int bars = (int)(pct * 100);  

StringBuilder sb = new StringBuilder(100);  
for (int i = 0; i < bars; i++) sb.append("█");  
while (sb.length() < 100) sb.append(" ");  

// Header line  
logInfo(label + ": " + color + " " + String.format(Locale.US, "%.1f°C", t));  

// BAR line (5sp monospace via <small><small><tt>>)  
appendHtml("<small><small><tt>" + escape(sb.toString()) + "</tt></small></small>");

}

// ============================================================
// LAB 17 — GEL AUTO Battery Reliability Evaluation
// Premium Diagnostic Edition (FINAL FULL BLOCK)
// ============================================================

private void lab17RunAuto() {

logLine();  
logInfo("17. GEL Auto Battery Reliability Evaluation");  
logInfo("GEL Battery Reliability Evaluation started.");  
logLine();  

new Thread(() -> {  

    try {  

        // ============================================================  
        // 1. STRESS TEST (LAB 14 CORE)  
        // ============================================================  
        float startPct = getCurrentBatteryPercent();  
        if (startPct < 50f) {  
            ui.post(() -> logError("Battery <50%. Please charge to run automatic evaluation."));  
            return;  
        }  

        ui.post(() -> {  
            logInfo("▶ Running Stress Test (Lab 15)...");  
            logInfo("[██████████░░░░░░░░░░░░░░░░░░] 32%");  
        });  

        float before = getCurrentBatteryPercent();  
        long t0 = SystemClock.elapsedRealtime();  

        ui.post(this::applyMaxBrightnessAndKeepOn);  
        startCpuBurn_C_Mode();  

        Thread.sleep(60_000);   // Stress load on background thread  

        stopCpuBurn();  
        ui.post(this::restoreBrightnessAndKeepOn);  

        float after = getCurrentBatteryPercent();  
        long t1 = SystemClock.elapsedRealtime();  

        float drop   = before - after;  
        float perHour = (drop * 3600000f) / (t1 - t0);  
        if (perHour < 0f) perHour = 0f;  
        int drain_mA = (int)(perHour * 50);  
        if (drain_mA < 0) drain_mA = 0;  

        ui.post(() -> {  
            logInfo("▶ Calculating drain rate...");  
            logInfo("[███████████████████████░░░░] 85%");  
        });  

        // ============================================================  
        // 2. THERMAL ZONES (LAB 16 STYLE)  
        // ============================================================  
        ui.post(() -> {  
            logInfo("");  
            logInfo("▶ Running Thermal Zones (Lab 16)...");  
            logInfo("[██████████████████░░░░░░░░░░] 68%");  
        });  

        Map<String,Float> z0 = readThermalZones();  
        Thread.sleep(1500);  
        Map<String,Float> z1 = readThermalZones();  

        Float cpu0  = pickZone(z0,"cpu","soc","big","little");  
        Float cpu1  = pickZone(z1,"cpu","soc","big","little");  
        Float batt0 = pickZone(z0,"battery","batt","bat");  
        Float batt1 = pickZone(z1,"battery","batt","bat");  

        float dCPU  = (cpu0  != null && cpu1  != null) ? (cpu1  - cpu0)  : 0f;  
        float dBATT = (batt0 != null && batt1 != null) ? (batt1 - batt0) : 0f;  

        // ============================================================  
        // 3. VOLTAGE STABILITY  
        // ============================================================  
        ui.post(() -> logInfo("▶ Calculating voltage stability..."));  
        float v0 = getBatteryVoltage_mV();  
        Thread.sleep(1500);  
        float v1 = getBatteryVoltage_mV();  
        float dv = Math.abs(v1 - v0);  

        // ============================================================  
        // 4. CAPACITY ESTIMATION + PMIC  
        // ============================================================  
        ui.post(() -> {  
            logInfo("▶ Calculating thermal rise...");  
            logInfo("▶ Calculating PMIC behavior...");  
            logInfo("▶ Calculating discharge curve...");  
            logInfo("▶ Calculating estimated real capacity...");  
            logInfo("▶ Getting device information...");  
        });  

        int factory = getFactoryCapacity_mAh();  
        if (factory <= 0) factory = 5000;  

        float estimatedCapacity_mAh =  
                factory * (100f / (100f + perHour));  
        if (estimatedCapacity_mAh > factory)  
            estimatedCapacity_mAh = factory;  

        // ============================================================  
        // 5. SCORING ENGINE  
        // ============================================================  
        int score = 100;  

        // drain penalty  
        if (perHour > 20f)       score -= 30;  
        else if (perHour > 15f)  score -= 20;  
        else if (perHour > 10f)  score -= 10;  

        // thermal penalty (CPU)  
        if (dCPU > 25f)          score -= 25;  
        else if (dCPU > 15f)     score -= 15;  
        else if (dCPU > 10f)     score -= 8;  

        // thermal penalty (BATT)  
        if (dBATT > 10f)         score -= 20;  
        else if (dBATT > 5f)     score -= 10;  

        // voltage penalty  
        if (dv > 45f)            score -= 20;  
        else if (dv > 30f)       score -= 10;  
        else if (dv > 20f)       score -= 5;  

        // capacity penalty  
        float pctHealth = (estimatedCapacity_mAh / factory) * 100f;  
        if (pctHealth < 60f)     score -= 25;  
        else if (pctHealth < 70f)score -= 15;  
        else if (pctHealth < 80f)score -= 8;  

        if (score < 0)   score = 0;  
        if (score > 100) score = 100;  

        // voltage label  
        String voltageLabel;  
        if (dv <= 15f)       voltageLabel = "Excellent";  
        else if (dv <= 30f)  voltageLabel = "OK";  
        else                 voltageLabel = "Unstable";  

        // thermal label  
        String thermalLabel;  
        if (dCPU <= 10f && dBATT <= 5f)  
            thermalLabel = "OK";  
        else if (dCPU <= 18f && dBATT <= 8f)  
            thermalLabel = "Warm";  
        else  
            thermalLabel = "Hot";  

        // cycle behaviour  
        String cycleLabel;  
        if (perHour < 10f && dv < 20f)  
            cycleLabel = "Strong (stable discharge curve)";  
        else if (perHour < 15f)  
            cycleLabel = "Normal (minor fluctuations)";  
        else  
            cycleLabel = "Stressed (irregular discharge curve)";  

        // category  
        String category;  
        if (score >= 90)      category = "Strong";  
        else if (score >= 80) category = "Excellent";  
        else if (score >= 70) category = "Very good";  
        else if (score >= 60) category = "Normal";  
        else                  category = "Weak";  

        // ============================================================  
        // 6. FINAL UI OUTPUT  
        // ============================================================  
        final float f_before   = before;  
        final float f_after    = after;  
        final float f_drop     = drop;  
        final float f_perHour  = perHour;  
        final int   f_drain    = drain_mA;  
        final float f_dCPU     = dCPU;  
        final float f_dBATT    = dBATT;  
        final float f_dv       = dv;  
        final float f_cap      = estimatedCapacity_mAh;  
        final int   f_factory  = factory;  
        final int   f_score    = score;  
        final String f_voltLbl = voltageLabel;  
        final String f_therm   = thermalLabel;  
        final String f_cycle   = cycleLabel;  
        final String f_cat     = category;  

        ui.post(() -> {  
            logLine();  
            logInfo("GEL Battery Intelligence Evaluation");  
            logLine();  

            logInfo(String.format(Locale.US,  
                    "Stress window: %.1f%% → %.1f%% (drop %.2f%%)",  
                    f_before, f_after, f_drop));  

            logInfo(String.format(Locale.US,  
                    "Drain rate under load: %.1f %%/hour", f_perHour));  

            logInfo(String.format(Locale.US,  
                    "1. Stress Drain Rate: %d mA", f_drain));  

            logInfo(String.format(Locale.US,  
                    "2. Estimated Real Capacity: %.0f mAh (factory: %d mAh)",  
                    f_cap, f_factory));  

            logInfo(String.format(Locale.US,  
                    "3. Voltage Stability: %s (Δ %.1f mV)",  
                    f_voltLbl, f_dv));  

            logInfo(String.format(Locale.US,  
                    "4. Thermal Rise: %s (CPU +%.1f°C, BATT +%.1f°C)",  
                    f_therm, f_dCPU, f_dBATT));  

            logInfo(String.format(Locale.US,  
                    "5. Cycle Behavior: %s", f_cycle));  

            logLine();  
            logOk(String.format(Locale.US,  
                    "Final Battery Health Score: %d%% (%s)",  
                    f_score, f_cat));  

            // Checkbox map with NEON ✔ and white labels  
            appendHtml("✔ <font color='#39FF14'>Strong</font>");  
            appendHtml("☐ <font color='#FFFFFF'>Excellent</font>");  
            appendHtml("☐ <font color='#FFFFFF'>Very good</font>");  
            appendHtml("☐ <font color='#FFFFFF'>Normal</font>");  
            appendHtml("☐ <font color='#FFFFFF'>Weak</font>");  
        });  

    } catch (Exception e) {  
        ui.post(() -> logError("Lab 17 error: " + e.getMessage()));  
    }  

}).start();

}

// ============================================================
// SUPPORT FUNCTIONS FOR LAB 17 (HELPERS)
// ============================================================

private float getBatteryVoltage_mV() {
try {
IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
Intent i = registerReceiver(null, f);
if (i == null) return 0f;
return i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
} catch (Exception e) {
return 0f;
}
}

private int getFactoryCapacity_mAh() {
// Generic fallback. You can later replace with per-model DB.
return 5000;
}                       
    
// ============================================================ // ============================================================
// LABS 18–21: STORAGE & PERFORMANCE
// ============================================================
private void lab18StorageSnapshot() {
logLine();
logInfo("LAB 18 — Internal Storage Snapshot.");
try {
StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());
long total = s.getBlockCountLong() * s.getBlockSizeLong();
long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
long used = total - free;
int pctFree = (int) ((free * 100L) / total);

logInfo("Internal storage used: " + humanBytes(used) + " / " + humanBytes(total)  
                + " (free " + humanBytes(free) + ", " + pctFree + "%).");  

        if (pctFree < 5)  
            logError("Free space below 5% — high risk of crashes, failed updates and slow UI.");  
        else if (pctFree < 10)  
            logWarn("Free space below 10% — performance and update issues likely.");  
        else  
            logOk("Internal storage level is acceptable for daily usage.");  
    } catch (Exception e) {  
        logError("Storage snapshot error: " + e.getMessage());  
    }  
}  

private void lab19AppsFootprint() {  
    logLine();  
    logInfo("LAB 19 — Installed Apps Footprint.");  
    try {  
        PackageManager pm = getPackageManager();  
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);  
        if (apps == null) {  
            logWarn("Cannot read installed applications list.");  
            return;  
        }  
        int userApps = 0;  
        int systemApps = 0;  
        for (ApplicationInfo ai : apps) {  
            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) systemApps++;  
            else userApps++;  
        }  
        logInfo("User-installed apps: " + userApps);  
        logInfo("System apps: " + systemApps);  
        logInfo("Total packages: " + apps.size());  

        if (userApps > 120)  
            logError("Very high number of user apps — strong risk of background drain and lag.");  
        else if (userApps > 80)  
            logWarn("High number of user apps — possible performance impact.");  
        else  
            logOk("App footprint is within a normal range.");  
    } catch (Exception e) {  
        logError("Apps footprint error: " + e.getMessage());  
    }  
}  

private void lab20RamSnapshot() {  
    logLine();  
    logInfo("LAB 20 — Live RAM Snapshot.");  
    try {  
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
        if (am == null) {  
            logError("ActivityManager not available.");  
            return;  
        }  
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();  
        am.getMemoryInfo(mi);  
        long free = mi.availMem;  
        long total = mi.totalMem;  
        int pct = (int) ((free * 100L) / total);  
        logInfo("RAM now: " + humanBytes(free) + " free (" + pct + "%).");  
        if (pct < 10)  
            logError("Very low free RAM (<10%) — expect heavy lag and aggressive app killing.");  
        else if (pct < 20)  
            logWarn("Low free RAM (10–20%) — borderline under load.");  
        else  
            logOk("RAM level is acceptable for normal usage at this moment.");  
    } catch (Exception e) {  
        logError("RAM snapshot error: " + e.getMessage());  
    }  
}  

private void lab21UptimeHints() {  
    logLine();  
    logInfo("LAB 21 — Uptime / Reboot History Hints.");  
    long upMs = SystemClock.elapsedRealtime();  
    String upStr = formatUptime(upMs);  
    logInfo("System uptime: " + upStr);  
    if (upMs < 2 * 60 * 60 * 1000L) {  
        logWarn("Device was rebooted recently (<2 hours) — some issues may already be masked by the reboot.");  
    } else if (upMs > 7L * 24L * 60L * 60L * 1000L) {  
        logWarn("Uptime above 7 days — recommend a reboot before deep diagnostics.");  
    } else {  
        logOk("Uptime is within a reasonable range for diagnostics.");  
    }  
}  

// ============================================================  
// LABS 22–25: SECURITY & SYSTEM HEALTH  
// ============================================================  
// ============================================================

// LAB 22 — Screen Lock / Biometrics Checklist (auto-detect + manual)
// ============================================================
private void lab22ScreenLock() {
logLine();
logInfo("LAB 22 — Screen Lock / Biometrics Checklist");

try {  
    android.app.KeyguardManager km =  
            (android.app.KeyguardManager) getSystemService(KEYGUARD_SERVICE);  

    if (km != null) {  
        boolean secure = km.isDeviceSecure();  

        if (secure) {  
            logOk("Device reports SECURE lock method (PIN / Pattern / Password).");  
        } else {  
            logError("Device has NO secure lock method — phone is UNPROTECTED!");  
            logWarn("Anyone can access data without authentication.");  
        }  
    } else {  
        logWarn("KeyguardManager not available — cannot read lock status.");  
    }  
} catch (Exception e) {  
    logWarn("Screen lock detection failed: " + e.getMessage());  
}  

// Manual guidance (kept for technician)  
logInfo("1) Verify that the device has a secure lock method (PIN / pattern / password).");  
logWarn("If the device is left with no lock at all — higher risk for data and account theft.");  
logInfo("2) Test fingerprint / face unlock if configured to confirm sensor response.");

}

// ============================================================

// LAB 23 — Security Patch & Play Protect (auto + manual)
// ============================================================
private void lab23SecurityPatchManual() {
logLine();
logInfo("LAB 23 — Security Patch & Play Protect Check");

// ----------------------------  
// 1) Security Patch Level  
// ----------------------------  
try {  
    String patch = android.os.Build.VERSION.SECURITY_PATCH;  
    if (patch != null && !patch.isEmpty()) {  
        logInfo("Security Patch Level: " + patch);  
    } else {  
        logWarn("Security Patch Level not reported by system.");  
    }  
} catch (Exception e) {  
    logWarn("Security patch read failed: " + e.getMessage());  
}  

// ----------------------------  
// 2) Play Protect Detection — BEST POSSIBLE WITHOUT ROOT  
// ----------------------------  
try {  
    PackageManager pm = getPackageManager();  

    // Check Google Play Services exists  
    boolean gmsPresent = false;  
    try {  
        pm.getPackageInfo("com.google.android.gms", 0);  
        gmsPresent = true;  
    } catch (Exception ignored) {}  

    if (!gmsPresent) {  
        logError("Google Play Services missing — Play Protect NOT available.");  
    } else {  
        // Check Verify Apps setting (Google verifier)  
        int verify = -1;  
        try {  
            verify = Settings.Global.getInt(  
                    getContentResolver(),  
                    "package_verifier_enable",  
                    -1  
            );  
        } catch (Exception ignored) {}  

        if (verify == 1) {  
            logOk("Play Protect: ON (Google Verify Apps ENABLED).");  
        } else if (verify == 0) {  
            logWarn("Play Protect: OFF (Google Verify Apps DISABLED).");  
        } else {  
            // Fallback — detect if the activity exists  
            Intent protectIntent = new Intent();  
            protectIntent.setClassName(  
                    "com.google.android.gms",  
                    "com.google.android.gms.security.settings.VerifyAppsSettingsActivity"  
            );  

            if (protectIntent.resolveActivity(pm) != null) {  
                logOk("Play Protect module detected (activity present).");  
            } else {  
                logWarn("Play Protect module not fully detected — OEM variant or restricted build.");  
            }  
        }  
    }  
} catch (Exception e) {  
    logWarn("Play Protect detection error: " + e.getMessage());  
}  

// MANUAL GUIDANCE (kept for technicians)  
logInfo("1) Open Android Settings → About phone → Android version → Security patch level.");  
logWarn("If the patch level is very old compared to current date — increased vulnerability risk.");  
logInfo("2) In Google Play Store → Play Protect → verify scanning is enabled and up to date.");

}

// ============================================================

// LAB 24 — Developer Options / ADB Risk Note + UI BUBBLES + AUTO-FIX HINTS
// GEL Security v3.1 (Realtime Snapshot)
// ============================================================
private void lab24DevOptions() {
logLine();
logInfo("LAB 24 — Developer Options / ADB Risk Note (Realtime).");

int risk = 0;  

// ============================================================  
// 1) USB DEBUGGING FLAG (ADB_ENABLED)  
// ============================================================  
boolean usbDebug = false;  
try {  
    int adb = Settings.Global.getInt(  
            getContentResolver(),  
            Settings.Global.ADB_ENABLED,  
            0  
    );  
    usbDebug = (adb == 1);  

    logInfo("USB Debugging: " + bubble(usbDebug) + " " + usbDebug);  

    if (usbDebug) {  
        logWarn("USB Debugging ENABLED — physical access risk.");  
        risk += 30;  
    } else {  
        logOk("USB Debugging is OFF.");  
    }  

} catch (Exception e) {  
    logWarn("Could not read USB Debugging flag (OEM restriction).");  
    risk += 5;  
}  

// ============================================================  
// 2) DEVELOPER OPTIONS FLAG  
// ============================================================  
boolean devOpts = false;  
try {  
    int dev = Settings.Global.getInt(  
            getContentResolver(),  
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,  
            0  
    );  
    devOpts = (dev == 1);  

    logInfo("Developer Options: " + bubble(devOpts) + " " + devOpts);  

    if (devOpts) {  
        logWarn("Developer Options ENABLED.");  
        risk += 20;  
    } else {  
        logOk("Developer Options are OFF.");  
    }  

} catch (Exception e) {  
    logWarn("Could not read Developer Options flag.");  
    risk += 5;  
}  

// ============================================================  
// 3) ADB OVER WIFI (TCP/IP mode — port 5555)  
// ============================================================  
boolean adbWifi = isPortOpen(5555, 200);  

logInfo("ADB over Wi-Fi (5555): " + bubble(adbWifi) + " " + (adbWifi ? "ACTIVE" : "OFF"));  

if (adbWifi) {  
    logError("ADB over Wi-Fi ACTIVE — remote debugging possible on same network.");  
    risk += 40;  
} else {  
    logOk("ADB over Wi-Fi is OFF.");  
}  

// ============================================================  
// 4) ADB PAIRING MODE (Android 11–14 typical ports)  
// ============================================================  
boolean adbPairing =  
        isPortOpen(3700, 200) ||   // some OEM pairing  
        isPortOpen(7460, 200) ||   // pairing service  
        scanPairingPortRange();    // 7460–7490  

logInfo("ADB Pairing Mode: " + bubble(adbPairing) + " " + (adbPairing ? "ACTIVE" : "OFF"));  

if (adbPairing) {  
    logError("ADB Pairing is ACTIVE — device discoverable for pairing.");  
    risk += 25;  
} else {  
    logOk("ADB Pairing is OFF.");  
}  

// ============================================================  
// 5) FINAL RISK SCORE  
// ============================================================  
if (risk > 100) risk = 100;  

String level;  
if (risk <= 10)       level = "LOW";  
else if (risk <= 30)  level = "MEDIUM";  
else if (risk <= 60)  level = "HIGH";  
else                  level = "CRITICAL";  

logLine();  
logInfo("Security Risk Score: " + risk + "/100  (" + level + ") " + riskBubble(risk));  

// ============================================================  
// 6) AUTO-FIX / ACTION HINTS  
// ============================================================  
logLine();  
logInfo("Recommended Actions:");  

if (usbDebug || devOpts) {  
    logWarn("• Disable Developer Options / USB Debugging:");  
    logInfo("  Settings → System → Developer options → OFF");  
    logInfo("  USB debugging → OFF");  
} else {  
    logOk("• Developer options & USB debugging look safe.");  
}  

if (adbWifi) {  
    logError("• ADB over Wi-Fi must be disabled:");  
    logInfo("  Developer options → Wireless debugging → OFF");  
    logInfo("  Or reboot to clear tcpip mode.");  
} else {  
    logOk("• Wireless debugging is not active.");  
}  

if (adbPairing) {  
    logError("• Turn OFF ADB Pairing / Wireless debugging:");  
    logInfo("  Developer options → Wireless debugging → OFF");  
} else {  
    logOk("• ADB Pairing is not active.");  
}  

if (risk >= 60)  
    logError("⚠ Very high risk — disable ADB features immediately!");  
else if (risk >= 30)  
    logWarn("⚠ Partial exposure — review ADB settings.");  
else  
    logOk("✔ Risk level acceptable.");

}

// ============================================================
// UI BUBBLES (GEL)
// ============================================================
private String bubble(boolean on) {
return on ? "🔴" : "🟢";
}

private String riskBubble(int risk) {
if (risk <= 10) return "🟢";
if (risk <= 30) return "🟡";
if (risk <= 60) return "🟠";
return "🔴";
}

// ============================================================
// HELPERS — PORT CHECK (LOCALHOST)
// ============================================================
private boolean isPortOpen(int port, int timeoutMs) {
Socket s = null;
try {
s = new Socket();
s.connect(new InetSocketAddress("127.0.0.1", port), timeoutMs);
return true;
} catch (Exception e) {
return false;
} finally {
if (s != null) try { s.close(); } catch (Exception ignored) {}
}
}

// Scan pairing port range 7460–7490 (best-effort)
private boolean scanPairingPortRange() {
for (int p = 7460; p <= 7490; p++) {
if (isPortOpen(p, 80)) return true;
}
return false;
}

// ============================================================

// LAB 25 — Root / Bootloader Suspicion Checklist (FULL AUTO + RISK SCORE)
// GEL Universal Edition — NO external libs
// ============================================================
private void lab25RootSuspicion() {
logLine();
logInfo("LAB 25 — Root / Bootloader Integrity Scan (AUTO).");

// ---------------------------  
// (1) ROOT DETECTION  
// ---------------------------  
int rootScore = 0;  
List<String> rootFindings = new ArrayList<>();  

// su / busybox paths  
String[] suPaths = {  
        "/system/bin/su",  
        "/system/xbin/su",  
        "/sbin/su",  
        "/su/bin/su",  
        "/system/app/Superuser.apk",  
        "/system/app/SuperSU.apk",  
        "/system/app/Magisk.apk",  
        "/system/bin/busybox",  
        "/system/xbin/busybox",  
        "/vendor/bin/su",  
        "/odm/bin/su"  
};  

boolean suFound = false;  
for (String p : suPaths) {  
    if (lab25_fileExists(p)) {  
        suFound = true;  
        rootScore += 18;  
        rootFindings.add("su/busybox path found: " + p);  
    }  
}  

// which su  
String whichSu = lab25_execFirstLine("which su");  
if (whichSu != null && whichSu.contains("/")) {  
    rootScore += 12;  
    rootFindings.add("'which su' returned: " + whichSu);  
    suFound = true;  
}  

// try exec su (best effort)  
boolean suExec = lab25_canExecSu();  
if (suExec) {  
    rootScore += 25;  
    rootFindings.add("su execution possible (shell granted).");  
    suFound = true;  
}  

// known root packages  
String[] rootPkgs = {  
        "com.topjohnwu.magisk",  
        "eu.chainfire.supersu",  
        "com.koushikdutta.superuser",  
        "com.noshufou.android.su",  
        "com.kingroot.kinguser",  
        "com.kingo.root",  
        "com.saurik.substrate",  
        "de.robv.android.xposed.installer",  
        "com.zachspong.temprootremovejb",  
        "com.ramdroid.appquarantine"  
};  

List<String> installed = lab25_getInstalledPackagesLower();  
boolean pkgHit = false;  
for (String rp : rootPkgs) {  
    if (installed.contains(rp)) {  
        pkgHit = true;  
        rootScore += 20;  
        rootFindings.add("root package installed: " + rp);  
    }  
}  

// build tags  
try {  
    String tags = Build.TAGS;  
    if (tags != null && tags.contains("test-keys")) {  
        rootScore += 15;  
        rootFindings.add("Build.TAGS contains test-keys.");  
    }  
} catch (Throwable ignore) {}  

// suspicious props  
String roSecure = lab25_getProp("ro.secure");  
String roDebug  = lab25_getProp("ro.debuggable");  
if ("0".equals(roSecure)) {  
    rootScore += 18;  
    rootFindings.add("ro.secure=0 (insecure build).");  
}  
if ("1".equals(roDebug)) {  
    rootScore += 12;  
    rootFindings.add("ro.debuggable=1 (debuggable build).");  
}  

// ---------------------------  
// (2) BOOTLOADER / VERIFIED BOOT  
// ---------------------------  
int blScore = 0;  
List<String> blFindings = new ArrayList<>();  

String vbState = lab25_getProp("ro.boot.verifiedbootstate"); // green/yellow/orange/red  
String vbmeta  = lab25_getProp("ro.boot.vbmeta.device_state"); // locked/unlocked  
String flashL  = lab25_getProp("ro.boot.flash.locked"); // 1/0  
String wlBit   = lab25_getProp("ro.boot.warranty_bit"); // 0/1 on some OEMs  

if (vbState != null && (vbState.contains("orange") || vbState.contains("yellow") || vbState.contains("red"))) {  
    blScore += 30;  
    blFindings.add("VerifiedBootState=" + vbState);  
} else if (vbState != null) {  
    blFindings.add("VerifiedBootState=" + vbState);  
}  

if (vbmeta != null && vbmeta.contains("unlocked")) {  
    blScore += 35;  
    blFindings.add("vbmeta.device_state=unlocked");  
} else if (vbmeta != null) {  
    blFindings.add("vbmeta.device_state=" + vbmeta);  
}  

if ("0".equals(flashL)) {  
    blScore += 25;  
    blFindings.add("flash.locked=0 (bootloader unlocked).");  
} else if (flashL != null) {  
    blFindings.add("flash.locked=" + flashL);  
}  

if ("1".equals(wlBit)) {  
    blScore += 15;  
    blFindings.add("warranty_bit=1 (tamper flag).");  
}  

// OEM unlock allowed flag (Android settings)  
try {  
    int oemAllowed = Settings.Global.getInt(getContentResolver(), "oem_unlock_allowed", 0);  
    if (oemAllowed == 1) {  
        blScore += 10;  
        blFindings.add("OEM unlock allowed=1 (developer enabled).");  
    }  
} catch (Throwable ignore) {}  

// /proc/cmdline hints  
String cmdline = lab25_readOneLine("/proc/cmdline");  
if (cmdline != null) {  
    String c = cmdline.toLowerCase(Locale.US);  
    if (c.contains("verifiedbootstate=orange") || c.contains("verifiedbootstate=yellow") ||  
        c.contains("vbmeta.device_state=unlocked") || c.contains("bootloader=unlocked")) {  
        blScore += 20;  
        blFindings.add("/proc/cmdline reports unlocked/weak verified boot.");  
    }  
}  

// ---------------------------  
// (3) BOOT ANIMATION / SPLASH MOD  
// ---------------------------  
int animScore = 0;  
List<String> animFindings = new ArrayList<>();  

// Strong indicator: custom bootanimation in /data/local  
if (lab25_fileExists("/data/local/bootanimation.zip")) {  
    animScore += 35;  
    animFindings.add("Custom bootanimation detected: /data/local/bootanimation.zip");  
}  

// If system bootanimation missing → suspicious ROM  
boolean sysBoot = lab25_fileExists("/system/media/bootanimation.zip") ||  
                  lab25_fileExists("/product/media/bootanimation.zip") ||  
                  lab25_fileExists("/oem/media/bootanimation.zip") ||  
                  lab25_fileExists("/vendor/media/bootanimation.zip");  
if (!sysBoot) {  
    animScore += 15;  
    animFindings.add("No stock bootanimation found in system partitions (non-stock ROM?).");  
} else {  
    animFindings.add("Stock bootanimation path exists.");  
}  

// ---------------------------  
// FINAL RISK SCORE  
// ---------------------------  
int risk = Math.min(100, rootScore + blScore + animScore);  

// Print ROOT section  
logLine();  
logInfo("Root Scan:");  
if (rootFindings.isEmpty()) {  
    logOk("No strong root traces detected.");  
} else {  
    for (String s : rootFindings) logWarn("• " + s);  
}  

// Print BOOTLOADER section  
logLine();  
logInfo("Bootloader / Verified Boot:");  
if (blFindings.isEmpty()) {  
    logOk("No bootloader anomalies detected.");  
} else {  
    for (String s : blFindings) logWarn("• " + s);  
}  

// Print ANIMATION section  
logLine();  
logInfo("Boot Animation / Splash:");  
if (animFindings.isEmpty()) {  
    logOk("No custom animation traces detected.");  
} else {  
    for (String s : animFindings) logWarn("• " + s);  
}  

// Verdict  
logLine();  
logInfo("FINAL VERDICT:");  
logInfo("RISK SCORE: " + risk + " / 100");  

if (risk >= 70 || suExec || pkgHit) {  
    logError("STATUS: ROOTED / MODIFIED (high confidence).");  
} else if (risk >= 35) {  
    logWarn("STATUS: SUSPICIOUS (possible root / unlocked / custom ROM).");  
} else {  
    logOk("STATUS: SAFE (no significant modification evidence).");  
}  

logOk("Lab 25 finished.");

}

// ============================================================
// LAB 25 — INTERNAL HELPERS (unique names to avoid conflicts)
// ============================================================
private boolean lab25_fileExists(String path) {
try { return new File(path).exists(); } catch (Throwable t) { return false; }
}

private List<String> lab25_getInstalledPackagesLower() {
List<String> out = new ArrayList<>();
try {
PackageManager pm = getPackageManager();
List<ApplicationInfo> apps = pm.getInstalledApplications(0);
if (apps != null) {
for (ApplicationInfo ai : apps) {
String p = ai.packageName;
if (p != null) out.add(p.toLowerCase(Locale.US));
}
}
} catch (Throwable ignore) {}
return out;
}

private boolean lab25_canExecSu() {
Process p = null;
try {
p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
BufferedReader br = new BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
String line = br.readLine();
br.close();
return line != null && line.toLowerCase(Locale.US).contains("uid=0");
} catch (Throwable t) {
return false;
} finally {
if (p != null) try { p.destroy(); } catch (Throwable ignore) {}
}
}

private String lab25_execFirstLine(String cmd) {
Process p = null;
try {
p = Runtime.getRuntime().exec(cmd);
BufferedReader br = new BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
String line = br.readLine();
br.close();
return line != null ? line.trim() : null;
} catch (Throwable t) {
return null;
} finally {
if (p != null) try { p.destroy(); } catch (Throwable ignore) {}
}
}

private String lab25_getProp(String key) {
String v = lab25_execFirstLine("getprop " + key);
if (v == null) return null;
v = v.trim();
return v.isEmpty() ? null : v.toLowerCase(Locale.US);
}

private String lab25_readOneLine(String path) {
BufferedReader br = null;
try {
br = new BufferedReader(new FileReader(new File(path)));
return br.readLine();
} catch (Throwable t) {
return null;
} finally {
if (br != null) try { br.close(); } catch (Throwable ignore) {}
}
}
// ============================================================
// LABS 26–29: ADVANCED / LOGS
// ============================================================

// ============================================================
// LAB 26 — GEL Crash Intelligence v5.0 (FULL AUTO EDITION)
// ============================================================
private void lab26CrashHistory() {

logLine();  
logInfo("LAB 26 — GEL Crash Intelligence (AUTO)");  

int crashCount = 0;  
int anrCount = 0;  
int systemCount = 0;  

Map<String, Integer> appEvents = new HashMap<>(); // Group per app  
List<String> details = new ArrayList<>();  

// ============================================================  
// (A) Android 11+ — Process Exit Reasons  
// ============================================================  
try {  
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {  
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  
        if (am != null) {  
            List<ActivityManager.ProcessErrorStateInfo> errs = am.getProcessesInErrorState();  
            if (errs != null) {  
                for (ActivityManager.ProcessErrorStateInfo e : errs) {  

                    String app = e.processName;  
                    appEvents.put(app, appEvents.getOrDefault(app, 0) + 1);  

                    if (e.condition == ActivityManager.ProcessErrorStateInfo.CRASHED) {  
                        crashCount++;  
                        details.add("CRASH: " + app + " — " + e.shortMsg);  
                    }   
                    else if (e.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {  
                        anrCount++;  
                        details.add("ANR: " + app + " — " + e.shortMsg);  
                    }  
                }  
            }  
        }  
    }  
} catch (Exception ignored) {}  

// ============================================================  
// (B) DropBox crash logs — legacy Android sources  
// ============================================================  
try {  
    DropBoxManager db = (DropBoxManager) getSystemService(DROPBOX_SERVICE);  

    if (db != null) {  
        String[] tags = {  
                "system_app_crash", "data_app_crash",  
                "system_app_anr", "data_app_anr",  
                "system_server_crash", "system_server_wtf",  
                "system_server_anr"  
        };  

        for (String tag : tags) {  
            DropBoxManager.Entry ent = db.getNextEntry(tag, 0);  

            while (ent != null) {  

                if (tag.contains("crash")) crashCount++;  
                if (tag.contains("anr")) anrCount++;  
                if (tag.contains("server")) systemCount++;  

                String shortTxt = readDropBoxEntry(ent);  

                String clean = tag.toUpperCase(Locale.US).replace("_", " ");  
                details.add(clean + ": " + shortTxt);  

                // grouping  
                String key = clean;  
                appEvents.put(key, appEvents.getOrDefault(key, 0) + 1);  

                ent = db.getNextEntry(tag, ent.getTimeMillis());  
            }  
        }  
    }  

} catch (Exception ignored) {}  

// ============================================================  
// (C) SUMMARY + RISK SCORE  
// ============================================================  
int risk = 0;  
risk += crashCount * 5;  
risk += anrCount * 8;  
risk += systemCount * 15;  
if (risk > 100) risk = 100;  

// COLOR INDICATOR  
String riskColor =  
        (risk <= 20) ? "🟩" :  
        (risk <= 50) ? "🟨" :  
        (risk <= 80) ? "🟧" : "🟥";  

logInfo("Crash events: " + crashCount);  
logInfo("ANR events: " + anrCount);  
logInfo("System-level faults: " + systemCount);  

logInfo(riskColor + " Stability Risk Score: " + risk + "%");  

// ============================================================  
// (D) HEATMAP (top offenders)  
// ============================================================  
if (!appEvents.isEmpty()) {  
    logLine();  
    logInfo("Top Offenders (Heatmap):");  

    appEvents.entrySet()  
            .stream()  
            .sorted((a, b) -> b.getValue() - a.getValue())  
            .limit(5)  
            .forEach(e -> {  
                String c = (e.getValue() >= 10) ? "🟥" :  
                           (e.getValue() >= 5)  ? "🟧" :  
                           (e.getValue() >= 2)  ? "🟨" :  
                                                  "🟩";  
                logInfo(" " + c + " " + e.getKey() + " → " + e.getValue() + " events");  
            });  
}  

// ============================================================  
// (E) FULL DETAILS  
// ============================================================  
if (!details.isEmpty()) {  
    logLine();  
    logInfo("Detailed Crash Records:");  
    for (String d : details) logInfo(d);  
} else {  
    logOk("No crash history found.");  
}  

logOk("Lab 26 finished.");

}

// ============================================================
// SMALL helper inside same block (allowed)
// Reads first 10 lines of DropBox entry
// ============================================================
private String readDropBoxEntry(DropBoxManager.Entry ent) {
try {
if (ent == null) return "(no text)";
InputStream is = ent.getInputStream();
if (is == null) return "(no text)";

BufferedReader br = new BufferedReader(new InputStreamReader(is));  
    StringBuilder sb = new StringBuilder();  
    String line;  
    int count = 0;  
    while ((line = br.readLine()) != null && count < 10) {  
        sb.append(line).append(" ");  
        count++;  
    }  
    br.close();  
    return sb.toString().trim();  
} catch (Exception e) {  
    return "(read error)";  
}

}

// ============================================================
// LAB 27 — App Permissions & Privacy (FULL AUTO + RISK SCORE)
// ============================================================
private void lab27PermissionsPrivacy() {

logLine();  
logInfo("LAB 27 — App Permissions & Privacy (AUTO scan)");  

PackageManager pm = getPackageManager();  
if (pm == null) {  
    logError("PackageManager not available.");  
    return;  
}  

List<String> details = new ArrayList<>();  
Map<String, Integer> appRisk = new HashMap<>();  

int totalApps = 0;  
int flaggedApps = 0;  

// Risk totals  
int riskTotal = 0;  
int dangTotal = 0;  

try {  
    List<android.content.pm.PackageInfo> packs;  

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  
        packs = pm.getInstalledPackages(  
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS));  
    } else {  
        //noinspection deprecation  
        packs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);  
    }  

    if (packs == null) packs = new ArrayList<>();  

    for (android.content.pm.PackageInfo p : packs) {  
        if (p == null || p.packageName == null) continue;  
        totalApps++;  

        // Skip system apps unless they have highly dangerous perms  
        boolean isSystem = (p.applicationInfo != null) &&  
                ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);  

        String[] req = p.requestedPermissions;  
        int[] grant = p.requestedPermissionsFlags;  

        if (req == null || req.length == 0) continue;  

        int appScore = 0;  
        int appDangerCount = 0;  
        StringBuilder sb = new StringBuilder();  

        for (int i = 0; i < req.length; i++) {  
            String perm = req[i];  
            if (perm == null) continue;  

            boolean granted = isGrantedFlag(grant, i);  

            int weight = permissionWeight(perm);  
            if (weight <= 0) continue; // ignore harmless  

            // count dangerous only if granted  
            if (granted) {  
                appDangerCount++;  
                appScore += weight;  
                sb.append("• ").append(shortPerm(perm)).append(" (granted)\n");  
            }  
        }  

        if (appScore > 0) {  
            dangTotal += appDangerCount;  
            riskTotal += appScore;  

            // system apps threshold higher  
            int threshold = isSystem ? 25 : 10;  

            if (appScore >= threshold) {  
                flaggedApps++;  
                appRisk.put(p.packageName, appScore);  

                String appLabel = safeLabel(pm, p.packageName);  
                String color =  
                        (appScore >= 60) ? "🟥" :  
                        (appScore >= 30) ? "🟧" :  
                        (appScore >= 15) ? "🟨" : "🟩";  

                details.add(color + " " + appLabel + " (" + p.packageName + ")"  
                        + " — Risk=" + appScore + "\n" + sb.toString());  
            }  
        }  
    }  

} catch (SecurityException se) {  
    logWarn("Permissions scan limited by Android package visibility policy.");  
    logWarn("Tip: add QUERY_ALL_PACKAGES if you want full scan on Android 11+.");  
} catch (Exception e) {  
    logError("Permissions scan error: " + e.getMessage());  
}  

// ============================================================  
// SUMMARY + FINAL RISK SCORE  
// ============================================================  
int riskPct = Math.min(100, riskTotal); // cap  
String riskColor =  
        (riskPct <= 20) ? "🟩" :  
        (riskPct <= 50) ? "🟨" :  
        (riskPct <= 80) ? "🟧" : "🟥";  

logInfo("Apps scanned: " + totalApps);  
logInfo("Dangerous permissions granted (total): " + dangTotal);  
logInfo("Flagged apps: " + flaggedApps);  
logInfo(riskColor + " Privacy Risk Score: " + riskPct + "%");  

// ============================================================  
// TOP OFFENDERS  
// ============================================================  
if (!appRisk.isEmpty()) {  
    logLine();  
    logInfo("Top Privacy Offenders:");  

    appRisk.entrySet()  
            .stream()  
            .sorted((a, b) -> b.getValue() - a.getValue())  
            .limit(8)  
            .forEach(e -> {  
                String c =  
                        (e.getValue() >= 60) ? "🟥" :  
                        (e.getValue() >= 30) ? "🟧" :  
                        (e.getValue() >= 15) ? "🟨" : "🟩";  

                logInfo(" " + c + " " + safeLabel(pm, e.getKey())  
                        + " — Risk " + e.getValue());  
            });  
}  

// ============================================================  
// FULL DETAILS  
// ============================================================  
if (!details.isEmpty()) {  
    logLine();  
    logInfo("Permission Details (flagged apps):");  
    for (String d : details) logInfo(d);  
} else {  
    logOk("No high-risk permission patterns detected.");  
}  

logOk("Lab 27 finished.");

}

// ============================================================
// INTERNAL helpers for Lab 27 (keep inside same lab block)
// ============================================================

private boolean isGrantedFlag(int[] flags, int i) {
try {
if (flags == null || i < 0 || i >= flags.length) return false;
return (flags[i] & android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
} catch (Exception e) {
return false;
}
}

private String safeLabel(PackageManager pm, String pkg) {
try {
ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
CharSequence cs = pm.getApplicationLabel(ai);
return cs != null ? cs.toString() : pkg;
} catch (Exception e) {
return pkg;
}
}

// Weight per dangerous/sensitive permission
private int permissionWeight(String p) {
if (p == null) return 0;

// VERY HIGH RISK  
if (p.equals(Manifest.permission.READ_SMS)) return 25;  
if (p.equals(Manifest.permission.RECEIVE_SMS)) return 20;  
if (p.equals(Manifest.permission.SEND_SMS)) return 25;  
if (p.equals(Manifest.permission.READ_CALL_LOG)) return 25;  
if (p.equals(Manifest.permission.WRITE_CALL_LOG)) return 25;  
if (p.equals(Manifest.permission.CALL_PHONE)) return 15;  

// HIGH RISK  
if (p.equals(Manifest.permission.RECORD_AUDIO)) return 20;  
if (p.equals(Manifest.permission.CAMERA)) return 18;  
if (p.equals(Manifest.permission.ACCESS_FINE_LOCATION)) return 18;  
if (p.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) return 12;  
if (p.equals(Manifest.permission.READ_CONTACTS)) return 15;  
if (p.equals(Manifest.permission.WRITE_CONTACTS)) return 15;  
if (p.equals(Manifest.permission.GET_ACCOUNTS)) return 10;  

// STORAGE (legacy)  
if (p.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) return 10;  
if (p.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) return 12;  

// BACKGROUND / SUSPICIOUS  
if (p.equals(Manifest.permission.REQUEST_INSTALL_PACKAGES)) return 20;  
if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) return 15;  
if (p.equals(Manifest.permission.PACKAGE_USAGE_STATS)) return 15;  
if (p.equals(Manifest.permission.BIND_ACCESSIBILITY_SERVICE)) return 25;  

return 0;

}

private String shortPerm(String p) {
if (p == null) return "";
int i = p.lastIndexOf('.');
return (i >= 0 && i < p.length() - 1) ? p.substring(i + 1) : p;
}

// ============================================================
// LAB 28 — Auto Final Diagnosis Summary (GEL Universal AUTO Edition)
// Combines Thermals + Battery + Storage + RAM + Apps + Uptime +
// Security + Privacy + Root + Stability into final scores.
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================
private void lab28CombineFindings() {
logLine();
logInfo("LAB 28 — Auto Final Diagnosis Summary (FULL AUTO)");

// ------------------------------------------------------------  
// 1) THERMALS (from zones + battery temp)  
// ------------------------------------------------------------  
Map<String, Float> zones = null;  
try { zones = readThermalZones(); } catch (Throwable ignored) {}  
float battTemp = getBatteryTemperature();  

Float cpu  = null, gpu = null, skin = null, pmic = null;  
if (zones != null && !zones.isEmpty()) {  
    cpu  = pickZone(zones, "cpu", "cpu-therm", "big", "little", "tsens", "mtktscpu");  
    gpu  = pickZone(zones, "gpu", "gpu-therm", "gpuss", "mtkgpu");  
    skin = pickZone(zones, "skin", "xo-therm", "shell", "surface");  
    pmic = pickZone(zones, "pmic", "pmic-therm", "power-thermal", "charger", "chg");  
}  

float maxThermal = maxOf(cpu, gpu, skin, pmic, battTemp);  
float avgThermal = avgOf(cpu, gpu, skin, pmic, battTemp);  

int thermalScore = scoreThermals(maxThermal, avgThermal);  
String thermalFlag = colorFlagFromScore(thermalScore);  

// ------------------------------------------------------------  
// 2) BATTERY HEALTH (light auto inference)  
// ------------------------------------------------------------  
float battPct = getCurrentBatteryPercent();  
boolean charging = isChargingNow();  
int batteryScore = scoreBattery(battTemp, battPct, charging);  
String batteryFlag = colorFlagFromScore(batteryScore);  

// ------------------------------------------------------------  
// 3) STORAGE HEALTH  
// ------------------------------------------------------------  
StorageSnapshot st = readStorageSnapshot();  
int storageScore = scoreStorage(st.pctFree, st.totalBytes);  
String storageFlag = colorFlagFromScore(storageScore);  

// ------------------------------------------------------------  
// 4) APPS FOOTPRINT  
// ------------------------------------------------------------  
AppsSnapshot ap = readAppsSnapshot();  
int appsScore = scoreApps(ap.userApps, ap.totalApps);  
String appsFlag = colorFlagFromScore(appsScore);  

// ------------------------------------------------------------  
// 5) RAM HEALTH  
// ------------------------------------------------------------  
RamSnapshot rm = readRamSnapshot();  
int ramScore = scoreRam(rm.pctFree);  
String ramFlag = colorFlagFromScore(ramScore);  

// ------------------------------------------------------------  
// 6) UPTIME / STABILITY  
// ------------------------------------------------------------  
long upMs = SystemClock.elapsedRealtime();  
int stabilityScore = scoreStability(upMs);  
String stabilityFlag = colorFlagFromScore(stabilityScore);  

// ------------------------------------------------------------  
// 7) SECURITY (lockscreen + patch + adb/dev + root)  
// ------------------------------------------------------------  
SecuritySnapshot sec = readSecuritySnapshot();  
int securityScore = scoreSecurity(sec);  
String securityFlag = colorFlagFromScore(securityScore);  

// ------------------------------------------------------------  
// 8) PRIVACY (dangerous granted perms to user apps)  
// ------------------------------------------------------------  
PrivacySnapshot pr = readPrivacySnapshot();  
int privacyScore = scorePrivacy(pr);  
String privacyFlag = colorFlagFromScore(privacyScore);  

// ------------------------------------------------------------  
// 9) FINAL SCORES  
// ------------------------------------------------------------  
int performanceScore = Math.round(  
        (storageScore * 0.35f) +  
        (ramScore     * 0.35f) +  
        (appsScore    * 0.15f) +  
        (thermalScore * 0.15f)  
);  

int deviceHealthScore = Math.round(  
        (thermalScore   * 0.25f) +  
        (batteryScore   * 0.25f) +  
        (performanceScore * 0.30f) +  
        (stabilityScore * 0.20f)  
);  

// ------------------------------------------------------------  
// PRINT DETAILS  
// ------------------------------------------------------------  
logLine();  
logInfo("AUTO Breakdown:");  

// Thermals  
logInfo("Thermals: " + thermalFlag + " " + thermalScore + "%");  
if (zones == null || zones.isEmpty()) {  
    logWarn("• No thermal zones readable. Using Battery temp only: " +  
            String.format(Locale.US, "%.1f°C", battTemp));  
} else {  
    logInfo("• Zones=" + zones.size() +  
            " | max=" + fmt1(maxThermal) + "°C" +  
            " | avg=" + fmt1(avgThermal) + "°C");  
    if (cpu != null)  logInfo("• CPU="  + fmt1(cpu)  + "°C");  
    if (gpu != null)  logInfo("• GPU="  + fmt1(gpu)  + "°C");  
    if (pmic != null) logInfo("• PMIC=" + fmt1(pmic) + "°C");  
    if (skin != null) logInfo("• Skin=" + fmt1(skin) + "°C");  
    logInfo("• Battery=" + fmt1(battTemp) + "°C");  
}  

// Battery  
logInfo("Battery: " + batteryFlag + " " + batteryScore + "%");  
logInfo("• Level=" + (battPct >= 0 ? fmt1(battPct) + "%" : "Unknown") +  
        " | Temp=" + fmt1(battTemp) + "°C | Charging=" + charging);  

// Storage  
logInfo("Storage: " + storageFlag + " " + storageScore + "%");  
logInfo("• Free=" + st.pctFree + "% | Used=" + humanBytes(st.usedBytes) +  
        " / " + humanBytes(st.totalBytes));  

// Apps  
logInfo("Apps Footprint: " + appsFlag + " " + appsScore + "%");  
logInfo("• User apps=" + ap.userApps + " | System apps=" + ap.systemApps +  
        " | Total=" + ap.totalApps);  

// RAM  
logInfo("RAM: " + ramFlag + " " + ramScore + "%");  
logInfo("• Free=" + rm.pctFree + "% (" + humanBytes(rm.freeBytes) + " / " +  
        humanBytes(rm.totalBytes) + ")");  

// Stability  
logInfo("Stability/Uptime: " + stabilityFlag + " " + stabilityScore + "%");  
logInfo("• Uptime=" + formatUptime(upMs));  
if (upMs < 2 * 60 * 60 * 1000L)  
    logWarn("• Recent reboot detected (<2h) — possible instability masking.");  
else if (upMs > 7L * 24L * 60L * 60L * 1000L)  
    logWarn("• Long uptime (>7d) — recommend reboot before deep servicing.");  

// Security  
logInfo("Security: " + securityFlag + " " + securityScore + "%");  
logInfo("• Lock secure=" + sec.lockSecure);  
logInfo("• Patch level=" + (sec.securityPatch == null ? "Unknown" : sec.securityPatch));  
logInfo("• ADB USB=" + sec.adbUsbOn + " | ADB Wi-Fi=" + sec.adbWifiOn +  
        " | DevOptions=" + sec.devOptionsOn);  
if (sec.rootSuspected) logWarn("• Root suspicion flags detected.");  
if (sec.testKeys) logWarn("• Build signed with test-keys (custom ROM risk).");  

// Privacy  
logInfo("Privacy: " + privacyFlag + " " + privacyScore + "%");  
logInfo("• Dangerous perms on user apps: " +  
        "Location=" + pr.userAppsWithLocation +  
        ", Mic=" + pr.userAppsWithMic +  
        ", Camera=" + pr.userAppsWithCamera +  
        ", SMS=" + pr.userAppsWithSms);  

// ------------------------------------------------------------  
// FINAL VERDICT  
// ------------------------------------------------------------  
logLine();  
logInfo("FINAL Scores:");  
logInfo("Device Health Score: " + deviceHealthScore + "% " + colorFlagFromScore(deviceHealthScore));  
logInfo("Performance Score:   " + performanceScore + "% " + colorFlagFromScore(performanceScore));  
logInfo("Security Score:      " + securityScore + "% " + securityFlag);  
logInfo("Privacy Score:       " + privacyScore + "% " + privacyFlag);  

String verdict = finalVerdict(deviceHealthScore, securityScore, privacyScore, performanceScore);  
if (verdict.startsWith("🟩")) logOk(verdict);  
else if (verdict.startsWith("🟨")) logWarn(verdict);  
else logError(verdict);  

logOk("Lab 28 finished.");

}

// ============================================================
// ======= LAB 28 INTERNAL AUTO HELPERS (SAFE, NO IMPORTS) =====
// ============================================================

private static class StorageSnapshot {
long totalBytes, freeBytes, usedBytes;
int pctFree;
}

private StorageSnapshot readStorageSnapshot() {
StorageSnapshot s = new StorageSnapshot();
try {
StatFs fs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
s.totalBytes = fs.getBlockCountLong() * fs.getBlockSizeLong();
s.freeBytes  = fs.getAvailableBlocksLong() * fs.getBlockSizeLong();
s.usedBytes  = s.totalBytes - s.freeBytes;
s.pctFree = (s.totalBytes > 0) ? (int)((s.freeBytes * 100L) / s.totalBytes) : 0;
} catch (Throwable ignored) {}
return s;
}

private static class AppsSnapshot {
int userApps, systemApps, totalApps;
}

private AppsSnapshot readAppsSnapshot() {
AppsSnapshot a = new AppsSnapshot();
try {
PackageManager pm = getPackageManager();
List<ApplicationInfo> apps = pm.getInstalledApplications(0);
if (apps != null) {
a.totalApps = apps.size();
for (ApplicationInfo ai : apps) {
if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) a.systemApps++;
else a.userApps++;
}
}
} catch (Throwable ignored) {}
return a;
}

private static class RamSnapshot {
long totalBytes, freeBytes;
int pctFree;
}

private RamSnapshot readRamSnapshot() {
RamSnapshot r = new RamSnapshot();
try {
ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
if (am != null) {
am.getMemoryInfo(mi);
r.totalBytes = mi.totalMem;
r.freeBytes  = mi.availMem;
r.pctFree = (r.totalBytes > 0) ? (int)((r.freeBytes * 100L) / r.totalBytes) : 0;
}
} catch (Throwable ignored) {}
return r;
}

private static class SecuritySnapshot {
boolean lockSecure;
boolean adbUsbOn;
boolean adbWifiOn;
boolean devOptionsOn;
boolean rootSuspected;
boolean testKeys;
String securityPatch;
}

private SecuritySnapshot readSecuritySnapshot() {
SecuritySnapshot s = new SecuritySnapshot();

// lock secure  
try {  
    android.app.KeyguardManager km =  
            (android.app.KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);  
    if (km != null) {  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) s.lockSecure = km.isDeviceSecure();  
        else s.lockSecure = km.isKeyguardSecure();  
    }  
} catch (Throwable ignored) {}  

// patch level  
try {  
    s.securityPatch = Build.VERSION.SECURITY_PATCH;  
} catch (Throwable ignored) {}  

// ADB / dev options  
try {  
    s.adbUsbOn = Settings.Global.getInt(getContentResolver(),  
            Settings.Global.ADB_ENABLED, 0) == 1;  
} catch (Throwable ignored) {}  
try {  
    s.devOptionsOn = Settings.Global.getInt(getContentResolver(),  
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;  
} catch (Throwable ignored) {}  

// ADB Wi-Fi (port property)  
try {  
    String adbPort = System.getProperty("service.adb.tcp.port", "");  
    if (adbPort != null && !adbPort.trim().isEmpty()) {  
        int p = Integer.parseInt(adbPort.trim());  
        s.adbWifiOn = (p > 0);  
    }  
} catch (Throwable ignored) {}  

// Root suspicion (no root needed)  
s.rootSuspected = detectRootFast();  

// test-keys check  
try {  
    String tags = Build.TAGS;  
    s.testKeys = (tags != null && tags.contains("test-keys"));  
} catch (Throwable ignored) {}  

return s;

}

private boolean detectRootFast() {
try {
// SU paths
String[] paths = {
"/system/bin/su", "/system/xbin/su", "/sbin/su",
"/system/app/Superuser.apk", "/system/app/Magisk.apk",
"/data/adb/magisk", "/vendor/bin/su"
};
for (String p : paths) if (new File(p).exists()) return true;

// Magisk / SuperSU packages  
    PackageManager pm = getPackageManager();  
    String[] pkgs = {  
            "com.topjohnwu.magisk",  
            "eu.chainfire.supersu",  
            "com.noshufou.android.su",  
            "com.koushikdutta.superuser"  
    };  
    for (String pkg : pkgs) {  
        try {  
            pm.getPackageInfo(pkg, 0);  
            return true;  
        } catch (Throwable ignored) {}  
    }  
} catch (Throwable ignored) {}  
return false;

}

private static class PrivacySnapshot {
int userAppsWithLocation;
int userAppsWithMic;
int userAppsWithCamera;
int userAppsWithSms;
int totalUserAppsChecked;
}

private PrivacySnapshot readPrivacySnapshot() {
PrivacySnapshot p = new PrivacySnapshot();
try {
PackageManager pm = getPackageManager();
List<android.content.pm.PackageInfo> packs =
pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

if (packs == null) return p;  

    for (android.content.pm.PackageInfo pi : packs) {  
        if (pi == null || pi.applicationInfo == null) continue;  
        ApplicationInfo ai = pi.applicationInfo;  

        // skip system apps  
        if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;  

        p.totalUserAppsChecked++;  

        String[] req = pi.requestedPermissions;  
        int[] flags = pi.requestedPermissionsFlags;  
        if (req == null || flags == null) continue;  

        boolean loc = false, mic = false, cam = false, sms = false;  

        for (int i = 0; i < req.length; i++) {  
            boolean granted = (flags[i] & android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;  
            if (!granted) continue;  
            String perm = req[i];  

            if (perm == null) continue;  
            if (perm.contains("ACCESS_FINE_LOCATION") || perm.contains("ACCESS_COARSE_LOCATION"))  
                loc = true;  
            if (perm.contains("RECORD_AUDIO"))  
                mic = true;  
            if (perm.contains("CAMERA"))  
                cam = true;  
            if (perm.contains("READ_SMS") || perm.contains("RECEIVE_SMS") || perm.contains("SEND_SMS"))  
                sms = true;  
        }  

        if (loc) p.userAppsWithLocation++;  
        if (mic) p.userAppsWithMic++;  
        if (cam) p.userAppsWithCamera++;  
        if (sms) p.userAppsWithSms++;  
    }  

} catch (Throwable ignored) {}  
return p;

}

// ------------------------- SCORING --------------------------

private int scoreThermals(float maxT, float avgT) {
int s = 100;
if (maxT >= 70) s -= 60;
else if (maxT >= 60) s -= 40;
else if (maxT >= 50) s -= 20;

if (avgT >= 55) s -= 25;  
else if (avgT >= 45) s -= 10;  

return clampScore(s);

}

private int scoreBattery(float battTemp, float battPct, boolean charging) {
int s = 100;

if (battTemp >= 55) s -= 55;  
else if (battTemp >= 45) s -= 30;  
else if (battTemp >= 40) s -= 15;  

if (!charging && battPct >= 0) {  
    if (battPct < 15) s -= 25;  
    else if (battPct < 30) s -= 10;  
}  

return clampScore(s);

}

private int scoreStorage(int pctFree, long totalBytes) {
int s = 100;
if (pctFree < 5) s -= 60;
else if (pctFree < 10) s -= 40;
else if (pctFree < 15) s -= 25;
else if (pctFree < 20) s -= 10;

// tiny storage penalty (<32GB)  
long gb = totalBytes / (1024L * 1024L * 1024L);  
if (gb > 0 && gb < 32) s -= 10;  

return clampScore(s);

}

private int scoreApps(int userApps, int totalApps) {
int s = 100;
if (userApps > 140) s -= 50;
else if (userApps > 110) s -= 35;
else if (userApps > 80) s -= 20;
else if (userApps > 60) s -= 10;

if (totalApps > 220) s -= 10;  
return clampScore(s);

}

private int scoreRam(int pctFree) {
int s = 100;
if (pctFree < 8) s -= 60;
else if (pctFree < 12) s -= 40;
else if (pctFree < 18) s -= 20;
else if (pctFree < 25) s -= 10;
return clampScore(s);
}

private int scoreStability(long upMs) {
int s = 100;
if (upMs < 30 * 60 * 1000L) s -= 50;          // <30min uptime
else if (upMs < 2 * 60 * 60 * 1000L) s -= 25; // <2h
else if (upMs > 10L * 24L * 60L * 60L * 1000L) s -= 10; // >10d
return clampScore(s);
}

private int scoreSecurity(SecuritySnapshot sec) {
int s = 100;

if (!sec.lockSecure) s -= 30;  

// old patch  
if (sec.securityPatch != null && sec.securityPatch.length() >= 4) {  
    // rough heuristic: if patch year < current year-2 => penalty  
    try {  
        int y = Integer.parseInt(sec.securityPatch.substring(0, 4));  
        int curY = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);  
        if (y <= curY - 3) s -= 30;  
        else if (y == curY - 2) s -= 15;  
    } catch (Throwable ignored) {}  
} else {  
    s -= 5; // unknown  
}  

if (sec.adbUsbOn) s -= 25;  
if (sec.adbWifiOn) s -= 35;  
if (sec.devOptionsOn) s -= 10;  

if (sec.rootSuspected) s -= 40;  
if (sec.testKeys) s -= 15;  

return clampScore(s);

}

private int scorePrivacy(PrivacySnapshot pr) {
int s = 100;

// weighted dangerous perms on user apps  
int risk = 0;  
risk += pr.userAppsWithLocation * 2;  
risk += pr.userAppsWithMic * 3;  
risk += pr.userAppsWithCamera * 3;  
risk += pr.userAppsWithSms * 4;  

if (risk > 80) s -= 60;  
else if (risk > 50) s -= 40;  
else if (risk > 25) s -= 20;  
else if (risk > 10) s -= 10;  

return clampScore(s);

}

// ------------------------- UTIL ----------------------------

private boolean isChargingNow() {
try {
Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
int status = i != null ? i.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
return (status == BatteryManager.BATTERY_STATUS_CHARGING ||
status == BatteryManager.BATTERY_STATUS_FULL);
} catch (Throwable ignored) {}
return false;
}

private float maxOf(Float a, Float b, Float c, Float d, float e) {
float m = e;
if (a != null && a > m) m = a;
if (b != null && b > m) m = b;
if (c != null && c > m) m = c;
if (d != null && d > m) m = d;
return m;
}

private float avgOf(Float a, Float b, Float c, Float d, float e) {
float sum = e;
int n = 1;
if (a != null) { sum += a; n++; }
if (b != null) { sum += b; n++; }
if (c != null) { sum += c; n++; }
if (d != null) { sum += d; n++; }
return sum / n;
}

private int clampScore(int s) {
if (s < 0) return 0;
if (s > 100) return 100;
return s;
}

private String colorFlagFromScore(int s) {
if (s >= 80) return "🟩";
if (s >= 55) return "🟨";
return "🟥";
}

private String finalVerdict(int health, int sec, int priv, int perf) {
int worst = Math.min(Math.min(health, sec), Math.min(priv, perf));
if (worst >= 80)
return "🟩 Device is healthy — no critical issues detected.";
if (worst >= 55)
return "🟨 Device has moderate risks — recommend service check.";
return "🟥 Device is NOT healthy — immediate servicing recommended.";
}

private String fmt1(float v) {
return String.format(Locale.US, "%.1f", v);
}

// ============================================================
// LAB 29 — FINAL TECHNICIAN SUMMARY (READ-ONLY)
// Does NOT modify GELServiceLog — only reads it.
// Exports via ServiceReportActivity.
// ============================================================
private void lab29FinalSummary() {

    logLine();
    logInfo("LAB 29 — Final Technician Summary (READ-ONLY)");

    // ------------------------------------------------------------
    // 1) READ FULL LOG (from all labs)
    // ------------------------------------------------------------
    String fullLog = GELServiceLog.getAll();

    if (fullLog.trim().isEmpty()) {
        logWarn("No diagnostic data found. Please run Manual Tests first.");
        return;
    }

    // ------------------------------------------------------------
    // 2) FILTER WARNINGS & ERRORS ONLY
    // ------------------------------------------------------------
    String[] lines = fullLog.split("\n");
    StringBuilder warnings = new StringBuilder();

    for (String l : lines) {
        String low = l.toLowerCase(Locale.US);

        if (low.contains("⚠") || low.contains("warning")) {
            warnings.append(l).append("\n");
        }
        if (low.contains("❌") || low.contains("error")) {
            warnings.append(l).append("\n");
        }
    }

    // ------------------------------------------------------------
    // 3) PRINT SUMMARY TO UI (ONLY)
    // ------------------------------------------------------------
    logInfo("===== FINAL TECHNICIAN SUMMARY =====");

    if (warnings.length() == 0) {
        logOk("No warnings or errors detected.");
    } else {
        logWarn("Warnings / Errors detected:");
        for (String w : warnings.toString().split("\n")) {
            if (!w.trim().isEmpty()) {
                logWarn(w.trim());
            }
        }
    }

    logLine();
    logInfo("To export the official PDF report, use the button below.");

    // Enable existing export button (do NOT create new)
    enableSingleExportButton();
}

// ============================================================
// ENABLE EXISTING EXPORT BUTTON — No duplicates!
// ============================================================
private void enableSingleExportButton() {

    ui.post(() -> {
        View rootView = scroll.getChildAt(0);
        if (!(rootView instanceof LinearLayout)) return;

        LinearLayout root = (LinearLayout) rootView;

        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);

            if (v instanceof Button) {
                Button b = (Button) v;

                if ("Export Service Report".contentEquals(b.getText())) {
                    b.setEnabled(true);
                    b.setAlpha(1f);
                }
            }
        }
    });
}

/* ============================================================
   Earpiece test tone — 220Hz (CALL PATH SAFE)
   ============================================================ */
private void playEarpieceTestTone220Hz(int durationMs) {
    try {
        int sampleRate = 8000;
        int samples = (int) ((durationMs / 1000f) * sampleRate);
        if (samples <= 0) samples = sampleRate / 2;

        short[] buffer = new short[samples];
        double freq = 220.0;

        for (int i = 0; i < samples; i++) {
            double t = i / (double) sampleRate;
            buffer[i] = (short) (Math.sin(2 * Math.PI * freq * t) * 9000);
        }

        AudioTrack track;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            track = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build(),
                    new AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    buffer.length * 2,
                    AudioTrack.MODE_STATIC,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
            );
        } else {
            track = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    buffer.length * 2,
                    AudioTrack.MODE_STATIC
            );
        }

        track.write(buffer, 0, buffer.length);
        track.play();

        SystemClock.sleep(durationMs + 80);

        try { track.stop(); } catch (Throwable ignored) {}
        try { track.release(); } catch (Throwable ignored) {}

    } catch (Throwable ignored) {}
}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 6006) { // LAB 6 — Touch Grid
        if (resultCode == RESULT_OK)
            logOk("LAB 6 — Touch grid passed (all zones responsive)");
        else
            logError("LAB 6 — Touch grid failed (dead zone suspected)");

        enableSingleExportButton();
        return;
    }

    if (requestCode == 7007) { // LAB 7 — Rotation
        if (resultCode == RESULT_OK)
            logOk("LAB 7 — Rotation detected via sensors");
        else
            logError("LAB 7 — No rotation detected");

        enableSingleExportButton();
        return;
    }

    if (requestCode == 8008) { // LAB 8 — Proximity
        if (resultCode == RESULT_OK)
            logOk("LAB 8 — Proximity sensor responded correctly");
        else
            logError("LAB 8 — No proximity response detected");

        enableSingleExportButton();
    }
}

// ============================================================
// END OF CLASS
// ============================================================
        }
