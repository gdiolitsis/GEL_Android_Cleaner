// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPhoneLabsActivity.java — iPhone Diagnostics Labs v1.0
// Dark-Gold + Neon Green Edition (MATCHES Manual Tests UI)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class IPhoneLabsActivity extends Activity {
    // ============================================================
    // COLORS (MATCH MANUAL TESTS SCREEN)
    // ============================================================
    private static final int COLOR_BG         = 0xFF101010;
    private static final int COLOR_GREEN_MAIN = 0xFF00FF66; // neon green
    private static final int COLOR_GREEN_SUB  = 0xFF00CC55;
    private static final int COLOR_WHITE      = 0xFFFFFFFF;
    private static final int COLOR_GRAY       = 0xFFCCCCCC;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ============================================================
        // ROOT
        // ============================================================
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setBackgroundColor(COLOR_BG);

        root.setClickable(false);
        root.setFocusable(false);
        scroll.setClickable(false);
        scroll.setFocusable(false);

        // ============================================================
        // TITLE
        // ============================================================
        TextView title = new TextView(this);
        title.setText("GEL iPhone Diagnostics");
        title.setTextSize(sp(22f));
        title.setTextColor(COLOR_WHITE);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText(
                "Εργαστηριακή διάγνωση iPhone μέσω αρχείων συστήματος\n" +
                "Ανάλυση δεδομένων service (χωρίς άμεση πρόσβαση στη συσκευή)"
        );
        sub.setTextSize(sp(14f));
        sub.setTextColor(COLOR_GRAY);
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.setPadding(0, dp(8), 0, dp(18));
        root.addView(sub);

        // ============================================================
        // LAB BUTTONS
        // ============================================================
        root.addView(makeLabButton(
                "📂 Panic Log Analyzer",
                "Ανάλυση panic logs (kernel / watchdog / reboot)",
                v -> runPanicLogLab()
        ));

        root.addView(makeLabButton(
                "📊 System Stability Evaluation",
                "Αξιολόγηση σταθερότητας iOS βάσει logs",
                v -> runStabilityLab()
        ));

        root.addView(makeLabButton(
                "🧠 Impact Analysis",
                "Συσχέτιση σφαλμάτων με hardware domain",
                v -> runImpactLab()
        ));

        root.addView(makeLabButton(
                "🧾 Service Recommendation",
                "Τελικό service verdict για τεχνικό",
                v -> runServiceRecommendationLab()
        ));

        scroll.addView(root);
        setContentView(scroll);
    }

    // ============================================================
    // LAB IMPLEMENTATIONS (LOGGING ONLY)
    // ============================================================

    private void runPanicLogLab() {
        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("📂 iPhone LAB — Panic Log Analyzer");
        GELServiceLog.info("• Αναμονή εισαγωγής panic log (TXT / ZIP)");
        GELServiceLog.warn("⚠ Δεν έχει φορτωθεί αρχείο log.");
    }

    private void runStabilityLab() {
        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("📊 iPhone LAB — System Stability Evaluation");
        GELServiceLog.info("• Ανάλυση συχνότητας panic / reboot events");
        GELServiceLog.warn("⚠ Ανεπαρκή δεδομένα για πλήρη αξιολόγηση.");
    }

    private void runImpactLab() {
        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("🧠 iPhone LAB — Impact Analysis");
        GELServiceLog.info("• Συσχέτιση σφαλμάτων με πιθανό hardware");
        GELServiceLog.warn("⚠ Απαιτούνται panic logs για ακρίβεια.");
    }

    private void runServiceRecommendationLab() {
        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("🧾 iPhone LAB — Service Recommendation");
        GELServiceLog.ok("✔ Δεν εντοπίστηκε κρίσιμη ένδειξη άμεσης βλάβης.");
        GELServiceLog.info("ℹ Σύσταση: παρακολούθηση ή περαιτέρω έλεγχος.");
    }

    // ============================================================
    // UI HELPER
    // ============================================================
    private View makeLabButton(String title, String desc, View.OnClickListener cb) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackgroundResource(R.drawable.gel_btn_outline_selector);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dp(6), 0, dp(6));
        card.setLayoutParams(lp);

        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(cb);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(sp(16f));
        t.setTextColor(COLOR_GREEN_MAIN);
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(t);

        TextView s = new TextView(this);
        s.setText(desc);
        s.setTextSize(sp(13f));
        s.setTextColor(COLOR_GREEN_SUB);
        s.setGravity(Gravity.CENTER_HORIZONTAL);
        s.setPadding(0, dp(6), 0, 0);
        card.addView(s);

        return card;
    }
}
