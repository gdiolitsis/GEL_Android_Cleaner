package com.gel.cleaner.base;

import android.view.MotionEvent;
import android.view.View;

public final class UIHelpers {

    private UIHelpers() {} // no instances

    public static void applyPressEffect(View v) {
        if (v == null) return;

        v.setOnTouchListener((x, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x.setScaleX(0.97f);
                    x.setScaleY(0.97f);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    x.setScaleX(1f);
                    x.setScaleY(1f);
                    break;
            }
            return false; // αφήνει το click κανονικά
        });
    }
}
