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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class AppListActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private ListView list;

    // SECTION STATE
    private boolean userExpanded = true;
    private boolean systemExpanded = true;

    // Foldable
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

    // ============================================================
    // ON CREATE
    // ============================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cache);

        list = findViewById(R.id.listApps);
        EditText searchBox = findViewById(R.id.searchBar);

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

        adapter = new AppListAdapter(this, visible);
        list.setAdapter(adapter);

        // ================= CLICK =================
        list.setOnItemClickListener((parent, view, position, id) -> {

            if (position < 0 || position >= visible.size()) return;

            AppEntry e = visible.get(position);
            if (e == null) return;

            // HEADER TAP
            if (e.isHeader) {

                if (e.isUserHeader) {
                    userExpanded = !userExpanded;
                }

                if (e.isSystemHeader) {
                    systemExpanded = !systemExpanded;
                }

                applyFiltersAndSort();
                return;
            }

            // NORMAL APP TAP
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

        // Foldable init
        uiManager    = new GELFoldableUIManager(this);
        animPack     = new GELFoldableAnimationPack(this);
        dualPane     = new DualPaneManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        new Thread(this::loadAllApps).start();
    }

    // ============================================================
    // LOAD APPS
    // ============================================================
    private void loadAllApps() {
        try {
            PackageManager pm = getPackageManager();
            ArrayList<ApplicationInfo> apps =
                    new ArrayList<>(pm.getInstalledApplications(PackageManager.GET_META_DATA));

            allApps.clear();

            for (ApplicationInfo ai : apps) {
                if (ai == null) continue;

                AppEntry e = new AppEntry();
                e.pkg = ai.packageName;
                e.isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                e.label = String.valueOf(pm.getApplicationLabel(ai));
                e.ai = ai;

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
    // FILTER + SORT + HEADERS
    // ============================================================
    private void applyFiltersAndSort() {

        visible.clear();

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

            if (e.isSystem) {
                if (showSystem) systems.add(e);
            } else {
                if (showUser) users.add(e);
            }
        }

        Comparator<AppEntry> comparator =
                sortByCacheBiggest
                        ? (a, b) -> {
                            long ca = a.cacheBytes;
                            long cb = b.cacheBytes;

                            if (ca < 0 && cb < 0) return alphaCompare(a, b);
                            if (ca < 0) return 1;
                            if (cb < 0) return -1;

                            int cmp = Long.compare(cb, ca);
                            return cmp != 0 ? cmp : alphaCompare(a, b);
                        }
                        : this::alphaCompare;

        Collections.sort(users, comparator);
        Collections.sort(systems, comparator);

        if (!users.isEmpty()) {
            AppEntry header = new AppEntry();
            header.isHeader = true;
            header.isUserHeader = true;
            header.headerTitle = userExpanded
                    ? "📱 USER APPS (tap to collapse)"
                    : "📱 USER APPS (tap to expand)";
            visible.add(header);

            if (userExpanded) visible.addAll(users);
        }

        if (!systems.isEmpty()) {
            AppEntry header = new AppEntry();
            header.isHeader = true;
            header.isSystemHeader = true;
            header.headerTitle = systemExpanded
                    ? "⚙ SYSTEM APPS (tap to collapse)"
                    : "⚙ SYSTEM APPS (tap to expand)";
            visible.add(header);

            if (systemExpanded) visible.addAll(systems);
        }

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
    // OPEN SETTINGS
    // ============================================================
    private void openAppDetails(String pkg) {
        try {
            Intent intent =
                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
        long appBytes;
        long cacheBytes;
        ApplicationInfo ai;

        boolean isHeader;
        boolean isUserHeader;
        boolean isSystemHeader;
        String headerTitle;
    }
}
