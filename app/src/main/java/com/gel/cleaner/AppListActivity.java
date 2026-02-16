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
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
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

    // STATS (FROM XML PANEL)
    private TextView txtStatsTotal;
    private TextView txtStatsUsers;
    private TextView txtStatsSystem;
    private TextView txtStatsSelected;
    
    private boolean returnedFromUsageScreen = false;

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
        
updateStartButtonUI();

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
    btnGuided.setOnClickListener(v -> {

        if (isUninstallMode) {
            showUninstallConfirmDialog();
        } else {
            startGuided();
        }

    });
}
}

@Override
protected void onResume() {
    super.onResume();

    // 1️⃣ Αν μόλις γυρίσαμε από Usage screen
    if (returnedFromUsageScreen) {
        returnedFromUsageScreen = false;

        if (hasUsageAccess()) {
            new Thread(this::loadAllApps).start();
        }

        return; // ⛔ ΜΗΝ συνεχίσεις άλλο
    }

    // 2️⃣ Κανονική ροή
    if (!hasUsageAccess()) {
        return; // ⛔ ΜΗΝ ξαναδείξεις dialog
    }

    if (allApps.isEmpty()) {
        new Thread(this::loadAllApps).start();
    }

    if (guidedActive) {
        advanceGuided();
    }
}

private void updateStartButtonUI() {

    Button startBtn = findViewById(R.id.btnGuidedClean);
    if (startBtn == null) return;

    boolean gr = AppLang.isGreek(this);

    if (isUninstallMode) {

        startBtn.setText(
                gr
                        ? "🗑 Απεγκατάσταση επιλεγμένων εφαρμογών"
                        : "🗑 Uninstall selected apps"
        );

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFFD00000); // GEL red
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(3), 0xFFFFD700);

        startBtn.setBackground(bg);
        startBtn.setTextColor(Color.WHITE);

    } else {

        startBtn.setText(
                gr
                        ? "Έναρξη καθοδηγούμενου καθαρισμού"
                        : "Start Guided Cleaning"
        );

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF39FF14); // neon green
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(3), 0xFFFFD700);

        startBtn.setBackground(bg);
        startBtn.setTextColor(Color.BLACK);
    }
}

private void showUninstallConfirmDialog() {

    boolean gr = AppLang.isGreek(this);

    new AlertDialog.Builder(this)
            .setTitle(gr ? "Επιβεβαίωση" : "Confirmation")
            .setMessage(gr
                    ? "Θέλεις να απεγκαταστήσεις τις επιλεγμένες εφαρμογές;"
                    : "Do you want to uninstall the selected apps?")
            .setPositiveButton(gr ? "ΝΑΙ" : "YES", (d, w) -> startGuided())
            .setNegativeButton(gr ? "ΑΚΥΡΟ" : "CANCEL", null)
            .show();
}

    // ============================================================
    // USAGE ACCESS
    // ============================================================

    private void checkUsageAccessGate() {

    if (!hasUsageAccess()) {
        showUsageAccessDialog();
    } else {
        new Thread(this::loadAllApps).start();
    }
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

private void showNextAppToast() {
    Toast.makeText(
            this,
            AppLang.isGreek(this)
                    ? "Πάτησε Πίσω για να συνεχίσεις στην επόμενη εφαρμογή."
                    : "Press Back to continue to the next app.",
            Toast.LENGTH_LONG
    ).show();
}

private void updateStartButtonText() {

    Button startBtn = findViewById(R.id.btnGuidedClean);

    if (isUninstallMode) {
        startBtn.setText(
                AppLang.isGreek(this)
                        ? "Απεγκατάσταση επιλεγμένων εφαρμογών"
                        : "Uninstall selected apps"
        );
    } else {
        startBtn.setText(
                AppLang.isGreek(this)
                        ? "Έναρξη καθοδηγούμενου καθαρισμού"
                        : "Start Guided Cleaning"
        );
    }
}

    // ============================================================
    // LOAD APPS
    // ============================================================

    // ============================================================
// LOAD APPS
// ============================================================

private void loadAllApps() {

    PackageManager pm = getPackageManager();

    synchronized (this) {
        allApps.clear();
        visible.clear();   // 🔥 ΠΟΛΥ ΣΗΜΑΝΤΙΚΟ
    }

    for (ApplicationInfo ai : pm.getInstalledApplications(0)) {

        AppEntry e = new AppEntry();
        e.pkg = ai.packageName;
        e.label = String.valueOf(pm.getApplicationLabel(ai));
        e.isSystem = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

        e.appBytes = -1;
        e.cacheBytes = -1;

        fillSizes(e);

        allApps.add(e);
    }

    // ❗ UI update ΠΑΝΤΑ στο main thread
    runOnUiThread(this::applyFiltersAndSort);
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
// FILTER + SORT (STABLE - NO DUPLICATES)
// ============================================================

private void applyFiltersAndSort() {

    // Snapshot once to avoid concurrent modification
    final ArrayList<AppEntry> snapshot = new ArrayList<>(allApps);

    new Thread(() -> {

        ArrayList<AppEntry> users   = new ArrayList<>();
        ArrayList<AppEntry> systems = new ArrayList<>();

        String s = (search == null) ? "" : search.toLowerCase(Locale.US);

        for (AppEntry e : snapshot) {
        	
        // 🔥 Hide 0-cache apps ONLY in cache mode
if (!isUninstallMode) {
    if (e.cacheBytes <= 0) continue;
}

            if (e == null) continue;
            if (e.isHeader) continue; // 🔒 NEVER process headers

            if (!TextUtils.isEmpty(s)) {
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

        final ArrayList<AppEntry> rebuilt = new ArrayList<>();

        // ---- USER HEADER ----
        if (!users.isEmpty()) {

            AppEntry h = new AppEntry();
            h.isHeader = true;
            h.isUserHeader = true;
            h.headerTitle = userExpanded
                    ? "📱 USER APPS ▼"
                    : "📱 USER APPS ►";

            rebuilt.add(h);

            if (userExpanded) rebuilt.addAll(users);
        }

        // ---- SYSTEM HEADER ----
        if (!systems.isEmpty()) {

            AppEntry h = new AppEntry();
            h.isHeader = true;
            h.isSystemHeader = true;
            h.headerTitle = systemExpanded
                    ? "⚙ SYSTEM APPS ▼"
                    : "⚙ SYSTEM APPS ►";

            rebuilt.add(h);

            if (systemExpanded) rebuilt.addAll(systems);
        }

        runOnUiThread(() -> {

            // 🔒 HARD RESET
            visible.clear();
            visible.addAll(rebuilt);

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
        showGelDialog(
                AppLang.isGreek(this)
                        ? "Η διαδικασία ολοκληρώθηκε."
                        : "Operation finished."
        );
        return;
    }

    String pkg = guidedQueue.get(guidedIndex);

    Intent intent = isUninstallMode
            ? new Intent(Intent.ACTION_DELETE)
            : new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

    intent.setData(Uri.parse("package:" + pkg));

    try {

        guidedIndex++;            // ✅ προχωράμε πρώτα index
        startActivity(intent);    // ✅ μία μόνο φορά
        showNextAppToast();       // ✅ ενημέρωση χρήστη

    } catch (Throwable t) {

        showGelDialog(
                AppLang.isGreek(this)
                        ? "Αδυναμία ανοίγματος ρυθμίσεων."
                        : "Cannot open settings."
        );

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
// GEL DIALOG (Dark-Gold + Neon Body)
// ============================================================
private void showGelDialog(String message) {

    final boolean gr = AppLang.isGreek(this);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(false);

    // ================= ROOT =================
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(22), dp(18), dp(22), dp(16));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);          // Dark background
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(3), 0xFFFFD700);  // Gold border
    root.setBackground(bg);

    // ================= TITLE =================
    TextView title = new TextView(this);
    title.setText(message);
    title.setTextColor(Color.WHITE);
    title.setTextSize(17f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(14));

    root.addView(title);

    // ================= NEON BODY =================
    LinearLayout neonBox = new LinearLayout(this);
    neonBox.setPadding(dp(18), dp(16), dp(18), dp(16));
    neonBox.setGravity(Gravity.CENTER);

    GradientDrawable neonBg = new GradientDrawable();
    neonBg.setColor(0xFF39FF14);      // Neon green
    neonBg.setCornerRadius(dp(14));
    neonBg.setStroke(dp(2), 0xFFFFD700);

    neonBox.setBackground(neonBg);

    TextView body = new TextView(this);
    body.setText(
            gr
                    ? "Η διαδικασία ολοκληρώθηκε επιτυχώς."
                    : "Operation completed successfully."
    );
    body.setTextColor(Color.BLACK);
    body.setTextSize(15f);
    body.setGravity(Gravity.CENTER);

    neonBox.addView(body);
    root.addView(neonBox);

    // ================= OK BUTTON (GEL STYLE) =================
    Button okBtn = new Button(this);
    okBtn.setText("OK");
    okBtn.setAllCaps(false);
    okBtn.setTextColor(Color.WHITE);
    okBtn.setTextSize(16f);
    okBtn.setTypeface(null, Typeface.BOLD);
    okBtn.setPadding(dp(18), dp(12), dp(18), dp(12));

    GradientDrawable okBg = new GradientDrawable();
    okBg.setColor(0xFF0F8A3B);       // Dark green
    okBg.setCornerRadius(dp(14));
    okBg.setStroke(dp(2), 0xFFFFD700);

    okBtn.setBackground(okBg);

    LinearLayout.LayoutParams lpBtn =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
    lpBtn.setMargins(0, dp(16), 0, 0);
    okBtn.setLayoutParams(lpBtn);

    root.addView(okBtn);

    b.setView(root);

    AlertDialog d = b.create();

    // REMOVE default white corners
    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    okBtn.setOnClickListener(v -> d.dismiss());

    d.show();
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
