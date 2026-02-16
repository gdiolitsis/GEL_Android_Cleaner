// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — FINAL GEL COMPLETE BUILD

package com.gel.cleaner;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
    private TextView txtStats;

    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visible = new ArrayList<>();
    private AppListAdapter adapter;

    private String search = "";
    private boolean sortByCacheBiggest = false;
    private boolean isUninstallMode = false;

    private boolean userExpanded = true;
    private boolean systemExpanded = true;

    private boolean allSelected = false;
    private boolean usersSelected = false;
    private boolean systemSelected = false;

    private boolean guidedActive = false;
    private final ArrayList<String> guidedQueue = new ArrayList<>();
    private int guidedIndex = 0;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        recyclerView = findViewById(R.id.listApps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtStats = new TextView(this);
        txtStats.setTextColor(Color.parseColor("#FFD700"));
        txtStats.setTextSize(13f);
        txtStats.setPadding(4,4,4,8);
        ((LinearLayout) recyclerView.getParent()).addView(txtStats, 3);

        EditText searchBox     = findViewById(R.id.searchBar);
        Button btnSelectAll    = findViewById(R.id.btnSelectAll);
        Button btnSortCache    = findViewById(R.id.btnSortCache);
        Button btnSelectUsers  = findViewById(R.id.btnSelectUsers);
        Button btnSelectSystem = findViewById(R.id.btnSelectSystem);
        Button btnGuided       = findViewById(R.id.btnGuidedClean);

        adapter = new AppListAdapter(this);
        recyclerView.setAdapter(adapter);

        requestUsageAccessIfNeeded();

        String mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "cache";
        isUninstallMode = "uninstall".equals(mode);

        // SEARCH
        if (searchBox != null) {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search = s == null ? "" : s.toString().trim();
                    applyFiltersAndSort();
                }
            });
        }

        // SORT
        if (btnSortCache != null) {
            btnSortCache.setOnClickListener(v -> {
                sortByCacheBiggest = !sortByCacheBiggest;
                applyFiltersAndSort();
            });
        }

        // GLOBAL SELECT
        if (btnSelectAll != null) {
            btnSelectAll.setOnClickListener(v -> {
                allSelected = !allSelected;
                for (AppEntry e : visible)
                    if (!e.isHeader) e.selected = allSelected;

                btnSelectAll.setText(allSelected ?
                        getString(R.string.deselect_all) :
                        getString(R.string.select_all));

                refreshUI();
            });
        }

        // USERS
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

        // SYSTEM
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

        // GUIDED
        if (btnGuided != null) {
            btnGuided.setOnClickListener(v -> startGuided());
        }

        new Thread(this::loadAllApps).start();
    }

    requestUsageAccessIfNeeded();
showUsageAccessDialog();

    @Override
    protected void onResume() {
        super.onResume();
        if (hasUsageAccess())
            new Thread(this::loadAllApps).start();

        if (guidedActive) advanceGuided();
    }
    if (!hasUsageAccess()) {
    showUsageAccessDialog();
}

    private void showUsageAccessDialog() {

    if (hasUsageAccess()) return;

    androidx.appcompat.app.AlertDialog.Builder b =
            new androidx.appcompat.app.AlertDialog.Builder(
                    this,
                    R.style.Theme_GEL_DarkGold
            );

    b.setCancelable(false);

    String msg = AppLang.isGreek(this)
            ? "Για να εμφανίζονται τα μεγέθη εφαρμογών και cache,\n" +
              "πρέπει να ενεργοποιήσεις Πρόσβαση Χρήσης.\n\n" +
              "Χωρίς αυτήν, η λειτουργία θα είναι περιορισμένη."
            : "To display application and cache sizes,\n" +
              "Usage Access permission is required.\n\n" +
              "Without it, functionality will be limited.";

    b.setTitle(AppLang.isGreek(this)
            ? "Απαιτείται Πρόσβαση Χρήσης"
            : "Usage Access Required");

    b.setMessage(msg);

    b.setPositiveButton(
            AppLang.isGreek(this)
                    ? "Ενεργοποίηση"
                    : "Enable Usage Access",
            (d, w) -> {
                Intent intent =
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            });

    b.setNegativeButton(
            AppLang.isGreek(this)
                    ? "Συνέχεια με περιορισμούς"
                    : "Continue Limited",
            (d, w) -> d.dismiss()
    );

    b.show();
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

        applyFiltersAndSort();
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

            if (st != null) {
                e.appBytes = st.getAppBytes();
                e.cacheBytes = st.getCacheBytes();
            }

        } catch (Throwable ignored) {}
    }

    // ============================================================
    // FILTER + SORT
    // ============================================================

    private void applyFiltersAndSort() {

        new Thread(() -> {

            ArrayList<AppEntry> users = new ArrayList<>();
            ArrayList<AppEntry> systems = new ArrayList<>();

            for (AppEntry e : allApps) {

                if (!TextUtils.isEmpty(search)) {
                    String s = search.toLowerCase(Locale.US);
                    if (!e.label.toLowerCase(Locale.US).contains(s) &&
                        !e.pkg.toLowerCase(Locale.US).contains(s))
                        continue;
                }

                if (e.isSystem) systems.add(e);
                else users.add(e);
            }

            Comparator<AppEntry> comp = sortByCacheBiggest ?
                    (a,b)->Long.compare(b.cacheBytes,a.cacheBytes)
                    : this::alphaCompare;

            Collections.sort(users, comp);
            Collections.sort(systems, comp);

            ArrayList<AppEntry> temp = new ArrayList<>();

            if (!users.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.isUserHeader = true;
                h.headerTitle = userExpanded ?
                        "📱 USER APPS ▼" :
                        "📱 USER APPS ►";
                temp.add(h);
                if (userExpanded) temp.addAll(users);
            }

            if (!systems.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.isSystemHeader = true;
                h.headerTitle = systemExpanded ?
                        "⚙ SYSTEM APPS ▼" :
                        "⚙ SYSTEM APPS ►";
                temp.add(h);
                if (systemExpanded) temp.addAll(systems);
            }

            runOnUiThread(() -> {
                visible.clear();
                visible.addAll(temp);
                refreshUI();
            });

        }).start();
    }

    private void refreshUI() {
        adapter.submitList(new ArrayList<>(visible));
        updateStats();
    }

    private void updateStats() {
        int total = allApps.size();
        int vis = 0;
        int sel = 0;

        for (AppEntry e : visible) {
            if (!e.isHeader) {
                vis++;
                if (e.selected) sel++;
            }
        }

        txtStats.setText("Total: " + total +
                "   Visible: " + vis +
                "   Selected: " + sel);
    }

    private int alphaCompare(AppEntry a, AppEntry b) {
        Collator c = Collator.getInstance(Locale.getDefault());
        c.setStrength(Collator.PRIMARY);
        return c.compare(a.label, b.label);
    }

    // ============================================================
    // GUIDED MODE
    // ============================================================

    private void startGuided() {
        guidedQueue.clear();
        guidedIndex = 0;

        for (AppEntry e : visible)
            if (!e.isHeader && e.selected)
                guidedQueue.add(e.pkg);

        if (guidedQueue.isEmpty()) {
            showGelDialog("No apps selected");
            return;
        }

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

        Intent intent = isUninstallMode ?
                new Intent(Intent.ACTION_DELETE) :
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.parse("package:" + pkg));
        startActivity(intent);
    }

    private void advanceGuided() {
        if (!guidedActive) return;
        guidedIndex++;
        openNext();
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
    // GEL DIALOG
    // ============================================================

    private void showGelDialog(String message) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);

        LinearLayout box = new LinearLayout(this);
        box.setPadding(40,40,40,40);
        box.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#101010"));
        bg.setCornerRadius(30);
        bg.setStroke(4, Color.parseColor("#FFD700"));
        box.setBackground(bg);

        TextView tv = new TextView(this);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(16f);
        tv.setText(message);
        tv.setGravity(Gravity.CENTER);

        box.addView(tv);
        b.setView(box);
        b.setPositiveButton("OK", null);
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
        boolean isUserHeader;
        boolean isSystemHeader;
        String headerTitle;
    }
}
