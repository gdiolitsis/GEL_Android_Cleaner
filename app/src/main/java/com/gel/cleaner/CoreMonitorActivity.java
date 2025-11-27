// GDiolitsis Engine Lab (GEL)
// CoreMonitorActivity.java — GOLD Title + Neon States

package com.gel.cleaner;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CoreMonitorActivity extends AppCompatActivity {

    static {
        System.loadLibrary("corefreq");
    }

    private TextView txtInfo;

    public native String getCoreInfoNative();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_monitor);

        txtInfo = findViewById(R.id.txtCoreInfo);

        updateUI();
    }

    private void updateUI() {
        new Thread(() -> {

            while (true) {
                String raw = getCoreInfoNative();

                // ===============================================
                //  NEON COLORING FOR [OK] / [BOOST]
                // ===============================================
                String html = raw
                        .replace("[OK]",    "<font color='#00FF66'>[OK]</font>")
                        .replace("[BOOST]", "<font color='#00FF66'>[BOOST]</font>");

                runOnUiThread(() ->
                        txtInfo.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY))
                );

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }

        }).start();
    }
}
