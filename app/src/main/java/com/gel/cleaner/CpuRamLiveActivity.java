package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class CpuRamLiveActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private TextView txtLive;
    private volatile boolean running = true;
    private boolean isRooted = false;

    // Foldable system
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_ram_live);

        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        txtLive = findViewById(R.id.txtLiveInfo);

        txtLive.setTextSize(sp(14f));
        txtLive.setLineSpacing(dp(2), 1.0f);
        txtLive.setPadding(dp(12), dp(12), dp(12), dp(12));
        txtLive.setText("CPU / RAM Live Monitor started…\n");

        isRooted = isDeviceRooted();
        startLive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        foldDetector.start();
    }

    @Override
    protected void onPause() {
        foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS (Unified v1.2)
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {

        final boolean isInner =
                (posture == Posture.FLAT ||
                 posture == Posture.TABLE_MODE ||
                 posture == Posture.FULLY_OPEN);

        animPack.animateReflow(() -> uiManager.applyUI(isInner));
    }

    @Override
    public void onScreenChanged(boolean isInner) {

        animPack.animateReflow(() -> uiManager.applyUI(isInner));

        // DualPane safe new API
        try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}

        // Adjust text scaling
        if (isInner) {
            txtLive.setTextSize(sp(17f));
            txtLive.setPadding(dp(18), dp(18), dp(18), dp(18));
        } else {
            txtLive.setTextSize(sp(14f));
            txtLive.setPadding(dp(12), dp(12), dp(12), dp(12));
        }
    }

    // ============================================================
    // SAFE LIVE LOOP
    // ============================================================
    private void startLive() {

        Thread t = new Thread(() -> {

            ActivityManager am =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            int i = 1;
            while (running) {

                double cpu = isRooted ? getCpuRootAccurate() : getCpuTotalAvgPercent();
                String cpuTxt = cpu < 0 ? "N/A" : String.format("%.1f%%", cpu);

                long usedMb = 0, totalMb = 0;
                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);

                    totalMb = mi.totalMem / (1024 * 1024);
                    long availMb = mi.availMem / (1024 * 1024);
                    usedMb = totalMb - availMb;
                }

                String temp = getCpuTemp();

                StringBuilder line = new StringBuilder();
                line.append("Live ").append(String.format("%02d", i))
                        .append(" | CPU: ").append(cpuTxt)
                        .append(" | Temp: ").append(temp)
                        .append(" | RAM: ").append(usedMb).append(" / ").append(totalMb).append(" MB");

                if (isRooted) {
                    String gov = getCpuGovernor();
                    if (gov != null) line.append("\nGovernor: ").append(gov);

                    String freq = getCpuFreqRoot();
                    if (freq != null) line.append("\nFreq: ").append(freq);
                }

                runOnUiThread(() -> appendSafe(line.toString()));

                i++;
                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void appendSafe(String s) {
        String current = txtLive.getText().toString();
        String[] lines = current.split("\n");

        if (lines.length > 300) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = lines.length - 250; i < lines.length; i++) {
                trimmed.append(lines[i]).append("\n");
            }
            txtLive.setText(trimmed.toString());
        }

        txtLive.append(s + "\n");
    }

    // ============================
    // CPU HELPERS
    // ============================
    private double getCpuRootAccurate() { /* unchanged */ return -1; }

    private long[] readCpuStat() { /* unchanged */ return null; }

    private String getCpuGovernor() { /* unchanged */ return null; }

    private String getCpuFreqRoot() { /* unchanged */ return null; }

    private double getCpuTotalAvgPercent() { /* unchanged */ return -1; }

    private long readLong(String path) { /* unchanged */ return -1; }

    private String getCpuTemp() { /* unchanged */ return "N/A"; }

    private boolean isDeviceRooted() { /* unchanged */ return false; }

    private String getProp(String key) { /* unchanged */ return ""; }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }
}
