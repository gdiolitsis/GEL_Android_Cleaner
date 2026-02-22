// GDiolitsis Engine Lab (GEL) — Author & Developer
// GuidedOptimizerActivity.java — FINAL (Guided Optimizer • System Settings Routing • Bilingual • Reminder Scheduler)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public final class GuidedOptimizerActivity extends AppCompatActivity {

    // ============================================================
    // PREFS (simple, centralized)
    // ============================================================
    private static final String PREFS = "gel_optimizer_prefs";
    private static final String K_OPT_OUT = "opt_out_guided_optimizer";
    private static final String K_LAST_RUN_ELAPSED = "last_run_elapsed";

    private static final String K_REMINDER_ENABLED = "reminder_enabled";
    private static final String K_REMINDER_INTERVAL = "reminder_interval"; // 1=day,7=week,30=month

    // UI
    private LinearLayout root;
    private TextView title;
    private TextView body;
    private LinearLayout actions;
    private Button btnPrimary;
    private Button btnSecondary;
    private Button btnSkip;

    // checklist
    private LinearLayout checklistBox;
    private CheckBox cbHeat, cbBattery, cbSlowCharge, cbLag, cbCrashes, cbStorage, cbData, cbBackground;

    private boolean gr;
    private int step = 0;

    // Steps
    private static final int STEP_INTRO = 0;
    private static final int STEP_STORAGE = 1;
    private static final int STEP_CACHE = 2;
    private static final int STEP_BATTERY = 3;
    private static final int STEP_DATA = 4;
    private static final int STEP_APPS = 5;
    private static final int STEP_SYMPTOMS = 6;
    private static final int STEP_LABS = 7;
    private static final int STEP_REMINDER = 8;
    private static final int STEP_DONE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gr = AppLang.isGreek(this);

        // Respect opt-out
        if (getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(K_OPT_OUT, false)) {
            finish();
            return;
        }

        buildUi();
        setContentView(root);

        // mark run time (best-effort)
        try {
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit()
                    .putLong(K_LAST_RUN_ELAPSED, SystemClock.elapsedRealtime())
                    .apply();
        } catch (Throwable ignore) {}

        go(STEP_INTRO);
    }

    // ============================================================
    // UI BUILD (No XML)
    // ============================================================
    private void buildUi() {

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF0B0B0B);
        bg.setCornerRadius(dp(22));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        title = new TextView(this);
        title.setTextColor(Color.WHITE);
        title.setTextSize(19f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(dp(10), dp(10), dp(10), dp(12));

        body = new TextView(this);
        body.setTextColor(0xFFDDDDDD);
        body.setTextSize(15.5f);
        body.setLineSpacing(dp(3), 1.0f);
        body.setPadding(dp(10), dp(8), dp(10), dp(10));

        checklistBox = new LinearLayout(this);
        checklistBox.setOrientation(LinearLayout.VERTICAL);
        checklistBox.setPadding(dp(12), dp(10), dp(12), dp(10));
        GradientDrawable cbg = new GradientDrawable();
        cbg.setColor(0xFF101010);
        cbg.setCornerRadius(dp(16));
        cbg.setStroke(dp(2), 0xFFB8860B);
        checklistBox.setBackground(cbg);
        checklistBox.setVisibility(View.GONE);

        TextView cTitle = new TextView(this);
        cTitle.setTextColor(0xFFFFD700);
        cTitle.setTextSize(16f);
        cTitle.setTypeface(null, Typeface.BOLD);
        cTitle.setPadding(0, 0, 0, dp(8));
        cTitle.setText(gr
                ? "Έχεις παρατηρήσει κάτι από τα παρακάτω;"
                : "Have you noticed any of the following?");

        cbHeat = mkCheck(gr ? "Αύξηση θερμοκρασίας / ζέστη" : "Higher temperature / heat");
        cbBattery = mkCheck(gr ? "Γρήγορη πτώση μπαταρίας" : "Fast battery drain");
        cbSlowCharge = mkCheck(gr ? "Αργή φόρτιση" : "Slow charging");
        cbLag = mkCheck(gr ? "Κολλήματα / αργή απόκριση" : "Lag / slow response");
        cbCrashes = mkCheck(gr ? "Κρασαρίσματα / ANR" : "Crashes / ANR");
        cbStorage = mkCheck(gr ? "Έλλειψη χώρου" : "Low storage");
        cbData = mkCheck(gr ? "Αυξημένη κατανάλωση δεδομένων" : "High data usage");
        cbBackground = mkCheck(gr ? "Πολλά apps στο παρασκήνιο" : "Too many apps in background");

        checklistBox.addView(cTitle);
        checklistBox.addView(cbHeat);
        checklistBox.addView(cbBattery);
        checklistBox.addView(cbSlowCharge);
        checklistBox.addView(cbLag);
        checklistBox.addView(cbCrashes);
        checklistBox.addView(cbStorage);
        checklistBox.addView(cbData);
        checklistBox.addView(cbBackground);

        actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        actions.setPadding(dp(6), dp(14), dp(6), dp(4));

        btnPrimary = mkBtn(true);
        btnSecondary = mkBtn(false);
        btnSkip = mkBtn(false);

        btnSkip.setText(gr ? "ΣΚΙΠ" : "SKIP");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(46), 1f);
        lp.setMargins(dp(6), 0, dp(6), 0);

        actions.addView(btnSecondary, lp);
        actions.addView(btnPrimary, lp);
        actions.addView(btnSkip, lp);

        root.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(body, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cLp.setMargins(dp(6), dp(6), dp(6), dp(6));
        root.addView(checklistBox, cLp);

        root.addView(actions, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private CheckBox mkCheck(String text) {
        CheckBox cb = new CheckBox(this);
        cb.setText(text);
        cb.setTextColor(Color.WHITE);
        cb.setPadding(dp(4), dp(4), dp(4), dp(4));
        return cb;
    }

    private Button mkBtn(boolean primary) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15.5f);
        b.setTypeface(null, Typeface.BOLD);

        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(16));

        if (primary) {
            d.setColor(0xFF1A2A1A);
            d.setStroke(dp(3), 0xFF00FF7F); // green border
        } else {
            d.setColor(0xFF111111);
            d.setStroke(dp(3), 0xFFFFD700); // gold border
        }
        b.setBackground(d);
        return b;
    }

    private int dp(int v) {
        float s = getResources().getDisplayMetrics().density;
        return Math.round(v * s);
    }

    // ============================================================
    // FLOW
    // ============================================================
    private void go(int next) {
        step = next;

        checklistBox.setVisibility(step == STEP_SYMPTOMS ? View.VISIBLE : View.GONE);

        btnSkip.setOnClickListener(v -> {
            if (step == STEP_REMINDER) {
                // skip reminder but finish
                go(STEP_DONE);
            } else {
                go(STEP_DONE);
            }
        });

        switch (step) {

            case STEP_INTRO: {
                title.setText(gr ? "Βελτιστοποίηση (Καθοδήγηση)" : "Optimization (Guided)");
                body.setText(gr
                        ? "Θα σε πάω στις σωστές ρυθμίσεις της συσκευής.\n"
                        + "Εσύ κάνεις τις επιλογές — εγώ κρατάω το τιμόνι (χωρίς να πατάω γκάζι μόνος μου 😄).\n\n"
                        + "Πάτα «Έναρξη»."
                        : "I will guide you to the right system settings.\n"
                        + "You make the choices — I just navigate (no autopilot 😄).\n\n"
                        + "Tap “Start”."
                );

                btnPrimary.setText(gr ? "Έναρξη" : "Start");
                btnSecondary.setText(gr ? "Όχι τώρα" : "Not now");
                btnSecondary.setOnClickListener(v -> go(STEP_DONE));
                btnPrimary.setOnClickListener(v -> go(STEP_STORAGE));
                return;
            }

            case STEP_STORAGE: {
                title.setText(gr ? "STEP 1 — Αποθήκευση" : "STEP 1 — Storage");
                body.setText(gr
                        ? "Θα μεταφερθείς στις ρυθμίσεις αποθήκευσης.\n"
                        + "Καθάρισε προσωρινά/περιττά αρχεία αν χρειάζεται.\n\n"
                        + "Όταν τελειώσεις, γύρνα πίσω και πάτα «ΟΚ»."
                        : "You will be taken to Storage settings.\n"
                        + "Clean temporary/unneeded files if necessary.\n\n"
                        + "When done, come back and tap “OK”."
                );

                btnSecondary.setText(gr ? "Άνοιγμα" : "Open");
                btnPrimary.setText(gr ? "ΟΚ" : "OK");

                btnSecondary.setOnClickListener(v -> OptimizerIntents.openStorageSettings(this));
                btnPrimary.setOnClickListener(v -> go(STEP_CACHE));
                return;
            }

            case STEP_CACHE: {
                title.setText(gr ? "STEP 2 — Cache Optimization" : "STEP 2 — Cache Optimization");
                body.setText(gr
                        ? "Με βάση τη χρήση, η cache μπορεί να βαραίνει εφαρμογές (π.χ. social).\n\n"
                        + "Επιλογές:\n"
                        + "• (A) Πήγαινε στη λίστα εφαρμογών μας και κάνε ταξινόμηση «Μεγαλύτερη Cache».\n"
                        + "• (B) Άνοιξε τα App settings για να καθαρίσεις cache σε όποια θες.\n\n"
                        + "Γύρνα πίσω και πάτα «ΟΚ»."
                        : "Based on usage, cache can grow (e.g. social apps).\n\n"
                        + "Options:\n"
                        + "• (A) Open our app list and sort by “Largest cache”.\n"
                        + "• (B) Open system App settings to clear cache per app.\n\n"
                        + "Come back and tap “OK”."
                );

                btnSecondary.setText(gr ? "A: Λίστα Apps" : "A: App List");
                btnPrimary.setText(gr ? "B: Ρυθμίσεις Apps" : "B: App Settings");

                btnSecondary.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(this, AppListActivity.class));
                    } catch (Throwable t) {
                        OptimizerIntents.openApplicationSettings(this);
                    }
                });

                btnPrimary.setOnClickListener(v -> OptimizerIntents.openApplicationSettings(this));

                btnSkip.setText(gr ? "ΟΚ" : "OK");
                btnSkip.setOnClickListener(v -> go(STEP_BATTERY));
                return;
            }

            case STEP_BATTERY: {
                title.setText(gr ? "STEP 3 — Μπαταρία" : "STEP 3 — Battery");
                body.setText(gr
                        ? "Θα ανοίξω τις ρυθμίσεις μπαταρίας.\n"
                        + "Δες αν υπάρχουν apps με υπερβολική κατανάλωση ή περιορισμοί που χρειάζονται.\n\n"
                        + "Γύρνα πίσω και πάτα «ΟΚ»."
                        : "I will open Battery settings.\n"
                        + "Check for apps with excessive battery usage or useful restrictions.\n\n"
                        + "Come back and tap “OK”."
                );

                btnSecondary.setText(gr ? "Άνοιγμα" : "Open");
                btnPrimary.setText(gr ? "ΟΚ" : "OK");

                btnSecondary.setOnClickListener(v -> OptimizerIntents.openBatterySettings(this));
                btnPrimary.setOnClickListener(v -> go(STEP_DATA));
                return;
            }

            case STEP_DATA: {
                title.setText(gr ? "STEP 4 — Δεδομένα" : "STEP 4 — Data Usage");
                body.setText(gr
                        ? "Θα ανοίξω τη χρήση δεδομένων.\n"
                        + "Αν βλέπεις apps με υπερβολική χρήση, μπορείς να βάλεις περιορισμούς.\n\n"
                        + "Γύρνα πίσω και πάτα «ΟΚ»."
                        : "I will open Data usage.\n"
                        + "If you see apps with high usage, you can apply restrictions.\n\n"
                        + "Come back and tap “OK”."
                );

                btnSecondary.setText(gr ? "Άνοιγμα" : "Open");
                btnPrimary.setText(gr ? "ΟΚ" : "OK");

                btnSecondary.setOnClickListener(v -> OptimizerIntents.openDataUsageSettings(this));
                btnPrimary.setOnClickListener(v -> go(STEP_APPS));
                return;
            }

            case STEP_APPS: {
                title.setText(gr ? "STEP 5 — Διαχείριση Εφαρμογών" : "STEP 5 — App Management");
                body.setText(gr
                        ? "Θα ανοίξω τις ρυθμίσεις εφαρμογών.\n"
                        + "Αν θες, έλεγξε:\n"
                        + "• Apps που τρέχουν στο παρασκήνιο\n"
                        + "• Άδειες (privacy)\n"
                        + "• Ειδοποιήσεις (αν ξυπνάνε τη συσκευή)\n\n"
                        + "Γύρνα πίσω και πάτα «Συνέχεια»."
                        : "I will open App settings.\n"
                        + "Optionally review:\n"
                        + "• Background behavior\n"
                        + "• Permissions (privacy)\n"
                        + "• Notifications (device wake-ups)\n\n"
                        + "Come back and tap “Continue”."
                );

                btnSecondary.setText(gr ? "Άνοιγμα" : "Open");
                btnPrimary.setText(gr ? "Συνέχεια" : "Continue");

                btnSecondary.setOnClickListener(v -> OptimizerIntents.openApplicationSettings(this));
                btnPrimary.setOnClickListener(v -> go(STEP_SYMPTOMS));
                return;
            }

            case STEP_SYMPTOMS: {
                title.setText(gr ? "STEP 6 — Γρήγορη Ερώτηση" : "STEP 6 — Quick Question");
                body.setText(gr
                        ? "Διάλεξε ό,τι σε απασχολεί.\n"
                        + "Με βάση τα τσεκ θα σου προτείνω τα κατάλληλα εργαστήρια."
                        : "Select what concerns you.\n"
                        + "Based on your checks, I will recommend the right labs."
                );

                btnSecondary.setText(gr ? "Καμία επιλογή" : "None");
                btnPrimary.setText(gr ? "Συνέχεια" : "Continue");

                btnSecondary.setOnClickListener(v -> {
                    clearChecks();
                    go(STEP_LABS);
                });
                btnPrimary.setOnClickListener(v -> go(STEP_LABS));
                return;
            }

            case STEP_LABS: {
                title.setText(gr ? "STEP 7 — Προτεινόμενα Εργαστήρια" : "STEP 7 — Recommended Labs");
                body.setText(buildLabsRecommendationText());

                btnSecondary.setText(gr ? "Άνοιγμα Manual Tests" : "Open Manual Tests");
                btnPrimary.setText(gr ? "ΟΚ" : "OK");

                btnSecondary.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(this, ManualTestsActivity.class));
                    } catch (Throwable ignore) {}
                });

                btnPrimary.setOnClickListener(v -> go(STEP_REMINDER));
                return;
            }

            case STEP_REMINDER: {
                title.setText(gr ? "STEP 8 — Υπενθύμιση" : "STEP 8 — Reminder");
                body.setText(gr
                        ? "Θες να σου υπενθυμίζω να κάνεις έλεγχο υγείας;\n"
                        + "Η υπενθύμιση είναι απλή ειδοποίηση (δεν τρέχει τίποτα στο παρασκήνιο).\n\n"
                        + "Διάλεξε συχνότητα:"
                        : "Do you want a reminder to run a health check?\n"
                        + "This is a simple notification (nothing runs in background).\n\n"
                        + "Choose frequency:"
                );

                btnSecondary.setText(gr ? "1 Ημέρα" : "Daily");
                btnPrimary.setText(gr ? "1 Εβδομάδα" : "Weekly");
                btnSkip.setText(gr ? "1 Μήνας" : "Monthly");

                btnSecondary.setOnClickListener(v -> {
                    OptimizerScheduler.enableReminder(this, 1);
                    go(STEP_DONE);
                });
                btnPrimary.setOnClickListener(v -> {
                    OptimizerScheduler.enableReminder(this, 7);
                    go(STEP_DONE);
                });
                btnSkip.setOnClickListener(v -> {
                    OptimizerScheduler.enableReminder(this, 30);
                    go(STEP_DONE);
                });

                // Extra opt-out row
                addDontShowAgainRowIfMissing();
                return;
            }

            case STEP_DONE:
            default: {
                title.setText(gr ? "Τέλος" : "Done");
                body.setText(gr
                        ? "Συγχαρητήρια.\n"
                        + "Ελπίζω να σε βοήθησα ώστε η συσκευή σου να λειτουργεί ταχύτερα και ασφαλέστερα.\n\n"
                        + "Αν δεις βελτίωση, μην ξεχάσεις να ενεργοποιήσεις τη συχνή παρακολούθηση ανά:\n"
                        + "1 ημέρα / 1 εβδομάδα / 1 μήνα."
                        : "Congrats.\n"
                        + "I hope this helped your device run faster and safer.\n\n"
                        + "If you notice improvement, consider enabling regular check reminders:\n"
                        + "Daily / Weekly / Monthly."
                );

                btnSecondary.setText(gr ? "Κλείσιμο" : "Close");
                btnPrimary.setText(gr ? "Manual Tests" : "Manual Tests");
                btnSkip.setText(gr ? "ΣΚΙΠ" : "SKIP");

                btnSecondary.setOnClickListener(v -> finish());
                btnPrimary.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(this, ManualTestsActivity.class));
                    } catch (Throwable ignore) {}
                    finish();
                });
                btnSkip.setOnClickListener(v -> finish());
                return;
            }
        }
    }

    private void clearChecks() {
        try {
            cbHeat.setChecked(false);
            cbBattery.setChecked(false);
            cbSlowCharge.setChecked(false);
            cbLag.setChecked(false);
            cbCrashes.setChecked(false);
            cbStorage.setChecked(false);
            cbData.setChecked(false);
            cbBackground.setChecked(false);
        } catch (Throwable ignore) {}
    }

    private String buildLabsRecommendationText() {

        ArrayList<String> list = new ArrayList<>();

        boolean heat = safeChecked(cbHeat);
        boolean batt = safeChecked(cbBattery);
        boolean slow = safeChecked(cbSlowCharge);
        boolean lag = safeChecked(cbLag);
        boolean crash = safeChecked(cbCrashes);
        boolean stor = safeChecked(cbStorage);
        boolean data = safeChecked(cbData);
        boolean bg = safeChecked(cbBackground);

        // Battery/Thermals bucket (your 14–17 set)
        if (heat || batt || slow) {
            list.add(gr
                    ? "• Labs 14–17 — Μπαταρία / Θερμικά / Φόρτιση (πλήρης έλεγχος)"
                    : "• Labs 14–17 — Battery / Thermals / Charging (full check)");
        }

        // Performance bucket
        if (lag || bg) {
            list.add(gr
                    ? "• Lab 29 — Αυτόματη σύνοψη υγείας/απόδοσης (scores + ενδείξεις)"
                    : "• Lab 29 — Auto health/performance summary (scores + signals)");
            list.add(gr
                    ? "• Lab 26 — Ανάλυση επιπτώσεων εφαρμογών (background/permissions footprint)"
                    : "• Lab 26 — Installed apps impact analysis (background/permissions footprint)");
        }

        // Storage bucket
        if (stor) {
            list.add(gr
                    ? "• Cleaner / AppList — Έλεγχος cache & σκουπιδιών (ταξινόμηση μεγαλύτερης cache)"
                    : "• Cleaner / AppList — Cache & junk review (sort by largest cache)");
        }

        // Data bucket
        if (data) {
            list.add(gr
                    ? "• Lab 26 — Έλεγχος footprint + έλεγχος ρυθμίσεων δεδομένων"
                    : "• Lab 26 — Footprint check + review data settings");
        }

        // Crash bucket
        if (crash) {
            list.add(gr
                    ? "• Lab 25 — Crash Intelligence (signals από logs όπου επιτρέπεται)"
                    : "• Lab 25 — Crash Intelligence (log signals where available)");
            list.add(gr
                    ? "• Lab 30 — Τελική τεχνική αναφορά (read-only summary)"
                    : "• Lab 30 — Final technician summary (read-only)");
        }

        // None selected
        if (list.isEmpty()) {
            list.add(gr
                    ? "• Προαιρετικά: Lab 29 — Αυτόματη σύνοψη (για γενική εικόνα)"
                    : "• Optional: Lab 29 — Auto summary (for a general view)");
            list.add(gr
                    ? "• Προαιρετικά: Lab 26 — Εφαρμογές (για footprint/background εικόνα)"
                    : "• Optional: Lab 26 — Apps (for footprint/background view)");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(gr
                ? "Με βάση τις επιλογές σου, προτείνονται:\n\n"
                : "Based on your selections, recommended:\n\n");

        for (String s : list) sb.append(s).append('\n');

        sb.append('\n');
        sb.append(gr
                ? "Σημείωση: Εσύ διαλέγεις τι θα τρέξεις — εμείς σου δείχνουμε τον σωστό δρόμο."
                : "Note: You choose what to run — we just point you to the right path.");

        return sb.toString();
    }

    private boolean safeChecked(CheckBox cb) {
        try { return cb != null && cb.isChecked(); } catch (Throwable t) { return false; }
    }

    private void addDontShowAgainRowIfMissing() {
        try {
            // already added?
            View tag = root.findViewWithTag("dont_show_row");
            if (tag != null) return;

            LinearLayout row = new LinearLayout(this);
            row.setTag("dont_show_row");
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(10), dp(10), dp(10), dp(6));

            CheckBox cb = new CheckBox(this);
            cb.setText(gr ? "Να μην εμφανιστεί ξανά" : "Don't show again");
            cb.setTextColor(0xFFDDDDDD);

            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    getSharedPreferences(PREFS, MODE_PRIVATE)
                            .edit()
                            .putBoolean(K_OPT_OUT, isChecked)
                            .apply();
                } catch (Throwable ignore) {}
            });

            row.addView(cb, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // insert above actions
            int idx = root.indexOfChild(actions);
            if (idx < 0) idx = root.getChildCount();
            root.addView(row, idx);
        } catch (Throwable ignore) {}
    }
}
