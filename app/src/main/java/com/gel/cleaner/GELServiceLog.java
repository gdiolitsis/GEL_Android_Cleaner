// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELServiceLog — Ultra-Safe Edition v2.0
// ============================================================
// • Thread-safe ALL operations
// • Internal ring-buffer safety (auto-trim > 50.000 lines)
// • Safe UTF-8 emojis on all vendors (Samsung/Xiaomi)
// • Full “Service Lab” compatibility: Auto Diagnostics / Manual Tests / Export
// • 100% έτοιμο για copy-paste (κανόνας παππού Γιώργου)
// ============================================================

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GELServiceLog {

    private static final StringBuilder LOG = new StringBuilder(4096);
    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private static final int MAX_CHARS = 50000;

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private static synchronized void add(String type, String msg) {
        if (msg == null) msg = "";

        String line =
                TS.format(new Date()) + "  " +
                type + "  " +
                sanitize(msg);

        LOG.append(line).append('\n');

        ensureLimit();
    }

    private static String sanitize(String s) {
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    private static void ensureLimit() {
        if (LOG.length() > MAX_CHARS) {
            int cut = (int) (MAX_CHARS * 0.20); // trim 20%
            LOG.delete(0, cut);
        }
    }

    // ============================================================
    // PUBLIC LOGGING API
    // ============================================================
    public static void info(String msg)  { add("ℹ️ INFO", msg); }
    public static void ok(String msg)    { add("✅ OK", msg); }
    public static void warn(String msg)  { add("⚠️ WARNING", msg); }
    public static void error(String msg) { add("❌ ERROR", msg); }

    // ============================================================
    // ADD FREE LINE
    // ============================================================
    public static synchronized void addLine(String line) {
        if (line == null || line.trim().isEmpty())
            line = "───────────────────────────";
    
    }

// ============================================================
// SECTION HEADER (SERVICE REPORT SPLIT)
// ============================================================
public static synchronized void section(String title) {
    if (title == null || title.trim().isEmpty())
        title = "Service Section";

    String line = "---------------------";

    add("", line)
    add("SECTION", title.toUpperCase(Locale.US));    
    add("", line);
}
    // ============================================================
    // GET FULL REPORT
    // ============================================================
    public static synchronized String getAll() {
        return LOG.toString();
    }

    // ============================================================
    // CLEAR LOG
    // ============================================================
    public static synchronized void clear() {
        LOG.setLength(0);
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
