// GDiolitsis Engine Lab (GEL) — Author & Developer
// CoreMonitorActivity.java — v1.0 (Per-Core Hz + Status)

package com.gel.cleaner;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CoreMonitorActivity extends AppCompatActivity {

    static {
        System.loadLibrary("corefreq");
    }

    private TextView txtCores;
    private boolean running = true;

    public native String getCoreInfoNative();  // returns full per-core text

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_monitor);

        txtCores = findViewById(R.id.txtCoreInfo);

        startLoop();
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }

    private void startLoop() {
        new Thread(() -> {

            while (running) {
                String info = getCoreInfoNative();

                runOnUiThread(() -> txtCores.setText(info));

                try { Thread.sleep(800); } catch (Exception ignored) {}
            }

        }).start();
    }
}
