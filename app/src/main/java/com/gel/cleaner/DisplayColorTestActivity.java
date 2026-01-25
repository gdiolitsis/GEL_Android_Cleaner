package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;

public class DisplayColorTestActivity extends Activity {

    private int colorIndex = 0;

    private final int[] colors = {
            Color.BLACK,
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.GRAY
    };

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable colorStepper = new Runnable() {
        @Override
        public void run() {
            colorIndex++;

            if (colorIndex >= colors.length) {
                setResult(RESULT_OK, new Intent());
                finish();
            } else {
                root.setBackgroundColor(colors[colorIndex]);
                handler.postDelayed(this, 2500); // 2.5s ανά χρώμα
            }
        }
    };

    private FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new FrameLayout(this);
        root.setBackgroundColor(colors[0]);
        setContentView(root);

        // start automatic color cycling
        handler.postDelayed(colorStepper, 2500);
    }

    @Override
    public void onBackPressed() {
        // ⛔ Ignore back — test must complete
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
