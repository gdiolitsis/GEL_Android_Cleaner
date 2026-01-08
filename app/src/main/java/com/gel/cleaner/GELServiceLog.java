// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELServiceLog — Ultra-Safe Edition v2.2 (HTML + TEXT)
// ============================================================
// • Thread-safe ALL operations
// • Internal ring-buffer safety (auto-trim > 50.000 chars)
// • UTF-safe emojis
// • Dual log: PLAIN (backward compat) + HTML (styled export)
// • NO labels (INFO / OK / WARNING / ERROR) — μόνο σύμβολα
// • Visual separation between LABS (γραμμή + κενή γραμμή)
// • 100% έτοιμο για copy-paste
// ============================================================

package com.gel.cleaner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GELServiceLog {

    // ----------------------------
    // BUFFERS
    // ----------------------------
    private static final StringBuilder LOG  = new StringBuilder(4096);   // plain text
    private static final StringBuilder HTML = new StringBuilder(4096);   // styled html

    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static final int MAX_CHARS = 50000;

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private static synchronized void add(String symbol, String msg) {
        if (msg == null) msg = "";

        String ts = TS.format(new Date());

        String line =
                ts + "  " +
                (symbol == null ? "" : symbol) + " " +
                sanitize(msg);

        LOG.append(line).append('\n');
        ensureLimit();
    }

    private static synchronized void addHtml(String htmlLine) {
        if (htmlLine == null) return;
        HTML.append(htmlLine).append("<br>");
        ensureLimit();
    }

    private static String sanitize(String s) {
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static void ensureLimit() {
        if (LOG.length() > MAX_CHARS) {
            int cut = (int) (MAX_CHARS * 0.20);
            LOG.delete(0, cut);
        }
        if (HTML.length() > MAX_CHARS) {
            int cut = (int) (MAX_CHARS * 0.20);
            HTML.delete(0, cut);
        }
    }

    // ============================================================
    // PUBLIC LOGGING API (PLAIN + HTML)
    // • ΜΟΝΟ σύμβολο + μήνυμα
    // ============================================================
    public static synchronized void info(String msg)  {
        add("ℹ", msg);
        addHtml("<font color='#7FC8FF'>ℹ " + escape(msg) + "</font>");
    }

    public static synchronized void ok(String msg)    {
        add("✔", msg);
        addHtml("<font color='#39FF14'>✔ " + escape(msg) + "</font>");
    }

    public static synchronized void warn(String msg)  {
        add("⚠", msg);
        addHtml("<font color='#FFD966'>⚠ " + escape(msg) + "</font>");
    }

    public static synchronized void error(String msg) {
        add("✖", msg);
        addHtml("<font color='#FF5555'>✖ " + escape(msg) + "</font>");
    }

    // ============================================================
    // KV HELPERS — τιμή δίπλα στο label (ΟΧΙ από κάτω)
    // ============================================================
    public static synchronized void infoKV(String label, String value) {
        info(label + " " + value);
    }

    public static synchronized void okKV(String label, String value) {
        ok(label + " " + value);
    }

    public static synchronized void warnKV(String label, String value) {
        warn(label + " " + value);
    }

    public static synchronized void errorKV(String label, String value) {
        error(label + " " + value);
    }

    // ============================================================
    // ADD FREE LINE
    // • γραμμή + ΚΕΝΗ γραμμή για οπτικό διαχωρισμό LABS
    // ============================================================
    public static synchronized void addLine(String line) {
        if (line == null || line.trim().isEmpty())
            line = "────────────────────────────";

        // plain
        LOG.append(line).append('\n').append('\n');

        // html
        addHtml(line);
        addHtml(""); // κενή γραμμή
    }

    // ============================================================
    // SECTION HEADER (SERVICE REPORT SPLIT)
    // • ΠΑΝΩ/ΚΑΤΩ σκέτη γραμμή
    // • ΜΟΝΟ στη μέση ο τίτλος
    // • ΟΧΙ κενή γραμμή πριν τον τίτλο
    // ============================================================
    public static synchronized void section(String title) {
        if (title == null || title.trim().isEmpty())
            title = "Service Section";

        String line = "════════════════════════════";

        // plain
        LOG.append(line).append('\n');
        LOG.append(title.toUpperCase(Locale.US)).append('\n');
        LOG.append(line).append('\n').append('\n');

        // html
        addHtml(line);
        addHtml("<b>" + escape(title.toUpperCase(Locale.US)) + "</b>");
        addHtml(line);
        addHtml(""); // κενή γραμμή μετά το section
    }

    // ============================================================
    // GEL FULL REPORT
    // ============================================================
    public static synchronized String getAll() {
        return LOG.toString();
    }

    public static synchronized String getHtml() {
        return HTML.toString();
    }

    // ============================================================
    // CLEAR LOG
    // ============================================================
    public static synchronized void clear() {
        LOG.setLength(0);
        HTML.setLength(0);
    }

    // ============================================================
    // CHECK IF EMPTY
    // ============================================================
    public static synchronized boolean isEmpty() {
        return LOG.length() == 0;
    }

    // ============================================================
    // BACKWARD-COMPATIBILITY ALIASES (DO NOT REMOVE)
    // ============================================================
    public static void logInfo(String msg)  { info(msg); }
    public static void logOk(String msg)    { ok(msg); }
    public static void logWarn(String msg)  { warn(msg); }
    public static void logError(String msg) { error(msg); }
    public static void logLine()            { addLine(null); }
}
