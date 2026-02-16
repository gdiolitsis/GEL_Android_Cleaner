// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — FINAL GEL BUILD (Cache + Uninstall + Full Toggle Select)

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

    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visible = new ArrayList<>();
    private AppListAdapter adapter;

    private String search = "";
    private boolean sortByCacheBiggest = false;

    private boolean guidedActive = false;
    private final ArrayList<String> guidedQueue = new ArrayList<>();
    private int guidedIndex = 0;

    private boolean isUninstallMode = false;

    // ===== SELECT STATES =====
    private boolean allSelected = false;
    private boolean usersSelected = false;
    private boolean systemSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        recyclerView = findViewById(R.id.listApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditText searchBox     = findViewById(R.id.searchBar);
        Button btnSortCache    = findViewById(R.id.btnSortCache);
        Button btnGuided       = findViewById(R.id.btnGuidedClean);
        Button btnSelectAll    = findViewById(R.id.btnSelectAll);
        Button btnSelectUsers  = findViewById(R.id.btnSelectUsers);
        Button btnSelectSystem = findViewById(R.id.btnSelectSystem);

        adapter = new AppListAdapter(this);
        recyclerView.setAdapter(adapter);

        // ===== MODE =====
        String mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "cache";
        isUninstallMode = "uninstall".equals(mode);

        // ===== SEARCH =====
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

        // ===== SORT =====
        if (btnSortCache != null) {
            btnSortCache.setOnClickListener(v -> {
                sortByCacheBiggest = true;
                applyFiltersAndSort();
            });
        }

        // ===== GLOBAL SELECT =====
        if (btnSelectAll != null) {
            btnSelectAll.setOnClickListener(v -> {

                allSelected = !allSelected;

                for (AppEntry e : visible) {
                    if (!e.isHeader) {
                        e.selected = allSelected;
                    }
                }

                btnSelectAll.setText(
                        allSelected ?
                                getString(R.string.deselect_all) :
                                getString(R.string.select_all)
                );

                adapter.submitList(new ArrayList<>(visible));
            });
        }

        // ===== USER SELECT =====
        if (btnSelectUsers != null) {
            btnSelectUsers.setOnClickListener(v -> {

                usersSelected = !usersSelected;

                for (AppEntry e : visible) {
                    if (!e.isHeader && !e.isSystem) {
                        e.selected = usersSelected;
                    }
                }

                btnSelectUsers.setText(
                        usersSelected ?
                                getString(R.string.deselect_user_apps) :
                                getString(R.string.select_user_apps)
                );

                adapter.submitList(new ArrayList<>(visible));
            });
        }

        // ===== SYSTEM SELECT =====
        if (btnSelectSystem != null) {
            btnSelectSystem.setOnClickListener(v -> {

                systemSelected = !systemSelected;

                for (AppEntry e : visible) {
                    if (!e.isHeader && e.isSystem) {
                        e.selected = systemSelected;
                    }
                }

                btnSelectSystem.setText(
                        systemSelected ?
                                getString(R.string.deselect_system_apps) :
                                getString(R.string.select_system_apps)
                );

                adapter.submitList(new ArrayList<>(visible));
            });
        }

        // ===== GUIDED ACTION =====
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

            Comparator<AppEntry> comparator =
                    sortByCacheBiggest ?
                            (a, b) -> Long.compare(b.cacheBytes, a.cacheBytes) :
                            this::alphaCompare;

            Collections.sort(users, comparator);
            Collections.sort(systems, comparator);

            if (!users.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.headerTitle = "📱 USER APPS";
                temp.add(h);
                temp.addAll(users);
            }

            if (!systems.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.headerTitle = "⚙ SYSTEM APPS";
                temp.add(h);
                temp.addAll(systems);
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
            Toast.makeText(this, "Operation finished", Toast.LENGTH_SHORT).show();
            return;
        }

        String pkg = guidedQueue.get(guidedIndex);

        if (isUninstallMode) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);
        }
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
        String headerTitle;
    }
}
