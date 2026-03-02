// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerDiagnosticActivity — Smart Diagnostic Popup (Severity + Recommended Labs)

package com.gel.cleaner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OptimizerDiagnosticActivity extends AppCompatActivity {

    private boolean cpu;
    private boolean thermal;
    private boolean crash;
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
        crash   = i.getBooleanExtra("mini_crash", false);
        cache   = i.getBooleanExtra("mini_cache", false);
        temp    = i.getDoubleExtra("mini_temp", 0);
    }

    private void buildUI() {

        final boolean gr = AppLang.isGreek(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(26), dp(22), dp(18));
        root.setBackgroundColor(Color.BLACK);

        scroll.addView(root,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        // ================= TITLE =================
        TextView title = new TextView(this);
        title.setText("GEL Smart Diagnostic");
        title.setTextColor(0xFFFFD700);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(6), 0, dp(10));
        root.addView(title);

        // ================= SEVERITY =================
        TextView severity = new TextView(this);
        severity.setTextColor(Color.WHITE);
        severity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        severity.setPadding(0, dp(6), 0, dp(6));
        severity.setText(getSeverityText(gr));
        root.addView(severity);

        // ================= DETAILS =================
        TextView details = new TextView(this);
        details.setTextColor(0xFF00FF7F);
        details.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        details.setPadding(0, dp(10), 0, dp(14));
        details.setText(buildDetailedMessage(gr));
        root.addView(details);

        // ================= RECOMMENDED LABS =================
        TextView labsTitle = new TextView(this);
        labsTitle.setText(gr ? "Προτεινόμενα εργαστήρια" : "Recommended labs");
        labsTitle.setTextColor(0xFFFFD700);
        labsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        labsTitle.setTypeface(null, Typeface.BOLD);
        labsTitle.setPadding(0, dp(8), 0, dp(8));
        root.addView(labsTitle);

        TextView labs = new TextView(this);
        labs.setTextColor(0xFF00FF7F);
        labs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        labs.setPadding(0, 0, 0, dp(18));
        labs.setText(buildRecommendedLabsText(gr));
        root.addView(labs);

        // ================= ACTION BUTTON =================
        Button runBtn = mkNeonGreenGoldBtn(gr ? "RUN RECOMMENDED TESTS" : "RUN RECOMMENDED TESTS");
        runBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManualTestsActivity.class));
            finish();
        });
        root.addView(runBtn);

        // ================= QUICK FIX (CACHE) =================
        if (cache && !crash) {
            Button fixBtn = mkNeonGreenGoldBtn(gr ? "ΑΜΕΣΟΣ ΚΑΘΑΡΙΣΜΟΣ CACHE" : "QUICK CACHE CLEANUP");
            fixBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, AppListActivity.class);
                i.putExtra("mode", "cache");
                startActivity(i);
                finish();
            });
            fixBtn.setPadding(dp(14), dp(12), dp(14), dp(12));
            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            p.topMargin = dp(10);
            root.addView(fixBtn, p);
        }

        // ================= CLOSE =================
        Button closeBtn = mkRedGoldBtn(gr ? "LATER" : "LATER");
        closeBtn.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams p2 =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        p2.topMargin = dp(12);
        root.addView(closeBtn, p2);

        setContentView(scroll);
    }

    private String getSeverityText(boolean gr) {

        // CRITICAL RULES (your spec)
        boolean criticalThermal = thermal && temp >= 45.0;
        boolean criticalCrash   = crash; // always notify on 1 crash

        int score = 0;
        if (criticalCrash) score += 4;
        if (criticalThermal) score += 4;

        // moderate scoring
        if (thermal) score += 2;
        if (cpu) score += 1;
        if (cache) score += 1;

        if (criticalCrash || criticalThermal || score >= 6) {
            return gr ? "⚠ ΚΡΙΣΙΜΗ Κατάσταση" : "⚠ CRITICAL Condition";
        } else if (score >= 2) {
            return gr ? "⚠ Μέτρια Επιβάρυνση" : "⚠ Moderate Load";
        } else {
            return gr ? "Ήπια ένδειξη επιβάρυνσης" : "Minor system signal";
        }
    }

    private String buildDetailedMessage(boolean gr) {

        StringBuilder sb = new StringBuilder();

        if (crash) {
            sb.append(gr
                    ? "• Εντοπίστηκε πρόσφατο crash / ANR / low-memory.\n\n"
                    : "• Recent crash / ANR / low-memory detected.\n\n");
        }

        if (thermal) {
            String t = (temp > 0 ? String.format("%.1f", temp) : "—");
            sb.append(gr
                    ? "• Θερμοκρασία μπαταρίας: " + t + "°C\n\n"
                    : "• Battery temperature: " + t + "°C\n\n");
        }

        if (cpu && thermal) {
            sb.append(gr
                    ? "• Υψηλό CPU load σε συνδυασμό με θερμοκρασία.\n\n"
                    : "• High CPU load combined with thermal increase.\n\n");
        } else if (cpu) {
            sb.append(gr
                    ? "• Παρατηρήθηκε στιγμιαίο CPU spike.\n\n"
                    : "• A short CPU spike was detected.\n\n");
        }

        if (cache) {
            sb.append(gr
                    ? "• Υψηλή προσωρινή μνήμη (cache) εφαρμογών.\n\n"
                    : "• High application cache usage.\n\n");
        }

        if (sb.length() == 0) {
            sb.append(gr
                    ? "Δεν εντοπίστηκε σοβαρή ανωμαλία."
                    : "No major anomaly detected.");
        } else {
            sb.append(gr
                    ? "Συνιστάται έλεγχος για επιβεβαίωση."
                    : "A diagnostic is recommended.");
        }

        return sb.toString().trim();
    }

    private String buildRecommendedLabsText(boolean gr) {

        List<String> labs = new ArrayList<>();

        // Crash path
        if (crash) {
            labs.add(gr ? "• LAB 14 — Stress / Stability" : "• LAB 14 — Stress / Stability");
            labs.add(gr ? "• LAB 15 — Thermal / Overheat Analysis" : "• LAB 15 — Thermal / Overheat Analysis");
            labs.add(gr ? "• LAB 19 — Installed Apps Impact Analysis" : "• LAB 19 — Installed Apps Impact Analysis");
        }

        // Thermal path
        if (thermal) {
            labs.add(gr ? "• LAB 15 — Thermal / Overheat Analysis" : "• LAB 15 — Thermal / Overheat Analysis");
            labs.add(gr ? "• LAB 14 — Stress / Stability" : "• LAB 14 — Stress / Stability");
        }

        // CPU spike path
        if (cpu) {
            labs.add(gr ? "• LAB 14 — Stress / Stability" : "• LAB 14 — Stress / Stability");
            labs.add(gr ? "• LAB 19 — Installed Apps Impact Analysis" : "• LAB 19 — Installed Apps Impact Analysis");
        }

        // Cache path
        if (cache) {
            labs.add(gr ? "• App Cache Cleanup (App Manager)" : "• App Cache Cleanup (App Manager)");
            labs.add(gr ? "• LAB 19 — Installed Apps Impact Analysis" : "• LAB 19 — Installed Apps Impact Analysis");
        }

        // de-dup
        List<String> uniq = new ArrayList<>();
        for (String s : labs) if (!uniq.contains(s)) uniq.add(s);

        if (uniq.isEmpty()) {
            return gr
                    ? "• Προαιρετικά: LAB 19 — Installed Apps Impact Analysis\n• Προαιρετικά: LAB 14 — Stress / Stability"
                    : "• Optional: LAB 19 — Installed Apps Impact Analysis\n• Optional: LAB 14 — Stress / Stability";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : uniq) sb.append(s).append("\n");
        return sb.toString().trim();
    }

    // =========================
    // GEL BUTTONS
    // =========================
    private Button mkNeonGreenGoldBtn(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.BLACK);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTypeface(null, Typeface.BOLD);
        b.setPadding(dp(14), dp(12), dp(14), dp(12));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF00FF7F);     // neon green
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(3), 0xFFFFD700); // gold
        b.setBackground(bg);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        p.topMargin = dp(8);
        b.setLayoutParams(p);

        return b;
    }

    private Button mkRedGoldBtn(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setTypeface(null, Typeface.BOLD);
        b.setPadding(dp(14), dp(12), dp(14), dp(12));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFFB00020);     // red
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(3), 0xFFFFD700); // gold
        b.setBackground(bg);

        return b;
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                getResources().getDisplayMetrics()
        );
    }
}
