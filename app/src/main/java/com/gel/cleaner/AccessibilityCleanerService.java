package com.gel.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Deque;

public class AccessibilityCleanerService extends AccessibilityService {

    /**
     * Multi-locale storage → preserve your core keys
     */
    private static final String[] STORAGE_KEYS = new String[]{
            "Storage & cache", "Storage", "Χώρος αποθήκευσης", "Αποθήκευση",
            "Storage settings", "Memory", "Memory usage"     // extra
    };

    /**
     * Clear cache keywords multi-locale
     */
    private static final String[] CLEAR_CACHE_KEYS = new String[]{
            "Clear cache", "Cache", "Εκκαθάριση μνήμης cache", "Μνήμη cache", "Εκκαθάριση cache",
            "Clear", "Remove cache", "Temporarily files",
            "Διαγραφή cache", "Καθαρισμός", "Διαγραφή προσωρινών αρχείων"
    };

    /**
     * Extra fallback → prevent clicks on "Clear data"
     */
    private static final String[] FORBIDDEN_KEYS = new String[]{
            "Clear data", "Διαγραφή δεδομένων"
    };

    /**
     * Dialog confirmation
     */
    private static final String[] OK_KEYS = new String[]{
            "OK", "ΕΝΤΑΞΕΙ", "ΟΚ", "Confirm", "Επιβεβαίωση"
    };


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        // 1) Localize storage → open Storage screen
        if (clickFirstMatch(root, STORAGE_KEYS)) return;

        // 2) Click "Clear Cache"
        if (clickFirstMatch(root, CLEAR_CACHE_KEYS)) return;

        // 3) If dialog appears → OK
        clickFirstMatch(root, OK_KEYS);
    }


    @Override
    public void onInterrupt() { }


    /* ================= CORE ================= */
    private boolean clickFirstMatch(AccessibilityNodeInfo root, String[] keys) {
        for (String k : keys) {
            if (clickByText(root, k)) return true;
        }
        return false;
    }


    private boolean clickByText(AccessibilityNodeInfo root, String text) {

        Deque<AccessibilityNodeInfo> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {

            AccessibilityNodeInfo n = stack.pop();
            if (n == null) continue;

            CharSequence c1 = safeText(n);
            CharSequence c2 = safeDesc(n);

            if (matches(c1, text) || matches(c2, text)) {

                // prevent Clear data
                if (isForbidden(c1) || isForbidden(c2)) {
                    continue;
                }

                AccessibilityNodeInfo target = n;

                // go up parents if needed
                for (int i = 0; i < 4 && target != null && !target.isClickable(); i++) {
                    target = target.getParent();
                }

                if (target != null && target.isClickable()) {
                    target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }

            for (int i = 0; i < n.getChildCount(); i++) {
                stack.push(n.getChild(i));
            }
        }

        return false;
    }


    /* ================= HELPER ================= */
    private boolean matches(CharSequence hay, String needle) {
        if (hay == null || needle == null) return false;
        return hay.toString().toLowerCase().contains(needle.toLowerCase());
    }

    private boolean isForbidden(CharSequence txt) {
        if (txt == null) return false;
        String s = txt.toString().toLowerCase();
        for (String f : FORBIDDEN_KEYS) {
            if (s.contains(f.toLowerCase())) return true;
        }
        return false;
    }

    private CharSequence safeText(AccessibilityNodeInfo n) {
        try { return n.getText(); }
        catch (Exception e) { return null; }
    }

    private CharSequence safeDesc(AccessibilityNodeInfo n) {
        try { return n.getContentDescription(); }
        catch (Exception e) { return null; }
    }

}
