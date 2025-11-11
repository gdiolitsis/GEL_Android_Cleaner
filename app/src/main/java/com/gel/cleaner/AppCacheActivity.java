package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppCacheActivity extends AppCompatActivity {

    ListView list;
    List<AppInfo> apps = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_app_cache);

        list = findViewById(R.id.listApps);

        loadApps();
        list.setAdapter(new AppListAdapter(this, apps));

        list.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo info = apps.get(position);
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + info.packageName));
            startActivity(i);
        });
    }

    private void loadApps() {
        List<ResolveInfo> lst =
                getPackageManager().queryIntentActivities(
                        new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
                        0
                );

        for (ResolveInfo r : lst) {
            String pkg = r.activityInfo.packageName;
            apps.add(new AppInfo(pkg, r));
        }
    }
}
