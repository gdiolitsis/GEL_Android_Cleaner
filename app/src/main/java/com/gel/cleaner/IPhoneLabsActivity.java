// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPhoneLabsActivity.java — iPhone Diagnostics Labs v1.0 FINAL (LOCKED)
// Dark-Gold + Neon Green Edition — Service Grade

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import com.gel.cleaner.base.UIHelpers;
import com.gel.cleaner.iphone.IPSPanicParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IPhoneLabsActivity extends AppCompatActivity {
	
	private boolean panicGuidePopupOpen = false;
	
	// ==========================
    // TTS ENGINE
    // ==========================
    private TextToSpeech[] tts   = new TextToSpeech[1];
    private boolean[]     ttsReady = new boolean[1];

    private final StringBuilder logHtmlBuffer = new StringBuilder();

private boolean looksCorruptedPanic(String text) {

    if (text == null) return true;

    String t = text.toLowerCase(Locale.US);

    if (t.length() < 120) return true;

    boolean hasCoreSignal =
            t.contains("panic") ||
            t.contains("bug_type") ||
            t.contains("incident") ||
            t.contains("kernel") ||
            t.contains("exception") ||
            t.contains("termination") ||
            t.contains("reason") ||
            t.contains("panicstring");

    return !hasCoreSignal;
}

    // ============================================================
    // REQUEST CODES
    // ============================================================
    private static final int REQ_PANIC_LOG = 1011;

    // ============================================================
    // SAFETY LIMITS (avoid OOM)
    // ============================================================
    private static final int MAX_TEXT_BYTES = 3 * 1024 * 1024; // 3MB read cap
    private static final int ZIP_SCAN_CAP   = 12;              // max entries to scan

    // ============================================================
    // COLORS (MATCH MANUAL TESTS FEEL)
    // ============================================================
    private static final int COLOR_BG         = 0xFF101010;
    private static final int COLOR_WHITE      = 0xFFFFFFFF;
    private static final int COLOR_GRAY       = 0xFFCCCCCC;
    private static final int COLOR_NEON       = 0xFF00FF9C;

    // HTML colors (log lines)
    private static final String H_WHITE = "#FFFFFF";
    private static final String H_NEON  = "#00FF9C";
    private static final String H_OK    = "#88FF88";
    private static final String H_WARN  = "#FFD966";
    private static final String H_ERR   = "#FF5555";
    private static final String H_DIM   = "#B8B8B8";

    // ============================================================
    // STATE (CANONICAL)
    // ============================================================
    private boolean panicLogLoaded = false;
    private String  panicLogName   = null;
    private String  panicLogText   = null;

    // ============================================================
    // UI (LIKE MANUAL TESTS — LOG AREA BOTTOM)
    // ============================================================
    private TextView txtLog;
    
    private boolean appendMode = false;
    private boolean panicGuideMuted = false;
    private String  panicGuideLang  = "EN";
    private int panicLogCount = 0;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ROOT SCROLL
        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        scroll.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        scroll.setFillViewport(false);

        // CONTENT ROOT
        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.setBackgroundColor(COLOR_BG);

        boolean gr = AppLang.isGreek(this);

// TITLE
TextView title = new TextView(this);
title.setLayoutParams(new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
));

title.setText(gr
        ? "GEL Διαγνωστικά iPhone"
        : "GEL iPhone Diagnostics"
);

title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
title.setTextColor(COLOR_WHITE);
title.setGravity(Gravity.CENTER_HORIZONTAL);
title.setIncludeFontPadding(false);
root.addView(title);

// SUBTITLE
TextView sub = new TextView(this);
sub.setLayoutParams(new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
));

sub.setText(gr
        ? "Εργαστηριακή διάγνωση iPhone μέσω αρχείων συστήματος\n"
          + "Ανάλυση logs επιπέδου service (χωρίς άμεση πρόσβαση στη συσκευή)"
        : "Laboratory diagnostics for iPhone using system files\n"
          + "Service-grade log analysis (no direct device access)"
);

sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
sub.setTextColor(0xFF39FF14);
sub.setGravity(Gravity.CENTER_HORIZONTAL);
sub.setPadding(0, dp(8), 0, dp(18));
sub.setIncludeFontPadding(false);
root.addView(sub);

// ============================================================
// LAB BUTTONS (GUARDED)
// ============================================================

// 1) Import (replace mode)
View importBtn = makeLabButton(
        gr ? "Εισαγωγή Panic Logs (TXT / ZIP)"
           : "Panic Log Import (TXT / ZIP)",
        gr ? "Αυτόματη αποσυμπίεση + φόρτωση αναφοράς"
           : "Auto unzip + load panic report",
        false,
        v -> {
            appendMode = false;
            openPanicLogPicker();
        }
);

setButtonTextWhite(importBtn);
root.addView(importBtn);


// 1b) Add more logs (append mode)
View appendBtn = makeLabButton(
        gr ? "Προσθήκη επιπλέον panic logs"
           : "Add more panic logs",
        gr ? "Προσθήκη logs στην τρέχουσα ανάλυση"
           : "Append logs to current analysis",
        false,
        v -> {
            appendMode = true;
            openPanicLogPicker();
        }
);

setButtonTextWhite(appendBtn);
root.addView(appendBtn);


// 2) Analyzer (guard)
root.addView(makeLabButton(
        gr ? "LAB 1 - Ανάλυση Panic Logs"
           : "LAB 1 - Panic Log Analyzer",
        gr ? "Μοτίβο • Domain • Αιτία • Σοβαρότητα • Σύσταση"
           : "Pattern match • Domain • Cause • Severity • Recommendation",
        true,
        v -> runPanicLogAnalyzer()
));

// 3) Signature Parser (guard)
root.addView(makeLabButton(
        gr ? "LAB 2 - Ανάλυση Υπογραφής Panic"
           : "LAB 2 - Panic Signature Parser",
        gr ? "Τύπος Crash • Domain • Βεβαιότητα • Τεκμηρίωση"
           : "Crash Type • Domain • Confidence • Evidence",
        true,
        v -> runPanicSignatureParser()
));

// 4) Stability (guard)
root.addView(makeLabButton(
        gr ? "LAB 3 - Αξιολόγηση Σταθερότητας Συστήματος"
           : "LAB 3 - System Stability Evaluation",
        gr ? "Αξιολόγηση σταθερότητας iOS από διαθέσιμα logs"
           : "Evaluate iOS stability from available logs",
        true,
        v -> runStabilityLab()
));

// 5) Impact (guard)
root.addView(makeLabButton(
        gr ? "LAB 4 - Ανάλυση Επιπτώσεων"
           : "LAB 4 - Impact Analysis",
        gr ? "Συσχέτιση crash με πιθανό hardware domain"
           : "Correlate crash with probable hardware domain",
        true,
        v -> runImpactLab()
));

// 6) Frequency
root.addView(makeLabButton(
        gr ? "LAB 5 - Ανάλυση Συχνότητας Panic"
           : "LAB 5 - Panic Frequency Analyzer",
        gr ? "Συχνότητα επαναλαμβανόμενων crash types"
           : "Repeated crash type frequency",
        true,
        v -> runPanicFrequencyLab()
));

// 7) Clustering
root.addView(makeLabButton(
        gr ? "LAB 6 - Ομαδοποίηση Domain Panic"
           : "LAB 6 - Panic Domain Clustering",
        gr ? "Εντοπισμός επαναλαμβανόμενου hardware domain"
           : "Detect recurring hardware domain",
        true,
        v -> runPanicClusteringLab()
));

// 8) Recurring Domain
root.addView(makeLabButton(
        gr ? "LAB 7 - Επαναλαμβανόμενο Domain"
           : "LAB 7 - Recurring Domain Detection",
        gr ? "Ανίχνευση κυρίαρχου hardware pattern"
           : "Detect dominant hardware crash pattern",
        true,
        v -> runRecurringDomainLab()
));

// 9) Stability Index
root.addView(makeLabButton(
        gr ? "LAB 8 - Δείκτης Σταθερότητας"
           : "LAB 8 - Stability Index",
        gr ? "Συνολική αξιολόγηση βάσει επαναλαμβανόμενων μοτίβων"
           : "Overall evaluation based on recurring crash patterns",
        true,
        v -> runStabilityIndexLab()
));

// 10) FINAL Service Recommendation
root.addView(makeLabButton(
        gr ? "LAB 9 - Τελική Σύσταση Service"
           : "LAB 9 - Final Service Recommendation",
        gr ? "Ολοκληρωμένη τεχνική αξιολόγηση βάσει όλων των εργαστηρίων"
           : "Integrated technical verdict based on all analysis",
        true,
        v -> runFinalServiceRecommendationLab()
));

        // ============================================================
        // LOG AREA (BOTTOM) — LIKE MANUAL TESTS
        // ============================================================

        TextView logTitle = new TextView(this);
        logTitle.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        logTitle.setText("iPhone Labs Log");
        logTitle.setTextColor(COLOR_WHITE);
        logTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        logTitle.setPadding(0, dp(18), 0, dp(8));
        logTitle.setIncludeFontPadding(false);
        root.addView(logTitle);

        txtLog = new TextView(this);
        txtLog.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        txtLog.setTextColor(COLOR_WHITE);
        txtLog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        txtLog.setLineSpacing(0f, 1.12f);
        txtLog.setMovementMethod(new ScrollingMovementMethod());
        txtLog.setPadding(dp(12), dp(12), dp(12), dp(12));
        txtLog.setIncludeFontPadding(false);
        root.addView(txtLog);

 // ============================================================
// EXPORT SERVICE REPORT BUTTON (iPhone Labs)
// ============================================================
Button btnExport = new Button(this);
btnExport.setText(getString(R.string.export_report_title));
btnExport.setAllCaps(false);
btnExport.setBackgroundResource(R.drawable.gel_btn_outline_selector);
btnExport.setTextColor(0xFFFFFFFF);

LinearLayout.LayoutParams lpExp =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpExp.setMargins(dp(4), dp(12), dp(4), dp(20));
btnExport.setLayoutParams(lpExp);

btnExport.setOnClickListener(v -> {
    Intent i = new Intent(this, ServiceReportActivity.class);
    startActivity(i);
});

root.addView(btnExport);

showPanicLogsGuidePopup();

// ============================================================
// FINAL BIND
// ============================================================
scroll.addView(root);
setContentView(scroll);

// ==========================
// TTS INIT
// ==========================
tts[0] = new TextToSpeech(this, status -> {
    if (status == TextToSpeech.SUCCESS) {
        ttsReady[0] = true;

        if (panicGuidePopupOpen && !panicGuideMuted) {
            speakPanicGuideTTS();
        }

    } else {
        ttsReady[0] = false;
    }
});

// ============================================================
// SERVICE LOG — SECTION HEADER (iPhone Labs)
// ============================================================
GELServiceLog.section("iPhone Labs — Panic Log & Stability Analysis");

// Boot / intro entries (ONCE)
logLine();
logInfo(gr 
        ? "GEL iPhone Labs — έτοιμο."
        : "GEL iPhone Labs — ready.");
logLine();

logOk(gr
        ? "Εισήγαγε panic log για να ξεκινήσει η ανάλυση."
        : "Import a panic log to begin analysis.");

} // onCreate ends here

private void resetSignatureCache() {
    sigCrashType  = "Unknown";
    sigDomain     = "Unknown";
    sigConfidence = "Low";
}

@Override
protected void onPause() {
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();   // 🔇 stop όταν φεύγουμε από την οθόνη
        }
    } catch (Throwable ignore) {}
    super.onPause();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
            tts[0].shutdown();
        }
    } catch (Throwable ignore) {}
}

// ============================================================
// TOAST (VISIBLE GUARD MESSAGE)
// ============================================================
private void toast(String msg) {
    try { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    catch (Throwable ignore) {}
}

// ============================================================
// PanicLog POPUP (STYLE + MUTE + LANG + TTS)
// ============================================================

private void showPanicLogsGuidePopup() {

    runOnUiThread(() -> {

        AlertDialog.Builder b =
                new AlertDialog.Builder(
        IPhoneLabsActivity.this,
        android.R.style.Theme_Material_Dialog_NoActionBar
);
        b.setCancelable(true);

        // ================= ROOT =================
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        box.setBackground(bg);

        // ================= TITLE =================
        TextView title = new TextView(this);
        title.setText("PANIC LOGS — Import Guide");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        box.addView(title);

        // ================= MESSAGE =================
        TextView msg = new TextView(this);
        msg.setTextColor(COLOR_NEON);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.START);
        msg.setText(getPanicGuideTextEN());
        box.addView(msg);

        // ============================================================
        // CONTROLS — MUTE + LANG
        // ============================================================
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(16), 0, dp(10));

            try {
                if (panicGuideMuted && tts != null && tts[0] != null) {
                    tts[0].stop();
                }
            } catch (Throwable ignore) {}

        // 🌐 LANGUAGE SPINNER
        Spinner langSpinner = new Spinner(this);
        ArrayAdapter<String> langAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"EN", "GR"}
                );
        langAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(langAdapter);
        
        // ================= INITIAL LANGUAGE + TTS =================

// διάλεξε αρχική γλώσσα από ρύθμιση / state
if ("GR".equals(panicGuideLang)) {
    langSpinner.setSelection(1);
    msg.setText(getPanicGuideTextGR());
} else {
    langSpinner.setSelection(0);
    msg.setText(getPanicGuideTextEN());
}

// μίλα όταν το TTS είναι έτοιμο (delayed trigger)
box.postDelayed(() -> {
    if (panicGuidePopupOpen) {
        speakPanicGuideTTS();
    }
}, 700);

        langSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> p,
                            View v,
                            int pos,
                            long id) {

                        panicGuideLang = (pos == 0) ? "EN" : "GR";

                        if ("GR".equals(panicGuideLang)) {
                            msg.setText(getPanicGuideTextGR());
                        } else {
                            msg.setText(getPanicGuideTextEN());
                        }
                        
                  if (!panicGuidePopupOpen) return;
                        speakPanicGuideTTS();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> p) {}
                });

        LinearLayout langBox = new LinearLayout(this);
        langBox.setOrientation(LinearLayout.HORIZONTAL);
        langBox.setGravity(Gravity.CENTER_VERTICAL);
        langBox.setPadding(dp(10), dp(6), dp(10), dp(6));

        GradientDrawable langBg = new GradientDrawable();
        langBg.setColor(0xFF1A1A1A);
        langBg.setCornerRadius(dp(12));
        langBg.setStroke(dp(2), 0xFFFFD700);
        langBox.setBackground(langBg);

        // ================= LANGUAGE BOX =================
LinearLayout.LayoutParams lpLangBox =
        new LinearLayout.LayoutParams(0, dp(48), 1f);
lpLangBox.setMargins(dp(8), 0, 0, 0);
langBox.setLayoutParams(lpLangBox);

langBox.addView(langSpinner);

// Controls (μόνο language — mute είναι global)
controls.addView(langBox);
box.addView(controls);

// ================= OK =================
Button okBtn = new Button(this);
okBtn.setText("OK");
okBtn.setAllCaps(false);
okBtn.setTextColor(0xFFFFFFFF);

GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF0F8A3B);
okBg.setCornerRadius(dp(14));
okBg.setStroke(dp(3), 0xFFFFD700);
okBtn.setBackground(okBg);

LinearLayout.LayoutParams lpOk =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpOk.setMargins(0, dp(16), 0, 0);
okBtn.setLayoutParams(lpOk);

box.addView(okBtn);

// ================= DIALOG =================
b.setView(box);
final AlertDialog d = b.create();

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );
}

panicGuidePopupOpen = true;
d.show();

// 🔇 Stop TTS when dialog closes
d.setOnDismissListener(dialog -> {
    panicGuidePopupOpen = false;

    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
        }
    } catch (Throwable ignore) {}
});

// OK button
okBtn.setOnClickListener(v -> {
    panicGuidePopupOpen = false;

    try {
        if (tts != null && tts[0] != null) {
            tts[0].stop();
        }
    } catch (Throwable ignore) {}

    d.dismiss();
});

        }); 
}          

// ============================================================
// TEXT HELPERS (FINAL CLEAN VERSION)
// ============================================================

private String getPanicGuideTextEN() {
    return
        "To analyze iPhone stability, system logs must be imported.\n\n" +

        "Where to find them on iPhone:\n" +
        "Settings → Privacy & Security → Analytics & Improvements → Analytics Data\n\n" +

        "Look for files named:\n" +
        "• panic-full-xxxx.log\n" +
        "• panic-base-xxxx.log\n" +
        "• system-xxxx.ips\n\n" +

        "How to export:\n" +
        "Open a file → Share → Save to Files or Send via Email\n" +
        "Export all available files.\n\n" +

        "In this app:\n" +
        "Press Import and select all log files.\n" +
        "The app analyzes them together\n" +
        "to detect stability patterns.\n\n" +

        "Tip:\n" +
        "More logs improve diagnostic accuracy.";
}

private String getPanicGuideTextGR() {
    return
        "Για την ανάλυση σταθερότητας του iPhone, απαιτείται εισαγωγή αρχείων καταγραφής.\n\n" +

        "Πού θα τα βρεις στο iPhone:\n" +
        "Ρυθμίσεις → Απόρρητο & Ασφάλεια → Ανάλυση & Βελτιώσεις → Δεδομένα Ανάλυσης\n\n" +

        "Αναζήτησε αρχεία με ονόματα:\n" +
        "• panic-full-xxxx.log\n" +
        "• panic-base-xxxx.log\n" +
        "• system-xxxx.ips\n\n" +

        "Πώς να τα εξαγάγεις:\n" +
        "Άνοιξε το αρχείο → Κοινή χρήση → Αποθήκευση στα Αρχεία ή Αποστολή μέσω email\n" +
        "Εξήγαγε όλα τα διαθέσιμα αρχεία.\n\n" +

        "Στην εφαρμογή:\n" +
        "Πάτησε Import και επίλεξε όλα τα logs.\n" +
        "Η εφαρμογή τα αναλύει συνολικά\n" +
        "για εντοπισμό μοτίβων αστάθειας.\n\n" +

        "Συμβουλή:\n" +
        "Όσο περισσότερα logs, τόσο πιο αξιόπιστη η διάγνωση.";
}

// ============================================================
// TTS — LAB 28 (CALLED ONLY ON LANGUAGE CHANGE)
// ============================================================
private void speakPanicGuideTTS() {

    if (panicGuideMuted) return;

    try {
        if (tts == null || tts[0] == null || !ttsReady[0]) return;

        tts[0].stop();

        if ("GR".equals(panicGuideLang)) {

            tts[0].setLanguage(new Locale("el", "GR"));

            tts[0].speak(
                getPanicGuideTextGR(),
                TextToSpeech.QUEUE_FLUSH,
                null,
                "PANIC_GUIDE_GR"
            );

        } else {

            tts[0].setLanguage(Locale.US);

            tts[0].speak(
                getPanicGuideTextEN(),
                TextToSpeech.QUEUE_FLUSH,
                null,
                "PANIC_GUIDE_EN"
            );
        }

    } catch (Throwable ignore) {}
}

// ============================================================
// PANIC LOG IMPORT (SAF) — FINAL CLEAN
// ============================================================
private void openPanicLogPicker() {

    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("*/*");
    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    i.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "text/plain",
            "application/zip",
            "application/octet-stream"
    });

    startActivityForResult(i, REQ_PANIC_LOG);

    appendHtml("<br>");
    logLine();
    logInfo(AppLang.isGreek(this)
            ? "Ζητήθηκε εισαγωγή Panic Logs (SAF)."
            : "Panic Logs import requested (SAF).");
    logLine();
}

@Override
protected void onActivityResult(int requestCode,
                                int resultCode,
                                @Nullable Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode != REQ_PANIC_LOG) return;

    boolean gr = AppLang.isGreek(this);

    if (!appendMode) {
        panicLogCount = 0;
        panicLogText  = null;
        panicLogLoaded = false;
    }

    if (resultCode != RESULT_OK || data == null) {
        logWarn(gr
                ? "Η εισαγωγή ακυρώθηκε."
                : "Panic log import cancelled.");
        return;
    }

    try {

        List<Uri> uris = new ArrayList<>();

        if (data.getClipData() != null) {
            ClipData clip = data.getClipData();
            for (int i = 0; i < clip.getItemCount(); i++) {
                uris.add(clip.getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uris.add(data.getData());
        }

        if (uris.isEmpty()) {
            logWarn(gr
                    ? "Δεν επιλέχθηκαν αρχεία."
                    : "No files selected.");
            return;
        }

        logOk((gr ? "Επιλέχθηκαν αρχεία: " : "Files selected: ") + uris.size());

        StringBuilder allLogs = new StringBuilder();

        // append mode
        if (appendMode && panicLogLoaded && panicLogText != null) {
            allLogs.append(panicLogText);
        }

        int loadedCount = 0;

        for (Uri uri : uris) {

            String name = (uri != null) ? uri.getLastPathSegment() : "unknown";
            String safeName = (name != null) ? name : "unknown";

            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) continue;

            String text;

            if (looksLikeZip(safeName)) {
                text = readPanicFromZip(is);
            } else {
                text = readTextStream(is);
            }

            try { is.close(); } catch (Throwable ignore) {}

            if (text == null || text.trim().isEmpty()) {
    logWarn(gr
            ? "Κενό αρχείο: " + safe(safeName)
            : "Empty file: " + safe(safeName));
    continue;
}

if (looksCorruptedPanic(text)) {
    logError(gr
            ? "Μη έγκυρο ή κατεστραμμένο panic log: " + safe(safeName)
            : "Corrupted or invalid panic log: " + safe(safeName));
    continue;
}

            allLogs.append("\n\n===== FILE: ")
                   .append(safeName)
                   .append(" =====\n\n")
                   .append(text);

            loadedCount++;

            logOk((gr ? "Φορτώθηκε: "
                      : "Loaded: ")
                    + safe(safeName));
        }

        if (loadedCount == 0) {
            throw new Exception(gr
                    ? "Όλα τα αρχεία ήταν κενά."
                    : "All files were empty.");
        }

        panicLogCount = appendMode
                ? panicLogCount + loadedCount
                : loadedCount;

        panicLogName = (panicLogCount == 1)
                ? (gr ? "Ένα panic log"
                      : "Single panic log")
                : (gr
                   ? "Πολλαπλά panic logs (" + panicLogCount + " αρχεία)"
                   : "Multiple panic logs (" + panicLogCount + " files)");

        panicLogText   = allLogs.toString();
        panicLogLoaded = true;

        // cache signature από όλα
        parseAndCacheSignature(panicLogText);

        logLine();
        logOk(gr
                ? "Η εισαγωγή ολοκληρώθηκε."
                : "Import completed.");
        logInfo(gr
                ? "Συνολικό μέγεθος:"
                : "Total size:");
        logOk(panicLogText.length() + " chars");
        logOk(gr
                ? "Έτοιμο για ανάλυση."
                : "Ready for analysis.");
        logLine();

    } catch (Exception e) {

        panicLogLoaded = false;
        panicLogText   = null;

        logError(gr
                ? "Αποτυχία εισαγωγής."
                : "Panic logs import failed.");

        logInfo(gr ? "Αιτία:" : "Reason:");
        logWarn(safe(e.getMessage()));
    }
}

// ============================================================
// LAB 1 — PANIC LOG ANALYZER (Initial Screening)
// ============================================================
private void runPanicLogAnalyzer() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr 
            ? "LAB 1 — Ανάλυση Panic Logs"
            : "LAB 1 — Panic Log Analyzer");
    logLine();

    logOk(gr
            ? "Αρχικός έλεγχος του panic log σε γνωστά μοτίβα crash."
            : "Initial screening of the panic log against known crash patterns.");

    IPSPanicParser.Result r = IPSPanicParser.analyze(this, panicLogText);

    if (r == null) {

        logWarn(gr
                ? "Δεν εντοπίστηκε γνωστή υπογραφή panic."
                : "No known panic signature matched.");

        logInfo(gr ? "Τι σημαίνει αυτό:" : "What this means:");
        logOk(gr
                ? "Το log είναι έγκυρο, αλλά δεν αντιστοιχεί σε προκαθορισμένο μοτίβο crash."
                : "The log is valid, but it does not match a predefined crash pattern.");

        logInfo(gr ? "Γιατί έχει σημασία:" : "Why this matters:");
        logOk(gr
                ? "Ορισμένα crashes απαιτούν συμπεριφορική ανάλυση και όχι μόνο σύγκριση υπογραφών."
                : "Some crashes require behavioral analysis rather than signature matching.");

        logInfo(gr ? "Επόμενο βήμα:" : "Next step:");
        logOk(gr
                ? "Το επόμενο LAB θα ερμηνεύσει τη συμπεριφορά του crash πέρα από σταθερές υπογραφές."
                : "The next lab will interpret crash behavior beyond fixed signatures.");

        logInfo(gr ? "Αρχείο που αναλύθηκε:" : "File analyzed:");
        logOk(safe(panicLogName));

        appendHtml("<br>");
        logOk(gr ? "Το Lab 1 ολοκληρώθηκε." : "Lab 1 finished.");
        logLine();
        return;
    }

    logOk(gr
            ? "Εντοπίστηκε υπογραφή panic."
            : "Panic signature matched.");

    logInfo(gr ? "Pattern ID:" : "Pattern ID:");
    logOk(safe(r.patternId));

    logInfo(gr ? "Πιθανό Domain:" : "Domain (hint):");
    logWarn(safe(r.domain));

    logInfo(gr ? "Αναφερόμενη Αιτία:" : "Reported Cause:");
    logOk(safe(r.cause));

    logInfo(gr ? "Σοβαρότητα:" : "Severity:");
    if ("High".equalsIgnoreCase(r.severity)) {
        logError(safe(r.severity));
    } else if ("Medium".equalsIgnoreCase(r.severity)) {
        logWarn(safe(r.severity));
    } else {
        logOk(safe(r.severity));
    }

    logInfo(gr ? "Επίπεδο Βεβαιότητας:" : "Confidence:");
    if ("High".equalsIgnoreCase(r.confidence)) {
        logOk(safe(r.confidence));
    } else if ("Medium".equalsIgnoreCase(r.confidence)) {
        logWarn(safe(r.confidence));
    } else {
        logOk(safe(r.confidence));
    }

    logInfo(gr ? "Αρχική Σύσταση:" : "Initial Recommendation:");
    logOk(safe(r.recommendation));

    logInfo(gr ? "Επόμενο βήμα:" : "Next step:");
    logOk(gr
            ? "Η υπογραφή θα ερμηνευθεί αναλυτικά στο LAB 2."
            : "The extracted signature will be interpreted in detail in LAB 2.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 1 ολοκληρώθηκε." : "Lab 1 finished.");
    logLine();
}

// ============================================================
// LAB 2 — PANIC SIGNATURE PARSER (Behavior Interpretation)
// ============================================================
private void runPanicSignatureParser() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 2 — Ανάλυση Υπογραφής Panic"
            : "LAB 2 — Panic Signature Parser");
    logLine();

    logOk(gr
            ? "Ερμηνεία της συμπεριφοράς του crash με βάση τα διαθέσιμα στοιχεία."
            : "Interpreting crash behavior using contextual evidence.");

    parseAndCacheSignature(panicLogText);

    logInfo(gr ? "Αρχείο:" : "File:");
    logOk(safe(panicLogName));

    logInfo(gr ? "Τύπος Crash:" : "Crash Type:");
    if ("Kernel Panic".equalsIgnoreCase(sigCrashType)
            || "Watchdog / Hang".equalsIgnoreCase(sigCrashType)) {

        logError(safe(sigCrashType));
        logWarn(gr
                ? "Αυτό υποδηλώνει σοβαρή διακοπή λειτουργίας σε επίπεδο συστήματος."
                : "This represents a serious system-level interruption.");
    } else {
        logOk(safe(sigCrashType));
    }

    logInfo(gr ? "Πιθανό Υποσύστημα:" : "Subsystem Hint:");
    logWarn(safe(sigDomain));

    logWarn(gr
            ? "Υποδεικνύει πιθανή εμπλοκή υποσυστήματος, όχι επιβεβαιωμένη βλάβη."
            : "This indicates a possible subsystem involved, not a confirmed fault.");

    logInfo(gr ? "Επίπεδο Βεβαιότητας:" : "Confidence Level:");
    if ("High".equalsIgnoreCase(sigConfidence)) {
        logOk(safe(sigConfidence));
    } else if ("Medium".equalsIgnoreCase(sigConfidence)) {
        logWarn(safe(sigConfidence));
    } else {
        logOk(safe(sigConfidence));
    }

    if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {
        logInfo(gr ? "Βασικά Στοιχεία:" : "Key Evidence Found:");
        logOk(safe(sigKeyEvidence));
    }

    logInfo(gr ? "Επόμενο βήμα:" : "Next step:");
    logOk(gr
            ? "Η ανάλυση σταθερότητας θα συνεχιστεί στο LAB 3."
            : "The extracted signature will be interpreted further in LAB 3.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 2 ολοκληρώθηκε." : "Lab 2 finished.");
    logLine();
}

// ============================================================
// LAB 3 — SYSTEM STABILITY EVALUATION
// ============================================================
private void runStabilityLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 3 — Αξιολόγηση Σταθερότητας Συστήματος"
            : "LAB 3 — System Stability Evaluation");
    logLine();

    logOk(gr
            ? "Αξιολόγηση εάν το crash υποδηλώνει ευρύτερη αστάθεια συστήματος."
            : "Assessing whether the crash indicates broader system instability.");

    parseAndCacheSignature(panicLogText);

    if ("High".equalsIgnoreCase(sigConfidence)
            && ("Kernel Panic".equalsIgnoreCase(sigCrashType)
            || "Watchdog / Hang".equalsIgnoreCase(sigCrashType))) {

        logError(gr
                ? "Εντοπίστηκαν ισχυρές ενδείξεις αστάθειας συστήματος."
                : "High system instability indicators detected.");

        logWarn(gr
                ? "Τέτοια crashes συχνά σχετίζονται με επανεκκινήσεις ή παγώματα."
                : "Such crashes are often associated with reboots or freezes.");

        logInfo(gr ? "Με απλά λόγια:" : "In simple terms:");
        logWarn(gr
                ? "Η συσκευή δεν κατάφερε να διατηρήσει σταθερή λειτουργία υπό συγκεκριμένες συνθήκες."
                : "The device was unable to maintain stable operation under certain conditions.");

    } else if ("Medium".equalsIgnoreCase(sigConfidence)) {

        logWarn(gr
                ? "Εντοπίστηκε μέτριος κίνδυνος αστάθειας."
                : "Moderate stability risk detected.");

        logOk(gr
                ? "Το σύστημα ενδέχεται να γίνει ασταθές σε συγκεκριμένα σενάρια."
                : "The system may become unstable under specific scenarios.");

    } else {

        logOk(gr
                ? "Δεν εντοπίστηκαν ισχυρές ενδείξεις συνεχιζόμενης αστάθειας."
                : "No strong indicators of ongoing system instability found.");
    }

    logInfo(gr ? "Τύπος Crash:" : "Crash Type:");
    logOk(safe(sigCrashType));

    logInfo(gr ? "Επίπεδο Βεβαιότητας:" : "Confidence Level:");
    logOk(safe(sigConfidence));

    logInfo(gr ? "Επόμενο βήμα:" : "Next step:");
    logOk(gr
            ? "Το LAB 4 θα αναλύσει ποια περιοχή hardware ενδέχεται να εμπλέκεται."
            : "LAB 4 will analyze which hardware area is most likely involved.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 3 ολοκληρώθηκε." : "Lab 3 finished.");
    logLine();
}

// ============================================================
// LAB 4 — IMPACT ANALYSIS
// ============================================================
private void runImpactLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 4 — Ανάλυση Επιπτώσεων"
            : "LAB 4 — Impact Analysis");
    logLine();

    logInfo(gr
            ? "Αξιολόγηση πιθανών επιπτώσεων σε hardware ή σύστημα."
            : "Evaluating which hardware or system areas may be affected.");

    parseAndCacheSignature(panicLogText);

    logInfo(gr ? "Τύπος Crash:" : "Crash Type:");
    logOk(safe(sigCrashType));

    logInfo(gr ? "Πιθανό Domain:" : "Suspected Domain:");
    logWarn(safe(sigDomain));

    if ("Power / PMIC".equals(sigDomain)
            || "Storage / NAND / FS".equals(sigDomain)
            || "Baseband / Cellular".equals(sigDomain)) {

        logInfo(gr ? "Σημαντική διευκρίνιση:" : "Important clarification:");
        logOk(gr
                ? "Αυτό δεν επιβεβαιώνει ελαττωματικό εξάρτημα."
                : "This does not confirm a faulty component.");

        logError(gr
                ? "Υποδεικνύεται κρίσιμη διαδρομή σχετιζόμενη με hardware."
                : "A critical hardware-related path is suggested.");

        logWarn(gr
                ? "Εάν τα crashes επαναλαμβάνονται, συνιστάται τεχνικός έλεγχος."
                : "If crashes repeat, professional inspection is advised.");

    } else if ("Thermal / Cooling".equals(sigDomain)
            || "Memory / OS Pressure".equals(sigDomain)) {

        logWarn(gr
                ? "Υποδεικνύεται επίδραση λόγω φόρτου ή stress συστήματος."
                : "System stress-related impact suggested.");

        logOk(gr
                ? "Συχνά σχετίζεται με θερμοκρασία, φόρτο ή παρατεταμένη χρήση."
                : "Often linked to heat, load, or prolonged usage.");

    } else {

        logOk(gr
                ? "Δεν εντοπίστηκε σαφής επίπτωση hardware μόνο από αυτό το log."
                : "No clear hardware impact identified from this log alone.");
    }

    if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {
        logInfo(gr ? "Υποστηρικτικά Στοιχεία:" : "Supporting Evidence:");
        logOk(safe(sigKeyEvidence));
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 4 ολοκληρώθηκε." : "Lab 4 finished.");
    logLine();
}

// ============================================================
// LAB 5 — PANIC FREQUENCY ANALYZER (Multi-File Correlation)
// ============================================================
private void runPanicFrequencyLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 5 — Ανάλυση Συχνότητας Panic"
            : "LAB 5 — Panic Frequency Analyzer");
    logLine();

    String[] blocks = panicLogText.split("===== FILE:");

    if (blocks.length <= 1) {
        logWarn(gr
                ? "Δεν υπάρχουν πολλαπλά logs για σύγκριση."
                : "No multiple logs detected for comparison.");
        return;
    }

    java.util.Map<String, Integer> crashCount = new java.util.HashMap<>();

    for (String block : blocks) {
        if (block.trim().isEmpty()) continue;

        resetSignatureCache();
parseAndCacheSignature(block);

        String key = sigCrashType;
        crashCount.put(key, crashCount.getOrDefault(key, 0) + 1);
    }

    logInfo(gr ? "Συχνότητα Crash Types:" : "Crash Type Frequency:");

    for (String k : crashCount.keySet()) {
        logOk(k + " → " + crashCount.get(k));
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 5 ολοκληρώθηκε." : "Lab 5 finished.");
    logLine();
}

// ============================================================
// LAB 6 — PANIC DOMAIN CLUSTERING
// ============================================================
private void runPanicClusteringLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 6 — Ομαδοποίηση Domain Panic"
            : "LAB 6 — Panic Domain Clustering");
    logLine();

    String[] blocks = panicLogText.split("===== FILE:");

    if (blocks.length <= 1) {
        logWarn(gr
                ? "Δεν υπάρχουν πολλαπλά logs για clustering."
                : "No multiple logs available for clustering.");
        return;
    }

    java.util.Map<String, Integer> domainCount = new java.util.HashMap<>();

    for (String block : blocks) {
        if (block.trim().isEmpty()) continue;

        resetSignatureCache();
parseAndCacheSignature(block);

        String key = sigDomain;
        domainCount.put(key, domainCount.getOrDefault(key, 0) + 1);
    }

    logInfo(gr ? "Συχνότητα Domain:" : "Domain Frequency:");

    for (String k : domainCount.keySet()) {
        int count = domainCount.get(k);

        if (count >= 2) {
            logError(k + " → " + count);
        } else {
            logOk(k + " → " + count);
        }
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 6 ολοκληρώθηκε." : "Lab 6 finished.");
    logLine();
}

// ============================================================
// LAB 7 — RECURRING DOMAIN DETECTION (Pattern Scoring)
// ============================================================
private void runRecurringDomainLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 7 — Ανίχνευση Επαναλαμβανόμενου Domain"
            : "LAB 7 — Recurring Domain Detection");
    logLine();

    String[] blocks = panicLogText.split("===== FILE:");

    if (blocks.length <= 1) {
        logOk(gr
                ? "Απαιτούνται πολλαπλά logs για ανίχνευση μοτίβου."
                : "Multiple logs are required for pattern detection.");
        return;
    }

    java.util.Map<String, Integer> domainCount = new java.util.HashMap<>();

    int total = 0;

    for (String block : blocks) {
        if (block.trim().isEmpty()) continue;

        resetSignatureCache();
parseAndCacheSignature(block);

        String key = sigDomain;
        domainCount.put(key, domainCount.getOrDefault(key, 0) + 1);
        total++;
    }

    if (total == 0) {
        logWarn(gr ? "Δεν βρέθηκαν έγκυρα logs." : "No valid logs found.");
        return;
    }

    // Βρες dominant
    String dominant = null;
    int max = 0;

    for (String d : domainCount.keySet()) {
        int c = domainCount.get(d);
        if (c > max) {
            max = c;
            dominant = d;
        }
    }

    double ratio = (double) max / (double) total;
    int percent = (int) (ratio * 100);

    logInfo(gr ? "Συνολικά logs:" : "Total logs:");
    logOk(String.valueOf(total));

    logInfo(gr ? "Κυρίαρχο domain:" : "Dominant domain:");
    logWarn(dominant + " (" + max + "/" + total + ")");

    logInfo(gr ? "Ποσοστό επανάληψης:" : "Repetition ratio:");
    logOk(percent + "%");

    // -------------------------------
    // PATTERN INTERPRETATION
    // -------------------------------

    if (ratio >= 0.5) {

        if (isHighRiskDomain(dominant)) {
            logError(gr
                    ? "Εντοπίστηκε ισχυρό επαναλαμβανόμενο hardware pattern."
                    : "Strong recurring hardware pattern detected.");
        } else {
            logWarn(gr
                    ? "Εντοπίστηκε επαναλαμβανόμενο μοτίβο domain."
                    : "Recurring domain pattern detected.");
        }

        logOk(gr
                ? "Αυτό δεν επιβεβαιώνει βλάβη, αλλά δείχνει σταθερή επανάληψη."
                : "This does not confirm hardware failure, but indicates stable recurrence.");

    } else if (max >= 2) {

        logWarn(gr
                ? "Μερική επανάληψη domain εντοπίστηκε."
                : "Partial domain recurrence detected.");

    } else {

        logOk(gr
                ? "Δεν εντοπίστηκε επαναλαμβανόμενο domain."
                : "No recurring domain pattern detected.");
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 7 ολοκληρώθηκε." : "Lab 7 finished.");
    logLine();
}

// ============================================================
// LAB 8 — STABILITY INDEX (Deterministic Score Engine)
// ============================================================
private void runStabilityIndexLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 8 — Δείκτης Σταθερότητας"
            : "LAB 8 — Stability Index");
    logLine();

    String[] blocks = panicLogText.split("===== FILE:");

    if (blocks.length <= 1) {
        logOk(gr
                ? "Απαιτούνται πολλαπλά logs για υπολογισμό δείκτη."
                : "Multiple logs are required to calculate index.");
        return;
    }

    java.util.Map<String, Integer> domainCount = new java.util.HashMap<>();
    int total = 0;

    int highConfidenceCount = 0;
    int criticalCrashCount = 0;

    for (String block : blocks) {
        if (block.trim().isEmpty()) continue;

        resetSignatureCache();
parseAndCacheSignature(block);

        total++;

        if (isHighConfidence(sigConfidence)) {
            highConfidenceCount++;
        }

        if (isCriticalCrash(sigCrashType)) {
            criticalCrashCount++;
        }

        domainCount.put(sigDomain,
                domainCount.getOrDefault(sigDomain, 0) + 1);
    }

    if (total == 0) {
        logWarn(gr ? "Δεν βρέθηκαν έγκυρα logs." : "No valid logs found.");
        return;
    }

    // ------------------------------------------------------------
    // SCORE ENGINE
    // ------------------------------------------------------------

    int score = 100;

    // High confidence crashes
    score -= (highConfidenceCount * 15);

    // Critical crash types (Kernel / Watchdog)
    score -= (criticalCrashCount * 10);

    // Domain recurrence
    String dominant = null;
    int max = 0;

    for (String d : domainCount.keySet()) {
        int c = domainCount.get(d);
        if (c > max) {
            max = c;
            dominant = d;
        }
    }

    double ratio = (double) max / (double) total;

    if (ratio >= 0.5) {
        if (isHighRiskDomain(dominant)) {
            score -= 30;
        } else {
            score -= 15;
        }
    } else if (max >= 2) {
        score -= 10;
    }

    if (score < 0) score = 0;

    // ------------------------------------------------------------
    // OUTPUT
    // ------------------------------------------------------------

    logInfo(gr ? "Συνολικά logs:" : "Total logs:");
    logOk(String.valueOf(total));

    logInfo(gr ? "Δείκτης Σταθερότητας:" : "Stability Index:");
    logOk(score + " / 100");

    // ------------------------------------------------------------
    // INTERPRETATION
    // ------------------------------------------------------------

    if (score >= 85) {

        logOk(gr
                ? "Υψηλή σταθερότητα. Δεν εντοπίστηκαν σοβαρά μοτίβα."
                : "High stability. No significant instability patterns detected.");

    } else if (score >= 60) {

        logWarn(gr
                ? "Μέτρια ένδειξη αστάθειας. Συνιστάται παρακολούθηση."
                : "Moderate instability indicators. Monitoring advised.");

    } else if (score >= 40) {

        logWarn(gr
                ? "Αυξημένες ενδείξεις αστάθειας."
                : "Elevated instability indicators detected.");

        logOk(gr
                ? "Εάν τα συμπτώματα επαναλαμβάνονται, συνιστάται τεχνικός έλεγχος."
                : "If symptoms persist, professional inspection is recommended.");

    } else {

        logError(gr
                ? "Χαμηλή σταθερότητα συστήματος."
                : "Low system stability detected.");

        logWarn(gr
                ? "Εντοπίστηκαν επαναλαμβανόμενα κρίσιμα μοτίβα."
                : "Recurring critical patterns were detected.");
    }

    appendHtml("<br>");
    logOk(gr ? "Το Lab 8 ολοκληρώθηκε." : "Lab 8 finished.");
    logLine();
}

// ============================================================
// LAB 9 — FINAL SERVICE RECOMMENDATION (Integrated Engine)
// ============================================================
private void runFinalServiceRecommendationLab() {
    if (!guardPanicLog()) return;

    boolean gr = AppLang.isGreek(this);

    appendHtml("<br>");
    logLine();
    logInfo(gr
            ? "LAB 9 — Τελική Σύσταση Service"
            : "LAB 9 — Final Service Recommendation");
    logLine();

    String[] blocks = panicLogText.split("===== FILE:");

    if (blocks.length <= 1) {
        logOk(gr
                ? "Απαιτούνται πολλαπλά logs για πλήρη τεχνική αξιολόγηση."
                : "Multiple logs are required for full technical evaluation.");
        return;
    }

    java.util.Map<String, Integer> domainCount = new java.util.HashMap<>();
    java.util.Map<String, Integer> crashCount = new java.util.HashMap<>();

    int total = 0;
    int highConfidenceCount = 0;
    int criticalCrashCount = 0;

    for (String block : blocks) {
        if (block.trim().isEmpty()) continue;

        resetSignatureCache();
parseAndCacheSignature(block);

        total++;

        crashCount.put(sigCrashType,
                crashCount.getOrDefault(sigCrashType, 0) + 1);

        domainCount.put(sigDomain,
                domainCount.getOrDefault(sigDomain, 0) + 1);

        if (isHighConfidence(sigConfidence))
            highConfidenceCount++;

        if (isCriticalCrash(sigCrashType))
            criticalCrashCount++;
    }

    if (total == 0) {
        logWarn(gr ? "Δεν βρέθηκαν έγκυρα logs." : "No valid logs found.");
        return;
    }

    // ------------------------------------------------------------
    // DOMINANT DOMAIN
    // ------------------------------------------------------------
    String dominant = null;
    int max = 0;

    for (String d : domainCount.keySet()) {
        int c = domainCount.get(d);
        if (c > max) {
            max = c;
            dominant = d;
        }
    }

    double ratio = (double) max / (double) total;
    int percent = (int) (ratio * 100);

    // ------------------------------------------------------------
    // STABILITY SCORE (Deterministic)
    // ------------------------------------------------------------
    int score = 100;

    score -= (highConfidenceCount * 15);
    score -= (criticalCrashCount * 10);

    if (ratio >= 0.5) {
        if (isHighRiskDomain(dominant)) {
            score -= 30;
        } else {
            score -= 15;
        }
    } else if (max >= 2) {
        score -= 10;
    }

    if (score < 0) score = 0;

    // ------------------------------------------------------------
    // OUTPUT SUMMARY
    // ------------------------------------------------------------
    logInfo(gr ? "Συνολικά logs:" : "Total logs:");
    logOk(String.valueOf(total));

    logInfo(gr ? "Κυρίαρχο domain:" : "Dominant domain:");
    logWarn(safe(dominant) + " (" + max + "/" + total + ")");

    logInfo(gr ? "Ποσοστό επανάληψης:" : "Repetition ratio:");
    logOk(percent + "%");

    logInfo(gr ? "Δείκτης Σταθερότητας:" : "Stability Index:");
    logOk(score + " / 100");

    logLine();

    // ------------------------------------------------------------
    // FINAL VERDICT ENGINE
    // ------------------------------------------------------------
    if (score >= 85) {

        logOk(gr
                ? "Υψηλή σταθερότητα συστήματος."
                : "High system stability.");

        logOk(gr
                ? "Δεν εντοπίστηκαν σοβαρά επαναλαμβανόμενα μοτίβα."
                : "No significant recurring crash patterns detected.");

    } else if (score >= 60) {

        logWarn(gr
                ? "Μέτρια ένδειξη αστάθειας."
                : "Moderate instability indicators detected.");

        logOk(gr
                ? "Συνιστάται παρακολούθηση εάν τα συμπτώματα συνεχιστούν."
                : "Monitoring is advised if symptoms persist.");

    } else if (score >= 40) {

        logWarn(gr
                ? "Αυξημένες ενδείξεις αστάθειας."
                : "Elevated instability indicators detected.");

        logWarn(gr
                ? "Εντοπίστηκε επαναλαμβανόμενο domain με σημαντική συχνότητα."
                : "A recurring hardware domain was detected with notable frequency.");

        logOk(gr
                ? "Συνιστάται τεχνικός έλεγχος εφόσον το φαινόμενο επαναλαμβάνεται."
                : "Professional inspection is recommended if recurrence continues.");

    } else {

        logError(gr
                ? "Χαμηλή σταθερότητα συστήματος."
                : "Low system stability detected.");

        logWarn(gr
                ? "Εντοπίστηκαν επαναλαμβανόμενα κρίσιμα μοτίβα crash."
                : "Recurring critical crash patterns were detected.");

        logError(gr
                ? "Συνιστάται άμεσος τεχνικός έλεγχος."
                : "Technical inspection is strongly recommended.");
    }

    logLine();

    // ------------------------------------------------------------
    // PROFESSIONAL NOTE
    // ------------------------------------------------------------
    logInfo(gr ? "Τελική σημείωση:" : "Final note:");

    logOk(gr
            ? "Η ανάλυση βασίζεται σε διαθέσιμα panic logs και δεν αντικαθιστά φυσικό τεχνικό έλεγχο."
            : "This analysis is based on available panic logs and does not replace physical device inspection.");

    logOk(gr
            ? "Τα συμπεράσματα πρέπει να συσχετίζονται με τα πραγματικά συμπτώματα της συσκευής."
            : "Conclusions should be correlated with real-world device symptoms.");

    appendHtml("<br>");
    logOk(gr ? "Το Lab 9 ολοκληρώθηκε." : "Lab 9 finished.");
    logLine();
}

// ============================================================
// GUARD
// ============================================================
private boolean guardPanicLog() {

    boolean gr = AppLang.isGreek(this);

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {

        toast(gr
                ? "Φόρτωσε πρώτα panic log."
                : "Load panic log first.");

        logWarn(gr
                ? "Φόρτωσε πρώτα panic log."
                : "Load Panic Log first.");

        return false;
    }

    return true;
}

// ============================================================
// ZIP/TEXT READERS
// ============================================================
private String readTextStream(InputStream is) throws Exception {

    BufferedInputStream bis = new BufferedInputStream(is);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    byte[] buf = new byte[4096];
    int read;
    int total = 0;

    while ((read = bis.read(buf)) != -1) {
        total += read;
        if (total > MAX_TEXT_BYTES) break;
        bos.write(buf, 0, read);
    }

    try { bis.close(); } catch (Throwable ignore) {}

    byte[] bytes = bos.toByteArray();

    String s = new String(bytes, Charset.forName("UTF-8"));
    if (looksGarbled(s)) s = new String(bytes, Charset.forName("ISO-8859-1"));

    return s;
}

private String readPanicFromZip(InputStream is) throws Exception {

    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
    ZipEntry entry;
    int scanned = 0;

    try {

        while ((entry = zis.getNextEntry()) != null && scanned < ZIP_SCAN_CAP) {
            scanned++;

            String name = (entry.getName() != null)
                    ? entry.getName().toLowerCase(Locale.US)
                    : "";

            boolean candidate =
                    name.contains("panic") ||
                    name.endsWith(".ips") ||
                    name.endsWith(".log") ||
                    name.endsWith(".txt");

            if (!candidate) continue;

            String text = readTextStream(zis);

            if (text != null && !text.trim().isEmpty()) {
                return text;
            }
        }

    } finally {
        try { zis.close(); } catch (Throwable ignore) {}
    }

    throw new Exception(
            AppLang.isGreek(this)
                    ? "Δεν βρέθηκε αναγνώσιμο panic log μέσα στο ZIP (πιθανώς κατεστραμμένο αρχείο)."
                    : "No readable panic entry found in ZIP (file may be corrupted)."
    );
}

// ============================================================
// SIGNATURE PARSER STATE (CANONICAL - DO NOT LOCALIZE)
// ============================================================
private static final String CRASH_UNKNOWN = "Unknown";
private static final String CONF_LOW      = "Low";

private String sigCrashType   = CRASH_UNKNOWN;
private String sigDomain      = CRASH_UNKNOWN;
private String sigConfidence  = CONF_LOW;
private String sigKeyEvidence = "";

private void parseAndCacheSignature(String text) {

    // reset state
    sigCrashType   = CRASH_UNKNOWN;
    sigDomain      = CRASH_UNKNOWN;
    sigConfidence  = CONF_LOW;
    sigKeyEvidence = "";

    if (text == null || text.trim().isEmpty()) return;

        String low = text.toLowerCase(Locale.US);

        boolean isWatchdog   = low.contains("watchdog") || low.contains("0x8badf00d");
        boolean isKernelPanic= low.contains("panic(") || low.contains("panic cpu") || low.contains("panicstring");
        boolean isJetsam     = low.contains("jetsam") || low.contains("memorystatus") || low.contains("highwater");
        boolean isThermal    = low.contains("thermal") && (low.contains("shutdown") || low.contains("throttle"));
        boolean isI2C        = low.contains("i2c") || low.contains("bus error");
        boolean isNand       = low.contains("nand") || low.contains("apfs") || low.contains("nvme") || low.contains("storage");
        boolean isBaseband   = low.contains("baseband") || low.contains("commcenter");
        boolean isPower      = low.contains("power") && (low.contains("pmu") || low.contains("brownout") || low.contains("sudden"));
        boolean isGpu        = low.contains("gpu") || low.contains("agx") || low.contains("metal");
        boolean isSensor     = low.contains("sensor") || low.contains("mic") || low.contains("camera") || low.contains("touch");

        if (isWatchdog) sigCrashType = "Watchdog / Hang";
        else if (isJetsam) sigCrashType = "Jetsam / Memory Pressure";
        else if (isThermal) sigCrashType = "Thermal Shutdown / Throttle";
        else if (isKernelPanic) sigCrashType = "Kernel Panic";
        else sigCrashType = "Unknown / Generic";

        if (isBaseband) sigDomain = "Baseband / Cellular";
        else if (isNand) sigDomain = "Storage / NAND / FS";
        else if (isGpu) sigDomain = "GPU / Graphics";
        else if (isI2C) sigDomain = "I2C / Peripheral Bus";
        else if (isPower || low.contains("brownout") || low.contains("pmu") || low.contains("pwr")) sigDomain = "Power / PMIC";
        else if (isThermal) sigDomain = "Thermal / Cooling";
        else if (isJetsam) sigDomain = "Memory / OS Pressure";
        else if (isSensor) sigDomain = "Sensors / I/O";
        else if (isKernelPanic) sigDomain = "Kernel / OS Core";
        else sigDomain = "Unknown";

        int points = 0;
        StringBuilder ev = new StringBuilder();

        if (isWatchdog)              { points += 30; evAppend(ev, "watchdog"); }
        if (low.contains("panicstring")) { points += 30; evAppend(ev, "panicString"); }
        if (low.contains("bug_type"))    { points += 20; evAppend(ev, "bug_type"); }
        if (low.contains("panic cpu"))   { points += 20; evAppend(ev, "panic cpu"); }
        if (low.contains("0x8badf00d"))  { points += 25; evAppend(ev, "0x8badf00d"); }
        if (isBaseband)              { points += 20; evAppend(ev, "baseband"); }
        if (isNand)                  { points += 20; evAppend(ev, "storage"); }
        if (isGpu)                   { points += 20; evAppend(ev, "gpu/agx"); }
        if (isThermal)               { points += 20; evAppend(ev, "thermal"); }
        if (isJetsam)                { points += 20; evAppend(ev, "jetsam"); }

        if (points >= 70) sigConfidence = "High";
        else if (points >= 40) sigConfidence = "Medium";
        else sigConfidence = "Low";

        sigKeyEvidence = ev.toString();
        if (sigKeyEvidence.endsWith(", ")) sigKeyEvidence = sigKeyEvidence.substring(0, sigKeyEvidence.length() - 2);
    }

    private void evAppend(StringBuilder ev, String token) {
        if (ev == null) return;
        ev.append(token).append(", ");
    }

    // ============================================================
    // UI HELPER — BUTTON (GUARDED CLICK)
    // ============================================================
    private View makeLabButton(
        String title,
        String subtitle,
        boolean requiresPanicLog,
        View.OnClickListener realClick
) {
    LinearLayout container = new LinearLayout(this);
    container.setOrientation(LinearLayout.VERTICAL);

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );
    lp.setMargins(0, dp(10), 0, dp(10));
    container.setLayoutParams(lp);

    container.setPadding(dp(16), dp(16), dp(16), dp(16));
    container.setBackgroundResource(R.drawable.gel_btn_outline_selector);
    container.setClickable(true);
    container.setFocusable(true);
    container.setFocusableInTouchMode(false);

    TextView t = new TextView(this);
    t.setText(title);
    t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    t.setTextColor(COLOR_NEON);
    t.setIncludeFontPadding(false);
    t.setClickable(false);
    t.setFocusable(false);

    TextView s = new TextView(this);
    s.setText(subtitle);
    s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    s.setTextColor(COLOR_WHITE);
    s.setPadding(0, dp(6), 0, 0);
    s.setClickable(false);
    s.setFocusable(false);

    container.addView(t);
    container.addView(s);

    // εφέ πατήματος (μία φορά για όλα τα buttons)
    UIHelpers.applyPressEffect(container);

    // guarded click
container.setOnClickListener(v -> {

    if (requiresPanicLog &&
        (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty())) {

        boolean gr = AppLang.isGreek(this);

        String msg = gr
                ? "Φόρτωσε πρώτα panic log."
                : "Load panic log first.";

        toast(msg);
        logWarn(msg);
        return;
    }

    if (realClick != null) {
        realClick.onClick(v);
    }
});

    return container;
}

private void setButtonTextWhite(View container) {

    if (!(container instanceof ViewGroup)) return;

    ViewGroup vg = (ViewGroup) container;

    for (int i = 0; i < vg.getChildCount(); i++) {
        View child = vg.getChildAt(i);

        if (child instanceof TextView) {
            ((TextView) child).setTextColor(0xFFFFFFFF);
        }

        if (child instanceof ViewGroup) {
            setButtonTextWhite(child);
        }
    }
}

// ============================================================
// LOGGING — GEL CANONICAL (UI + SERVICE REPORT)
// ============================================================

private static final int MAX_LOG_BUFFER = 250_000; // προστασία από UI lag

private void appendHtml(String htmlLine) {
    if (txtLog == null || htmlLine == null) return;

    logHtmlBuffer.append(htmlLine).append("<br>");

    // 🔒 Prevent unbounded growth (large panic logs protection)
    if (logHtmlBuffer.length() > MAX_LOG_BUFFER) {
        logHtmlBuffer.delete(
                0,
                logHtmlBuffer.length() - MAX_LOG_BUFFER
        );
    }

    try {
        txtLog.setText(
                Html.fromHtml(
                        logHtmlBuffer.toString(),
                        Html.FROM_HTML_MODE_LEGACY
                )
        );
    } catch (Throwable ignore) {
        txtLog.setText(logHtmlBuffer.toString());
    }
}

private void logInfo(String msg) {
    String clean = safe(msg);
    String s = "ℹ️ " + clean;
    appendHtml(escape(s));
    GELServiceLog.logInfo(clean);
}

private void logOk(String msg) {
    String clean = safe(msg);
    String s = "✔ " + clean;
    appendHtml("<font color='#39FF14'>" + escape(s) + "</font>");
    GELServiceLog.logOk(clean);
}

private void logWarn(String msg) {
    String clean = safe(msg);
    String s = "⚠ " + clean;
    appendHtml("<font color='#FFD966'>" + escape(s) + "</font>");
    GELServiceLog.logWarn(clean);
}

private void logError(String msg) {
    String clean = safe(msg);
    String s = "✖ " + clean;
    appendHtml("<font color='#FF5555'>" + escape(s) + "</font>");
    GELServiceLog.logError(clean);
}

private void logLine() {
    appendHtml("--------------------------------------------------");
    GELServiceLog.logLine();
}
// ------------------------------------------------------------
// UI APPENDER
// ------------------------------------------------------------

private String stripHtml(String s) {
    if (s == null) return "";
    return s.replace("<br>", "\n").replaceAll("<[^>]*>", "");
}

private String escape(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
}

// ============================================================
// SEMANTIC HELPERS (NO COLORS — LOG METHODS DECIDE)
// ============================================================

private boolean isHighConfidence(String conf) {
    return conf != null && "High".equalsIgnoreCase(conf);
}

private boolean isMediumConfidence(String conf) {
    return conf != null && "Medium".equalsIgnoreCase(conf);
}

private boolean isCriticalSeverity(String sev) {
    if (sev == null) return false;
    String s = sev.toLowerCase(Locale.US);
    return s.contains("critical") || s.contains("high");
}

private boolean isMediumSeverity(String sev) {
    if (sev == null) return false;
    String s = sev.toLowerCase(Locale.US);
    return s.contains("medium") || s.contains("warn");
}

private boolean isCriticalCrash(String crash) {
    if (crash == null) return false;
    return crash.contains("Kernel Panic")
            || crash.contains("Watchdog");
}

private boolean isWarningCrash(String crash) {
    if (crash == null) return false;
    return crash.contains("Thermal")
            || crash.contains("Jetsam");
}

private boolean isHighRiskDomain(String domain) {
    if (domain == null) return false;
    return domain.contains("Power")
            || domain.contains("Baseband")
            || domain.contains("Storage");
}

private boolean isWarningDomain(String domain) {
    if (domain == null) return false;
    return domain.contains("Thermal")
            || domain.contains("Memory")
            || domain.contains("GPU");
}

private String safe(String s) {
    return (s == null || s.trim().isEmpty()) ? "unknown" : s;
}

    // ============================================================
    // HELPERS (dp/sp + I/O)
    // ============================================================
    private int dp(float v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    private float sp(float v) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, v, getResources().getDisplayMetrics());
    }

    private boolean looksLikeZip(String name) {
        if (name == null) return false;
        String low = name.toLowerCase(Locale.US);
        return low.endsWith(".zip") || low.contains(".zip");
    }

    private boolean looksGarbled(String s) {
        if (s == null || s.isEmpty()) return false;
        int bad = 0;
        int lim = Math.min(s.length(), 4000);
        for (int i = 0; i < lim; i++) {
            if (s.charAt(i) == '\uFFFD') bad++;
        }
        return bad > 10;
    }

    // (kept for compatibility with other blocks you might paste later)
    private boolean textContainsAny(String text, String... keys) {
        if (text == null || keys == null) return false;
        String low = text.toLowerCase(Locale.US);
        for (String k : keys) {
            if (k == null) continue;
            if (low.contains(k.toLowerCase(Locale.US))) return true;
        }
        return false;
    }
}
