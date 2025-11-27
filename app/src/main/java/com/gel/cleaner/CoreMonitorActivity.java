// GDiolitsis Engine Lab (GEL) — FINAL v4.0
// CoreMonitorActivity.java — GOLD TITLE + Neon States + <br> Correct Layout

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

        startLoop();
    }

    private void startLoop() {
        new Thread(() -> {
            while (true) {

                String raw = getCoreInfoNative();

                // Neon highlight
                String html = raw
                        .replace("[OK]", "<font color='#00FF66'>[OK]</font>")
                        .replace("[BOOST]", "<font color='#00FF66'>[BOOST]</font>")
                        .replace("[MAX]", "<font color='#00FF66'>[MAX]</font>")
                        .replace("[SUSPECT]", "<font color='#FF3333'>[SUSPECT]</font>")
                        .replace("[OFFLINE]", "<font color='#888888'>[OFFLINE]</font>");

                runOnUiThread(() ->
                        txtInfo.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY))
                );

                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
        }).start();
    }
}
