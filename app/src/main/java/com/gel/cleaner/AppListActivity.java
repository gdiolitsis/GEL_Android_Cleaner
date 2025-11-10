package com.gel.cleaner;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    ListView list;
    List<AppInfo> data = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        list = findViewById(R.id.listApps);

        loadApps();
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        data.clear();

        for (ApplicationInfo a : apps) {
            if (pm.getLaunchIntentForPackage(a.packageName) != null) {
                data.add(new AppInfo(
                        a.loadLabel(pm).toString(),
                        a.packageName,
                        a.loadIcon(pm)
                ));
            }
        }

        AppListAdapter ad = new AppListAdapter(this, data);
        list.setAdapter(ad);
    }

    public static class AppInfo {
        public String label;
        public String pkg;
        public Object icon;

        public AppInfo(String label, String pkg, Object icon) {
            this.label = label;
            this.pkg = pkg;
            this.icon = icon;
        }
    }
}
