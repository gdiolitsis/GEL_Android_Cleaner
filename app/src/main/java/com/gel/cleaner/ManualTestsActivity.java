// ============================================================
// ManualTestsActivity
// GEL Manual Tests — Hospital Edition (30 MANUAL LABS, 7 TABS)
// Full manual test suite with shared GELServiceLog export
// NOTE: Whole file is ready for copy-paste into the project.
// ============================================================
package com.gel.cleaner;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
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
import android.os.Vibrator;
import android.provider.Settings;
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
// GEL Manual Tests — Hospital Edition (30 Labs, 7 Tabs)
// Each button runs a targeted manual test and writes results
// to the on-screen log AND GELServiceLog for export.
// ============================================================
public class ManualTestsActivity extends AppCompatActivity {

    private static final int TAB_COUNT = 7;

    private TextView txtLog;
    private ScrollView scroll;
    private Handler ui;

    private Button[] tabButtons = new Button[TAB_COUNT];
    private LinearLayout[] tabLayouts = new LinearLayout[TAB_COUNT];

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
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText(
                "Professional service tools.\n" +
                "Each test logs detailed results below (OK / WARN / ERROR) " +
                "and is included in the final Service Report."
        );
        sub.setTextSize(13f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // ===================== TAB BAR (7 CATEGORIES) =====================
        LinearLayout tabBar = new LinearLayout(this);
        tabBar.setOrientation(LinearLayout.HORIZONTAL);
        tabBar.setPadding(0, 0, 0, dp(8));

        String[] tabTitles = new String[]{
                "Audio / Vib",
                "Display / Sensors",
                "Wireless",
                "Battery / Thermal",
                "Storage / System",
                "Security / Root",
                "Advanced"
        };

        for (int i = 0; i < TAB_COUNT; i++) {
            final int idx = i;
            Button b = makeTabButton(tabTitles[i], idx);
            tabButtons[i] = b;
            tabBar.addView(b);
        }

        root.addView(tabBar);

        // ===================== TAB CONTENT CONTAINER =====================
        LinearLayout tabContainer = new LinearLayout(this);
        tabContainer.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < TAB_COUNT; i++) {
            LinearLayout tab = new LinearLayout(this);
            tab.setOrientation(LinearLayout.VERTICAL);
            tabLayouts[i] = tab;
            tabContainer.addView(tab);
        }

        // ---- TAB 0: AUDIO / VIBRATION (LABS 1–5) ----
        LinearLayout t0 = tabLayouts[0];
        t0.addView(makeSectionLabel("Audio & Vibration — Manual Labs 1–5"));
        t0.addView(makeButton("1. Speaker Test (single tone)", this::testSpeaker));
        t0.addView(makeButton("2. Speaker Sweep (multiple tones)", this::testSpeakerSweep));
        t0.addView(makeButton("3. Earpiece Call Check (manual)", this::testEarpieceExplain));
        t0.addView(makeButton("4. Microphone Recording Check (manual)", this::testMicManualInfo));
        t0.addView(makeButton("5. Vibration Motor Test", this::testVibration));

        // ---- TAB 1: DISPLAY / SENSORS (LABS 6–10) ----
        LinearLayout t1 = tabLayouts[1];
        t1.addView(makeSectionLabel("Display & Sensors — Manual Labs 6–10"));
        t1.addView(makeButton("6. Display / Touch Basic Inspection", this::testDisplayBasic));
        t1.addView(makeButton("7. Rotation / Auto-Rotate Check (manual)", this::testRotationManual));
        t1.addView(makeButton("8. Proximity During Call (manual)", this::testProximityQuickInfo));
        t1.addView(makeButton("9. Sensors Quick Presence Check", this::testSensorsQuick));
        t1.addView(makeButton("10. Full Sensor List for Report", this::testSensorFullList));

        // ---- TAB 2: WIRELESS / NETWORK (LABS 11–15) ----
        LinearLayout t2 = tabLayouts[2];
        t2.addView(makeSectionLabel("Wireless / Network — Manual Labs 11–15"));
        t2.addView(makeButton("11. Network Quick Check (Wi-Fi / Mobile)", this::testNetworkQuick));
        t2.addView(makeButton("12. Wi-Fi Quality Snapshot", this::testWifiQuality));
        t2.addView(makeButton("13. Mobile Data / Signal Checklist", this::testMobileDataChecklist));
        t2.addView(makeButton("14. Bluetooth Basic Check", this::testBluetoothManual));
        t2.addView(makeButton("15. GPS / Location Quick Check", this::testGpsQuick));

        // ---- TAB 3: BATTERY / THERMAL (LABS 16–20) ----
        LinearLayout t3 = tabLayouts[3];
        t3.addView(makeSectionLabel("Battery & Thermal — Manual Labs 16–20"));
        t3.addView(makeButton("16. Battery Snapshot (level / temp / health)", this::testBatterySnapshot));
        t3.addView(makeButton("17. Charging Port & Cable Checklist", this::testChargingPortChecklist));
        t3.addView(makeButton("18. Thermal Snapshot (CPU where available)", this::testThermalSnapshot));
        t3.addView(makeButton("19. Overnight Battery Drain Checklist", this::testOvernightDrainChecklist));
        t3.addView(makeButton("20. Battery Age & Cycle Checklist", this::testBatteryAgeChecklist));

        // ---- TAB 4: STORAGE / SYSTEM PERFORMANCE (LABS 21–25) ----
        LinearLayout t4 = tabLayouts[4];
        t4.addView(makeSectionLabel("Storage & System Performance — Manual Labs 21–25"));
        t4.addView(makeButton("21. Live RAM Snapshot", this::testRamSnapshot));
        t4.addView(makeButton("22. Internal Storage Snapshot", this::testStorageInternalSnapshot));
        t4.addView(makeButton("23. External / SD Storage Check", this::testStorageExternalCheck));
        t4.addView(makeButton("24. Installed Apps Footprint", this::testAppsFootprint));
        t4.addView(makeButton("25. System Uptime / Reboot Pattern", this::testUptime));

        // ---- TAB 5: SECURITY / ROOT / OS (LABS 26–30) ----
        LinearLayout t5 = tabLayouts[5];
        t5.addView(makeSectionLabel("Security / Root / OS — Manual Labs 26–30"));
        t5.addView(makeButton("26. Root Quick Check (test-keys / su traces)", this::testRootQuick));
        t5.addView(makeButton("27. SELinux / ADB / Debug Flags", this::testSelinuxDebugFlagsManual));
        t5.addView(makeButton("28. Android Security Patch Level", this::testSecurityPatchView));
        t5.addView(makeButton("29. Screen Lock / Biometrics Checklist", this::testScreenLockBiometricsChecklist));
        t5.addView(makeButton("30. Play Protect / Unknown Sources Checklist", this::testPlayProtectUnknownSources));

        // ---- TAB 6: ADVANCED (PLACEHOLDER FOR FUTURE LABS) ----
        LinearLayout t6 = tabLayouts[6];
        t6.addView(makeSectionLabel("Advanced Notes"));
        t6.addView(makeInfoText(
                "This tab is reserved for future advanced manual labs " +
                "(e.g., special OEM service codes, board-level notes, etc.).\n\n" +
                "Use Labs 1–30 to produce a hospital-grade report today."
        ));

        root.addView(tabContainer);

        // LOG AREA (shared for all tabs)
        txtLog = new TextView(this);
        txtLog.setTextSize(13f);
        txtLog.setTextColor(0xFFEEEEEE);
        txtLog.setPadding(0, dp(16), 0, dp(8));
        txtLog.setMovementMethod(new ScrollingMovementMethod());
        txtLog.setText(Html.fromHtml("<b>Manual Tests Log</b><br>"));

        root.addView(txtLog);

        scroll.addView(root);
        setContentView(scroll);

        // Default tab
        showTab(0);
    }

    // ============================================================
    // UI HELPERS
    // ============================================================
    private Button makeTabButton(String text, int index) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextSize(13f);
        b.setTextColor(0xFFFFFFFF);
        b.setBackgroundResource(R.drawable.gel_btn_outline_selector);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, dp(40), 1f);
        lp.setMargins(dp(2), 0, dp(2), 0);
        b.setLayoutParams(lp);
        b.setGravity(Gravity.CENTER);

        b.setOnClickListener(v -> showTab(index));
        return b;
    }

    private void showTab(int index) {
        for (int i = 0; i < TAB_COUNT; i++) {
            if (tabLayouts[i] != null) {
                tabLayouts[i].setVisibility(i == index ? View.VISIBLE : View.GONE);
            }
            if (tabButtons[i] != null) {
                tabButtons[i].setTextColor(i == index ? 0xFFFFD700 : 0xFFFFFFFF);
            }
        }
    }

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

    private TextView makeSectionLabel(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setTextSize(16f);
        tv.setTextColor(0xFFEEEEEE);
        tv.setPadding(0, dp(14), 0, dp(6));
        return tv;
    }

    private TextView makeInfoText(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setTextSize(13f);
        tv.setTextColor(0xFFCCCCCC);
        tv.setPadding(0, dp(4), 0, dp(4));
        return tv;
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
        GELServiceLog.addLine("------------------------------");
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
    // TESTS 1–5: AUDIO & VIBRATION
    // ============================================================

    // 1) Speaker Test (basic tone)
    private void testSpeaker() {
        logLine();
        logInfo("🔊 Speaker Test started (2–3 seconds).");
        try {
            new Thread(() -> {
                try {
                    ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);
                    SystemClock.sleep(2300);
                    tg.release();
                    logOk("If a clear tone was heard → loudspeaker path is working.");
                    logError("If there was NO sound → possible loudspeaker / audio-IC / flex failure.");
                } catch (Exception e) {
                    logError("Speaker Test error: " + e.getMessage());
                }
            }).start();
        } catch (Throwable t) {
            logError("ToneGenerator failure: " + t.getMessage());
        }
    }

    // 2) Speaker Sweep Test (set of different tones)
    private void testSpeakerSweep() {
        logLine();
        logInfo("🎶 Speaker Sweep Test (several frequencies for ~3 seconds).");
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

                    logOk("If ALL tones are loud and clean → speaker is OK across the spectrum.");
                    logWarn("If some tones are distorted or missing → possible partial speaker damage.");
                } catch (Exception e) {
                    logError("Speaker Sweep error: " + e.getMessage());
                } finally {
                    if (tg != null) tg.release();
                }
            }).start();
        } catch (Throwable t) {
            logError("Speaker Sweep Thread error: " + t.getMessage());
        }
    }

    // 3) Earpiece basic info (manual call test)
    private void testEarpieceExplain() {
        logLine();
        logInfo("📞 Earpiece Basic Check (manual instructions).");
        logInfo("1) Place a normal voice call (no headset, no speakerphone).");
        logInfo("2) Hold the phone normally at the ear.");
        logInfo("3) If volume is very low / distorted while loudspeaker is OK:");
        logError("   → suspect earpiece unit, dust mesh, or moisture damage.");
        logInfo("4) If there is NO sound in earpiece but loudspeaker works:");
        logError("   → strong suspicion of earpiece or audio line failure.");
    }

    // 4) Mic Manual Check (instructions)
    private void testMicManualInfo() {
        logLine();
        logInfo("🎙 Microphone Manual Check (no root / no special permissions).");
        logInfo("1) Open Voice Recorder or send a voice message (WhatsApp / Viber etc.).");
        logInfo("2) Speak normally near the MAIN bottom microphone.");
        logInfo("3) Play back the recording:");
        logError("   → very low / noisy / cutting sound → suspect microphone or dust filter.");
        logError("   → completely silent recording → suspect microphone, flex, or audio IC.");
        logInfo("4) For secondary/top microphones: record a video or use loudspeaker call and compare.");
    }

    // 5) Vibration Test
    private void testVibration() {
        logLine();
        logInfo("📳 Vibration Test started (about 0.8s).");
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) {
                logError("No Vibrator service reported — device may lack vibration or hardware may be faulty.");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(android.os.VibrationEffect.createOneShot(
                        800, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //noinspection deprecation
                v.vibrate(800);
            }
            logOk("If a strong vibration was felt → vibration motor is working.");
            logError("If there was no vibration → suspect vibration motor / contacts / flex.");
        } catch (Exception e) {
            logError("Vibration Test error: " + e.getMessage());
        }
    }

    // ============================================================
    // TESTS 6–10: DISPLAY & SENSORS
    // ============================================================

    // 6) Display / Touch Basic info
    private void testDisplayBasic() {
        logLine();
        logInfo("🖥 Display / Touch Basic Inspection (manual).");
        logInfo("1) Open a pure white picture or a blank browser page.");
        logWarn("2) Look for yellow tint, purple tone, burn-in, or dark patches → possible panel damage.");
        logWarn("3) Check for dead zones on touch (drag a finger slowly across the screen).");
        logInfo("4) For advanced OEM diagnostics, use manufacturer service codes if available.");
    }

    // 7) Rotation / Auto-Rotate check
    private void testRotationManual() {
        logLine();
        logInfo("🔄 Rotation / Auto-Rotate Check (manual).");
        logInfo("1) Enable auto-rotate from quick settings.");
        logInfo("2) Open Gallery or Browser and rotate the phone 90°.");
        logError("   → If screen does NOT rotate at all → suspect accelerometer / sensor service / settings.");
        logWarn("   → If rotation is very slow or inconsistent → possible sensor or firmware issue.");
    }

    // 8) Proximity Quick Check (manual)
    private void testProximityQuickInfo() {
        logLine();
        logInfo("📲 Proximity Quick Check (manual).");
        logInfo("1) Place a normal voice call.");
        logInfo("2) Move the phone to the ear, fully covering the proximity sensor area.");
        logError("   → If the screen does NOT turn off → suspect proximity sensor / glass / protector.");
        logError("   → If the screen turns off but does NOT wake again → software or sensor handling issue.");
    }

    // 9) Sensors Quick Check
    private void testSensorsQuick() {
        logLine();
        logInfo("🎛 Sensors Quick Presence Check.");

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm == null) {
                logError("SensorManager not available — serious framework issue.");
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Total sensors reported: " + (all == null ? 0 : all.size()));

            boolean hasAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
            boolean hasGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
            boolean hasMag  = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
            boolean hasLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null;
            boolean hasProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null;

            if (!hasAccel) logError("Accelerometer missing → motion features and rotation will fail.");
            if (!hasGyro)  logWarn("Gyroscope missing → limited motion / AR features.");
            if (!hasMag)   logWarn("Magnetometer missing → compass / navigation may be inaccurate.");
            if (!hasLight) logWarn("Light sensor missing → auto-brightness will not work.");
            if (!hasProx)  logError("Proximity sensor missing → call screen issues expected.");

            if (hasAccel && hasGyro && hasProx) {
                logOk("Core motion sensors (accelerometer / gyroscope / proximity) are present.");
            }

        } catch (Exception e) {
            logError("Sensors Quick Check error: " + e.getMessage());
        }
    }

    // 10) Full Sensor List (for report)
    private void testSensorFullList() {
        logLine();
        logInfo("📋 Full Sensor List (type / vendor / name).");

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

            logOk("Sensor list recorded for the service report.");

        } catch (Exception e) {
            logError("Full Sensor List error: " + e.getMessage());
        }
    }

    // ============================================================
    // TESTS 11–15: WIRELESS / NETWORK
    // ============================================================

    // 11) Network Quick Check
    private void testNetworkQuick() {
        logLine();
        logInfo("🌐 Network Quick Check (Internet reachability).");

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
            if (wifi) logOk("Wi-Fi is active and connected.");
            if (mobile) logOk("Mobile data is active and connected.");
        }
    }

    // 12) Wi-Fi Quality Snapshot
    private void testWifiQuality() {
        logLine();
        logInfo("📶 Wi-Fi Quality Snapshot.");

        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wm == null) {
                logWarn("WifiManager not available on this device.");
                return;
            }

            if (!wm.isWifiEnabled()) {
                logWarn("Wi-Fi is currently disabled.");
                return;
            }

            WifiInfo info = wm.getConnectionInfo();
            if (info == null || info.getNetworkId() == -1) {
                logWarn("Wi-Fi is enabled but not connected to an access point.");
                return;
            }

            int rssi = info.getRssi();
            int linkSpeed = info.getLinkSpeed();

            logInfo("SSID: " + info.getSSID());
            logInfo("RSSI: " + rssi + " dBm");
            logInfo("Link speed: " + linkSpeed + " Mbps");

            if (rssi > -65)
                logOk("Wi-Fi signal is strong for normal use.");
            else if (rssi > -80)
                logWarn("Wi-Fi signal is medium — instability is possible at distance.");
            else
                logError("Wi-Fi signal is very weak — expect drops and low speed.");

        } catch (SecurityException se) {
            logWarn("Wi-Fi details are restricted by permissions / Android version.");
        } catch (Exception e) {
            logError("Wi-Fi Quality Snapshot error: " + e.getMessage());
        }
    }

    // 13) Mobile data / signal checklist
    private void testMobileDataChecklist() {
        logLine();
        logInfo("📡 Mobile Data / Signal Checklist (manual + basic info).");

        try {
            android.telephony.TelephonyManager tm =
                    (android.telephony.TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tm != null) {
                String netOp = tm.getNetworkOperatorName();
                String simOp = tm.getSimOperatorName();
                logInfo("Network operator (current): " + (netOp == null ? "N/A" : netOp));
                logInfo("SIM operator: " + (simOp == null ? "N/A" : simOp));
            }
        } catch (Exception e) {
            logWarn("Cannot read basic operator info: " + e.getMessage());
        }

        logInfo("Manual steps:");
        logInfo("1) Check that Mobile Data is ON and Airplane mode is OFF.");
        logInfo("2) Try a speed-test or open a web page without Wi-Fi.");
        logError("   → If calls work but data does not → APN / data plan / network configuration issue.");
        logError("   → If there is no signal at all while other phones have signal → antenna / RF issue.");
    }

    // 14) Bluetooth basic check
    private void testBluetoothManual() {
        logLine();
        logInfo("🔵 Bluetooth Basic Check.");

        try {
            BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
            if (bt == null) {
                logWarn("This device reports NO Bluetooth adapter.");
                return;
            }

            boolean enabled;
            try {
                enabled = bt.isEnabled();
            } catch (SecurityException se) {
                logWarn("Bluetooth status restricted by permissions.");
                return;
            }

            logInfo("Bluetooth enabled: " + enabled);

            if (enabled)
                logOk("Bluetooth radio is enabled. Try pairing with a known device (speaker / headset).");
            else
                logWarn("Bluetooth is OFF — enable it and repeat test.");

        } catch (Exception e) {
            logError("Bluetooth check error: " + e.getMessage());
        }
    }

    // 15) GPS / Location quick check
    private void testGpsQuick() {
        logLine();
        logInfo("📍 GPS / Location Quick Check.");

        try {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (lm == null) {
                logWarn("LocationManager not available.");
                return;
            }

            boolean gpsEnabled;
            boolean netEnabled;
            try {
                gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                netEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                logWarn("Cannot read provider status (permissions / OEM restriction).");
                gpsEnabled = false;
                netEnabled = false;
            }

            logInfo("GPS provider enabled: " + gpsEnabled);
            logInfo("Network location enabled: " + netEnabled);

            if (!gpsEnabled && !netEnabled)
                logWarn("All location providers are disabled — navigation apps will fail.");
            else
                logOk("At least one location provider is enabled. Test with Maps for accuracy.");

        } catch (Exception e) {
            logError("GPS quick check error: " + e.getMessage());
        }
    }

    // ============================================================
    // TESTS 16–20: BATTERY & THERMAL
    // ============================================================

    // 16) Battery Snapshot
    private void testBatterySnapshot() {
        logLine();
        logInfo("🔋 Battery Snapshot (level / temperature / health).");

        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);

            int level = -1;
            if (bm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }

            if (level >= 0) {
                logInfo("Estimated battery level: " + level + "%");
            } else {
                logWarn("Could not obtain exact battery level from BatteryManager.");
            }

            android.content.Intent intent = registerReceiver(
                    null,
                    new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
            );

            if (intent != null) {
                int temp10 = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                float temp = (temp10 > 0) ? (temp10 / 10f) : -1f;
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

                if (temp > 0) {
                    logInfo(String.format(Locale.US, "Battery temperature: %.1f°C", temp));
                    if (temp > 45f) {
                        logError("Very high battery temperature (>45°C) — check charger, board, environment.");
                    } else if (temp > 38f) {
                        logWarn("Warm battery (38–45°C) — heavy use or thermal issue.");
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

                logInfo("Battery health flag: " + healthStr);

                if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                        health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                    logError("Battery is reported as FAILED — replacement is strongly recommended.");
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                    logError("Battery flag OVERHEAT — serious safety / hardware issue.");
                }
            } else {
                logWarn("Could not read ACTION_BATTERY_CHANGED data.");
            }

        } catch (Exception e) {
            logError("Battery Snapshot error: " + e.getMessage());
        }
    }

    // 17) Charging port & cable checklist
    private void testChargingPortChecklist() {
        logLine();
        logInfo("🔌 Charging Port & Cable Checklist (manual).");
        logInfo("1) Test with ORIGINAL or certified charger and cable.");
        logInfo("2) Check if charging starts immediately and remains stable (no frequent connect / disconnect).");
        logWarn("   → If device charges only with strong pressure on cable → suspect USB port / flex damage.");
        logWarn("   → If charging is extremely slow with multiple chargers → battery / board / software issue.");
        logInfo("3) Inspect port for dust / corrosion and document findings with photos if needed.");
    }

    // 18) Thermal snapshot (CPU where supported)
    private void testThermalSnapshot() {
        logLine();
        logInfo("🌡 Thermal Snapshot (CPU temperature where supported).");

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
                        logInfo(String.format(Locale.US, "CPU temperature: %.1f°C", t));

                        if (t > 80f) {
                            logError("Very high CPU temperature (>80°C) — thermal throttling or damage possible.");
                        } else if (t > 70f) {
                            logWarn("High CPU temperature (70–80°C) — throttling and lag expected.");
                        } else {
                            logOk("CPU temperature within normal range at this moment.");
                        }
                    } else {
                        logWarn("No CPU temperature data provided by the system.");
                    }
                } else {
                    logWarn("HardwarePropertiesManager not available — limited thermal diagnostics.");
                }
            } catch (Throwable t) {
                logError("Thermal Snapshot error: " + t.getMessage());
            }
        } else {
            logWarn("Thermal APIs are not available on this Android version (API < 29).");
        }
    }

    // 19) Overnight drain checklist
    private void testOvernightDrainChecklist() {
        logLine();
        logInfo("🌙 Overnight Battery Drain Checklist (manual).");
        logInfo("Ask the customer:");
        logInfo("• How many % are lost during 6–8 hours of standby with screen OFF?");
        logWarn("   → >15% drain overnight usually indicates rogue apps, sync loops or battery wear.");
        logWarn("   → 2–8% overnight is considered normal for most modern devices.");
        logInfo("Combine this with Battery Snapshot and Apps Footprint for final verdict.");
    }

    // 20) Battery age & cycles checklist
    private void testBatteryAgeChecklist() {
        logLine();
        logInfo("📆 Battery Age / Cycles Checklist (best-effort).");
        logInfo("1) Ask for device age and if the battery was ever replaced.");
        logInfo("2) Compare current capacity / behaviour with expected for that age.");
        logWarn("   → Devices older than 2–3 years with original battery often require replacement.");
        logInfo("Note: exact cycle count is OEM-specific and not always available from Android.");
    }

    // ============================================================
    // TESTS 21–25: STORAGE & SYSTEM PERFORMANCE
    // ============================================================

    // 21) RAM Snapshot
    private void testRamSnapshot() {
        logLine();
        logInfo("💾 Live RAM Snapshot.");

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
            int pctFree = (int) ((avail * 100L) / total);

            logInfo("Available RAM now: " + readable(avail) +
                    " (" + pctFree + "% free)");

            if (pctFree < 10) {
                logError("VERY low free RAM (<10%) — close apps / consider reboot.");
            } else if (pctFree < 20) {
                logWarn("Low free RAM (<20%) — borderline condition.");
            } else {
                logOk("RAM status is acceptable for normal usage.");
            }

        } catch (Exception e) {
            logError("RAM Snapshot error: " + e.getMessage());
        }
    }

    // 22) Internal storage snapshot
    private void testStorageInternalSnapshot() {
        logLine();
        logInfo("💽 Internal Storage Snapshot.");

        try {
            java.io.File data = Environment.getDataDirectory();
            StatFs s = new StatFs(data.getAbsolutePath());

            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();
            long used = total - free;
            int pctFree = (int) ((free * 100L) / total);

            logInfo("Internal used: " + readable(used) + " / " + readable(total) +
                    " (free " + readable(free) + ", " + pctFree + "%)");

            if (pctFree < 5)
                logError("Free space below 5% — high risk of crashes and failed updates.");
            else if (pctFree < 10)
                logWarn("Free space below 10% — performance problems and update issues possible.");
            else
                logOk("Internal storage free space is within safe range.");

        } catch (Exception e) {
            logError("Internal Storage Snapshot error: " + e.getMessage());
        }
    }

    // 23) External / SD storage check
    private void testStorageExternalCheck() {
        logLine();
        logInfo("💿 External / SD Storage Check.");

        try {
            java.io.File ext = getExternalFilesDir(null);
            if (ext == null) {
                logInfo("No external app directory reported — device may have no SD / external storage.");
                return;
            }

            StatFs s = new StatFs(ext.getAbsolutePath());
            long total = s.getBlockCountLong() * s.getBlockSizeLong();
            long free = s.getAvailableBlocksLong() * s.getBlockSizeLong();

            logInfo("External (app) storage: " + readable(free) +
                    " free / " + readable(total) + " total.");
            logOk("External storage is accessible for this app.");

        } catch (Exception e) {
            logWarn("External storage statistics could not be read: " + e.getMessage());
        }
    }

    // 24) Installed apps footprint
    private void testAppsFootprint() {
        logLine();
        logInfo("📦 Installed Apps Footprint.");

        try {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            if (apps == null) {
                logWarn("Installed applications list is not available (OEM restriction).");
                return;
            }

            int userApps = 0;
            int systemApps = 0;
            for (ApplicationInfo ai : apps) {
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    systemApps++;
                else
                    userApps++;
            }

            logInfo("User-installed apps: " + userApps);
            logInfo("System apps: " + systemApps);
            logInfo("Total packages: " + apps.size());

            if (userApps > 120)
                logError("Very high number of user apps — strong risk of background drain and lag.");
            else if (userApps > 80)
                logWarn("High number of user apps — performance can be affected.");
            else
                logOk("App footprint is within a typical range.");

        } catch (Exception e) {
            logError("Apps Footprint error: " + e.getMessage());
        }
    }

    // 25) Uptime / reboot pattern
    private void testUptime() {
        logLine();
        logInfo("⏱ System Uptime / Reboots.");

        long upMs = SystemClock.elapsedRealtime();
        long upSec = upMs / 1000;
        long days = upSec / (24 * 3600);
        long hours = (upSec % (24 * 3600)) / 3600;
        long mins = (upSec % 3600) / 60;

        logInfo(String.format(Locale.US,
                "Uptime: %d days, %d hours, %d minutes", days, hours, mins));

        if (days < 1) {
            logWarn("Device was rebooted very recently (<1 day) — some issues may be transient.");
        } else if (days > 7) {
            logWarn("Uptime >7 days — recommend reboot before deep diagnostics.");
        } else {
            logOk("Uptime is in a normal range.");
        }
    }

    // ============================================================
    // TESTS 26–30: SECURITY / ROOT / OS
    // ============================================================

    // 26) Root quick check
    private void testRootQuick() {
        logLine();
        logInfo("🛡 Root Quick Check (test-keys / su traces).");

        boolean rooted = isRootedQuick();
        if (rooted) {
            logError("Device shows ROOT indicators (test-keys / su binary / Superuser traces).");
            logWarn("From service perspective this is acceptable, but security is reduced.");
        } else {
            logOk("No direct root indicators found. Device looks locked / non-rooted.");
        }
    }

    // 27) SELinux / ADB / Debug flags
    private void testSelinuxDebugFlagsManual() {
        logLine();
        logInfo("🔐 SELinux / ADB / Debug Flags Snapshot.");

        try {
            boolean enabled = false;
            boolean enforced = false;

            try {
                Class<?> clazz = Class.forName("android.os.SELinux");
                enabled = (boolean) clazz.getMethod("isSELinuxEnabled").invoke(null);
                enforced = (boolean) clazz.getMethod("isSELinuxEnforced").invoke(null);
            } catch (Throwable ignored) {
                logWarn("SELinux API not accessible on this build.");
            }

            logInfo("SELinux enabled: " + enabled + " | enforced: " + enforced);

            if (!enabled) logError("SELinux is disabled — security baseline is weak.");
            else if (!enforced) logWarn("SELinux is permissive — weaker enforcement.");
            else logOk("SELinux is enforcing — expected on most modern Android builds.");
        } catch (Throwable t) {
            logWarn("SELinux status read failure: " + t.getMessage());
        }

        try {
            int adb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);
            int dev = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            logInfo("ADB enabled: " + (adb == 1));
            logInfo("Developer options enabled: " + (dev == 1));

            if (adb == 1)
                logWarn("ADB is enabled — recommended only for developers / service use.");
        } catch (Throwable t) {
            logWarn("ADB / Developer flags not accessible: " + t.getMessage());
        }
    }

    // 28) Security patch level
    private void testSecurityPatchView() {
        logLine();
        logInfo("🧩 Android Security Patch Level.");

        try {
            String patch = Build.VERSION.SECURITY_PATCH;
            if (patch == null || patch.trim().isEmpty()) {
                logWarn("Security patch level not reported (OEM-specific or very old Android).");
            } else {
                logInfo("Reported security patch: " + patch);
                logOk("Patch level is available — evaluate according to corporate policy.");
            }
        } catch (Exception e) {
            logWarn("Security patch property not accessible: " + e.getMessage());
        }
    }

    // 29) Screen lock / biometrics checklist
    private void testScreenLockBiometricsChecklist() {
        logLine();
        logInfo("🔒 Screen Lock / Biometrics Checklist.");

        try {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km != null) {
                boolean secure = km.isDeviceSecure();
                logInfo("Device secure (PIN / pattern / password or biometrics): " + secure);
                if (!secure) {
                    logWarn("No secure lock screen set — device is unprotected if lost.");
                } else {
                    logOk("Secure lock screen is enabled.");
                }
            }
        } catch (Exception e) {
            logWarn("KeyguardManager not accessible: " + e.getMessage());
        }

        logInfo("Manual notes:");
        logInfo("• Confirm that fingerprint / face unlock (if present) is working reliably.");
        logInfo("• For business devices, document whether lock policy meets company requirements.");
    }

    // 30) Play Protect / Unknown sources checklist
    private void testPlayProtectUnknownSources() {
        logLine();
        logInfo("🛡 Google Play Protect / Unknown Sources Checklist (manual).");

        logInfo("1) Open Google Play Store → Play Protect → verify that scanning is enabled.");
        logWarn("   → If Play Protect is disabled, risk from malicious apps is higher.");
        logInfo("2) Check if the user frequently installs APKs from outside official stores.");
        logWarn("   → Heavy sideloading history increases malware risk.");
        logInfo("3) For older Android versions, verify if 'Unknown sources' is enabled in system settings.");
    }

    // ============================================================
    // ROOT HELPERS
    // ============================================================
    private boolean isRootedQuick() {
        return hasTestKeys() || hasSuBinary() || hasSuperUserApk() || whichSu();
    }

    private boolean hasTestKeys() {
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    private boolean hasSuBinary() {
        String[] paths = new String[]{
                "/system/bin/su", "/system/xbin/su", "/sbin/su",
                "/system/bin/.ext/su", "/system/usr/we-need-root/su"
        };
        try {
            for (String p : paths) {
                if (new java.io.File(p).exists()) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean hasSuperUserApk() {
        try {
            return new java.io.File("/system/app/Superuser.apk").exists();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean whichSu() {
        java.io.BufferedReader in = null;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"which", "su"});
            in = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            return line != null;
        } catch (Exception ignored) {
            return false;
        } finally {
            if (in != null) try { in.close(); } catch (Exception ignored) {}
        }
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
