package com.gel.cleaner;

import android.app.Activity;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppListActivity extends AppCompatActivity {

    ListView listView;
    List<AppInfo> apps = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        listView = findViewById(R.id.listView);

        loadApps();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo a = apps.get(position);

            Toast.makeText(this, "Clearing cache: " + a.label, Toast.LENGTH_SHORT).show();

            GELCleaner.clearAppCache(this, a.packageName, null);
        });
    }


    /* =========================================
     *            LOAD APPS & CACHE SIZE
     * ========================================= */
    private void loadApps() {
        PackageManager pm = getPackageManager();

        List<ApplicationInfo> list = pm.getInstalledApplications(0);

        for (ApplicationInfo ai : list) {
            try {
                String name = pm.getApplicationLabel(ai).toString();
                long cache = getCacheSize(ai.packageName);
                apps.add(new AppInfo(name, ai.packageName, cache));
            } catch (Exception ignored) {}
        }

        apps.sort((a, b) -> Long.compare(b.cacheSize, a.cacheSize));

        List<String> labels = new ArrayList<>();
        for (AppInfo a: apps)
            labels.add(a.label + "   (" + format(a.cacheSize) + ")");

        listView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                labels
        ));
    }


    /* =========================================
     *                UTILS
     * ========================================= */
    private long getCacheSize(String pkg){
        try {
            StorageStatsManager ssm = (StorageStatsManager)
                    getSystemService(Context.STORAGE_STATS_SERVICE);

            UUID uuid = StorageStatsManager.UUID_DEFAULT;
            UserHandle user = android.os.Process.myUserHandle();

            StorageStats stats = ssm.queryStatsForPackage(
                    uuid,
                    pkg,
                    user
            );

            return stats.getCacheBytes();
        }
        catch (Exception ignored) {}

        return 0;
    }

    private String format(long bytes){
        if (bytes < 1024) return bytes + " B";
        int z = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
        return String.format("%.1f %sB",
                (double) bytes / (1L << (z * 10)),
                " KMGTPE".charAt(z));
    }


    /* =========================================
     *              MODEL
     * ========================================= */
    static class AppInfo {
        String label;
        String packageName;
        long cacheSize;

        AppInfo(String label, String packageName, long cacheSize) {
            this.label = label;
            this.packageName = packageName;
            this.cacheSize = cacheSize;
        }
    }
}
