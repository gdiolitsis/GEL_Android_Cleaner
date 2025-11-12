package com.gel.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Auto-Cache-Cleaner (1Tap mode)
 * - Open Storage
 * - Clear Cache
 * - Confirm (OK)
 * - Next target
 *
 * Fully safe + state machine
 */
public class GELAccessibilityService extends AccessibilityService {

    private enum Step { OPENED_APP_INFO, OPENED_STORAGE, CLEARED, DONE }
    private Step step = Step.OPENED_APP_INFO;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        String target = AutoCleanManager.peek();
        if (target == null) return;

        // UI settle delay
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(this::tick, 140);
    }

    private void tick() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        switch (step) {
            case OPENED_APP_INFO:
                if (clickAny(root, AccessibilityKeys.STORAGE)) {
                    step = Step.OPENED_STORAGE;
                    return;
                }
                if (hasAny(root, AccessibilityKeys.CLEAR_CACHE)) {
                    step = Step.OPENED_STORAGE;
                }
                break;

            case OPENED_STORAGE:
                if (clickAny(root, AccessibilityKeys.CLEAR_CACHE)) {
                    step = Step.CLEARED;
                    return;
                }
                break;

            case CLEARED:
                if (clickAny(root, AccessibilityKeys.OK)) {
                    step = Step.DONE;
                    finishTarget();
                    return;
                }
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
        step = Step.OPENED_APP_INFO;
        AutoCleanManager.onTargetFinished(getApplicationContext());
    }

    /* =====================================================
       SEARCH HELPERS
     ===================================================== */

    private boolean clickAny(AccessibilityNodeInfo root, String[] keys) {
        if (root == null) return false;
        for (String k : keys) {
            if (clickByText(root, k)) return true;
        }
        return false;
    }

    private boolean hasAny(AccessibilityNodeInfo root, String[] keys) {
        if (root == null) return false;
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

        String needle = text.toLowerCase();
        Deque<AccessibilityNodeInfo> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            AccessibilityNodeInfo n = stack.pop();
            if (n == null) continue;

            if (match(n.getText(), needle) ||
                    match(n.getContentDescription(), needle)) {
                return n;
            }

            for (int i = 0; i < n.getChildCount(); i++) {
                AccessibilityNodeInfo child = n.getChild(i);
                if (child != null) stack.push(child);
            }
        }
        return null;
    }

    private boolean match(CharSequence cs, String needle) {
        if (cs == null) return false;
        return cs.toString().toLowerCase().contains(needle);
    }

    @Override
    public void onInterrupt() { }
}
