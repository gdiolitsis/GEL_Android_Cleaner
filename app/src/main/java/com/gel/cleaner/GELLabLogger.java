package com.gel.cleaner;

/**
 * Unified logger for all Manual / Auto Labs.
 *
 * - UI log  → full technical information (visible to technician)
 * - Report log (GELServiceLog) → ONLY warnings & errors (visible in PDF export)
 *
 * This keeps the service report clean while preserving full data for diagnostics.
 */
public class GELLabLogger {

    private final ManualTestsActivity ui;

    public GELLabLogger(ManualTestsActivity uiActivity) {
        this.ui = uiActivity;
    }

    // ------------------------------------------------------------
    // UI LOG (FULL DETAILS)
    // ------------------------------------------------------------
    public void uiInfo(String msg)   { ui.addLog("INFO", msg); }
    public void uiOk(String msg)     { ui.addLog("OK", msg);   }
    public void uiWarn(String msg)   { ui.addLog("WARN", msg); }
    public void uiError(String msg)  { ui.addLog("ERROR", msg); }

    // ------------------------------------------------------------
    // REPORT LOG (ONLY IMPORTANT)
    // ------------------------------------------------------------
    public void reportWarn(String msg) {
        GELServiceLog.warn(msg);  // goes to final PDF
    }

    public void reportError(String msg) {
        GELServiceLog.error(msg); // goes to final PDF
    }

    // ------------------------------------------------------------
    // SMART LOGGING
    // Only log WARNING/ERROR conditions into report log.
    // UI always logs everything.
    // ------------------------------------------------------------
    public void check(boolean condition, String okMsg, String warnMsg) {
        if (condition) {
            uiOk(okMsg);
        } else {
            uiWarn(warnMsg);
            reportWarn(warnMsg);
        }
    }

    public void fail(String msg) {
        uiError(msg);
        reportError(msg);
    }

    public void warn(String msg) {
        uiWarn(msg);
        reportWarn(msg);
    }

    public void ok(String msg) {
        uiOk(msg);
        // No report entry for OK events
    }

    public void info(String msg) {
        uiInfo(msg);
        // No report entry
    }

    // ------------------------------------------------------------
    // Manual add (UI only)
    // ------------------------------------------------------------
    public void ui(String msg) {
        uiInfo(msg);
    }
}
