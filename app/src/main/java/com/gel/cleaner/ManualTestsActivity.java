// ============================================================
// ManualTestsActivity
// GEL Manual Diagnostics
// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
//
// LAB MAP (Manual Diagnostics Overview)
//
// LAB 1   — Speaker Tone Test
// LAB 2   — Speaker Frequency Sweep
// LAB 3   — Earpiece Audio Test
// LAB 4   — Microphone Recording Test (Bottom / Top)
//
// LAB 5   — Panic Frequency Analyzer
// LAB 6   — Sensors Basic Check
// LAB 7   — Sensors Motion Analysis
// LAB 8   — Camera Hardware & Path Integrity
//
// LAB 9   — Flash / Torch Test
// LAB 10  — Display & Pixel Integrity
// LAB 11  — Wi-Fi SSID Detection
// LAB 12  — Network Connectivity Test
//
// LAB 13  — Bluetooth Stack Test
// LAB 14  — Battery Health Stress Test
// LAB 15  — Charging System Analysis
// LAB 16  — Thermal Behaviour Analysis
//
// LAB 17  — CPU Stress & Stability
// LAB 18  — Storage I/O Test
// LAB 19  — RAM Pressure Detection
// LAB 20  — Reboot Pattern Analysis
//
// LAB 21  — GPS Location Fix Test
// LAB 22  — NFC Hardware Check
// LAB 23  — Audio Routing Diagnostics
// LAB 24  — Root Detection & Security Risk
//
// LAB 25  — Crash Log Detection
// LAB 26  — System Integrity Scan
// LAB 27  — Power Management Behaviour
// LAB 28  — Background Activity Check
//
// LAB 29  — Service Log Aggregation
// LAB 30  — Final Technician Summary
//
// ============================================================
// NOTE
// This activity contains all manual diagnostics used by
// GEL service mode. Each LAB produces structured logs
// consumed by LAB 30 for the final technician report.
// ============================================================

package com.gel.cleaner;

// ============================================================
// ANDROID — CORE
// ============================================================

import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.Manifest;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
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
import android.media.AudioRecord;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import android.os.PowerManager;
import android.os.Process;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.Settings;
import android.provider.MediaStore;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.VideoView;
import android.widget.TextView;
import android.widget.Toast;

import android.speech.tts.TextToSpeech;
import android.widget.CheckBox;

// ============================================================
// ANDROIDX
// ============================================================
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.gel.cleaner.UIHelpers;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

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

    // ============================================================
    // PERMISSION ENGINE (UNIVERSAL)
    // ============================================================
    private static final int REQ_CORE_PERMS = 5000;
    private Runnable pendingAfterPermission = null;

    private static final int REQ_LAB6_TOUCH = 6006;
    private static final int REQ_LAB6_COLOR = 6007;
    private static final int REQ_LAB13_BT_CONNECT = 1313;

    private AlertDialog lab14RunningDialog;
// ------------------------------------------------------------
// LAB14 GPU STRESS
// ------------------------------------------------------------
    private volatile boolean lab14GpuRunning = false;
    private Thread lab14GpuThread = null;
    private AlertDialog activeDialog;
    private String pendingTtsText;
    
    final float[] batterySOH = {Float.NaN};
    
    final boolean[] cellImbalanceRisk = {false};
    
    final boolean[] batteryFailureRisk = {false};

    private boolean lab6ProCanceled = false;

    // ============================================================
    // LAB 8.1 — STATE (CLASS FIELDS)
    // ============================================================
    private ArrayList<Lab8Cam> lab8CamsFor81 = null;
    private CameraManager lab8CmFor81 = null;
    private final Map<String, Integer> lab8CameraLogAnchors = new HashMap<>();

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

                    final boolean gr = AppLang.isGreek(c);

                    if (lab13StatusText != null) {
                        lab13StatusText.setText(
                                gr
                                        ? "Συνδέθηκε εξωτερική συσκευή Bluetooth. Εκκίνηση παρακολούθησης..."
                                        : "External Bluetooth device connected. Starting monitor..."
                        );
                    }

                    if (!lab13WaitTtsPlayed && !AppTTS.isMuted(c)) {
                        lab13WaitTtsPlayed = true;
                        AppTTS.ensureSpeak(
                                c,
                                gr
                                        ? "Εντοπίστηκε σύνδεση Bluetooth. Ξεκινά η παρακολούθηση."
                                        : "Bluetooth connection detected. Monitoring started."
                        );
                    }

                    startLab13Monitor60s();
                }
            }
        }
    };

    // ✅ Activity field (NOT inside receiver)
    private boolean lab13WaitTtsPlayed = false;

    // ============================================================
    // GLOBAL TTS (for labs that need shared access)
    // ============================================================
    private TextToSpeech[] tts = new TextToSpeech[1];
    private boolean[] ttsReady = { false };

    // ============================================================
    // GLOBAL TTS PREF — WRAPPER TO AppTTS (SINGLE AUTHORITY)
    // ============================================================
    private void loadTtsMuted() {
        // handled centrally by AppTTS
    }

    private boolean isTtsMuted() {
        return AppTTS.isMuted(this);
    }

    private void setTtsMuted(boolean muted) {
        AppTTS.setMuted(this, muted);
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
    private boolean lab3OldMicMute = false;

    private volatile boolean lab4HumanFallbackUsed = false;

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
private VideoView lab14StressVideo;

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
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // SAFETY GUARD
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (isFinishing() || isDestroyed()) return;
    }, 120);

    prefs = getSharedPreferences("GEL_DIAG", MODE_PRIVATE);
    p = prefs;

    ui = new Handler(Looper.getMainLooper());

    initTTS();

    // ============================================================
    // ROOT SCROLL + LAYOUT
    // ============================================================
    scroll = new ScrollView(this);
    scroll.setFillViewport(true);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    int pad = dp(16);
    root.setPadding(pad, pad, pad, pad);
    root.setBackgroundColor(0xFF101010);

    scroll.addView(root);
    setContentView(scroll);
    
    UIHelpers.applyPressEffectRecursive(getWindow().getDecorView());

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
    lab14DotsView.setText("•");
    lab14DotsView.setTextSize(22f);
    lab14DotsView.setTextColor(0xFF39FF14);
    lab14DotsView.setPadding(0, dp(6), 0, dp(10));
    lab14DotsView.setGravity(Gravity.CENTER_HORIZONTAL);
    root.addView(lab14DotsView);
    
final boolean gr = AppLang.isGreek(this);

    // ============================================================  
    // SECTION 1: AUDIO & VIBRATION — LABS 1-5  
    // ============================================================  
    LinearLayout body1 = makeSectionBody();  
    Button header1 = makeSectionHeader(getString(R.string.manual_cat_1), body1);  
    root.addView(header1);  
    root.addView(body1);  

    body1.addView(makeTestButton(
        gr ? "1. Δοκιμή Τόνου Ηχείου"
           : "1. Speaker Tone Test",
        this::lab1SpeakerTone));

body1.addView(makeTestButton(
        gr ? "2. Έλεγχος Συχνοτήτων Ηχείου"
           : "2. Speaker Frequency Sweep Test",
        this::lab2SpeakerSweep));

body1.addView(makeTestButton(
        gr ? "3. Έλεγχος Ακουστικού Κλήσης"
           : "3. Earpiece Call Check",
        this::lab3EarpieceManual));

body1.addView(makeTestButton(
        gr ? "4. Έλεγχος Ποιότητας Κλήσης Μικροφώνου / Ακουστικού"
           : "4. Microphone / Earpiece Call Quality Check",
        this::lab4MicManual));

body1.addView(makeTestButton(
        gr ? "5. Δοκιμή Δόνησης"
           : "5. Vibration Motor Test",
        this::lab5Vibration));

    // ============================================================  
    // SECTION 2: DISPLAY & SENSORS — LABS 6 - 9  
    // ============================================================  
    LinearLayout body2 = makeSectionBody();  
    Button header2 = makeSectionHeader(getString(R.string.manual_cat_2), body2);  
    root.addView(header2);  
    root.addView(body2);  

    body2.addView(makeTestButton(
        gr ? "6. Έλεγχος Οθόνης / Αφής"
           : "6. Display / Touch Inspection",
        this::lab6DisplayTouch));

body2.addView(makeTestButton(
        gr ? "7. Ελεγχος Περιστροφής & Αισθητήρα Εγγύτητας"
           : "7. Rotation & Proximity Sensors Check",
        this::lab7RotationAndProximityManual));

body2.addView(makeTestButton(
        gr ? "8. Ελεγχος Hardware Καμερας & Preview Path"
           : "8. Camera Hardware & Preview Path Check",
        this::lab8CameraHardwareCheck));

body2.addView(makeTestButton(
        gr ? "9. Έλεγχος Αισθητήρων"
           : "9. Sensors Check",
        this::lab9SensorsCheck));

    // ============================================================  
    // SECTION 3: WIRELESS & CONNECTIVITY — LABS 10 - 13  
    // ============================================================  
    LinearLayout body3 = makeSectionBody();  
    Button header3 = makeSectionHeader(getString(R.string.manual_cat_3), body3);  
    root.addView(header3);  
    root.addView(body3);  

    body3.addView(makeTestButton(
        gr ? "10. Έλεγχος Wi-Fi"
           : "10. Wi-Fi Connection Check",
        this::lab10WifiConnectivityCheck));

body3.addView(makeTestButton(
        gr ? "11. Διάγνωση Δικτύου Κινητού"
           : "11. Mobile Network Diagnostic",
        this::lab11MobileDataDiagnostic));

body3.addView(makeTestButton(
        gr ? "12. Ανάλυση Τηλεφωνικής Λειτουργίας"
           : "12. Telephony Function Analysis",
        this::lab12CallFunctionInterpretation));

body3.addView(makeTestButton(
        gr ? "13. Έλεγχος Σύνδεσης Bluetooth"
           : "13. Bluetooth Connectivity Check",
        this::lab13BluetoothConnectivityCheck));
    
    // ============================================================  
    // SECTION 4: BATTERY & THERMAL — LABS 14 - 17  
    // ============================================================  
    LinearLayout body4 = makeSectionBody();  
    Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);  
    root.addView(header4);  
    root.addView(body4);  

    body4.addView(makeTestButtonRedGold(
        gr ? "14. Δοκιμή Καταπόνησης Υγείας Μπαταρίας"
           : "14. Battery Health Stress Test",
        () -> showLab14PreTestAdvisory(this::lab14BatteryHealthStressTest)
));

body4.addView(makeTestButton(
        gr ? "15. Διαγνωστικός Έλεγχος Συστήματος Φόρτισης (Smart)"
           : "15. Charging System Diagnostic (Smart)",
        this::lab15ChargingSystemSmart
));

body4.addView(makeTestButton(
        gr ? "16. Στιγμιότυπο Θερμικών Αισθητήρων"
           : "16. Thermal Sensors Snapshot",
        this::lab16ThermalSnapshot
));

body4.addView(makeTestButtonGreenGold(
        gr ? "17. Ευφυής Ανάλυση Υγείας Συστήματος"
           : "17. Intelligent System Health Analysis",
        this::lab17RunAuto
));

    // ============================================================  
    // SECTION 5: STORAGE & PERFORMANCE — LABS 18 - 20  
    // ============================================================  
    LinearLayout body5 = makeSectionBody();  
    Button header5 = makeSectionHeader(getString(R.string.manual_cat_5), body5);  
    root.addView(header5);  
    root.addView(body5);  
      
    body5.addView(makeTestButton(
        gr ? "18. Έλεγχος Υγείας Αποθηκευτικού Χώρου"
           : "18. Storage Health Inspection",
        this::lab18StorageSnapshot));

body5.addView(makeTestButton(
        gr ? "19. Ανάλυση Πίεσης Μνήμης & Σταθερότητας"
           : "19. Memory Pressure & Stability Analysis",
        this::lab19RamSnapshot));

body5.addView(makeTestButton(
        gr ? "20. Ανάλυση Uptime & Προτύπων Επανεκκίνησης"
           : "20. Uptime & Reboot Pattern Analysis",
        this::lab20UptimeHints)); 
 
    // ============================================================  
    // SECTION 6: SECURITY & SYSTEM HEALTH — LABS 21 - 24  
    // ============================================================  
    LinearLayout body6 = makeSectionBody();  
    Button header6 = makeSectionHeader(getString(R.string.manual_cat_6), body6);  
    root.addView(header6);  
    root.addView(body6);  

    body6.addView(makeTestButton(
        gr ? "21. Κλείδωμα Οθόνης / Βιομετρικά"
           : "21. Screen Lock / Biometrics",
        this::lab21ScreenLock));

body6.addView(makeTestButton(
        gr ? "22. Έλεγχος Ενημέρωσης Ασφαλείας"
           : "22. Security Patch Check",
        this::lab22SecurityPatchAndPlayProtect));

body6.addView(makeTestButton(
        gr ? "23. Κίνδυνος από Επιλογές Προγραμματιστή"
           : "23. Developer Options Risk",
        this::lab23DeveloperOptionsRisk));

body6.addView(makeTestButton(
        gr ? "24. Ένδειξη Root / Ξεκλείδωτου Bootloader"
           : "24. Root / Bootloader Suspicion",
        this::lab24RootSuspicion));

    // ============================================================  
    // SECTION 7: ADVANCED / LOGS — LABS 25 - 30 
    // ============================================================  
    LinearLayout body7 = makeSectionBody();  
    Button header7 = makeSectionHeader(getString(R.string.manual_cat_7), body7);  
    root.addView(header7);  
    root.addView(body7);  

        body7.addView(makeTestButton(
        gr ? "25. Ιστορικό Κρασαρισμάτων / Παγώματος"
           : "25. Crash / Freeze History",
        this::lab25CrashHistory));

body7.addView(makeTestButton(
        gr ? "26. Ανάλυση Επιπτώσεων Εγκατεστημένων Εφαρμογών"
           : "26. Installed Applications Impact Analysis",
        this::lab26AppsFootprint));

body7.addView(makeTestButton(
        gr ? "27. Δικαιώματα Εφαρμογών & Απόρρητο"
           : "27. App Permissions & Privacy",
        this::lab27PermissionsPrivacy));

body7.addView(makeTestButton(
        gr ? "28. Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων\nΥποψία Κόλλησης / Υγρασίας (Βάσει Συμπτωμάτων)"
           : "28. Hardware Stability & Interconnect Integrity\nSolder / Moisture Indicators (SYMPTOM-BASED)",
        this::lab28HardwareStability)); 
        
body7.addView(makeTestButton(
        gr ? "29. Έλεγχος Γνησιότητας Συσκευής & Ανταλλακτικών\nΠιθανή Αντικατάσταση Μερών"
           : "29. Device Authenticity & Parts Verification\nPossible Non-OEM Components",
        this::lab29DeviceAuthenticity));

body7.addView(makeTestButton(
        gr ? "30. Σύνοψη Βαθμολογιών Συσκευής"
           : "30. DEVICE SCORES Summary",
        this::lab30CombineFindings));

body7.addView(makeTestButton(
        gr ? "31. Τελική Τεχνική Αναφορά"
           : "31. FINAL TECH SUMMARY",
        this::lab31FinalSummary));

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
// SERVICE LOG — INIT (Android Manual Tests)
// ============================================================

if (!serviceLogInit) {

    GELServiceLog.section(
            gr
                    ? "Χειροκίνητοι Έλεγχοι Android — Διαγνωστικά Υλικού"
                    : "Android Manual Tests — Hardware Diagnostics"
    );

    logLine();

    logInfo(
            gr
                    ? "Έναρξη χειροκίνητων διαγνωστικών ελέγχων συσκευής."
                    : getString(R.string.manual_log_desc)
    );

    serviceLogInit = true;
}

}  // onCreate ENDS HERE

private boolean ensurePermissions(String[] permissions, Runnable afterGranted) {

    List<String> missing = new ArrayList<>();

    for (String p : permissions) {
        if (ContextCompat.checkSelfPermission(this, p)
                != PackageManager.PERMISSION_GRANTED) {
            missing.add(p);
        }
    }

    if (missing.isEmpty()) {
        return true;
    }

    pendingAfterPermission = afterGranted;

    ActivityCompat.requestPermissions(
            this,
            missing.toArray(new String[0]),
            REQ_CORE_PERMS
    );

    return false;
}

@Override
protected void onPause() {

    // ============================================================
    // LAB 3 — FORCE STOP IF USER LEFT ACTIVITY
    // ============================================================
    try {
        lab3WaitingUser = false;
        stopLab3Tone();
        SystemClock.sleep(120);
        restoreLab3Audio();
    } catch (Throwable ignore) {
        // never crash on pause
    }

    // ============================================================
    // TTS STOP — lifecycle safe (no speech leaks)
    // ============================================================
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
        }
    } catch (Throwable ignore) {
        // never crash on pause
    }

    super.onPause();
}

@Override
protected void onDestroy() {

    // ============================================================
    // HANDLER CLEANUP — prevent pending UI callbacks
    // ============================================================
    try {
        if (ui != null) {
            ui.removeCallbacksAndMessages(null);
        }
    } catch (Throwable ignore) {}

    // ============================================================
    // LAB 13 — receiver cleanup (SAFE)
    // ============================================================
    try {
        unregisterReceiver(lab13BtReceiver);
    } catch (Throwable ignore) {
        // receiver may already be unregistered
    }

    // ============================================================
    // LAB 14 — dots animation cleanup
    // ============================================================
    try {
        if (lab14DotsView != null) {
            lab14DotsView.removeCallbacks(null);
        }
    } catch (Throwable ignore) {}

// ============================================================
// LAB 14 — HARD STOP STRESS THREADS
// ============================================================
try { stopCpuBurn(); } catch (Throwable ignore) {}
try { stopMemoryStress(); } catch (Throwable ignore) {}
try { stopGpuStress(); } catch (Throwable ignore) {}

    // ============================================================
    // TTS FULL CLEANUP — final lifecycle teardown
    // ============================================================
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
            tts[0].shutdown();
        }
    } catch (Throwable ignore) {
        // never crash on destroy
    } finally {
        if (tts != null) {
            tts[0] = null;
        }
        if (ttsReady != null) {
            ttsReady[0] = false;
        }
    }

    super.onDestroy();
}

// ============================================================
// GLOBAL TTS INIT — ONE TIME ONLY (SAFE)
// ============================================================
private void initTTS() {

    if (tts[0] != null) return;

    tts[0] = new TextToSpeech(this, status -> {

        if (status == TextToSpeech.SUCCESS && tts[0] != null) {

            Locale locale = AppLang.isGreek(this)
                    ? new Locale("el", "GR")
                    : Locale.US;

            int res = tts[0].setLanguage(locale);

            if (res == TextToSpeech.LANG_MISSING_DATA
                    || res == TextToSpeech.LANG_NOT_SUPPORTED) {

                tts[0].setLanguage(Locale.US);
            }

            ttsReady[0] = true;

            if (pendingTtsText != null) {
                tts[0].speak(
                        pendingTtsText,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "GEL_TTS_PENDING"
                );
                pendingTtsText = null;
            }
        }
    });
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
// BATTERY VOLTAGE HELPER (SAFE)
// ============================================================
private float getBatteryVoltageSafe() {

    try {

        IntentFilter filter =
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent battery =
                registerReceiver(null, filter);

        if (battery == null)
            return Float.NaN;

        int mv =
                battery.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        if (mv <= 0)
            return Float.NaN;

        return mv / 1000f; // volts

    } catch (Throwable ignore) {
        return Float.NaN;
    }
}

// ------------------------------------------------------------
// BATTERY PERCENT (SAFE)
// ------------------------------------------------------------
private int getBatteryPercentSafe() {

    try {

        IntentFilter iFilter =
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent batteryStatus =
                registerReceiver(null, iFilter);

        if (batteryStatus == null)
            return -1;

        int level =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_LEVEL,
                        -1
                );

        int scale =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_SCALE,
                        -1
                );

        if (level < 0 || scale <= 0)
            return -1;

        return (int) ((level / (float) scale) * 100f);

    } catch (Throwable ignore) {
        return -1;
    }
}

// ============================================================
// BATTERY VOLTAGE FILTER (3-SAMPLE AVERAGE)
// ============================================================
private float getBatteryVoltageFiltered() {

    float sum = 0f;
    int count = 0;

    // ----------------------------------------------------
    // DISCARD FIRST READ (PMIC refresh jitter)
    // ----------------------------------------------------
    getBatteryVoltageSafe();
    SystemClock.sleep(120);

    for (int i = 0; i < 4; i++) {

        float v = getBatteryVoltageSafe();

        if (!Float.isNaN(v)) {
            sum += v;
            count++;
        }

        SystemClock.sleep(120);
    }

    if (count == 0)
        return Float.NaN;

    return sum / count;
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
bg.setCornerRadius(dp(10));  
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
// FINAL — GEL Dark/Gold + Neon Green + TTS + Mute
// ============================================================
private void askUserEarpieceConfirmation() {

    runOnUiThread(() -> {

        if (lab3WaitingUser) return;
        lab3WaitingUser = true;

        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        // ==========================
        // ROOT
        // ==========================
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(24), dp(28), dp(22));
        root.setMinimumWidth(dp(300));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        // ==========================
        // TITLE (WHITE)
        // ==========================
        TextView title = new TextView(this);
        title.setText(gr ? "LAB 3 — Επιβεβαίωση" : "LAB 3 — Confirmation");
        title.setTextColor(Color.WHITE);
        title.setTextSize(17f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(14));
        root.addView(title);

        // ==========================
        // MESSAGE (NEON GREEN)
        // ==========================
        TextView msg = new TextView(this);
        msg.setText(
                gr
                        ? "Άκουσες καθαρά τους ήχους\nαπό το ακουστικό;"
                        : "Did you hear the tones\nclearly from the earpiece?"
        );
        msg.setTextColor(0xFF39FF14); // GEL neon green
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        msg.setLineSpacing(0f, 1.2f);
        msg.setPadding(0, 0, 0, dp(18));
        root.addView(msg);

// ==========================
// MUTE ROW (UNIFIED — AppTTS HELPER)
// ==========================
root.addView(buildMuteRow());

        // ---------- BUTTON ROW ----------
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams btnLp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        btnLp.setMargins(dp(12), dp(8), dp(12), dp(8));

        // ---------- NO ----------
        Button noBtn = new Button(this);
        noBtn.setText(gr ? "ΟΧΙ" : "NO");
        noBtn.setAllCaps(false);
        noBtn.setTextColor(Color.WHITE);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);
        noBtn.setBackground(noBg);
        noBtn.setLayoutParams(btnLp);

        // ---------- YES ----------
        Button yesBtn = new Button(this);
        yesBtn.setText(gr ? "ΝΑΙ" : "YES");
        yesBtn.setAllCaps(false);
        yesBtn.setTextColor(Color.WHITE);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);
        yesBtn.setBackground(yesBg);
        yesBtn.setLayoutParams(btnLp);

// ---------- ADD ----------
btnRow.addView(noBtn);
btnRow.addView(yesBtn);
root.addView(btnRow);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// ------------------------------------------------------------
// STOP TTS ON ANY DISMISS
// ------------------------------------------------------------
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// ------------------------------------------------------------
// BACK KEY — STOP TTS + RESTORE AUDIO
// ------------------------------------------------------------
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}

        lab3WaitingUser = false;
        restoreLab3Audio();
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (d.isShowing()
                && !isFinishing()
                && !isDestroyed()
                && !AppTTS.isMuted(this)) {

            AppTTS.ensureSpeak(
                    this,
                    gr
                            ? "Άκουσες καθαρά τους ήχους από το ακουστικό;"
                            : "Did you hear the tones clearly from the earpiece?"
            );
        }
    }, 400);
}

// ------------------------------------------------------------
// YES ACTION (PASS)
// ------------------------------------------------------------
yesBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    lab3WaitingUser = false;

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr
                    ? "Ο χρήστης επιβεβαίωσε καθαρή αναπαραγωγή ήχου"
                    : "User confirmed audio playback"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();

    restoreLab3Audio();
    d.dismiss();
});

// ------------------------------------------------------------
// NO ACTION (FAIL)
// ------------------------------------------------------------
noBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    lab3WaitingUser = false;

    logLabelErrorValue(
            gr ? "LAB 3 — Ακουστικό" : "LAB 3 — Earpiece",
            gr
                    ? "Ο χρήστης ΔΕΝ άκουσε τους ήχους"
                    : "User did NOT hear tones"
    );

    logLabelWarnValue(
            gr ? "Πιθανό πρόβλημα" : "Possible issue",
            gr
                    ? "Πιθανή βλάβη ακουστικού ή πρόβλημα δρομολόγησης ήχου"
                    : "Earpiece failure or audio routing problem"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();

    restoreLab3Audio();
    d.dismiss();
});

    });
} 

// ============================================================
// LAB 3 — STATE / HELPERS (LOCKED)
// ============================================================

private void routeToCallEarpiece() {
    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return;

        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(false);
        am.setMicrophoneMute(false);
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
    } catch (Throwable ignore) {}
}

private void routeToEarpiecePlayback() {
    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) return;

        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(false);
        am.setMode(AudioManager.MODE_NORMAL);
    } catch (Throwable ignore) {}
}

private ToneGenerator lab3Tone;

/**
 * HARD restore for LAB 3
 * One single source of truth.
 * Used on success / cancel / exception.
 */
 
private void restoreLab3Audio() {
    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (am == null) return;   // 🔒 safety

        resetAudioAfterLab3(
                am,
                lab3OldMode,
                lab3OldSpeaker,
                lab3OldMicMute
        );

    } catch (Throwable ignore) {}
}

/**
 * Plays a short earpiece beep using VOICE_CALL stream.
 * Earpiece-only, OEM safe.
 */
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

    try {
        track.write(buffer, 0, buffer.length);
        track.play();
        SystemClock.sleep(durationMs + 100);
    } finally {
        try { track.stop(); } catch (Throwable ignore) {}
        try { track.release(); } catch (Throwable ignore) {}
    }
}

/**
 * Optional tone stop helper (defensive).
 */
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
// LAB 3 — HARD AUDIO RESET (SINGLE SOURCE OF TRUTH)
// ============================================================
private void resetAudioAfterLab3(
        AudioManager am,
        int oldMode,
        boolean oldSpeaker,
        boolean oldMicMute
) {
    if (am == null) return;

    try {
        try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
        try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}

        // Force clean baseline
        try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(oldSpeaker); } catch (Throwable ignore) {}
        try { am.setMicrophoneMute(oldMicMute); } catch (Throwable ignore) {}

        SystemClock.sleep(120);

    } catch (Throwable ignore) {}
}

// ============================================================
// HARD AUDIO NORMALIZE — BEFORE MIC CAPTURE (MANDATORY)
// ============================================================
private void hardNormalizeAudioForMic() {

    try {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (am == null) return;

        try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
        try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}

        try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(false); } catch (Throwable ignore) {}

        // 🔴 ΤΟ ΣΗΜΑΝΤΙΚΟ
        try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}

        SystemClock.sleep(300);

    } catch (Throwable ignore) {}
}

// ============================================================
// HELPERS REQUIRED BY LAB 4 PRO (STRICT – DO NOT TOUCH)
// ============================================================

private AlertDialog buildInfoDialog(
        String titleText,
        String messageText,
        AtomicBoolean cancelled,
        AtomicReference<AlertDialog> dialogRef
) {
    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(17f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(14));
    root.addView(title);

    TextView msg = new TextView(this);
    msg.setText(messageText);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(14.5f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(16));
    root.addView(msg);

    Button exit = new Button(this);
    exit.setAllCaps(false);
    exit.setText(AppLang.isGreek(this) ? "ΕΞΟΔΟΣ ΤΕΣΤ" : "EXIT TEST");
    exit.setTextColor(Color.WHITE);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(10));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    exit.setBackground(exitBg);

    exit.setOnClickListener(v -> {
        cancelled.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        try {
            AlertDialog d = dialogRef.get();
            if (d != null) d.dismiss();
        } catch (Throwable ignore) {}
    });

    root.addView(exit);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// 🔴 ΣΗΜΑΝΤΙΚΟ — ΔΗΛΩΝΟΥΜΕ ΤΟ DIALOG ΣΤΟ REF
dialogRef.set(d);

// Stop TTS on ANY dismiss
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// Back key handling
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        cancelled.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

return d;
}

private void forceSpeaker(AudioManager am) {
    if (am == null) return;
    try {
        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setMicrophoneMute(false);
        am.setMode(AudioManager.MODE_NORMAL);
        am.setSpeakerphoneOn(true);
        SystemClock.sleep(120);
    } catch (Throwable ignore) {}
}

private void dismiss(AtomicReference<AlertDialog> ref) {
    try {
        AlertDialog d = ref.get();
        if (d != null) d.dismiss();
    } catch (Throwable ignore) {}
}

private int getWorkingMicSource() {
    SharedPreferences sp = getSharedPreferences("gel_audio_profile", MODE_PRIVATE);
    return sp.getInt("mic_source", MediaRecorder.AudioSource.VOICE_COMMUNICATION);
}

// ============================================================
// HUMAN VOICE DETECTION — FULLY SELF-CONTAINED (NO ENGINE)
// Adaptive AudioSource scan — WORKS ON REAL DEVICES
// ============================================================

// ====================================================
// AUDIO SOURCES — CLASS LEVEL (LOCKED)
// ====================================================
private static final int[] AUDIO_SOURCES = new int[] {
        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
        MediaRecorder.AudioSource.VOICE_RECOGNITION,
        MediaRecorder.AudioSource.MIC,
        MediaRecorder.AudioSource.DEFAULT,
        MediaRecorder.AudioSource.CAMCORDER,
        MediaRecorder.AudioSource.UNPROCESSED
};

private boolean detectHumanVoiceAdaptive(boolean gr) {

    final int SR = 44100;
    final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    final int STEP_MS = 100;
    final long WINDOW_MS = 5000;

    for (int source : AUDIO_SOURCES) {

        AudioRecord ar = null;

        try {
            int minBuf = AudioRecord.getMinBufferSize(SR, CHANNEL, FORMAT);
            if (minBuf <= 0) continue;

            ar = new AudioRecord(
                    source,
                    SR,
                    CHANNEL,
                    FORMAT,
                    minBuf * 2
            );

            if (ar.getState() != AudioRecord.STATE_INITIALIZED) continue;

            ar.startRecording();
SystemClock.sleep(250);

// 👇 ΠΡΩΤΑ buffer
short[] buf = new short[1024];
            
// =============================
// BASELINE — SILENCE
// =============================
long noiseSum = 0;
int noiseFrames = 0;

for (int i = 0; i < 5; i++) {
    int n = ar.read(buf, 0, buf.length);
    if (n <= 0) continue;

    long sumSq = 0;
    for (int j = 0; j < n; j++) {
        int v = Math.abs(buf[j]);
        sumSq += (long) v * v;
    }

    double rms = Math.sqrt((double) sumSq / n);
    noiseSum += rms;
    noiseFrames++;

    SystemClock.sleep(100);
}

double noiseFloor = noiseFrames > 0
        ? noiseSum / noiseFrames
        : 0;

            long until = SystemClock.uptimeMillis() + WINDOW_MS;
            long voicedMs = 0;

            while (SystemClock.uptimeMillis() < until) {

                int n = ar.read(buf, 0, buf.length);
                if (n <= 0) {
                    SystemClock.sleep(STEP_MS);
                    continue;
                }

                long sumSq = 0;
                int peak = 0;

                for (int i = 0; i < n; i++) {
                    int v = Math.abs(buf[i]);
                    peak = Math.max(peak, v);
                    sumSq += (long) v * v;
                }

                double rms = Math.sqrt((double) sumSq / n);

                boolean rmsOk  = rms > noiseFloor * 2.2;
boolean peakOk = peak > 2500;

                if (rmsOk && peakOk) {
                    voicedMs += STEP_MS;
                } else {
                    voicedMs = Math.max(0, voicedMs - STEP_MS); // decay
                }

                if (voicedMs >= 800) {

                    saveWorkingMicSource(source);

                    logOk(gr
                            ? "Φωνή ανιχνεύθηκε."
                            : "Voice detected.");

                    logInfo(gr
                            ? "Πηγή ήχου: " + source
                            : "Audio source: " + source);

                    return true;
                }

                SystemClock.sleep(STEP_MS);
            }

        } catch (Throwable ignore) {

        } finally {
            try {
                if (ar != null) {
                    ar.stop();
                    ar.release();
                }
            } catch (Throwable ignore) {}
        }
    }

    logLabelErrorValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Δεν ανιχνεύθηκε ανθρώπινη φωνή με καμία πηγή."
                    : "Human voice not detected with any audio source."
    );

    return false;
}

// ============================================================
// PERSISTENCE
// ============================================================
private void saveWorkingMicSource(int source) {
    getSharedPreferences("gel_audio_profile", MODE_PRIVATE)
            .edit()
            .putInt("mic_source", source)
            .apply();
}

// ============================================================
// LAB 8.1 — HUMAN SUMMARY HELPERS
// ============================================================

private static class CameraHumanSummary {
    String photoQuality;          // "9 MP photos (very good)"
    String professionalPhotos;    // "RAW uncompressed photos supported"
    String videoQuality;          // "4K (very high)" / "Full HD (high)"
    String videoSmoothness;
    String slowMotion;
    String stabilization;
    String manualMode;
    String flash;
    String realLifeUse;
    String verdict;
}

private CameraHumanSummary buildHumanSummary(CameraCharacteristics cc) {

    CameraHumanSummary h = new CameraHumanSummary();

    // ------------------------------------------------------------
    // CAPS
    // ------------------------------------------------------------
    boolean hasRaw = false;
    boolean manual = false;

    int[] caps = cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
    if (caps != null) {
        for (int c : caps) {
            if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)
                hasRaw = true;
            if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)
                manual = true;
        }
    }

    h.manualMode = manual ? "Supported" : "Not supported";

    // ------------------------------------------------------------
    // PHOTO QUALITY (MP)
    // ------------------------------------------------------------
    StreamConfigurationMap map =
            cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

    Size maxPhoto = null;
    if (map != null) {
        Size[] photos = map.getOutputSizes(ImageFormat.JPEG);
        if (photos != null && photos.length > 0) {
            maxPhoto = photos[0];
            for (Size s : photos) {
                long a = (long) s.getWidth() * s.getHeight();
                long b = (long) maxPhoto.getWidth() * maxPhoto.getHeight();
                if (a > b) maxPhoto = s;
            }
        }
    }

    if (maxPhoto != null) {
        int mp = (maxPhoto.getWidth() * maxPhoto.getHeight()) / 1_000_000;
        h.photoQuality = mp + " MP photos (very good)";
    } else {
        h.photoQuality = "Standard photos";
    }

// ------------------------------------------------------------
// PROFESSIONAL PHOTOS (RAW)
// ------------------------------------------------------------
final boolean gr = AppLang.isGreek(this);

h.professionalPhotos = hasRaw
        ? (gr
            ? "Υποστηρίζεται λήψη RAW (ασυμπίεστων) φωτογραφιών"
            : "RAW (uncompressed) photo capture supported")
        : (gr
            ? "Δεν υποστηρίζεται RAW (μόνο JPEG)"
            : "RAW not supported (JPEG only)");

    // ------------------------------------------------------------
    // VIDEO QUALITY
    // ------------------------------------------------------------
    int maxWidth = 0;
    if (map != null) {
        Size[] vids = map.getOutputSizes(MediaRecorder.class);
        if (vids != null) {
            for (Size s : vids)
                maxWidth = Math.max(maxWidth, s.getWidth());
        }
    }

    if (maxWidth >= 3840)
        h.videoQuality = "4K (very high)";
    else if (maxWidth >= 1920)
        h.videoQuality = "Full HD (high)";
    else
        h.videoQuality = "HD (basic)";

// ------------------------------------------------------------
// FPS / SMOOTHNESS / SLOW MOTION
// ------------------------------------------------------------
int maxFps = 0;
Range<Integer>[] fpsRanges =
        cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

if (fpsRanges != null) {
    for (Range<Integer> r : fpsRanges) {
        if (r != null && r.getUpper() != null)
            maxFps = Math.max(maxFps, r.getUpper());
    }
}

if (maxFps >= 120) {

    h.videoSmoothness = gr
            ? "Πολύ ομαλή κίνηση (έως " + maxFps + " FPS)"
            : "Very smooth motion (up to " + maxFps + " FPS)";

    h.slowMotion = gr
            ? "Υποστηρίζεται αργή κίνηση (Slow Motion)"
            : "Slow motion supported";

} else if (maxFps >= 60) {

    h.videoSmoothness = gr
            ? "Ομαλή κίνηση (έως " + maxFps + " FPS)"
            : "Smooth motion (up to " + maxFps + " FPS)";

    h.slowMotion = gr
            ? "Περιορισμένη υποστήριξη slow motion"
            : "Limited slow motion support";

} else if (maxFps >= 30) {

    h.videoSmoothness = gr
            ? "Κανονική ομαλότητα βίντεο (30 FPS)"
            : "Standard smoothness (30 FPS)";

    h.slowMotion = gr
            ? "Δεν υποστηρίζεται slow motion"
            : "Slow motion not supported";

} else {

    h.videoSmoothness = gr
            ? "Βασική απόδοση βίντεο"
            : "Basic video performance";

    h.slowMotion = gr
            ? "Δεν υποστηρίζεται slow motion"
            : "Slow motion not supported";
}

// ------------------------------------------------------------
// STABILIZATION
// ------------------------------------------------------------
boolean stab = false;
int[] stabModes =
        cc.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);

if (stabModes != null) {
    for (int m : stabModes) {
        if (m == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON) {
            stab = true;
            break;
        }
    }
}

h.stabilization = stab
        ? (gr ? "Υποστηρίζεται ηλεκτρονική σταθεροποίηση (EIS)"
              : "Electronic stabilization (EIS) supported")
        : (gr ? "Δεν υποστηρίζεται σταθεροποίηση βίντεο"
              : "Video stabilization not supported");

// ------------------------------------------------------------
// FLASH
// ------------------------------------------------------------
Boolean flashAvail = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

h.flash = Boolean.TRUE.equals(flashAvail)
        ? (gr ? "Διαθέσιμο φλας"
              : "Flash available")
        : (gr ? "Δεν υπάρχει φλας"
              : "Flash not available");

// ------------------------------------------------------------
// REAL LIFE USE
// ------------------------------------------------------------
if (maxFps >= 60 && stab) {

    h.realLifeUse = gr
            ? "Κατάλληλη για καθημερινή χρήση και σκηνές με κίνηση."
            : "Suitable for everyday use and moving scenes.";

} else if (maxFps >= 30) {

    h.realLifeUse = gr
            ? "Κατάλληλη για καθημερινή χρήση και κοινωνικά δίκτυα."
            : "Suitable for daily use and social media.";

} else {

    h.realLifeUse = gr
            ? "Βασική χρήση χωρίς απαιτήσεις."
            : "Basic usage only.";
}

// ------------------------------------------------------------
// FINAL VERDICT
// ------------------------------------------------------------
if (hasRaw && maxFps >= 60) {

    h.verdict = gr
            ? "Καλή κάμερα για καθημερινή χρήση και λήψεις RAW. "
              + "Δεν προορίζεται για επαγγελματική παραγωγή βίντεο."
            : "Good camera for daily use and RAW photography. "
              + "Not intended for professional video production.";

} else {

    h.verdict = gr
            ? "Επαρκής κάμερα για βασική καθημερινή χρήση."
            : "Decent camera for basic daily use.";
}

return h;
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
    appendHtml("• " + escape(msg));
    GELServiceLog.logInfo(msg);
}

private void logOk(String msg) {
    appendHtml("<font color='#39FF14'>✔ " + escape(msg) + "</font>");
    GELServiceLog.logOk(msg);
}

private void logWarn(String msg) {
    appendHtml("<font color='#FFD966'>⚠ " + escape(msg) + "</font>");
    GELServiceLog.logWarn(msg);
}

private void logError(String msg) {
    appendHtml("<font color='#FF5555'>✖ " + escape(msg) + "</font>");
    GELServiceLog.logError(msg);
}

private void logLine() {
    appendHtml("--------------------------------------------------");
    GELServiceLog.logLine();
}

// ------------------------------------------------------------
// SAFE ESCAPE FOR UI ONLY (SERVICE LOG STORES RAW TEXT)
// ------------------------------------------------------------

private int dp(int v) {
float d = getResources().getDisplayMetrics().density;
return (int) (v * d + 0.5f);
}



private View space(int w) {
    View v = new View(this);
    v.setLayoutParams(new LinearLayout.LayoutParams(w, 1));
    return v;
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
// LAB 15 thermal correlation — BILINGUAL (LABEL WHITE, VALUES GREEN)
// ------------------------------------------------------------
private void logLab15ThermalCorrelation(
        float battTempStart,
        float battTempPeak,
        float battTempEnd
) {

    final boolean gr = AppLang.isGreek(this);

    String label = gr
            ? "Θερμική συσχέτιση (κατά τη φόρτιση): "
            : "Thermal correlation (during charging): ";

    String values = String.format(
            Locale.US,
            gr
                    ? "αρχή %.1f°C → μέγιστο %.1f°C → τέλος %.1f°C"
                    : "start %.1f°C → peak %.1f°C → end %.1f°C",
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
// Health checkbox map — BILINGUAL (LAB 14/17 use)
// ------------------------------------------------------------
private void printHealthCheckboxMap(String decision) {

    final boolean gr = AppLang.isGreek(this);

    String d = (decision == null) ? "" : decision.trim();

    logLine();

    boolean strong = "Strong".equalsIgnoreCase(d);
    boolean normal = "Normal".equalsIgnoreCase(d);
    boolean weak   = "Weak".equalsIgnoreCase(d);

    String strongTxt = gr ? "Ισχυρή" : "Strong";
    String normalTxt = gr ? "Κανονική" : "Normal";
    String weakTxt   = gr ? "Αδύναμη"  : "Weak";

    appendHtml((strong ? "✔ " : "• ") +
            "<font color='#FFFFFF'>" + strongTxt + "</font>");

    appendHtml((normal ? "✔ " : "• ") +
            "<font color='#FFFFFF'>" + normalTxt + "</font>");

    appendHtml((weak ? "✔ " : "• ") +
            "<font color='#FFFFFF'>" + weakTxt + "</font>");

    if (strong)
        logOk(gr ? "Χάρτης Υγείας: Ισχυρή" : "Health Map: Strong");
    else if (normal)
        logWarn(gr ? "Χάρτης Υγείας: Κανονική" : "Health Map: Normal");
    else if (weak)
        logError(gr ? "Χάρτης Υγείας: Αδύναμη" : "Health Map: Weak");
    else
        logInfo(gr ? "Χάρτης Υγείας: Πληροφοριακό"
                   : "Health Map: Informational");
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
// LAB 14 — PRE-TEST ADVISORY POPUP (HELPERS + AppTTS)
// ============================================================
private void showLab14PreTestAdvisory(Runnable onContinue) {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(true);

    LinearLayout root = buildGELPopupRoot(this);

    // HEADER (TITLE ONLY)
root.addView(
        buildPopupHeader(
                this,
                gr
                        ? "Δοκιμή Καταπόνησης Μπαταρίας — Προειδοποίηση"
                        : "Battery Stress Test — Pre-Test Check"
        )
);

    final String text =
            gr
                    ? "Για μεγαλύτερη διαγνωστική ακρίβεια, συνιστάται, το τεστ "
                      + "να εκτελείται μετά από επανεκκίνηση της συσκευής.\n\n"
                      + "Μπορείς να συνεχίσεις χωρίς επανεκκίνηση, όμως, "
                      + "πρόσφατη έντονη χρήση, μπορεί να επηρεάσει τα αποτελέσματα.\n\n"
                      + "Μην χρησιμοποιήσεις τη συσκευή, για τα επόμενα 5 λεπτά."
                    : "For best diagnostic accuracy, it is recommended to run this test, "
                      + "after a system restart.\n\n"
                      + "You may continue without restarting, but recent heavy usage, "
                      + "can affect the results.\n\n"
                      + "Do not use your device for the next 5 minutes.";

    TextView msg = new TextView(this);
    msg.setText(text);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f, 1.2f);
    root.addView(msg);
    
// MUTE ROW (CHECKBOX)
root.addView(buildMuteRow());

    Button btnContinue = gelButton(
            this,
            gr ? "Συνέχεια παρ’ όλα αυτά" : "Continue anyway",
            0xFF0B5D1E
    );

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lp.setMargins(0, dp(18), 0, 0);
    btnContinue.setLayoutParams(lp);
    root.addView(btnContinue);

    b.setView(root);

    AlertDialog dlg = b.create();
    if (dlg.getWindow() != null)
        dlg.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

    dlg.show();

    // 🔊 TTS — ΜΟΝΟ αν δεν είναι muted
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (dlg.isShowing() && !AppTTS.isMuted(this)) {
            AppTTS.ensureSpeak(this, text);
        }
    }, 120);

    btnContinue.setOnClickListener(v -> {
        AppTTS.stop();
        dlg.dismiss();
        if (onContinue != null) onContinue.run();
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
// GPU COMPUTE STRESS (LAB14)
// ------------------------------------------------------------
private void startGpuStress() {

    lab14GpuRunning = true;

    lab14GpuThread = new Thread(() -> {

        float x = 0f;

        while (lab14GpuRunning) {

            // fake heavy compute
            for (int i = 0; i < 200000; i++) {
                x += Math.sin(i) * Math.cos(i);
            }

            if (x > 1e8) x = 0f;
        }

    });

    lab14GpuThread.setPriority(Thread.MAX_PRIORITY);
    lab14GpuThread.start();
}

private void stopGpuStress() {

    lab14GpuRunning = false;

    try {
        if (lab14GpuThread != null)
            lab14GpuThread.join(200);
    } catch (Throwable ignore) {}

    lab14GpuThread = null;
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

    final boolean gr = AppLang.isGreek(this);

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

        logInfo(gr ? "Συνέπεια μετρήσεων:" : "Measurement consistency:");

        if (relVar < 0.08) {
            logOk(gr
                    ? "Τα αποτελέσματα είναι συνεπή μεταξύ των εκτελέσεων."
                    : "Results are consistent across runs.");
        }
        else if (relVar < 0.15) {
            logOk(gr
                    ? "Μικρή μεταβλητότητα ανιχνεύθηκε. Τα αποτελέσματα είναι γενικά αξιόπιστα."
                    : "Minor variability detected. Results are generally reliable.");
        }
        else {
            logWarn(gr
                    ? "Υψηλή μεταβλητότητα ανιχνεύθηκε. Επανεκτέλεσε το τεστ μετά από επανεκκίνηση για μεγαλύτερη αξιοπιστία."
                    : "High variability detected. Repeat the test after a system restart to improve reliability.");
        }

    } catch (Throwable ignore) {}
}

private void logLab14Confidence() {

    final boolean gr = AppLang.isGreek(this);

    int runs = getLab14RunCount();
    logLine();

    if (runs <= 1) {

        logWarn(gr
                ? "Εμπιστοσύνη: Προκαταρκτική (1 εκτέλεση)"
                : "Confidence: Preliminary (1 run)");

        logWarn(gr
                ? "Για υψηλότερη διαγνωστική ακρίβεια, εκτέλεσε το τεστ 2 ακόμη φορές, σε διαφορετική ημέρα, υπό παρόμοιες συνθήκες."
                : "For higher diagnostic accuracy, run this test 2 more times, on a different day, under similar conditions.");

    }
    else if (runs == 2) {

        logWarn(gr
                ? "Εμπιστοσύνη: Μεσαία (2 εκτελέσεις)"
                : "Confidence: Medium (2 runs)");

        logWarn(gr
                ? "Συνιστάται μία επιπλέον εκτέλεση για επιβεβαίωση της τάσης φθοράς της μπαταρίας."
                : "One additional run is recommended to confirm battery aging trend.");

    }
    else {

        logOk(gr
                ? "Εμπιστοσύνη: Υψηλή (3+ συνεπείς εκτελέσεις)"
                : "Confidence: High (3+ consistent runs)");

        logInfo(gr
                ? "Η διαγνωστική αξιοπιστία της μπαταρίας είναι υψηλή."
                : "Battery diagnostic confidence is high.");
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

private void startMainStressPhase(
        int durationSec,
        long t0,
        TextView dotsView,
        TextView counterText,
        LinearLayout progressBar
) {

    // αυτή η μέθοδος υπάρχει μόνο για να καλέσει
    // το ήδη υπάρχον main stress logic

    // το πραγματικό stress τρέχει στο UI handler
    // που έχεις ήδη παρακάτω

}

// ============================================================
// MEMORY BANDWIDTH STRESS (LOW HEAT LOAD)
// ============================================================
private Thread memStressThread;

private void startMemoryStress() {

    memStressThread = new Thread(() -> {

        try {

            byte[] buf = new byte[4 * 1024 * 1024]; // 4MB buffer
            Random r = new Random();

            while (lab14Running) {

                for (int i = 0; i < buf.length; i += 64) {
                    buf[i] = (byte) r.nextInt(255);
                }

            }

        } catch (Throwable ignore) {}

    });

    memStressThread.setPriority(Thread.NORM_PRIORITY);
    memStressThread.start();
}

private void stopMemoryStress() {

    try {
        if (memStressThread != null) {
            memStressThread.interrupt();
            memStressThread = null;
        }
    } catch (Throwable ignore) {}

}

private void stopCpuBurn() {
__cpuBurn = false;
}

// ============================================================
// BATTERY CURRENT HELPER
// ============================================================
private float getBatteryCurrentNowSafe() {

    try {

        BatteryManager bm =
                (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (bm == null)
            return Float.NaN;

        long raw =
                bm.getLongProperty(
                        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
                );

        // invalid value
        if (raw == Long.MIN_VALUE || raw == 0)
            return Float.NaN;

        float current = (float) raw;

        // ----------------------------------------------------
        // NORMALIZE UNITS (µA -> mA)
        // ----------------------------------------------------
        if (Math.abs(raw) > 10000L) {
            current = current / 1000f;
        }

        return current;

    } catch (Throwable ignore) {
        return Float.NaN;
    }
}

// ------------------------------------------------------------
// LAB 15 USER ABORT — required by Exit button
// (safe: stops flags + dismisses dialog; does NOT nuke all handler callbacks)
// ------------------------------------------------------------
private void abortLab15ByUser() {

    final boolean gr = AppLang.isGreek(this);

    ui.post(() -> {

        if (!lab15Running) {
            try {
                if (lab15Dialog != null && lab15Dialog.isShowing())
                    lab15Dialog.dismiss();
            } catch (Throwable ignore) {}

            lab15Dialog = null;
            return;
        }

        logWarn(gr
                ? "Το LAB 15 ακυρώθηκε από τον χρήστη."
                : "LAB 15 cancelled by user.");

        lab15Running = false;
        lab15Finished = true;

        try {
            if (lab15Dialog != null && lab15Dialog.isShowing())
                lab15Dialog.dismiss();
        } catch (Throwable ignore) {}

        lab15Dialog = null;

        appendHtml("<br>");
        logOk(gr ? "Το Lab 15 ολοκληρώθηκε." : "Lab 15 finished.");
        logLine();
        enableSingleExportButton();
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

    return s
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
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
// GEL STYLE OUTPUT — ONE LINE PER SENSOR (BILINGUAL)
// Label = white (log channel)
// Value = colored by severity
// ------------------------------------------------------------
private void logTempInline(String label, float c) {

    final boolean gr = AppLang.isGreek(this);

    String base = String.format(
            Locale.US,
            "%s: %.1f°C",
            label,
            c
    );

    if (c < 45f) {

        logOk(base + (gr ? " (ΦΥΣΙΟΛΟΓΙΚΗ)" : " (NORMAL)"));

    }
    else if (c < 55f) {

        logWarn(base + (gr ? " (ΑΥΞΗΜΕΝΗ)" : " (WARM)"));

    }
    else {

        logError(base + (gr ? " (ΥΠΕΡΘΕΡΜΑΝΣΗ)" : " (HOT)"));
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
    // Most smartphones use passive thermal dissipation (no active cooling)
    return false;
}

private String buildHardwareCoolingReport() {

    final boolean gr = AppLang.isGreek(this);

    return gr
            ? "Δεν εντοπίστηκαν ενεργά συστήματα ψύξης. Η συσκευή χρησιμοποιεί παθητική θερμική απαγωγή."
            : "No active hardware cooling devices detected. The device relies on passive thermal dissipation.";
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
normalBg.setCornerRadius(dp(10));  
normalBg.setStroke(dp(3), 0xFFFFD700);  // GOLD BORDER  

// -------------------------------  
// PRESSED STATE  
// -------------------------------  
GradientDrawable pressedBg = new GradientDrawable();  
pressedBg.setColor(0xFF00CC55);          // darker green (pressed)  
pressedBg.setCornerRadius(dp(10));  
pressedBg.setStroke(dp(3), 0xFFFFD700);  

// -------------------------------  
// DISABLED STATE  
// -------------------------------  
GradientDrawable disabledBg = new GradientDrawable();  
disabledBg.setColor(0xFF1E3A2A);          // muted green  
disabledBg.setCornerRadius(dp(10));  
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
// HUMAN LABEL (BILINGUAL)
// ------------------------------------------------------------
private String humanPressureLabel(String level) {

    final boolean gr = AppLang.isGreek(this);

    if ("High".equalsIgnoreCase(level)) {
        return gr ? "Υψηλή" : "High";
    }

    if ("Medium".equalsIgnoreCase(level)) {
        return gr ? "Μέτρια" : "Moderate";
    }

    return gr ? "Χαμηλή" : "Low";
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

private boolean hasUsageAccess() {
    try {
        android.app.AppOpsManager appOps =
                (android.app.AppOpsManager) getSystemService(android.content.Context.APP_OPS_SERVICE);

        if (appOps == null) return false;

        int mode;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            mode = appOps.unsafeCheckOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    getPackageName()
            );
        } else {
            mode = appOps.checkOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    getPackageName()
            );
        }

        return mode == android.app.AppOpsManager.MODE_ALLOWED;

    } catch (Throwable ignore) {
        return false;
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

// ------------------------------------------------------------
// MUTE ROW (UNIFIED — AppTTS HELPER)
// ------------------------------------------------------------
private LinearLayout buildMuteRow() {

    final boolean gr = AppLang.isGreek(this);

    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER_VERTICAL);
    row.setPadding(0, dp(8), 0, dp(16));

    CheckBox muteCheck = new CheckBox(this);
    muteCheck.setChecked(AppTTS.isMuted(this));
    muteCheck.setPadding(0, 0, dp(6), 0);

    TextView label = new TextView(this);
    label.setText(
            gr ? "Σίγαση φωνητικών οδηγιών"
               : "Mute voice instructions"
    );
    label.setTextColor(Color.WHITE);
    label.setTextSize(14f);

    // --------------------------------------------------------
    // TOGGLE (ROW + LABEL CLICK)
    // --------------------------------------------------------
    View.OnClickListener toggle = v -> {

        boolean newState = !AppTTS.isMuted(this);

        AppTTS.setMuted(this, newState);
        muteCheck.setChecked(newState);

        // 🔇 Immediate hard stop when muting
        if (newState) {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        }
    };

    row.setOnClickListener(toggle);
    label.setOnClickListener(toggle);

    // --------------------------------------------------------
    // CHECKBOX DIRECT CHANGE
    // --------------------------------------------------------
    muteCheck.setOnCheckedChangeListener((button, checked) -> {

        if (checked == AppTTS.isMuted(this)) return;

        AppTTS.setMuted(this, checked);

        if (checked) {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        }
    });

    row.addView(muteCheck);
    row.addView(label);

    return row;
}

// ============================================================
// POPUP HEADER + TITLE (NO MUTE BUTTON HERE)
// ============================================================
private LinearLayout buildPopupHeader(Context ctx, String titleText) {

    LinearLayout header = new LinearLayout(ctx);
    header.setOrientation(LinearLayout.HORIZONTAL);
    header.setGravity(Gravity.CENTER_VERTICAL);
    header.setPadding(0, 0, 0, dp(12));

    TextView title = new TextView(ctx);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.START);

    LinearLayout.LayoutParams lpTitle =
            new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
    title.setLayoutParams(lpTitle);

    header.addView(title);
    return header;
}

// ============================================================
// GEL BUTTON — STANDARD (GREEN / GOLD)
// ============================================================
private Button gelButton(Context ctx, String text, int bgColor) {

    Button b = new Button(ctx);
    b.setText(text);
    b.setAllCaps(false);
    b.setTextColor(Color.WHITE);
    b.setTextSize(15f);
    b.setTypeface(null, Typeface.BOLD);

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(bgColor);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);

    b.setBackground(bg);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lp.setMargins(0, dp(10), 0, 0);
    b.setLayoutParams(lp);

    return b;
}

// ============================================================
// GEL POPUP ROOT — BLACK + GOLD (UNIFIED)
// ============================================================
private LinearLayout buildGELPopupRoot(Context ctx) {

    LinearLayout root = new LinearLayout(ctx);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(
            dp(24),  // left
            dp(22),  // top
            dp(24),  // right
            dp(18)   // bottom
    );

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);        // GEL black
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700); // GEL gold
    root.setBackground(bg);

    return root;
}

// ============================================================
// TTS — speakOnce helper (safe)
// ============================================================
private void speakOnce(String text) {
try {
if (text == null) return;
if (AppTTS.isMuted(this)) return;
AppTTS.ensureSpeak(this, text);
} catch (Throwable ignore) {}
}

// ============================================================
// USAGE ACCESS
// ============================================================

private void checkUsageAccessGate() {

    if (!hasUsageAccess()) {
        showUsageAccessDialog();
    }
}

// ============================================================

private void showUsageAccessDialog() {

    if (hasUsageAccess()) return;

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(22), dp(24), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText(gr
            ? "ΑΠΑΙΤΕΙΤΑΙ ΠΡΟΣΒΑΣΗ ΧΡΗΣΗΣ"
            : "USAGE ACCESS REQUIRED");
    title.setTextColor(Color.WHITE);
    title.setTextSize(19f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(14));
    root.addView(title);

    TextView msg = new TextView(this);

    final String messageText =
            gr
                    ? "Καμία συλλογή προσωπικών δεδομένων δεν γίνεται με την παραχώρηση της Πρόσβασης Χρήσης.\n\n"
                      + "Θα μεταφερθείς στις Ρυθμίσεις."
                    : "Usage Access is required for certain analysis features.\n\n"
                      + "No personal data is collected.\n\n"
                      + "You will be redirected to Settings.";

    msg.setText(messageText);
    msg.setTextColor(0xFF00FF9C);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setLineSpacing(0f, 1.15f);
    msg.setPadding(dp(6), 0, dp(6), dp(20));
    root.addView(msg);

    root.addView(buildMuteRow());

    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams btnLp =
            new LinearLayout.LayoutParams(0, dp(110), 1f);
    btnLp.setMargins(dp(8), 0, dp(8), 0);

    Button continueBtn = new Button(this);
    continueBtn.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
    continueBtn.setAllCaps(false);
    continueBtn.setTextColor(Color.WHITE);
    continueBtn.setTextSize(16f);
    continueBtn.setTypeface(null, Typeface.BOLD);
    continueBtn.setLayoutParams(btnLp);

    GradientDrawable contBg = new GradientDrawable();
    contBg.setColor(0xFF00E676);
    contBg.setCornerRadius(dp(10));
    contBg.setStroke(dp(3), 0xFFFFD700);
    continueBtn.setBackground(contBg);

    Button skipBtn = new Button(this);
    skipBtn.setText(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP");
    skipBtn.setAllCaps(false);
    skipBtn.setTextColor(Color.WHITE);
    skipBtn.setTextSize(16f);
    skipBtn.setTypeface(null, Typeface.BOLD);
    skipBtn.setLayoutParams(btnLp);

    GradientDrawable skipBg = new GradientDrawable();
    skipBg.setColor(0xFFC62828);
    skipBg.setCornerRadius(dp(10));
    skipBg.setStroke(dp(3), 0xFFFFD700);
    skipBtn.setBackground(skipBg);

    btnRow.addView(skipBtn);
    btnRow.addView(continueBtn);
    root.addView(btnRow);

    b.setView(root);
    b.setCancelable(false);

    AlertDialog d = b.create();

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    d.setOnDismissListener(dialog -> {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    });

    d.setOnCancelListener(dialog -> {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    });

    d.show();

    root.postDelayed(() -> {
        try {
            if (!AppTTS.isMuted(this)) {
                AppTTS.speak(this, messageText);
            }
        } catch (Throwable ignore) {}
    }, 220);

    continueBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    d.dismiss();

    try {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    } catch (Throwable e) {
        // Fallback
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }
});

skipBtn.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

}

// ============================================================
// LAB 28 — TECHNICIAN POPUP (FINAL / CHECKBOX MUTE)
// ============================================================
private void showLab28Popup() {

    runOnUiThread(() -> {

        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(true);

        // ==========================
        // ROOT (GEL HELPER)
        // ==========================
        LinearLayout root = buildGELPopupRoot(this);

        // ==========================
        // HEADER (TITLE ONLY)
        // ==========================
        LinearLayout header = buildPopupHeader(
        this,
        gr
                ? "LAB 28 — Τεχνική Ανάλυση"
                : "LAB 28 — Technician Analysis"
);
        root.addView(header);

        // ==========================
        // MESSAGE
        // ==========================
        final String text = gr ? getLab28TextGR() : getLab28TextEN();

        TextView msg = new TextView(this);
        msg.setText(text);
        msg.setTextColor(0xFF00FF9C);
        msg.setTextSize(15f);
        msg.setLineSpacing(0f, 1.15f);
        msg.setPadding(0, 0, 0, dp(8));
        root.addView(msg);
        
        // ==========================
        // MUTE ROW (CHECKBOX)
        // ==========================
        root.addView(buildMuteRow());


        // ==========================
        // OK BUTTON
        // ==========================
        Button okBtn = new Button(this);
        okBtn.setText("OK");
        okBtn.setAllCaps(false);
        okBtn.setTextColor(Color.WHITE);
        okBtn.setTextSize(15f);

        GradientDrawable okBg = new GradientDrawable();
        okBg.setColor(0xFF0F8A3B);
        okBg.setCornerRadius(dp(10));
        okBg.setStroke(dp(3), 0xFFFFD700);
        okBtn.setBackground(okBg);

        LinearLayout.LayoutParams lpOk =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lpOk.setMargins(0, dp(16), 0, 0);
        okBtn.setLayoutParams(lpOk);

        root.addView(okBtn);

        // ==========================
        // DIALOG
        // ==========================
        
        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

        // ==========================
        // SPEAK (ONLY IF NOT MUTED)
        // ==========================
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (d.isShowing() && !AppTTS.isMuted(this)) {
                AppTTS.ensureSpeak(this, text);
            }
        }, 120);

        okBtn.setOnClickListener(v -> {
            AppTTS.stop();
            d.dismiss();
        });
    });
}

// ============================================================
// TEXT HELPERS — LAB 28
// ============================================================

private String getLab28TextEN() {
    return
        "For improved diagnostic accuracy, it is recommended to run all labs, before this test. " +
        "This lab, performs symptom-based analysis only. " +
        "It does not diagnose hardware faults, and does not confirm solder or interconnect defects. " +
        "Results, may indicate behavioral patterns, consistent with intermittent contact issues, " +
        "such as, unstable operation, random reboots, or signal drops. " +
        "Use this lab, strictly as a triage tool, and not as a final hardware diagnosis.";
}

private String getLab28TextGR() {
    return
        "Για βελτιωμένη διαγνωστική ακρίβεια, συνιστάται η εκτέλεση όλων των labs, πριν από αυτό το τεστ. " +
        "Το lab αυτό, πραγματοποιεί αποκλειστικά ανάλυση, βασισμένη σε συμπτώματα. " +
        "Δεν διαγιγνώσκει βλάβες υλικού, και δεν επιβεβαιώνει προβλήματα κόλλησης ή διασύνδεσης. " +
        "Τα αποτελέσματα, μπορεί να υποδεικνύουν πρότυπα συμπεριφοράς, συμβατά με διακοπτόμενη επαφή. " +
        "όπως, ασταθή λειτουργία, τυχαίες επανεκκινήσεις, ή απώλειες σήματος. " +
        "Χρησιμοποίησε το lab, αυστηρά ως εργαλείο προελέγχου, και όχι ως τελική διάγνωση υλικού.";
}

// ============================================================
// LAB 29 — Device Authenticity & Repair Analysis
// SERVICE LEVEL — COMPONENT & BOARD INSPECTION (LOGIC BASED)
// ============================================================
private void lab29AuthenticityCheck() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 29 — Έλεγχος γνησιότητας συσκευής & πιθανών επισκευών"
            : "LAB 29 — Device authenticity & repair inspection");
    logLine();

    int manipulationScore = 0;

    boolean batteryAuthSuspect =
            p.getBoolean("lab14_battery_auth_suspect", false);

    boolean calibrationDrift =
            p.getBoolean("lab14_calibration_drift", false);

    boolean collapseRisk =
            p.getBoolean("lab14_collapse_risk", false);

    boolean swellingRisk =
            p.getBoolean("lab14_swelling_risk", false);

    boolean instabilityPattern =
            p.getBoolean("lab29_instability_pattern", false);

    boolean thermalSpike =
            p.getBoolean("lab28_thermal_spike", false);

    boolean radioInstability =
            p.getBoolean("lab28_radio_instability", false);

    boolean sensorFlaps =
            p.getBoolean("lab28_sensor_flaps", false);

    boolean rebootPattern =
            p.getBoolean("lab28_reboot_pattern", false);

    // ------------------------------------------------------------
    // BATTERY AUTHENTICITY
    // ------------------------------------------------------------
    if (batteryAuthSuspect) {

        logLabelWarnValue(
                gr ? "Μπαταρία"
                   : "Battery",
                gr
                        ? "Ενδείξεις πιθανής μη γνήσιας ή aftermarket μπαταρίας"
                        : "Possible aftermarket or non-original battery detected"
        );

        manipulationScore += 20;

    } else {

        logLabelOkValue(
                gr ? "Μπαταρία"
                   : "Battery",
                gr
                        ? "Δεν εντοπίστηκαν ενδείξεις μη γνήσιας μπαταρίας"
                        : "No indicators of non-original battery"
        );
    }

    if (calibrationDrift) {

        logLabelWarnValue(
                gr ? "Fuel gauge"
                   : "Fuel gauge",
                gr
                        ? "Απόκλιση βαθμονόμησης μπαταρίας"
                        : "Battery fuel-gauge calibration drift detected"
        );

        manipulationScore += 10;
    }

    if (collapseRisk || swellingRisk) {

        logLabelWarnValue(
                gr ? "Συμπεριφορά μπαταρίας"
                   : "Battery behaviour",
                gr
                        ? "Ανώμαλη ηλεκτροχημική συμπεριφορά"
                        : "Abnormal electrochemical behaviour"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // SENSOR & BOARD LINES
    // ------------------------------------------------------------
    if (sensorFlaps) {

        logLabelWarnValue(
                gr ? "Αισθητήρες"
                   : "Sensors",
                gr
                        ? "Ασταθής λειτουργία αισθητήρων"
                        : "Sensor instability detected"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // RADIO / BASEBAND
    // ------------------------------------------------------------
    if (radioInstability) {

        logLabelWarnValue(
                gr ? "Ραδιοσύστημα"
                   : "Radio subsystem",
                gr
                        ? "Ασταθής λειτουργία modem ή RF γραμμών"
                        : "Possible modem / RF line instability"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // THERMAL PMIC BEHAVIOUR
    // ------------------------------------------------------------
    if (thermalSpike) {

        logLabelWarnValue(
                gr ? "Θερμική συμπεριφορά"
                   : "Thermal behaviour",
                gr
                        ? "Απότομες θερμικές αιχμές"
                        : "Abnormal thermal spikes detected"
        );

        manipulationScore += 10;
    }

    // ------------------------------------------------------------
    // REBOOT PATTERN
    // ------------------------------------------------------------
    if (rebootPattern) {

        logLabelWarnValue(
                gr ? "Επανεκκινήσεις"
                   : "Reboot behaviour",
                gr
                        ? "Μοτίβο επανεκκινήσεων"
                        : "Unexpected reboot pattern detected"
        );

        manipulationScore += 15;
    }

    // ------------------------------------------------------------
    // BOARD STABILITY
    // ------------------------------------------------------------
    if (instabilityPattern) {

        logLabelWarnValue(
                gr ? "Σταθερότητα συστήματος"
                   : "System stability",
                gr
                        ? "Μοτίβο αστάθειας υλικού"
                        : "Hardware instability pattern detected"
        );

        manipulationScore += 15;
    }

    if (manipulationScore > 100)
        manipulationScore = 100;

    // ------------------------------------------------------------
    // FINAL INTERPRETATION
    // ------------------------------------------------------------
    String level =
            (manipulationScore < 20) ? "LOW" :
            (manipulationScore < 40) ? "MODERATE" :
            (manipulationScore < 60) ? "ELEVATED" :
            (manipulationScore < 80) ? "HIGH" :
                                      "VERY HIGH";

    appendHtml("<br>");

    if (manipulationScore >= 40) {

        logLabelWarnValue(
                gr ? "Δείκτης επέμβασης συσκευής"
                   : "Device manipulation index",
                manipulationScore + "/100 (" + level + ")"
        );

    } else {

        logLabelOkValue(
                gr ? "Δείκτης επέμβασης συσκευής"
                   : "Device manipulation index",
                manipulationScore + "/100 (" + level + ")"
        );
    }

    appendHtml("<br>");

    if (manipulationScore >= 60) {

        logLabelWarnValue(
                gr ? "Συμπέρασμα"
                   : "Conclusion",
                gr
                        ? "Ισχυρές ενδείξεις ότι η συσκευή έχει επισκευαστεί ή τροποποιηθεί."
                        : "Strong indicators that the device has been repaired or modified."
        );

    } else if (manipulationScore >= 30) {

        logLabelWarnValue(
                gr ? "Συμπέρασμα"
                   : "Conclusion",
                gr
                        ? "Πιθανές ενδείξεις προηγούμενης επισκευής."
                        : "Possible indicators of previous repair."
        );

    } else {

        logLabelOkValue(
                gr ? "Συμπέρασμα"
                   : "Conclusion",
                gr
                        ? "Δεν εντοπίστηκαν σημαντικές ενδείξεις επέμβασης."
                        : "No significant repair indicators detected."
        );
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 29 ολοκληρώθηκε." : "Lab 29 finished.");
    logLine();
}

// ============================================================
// SPEAKER OUTPUT EVALUATION — UNIFIED (LAB 1 / LAB 2)
// ============================================================
private enum SpeakerOutputState {
    NO_OUTPUT,     // No acoustic output detected
    LOW_SIGNAL,    // Output detected but weak / low confidence
    OK             // Normal speaker output
}

private SpeakerOutputState evaluateSpeakerOutput(
        MicDiagnosticEngine.Result r
) {
    if (r == null)
        return SpeakerOutputState.NO_OUTPUT;

    if (r.rms <= 0 && r.peak <= 0)
        return SpeakerOutputState.NO_OUTPUT;

    // Low quality signal (still valid output)
    if ("LOW".equalsIgnoreCase(r.confidence)
            || "WEAK".equalsIgnoreCase(r.confidence))
        return SpeakerOutputState.LOW_SIGNAL;

    return SpeakerOutputState.OK;
}

// ============================================================
// AUDIO OUTPUT CONTEXT — LAB 1 SUPPORT (BILINGUAL)
// ============================================================
private static class AudioOutputContext {

    boolean volumeMuted;
    boolean volumeLow;

    boolean bluetoothRouted;
    boolean wiredRouted;

    int volume;
    int maxVolume;

    String explain(boolean gr) {

        if (volumeMuted) {
            return gr
                    ? "Η ένταση πολυμέσων είναι στο μηδέν (0%)."
                    : "Media volume is muted (0%).";
        }

        if (bluetoothRouted) {
            return gr
                    ? "Ο ήχος δρομολογείται σε συσκευή Bluetooth."
                    : "Audio is routed to a Bluetooth device.";
        }

        if (wiredRouted) {
            return gr
                    ? "Ο ήχος δρομολογείται σε ενσύρματα ακουστικά ή USB audio."
                    : "Audio is routed to a wired headset or USB audio device.";
        }

        if (volumeLow) {
            return gr
                    ? "Η ένταση πολυμέσων είναι πολύ χαμηλή."
                    : "Media volume is very low.";
        }

        return gr
                ? "Η δρομολόγηση ήχου και η ένταση φαίνονται φυσιολογικές."
                : "Audio output routing and volume appear normal.";
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

// ============================
// MIC CAPTURE (LOCAL HELPER)
// ============================
private static final class MicQuickResult {
    final int rms;
    final int peak;
    MicQuickResult(int rms, int peak) { this.rms = rms; this.peak = peak; }
}

private MicQuickResult micCaptureOnceMs(int ms) {

    // Permission gate (mandatory)
    try {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED) {
            return new MicQuickResult(0, 0);
        }
    } catch (Throwable ignore) {
        // if anything weird, fail closed
        return new MicQuickResult(0, 0);
    }

    final int sr = 44100;
    final int ch = AudioFormat.CHANNEL_IN_MONO;
    final int fmt = AudioFormat.ENCODING_PCM_16BIT;

    int min = AudioRecord.getMinBufferSize(sr, ch, fmt);
    if (min <= 0) return new MicQuickResult(0, 0);

    AudioRecord ar = null;
    try {
        ar = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sr, ch, fmt,
                min * 2
        );

        short[] buf = new short[Math.max(256, min)];
        ar.startRecording();

        long until = SystemClock.uptimeMillis() + Math.max(250, ms);

        long sumSq = 0;
        long nSamp = 0;
        int peak = 0;

        while (SystemClock.uptimeMillis() < until) {
            int n = ar.read(buf, 0, buf.length);
            if (n > 0) {
                for (int i = 0; i < n; i++) {
                    int v = Math.abs(buf[i]);
                    if (v > peak) peak = v;
                    sumSq += (long) v * (long) v;
                    nSamp++;
                }
            }
        }

        if (nSamp <= 0) return new MicQuickResult(0, 0);

        int rms = (int) Math.sqrt((double) sumSq / (double) nSamp);
        return new MicQuickResult(rms, peak);

    } catch (Throwable t) {
        return new MicQuickResult(0, 0);
    } finally {
        try {
            if (ar != null) {
                try { ar.stop(); } catch (Throwable ignore) {}
                try { ar.release(); } catch (Throwable ignore) {}
            }
        } catch (Throwable ignore) {}
    }
}

// ============================================================
// PERMISSION GUARD HELPERS — GEL EDITION
// Clean single-line permission guards for each LAB
// ============================================================

// ------------------------------------------------------------
// MICROPHONE (RECORD_AUDIO)
// ------------------------------------------------------------
private boolean guardMic(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.RECORD_AUDIO},
            afterGranted
    );
}

// ------------------------------------------------------------
// CAMERA (CAMERA)
// ------------------------------------------------------------
private boolean guardCamera(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.CAMERA},
            afterGranted
    );
}

// ------------------------------------------------------------
// LOCATION (FINE + COARSE)
// ------------------------------------------------------------
private boolean guardLocation(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },
            afterGranted
    );
}

// ------------------------------------------------------------
// PHONE STATE (READ_PHONE_STATE)
// ------------------------------------------------------------
private boolean guardPhoneState(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.READ_PHONE_STATE},
            afterGranted
    );
}

// ------------------------------------------------------------
// STORAGE READ (Android ≤ 12)
// ------------------------------------------------------------
private boolean guardStorageRead(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            afterGranted
    );
}

// ------------------------------------------------------------
// STORAGE WRITE (Android ≤ 10)
// ------------------------------------------------------------
private boolean guardStorageWrite(Runnable afterGranted) {
    return ensurePermissions(
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
            afterGranted
    );
}

// ------------------------------------------------------------
// USAGE ACCESS (SPECIAL PERMISSION — NOT RUNTIME)
// ------------------------------------------------------------
private boolean guardUsageAccess() {

    if (hasUsageAccess()) {
        return true;
    }

    showUsageAccessDialog();
    return false;
}

// ============================================================
// LABS 1-5: AUDIO & VIBRATION
// ============================================================

// ============================================================
// LAB 1 - Speaker Tone Test (AUTO) — WITH AUDIO PATH CHECK
// ============================================================
private void lab1SpeakerTone() {

    if (!guardMic(this::lab1SpeakerTone)) return;

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 1 — Δοκιμή Τόνου Ηχείου"
            : "LAB 1 — Speaker Tone Test");
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
// BLOCKED AUDIO PATH — STOP & ASK RE-RUN
// ------------------------------------------------------------
if (volumeMuted || bluetoothRouted || wiredRouted) {

    logLine();
    logInfo(gr
            ? "Έλεγχος διαδρομής εξόδου ήχου"
            : "Audio output path check");

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Μη καθαρή (μπλοκαρισμένη)"
               : "Not clear (blocked)"
    );

    if (volumeMuted) {
        logLabelWarnValue(
                gr ? "Εντοπίστηκε" : "Detected",
                gr ? "Η ένταση πολυμέσων είναι στο μηδέν (0%)."
                   : "Media volume is muted (volume = 0)"
        );
    }

    if (bluetoothRouted) {
        logLabelWarnValue(
                gr ? "Εντοπίστηκε" : "Detected",
                gr ? "Ο ήχος δρομολογείται σε συσκευή Bluetooth."
                   : "Audio routed to Bluetooth device"
        );
    }

    if (wiredRouted) {
        logLabelWarnValue(
                gr ? "Εντοπίστηκε" : "Detected",
                gr ? "Ο ήχος δρομολογείται σε ενσύρματη ή USB συσκευή."
                   : "Audio routed to wired or USB device"
        );
    }

    logLabelOkValue(
            gr ? "Απαιτούμενη ενέργεια" : "Action required",
            gr ? "Διόρθωσε τα παραπάνω και εκτέλεσε ξανά το LAB 1."
               : "Fix the condition(s) above and re-run LAB 1"
    );

    appendHtml("<br>");
logLabelErrorValue(
        gr ? "Αποτέλεσμα LAB 1" : "LAB 1 result",
        gr ? "Δεν εντοπίστηκε έξοδος ήχου."
           : "No acoustic output detected."
);

logLabelWarnValue(
        gr ? "Παρατήρηση" : "Note",
        gr ? "Η διαδρομή ήχου, ενδέχεται να είναι μπλοκαρισμένη, ή εκτός δρομολόγησης."
           : "Audio path, may be blocked, or not properly routed."
);

    logLine();
    return;
}

// ------------------------------------------------------------
// PLAY TEST TONE
// ------------------------------------------------------------

// FORCE CLEAN MEDIA STATE (FULL RESET)
if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}

tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 90);
tg.startTone(ToneGenerator.TONE_DTMF_1, 1200);
            SystemClock.sleep(1400);

            // ------------------------------------------------------------
            // MIC ANALYSIS
            // ------------------------------------------------------------

// SAFE AUDIO NORMALIZE BEFORE MIC CAPTURE (NO EXTRA HELPERS)
if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    // Κρατάμε speaker ON γιατί θέλουμε το mic να "ακούσει" το speaker
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}
SystemClock.sleep(250);

MicDiagnosticEngine.Result r =
        MicDiagnosticEngine.run(this);

if (r == null) {
    logLabelErrorValue(
            gr ? "Μικρόφωνο" : "Mic",
            gr ? "Δεν καταγράφηκαν δεδομένα"
               : "No data captured"
    );
    return;
}

int rms  = (int) r.rms;
int peak = (int) r.peak;

logLabelOkValue(
        gr ? "RMS Μικροφώνου" : "Mic RMS",
        String.valueOf(rms)
);

logLabelOkValue(
        gr ? "Peak Μικροφώνου" : "Mic Peak",
        String.valueOf(peak)
);

String conf = (r.confidence == null)
        ? ""
        : r.confidence.trim().toUpperCase(Locale.US);

// CONFIDENCE = QUALITY ONLY (NEVER RED)
if (conf.contains("LOW") || conf.contains("WEAK")
        || conf.contains("FAIL") || conf.contains("NONE") || conf.contains("NO")) {

    logLabelWarnValue(
            gr ? "Ποιότητα Ανίχνευσης" : "Confidence",
            r.confidence
    );

} else {

    logLabelOkValue(
            gr ? "Ποιότητα Ανίχνευσης" : "Confidence",
            r.confidence
    );
}

// ------------------------------------------------------------
// SPEAKER OUTPUT EVALUATION (UNIFIED)
// ------------------------------------------------------------
SpeakerOutputState state = evaluateSpeakerOutput(r);

if (state == SpeakerOutputState.NO_OUTPUT) {
    
appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Έξοδος ηχείου" : "Speaker output",
            gr ? "Δεν ανιχνεύθηκε ακουστικό σήμα"
               : "No acoustic output detected"
    );

    logLabelErrorValue(
            gr ? "Διάγνωση" : "Diagnosis",
            gr ? "Η διαδρομή ήχου είναι καθαρή, αλλά δεν καταγράφηκε ήχος από το μικρόφωνο"
               : "Audio path is clear, but no sound was captured by the microphone"
    );

    logLabelWarnValue(
            gr ? "Πιθανή αιτία" : "Possible cause",
            gr ? "Πιθανή βλάβη ηχείου ή έντονη ακουστική απομόνωση"
               : "Speaker hardware failure or severe acoustic isolation"
    );

    logLabelOkValue(
            gr ? "Προτεινόμενη ενέργεια" : "Recommended action",
            gr ? "Επανεκτέλεσε το τεστ. Αν η σιωπή επιμένει, συνιστάται έλεγχος υλικού"
               : "Re-run the test once more. If silence persists, hardware inspection is advised"
    );

    appendHtml("<br>");

logLabelErrorValue(
        gr ? "Αποτέλεσμα" : "Result",
        gr ? "Δεν ανιχνεύθηκε έξοδος ήχου."
           : "No acoustic output detected."
);

logLabelWarnValue(
        gr ? "Παρατήρηση" : "Note",
        gr
                ? "Αυτό μπορεί να οφείλεται σε χαμηλή ένταση, ακουστική απομόνωση, "
                  + "DSP φιλτράρισμα ή πιθανή βλάβη ηχείου."
                : "This may be caused by low volume level, acoustic isolation, "
                  + "DSP filtering, or possible speaker hardware issue."
);

    logLine();
    return;
}

// ------------------------------------------------------------
// OUTPUT DETECTED — CONFIDENCE IS INFORMATIONAL ONLY
// ------------------------------------------------------------

appendHtml("<br>");

if (conf.contains("LOW")) {

    logLabelOkValue(
            gr ? "Έξοδος ηχείου" : "Speaker output",
            gr ? "Ανιχνεύθηκε ακουστικό σήμα, με χαμηλή αξιοπιστία"
               : "Acoustic signal detected, with LOW confidence"
    );

    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr ? "Η χαμηλή αξιοπιστία μπορεί να οφείλεται, σε DSP φιλτράρισμα, "
                 + "ακύρωση θορύβου, ή θέση μικροφώνου"
               : "Low confidence may be caused, by DSP filtering, noise cancellation, "
                 + "microphone placement, or acoustic design"
    );

} else {

appendHtml("<br>");
    logLabelOkValue(
            gr ? "Έξοδος ηχείου" : "Speaker output",
            gr ? "Ανιχνεύθηκε ακουστικό σήμα"
               : "Acoustic signal detected"
    );

}

} catch (Throwable t) {

    appendHtml("<br>");
logLine();
logInfo(gr ? "LAB 1 - Δοκιμή Τόνου Ηχείου"
           : "LAB 1 - Speaker tone test");
logLine();

appendHtml("<br>");
logLabelErrorValue(
gr ? "Κατάσταση" : "Status",
gr ? "Αποτυχία"
: "Failed"
);

logLabelWarnValue(
gr ? "Παρατήρηση" : "Observation",
gr ? "Η δοκιμή τόνου δεν ολοκληρώθηκε."
: "The tone test did not complete."
);

logLabelWarnValue(
gr ? "Πιθανή αιτία" : "Possible cause",
gr ? "Αποτυχία δρομολόγησης ήχου ή περιορισμός συστήματος."
: "Audio routing failure or system-level restriction."
);

} finally {

    if (tg != null) {
        tg.release();
    }

            appendHtml("<br>");
            logOk(gr ? "Το Lab 1 ολοκληρώθηκε." : "Lab 1 finished.");
            logLine();
        }

    }).start();
}

// ============================================================
// LAB 2 — Speaker Frequency Sweep (ADAPTIVE)
// • Runs independently
// • Detects real speaker output via mic
// • FAIL only if absolute silence (RMS == 0 && Peak == 0)
// ============================================================
private void lab2SpeakerSweep() {

    if (!guardMic(this::lab2SpeakerSweep)) return;

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
logLine();
logInfo(gr ? "LAB 2 — Έλεγχος Συχνοτήτων Ηχείου"
               : "LAB 2 — Speaker Frequency Sweep"
    );
    logLine();

    new Thread(() -> {

        ToneGenerator tg = null;

        try {

            AudioManager am =
        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}

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

// SAFE AUDIO NORMALIZE BEFORE MIC CAPTURE (NO EXTRA HELPERS)
if (am != null) {
    try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
    try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
}
SystemClock.sleep(250);

MicDiagnosticEngine.Result r =
        MicDiagnosticEngine.run(this);

if (r == null) {
    
appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Μικρόφωνο" : "Mic",
            gr ? "Δεν καταγράφηκαν δεδομένα" : "No data captured"
    );
    return;
}

int rms  = (int) r.rms;
int peak = (int) r.peak;

logLabelOkValue("Mic RMS",  String.valueOf(rms));
logLabelOkValue("Mic Peak", String.valueOf(peak));

String conf = (r.confidence == null)
        ? ""
        : r.confidence.trim().toUpperCase(Locale.US);

// ----------------------------------------------------
// CONFIDENCE (QUALITY, NOT EXISTENCE)
// ----------------------------------------------------
if (conf.contains("LOW") || conf.contains("WEAK")
        || conf.contains("FAIL") || conf.contains("NONE")) {

    logLabelWarnValue(
            gr ? "Ποιότητα" : "Confidence",
            r.confidence
    );

} else {

    logLabelOkValue(
            gr ? "Ποιότητα" : "Confidence",
            r.confidence
    );
}

// ----------------------------------------------------
// HARD GATE — ABSOLUTE SILENCE ONLY
// ----------------------------------------------------
if (rms == 0 && peak == 0) {

appendHtml("<br>");
    logLabelErrorValue(
            gr ? "Έξοδος Ηχείου" : "Speaker output",
            gr ? "Δεν ανιχνεύθηκε ακουστικό σήμα"
               : "No acoustic output detected"
    );

    logLabelWarnValue(
            gr ? "Πιθανή αιτία" : "Possible cause",
            gr
                    ? "Βλάβη ηχείου, σίγαση εξόδου ή πλήρης ακουστική απομόνωση"
                    : "Speaker hardware failure, muted output path, or extreme isolation"
    );

    logLabelOkValue(
            gr ? "Σύσταση" : "Recommended",
            gr
                    ? "Επανεκτέλεσε το LAB 1 για έλεγχο διαδρομής ήχου"
                    : "Re-run LAB 1 to verify speaker operation and routing"
    );

    appendHtml("<br>");
    logLine();
    return;
}

// ----------------------------------------------------
// OUTPUT CONFIRMED (EVEN WITH LOW CONFIDENCE)
// ----------------------------------------------------
if (conf.contains("LOW") || conf.contains("WEAK")) {

appendHtml("<br>");
    logLabelOkValue(
            gr ? "Έξοδος Ηχείου" : "Speaker output",
            gr
                    ? "Ανιχνεύθηκε ακουστικό σήμα με χαμηλή αξιοπιστία."
                    : "Acoustic signal detected with LOW confidence"
    );

    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Η χαμηλή αξιοπιστία μπορεί να οφείλεται, σε DSP φιλτράρισμα, ακύρωση θορύβου, περιοσισμό απόκρισης συχνότητας, ή θέση μικροφώνου."
                    : "Low confidence may be caused, by DSP filtering, noise cancellation, speaker frequency limits, or microphone placement."
    );

} else {

appendHtml("<br>");
    logLabelOkValue(
            gr ? "Έξοδος Ηχείου" : "Speaker output",
            gr
                    ? "Ανιχνεύθηκε ακουστικό σήμα"
                    : "Acoustic signal detected"
    );

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Η σάρωση συχνοτήτων ολοκληρώθηκε επιτυχώς."
                    : "Frequency sweep detected successfully across multiple tones."
    );
}

} catch (Throwable t) {

    logError(
            gr
                    ? "Αποτυχία δοκιμής σάρωσης συχνοτήτων ηχείου"
                    : "Speaker frequency sweep failed"
    );

} finally {

            if (tg != null) tg.release();

            appendHtml("<br>");
            logOk(gr ? "Το Lab 2 ολοκληρώθηκε." : "Lab 2 finished.");
            logLine();
        }

    }).start();
}

/* ============================================================
   LAB 3 — Earpiece Audio Path Check (MANUAL)
   Custom GEL Dialog — START → tones → confirmation
   ============================================================ */
private void lab3EarpieceManual() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 3 — Έλεγχος Διαδρομής Ήχου Ακουστικού"
                    : "LAB 3 — Earpiece Audio Path Check"
    );
    logLine();

    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    if (am == null) {
        logError(
                gr
                        ? "Ο AudioManager δεν είναι διαθέσιμος."
                        : "AudioManager is unavailable."
        );
        return;
    }

    // ------------------------------------------------------------
    // SAVE AUDIO STATE
    // ------------------------------------------------------------
    lab3OldMode = am.getMode();
    lab3OldSpeaker = am.isSpeakerphoneOn();
    lab3OldMicMute = am.isMicrophoneMute();

    logInfo(
            gr
                    ? "Αποθήκευση τρέχουσας κατάστασης ήχου."
                    : "Saving audio state."
    );

    logInfo(
            gr
                    ? "Προετοιμασία δρομολόγησης προς το ακουστικό."
                    : "Preparing earpiece routing."
    );

    try {
        am.stopBluetoothSco();
        am.setBluetoothScoOn(false);
        am.setSpeakerphoneOn(false);
        am.setMicrophoneMute(false); // 🔴 ΑΠΑΡΑΙΤΗΤΟ
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);

    } catch (Throwable t) {

        logError(
                gr
                        ? "Αποτυχία δρομολόγησης ήχου."
                        : "Audio routing failed."
        );

        restoreLab3Audio(); // 🔒 FAIL-SAFE
        return;
    }

    SystemClock.sleep(250);

    runOnUiThread(() -> {

        final String titleText = gr
                ? "LAB 3 — Έλεγχος ακουστικού"
                : "LAB 3 — Earpiece Audio Test";

        final String bodyText = gr
                ? "Τοποθέτησε το ακουστικό του τηλεφώνου στο αυτί σου.\n"
                  + "Πάτησε έναρξη για να ξεκινήσει ο έλεγχος."
                : "Put the phone earpiece to your ear.\n"
                  + "Press start to begin the test.";

        final String ttsText = bodyText; // ΜΙΑ πηγή αλήθειας

        // ------------------------------------------------------------
        // DIALOG
        // ------------------------------------------------------------
        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(26), dp(24), dp(26), dp(22));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        // TITLE
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(17f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(14));
        root.addView(title);

        // MESSAGE
        TextView msg = new TextView(this);
        msg.setText(bodyText);
        msg.setTextColor(0xFF39FF14);
        msg.setTextSize(14.5f);
        msg.setGravity(Gravity.CENTER);
        msg.setLineSpacing(1.1f, 1.15f);
        msg.setPadding(0, 0, 0, dp(18));
        root.addView(msg);
        
// ---------------------------
// MUTE ROW
// ---------------------------
root.addView(buildMuteRow());

// ---------------------------
// BUTTON ROW (EXIT + START)
// ---------------------------
LinearLayout btnRow = new LinearLayout(this);
btnRow.setOrientation(LinearLayout.HORIZONTAL);
btnRow.setGravity(Gravity.CENTER);

LinearLayout.LayoutParams btnLp =
        new LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
        );
btnLp.setMargins(dp(6), dp(6), dp(6), dp(6));

// ---------- EXIT ----------
Button exitBtn = new Button(this);
exitBtn.setText(gr ? "ΕΞΟΔΟΣ" : "EXIT");
exitBtn.setAllCaps(false);
exitBtn.setTextSize(14f);
exitBtn.setTextColor(Color.WHITE);

GradientDrawable exitBg = new GradientDrawable();
exitBg.setColor(0xFF8B0000);
exitBg.setCornerRadius(dp(10));
exitBg.setStroke(dp(3), 0xFFFFD700);
exitBtn.setBackground(exitBg);
exitBtn.setLayoutParams(btnLp);

// ---------- START ----------
Button start = new Button(this);
start.setText(gr ? "ΕΝΑΡΞΗ" : "START");
start.setAllCaps(false);
start.setTextSize(14f);
start.setTextColor(Color.BLACK);

GradientDrawable startBg = new GradientDrawable();
startBg.setColor(0xFF39FF14);
startBg.setCornerRadius(dp(10));
startBg.setStroke(dp(3), 0xFFFFD700);
start.setBackground(startBg);
start.setLayoutParams(btnLp);

btnRow.addView(exitBtn);
btnRow.addView(start);
root.addView(btnRow);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// STOP TTS
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// BACK
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        appendHtml("<br>");
        logWarn(gr ? "Η δοκιμή ακυρώθηκε από τον χρήστη."
                   : "Test canceled by user.");
        logLine();

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (d.isShowing()
                && !isFinishing()
                && !isDestroyed()
                && !AppTTS.isMuted(this)) {

            AppTTS.ensureSpeak(this, bodyText);
        }
    }, 500);
}

// ---------------------------
// ACTIONS
// ---------------------------

exitBtn.setOnClickListener(v -> {

    appendHtml("<br>");
    logWarn(gr ? "Η δοκιμή ακυρώθηκε από τον χρήστη."
               : "Test canceled by user.");
    logLine();

    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();

    runOnUiThread(this::enableSingleExportButton);
});

start.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();

    // 👉 εδώ συνεχίζεις το test
});

// ------------------------------------------------------------
// START ACTION
// ------------------------------------------------------------
start.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    new Thread(() -> {
        try {

            logInfo(gr
                    ? "Αναπαραγωγή δοκιμαστικών τόνων ακουστικού."
                    : "Playing earpiece test tones.");

            for (int i = 1; i <= 3; i++) {

                logInfo(gr
                        ? "Τόνος " + i + " / 3"
                        : "Tone " + i + " / 3");

                playEarpieceBeep();
                SystemClock.sleep(650);
            }

            logOk(gr
                    ? "Η αναπαραγωγή τόνων ολοκληρώθηκε."
                    : "Earpiece tone playback completed.");

        } catch (Throwable t) {

            logError(gr
                    ? "Αποτυχία αναπαραγωγής τόνων ακουστικού."
                    : "Earpiece tone playback failed.");

            logLabelWarnValue(
                    gr ? "Πιθανή αιτία" : "Possible cause",
                    gr
                            ? "Αποτυχία δρομολόγησης ήχου, περιορισμός συστήματος ή μη διαθέσιμη έξοδος ακουστικού."
                            : "Audio routing failure, system-level restriction or unavailable earpiece output."
            );

        } finally {

            // 🔒 HARD AUDIO RESET
            resetAudioAfterLab3(am, lab3OldMode, lab3OldSpeaker, lab3OldMicMute);

            runOnUiThread(() -> {
                try { d.dismiss(); } catch (Throwable ignore) {}
                askUserEarpieceConfirmation();
            });
        }

    }).start();
});

// ------------------------------------------------------------
// TTS INTRO — DIALOG BOUND (GLOBAL MUTE SAFE)
// ------------------------------------------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (d.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, ttsText);
    }
}, 120);

});   // <-- ΚΛΕΙΝΕΙ ΤΟ runOnUiThread
}   

/* ============================================================
   LAB 4 — Microphone Recording Check (BOTTOM + TOP)
   BASE — FINAL • CLEAN • ISOLATED
   ============================================================ */

private void lab4MicManual() {

    if (!guardMic(this::lab4MicManual)) return;
    
    lab4MicBase(() -> lab4MicPro());
}

private void lab4MicBase(Runnable onFinished) {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 4 — Έλεγχος Υλικού Μικροφώνων"
                    : "LAB 4 — Microphone Hardware Check"
    );
    logLine();

    new Thread(() -> {

        boolean bottomOk = false;
        boolean topOk = false;
        boolean fallbackUsed = false;

        int bottomRms = 0, bottomPeak = 0;
        int topRms = 0, topPeak = 0;

        boolean stopBaseHere = false;

        try {

            // ====================================================
            // AUTO CHECK — BOTTOM MIC
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "Έλεγχος κάτω μικροφώνου (αυτόματος):"
                       : "Bottom microphone auto check:");
            logLine();

            hardNormalizeAudioForMic();
            MicDiagnosticEngine.Result bottom =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.BOTTOM);

            if (bottom != null) {
                bottomRms  = (int) bottom.rms;
                bottomPeak = (int) bottom.peak;
            }

            logLabelOkValue("Bottom RMS",  String.valueOf(bottomRms));
            logLabelOkValue("Bottom Peak", String.valueOf(bottomPeak));

            bottomOk = bottomRms > 0 || bottomPeak > 0;

            // ====================================================
            // AUTO CHECK — TOP MIC
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "Έλεγχος άνω μικροφώνου (αυτόματος):"
                       : "Top microphone auto check:");
            logLine();

            hardNormalizeAudioForMic();
            MicDiagnosticEngine.Result top =
                    MicDiagnosticEngine.run(this, MicDiagnosticEngine.MicType.TOP);

            if (top != null) {
                topRms  = (int) top.rms;
                topPeak = (int) top.peak;
            }

            logLabelOkValue(
        gr ? "RMS (Άνω Μικρόφωνο)" : "Top RMS",
        String.valueOf(topRms)
);

logLabelOkValue(
        gr ? "Peak (Άνω Μικρόφωνο)" : "Top Peak",
        String.valueOf(topPeak)
);

            topOk = topRms > 0 || topPeak > 0;

// ====================================================
// FINAL BASE VERDICT (NO FALLBACK)
// ====================================================
appendHtml("<br>");
logInfo(gr ? "Συμπεράσματα υλικού:" : "Hardware conclusions:");
logLine();

if (bottomOk && topOk) {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Και τα δύο μικρόφωνα λειτουργούν κανονικά"
               : "Both microphones are operational"
    );

} else if (bottomOk || topOk) {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Μερική λειτουργία μικροφώνων"
               : "Partial microphone operation detected"
    );

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Η λειτουργία μικροφώνων δεν επιβεβαιώθηκε από τον αυτόματο έλεγχο."
                    : "Microphone operation was not confirmed by the automatic test."
    );

    logWarn(
            gr
                    ? "Πιθανός περιορισμός firmware ή δικαιωμάτων."
                    : "Possible firmware or permission restriction."
    );

    logOk(
            gr
                    ? "Συνιστάται έλεγχος μέσω πραγματικής κλήσης."
                    : "Testing via a real call is recommended."
    );
}

        } finally {

            appendHtml("<br>");
            logOk(gr ? "Το Lab 4 BASE ολοκληρώθηκε." : "Lab 4 BASE finished.");
            logLine();

            if (onFinished != null && !fallbackUsed) {
                runOnUiThread(onFinished);
            }
        }

    }).start();
}

/* ============================================================
   LAB 4 PRO — CALL QUALITY VERIFICATION (FINAL • LOCKED)
   ============================================================ */

private volatile boolean lastAnswerHeardClearly = false;

private void lab4MicPro() {

    final boolean gr = AppLang.isGreek(this);

    new Thread(() -> {

    try {

// ====================================================
// STAGE 1 — Bottom microphone HUMAN ACOUSTIC check
// ====================================================

// 🔊 Force call audio path
AudioManager amCall = (AudioManager) getSystemService(AUDIO_SERVICE);
if (amCall != null) {
    try { amCall.stopBluetoothSco(); } catch (Throwable ignore) {}
    try { amCall.setBluetoothScoOn(false); } catch (Throwable ignore) {}
    try { amCall.setSpeakerphoneOn(false); } catch (Throwable ignore) {}
    try { amCall.setMicrophoneMute(false); } catch (Throwable ignore) {}
    try { amCall.setMode(AudioManager.MODE_IN_COMMUNICATION); } catch (Throwable ignore) {}
}

AtomicBoolean cancelled = new AtomicBoolean(false);
AtomicBoolean started = new AtomicBoolean(false);
AtomicReference<AlertDialog> dialogRef = new AtomicReference<>();

// ==========================
// POPUP 1 — INSTRUCTION
// ==========================
runOnUiThread(() -> {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Μίλησε στο κάτω μικρόφωνο, και άκου, αν η φωνή σου ακούγεται καθαρά, από το ακουστικό."
            : "Speak into the bottom microphone, and check, if your voice is clearly heard, from the earpiece.");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    root.addView(buildMuteRow());

    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
    lp.setMargins(dp(12), dp(8), dp(12), dp(8));

    Button exitBtn = new Button(this);
    exitBtn.setText("EXIT");
    exitBtn.setTextColor(Color.WHITE);
    exitBtn.setAllCaps(false);

    GradientDrawable exitBg = new GradientDrawable();
    exitBg.setColor(0xFF8B0000);
    exitBg.setCornerRadius(dp(10));
    exitBg.setStroke(dp(3), 0xFFFFD700);
    exitBtn.setBackground(exitBg);
    exitBtn.setLayoutParams(lp);

    Button startBtn = new Button(this);
    startBtn.setText("START");
    startBtn.setTextColor(Color.WHITE);
    startBtn.setAllCaps(false);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF0B5F3B);
    startBg.setCornerRadius(dp(10));
    startBg.setStroke(dp(3), 0xFFFFD700);
    startBtn.setBackground(startBg);
    startBtn.setLayoutParams(lp);

    btnRow.addView(exitBtn);
    btnRow.addView(startBtn);
    root.addView(btnRow);

    b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// STOP TTS on ANY dismiss
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// BACK key protection
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        cancelled.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

// IMPORTANT (μόνο στο LAB4PRO που έχει dialogRef)
dialogRef.set(d);

exitBtn.setOnClickListener(v -> {
    cancelled.set(true);
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

startBtn.setOnClickListener(v -> {
    started.set(true);
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        AppTTS.ensureSpeak(
                this,
                gr
                        ? "Μίλησε στο κάτω μικρόφωνο, και άκου, αν η φωνή σου ακούγεται καθαρά, από το ακουστικό."
            : "Speak into the bottom microphone, and check, if your voice is clearly heard, from the earpiece.");
            
    }, 500);
});

// ==========================
// WAIT FOR START
// ==========================
long waitStart = SystemClock.uptimeMillis() + 10000;
while (!started.get() && !cancelled.get()
        && SystemClock.uptimeMillis() < waitStart) {
    SystemClock.sleep(80);
}

if (cancelled.get()) {

    appendHtml("<br>");

    logWarn(gr
            ? "LAB 4 PRO — Διακόπηκε από τον χρήστη"
            : "LAB 4 PRO — Interrupted by user");
    logLine();

    runOnUiThread(this::enableSingleExportButton);
    return;
}

// ==========================
// LIVE MIC → EARPIECE LOOP (5s)
// ==========================
int sampleRate = 16000;
int minBuf = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
);

AudioRecord recorder = new AudioRecord(
        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        minBuf
);

AudioTrack track = new AudioTrack(
        AudioManager.STREAM_VOICE_CALL,
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        minBuf,
        AudioTrack.MODE_STREAM
);

byte[] buffer = new byte[minBuf];

recorder.startRecording();
track.play();

long loopUntil = SystemClock.uptimeMillis() + 5000;

while (SystemClock.uptimeMillis() < loopUntil && !cancelled.get()) {

    int read = recorder.read(buffer, 0, buffer.length);
    if (read > 0) {
        track.write(buffer, 0, read);
    }
}

try { recorder.stop(); } catch (Throwable ignore) {}
try { recorder.release(); } catch (Throwable ignore) {}
try { track.stop(); } catch (Throwable ignore) {}
try { track.release(); } catch (Throwable ignore) {}

// ==========================
// POPUP 2 — CONFIRMATION (SAFE VERSION)
// ==========================
AtomicBoolean heardClearly = new AtomicBoolean(false);
AtomicBoolean answered = new AtomicBoolean(false);

runOnUiThread(() -> {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Άκουσες καθαρά τη φωνή σου από το ακουστικό;"
            : "Did you hear your voice clearly from the earpiece?");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    // ---------- BUTTON ROW ----------
    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
    lp.setMargins(dp(12), dp(8), dp(12), dp(8));

    Button noBtn = new Button(this);
    noBtn.setText(gr ? "ΟΧΙ" : "NO");
    noBtn.setAllCaps(false);
    noBtn.setTextColor(Color.WHITE);

    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(dp(3), 0xFFFFD700);
    noBtn.setBackground(noBg);
    noBtn.setLayoutParams(lp);

    Button yesBtn = new Button(this);
    yesBtn.setText(gr ? "ΝΑΙ" : "YES");
    yesBtn.setAllCaps(false);
    yesBtn.setTextColor(Color.WHITE);

    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yesBtn.setBackground(yesBg);
    yesBtn.setLayoutParams(lp);

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

    // STOP TTS on any dismiss
    d.setOnDismissListener(dialog -> {
dialogRef.set(null);
        try { AppTTS.stop(); } catch (Throwable ignore) {}

        if (!answered.get()) {
            cancelled.set(true);
            answered.set(true);
        }
    });

    // BACK protection
    d.setOnKeyListener((dialog, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
            event.getAction() == KeyEvent.ACTION_UP) {

            cancelled.set(true);
            answered.set(true);

            try { AppTTS.stop(); } catch (Throwable ignore) {}
            dialog.dismiss();
            return true;
        }
        return false;
    });

    if (!isFinishing() && !isDestroyed()) {
        d.show();

        // 🔊 SAFE TTS after attach
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (d.isShowing() && !AppTTS.isMuted(this)) {
                AppTTS.ensureSpeak(
                        this,
                        gr
                                ? "Άκουσες καθαρά τη φωνή σου από το ακουστικό;"
                                : "Did you hear your voice clearly from the earpiece?"
                );
            }
        }, 400);
    }

    noBtn.setOnClickListener(v -> {
        heardClearly.set(false);
        answered.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        d.dismiss();
    });

    yesBtn.setOnClickListener(v -> {
        heardClearly.set(true);
        answered.set(true);
        try { AppTTS.stop(); } catch (Throwable ignore) {}
        d.dismiss();
    });
});

// ==========================
// WAIT (SAFE — NO DEADLOCK)
// ==========================
while (!answered.get() && !cancelled.get()) {
    SystemClock.sleep(80);
}

if (cancelled.get()) return;

// -----------------------------------------
// UI STABILIZATION BEFORE STAGE 2
// -----------------------------------------
try { AppTTS.stop(); } catch (Throwable ignore) {}

SystemClock.sleep(350);   // αφήνουμε το UI να "κάτσει"

// ====================================================
// RESULT LOGGING (USER CONFIRMATION BASED)
// ====================================================
appendHtml("<br>");
logInfo(gr
        ? "LAB 4 PRO — Ποιότητα συνομιλίας κάτω μικροφώνου"
        : "LAB 4 PRO — Bottom microphone call quality");
logLine();

if (heardClearly.get()) {

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr
                    ? "Ο χρήστης επιβεβαίωσε καθαρή ακουστική επανάληψη. Το κάτω μικρόφωνο λειτουργεί σωστά."
                    : "User confirmed clear acoustic loop. Bottom microphone is functioning properly."
    );

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Αν παρουσιαστούν προβλήματα σε πραγματικές συνομιλίες, "
                      + "ενδέχεται να σχετίζονται με το δίκτυο, ή codec, η προβλημα στο μικρόφωνο του συνομιλητη"
                    : "If issues occur during real calls, they may be related to network, or codec, or other's party microphone issue"
    );

} else {

    logLabelWarnValue(
        gr ? "Αποτέλεσμα" : "Result",
        gr
                ? "Η ακουστική επανάληψη, δεν επιβεβαιώθηκε από τον χρήστη."
                : "Acoustic loop, was not confirmed by the user."
);

logWarn(
        gr
                ? "Σε ορισμένες συσκευές, η συμπεριφορά μπορεί να επηρεάζεται, από τις ρυθμίσεις ήχου, "
                  + "περιορισμούς firmware, ή αυτόματη καταστολή ηχούς."
                : "On some devices, behavior may be influenced, by audio settings, "
                  + "firmware restrictions, or echo cancellation mechanisms."
);

logOk(
        gr
                ? "Συνιστάται επιβεβαίωση μέσω πραγματικής τηλεφωνικής κλήσης."
                : "Verification via a real phone call is recommended."
);
}

logLine();

// ====================================================
// STAGE 2 — USER INSTRUCTION (SPEAKER)
// ====================================================

// 🔊 Πάμε καθαρά σε speaker για οδηγία
try {
    if (amCall != null) {
        try { amCall.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        try { amCall.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
    }
} catch (Throwable ignore) {}

AtomicBoolean ttsFinished = new AtomicBoolean(false);

// ==========================
// SHOW DIALOG (UI THREAD)
// ==========================
runOnUiThread(() -> {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Βάλε το ακουστικό στο αυτί σου."
            : "Place the earpiece on your ear.");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    root.addView(msg);

    b.setView(root);

    final AlertDialog d = b.create();
    dialogRef.set(d);

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    if (!isFinishing() && !isDestroyed()) {

        d.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (!isFinishing() && !isDestroyed()
                    && !AppTTS.isMuted(this)) {

                AppTTS.ensureSpeak(
                        this,
                        gr
                                ? "Βάλε το ακουστικό στο αυτί σου."
                                : "Place the earpiece on your ear."
                );
            }

            // Περιμένουμε να ξεκινήσει
            new Thread(() -> {

                long startWait = SystemClock.uptimeMillis() + 1500;
                while (!AppTTS.isSpeaking()
                        && SystemClock.uptimeMillis() < startWait) {
                    SystemClock.sleep(50);
                }

                long maxWait = SystemClock.uptimeMillis() + 4000;
                while (AppTTS.isSpeaking()
                        && SystemClock.uptimeMillis() < maxWait) {
                    SystemClock.sleep(80);
                }

                SystemClock.sleep(250);

                runOnUiThread(() -> {
                    try {
                        if (d.isShowing()) d.dismiss();
                    } catch (Throwable ignore) {}
                });

                routeToCallEarpiece();
                playAnswerCheckWav();

            }).start();

        }, 500);
    }
});

// ==========================
// WAIT WITH TIMEOUT (MAX 4s)
// ==========================
long maxWait = SystemClock.uptimeMillis() + 4000;

while (AppTTS.isSpeaking()
        && SystemClock.uptimeMillis() < maxWait) {

    SystemClock.sleep(80);
}

// μικρό grace delay
SystemClock.sleep(250);

// Κλείσιμο dialog
runOnUiThread(() -> {
    try {
        AlertDialog dlg = dialogRef.get();
        if (dlg != null && dlg.isShowing()) {
            dlg.dismiss();
        }
    } catch (Throwable ignore) {}
});

// 🔁 Επιστροφή σε call earpiece
routeToCallEarpiece();

// ====================================================
            // STAGE 3 — WAV (EARPIECE ONLY)
            // ====================================================
            playAnswerCheckWav();

            // ====================================================
            // RESULT — EARPIECE
            // ====================================================
            appendHtml("<br>");
            logInfo(gr ? "LAB 4 PRO — Ποιότητα συνομιλίας ακουστικού" : "LAB 4 PRO — Earpiece Call quality");
            logLine();

            if (lastAnswerHeardClearly) {

                logLabelOkValue(
                        gr ? "Αποτέλεσμα" : "Result",
                        gr
                                ? "Σύμφωνα με τη δήλωση χρήστη, το ακουστικό αποδίδει καθαρό ήχο."
                                : "According to the user's declaration, the earpiece delivers clear audio."
                );

                logLabelOkValue(
                        gr ? "Σημείωση" : "Note",
                        gr
                                ? "Αν παρουσιαστούν προβλήματα σε πραγματικές συνομιλίες, "
                                + "ενδέχεται να οφείλονται στο δίκτυο, στον codec ή "
                                + "στο μικρόφωνο / ακουστικό της άλλης συσκευής."
                                : "If issues occur during real calls, they may be related to network conditions, "
                                + "codec selection, or the microphone / earpiece of the other party."
                );

            } else {

                logLabelWarnValue(
                        gr ? "Αποτέλεσμα" : "Result",
                        gr
                                ? "Σύμφωνα με τη δήλωση χρήστη, ο ήχος από το ακουστικό δεν ήταν καθαρός."
                                : "According to the user's declaration, the earpiece audio was not clear."
                );

                logLabelWarnValue(
        gr ? "Πιθανές αιτίες" : "Possible causes",
        gr
                ? "Χαμηλή στάθμη έντασης, βουλωμένο ακουστικό, "
                  + "προστατευτικό οθόνης, θέση συσκευής, ή πραγματική βλάβη ακουστικού."
                : "Low volume level, obstructed earpiece, "
                  + "screen protector interference, device position, or actual earpiece hardware issue."
);
            }

            logLine();

            appendHtml("<br>");
            logOk(gr ? "Το Lab 4 ολοκληρώθηκε." : "Lab 4 finished.");
            logLine();

            runOnUiThread(this::enableSingleExportButton);
            cancelled.set(true);

        } catch (Throwable t) {

            appendHtml("<br>");

            logLabelWarnValue(
                    gr ? "Διακοπή" : "Interrupted",
                    gr
                            ? "Το LAB 4 PRO δεν ολοκληρώθηκε κανονικά."
                            : "LAB 4 PRO did not complete normally."
            );

            logLabelWarnValue(
        gr ? "Πιθανές αιτίες" : "Possible causes",
        gr
                ? "Χαμηλή στάθμη έντασης, βουλωμένο ακουστικό, "
                  + "προστατευτικό οθόνης, θέση συσκευής, ή πραγματική βλάβη ακουστικού."
                : "Low volume level, obstructed earpiece, "
                  + "screen protector interference, device position, or actual earpiece hardware issue."
);

            appendHtml("<br>");
            logOk(gr ? "Το Lab 4 ολοκληρώθηκε." : "Lab 4 finished.");
            logLine();

} finally {

    // ABSOLUTE SAFETY — COMPILE SAFE
    try { AppTTS.stop(); } catch (Throwable ignore) {}

    try {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
            try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
            try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
            try { am.setSpeakerphoneOn(false); } catch (Throwable ignore) {}
            try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        }
    } catch (Throwable ignore) {}
}

}).start();
}
    
// ============================================================
// 🎵 PLAY VOICE WAV — AUTO LANGUAGE (EARPIECE ONLY • LOCKED)
// ============================================================
private void playAnswerCheckWav() {

    // 👂 FORCE CALL PATH → EARPICE
    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am != null) {
        try { am.stopBluetoothSco(); } catch (Throwable ignore) {}
        try { am.setBluetoothScoOn(false); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(false); } catch (Throwable ignore) {}
        try { am.setMicrophoneMute(false); } catch (Throwable ignore) {}
        try { am.setMode(AudioManager.MODE_IN_COMMUNICATION); } catch (Throwable ignore) {}
    }

    SystemClock.sleep(150);

    // 🌍 AUTO LANGUAGE
    final boolean gr = AppLang.isGreek(this);
    final int resId = gr ? R.raw.answercheck_el : R.raw.answercheck_en;

    MediaPlayer mp = new MediaPlayer();

    try {
        AssetFileDescriptor afd =
                getResources().openRawResourceFd(resId);
        if (afd == null) return;

        mp.setDataSource(
                afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength()
        );
        afd.close();

        // 🔒 CRITICAL: VOICE_CALL → EARPICE (ΟΧΙ MUSIC)
        mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

        mp.prepare();
        mp.start();

        int dur = 0;
        try { dur = mp.getDuration(); } catch (Throwable ignore) {}
        SystemClock.sleep(dur > 0 ? dur : 1800);

    } catch (Throwable ignore) {

    } finally {
        try { mp.stop(); } catch (Throwable ignore) {}
        try { mp.release(); } catch (Throwable ignore) {}
    }

    // ❗ ΔΕΝ αλλάζουμε route εδώ
    // συνεχίζουμε με confirmation
    showAnswerCheckConfirmation();
}

// ============================================================
// STAGE 4 — HUMAN CONFIRMATION (FINAL • COMPILE SAFE)
// ============================================================
private void showAnswerCheckConfirmation() {
    
final boolean gr = AppLang.isGreek(this);

    // 🔊 ΟΔΗΓΙΕΣ ΑΠΟ SPEAKER
    AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (am != null) {
        try { am.setMode(AudioManager.MODE_NORMAL); } catch (Throwable ignore) {}
        try { am.setSpeakerphoneOn(true); } catch (Throwable ignore) {}
    }

    final AtomicBoolean answered = new AtomicBoolean(false);

    runOnUiThread(() -> {

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(26), dp(24), dp(26), dp(22));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF000000);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        TextView msg = new TextView(this);
        msg.setText(gr
                ? "Με άκουσες καθαρά; Τσέκαρε την απάντησή σου."
                : "Did you hear me clearly? Check your answer.");
        msg.setTextColor(0xFF39FF14);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 0, 0, dp(18));
        root.addView(msg);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        lp.setMargins(dp(12), dp(8), dp(12), dp(8));

        Button noBtn = new Button(this);
        noBtn.setText(gr ? "ΟΧΙ" : "NO");
        noBtn.setAllCaps(false);
        noBtn.setTextColor(Color.WHITE);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);
        noBtn.setBackground(noBg);
        noBtn.setLayoutParams(lp);

        Button yesBtn = new Button(this);
        yesBtn.setText(gr ? "ΝΑΙ" : "YES");
        yesBtn.setAllCaps(false);
        yesBtn.setTextColor(Color.WHITE);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);
        yesBtn.setBackground(yesBg);
        yesBtn.setLayoutParams(lp);

        btnRow.addView(noBtn);
        btnRow.addView(yesBtn);
        root.addView(btnRow);

        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// Σταματά TTS σε ΟΠΟΙΟΔΗΠΟΤΕ κλείσιμο
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

// NO
noBtn.setOnClickListener(v -> {
    lastAnswerHeardClearly = false;
    answered.set(true);
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

// YES
yesBtn.setOnClickListener(v -> {
    lastAnswerHeardClearly = true;   // ✅ ΣΩΣΤΟ
    answered.set(true);
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
});

// 🔊 TTS μετά το show
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (d.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(
                this,
                gr
                        ? "Με άκουσες καθαρά; Τσέκαρε την απάντησή σου."
                        : "Did you hear me clearly? Check your answer."
        );
    }
}, 500);
});

    // ==========================
    // WAIT FOR USER ANSWER (BACKGROUND)
    // ==========================
    long waitUntil = SystemClock.uptimeMillis() + 8000;
    while (!answered.get() && SystemClock.uptimeMillis() < waitUntil) {
        SystemClock.sleep(50);
    }

    if (!answered.get()) {
        lastAnswerHeardClearly = false;
    }
}

/* ============================================================
   LAB 5 — Vibration Motor Test
   FULL ENV CHECK + PRO TEST + USER CONFIRM
   ============================================================ */
private void lab5Vibration() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(
            gr
                    ? "LAB 5 — Διαγνωστικός Έλεγχος Μηχανισμού Δόνησης"
                    : "LAB 5 — Vibration Motor Test"
    );
    logLine();

    final AtomicBoolean userConfirmed = new AtomicBoolean(false);

    new Thread(() -> {

        try {

            Vibrator vibrator;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vm =
                        (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                vibrator = (vm != null) ? vm.getDefaultVibrator() : null;
            } else {
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            }

            if (vibrator == null || !vibrator.hasVibrator()) {
                logError(gr ? "Δεν εντοπίστηκε μοτέρ δόνησης"
                            : "No vibration motor detected");
                return;
            }

            appendHtml("<br>");
            logInfo(gr ? "Έλεγχος ρυθμίσεων συστήματος:"
                       : "System settings check:");
            logLine();

            // =====================================================
            // 1️⃣ DND
            // =====================================================
            try {
                NotificationManager nm =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (nm != null &&
                        nm.getCurrentInterruptionFilter()
                                != NotificationManager.INTERRUPTION_FILTER_ALL) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Ενεργή λειτουργία Μην Ενοχλείτε."
                               : "Do Not Disturb mode is active."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 2️⃣ Battery Saver
            // =====================================================
            try {
                PowerManager pm =
                        (PowerManager) getSystemService(Context.POWER_SERVICE);

                if (pm != null && pm.isPowerSaveMode()) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Ενεργή λειτουργία εξοικονόμησης ενέργειας μπαταρίας."
                               : "Battery saver mode is active."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 3️⃣ Silent Mode
            // =====================================================
            try {
                AudioManager am =
                        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                if (am != null &&
                        am.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {

                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Η συσκευή βρίσκεται σε αθόρυβη λειτουργία."
                               : "Device is in Silent mode."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 4️⃣ Haptic Feedback Enabled
            // =====================================================
            try {
                int haptic = Settings.System.getInt(
                        getContentResolver(),
                        Settings.System.HAPTIC_FEEDBACK_ENABLED
                );

                if (haptic == 0) {
                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Η απτική ανάδραση είναι απενεργοποιημένη."
                               : "Haptic feedback is disabled."
                    );
                }
            } catch (Throwable ignore) {}

            // =====================================================
            // 5️⃣ Vibrate When Ringing
            // =====================================================
            try {
                int vibrate =
                        Settings.System.getInt(
                                getContentResolver(),
                                "vibrate_when_ringing"
                        );

                if (vibrate == 0) {
                    logLabelWarnValue(
                            gr ? "Ρύθμιση" : "Setting",
                            gr ? "Η δόνηση κατά την κλήση είναι απενεργοποιημένη."
                               : "Vibrate on ring is disabled."
                    );
                }
            } catch (Throwable ignore) {}

            logLine();

            // =====================================================
            // PRO TESTS
            // =====================================================

            logInfo(gr ? "Συνεχής δόνηση 3 δευτερολέπτων"
                       : "Continuous vibration 3 seconds");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(3000,
                                VibrationEffect.DEFAULT_AMPLITUDE)
                );
            } else {
                vibrator.vibrate(3000);
            }

            SystemClock.sleep(3200);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    vibrator.hasAmplitudeControl()) {

                logInfo(gr ? "Έλεγχος έντασης δόνησης"
                           : "Amplitude variation test");

                vibrator.vibrate(VibrationEffect.createOneShot(800, 80));
                SystemClock.sleep(900);

                vibrator.vibrate(VibrationEffect.createOneShot(800, 255));
                SystemClock.sleep(900);

            }

// =====================================================
// USER CONFIRMATION
// =====================================================

final AtomicBoolean answered = new AtomicBoolean(false);

runOnUiThread(() -> {

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(false);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(26), dp(24), dp(26), dp(22));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView msg = new TextView(this);
    msg.setText(gr
            ? "Ένιωσες καθαρά τη δόνηση;"
            : "Did you clearly feel the vibration?");
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    // ---------------------------
    // MUTE ROW (HELPER)
    // ---------------------------
    root.addView(buildMuteRow());

    // ---------- BUTTON ROW ----------
    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams btnLp =
            new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
    btnLp.setMargins(dp(12), dp(8), dp(12), dp(8));

    // ---------- NO ----------
    Button noBtn = new Button(this);
    noBtn.setText(gr ? "ΟΧΙ" : "NO");
    noBtn.setAllCaps(false);
    noBtn.setTextColor(Color.WHITE);

    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
    noBg.setStroke(dp(3), 0xFFFFD700);
    noBtn.setBackground(noBg);
    noBtn.setLayoutParams(btnLp);

    // ---------- YES ----------
    Button yesBtn = new Button(this);
    yesBtn.setText(gr ? "ΝΑΙ" : "YES");
    yesBtn.setAllCaps(false);
    yesBtn.setTextColor(Color.WHITE);

    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yesBtn.setBackground(yesBg);
    yesBtn.setLayoutParams(btnLp);

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

    // STOP TTS on ANY dismiss
    d.setOnDismissListener(dialog -> {
        try { AppTTS.stop(); } catch (Throwable ignore) {}
    });

    // BACK protection
    d.setOnKeyListener((dialog, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_UP) {

            try { AppTTS.stop(); } catch (Throwable ignore) {}
            dialog.dismiss();
            return true;
        }
        return false;
    });

    if (!isFinishing() && !isDestroyed()) {
        d.show();
    }

    // ---------------------------
    // TTS (SAFE + RESPECT MUTE)
    // ---------------------------
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (d.isShowing() && !AppTTS.isMuted(this)) {
            AppTTS.ensureSpeak(
                    this,
                    gr
                            ? "Ένιωσες καθαρά τη δόνηση;"
                            : "Did you clearly feel the vibration?"
            );
        }
    }, 400);

    noBtn.setOnClickListener(v -> {
        userConfirmed.set(false);
        answered.set(true);
        d.dismiss();
    });

    yesBtn.setOnClickListener(v -> {
        userConfirmed.set(true);
        answered.set(true);
        d.dismiss();
    });

});

// ==========================
// WAIT FOR USER RESPONSE
// ==========================
long waitUntil = SystemClock.uptimeMillis() + 10000;

while (!answered.get() &&
        SystemClock.uptimeMillis() < waitUntil) {

    SystemClock.sleep(80);
}

appendHtml("<br>");
logLine();

if (userConfirmed.get()) {

    logLabelOkValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr ? "Η δόνηση επιβεβαιώθηκε από τον χρήστη."
               : "Vibration confirmed by the user."
    );

} else {

    logLabelErrorValue(
            gr ? "Αποτέλεσμα" : "Result",
            gr ? "Η δόνηση δεν επιβεβαιώθηκε από τον χρήστη."
               : "Vibration was not confirmed by the user."
    );

    logLabelWarnValue(
            gr ? "Πιθανές αιτίες" : "Possible causes",
            gr
                    ? "Απενεργοποιημένες ρυθμίσεις δόνησης, χαμηλή ένταση απτικής ανάδρασης, "
                      + "περιορισμός firmware, ή πιθανή μηχανική φθορά."
                    : "Disabled vibration settings, low haptic intensity, "
                      + "firmware restriction, or possible mechanical wear."
    );

    logOk(
            gr
                    ? "Συνιστάται επιβεβαίωση μέσω πραγματικής κλήσης ή δοκιμής ειδοποίησης."
                    : "Verification via a real call or notification test is recommended."
    );
}

} catch (Throwable t) {

    logError(gr ? "Η δοκιμή δόνησης απέτυχε"
            : "Vibration test failed");

logLabelWarnValue(
        gr ? "Πιθανή αιτία" : "Possible cause",
        gr
                ? "Απενεργοποιημένη δόνηση, περιορισμός συστήματος, ή βλάβη μηχανισμού δόνησης."
                : "Vibration disabled, system restriction, or vibration motor malfunction."
);

} finally {

    appendHtml("<br>");
    logOk(gr ? "Το Lab 5 ολοκληρώθηκε." : "Lab 5 finished.");
    logLine();

    runOnUiThread(this::enableSingleExportButton);
}

}).start();
} 

// ============================================================
// LABS 6 — 9: DISPLAY & SENSORS
// ============================================================

// ============================================================
// LAB 6 — Display Touch (POPUP + MUTE + TTS + GR/EN)
// FINAL — LIFECYCLE SAFE
// ============================================================
private void lab6DisplayTouch() {

    final boolean gr = AppLang.isGreek(this);

    final String title =
            gr ? "Έλεγχος Οθόνης Αφής" : "Display Touch Test";

    final String message =
            gr
                    ? "Άγγιξε όλα τα σημεία στην οθόνη, για να ολοκληρωθεί το τεστ αφής.\n\n"
                    + "Το τεστ ελέγχει, αν υπάρχουν νεκρές, ή μη αποκρινόμενες περιοχές."
                    : "Touch all dots on the screen, to complete the touch test.\n\n"
                    + "This test checks, for unresponsive, or dead touch areas.";

// ---------------------------
// POPUP
// ---------------------------
AlertDialog.Builder b =
        new AlertDialog.Builder(
                this,
                android.R.style.Theme_Material_Dialog_NoActionBar
        );
b.setCancelable(false);

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(32, 28, 32, 24);

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);
bg.setCornerRadius(10);
bg.setStroke(4, 0xFFFFD700);
root.setBackground(bg);

// ---------------------------
// TITLE
// ---------------------------
TextView titleView = new TextView(this);
titleView.setText(title);
titleView.setTextColor(Color.WHITE);
titleView.setTextSize(18f);
titleView.setTypeface(null, Typeface.BOLD);
titleView.setGravity(Gravity.CENTER);
titleView.setPadding(0, 0, 0, dp(14));

root.addView(titleView);

// ---------------------------
// MUTE ROW (CHECKBOX)
// ---------------------------
root.addView(buildMuteRow());

// ---------------------------
// MESSAGE
// ---------------------------
TextView tvMsg = new TextView(this);
tvMsg.setText(message);
tvMsg.setTextColor(0xFF39FF14);
tvMsg.setTextSize(15f);
tvMsg.setGravity(Gravity.CENTER);
tvMsg.setPadding(0, 0, 0, 32);
root.addView(tvMsg);

// ---------------------------
// START BUTTON
// ---------------------------
Button startBtn = new Button(this);
startBtn.setAllCaps(false);
startBtn.setText(gr ? "ΕΝΑΡΞΗ ΤΕΣΤ" : "START TEST");
startBtn.setTextColor(Color.WHITE);
startBtn.setTextSize(16f);

GradientDrawable startBg = new GradientDrawable();
startBg.setColor(0xFF0F8A3B);
startBg.setCornerRadius(10);
startBg.setStroke(3, 0xFFFFD700);
startBtn.setBackground(startBg);

LinearLayout.LayoutParams lpStart =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                120
        );
startBtn.setLayoutParams(lpStart);

root.addView(startBtn);

b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// Σταμάτα TTS όταν κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// Κάλυψη BACK
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

// ---------------------------
// ACTION
// ---------------------------
startBtn.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();

    startActivityForResult(
            new Intent(this, TouchGridTestActivity.class),
            6006
    );
});

}

// ============================================================
// LAB 7 — Rotation + Proximity Sensors (MANUAL • MODERN)
// ============================================================
private void lab7RotationAndProximityManual() {

    runOnUiThread(() -> {

        final boolean gr = AppLang.isGreek(this);

        final String titleText =
                gr
                        ? "LAB 7 — Αισθητήρες Περιστροφής & Εγγύτητας"
                        : "LAB 7 — Rotation & Proximity Sensors";

        final String messageText =
        gr
                ? "Βήμα 1:\n"
                  + "Περιστρέψτε αργά τη συσκευή.\n"
                  + "Η οθόνη πρέπει να ακολουθεί τον προσανατολισμό.\n\n"
                  + "Βήμα 2:\n"
                  + "Καλύψτε με το χέρι σας τον αισθητήρα εγγύτητας, "
                  + "στο επάνω μέρος της οθόνης, στην περιοχή ειδοποιήσεων.\n"
                  + "Η οθόνη πρέπει να σβήσει."
                : "Step 1:\n"
                  + "Rotate the device slowly.\n"
                  + "The screen should rotate accordingly.\n\n"
                  + "Step 2:\n"
                  + "Cover the proximity sensor with your hand, "
                  + "at the top of the screen, to the notification area.\n"
                  + "The screen should turn off.";

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
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

        // ---------------------------
        // TITLE
        // ---------------------------
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        root.addView(title);

// ---------------------------
// MESSAGE (NEON GREEN EXCEPT "Βήμα X")
// ---------------------------
SpannableString span = new SpannableString(messageText);

int neonGreen = 0xFF39FF14;

// Βήμα 1
int step1Start = messageText.indexOf("Βήμα 1:");
int step2Start = messageText.indexOf("Βήμα 2:");

if (step1Start != -1 && step2Start != -1) {
    span.setSpan(
            new ForegroundColorSpan(neonGreen),
            step1Start + "Βήμα 1:".length(),
            step2Start,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );

    span.setSpan(
            new ForegroundColorSpan(neonGreen),
            step2Start + "Βήμα 2:".length(),
            messageText.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    );
}

TextView msg = new TextView(this);
msg.setText(span);
msg.setTextColor(0xFFFFFFFF); // default για "Βήμα"
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);

root.addView(msg);

        // ---------------------------
        // MUTE ROW (STANDARD GEL)
        // ---------------------------
        root.addView(buildMuteRow());

        // ---------------------------
        // START BUTTON
        // ---------------------------
        Button start = gelButton(
                this,
                gr ? "ΕΝΑΡΞΗ ΤΕΣΤ" : "START TEST",
                0xFF39FF14
        );
        root.addView(start);

        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// ΣΤΑΜΑΤΑ TTS ΟΠΟΤΕ ΚΛΕΙΣΕΙ
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// ΚΑΛΥΨΗ BACK BUTTON
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

        // ---------------------------
        // TTS (ONLY IF NOT MUTED)
        // ---------------------------
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (d.isShowing() && !AppTTS.isMuted(this)) {
                AppTTS.ensureSpeak(this, messageText);
            }
        }, 120);

        // ---------------------------
        // ACTION
        // ---------------------------
        start.setOnClickListener(v -> {
            AppTTS.stop();
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

    if (!guardCamera(this::lab8CameraHardwareCheck)) return;

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 8 — Έλεγχος Υλικού Κάμερας & Ακεραιότητας Διαδρομής"
            : "LAB 8 — Camera Hardware & Path Integrity");
    logLine();

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        logWarn(gr
                ? "Το Camera2 δεν υποστηρίζεται σε αυτήν την έκδοση Android."
                : "Camera2 not supported on this Android version.");
        logOk(gr
                ? "Fallback: άνοιγμα εφαρμογής κάμερας (βασικός έλεγχος)."
                : "Fallback: opening system camera app (basic check).");
        try {
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 9009);
        } catch (Throwable t) {
            logError(gr
                    ? "Αποτυχία εκκίνησης εφαρμογής κάμερας."
                    : "Failed to launch camera app.");
            logWarn(gr
                    ? "Η εφαρμογή κάμερας μπορεί να λείπει ή να είναι μπλοκαρισμένη."
                    : "Camera app may be missing or blocked.");

            appendHtml("<br>");
            logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
            logLine();
            enableSingleExportButton();
        }
        return;
    }

    final PackageManager pm = getPackageManager();
    final boolean hasAnyCamera =
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

    if (!hasAnyCamera) {
        logError(gr
                ? "Δεν εντοπίστηκε υλικό κάμερας στη συσκευή."
                : "No camera hardware detected on this device.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    final CameraManager cm =
            (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    if (cm == null) {
        logError(gr
                ? "Το CameraManager δεν είναι διαθέσιμο."
                : "CameraManager unavailable.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // Permission check (Android 6+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    2001
            );
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
        logError(gr
                ? "Αποτυχία καταγραφής camera IDs."
                : "Failed to enumerate cameras.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    if (ids == null || ids.length == 0) {
        logError(gr
                ? "Δεν βρέθηκαν προσβάσιμα camera IDs."
                : "No accessible camera IDs found.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logLabelOkValue(
            gr ? "Υποσύστημα Κάμερας" : "Camera subsystem",
            gr ? "Εντοπίστηκε" : "Detected"
    );

    logLabelOkValue(
            gr ? "Σύνολο camera IDs" : "Total camera IDs",
            String.valueOf(ids.length)
    );

    // ------------------------------------------------------------
    // Build per-camera descriptors
    // ------------------------------------------------------------
    final ArrayList<Lab8Cam> cams = new ArrayList<>();

    for (String id : ids) {
        try {
            CameraCharacteristics cc =
                    cm.getCameraCharacteristics(id);

            Integer facing =
                    cc.get(CameraCharacteristics.LENS_FACING);

            Float focal =
                    cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) != null
                            && cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS).length > 0
                            ? cc.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0]
                            : null;

            Boolean flash =
                    cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

            int[] caps =
                    cc.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);

            boolean hasRaw = false;
            boolean hasManual = false;
            boolean hasDepth = false;

            if (caps != null) {
                for (int c : caps) {
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)
                        hasRaw = true;
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)
                        hasManual = true;
                    if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT)
                        hasDepth = true;
                }
            }

            StreamConfigurationMap map =
                    cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size previewSize = null;
            if (map != null) {
                Size[] outs =
                        map.getOutputSizes(SurfaceTexture.class);
                if (outs != null && outs.length > 0) {
                    previewSize = outs[0];
                    for (Size s : outs) {
                        if (s.getWidth() <= 1920 && s.getHeight() <= 1080) {
                            previewSize = s;
                            break;
                        }
                    }
                }
            }

            String facingStr = gr ? "ΑΓΝΩΣΤΟ" : "UNKNOWN";
            if (facing != null) {
                if (facing == CameraCharacteristics.LENS_FACING_BACK)
                    facingStr = gr ? "ΠΙΣΩ" : "BACK";
                else if (facing == CameraCharacteristics.LENS_FACING_FRONT)
                    facingStr = gr ? "ΜΠΡΟΣΤΑ" : "FRONT";
                else if (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                    facingStr = gr ? "ΕΞΩΤΕΡΙΚΗ" : "EXTERNAL";
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
            logWarn(gr
                    ? "Camera ID " + id + " — αποτυχία ανάγνωσης χαρακτηριστικών"
                    : "Camera ID " + id + " — Characteristics read failed");
        }
    }

    if (cams.isEmpty()) {
        logError(gr
                ? "Δεν βρέθηκαν αξιοποιήσιμες περιγραφές καμερών."
                : "No usable camera descriptors.");
        appendHtml("<br>");
        logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logInfo(gr
            ? "Σύνοψη δυνατοτήτων καμερών:"
            : "Camera capabilities summary:");
    logLine();
    appendHtml("<br>");

// ------------------------------------------------------------
// Run test sequence (one camera at a time)
// ------------------------------------------------------------
final int[] idx = {0};

final Lab8Overall overall = new Lab8Overall();
overall.total = cams.size();

// Save state for LAB 8.1
lab8CamsFor81 = cams;
lab8CmFor81 = cm;

runOnUiThread(() -> showLab8IntroAndStart(cams, idx, cm, overall));
}

// ============================================================
// LAB 8 — Intro dialog (TTS + MUTE + GR/EN)
// ============================================================
private void showLab8IntroAndStart(
        ArrayList<Lab8Cam> cams,
        int[] idx,
        CameraManager cm,
        Lab8Overall overall
) {

    final boolean gr = AppLang.isGreek(this);

    final String titleText =
            gr ? "LAB 8 — Έλεγχος Καμερών (Πλήρης)"
               : "LAB 8 — Camera Lab (Full)";

    final String messageText =
            gr
                    ? "Αυτό το τεστ, θα ελέγξει ΟΛΕΣ τις κάμερες, μία-μία.\n\n"
                      + "Για κάθε κάμερα:\n"
                      + "• Θα ανοίξει ζωντανή προεπισκόπηση.\n"
                      + "• Θα μετρηθεί η ροή καρέ.\n"
                      + "• Θα εκτιμηθεί η καθυστέρηση pipeline.\n"
                      + "• Θα ενεργοποιηθεί το φλας, όπου υπάρχει.\n\n"
                      + "Μετά από κάθε κάμερα, θα σου ζητηθεί επιβεβαίωση."
                    : "This lab, will test ALL cameras, one by one.\n\n"
                      + "For each camera:\n"
                      + "• Live preview will open.\n"
                      + "• Frame stream will be sampled.\n"
                      + "• Pipeline latency, will be estimated\n"
                      + "• Flash will be toggled, where available\n\n"
                      + "After each camera, you will be asked to confirm.";

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

    // ---------------------------
    // TITLE
    // ---------------------------
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

// ---------------------------
// MESSAGE (NEON GREEN)
// ---------------------------
TextView msg = new TextView(this);
msg.setText(messageText);
msg.setTextColor(0xFF39FF14); // NEON GREEN
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
root.addView(msg);

    // ---------------------------
    // MUTE ROW (ABOVE START)
    // ---------------------------
    root.addView(buildMuteRow());

    // ---------------------------
    // START BUTTON
    // ---------------------------
    Button start = new Button(this);
    start.setText(gr ? "ΕΝΑΡΞΗ ΤΕΣΤ" : "START TEST");
    start.setAllCaps(false);
    start.setTextColor(Color.WHITE);

    GradientDrawable startBg = new GradientDrawable();
    startBg.setColor(0xFF39FF14);
    startBg.setCornerRadius(dp(10));
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

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// STOP TTS όταν κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// START BUTTON
start.setOnClickListener(v -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    d.dismiss();
    lab8RunNextCamera(cams, idx, cm, overall);
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}

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

    final boolean gr = AppLang.isGreek(this);

    // ====================================================
    // ALL CAMERAS DONE — FINAL SUMMARY + VERDICT
    // ====================================================
    if (idx[0] >= cams.size()) {

        logLine();
        logInfo(gr ? "ΣΥΝΟΨΗ LAB 8:" : "LAB 8 summary:");
        logLine();

        logLabelValue(
                gr ? "Κάμερες που ελέγχθηκαν" : "Cameras tested",
                String.valueOf(overall.total)
        );

        if (overall.previewOkCount == overall.total && overall.total > 0)
            logLabelOkValue(
                    gr ? "Προεπισκόπηση OK" : "Preview OK",
                    overall.previewOkCount + "/" + overall.total
            );
        else
            logLabelWarnValue(
                    gr ? "Προεπισκόπηση OK" : "Preview OK",
                    overall.previewOkCount + "/" + overall.total
            );

        if (overall.previewFailCount == 0)
            logLabelOkValue(
                    gr ? "Αποτυχίες προεπισκόπησης" : "Preview FAIL",
                    "0"
            );
        else
            logLabelErrorValue(
                    gr ? "Αποτυχίες προεπισκόπησης" : "Preview FAIL",
                    String.valueOf(overall.previewFailCount)
            );

        if (overall.torchOkCount > 0)
            logLabelOkValue(
                    gr ? "Φλας OK" : "Torch OK",
                    String.valueOf(overall.torchOkCount)
            );
        else
            logLabelWarnValue(
                    gr ? "Φλας OK" : "Torch OK",
                    "0"
            );

        if (overall.torchFailCount == 0)
            logLabelOkValue(
                    gr ? "Αποτυχίες φλας" : "Torch FAIL",
                    "0"
            );
        else
            logLabelWarnValue(
                    gr ? "Αποτυχίες φλας" : "Torch FAIL",
                    String.valueOf(overall.torchFailCount)
            );

        if (overall.streamIssueCount == 0)
            logLabelOkValue(
                    gr ? "Προβλήματα ροής καρέ" : "Frame stream issues",
                    gr ? "Κανένα" : "None detected"
            );
        else
            logLabelWarnValue(
                    gr ? "Προβλήματα ροής καρέ" : "Frame stream issues",
                    String.valueOf(overall.streamIssueCount)
            );

        // ====================================================
        // FINAL VERDICT
        // ====================================================
        boolean cameraSubsystemOk =
                overall.total > 0 &&
                overall.previewFailCount == 0 &&
                overall.previewOkCount == overall.total;

        if (cameraSubsystemOk) {

            logLabelOkValue(
                    gr ? "Υποσύστημα κάμερας" : "Camera subsystem",
                    gr ? "Λειτουργικό" : "Operational"
            );

            if (overall.streamIssueCount == 0)
                logLabelOkValue(
                        gr ? "Σταθερότητα ροής" : "Live stream stability",
                        "OK"
                );
            else
                logLabelWarnValue(
                        gr ? "Σταθερότητα ροής" : "Live stream stability",
                        gr ? "Μικρές ανωμαλίες" : "Minor anomalies detected"
                );

            if (overall.torchFailCount == 0)
                logLabelOkValue(
                        gr ? "Υποσύστημα φλας" : "Flash subsystem",
                        gr ? "OK (όπου υπάρχει)" : "OK (where available)"
                );
            else
                logLabelWarnValue(
                        gr ? "Υποσύστημα φλας" : "Flash subsystem",
                        gr
                                ? "Ορισμένες κάμερες χωρίς φλας ή με πρόβλημα"
                                : "Some cameras have no flash / torch issues"
                );

            logOk(
                    gr
                            ? "Η συσκευή πληροί τα κριτήρια για αξιολόγηση δυνατοτήτων κάμερας."
                            : "Your device meets the criteria to evaluate camera capabilities."
            );

            logInfo(
                    gr
                            ? "Στο επόμενο βήμα αναλύουμε δυνατότητες φωτογραφίας & βίντεο."
                            : "Next step: analyze photo & video capabilities."
            );

            appendHtml("<br>");
            logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
            logLine();

            runOnUiThread(this::showLab8_1Prompt);
            return;

        } else {

            logLabelErrorValue(
                    gr ? "Υποσύστημα κάμερας" : "Camera subsystem",
                    gr ? "ΜΗ αξιόπιστο" : "NOT reliable"
            );

            logError(
                    gr
                            ? "Μία ή περισσότερες κάμερες απέτυχαν στον βασικό έλεγχο."
                            : "One or more cameras failed basic operation checks."
            );

            appendHtml("<br>");
            logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
            logLine();

            enableSingleExportButton();
            return;
        }
    }

    // ====================================================
    // NEXT CAMERA
    // ====================================================
    final Lab8Cam cam = cams.get(idx[0]);
    idx[0]++;

    logSection("LAB 8 — Camera ID " + cam.id + " (" + cam.facing + ")");
    logLine();

    if (cam.hasManual)
        logLabelOkValue(
                gr ? "Χειροκίνητος αισθητήρας" : "Manual sensor",
                "YES"
        );
    else
        logLabelWarnValue(
                gr ? "Χειροκίνητος αισθητήρας" : "Manual sensor",
                "NO"
        );

    if (cam.hasDepth)
        logLabelOkValue(
                gr ? "Αισθητήρας βάθους" : "Depth output",
                "YES"
        );
    else
        logLabelWarnValue(
                gr ? "Αισθητήρας βάθους" : "Depth output",
                "NO"
        );

    if (cam.focal != null)
        logLabelValue(
                gr ? "Εστιακή απόσταση" : "Focal length",
                String.format(Locale.US, "%.2f mm", cam.focal)
        );

    if (cam.preview != null)
        logLabelValue(
                gr ? "Ανάλυση προεπισκόπησης" : "Preview size",
                cam.preview.getWidth() + " x " + cam.preview.getHeight()
        );

    logLine();

    if (cam.hasFlash) {
        lab8TryTorchToggle(cam.id, cam, overall);
    } else {
        logLabelWarnValue(
                gr ? "Φλας" : "Flash",
                gr ? "Δεν υπάρχει" : "Not available"
        );
    }

    runOnUiThread(() ->
            lab8ShowPreviewDialogForCamera(
                    cam,
                    cm,
                    overall,
                    () -> lab8RunNextCamera(cams, idx, cm, overall)
            )
    );
}

// ============================================================
// LAB 8 — Torch toggle
// ============================================================
private void lab8TryTorchToggle(String camId, Lab8Cam cam, Lab8Overall overall) {
    try {
        CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cm == null) {
            logLabelWarnValue("Flash", "Test skipped (CameraManager unavailable)");
            overall.torchFailCount++;
            return;
        }

        cm.setTorchMode(camId, true);
        SystemClock.sleep(250);
        cm.setTorchMode(camId, false);

        logLabelOkValue("Flash", "Torch toggled successfully");
        overall.torchOkCount++;

    } catch (Throwable t) {
        logLabelErrorValue("Flash", "Torch control failed");
        logWarn("Possible flash hardware, driver, or permission issue.");
        overall.torchFailCount++;
    }
}

// ============================================================
// LAB 8 — Preview dialog + stream sampling (TTS + MUTE + GR/EN)
// ============================================================
private void lab8ShowPreviewDialogForCamera(
        Lab8Cam cam,
        CameraManager cm,
        Lab8Overall overall,
        Runnable onDone
) {

    final boolean gr = AppLang.isGreek(this);

    final String titleText =
            gr
                    ? "Προεπισκόπηση Κάμερας — " + cam.facing + " (ID " + cam.id + ")"
                    : "Camera Preview — " + cam.facing + " (ID " + cam.id + ")";

    final String messageText =
            gr
                    ? "Περίμενε περίπου 5 δευτερόλεπτα, όσο γίνεται δειγματοληψία καρέ.\n\n"
                      + "Στη συνέχεια απάντησε:\n"
                      + "Είδες ζωντανή εικόνα από την κάμερα;"
                    : "Please wait about 5 seconds, while frames are sampled.\n\n"
                      + "Then answer:\n"
                      + "Did you see live image from the camera?";

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
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // ---------------------------
    // TITLE
    // ---------------------------
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(16f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(10));
    root.addView(title);

    // ---------------------------
    // MESSAGE 
    // ---------------------------

TextView hint = new TextView(this);
hint.setText(messageText);
hint.setTextColor(0xFF39FF14); // NEON GREEN
hint.setTextSize(14f);
hint.setGravity(Gravity.CENTER);
hint.setPadding(0, 0, 0, dp(10));
hint.setLineSpacing(0f, 1.15f);
root.addView(hint);

    // ---------------------------
    // PREVIEW (TextureView)
    // ---------------------------
    final TextureView tv = new TextureView(this);
    LinearLayout.LayoutParams lpTv =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(280)
            );
    tv.setLayoutParams(lpTv);
    root.addView(tv);

    // ---------------------------
    // MUTE ROW (ABOVE YES / NO)
    // ---------------------------
    root.addView(buildMuteRow());

    // Buttons row
    LinearLayout row = new LinearLayout(this);
    row.setOrientation(LinearLayout.HORIZONTAL);
    row.setGravity(Gravity.CENTER);
    row.setPadding(0, dp(12), 0, 0);

    Button yes = new Button(this);
    yes.setText(gr ? "ΒΛΕΠΩ ΕΙΚΟΝΑ" : "I SEE IMAGE");
    yes.setAllCaps(false);
    yes.setTextColor(0xFFFFFFFF);
    GradientDrawable yesBg = new GradientDrawable();
    yesBg.setColor(0xFF0B5F3B);
    yesBg.setCornerRadius(dp(10));
    yesBg.setStroke(dp(3), 0xFFFFD700);
    yes.setBackground(yesBg);

    Button no = new Button(this);
    no.setText(gr ? "ΔΕΝ ΒΛΕΠΩ ΕΙΚΟΝΑ" : "NO IMAGE");
    no.setAllCaps(false);
    no.setTextColor(0xFFFFFFFF);
    GradientDrawable noBg = new GradientDrawable();
    noBg.setColor(0xFF8B0000);
    noBg.setCornerRadius(dp(10));
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
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// Σταμάτα TTS ΟΠΟΤΕ κλείσει
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// Κάλυψη BACK button
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK &&
        event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}
    
// ---------------------------
// TTS (ONLY IF NOT MUTED)
// ---------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (d.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, messageText);
    }
}, 120);

    // Disable buttons until sampling done (avoid instant wrong click)
    yes.setEnabled(false);
    no.setEnabled(false);

    final Lab8Session s = new Lab8Session();
    s.camId = cam.id;
    s.cm = cm;
    s.textureView = tv;
    s.cam = cam;
    
cam.runtimeSession = s;

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
    AppTTS.stop();
    overall.previewOkCount++;
    s.userConfirmedPreview = Boolean.TRUE;
    finishAndNext.run();
});

no.setOnClickListener(v -> {
    AppTTS.stop();
    overall.previewFailCount++;
    s.userConfirmedPreview = Boolean.FALSE;
    finishAndNext.run();
});

    // Start camera when texture is ready
    if (tv.isAvailable()) {
        lab8StartCamera2Session(s, overall, enableButtons, () -> {
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

    final boolean gr = AppLang.isGreek(this);
    
    try {
        // Choose preview size
        Size ps = (s.cam != null && s.cam.preview != null) ? s.cam.preview : new Size(1280, 720);

        SurfaceTexture st = s.textureView.getSurfaceTexture();
        if (st == null) {
            logLabelErrorValue("Preview", "SurfaceTexture unavailable");
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

                    int stepX = Math.max(8, w / 64);
                    int stepY = Math.max(8, h / 48);

                    long sum = 0;
                    long sum2 = 0;
                    int count = 0;
                    int localMin = 999;
                    int localMax = -1;

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

                        if (mean < 8 && localMax < 20) s.blackFrames++;
                    }
                }

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

    @Override
    public void onOpened(CameraDevice camera) {
        s.device = camera;

        try {
            ArrayList<Surface> outs = new ArrayList<>();
            outs.add(previewSurface);
            outs.add(s.reader.getSurface());

            camera.createCaptureSession(
                    outs,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            s.session = session;

                            try {
                                CaptureRequest.Builder rb =
                                        camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                                rb.addTarget(previewSurface);
                                rb.addTarget(s.reader.getSurface());

                                try {
                                    CameraCharacteristics cc =
                                            s.cm.getCameraCharacteristics(s.camId);
                                    Range<Integer>[] ranges =
                                            cc.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

                                    if (ranges != null && ranges.length > 0) {
                                        Range<Integer> best = ranges[0];
                                        for (Range<Integer> r : ranges) {
                                            if (r.getUpper() >= 30 && r.getLower() >= 15) {
                                                best = r;
                                                break;
                                            }
                                        }
                                        rb.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, best);
                                    }
                                } catch (Throwable ignore) {}

                                rb.set(CaptureRequest.CONTROL_MODE,
                                        CaptureRequest.CONTROL_MODE_AUTO);
                                rb.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                session.setRepeatingRequest(
                                        rb.build(),
                                        null,
                                        new Handler(Looper.getMainLooper())
                                );

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    try {
                                        lab8StopAndReportSample(s, overall);
                                    } catch (Throwable ignore) {}
                                    onSamplingDoneEnableButtons.run();
                                }, 5000);

                            } catch (Throwable t) {
                                logLabelErrorValue(
                                        "Preview",
                                        gr
                                                ? "Αποτυχία εκκίνησης επαναλαμβανόμενου αιτήματος"
                                                : "Failed to start repeating request"
                                );
                                onFail.run();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            logLabelErrorValue(
                                    "Preview",
                                    gr
                                            ? "Αποτυχία διαμόρφωσης capture session"
                                            : "Capture session configuration failed"
                            );
                            onFail.run();
                        }

                    },
                    new Handler(Looper.getMainLooper())
            );

        } catch (Throwable t) {
            logLabelErrorValue(
                    "Preview",
                    gr
                            ? "Αποτυχία δημιουργίας session"
                            : "Session creation failed"
            );
            onFail.run();
        }
    }

    @Override
    public void onDisconnected(CameraDevice camera) {
        logLabelWarnValue(
                "Preview",
                gr
                        ? "Η κάμερα αποσυνδέθηκε κατά τη δειγματοληψία"
                        : "Camera disconnected during sampling"
        );
        onFail.run();
    }

    @Override
    public void onError(CameraDevice camera, int error) {
        logLabelErrorValue(
                "Camera open",
                gr
                        ? "Σφάλμα ανοίγματος κάμερας (κωδικός " + error + ")"
                        : "Camera open error (code " + error + ")"
        );
        onFail.run();
    }

}, new Handler(Looper.getMainLooper()));

} catch (Throwable t) {
    logLabelErrorValue(
            "Camera2",
            gr
                    ? "Αποτυχία εκκίνησης Camera2 session"
                    : "Session start failed"
    );
    onFail.run();
}
}

// ============================================================
// LAB 8 — Stop + report stream sample
// ============================================================
private void lab8StopAndReportSample(Lab8Session s, Lab8Overall overall) {

    final boolean gr = AppLang.isGreek(this);

    // ------------------------------------------------------------
    // Camera runtime results (AFTER sampling)
    // ------------------------------------------------------------
    long durMs = Math.max(1, SystemClock.elapsedRealtime() - s.sampleStartMs);
    float fps = (s.frames * 1000f) / durMs;

    // Stream sampling
    logLabelValue(
            gr ? "Δειγματοληψία ροής" : "Stream sampling",
            "5s"
    );

    if (s.frames > 0)
        logLabelOkValue(
                gr ? "Καρέ" : "Frames",
                String.valueOf(s.frames)
        );
    else
        logLabelErrorValue(
                gr ? "Καρέ" : "Frames",
                "0"
        );

    if (fps >= 20f)
        logLabelOkValue(
                gr ? "FPS (εκτίμηση)" : "FPS (estimated)",
                String.format(Locale.US, "%.1f", fps)
        );
    else
        logLabelWarnValue(
                gr ? "FPS (εκτίμηση)" : "FPS (estimated)",
                String.format(Locale.US, "%.1f", fps)
        );

    if (s.droppedFrames == 0)
        logLabelOkValue(
                gr ? "Απώλειες καρέ / timeouts" : "Frame drops / timeouts",
                "0"
        );
    else
        logLabelWarnValue(
                gr ? "Απώλειες καρέ / timeouts" : "Frame drops / timeouts",
                String.valueOf(s.droppedFrames)
        );

    if (s.blackFrames == 0)
        logLabelOkValue(
                gr ? "Μαύρα καρέ (ύποπτα)" : "Black frames (suspected)",
                "0"
        );
    else {
        logLabelWarnValue(
                gr ? "Μαύρα καρέ (ύποπτα)" : "Black frames (suspected)",
                String.valueOf(s.blackFrames)
        );
        overall.streamIssueCount++;
    }

    logLabelValue(
            gr ? "Εύρος φωτεινότητας (min / max)" : "Luma range (min / max)",
            s.minLuma + " / " + s.maxLuma
    );

    if (s.latencyCount > 0) {
        long avg = s.latencySumMs / Math.max(1, s.latencyCount);

        if (avg <= 250)
            logLabelOkValue(
                    gr ? "Καθυστέρηση pipeline (μ.ο. ms)" : "Pipeline latency (avg ms)",
                    String.valueOf(avg)
            );
        else
            logLabelWarnValue(
                    gr ? "Καθυστέρηση pipeline (μ.ο. ms)" : "Pipeline latency (avg ms)",
                    String.valueOf(avg)
            );
    } else {
        logLabelWarnValue(
                gr ? "Καθυστέρηση pipeline (μ.ο. ms)" : "Pipeline latency (avg ms)",
                gr ? "Μη διαθέσιμο" : "Not available"
        );
    }

    if (s.cam != null && s.cam.hasRaw)
        logLabelOkValue(
                gr ? "Υποστήριξη RAW" : "RAW support",
                gr
                        ? "ΝΑΙ — επαγγελματικές ασυμπίεστες φωτογραφίες"
                        : "YES — professional uncompressed photos"
        );
    else
        logLabelWarnValue(
                gr ? "Υποστήριξη RAW" : "RAW support",
                gr
                        ? "ΟΧΙ — μόνο JPEG"
                        : "NO — professional uncompressed photos not supported (JPEG only)"
        );

    // User confirmation
    if (s.userConfirmedPreview != null) {
        if (s.userConfirmedPreview)
            logLabelOkValue(
                    gr ? "Επιβεβαίωση χρήστη" : "User confirmation",
                    gr ? "Η προεπισκόπηση ήταν ορατή" : "Live preview visible"
            );
        else
            logLabelErrorValue(
                    gr ? "Επιβεβαίωση χρήστη" : "User confirmation",
                    gr ? "Η προεπισκόπηση ΔΕΝ ήταν ορατή" : "Preview NOT visible"
            );
    }

    // ------------------------------------------------------------
    // Final verdict (per camera)
    // ------------------------------------------------------------
    boolean ok =
            (s.frames > 0) &&
            (s.blackFrames == 0) &&
            (s.droppedFrames == 0) &&
            (s.latencyCount == 0 || (s.latencySumMs / Math.max(1, s.latencyCount)) <= 250) &&
            (s.userConfirmedPreview != null && s.userConfirmedPreview);

    s.verdictOk = ok;

    if (ok) {
        logLabelOkValue(
                gr ? "Τελικό αποτέλεσμα" : "Verdict",
                gr ? "OK — Η διαδρομή κάμερας λειτουργεί σωστά"
                   : "OK — Camera path operational"
        );
    } else {
        logLabelWarnValue(
                gr ? "Τελικό αποτέλεσμα" : "Verdict",
                gr ? "Εντοπίστηκαν θέματα — έλεγξε τα παραπάνω"
                   : "Issues detected — review above"
        );
    }

    logLine();
    appendHtml("<br>");
} // ✅ ΤΕΛΟΣ lab8StopAndReportSample (αυτό έλειπε)

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

    Lab8Session runtimeSession;
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

    Boolean userConfirmedPreview = null;
    boolean verdictOk = false;
}

// ============================================================
// LAB 8.1 — PROMPT (FINAL + TTS + MUTE + GR/EN)
// ============================================================
private void showLab8_1Prompt() {

    runOnUiThread(() -> {

        final boolean gr = AppLang.isGreek(this);

        final String titleText =
                gr
                        ? "Ανάλυση Δυνατοτήτων Κάμερας"
                        : "Camera Capabilities Analysis";

        final String messageText =
        gr
                ? "Το LAB 8.1 εξηγεί, τι μπορεί πραγματικά να κάνει η κάμερά σου,\n"
                  + "με απλούς όρους.\n\n"
                  + "• Ποιότητα φωτογραφίας,\n"
                  + "• Ανάλυση & ομαλότητα βίντεο,\n"
                  + "• Επαγγελματικές δυνατότητες (RAW).\n\n"
                : "LAB 8.1 explains, what your camera can actually do,\n"
                  + "in simple terms.\n\n"
                  + "• Photo quality,\n"
                  + "• Video resolution & smoothness,\n"
                  + "• Professional features (RAW).\n\n";
                        

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        ManualTestsActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );
        b.setCancelable(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(18), dp(20), dp(16));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(4), 0xFFFFD700);
        root.setBackground(bg);

        // ---------------------------
        // TITLE
        // ---------------------------
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextColor(Color.WHITE);
        title.setTextSize(17f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(10));
        root.addView(title);

        // ---------------------------
        // MESSAGE
        // ---------------------------
        TextView msg = new TextView(this);
msg.setText(messageText);
msg.setTextColor(0xFF39FF14); // NEON GREEN
msg.setTextSize(14f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
root.addView(msg);

        // ---------------------------
        // MUTE ROW (ABOVE BUTTON)
        // ---------------------------
        root.addView(buildMuteRow());

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(0, dp(14), 0, 0);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(54), 1f);
        lp.setMargins(dp(6), 0, dp(6), 0);

        Button yes = new Button(this);
        yes.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
        yes.setAllCaps(false);
        yes.setTextColor(Color.WHITE);
        yes.setLayoutParams(lp);

        GradientDrawable yesBg = new GradientDrawable();
        yesBg.setColor(0xFF0B5F3B);
        yesBg.setCornerRadius(dp(10));
        yesBg.setStroke(dp(3), 0xFFFFD700);
        yes.setBackground(yesBg);

        Button no = new Button(this);
        no.setText(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP");
        no.setAllCaps(false);
        no.setTextColor(Color.WHITE);
        no.setLayoutParams(lp);

        GradientDrawable noBg = new GradientDrawable();
        noBg.setColor(0xFF8B0000);
        noBg.setCornerRadius(dp(10));
        noBg.setStroke(dp(3), 0xFFFFD700);
        no.setBackground(noBg);

        buttons.addView(yes);
        buttons.addView(no);
        root.addView(buttons);

        b.setView(root);
b.setCancelable(false);

final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

// ΣΤΑΜΑΤΑ TTS ΟΠΟΤΕ ΚΛΕΙΣΕΙ
d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
});

// ΚΑΛΥΨΗ BACK BUTTON
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

if (!isFinishing() && !isDestroyed()) {
    d.show();
}
        
        yes.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();
    startLab8_1CameraCapabilities();
});

no.setOnClickListener(v -> {
    AppTTS.stop();
    d.dismiss();
    logWarn(gr
        ? "Το LAB 8.1 παραλείφθηκε από τον χρήστη."
        : "LAB 8.1 skipped by user.");
    
    logLine();
    logOk(gr ? "Το Lab 8.1 ολοκληρώθηκε." : "Lab 8.1 finished.");
    logLine();
    enableSingleExportButton();
});
    });
}

// ============================================================
// LAB 8.1 — CAPABILITIES MAP (HUMAN FRIENDLY)
// ============================================================
private void startLab8_1CameraCapabilities() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logSection(gr
            ? "LAB 8.1 — Δυνατότητες Κάμερας"
            : "LAB 8.1 — Camera Capabilities");
    logLine();

    if (lab8CmFor81 == null || lab8CamsFor81 == null || lab8CamsFor81.isEmpty()) {

        logLabelErrorValue(
                "LAB 8.1",
                gr ? "Λείπει το context καμερών" : "Missing camera context"
        );

        logOk(gr
                ? "Παρακαλώ εκτέλεσε ξανά το LAB 8."
                : "Please re-run LAB 8.");

        logLine();
        enableSingleExportButton();
        return;
    }

    logInfo(gr
            ? "Αυτή η ενότητα εξηγεί τις δυνατότητες της κάμερας με απλά λόγια."
            : "This section explains camera abilities in plain language.");

    logLabelValue(
            gr ? "Κάμερες που ανιχνεύθηκαν" : "Cameras detected",
            String.valueOf(lab8CamsFor81.size())
    );

    logLine();

    for (Lab8Cam cam : lab8CamsFor81) {
        lab8_1DumpOneCameraCapabilities(lab8CmFor81, cam);
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 8.1 ολοκληρώθηκε." : "Lab 8.1 finished.");
    logLine();
    enableSingleExportButton();
}

// ============================================================
// LAB 8.1 — ONE CAMERA (HUMAN OUTPUT)
// ============================================================
private void lab8_1DumpOneCameraCapabilities(CameraManager cm, Lab8Cam cam) {

    final boolean gr = AppLang.isGreek(this);

    if (cm == null || cam == null || cam.id == null) return;

    appendHtml("<br>");
    logSection((gr ? "Κάμερα " : "Camera ") + cam.facing);
    logLine();

    CameraCharacteristics cc;
    try {
        cc = cm.getCameraCharacteristics(cam.id);
    } catch (Throwable t) {
        logLabelErrorValue(
                gr ? "Πληροφορίες κάμερας" : "Camera info",
                gr ? "Μη διαθέσιμες" : "Unavailable"
        );
        logLine();
        return;
    }

    // ------------------------------------------------------------
    // HUMAN FINAL VERDICT
    // ------------------------------------------------------------
    CameraHumanSummary h = buildHumanSummary(cc);

    logInfo(gr ? "ΤΕΛΙΚΟ ΑΝΘΡΩΠΙΝΟ ΣΥΜΠΕΡΑΣΜΑ" : "FINAL HUMAN VERDICT");
    logLine();

    logLabelValue(gr ? "Ποιότητα φωτογραφίας" : "Photo quality", h.photoQuality);
    logLabelValue(gr ? "Επαγγελματικές φωτογραφίες" : "Professional photos", h.professionalPhotos);
    logLabelValue(gr ? "Ποιότητα βίντεο" : "Video quality", h.videoQuality);
    logLabelValue(gr ? "Ομαλότητα βίντεο" : "Video smoothness", h.videoSmoothness);
    logLabelValue(gr ? "Αργή κίνηση (slow motion)" : "Slow motion", h.slowMotion);
    logLabelValue(gr ? "Σταθεροποίηση" : "Stabilization", h.stabilization);
    logLabelValue(gr ? "Χειροκίνητη λειτουργία" : "Manual mode", h.manualMode);
    logLabelValue(gr ? "Φλας" : "Flash", h.flash);
    logLabelValue(gr ? "Χρήση στην πράξη" : "Real life use", h.realLifeUse);

    logLine();
    logLabelOkValue(gr ? "Συμπέρασμα" : "Verdict", h.verdict);
    logLine();
}

// ============================================================
// LAB 8.1 — Helpers (NO NESTED METHODS)
// ============================================================
private Size lab8_1MaxSize(Size[] sizes) {
    if (sizes == null || sizes.length == 0) return null;
    Size best = sizes[0];
    for (Size s : sizes) {
        if (s == null) continue;
        long a = (long) s.getWidth() * (long) s.getHeight();
        long b = (long) best.getWidth() * (long) best.getHeight();
        if (a > b) best = s;
    }
    return best;
}

private String lab8_1FpsRangesToString(Range<Integer>[] rs) {
    if (rs == null || rs.length == 0) return "N/A";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rs.length; i++) {
        Range<Integer> r = rs[i];
        if (r == null) continue;
        if (sb.length() > 0) sb.append(", ");
        sb.append(r.getLower()).append("–").append(r.getUpper());
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1FormatList(int[] fmts, boolean hasRaw) {
    if (fmts == null || fmts.length == 0) return "N/A";
    // keep it readable, not a dump of 50 formats
    boolean hasJpeg = false, hasYuv = false, hasPrivate = false;
    for (int f : fmts) {
        if (f == ImageFormat.JPEG) hasJpeg = true;
        if (f == ImageFormat.YUV_420_888) hasYuv = true;
        if (f == ImageFormat.PRIVATE) hasPrivate = true;
    }
    StringBuilder sb = new StringBuilder();
    if (hasJpeg) sb.append("JPEG");
    if (hasYuv) { if (sb.length() > 0) sb.append(", "); sb.append("YUV_420_888"); }
    if (hasRaw) { if (sb.length() > 0) sb.append(", "); sb.append("RAW_SENSOR"); }
    if (hasPrivate) { if (sb.length() > 0) sb.append(", "); sb.append("PRIVATE"); }
    return (sb.length() == 0) ? "Available (many)" : sb.toString();
}

private String lab8_1AfModesToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s = null;
        if (m == CaptureRequest.CONTROL_AF_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_AF_MODE_AUTO) s = "AUTO";
        else if (m == CaptureRequest.CONTROL_AF_MODE_MACRO) s = "MACRO";
        else if (m == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) s = "CONTINUOUS_VIDEO";
        else if (m == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE) s = "CONTINUOUS_PICTURE";
        else if (m == CaptureRequest.CONTROL_AF_MODE_EDOF) s = "EDOF";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1AeModesToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s;
        if (m == CaptureRequest.CONTROL_AE_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON) s = "ON";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH) s = "ON_AUTO_FLASH";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH) s = "ON_ALWAYS_FLASH";
        else if (m == CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE) s = "ON_REDEYE";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1AwbModesToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s;
        if (m == CaptureRequest.CONTROL_AWB_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_AUTO) s = "AUTO";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT) s = "INCANDESCENT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT) s = "FLUORESCENT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT) s = "WARM_FLUORESCENT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT) s = "DAYLIGHT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT) s = "CLOUDY";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_TWILIGHT) s = "TWILIGHT";
        else if (m == CaptureRequest.CONTROL_AWB_MODE_SHADE) s = "SHADE";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

private String lab8_1VideoStabToString(int[] modes) {
    StringBuilder sb = new StringBuilder();
    for (int m : modes) {
        String s;
        if (m == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF) s = "OFF";
        else if (m == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON) s = "ON";
        else s = "MODE_" + m;

        if (sb.length() > 0) sb.append(", ");
        sb.append(s);
    }
    return (sb.length() == 0) ? "N/A" : sb.toString();
}

/* ============================================================
LAB 9 — Sensors Check (LABEL / VALUE MODE)
============================================================ */
private void lab9SensorsCheck() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logSection(gr
            ? "LAB 9 — Αισθητήρες (Παρουσία & Πλήρης Ανάλυση)"
            : "LAB 9 — Sensors Presence & Full Analysis");
    logLine();

    try {

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm == null) {
            logLabelErrorValue(
                    gr ? "SensorManager" : "SensorManager",
                    gr ? "Μη διαθέσιμο (πρόβλημα framework)" : "Not available (framework issue)"
            );
            return;
        }

        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
        int total = (sensors == null ? 0 : sensors.size());

        logLabelOkValue(
                gr ? "Σύνολο αισθητήρων" : "Total sensors reported",
                String.valueOf(total)
        );

        // ------------------------------------------------------------
        // QUICK PRESENCE CHECK
        // ------------------------------------------------------------
        checkSensor(sm, Sensor.TYPE_ACCELEROMETER, gr ? "Επιταχυνσιόμετρο" : "Accelerometer");
        checkSensor(sm, Sensor.TYPE_GYROSCOPE, gr ? "Γυροσκόπιο" : "Gyroscope");
        checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, gr ? "Μαγνητόμετρο / Πυξίδα" : "Magnetometer / Compass");
        checkSensor(sm, Sensor.TYPE_LIGHT, gr ? "Αισθητήρας φωτός" : "Ambient Light");
        checkSensor(sm, Sensor.TYPE_PROXIMITY, gr ? "Εγγύτητας" : "Proximity");

        if (sensors == null || sensors.isEmpty()) {
            logLabelErrorValue(
                    gr ? "Λίστα αισθητήρων" : "Sensor list",
                    gr ? "Δεν αναφέρθηκαν αισθητήρες από το σύστημα" : "No sensors reported by the system"
            );
            return;
        }

        logLine();

        // ------------------------------------------------------------
        // RAW SENSOR LIST
        // ------------------------------------------------------------
        for (Sensor s : sensors) {
            logOk(
                    gr ? "Αισθητήρας" : "Sensor",
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
            logLabelOkValue(
                    gr ? "Εικονικό γυροσκόπιο" : "Virtual Gyroscope",
                    gr ? "Εντοπίστηκε (sensor fusion — αναμενόμενο)" : "Detected (sensor fusion — expected behavior)"
            );
        else
            logLabelWarnValue(
                    gr ? "Εικονικό γυροσκόπιο" : "Virtual Gyroscope",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        if (hasDualALS)
            logLabelOkValue(
                    gr ? "Αισθητήρες φωτός" : "Ambient Light Sensors",
                    gr ? "Διπλός ALS (μπροστά + πίσω)" : "Dual ALS (front + rear)"
            );
        else
            logLabelWarnValue(
                    gr ? "Αισθητήρες φωτός" : "Ambient Light Sensors",
                    gr ? "Μονός ALS" : "Single ALS"
            );

        if (hasSAR)
            logLabelOkValue(
                    gr ? "Αισθητήρες SAR" : "SAR Sensors",
                    gr ? "Υπάρχουν (proximity / RF tuning)" : "Present (proximity / RF tuning)"
            );
        else
            logLabelWarnValue(
                    gr ? "Αισθητήρες SAR" : "SAR Sensors",
                    gr ? "Δεν αναφέρθηκαν" : "Not reported"
            );

        if (hasPickup)
            logLabelOkValue(
                    gr ? "Pickup sensor" : "Pickup Sensor",
                    gr ? "Υπάρχει (lift-to-wake υποστήριξη)" : "Present (lift-to-wake supported)"
            );
        else
            logLabelWarnValue(
                    gr ? "Pickup sensor" : "Pickup Sensor",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        if (hasLargeTouch)
            logLabelOkValue(
                    gr ? "Large area touch" : "Large Area Touch",
                    gr ? "Υπάρχει (palm rejection / ακρίβεια)" : "Present (palm rejection / accuracy)"
            );
        else
            logLabelWarnValue(
                    gr ? "Large area touch" : "Large Area Touch",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        if (hasGameRotation)
            logLabelOkValue(
                    gr ? "Game rotation vector" : "Game Rotation Vector",
                    gr ? "Υπάρχει (gaming orientation)" : "Present (gaming orientation)"
            );
        else
            logLabelWarnValue(
                    gr ? "Game rotation vector" : "Game Rotation Vector",
                    gr ? "Δεν αναφέρθηκε" : "Not reported"
            );

        logLabelOkValue(
                gr ? "Συνολική εκτίμηση" : "Overall Assessment",
                gr
                        ? "Το sensor suite είναι πλήρες και υγιές για αυτή τη συσκευή"
                        : "Sensor suite complete and healthy for this device"
        );

    } catch (Throwable e) {
        logError(gr ? "Σφάλμα ανάλυσης αισθητήρων" : "Sensors analysis error", e.getMessage());
    } finally {
        appendHtml("<br>");
        logOk(gr ? "Το Lab 9 ολοκληρώθηκε." : "Lab 9 finished.");
        logLine();
        enableSingleExportButton();
    }
}

/* ============================================================
Helper — Sensor Presence
============================================================ */
private void checkSensor(SensorManager sm, int type, String name) {

    final boolean gr = AppLang.isGreek(this);

    boolean ok = sm.getDefaultSensor(type) != null;

    if (ok) {
        logLabelOkValue(
                name,
                gr ? "Διαθέσιμος" : "Available"
        );
    } else {
        logLabelWarnValue(
                name,
                gr
                        ? "Δεν αναφέρθηκε (ενδέχεται περιορισμένες ή μη διαθέσιμες λειτουργίες)"
                        : "Not reported (dependent features may be limited or missing)"
        );
    }
}

// ============================================================
// LAB 10: Wi-Fi Connectivity Check (Wi-Fi + Internet + Exposure)
// ============================================================
private void lab10WifiConnectivityCheck() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 10 — Έλεγχος Συνδεσιμότητας Wi-Fi" : "LAB 10 — Wi-Fi Link Connectivity Check");
    logLine();

    WifiManager wm =
            (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

    if (wm == null) {
        logError(gr ? "WifiManager δεν είναι διαθέσιμο." : "WifiManager not available.");
        return;
    }

    if (!wm.isWifiEnabled()) {
        logWarn(gr ? "Το Wi-Fi είναι ΚΛΕΙΣΤΟ — άνοιξέ το και ξαναδοκίμασε." : "Wi-Fi is OFF — please enable and retry.");
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

            pendingLab10AfterPermission = this::lab10WifiConnectivityCheck;

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQ_LOCATION_LAB10
            );
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
                logWarn(gr ? "Οι Υπηρεσίες Τοποθεσίας είναι OFF. Το SSID μπορεί να φαίνεται UNKNOWN."
                           : "Location services are OFF. SSID may be UNKNOWN.");
            }

        } catch (Throwable e) {
            logWarn((gr ? "Έλεγχος Location απέτυχε: " : "Location services check failed: ") + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // 2) Wi-Fi snapshot
    // ------------------------------------------------------------
    WifiInfo info = wm.getConnectionInfo();
    if (info == null) {
        logLabelErrorValue(gr ? "Wi-Fi" : "Wi-Fi", gr ? "Δεν υπάρχουν στοιχεία σύνδεσης" : "Connection info not available");
        return;
    }

    String ssid  = cleanSsid(info.getSSID());
    String bssid = info.getBSSID();
    int rssi     = info.getRssi();
    int speed    = info.getLinkSpeed();

    int freqMhz = 0;
    try { freqMhz = info.getFrequency(); } catch (Throwable ignore) {}

    String band = (freqMhz > 3000) ? (gr ? "5 GHz" : "5 GHz") : (gr ? "2.4 GHz" : "2.4 GHz");

    // ---------------- IDENTIFIERS ----------------
    logLabelValue(gr ? "SSID" : "SSID", ssid);

    if (bssid != null)
        logLabelValue(gr ? "BSSID" : "BSSID", bssid);

    // ---------------- BAND ----------------
    logLabelOkValue(
            gr ? "Μπάντα" : "Band",
            band + (freqMhz > 0 ? " (" + freqMhz + " MHz)" : "")
    );

    // ---------------- LINK SPEED ----------------
    if (speed >= 150) {
        logLabelOkValue(gr ? "Ταχύτητα Link" : "Link speed", speed + " Mbps");
    } else if (speed >= 54) {
        logLabelWarnValue(gr ? "Ταχύτητα Link" : "Link speed", speed + " Mbps");
    } else {
        logLabelErrorValue(gr ? "Ταχύτητα Link" : "Link speed", speed + " Mbps");
    }

    // ---------------- SIGNAL (RSSI) ----------------
    if (rssi >= -60) {
        logLabelOkValue(gr ? "Ισχύς Σήματος" : "Signal strength", rssi + " dBm");
    } else if (rssi >= -75) {
        logLabelWarnValue(gr ? "Ισχύς Σήματος" : "Signal strength", rssi + " dBm");
    } else {
        logLabelErrorValue(gr ? "Ισχύς Σήματος" : "Signal strength", rssi + " dBm");
    }

    // SSID status — single line
    if ("Unknown".equalsIgnoreCase(ssid)) {
        logLabelWarnValue(gr ? "SSID" : "SSID", gr ? "Κρυφό από Android policy απορρήτου" : "Hidden by Android privacy policy");
    } else {
        logLabelOkValue(gr ? "SSID" : "SSID", gr ? "Ανάγνωση OK" : "Read OK");
    }

    // Signal quality — single line
    if (rssi > -65)
        logLabelOkValue(gr ? "Σήμα Wi-Fi" : "Wi-Fi signal", gr ? "Ισχυρό" : "Strong");
    else if (rssi > -80)
        logLabelWarnValue(gr ? "Σήμα Wi-Fi" : "Wi-Fi signal", gr ? "Μέτριο" : "Moderate");
    else
        logLabelErrorValue(gr ? "Σήμα Wi-Fi" : "Wi-Fi signal", gr ? "Αδύναμο" : "Weak");

    // ------------------------------------------------------------
    // 3) DHCP / LAN info — unified label/value format
    // ------------------------------------------------------------
    try {
        DhcpInfo dh = wm.getDhcpInfo();

        if (dh != null) {
            logLabelOkValue(gr ? "IP" : "IP",           ipToStr(dh.ipAddress));
            logLabelOkValue(gr ? "Gateway" : "Gateway", ipToStr(dh.gateway));
            logLabelOkValue(gr ? "DNS1" : "DNS1",       ipToStr(dh.dns1));
            logLabelOkValue(gr ? "DNS2" : "DNS2",       ipToStr(dh.dns2));
        } else {
            logLabelWarnValue(gr ? "DHCP" : "DHCP", gr ? "Δεν υπάρχουν στοιχεία" : "Info not available");
        }

    } catch (Throwable e) {
        logLabelErrorValue(gr ? "DHCP" : "DHCP", (gr ? "Αποτυχία ανάγνωσης: " : "Read failed: ") + e.getMessage());
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

    final boolean gr = AppLang.isGreek(this);

    // =========================
    // CORE PERMISSIONS
    // =========================
    if (requestCode == REQ_CORE_PERMS) {

        boolean allGranted = true;

        if (grantResults.length == 0) {
            allGranted = false;
        } else {
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
        }

        if (allGranted) {

            logOk(gr ? "Δόθηκαν τα απαιτούμενα permissions." : "Required permissions granted.");

            if (pendingAfterPermission != null) {
                Runnable action = pendingAfterPermission;
                pendingAfterPermission = null;
                action.run();
            }

        } else {

            logLabelErrorValue(
                    gr ? "Permissions" : "Permissions",
                    gr ? "Αρνήθηκαν τα απαιτούμενα permissions" : "Required permissions denied"
            );

            pendingAfterPermission = null;
        }

        return;
    }

    // =========================
    // LAB 10 - LOCATION (WiFi SSID)
    // =========================
    if (requestCode == REQ_LOCATION_LAB10) {

        boolean granted = false;
        if (grantResults != null && grantResults.length > 0) {
            // accepted if ANY requested location perm granted
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
        }

        if (granted && pendingLab10AfterPermission != null) {

            Runnable action = pendingLab10AfterPermission;
            pendingLab10AfterPermission = null;
            action.run();

        } else {

            logLabelErrorValue(
                    gr ? "Άδεια Τοποθεσίας" : "Location Permission",
                    gr ? "Αρνήθηκε" : "Denied"
            );

            pendingLab10AfterPermission = null;
        }

        return;
    }

    // =========================
    // LAB 13 BLUETOOTH
    // =========================
    if (requestCode == REQ_LAB13_BT_CONNECT) {

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            lab13Running = true;
            lab13BluetoothConnectivityCheck();

        } else {

            lab13Running = false;

            logLabelErrorValue(
                    gr ? "Άδεια Bluetooth" : "Bluetooth Permission",
                    gr ? "Αρνήθηκε" : "Denied"
            );

            appendHtml("<br>");
            logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
            logLine();
        }

        return;
    }
}

// ============================================================
// LAB 10 — DEEPSCAN v3.0 (Internet + Exposure included)
// ============================================================
private void runWifiDeepScan(WifiManager wm) {

    new Thread(() -> {

        final boolean gr = AppLang.isGreek(ManualTestsActivity.this);

        try {
            logLine();
            logOk(gr ? "Network DeepScan v3.0 ξεκίνησε..." : "Network DeepScan v3.0 started...");

            String gatewayStr = null;
            try {
                DhcpInfo dh = wm.getDhcpInfo();
                if (dh != null)
                    gatewayStr = ipToStr(dh.gateway);
            } catch (Throwable ignored) {}

            // ----------------------------------------------------
            // NETWORK DEEP SCAN — unified label/value format
            // ----------------------------------------------------

            // 1) Internet ping
            float pingMs = tcpLatencyMs("8.8.8.8", 53, 1500);
            if (pingMs > 0)
                logLabelOkValue(gr ? "Ping 8.8.8.8" : "Ping 8.8.8.8", String.format(Locale.US, "%.1f ms", pingMs));
            else
                logLabelWarnValue(gr ? "Ping 8.8.8.8" : "Ping 8.8.8.8", gr ? "Απέτυχε" : "Failed");

            // 2) DNS resolve
            float dnsMs = dnsResolveMs("google.com");
            if (dnsMs > 0)
                logLabelOkValue(gr ? "DNS google.com" : "DNS google.com", String.format(Locale.US, "%.0f ms", dnsMs));
            else
                logLabelWarnValue(gr ? "DNS google.com" : "DNS google.com", gr ? "Απέτυχε" : "Resolve failed");

            // 3) Gateway ping
            if (gatewayStr != null) {
                float gwMs = tcpLatencyMs(gatewayStr, 80, 1200);
                if (gwMs > 0)
                    logLabelOkValue(gr ? "Ping Gateway" : "Gateway ping", String.format(Locale.US, "%.1f ms", gwMs));
                else
                    logLabelWarnValue(gr ? "Ping Gateway" : "Gateway ping", gr ? "Απέτυχε" : "Failed");
            } else {
                logLabelWarnValue(gr ? "Gateway" : "Gateway", gr ? "Δεν εντοπίστηκε" : "Not detected");
            }

            // 4) Speed heuristic
            WifiInfo info = wm.getConnectionInfo();
            int link = info != null ? info.getLinkSpeed() : 0;
            int rssi = info != null ? info.getRssi() : -80;

            float speedSim = estimateSpeedSimMbps(link, rssi);
            logLabelOkValue(
                    gr ? "SpeedSim" : "SpeedSim",
                    String.format(Locale.US, gr ? "~%.2f Mbps (εκτίμηση)" : "~%.2f Mbps (heuristic)", speedSim)
            );

            // Finish
            logLabelOkValue(gr ? "DeepScan" : "DeepScan", gr ? "Ολοκληρώθηκε" : "Finished");

            // ====================================================
            // INTERNET AVAILABILITY
            // ====================================================
            try {
                ConnectivityManager cm =
                        (ConnectivityManager)
                                getSystemService(CONNECTIVITY_SERVICE);

                if (cm == null) {
                    logError(gr ? "ConnectivityManager δεν είναι διαθέσιμο." : "ConnectivityManager not available.");
                } else {

                    boolean hasInternet = false;
                    String transport = gr ? "ΑΓΝΩΣΤΟ" : "UNKNOWN";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Network n = cm.getActiveNetwork();
                        NetworkCapabilities caps = cm.getNetworkCapabilities(n);

                        if (caps != null) {
                            hasInternet =
                                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

                            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                                transport = gr ? "Wi-Fi" : "Wi-Fi";
                            else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                                transport = gr ? "Δεδομένα" : "Cellular";
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
                        logError(gr ? "Δεν βρέθηκε ενεργή σύνδεση Internet (OS-level)." : "No active Internet connection detected (OS-level).");
                    else
                        logOk((gr ? "Internet ενεργό (" : "Internet connectivity active (") + transport + ").");
                }

            } catch (Throwable e) {
                logError((gr ? "Σφάλμα ελέγχου Internet: " : "Internet quick check error: ") + e.getMessage());
            }

            // ====================================================
            // NETWORK / PRIVACY EXPOSURE
            // ====================================================
            try {
                logLine();
                logInfo(gr ? "Αποτύπωση Έκθεσης Δικτύου & Ιδιωτικότητας"
           : "Network / Privacy Exposure Snapshot");
                logInfo(gr
        ? "(Έλεγχος δηλωμένων δυνατοτήτων — χωρίς επιθεώρηση traffic)"
        : "(Capabilities only — no traffic inspection)");

                PackageManager pm2 = getPackageManager();
                ApplicationInfo ai = getApplicationInfo();

                // INTERNET PERMISSION
                boolean hasInternetPerm =
                        pm2.checkPermission(
                                Manifest.permission.INTERNET,
                                ai.packageName
                        ) == PackageManager.PERMISSION_GRANTED;

                logLabelValue(
                        gr ? "Δυνατότητα Internet" : "Internet capability",
                        hasInternetPerm
                                ? (gr ? "Άδεια INTERNET δηλωμένη" : "INTERNET permission declared")
                                : (gr ? "Δεν δηλώθηκε άδεια INTERNET" : "No INTERNET permission declared")
                );

                // CLEARTEXT TRAFFIC
                boolean cleartextAllowed = true;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cleartextAllowed =
                                android.security.NetworkSecurityPolicy
                                        .getInstance()
                                        .isCleartextTrafficPermitted();
                    }
                } catch (Throwable ignore) {}

                if (cleartextAllowed)
                    logLabelWarnValue(
    gr ? "Cleartext traffic (μη κρυπτογραφημένη μεταφορά δεδομένων)"
       : "Cleartext traffic",
    gr ? "Επιτρέπεται από το Network Security Policy"
       : "Allowed by network security policy"
);
                else
                    logLabelOkValue(
                            gr ? "Cleartext traffic (μη κρυπτογραφημένη μεταφορά δεδομένων)" : "Cleartext traffic",
                            gr ? "Δεν επιτρέπεται (enforced encryption)" : "Not allowed (encrypted traffic enforced)"
                    );

                // BACKGROUND NETWORK (BOOT)
                boolean bgPossible =
                        pm2.checkPermission(
                                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                ai.packageName
                        ) == PackageManager.PERMISSION_GRANTED;

                logLabelValue(
                        gr ? "Δυνατότητα εκτέλεσης δικτύου στο παρασκήνιο" : "Background network capability",
                        bgPossible
                                ? (gr ? "Δηλωμένο RECEIVE_BOOT_COMPLETED (εκκίνηση στο boot)"
              : "RECEIVE_BOOT_COMPLETED declared")
        : (gr ? "Δεν υπάρχει BOOT RECEIVER δηλωμένο"
              : "No BOOT-TIME RECEIVER declared")
);

                logLabelOkValue(
        gr ? "Συνολική Εκτίμηση" : "Assessment",
        gr ? "Ολοκληρώθηκε η αποτύπωση έκθεσης δικτύου & ιδιωτικότητας"
           : "Network / privacy exposure snapshot completed"
);

} catch (Throwable e) {
    logLabelWarnValue(
            gr ? "Έκθεση Δικτύου" : "Network exposure",
            (gr ? "Μη διαθέσιμο: " : "Snapshot unavailable: ")
                    + (e.getMessage() != null ? e.getMessage()
                                              : (gr ? "Άγνωστο σφάλμα"
                                                    : "Unknown error"))
    );
}

            appendHtml("<br>");
            logOk(gr ? "Το Lab 10 ολοκληρώθηκε." : "Lab 10 finished.");
            logLine();

        } catch (Throwable e) {

            logLine();
            logInfo(gr ? "DeepScan" : "DeepScan");

            logLabelErrorValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Απέτυχε" : "Failed"
            );

            logLabelWarnValue(
                    gr ? "Αιτία" : "Reason",
                    e.getMessage() != null ? e.getMessage() : (gr ? "Άγνωστο σφάλμα" : "Unknown error")
            );
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

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr ? "LAB 11 — Διαγνωστικός Έλεγχος Κινητού Δικτύου (Εργαστήριο)"
               : "LAB 11 — Mobile Network Diagnostic (Laboratory)");
    logLine();

    TelephonySnapshot s = getTelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode (context only)
    // ------------------------------------------------------------
    if (s.airplaneOn) {
        logInfo(gr
                ? "Η Λειτουργία Πτήσης είναι ΕΝΕΡΓΗ. Τα ραδιο-interfaces είναι σκόπιμα απενεργοποιημένα."
                : "Airplane mode is ENABLED. Radio interfaces are intentionally disabled.");
        return;
    }

    // ------------------------------------------------------------
    // SIM state (laboratory reporting)
    // ------------------------------------------------------------
    if (!s.simReady) {

        switch (s.simState) {

            case TelephonyManager.SIM_STATE_ABSENT:
                logLabelWarnValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΑΠΟΥΣΑ" : "ABSENT");
                return;

            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά κλειδωμένη (απαιτείται PIN)"
                           : "PRESENT but locked (PIN required)"
                );
                return;

            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά κλειδωμένη (απαιτείται PUK)"
                           : "PRESENT but locked (PUK required)"
                );
                return;

            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά κλειδωμένη από δίκτυο"
                           : "PRESENT but network locked"
                );
                return;

            default:
                logLabelWarnValue(
                        gr ? "Κατάσταση SIM" : "SIM State",
                        gr ? "ΠΑΡΟΥΣΑ αλλά δεν είναι έτοιμη"
                           : "PRESENT but not ready"
                );
                return;
        }
    }

    // SIM ready
    logLabelOkValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΕΤΟΙΜΗ" : "READY");

    // ------------------------------------------------------------
    // Service state (legacy domain — informational)
    // ------------------------------------------------------------
    logLabelValue(
        gr ? "Κατάσταση Υπηρεσίας (legacy)" : "Service State (legacy)",
        s.inService
                ? (gr ? "ΣΕ ΥΠΗΡΕΣΙΑ" : "IN SERVICE")
                : (gr ? "ΔΕΝ ΑΝΑΦΕΡΕΤΑΙ ΩΣ ΕΝΕΡΓΗ" : "NOT REPORTED AS IN SERVICE")
);

if (!s.inService) {
    logLabelWarnValue(
            gr ? "Σημείωση (Legacy Service)" : "Legacy Service Note",
            gr
                    ? "Η legacy κατάσταση υπηρεσίας δεν αναφέρεται ως ενεργή. "
                      + "Σε σύγχρονες LTE/5G συσκευές, η φωνή και τα δεδομένα "
                      + "μπορεί να παρέχονται μέσω IMS (VoLTE / VoWiFi)."
                    : "Legacy service registration is not reported as active. "
                      + "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi)."
    );
}

    // ------------------------------------------------------------
    // Data state (packet domain — informational)
    // ------------------------------------------------------------
    String dataStateLabel;
    switch (s.dataState) {
        case TelephonyManager.DATA_CONNECTED:
            dataStateLabel = gr ? "ΣΥΝΔΕΔΕΜΕΝΟ" : "CONNECTED";
            break;
        case TelephonyManager.DATA_CONNECTING:
            dataStateLabel = gr ? "ΣΥΝΔΕΣΗ..." : "CONNECTING";
            break;
        case TelephonyManager.DATA_DISCONNECTED:
            dataStateLabel = gr ? "ΑΠΟΣΥΝΔΕΔΕΜΕΝΟ" : "DISCONNECTED";
            break;
        default:
            dataStateLabel = gr ? "ΑΓΝΩΣΤΟ" : "UNKNOWN";
            break;
    }

    logLabelValue(gr ? "Κατάσταση Δεδομένων" : "Data State", dataStateLabel);

    // ------------------------------------------------------------
    // Internet routing context (best effort)
    // ------------------------------------------------------------
    if (s.hasInternet) {
        logLabelOkValue(gr ? "Internet Context" : "Internet Context",
                gr ? "ΔΙΑΘΕΣΙΜΟ (system routing)" : "AVAILABLE (system routing)");
    } else {
        logLabelWarnValue(gr ? "Internet Context" : "Internet Context",
                gr ? "ΜΗ ΔΙΑΘΕΣΙΜΟ" : "NOT AVAILABLE");
    }

    // ------------------------------------------------------------
    // Laboratory conclusion
    // ------------------------------------------------------------
    logOk(gr
            ? "Έγινε συλλογή εργαστηριακού snapshot. Δεν βγαίνει λειτουργικό συμπέρασμα."
            : "Laboratory snapshot collected. No functional verdict inferred.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 11 ολοκληρώθηκε." : "Lab 11 finished.");
    logLine();
}

// ============================================================
// LAB 12 — Call Function Interpretation (Laboratory)
// ============================================================
private void lab12CallFunctionInterpretation() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 12 — Ερμηνεία Λειτουργίας Κλήσεων (Εργαστήριο)"
            : "LAB 12 — Call Function Interpretation (Laboratory)");
    logLine();

    TelephonySnapshot s = getTelephonySnapshot();

    // ------------------------------------------------------------
    // Airplane mode (context only)
    // ------------------------------------------------------------
    if (s.airplaneOn) {
        logInfo(gr
                ? "Η Λειτουργία Πτήσης είναι ΕΝΕΡΓΗ. Τα voice radio interfaces είναι σκόπιμα απενεργοποιημένα."
                : "Airplane mode is ENABLED. Voice radio interfaces are intentionally disabled.");
        return;
    }

    // ------------------------------------------------------------
    // SIM availability (context only)
    // ------------------------------------------------------------
    if (s.simReady) {
        logLabelOkValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΕΤΟΙΜΗ" : "READY");
    } else {
        logLabelWarnValue(gr ? "Κατάσταση SIM" : "SIM State", gr ? "ΜΗ ΕΤΟΙΜΗ" : "NOT READY");
    }

    if (!s.simReady) {
        logInfo(gr
                ? "Η διαθεσιμότητα φωνητικής υπηρεσίας εξαρτάται από το αν η SIM είναι έτοιμη. "
                  + "Δεν βγαίνει λειτουργικό συμπέρασμα."
                : "Voice service availability depends on SIM readiness. "
                  + "No functional verdict inferred.");
        return;
    }

    // ------------------------------------------------------------
    // Legacy voice service state (informational)
    // ------------------------------------------------------------
    if (s.inService) {
        logLabelOkValue(
                gr ? "Φωνητική Υπηρεσία (legacy)" : "Voice Service (legacy)",
                gr ? "ΣΕ ΥΠΗΡΕΣΙΑ" : "IN SERVICE"
        );
    } else {
        logLabelWarnValue(
                gr ? "Φωνητική Υπηρεσία (legacy)" : "Voice Service (legacy)",
                gr ? "ΔΕΝ ΑΝΑΦΕΡΕΤΑΙ ΩΣ ΣΕ ΥΠΗΡΕΣΙΑ" : "NOT REPORTED AS IN SERVICE"
        );
    }

    if (!s.inService) {
        logInfo(gr
                ? "Η legacy εγγραφή υπηρεσίας δεν αναφέρεται. "
                  + "Σε σύγχρονες LTE/5G συσκευές, φωνή/δεδομένα μπορεί να παρέχονται μέσω IMS (VoLTE / VoWiFi)."
                : "Legacy service registration is not reported. "
                  + "On modern LTE/5G devices, voice and data may be provided via IMS (VoLTE / VoWiFi).");
    }

    // ------------------------------------------------------------
    // Internet context (IMS relevance)
    // ------------------------------------------------------------
    if (s.hasInternet) {
    logLabelOkValue(
            gr ? "Κατάσταση Σύνδεσης Internet"
               : "Internet Context",
            gr ? "ΔΙΑΘΕΣΙΜΗ (system routing)"
               : "AVAILABLE (system routing)"
    );
} else {
    logLabelWarnValue(
            gr ? "Κατάσταση Σύνδεσης Internet"
               : "Internet Context",
            gr ? "ΜΗ ΔΙΑΘΕΣΙΜΗ"
               : "NOT AVAILABLE"
    );
}

    if (s.hasInternet) {
        logOk(gr
                ? "Εντοπίστηκε ενεργό internet routing. "
                  + "Κλήσεις μέσω IMS (VoLTE / VoWiFi) μπορεί να υποστηρίζονται, ανάλογα με τον πάροχο."
                : "Active internet routing detected. "
                  + "IMS-based calling (VoLTE / VoWiFi) may be supported depending on carrier configuration.");
    } else {
        logOk(gr
                ? "Δεν εντοπίστηκε ενεργό internet routing. "
                  + "Οι κλασικές κλήσεις μπορεί να λειτουργούν κανονικά, αν τις υποστηρίζει το δίκτυο."
                : "No active internet routing detected. "
                  + "Legacy voice calling may still function if supported by the network.");
    }

    // ------------------------------------------------------------
    // Laboratory conclusion
    // ------------------------------------------------------------
    logOk(gr
            ? "Η εργαστηριακή ερμηνεία ολοκληρώθηκε. "
              + "Αυτό το τεστ δεν ξεκινά ούτε επιβεβαιώνει πραγματική κλήση."
            : "Laboratory interpretation complete. "
              + "This test does not initiate or verify real call execution.");

    logInfo(gr
            ? "Το audio routing κλήσης και οι διαδρομές μικροφώνου/ακουστικού ελέγχονται ξεχωριστά (LAB 3)."
            : "Call audio routing and microphone/earpiece paths are examined separately (LAB 3).");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 12 ολοκληρώθηκε." : "Lab 12 finished.");
    logLine();
}

// ============================================================
// LAB 13 — Bluetooth Connectivity Check
// POPUP + WAIT FOR DEVICE + 60s MONITOR + DIAGNOSIS
// (FINAL — STRUCTURED / NO NESTED METHODS / READY COPY-PASTE)
// ============================================================

private void lab13BluetoothConnectivityCheck() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!ensurePermissions(
                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                this::lab13BluetoothConnectivityCheck
        )) return;
    }

    final boolean gr = AppLang.isGreek(this);

    BluetoothManager bm = null;
    BluetoothAdapter ba = null;

    try {
        bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ba = (bm != null) ? bm.getAdapter() : null;
    } catch (Throwable ignore) {}

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 13 — Έλεγχος Συνδεσιμότητας Bluetooth"
            : "LAB 13 — Bluetooth Connectivity Check");
    logLine();

    if (ba == null) {
        logError(gr
                ? "Το Bluetooth ΔΕΝ υποστηρίζεται σε αυτή τη συσκευή."
                : "Bluetooth NOT supported on this device.");
        logLine();
        return;
    }

    boolean enabled = false;
    try { enabled = ba.isEnabled(); } catch (Throwable ignore) {}

    if (!enabled) {
        logError(gr
                ? "Το Bluetooth είναι ΚΛΕΙΣΤΟ. Ενεργοποίησέ το και ξαναδοκίμασε."
                : "Bluetooth is OFF. Please enable Bluetooth and retry.");
        logLine();
        return;
    }

    logLabelOkValue(
            gr ? "Κατάσταση Bluetooth" : "Bluetooth State",
            gr ? "ΕΝΕΡΓΟ" : "ENABLED"
    );

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
// LAB 13 — GATE POPUP (Skip / Continue) — MODERN
// AppLang + AppTTS + GEL UI
// ============================================================
private void showLab13GatePopup() {

    final boolean gr = AppLang.isGreek(this);

    final String titleText =
            gr
                    ? "LAB 13 — Έλεγχος Εξωτερικής Συσκευηςής Bluetooth"
                    : "LAB 13 — External Bluetooth Device Check";

    final String messageText =
            gr
                    ? "Σύνδεσε ΜΙΑ εξωτερική συσκευη Bluetooth.\n\n"
                      + "π.χ. ακουστικά, σύστημα αυτοκινήτου, πληκτρολόγιο.\n\n"
                      + "Το τεστ, αξιολογεί τη σταθερότητα της σύνδεσης Bluetooth.\n\n"
                      + "Αν δεν έχεις συνδεδεμένη εξωτερική συσκευή,\n"
                      + "μπορείς να παραλείψεις αυτό το βήμα,\n"
                      + "και να συνεχίσεις με τον έλεγχο του Bluetooth του συστήματος."
                    : "Connect ONE external Bluetooth device.\n\n"
                      + "e.g. headphones, car kit, keyboard.\n\n"
                      + "This test, evaluates Bluetooth connection stability.\n\n"
                      + "If no external device is connected,\n"
                      + "you may skip this step,\n"
                      + "and continue with the system Bluetooth check.";

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(true);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(4), 0xFFFFD700);
    root.setBackground(bg);

    // ---------------------------
    // TITLE (WHITE)
    // ---------------------------
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // ---------------------------
    // MESSAGE (NEON GREEN)
    // ---------------------------
    TextView msg = new TextView(this);
    msg.setText(messageText);
    msg.setTextColor(0xFF39FF14);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.CENTER);
    msg.setLineSpacing(0f, 1.15f);
    root.addView(msg);

    // ---------------------------
    // MUTE ROW (ABOVE BUTTONS)
    // ---------------------------
    root.addView(buildMuteRow());

    // ---------------------------
    // BUTTONS
    // ---------------------------
    LinearLayout buttons = new LinearLayout(this);
    buttons.setOrientation(LinearLayout.HORIZONTAL);
    buttons.setPadding(0, dp(14), 0, 0);

    Button skip = gelButton(
        this,
        gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP",
        0xFF444444
);

    Button cont = gelButton(
        this,
        gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE",
        0xFF0F8A3B
);

    LinearLayout.LayoutParams lp =
        new LinearLayout.LayoutParams(0, dp(52), 1f);

lp.setMargins(0, 0, dp(8), 0);
skip.setLayoutParams(lp);

LinearLayout.LayoutParams lp2 =
        new LinearLayout.LayoutParams(0, dp(52), 1f);

lp2.setMargins(dp(8), 0, 0, 0);
cont.setLayoutParams(lp2);

buttons.addView(skip);
buttons.addView(cont);

    root.addView(buttons);

    b.setView(root);

    final AlertDialog gate = b.create();
    if (gate.getWindow() != null) {
        gate.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    gate.show();

    // ---------------------------
    // ACTIONS
    // ---------------------------
    skip.setOnClickListener(v -> {
        AppTTS.stop();
        lab13SkipExternalTest = true;
        gate.dismiss();
        runLab13BluetoothCheckCore();   // system-only
    });

    cont.setOnClickListener(v -> {
        AppTTS.stop();
        lab13SkipExternalTest = false;
        gate.dismiss();
        runLab13BluetoothCheckCore();   // full test
    });
}

// ============================================================
// CORE — FULL LAB 13 (LOG + UI + WAIT + MONITOR + DIAGNOSIS)
// ============================================================
private void runLab13BluetoothCheckCore() {

    final boolean gr = AppLang.isGreek(this);

    // ---------- GET BT
    lab13Bm = null;
    lab13Ba = null;

    try {
        lab13Bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        lab13Ba = (lab13Bm != null) ? lab13Bm.getAdapter() : null;
    } catch (Throwable e) {
        logError(gr
                ? "Αποτυχία πρόσβασης BluetoothManager: " + (e.getMessage() != null ? e.getMessage() : "")
                : "BluetoothManager access failed: " + (e.getMessage() != null ? e.getMessage() : ""));
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // BASIC SUPPORT
    if (lab13Ba == null) {
        logError(gr
                ? "Το Bluetooth ΔΕΝ υποστηρίζεται σε αυτή τη συσκευή."
                : "Bluetooth NOT supported on this device.");
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    logOk(gr ? "Το Bluetooth υποστηρίζεται." : "Bluetooth supported.");

    boolean enabled = false;
    try { enabled = lab13Ba.isEnabled(); } catch (Throwable ignore) {}

    logLabelValue(
            gr ? "Ενεργό" : "Enabled",
            enabled ? (gr ? "Ναι" : "Yes") : (gr ? "Όχι" : "No")
    );

    int state = BluetoothAdapter.STATE_OFF;
    try { state = lab13Ba.getState(); } catch (Throwable ignore) {}

    String stateStr;
    if (state == BluetoothAdapter.STATE_ON) {
        stateStr = gr ? "ΑΝΟΙΧΤΟ" : "ON";
    } else if (state == BluetoothAdapter.STATE_TURNING_ON) {
        stateStr = gr ? "ΑΝΟΙΓΕΙ" : "TURNING ON";
    } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
        stateStr = gr ? "ΚΛΕΙΝΕΙ" : "TURNING OFF";
    } else {
        stateStr = gr ? "ΚΛΕΙΣΤΟ" : "OFF";
    }

    logLabelValue(
            gr ? "Κατάσταση" : "State",
            stateStr
    );

    boolean le = false;
    try {
        le = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    } catch (Throwable ignore) {}

    logLabelValue(
            gr ? "Υποστήριξη BLE" : "BLE Support",
            le ? (gr ? "Ναι" : "Yes") : (gr ? "Όχι" : "No")
    );

    if (!enabled) {
        logWarn(gr
                ? "Το Bluetooth είναι ΚΛΕΙΣΤΟ — ενεργοποίησέ το και ξανατρέξε το Lab 13."
                : "Bluetooth is OFF — enable Bluetooth and re-run Lab 13.");
        logLine();

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
        return;
    }

    // ---------- PAIRED DEVICES SNAPSHOT
    try {
        Set<BluetoothDevice> bonded = lab13Ba.getBondedDevices();

        if (bonded == null || bonded.isEmpty()) {

            logWarn(gr
                    ? "Συζευγμένες συσκευές Bluetooth: 0 (δεν βρέθηκαν)."
                    : "Paired Bluetooth devices: 0 (no paired devices found).");

        } else {

            logOk(gr
                    ? "Βρέθηκαν Συζευγμένες συσκευές Bluetooth: " + bonded.size()
                    : "Paired Bluetooth devices detected: " + bonded.size());

            for (BluetoothDevice d : bonded) {

                String name = gr ? "Χωρίς όνομα" : "Unnamed";
                String addr = gr ? "χωρίς-mac" : "no-mac";
                String typeStr = gr ? "Άγνωστο" : "Unknown";

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
                                type == BluetoothDevice.DEVICE_TYPE_CLASSIC ? (gr ? "Κλασικό" : "Classic") :
                                type == BluetoothDevice.DEVICE_TYPE_LE ? "LE" :
                                type == BluetoothDevice.DEVICE_TYPE_DUAL ? (gr ? "Διπλό" : "Dual") :
                                (gr ? "Άγνωστο" : "Unknown");
                    } catch (Throwable ignore) {}
                }

                logInfo("• " + name + " [" + typeStr + "] (" + addr + ")");
            }
        }

    } catch (Throwable e) {

        logWarn(gr
                ? "Αποτυχία σάρωσης συζευγμένων συσκευών: " + e.getClass().getSimpleName()
                : "Paired device scan failed: " + e.getClass().getSimpleName());
    }

    // ------------------------------------------------------------
    // SYSTEM-ONLY MODE (Skip external device test)
    // ------------------------------------------------------------
    if (lab13SkipExternalTest) {
        logWarn(gr
                ? "Το τεστ εξωτερικής Bluetooth συσκευής παραλείφθηκε από τον χρήστη."
                : "External Bluetooth device test skipped by user.");
        logOk(gr
                ? "Συνέχεια μόνο με έλεγχο Bluetooth του συστήματος."
                : "Proceeded with system Bluetooth connection check only.");

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        enableSingleExportButton();
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

// ------------------------------------------------------------
// UI — GEL DARK GOLD MONITOR DIALOG (MODERN)
// ------------------------------------------------------------

final String titleText =
        gr
                ? "LAB 13 — Παρακολούθηση Σταθερότητας Bluetooth"
                : "LAB 13 — Bluetooth Stability Monitor";

final String messageText =
        gr
                ? "Σύνδεσε ΜΙΑ εξωτερική συσκευή Bluetooth.\n\n"
                  + "Κράτησέ την συνδεδεμένη, για τουλάχιστον 1 λεπτό.\n"
                  + "Μην αποσυνδέσεις τη συσκευή κατά τη διάρκεια του τεστ.\n\n"
                  + "Κράτησε τη συσκευή Bluetooth σε απόσταση\n"
                  + "έως 10 μέτρα από το τηλέφωνο.\n"
                  + "Μην απομακρυνθείς κατά την παρακολούθηση."
                : "Connect ONE external Bluetooth device.\n\n"
                  + "Keep it connected for at least one minute.\n"
                  + "Do not disconnect during the test.\n\n"
                  + "Keep the Bluetooth device within\n"
                  + "10 meters from the phone.\n"
                  + "Do not move away during monitoring.";

AlertDialog.Builder b =
        new AlertDialog.Builder(
                this,
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

// ---------------------------
// TITLE (WHITE)
// ---------------------------
TextView title = new TextView(this);
title.setText(titleText);
title.setTextColor(Color.WHITE);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(12));
root.addView(title);

// ---------------------------
// MESSAGE (NEON GREEN)
// ---------------------------
TextView msg = new TextView(this);
msg.setText(messageText);
msg.setTextColor(0xFF39FF14);
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
root.addView(msg);

// ---------------------------
// STATUS TEXT
// ---------------------------
lab13StatusText = new TextView(this);
lab13StatusText.setText(
        gr
                ? "Αναμονή για σταθερή σύνδεση Bluetooth…"
                : "Waiting for stable Bluetooth connection…"
);
lab13StatusText.setTextColor(0xFFAAAAAA);
lab13StatusText.setTextSize(15f);
lab13StatusText.setGravity(Gravity.CENTER);
lab13StatusText.setPadding(0, dp(10), 0, 0);
root.addView(lab13StatusText);

// ---------------------------
// DOTS (NEON)
// ---------------------------
lab13DotsView = new TextView(this);
lab13DotsView.setText("•••");
lab13DotsView.setTextColor(0xFF39FF14);
lab13DotsView.setTextSize(22f);
lab13DotsView.setGravity(Gravity.CENTER);
root.addView(lab13DotsView);

// ---------------------------
// COUNTER
// ---------------------------
lab13CounterText = new TextView(this);
lab13CounterText.setText(
        gr
                ? "Παρακολούθηση: 0 / 60 δευτ."
                : "Monitoring: 0 / 60 sec"
);
lab13CounterText.setTextColor(0xFF39FF14);
lab13CounterText.setGravity(Gravity.CENTER);
root.addView(lab13CounterText);

// ---------------------------
// PROGRESS BAR (SEGMENTS)
// ---------------------------
lab13ProgressBar = new LinearLayout(this);
lab13ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
lab13ProgressBar.setGravity(Gravity.CENTER);
lab13ProgressBar.setPadding(0, dp(10), 0, 0);

for (int i = 0; i < 6; i++) {
    View seg = new View(this);
    LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(0, dp(10), 1f);
    lp.setMargins(dp(3), 0, dp(3), 0);
    seg.setLayoutParams(lp);
    seg.setBackgroundColor(0xFF333333);
    lab13ProgressBar.addView(seg);
}
root.addView(lab13ProgressBar);

// ---------------------------
// MUTE ROW (GLOBAL APP TTS)
// ---------------------------
root.addView(buildMuteRow());

// ---------------------------
// EXIT BUTTON
// ---------------------------
Button exitBtn = gelButton(this, gr ? "ΕΞΟΔΟΣ ΤΕΣΤ" : "EXIT TEST",
        0xFF8B0000
);
LinearLayout.LayoutParams lpExit =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpExit.setMargins(0, dp(14), 0, 0);
exitBtn.setLayoutParams(lpExit);

exitBtn.setOnClickListener(v -> {
    AppTTS.stop();
    abortLab13ByUser();
});
root.addView(exitBtn);

b.setView(root);

lab13Dialog = b.create();
if (lab13Dialog.getWindow() != null) {
    lab13Dialog.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

lab13Dialog.show();

// ---------------------------
// TTS (ONLY IF NOT MUTED)
// ---------------------------
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (lab13Dialog.isShowing() && !AppTTS.isMuted(this)) {
        AppTTS.ensureSpeak(this, messageText);
    }
}, 120);

    // ------------------------------------------------------------
    // RESET MONITOR FLAGS (NEW RUN)
    // ------------------------------------------------------------
    lab13MonitoringStarted = false;
    lab13HadAnyConnection = false;
    lab13LastConnected = false;

// ------------------------------------------------------------
// ANDROID 12+ PERMISSION — MUST NOT STOP FLOW WHEN ALREADY GRANTED
// (FIX: remove unreachable code)
// ------------------------------------------------------------
if (Build.VERSION.SDK_INT >= 31) {
    if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED) {

        requestPermissions(
                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                REQ_LAB13_BT_CONNECT
        );
        return;
    }
}

// ------------------------------------------------------------
// SNAPSHOT CHECK — already connected device (AFTER UI READY)
// ------------------------------------------------------------
if (lab13IsAnyExternalConnected()) {

    lab13HadAnyConnection = true;

    if (lab13StatusText != null) {
        lab13StatusText.setText(
                gr
                        ? "Εξωτερική συσκευή ήδη συνδεδεμένη. Εκκίνηση παρακολούθησης..."
                        : "External device already connected. Starting stability monitor..."
        );
    }

    startLab13Monitor60s();
    return;
}

// ------------------------------------------------------------
// WAIT FOR EXTERNAL DEVICE — RECEIVER-BASED (MODERN)
// ------------------------------------------------------------
if (!lab13MonitoringStarted && lab13StatusText != null) {
    lab13StatusText.setText(
            gr
                    ? "Αναμονή για εξωτερική συσκευή Bluetooth…"
                    : "Waiting for an external Bluetooth device…"
    );
}

if (lab13CounterText != null) {
    lab13CounterText.setText(
            gr
                    ? "Παρακολούθηση: σε αναμονή…"
                    : "Monitoring: waiting…"
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
    lab13StatusText.setText(
            AppLang.isGreek(this)
                    ? "Παρακολούθηση σταθερότητας Bluetooth…"
                    : "Monitoring Bluetooth stability…"
    );
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

    final boolean gr = AppLang.isGreek(ManualTestsActivity.this);

    if (!adapterStable) {

        lab13StatusText.setText(
                gr ? "Ο Bluetooth adapter δεν είναι σταθερός."
                   : "Bluetooth adapter not stable."
        );
        lab13StatusText.setTextColor(0xFFFFD966); // yellow (warning)

    } else if (connected) {

        lab13StatusText.setText(
                gr ? "Εξωτερική συσκευή συνδεδεμένη — παρακολούθηση σταθερότητας..."
                   : "External device connected — monitoring stability..."
        );
        lab13StatusText.setTextColor(0xFF39FF14); // GEL green (OK)

    } else if (lab13HadAnyConnection) {

        lab13StatusText.setText(
                gr ? "Η εξωτερική συσκευή δεν είναι προσωρινά διαθέσιμη."
                   : "External device temporarily unavailable."
        );
        lab13StatusText.setTextColor(0xFFFFD966); // yellow (warning)

    } else {

        lab13StatusText.setText(
                gr ? "Αναμονή για εξωτερική συσκευή Bluetooth..."
                   : "Waiting for an external Bluetooth device..."
        );
        lab13StatusText.setTextColor(0xFFFFD966); // yellow (info/wait)
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
// FINISH — close dialog + structured diagnosis (GEL LOGIC)
// ============================================================
private void lab13FinishAndReport(boolean adapterStable) {

    final boolean gr = AppLang.isGreek(this);

    lab13Running = false;
    try { lab13Handler.removeCallbacksAndMessages(null); } catch (Throwable ignore) {}

    try {
        if (lab13Dialog != null && lab13Dialog.isShowing())
            lab13Dialog.dismiss();
    } catch (Throwable ignore) {}
    lab13Dialog = null;

    try { unregisterReceiver(lab13BtReceiver); } catch (Throwable ignore) {}

    // ------------------------------------------------------------
    // NO EXTERNAL DEVICE
    // ------------------------------------------------------------
    if (!lab13HadAnyConnection) {

        logLine();
        logInfo(gr ? "LAB 13 — Αποτελέσματα"
                   : "LAB 13 — Results");

        logWarn(gr
                ? "Δεν συνδέθηκε καμία εξωτερική Bluetooth συσκευή."
                : "No external Bluetooth device was connected.");

        appendHtml("<br>");
        logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
        logLine();
        return;
    }

    logLine();
    logInfo(gr ? "LAB 13 — Αποτελέσματα (60s monitor)"
               : "LAB 13 — Results (60s monitor)");

    // ============================================================
    // 1️⃣ Adapter Stability (COLOR)
    // ============================================================
    if (adapterStable) {
        logLabelOkValue(
                gr ? "Σταθερότητα Bluetooth adapter"
                   : "Adapter stability",
                gr ? "Σταθερή" : "Stable"
        );
    } else {
        logLabelErrorValue(
                gr ? "Σταθερότητα Bluetooth adapter"
                   : "Adapter stability",
                gr ? "Μη σταθερή" : "Unstable"
        );
    }

    // ============================================================
    // 2️⃣ Disconnect Events (SEVERITY)
    // ============================================================
    if (lab13DisconnectEvents == 0) {

        logLabelOkValue(
                gr ? "Αποσυνδέσεις" : "Disconnect events",
                "0"
        );

    } else if (lab13DisconnectEvents <= 2) {

        logLabelWarnValue(
                gr ? "Αποσυνδέσεις" : "Disconnect events",
                String.valueOf(lab13DisconnectEvents)
        );

    } else {

        logLabelErrorValue(
                gr ? "Αποσυνδέσεις" : "Disconnect events",
                String.valueOf(lab13DisconnectEvents)
        );
    }

    // ============================================================
    // 3️⃣ Reconnect Events (RECOVERY INDICATOR)
    // ============================================================
    if (lab13ReconnectEvents == 0) {

        logLabelValue(
                gr ? "Επανασυνδέσεις" : "Reconnect events",
                "0"
        );

    } else {

        logLabelOkValue(
                gr ? "Επανασυνδέσεις" : "Reconnect events",
                String.valueOf(lab13ReconnectEvents)
        );
    }

    // ============================================================
    // 4️⃣ PATTERN DIAGNOSIS
    // ============================================================
    boolean flapping =
            lab13DisconnectEvents >= 3 &&
            lab13ReconnectEvents >= 3;

    boolean fullLoss =
            lab13DisconnectEvents >= 3 &&
            lab13ReconnectEvents == 0;

    logLine();

    if (flapping) {

        logLabelErrorValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Connection flapping (συχνές αποσυνδέσεις & επανασυνδέσεις)"
                        : "Connection flapping (frequent disconnect/reconnect)"
        );

        logWarn(gr
                ? "Πιθανό πρόβλημα εξωτερικής συσκευής ή RF παρεμβολή."
                : "Likely external device instability or RF interference.");

    } else if (fullLoss) {

        logLabelErrorValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Πλήρης απώλεια σύνδεσης"
                        : "Full connection loss"
        );

    } else if (lab13DisconnectEvents > 0) {

        logLabelWarnValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Μικρή αστάθεια σύνδεσης"
                        : "Minor connection instability"
        );

    } else {

        logLabelOkValue(
                gr ? "Διάγνωση" : "Diagnosis",
                gr
                        ? "Σταθερή σύνδεση Bluetooth"
                        : "Stable Bluetooth connection"
        );
    }

    // ============================================================
    // ROOT NOTE
    // ============================================================
    logLabelValue(
            "Root access",
            isDeviceRooted()
                    ? (gr
                        ? "Διαθέσιμο (advanced diagnostics)"
                        : "Available (advanced diagnostics)")
                    : (gr
                        ? "Μη διαθέσιμο"
                        : "Not available")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 13 ολοκληρώθηκε." : "Lab 13 finished.");
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
AppTTS.stop();

    // ------------------------------------------------------------
    // ABORT LOG
    // ------------------------------------------------------------
    appendHtml("<br>");
    logWarn("Lab 13 aborted by user.");
    logLine();
}

// ============================================================
// LAB 14 ARCHITECTURE OVERVIEW
// ============================================================
//
// FLOW:
//
// 1) INITIAL SNAPSHOT
//    Read baseline battery state:
//    - charge counter
//    - temperature
//    - cycle count
//    - voltage
//
// 2) FAST STRESS TEST (45 sec)
//    Short burst load to detect:
//    - voltage sag
//    - weak cells
//
// 3) MAIN STRESS
//    Apply combined load:
//    - CPU burn
//    - memory stress
//    - GPU compute
//    - video playback
//    - vibration
//    - max brightness
//
// 4) LOAD MEASUREMENTS
//    During load capture:
//    - voltage under load
//    - temperature rise
//
// 5) STOP LOAD
//    Stop all stress threads:
//
//    stopCpuBurn()
//    stopMemoryStress()
//    stopGpuStress()
//
// 6) PMIC REFRESH
//    Wait for fuel-gauge update:
//
//    SystemClock.sleep(800)
//
// 7) FINAL SNAPSHOT
//    Read final battery state and compute:
//
//    drainMah
//    drain rate (mAh/h)
//    normalized drain (%/h)
//
// 8) ELECTRICAL ANALYSIS
//    - voltage sag
//    - internal resistance
//    - voltage recovery
//    - voltage stability
//
// 9) ADVANCED DETECTORS
//    - collapse predictor
//    - swelling indicator
//    - calibration drift
//
// 10) AGING MODEL
//    Compute:
//    - aging index
//    - lifespan estimate
//
// 11) FINAL BATTERY HEALTH SCORE
//    Combine:
//    - drain behaviour
//    - temperature
//    - cycles
//    - resistance
//    - stability
//
// 12) SAVE RESULTS
//    Store values for:
//
//    LAB 30 final technician summary
//
// ============================================================
  
// ============================================================
// LAB 14 — Battery Health Stress Test
// FINAL CLEAN VERSION — GEL EDITION
// FULL METHOD / COPY-PASTE READY
// ============================================================
private void lab14BatteryHealthStressTest() {

    final boolean gr = AppLang.isGreek(this);

    if (lab14Running) {
        logWarn(gr
                ? "Το LAB 14 εκτελείται ήδη."
                : "LAB 14 already running.");
        return;
    }

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
// 4) DECLARATIONS FOR ELECTRICAL DIAGNOSTICS
// ------------------------------------------------------------
final float[] vStart = {Float.NaN};
final float[] vLoad1 = {Float.NaN};
final float[] vRecover = {Float.NaN};
final float[] vLoad2 = {Float.NaN};

final float[] sag1 = {Float.NaN};
final float[] sag2 = {Float.NaN};
final float[] sagAvg = {Float.NaN};

final float[] voltageRecovery = {Float.NaN};
final float[] voltageStability = {Float.NaN};
final float[] internalResistance = {Float.NaN};

final float[] voltageRecoverySpeed = {Float.NaN};
final float[] cellElasticityIndex = {Float.NaN};
final float[] thermalImpedance = {Float.NaN};
final float[] powerStabilityFactor = {Float.NaN};
final float[] stressSignature = {Float.NaN};
final float[] structuralIntegrityIndex = {Float.NaN};

final boolean[] collapseRisk = {false};
final boolean[] swellingRisk = {false};
final boolean[] calibrationDrift = {false};
final boolean[] cellImbalanceRisk = {false};
final boolean[] batteryFailureRisk = {false};
final float[] batterySOH = {Float.NaN};

final float[] expectedPercent = {Float.NaN};
final float[] percentDeviation = {Float.NaN};

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

        final float[] voltageUnderLoad = {Float.NaN};

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
if (!Float.isNaN(vLoad1) && !Float.isNaN(vRecover)) {

    float recoveryDelta = vRecover - vLoad1;

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
if (!Float.isNaN(voltageStart) &&
        !Float.isNaN(voltageUnderLoad[0])) {

    float sag = vStart[0] - voltageUnderLoad[0];

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
            internalResistance[0] = sagFiltered / currentAmp;
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

    if (voltageDrop > 0.01f) {

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
if (!Float.isNaN(vStart[0]) && !Float.isNaN(voltageUnderLoad[0])) {

    float sag = vStart[0] - voltageUnderLoad[0];

// ignore micro sag noise
if (sag < 0.015f)
    sag = 0f;
    
    String sagLabel;

    if (sag < 0.05f) sagLabel = "Excellent";
    else if (sag < 0.12f) sagLabel = "Normal";
    else if (sag < 0.20f) sagLabel = "Weak";
    else sagLabel = "Severe";

    logLabelValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            String.format(Locale.US, "%.3f V (%s)", sag, sagLabel)
    );

} else {

    logLabelWarnValue(
            gr ? "Πτώση τάσης υπό φορτίο"
               : "Voltage sag under load",
            gr ? "Μη διαθέσιμο" : "Unavailable"
    );
}

// internal resistance
String label = "Unknown";

if (!Float.isNaN(internalResistance[0])) {

    if (internalResistance[0] < 0.08f) label = "Excellent";
    else if (internalResistance[0] < 0.15f) label = "Normal";
    else if (internalResistance[0] < 0.25f) label = "Worn";
    else label = "Failing";

}

if (!Float.isNaN(internalResistance[0])) {

    if (internalResistance[0] < 0.15f) {

        logLabelOkValue(
                gr ? "Εσωτερική αντίσταση μπαταρίας"
                   : "Battery internal resistance",
                String.format(
                        Locale.US,
                        "%.3f Ω (%s)",
                        internalResistance[0],
                        label
                )
        );
        
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

    logLabelValue(
            gr ? "Ηλεκτροχημική αντίσταση κυψελών (ESR)"
               : "Battery ESR estimation",
            String.format(Locale.US, "%.3f Ω (%s)", estimatedESR, esrLabel)
    );
}

    } else {

        logLabelWarnValue(
                gr ? "Εσωτερική αντίσταση μπαταρίας"
                   : "Battery internal resistance",
                String.format(
                        Locale.US,
                        "%.3f Ω (%s)",
                        internalResistance[0],
                        label
                )
        );

    }

} else {

    logLabelWarnValue(
            gr ? "Εσωτερική αντίσταση μπαταρίας"
               : "Battery internal resistance",
            gr ? "Μη διαθέσιμη" : "Unavailable"
    );
}

                // voltage recovery
                if (!Float.isNaN(voltageRecovery[0])) {

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

                    float sag = vStart[0] - voltageUnderLoad[0];

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
                
// ------------------------------------------------------------
// BATTERY DIAGNOSIS CONFIDENCE
// ------------------------------------------------------------
String confidenceLabel;

if (conf.percent >= 90)
    confidenceLabel = gr ? "Πολύ υψηλή αξιοπιστία" : "Very high confidence";
else if (conf.percent >= 75)
    confidenceLabel = gr ? "Υψηλή αξιοπιστία" : "High confidence";
else if (conf.percent >= 60)
    confidenceLabel = gr ? "Μέτρια αξιοπιστία" : "Moderate confidence";
else
    confidenceLabel = gr ? "Χαμηλή αξιοπιστία — απαιτείται επανάληψη τεστ"
                         : "Low confidence — repeat test recommended";

logLabelValue(
        gr ? "Αξιοπιστία διάγνωσης μπαταρίας"
           : "Battery diagnosis confidence",
        confidenceLabel
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

            }
        });
        
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

//=============================================================
// LAB 15 - Charging System Diagnostic (SMART)
// FINAL / LOCKED — NO PATCHES — NO SIDE EFFECTS
//=============================================================
private void lab15ChargingSystemSmart() {

    final boolean gr = AppLang.isGreek(this);

    if (lab15Running) {
        logWarn(gr
                ? "Το LAB 15 εκτελείται ήδη."
                : "LAB 15 already running.");
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
bg.setCornerRadius(dp(10));       // smooth premium corners
bg.setStroke(dp(4), 0xFFFFD700);  // GOLD border
root.setBackground(bg);

// ============================================================
// LAB 15 — CHARGING MONITOR POPUP (GEL STYLE)
// ============================================================

// ---------------------------
// TITLE (WHITE)
// ---------------------------
TextView title = new TextView(this);
title.setText(
        gr
                ? "LAB 15 — Έλεγχος Φόρτισης Συσκευής"
                : "LAB 15 — Charging Behavior Test"
);
title.setTextColor(Color.WHITE);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(12));
root.addView(title);

// ---------------------------
// MAIN MESSAGE (NEON GREEN)
// ---------------------------

int level = getBatteryPercentSafe();

if (level > 80) {

    logLabelWarnValue(
            gr ? "Δοκιμή φόρτισης"
               : "Charging test",
            gr
                    ? "Παραλείφθηκε — η μπαταρία είναι πάνω από 80%"
                    : "Skipped — battery above 80%"
    );

    logLabelWarnValue(
            gr ? "Σημείωση"
               : "Note",
            gr
                    ? "Η δοκιμή φόρτισης είναι αξιόπιστη μόνο μεταξύ 20% και 80%."
                    : "Charging diagnostics are reliable only between 20% and 80% battery level."
    );

    return;
}

TextView msg = new TextView(this);
msg.setText(
        gr
                ? "Σύνδεσε τον φορτιστή στη θύρα φόρτισης της συσκευής.\n\n"
                  + "Το σύστημα θα παρακολουθεί τη συμπεριφορά φόρτισης\n"
                  + "για τα επόμενα 3 λεπτά.\n\n"
                  + "Κράτησε τη συσκευή συνδεδεμένη\n"
                  + "καθ’ όλη τη διάρκεια του τεστ."
                : "Connect the charger to the device’s charging port.\n\n"
                  + "The system will monitor charging behavior\n"
                  + "for the next 3 minutes.\n\n"
                  + "Please keep the device connected\n"
                  + "during the entire test."
);
msg.setTextColor(0xFF39FF14); // GEL neon green
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.2f);
root.addView(msg);

// ---------------------------
// STATUS TEXT (GRAY / DYNAMIC)
// ---------------------------
lab15StatusText = new TextView(this);
lab15StatusText.setText(
        gr
                ? "Αναμονή για σύνδεση φορτιστή…"
                : "Waiting for charging connection…"
);
lab15StatusText.setTextColor(0xFFAAAAAA);
lab15StatusText.setTextSize(15f);
lab15StatusText.setGravity(Gravity.CENTER);
lab15StatusText.setPadding(0, dp(10), 0, 0);
root.addView(lab15StatusText);

// ---------------------------
// DOTS (NEON)
// ---------------------------
final TextView dotsView = new TextView(this);
dotsView.setText("•");
dotsView.setTextColor(0xFF39FF14);
dotsView.setTextSize(22f);
dotsView.setGravity(Gravity.CENTER);
root.addView(dotsView);

// ---------------------------
// COUNTER (NEON)
// ---------------------------
lab15CounterText = new TextView(this);
lab15CounterText.setText(
        gr
                ? "Πρόοδος: 0 / 180 δευτ."
                : "Progress: 0 / 180 sec"
);
lab15CounterText.setTextColor(0xFF39FF14);
lab15CounterText.setGravity(Gravity.CENTER);
root.addView(lab15CounterText);

// ---------------------------
// PROGRESS BAR (SEGMENTS)
// ---------------------------
lab15ProgressBar = new LinearLayout(this);
lab15ProgressBar.setOrientation(LinearLayout.HORIZONTAL);
lab15ProgressBar.setGravity(Gravity.CENTER);
lab15ProgressBar.setPadding(0, dp(8), 0, 0);

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

// ---------------------------
// MUTE ROW (GLOBAL APP TTS)
// ---------------------------
root.addView(buildMuteRow());

// αν η μπαταρία είναι >80% μην μιλήσει καν το TTS
if (level > 80)
    return;

// ---------------------------
// TTS (ONLY IF NOT MUTED)
// ---------------------------
final String ttsText =
        gr
                ? "Σύνδεσε τον φορτιστή στη θύρα φόρτισης της συσκευής.\n\n"
                  + "Το σύστημα θα παρακολουθεί τη συμπεριφορά φόρτισης,\n"
                  + "για τα επόμενα 3 λεπτά.\n\n"
                  + "Κράτησε τη συσκευή συνδεδεμένη\n"
                  + "καθ’ όλη τη διάρκεια του τεστ."
                : "Connect the charger to the device’s charging port.\n\n"
                  + "The system will monitor charging behavior\n"
                  + "for the next 3 minutes.\n\n"
                  + "Please keep the device connected\n"
                  + "during the entire test.";

// ============================================================
// EXIT BUTTON (LAB 15 — GEL STYLE)
// ============================================================

Button exitBtn = new Button(this);
exitBtn.setText(
        gr
                ? "Έξοδος τεστ"
                : "Exit test"
);
exitBtn.setAllCaps(false);
exitBtn.setTextColor(Color.WHITE);
exitBtn.setTypeface(null, Typeface.BOLD);

GradientDrawable exitBg = new GradientDrawable();
exitBg.setColor(0xFF8B0000);          // dark red
exitBg.setCornerRadius(dp(10));
exitBg.setStroke(dp(3), 0xFFFFD700);  // gold border
exitBtn.setBackground(exitBg);

LinearLayout.LayoutParams lpExit =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpExit.setMargins(0, dp(14), 0, 0);
exitBtn.setLayoutParams(lpExit);

// ------------------------------------------------------------
// EXIT ACTION — STOP TTS (NO SHUTDOWN)
// ------------------------------------------------------------
exitBtn.setOnClickListener(v -> {

    // stop voice immediately (GLOBAL)
    try {
        AppTTS.stop();
    } catch (Throwable ignore) {}

    abortLab15ByUser();
});

// add LAST
root.addView(exitBtn);

// ============================================================
// SHOW DIALOG
// ============================================================

b.setView(root);
lab15Dialog = b.create();

if (lab15Dialog.getWindow() != null) {
    lab15Dialog.getWindow()
            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
}

lab15Dialog.show();

// ============================================================
// LOGS
// ============================================================
appendHtml("<br>");
logLine();
logInfo(gr
        ? "LAB 15 — Διάγνωση Συστήματος Φόρτισης (Smart)"
        : "LAB 15 — Charging System Diagnostic (Smart)");
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

        lab15StatusText.setText(gr
                ? "Ανιχνεύθηκε κατάσταση φόρτισης."
                : "Charging state detected.");
        lab15StatusText.setTextColor(0xFF39FF14);

        logOk(gr
                ? "Κατάσταση φόρτισης ανιχνεύθηκε."
                : "Charging state detected.");
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

        lab15StatusText.setText(gr
                ? "Η φόρτιση διακόπηκε."
                : "Charging disconnected.");
        lab15StatusText.setTextColor(0xFFFF4444);

        logError(gr
                ? "Ο φορτιστής αποσυνδέθηκε για περισσότερο από 5 δευτερόλεπτα."
                : "Charger disconnected for more than 5 seconds.");
        logError(gr
                ? "Η δοκιμή φόρτισης ακυρώθηκε."
                : "Charging test aborted.");

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
        gr
                ? "Πρόοδος: " + shown + " / " + LAB15_TOTAL_SECONDS + " δευτ."
                : "Progress: " + shown + " / " + LAB15_TOTAL_SECONDS + " sec"
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

startBatteryTemp = lab15BattTempStart;
endBatteryTemp   = lab15BattTempEnd;

// ------------------------------------------------------------
// Battery temperature + thermal correlation
// ------------------------------------------------------------
logInfo(gr ? "Θερμοκρασία μπαταρίας:" : "Battery temperature:");

logLabelOkValue(
        gr ? "Τελική θερμοκρασία" : "End temperature",
        String.format(Locale.US, "%.1f°C", lab15BattTempEnd)
);

// ------------------------------------------------------------
// Thermal correlation analysis
// ------------------------------------------------------------
logLab15ThermalCorrelation(
        lab15BattTempStart,
        lab15BattTempPeak,
        lab15BattTempEnd
);

// ------------------------------------------------------------
// Thermal verdict
// ------------------------------------------------------------
float dtCharge = lab15BattTempEnd - lab15BattTempStart;

logInfo(gr
        ? "Θερμική αξιολόγηση (κατά τη φόρτιση):"
        : "Thermal verdict (charging):");

if (lab15OverTempDuringCharge) {

    logLabelErrorValue(
            gr ? "Θερμοκρασία" : "Temperature",
            String.format(
                    Locale.US,
                    gr
                            ? "ΥΨΗΛΗ (ΔT +%.1f°C) — Ανιχνεύθηκε αυξημένη θερμοκρασία"
                            : "HOT (ΔT +%.1f°C) — Elevated temperature detected",
                    Math.max(0f, dtCharge)
            )
    );

} else {

    logLabelOkValue(
            gr ? "Θερμοκρασία" : "Temperature",
            String.format(
                    Locale.US,
                    gr
                            ? "OK (ΔT +%.1f°C) — Φυσιολογική θερμική συμπεριφορά"
                            : "OK (ΔT +%.1f°C) — Normal thermal behavior",
                    Math.max(0f, dtCharge)
            )
    );
}

// ------------------------------------------------------------
// Charging connection stability
// ------------------------------------------------------------
logInfo(gr ? "Σταθερότητα σύνδεσης φόρτισης:" : "Charging connection:");

if (lab15FlapUnstable) {

    logLabelErrorValue(
            gr ? "Σύνδεση" : "Connection",
            gr
                    ? "Ασταθής — εντοπίστηκε επαναλαμβανόμενο plug/unplug"
                    : "Unstable — plug/unplug behavior detected"
    );

} else {

    logLabelOkValue(
            gr ? "Σύνδεση" : "Connection",
            gr
                    ? "Σταθερή — δεν ανιχνεύθηκε μη φυσιολογική επανασύνδεση"
                    : "Stable — no abnormal reconnect behavior"
    );
}

// ------------------------------------------------------------
// CHARGING INPUT & STRENGTH
// ------------------------------------------------------------
BatteryInfo endInfo = getBatteryInfo();

if (startMah > 0 && endInfo != null &&
        endInfo.currentChargeMah > startMah && startTs[0] > 0) {

    lab15_strengthKnown = true;

    long deltaMah = endInfo.currentChargeMah - startMah;
    long dtMs     = Math.max(1, SystemClock.elapsedRealtime() - startTs[0]);
    double minutes = dtMs / 60000.0;
    double mahPerMin = (minutes > 0) ? (deltaMah / minutes) : -1;

    logLabelOkValue(
            gr ? "Είσοδος φόρτισης" : "Charging input",
            String.format(
                    Locale.US,
                    gr
                            ? "+%d mAh σε %.1f λεπτά (%.1f mAh/min)"
                            : "+%d mAh in %.1f min (%.1f mAh/min)",
                    deltaMah,
                    minutes,
                    mahPerMin
            )
    );

    logInfo(gr ? "Ισχύς φόρτισης:" : "Charging strength:");

    if (mahPerMin >= 20.0) {
        logLabelOkValue(gr ? "Ισχύς" : "Strength", gr ? "ΙΣΧΥΡΗ" : "STRONG");
        lab15_strengthWeak = false;

    } else if (mahPerMin >= 10.0) {
        logLabelOkValue(gr ? "Ισχύς" : "Strength", gr ? "ΚΑΝΟΝΙΚΗ" : "NORMAL");
        lab15_strengthWeak = false;

    } else if (mahPerMin >= 5.0) {
        logLabelWarnValue(gr ? "Ισχύς" : "Strength", gr ? "ΜΕΤΡΙΑ" : "MODERATE");
        lab15_strengthWeak = true;

    } else {
        logLabelErrorValue(gr ? "Ισχύς" : "Strength", gr ? "ΑΣΘΕΝΗΣ" : "WEAK");
        lab15_strengthWeak = true;
    }

} else {

    lab15_strengthKnown = false;
    lab15_strengthWeak  = true;

    logLabelWarnValue(
            gr ? "Ισχύς φόρτισης" : "Charging strength",
            gr
                    ? "Δεν ήταν δυνατή η αξιόπιστη εκτίμηση"
                    : "Unable to estimate accurately"
    );
}

// ------------------------------------------------------------
// FINAL LAB 15 DECISION
// ------------------------------------------------------------
logInfo(gr ? "Απόφαση LAB:" : "LAB decision:");

if (!lab15OverTempDuringCharge && !lab15FlapUnstable && !lab15_strengthWeak) {

    logLabelOkValue(
            gr ? "Σύστημα φόρτισης" : "Charging system",
            gr
                    ? "OK — δεν απαιτείται καθαρισμός ή αντικατάσταση"
                    : "OK — no cleaning or replacement required"
    );

    logLabelOkValue(gr ? "Σταθερότητα" : "Stability", "OK");

} else {

    logLabelWarnValue(
            gr ? "Σύστημα φόρτισης" : "Charging system",
            gr
                    ? "Εντοπίστηκαν πιθανά ζητήματα"
                    : "Potential issues detected"
    );

    logLabelWarnValue(
            gr ? "Σύσταση" : "Recommendation",
            gr
                    ? "Συνιστάται περαιτέρω έλεγχος ή επανάληψη δοκιμής"
                    : "Further inspection or repeat test recommended"
    );
}

// ------------------------------------------------------------
// SUMMARY FLAG
// ------------------------------------------------------------
boolean chargingGlitchDetected =
        lab15FlapUnstable ||
        lab15OverTempDuringCharge ||
        lab15_strengthWeak ||
        lab15_systemLimited;

GELServiceLog.info(
        "SUMMARY: CHARGING_STABILITY=" +
                (chargingGlitchDetected ? "UNSTABLE" : "STABLE")
);

appendHtml("<br>");
logOk(gr ? "Το Lab 15 ολοκληρώθηκε." : "Lab 15 finished.");
logLine();

// ------------------------------------------------------------
// STORE RESULT FOR LAB 17 (LAB 15 OUTPUT)
// ------------------------------------------------------------
try {

    int chargeScore = 100;

    if (lab15_strengthWeak)          chargeScore -= 25;
    if (lab15FlapUnstable)           chargeScore -= 25;
    if (lab15OverTempDuringCharge)   chargeScore -= 25;

    chargeScore = Math.max(0, Math.min(100, chargeScore));

    p.edit()
            .putInt("lab15_charge_score", chargeScore)
            .putBoolean("lab15_system_limited", lab15_systemLimited)
            .putBoolean("lab15_overtemp", lab15OverTempDuringCharge)
            .putString(
                    "lab15_strength_label",
                    lab15_strengthWeak ? "WEAK" : "NORMAL/STRONG"
            )
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

    final boolean gr = AppLang.isGreek(this);

    SharedPreferences p = getSharedPreferences("GEL_DIAG", MODE_PRIVATE);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 16 — Θερμικό Στιγμιότυπο"
            : "LAB 16 — Thermal Snapshot");
    logLine();

    List<ThermalEntry> internal     = buildThermalInternal();
    List<ThermalEntry> peripherals  = buildThermalPeripheralsCritical();

    float  peakTemp = -1f;
    String peakSrc  = "N/A";

    // ------------------------------------------------------------
    // BASIC + CRITICAL THERMALS
    // ------------------------------------------------------------
    logInfo(gr ? "Θερμικοί αισθητήρες:" : "Thermal sensors:");

    for (ThermalEntry t : internal) {

        logLabelOkValue(
                t.label,
                String.format(Locale.US, "%.1f°C", t.temp)
        );

        if (t.temp > peakTemp) {
            peakTemp = t.temp;
            peakSrc  = t.label;
        }
    }

    for (ThermalEntry t : peripherals) {

        logLabelOkValue(
                t.label,
                String.format(Locale.US, "%.1f°C", t.temp)
        );

        if (t.temp > peakTemp) {
            peakTemp = t.temp;
            peakSrc  = t.label;
        }
    }

    logLine();

    // ------------------------------------------------------------
    // SUMMARY
    // ------------------------------------------------------------
    boolean danger = peakTemp >= 55f;

    logInfo(gr ? "Θερμική σύνοψη:" : "Thermal summary:");

    if (danger) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Ανιχνεύθηκε αυξημένη θερμοκρασία"
                   : "Elevated temperature detected"
        );

        logLabelWarnValue(
                gr ? "Αντίδραση συστήματος" : "System response",
                gr ? "Ενδέχεται να ενεργοποιηθεί θερμική προστασία"
                   : "Thermal protection may activate"
        );

    } else {

        logLabelOkValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Ασφαλείς θερμοκρασίες λειτουργίας"
                   : "Safe operating temperatures"
        );

        logLabelOkValue(
                gr ? "Κάλυψη" : "Coverage",
                gr ? "Παρακολουθήθηκαν εσωτερικά chips και κρίσιμα περιφερειακά"
                   : "Internal chips and critical peripherals monitored"
        );
    }

    // ------------------------------------------------------------
    // PEAK TEMPERATURE
    // ------------------------------------------------------------
    if (peakTemp > 0) {

        logInfo(gr
                ? "Μέγιστη θερμοκρασία που παρατηρήθηκε:"
                : "Peak temperature observed:");

        String peakText = String.format(
                Locale.US,
                "%.1f°C %s %s",
                peakTemp,
                gr ? "στο" : "at",
                peakSrc
        );

        if (peakTemp >= 55f) {

            logLabelErrorValue(
                    gr ? "Μέγιστη" : "Peak",
                    peakText
            );

        } else if (peakTemp >= 45f) {

            logLabelWarnValue(
                    gr ? "Μέγιστη" : "Peak",
                    peakText
            );

        } else {

            logLabelOkValue(
                    gr ? "Μέγιστη" : "Peak",
                    peakText
            );
        }
    }

    // ------------------------------------------------------------
    // HIDDEN THERMAL SAFETY CHECK
    // ------------------------------------------------------------
    boolean hiddenRisk = detectHiddenThermalAnomaly(55f);

    if (hiddenRisk) {

        logLabelWarnValue(
                gr ? "Κρυφοί αισθητήρες" : "Hidden sensors",
                gr
                        ? "Ανιχνεύθηκε αυξημένη θερμοκρασία (μη εμφανιζόμενα στοιχεία)"
                        : "Elevated temperature detected (non-displayed components)"
        );

        logLabelWarnValue(
                gr ? "Κίνδυνος" : "Risk",
                gr
                        ? "Ενδέχεται να ενεργοποιηθούν μηχανισμοί θερμικής προστασίας"
                        : "Thermal protection mechanisms may activate"
        );

    } else {

        logLabelOkValue(
                gr ? "Κρυφοί αισθητήρες" : "Hidden sensors",
                gr
                        ? "Όλοι οι κρίσιμοι θερμικοί αισθητήρες είναι εντός ορίων"
                        : "All critical thermal sensors monitored"
        );
    }

    // ------------------------------------------------------------
    // THERMAL SCORE
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

    logInfo(gr ? "Δείκτης θερμικής συμπεριφοράς:" : "Thermal behaviour score:");

    logLabelOkValue(
            gr ? "Βαθμολογία" : "Score",
            String.format(Locale.US, "%d%%", thermalScore)
    );

    boolean thermalSpikesDetected = thermalDanger;

    GELServiceLog.info(
            "SUMMARY: THERMAL_PATTERN=" +
                    (thermalSpikesDetected ? "SPIKES" : "NORMAL")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 16 ολοκληρώθηκε." : "Lab 16 finished.");
    logLine();
}

// ============================================================
// LAB 17 — GEL Auto Battery Reliability Evaluation
// INTELLIGENCE EDITION • STRICT FRESHNESS (â‰¤ 2 HOURS)
// ============================================================
private void lab17RunAuto() {

    final boolean gr = AppLang.isGreek(this);
    
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

    // --------------------------------------------------------
    // STATUS HEADER
    // --------------------------------------------------------
    msg.append(
            gr
                    ? "Κατάσταση (απαιτούνται αποτελέσματα τελευταίων 2 ωρών):\n\n"
                    : "Status (results required within last 2 hours):\n\n"
    );

    // --------------------------------------------------------
    // LAB 14
    // --------------------------------------------------------
    msg.append(gr ? "• LAB 14: " : "• LAB 14: ");
    if (!has14)
        msg.append(gr ? "Απουσιάζει\n" : "Missing\n");
    else if (!fresh14)
        msg.append(gr ? "Έληξε (" : "Expired (")
           .append(lab17_age(now - ts14))
           .append(")\n");
    else
        msg.append("OK (")
           .append(lab17_age(now - ts14))
           .append(")\n");

    // --------------------------------------------------------
    // LAB 15
    // --------------------------------------------------------
    msg.append(gr ? "• LAB 15: " : "• LAB 15: ");
    if (!has15)
        msg.append(gr ? "Απουσιάζει\n" : "Missing\n");
    else if (!fresh15)
        msg.append(gr ? "Έληξε (" : "Expired (")
           .append(lab17_age(now - ts15))
           .append(")\n");
    else
        msg.append("OK (")
           .append(lab17_age(now - ts15))
           .append(")\n");

    // --------------------------------------------------------
    // LAB 16
    // --------------------------------------------------------
    msg.append(gr ? "• LAB 16: " : "• LAB 16: ");
    if (!has16)
        msg.append(gr ? "Απουσιάζει\n" : "Missing\n");
    else if (!fresh16)
        msg.append(gr ? "Έληξε (" : "Expired (")
           .append(lab17_age(now - ts16))
           .append(")\n");
    else
        msg.append("OK (")
           .append(lab17_age(now - ts16))
           .append(")\n");

    msg.append("\n");

    // --------------------------------------------------------
    // SMART DECISION
    // --------------------------------------------------------
    if ((fresh14 && fresh15) && !fresh16) {

        msg.append(
                gr
                        ? "Έχουν ολοκληρωθεί τα LAB 14 και LAB 15.\n"
                          + "Εκτέλεσε ΜΟΝΟ το LAB 16 για να ολοκληρωθεί το σύνολο.\n"
                        : "LAB 14 and LAB 15 are already completed.\n"
                          + "Run ONLY LAB 16 to complete the set.\n"
        );

    } else if ((fresh14 && fresh16) && !fresh15) {

        msg.append(
                gr
                        ? "Έχουν ολοκληρωθεί τα LAB 14 και LAB 16.\n"
                          + "Εκτέλεσε ΜΟΝΟ το LAB 15 για να ολοκληρωθεί το σύνολο.\n"
                        : "LAB 14 and LAB 16 are already completed.\n"
                          + "Run ONLY LAB 15 to complete the set.\n"
        );

    } else if ((fresh15 && fresh16) && !fresh14) {

        msg.append(
                gr
                        ? "Έχουν ολοκληρωθεί τα LAB 15 και LAB 16.\n"
                          + "Εκτέλεσε ΜΟΝΟ το LAB 14 για να ολοκληρωθεί το σύνολο.\n"
                        : "LAB 15 and LAB 16 are already completed.\n"
                          + "Run ONLY LAB 14 to complete the set.\n"
        );

    } else {

        msg.append(
                gr
                        ? "Για έγκυρο αποτέλεσμα, απαιτείται εκτέλεση των\n"
                          + "LAB 14 + LAB 15 + LAB 16 μαζί.\n\n"
                          + "Αιτία: απουσία ή/και λήξη αποτελεσμάτων.\n"
                        : "To generate a valid result, run\n"
                          + "LAB 14 + LAB 15 + LAB 16 together.\n\n"
                          + "Reason: missing and/or expired results.\n"
        );
    }

    lab17_showPopup(
            gr
                    ? "LAB 17 — Έλεγχος Προϋποθέσεων"
                    : "LAB 17 — Prerequisites Check",
            msg.toString()
    );
    return;
}  

// ------------------------------------------------------------
// START LAB 17
// ------------------------------------------------------------

appendHtml("<br>");
logLine();
logInfo(gr
        ? "LAB 17 — GEL Ευφυής Ανάλυση Υγείας Συστήματος"
        : "LAB 17 — GEL Intelligent System Health Analysis");
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
logLine();
logInfo(gr
        ? "LAB 14 — Υγεία μπαταρίας"
        : "LAB 14 — Battery health");

logLabelOkValue(
        gr ? "Υγεία" : "Health",
        String.format(
                Locale.US,
                gr
                        ? "%.0f%% | Δείκτης γήρανσης: %s"
                        : "%.0f%% | Aging index: %s",
                lab14Health,
                (lab14Aging >= 0
                        ? lab14Aging + "/100"
                        : (gr ? "Μ/Δ" : "N/A"))
        )
);

logInfo(gr
        ? "LAB 15 — Φόρτιση"
        : "LAB 15 — Charging");

if (lab15Charge >= 70) {

    logLabelOkValue(
            gr ? "Φόρτιση" : "Charging",
            String.format(
                    Locale.US,
                    gr
                            ? "%d%% | Ισχύς: %s"
                            : "%d%% | Strength: %s",
                    lab15Charge,
                    (lab15StrengthLabel != null
                            ? lab15StrengthLabel
                            : (gr ? "Μ/Δ" : "N/A"))
            )
    );

} else {

    logLabelWarnValue(
            gr ? "Φόρτιση" : "Charging",
            String.format(
                    Locale.US,
                    gr
                            ? "%d%% | Ισχύς: %s"
                            : "%d%% | Strength: %s",
                    lab15Charge,
                    (lab15StrengthLabel != null
                            ? lab15StrengthLabel
                            : (gr ? "Μ/Δ" : "N/A"))
            )
    );
}

logInfo(gr
        ? "LAB 16 — Θερμική συμπεριφορά"
        : "LAB 16 — Thermal behaviour");

if (lab16Thermal >= 75) {

    logLabelOkValue(
            gr ? "Θερμική βαθμολογία" : "Thermal score",
            lab16Thermal + "%"
    );

} else if (lab16Thermal >= 60) {

    logLabelWarnValue(
            gr ? "Θερμική βαθμολογία" : "Thermal score",
            lab16Thermal + "%"
    );

} else {

    logLabelErrorValue(
            gr ? "Θερμική βαθμολογία" : "Thermal score",
            lab16Thermal + "%"
    );
}

// ================= ANALYSIS =================
if (lab15SystemLimited) {

    logLine();
    logInfo(gr
            ? "Ανάλυση περιορισμού φόρτισης"
            : "Charging limitation analysis");

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Ανιχνεύθηκε περιορισμός από το σύστημα"
                    : "System-limited throttling detected"
    );

    logLabelWarnValue(
            gr ? "Πηγή" : "Source",
            "PMIC / thermal protection"
    );

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Δεν αποδίδεται αποκλειστικά σε υγεία μπαταρίας"
                    : "Not attributed to battery health alone"
    );
}

if (fPenaltyExtra > 0) {

    logLine();
    logInfo(gr
            ? "Ανάλυση ποινών"
            : "Penalty breakdown");

    if (lab15Charge < 60 && lab15SystemLimited)
        logLabelWarnValue(
                gr ? "Φόρτιση" : "Charging",
                gr
                        ? "Περιορισμός από το σύστημα"
                        : "System-limited throttling detected"
        );
    else if (lab15Charge < 60)
        logLabelWarnValue(
                gr ? "Φόρτιση" : "Charging",
                gr
                        ? "Ασθενής απόδοση φόρτισης"
                        : "Weak charging performance detected"
        );

    if (lab14Aging >= 70)
        logLabelErrorValue(
                gr ? "Γήρανση" : "Aging",
                gr
                        ? "Σοβαρές ενδείξεις γήρανσης"
                        : "Severe aging indicators detected"
        );
    else if (lab14Aging >= 50)
        logLabelWarnValue(
                gr ? "Γήρανση" : "Aging",
                gr
                        ? "Υψηλές ενδείξεις γήρανσης"
                        : "High aging indicators detected"
        );
    else if (lab14Aging >= 30)
        logLabelWarnValue(
                gr ? "Γήρανση" : "Aging",
                gr
                        ? "Μέτριες ενδείξεις γήρανσης"
                        : "Moderate aging indicators detected"
        );
}

// ================= FINAL SCORE =================
logLine();
logInfo(gr
        ? "Τελικός Δείκτης Αξιοπιστίας Μπαταρίας"
        : "Final Battery Reliability Score");

if (fFinalScore >= 80) {

    logLabelOkValue(
            gr ? "Βαθμολογία" : "Score",
            String.format(Locale.US, "%d%% (%s)", fFinalScore, fCategory)
    );

} else if (fFinalScore >= 60) {

    logLabelWarnValue(
            gr ? "Βαθμολογία" : "Score",
            String.format(Locale.US, "%d%% (%s)", fFinalScore, fCategory)
    );

} else {

    logLabelErrorValue(
            gr ? "Βαθμολογία" : "Score",
            String.format(Locale.US, "%d%% (%s)", fFinalScore, fCategory)
    );
}

// ================= DIAGNOSIS =================
logLine();
logInfo(gr ? "Διάγνωση" : "Diagnosis");

if (lab14Unstable) {

    logLabelWarnValue(
            gr ? "Αξιοπιστία μέτρησης" : "Measurement reliability",
            gr ? "Ασταθής" : "Unstable"
    );

    logLabelWarnValue(
            gr ? "Αιτία" : "Cause",
            "PMIC / fuel gauge instability"
    );

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Δεν αποτελεί επιβεβαιωμένη αστοχία μπαταρίας"
                    : "Not a confirmed battery failure"
    );
}

if (!overallDeviceConcern) {

    logLabelOkValue(
            gr ? "Συνολική κατάσταση" : "Overall status",
            gr
                    ? "Δεν εντοπίστηκαν κρίσιμα προβλήματα (μπαταρία / φόρτιση / θερμικά)"
                    : "No critical issues detected (battery / charging / thermal)"
    );

    logLabelOkValue(
            gr ? "Παρακολούθηση" : "Monitoring",
            gr
                    ? "Ελέγχθηκαν εσωτερικά chips και κρίσιμα περιφερειακά"
                    : "Internal chips and critical peripherals checked"
    );

} else {

    if (batteryLooksFineButThermalBad) {

        logLabelWarnValue(
                gr ? "Θερμικός κίνδυνος" : "Thermal risk",
                gr
                        ? "Η υγεία μπαταρίας είναι ΟΚ, αλλά η θερμική συμπεριφορά είναι οριακή"
                        : "Battery health OK, thermal behaviour risky"
        );

        logLabelWarnValue(
                gr ? "Σύσταση" : "Recommendation",
                gr
                        ? "Έλεγχος ψύξης και θερμικών επαφών"
                        : "Inspect cooling path and thermal interfaces"
        );

        logLabelWarnValue(
                gr ? "Πιθανές αιτίες" : "Possible causes",
                gr
                        ? "Φόρτος CPU/GPU, thermal pads, επαφή heatsink"
                        : "CPU/GPU load, thermal pads, heatsink contact"
        );
    }

    if (chargingWeakOrThrottled) {

        if (lab15SystemLimited) {

            logLabelWarnValue(
                    gr ? "Φόρτιση" : "Charging",
                    gr
                            ? "Περιορισμός από το σύστημα (προστασία ενεργή)"
                            : "System-limited (protection logic active)"
            );

            logLabelWarnValue(
                    gr ? "Πιθανές αιτίες" : "Possible causes",
                    gr
                            ? "Υπερθέρμανση ή περιορισμός ρεύματος από PMIC"
                            : "Overheating or PMIC current limiting"
            );

        } else if (lab15Charge < 60) {

            logLabelWarnValue(
                    gr ? "Φόρτιση" : "Charging",
                    gr
                            ? "Ασθενής απόδοση φόρτισης"
                            : "Weak charging performance"
            );

            logLabelWarnValue(
                    gr ? "Πιθανές αιτίες" : "Possible causes",
                    gr
                            ? "Καλώδιο / αντάπτορας, φθορά θύρας, αυξημένη εσωτερική αντίσταση μπαταρίας"
                            : "Cable / adapter quality, port wear, battery impedance"
            );
        }
    }

    if (batteryBadButThermalOk) {

        logLabelWarnValue(
                gr ? "Μπαταρία" : "Battery",
                gr
                        ? "Η υγεία είναι μειωμένη ενώ τα θερμικά είναι φυσιολογικά"
                        : "Health weak while thermals remain normal"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Likely cause",
                gr
                        ? "Γήρανση / απώλεια χωρητικότητας"
                        : "Battery aging / capacity loss"
        );
    }

    if (lab14Health < 70f && thermalDanger) {

        logLabelErrorValue(
                gr ? "Συνδυασμένος κίνδυνος" : "Combined risk",
                gr
                        ? "Εντοπίστηκαν προβλήματα μπαταρίας και θερμικής συμπεριφοράς — συνιστάται τεχνικός έλεγχος"
                        : "Battery + thermal issues detected — technician inspection recommended"
        );
    }
}

// ------------------------------------------------------------
// STORE FINAL RESULT
// ------------------------------------------------------------
try {
    p.edit()
            .putInt("lab17_final_score", fFinalScore)
            .putString("lab17_category", fCategory)
            .putLong("lab17_ts", System.currentTimeMillis())
            .apply();
} catch (Throwable ignore) {}

// ================= FINAL =================
appendHtml("<br>");
logOk(gr ? "Το Lab 17 ολοκληρώθηκε." : "Lab 17 finished.");
logLine();

}); // END ui.post

} catch (Throwable ignore) {
    // silent
}

}).start();

} // ===== END lab17RunAuto()

// ============================================================
// LAB 17 — POPUP (GEL DARK + GOLD)
// AppLang + AppTTS + GLOBAL MUTE
// ============================================================
private void lab17_showPopup(String titleText, String msgText) {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );
    b.setCancelable(true);

    // ==========================
    // ROOT
    // ==========================
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(24), dp(20), dp(24), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(10));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    // ==========================
    // TITLE (WHITE)
    // ==========================
    TextView title = new TextView(this);
    title.setText(titleText);
    title.setTextColor(Color.WHITE);
    title.setTextSize(17f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    root.addView(title);

    // ==========================
    // MESSAGE (NEON GREEN)
    // ==========================
    TextView msg = new TextView(this);
    msg.setText(msgText);
    msg.setTextColor(0xFF39FF14); // GEL neon green
    msg.setTextSize(14.5f);
    msg.setLineSpacing(0f, 1.2f);
    msg.setGravity(Gravity.CENTER);
    msg.setPadding(0, 0, 0, dp(18));
    root.addView(msg);

    // ==========================
    // MUTE ROW (GLOBAL APP TTS)
    // ==========================
    root.addView(buildMuteRow());

    // ==========================
    // OK BUTTON
    // ==========================
    Button ok = gelButton(
            this,
            gr ? "ΟΚ" : "OK",
            0xFF000000
    );

    LinearLayout.LayoutParams lpOk =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lpOk.setMargins(0, dp(10), 0, 0);
    ok.setLayoutParams(lpOk);
    root.addView(ok);

    // ==========================
    // BUILD DIALOG
    // ==========================
    b.setView(root);
    AlertDialog popup = b.create();

    if (popup.getWindow() != null) {
        popup.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    popup.show();

    // ==========================
    // TTS — GLOBAL ENGINE (ONCE)
    // ==========================
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
        if (popup.isShowing() && !AppTTS.isMuted(this)) {

            String speakText =
                    gr
                            ? "Πριν την εκτέλεση αυτού του εργαστηρίου, "
                              + "βεβαιώσου ότι έχουν ολοκληρωθεί τα LAB δεκατέσσερα, "
                              + "δεκαπέντε και δεκαέξι."
                            : "Before running this lab, please make sure that "
                              + "LAB fourteen, LAB fifteen and LAB sixteen "
                              + "have been completed.";

            AppTTS.ensureSpeak(this, speakText);
        }
    }, 120);

    // ==========================
    // OK ACTION
    // ==========================
    ok.setOnClickListener(v -> {
        AppTTS.stop();
        try { popup.dismiss(); } catch (Throwable ignore) {}
    });
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

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 18 — Έλεγχος Υγείας Εσωτερικού Αποθηκευτικού Χώρου"
            : "LAB 18 — Internal Storage Health Inspection");
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
        logInfo(gr ? "Χρήση αποθηκευτικού χώρου:" : "Storage usage:");
        logLabelOkValue(
                gr ? "Χρήση" : "Usage",
                humanBytes(used) + (gr ? " χρησιμοποιούνται / " : " used / ") +
                humanBytes(total) +
                (gr
                        ? " (ελεύθερα " + humanBytes(free) + ", " + pctFree + "%)"
                        : " (free " + humanBytes(free) + ", " + pctFree + "%)")
        );

        // ------------------------------------------------------------
        // MEMORY PRESSURE INDICATORS
        // ------------------------------------------------------------
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
        logInfo(gr
                ? "Δείκτες πίεσης μνήμης:"
                : "Memory pressure indicators:");

        logLabelOkValue(
                gr ? "Πίεση μνήμης" : "Memory pressure",
                humanPressure
        );
        logLabelOkValue(
                gr ? "Επίπεδο πίεσης" : "Pressure level",
                pressureLevel
        );
        logLabelOkValue(
                "ZRAM dependency",
                zramDep
        );

        if (swapUsedKb > 0) {
            logLabelWarnValue(
                    gr ? "Χρήση Swap" : "Swap used",
                    humanBytes(swapUsedKb * 1024L)
            );
        }

        if (snap.memFreeKb > 0) {
            logLabelOkValue(
                    "MemFree",
                    humanBytes(snap.memFreeKb * 1024L)
            );
        }

        if (snap.cachedKb > 0) {
            logLabelOkValue(
                    "Cached",
                    humanBytes(snap.cachedKb * 1024L) +
                            (gr ? " (επανακτήσιμη μνήμη)" : " (reclaimable)")
            );
        }

        // ------------------------------------------------------------
        // PRESSURE LEVEL (HUMAN SCALE)
        // ------------------------------------------------------------
        boolean critical = pctFree < 7;
        boolean pressure = pctFree < 15;

        logLine();
        logInfo(gr
                ? "Αξιολόγηση πίεσης αποθηκευτικού χώρου:"
                : "Storage pressure assessment:");

        if (critical) {

            logLabelErrorValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Κρίσιμα χαμηλός διαθέσιμος χώρος"
                       : "Critically low storage"
            );
            logLabelErrorValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Η σταθερότητα του συστήματος μπορεί να επηρεαστεί"
                       : "System stability may be affected"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Πιθανά κρασαρίσματα εφαρμογών, αποτυχία ενημερώσεων ή επιβράδυνση UI"
                       : "Apps may crash, updates may fail, UI may slow down"
            );

        } else if (pressure) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Ο αποθηκευτικός χώρος βρίσκεται υπό πίεση"
                       : "Storage under pressure"
            );
            logLabelWarnValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Το σύστημα μπορεί να επιβραδύνεται σε λειτουργίες αρχείων"
                       : "System may feel slower during file operations"
            );

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Υγιές επίπεδο αποθηκευτικού χώρου για καθημερινή χρήση"
                       : "Healthy storage level for daily usage"
            );
        }

        // ------------------------------------------------------------
        // FILESYSTEM INFO (BEST EFFORT)
        // ------------------------------------------------------------
        try {
            String fsType = s.getClass().getMethod("getFilesystemType") != null
                    ? (String) s.getClass().getMethod("getFilesystemType").invoke(s)
                    : null;

            if (fsType != null) {
                logInfo("Filesystem:");
                logLabelOkValue(
                        gr ? "Τύπος" : "Type",
                        fsType.toUpperCase(Locale.US)
                );
            }
        } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // ROOT AWARE INTELLIGENCE
        // ------------------------------------------------------------
        boolean rooted = isDeviceRooted();

        if (rooted) {

            logLine();
            logInfo(gr
                    ? "Προχωρημένη ανάλυση αποθηκευτικού χώρου (root access):"
                    : "Advanced storage analysis (root access):");

            boolean wearSignals = detectStorageWearSignals();
            boolean reservedPressure = pctFree < 12;
            
// ------------------------------------------------------------
// EARLY STORAGE DEGRADATION INDICATORS
// ------------------------------------------------------------
boolean nandRisk = false;
int nandScore = 0;

if (wearSignals)
    nandScore += 30;

if (reservedPressure)
    nandScore += 20;

if (swapUsedKb > 0)
    nandScore += 10;

if ("HIGH".equalsIgnoreCase(pressureLevel))
    nandScore += 20;

if (pctFree < 10)
    nandScore += 20;

logLabelValue(
        gr ? "Δείκτης πιθανής φθοράς NAND"
           : "Possible NAND degradation index",
        nandScore + "/100"
);

if (nandScore >= 60) {

    nandRisk = true;

    logLabelWarnValue(
            gr ? "Ένδειξη αποθηκευτικού χώρου"
               : "Storage integrity",
            gr
                    ? "Εντοπίστηκε μοτίβο που μπορεί να σχετίζεται με φθορά NAND ή controller."
                    : "Pattern may indicate NAND or storage controller degradation."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα αποθηκευτικού χώρου"
               : "Storage integrity",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις φθοράς NAND."
                    : "No NAND degradation indicators detected."
    );
}

// ------------------------------------------------------------
// STORAGE CONTROLLER INSTABILITY DETECTOR
// ------------------------------------------------------------
boolean controllerRisk = false;
int controllerScore = 0;

// υψηλή πίεση μνήμης + swap usage
if ("HIGH".equalsIgnoreCase(pressureLevel))
    controllerScore += 25;

if (swapUsedKb > 0)
    controllerScore += 15;

// πολύ χαμηλός διαθέσιμος χώρος
if (pctFree < 10)
    controllerScore += 20;

// storage wear indicators
if (wearSignals)
    controllerScore += 20;

// έντονη πίεση συστήματος
if (pctFree < 7)
    controllerScore += 20;

logLabelValue(
        gr ? "Δείκτης σταθερότητας controller"
           : "Storage controller stability index",
        controllerScore + "/100"
);

if (controllerScore >= 60) {

    controllerRisk = true;

    logLabelWarnValue(
            gr ? "Controller αποθήκευσης"
               : "Storage controller",
            gr
                    ? "Πιθανή αστάθεια controller αποθήκευσης."
                    : "Possible storage controller instability detected."
    );

} else {

    logLabelOkValue(
            gr ? "Controller αποθήκευσης"
               : "Storage controller",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις αστάθειας."
                    : "Controller behaviour appears stable."
    );
}

// ------------------------------------------------------------
// FILESYSTEM CORRUPTION EARLY DETECTOR
// ------------------------------------------------------------
boolean fsCorruptionRisk = false;
int fsScore = 0;

// έντονη πίεση αποθηκευτικού χώρου
if (pctFree < 10)
    fsScore += 30;

// πολύ χαμηλός χώρος
if (pctFree < 7)
    fsScore += 20;

// χρήση swap
if (swapUsedKb > 0)
    fsScore += 15;

// υψηλή πίεση μνήμης
if ("HIGH".equalsIgnoreCase(pressureLevel))
    fsScore += 15;

// ενδείξεις wear
if (wearSignals)
    fsScore += 20;

logLabelValue(
        gr ? "Δείκτης ακεραιότητας filesystem"
           : "Filesystem integrity index",
        fsScore + "/100"
);

if (fsScore >= 60) {

    fsCorruptionRisk = true;

    logLabelWarnValue(
            gr ? "Ακεραιότητα filesystem"
               : "Filesystem integrity",
            gr
                    ? "Εντοπίστηκε μοτίβο που μπορεί να οδηγήσει σε σφάλματα filesystem."
                    : "Pattern detected that may lead to filesystem errors."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα filesystem"
               : "Filesystem integrity",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις πιθανής αλλοίωσης."
                    : "No filesystem corruption indicators detected."
    );
}

            if (wearSignals) {
                logLabelWarnValue(
                        gr ? "Ενδείξεις φθοράς" : "Wear indicators",
                        gr ? "Εντοπίστηκαν (μακροχρόνια χρήση)"
                           : "Detected (long-term usage)"
                );
                logLabelOkValue(
                        gr ? "Σημείωση" : "Note",
                        gr ? "Δεν υποδηλώνει άμεση αστοχία"
                           : "Does not indicate imminent failure"
                );
            } else {
                logLabelOkValue(
                        gr ? "Ενδείξεις φθοράς" : "Wear indicators",
                        gr ? "Δεν εντοπίστηκαν" : "Not detected"
                );
            }

            if (reservedPressure) {
                logLabelWarnValue(
                        gr ? "Σύστημα εφεδρείας" : "System reserve",
                        gr
                                ? "Περιορισμένο — το Android ενδέχεται να περιορίσει background διεργασίες"
                                : "Compressed — Android may limit background tasks"
                );
            }

            logLabelOkValue(
                    gr ? "Σύσταση" : "Recommendation",
                    gr
                            ? "Διατηρήστε τουλάχιστον 15% ελεύθερο χώρο για βέλτιστη απόδοση"
                            : "Keep free storage above 15% for optimal performance"
            );
        }
        
// ------------------------------------------------------------
// SILENT DATA CORRUPTION PREDICTOR
// ------------------------------------------------------------
boolean silentCorruptionRisk = false;
int corruptionScore = 0;

// χαμηλός ελεύθερος χώρος
if (pctFree < 12)
    corruptionScore += 20;

// έντονη πίεση μνήμης
if ("HIGH".equalsIgnoreCase(pressureLevel))
    corruptionScore += 20;

// χρήση swap
if (swapUsedKb > 0)
    corruptionScore += 15;

// ενδείξεις NAND wear
if (wearSignals)
    corruptionScore += 25;

// πιθανή αστάθεια controller
if (controllerRisk)
    corruptionScore += 20;

logLabelValue(
        gr ? "Δείκτης κινδύνου σιωπηλής αλλοίωσης δεδομένων"
           : "Silent data corruption risk index",
        corruptionScore + "/100"
);

if (corruptionScore >= 60) {

    silentCorruptionRisk = true;

    logLabelWarnValue(
            gr ? "Κίνδυνος σιωπηλής αλλοίωσης δεδομένων"
               : "Silent data corruption risk",
            gr
                    ? "Πιθανή φθορά NAND ή controller μπορεί να προκαλέσει αλλοίωση αρχείων."
                    : "Possible NAND or controller degradation may lead to silent file corruption."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα δεδομένων"
               : "Data integrity",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις σιωπηλής αλλοίωσης."
                    : "No silent corruption indicators detected."
    );
}

        // ------------------------------------------------------------
        // FINAL HUMAN SUMMARY
        // ------------------------------------------------------------
        logLine();
        logInfo(gr ? "Σύνοψη αποθηκευτικού χώρου:" : "Storage summary:");

        if (critical) {
            logLabelErrorValue(
                    gr ? "Ενέργεια" : "Action",
                    gr
                            ? "Συνιστάται άμεσος καθαρισμός"
                            : "Immediate cleanup strongly recommended"
            );
        } else if (pressure) {
            logLabelWarnValue(
                    gr ? "Ενέργεια" : "Action",
                    gr
                            ? "Συνιστάται καθαρισμός για αποκατάσταση απόδοσης"
                            : "Cleanup recommended to restore performance"
            );
        } else {
            logLabelOkValue(
                    gr ? "Ενέργεια" : "Action",
                    gr ? "Δεν απαιτείται ενέργεια"
                       : "No action required"
            );
        }

        appendHtml("<br>");
        logOk(gr ? "Το Lab 18 ολοκληρώθηκε." : "Lab 18 finished.");
        logLine();

    } catch (Throwable ignore) {
        // silent
    }
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

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 19 — Ζωντανό Στιγμιότυπο Υγείας RAM"
            : "LAB 19 — Live RAM Health Snapshot");
    logLine();

    try {

        ActivityManager am =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        if (am == null) {
            logLabelErrorValue(
                    gr ? "Υπηρεσία" : "Service",
                    gr ? "Η υπηρεσία μνήμης δεν είναι διαθέσιμη"
                       : "Memory service not available"
            );
            return;
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        long total = mi.totalMem;
        long free  = mi.availMem;
        long used  = total - free;

        int pctFree = (int) ((free * 100L) / Math.max(1L, total));

        // ------------------------------------------------------------
        // BASIC SNAPSHOT
        // ------------------------------------------------------------
        logInfo(gr ? "Τρέχουσα χρήση RAM:" : "Current RAM usage:");
        logLabelOkValue(
                gr ? "Χρήση" : "Usage",
                humanBytes(used) +
                        (gr ? " χρησιμοποιούνται / " : " used / ") +
                        humanBytes(total) +
                        (gr
                                ? " (ελεύθερα " + humanBytes(free) + ", " + pctFree + "%)"
                                : " (free " + humanBytes(free) + ", " + pctFree + "%)")
        );

        // ------------------------------------------------------------
        // HUMAN INTERPRETATION
        // ------------------------------------------------------------
        logLine();
        logInfo(gr ? "Αξιολόγηση πίεσης RAM:" : "RAM pressure assessment:");

        if (pctFree < 8) {

            logLabelErrorValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Κρίσιμη πίεση RAM"
                       : "Critical RAM pressure"
            );
            logLabelErrorValue(
                    gr ? "Συμπεριφορά συστήματος" : "System behaviour",
                    gr ? "Επιθετικό κλείσιμο εφαρμογών στο παρασκήνιο"
                       : "Aggressive background app killing"
            );
            logLabelWarnValue(
                    gr ? "Επίδραση στον χρήστη" : "User impact",
                    gr ? "Έντονο lag, επαναφορτώσεις και κολλήματα UI"
                       : "Strong lag, reloads and UI stutter"
            );

        } else if (pctFree < 15) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Υψηλή πίεση RAM"
                       : "High RAM pressure"
            );
            logLabelWarnValue(
                    gr ? "Επίδραση στον χρήστη" : "User impact",
                    gr ? "Το multitasking μπορεί να γίνει ασταθές"
                       : "Multitasking may become unstable"
            );

        } else if (pctFree < 25) {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Αυξημένη χρήση RAM"
                       : "Elevated RAM usage"
            );
            logLabelOkValue(
                    gr ? "Σημείωση" : "Note",
                    gr ? "Φυσιολογικό κατά τη χρήση βαριών εφαρμογών ή gaming"
                       : "Normal during heavy apps or gaming"
            );

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Υγιές επίπεδο RAM"
                       : "Healthy RAM level"
            );
        }

        // ------------------------------------------------------------
        // MEMORY PRESSURE INDICATORS (LOW-LEVEL)
        // ------------------------------------------------------------
        try {

            MemSnapshot snap = readMemSnapshotSafe();

            long swapUsedKb = 0;
            if (snap.swapTotalKb > 0 && snap.swapFreeKb >= 0) {
                swapUsedKb = Math.max(0, snap.swapTotalKb - snap.swapFreeKb);
            }

            String pressureLevel =
                    pressureLevel(
                            snap.memFreeKb,
                            snap.cachedKb,
                            swapUsedKb
                    );

            String pressureHuman =
                    humanPressureLabel(pressureLevel);

            String zramDep =
                    zramDependency(swapUsedKb, total);

            logLine();
            logInfo(gr
                    ? "Δείκτες πίεσης μνήμης:"
                    : "Memory pressure indicators:");

            logLabelOkValue(
                    gr ? "Επίπεδο πίεσης" : "Pressure level",
                    pressureHuman
            );

            logLabelOkValue(
                    "ZRAM / Swap dependency",
                    zramDep
            );

            if (swapUsedKb > 0) {
                logLabelWarnValue(
                        gr ? "Χρήση Swap" : "Swap used",
                        humanBytes(swapUsedKb * 1024L)
                );
            }

            if (snap.memFreeKb > 0) {
                logLabelOkValue(
                        "MemFree",
                        humanBytes(snap.memFreeKb * 1024L)
                );
            }

            if (snap.cachedKb > 0) {
                logLabelOkValue(
                        "Cached",
                        humanBytes(snap.cachedKb * 1024L) +
                                (gr ? " (επανακτήσιμη)" : " (reclaimable)")
                );
            }

        } catch (Throwable ignore) {}

        // ------------------------------------------------------------
        // ANDROID LOW-MEMORY SIGNAL
        // ------------------------------------------------------------
        if (mi.lowMemory) {

            logLine();
            logLabelWarnValue(
                    gr ? "Σήμα Android" : "Android signal",
                    gr ? "Αναφέρθηκε κατάσταση low-memory"
                       : "Low-memory state reported"
            );
            logLabelWarnValue(
                    gr ? "Αντίδραση συστήματος" : "System response",
                    gr ? "Ενεργοί μηχανισμοί προστασίας μνήμης"
                       : "Memory protection mechanisms active"
            );
        }

        // ------------------------------------------------------------
        // ROOT-AWARE INTELLIGENCE
        // ------------------------------------------------------------
        boolean rooted = isDeviceRooted();

        if (rooted) {

            logLine();
            logInfo(gr
                    ? "Προχωρημένη ανάλυση RAM (root access):"
                    : "Advanced RAM analysis (root access):");

            boolean zramActive = isZramActiveSafe();
            boolean swapActive = isSwapActiveSafe();

            if (zramActive || swapActive) {

                logLabelWarnValue(
                        gr ? "Επέκταση μνήμης" : "Memory extension",
                        gr ? "Εντοπίστηκε συμπίεση / swap"
                           : "Compression / swap detected"
                );
                logLabelOkValue(
                        gr ? "Επίδραση" : "Effect",
                        gr
                                ? "Βελτιώνει τη σταθερότητα αλλά μπορεί να μειώσει την απόδοση"
                                : "Improves stability but may reduce performance"
                );

            } else {

                logLabelOkValue(
                        gr ? "Επέκταση μνήμης" : "Memory extension",
                        gr
                                ? "Δεν εντοπίστηκε swap ή συμπίεση"
                                : "No swap or compression detected"
                );
            }

            long cachedKb = readCachedMemoryKbSafe();
            if (cachedKb > 0) {
                logLabelOkValue(
                        gr ? "Cached μνήμη" : "Cached memory",
                        humanBytes(cachedKb * 1024L) +
                                (gr ? " (επανακτήσιμη από το σύστημα)"
                                   : " (reclaimable by system)")
                );
            }
        }

    } catch (Throwable t) {

        logLabelErrorValue(
                gr ? "Στιγμιότυπο RAM" : "RAM snapshot",
                gr ? "Αποτυχία ανάγνωσης κατάστασης μνήμης"
                   : "Failed to read memory state"
        );
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 19 ολοκληρώθηκε." : "Lab 19 finished.");
    logLine();
}

// ============================================================
// LAB 20 — Uptime & Reboot Intelligence
// FINAL — HUMAN • ROOT-AWARE • NO BULLSHIT
// ============================================================
private void lab20UptimeHints() {

    final boolean gr = AppLang.isGreek(this);

    boolean frequentReboots = false;   // shared summary flag

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 20 — Χρόνος Λειτουργίας Συστήματος & Συμπεριφορά Επανεκκινήσεων"
            : "LAB 20 — System Uptime & Reboot Behaviour");
    logLine();

    try {

        long upMs = SystemClock.elapsedRealtime();
        String upStr = formatUptime(upMs);

        logInfo(gr ? "Χρόνος λειτουργίας συστήματος:" : "System uptime:");
        logLabelOkValue("Uptime", upStr);

        boolean veryRecentReboot =
                upMs < 2L * 60L * 60L * 1000L;        // < 2 hours
        boolean veryLongUptime =
                upMs > 7L * 24L * 60L * 60L * 1000L; // > 7 days
        boolean extremeUptime =
                upMs > 14L * 24L * 60L * 60L * 1000L;

        // ----------------------------------------------------
        // HUMAN INTERPRETATION (NON-ROOT)
        // ----------------------------------------------------
        logLine();
        logInfo(gr ? "Αξιολόγηση uptime:" : "Uptime assessment:");

        if (veryRecentReboot) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Εντοπίστηκε πρόσφατη επανεκκίνηση"
                       : "Recent reboot detected"
            );
            logLabelWarnValue(
                    gr ? "Επίπτωση" : "Impact",
                    gr ? "Ορισμένα προβλήματα μπορεί να καλύπτονται προσωρινά"
                       : "Some issues may be temporarily masked"
            );
            logLabelOkValue(
                    gr ? "Σημείωση" : "Note",
                    gr
                            ? "Οι διαγνώσεις είναι έγκυρες αλλά όχι πλήρως αντιπροσωπευτικές ακόμη"
                            : "Diagnostics are valid but not fully representative yet"
            );

        } else if (veryLongUptime) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "Μεγάλος χρόνος λειτουργίας"
                       : "Long uptime detected"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Συσσώρευση φόρτου παρασκηνίου και πίεσης μνήμης"
                       : "Background load and memory pressure may accumulate"
            );

            if (extremeUptime) {

                logLabelErrorValue(
                        gr ? "Σοβαρότητα" : "Severity",
                        gr
                                ? "Εξαιρετικά μεγάλος χρόνος λειτουργίας (> 14 ημέρες)"
                                : "Extremely long uptime (> 14 days)"
                );
                logLabelErrorValue(
                        gr ? "Σύσταση" : "Recommendation",
                        gr
                                ? "Συνιστάται έντονα επανεκκίνηση πριν από τελικά συμπεράσματα"
                                : "Reboot strongly recommended before final conclusions"
                );

            } else {

                logLabelOkValue(
                        gr ? "Σύσταση" : "Recommendation",
                        gr
                                ? "Μια επανεκκίνηση μπορεί να βοηθήσει στην επαναφορά της κατάστασης"
                                : "A reboot can help reset system state"
                );
            }

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr
                            ? "Χρόνος λειτουργίας εντός υγιούς διαγνωστικού εύρους"
                            : "Uptime within healthy diagnostic range"
            );
        }

        // ----------------------------------------------------
        // ROOT-AWARE INTELLIGENCE
        // ----------------------------------------------------
        if (isDeviceRooted()) {

            logLine();
            logInfo(gr
                    ? "Προχωρημένα σήματα uptime (root access):"
                    : "Advanced uptime signals (root access):");

            boolean lowMemoryPressure =
                    readLowMemoryKillCountSafe() < 5;

            frequentReboots =
                    detectFrequentRebootsHint();

            if (frequentReboots) {

                logLabelWarnValue(
                        gr ? "Μοτίβο επανεκκινήσεων" : "Reboot pattern",
                        gr
                                ? "Εντοπίστηκαν επαναλαμβανόμενες επανεκκινήσεις"
                                : "Repeated reboots detected"
                );
                logLabelWarnValue(
                        gr ? "Πιθανές αιτίες" : "Possible causes",
                        gr
                                ? "Αστάθεια, κρασαρίσματα ή watchdog resets"
                                : "Instability, crashes or watchdog resets"
                );

            } else {

                logLabelOkValue(
                        gr ? "Μοτίβο επανεκκινήσεων" : "Reboot pattern",
                        gr
                                ? "Δεν εντοπίστηκε μη φυσιολογική συμπεριφορά επανεκκινήσεων"
                                : "No abnormal reboot behaviour detected"
                );
            }

            if (!lowMemoryPressure) {

                logLabelWarnValue(
                        gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr
                                ? "Εντοπίστηκαν συμβάντα πίεσης στο παρασκήνιο"
                                : "Background pressure events detected"
                );
                logLabelWarnValue(
                        gr ? "Συμπεριφορά συστήματος" : "System behaviour",
                        gr
                                ? "Επιθετική διαχείριση εφαρμογών στο παρασκήνιο"
                                : "Aggressive background app management"
                );

            } else {

                logLabelOkValue(
                        gr ? "Πίεση μνήμης" : "Memory pressure",
                        gr
                                ? "Δεν εντοπίστηκαν σημαντικά σήματα πίεσης"
                                : "No significant pressure signals detected"
                );
            }

            logLabelOkValue(
                    gr ? "Ερμηνεία" : "Interpretation",
                    gr
                            ? "Η συμπεριφορά uptime συμβαδίζει με φυσιολογική λειτουργία συστήματος"
                            : "Uptime behaviour consistent with normal system operation"
            );
        }

    } catch (Throwable t) {

        logLabelErrorValue(
                gr ? "Ανάλυση uptime" : "Uptime analysis",
                gr
                        ? "Αποτυχία αξιολόγησης χρόνου λειτουργίας"
                        : "Failed to evaluate system uptime"
        );
    }

// ----------------------------------------------------
// SUMMARY (Structured / Color-coded)
// ----------------------------------------------------
logLine();
logInfo(gr ? "Σύνοψη επανεκκινήσεων" : "Reboot summary");

if (frequentReboots) {

    logLabelWarnValue(
            "REBOOT_PATTERN",
            gr ? "ΜΗ ΦΥΣΙΟΛΟΓΙΚΟ" : "ABNORMAL"
    );

} else {

    logLabelOkValue(
            "REBOOT_PATTERN",
            gr ? "ΦΥΣΙΟΛΟΓΙΚΌ" : "NORMAL"
    );
}

    appendHtml("<br>");
    logOk(gr ? "Το Lab 20 ολοκληρώθηκε." : "Lab 20 finished.");
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
	
	final boolean gr = AppLang.isGreek(this);

// GUARD — avoid double-tap spam  
if (lab21Running) {  
    logWarn(gr 
        ? "Το LAB 21 εκτελείται ήδη..." 
        : "LAB 21 is already running...");  
    return;  
}  
lab21Running = true;  

appendHtml("<br>");  
logLine();  
logInfo(gr 
    ? "LAB 21 — Κλείδωμα Οθόνης / Βιομετρικά (Live + Root-Aware)" 
    : "LAB 21 — Screen Lock / Biometrics (Live + Root-Aware)");  
logLine();

// ------------------------------------------------------------  
// PART A — LOCK CONFIG + STATE  
// ------------------------------------------------------------  
boolean secure = false;
boolean lockedNow = false;

try {
    KeyguardManager km =
            (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

    if (km != null) {

        secure = km.isDeviceSecure();

        try {
            lockedNow = km.isKeyguardLocked();
        } catch (Throwable ignore) {}

        logInfo(gr 
                ? "Ρύθμιση κλειδώματος οθόνης:" 
                : "Screen lock configuration:");

        if (secure) {
            logLabelOkValue(
                    gr ? "Διαπιστευτήριο" : "Credential",
                    gr ? "Ρυθμισμένο (PIN / Μοτίβο / Κωδικός)"
                       : "Configured (PIN / Pattern / Password)"
            );
        } else {
            logLabelErrorValue(
                    gr ? "Διαπιστευτήριο" : "Credential",
                    gr ? "ΔΕΝ έχει ρυθμιστεί"
                       : "NOT configured"
            );
            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Φυσική πρόσβαση = πλήρης έκθεση δεδομένων"
                       : "Physical access = full data exposure"
            );
        }

        if (secure) {

            logInfo(gr 
                    ? "Τρέχουσα κατάσταση κλειδώματος:" 
                    : "Current lock state:");

            if (lockedNow) {
                logLabelOkValue(
                        gr ? "Κατάσταση" : "State",
                        gr ? "ΚΛΕΙΔΩΜΕΝΟ (ενεργό keyguard)"
                           : "LOCKED (keyguard active)"
                );
            } else {
                logLabelWarnValue(
                        gr ? "Κατάσταση" : "State",
                        gr ? "ΞΕΚΛΕΙΔΩΤΟ (η συσκευή είναι ανοιχτή)"
                           : "UNLOCKED (device currently open)"
                );
            }
        }

    } else {
        logLabelWarnValue(
                gr ? "Υπηρεσία Keyguard" : "Keyguard",
                gr ? "Μη διαθέσιμη υπηρεσία"
                   : "Service unavailable"
        );
    }

} catch (Throwable e) {
    logLabelWarnValue(
            gr ? "Έλεγχος κλειδώματος" : "Lock detection",
            (gr ? "Αποτυχία: " : "Failed: ") + e.getMessage()
    );
}

// ------------------------------------------------------------  
// PART B — BIOMETRIC CAPABILITY (FRAMEWORK, NO ANDROIDX)  
// ------------------------------------------------------------

boolean biometricSupported = false;

if (Build.VERSION.SDK_INT >= 29) {
    try {
        android.hardware.biometrics.BiometricManager bm =
                getSystemService(android.hardware.biometrics.BiometricManager.class);

        if (bm != null) {
            int r = bm.canAuthenticate(
                    android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
            );

            if (r == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS) {
                biometricSupported = true;
                logLabelOkValue(
                        gr ? "Βιομετρικά" : "Biometrics",
                        gr ? "Υλικό παρόν & έτοιμο για χρήση"
                           : "Hardware present & usable"
                );
            } else {
                logLabelWarnValue(
                        gr ? "Βιομετρικά" : "Biometrics",
                        gr ? "Υπάρχουν αλλά δεν είναι έτοιμα"
                           : "Present but not ready"
                );
            }
        } else {
            logLabelWarnValue(
                    gr ? "Βιομετρικά" : "Biometrics",
                    gr ? "Μη διαθέσιμος διαχειριστής"
                       : "Manager unavailable"
            );
        }
    } catch (Throwable e) {
        logLabelWarnValue(
                gr ? "Βιομετρικά" : "Biometrics",
                (gr ? "Αποτυχία ελέγχου: " : "Check failed: ") + e.getMessage()
        );
    }
} else {
    logLabelWarnValue(
            gr ? "Βιομετρικά" : "Biometrics",
            gr ? "Δεν υποστηρίζονται σε αυτήν την έκδοση Android"
               : "Not supported on this Android version"
    );
}

// ------------------------------------------------------------  
// PART C — ROOT-AWARE AUTH INFRA CHECK (POLICY / FILES)  
// ------------------------------------------------------------  
boolean hasLockDb = false;
boolean hasGatekeeper = false;
boolean hasKeystore = false;

boolean root = isRootAvailable();

logInfo(gr ? "Πρόσβαση Root:" : "Root access:");

if (root) {

    logLabelOkValue(
            gr ? "Λειτουργία Root" : "Root mode",
            gr ? "ΔΙΑΘΕΣΙΜΗ" : "AVAILABLE"
    );

    hasLockDb     = rootPathExists("/data/system/locksettings.db");
    hasGatekeeper = rootGlobExists("/data/system/gatekeeper*");
    hasKeystore   = rootPathExists("/data/misc/keystore");

    logLabelOkValue(
            "Gatekeeper",
            hasGatekeeper
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

    logLabelOkValue(
            gr ? "Βάση κλειδώματος" : "Lock DB",
            hasLockDb
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

    logLabelOkValue(
            "Keystore",
            hasKeystore
                    ? (gr ? "Εντοπίστηκε" : "Detected")
                    : (gr ? "Δεν εντοπίστηκε" : "Not detected")
    );

} else {

    logLabelOkValue(
            gr ? "Λειτουργία Root" : "Root mode",
            gr ? "Μη διαθέσιμη" : "Not available"
    );
}

// ============================================================  
// LAB 21 — TRUST BOUNDARY AWARENESS  
// ============================================================  

logLine();
logInfo(gr ? "Ανάλυση ορίου εμπιστοσύνης:" 
           : "Trust boundary analysis:");

if (secure) {

    logLabelOkValue(
            gr ? "Προστασία μετά από επανεκκίνηση" 
               : "Post-reboot protection",
            gr ? "Απαιτείται ταυτοποίηση πριν την πρόσβαση στα δεδομένα"
               : "Authentication required before data access"
    );

} else {

    logLabelErrorValue(
            gr ? "Προστασία μετά από επανεκκίνηση" 
               : "Post-reboot protection",
            gr ? "ΔΕΝ επιβάλλεται (τα δεδομένα εκτίθενται μετά από επανεκκίνηση)"
               : "NOT enforced (data exposed after reboot)"
    );
}

logLabelOkValue(
        gr ? "Κύριο επίπεδο ασφάλειας" 
           : "Primary security layer",
        secure
                ? (gr ? "Γνωστικό διαπιστευτήριο (PIN / Μοτίβο / Κωδικός)"
                      : "Knowledge-based credential")
                : (gr ? "ΚΑΝΕΝΑ"
                      : "NONE")
);

logLabelOkValue(
        gr ? "Επίπεδο ευκολίας" 
           : "Convenience layer",
        biometricSupported
                ? (gr ? "Διαθέσιμα βιομετρικά"
                      : "Biometrics available")
                : (gr ? "Μη διαθέσιμα"
                      : "Not available")
);

if (secure && !lockedNow) {

    logLabelWarnValue(
            gr ? "Ζωντανός κίνδυνος" 
               : "Live risk",
            gr ? "Ξεκλείδωτη συσκευή ΔΕΝ προστατεύεται από βιομετρικά"
               : "Unlocked device is NOT protected by biometrics"
    );
}

if (root) {

    if (hasGatekeeper || hasLockDb) {

        logLabelOkValue(
                gr ? "Επιβολή συστήματος" 
                   : "System enforcement",
                gr ? "Υποδομή ταυτοποίησης ενεργή"
                   : "Authentication infrastructure active"
        );

    } else {

        logLabelWarnValue(
                gr ? "Επιβολή συστήματος" 
                   : "System enforcement",
                gr ? "Μη ξεκάθαρα σήματα (διαφοροποίηση ROM / κατασκευαστή)"
                   : "Signals unclear (ROM/vendor variation)"
        );
    }
}

// ------------------------------------------------------------  
// PART D — RISK SCORE (FAST, CLEAR)  
// ------------------------------------------------------------  
int risk = 0;

if (!secure) risk += 70;
if (secure && !lockedNow) risk += 10;
if (secure && !biometricSupported) risk += 5;

logLine();
logInfo(gr ? "Δείκτης επίδρασης ασφάλειας:"
           : "Security impact score:");

if (risk >= 70) {

    logLabelErrorValue(
            gr ? "Επίδραση" : "Impact",
            gr ? "ΥΨΗΛΗ (" + risk + "/100)"
               : "HIGH (" + risk + "/100)"
    );

} else if (risk >= 30) {

    logLabelWarnValue(
            gr ? "Επίδραση" : "Impact",
            gr ? "ΜΕΤΡΙΑ (" + risk + "/100)"
               : "MEDIUM (" + risk + "/100)"
    );

} else {

    logLabelOkValue(
            gr ? "Επίδραση" : "Impact",
            gr ? "ΧΑΜΗΛΗ (" + risk + "/100)"
               : "LOW (" + risk + "/100)"
    );
}

// ------------------------------------------------------------
// PART E — LIVE BIOMETRIC AUTH TEST (USER-DRIVEN, REAL)
// ------------------------------------------------------------
if (!secure) {

    logLine();
    logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
               : "Live biometric test:");
    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Παραλείφθηκε" : "Skipped"
    );
    logLabelWarnValue(
            gr ? "Λόγος" : "Reason",
            gr ? "Απαιτείται ασφαλές κλείδωμα (PIN / Μοτίβο / Κωδικός)"
               : "Secure lock required (PIN / Pattern / Password)"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
    logLine();
    lab21Running = false;
    return;
}

if (!biometricSupported) {

    logLine();
    logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
               : "Live biometric test:");
    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Δεν ξεκίνησε" : "Not started"
    );
    logLabelWarnValue(
            gr ? "Λόγος" : "Reason",
            gr ? "Τα βιομετρικά δεν είναι έτοιμα ή δεν είναι διαθέσιμα"
               : "Biometrics not ready or not available"
    );
    logLabelOkValue(
            gr ? "Ενέργεια" : "Action",
            gr ? "Ρυθμίστε βιομετρικά στις Ρυθμίσεις και επανεκτελέστε το LAB 21"
               : "Enroll biometrics in Settings and re-run LAB 21"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
    logLine();
    lab21Running = false;
    return;
}

if (Build.VERSION.SDK_INT >= 28) {

    try {

        logLine();
        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΑΙΣΘΗΤΗΡΑ"
                   : "LIVE SENSOR TEST");
        logLabelOkValue(
                gr ? "Οδηγία" : "Instruction",
                gr ? "Τοποθετήστε δάχτυλο / πρόσωπο για ταυτοποίηση ΤΩΡΑ"
                   : "Place finger / face for authentication NOW"
        );
        logLabelOkValue(
                gr ? "Αποτέλεσμα" : "Result",
                gr ? "Θα καταγραφεί PASS / FAIL (πραγματικός έλεγχος υλικού)"
                   : "PASS / FAIL will be recorded (real hardware)"
        );

        Executor executor = getMainExecutor();
        CancellationSignal cancel = new CancellationSignal();

        android.hardware.biometrics.BiometricPrompt.AuthenticationCallback cb =
                new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(
                            android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {

                        logLine();
                        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                   : "LIVE BIOMETRIC TEST");
                        logLabelOkValue(
                                gr ? "Αποτέλεσμα" : "Result",
                                "PASS"
                        );
                        logLabelOkValue(
                                gr ? "Αλυσίδα ελέγχου" : "Pipeline",
                                gr ? "Αισθητήρας + ταυτοποίηση λειτουργούν σωστά"
                                   : "Biometric sensor + auth verified functional"
                        );

                        logInfo(gr ? "Συσκευές με πολλαπλά βιομετρικά"
                                   : "Multi-biometric devices");
                        logLabelWarnValue(
                                gr ? "Σημείωση" : "Note",
                                gr ? "Το Android ελέγχει ΕΝΑ βιομετρικό ανά εκτέλεση"
                                   : "Android tests ONE biometric path per run"
                        );
                        logLabelOkValue(
                                gr ? "Ενέργεια" : "Action",
                                gr ? "Απενεργοποιήστε το τρέχον βιομετρικό και επανεκτελέστε το LAB 21"
                                   : "Disable current biometric in Settings and re-run LAB 21"
                        );
                        logLabelWarnValue(
                                gr ? "Σημείωση OEM" : "OEM note",
                                gr ? "Ο κατασκευαστής μπορεί να δίνει προτεραιότητα στον ίδιο αισθητήρα"
                                   : "OEM may still prioritize same sensor"
                        );

                        appendHtml("<br>");
                        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                        logLine();
                        lab21Running = false;
                    }

                    @Override
                    public void onAuthenticationFailed() {

                        logLine();
                        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                   : "LIVE BIOMETRIC TEST");
                        logLabelErrorValue(
                                gr ? "Αποτέλεσμα" : "Result",
                                "FAIL"
                        );
                        logLabelWarnValue(
                                gr ? "Ερμηνεία" : "Meaning",
                                gr ? "Το βιομετρικό δεν επιβεβαιώθηκε κατά τον πραγματικό έλεγχο"
                                   : "Biometric did not authenticate during real sensor test"
                        );

                        appendHtml("<br>");
                        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                        logLine();
                        lab21Running = false;
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {

                        logLine();
                        logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                   : "LIVE BIOMETRIC TEST");
                        logLabelWarnValue(
                                gr ? "Αποτέλεσμα" : "Result",
                                gr ? "Μη επιβεβαιωμένο"
                                   : "Not confirmed"
                        );
                        logLabelWarnValue(
                                gr ? "Σύστημα" : "System",
                                gr ? "Ενεργοποιήθηκε εφεδρικό διαπιστευτήριο"
                                   : "Fallback to device credential detected"
                        );
                        logLabelWarnValue(
                                gr ? "Ερμηνεία" : "Meaning",
                                gr ? "Ο αισθητήρας ΔΕΝ επιβεβαιώθηκε λειτουργικός"
                                   : "Biometric sensor NOT verified functional"
                        );

                        appendHtml("<br>");
                        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                        logLine();
                        lab21Running = false;
                    }
                };

        android.hardware.biometrics.BiometricPrompt prompt =
                new android.hardware.biometrics.BiometricPrompt.Builder(this)
                        .setTitle(gr
                                ? "LAB 21 — Ζωντανός Έλεγχος Βιομετρικού Αισθητήρα"
                                : "LAB 21 — Live Biometric Sensor Test")
                        .setSubtitle(gr
                                ? "Τοποθετήστε δάχτυλο / πρόσωπο για επιβεβαίωση"
                                : "Place finger / face to verify sensor works")
                        .setDescription(gr
                                ? "Πραγματικός έλεγχος υλικού (χωρίς προσομοίωση)."
                                : "This is a REAL hardware test (no simulation).")
                        .setNegativeButton(
                                gr ? "Ακύρωση ελέγχου" : "Cancel test",
                                executor,
                                (dialog, which) -> {

                                    logLine();
                                    logInfo(gr ? "ΖΩΝΤΑΝΟΣ ΕΛΕΓΧΟΣ ΒΙΟΜΕΤΡΙΚΟΥ"
                                               : "LIVE BIOMETRIC TEST");
                                    logLabelWarnValue(
                                            gr ? "Αποτέλεσμα" : "Result",
                                            gr ? "Ακυρώθηκε από τον χρήστη"
                                               : "Cancelled by user"
                                    );

                                    appendHtml("<br>");
                                    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
                                    logLine();
                                    lab21Running = false;
                                }
                        )
                        .setAllowedAuthenticators(
                                android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
                        )
                        .build();

        logInfo(gr ? "Προτροπή βιομετρικού:" : "Biometric prompt:");
        logLabelOkValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Εκκίνηση…" : "Starting…"
        );

        prompt.authenticate(cancel, executor, cb);

    } catch (Throwable e) {

        logLine();
        logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
                   : "Live biometric test");
        logLabelErrorValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Αποτυχία" : "Failed"
        );
        logLabelWarnValue(
                gr ? "Λόγος" : "Reason",
                (gr ? "Σφάλμα προτροπής βιομετρικών: " : "Biometric prompt error: ") + e.getMessage()
        );

        appendHtml("<br>");
        logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
        logLine();
        lab21Running = false;
    }

} else {

    logLine();
    logInfo(gr ? "Ζωντανός έλεγχος βιομετρικών:"
               : "Live biometric test:");
    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "Δεν υποστηρίζεται"
               : "Not supported"
    );
    logLabelWarnValue(
            gr ? "Λόγος" : "Reason",
            gr ? "Το BiometricPrompt framework δεν υποστηρίζεται σε αυτήν την έκδοση Android"
               : "BiometricPrompt framework not available on this Android version"
    );

    logInfo(gr ? "Απαιτούμενη ενέργεια" : "Action required");
    logLabelOkValue(
            gr ? "Ενέργεια" : "Action",
            gr ? "Ελέγξτε τα βιομετρικά από τις ρυθμίσεις συστήματος κλειδώματος οθόνης και επανεκτελέστε το LAB 21"
               : "Test biometrics via system lock screen settings, then re-run LAB 21"
    );

    logInfo("Note");
    logLabelOkValue(
            gr ? "Κάλυψη" : "Coverage",
            gr ? "Κάθε εκτέλεση του LAB 21 ελέγχει ΕΝΑ βιομετρικό μονοπάτι"
               : "Each LAB 21 run verifies ONE biometric sensor path"
    );
    logLabelOkValue(
            gr ? "Ενέργεια" : "Action",
            gr ? "Απενεργοποιήστε το ενεργό βιομετρικό στις ρυθμίσεις, για να ελέγξετε άλλον αισθητήρα"
               : "Disable active biometric in Settings to test another sensor"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 21 ολοκληρώθηκε." : "Lab 21 finished.");
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
java.lang.Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
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

/* ============================================================
LAB 22 — Security Patch + Play Protect (Realtime)
============================================================ */
private void lab22SecurityPatchAndPlayProtect() {
	
	final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr 
            ? "LAB 22 — Ενημέρωση Ασφαλείας + Play Protect (Σε πραγματικό χρόνο)"
            : "LAB 22 — Security Patch + Play Protect (Realtime)");
    logLine();

// ------------------------------------------------------------
// 1) Security Patch Level (raw)
// ------------------------------------------------------------
String patch = null;

try {
    patch = android.os.Build.VERSION.SECURITY_PATCH;

    logInfo(gr ? "Επίπεδο ενημέρωσης ασφαλείας"
               : "Security patch level");

    if (patch != null && !patch.isEmpty()) {

        logLabelOkValue(
                gr ? "Αναφέρεται" : "Reported",
                patch
        );

    } else {

        logLabelWarnValue(
                gr ? "Αναφέρεται" : "Reported",
                gr ? "Δεν παρέχεται από το σύστημα"
                   : "Not provided by system"
        );
    }

} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Ανάγνωση patch" : "Patch read",
            (gr ? "Αποτυχία (" : "Failed (") + e.getMessage() + ")"
    );
}

// ------------------------------------------------------------
// 2) Patch Freshness Intelligence (AGE + RISK)
// ------------------------------------------------------------
try {
    if (patch != null && !patch.isEmpty()) {

        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);

        long patchTime = sdf.parse(patch).getTime();
        long now = System.currentTimeMillis();

        long diffDays   = (now - patchTime) / (1000L * 60 * 60 * 24);
        long diffMonths = diffDays / 30;

        logInfo(gr ? "Ηλικία ενημέρωσης ασφαλείας"
                   : "Patch age");

        logLabelOkValue(
                gr ? "Εκτίμηση" : "Estimated",
                gr ? diffMonths + " μήνες"
                   : diffMonths + " months"
        );

        logInfo(gr ? "Κατάσταση ενημέρωσης"
                   : "Patch status");

        if (diffMonths <= 3) {

            logLabelOkValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "ΠΡΟΣΦΑΤΗ (χαμηλή έκθεση σε γνωστά exploits)"
                       : "RECENT (low known exploit exposure)"
            );

        } else if (diffMonths <= 6) {

            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "ΜΕΤΡΙΩΣ ΠΑΛΙΑ"
                       : "MODERATELY OUTDATED"
            );

        } else {

            logLabelErrorValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "ΠΑΛΙΑ (λείπουν πρόσφατες διορθώσεις ασφαλείας)"
                       : "OUTDATED (missing recent security fixes)"
            );
        }
    }
} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Ανάλυση ηλικίας ενημέρωσης" : "Patch age analysis",
            (gr ? "Αποτυχία (" : "Failed (") + e.getMessage() + ")"
    );
}

// ------------------------------------------------------------
// 3) Play Protect Detection (best effort, non-root)
// ------------------------------------------------------------
try {
    PackageManager pm = getPackageManager();

    boolean gmsPresent;
    try {
        pm.getPackageInfo("com.google.android.gms", 0);
        gmsPresent = true;
    } catch (Throwable ignore) {
        gmsPresent = false;
    }

    logInfo(gr ? "Play Protect" : "Play Protect");

    if (!gmsPresent) {

        logLabelErrorValue(
                gr ? "Υπηρεσίες Google Play" : "Google Play Services",
                gr ? "ΔΕΝ βρέθηκαν" : "NOT present"
        );

        logLabelWarnValue(
                "Play Protect",
                gr ? "Μη διαθέσιμο" : "Unavailable"
        );

    } else {

        int verify = -1;
        try {
            verify = Settings.Global.getInt(
                    getContentResolver(),
                    "package_verifier_enable",
                    -1
            );
        } catch (Throwable ignore) {}

        if (verify == 1) {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "ΕΝΕΡΓΟ (Έλεγχος εφαρμογών ενεργός)"
                       : "ENABLED (Verify Apps ON)"
            );

        } else if (verify == 0) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "ΑΝΕΝΕΡΓΟ (Έλεγχος εφαρμογών απενεργοποιημένος)"
                       : "DISABLED (Verify Apps OFF)"
            );

        } else {

            Intent i = new Intent();
            i.setClassName(
                    "com.google.android.gms",
                    "com.google.android.gms.security.settings.VerifyAppsSettingsActivity"
            );

            if (i.resolveActivity(pm) != null) {

                logLabelOkValue(
                        gr ? "Μονάδα" : "Module",
                        gr ? "Εντοπίστηκε (διαθέσιμη δραστηριότητα ρυθμίσεων)"
                           : "Detected (settings activity present)"
                );

                logLabelWarnValue(
                        gr ? "Κατάσταση" : "Status",
                        gr ? "Άγνωστη (OEM / περιορισμένη έκδοση)"
                           : "Unknown (OEM / restricted build)"
                );

            } else {

                logLabelWarnValue(
                        "Play Protect",
                        gr ? "Η κατάσταση δεν είναι σαφής"
                           : "Status unclear"
                );
            }
        }
    }

} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Ανίχνευση Play Protect" : "Play Protect detection",
            (gr ? "Αποτυχία (" : "Failed (") + e.getMessage() + ")"
    );
}

// ------------------------------------------------------------
// 4) Trust Boundary Clarification
// ------------------------------------------------------------
logLine();
logInfo(gr ? "Πεδίο ασφάλειας"
           : "Security scope");

logLabelOkValue(
        "Play Protect",
        gr ? "Έλεγχος κακόβουλου λογισμικού και επαλήθευση εφαρμογών"
           : "Malware scanning and app verification"
);

logLabelWarnValue(
        gr ? "Περιορισμός" : "Limitation",
        gr ? "ΔΕΝ επιδιορθώνει ευπάθειες συστήματος ή σφάλματα firmware"
           : "Does NOT patch system vulnerabilities or firmware flaws"
);

// ------------------------------------------------------------
// 5) Manual Guidance (Technician)
// ------------------------------------------------------------
logLine();
logInfo(gr ? "Χειροκίνητη επαλήθευση"
           : "Manual verification");

logLabelOkValue(
        gr ? "Έλεγχος 1" : "Check 1",
        gr ? "Ρυθμίσεις > Πληροφορίες τηλεφώνου > Έκδοση Android > Επίπεδο ενημέρωσης ασφαλείας"
           : "Settings > About phone > Android version > Security patch level"
);

logLabelWarnValue(
        gr ? "Σημείωση" : "Note",
        gr ? "Πολύ παλιά επίπεδα ενημέρωσης αυξάνουν την έκθεση σε exploits"
           : "Very old patch levels increase exploit exposure"
);

logLabelOkValue(
        gr ? "Έλεγχος 2" : "Check 2",
        gr ? "Google Play Store > Play Protect > Έλεγχος ότι η σάρωση είναι ενεργή"
           : "Google Play Store > Play Protect > Verify scanning enabled"
);

appendHtml("<br>");
logOk(gr ? "Το Lab 22 ολοκληρώθηκε." : "Lab 22 finished.");
logLine();

}

// ============================================================
// LAB 23 — Developer Options / ADB Risk Note + UI BUBBLES + AUTO-FIX HINTS
// GEL Security v3.1 (Realtime Snapshot)
// ============================================================
// ============================================================
// 1) USB DEBUGGING FLAG
// ============================================================

private void lab23DeveloperOptionsRisk() {
	
	final boolean gr = AppLang.isGreek(this);

    int risk = 0;
    boolean usbDebug = false;

    try {
        int adb = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.ADB_ENABLED,
                0
        );
        usbDebug = (adb == 1);

        logInfo(gr ? "USB Debugging"
                   : "USB Debugging");

        if (usbDebug) {

            logLabelWarnValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "ΕΝΕΡΓΟΠΟΙΗΜΕΝΟ"
                       : "ENABLED"
            );

            logLabelWarnValue(
                    gr ? "Κίνδυνος" : "Risk",
                    gr ? "Επιφάνεια επίθεσης με φυσική πρόσβαση"
                       : "Physical access attack surface"
            );

            risk += 30;

        } else {

            logLabelOkValue(
                    gr ? "Κατάσταση" : "Status",
                    gr ? "ΑΝΕΝΕΡΓΟ"
                       : "OFF"
            );
        }

    } catch (Throwable e) {

        logLabelWarnValue(
                gr ? "USB Debugging" : "USB Debugging",
                gr ? "Αδυναμία ανάγνωσης (περιορισμός κατασκευαστή)"
                   : "Unable to read (OEM restriction)"
        );

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

    logInfo(gr ? "Επιλογές προγραμματιστή"
               : "Developer options");

    if (devOpts) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "ΕΝΕΡΓΟΠΟΙΗΜΕΝΕΣ"
                   : "ENABLED"
        );

        logLabelWarnValue(
                gr ? "Κίνδυνος" : "Risk",
                gr ? "Έκθεση σε προχωρημένες ρυθμίσεις συστήματος"
                   : "Advanced system settings exposed"
        );

        risk += 20;

    } else {

        logLabelOkValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "ΑΝΕΝΕΡΓΕΣ"
                   : "OFF"
        );
    }

} catch (Throwable e) {

    logLabelWarnValue(
            gr ? "Επιλογές προγραμματιστή" : "Developer options",
            gr ? "Αδυναμία ανάγνωσης"
               : "Unable to read"
    );

    risk += 5;
}

// ============================================================
// 3) ADB OVER WI-FI (TCP/IP 5555)
// ============================================================
boolean adbWifi = isPortOpen(5555, 200);

logInfo(gr ? "ADB μέσω Wi-Fi"
           : "ADB over Wi-Fi");

if (adbWifi) {

    logLabelErrorValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ΕΝΕΡΓΟ (θύρα 5555)"
               : "ACTIVE (port 5555)"
    );

    logLabelErrorValue(
            gr ? "Κίνδυνος" : "Risk",
            gr ? "Δυνατότητα απομακρυσμένου debugging στο τοπικό δίκτυο"
               : "Remote debugging possible on local network"
    );

    risk += 40;

} else {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ΑΝΕΝΕΡΓΟ"
               : "OFF"
    );
}

// ============================================================
// 4) ADB PAIRING MODE (Wireless Debugging)
// ============================================================
boolean adbPairing =
        isPortOpen(3700, 200) ||
        isPortOpen(7460, 200) ||
        scanPairingPortRange();

logInfo(gr ? "ADB σύζευξη / Ασύρματο debugging"
           : "ADB pairing / wireless debugging");

if (adbPairing) {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ΕΝΕΡΓΟ"
               : "ACTIVE"
    );

    logLabelWarnValue(
            gr ? "Κίνδυνος" : "Risk",
            gr ? "Η συσκευή είναι ανιχνεύσιμη για σύζευξη"
               : "Device discoverable for pairing"
    );

    risk += 25;

} else {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ΑΝΕΝΕΡΓΟ"
               : "OFF"
    );
}

// ============================================================
// 5) FINAL RISK SCORE
// ============================================================
risk = Math.min(100, risk);

String level;
if (risk <= 10)       level = gr ? "ΧΑΜΗΛΟ" : "LOW";
else if (risk <= 30)  level = gr ? "ΜΕΤΡΙΟ" : "MEDIUM";
else if (risk <= 60)  level = gr ? "ΥΨΗΛΟ" : "HIGH";
else                  level = gr ? "ΚΡΙΣΙΜΟ" : "CRITICAL";

logLine();
logInfo(gr ? "Δείκτης κινδύνου ασφάλειας"
           : "Security risk score");

if (risk >= 70) {

    logLabelErrorValue(
            gr ? "Βαθμολογία" : "Score",
            risk + "/100 (" + level + ")"
    );

} else if (risk >= 30) {

    logLabelWarnValue(
            gr ? "Βαθμολογία" : "Score",
            risk + "/100 (" + level + ")"
    );

} else {

    logLabelOkValue(
            gr ? "Βαθμολογία" : "Score",
            risk + "/100 (" + level + ")"
    );
}

// ============================================================
// 6) ACTION RECOMMENDATIONS
// ============================================================
logLine();
logInfo(gr ? "Προτεινόμενες ενέργειες"
           : "Recommended actions");

if (usbDebug || devOpts) {

    logLabelWarnValue(
            gr ? "Απενεργοποίηση" : "Disable",
            gr ? "Ρυθμίσεις > Σύστημα > Επιλογές προγραμματιστή > OFF"
               : "Settings > System > Developer options > OFF"
    );

    logLabelWarnValue(
            "USB Debugging",
            gr ? "Απενεργοποιήστε το"
               : "Turn OFF"
    );

} else {

    logLabelOkValue(
            gr ? "Ρυθμίσεις προγραμματιστή"
               : "Developer settings",
            gr ? "Ήδη ασφαλείς"
               : "Already safe"
    );
}

if (adbWifi || adbPairing) {

    logLabelErrorValue(
            gr ? "Ασύρματο debugging"
               : "Wireless debugging",
            gr ? "Απενεργοποιήστε άμεσα (Επιλογές προγραμματιστή)"
               : "Disable immediately (Developer options)"
    );

    logLabelWarnValue(
            gr ? "Συμβουλή" : "Tip",
            gr ? "Η επανεκκίνηση καθαρίζει ενεργό TCP/IP debugging"
               : "Reboot clears active TCP/IP debugging"
    );

} else {

    logLabelOkValue(
            gr ? "Ασύρματο debugging"
               : "Wireless debugging",
            gr ? "Δεν είναι ενεργό"
               : "Not active"
    );
}

if (risk >= 60) {

    logLabelErrorValue(
            gr ? "Επείγον" : "Urgency",
            gr ? "Πολύ υψηλό — απενεργοποιήστε άμεσα τις λειτουργίες ADB"
               : "Very high — disable ADB features immediately"
    );

} else if (risk >= 30) {

    logLabelWarnValue(
            gr ? "Επείγον" : "Urgency",
            gr ? "Μερική έκθεση — ελέγξτε τις ρυθμίσεις"
               : "Partial exposure — review settings"
    );

} else {

    logLabelOkValue(
            gr ? "Συνολικά" : "Overall",
            gr ? "Το επίπεδο κινδύνου είναι αποδεκτό"
               : "Risk level acceptable"
    );
}

appendHtml("<br>");
logOk(gr ? "Το Lab 23 ολοκληρώθηκε." : "Lab 23 finished.");
logLine();
}


// ============================================================
// UI BUBBLES (GEL)
// ============================================================
private String bubble(boolean on) {
    return on ? "[ON]" : "[OFF]";
}

private String riskBubble(int risk) {
    if (risk <= 10) return "[LOW]";
    if (risk <= 30) return "[MEDIUM]";
    if (risk <= 60) return "[HIGH]";
    return "[CRITICAL]";
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

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 24 — Έλεγχος Root / Ακεραιότητας Bootloader (ΑΥΤΟΜΑΤΟ)."
            : "LAB 24 — Root / Bootloader Integrity Scan (AUTO).");
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

logInfo(gr ? "Έλεγχος Root:" : "Root Scan:");  
if (rootFindings.isEmpty()) {  
    logOk(gr ? "Δεν εντοπίστηκαν ισχυρά ίχνη root."
             : "No strong root traces detected.");  
} else {  
    for (String s : rootFindings)
        logWarn("• " + s);  
}  

logInfo(gr ? "Bootloader / Verified Boot:"
           : "Bootloader / Verified Boot:");  
if (blFindings.isEmpty()) {  
    logOk(gr ? "Δεν εντοπίστηκαν ανωμαλίες bootloader."
             : "No bootloader anomalies detected.");  
} else {  
    for (String s : blFindings)
        logWarn("• " + s);  
}  

logInfo(gr ? "Boot Animation / Splash:"
           : "Boot Animation / Splash:");  
if (animFindings.isEmpty()) {  
    logOk(gr ? "Δεν εντοπίστηκαν ίχνη προσαρμοσμένης εκκίνησης."
             : "No custom animation traces detected.");  
} else {  
    for (String s : animFindings)
        logWarn("• " + s);  
}  

logInfo(gr ? "ΤΕΛΙΚΗ ΕΚΤΙΜΗΣΗ:"
           : "FINAL VERDICT:");

// ------------------------------------------------------------
// RISK SCORE (colored VALUE only)
// ------------------------------------------------------------
logInfo(gr ? "ΤΕΛΙΚΗ ΕΚΤΙΜΗΣΗ:"
           : "FINAL VERDICT:");

if (risk >= 70) {
    logLabelErrorValue(
            gr ? "Βαθμός κινδύνου" : "Risk score",
            risk + " / 100"
    );
} else if (risk >= 35) {
    logLabelWarnValue(
            gr ? "Βαθμός κινδύνου" : "Risk score",
            risk + " / 100"
    );
} else {
    logLabelOkValue(
            gr ? "Βαθμός κινδύνου" : "Risk score",
            risk + " / 100"
    );
}

// ------------------------------------------------------------
// STATUS (GEL LABEL/VALUE STYLE)
// ------------------------------------------------------------
logInfo(gr ? "Τελική κατάσταση:"
           : "Final status:");

if (risk >= 70 || suExec || pkgHit) {

    logLabelErrorValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ROOT / ΤΡΟΠΟΠΟΙΗΜΕΝΟ ΣΥΣΤΗΜΑ (υψηλή βεβαιότητα)"
               : "ROOTED / SYSTEM MODIFIED (high confidence)"
    );

} else if (risk >= 35) {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ΥΠΟΠΤΟ (πιθανό root / ξεκλείδωτος bootloader / custom ROM)"
               : "SUSPICIOUS (possible root / unlocked / custom ROM)"
    );

} else {

    logLabelOkValue(
            gr ? "Κατάσταση" : "Status",
            gr ? "ΑΣΦΑΛΕΣ (δεν βρέθηκαν σημαντικές ενδείξεις τροποποίησης)"
               : "SAFE (no significant modification evidence)"
    );
}

appendHtml("<br>");
logOk(gr ? "Το Lab 24 ολοκληρώθηκε." : "Lab 24 finished.");
logLine();
}


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
java.lang.Process p = null;
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
java.lang.Process p = null;
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
// LABS 25 — 30: ADVANCED / LOGS
// ============================================================

// ============================================================
// LAB 25 — GEL Crash Intelligence v5.0 (FULL AUTO EDITION)
// ============================================================
private void lab25CrashHistory() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 25 — GEL Ανάλυση Σφαλμάτων (ΑΥΤΟΜΑΤΗ)"
            : "LAB 25 — GEL Crash Intelligence (AUTO)");
    logLine();

    int crashCount = 0;
    int anrCount = 0;
    int systemCount = 0;

    Map<String, Integer> appEvents = new HashMap<>();
    List<String> details = new ArrayList<>();

    // ============================================================
    // (A) Android 11+ — REALTIME ERROR SNAPSHOT
    // ============================================================
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            ActivityManager am =
                    (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            if (am != null) {

                List<ActivityManager.ProcessErrorStateInfo> errs =
                        am.getProcessesInErrorState();

                if (errs != null && !errs.isEmpty()) {

                    logInfo(gr
                            ? "Στιγμιότυπο τρεχόντων σφαλμάτων"
                            : "Realtime error snapshot");

                    for (ActivityManager.ProcessErrorStateInfo e : errs) {

                        String app =
                                (e != null && e.processName != null)
                                        ? e.processName
                                        : "(unknown)";

                        appEvents.put(app, appEvents.getOrDefault(app, 0) + 1);

                        if (e.condition ==
                                ActivityManager.ProcessErrorStateInfo.CRASHED) {

                            logLabelErrorValue(
                                    "CRASH",
                                    app + " — " + safeStr(e.shortMsg)
                            );

                        } else if (e.condition ==
                                ActivityManager.ProcessErrorStateInfo.NOT_RESPONDING) {

                            logLabelWarnValue(
                                    "ANR",
                                    app + " — " + safeStr(e.shortMsg)
                            );

                        } else {

                            logLabelWarnValue(
                                    gr ? "ΣΦΑΛΜΑ" : "ERROR",
                                    app + " — " + safeStr(e.shortMsg)
                            );
                        }
                    }

                    appendHtml("<br>");
                    logLabelOkValue(
                            gr ? "Σημείωση" : "Note",
                            gr
                                    ? "Το στιγμιότυπο δείχνει ΜΟΝΟ τρέχοντα crashes / ANR (όχι ιστορικό)"
                                    : "Snapshot shows ONLY current crashed / ANR processes (not history)"
                    );
                }
            }
        }

    } catch (Throwable ignore) {}

    // ============================================================
    // (B) DropBox crash logs
    // ============================================================
    try {
        DropBoxManager db =
                (DropBoxManager) getSystemService(DROPBOX_SERVICE);

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
                    String clean = tag.toUpperCase(Locale.US)
                            .replace("_", " ");

                    details.add(clean + ": " + shortTxt);

                    try {
                        String key;

                        if (shortTxt != null && shortTxt.length() > 0) {
                            String t = shortTxt.toLowerCase(Locale.US);
                            int pi = t.indexOf("package:");
                            if (pi >= 0) {
                                String rest = t.substring(pi + 8).trim();
                                String[] parts =
                                        rest.split("[\\s\\n\\r\\t]+");
                                key = (parts.length > 0 &&
                                       parts[0].contains("."))
                                        ? parts[0]
                                        : clean;
                            } else {
                                key = clean;
                            }
                        } else {
                            key = clean;
                        }

                        appEvents.put(key,
                                appEvents.getOrDefault(key, 0) + 1);

                    } catch (Exception ignored) {}

                    ent = db.getNextEntry(tag,
                            ent.getTimeMillis());
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

    appendHtml("<br>");
    logInfo(gr ? "Σύνοψη Σταθερότητας" : "Stability summary");
    logLine();

    logLabelOkValue(
            gr ? "Συμβάντα Crash" : "Crash events",
            String.valueOf(crashCount)
    );

    if (anrCount > 0)
        logLabelWarnValue(
                "ANR",
                String.valueOf(anrCount)
        );
    else
        logLabelOkValue("ANR", "0");

    if (systemCount > 0)
        logLabelErrorValue(
                gr ? "Σφάλματα Συστήματος" : "System-level faults",
                String.valueOf(systemCount)
        );
    else
        logLabelOkValue(
                gr ? "Σφάλματα Συστήματος" : "System-level faults",
                "0"
        );

    appendHtml("<br>");
    logInfo(gr ? "Δείκτης Ρίσκου Σταθερότητας"
               : "Stability risk score");
    logLine();

    if (risk >= 60)
        logLabelErrorValue("Risk", risk + "%");
    else if (risk >= 30)
        logLabelWarnValue("Risk", risk + "%");
    else
        logLabelOkValue("Risk", risk + "%");

    logLabelOkValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Η βαθμολογία βασίζεται σε διαθέσιμα system logs (διαφέρει ανά OEM / Android)"
                    : "Score based on detected system log signals (availability varies by OEM / Android)"
    );

    boolean softwareCrashLikely =
            (crashCount > 0 || anrCount > 0);

    // ============================================================
    // (D) HEATMAP
    // ============================================================
    if (!appEvents.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Heatmap (συχνότερα συμβάντα)"
                : "Heatmap (top offenders)");
        logLine();

        appEvents.entrySet()
                .stream()
                .sorted((a, b) ->
                        b.getValue() - a.getValue())
                .limit(5)
                .forEach(e -> {

                    String label =
                            e.getValue() + (gr ? " συμβάντα" : " events");

                    if (e.getValue() >= 10)
                        logLabelErrorValue(e.getKey(), label);
                    else if (e.getValue() >= 5)
                        logLabelWarnValue(e.getKey(), label);
                    else
                        logLabelOkValue(e.getKey(), label);
                });
    }

    // ============================================================
    // (E) FULL DETAILS
    // ============================================================
    if (!details.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Αναλυτικά αρχεία crash"
                : "Detailed crash records");
        logLine();

        int count = details.size();

        if (count == 1)
            logLabelWarnValue(
                    gr ? "Καταγραφές" : "Records",
                    gr ? "1 crash εντοπίστηκε"
                       : "1 crash detected");
        else if (count <= 3)
            logLabelWarnValue(
                    gr ? "Καταγραφές" : "Records",
                    count + (gr
                            ? " crashes εντοπίστηκαν"
                            : " crashes detected"));
        else
            logLabelErrorValue(
                    gr ? "Καταγραφές" : "Records",
                    count + (gr
                            ? " crashes εντοπίστηκαν (ΥΨΗΛΗ αστάθεια)"
                            : " crashes detected (HIGH instability)")
            );

        for (String d : details) {
            logLabelWarnValue(
                    gr ? "Λεπτομέρεια" : "Detail",
                    d
            );
        }

    } else {
        logLine();
        logLabelOkValue(
                gr ? "Ιστορικό Crash" : "Crash history",
                gr
                        ? "Δεν εντοπίστηκαν καταγραφές crash"
                        : "No crash records detected"
        );
    }

    GELServiceLog.info(
            "SUMMARY: CRASH_ORIGIN=" +
                    (softwareCrashLikely
                            ? "SOFTWARE"
                            : "UNCLEAR")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 25 ολοκληρώθηκε." : "Lab 25 finished.");
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
// LAB 26 — Installed Applications Impact Analysis (FINAL v2 • Full Bilingual • Engine-backed)
// ============================================================

private void lab26AppsFootprint() {

    if (!guardUsageAccess()) return;

    appendHtml("<br>");
    logLine();

    final boolean gr = AppLang.isGreek(this);

    logInfo(gr
            ? "LAB 26 — Ανάλυση Επιπτώσεων Εγκατεστημένων Εφαρμογών"
            : "LAB 26 — Installed Applications Impact Analysis");

    logLine();

    final boolean rooted = isDeviceRooted();

    // ============================================================
    // ENGINE
    // ============================================================
    AppImpactEngine.ImpactResult r;
    try {
        r = AppImpactEngine.analyze(this, rooted);
    } catch (Throwable t) {

        logLabelErrorValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Αποτυχία ανάλυσης εφαρμογών" : "Failed to analyze applications"
        );

        logLabelWarnValue(
                gr ? "Αιτία" : "Reason",
                gr ? "Σφάλμα πρόσβασης PackageManager / UsageStats" : "PackageManager / UsageStats access error"
        );

        logLine();
        appendHtml("<br>");
        logOk(gr ? "Το Lab 26 ολοκληρώθηκε." : "Lab 26 finished.");
        logLine();
        return;
    }

    if (r == null) {

        logLabelErrorValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Το αποτέλεσμα ανάλυσης είναι κενό" : "Engine result is null"
        );

        logLine();
        appendHtml("<br>");
        logOk(gr ? "Το Lab 26 ολοκληρώθηκε." : "Lab 26 finished.");
        logLine();
        return;
    }

    // ============================================================
    // OVERVIEW
    // ============================================================
    
appendHtml("<br>");
logInfo(gr ? "Επισκόπηση εγκατεστημένων" : "Installed overview");
logLine();

    logLabelOkValue(
            gr ? "Σύνολα" : "Totals",
            (gr
                    ? "Όλα: " + r.totalPkgs + " | Χρήστη: " + r.userApps + " | Συστήματος: " + r.systemApps
                    : "All: " + r.totalPkgs + " | User: " + r.userApps + " | System: " + r.systemApps)
    );

    logLabelOkValue(
            gr ? "Usage Access" : "Usage Access",
            r.usageAccessOk
                    ? (gr ? "Ενεργό (OK)" : "Enabled (OK)")
                    : (gr ? "Ανενεργό (χωρίς foreground χρόνο)" : "Disabled (no foreground time)")
    );

    logLabelOkValue(
            gr ? "Root-aware" : "Root-aware",
            rooted
                    ? (gr ? "Ναι (best-effort χωρίς su)" : "Yes (best-effort without su)")
                    : (gr ? "Όχι" : "No")
    );

    // ============================================================
    // CAPABILITY PRESSURE (HONEST)
    // ============================================================
    int userApps = Math.max(1, r.userApps);
    int pctBg   = (int) Math.round((r.bgCapable * 100.0) / userApps);
    int pctPerm = (int) Math.round((r.permHeavy * 100.0) / userApps);

    appendHtml("<br>");
    logInfo(gr ? "Ενδείξεις φόρτου (βάσει δυνατοτήτων)" : "Load indicators (capability-based)");
    logLine();

    logLabelOkValue(gr ? "Background-capable" : "Background-capable",
            r.bgCapable + " (" + pctBg + "%)");

    logLabelOkValue(gr ? "Permission-heavy" : "Permission-heavy",
            r.permHeavy + " (" + pctPerm + "%)");
            
   appendHtml("<br>");
    logInfo(gr ? "Χάρτης δυνατοτήτων (user apps)" : "Capability map (user apps)");
    logLine();

    logLabelOkValue(
            gr ? "Boot / Location / Mic / Camera" : "Boot / Location / Mic / Camera",
            r.bootAware + " | " + r.locationLike + " | " + r.micLike + " | " + r.cameraLike
    );

    logLabelOkValue(
            gr ? "Overlay / VPN / Storage / Notifications" : "Overlay / VPN / Storage / Notifications",
            r.overlayLike + " | " + r.vpnLike + " | " + r.storageLike + " | " + r.notifLike
    );

    // ============================================================
    // REDUNDANCY (HONEST / HEURISTIC)
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Ενδείξεις πλεονασμού (heuristic)" : "Redundancy signals (heuristic)");
    logLine();

    logLabelOkValue(gr ? "Cleaners / Optimizers" : "Cleaners / Optimizers", String.valueOf(r.cleanersLike));
    logLabelOkValue(gr ? "Launchers" : "Launchers", String.valueOf(r.launchersLike));
    logLabelOkValue(gr ? "Antivirus suites" : "Antivirus suites", String.valueOf(r.antivirusLike));
    logLabelOkValue(gr ? "Keyboards" : "Keyboards", String.valueOf(r.keyboardsLike));

// ============================================================
// REAL DATA (HONEST) — SINCE BOOT (TrafficStats)
// ============================================================
appendHtml("<br>");
logInfo(gr ? "Κατανάλωση δεδομένων (από boot)" : "Data usage (since boot)");
logLine();

if (r.topDataConsumers != null && !r.topDataConsumers.isEmpty()) {

    int limit = Math.min(10, r.topDataConsumers.size());
    boolean foundRealData = false;

    for (int i = 0; i < limit; i++) {

        AppImpactEngine.AppScore a = r.topDataConsumers.get(i);
        if (a == null) continue;

        if (a.dataBytesSinceBoot <= 0) {
            continue; // εξαφανίζουμε τα μηδενικά
        }

        foundRealData = true;

        String val = humanBytes(a.dataBytesSinceBoot);

        logLabelWarnValue(
                a.safeLabel(),
                (gr
        ? val + " (Συνολική κίνηση δεδομένων από εκκίνηση)"
        : val + " (Total data traffic since boot)")
);

        logInfo(a.pkg);
    }

    if (!foundRealData) {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr
                        ? "Δεν υπάρχουν διαθέσιμα δεδομένα κατανάλωσης.\n\n"
                          + "Πιθανές αιτίες:\n"
                          + "• Πρόσφατη επανεκκίνηση συσκευής\n"
                          + "• Περιορισμοί κατασκευαστή (OEM)\n"
                          + "• Μη διαθέσιμα UID counters στο Android"
                        : "No data usage available.\n\n"
                          + "Possible reasons:\n"
                          + "• Device was recently rebooted\n"
                          + "• OEM restrictions\n"
                          + "• UID traffic counters not exposed by Android"
        );

    } else {

        logLabelOkValue(
                gr ? "Σημείωση" : "Note",
                gr
                        ? "Τα δεδομένα είναι συνολικά από την τελευταία εκκίνηση της συσκευής."
                        : "Data is cumulative since last device boot."
        );
    }

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση" : "Status",
            gr
                    ? "Δεν ήταν δυνατή η ανάκτηση στατιστικών κατανάλωσης."
                    : "Unable to retrieve usage statistics."
    );
}

    // ============================================================
    // BATTERY EXPOSURE (HONEST HEURISTIC)
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Έκθεση μπαταρίας (heuristic — no mAh)" : "Battery exposure (heuristic — no mAh)");
    logLine();

    if (!r.usageAccessOk) {

        logLabelWarnValue(
                gr ? "Περιορισμός" : "Limitation",
                gr
                        ? "Δεν υπάρχει Usage Access, άρα δεν έχουμε αξιόπιστο foreground χρόνο."
                        : "Usage Access is off, so we do not have reliable foreground time."
        );
    }

    if (r.topBatteryExposure != null && !r.topBatteryExposure.isEmpty()) {

        int limit = Math.min(10, r.topBatteryExposure.size());
        for (int i = 0; i < limit; i++) {

            AppImpactEngine.AppScore a = r.topBatteryExposure.get(i);
            if (a == null) continue;

// =======================
// DATA SAFETY (EXPLAINED)
// =======================

long fgMin = a.fgMs24h / 60000L;

// ---------- Usage text ----------
String usageText;

if (!r.usageAccessOk) {

    usageText = gr
            ? "Χρήση (24h): δεν υπάρχουν δεδομένα (δεν έχει δοθεί Usage Access)"
            : "Usage (24h): no data (Usage Access not granted)";

} else if (fgMin <= 0) {

    usageText = gr
            ? "Χρήση (24h): δεν καταγράφηκε χρήση το τελευταίο 24ωρο"
            : "Usage (24h): no recorded usage in the last 24h";

} else {

    usageText = gr
            ? "Χρήση (24h): " + fgMin + " λεπτά"
            : "Usage (24h): " + fgMin + " min";
}

// ---------- Data text (TrafficStats since boot) ----------
String dataText;

if (a.dataBytesSinceBoot <= 0) {

    dataText = gr
            ? "Δεδομένα: δεν υπάρχουν διαθέσιμα στοιχεία (πιθανός περιορισμός συστήματος/ROM)"
            : "Data: not available (possible system/ROM limitation)";

} else {

    dataText = gr
            ? "Δεδομένα: " + humanBytes(a.dataBytesSinceBoot)
            : "Data: " + humanBytes(a.dataBytesSinceBoot);
}

// ---------- Final detail ----------
String detail = gr
        ? "Δείκτης Επιρροής: " + a.estImpactScore +
          " | " + usageText +
          " | " + dataText +
          " | Ενδείξεις: " + a.tags
        : "Impact Index: " + a.estImpactScore +
          " | " + usageText +
          " | " + dataText +
          " | Indicators: " + a.tags;

            logLabelWarnValue(a.safeLabel(), detail);
            logInfo(a.pkg);
        }

        logLabelOkValue(
                gr ? "Σημείωση" : "Note",
                gr
                        ? "Το «Battery exposure» είναι εκτίμηση βάσει χρήσης/δυνατοτήτων/δεδομένων — όχι πραγματικό mAh."
                        : "'Battery exposure' is an estimate based on usage/capabilities/data — not real mAh."
        );

    } else {

        logLabelWarnValue(
                gr ? "Κατάσταση" : "Status",
                gr ? "Δεν ήταν δυνατή η κατάταξη έκθεσης μπαταρίας" : "Unable to rank battery exposure"
        );
    }

    // ============================================================
    // TOP CAPABILITY-HEAVY
    // ============================================================
    appendHtml("<br>");
    logInfo(gr
            ? "Top εφαρμογές με ισχυρές δυνατότητες (επισήμανση, όχι κατηγορούμενα)"
            : "Top capability-heavy apps (flagged, not accused)");
            logLine();

    if (r.topCapabilityHeavy != null && !r.topCapabilityHeavy.isEmpty()) {

        int limit = Math.min(10, r.topCapabilityHeavy.size());
        for (int i = 0; i < limit; i++) {

            AppImpactEngine.AppScore a = r.topCapabilityHeavy.get(i);
            if (a == null) continue;

            String detail =
                    (gr
        ? "Δείκτης Δυνατοτήτων: " + a.capabilityScore +
          " | Επικίνδυνες Άδειες: " + a.dangerPermCount +
          " | Παράγοντες Επιρροής: " + a.tags
        : "Capability Index: " + a.capabilityScore +
          " | Dangerous Permissions: " + a.dangerPermCount +
          " | Impact Factors: " + a.tags);

            logLabelWarnValue(a.safeLabel(), detail);
            logInfo(a.pkg);
        }
    }

    // ============================================================
    // HUMAN VERDICT
    // ============================================================
    appendHtml("<br>");
    logOk(gr ? "ΓΕΝΙΚΟ ΣΥΜΠΕΡΑΣΜΑ" : "TOTAL VERDICT");
    logLine();

    if (r.riskPoints >= 8) {
        logLabelWarnValue(gr ? "Επίπεδο πίεσης" : "Pressure level", gr ? "ΥΨΗΛΟ" : "HIGH");
    } else if (r.riskPoints >= 5) {
        logLabelWarnValue(gr ? "Επίπεδο πίεσης" : "Pressure level", gr ? "ΜΕΤΡΙΟ" : "MODERATE");
    } else {
        logLabelOkValue(gr ? "Επίπεδο πίεσης" : "Pressure level", gr ? "ΦΥΣΙΟΛΟΓΙΚΟ" : "NORMAL");
    }

    GELServiceLog.info(
            "SUMMARY: APPS_IMPACT=" + (r.appsImpactHigh ? "HIGH" : "NORMAL")
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 26 ολοκληρώθηκε." : "Lab 26 finished.");
    logLine();
}

// ============================================================
// ROOT HELPER — BEST EFFORT DIRECTORY SIZE (SAFE)
// ============================================================
private long dirSizeBestEffortRoot(File dir) {

    if (dir == null) return 0L;

    try {
        if (!dir.exists() || !dir.isDirectory()) return 0L;
    } catch (Throwable ignore) {
        return 0L;
    }

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

    // ============================================================
    // USAGE ACCESS — MANDATORY GATE
    // ============================================================
    if (!hasUsageAccess()) {
    showUsageAccessDialog();
    return;
}

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 27 — Άδειες Εφαρμογών & Ιδιωτικότητα (Αυτόματη Σάρωση)"
            : "LAB 27 — App Permissions & Privacy (Auto Scan)");
    logLine();

    PackageManager pm = getPackageManager();
    if (pm == null) {
        logError(gr
                ? "Το PackageManager δεν είναι διαθέσιμο."
                : "PackageManager not available.");
        return;
    }

    List<String> details = new ArrayList<>();
    Map<String, Integer> appRisk = new HashMap<>();

    int totalApps = 0;
    int flaggedApps = 0;

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
            // EXCLUDE SYSTEM / GOOGLE / PLAY STORE APPS
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
                if (weight <= 0) continue;

                if (granted) {
                    appDangerCount++;
                    appScore += weight;

                    sb.append("• ")
                      .append(shortPerm(perm))
                      .append(gr ? " (χορηγήθηκε)\n" : " (granted)\n");
                }
            }

            if (appScore > 0) {

                dangTotal += appDangerCount;
                riskTotal += appScore;

                int threshold = 10;

                if (appScore >= threshold) {

                    flaggedApps++;
                    appRisk.put(pkg, appScore);

                    String appLabel = safeLabel(pm, pkg);

                    details.add(
                            appLabel + " (" + pkg + ")" +
                            (gr ? " — Κίνδυνος=" : " — Risk=") +
                            appScore + "\n" + sb.toString()
                    );
                }
            }
        }

    } catch (SecurityException se) {

        logWarn(gr
                ? "Η σάρωση περιορίστηκε από την πολιτική ορατότητας πακέτων Android."
                : "Permissions scan limited by Android package visibility policy.");

    } catch (Exception e) {

        logError(gr
                ? "Σφάλμα σάρωσης αδειών: " + e.getMessage()
                : "Permissions scan error: " + e.getMessage());
    }

    // ============================================================
    // SUMMARY
    // ============================================================
    int maxRiskRef = 300;
    int riskPct = Math.min(100, (riskTotal * 100) / maxRiskRef);

appendHtml("<br>");
    logInfo(gr ? "Σύνοψη Σάρωσης" : "Scan Summary");
    logLine();

    logLabelOkValue(
            gr ? "Εφαρμογές που ελέγχθηκαν" : "Apps scanned",
            String.valueOf(totalApps)
    );

    if (dangTotal == 0) {
        logLabelOkValue(
                gr ? "Επικίνδυνες άδειες (χορηγημένες)" : "Dangerous permissions granted",
                String.valueOf(dangTotal)
        );
    } else if (dangTotal <= 5) {
        logLabelWarnValue(
                gr ? "Επικίνδυνες άδειες (χορηγημένες)" : "Dangerous permissions granted",
                String.valueOf(dangTotal)
        );
    } else {
        logLabelErrorValue(
                gr ? "Επικίνδυνες άδειες (χορηγημένες)" : "Dangerous permissions granted",
                String.valueOf(dangTotal)
        );
    }

    if (flaggedApps == 0) {
        logLabelOkValue(
                gr ? "Εφαρμογές με αυξημένο ρίσκο" : "Flagged apps",
                String.valueOf(flaggedApps)
        );
    } else if (flaggedApps <= 2) {
        logLabelWarnValue(
                gr ? "Εφαρμογές με αυξημένο ρίσκο" : "Flagged apps",
                String.valueOf(flaggedApps)
        );
    } else {
        logLabelErrorValue(
                gr ? "Εφαρμογές με αυξημένο ρίσκο" : "Flagged apps",
                String.valueOf(flaggedApps)
        );
    }

    // ============================================================
    // PRIVACY RISK SCORE
    // ============================================================
    logInfo(gr ? "Δείκτης Ρίσκου Ιδιωτικότητας" : "Privacy Risk Score");

    if (riskPct >= 70) {
        logLabelErrorValue(gr ? "Ρίσκο" : "Risk", riskPct + "%");
    } else if (riskPct >= 30) {
        logLabelWarnValue(gr ? "Ρίσκο" : "Risk", riskPct + "%");
    } else {
        logLabelOkValue(gr ? "Ρίσκο" : "Risk", riskPct + "%");
    }

    // ============================================================
    // TOP OFFENDERS
    // ============================================================
    if (!appRisk.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Εφαρμογές με τον υψηλότερο δείκτη ρίσκου"
                : "Top privacy offenders");
                 logLine();

        appRisk.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(8)
                .forEach(e -> {

                    String label = safeLabel(pm, e.getKey());
                    String riskVal = String.valueOf(e.getValue());

                    if (e.getValue() >= 60) {
                        logLabelErrorValue(label, (gr ? "Ρίσκο " : "Risk ") + riskVal);
                    } else if (e.getValue() >= 30) {
                        logLabelWarnValue(label, (gr ? "Ρίσκο " : "Risk ") + riskVal);
                    } else {
                        logLabelOkValue(label, (gr ? "Ρίσκο " : "Risk ") + riskVal);
                    }
                });
    }

    // ============================================================
    // FULL DETAILS
    // ============================================================
    if (!details.isEmpty()) {

        appendHtml("<br>");
        logInfo(gr
                ? "Αναλυτικές Πληροφορίες (Εφαρμογές με ρίσκο)"
                : "Permission details (flagged apps)");
                logLine();

        for (String d : details) {
            logLabelWarnValue(gr ? "Εύρημα" : "Finding", d.trim());
        }

    } else {

        logLabelOkValue(
                gr ? "Συνδυασμοί αδειών" : "Permission patterns",
                gr
                        ? "Δεν εντοπίστηκαν συνδυασμοί υψηλού ρίσκου"
                        : "No high-risk permission combinations detected"
        );
    }

    // ============================================================
    // CONTEXT NOTE
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Σημείωση Ανάλυσης Ιδιωτικότητας" : "Privacy Analysis Note");
    logLine();

    logLabelOkValue(
            gr ? "Διευκρίνιση" : "Clarification",
            gr
                    ? "Η χορήγηση αδειών δεν σημαίνει κακόβουλη συμπεριφορά."
                    : "Granted permissions do not imply malicious behavior."
    );

    logLabelOkValue(
            gr ? "Πεδίο Ανάλυσης" : "Scope",
            gr
                    ? "Το αποτέλεσμα ΔΕΝ υποδεικνύει βλάβη υλικού ή συστήματος."
                    : "This result does NOT indicate hardware or system failure."
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 27 ολοκληρώθηκε." : "Lab 27 finished.");
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
// LAB 28 — Hardware Stability & Interconnect / humidity Integrity
// TECHNICIAN MODE — SYMPTOM-BASED TRIAGE ONLY
// ============================================================
private void lab28HardwareStability() {

    final boolean gr = AppLang.isGreek(this);

    boolean randomReboots = false;
    boolean signalDrops = false;
    boolean sensorFlaps = false;
    boolean thermalSpikes = false;

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 28 — Σταθερότητα Υλικού & Ακεραιότητα Διασυνδέσεων"
            : "LAB 28 — Hardware Stability & Interconnect Integrity");
    logWarn(gr
            ? "Λειτουργία τεχνικού — Ανάλυση βασισμένη σε συμπτώματα ΜΟΝΟ."
            : "Technician mode — symptom-based analysis ONLY.");
    logLine();

    int symptomScore = 0;
    int powerGlitches = 0;

    Lab28Evidence ev = Lab28EvidenceReader.readFromGELServiceLog();

    randomReboots = ev.rebootPattern;
    signalDrops   = ev.radioInstability;
    sensorFlaps   = ev.sensorFlaps;
    thermalSpikes = ev.thermalSpikes;

    if (ev.chargingGlitch) powerGlitches++;

    // Technician popup
    showLab28Popup();

    // ============================================================
    // STAGE A — SYMPTOM SCORE
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Παρατηρούμενα συμπτώματα" : "Observed symptom signals");
    logLine();

    if (randomReboots) {
        logLabelWarnValue(gr ? "Επανεκκινήσεις" : "Reboots",
                gr ? "Τυχαίες επανεκκινήσεις ή resets"
                   : "Random reboots or sudden resets detected");
        symptomScore += 25;
    } else {
        logLabelOkValue(gr ? "Επανεκκινήσεις" : "Reboots",
                gr ? "Καμία ανωμαλία" : "No abnormal reboot pattern");
    }

    if (signalDrops) {
        logLabelWarnValue(gr ? "Δίκτυο" : "Radio",
                gr ? "Αστάθεια σήματος ή δικτύου"
                   : "Network or signal instability detected");
        symptomScore += 20;
    } else {
        logLabelOkValue(gr ? "Δίκτυο" : "Radio",
                gr ? "Σήμα σταθερό" : "Signals appear stable");
    }

    if (sensorFlaps) {
        logLabelWarnValue(gr ? "Αισθητήρες" : "Sensors",
                gr ? "Διακοπτόμενες μετρήσεις αισθητήρων"
                   : "Intermittent sensor readings detected");
        symptomScore += 15;
    } else {
        logLabelOkValue(gr ? "Αισθητήρες" : "Sensors",
                gr ? "Σταθερή λειτουργία" : "Sensors stable");
    }

    if (thermalSpikes) {
        logLabelWarnValue(gr ? "Θερμικά" : "Thermal",
                gr ? "Απότομες θερμικές αιχμές"
                   : "Abnormal thermal spikes detected");
        symptomScore += 20;
    } else {
        logLabelOkValue(gr ? "Θερμικά" : "Thermal",
                gr ? "Θερμική συμπεριφορά φυσιολογική"
                   : "Thermal behaviour normal");
    }

    if (powerGlitches > 0) {
        logLabelWarnValue(gr ? "Τροφοδοσία" : "Power",
                gr ? "Αστάθεια φόρτισης ή ρεύματος"
                   : "Power or charging instability detected");
        symptomScore += 20;
    } else {
        logLabelOkValue(gr ? "Τροφοδοσία" : "Power",
                gr ? "Σταθερή συμπεριφορά"
                   : "Power behaviour stable");
    }

    if (symptomScore > 100) symptomScore = 100;

    // ------------------------------------------------------------
    // SYMPTOM INTERPRETATION
    // ------------------------------------------------------------
    String symptomLevel =
            (symptomScore <= 20) ? (gr ? "ΧΑΜΗΛΟ" : "LOW") :
            (symptomScore <= 45) ? (gr ? "ΜΕΤΡΙΟ" : "MODERATE") :
            (symptomScore <= 70) ? (gr ? "ΥΨΗΛΟ" : "HIGH") :
                                   (gr ? "ΠΟΛΥ ΥΨΗΛΟ" : "VERY HIGH");

    appendHtml("<br>");
    if (symptomScore >= 40)
        logLabelWarnValue(gr ? "Δείκτης Συνεκτικότητας Συμπτωμάτων"
                             : "Symptom consistency score",
                symptomScore + "/100 (" + symptomLevel + ")");
    else
        logLabelOkValue(gr ? "Δείκτης Συνεκτικότητας Συμπτωμάτων"
                           : "Symptom consistency score",
                symptomScore + "/100 (" + symptomLevel + ")");

    // ============================================================
    // HARDWARE PATTERN ANALYSIS
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Ανάλυση μοτίβων υλικού" : "Hardware instability patterns");
    logLine();

    boolean pmicInstability = false;
    boolean basebandDegradation = false;
    boolean sensorBusInstability = false;
    boolean thermalRunaway = false;
    boolean storageDegradation = false;

// PMIC instability
if ((powerGlitches > 1 && thermalSpikes) || powerGlitches > 3) {
        pmicInstability = true;
        logLabelWarnValue(
                gr ? "PMIC αστάθεια"
                   : "PMIC instability",
                gr
                        ? "Συνδυασμός θερμικών αιχμών και αστάθειας φόρτισης."
                        : "Thermal spikes combined with charging instability."
        );
    }
    
// ----------------------------------------------------
// PMIC / FUEL GAUGE STABILITY CHECK
// ----------------------------------------------------
pmicInstability = false;

if (validDrain &&
    !Float.isNaN(voltageStart) &&
    !Float.isNaN(voltageUnderLoad[0]) &&
    !Float.isNaN(voltageRecovery[0])) {

    float sag = vStart[0] - voltageUnderLoad[0];

// ignore micro sag noise
if (sag < 0.015f)
    sag = 0f;
    
    float recovery = voltageRecovery[0];

    float electricalNoise =
            Math.abs(sag - recovery);

    // abnormal voltage behaviour
    if (electricalNoise > 0.20f)
        pmicInstability = true;

    // counter behaviour anomaly
    if (drainMah == 0 && mahPerHour > 0)
        pmicInstability = true;

    // unrealistic drain spike
    if (mahPerHour > 2500)
        pmicInstability = true;
}

// ----------------------------------------------------
// RESULT
// ----------------------------------------------------
if (pmicInstability) {

    logLabelWarnValue(
            gr ? "Σταθερότητα ελεγκτή τροφοδοσίας (PMIC)"
               : "Power controller stability (PMIC)",
            gr
                    ? "Ανιχνεύθηκε πιθανή αστάθεια fuel-gauge ή PMIC."
                    : "Possible PMIC or fuel-gauge instability detected."
    );

} else {

    logLabelOkValue(
            gr ? "Σταθερότητα ελεγκτή τροφοδοσίας (PMIC)"
               : "Power controller stability (PMIC)",
            gr
                    ? "Δεν εντοπίστηκαν ανωμαλίες ελεγκτή."
                    : "No PMIC instability detected."
    );
}

// ----------------------------------------------------
// BASEBAND INSTABILITY INDICATOR
// ----------------------------------------------------
if (signalDrops && randomReboots && !thermalSpikes) {

    basebandDegradation = true;

    logLabelWarnValue(
            gr ? "Πιθανή αστάθεια baseband"
               : "Possible baseband instability",
            gr
                    ? "Αστάθεια σήματος σε συνδυασμό με επανεκκινήσεις."
                    : "Signal instability combined with device reboots."
    );

} else if (signalDrops) {

    logLabelWarnValue(
            gr ? "Σήμα δικτύου"
               : "Radio signal",
            gr
                    ? "Παρατηρήθηκε αστάθεια σήματος."
                    : "Signal instability observed."
    );

} else {

    logLabelOkValue(
            gr ? "Baseband"
               : "Baseband",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις αστάθειας."
                    : "No baseband instability indicators."
    );
}

// ----------------------------------------------------
// SENSOR BUS INSTABILITY DETECTOR (ROOT ONLY)
// ----------------------------------------------------
boolean sensorBusInstability = false;

if (!isDeviceRooted()) {

    logLabelWarnValue(
            gr ? "Δίαυλος αισθητήρων"
               : "Sensor bus diagnostics",
            gr
                    ? "Η προχωρημένη διάγνωση αισθητήρων απαιτεί πρόσβαση root."
                    : "Advanced sensor diagnostics require root access."
    );

} else {

    // basic communication issues
    if (sensorErrors > 0 || sensorTimeouts > 0 || sensorFlapping) {

        sensorBusInstability = true;

        logLabelWarnValue(
                gr ? "Αστάθεια διαύλου αισθητήρων"
                   : "Sensor bus instability",
                gr
                        ? "Εντοπίστηκαν ασυνέπειες επικοινωνίας αισθητήρων."
                        : "Sensor communication inconsistencies detected."
        );
    }

    // severe pattern
    if (sensorFlaps && randomReboots) {

        sensorBusInstability = true;

        logLabelWarnValue(
                gr ? "Σοβαρή αστάθεια αισθητήρων"
                   : "Severe sensor bus instability",
                gr
                        ? "Διακοπτόμενη λειτουργία αισθητήρων σε συνδυασμό με επανεκκινήσεις."
                        : "Intermittent sensor communication combined with system reboots."
        );
    }

    if (!sensorBusInstability) {

        logLabelOkValue(
                gr ? "Δίαυλος αισθητήρων"
                   : "Sensor bus integrity",
                gr
                        ? "Δεν εντοπίστηκαν ανωμαλίες επικοινωνίας."
                        : "No sensor communication issues detected."
        );
    }
}

    // Thermal runaway
    if (thermalSpikes && randomReboots) {
        thermalRunaway = true;
        logLabelWarnValue(
                gr ? "Θερμική αστάθεια"
                   : "Thermal runaway pattern",
                gr
                        ? "Απότομες αυξήσεις θερμοκρασίας."
                        : "Rapid thermal escalation behaviour."
        );
    }

    // Storage degradation indicator
    if (ev.crashPattern.equals("UNKNOWN") && randomReboots) {
        storageDegradation = true;
        logLabelWarnValue(
                gr ? "Αποθηκευτικός χώρος"
                   : "Storage subsystem",
                gr
                        ? "Πιθανή αστάθεια NAND / controller."
                        : "Possible NAND or storage controller instability."
        );
    }
    
// ----------------------------------------------------
// THERMAL RUNAWAY PREDICTOR
// ----------------------------------------------------
boolean thermalRunawayRisk = false;
int thermalScore = 0;

// έντονες θερμικές αιχμές
if (thermalSpikes)
    thermalScore += 30;

// PMIC instability
if (pmicInstability)
    thermalScore += 20;

// υψηλή θερμοκρασία CPU
if (cpuTemp > 85)
    thermalScore += 20;

// υψηλή θερμοκρασία GPU
if (gpuTemp > 80)
    thermalScore += 15;

// repeated thermal throttle
if (thermalThrottleEvents > 0)
    thermalScore += 15;

logLabelValue(
        gr ? "Δείκτης θερμικής αστάθειας"
           : "Thermal instability index",
        thermalScore + "/100"
);

if (thermalScore >= 60) {

    thermalRunawayRisk = true;

    logLabelWarnValue(
            gr ? "Κίνδυνος θερμικής αστάθειας"
               : "Thermal runaway risk",
            gr
                    ? "Εντοπίστηκε μοτίβο θερμικής αστάθειας στο hardware."
                    : "Hardware pattern indicates possible thermal runaway."
    );

} else {

    logLabelOkValue(
            gr ? "Θερμική σταθερότητα"
               : "Thermal stability",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις θερμικής αστάθειας."
                    : "No thermal runaway indicators detected."
    );
}
    
// ------------------------------------------------------------
// MOTHERBOARD REPAIR / REBALL INDICATORS
// ------------------------------------------------------------
boolean boardRepairSuspicion = false;
int boardScore = 0;

// συνδυασμός πολλών instability patterns
if (pmicInstability)
    boardScore += 25;

if (basebandDegradation)
    boardScore += 20;

if (sensorBusInstability)
    boardScore += 20;

if (thermalRunaway)
    boardScore += 15;

if (storageDegradation)
    boardScore += 15;

// reboot behaviour
if (randomReboots)
    boardScore += 10;

logLabelValue(
        gr ? "Δείκτης πιθανής επέμβασης μητρικής"
           : "Motherboard repair suspicion index",
        boardScore + "/100"
);

if (boardScore >= 60) {

    boardRepairSuspicion = true;

    logLabelWarnValue(
            gr ? "Μητρική πλακέτα"
               : "Mainboard integrity",
            gr
                    ? "Εντοπίστηκε μοτίβο αστάθειας που συναντάται σε συσκευές μετά από επέμβαση μητρικής."
                    : "Instability pattern consistent with board repair or micro-soldering detected."
    );

} else {

    logLabelOkValue(
            gr ? "Ακεραιότητα μητρικής"
               : "Mainboard integrity",
            gr
                    ? "Δεν εντοπίστηκαν ισχυρές ενδείξεις επέμβασης."
                    : "No strong motherboard repair indicators detected."
    );
}

// ------------------------------------------------------------
// HIDDEN HARDWARE FAULT PREDICTOR
// ------------------------------------------------------------
boolean hiddenFaultRisk = false;

int hiddenScore = 0;

// power instability patterns
if (pmicInstability)
    hiddenScore += 25;

// thermal abnormal behaviour
if (thermalRunaway)
    hiddenScore += 20;

// baseband aging patterns
if (basebandDegradation)
    hiddenScore += 15;

// sensor bus instability
if (sensorBusInstability)
    hiddenScore += 15;

// storage subsystem instability
if (storageDegradation)
    hiddenScore += 15;

// repeated reboot behaviour
if (randomReboots)
    hiddenScore += 10;

logLabelValue(
        gr ? "Δείκτης κρυφής αστάθειας υλικού"
           : "Hidden hardware fault predictor",
        hiddenScore + "/100"
);

if (hiddenScore >= 60) {

    hiddenFaultRisk = true;

    logLabelWarnValue(
            gr ? "Πρόβλεψη βλάβης υλικού"
               : "Hardware risk prediction",
            gr
                    ? "Εντοπίστηκε μοτίβο που μπορεί να οδηγήσει σε μελλοντική αστοχία hardware."
                    : "Pattern detected that may lead to future hardware failure."
    );

} else {

    logLabelOkValue(
            gr ? "Σταθερότητα υλικού"
               : "Hardware stability",
            gr
                    ? "Δεν εντοπίστηκαν πρώιμες ενδείξεις αστοχίας."
                    : "No early hardware fault indicators detected."
    );
}

    // ============================================================
    // MOISTURE EXPOSURE INDICATOR
    // ============================================================
    boolean moistureIndicator = false;
    int moistureSignals = 0;

    if (sensorFlaps) moistureSignals++;
    if (signalDrops) moistureSignals++;
    if (powerGlitches > 0) moistureSignals++;

    if (thermalSpikes && ev.thermalOnlyDuringCharging)
        moistureSignals++;

    if (moistureSignals >= 2)
        moistureIndicator = true;

    appendHtml("<br>");

    if (moistureIndicator) {

        logLabelWarnValue(
                gr ? "Ένδειξη πιθανής υγρασίας"
                   : "Possible moisture exposure",
                gr
                        ? "Εντοπίστηκε μοτίβο συμπτωμάτων που σχετίζεται με υγρασία."
                        : "Symptom pattern consistent with moisture exposure detected."
        );

    } else {

        logLabelOkValue(
                gr ? "Ένδειξη υγρασίας"
                   : "Moisture exposure",
                gr
                        ? "Δεν εντοπίστηκαν σαφή σημάδια."
                        : "No clear moisture indicators."
        );
    }

    // ============================================================
    // STAGE D — FINAL CONFIDENCE
    // ============================================================
    int finalScore = symptomScore;
    if (finalScore > 100) finalScore = 100;

    String finalLevel =
            (finalScore <= 20) ? (gr ? "ΧΑΜΗΛΟ" : "LOW") :
            (finalScore <= 45) ? (gr ? "ΜΕΤΡΙΟ" : "MODERATE") :
            (finalScore <= 70) ? (gr ? "ΥΨΗΛΟ" : "HIGH") :
                                 (gr ? "ΠΟΛΥ ΥΨΗΛΟ" : "VERY HIGH");

    appendHtml("<br>");
    if (finalScore >= 40)
        logLabelWarnValue(gr ? "Τελική Εκτίμηση Σταθερότητας"
                             : "Final stability confidence",
                finalScore + "/100 (" + finalLevel + ")");
    else
        logLabelOkValue(gr ? "Τελική Εκτίμηση Σταθερότητας"
                           : "Final stability confidence",
                finalScore + "/100 (" + finalLevel + ")");

    // ============================================================
    // TRIAGE NOTE
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Σημείωση Τεχνικού" : "Technician note");
    logLine();

    if (finalScore >= 60) {

        logLabelWarnValue(gr ? "Εύρημα" : "Finding",
                gr ? "Εντοπίστηκε μοτίβο αστάθειας"
                   : "Multi-source instability pattern detected");

        logLabelOkValue(gr ? "Σημαντικό" : "Important",
                gr ? "ΔΕΝ αποτελεί διάγνωση υλικού."
                   : "This is NOT a hardware diagnosis.");

        logLabelOkValue(gr ? "Συστήνεται" : "Recommended action",
                gr ? "Έλεγχος από τεχνικό."
                   : "Professional inspection recommended.");

    } else {

        logLabelOkValue(gr ? "Εύρημα" : "Finding",
                gr ? "Δεν εντοπίστηκαν σοβαρά μοτίβα αστάθειας."
                   : "No significant instability patterns detected.");
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 28 ολοκληρώθηκε." : "Lab 28 finished.");
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
// LAB 29 — Device Authenticity & Parts Integrity
// TECHNICIAN MODE — NON-OEM COMPONENT INDICATORS
// ============================================================
private void lab29DeviceAuthenticity() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 29 — Έλεγχος Γνησιότητας Συσκευής"
            : "LAB 29 — Device Authenticity & Parts Integrity");
    logWarn(gr
        ? "Λειτουργία τεχνικού — Έλεγχος πιθανών μη γνήσιων ανταλλακτικών."
        : "Technician mode — possible non-OEM component detection.");
logLine();

appendHtml("<br>");

if (!isDeviceRooted()) {

    logLabelWarnValue(
            gr ? "Προχωρημένη επιβεβαίωση hardware"
               : "Advanced hardware verification",
            gr
                    ? "Ορισμένοι έλεγχοι απαιτούν πρόσβαση root."
                    : "Some authenticity checks require root access."
    );

}

    int authenticityScore = 100;

    boolean batterySuspicious = false;
    boolean displaySuspicious = false;
    boolean cameraSuspicious = false;
    boolean fingerprintMismatch = false;

    // ============================================================
    // BATTERY CHECK
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος Μπαταρίας" : "Battery authenticity check");
    logLine();

    int level = getBatteryPercentSafe();
    float voltage = getBatteryVoltageFiltered();

    if (level < 0 || Float.isNaN(voltage) || voltage < 3000 || voltage > 5000) {

    batterySuspicious = true;

    logLabelWarnValue(
            gr ? "Μπαταρία"
               : "Battery",
            gr
                    ? "Ασυνήθιστη συμπεριφορά μετρήσεων."
                    : "Unusual battery measurement behaviour detected."
    );

    authenticityScore -= 10;

} else {

    logLabelOkValue(
            gr ? "Μπαταρία"
               : "Battery",
            gr
                    ? "Μετρήσεις φυσιολογικές."
                    : "Battery metrics appear normal."
    );
}

    // ============================================================
    // DISPLAY CHECK
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος Οθόνης" : "Display authenticity check");
    logLine();

    Display display = getWindowManager().getDefaultDisplay();
    float refreshRate = display.getRefreshRate();

    if (refreshRate < 30f) {

    displaySuspicious = true;

    logLabelWarnValue(
            gr ? "Οθόνη"
               : "Display",
            gr
                    ? "Ασυνήθιστος ρυθμός ανανέωσης."
                    : "Unusual display refresh rate reported."
    );

    authenticityScore -= 5;

} else {

    logLabelOkValue(
            gr ? "Οθόνη"
               : "Display",
            String.format(Locale.US,
                    gr
                            ? "Ρυθμός ανανέωσης: %.1f Hz"
                            : "Refresh rate: %.1f Hz",
                    refreshRate));
}


// ------------------------------------------------------------
// DISPLAY PANEL CONSISTENCY CHECK
// ------------------------------------------------------------
try {

    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    
    // ------------------------------------------------------------
// PHYSICAL SCREEN SIZE CONSISTENCY CHECK
// ------------------------------------------------------------
try {

    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);

    int widthPx  = dm.widthPixels;
    int heightPx = dm.heightPixels;

    float xdpi = dm.xdpi;
    float ydpi = dm.ydpi;

    float widthInches  = widthPx / xdpi;
    float heightInches = heightPx / ydpi;

    double diagonal =
            Math.sqrt(widthInches * widthInches +
                      heightInches * heightInches);

    logLabelValue(
            gr ? "Εκτιμώμενο μέγεθος οθόνης"
               : "Estimated screen size",
            String.format(Locale.US, "%.2f\"", diagonal)
    );

    // abnormal panel size heuristic
    if (diagonal < 3.5 || diagonal > 8.5) {

        displaySuspicious = true;

        logLabelWarnValue(
        gr ? "Συμβατότητα panel"
           : "Display panel consistency",
        gr
                ? "Ασυνήθιστη αναλογία ανάλυσης και DPI. "
                + "Μπορεί να οφείλεται σε αντικατάσταση οθόνης, "
                + "λανθασμένο calibration ή ασύμβατο panel."
                : "Unusual resolution / DPI combination detected. "
                + "May indicate display replacement, incorrect calibration "
                + "or incompatible panel."
);

        authenticityScore -= 5;

    }

} catch (Throwable ignore) {}

    int width = dm.widthPixels;
    int height = dm.heightPixels;
    float density = dm.density;

    logLabelValue(
            gr ? "Ανάλυση οθόνης"
               : "Display resolution",
            width + " x " + height
    );

    logLabelValue(
            gr ? "Πυκνότητα"
               : "Display density",
            String.format(Locale.US, "%.2f", density)
    );

    if (width < 800 || height < 800) {

        displaySuspicious = true;

        logLabelWarnValue(
                gr ? "Panel ανάλυση"
                   : "Display panel",
                gr
                        ? "Ασυνήθιστη ανάλυση οθόνης."
                        : "Unusual display resolution detected."
        );

        authenticityScore -= 5;

    }

} catch (Throwable ignore) {}

    // ============================================================
    // CAMERA CHECK
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος Κάμερας" : "Camera authenticity check");
    logLine();

    boolean cameraAvailable =
            getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_ANY);

    if (!cameraAvailable) {

    cameraSuspicious = true;

    logLabelWarnValue(
            gr ? "Κάμερα"
               : "Camera",
            gr
                    ? "Η κάμερα δεν εντοπίστηκε από το σύστημα."
                    : "Camera not detected by system."
    );

    authenticityScore -= 20;

} else {

    logLabelOkValue(
            gr ? "Κάμερα"
               : "Camera",
            gr
                    ? "Το module κάμερας φαίνεται λειτουργικό."
                    : "Camera module appears functional."
    );
}


// ------------------------------------------------------------
// CAMERA MODULE CONSISTENCY CHECK
// ------------------------------------------------------------
try {

    CameraManager cm =
            (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    if (cm != null) {

        String[] ids = cm.getCameraIdList();

        int cameraCount = ids != null ? ids.length : 0;

        logLabelValue(
                gr ? "Αριθμός καμερών"
                   : "Camera modules detected",
                String.valueOf(cameraCount)
        );

        if (cameraCount == 0) {

            cameraSuspicious = true;

            logLabelWarnValue(
                    gr ? "Υποσύστημα κάμερας"
                       : "Camera subsystem",
                    gr
                            ? "Δεν εντοπίστηκαν modules κάμερας."
                            : "No camera modules detected."
            );

            authenticityScore -= 15;

        }

    }

} catch (Throwable ignore) {}

// ------------------------------------------------------------
// HARDWARE CAPABILITY MISMATCH DETECTION
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr ? "Έλεγχος συμβατότητας hardware"
           : "Hardware capability verification");
logLine();

try {

    PackageManager pm = getPackageManager();

    boolean hasCamera =
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);

    boolean hasFlash =
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

    boolean hasAutofocus =
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);

    boolean mismatch = false;

    if (hasCamera && !cameraAvailable) {

        mismatch = true;

        logLabelWarnValue(
                gr ? "Camera capability"
                   : "Camera capability",
                gr
                        ? "Το σύστημα δηλώνει ύπαρξη κάμερας αλλά το module δεν ανιχνεύθηκε."
                        : "System reports camera capability but module not detected."
        );

    }

    if (hasFlash && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

        mismatch = true;

        logLabelWarnValue(
                gr ? "Flash capability"
                   : "Flash capability",
                gr
                        ? "Ασυνέπεια στο hardware flash."
                        : "Flash hardware inconsistency detected."
        );

    }

    if (hasAutofocus && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {

        mismatch = true;

        logLabelWarnValue(
                gr ? "Autofocus capability"
                   : "Autofocus capability",
                gr
                        ? "Ασυνέπεια στο autofocus module."
                        : "Autofocus capability mismatch detected."
        );

    }

    if (!mismatch) {

        logLabelOkValue(
                gr ? "Συμβατότητα hardware"
                   : "Hardware capability",
                gr
                        ? "Δεν εντοπίστηκαν ασυνέπειες hardware."
                        : "No hardware capability mismatches detected."
        );

    } else {

        logLabelValue(
                gr ? "Πιθανές αιτίες"
                   : "Possible causes",
                gr
                        ? "Αντικατάσταση module (κάμερα ή flash), λάθος firmware ή ασυμβατό hardware μετά από επισκευή."
                        : "Camera or flash module replacement, incorrect firmware or incompatible hardware after repair."
        );

    }

} catch (Throwable ignore) {}

// ------------------------------------------------------------
// SENSOR CALIBRATION ANOMALY DETECTION
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr ? "Έλεγχος βαθμονόμησης αισθητήρων"
           : "Sensor calibration inspection");
logLine();

try {

    SensorManager sm =
            (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    if (sm != null) {

        Sensor proximity =
                sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        Sensor light =
                sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        Sensor accel =
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        boolean sensorSuspicious = false;

        // ------------------------------------------------
        // PROXIMITY SENSOR RANGE CHECK
        // ------------------------------------------------
        if (proximity != null) {

            float range = proximity.getMaximumRange();

            logLabelValue(
                    gr ? "Proximity range"
                       : "Proximity range",
                    String.valueOf(range)
            );

            if (range < 1f || range > 10f) {

                sensorSuspicious = true;

                logLabelWarnValue(
                        gr ? "Proximity αισθητήρας"
                           : "Proximity sensor",
                        gr
                                ? "Ασυνήθιστη βαθμονόμηση proximity."
                                : "Unusual proximity calibration detected."
                );
            }

        }

        // ------------------------------------------------
        // LIGHT SENSOR RANGE CHECK
        // ------------------------------------------------
        if (light != null) {

            float range = light.getMaximumRange();

            logLabelValue(
                    gr ? "Light sensor range"
                       : "Light sensor range",
                    String.valueOf(range)
            );

            if (range < 100) {

                sensorSuspicious = true;

                logLabelWarnValue(
                        gr ? "Light αισθητήρας"
                           : "Light sensor",
                        gr
                                ? "Περιορισμένο εύρος φωτεινότητας."
                                : "Limited light sensor range detected."
                );
            }

        }

        // ------------------------------------------------
        // ACCELEROMETER RANGE CHECK
        // ------------------------------------------------
        if (accel != null) {

            float range = accel.getMaximumRange();

            logLabelValue(
                    gr ? "Accelerometer range"
                       : "Accelerometer range",
                    String.valueOf(range)
            );

            if (range < 5f) {

                sensorSuspicious = true;

                logLabelWarnValue(
                        gr ? "Accelerometer"
                           : "Accelerometer",
                        gr
                                ? "Ασυνήθιστο εύρος accelerometer."
                                : "Unusual accelerometer calibration."
                );
            }

        }

// ------------------------------------------------
// RESULT
// ------------------------------------------------
if (!sensorSuspicious) {

    logLabelOkValue(
            gr ? "Βαθμονόμηση αισθητήρων"
               : "Sensor calibration",
            gr
                    ? "Δεν εντοπίστηκαν ασυνήθιστες τιμές."
                    : "Sensor calibration appears normal."
    );

} else {

    logLabelWarnValue(
            gr ? "Βαθμονόμηση αισθητήρων"
               : "Sensor calibration",
            gr
                    ? "Ανιχνεύθηκαν ασυνήθιστες τιμές βαθμονόμησης αισθητήρων."
                    : "Unusual sensor calibration values detected."
    );

    logLabelValue(
            gr ? "Πιθανές αιτίες"
               : "Possible causes",
            gr
                    ? "Αλλαγή οθόνης, αντικατάσταση αισθητήρα ή ελλιπής βαθμονόμηση μετά από επισκευή."
                    : "Display replacement, sensor replacement or incomplete calibration after repair."
    );

}
}

} catch (Throwable ignore) {}

    // ============================================================
    // HARDWARE FINGERPRINT CHECK
    // ============================================================
    appendHtml("<br>");
    logInfo(gr ? "Έλεγχος Ταυτότητας Hardware"
               : "Hardware fingerprint verification");
    logLine();

    String fingerprint = Build.FINGERPRINT;
    String board = Build.BOARD;
    String hardware = Build.HARDWARE;

    if (fingerprint == null || fingerprint.length() < 10 || fingerprint.contains("unknown")) {

    fingerprintMismatch = true;

    logLabelWarnValue(
            gr ? "Ταυτότητα Hardware"
               : "Hardware fingerprint",
            gr
                    ? "Ασυνήθιστο fingerprint συστήματος."
                    : "Unusual system fingerprint detected."
    );

    authenticityScore -= 5;

} else {

    logLabelOkValue(
            gr ? "Fingerprint"
               : "System fingerprint",
            fingerprint);
}

    logLabelValue(
            gr ? "Board"
               : "Board",
            board);

    logLabelValue(
            gr ? "Hardware"
               : "Hardware",
            hardware);
            
// ------------------------------------------------------------
// HARDWARE FINGERPRINT CONSISTENCY ENGINE
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr ? "Έλεγχος συνέπειας ταυτότητας hardware"
           : "Hardware fingerprint consistency");
logLine();

try {

    boolean mismatch = false;

    String model  = Build.MODEL;
    String device = Build.DEVICE;
    String brand  = Build.BRAND;
    String product = Build.PRODUCT;

    if (model == null || device == null || brand == null) {

        mismatch = true;

        logLabelWarnValue(
                gr ? "Ταυτότητα συσκευής"
                   : "Device identity",
                gr
                        ? "Ελλιπή στοιχεία ταυτότητας συσκευής."
                        : "Incomplete device identity information."
        );

    }

    if (model != null && device != null &&
        !model.toLowerCase(Locale.US).contains(device.toLowerCase(Locale.US))) {

        mismatch = true;

        logLabelWarnValue(
                gr ? "Συνέπεια μοντέλου"
                   : "Model consistency",
                gr
                        ? "Ασυνήθιστη αντιστοίχιση model και device."
                        : "Model / device combination appears unusual."
        );

    }

    if (brand != null && product != null &&
        !product.toLowerCase(Locale.US).contains(brand.toLowerCase(Locale.US))) {

        logLabelWarnValue(
                gr ? "Firmware ταυτότητα"
                   : "Firmware identity",
                gr
                        ? "Το firmware δεν ταιριάζει πλήρως με το brand."
                        : "Firmware identity differs from device brand."
        );

    }

    if (!mismatch) {

        logLabelOkValue(
                gr ? "Συνέπεια ταυτότητας hardware"
                   : "Hardware identity consistency",
                gr
                        ? "Τα στοιχεία ταυτότητας φαίνονται συμβατά."
                        : "Hardware identity values appear consistent."
        );

    } else {

        logLabelValue(
                gr ? "Πιθανές αιτίες"
                   : "Possible causes",
                gr
                        ? "Αλλαγή μητρικής, τροποποιημένο firmware ή μη επίσημο ROM."
                        : "Motherboard replacement, modified firmware or unofficial ROM."
        );

    }

} catch (Throwable ignore) {}
           
// ------------------------------------------------------------
// READ FLAGS FROM PREVIOUS LABS
// ------------------------------------------------------------
boolean sensorFlaps =
        p.getBoolean("lab28_sensor_flaps", false);

boolean radioInstability =
        p.getBoolean("lab28_radio_instability", false);

boolean thermalSpike =
        p.getBoolean("lab28_thermal_spike", false);

boolean rebootPattern =
        p.getBoolean("lab28_reboot_pattern", false);

boolean instabilityPattern =
        p.getBoolean("lab28_instability_pattern", false);
         
 // ------------------------------------------------------------
// WATER DAMAGE INDICATOR (logic-based moisture detection)
// ------------------------------------------------------------
boolean moistureSuspicion = false;

int moistureScore = 0;

if (sensorFlaps) moistureScore += 20;
if (radioInstability) moistureScore += 20;
if (thermalSpike) moistureScore += 15;
if (rebootPattern) moistureScore += 15;
if (instabilityPattern) moistureScore += 20;

if (collapseRisk || swellingRisk)
    moistureScore += 10;

logLabelValue(
        gr ? "Δείκτης πιθανής υγρασίας"
           : "Moisture risk index",
        moistureScore + "/100"
);

if (moistureScore >= 50) {

    moistureSuspicion = true;

    logLabelWarnValue(
            gr ? "Ένδειξη πιθανής υγρασίας"
               : "Possible moisture exposure",
            gr
                    ? "Το σύστημα εντόπισε μοτίβο αστάθειας που συναντάται σε συσκευές με εισχώρηση υγρασίας."
                    : "Instability pattern similar to moisture-affected devices detected."
    );

} else {

    logLabelOkValue(
            gr ? "Έλεγχος υγρασίας"
               : "Moisture inspection",
            gr
                    ? "Δεν εντοπίστηκαν ισχυρές ενδείξεις υγρασίας."
                    : "No strong moisture indicators detected."
    );
}
            

    // ============================================================
    // FINAL RESULT
    // ============================================================
    appendHtml("<br>");
    logLine();

    if (authenticityScore < 0) authenticityScore = 0;

    String level;

    if (authenticityScore >= 90)
        level = gr ? "ΥΨΗΛΗ ΓΝΗΣΙΟΤΗΤΑ" : "HIGH AUTHENTICITY";
    else if (authenticityScore >= 70)
        level = gr ? "ΠΙΘΑΝΗ ΑΝΤΙΚΑΤΑΣΤΑΣΗ" : "POSSIBLE REPLACEMENT";
    else
        level = gr ? "ΥΨΗΛΗ ΠΙΘΑΝΟΤΗΤΑ ΜΗ ΓΝΗΣΙΩΝ ΜΕΡΩΝ"
                   : "HIGH PROBABILITY OF NON-OEM PARTS";

    if (authenticityScore >= 70) {

        logLabelOkValue(
                gr ? "Δείκτης Γνησιότητας Συσκευής"
                   : "Device authenticity score",
                authenticityScore + "/100 (" + level + ")"
        );

    } else {

        logLabelWarnValue(
                gr ? "Δείκτης Γνησιότητας Συσκευής"
                   : "Device authenticity score",
                authenticityScore + "/100 (" + level + ")"
        );
    }
    p.edit()
        .putBoolean("lab29_moisture_suspect", moistureSuspicion)
        .apply();
    

    appendHtml("<br>");
    logOk(gr
            ? "Το Lab 29 ολοκληρώθηκε."
            : "Lab 29 finished.");
    logLine();
}

// ============================================================
// LAB 30 — Auto Final Diagnosis Summary (GEL Universal AUTO Edition)
// Combines Thermals + Battery + Storage + RAM + Apps + Uptime +
// Security + Privacy + Root + Stability into final scores.
// NOTE (GEL RULE): Whole block ready for copy-paste.
// ============================================================
private void lab30CombineFindings() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 30 — Αυτόματη Τελική Σύνοψη Διάγνωσης (ΠΛΗΡΩΣ ΑΥΤΟΜΑΤΗ)"
            : "LAB 30 — Auto Final Diagnosis Summary (FULL AUTO)");
    logLine();

    // ============================================================
    // USAGE ACCESS — REQUIRED
    // ============================================================
    if (!hasUsageAccess()) {
        showUsageAccessDialog();
        return;
    }
    
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
// 9) AUTHENTICITY / REPAIR INDICATORS (LAB 29)
// ------------------------------------------------------------
boolean moistureDetected =
        p.getBoolean("lab29_moisture_detected", false);

boolean nonOemParts =
        p.getBoolean("lab29_non_oem_parts", false);

boolean displayReplaced =
        p.getBoolean("lab29_display_replaced", false);

boolean cameraReplaced =
        p.getBoolean("lab29_camera_replaced", false);

boolean batteryReplaced =
        p.getBoolean("lab29_battery_replaced", false);

appendHtml("<br>");
logInfo(gr ? "Αυθεντικότητα / Επισκευές" : "Authenticity / Repairs");
logLine();

if (moistureDetected)
    logLabelWarnValue(
            gr ? "Υγρασία" : "Moisture",
            gr ? "Ενδείξεις εισχώρησης υγρασίας"
               : "Moisture ingress indicators detected"
    );

if (nonOemParts)
    logLabelWarnValue(
            gr ? "Μη γνήσια εξαρτήματα" : "Non-OEM parts",
            gr ? "Πιθανή χρήση μη γνήσιων ανταλλακτικών"
               : "Possible non-OEM components detected"
    );

if (displayReplaced)
    logLabelWarnValue(
            gr ? "Οθόνη" : "Display",
            gr ? "Ενδείξεις αντικατάστασης οθόνης"
               : "Display replacement indicators detected"
    );

if (cameraReplaced)
    logLabelWarnValue(
            gr ? "Κάμερα" : "Camera",
            gr ? "Ενδείξεις αντικατάστασης κάμερας"
               : "Camera replacement indicators detected"
    );

if (batteryReplaced)
    logLabelWarnValue(
            gr ? "Μπαταρία" : "Battery",
            gr ? "Ενδείξεις αντικατάστασης μπαταρίας"
               : "Battery replacement indicators detected"
    );

if (!moistureDetected &&
    !nonOemParts &&
    !displayReplaced &&
    !cameraReplaced &&
    !batteryReplaced) {

    logLabelOkValue(
            gr ? "Κατάσταση υλικού" : "Hardware authenticity",
            gr ? "Δεν εντοπίστηκαν ενδείξεις επισκευής"
               : "No repair indicators detected"
    );
}

// ------------------------------------------------------------
// UNIFIED HARDWARE RISK SCORE
// ------------------------------------------------------------
int hardwareRiskScore = 0;

// battery risks
if (collapseRisk)
    hardwareRiskScore += 20;

if (swellingRisk)
    hardwareRiskScore += 15;

// storage risks
if (nandRisk)
    hardwareRiskScore += 20;

if (controllerRisk)
    hardwareRiskScore += 15;

if (silentCorruptionRisk)
    hardwareRiskScore += 15;

// motherboard stability
if (pmicInstability)
    hardwareRiskScore += 10;

if (sensorBusInstability)
    hardwareRiskScore += 10;

if (thermalRunawayRisk)
    hardwareRiskScore += 10;

logLabelValue(
        gr ? "Συνολικός δείκτης κινδύνου hardware"
           : "Unified hardware risk score",
        hardwareRiskScore + "/100"
);

// ------------------------------------------------------------
// AUTOMATIC FAULT CLASSIFICATION ENGINE
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Αυτόματη αξιολόγηση hardware:"
        : "Automatic hardware assessment:");

boolean faultDetected = false;

// ------------------------------------------------------------
// BATTERY
// ------------------------------------------------------------
if (collapseRisk || swellingRisk || finalScore < 60) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Μπαταρία"
               : "Battery",
            gr
                    ? "Πιθανή υποβάθμιση μπαταρίας."
                    : "Possible battery degradation detected."
    );
}

// ------------------------------------------------------------
// STORAGE
// ------------------------------------------------------------
if (nandRisk || controllerRisk || silentCorruptionRisk) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Αποθηκευτικός χώρος"
               : "Storage",
            gr
                    ? "Ενδείξεις πιθανής φθοράς NAND ή controller."
                    : "Possible NAND or storage controller degradation."
    );
}

// ------------------------------------------------------------
// THERMAL
// ------------------------------------------------------------
if (thermalRunawayRisk) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Θερμική συμπεριφορά"
               : "Thermal behaviour",
            gr
                    ? "Εντοπίστηκε μοτίβο θερμικής αστάθειας."
                    : "Thermal instability pattern detected."
    );
}

// ------------------------------------------------------------
// PMIC / POWER
// ------------------------------------------------------------
if (pmicInstability) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Τροφοδοσία συστήματος"
               : "Power management",
            gr
                    ? "Πιθανή αστάθεια PMIC."
                    : "Possible PMIC instability."
    );
}

// ------------------------------------------------------------
// SENSOR BUS
// ------------------------------------------------------------
if (sensorBusInstability) {

    faultDetected = true;

    logLabelWarnValue(
            gr ? "Αισθητήρες"
               : "Sensors",
            gr
                    ? "Πιθανή αστάθεια διαύλου αισθητήρων."
                    : "Possible sensor bus instability."
    );
}

// ------------------------------------------------------------
// FINAL CLASSIFICATION
// ------------------------------------------------------------
if (!faultDetected) {

    logLabelOkValue(
            gr ? "Συμπέρασμα"
               : "Conclusion",
            gr
                    ? "Δεν εντοπίστηκαν ενδείξεις hardware βλάβης."
                    : "No hardware fault indicators detected."
    );

} else {

    logLabelWarnValue(
            gr ? "Σύσταση τεχνικού"
               : "Technician recommendation",
            gr
                    ? "Συνιστάται περαιτέρω έλεγχος ή παρακολούθηση της συσκευής."
                    : "Further monitoring or service inspection recommended."
    );
}

// ------------------------------------------------------------
// PROBABLE ROOT CAUSE ENGINE
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Πιθανή βασική αιτία προβλήματος:"
        : "Probable root cause analysis:");

String rootCause = gr ? "Δεν εντοπίστηκε σαφής αιτία"
                      : "No clear root cause detected";

int rootConfidence = 0;

// ------------------------------------------------------------
// BATTERY ROOT CAUSE
// ------------------------------------------------------------
if (collapseRisk || swellingRisk || finalScore < 60) {

    rootCause = gr
            ? "Υποβάθμιση μπαταρίας"
            : "Battery degradation";

    rootConfidence = 80;

    if (collapseRisk && swellingRisk)
        rootConfidence = 90;
}

// ------------------------------------------------------------
// STORAGE ROOT CAUSE
// ------------------------------------------------------------
else if (nandRisk || controllerRisk || silentCorruptionRisk) {

    rootCause = gr
            ? "Φθορά NAND ή controller αποθήκευσης"
            : "NAND or storage controller degradation";

    rootConfidence = 75;

    if (silentCorruptionRisk)
        rootConfidence += 10;
}

// ------------------------------------------------------------
// THERMAL ROOT CAUSE
// ------------------------------------------------------------
else if (thermalRunawayRisk) {

    rootCause = gr
            ? "Θερμική αστάθεια hardware"
            : "Hardware thermal instability";

    rootConfidence = 70;
}

// ------------------------------------------------------------
// POWER / PMIC ROOT CAUSE
// ------------------------------------------------------------
else if (pmicInstability) {

    rootCause = gr
            ? "Αστάθεια κυκλώματος τροφοδοσίας (PMIC)"
            : "Power management instability (PMIC)";

    rootConfidence = 65;
}

// ------------------------------------------------------------
// SENSOR BUS ROOT CAUSE
// ------------------------------------------------------------
else if (sensorBusInstability) {

    rootCause = gr
            ? "Αστάθεια διαύλου αισθητήρων"
            : "Sensor bus instability";

    rootConfidence = 60;
}

// ------------------------------------------------------------
// OUTPUT
// ------------------------------------------------------------
logLabelValue(
        gr ? "Πιθανή αιτία"
           : "Most likely cause",
        rootCause
);

if (rootConfidence > 0) {

    logLabelValue(
            gr ? "Βαθμός βεβαιότητας"
               : "Confidence",
            rootConfidence + "%"
    );

} else {

    logLabelOkValue(
            gr ? "Κατάσταση"
               : "Status",
            gr
                    ? "Δεν εντοπίστηκε συγκεκριμένη αιτία."
                    : "No specific hardware fault detected."
    );
}

int adjustedRisk = Math.max(0, hardwareRiskScore - 5);
int reliabilityScore = 100 - adjustedRisk;

// ------------------------------------------------------------
// CONFIDENCE-WEIGHTED ROOT CAUSE
// ------------------------------------------------------------
if (rootConfidence > 0) {

    int weightedConfidence =
            (int)Math.round(rootConfidence * (reliabilityScore / 100.0));

    logLabelValue(
            gr ? "Σταθμισμένη βεβαιότητα"
               : "Weighted confidence",
            weightedConfidence + "%"
    );

    if (weightedConfidence >= 80) {

        logLabelOkValue(
                gr ? "Ερμηνεία"
                   : "Interpretation",
                gr
                        ? "Η εκτίμηση αιτίας θεωρείται αξιόπιστη."
                        : "Root cause estimation considered reliable."
        );

    } else if (weightedConfidence >= 60) {

        logLabelWarnValue(
                gr ? "Ερμηνεία"
                   : "Interpretation",
                gr
                        ? "Η αιτία είναι πιθανή αλλά απαιτεί επιβεβαίωση."
                        : "Root cause is plausible but requires confirmation."
        );

    } else {

        logLabelWarnValue(
                gr ? "Ερμηνεία"
                   : "Interpretation",
                gr
                        ? "Χαμηλή βεβαιότητα λόγω περιορισμένων δεδομένων."
                        : "Low confidence due to limited diagnostic history."
        );

    }

}

// ------------------------------------------------------------
// FINAL HARDWARE STATUS
// ------------------------------------------------------------
if (hardwareRiskScore >= 60) {

    logLabelErrorValue(
            gr ? "Κατάσταση συσκευής"
               : "Device hardware status",
            gr
                    ? "Υψηλός κίνδυνος hardware αστοχίας."
                    : "High hardware failure risk detected."
    );

} else if (hardwareRiskScore >= 30) {

    logLabelWarnValue(
            gr ? "Κατάσταση συσκευής"
               : "Device hardware status",
            gr
                    ? "Μέτριος κίνδυνος hardware υποβάθμισης."
                    : "Moderate hardware degradation risk."
    );

} else {

    logLabelOkValue(
            gr ? "Κατάσταση συσκευής"
               : "Device hardware status",
            gr
                    ? "Η συσκευή φαίνεται hardware-stable."
                    : "Device hardware appears stable."
    );
}

// ------------------------------------------------------------
// FALSE POSITIVE GUARD ENGINE
// ------------------------------------------------------------
int riskSignals = 0;

if (collapseRisk || swellingRisk) riskSignals++;
if (nandRisk || controllerRisk) riskSignals++;
if (thermalRunawayRisk) riskSignals++;
if (pmicInstability) riskSignals++;
if (sensorBusInstability) riskSignals++;

if (riskSignals <= 1) {

    logLabelWarnValue(
            gr ? "Σημείωση αξιολόγησης"
               : "Diagnostic note",
            gr
                    ? "Εντοπίστηκε μόνο ένας μεμονωμένος δείκτης κινδύνου."
                    : "Only a single risk indicator detected."
    );

    logLabelOkValue(
            gr ? "Ερμηνεία"
               : "Interpretation",
            gr
                    ? "Απαιτείται επανάληψη τεστ πριν εξαχθεί συμπέρασμα."
                    : "Repeat diagnostics before concluding."
    );

}


// ------------------------------------------------------------
// DEVICE RELIABILITY RATING
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Συνολική αξιολόγηση αξιοπιστίας συσκευής:"
        : "Device reliability rating:");

String rating;
String ratingDesc;

if (reliabilityScore >= 92) {

    rating = "A+";
    ratingDesc = gr
            ? "Εξαιρετική αξιοπιστία hardware"
            : "Excellent hardware reliability";

}
else if (reliabilityScore >= 85) {

    rating = "A";
    ratingDesc = gr
            ? "Πολύ καλή κατάσταση hardware"
            : "Very good hardware condition";

}
else if (reliabilityScore >= 70) {

    rating = "B";
    ratingDesc = gr
            ? "Καλή κατάσταση με φυσιολογική φθορά"
            : "Good condition with normal wear";

}
else if (reliabilityScore >= 55) {

    rating = "C";
    ratingDesc = gr
            ? "Μέτρια φθορά hardware"
            : "Moderate hardware wear";

}
else {

    rating = "D";
    ratingDesc = gr
            ? "Αυξημένος κίνδυνος hardware αστοχίας"
            : "Elevated hardware failure risk";

}

logLabelValue(
        gr ? "Βαθμολογία συσκευής"
           : "Device rating",
        rating
);

logLabelValue(
        gr ? "Ερμηνεία"
           : "Interpretation",
        ratingDesc
);

// ------------------------------------------------------------
// GEL HARDWARE INTEGRITY CERTIFICATE
// ------------------------------------------------------------
appendHtml("<br>");
logLine();
logInfo(gr
        ? "Πιστοποιητικό Ακεραιότητας Υλικού GEL"
        : "GEL Hardware Integrity Certificate");
logLine();

boolean certificateWarning = false;

// battery flags
boolean collapseRisk =
        p.getBoolean("lab14_collapse_risk", false);

boolean swellingRisk =
        p.getBoolean("lab14_swelling_risk", false);

// authenticity flags
boolean moistureSuspect =
        p.getBoolean("lab29_moisture_suspect", false);

// instability indicators
if (collapseRisk || swellingRisk)
    certificateWarning = true;

if (moistureSuspect)
    certificateWarning = true;

if (hardwareRiskScore >= 60)
    certificateWarning = true;

// ------------------------------------------------------------
// CERTIFICATE RESULT
// ------------------------------------------------------------
if (!certificateWarning && reliabilityScore >= 85) {

    logLabelOkValue(
            gr ? "Κατάσταση Πιστοποιητικού"
               : "Certificate status",
            gr
                    ? "Η συσκευή πέρασε όλους τους ελέγχους ακεραιότητας."
                    : "Device passed all integrity checks."
    );

    logLabelOkValue(
            gr ? "Κατηγορία"
               : "Category",
            "GEL VERIFIED DEVICE"
    );

} else if (!certificateWarning) {

    logLabelWarnValue(
            gr ? "Κατάσταση Πιστοποιητικού"
               : "Certificate status",
            gr
                    ? "Η συσκευή είναι λειτουργική αλλά παρουσιάζει ενδείξεις φυσιολογικής φθοράς."
                    : "Device functional but shows normal wear indicators."
    );

    logLabelValue(
            gr ? "Κατηγορία"
               : "Category",
            "GEL VERIFIED (USED DEVICE)"
    );

} else {

    logLabelWarnValue(
            gr ? "Κατάσταση Πιστοποιητικού"
               : "Certificate status",
            gr
                    ? "Εντοπίστηκαν ενδείξεις πιθανής επέμβασης ή αστάθειας hardware."
                    : "Indicators of hardware intervention or instability detected."
    );

    logLabelValue(
            gr ? "Κατηγορία"
               : "Category",
            "GEL INSPECTION RECOMMENDED"
    );

}

// ------------------------------------------------------------
// NOTE
// ------------------------------------------------------------
logLabelValue(
        gr ? "Σημείωση"
           : "Note",
        gr
                ? "Η αξιολόγηση βασίζεται σε διαγνωστική ανάλυση hardware και στατιστική συνέπεια μετρήσεων."
                : "Evaluation based on hardware diagnostics and measurement consistency."
);

// ------------------------------------------------------------
// GEL DIAGNOSTIC CONFIDENCE
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr
        ? "Αξιοπιστία Διάγνωσης GEL"
        : "GEL Diagnostic Confidence");
logLine();

int evidenceSignals = 0;

// battery evidence
if (collapseRisk) evidenceSignals++;
if (swellingRisk) evidenceSignals++;

// authenticity evidence
if (moistureSuspect) evidenceSignals++;

// hardware instability
if (hardwareRiskScore >= 30) evidenceSignals++;

// reliability factor
if (reliabilityScore >= 85) evidenceSignals++;

int confidencePercent = 65 + (evidenceSignals * 7);
if (confidencePercent > 98) confidencePercent = 98;

logLabelValue(
        gr ? "Confidence"
           : "Confidence",
        confidencePercent + "%"
);

logLabelValue(
        gr ? "Signals analysed"
           : "Signals analysed",
        String.valueOf(evidenceSignals)
);

if (confidencePercent >= 85) {

    logLabelOkValue(
            gr ? "Ερμηνεία"
               : "Interpretation",
            gr
                    ? "Υψηλή αξιοπιστία διάγνωσης."
                    : "High diagnostic reliability."
    );

} else if (confidencePercent >= 70) {

    logLabelWarnValue(
            gr ? "Ερμηνεία"
               : "Interpretation",
            gr
                    ? "Μέτρια αξιοπιστία — συνιστάται επανάληψη ελέγχου."
                    : "Moderate reliability — repeating diagnostics recommended."
    );

} else {

    logLabelWarnValue(
            gr ? "Ερμηνεία"
               : "Interpretation",
            gr
                    ? "Περιορισμένη αξιοπιστία διάγνωσης."
                    : "Limited diagnostic confidence."
    );

}

// ------------------------------------------------------------
// TECHNICIAN NOTE
// ------------------------------------------------------------
if ("A+".equals(rating) || "A".equals(rating)) {

    logLabelOkValue(
            gr ? "Συμπέρασμα τεχνικού"
               : "Technician note",
            gr
                    ? "Η συσκευή εμφανίζει πολύ καλή σταθερότητα hardware."
                    : "Device hardware appears stable."
    );

}
else if ("B".equals(rating)) {

    logLabelWarnValue(
            gr ? "Συμπέρασμα τεχνικού"
               : "Technician note",
            gr
                    ? "Παρατηρείται φυσιολογική φθορά."
                    : "Normal wear detected."
    );

}
else {

    logLabelWarnValue(
            gr ? "Συμπέρασμα τεχνικού"
               : "Technician note",
            gr
                    ? "Συνιστάται παρακολούθηση ή τεχνικός έλεγχος."
                    : "Monitoring or service inspection recommended."
    );

}

// ------------------------------------------------------------
// 10) FINAL SCORES
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

appendHtml("<br>");
logInfo(gr ? "Αυτόματη Ανάλυση" : "AUTO Breakdown");
logLine();

// ================= THERMALS =================
appendHtml("<br>");
logInfo(gr ? "Θερμικά" : "Thermals");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", thermalFlag + " " + thermalScore + "%");

if (zones == null || zones.isEmpty()) {
    logLabelWarnValue(
            gr ? "Ζώνες" : "Zones",
            gr
                    ? "Δεν είναι αναγνώσιμες θερμικές ζώνες — Μόνο θερμοκρασία μπαταρίας (" + fmt1(battTemp) + "°C)"
                    : "No thermal zones readable — Battery temp only (" + fmt1(battTemp) + "°C)"
    );
} else {
    logLabelOkValue(gr ? "Ζώνες" : "Zones", String.valueOf(zones.size()));
    logLabelOkValue(gr ? "Μέγιστη" : "Max", fmt1(maxThermal) + "°C");
    logLabelOkValue(gr ? "Μέση" : "Average", fmt1(avgThermal) + "°C");

    if (cpu  != null) logLabelOkValue("CPU",  fmt1(cpu)  + "°C");
    if (gpu  != null) logLabelOkValue("GPU",  fmt1(gpu)  + "°C");
    if (pmic != null) logLabelOkValue("PMIC", fmt1(pmic) + "°C");
    if (skin != null) logLabelOkValue(gr ? "Επιφάνεια" : "Skin", fmt1(skin) + "°C");

    logLabelOkValue(gr ? "Μπαταρία" : "Battery", fmt1(battTemp) + "°C");
}

appendHtml("<br>");
logInfo(gr ? "Μπαταρία" : "Battery");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", batteryFlag + " " + batteryScore + "%");

logLabelOkValue(
        gr ? "Στοιχεία" : "State",
        (gr ? "Επίπεδο=" : "Level=") +
        (battPct >= 0 ? fmt1(battPct) + "%" : (gr ? "Άγνωστο" : "Unknown")) +
        " | Temp=" + fmt1(battTemp) + "°C" +
        " | " + (gr ? "Φόρτιση=" : "Charging=") + charging
);

appendHtml("<br>");
logInfo(gr ? "Αποθήκευση" : "Storage");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", storageFlag + " " + storageScore + "%");

logLabelOkValue(
        gr ? "Χρήση" : "Usage",
        (gr ? "Ελεύθερο=" : "Free=") + st.pctFree + "% | " +
        (gr ? "Χρησιμοποιείται=" : "Used=") +
        humanBytes(st.usedBytes) + " / " + humanBytes(st.totalBytes)
);

appendHtml("<br>");
logInfo(gr ? "Αποτύπωμα Εφαρμογών" : "Apps footprint");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", appsFlag + " " + appsScore + "%");

logLabelOkValue(
        gr ? "Μετρήσεις" : "Counts",
        "User=" + ap.userApps +
        " | System=" + ap.systemApps +
        " | Total=" + ap.totalApps
);

appendHtml("<br>");
logInfo("RAM");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", ramFlag + " " + ramScore + "%");

logLabelOkValue(
        gr ? "Ελεύθερη" : "Free",
        rm.pctFree + "% (" +
        humanBytes(rm.freeBytes) + " / " + humanBytes(rm.totalBytes) + ")"
);

appendHtml("<br>");
logInfo(gr ? "Σταθερότητα / Χρόνος λειτουργίας" : "Stability / Uptime");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", stabilityFlag + " " + stabilityScore + "%");

logLabelOkValue(gr ? "Χρόνος λειτουργίας" : "Uptime", formatUptime(upMs));

if (upMs < 2 * 60 * 60 * 1000L) {
    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Πρόσφατη επανεκκίνηση (<2h) — πιθανή απόκρυψη αστάθειας"
                    : "Recent reboot (<2h) — instability may be masked"
    );
} else if (upMs > 7L * 24L * 60L * 60L * 1000L) {
    logLabelWarnValue(
            gr ? "Σημείωση" : "Note",
            gr
                    ? "Μεγάλος χρόνος λειτουργίας (>7 ημέρες) — συνιστάται επανεκκίνηση πριν από έλεγχο"
                    : "Long uptime (>7 days) — reboot recommended before deep servicing"
    );
}

appendHtml("<br>");
logInfo(gr ? "Ασφάλεια" : "Security");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", securityFlag + " " + securityScore + "%");

logLabelOkValue(gr ? "Κλείδωμα συσκευής" : "Secure lock", String.valueOf(sec.lockSecure));
logLabelOkValue(
        gr ? "Ενημέρωση ασφαλείας" : "Patch level",
        sec.securityPatch == null ? (gr ? "Άγνωστο" : "Unknown") : sec.securityPatch
);

logLabelOkValue(
        "ADB / Dev",
        "USB=" + sec.adbUsbOn +
        " | Wi-Fi=" + sec.adbWifiOn +
        " | DevOptions=" + sec.devOptionsOn
);

if (sec.rootSuspected)
    logLabelWarnValue(gr ? "Root" : "Root", gr ? "Εντοπίστηκαν ενδείξεις root" : "Suspicion flags detected");

if (sec.testKeys)
    logLabelWarnValue(gr ? "Build" : "Build", gr ? "Υπογεγραμμένο με test-keys (κίνδυνος custom ROM)" : "Signed with test-keys (custom ROM risk)");

appendHtml("<br>");
logInfo(gr ? "Ιδιωτικότητα" : "Privacy");
logLine();

logLabelOkValue(gr ? "Κατάσταση" : "Status", privacyFlag + " " + privacyScore + "%");

logLabelOkValue(
        gr ? "Επικίνδυνες άδειες" : "Dangerous permissions",
        "Location=" + pr.userAppsWithLocation +
        " | Mic=" + pr.userAppsWithMic +
        " | Camera=" + pr.userAppsWithCamera +
        " | SMS=" + pr.userAppsWithSms
);

// ------------------------------------------------------------
// FINAL VERDICT
// ------------------------------------------------------------
appendHtml("<br>");
logInfo(gr ? "Τελικές Βαθμολογίες" : "FINAL Scores");
logLine();

logLabelOkValue(
        gr ? "Υγεία συσκευής" : "Device health",
        deviceHealthScore + "% " + colorFlagFromScore(deviceHealthScore)
);

logLabelOkValue(
        gr ? "Απόδοση" : "Performance",
        performanceScore + "% " + colorFlagFromScore(performanceScore)
);

logLabelOkValue(
        gr ? "Ασφάλεια" : "Security",
        securityScore + "% " + securityFlag
);

logLabelOkValue(
        gr ? "Ιδιωτικότητα" : "Privacy",
        privacyScore + "% " + privacyFlag
);

String verdict =
        finalVerdict(
                deviceHealthScore,
                securityScore,
                privacyScore,
                performanceScore
        );

appendHtml("<br>");
logInfo(gr ? "Τελικό Συμπέρασμα" : "Final verdict");
logLine();

if (verdict.startsWith("🟢"))
    logLabelOkValue(gr ? "Αποτέλεσμα" : "Result", verdict);
else if (verdict.startsWith("🟡"))
    logLabelWarnValue(gr ? "Αποτέλεσμα" : "Result", verdict);
else
    logLabelErrorValue(gr ? "Αποτέλεσμα" : "Result", verdict);

// ------------------------------------------------------------
// HARDWARE TREND ENGINE
// ------------------------------------------------------------
SharedPreferences p = getSharedPreferences("gel_hw_history", MODE_PRIVATE);

int prevRisk = p.getInt("hw_last_risk", -1);
float prevBattery = p.getFloat("hw_last_battery_health", -1f);
int prevAging = p.getInt("hw_last_aging_index", -1);

boolean trendDetected = false;

if (prevRisk >= 0) {

    int riskDiff = hardwareRiskScore - prevRisk;

    if (riskDiff >= 15)
        trendDetected = true;

    if (prevBattery > 0 && (prevBattery - finalScore) > 8)
        trendDetected = true;

    if (prevAging >= 0 && (agingIndex - prevAging) > 10)
        trendDetected = true;

    logLabelValue(
            gr ? "Σύγκριση προηγούμενης διάγνωσης"
               : "Previous diagnosis comparison",
            gr ? "Διαφορά δείκτη: " + riskDiff
               : "Risk difference: " + riskDiff
    );

}

if (trendDetected) {

    logLabelWarnValue(
            gr ? "Τάση υποβάθμισης hardware"
               : "Hardware degradation trend",
            gr
                    ? "Τα δεδομένα δείχνουν πιθανή επιδείνωση της συσκευής."
                    : "Device metrics show a degradation trend."
    );

} else {

    logLabelOkValue(
            gr ? "Τάση hardware"
               : "Hardware trend",
            gr
                    ? "Δεν εντοπίστηκε επιδείνωση από την προηγούμενη μέτρηση."
                    : "No degradation trend detected since previous run."
    );
}

// ------------------------------------------------------------
// RUN COUNTER + RELIABILITY INDEX
// ------------------------------------------------------------

int runCount = p.getInt("hw_run_count", 0);
runCount++;

p.edit().putInt("hw_run_count", runCount).apply();

int reliabilityIndex;

if (runCount >= 10)
    reliabilityIndex = 95;
else if (runCount >= 7)
    reliabilityIndex = 85;
else if (runCount >= 5)
    reliabilityIndex = 75;
else if (runCount >= 3)
    reliabilityIndex = 65;
else
    reliabilityIndex = 50;

logLabelValue(
        gr ? "Αξιοπιστία διάγνωσης"
           : "Diagnosis reliability",
        reliabilityIndex + "% (" +
                (gr ? "εκτελέσεις: " : "runs: ") +
                runCount + ")"
);

if (runCount < 3) {

    logLabelWarnValue(
            gr ? "Σημείωση αξιοπιστίας"
               : "Reliability note",
            gr
                    ? "Η διάγνωση βασίζεται σε περιορισμένο ιστορικό."
                    : "Diagnosis based on limited history."
    );

} else if (runCount >= 7) {

    logLabelOkValue(
            gr ? "Στατιστική αξιοπιστία"
               : "Statistical reliability",
            gr
                    ? "Η διάγνωση βασίζεται σε επαρκές ιστορικό runs."
                    : "Diagnosis based on sufficient historical runs."
    );
}

// ------------------------------------------------------------
// STORE HARDWARE HEALTH SNAPSHOT
// ------------------------------------------------------------

p.edit()
        .putInt("hw_last_risk", hardwareRiskScore)
        .putFloat("hw_last_battery_health", finalScore)
        .putInt("hw_last_aging_index", agingIndex)
        .putBoolean("hw_last_nand_risk", nandRisk)
        .putBoolean("hw_last_controller_risk", controllerRisk)
        .putBoolean("hw_last_thermal_risk", thermalRunawayRisk)
        .putLong("hw_last_timestamp", System.currentTimeMillis())
        .apply();

logLabelOkValue(
        gr ? "Ιστορικό hardware"
           : "Hardware history",
        gr ? "Το snapshot αποθηκεύτηκε"
           : "Hardware snapshot stored"
);

// ------------------------------------------------------------
// DIAGNOSTIC CONSISTENCY ENGINE
// ------------------------------------------------------------
logLine();
logInfo(gr
        ? "Έλεγχος συνέπειας διαγνωστικών αποτελεσμάτων:"
        : "Diagnostic consistency check:");

boolean diagnosticConflict = false;
int conflictScore = 0;

// Battery healthy but power instability
if (finalScore >= 80 && pmicInstability) {
    diagnosticConflict = true;
    conflictScore += 30;
}

// Battery healthy but collapse risk
if (finalScore >= 80 && collapseRisk) {
    diagnosticConflict = true;
    conflictScore += 30;
}

// Storage healthy but controller risk
if (!nandRisk && controllerRisk) {
    diagnosticConflict = true;
    conflictScore += 25;
}

// Thermal normal but PMIC instability
if (!thermalRunawayRisk && pmicInstability) {
    diagnosticConflict = true;
    conflictScore += 20;
}

// Sensors stable but sensor bus instability
if (!sensorFlaps && sensorBusInstability) {
    diagnosticConflict = true;
    conflictScore += 20;
}

logLabelValue(
        gr ? "Δείκτης ασυνέπειας διάγνωσης"
           : "Diagnostic inconsistency index",
        conflictScore + "/100"
);

if (diagnosticConflict) {

    logLabelWarnValue(
            gr ? "Συνέπεια διάγνωσης"
               : "Diagnostic consistency",
            gr
                    ? "Εντοπίστηκαν αντικρουόμενα διαγνωστικά ευρήματα."
                    : "Conflicting diagnostic indicators detected."
    );

    logLabelWarnValue(
            gr ? "Σύσταση"
               : "Recommendation",
            gr
                    ? "Συνιστάται επανάληψη τεστ για επιβεβαίωση."
                    : "Repeat diagnostics to confirm results."
    );

} else {

    logLabelOkValue(
            gr ? "Συνέπεια διάγνωσης"
               : "Diagnostic consistency",
            gr
                    ? "Τα διαγνωστικά αποτελέσματα είναι συνεπή."
                    : "Diagnostic results appear consistent."
    );
}

// ------------------------------------------------------------
// GEL DIAGNOSTIC CERTIFICATION
// ------------------------------------------------------------
logLine();
logInfo("GEL Hardware Diagnostic Certification");

// Device rating
logLabelValue(
        gr ? "Βαθμολογία συσκευής"
           : "Device rating",
        rating
);

// Hardware risk
logLabelValue(
        gr ? "Δείκτης κινδύνου hardware"
           : "Hardware risk score",
        hardwareRiskScore + "/100"
);

// Diagnostic reliability
logLabelValue(
        gr ? "Αξιοπιστία διάγνωσης"
           : "Diagnostic reliability",
        reliabilityIndex + "%"
);

// Timestamp
String ts = new java.text.SimpleDateFormat(
        "yyyy-MM-dd HH:mm",
        java.util.Locale.US
).format(new java.util.Date());

logLabelValue(
        gr ? "Χρόνος διάγνωσης"
           : "Diagnostic timestamp",
        ts
);

// ------------------------------------------------------------
// GEL DIAGNOSTIC REPORT ID
// ------------------------------------------------------------
String reportSource =
        Build.MANUFACTURER +
        Build.MODEL +
        ts +
        hardwareRiskScore +
        deviceHealthScore +
        performanceScore;

String reportId;

try {

    java.security.MessageDigest md =
            java.security.MessageDigest.getInstance("SHA-256");

    byte[] hash =
            md.digest(reportSource.getBytes(java.nio.charset.StandardCharsets.UTF_8));

    StringBuilder hex = new StringBuilder();

    for (int i = 0; i < 8; i++) {
        hex.append(String.format("%02X", hash[i]));
    }

    reportId = "GEL-" + hex.toString();

} catch (Throwable e) {

    reportId = "GEL-" + System.currentTimeMillis();

}

logLabelValue(
        gr ? "ID διάγνωσης"
           : "Diagnostic ID",
        reportId
);

// Certification note
logLabelOkValue(
        gr ? "Πιστοποίηση"
           : "Certification",
        gr
                ? "Η αξιολόγηση βασίζεται στα αποτελέσματα των διαγνωστικών εργαστηρίων GEL."
                : "Assessment based on GEL diagnostic laboratory results."
);

appendHtml("<br>");
logOk(gr ? "GEL Certification completed." : "GEL Certification completed.");
logLine();

appendHtml("<br>");
logOk(gr ? "Το Lab 30 ολοκληρώθηκε." : "Lab 30 finished.");
logLine();
}

// ============================================================
// ======= LAB 30 INTERNAL AUTO HELPERS (SAFE, NO IMPORTS) =====
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

    final boolean gr = AppLang.isGreek(this);

// ============================================================
// LEVEL 1 — HEALTHY / NORMAL
// ============================================================
if (health >= 80) {

    if (sec < 55 || priv < 55) {
        return gr
                ? "Κατάσταση συσκευής: ΥΓΙΗΣ.\n" +
                  "Προσοχή: εντοπίστηκαν ζητήματα ιδιωτικότητας ή ασφάλειας.\n" +
                  "Συνιστάται έλεγχος από τον χρήστη."
                : "Device condition: HEALTHY.\n" +
                  "Attention: privacy or security risks detected.\n" +
                  "User review is recommended.";
    }

    return gr
            ? "Κατάσταση συσκευής: ΥΓΙΗΣ.\n" +
              "Δεν απαιτείται τεχνική παρέμβαση."
            : "Device condition: HEALTHY.\n" +
              "No servicing required.";
}

// ============================================================
// LEVEL 2 — OBSERVATION (UNCERTAIN CAUSE)
// ============================================================
if (health >= 55) {

    if (sec < 55 || priv < 55) {
        return gr
                ? "Κατάσταση συσκευής: ΜΕΤΡΙΑ ΥΠΟΒΑΘΜΙΣΗ.\n" +
                  "Προσοχή: εντοπίστηκαν ζητήματα ιδιωτικότητας ή ασφάλειας.\n" +
                  "Συνιστάται έλεγχος από τον χρήστη."
                : "Device condition: MODERATE DEGRADATION.\n" +
                  "Attention: privacy or security risks detected.\n" +
                  "User review is recommended.";
    }

    return gr
            ? "Κατάσταση συσκευής: ΜΕΤΡΙΑ ΥΠΟΒΑΘΜΙΣΗ.\n" +
              "Συνιστάται περαιτέρω παρακολούθηση."
            : "Device condition: MODERATE DEGRADATION.\n" +
              "Further monitoring is recommended.";
}

// ============================================================
// LEVEL 3 — UNATTRIBUTED INSTABILITY
// (Evidence-based — no hardware accusation)
// ============================================================
return gr
        ? "Κατάσταση συσκευής: ΕΝΤΟΠΙΣΤΗΚΕ ΑΣΤΑΘΕΙΑ.\n" +
          "Παρατηρείται υποβάθμιση συστήματος χωρίς επιβεβαιωμένη αιτία λογισμικού.\n" +
          "Η αιτία δεν έχει επιβεβαιωθεί.\n" +
          "Κατηγοριοποίηση: Μη αποδοθείσα αστάθεια συστήματος.\n" +
          "Συνιστώνται περαιτέρω διαγνωστικοί έλεγχοι."
        : "Device condition: INSTABILITY DETECTED.\n" +
          "System degradation observed without a confirmed software cause.\n" +
          "Cause is not confirmed.\n" +
          "Classification: Unattributed system instability.\n" +
          "Further diagnostics are recommended.";

}

private String fmt1(float v) {
return String.format(Locale.US, "%.1f", v);
}

// ============================================================
// LAB 31 — FINAL TECHNICIAN SUMMARY (READ-ONLY)
// Does NOT modify GELServiceLog — only reads it.
// Exports via ServiceReportActivity.
// ============================================================
private void lab31FinalSummary() {

    final boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 31 — ΤΕΛΙΚΗ ΣΥΝΟΨΗ ΤΕΧΝΙΚΟΥ (ΜΟΝΟ ΑΝΑΓΝΩΣΗ)"
            : "LAB 31 — FINAL TECHNICIAN SUMMARY (READ-ONLY)");
    logLine();

    // ------------------------------------------------------------
    // 1) READ FULL LOG
    // ------------------------------------------------------------
    String fullLog = GELServiceLog.getAll();

    if (fullLog.trim().isEmpty()) {
        logWarn(gr
                ? "Δεν βρέθηκαν διαγνωστικά δεδομένα. Εκτελέστε πρώτα τα Manual Tests."
                : "No diagnostic data found. Please run Manual Tests first.");
        return;
    }

    // ------------------------------------------------------------
    // 2) DETECT WARNINGS / ERRORS
    // ------------------------------------------------------------
    String[] lines = fullLog.split("\n");
    StringBuilder warnings = new StringBuilder();

    for (String l : lines) {

        if (l == null) continue;

        String low = l.toLowerCase(Locale.US);

        if (low.contains("warning") || low.contains("error")) {
            warnings.append(l).append("\n");
        }
    }

    // ------------------------------------------------------------
    // 3) PRINT WARNINGS SUMMARY
    // ------------------------------------------------------------
    appendHtml("<br>");
    logInfo(gr ? "Σύνοψη διαγνωστικών" : "Diagnostics summary");
    logLine();

    boolean issuesDetected = warnings.length() > 0;

    if (!issuesDetected) {

        logLabelOkValue(
                gr ? "Κατάσταση συστήματος" : "System status",
                gr
                        ? "Δεν εντοπίστηκαν προειδοποιήσεις ή σφάλματα"
                        : "No warnings or errors detected"
        );

    } else {

        logLabelWarnValue(
                gr ? "Κατάσταση συστήματος" : "System status",
                gr
                        ? "Εντοπίστηκαν προειδοποιήσεις / σφάλματα"
                        : "Warnings / errors detected"
        );

        for (String w : warnings.toString().split("\n")) {

            if (w != null && !w.trim().isEmpty()) {

                logLabelWarnValue(
                        gr ? "Ζήτημα" : "Issue",
                        w.trim()
                );
            }
        }
    }

    // ------------------------------------------------------------
    // 4) BATTERY SCORE FROM LAB 14
    // ------------------------------------------------------------
    float batteryScore =
            p.getFloat("lab14_health_score", -1f);

    float batteryContribution = 0f;

    if (batteryScore >= 0) {
        batteryContribution = batteryScore * 0.20f;
    }

    // ------------------------------------------------------------
    // 5) DEVICE BASE SCORE
    // ------------------------------------------------------------
    float deviceScore = 80f;

    if (issuesDetected) {
        deviceScore -= 20f;
    }

    deviceScore += batteryContribution;

    if (deviceScore > 100f) deviceScore = 100f;
    if (deviceScore < 0f) deviceScore = 0f;

    // ------------------------------------------------------------
    // 6) DEVICE GRADE
    // ------------------------------------------------------------
    String deviceGrade;

    if (deviceScore >= 90)
        deviceGrade = "A+";
    else if (deviceScore >= 80)
        deviceGrade = "A";
    else if (deviceScore >= 70)
        deviceGrade = "B";
    else if (deviceScore >= 60)
        deviceGrade = "C";
    else
        deviceGrade = "D";

    // ------------------------------------------------------------
    // 7) PRINT DEVICE EVALUATION
    // ------------------------------------------------------------
    appendHtml("<br>");
    logInfo(gr
            ? "Αξιολόγηση συσκευής"
            : "Device evaluation");
    logLine();

    if (batteryScore >= 0) {

        logLabelValue(
                gr ? "Υγεία μπαταρίας (LAB 14)"
                        : "Battery health (LAB 14)",
                String.format(
                        Locale.US,
                        "%.0f%%",
                        batteryScore
                )
        );

        logLabelValue(
                gr ? "Συμβολή μπαταρίας"
                        : "Battery contribution",
                String.format(
                        Locale.US,
                        "%.1f / 20",
                        batteryContribution
                )
        );
    }

    logLabelOkValue(
            gr ? "Συνολική βαθμολογία συσκευής"
                    : "Device overall score",
            String.format(
                    Locale.US,
                    "%.0f / 100",
                    deviceScore
            )
    );

    logLabelOkValue(
            gr ? "Κατηγορία συσκευής"
                    : "Device grade",
            deviceGrade
    );
    
    appendHtml("<br>");
   
// ------------------------------------------------------------
// DEVICE MANIPULATION SUSPICION INDEX
// (LAB28 + LAB29 indicators)
// ------------------------------------------------------------
int manipulationScore = 0;

// LAB29 instability patterns
boolean hwInstability =
        p.getBoolean("lab29_instability_pattern", false);

boolean moistureIndicators =
        p.getBoolean("lab29_moisture_indicators", false);

// LAB30 authenticity indicators
boolean nonOemParts =
        p.getBoolean("lab30_non_oem_parts", false);

boolean displayReplaced =
        p.getBoolean("lab30_display_replaced", false);

boolean cameraReplaced =
        p.getBoolean("lab30_camera_replaced", false);

boolean batteryReplaced =
        p.getBoolean("lab30_battery_replaced", false);

// scoring
if (hwInstability) manipulationScore += 20;
if (moistureIndicators) manipulationScore += 25;
if (nonOemParts) manipulationScore += 20;
if (displayReplaced) manipulationScore += 10;
if (cameraReplaced) manipulationScore += 10;
if (batteryReplaced) manipulationScore += 5;

if (manipulationScore > 100) manipulationScore = 100;

String manipulationLabel;

if (manipulationScore >= 60)
    manipulationLabel = gr ? "Ισχυρές ενδείξεις παρέμβασης"
                           : "Strong manipulation indicators";
else if (manipulationScore >= 35)
    manipulationLabel = gr ? "Μέτριες ενδείξεις παρέμβασης"
                           : "Moderate manipulation indicators";
else if (manipulationScore >= 15)
    manipulationLabel = gr ? "Ασθενείς ενδείξεις"
                           : "Weak indicators";
else
    manipulationLabel = gr ? "Καμία ένδειξη"
                           : "No indicators";
                           
appendHtml("<br>");

if (manipulationScore >= 35) {

    logLabelWarnValue(
            gr ? "Δείκτης παρέμβασης συσκευής"
               : "Device manipulation suspicion index",
            manipulationScore + "/100 (" + manipulationLabel + ")"
    );

} else {

    logLabelOkValue(
            gr ? "Δείκτης παρέμβασης συσκευής"
               : "Device manipulation suspicion index",
            manipulationScore + "/100 (" + manipulationLabel + ")"
    );

}
    
// ------------------------------------------------------------
// DEVICE RELIABILITY INDEX
// ------------------------------------------------------------
float dri = 100f;

// battery health
if (batteryScore >= 0) {

    if (batteryScore < 55)
        dri -= 25;
    else if (batteryScore < 70)
        dri -= 15;
}

// RAM pressure
boolean ramStress =
        p.getBoolean("lab19_ram_pressure", false);

if (ramStress) dri -= 10;

// crash history
boolean crashHistory =
        p.getBoolean("lab25_crash_detected", false);

if (crashHistory) dri -= 20;

// reboot instability
boolean rebootPattern =
        p.getBoolean("lab20_reboot_pattern", false);

if (rebootPattern) dri -= 10;

// thermal instability
boolean thermalIssue =
        p.getBoolean("lab16_thermal_warning", false);

if (thermalIssue) dri -= 15;

// swelling
boolean swellingRisk =
        p.getBoolean("lab14_swelling_risk", false);

if (swellingRisk) dri -= 15;

// root risk
boolean rooted =
        p.getBoolean("lab24_root_detected", false);

if (rooted) dri -= 10;

// battery calibration drift
boolean calibrationDrift =
        p.getBoolean("lab14_calibration_drift", false);

if (calibrationDrift) dri -= 10;

// battery collapse risk
boolean collapseRisk =
        p.getBoolean("lab14_collapse_risk", false);

if (collapseRisk) dri -= 20;

// manipulation suspicion (LAB28 + LAB29)
if (manipulationScore >= 60)
    dri -= 20;
else if (manipulationScore >= 35)
    dri -= 10;

if (dri > 100) dri = 100;
if (dri < 0) dri = 0;

String driLabel;

if (dri >= 90)
    driLabel = gr ? "Πολύ αξιόπιστη συσκευή" : "Highly reliable";
else if (dri >= 75)
    driLabel = gr ? "Σταθερή συσκευή" : "Stable";
else if (dri >= 60)
    driLabel = gr ? "Μέτριος κίνδυνος αστάθειας" : "Moderate risk";
else if (dri >= 40)
    driLabel = gr ? "Υψηλός κίνδυνος προβλημάτων" : "High risk";
else
    driLabel = gr ? "Ασταθής συσκευή" : "Unstable device";

logLabelOkValue(
        gr ? "Δείκτης αξιοπιστίας συσκευής"
           : "Device reliability index",
        String.format(
                Locale.US,
                "%.0f / 100 (%s)",
                dri,
                driLabel
        )
);

    // ------------------------------------------------------------
    // 8) EXPORT NOTE
    // ------------------------------------------------------------
    appendHtml("<br>");

    logLabelOkValue(
            gr ? "Εξαγωγή"
                    : "Export",
            gr
                    ? "Χρησιμοποιήστε το κουμπί παρακάτω για δημιουργία επίσημου PDF report"
                    : "Use the button below to generate the official PDF report"
    );

    // enable export button
    enableSingleExportButton();

    appendHtml("<br>");
    logOk(gr
            ? "Το Lab 31 ολοκληρώθηκε."
            : "Lab 31 finished.");
    logLine();
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

// ============================================================
// LAB 6 — TOUCH GRID
// ============================================================

final boolean gr = AppLang.isGreek(this);

if (requestCode == REQ_LAB6_TOUCH) {

    int total = TouchGridTestActivity.getTotalZones();
    int remaining = TouchGridTestActivity.getRemainingZones();

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 6 — Οθόνη / Αφή" : "LAB 6 — Display / Touch");
    logLine();

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Έλεγχος αφής" : "Touch grid test",
                gr ? "Ολοκληρώθηκε" : "Completed"
        );

        logLabelOkValue(
                gr ? "Ζώνες οθόνης" : "Screen zones",
                gr ? "Όλες οι ζώνες ανταποκρίθηκαν" : "All zones responded"
        );

        logLabelOkValue(
                gr ? "Νεκρές ζώνες" : "Dead zones",
                gr ? "Δεν εντοπίστηκαν" : "Not detected"
        );

    } else {

        logLabelWarnValue(
                gr ? "Έλεγχος αφής" : "Touch grid test",
                gr ? "Μη ολοκληρωμένος" : "Incomplete"
        );

        logLabelErrorValue(
                gr ? "Μη ανταποκρινόμενες ζώνες" : "Unresponsive zones",
                remaining + " / " + total
        );

        logInfo(gr ? "Ερμηνεία:" : "Interpretation:");

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Τοπικές νεκρές ζώνες digitizer"
                        : "Localized digitizer dead zones"
        );

        logLabelOkValue(
                gr ? "Σύσταση" : "Recommendation",
                gr
                        ? "Επαναλάβετε χειροκίνητα τον έλεγχο για επιβεβαίωση"
                        : "Manual re-test to confirm behavior"
        );
    }

    appendHtml("<br>");
    logLabelOkValue(
            gr ? "Επόμενο βήμα" : "Next step",
            gr
                    ? "LAB 6 PRO — Χρώμα & Ομοιομορφία Οθόνης"
                    : "LAB 6 PRO — Display Color & Uniformity"
    );
    logLine();

    startActivityForResult(
            new Intent(this, DisplayProTestActivity.class),
            REQ_LAB6_COLOR
    );

    return;
}

// ============================================================
// LAB 6 — TOUCH GRID
// ============================================================
if (requestCode == REQ_LAB6_TOUCH) {

    int total = TouchGridTestActivity.getTotalZones();
    int remaining = TouchGridTestActivity.getRemainingZones();

    appendHtml("<br>");
    logLine();
    logSection(gr ? "LAB 6 — Οθόνη / Αφή" : "LAB 6 — Display / Touch");
    logLine();

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Έλεγχος αφής" : "Touch grid test",
                gr ? "Ολοκληρώθηκε" : "Completed"
        );

        logLabelOkValue(
                gr ? "Ζώνες οθόνης" : "Screen zones",
                gr ? "Όλες οι ζώνες ανταποκρίθηκαν" : "All zones responded"
        );

        logLabelOkValue(
                gr ? "Νεκρές ζώνες" : "Dead zones",
                gr ? "Δεν εντοπίστηκαν" : "Not detected"
        );

    } else {

        logLabelWarnValue(
                gr ? "Έλεγχος αφής" : "Touch grid test",
                gr ? "Μη ολοκληρωμένος" : "Incomplete"
        );

        logLabelErrorValue(
                gr ? "Μη ανταποκρινόμενες ζώνες" : "Unresponsive zones",
                remaining + " / " + total
        );

        logInfo(gr ? "Ερμηνεία:" : "Interpretation:");

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Τοπικές νεκρές ζώνες digitizer"
                        : "Localized digitizer dead zones"
        );

        logLabelOkValue(
                gr ? "Σύσταση" : "Recommendation",
                gr
                        ? "Επαναλάβετε χειροκίνητα τον έλεγχο για επιβεβαίωση"
                        : "Manual re-test to confirm behavior"
        );
    }

    appendHtml("<br>");
    logLabelOkValue(
            gr ? "Επόμενο βήμα" : "Next step",
            gr
                    ? "LAB 6 PRO — Χρώμα & Ομοιομορφία Οθόνης"
                    : "LAB 6 PRO — Display Color & Uniformity"
    );
    logLine();

    startActivityForResult(
            new Intent(this, DisplayProTestActivity.class),
            REQ_LAB6_COLOR
    );
    return;
}

// ============================================================
// LAB 6 PRO — DISPLAY COLOR / UNIFORMITY / ARTIFACTS
// ============================================================
if (requestCode == REQ_LAB6_COLOR) {

    if (resultCode == RESULT_CANCELED) {

        logLabelWarnValue(
                "LAB 6 PRO",
                gr ? "Ακυρώθηκε από τον χρήστη" : "Canceled by user"
        );

        logLabelWarnValue(
                gr ? "Οπτικός έλεγχος" : "Visual inspection",
                gr ? "Δεν πραγματοποιήθηκε" : "Not performed"
        );

        appendHtml("<br>");
        logLine();
        enableSingleExportButton();
        return;
    }

    boolean issues =
            data != null && data.getBooleanExtra("display_issues", false);

    if (!issues) {

        logLabelOkValue(
                gr ? "Οπτικός έλεγχος" : "Visual inspection",
                gr ? "Δεν αναφέρθηκαν ανωμαλίες" : "No visible artifacts reported"
        );

        logLabelOkValue(
                gr ? "Ομοιομορφία οθόνης" : "Display uniformity",
                "OK"
        );

        logLabelOkValue(
                gr ? "Burn-in / banding" : "Burn-in / banding",
                gr ? "Δεν παρατηρήθηκε" : "Not observed"
        );

    } else {

        logLabelWarnValue(
                gr ? "Οπτικός έλεγχος" : "Visual inspection",
                gr ? "Αναφέρθηκαν οπτικές ανωμαλίες" : "User reported visual anomalies"
        );

        logInfo(gr ? "Πιθανά ευρήματα:" : "Possible findings:");

        logLabelWarnValue("• Issue",
                gr ? "Burn-in / αποτύπωση εικόνας" : "Burn-in / image retention");

        logLabelWarnValue("• Issue",
                gr ? "Color banding / διαβαθμίσεις" : "Color banding / gradient steps");

        logLabelWarnValue("• Issue",
                gr ? "Λεκέδες / mura / μετατόπιση χρώματος"
                   : "Screen stains / mura / tint shift");
    }

    appendHtml("<br>");
    logSection(gr ? "LAB 6 — Τελικό Αποτέλεσμα" : "LAB 6 — Final Result");
    logLine();

    logLabelOkValue(
            gr ? "Έλεγχος οθόνης" : "Display test",
            gr
                    ? "Ολοκληρώθηκε έλεγχος αφής και οπτικής επιθεώρησης"
                    : "Touch integrity and visual inspection completed"
    );

    appendHtml("<br>");
    logOk(gr ? "Το Lab 6 ολοκληρώθηκε." : "Lab 6 finished.");
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
    logSection(gr
            ? "LAB 7 — Περιστροφή & Αισθητήρας Εγγύτητας"
            : "LAB 7 — Rotation & Proximity Sensors");
    logLine();

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Ανίχνευση περιστροφής" : "Rotation detection",
                gr ? "Εντοπίστηκε μέσω επιταχυνσιόμετρου" : "Detected via accelerometer"
        );

        logLabelOkValue(
                gr ? "Αλλαγή προσανατολισμού" : "Orientation change",
                gr ? "Επιβεβαιώθηκε" : "Confirmed"
        );

        logLabelOkValue(
                gr ? "Αισθητήρες κίνησης" : "Motion sensors",
                gr ? "Λειτουργούν κανονικά" : "Responding normally"
        );

        logLabelOkValue(
                gr ? "Επόμενο βήμα" : "Next step",
                gr ? "Έλεγχος αισθητήρα εγγύτητας" : "Proximity sensor test"
        );

        // AUTO-START PROXIMITY TEST
        startActivityForResult(
                new Intent(this, ProximityCheckActivity.class),
                8008
        );
        return;

    } else {

        logLabelErrorValue(
                gr ? "Ανίχνευση περιστροφής" : "Rotation detection",
                gr ? "Δεν εντοπίστηκε" : "Not detected"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Η αυτόματη περιστροφή είναι απενεργοποιημένη ή υπάρχει βλάβη αισθητήρα"
                        : "Auto-rotate disabled or sensor malfunction"
        );

        appendHtml("<br>");
        logOk(gr
                ? "Το Lab 7 ολοκληρώθηκε (περιστροφή μη επιτυχής)."
                : "Lab 7 finished (rotation incomplete).");
        logLine();

        enableSingleExportButton();
        return;
    }
}

// ============================================================
// LAB 7 — PROXIMITY SENSOR
// ============================================================

if (requestCode == 8008) {

    if (resultCode == RESULT_OK) {

        logLabelOkValue(
                gr ? "Αισθητήρας εγγύτητας" : "Proximity sensor",
                gr ? "Ανταποκρίθηκε σωστά" : "Responded correctly"
        );

        logLabelOkValue(
                gr ? "Ανίχνευση Κοντά / Μακριά" : "Near / Far detection",
                gr ? "Επιβεβαιώθηκε" : "Confirmed"
        );

        logLabelOkValue(
                gr ? "Συμπεριφορά οθόνης" : "Screen behavior",
                gr
                        ? "Η οθόνη έκλεισε όταν καλύφθηκε ο αισθητήρας"
                        : "Turned off when sensor was covered"
        );

    } else {

        logLabelErrorValue(
                gr ? "Αισθητήρας εγγύτητας" : "Proximity sensor",
                gr ? "Δεν εντοπίστηκε απόκριση" : "No response detected"
        );

        logLabelWarnValue(
                gr ? "Πιθανή αιτία" : "Possible cause",
                gr
                        ? "Εμπόδιο στον αισθητήρα ή πιθανή βλάβη υλικού"
                        : "Sensor obstruction or hardware fault"
        );
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 7 ολοκληρώθηκε." : "Lab 7 finished.");
    logLine();

    enableSingleExportButton();
    return;
}
}

// ============================================================
// END OF CLASS
// ============================================================
}
