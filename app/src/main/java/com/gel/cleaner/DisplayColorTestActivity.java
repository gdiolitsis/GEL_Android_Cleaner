package com.gel.cleaner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private final String[] colorNames = {
            "Black",
            "White",
            "Red",
            "Green",
            "Blue",
            "Gray"
    };

    private FrameLayout root;
    private final Handler h = new Handler(Looper.getMainLooper());
    private boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new FrameLayout(this);
        root.setBackgroundColor(colors[0]);
        setContentView(root);

        startAutoCycle();
    }

    // ============================================================
    // AUTO COLOR CYCLE
    // ============================================================
    private void startAutoCycle() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finished) return;

                colorIndex++;

                if (colorIndex >= colors.length) {
                    finished = true;
                    showUserEvaluationDialog();
                    return;
                }

                root.setBackgroundColor(colors[colorIndex]);
                h.postDelayed(this, 3000); // 3 seconds per color
            }
        }, 3000);
    }

    // ============================================================
    // USER DECLARATION (NO AUTO ANALYSIS)
    // ============================================================
    private void showUserEvaluationDialog() {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(40, 40, 40, 40);
        box.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("Display Color & Uniformity");
        title.setTextSize(18f);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 20);
        title.setGravity(Gravity.CENTER);
        box.addView(title);

        TextView msg = new TextView(this);
        msg.setText(
                "Did you notice visible spots, shadows,\n" +
                "uneven brightness, or color stains\n" +
                "during the test?"
        );
        msg.setTextSize(15f);
        msg.setTextColor(Color.DKGRAY);
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, 0, 0, 30);
        box.addView(msg);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        Button noBtn = new Button(this);
        noBtn.setText("NO — Screen looks uniform");
        row.addView(noBtn);

        Button yesBtn = new Button(this);
        yesBtn.setText("YES — Visible spots / uneven areas");
        row.addView(yesBtn);

        box.addView(row);

        b.setView(box);
        b.setCancelable(false);

        AlertDialog d = b.create();
        d.show();

        noBtn.setOnClickListener(v -> {
            setResult(RESULT_OK);
            d.dismiss();
            finish();
        });

        yesBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            d.dismiss();
            finish();
        });
    }

    // ============================================================
    // BLOCK BACK — TEST MUST COMPLETE
    // ============================================================
    @Override
    public void onBackPressed() {
        // ignore back
    }
}
