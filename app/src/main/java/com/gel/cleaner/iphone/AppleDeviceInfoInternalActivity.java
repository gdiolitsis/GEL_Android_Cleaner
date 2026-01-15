// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleDeviceInfoInternalActivity.java
// CARBON INFO with Android Internals — HARDCODED Apple DATA
// ============================================================

package com.gel.cleaner.iphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gel.cleaner.R;
import com.gel.cleaner.base.AppleSpecProvider;
import com.gel.cleaner.iphone.specs.AppleDeviceSpec;

public class AppleDeviceInfoInternalActivity extends Activity {

    // -------- SECTIONS (ίδια λογική με Android Internal) --------
    private LinearLayout secSystem;
    private LinearLayout secOS;
    private LinearLayout secCPU;
    private LinearLayout secRAM;
    private LinearLayout secDisplay;
    private LinearLayout secConnectivity;

    // -------- OUTPUT --------
    private TextView outSystem;
    private TextView outOS;
    private TextView outCPU;
    private TextView outRAM;
    private TextView outDisplay;
    private TextView outConnectivity;

    private AppleDeviceSpec d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal); // ίδιο layout

        bindViews();
        d = AppleSpecProvider.getSelectedDevice(this);

        populateAll();
    }

    // ============================================================
    // BIND
    // ============================================================
    private void bindViews() {

        // χρησιμοποιούμε Ο,ΤΙ υπάρχει ήδη στο XML
        secSystem       = findViewById(R.id.headerSystem);
        secOS           = findViewById(R.id.headerAndroid); // reuse → iOS
        secCPU          = findViewById(R.id.headerCpu);
        secRAM          = findViewById(R.id.headerRam);
        secDisplay      = findViewById(R.id.headerGpu);     // reuse → Display
        secConnectivity = findViewById(R.id.headerStorage); // reuse → Connectivity

        outSystem       = findViewById(R.id.txtSystemContent);
        outOS           = findViewById(R.id.txtAndroidContent);
        outCPU          = findViewById(R.id.txtCpuContent);
        outRAM          = findViewById(R.id.txtRamContent);
        outDisplay      = findViewById(R.id.txtGpuContent);
        outConnectivity = findViewById(R.id.txtStorageContent);
    }

    // ============================================================
    // POPULATE — ΚΑΡΜΠΟΝ ΠΛΗΡΟΦΟΡΙΩΝ
    // ============================================================
    private void populateAll() {

        if (d == null) {
            hideAll();
            return;
        }

        // -------- 1) SYSTEM --------
        show(secSystem);
        outSystem.setText(
                logInfo("Manufacturer", "Apple") +
                logInfo("SoC", d.soc)
        );

        // -------- 2) OS --------
        show(secOS);
        outOS.setText(
                logInfo("Operating System", d.os)
        );

        // -------- 3) CPU --------
        show(secCPU);
        outCPU.setText(
                logInfo("CPU", d.cpu)
        );

        // -------- 4) RAM --------
        show(secRAM);
        outRAM.setText(
                logInfo("Memory", d.ram)
        );

        // -------- 5) DISPLAY --------
        show(secDisplay);
        outDisplay.setText(
                logInfo("Screen", d.screen)
        );

        // -------- 6) CONNECTIVITY --------
        show(secConnectivity);
        outConnectivity.setText(
                logInfo("Wi-Fi", d.wifi) +
                logInfo("Bluetooth", d.bluetooth) +
                logInfo("Biometrics", d.biometrics) +
                logInfo("Port", d.port) +
                logInfo("Charging", d.charging)
        );
    }

    // ============================================================
    // HELPERS — ίδια φιλοσοφία logs
    // ============================================================
    private String logInfo(String k, String v) {
        if (v == null || v.trim().isEmpty()) return "";
        return "• " + k + ": " + v + "\n";
    }

    private void hideAll() {
        hide(secSystem);
        hide(secOS);
        hide(secCPU);
        hide(secRAM);
        hide(secDisplay);
        hide(secConnectivity);
    }

    private void hide(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void show(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }
}
