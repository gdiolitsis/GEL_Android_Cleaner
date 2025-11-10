package com.gel.cleaner;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    ListView list;
    PackageManager pm;
    List<AppInfo> apps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_app_list);

        list = findViewById(R.id.appListView);
        pm   = getPackageManager();

        loadApps();
    }

    private void loadApps() {

        List<ApplicationInfo> lst = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        apps.clear();

        for (ApplicationInfo a : lst) {
            if ((a.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                continue;

            String label = pm.getApplicationLabel(a).toString();
            String pkg   = a.packageName;

            apps.add(new AppInfo(label, pkg));
        }

        AppListAdapter adapter = new AppListAdapter(this, apps);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, pos, id) -> {
            AppInfo i = apps.get(pos);
            Toast.makeText(this, "Clear: " + i.pkg, Toast.LENGTH_SHORT).show();

            GELCleaner.clearCacheForApp(this, i.pkg);
        });
    }

    static class AppInfo {
        String label;
        String pkg;

        AppInfo(String l, String p){
            label = l;
            pkg   = p;
        }
    }
}
