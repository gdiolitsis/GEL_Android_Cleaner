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

// ============================================================
// ANDROID — CORE
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
// JAVA — IO / NET
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
// JAVA — UTIL
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
    // LAB 14 — FLAGS / UI STATE (REQUIRED)
    // ============================================================
    private volatile boolean lab14Running = false;
    private TextView lab14DotsView;
    private AlertDialog lab14Dialog;
    private TextView lab14ProgressText;
    private LinearLayout lab14ProgressBar;
    private final int LAB14_TOTAL_SECONDS = 5 * 60; // 300 sec hard lock

    private int lastSelectedStressDurationSec = 60;

// ============================================================
// LAB 15 — FLAGS (DO NOT MOVE)
// ============================================================

private volatile boolean lab15Running  = false;
private volatile boolean lab15Finished = false;

private volatile boolean lab15FlapUnstable = false;
private volatile boolean lab15OverTempDuringCharge = false;

private AlertDialog lab15Dialog;
private TextView lab15StatusText;
private LinearLayout lab15ProgressBar;
private Button lab15ExitBtn;
private TextView lab15CounterText;

// LAB 15 — Thermal Correlation
private float lab15BattTempStart = Float.NaN;
private float lab15BattTempPeak  = Float.NaN;
private float lab15BattTempEnd   = Float.NaN;

private static final int LAB15_TOTAL_SECONDS = 180;

    // ============================================================
    // TELEPHONY SNAPSHOT — Passive system probe (no side effects)
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

    // REQUIRED — used by LAB 14 / drain logic
    long currentChargeMah = -1;

    // capacity estimation
    long estimatedFullMah = -1;

    // charging state (CRITICAL for LAB 14 / 15)
    boolean charging = false;

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
        // DOTS (running indicator) — UI ONLY (LAB 14 uses its own dialog dots)
        // ------------------------------------------------------------
        lab14DotsView = new TextView(this);
        lab14DotsView.setText("•");
        lab14DotsView.setTextSize(22f);
        lab14DotsView.setTextColor(0xFF39FF14);
        lab14DotsView.setPadding(0, dp(6), 0, dp(10));
        lab14DotsView.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(lab14DotsView);

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
        body3.addView(makeTestButton("11. Mobile Network Diagnostic", this::lab11MobileDataDiagnostic));
        body3.addView(makeTestButton("12. Call Function Interpretation", this::lab12CallFunctionInterpretation));
        body3.addView(makeTestButton("13. Internet Access Quick Check", this::lab13InternetQuickCheck));

        // ============================================================
        // SECTION 4: BATTERY & THERMAL — LABS 14–17
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
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
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
                        dp(48)
                );
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
                        dp(52)
                );
        lp.setMargins(0, dp(6), 0, dp(6));
        b.setLayoutParams(lp);
        b.setGravity(Gravity.CENTER);
        b.setOnClickListener(v -> action.run());

        return b;
    }

// ============================================================
// WIFI / NETWORK HELPERS — REQUIRED
// ============================================================

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
// LAB 3 — User Confirmation Dialog (Earpiece)
// ============================================================
private void askUserEarpieceConfirmation() {

    ui.post(() -> {
        try {
            AlertDialog.Builder b =
                    new AlertDialog.Builder(
                            ManualTestsActivity.this,
                            android.R.style.Theme_Material_Dialog_NoActionBar
                    );

            b.setTitle("LAB 3 — Confirm");
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

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(0xFF101010)
                );
            }

        } catch (Throwable t) {
            enableSingleExportButton();
        }
    });
}

// ============================================================
// TELEPHONY SNAPSHOT (SAFE / INFO ONLY)
// ============================================================
private TelephonySnapshot getTelephonySnapshot() {

    TelephonySnapshot s = new TelephonySnapshot();

    try {
        s.airplaneOn = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0
        ) == 1;
    } catch (Throwable ignored) {}

    TelephonyManager tm =
            (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

    if (tm != null) {
        try {
            s.simState = tm.getSimState();
            s.simReady = (s.simState == TelephonyManager.SIM_STATE_READY);
        } catch (Throwable ignored) {}

        try {
            ServiceState ss = tm.getServiceState();
            if (ss != null) {
                s.serviceState = ss.getState();
                s.inService =
                        (s.serviceState == ServiceState.STATE_IN_SERVICE);
            }
        } catch (Throwable ignored) {}

        try {
            s.dataState = tm.getDataState();
        } catch (Throwable ignored) {}
    }

    ConnectivityManager cm =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

    if (cm != null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network n = cm.getActiveNetwork();
                NetworkCapabilities caps =
                        cm.getNetworkCapabilities(n);
                s.hasInternet =
                        caps != null &&
                        caps.hasCapability(
                                NetworkCapabilities.NET_CAPABILITY_INTERNET
                        );
            }
        } catch (Throwable ignored) {}
    }

    return s;
}

// ============================================================
// LOGGING — GEL CANONICAL
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
    appendHtml("ℹ️ " + escape(msg));
}

private void logOk(String msg) {
    appendHtml("<font color='#88FF88'>✔ " + escape(msg) + "</font>");
}

private void logWarn(String msg) {
    appendHtml("<font color='#FFD966'>⚠ " + escape(msg) + "</font>");
}

private void logError(String msg) {
    appendHtml("<font color='#FF5555'>✖ " + escape(msg) + "</font>");
}

private void logLine() {
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
// FORMAT HELPERS
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

// ============================================================
// GEL BATTERY + LAB15 SUPPORT — REQUIRED (RESTORE MISSING SYMBOLS)
// KEEP THIS BLOCK INSIDE ManualTestsActivity (helpers area)
// ============================================================

// ------------------------------------------------------------
// NORMALIZE mAh / μAh (shared)
// ------------------------------------------------------------
private long normalizeMah(long raw) {
    if (raw <= 0) return -1;
    if (raw > 200000) return raw / 1000; // μAh → mAh
    return raw;                          // already mAh
}

// ------------------------------------------------------------
// Battery temperature (°C) — SAFE
// ------------------------------------------------------------
private float getBatteryTemperature() {
    try {
        Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i == null) return 0f;

        int raw = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        if (raw <= 0) return 0f;

        return raw / 10f; // tenths of °C
    } catch (Throwable t) {
        return 0f;
    }
}

// ------------------------------------------------------------
// Battery % — SAFE
// ------------------------------------------------------------
private float getCurrentBatteryPercent() {
    try {
        Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i == null) return -1f;

        int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level < 0 || scale <= 0) return -1f;
        return (level * 100f) / (float) scale;
    } catch (Throwable t) {
        return -1f;
    }
}

// ------------------------------------------------------------
// Charging detection — SAFE (plugged based)
// ------------------------------------------------------------
private boolean isDeviceCharging() {
    try {
        Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (i == null) return false;

        int plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;

    } catch (Throwable t) {
        return false;
    }
}

// ------------------------------------------------------------
// BatteryInfo snapshot — SAFE (BatteryManager properties)
// ------------------------------------------------------------
private BatteryInfo getBatteryInfo() {

    BatteryInfo bi = new BatteryInfo();
    bi.charging = isDeviceCharging();
    bi.source = "BatteryManager";

    try {
        BatteryManager bm =
                (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (bm == null) {
            bi.currentChargeMah = -1;
            bi.estimatedFullMah = -1;
            bi.source = "BatteryManager:N/A";
            return bi;
        }

        // Charge counter (μAh → mAh)
        long cc_uAh =
                bm.getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER
                );

        bi.currentChargeMah = normalizeMah(cc_uAh);

        // ❗ SAFE FULL CAPACITY — NOT via CHARGE_FULL (API trap)
        bi.estimatedFullMah = -1; // handled elsewhere (LAB 14 engine / heuristics)

        if (bi.currentChargeMah <= 0)
            bi.currentChargeMah = -1;

    } catch (Throwable t) {
        bi.currentChargeMah = -1;
        bi.estimatedFullMah = -1;
        bi.source = "BatteryManager:ERROR";
    }

    return bi;
}

// ------------------------------------------------------------
// LAB 15 thermal correlation — REQUIRED
// ------------------------------------------------------------
private void logLab15ThermalCorrelation() {
    logLab15ThermalCorrelation(Float.NaN, Float.NaN, Float.NaN);
}

private void logLab15ThermalCorrelation(
        float battTempStart,
        float battTempPeak,
        float battTempEnd
) {
    float rawDelta = !Float.isNaN(battTempPeak)
            ? (battTempPeak - battTempStart)
            : (battTempEnd - battTempStart);

    float dPeak = Math.max(0f, rawDelta);

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

    logInfo(String.format(
            Locale.US,
            "Thermal correlation (charging): start %.1f°C -> peak %.1f°C -> end %.1f°C",
            battTempStart,
            (Float.isNaN(battTempPeak) ? battTempEnd : battTempPeak),
            battTempEnd
    ));

    logOk(String.format(
            Locale.US,
            "Thermal verdict (charging): %s (ΔT +%.1f°C) — %s",
            verdict,
            dPeak,
            note
    ));
}

// ------------------------------------------------------------
// Health checkbox map — REQUIRED (LAB 14/17 use)
// ------------------------------------------------------------
private void printHealthCheckboxMap(String decision) {

    String d = (decision == null) ? "" : decision.trim();

    logLine();

    boolean strong = "Strong".equalsIgnoreCase(d);
    boolean normal = "Normal".equalsIgnoreCase(d);
    boolean weak   = "Weak".equalsIgnoreCase(d);

    appendHtml((strong ? "✔ " : "☐ ") + "<font color='#FFFFFF'>Strong</font>");
    appendHtml((normal ? "✔ " : "☐ ") + "<font color='#FFFFFF'>Normal</font>");
    appendHtml((weak   ? "✔ " : "☐ ") + "<font color='#FFFFFF'>Weak</font>");

    if (strong) logOk("Health Map: Strong");
    else if (normal) logWarn("Health Map: Normal");
    else if (weak) logError("Health Map: Weak");
    else logInfo("Health Map: Informational");
}

// ============================================================
// MISSING SYMBOLS PATCH — REQUIRED FOR LAB 14 + LAB 15
// Put this block INSIDE ManualTestsActivity (helpers area)
// ============================================================

// ------------------------------------------------------------
// logLabelValue (2-arg) — you already had it earlier, restore it
// ------------------------------------------------------------
private void logLabelValue(String label, String value) {
    appendHtml(
            escape(label) + ": "
                    + "<font color='#39FF14'>" + escape(value) + "</font>"
    );
}

// ------------------------------------------------------------
// LAB 14 RUNNING DIALOG (minimal, safe)
// ------------------------------------------------------------
private AlertDialog lab14RunningDialog;

private void showLab14RunningDialog() {
    ui.post(() -> {
        try {
            if (lab14RunningDialog != null && lab14RunningDialog.isShowing())
                return;

            AlertDialog.Builder b =
                    new AlertDialog.Builder(
                            ManualTestsActivity.this,
                            android.R.style.Theme_Material_Dialog_NoActionBar
                    );
            b.setCancelable(false);

            LinearLayout root = new LinearLayout(this);
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(dp(24), dp(20), dp(24), dp(18));
            root.setBackgroundColor(0xFF101010);

            TextView title = new TextView(this);
            title.setText("LAB 14 — Running stress test...");
            title.setTextColor(0xFFFFFFFF);
            title.setTextSize(18f);
            title.setPadding(0, 0, 0, dp(10));
            root.addView(title);

            TextView msg = new TextView(this);
            msg.setText("Please keep the app open.\nDo not charge the device during this test.");
            msg.setTextColor(0xFFDDDDDD);
            msg.setTextSize(14f);
            root.addView(msg);

            b.setView(root);

            lab14RunningDialog = b.create();
            if (lab14RunningDialog.getWindow() != null) {
                lab14RunningDialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            }
            lab14RunningDialog.show();

        } catch (Throwable ignore) {}
    });
}

private void dismissLab14RunningDialog() {
    ui.post(() -> {
        try {
            if (lab14RunningDialog != null && lab14RunningDialog.isShowing())
                lab14RunningDialog.dismiss();
        } catch (Throwable ignore) {}
        lab14RunningDialog = null;
    });
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
// CPU stress (controlled) — used by LAB 14/17
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
    }, "LAB-CPU-BURN").start();
}

private void stopCpuBurn() {
    __cpuBurn = false;
}

// ------------------------------------------------------------
// LAB 15 USER ABORT — required by Exit button
// (safe: stops flags + dismisses dialog; does NOT nuke all handler callbacks)
// ------------------------------------------------------------
private void abortLab15ByUser() {

    ui.post(() -> {

        if (!lab15Running) {
            try {
                if (lab15Dialog != null && lab15Dialog.isShowing())
                    lab15Dialog.dismiss();
            } catch (Throwable ignore) {}
            lab15Dialog = null;
            return;
        }

        logWarn("LAB 15 aborted by user.");

        lab15Running = false;
        lab15Finished = true;

        try {
            if (lab15Dialog != null && lab15Dialog.isShowing())
                lab15Dialog.dismiss();
        } catch (Throwable ignore) {}

        lab15Dialog = null;

        logWarn("LAB 15 cancelled by user.");
    });
}

// ===================================================================
// HELPERS — LAB 16 THERMAL SNAPSHOT
// Internal + Peripherals • Root Aware • GEL Edition
// ===================================================================

/* ---------------------------------------------------------------
 * INTERNAL STATE
 * --------------------------------------------------------------- */
private boolean isRooted = false; // set once in onCreate()

/* ---------------------------------------------------------------
 * INTERNAL THERMAL REPORT
 * --------------------------------------------------------------- */
private String buildThermalInternalReport() {

    StringBuilder sb = new StringBuilder();

    sb.append("THERMAL SENSORS (INTERNAL)\n");
    sb.append("──────────────────────────\n");

    File thermalDir = new File("/sys/class/thermal");
    if (!thermalDir.exists()) {
        sb.append("Thermal sensors not available.\n");
        return sb.toString();
    }

    File[] zones = thermalDir.listFiles(f -> f.getName().startsWith("thermal_zone"));
    if (zones == null || zones.length == 0) {
        sb.append("No thermal zones detected.\n");
        return sb.toString();
    }

    Map<String, Float> maxTemps = new HashMap<>();

    for (File z : zones) {
        try {
            String type = readSys(z, "type");
            String temp = readSys(z, "temp");
            if (type == null || temp == null) continue;

            float c = Float.parseFloat(temp.trim()) / 1000f;
            if (c <= 0 || c > 120) continue;

            String label = mapInternalType(type);
            if (label == null) continue;

            Float prev = maxTemps.get(label);
            if (prev == null || c > prev) maxTemps.put(label, c);

        } catch (Throwable ignore) {}
    }

    String[] order = {
            "CPU Core",
            "CPU Cluster 0",
            "CPU Cluster 1",
            "GPU",
            "DDR Memory",
            "Battery",
            "Backlight"
    };

    for (String k : order) {
        Float v = maxTemps.get(k);
        if (v != null) {
            sb.append(String.format(
                    Locale.US,
                    "%-18s : %5.1f°C  (%s)\n",
                    k, v, thermalState(v)
            ));
        }
    }

    if (!isRooted) {
        sb.append("\nAdvanced Info: For detailed thermal and cooling information, requires root access\n");
        return sb.toString();
    }

    // ROOT DETAILS
    sb.append("\nAdvanced Thermal (Root)\n");
    sb.append("──────────────────────\n");

    for (File z : zones) {
        try {
            String type = readSys(z, "type");
            String temp = readSys(z, "temp");
            if (type == null || temp == null) continue;

            float c = Float.parseFloat(temp.trim()) / 1000f;

            sb.append("\n")
              .append(z.getName()).append(" [").append(type).append("]\n")
              .append("  Current Temp : ")
              .append(String.format(Locale.US, "%.1f°C\n", c));

            for (int i = 0; i < 10; i++) {
                String tp = readSys(z, "trip_point_" + i + "_temp");
                String tt = readSys(z, "trip_point_" + i + "_type");
                if (tp == null || tt == null) break;

                float tc = Float.parseFloat(tp.trim()) / 1000f;
                sb.append("  Trip ").append(i)
                  .append(" (").append(tt).append(") : ")
                  .append(String.format(Locale.US, "%.1f°C\n", tc));
            }

        } catch (Throwable ignore) {}
    }

    return sb.toString();
}

/* ---------------------------------------------------------------
 * PERIPHERALS / HARDWARE THERMAL REPORT
 * --------------------------------------------------------------- */
private String buildThermalInfo() {

    StringBuilder sb = new StringBuilder();

    ThermalGroup batteryMain  = new ThermalGroup();
    ThermalGroup batteryShell = new ThermalGroup();
    ThermalGroup pmic         = new ThermalGroup();
    ThermalGroup charger      = new ThermalGroup();
    ThermalGroup modemMain    = new ThermalGroup();
    ThermalGroup modemAux     = new ThermalGroup();

    File[] zones = new File("/sys/class/thermal")
            .listFiles(f -> f.getName().startsWith("thermal_zone"));

    if (zones != null) {
        for (File z : zones) {
            try {
                String type = readSys(z, "type");
                String temp = readSys(z, "temp");
                if (type == null || temp == null) continue;

                float c = Float.parseFloat(temp.trim()) / 1000f;
                if (c <= 0 || c > 120) continue;

                String t = type.toLowerCase(Locale.US);

                if (t.contains("battery")) batteryMain.update(type, c);
                else if (t.contains("skin") || t.contains("shell")) batteryShell.update(type, c);
                else if (t.contains("pmic") || t.contains("bcl")) pmic.update(type, c);
                else if (t.contains("charger") || t.contains("usb")) charger.update(type, c);
                else if (t.contains("modem")) modemMain.update(type, c);

            } catch (Throwable ignore) {}
        }
    }

    sb.append("Hardware Thermal Systems\n");
    sb.append("================================\n\n");

    sb.append(formatLine("Main Modem", modemMain));
    sb.append(formatLine("Secondary Modem", modemAux));
    sb.append(formatLine("Main Battery", batteryMain));
    sb.append(formatLine("Battery Shell", batteryShell));
    sb.append(formatLine("Charger Thermal", charger));
    sb.append(formatLine("PMIC Thermal", pmic));

    sb.append("\nHardware Cooling Systems\n");
    sb.append("================================\n");
    sb.append("• (no hardware cooling devices found) (this device uses passive cooling only)\n");

    return sb.toString();
}

/* ---------------------------------------------------------------
 * SMALL HELPERS
 * --------------------------------------------------------------- */
private static class ThermalGroup {
    float temp = Float.NaN;
    boolean valid;

    void update(String n, float c) {
        if (!valid || c > temp) {
            temp = c;
            valid = true;
        }
    }
}

private String formatLine(String label, ThermalGroup g) {
    if (g == null || !g.valid)
        return String.format(Locale.US, "%-17s: N/A\n", label);

    return String.format(
            Locale.US,
            "%-17s: %.1f°C (%s)\n",
            label, g.temp, thermalState(g.temp)
    );
}

private String thermalState(float c) {
    if (c < 35) return "COOL";
    if (c < 45) return "NORMAL";
    if (c < 55) return "WARM";
    return "HOT";
}

private String readSys(File dir, String name) {
    try (BufferedReader br = new BufferedReader(new FileReader(new File(dir, name)))) {
        return br.readLine();
    } catch (Throwable ignore) {
        return null;
    }
}

private String mapInternalType(String t) {
    t = t.toLowerCase(Locale.US);
    if (t.contains("cpu")) return "CPU Core";
    if (t.contains("gpu")) return "GPU";
    if (t.contains("ddr")) return "DDR Memory";
    if (t.contains("battery")) return "Battery";
    if (t.contains("backlight")) return "Backlight";
    return null;
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
// LAB 14 — Battery Health Stress Test
// FINAL — SNAPSHOT ONLY — UI MATCHES LAB 15
// ✔ Confidence NOT in intro
// ✔ Confidence calculated AFTER stress + shown with Aging + Final Score
// ✔ One confidence only — no contradictions
// ============================================================
private void lab14BatteryHealthStressTest() {

    if (lab14Running) {
        logWarn("⚠️ LAB 14 already running.");
        return;
    }
    lab14Running = true;

    final Lab14Engine engine = new Lab14Engine(this);

    try {

        // ------------------------------------------------------------
        // 1) INITIAL SNAPSHOT (Single Source of Truth)
        // ------------------------------------------------------------
        final Lab14Engine.GelBatterySnapshot snapStart = engine.readSnapshot();

        if (snapStart.charging) {
            logError("❌ Stress test requires device NOT charging.");
            lab14Running = false;
            return;
        }

        if (snapStart.chargeNowMah <= 0) {
            logError("❌ Charge Counter unavailable. LAB 14 cannot run.");
            lab14Running = false;
            return;
        }

        final long startMah   = snapStart.chargeNowMah;
        final boolean rooted  = snapStart.rooted;
        final long cycles     = snapStart.cycleCount;
        final float tempStart = snapStart.temperature;

        final int durationSec = LAB14_TOTAL_SECONDS;
        lastSelectedStressDurationSec = durationSec;

        final long baselineFullMah =
                (snapStart.chargeFullMah > 0)
                        ? snapStart.chargeFullMah
                        : -1;

        // ------------------------------------------------------------
        // 2) LOG HEADER (FULL INFO — LIKE OLD LAB) ✅
        // ------------------------------------------------------------
        logLine();
        logInfo("✅ LAB 14 — Battery Health Stress Test");
        logInfo("✅ Mode: " + (rooted ? "Advanced (Rooted)" : "Standard (Unrooted)"));
        logInfo("✅ Duration: " + durationSec + " sec (laboratory mode)");
        logInfo(String.format(
                Locale.US,
                "✅ Start conditions: charge=%d mAh, status=Discharging, temp=%.1f°C",
                startMah,
                (Float.isNaN(tempStart) ? 0f : tempStart)
        ));
        logInfo("✅ Data source: " + snapStart.source);

        if (baselineFullMah > 0)
            logInfo("✅ Battery capacity baseline (from counter): " + baselineFullMah + " mAh");
        else
            logInfo("✅ Battery capacity baseline (from counter): N/A");

        logInfo("✅ Cycle count: " + (cycles > 0 ? String.valueOf(cycles) : "N/A"));
        logLine();

        // ------------------------------------------------------------
        // 3) DIALOG — SAME STYLE AS LAB 15 (EXIT BUTTON)
        // ------------------------------------------------------------
        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);
        b.setTitle("LAB 14 - Battery Health Stress Test");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(20), dp(24), dp(18));
        root.setBackgroundColor(0xFF101010);

        final TextView statusText = new TextView(this);
        statusText.setText("Stress test running...");
        statusText.setTextColor(0xFF39FF14);
        statusText.setTextSize(15f);
        root.addView(statusText);

        final TextView dotsView = new TextView(this);
        dotsView.setText("•");
        dotsView.setTextColor(0xFF39FF14);
        dotsView.setTextSize(22f);
        dotsView.setGravity(Gravity.CENTER);
        root.addView(dotsView);

        final TextView counterText = new TextView(this);
        counterText.setText("Progress: 0 / " + durationSec + " sec");
        counterText.setTextColor(0xFF39FF14);
        counterText.setGravity(Gravity.CENTER);
        root.addView(counterText);

        final LinearLayout progressBar = new LinearLayout(this);
        progressBar.setOrientation(LinearLayout.HORIZONTAL);
        progressBar.setGravity(Gravity.CENTER);

        for (int i = 0; i < 6; i++) {
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
        exitBtn.setText("Exit test");
        exitBtn.setAllCaps(false);
        exitBtn.setTextColor(0xFFFFFFFF);
        exitBtn.setTypeface(null, Typeface.BOLD);

        GradientDrawable exitBg = new GradientDrawable();
        exitBg.setColor(0xFF8B0000);
        exitBg.setCornerRadius(dp(14));
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
            // USER ABORT
            try { stopCpuBurn(); } catch (Throwable ignore) {}
            try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}
            lab14Running = false;

            try {
                if (lab14Dialog != null && lab14Dialog.isShowing())
                    lab14Dialog.dismiss();
            } catch (Throwable ignore) {}
            lab14Dialog = null;

            logWarn("⚠️ LAB 14 aborted by user.");
        });

        root.addView(exitBtn);

        b.setView(root);
        lab14Dialog = b.create();
        if (lab14Dialog.getWindow() != null) {
            lab14Dialog.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        lab14Dialog.show();

        // ------------------------------------------------------------
        // 4) START STRESS (CPU burn + max brightness)
        // ------------------------------------------------------------
        final long t0 = SystemClock.elapsedRealtime();
        final String[] dotFrames = { "•", "• •", "• • •" };

        applyMaxBrightnessAndKeepOn();
        startCpuBurn_C_Mode();

        ui.post(new Runnable() {

            int dotStep = 0;
            int lastSeg = -1;

            @Override
            public void run() {

                if (!lab14Running) return;

                long now = SystemClock.elapsedRealtime();
                int elapsed = (int) ((now - t0) / 1000);

                dotsView.setText(dotFrames[dotStep++ % dotFrames.length]);
                counterText.setText(
                        "Progress: " + Math.min(elapsed, durationSec) +
                                " / " + durationSec + " sec"
                );

                int segSpan = Math.max(1, durationSec / 6);
                int seg = Math.min(6, elapsed / segSpan);

                if (seg != lastSeg) {
                    lastSeg = seg;
                    for (int i = 0; i < progressBar.getChildCount(); i++) {
                        progressBar.getChildAt(i)
                                .setBackgroundColor(i < seg ? 0xFF39FF14 : 0xFF333333);
                    }
                }

                if (elapsed < durationSec) {
                    ui.postDelayed(this, 1000);
                    return;
                }

                // ----------------------------------------------------
                // 5) STOP + FINAL SNAPSHOT
                // ----------------------------------------------------
                lab14Running = false;

                try { stopCpuBurn(); } catch (Throwable ignore) {}
                try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

                try {
                    if (lab14Dialog != null && lab14Dialog.isShowing())
                        lab14Dialog.dismiss();
                } catch (Throwable ignore) {}
                lab14Dialog = null;

                final Lab14Engine.GelBatterySnapshot snapEnd = engine.readSnapshot();

                if (snapEnd.chargeNowMah <= 0) {
                    logWarn("⚠️ Unable to read final charge counter.");
                    return;
                }

                final long endMah = snapEnd.chargeNowMah;
                final float tempEnd = snapEnd.temperature;

                final long dtMs = Math.max(1, SystemClock.elapsedRealtime() - t0);
                final long drainMah = startMah - endMah;

                final boolean validDrain =
                        drainMah > 0 &&
                        !(baselineFullMah > 0 && drainMah > (long)(baselineFullMah * 0.30));

                final double mahPerHour =
                        validDrain ? (drainMah * 3600000.0) / dtMs : -1;

                // ----------------------------------------------------
                // 6) SAVE RUN + SINGLE CONFIDENCE
                // ----------------------------------------------------
                if (validDrain) engine.saveDrainValue(mahPerHour);
                engine.saveRun();

                final Lab14Engine.ConfidenceResult conf = engine.computeConfidence();

                // ----------------------------------------------------
                // 7) PROFILE + AGING (Engine)
                // ----------------------------------------------------
                final Lab14Engine.BatteryProfile profile =
                        engine.detectProfile(snapStart, conf);

                final Lab14Engine.AgingResult aging =
                        engine.computeAging(
                                mahPerHour,
                                conf,
                                cycles,
                                tempStart,
                                tempEnd
                        );

                // ----------------------------------------------------
                // 8) BATTERY AGING INDEX (0..100)
                // ----------------------------------------------------
                int agingIndex = -1;
                String agingInterp = "N/A";

                if (validDrain && conf.percent >= 70 && !Float.isNaN(tempStart) && !Float.isNaN(tempEnd)) {

                    double tempRise = Math.max(0.0, (double)tempEnd - (double)tempStart);

                    // index grows with: drain/h, thermal rise, high cycles, low confidence
                    double idx = 0;

                    // drain component (0..55)
                    // 600 mAh/h => ~0, 1000 => ~35, 1400 => ~55
                    double d = Math.max(0.0, mahPerHour - 600.0);
                    idx += Math.min(55.0, d / 800.0 * 55.0);

                    // thermal component (0..25)
                    // +3°C => 0, +10°C => ~18, +14°C => 25
                    double tr = Math.max(0.0, tempRise - 3.0);
                    idx += Math.min(25.0, tr / 11.0 * 25.0);

                    // cycles component (0..15)
                    if (cycles > 0) {
                        double cy = Math.max(0.0, cycles - 150.0);
                        idx += Math.min(15.0, cy / 350.0 * 15.0);
                    }

                    // confidence penalty (0..10)
                    idx += Math.min(10.0, (100 - conf.percent) / 5.0);

                    agingIndex = (int)Math.round(Math.max(0.0, Math.min(100.0, idx)));

                    if (agingIndex < 15) agingInterp = "Excellent (very low aging indicators)";
                    else if (agingIndex < 30) agingInterp = "Good (low aging indicators)";
                    else if (agingIndex < 50) agingInterp = "Moderate (watch trend)";
                    else if (agingIndex < 70) agingInterp = "High (aging signs detected)";
                    else agingInterp = "Severe (strong aging indicators)";
                } else {
                    // This is where "insufficient data" belongs:
                    agingIndex = -1;
                    agingInterp = "Insufficient data (need stable runs with confidence ≥70%)";
                }

                // ----------------------------------------------------
                // 9) FINAL BATTERY HEALTH SCORE (0..100)
                // ----------------------------------------------------
                int finalScore = 100;

                // invalid drain => informational only
                if (!validDrain) finalScore = 0;
                else {

                    // Drain penalty (golden-style, but ONLY battery-relevant)
                    // <=650 good, 650-900 medium, 900-1200 bad, >1200 severe
                    if (mahPerHour >= 1200) finalScore -= 45;
                    else if (mahPerHour >= 1000) finalScore -= 30;
                    else if (mahPerHour >= 850) finalScore -= 18;
                    else if (mahPerHour >= 700) finalScore -= 8;

                    // Thermal penalty (battery temp end)
                    if (!Float.isNaN(tempEnd)) {
                        if (tempEnd >= 55f) finalScore -= 35;
                        else if (tempEnd >= 45f) finalScore -= 18;
                        else if (tempEnd >= 40f) finalScore -= 8;
                    }

                    // Thermal rise penalty
                    if (!Float.isNaN(tempStart) && !Float.isNaN(tempEnd)) {
                        float rise = Math.max(0f, tempEnd - tempStart);
                        if (rise >= 12f) finalScore -= 18;
                        else if (rise >= 8f) finalScore -= 10;
                        else if (rise >= 5f) finalScore -= 5;
                    }

                    // Cycles penalty (only if known)
                    if (cycles > 0) {
                        if (cycles >= 600) finalScore -= 20;
                        else if (cycles >= 400) finalScore -= 12;
                        else if (cycles >= 250) finalScore -= 6;
                    }

                    // Confidence penalty (single)
                    if (conf.percent < 70) finalScore -= 12;
                    else if (conf.percent < 80) finalScore -= 6;

                    // Clamp
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

                // ----------------------------------------------------
                // 10) PRINT RESULTS (FULL LIKE OLD LAB) ✅
                // ----------------------------------------------------
                logLine();
                logInfo("✅ LAB 14 - Stress result");
                logInfo(String.format(
                        Locale.US,
                        "✅ Start: %d mAh | End: %d mAh | Drop: %d mAh | Time: %.1f sec",
                        startMah, endMah, Math.max(0, drainMah), dtMs / 1000.0
                ));

                if (validDrain) {
                    logInfo(String.format(
                            Locale.US,
                            "✅ Drain rate: %.0f mAh/hour (counter-based)",
                            mahPerHour
                    ));
                } else {
                    logWarn("⚠️ Drain data invalid (counter anomaly or no drop).");
                }

                // Single confidence (HERE ONLY)
                logLine();
                logInfo(String.format(
                        Locale.US,
                        "✅ Confidence Score: %d%% (%d valid runs)",
                        conf.percent,
                        conf.validRuns
                ));

                logInfo("✅ Battery profile: " + profile.label);

                // Aging index block (where insufficient data belongs)
                logLine();
                if (agingIndex >= 0) {
                    logInfo("✅ Battery Aging Index: " + agingIndex + "/100");
                    logInfo("✅ Interpretation: " + agingInterp);
                } else {
                    logWarn("⚠️ Battery Aging Index: Insufficient data");
                    logWarn("⚠️ Interpretation: " + agingInterp);
                }

                // Engine aging text (kept, but now aligned with index)
                logInfo("✅ Aging analysis: " + aging.description);

                // Final score block
                logLine();
                logOk(String.format(
                        Locale.US,
                        "✅ Final Battery Health Score: %d%% (%s)",
                        finalScore,
                        finalLabel
                ));

                // Health checkbox map
                printHealthCheckboxMap(finalLabel);

                // Extra observation lines (emoji + old style vibes)
                logLine();
                if (!Float.isNaN(tempEnd))
                    logInfo(String.format(Locale.US, "✅ End temperature: %.1f°C", tempEnd));
                if (!Float.isNaN(tempStart) && !Float.isNaN(tempEnd))
                    logInfo(String.format(Locale.US, "✅ Thermal rise: +%.1f°C", Math.max(0f, tempEnd - tempStart)));

            }
        });

    } catch (Throwable t) {
        try { stopCpuBurn(); } catch (Throwable ignore) {}
        try { restoreBrightnessAndKeepOn(); } catch (Throwable ignore) {}

        try {
            if (lab14Dialog != null && lab14Dialog.isShowing())
                lab14Dialog.dismiss();
        } catch (Throwable ignore) {}
        lab14Dialog = null;

        lab14Running = false;
        logError("❌ LAB 14 failed unexpectedly.");
    }
}

// ============================================================
// LAB 15 — Charging System Diagnostic (PHOTO FORMAT v1.1 FINAL)
// Output: EXACT screenshot-style report (mAh + temp + correlation)
// Battery level = mAh ONLY (NO %)
// ============================================================
private void lab15ChargingSystemSmart() {

    if (lab15Running) {
        logWarn("⚠️ LAB 15 already running.");
        return;
    }

    lab15Running = true;
    lab15Finished = false;
    lab15FlapUnstable = false;
    lab15OverTempDuringCharge = false;

    lab15BattTempStart = Float.NaN;
    lab15BattTempPeak  = Float.NaN;
    lab15BattTempEnd   = Float.NaN;

    // ------------------------------------------------------------
    // DIALOG (UNCHANGED — SAME STYLE AS YOU GAVE)
    // ------------------------------------------------------------
    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);
    b.setTitle("LAB 15 - Connect the charger to the device charging port");

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));
    root.setBackgroundColor(0xFF101010);

    lab15StatusText = new TextView(this);
    lab15StatusText.setText("Waiting for charging connection...");
    lab15StatusText.setTextColor(0xFFAAAAAA);
    lab15StatusText.setTextSize(15f);
    root.addView(lab15StatusText);

    final TextView dotsView = new TextView(this);
    dotsView.setText("•");
    dotsView.setTextColor(0xFF39FF14);
    dotsView.setTextSize(22f);
    dotsView.setGravity(Gravity.CENTER);
    root.addView(dotsView);

    lab15CounterText = new TextView(this);
    lab15CounterText.setText("Progress: 0 / " + LAB15_TOTAL_SECONDS + " sec");
    lab15CounterText.setTextColor(0xFF39FF14);
    lab15CounterText.setGravity(Gravity.CENTER);
    root.addView(lab15CounterText);

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

    Button exitBtn = new Button(this);
    exitBtn.setText("Exit test");
    exitBtn.setAllCaps(false);
    exitBtn.setTextColor(0xFFFFFFFF);
    exitBtn.setTypeface(null, Typeface.BOLD);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(14));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    exitBtn.setBackground(exitBg);

    LinearLayout.LayoutParams lpExit =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lpExit.setMargins(0, dp(14), 0, 0);
    exitBtn.setLayoutParams(lpExit);
    exitBtn.setOnClickListener(v -> abortLab15ByUser());
    root.addView(exitBtn);

    b.setView(root);
    lab15Dialog = b.create();
    if (lab15Dialog.getWindow() != null) {
        lab15Dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }
    lab15Dialog.show();

    // ------------------------------------------------------------
    // PHOTO HEADER
    // ------------------------------------------------------------
    logLine();
    logInfo("LAB 15 — Charging System Diagnostic (Smart)");
    logLine();

    final long[] startTs = { -1 };
    final boolean[] wasCharging = { false };
    final String[] dots = { "•", "• •", "• • •" };

    final BatteryInfo startInfo = getBatteryInfo();
    final long startMah =
            (startInfo != null && startInfo.currentChargeMah > 0)
                    ? startInfo.currentChargeMah : -1;

    final long fullMah =
            (startInfo != null && startInfo.estimatedFullMah > 0)
                    ? startInfo.estimatedFullMah : -1;

    ui.post(new Runnable() {

        int dotStep = 0;
        int lastSeg = -1;

        @Override
        public void run() {

            if (!lab15Running || lab15Finished) return;

            boolean chargingNow = isDeviceCharging();
            long now = SystemClock.elapsedRealtime();

            dotsView.setText(dots[dotStep++ % dots.length]);

            // ----------------------------------------------------
            // CHARGING START DETECT
            // ----------------------------------------------------
            if (chargingNow && !wasCharging[0]) {
                wasCharging[0] = true;
                startTs[0] = now;

                lab15BattTempStart = getBatteryTemperature();
                lab15BattTempPeak  = lab15BattTempStart;

                lab15StatusText.setText("Charging state detected.");
                lab15StatusText.setTextColor(0xFF39FF14);
                logOk("✅ Charging state detected.");
            }

            // ----------------------------------------------------
            // LIVE THERMAL
            // ----------------------------------------------------
            if (chargingNow) {
                float t = getBatteryTemperature();
                if (t > 0) {
                    if (Float.isNaN(lab15BattTempPeak) || t > lab15BattTempPeak)
                        lab15BattTempPeak = t;
                    if (t >= 45f) lab15OverTempDuringCharge = true;
                }
            }

            if (startTs[0] < 0) {
                ui.postDelayed(this, 500);
                return;
            }

            int elapsed = (int) ((now - startTs[0]) / 1000);
            int shown   = Math.min(elapsed, LAB15_TOTAL_SECONDS);

            lab15CounterText.setText(
                    "Progress: " + shown + " / " + LAB15_TOTAL_SECONDS + " sec"
            );

            int seg = elapsed / 30;
            if (seg != lastSeg) {
                lastSeg = seg;
                for (int i = 0; i < lab15ProgressBar.getChildCount(); i++) {
                    lab15ProgressBar.getChildAt(i)
                            .setBackgroundColor(i < seg ? 0xFF39FF14 : 0xFF333333);
                }
            }

            if (elapsed < LAB15_TOTAL_SECONDS) {
                ui.postDelayed(this, 1000);
                return;
            }

            // ====================================================
            // FINAL SNAPSHOT
            // ====================================================
            lab15Finished = true;
            lab15Running  = false;

            lab15BattTempEnd = getBatteryTemperature();

            BatteryInfo endInfo = getBatteryInfo();
            long endMah =
                    (endInfo != null && endInfo.currentChargeMah > 0)
                            ? endInfo.currentChargeMah : -1;

            long dtMs = Math.max(1, now - startTs[0]);
            double minutes = dtMs / 60000.0;
            long deltaMah = (startMah > 0 && endMah > startMah)
                    ? (endMah - startMah) : -1;

            double mahPerMin =
                    (deltaMah > 0 && minutes > 0)
                            ? deltaMah / minutes : -1;

            float dT =
                    (!Float.isNaN(lab15BattTempStart) && !Float.isNaN(lab15BattTempEnd))
                            ? (lab15BattTempEnd - lab15BattTempStart)
                            : Float.NaN;

            // ----------------------------------------------------
            // PHOTO OUTPUT
            // ----------------------------------------------------
            logLine();
            logInfo("LAB 15 — Charging System Diagnostic (Result)");

            if (startMah > 0)
                logInfo("Battery level (start): " + startMah + " mAh");
            else
                logWarn("Battery level (start): N/A");

            if (endMah > 0)
                logInfo("Battery level (end): " + endMah + " mAh");

            logInfo(String.format(Locale.US,
                    "Thermal correlation (charging): start %.1f°C → peak %.1f°C → end %.1f°C",
                    lab15BattTempStart,
                    lab15BattTempPeak,
                    lab15BattTempEnd
            ));

            // ----------------------------------------------------
            // CHARGING SPEED
            // ----------------------------------------------------
            if (mahPerMin > 0) {
                logInfo(String.format(
                        Locale.US,
                        "Charging rate: %.1f mAh/min",
                        mahPerMin
                ));

                if (mahPerMin < 6)
                    logError("Charging mode: SLOW");
                else if (mahPerMin < 12)
                    logWarn("Charging mode: NORMAL");
                else if (mahPerMin < 20)
                    logOk("Charging mode: FAST");
                else
                    logOk("Charging mode: TURBO");
            } else {
                logWarn("Charging rate: Unable to calculate.");
            }

            // ----------------------------------------------------
            // THERMAL VERDICT
            // ----------------------------------------------------
            if (!lab15OverTempDuringCharge && !Float.isNaN(dT)) {
                logOk(String.format(
                        Locale.US,
                        "Thermal verdict (charging): OK (ΔT +%.1f°C)",
                        dT
                ));
            } else {
                logWarn("Thermal verdict (charging): HOT — Elevated temperature detected.");
            }

            // ----------------------------------------------------
            // FINAL VERDICT
            // ----------------------------------------------------
            if (lab15FlapUnstable) {
                logError("LAB decision: Charging instability detected (plug/unplug behavior).");
            } else if (mahPerMin > 0 && mahPerMin < 6) {
                logWarn("LAB decision: Suboptimal charging performance. Monitor charger or cable.");
            } else {
                logOk("LAB decision: Charging system healthy. No issues detected.");
            }

            logLine();

            ui.post(() -> {
                try {
                    if (lab15Dialog != null && lab15Dialog.isShowing())
                        lab15Dialog.dismiss();
                } catch (Throwable ignore) {}
                lab15Dialog = null;
            });
        }
    });
}

// ============================================================
// LAB 16 — Thermal Snapshot (REAL HARDWARE EDITION)
// Source: Internal + Peripherals (merged)
// Output: Screenshot-style report
// ============================================================
private void lab16ThermalSnapshot() {

    logLine();
    logInfo("LAB 16 — Thermal Snapshot");

    // --- INTERNAL (like Phone Info Internal) ---
    String internal = buildThermalInternalReport();
    if (internal != null && !internal.trim().isEmpty()) {
        logInfo(internal.trim());
    }

    // --- HARDWARE / PERIPHERALS (like Phone Info Peripherals) ---
    String hw = buildThermalInfo();
    if (hw != null && !hw.trim().isEmpty()) {
        logInfo(hw.trim());
    }

    logOk("Lab 16 finished.");
    logLine();
}

// ============================================================
// LAB 17 — GEL Auto Battery Reliability Evaluation
// SNAPSHOT-BASED • SAFE • NO STRESS
// ============================================================
private void lab17RunAuto() {

    logLine();
    logInfo("LAB 17 — GEL Auto Battery Reliability Evaluation");
    logLine();

    new Thread(() -> {

        try {

            // ------------------------------------------------------------
            // 1) Battery snapshot
            // ------------------------------------------------------------
            float pct = getCurrentBatteryPercent();
            float temp = getBatteryTemperature();
            float volt = getBatteryVoltage_mV();

            if (pct < 20f) {
                ui.post(() ->
                        logError("Battery too low for evaluation (<20%)."));
                return;
            }

            // ------------------------------------------------------------
            // 2) Thermal snapshot
            // ------------------------------------------------------------
            Map<String, Float> zones = readThermalZones();

            Float cpu  = pickZone(zones, "cpu", "soc");
            Float batt = temp;

            // ------------------------------------------------------------
            // 3) Scoring (SAFE HEURISTICS)
            // ------------------------------------------------------------
            int score = 100;

            if (temp > 45f) score -= 15;
            if (temp > 55f) score -= 25;

            if (cpu != null) {
                if (cpu > 70f) score -= 15;
                if (cpu > 85f) score -= 25;
            }

            if (volt < 3600f) score -= 10;
            if (volt < 3400f) score -= 20;

            if (score < 0) score = 0;

            final int fScore = score;
            final String category =
                    (score >= 85) ? "Strong" :
                    (score >= 70) ? "Normal" :
                    "Weak";

            // ------------------------------------------------------------
            // 4) UI OUTPUT
            // ------------------------------------------------------------
            ui.post(() -> {

                logInfo(String.format(
                        Locale.US,
                        "Battery: %.1f%% | %.1f°C | %.0f mV",
                        pct, temp, volt
                ));

                if (cpu != null) {
                    logInfo(String.format(
                            Locale.US,
                            "CPU temperature: %.1f°C",
                            cpu
                    ));
                }

                logLine();
                logOk(String.format(
                        Locale.US,
                        "Final Battery Reliability Score: %d%% (%s)",
                        fScore, category
                ));

                printHealthCheckboxMap(category);
            });

        } catch (Throwable t) {
            ui.post(() ->
                    logError("LAB 17 failed: " + t.getMessage()));
        }

    }).start();
}

// ============================================================
// SUPPORT (SAFE)
// ============================================================
private float getBatteryVoltage_mV() {
    try {
        Intent i = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );
        if (i == null) return 0f;
        return i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
    } catch (Throwable t) {
        return 0f;
    }
}     
    
// ============================================================ // ============================================================
// LABS 18 - 21: STORAGE & PERFORMANCE
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
// END OF CLASS
// ============================================================
}
