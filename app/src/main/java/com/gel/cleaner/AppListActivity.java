// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — Foldable-ready + GELAutoScaling + DarkGold UI (FIXED v2.1)
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AppListActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private ListView list;

    // FOLDABLE ENGINE
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private DualPaneManager dualPane;
    private GELFoldableAnimationPack animPack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        // NORMAL UI SETUP
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

        // ============================================================
        // INIT FOLDABLE ENGINE (100% compatible with GEL base)
        // ============================================================
        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foldDetector != null) foldDetector.start();
    }

    @Override
    protected void onPause() {
        if (foldDetector != null) foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS (BASE API)
    // ============================================================
    @Override
    public void onPostureChanged(@NonNull Posture posture) {

        final boolean isInner =
                (posture == Posture.FLAT ||
                 posture == Posture.TABLE_MODE ||
                 posture == Posture.FULLY_OPEN);

        if (animPack != null) {
            animPack.animateReflow(() -> {
                if (uiManager != null) uiManager.applyUI(isInner);
                if (dualPane  != null) dualPane.dispatchMode(isInner);
            });
        } else {
            if (uiManager != null) uiManager.applyUI(isInner);
            if (dualPane  != null) dualPane.dispatchMode(isInner);
        }
    }

    @Override
    public void onScreenChanged(boolean isInner) {

        if (animPack != null) {
            animPack.animateReflow(() -> {
                if (uiManager != null) uiManager.applyUI(isInner);
                if (dualPane  != null) dualPane.dispatchMode(isInner);
            });
        } else {
            if (uiManager != null) uiManager.applyUI(isInner);
            if (dualPane  != null) dualPane.dispatchMode(isInner);
        }
    }

    // ============================================================
    // OPEN PER-APP SETTINGS
    // ============================================================
    private void openAppDetails(String pkg) {
        try {
            Intent intent =
                    new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + pkg));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
