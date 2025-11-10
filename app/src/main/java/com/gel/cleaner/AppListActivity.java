package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends Activity {

    ListView listView;
    List<AppEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        listView = findViewById(R.id.listApps);

        loadApps();
        AppListAdapter adapter = new AppListAdapter(this, entries);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppEntry e = entries.get(position);
            GELCleaner.clearAppCache(this, e.packageName);
        });
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        for (ApplicationInfo a : apps) {
            if ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                entries.add(new AppEntry(
                        a.loadLabel(pm).toString(),
                        a.packageName,
                        a.loadIcon(pm)
                ));
            }
        }
    }
}
