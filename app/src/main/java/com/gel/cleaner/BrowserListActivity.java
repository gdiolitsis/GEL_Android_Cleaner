package com.gel.cleaner;

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

// ============================================================
// GEL UNIVERSAL SCALING EDITION
// ============================================================
public class BrowserListActivity extends GELAutoActivityHook {

    private LinearLayout listRoot;

    private static class BrowserItem {
        String pkg;
        String label;
        BrowserItem(String p, String l) {
            pkg = p;
            label = l;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_list);

        listRoot = findViewById(R.id.browserListRoot);

        List<BrowserItem> installed = getInstalledBrowsers();

        if (installed.isEmpty()) {
            addText("❌ No browsers found on this device.");
            return;
        }

        for (BrowserItem b : installed) {
            addBrowserRow(b);
        }
    }

    // --------------------------------------------------------------------
    // FIND INSTALLED BROWSERS
    // --------------------------------------------------------------------
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

    // --------------------------------------------------------------------
    // UI ROW FOR EACH BROWSER  (GEL AUTO-SCALED)
    // --------------------------------------------------------------------
    private void addBrowserRow(BrowserItem b) {

        View row = getLayoutInflater().inflate(R.layout.row_browser_item, null);

        TextView name = row.findViewById(R.id.txtBrowserName);
        name.setText(b.label);

        // ============================
        // SCALE: text + padding + height
        // ============================
        name.setTextSize(sp(15f));

        int padV = dp(10);
        int padH = dp(16);
        row.setPadding(padH, padV, padH, padV);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = dp(6);
        row.setLayoutParams(lp);

        row.setOnClickListener(v -> openBrowserSettings(b.pkg));

        listRoot.addView(row);
    }

    // --------------------------------------------------------------------
    // OPEN STORAGE → CLEAR CACHE
    // --------------------------------------------------------------------
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

    // --------------------------------------------------------------------
    // FALLBACK TEXT ROW (Auto-scaled)
    // --------------------------------------------------------------------
    private void addText(String t) {
        TextView tv = new TextView(this);
        tv.setText(t);
        tv.setTextSize(sp(16f));
        tv.setPadding(dp(12), dp(12), dp(12), dp(12));

        listRoot.addView(tv);
    }
}
