// GDiolitsis Engine Lab (GEL) — Author & Developer
// MainActivity v4.2 — Foldable-Integrated + Locale-Safe + AutoDP Scaling
// 100% έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences; // ✅ ADDED
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GELAutoActivityHook
        implements GELCleaner.LogCallback {

    private TextView txtLogs;
    private ScrollView scroll;

    // ================================
    // PREFS (ADDED — δεν πειράζουμε τίποτα άλλο)
    // ================================
    private static final String PREFS = "gel_prefs";
    private static final String KEY_PLATFORM = "platform_mode"; // "android" | "apple"
    private static final String KEY_WELCOME_SHOWN = "welcome_shown";

    // =========================================================
    // LOCALE HOOK
    // =========================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    // =========================================================
    // ON CREATE
    // =========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        String platformMode =
                prefs.getString(KEY_PLATFORM, null);   // "android" | "apple" | null;

        boolean welcomeShown =
                prefs.getBoolean(KEY_WELCOME_SHOWN, false);

        txtLogs = findViewById(R.id.txtLogs);
        scroll  = findViewById(R.id.scrollRoot);

        applySavedLanguage();
        setupLangButtons();
        startPlatformFlow();
        setupDonate();
        setupButtons();
        
// ================================
// 🍎 APPLE MODE — UI FILTER
// ================================
if (isAppleMode()) {
    applyAppleModeUI();
}

        // ================================
        // ✅ NEW FLOW (ADDED)
        // ================================
        
        log("📱 Device ready", false);
    }
    
    private void showBatteryCapacityPopupIfNeeded() {
    // intentionally empty — removed feature, keep stub for compatibility
}

// =========================================================
// 🍎 APPLE MODE — UI FILTER
// =========================================================
private void applyAppleModeUI() {

    // ----- BUTTONS (ANDROID ONLY) -----
    hide(R.id.btnCpuRamLive);
    hide(R.id.btnCleanAll);
    hide(R.id.btnBrowserCache);
    hide(R.id.btnAppCache);

    // ----- OPTIONAL: Android logs / extras -----
    hide(R.id.txtLogs);

    // ----- KEEP ONLY APPLE FLOW -----
    show(R.id.btnDonate);
    show(R.id.btnPhoneInfoInternal);
    show(R.id.btnPhoneInfoPeripherals);
    show(R.id.btnDiagnostics);
    show(R.id.btnAppleDeviceDeclaration);
}

private void hide(int id) {
    View v = findViewById(id);
    if (v != null) v.setVisibility(View.GONE);
}

private void show(int id) {
    View v = findViewById(id);
    if (v != null) v.setVisibility(View.VISIBLE);
}

    // =========================================================
    // LANGUAGE SYSTEM
    // =========================================================
    private void setupLangButtons() {
        View bGR = findViewById(R.id.btnLangGR);
        View bEN = findViewById(R.id.btnLangEN);

        if (bGR != null) bGR.setOnClickListener(v -> changeLang("el"));
        if (bEN != null) bEN.setOnClickListener(v -> changeLang("en"));
    }

    private void changeLang(String code) {
        LocaleHelper.set(this, code);
        recreate();
    }

    private void applySavedLanguage() {
        String code = LocaleHelper.getLang(this);
        LocaleHelper.set(this, code);
    }

// =========================================================
// PLATFORM CHECK
// =========================================================
private boolean isAppleMode() {
    SharedPreferences prefs =
            getSharedPreferences(PREFS, MODE_PRIVATE);
    return "apple".equals(
            prefs.getString(KEY_PLATFORM, "android")
    );
}

// =========================================================
// PLATFORM / WELCOME FLOW
// =========================================================
private void startPlatformFlow() {

    SharedPreferences prefs =
            getSharedPreferences(PREFS, MODE_PRIVATE);

    boolean welcomeShown =
            prefs.getBoolean(KEY_WELCOME_SHOWN, false);

    String platformMode =
            prefs.getString(KEY_PLATFORM, null); // "android" | "apple" | null

    // 1️⃣ Αν δεν έχει δείξει welcome → δείξε το
    if (!welcomeShown) {
        showWelcomePopup();
        return;
    }

    // 2️⃣ Αν δεν έχει διαλέξει platform → δείξε επιλογή
    if (platformMode == null) {
        showPlatformSelectPopup();
        return;
    }

    // 3️⃣ Έτοιμο → προχώρα κανονικά
    continueNormalFlow();
}

private void showWelcomePopup() {

    new AlertDialog.Builder(
        this,
        android.R.style.Theme_Material_Dialog_Alert
)
            .setTitle(getString(R.string.platform_select_title))
            .setMessage(getString(R.string.welcome_popup_text))
            .setCancelable(false)
            .setPositiveButton("OK", (d, w) -> {

                SharedPreferences prefs =
                        getSharedPreferences(PREFS, MODE_PRIVATE);

                prefs.edit()
                        .putBoolean(KEY_WELCOME_SHOWN, true)
                        .apply();

                showPlatformSelectPopup();
            })
            .show();
}

private void continueNormalFlow() {

    SharedPreferences prefs =
            getSharedPreferences(PREFS, MODE_PRIVATE);

    String platformMode =
            prefs.getString(KEY_PLATFORM, "android");

    // ------------------------------------------------
    // ANDROID → δείχνουμε battery capacity popup
    // APPLE   → το παραλείπουμε τελείως
    // ------------------------------------------------
    if ("android".equals(platformMode)) {
        showBatteryCapacityPopupIfNeeded();
    }
}

    // =========================================================
    // DONATE
    // =========================================================
    private void setupDonate() {
        View b = findViewById(R.id.btnDonate);
        if (b != null) {
            b.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.paypal.com/paypalme/gdiolitsis")));
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open browser", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // =========================================================
    // BUTTON BINDINGS
    // =========================================================
    private void setupButtons() {

        bind(R.id.btnPhoneInfoInternal,
                () -> startActivity(new Intent(this, DeviceInfoInternalActivity.class)));

        bind(R.id.btnPhoneInfoPeripherals,
                () -> startActivity(new Intent(this, DeviceInfoPeripheralsActivity.class)));

        bind(R.id.btnCpuRamLive,
                () -> startActivity(new Intent(this, CpuRamLiveActivity.class)));

        bind(R.id.btnCleanAll,
                () -> GELCleaner.deepClean(this, this));

        bind(R.id.btnBrowserCache,
                this::showBrowserPicker);

        View appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {
            appCache.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, AppListActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open App List", Toast.LENGTH_SHORT).show();
                }
            });
        }

        bind(R.id.btnDiagnostics,
                () -> startActivity(new Intent(this, DiagnosisMenuActivity.class)));
    }

    private void bind(int id, Runnable fn) {
        View b = findViewById(id);
        if (b != null) {
            b.setOnClickListener(v -> {
                try { fn.run(); }
                catch (Throwable t) {
                    Toast.makeText(this,
                            "Action failed: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // =========================================================
    // BROWSER PICKER
    // =========================================================
    private void showBrowserPicker() {

        PackageManager pm = getPackageManager();

        String[] candidates = {
            "com.android.chrome","com.chrome.beta",
            "org.mozilla.firefox","com.opera.browser",
            "com.microsoft.emmx","com.brave.browser",
            "com.vivaldi.browser","com.duckduckgo.mobile.android",
            "com.sec.android.app.sbrowser","com.mi.globalbrowser",
            "com.miui.hybrid","com.android.browser"
        };

        List<String> installed = new ArrayList<>();

        for (String pkg : candidates) {
            try { pm.getPackageInfo(pkg, 0); installed.add(pkg); }
            catch (Exception ignored) {}
        }

        if (installed.isEmpty()) {
            Toast.makeText(this, "No browsers installed.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (installed.size() == 1) {
            openAppInfo(installed.get(0));
            return;
        }

        AlertDialog.Builder builder =
        new AlertDialog.Builder(
                this,
                android.R.style.Theme_Material_Dialog_Alert
        );
        builder.setTitle("Select Browser");
        String[] labels = installed.toArray(new String[0]);

        builder.setItems(labels, (dialog, which) ->
                openAppInfo(installed.get(which)));

        builder.show();
    }

    private void openAppInfo(String pkg) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + pkg));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open App Info", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================
    // LOGGING SYSTEM
    // =========================================================
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;

            String prev = txtLogs.getText() == null ? "" : txtLogs.getText().toString();
            txtLogs.setText(prev.isEmpty() ? msg : prev + "\n" + msg);

            if (scroll != null)
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

// =========================================================
// PLATFORM SELECT POPUP
// =========================================================
private void showPlatformSelectPopup() {

    SharedPreferences prefs =
            getSharedPreferences(PREFS, MODE_PRIVATE);

    String[] items = {
            getString(R.string.platform_android),
            getString(R.string.platform_apple)
    };

    new AlertDialog.Builder(
            this,
            android.R.style.Theme_Material_Dialog_Alert   // 🔥 FIX
    )
            .setTitle(getString(R.string.platform_select_title))
            .setCancelable(false)
            .setItems(items, (d, which) -> {

                String mode = (which == 0) ? "android" : "apple";

                prefs.edit()
                        .putString(KEY_PLATFORM, mode)
                        .apply();

                d.dismiss();
            })
            .show();
}
}
