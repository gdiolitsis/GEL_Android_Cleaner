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
	
	// ==========================
    // TTS ENGINE
    // ==========================
    private TextToSpeech[] tts   = new TextToSpeech[1];
    private boolean[]     ttsReady = new boolean[1];

    private final StringBuilder logHtmlBuffer = new StringBuilder();

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

        // TITLE
        TextView title = new TextView(this);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        title.setText("GEL iPhone Diagnostics");
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
        sub.setText(
                "Laboratory diagnostics for iPhone using system files\n" +
                "Service-grade log analysis (no direct device access)"
        );
        sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        sub.setTextColor(COLOR_GRAY);
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.setPadding(0, dp(8), 0, dp(18));
        sub.setIncludeFontPadding(false);
        root.addView(sub);

        // ============================================================
        // LAB BUTTONS (GUARDED)
        // ============================================================

        // 1) Import (replace mode)
root.addView(makeLabButton(
        "Panic Log Import (TXT / ZIP)",
        "Auto unzip + load panic report",
        false,
        v -> {
            appendMode = false;
            openPanicLogPicker();
        }
));

// 1b) Add more logs (append mode)
root.addView(makeLabButton(
        "Add more panic logs",
        "Append logs to current analysis",
        false,
        v -> {
            appendMode = true;
            openPanicLogPicker();
        }
));

        // 2) Analyzer (guard)
        root.addView(makeLabButton(
                "LAB 1 -  Panic Log Analyzer",
                "Pattern match • Domain • Cause • Severity • Recommendation",
                true,
                v -> runPanicLogAnalyzer()
        ));

        // 3) Signature Parser (guard)
        root.addView(makeLabButton(
                "LAB 2 -  Panic Signature Parser",
                "Crash Type • Domain • Confidence • Evidence",
                true,
                v -> runPanicSignatureParser()
        ));

        // 4) Stability (guard)
        root.addView(makeLabButton(
                "LAB 3 - System Stability Evaluation",
                "Evaluate iOS stability from available logs",
                true,
                v -> runStabilityLab()
        ));

        // 5) Impact (guard)
        root.addView(makeLabButton(
                "LAB 4 -  Impact Analysis",
                "Correlate crash with probable hardware domain",
                true,
                v -> runImpactLab()
        ));

        // 6) Service Verdict (guard)
        root.addView(makeLabButton(
                "LAB 5 - Service Recommendation",
                "Final service verdict (technician-friendly)",
                true,
                v -> runServiceRecommendationLab()
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

if (panicGuidePopupOpen) {
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
logInfo("GEL iPhone Labs — ready.");
logLine();

logOk("Import a panic log to begin analysis.");

} // onCreate ends here

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
        msg.setTextColor(0xFFDDDDDD);
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

        // 🔕 MUTE
        Button muteBtn = new Button(this);
        muteBtn.setText(panicGuideMuted ? "Unmute" : "Mute");
        muteBtn.setAllCaps(false);
        muteBtn.setTextColor(0xFFFFFFFF);

        GradientDrawable muteBg = new GradientDrawable();
        muteBg.setColor(0xFF444444);
        muteBg.setCornerRadius(dp(12));
        muteBg.setStroke(dp(2), 0xFFFFD700);
        muteBtn.setBackground(muteBg);

        LinearLayout.LayoutParams lpMute =
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lpMute.setMargins(0, 0, dp(8), 0);
        muteBtn.setLayoutParams(lpMute);

        muteBtn.setOnClickListener(v -> {
            panicGuideMuted = !panicGuideMuted;
            muteBtn.setText(panicGuideMuted ? "Unmute" : "Mute");

            try {
                if (panicGuideMuted && tts != null && tts[0] != null) {
                    tts[0].stop();
                }
            } catch (Throwable ignore) {}
        });

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
    speakPanicGuideTTS();
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

        LinearLayout.LayoutParams lpLangBox =
                new LinearLayout.LayoutParams(0, dp(48), 1f);
        lpLangBox.setMargins(dp(8), 0, 0, 0);
        langBox.setLayoutParams(lpLangBox);

        langBox.addView(langSpinner);

        controls.addView(muteBtn);
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

        b.setView(box);
        final AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        d.show();

        okBtn.setOnClickListener(v -> d.dismiss());
    });
}

// ============================================================
// TEXT HELPERS
// ============================================================

private String getPanicGuideTextEN() {
    return
        "To analyze iPhone stability, you need to import system logs.\n\n" +

        "Where to find them on iPhone:\n" +
        "Settings. → Privacy & Security. → Analytics & Improvements. → Analytics Data.\n\n" +

        "Look for files named:\n" +
        "• panic-full-xxxx.log.\n" +
        "• panic-base-xxxx.log.\n" +
        "• system-xxxx.ips.\n\n" +

        "How to export:\n" +
        "Tap a file. → Share. → Save to Files, or, Send by Email.\n" +
        "You must export all files.\n\n" +

        "In this app:\n" +
        "Press Import, and select all log files.\n" +
        "The app, will analyze all of them together,\n" +
        "to detect stability patterns.\n\n" +

        "Tip:\n" +
        "More logs, means better diagnosis accuracy.";
}

private String getPanicGuideTextGR() {
    return
        "Για την ανάλυση σταθερότητας iPhone, χρειάζονται τα αρχεία καταγραφής.\n\n" +

        "Πού τα βρίσκεις στο iPhone:\n" +
        "Ρυθμίσεις. → Απόρρητο & Ασφάλεια. → Ανάλυση & Βελτιώσεις. → Δεδομένα ανάλυσης.\n\n" +

        "Αναζήτησε αρχεία όπως:\n" +
        "• panic-full-xxxx.log.\n" +
        "• panic-base-xxxx.log.\n" +
        "• system-xxxx.ips.\n\n" +

        "Πώς τα εξάγεις:\n" +
        "Πάτησε στο αρχείο. → Κοινή χρήση. → Αποθήκευση στα Αρχεία, ή, Αποστολή με email.\n" +
        "Πρέπει να στείλεις όλα τα αρχεία.\n\n" +

        "Στην εφαρμογή:\n" +
        "Πάτησε Import, και διάλεξε όλα τα logs.\n" +
        "Η εφαρμογή τα αναλύει όλα μαζί,\n" +
        "για να εντοπίσει μοτίβα αστάθειας.\n\n" +

        "Συμβουλή:\n" +
        "Όσο περισσότερα logs, τόσο πιο αξιόπιστο το αποτέλεσμα.";
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
// PANIC LOG IMPORT (SAF)
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
    logInfo("Panic Logs Import requested (SAF).");
    logLine();
}

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode != REQ_PANIC_LOG) return;

    // reset ΜΟΝΟ όταν όντως γυρίσαμε από Panic Log picker
    if (!appendMode) {
        panicLogCount = 0;
        panicLogText  = null;
    }

    if (resultCode != RESULT_OK || data == null) {
        logWarn("Panic log import cancelled.");
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
            logWarn("No files selected.");
            return;
        }

        logOk("Panic logs selected: " + uris.size());

        StringBuilder allLogs = new StringBuilder();

if (appendMode && panicLogLoaded && panicLogText != null) {
    allLogs.append(panicLogText);
}

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

            if (text == null || text.trim().isEmpty()) {
                logWarn("Skipped empty file: " + safe(safeName));
                continue;
            }

            allLogs.append("\n\n===== FILE: ")
                   .append(safeName)
                   .append(" =====\n\n")
                   .append(text);

            logOk("Loaded: " + safe(safeName));
        }

           panicLogCount = uris.size();

if (panicLogCount == 1) {
    panicLogName = "Single panic log";
} else {
    panicLogName = "Multiple panic logs (" + panicLogCount + " files)";
}

        if (allLogs.length() == 0) {
            throw new Exception("All files empty.");
        }

        panicLogText   = allLogs.toString();
panicLogLoaded = true;

panicLogCount += uris.size();

panicLogName = (panicLogCount == 1)
        ? "Single panic log"
        : "Multiple panic logs (" + panicLogCount + " files)";
        
        // cache signature από ΟΛΑ
        parseAndCacheSignature(panicLogText);

        logOk("Multi panic logs imported.");
        logInfo("Total size:");
        logOk(String.valueOf(panicLogText.length()) + " chars");
        logOk("Ready for analysis.");

    } catch (Exception e) {

        panicLogLoaded = false;
        panicLogText   = null;

        logError("Panic logs import failed.");
        logInfo("Reason:");
        logWarn(safe(e.getMessage()));
    }
}

// ============================================================
// LAB 1 — PANIC LOG ANALYZER (Initial Screening)
// ============================================================
private void runPanicLogAnalyzer() {
    if (!guardPanicLog()) return;

    appendHtml("<br>");
    logLine();
    logInfo("LAB 1 — Panic Log Analyzer");
    logLine();
    logInfo("Initial screening of the panic log against known crash patterns.");
    
    IPSPanicParser.Result r = IPSPanicParser.analyze(this, panicLogText);

    if (r == null) {
        logWarn("No known panic signature matched.");

        logInfo("What this means:");
        logOk("The log is valid, but it does not match a predefined crash pattern.");

        logInfo("Why this matters:");
        logOk("Some crashes require behavioral analysis rather than signature matching.");

        logInfo("Next step:");
        logOk("The next lab will interpret crash behavior beyond fixed signatures.");

        logInfo("File analyzed:");
        logOk(safe(panicLogName));

        appendHtml("<br>");
        logOk("Lab 1 finished.");
        logLine();
        return;
    }

    logOk("Panic signature matched.");

    logInfo("Pattern ID:");
    logOk(safe(r.patternId));

    logInfo("Domain (hint):");
    logWarn(safe(r.domain));

    logInfo("Reported Cause:");
    logOk(safe(r.cause));

    logInfo("Severity:");
    if ("High".equalsIgnoreCase(r.severity)) {
        logError(safe(r.severity));
    } else if ("Medium".equalsIgnoreCase(r.severity)) {
        logWarn(safe(r.severity));
    } else {
        logOk(safe(r.severity));
    }

    logInfo("Confidence:");
    if ("High".equalsIgnoreCase(r.confidence)) {
        logOk(safe(r.confidence));
    } else if ("Medium".equalsIgnoreCase(r.confidence)) {
        logWarn(safe(r.confidence));
    } else {
        logInfo(safe(r.confidence));
    }

    logInfo("Initial Recommendation:");
    logOk(safe(r.recommendation));

    logInfo("Next step:");
    logOk("The extracted signature will be interpreted in detail in the next LAB 2.");

    appendHtml("<br>");
    logOk("Lab 1 finished.");
    logLine();
}


// ============================================================
// LAB 2 — PANIC SIGNATURE PARSER (Behavior Interpretation)
// ============================================================
private void runPanicSignatureParser() {
    if (!guardPanicLog()) return;

    appendHtml("<br>");
    logLine();
    logInfo("LAB 2 — Panic Signature Parser");
    logLine();
    logInfo("Interpreting crash behavior using contextual evidence.");
    
    parseAndCacheSignature(panicLogText);

    logInfo("File:");
    logOk(safe(panicLogName));

    logInfo("Crash Type:");
    if ("Kernel Panic".equalsIgnoreCase(sigCrashType)
            || "Watchdog / Hang".equalsIgnoreCase(sigCrashType)) {
        logError(safe(sigCrashType));
        logWarn("This represents a serious system-level interruption.");
    } else {
        logOk(safe(sigCrashType));
    }

    logInfo("Subsystem Hint:");
    logWarn(safe(sigDomain));
    logInfo("This indicates a possible subsystem involved, not a confirmed fault.");

    logInfo("Confidence Level:");
    if ("High".equalsIgnoreCase(sigConfidence)) {
        logOk(safe(sigConfidence));
    } else if ("Medium".equalsIgnoreCase(sigConfidence)) {
        logWarn(safe(sigConfidence));
    } else {
        logInfo(safe(sigConfidence));
    }

    if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {
        logInfo("Key Evidence Found:");
        logOk(safe(sigKeyEvidence));
    }

    logInfo("Next step:");
    logOk("The extracted signature will be interpreted in detail in the next LAB 3.");

    appendHtml("<br>");
    logOk("Lab 2 finished.");
    logLine();
}


// ============================================================
// LAB 3 — SYSTEM STABILITY EVALUATION
// ============================================================
private void runStabilityLab() {
    if (!guardPanicLog()) return;
    
    appendHtml("<br>");
    logLine();
    logInfo("LAB 3 — System Stability Evaluation");
    logLine();
    
    logInfo("Assessing whether the crash indicates broader system instability.");
    
    parseAndCacheSignature(panicLogText);

    if ("High".equalsIgnoreCase(sigConfidence)
            && ("Kernel Panic".equalsIgnoreCase(sigCrashType)
            || "Watchdog / Hang".equalsIgnoreCase(sigCrashType))) {

        logError("High system instability indicators detected.");
        logWarn("Such crashes are often associated with reboots or freezes.");

        logInfo("In simple terms:");
        logOk("The device was unable to maintain stable operation under certain conditions.");

    } else if ("Medium".equalsIgnoreCase(sigConfidence)) {

        logWarn("Moderate stability risk detected.");
        logInfo("The system may become unstable under specific scenarios.");

    } else {

        logOk("No strong indicators of ongoing system instability found.");
    }

    logInfo("Crash Type:");
    logOk(safe(sigCrashType));

    logInfo("Confidence Level:");
    logOk(safe(sigConfidence));

    logInfo("Next step:");
    logOk("The following LAB 4 analyzes which hardware area is most likely involved.");

    appendHtml("<br>");
    logOk("Lab 3 finished.");
    logLine();
}


// ============================================================
// LAB 4 — IMPACT ANALYSIS
// ============================================================
private void runImpactLab() {
    if (!guardPanicLog()) return;
    
    appendHtml("<br>");
    logLine();
    logInfo("LAB 4 — Impact Analysis");
    logLine();
    logInfo("Evaluating which hardware or system areas may be affected.");

    parseAndCacheSignature(panicLogText);

    logInfo("Crash Type:");
    logOk(safe(sigCrashType));

    logInfo("Suspected Domain:");
    logWarn(safe(sigDomain));

    if ("Power / PMIC".equals(sigDomain)
            || "Storage / NAND / FS".equals(sigDomain)
            || "Baseband / Cellular".equals(sigDomain)) {

        logInfo("Important clarification:");
        logOk("This does not confirm a faulty component.");

        logError("A critical hardware-related path is suggested.");
        logWarn("If crashes repeat, professional inspection is advised.");

    } else if ("Thermal / Cooling".equals(sigDomain)
            || "Memory / OS Pressure".equals(sigDomain)) {

        logWarn("System stress-related impact suggested.");
        logInfo("Often linked to heat, load, or prolonged usage.");

    } else {

        logOk("No clear hardware impact identified from this log alone.");
    }

    if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {
        logInfo("Supporting Evidence:");
        logOk(safe(sigKeyEvidence));
    }

    logInfo("Next step:");
    logOk("A final service-level recommendation will be provided at LAB 5.");

    appendHtml("<br>");
    logOk("Lab 4 finished.");
    logLine();
}

// ============================================================
// LAB 5 — SERVICE RECOMMENDATION (Final Verdict)
// ============================================================
private void runServiceRecommendationLab() {
    if (!guardPanicLog()) return;
    
    appendHtml("<br>");
    logLine();
    logInfo("LAB 5 — Service Recommendation");
    logLine();
    logInfo("Final technical summary based on available panic log data.");
    
    parseAndCacheSignature(panicLogText);

    // ------------------------------------------------------------
    // HUMAN VERDICT (NO FEAR, NO LIES)
    // ------------------------------------------------------------
    if ("High".equalsIgnoreCase(sigConfidence)
            && ("Kernel Panic".equalsIgnoreCase(sigCrashType)
            || "Watchdog / Hang".equalsIgnoreCase(sigCrashType))) {

        logWarn("Important notice:");
        logOk("The log shows a recurring critical system crash pattern.");

        logWarn("What this usually indicates:");
        logOk("Such crashes are commonly linked to power instability, system protection triggers, or hardware stress.");

        logWarn("Service recommendation:");
        logError("Professional inspection is recommended if the issue repeats.");

    } else if ("Medium".equalsIgnoreCase(sigConfidence)) {

        logWarn("Observed condition:");
        logOk("The log indicates instability under certain conditions.");

        logWarn("Recommended action:");
        logOk("Monitoring is advised. Collect additional logs if symptoms continue.");

    } else {

        logOk("Result summary:");
        logOk("No critical fault is indicated by this panic log alone.");

        logInfo("What this means:");
        logOk("If this was a one-time event, no immediate service action is required.");
    }

    // ------------------------------------------------------------
    // TECHNICIAN SUMMARY (PDF-FRIENDLY)
    // ------------------------------------------------------------
    logLine();
    logInfo("Technical summary:");

    logInfo("Crash type:");
    logOk(safe(sigCrashType));

    logInfo("Domain indication:");
    logWarn(safe(sigDomain));

    logInfo("Confidence level:");
    logOk(safe(sigConfidence));

    // ------------------------------------------------------------
    // FINAL NOTE (VERY IMPORTANT)
    // ------------------------------------------------------------
    logLine();
    logInfo("Final note:");
    logOk("A panic log represents a snapshot in time, not a full diagnosis.");
    logOk("Conclusions should be correlated with device history and user symptoms.");

    appendHtml("<br>");
    logOk("Lab 5 finished.");
    logLine();
}

    // ============================================================
    // GUARD
    // ============================================================
    private boolean guardPanicLog() {
        if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
            toast("Load panic log first.");
            logWarn("Load Panic Log first.");
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
        bis.close();

        byte[] bytes = bos.toByteArray();
        String s = new String(bytes, Charset.forName("UTF-8"));
        if (looksGarbled(s)) s = new String(bytes, Charset.forName("ISO-8859-1"));
        return s;
    }

    private String readPanicFromZip(InputStream is) throws Exception {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry entry;
        int scanned = 0;

        while ((entry = zis.getNextEntry()) != null && scanned < ZIP_SCAN_CAP) {
            scanned++;

            String name = (entry.getName() != null) ? entry.getName().toLowerCase(Locale.US) : "";

            boolean candidate =
                    name.contains("panic") ||
                    name.endsWith(".ips") ||
                    name.endsWith(".log") ||
                    name.endsWith(".txt");

            if (candidate) {
                String text = readTextStream(zis);
                zis.close();
                if (text != null && !text.trim().isEmpty()) return text;
            }
        }

        zis.close();
        throw new Exception("No readable panic entry found in ZIP");
    }

    // ============================================================
    // SIGNATURE PARSER (service-grade, no lies)
    // ============================================================
    private String sigCrashType    = "Unknown";
    private String sigDomain       = "Unknown";
    private String sigConfidence   = "Low";
    private String sigKeyEvidence  = "";

    private void parseAndCacheSignature(String text) {
        sigCrashType   = "Unknown";
        sigDomain      = "Unknown";
        sigConfidence  = "Low";
        sigKeyEvidence = "";

        if (text == null) return;

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
        if (requiresPanicLog && (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty())) {
            toast("Load panic log first.");
            logWarn("Load Panic Log first.");
            return;
        }
        if (realClick != null) realClick.onClick(v);
    });

    return container;
}

// ============================================================
// LOGGING — GEL CANONICAL (UI + SERVICE REPORT)
// ============================================================
private void appendHtml(String htmlLine) {
    if (txtLog == null) return;

    logHtmlBuffer.append(htmlLine).append("<br>");

    try {
        txtLog.setText(
            Html.fromHtml(
                logHtmlBuffer.toString(),
                Html.FROM_HTML_MODE_LEGACY
            )
        );
    } catch (Throwable t) {
        txtLog.setText(logHtmlBuffer.toString());
    }
}

private void logInfo(String msg) {
    String s = "ℹ️ " + safe(msg);
    appendHtml(s);
    GELServiceLog.logInfo(msg);
}

private void logOk(String msg) {
    String s = "✔ " + safe(msg);
    appendHtml("<font color='#39FF14'>" + s + "</font>");
    GELServiceLog.logOk(msg);
}

private void logWarn(String msg) {
    String s = "⚠ " + safe(msg);
    appendHtml("<font color='#FFD966'>" + s + "</font>");
    GELServiceLog.logWarn(msg);
}

private void logError(String msg) {
    String s = "✖ " + safe(msg);
    appendHtml("<font color='#FF5555'>" + s + "</font>");
    GELServiceLog.logError(msg);
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
