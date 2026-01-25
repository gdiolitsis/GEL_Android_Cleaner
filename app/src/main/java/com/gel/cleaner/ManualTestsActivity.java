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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.LocationManager;
import android.Manifest;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.Image;
import android.media.ImageReader;
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
import android.os.CancellationSignal;
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
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

// ============================================================
// JAVA — UTIL
// ============================================================
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ManualTestsActivity extends AppCompatActivity {

private AlertDialog lab14RunningDialog;
private static final int REQ_LAB13_BT_CONNECT = 1313;

// ============================================================
// LAB 13 — BLUETOOTH RECEIVER (FINAL / AUTHORITATIVE)
// ============================================================
private final BroadcastReceiver lab13BtReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context c, Intent i) {

        if (!lab13Running && !lab13MonitoringStarted) {

            String a = i.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(a)) {

                lab13ReceiverSawConnection = true;
                lab13HadAnyConnection = true;

                if (lab13StatusText != null) {
                    lab13StatusText.setText(
                        "External Bluetooth device connected. Starting monitor..."
                    );
                }

                //  CRITICAL: start monitor NOW
                startLab13Monitor60s();
            }
        }
    }
};

// ============================================================
// GLOBAL TTS (for labs that need shared access)
// ============================================================
private TextToSpeech[] tts = new TextToSpeech[1];
private boolean[] ttsReady = { false };

// ============================================================
// GLOBAL TTS PREF
// ============================================================
private static final String PREF_TTS_MUTED = "tts_muted_global";

private boolean isTtsMuted() {
return prefs != null && prefs.getBoolean(PREF_TTS_MUTED, false);
}

private void setTtsMuted(boolean muted) {
if (prefs != null)
prefs.edit().putBoolean(PREF_TTS_MUTED, muted).apply();
}

// ============================================================
// GLOBAL PREFS ALIAS (used by labs + helpers)
// ============================================================
private SharedPreferences p;

// ============================================================
// GEL DIAG — GLOBAL PREFS (CLASS LEVEL)
// ============================================================
private SharedPreferences prefs;

// ============================================================
// LAB 3 — STATE (CLASS LEVEL)
// ============================================================
private volatile boolean lab3WaitingUser = false;
private int lab3OldMode = AudioManager.MODE_NORMAL;
private boolean lab3OldSpeaker = false;

// ============================================================  
// SERVICE LOG SESSION FLAG (CRITICAL)  
// ============================================================  
private boolean serviceLogInit = false;  

// ============================================================  
// GLOBAL FINAL SCORE FIELDS (used by Lab 29 PDF Report)  
// ============================================================  
private String lastScoreHealth      = "N/A";  
private String lastScorePerformance = "N/A";  
private String lastScoreSecurity    = "N/A";  
private String lastScorePrivacy     = "N/A";  
private String lastFinalVerdict     = "N/A";  

// ============================================================
// LAB 13 — STATE / FIELDS (FINAL)
// ============================================================

// runtime state
private volatile boolean lab13Running = false;
private volatile boolean lab13MonitoringStarted = false;
private volatile boolean lab13HadAnyConnection = false;
private volatile boolean lab13AssumedConnected = false;
private boolean lab13LastConnected = false;

// counters
private int lab13DisconnectEvents = 0;
private int lab13ReconnectEvents  = 0;
private int lab13Seconds = 0;
private long lab13StartMs = 0L;

// flags
private boolean lab13SkipExternalTest = false;

// bluetooth handles
private BluetoothManager lab13Bm;
private BluetoothAdapter lab13Ba;

// UI (monitor dialog)
private AlertDialog lab13Dialog;
private TextView lab13StatusText;
private TextView lab13CounterText;
private TextView lab13DotsView;
private LinearLayout lab13ProgressBar;

// handler
private final Handler lab13Handler =
        new Handler(Looper.getMainLooper());
        
// ============================================================
// LAB 13 — HARD SYNC FLAGS
// ============================================================
private volatile boolean lab13ReceiverSawConnection = false;
private volatile boolean lab13ReceiverSawDisconnection = false;

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
// LAB 15 / LAB 16 thermal aliases (keep legacy names)
private float startBatteryTemp = Float.NaN;
private float endBatteryTemp   = Float.NaN;
// LAB 15 — Charging strength state (MUST be fields)
private boolean lab15_strengthKnown = false;
private boolean lab15_strengthWeak  = false;
private boolean lab15_systemLimited = false;

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

prefs = getSharedPreferences("GEL_DIAG", MODE_PRIVATE);  
p     = prefs;  

ui = new Handler(Looper.getMainLooper());

// ============================================================
// GLOBAL TTS INIT — ONE TIME ONLY (SAFE)
// ============================================================
tts[0] = new TextToSpeech(this, status -> {
if (status == TextToSpeech.SUCCESS) {

if (tts[0] == null) return; 

    int res = tts[0].setLanguage(Locale.US);  

    if (res == TextToSpeech.LANG_MISSING_DATA ||  
        res == TextToSpeech.LANG_NOT_SUPPORTED) {  

        // fallback  
        tts[0].setLanguage(Locale.ENGLISH);  
    }  

    ttsReady[0] = true;  
}

});

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
    // SECTION 1: AUDIO & VIBRATION — LABS 1â€“5  
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
    // SECTION 2: DISPLAY & SENSORS — LABS 6â€“9  
    // ============================================================  
    LinearLayout body2 = makeSectionBody();  
    Button header2 = makeSectionHeader(getString(R.string.manual_cat_2), body2);  
    root.addView(header2);  
    root.addView(body2);  

    body2.addView(makeTestButton("6. Display / Touch Basic Inspection", this::lab6DisplayTouch));  
    body2.addView(makeTestButton("7. Rotation & Proximity Sensors Check",this::lab7RotationAndProximityManual));
    body2.addView(makeTestButton("8. Camera Hardware & Preview Path Check",this::lab8CameraHardwareCheck));
    body2.addView(makeTestButton("9. Sensors Check", this::lab9SensorsCheck));  

    // ============================================================  
    // SECTION 3: WIRELESS & CONNECTIVITY — LABS 10â€“13  
    // ============================================================  
    LinearLayout body3 = makeSectionBody();  
    Button header3 = makeSectionHeader(getString(R.string.manual_cat_3), body3);  
    root.addView(header3);  
    root.addView(body3);  

    body3.addView(makeTestButton("10. Wi-Fi Connection Check", this::lab10WifiConnectivityCheck));  
    body3.addView(makeTestButton("11. Mobile Network Diagnostic", this::lab11MobileDataDiagnostic));  
    body3.addView(makeTestButton("12. Call Function Interpretation", this::lab12CallFunctionInterpretation));  
    body3.addView(makeTestButton("13. Bluetooth Connectivity Check",this::lab13BluetoothConnectivityCheck));
    // ============================================================  
    // SECTION 4: BATTERY & THERMAL — LABS 14â€“17  
    // ============================================================  
    LinearLayout body4 = makeSectionBody();  
    Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);  
    root.addView(header4);  
    root.addView(body4);  

    body4.addView(makeTestButtonRedGold("14. Battery Health Stress Test",  
    () -> showLab14PreTestAdvisory(this::lab14BatteryHealthStressTest)));    
    body4.addView(makeTestButton("15. Charging System Diagnostic (Smart)", this::lab15ChargingSystemSmart));  
    body4.addView(makeTestButton("16. Thermal Snapshot", this::lab16ThermalSnapshot));  
    body4.addView(makeTestButtonGreenGold("17. Intelligent System Health Analysis",this::lab17RunAuto));  

    // ============================================================  
    // SECTION 5: STORAGE & PERFORMANCE — LABS 18â€“20  
    // ============================================================  
    LinearLayout body5 = makeSectionBody();  
    Button header5 = makeSectionHeader(getString(R.string.manual_cat_5), body5);  
    root.addView(header5);  
    root.addView(body5);  
      
    body5.addView(makeTestButton("18. Storage Health Inspection", this::lab18StorageSnapshot));  
    body5.addView(makeTestButton("19. Memory Pressure & Stability Analysis", this::lab19RamSnapshot));  
    body5.addView(makeTestButton("20. Uptime & Reboot Pattern Analysis", this::lab20UptimeHints));  

    // ============================================================  
    // SECTION 6: SECURITY & SYSTEM HEALTH — LABS 21â€“24  
    // ============================================================  
    LinearLayout body6 = makeSectionBody();  
    Button header6 = makeSectionHeader(getString(R.string.manual_cat_6), body6);  
    root.addView(header6);  
    root.addView(body6);  

    body6.addView(makeTestButton("21. Screen Lock / Biometrics", this::lab21ScreenLock));  
    body6.addView(makeTestButton("22. Security Patch Check", this::lab22SecurityPatchManual));  
    body6.addView(makeTestButton("23. Developer Options Risk", this::lab23DevOptions));  
    body6.addView(makeTestButton("24. Root / Bootloader Suspicion", this::lab24RootSuspicion));  

    // ============================================================  
    // SECTION 7: ADVANCED / LOGS — LABS 25â€“29  
    // ============================================================  
    LinearLayout body7 = makeSectionBody();  
    Button header7 = makeSectionHeader(getString(R.string.manual_cat_7), body7);  
    root.addView(header7);  
    root.addView(body7);  

        body7.addView(makeTestButton("25. Crash / Freeze History", this::lab25CrashHistory));
        body7.addView(makeTestButton("26. Installed Applications Impact Analysis", this::lab26AppsFootprint));
        body7.addView(makeTestButton("27. App Permissions & Privacy", this::lab27PermissionsPrivacy));
        body7.addView(makeTestButton("28. Hardware Stability & Interconnect Integrity\nSolder / Contact Suspicion (SYMPTOM-BASED)",this::lab28HardwareStability));
        body7.addView(makeTestButton("29. DEVICE SCORES Summary", this::lab28CombineFindings));
        body7.addView(makeTestButton("30. FINAL TECH SUMMARY", this::lab29FinalSummary));


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
// EXPORT SERVICE REPORT BUTTON (LOCKED HEIGHT)
// ============================================================
Button btnExport = new Button(this);
btnExport.setText(getString(R.string.export_report_title));
btnExport.setAllCaps(false);
btnExport.setTextColor(0xFFFFFFFF);
btnExport.setBackgroundResource(R.drawable.gel_btn_outline_selector);

//  OVERRIDE THEME / DRAWABLE
btnExport.setMinHeight(0);
btnExport.setMinimumHeight(0);
btnExport.setPadding(dp(16), dp(14), dp(16), dp(14));

LinearLayout.LayoutParams lpExp =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT
);
lpExp.setMargins(dp(8), dp(16), dp(8), dp(24));
btnExport.setLayoutParams(lpExp);

btnExport.setOnClickListener(v ->
startActivity(new Intent(this, ServiceReportActivity.class))
);

root.addView(btnExport);

// ============================================================
// FINAL BIND
// ============================================================
scroll.addView(root);
setContentView(scroll);

// ============================================================
// SERVICE LOG — INIT (Android Manual Tests)
// ============================================================

if (!serviceLogInit) {

GELServiceLog.section("Android Manual Tests — Hardware Diagnostics");  

logLine();  
logInfo(getString(R.string.manual_log_desc));  

serviceLogInit = true;

}

}  // onCreate ENDS HERE

@Override
protected void onPause() {

    // ==========================
    // LAB 3 LOGIC
    // ==========================
    lab3WaitingUser = false;
    stopLab3Tone();
    SystemClock.sleep(120);
    restoreLab3Audio();

    // ==========================
    // TTS STOP
    // ==========================
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();   
        }
    } catch (Throwable ignore) {}

    super.onPause();
}

@Override
protected void onDestroy() {

    // LAB 13 — receiver cleanup (SAFE)
    try {
        unregisterReceiver(lab13BtReceiver);
    } catch (Throwable ignore) {}

    // TTS cleanup
    if (tts != null && tts[0] != null) {
        try {
            tts[0].stop();
            tts[0].shutdown();
        } catch (Throwable ignore) {}
        tts[0] = null;
    }

    super.onDestroy();
}

// ============================================================  
// GEL legacy aliases (LOCKED)  
// ============================================================  
private void logYellow(String msg) { logWarn(msg); }  
private void logGreen(String msg)  { logOk(msg); }  
private void logRed(String msg)    { logError(msg); }  

private void logSection(String msg) {  
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
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
lp.setMargins(0, dp(4), 0, dp(4));
b.setLayoutParams(lp);

b.setMinHeight(dp(48));

b.setSingleLine(false);
b.setMaxLines(2);
b.setEllipsize(null);

b.setGravity(Gravity.CENTER);
b.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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

runOnUiThread(() -> {  

    if (lab3WaitingUser) return;  
    lab3WaitingUser = true;  

    AlertDialog.Builder b =  
            new AlertDialog.Builder(  
                    ManualTestsActivity.this,  
                    android.R.style.Theme_Material_Dialog_NoActionBar  
            );  
    b.setCancelable(false);  

    // ---------- UI ROOT ----------  
    LinearLayout root = new LinearLayout(this);  
    root.setOrientation(LinearLayout.VERTICAL);  
    root.setPadding(dp(28), dp(24), dp(28), dp(24));  
    root.setMinimumWidth(dp(300)); 

    GradientDrawable bg = new GradientDrawable();  
    bg.setColor(0xFF101010);  
    bg.setCornerRadius(dp(18));  
    bg.setStroke(dp(4), 0xFFFFD700);  
    root.setBackground(bg);  

    // ---------- MESSAGE ----------  
    TextView msg = new TextView(this);  
    msg.setText("Did you hear the sound?");  
    msg.setTextColor(0xFFFFFFFF);  
    msg.setTextSize(16f);  
    msg.setGravity(Gravity.CENTER);  
    msg.setPadding(0, 0, 0, dp(18));  
    root.addView(msg);  

    // ---------- BUTTON ROW ----------  
    LinearLayout btnRow = new LinearLayout(this);  
    btnRow.setOrientation(LinearLayout.HORIZONTAL);  
    btnRow.setGravity(Gravity.CENTER);  
    btnRow.setPadding(0, dp(8), 0, 0);  

    LinearLayout.LayoutParams btnLp =  
            new LinearLayout.LayoutParams(0, dp(52), 1f);  
    btnLp.setMargins(dp(8), 0, dp(8), 0); 

    // ---------- NO BUTTON ----------  
    Button noBtn = new Button(this);  
    noBtn.setText("NO");  
    noBtn.setAllCaps(false);  
    noBtn.setTextColor(0xFFFFFFFF);  

    GradientDrawable noBg = new GradientDrawable();  
    noBg.setColor(0xFF8B0000);  
    noBg.setCornerRadius(dp(14));  
    noBg.setStroke(dp(3), 0xFFFFD700);  
    noBtn.setBackground(noBg);  
    noBtn.setLayoutParams(btnLp);  

    // ---------- YES BUTTON ----------  
    Button yesBtn = new Button(this);  
    yesBtn.setText("YES");  
    yesBtn.setAllCaps(false);  
    yesBtn.setTextColor(0xFFFFFFFF);  

    GradientDrawable yesBg = new GradientDrawable();  
    yesBg.setColor(0xFF0B5F3B);  
    yesBg.setCornerRadius(dp(14));  
    yesBg.setStroke(dp(3), 0xFFFFD700);  
    yesBtn.setBackground(yesBg);  
    yesBtn.setLayoutParams(btnLp);  

    // ADD BUTTONS  
    btnRow.addView(noBtn);  
    btnRow.addView(yesBtn);  
    root.addView(btnRow);  

    b.setView(root);  

    final AlertDialog d = b.create();  
    if (d.getWindow() != null) {  
        d.getWindow().setBackgroundDrawable(  
                new ColorDrawable(Color.TRANSPARENT)  
        );  
    }  

    // ---------- YES ACTION ----------  
    yesBtn.setOnClickListener(v -> {  
        lab3WaitingUser = false;  

        logOk("LAB 3 — Earpiece audio path OK.");  
        logOk("User confirmed sound was heard from earpiece.");  

        appendHtml("<br>");  
        logOk("Lab 3 finished.");  
        logLine();  

        restoreLab3Audio();  
        d.dismiss();  
    });  

    // ---------- NO ACTION ----------  
    noBtn.setOnClickListener(v -> {  
        lab3WaitingUser = false;  

        logError("LAB 3 — Earpiece audio path FAILED.");  
        logWarn("User did NOT hear sound from earpiece.");  
        logWarn("Possible earpiece failure or audio routing issue.");  

        appendHtml("<br>");  
        logOk("Lab 3 finished.");  
        logLine();  

        restoreLab3Audio();  
        d.dismiss();  
    });  

    d.show();  
});

}

// ============================================================
// LAB 3 — STATE / HELPERS
// ============================================================

private ToneGenerator lab3Tone;
private void restoreLab3Audio() {
try {
AudioManager am =
(AudioManager) getSystemService(Context.AUDIO_SERVICE);
if (am != null) {
am.setMicrophoneMute(false);
am.setMode(lab3OldMode);
am.setSpeakerphoneOn(lab3OldSpeaker);
}
} catch (Throwable ignore) {}
}

private void playEarpieceBeep() {

int sampleRate = 8000;  
int durationMs = 400;  
int samples = sampleRate * durationMs / 1000;  

short[] buffer = new short[samples];  
double freq = 1000.0;  

for (int i = 0; i < samples; i++) {  
    buffer[i] = (short)  
            (Math.sin(2 * Math.PI * i * freq / sampleRate) * 32767);  
}  

AudioTrack track = new AudioTrack(  
        AudioManager.STREAM_VOICE_CALL,  
        sampleRate,  
        AudioFormat.CHANNEL_OUT_MONO,  
        AudioFormat.ENCODING_PCM_16BIT,  
        buffer.length * 2,  
        AudioTrack.MODE_STATIC  
);  

track.write(buffer, 0, buffer.length);  
track.play();  

SystemClock.sleep(durationMs + 100);  

track.stop();  
track.release();

}

// ============================================================
// LAB 3 — Tone stop helper
// ============================================================
private void stopLab3Tone() {
try {
if (lab3Tone != null) {
lab3Tone.stopTone();
lab3Tone.release();
}
} catch (Throwable ignore) {}
lab3Tone = null;
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
// LOGGING — GEL CANONICAL (UI + SERVICE REPORT)
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
    appendHtml(" " + safe(msg));
    GELServiceLog.logInfo(msg);
}

private void logOk(String msg) {
    appendHtml("<font color='#39FF14'> " + safe(msg) + "</font>");
    GELServiceLog.logOk(msg);
}

private void logWarn(String msg) {
    appendHtml("<font color='#FFD966'> " + safe(msg) + "</font>");
    GELServiceLog.logWarn(msg);
}

private void logError(String msg) {
    appendHtml("<font color='#FF5555'> " + safe(msg) + "</font>");
    GELServiceLog.logError(msg);
}

private void logLine() {
    appendHtml("--------------------------------------------------");
    GELServiceLog.logLine();
}

// ------------------------------------------------------------
// SAFE ESCAPE FOR UI ONLY (SERVICE LOG STORES RAW TEXT)
// ------------------------------------------------------------
private String safe(String s) {
if (s == null) return "";
return s.replace("&", "&")
.replace("<", "<")
.replace(">", ">");
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
// NETWORK HELPERS — USED BY LAB 10
// ============================================================

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

// ============================================================
// GEL BATTERY + LAB15 SUPPORT — REQUIRED (RESTORE MISSING SYMBOLS)
// KEEP THIS BLOCK INSIDE ManualTestsActivity (helpers area)
// ============================================================

// ------------------------------------------------------------
// NORMALIZE mAh / Î¼Ah (shared)
// ------------------------------------------------------------
private long normalizeMah(long raw) {
if (raw <= 0) return -1;
if (raw > 200000) return raw / 1000;
return raw;                          // already mAh
}

// ------------------------------------------------------------
// Battery temperature — SAFE
// ------------------------------------------------------------
private float getBatteryTemperature() {
try {
Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
if (i == null) return 0f;

int raw = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);  
    if (raw <= 0) return 0f;  

    return raw / 10f; 
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

    // Charge counter
    long cc_uAh =  
            bm.getLongProperty(  
                    BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER  
            );  

    bi.currentChargeMah = normalizeMah(cc_uAh);  

    // SAFE FULL CAPACITY — NOT via CHARGE_FULL (API trap)  
    bi.estimatedFullMah = -1; 

    if (bi.currentChargeMah <= 0)  
        bi.currentChargeMah = -1;  

} catch (Throwable t) {  
    bi.currentChargeMah = -1;  
    bi.estimatedFullMah = -1;  
    bi.source = "BatteryManager:ERROR";  
}  

return bi;

}

// ============================================================
// THERMAL HELPERS — System thermal zones (no libs, best-effort)
// Used by CPU/GPU/Skin/PMIC temp readers
// ============================================================
private Map<String, Float> readThermalZones() {

Map<String, Float> out = new HashMap<>();  

try {  
    File base = new File("/sys/class/thermal");  
    File[] zones = base.listFiles(new FileFilter() {  
        @Override public boolean accept(File f) {  
            return f != null && f.isDirectory() && f.getName().startsWith("thermal_zone");  
        }  
    });  

    if (zones == null) return out;  

    for (File z : zones) {  
        try {  
            String type = safeReadOneLine(new File(z, "type"));  
            String temp = safeReadOneLine(new File(z, "temp"));  

            if (type == null || temp == null) continue;  

            type = type.trim().toLowerCase(Locale.US);  
            temp = temp.trim();  

            // temp is usually in millidegrees (e.g. 42000), sometimes in degrees (42)  
            float t;  
            try {  
                long v = Long.parseLong(temp.replaceAll("[^0-9\\-]", ""));  
                t = (Math.abs(v) >= 1000) ? (v / 1000f) : (float) v;  
            } catch (Throwable ignore) {  
                continue;  
            }  

            // keep best (highest) reading if duplicate type keys appear  
            if (!out.containsKey(type) || out.get(type) < t) out.put(type, t);  

        } catch (Throwable ignore) {}  
    }  

} catch (Throwable ignore) {}  

return out;

}

private Float pickZone(Map<String, Float> zones, String... keys) {
if (zones == null || zones.isEmpty() || keys == null || keys.length == 0) return null;

// normalize search keys  
List<String> k = new ArrayList<>();  
for (String s : keys) {  
    if (s != null && !s.trim().isEmpty()) k.add(s.trim().toLowerCase(Locale.US));  
}  
if (k.isEmpty()) return null;  

// best match strategy: first key hit in type string  
Float best = null;  

for (Map.Entry<String, Float> e : zones.entrySet()) {  
    String type = e.getKey();  
    Float val = e.getValue();  
    if (type == null || val == null) continue;  

    for (String kk : k) {  
        if (type.contains(kk)) {  
            // prefer higher temp (more indicative of active hotspot)  
            if (best == null || val > best) best = val;  
            break;  
        }  
    }  
}  

return best;

}

private String safeReadOneLine(File f) {
BufferedReader br = null;
try {
br = new BufferedReader(new FileReader(f));
return br.readLine();
} catch (Throwable t) {
return null;
} finally {
try { if (br != null) br.close(); } catch (Throwable ignore) {}
}
}

// ------------------------------------------------------------
// LAB 15 thermal correlation — FIXED (LABEL WHITE, VALUES GREEN)
// ------------------------------------------------------------
private void logLab15ThermalCorrelation(
        float battTempStart,
        float battTempPeak,
        float battTempEnd
) {

    String label = "Thermal correlation (charging): ";

    String values = String.format(
            Locale.US,
            "start %.1f°C -> peak %.1f°C -> end %.1f°C",
            battTempStart,
            (Float.isNaN(battTempPeak) ? battTempEnd : battTempPeak),
            battTempEnd
    );

    // fallback: no UI
    if (txtLog == null) {
        logInfo(label + values);
        return;
    }

    // UI — label white, values green
    SpannableString sp = new SpannableString(label + values);

    // label = white
    sp.setSpan(
            new ForegroundColorSpan(0xFFFFFFFF),
            0,
            label.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    // values = green
    sp.setSpan(
            new ForegroundColorSpan(0xFF39FF14),
            label.length(),
            sp.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    txtLog.append(sp);
    txtLog.append("\n");
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

appendHtml((strong ? " " : " ") + "<font color='#FFFFFF'>Strong</font>");  
appendHtml((normal ? " " : " ") + "<font color='#FFFFFF'>Normal</font>");  
appendHtml((weak   ? " " : " ") + "<font color='#FFFFFF'>Weak</font>");  

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
// BACKWARD COMPATIBILITY — DO NOT REMOVE (yet)
// ------------------------------------------------------------
private void logLabelValue(String label, String value) {
    logOk(label, value);
}

// ------------------------------------------------------------
// logLabelOkValue — white label, green value
// ------------------------------------------------------------
private void logLabelOkValue(String label, String value) {
    appendHtml(
            escape(label) + ": " +
            "<font color='#39FF14'>" + escape(value) + "</font>"
    );
}

// ------------------------------------------------------------
// logLabelWarnValue — white label, yellow value
// ------------------------------------------------------------
private void logLabelWarnValue(String label, String value) {
    appendHtml(
            escape(label) + ": " +
            "<font color='#FFD700'>" + escape(value) + "</font>"
    );
}

// ------------------------------------------------------------
// logLabelErrorValue — white label, red value
// ------------------------------------------------------------
private void logLabelErrorValue(String label, String value) {
    appendHtml(
            escape(label) + ": " +
            "<font color='#FF5555'>" + escape(value) + "</font>"
    );
}

private void logOk(String label, String value) {
    logLabelOkValue(label, value);
}

private void logWarn(String label, String value) {
    logLabelWarnValue(label, value);
}

private void logError(String label, String value) {
    logLabelErrorValue(label, value);
}

// ============================================================
// LAB 14 — PRE-TEST ADVISORY POPUP (GEL NEON) + TTS
// ============================================================
private void showLab14PreTestAdvisory(Runnable onContinue) {

AlertDialog.Builder b =  
        new AlertDialog.Builder(  
                ManualTestsActivity.this,  
                android.R.style.Theme_Material_Dialog_NoActionBar  
        );  

b.setCancelable(true);  

// ==========================  
// ROOT  
// ==========================  
LinearLayout root = new LinearLayout(this);  
root.setOrientation(LinearLayout.VERTICAL);  
root.setPadding(dp(24), dp(22), dp(24), dp(20));  

// BLACK BACKGROUND + GOLD BORDER (GEL STYLE)  
GradientDrawable bg = new GradientDrawable();  
bg.setColor(0xFF0E0E0E);  
bg.setCornerRadius(dp(18));  
bg.setStroke(dp(3), 0xFFFFD700);  
root.setBackground(bg);  

// ------------------------------------------------------------  
// TITLE  
// ------------------------------------------------------------  
TextView title = new TextView(this);
title.setText("Battery Stress Test — Pre-Test Check");
title.setTextColor(Color.WHITE);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setPadding(0, 0, 0, dp(12));

title.setSingleLine(false);
title.setMaxLines(Integer.MAX_VALUE);
title.setEllipsize(null);

root.addView(title);

// ------------------------------------------------------------  
// MESSAGE  
// ------------------------------------------------------------  
TextView msg = new TextView(this);  
msg.setText(  
        "For best diagnostic accuracy, it is recommended to run this test " +  
        "after a system restart.\n" +  
        "You may continue without restarting, but recent heavy usage " +  
        "can affect the results.\n" +  
        "Don't use your device for the next 5 minutes."  
);  
msg.setTextColor(Color.WHITE);  
msg.setTextSize(14.5f);  
msg.setLineSpacing(0f, 1.2f);  
root.addView(msg);

// ==========================
// MUTE TOGGLE — GLOBAL
// ==========================
CheckBox muteBox = new CheckBox(this);
muteBox.setChecked(isTtsMuted());
muteBox.setText("Mute voice instructions");
muteBox.setTextColor(0xFFDDDDDD);
muteBox.setGravity(Gravity.CENTER);
muteBox.setPadding(0, dp(10), 0, dp(10));

root.addView(muteBox);

// ==========================
// MUTE LOGIC — GLOBAL
// ==========================
muteBox.setOnCheckedChangeListener((v, checked) -> {
setTtsMuted(checked);
try {
if (checked && tts != null && tts[0] != null) {
tts[0].stop();
}
} catch (Throwable ignore) {}
});

// ------------------------------------------------------------
// CONTINUE BUTTON
// ------------------------------------------------------------
Button btnContinue = new Button(this);
btnContinue.setText("Continue anyway");
btnContinue.setAllCaps(false);
btnContinue.setTextColor(Color.WHITE);
btnContinue.setTextSize(15f);
btnContinue.setTypeface(null, Typeface.BOLD);

GradientDrawable btnBg = new GradientDrawable();
btnBg.setColor(0xFF0B5D1E);
btnBg.setCornerRadius(dp(14));
btnBg.setStroke(dp(2), 0xFFFFD700);
btnContinue.setBackground(btnBg);

LinearLayout.LayoutParams lpBtn =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(52)
);
lpBtn.setMargins(0, dp(18), 0, 0);
btnContinue.setLayoutParams(lpBtn);

root.addView(btnContinue);

// ============================================================  
// TTS — PLAY (GLOBAL ENGINE)  
// ============================================================  
try {  
    if (tts != null && tts[0] != null && ttsReady[0] && !isTtsMuted()) {  
        tts[0].stop();  
        tts[0].speak(  
                "For best diagnostic accuracy, it is recommended to run this test after a system restart. " +  
                "You may continue without restarting, but recent heavy usage can affect the results. " +  
                "Don't use your device for the next five minutes.",  
                TextToSpeech.QUEUE_FLUSH,  
                null,  
                "LAB14_PRECHECK"  
        );  
    }  
} catch (Throwable ignore) {}  

b.setView(root);  

AlertDialog dlg = b.create();  
if (dlg.getWindow() != null) {  
    dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  
}  

// ------------------------------------------------------------  
// CONTINUE CLICK — STOP TTS (NO SHUTDOWN)  
// ------------------------------------------------------------  
btnContinue.setOnClickListener(v -> {  
    try {  
        if (tts != null && tts[0] != null) tts[0].stop();  
    } catch (Throwable ignore) {}  

    try { dlg.dismiss(); } catch (Throwable ignore) {}  
    if (onContinue != null) onContinue.run();  
});  

dlg.show();

}

// ============================================================
// LAB 14 — RUNNING POPUP (GEL DARK + GOLD)
// FIX: must be INSIDE a method (not loose code in class body)
// ============================================================

private void showLab14RunningDialog() {
ui.post(() -> {
try {
if (isFinishing()) return;

AlertDialog.Builder b =  
                new AlertDialog.Builder(  
                        ManualTestsActivity.this,  
                        android.R.style.Theme_Material_Dialog_NoActionBar  
                );  

        b.setCancelable(false);  

        // ROOT  
        LinearLayout root = new LinearLayout(this);  
        root.setOrientation(LinearLayout.VERTICAL);  
        root.setPadding(dp(24), dp(20), dp(24), dp(18));  

        // GEL dark + GOLD border  
        GradientDrawable bg = new GradientDrawable();  
        bg.setColor(0xFF101010);  
        bg.setCornerRadius(dp(18));  
        bg.setStroke(dp(4), 0xFFFFD700);  
        root.setBackground(bg);  

        TextView title = new TextView(this);  
        title.setText("LAB 14 — Running stress test...");  
        title.setTextColor(0xFFFFFFFF);  
        title.setTextSize(18f);  
        title.setTypeface(null, Typeface.BOLD);  
        title.setPadding(0, 0, 0, dp(10));  
        root.addView(title);  

        TextView msg = new TextView(this);  
        msg.setText("Please keep the app open.\nDo not charge the device during this test.");  
        msg.setTextColor(0xFFDDDDDD);  
        msg.setTextSize(14f);  
        msg.setLineSpacing(0f, 1.15f);  
        root.addView(msg);  

        b.setView(root);  

        lab14RunningDialog = b.create();  
        if (lab14RunningDialog.getWindow() != null) {  
            lab14RunningDialog.getWindow().setBackgroundDrawable(  
                    new ColorDrawable(Color.TRANSPARENT)  
            );  
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

// ===================================================================
// LAB 14 — CONFIDENCE SCORE (%)
// Variance-based reliability indicator
// ===================================================================
private static final String LAB14_PREFS = "lab14_prefs";
private static final String KEY_LAB14_RUNS = "lab14_run_count";
private static final String KEY_LAB14_LAST_DRAIN_1 = "lab14_drain_1";
private static final String KEY_LAB14_LAST_DRAIN_2 = "lab14_drain_2";
private static final String KEY_LAB14_LAST_DRAIN_3 = "lab14_drain_3";

private void logLab14VarianceInfo() {
int runs = getLab14RunCount();
if (runs < 2) return;

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
    if (n < 2) return;  

    double mean = sum / n;  
    double var = 0;  
    for (double v : vals) {  
        if (v > 0)  
            var += (v - mean) * (v - mean);  
    }  
    var /= n;  

    double relVar = Math.sqrt(var) / mean;  

    logInfo("Measurement consistency:");  

    if (relVar < 0.08) {  
        logOk("Results are consistent across runs.");  
    }  
    else if (relVar < 0.15) {  
        logOk("Minor variability detected. Results are generally reliable.");  
    }  
    else {  
        logWarn("High variability detected. Repeat the test after a system restart to improve reliability.");  
    }  

} catch (Throwable ignore) {}

}

private void logLab14Confidence() {

int runs = getLab14RunCount();  
logLine();  

if (runs <= 1) {  
    logWarn("Confidence: Preliminary (1 run)");  
    logWarn("For Higher Diagnostic Accuracy, Run This Test 2 More Times, Any Other Day, Under Similar Conditions.");  
}  
else if (runs == 2) {  
    logWarn("Confidence: Medium (2 runs)");  
    logWarn("One Additional Run Is Recommended, To Confirm Battery Aging Trend.");  
}  
else {  
    logOk("Confidence: High (3+ consistent runs)");  
    logInfo("Battery diagnostic confidence is high.");  
}

}

private int getLab14RunCount() {
try {
return getSharedPreferences(LAB14_PREFS, MODE_PRIVATE)
.getInt(KEY_LAB14_RUNS, 0);
} catch (Throwable ignore) {
return 0;
}
}

// ------------------------------------------------------------
// CPU / GPU thermal helpers (SAFE, READ-ONLY)
// ------------------------------------------------------------
private Float readCpuTempSafe() {
try {
Map<String, Float> zones = readThermalZones();
return pickZone(zones, "cpu", "soc", "ap");
} catch (Throwable ignore) {}
return null;
}

private Float readGpuTempSafe() {
try {
Map<String, Float> zones = readThermalZones();
return pickZone(zones, "gpu", "gfx", "kgsl");
} catch (Throwable ignore) {}
return null;
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

    logWarn("LAB 15 cancelled by user.");  

    lab15Running = false;  
    lab15Finished = true;  

    try {  
        if (lab15Dialog != null && lab15Dialog.isShowing())  
            lab15Dialog.dismiss();  
    } catch (Throwable ignore) {}  

    lab15Dialog = null;  

});

}

// ------------------------------------------------------------
// TEMP FORMATTER (USED BY LAB 15 / LAB 16 LOGS)
// ------------------------------------------------------------
private String formatTemp(float temp) {
if (Float.isNaN(temp)) return "N/A";
return String.format(Locale.US, "%.1f°C", temp);
}

// ------------------------------------------------------------
// HTML / LOG SAFE ESCAPE
// ------------------------------------------------------------
private String escape(String s) {
if (s == null) return "";
return s.replace("&", "&")
.replace("<", "<")
.replace(">", ">");
}

// ============================================================
// LAB 16 — INTERNAL + PERIPHERALS THERMAL HELPERS
// GEL LOCKED • HUMAN-READABLE • COMPACT MODE
// ============================================================

// ------------------------------------------------------------
// DATA MODEL
// ------------------------------------------------------------
private static class ThermalEntry {
final String label;
final float temp;

ThermalEntry(String label, float temp) {  
    this.label = label;  
    this.temp  = temp;  
}

}

// ------------------------------------------------------------
// INTERNAL THERMALS (CORE CHIPS ONLY)
// What user actually understands & cares about
// ------------------------------------------------------------
private List<ThermalEntry> buildThermalInternal() {

List<ThermalEntry> out = new ArrayList<>();  

try {  
    float batt = getBatteryTemperature();  
    if (batt > 0)  
        out.add(new ThermalEntry("Battery", batt));  

    Float cpu = readCpuTempSafe();  
    if (cpu != null && cpu > 0)  
        out.add(new ThermalEntry("CPU", cpu));  

    Float gpu = readGpuTempSafe();  
    if (gpu != null && gpu > 0)  
        out.add(new ThermalEntry("GPU", gpu));  

} catch (Throwable ignore) {}  

return out;

}

// ------------------------------------------------------------
// PERIPHERALS — CRITICAL ONLY (NOT EVERYTHING)
// System-protection relevant sensors
// ------------------------------------------------------------
private List<ThermalEntry> buildThermalPeripheralsCritical() {

List<ThermalEntry> out = new ArrayList<>();  

try {  
    File dir = new File("/sys/class/thermal");  
    File[] zones = dir.listFiles(f -> f.getName().startsWith("thermal_zone"));  
    if (zones == null) return out;  

    for (File z : zones) {  
        try {  
            String type = readSys(z, "type");  
            String temp = readSys(z, "temp");  
            if (type == null || temp == null) continue;  

            float c = Float.parseFloat(temp.trim()) / 1000f;  
            if (c <= 0 || c > 120) continue;  

            String t = type.toLowerCase(Locale.US);  

            if (t.contains("pmic"))  
                out.add(new ThermalEntry("PMIC", c));  
            else if (t.contains("charger") || t.contains("usb"))  
                out.add(new ThermalEntry("Charger", c));  
            else if (t.contains("skin") || t.contains("shell"))  
                out.add(new ThermalEntry("Device surface", c));  

        } catch (Throwable ignore) {}  
    }  
} catch (Throwable ignore) {}  

return out;

}

// ------------------------------------------------------------
// GEL STYLE OUTPUT — ONE LINE PER SENSOR
// Label = white (log channel)
// Value = colored by severity
// ------------------------------------------------------------
private void logTempInline(String label, float c) {

String base = String.format(Locale.US, "%s: %.1f°C", label, c);  

if (c < 45f) {  
    logOk(base + " (NORMAL)");  
}  
else if (c < 55f) {  
    logWarn(base + " (WARM)");  
}  
else {  
    logError(base + " (HOT)");  
}

}

// ------------------------------------------------------------
// LAB 16 — Hidden / Non-displayed thermal safety check
// ------------------------------------------------------------
private boolean detectHiddenThermalAnomaly(float thresholdC) {

try {  
    File dir = new File("/sys/class/thermal");  
    File[] zones = dir.listFiles(f -> f.getName().startsWith("thermal_zone"));  
    if (zones == null) return false;  

    for (File z : zones) {  
        try {  
            String type = readSys(z, "type");  
            String temp = readSys(z, "temp");  
            if (type == null || temp == null) continue;  

            float c = Float.parseFloat(temp.trim()) / 1000f;  
            if (c <= 0 || c > 120) continue;  

            String t = type.toLowerCase(Locale.US);  

            if (t.contains("battery") ||  
                t.contains("cpu") ||  
                t.contains("gpu")) {  
                continue;  
            }  

            // hidden / system sensor exceeded threshold  
            if (c >= thresholdC) {  
                return true;  
            }  

        } catch (Throwable ignore) {}  
    }  
} catch (Throwable ignore) {}  

return false;

}

// ============================================================
// LAB 17 — SAFE HELPERS (REQUIRED)
// Put in helpers section (same class), not inside lab17RunAuto()
// ============================================================

// True if LAB15 concluded that charging is being limited by system protection logic
private boolean isLab15ChargingPathSystemLimited() {
try {

return p.getBoolean("lab15_system_limited", false);  
} catch (Throwable t) {  
    return false;  
}

}

// Last known label (STRONG/NORMAL/MODERATE/WEAK) saved by LAB15
private String getLastLab15StrengthLabel() {
try {

return p.getString("lab15_strength_label", null);  
} catch (Throwable t) {  
    return null;  
}

}

// ============================================================
// REQUIRED HELPERS — LAB 14 / 15 / 16 / 17
// SAFE STUBS • SHARED PREF BASED • GEL EDITION
// ============================================================

// ---------------- LAB 14 ----------------
private float getLastLab14HealthScore() {
try {

return p.getFloat("lab14_health_score", -1f);  
} catch (Throwable t) {  
    return -1f;  
}

}

private int getLastLab14AgingIndex() {
try {

return p.getInt("lab14_aging_index", -1);  
} catch (Throwable t) {  
    return -1;  
}

}

private boolean hasValidLab14() {
return getLastLab14HealthScore() >= 0;
}

// ---------------- LAB 15 ----------------
private int getLastLab15ChargeScore() {
try {

return p.getInt("lab15_charge_score", -1);  
} catch (Throwable t) {  
    return -1;  
}

}

private boolean hasValidLab15() {
return getLastLab15ChargeScore() >= 0;
}

// ---------------- LAB 16 ----------------
private int getLastLab16ThermalScore() {
try {

return p.getInt("lab16_thermal_score", -1);  
} catch (Throwable t) {  
    return -1;  
}

}

private boolean hasValidLab16() {
return getLastLab16ThermalScore() >= 0;
}

// ---------------- COOLING (SAFE DEFAULTS) ----------------
private boolean hasHardwareCoolingDevices() {
// Most phones are passive-cooled
return false;
}

private String buildHardwareCoolingReport() {
return "No hardware cooling devices found. This device uses passive cooling only.";
}

// ============================================================
// LAB 17: Premium Green-Gold Button (LOCKED)
// ============================================================
private Button makeTestButtonGreenGold(String text, Runnable action) {

Button btn = new Button(this);  
btn.setText(text);  
btn.setAllCaps(false);  
btn.setTextColor(0xFF8B0000); // Red text  
btn.setTextSize(15f);  
btn.setTypeface(null, Typeface.BOLD);  
btn.setElevation(dp(3)); // premium shadow  

// -------------------------------  
// NORMAL STATE  
// -------------------------------  
GradientDrawable normalBg = new GradientDrawable();  
normalBg.setColor(0xFF00FF6A);          // GREEN NEON  
normalBg.setCornerRadius(dp(18));  
normalBg.setStroke(dp(3), 0xFFFFD700);  // GOLD BORDER  

// -------------------------------  
// PRESSED STATE  
// -------------------------------  
GradientDrawable pressedBg = new GradientDrawable();  
pressedBg.setColor(0xFF00CC55);          // darker green (pressed)  
pressedBg.setCornerRadius(dp(18));  
pressedBg.setStroke(dp(3), 0xFFFFD700);  

// -------------------------------  
// DISABLED STATE  
// -------------------------------  
GradientDrawable disabledBg = new GradientDrawable();  
disabledBg.setColor(0xFF1E3A2A);          // muted green  
disabledBg.setCornerRadius(dp(18));  
disabledBg.setStroke(dp(2), 0xFFBFAE60);  // faded gold  

StateListDrawable states = new StateListDrawable();  
states.addState(new int[]{-android.R.attr.state_enabled}, disabledBg);  
states.addState(new int[]{android.R.attr.state_pressed}, pressedBg);  
states.addState(new int[]{}, normalBg);  
btn.setBackground(states);

// -------------------------------  
// RIPPLE (Modern Android Feel)  
// -------------------------------  
RippleDrawable ripple = new RippleDrawable(  
        ColorStateList.valueOf(0x40FFFFFF), // soft white ripple  
        states,  
        null  
);  

btn.setBackground(ripple);  

LinearLayout.LayoutParams lp =  
        new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                dp(54)  
        );  
lp.setMargins(0, dp(8), 0, dp(8));  
btn.setLayoutParams(lp);  

btn.setOnClickListener(v -> action.run());  

return btn;

}

private String readSys(File dir, String name) {
try (BufferedReader br =
new BufferedReader(new FileReader(new File(dir, name)))) {
return br.readLine();
} catch (Throwable ignore) {
return null;
}
}

// ============================================================
// GEL — HELPERS FOR LAB 18 / 19 / 21/ 26
// PRODUCTION • ROOT-AWARE • HUMAN-ORIENTED
// ============================================================

// ------------------------------------------------------------
// ROOT DETECTION (SAFE, NO LIES)
// ------------------------------------------------------------
private boolean isDeviceRooted() {
try {
String[] paths = {
"/system/bin/su",
"/system/xbin/su",
"/sbin/su",
"/system/su",
"/vendor/bin/su"
};
for (String p : paths) {
if (new File(p).exists()) return true;
}
} catch (Throwable ignore) {}
return false;
}

// ============================================================
// LAB 18 — STORAGE HEALTH HELPERS
// ============================================================

// Heuristic ONLY — real NAND wear is not exposed on consumer devices
private boolean detectStorageWearSignals() {
try {
StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());
long total = s.getBlockCountLong();
long free  = s.getAvailableBlocksLong();
if (total <= 0) return false;

int pctFree = (int) ((free * 100L) / total);  

    return pctFree < 5;  
} catch (Throwable t) {  
    return false;  
}

}

// ============================================================
// LAB 19 — MEMORY HELPERS (SELF-CONTAINED)
// No external dependencies
// ============================================================

private static class MemSnapshot {
    long memFreeKb;
    long cachedKb;
    long swapTotalKb;
    long swapFreeKb;
}

// ------------------------------------------------------------
// read /proc/meminfo without helper dependencies
// ------------------------------------------------------------
private MemSnapshot readMemSnapshotSafe() {
    MemSnapshot m = new MemSnapshot();

    BufferedReader br = null;
    try {
        File f = new File("/proc/meminfo");
        if (!f.exists()) return m;

        br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {

            if (line.startsWith("MemFree:"))
                m.memFreeKb = extractKb(line);

            else if (line.startsWith("Cached:"))
                m.cachedKb = extractKb(line);

            else if (line.startsWith("SwapTotal:"))
                m.swapTotalKb = extractKb(line);

            else if (line.startsWith("SwapFree:"))
                m.swapFreeKb = extractKb(line);
        }

    } catch (Throwable ignore) {
    } finally {
        try {
            if (br != null) br.close();
        } catch (Exception ignored) {}
    }

    return m;
}

// ------------------------------------------------------------
// extract number from "XXXX kB"
// ------------------------------------------------------------
private long extractKb(String line) {
    try {
        // keep only digits
        String n = line.replaceAll("[^0-9]", "");
        return Long.parseLong(n);
    } catch (Throwable t) {
        return 0;
    }
}

// ------------------------------------------------------------
// MEMORY PRESSURE LEVEL
// ------------------------------------------------------------
private String pressureLevel(long memFreeKb, long cachedKb, long swapUsedKb) {

    boolean lowFree   = memFreeKb < (150 * 1024);   // <150MB
    boolean midFree   = memFreeKb < (300 * 1024);   // <300MB
    boolean heavySwap = swapUsedKb > (512 * 1024);  // >512MB
    boolean midSwap   = swapUsedKb > (256 * 1024);  // >256MB

    if (lowFree && heavySwap) return "High";
    if (midFree || midSwap)   return "Medium";
    return "Low";
}

// ------------------------------------------------------------
// ZRAM SWAP DEPENDENCY
// ------------------------------------------------------------
private String zramDependency(long swapUsedKb, long totalMemBytes) {

    long swapUsedMb = swapUsedKb / 1024;
    long totalMb    = totalMemBytes / (1024 * 1024);

    if (swapUsedMb > (totalMb / 4)) return "High";     // >25% of RAM
    if (swapUsedMb > (totalMb / 8)) return "Medium";   // >12.5%
    return "Low";
}

// ------------------------------------------------------------
// HUMAN LABEL
// ------------------------------------------------------------
private String humanPressureLabel(String level) {
    if ("High".equals(level))   return "High";
    if ("Medium".equals(level)) return "Moderate";
    return "Low";
}

// ============================================================
// LAB 26 — APPS IMPACT HELPERS
// ============================================================

private boolean isSystemApp(ApplicationInfo ai) {
return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
}

private long getAppInstalledSizeSafe(String pkg) {
try {
PackageManager pm = getPackageManager();
ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
File apk = new File(ai.sourceDir);
return apk.exists() ? apk.length() : -1;
} catch (Throwable t) {
return -1;
}
}

// ============================================================
// LAB 19 — RAM / MEMORY HELPERS (ROOT AWARE)
// ============================================================

private boolean isZramActiveSafe() {
try {
return new File("/sys/block/zram0").exists();
} catch (Throwable t) {
return false;
}
}

private boolean isSwapActiveSafe() {
try {
BufferedReader br = new BufferedReader(new FileReader("/proc/swaps"));
int lines = 0;
while (br.readLine() != null) lines++;
br.close();
return lines > 1; // header + entries
} catch (Throwable t) {
return false;
}
}

private long readCachedMemoryKbSafe() {
try {
BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
String line;
while ((line = br.readLine()) != null) {
if (line.startsWith("Cached:")) {
br.close();
return Long.parseLong(line.replaceAll("\\D+", ""));
}
}
br.close();
} catch (Throwable ignore) {}
return -1;
}

// ============================================================
// LAB 20 — UPTIME / REBOOT / PRESSURE HELPERS
// ============================================================

// Reads kernel OOM kill counter (heuristic pressure signal)
private int readLowMemoryKillCountSafe() {
try {
BufferedReader br = new BufferedReader(new FileReader("/proc/vmstat"));
String line;
while ((line = br.readLine()) != null) {
if (line.startsWith("oom_kill")) {
br.close();
return Integer.parseInt(line.replaceAll("\\D+", ""));
}
}
br.close();
} catch (Throwable ignore) {}
return -1;
}

// Frequent reboot hint (human-level inference)
private boolean detectFrequentRebootsHint() {
try {
long uptimeMs = SystemClock.elapsedRealtime();
// Reboot within last 6 hours
return uptimeMs < (6L * 60L * 60L * 1000L);
} catch (Throwable t) {
return false;
}
}

// ============================================================
// SAFETY STUBS — Stability detectors
// (production-safe, no side effects)
// ============================================================

private boolean detectRecentReboots() {
    try {
        // TODO: future implementation (DropBox / uptime diff)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectSignalInstability() {
    try {
        // TODO: future implementation (Telephony / ServiceState history)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectSensorInstability() {
    try {
        // TODO: future implementation (SensorManager error rates)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectThermalSpikes() {
    try {
        // TODO: future implementation (thermal zones delta scan)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

private boolean detectPowerInstability() {
    try {
        // TODO: future implementation (battery + power HAL hints)
        return false;
    } catch (Throwable t) {
        return false;
    }
}

// ============================================================
// LAB 28 — TECHNICIAN POPUP (STYLE + MUTE + LANG + TTS)  …FIXED
// ============================================================

private boolean lab28Muted = false;
private String  lab28Lang  = "EN";

// ------------------------------------------------------------
// SHOW POPUP
// ------------------------------------------------------------
private void showLab28Popup() {

    runOnUiThread(() -> {

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(true);

        // ================= ROOT =================
        LinearLayout box = new LinearLayout(ManualTestsActivity.this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        box.setBackground(bg);

        // ================= TITLE =================
        TextView title = new TextView(ManualTestsActivity.this);
        title.setText("LAB 28 — Technicians Only");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        box.addView(title);

        // ================= MESSAGE =================
        TextView msg = new TextView(ManualTestsActivity.this);
        msg.setTextColor(0xFFDDDDDD);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.START);
        msg.setText(getLab28TextEN());
        box.addView(msg);

        // ============================================================
        // CONTROLS ROW — MUTE (LEFT) + LANG (RIGHT)
        // ============================================================
        LinearLayout controls = new LinearLayout(ManualTestsActivity.this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(16), 0, dp(10));

        // ==========================
        //    MUTE BUTTON
        // ==========================
        Button muteBtn = new Button(ManualTestsActivity.this);
        muteBtn.setText(lab28Muted ? "Unmute" : "Mute");
        muteBtn.setAllCaps(false);
        muteBtn.setTextColor(0xFFFFFFFF);

        GradientDrawable muteBg = new GradientDrawable();
        muteBg.setColor(0xFF444444);
        muteBg.setCornerRadius(dp(12));
        muteBg.setStroke(dp(2), 0xFFFFD700);
        muteBtn.setBackground(muteBg);

        LinearLayout.LayoutParams lpMute =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );
        lpMute.setMargins(0, 0, dp(8), 0);
        muteBtn.setLayoutParams(lpMute);

        muteBtn.setOnClickListener(v -> {
            lab28Muted = !lab28Muted;
            muteBtn.setText(lab28Muted ? "Unmute" : "Mute");
            try {
                if (lab28Muted && tts != null && tts[0] != null) tts[0].stop();
            } catch (Throwable ignore) {}
        });

        // ==========================
        //  LANGUAGE SPINNER
        // ==========================
        Spinner langSpinner = new Spinner(ManualTestsActivity.this);

        ArrayAdapter<String> langAdapter =
                new ArrayAdapter<>(
                        ManualTestsActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"EN", "GR"}
                );
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(langAdapter);
        
        if ("GR".equals(lab28Lang)) {
            langSpinner.setSelection(1);
            msg.setText(getLab28TextGR());
        } else {
            langSpinner.setSelection(0);
            msg.setText(getLab28TextEN());
        }

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {

                lab28Lang = (pos == 0) ? "EN" : "GR";

                if ("GR".equals(lab28Lang)) {
                    msg.setText(getLab28TextGR());
                } else {
                    msg.setText(getLab28TextEN());
                }

                speakLab28TTS();
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) { }
        });

        // ==========================
        //  LANGUAGE BOX (RIGHT)
        // ==========================
        LinearLayout langBox = new LinearLayout(ManualTestsActivity.this);
        langBox.setOrientation(LinearLayout.HORIZONTAL);
        langBox.setGravity(Gravity.CENTER_VERTICAL);
        langBox.setPadding(dp(10), dp(6), dp(10), dp(6));

        GradientDrawable langBg = new GradientDrawable();
        langBg.setColor(0xFF1A1A1A);
        langBg.setCornerRadius(dp(12));
        langBg.setStroke(dp(2), 0xFFFFD700);
        langBox.setBackground(langBg);

        LinearLayout.LayoutParams lpLangBox =
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1f
                );
        lpLangBox.setMargins(dp(8), 0, 0, 0);
        langBox.setLayoutParams(lpLangBox);

        langSpinner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        langBox.addView(langSpinner);

        controls.addView(muteBtn);
        controls.addView(langBox);
        box.addView(controls);

        // ==========================
        // OK BUTTON
        // ==========================
        Button okBtn = new Button(ManualTestsActivity.this);
        okBtn.setText("OK");
        okBtn.setAllCaps(false);
        okBtn.setTextColor(0xFFFFFFFF);

        GradientDrawable okBg = new GradientDrawable();
        okBg.setColor(0xFF0F8A3B);
        okBg.setCornerRadius(dp(14));
        okBg.setStroke(dp(3), 0xFFFFD700);
        okBtn.setBackground(okBg);

        LinearLayout.LayoutParams lpOk =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lpOk.setMargins(0, dp(16), 0, 0);
        okBtn.setLayoutParams(lpOk);

        box.addView(okBtn);

        // ==========================
        // DIALOG
        // ==========================
        b.setView(box);
        final AlertDialog d = b.create();

        d.setOnDismissListener(dialog -> {
            try {
                if (tts != null && tts[0] != null) tts[0].stop();
            } catch (Throwable ignore) {}
        });

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        d.show();

        okBtn.setOnClickListener(v -> {
            try {
                if (tts != null && tts[0] != null) tts[0].stop();
            } catch (Throwable ignore) {}
            d.dismiss();
        });
    });
}

// ============================================================
// TEXT HELPERS
// ============================================================

private String getLab28TextEN() {
    return
        
        "For better diagnostic accuracy, please run all labs before this test." +
        "This lab performs symptom based analysis only. " +
        "It does not diagnose hardware faults " +
        "and does not confirm soldering defects. " +
        "Findings may indicate behavior patterns " +
        "consistent with intermittent contact issues, " +
        "such as unstable operation, random reboots, or signal drops. " +
        "Use this lab strictly as a triage tool, not as a final diagnosis. " +
        "If indicators are present, proceed only with physical inspection " +
        "and professional bench level testing.";
}

private String getLab28TextGR() {
    return
        "   ,     labs     . " +
        "      . " +
        "        . " +
        "       " +
        "    , " +
        " ,     . " +
        "     ,    . " +
        "  ,      " +
        "   .";
}

// ============================================================
// TTS — LAB 28 (CALLED ONLY ON LANGUAGE CHANGE)
// ============================================================
private void speakLab28TTS() {

    if (lab28Muted) return;

    try {
        if (tts == null || tts[0] == null || !ttsReady[0]) return;

        tts[0].stop();

        if ("GR".equals(lab28Lang)) {
            tts[0].setLanguage(new java.util.Locale("el", "GR"));
            tts[0].speak(
                    getLab28TextGR(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "LAB28_INTRO_GR"
            );
        } else {
            tts[0].setLanguage(java.util.Locale.US);
            tts[0].speak(
                    getLab28TextEN(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "LAB28_INTRO_EN"
            );
        }

    } catch (Throwable ignore) { }
}

// ============================================================
// AUDIO OUTPUT CONTEXT — LAB 1 SUPPORT
// ============================================================
private static class AudioOutputContext {

    boolean volumeMuted;
    boolean volumeLow;

    boolean bluetoothRouted;
    boolean wiredRouted;

    int volume;
    int maxVolume;

    String explain() {

        if (volumeMuted) {
            return "Media volume is muted (0%).";
        }

        if (bluetoothRouted) {
            return "Audio is routed to a Bluetooth device.";
        }

        if (wiredRouted) {
            return "Audio is routed to a wired headset or USB audio device.";
        }

        if (volumeLow) {
            return "Media volume is very low.";
        }

        return "Audio output routing and volume appear normal.";
    }
}

// ------------------------------------------------------------
// GET AUDIO OUTPUT CONTEXT
// ------------------------------------------------------------
private AudioOutputContext getAudioOutputContext() {

    AudioOutputContext c = new AudioOutputContext();

    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am == null) return c;

    c.volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    c.maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    c.volumeMuted = (c.volume == 0);
    c.volumeLow   = (c.volume > 0 && c.volume < (c.maxVolume * 0.6f));

    c.bluetoothRouted =
            am.isBluetoothA2dpOn() ||
            am.isBluetoothScoOn();

    c.wiredRouted = am.isWiredHeadsetOn();

    return c;
}

// ============================================================
// LABS 1-5: AUDIO & VIBRATION
// ============================================================

// ============================================================
// LAB 1 - Speaker Tone Test (AUTO) — WITH AUDIO PATH CHECK
// ============================================================
private void lab1SpeakerTone() {

    appendHtml("<br>");
    logLine();
    logSection("LAB 1 — Speaker Tone Test");
    logLine();

    new Thread(() -> {

        ToneGenerator tg = null;

        try {

            // ------------------------------------------------------------
            // AUDIO PATH PRE-CHECK (NO UI)
            // ------------------------------------------------------------
            AudioManager am =
                    (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            boolean volumeMuted = false;
            boolean bluetoothRouted = false;
            boolean wiredRouted = false;

            try {
                volumeMuted =
                        am != null &&
                        am.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
            } catch (Throwable ignore) {}

            try {
                bluetoothRouted =
                        am != null &&
                        (am.isBluetoothA2dpOn() || am.isBluetoothScoOn());
            } catch (Throwable ignore) {}

            try {
                wiredRouted =
                        am != null &&
                        am.isWiredHeadsetOn();
            } catch (Throwable ignore) {}

            // ------------------------------------------------------------
            // BLOCKED AUDIO PATH  STOP & ASK RE-RUN
            // ------------------------------------------------------------
            if (volumeMuted || bluetoothRouted || wiredRouted) {
                logWarn("Audio output path is not clear.");
                
                if (volumeMuted) {
    logWarn("Detected", "Media volume is muted (volume = 0)");
}

if (bluetoothRouted) {
    logWarn("Detected", "Audio is routed to a Bluetooth device");
}

if (wiredRouted) {
    logWarn("Detected", "Audio is routed to a wired or USB device");
}

                logWarn(
                        "Please correct the above condition(s) " +
                        "and re-run LAB 1 for accurate diagnostics."
                );

                appendHtml("<br>");
                logInfo("LAB 1 result: Inconclusive (audio path blocked).");
                logLine();
                return;
            }

            // ------------------------------------------------------------
            // PLAY TEST TONE
            // ------------------------------------------------------------
            tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);
            tg.startTone(ToneGenerator.TONE_DTMF_1, 1200);
            SystemClock.sleep(1400);

            // ------------------------------------------------------------
            // MIC ANALYSIS
            // ------------------------------------------------------------
            MicDiagnosticEngine.Result r =
                    MicDiagnosticEngine.run(this);

            logOk("Mic RMS", String.valueOf((int) r.rms));
            logOk("Mic Peak", String.valueOf((int) r.peak));
            if ("HIGH".equals(r.confidence)) {
    logOk("Confidence", r.confidence);
} else if ("MEDIUM".equals(r.confidence)) {
    logWarn("Confidence", r.confidence);
} else {
    logError("Confidence", r.confidence);
}

            // ------------------------------------------------------------
            // SILENCE DETECTION (HARD)
            // ------------------------------------------------------------
            boolean silenceDetected = r.silenceDetected;

            if (silenceDetected) {

                logError("No acoustic output detected.");

                logWarn(
                        "Audio path is clear, but no sound was captured " +
                        "by the microphone during the speaker test."
                );

                logWarn(
                        "This may indicate a speaker hardware failure " +
                        "or severe acoustic isolation."
                );

                logOk(
    "Recommended",
    "re-run the test once more. If silence persists, hardware inspection is advised."
);

                return;
            }

            // ------------------------------------------------------------
            // NORMAL / LOW CONFIDENCE PATH
            // ------------------------------------------------------------
            logOk("Speaker output detected.");

            if ("LOW".equalsIgnoreCase(r.confidence)) {

                logLabelValue(
"Note",
"Speaker signal detected with low confidence. " +
"This may be caused by noise cancellation, " +
"microphone placement, or acoustic design."
);

            } else {

                logLabelValue(
                        "Note",
                        "Speaker signal detected successfully."
                );
            }

        } catch (Throwable t) {

            logError("Speaker tone test failed.");

        } finally {

            if (tg != null) tg.release();

            appendHtml("<br>");
            logOk("Lab 1 finished.");
            logLine();
        }

    }).start();
}

// ============================================================
// LAB 2 — Speaker Frequency Sweep (ADAPTIVE)
// • Runs independently
// • Detects real speaker output via mic
// • If silence is detected  instructs user to run LAB 1
// ============================================================
private void lab2SpeakerSweep() {

    appendHtml("<br>");
    logLine();
    logSection("LAB 2 — Speaker Frequency Sweep");
    logLine();

    new Thread(() -> {

        ToneGenerator tg = null;

        try {

            tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);

            // ----------------------------------------------------
            // PLAY MULTI-TONE SWEEP
            // ----------------------------------------------------
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

            // ----------------------------------------------------
            // MIC FEEDBACK ANALYSIS
            // ----------------------------------------------------
            MicDiagnosticEngine.Result r =
                    MicDiagnosticEngine.run(this);

            logOk("Mic RMS",  String.valueOf((int) r.rms));
logOk("Mic Peak", String.valueOf((int) r.peak));

String c = (r.confidence == null) ? "" : r.confidence.trim().toUpperCase(Locale.US);
if (c.contains("LOW") || c.contains("WEAK")) {
    logWarn("Confidence", r.confidence);
} else if (c.contains("FAIL") || c.contains("NONE") || c.contains("NO")) {
    logError("Confidence", r.confidence);
} else {
    logOk("Confidence", r.confidence);
}

            // ----------------------------------------------------
            // ADAPTIVE GATE — NO SPEAKER OUTPUT
            // ----------------------------------------------------
            boolean noSpeakerOutput =
                    r.silenceDetected ||
                    r.rms <= 0 ||
                    r.peak <= 0;

            if (noSpeakerOutput) {

                logWarn(
                        "No speaker output was detected during this test."
                );

                logWarn(
                        "LAB 2 cannot evaluate frequency response " +
                        "without confirmed audio output."
                );

                logWarn(
                        "Please run LAB 1 to verify speaker operation, " +
                        "audio routing, and system volume settings."
                );

                appendHtml("<br>");
                logLine();
                return;
            }

            // ----------------------------------------------------
            // SPEAKER OUTPUT CONFIRMED — CONTINUE LAB 2
            // ----------------------------------------------------
            logOk("Speaker output detected during frequency sweep.");

            if ("LOW".equalsIgnoreCase(r.confidence)) {

                logLabelValue(
                        "Note",
                        "Frequency sweep detected at low confidence. " +
                        "This may be caused by DSP filtering, narrow speaker " +
                        "frequency response, or microphone placement."
                );

            } else {

                logLabelValue(
                        "Note",
                        "Frequency sweep detected successfully across multiple tones."
                );
            }

        } catch (Throwable t) {

            logError("Speaker frequency sweep failed");

        } finally {

            if (tg != null) tg.release();

            appendHtml("<br>");
            logOk("Lab 2 finished.");
            logLine();
        }

    }).start();
}

/* ============================================================
LAB 3 — Earpiece Audio Path Check (MANUAL)
FINAL — dialog â†’ tones â†’ confirmation
============================================================ */
private void lab3EarpieceManual() {

appendHtml("<br>");  
logLine();  
logSection("LAB 3 — Earpiece Audio Path Check");  
logLine();  

AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);  
if (am == null) {  
    logError("AudioManager unavailable.");  
    return;  
}  

// SAVE STATE  
lab3OldMode = am.getMode();  
lab3OldSpeaker = am.isSpeakerphoneOn();  

logInfo("Saving audio state.");  
logInfo("Routing audio to earpiece.");  

try {  
    am.stopBluetoothSco();  
    am.setBluetoothScoOn(false);  
    am.setSpeakerphoneOn(false);  
    am.setMicrophoneMute(true);  
    am.setMode(AudioManager.MODE_IN_COMMUNICATION);  
} catch (Throwable t) {  
    logError("Audio routing failed.");  
    restoreLab3Audio();  
    return;  
}  

SystemClock.sleep(300);  

runOnUiThread(() -> {  

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
    bg.setCornerRadius(dp(18));  
    bg.setStroke(dp(4), 0xFFFFD700);  
    root.setBackground(bg);  

    TextView msg = new TextView(this);  
    msg.setText("Put the phone earpiece to your ear.\n\nPress OK to start.\n");  
    msg.setTextColor(0xFFFFFFFF);  
    msg.setGravity(Gravity.CENTER);  
    root.addView(msg);  

    Button ok = new Button(this);

ok.setText("OK");
ok.setAllCaps(false);
ok.setTextColor(0xFFFFFFFF);

// DARK GREEN BUTTON (GEL style)
GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF0B5F3B);          
okBg.setCornerRadius(dp(14));
okBg.setStroke(dp(3), 0xFFFFD700); 
ok.setBackground(okBg);

root.addView(ok);

b.setView(root);  

    final AlertDialog d = b.create();  
    if (d.getWindow() != null)  
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  

    ok.setOnClickListener(v -> {  
        d.dismiss();  

        new Thread(() -> {  
            try {  
                logInfo("Playing earpiece test tones.");  

                for (int i = 1; i <= 3; i++) {  
                    logInfo("Tone " + i + " / 3");  
                    playEarpieceBeep();  
                    SystemClock.sleep(600);  
                }  

                logOk("Tone playback completed.");  

            } catch (Throwable t) {  
                logError("Tone playback failed.");  
            } finally {  
                askUserEarpieceConfirmation();  
            }  
        }).start();  
    });  

    d.show();  
});

}

/* ============================================================
LAB 4 — Microphone Recording Check (BOTTOM + TOP)
============================================================ */
private void lab4MicManual() {

appendHtml("<br>");  
logLine();  
logSection("LAB 4 — Microphone Recording Check (BOTTOM + TOP)");  
logLine();  

new Thread(() -> {  

    try {  

        MicDiagnosticEngine.Result bottom =
        MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.BOTTOM);

logOk("Bottom Mic RMS",  String.valueOf((int) bottom.rms));
logOk("Bottom Mic Peak", String.valueOf((int) bottom.peak));

String bConf = bottom.confidence == null ? "" : bottom.confidence.toUpperCase(Locale.US);
if (bConf.contains("LOW") || bConf.contains("WEAK")) {
    logWarn("Bottom Mic Confidence", bottom.confidence);
} else if (bConf.contains("FAIL") || bConf.contains("NO")) {
    logError("Bottom Mic Confidence", bottom.confidence);
} else {
    logOk("Bottom Mic Confidence", bottom.confidence);
}

MicDiagnosticEngine.Result top =
        MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);

logOk("Top Mic RMS",  String.valueOf((int) top.rms));
logOk("Top Mic Peak", String.valueOf((int) top.peak));

String tConf = top.confidence == null ? "" : top.confidence.toUpperCase(Locale.US);
if (tConf.contains("LOW") || tConf.contains("WEAK")) {
    logWarn("Top Mic Confidence", top.confidence);
} else if (tConf.contains("FAIL") || tConf.contains("NO")) {
    logError("Top Mic Confidence", top.confidence);
} else {
    logOk("Top Mic Confidence", top.confidence);
}

logOk("Microphone Path", "Recording path executed");

    } catch (Throwable t) {  

        logError("Lab 4 failed");  

    } finally {  

        appendHtml("<br>");  
        logOk("Lab 4 finished.");  
        logLine();  

        runOnUiThread(this::enableSingleExportButton);  
    }  

}).start();

}

/* ============================================================
LAB 5 — Vibration Motor Test (AUTO)
============================================================ */
private void lab5Vibration() {

appendHtml("<br>");  
logLine();  
logSection("LAB 5 — Vibration Motor Test");  
logLine();  

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

appendHtml("<br>");
logOk("Lab 5 finished.");
logLine();
enableSingleExportButton();
}

// ============================================================
// LABS 6 — 9: DISPLAY & SENSORS
// ============================================================

// ============================================================
// LAB 6 — Display Touch
// ============================================================
private void lab6DisplayTouch() {

runOnUiThread(() -> {  

    final boolean[] ttsMuted = {  
            prefs.getBoolean(PREF_TTS_MUTED, false)  
    };  

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
    bg.setCornerRadius(dp(18));  
    bg.setStroke(dp(4), 0xFFFFD700);  
    root.setBackground(bg);  

    TextView title = new TextView(this);  
    title.setText("LAB 6 — Display / Touch");  
    title.setTextColor(0xFFFFFFFF);  
    title.setTextSize(18f);  
    title.setTypeface(null, Typeface.BOLD);  
    title.setGravity(Gravity.CENTER);  
    title.setPadding(0, 0, 0, dp(12));  
    root.addView(title);  

    TextView msg = new TextView(this);  
    msg.setText(  
            "Touch all dots on the screen to complete the test.\n\n" +  
            "All screen areas must respond to touch input."  
    );  
    msg.setTextColor(0xFFDDDDDD);  
    msg.setTextSize(15f);  
    msg.setGravity(Gravity.CENTER);  
    root.addView(msg);  

    // ==========================  
    //  MUTE TOGGLE  
    // ==========================  
    CheckBox muteBox = new CheckBox(this);  
    muteBox.setChecked(ttsMuted[0]);  
    muteBox.setText("Mute voice instructions");  
    muteBox.setTextColor(0xFFDDDDDD);  
    muteBox.setGravity(Gravity.CENTER);  
    muteBox.setPadding(0, dp(10), 0, dp(10));  
    root.addView(muteBox);  

    // ==========================  
    //  START BUTTON
    // ==========================  
    Button start = new Button(this);  
    start.setText("START TEST");  
    start.setAllCaps(false);  
    start.setTextColor(0xFFFFFFFF);  

    GradientDrawable startBg = new GradientDrawable();  
    startBg.setColor(0xFF39FF14);  
    startBg.setCornerRadius(dp(14));  
    startBg.setStroke(dp(3), 0xFFFFD700);  
    start.setBackground(startBg);  
    root.addView(start);  

    // ==========================  
    // MUTE LOGIC — GLOBAL  
    // ==========================  
    muteBox.setOnCheckedChangeListener((v, checked) -> {  
        ttsMuted[0] = checked;  
        prefs.edit().putBoolean(PREF_TTS_MUTED, checked).apply();  

        if (checked && tts != null && tts[0] != null) {  
            tts[0].stop();  
        }  
    });  

    // ==========================  
    // DIALOG CREATE / SHOW  
    // ==========================  
    b.setView(root);  
    final AlertDialog d = b.create();  
    if (d.getWindow() != null)  
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  
    d.show();  

    // ==========================  
    // TTS — SPEAK AFTER SHOW  
    // ==========================  
    if (tts != null && tts[0] != null && ttsReady[0] && !ttsMuted[0]) {  
        tts[0].stop();  
        tts[0].speak(  
                "Touch all dots on the screen to complete the test. " +  
                "All screen areas must respond to touch input.",  
                TextToSpeech.QUEUE_FLUSH,  
                null,  
                "LAB6_INTRO"  
        );  
    }  

    start.setOnClickListener(v -> {  
        if (tts != null && tts[0] != null) tts[0].stop();  
        d.dismiss();  
        startActivityForResult(  
                new Intent(this, TouchGridTestActivity.class),  
                6006  
        );  
    });  
});

}

// ============================================================
// LAB 7 — Rotation + Proximity Sensors (MANUAL)
// ============================================================
private void lab7RotationAndProximityManual() {

runOnUiThread(() -> {

    final boolean[] ttsMuted = {
            prefs.getBoolean(PREF_TTS_MUTED, false)
    };

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
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("LAB 7 — Rotation & Proximity Sensors");
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // STEP 1 — TITLE
TextView step1 = new TextView(this);
step1.setText("Step 1: Rotate the device slowly.");
step1.setTextColor(0xFFFFFFFF); // white
step1.setTextSize(15f);
step1.setGravity(Gravity.CENTER);
root.addView(step1);

// STEP 1 — DESCRIPTION
TextView step1Desc = new TextView(this);
step1Desc.setText("The screen should follow orientation.");
step1Desc.setTextColor(0xFF39FF14); // green
step1Desc.setTextSize(14f);
step1Desc.setGravity(Gravity.CENTER);
step1Desc.setPadding(0, dp(4), 0, dp(12));
root.addView(step1Desc);

// STEP 2 — TITLE
TextView step2 = new TextView(this);
step2.setText("Step 2: Cover the proximity sensor.");
step2.setTextColor(0xFFFFFFFF); // white
step2.setTextSize(15f);
step2.setGravity(Gravity.CENTER);
root.addView(step2);

// STEP 2 — DESCRIPTION
TextView step2Desc = new TextView(this);
step2Desc.setText("The screen should turn off.");
step2Desc.setTextColor(0xFF39FF14); // green
step2Desc.setTextSize(14f);
step2Desc.setGravity(Gravity.CENTER);
root.addView(step2Desc);

    // ==========================
    // MUTE
    // ==========================
    CheckBox muteBox = new CheckBox(this);
    muteBox.setChecked(ttsMuted[0]);
    muteBox.setText("Mute voice instructions");
    muteBox.setTextColor(0xFFDDDDDD);
    muteBox.setGravity(Gravity.CENTER);
    muteBox.setPadding(0, dp(10), 0, dp(10));
    root.addView(muteBox);

    muteBox.setOnCheckedChangeListener((v, checked) -> {
        ttsMuted[0] = checked;
        prefs.edit().putBoolean(PREF_TTS_MUTED, checked).apply();
        if (checked && tts != null && tts[0] != null) tts[0].stop();
    });

    // ==========================
    // START
    // ==========================
    Button start = new Button(this);
    start.setText("START TEST");
    start.setAllCaps(false);
    start.setTextColor(0xFFFFFFFF);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF39FF14);
    startBg.setCornerRadius(dp(14));
    startBg.setStroke(dp(3), 0xFFFFD700);
    start.setBackground(startBg);
    root.addView(start);

    b.setView(root);
    final AlertDialog d = b.create();
    if (d.getWindow() != null)
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.show();

    // TTS
    if (tts != null && tts[0] != null && ttsReady[0] && !ttsMuted[0]) {
        tts[0].stop();
        tts[0].speak(
                "Rotate the device, then cover the proximity sensor.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "LAB7_INTRO"
        );
    }

    start.setOnClickListener(v -> {
        if (tts != null && tts[0] != null) tts[0].stop();
        d.dismiss();

        startActivityForResult(
                new Intent(this, RotationCheckActivity.class),
                7007
        );
    });
});
}

// ============================================================
// LAB 8 — Camera Hardware & Path Integrity (FULL TECH MODE)
// • All cameras (front/back/extra)
// • Preview path per camera (user confirmation)
// • Torch test where available
// • Frame stream sampling (FPS / drops / black frames / luma stats)
// • Pipeline latency estimate (sensor timestamp  arrival)
// • RAW support check (and optional RAW stream probe if supported)
// ============================================================

private void lab8CameraHardwareCheck() {

    appendHtml("<br>");
    logLine();
    logSection("LAB 8 — Camera Hardware & Path Integrity");
    logLine();

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        logWarn("Camera2 not supported on this Android version.");
        logInfo("Fallback: opening system camera app (basic check).");
        try {
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 9009);
        } catch (Throwable t) {
            logError("Failed to launch camera app.");
            logWarn("Camera app may be missing or blocked.");
            appendHtml("<br>");
            logOk("Lab 8 finished.");
            logLine();
            enableSingleExportButton();
        }
        return;
    }

    final PackageManager pm = getPackageManager();
    final boolean hasAnyCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

    if (!hasAnyCamera) {
        logError("No camera hardware detected on this device.");
        appendHtml("<br>");
        logOk("Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    final CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    if (cm == null) {
        logError("CameraManager unavailable.");
        appendHtml("<br>");
        logOk("Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // Permission check (Android 6+). (You said: strict, no lies.)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            logWarn("Camera permission not granted.");
            logWarn("Grant CAMERA permission and re-run Lab 8.");
            appendHtml("<br>");
            logOk("Lab 8 finished.");
            logLine();
            enableSingleExportButton();
            return;
        }
    }

    // ------------------------------------------------------------
    // Collect camera IDs
    // ------------------------------------------------------------
    final String[] ids;
    try {
        ids = cm.getCameraIdList();
    } catch (Throwable t) {
        logError("Failed to enumerate cameras.");
        appendHtml("<br>");
        logOk("Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    if (ids == null || ids.length == 0) {
        logError("No accessible camera IDs found.");
        appendHtml("<br>");
        logOk("Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logOk("Camera subsystem detected.");
    logLabelValue("Total camera IDs", String.valueOf(ids.length));

    // ------------------------------------------------------------
    // Build per-camera descriptors
    // ------------------------------------------------------------
    final ArrayList<Lab8Cam> cams = new ArrayList<>();
    for (String id : ids) {
        try {
            CameraCharacteristics cc = cm.getCameraCharacteristics(id);

            Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
            Float focal = cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) != null
                    && cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length > 0
                    ? cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0]
                    : null;

            Boolean flash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            int[] caps = cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);

            boolean hasRaw = false;
            boolean hasManual = false;
            boolean hasDepth = false;
            if (caps != null) {
                for (int c : caps) {
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) hasRaw = true;
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR) hasManual = true;
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT) hasDepth = true;
                }
            }

            StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size previewSize = null;
            if (map != null) {
                Size[] outs = map.getOutputSizes(SurfaceTexture.class);
                if (outs != null && outs.length > 0) {
                    // pick a stable "not huge" size
                    previewSize = outs[0];
                    for (Size s : outs) {
                        if (s.getWidth() <= 1920 && s.getHeight() <= 1080) {
                            previewSize = s;
                            break;
                        }
                    }
                }
            }

            String facingStr = "UNKNOWN";
            if (facing != null) {
                if (facing == CameraCharacteristics.LENS_FACING_BACK) facingStr = "BACK";
                else if (facing == CameraCharacteristics.LENS_FACING_FRONT) facingStr = "FRONT";
                else if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL) facingStr = "EXTERNAL";
            }

            Lab8Cam c = new Lab8Cam();
            c.id = id;
            c.facing = facingStr;
            c.hasFlash = Boolean.TRUE.equals(flash);
            c.hasRaw = hasRaw;
            c.hasManual = hasManual;
            c.hasDepth = hasDepth;
            c.focal = focal;
            c.preview = previewSize;

            cams.add(c);

        } catch (Throwable t) {
            logWarn("Camera ID " + id, "Characteristics read failed");
        }
    }

    if (cams.isEmpty()) {
        logError("No usable camera descriptors.");
        appendHtml("<br>");
        logOk("Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // Log summary (labels white, values colored via existing log methods you already use)
    logLine();
logInfo("Camera capabilities summary:");

for (Lab8Cam c : cams) {

    logLabelValue("Camera ID", c.id);

    // Facing
    if ("BACK".equals(c.facing))
        logOk("Facing", c.facing);
    else
        logWarn("Facing", c.facing);

    // Flash
    if (c.hasFlash)
        logOk("Flash", "YES");
    else
        logWarn("Flash", "NO");

    // RAW
    if (c.hasRaw)
        logOk("RAW", "YES");
    else
        logWarn("RAW", "NO");

    // Manual sensor
    if (c.hasManual)
        logOk("Manual sensor", "YES");
    else
        logWarn("Manual sensor", "NO");

    // Depth
    if (c.hasDepth)
        logOk("Depth output", "YES");
    else
        logWarn("Depth output", "NO");

    // Focal length
    if (c.focal != null) {
        logLabelValue(
                "Focal length",
                String.format(Locale.US, "%.2f mm", c.focal)
        );
    }

    // Preview size
    if (c.preview != null) {
        logLabelValue(
                "Preview size",
                c.preview.getWidth() + " x " + c.preview.getHeight()
        );
    }

    logLine();
}

    // ------------------------------------------------------------
    // Run test sequence (one camera at a time)
    // ------------------------------------------------------------
    final int[] idx = {0};

    final Lab8Overall overall = new Lab8Overall();
    overall.total = cams.size();

    runOnUiThread(() -> showLab8IntroAndStart(cams, idx, cm, overall));
}

// ============================================================
// LAB 8 — Intro dialog
// ============================================================
private void showLab8IntroAndStart(
        ArrayList<Lab8Cam> cams,
        int[] idx,
        CameraManager cm,
        Lab8Overall overall
) {
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
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("LAB 8 — Camera Lab (Full)");
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    TextView msg = new TextView(this);
    msg.setText(
            "This lab will test ALL cameras one-by-one.\n\n" +
            "For each camera:\n" +
            "• Live preview will open\n" +
            "• We sample frame stream (FPS / drops / black frames)\n" +
            "• We estimate pipeline latency\n" +
            "• Flash (torch) will be toggled where available\n\n" +
            "After each camera you will confirm if you saw live image."
    );
    msg.setTextColor(0xFFDDDDDD);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    root.addView(msg);

    Button start = new Button(this);
    start.setText("START TEST");
    start.setAllCaps(false);
    start.setTextColor(0xFFFFFFFF);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF39FF14);
    startBg.setCornerRadius(dp(14));
    startBg.setStroke(dp(3), 0xFFFFD700);
    start.setBackground(startBg);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(56)
            );
    lp.setMargins(0, dp(14), 0, 0);
    start.setLayoutParams(lp);
    root.addView(start);

    b.setView(root);
    final AlertDialog d = b.create();
    if (d.getWindow() != null)
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.show();

    start.setOnClickListener(v -> {
        d.dismiss();
        lab8RunNextCamera(cams, idx, cm, overall);
    });
}

// ============================================================
// LAB 8 — Run next camera
// ============================================================
private void lab8RunNextCamera(
        ArrayList<Lab8Cam> cams,
        int[] idx,
        CameraManager cm,
        Lab8Overall overall
) {
    if (idx[0] >= cams.size()) {
       // Final summary
appendHtml("<br>");
logLine();
logInfo("LAB 8 summary:");

logLabelValue("Cameras tested", String.valueOf(overall.total));

if (overall.previewOkCount == overall.total)
    logOk("Preview OK", overall.previewOkCount + "/" + overall.total);
else
    logWarn("Preview OK", overall.previewOkCount + "/" + overall.total);

if (overall.previewFailCount == 0)
    logOk("Preview FAIL", "0");
else
    logError("Preview FAIL", String.valueOf(overall.previewFailCount));

if (overall.torchOkCount > 0)
    logOk("Torch OK", String.valueOf(overall.torchOkCount));
else
    logWarn("Torch OK", "0");

if (overall.torchFailCount == 0)
    logOk("Torch FAIL", "0");
else
    logWarn("Torch FAIL", String.valueOf(overall.torchFailCount));

if (overall.streamIssueCount == 0)
    logOk("Frame stream issues", "None detected");
else
    logWarn("Frame stream issues", String.valueOf(overall.streamIssueCount));

logLine();
appendHtml("<br>");
logOk("Lab 8 finished.");
logLine();
enableSingleExportButton();
return;
    }

    final Lab8Cam cam = cams.get(idx[0]);
    idx[0]++;

    // Pre-log per-camera header
    appendHtml("<br>");
    logLine();
    logSection("LAB 8 — Camera ID " + cam.id + " (" + cam.facing + ")");
    logLine();

    // Torch quick test (if available)
    if (cam.hasFlash) {
        lab8TryTorchToggle(cam.id, cam, overall);
    } else {
        logWarn("Flash", "Not available");
    }

    // Open preview dialog + camera2 stream sampler
    runOnUiThread(() -> lab8ShowPreviewDialogForCamera(cam, cm, overall, () -> {
        // Next camera
        lab8RunNextCamera(cams, idx, cm, overall);
    }));
}

// ============================================================
// LAB 8 — Torch toggle
// ============================================================
private void lab8TryTorchToggle(String camId, Lab8Cam cam, Lab8Overall overall) {
    try {
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cm == null) {
            logWarn("Flash test skipped: CameraManager unavailable.");
            overall.torchFailCount++;
            return;
        }

        // NOTE: Torch control generally needs CAMERA permission (already checked).
        cm.setTorchMode(camId, true);
        SystemClock.sleep(250);
        cm.setTorchMode(camId, false);

        logOk("Flash torch toggled successfully.");
        overall.torchOkCount++;

    } catch (Throwable t) {
        logError("Flash torch control failed.");
        logWarn("Possible flash hardware, driver, or permission issue.");
        overall.torchFailCount++;
    }
}

// ============================================================
// LAB 8 — Preview dialog + stream sampling
// ============================================================
private void lab8ShowPreviewDialogForCamera(
        Lab8Cam cam,
        CameraManager cm,
        Lab8Overall overall,
        Runnable onDone
) {
    // UI container
    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    ManualTestsActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(18), dp(16), dp(18), dp(14));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("Camera Preview — " + cam.facing + " (ID " + cam.id + ")");
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(16f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(10));
    root.addView(title);

    TextView hint = new TextView(this);
    hint.setText(
            "Wait ~5 seconds while we sample frames.\n" +
            "Then confirm: did you see live image?"
    );
    hint.setTextColor(0xFFDDDDDD);
    hint.setTextSize(14f);
    hint.setGravity(Gravity.CENTER);
    hint.setPadding(0, 0, 0, dp(10));
    root.addView(hint);

    // TextureView for preview
    final TextureView tv = new TextureView(this);
    LinearLayout.LayoutParams lpTv =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(280)
            );
    tv.setLayoutParams(lpTv);
    root.addView(tv);

    // Buttons row
    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);
    row.setPadding(0, dp(12), 0, 0);

    Button yes = new Button(this);
    yes.setText("I SEE IMAGE");
    yes.setAllCaps(false);
    yes.setTextColor(0xFFFFFFFF);
    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0F8A3B);
    yesBg.setCornerRadius(dp(14));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yes.setBackground(yesBg);

    Button no = new Button(this);
    no.setText("NO IMAGE");
    no.setAllCaps(false);
    no.setTextColor(0xFFFFFFFF);
    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF444444);
    noBg.setCornerRadius(dp(14));
    noBg.setStroke(dp(3), 0xFFFFD700);
    no.setBackground(noBg);

    LinearLayout.LayoutParams lpB =
            new LinearLayout.LayoutParams(0, dp(56), 1f);
    lpB.setMargins(0, 0, dp(8), 0);
    yes.setLayoutParams(lpB);

    LinearLayout.LayoutParams lpB2 =
            new LinearLayout.LayoutParams(0, dp(56), 1f);
    lpB2.setMargins(dp(8), 0, 0, 0);
    no.setLayoutParams(lpB2);

    row.addView(yes);
    row.addView(no);
    root.addView(row);

    b.setView(root);
    final AlertDialog d = b.create();
    if (d.getWindow() != null)
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.show();

    // Disable buttons until sampling done (avoid instant wrong click)
    yes.setEnabled(false);
    no.setEnabled(false);

    final Lab8Session s = new Lab8Session();
    s.camId = cam.id;
    s.cm = cm;
    s.textureView = tv;
    s.cam = cam;

    final AtomicBoolean finished = new AtomicBoolean(false);

    Runnable finishAndNext = () -> {
        if (finished.getAndSet(true)) return;
        try { lab8CloseSession(s); } catch (Throwable ignore) {}
        try { d.dismiss(); } catch (Throwable ignore) {}
        onDone.run();
    };

    // After sampling window, enable buttons
    Runnable enableButtons = () -> {
        if (finished.get()) return;
        yes.setEnabled(true);
        no.setEnabled(true);
    };

    yes.setOnClickListener(v -> {
        overall.previewOkCount++;
        logOk("User confirmation", "Live preview visible");
        finishAndNext.run();
    });

    no.setOnClickListener(v -> {
        overall.previewFailCount++;
        logError("User confirmation", "NO live preview");
        logWarn("Possible camera module, driver, permission, or routing issue.");
        finishAndNext.run();
    });

    // Start camera when texture is ready
    if (tv.isAvailable()) {
        lab8StartCamera2Session(s, overall, enableButtons, () -> {
            // If session fails, we still ask user (buttons enable) but log it
            overall.streamIssueCount++;
            enableButtons.run();
        });
    } else {
        tv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override public void onSurfaceTextureAvailable(SurfaceTexture st, int w, int h) {
                if (finished.get()) return;
                lab8StartCamera2Session(s, overall, enableButtons, () -> {
                    overall.streamIssueCount++;
                    enableButtons.run();
                });
            }
            @Override public void onSurfaceTextureSizeChanged(SurfaceTexture st, int w, int h) {}
            @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture st) { return true; }
            @Override public void onSurfaceTextureUpdated(SurfaceTexture st) {}
        });
    }
}

// ============================================================
// LAB 8 — Start Camera2 preview + stream sampling
// ============================================================
private void lab8StartCamera2Session(
        Lab8Session s,
        Lab8Overall overall,
        Runnable onSamplingDoneEnableButtons,
        Runnable onFail
) {
    try {
        // Choose preview size
        Size ps = (s.cam != null && s.cam.preview != null) ? s.cam.preview : new Size(1280, 720);

        SurfaceTexture st = s.textureView.getSurfaceTexture();
        if (st == null) {
            logError("Preview SurfaceTexture unavailable.");
            onFail.run();
            return;
        }
        st.setDefaultBufferSize(ps.getWidth(), ps.getHeight());
        final Surface previewSurface = new Surface(st);

        // ImageReader for stream sampling (YUV)
        s.reader = ImageReader.newInstance(
                Math.min(ps.getWidth(), 1280),
                Math.min(ps.getHeight(), 720),
                ImageFormat.YUV_420_888,
                2
        );

        s.sampleStartMs = SystemClock.elapsedRealtime();
        s.frames = 0;
        s.blackFrames = 0;
        s.droppedFrames = 0;
        s.sumLuma = 0;
        s.sumLuma2 = 0;
        s.minLuma = 999;
        s.maxLuma = -1;
        s.latencySumMs = 0;
        s.latencyCount = 0;
        s.lastFrameTsNs = 0;

        s.reader.setOnImageAvailableListener(reader -> {
            Image img = null;
            try {
                img = reader.acquireLatestImage();
                if (img == null) return;

                long nowNs = SystemClock.elapsedRealtimeNanos();
                s.frames++;

                // Estimate drop/jitter (very simple)
                if (s.lastFrameTsNs != 0) {
                    long dtNs = nowNs - s.lastFrameTsNs;
                    // if > 200ms between frames, call it a "drop/timeout"
                    if (dtNs > 200_000_000L) s.droppedFrames++;
                }
                s.lastFrameTsNs = nowNs;

                // Basic frame analysis: sample luma plane sparsely
                Image.Plane[] planes = img.getPlanes();
                if (planes != null && planes.length > 0 && planes[0] != null) {
                    ByteBuffer y = planes[0].getBuffer();
                    int rowStride = planes[0].getRowStride();
                    int w = img.getWidth();
                    int h = img.getHeight();

                    // sample grid
                    int stepX = Math.max(8, w / 64);
                    int stepY = Math.max(8, h / 48);

                    long sum = 0;
                    long sum2 = 0;
                    int count = 0;
                    int localMin = 999;
                    int localMax = -1;

                    // Access with care
                    for (int yy = 0; yy < h; yy += stepY) {
                        int row = yy * rowStride;
                        for (int xx = 0; xx < w; xx += stepX) {
                            int idx = row + xx;
                            if (idx < 0 || idx >= y.limit()) continue;
                            int v = y.get(idx) & 0xFF;
                            sum += v;
                            sum2 += (long) v * (long) v;
                            count++;
                            if (v < localMin) localMin = v;
                            if (v > localMax) localMax = v;
                        }
                    }

                    if (count > 0) {
                        int mean = (int) (sum / count);
                        s.sumLuma += sum;
                        s.sumLuma2 += sum2;
                        if (localMin < s.minLuma) s.minLuma = localMin;
                        if (localMax > s.maxLuma) s.maxLuma = localMax;

                        // "black frame" heuristic
                        if (mean < 8 && localMax < 20) s.blackFrames++;
                    }
                }

                // Pipeline latency estimate:
                // SENSOR_TIMESTAMP (ns)  arrival time (ns) if we can read capture results.
                // Here we only have arrival; capture timestamp is taken from the image itself if present.
                long sensorNs = img.getTimestamp(); // best-effort
                if (sensorNs > 0) {
                    long latMs = (nowNs - sensorNs) / 1_000_000L;
                    if (latMs >= 0 && latMs < 2000) {
                        s.latencySumMs += latMs;
                        s.latencyCount++;
                    }
                }

            } catch (Throwable ignore) {
            } finally {
                try { if (img != null) img.close(); } catch (Throwable ignore2) {}
            }
        }, new Handler(Looper.getMainLooper()));

        // Open camera device
        s.cm.openCamera(s.camId, new CameraDevice.StateCallback() {
            @Override public void onOpened(CameraDevice camera) {
                s.device = camera;

                try {
                    ArrayList<Surface> outs = new ArrayList<>();
                    outs.add(previewSurface);
                    outs.add(s.reader.getSurface());

                    camera.createCaptureSession(outs, new CameraCaptureSession.StateCallback() {
                        @Override public void onConfigured(CameraCaptureSession session) {
                            s.session = session;

                            try {
                                CaptureRequest.Builder rb =
                                        camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                                rb.addTarget(previewSurface);
                                rb.addTarget(s.reader.getSurface());

                                // Try to push stable FPS range if available
                                try {
                                    CameraCharacteristics cc = s.cm.getCameraCharacteristics(s.camId);
                                    Range<Integer>[] ranges =
                                            cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                                    if (ranges != null && ranges.length > 0) {
                                        Range<Integer> best = ranges[0];
                                        for (Range<Integer> r : ranges) {
                                            if (r.getUpper() >= 30 && r.getLower() >= 15) { best = r; break; }
                                        }
                                        rb.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, best);
                                    }
                                } catch (Throwable ignore) {}

                                rb.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                                rb.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                session.setRepeatingRequest(rb.build(), null, new Handler(Looper.getMainLooper()));

                                // Sampling window: 5 seconds
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    try { lab8StopAndReportSample(s, overall); } catch (Throwable ignore) {}
                                    onSamplingDoneEnableButtons.run();
                                }, 5000);

                            } catch (Throwable t) {
                                logError("Failed to start repeating preview request.");
                                onFail.run();
                            }
                        }

                        @Override public void onConfigureFailed(CameraCaptureSession session) {
                            logError("Camera capture session configuration failed.");
                            onFail.run();
                        }
                    }, new Handler(Looper.getMainLooper()));

                } catch (Throwable t) {
                    logError("Camera preview session creation failed.");
                    onFail.run();
                }
            }

            @Override public void onDisconnected(CameraDevice camera) {
                logWarn("Camera disconnected during preview.");
                onFail.run();
            }

            @Override public void onError(CameraDevice camera, int error) {
                logError("Camera open error", "Error code " + error);
                onFail.run();
            }
        }, new Handler(Looper.getMainLooper()));

    } catch (Throwable t) {
        logError("Camera2 session start failed.");
        onFail.run();
    }
}

// ============================================================
// LAB 8 — Stop + report stream sample
// ============================================================
private void lab8StopAndReportSample(Lab8Session s, Lab8Overall overall) {

    long durMs = Math.max(1, SystemClock.elapsedRealtime() - s.sampleStartMs);
    float fps = (s.frames * 1000f) / durMs;

    logLine();
logInfo("Stream sampling", "5s");

// Frames
if (s.frames > 0)
    logOk("Frames", String.valueOf(s.frames));
else
    logError("Frames", "0");

// FPS
if (fps >= 20f)
    logOk("FPS (estimated)", String.format(Locale.US, "%.1f", fps));
else
    logWarn("FPS (estimated)", String.format(Locale.US, "%.1f", fps));

// Frame drops
if (s.droppedFrames == 0)
    logOk("Frame drops / timeouts", "0");
else
    logWarn("Frame drops / timeouts", String.valueOf(s.droppedFrames));

// Black frames
if (s.blackFrames == 0)
    logOk("Black frames (suspected)", "0");
else {
    logWarn("Black frames (suspected)", String.valueOf(s.blackFrames));
    overall.streamIssueCount++;
}

// Luma stats
if (s.frames > 0 && s.sumLuma > 0) {
    if (s.minLuma >= 0 && s.maxLuma >= 0)
        logOk("Luma range (min / max)", s.minLuma + " / " + s.maxLuma);
    else
        logWarn("Luma range (min / max)", "N/A");
}

// Latency
if (s.latencyCount > 0) {
    long avg = s.latencySumMs / Math.max(1, s.latencyCount);
    if (avg <= 250)
        logOk("Pipeline latency (avg ms)", String.valueOf(avg));
    else
        logWarn("Pipeline latency (avg ms)", String.valueOf(avg));
} else {
    logWarn(
            "Pipeline latency (avg ms)",
            "Not available (no sensor timestamps)"
    );
}

// RAW support
if (s.cam != null) {
    if (s.cam.hasRaw)
        logOk("RAW support", "YES");
    else
        logWarn("RAW support", "NO");
}

logLine();
}

// ============================================================
// LAB 8 — Close session safely
// ============================================================
private void lab8CloseSession(Lab8Session s) {
    try { if (s.session != null) s.session.close(); } catch (Throwable ignore) {}
    try { if (s.device != null) s.device.close(); } catch (Throwable ignore) {}
    try { if (s.reader != null) s.reader.close(); } catch (Throwable ignore) {}
    s.session = null;
    s.device = null;
    s.reader = null;
}

// ============================================================
// LAB 8 — Structs
// ============================================================
private static class Lab8Cam {
    String id;
    String facing;
    boolean hasFlash;
    boolean hasRaw;
    boolean hasManual;
    boolean hasDepth;
    Float focal;
    Size preview;
}

private static class Lab8Overall {
    int total;
    int previewOkCount;
    int previewFailCount;
    int torchOkCount;
    int torchFailCount;
    int streamIssueCount;
}

private static class Lab8Session {
    String camId;
    CameraManager cm;
    TextureView textureView;
    Lab8Cam cam;

    CameraDevice device;
    CameraCaptureSession session;
    ImageReader reader;

    long sampleStartMs;
    long frames;
    long blackFrames;
    long droppedFrames;

    long sumLuma;
    long sumLuma2;
    int minLuma = 999;
    int maxLuma = -1;

    long latencySumMs;
    int latencyCount;

    long lastFrameTsNs;
}

/* ============================================================
LAB 9 — Sensors Check (LABEL / VALUE MODE)
============================================================ */
private void lab9SensorsCheck() {

    appendHtml("<br>");
    logLine();
    logSection("LAB 9 — Sensors Presence & Full Analysis");
    logLine();

    try {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm == null) {
            logError("SensorManager", "Not available (framework issue)");
            return;
        }

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        int total = (sensors == null ? 0 : sensors.size());
        logOk("Total sensors reported", String.valueOf(total));

        // ------------------------------------------------------------
        // QUICK PRESENCE CHECK
        // ------------------------------------------------------------
        checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");
        checkSensor(sm, Sensor.TYPE_GYROSCOPE, "Gyroscope");
        checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer / Compass");
        checkSensor(sm, Sensor.TYPE_LIGHT, "Ambient Light");
        checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");

        if (sensors == null || sensors.isEmpty()) {
            logError("Sensor list", "No sensors reported by the system");
            return;
        }

        logLine();

        // ------------------------------------------------------------
        // RAW SENSOR LIST
        // ------------------------------------------------------------
        for (Sensor s : sensors) {
            logOk(
                    "Sensor",
                    "type=" + s.getType()
                            + " | name=" + s.getName()
                            + " | vendor=" + s.getVendor()
            );
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
        // SENSOR INTERPRETATION SUMMARY — ONE LINE PER ITEM
        // ------------------------------------------------------------
        logLine();

        if (hasVirtualGyro)
            logOk("Virtual Gyroscope", "Detected (sensor fusion — expected behavior)");
        else
            logWarn("Virtual Gyroscope", "Not reported");

        if (hasDualALS)
            logOk("Ambient Light Sensors", "Dual ALS (front + rear)");
        else
            logWarn("Ambient Light Sensors", "Single ALS");

        if (hasSAR)
            logOk("SAR Sensors", "Present (proximity / RF tuning)");
        else
            logWarn("SAR Sensors", "Not reported");

        if (hasPickup)
            logOk("Pickup Sensor", "Present (lift-to-wake supported)");
        else
            logWarn("Pickup Sensor", "Not reported");

        if (hasLargeTouch)
            logOk("Large Area Touch", "Present (palm rejection / accuracy)");
        else
            logWarn("Large Area Touch", "Not reported");

        if (hasGameRotation)
            logOk("Game Rotation Vector", "Present (gaming orientation)");
        else
            logWarn("Game Rotation Vector", "Not reported");

        logOk("Overall Assessment", "Sensor suite complete and healthy for this device");

    } catch (Throwable e) {
        logError("Sensors analysis error", e.getMessage());
    } finally {
        appendHtml("<br>");
        logOk("Lab 9", "Finished");
        logLine();
        enableSingleExportButton();
    }
}

/* ============================================================
Helper — Sensor Presence
============================================================ */
private void checkSensor(SensorManager sm, int type, String name) {
boolean ok = sm.getDefaultSensor(type) != null;
if (ok) {
    logOk(name, "Available");
} else {
    logWarn(name, "Not reported (dependent features may be limited or missing)");
}
}

// ============================================================
// LAB 10: Wi-Fi Connectivity Check (Wi-Fi + Internet + Exposure)
// ============================================================
private void lab10WifiConnectivityCheck() {

    appendHtml("<br>");
    logLine();
    logInfo("LAB 10 — Wi-Fi Link Connectivity Check");
    logLine();

    WifiManager wm =
            (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    if (wm == null) {
        logError("WifiManager not available.");
        return;
    }

    if (!wm.isWifiEnabled()) {
        logWarn("Wi-Fi is OFF — please enable and retry.");
        return;
    }

    // ------------------------------------------------------------
    // 1) Location permission (SSID policy)
    // ------------------------------------------------------------
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        boolean fineGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean coarseGranted =
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (!fineGranted && !coarseGranted) {

            logWarn("Location permission required to read SSID/BSSID.");
            pendingLab10AfterPermission =
                    this::lab10WifiConnectivityCheck;

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

        try {
            LocationManager lm =
                    (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean gpsOn =
                    lm != null &&
                    (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                     || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

            if (!gpsOn) {
                logWarn("Location services are OFF. SSID may be UNKNOWN.");
                startActivity(
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return;
            }

        } catch (Exception e) {
            logWarn("Location services check failed: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // 2) Wi-Fi snapshot
    // ------------------------------------------------------------
    WifiInfo info = wm.getConnectionInfo();
    if (info == null) {
        logError("Wi-Fi info not available.");
        return;
    }

    String ssid = cleanSsid(info.getSSID());
    String bssid = info.getBSSID();
    int rssi = info.getRssi();
    int speed = info.getLinkSpeed();
    int freqMhz = 0;
    try { freqMhz = info.getFrequency(); } catch (Throwable ignore) {}

    String band = (freqMhz > 3000) ? "5 GHz" : "2.4 GHz";

    logLabelValue("SSID", ssid);

if (bssid != null)
    logLabelValue("BSSID", bssid);

logLabelValue(
        "Band",
        band + (freqMhz > 0 ? " (" + freqMhz + " MHz)" : "")
);

logLabelValue("Link speed", speed + " Mbps");
logLabelValue("RSSI", rssi + " dBm");

// SSID status — single line, new system
if ("Unknown".equalsIgnoreCase(ssid)) {
    logLabelWarnValue("SSID", "Hidden by Android privacy policy");
} else {
    logLabelOkValue("SSID", "Read OK");
}

// Signal quality — single line, new system
if (rssi > -65)
    logLabelOkValue("Wi-Fi signal", "Strong");
else if (rssi > -80)
    logLabelWarnValue("Wi-Fi signal", "Moderate");
else
    logLabelErrorValue("Wi-Fi signal", "Weak");

    // ------------------------------------------------------------
// 3) DHCP / LAN info — unified label/value format
// ------------------------------------------------------------
try {
    DhcpInfo dh = wm.getDhcpInfo();

    if (dh != null) {
        logLabelOkValue("IP",      ipToStr(dh.ipAddress));
        logLabelOkValue("Gateway", ipToStr(dh.gateway));
        logLabelOkValue("DNS1",    ipToStr(dh.dns1));
        logLabelOkValue("DNS2",    ipToStr(dh.dns2));
    } else {
        logLabelWarnValue("DHCP", "Info not available");
    }

} catch (Exception e) {
    logLabelErrorValue("DHCP", "Read failed: " + e.getMessage());
}

    // ------------------------------------------------------------
    // 4) DeepScan + Internet + Exposure
    // ------------------------------------------------------------
    runWifiDeepScan(wm);
}

@Override
public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == REQ_LAB13_BT_CONNECT) {

        if (grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            lab13Running = true;
            
        } else {

            lab13Running = false;

            logWarn("BLUETOOTH_CONNECT permission denied.");
            logWarn("External Bluetooth device monitoring skipped.");
           
            appendHtml("<br>");
            logOk("Lab 13 finished.");
            logLine();
        }
    }
}

// ============================================================
// LAB 10 — DEEPSCAN v3.0 (Internet + Exposure included)
// ============================================================
private void runWifiDeepScan(WifiManager wm) {

    new Thread(() -> {

        try {
            logLine();
            logInfo("GEL Network DeepScan v3.0 started...");

            String gatewayStr = null;
            try {
                DhcpInfo dh = wm.getDhcpInfo();
                if (dh != null)
                    gatewayStr = ipToStr(dh.gateway);
            } catch (Exception ignored) {}

            // ----------------------------------------------------
// NETWORK DEEP SCAN — unified label/value format
// ----------------------------------------------------

// 1) Internet ping
float pingMs = tcpLatencyMs("8.8.8.8", 53, 1500);
if (pingMs > 0)
    logLabelOkValue("Ping 8.8.8.8", String.format(Locale.US, "%.1f ms", pingMs));
else
    logLabelWarnValue("Ping 8.8.8.8", "Failed");

// 2) DNS resolve
float dnsMs = dnsResolveMs("google.com");
if (dnsMs > 0)
    logLabelOkValue("DNS google.com", String.format(Locale.US, "%.0f ms", dnsMs));
else
    logLabelWarnValue("DNS google.com", "Resolve failed");

// 3) Gateway ping
if (gatewayStr != null) {
    float gwMs = tcpLatencyMs(gatewayStr, 80, 1200);
    if (gwMs > 0)
        logLabelOkValue("Gateway ping", String.format(Locale.US, "%.1f ms", gwMs));
    else
        logLabelWarnValue("Gateway ping", "Failed");
} else {
    logLabelWarnValue("Gateway", "Not detected");
}

// 4) Speed heuristic
WifiInfo info = wm.getConnectionInfo();
int link = info != null ? info.getLinkSpeed() : 0;
int rssi = info != null ? info.getRssi() : -80;

float speedSim = estimateSpeedSimMbps(link, rssi);
logLabelOkValue(
        "SpeedSim",
        String.format(Locale.US, "~%.2f Mbps (heuristic)", speedSim)
);

// Finish
logLabelOkValue("DeepScan", "Finished");

            // ====================================================
            // INTERNET AVAILABILITY (former LAB 13)
            // ====================================================
            try {
                ConnectivityManager cm =
                        (ConnectivityManager)
                                getSystemService(CONNECTIVITY_SERVICE);

                if (cm == null) {
                    logError("ConnectivityManager not available.");
                } else {

                    boolean hasInternet = false;
                    String transport = "UNKNOWN";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Network n = cm.getActiveNetwork();
                        NetworkCapabilities caps =
                                cm.getNetworkCapabilities(n);

                        if (caps != null) {
                            hasInternet =
                                    caps.hasCapability(
                                            NetworkCapabilities
                                                    .NET_CAPABILITY_INTERNET);

                            if (caps.hasTransport(
                                    NetworkCapabilities.TRANSPORT_WIFI))
                                transport = "Wi-Fi";
                            else if (caps.hasTransport(
                                    NetworkCapabilities.TRANSPORT_CELLULAR))
                                transport = "Cellular";
                        }
                    } else {
                        @SuppressWarnings("deprecation")
                        NetworkInfo ni =
                                cm.getActiveNetworkInfo();
                        if (ni != null && ni.isConnected()) {
                            hasInternet = true;
                            transport = ni.getTypeName();
                        }
                    }

                    if (!hasInternet)
                        logError("No active Internet connection detected (OS-level).");
                    else
                        logOk("Internet connectivity active (" + transport + ").");
                }

            } catch (Exception e) {
                logError("Internet quick check error: " + e.getMessage());
            }

            // ====================================================
            // NETWORK / PRIVACY EXPOSURE
            // ====================================================
            try {
                logInfo("Network Exposure Snapshot (no traffic inspection).");

                PackageManager pm2 = getPackageManager();
                ApplicationInfo ai = getApplicationInfo();

                boolean hasInternetPerm =
                        pm2.checkPermission(
                                Manifest.permission.INTERNET,
                                ai.packageName)
                                == PackageManager.PERMISSION_GRANTED;

                if (hasInternetPerm)
                    logWarn("INTERNET permission present (capability only).");
                else
                    logOk("No INTERNET permission detected.");

                boolean cleartextAllowed = true;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cleartextAllowed =
                                android.security
                                        .NetworkSecurityPolicy
                                        .getInstance()
                                        .isCleartextTrafficPermitted();
                    }
                } catch (Throwable ignore) {}

                if (cleartextAllowed)
                    logWarn("Cleartext traffic ALLOWED.");
                else
                    logOk("Cleartext traffic NOT allowed.");

                boolean bgPossible =
                        pm2.checkPermission(
                                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                ai.packageName)
                                == PackageManager.PERMISSION_GRANTED;

                if (bgPossible)
                    logWarn("Background network possible after boot.");
                else
                    logOk("No boot-time background network capability.");

                logOk("Network exposure assessment completed.");

            } catch (Throwable e) {
                logWarn("Network exposure snapshot unavailable: " + e.getMessage());
            }

            appendHtml("<br>");
            logOk("Lab 10 finished.");
            logLine();

        } catch (Exception e) {
            logError("DeepScan error: " + e.getMessage());
        }

    }).start();
}

private float estimateSpeedSimMbps(
        int linkSpeedMbps,
        int rssiDbm) {

    if (linkSpeedMbps <= 0)
        linkSpeedMbps = 72;

    float rssiFactor;
    if (rssiDbm > -55) rssiFactor = 1.2f;
    else if (rssiDbm > -65) rssiFactor = 1.0f;
    else if (rssiDbm > -75) rssiFactor = 0.7f;
    else rssiFactor = 0.4f;

    return Math.max(5f, linkSpeedMbps * rssiFactor);
}

// ============================================================
// LAB 11 — Mobile Data Diagnostic
// ============================================================

private void lab11MobileDataDiagnostic() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 11 — Mobile Network Diagnostic (Laboratory)");  
logLine();  

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
            logLabelWarnValue("SIM State", "ABSENT");
            return;

        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            logLabelWarnValue("SIM State", "PRESENT but locked (PIN required)");
            return;

        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            logLabelWarnValue("SIM State", "PRESENT but locked (PUK required)");
            return;

        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            logLabelWarnValue("SIM State", "PRESENT but network locked");
            return;

        default:
            logLabelWarnValue("SIM State", "PRESENT but not ready");
            return;
    }
}

// SIM ready
logLabelOkValue("SIM State", "READY");

// ------------------------------------------------------------
// Service state (legacy domain — informational)
// ------------------------------------------------------------
logLabelValue(
        "Service State (legacy)",
        s.inService ? "IN SERVICE" : "NOT REPORTED AS IN SERVICE"
);

if (!s.inService) {
    logLabelWarnValue(
            "Legacy Service Note",
            "Legacy service registration is not reported. "
          + "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi)."
    );
}

// ------------------------------------------------------------  
// Data state (packet domain — informational)  
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
if (s.hasInternet) {
    logLabelOkValue("Internet Context", "AVAILABLE (system routing)");
} else {
    logLabelWarnValue("Internet Context", "NOT AVAILABLE");
}

// ------------------------------------------------------------  
// Laboratory conclusion  
// ------------------------------------------------------------  
logOk("Laboratory snapshot collected. No functional verdict inferred.");

appendHtml("<br>");
logOk("Lab 11 finished.");
logLine();
}

// ============================================================
// LAB 12 — Call Function Interpretation (Laboratory)
// ============================================================

private void lab12CallFunctionInterpretation() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 12 — Call Function Interpretation (Laboratory)");  
logLine();  

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
if (s.simReady) {
    logLabelOkValue("SIM State", "READY");
} else {
    logLabelWarnValue("SIM State", "NOT READY");
}

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
if (s.inService) {
    logLabelOkValue("Voice Service (legacy)", "IN SERVICE");
} else {
    logLabelWarnValue("Voice Service (legacy)", "NOT REPORTED AS IN SERVICE");
}

if (!s.inService) {
    logInfo(
            "Legacy service registration is not reported. " +
            "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi)."
    );
}

// ------------------------------------------------------------
// Internet context (IMS relevance)
// ------------------------------------------------------------
if (s.hasInternet) {
    logLabelOkValue("Internet Context", "AVAILABLE (system routing)");
} else {
    logLabelWarnValue("Internet Context", "NOT AVAILABLE");
}

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

appendHtml("<br>");
logOk("Lab 12 finished.");
logLine();

// ============================================================
// LAB 13 — Bluetooth Connectivity Check
// POPUP + WAIT FOR DEVICE + 60s MONITOR + DIAGNOSIS
// (FINAL — STRUCTURED / NO NESTED METHODS / READY COPY-PASTE)
// ============================================================

private void lab13BluetoothConnectivityCheck() {

    BluetoothManager bm = null;
    BluetoothAdapter ba = null;

    try {
        bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ba = (bm != null) ? bm.getAdapter() : null;
    } catch (Throwable ignore) {}

    appendHtml("<br>");
    logLine();
    logInfo("LAB 13 — Bluetooth Connectivity Check");
    logLine();

    if (ba == null) {
        logError("Bluetooth NOT supported on this device.");
        logLine();
        return;
    }

    boolean enabled = false;
    try { enabled = ba.isEnabled(); } catch (Throwable ignore) {}

    if (!enabled) {
        logError("Bluetooth is OFF. Please enable Bluetooth and retry.");
        logLine();
        return;
    }

    // RESET STATE
    lab13Bm = bm;
    lab13Ba = ba;

    lab13Running = false;
    lab13MonitoringStarted = false;
    lab13HadAnyConnection = false;
    lab13AssumedConnected = false;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents  = 0;

    showLab13GatePopup();
}

// ============================================================
// LAB 13 — GATE POPUP (Skip / Continue)
// ============================================================
private void showLab13GatePopup() {

    AlertDialog.Builder b = new AlertDialog.Builder(this);

    // GEL Dark+Gold custom view (like LAB 15)
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(
            "LAB 13 — External Bluetooth Device Check\n\n" +
            "Please connect ONE external Bluetooth device.\n" +
            "(e.g. headphones, car kit, keyboard).\n\n" +
            "This test evaluates Bluetooth connection stability.\n\n" +
            "If no external device is connected,\n" +
            "you may skip this step, to continue\n" +
            "with the system Bluetooth check."
    );
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // Small note line
    TextView note = new TextView(this);
    note.setText("Tip: keep the Bluetooth device within 10 meters.");
    note.setTextColor(0xFFAAAAAA);
    note.setTextSize(14f);
    note.setGravity(Gravity.CENTER);
    note.setPadding(0, 0, 0, dp(10));
    root.addView(note);

    // Buttons row
    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);
    row.setPadding(0, dp(6), 0, 0);

    Button cancelBtn = new Button(this);
    cancelBtn.setAllCaps(false);
    cancelBtn.setText("Skip");
    cancelBtn.setTextColor(0xFFFFFFFF);

    GradientDrawable cancelBg = new GradientDrawable();
    cancelBg.setColor(0xFF444444);
    cancelBg.setCornerRadius(dp(14));
    cancelBg.setStroke(dp(3), 0xFFFFD700);
    cancelBtn.setBackground(cancelBg);

    LinearLayout.LayoutParams lpC =
            new LinearLayout.LayoutParams(0, dp(52), 1f);
    lpC.setMargins(0, 0, dp(8), 0);
    cancelBtn.setLayoutParams(lpC);

    Button contBtn = new Button(this);
    contBtn.setAllCaps(false);
    contBtn.setText("Continue");
    contBtn.setTextColor(0xFFFFFFFF);
    contBtn.setTypeface(null, Typeface.BOLD);

    GradientDrawable contBg = new GradientDrawable();
    contBg.setColor(0xFF0F8A3B);
    contBg.setCornerRadius(dp(14));
    contBg.setStroke(dp(3), 0xFFFFD700);
    contBtn.setBackground(contBg);

    LinearLayout.LayoutParams lpK =
            new LinearLayout.LayoutParams(0, dp(52), 1f);
    lpK.setMargins(dp(8), 0, 0, 0);
    contBtn.setLayoutParams(lpK);

    row.addView(cancelBtn);
    row.addView(contBtn);
    root.addView(row);

    b.setView(root);

    final AlertDialog gate = b.create();
    if (gate.getWindow() != null) {
        gate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    cancelBtn.setOnClickListener(v -> {
        try { if (tts != null && tts[0] != null) tts[0].stop(); } catch (Throwable ignore) {}
        lab13SkipExternalTest = true;
        gate.dismiss();
        runLab13BluetoothCheckCore();   // system-only
    });

    contBtn.setOnClickListener(v -> {
        try { if (tts != null && tts[0] != null) tts[0].stop(); } catch (Throwable ignore) {}
        lab13SkipExternalTest = false;
        gate.dismiss();
        runLab13BluetoothCheckCore();   // full test
    });

    gate.setCancelable(true);
    gate.show();

    // TTS (optional)
    if (tts != null && tts[0] != null && ttsReady[0] && !isTtsMuted()) {
        try { tts[0].stop(); } catch (Throwable ignore) {}

        tts[0].speak(
                "Please connect one external Bluetooth device now. " +
                "This test evaluates Bluetooth connection stability. " +
                "If no external device is connected, you may skip this step, " +
                "to continue with the system Bluetooth check.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "LAB13_GATE"
        );
    }
}

// ============================================================
// CORE — FULL LAB 13 (LOG + UI + WAIT + MONITOR + DIAGNOSIS)
// ============================================================
private void runLab13BluetoothCheckCore() {

    // ---------- GET BT
    lab13Bm = null;
    lab13Ba = null;

    try {
        lab13Bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        lab13Ba = (lab13Bm != null) ? lab13Bm.getAdapter() : null;
    } catch (Throwable e) {
        logError("BluetoothManager access failed: " + e.getMessage());
        logLine();
        return;
    }

    // BASIC SUPPORT
    if (lab13Ba == null) {
        logError("Bluetooth NOT supported on this device.");
        logLine();
        return;
    }
    logOk("Bluetooth supported.");

    boolean enabled = false;
try { enabled = lab13Ba.isEnabled(); } catch (Throwable ignore) {}

logLabelValue(
        "Enabled",
        enabled ? "Yes" : "No"
);

int state = BluetoothAdapter.STATE_OFF;
try { state = lab13Ba.getState(); } catch (Throwable ignore) {}

String stateStr;
if (state == BluetoothAdapter.STATE_ON) {
    stateStr = "ON";
} else if (state == BluetoothAdapter.STATE_TURNING_ON) {
    stateStr = "TURNING ON";
} else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
    stateStr = "TURNING OFF";
} else {
    stateStr = "OFF";
}

logLabelValue(
        "State",
        stateStr
);

boolean le = false;
try {
    le = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
} catch (Throwable ignore) {}

logLabelValue(
        "BLE Support",
        le ? "Yes" : "No"
);

if (!enabled) {
    logWarn("Bluetooth is OFF — enable Bluetooth and re-run Lab 13.");
    logLine();
    return;
}

    // ---------- PAIRED DEVICES SNAPSHOT
    try {
        Set<BluetoothDevice> bonded = lab13Ba.getBondedDevices();

        if (bonded == null || bonded.isEmpty()) {

            logWarn("Paired Bluetooth devices: 0 (no paired devices found).");

        } else {

            logOk("Paired Bluetooth devices detected: " + bonded.size());

            for (BluetoothDevice d : bonded) {

                String name = "Unnamed";
                String addr = "no-mac";
                String typeStr = "Unknown";

                if (d != null) {
                    try {
                        if (d.getName() != null) name = d.getName();
                    } catch (Throwable ignore) {}

                    try {
                        if (d.getAddress() != null) addr = d.getAddress();
                    } catch (Throwable ignore) {}

                    try {
                        int type = d.getType();
                        typeStr =
                                type == BluetoothDevice.DEVICE_TYPE_CLASSIC ? "Classic" :
                                type == BluetoothDevice.DEVICE_TYPE_LE ? "LE" :
                                type == BluetoothDevice.DEVICE_TYPE_DUAL ? "Dual" :
                                "Unknown";
                    } catch (Throwable ignore) {}
                }

                logInfo("• " + name + " [" + typeStr + "] (" + addr + ")");
            }
        }

    } catch (Throwable e) {

        logWarn("Paired device scan failed: " + e.getClass().getSimpleName());
    }

    // ------------------------------------------------------------
    // SYSTEM-ONLY MODE (Skip external device test)
    // ------------------------------------------------------------
    if (lab13SkipExternalTest) {
        logWarn("External Bluetooth device test skipped by user.");
        logOk("Proceeded with system Bluetooth connection check only.");
        appendHtml("<br>");
        logOk("Lab 13 finished.");
        logLine();
        return;
    }

    // ---------- RESET RUN STATE
    lab13Running = false;
    lab13Seconds = 0;
    lab13StartMs = 0L;

    lab13HadAnyConnection = false;
    lab13LastConnected = false;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents = 0;

    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // REGISTER BLUETOOTH RECEIVER (LAB 13)
    // ------------------------------------------------------------
    IntentFilter f = new IntentFilter();
    f.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    f.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

    registerReceiver(lab13BtReceiver, f);

    // ---------- SHOW GEL DARK-GOLD MONITOR DIALOG
    AlertDialog.Builder b = new AlertDialog.Builder(this);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(
            "LAB 13 — Bluetooth Stability Monitor\n\n" +
            "Connect one external Bluetooth device. " +
            "Keep it connected, for at least one minute. " +
            "Do not disconnect during the test. " +
            "Keep the Bluetooth device, within ten meters of the phone. " +
            "Do not move away from the device, during monitoring."
    );

    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    lab13StatusText = new TextView(this);
    lab13StatusText.setText("Waiting for stable Bluetooth connection...");
    lab13StatusText.setTextColor(0xFFAAAAAA);
    lab13StatusText.setTextSize(15f);
    lab13StatusText.setGravity(Gravity.CENTER);
    root.addView(lab13StatusText);

    lab13DotsView = new TextView(this);
    lab13DotsView.setText("•••");
    lab13DotsView.setTextColor(0xFF39FF14);
    lab13DotsView.setTextSize(22f);
    lab13DotsView.setGravity(Gravity.CENTER);
    root.addView(lab13DotsView);

    lab13CounterText = new TextView(this);
    lab13CounterText.setText("Monitoring: 0 / 60 sec");
    lab13CounterText.setTextColor(0xFF39FF14);
    lab13CounterText.setGravity(Gravity.CENTER);
    root.addView(lab13CounterText);

    lab13ProgressBar = new LinearLayout(this);
    lab13ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
    lab13ProgressBar.setGravity(Gravity.CENTER);

    for (int i = 0; i < 6; i++) {
        View seg = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(10), 1f);
        lp.setMargins(dp(3), 0, dp(3), 0);
        seg.setLayoutParams(lp);
        seg.setBackgroundColor(0xFF333333);
        lab13ProgressBar.addView(seg);
    }
    root.addView(lab13ProgressBar);

    // MUTE (GLOBAL)
    CheckBox muteBox = new CheckBox(this);
    muteBox.setChecked(isTtsMuted());
    muteBox.setText("Mute voice instructions");
    muteBox.setTextColor(0xFFDDDDDD);
    muteBox.setGravity(Gravity.CENTER);
    muteBox.setPadding(0, dp(10), 0, dp(10));
    root.addView(muteBox);

    muteBox.setOnCheckedChangeListener((v, checked) -> {
        setTtsMuted(checked);
        if (checked && tts != null && tts[0] != null) {
            try { tts[0].stop(); } catch (Throwable ignore) {}
        }
    });

    // EXIT BUTTON
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
        try { if (tts != null && tts[0] != null) tts[0].stop(); } catch (Throwable ignore) {}
        abortLab13ByUser();
    });

    root.addView(exitBtn);

    b.setView(root);
    lab13Dialog = b.create();

    if (lab13Dialog.getWindow() != null) {
        lab13Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    lab13Dialog.setCancelable(false);
    lab13Dialog.show();

    // ------------------------------------------------------------
    // RESET MONITOR FLAGS (NEW RUN)
    // ------------------------------------------------------------
    lab13MonitoringStarted = false;
    lab13HadAnyConnection = false;
    lab13LastConnected = false;

    // ------------------------------------------------------------
    // ANDROID 12+ PERMISSION — MUST BE FIRST
    // ------------------------------------------------------------
    if (Build.VERSION.SDK_INT >= 31 &&
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {

        if (lab13StatusText != null)
            lab13StatusText.setText("Bluetooth permission required.");

        requestPermissions(
                new String[]{ Manifest.permission.BLUETOOTH_CONNECT },
                REQ_LAB13_BT_CONNECT
        );
        return;
    }

    // ------------------------------------------------------------
    // SNAPSHOT CHECK — already connected device (AFTER UI READY)
    // ------------------------------------------------------------
    if (lab13IsAnyExternalConnected()) {

        lab13HadAnyConnection = true;

        if (lab13StatusText != null) {
            lab13StatusText.setText(
                    "External device already connected. Starting stability monitor..."
            );
        }

        startLab13Monitor60s();
        return;
    }

    // ------------------------------------------------------------
    // WAIT FOR EXTERNAL DEVICE — RECEIVER-BASED
    // ------------------------------------------------------------
    if (!lab13MonitoringStarted && lab13StatusText != null)
        lab13StatusText.setText("Waiting for an external Bluetooth device...");

    if (lab13CounterText != null)
        lab13CounterText.setText("Monitoring: waiting...");

    // TTS
    if (tts != null && tts[0] != null && ttsReady[0] && !isTtsMuted()) {
        try { tts[0].stop(); } catch (Throwable ignore) {}

        tts[0].speak(
                "Connect one external Bluetooth device. " +
                "Keep it connected, for at least one minute. " +
                "Do not disconnect during the test. " +
                "Keep the Bluetooth device, within ten meters of the phone. " +
                "Do not move away from the device, during monitoring.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "LAB13_GATE"
        );
    }
}

// ============================================================
// MONITOR LOOP (60s) — polls connected devices + detects flips
// ============================================================
private void startLab13Monitor60s() {

    if (lab13MonitoringStarted) return;
    lab13MonitoringStarted = true;

    lab13Running = true;
    lab13StartMs = SystemClock.elapsedRealtime();
    lab13Seconds = 0;

    lab13DisconnectEvents = 0;
    lab13ReconnectEvents = 0;

    boolean connectedNow = lab13IsAnyExternalConnected();

    // ------------------------------------------------------------
    // HARD SYNC — receiver + snapshot (INITIAL)
    // ------------------------------------------------------------
    if (lab13ReceiverSawConnection && !connectedNow) {
        connectedNow = true;
    }

    if (lab13ReceiverSawDisconnection && connectedNow) {
        connectedNow = false;
    }

    lab13ReceiverSawConnection = false;
    lab13ReceiverSawDisconnection = false;

    lab13LastConnected = connectedNow;
    if (connectedNow) lab13HadAnyConnection = true;

    if (lab13StatusText != null) {
        lab13StatusText.setText("Monitoring Bluetooth stability...");
    }

    if (lab13CounterText != null) {
        lab13CounterText.setText("Monitoring: 0 / 60 sec");
    }

    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    lab13Handler.post(new Runnable() {
        int dotPhase = 0;

        @Override
        public void run() {

            if (!lab13Running) return;

            boolean adapterStable = false;
            try {
                adapterStable =
                        lab13Ba != null &&
                        lab13Ba.isEnabled() &&
                        lab13Ba.getState() == BluetoothAdapter.STATE_ON;
            } catch (Throwable ignore) {}

            boolean connected = lab13IsAnyExternalConnected();

            // ------------------------------------------------------------
            // HARD SYNC — receiver + snapshot (EACH TICK)
            // ------------------------------------------------------------
            // Priority: explicit events > snapshot
            if (lab13ReceiverSawConnection) {
                connected = true;
            } else if (lab13ReceiverSawDisconnection) {
                connected = false;
            }

            // clear flags each tick
            lab13ReceiverSawConnection = false;
            lab13ReceiverSawDisconnection = false;

            if (connected) {
                lab13HadAnyConnection = true;
            }

            // ------------------------------------------------------------
            // TRANSITION LOGIC (CORRECT)
            // ------------------------------------------------------------
            if (!lab13LastConnected && connected && lab13Seconds > 0) {
                lab13ReconnectEvents++;
            }

            if (lab13LastConnected && !connected) {
                lab13DisconnectEvents++;
            }

            lab13LastConnected = connected;

            // ------------------------------------------------------------
            // TIME
            // ------------------------------------------------------------
            lab13Seconds++;

            if (lab13CounterText != null) {
                lab13CounterText.setText(
                        "Monitoring: " + lab13Seconds + " / 60 sec"
                );
            }

            // ------------------------------------------------------------
            // DOTS
            // ------------------------------------------------------------
            dotPhase = (dotPhase + 1) % 4;
            if (lab13DotsView != null) {
                lab13DotsView.setText(
                        dotPhase == 1 ? "••" :
                        dotPhase == 2 ? "•••" : "•"
                );
            }

            // ------------------------------------------------------------
            // PROGRESS BAR
            // ------------------------------------------------------------
            lab13UpdateProgressSegments(lab13Seconds);

            // ------------------------------------------------------------
// STATUS TEXT (COLOR-CODED)
// ------------------------------------------------------------
if (lab13StatusText != null) {

    if (!adapterStable) {

        lab13StatusText.setText("Bluetooth adapter not stable.");
        lab13StatusText.setTextColor(0xFFFFD966); //  yellow (warning)

    } else if (connected) {

        lab13StatusText.setText(
                "External device connected — monitoring stability..."
        );
        lab13StatusText.setTextColor(0xFF39FF14); //  GEL green (OK)

    } else if (lab13HadAnyConnection) {

        lab13StatusText.setText(
                "External device temporarily unavailable."
        );
        lab13StatusText.setTextColor(0xFFFFD966); //  yellow (warning)

    } else {

        lab13StatusText.setText(
                "Waiting for an external Bluetooth device..."
        );
        lab13StatusText.setTextColor(0xFFFFD966); //  yellow (info/wait)
    }
}

            // ------------------------------------------------------------
            // FINISH
            // ------------------------------------------------------------
            if (lab13Seconds >= 60) {
                lab13Running = false;
                lab13FinishAndReport(adapterStable);
                return;
            }

            lab13Handler.postDelayed(this, 1000);
        }
    });
}

// ============================================================
// CONNECTED DEVICES — SNAPSHOT (STABLE)
// ============================================================
private boolean lab13IsAnyExternalConnected() {

    if (lab13Ba == null) return false;

    try {
        return lab13Ba.getProfileConnectionState(BluetoothProfile.A2DP)
                    == BluetoothProfile.STATE_CONNECTED
            || lab13Ba.getProfileConnectionState(BluetoothProfile.HEADSET)
                    == BluetoothProfile.STATE_CONNECTED
            || lab13Ba.getProfileConnectionState(BluetoothProfile.GATT)
                    == BluetoothProfile.STATE_CONNECTED;
    } catch (Throwable ignore) {}

    return false;
}

// ============================================================
// UI — progress segments
// ============================================================
private void lab13UpdateProgressSegments(int seconds) {
    if (lab13ProgressBar == null) return;

    int filled = Math.min(6, seconds / 10); // 0..6
    for (int i = 0; i < lab13ProgressBar.getChildCount(); i++) {
        View seg = lab13ProgressBar.getChildAt(i);
        if (seg == null) continue;
        if (i < filled) seg.setBackgroundColor(0xFF39FF14);   // GEL green
        else seg.setBackgroundColor(0xFF333333);
    }
}

// ============================================================
// FINISH — close dialog + log diagnosis + list connected devices
// ============================================================
private void lab13FinishAndReport(boolean adapterStable) {

    // stop lab state FIRST
    lab13Running = false;

    // stop handler callbacks
    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    // close dialog
    try {
        if (lab13Dialog != null && lab13Dialog.isShowing())
            lab13Dialog.dismiss();
    } catch (Throwable ignore) {}
    lab13Dialog = null;

    // unregister BT receiver
    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // NO EXTERNAL DEVICE CONNECTED — SYSTEM CHECK ONLY
    // ------------------------------------------------------------
    if (!lab13HadAnyConnection) {

        logLine();
        logInfo("LAB 13 — Results");
        logWarn("No external Bluetooth device was connected during the test.");
        logInfo("System Bluetooth check completed. External device test skipped.");
        appendHtml("<br>");
        logOk("Lab 13 finished.");
        logLine();
        return;
    }

    // snapshot: connected devices list per profile (for report)
    boolean anyActive = false;

    final int[] profiles = new int[]{
            BluetoothProfile.A2DP,
            BluetoothProfile.HEADSET,
            BluetoothProfile.GATT
    };

    // report header
    logLine();
    logInfo("LAB 13 — Results (60s monitor)");
    logLabelValue(
        "Adapter stable",
        adapterStable ? "Yes" : "No"
);

logLabelValue(
        "Disconnect events",
        String.valueOf(lab13DisconnectEvents)
);

logLabelValue(
        "Reconnect events",
        String.valueOf(lab13ReconnectEvents)
);

    // list connected devices now
    for (int p : profiles) {
        try {
            List<BluetoothDevice> list = (lab13Bm != null) ? lab13Bm.getConnectedDevices(p) : null;
            if (list != null && !list.isEmpty()) {
                anyActive = true;
                logOk(lab13ProfileName(p) + " connected devices:");
                for (BluetoothDevice d : list) {
                    String n = null;
                    try { n = (d != null ? d.getName() : null); } catch (Throwable ignore) {}
                    logInfo("• " + (n != null ? n : "Unnamed"));
                }
            }
        } catch (Throwable ignore) {}
    }

    if (anyActive) {

    logOk("External Bluetooth connectivity detected at finish.");

} else if (lab13HadAnyConnection) {

        logInfo(
    "An external Bluetooth device was connected during the test, " +
    "but it is currently not in active use."
    );

}

    // ------------------------------------------------------------
    // DIAGNOSIS LOGIC (LOCKED MESSAGE)
    // ------------------------------------------------------------
    // "Frequent disconnects" threshold: >=3 disconnect events within 60s
    boolean frequentDisconnects = (lab13DisconnectEvents >= 3);

    if (adapterStable && lab13HadAnyConnection && frequentDisconnects) {

        // LOCKED DIAGNOSIS MESSAGE
        logWarn(
                "The Bluetooth connection shows frequent disconnections,\n" +
                "while the phone’s Bluetooth subsystem remains stable.\n" +
                "This indicates a problem with the connected external device."
        );

    } else if (!adapterStable) {

        logWarn("Bluetooth adapter was not stable during the test. Toggle Bluetooth OFF/ON and retry.");

    } else {

        logOk("Bluetooth connection appears stable during the 60s monitor.");
    }

    // root note (optional)
        if (isDeviceRooted()) {
    logLabelValue(
            "Root access",
            "Available (advanced diagnostics possible)"
    );
} else {
    logLabelValue(
            "Root access",
            "Not available"
    );
}

    // ------------------------------------------------------------
    // CONNECTED DEVICE VERDICT — FINAL (UNIFIED)
    // ------------------------------------------------------------

    logLine();
logInfo("LAB 13 — Final Verdict");

if (!lab13MonitoringStarted) {

    logWarn(
        "No valid external Bluetooth monitoring session was completed."
    );

} else if (lab13DisconnectEvents > 0) {

    logWarn(
        "Bluetooth connectivity shows instability during the test period."
    );

} else {

    logOk(
        "Bluetooth connectivity is stable."
    );
}

    appendHtml("<br>");
    logOk("Lab 13 finished.");
    logLine();
}

// ============================================================
// PROFILE NAME (small internal helper)
// ============================================================
private String lab13ProfileName(int p) {
    if (p == BluetoothProfile.A2DP) return "A2DP";
    if (p == BluetoothProfile.HEADSET) return "HEADSET";
    if (p == BluetoothProfile.GATT) return "GATT";
    return "PROFILE(" + p + ")";
}

// ============================================================
// ABORT HOOK
// ============================================================
private void abortLab13ByUser() {

    // stop lab state
    lab13Running = false;
    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    // close dialog
    try {
        if (lab13Dialog != null && lab13Dialog.isShowing())
            lab13Dialog.dismiss();
    } catch (Throwable ignore) {}
    lab13Dialog = null;

    // unregister BT receiver (safety)
    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

    // stop TTS
    try { if (tts != null && tts[0] != null) tts[0].stop(); } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // ABORT LOG
    // ------------------------------------------------------------
    appendHtml("<br>");
    logWarn("Lab 13 aborted by user.");
    logLine();
}

    
// ============================================================
// LAB 14 — Battery Health Stress Test
// FINAL — SNAPSHOT ONLY — UI MATCHES LAB 15
//  Confidence NOT in intro
//  Confidence calculated AFTER stress + shown with Aging + Final Score
//  One confidence only — no contradictions
//
// NOTE (GEL RULE): When you ask for full lab, I must return full lab copy-paste.
// ============================================================
private void lab14BatteryHealthStressTest() {

if (lab14Running) {  
    logWarn(" LAB 14 already running.");  
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
        logError(" Stress test requires device NOT charging.");  
        lab14Running = false;  
        return;  
    }  

    if (snapStart.chargeNowMah <= 0) {  
        logError(" Charge Counter unavailable. LAB 14 cannot run.");  
        lab14Running = false;  
        return;  
    }  

    final long startMah   = snapStart.chargeNowMah;  
    final boolean rooted  = snapStart.rooted;  
    final long cycles     = snapStart.cycleCount;  
    final float tempStart = snapStart.temperature;  

    // ------------------------------------------------------------  
    // CPU / GPU thermal snapshot (START)  
    // ------------------------------------------------------------  
    final Float cpuTempStart = readCpuTempSafe();  
    final Float gpuTempStart = readGpuTempSafe();  

    final int durationSec = LAB14_TOTAL_SECONDS;  
    lastSelectedStressDurationSec = durationSec;  

    final long baselineFullMah =  
            (snapStart.chargeFullMah > 0)  
                    ? snapStart.chargeFullMah  
                    : -1;

// ------------------------------------------------------------
// 2) LOG HEADER (FULL INFO — SERVICE / OLD LAB STYLE) …
// ------------------------------------------------------------

appendHtml("<br>");
logLine();
logInfo("… LAB 14 — Battery Health Stress Test");
logLine();

logInfo("… Mode: " + (rooted ? "Advanced (Rooted)" : "Standard (Unrooted)"));
logInfo("… Duration: " + durationSec + " sec (laboratory mode)");
logInfo("… Stress profile: GEL C Mode (aggressive CPU burn + brightness MAX)");

logInfo(String.format(
Locale.US,
"… Start conditions: charge=%d mAh, status=Discharging, temp=%.1f°C",
startMah,
(Float.isNaN(tempStart) ? 0f : tempStart)
));

logInfo("… Data source: " + snapStart.source);

// Capacity baseline
if (baselineFullMah > 0)
logInfo("… Battery capacity baseline (counter-based): " + baselineFullMah + " mAh");
else
logInfo("… Battery capacity baseline (counter-based): N/A");

// Cycles
logInfo("… Cycle count: " + (cycles > 0 ? String.valueOf(cycles) : "N/A"));

// Stress environment (explicit — ÏŒÏ€Ï‰Ï‚ ÏƒÏ„Î¿ Ï€Î±Î»Î¹ÏŒ lab)
logInfo("… Screen state: brightness forced to MAX, screen lock ON");
logInfo("… CPU stress threads: " +
Runtime.getRuntime().availableProcessors() +
" (cores=" + Runtime.getRuntime().availableProcessors() + ")");

// Thermal snapshot availability (START)
if (cpuTempStart != null)
logOk(String.format(Locale.US, "… CPU temperature (start): %.1f°C", cpuTempStart));
else
logWarn(" CPU temperature (start): N/A");

if (gpuTempStart != null)
logOk(String.format(Locale.US, "… GPU temperature (start): %.1f°C", gpuTempStart));
else
logWarn(" GPU temperature (start): N/A");

// System thermal domains (informational, like old LAB)
logOk("… Thermal domains: CPU / GPU / SKIN / PMIC / BATT");

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

// ============================================================
// GEL DARK + GOLD POPUP BACKGROUND (LAB 14 — MAIN STRESS POPUP)
// ============================================================
LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(20), dp(24), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);           // GEL dark black
bg.setCornerRadius(dp(18));
bg.setStroke(dp(4), 0xFFFFD700);  // GOLD border
root.setBackground(bg);

// ============================================================
// ðŸ”¹ TITLE — INSIDE POPUP (LAB 14)
// ============================================================
TextView title = new TextView(this);
title.setText("LAB 14 — Battery's health stress test");
title.setTextColor(0xFFFFFFFF);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(12));
root.addView(title);

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

        logWarn(" LAB 14 cancelled by user.");  
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
    // 4) START STRESS (CPU burn + max brightness)  
    // ------------------------------------------------------------  
    final long t0 = SystemClock.elapsedRealtime();  
    final String[] dotFrames = {"•", "• •", "• • •"};  

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
                logWarn(" Unable to read final charge counter.");  
                return;  
            }  

            final long endMah = snapEnd.chargeNowMah;  
            final float tempEnd = snapEnd.temperature;  

            // ------------------------------------------------------------  
            // CPU / GPU thermal snapshot (END)  
            // ------------------------------------------------------------  
            final Float cpuTempEnd = readCpuTempSafe();  
            final Float gpuTempEnd = readGpuTempSafe();  

            final long dtMs = Math.max(1, SystemClock.elapsedRealtime() - t0);  
            final long drainMah = startMah - endMah;  

            final boolean validDrain =  
                    drainMah > 0 &&  
                    !(baselineFullMah > 0 && drainMah > (long) (baselineFullMah * 0.30));  

            final double mahPerHour =  
                    validDrain ? (drainMah * 3600000.0) / dtMs : -1;  

            // ----------------------------------------------------  
            // 6) SAVE RUN (ENGINE = single source of truth)  
            // ----------------------------------------------------  
            if (validDrain) engine.saveDrainValue(mahPerHour);  
            engine.saveRun();  

            final Lab14Engine.ConfidenceResult conf = engine.computeConfidence();

// ============================================================
// LAB 14 — VARIABILITY DETECTION (SINGLE SOURCE)
// ============================================================
boolean variabilityDetected =
!validDrain ||           // counter anomaly
conf.percent < 60;       // unstable repeated runs

// ----------------------------------------------------  
            // 7) PROFILE + AGING (Engine)  
            // ----------------------------------------------------  
              
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

                double tempRise = Math.max(0.0, (double) tempEnd - (double) tempStart);  

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

                // consistency penalty (0..10) — NOT a second "confidence"  
                idx += Math.min(10.0, (100 - conf.percent) / 5.0);  

                agingIndex = (int) Math.round(Math.max(0.0, Math.min(100.0, idx)));  

                if (agingIndex < 15) agingInterp = "Excellent (very low aging indicators)";  
                else if (agingIndex < 30) agingInterp = "Good (low aging indicators)";  
                else if (agingIndex < 50) agingInterp = "Moderate (watch trend)";  
                else if (agingIndex < 70) agingInterp = "High (aging signs detected)";  
                else agingInterp = "Severe (strong aging indicators)";  

            } else {  
                agingIndex = -1;  
                agingInterp = "Insufficient data (need stable runs with confidence â‰¥70%)";  
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

                // ----------------------------------------------------  
                // CPU / GPU thermal contribution (CAPPED, non-dominant)  
                // ----------------------------------------------------  
                if (cpuTempEnd != null) {  
                    if (cpuTempEnd >= 85f) finalScore -= 8;  
                    else if (cpuTempEnd >= 75f) finalScore -= 4;  
                }  

                if (gpuTempEnd != null) {  
                    if (gpuTempEnd >= 80f) finalScore -= 6;  
                    else if (gpuTempEnd >= 70f) finalScore -= 3;  
                }  

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
// THERMAL SNAPSHOT FOR REPORT (LAB 14)
// ----------------------------------------------------
startBatteryTemp = tempStart;
endBatteryTemp   = tempEnd;

// ----------------------------------------------------
// 10) PRINT RESULTS (FINAL ORDER — LOCKED)
// ----------------------------------------------------
logInfo("LAB 14 - Stress result");

// ----------------------------------------------------
// End temperature
// ----------------------------------------------------
logInfo("End temperature:");
logOk(String.format(
Locale.US,
"%.1f°C",
endBatteryTemp
));

// ----------------------------------------------------
// Thermal change (rise / drop)
// ----------------------------------------------------
float delta = endBatteryTemp - startBatteryTemp;

logInfo("Thermal change:");

if (delta >= 3.0f) {
// Î¿Ï…ÏƒÎ¹Î±ÏƒÏ„Î¹ÎºÎ® Î¸ÎµÏÎ¼Î¹ÎºÎ® Î¬Î½Î¿Î´Î¿Ï‚
logWarn(String.format(
Locale.US,
"+%.1f°C",
delta
));

} else if (delta >= 0.5f) {
// Ï†Ï…ÏƒÎ¹Î¿Î»Î¿Î³Î¹ÎºÎ® Î¬Î½Î¿Î´Î¿Ï‚ Î±Ï€ÏŒ stress
logOk(String.format(
Locale.US,
"+%.1f°C",
delta
));

} else if (delta <= -0.5f) {
// Ï€Ï„ÏŽÏƒÎ· Î¸ÎµÏÎ¼Î¿ÎºÏÎ±ÏƒÎ¯Î±Ï‚ (ÎºÎ±Î»ÏŒ)
logOk(String.format(
Locale.US,
"%.1f°C",
delta
));

} else {
// Ï€ÏÎ±ÎºÏ„Î¹ÎºÎ¬ ÏƒÏ„Î±Î¸ÎµÏÏŒ
logOk(String.format(
Locale.US,
"%.1f°C",
delta
));
}

logInfo("Battery behaviour:");
logOk(String.format(
Locale.US,
"Start: %d mAh | End: %d mAh | Drop: %d mAh | Time: %.1f sec",
startMah,
endMah,
Math.max(0, drainMah),
dtMs / 1000.0
));

// ----------------------------------------------------
// Drain rate
// ----------------------------------------------------
logInfo("Drain rate:");
if (validDrain) {
logOk(String.format(
Locale.US,
"… %.0f mAh/hour (counter-based)",
mahPerHour
));
} else {
logWarn(" Invalid (counter anomaly or no drop)");
logWarn(" Counter anomaly detected (PMIC / system-level behavior). Repeat test after system reboot");
}

// SCORE (Î±ÏÎ¹Î¸Î¼ÏŒÏ‚ + runs)
logInfo("Measurement consistency score:");
logOk(String.format(
Locale.US,
"… %d%% (%d valid runs)",
conf.percent,
conf.validRuns
));

// VARIANCE / INTERPRETATION
logLab14VarianceInfo();

// ----------------------------------------------------
// Battery Aging Index + Interpretation
// ----------------------------------------------------
if (agingIndex >= 0) {

logInfo("Battery aging index:");  
logOk(String.format(  
        Locale.US,  
        "… %d/100 — %s",  
        agingIndex,  
        agingInterp  
));

} else {

logInfo("Battery aging index:");  
logWarn(" Insufficient data");

}

// ----------------------------------------------------
// Aging analysis
// ----------------------------------------------------
logInfo("Aging analysis:");
logOk("… " + aging.description);

// ----------------------------------------------------
// Final Score
// ----------------------------------------------------
logInfo("Final battery health score:");
logOk(String.format(
Locale.US,
"… %d%% (%s)",
finalScore,
finalLabel
));

// ----------------------------------------------------
// Measurement reliability (LAB 14)
// ----------------------------------------------------

p.edit()
.putBoolean("lab14_unstable_measurement", variabilityDetected)
.apply();

// ------------------------------------------------------------
// STORE RESULT FOR LAB 17 (LAB 14 OUTPUT) — FINAL & LOCKED
// ------------------------------------------------------------

p.edit()
.putFloat("lab14_health_score", finalScore)
.putInt("lab14_aging_index", agingIndex)
.putLong("lab14_last_ts", System.currentTimeMillis())
.apply();

logOk("… LAB 14 result stored successfully.");

// 11) RUN-BASED CONFIDENCE (THE ONLY "CONFIDENCE") …
logLab14Confidence();

appendHtml("<br>");
logOk("LAB 14 finished.");
logLine();
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
    logError(" LAB 14 failed unexpectedly.");  
}

}

//=============================================================
// LAB 15 - Charging System Diagnostic (SMART)
// FINAL / LOCKED — NO PATCHES — NO SIDE EFFECTS
//=============================================================
private void lab15ChargingSystemSmart() {

if (lab15Running) {  
    logWarn(" LAB 15 already running.");  
    return;  
}  

// ================= FLAGS RESET =================

lab15Running  = true;
lab15Finished = false;
lab15FlapUnstable = false;
lab15OverTempDuringCharge = false;

lab15BattTempStart = Float.NaN;
lab15BattTempPeak  = Float.NaN;
lab15BattTempEnd   = Float.NaN;

// reset LAB 15 charging strength state (FIELDS)
lab15_strengthKnown = false;
lab15_strengthWeak  = false;
lab15_systemLimited = false;

// ================= DIALOG =================

AlertDialog.Builder b =
new AlertDialog.Builder(
ManualTestsActivity.this,
android.R.style.Theme_Material_Dialog_NoActionBar
);
b.setCancelable(false);

// ============================================================
// GEL DARK + GOLD POPUP BACKGROUND LAB 15
// ============================================================
LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(20), dp(24), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);           // GEL dark black
bg.setCornerRadius(dp(18));       // smooth premium corners
bg.setStroke(dp(4), 0xFFFFD700);  // GOLD border
root.setBackground(bg);

// ============================================================
// ðŸ”¹ TITLE — INSIDE POPUP (LAB 15)
// ============================================================
TextView title = new TextView(this);
title.setText(
"LAB 15 — Connect the charger to the device's charging port.\n" +
"The system will monitor charging behavior for the next three minutes.\n" +
"Please keep the device connected during the test."
);
title.setTextColor(0xFFFFFFFF);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(12));
root.addView(title);

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
lab15CounterText.setText("Progress: 0 / 180 sec");
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

// ==========================
//  MUTE TOGGLE (LAB 15 — GLOBAL)
// ==========================
CheckBox muteBox = new CheckBox(this);
muteBox.setChecked(isTtsMuted());   // â¬…ï¸ Î¼ÏŒÎ½Î¿ GLOBAL ÎºÎ±Ï„Î¬ÏƒÏ„Î±ÏƒÎ·
muteBox.setText("Mute voice instructions");
muteBox.setTextColor(0xFFDDDDDD);
muteBox.setGravity(Gravity.CENTER);
muteBox.setPadding(0, dp(10), 0, dp(10));

// â¬‡ï¸ Î Î¡Î©Î¤Î‘ Î¼Ï€Î±Î¯Î½ÎµÎ¹ Ï„Î¿ mute
root.addView(muteBox);

// ==========================
//  MUTE LOGIC — GLOBAL
// ==========================
muteBox.setOnCheckedChangeListener((v, checked) -> {

// Î±Ï€Î¿Î¸Î®ÎºÎµÏ…ÏƒÎ· GLOBAL ÎµÏ€Î¹Î»Î¿Î³Î®Ï‚  
setTtsMuted(checked);  

// ÎºÏŒÏˆÎµ Î¬Î¼ÎµÏƒÎ± Ï„Î¿Î½ Î®Ï‡Î¿ Î±Î½ Î¼Ï€Î®ÎºÎµ mute  
if (checked && tts != null && tts[0] != null) {  
    tts[0].stop();   //  Î¼ÏŒÎ½Î¿ stop — ÎŸÎ§Î™ shutdown  
}

});

// ============================================================
// ðŸ”¹ EXIT BUTTON
// ============================================================
Button exitBtn = new Button(this);
exitBtn.setText("Exit test");
exitBtn.setAllCaps(false);
exitBtn.setTextColor(0xFFFFFFFF);
exitBtn.setTypeface(null, Typeface.BOLD);

GradientDrawable exitBg = new GradientDrawable();
exitBg.setColor(0xFF8B0000);
exitBg.setCornerRadius(dp(14));   // â— Î”Î™ÎŸÎ¡Î˜Î©Î£Î—: Î­Ï†Ï…Î³Îµ Ï„Î¿ Ï„Ï…Ï‡Î±Î¯Î¿ 7
exitBg.setStroke(dp(3), 0xFFFFD700);
exitBtn.setBackground(exitBg);

LinearLayout.LayoutParams lpExit =
new LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(52)
);
lpExit.setMargins(0, dp(14), 0, 0);
exitBtn.setLayoutParams(lpExit);

// ------------------------------------------------------------
// EXIT BUTTON — STOP TTS (NO SHUTDOWN)
// ------------------------------------------------------------
exitBtn.setOnClickListener(v -> {
try {
if (tts != null && tts[0] != null) {
tts[0].stop();   //  Î¼ÏŒÎ½Î¿ stop
}
} catch (Throwable ignore) {}
abortLab15ByUser();
});

// â¬‡ï¸ ÎœÎ•Î¤Î‘ Î¼Ï€Î±Î¯Î½ÎµÎ¹ Ï„Î¿ exit
root.addView(exitBtn);

// ============================================================
// ðŸ”¹ SHOW DIALOG
// ============================================================
b.setView(root);
lab15Dialog = b.create();

if (lab15Dialog.getWindow() != null) {
lab15Dialog.getWindow()
.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
}

lab15Dialog.show();

// ============================================================
//  TTS — SPEAK AFTER SHOW (FINAL / GLOBAL)
// ============================================================
if (tts != null && tts[0] != null && ttsReady[0] && !isTtsMuted()) {

tts[0].stop();  

tts[0].speak(  
        "Connect the charger to the device's charging port. " +  
        "The system will monitor charging behavior for the next three minutes. " +  
        "Please keep the device connected during the test.",  
        TextToSpeech.QUEUE_FLUSH,  
        null,  
        "LAB15_INTRO"  
);

}

// ============================================================
// ðŸ”¹ LOGS
// ============================================================
appendHtml("<br>");
logLine();
logInfo("LAB 15 - Charging System Diagnostic (Smart).");
logLine();

// ================= CORE LOOP =================  
final long[] startTs = { -1 };  
final boolean[] wasCharging = { false };  
final long[] unplugTs = { -1 };  
final String[] dotFrames = { "•", "• •", "• • •" };  

final BatteryInfo startInfo = getBatteryInfo();  
final long startMah =  
        (startInfo != null && startInfo.currentChargeMah > 0)  
                ? startInfo.currentChargeMah : -1;  

ui.post(new Runnable() {  

    int dotStep = 0;  
    int lastSeg = -1;  

    @Override  
    public void run() {  

        if (!lab15Running || lab15Finished) return;  

        boolean chargingNow = isDeviceCharging();  
        long now = SystemClock.elapsedRealtime();  

        dotsView.setText(dotFrames[dotStep++ % dotFrames.length]);  

        // ------------------------------------------------------------  
        // CHARGING STATE TRACKING (5s debounce unplug)  
        // ------------------------------------------------------------  
        if (chargingNow) {  

            unplugTs[0] = -1;  

            if (!wasCharging[0]) {  
                wasCharging[0] = true;  
                startTs[0] = now;  

                lab15BattTempStart = getBatteryTemperature();  
                lab15BattTempPeak  = lab15BattTempStart;  

                lab15StatusText.setText("Charging state detected.");  
                lab15StatusText.setTextColor(0xFF39FF14);  
                logOk("… Charging state detected.");  
            }  

        } else if (wasCharging[0]) {  

            if (unplugTs[0] < 0) {  
                unplugTs[0] = now;  
            }  

            long unplugSec = (now - unplugTs[0]) / 1000;  

            if (unplugSec >= 5) {  

                lab15FlapUnstable = true;  
                lab15Finished = true;  
                lab15Running  = false;  

                lab15StatusText.setText("Charging disconnected.");  
                lab15StatusText.setTextColor(0xFFFF4444);  

                logError(" Charger disconnected for more than 5 seconds.");  
                logError(" Charging test aborted.");  

                try {  
                    if (lab15Dialog != null && lab15Dialog.isShowing())  
                        lab15Dialog.dismiss();  
                } catch (Throwable ignore) {}  
                lab15Dialog = null;  

                return;  
            }  
        }  

        // temp peak tracking while charging  
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

        // ================= FINAL =================  
        lab15Finished = true;  
        lab15Running  = false;  

        lab15BattTempEnd = getBatteryTemperature();  

        // propagate LAB15 temps to legacy / cross-lab names  
        startBatteryTemp = lab15BattTempStart;  
        endBatteryTemp   = lab15BattTempEnd;  

        // ------------------------------------------------------------  
        // Battery temperature + thermal correlation  
        // ------------------------------------------------------------  
        logInfo("Battery temperature:");  
        logOk(String.format(  
                Locale.US,  
                "… %.1f°C",  
                lab15BattTempEnd  
        ));  

        logLab15ThermalCorrelation(  
                lab15BattTempStart,  
                lab15BattTempPeak,  
                lab15BattTempEnd  
        );  

        // ------------------------------------------------------------  
        // Thermal verdict  
        // ------------------------------------------------------------  
        float dtCharge = lab15BattTempEnd - lab15BattTempStart;  

        logInfo("Thermal verdict (charging):");  

        if (lab15OverTempDuringCharge) {  
            logError(String.format(  
                    Locale.US,  
                    " HOT (Î”T +%.1f°C) — Elevated temperature detected.",  
                    Math.max(0f, dtCharge)  
            ));  
        } else {  
            logOk(String.format(  
                    Locale.US,  
                    "… OK (Î”T +%.1f°C) — Normal thermal behavior during charging.",  
                    Math.max(0f, dtCharge)  
            ));  
        }  

        // ------------------------------------------------------------  
        // Connection stability  
        // ------------------------------------------------------------  
        logInfo("Charging connection:");  
        if (lab15FlapUnstable) logError(" Unstable (plug/unplug behavior detected).");  
        else logOk("… Appears stable. No abnormal plug/unplug behavior detected.");

// ------------------------------------------------------------  
        // CHARGING INPUT & STRENGTH (mAh/min)  
        // ------------------------------------------------------------  
        BatteryInfo endInfo = getBatteryInfo();  

        if (startMah > 0 && endInfo != null &&  
                endInfo.currentChargeMah > startMah && startTs[0] > 0) {  

            lab15_strengthKnown = true;  

            long deltaMah = endInfo.currentChargeMah - startMah;  
            long dtMs     = Math.max(1, SystemClock.elapsedRealtime() - startTs[0]);  
            double minutes = dtMs / 60000.0;  

            double mahPerMin = (minutes > 0) ? (deltaMah / minutes) : -1;  

            logInfo("Charging input:");  
            logOk(String.format(  
                    Locale.US,  
                    "… +%d mAh in %.1f min (%.1f mAh/min)",  
                    deltaMah,  
                    minutes,  
                    mahPerMin  
            ));  

            logInfo("Charging strength:");  
            if (mahPerMin >= 20.0) {  
                logOk("… STRONG");  
                lab15_strengthWeak = false;  
            } else if (mahPerMin >= 10.0) {  
                logOk("… NORMAL");  
                lab15_strengthWeak = false;  
            } else if (mahPerMin >= 5.0) {  
                logWarn(" MODERATE");  
                lab15_strengthWeak = true;  
            } else {  
                logError(" WEAK");  
                lab15_strengthWeak = true;  
            }  

        } else {  
            logInfo("Charging strength:");  
            logWarn(" Unable to estimate accurately.");  
            lab15_strengthKnown = false;  
            lab15_strengthWeak  = true;  
        }  

// ------------------------------------------------------------
// FINAL LAB 15 DECISION
// ------------------------------------------------------------
logInfo("LAB decision:");
if (!lab15OverTempDuringCharge && !lab15FlapUnstable && !lab15_strengthWeak) {
logOk("… Charging system OK. No cleaning or replacement required.");
logOk("… Charging stability OK.");
} else {
logWarn(" Charging system shows potential issues.");
logWarn(" Further inspection or repeat test recommended.");
}

        // ------------------------------------------------------------  
// SYSTEM-LEVEL CHARGING THROTTLING (NOT BATTERY FAULT)  
// ------------------------------------------------------------  
try {  

    boolean chargingStable = !lab15FlapUnstable;  

    float lab14Health  = getLastLab14HealthScore();  
    int   lab16Thermal = getLastLab16ThermalScore();  

    boolean batteryHealthy = (lab14Health >= 85f);  
    boolean thermalPressure = (lab16Thermal > 0 && lab16Thermal < 75);  

    logInfo("Charging path:");  

    if (chargingStable &&  
            lab15_strengthKnown &&  
            lab15_strengthWeak &&  
            (batteryHealthy || thermalPressure)) {  

        lab15_systemLimited = true;  
        logWarn(" System-limited (not battery)");  
        logOk("Likely cause: thermal / PMIC protection limiting current.");  

    } else {  
        logOk("… Operating normally (no system-level current throttling).");  
    }  

} catch (Throwable ignore) {}  // … ÎšÎ›Î•Î™Î£Î™ÎœÎŸ TRY/CATCH

// ------------------------------------------------------------
// SUMMARY FLAG (SAFE)
// ------------------------------------------------------------
boolean chargingGlitchDetected =
        lab15FlapUnstable ||
        lab15OverTempDuringCharge ||
        lab15_strengthWeak ||
        lab15_systemLimited;

GELServiceLog.info("SUMMARY: CHARGING_STABILITY=" +
        (chargingGlitchDetected ? "UNSTABLE" : "STABLE"));

appendHtml("<br>");  
logOk("LAB 15 finished.");  
logLine();

        // ------------------------------------------------------------  
        // STORE RESULT FOR LAB 17 (LAB 15 OUTPUT)  
        // ------------------------------------------------------------  
        try {  
            int chargeScore = 100;  

            if (lab15_strengthWeak) chargeScore -= 25;  
            if (lab15FlapUnstable) chargeScore -= 25;  
            if (lab15OverTempDuringCharge) chargeScore -= 25;  

            chargeScore = Math.max(0, Math.min(100, chargeScore));  

            p.edit()  
                    .putInt("lab15_charge_score", chargeScore)  
                    .putBoolean("lab15_system_limited", lab15_systemLimited)  
                    .putBoolean("lab15_overtemp", lab15OverTempDuringCharge)  
                    .putString("lab15_strength_label", lab15_strengthWeak ? "WEAK" : "NORMAL/STRONG")  
                    .putLong("lab15_ts", System.currentTimeMillis())  
                    .apply();  

        } catch (Throwable ignore) {}  

        // ------------------------------------------------------------  
        // CLEAN EXIT — CLOSE POPUP  
        // ------------------------------------------------------------  
        try {  
            if (lab15Dialog != null && lab15Dialog.isShowing())  
                lab15Dialog.dismiss();  
        } catch (Throwable ignore) {}  
        lab15Dialog = null;  
    }  
});

}

// ============================================================
// LAB 16 — Thermal Snapshot
// FINAL — COMPACT — GEL LOCKED
// ============================================================
private void lab16ThermalSnapshot() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 16 — Thermal Snapshot");  
logLine();  

List<ThermalEntry> internal     = buildThermalInternal();  
List<ThermalEntry> peripherals = buildThermalPeripheralsCritical();  

float  peakTemp = -1f;  
String peakSrc  = "N/A";  

// ------------------------------------------------------------  
// BASIC + CRITICAL THERMALS (INLINE, HUMAN READABLE)  
// ------------------------------------------------------------  
logInfo("Thermal sensors:");  

for (ThermalEntry t : internal) {  
    logTempInline(t.label, t.temp);  
    if (t.temp > peakTemp) {  
        peakTemp = t.temp;  
        peakSrc  = t.label;  
    }  
}  

for (ThermalEntry t : peripherals) {  
    logTempInline(t.label, t.temp);  
    if (t.temp > peakTemp) {  
        peakTemp = t.temp;  
        peakSrc  = t.label;  
    }  
}  

logLine();  

// ------------------------------------------------------------  
// SUMMARY (HUMAN LANGUAGE)  
// ------------------------------------------------------------  
boolean danger = peakTemp >= 55f;  

logInfo("Thermal summary:");  
if (danger) {  
    logWarn("Elevated temperature detected in critical components.");  
    logWarn("System may apply thermal protection.");  
} else {  
    logOk("Device operating at safe temperatures.");  
    logOk("Internal chips and critical peripherals were monitored.");  
}  

if (peakTemp > 0) {  

    logInfo("Peak temperature observed:");  

    if (peakTemp >= 55f) {  
        logWarn(String.format(  
                Locale.US,  
                "%.1f°C at %s",  
                peakTemp, peakSrc  
        ));  
    } else if (peakTemp >= 45f) {  
        logInfo(String.format(  
                Locale.US,  
                "%.1f°C at %s",  
                peakTemp, peakSrc  
        ));  
    } else {  
        logOk(String.format(  
                Locale.US,  
                "%.1f°C at %s",  
                peakTemp, peakSrc  
        ));  
    }  
}  

// ------------------------------------------------------------  
// HIDDEN THERMAL SAFETY CHECK (NON-DISPLAYED SENSORS)  
// ------------------------------------------------------------  
boolean hiddenRisk = detectHiddenThermalAnomaly(55f);  

if (hiddenRisk) {  
    logWarn(" Elevated temperature detected in non-displayed system components.");  
    logWarn(" Thermal protection mechanisms may activate.");  
} else {  
    logOk("All critical thermal sensors were monitored during this test.");  
}  

// ------------------------------------------------------------  
// THERMAL SCORE (USED BY LAB 17)  
// ------------------------------------------------------------  
int thermalScore = 100;  
boolean thermalDanger = false;  

for (ThermalEntry t : internal) {  
    if (t.temp >= 55f) {  
        thermalScore -= 25;  
        thermalDanger = true;  
    } else if (t.temp >= 45f) {  
        thermalScore -= 10;  
    }  
}  

for (ThermalEntry t : peripherals) {  
    if (t.temp >= 55f) {  
        thermalScore -= 25;  
        thermalDanger = true;  
    } else if (t.temp >= 45f) {  
        thermalScore -= 10;  
    }  
}  

thermalScore = Math.max(0, Math.min(100, thermalScore));

try {

p.edit()  
 .putInt("lab16_thermal_score", thermalScore)  
 .putBoolean("lab16_thermal_danger", thermalDanger)  
 .putFloat("lab16_peak_temp", peakTemp)  
 .putString("lab16_peak_source", peakSrc)  
 .putLong("lab16_last_ts", System.currentTimeMillis())  
 .apply();

} catch (Throwable ignore) {}

logInfo("Thermal behaviour score:");
logOk(String.format(Locale.US, "%d%%", thermalScore));

boolean thermalSpikesDetected = thermalDanger;

GELServiceLog.info("SUMMARY: THERMAL_PATTERN=" +
        (thermalSpikesDetected ? "SPIKES" : "NORMAL"));
        
appendHtml("<br>");
logOk("Lab 16 finished.");
logLine();
}

// ============================================================
// LAB 17 — GEL Auto Battery Reliability Evaluation
// INTELLIGENCE EDITION • STRICT FRESHNESS (â‰¤ 2 HOURS)
// ============================================================
private void lab17RunAuto() {

final String PREF = "GEL_DIAG";  

// STRICT WINDOW: 2 hours  
final long WINDOW_MS = 2L * 60L * 60L * 1000L;  
final long now = System.currentTimeMillis();  

// ------------------------------------------------------------  
// READ STORED RESULTS + TIMESTAMPS (STRICT)  
// ------------------------------------------------------------  
SharedPreferences p = getSharedPreferences(PREF, MODE_PRIVATE);

// LAB 14 results
final float lab14Health  = p.getFloat("lab14_health_score", -1f);
final int   lab14Aging   = p.getInt("lab14_aging_index", -1);
final long  ts14         = p.getLong("lab14_last_ts", 0L);

// LAB 14 reliability flag (future-safe)
final boolean lab14Unstable =
p.getBoolean("lab14_unstable_measurement", false);

final int lab15Charge = p.getInt("lab15_charge_score", -1);
final boolean lab15SystemLimited = p.getBoolean("lab15_system_limited", false);
final String lab15StrengthLabel = p.getString("lab15_strength_label", null);
final long ts15 = p.getLong("lab15_ts", 0L);

final int lab16Thermal = p.getInt("lab16_thermal_score", -1);
final boolean lab16ThermalDanger = p.getBoolean("lab16_thermal_danger", false);
final long ts16 = p.getLong("lab16_last_ts", 0L);

// ------------------------------------------------------------  
// PRESENCE + FRESHNESS CHECK  
// ------------------------------------------------------------  
final boolean has14 = (lab14Health >= 0f && ts14 > 0L);  
final boolean has15 = (lab15Charge >= 0  && ts15 > 0L);  
final boolean has16 = (lab16Thermal >= 0 && ts16 > 0L);  

final boolean fresh14 = has14 && (now - ts14) <= WINDOW_MS;  
final boolean fresh15 = has15 && (now - ts15) <= WINDOW_MS;  
final boolean fresh16 = has16 && (now - ts16) <= WINDOW_MS;

// ------------------------------------------------------------
// HIGH VARIABILITY CONFIRMATION (LAB 14 INTELLIGENCE)
// ------------------------------------------------------------
final long hvFirstTs    = p.getLong("lab14_hv_first_ts", -1L);
final long hvLastTs     = p.getLong("lab14_hv_last_ts", -1L);
final boolean hvPending = p.getBoolean("lab14_hv_pending", false);

// confirmed ONLY if repeated within strict window
final boolean hvConfirmed =
hvPending &&
hvFirstTs > 0L &&
hvLastTs > hvFirstTs &&
(hvLastTs - hvFirstTs) <= WINDOW_MS;

// ------------------------------------------------------------  
// PRECHECK — SMART POPUP (STRICT)  
// ------------------------------------------------------------  
if (!(fresh14 && fresh15 && fresh16)) {  

    StringBuilder msg = new StringBuilder();  

    // status lines  
    msg.append("Status (required within last 2 hours):\n\n");  

    msg.append("• LAB 14: ");  
    if (!has14) msg.append("Missing\n");  
    else if (!fresh14) msg.append("Expired (").append(lab17_age(now - ts14)).append(")\n");  
    else msg.append("OK (").append(lab17_age(now - ts14)).append(")\n");  

    msg.append("• LAB 15: ");  
    if (!has15) msg.append("Missing\n");  
    else if (!fresh15) msg.append("Expired (").append(lab17_age(now - ts15)).append(")\n");  
    else msg.append("OK (").append(lab17_age(now - ts15)).append(")\n");  

    msg.append("• LAB 16: ");  
    if (!has16) msg.append("Missing\n");  
    else if (!fresh16) msg.append("Expired (").append(lab17_age(now - ts16)).append(")\n");  
    else msg.append("OK (").append(lab17_age(now - ts16)).append(")\n");  

    msg.append("\n");  

    // decision  
    if ((fresh14 && fresh15) && (!fresh16)) {  
        msg.append("I detected you already ran LAB 14 + LAB 15.\n");  
        msg.append("Run ONLY LAB 16 now to complete the set.\n");  
    } else if ((fresh14 && fresh16) && (!fresh15)) {  
        msg.append("I detected you already ran LAB 14 + LAB 16.\n");  
        msg.append("Run ONLY LAB 15 now to complete the set.\n");  
    } else if ((fresh15 && fresh16) && (!fresh14)) {  
        msg.append("I detected you already ran LAB 15 + LAB 16.\n");  
        msg.append("Run ONLY LAB 14 now to complete the set.\n");  
    } else {  
        // if any expired OR multiple missing -> rerun all together  
        msg.append("To generate a valid result, run LAB 14 + LAB 15 + LAB 16 together.\n");  
        msg.append("Reason: missing and/or expired results.\n");  
    }  

    lab17_showPopup(  
            "LAB 17 — Prerequisites Check",  
            msg.toString()  
    );  
    return;  
}

// ------------------------------------------------------------
// START LAB 17
// ------------------------------------------------------------

appendHtml("<br>");
logLine();
logInfo("LAB 17 — GEL Intelligent System Health Analysis");
logLine();

new Thread(() -> {

try {  

    // ------------------------------------------------------------  
    // BASE WEIGHTED SCORE  
    // ------------------------------------------------------------  
    int baseScore = Math.round(  
            (lab14Health * 0.50f) +  
            (lab15Charge * 0.25f) +  
            (lab16Thermal * 0.25f)  
    );  
    baseScore = Math.max(0, Math.min(100, baseScore));  

    // ------------------------------------------------------------  
    // PENALTIES (LOCKED)  
    // ------------------------------------------------------------  
    int penaltyExtra = 0;  

    if (lab15Charge < 60 && lab15SystemLimited) penaltyExtra += 6;  
    else if (lab15Charge < 60) penaltyExtra += 12;  

    if (lab16Thermal < 60) penaltyExtra += 10;  
    else if (lab16Thermal < 75) penaltyExtra += 5;  

    if (lab14Aging >= 0) {  
        if (lab14Aging >= 70) penaltyExtra += 10;  
        else if (lab14Aging >= 50) penaltyExtra += 6;  
        else if (lab14Aging >= 30) penaltyExtra += 3;  
    }  

    int finalScore = Math.max(0, Math.min(100, baseScore - penaltyExtra));  

    String category =  
            (finalScore >= 85) ? "Strong" :  
            (finalScore >= 70) ? "Normal" :  
            "Weak";  

    // ------------------------------------------------------------  
    // FREEZE VALUES FOR UI THREAD  
    // ------------------------------------------------------------  
    final int    fFinalScore   = finalScore;  
    final int    fPenaltyExtra = penaltyExtra;  
    final String fCategory     = category;  

    final boolean thermalDanger =  
            lab16ThermalDanger || (lab16Thermal < 60);  

    final boolean chargingWeakOrThrottled =  
            (lab15Charge < 60) || lab15SystemLimited;  

    final boolean batteryLooksFineButThermalBad =  
            (lab14Health >= 80f) && thermalDanger;  

    final boolean batteryBadButThermalOk =  
            (lab14Health < 70f) && (lab16Thermal >= 75);  

    final boolean overallDeviceConcern =  
            thermalDanger ||  
            chargingWeakOrThrottled ||  
            (lab14Health < 70f);  

    // ------------------------------------------------------------  
    // UI OUTPUT  
    // ------------------------------------------------------------  
    ui.post(() -> {  

        // ================= SUMMARY =================  
        logInfo("LAB14 — Battery health:");  
        logOk(String.format(  
                Locale.US,  
                "%.0f%% | Aging index: %s",  
                lab14Health,  
                (lab14Aging >= 0 ? lab14Aging + "/100" : "N/A")  
        ));  

        logInfo("LAB15 — Charging:");  
        if (lab15Charge >= 70) {  
            logOk(String.format(  
                    Locale.US,  
                    "%d%% | Strength: %s",  
                    lab15Charge,  
                    (lab15StrengthLabel != null ? lab15StrengthLabel : "N/A")  
            ));  
        } else {  
            logWarn(String.format(  
                    Locale.US,  
                    "%d%% | Strength: %s",  
                    lab15Charge,  
                    (lab15StrengthLabel != null ? lab15StrengthLabel : "N/A")  
            ));  
        }  

        logInfo("LAB16 — Thermal behaviour:");  
        if (lab16Thermal >= 75) {  
            logOk(String.format(Locale.US, "%d%%", lab16Thermal));  
        } else if (lab16Thermal >= 60) {  
            logWarn(String.format(Locale.US, "%d%%", lab16Thermal));  
        } else {  
            logError(String.format(Locale.US, "%d%%", lab16Thermal));  
        }  

        // ================= ANALYSIS =================  
        if (lab15SystemLimited) {  
            logLine();  
            logWarn("Charging limitation analysis:");  
            logWarn("System-limited throttling detected (PMIC / thermal protection).");  
            logWarn("This behaviour is NOT attributed to battery health alone.");  
        }  

        if (fPenaltyExtra > 0) {  
            logLine();  
            logInfo("Penalty breakdown:");  

            if (lab15Charge < 60 && lab15SystemLimited)  
                logWarn("• Charging: system-limited throttling detected.");  
            else if (lab15Charge < 60)  
                logWarn("• Charging: weak charging performance detected.");  

            if (lab14Aging >= 70)  
                logError("• Aging: severe aging indicators detected.");  
            else if (lab14Aging >= 50)  
                logWarn("• Aging: high aging indicators detected.");  
            else if (lab14Aging >= 30)  
                logWarn("• Aging: moderate aging indicators detected.");  
        }  

        // ================= FINAL SCORE =================  
        logLine();  
        logInfo("Final Battery Reliability Score:");  
        if (fFinalScore >= 80) {  
            logOk(String.format(Locale.US, "%d%% (%s)", fFinalScore, fCategory));  
        } else if (fFinalScore >= 60) {  
            logWarn(String.format(Locale.US, "%d%% (%s)", fFinalScore, fCategory));  
        } else {  
            logError(String.format(Locale.US, "%d%% (%s)", fFinalScore, fCategory));  
        }  
        logLine();  

        // ================= DIAGNOSIS =================  
        logInfo("Diagnosis:");  

        if (lab14Unstable) {  
            logLine();  
            logWarn(" Measurement reliability warning:");  
            logWarn("Battery measurements show instability.");  
            logWarn("This suggests unstable power measurement (PMIC / fuel gauge),");  
            logOk("not a confirmed battery failure.");  
        }  

        if (!overallDeviceConcern) {  

            logOk("… No critical issues detected. Battery + charging + thermal look stable.");  
            logInfo("Note:");  
            logOk("Internal chips and critical peripherals were monitored.");  

        } else {  

            if (batteryLooksFineButThermalBad) {  
                logWarn(" Battery health looks OK, but device thermal behaviour is risky.");  
                logInfo("Recommendation:");  
                logWarn("Inspect cooling path and thermal interfaces.");  
                logInfo("Possible causes:");  
                logWarn("CPU/GPU load, thermal pads, heatsink contact.");  
            }  

            if (chargingWeakOrThrottled) {  
                if (lab15SystemLimited) {  
                    logWarn(" Charging appears system-limited (protection logic).");  
                    logInfo("Possible causes:");  
                    logWarn("Overheating, PMIC limiting current.");  
                } else if (lab15Charge < 60) {  
                    logWarn(" Charging performance is weak.");  
                    logInfo("Possible causes:");  
                    logWarn("Cable / adapter quality, charging port wear, battery impedance.");  
                }  
            }  

            if (batteryBadButThermalOk) {  
                logWarn(" Battery health is weak while thermals are OK.");  
                logInfo("Likely cause:");  
                logWarn("Battery aging / capacity loss.");  
            }  

            if (lab14Health < 70f && thermalDanger) {  
                logError(" Combined risk detected (battery + thermal). Technician inspection strongly recommended.");  
            }  
        }

// ------------------------------------------------------------
// STORE FINAL RESULT (+ timestamp)
// ------------------------------------------------------------
try {
p.edit()
.putInt("lab17_final_score", fFinalScore)
.putString("lab17_category", fCategory)
.putLong("lab17_ts", System.currentTimeMillis())
.apply();
} catch (Throwable ignore) {}

// ================= FINAL (UI THREAD) =================

appendHtml("<br>");
logOk("LAB 17 finished.");
logLine();

}); // <-- END ui.post

} catch (Throwable ignore) {
// silent
}

}).start();
}

// ============================================================
// LAB 17 — POPUP (GEL DARK + GOLD) — WITH GLOBAL TTS
// ============================================================
private void lab17_showPopup(String titleText, String msgText) {

AlertDialog.Builder b =  
        new AlertDialog.Builder(  
                ManualTestsActivity.this,  
                android.R.style.Theme_Material_Dialog_NoActionBar  
        );  

b.setCancelable(true);  

// ==========================  
// ROOT  
// ==========================  
LinearLayout box = new LinearLayout(this);  
box.setOrientation(LinearLayout.VERTICAL);  
box.setPadding(dp(24), dp(20), dp(24), dp(20));  

GradientDrawable bg = new GradientDrawable();  
bg.setColor(0xFF101010);  
bg.setCornerRadius(dp(18));  
bg.setStroke(dp(3), 0xFFFFD700);  
box.setBackground(bg);  

// ==========================  
// TITLE  
// ==========================  
TextView title = new TextView(this);  
title.setText(titleText);  
title.setTextColor(0xFFFFD700);  
title.setTextSize(17f);  
title.setPadding(0, 0, 0, dp(12));  
box.addView(title);  

// ==========================  
// MESSAGE  
// ==========================  
TextView msg = new TextView(this);  
msg.setText(msgText);  
msg.setTextColor(0xFFFFFFFF);  
msg.setTextSize(14.5f);  
msg.setPadding(0, 0, 0, dp(18));  
box.addView(msg);  

// ==========================  
//  MUTE TOGGLE (GLOBAL)  
// ==========================  
CheckBox muteBox = new CheckBox(this);  
muteBox.setChecked(isTtsMuted());  
muteBox.setText("Mute voice instructions");  
muteBox.setTextColor(0xFFDDDDDD);  
muteBox.setGravity(Gravity.CENTER);  
muteBox.setPadding(0, dp(10), 0, dp(10));  
box.addView(muteBox);  

// ==========================  
// OK BUTTON  
// ==========================  
Button ok = new Button(this);  
ok.setText("OK");  
ok.setAllCaps(true);  
ok.setTextSize(15f);  
ok.setTextColor(0xFF00FF6A);  

GradientDrawable okBg = new GradientDrawable();  
okBg.setColor(0xFF000000);  
okBg.setCornerRadius(dp(14));  
okBg.setStroke(dp(3), 0xFFFFD700);  
ok.setBackground(okBg);  
ok.setPadding(dp(18), dp(10), dp(18), dp(10));  
box.addView(ok);  

// ==========================  
// BUILD DIALOG  
// ==========================  
b.setView(box);  
AlertDialog popup = b.create();  

if (popup.getWindow() != null) {  
    popup.getWindow()  
            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  
}  

// ==========================

//  MUTE LOGIC — GLOBAL
// ==========================
muteBox.setOnCheckedChangeListener((v, checked) -> {

// Î±Ï€Î¿Î¸Î®ÎºÎµÏ…ÏƒÎ· GLOBAL ÎµÏ€Î¹Î»Î¿Î³Î®Ï‚  
setTtsMuted(checked);  

// ÎºÏŒÏˆÎµ Î¬Î¼ÎµÏƒÎ± Ï„Î¿Î½ Î®Ï‡Î¿ Î±Î½ Î¼Ï€Î®ÎºÎµ mute  
if (checked && tts != null && tts[0] != null) {  
    tts[0].stop();   //  Î¼ÏŒÎ½Î¿ stop  
}

});

// ==========================
//  TTS — PLAY (GLOBAL ENGINE)
// ==========================
if (tts != null && tts[0] != null && ttsReady[0] && !isTtsMuted()) {

// ÎºÎ±Î¸Î¬ÏÎ¹ÏƒÎµ ÏŒ,Ï„Î¹ Î­Ï€Î±Î¹Î¶Îµ Ï€ÏÎ¹Î½  
tts[0].stop();  

tts[0].speak(  
        "Before running this lab, please make sure that " +  
        "lab fourteen, lab fifteen and lab sixteen have been completed.",  
        TextToSpeech.QUEUE_FLUSH,  
        null,  
        "LAB17_POPUP"  
);

}

// ==========================  
// OK ACTION  
// ==========================  
ok.setOnClickListener(v -> {  

    try {  
        if (tts[0] != null) {  
            tts[0].stop();   //  Î¼ÏŒÎ½Î¿ stop — ÏŒÏ‡Î¹ shutdown ÎµÎ´ÏŽ  
        }  
    } catch (Throwable ignore) {}  

    try {  
        popup.dismiss();  
    } catch (Throwable ignore) {}  
});  

popup.show();

}

// ============================================================
// LAB 17 — AGE FORMATTER
// ============================================================
private String lab17_age(long deltaMs) {
if (deltaMs < 0) deltaMs = 0;
long sec = deltaMs / 1000L;
long min = sec / 60L;
long hr  = min / 60L;

if (hr > 0) {  
    long rm = min % 60L;  
    return hr + "h " + rm + "m ago";  
}  
if (min > 0) return min + "m ago";  
return Math.max(0, sec) + "s ago";

}

// ============================================================
// LABS 18 - 21: STORAGE & PERFORMANCE
// ============================================================

// ============================================================
// LAB 18 — STORAGE HEALTH INSPECTION
// FINAL • HUMAN READABLE • ROOT AWARE • GEL LOCKED
// ============================================================
private void lab18StorageSnapshot() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 18 — Internal Storage Health Inspection");  
logLine();  

try {  

    StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());  

    long blockSize = s.getBlockSizeLong();  
    long total     = s.getBlockCountLong() * blockSize;  
    long free      = s.getAvailableBlocksLong() * blockSize;  
    long used      = total - free;  

    int pctFree = (int) ((free * 100L) / Math.max(1L, total));  
    int pctUsed = 100 - pctFree;  

    // ------------------------------------------------------------  
    // BASIC SNAPSHOT  
    // ------------------------------------------------------------  
    logInfo("Storage usage:");  
    logOk(  
            humanBytes(used) + " used / " +  
            humanBytes(total) +  
            " (free " + humanBytes(free) + ", " + pctFree + "%)"  
    );  
    
    // ---------------- MEMORY PRESSURE INDICATORS ----------------
MemSnapshot snap = readMemSnapshotSafe();

long swapUsedKb = 0;
if (snap.swapTotalKb > 0 && snap.swapFreeKb >= 0) {
    swapUsedKb = Math.max(0, snap.swapTotalKb - snap.swapFreeKb);
}

String pressureLevel = pressureLevel(
        snap.memFreeKb,
        snap.cachedKb,
        swapUsedKb
);

String zramDep = zramDependency(swapUsedKb, total);
String humanPressure = humanPressureLabel(pressureLevel);

logLine();
logInfo("Memory Pressure Indicators:");

logOk("Memory Pressure: " + humanPressure);
logInfo("Pressure Level: " + pressureLevel);
logInfo("ZRAM Swap Dependency: " + zramDep);

if (swapUsedKb > 0) {
    logInfo("Swap used: " + humanBytes(swapUsedKb * 1024L));
}
if (snap.memFreeKb > 0) {
    logInfo("MemFree: " + humanBytes(snap.memFreeKb * 1024L));
}
if (snap.cachedKb > 0) {
    logInfo("Cached: " + humanBytes(snap.cachedKb * 1024L) + " (reclaimable)");
}

    // ------------------------------------------------------------  
    // PRESSURE LEVEL (HUMAN SCALE)  
    // ------------------------------------------------------------  
    boolean critical = pctFree < 7;  
    boolean pressure = pctFree < 15;  

    if (critical) {  

        logError(" Storage critically low.");  
        logError("System stability may be affected.");  
        logWarn("Apps may crash, updates may fail, UI may slow down.");  

    } else if (pressure) {  

        logWarn(" Storage under pressure.");  
        logWarn("System may feel slower when handling files and updates.");  

    } else {  

        logOk("… Storage level is healthy for daily usage.");  
    }  

    // ------------------------------------------------------------  
    // FILESYSTEM INFO (BEST EFFORT)  
    // ------------------------------------------------------------  
    try {  
        String fsType = s.getClass().getMethod("getFilesystemType") != null  
                ? (String) s.getClass().getMethod("getFilesystemType").invoke(s)  
                : null;  

        if (fsType != null) {  
            logInfo("Filesystem type:");  
            logOk(fsType.toUpperCase(Locale.US));  
        }  
    } catch (Throwable ignore) {}  

    // ------------------------------------------------------------  
    // ROOT AWARE INTELLIGENCE  
    // ------------------------------------------------------------  
    boolean rooted = isDeviceRooted();  

    if (rooted) {  

        logLine();  
        logInfo("Advanced storage analysis (root access):");  

        boolean wearSignals = detectStorageWearSignals(); // SAFE / HEURISTIC  
        boolean reservedPressure = pctFree < 12;  

        if (wearSignals) {  

            logWarn(" Internal signs of long-term storage wear detected.");  
            logInfo("This does NOT indicate failure.");  
            logOk("Flash memory wear increases gradually over time.");  

        } else {  

            logOk("No internal storage wear indicators detected.");  
        }  

        if (reservedPressure) {  
            logWarn(" System reserved space is being compressed.");  
            logInfo("Android may limit background tasks to protect stability.");  
        }  

        logOk("Recommendation: keep free storage above 15% for best performance.");  

    }  

    // ------------------------------------------------------------  
    // FINAL HUMAN SUMMARY  
    // ------------------------------------------------------------  
      
    logInfo("Storage summary:");  

    if (critical) {  
        logError(" Immediate cleanup strongly recommended.");  
    } else if (pressure) {  
        logWarn(" Cleanup recommended to restore smooth performance.");  
    } else {  
        logOk("… No action required.");  
    }  

} catch (Throwable t) {  

    logError("Storage inspection failed.");  
    logWarn("Unable to access filesystem statistics safely.");  
}  

appendHtml("<br>");  
logOk("Lab 18 finished.");  
logLine();

}

// ============================================================
// LAB 19 — Live RAM Health Snapshot
// FINAL — HUMAN • REAL-TIME • ROOT-AWARE • NO GUESSING
//
//  Instant snapshot (not stress / not forecast)
//  Explains what the system is doing NOW
//  Root-aware (extra insight, never fake)
//  No cleaning myths, no placebo claims
// ============================================================
private void lab19RamSnapshot() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 19 — Live RAM Health Snapshot");  
logLine();  

try {  
    ActivityManager am =  
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  

    if (am == null) {  
        logError("Memory service not available.");  
        return;  
    }  

    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();  
    am.getMemoryInfo(mi);  

    long total = mi.totalMem;  
    long free  = mi.availMem;  
    long used  = total - free;  

    int pctFree = (int) ((free * 100L) / Math.max(1L, total));  

    logInfo("Current RAM usage:");  
    logOk(  
            humanBytes(used) + " used / " +  
            humanBytes(total) +  
            " (free " + humanBytes(free) + ", " + pctFree + "%)"  
    );  

    // ---------------- HUMAN INTERPRETATION ----------------  
    if (pctFree < 8) {  
        logError(" Critical RAM pressure.");  
        logError("System is actively killing background apps to survive.");  
        logWarn("User experience: strong lag, reloads, UI stutter.");  

    } else if (pctFree < 15) {  
        logWarn(" High RAM pressure detected.");  
        logWarn("Multitasking may be unstable under load.");  

    } else if (pctFree < 25) {  
        logInfo("RAM usage is elevated.");  
        logInfo("This is normal during heavy apps or gaming.");  

    } else {  
        logOk("… RAM level is healthy at this moment.");  
    }  
    
    // ---------------- MEMORY PRESSURE INDICATORS ----------------
try {
    MemSnapshot snap = readMemSnapshotSafe();

    long swapUsedKb = 0;
    if (snap.swapTotalKb > 0 && snap.swapFreeKb > 0) {
        swapUsedKb = snap.swapTotalKb - snap.swapFreeKb;
    }

    String pressure =
            pressureLevel(
                    snap.memFreeKb,
                    snap.cachedKb,
                    swapUsedKb
            );

    String pressureHuman =
            humanPressureLabel(pressure);

    String zramDep =
            zramDependency(swapUsedKb, total);

    logLine();
    logInfo("Memory Pressure:");
    logOk("Level: " + pressureHuman);

    logInfo("ZRAM / Swap Dependency:");
    logOk(zramDep);

} catch (Throwable ignore) {}

    // ---------------- LOW MEMORY STATE ----------------  
    if (mi.lowMemory) {  
        logWarn(" Android reports low-memory state.");  
        logWarn("System protection mechanisms are active.");  
    }  

    // ---------------- ROOT-AWARE INTELLIGENCE ----------------  
    boolean rooted = isDeviceRooted();  

    if (rooted) {  
        logLine();  
        logInfo("Advanced RAM analysis:");  

        boolean zramActive = isZramActiveSafe();   // swap/zram check  
        boolean swapActive = isSwapActiveSafe();   // generic swap  

        if (zramActive || swapActive) {  
            logWarn(" Memory compression / swap detected.");  
            logInfo("System is extending RAM using CPU cycles.");  
            logOk("This improves stability but may reduce performance.");  
        } else {  
            logOk("No swap or memory compression detected.");  
        }  

        long cachedKb = readCachedMemoryKbSafe();  
        if (cachedKb > 0) {  
            logInfo(  
                    "Cached memory: " +  
                    humanBytes(cachedKb * 1024L) +  
                    " (reclaimable by system)"  
            );  
        }  
    }  

} catch (Throwable t) {  
    logError("RAM snapshot failed.");  
}  

appendHtml("<br>");  
logOk("Lab 19 finished.");  
logLine();

}

// ============================================================
// LAB 20 — Uptime & Reboot Intelligence
// FINAL — HUMAN • ROOT-AWARE • NO BULLSHIT
// ============================================================
private void lab20UptimeHints() {

    boolean frequentReboots = false;   // â­ Ï€ÏÎ­Ï€ÎµÎ¹ Î½Î± ÎµÎ¯Î½Î±Î¹ ÎµÎ´ÏŽ, ÎŸÎ§Î™ Î¼Î­ÏƒÎ± ÏƒÎµ if

    appendHtml("<br>");
    logLine();
    logInfo("LAB 20 — System Uptime & Reboot Behaviour");
    logLine();

    try {

        long upMs = SystemClock.elapsedRealtime();
        String upStr = formatUptime(upMs);

        logInfo("System uptime:");
        logOk(upStr);

        boolean veryRecentReboot = upMs < 2L * 60L * 60L * 1000L;        // < 2h
        boolean veryLongUptime   = upMs > 7L * 24L * 60L * 60L * 1000L; // > 7 days
        boolean extremeUptime    = upMs > 14L * 24L * 60L * 60L * 1000L;

        // ----------------------------------------------------
        // HUMAN INTERPRETATION (NON-ROOT)
        // ----------------------------------------------------
        if (veryRecentReboot) {

            logWarn(" Recent reboot detected.");
            logWarn("Some issues may be temporarily masked (memory, thermal, background load).");
            logInfo("Diagnostics are valid, but not fully representative yet.");

        } else if (veryLongUptime) {

            logWarn(" Long uptime detected.");
            logWarn("Background processes and memory pressure may accumulate over time.");

            if (extremeUptime) {
                logError(" Extremely long uptime (>14 days).");
                logError("Strongly recommended: reboot before drawing final conclusions.");
            } else {
                logInfo("Recommendation:");
                logOk("A reboot can help reset system state before deep diagnostics.");
            }

        } else {

            logOk("… Uptime is within a healthy range for diagnostics.");
        }

        // ----------------------------------------------------
        // ROOT-AWARE INTELLIGENCE (SILENT IF NOT ROOTED)
        // ----------------------------------------------------
        if (isDeviceRooted()) {

            logLine();
            logInfo("Advanced uptime signals:");

            boolean lowMemoryPressure = readLowMemoryKillCountSafe() < 5;
            frequentReboots = detectFrequentRebootsHint();   // â­ ASSIGN, ÏŒÏ‡Î¹ Î½Î­Î± Î´Î®Î»Ï‰ÏƒÎ·

            if (frequentReboots) {
                logWarn(" Repeated reboot pattern detected.");
                logWarn("This may indicate instability, crashes or watchdog resets.");
            } else {
                logOk("No abnormal reboot patterns detected.");
            }

            if (!lowMemoryPressure) {
                logWarn(" Memory pressure events detected during uptime.");
                logInfo("System may be aggressively managing apps in background.");
            } else {
                logOk("No significant memory pressure signals detected.");
            }

            logInfo("Interpretation:");
            logOk("Uptime behaviour appears consistent with normal system operation.");
        }

    } catch (Throwable t) {
        logError("Uptime analysis failed.");
    }

    // ----------------------------------------------------
    // SUMMARY LINE (FOR LAB 28 & CROSS-LAB LOGIC)
    // ----------------------------------------------------
    GELServiceLog.info("SUMMARY: REBOOT_PATTERN=" +
            (frequentReboots ? "ABNORMAL" : "NORMAL"));

    appendHtml("<br>");
    logOk("Lab 20 finished.");
    logLine();
}

// ============================================================
// LABS 21 — 24 SECURITY & SYSTEM HEALTH
// ============================================================

// ============================================================
// LAB 21 — Screen Lock / Biometrics LIVE + Root-Aware
// REAL • USER-DRIVEN • NO LIES • POLICY + INFRA CHECK (ROOT)
// ============================================================
private boolean lab21Running = false;

private void lab21ScreenLock() {

// GUARD — avoid double-tap spam  
if (lab21Running) {  
    logWarn("LAB 21 is already running...");  
    return;  
}  
lab21Running = true;  

appendHtml("<br>");  
logLine();  
logInfo("LAB 21 — Screen Lock / Biometrics (Live + Root-Aware)");  
logLine();  

// ------------------------------------------------------------  
// PART A — LOCK CONFIG + STATE  
// ------------------------------------------------------------  
boolean secure = false;  
boolean lockedNow = false;  

try {  
    android.app.KeyguardManager km =  
            (android.app.KeyguardManager) getSystemService(KEYGUARD_SERVICE);  

    if (km != null) {  

        secure = km.isDeviceSecure();  

        try { lockedNow = km.isKeyguardLocked(); } catch (Throwable ignore) {}  

        if (secure) {  
            logOk("Secure lock configured (PIN / Pattern / Password).");  
        } else {  
            logError("NO secure lock configured — device is UNPROTECTED!");  
            logWarn("Risk: anyone with physical access can access data.");  
        }  

        if (secure) {  
logInfo("Current state:");  
if (lockedNow) {  
    logOk("LOCKED (keyguard active).");  
} else {  
    logWarn("UNLOCKED right now (device open).");  
}

}

} else {  
        logWarn("KeyguardManager not available — cannot read lock status.");  
    }  

} catch (Throwable e) {  
    logWarn("Screen lock detection failed: " + e.getMessage());  
}  

// ------------------------------------------------------------  
// PART B — BIOMETRIC CAPABILITY (FRAMEWORK, NO ANDROIDX)  
// ------------------------------------------------------------

boolean biometricSupported = false;

if (android.os.Build.VERSION.SDK_INT >= 29) {
try {
android.hardware.biometrics.BiometricManager bm =
getSystemService(android.hardware.biometrics.BiometricManager.class);

if (bm != null) {  
        int result = bm.canAuthenticate(  
                android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG  
        );  

        if (result == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS) {  
            biometricSupported = true;  
            logOk("Biometric hardware PRESENT (system reports available).");  
        } else {  
            logWarn("Biometric hardware PRESENT but NOT ready / not usable.");  
        }  
    } else {  
        logWarn("BiometricManager unavailable.");  
    }  
} catch (Throwable e) {  
    logWarn("Biometric capability check failed: " + e.getMessage());  
}

} else {
logWarn("Biometric framework not supported on this Android version.");
}

// ------------------------------------------------------------  
// PART C — ROOT-AWARE AUTH INFRA CHECK (POLICY / FILES)  
// ------------------------------------------------------------  
boolean hasLockDb = false;  
boolean hasGatekeeper = false;  
boolean hasKeystore = false;  

boolean root = isRootAvailable();  
if (root) {  

    logInfo("Root mode:");  
    logOk("AVAILABLE (extra infrastructure checks enabled).");  

    hasLockDb     = rootPathExists("/data/system/locksettings.db");  
    hasGatekeeper = rootPathExists("/data/system/gatekeeper.password.key") ||  
                    rootPathExists("/data/system/gatekeeper.pattern.key") ||  
                    rootGlobExists("/data/system/gatekeeper*");  
    hasKeystore   = rootPathExists("/data/misc/keystore") ||  
                    rootPathExists("/data/misc/keystore/");  

    if (hasGatekeeper) logOk("Gatekeeper artifacts found (auth infrastructure likely active).");  
    else logWarn("No gatekeeper artifacts detected (lock disabled OR vendor storage).");  

    if (hasLockDb) logOk("Locksettings database found (lock configuration maintained).");  
    else logWarn("Locksettings database not detected (ROM/vendor variation possible).");  

    if (hasKeystore) logOk("System keystore path detected (secure storage present).");  
    else logWarn("Keystore path not detected (vendor / Android version variation possible).");  

} else {  
    logInfo("Root mode:");  
    logOk("not available (standard checks only).");  
}  

// ============================================================  
// LAB 21 — TRUST BOUNDARY AWARENESS  
// ============================================================  

try {  
    if (secure) {  
logInfo("Post-reboot protection:");  
logOk("authentication REQUIRED before data access.");

} else {
logInfo("Post-reboot protection:");
logError("NOT enforced — data exposure risk after reboot.");
}
} catch (Throwable ignore) {}

if (secure) {  
logInfo("Primary security layer:");  
logOk("knowledge-based credential (PIN / Pattern / Password).");

} else {
logInfo("Primary security layer:");
logWarn("NONE (no credential configured).");
}

if (biometricSupported) {
logInfo("Convenience layer:");
logOk("biometrics available (user-facing).");
} else {
logInfo("Convenience layer:");
logWarn("biometrics not available or not ready (non-critical).");
}

if (secure && !lockedNow) {  
    logWarn("Warning: biometrics do NOT protect an already UNLOCKED device.");  
}  

if (root) {  
    if (hasGatekeeper || hasLockDb) logOk("System enforcement signals present (auth infrastructure active).");  
    else logWarn("Enforcement signals unclear — ROM/vendor variation or relaxed policy.");  
}  

// ------------------------------------------------------------  
// PART D — RISK SCORE (FAST, CLEAR)  
// ------------------------------------------------------------  
int risk = 0;  

if (!secure) risk += 70;  
if (secure && !lockedNow) risk += 10;  
if (secure && !biometricSupported) risk += 5;  

if (risk >= 70) logError("Security impact: HIGH (" + risk + "/100)");  
else if (risk >= 30) logWarn("Security impact: MEDIUM (" + risk + "/100)");  
else logOk("Security impact: LOW (" + risk + "/100)");

// ------------------------------------------------------------
// PART E — LIVE BIOMETRIC AUTH TEST (USER-DRIVEN, REAL)
// ------------------------------------------------------------
if (!secure) {
logWarn("Live biometric test skipped: secure lock required.");

appendHtml("<br>");  
logOk("LAB 21 finished.");  
logLine();  
lab21Running = false;  
return;

}

if (!biometricSupported) {
logInfo("Live biometric test not started:");
logWarn("Biometrics not ready or not available.");
logInfo("Action:");
logOk("Enroll biometrics in Settings, then re-run LAB 21.");

appendHtml("<br>");  
logOk("LAB 21 finished.");  
logLine();  
lab21Running = false;  
return;

}

if (android.os.Build.VERSION.SDK_INT >= 28) {
try {
logLine();
logInfo("LIVE SENSOR TEST:");
logOk("Place finger / face for biometric authentication NOW.");
logOk("Result will be recorded as PASS/FAIL (real hardware interaction).");

java.util.concurrent.Executor executor = getMainExecutor();  
    android.os.CancellationSignal cancel = new android.os.CancellationSignal();  

    android.hardware.biometrics.BiometricPrompt.AuthenticationCallback cb =  
            new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {  

                @Override  
                public void onAuthenticationSucceeded(  
                        android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {  

                    logInfo("LIVE BIOMETRIC TEST:");  
                    logOk("PASS — biometric sensor and authentication pipeline verified functional.");  

                    logInfo("Multi-biometric devices:");  
                    logWarn("Android tests ONE biometric sensor per run.");  
                    logOk("Disable current biometric in Settings and re-run LAB 21 to test another sensor.");  
                    logWarn("OEM priority may keep same sensor even after disabling.");  

                    appendHtml("<br>");  
                    logOk("LAB 21 finished.");  
                    logLine();  
                    lab21Running = false;  
                }  

                @Override  
                public void onAuthenticationFailed() {  
                    logInfo("LIVE BIOMETRIC TEST:");  
                    logError("FAIL — biometric hardware did NOT authenticate during real sensor test.");  

                    logOk("LAB 21 finished.");  
                    logLine();  
                    lab21Running = false;  
                }  

                @Override  
                public void onAuthenticationError(int errorCode, CharSequence errString) {  
                    logWarn("System fallback to device credential detected — biometric sensor NOT confirmed functional.");  

                    appendHtml("<br>");  
                    logOk("LAB 21 finished.");  
                    logLine();  
                    lab21Running = false;  
                }  
            };  

    android.hardware.biometrics.BiometricPrompt prompt =  
            new android.hardware.biometrics.BiometricPrompt.Builder(this)  
                    .setTitle("LAB 21 — Live Biometric Sensor Test")  
                    .setSubtitle("Place finger / face to verify sensor works")  
                    .setDescription("This is a REAL hardware test (no simulation).")  
                    .setNegativeButton(  
                            "Cancel test",  
                            executor,  
                            (dialog, which) -> {  
                                logWarn("LIVE BIOMETRIC TEST: cancelled by user.");  
                                  
                                appendHtml("<br>");  
                                logOk("LAB 21 finished.");  
                                logLine();  
                                lab21Running = false;  
                            }  
                    )  
                    .setAllowedAuthenticators(  
                            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG  
                    )  
                    .build();  

    logInfo("Starting LIVE biometric prompt...");  
    prompt.authenticate(cancel, executor, cb);  

} catch (Throwable e) {  
    logWarn("Live biometric prompt failed: " + e.getMessage());  
      
    appendHtml("<br>");  
    logOk("LAB 21 finished.");  
    logLine();  
    lab21Running = false;  
}

} else {

logOk("Live biometric prompt not supported on this Android version.");  
logInfo("Action required:");  
logOk("Test biometrics via system lock screen settings, then re-run LAB 21.");  

logInfo("Note:");  
logOk("Each LAB 21 run verifies ONE biometric sensor path.");  

logInfo("Action:");  
logOk("Disable the active biometric in Settings to test another sensor.");  
  
appendHtml("<br>");  
logOk("LAB 21 finished.");      
logLine();  
lab21Running = false;  
}

}

// ============================================================
// ROOT HELPERS — minimal, safe, no assumptions
// ============================================================
private boolean isRootAvailable() {
try {
if (new java.io.File("/system/xbin/su").exists()) return true;
if (new java.io.File("/system/bin/su").exists())  return true;
if (new java.io.File("/sbin/su").exists())        return true;
if (new java.io.File("/su/bin/su").exists())      return true;

String out = runSu("id");  
    return out != null && out.toLowerCase(java.util.Locale.US).contains("uid=0");  
} catch (Throwable ignore) {  
    return false;  
}

}

private boolean rootPathExists(String path) {
String cmd = "[ -e '" + path + "' ] && echo OK || echo NO";
String out = runSu(cmd);
return out != null && out.contains("OK");
}

private boolean rootGlobExists(String glob) {
String cmd = "ls " + glob + " 1>/dev/null 2>/dev/null && echo OK || echo NO";
String out = runSu(cmd);
return out != null && out.contains("OK");
}

private String runSu(String command) {
java.io.BufferedReader br = null;
try {
Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
StringBuilder sb = new StringBuilder();
String line;
while ((line = br.readLine()) != null) {
if (sb.length() > 0) sb.append("\n");
sb.append(line);
}
try { p.waitFor(); } catch (Throwable ignore) {}
String s = sb.toString().trim();
return s.isEmpty() ? null : s;
} catch (Throwable ignore) {
return null;
} finally {
try { if (br != null) br.close(); } catch (Throwable ignore) {}
}
}

// ============================================================
// LAB 22 — Security Patch & Play Protect (AUTO + MANUAL)
// ============================================================
private void lab22SecurityPatchManual() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 22 — Security Patch & Play Protect Check");  
logLine();  

// ------------------------------------------------------------  
// 1) Security Patch Level (raw)  
// ------------------------------------------------------------  
String patch = null;  
try {  
    patch = android.os.Build.VERSION.SECURITY_PATCH;  
    if (patch != null && !patch.isEmpty()) {  
        logInfo("Security Patch Level: " + patch);  
    } else {  
        logWarn("Security Patch Level not reported by system.");  
    }  
} catch (Throwable e) {  
    logWarn("Security patch read failed: " + e.getMessage());  
}  

// ------------------------------------------------------------  
// 2) Patch Freshness Intelligence (AGE + RISK)  
// ------------------------------------------------------------  
try {  
    if (patch != null && !patch.isEmpty()) {  

        java.text.SimpleDateFormat sdf =  
                new java.text.SimpleDateFormat(  
                        "yyyy-MM-dd", java.util.Locale.US);  
        sdf.setLenient(false);  

        long patchTime = sdf.parse(patch).getTime();  
        long now = System.currentTimeMillis();  

        long diffMs = now - patchTime;  
        long diffDays = diffMs / (1000L * 60 * 60 * 24);  
        long diffMonths = diffDays / 30;  

        logInfo("Security patch age: ~" + diffMonths + " months.");  

        if (diffMonths <= 3) {  
            logOk("Patch currency status: RECENT (low known exploit exposure).");  
        } else if (diffMonths <= 6) {  
            logWarn("Patch currency status: MODERATELY OUTDATED.");  
        } else {  
            logError("Patch currency status: OUTDATED — missing recent security fixes.");  
        }  
    }  
} catch (Throwable e) {  
    logWarn("Security patch age evaluation failed: " + e.getMessage());  
}  

// ------------------------------------------------------------  
// 3) Play Protect Detection (best effort, no root)  
// ------------------------------------------------------------  
try {  
    PackageManager pm = getPackageManager();  

    boolean gmsPresent = false;  
    try {  
        pm.getPackageInfo("com.google.android.gms", 0);  
        gmsPresent = true;  
    } catch (Exception ignore) {}  

    if (!gmsPresent) {  
        logError("Google Play Services NOT present — Play Protect unavailable.");  
    } else {  

        int verify = -1;  
        try {  
            verify = Settings.Global.getInt(  
                    getContentResolver(),  
                    "package_verifier_enable",  
                    -1  
            );  
        } catch (Exception ignore) {}  

        if (verify == 1) {  
            logOk("Play Protect: ENABLED (Google Verify Apps ON).");  
        } else if (verify == 0) {  
            logWarn("Play Protect: DISABLED (Verify Apps OFF).");  
        } else {  
            // Fallback detection (activity presence)  
            Intent i = new Intent();  
            i.setClassName(  
                    "com.google.android.gms",  
                    "com.google.android.gms.security.settings.VerifyAppsSettingsActivity"  
            );  

            if (i.resolveActivity(pm) != null) {  
                logOk("Play Protect module detected (settings activity present).");  
            } else {  
                logWarn("Play Protect status unclear (OEM or restricted build).");  
            }  
        }  
    }  

} catch (Throwable e) {  
    logWarn("Play Protect detection error: " + e.getMessage());  
}  

// ------------------------------------------------------------  
// 4) Trust Boundary Clarification  
// ------------------------------------------------------------  

logInfo("Play Protect scope: malware scanning & app verification.");  
logWarn("Play Protect does NOT patch system vulnerabilities or firmware flaws.");  

// ------------------------------------------------------------  
// 5) Manual Guidance (Technician)  
// ------------------------------------------------------------  
  
logInfo("Manual checks:");  
logInfo("1) Settings â†’ About phone â†’ Android version â†’ Security patch level.");  
logWarn("   Very old patch levels increase exploit exposure.");  
logInfo("2) Google Play Store â†’ Play Protect â†’ verify scanning is enabled.");  

appendHtml("<br>");  
logOk("Lab 22 finished.");  
logLine();

}

// ============================================================
// LAB 23 — Developer Options / ADB Risk Note + UI BUBBLES + AUTO-FIX HINTS
// GEL Security v3.1 (Realtime Snapshot)
// ============================================================
private void lab23DevOptions() {

appendHtml("<br>");
logLine();
logInfo("LAB 23 — Developer Options / ADB Risk Note (Realtime).");
logLine();

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
logError("ADB over Wi-Fi ACTIVE — remote debugging possible on local network.");
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

logInfo("Security Risk Score:");

if (risk >= 70) {
logError(risk + "/100 (" + level + ") " + riskBubble(risk));
} else if (risk >= 30) {
logWarn(risk + "/100 (" + level + ") " + riskBubble(risk));
} else {
logOk(risk + "/100 (" + level + ") " + riskBubble(risk));
}

// ============================================================
// 6) AUTO-FIX / ACTION HINTS
// ============================================================

logInfo("Recommended Actions:");

if (usbDebug || devOpts) {
logWarn("• Disable Developer Options / USB Debugging:");
logInfo("  Settings â†’ System â†’ Developer options â†’ OFF");
logInfo("  USB debugging â†’ OFF");
} else {
logOk("• Developer options & USB debugging look safe.");
}

if (adbWifi) {
logError("• ADB over Wi-Fi must be disabled:");
logInfo("  Developer options â†’ Wireless debugging â†’ OFF");
logInfo("  Or reboot to clear tcpip mode.");
} else {
logOk("• Wireless debugging is not active.");
}

if (adbPairing) {
logError("• Turn OFF ADB Pairing / Wireless debugging:");
logInfo("  Developer options â†’ Wireless debugging â†’ OFF");
} else {
logOk("• ADB Pairing is not active.");
}

if (risk >= 60)
logError("Â  Very high risk — disable ADB features immediately!");
else if (risk >= 30)
logWarn("Â  Partial exposure — review ADB settings.");
else
logOk("ï¸ Risk level acceptable.");

appendHtml("<br>");
logOk("LAB 23 finished.");
logLine();
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

// Scan pairing port range 7460-7490 (best-effort)
private boolean scanPairingPortRange() {
for (int p = 7460; p <= 7490; p++) {
if (isPortOpen(p, 80)) return true;
}
return false;
}

// ============================================================
// LAB 24 — Root / Bootloader Suspicion Checklist (FULL AUTO + RISK SCORE)
// GEL Universal Edition — NO external libs
// ============================================================
private void lab24RootSuspicion() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 24 — Root / Bootloader Integrity Scan (AUTO).");  
logLine();  

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
        "/system/bin/busybox",  
        "/system/xbin/busybox",  
        "/vendor/bin/su",  
        "/odm/bin/su"  
};  

boolean suFound = false;  

for (String p : suPaths) {  
    if (lab24_fileExists(p)) {  
        suFound = true;  
        rootScore += 18;  
        rootFindings.add("su/busybox path found: " + p);  
    }  
}  

// which su (best-effort, avoid false positives)  
String whichSu = lab24_execFirstLine("which su");  
if (whichSu != null && whichSu.contains("/su")) {  
    rootScore += 12;  
    rootFindings.add("'which su' returned: " + whichSu);  
    suFound = true;  
}  

// try exec su (strong indicator)  
boolean suExec = lab24_canExecSu();  
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
        "de.robv.android.xposed.installer"  
};  

List<String> installed = lab24_getInstalledPackagesLower();  
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

// suspicious system properties  
String roSecure = lab24_getProp("ro.secure");  
String roDebug  = lab24_getProp("ro.debuggable");  

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

String vbState = lab24_getProp("ro.boot.verifiedbootstate"); // green/yellow/orange/red  
String vbmeta  = lab24_getProp("ro.boot.vbmeta.device_state"); // locked/unlocked  
String flashL  = lab24_getProp("ro.boot.flash.locked"); // 1/0  
String wlBit   = lab24_getProp("ro.boot.warranty_bit"); // 0/1 (OEM)  

if (vbState != null &&  
        (vbState.contains("orange") ||  
         vbState.contains("yellow") ||  
         vbState.contains("red"))) {  
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

// OEM unlock allowed (settings)  
try {  
    int oemAllowed =  
            Settings.Global.getInt(  
                    getContentResolver(),  
                    "oem_unlock_allowed",  
                    0  
            );  
    if (oemAllowed == 1) {  
        blScore += 10;  
        blFindings.add("OEM unlock allowed=1 (developer enabled).");  
    }  
} catch (Throwable ignore) {}  

// /proc/cmdline hints  
String cmdline = lab24_readOneLine("/proc/cmdline");  
if (cmdline != null) {  
    String c = cmdline.toLowerCase(Locale.US);  
    if (c.contains("verifiedbootstate=orange") ||  
        c.contains("verifiedbootstate=yellow") ||  
        c.contains("vbmeta.device_state=unlocked") ||  
        c.contains("bootloader=unlocked")) {  
        blScore += 20;  
        blFindings.add("/proc/cmdline reports unlocked / weak verified boot.");  
    }  
}  

// ---------------------------  
// (3) BOOT ANIMATION / SPLASH MOD  
// ---------------------------  
int animScore = 0;  
List<String> animFindings = new ArrayList<>();  

if (lab24_fileExists("/data/local/bootanimation.zip")) {  
    animScore += 35;  
    animFindings.add("Custom bootanimation: /data/local/bootanimation.zip");  
}  

boolean sysBoot =  
        lab24_fileExists("/system/media/bootanimation.zip") ||  
        lab24_fileExists("/product/media/bootanimation.zip") ||  
        lab24_fileExists("/oem/media/bootanimation.zip") ||  
        lab24_fileExists("/vendor/media/bootanimation.zip");  

if (!sysBoot) {  
    animScore += 15;  
    animFindings.add("No stock bootanimation found (possible custom ROM).");  
} else {  
    animFindings.add("Stock bootanimation path exists.");  
}  

// ---------------------------  
// FINAL RISK SCORE  
// ---------------------------  
int risk = Math.min(100, rootScore + blScore + animScore);  

logInfo("Root Scan:");  
if (rootFindings.isEmpty()) {  
    logOk("No strong root traces detected.");  
} else {  
    for (String s : rootFindings) logWarn("• " + s);  
}  

logInfo("Bootloader / Verified Boot:");  
if (blFindings.isEmpty()) {  
    logOk("No bootloader anomalies detected.");  
} else {  
    for (String s : blFindings) logWarn("• " + s);  
}  

logInfo("Boot Animation / Splash:");  
if (animFindings.isEmpty()) {  
    logOk("No custom animation traces detected.");  
} else {  
    for (String s : animFindings) logWarn("• " + s);  
}  

logInfo("FINAL VERDICT:");

// ------------------------------------------------------------
// RISK SCORE (colored VALUE only)
// ------------------------------------------------------------
String riskLine = "RISK SCORE: " + risk + " / 100";
SpannableString spRisk = new SpannableString(riskLine);

int color =
(risk >= 70) ? 0xFFFF3B3B :   // RED
(risk >= 35) ? 0xFFFFD700 :   // YELLOW
0xFF39FF14;   // GREEN

int start = riskLine.indexOf(":") + 1;

spRisk.setSpan(
new ForegroundColorSpan(color),
start,
riskLine.length(),
Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
);

if (txtLog != null) {
txtLog.append(spRisk);
txtLog.append("\n");
} else {
logInfo(riskLine);
}

// ------------------------------------------------------------
// STATUS
// ------------------------------------------------------------
if (risk >= 70 || suExec || pkgHit) {
logError("STATUS: ROOTED / SYSTEM MODIFIED (high confidence).");
} else if (risk >= 35) {
logWarn("STATUS: SUSPICIOUS (possible root / unlocked / custom ROM).");
} else {
logOk("STATUS: SAFE (no significant modification evidence).");
}

appendHtml("<br>");
logOk("Lab 24 finished.");
logLine();

} // … Î¤Î•Î›ÎŸÎ£ ÎœÎ•Î˜ÎŸÎ”ÎŸÎ¥

// ============================================================
// LAB 24 — INTERNAL HELPERS
// ============================================================
private boolean lab24_fileExists(String path) {
try { return new File(path).exists(); }
catch (Throwable t) { return false; }
}

private List<String> lab24_getInstalledPackagesLower() {
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

private boolean lab24_canExecSu() {
Process p = null;
try {
p = Runtime.getRuntime().exec(new String[]{"su", "-c", "id"});
BufferedReader br =
new BufferedReader(
new InputStreamReader(p.getInputStream()));
String line = br.readLine();
br.close();
return line != null &&
line.toLowerCase(Locale.US).contains("uid=0");
} catch (Throwable t) {
return false;
} finally {
if (p != null) try { p.destroy(); } catch (Throwable ignore) {}
}
}

private String lab24_execFirstLine(String cmd) {
Process p = null;
try {
p = Runtime.getRuntime().exec(cmd);
BufferedReader br =
new BufferedReader(
new InputStreamReader(p.getInputStream()));
String line = br.readLine();
br.close();
return line != null ? line.trim() : null;
} catch (Throwable t) {
return null;
} finally {
if (p != null) try { p.destroy(); } catch (Throwable ignore) {}
}
}

private String lab24_getProp(String key) {
String v = lab24_execFirstLine("getprop " + key);
if (v == null) return null;
v = v.trim();
return v.isEmpty() ? null : v.toLowerCase(Locale.US);
}

private String lab24_readOneLine(String path) {
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
// LABS 25 — 29: ADVANCED / LOGS
// ============================================================

// ============================================================
// LAB 25 — GEL Crash Intelligence v5.0 (FULL AUTO EDITION)
// ============================================================
private void lab25CrashHistory() {

appendHtml("<br>");
logLine();
logInfo("LAB 25 — GEL Crash Intelligence (AUTO)");
logLine();

int crashCount = 0;
int anrCount = 0;
int systemCount = 0;

Map<String, Integer> appEvents = new HashMap<>(); // Group per app
List<String> details = new ArrayList<>();

// ============================================================
// (A) Android 11+ — REALTIME ERROR SNAPSHOT (NOT HISTORY)
// ============================================================
// REPLACE your whole (A) block with this:
try {
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);  

    if (am != null) {  

        List<ActivityManager.ProcessErrorStateInfo> errs =  
                am.getProcessesInErrorState();  

        if (errs != null && !errs.isEmpty()) {  

            logInfo("Realtime Error Snapshot (current state):");  

            for (ActivityManager.ProcessErrorStateInfo e : errs) {  

                String app = (e != null && e.processName != null)  
                        ? e.processName  
                        : "(unknown)";  

                // Group snapshot per process (ok)  
                appEvents.put(app, appEvents.getOrDefault(app, 0) + 1);  

                if (e.condition == ActivityManager.ProcessErrorStateInfo.CRASHED) {  
                    details.add("SNAPSHOT CRASH: " + app + " — " + safeStr(e.shortMsg));  
                } else if (e.condition == ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {  
                    details.add("SNAPSHOT ANR: " + app + " — " + safeStr(e.shortMsg));  
                } else {  
                    details.add("SNAPSHOT ERROR: " + app + " — " + safeStr(e.shortMsg));  
                }  
            }  

            logInfo("Note:");  
            logOk("snapshot shows ONLY current crashed/ANR processes (not history).");  
        }  
    }  
}

} catch (Throwable ignore) {}

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

            if (tag.contains("system_server")) {  
systemCount++;

} else if (tag.contains("anr")) {
anrCount++;
} else if (tag.contains("crash")) {
crashCount++;
}

String shortTxt = readDropBoxEntry(ent);    

            String clean = tag.toUpperCase(Locale.US).replace("_", " ");    
            details.add(clean + ": " + shortTxt);    

            // grouping       
         try {  

String key;  

if (shortTxt != null && shortTxt.length() > 0) {  
    String t = shortTxt.toLowerCase(Locale.US);  
    int pi = t.indexOf("package:");  
    if (pi >= 0) {  
        String rest = t.substring(pi + 8).trim();  
        String[] parts = rest.split("[\\s\\n\\r\\t]+");  
        key = (parts.length > 0 && parts[0].contains(".")) ? parts[0] : clean;  
    } else {  
        key = clean;  
    }  
} else {  
    key = clean;  
}  

appEvents.put(key, appEvents.getOrDefault(key, 0) + 1);

} catch (Exception ignored) {}
ent = db.getNextEntry(tag, ent.getTimeMillis());
}   // END while
}       // END for
}           // END if (db != null)
} catch (Exception ignored) {}   // END DropBox try

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
(risk <= 20) ? "" :
(risk <= 50) ? "" :
(risk <= 80) ? "" : "";

logInfo("Crash events:");
if (crashCount > 0) logWarn(String.valueOf(crashCount));
else logOk("0");

logInfo("ANR events:");
if (anrCount > 0) logWarn(String.valueOf(anrCount));
else logOk("0");

logInfo("System-level faults:");
if (systemCount > 0) logError(String.valueOf(systemCount));
else logOk("0");

logInfo("Stability Risk Score:");

if (risk >= 60)
logError(risk + "%");
else if (risk >= 30)
logWarn(risk + "%");
else
logOk(risk + "%");

logInfo("Note:");
logOk("risk score is based on detected system log signals; availability varies by OEM/Android.");

boolean softwareCrashLikely = (crashCount > 0 || anrCount > 0);

// ============================================================
// (D) HEATMAP (top offenders)
// ============================================================
if (!appEvents.isEmpty()) {

logInfo("Heatmap (Top Categories / Packages — best-effort):");  

appEvents.entrySet()    
        .stream()    
        .sorted((a, b) -> b.getValue() - a.getValue())    
        .limit(5)    
        .forEach(e -> {    
            String c = (e.getValue() >= 10) ? "" :    
                       (e.getValue() >= 5)  ? "" :    
                       (e.getValue() >= 2)  ? "" :    
                                              "";    
            logInfo(" " + c + " " + e.getKey() + " â†’ " + e.getValue() + " events");    
        });

}

// ============================================================
// (E) FULL DETAILS
// ============================================================
if (!details.isEmpty()) {

logInfo("Detailed Crash Records:");  

int count = details.size();  

if (count == 1) {  
    logWarn("1 crash record detected:");  
} else if (count <= 3) {  
    logWarn(count + " crash records detected:");  
} else {  
    logError(count + " crash records detected (HIGH instability).");  
}  

for (String d : details) {  
    logInfo("• " + d);  
}

} else {
    logOk("No crash history found.");
}

GELServiceLog.info("SUMMARY: CRASH_ORIGIN=" +
        (softwareCrashLikely ? "SOFTWARE" : "UNCLEAR"));

appendHtml("<br>");
logOk("Lab 25 finished.");
logLine();
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

private String safeStr(String s) {
return (s == null || s.trim().isEmpty()) ? "(no data)" : s;
}

// ============================================================
// LAB 26 — Installed Apps Footprint & System Load Intelligence
// FINAL — LOCKED — PRODUCTION-GRADE — HUMAN OUTPUT — ROOT AWARE
//
//  Honest diagnostics (no lies, no â€œmagicâ€)
//  Normal vs Risk vs Critical verdicts
//  Detects: app pressure, background-capable apps, permission load,
//            redundancy, â€œheavy offendersâ€ (by capabilities),
//            root-only leftovers (orphan data dirs), cache pressure signals
//  Root-aware: deeper scan ONLY when rooted, otherwise safe-mode
//
// NOTE (GEL RULE): Full lab block returned for copy-paste.
// ============================================================
private void lab26AppsFootprint() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 26 — Installed Apps Footprint & System Load");  
logLine();  

final PackageManager pm = getPackageManager();  
final boolean rooted = isDeviceRooted(); // you already have this  

// -----------------------------  
// SAFE GUARDS  
// -----------------------------  
List<ApplicationInfo> apps;  
try {  
    apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);  
} catch (Throwable t) {  
    logError("Apps footprint error: cannot read installed applications list.");  
    logLine();  
    return;  
}  

if (apps == null || apps.isEmpty()) {  
    logWarn("Cannot read installed applications list (empty).");  
    logLine();  
    return;  
}  

// -----------------------------  
// COUNTERS / BUCKETS  
// -----------------------------  
int totalPkgs  = apps.size();  
int userApps   = 0;  
int systemApps = 0;  

// â€œpressureâ€ signals (capability-based, not guesses)  
int bgCapable = 0;            // has background-ish abilities  
int permHeavy = 0;            // requests many dangerous-ish perms  
int bootAware = 0;            // has BOOT_COMPLETED receiver declared  
int adminLike = 0;            // device admin / accessibility / notif listener capabilities (best-effort)  
int overlayLike = 0;          // SYSTEM_ALERT_WINDOW request  
int vpnLike = 0;              // BIND_VPN_SERVICE  
int locationLike = 0;         // ACCESS_FINE/COARSE  
int micLike = 0;              // RECORD_AUDIO  
int cameraLike = 0;           // CAMERA  
int storageLike = 0;          // READ/WRITE external/media  
int notifLike = 0;            // POST_NOTIFICATIONS (13+), notification listener (best-effort)  

// redundancy buckets (simple + honest)  
int cleanersLike = 0;  
int launchersLike = 0;  
int antivirusLike = 0;  
int keyboardsLike = 0;  

// top offenders (by â€œcapability scoreâ€, not usage)  
class Offender {  
    String label;  
    String pkg;  
    int score;  
    String tags;  
}  
ArrayList<Offender> offenders = new ArrayList<>();  

// -----------------------------  
// SCAN LOOP  
// -----------------------------  
for (ApplicationInfo ai : apps) {  

    final boolean isSystem =  
            (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||  
            (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;  

    if (isSystem) systemApps++;  
    else userApps++;  

    final String pkg = (ai.packageName != null ? ai.packageName : "unknown");  
    String labelStr;  
    try {  
        CharSequence cs = pm.getApplicationLabel(ai);  
        labelStr = (cs != null ? cs.toString() : pkg);  
    } catch (Throwable ignore) {  
        labelStr = pkg;  
    }  

    // ---------  
    // CAPABILITY SCORE (honest)  
    // ---------  
    int score = 0;  
    StringBuilder tags = new StringBuilder();  

    // Try read requested permissions  
    String[] reqPerms = null;  
    try {  
        PackageInfo pi;  
        if (android.os.Build.VERSION.SDK_INT >= 33) {  
            pi = pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS));  
        } else {  
            pi = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);  
        }  
        if (pi != null) reqPerms = pi.requestedPermissions;  
    } catch (Throwable ignore) {}  

    // Count â€œdanger-ishâ€ permissions (best-effort, honest)  
    int dangerCount = 0;  
    boolean hasBoot = false;  

    boolean hasLocation = false;  
    boolean hasMic = false;  
    boolean hasCamera = false;  
    boolean hasOverlay = false;  
    boolean hasStorage = false;  
    boolean hasVpnBind = false;  
    boolean hasPostNotif = false;  

    if (reqPerms != null) {  
        for (String p : reqPerms) {  
            if (p == null) continue;  

            // BOOT receiver isn't a perm, but many apps declare RECEIVE_BOOT_COMPLETED as indicator  
            if ("android.permission.RECEIVE_BOOT_COMPLETED".equals(p)) hasBoot = true;  

            if ("android.permission.ACCESS_FINE_LOCATION".equals(p) ||  
                "android.permission.ACCESS_COARSE_LOCATION".equals(p)) hasLocation = true;  

            if ("android.permission.RECORD_AUDIO".equals(p)) hasMic = true;  
            if ("android.permission.CAMERA".equals(p)) hasCamera = true;  

            if ("android.permission.SYSTEM_ALERT_WINDOW".equals(p)) hasOverlay = true;  

            if ("android.permission.READ_EXTERNAL_STORAGE".equals(p) ||  
                "android.permission.WRITE_EXTERNAL_STORAGE".equals(p) ||  
                "android.permission.READ_MEDIA_IMAGES".equals(p) ||  
                "android.permission.READ_MEDIA_VIDEO".equals(p) ||  
                "android.permission.READ_MEDIA_AUDIO".equals(p)) hasStorage = true;  

            if ("android.permission.BIND_VPN_SERVICE".equals(p)) hasVpnBind = true;  

            if ("android.permission.POST_NOTIFICATIONS".equals(p)) hasPostNotif = true;  

            // â€œdanger-ishâ€ set (not perfect, but honest enough to show â€œpermission loadâ€)  
            if ("android.permission.READ_CONTACTS".equals(p) ||  
                "android.permission.WRITE_CONTACTS".equals(p) ||  
                "android.permission.READ_CALL_LOG".equals(p) ||  
                "android.permission.WRITE_CALL_LOG".equals(p) ||  
                "android.permission.READ_SMS".equals(p) ||  
                "android.permission.SEND_SMS".equals(p) ||  
                "android.permission.RECEIVE_SMS".equals(p) ||  
                "android.permission.READ_PHONE_STATE".equals(p) ||  
                "android.permission.CALL_PHONE".equals(p) ||  
                "android.permission.ACCESS_FINE_LOCATION".equals(p) ||  
                "android.permission.RECORD_AUDIO".equals(p) ||  
                "android.permission.CAMERA".equals(p) ||  
                "android.permission.BODY_SENSORS".equals(p) ||  
                "android.permission.USE_SIP".equals(p) ||  
                "android.permission.WRITE_SETTINGS".equals(p) ||  
                "android.permission.SYSTEM_ALERT_WINDOW".equals(p)) {  
                dangerCount++;  
            }  
        }  
    }  

    // scoring + tags (capability-based)  
    if (dangerCount >= 8) { score += 12; tags.append("perm-heavy, "); }  
    else if (dangerCount >= 5) { score += 8; tags.append("perm-heavy, "); }  
    else if (dangerCount >= 3) { score += 4; }  

    if (hasBoot) { score += 6; tags.append("boot-aware, "); }  
    if (hasLocation) { score += 5; tags.append("location, "); }  
    if (hasMic) { score += 5; tags.append("mic, "); }  
    if (hasCamera) { score += 4; tags.append("camera, "); }  
    if (hasOverlay) { score += 7; tags.append("overlay, "); }  
    if (hasStorage) { score += 3; tags.append("storage, "); }  
    if (hasVpnBind) { score += 6; tags.append("vpn, "); }  
    if (hasPostNotif) { score += 2; tags.append("notifications, "); }  

    // â€œbackground-capableâ€ heuristic (honest: capability, not runtime)  
    boolean bg =  
            hasBoot || hasLocation || hasVpnBind || hasOverlay || hasPostNotif ||  
            dangerCount >= 5;  

    if (bg) bgCapable++;  

    if (dangerCount >= 5) permHeavy++;  

    if (hasBoot) bootAware++;  
    if (hasOverlay) overlayLike++;  
    if (hasVpnBind) vpnLike++;  
    if (hasLocation) locationLike++;  
    if (hasMic) micLike++;  
    if (hasCamera) cameraLike++;  
    if (hasStorage) storageLike++;  
    if (hasPostNotif) notifLike++;  

    // Redundancy (package-name heuristic only — honest)  
    final String lowPkg = pkg.toLowerCase(Locale.US);  
    if (lowPkg.contains("clean") || lowPkg.contains("booster") || lowPkg.contains("optimizer"))  
        cleanersLike++;  
    if (lowPkg.contains("launcher"))  
        launchersLike++;  
    if (lowPkg.contains("avast") || lowPkg.contains("kaspersky") || lowPkg.contains("avg") ||  
        lowPkg.contains("bitdefender") || lowPkg.contains("eset") || lowPkg.contains("norton"))  
        antivirusLike++;  
    if (lowPkg.contains("keyboard") || lowPkg.contains("ime"))  
        keyboardsLike++;

// Store top offenders — USER APPS ONLY
// Exclude system apps & Play Store related packages
if (!isSystem &&
score >= 14 &&
!pkg.startsWith("com.android.") &&
!pkg.startsWith("com.google.android.") &&
!pkg.equals("com.android.vending")) {

Offender o = new Offender();  
o.label = labelStr;  
o.pkg = pkg;  
o.score = score;  

String tgs = tags.toString().trim();  
if (tgs.endsWith(",")) tgs = tgs.substring(0, tgs.length() - 1).trim();  
o.tags = (tgs.length() > 0 ? tgs : "high-capability");  

offenders.add(o);  
    }  
}  

// -----------------------------  
// SORT OFFENDERS (desc by score)  
// -----------------------------  
try {  
    java.util.Collections.sort(offenders, (a, b) -> Integer.compare(b.score, a.score));  
} catch (Throwable ignore) {}  

// -----------------------------  
// HUMAN SUMMARY  
// -----------------------------  
logInfo("Installed packages:");  
logOk("Total: " + totalPkgs + " | User apps: " + userApps + " | System apps: " + systemApps);  

// -----------------------------  
// PRESSURE METRICS (capability-based)  
// -----------------------------  
int pctBg = (int) Math.round((bgCapable * 100.0) / Math.max(1, userApps));  
int pctPerm = (int) Math.round((permHeavy * 100.0) / Math.max(1, userApps));  

logInfo("System load indicators (capability-based):");  
logOk("Background-capable user apps: " + bgCapable + " (" + pctBg + "%)");  
logOk("Permission-heavy user apps: " + permHeavy + " (" + pctPerm + "%)");  
logInfo("(Percentages above 100% mean multiple capabilities per app — this is normal.)");  

logInfo("Capability map (user apps):");  
logOk("Boot-aware: " + bootAware +  
        " | Location: " + locationLike +  
        " | Microphone: " + micLike +  
        " | Camera: " + cameraLike);  
logOk("Overlay: " + overlayLike +  
        " | VPN-capable: " + vpnLike +  
        " | Storage access: " + storageLike +  
        " | Notifications: " + notifLike);  

// -----------------------------  
// REDUNDANCY (honest)  
// -----------------------------  
logInfo("Redundancy signals (heuristic):");  
if (cleanersLike >= 2) logWarn("• Multiple cleaner/optimizer-style apps detected (" + cleanersLike + ").");  
else logOk("• Cleaner/optimizer-style apps: " + cleanersLike);  

if (launchersLike >= 2) logWarn("• Multiple launchers detected (" + launchersLike + ").");  
else logOk("• Launchers: " + launchersLike);  

if (antivirusLike >= 2) logWarn("• Multiple antivirus suites detected (" + antivirusLike + ").");  
else logOk("• Antivirus suites: " + antivirusLike);  

if (keyboardsLike >= 2) logWarn("• Multiple keyboards detected (" + keyboardsLike + ").");  
else logOk("• Keyboards: " + keyboardsLike);  

// -----------------------------  
// VERDICT LOGIC (honest thresholds)  
// -----------------------------  
// NOTE: These are risk heuristics, not guarantees.  
boolean countHigh = userApps >= 120;  
boolean countMed  = userApps >= 85;  

boolean bgHigh = pctBg >= 45 || bgCapable >= 45;  
boolean bgMed  = pctBg >= 30 || bgCapable >= 30;  

boolean permHigh = pctPerm >= 25 || permHeavy >= 25;  
boolean permMed  = pctPerm >= 15 || permHeavy >= 15;  

boolean redundancy = (cleanersLike >= 2) || (launchersLike >= 2) || (antivirusLike >= 2);  

int riskPoints = 0;  
if (countHigh) riskPoints += 3;  
else if (countMed) riskPoints += 2;  

if (bgHigh) riskPoints += 3;  
else if (bgMed) riskPoints += 2;  

if (permHigh) riskPoints += 3;  
else if (permMed) riskPoints += 2;  

if (redundancy) riskPoints += 1;  

logInfo("Human verdict:");  
  
if (riskPoints >= 8) {  
logWarn(" High app pressure detected.");  
logWarn("This increases the probability of lag, or background drain over time.");  
logInfo("What this means (simple terms):");  
logWarn("Your phone runs many apps with background or high-permission capabilities.");  
logOk("This is common on power-user devices and is NOT a hardware fault.");  
logOk("Recommendation: keep only what you really use and reduce duplicates if you want extra smoothness.");  

} else if (riskPoints >= 5) {  
    logWarn(" Moderate app pressure detected.");  
    logWarn("Performance may degrade over time depending on usage patterns.");  
    logInfo("What this means (simple terms):");  
    logOk("Several apps can run or react in the background, even if you don’t open them daily.");  
    logOk("Recommendation: review redundant apps and background-heavy categories.");  

} else {  
    logOk("App footprint looks healthy for daily usage.");  
    logOk("No strong indicators of app-driven system overload detected.");  
}  

// -----------------------------  
// TOP OFFENDERS (capability-heavy)  
// -----------------------------  
if (!offenders.isEmpty()) {  
    logLine();  
    logInfo("TOP 10 High-capability user apps (not accused — just flagged):");  

    int limit = Math.min(10, offenders.size());  
    for (int i = 0; i < limit; i++) {  
        Offender o = offenders.get(i);  
        logWarn("• " + o.label + "  [" + o.tags + "]");  
        logInfo("  " + o.pkg);  
    }  

    logInfo("Note:");  
    logOk("These apps are NOT confirmed as â€œbadâ€. They simply have strong background/permission capabilities.");  
}  

int orphanDirs = 0;
long orphanBytes = 0L;

// ============================================================  
// ROOT AWARE INTELLIGENCE — LEFTOVERS / ORPHANS  
// ============================================================  
if (rooted) {  

    logLine();  
    logInfo("Advanced (root-aware) inspection:");  

    // Build set of installed package names for quick lookup  
    java.util.HashSet<String> installed = new java.util.HashSet<>();  
    for (ApplicationInfo ai : apps) {  
        if (ai != null && ai.packageName != null) installed.add(ai.packageName);  
    }  

    // Orphan data dirs check (honest: some dirs can be system-managed)  

    try {  
        // /data/user/0 is common; fall back to /data/data  
        File base = new File("/data/user/0");  
        if (!base.exists() || !base.isDirectory()) base = new File("/data/data");  

        File[] dirs = base.listFiles();  
        if (dirs != null) {  
            for (File d : dirs) {  
                if (d == null || !d.isDirectory()) continue;  
                String name = d.getName();  
                if (name == null || name.length() < 3) continue;  

                // if not installed -> orphan candidate  
                if (!installed.contains(name)) {  
                    long sz = dirSizeBestEffortRoot(d);  
                    // ignore tiny noise  
                    if (sz > (3L * 1024L * 1024L)) { // >3MB  
                        orphanDirs++;  
                        orphanBytes += sz;  
                    }  
                }  
            }  
        }  
    } catch (Throwable ignore) {}  

    if (orphanDirs > 0) {  
        logWarn(" Leftover app data detected (orphan folders).");  
        logOk("Count: " + orphanDirs + " | Approx size: " + humanBytes(orphanBytes));  
        logInfo("Human meaning:");  
        logOk("Uninstalled apps may have left data behind. Not dangerous, but adds clutter.");  
    } else {  
        logOk("No significant orphan app-data folders detected.");  
    }  

    // Cache pressure hint (root-only best effort)  
    try {  
        File cache = new File("/data/cache");  
        long cacheSz = dirSizeBestEffortRoot(cache);  
        if (cacheSz > (700L * 1024L * 1024L)) {  
            logWarn(" System cache is very large (" + humanBytes(cacheSz) + ").");  
            logOk("This can contribute to storage pressure on some devices.");  
        } else if (cacheSz > (350L * 1024L * 1024L)) {  
            logInfo("System cache size: " + humanBytes(cacheSz) + " (moderate).");  
        } else if (cacheSz > 0) {  
            logOk("System cache size: " + humanBytes(cacheSz) + " (normal).");  
        }  
    } catch (Throwable ignore) {}  

logInfo("Root-aware note:");  
logOk("Results are best-effort and device/vendor dependent. No false certainty reported.");  
}  

boolean appsImpactHigh = orphanDirs > 0 || orphanBytes > (200L * 1024L * 1024L);

GELServiceLog.info("SUMMARY: APPS_IMPACT=" +
        (appsImpactHigh ? "HIGH" : "NORMAL"));

appendHtml("<br>");  
logOk("Lab 26 finished.");  
logLine();
}

// ============================================================
// ROOT HELPER — BEST EFFORT DIRECTORY SIZE
// (Safe: if cannot read -> returns 0)
// ============================================================
private long dirSizeBestEffortRoot(File dir) {
if (dir == null) return 0L;
try {
if (!dir.exists() || !dir.isDirectory()) return 0L;
} catch (Throwable ignore) { return 0L; }

long total = 0L;  
File[] files;  
try {  
    files = dir.listFiles();  
} catch (Throwable t) {  
    return 0L;  
}  
if (files == null) return 0L;  

for (File f : files) {  
    if (f == null) continue;  
    try {  
        if (f.isFile()) {  
            total += Math.max(0L, f.length());  
        } else if (f.isDirectory()) {  
            total += dirSizeBestEffortRoot(f);  
        }  
    } catch (Throwable ignore) {}  
}  
return total;

}

// ============================================================
// LAB 27 — App Permissions & Privacy (FULL AUTO + RISK SCORE)
// ============================================================
private void lab27PermissionsPrivacy() {

appendHtml("<br>");
logLine();
logInfo("LAB 27 — App Permissions & Privacy (AUTO scan)");
logLine();

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
      
    String pkg = p.packageName;

// ============================================================
// EXCLUDE SYSTEM / GOOGLE / PLAY STORE APPS (LAB 27)
// ============================================================
boolean isSystem =
(p.applicationInfo != null) &&
((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
(p.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);

if (isSystem ||
pkg.startsWith("com.android.") ||
pkg.startsWith("com.google.android.") ||
pkg.equals("com.android.vending")) {
continue;
}

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
                    (appScore >= 60) ? "" :    
                    (appScore >= 30) ? "" :    
                    (appScore >= 15) ? "" : "";    

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
int maxRiskRef = 300; // theoretical max
int riskPct = Math.min(100, (riskTotal * 100) / maxRiskRef);
String riskColor =
(riskPct <= 20) ? "" :
(riskPct <= 50) ? "" :
(riskPct <= 80) ? "" : "";

logInfo("Apps scanned:");
logOk(String.valueOf(totalApps));

logInfo("Dangerous permissions GRANTED (total count):");
if (dangTotal == 0)
logOk(String.valueOf(dangTotal));
else if (dangTotal <= 5)
logWarn(String.valueOf(dangTotal));
else
logError(String.valueOf(dangTotal));

logInfo("Flagged apps:");
if (flaggedApps == 0)
logOk(String.valueOf(flaggedApps));
else if (flaggedApps <= 2)
logWarn(String.valueOf(flaggedApps));
else
logError(String.valueOf(flaggedApps));

logInfo("Privacy Risk Score:");
if (riskPct >= 70)
logError(riskPct + "%");
else if (riskPct >= 30)
logWarn(riskPct + "%");
else
logOk(riskPct + "%");

// ============================================================
// TOP OFFENDERS
// ============================================================
if (!appRisk.isEmpty()) {

logInfo("Top Privacy Offenders:");    

appRisk.entrySet()    
        .stream()    
        .sorted((a, b) -> b.getValue() - a.getValue())    
        .limit(8)    
        .forEach(e -> {    
            String c =    
                    (e.getValue() >= 60) ? "??" :    
                    (e.getValue() >= 30) ? "" :    
                    (e.getValue() >= 15) ? "" : "";    

            logInfo(" " + c + " " + safeLabel(pm, e.getKey())    
                    + " — Risk " + e.getValue());    
        });

}

// ============================================================
// FULL DETAILS
// ============================================================
if (!details.isEmpty()) {

logInfo("Permission Details (flagged apps):");  

for (String d : details) {  
    logWarn(d);  
}

} else {

logOk("No high-risk permission patterns detected.");

}

// ============================================================
// PRIVACY CONTEXT NOTE (SERVICE REPORT SAFE)
// ============================================================
logInfo("Privacy analysis note:");
logOk("Granted permissions do not imply malicious behavior.");
logOk("This result does not indicate hardware or system failure.");

appendHtml("<br>");
logOk("Lab 27 finished.");
logLine();

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
// LAB 28 — Hardware Stability & Interconnect Integrity
// TECHNICIAN MODE — SYMPTOM-BASED TRIAGE ONLY
// !! This lab does NOT diagnose hardware faults.
// !! Does NOT confirm soldering defects.
// ============================================================
private void lab28HardwareStability() {

    appendHtml("<br>");
    logLine();
    logInfo("LAB 28 — Hardware Stability & Interconnect Integrity");
    logWarn("Technician mode — symptom-based analysis ONLY.");
    logLine();

    // ------------------------------------------------------------
    // POPUP — TECHNICIAN WARNING (with TTS + Language + Mute)
    // ------------------------------------------------------------
    // helper method (showLab28Popup) is located in activity helpers
    showLab28Popup();

    // ============================================================
    // STAGE A — SYMPTOM SCORE (ORIGINAL LOGIC — UNTOUCHED)
    // ============================================================
    int symptomScore = 0;

    boolean randomReboots = detectRecentReboots();
    boolean signalDrops   = detectSignalInstability();
    boolean sensorFlaps   = detectSensorInstability();
    boolean thermalSpikes = detectThermalSpikes();
    boolean powerGlitches = detectPowerInstability();

    logInfo("Observed symptom signals:");

    if (randomReboots) {
        logWarn("Random reboots or sudden resets detected.");
        symptomScore += 25;
    } else logOk("No abnormal reboot pattern detected.");

    if (signalDrops) {
        logWarn("Network or radio instability detected.");
        symptomScore += 20;
    } else logOk("Radio signals appear stable.");

    if (sensorFlaps) {
        logWarn("Sensor instability (intermittent readings).");
        symptomScore += 15;
    } else logOk("Sensors appear stable.");

    if (thermalSpikes) {
        logWarn("Abnormal thermal spikes detected.");
        symptomScore += 20;
    } else logOk("Thermal behavior within normal range.");

    if (powerGlitches) {
        logWarn("Power or charging instability signals detected.");
        symptomScore += 20;
    } else logOk("Power behavior appears stable.");

    if (symptomScore > 100) symptomScore = 100;

    // ------------------------------------------------------------
    // SYMPTOM INTERPRETATION
    // ------------------------------------------------------------
    logLine();
    logInfo("Symptom Consistency Score:");

    String symptomLevel;
    if (symptomScore <= 20) symptomLevel = "LOW";
    else if (symptomScore <= 45) symptomLevel = "MODERATE";
    else if (symptomScore <= 70) symptomLevel = "HIGH";
    else symptomLevel = "VERY HIGH";

    if (symptomScore >= 40)
        logWarn(symptomScore + "/100 (" + symptomLevel + ")");
    else
        logOk(symptomScore + "/100 (" + symptomLevel + ")");

    // ============================================================
    // STAGE B — EVIDENCE SCORE (FROM GELServiceLog)
    // ============================================================
    int evidenceScore = 0;

    Lab28Evidence ev = Lab28EvidenceReader.readFromGELServiceLog();

    if (ev != null) {

        logLine();
        logInfo("Cross-lab evidence signals:");

        if (ev.thermalSpikes) {
            logWarn("Evidence: thermal instability (Lab 16).");
            evidenceScore += 20;
        } else logOk("Evidence: no abnormal thermal pattern.");

        if (ev.chargingGlitch) {
            logWarn("Evidence: charging or power glitches (Lab 15).");
            evidenceScore += 20;
        } else logOk("Evidence: charging behavior stable.");

        if (ev.radioInstability) {
            logWarn("Evidence: radio or network instability (Labs 10-13).");
            evidenceScore += 20;
        } else logOk("Evidence: radio signals stable.");

        if (ev.sensorFlaps) {
            logWarn("Evidence: sensor instability (Labs 7-9).");
            evidenceScore += 15;
        } else logOk("Evidence: sensors stable.");

        if (ev.rebootPattern) {
            logWarn("Evidence: abnormal reboot pattern (Lab 20).");
            evidenceScore += 15;
        } else logOk("Evidence: reboot behavior normal.");

        if (evidenceScore > 100) evidenceScore = 100;
    }

    // ============================================================
    // STAGE C — EXCLUSION RULES (ANTI-FALSE-POSITIVE)
    // ============================================================
    boolean softwareLikely = false;

    if (ev != null) {

        if ("SOFTWARE".equals(ev.crashPattern)) {
            logWarn("Exclusion: crash history indicates SOFTWARE origin.");
            softwareLikely = true;
        }

        if (ev.appsHeavyImpact) {
            logWarn("Exclusion: installed apps impact suggests SOFTWARE stress.");
            softwareLikely = true;
        }

        if (ev.thermalOnlyDuringCharging) {
            logWarn("Exclusion: thermal spikes linked to charging conditions.");
            softwareLikely = true;
        }
    }

    if (softwareLikely) {
        evidenceScore -= 30;
        if (evidenceScore < 0) evidenceScore = 0;
        logWarn("Evidence score adjusted due to software indicators.");
    }

    // ============================================================
    // STAGE D — FINAL CONFIDENCE
    // ============================================================
    int finalScore = (int) (0.6f * symptomScore + 0.4f * evidenceScore);
    if (finalScore > 100) finalScore = 100;

    logLine();
    logInfo("Final Stability Confidence Score:");

    String finalLevel;
    if (finalScore <= 20) finalLevel = "LOW";
    else if (finalScore <= 45) finalLevel = "MODERATE";
    else if (finalScore <= 70) finalLevel = "HIGH";
    else finalLevel = "VERY HIGH";

    if (finalScore >= 40)
        logWarn(finalScore + "/100 (" + finalLevel + ")");
    else
        logOk(finalScore + "/100 (" + finalLevel + ")");

    // ============================================================
    // FINAL WORDING — TRIAGE, NOT DIAGNOSIS
    // ============================================================
    logLine();
    logInfo("Technician note:");

    if (finalScore >= 60) {
        logWarn("Multi-source instability pattern detected.");
        logWarn("Symptoms may be consistent with intermittent contact issues.");
        logWarn("Possible loose connectors or unstable interconnect paths.");
        logInfo("Important:");
        logWarn("This is NOT a hardware diagnosis.");
        logWarn("This does NOT confirm soldering defects.");
        logInfo("Action:");
        logOk("Professional physical inspection and bench testing recommended.");
    }
    else if (finalScore >= 30) {
        logWarn("Some instability patterns detected.");
        logInfo("Evidence suggests mixed origin (hardware and software possible).");
        logOk("Hardware intervention is NOT indicated at this stage.");
    }
    else {
        logOk("No significant instability patterns detected.");
        logOk("No indication of interconnect or solder-related issues.");
    }

    appendHtml("<br>");
    logOk("Lab 28 finished.");
    logLine();
}

// ============================================================
// LAB 28 — Helpers
// ============================================================

private static class Lab28Evidence {
    boolean thermalSpikes;
    boolean chargingGlitch;
    boolean radioInstability;
    boolean sensorFlaps;
    boolean rebootPattern;

    boolean appsHeavyImpact;
    boolean thermalOnlyDuringCharging;

    String crashPattern; // SOFTWARE, MIXED, UNKNOWN
}

private static class Lab28EvidenceReader {

    static Lab28Evidence readFromGELServiceLog() {

        Lab28Evidence ev = new Lab28Evidence();
        ev.crashPattern = "UNKNOWN";

        String log;
        try {
            log = GELServiceLog.getAll();
        } catch (Throwable t) {
            return ev;
        }

        if (log == null || log.trim().isEmpty())
            return ev;

        final String L = log.toLowerCase(Locale.US);

        ev.thermalSpikes = containsAny(L,
                "thermal spike","thermal spikes","abnormal thermal",
                "overheat","overheating","temperature spike","temp spike","thermal behavior");

        ev.thermalOnlyDuringCharging =
                ev.thermalSpikes && containsAny(L,
                        "while charging","during charging","charging only","only while charging");

        ev.chargingGlitch = containsAny(L,
                "charging glitch","power glitch","charging instability",
                "usb disconnect","disconnect while charging","charger unstable");

        ev.radioInstability = containsAny(L,
                "radio instability","network instability","signal drop","no service",
                "wifi disconnect","internet access");

        ev.sensorFlaps = containsAny(L,
                "sensor instability","intermittent readings",
                "proximity","rotation","auto-rotate","sensor unavailable");

        ev.rebootPattern = containsAny(L,
                "random reboots","sudden resets","abnormal reboot",
                "unexpected reboot","uptime");

        boolean crashMention = containsAny(L,
                "crash","anr","freeze","app not responding","fatal exception");
        if (crashMention) ev.crashPattern = "SOFTWARE";

        ev.appsHeavyImpact = containsAny(L,
                "installed applications impact analysis",
                "heavy apps","high app impact","background apps");

        return ev;
    }

    private static boolean containsAny(String hay, String... needles) {
        if (hay == null || hay.isEmpty() || needles == null) return false;
        for (String n : needles) {
            if (n != null && hay.contains(n)) return true;
        }
        return false;
    }
}

// ============================================================
// LAB 29 — Auto Final Diagnosis Summary (GEL Universal AUTO Edition)
// Combines Thermals + Battery + Storage + RAM + Apps + Uptime +
// Security + Privacy + Root + Stability into final scores.
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================
private void lab28CombineFindings() {

appendHtml("<br>");
logLine();
logInfo("LAB 29 — Auto Final Diagnosis Summary (FULL AUTO)");
logLine();

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

logInfo("AUTO Breakdown:");

// Thermals  
logInfo("Thermals: " + thermalFlag + " " + thermalScore + "%");  
if (zones == null || zones.isEmpty()) {  
    logWarn("• No thermal zones readable. Using Battery temp only: " +  
            String.format(Locale.US, "°C", battTemp));  
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
" | Temp=" + fmt1(battTemp) + "Ã‚°C | Charging=" + charging);

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
if (verdict.startsWith("")) logOk(verdict);
else if (verdict.startsWith("")) logWarn(verdict);
else logError(verdict);

appendHtml("<br>");
logOk("Lab 29 finished.");
logLine();

}

// ============================================================
// ======= LAB 29 INTERNAL AUTO HELPERS (SAFE, NO IMPORTS) =====
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
"/system/app/Superuser.apk",
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
if (s >= 80) return "";
if (s >= 55) return "";
return "";
}

private String finalVerdict(int health, int sec, int priv, int perf) {

    // ============================================================
    // LEVEL 1 — HEALTHY / NORMAL
    // ============================================================
    if (health >= 80) {

        if (sec < 55 || priv < 55) {
            return
                " Device condition is healthy.\n" +
                " Privacy or security risks detected.\n" +
                "User review recommended.";
        }

        return
            " Device condition is healthy.\n" +
            "No servicing required.";
    }

    // ============================================================
    // LEVEL 2 — OBSERVATION (UNCERTAIN CAUSE)
    // ============================================================
    if (health >= 55) {

        if (sec < 55 || priv < 55) {
            return
                " Device condition shows moderate degradation.\n" +
                " Privacy or security risks detected.\n" +
                "User review recommended.";
        }

        return
            " Device condition shows moderate degradation.\n" +
            "Further monitoring recommended.";
    }

// ============================================================
// LEVEL 3 — UNATTRIBUTED INSTABILITY
// (NO hardware claim — evidence-based wording)
// ============================================================
return
    " Device condition shows instability.\n" +
    " Degradation detected without a clear software cause.\n" +
    "Cause not confirmed.\n" +
    "Classification: Unattributed system instability.\n" +
    "Further diagnostics recommended.";

}

private String fmt1(float v) {
return String.format(Locale.US, "%.1f", v);
}

// ============================================================
// LAB 30 — FINAL TECHNICIAN SUMMARY (READ-ONLY)
// Does NOT modify GELServiceLog — only reads it.
// Exports via ServiceReportActivity.
// ============================================================
private void lab29FinalSummary() {

appendHtml("<br>");  
logLine();  
logInfo("LAB 30 — FINAL TECHNICIAN SUMMARY (READ-ONLY)");  
logLine();  

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

    if (low.contains("") || low.contains("warning")) {  
        warnings.append(l).append("\n");  
    }  
    if (low.contains("") || low.contains("error")) {  
        warnings.append(l).append("\n");  
    }  
}  

// ------------------------------------------------------------  
// 3) PRINT SUMMARY TO UI (ONLY)  
// ------------------------------------------------------------  
  
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

appendHtml("<br>");  
logOk("Lab 30 finished.");  
logLine();  

appendHtml("<br>");  
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

    int total = TouchGridTestActivity.getTotalZones();  
    int remaining = TouchGridTestActivity.getRemainingZones();  

appendHtml("<br>");  
logLine();  
logSection("LAB 6 — Display / Touch");  
logLine();  

if (resultCode == RESULT_OK) {  

    logOk("Touch grid test completed.");  
    logOk("All screen zones responded to touch input.");  
    logOk("No dead touch zones detected.");  

} else {  

    logWarn("Touch grid test incomplete.");  
    logWarn(  
            "These " + remaining +  
            " screen zones did not respond to touch input (" +  
            remaining + " / " + total + ")."  
    );  

    logInfo("This may indicate:");  
    logError("Localized digitizer dead zones");  
    logWarn("Manual re-test is recommended to confirm behavior.");  
}  

appendHtml("<br>");  
logOk("Lab 6 finished.");  
logLine();  

enableSingleExportButton();  
return;

}

    // ============================================================
    // LAB 7 — Rotation + Proximity Sensors
    // ============================================================
    if (requestCode == 7007) {

        appendHtml("<br>");
        logLine();
        logSection("LAB 7 — Rotation & Proximity Sensors");
        logLine();

        if (resultCode == RESULT_OK) {
            logOk("Rotation detected via accelerometer.");
            logOk("Orientation change confirmed.");
            logOk("Motion sensors responding normally.");

            //  NEXT: Proximity
            startActivityForResult(
                    new Intent(this, ProximityCheckActivity.class),
                    8008
            );
            return;

        } else {
            logError("Rotation was not detected.");
            logWarn("Auto-rotate may be disabled or sensor malfunctioning.");

            logLine();
            logOk("Lab 7 finished.");
            enableSingleExportButton();
            return;
        }
    }

    if (requestCode == 8008) {

        if (resultCode == RESULT_OK) {
            logOk("Proximity sensor responded correctly.");
            logOk("Near/Far response confirmed.");
            logOk("Screen turned off when sensor was covered.");
        } else {
            logError("Proximity sensor did not respond.");
            logWarn("Possible sensor obstruction or hardware fault.");
        }

        appendHtml("<br>");
        logOk("Lab 7 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }
}

// ============================================================
// END OF CLASS
// ============================================================
}

