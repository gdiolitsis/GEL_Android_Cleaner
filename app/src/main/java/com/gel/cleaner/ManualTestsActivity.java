package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import java.util.List;
import java.util.Locale;

// ============================================================
// GEL Manual Tests — στοχευμένα service tests (PRO Edition)
// Με πλήρη Service Log (GELServiceLog) για export
// ============================================================
public class ManualTestsActivity extends AppCompatActivity {

    private TextView txtLog;
    private ScrollView scroll;
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
        title.setText("🧪 GEL Manual Tests");
        title.setTextSize(20f);
        title.setTextColor(0xFFFFD700);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Εργαλεία για επαγγελματικό service.\n" +
                "Κάθε test γράφει αναλυτικά αποτελέσματα παρακάτω (OK / WARN / ERROR).");
        sub.setTextSize(13f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // ===================== Ήχος / Δόνηση =====================
        root.addView(makeSectionLabel("Ήχος / Δόνηση"));

        root.addView(makeButton("🔊 Speaker Test", this::testSpeaker));
        root.addView(makeButton("📞 Earpiece Basic Check", this::testEarpieceExplain));
        root.addView(makeButton("📳 Vibration Test", this::testVibration));
        root.addView(makeButton("🎶 Speaker Sweep Test", this::testSpeakerSweep));
        root.addView(makeButton("🎙 Mic Manual Check", this::testMicManualInfo));

        // ===================== Αισθητήρες / Οθόνη =====================
        root.addView(makeSectionLabel("Αισθητήρες / Οθόνη"));

        root.addView(makeButton("🎛 Sensors Quick Check", this::testSensorsQuick));
        root.addView(makeButton("📲 Proximity Quick Check", this::testProximityQuickInfo));
        root.addView(makeButton("🖥 Display / Touch Basic", this::testDisplayBasic));
        root.addView(makeButton("📋 Full Sensor List", this::testSensorFullList));

        // ===================== Σύστημα / RAM / Uptime =====================
        root.addView(makeSectionLabel("Σύστημα / RAM / Uptime / Θερμοκρασίες"));

        root.addView(makeButton("💾 RAM Live Snapshot", this::testRamSnapshot));
        root.addView(makeButton("⏱ Uptime / Reboots", this::testUptime));
        root.addView(makeButton("🌐 Network Quick Check", this::testNetworkQuick));
        root.addView(makeButton("🔋 Battery Snapshot", this::testBatterySnapshot));
        root.addView(makeButton("🌡 Thermal Snapshot", this::testThermalSnapshot));

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

    private TextView makeSectionLabel(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setTextSize(16f);
        tv.setTextColor(0xFFEEEEEE);
        tv.setPadding(0, dp(14), 0, dp(6));
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
        GELServiceLog.info(msg); // για export
        appendHtml("ℹ️ " + escape(msg));
    }

    private void logOk(String msg) {
        GELServiceLog.ok(msg); // για export
        appendHtml("<font color='#88FF88'>✅ " + escape(msg) + "</font>");
    }

    private void logError(String msg) {
        GELServiceLog.error(msg); // για export
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
    }

    private void logWarn(String msg) {
        GELServiceLog.warn(msg); // για export
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
    // TESTS
    // ============================================================

    // 1) Speaker Test (basic tone)
    private void testSpeaker() {
        logLine();
        logInfo("🔊 Speaker Test ξεκίνησε (2–3 δευτ.).");
        try {
            new Thread(() -> {
                try {
                    ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000);
                    SystemClock.sleep(2300);
                    tg.release();
                    logOk("Αν ΑΚΟΥΣΤΗΚΕ καθαρός ήχος → speaker OK.");
                    logError("Αν ΔΕΝ ακούστηκε τίποτα → πιθανή βλάβη speaker / γραμμής ήχου.");
                } catch (Exception e) {
                    logError("Σφάλμα στο Speaker Test: " + e.getMessage());
                }
            }).start();
        } catch (Throwable t) {
            logError("Σφάλμα ToneGenerator: " + t.getMessage());
        }
    }

    // 1b) Speaker Sweep Test (σειρά από διαφορετικούς τόνους)
    private void testSpeakerSweep() {
        logLine();
        logInfo("🎶 Speaker Sweep Test (διαφορετικές συχνότητες ~2–3 δευτ.).");
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

                    logOk("Αν όλοι οι τόνοι ακούστηκαν καθαρά → speaker OK σε όλο το φάσμα.");
                    logError("Αν κάποιο tone «βρομάει» ή δεν ακούγεται → πιθανή βλάβη σε συγκεκριμένες συχνότητες.");
                } catch (Exception e) {
                    logError("Σφάλμα Speaker Sweep: " + e.getMessage());
                } finally {
                    if (tg != null) tg.release();
                }
            }).start();
        } catch (Throwable t) {
            logError("Σφάλμα Speaker Sweep Thread: " + t.getMessage());
        }
    }

    // 2) Earpiece basic info (οδηγίες)
    private void testEarpieceExplain() {
        logLine();
        logInfo("📞 Earpiece Basic Check (manual).");
        logInfo("1) Κάλεσε έναν αριθμό ή φωνητικό μήνυμα.");
        logInfo("2) Βάλε το τηλέφωνο στο αυτί (χωρίς handsfree).");
        logInfo("3) Αν ο ήχος είναι πολύ χαμηλός / παραμορφωμένος:");
        logError("   → πιθανή βλάβη earpiece / φίλτρου ακουστικού / υγρασία.");
        logInfo("4) Αν δεν ακούγεται τίποτα αλλά speakerphone παίζει:");
        logError("   → earpiece ή γραμμή ήχου προς επάνω μέρος βλάβη.");
    }

    // 2b) Mic Manual Check (οδηγίες για service)
    private void testMicManualInfo() {
        logLine();
        logInfo("🎙 Mic Manual Check (χωρίς root / χωρίς extra άδειες).");
        logInfo("1) Άνοιξε την εφαρμογή Εγγραφής Ήχου ή στείλε φωνητικό μήνυμα (WhatsApp / Viber κ.λπ.).");
        logInfo("2) Μίλα κανονικά από την κάτω πλευρά του κινητού (κύριο μικρόφωνο).");
        logInfo("3) Άκουσε την εγγραφή:");
        logError("   → Αν ο ήχος είναι πολύ χαμηλός / «βουίζει» / κόβεται → πιθανή βλάβη μικροφώνου ή φίλτρου.");
        logError("   → Αν δεν γράφει καθόλου → βλάβη μικροφώνου, γραμμής ή IC audio.");
        logInfo("4) Για δεύτερο μικρόφωνο (πάνω πλευρά): κάνε δοκιμή σε βίντεο ή σε loudspeaker κλήση.");
    }

    // 3) Vibration Test
    private void testVibration() {
        logLine();
        logInfo("📳 Vibration Test ξεκίνησε (1 δευτ.).");
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v == null) {
                logError("Δεν βρέθηκε Vibrator service — πιθανή βλάβη ή συσκευή χωρίς δόνηση.");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(android.os.VibrationEffect.createOneShot(
                        800, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //noinspection deprecation
                v.vibrate(800);
            }
            logOk("Αν νιώθεις δυνατή δόνηση → μοτέρ OK.");
            logError("Αν δεν υπάρχει δόνηση → βλάβη μοτέρ / επαφών δόνησης / flex.");
        } catch (Exception e) {
            logError("Σφάλμα Vibration Test: " + e.getMessage());
        }
    }

    // 4) Sensors Quick Check
    private void testSensorsQuick() {
        logLine();
        logInfo("🎛 Γρήγορος έλεγχος βασικών αισθητήρων.");

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm == null) {
                logError("Δεν βρέθηκε SensorManager — πιθανό σοβαρό πρόβλημα framework.");
                return;
            }

            List<Sensor> all = sm.getSensorList(Sensor.TYPE_ALL);
            logInfo("Σύνολο αισθητήρων: " + (all == null ? 0 : all.size()));

            boolean hasAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null;
            boolean hasGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
            boolean hasMag  = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
            boolean hasLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null;
            boolean hasProx = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null;

            if (!hasAccel) logError("Λείπει accelerometer → πιθανή βλάβη πλακέτας / motion.");
            if (!hasGyro)  logWarn("Λείπει gyroscope → περιορισμένα motion features.");
            if (!hasMag)   logWarn("Λείπει magnetometer → προβλήματα πυξίδας / navigation.");
            if (!hasLight) logWarn("Λείπει light sensor → δεν δουλεύει σωστά το auto-brightness.");
            if (!hasProx)  logError("Λείπει proximity → προβλήματα με κλείσιμο οθόνης σε κλήσεις.");

            if (hasAccel && hasGyro && hasProx) {
                logOk("Βασικοί αισθητήρες (accel / gyro / proximity) υπάρχουν.");
            }

        } catch (Exception e) {
            logError("Σφάλμα Sensors Quick Check: " + e.getMessage());
        }
    }

    // 4b) Full Sensor List (για service report)
    private void testSensorFullList() {
        logLine();
        logInfo("📋 Full Sensor List (τύπος / vendor / name).");

        try {
            SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sm == null) {
                logError("Δεν βρέθηκε SensorManager.");
                return;
            }

            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors == null || sensors.isEmpty()) {
                logError("Δεν αναφέρθηκαν αισθητήρες από το σύστημα.");
                return;
            }

            for (Sensor s : sensors) {
                String line = "• type=" + s.getType()
                        + " | name=" + s.getName()
                        + " | vendor=" + s.getVendor();
                logInfo(line);
            }

            logOk("Λίστα αισθητήρων συμπληρώθηκε για service report.");

        } catch (Exception e) {
            logError("Σφάλμα Full Sensor List: " + e.getMessage());
        }
    }

    // 5) Proximity Quick Check (οδηγίες)
    private void testProximityQuickInfo() {
        logLine();
        logInfo("📲 Proximity Quick Check (manual).");
        logInfo("1) Κάλεσε έναν αριθμό.");
        logInfo("2) Πλησίασε το τηλέφωνο στο αυτί.");
        logInfo("3) Η οθόνη ΠΡΕΠΕΙ να σβήνει όταν καλύπτεται ο αισθητήρας.");
        logError("Αν η οθόνη δεν σβήνει → πιθανή βλάβη proximity / βρόμικη προστασία / tempered glass.");
        logError("Αν σβήνει αλλά δεν ανάβει μετά → θέμα λογισμικού / αισθητήρα.");
    }

    // 6) Display / Touch Basic info
    private void testDisplayBasic() {
        logLine();
        logInfo("🖥 Βασικός έλεγχος οθόνης / αφής (manual).");
        logInfo("1) Άνοιξε ένα λευκό φόντο (π.χ. gallery ή browser).");
        logError("2) Έλεγξε για κιτρινίλες, μωβ τόνο, burn-in, σκιές — πιθανή βλάβη panel.");
        logError("3) Αν υπάρχουν νεκρά σημεία στην αφή → πιθανή βλάβη digitizer / ταινίας.");
        logInfo("4) Για πιο advanced: τρέξε κωδικούς service (όπου υποστηρίζονται από τον κατασκευαστή).");
    }

    // 7) RAM Snapshot
    private void testRamSnapshot() {
        logLine();
        logInfo("💾 Live RAM Snapshot.");

        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                logError("Δεν βρέθηκε ActivityManager.");
                return;
            }

            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);

            long avail = mi.availMem;
            long total = mi.totalMem;
            int pctFree = (int) ((avail * 100L) / total);

            logInfo("RAM διαθέσιμη τώρα: " + readable(avail) +
                    " (" + pctFree + "% ελεύθερα)");

            if (pctFree < 10) {
                logError("ΠΟΛΥ χαμηλή διαθέσιμη RAM (< 10%) — πρότεινε κλείσιμο apps / reboot.");
            } else if (pctFree < 20) {
                logWarn("Χαμηλή διαθέσιμη RAM (< 20%) — οριακή κατάσταση.");
            } else {
                logOk("RAM status: αποδεκτό για χρήση.");
            }

        } catch (Exception e) {
            logError("Σφάλμα RAM Snapshot: " + e.getMessage());
        }
    }

    // 8) Uptime / Reboots
    private void testUptime() {
        logLine();
        logInfo("⏱ Uptime / επανεκκινήσεις.");

        long upMs = SystemClock.elapsedRealtime();
        long upSec = upMs / 1000;
        long days = upSec / (24 * 3600);
        long hours = (upSec % (24 * 3600)) / 3600;
        long mins = (upSec % 3600) / 60;

        logInfo(String.format(Locale.US,
                "Uptime: %d ημέρες, %d ώρες, %d λεπτά", days, hours, mins));

        if (days < 1) {
            logWarn("Η συσκευή έχει γίνει reboot πολύ πρόσφατα — ίσως ο πελάτης έκανε ήδη επανεκκίνηση για πρόβλημα.");
        } else if (days > 7) {
            logWarn("Uptime > 7 μέρες — προτείνεται επανεκκίνηση πριν από βαθιά διάγνωση.");
        } else {
            logOk("Uptime σε φυσιολογικά επίπεδα.");
        }
    }

    // 9) Network Quick Check
    private void testNetworkQuick() {
        logLine();
        logInfo("🌐 Γρήγορος έλεγχος δικτύου.");

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            logError("Δεν βρέθηκε ConnectivityManager.");
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
            logError("Σφάλμα Network Quick Check: " + e.getMessage());
        }

        if (!hasInternet) {
            logError("Δεν υπάρχει ενεργή σύνδεση Internet αυτή τη στιγμή.");
        } else {
            if (wifi) logOk("WiFi ενεργό.");
            if (mobile) logOk("Mobile Data ενεργά.");
        }
    }

    // 10) Battery Snapshot
    private void testBatterySnapshot() {
        logLine();
        logInfo("🔋 Battery Snapshot (level / temp / health).");

        try {
            BatteryManager bm = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);

            // Best-effort: level
            int level = -1;
            if (bm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }

            if (level >= 0) {
                logInfo("Εκτιμώμενη στάθμη μπαταρίας: " + level + "%");
            } else {
                logWarn("Δεν μπόρεσα να πάρω ακριβές επίπεδο μπαταρίας.");
            }

            // Θερμοκρασία / health με ACTION_BATTERY_CHANGED
            android.content.Intent intent = registerReceiver(
                    null,
                    new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
            );

            if (intent != null) {
                int temp10 = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                float temp = (temp10 > 0) ? (temp10 / 10f) : -1f;
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

                if (temp > 0) {
                    logInfo(String.format(Locale.US, "Θερμοκρασία μπαταρίας: %.1f°C", temp));
                    if (temp > 45f) {
                        logError("Υψηλή θερμοκρασία μπαταρίας (> 45°C) — έλεγχος φορτιστή / πλακέτας.");
                    } else if (temp > 38f) {
                        logWarn("Ζεστή μπαταρία (38–45°C) — πιθανή έντονη χρήση / θερμικό θέμα.");
                    } else {
                        logOk("Θερμοκρασία μπαταρίας σε φυσιολογικά επίπεδα.");
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

                logInfo("Κατάσταση υγείας (Android flag): " + healthStr);

                if (health == BatteryManager.BATTERY_HEALTH_DEAD ||
                        health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                    logError("Η μπαταρία φαίνεται ΚΑΤΕΣΤΡΑΜΜΕΝΗ — πρότεινε αλλαγή μπαταρίας.");
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                    logError("Flag OVERHEAT — σοβαρή υπερθέρμανση, έλεγχος hardware.");
                }
            } else {
                logWarn("Δεν μπόρεσα να διαβάσω λεπτομέρειες μπαταρίας (ACTION_BATTERY_CHANGED=null).");
            }

        } catch (Exception e) {
            logError("Σφάλμα Battery Snapshot: " + e.getMessage());
        }
    }

    // 11) Thermal Snapshot (CPU όπου υποστηρίζεται)
    private void testThermalSnapshot() {
        logLine();
        logInfo("🌡 Thermal Snapshot (CPU θερμοκρασία όπου υποστηρίζεται).");

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
                        logInfo(String.format(Locale.US, "CPU θερμοκρασία: %.1f°C", t));

                        if (t > 80f) {
                            logError("Πολύ υψηλή CPU θερμοκρασία (> 80°C) — πιθανή βλάβη ψύξης / SoC.");
                        } else if (t > 70f) {
                            logWarn("Υψηλή CPU θερμοκρασία (70–80°C) — throttling / κολλήματα.");
                        } else {
                            logOk("CPU temperature: εντός φυσιολογικών ορίων.");
                        }
                    } else {
                        logWarn("Δεν διατέθηκαν θερμοκρασίες CPU από το σύστημα.");
                    }
                } else {
                    logWarn("Δεν διατέθηκε HardwarePropertiesManager — περιορισμένη thermal διάγνωση.");
                }
            } catch (Throwable t) {
                logError("Σφάλμα thermal check: " + t.getMessage());
            }
        } else {
            logWarn("Thermal APIs δεν υποστηρίζονται σε αυτήν την έκδοση Android (API < 29).");
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
```0
