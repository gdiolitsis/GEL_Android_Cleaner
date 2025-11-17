package com.gel.cleaner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// ============================================================
// GEL Service Lab — Main Diagnosis Menu (UI Fix: Center + White Text)
// ============================================================
public class DiagnosisMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);
        root.setBackgroundColor(0xFF101010);

        // TITLE
        TextView title = new TextView(this);
        title.setText("🔬 GEL Service Lab");
        title.setTextSize(22f);
        title.setTextColor(0xFFFFFFFF);        // White title
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        // SUBTITLE
        TextView sub = new TextView(this);
        sub.setText("Επαγγελματική διάγνωση συσκευής\nAuto + Manual tests + Export report");
        sub.setTextSize(14f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.setPadding(0, 0, 0, dp(16));
        root.addView(sub);

        // =========================
        // 🟦 AUTO DIAGNOSIS
        // =========================
        root.addView(sectionLabel("AUTO DIAGNOSIS"));

        // 🔥 ΕΙΔΙΚΟ ΚΟΥΜΠΙ ΜΕ ΣΗΜΑ & NEON GREEN TEXT
        View autoBtn = makeMedicalBlockButton(
                "GEL Phone Diagnosis",
                "Πλήρης αυτόματη διάγνωση 20 εργαστηριακών ελέγχων\nHardware • RAM • Storage • Battery • Network • Sensors…"
        );
        autoBtn.setOnClickListener(v ->
                startActivity(new Intent(this, PerformanceDiagnosticsActivity.class)));
        root.addView(autoBtn);

        // =========================
        // 🟩 MANUAL TESTS
        // =========================
        root.addView(sectionLabel("MANUAL TESTS"));

        View manualBtn = makeBlockButton(
                "🧪 Manual Tests",
                "Στοχευμένα tests για service:\nΗχεία, δόνηση, οθόνη, αισθητήρες, RAM live, WiFi κ.λπ."
        );
        manualBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ManualTestsActivity.class)));
        root.addView(manualBtn);

        // =========================
        // 🟨 EXPORT REPORT
        // =========================
        root.addView(sectionLabel("SERVICE REPORT"));

        View exportBtn = makeBlockButton(
                "📄 Export Service Report",
                "Τελικό Report για τον πελάτη (PDF ή TXT) + αυτόματο reset"
        );
        exportBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ServiceReportActivity.class)));
        root.addView(exportBtn);

        scroll.addView(root);
        setContentView(scroll);
    }

    private TextView sectionLabel(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setTextSize(16f);
        tv.setTextColor(0xFFEEEEEE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, dp(12), 0, dp(6));
        return tv;
    }

    // ------------------------------------------------------------
    // Κλασικό block button (λευκό κείμενο)
    // ------------------------------------------------------------
    private View makeBlockButton(String title, String subtitle) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dp(6), 0, dp(6));
        card.setLayoutParams(lp);

        card.setBackgroundResource(R.drawable.gel_btn_outline_selector);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(16f);
        t.setTextColor(0xFFFFFFFF);           // White text
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setPadding(0, 0, 0, dp(4));
        card.addView(t);

        TextView s = new TextView(this);
        s.setText(subtitle);
        s.setTextSize(13f);
        s.setTextColor(0xFFAAAAAA);
        s.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(s);

        card.setClickable(true);
        card.setFocusable(true);
        return card;
    }

    // ------------------------------------------------------------
    // ΕΙΔΙΚΟ medical button για GEL Phone Diagnosis
    // ------------------------------------------------------------
    private View makeMedicalBlockButton(String title, String subtitle) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dp(6), 0, dp(6));
        card.setLayoutParams(lp);

        card.setBackgroundResource(R.drawable.gel_btn_outline_selector);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(16f);
        t.setTextColor(0xFF39FF14);           // NEON GREEN text
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setPadding(0, 0, 0, dp(4));
        // medical.jpg στο drawable -> R.drawable.medical
        t.setCompoundDrawablesWithIntrinsicBounds(R.drawable.medical, 0, 0, 0);
        t.setCompoundDrawablePadding(dp(8));
        card.addView(t);

        TextView s = new TextView(this);
        s.setText(subtitle);
        s.setTextSize(13f);
        s.setTextColor(0xFFAAAAAA);
        s.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(s);

        card.setClickable(true);
        card.setFocusable(true);
        return card;
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
