package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppCacheActivity extends Activity {

    ListView list;
    List<AppListActivity.AppInfo> apps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_app_cache);

        list = findViewById(R.id.list);

        loadApps();
        AppListAdapter ad = new AppListAdapter(this, apps);
        list.setAdapter(ad);
    }

    void loadApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> installed = pm.getInstalledApplications(0);

        for (ApplicationInfo ai : installed) {
            String label = ai.loadLabel(pm).toString();
            apps.add(new AppListActivity.AppInfo(
                    ai.packageName,
                    ai,
                    label
            ));
        }
    }
}
