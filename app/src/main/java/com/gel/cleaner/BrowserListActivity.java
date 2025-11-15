package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BrowserListActivity extends AppCompatActivity {

    private LinearLayout listRoot;

    private static class BrowserItem {
        String pkg;
        String label;
        int icon;
        BrowserItem(String p, String l, int i) {
            pkg = p; label = l; icon = i;
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

                new BrowserItem("com.android.chrome", "Google Chrome", R.drawable.ic_browser_chrome),
                new BrowserItem("com.chrome.beta", "Chrome Beta", R.drawable.ic_browser_chrome),
                new BrowserItem("org.mozilla.firefox", "Firefox", R.drawable.ic_browser_firefox),
                new BrowserItem("com.brave.browser", "Brave", R.drawable.ic_browser_brave),
                new BrowserItem("com.microsoft.emmx", "Microsoft Edge", R.drawable.ic_browser_edge),
                new BrowserItem("com.opera.browser", "Opera", R.drawable.ic_browser_opera),
                new BrowserItem("com.vivaldi.browser", "Vivaldi", R.drawable.ic_browser_vivaldi),
                new BrowserItem("com.duckduckgo.mobile.android", "DuckDuckGo", R.drawable.ic_browser_ddg),
                new BrowserItem("com.sec.android.app.sbrowser", "Samsung Internet", R.drawable.ic_browser_samsung)
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
    // UI ROW FOR EACH BROWSER
    // --------------------------------------------------------------------
    private void addBrowserRow(BrowserItem b) {

        View row = getLayoutInflater().inflate(R.layout.row_browser_item, null);

        ImageView icon = row.findViewById(R.id.iconBrowser);
        TextView name  = row.findViewById(R.id.txtBrowserName);

        icon.setImageResource(b.icon);
        name.setText(b.label);

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
    // FALLBACK TEXT ROW
    // --------------------------------------------------------------------
    private void addText(String t) {
        TextView tv = new TextView(this);
        tv.setText(t);
        tv.setTextSize(16);
        listRoot.addView(tv);
    }
}
