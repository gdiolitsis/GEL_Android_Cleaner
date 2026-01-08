// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELServiceLog — Ultra-Safe Edition v2.2 (FINAL FORMAT)
// ============================================================
// • Thread-safe ALL operations
// • Internal ring-buffer safety
// • UTF-safe emojis
// • Dual log: PLAIN + HTML (colored)
// ============================================================

package com.gel.cleaner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GELServiceLog {

    // ----------------------------
    // BUFFERS
    // ----------------------------
    private static final StringBuilder LOG  = new StringBuilder(4096); // plain
    private static final StringBuilder HTML = new StringBuilder(4096); // colored

    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static final int MAX_CHARS = 50000;

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private static synchronized void addPlain(String line) {
        if (line == null) return;
        LOG.append(line).append('\n');
        ensureLimit();
    }

    private static synchronized void addHtml(String htmlLine) {
        if (htmlLine == null) return;
        HTML.append(htmlLine).append("<br>");
        ensureLimit();
    }

    private static String sanitize(String s) {
        if (s == null) return "";
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
    // PUBLIC LOGGING API (SYMBOLS ONLY)
    // ============================================================
    public static synchronized void info(String msg)  {
        String m = sanitize(msg);
        addPlain("ℹ " + m);
        addHtml("<font color='#7FC8FF'>ℹ " + escape(m) + "</font>");
    }

    public static synchronized void ok(String msg)    {
        String m = sanitize(msg);
        addPlain("✔ " + m);
        addHtml("<font color='#39FF14'>✔ " + escape(m) + "</font>");
    }

    public static synchronized void warn(String msg)  {
        String m = sanitize(msg);
        addPlain("⚠ " + m);
        addHtml("<font color='#FFD966'>⚠ " + escape(m) + "</font>");
    }

    public static synchronized void error(String msg) {
        String m = sanitize(msg);
        addPlain("✖ " + m);
        addHtml("<font color='#FF5555'>✖ " + escape(m) + "</font>");
    }

    // ============================================================
    // SEPARATOR LINE
    // ============================================================
    public static synchronized void line() {
        String sep = "--------------------------------------------";
        addPlain(sep);
        addHtml(sep);
    }

    // ============================================================
    // SECTION HEADER
    // (NO empty line before title)
    // ============================================================
    public static synchronized void section(String title) {
        if (title == null || title.trim().isEmpty())
            title = "SECTION";

        String t = title.toUpperCase(Locale.US);
        String sep = "--------------------------------------------";

        // plain
        addPlain(sep);
        addPlain(t);
        addPlain(sep);

        // html
        addHtml(sep);
        addHtml("<b>" + escape(t) + "</b>");
        addHtml(sep);
    }

    // ============================================================
// LAB FINISHED BLOCK
// ============================================================
public static synchronized void labFinished(String labName) {
    if (labName == null) labName = "Lab";

    ok(labName + " finished.");
    line();

    // ➕ κενή γραμμή για οπτικό διαχωρισμό
    addPlain("");
    addHtml("");
}
    
    // ============================================================
    // FULL LOG ACCESS
    // ============================================================
    public static synchronized String getAll() {
        return LOG.toString();
    }

    public static synchronized String getHtml() {
        return HTML.toString();
    }

    // ============================================================
    // CLEAR
    // ============================================================
    public static synchronized void clear() {
        LOG.setLength(0);
        HTML.setLength(0);
    }

    // ============================================================
    // CHECK EMPTY
    // ============================================================
    public static synchronized boolean isEmpty() {
        return LOG.length() == 0;
    }

    // ============================================================
    // BACKWARD-COMPATIBILITY (DO NOT REMOVE)
    // ============================================================
    public static void logInfo(String msg)  { info(msg); }
    public static void logOk(String msg)    { ok(msg); }
    public static void logWarn(String msg)  { warn(msg); }
    public static void logError(String msg) { error(msg); }
    public static void logLine()            { line(); }
}
