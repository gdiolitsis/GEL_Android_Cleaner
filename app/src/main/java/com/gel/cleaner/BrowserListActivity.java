// GDiolitsis Engine Lab (GEL) — Author & Developer
// FINAL — BrowserListActivity (GEL Auto-Scaling + Foldable Orchestrator)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BrowserListActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private LinearLayout listRoot;

    // Foldable Engine
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    // ============================================================
    // INTERNAL MODEL
    // ============================================================
    private static class BrowserItem {
        final String pkg;
        final String label;

        BrowserItem(String p, String l) {
            pkg = p;
            label = l;
        }
    }

    // ============================================================
    // ON CREATE
    // ============================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_list);

        // 1) FOLDABLE ENGINE INIT
        uiManager = new GELFoldableUIManager(this);
        animPack = new GELFoldableAnimationPack(this);
        foldDetector = new GELFoldableDetector(this, this);
        dualPane = new DualPaneManager(this);

        // 2) NORMAL UI
        listRoot = findViewById(R.id.browserListRoot);

        List<BrowserItem> installed = getInstalledBrowsers();

        if (installed.isEmpty()) {
            addFallbackText("❌ No browsers found on this device.");
            return;
        }

        for (BrowserItem b : installed) {
            addBrowserRow(b);
        }

        // Apply dual-pane layout if device is unfolded
        uiManager.applyUI(isLarge());
    }

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
        // Reserved hook for future animations
        animPack.applyHingeMotion(listRoot, posture);
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        // inner = unfolded tablet mode
        uiManager.applyUI(isInner);
        dualPane.reflow(listRoot, isInner);
    }

    // ============================================================
    // DETECT INSTALLED BROWSERS
    // ============================================================
    private List<BrowserItem> getInstalledBrowsers() {
        PackageManager pm = getPackageManager();
        List<BrowserItem> out = new ArrayList<>();

        BrowserItem[] all = {
                new BrowserItem("com.android.chrome", "Google Chrome"),
                new BrowserItem("com.chrome.beta", "Chrome Beta"),
                new BrowserItem("org.mozilla.firefox", "Firefox"),
                new BrowserItem("com.brave.browser", "Brave"),
                new BrowserItem("com.microsoft.emmx", "Microsoft Edge"),
                new BrowserItem("com.opera.browser", "Opera"),
                new BrowserItem("com.vivaldi.browser", "Vivaldi"),
                new BrowserItem("com.duckduckgo.mobile.android", "DuckDuckGo"),
                new BrowserItem("com.sec.android.app.sbrowser", "Samsung Internet")
        };

        for (BrowserItem b : all) {
            try {
                ApplicationInfo ai = pm.getApplicationInfo(b.pkg, 0);
                if (ai != null) out.add(b);
            } catch (PackageManager.NameNotFoundException ignored) {}
        }

        return out;
    }

    // ============================================================
    // ADD A BROWSER ROW (GEL AUTO-SCALED + FOLDABLE SAFE)
    // ============================================================
    private void addBrowserRow(BrowserItem b) {

        View row = getLayoutInflater().inflate(
                R.layout.row_browser_item,
                listRoot,
                false
        );

        TextView name = row.findViewById(R.id.txtBrowserName);
        name.setText(b.label);
        name.setTextSize(sp(15f));

        // GEL padding
        int padV = dp(10);
        int padH = dp(16);
        row.setPadding(padH, padV, padH, padV);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = dp(6);
        row.setLayoutParams(lp);

        // Foldable list animation
        animPack.applyListItemFade(row);

        // CLICK → OPEN STORAGE SETTINGS
        row.setOnClickListener(v -> openBrowserSettings(b.pkg));

        // Add to container
        listRoot.addView(row);
    }

    // ============================================================
    // OPEN PACKAGE → DETAILS
    // ============================================================
    private void openBrowserSettings(String pkg) {
        try {
            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + pkg));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // FALLBACK TEXT ROW
    // ============================================================
    private void addFallbackText(String t) {
        TextView tv = new TextView(this);
        tv.setText(t);
        tv.setTextSize(sp(16f));
        tv.setPadding(dp(12), dp(12), dp(12), dp(12));
        listRoot.addView(tv);
    }
}
