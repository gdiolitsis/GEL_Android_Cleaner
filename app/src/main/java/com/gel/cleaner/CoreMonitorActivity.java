// GDiolitsis Engine Lab (GEL) — Author & Developer
// CoreMonitorActivity.java — FINAL v7 (Gold Title + Neon States)

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

    private native String getCoreInfoNative();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_monitor);

        TextView txt = findViewById(R.id.txtCoreData);

        // ============================================================
        // RAW TEXT from native
        // ============================================================
        String data = getCoreInfoNative();
        if (data == null) data = "N/A";

        // ============================================================
        // GOLD TITLE
        // ============================================================
        String html =
                "<font color='#FFD700'><b>GEL Core Monitor</b></font><br><br>";

        // ============================================================
        // COLOR THE STATES
        // ============================================================
        String[] lines = data.split("\n");

        for (String line : lines) {

            String styled = line;

            // Neon green for OK
            if (line.contains("[OK]")) {
                styled = line.replace("[OK]",
                        "<font color='#00FF66'><b>[OK]</b></font>");
            }

            // Neon green for BOOST
            if (line.contains("[BOOST]")) {
                styled = line.replace("[BOOST]",
                        "<font color='#00FF66'><b>[BOOST]</b></font>");
            }

            html += styled + "<br>";
        }

        // ============================================================
        // APPLY HTML
        // ============================================================
        txt.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
    }
}
