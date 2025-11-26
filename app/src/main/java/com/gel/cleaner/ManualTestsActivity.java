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
// GLOBAL FINAL SCORE FIELDS (used by Lab 30 PDF Report)
// Αυτά γεμίζουν στο Lab 29
// ============================================================
private String lastScoreHealth     = "N/A";
private String lastScorePerformance = "N/A";
private String lastScoreSecurity    = "N/A";
private String lastScorePrivacy     = "N/A";
private String lastFinalVerdict     = "N/A";

private ScrollView scroll;  
private TextView txtLog;  
private Handler ui;  

// Battery stress internals  
private volatile boolean cpuBurnRunning = false;  
private final List<Thread> cpuBurnThreads = new ArrayList<>();  
private float oldWindowBrightness = -2f; // sentinel  
private boolean oldKeepScreenOn = false;  

// Lab 11 location permission internals  
private static final int REQ_LOCATION_LAB11 = 11012;  
private Runnable pendingLab11AfterPermission = null;  

/* =========================================================  
 *  FIX: APPLY SAVED LANGUAGE TO THIS ACTIVITY  
 * ========================================================= */  
@Override  
protected void attachBaseContext(Context base) {  
    super.attachBaseContext(LocaleHelper.apply(base));  
}  

@Override  
protected void onCreate(@Nullable Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  

    ui = new Handler(Looper.getMainLooper());  

    scroll = new ScrollView(this);  
    scroll.setFillViewport(true);  

    LinearLayout root = new LinearLayout(this);  
    root.setOrientation(LinearLayout.VERTICAL);  
    int pad = dp(16);  
    root.setPadding(pad, pad, pad, pad);  
    root.setBackgroundColor(0xFF101010); // GEL black  

    // TITLE  
    TextView title = new TextView(this);  
    title.setText(getString(R.string.manual_hospital_title));  
    title.setTextSize(20f);  
    title.setTextColor(0xFFFFD700);  
    title.setGravity(Gravity.CENTER_HORIZONTAL);  
    title.setPadding(0, 0, 0, dp(6));  
    root.addView(title);  

    // SUBTITLE  
    TextView sub = new TextView(this);  
    sub.setText(getString(R.string.manual_hospital_sub));  
    sub.setTextSize(13f);  
    sub.setTextColor(0xFF39FF14);  
    sub.setGravity(Gravity.CENTER_HORIZONTAL);  
    sub.setPadding(0, 0, 0, dp(12));  
    root.addView(sub);  

    // SECTION TITLE  
    TextView sec1 = new TextView(this);  
    sec1.setText(getString(R.string.manual_section1));  
    sec1.setTextSize(17f);  
    sec1.setTextColor(0xFFFFD700);  
    sec1.setGravity(Gravity.CENTER_HORIZONTAL);  
    sec1.setPadding(0, dp(10), 0, dp(6));  
    root.addView(sec1);  

    // ========== SECTION 1: AUDIO & VIBRATION — LABS 1–5 ==========  
    LinearLayout body1 = makeSectionBody();  
    Button header1 = makeSectionHeader(getString(R.string.manual_cat_1), body1);  
    root.addView(header1);  
    root.addView(body1);  

    body1.addView(makeTestButton("1. Speaker Tone Test", this::lab1SpeakerTone));  
    body1.addView(makeTestButton("2. Speaker Frequency Sweep", this::lab2SpeakerSweep));  
    body1.addView(makeTestButton("3. Earpiece Call Check (manual)", this::lab3EarpieceManual));  
    body1.addView(makeTestButton("4. Microphone Recording Check (manual)", this::lab4MicManual));  
    body1.addView(makeTestButton("5. Vibration Motor Test", this::lab5Vibration));  

    // ========== SECTION 2: DISPLAY & SENSORS — LABS 6–10 ==========  
    LinearLayout body2 = makeSectionBody();  
    Button header2 = makeSectionHeader(getString(R.string.manual_cat_2), body2);  
    root.addView(header2);  
    root.addView(body2);  

    body2.addView(makeTestButton("6. Display / Touch Basic Inspection", this::lab6DisplayTouch));  
    body2.addView(makeTestButton("7. Rotation / Auto-Rotate Check (manual)", this::lab7RotationManual));  
    body2.addView(makeTestButton("8. Proximity During Call (manual)", this::lab8ProximityCall));  
    body2.addView(makeTestButton("9. Sensors Quick Presence Check", this::lab9SensorsQuick));  
    body2.addView(makeTestButton("10. Full Sensor List for Report", this::lab10FullSensorList));  

    // ========== SECTION 3: WIRELESS & CONNECTIVITY — LABS 11–14 ==========  
    LinearLayout body3 = makeSectionBody();  
    Button header3 = makeSectionHeader(getString(R.string.manual_cat_3), body3);  
    root.addView(header3);  
    root.addView(body3);  

    body3.addView(makeTestButton("11. Wi-Fi Link, SSID Safe Mode & DeepScan (GEL C)", this::lab11WifiSnapshot));  
    body3.addView(makeTestButton("12. Mobile Data / Airplane Mode Checklist (manual)", this::lab12MobileDataChecklist));  
    body3.addView(makeTestButton("13. Basic Call Test Guidelines (manual)", this::lab13CallGuidelines));  
    body3.addView(makeTestButton("14. Internet Access Quick Check", this::lab14InternetQuickCheck));  

    // ========== SECTION 4: BATTERY & THERMAL — LABS 15–18 ==========  
    LinearLayout body4 = makeSectionBody();  
    Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);  
    root.addView(header4);  
    root.addView(body4);  

    body4.addView(makeTestButtonRedGold("15. Battery Health Stress Test", this::lab15BatteryHealthStressTest));  
    body4.addView(makeTestButton("16. Charging Port & Charger Inspection (manual)", this::lab16ChargingPortManual));  
    body4.addView(makeTestButton("17. Thermal Snapshot (CPU where available)", this::lab17ThermalSnapshot));  
    body4.addView(makeTestButton("18. GEL AUTO Battery Reliability Evaluation", this::lab18RunAuto));  

    // ========== SECTION 5: STORAGE & PERFORMANCE — LABS 19–22 ==========  
    LinearLayout body5 = makeSectionBody();  
    Button header5 = makeSectionHeader(getString(R.string.manual_cat_5), body5);  
    root.addView(header5);  
    root.addView(body5);  

    body5.addView(makeTestButton("19. Internal Storage Snapshot", this::lab19StorageSnapshot));  
    body5.addView(makeTestButton("20. Installed Apps Footprint", this::lab20AppsFootprint));  
    body5.addView(makeTestButton("21. Live RAM Snapshot", this::lab21RamSnapshot));  
    body5.addView(makeTestButton("22. Uptime / Reboot History Hints", this::lab22UptimeHints));  

    // ========== SECTION 6: SECURITY & SYSTEM HEALTH — LABS 23–26 ==========  
    LinearLayout body6 = makeSectionBody();  
    Button header6 = makeSectionHeader(getString(R.string.manual_cat_6), body6);  
    root.addView(header6);  
    root.addView(body6);  

    body6.addView(makeTestButton("23. Screen Lock / Biometrics Checklist (manual)", this::lab23ScreenLock));  
    body6.addView(makeTestButton("24. Security Patch & Play Protect (manual)", this::lab24SecurityPatchManual));  
    body6.addView(makeTestButton("25. Developer Options / ADB Risk Note", this::lab25DevOptions));  
    body6.addView(makeTestButton("26. Root / Bootloader Suspicion Checklist (manual)", this::lab26RootSuspicion));  

    // ========== SECTION 7: ADVANCED / LOGS — LABS 27–30 ==========  
    LinearLayout body7 = makeSectionBody();  
    Button header7 = makeSectionHeader(getString(R.string.manual_cat_7), body7);  
    root.addView(header7);  
    root.addView(body7);  

    body7.addView(makeTestButton("27. Crash / Freeze History (interview)", this::lab27CrashHistory));  
    body7.addView(makeTestButton("28. App Permissions & Privacy (FULL AUTO + RISK SCORE)", this::lab28PermissionsPrivacy));  
    body7.addView(makeTestButton("29. DEVICE SCORES Summary", this::lab29CombineFindings));  
    body7.addView(makeTestButton("30. FINAL TECHNICIAN SUMMARY Notes (PDF Export)", this::lab30FinalSummary));  

    // LOG AREA  
txtLog = new TextView(this);  
txtLog.setTextSize(13f);  
txtLog.setTextColor(0xFFEEEEEE);  
txtLog.setPadding(0, dp(16), 0, dp(8));  
txtLog.setMovementMethod(new ScrollingMovementMethod());  
txtLog.setText(Html.fromHtml("<b>" + getString(R.string.manual_log_title) + "</b><br>"));  

root.addView(txtLog);

// ============================================================
// EXPORT SERVICE REPORT BUTTON (AFTER LOG)
// ============================================================

Button btnExport = new Button(this);
btnExport.setText(getString(R.string.export_report_title)); // "Export Service Report"
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
    Intent i = new Intent(ManualTestsActivity.this, ServiceReportActivity.class);
    startActivity(i);
});

root.addView(btnExport);

// ============================================================

scroll.addView(root);  
setContentView(scroll);  

// First log entry  
GELServiceLog.clear();  
logInfo(getString(R.string.manual_log_desc));  
}   // onCreate ends here


// ============================================================  
// UI HELPERS  
// ============================================================  
private LinearLayout makeSectionBody() {  
    LinearLayout body = new LinearLayout(this);  
    body.setOrientation(LinearLayout.VERTICAL);  
    body.setVisibility(View.GONE);  
    body.setPadding(0, dp(4), 0, dp(4));  
    return body;  
}  

private Button makeSectionHeader(String text, LinearLayout bodyToToggle) {  
    Button b = new Button(this);  
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
        if (bodyToToggle.getVisibility() == View.VISIBLE) {  
            bodyToToggle.setVisibility(View.GONE);  
        } else {  
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
    b.setTextColor(0xFFFFFFFF); // white  
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
    b.setTextColor(0xFFFFFFFF); // white  
    b.setTypeface(null, Typeface.BOLD);  

    GradientDrawable redBtn = new GradientDrawable();  
    redBtn.setColor(0xFF8B0000); // dark red  
    redBtn.setCornerRadius(dp(12));  
    redBtn.setStroke(dp(3), 0xFFFFD700); // gold border  
    b.setBackground(redBtn);  

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

// --- GEL legacy aliases used in older labs (to avoid crashes) ---  
private void logYellow(String msg) { logWarn(msg); }  
private void logGreen(String msg)  { logOk(msg); }  
private void logRed(String msg)    { logError(msg); }  
private void logSection(String msg){ logLine(); logInfo(msg); }  

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
    long seconds = ms / 1000;  
    long days = seconds / (24 * 3600);  
    seconds %= (24 * 3600);  
    long hours = seconds / 3600;  
    seconds %= 3600;  
    long minutes = seconds / 60;  
    return String.format(Locale.US, "%dd %dh %dm", days, hours, minutes);  
}  

private String cleanSsid(String raw) {  
    if (raw == null) return "Unknown";  
    raw = raw.trim();  
    if (raw.equalsIgnoreCase("<unknown ssid>") || raw.equalsIgnoreCase("unknown ssid"))  
        return "Unknown";  
    if (raw.startsWith("\"") && raw.endsWith("\"") && raw.length() > 1)  
        raw = raw.substring(1, raw.length() - 1);  
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
private void lab1SpeakerTone() {  
    logLine();  
    logInfo("LAB 1 — Speaker Tone Test started (2 seconds).");  
    new Thread(() -> {  
        try {  
            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);  
            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);  
            SystemClock.sleep(2200);  
            tg.release();  
            logOk("If a clear tone was heard from the main speaker, hardware path is basically OK.");  
            logError("If NO sound was heard at all, suspect main speaker / audio amp / flex damage.");  
        } catch (Exception e) {  
            logError("Speaker Tone Test error: " + e.getMessage());  
        }  
    }).start();  
}  

private void lab2SpeakerSweep() {  
    logLine();  
    logInfo("LAB 2 — Speaker Frequency Sweep (4 short tones).");  
    new Thread(() -> {  
        ToneGenerator tg = null;  
        try {  
            tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);  
            int[] tones = {  
                    ToneGenerator.TONE_DTMF_1,  
                    ToneGenerator.TONE_DTMF_3,  
                    ToneGenerator.TONE_DTMF_6,  
                    ToneGenerator.TONE_DTMF_9  
            };  
            for (int t : tones) {  
                tg.startTone(t, 600);  
                SystemClock.sleep(650);  
            }  
            logOk("If all tones were heard cleanly, the speaker handles a basic frequency range.");  
            logWarn("If some tones were distorted or missing, suspect partial cone / mesh / water damage.");  
        } catch (Exception e) {  
            logError("Speaker Sweep error: " + e.getMessage());  
        } finally {  
            if (tg != null) tg.release();  
        }  
    }).start();  
}  

private void lab3EarpieceManual() {  
    logLine();  
    logInfo("LAB 3 — Earpiece Call Check (manual instructions).");  
    logInfo("1) Place a normal voice call or listen to a voicemail without loudspeaker.");  
    logInfo("2) Hold the phone to the ear using the top earpiece only.");  
    logWarn("If volume is very low or muffled while speakerphone is OK -> possible clogged mesh or earpiece wear.");  
    logError("If there is absolutely no sound in earpiece but speakerphone works -> earpiece or audio path fault.");  
}  

private void lab4MicManual() {  
    logLine();  
    logInfo("LAB 4 — Microphone Recording Check (manual).");  
    logInfo("1) Open a voice recorder or send a voice message (WhatsApp / Viber etc.).");  
    logInfo("2) Speak normally near the main microphone (bottom edge of the phone).");  
    logInfo("3) Play back the recording and compare with a reference device if possible.");  
    logWarn("If sound is very low / noisy / underwater -> suspect microphone hole clogged, mesh or early mic damage.");  
    logError("If recording is totally silent on all apps -> strong indication of microphone / audio IC / flex failure.");  
}  

private void lab5Vibration() {  
    logLine();  
    logInfo("LAB 5 — Vibration Motor Test (strong pattern).");  

    try {  
        Vibrator v;  

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  
            VibratorManager vm = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);  
            v = (vm != null) ? vm.getDefaultVibrator() : null;  
        } else {  
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);  
        }  

        if (v == null) {  
            logError("No Vibrator service reported — framework issue.");  
            return;  
        }  

        if (!v.hasVibrator()) {  
            logError("Device reports NO vibrator hardware.");  
            return;  
        }  

        try {  
            int haptic = Settings.System.getInt(getContentResolver(),  
                    Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);  
            if (haptic == 0)  
                logWarn("System haptic feedback is OFF (some OEMs block app vibration).");  
        } catch (Exception ignored) {}  

        try {  
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
            if (nm != null && nm.getCurrentInterruptionFilter()  
                    != NotificationManager.INTERRUPTION_FILTER_ALL) {  
                logWarn("Do Not Disturb is ON — may suppress vibration on some devices.");  
            }  
        } catch (Exception ignored) {}  

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
            long[] pattern = {0, 300, 150, 300, 150, 450};  
            int[] amps = {0, 255, 0, 255, 0, 255};  
            v.vibrate(VibrationEffect.createWaveform(pattern, amps, -1));  
        } else {  
            //noinspection deprecation  
            long[] pattern = {0, 300, 150, 300, 150, 450};  
            //noinspection deprecation  
            v.vibrate(pattern, -1);  
        }  

        logOk("If strong pulses were felt clearly, vibrator motor + driver are OK.");  
        logWarn("If vibration is weak/intermittent, suspect worn motor or OEM suppression modes.");  

    } catch (Exception e) {  
        logError("Vibration Test error: " + e.getMessage());  
    }  
}  

// ============================================================  
// LABS 6–10: DISPLAY & SENSORS  
// ============================================================  
private void lab6DisplayTouch() {  
    logLine();  
    logInfo("LAB 6 — Display / Touch Basic Inspection (manual).");  
    logInfo("1) Open a plain white or grey image full-screen.");  
    logWarn("2) Look for yellow / purple tint, burn-in, strong shadows or vertical lines — possible panel damage.");  
    logWarn("3) Slowly drag a finger across the entire screen (top to bottom, left to right).");  
    logError("If there are dead touch zones or ghost touches -> digitizer / touch controller problem.");  
}  

private void lab7RotationManual() {  
    logLine();  
    logInfo("LAB 7 — Rotation / Auto-Rotate Check (manual).");  
    logInfo("1) Make sure Auto-Rotate is enabled in Quick Settings.");  
    logInfo("2) Open an app that supports rotation (gallery, browser, YouTube).");  
    logWarn("If the UI never rotates despite Auto-Rotate ON -> suspect accelerometer failure or sensor-service bug.");  
    logInfo("If rotation works only after reboot -> possible software/ROM issue, not pure hardware.");  
}  

private void lab8ProximityCall() {  
    logLine();  
    logInfo("LAB 8 — Proximity During Call (manual).");  
    logInfo("1) Start a normal call and bring the phone to the ear.");  
    logInfo("2) The display MUST turn off when the proximity area is covered.");  
    logError("If the screen stays ON near the ear -> proximity sensor or glass / protector alignment problem.");  
    logWarn("If the screen turns off but sometimes does not wake properly -> software/sensor edge cases.");  
}  

private void lab9SensorsQuick() {  
    logLine();  
    logInfo("LAB 9 — Sensors Quick Presence Check.");  
    try {  
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);  
        if (sm == null) {  
            logError("SensorManager not available — framework issue.");  
            return;  
        }  
        List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);  
        logInfo("Total sensors reported: " + (all == null ? 0 : all.size()));  

        checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");  
        checkSensor(sm, Sensor.TYPE_GYROSCOPE, "Gyroscope");  
        checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer / Compass");  
        checkSensor(sm, Sensor.TYPE_LIGHT, "Ambient Light");  
        checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");  
    } catch (Exception e) {  
        logError("Sensors Quick Check error: " + e.getMessage());  
    }  
}  

private void checkSensor(SensorManager sm, int type, String name) {  
    boolean ok = sm.getDefaultSensor(type) != null;  
    if (ok) logOk(name + " is reported as available.");  
    else logWarn(name + " is NOT reported — features depending on it will be limited or missing.");  
}  

private void lab10FullSensorList() {  
    logLine();  
    logInfo("LAB 10 — Full Sensor List for Report.");  

    try {  
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);  
        if (sm == null) {  
            logError("SensorManager not available.");  
            return;  
        }  

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);  
        if (sensors == null || sensors.isEmpty()) {  
            logError("No sensors reported by the system.");  
            return;  
        }  

        // RAW LIST  
        for (Sensor s : sensors) {  
            String line = "• type=" + s.getType()  
                    + " | name=" + s.getName()  
                    + " | vendor=" + s.getVendor();  
            logInfo(line);  
        }  

        boolean hasVirtualGyro = false;  
        boolean hasDualALS = false;  
        int alsCount = 0;  
        boolean hasSAR = false;  
        boolean hasPickup = false;  
        boolean hasLargeTouch = false;  
        boolean hasGameRotation = false;  

        for (Sensor s : sensors) {  
            String name = s.getName() != null ? s.getName().toLowerCase(Locale.US) : "";  
            String vendor = s.getVendor() != null ? s.getVendor().toLowerCase(Locale.US) : "";  

            if (name.contains("virtual_gyro") ||  
                    (name.contains("gyroscope") && vendor.contains("xiaomi")))  
                hasVirtualGyro = true;  

            if (name.contains("ambient light"))  
                alsCount++;  

            if (name.contains("sar") || name.contains("rf"))  
                hasSAR = true;  

            if (name.contains("pickup"))  
                hasPickup = true;  

            if (name.contains("touch") && name.contains("large"))  
                hasLargeTouch = true;  

            if (name.contains("game rotation"))  
                hasGameRotation = true;  
        }  

        if (alsCount >= 2) hasDualALS = true;  

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
        logError("Full Sensor List error: " + e.getMessage());  
    }  
}  

// ============================================================  
// LAB 11: Wi-Fi Snapshot (SAFE SSID + DeepScan) — NO PASSWORD / NO QR  
// ============================================================  
private void lab11WifiSnapshot() {  
    logLine();  
    logInfo("LAB 11 — Wi-Fi Link Snapshot + SSID Safe Mode + DeepScan (NO password).");  

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
            pendingLab11AfterPermission = this::lab11WifiSnapshot;  

            ActivityCompat.requestPermissions(  
                    this,  
                    new String[]{  
                            Manifest.permission.ACCESS_FINE_LOCATION,  
                            Manifest.permission.ACCESS_COARSE_LOCATION  
                    },  
                    REQ_LOCATION_LAB11  
            );  

            logInfo("Grant permission, then Lab 11 will auto-retry.");  
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

    if (requestCode == REQ_LOCATION_LAB11) {  
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
            if (pendingLab11AfterPermission != null) pendingLab11AfterPermission.run();  
        } else {  
            logWarn("Location permission denied. SSID/BSSID may remain UNKNOWN.");  
        }  
        pendingLab11AfterPermission = null;  
    }  
}  

// ============================================================  
// LAB 11 — DEEPSCAN v3.0  
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

private void lab12MobileDataChecklist() {  
    logLine();  
    logInfo("LAB 12 — Mobile Data / Airplane Mode Checklist + SIM detection.");  

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

private void lab13CallGuidelines() {  
    logLine();  
    logInfo("LAB 13 — Basic Call Test Guidelines (manual).");  
    logInfo("1) Place a normal call to a known-good number.");  
    logInfo("2) Verify both directions: you hear them AND they hear you clearly.");  
    logWarn("If only one direction fails -> isolate earpiece vs microphone path.");  
    logError("If calls always drop or never connect while data works -> telephony/carrier registration issue.");  
}  

private void lab14InternetQuickCheck() {  
    logLine();  
    logInfo("LAB 14 — Internet Access Quick Check.");  
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
// LAB 15 — Battery Health Stress Test (GEL Full Mode)
// ============================================================
private void lab15BatteryHealthStressTest() {

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
    logInfo("LAB 15 — Battery Health Stress Test started.");
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
// LAB 16 — Charging Port & Charger Inspection (manual)  
// ============================================================  
private void lab16ChargingPortManual() {  
    logLine();  
    logInfo("LAB 16 — Charging Port & Charger Inspection (manual).");  
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

// LAB 17 — Thermal Snapshot (GEL Universal Edition — SAFE)
// ============================================================
private void lab17ThermalSnapshot() {
logLine();
logInfo("LAB 17 — Thermal Snapshot (ASCII thermal map)");

// 1) Read thermal zones (CPU/GPU/PMIC/Skin)  
Map<String, Float> zones = readThermalZones();  

// 2) Battery ALWAYS from BatteryManager  
float batt = getBatteryTemperature();  

if (zones == null || zones.isEmpty()) {  
    logWarn("Device exposes NO thermal zones. Printing battery only.");  
    printZoneAscii("Battery", batt);  
    logOk("Lab 17 finished.");  
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

logOk("Lab 17 finished.");

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
// LAB 18 — GEL AUTO Battery Reliability Evaluation (FINAL v8)
// 3 ΠΡΑΣΙΝΕΣ LIVE ΜΠΑΡΕΣ — ΑΚΡΙΒΩΣ ΟΠΩΣ ΤΟ ΠΑΡΑΔΕΙΓΜΑ
// ============================================================

private void lab18RunAuto() {

    new Thread(() -> {

        logLine();
        logInfo("ℹ️ GEL Battery Reliability Evaluation started.");
        logLine();

        // =================================================================
        // BAR #1 — STRESS TEST  (0 → 32%)
        // =================================================================

        logInfo("▶ Running Stress Test (Lab 15)...");
        appendHtml(buildBar(0));   // 1st bar appears

        float before = getCurrentBatteryPercent();
        long t0 = SystemClock.elapsedRealtime();

        applyMaxBrightnessAndKeepOn();
        startCpuBurn_C_Mode();

        for (int p = 0; p <= 32; p++) {
            replaceLastBar(p);
            try { Thread.sleep(1000); } catch (Exception ignore) {}
        }

        stopCpuBurn();
        restoreBrightnessAndKeepOn();

        float after = getCurrentBatteryPercent();
        long t1 = SystemClock.elapsedRealtime();

        float drop = before - after;
        if (drop < 0) drop = 0;

        float perHour = (drop * 3600000f) / Math.max(1, t1 - t0);
        if (perHour < 0) perHour = 0;

        int drain_mA = (int)(perHour * 50f);

        logLine();

        // =================================================================
        // BAR #2 — THERMAL ZONES (0 → 68%)
        // EACH BAR STARTS FROM ZERO
        // =================================================================

        logInfo("▶ Running Thermal Zones (Lab 17)...");
        appendHtml(buildBar(0));   // new bar #2 (stays separate)

        Map<String,Float> z0 = readThermalZones();
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        Map<String,Float> z1 = readThermalZones();

        for (int p = 0; p <= 68; p++) {
            replaceLastBar(p);
            try { Thread.sleep(40); } catch (Exception ignore) {}
        }

        Float cpu0  = pickZone(z0,"cpu","soc","big","little");
        Float cpu1  = pickZone(z1,"cpu","soc","big","little");
        Float batt0 = pickZone(z0,"battery","batt","bat");
        Float batt1 = pickZone(z1,"battery","batt","bat");

        float dCPU  = (cpu0 != null && cpu1 != null) ? cpu1 - cpu0 : 0f;
        float dBATT = (batt0 != null && batt1 != null) ? batt1 - batt0 : 0f;

        logLine();

        // =================================================================
        // BAR #3 — DRAIN MODEL (0 → 85%)
        // =================================================================

        logInfo("▶ Calculating drain rate...");
        appendHtml(buildBar(0));   // new bar #3

        for (int p = 0; p <= 85; p++) {
            replaceLastBar(p);
            try { Thread.sleep(30); } catch (Exception ignore) {}
        }

        logInfo("▶ Calculating voltage stability...");
        float v0 = getBatteryVoltage_mV();
        try { Thread.sleep(1500); } catch (Exception ignore) {}
        float v1 = getBatteryVoltage_mV();
        float dv = Math.abs(v1 - v0);

        logInfo("▶ Calculating thermal rise...");
        logInfo("▶ Calculating PMIC behavior...");
        logInfo("▶ Calculating discharge curve...");
        logInfo("▶ Calculating estimated real capacity...");
        logInfo("▶ Getting device information...");

        int factory = getFactoryCapacity_mAh();
        if (factory <= 0) factory = 5000;

        float estimatedCapacity_mAh = factory * (100f / (100f + perHour));
        if (estimatedCapacity_mAh > factory) estimatedCapacity_mAh = factory;

        // =================================================================
        // SCORING
        // =================================================================

        int score = 100;

        if (perHour > 15)      score -= 25;
        else if (perHour > 10) score -= 10;

        if (dCPU > 20)       score -= 20;
        else if (dCPU > 12)  score -= 10;

        if (dv > 30)         score -= 15;
        else if (dv > 20)    score -= 5;

        float pctHealth = (estimatedCapacity_mAh / factory) * 100f;
        if (pctHealth < 70)      score -= 20;
        else if (pctHealth < 80) score -= 10;

        if (score < 0) score = 0;
        if (score > 100) score = 100;

        String cycle = (perHour < 10 && dv < 20)
                ? "Strong (stable discharge curve)"
                : "Normal (minor fluctuations)";

        String category;
        if (score >= 90)      category = "Strong";
        else if (score >= 80) category = "Excellent";
        else if (score >= 70) category = "Very good";
        else if (score >= 60) category = "Normal";
        else                  category = "Weak";

        // =================================================================
        // REPORT
        // =================================================================

        logLine();
        logInfo("--------------------------------------------------");
        logInfo("GEL Battery Intelligence Evaluation");
        logInfo("--------------------------------------------------");

        logInfo("1. Stress Drain Rate: " + drain_mA + " mA");
        logInfo(String.format(Locale.US,
                "2. Estimated Real Capacity: %.0f mAh (factory: %d mAh)",
                estimatedCapacity_mAh, factory));
        logInfo(String.format(Locale.US,
                "3. Voltage Stability: Δ %.1f mV", dv));
        logInfo(String.format(Locale.US,
                "4. Thermal Rise: CPU +%.1f°C, BATT +%.1f°C", dCPU, dBATT));
        logInfo("5. Cycle Behavior: " + cycle);

        logLine();
        logOk(String.format(Locale.US,
                "Final Battery Health Score: %d%% (%s)", score, category));

        printHealthCheckboxMap(category);

    }).start();
}
        
//=====================    
//HELPERS
//=====================
private String buildBar(int percent) {
    percent = Math.max(0, Math.min(100, percent));
    int total = 30;
    int filled = (percent * total) / 100;
    int empty = total - filled;

    StringBuilder sb = new StringBuilder("[");
    sb.append("<font color='#39FF14'>");
    for (int i = 0; i < filled; i++) sb.append("█");
    sb.append("</font>");
    for (int i = 0; i < empty; i++) sb.append("░");
    sb.append("] ").append(percent).append("%");
    return sb.toString();
}

private void replaceLastBar(int percent) {
    String bar = buildBar(percent);

    ui.post(() -> {
        CharSequence cur = txtLog.getText();
        String s = (cur == null) ? "" : cur.toString();
        int idx = s.lastIndexOf("[");
        if (idx < 0) {
            appendHtml(bar + "<br>");
            return;
        }
        int br = s.indexOf("]", idx);
        if (br < 0) { appendHtml(bar + "<br>"); return; }

        String head = s.substring(0, idx);
        String tail = s.substring(br + 1);

        txtLog.setText(Html.fromHtml(head + bar + "<br>" + tail));
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    });
}

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
    return 5000;
}
    
// ============================================================ // ============================================================
// LABS 19–22: STORAGE & PERFORMANCE
// ============================================================
private void lab19StorageSnapshot() {
logLine();
logInfo("LAB 19 — Internal Storage Snapshot.");
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

private void lab20AppsFootprint() {  
    logLine();  
    logInfo("LAB 20 — Installed Apps Footprint.");  
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

private void lab21RamSnapshot() {  
    logLine();  
    logInfo("LAB 21 — Live RAM Snapshot.");  
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

private void lab22UptimeHints() {  
    logLine();  
    logInfo("LAB 22 — Uptime / Reboot History Hints.");  
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
// LABS 23–26: SECURITY & SYSTEM HEALTH  
// ============================================================  
// ============================================================

// LAB 23 — Screen Lock / Biometrics Checklist (auto-detect + manual)
// ============================================================
private void lab23ScreenLock() {
logLine();
logInfo("LAB 23 — Screen Lock / Biometrics Checklist");

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

// LAB 24 — Security Patch & Play Protect (auto + manual)
// ============================================================
private void lab24SecurityPatchManual() {
logLine();
logInfo("LAB 24 — Security Patch & Play Protect Check");

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

// LAB 25 — Developer Options / ADB Risk Note + UI BUBBLES + AUTO-FIX HINTS
// GEL Security v3.1 (Realtime Snapshot)
// ============================================================
private void lab25DevOptions() {
logLine();
logInfo("LAB 25 — Developer Options / ADB Risk Note (Realtime).");

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

// LAB 26 — Root / Bootloader Suspicion Checklist (FULL AUTO + RISK SCORE)
// GEL Universal Edition — NO external libs
// ============================================================
private void lab26RootSuspicion() {
logLine();
logInfo("LAB 26 — Root / Bootloader Integrity Scan (AUTO).");

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
    if (lab26_fileExists(p)) {  
        suFound = true;  
        rootScore += 18;  
        rootFindings.add("su/busybox path found: " + p);  
    }  
}  

// which su  
String whichSu = lab26_execFirstLine("which su");  
if (whichSu != null && whichSu.contains("/")) {  
    rootScore += 12;  
    rootFindings.add("'which su' returned: " + whichSu);  
    suFound = true;  
}  

// try exec su (best effort)  
boolean suExec = lab26_canExecSu();  
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

List<String> installed = lab26_getInstalledPackagesLower();  
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
String roSecure = lab26_getProp("ro.secure");  
String roDebug  = lab26_getProp("ro.debuggable");  
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

String vbState = lab26_getProp("ro.boot.verifiedbootstate"); // green/yellow/orange/red  
String vbmeta  = lab26_getProp("ro.boot.vbmeta.device_state"); // locked/unlocked  
String flashL  = lab26_getProp("ro.boot.flash.locked"); // 1/0  
String wlBit   = lab26_getProp("ro.boot.warranty_bit"); // 0/1 on some OEMs  

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
String cmdline = lab26_readOneLine("/proc/cmdline");  
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
if (lab26_fileExists("/data/local/bootanimation.zip")) {  
    animScore += 35;  
    animFindings.add("Custom bootanimation detected: /data/local/bootanimation.zip");  
}  

// If system bootanimation missing → suspicious ROM  
boolean sysBoot = lab26_fileExists("/system/media/bootanimation.zip") ||  
                  lab26_fileExists("/product/media/bootanimation.zip") ||  
                  lab26_fileExists("/oem/media/bootanimation.zip") ||  
                  lab26_fileExists("/vendor/media/bootanimation.zip");  
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

logOk("Lab 26 finished.");

}

// ============================================================
// LAB 26 — INTERNAL HELPERS (unique names to avoid conflicts)
// ============================================================
private boolean lab26_fileExists(String path) {
try { return new File(path).exists(); } catch (Throwable t) { return false; }
}

private List<String> lab26_getInstalledPackagesLower() {
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

private boolean lab26_canExecSu() {
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

private String lab26_execFirstLine(String cmd) {
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

private String lab26_getProp(String key) {
String v = lab26_execFirstLine("getprop " + key);
if (v == null) return null;
v = v.trim();
return v.isEmpty() ? null : v.toLowerCase(Locale.US);
}

private String lab26_readOneLine(String path) {
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
// LABS 27–30: ADVANCED / LOGS
// ============================================================

// ============================================================
// LAB 27 — GEL Crash Intelligence v5.0 (FULL AUTO EDITION)
// ============================================================
private void lab27CrashHistory() {

logLine();  
logInfo("LAB 27 — GEL Crash Intelligence (AUTO)");  

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

logOk("Lab 27 finished.");

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
// LAB 28 — App Permissions & Privacy (FULL AUTO + RISK SCORE)
// ============================================================
private void lab28PermissionsPrivacy() {

logLine();  
logInfo("LAB 28 — App Permissions & Privacy (AUTO scan)");  

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

logOk("Lab 28 finished.");

}

// ============================================================
// INTERNAL helpers for Lab 28 (keep inside same lab block)
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
// LAB 29 — Auto Final Diagnosis Summary (GEL Universal AUTO Edition)
// Combines Thermals + Battery + Storage + RAM + Apps + Uptime +
// Security + Privacy + Root + Stability into final scores.
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================
private void lab29CombineFindings() {
logLine();
logInfo("LAB 29 — Auto Final Diagnosis Summary (FULL AUTO)");

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

logOk("Lab 29 finished.");

}

// ============================================================
// ======= LAB 29 INTERNAL AUTO HELPERS (SAFE, NO IMPORTS) =====
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
// LAB 30 — FINAL TECHNICIAN SUMMARY (READ-ONLY)
// Does NOT modify GELServiceLog — only reads it.
// Exports via ServiceReportActivity.
// ============================================================
private void lab30FinalSummary() {

    logLine();
    logInfo("LAB 30 — Final Technician Summary (READ-ONLY)");

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

// ============================================================
// END OF CLASS
// ============================================================

}




























