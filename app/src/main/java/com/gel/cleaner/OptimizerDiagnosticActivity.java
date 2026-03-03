// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerDiagnosticActivity.java — FINAL (No crash path, Mini-focused)

package com.gel.cleaner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OptimizerDiagnosticActivity extends AppCompatActivity {

    private boolean cpu;
    private boolean thermal;
    private boolean cache;
    private double temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readExtras();
        buildUI();
    }

    private void readExtras() {
        Intent i = getIntent();
        if (i == null) return;

        cpu     = i.getBooleanExtra("mini_cpu", false);
        thermal = i.getBooleanExtra("mini_thermal", false);
        cache   = i.getBooleanExtra("mini_cache", false);
        temp    = i.getDoubleExtra("mini_temp", 0);
    }

    private void buildUI() {

        boolean gr = AppLang.isGreek(this);

        ScrollView scroll = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 80, 60, 60);
        root.setBackgroundColor(Color.BLACK);

        scroll.addView(root);

        // ================= TITLE =================
        TextView title = new TextView(this);
        title.setText(gr ? "GEL iDoctor — Smart Diagnostic" : "GEL iDoctor — Smart Diagnostic");
        title.setTextColor(Color.parseColor("#FFD700"));
        title.setTextSize(22f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // ================= SEVERITY =================
        TextView severity = new TextView(this);
        severity.setTextColor(Color.WHITE);
        severity.setTextSize(18f);
        severity.setPadding(0, 40, 0, 20);
        severity.setText(getSeverityText(gr));
        root.addView(severity);

        // ================= DETAILS =================
        TextView details = new TextView(this);
        details.setTextColor(Color.parseColor("#00FF7F"));
        details.setTextSize(15f);
        details.setPadding(0, 20, 0, 40);
        details.setText(buildDetailedMessage(gr));
        root.addView(details);

        // ================= RECOMMENDED LABS =================
        TextView labsTitle = new TextView(this);
        labsTitle.setText(gr ? "Προτεινόμενα εργαστήρια" : "Recommended Labs");
        labsTitle.setTextColor(Color.parseColor("#FFD700"));
        labsTitle.setTextSize(17f);
        labsTitle.setPadding(0, 20, 0, 20);
        root.addView(labsTitle);

        TextView labs = new TextView(this);
        labs.setTextColor(Color.parseColor("#00FF7F"));
        labs.setTextSize(15f);
        labs.setText(buildLabsText(gr));
        root.addView(labs);

        // ================= RUN LABS BUTTON =================
        Button runBtn = buildButton(
                gr ? "ΕΚΤΕΛΕΣΗ ΠΡΟΤΕΙΝΟΜΕΝΩΝ ΕΡΓΑΣΤΗΡΙΩΝ"
                   : "RUN RECOMMENDED TESTS",
                0xFF00FF7F
        );

        runBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManualTestsActivity.class));
            finish();
        });

        root.addView(runBtn);

        // ================= CACHE CLEAN BUTTON =================
        if (cache) {

            Button cacheBtn = buildButton(
                    gr ? "ΚΑΘΑΡΙΣΜΟΣ CACHE"
                       : "CACHE CLEANER",
                    0xFFFFC107
            );

            cacheBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, AppListActivity.class);
                i.putExtra("mode", "cache");
                startActivity(i);
                finish();
            });

            root.addView(cacheBtn);
        }

        // ================= LATER BUTTON =================
        Button laterBtn = buildButton(
                gr ? "ΑΡΓΟΤΕΡΑ" : "LATER",
                0xFFD50000
        );

        laterBtn.setOnClickListener(v -> finish());

        root.addView(laterBtn);

        setContentView(scroll);
    }

    private Button buildButton(String text, int backgroundColor) {

        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.BLACK);
        btn.setTextSize(15f);
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(backgroundColor);
        bg.setCornerRadius(40);
        bg.setStroke(6, Color.parseColor("#FFD700"));

        btn.setBackground(bg);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        lp.setMargins(0, 30, 0, 0);
        btn.setLayoutParams(lp);

        return btn;
    }

    private String getSeverityText(boolean gr) {

        boolean thermalCritical = (thermal && temp >= 45.0);
        boolean cpuThermalCritical = (cpu && thermalCritical);
        boolean cacheCritical = cache; // cache≥85 triggers mini

        if (thermalCritical || cpuThermalCritical || cacheCritical) {
            return gr ? "⚠ ΚΡΙΣΙΜΗ Κατάσταση"
                      : "⚠ CRITICAL Condition";
        }

        return gr ? "Ήπια ένδειξη επιβάρυνσης"
                  : "Minor system signal";
    }

    private String buildDetailedMessage(boolean gr) {

        StringBuilder sb = new StringBuilder();

        if (thermal) {
            sb.append(gr
                    ? "• Θερμοκρασία μπαταρίας: " + temp + "°C\n\n"
                    : "• Battery temperature: " + temp + "°C\n\n");
        }

        if (cpu) {
            sb.append(gr
                    ? "• Εντοπίστηκε αυξημένη χρήση CPU.\n\n"
                    : "• High CPU usage detected.\n\n");
        }

        if (cache) {
            sb.append(gr
                    ? "• Υψηλή προσωρινή μνήμη (cache) εφαρμογών.\n\n"
                    : "• High application cache usage.\n\n");
        }

        sb.append(gr
                ? "Συνιστάται έλεγχος για επιβεβαίωση."
                : "Diagnostic confirmation recommended.");

        return sb.toString();
    }

    private String buildLabsText(boolean gr) {

        return gr
                ? "• LAB 14 — Stress / Stability\n"
                + "• LAB 15 — Thermal / Overheat Analysis\n"
                + "• LAB 19 — Installed Apps Impact Analysis\n"
                + "• App Cache Cleanup (App Manager)"
                : "• LAB 14 — Stress / Stability\n"
                + "• LAB 15 — Thermal / Overheat Analysis\n"
                + "• LAB 19 — Installed Apps Impact Analysis\n"
                + "• App Cache Cleanup (App Manager)";
    }
}
