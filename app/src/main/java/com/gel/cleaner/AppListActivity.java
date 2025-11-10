package com.gel.cleaner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    public static class AppInfo {
        public final String label;
        public final String packageName;
        public final ResolveInfo resolveInfo;

        public AppInfo(String label, String packageName, ResolveInfo resolveInfo) {
            this.label = label;
            this.packageName = packageName;
            this.resolveInfo = resolveInfo;
        }
    }

    private PackageManager pm;
    private List<AppInfo> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        pm = getPackageManager();

        // Apps με LAUNCHER activity
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, (a, b) ->
                String.valueOf(a.loadLabel(pm)).compareToIgnoreCase(String.valueOf(b.loadLabel(pm))));

        for (ResolveInfo ri : apps) {
            String label = String.valueOf(ri.loadLabel(pm));
            String pkg   = ri.activityInfo.packageName;
            data.add(new AppInfo(label, pkg, ri));
        }

        ListView list = findViewById(R.id.listApps);
        list.setAdapter(new AppListAdapter(this, data));
        list.setOnItemClickListener(onItemClick);
    }

    private final AdapterView.OnItemClickListener onItemClick = (parent, view, position, id) -> {
        AppInfo info = data.get(position);
        // Ανοίγει App Info → για να πατήσεις χειροκίνητα "Clear cache"
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:" + info.packageName));
        startActivity(i);
    };
}
