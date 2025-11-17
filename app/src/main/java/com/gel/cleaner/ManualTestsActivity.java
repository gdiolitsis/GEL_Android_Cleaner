// ============================================================
// ManualTestsActivity
// GEL Manual Tests — Hospital Edition (Accordion UI)
// Classic vertical accordion sections, all logs in ENGLISH.
// Whole file is ready for copy-paste into GitHub (GEL rule).
// ============================================================
package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HardwarePropertiesManager;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Html;
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

// ============================================================
// GEL Manual Tests — Hospital Edition (Accordion Service UI)
// Professional manual tests with full GELServiceLog export.
// ============================================================
public class ManualTestsActivity extends AppCompatActivity {

    private TextView txtLog;
    private ScrollView scroll;
    private Handler ui;

    // Small helper interface for accordion content builders
    private interface SectionBuilder {
        void build(LinearLayout content);
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
        root.setBackgroundColor(0xFF101010);

        // TITLE
        TextView title = new TextView(this);
        title.setText("🧪 GEL Manual Tests — Hospital Edition");
        title.setTextSize(20f);
        title.setTextColor(0xFFFFD700); // Dark-Gold header
        title.setPadding(0, 0, 0, dp(6));
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Professional service tools.\n" +
                "Each test writes detailed results below (OK / WARN / ERROR)\n" +
                "and is included in the final Service Report.");
        sub.setTextSize(13f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // ========================================================
        // ACCORDION SECTIONS (Classic Service Style)
        // ========================================================

        // 1) Audio / Vibration — Manual Labs 1–5
        addAccordionSection(root,
                "Audio / Vibration — Manual Labs 1–5",
                new SectionBuilder() {
                    @Override
                    public void build(LinearLayout content) {
                        content.addView(makeButton("1. Speaker Tone Test", ManualTestsActivity.this::testSpeaker));
                        content.addView(makeButton("2. Speaker Sweep Test", ManualTestsActivity.this::testSpeakerSweep));
                        content.addView(makeButton("3. Earpiece Basic Check (manual)", ManualTestsActivity.this::testEarpieceExplain));
                        content.addView(makeButton("4. Mic Manual Check (manual)", ManualTestsActivity.this::testMicManualInfo));
                        content.addView(makeButton("5. Vibration Motor Test", ManualTestsActivity.this::testVibration));
                    }
                });

        // 2) Display & Sensors — Manual Labs 6–10
        addAccordionSection(root,
                "Display & Sensors — Manual Labs 6–10",
                new SectionBuilder() {
                    @Override
                    public void build(LinearLayout content) {
                        content.addView(makeButton("6. Display / Touch Basic Inspection", ManualTestsActivity.this::testDisplayBasic));
                        content.addView(makeButton("7. Rotation / Auto-Rotate Check (manual)", ManualTestsActivity.this::testRotationManual));
                        content.addView(makeButton("8. Proximity During Call (manual)", ManualTestsActivity.this::testProximityQuickInfo));
                        content.addView(makeButton("9. Sensors Quick Presence Check", ManualTestsActivity.this::testSensorsQuick));
                        content.addView(makeButton("10. Full Sensor List for Report", ManualTestsActivity.this::testSensorFullList));
                    }
                });

        // 3) System / RAM / Uptime / Thermal — Manual Labs 11–13
        addAccordionSection(root,
                "System / RAM / Uptime / Thermal — Manual Labs 11–13",
                new SectionBuilder() {
                    @Override
                    public void build(LinearLayout content) {
                        content.addView(makeButton("11. RAM Live Snapshot", ManualTestsActivity.this::testRamSnapshot));
                        content.addView(makeButton("12. Uptime / Reboot History Hint", ManualTestsActivity.this::testUptime));
                        content.addView(makeButton("13. CPU Thermal Snapshot", ManualTestsActivity.this::testThermalSnapshot));
                    }
                });

        // 4) Network & Wireless — Manual Labs 14–15
        addAccordionSection(root,
                "Network & Wireless — Manual Labs 14–15",
                new SectionBuilder() {
                    @Override
                    public void build(LinearLayout content) {
                        content.addView(makeButton("14. Network Connectivity Quick Check", ManualTestsActivity.this::testNetworkQuick));
                        content.addView(makeButton("15. Service Tips: Wi-Fi / Data (manual)", ManualTestsActivity.this::testWirelessTipsManual));
                    }
                });

        // 5) Battery & Charging — Manual Labs 16–17
        addAccordionSection(root,
                "Battery & Charging — Manual Labs 16–17",
                new SectionBuilder() {
                    @Override
                    public void build(LinearLayout content) {
                        content.addView(makeButton("16. Battery Snapshot (level / temp / health)", ManualTestsActivity.this::testBatterySnapshot));
                        content.addView(makeButton("17. Charging Port / Cable Inspection (manual)", ManualTestsActivity.this::testChargingPortManual));
                    }
                });

        // LOG AREA
        txtLog = new TextView(this);
        txtLog.setTextSize(13f);
        txtLog.setTextColor(0xFFEEEEEE);
        txtLog.setPadding(0, dp(18), 0, dp(8));
        txtLog.setMovementMethod(new ScrollingMovementMethod());
        txtLog.setText(Html.fromHtml("<b>Manual Tests Log</b><br>"));

        root.addView(txtLog);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ============================================================
    // ACCORDION HELPERS
    // ============================================================
    private void addAccordionSection(LinearLayout parent, String title, SectionBuilder builder) {
        // Container for header + content
        LinearLayout section = new LinearLayout(this);
        section.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sLp.setMargins(0, dp(6), 0, dp(6));
        section.setLayoutParams(sLp);

        // Header (clickable)
        TextView header = new TextView(this);
        header.setText("► " + title);
        header.setTextSize(15f);
        header.setTextColor(0xFF00FF66); // Neon service green
        header.setPadding(dp(12), dp(10), dp(12), dp(10));
        header.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setClickable(true);
        header.setFocusable(true);
        header.setTag(Boolean.FALSE); // collapsed

        // Content area
        final LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(8), dp(6), dp(8), dp(10));
        content.setVisibility(View.GONE);

        // Build children
        builder.build(content);

        header.setOnClickListener(v -> {
            boolean expanded = (Boolean) header.getTag();
            if (expanded) {
                // collapse
                content.setVisibility(View.GONE);
                header.setText("► " + title);
                header.setTag(Boolean.FALSE);
            } else {
                // expand
                content.setVisibility(View.VISIBLE);
                header.setText("▼ " + title);
                header.setTag(Boolean.TRUE);
            }
        });

        section.addView(header);
        section.addView(content);
        parent.addView(section);
    }

    // ============================================================
    // UI HELPERS
    // ============================================================
    private Button makeButton(String text, Runnable action) {
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
            String add = Html.fromHtml(html + "<br>") + "";
            txtLog.setText(cur + add);

            if (scroll != null) {
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
            }
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

    private void logError(String msg) {
        GELServiceLog.error(msg);
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
    }

    private void logWarn(String msg) {
        GELServiceLog.warn(msg);
        appendHtml("<font color='#FFD966'>⚠️ " + escape(msg) + "</font>");
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

    // ============================================================
    // MANUAL TESTS (SERVICE LOGIC)
    // ============================================================

    // 1) Speaker Tone Test (basic)
    private void testSpeaker() {
        logLine();
        logInfo("🔊 Manual Lab 1 — Speaker Tone Test started (~2–3 seconds).");
        try {
            new Thread(() -> {
                try {
                    ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);
                    SystemClock.sleep(2300);
                    tg.release();
                    logOk("If you heard a clear tone from the main speaker → speaker path is OK.");
                    logError("If you heard nothing / heavy distortion → possible speaker or audio-IC fault.");
                } catch (Exception e) {
                    logError("Speaker Tone Test error: " + e.getMessage());
                }
            }).start();
        } catch (Throwable t) {
            logError("ToneGenerator error: " + t.getMessage());
        }
    }

    // 2) Speaker Sweep Test (different frequencies)
    private void testSpeakerSweep() {
        logLine();
        logInfo("🎶 Manual Lab 2 — Speaker Sweep Test (multiple tones, ~2–3 seconds).");
        try {
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

                    for (int tone : tones) {
                        tg.startTone(tone, 700);
                        SystemClock.sleep(750);
                    }

                    logOk("If all tones sounded clean → speaker is OK across basic frequency range.");
                    logWarn("If some tones are weak / noisy → possible partial speaker damage.");
                } catch (Exception e) {
                    logError("Speaker Sweep Test error: " + e.getMessage());
                } finally {
                    if (tg != null) tg.release();
                }
            }).start();
        } catch (Throwable t) {
            logError("Speaker Sweep thread error: " + t.getMessage());
        }
    }

    // 3) Earpiece basic info (manual)
    private void testEarpieceExplain() {
        logLine();
        logInfo("📞 Manual Lab 3 — Earpiece Basic Check (manual instructions).");
        logInfo("1) Place a normal voice call or play a voicemail without headset.");
        logInfo("2) Put the phone to the ear and listen through the top earpiece.");
        logWarn("If volume is very low / distorted → possible earpiece or dust-filter issue.");
        logError("If absolutely no sound but speakerphone works → strong indication of earpiece or top-audio path failure.");
    }

    // 4) Mic Manual Check (manual instructions)
    private void testMicManualInfo() {
        logLine();
        logInfo("🎙 Manual Lab 4 — Main Microphone Check (manual).");
        logInfo("1) Open Voice Recorder or send a voice message (WhatsApp / Viber etc.).");
        logInfo("2) Speak normally close to the bottom microphone of the device.");
        logInfo("3) Playback the recording and compare to a reference phone if possible.");
        logWarn("If sound is very low / noisy / cut → possible microphone or mesh damage.");
        logError("If no voice is recorded at all → strong indication of microphone or audio-IC failure.");
        logInfo("4) For secondary mics (top / back), repeat using video recording or loudspeaker calls.");
    }

    // 5) Vibration Test
    private void testVibration() {
        logLine();
        logInfo("📳 Manual Lab 5 — Vibration Motor Test (about 0.8 sec).");
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) {
                logError("Vibrator service not found — device may not support vibration or hardware fault.");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(android.os.VibrationEffect.createOneShot(
                        800, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //noinspection deprecation
                v.vibrate(800);
            }
            logOk("If a strong vibration was felt → vibration motor and contacts are OK.");
            logError("If no vibration was felt → check motor, flex cable and board contacts.");
        } catch (Exception e) {
            logError("Vibration Test error: " + e.getMessage());
        }
    }

    // 6) Display / Touch Basic (manual)
    private void testDisplayBasic() {
        logLine();
        logInfo("🖥 Manual Lab 6 — Display / Touch Basic Inspection (manual).");
        logInfo("1) Open a full-white image or browser page at maximum brightness.");
        logWarn("Check for yellow tint, purple areas, burn-in or shadows → may indicate panel ageing or damage.");
        logWarn("Check for lines / flicker → may indicate panel, flex or driver IC issues.");
        logInfo("2) Use any touch-test grid app to drag a finger across the whole screen.");
        logError("Any dead areas or unresponsive stripes → strong sign of digitizer / flex damage.");
    }

    // 7) Rotation / Auto-Rotate Check (manual)
    private void testRotationManual() {
        logLine();
        logInfo("📐 Manual Lab 7 — Rotation / Auto-Rotate Check (manual).");
        logInfo("1) Enable auto-rotate in system settings.");
        logInfo("2) Open gallery or browser and rotate the device slowly left/right.");
        logWarn("If the screen never rotates while auto-rotate is ON → possible accelerometer or software issue.");
        logInfo("3) Compare with a known-good device if needed.");
    }

    // 8) Proximity Quick Check (manual)
    private void testProximityQuickInfo() {
        logLine();
        logInfo("📲 Manual Lab 8 — Proximity During Call (manual).");
        logInfo("1) Start a normal voice call.");
        logInfo("2) Cover the top area where the proximity sensor is located (close to the earpiece).");
        logWarn("The display SHOULD turn off when the sensor is fully covered.");
        logError("If the display never turns off during call → possible proximity sensor / glass / screen-protector issue.");
        logWarn("If the display turns off but does not wake again → may indicate software or sensor calibration problem.");
    }

    // 9) Sensors Quick Check
    private void testSensorsQuick() {
        logLine();
        logInfo("🎛 Manual Lab 9 — Sensors Quick Presence Check.");

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm == null) {
                logError("SensorManager not available — framework problem.");
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Total sensors reported: " + (all == null ? 0 : all.size()));

            boolean hasAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
            boolean hasGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
            boolean hasMag = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
            boolean hasLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null;
            boolean hasProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null;

            if (!hasAccel) logError("Accelerometer missing → motion-based features and rotation will fail.");
            if (!hasGyro) logWarn("Gyroscope missing → some AR / stabilization features may be limited.");
            if (!hasMag) logWarn("Magnetometer missing → compass / navigation may be unstable.");
            if (!hasLight) logWarn("Light sensor missing → auto-brightness will not work correctly.");
            if (!hasProx) logError("Proximity sensor missing → screen may not turn off during calls.");

            if (hasAccel && hasGyro && hasProx) {
                logOk("Core motion sensors (accelerometer / gyroscope / proximity) are present.");
            }

        } catch (Exception e) {
            logError("Sensors Quick Check error: " + e.getMessage());
        }
    }

    // 10) Full Sensor List
    private void testSensorFullList() {
        logLine();
        logInfo("📋 Manual Lab 10 — Full Sensor List for Report.");

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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

            logOk("Sensor list recorded in Service Log for export.");

        } catch (Exception e) {
            logError("Full Sensor List error: " + e.getMessage());
        }
    }

    // 11) RAM Snapshot
    private void testRamSnapshot() {
        logLine();
        logInfo("💾 Manual Lab 11 — Live RAM Snapshot.");

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                logError("ActivityManager not available.");
                return;
            }

            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long avail = mi.availMem;
            long total = mi.totalMem;
            int pctFree = (total > 0) ? (int) ((avail * 100L) / total) : -1;

            logInfo("Available RAM now: " + readable(avail) + " (" + pctFree + "% free)");

            if (pctFree >= 0 && pctFree < 10) {
                logError("Very low free RAM (<10%) — strong chance of lag and app closures. Suggest closing apps or reboot.");
            } else if (pctFree < 20) {
                logWarn("Low free RAM (<20%) — borderline status under heavy load.");
            } else {
                logOk("RAM level is acceptable for normal use.");
            }

        } catch (Exception e) {
            logError("RAM Snapshot error: " + e.getMessage());
        }
    }

    // 12) Uptime / Reboots
    private void testUptime() {
        logLine();
        logInfo("⏱ Manual Lab 12 — System Uptime / Reboot Hint.");

        long upMs = SystemClock.elapsedRealtime();
        long upSec = upMs / 1000;
        long days = upSec / (24 * 3600);
        long hours = (upSec % (24 * 3600)) / 3600;
        long mins = (upSec % 3600) / 60;

        logInfo(String.format(Locale.US,
                "System uptime: %d days, %d hours, %d minutes", days, hours, mins));

        if (days < 1) {
            logWarn("Device was rebooted very recently — some issues may already have been reset by the user.");
        } else if (days > 7) {
            logWarn("Uptime > 7 days — suggest a reboot before deep diagnostics and updates.");
        } else {
            logOk("Uptime is within normal range.");
        }
    }

    // 13) Thermal Snapshot (CPU)
    private void testThermalSnapshot() {
        logLine();
        logInfo("🌡 Manual Lab 13 — CPU Thermal Snapshot (where supported).");

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
                        logInfo(String.format(Locale.US, "Reported CPU temperature: %.1f°C", t));

                        if (t > 80f) {
                            logError("Very high CPU temperature (>80°C) — possible cooling or SoC issue.");
                        } else if (t > 70f) {
                            logWarn("High CPU temperature (70–80°C) — throttling and lag likely under load.");
                        } else {
                            logOk("CPU temperature is within normal range.");
                        }
                    } else {
                        logWarn("No CPU temperature data returned by system.");
                    }
                } else {
                    logWarn("HardwarePropertiesManager not available — limited thermal diagnostics.");
                }
            } catch (Throwable t) {
                logError("Thermal Snapshot error: " + t.getMessage());
            }
        } else {
            logWarn("Thermal APIs are not supported on this Android version (API < 29).");
        }
    }

    // 14) Network Quick Check
    private void testNetworkQuick() {
        logLine();
        logInfo("🌐 Manual Lab 14 — Network Connectivity Quick Check.");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            logError("ConnectivityManager not available.");
            return;
        }

        boolean hasInternet = false;
        boolean wifi = false;
        boolean mobile = false;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.net.Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                    if (caps != null) {
                        hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        wifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                        mobile = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    }
                }
            } else {
                @SuppressWarnings("deprecation")
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    hasInternet = true;
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI) wifi = true;
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) mobile = true;
                }
            }
        } catch (Exception e) {
            logError("Network Quick Check error: " + e.getMessage());
        }

        if (!hasInternet) {
            logError("No active Internet connection detected at this moment.");
        } else {
            if (wifi) logOk("Wi-Fi connection detected.");
            if (mobile) logOk("Mobile data connection detected.");
            logInfo("If user reports issues only on one type (Wi-Fi or mobile), focus diagnostics there.");
        }
    }

    // 15) Wireless manual tips
    private void testWirelessTipsManual() {
        logLine();
        logInfo("📡 Manual Lab 15 — Service Tips for Wi-Fi / Mobile Data (manual).");
        logInfo("1) Check if the problem exists on both Wi-Fi and mobile data or only on one of them.");
        logInfo("2) Test with another known-good Wi-Fi network and another SIM if possible.");
        logWarn("If issues appear only on a specific router / SIM → likely provider/router problem, not device.");
        logError("If device fails to connect to ANY known-good network / SIM → possible RF or antenna issue.");
    }

    // 16) Battery Snapshot
    private void testBatterySnapshot() {
        logLine();
        logInfo("🔋 Manual Lab 16 — Battery Snapshot (level / temp / health).");

        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);

            int level = -1;
            if (bm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }

            if (level >= 0) {
                logInfo("Estimated battery level: " + level + "%");
            } else {
                logWarn("Could not read accurate battery level from BatteryManager.");
            }

            Intent intent = registerReceiver(
                    null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            );

            if (intent != null) {
                int temp10 = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                float temp = (temp10 > 0) ? (temp10 / 10f) : -1f;
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

                if (temp > 0) {
                    logInfo(String.format(Locale.US, "Battery temperature: %.1f°C", temp));
                    if (temp > 45f) {
                        logError("High battery temperature (>45°C) — check charger, board and physical environment.");
                    } else if (temp > 38f) {
                        logWarn("Warm battery (38–45°C) — could be heavy use, heat or charging.");
                    } else {
                        logOk("Battery temperature is within normal range.");
                    }
                }

                String healthStr;
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        healthStr = "GOOD";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthStr = "OVERHEAT";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        healthStr = "DEAD";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        healthStr = "OVER_VOLTAGE";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        healthStr = "UNSPECIFIED_FAILURE";
                        break;
                    default:
                        healthStr = "UNKNOWN";
                        break;
                }

                logInfo("Battery health flag (Android): " + healthStr);

                if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                        health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                    logError("Android reports DEAD / FAILURE — recommend battery replacement.");
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                    logError("OVERHEAT flag present — serious thermal condition, hardware check required.");
                }
            } else {
                logWarn("ACTION_BATTERY_CHANGED returned null — cannot read detailed battery info.");
            }

        } catch (Exception e) {
            logError("Battery Snapshot error: " + e.getMessage());
        }
    }

    // 17) Charging Port / Cable manual tips
    private void testChargingPortManual() {
        logLine();
        logInfo("🔌 Manual Lab 17 — Charging Port / Cable Inspection (manual).");
        logInfo("1) Test with at least TWO known-good chargers and cables (preferably original).");
        logWarn("If charging is unstable only with one charger/cable → likely accessory problem, not device.");
        logWarn("Inspect the USB port with light for dust, corrosion or bent pins.");
        logError("If device fails to charge with multiple good chargers and a clean port → strong sign of port / board fault.");
    }

    // ============================================================
    // UTILS
    // ============================================================
    private String readable(long bytes) {
        if (bytes <= 0) return "0 B";
        float kb = bytes / 1024f;
        if (kb < 1024) return String.format(Locale.US, "%.2f KB", kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US, "%.2f MB", mb);
        float gb = mb / 1024f;
        return String.format(Locale.US, "%.2f GB", gb);
    }
}
