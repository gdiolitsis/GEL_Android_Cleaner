το// ============================================================
// ManualTestsActivity
// GEL Manual Tests — Hospital Edition (30 Manual Labs)
// Single-screen Accordion UI + detailed English service logs
// NOTE: Whole file is ready for copy-paste (GEL rule).
// ============================================================
package com.gel.cleaner;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.widget.CheckBox;
import android.content.res.ColorStateList;
import android.widget.SeekBar;
import android.widget.CompoundButton;
import androidx.appcompat.app.AlertDialog;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.Looper;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class ManualTestsActivity extends AppCompatActivity {

    private ScrollView scroll;
    private TextView txtLog;
    private Handler ui;

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

body3.addView(makeTestButton("11. Wi-Fi Link & RSSI Snapshot", this::lab11WifiSnapshot));
body3.addView(makeTestButton("12. Mobile Data / Airplane Mode Checklist", this::lab12MobileDataChecklist));
body3.addView(makeTestButton("13. Basic Call Test Guidelines", this::lab13CallGuidelines));
body3.addView(makeTestButton("14. Internet Access Quick Check", this::lab14InternetQuickCheck));

// ========== SECTION 4: BATTERY & THERMAL — LABS 15–18 ==========
LinearLayout body4 = makeSectionBody();
Button header4 = makeSectionHeader(getString(R.string.manual_cat_4), body4);
root.addView(header4);
root.addView(body4);

body4.addView(makeTestButton("15. Battery Level / Status Snapshot", this::lab15BatterySnapshot));
body4.addView(makeTestButton("16. Charging Port & Charger Inspection (manual)", this::lab16ChargingPortManual));
body4.addView(makeTestButton("17. Thermal Snapshot (CPU where available)", this::lab17ThermalSnapshot));
body4.addView(makeTestButton("18. Heat Under Load (manual questionnaire)", this::lab18ThermalQuestionnaire));

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

body6.addView(makeTestButton("23. Screen Lock / Biometrics Checklist", this::lab23ScreenLock));
body6.addView(makeTestButton("24. Security Patch & Play Protect (manual)", this::lab24SecurityPatchManual));
body6.addView(makeTestButton("25. Developer Options / ADB Risk Note", this::lab25DevOptions));
body6.addView(makeTestButton("26. Root / Bootloader Suspicion Checklist", this::lab26RootSuspicion));

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

txtLog.setText(
        Html.fromHtml("<b>" + getString(R.string.manual_log_title) + "</b><br>")
);

root.addView(txtLog);
scroll.addView(root);
setContentView(scroll);

// First log entry
GELServiceLog.clear();
logInfo(getString(R.string.manual_log_desc));
}   // ← ΚΛΕΙΝΕΙ ΤΟ onCreate()

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

    // 🔹 Expandable headers: NEON GREEN + CENTER
    private Button makeSectionHeader(String text, LinearLayout bodyToToggle) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(15f);
        b.setTextColor(0xFF39FF14); // NEON GREEN
        b.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(6), 0, dp(4));
        b.setLayoutParams(lp);
        b.setGravity(Gravity.CENTER); // center align
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

    // 🔹 Test buttons: WHITE TEXT + CENTER (ήδη όπως τα ήθελες)
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

    private void logLine() {
        GELServiceLog.addLine("────────────────────────────");
        appendHtml("<font color='#666666'>────────────────────────────</font>");
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
        logWarn("If volume is very low or muffled while speakerphone is OK → possible clogged mesh or earpiece wear.");
        logError("If there is absolutely no sound in earpiece but speakerphone works → earpiece or audio path fault.");
    }

    private void lab4MicManual() {
        logLine();
        logInfo("LAB 4 — Microphone Recording Check (manual).");
        logInfo("1) Open a voice recorder or send a voice message (WhatsApp / Viber etc.).");
        logInfo("2) Speak normally near the main microphone (bottom edge of the phone).");
        logInfo("3) Play back the recording and compare with a reference device if possible.");
        logWarn("If sound is very low / noisy / 'underwater' → suspect microphone hole clogged, mesh or early mic damage.");
        logError("If recording is totally silent on all apps → strong indication of microphone / audio IC / flex failure.");
    }

    private void lab5Vibration() {
        logLine();
        logInfo("LAB 5 — Vibration Motor Test (short one-shot).");
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) {
                logError("No Vibrator service reported — either missing hardware or framework issue.");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //noinspection deprecation
                v.vibrate(800);
            }
            logOk("If a strong vibration was felt, motor and driver are basically OK.");
            logError("If no vibration was felt at all, suspect vibrator motor, contacts or flex damage.");
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
        logError("If there are dead touch zones or ghost touches → digitizer / touch controller problem.");
    }

    private void lab7RotationManual() {
        logLine();
        logInfo("LAB 7 — Rotation / Auto-Rotate Check (manual).");
        logInfo("1) Make sure Auto-Rotate is enabled in Quick Settings.");
        logInfo("2) Open an app that supports rotation (gallery, browser, YouTube).");
        logWarn("If the UI never rotates despite Auto-Rotate ON → suspect accelerometer failure or sensor-service bug.");
        logInfo("If rotation works only after reboot → possible software/ROM issue, not pure hardware.");
    }

    private void lab8ProximityCall() {
        logLine();
        logInfo("LAB 8 — Proximity During Call (manual).");
        logInfo("1) Start a normal call and bring the phone to the ear.");
        logInfo("2) The display MUST turn off when the proximity area is covered.");
        logError("If the screen stays ON near the ear → proximity sensor or glass / protector alignment problem.");
        logWarn("If the screen turns off but sometimes does not wake properly → mix of software and sensor edge-cases.");
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
            for (Sensor s : sensors) {
                String line = "• type=" + s.getType()
                        + " | name=" + s.getName()
                        + " | vendor=" + s.getVendor();
                logInfo(line);
            }
            logOk("Sensor list captured for the final service report.");
        } catch (Exception e) {
            logError("Full Sensor List error: " + e.getMessage());
        }
    }

    private void checkSensor(SensorManager sm, int type, String name) {
        boolean ok = sm.getDefaultSensor(type) != null;
        if (ok) logOk(name + " is reported as available.");
        else logWarn(name + " is NOT reported — features depending on it will be limited or missing.");
    }

    // ============================================================
// LABS 11–14: WIRELESS & CONNECTIVITY
// ============================================================

private void lab11WifiSnapshot() {
    logLine();
    logInfo("LAB 11 — Wi-Fi Link & RSSI Snapshot.");
    try {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm == null) {
            logError("WifiManager not available.");
            return;
        }
        if (!wm.isWifiEnabled()) {
            logWarn("Wi-Fi is currently disabled.");
            return;
        }
        WifiInfo info = wm.getConnectionInfo();
        if (info == null || info.getNetworkId() == -1) {
            logWarn("Wi-Fi enabled but not connected to any access point.");
            return;
        }
        int rssi = info.getRssi();
        int linkSpeed = info.getLinkSpeed();
        logInfo("SSID: " + info.getSSID());
        logInfo("RSSI: " + rssi + " dBm");
        logInfo("Link speed: " + linkSpeed + " Mbps");
        if (rssi > -65) logOk("Wi-Fi signal is strong for normal use.");
        else if (rssi > -80) logWarn("Wi-Fi signal is moderate — possible instability further away.");
        else logError("Wi-Fi signal is very weak — disconnections and low speeds expected.");
    } catch (Exception e) {
        logError("Wi-Fi snapshot error: " + e.getMessage());
    }
}

private void lab12MobileDataChecklist() {
    logLine();
    logInfo("LAB 12 — Mobile Data / Airplane Mode Checklist (manual).");
    logInfo("1) Check that Airplane mode is OFF and mobile data is enabled.");
    logInfo("2) Ensure a valid SIM with active data plan is inserted.");
    logWarn("If the device shows signal bars but mobile data never works → APN / carrier or modem issue.");
    logError("If there is no mobile network at all in known-good coverage → SIM, antenna or baseband problem.");
}

private void lab13CallGuidelines() {
    logLine();
    logInfo("LAB 13 — Basic Call Test Guidelines (manual).");
    logInfo("1) Place a normal call to a known-good number.");
    logInfo("2) Verify both directions: you hear the remote side AND they hear you clearly.");
    logWarn("If only one direction fails → isolate between earpiece vs microphone path.");
    logError("If calls always drop or never connect while data works → core telephony / carrier registration issue.");
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
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) transport = "Wi-Fi";
                else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) transport = "Cellular";
            }
        } else {
            @SuppressWarnings("deprecation")
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                hasInternet = true;
                transport = ni.getTypeName();
            }
        }

        if (!hasInternet) logError("No active Internet connection detected at OS level.");
        else logOk("Internet connectivity is reported as active (" + transport + ").");

    } catch (Exception e) {
        logError("Internet quick check error: " + e.getMessage());
    }
    

// ============================================================
// LABS 15–18: BATTERY & THERMAL  (GEL Edition + Battery Health %)
// ============================================================
private void lab15BatterySnapshot() {
    logLine();
    logInfo("LAB 15 — Battery Level / Status / Health Snapshot.");

    try {
        IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent i = registerReceiver(null, f);
        if (i == null) {
            logWarn("Battery broadcast not available.");
            return;
        }

        int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float pct = (scale > 0) ? (100f * level / scale) : -1f;

        int status = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int temp10 = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        float temp = (temp10 > 0) ? (temp10 / 10f) : -1f;

        // --- BASIC INFO ---
        logInfo(String.format(Locale.US, "Battery level: %.1f%%", pct));
        logInfo(String.format(Locale.US, "Battery temperature: %.1f°C", temp));

        String statusStr;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:      statusStr = "Charging"; break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:   statusStr = "Discharging"; break;
            case BatteryManager.BATTERY_STATUS_FULL:          statusStr = "Full"; break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:  statusStr = "Not charging"; break;
            default: statusStr = "Unknown";
        }
        logInfo("Battery status: " + statusStr);

        // --- BATTERY HEALTH % (NEW GEL METHOD) ---
        int healthPct = getBatteryHealthPercent();
        if (healthPct > 0) {
            if (healthPct >= 90)
                logOk("Estimated Battery Health: " + healthPct + "% (Excellent)");
            else if (healthPct >= 80)
                logOk("Estimated Battery Health: " + healthPct + "% (Normal)");
            else if (healthPct >= 60)
                logWarn("Estimated Battery Health: " + healthPct + "% (Worn)");
            else
                logError("Estimated Battery Health: " + healthPct + "% (Poor)");
        } else {
            logWarn("Battery Health % not supported on this device.");
        }

        if (pct >= 0 && pct <= 5)
            logError("Battery almost empty — high risk of sudden shutdown.");
        else if (pct <= 15)
            logWarn("Battery low — recommend charging before diagnostics.");

        if (temp > 45f)
            logError("Battery temperature above 45°C — possible thermal problem.");

        // UI Popup for live drain test
        showBatteryHealthTestDialog();

    } catch (Exception e) {
        logError("Battery snapshot error: " + e.getMessage());
    }
}

// ============================================================
// INTERNAL: Battery Health Calculator (GEL Formula)
// ============================================================
private int getBatteryHealthPercent() {
    try {
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (bm == null) return -1;

        // mAh right now
        long chargeCounter = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
        // % right now
        long capacityPct = bm.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if (chargeCounter <= 0 || capacityPct <= 0) return -1;

        // Estimate real full capacity in mAh
        float fullMah = (chargeCounter / (capacityPct / 100f));

        // Try DESIGN CAPACITY (some phones expose it)
        long designMah = bm.getLongProperty(0x00000008); // hidden ID, works on many devices

        if (designMah <= 0) {
            // Fallback typical value (better than nothing)
            designMah = (long) fullMah;
        }

        int est = (int) ((fullMah / designMah) * 100f);
        if (est > 100) est = 100;
        if (est < 1) est = 1;

        return est;

    } catch (Exception e) {
        return -1;
    }
}

// ============================================================
// BATTERY HEALTH STRESS TEST POPUP
// ============================================================
private void showBatteryHealthTestDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(true);
    builder.setTitle("Start Battery Health Test");

    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    int pad = dp(16);
    layout.setPadding(pad, pad, pad, pad);

    // --- Description text ---
    TextView info = new TextView(this);
    info.setText("This live test watches battery % while the screen stays ON.\nUse STANDARD for quick checks, PRO for deeper checks.");
    info.setTextSize(13f);
    info.setTextColor(0xFFFFFFFF);
    info.setPadding(0, 0, 0, dp(8));
    layout.addView(info);

    // --- Mode label ---
    TextView modeLabel = new TextView(this);
    modeLabel.setText("Select mode:");
    modeLabel.setTextSize(13f);
    modeLabel.setTextColor(0xFFFFD700);
    layout.addView(modeLabel);

    LinearLayout modeRow = new LinearLayout(this);
    modeRow.setOrientation(LinearLayout.HORIZONTAL);

    // --- Checkboxes
    final CheckBox chkStandard = new CheckBox(this);
    chkStandard.setText("STANDARD 10–60 sec");
    chkStandard.setTextColor(0xFFFFFFFF);
    chkStandard.setButtonTintList(ColorStateList.valueOf(0xFFFFD700));
    chkStandard.setChecked(true);
    modeRow.addView(chkStandard);

    final CheckBox chkPro = new CheckBox(this);
    chkPro.setText("PRO 60–120 sec");
    chkPro.setTextColor(0xFFFFFFFF);
    chkPro.setButtonTintList(ColorStateList.valueOf(0xFFFFD700));
    modeRow.addView(chkPro);

    layout.addView(modeRow);

    // --- Duration label
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
    seek.setMax(50);
    layout.addView(seek);

    final boolean[] proMode = new boolean[]{false};

    CompoundButton.OnCheckedChangeListener modeListener = (btn, isChecked) -> {
        if (!isChecked) {
            if (!chkStandard.isChecked() && !chkPro.isChecked()) {
                btn.setChecked(true);
                return;
            }
        }

        if (btn == chkStandard && isChecked) {
            chkPro.setChecked(false);
            proMode[0] = false;
            seek.setMax(50);
            if (seek.getProgress() == 0) seek.setProgress(20);
        } else if (btn == chkPro && isChecked) {
            chkStandard.setChecked(false);
            proMode[0] = true;
            seek.setMax(60);
            if (seek.getProgress() == 0) seek.setProgress(30);
        }

        updateDurationLabelForSeek(seek, durValue, proMode[0]);
    };

    chkStandard.setOnCheckedChangeListener(modeListener);
    chkPro.setOnCheckedChangeListener(modeListener);

    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
            updateDurationLabelForSeek(sb, durValue, proMode[0]);
        }
        @Override public void onStartTrackingTouch(SeekBar sb) {}
        @Override public void onStopTrackingTouch(SeekBar sb) {}
    });

    seek.setProgress(20);
    updateDurationLabelForSeek(seek, durValue, false);

    // --- START BTN
    Button start = new Button(this);
    start.setText("Start Battery Health Test");
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
        int progress = seek.getProgress();
        int durationSec = proMode[0] ? (60 + progress) : (10 + progress);
        dialog.dismiss();
        runBatteryHealthTest(durationSec, proMode[0]);
    });

    dialog.show();
}

private void updateDurationLabelForSeek(SeekBar seek, TextView label, boolean proMode) {
    int progress = seek.getProgress();
    int seconds = proMode ? (60 + progress) : (10 + progress);
    String range = proMode ? "60–120 seconds (pro mode)" : "10–60 seconds (standard mode)";
    label.setText("Selected: " + seconds + " sec\n" + range);
}

private void runBatteryHealthTest(int durationSec, boolean proMode) {
    float startPct = getCurrentBatteryPercent();
    if (startPct < 0f) {
        logWarn("Battery Health Test: unable to read initial battery level.");
        return;
    }

    long startTime = SystemClock.elapsedRealtime();

    logLine();
    logInfo("Battery Health Test started for " + durationSec + " seconds "
            + (proMode ? "(PRO mode)." : "(standard mode)."));

    ui.postDelayed(() -> {
        float endPct = getCurrentBatteryPercent();
        if (endPct < 0f) {
            logWarn("Battery Health Test: unable to read final battery level.");
            return;
        }

        long endTime = SystemClock.elapsedRealtime();
        long dtMs = endTime - startTime;
        if (dtMs <= 0) dtMs = durationSec * 1000L;

        float delta = startPct - endPct;
        float perHour = (delta * 3600000f) / dtMs;

        logInfo(String.format(Locale.US,
                "Battery test: start=%.1f%%, end=%.1f%%, Δ=%.2f%% over %.1f sec.",
                startPct, endPct, delta, dtMs / 1000f));

        if (delta <= 0.1f)
            logOk("Almost zero drain — battery looks normal.");
        else if (perHour <= 10f)
            logOk(String.format(Locale.US, "Estimated drain ≈ %.1f%%/hour — acceptable.", perHour));
        else if (perHour <= 20f)
            logWarn(String.format(Locale.US, "Estimated drain ≈ %.1f%%/hour — borderline.", perHour));
        else
            logError(String.format(Locale.US, "Estimated drain ≈ %.1f%%/hour — heavy load or battery wear."));

    }, durationSec * 1000L);
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
        logWarn("If the device is left with no lock at all → higher risk for data and account theft.");
        logInfo("2) Test fingerprint / face unlock if configured, to confirm sensors respond consistently.");
    }

    private void lab24SecurityPatchManual() {
        logLine();
        logInfo("LAB 24 — Security Patch & Play Protect (manual).");
        logInfo("1) Open Android settings → About phone → Android version → Security patch level.");
        logWarn("If the patch level is very old compared to current date → increased vulnerability risk.");
        logInfo("2) In Google Play Store → Play Protect → verify scanning is enabled and up to date.");
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
        logError("If SafetyNet / Play Integrity fails and root tools are visible → treat device as modified / rooted.");
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
        logWarn("Frequent reboots in heavy apps only → could be thermal or RAM pressure.");
        logError("Random reboots even on idle → suspect deeper board / power / storage issues.");
    }

    private void lab28PermissionsPrivacy() {
        logLine();
        logInfo("LAB 28 — App Permissions & Privacy (manual).");
        logInfo("1) In Settings → Privacy / Permissions, review apps with access to location, microphone and camera.");
        logWarn("Unknown apps with broad permissions can cause drain, slowdowns and privacy concerns.");
        logInfo("2) Recommend uninstalling unused or clearly suspicious apps.");
    }

    private void lab29CombineFindings() {
        logLine();
        logInfo("LAB 29 — Combine Auto-Diagnosis and Manual Labs.");
        logInfo("Use this step to correlate:");
        logInfo("• Auto-Diagnosis hardware flags (RAM, storage, battery, root, thermals)");
        logInfo("• Manual tests (audio, sensors, display, wireless, charger, user history).");
        logOk("The more labs you run, the closer the final diagnosis gets to a 'hospital-grade' conclusion.");
    }

    private void lab30FinalNotes() {
        logLine();
        logInfo("LAB 30 — Final Service Notes for Report (manual).");
        logInfo("Write technician notes directly in the exported PDF/TXT:");
        logInfo("• Main findings (OK / WARN / ERROR).");
        logInfo("• Suspected faulty modules (board, battery, display, speaker, mic, sensors).");
        logInfo("• Recommended actions (cleaning, reset, part replacement, full board repair).");
        logOk("This completes the 30 Manual Labs set. Use it together with Auto-Diagnosis for a full GEL Service workflow.");
    }
}
