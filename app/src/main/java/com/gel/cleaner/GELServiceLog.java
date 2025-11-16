package com.gel.cleaner;

// ============================================================
// GELServiceLog
// Κεντρικό "μαύρο κουτί" για Service Lab / Reports
// ============================================================
public class GELServiceLog {

    private static final StringBuilder LOG = new StringBuilder();

    // Προσθήκη απλής γραμμής
    public static synchronized void addLine(String line) {
        if (line == null) return;
        LOG.append(line).append("\n");
    }

    // Βοηθητικά με icons (όπως στο UI)
    public static void info(String msg)  { addLine("ℹ️ " + msg); }
    public static void ok(String msg)    { addLine("✅ " + msg); }
    public static void warn(String msg)  { addLine("⚠️ " + msg); }
    public static void error(String msg) { addLine("❌ " + msg); }

    // Παίρνουμε όλο το log (για export)
    public static synchronized String getAll() {
        return LOG.toString();
    }

    // Καθάρισμα log για επόμενο πελάτη
    public static synchronized void clear() {
        LOG.setLength(0);
    }

    // Γρήγορος έλεγχος αν έχουμε δεδομένα
    public static synchronized boolean isEmpty() {
        return LOG.length() == 0;
    }
}
