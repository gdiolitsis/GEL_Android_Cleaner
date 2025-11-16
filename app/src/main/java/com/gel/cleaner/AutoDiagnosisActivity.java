package com.gel.cleaner;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AutoDiagnosisActivity extends AppCompatActivity {

    private TextView txtDiag;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_diagnosis);

        txtDiag = findViewById(R.id.txtDiag);
        scroll = findViewById(R.id.scrollDiag);

        txtDiag.setMovementMethod(new ScrollingMovementMethod());
        txtDiag.setTextSize(15f);

        runDiagnostics();
    }

    /* ============================================================
     * HTML + COLOR LOGGING
     * ============================================================ */
    private void appendHtml(String html) {
        CharSequence prev = txtDiag.getText();
        CharSequence next = Html.fromHtml(html + "<br>");
        txtDiag.setText(TextUtils.concat(prev, next));

        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void logInfo(String msg) {
        appendHtml("ℹ️ " + escape(msg));
    }

    private void logOk(String msg) {
        appendHtml("<font color='#66FF66'>✅ " + escape(msg) + "</font>");
    }

    private void logWarn(String msg) {
        appendHtml("<font color='#FFD700'>⚠️ " + escape(msg) + "</font>");
    }

    private void logError(String msg) {
        appendHtml("<font color='#FF5555'>❌ " + escape(msg) + "</font>");
    }

    private void logAccessDenied(String area) {
        appendHtml("<font color='#FFD700'>⚠️ Access Denied — Firmware Restriction (" +
                escape(area) + ")</font>");
    }

    private String escape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /* ============================================================
     * AUTO DIAG ENGINE
     * ============================================================ */
    private void runDiagnostics() {

        logInfo("🔍 Starting Auto-Diagnosis...");

        // DEVICE MODEL
        try {
            logOk("Device Model: " + android.os.Build.MODEL);
        } catch (Exception e) {
            logAccessDenied("Device Model");
        }

        // ANDROID VERSION
        try {
            logOk("Android Version: " + android.os.Build.VERSION.RELEASE);
        } catch (Exception e) {
            logAccessDenied("Android Version");
        }

        // MANUFACTURER
        try {
            logOk("Manufacturer: " + android.os.Build.MANUFACTURER);
        } catch (Exception e) {
            logAccessDenied("Manufacturer");
        }

        // SECURITY PATCH
        try {
            logOk("Security Patch: " + android.os.Build.VERSION.SECURITY_PATCH);
        } catch (Exception e) {
            logAccessDenied("Security Patch");
        }

        // STORAGE
        try {
            long freeBytes = getFilesDir().getFreeSpace();
            long freeMB = freeBytes / (1024L * 1024L);
            logInfo("Free Internal Storage: " + freeMB + " MB");
        } catch (Exception e) {
            logAccessDenied("Storage Check");
        }

        // RAM
        try {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory() / (1024L * 1024L);
            long total = rt.totalMemory() / (1024L * 1024L);
            logInfo("RAM: " + free + "MB free / " + total + "MB total");
        } catch (Exception e) {
            logAccessDenied("RAM Check");
        }

        logOk("Auto-Diagnosis Completed.");
    }
}
