package com.gel.cleaner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ============================================================
// GELServiceLog — Κεντρικό “μαύρο κουτί” για Service Lab Reports
// ============================================================
public class GELServiceLog {

    private static final StringBuilder LOG = new StringBuilder();
    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    // ------------------------------------------------------------
    // INTERNAL HELPERS
    // ------------------------------------------------------------
    private static synchronized void add(String type, String msg) {
        if (msg == null) msg = "";
        String line = TS.format(new Date()) + "  " + type + "  " + sanitize(msg);
        LOG.append(line).append("\n");
    }

    private static String sanitize(String s) {
        return s.replace("\n", " ").trim();
    }

    // ------------------------------------------------------------
    // PUBLIC LOGGING API
    // ------------------------------------------------------------
    public static void info(String msg)  { add("ℹ️ INFO", msg); }
    public static void ok(String msg)    { add("✅ OK", msg); }
    public static void warn(String msg)  { add("⚠️ WARNING", msg); }
    public static void error(String msg) { add("❌ ERROR", msg); }

    // ------------------------------------------------------------
    // ADD LINE (NEEDED BY MANUAL TESTS & AUTO DIAGNOSIS)
    // ------------------------------------------------------------
    public static synchronized void addLine(String line) {
        if (line == null) line = "────────────────────────────";
        add("──", line);
    }

    // ------------------------------------------------------------
    // GET FULL REPORT
    // ------------------------------------------------------------
    public static synchronized String getAll() {
        return LOG.toString();
    }

    // ------------------------------------------------------------
    // CLEAR FOR NEXT CUSTOMER
    // ------------------------------------------------------------
    public static synchronized void clear() {
        LOG.setLength(0);
    }

    // ------------------------------------------------------------
    // CHECK IF ANYTHING EXISTS
    // ------------------------------------------------------------
    public static synchronized boolean isEmpty() {
        return LOG.length() == 0;
    }
}
