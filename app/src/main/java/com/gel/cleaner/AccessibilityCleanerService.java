package com.gel.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;
import java.util.Deque;

public class AccessibilityCleanerService extends AccessibilityService {

    // Απλοϊκή μηχανή που ψάχνει κόμβους με κείμενα-στόχους και κάνει performAction(CLICK)
    private static final String[] STORAGE_KEYS = new String[]{
            "Storage & cache", "Storage", "Χώρος αποθήκευσης", "Αποθήκευση"
    };
    private static final String[] CLEAR_CACHE_KEYS = new String[]{
            "Clear cache", "Cache", "Εκκαθάριση μνήμης cache", "Μνήμη cache", "Εκκαθάριση cache"
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Κάθε φορά που αλλάζει παράθυρο/περιεχόμενο, δοκιμάζουμε να βρούμε στόχο
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        // 1) Αν βρούμε "Storage"/"Χώρος αποθήκευσης", πρώτα κλικ εκεί
        if (clickFirstMatch(root, STORAGE_KEYS)) return;

        // 2) Μετά ψάχνουμε για "Clear cache"/"Εκκαθάριση μνήμης cache"
        clickFirstMatch(root, CLEAR_CACHE_KEYS);
    }

    @Override
    public void onInterrupt() { }

    private boolean clickFirstMatch(AccessibilityNodeInfo root, String[] keys){
        for (String k : keys){
            if (clickByText(root, k)) return true;
        }
        return false;
    }

    private boolean clickByText(AccessibilityNodeInfo root, String text){
        Deque<AccessibilityNodeInfo> stack = new ArrayDeque<>();
        stack.push(root);
        while(!stack.isEmpty()){
            AccessibilityNodeInfo n = stack.pop();
            if (n == null) continue;

            CharSequence c1 = n.getText();
            CharSequence c2 = n.getContentDescription();

            if ( (c1!=null && match(c1.toString(), text)) || (c2!=null && match(c2.toString(), text)) ){
                // Προσπάθησε click στο ίδιο ή στον γονιό του που είναι clickable
                AccessibilityNodeInfo target = n;
                for(int i=0;i<4 && target!=null && !target.isClickable(); i++){
                    target = target.getParent();
                }
                if (target!=null && target.isClickable()){
                    target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }

            for (int i=0;i<n.getChildCount();i++){
                stack.push(n.getChild(i));
            }
        }
        return false;
    }

    private boolean match(String hay, String needle){
        // Χαλαρό ταίριασμα (ανεξαρτήτως πεζών/κεφαλαίων)
        if (hay == null) return false;
        return hay.toLowerCase().contains(needle.toLowerCase());
    }
}
