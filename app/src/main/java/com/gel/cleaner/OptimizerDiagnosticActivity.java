package com.gel.cleaner;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
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

    private int severityLevel; // 0=minor,1=moderate,2=critical

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readExtras();
        evaluateSeverity();
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

    private void evaluateSeverity() {

        if (crash) {
            severityLevel = 2;
            return;
        }

        if (thermal && temp >= 45.0) {
            severityLevel = 2;
            return;
        }

        if (cpu && thermal) {
            severityLevel = 1;
            return;
        }

        if (cache) {
            severityLevel = 1;
            return;
        }

        severityLevel = 0;
    }

    private void buildUI() {

        boolean gr = AppLang.isGreek(this);

        ScrollView scroll = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 90, 60, 60);
        root.setBackgroundColor(Color.BLACK);

        scroll.addView(root);

        // ================= TITLE =================
        TextView title = new TextView(this);
        title.setText("GEL Smart Diagnostic");
        title.setTextColor(Color.parseColor("#FFD700"));
        title.setTextSize(22f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // ================= SEVERITY =================
        TextView severity = new TextView(this);
        severity.setTextSize(17f);
        severity.setPadding(0, 50, 0, 20);
        severity.setTypeface(null, Typeface.BOLD);

        if (severityLevel == 2) {
            severity.setTextColor(Color.parseColor("#FF3B3B"));
        } else if (severityLevel == 1) {
            severity.setTextColor(Color.parseColor("#FFA500"));
        } else {
            severity.setTextColor(Color.WHITE);
        }

        severity.setText(getSeverityText(gr));
        root.addView(severity);

        // ================= DETAILS =================
        TextView details = new TextView(this);
        details.setTextColor(Color.parseColor("#00FF7F"));
        details.setTextSize(15f);
        details.setPadding(0, 20, 0, 60);
        details.setText(buildDetailedMessage(gr));
        root.addView(details);

        LinearLayout.LayoutParams btnParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        btnParams.setMargins(0, 25, 0, 25);

        // ================= RUN BUTTON =================
        Button runBtn = new Button(this);
        runBtn.setText(gr ? "Προτεινόμενος Έλεγχος"
                : "Run Recommended Tests");

        runBtn.setLayoutParams(btnParams);
        runBtn.setTextColor(Color.BLACK);
        runBtn.setTextSize(16f);
        runBtn.setAllCaps(false);

        GradientDrawable runBg = new GradientDrawable();
        runBg.setColor(Color.parseColor("#00FF7F"));
        runBg.setCornerRadius(25);
        runBg.setStroke(5, Color.parseColor("#FFD700"));
        runBtn.setBackground(runBg);

        runBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManualTestsActivity.class));
            finish();
        });

        root.addView(runBtn);

        // 🔥 Pulse animation if critical
        if (severityLevel == 2) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(runBtn, "scaleX", 1f, 1.05f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(runBtn, "scaleY", 1f, 1.05f);
            scaleX.setDuration(800);
            scaleY.setDuration(800);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setInterpolator(new DecelerateInterpolator());
            scaleY.setInterpolator(new DecelerateInterpolator());
            scaleX.start();
            scaleY.start();
        }

        // ================= QUICK FIX (Cache Only) =================
        if (cache && !crash) {

            Button fixBtn = new Button(this);
            fixBtn.setText(gr ? "Άμεσος Καθαρισμός Cache"
                    : "Quick Cache Cleanup");

            fixBtn.setLayoutParams(btnParams);
            fixBtn.setTextColor(Color.BLACK);
            fixBtn.setAllCaps(false);

            GradientDrawable fixBg = new GradientDrawable();
            fixBg.setColor(Color.parseColor("#00FF7F"));
            fixBg.setCornerRadius(25);
            fixBg.setStroke(5, Color.parseColor("#FFD700"));

            fixBtn.setBackground(fixBg);

            fixBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, AppListActivity.class);
                i.putExtra("mode", "cache");
                startActivity(i);
                finish();
            });

            root.addView(fixBtn);
        }

        // ================= CLOSE BUTTON =================
        Button closeBtn = new Button(this);
        closeBtn.setText(gr ? "Αργότερα" : "Later");
        closeBtn.setLayoutParams(btnParams);
        closeBtn.setTextColor(Color.WHITE);
        closeBtn.setAllCaps(false);

        GradientDrawable closeBg = new GradientDrawable();
        closeBg.setColor(Color.parseColor("#B00020"));
        closeBg.setCornerRadius(25);

        closeBtn.setBackground(closeBg);
        closeBtn.setOnClickListener(v -> finish());

        root.addView(closeBtn);

        setContentView(scroll);
    }

    private String getSeverityText(boolean gr) {

        if (severityLevel == 2) {
            return gr ? "⚠ ΚΡΙΣΙΜΗ Κατάσταση"
                      : "⚠ CRITICAL Condition";
        } else if (severityLevel == 1) {
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
