package com.gel.cleaner;

import android.content.Intent;
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
        root.setPadding(40, 60, 40, 40);

        scroll.addView(root);

        // TITLE
        TextView title = new TextView(this);
        title.setTextSize(20f);
        title.setGravity(Gravity.CENTER);
        title.setText(gr ?
                "Έξυπνη Διάγνωση Συστήματος" :
                "Smart System Diagnostic");
        root.addView(title);

        // MESSAGE
        TextView message = new TextView(this);
        message.setTextSize(16f);
        message.setPadding(0, 40, 0, 40);
        message.setText(buildMessage(gr));
        root.addView(message);

        // BUTTON 1 — RUN TESTS
        Button runBtn = new Button(this);
        runBtn.setText(gr ? "Έλεγχος Συστήματος" : "Run Diagnostics");
        runBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManualTestsActivity.class));
            finish();
        });
        root.addView(runBtn);

        // BUTTON 2 — QUICK FIX (αν cache)
        if (cache) {
            Button fixBtn = new Button(this);
            fixBtn.setText(gr ? "Καθαρισμός Cache" : "Clean Cache");
            fixBtn.setOnClickListener(v -> {
                Intent i = new Intent(this, AppListActivity.class);
                i.putExtra("mode", "cache");
                startActivity(i);
                finish();
            });
            root.addView(fixBtn);
        }

        // BUTTON 3 — CLOSE
        Button closeBtn = new Button(this);
        closeBtn.setText(gr ? "Αργότερα" : "Later");
        closeBtn.setOnClickListener(v -> finish());
        root.addView(closeBtn);

        setContentView(scroll);
    }

    private String buildMessage(boolean gr) {

        StringBuilder sb = new StringBuilder();

        if (crash) {
            sb.append(gr ?
                    "• Εντοπίστηκε πρόσφατο crash ή ANR.\n\n" :
                    "• Recent crash or ANR detected.\n\n");
        }

        if (thermal) {
            sb.append(gr ?
                    "• Υψηλή θερμοκρασία: " + temp + "°C\n\n" :
                    "• High temperature detected: " + temp + "°C\n\n");
        }

        if (cpu && thermal) {
            sb.append(gr ?
                    "• Υψηλό φορτίο CPU σε συνδυασμό με θερμοκρασία.\n\n" :
                    "• High CPU load combined with temperature.\n\n");
        }

        if (cache) {
            sb.append(gr ?
                    "• Αυξημένη προσωρινή μνήμη εφαρμογών.\n\n" :
                    "• High application cache usage detected.\n\n");
        }

        if (sb.length() == 0) {
            sb.append(gr ?
                    "Δεν εντοπίστηκε σοβαρό πρόβλημα.\n\n" :
                    "No critical issue detected.\n\n");
        }

        sb.append(gr ?
                "Συνιστάται πλήρης έλεγχος για επιβεβαίωση." :
                "A full diagnostic is recommended.");

        return sb.toString();
    }
}
