package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView;

import java.util.List;

public class AppListActivity extends Activity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);   // ✅ σωστό layout

        list = findViewById(R.id.listApps);            // ✅ σωστό ID

        // Fetch launcher apps
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(i, 0);

        AppListAdapter ad = new AppListAdapter(this, apps);
        list.setAdapter(ad);

        // ✅ CLICK → Open App Details → (Permission/UI→Clear cache)
        list.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            ResolveInfo info = apps.get(position);
            if (info != null && info.activityInfo != null) {
                String pkg = info.activityInfo.packageName;
                openAppDetails(pkg);
            }
        });
    }

    private void openAppDetails(String pkg) {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + pkg));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
