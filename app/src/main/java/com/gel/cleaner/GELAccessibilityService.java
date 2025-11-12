package com.gel.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 1Tap-mode: Όταν ανοιχτεί το app settings screen,
 * - πατάει Storage/Χώρος αποθήκευσης
 * - πατάει Clear cache/Εκκαθάριση cache
 * - πατάει OK (αν χρειαστεί)
 * - ειδοποιεί τον AutoCleanManager να ανοίξει το επόμενο.
 */
public class GELAccessibilityService extends AccessibilityService {

    // απλό state για κάθε στόχο
    private enum Step {OPENED_APP_INFO, OPENED_STORAGE, CLEARED, DONE}
    private Step step = Step.OPENED_APP_INFO;

    // μικρό throttle για επεξεργασία
    private final Handler h = new Handler(Looper.getMainLooper());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        // Τρέχων στόχος (package)
        String target = AutoCleanManager.peek();
        if (target == null) return;

        // Μικρό delay για να έχει “δέσει” το UI
        h.removeCallbacksAndMessages(null);
        h.postDelayed(this::tick, 120);
    }

    private void tick() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        switch (step) {
            case OPENED_APP_INFO:
                // Πήγαινε στο "Storage"
                if (clickAny(root, AccessibilityKeys.STORAGE)) {
                    step = Step.OPENED_STORAGE;
                    return;
                }
                // Αν ήδη είμαστε στο storage screen, προχώρα
                if (hasAny(root, AccessibilityKeys.CLEAR_CACHE)) {
                    step = Step.OPENED_STORAGE;
                }
                break;

            case OPENED_STORAGE:
                // Πάτησε "Clear cache"
                if (clickAny(root, AccessibilityKeys.CLEAR_CACHE)) {
                    step = Step.CLEARED;
                    return;
                }
                break;

            case CLEARED:
                // Επιβεβαίωση, αν υπάρχει
                if (clickAny(root, AccessibilityKeys.OK)) {
                    step = Step.DONE;
                    finishTarget();
                    return;
                }
                // Πολλά settings δεν ζητούν OK — θεωρούμε done αν δεν υπάρχει OK
                if (!hasAny(root, AccessibilityKeys.OK)) {
                    step = Step.DONE;
                    finishTarget();
                }
                break;

            case DONE:
                finishTarget();
                break;
        }
    }

    private void finishTarget() {
        // Reset για τον επόμενο
        step = Step.OPENED_APP_INFO;
        // Πές στον manager να ανοίξει το επόμενο
        AutoCleanManager.onTargetFinished(getApplicationContext());
    }

    private boolean clickAny(AccessibilityNodeInfo root, String[] keys) {
        for (String k : keys) {
            if (clickByText(root, k)) return true;
        }
        return false;
    }

    private boolean hasAny(AccessibilityNodeInfo root, String[] keys) {
        for (String k : keys) {
            if (findByText(root, k) != null) return true;
        }
        return false;
    }

    private boolean clickByText(AccessibilityNodeInfo root, String text) {
        AccessibilityNodeInfo node = findByText(root, text);
        if (node == null) return false;

        AccessibilityNodeInfo target = node;
        for (int i = 0; i < 4 && target != null && !target.isClickable(); i++) {
            target = target.getParent();
        }
        if (target != null && target.isClickable()) {
            return target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        return false;
    }

    private AccessibilityNodeInfo findByText(AccessibilityNodeInfo root, String text) {
        if (root == null || text == null) return null;
        Deque<AccessibilityNodeInfo> stack = new ArrayDeque<>();
        stack.push(root);
        String needle = text.toLowerCase();

        while (!stack.isEmpty()) {
            AccessibilityNodeInfo n = stack.pop();
            if (n == null) continue;

            CharSequence t = n.getText();
            CharSequence d = n.getContentDescription();

            if (match(t, needle) || match(d, needle)) {
                return n;
            }
            for (int i = 0; i < n.getChildCount(); i++) stack.push(n.getChild(i));
        }
        return null;
    }

    private boolean match(CharSequence cs, String needleLower) {
        if (cs == null) return false;
        return cs.toString().toLowerCase().contains(needleLower);
    }

    @Override
    public void onInterrupt() { }
}
