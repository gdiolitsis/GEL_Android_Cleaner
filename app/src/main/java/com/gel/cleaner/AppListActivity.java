// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — All Installed Apps + User/System + Alpha Sort + Guided Batch Settings (SAFE)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class AppListActivity extends GELAutoActivityHook implements GELFoldableCallback {

    private ListView list;

    // Foldable system
    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager uiManager;
    private GELFoldableAnimationPack animPack;
    private DualPaneManager dualPane;

    // DATA
    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visible = new ArrayList<>();
    private AppListAdapter adapter;
    // FILTERS
    private boolean showUser = true;
    private boolean showSystem = true;
    private String search = "";
    private boolean sortByCacheBiggest = false;

    // GUIDED MODE
    private boolean guidedActive = false;
    private final ArrayList<String> guidedQueue = new ArrayList<>();
    private int guidedIndex = 0;
    private String guidedCurrentPkg = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        list = findViewById(R.id.listApps);

        EditText searchBox = findViewById(R.id.searchBar);

searchBox.addTextChangedListener(new TextWatcher() {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        search = (s == null) ? "" : s.toString().trim();
        applyFiltersAndSort();
    }

    @Override public void afterTextChanged(Editable s) {}
});

        adapter = new AppListAdapter(this, visible);

        // Tap: open settings (normal mode)
        list.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            if (position < 0 || position >= visible.size()) return;
            AppEntry e = visible.get(position);
            if (e == null) return;
            openAppDetails(e.pkg);
        });

        // Long tap: toggle selection (multi-select)
        list.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= visible.size()) return true;
            AppEntry e = visible.get(position);
            if (e == null) return true;
            e.selected = !e.selected;
            adapter.notifyDataSetChanged();
            return true;
        });

        // INIT FOLDABLE ENGINE (SAFE)
        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // LOAD
        new Thread(this::loadAllApps).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foldDetector != null) foldDetector.start();

        // GUIDED: when user returns from Settings -> go next
        if (guidedActive) {
            advanceGuidedIfNeeded();
        }
    }

    @Override
    protected void onPause() {
        if (foldDetector != null) foldDetector.stop();
        super.onPause();
    }

    // ============================================================
    // FOLDABLE CALLBACKS
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
                try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}
            });
        } else {
            if (uiManager != null) uiManager.applyUI(isInner);
            try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}
        }
    }

    @Override
    public void onScreenChanged(boolean isInner) {
        if (animPack != null) {
            animPack.animateReflow(() -> {
                if (uiManager != null) uiManager.applyUI(isInner);
                try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}
            });
        } else {
            if (uiManager != null) uiManager.applyUI(isInner);
            try { DualPaneManager.prepareIfSupported(this); } catch (Throwable ignore) {}
        }
    }

    // ============================================================
    // LOAD APPS (ALL INSTALLED)
    // ============================================================
    private void loadAllApps() {
        try {
            PackageManager pm = getPackageManager();
            ArrayList<ApplicationInfo> apps = new ArrayList<>(pm.getInstalledApplications(PackageManager.GET_META_DATA));

            allApps.clear();

            for (ApplicationInfo ai : apps) {
                if (ai == null) continue;

                // Exclude ourselves? (optional)
                if (getPackageName().equals(ai.packageName)) {
                    // keep it if you want, but usually hide
                    // continue;
                }

                AppEntry e = new AppEntry();
                e.pkg = ai.packageName;
                e.isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                e.label = String.valueOf(pm.getApplicationLabel(ai));
                e.ai = ai;

                // sizes (best effort)
                fillSizesBestEffort(e);

                allApps.add(e);
            }

            applyFiltersAndSort();

        } catch (Throwable t) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Failed to load apps", Toast.LENGTH_SHORT).show()
            );
        }
    }

    // ============================================================
    // SIZES (StorageStatsManager on API 26+ with Usage Access)
    // ============================================================
    private void fillSizesBestEffort(AppEntry e) {
        e.appBytes = -1;
        e.cacheBytes = -1;

        if (e == null || TextUtils.isEmpty(e.pkg)) return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Old devices: keep unknown (production-safe, no hidden APIs)
            return;
        }

        if (!hasUsageAccess()) {
            return;
        }

        try {
            StorageStatsManager ssm = (StorageStatsManager) getSystemService(Context.STORAGE_STATS_SERVICE);
            if (ssm == null) return;

            StorageStats st = ssm.queryStatsForPackage(
                    android.os.storage.StorageManager.UUID_DEFAULT,
                    e.pkg,
                    android.os.UserHandle.getUserHandleForUid(Process.myUid())
            );

            if (st != null) {
                e.appBytes = st.getAppBytes();
                e.cacheBytes = st.getCacheBytes();
            }
        } catch (Throwable ignore) {}
    }

    private boolean hasUsageAccess() {
        try {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            if (appOps == null) return false;

            int mode = appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    getPackageName()
            );
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Throwable t) {
            return false;
        }
    }

    // Call this from a button later (Guided UI)
    private void requestUsageAccess() {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        } catch (Throwable ignore) {}
    }

    // ============================================================
    // FILTER + SORT
    // ============================================================
    private void applyFiltersAndSort() {

    visible.clear();

    ArrayList<AppEntry> users = new ArrayList<>();
    ArrayList<AppEntry> systems = new ArrayList<>();

    for (AppEntry e : allApps) {
        if (e == null) continue;

        if (e.isSystem && !showSystem) continue;
        if (!e.isSystem && !showUser) continue;

        if (!TextUtils.isEmpty(search)) {
            String s = search.toLowerCase(Locale.getDefault());
            String name = e.label == null ? "" : e.label.toLowerCase(Locale.getDefault());
            String pkg = e.pkg == null ? "" : e.pkg.toLowerCase(Locale.getDefault());
            if (!name.contains(s) && !pkg.contains(s)) continue;
        }

        if (e.isSystem) {
            systems.add(e);
        } else {
            users.add(e);
        }
    }

    // Sort inside each group
    if (sortByCacheBiggest) {

        Comparator<AppEntry> cacheSort = (a, b) -> {
            long ca = a.cacheBytes;
            long cb = b.cacheBytes;

            if (ca < 0 && cb < 0) return alphaCompare(a, b);
            if (ca < 0) return 1;
            if (cb < 0) return -1;

            int cmp = Long.compare(cb, ca);
            if (cmp != 0) return cmp;
            return alphaCompare(a, b);
        };

        Collections.sort(users, cacheSort);
        Collections.sort(systems, cacheSort);

    } else {

        Collections.sort(users, this::alphaCompare);
        Collections.sort(systems, this::alphaCompare);
    }

    // Merge: User first, then System
    visible.addAll(users);
    visible.addAll(systems);

    runOnUiThread(() -> adapter.notifyDataSetChanged());
}

    private int alphaCompare(AppEntry a, AppEntry b) {
        String la = (a == null || a.label == null) ? "" : a.label;
        String lb = (b == null || b.label == null) ? "" : b.label;

        Collator c = Collator.getInstance(Locale.getDefault());
        c.setStrength(Collator.PRIMARY);

        int cmp = c.compare(la, lb);
        if (cmp != 0) return cmp;

        String pa = (a == null || a.pkg == null) ? "" : a.pkg;
        String pb = (b == null || b.pkg == null) ? "" : b.pkg;
        return pa.compareToIgnoreCase(pb);
    }

    // ============================================================
    // GUIDED BATCH MODE (SAFE)
    // ============================================================
    private void startGuidedFromSelected() {
        guidedQueue.clear();
        guidedIndex = 0;
        guidedCurrentPkg = null;

        for (AppEntry e : visible) {
            if (e != null && e.selected && !TextUtils.isEmpty(e.pkg)) {
                guidedQueue.add(e.pkg);
            }
        }

        if (guidedQueue.isEmpty()) {
            Toast.makeText(this, "No apps selected", Toast.LENGTH_SHORT).show();
            return;
        }

        guidedActive = true;
        openNextGuided();
    }

    private void stopGuided() {
        guidedActive = false;
        guidedQueue.clear();
        guidedIndex = 0;
        guidedCurrentPkg = null;
    }

    private void skipCurrentGuided() {
        if (!guidedActive) return;
        guidedIndex++;
        openNextGuided();
    }

    private void advanceGuidedIfNeeded() {
        // We returned from Settings -> go next
        // (No need to detect if cache cleared; user is the judge.)
        if (!guidedActive) return;

        // If we have a current, advance to next
        if (guidedCurrentPkg != null) {
            guidedIndex++;
            openNextGuided();
        }
    }

    private void openNextGuided() {
        if (!guidedActive) return;

        if (guidedIndex < 0) guidedIndex = 0;

        if (guidedIndex >= guidedQueue.size()) {
            stopGuided();
            Toast.makeText(this, "Guided batch finished", Toast.LENGTH_SHORT).show();
            return;
        }

        guidedCurrentPkg = guidedQueue.get(guidedIndex);
        openAppDetails(guidedCurrentPkg);
    }

    // ============================================================
    // OPEN APP SETTINGS
    // ============================================================
    private void openAppDetails(String pkg) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);
        } catch (Throwable ignored) {}
    }

    // ============================================================
    // MODEL
    // ============================================================
    static class AppEntry {
        String pkg;
        String label;
        boolean isSystem;
        boolean selected;
        long appBytes;   // -1 unknown
        long cacheBytes; // -1 unknown
        ApplicationInfo ai;
    }
}
