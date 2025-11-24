// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — Foldable-ready + GELAutoScaling + DarkGold UI
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AppListActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private ListView list;

    // FOLDABLE ENGINE
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private GELFoldableOrchestrator orchestrator;      // NEW
    private DualPaneManager dualPaneManager;           // NEW
    private GELFoldableAnimationPack animPack;         // NEW

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        // ============================================================
        // 1) INIT FOLDABLE ENGINE (Full Integration)
        // ============================================================
        uiManager = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);
        orchestrator = new GELFoldableOrchestrator(this, uiManager);
        dualPaneManager = new DualPaneManager(this);
        animPack = new GELFoldableAnimationPack(this);

        orchestrator.attach(list);       // future-proof auto-width handling
        dualPaneManager.attach(this);    // split mode for tablets / inner screen
        animPack.applyFadeIn(findViewById(android.R.id.content)); // soft animation

        // ============================================================
        // 2) NORMAL UI SETUP
        // ============================================================
        list = findViewById(R.id.listApps);

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(i, 0);

        AppListAdapter ad = new AppListAdapter(this, apps);
        list.setAdapter(ad);

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
        foldDetector.start();
    }

    @Override
    protected void onPause() {
        foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS — FIXED + UPDATED
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull GELFoldablePosture posture) {
        orchestrator.onPosture(posture);     // main hinge logic
        animPack.onPosture(posture);         // animations adapt
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        uiManager.applyUI(isInner);          // inner = big tablet-like screen
        dualPaneManager.onScreenMode(isInner);
    }

    // ============================================================
    // OPEN PER-APP SETTINGS
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
