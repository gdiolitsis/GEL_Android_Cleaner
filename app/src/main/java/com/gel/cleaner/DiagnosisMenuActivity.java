package com.gel.cleaner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// ============================================================
// GEL Service Lab — Main Diagnosis Menu  (Updated)
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
        title.setTextColor(0xFFFFD700);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        // SUBTITLE
        TextView sub = new TextView(this);
        sub.setText("Επαγγελματική διάγνωση συσκευής\nAuto + Manual tests + Export report");
        sub.setTextSize(14f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(16));
        root.addView(sub);

        // =========================
        // 🟦 AUTO DIAGNOSIS (UPDATED)
        // =========================
        root.addView(sectionLabel("AUTO DIAGNOSIS"));

        View autoBtn = makeBlockButton("📊 GEL Auto Diagnosis",
                "Πλήρης αυτόματη διάγνωση (Service Lab)\nHardware • RAM • Storage • Battery • Network • Sensors…");

        // 🔥 Updated: NOW opens AutoDiagnosisActivity
        autoBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AutoDiagnosisActivity.class)));

        root.addView(autoBtn);

        // =========================
        // 🟩 MANUAL TESTS
        // =========================
        root.addView(sectionLabel("MANUAL TESTS"));

        View manualBtn = makeBlockButton("🧪 Manual Tests",
                "Στοχευμένα tests για service:\nΗχεία, δόνηση, οθόνη, αισθητήρες, RAM live, WiFi κ.λπ.");
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
        tv.setPadding(0, dp(12), 0, dp(6));
        return tv;
    }

    private View makeBlockButton(String title, String subtitle) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(6), 0, dp(6));
        card.setLayoutParams(lp);

        card.setBackgroundResource(R.drawable.gel_btn_outline_selector);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(16f);
        t.setTextColor(0xFFFFFFFF);
        t.setPadding(0, 0, 0, dp(4));
        card.addView(t);

        TextView s = new TextView(this);
        s.setText(subtitle);
        s.setTextSize(13f);
        s.setTextColor(0xFFCCCCCC);
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
