package com.gel.cleaner.base;

import android.widget.TextView;

import com.gel.cleaner.R;
import com.gel.cleaner.iphone.AppleDeviceSpec;

public class AppleUiBinder {

    // =====================================================
    // INTERNAL
    // =====================================================
    public static void bindInternal(TextView out, AppleDeviceSpec d) {

        if (d == null) {
            out.setText("❌ No Apple device selected");
            return;
        }

        String txt =
                "📱 MODEL: " + d.model + "\n" +
                "🧠 CHIP: " + d.chip + "\n" +
                "📐 ARCH: " + d.arch + "\n" +
                "💾 RAM: " + d.ram + "\n" +
                "🔋 BATTERY: " + d.battery + "\n" +
                "📆 RELEASE: " + d.releaseYear;

        out.setText(txt);
    }

    // =====================================================
    // PERIPHERALS
    // =====================================================
    public static void bindPeripherals(TextView out, AppleDeviceSpec d) {

        if (d == null) {
            out.setText("❌ No Apple device selected");
            return;
        }

        String txt =
                "📷 CAMERA: " + d.camera + "\n" +
                "📡 MODEM: " + d.modem + "\n" +
                "📶 WIFI: " + d.wifi + "\n" +
                "🛰 GPS: " + d.gps + "\n" +
                "🔊 AUDIO: " + d.audio + "\n" +
                "🔌 PORT: " + d.port;

        out.setText(txt);
    }
}
