// GDiolitsis Engine Lab (GEL) — Author & Developer
// DiagnosisMenuActivity.java — Service Lab Menu v3.0
// GEL Universal Scaling + Foldable Ready
// FULL FILE — SAFE REPLACEMENT

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DiagnosisMenuActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private GELFoldableUIManager uiManager;
    private GELFoldableDetector foldDetector;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ============================================================
        // INIT FOLDABLE ENGINE
        // ============================================================
        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // ============================================================
        // ROOT
        // ============================================================
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);
        root.setBackgroundColor(0xFF101010);

        // ============================================================
        // TITLE
        // ============================================================
        TextView title = new TextView(this);
        title.setText(getString(R.string.gel_service_lab));
        title.setTextSize(sp(22f));
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText(getString(R.string.gel_service_lab_sub));
        sub.setTextSize(sp(14f));
        sub.setTextColor(0xFFCCCCCC);
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.setPadding(0, 0, 0, dp(16));
        root.addView(sub);

        // ============================================================
        // ANDROID — MANUAL TESTS
        // ============================================================
        root.addView(sectionLabel(getString(R.string.manual_tests)));

        View manualBtn = makeBlockButton(
                getString(R.string.manual_tests_title),
                getString(R.string.manual_tests_desc)
        );
        manualBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ManualTestsActivity.class)));
        root.addView(manualBtn);

        // ============================================================
        // iPHONE DIAGNOSIS (LABS)
        // ============================================================
        root.addView(sectionLabel(getString(R.string.auto_diagnosis)));

        View iphoneBtn = makeBlockButton(
                getString(R.string.gel_phone_diag_title),
                getString(R.string.gel_phone_diag_desc)
        );
        iphoneBtn.setOnClickListener(v ->
                startActivity(new Intent(this, IPhoneLabsActivity.class)));
        root.addView(iphoneBtn);

        // ============================================================
        // SERVICE REPORT
        // ============================================================
        root.addView(sectionLabel(getString(R.string.service_report)));

        View reportBtn = makeBlockButton(
                getString(R.string.export_report_title),
                getString(R.string.export_report_desc)
        );
        reportBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ServiceReportActivity.class)));
        root.addView(reportBtn);

        // ============================================================
        // FINALIZE
        // ============================================================
        scroll.addView(root);
        setContentView(scroll);
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================
    @Override
    protected void onResume() {
        super.onResume();
        foldDetector.start();
    }

    @Override
    protected void onPause() {
        foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        animPack.onPostureChanged(posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        uiManager.applyUI(isInner);
        try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}
    }

    // ============================================================
    // UI HELPERS
    // ============================================================
    private TextView sectionLabel(String txt) {
        TextView tv = new TextView(this);
        tv.setText(txt);
        tv.setTextSize(sp(16f));
        tv.setTextColor(0xFFEEEEEE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, dp(12), 0, dp(6));
        return tv;
    }

    private View makeBlockButton(String title, String subtitle) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dp(6), 0, dp(6));
        card.setLayoutParams(lp);

        card.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        card.setClickable(true);
        card.setFocusable(true);

        TextView t = new TextView(this);
        t.setText(title);
        t.setTextSize(sp(16f));
        t.setTextColor(0xFFFFFFFF);
        t.setGravity(Gravity.CENTER_HORIZONTAL);
        t.setPadding(0, 0, 0, dp(4));
        card.addView(t);

        TextView s = new TextView(this);
        s.setText(subtitle);
        s.setTextSize(sp(13f));
        s.setTextColor(0xFFAAAAAA);
        s.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(s);

        return card;
    }
}
