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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class ManualTestsActivity extends AppCompatActivity {

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
        body4.addView(makeTestButton("18. Thermal Stress (LIVE + Manual)", this::lab18ThermalQuestionnaire)); // alias -> lab18()

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
        body7.addView(makeTestButton("28. App Permissions & Privacy (manual)", this::lab28PermissionsPrivacy));
        body7.addView(makeTestButton("29. Combine Auto + Manual Findings", this::lab29CombineFindings));
        body7.addView(makeTestButton("30. Final Service Notes for Report", this::lab30FinalNotes));

        // LOG AREA
        txtLog = new TextView(this);
        txtLog.setTextSize(13f);
        txtLog.setTextColor(0xFFEEEEEE);
        txtLog.setPadding(0, dp(16), 0, dp(8));
        txtLog.setMovementMethod(new ScrollingMovementMethod());
        txtLog.setText(Html.fromHtml("<b>" + getString(R.string.manual_log_title) + "</b><br>"));

        root.addView(txtLog);
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
    // LAB 15 — Battery Health Stress Test (GEL C Mode)
    // ============================================================
    private void lab15BatteryHealthStressTest() {
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
        info.setText("GEL Stress test burns CPU + max brightness and watches real battery % drop.\nSelect duration then start.");
        info.setTextSize(13f);
        info.setTextColor(0xFFFFFFFF);
        info.setPadding(0, 0, 0, dp(8));
        layout.addView(info);

        TextView durLabel = new TextView(this);
        durLabel.setText("Duration (seconds):");
        durLabel.setTextSize(13f);
        durLabel.setTextColor(0xFFFFD700);
        durLabel.setPadding(0, dp(8), 0, 0);
        layout.addView(durLabel);

        final TextView durValue = new TextView(this);
        durValue.setTextSize(13f);
        durValue.setTextColor(0xFF39FF14);
        layout.addView(durValue);

        final SeekBar seek = new SeekBar(this);
        seek.setMax(110); // 10..120 sec
        layout.addView(seek);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int seconds = 10 + progress;
                durValue.setText("Selected: " + seconds + " sec (10–120 sec)");
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        seek.setProgress(20);
        durValue.setText("Selected: 30 sec (10–120 sec)");

        Button start = new Button(this);
        start.setText("Start Stress Test (GEL C Mode)");
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
            int durationSec = 10 + seek.getProgress();
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

        logLine();
        logInfo("LAB 15 — Battery Health Stress Test started.");
        logInfo("Mode: GEL C Mode (aggressive CPU burn + brightness MAX).");
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

            int healthPct = estimateHealthFromDrain(perHour);

            if (healthPct >= 90) {
                logOk("Estimated Battery Health from stress: " + healthPct + "% (Excellent)");
            } else if (healthPct >= 80) {
                logOk("Estimated Battery Health from stress: " + healthPct + "% (Normal)");
            } else if (healthPct >= 60) {
                logWarn("Estimated Battery Health from stress: " + healthPct + "% (Worn)");
            } else {
                logError("Estimated Battery Health from stress: " + healthPct + "% (Poor)");
            }

            if (delta <= 0.1f) {
                logOk("Almost zero drain in stress window — battery behavior looks strong.");
            } else if (perHour <= 12f) {
                logOk(String.format(Locale.US, "Estimated drain ≈ %.1f%%/hour under stress — strong.", perHour));
            } else if (perHour <= 20f) {
                logWarn(String.format(Locale.US, "Estimated drain ≈ %.1f%%/hour under stress — borderline.", perHour));
            } else {
                logError(String.format(Locale.US, "Estimated drain ≈ %.1f%%/hour under stress — heavy wear.", perHour));
            }

        }, durationSec * 1000L);
    }

    private int estimateHealthFromDrain(float perHour) {
        if (perHour <= 0f) return 100;
        float baseline = 12f;
        int est = Math.round((baseline / perHour) * 100f);
        if (est > 100) est = 100;
        if (est < 1) est = 1;
        return est;
    }

    private void applyMaxBrightnessAndKeepOn() {
        try {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            oldWindowBrightness = lp.screenBrightness;
            oldKeepScreenOn = (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) != 0;

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
// LAB 17 — Thermal Snapshot (GEL Universal Edition — SAFE, NO HELPERS INSIDE)
// ============================================================
private void lab17ThermalSnapshot() {
    logLine();
    logInfo("LAB 17 — Thermal Snapshot (ASCII thermal map)");

    // 1) Read thermal zones (CPU/GPU/PMIC/Skin) — uses EXISTING helper
    Map<String, Float> zones = readThermalZones();

    // 2) Battery ALWAYS from BatteryManager — uses EXISTING helper
    float batt = getBatteryTemperature();

    if (zones == null || zones.isEmpty()) {
        logWarn("Device exposes NO thermal zones. Printing battery only.");
        printZoneAscii("Battery", batt);
        logOk("Lab 17 finished.");
        return;
    }

    // Auto-detect CPU/GPU/SKIN/PMIC (uses existing pickZone)
    Float cpu  = pickZone(zones, "cpu", "cpu-therm", "big", "little", "tsens", "mtktscpu");
    Float gpu  = pickZone(zones, "gpu", "gpu-therm", "gpuss", "mtkgpu");
    Float skin = pickZone(zones, "skin", "xo-therm", "shell", "surface");
    Float pmic = pickZone(zones, "pmic", "pmic-therm", "power-thermal", "charger", "chg");

    logOk("Thermal Zones found: " + zones.size());

    // Print snapshot
    if (cpu  != null) printZoneAscii("CPU", cpu);
    if (gpu  != null) printZoneAscii("GPU", gpu);

    // Battery ALWAYS printed even if no thermal zone
    printZoneAscii("Battery", batt);

    if (skin != null) printZoneAscii("Skin", skin);
    if (pmic != null) printZoneAscii("PMIC", pmic);

    logOk("Lab 17 finished.");
}

// ============================================================
// ASCII BAR (100 chars, MONOSPACE, NEVER WRAPS)
// ============================================================
private void printZoneAscii(String label, float t) {

    // Color logic
    String color;
    if (t < 45)        color = "🟩";
    else if (t < 60)   color = "🟨";
    else               color = "🟥";

    // Normalize to 0–100 (full bar at 80°C)
    float maxT = 80f;
    float pct = Math.min(1f, t / maxT);
    int bars = (int)(pct * 100f);

    // Build the bar
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bars; i++) sb.append("█");
    while (sb.length() < 100) sb.append(" ");   // fill to exactly 100 chars

    // PRINT WITH MONOSPACE TEXTVIEW
    printMonospace(label + ": " + color + " " + String.format(Locale.US, "%.1f°C", t));
    printMonospace(sb.toString());
}

// ============================================================
// LOG WRAPPER WITH MONOSPACE
// ============================================================
private void printMonospace(String text) {
    TextView tv = new TextView(this);
    tv.setText(text);
    tv.setTextColor(Color.WHITE);
    tv.setTextSize(14f);
    tv.setTypeface(Typeface.MONOSPACE);  // THE MAGIC FIX
    logContainer.addView(tv);            // same container used by logInfo/logOk
}
    
// ============================================================
// LAB 18 — Heat Under Load (EXACT TEXT + COLORS LIKE PHOTOS)
// ============================================================
private boolean lab18Running = false;
private final Handler lab18Handler = new Handler(Looper.getMainLooper());

private void lab18ThermalQuestionnaire() { lab18(); }

private void lab18() {
    logLine();
    logInfo("18. Thermal Stress (LIVE + Manual)");

    Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    int status = i != null ? i.getIntExtra(BatteryManager.EXTRA_STATUS, -1) : -1;
    boolean charging = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);

    if (!charging) {
        logOk("Device is NOT charging. Plug charger and re-run Lab 18 to start LIVE thermal stress.");
    }

    // EXACT SCRIPT FROM YOUR PHOTOS
    logInfo("Manual Mode started.");
    logInfo("1) Run a heavy app (camera 4K / game / benchmark) for 5–10 minutes.");
    logInfo("2) While charging, watch if device becomes hot or throttles.");
    logWarn("If UI stutters, apps close, or phone gets very hot -> thermal throttling / PMIC stress.");
    logError("If device shuts down or reboots under load -> battery/PMIC/board heat fault suspected.");
    logOk("Manual Mode complete. If charging, you can start LIVE monitor for real-time map.");

    if (charging) showLab18ChargingPopup();
}

private void showLab18ChargingPopup() {
    AlertDialog.Builder b = new AlertDialog.Builder(this);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(20), dp(20), dp(20), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(Color.BLACK);
    bg.setCornerRadius(dp(20));
    bg.setStroke(dp(4), Color.parseColor("#FFD700"));
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("Press START for battery thermal test");
    title.setTextColor(Color.parseColor("#FFD700"));
    title.setGravity(Gravity.CENTER);
    title.setTextSize(17f);
    root.addView(title);

    Button btnStart = new Button(this);
    btnStart.setText("START");
    btnStart.setAllCaps(false);
    btnStart.setTextSize(16f);
    btnStart.setBackgroundColor(Color.parseColor("#FFD700"));
    btnStart.setTextColor(Color.BLACK);
    root.addView(btnStart, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(45)));

    TextView btnCancel = new TextView(this);
    btnCancel.setText("CANCEL");
    btnCancel.setTextColor(Color.parseColor("#00E5FF"));
    btnCancel.setGravity(Gravity.END);
    btnCancel.setPadding(0, dp(10), 0, 0);
    root.addView(btnCancel);

    b.setView(root);
    AlertDialog dialog = b.create();

    if (dialog.getWindow() != null)
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    dialog.show();

    btnStart.setOnClickListener(v -> {
        dialog.dismiss();
        showBatteryLiveMonitor();
    });

    btnCancel.setOnClickListener(v -> dialog.dismiss());
}


private void showBatteryLiveMonitor() {
    AlertDialog.Builder b = new AlertDialog.Builder(this);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(20), dp(20), dp(20), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(Color.BLACK);
    bg.setCornerRadius(dp(20));
    bg.setStroke(dp(4), Color.parseColor("#FFD700"));
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("Battery Temperature — LIVE");
    title.setTextColor(Color.parseColor("#FFD700"));
    title.setGravity(Gravity.CENTER);
    title.setTextSize(17f);
    root.addView(title);

    ScrollView sc = new ScrollView(this);
    TextView txt = new TextView(this);
    txt.setTextColor(Color.WHITE);
    txt.setTextSize(14f);
    txt.setPadding(0, dp(15), 0, 0);
    sc.addView(txt);
    root.addView(sc, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(260)
    ));

    TextView btnStop = new TextView(this);
    btnStop.setText("STOP");
    btnStop.setTextColor(Color.parseColor("#00E5FF"));
    btnStop.setGravity(Gravity.END);
    btnStop.setPadding(0, dp(15), 0, 0);
    root.addView(btnStop);

    b.setView(root);
    AlertDialog dlg = b.create();

    if (dlg.getWindow() != null)
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    lab18Running = true;
    dlg.show();

    btnStop.setOnClickListener(v -> {
        lab18Running = false;
        dlg.dismiss();
    });

    lab18Handler.post(new Runnable() {
        @Override public void run() {
            if (!lab18Running) return;

            float t = getBatteryTemperature();
            String line = String.format(Locale.US,
                    "Battery: %.1f°C\n%s\n\n", t, asciiBar(t));

            txt.append(line);
            sc.post(() -> sc.fullScroll(View.FOCUS_DOWN));

            lab18Handler.postDelayed(this, 1000);
        }
    });
}


private String asciiBar(float t) {
    int bars = Math.max(1, Math.min(50, (int)t));
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bars; i++) sb.append("█");
    return sb.toString();
}


// ============================================================
// THERMAL HELPERS (GEL UNIVERSAL AUTO-SCALE — FINAL EDITION)
// ============================================================
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

        String type = name;
        try {
            // Read zone type if available
            if (typeFile.exists()) {
                type = readFirstLine(typeFile);
                if (type == null || type.trim().isEmpty())
                    type = name;
            }

            // Read raw temperature
            String tRaw = readFirstLine(tempFile);
            if (tRaw == null) continue;

            float v = Float.parseFloat(tRaw.trim());

            // ============================================================
            // GEL UNIVERSAL AUTO-SCALE (fix for ALL Android devices)
            // ============================================================
            if (v > 1000f) {
                // millidegree → Pixel / Samsung / Huawei
                v = v / 1000f;
            } 
            else if (v > 200f) {
                // centidegree → Xiaomi / Redmi / POCO
                v = v / 100f;
            } 
            else if (v > 20f) {
                // deci-degree → some MediaTek devices
                v = v / 10f;
            }
            // else → already °C

            out.put(type.toLowerCase(Locale.US), v);

        } catch (Throwable ignore) {}
    }

    return out;
}

// ============================================================
// PICK ZONE
// ============================================================
private Float pickZone(Map<String, Float> zones, String... keys) {
    if (zones == null || zones.isEmpty()) return null;

    List<String> list = new ArrayList<>();
    for (String k : keys)
        if (k != null) list.add(k.toLowerCase(Locale.US));

    for (Map.Entry<String, Float> e : zones.entrySet()) {
        String z = e.getKey().toLowerCase(Locale.US);
        for (String k : list)
            if (z.equals(k) || z.contains(k))
                return e.getValue();
    }
    return null;
}

// ============================================================
// READ FIRST LINE
// ============================================================
private String readFirstLine(File file) throws IOException {
    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(file));
        return br.readLine();
    } finally {
        if (br != null) try { br.close(); } catch (Throwable ignore) {}
    }
}
    // ============================================================
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
    private void lab23ScreenLock() {
        logLine();
        logInfo("LAB 23 — Screen Lock / Biometrics Checklist (manual).");
        logInfo("1) Verify that the device has a secure lock method (PIN / pattern / password).");
        logWarn("If the device is left with no lock at all — higher risk for data and account theft.");
        logInfo("2) Test fingerprint / face unlock if configured, to confirm sensors respond consistently.");
    }

    private void lab24SecurityPatchManual() {
        logLine();
        logInfo("LAB 24 — Security Patch & Play Protect (manual).");
        logInfo("1) Open Android settings -> About phone -> Android version -> Security patch level.");
        logWarn("If the patch level is very old compared to current date — increased vulnerability risk.");
        logInfo("2) In Google Play Store -> Play Protect -> verify scanning is enabled and up to date.");
    }

    private void lab25DevOptions() {
        logLine();
        logInfo("LAB 25 — Developer Options / ADB Risk Note.");
        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            int dev = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
            logInfo("ADB enabled flag: " + (adb == 1));
            logInfo("Developer options enabled flag: " + (dev == 1));
            if (adb == 1)
                logWarn("ADB is enabled — only recommended for trusted environments and service use.");
        } catch (Exception e) {
            logWarn("Could not read Developer / ADB flags — OEM restrictions possible.");
        }
    }

    private void lab26RootSuspicion() {
        logLine();
        logInfo("LAB 26 — Root / Bootloader Suspicion Checklist (manual).");
        logInfo("Use this together with the automatic root labs:");
        logWarn("• Look for Magisk / SuperSU / custom recovery apps installed.");
        logWarn("• Check if boot animation or splash logo is non-stock for this model.");
        logError("If Play Integrity fails and root tools are visible — treat device as modified / rooted.");
    }

    // ============================================================
// LABS 27–30: ADVANCED / LOGS
// ============================================================

private void lab27CrashHistory() {
    logLine();
    logInfo("LAB 27 — Crash / Freeze History (interview).");
    logInfo("Ask the customer:");
    logInfo("• How often does the phone reboot or freeze per day/week?");
    logInfo("• Does it happen only in specific apps (camera, games, calls) or randomly?");
    logWarn("Frequent reboots in heavy apps only — could be thermal or RAM pressure.");
    logError("Random reboots even on idle — suspect deeper board / power / storage issues.");
}

private void lab28PermissionsPrivacy() {
    logLine();
    logInfo("LAB 28 — App Permissions & Privacy (manual).");
    logInfo("1) In Settings -> Privacy / Permissions, review apps with access to location, microphone and camera.");
    logWarn("Unknown apps with broad permissions can cause drain, slowdowns and privacy concerns.");
    logInfo("2) Recommend uninstalling unused or clearly suspicious apps.");
}

private void lab29CombineFindings() {
    logLine();
    logInfo("LAB 29 — Combine Auto-Diagnosis and Manual Labs.");
    logInfo("Use this step to correlate:");
    logInfo("• Auto-Diagnosis hardware flags (RAM, storage, battery, root, thermals)");
    logInfo("• Manual tests (audio, sensors, display, wireless, charger, user history).");
    logOk("The more labs you run, the closer the final diagnosis gets to a hospital-grade conclusion.");
}

private void lab30FinalNotes() {
    logLine();
    logInfo("LAB 30 — Final Service Notes for Report (manual).");
    logInfo("Write technician notes directly in the exported PDF/TXT:");
    logInfo("• Main findings (OK / WARN / ERROR).");
    logInfo("• Suspected faulty modules (board, battery, display, speaker, mic, sensors).");
    logInfo("• Recommended actions (cleaning, reset, part replacement, full board repair).");
    logOk("This completes the 30 Manual Labs set. Use it with Auto-Diagnosis for full GEL workflow.");
}

// ============================================================
// END OF CLASS
// ============================================================

}
