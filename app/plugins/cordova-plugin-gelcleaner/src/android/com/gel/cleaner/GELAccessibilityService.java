package com.gel.cleaner;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class GELAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Handle accessibility events (future work)
    }

    @Override
    public void onInterrupt() {
        // Required override
    }
}
