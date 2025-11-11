package com.gel.cleaner;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class AppListActivity extends Activity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewlayout);

        list = findViewById(R.id.list);

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(i, 0);

        AppListAdapter ad = new AppListAdapter(this, apps);
        list.setAdapter(ad);
    }
}
