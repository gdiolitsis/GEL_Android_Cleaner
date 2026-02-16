// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — FINAL RecyclerView + Category Select BUILD

package com.gel.cleaner;

import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class AppListActivity extends GELAutoActivityHook {

    private RecyclerView recyclerView;

    private boolean userExpanded = true;
    private boolean systemExpanded = true;

    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visible = new ArrayList<>();
    private AppListAdapter adapter;

    private String search = "";
    private boolean sortByCacheBiggest = false;

    private boolean guidedActive = false;
    private final ArrayList<String> guidedQueue = new ArrayList<>();
    private int guidedIndex = 0;

    private String mode = "cache";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        recyclerView = findViewById(R.id.listApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditText searchBox      = findViewById(R.id.searchBar);
        Button btnSortName      = findViewById(R.id.btnSortName);
        Button btnSortCache     = findViewById(R.id.btnSortCache);
        Button btnGuided        = findViewById(R.id.btnGuidedClean);
        Button btnSelectUsers   = findViewById(R.id.btnSelectUsers);
        Button btnSelectSystem  = findViewById(R.id.btnSelectSystem);

        adapter = new AppListAdapter(this);
        recyclerView.setAdapter(adapter);

        mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "cache";

        // ================= SEARCH =================
        if (searchBox != null) {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search = (s == null) ? "" : s.toString().trim();
                    applyFiltersAndSort();
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // ================= SORT =================
        if (btnSortName != null) {
            btnSortName.setOnClickListener(v -> {
                sortByCacheBiggest = false;
                applyFiltersAndSort();
            });
        }

        if (btnSortCache != null) {
            btnSortCache.setOnClickListener(v -> {
                sortByCacheBiggest = true;
                applyFiltersAndSort();
            });
        }

        // ================= SELECT USERS =================
        if (btnSelectUsers != null) {
            btnSelectUsers.setOnClickListener(v -> {
                for (AppEntry e : visible) {
                    if (!e.isHeader && !e.isSystem) {
                        e.selected = true;
                    }
                }
                adapter.submitList(new ArrayList<>(visible));
            });
        }

        // ================= SELECT SYSTEM =================
        if (btnSelectSystem != null) {
            btnSelectSystem.setOnClickListener(v -> {
                for (AppEntry e : visible) {
                    if (!e.isHeader && e.isSystem) {
                        e.selected = true;
                    }
                }
                adapter.submitList(new ArrayList<>(visible));
            });
        }

        // ================= GUIDED =================
        if (btnGuided != null) {
            btnGuided.setOnClickListener(v -> startGuidedFromSelected());
        }

        new Thread(this::loadAllApps).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (guidedActive) advanceGuidedIfNeeded();
    }

    // ============================================================
    // LOAD APPS
    // ============================================================

    private void loadAllApps() {

        PackageManager pm = getPackageManager();
        allApps.clear();

        for (ApplicationInfo ai : pm.getInstalledApplications(0)) {

            AppEntry e = new AppEntry();
            e.pkg = ai.packageName;
            e.label = String.valueOf(pm.getApplicationLabel(ai));
            e.isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            e.ai = ai;

            fillSizesBestEffort(e);
            allApps.add(e);
        }

        applyFiltersAndSort();
    }

    private void fillSizesBestEffort(AppEntry e) {

        e.appBytes = -1;
        e.cacheBytes = -1;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        if (!hasUsageAccess()) return;

        try {
            StorageStatsManager ssm =
                    (StorageStatsManager) getSystemService(Context.STORAGE_STATS_SERVICE);

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

        } catch (Throwable ignored) {}
    }

    private boolean hasUsageAccess() {
        try {
            AppOpsManager appOps =
                    (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

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

    // ============================================================
    // FILTER + SORT
    // ============================================================

    private void applyFiltersAndSort() {

        new Thread(() -> {

            ArrayList<AppEntry> temp = new ArrayList<>();
            ArrayList<AppEntry> users = new ArrayList<>();
            ArrayList<AppEntry> systems = new ArrayList<>();

            for (AppEntry e : allApps) {

                if (!TextUtils.isEmpty(search)) {
                    String s = search.toLowerCase(Locale.US);
                    String name = e.label == null ? "" : e.label.toLowerCase(Locale.US);
                    String pkg  = e.pkg == null ? "" : e.pkg.toLowerCase(Locale.US);
                    if (!name.contains(s) && !pkg.contains(s)) continue;
                }

                if (e.isSystem) systems.add(e);
                else users.add(e);
            }

            Comparator<AppEntry> comparator;

            if (sortByCacheBiggest) {
                comparator = (a, b) -> {
                    long ca = a.cacheBytes;
                    long cb = b.cacheBytes;
                    if (ca < 0 && cb < 0) return alphaCompare(a,b);
                    if (ca < 0) return 1;
                    if (cb < 0) return -1;
                    int cmp = Long.compare(cb, ca);
                    return (cmp != 0) ? cmp : alphaCompare(a,b);
                };
            } else {
                comparator = this::alphaCompare;
            }

            Collections.sort(users, comparator);
            Collections.sort(systems, comparator);

            if (!users.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.isUserHeader = true;
                h.headerTitle = userExpanded
                        ? "📱 USER APPS (tap to collapse)"
                        : "📱 USER APPS (tap to expand)";
                temp.add(h);
                if (userExpanded) temp.addAll(users);
            }

            if (!systems.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.isSystemHeader = true;
                h.headerTitle = systemExpanded
                        ? "⚙ SYSTEM APPS (tap to collapse)"
                        : "⚙ SYSTEM APPS (tap to expand)";
                temp.add(h);
                if (systemExpanded) temp.addAll(systems);
            }

            runOnUiThread(() -> {
                visible.clear();
                visible.addAll(temp);
                adapter.submitList(new ArrayList<>(visible));
            });

        }).start();
    }

    private int alphaCompare(AppEntry a, AppEntry b) {

        Collator c = Collator.getInstance(Locale.getDefault());
        c.setStrength(Collator.PRIMARY);

        int cmp = c.compare(a.label, b.label);
        if (cmp != 0) return cmp;

        return a.pkg.compareToIgnoreCase(b.pkg);
    }

    // ============================================================
    // GUIDED MODE
    // ============================================================

    private void startGuidedFromSelected() {

        guidedQueue.clear();
        guidedIndex = 0;

        for (AppEntry e : visible) {
            if (!e.isHeader && e.selected) {
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

    private void openNextGuided() {

        if (guidedIndex >= guidedQueue.size()) {
            guidedActive = false;
            Toast.makeText(this, "Guided cleaning finished", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + guidedQueue.get(guidedIndex)));
        startActivity(intent);
    }

    private void advanceGuidedIfNeeded() {
        if (!guidedActive) return;
        guidedIndex++;
        openNextGuided();
    }

    // ============================================================
    // MODEL
    // ============================================================

    static class AppEntry {
        String pkg;
        String label;
        boolean isSystem;
        boolean selected;
        long appBytes;
        long cacheBytes;
        ApplicationInfo ai;

        boolean isHeader;
        boolean isUserHeader;
        boolean isSystemHeader;
        String headerTitle;
    }
}
