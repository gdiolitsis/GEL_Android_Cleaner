// GDiolitsis Engine Lab (GEL) — Author & Developer
// IPhoneLabsActivity.java — iPhone Diagnostics Labs v1.0 FINAL (LOCKED)
// Dark-Gold + Neon Green Edition — Service Grade
//
// NOTE (GEL RULE): Full file for copy-paste. No partial patches.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;

import com.gel.cleaner.iphone.IPSPanicParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IPhoneLabsActivity extends Activity {

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
    // COLORS (MATCH MANUAL TESTS)
    // ============================================================
    private static final int COLOR_BG         = 0xFF101010;
    private static final int COLOR_GREEN_MAIN = 0xFF00FF66;
    private static final int COLOR_GREEN_SUB  = 0xFF00CC55;
    private static final int COLOR_WHITE      = 0xFFFFFFFF;
    private static final int COLOR_GRAY       = 0xFFCCCCCC;

// ============================================================
// STATE (CANONICAL — ΜΙΑ ΚΑΙ ΜΟΝΑΔΙΚΗ ΑΛΗΘΕΙΑ)
// ============================================================
private boolean panicLogLoaded = false;
private String  panicLogName   = null;
private String  panicLogText   = null;
    
    // ============================================================
    // PANIC LOG ANALYZER
    // ============================================================
    private void runPanicLogAnalyzer() {
        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("📄 iPhone LAB — Panic Log Analyzer");

        if (!panicLogLoaded || panicLogText == null) {
            GELServiceLog.warn("⚠ Δεν έχει φορτωθεί panic log.");
            return;
        }

        IPSPanicParser.Result r =
                IPSPanicParser.analyze(this, panicLogText);

        if (r == null) {
            GELServiceLog.warn("⚠ No known panic signature matched.");
            return;
        }

        GELServiceLog.info("📄 Panic Signature Match");
        GELServiceLog.info("• Pattern ID: " + r.patternId);
        GELServiceLog.info("• Domain: " + r.domain);
        GELServiceLog.info("• Cause: " + r.cause);
        GELServiceLog.info("• Severity: " + r.severity);
        GELServiceLog.info("• Confidence: " + r.confidence);
        GELServiceLog.info("🧾 Recommendation: " + r.recommendation);
    }

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
    return bos.toString("UTF-8");
}

private String readPanicFromZip(InputStream is) throws Exception {
    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
    ZipEntry entry;
    int scanned = 0;

    while ((entry = zis.getNextEntry()) != null && scanned < ZIP_SCAN_CAP) {
        scanned++;

        String name = entry.getName().toLowerCase();

        if (name.contains("panic") || name.endsWith(".ips") || name.endsWith(".log")) {
            String text = readTextStream(zis);
            zis.close();
            return text;
        }
    }
    zis.close();
    throw new Exception("No panic log found in ZIP");
}

    // Parsed signature state
    private String sigCrashType    = "Unknown";
    private String sigDomain       = "Unknown";
    private String sigConfidence   = "Low";
    private String sigKeyEvidence  = "";

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
            "Εργαστηριακή διάγνωση iPhone μέσω αρχείων συστήματος\n" +
            "Ανάλυση δεδομένων service (χωρίς άμεση πρόσβαση στη συσκευή)"
    );
    sub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    sub.setTextColor(COLOR_GRAY);
    sub.setGravity(Gravity.CENTER_HORIZONTAL);
    sub.setPadding(0, dp(8), 0, dp(18));
    sub.setIncludeFontPadding(false);
    root.addView(sub);

        // ============================================================
        // LABS — FINAL SET
        // ============================================================

        // 1️⃣ PANIC LOG IMPORT (TXT / LOG / ZIP)
        root.addView(makeLabButton(
                "📦 Panic Log Import (TXT / ZIP)",
                "Αυτόματο unzip + φόρτωση panic report",
                v -> openPanicLogPicker()
        ));

        // 2️⃣ PANIC SIGNATURE PARSER
        root.addView(makeLabButton(
                "🧷 Panic Signature Parser",
                "Crash type • Domain • Confidence • Evidence",
                v -> runPanicSignatureParser()
        ));

        // 3️⃣ SYSTEM STABILITY
        root.addView(makeLabButton(
                "📊 System Stability Evaluation",
                "Αξιολόγηση σταθερότητας iOS βάσει logs",
                v -> runStabilityLab()
        ));

        // 4️⃣ IMPACT ANALYSIS
        root.addView(makeLabButton(
                "🧠 Impact Analysis",
                "Συσχέτιση σφάλματος με hardware domain",
                v -> runImpactLab()
        ));

        // 5️⃣ SERVICE RECOMMENDATION
        root.addView(makeLabButton(
                "🧾 Service Recommendation",
                "Τελικό service verdict",
                v -> runServiceRecommendationLab()
        ));

        scroll.addView(root);
        setContentView(scroll);
    }

    // ============================================================
    // PANIC LOG IMPORT (SAF)
    // ============================================================

    private void openPanicLogPicker() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                "text/plain",
                "application/zip",
                "application/octet-stream"
        });

        startActivityForResult(i, REQ_PANIC_LOG);

        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("📦 iPhone LAB — Panic Log Import requested");
    }

    @Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode != REQ_PANIC_LOG) return;

    if (resultCode != RESULT_OK || data == null || data.getData() == null) {
        GELServiceLog.warn("⚠ Panic log import cancelled.");
        return;
    }

    Uri uri = data.getData();

    try {
        String name = uri.getLastPathSegment();
        panicLogName = name;

        InputStream is = getContentResolver().openInputStream(uri);
        if (is == null) throw new Exception("InputStream null");

        if (name != null && name.toLowerCase().endsWith(".zip")) {
            panicLogText = readPanicFromZip(is);
        } else {
            panicLogText = readTextStream(is);
        }

        if (panicLogText == null || panicLogText.trim().isEmpty()) {
            throw new Exception("Empty panic log");
        }

        panicLogLoaded = true;

        GELServiceLog.info("────────────────────────────────");
        GELServiceLog.info("📂 iPhone LAB — Panic Log Imported");
        GELServiceLog.info("• File: " + panicLogName);
        GELServiceLog.ok("✔ Panic log ready for analysis.");

    } catch (Exception e) {
        panicLogLoaded = false;
        panicLogText   = null;

        GELServiceLog.warn("❌ Panic log import failed: " + e.getMessage());
    }
}

    private void loadPanicFromUri(Uri uri) throws Exception {
    ContentResolver cr = getContentResolver();
    String name = (uri != null) ? String.valueOf(uri.getLastPathSegment()) : "unknown";

    GELServiceLog.info("────────────────────────────────");
    GELServiceLog.info("📦 iPhone LAB — Loading file");
    GELServiceLog.info("• Source: SAF document");
    GELServiceLog.info("• Name: " + name);

    boolean isZip = looksLikeZip(name);

    String loadedText;
    String chosenInner = null;

    if (isZip) {
        ZipExtractResult zr = extractBestTextFromZip(cr, uri);
        loadedText = (zr != null) ? zr.text : null;
        chosenInner = (zr != null) ? zr.entryName : null;

        if (loadedText == null || loadedText.trim().isEmpty()) {
            panicLogLoaded = false;
            panicLogName = name;
            panicLogText = null;
            GELServiceLog.warn("❌ ZIP opened but no readable TXT/IPS/LOG entry found.");
            return;
        }

        panicLogLoaded = true;
        panicLogName = (chosenInner != null) ? (name + " → " + chosenInner) : name;
        panicLogText = loadedText;

        GELServiceLog.ok("✔ ZIP auto-extract OK.");
        GELServiceLog.info("• Extracted: " + (chosenInner != null ? chosenInner : "(unknown entry)"));
        GELServiceLog.info("• Size: " + panicLogText.length() + " chars");

    } else {
        loadedText = readAllTextSafely(cr, uri);

        if (loadedText == null || loadedText.trim().isEmpty()) {
            panicLogLoaded = false;
            panicLogName = name;
            panicLogText = null;
            GELServiceLog.warn("❌ File loaded but empty / unreadable.");
            return;
        }

        panicLogLoaded = true;
        panicLogName = name;
        panicLogText = loadedText;

        GELServiceLog.ok("✔ Text file loaded.");
        GELServiceLog.info("• Size: " + panicLogText.length() + " chars");
    }

    // Auto-parse signature immediately
    parseAndCacheSignature(panicLogText);

    GELServiceLog.ok("✔ Panic log ready.");
}

    // ============================================================
    // ZIP AUTO-EXTRACT (Best candidate)
    // ============================================================

    private static class ZipExtractResult {
        final String entryName;
        final String text;
        ZipExtractResult(String entryName, String text) {
            this.entryName = entryName;
            this.text = text;
        }
    }

    private ZipExtractResult extractBestTextFromZip(ContentResolver cr, Uri uri) {
        InputStream raw = null;
        ZipInputStream zis = null;

        try {
            raw = cr.openInputStream(uri);
            if (raw == null) return null;

            zis = new ZipInputStream(new BufferedInputStream(raw));

            ZipExtractResult best = null;
            int scanned = 0;

            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {

                if (e.isDirectory()) {
                    zis.closeEntry();
                    continue;
                }

                scanned++;
                if (scanned > ZIP_SCAN_CAP) break;

                String en = e.getName() != null ? e.getName() : "";
                String low = en.toLowerCase(Locale.US);

                // Candidate filters: ips/txt/log or anything containing "panic"
                boolean candidate =
                        low.endsWith(".ips") ||
                        low.endsWith(".log") ||
                        low.endsWith(".txt") ||
                        low.contains("panic");

                if (!candidate) {
                    zis.closeEntry();
                    continue;
                }

                String text = readZipEntryTextSafely(zis, MAX_TEXT_BYTES);
                zis.closeEntry();

                if (text == null || text.trim().isEmpty()) continue;

                // Score candidate (prefer .ips + contains "panic" + contains typical keys)
                int score = 0;
                if (low.endsWith(".ips")) score += 50;
                if (low.contains("panic")) score += 25;
                if (textContainsAny(text,
                        "panicString", "panic(", "bug_type", "watchdog", "panic cpu", "Kernel version")) score += 25;

                if (best == null || score > scoreOf(best.text, best.entryName)) {
                    best = new ZipExtractResult(en, text);
                }

                // Early exit if very good
                if (score >= 90) break;
            }

            return best;

        } catch (Exception ignore) {
            return null;
        } finally {
            try { if (zis != null) zis.close(); } catch (Exception ignore) {}
            try { if (raw != null) raw.close(); } catch (Exception ignore) {}
        }
    }

    private int scoreOf(String text, String entryName) {
        if (text == null) return 0;
        String en = entryName == null ? "" : entryName.toLowerCase(Locale.US);

        int score = 0;
        if (en.endsWith(".ips")) score += 50;
        if (en.contains("panic")) score += 25;
        if (textContainsAny(text,
                "panicString", "panic(", "bug_type", "watchdog", "panic cpu", "Kernel version")) score += 25;
        return score;
    }

    // ============================================================
    // PANIC SIGNATURE PARSER (service-grade, no lies)
    // ============================================================

    private void runPanicSignatureParser() {

    // GUARD — REQUIRE PANIC LOG
    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("⚠ Load Panic Log first.");
        return;
    }
    
    GELServiceLog.info("────────────────────────────────");
    GELServiceLog.info("🧷 iPhone LAB — Panic Signature Parser");

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("Panic log not loaded.");
        GELServiceLog.info("Load a panic log first.");
        return;
    }

    parseAndCacheSignature(panicLogText);

    GELServiceLog.info("File: " + (panicLogName != null ? panicLogName : "unknown"));
    GELServiceLog.info("Crash Type: " + sigCrashType);
    GELServiceLog.info("Domain: " + sigDomain);
    GELServiceLog.info("Confidence: " + sigConfidence);

    if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {
        GELServiceLog.info("Evidence: " + sigKeyEvidence);
    }

    GELServiceLog.ok("Signature extracted.");
}

    private void parseAndCacheSignature(String text) {
        // Defaults
        sigCrashType   = "Unknown";
        sigDomain      = "Unknown";
        sigConfidence  = "Low";
        sigKeyEvidence = "";

        if (text == null) return;

        String low = text.toLowerCase(Locale.US);

        // Crash type detection (simple but robust)
        boolean isWatchdog = low.contains("watchdog") || low.contains("0x8badf00d");
        boolean isKernelPanic = low.contains("panic(") || low.contains("panic cpu") || low.contains("panicstring");
        boolean isJetsam = low.contains("jetsam") || low.contains("memorystatus") || low.contains("highwater");
        boolean isThermal = low.contains("thermal") && (low.contains("shutdown") || low.contains("throttle"));
        boolean isI2C = low.contains("i2c") || low.contains("bus error");
        boolean isNand = low.contains("nand") || low.contains("apfs") || low.contains("nvme") || low.contains("storage");
        boolean isBaseband = low.contains("baseband") || low.contains("bb") || low.contains("commcenter");
        boolean isPower = low.contains("power") && (low.contains("pmu") || low.contains("brownout") || low.contains("sudden"));
        boolean isGpu = low.contains("gpu") || low.contains("agx") || low.contains("metal") || low.contains("gpus");
        boolean isSensor = low.contains("sensor") || low.contains("mic") || low.contains("camera") || low.contains("touch");

        // Choose crash type
        if (isWatchdog) sigCrashType = "Watchdog / Hang";
        else if (isJetsam) sigCrashType = "Jetsam / Memory Pressure";
        else if (isThermal) sigCrashType = "Thermal Shutdown / Throttle";
        else if (isKernelPanic) sigCrashType = "Kernel Panic";
        else sigCrashType = "Unknown / Generic";

        // Domain detection
        // (We never claim certainty. It's a domain hint for technician.)
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

        // Confidence (based on strong indicators)
        int points = 0;
        StringBuilder ev = new StringBuilder();

        if (isWatchdog) { points += 30; evAppend(ev, "watchdog"); }
        if (low.contains("panicstring")) { points += 30; evAppend(ev, "panicString"); }
        if (low.contains("bug_type")) { points += 20; evAppend(ev, "bug_type"); }
        if (low.contains("panic cpu")) { points += 20; evAppend(ev, "panic cpu"); }
        if (low.contains("0x8badf00d")) { points += 25; evAppend(ev, "0x8badf00d"); }
        if (isBaseband) { points += 20; evAppend(ev, "baseband"); }
        if (isNand) { points += 20; evAppend(ev, "storage"); }
        if (isGpu) { points += 20; evAppend(ev, "gpu/agx"); }
        if (isThermal) { points += 20; evAppend(ev, "thermal"); }
        if (isJetsam) { points += 20; evAppend(ev, "jetsam"); }

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
    // OTHER LABS (use cached signature state)
    // ============================================================

    private void runStabilityLab() {

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("⚠ Load Panic Log first.");
        return;
    }
    
    GELServiceLog.info("────────────────────────────────");
    GELServiceLog.info("📊 iPhone LAB — System Stability Evaluation");

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("No panic log available.");
        return;
    }

    if ("High".equals(sigConfidence) && "Kernel Panic".equals(sigCrashType)) {
        GELServiceLog.error("High stability risk detected (Kernel Panic).");
    } else if ("Medium".equals(sigConfidence)) {
        GELServiceLog.warn("Moderate stability risk detected.");
    } else {
        GELServiceLog.ok("No strong instability indicators detected.");
    }

    GELServiceLog.info("Crash Type: " + sigCrashType);
    GELServiceLog.info("Domain: " + sigDomain);
    GELServiceLog.info("Confidence: " + sigConfidence);
}

    private void runImpactLab() {

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("⚠ Load Panic Log first.");
        return;
    }
    
    GELServiceLog.info("────────────────────────────────");
    GELServiceLog.info("🧠 iPhone LAB — Impact Analysis");

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("No panic log available for correlation.");
        return;
    }

    GELServiceLog.info("Crash Type: " + sigCrashType);
    GELServiceLog.info("Suggested Domain: " + sigDomain);
    GELServiceLog.info("Confidence: " + sigConfidence);

    if ("Power / PMIC".equals(sigDomain) ||
        "Baseband / Cellular".equals(sigDomain)) {
        GELServiceLog.error("High-risk hardware domain suspected.");
    } else if ("Thermal / Cooling".equals(sigDomain) ||
               "Memory / OS Pressure".equals(sigDomain)) {
        GELServiceLog.warn("Potential subsystem instability detected.");
    } else {
        GELServiceLog.ok("No high-risk hardware domain identified.");
    }

    if (sigKeyEvidence != null && !sigKeyEvidence.trim().isEmpty()) {
        GELServiceLog.info("Evidence: " + sigKeyEvidence);
    }

    GELServiceLog.ok("Impact analysis completed.");
}

    private void runServiceRecommendationLab() {

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.warn("⚠ Load Panic Log first.");
        return;
    }
    
    GELServiceLog.info("────────────────────────────────");
    GELServiceLog.info("🧾 iPhone LAB — Service Recommendation");

    if (!panicLogLoaded || panicLogText == null || panicLogText.trim().isEmpty()) {
        GELServiceLog.ok("No panic log provided — no fault evidenced by logs.");
        GELServiceLog.info("Recommendation: request logs if symptoms persist.");
        return;
    }

    if ("High".equals(sigConfidence)) {
        GELServiceLog.error("Service-level inspection recommended.");
    } else if ("Medium".equals(sigConfidence)) {
        GELServiceLog.warn("Monitoring recommended. Collect additional logs.");
    } else {
        GELServiceLog.ok("No critical fault indicated by this panic log.");
    }

    GELServiceLog.info("Crash Type: " + sigCrashType);
    GELServiceLog.info("Domain: " + sigDomain);
    GELServiceLog.info("Confidence: " + sigConfidence);

    GELServiceLog.ok("Service verdict recorded.");
}

    // ============================================================
    // UI HELPER
    // ============================================================

    private View makeLabButton(
        String title,
        String subtitle,
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
    container.setBackgroundResource(R.drawable.gel_btn_gold_bordo); // ΤΟ ΕΤΟΙΜΟ drawable
    container.setClickable(true);
    container.setFocusable(true);
    container.setFocusableInTouchMode(false);

    TextView t = new TextView(this);
    t.setText(title);
    t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    t.setTextColor(0xFF00FF9C);
    t.setIncludeFontPadding(false);

    TextView s = new TextView(this);
    s.setText(subtitle);
    s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    s.setTextColor(0xFFFFFFFF);
    s.setPadding(0, dp(6), 0, 0);

    container.addView(t);
    container.addView(s);

    // ✅ GUARDED CLICK — ΕΔΩ ΤΟ ΚΛΕΙΔΙ
    container.setOnClickListener(v -> {
        if (!panicLogLoaded && !title.contains("Import")) {
            GELServiceLog.warn("⚠ Load Panic Log first.");
            return;
        }
        realClick.onClick(v);
    });

    return container;
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

    private String readAllTextSafely(ContentResolver cr, Uri uri) {
        InputStream in = null;
        ByteArrayOutputStream bos = null;

        try {
            in = cr.openInputStream(uri);
            if (in == null) return null;

            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int total = 0;

            int n;
            while ((n = in.read(buf)) > 0) {
                total += n;
                if (total > MAX_TEXT_BYTES) break;
                bos.write(buf, 0, n);
            }

            byte[] bytes = bos.toByteArray();

            // Best-effort charset: UTF-8 first, then ISO-8859-1 fallback
            String s = new String(bytes, Charset.forName("UTF-8"));
            if (looksGarbled(s)) {
                s = new String(bytes, Charset.forName("ISO-8859-1"));
            }
            return s;

        } catch (Exception e) {
            return null;
        } finally {
            try { if (bos != null) bos.close(); } catch (Exception ignore) {}
            try { if (in != null) in.close(); } catch (Exception ignore) {}
        }
    }

    private String readZipEntryTextSafely(InputStream entryStream, int capBytes) {
        if (entryStream == null) return null;
        ByteArrayOutputStream bos = null;

        try {
            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int total = 0;

            int n;
            while ((n = entryStream.read(buf)) > 0) {
                total += n;
                if (total > capBytes) break;
                bos.write(buf, 0, n);
            }

            byte[] bytes = bos.toByteArray();

            String s = new String(bytes, Charset.forName("UTF-8"));
            if (looksGarbled(s)) {
                s = new String(bytes, Charset.forName("ISO-8859-1"));
            }
            return s;

        } catch (Exception e) {
            return null;
        } finally {
            try { if (bos != null) bos.close(); } catch (Exception ignore) {}
        }
    }

    private boolean looksGarbled(String s) {
        if (s == null || s.isEmpty()) return false;
        // Heuristic: too many replacement chars suggests wrong encoding
        int bad = 0;
        int lim = Math.min(s.length(), 4000);
        for (int i = 0; i < lim; i++) {
            if (s.charAt(i) == '\uFFFD') bad++;
        }
        return bad > 10;
    }

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
