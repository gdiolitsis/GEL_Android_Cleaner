package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppCacheActivity extends Activity {

    ListView listView;
    List<ResolveInfo> apps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        listView = findViewById(R.id.listApps);

        loadApps();

        AppListAdapter ad = new AppListAdapter(this, apps);
        listView.setAdapter(ad);

        listView.setOnItemClickListener((adapterView, v, position, id) -> {
            ResolveInfo r = apps.get(position);
            if (r != null && r.activityInfo != null) {
                String pkg = r.activityInfo.packageName;
                clearCache(pkg);
            }
        });
    }

    private void loadApps() {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        apps = pm.queryIntentActivities(i, 0);
    }

    private void clearCache(String packageName) {
        // TODO implement real cache clean
    }
}
