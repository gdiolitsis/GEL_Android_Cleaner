package com.gel.cleaner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private List<ResolveInfo> apps;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        pm = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, (a, b) ->
                String.valueOf(a.loadLabel(pm)).compareToIgnoreCase(String.valueOf(b.loadLabel(pm))));

        ListView list = findViewById(R.id.listApps);
        list.setAdapter(new AppAdapter());
        list.setOnItemClickListener(onItemClick);
    }

    private final AdapterView.OnItemClickListener onItemClick = (parent, view, position, id) -> {
        ResolveInfo info = apps.get(position);
        String pkg = info.activityInfo.packageName;

        // Open App Info screen so the user can tap "Clear cache"
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:" + pkg));
        startActivity(i);
    };

    private class AppAdapter extends BaseAdapter {
        @Override public int getCount() { return apps.size(); }
        @Override public Object getItem(int i) { return apps.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(AppListActivity.this)
                        .inflate(R.layout.list_item_app, parent, false);
            }
            ResolveInfo info = apps.get(position);
            ImageView icon = v.findViewById(R.id.appIcon);
            TextView name = v.findViewById(R.id.appName);
            TextView pkg  = v.findViewById(R.id.appPkg);

            icon.setImageDrawable(info.loadIcon(pm));
            name.setText(info.loadLabel(pm));
            pkg.setText(info.activityInfo.packageName);
            return v;
        }
    }
}
