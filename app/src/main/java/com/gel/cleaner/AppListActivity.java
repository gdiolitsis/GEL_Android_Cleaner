// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppListActivity — FINAL STABLE GEL BUILD (Cache + Uninstall + Toggles + Stats Panel)

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

    // STATS (FROM XML PANEL)
    private TextView txtStatsTotal;
    private TextView txtStatsUsers;
    private TextView txtStatsSystem;
    private TextView txtStatsSelected;

    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visible = new ArrayList<>();
    private AppListAdapter adapter;

    private String search = "";
    private boolean sortByCacheBiggest = false;
    private boolean isUninstallMode = false;

    private boolean userExpanded = true;
    private boolean systemExpanded = true;

    // TOGGLE STATES
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

        adapter = new AppListAdapter(this);
        recyclerView.setAdapter(adapter);

        // STATS PANEL (XML)
        txtStatsTotal    = findViewById(R.id.txtStatsTotal);
        txtStatsUsers    = findViewById(R.id.txtStatsUsers);
        txtStatsSystem   = findViewById(R.id.txtStatsSystem);
        txtStatsSelected = findViewById(R.id.txtStatsSelected);

        EditText searchBox     = findViewById(R.id.searchBar);
        Button btnSelectAll    = findViewById(R.id.btnSelectAll);
        Button btnSortCache    = findViewById(R.id.btnSortCache);
        Button btnSelectUsers  = findViewById(R.id.btnSelectUsers);
        Button btnSelectSystem = findViewById(R.id.btnSelectSystem);
        Button btnGuided       = findViewById(R.id.btnGuidedClean);

        // MODE
        String mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "cache";
        isUninstallMode = "uninstall".equalsIgnoreCase(mode);

        // Permission prompt (Usage Access = sizes)
        checkUsageAccessGate();

        // SEARCH
        if (searchBox != null) {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search = (s == null) ? "" : s.toString().trim();
                    applyFiltersAndSort();
                }
            });
        }

        // SORT (TOGGLE)
        if (btnSortCache != null) {
            btnSortCache.setOnClickListener(v -> {
                sortByCacheBiggest = !sortByCacheBiggest;
                applyFiltersAndSort();
            });
        }

        // GLOBAL SELECT (TOGGLE)
        if (btnSelectAll != null) {
            btnSelectAll.setOnClickListener(v -> {
                allSelected = !allSelected;

                for (AppEntry e : visible) {
                    if (e == null || e.isHeader) continue;
                    e.selected = allSelected;
                }

                // When global select is used, category toggles become "unknown" -> recompute
                syncToggleStatesFromSelection();

                btnSelectAll.setText(allSelected
                        ? getString(R.string.deselect_all)
                        : getString(R.string.select_all));

                refreshUI();
            });

            // initial wording
            btnSelectAll.setText(getString(R.string.select_all));
        }

        // USER APPS SELECT (TOGGLE)
        if (btnSelectUsers != null) {
            btnSelectUsers.setOnClickListener(v -> {
                usersSelected = !usersSelected;

                for (AppEntry e : visible) {
                    if (e == null || e.isHeader) continue;
                    if (!e.isSystem) e.selected = usersSelected;
                }

                // Recompute global toggle too
                syncToggleStatesFromSelection();

                btnSelectUsers.setText(usersSelected
                        ? getString(R.string.deselect_user_apps)
                        : getString(R.string.select_user_apps));

                refreshUI();
            });

            btnSelectUsers.setText(getString(R.string.select_user_apps));
        }

        // SYSTEM APPS SELECT (TOGGLE)
        if (btnSelectSystem != null) {
            btnSelectSystem.setOnClickListener(v -> {
                systemSelected = !systemSelected;

                for (AppEntry e : visible) {
                    if (e == null || e.isHeader) continue;
                    if (e.isSystem) e.selected = systemSelected;
                }

                // Recompute global toggle too
                syncToggleStatesFromSelection();

                btnSelectSystem.setText(systemSelected
                        ? getString(R.string.deselect_system_apps)
                        : getString(R.string.select_system_apps));

                refreshUI();
            });

            btnSelectSystem.setText(getString(R.string.select_system_apps));
        }

        // GUIDED ACTION
        if (btnGuided != null) {
            btnGuided.setOnClickListener(v -> startGuided());
        }

        // Initial load
        new Thread(this::loadAllApps).start();
    }

@Override
protected void onResume() {
    super.onResume();

    if (hasUsageAccess()) {
        new Thread(this::loadAllApps).start();
    }

    if (guidedActive) {
        advanceGuided();
    }
}

    // ============================================================
    // USAGE ACCESS
    // ============================================================

    private void checkUsageAccessGate() {

    if (!hasUsageAccess()) {
        showUsageAccessDialog();
        return;
    }

    // Access granted → load apps normally
    new Thread(this::loadAllApps).start();
}

    private void showUsageAccessDialog() {

        if (hasUsageAccess()) return;

        final boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b = new AlertDialog.Builder(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(18), dp(22), dp(16));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        TextView title = new TextView(this);
        title.setText(gr ? "Απαιτείται Πρόσβαση Χρήσης" : "Usage Access Required");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18f);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(10));

        TextView msg = new TextView(this);
        msg.setText(gr
                ? "Για να εμφανίζονται τα μεγέθη εφαρμογών και cache,\nενεργοποίησε «Πρόσβαση Χρήσης».\n\nΧωρίς αυτό, θα δεις λίστα αλλά χωρίς μεγέθη."
                : "To show app + cache sizes,\nplease enable Usage Access.\n\nWithout it, the list works but sizes stay empty.");
        msg.setTextColor(0xFFDDDDDD);
        msg.setTextSize(14.5f);
        msg.setGravity(Gravity.CENTER);

        root.addView(title);
        root.addView(msg);

        b.setView(root);
        b.setCancelable(false);

        b.setPositiveButton(gr ? "Enable Usage Access" : "Enable Usage Access", (d, w) -> {
            try {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            } catch (Throwable ignored) {}
        });

        b.setNegativeButton(gr ? "Συνέχεια" : "Continue", (d, w) -> d.dismiss());

        b.show();
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

            // sizes
            e.appBytes = -1;
            e.cacheBytes = -1;
            fillSizes(e);

            allApps.add(e);
        }

        applyFiltersAndSort();
    }

    private void fillSizes(AppEntry e) {

        if (e == null) return;
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

    // ============================================================
    // FILTER + SORT
    // ============================================================

    private void applyFiltersAndSort() {

        new Thread(() -> {

            ArrayList<AppEntry> users = new ArrayList<>();
            ArrayList<AppEntry> systems = new ArrayList<>();

            ArrayList<AppEntry> snapshot = new ArrayList<>(allApps);

for (AppEntry e : snapshot) {

                if (e == null) continue;

                if (!TextUtils.isEmpty(search)) {
                    String s = search.toLowerCase(Locale.US);
                    String name = (e.label == null) ? "" : e.label.toLowerCase(Locale.US);
                    String pkg  = (e.pkg == null) ? "" : e.pkg.toLowerCase(Locale.US);
                    if (!name.contains(s) && !pkg.contains(s)) continue;
                }

                if (e.isSystem) systems.add(e);
                else users.add(e);
            }

            Comparator<AppEntry> comp = sortByCacheBiggest
                    ? (a, b) -> {
                        long ca = (a == null) ? -1 : a.cacheBytes;
                        long cb = (b == null) ? -1 : b.cacheBytes;
                        if (ca < 0 && cb < 0) return alphaCompare(a, b);
                        if (ca < 0) return 1;
                        if (cb < 0) return -1;
                        int c = Long.compare(cb, ca);
                        return (c != 0) ? c : alphaCompare(a, b);
                    }
                    : this::alphaCompare;

            Collections.sort(users, comp);
            Collections.sort(systems, comp);

            ArrayList<AppEntry> temp = new ArrayList<>();

            if (!users.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.isUserHeader = true;
                h.headerTitle = userExpanded ? "📱 USER APPS ▼" : "📱 USER APPS ►";
                temp.add(h);
                if (userExpanded) temp.addAll(users);
            }

            if (!systems.isEmpty()) {
                AppEntry h = new AppEntry();
                h.isHeader = true;
                h.isSystemHeader = true;
                h.headerTitle = systemExpanded ? "⚙ SYSTEM APPS ▼" : "⚙ SYSTEM APPS ►";
                temp.add(h);
                if (systemExpanded) temp.addAll(systems);
            }

            runOnUiThread(() -> {
                visible.clear();
                visible.addAll(temp);

                // IMPORTANT: after rebuild, toggles must reflect current selection
                syncToggleStatesFromSelection();

                refreshUI();
            });

        }).start();
    }

    private int alphaCompare(AppEntry a, AppEntry b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;

        Collator c = Collator.getInstance(Locale.getDefault());
        c.setStrength(Collator.PRIMARY);

        String la = (a.label == null) ? "" : a.label;
        String lb = (b.label == null) ? "" : b.label;

        int cmp = c.compare(la, lb);
        if (cmp != 0) return cmp;

        String pa = (a.pkg == null) ? "" : a.pkg;
        String pb = (b.pkg == null) ? "" : b.pkg;

        return pa.compareToIgnoreCase(pb);
    }

    // ============================================================
    // UI REFRESH + STATS (THIS IS THE SAFE POINT)
    // ============================================================

    private void refreshUI() {
        adapter.submitList(new ArrayList<>(visible));
        updateStats();   // 🔥 ΧΩΡΙΣ ΑΥΤΟ θα δείχνει 0
    }

    private void updateStats() {

        int total = allApps.size();
        int visibleCount = 0;
        int selectedCount = 0;
        int userCount = 0;
        int systemCount = 0;

        for (AppEntry e : allApps) {
            if (e == null) continue;
            if (e.isSystem) systemCount++;
            else userCount++;
        }

        for (AppEntry e : visible) {
            if (e == null) continue;
            if (!e.isHeader) {
                visibleCount++;
                if (e.selected) selectedCount++;
            }
        }

        // Update XML panel
        if (txtStatsTotal != null) {
            txtStatsTotal.setText("Total Apps: " + total + "   (Visible: " + visibleCount + ")");
        }
        if (txtStatsUsers != null) {
            txtStatsUsers.setText("User Apps: " + userCount);
        }
        if (txtStatsSystem != null) {
            txtStatsSystem.setText("System Apps: " + systemCount);
        }
        if (txtStatsSelected != null) {
            txtStatsSelected.setText("Selected: " + selectedCount);
        }
    }

    // ============================================================
    // TOGGLE SYNC (prevents “select says deselect” mismatch)
    // ============================================================

    private void syncToggleStatesFromSelection() {

        int selectable = 0;
        int selected = 0;

        int userSelectable = 0;
        int userSelected = 0;

        int sysSelectable = 0;
        int sysSelected = 0;

        for (AppEntry e : visible) {
            if (e == null || e.isHeader) continue;

            selectable++;
            if (e.selected) selected++;

            if (e.isSystem) {
                sysSelectable++;
                if (e.selected) sysSelected++;
            } else {
                userSelectable++;
                if (e.selected) userSelected++;
            }
        }

        allSelected = (selectable > 0 && selected == selectable);
        usersSelected = (userSelectable > 0 && userSelected == userSelectable);
        systemSelected = (sysSelectable > 0 && sysSelected == sysSelectable);

        // Update button wording if buttons exist
        Button btnSelectAll = findViewById(R.id.btnSelectAll);
        if (btnSelectAll != null) {
            btnSelectAll.setText(allSelected ? getString(R.string.deselect_all) : getString(R.string.select_all));
        }

        Button btnSelectUsers = findViewById(R.id.btnSelectUsers);
        if (btnSelectUsers != null) {
            btnSelectUsers.setText(usersSelected ? getString(R.string.deselect_user_apps) : getString(R.string.select_user_apps));
        }

        Button btnSelectSystem = findViewById(R.id.btnSelectSystem);
        if (btnSelectSystem != null) {
            btnSelectSystem.setText(systemSelected ? getString(R.string.deselect_system_apps) : getString(R.string.select_system_apps));
        }
    }

    // ============================================================
    // GUIDED MODE
    // ============================================================

    private void startGuided() {

        guidedQueue.clear();
        guidedIndex = 0;

        for (AppEntry e : visible) {
            if (e == null || e.isHeader) continue;
            if (e.selected) guidedQueue.add(e.pkg);
        }

        if (guidedQueue.isEmpty()) {
            showGelDialog(AppLang.isGreek(this) ? "Δεν έχεις επιλέξει εφαρμογές." : "No apps selected.");
            return;
        }

        guidedActive = true;
        openNext();
    }

    private void openNext() {

        if (guidedIndex >= guidedQueue.size()) {
            guidedActive = false;
            showGelDialog(AppLang.isGreek(this) ? "Η διαδικασία ολοκληρώθηκε." : "Operation finished.");
            return;
        }

        String pkg = guidedQueue.get(guidedIndex);

        Intent intent = isUninstallMode
                ? new Intent(Intent.ACTION_DELETE)
                : new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        intent.setData(Uri.parse("package:" + pkg));

        try {
            startActivity(intent);
        } catch (Throwable t) {
            showGelDialog(AppLang.isGreek(this) ? "Αδυναμία ανοίγματος ρυθμίσεων." : "Cannot open settings.");
            guidedIndex++;
            openNext();
        }
    }

    private void advanceGuided() {
        if (!guidedActive) return;
        guidedIndex++;
        openNext();
    }

    // ============================================================
    // GEL DIALOG (Dark-Gold)
    // ============================================================

    private void showGelDialog(String message) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);

        LinearLayout box = new LinearLayout(this);
        box.setPadding(dp(22), dp(18), dp(22), dp(16));
        box.setGravity(Gravity.CENTER);
        box.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(3), 0xFFFFD700);
        box.setBackground(bg);

        TextView tv = new TextView(this);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(15.5f);
        tv.setText(message);
        tv.setGravity(Gravity.CENTER);

        box.addView(tv);
        b.setView(box);
        b.setPositiveButton("OK", (d, w) -> d.dismiss());
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
