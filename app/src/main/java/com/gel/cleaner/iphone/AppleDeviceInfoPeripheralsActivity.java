// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoPeripheralsActivity.java
// CARBON INFO with Android Peripherals — HARDCODED Apple DATA
// ============================================================

package com.gel.cleaner;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gel.cleaner.iphone.AppleDeviceSpec;
import com.gel.cleaner.iphone.AppleSpecs;

public class AppleDeviceInfoPeripheralsActivity extends Activity {

    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ---------------- ROOT ----------------
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(root);
        setContentView(scroll);

        // ---------------- LOAD SELECTED APPLE DEVICE ----------------
        AppleDeviceSpec d = AppleSpecs.getSelected(this);

        if (d == null) {
            addError("❌ No Apple device selected");
            return;
        }

        // ============================================================
        // PERIPHERALS — SECTIONS (CARBON INFO WITH ANDROID)
        // ============================================================

        // 1️⃣ CAMERA
        section("📷 CAMERA",
                info("Main Camera", d.cameraMain),
                info("Ultra-Wide", d.cameraUltraWide),
                info("Telephoto", d.cameraTele),
                info("Front Camera", d.cameraFront),
                info("Video", d.cameraVideo)
        );

        // 2️⃣ MODEM / CELLULAR
        section("📡 MODEM & CELLULAR",
                info("Modem", d.modem),
                info("5G Support", yesNo(d.has5G)),
                info("LTE", yesNo(d.hasLTE)),
                info("SIM Slots", d.simSlots),
                info("eSIM", yesNo(d.hasESim))
        );

        // 3️⃣ WIFI / BLUETOOTH
        section("📶 CONNECTIVITY",
                info("Wi-Fi", d.wifi),
                info("Bluetooth", d.bluetooth),
                info("AirDrop", yesNo(d.hasAirDrop)),
                info("NFC", yesNo(d.hasNFC))
        );

        // 4️⃣ GPS / SENSORS
        section("🛰 GPS & SENSORS",
                info("GPS", d.gps),
                info("Compass", yesNo(d.hasCompass)),
                info("Gyroscope", yesNo(d.hasGyro)),
                info("Accelerometer", yesNo(d.hasAccel)),
                info("Barometer", yesNo(d.hasBarometer))
        );

        // 5️⃣ AUDIO
        section("🔊 AUDIO",
                info("Speakers", d.speakers),
                info("Dolby Audio", yesNo(d.hasDolby)),
                info("Microphones", d.microphones),
                info("Headphone Jack", yesNo(d.hasJack))
        );

        // 6️⃣ PORTS
        section("🔌 PORTS",
                info("Charging Port", d.port),
                info("USB Standard", d.usbStandard),
                info("Fast Charge", yesNo(d.hasFastCharge)),
                info("Wireless Charge", yesNo(d.hasWirelessCharge))
        );

        // 7️⃣ BIOMETRICS
        section("🔐 BIOMETRICS",
                info("Face ID", yesNo(d.hasFaceID)),
                info("Touch ID", yesNo(d.hasTouchID))
        );

        // 8️⃣ DISPLAY I/O
        section("🖥 DISPLAY I/O",
                info("Screen Output", d.displayOut),
                info("AirPlay", yesNo(d.hasAirPlay))
        );

        // 9️⃣ STORAGE I/O
        section("💾 STORAGE I/O",
                info("Internal Storage", d.storageOptions),
                info("External SD", "❌ Not supported (Apple design)")
        );

        // 🔚 DONE
        addFooter("GEL — Apple Peripherals (Hardcoded Specs)");
    }

    // ============================================================
    // UI HELPERS — SAME FEEL AS ANDROID (LABEL WHITE / VALUE COLOR)
    // ============================================================

    private void section(String title, TextView... rows) {
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextColor(Color.WHITE);
        t.setTextSize(18f);
        t.setPadding(0, dp(12), 0, dp(6));
        t.setGravity(Gravity.START);
        root.addView(t);

        for (TextView r : rows) {
            root.addView(r);
        }
    }

    private TextView info(String label, String value) {
        TextView tv = new TextView(this);
        tv.setText("• " + label + ": " + safe(value));
        tv.setTextSize(15f);
        tv.setPadding(dp(8), dp(4), dp(8), dp(4));
        tv.setTextColor(colorForValue(value));
        return tv;
    }

    private void addError(String msg) {
        TextView t = new TextView(this);
        t.setText(msg);
        t.setTextColor(0xFFFF4444);
        t.setTextSize(16f);
        root.addView(t);
    }

    private void addFooter(String msg) {
        TextView t = new TextView(this);
        t.setText("\n" + msg);
        t.setTextColor(0xFF888888);
        t.setTextSize(12f);
        t.setGravity(Gravity.CENTER);
        root.addView(t);
    }

    // ============================================================
    // VALUE HELPERS
    // ============================================================

    private String yesNo(boolean b) {
        return b ? "Yes" : "No";
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "—" : s;
    }

    // ίδια λογική με logInfo / logOk / logWarn / logError
    private int colorForValue(String v) {
        if (v == null) return 0xFFAAAAAA;

        String x = v.toLowerCase();

        if (x.contains("no") || x.contains("not supported") || x.contains("❌"))
            return 0xFFFF4444;      // logError

        if (x.contains("yes") || x.contains("supported"))
            return 0xFF4CAF50;      // logOk

        if (x.contains("limited") || x.contains("partial"))
            return 0xFFFFC107;      // logWarn

        return 0xFFDDDDDD;          // logInfo
    }

    // ============================================================
    // DIMEN
    // ============================================================
    private int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
