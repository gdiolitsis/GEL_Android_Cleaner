package com.gel.cleaner;

import android.content.Intent;
import android.graphics.Color;
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

        boolean gr = AppLang.isGreek(this);

        ScrollView scroll = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 80, 60, 60);
        root.setBackgroundColor(Color.BLACK);

        scroll.addView(root);

        // ================= TITLE =================
        TextView title = new TextView(this);
        title.setText(gr ? "GEL Smart Diagnostic"
                : "GEL Smart Diagnostic");
        title.setTextColor(Color.parseColor("#FFD700"));
        title.setTextSize(22f);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // ================= SEVERITY =================
        TextView severity = new TextView(this);
        severity.setTextColor(Color.WHITE);
        severity.setTextSize(16f);
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

        // ================= ACTION BUTTON =================
        Button runBtn = new Button(this);
        runBtn.setText(gr ? "Προτεινόμενος Έλεγχος"
                : "Run Recommended Tests");
        runBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManualTestsActivity.class));
            finish();
        });
        root.addView(runBtn);

        // ================= QUICK FIX =================
        if (cache && !crash) {
            Button fixBtn = new Button(this);
            fixBtn.setText(gr ? "Άμεσος Καθαρισμός Cache"
                    : "Quick Cache Cleanup");
            fixBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, AppListActivity.class);
                i.putExtra("mode", "cache");
                startActivity(i);
                finish();
            });
            root.addView(fixBtn);
        }

        // ================= CLOSE =================
        Button closeBtn = new Button(this);
        closeBtn.setText(gr ? "Αργότερα" : "Later");
        closeBtn.setOnClickListener(v -> finish());
        root.addView(closeBtn);

        setContentView(scroll);
    }

    private String getSeverityText(boolean gr) {

        int score = 0;

        if (crash) score += 3;
        if (thermal) score += 2;
        if (cpu) score += 1;
        if (cache) score += 1;

        if (score >= 4) {
            return gr ? "⚠ ΚΡΙΣΙΜΗ Κατάσταση"
                      : "⚠ CRITICAL Condition";
        } else if (score >= 2) {
            return gr ? "⚠ Μέτρια Επιβάρυνση"
                      : "⚠ Moderate Load";
        } else {
            return gr ? "Ήπια ένδειξη επιβάρυνσης"
                      : "Minor system signal";
        }
    }

    private String buildDetailedMessage(boolean gr) {

        StringBuilder sb = new StringBuilder();

        if (crash) {
            sb.append(gr ?
                    "• Εντοπίστηκε πρόσφατο crash / ANR.\n\n"
                    : "• Recent crash / ANR detected.\n\n");
        }

        if (thermal) {
            sb.append(gr ?
                    "• Θερμοκρασία: " + temp + "°C\n\n"
                    : "• Temperature: " + temp + "°C\n\n");
        }

        if (cpu && thermal) {
            sb.append(gr ?
                    "• Υψηλό CPU load σε συνδυασμό με θερμοκρασία.\n\n"
                    : "• High CPU load combined with thermal increase.\n\n");
        }

        if (cache) {
            sb.append(gr ?
                    "• Υψηλή προσωρινή μνήμη εφαρμογών.\n\n"
                    : "• High application cache usage.\n\n");
        }

        if (sb.length() == 0) {
            sb.append(gr ?
                    "Δεν εντοπίστηκε σοβαρή ανωμαλία."
                    : "No major anomaly detected.");
        } else {
            sb.append(gr ?
                    "Συνιστάται πλήρης έλεγχος για επιβεβαίωση."
                    : "A full diagnostic is recommended.");
        }

        return sb.toString();
    }
}
