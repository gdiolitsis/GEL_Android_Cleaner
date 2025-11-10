package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    ListView listView;
    TextView title;
    List<AppEntry> items = new ArrayList<>();
    AppListAdapter adapter;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        listView = findViewById(R.id.listApps);
        title    = findViewById(R.id.txtTitle);

        title.setText(getString(R.string.app_cache_title));

        loadApps();
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_META_DATA);

        items.clear();

        for (PackageInfo info : pkgs) {

            // skip system packages
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                continue;

            String name = info.applicationInfo.loadLabel(pm).toString();
            String pkg  = info.packageName;

            long size = 0;   // δεν επιτρέπεται πλέον να διαβάσουμε exact cache size
                             // το αφήνουμε "0" εμφανιστικά

            items.add(new AppEntry(name, pkg, size));
        }

        adapter = new AppListAdapter(this, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((p, v, i, id) -> {
            AppEntry e = items.get(i);
            openAppSettings(e.pkg);
        });
    }

    private void openAppSettings(String pkg) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkg));
        startActivity(intent);
    }
}
