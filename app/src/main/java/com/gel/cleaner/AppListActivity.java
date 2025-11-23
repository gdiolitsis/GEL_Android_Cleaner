// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — Foldable-ready + GELAutoScaling + DarkGold UI
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView;
import androidx.annotation.Nullable;

import java.util.List;

public class AppListActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private ListView list;

    // Foldable engine
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        // ============================================================
        // 1) INIT FOLDABLE ENGINE
        // ============================================================
        uiManager = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // ============================================================
        // 2) NORMAL UI SETUP
        // ============================================================
        list = findViewById(R.id.listApps);

        // Fetch launcher apps
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(i, 0);

        // Adapter auto-scales μέσω GELAutoDP
        AppListAdapter ad = new AppListAdapter(this, apps);
        list.setAdapter(ad);

        // CLICK → Open app settings
        list.setOnItemClickListener((AdapterView<?> parent, android.view.View view,
                                     int position, long id) -> {

            ResolveInfo info = apps.get(position);
            if (info != null && info.activityInfo != null) {
                openAppDetails(info.activityInfo.packageName);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        foldDetector.start();   // Start hinge angle listener
    }

    @Override
    protected void onPause() {
        foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {
        // Optional debug point — no UI changes needed here yet
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        // Auto-UI reflow (inner = tablet mode)
        uiManager.applyUI(isInner);
    }

    // ============================================================
    // APP DETAILS SCREEN
    // ============================================================
    private void openAppDetails(String pkg) {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + pkg));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
