// ============================================================
// ManualTestsActivity
// GEL Manual Tests â€” Hospital Edition (30 Manual Labs)
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

// ============================================================
// ANDROID â€” CORE
// ============================================================
import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

// ============================================================
// ANDROIDX
// ============================================================
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

// ============================================================
// JAVA â€” IO / NET
// ============================================================
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

// ============================================================
// JAVA â€” UTIL
// ============================================================
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
// LAB 15 â€” FLAGS (DO NOT MOVE)
// ============================================================
private volatile boolean lab15FlapUnstable = false;
private volatile boolean lab15OverTempDuringCharge = false;
private volatile boolean lab15Running = false;

private AlertDialog lab15Dialog;
private TextView lab15StatusText;
private LinearLayout lab15ProgressBar;
private Button lab15ExitBtn;
private TextView lab15CounterText;
// LAB 15 â€” Thermal Correlation
private float lab15BattTempStart = Float.NaN;
private float lab15BattTempPeak  = Float.NaN;
private float lab15BattTempEnd   = Float.NaN;

private static final int LAB15_TOTAL_SECONDS = 180;

// ============================================================
// TELEPHONY SNAPSHOT â€” Passive system probe (no side effects)
// ============================================================
private static class TelephonySnapshot {

    boolean airplaneOn = false;

    int simState = TelephonyManager.SIM_STATE_UNKNOWN;
    boolean simReady = false;

    int serviceState = ServiceState.STATE_OUT_OF_SERVICE;
    boolean inService = false;

    int dataState = TelephonyManager.DATA_UNKNOWN;

    boolean hasInternet = false;
}

// ================= SNAPSHOTS CONTAINERS =================

private static class StorageSnapshot {
    long totalBytes, freeBytes, usedBytes;
    int pctFree;
}

private static class AppsSnapshot {
    int userApps, systemApps, totalApps;
}

private static class RamSnapshot {
    long totalBytes, freeBytes;
    int pctFree;
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

private static class PrivacySnapshot {
    int userAppsWithLocation;
    int userAppsWithMic;
    int userAppsWithCamera;
    int userAppsWithSms;
    int totalUserAppsChecked;
}

private static class BatteryInfo {
    int level = -1;
    float temperature = Float.NaN;
    String status = "Unknown";

    // ðŸ”¥ REQUIRED â€” used by LAB 14 / drain logic
    long currentChargeMah = -1;

    // capacity estimation
    long estimatedFullMah = -1;
    String source = "Unknown";
}

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

// ------------------------------------------------------------
// DOTS (running indicator)
// ------------------------------------------------------------
lab14DotsView = new TextView(this);
lab14DotsView.setText("â€¢");
lab14DotsView.setTextSize(22f);
lab14DotsView.setTextColor(0xFF39FF14);
lab14DotsView.setPadding(0, dp(6), 0, dp(10));
lab14DotsView.setGravity(Gravity.CENTER_HORIZONTAL);
root.addView(lab14DotsView);

    // ============================================================
    // SECTION 1: AUDIO & VIBRATION â€” LABS 1â€“5
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
    // SECTION 2: DISPLAY & SENSORS â€” LABS 6â€“9
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
// SECTION 3: WIRELESS & CONNECTIVITY â€” LABS 10â€“13
// ============================================================
LinearLayout body3 = makeSectionBody();
Button header3 = makeSectionHeader(getString(R.string.manual_cat_3), body3);
root.addView(header3);
root.addView(body3);

body3.addView(makeTestButton("10. Wi-Fi Link Snapshot",this::lab10WifiSnapshot));
body3.addView(makeTestButton("11. Mobile Network Diagnostic",this::lab11MobileDataDiagnostic));
body3.addView(makeTestButton("12. Call Function Interpretation",this::lab12CallFunctionInterpretation));
body3.addView(makeTestButton("13. Internet Access Quick Check",this::lab13InternetQuickCheck));

    // ============================================================
    // SECTION 4: BATTERY & THERMAL â€” LABS 14â€“17
    // ============================================================
    LinearLayout body4 = makeSectionBody();
    Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);
    root.addView(header4);
    root.addView(body4);

    body4.addView(makeTestButtonRedGold("14. Battery Health Stress Test", this::lab14BatteryHealthStressTest));
    body4.addView(makeTestButton("15. Charging System Diagnostic (Smart)", this::lab15ChargingSystemSmart));
    body4.addView(makeTestButton("16. Thermal Snapshot", this::lab16ThermalSnapshot));
    body4.addView(makeTestButton("17. AUTO Battery Reliability", this::lab17RunAuto));

    // ============================================================
    // SECTION 5: STORAGE & PERFORMANCE â€” LABS 18â€“21
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
    // SECTION 6: SECURITY & SYSTEM HEALTH â€” LABS 22â€“25
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
    // SECTION 7: ADVANCED / LOGS â€” LABS 26â€“29
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
// GEL Battery Temperature Reader (Universal)
// ============================================================
private float getBatteryTemperature() {
    try {
        Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i == null) return 0f;

        int raw = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        if (raw <= 0) return 0f;

        return raw / 10f; // tenths of Â°C
    } catch (Exception e) {
        return 0f;
    }
}

// ============================================================
// LAB 14 â€” FINAL BATTERY HEALTH JUDGE (THERMAL WEIGHTED)
// GEL EDITION â€” LOCKED / PRODUCTION
// ============================================================
//
// INPUTS (ALREADY COMPUTED BY LAB 14 CORE):
//  - baseScore        : int    (0â€“100)
//  - mahPerHour       : double (discharge rate under stress)
//  - healthPct        : int    (estimated capacity %)
//  - battTempBefore   : Float  (Â°C, start)
//  - battTempAfter    : Float  (Â°C, end)
//  - battTempPeak     : Float  (Â°C, peak, optional)
//
// OUTPUT:
//  - Final Battery Health Score
//  - Category
//  - Thermal Verdict line
//
// ============================================================

private void logFinalBatteryHealthScore(
        int baseScore,
        double mahPerHour,
        int healthPct,
        Float battTempBefore,
        Float battTempAfter,
        Float battTempPeak
) {

    int score = baseScore;

    // --------------------------------------------------------
    // 1ï¸âƒ£ DRAIN PENALTIES (STRESS BEHAVIOR)
    // --------------------------------------------------------
    if (mahPerHour > 900)       score -= 30;
    else if (mahPerHour > 750)  score -= 20;
    else if (mahPerHour > 600)  score -= 10;

    // --------------------------------------------------------
    // 2ï¸âƒ£ CAPACITY PENALTIES
    // --------------------------------------------------------
    if (healthPct > 0) {
        if (healthPct < 60)       score -= 25;
        else if (healthPct < 70)  score -= 15;
        else if (healthPct < 80)  score -= 8;
    }

    // --------------------------------------------------------
    // 3ï¸âƒ£ THERMAL ANALYSIS (VERDICT + WEIGHT)
    // --------------------------------------------------------
    float dT = 0f;
    if (battTempBefore != null) {
        float ref = battTempBefore;
        float end = (battTempPeak != null)
                ? battTempPeak
                : (battTempAfter != null ? battTempAfter : ref);
        dT = end - ref;
    }

    String thermalVerdict;
    int thermalPenalty = 0;

    if (dT <= 4f) {
        thermalVerdict = "OK";
        thermalPenalty = 0;
    } else if (dT <= 7f) {
        thermalVerdict = "Warm";
        thermalPenalty = 5;
    } else {
        thermalVerdict = "Risk";
        thermalPenalty = (dT > 10f ? 15 : 10);
    }

    score -= thermalPenalty;

    // --------------------------------------------------------
    // CLAMP
    // --------------------------------------------------------
    if (score < 0)   score = 0;
    if (score > 100) score = 100;

    // --------------------------------------------------------
    // CATEGORY
    // --------------------------------------------------------
    String category;
    if (score >= 90)       category = "Strong";
    else if (score >= 80)  category = "Excellent";
    else if (score >= 70)  category = "Very good";
    else if (score >= 60)  category = "Normal";
    else                   category = "Weak";

    // --------------------------------------------------------
    // LOG OUTPUT (LAB STYLE)
    // --------------------------------------------------------
    logLine();

    logInfo(String.format(
            Locale.US,
            "Thermal Verdict: %s (Î”T +%.1fÂ°C)",
            thermalVerdict,
            dT
    ));

    if (thermalPenalty > 0) {
        logWarn(String.format(
                Locale.US,
                "Thermal Weight Applied: -%d%%",
                thermalPenalty
        ));
    } else {
        logOk("Thermal Weight Applied: none");
    }

    logLine();

    logOk(String.format(
            Locale.US,
            "Final Battery Health Score: %d%% (%s)",
            score,
            category
    ));
}

// ============================================================
// LAB 14 â€” FINAL BATTERY HEALTH SCORE (Judge Layer)
// ============================================================
private void logFinalBatteryHealthScore(
        double mahPerHour,
        int healthPct,
        Float battTempAfter,
        Float battTempBefore
) {

    int score = 100;

    if (mahPerHour > 900)       score -= 30;
    else if (mahPerHour > 750)  score -= 20;
    else if (mahPerHour > 600)  score -= 10;

    if (battTempAfter != null && battTempBefore != null) {
        double dT = battTempAfter - battTempBefore;
        if (dT > 10)       score -= 20;
        else if (dT > 7)   score -= 10;
    }

    if (healthPct > 0) {
        if (healthPct < 60)       score -= 25;
        else if (healthPct < 70)  score -= 15;
        else if (healthPct < 80)  score -= 8;
    }

    score = Math.max(0, Math.min(100, score));

    String category;
    if (score >= 90)      category = "Strong";
    else if (score >= 75) category = "Very good";
    else if (score >= 60) category = "Normal";
    else                  category = "Weak";

    logLine();
    logOk(String.format(Locale.US,
            "Final Battery Health Score: %d%% (%s)",
            score, category));
}

// ------------------------------------------------------------
// Call when LAB 14 fully ends
// ------------------------------------------------------------
private void dismissLab14RunningDialog() {
    try {
        if (lab14Dialog != null && lab14Dialog.isShowing()) {
            lab14Dialog.dismiss();
        }
    } catch (Throwable ignore) {}
    finally {
        lab14Dialog = null;
        lab14ProgressText = null;
        lab14ProgressBar = null;
    }
}

// ------------------------------------------------------------
// LAB 14 â€” USER ABORT (CANCEL / EXIT)
// ------------------------------------------------------------
private void abortLab14ByUser() {

    logWarn("LAB 14 aborted by user.");

    try { stopCpuBurn(); } catch (Throwable ignore) {}
    try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

    dismissLab14RunningDialog();

    lab14Running = false;

    logInfo("Battery stress test was cancelled before completion.");
}

// ------------------------------------------------------------
// Reliable charging detection (plugged-based)
// ------------------------------------------------------------
private boolean isDeviceCharging() {
    try {
        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );
        if (i == null) return false;

        int plugged = i.getIntExtra(
                BatteryManager.EXTRA_PLUGGED, 0
        );

        return plugged == BatteryManager.BATTERY_PLUGGED_AC
            || plugged == BatteryManager.BATTERY_PLUGGED_USB
            || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;

    } catch (Throwable t) {
        return false;
    }
}

// ============================================================
// LAB 3 â€” User Confirmation Dialog (Earpiece)
// ============================================================
private void askUserEarpieceConfirmation() {

    ui.post(() -> {
        try {
            AlertDialog.Builder b =
                    new AlertDialog.Builder(
                            ManualTestsActivity.this,
                            android.R.style.Theme_Material_Dialog_NoActionBar
                    );

            b.setTitle("LAB 3 â€” Confirm");
            b.setMessage("Did you hear the sound clearly from the earpiece?");
            b.setCancelable(false);

            b.setPositiveButton("YES", (d, w) -> {
                logOk("User confirmed earpiece audio was audible");
                enableSingleExportButton();
            });

            b.setNegativeButton("NO", (d, w) -> {
                logWarn("Earpiece signal detected but user did not hear sound clearly");
                enableSingleExportButton();
            });

            AlertDialog dialog = b.create();
            dialog.show();

            // ðŸ”¥ GEL DARK STYLE (NO XML)
            try {
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(0xFF101010)
                );

                TextView title = dialog.findViewById(
                        getResources().getIdentifier(
                                "alertTitle", "id", "android")
                );
                if (title != null)
                    title.setTextColor(0xFFFFFFFF);

                TextView msg = dialog.findViewById(android.R.id.message);
                if (msg != null)
                    msg.setTextColor(0xFFEEEEEE);

                Button yes = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button no  = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                if (yes != null) yes.setTextColor(0xFFFFD700);
                if (no  != null) no.setTextColor(0xFFFFD700);

            } catch (Throwable ignore) {}

        } catch (Throwable t) {
            enableSingleExportButton();
        }
    });
}

private TelephonySnapshot getTelephonySnapshot() {

    TelephonySnapshot s = new TelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode
    // ------------------------------------------------------------
    try {
        s.airplaneOn = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0
        ) == 1;
    } catch (Exception ignored) {}

    TelephonyManager tm =
            (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

    if (tm != null) {

        // --------------------------------------------------------
        // SIM state
        // --------------------------------------------------------
        try {
            s.simState = tm.getSimState();
            s.simReady = (s.simState == TelephonyManager.SIM_STATE_READY);
        } catch (Exception ignored) {}

        // --------------------------------------------------------
        // Service state (SAFE DEFAULT FIRST)
        // --------------------------------------------------------
        s.serviceState = ServiceState.STATE_OUT_OF_SERVICE;
        s.inService = false;

        try {
            ServiceState ss = tm.getServiceState();
            if (ss != null) {
                s.serviceState = ss.getState();
                s.inService =
                        (s.serviceState == ServiceState.STATE_IN_SERVICE);
            }
        } catch (Exception ignored) {}

        // --------------------------------------------------------
        // Data state
        // --------------------------------------------------------
        try {
            s.dataState = tm.getDataState();
        } catch (Exception ignored) {}
    }

    // ------------------------------------------------------------
    // Internet context (best effort, informational only)
    // ------------------------------------------------------------
    ConnectivityManager cm =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

    if (cm != null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network n = cm.getActiveNetwork();
                NetworkCapabilities caps =
                        cm.getNetworkCapabilities(n);
                s.hasInternet = caps != null &&
                        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                @SuppressWarnings("deprecation")
                NetworkInfo ni = cm.getActiveNetworkInfo();
                s.hasInternet = ni != null && ni.isConnected();
            }
        } catch (Exception ignored) {}
    }

    return s;
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
    appendHtml("â„¹ï¸ " + escape(msg));
}

private void logOk(String msg) {
    GELServiceLog.ok(msg);
    appendHtml("<font color='#88FF88'>âœ… " + escape(msg) + "</font>");
}

private void logWarn(String msg) {
    GELServiceLog.warn(msg);
    appendHtml("<font color='#FFD966'>âš ï¸ " + escape(msg) + "</font>");
}

private void logError(String msg) {
    GELServiceLog.error(msg);
    appendHtml("<font color='#FF5555'>âŒ " + escape(msg) + "</font>");
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
    appendHtml("----------------------------------------");
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
    if (raw.equalsIgnoreCase("<unknown ssid>") ||
        raw.equalsIgnoreCase("unknown ssid"))
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
// LAB 14 â€” REQUIRED SUPPORT METHODS (DO NOT MODIFY)
// ============================================================

private float getCurrentBatteryPercent() {
    try {
        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );
        if (i == null) return -1f;

        int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level < 0 || scale <= 0) return -1f;
        return (level * 100f) / scale;

    } catch (Throwable t) {
        return -1f;
    }
}

// ------------------------------------------------------------
// Brightness + keep screen on (LAB stress)
// ------------------------------------------------------------

private int __oldBrightness = -1;

private void applyMaxBrightnessAndKeepOn() {
    try {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        if (__oldBrightness < 0) {
            __oldBrightness = Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    128
            );
        }

        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    } catch (Throwable ignore) {}
}

private void restoreBrightnessAndKeepOn() {
    try {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        if (__oldBrightness >= 0) {
            lp.screenBrightness = __oldBrightness / 255f;
            getWindow().setAttributes(lp);
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    } catch (Throwable ignore) {}
}

// ------------------------------------------------------------
// CPU stress (controlled)
// ------------------------------------------------------------

private volatile boolean __cpuBurn = false;

private void startCpuBurn_C_Mode() {
    __cpuBurn = true;

    new Thread(() -> {
        try {
            while (__cpuBurn) {
                double x = 0;
                for (int i = 0; i < 100_000; i++) {
                    x += Math.sqrt(i);
                }
            }
        } catch (Throwable ignore) {}
    }, "LAB14-CPU-BURN").start();
}

private void stopCpuBurn() {
    __cpuBurn = false;
}

// ------------------------------------------------------------
// UI health map (fallback)
// ------------------------------------------------------------

private void printHealthCheckboxMap(String decision) {
    logLine();

    if ("Strong".equalsIgnoreCase(decision)) {
        logOk("Health Map: âœ” Battery âœ” Thermal âœ” Drain");
    } else if ("Normal".equalsIgnoreCase(decision)) {
        logWarn("Health Map: âš  Moderate wear detected");
    } else {
        logError("Health Map: âœ– Battery health critical");
    }
}

// ----------------------------------------------------
// PEAK TEMPERATURE TRACKING (CHARGING-ONLY)
// ----------------------------------------------------
private void updateChargingPeakTemperature() {

    if (!isChargingNow()) return;

    float tNow = getBatteryTemperature();
    if (tNow > 0 && !Float.isNaN(tNow)) {
        if (Float.isNaN(lab15BattTempPeak) || tNow > lab15BattTempPeak) {
            lab15BattTempPeak = tNow;
        }
    }
}

// ============================================================
// THERMAL HELPERS â€” REQUIRED (SCAN + FALLBACK + FORMAT)
// ============================================================

private boolean isRooted = false; // fix: missing symbol

// ------------------------------------------------------------
// Safe long read
// ------------------------------------------------------------
private long readLongSafe(File f) {
    BufferedReader br = null;
    try {
        if (f == null || !f.exists()) return Long.MIN_VALUE;
        br = new BufferedReader(new FileReader(f));
        String line = br.readLine();
        if (line == null) return Long.MIN_VALUE;
        line = line.trim();
        if (line.isEmpty()) return Long.MIN_VALUE;
        return Long.parseLong(line);
    } catch (Throwable ignored) {
        return Long.MIN_VALUE;
    } finally {
        try { if (br != null) br.close(); } catch (Throwable ignored) {}
    }
}

// ------------------------------------------------------------
// Thermal group accumulator
// ------------------------------------------------------------
private static class ThermalGroupReading {
    float max = Float.NaN;
    float avg = Float.NaN;
    int count = 0;

    void add(float v) {
        if (Float.isNaN(v)) return;
        if (Float.isNaN(max) || v > max) max = v;
        avg = Float.isNaN(avg) ? v : ((avg * count + v) / (count + 1));
        count++;
    }
}

// ------------------------------------------------------------
// Thermal summary container (fix: missing fields)
// ------------------------------------------------------------
private static class ThermalSummary {
    int zoneCount = 0;
    int coolingDeviceCount = 0;
}

// ------------------------------------------------------------
// Scan thermal hardware (zones + cooling devices) and feed groups
// ------------------------------------------------------------
private ThermalSummary scanThermalHardware(
        ThermalGroupReading batteryMain,
        ThermalGroupReading batteryShell,
        ThermalGroupReading pmic,
        ThermalGroupReading charger,
        ThermalGroupReading modemMain,
        ThermalGroupReading modemAux
) {
    ThermalSummary s = new ThermalSummary();

    try {
        File base = new File("/sys/class/thermal");
        File[] all = base.listFiles();
        if (all == null) return s;

        for (File z : all) {
            if (z == null) continue;

            String name = z.getName();
            if (name == null) continue;

            // cooling devices count
            if (name.startsWith("cooling_device")) {
                s.coolingDeviceCount++;
                continue;
            }

            // zones
            if (!name.startsWith("thermal_zone")) continue;

            s.zoneCount++;

            String type = null;
            try {
                type = readFirstLineSafe(new File(z, "type"));
            } catch (Throwable ignored) {}

            long milli = readLongSafe(new File(z, "temp"));
            if (milli == Long.MIN_VALUE) continue;

            float t = (float) milli;

            // autoscale (milli/centi/deci)
            if (t > 1000f) t /= 1000f;
            else if (t > 200f) t /= 100f;
            else if (t > 20f) t /= 10f;

            if (!isValidTemp(t)) continue;

            String key = (type != null ? type : name).toLowerCase(Locale.US);

            // simple routing rules
            if (key.contains("batt") || key.contains("battery"))
                batteryMain.add(t);
            else if (key.contains("skin") || key.contains("shell"))
                batteryShell.add(t);
            else if (key.contains("pmic") || key.contains("pmi"))
                pmic.add(t);
            else if (key.contains("chg") || key.contains("charger") || key.contains("usb"))
                charger.add(t);
            else if (key.contains("modem") || key.contains("mdm") || key.contains("baseband"))
                modemMain.add(t);
            else if (key.contains("rf") || key.contains("pa") || key.contains("qcom"))
                modemAux.add(t);
        }
    } catch (Throwable ignored) {}

    return s;
}

// ------------------------------------------------------------
// Fallbacks (safe no-crash). Keeps compilation + avoids NaN chaos.
// ------------------------------------------------------------
private void applyThermalFallbacks(
        ThermalGroupReading batteryMain,
        ThermalGroupReading batteryShell,
        ThermalGroupReading pmic,
        ThermalGroupReading charger,
        ThermalGroupReading modemMain,
        ThermalGroupReading modemAux
) {
    // If main battery missing, try shell as proxy
    if ((batteryMain == null || batteryMain.count == 0) && batteryShell != null && batteryShell.count > 0) {
        if (batteryMain != null) {
            batteryMain.max = batteryShell.max;
            batteryMain.avg = batteryShell.avg;
            batteryMain.count = batteryShell.count;
        }
    }

    // If modemMain missing, try modemAux
    if ((modemMain == null || modemMain.count == 0) && modemAux != null && modemAux.count > 0) {
        if (modemMain != null) {
            modemMain.max = modemAux.max;
            modemMain.avg = modemAux.avg;
            modemMain.count = modemAux.count;
        }
    }
}

// ------------------------------------------------------------
// Formatter
// ------------------------------------------------------------
private String formatThermalLine(String label, ThermalGroupReading g) {
    if (g == null || g.count <= 0) {
        return String.format(Locale.US, "%-18s : N/A\n", label);
    }
    float mx = g.max;
    float av = g.avg;
    if (Float.isNaN(mx) && Float.isNaN(av)) {
        return String.format(Locale.US, "%-18s : N/A\n", label);
    }
    if (Float.isNaN(av)) av = mx;
    if (Float.isNaN(mx)) mx = av;

    return String.format(
            Locale.US,
            "%-18s : max %.1fÂ°C | avg %.1fÂ°C | n=%d\n",
            label, mx, av, g.count
    );
}

// ============================================================
// MISSING HELPERS â€” REQUIRED FOR THERMAL / LAB LOGIC
// SAFE STUBS â€” PRODUCTION COMPATIBLE
// ============================================================

// ------------------------------------------------------------
// 1) Temperature sanity check
// ------------------------------------------------------------
private boolean isValidTemp(float t) {
    return !Float.isNaN(t) && t > -20f && t < 120f;
}

// ------------------------------------------------------------
// 2) Safe file read (first line)
// ------------------------------------------------------------
private String readFirstLineSafe(File f) {
    BufferedReader br = null;
    try {
        if (f == null || !f.exists()) return null;
        br = new BufferedReader(new FileReader(f));
        return br.readLine();
    } catch (Throwable ignored) {
        return null;
    } finally {
        try { if (br != null) br.close(); } catch (Throwable ignored) {}
    }
}

// ------------------------------------------------------------
// 4) Cooling devices enumeration
// ------------------------------------------------------------
private void appendHardwareCoolingDevices(StringBuilder sb) {
    try {
        File base = new File("/sys/class/thermal");
        File[] files = base.listFiles();
        if (files == null) return;

        sb.append("\nCooling devices:\n");
        for (File f : files) {
            if (f != null && f.getName().startsWith("cooling_device")) {
                sb.append("â€¢ ").append(f.getName()).append("\n");
            }
        }
    } catch (Throwable ignored) {}
}

private void logLab15ThermalCorrelation() {
    logLab15ThermalCorrelation(Float.NaN, Float.NaN, Float.NaN);
}

// ------------------------------------------------------------
// LAB 15 â€” Thermal Correlation Logger
// ------------------------------------------------------------

private void logLab15ThermalCorrelation(
        float battTempStart,
        float battTempPeak,
        float battTempEnd
) {

    if (Float.isNaN(battTempStart) || Float.isNaN(battTempEnd)) {
        logWarn("Thermal correlation: insufficient data.");
        return;
    }

    // --------------------------------------------------------
    // Î”T calculation (start â†’ peak/end)
    // --------------------------------------------------------
    float rawDelta = !Float.isNaN(battTempPeak)
            ? (battTempPeak - battTempStart)
            : (battTempEnd - battTempStart);

    float dPeak = Math.max(0f, rawDelta);

    // --------------------------------------------------------
    // VERDICT (LAB STYLE)
    // --------------------------------------------------------
    String verdict;
    String note;

    if (dPeak <= 4.0f) {
        verdict = "OK";
        note = "Normal thermal behavior during charging";
    } else if (dPeak <= 7.0f) {
        verdict = "Warm";
        note = "Elevated thermal load during charging";
    } else {
        verdict = "Risk";
        note = "Abnormal thermal rise detected";
    }

    // --------------------------------------------------------
    // LOG OUTPUT
    // --------------------------------------------------------
    logLine();

    logInfo(String.format(
            Locale.US,
            "Thermal correlation (charging): start %.1fÂ°C â†’ peak %.1fÂ°C â†’ end %.1fÂ°C",
            battTempStart,
            (Float.isNaN(battTempPeak) ? battTempEnd : battTempPeak),
            battTempEnd
    ));

    logOk(String.format(
            Locale.US,
            "Thermal verdict (charging): %s (Î”T +%.1fÂ°C) â€” %s",
            verdict,
            dPeak,
            note
    ));
}

// ============================================================
// CHARGING REQUIRED DIALOG â€” SAFE DISMISS
// ============================================================
protected void dismissChargingDialogSafe(
        Context ctx,
        AlertDialog dialog,
        BroadcastReceiver receiver
) {
    try {
        if (receiver != null) {
            ctx.unregisterReceiver(receiver);
        }
    } catch (Throwable ignore) {}

    try {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    } catch (Throwable ignore) {}
}

// ============================================================
// DOTS ANIMATION HELPER (OPTIONAL)
// ============================================================
protected void advanceDots(TextView dotsView, int step) {
    if (dotsView == null) return;
    String[] frames = { "â€¢", "â€¢ â€¢", "â€¢ â€¢ â€¢" };
    dotsView.setText(frames[step % frames.length]);
}

// ============================================================
// LOGGER STUBS (EXPECTED FROM HOST ACTIVITY)
// ============================================================
// These are expected to exist in ManualTestsActivity:
//
// void logLine()
// void logInfo(String s)
// void logOk(String s)
// void logWarn(String s)
//
// ============================================================


// ============================================================
// LAB 15 â€” Charging Required Dialog (LIVE STATUS PANEL)
// GEL EDITION â€” Blocking + Animated + Color State
// ============================================================

private AlertDialog chargingDialog;
private TextView chargingTitleView;
private TextView chargingMsgView;
private TextView chargingDotsView;
private BroadcastReceiver chargingReceiver;

private volatile boolean chargingDetected = false;

// ============================================================
// SHOW DIALOG â€” WAIT FOR CHARGING (PHASE 1)
// ============================================================
private void showChargingRequiredDialogWithLiveStatus(Runnable onChargingDetected) {

    ui.post(() -> {
        try {
            AlertDialog.Builder b =
                    new AlertDialog.Builder(
                            ManualTestsActivity.this,
                            android.R.style.Theme_Material_Dialog_NoActionBar
                    );
            b.setCancelable(false);

            // -------------------------
            // ROOT LAYOUT
            // -------------------------
            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(dp(24), dp(20), dp(24), dp(20));
            root.setBackgroundColor(0xFF101010);

            // -------------------------
            // TITLE
            // -------------------------
            chargingTitleView = new TextView(this);
            chargingTitleView.setText("Lab 15 â€” Connect the charger to the device charging port");
            chargingTitleView.setTextColor(0xFFFFFFFF);
            chargingTitleView.setTextSize(18f);
            chargingTitleView.setPadding(0, 0, 0, dp(12));
            root.addView(chargingTitleView);

            // -------------------------
            // MESSAGE
            // -------------------------
            chargingMsgView = new TextView(this);
            chargingMsgView.setText("Connect the charger to the device charging port");
            chargingMsgView.setTextColor(0xFFDDDDDD);
            chargingMsgView.setTextSize(14f);
            chargingMsgView.setPadding(0, 0, 0, dp(12));
            root.addView(chargingMsgView);

// -------------------------
// ANIMATED DOTS
// -------------------------
chargingDotsView = new TextView(this);
chargingDotsView.setText("â€¢");
chargingDotsView.setTextColor(0xFF39FF14);
chargingDotsView.setTextSize(22f);
chargingDotsView.setGravity(Gravity.CENTER);
chargingDotsView.setPadding(0, 0, 0, dp(4));
root.addView(chargingDotsView);

// -------------------------
// STATUS TEXT (NO COUNTER HERE)
// -------------------------
lab15StatusText = new TextView(this);
lab15StatusText.setText("Monitoring charging systemâ€¦");
lab15StatusText.setTextColor(0xFFDDDDDD);
lab15StatusText.setGravity(Gravity.CENTER);
lab15StatusText.setPadding(0, dp(4), 0, dp(2));
root.addView(lab15StatusText);

// -------------------------
// COUNTER TEXT (POPUP ONLY)
// -------------------------
lab15CounterText = new TextView(this);
lab15CounterText.setText("Progress: 0 / 180 sec");
lab15CounterText.setTextColor(0xFFAAAAAA);
lab15CounterText.setGravity(Gravity.CENTER);
lab15CounterText.setPadding(0, 0, 0, dp(8));
root.addView(lab15CounterText);

            // -------------------------
            // PROGRESS BAR (6 Ã— 30s)
            // -------------------------
            lab15ProgressBar = new LinearLayout(this);
            lab15ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
            lab15ProgressBar.setGravity(Gravity.CENTER);

            for (int i = 0; i < 6; i++) {
                View seg = new View(this);
                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams(0, dp(10), 1f);
                lp.setMargins(dp(3), 0, dp(3), 0);
                seg.setLayoutParams(lp);
                seg.setBackgroundColor(0xFF333333);
                lab15ProgressBar.addView(seg);
            }

            root.addView(lab15ProgressBar);

            b.setView(root);

            chargingDialog = b.create();
            chargingDialog.show();

            startChargingDotsAnimation();

            // -------------------------
            // LIVE CHARGING DETECTOR
            // -------------------------
            chargingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (chargingDetected || !isDeviceCharging())
                        return;

                    chargingDetected = true;
                    lab15Running = true;

                    chargingTitleView.setTextColor(0xFF39FF14);
                    chargingMsgView.setText("Charging connection detected");

                    try {
                        unregisterReceiver(this);
                    } catch (Throwable ignore) {}

                    chargingReceiver = null;
                    ui.postDelayed(onChargingDetected, 300);
                }
            };

            registerReceiver(
                    chargingReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );

        } catch (Throwable ignore) {}
    });
}

// ============================================================
// DOTS ANIMATION ENGINE
// ============================================================
private void startChargingDotsAnimation() {

    ui.post(new Runnable() {

        int step = 0;
        final String[] frames = {"â€¢", "â€¢ â€¢", "â€¢ â€¢ â€¢"};

        @Override
        public void run() {
            if (chargingDotsView == null) return;
            chargingDotsView.setText(frames[step % frames.length]);
            step++;
            ui.postDelayed(this, 500);
        }
    });
}

// ============================================================
// LAB 15 â€” CORE RUNNER (180 sec)
// Battery-based strength estimation (NO mA)
// ============================================================
private static final int LAB15_TOTAL_SEC = 180;
private static final int LAB15_FLAP_WINDOW_SEC = 20;

private void runLab15Core() {

    final long startTs = SystemClock.elapsedRealtime();

    // reset flags ÎºÎ¬Î¸Îµ run (HARD RESET)
    lab15OverTempDuringCharge = false;
    lab15FlapUnstable = false;

    // start flapping monitor (20 sec)
    startChargingFlapMonitor();

    // ------------------------------------------------------------
    // Capture BATTERY INFO at start (Peripherals logic)
    // ------------------------------------------------------------
    final BatteryInfo startInfo = getBatteryInfo();

    final long startChargeMah =
            (startInfo != null && startInfo.currentChargeMah > 0)
                    ? startInfo.currentChargeMah
                    : -1;

    final long startEstimatedFull =
            (startInfo != null && startInfo.estimatedFullMah > 0)
                    ? startInfo.estimatedFullMah
                    : -1;

    ui.post(new Runnable() {
        @Override
        public void run() {

            int elapsed =
                    (int) ((SystemClock.elapsedRealtime() - startTs) / 1000);

            updateLab15Status(elapsed);
            updateLab15Progress(elapsed);

            // passive thermal watch
            if (getBatteryTemperature() >= 47f) {
                lab15OverTempDuringCharge = true;
            }

            if (elapsed < LAB15_TOTAL_SEC) {
                ui.postDelayed(this, 1000);
                return;
            }

            // =====================================================
            // FINAL EVALUATION
            // =====================================================
            logLine();

            // ----------------------------------------------------
            // Final battery snapshot
            // ----------------------------------------------------
            BatteryInfo endInfo = getBatteryInfo();

            final long endChargeMah =
                    (endInfo != null && endInfo.currentChargeMah > 0)
                            ? endInfo.currentChargeMah
                            : -1;

            //   estimated full reference
            final long estimatedFullMah =
                    (endInfo != null && endInfo.estimatedFullMah > 0)
                            ? endInfo.estimatedFullMah
                            : startEstimatedFull;

            // ----------------------------------------------------
            // CORE VERDICT (STABILITY / THERMAL)
            // ----------------------------------------------------
            if (lab15FlapUnstable) {

                logError(
                        "Charging connection is unstable.\n\n" +
                        "Most likely cause:\n" +
                        "• Worn or damaged charging cable\n\n" +
                        "Action:\n" +
                        "• Replace the cable and retry the test\n\n" +
                        "If instability persists with another cable:\n" +
                        "• Charging port may require cleaning or inspection"
                );

            } else if (lab15OverTempDuringCharge) {

                logError(
                        "High battery temperature detected during charging.\n\n" +
                        "This may indicate elevated internal resistance\n" +
                        "or battery degradation."
                );

                logError(
                        "LAB decision:  Battery replacement recommended."
                );

            } else {

                logOk(
                        "Charging behavior stable, for the full test duration.\n" +
                        "No abnormal connection, or thermal issues detected."
                );

                logOk("LAB decision: ✅ Charging system OK.");

            // ----------------------------------------------------
            // ðŸ”‹ CHARGING STRENGTH ESTIMATION (BATTERY-BASED)
            // ----------------------------------------------------
            logLine();

            if (startChargeMah > 0 &&
                endChargeMah > startChargeMah &&
                estimatedFullMah > 0) {

                long deltaMah = endChargeMah - startChargeMah;
                float deltaPct =
                        (deltaMah * 100f) / (float) estimatedFullMah;

                if (deltaPct >= 1.2f) {

                    logOk("Charging strength: STRONG");
                            logInfo("Charging path quality:");
                  logInfo("Charging path quality:");
                  logOk("Fast / high-quality charging path detected.");

                } else if (deltaPct >= 0.6f) {

                    logOk(
                            "Charging strength: NORMAL \n" +
                            "Typical charger and cable behavior."
                    );

                } else if (deltaPct >= 0.3f) {

                    logWarn(
        "Charging strength: MODERATE\n" +
        "Possible causes: low-power charger, USB port limitations, wireless charging, or a degraded cable."
);

                } else {

                    logError(
                            "Charging strength: POOR \n" +
                            "Very low effective charging detected."
                    );
                }

            } else {

                logWarn(
                        "Charging strength: Unable to estimate accurately.\n" +
                        "Charge counter or capacity data unavailable on this device."
                );
            }

            dismissChargingStatusDialog();
        }
    });
}

// ------------------------------------------------------------
// LAB 15 - USER ABORT (CANCEL / EXIT)
// ------------------------------------------------------------
private void abortLab15ByUser() {

    // GUARD - avoid double / early abort
    if (!lab15Running && !chargingDetected) {
        return;
    }

    logWarn("LAB 15 aborted by user.");

    try {
        if (chargingReceiver != null) {
            unregisterReceiver(chargingReceiver);
            chargingReceiver = null;
        }
    } catch (Throwable ignore) {}

    dismissChargingStatusDialog();

    lab15Running = false;
    chargingDetected = false;

    logWarn("Charging system diagnostic was cancelled before completion.");
}

// ============================================================
// FLAPPING MONITOR (20 sec)
// ============================================================
private void startChargingFlapMonitor() {

    final boolean[] last = { isDeviceCharging() };
    final int[] toggles = { 0 };
    final long ts = SystemClock.elapsedRealtime();

    ui.post(new Runnable() {
        @Override
        public void run() {

            boolean now = isDeviceCharging();
            if (now != last[0]) {
                toggles[0]++;
                last[0] = now;
            }

            int elapsed =
                    (int) ((SystemClock.elapsedRealtime() - ts) / 1000);

            if (elapsed < LAB15_FLAP_WINDOW_SEC) {
                ui.postDelayed(this, 1000);
                return;
            }

            lab15FlapUnstable = (toggles[0] >= 3);
        }
    });
}

// ============================================================
// UI HELPERS
// ============================================================
private void updateLab15Status(int sec) {
    if (lab15StatusText != null) {
        lab15StatusText.setText(
                "Monitoring charging systemâ€¦ " + sec + " / 180 sec"
        );
    }
}

private void updateLab15Progress(int sec) {
    if (lab15ProgressBar == null) return;

    int segment = sec / 30;
    for (int i = 0; i < lab15ProgressBar.getChildCount(); i++) {
        View v = lab15ProgressBar.getChildAt(i);
        v.setBackgroundColor(i < segment ? 0xFF39FF14 : 0xFF333333);
    }
}

// ============================================================
// CLOSE PANEL
// ============================================================
private void dismissChargingStatusDialog() {

    try {
        if (chargingReceiver != null) {
            unregisterReceiver(chargingReceiver);
        }
    } catch (Throwable ignore) {}

    try {
        if (chargingDialog != null) {
            chargingDialog.dismiss();
        }
    } catch (Throwable ignore) {}

    chargingDialog = null;
    chargingReceiver = null;
    chargingDotsView = null;
    chargingTitleView = null;
    chargingMsgView = null;
    lab15StatusText = null;
    lab15ProgressBar = null;
    lab15Running = false;
    chargingDetected = false;
}

// ============================================================
// LABS 1-5: AUDIO & VIBRATION
// ============================================================

// ============================================================
// LAB 1 - Speaker Tone Test (AUTO)
// ============================================================
private void lab1SpeakerTone() {

    logSection("LAB 1 â€” Speaker Tone Test");

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
            logLabelValue("Confidence", r.confidence);

            logOk("Speaker output detected");

            // ðŸ”§ FIX: Explain LOW confidence explicitly
            if ("LOW".equalsIgnoreCase(r.confidence)) {

                logLabelValue(
                        "Note",
                        r.silenceDetected
                                ? "Signal detected at extremely low level. Low confidence may be caused by aggressive noise cancellation, microphone isolation, or device acoustic shielding."
                                : "Signal detected successfully, but with low confidence. This may be caused by system noise cancellation, microphone placement, or acoustic design."
                );

            } else {

                logLabelValue(
                        "Note",
                        "Speaker signal detected successfully."
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
// LAB 2 â€” Speaker Frequency Sweep
// ============================================================
private void lab2SpeakerSweep() {

    logSection("LAB 2 â€” Speaker Frequency Sweep");

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
            logLabelValue("Confidence", r.confidence);

            logOk("Frequency sweep executed");

            // ðŸ”§ FIX: Explain LOW confidence explicitly
            if ("LOW".equalsIgnoreCase(r.confidence)) {

                logLabelValue(
                        "Note",
                        r.silenceDetected
                                ? "Sweep executed at extremely low level. Low confidence may be caused by aggressive noise cancellation, narrow speaker frequency response, or acoustic isolation."
                                : "Sweep executed successfully, but with low confidence. This may be caused by DSP filtering, speaker frequency roll-off, or microphone placement."
                );

            } else {

                logLabelValue(
                        "Note",
                        "Frequency sweep detected successfully."
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
   LAB 3 â€” Earpiece Audio Path Check
   ============================================================ */
private void lab3EarpieceManual() {

    logSection("LAB 3 â€” Earpiece Audio Path Check");

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
            logLabelValue("Confidence", r.confidence);

            logOk("Earpiece audio path executed");

            logLabelValue(
                    "Note",
                    r.silenceDetected
                            ? "Audio path active but detected at very low level. This may occur due to call routing isolation or aggressive noise suppression."
                            : "Earpiece audio path detected successfully."
            );

            askUserEarpieceConfirmation();

        } catch (Throwable t) {
            logError("LAB 3 failed");
        } finally {
            try {
                if (am != null) {
                    am.setMode(oldMode);
                    am.setSpeakerphoneOn(oldSpeaker);
                }
            } catch (Throwable ignore) {}
            enableSingleExportButton();
        }

    }).start();
}

/* ============================================================
   LAB 4 â€” Microphone Recording Check (BOTTOM + TOP)
   ============================================================ */
private void lab4MicManual() {

    logSection("LAB 4 â€” Microphone Recording Check (BOTTOM + TOP)");

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

        logOk("Microphone recording path executed");

        logLabelValue(
                "Note",
                (bottom.silenceDetected && top.silenceDetected)
                        ? "Microphones active but detected at very low levels. Low confidence may be caused by environment silence or noise suppression."
                        : "Microphone signal detected successfully."
        );

        enableSingleExportButton();

    }).start();
}

/* ============================================================
   LAB 5 â€” Vibration Motor Test (AUTO)
   ============================================================ */
private void lab5Vibration() {

    logSection("LAB 5 â€” Vibration Motor Test");

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
// LABS 6â€“9: DISPLAY & SENSORS  
// ============================================================  

/* ============================================================
   LAB 6 â€” Display / Touch Basic Inspection (manual)
   ============================================================ */

private void lab6DisplayTouch() {

    logSection("LAB 6 â€” Display / Touch Basic Inspection");

    startActivityForResult(
            new Intent(this, TouchGridTestActivity.class),
            6006
    );
}

/* ============================================================
   LAB 7 â€” Rotation / Auto-Rotate Check (manual)
   ============================================================ */

private void lab7RotationManual() {

    logSection("LAB 7 â€” Rotation / Auto-Rotate Check");

    startActivityForResult(
            new Intent(this, RotationCheckActivity.class),
            7007
    );
}

/* ============================================================
   LAB 8 â€” Proximity During Call (manual)
   ============================================================ */

private void lab8ProximityCall() {

    logSection("LAB 8 â€” Proximity During Call");

    startActivityForResult(
            new Intent(this, ProximityCheckActivity.class),
            8008
    );
}

/* ============================================================
   LAB 9 â€” Sensors Check 
   ============================================================ */

private void lab9SensorsCheck() {

    logLine();
    logInfo("LAB 9 â€” Sensors Presence & Full Analysis");

    try {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm == null) {
            logError("SensorManager not available â€” framework issue.");
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
            String line = "â€¢ type=" + s.getType()
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
            logOk("Detected Xiaomi Virtual Gyroscope â€” expected behavior (sensor fusion instead of hardware gyro).");

        if (hasDualALS)
            logOk("Dual Ambient Light Sensors detected â€” OK. Device uses front + rear ALS for better auto-brightness.");
        else
            logWarn("Only one Ambient Light Sensor detected â€” auto-brightness may be less accurate.");

        if (hasSAR)
            logOk("SAR Detectors detected â€” normal. Used for proximity + radio tuning (Xiaomi/QTI platforms).");

        if (hasPickup)
            logOk("Pickup Sensor detected â€” supports 'lift to wake' and motion awareness.");

        if (hasLargeTouch)
            logOk("Large Area Touch Sensor detected â€” improved palm rejection and touch accuracy.");

        if (hasGameRotation)
            logOk("Game Rotation Vector sensor detected â€” smoother gaming orientation response.");

        logOk("Sensor suite appears complete and healthy for this device.");

    } catch (Exception e) {
        logError("Sensors analysis error: " + e.getMessage());
    }
}

/* ============================================================
   Helper â€” Sensor Presence
   ============================================================ */
private void checkSensor(SensorManager sm, int type, String name) {
    boolean ok = sm.getDefaultSensor(type) != null;
    if (ok)
        logOk(name + " is reported as available.");
    else
        logWarn(name + " is NOT reported â€” features depending on it may be limited or missing.");
}

// ============================================================  
// LAB 10: Wi-Fi Snapshot (SAFE SSID + DeepScan) â€” NO PASSWORD / NO QR  
// ============================================================  
private void lab10WifiSnapshot() {  
    logLine();  
    logInfo("LAB 10 â€” Wi-Fi Link Snapshot + SSID Safe Mode + DeepScan (NO password).");  

    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);  
    if (wm == null) {  
        logError("WifiManager not available.");  
        return;  
    }  

    if (!wm.isWifiEnabled()) {  
        logWarn("Wi-Fi is OFF â€” please enable and retry.");  
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
                logWarn("Opening Location Settingsâ€¦ enable Location and come back.");  
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
        logError("Very weak Wi-Fi signal â€” expect drops.");  

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
// LAB 10 â€” DEEPSCAN v3.0  
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

// ============================================================  
// LAB 11 â€” Mobile Data Diagnostic
// ============================================================  

private void lab11MobileDataDiagnostic() {

    logLine();
    logInfo("LAB 11 â€” Mobile Network Diagnostic (Laboratory)");

    TelephonySnapshot s = getTelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode (context only)
    // ------------------------------------------------------------
    if (s.airplaneOn) {
        logInfo("Airplane mode is ENABLED. Radio interfaces are intentionally disabled.");
        return;
    }

    // ------------------------------------------------------------
    // SIM state (laboratory reporting)
    // ------------------------------------------------------------
    if (!s.simReady) {

        switch (s.simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                logInfo("SIM state: ABSENT.");
                return;

            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                logInfo("SIM state: PRESENT but locked (PIN required).");
                return;

            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                logInfo("SIM state: PRESENT but locked (PUK required).");
                return;

            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                logInfo("SIM state: PRESENT but network locked.");
                return;

            default:
                logInfo("SIM state: PRESENT but not ready.");
                return;
        }
    }

    logLabelValue("SIM State", "READY");

    // ------------------------------------------------------------
    // Service state (legacy domain â€” informational)
    // ------------------------------------------------------------
    logLabelValue(
            "Service State (legacy)",
            s.inService ? "IN SERVICE" : "NOT REPORTED AS IN SERVICE"
    );

    if (!s.inService) {
        logInfo(
                "Legacy service registration is not reported. " +
                "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi)."
        );
    }

    // ------------------------------------------------------------
    // Data state (packet domain â€” informational)
    // ------------------------------------------------------------
    String dataStateLabel;
    switch (s.dataState) {
        case TelephonyManager.DATA_CONNECTED:
            dataStateLabel = "CONNECTED";
            break;
        case TelephonyManager.DATA_CONNECTING:
            dataStateLabel = "CONNECTING";
            break;
        case TelephonyManager.DATA_DISCONNECTED:
            dataStateLabel = "DISCONNECTED";
            break;
        default:
            dataStateLabel = "UNKNOWN";
            break;
    }

    logLabelValue("Data State", dataStateLabel);

    // ------------------------------------------------------------
    // Internet routing context (best effort)
    // ------------------------------------------------------------
    logLabelValue(
            "Internet Context",
            s.hasInternet ? "AVAILABLE (system routing)" : "NOT AVAILABLE"
    );

    // ------------------------------------------------------------
    // Laboratory conclusion
    // ------------------------------------------------------------
    logOk("Laboratory snapshot collected. No functional verdict inferred.");
}

// ============================================================
// LAB 12 â€” Call Function Interpretation (Laboratory)
// ============================================================

private void lab12CallFunctionInterpretation() {

    logLine();
    logInfo("LAB 12 â€” Call Function Interpretation (Laboratory)");

    TelephonySnapshot s = getTelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode (context only)
    // ------------------------------------------------------------
    if (s.airplaneOn) {
        logInfo("Airplane mode is ENABLED. Voice radio interfaces are intentionally disabled.");
        return;
    }

    // ------------------------------------------------------------
    // SIM availability (context only)
    // ------------------------------------------------------------
    logLabelValue(
            "SIM State",
            s.simReady ? "READY" : "NOT READY"
    );

    if (!s.simReady) {
        logInfo(
                "Voice service availability depends on SIM readiness. " +
                "No functional verdict inferred."
        );
        return;
    }

    // ------------------------------------------------------------
    // Legacy voice service state (informational)
    // ------------------------------------------------------------
    logLabelValue(
            "Voice Service (legacy)",
            s.inService ? "IN SERVICE" : "NOT REPORTED AS IN SERVICE"
    );

    if (!s.inService) {
        logInfo(
                "Legacy circuit-switched voice service is not reported. " +
                "On modern LTE/5G devices, voice calls may be provided via IMS (VoLTE / VoWiFi)."
        );
    }

    // ------------------------------------------------------------
    // Internet context (IMS relevance)
    // ------------------------------------------------------------
    logLabelValue(
            "Internet Context",
            s.hasInternet ? "AVAILABLE (system routing)" : "NOT AVAILABLE"
    );

    if (s.hasInternet) {
        logInfo(
                "Active internet routing detected. " +
                "IMS-based calling (VoLTE / VoWiFi) may be supported depending on carrier configuration."
        );
    } else {
        logInfo(
                "No active internet routing detected. " +
                "Legacy voice calling may still function if supported by the network."
        );
    }

    // ------------------------------------------------------------
    // Laboratory conclusion
    // ------------------------------------------------------------
    logOk(
            "Laboratory interpretation complete. " +
            "This test does not initiate or verify real call execution."
    );

    logInfo(
            "Call audio routing and microphone/earpiece paths are examined separately (LAB 3)."
    );
}

// ============================================================
// LAB 13 â€” Internet Quich Check
// ============================================================

private void lab13InternetQuickCheck() {  
    logLine();  
    logInfo("LAB 13 â€” Internet Access Quick Check.");  
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
// GEL THERMAL ENGINE â€” UNIVERSAL AUTO-SCALE (FINAL)
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
            // else already Â°C

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
// LAB 14 â€” Battery Health Stress Test (GEL Full Mode)
// LABORATORY EDITION â€” Objective battery diagnostics
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================

// ============================================================
// LAB 14 â€” FLAGS / UI STATE
// ============================================================
private volatile boolean lab14Running = false;
private TextView lab14DotsView;
private AlertDialog lab14Dialog;
private TextView lab14ProgressText;
private LinearLayout lab14ProgressBar;
private final int LAB14_TOTAL_SECONDS = 5 * 60; // ðŸ”’ 300 sec hard lock

private int lastSelectedStressDurationSec = 60;

// ============================================================
// LAB 14 â€” Battery Health Stress Test (GEL Full Mode)
// ============================================================
private void lab14BatteryHealthStressTest() {

    // HARD GUARD
    if (lab14Running) {
        logWarn("LAB 14 already running.");
        return;
    }
    lab14Running = true;

    try {

        // ------------------------------------------------------------
        // 0ï¸âƒ£ BASIC SAFETY CHECKS
        // ------------------------------------------------------------
        final float fStartPct = getCurrentBatteryPercent();
        if (fStartPct < 0f) {
            logError("Unable to read battery level.");
            lab14Running = false;
            return;
        }

        if (fStartPct < 50f) {
            logLine();
            logError("Battery level too low (<50%). Please charge the battery before running the stress test.");
            lab14Running = false;
            return;
        }

        // ------------------------------------------------------------
        // 1ï¸âƒ£ READ REAL BATTERY INFO
        // ------------------------------------------------------------
        final BatteryInfo fBiStart = getBatteryInfo();
        if (fBiStart == null || fBiStart.level < 0) {
            logError("Unable to read detailed battery information.");
            lab14Running = false;
            return;
        }

        if (isDeviceCharging()) {
            logError("Stress test requires the device to be NOT charging.");
            lab14Running = false;
            return;
        }

        final long modelCap = getStoredModelCapacity();

        long tmpFullMah = -1;
        String tmpCapSource = "Unknown";

        if (fBiStart.estimatedFullMah > 0) {
            tmpFullMah = fBiStart.estimatedFullMah;
            tmpCapSource = fBiStart.source;
        } else if (modelCap > 0) {
            tmpFullMah = modelCap;
            tmpCapSource = "Model capacity";
        }

        final long fFullMah = tmpFullMah;
        final String fCapSource = tmpCapSource;

        int tmpHealth = -1;
        if (modelCap > 0 && fBiStart.estimatedFullMah > 0) {
            tmpHealth = (int) Math.round(
                    (fBiStart.estimatedFullMah * 100.0) / (double) modelCap
            );
        }
        final int fHealthPct = tmpHealth;

        // ------------------------------------------------------------
        // 2ï¸âƒ£ FIXED DURATION â€” LAB MODE
        // ------------------------------------------------------------
        final int durationSec = LAB14_TOTAL_SECONDS;
        lastSelectedStressDurationSec = durationSec;

        // ------------------------------------------------------------
        // ðŸ”´ SHOW RUNNING DIALOG
        // ------------------------------------------------------------
        showLab14RunningDialog();

        // ------------------------------------------------------------
        // 3ï¸âƒ£ RUN STRESS (ASYNC)
        // ------------------------------------------------------------
        ui.post(() -> {

            Map<String, Float> z0 = readThermalZones();
            Float batt0 = pickZone(z0, "battery", "batt", "bat");

            logLine();
            logInfo("LAB 14 â€” Battery Health Stress Test started.");
            logInfo("Mode: GEL Full Mode (CPU burn + MAX brightness).");
            logInfo("Duration: " + durationSec + " seconds (laboratory mode).");

            logInfo(String.format(Locale.US,
                    "Start conditions: level=%d%%, status=%s, temp=%.1fÂ°C.",
                    fBiStart.level,
                    fBiStart.status,
                    fBiStart.temperature));

            if (fFullMah > 0) {
                logInfo("Capacity baseline: " + fFullMah + " mAh (" + fCapSource + ").");
            } else {
                logWarn("Capacity baseline unavailable. Using percentage-only analysis.");
            }

            if (fHealthPct > 0) {
                logInfo("Estimated battery health: ~" + fHealthPct + "% of model capacity.");
            }

            final long t0 = SystemClock.elapsedRealtime();

            applyMaxBrightnessAndKeepOn();
            startCpuBurn_C_Mode();

            // ------------------------------------------------------------
            // 4ï¸âƒ£ STOP STRESS
            // ------------------------------------------------------------
            ui.postDelayed(() -> {

                if (!lab14Running) return;

                try {
                    stopCpuBurn();
                    restoreBrightnessAndKeepOn();
                } catch (Throwable ignore) {}

                dismissLab14RunningDialog();

                BatteryInfo biEnd = getBatteryInfo();
                float endPct = getCurrentBatteryPercent();

                if (endPct < 0f || biEnd == null || biEnd.level < 0) {
                    logWarn("Unable to read final battery state.");
                    lab14Running = false;
                    return;
                }

                long t1 = SystemClock.elapsedRealtime();
                long dtMs = Math.max(1, t1 - t0);

                float deltaPct = fStartPct - endPct;
                float pctPerHour = (deltaPct * 3600000f) / dtMs;

                double consumedMah = -1;
                double mahPerHour  = -1;

                if (fFullMah > 0 && deltaPct > 0f) {
                    consumedMah = (deltaPct / 100.0) * fFullMah;
                    mahPerHour  = (consumedMah * 3600000.0) / dtMs;
                }

                logLine();
                logInfo(String.format(Locale.US,
                        "Stress result: start=%.1f%%, end=%.1f%%, drop=%.2f%% over %.1f sec.",
                        fStartPct, endPct, deltaPct, dtMs / 1000f));

                if (consumedMah >= 0) {
                    logInfo(String.format(Locale.US,
                            "Measured drain: %.0f mAh (â‰ˆ %.0f mAh/hour).",
                            consumedMah, mahPerHour));
                } else {
                    logInfo(String.format(Locale.US,
                            "Measured drain: â‰ˆ %.1f%%/hour.",
                            pctPerHour));
                }

                Map<String, Float> z1 = readThermalZones();
                Float batt1 = pickZone(z1, "battery", "batt", "bat");



                // ------------------------------------------------------------
                // 5ï¸âƒ£ LAB INTERPRETATION
                // ------------------------------------------------------------
                String decision;
                if (fHealthPct > 0 && fHealthPct < 70) {
                    decision = "Weak";
                    logError("LAB conclusion: Battery is heavily degraded. Replacement is recommended.");
                }
                else if (mahPerHour > 0 && mahPerHour > 900) {
                    decision = "Weak";
                    logWarn("LAB conclusion: High drain under stress. Battery replacement should be considered.");
                }
                else if ((fHealthPct > 0 && fHealthPct < 80) ||
                         (mahPerHour > 0 && mahPerHour > 650)) {
                    decision = "Normal";
                    logWarn("LAB conclusion: Battery shows wear but is still usable.");
                }
                else {
                    decision = "Strong";
                    logOk("LAB conclusion: Battery health is good. No replacement indicated.");
                }

                // ------------------------------------------------------------
                // 6ï¸âƒ£ HEALTH MAP
                // ------------------------------------------------------------
                printHealthCheckboxMap(decision);

                // ------------------------------------------------------------
                // 7ï¸âƒ£ ANALYTICS
                // ------------------------------------------------------------
                saveLab14DrainValue(mahPerHour);
                saveLab14Run();
                computeAndLogAgingIndex(mahPerHour, fHealthPct, batt1, batt0);
                computeAndLogConfidenceScore();
                logLab14Confidence();

                // ------------------------------------------------------------
                // ðŸ§  FINAL BATTERY HEALTH SCORE (NEW)
                // ------------------------------------------------------------
                logFinalBatteryHealthScore(mahPerHour, fHealthPct, batt1, batt0);

                lab14Running = false;

            }, durationSec * 1000L);
        });

    } catch (Throwable t) {
        try { stopCpuBurn(); } catch (Throwable ignore) {}
        try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}
        dismissLab14RunningDialog();
        lab14Running = false;
        logError("LAB 14 failed unexpectedly.");
    }
}

// ===========================================================
// LAB 14 â€” STRESS RUNNING DIALOG (LOCKED 300s)
// Visual progress + animated dots + segmented bar + EXIT
// ============================================================
private void showLab14RunningDialog() {

    try {
        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

        b.setTitle("LAB 14 â€” Battery Stress Test");
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(20), dp(24), dp(18));
        root.setBackgroundColor(0xFF101010);

        // ------------------------------------------------------------
        // Info text
        // ------------------------------------------------------------
        TextView info = new TextView(this);
        info.setText(
                "Running controlled battery stress test.\n" +
                "Duration locked to 5 minutes for reliable diagnostics.\n\n" +
                "Please do NOT use the device."
        );
        info.setTextColor(0xFFFFFFFF);
        info.setTextSize(14f);
        info.setPadding(0, 0, 0, dp(12));
        root.addView(info);

        // ------------------------------------------------------------
        // Progress text (seconds)
        // ------------------------------------------------------------
        lab14ProgressText = new TextView(this);
        lab14ProgressText.setText("Progress: 0 / 300 seconds");
        lab14ProgressText.setTextColor(0xFF39FF14);
        lab14ProgressText.setGravity(Gravity.CENTER);
        lab14ProgressText.setPadding(0, 0, 0, dp(10));
        root.addView(lab14ProgressText);

        // ------------------------------------------------------------
        // Animated dots
        // ------------------------------------------------------------
        lab14DotsView = new TextView(this);
        lab14DotsView.setText("â€¢");
        lab14DotsView.setTextColor(0xFF39FF14);
        lab14DotsView.setTextSize(22f);
        lab14DotsView.setGravity(Gravity.CENTER);
        lab14DotsView.setPadding(0, 0, 0, dp(10));
        root.addView(lab14DotsView);

        // ------------------------------------------------------------
        // Segmented progress bar (10 Ã— 30s)
        // ------------------------------------------------------------
        lab14ProgressBar = new LinearLayout(this);
        lab14ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
        lab14ProgressBar.setGravity(Gravity.CENTER);

        for (int i = 0; i < 10; i++) {
            View seg = new View(this);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(0, dp(12), 1f);
            lp.setMargins(dp(3), 0, dp(3), 0);
            seg.setLayoutParams(lp);
            seg.setBackgroundColor(0xFF333333);
            lab14ProgressBar.addView(seg);
        }

        root.addView(lab14ProgressBar);

        // ------------------------------------------------------------
        // EXIT / CANCEL BUTTON â€” RED / GOLD
        // ------------------------------------------------------------
        Button btnExit = new Button(this);
        btnExit.setText("Exit test");
        btnExit.setAllCaps(false);
        btnExit.setTextSize(15f);
        btnExit.setTextColor(0xFFFFFFFF);
        btnExit.setTypeface(null, Typeface.BOLD);

        GradientDrawable exitBg = new GradientDrawable();
        exitBg.setColor(0xFF8B0000);
        exitBg.setCornerRadius(dp(14));
        exitBg.setStroke(dp(3), 0xFFFFD700);
        btnExit.setBackground(exitBg);

        LinearLayout.LayoutParams lpExit =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lpExit.setMargins(0, dp(14), 0, 0);
        btnExit.setLayoutParams(lpExit);

        btnExit.setOnClickListener(v -> abortLab14ByUser());
        root.addView(btnExit);

        // ------------------------------------------------------------
        // FINALIZE DIALOG
        // ------------------------------------------------------------
        b.setView(root);

        lab14Dialog = b.create();
        if (lab14Dialog.getWindow() != null) {
            lab14Dialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        lab14Dialog.show();

        // ------------------------------------------------------------
        // Start dots animation
        // ------------------------------------------------------------
        startLab14DotsAnimation();

        // ------------------------------------------------------------
        // Progress updater (every 1 sec)
        // ------------------------------------------------------------
        final long startTs = SystemClock.elapsedRealtime();

        ui.post(new Runnable() {

            int lastStep = -1;

            @Override
            public void run() {

                if (!lab14Running ||
                        lab14ProgressBar == null ||
                        lab14ProgressText == null) {
                    return;
                }

                long now = SystemClock.elapsedRealtime();
                int elapsedSec = Math.min(
                        (int) ((now - startTs) / 1000),
                        LAB14_TOTAL_SECONDS
                );

                int step = Math.min(
                        elapsedSec / 30,
                        lab14ProgressBar.getChildCount()
                );

                if (step != lastStep) {
                    lastStep = step;
                    for (int i = 0; i < lab14ProgressBar.getChildCount(); i++) {
                        View seg = lab14ProgressBar.getChildAt(i);
                        if (seg != null) {
                            seg.setBackgroundColor(
                                    i < step ? 0xFF39FF14 : 0xFF333333
                            );
                        }
                    }
                }

                lab14ProgressText.setText(
                        "Progress: " + elapsedSec + " / " +
                        LAB14_TOTAL_SECONDS + " seconds"
                );

                if (elapsedSec < LAB14_TOTAL_SECONDS) {
                    ui.postDelayed(this, 1000);
                } else {
                    if (lab14ProgressText != null) {
                        lab14ProgressText.setText("Finalizing analysisâ€¦");
                    }
                }
            }
        });

    } catch (Throwable t) {
        dismissLab14RunningDialog();
    }
}

// ============================================================
// LAB 14 â€” DOTS ANIMATION ENGINE
// ============================================================
private void startLab14DotsAnimation() {

    ui.post(new Runnable() {

        int step = 0;
        final String[] frames = {"â€¢", "â€¢ â€¢", "â€¢ â€¢ â€¢"};

        @Override
        public void run() {

            if (!lab14Running || lab14DotsView == null)
                return;

            lab14DotsView.setText(frames[step % frames.length]);
            step++;

            ui.postDelayed(this, 500);
        }
    });
}

// ===================================================================
// LAB 14 â€” AGING INDEX ENGINE
// Objective trend-based battery aging indicator
// ===================================================================
private void computeAndLogAgingIndex(
        double mahPerHour,
        int healthPct,
        Float battTempAfter,
        Float battTempBefore
) {

    int runs = getLab14RunCount();
    if (runs < 2) {
        logLine();
        logInfo("Battery Aging Index: not available (requires at least 2 runs).");
        return;
    }

    final double BASE_DRAIN_MAH_PER_HOUR = 600.0; // healthy stress drain
    final double BASE_THERMAL_DELTA_C    = 6.0;   // healthy thermal rise

    double drainFactor;
    if (mahPerHour > 0) drainFactor = mahPerHour / BASE_DRAIN_MAH_PER_HOUR;
    else drainFactor = 1.0;

    double capacityFactor = 1.0;
    if (healthPct > 0) capacityFactor = 100.0 / Math.max(healthPct, 1);

    double thermalFactor = 1.0;
    if (battTempAfter != null && battTempBefore != null) {
        double dT = battTempAfter - battTempBefore;
        if (dT > 0) thermalFactor = dT / BASE_THERMAL_DELTA_C;
    }

    drainFactor    = clamp(drainFactor,    0.7, 1.4);
    capacityFactor = clamp(capacityFactor, 0.7, 1.4);
    thermalFactor  = clamp(thermalFactor,  0.7, 1.4);

    double agingIndex =
            0.5 * drainFactor +
            0.3 * capacityFactor +
            0.2 * thermalFactor;

    agingIndex = clamp(agingIndex, 0.7, 1.3);

    String interpretation;
    if (agingIndex <= 0.85) interpretation = "Low aging (battery condition is strong).";
    else if (agingIndex <= 1.00) interpretation = "Normal aging (expected wear).";
    else if (agingIndex <= 1.15) interpretation = "Moderate wear detected.";
    else interpretation = "Heavy aging detected. Battery replacement should be considered.";

    logLine();
    logInfo(String.format(Locale.US,
            "Battery Aging Index: %.2f (based on %d runs).",
            agingIndex, runs));
    logInfo("Interpretation: " + interpretation);
}

// ------------------------------------------------------------
// Utility clamp
// ------------------------------------------------------------
private double clamp(double v, double min, double max) {
    return Math.max(min, Math.min(max, v));
}

// ===================================================================
// LAB 14 â€” CONFIDENCE SCORE (%)
// Variance-based reliability indicator
// ===================================================================
private static final String LAB14_PREFS = "lab14_prefs";
private static final String KEY_LAB14_RUNS = "lab14_run_count";

private static final String KEY_LAB14_LAST_DRAIN_1 = "lab14_drain_1";
private static final String KEY_LAB14_LAST_DRAIN_2 = "lab14_drain_2";
private static final String KEY_LAB14_LAST_DRAIN_3 = "lab14_drain_3";

// ------------------------------------------------------------
// Save last drain value (called from LAB 14 end)
// ------------------------------------------------------------
private void saveLab14DrainValue(double mahPerHour) {

    if (mahPerHour <= 0) return;

    try {
        SharedPreferences sp = getSharedPreferences(LAB14_PREFS, MODE_PRIVATE);

        double d1 = Double.longBitsToDouble(
                sp.getLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(-1))
        );
        double d2 = Double.longBitsToDouble(
                sp.getLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(-1))
        );

        sp.edit()
                .putLong(KEY_LAB14_LAST_DRAIN_3, Double.doubleToLongBits(d2))
                .putLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(d1))
                .putLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(mahPerHour))
                .apply();

    } catch (Throwable ignore) {}
}

// ------------------------------------------------------------
// Save one LAB 14 run
// ------------------------------------------------------------
private void saveLab14Run() {
    try {
        SharedPreferences sp = getSharedPreferences(LAB14_PREFS, MODE_PRIVATE);
        int runs = sp.getInt(KEY_LAB14_RUNS, 0);
        sp.edit().putInt(KEY_LAB14_RUNS, runs + 1).apply();
    } catch (Throwable ignore) {}
}

// ------------------------------------------------------------
// Get number of completed LAB 14 runs
// ------------------------------------------------------------
private int getLab14RunCount() {
    try {
        SharedPreferences sp = getSharedPreferences(LAB14_PREFS, MODE_PRIVATE);
        return sp.getInt(KEY_LAB14_RUNS, 0);
    } catch (Throwable ignore) {
        return 0;
    }
}

// ------------------------------------------------------------
// Compute and log confidence score
// ------------------------------------------------------------
private void computeAndLogConfidenceScore() {

    int runs = getLab14RunCount();
    if (runs < 2) {
        logLine();
        logInfo("Confidence Score: not available (requires multiple runs).");
        return;
    }

    try {
        SharedPreferences sp = getSharedPreferences(LAB14_PREFS, MODE_PRIVATE);

        double[] vals = new double[]{
                Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_1, Double.doubleToLongBits(-1))),
                Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_2, Double.doubleToLongBits(-1))),
                Double.longBitsToDouble(sp.getLong(KEY_LAB14_LAST_DRAIN_3, Double.doubleToLongBits(-1)))
        };

        double sum = 0;
        int n = 0;
        for (double v : vals) {
            if (v > 0) {
                sum += v;
                n++;
            }
        }

        if (n < 2) {
            logInfo("Confidence Score: insufficient data.");
            return;
        }

        double mean = sum / n;

        double var = 0;
        for (double v : vals) {
            if (v > 0) var += (v - mean) * (v - mean);
        }
        var /= n;

        double std = Math.sqrt(var);

        double relVar = std / mean;

        int confidence;
        if (relVar < 0.05) confidence = 95;
        else if (relVar < 0.08) confidence = 90;
        else if (relVar < 0.12) confidence = 80;
        else if (relVar < 0.18) confidence = 70;
        else confidence = 60;

        logLine();
        logInfo(String.format(Locale.US,
                "Confidence Score: %d%% (based on %d runs).",
                confidence, n));

        if (confidence >= 90) logOk("Results are highly consistent.");
        else if (confidence >= 80) logInfo("Results show good consistency.");
        else logWarn("Results show noticeable variance. Additional runs may improve accuracy.");

    } catch (Throwable t) {
        logWarn("Confidence Score calculation failed.");
    }
}

// ------------------------------------------------------------
// Log confidence message based on run count (COLOR CODED)
// ------------------------------------------------------------
private void logLab14Confidence() {

    int runs = getLab14RunCount();
    logLine();

    if (txtLog == null) return;

    if (runs <= 1) {

        SpannableString sp =
                new SpannableString("Confidence: Preliminary (1 run)");

        sp.setSpan(
                new ForegroundColorSpan(Color.RED),
                "Confidence:".length(),
                sp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        txtLog.append(sp);
        txtLog.append("\n");

        logInfo("FOR HIGHER DIAGNOSTIC ACCURACY, RUN THIS TEST 2 MORE TIMES UNDER SIMILAR CONDITIONS.");
    }
    else if (runs == 2) {

        SpannableString sp =
                new SpannableString("Confidence: Medium (2 runs)");

        sp.setSpan(
                new ForegroundColorSpan(0xFFFFA500), // orange
                "Confidence:".length(),
                sp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        txtLog.append(sp);
        txtLog.append("\n");

        logInfo("ONE ADDITIONAL RUN IS RECOMMENDED TO CONFIRM BATTERY AGING TREND.");
    }
    else {

        SpannableString sp =
                new SpannableString("Confidence: High (3+ consistent runs)");

        sp.setSpan(
                new ForegroundColorSpan(0xFF39FF14), // GEL green
                "Confidence:".length(),
                sp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        txtLog.append(sp);
        txtLog.append("\n");

        logInfo("Battery diagnostic confidence is high.");
    }
}

// ===================================================================
// BATTERY REAL CAPACITY ENGINE â€” needed by LAB 14
// ===================================================================
private static final String PREFS_NAME_BATTERY = "gel_prefs";
private static final String KEY_BATTERY_MODEL_CAPACITY = "battery_model_capacity";

// ===================================================================
// MODEL CAPACITY HELPERS
// ===================================================================
private long getStoredModelCapacity() {
    try {
        SharedPreferences sp =
                getSharedPreferences(PREFS_NAME_BATTERY, MODE_PRIVATE);
        return sp.getLong(KEY_BATTERY_MODEL_CAPACITY, -1L);
    } catch (Throwable t) {
        return -1L;
    }
}

// ===================================================================
// NORMALIZE mAh / Î¼Ah
// ===================================================================
private long normalizeMah(long raw) {
    if (raw <= 0) return -1;
    if (raw > 200000) return raw / 1000;
    return raw;
}

// ===================================================================
// UNIVERSAL BATTERY SCANNER â€” GEL v7.1
// ===================================================================
private BatteryInfo getBatteryInfo() {

    BatteryInfo bi = new BatteryInfo();

    try {
        Intent i = registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i != null) {

            bi.level = i.getIntExtra(
                    BatteryManager.EXTRA_LEVEL, -1);

            bi.temperature =
                    i.getIntExtra(
                            BatteryManager.EXTRA_TEMPERATURE, 0) / 10f;

            switch (i.getIntExtra(
                    BatteryManager.EXTRA_STATUS, -1)) {

                case BatteryManager.BATTERY_STATUS_CHARGING:
                    bi.status = "Charging";
                    break;

                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    bi.status = "Discharging";
                    break;

                case BatteryManager.BATTERY_STATUS_FULL:
                    bi.status = "Full";
                    break;

                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    bi.status = "Not charging";
                    break;

                default:
                    bi.status = "Unknown";
                    break;
            }
        }
    } catch (Throwable ignore) {}

    try {
        BatteryManager bm =
                (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (bm != null) {
            long cc = normalizeMah(
                    bm.getLongProperty(
                            BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER));

            if (cc > 0 && bi.level > 0) {
                bi.currentChargeMah = cc;
                bi.estimatedFullMah =
                        (long) (cc / (bi.level / 100f));
                bi.source = "Charge Counter";
            }
        }
    } catch (Throwable ignore) {}

    return bi;
}





     
//=============================================================
// LAB 15 â€” Charging System Diagnostic (SMART AUTO ENGINE)
// GEL LAB EDITION â€” UNPLUG SAFE + EXIT SAFE (FINAL / LOCKED)
//=============================================================
private void lab15ChargingSystemSmart() {

    if (lab15Running) {
        logWarn("LAB 15 already running.");
        return;
    }

    lab15Running = true;
    lab15FlapUnstable = false;
    lab15OverTempDuringCharge = false;

    // ------------------------------------------------------------
    // DIALOG UI
    // ------------------------------------------------------------
    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setTitle("LAB 15 â€” Connect the charger to the device charging port");
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));
    root.setBackgroundColor(0xFF101010);

    // ------------------------------------------------------------
    // STATUS TEXT (NOT TITLE)
    // ------------------------------------------------------------
    lab15StatusText = new TextView(this);
    lab15StatusText.setText("Waiting for charging connectionâ€¦");
    lab15StatusText.setTextColor(0xFFAAAAAA);
    lab15StatusText.setTextSize(15f);
    lab15StatusText.setPadding(0, 0, 0, dp(8));
    root.addView(lab15StatusText);

    // ------------------------------------------------------------
    // DOTS (ANIMATION)
    // ------------------------------------------------------------
    TextView dotsView = new TextView(this);
    dotsView.setText("â€¢");
    dotsView.setTextColor(0xFF39FF14);
    dotsView.setTextSize(22f);
    dotsView.setGravity(Gravity.CENTER);
    dotsView.setPadding(0, 0, 0, dp(6));
    root.addView(dotsView);

    // ------------------------------------------------------------
    // COUNTER TEXT (POPUP ONLY)
    // ------------------------------------------------------------
    lab15CounterText = new TextView(this);
    lab15CounterText.setText("0 / 180 sec");
    lab15CounterText.setTextColor(0xFFAAAAAA);
    lab15CounterText.setTextSize(13f);
    lab15CounterText.setGravity(Gravity.CENTER);
    lab15CounterText.setPadding(0, 0, 0, dp(10));
    root.addView(lab15CounterText);

    // ------------------------------------------------------------
    // PROGRESS BAR (6 Ã— 30s = 180s)
    // ------------------------------------------------------------
    lab15ProgressBar = new LinearLayout(this);
    lab15ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
    lab15ProgressBar.setGravity(Gravity.CENTER);

    for (int i = 0; i < 6; i++) {
        View seg = new View(this);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(10), 1f);
        lp.setMargins(dp(3), 0, dp(3), 0);
        seg.setLayoutParams(lp);
        seg.setBackgroundColor(0xFF333333);
        lab15ProgressBar.addView(seg);
    }
    root.addView(lab15ProgressBar);

    // ------------------------------------------------------------
    // EXIT BUTTON â€” RED / GOLD
    // ------------------------------------------------------------
    lab15ExitBtn = new Button(this);
    lab15ExitBtn.setText("Exit test");
    lab15ExitBtn.setAllCaps(false);
    lab15ExitBtn.setTextColor(0xFFFFFFFF);
    lab15ExitBtn.setTypeface(null, Typeface.BOLD);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(14));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    lab15ExitBtn.setBackground(exitBg);

    LinearLayout.LayoutParams lpExit =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lpExit.setMargins(0, dp(16), 0, 0);
    lab15ExitBtn.setLayoutParams(lpExit);

    lab15ExitBtn.setOnClickListener(v -> {
        logWarn("LAB 15 aborted by user.");
        lab15Running = false;
        if (lab15Dialog != null) lab15Dialog.dismiss();
    });

    root.addView(lab15ExitBtn);

    b.setView(root);
    lab15Dialog = b.create();
    lab15Dialog.getWindow()
            .setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    lab15Dialog.show();

    // ------------------------------------------------------------
    // LOG HEADER (ORDER LOCKED)
    // ------------------------------------------------------------
    logLine();
    logInfo("LAB 15 - Charging System Diagnostic (Smart).");

    // ------------------------------------------------------------
    // WAIT FOR CHARGER + MONITOR
    // ------------------------------------------------------------
    final long[] startTs = { -1 };
    final long[] lastChargeSeenTs = { -1 };
    final boolean[] wasCharging = { false };
    final BatteryInfo[] startInfo = { null };

    final String[] dotFrames = { "â€¢", "â€¢ â€¢", "â€¢ â€¢ â€¢" };

    ui.post(new Runnable() {

        int lastStep = -1;
        int dotStep = 0;

        @Override
        public void run() {

            if (!lab15Running) return;

            boolean chargingNow = isDeviceCharging();
            long nowTs = SystemClock.elapsedRealtime();

            // ----------------------------------------------------
            // DOTS ANIMATION
            // ----------------------------------------------------
            dotsView.setText(dotFrames[dotStep % dotFrames.length]);
            dotStep++;

            // ----------------------------------------------------
            // FIRST CHARGER DETECTION
            // ----------------------------------------------------
            if (chargingNow) {
                lastChargeSeenTs[0] = nowTs;
            }

            if (chargingNow && !wasCharging[0]) {
                wasCharging[0] = true;
                startTs[0] = nowTs;
                startInfo[0] = getBatteryInfo();
                
                lab15BattTempStart = getBatteryTemperature();
                lab15BattTempPeak  = lab15BattTempStart;

                lab15StatusText.setText("Charging connection detected");
                lab15StatusText.setTextColor(0xFF39FF14);

                float battPct = getCurrentBatteryPercent();
                float battTemp = getBatteryTemperature();

                if (battPct >= 0)
                    logInfo(String.format(Locale.US,
                            "Battery level: %.1f%%", battPct));
                if (battTemp > 0)
                    logInfo(String.format(Locale.US,
                            "Battery temperature: %.1fÂ°C", battTemp));

                logOk("Charging state detected.");
                logLine();
                logInfo("Monitoring charging stability for 20 seconds...");
                logInfo("Monitoring charging system for 180 seconds...");
            }

            // ----------------------------------------------------
            // SMART UNPLUG DETECTION (5s GRACE)
            // ----------------------------------------------------
            if (wasCharging[0] && !chargingNow) {
                if (lastChargeSeenTs[0] > 0 &&
                        nowTs - lastChargeSeenTs[0] > 5000) {

                    lab15StatusText.setText("Charging disconnected â€” test aborted");
                    lab15StatusText.setTextColor(0xFFFF5555);

                    logLine();
                    logError(
                            "Charging instability detected. " +
                            "Check or replace the charging cable. " +
                            "If the issue persists, charging port inspection is recommended."
                    );

                    lab15Running = false;
                    if (lab15Dialog != null) lab15Dialog.dismiss();
                    return;
                }
            }

            if (startTs[0] < 0) {
                ui.postDelayed(this, 500);
                return;
            }

            // ----------------------------------------------------
            // PROGRESS + COUNTER (POPUP ONLY)
            // ----------------------------------------------------
            int elapsedSec = (int) ((nowTs - startTs[0]) / 1000);

            if (lab15CounterText != null) {
                lab15CounterText.setText(
                        Math.min(elapsedSec, LAB15_TOTAL_SECONDS) +
                        " / " + LAB15_TOTAL_SECONDS + " sec"
                );
            }

            int step = Math.min(
                    elapsedSec / 30,
                    lab15ProgressBar.getChildCount()
            );

            if (step != lastStep) {
                lastStep = step;
                for (int i = 0; i < lab15ProgressBar.getChildCount(); i++) {
                    View seg = lab15ProgressBar.getChildAt(i);
                    if (seg != null) {
                        seg.setBackgroundColor(
                                i < step ? 0xFF39FF14 : 0xFF333333
                        );
                    }
                }
            }

            if (elapsedSec < LAB15_TOTAL_SECONDS) {
                ui.postDelayed(this, 1000);
            } else {

// ----------------------------------------------------
// END THERMAL SNAPSHOT (CHARGING CORRELATED)
// ----------------------------------------------------
if (isChargingNow()) {
    lab15BattTempEnd = getBatteryTemperature();
} else {
    // fallback: last known peak is safer than idle temp
    lab15BattTempEnd = lab15BattTempPeak;
}

logLab15ThermalCorrelation();

                lab15StatusText.setText("Charging system stable");
                lab15StatusText.setTextColor(0xFF39FF14);

                logLine();
                logOk("Charging behavior appears normal. Temperature within safe limits.");
                logOk("LAB decision: Charging system OK. No cleaning or replacement required.");

                logOk("Charging connection appears stable. No abnormal plug/unplug behavior detected.");
                logOk("LAB decision: Charging stability OK.");

                // ðŸ”‹ CHARGING STRENGTH ESTIMATION â€” UNCHANGED
                logLine();

                BatteryInfo endInfo = getBatteryInfo();

                if (startInfo[0] != null &&
                        endInfo != null &&
                        startInfo[0].currentChargeMah > 0 &&
                        endInfo.currentChargeMah > startInfo[0].currentChargeMah) {

                    long startMah = startInfo[0].currentChargeMah;
                    long endMah   = endInfo.currentChargeMah;
                    long fullMah  =
                            endInfo.estimatedFullMah > 0
                                    ? endInfo.estimatedFullMah
                                    : startInfo[0].estimatedFullMah;

                    float deltaPct =
                            ((endMah - startMah) * 100f) / (float) fullMah;

                    if (deltaPct >= 1.2f)
                        logOk("Charging strength: STRONG");
                    else if (deltaPct >= 0.6f)
                        logOk("Charging strength: NORMAL");
                    else if (deltaPct >= 0.3f)
                        logWarn("Charging strength: MODERATE");
                    else
                        logError("Charging strength: POOR");

                } else {
                    logWarn("Charging strength: Unable to estimate accurately.");
                }

                lab15Running = false;
                if (lab15Dialog != null) lab15Dialog.dismiss();
            }
        }
    });
}

// ============================================================
// LAB 16 â€” Thermal Snapshot
// GEL Universal Edition â€” Internals + Peripherals + Root-Aware
// READ-ONLY â€¢ SNAPSHOT â€¢ SAFE
// ============================================================
private void lab16ThermalSnapshot() {

    logLine();
    logInfo("LAB 16 â€” Thermal Snapshot (ASCII thermal map)");

    // ------------------------------------------------------------
    // 1ï¸âƒ£ Read generic thermal zones
    // ------------------------------------------------------------
    Map<String, Float> zones = readThermalZones();

    // ------------------------------------------------------------
    // 2ï¸âƒ£ Battery ALWAYS from BatteryManager
    // ------------------------------------------------------------
    float batt = getBatteryTemperature();

    if (zones == null || zones.isEmpty()) {
        logWarn("Device exposes NO thermal zones. Printing battery only.");
        printZoneAscii("Battery", batt);
        logOk("Lab 16 finished.");
        return;
    }

    // ------------------------------------------------------------
    // 3ï¸âƒ£ Auto-detect main zones
    // ------------------------------------------------------------
    Float cpu  = pickZone(zones, "cpu", "cpu-therm", "big", "little", "tsens", "mtktscpu");
    Float gpu  = pickZone(zones, "gpu", "gpu-therm", "gpuss", "mtkgpu");
    Float skin = pickZone(zones, "skin", "xo-therm", "shell", "surface");
    Float pmic = pickZone(zones, "pmic", "pmic-therm", "power-thermal", "charger", "chg");

    logOk("Thermal Zones found: " + zones.size());

    // ------------------------------------------------------------
    // 4ï¸âƒ£ ASCII SNAPSHOT MAP
    // ------------------------------------------------------------
    if (cpu != null)  printZoneAscii("CPU", cpu);
    if (gpu != null)  printZoneAscii("GPU", gpu);

    printZoneAscii("Battery", batt); // ALWAYS

    if (skin != null) printZoneAscii("Skin", skin);
    if (pmic != null) printZoneAscii("PMIC", pmic);

    // ------------------------------------------------------------
    // 5ï¸âƒ£ HARDWARE SUMMARY (Internals + Peripherals)
    // ------------------------------------------------------------
    logLine();
    appendHtml("<b>Hardware Thermal Summary</b>");
    appendHtml("<small><tt>" + escape(buildThermalInfo()) + "</tt></small>");

    // ------------------------------------------------------------
    // 6ï¸âƒ£ ROOT-ENHANCED INTERNAL VIEW (if available)
    // ------------------------------------------------------------
    if (isRooted) {
        logLine();
        appendHtml("<b>Advanced Thermal (Root)</b>");
        appendHtml("<small><tt>" + escape(buildThermalInternalReport()) + "</tt></small>");
    } else {
        logInfo("Advanced Info: For detailed thermal and cooling information, requires root access.");
    }

    logOk("Lab 16 finished.");
}

// ============================================================
// ASCII BAR (100 chars â€” monospace via HTML)
// ============================================================
private void printZoneAscii(String label, float t) {

    String color;
    if (t < 45f)       color = "ðŸŸ©";
    else if (t < 60f)  color = "ðŸŸ¨";
    else               color = "ðŸŸ¥";

    float maxT = 80f;
    float pct  = Math.min(1f, t / maxT);
    int bars   = (int) (pct * 100);

    StringBuilder sb = new StringBuilder(100);
    for (int i = 0; i < bars; i++) sb.append("â–ˆ");
    while (sb.length() < 100) sb.append(" ");

    logInfo(label + ": " + color + " " + String.format(Locale.US, "%.1fÂ°C", t));
    appendHtml("<small><small><tt>" + escape(sb.toString()) + "</tt></small></small>");
}

// ===================================================================
// THERMAL SENSORS â€” INTERNAL (Human Readable, Root-Enhanced)
// ===================================================================
private String buildThermalInternalReport() {

    StringBuilder sb = new StringBuilder();
    sb.append("THERMAL SENSORS (INTERNAL)\n");
    sb.append("â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€\n");

    File thermalDir = new File("/sys/class/thermal");
    if (!thermalDir.exists() || !thermalDir.isDirectory()) {
        sb.append("Thermal sensors not available on this device.\n");
        return sb.toString();
    }

    File[] zones = thermalDir.listFiles((d, n) -> n.startsWith("thermal_zone"));
    if (zones == null || zones.length == 0) {
        sb.append("No thermal zones detected.\n");
        return sb.toString();
    }

    for (File z : zones) {
        try {
            String type = readFirstLineSafe(new File(z, "type"));
            long milli  = readLongSafe(new File(z, "temp"));
            if (type == null || milli == Long.MIN_VALUE) continue;

            float c = milli / 1000f;
            if (!isValidTemp(c)) continue;

            sb.append(z.getName())
              .append(" [").append(type).append("]\n")
              .append("  Temp : ")
              .append(String.format(Locale.US, "%.1fÂ°C", c))
              .append("\n");

            // Trip points
            for (int i = 0; i < 10; i++) {
                String tp  = readFirstLineSafe(new File(z, "trip_point_" + i + "_temp"));
                String tpt = readFirstLineSafe(new File(z, "trip_point_" + i + "_type"));
                if (tp.isEmpty() || tpt.isEmpty()) break;

                float tc = Float.parseFloat(tp) / 1000f;
                sb.append("  Trip ").append(i)
                  .append(" (").append(tpt).append(") : ")
                  .append(String.format(Locale.US, "%.1fÂ°C", tc))
                  .append("\n");
            }
            sb.append("\n");

        } catch (Throwable ignore) {}
    }

    return sb.toString();
}

// ===================================================================
// FINAL HARDWARE SUMMARY (Internals + Peripherals)
// ===================================================================
private String buildThermalInfo() {

    StringBuilder sb = new StringBuilder();

    ThermalGroupReading batteryMain  = new ThermalGroupReading();
    ThermalGroupReading batteryShell = new ThermalGroupReading();
    ThermalGroupReading pmic         = new ThermalGroupReading();
    ThermalGroupReading charger      = new ThermalGroupReading();
    ThermalGroupReading modemMain    = new ThermalGroupReading();
    ThermalGroupReading modemAux     = new ThermalGroupReading();

    ThermalSummary summary = scanThermalHardware(
            batteryMain, batteryShell, pmic, charger, modemMain, modemAux
    );

    applyThermalFallbacks(
            batteryMain, batteryShell, pmic, charger, modemMain, modemAux
    );

    if (summary != null) {
        sb.append(String.format(Locale.US,
                "Thermal Zones     : %d\n", summary.zoneCount));
        sb.append(String.format(Locale.US,
                "Cooling Devices   : %d%s\n\n",
                summary.coolingDeviceCount,
                summary.coolingDeviceCount == 0
                        ? " (passive cooling only)"
                        : ""));
    }

    sb.append("Hardware Thermal Systems\n");
    sb.append("================================\n");
    sb.append(formatThermalLine("Main Modem", modemMain));
    sb.append(formatThermalLine("Secondary Modem", modemAux));
    sb.append(formatThermalLine("Main Battery", batteryMain));
    sb.append(formatThermalLine("Battery Shell", batteryShell));
    sb.append(formatThermalLine("Charger Thermal", charger));
    sb.append(formatThermalLine("PMIC Thermal", pmic));
    sb.append("\n");

    sb.append("Hardware Cooling Systems\n");
    sb.append("================================\n");
    appendHardwareCoolingDevices(sb);

    return sb.toString();
}

// ============================================================
// LAB 17 â€” GEL AUTO Battery Reliability Evaluation
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
            logInfo("â–¶ Running Stress Test (Lab 15)...");  
            logInfo("[â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘] 32%");  
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
            logInfo("â–¶ Calculating drain rate...");  
            logInfo("[â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–‘â–‘â–‘â–‘] 85%");  
        });  

        // ============================================================  
        // 2. THERMAL ZONES (LAB 16 STYLE)  
        // ============================================================  
        ui.post(() -> {  
            logInfo("");  
            logInfo("â–¶ Running Thermal Zones (Lab 16)...");  
            logInfo("[â–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–ˆâ–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘] 68%");  
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
        ui.post(() -> logInfo("â–¶ Calculating voltage stability..."));  
        float v0 = getBatteryVoltage_mV();  
        Thread.sleep(1500);  
        float v1 = getBatteryVoltage_mV();  
        float dv = Math.abs(v1 - v0);  

        // ============================================================  
        // 4. CAPACITY ESTIMATION + PMIC  
        // ============================================================  
        ui.post(() -> {  
            logInfo("â–¶ Calculating thermal rise...");  
            logInfo("â–¶ Calculating PMIC behavior...");  
            logInfo("â–¶ Calculating discharge curve...");  
            logInfo("â–¶ Calculating estimated real capacity...");  
            logInfo("â–¶ Getting device information...");  
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
                    "Stress window: %.1f%% â†’ %.1f%% (drop %.2f%%)",  
                    f_before, f_after, f_drop));  

            logInfo(String.format(Locale.US,  
                    "Drain rate under load: %.1f %%/hour", f_perHour));  

            logInfo(String.format(Locale.US,  
                    "1. Stress Drain Rate: %d mA", f_drain));  

            logInfo(String.format(Locale.US,  
                    "2. Estimated Real Capacity: %.0f mAh (factory: %d mAh)",  
                    f_cap, f_factory));  

            logInfo(String.format(Locale.US,  
                    "3. Voltage Stability: %s (Î” %.1f mV)",  
                    f_voltLbl, f_dv));  

            logInfo(String.format(Locale.US,  
                    "4. Thermal Rise: %s (CPU +%.1fÂ°C, BATT +%.1fÂ°C)",  
                    f_therm, f_dCPU, f_dBATT));  

            logInfo(String.format(Locale.US,  
                    "5. Cycle Behavior: %s", f_cycle));  

            logLine();  
            logOk(String.format(Locale.US,  
                    "Final Battery Health Score: %d%% (%s)",  
                    f_score, f_cat));  

            // Checkbox map with NEON âœ” and white labels  
            appendHtml("âœ” <font color='#39FF14'>Strong</font>");  
            appendHtml("â˜ <font color='#FFFFFF'>Excellent</font>");  
            appendHtml("â˜ <font color='#FFFFFF'>Very good</font>");  
            appendHtml("â˜ <font color='#FFFFFF'>Normal</font>");  
            appendHtml("â˜ <font color='#FFFFFF'>Weak</font>");  
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
// LABS 18â€“21: STORAGE & PERFORMANCE
// ============================================================
private void lab18StorageSnapshot() {
logLine();
logInfo("LAB 18 â€” Internal Storage Snapshot.");
try {
StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());
long total = s.getBlockCountLong() * s.getBlockSizeLong();
long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
long used = total - free;
int pctFree = (int) ((free * 100L) / total);

logInfo("Internal storage used: " + humanBytes(used) + " / " + humanBytes(total)  
                + " (free " + humanBytes(free) + ", " + pctFree + "%).");  

        if (pctFree < 5)  
            logError("Free space below 5% â€” high risk of crashes, failed updates and slow UI.");  
        else if (pctFree < 10)  
            logWarn("Free space below 10% â€” performance and update issues likely.");  
        else  
            logOk("Internal storage level is acceptable for daily usage.");  
    } catch (Exception e) {  
        logError("Storage snapshot error: " + e.getMessage());  
    }  
}  

private void lab19AppsFootprint() {  
    logLine();  
    logInfo("LAB 19 â€” Installed Apps Footprint.");  
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
            logError("Very high number of user apps â€” strong risk of background drain and lag.");  
        else if (userApps > 80)  
            logWarn("High number of user apps â€” possible performance impact.");  
        else  
            logOk("App footprint is within a normal range.");  
    } catch (Exception e) {  
        logError("Apps footprint error: " + e.getMessage());  
    }  
}  

private void lab20RamSnapshot() {  
    logLine();  
    logInfo("LAB 20 â€” Live RAM Snapshot.");  
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
            logError("Very low free RAM (<10%) â€” expect heavy lag and aggressive app killing.");  
        else if (pct < 20)  
            logWarn("Low free RAM (10â€“20%) â€” borderline under load.");  
        else  
            logOk("RAM level is acceptable for normal usage at this moment.");  
    } catch (Exception e) {  
        logError("RAM snapshot error: " + e.getMessage());  
    }  
}  

private void lab21UptimeHints() {  
    logLine();  
    logInfo("LAB 21 â€” Uptime / Reboot History Hints.");  
    long upMs = SystemClock.elapsedRealtime();  
    String upStr = formatUptime(upMs);  
    logInfo("System uptime: " + upStr);  
    if (upMs < 2 * 60 * 60 * 1000L) {  
        logWarn("Device was rebooted recently (<2 hours) â€” some issues may already be masked by the reboot.");  
    } else if (upMs > 7L * 24L * 60L * 60L * 1000L) {  
        logWarn("Uptime above 7 days â€” recommend a reboot before deep diagnostics.");  
    } else {  
        logOk("Uptime is within a reasonable range for diagnostics.");  
    }  
}  

// ============================================================  
// LABS 22â€“25: SECURITY & SYSTEM HEALTH  
// ============================================================  
// ============================================================

// LAB 22 â€” Screen Lock / Biometrics Checklist (auto-detect + manual)
// ============================================================
private void lab22ScreenLock() {
logLine();
logInfo("LAB 22 â€” Screen Lock / Biometrics Checklist");

try {  
    android.app.KeyguardManager km =  
            (android.app.KeyguardManager) getSystemService(KEYGUARD_SERVICE);  

    if (km != null) {  
        boolean secure = km.isDeviceSecure();  

        if (secure) {  
            logOk("Device reports SECURE lock method (PIN / Pattern / Password).");  
        } else {  
            logError("Device has NO secure lock method â€” phone is UNPROTECTED!");  
            logWarn("Anyone can access data without authentication.");  
        }  
    } else {  
        logWarn("KeyguardManager not available â€” cannot read lock status.");  
    }  
} catch (Exception e) {  
    logWarn("Screen lock detection failed: " + e.getMessage());  
}  

// Manual guidance (kept for technician)  
logInfo("1) Verify that the device has a secure lock method (PIN / pattern / password).");  
logWarn("If the device is left with no lock at all â€” higher risk for data and account theft.");  
logInfo("2) Test fingerprint / face unlock if configured to confirm sensor response.");

}

// ============================================================

// LAB 23 â€” Security Patch & Play Protect (auto + manual)
// ============================================================
private void lab23SecurityPatchManual() {
logLine();
logInfo("LAB 23 â€” Security Patch & Play Protect Check");

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
// 2) Play Protect Detection â€” BEST POSSIBLE WITHOUT ROOT  
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
        logError("Google Play Services missing â€” Play Protect NOT available.");  
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
            // Fallback â€” detect if the activity exists  
            Intent protectIntent = new Intent();  
            protectIntent.setClassName(  
                    "com.google.android.gms",  
                    "com.google.android.gms.security.settings.VerifyAppsSettingsActivity"  
            );  

            if (protectIntent.resolveActivity(pm) != null) {  
                logOk("Play Protect module detected (activity present).");  
            } else {  
                logWarn("Play Protect module not fully detected â€” OEM variant or restricted build.");  
            }  
        }  
    }  
} catch (Exception e) {  
    logWarn("Play Protect detection error: " + e.getMessage());  
}  

// MANUAL GUIDANCE (kept for technicians)  
logInfo("1) Open Android Settings â†’ About phone â†’ Android version â†’ Security patch level.");  
logWarn("If the patch level is very old compared to current date â€” increased vulnerability risk.");  
logInfo("2) In Google Play Store â†’ Play Protect â†’ verify scanning is enabled and up to date.");

}

// ============================================================

// LAB 24 â€” Developer Options / ADB Risk Note + UI BUBBLES + AUTO-FIX HINTS
// GEL Security v3.1 (Realtime Snapshot)
// ============================================================
private void lab24DevOptions() {
logLine();
logInfo("LAB 24 â€” Developer Options / ADB Risk Note (Realtime).");

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
        logWarn("USB Debugging ENABLED â€” physical access risk.");  
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
// 3) ADB OVER WIFI (TCP/IP mode â€” port 5555)  
// ============================================================  
boolean adbWifi = isPortOpen(5555, 200);  

logInfo("ADB over Wi-Fi (5555): " + bubble(adbWifi) + " " + (adbWifi ? "ACTIVE" : "OFF"));  

if (adbWifi) {  
    logError("ADB over Wi-Fi ACTIVE â€” remote debugging possible on same network.");  
    risk += 40;  
} else {  
    logOk("ADB over Wi-Fi is OFF.");  
}  

// ============================================================  
// 4) ADB PAIRING MODE (Android 11â€“14 typical ports)  
// ============================================================  
boolean adbPairing =  
        isPortOpen(3700, 200) ||   // some OEM pairing  
        isPortOpen(7460, 200) ||   // pairing service  
        scanPairingPortRange();    // 7460â€“7490  

logInfo("ADB Pairing Mode: " + bubble(adbPairing) + " " + (adbPairing ? "ACTIVE" : "OFF"));  

if (adbPairing) {  
    logError("ADB Pairing is ACTIVE â€” device discoverable for pairing.");  
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
    logWarn("â€¢ Disable Developer Options / USB Debugging:");  
    logInfo("  Settings â†’ System â†’ Developer options â†’ OFF");  
    logInfo("  USB debugging â†’ OFF");  
} else {  
    logOk("â€¢ Developer options & USB debugging look safe.");  
}  

if (adbWifi) {  
    logError("â€¢ ADB over Wi-Fi must be disabled:");  
    logInfo("  Developer options â†’ Wireless debugging â†’ OFF");  
    logInfo("  Or reboot to clear tcpip mode.");  
} else {  
    logOk("â€¢ Wireless debugging is not active.");  
}  

if (adbPairing) {  
    logError("â€¢ Turn OFF ADB Pairing / Wireless debugging:");  
    logInfo("  Developer options â†’ Wireless debugging â†’ OFF");  
} else {  
    logOk("â€¢ ADB Pairing is not active.");  
}  

if (risk >= 60)  
    logError("âš  Very high risk â€” disable ADB features immediately!");  
else if (risk >= 30)  
    logWarn("âš  Partial exposure â€” review ADB settings.");  
else  
    logOk("âœ” Risk level acceptable.");

}

// ============================================================
// UI BUBBLES (GEL)
// ============================================================
private String bubble(boolean on) {
return on ? "ðŸ”´" : "ðŸŸ¢";
}

private String riskBubble(int risk) {
if (risk <= 10) return "ðŸŸ¢";
if (risk <= 30) return "ðŸŸ¡";
if (risk <= 60) return "ðŸŸ ";
return "ðŸ”´";
}

// ============================================================
// HELPERS â€” PORT CHECK (LOCALHOST)
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

// Scan pairing port range 7460â€“7490 (best-effort)
private boolean scanPairingPortRange() {
for (int p = 7460; p <= 7490; p++) {
if (isPortOpen(p, 80)) return true;
}
return false;
}

// ============================================================

// LAB 25 â€” Root / Bootloader Suspicion Checklist (FULL AUTO + RISK SCORE)
// GEL Universal Edition â€” NO external libs
// ============================================================
private void lab25RootSuspicion() {
logLine();
logInfo("LAB 25 â€” Root / Bootloader Integrity Scan (AUTO).");

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

// If system bootanimation missing â†’ suspicious ROM  
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
    for (String s : rootFindings) logWarn("â€¢ " + s);  
}  

// Print BOOTLOADER section  
logLine();  
logInfo("Bootloader / Verified Boot:");  
if (blFindings.isEmpty()) {  
    logOk("No bootloader anomalies detected.");  
} else {  
    for (String s : blFindings) logWarn("â€¢ " + s);  
}  

// Print ANIMATION section  
logLine();  
logInfo("Boot Animation / Splash:");  
if (animFindings.isEmpty()) {  
    logOk("No custom animation traces detected.");  
} else {  
    for (String s : animFindings) logWarn("â€¢ " + s);  
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
// LAB 25 â€” INTERNAL HELPERS (unique names to avoid conflicts)
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
// LABS 26â€“29: ADVANCED / LOGS
// ============================================================

// ============================================================
// LAB 26 â€” GEL Crash Intelligence v5.0 (FULL AUTO EDITION)
// ============================================================
private void lab26CrashHistory() {

logLine();  
logInfo("LAB 26 â€” GEL Crash Intelligence (AUTO)");  

int crashCount = 0;  
int anrCount = 0;  
int systemCount = 0;  

Map<String, Integer> appEvents = new HashMap<>(); // Group per app  
List<String> details = new ArrayList<>();  

// ============================================================  
// (A) Android 11+ â€” Process Exit Reasons  
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
                        details.add("CRASH: " + app + " â€” " + e.shortMsg);  
                    }   
                    else if (e.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {  
                        anrCount++;  
                        details.add("ANR: " + app + " â€” " + e.shortMsg);  
                    }  
                }  
            }  
        }  
    }  
} catch (Exception ignored) {}  

// ============================================================  
// (B) DropBox crash logs â€” legacy Android sources  
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
        (risk <= 20) ? "ðŸŸ©" :  
        (risk <= 50) ? "ðŸŸ¨" :  
        (risk <= 80) ? "ðŸŸ§" : "ðŸŸ¥";  

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
                String c = (e.getValue() >= 10) ? "ðŸŸ¥" :  
                           (e.getValue() >= 5)  ? "ðŸŸ§" :  
                           (e.getValue() >= 2)  ? "ðŸŸ¨" :  
                                                  "ðŸŸ©";  
                logInfo(" " + c + " " + e.getKey() + " â†’ " + e.getValue() + " events");  
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
// LAB 27 â€” App Permissions & Privacy (FULL AUTO + RISK SCORE)
// ============================================================
private void lab27PermissionsPrivacy() {

logLine();  
logInfo("LAB 27 â€” App Permissions & Privacy (AUTO scan)");  

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
                sb.append("â€¢ ").append(shortPerm(perm)).append(" (granted)\n");  
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
                        (appScore >= 60) ? "ðŸŸ¥" :  
                        (appScore >= 30) ? "ðŸŸ§" :  
                        (appScore >= 15) ? "ðŸŸ¨" : "ðŸŸ©";  

                details.add(color + " " + appLabel + " (" + p.packageName + ")"  
                        + " â€” Risk=" + appScore + "\n" + sb.toString());  
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
        (riskPct <= 20) ? "ðŸŸ©" :  
        (riskPct <= 50) ? "ðŸŸ¨" :  
        (riskPct <= 80) ? "ðŸŸ§" : "ðŸŸ¥";  

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
                        (e.getValue() >= 60) ? "ðŸŸ¥" :  
                        (e.getValue() >= 30) ? "ðŸŸ§" :  
                        (e.getValue() >= 15) ? "ðŸŸ¨" : "ðŸŸ©";  

                logInfo(" " + c + " " + safeLabel(pm, e.getKey())  
                        + " â€” Risk " + e.getValue());  
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
// LAB 28 â€” Auto Final Diagnosis Summary (GEL Universal AUTO Edition)
// Combines Thermals + Battery + Storage + RAM + Apps + Uptime +
// Security + Privacy + Root + Stability into final scores.
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================
private void lab28CombineFindings() {
logLine();
logInfo("LAB 28 â€” Auto Final Diagnosis Summary (FULL AUTO)");

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
    logWarn("â€¢ No thermal zones readable. Using Battery temp only: " +  
            String.format(Locale.US, "%.1fÂ°C", battTemp));  
} else {  
    logInfo("â€¢ Zones=" + zones.size() +  
            " | max=" + fmt1(maxThermal) + "Â°C" +  
            " | avg=" + fmt1(avgThermal) + "Â°C");  
    if (cpu != null)  logInfo("â€¢ CPU="  + fmt1(cpu)  + "Â°C");  
    if (gpu != null)  logInfo("â€¢ GPU="  + fmt1(gpu)  + "Â°C");  
    if (pmic != null) logInfo("â€¢ PMIC=" + fmt1(pmic) + "Â°C");  
    if (skin != null) logInfo("â€¢ Skin=" + fmt1(skin) + "Â°C");  
    logInfo("â€¢ Battery=" + fmt1(battTemp) + "Â°C");  
}  

// Battery  
logInfo("Battery: " + batteryFlag + " " + batteryScore + "%");  
logInfo("â€¢ Level=" + (battPct >= 0 ? fmt1(battPct) + "%" : "Unknown") +  
        " | Temp=" + fmt1(battTemp) + "Â°C | Charging=" + charging);  

// Storage  
logInfo("Storage: " + storageFlag + " " + storageScore + "%");  
logInfo("â€¢ Free=" + st.pctFree + "% | Used=" + humanBytes(st.usedBytes) +  
        " / " + humanBytes(st.totalBytes));  

// Apps  
logInfo("Apps Footprint: " + appsFlag + " " + appsScore + "%");  
logInfo("â€¢ User apps=" + ap.userApps + " | System apps=" + ap.systemApps +  
        " | Total=" + ap.totalApps);  

// RAM  
logInfo("RAM: " + ramFlag + " " + ramScore + "%");  
logInfo("â€¢ Free=" + rm.pctFree + "% (" + humanBytes(rm.freeBytes) + " / " +  
        humanBytes(rm.totalBytes) + ")");  

// Stability  
logInfo("Stability/Uptime: " + stabilityFlag + " " + stabilityScore + "%");  
logInfo("â€¢ Uptime=" + formatUptime(upMs));  
if (upMs < 2 * 60 * 60 * 1000L)  
    logWarn("â€¢ Recent reboot detected (<2h) â€” possible instability masking.");  
else if (upMs > 7L * 24L * 60L * 60L * 1000L)  
    logWarn("â€¢ Long uptime (>7d) â€” recommend reboot before deep servicing.");  

// Security  
logInfo("Security: " + securityFlag + " " + securityScore + "%");  
logInfo("â€¢ Lock secure=" + sec.lockSecure);  
logInfo("â€¢ Patch level=" + (sec.securityPatch == null ? "Unknown" : sec.securityPatch));  
logInfo("â€¢ ADB USB=" + sec.adbUsbOn + " | ADB Wi-Fi=" + sec.adbWifiOn +  
        " | DevOptions=" + sec.devOptionsOn);  
if (sec.rootSuspected) logWarn("â€¢ Root suspicion flags detected.");  
if (sec.testKeys) logWarn("â€¢ Build signed with test-keys (custom ROM risk).");  

// Privacy  
logInfo("Privacy: " + privacyFlag + " " + privacyScore + "%");  
logInfo("â€¢ Dangerous perms on user apps: " +  
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
if (verdict.startsWith("ðŸŸ©")) logOk(verdict);  
else if (verdict.startsWith("ðŸŸ¨")) logWarn(verdict);  
else logError(verdict);  

logOk("Lab 28 finished.");

}

// ============================================================
// ======= LAB 28 INTERNAL AUTO HELPERS (SAFE, NO IMPORTS) =====
// ============================================================


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
        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );
        int status = (i != null)
                ? i.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                : -1;

        return status == BatteryManager.BATTERY_STATUS_CHARGING
            || status == BatteryManager.BATTERY_STATUS_FULL;

    } catch (Throwable ignored) {
        return false;
    }
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
if (s >= 80) return "ðŸŸ©";
if (s >= 55) return "ðŸŸ¨";
return "ðŸŸ¥";
}

private String finalVerdict(int health, int sec, int priv, int perf) {
int worst = Math.min(Math.min(health, sec), Math.min(priv, perf));
if (worst >= 80)
return "ðŸŸ© Device is healthy â€” no critical issues detected.";
if (worst >= 55)
return "ðŸŸ¨ Device has moderate risks â€” recommend service check.";
return "ðŸŸ¥ Device is NOT healthy â€” immediate servicing recommended.";
}

private String fmt1(float v) {
return String.format(Locale.US, "%.1f", v);
}

// ============================================================
// LAB 29 â€” FINAL TECHNICIAN SUMMARY (READ-ONLY)
// Does NOT modify GELServiceLog â€” only reads it.
// Exports via ServiceReportActivity.
// ============================================================
private void lab29FinalSummary() {

    logLine();
    logInfo("LAB 29 â€” Final Technician Summary (READ-ONLY)");

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

        if (low.contains("âš ") || low.contains("warning")) {
            warnings.append(l).append("\n");
        }
        if (low.contains("âŒ") || low.contains("error")) {
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
// ENABLE EXISTING EXPORT BUTTON â€” No duplicates!
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
   Earpiece test tone â€” 220Hz (CALL PATH SAFE)
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

    if (requestCode == 6006) { // LAB 6 â€” Touch Grid
        if (resultCode == RESULT_OK)
            logOk("LAB 6 â€” Touch grid passed (all zones responsive)");
        else
            logError("LAB 6 â€” Touch grid failed (dead zone suspected)");

        enableSingleExportButton();
        return;
    }

    if (requestCode == 7007) { // LAB 7 â€” Rotation
        if (resultCode == RESULT_OK)
            logOk("LAB 7 â€” Rotation detected via sensors");
        else
            logError("LAB 7 â€” No rotation detected");

        enableSingleExportButton();
        return;
    }

    if (requestCode == 8008) { // LAB 8 â€” Proximity
        if (resultCode == RESULT_OK)
            logOk("LAB 8 â€” Proximity sensor responded correctly");
        else
            logError("LAB 8 â€” No proximity response detected");

        enableSingleExportButton();
    }
}

// ============================================================
// LAB 15 SAFETY CLEANUP â€” DO NOT REMOVE
// ============================================================
@Override
protected void onDestroy() {
    super.onDestroy();
    try {
        if (chargingReceiver != null) {
            unregisterReceiver(chargingReceiver);
            chargingReceiver = null;
        }
    } catch (Throwable ignore) {}
}

// ============================================================
// END OF CLASS
// ============================================================
}
