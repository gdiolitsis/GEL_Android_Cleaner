// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — FINAL PRO BUILD (Dark-Gold + Stats + Full Toggle)

package com.gel.cleaner;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private boolean isUninstallMode = false;
    private boolean sortByCacheBiggest = false;

    // ===== SELECT STATES =====
    private boolean allSelected = false;
    private boolean usersSelected = false;
    private boolean systemSelected = false;

    // ===== STATS =====
    private TextView txtTotal;
    private TextView txtUsers;
    private TextView txtSystem;
    private TextView txtSelected;
    private TextView txtSelectedCache;
    private TextView txtSelectedApp;

    private final ArrayList<String> guidedQueue = new ArrayList<>();
    private int guidedIndex = 0;
    private boolean guidedActive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        recyclerView = findViewById(R.id.listApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditText searchBox     = findViewById(R.id.searchBar);
        Button btnSelectAll    = findViewById(R.id.btnSelectAll);
        Button btnSelectUsers  = findViewById(R.id.btnSelectUsers);
        Button btnSelectSystem = findViewById(R.id.btnSelectSystem);
        Button btnSortCache    = findViewById(R.id.btnSortCache);
        Button btnGuided       = findViewById(R.id.btnGuidedClean);

        txtTotal         = findViewById(R.id.txtStatTotal);
        txtUsers         = findViewById(R.id.txtStatUsers);
        txtSystem        = findViewById(R.id.txtStatSystem);
        txtSelected      = findViewById(R.id.txtStatSelected);
        txtSelectedCache = findViewById(R.id.txtStatCache);
        txtSelectedApp   = findViewById(R.id.txtStatApp);

        adapter = new AppListAdapter(this);
        recyclerView.setAdapter(adapter);

        requestUsageAccessIfNeeded();

        String mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "cache";
        isUninstallMode = "uninstall".equals(mode);

        if (searchBox != null) {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFiltersAndSort(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        if (btnSortCache != null) {
            btnSortCache.setOnClickListener(v -> {
                sortByCacheBiggest = !sortByCacheBiggest;
                applyFiltersAndSort(null);
            });
        }

        if (btnSelectAll != null) {
            btnSelectAll.setOnClickListener(v -> {
                allSelected = !allSelected;
                for (AppEntry e : visible) if (!e.isHeader) e.selected = allSelected;
                btnSelectAll.setText(allSelected ?
                        getString(R.string.deselect_all) :
                        getString(R.string.select_all));
                refreshUI();
            });
        }

        if (btnSelectUsers != null) {
            btnSelectUsers.setOnClickListener(v -> {
                usersSelected = !usersSelected;
                for (AppEntry e : visible)
                    if (!e.isHeader && !e.isSystem)
                        e.selected = usersSelected;
                btnSelectUsers.setText(usersSelected ?
                        getString(R.string.deselect_user_apps) :
                        getString(R.string.select_user_apps));
                refreshUI();
            });
        }

        if (btnSelectSystem != null) {
            btnSelectSystem.setOnClickListener(v -> {
                systemSelected = !systemSelected;
                for (AppEntry e : visible)
                    if (!e.isHeader && e.isSystem)
                        e.selected = systemSelected;
                btnSelectSystem.setText(systemSelected ?
                        getString(R.string.deselect_system_apps) :
                        getString(R.string.select_system_apps));
                refreshUI();
            });
        }

        if (btnGuided != null)
            btnGuided.setOnClickListener(v -> startGuided());

        new Thread(this::loadAllApps).start();
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

            fillSizes(e);
            allApps.add(e);
        }

        applyFiltersAndSort(null);
    }

    private void fillSizes(AppEntry e) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        if (!hasUsageAccess()) return;

        try {
            StorageStatsManager ssm =
                    (StorageStatsManager) getSystemService(Context.STORAGE_STATS_SERVICE);

            StorageStats st = ssm.queryStatsForPackage(
                    android.os.storage.StorageManager.UUID_DEFAULT,
                    e.pkg,
                    android.os.UserHandle.getUserHandleForUid(Process.myUid())
            );

            e.appBytes = st.getAppBytes();
            e.cacheBytes = st.getCacheBytes();

        } catch (Throwable ignored) {}
    }

    // ============================================================
    // FILTER
    // ============================================================

    private void applyFiltersAndSort(String search) {

        new Thread(() -> {

            ArrayList<AppEntry> temp = new ArrayList<>();

            for (AppEntry e : allApps) {

                if (!TextUtils.isEmpty(search)) {
                    String s = search.toLowerCase(Locale.US);
                    if (!e.label.toLowerCase(Locale.US).contains(s)
                            && !e.pkg.toLowerCase(Locale.US).contains(s))
                        continue;
                }

                temp.add(e);
            }

            Comparator<AppEntry> comp =
                    sortByCacheBiggest ?
                            (a,b) -> Long.compare(b.cacheBytes, a.cacheBytes) :
                            this::alphaCompare;

            Collections.sort(temp, comp);

            runOnUiThread(() -> {
                visible.clear();
                visible.addAll(temp);
                refreshUI();
            });

        }).start();
    }

    private int alphaCompare(AppEntry a, AppEntry b) {
        Collator c = Collator.getInstance(Locale.getDefault());
        return c.compare(a.label, b.label);
    }

    // ============================================================
    // UI REFRESH
    // ============================================================

    private void refreshUI() {
        adapter.submitList(new ArrayList<>(visible));
        updateStats();
    }

    private void updateStats() {

        int total = 0, users = 0, systems = 0, selected = 0;
        long selCache = 0, selApp = 0;

        for (AppEntry e : visible) {

            total++;
            if (e.isSystem) systems++;
            else users++;

            if (e.selected) {
                selected++;
                selCache += e.cacheBytes;
                selApp   += e.appBytes;
            }
        }

        if (txtTotal != null)    txtTotal.setText("Total: " + total);
        if (txtUsers != null)    txtUsers.setText("User: " + users);
        if (txtSystem != null)   txtSystem.setText("System: " + systems);
        if (txtSelected != null) txtSelected.setText("Selected: " + selected);
        if (txtSelectedCache != null)
            txtSelectedCache.setText("Cache: " + formatBytes(selCache));
        if (txtSelectedApp != null)
            txtSelectedApp.setText("App: " + formatBytes(selApp));
    }

    private String formatBytes(long b) {
        if (b <= 0) return "0 B";
        float kb = b / 1024f;
        if (kb < 1024) return String.format(Locale.US,"%.1f KB",kb);
        float mb = kb / 1024f;
        if (mb < 1024) return String.format(Locale.US,"%.1f MB",mb);
        return String.format(Locale.US,"%.1f GB",mb/1024f);
    }

    // ============================================================
    // GUIDED
    // ============================================================

    private void startGuided() {

        guidedQueue.clear();

        for (AppEntry e : visible)
            if (e.selected) guidedQueue.add(e.pkg);

        if (guidedQueue.isEmpty()) {
            showGelDialog("No apps selected");
            return;
        }

        guidedIndex = 0;
        guidedActive = true;
        openNext();
    }

    private void openNext() {

        if (guidedIndex >= guidedQueue.size()) {
            guidedActive = false;
            showGelDialog("Operation finished");
            return;
        }

        String pkg = guidedQueue.get(guidedIndex);

        Intent i = isUninstallMode ?
                new Intent(Intent.ACTION_DELETE) :
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        i.setData(Uri.parse("package:" + pkg));
        startActivity(i);
    }

    // ============================================================
    // USAGE ACCESS
    // ============================================================

    private void requestUsageAccessIfNeeded() {
        if (hasUsageAccess()) return;
        showGelDialog(getString(R.string.toast_usage_access));
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    private boolean hasUsageAccess() {
        AppOpsManager appOps =
                (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // ============================================================
    // GEL DIALOG
    // ============================================================

    private void showGelDialog(String message) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LinearLayout root = new LinearLayout(this);
        root.setPadding(50,40,50,40);
        root.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(30);
        bg.setStroke(4,0xFFFFD700);
        root.setBackground(bg);

        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(16f);

        root.addView(tv);
        b.setView(root);
        b.setPositiveButton("OK",null);
        b.show();
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
        boolean isHeader;
    }
}
