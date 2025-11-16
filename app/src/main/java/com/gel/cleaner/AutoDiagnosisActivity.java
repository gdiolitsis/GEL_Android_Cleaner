package com.gdiolitsis.gelcleaner;

import android.os.Bundle;
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

        runDiagnostics();
    }

    private void runDiagnostics() {
        append("🔍 Starting Auto-Diagnosis...\n");

        // -------------------------
        // 📌 DEVICE MODEL
        // -------------------------
        String model = android.os.Build.MODEL;
        append("📱 Device Model: " + model);

        // -------------------------
        // 📌 ANDROID VERSION
        // -------------------------
        String androidVer = android.os.Build.VERSION.RELEASE;
        append("🤖 Android Version: " + androidVer);

        // -------------------------
        // 📌 MANUFACTURER
        // -------------------------
        String manufacturer = android.os.Build.MANUFACTURER;
        append("🏭 Manufacturer: " + manufacturer);

        // -------------------------
        // 📌 SECURITY PATCH LEVEL
        // -------------------------
        String securityPatch = android.os.Build.VERSION.SECURITY_PATCH;
        append("🔐 Security Patch: " + securityPatch);

        // -------------------------
        // 📌 STORAGE CHECK
        // -------------------------
        try {
            long freeBytes = getFilesDir().getFreeSpace();
            long freeMB = freeBytes / (1024 * 1024);
            append("💾 Free Internal Storage: " + freeMB + " MB");
        } catch (Exception e) {
            append("⚠ Storage check not available on this device.");
        }

        // -------------------------
        // 📌 RAM CHECK
        // -------------------------
        try {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory() / (1024 * 1024);
            long total = rt.totalMemory() / (1024 * 1024);
            append("🐏 RAM: " + free + "MB free / " + total + "MB total");
        } catch (Exception e) {
            append("⚠ RAM check not available.");
        }

        append("\n✅ Auto-Diagnosis Completed.");
    }

    private void append(String msg) {
        txtDiag.append(msg + "\n");

        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
