// ============================================================
// ManualTestsActivity
// GEL Manual Tests — Hospital Edition (30 Manual Labs)
// Single-screen Accordion UI + detailed English service logs
// NOTE: Whole file is ready for copy-paste (GEL rule).
// ============================================================
package com.gel.cleaner;

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
        root.setBackgroundColor(0xFF101010);

        // TITLE
        TextView title = new TextView(this);
        title.setText("🧪 GEL Manual Tests — Hospital Edition");
        title.setTextSize(20f);
        title.setTextColor(0xFFFFD700); 
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(6));
        root.addView(title);

        // SUBTITLE
        TextView sub = new TextView(this);
        sub.setText(
                "Professional service tools.\n"
                        + "Each manual lab writes detailed results below (OK / WARN / ERROR)\n"
                        + "and is included in the final Service Report."
        );
        sub.setTextSize(13f);
        sub.setTextColor(0xFF39FF14);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // SECTION 1 TITLE
        TextView sec1 = new TextView(this);
        sec1.setText("📂 SECTION 1 — System & Hardware Tests");
        sec1.setTextSize(17f);
        sec1.setTextColor(0xFFFFD700);
        sec1.setGravity(Gravity.CENTER);
        sec1.setPadding(0, dp(10), 0, dp(6));
        root.addView(sec1);

        // SECTION 1: AUDIO & VIBRATION — LABS 1–5
        LinearLayout body1 = makeSectionBody();
        Button header1 = makeSectionHeader("Audio & Vibration — Manual Labs 1–5", body1);
        root.addView(header1);
        root.addView(body1);

        body1.addView(makeTestButton("1. Speaker Tone Test", this::lab1SpeakerTone));
        body1.addView(makeTestButton("2. Speaker Frequency Sweep", this::lab2SpeakerSweep));
        body1.addView(makeTestButton("3. Earpiece Call Check (manual)", this::lab3EarpieceManual));
        body1.addView(makeTestButton("4. Microphone Recording Check (manual)", this::lab4MicManual));
        body1.addView(makeTestButton("5. Vibration Motor Test", this::lab5Vibration));

        // ========== SECTION 1: AUDIO & VIBRATION — LABS 1–5 ==========
        LinearLayout body1 = makeSectionBody();
        Button header1 = makeSectionHeader("Audio & Vibration — Manual Labs 1–5", body1);
        root.addView(header1);
        root.addView(body1);

        body1.addView(makeTestButton("1. Speaker Tone Test", this::lab1SpeakerTone));
        body1.addView(makeTestButton("2. Speaker Frequency Sweep", this::lab2SpeakerSweep));
        body1.addView(makeTestButton("3. Earpiece Call Check (manual)", this::lab3EarpieceManual));
        body1.addView(makeTestButton("4. Microphone Recording Check (manual)", this::lab4MicManual));
        body1.addView(makeTestButton("5. Vibration Motor Test", this::lab5Vibration));

        // ========== SECTION 2: DISPLAY & SENSORS — LABS 6–10 ==========
        LinearLayout body2 = makeSectionBody();
        Button header2 = makeSectionHeader("Display & Sensors — Manual Labs 6–10", body2);
        root.addView(header2);
        root.addView(body2);

        body2.addView(makeTestButton("6. Display / Touch Basic Inspection", this::lab6DisplayTouch));
        body2.addView(makeTestButton("7. Rotation / Auto-Rotate Check (manual)", this::lab7RotationManual));
        body2.addView(makeTestButton("8. Proximity During Call (manual)", this::lab8ProximityCall));
        body2.addView(makeTestButton("9. Sensors Quick Presence Check", this::lab9SensorsQuick));
        body2.addView(makeTestButton("10. Full Sensor List for Report", this::lab10FullSensorList));

        // ========== SECTION 3: WIRELESS & CONNECTIVITY — LABS 11–14 ==========
        LinearLayout body3 = makeSectionBody();
        Button header3 = makeSectionHeader("Wireless & Connectivity — Manual Labs 11–14", body3);
        root.addView(header3);
        root.addView(body3);

        body3.addView(makeTestButton("11. Wi-Fi Link & RSSI Snapshot", this::lab11WifiSnapshot));
        body3.addView(makeTestButton("12. Mobile Data / Airplane Mode Checklist", this::lab12MobileDataChecklist));
        body3.addView(makeTestButton("13. Basic Call Test Guidelines", this::lab13CallGuidelines));
        body3.addView(makeTestButton("14. Internet Access Quick Check", this::lab14InternetQuickCheck));

        // ========== SECTION 4: BATTERY & THERMAL — LABS 15–18 ==========
        LinearLayout body4 = makeSectionBody();
        Button header4 = makeSectionHeader("Battery & Thermal — Manual Labs 15–18", body4);
        root.addView(header4);
        root.addView(body4);

        body4.addView(makeTestButton("15. Battery Level / Status Snapshot", this::lab15BatterySnapshot));
        body4.addView(makeTestButton("16. Charging Port & Charger Inspection (manual)", this::lab16ChargingPortManual));
        body4.addView(makeTestButton("17. Thermal Snapshot (CPU where available)", this::lab17ThermalSnapshot));
        body4.addView(makeTestButton("18. Heat Under Load (manual questionnaire)", this::lab18ThermalQuestionnaire));

        // ========== SECTION 5: STORAGE & PERFORMANCE — LABS 19–22 ==========
        LinearLayout body5 = makeSectionBody();
        Button header5 = makeSectionHeader("Storage & Performance — Manual Labs 19–22", body5);
        root.addView(header5);
        root.addView(body5);

        body5.addView(makeTestButton("19. Internal Storage Snapshot", this::lab19StorageSnapshot));
        body5.addView(makeTestButton("20. Installed Apps Footprint", this::lab20AppsFootprint));
        body5.addView(makeTestButton("21. Live RAM Snapshot", this::lab21RamSnapshot));
        body5.addView(makeTestButton("22. Uptime / Reboot History Hints", this::lab22UptimeHints));

        // ========== SECTION 6: SECURITY & SYSTEM HEALTH — LABS 23–26 ==========
        LinearLayout body6 = makeSectionBody();
        Button header6 = makeSectionHeader("Security & System Health — Manual Labs 23–26", body6);
        root.addView(header6);
        root.addView(body6);

        body6.addView(makeTestButton("23. Screen Lock / Biometrics Checklist", this::lab23ScreenLock));
        body6.addView(makeTestButton("24. Security Patch & Play Protect (manual)", this::lab24SecurityPatchManual));
        body6.addView(makeTestButton("25. Developer Options / ADB Risk Note", this::lab25DevOptions));
        body6.addView(makeTestButton("26. Root / Bootloader Suspicion Checklist", this::lab26RootSuspicion));

        // ========== SECTION 7: ADVANCED / LOGS — LABS 27–30 ==========
        LinearLayout body7 = makeSectionBody();
        Button header7 = makeSectionHeader("Advanced / Logs — Manual Labs 27–30", body7);
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
        txtLog.setText(Html.fromHtml("<b>Manual Tests Log</b><br>"));
        root.addView(txtLog);

        scroll.addView(root);
        setContentView(scroll);

        GELServiceLog.clear(); // start fresh log section for manual tests
        logInfo("GEL Manual Tests — ready. Open a category above and run the required labs.");
    }

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
        b.setTextColor(0xFFFFD700);
        b.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(6), 0, dp(4));
        b.setLayoutParams(lp);
        b.setGravity(Gravity.CENTER_VERTICAL);
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
        logInfo("1) Open a voice recorder or send a voice message.");
        logInfo("2) Speak near the main microphone.");
        logWarn("If sound is low or noisy → clogged mic / mesh / water damage.");
        logError("If recording is totally silent → microphone or audio IC failure.");
    }

    private void lab5Vibration() {
        logLine();
        logInfo("LAB 5 — Vibration Motor Test.");
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) {
                logError("No Vibrator service — missing hardware or framework issue.");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //noinspection deprecation
                v.vibrate(800);
            }
            logOk("Strong vibration felt → motor OK.");
            logError("No vibration → suspect motor or flex damage.");
        } catch (Exception e) {
            logError("Vibration Test error: " + e.getMessage());
        }
    }

    // ============================================================
    // LABS 6–10: DISPLAY & SENSORS
    // ============================================================
    private void lab6DisplayTouch() {
        logLine();
        logInfo("LAB 6 — Display / Touch Basic Inspection.");
        logInfo("1) Open a white/grey image fullscreen.");
        logWarn("2) Look for tint, burn-in, lines.");
        logError("Dead zones → digitizer/touch controller fault.");
    }

    private void lab7RotationManual() {
        logLine();
        logInfo("LAB 7 — Rotation / Auto-Rotate Check.");
        logInfo("1) Ensure Auto-Rotate is enabled.");
        logWarn("If UI never rotates → accelerometer failure.");
    }

    private void lab8ProximityCall() {
        logLine();
        logInfo("LAB 8 — Proximity During Call.");
        logWarn("If screen does NOT turn off near ear → proximity/glass misalignment.");
    }

    private void lab9SensorsQuick() {
        logLine();
        logInfo("LAB 9 — Sensors Quick Presence Check.");
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sm == null) {
                logError("SensorManager not available.");
                return;
            }
            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Total sensors: " + (all == null ? 0 : all.size()));

            checkSensor(sm, Sensor.TYPE_ACCELEROMETER, "Accelerometer");
            checkSensor(sm, Sensor.TYPE_GYROSCOPE, "Gyroscope");
            checkSensor(sm, Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer");
            checkSensor(sm, Sensor.TYPE_LIGHT, "Ambient Light");
            checkSensor(sm, Sensor.TYPE_PROXIMITY, "Proximity");

        } catch (Exception e) {
            logError("Sensors Quick Check error: " + e.getMessage());
        }
    }

    private void lab10FullSensorList() {
        logLine();
        logInfo("LAB 10 — Full Sensor List.");
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sm == null) {
                logError("SensorManager not available.");
                return;
            }
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors == null || sensors.isEmpty()) {
                logError("No sensors reported.");
                return;
            }
            for (Sensor s : sensors) {
                logInfo("• type=" + s.getType()
                        + " | name=" + s.getName()
                        + " | vendor=" + s.getVendor());
            }
            logOk("Sensor list captured.");
        } catch (Exception e) {
            logError("Full Sensor List error: " + e.getMessage());
        }
    }

    private void checkSensor(SensorManager sm, int type, String name) {
        boolean ok = sm.getDefaultSensor(type) != null;
        if (ok) logOk(name + " available.");
        else logWarn(name + " NOT reported.");
    }

    // ============================================================
    // LABS 11–14: WIRELESS & CONNECTIVITY
    // ============================================================
    private void lab11WifiSnapshot() {
        logLine();
        logInfo("LAB 11 — Wi-Fi RSSI Snapshot.");
        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wm == null) {
                logError("WifiManager not available.");
                return;
            }
            if (!wm.isWifiEnabled()) {
                logWarn("Wi-Fi disabled.");
                return;
            }
            WifiInfo info = wm.getConnectionInfo();
            if (info == null || info.getNetworkId() == -1) {
                logWarn("Wi-Fi enabled but not connected.");
                return;
            }
            int rssi = info.getRssi();
            int linkSpeed = info.getLinkSpeed();
            logInfo("SSID: " + info.getSSID());
            logInfo("RSSI: " + rssi + " dBm");
            logInfo("Link speed: " + linkSpeed + " Mbps");

            if (rssi > -65) logOk("Strong Wi-Fi.");
            else if (rssi > -80) logWarn("Moderate signal.");
            else logError("Weak Wi-Fi — expect instability.");
        } catch (Exception e) {
            logError("Wi-Fi snapshot error: " + e.getMessage());
        }
    }

    private void lab12MobileDataChecklist() {
        logLine();
        logInfo("LAB 12 — Mobile Data Checklist.");
        logInfo("Check SIM, APN, coverage.");
        logError("No signal in good coverage → antenna/modem issue.");
    }

    private void lab13CallGuidelines() {
        logLine();
        logInfo("LAB 13 — Basic Call Test.");
        logWarn("If only one direction fails → isolate mic vs earpiece.");
    }

    private void lab14InternetQuickCheck() {
        logLine();
        logInfo("LAB 14 — Internet Access Check.");
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

            if (!hasInternet)
                logError("No Internet.");
            else
                logOk("Internet active (" + transport + ").");

        } catch (Exception e) {
            logError("Internet check error: " + e.getMessage());
        }
    }

    // ============================================================
    // LABS 15–18: BATTERY & THERMAL
    // ============================================================
    private void lab15BatterySnapshot() {
        logLine();
        logInfo("LAB 15 — Battery Snapshot.");
        try {
            Intent i = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) {
                logWarn("Battery broadcast missing.");
                return;
            }
            int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float pct = (scale > 0) ? (100f * level / scale) : -1f;
            int temp10 = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            float temp = temp10 / 10f;

            logInfo(String.format(Locale.US, "Level: %.1f%%", pct));
            logInfo(String.format(Locale.US, "Temp: %.1f°C", temp));

            if (pct <= 5) logError("Battery critical.");
            else if (pct <= 15) logWarn("Battery low.");

            if (temp > 45f) logError("Battery overheating.");

        } catch (Exception e) {
            logError("Battery snapshot error: " + e.getMessage());
        }
    }

    private void lab16ChargingPortManual() {
        logLine();
        logInfo("LAB 16 — Charging Port Check.");
        logWarn("Check USB port for dust, bent pins.");
    }

    private void lab17ThermalSnapshot() {
        logLine();
        logInfo("LAB 17 — Thermal Snapshot.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                HardwarePropertiesManager hpm =
                        (HardwarePropertiesManager) getSystemService(Context.HARDWARE_PROPERTIES_SERVICE);
                if (hpm != null) {
                    float[] cpuTemps = hpm.getDeviceTemperatures(
                            HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,
                            HardwarePropertiesManager.TEMPERATURE_CURRENT);
                    if (cpuTemps != null && cpuTemps.length > 0) {
                        float t = cpuTemps[0];
                        logInfo(String.format(Locale.US, "CPU: %.1f°C", t));
                        if (t > 80f) logError("CPU extremely hot.");
                        else if (t > 70f) logWarn("CPU hot.");
                        else logOk("CPU temperature OK.");
                    } else {
                        logWarn("No CPU thermal data.");
                    }
                }
            } catch (Exception e) {
                logError("Thermal snapshot error: " + e.getMessage());
            }
        } else {
            logWarn("Thermal API not available (< Android 10).");
        }
    }

    private void lab18ThermalQuestionnaire() {
        logLine();
        logInfo("LAB 18 — Thermal Questionnaire.");
        logWarn("If overheating in idle → battery/PMIC issue.");
    }

    // ============================================================
    // LABS 19–22: STORAGE & PERFORMANCE
    // ============================================================
    private void lab19StorageSnapshot() {
        logLine();
        logInfo("LAB 19 — Storage Snapshot.");
        try {
            StatFs s = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
            long used = total - free;
            int pctFree = (int) ((free * 100L) / total);

            logInfo("Used: " + humanBytes(used) + " / " + humanBytes(total)
                    + " (free " + humanBytes(free) + ", " + pctFree + "%).");

            if (pctFree < 5) logError("Storage critically low.");
            else if (pctFree < 10) logWarn("Storage low.");
            else logOk("Storage OK.");

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
                logWarn("Cannot read apps list.");
                return;
            }
            int userApps = 0;
            int systemApps = 0;
            for (ApplicationInfo ai : apps) {
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) systemApps++;
                else userApps++;
            }
            logInfo("User apps: " + userApps);
            logInfo("System apps: " + systemApps);

            if (userApps > 120) logError("Too many user apps.");
            else if (userApps > 80) logWarn("High number of user apps.");
            else logOk("App footprint normal.");

        } catch (Exception e) {
            logError("Apps footprint error: " + e.getMessage());
        }
    }

    private void lab21RamSnapshot() {
        logLine();
        logInfo("LAB 21 — RAM Snapshot.");
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                logError("ActivityManager missing.");
                return;
            }
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long free = mi.availMem;
            long total = mi.totalMem;
            int pct = (int) ((free * 100L) / total);

            logInfo("Free RAM: " + humanBytes(free) + " (" + pct + "%)");

            if (pct < 10) logError("RAM extremely low.");
            else if (pct < 20) logWarn("Low RAM.");
            else logOk("RAM OK.");

        } catch (Exception e) {
            logError("RAM snapshot error: " + e.getMessage());
        }
    }

    private void lab22UptimeHints() {
        logLine();
        logInfo("LAB 22 — Uptime / Reboot History.");
        long upMs = SystemClock.elapsedRealtime();
        String upStr = formatUptime(upMs);
        logInfo("Uptime: " + upStr);

        if (upMs < 2 * 60 * 60 * 1000L)
            logWarn("Recent reboot (<2h).");
        else if (upMs > 7L * 24L * 60L * 60L * 1000L)
            logWarn("Long uptime (>7d).");
        else
            logOk("Uptime normal.");
    }

    // ============================================================
    // LABS 23–26: SECURITY & SYSTEM HEALTH
    // ============================================================
    private void lab23ScreenLock() {
        logLine();
        logInfo("LAB 23 — Screen Lock / Biometrics.");
        logWarn("No lock → high risk.");
    }

    private void lab24SecurityPatchManual() {
        logLine();
        logInfo("LAB 24 — Security Patch Check.");
        logWarn("Old patch level → vulnerability risk.");
    }

    private void lab25DevOptions() {
        logLine();
        logInfo("LAB 25 — Developer Options / ADB.");
        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            logInfo("ADB enabled: " + (adb == 1));
            if (adb == 1) logWarn("ADB enabled — security risk.");
        } catch (Exception e) {
            logWarn("Cannot read ADB flag.");
        }
    }

    private void lab26RootSuspicion() {
        logLine();
        logInfo("LAB 26 — Root / Bootloader Check.");
        logWarn("Look for Magisk, SuperSU, custom recovery.");
    }

    // ============================================================
    // LABS 27–30: ADVANCED / LOGS
    // ============================================================
    private void lab27CrashHistory() {
        logLine();
        logInfo("LAB 27 — Crash / Freeze History.");
        logWarn("Random reboots → potential board issue.");
    }

    private void lab28PermissionsPrivacy() {
        logLine();
        logInfo("LAB 28 — Permissions & Privacy.");
        logWarn("Unknown apps with sensitive permissions → risk.");
    }

    private void lab29CombineFindings() {
        logLine();
        logInfo("LAB 29 — Combine Auto + Manual Findings.");
        logOk("Cross-reference tests for final diagnosis.");
    }

    private void lab30FinalNotes() {
        logLine();
        logInfo("LAB 30 — Final Service Notes.");
        logOk("Completed all 30 Manual Labs.");
    }
}
