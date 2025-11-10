package com.gel.cleaner;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    ListView listApps;
    ArrayAdapter<String> adapter;
    ArrayList<String> appNames = new ArrayList<>();
    ArrayList<String> packages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        listApps = findViewById(R.id.listApps);

        loadApps();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                appNames
        );
        listApps.setAdapter(adapter);

        listApps.setOnItemClickListener(onClick);
    }

    /** Load installed apps */
    private void loadApps() {

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        for (ApplicationInfo ai : apps) {

            // Skip system apps
            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

            String label = pm.getApplicationLabel(ai).toString();
            appNames.add(label);
            packages.add(ai.packageName);
        }
    }

    /** OnClick → go to app-info page */
    private final AdapterView.OnItemClickListener onClick = (parent, view, pos, id) -> {

        String pkg = packages.get(pos);

        Intent intent = new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + pkg)
        );

        startActivity(intent);
    };
}
