package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private ListView listView;
    private List<AppInfo> data = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_app_list);

        listView = findViewById(R.id.list);

        load();
    }

    private void load() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ai : apps) {
            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                continue;

            AppInfo a = new AppInfo();
            a.packageName = ai.packageName;
            a.label = pm.getApplicationLabel(ai).toString();
            a.resolveInfo = ai;
            data.add(a);
        }

        AppListAdapter ad = new AppListAdapter(this, data);
        listView.setAdapter(ad);

        listView.setOnItemClickListener((AdapterView<?> parent, android.view.View v, int pos, long id) -> {
            AppInfo a = data.get(pos);
            Intent i = new Intent(this, AppCacheActivity.class);
            i.putExtra("pkg", a.packageName);
            startActivity(i);
        });
    }

    // ✅ CORRECT NESTED CLASS
    public static class AppInfo {
        public String packageName;
        public String label;
        public ApplicationInfo resolveInfo;
    }
}
