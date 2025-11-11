package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends Activity {

    ListView list;
    List<AppInfo> apps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_app_list);

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
            apps.add(new AppInfo(
                    ai.packageName,
                    ai,
                    label
            ));
        }
    }

    public static class AppInfo {
        public String packageName;
        public ApplicationInfo resolveInfo;
        public String label;

        public AppInfo(String pkg, ApplicationInfo info, String label) {
            this.packageName = pkg;
            this.resolveInfo = info;
            this.label = label;
        }
    }
}
