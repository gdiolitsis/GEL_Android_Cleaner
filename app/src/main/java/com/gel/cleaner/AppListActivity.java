// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — FINAL STABLE SAFE BUILD (Thread-safe + No UI freeze)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class AppListActivity extends GELAutoActivityHook {

    private ListView list;

    private boolean userExpanded = true;
    private boolean systemExpanded = true;

    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visible = new ArrayList<>();
    private AppListAdapter adapter;

    private String search = "";
    private boolean sortByCacheBiggest = false;

    // ================= GUIDED MODE =================
    private boolean guidedActive = false;
    private final ArrayList<String> guidedQueue = new ArrayList<>();
    private int guidedIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        list = findViewById(R.id.listApps);
        EditText searchBox = findViewById(R.id.searchBar);
        Button btnSortName = findViewById(R.id.btnSortName);
        Button btnSortCache = findViewById(R.id.btnSortCache);
        Button btnGuided = findViewById(R.id.btnGuidedClean);
        CheckBox checkAll = findViewById(R.id.checkSelectAll);

        adapter = new AppListAdapter(this, visible);
        list.setAdapter(adapter);

        // ================= SELECT ALL =================
        if (checkAll != null) {
            checkAll.setOnCheckedChangeListener((b, checked) -> {

                for (AppEntry e : visible) {
                    if (e == null || e.isHeader) continue;
                    e.selected = checked;
                }

                adapter.notifyDataSetChanged();
            });
        }

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

        // ================= GUIDED =================
        if (btnGuided != null) {
            btnGuided.setOnClickListener(v -> startGuidedFromSelected());
        }

        // ================= CLICK =================
        list.setOnItemClickListener((parent, view, position, id) -> {

            if (position < 0 || position >= visible.size()) return;

            AppEntry e = visible.get(position);
            if (e == null) return;

            if (e.isHeader) {

                if (e.isUserHeader) userExpanded = !userExpanded;
                if (e.isSystemHeader) systemExpanded = !systemExpanded;

                applyFiltersAndSort();
                return;
            }

            openAppDetails(e.pkg);
        });

        // ================= LONG CLICK =================
        list.setOnItemLongClickListener((parent, view, position, id) -> {

            if (position < 0 || position >= visible.size()) return true;

            AppEntry e = visible.get(position);
            if (e == null || e.isHeader) return true;

            e.selected = !e.selected;
            adapter.notifyDataSetChanged();
            return true;
        });

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
    // APPLY FILTER + SORT (SAFE THREAD)
    // ============================================================

    private void applyFiltersAndSort() {

        new Thread(() -> {

            ArrayList<AppEntry> temp = new ArrayList<>();
            ArrayList<AppEntry> users = new ArrayList<>();
            ArrayList<AppEntry> systems = new ArrayList<>();

            for (AppEntry e : allApps) {

                if (e == null) continue;

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
                adapter.notifyDataSetChanged();
            });

        }).start();
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
    // GUIDED MODE
    // ============================================================

    private void startGuidedFromSelected() {

        guidedQueue.clear();
        guidedIndex = 0;

        for (AppEntry e : visible) {
            if (!e.isHeader && e.selected) guidedQueue.add(e.pkg);
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

        openAppDetails(guidedQueue.get(guidedIndex));
    }

    private void advanceGuidedIfNeeded() {
        if (!guidedActive) return;
        guidedIndex++;
        openNextGuided();
    }

    private void openAppDetails(String pkg) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkg));
        startActivity(intent);
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
