// GDiolitsis Engine Lab (GEL) — Author & Developer
// MainActivity — Unified, Clean, Locale-Safe Edition
// -------------------------------------------------------
// ✔ Συμβατό με LocaleHelper v3.0
// ✔ Αφαιρεμένα τα διπλά prefs (χρησιμοποιεί μόνο gel_lang_pref)
// ✔ 100% ασφαλές recreate()
// ✔ ΟΛΟ το αρχείο έτοιμο για copy-paste (κανόνας Γιώργου)
// -------------------------------------------------------

package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GELCleaner.LogCallback {

    private TextView txtLogs;
    private ScrollView scroll;

    // =========================================================
    // LOCALE (LocaleHelper v3.0)
    // =========================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base)); // 🔥 merged system
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLogs = findViewById(R.id.txtLogs);
        scroll  = findViewById(R.id.scrollRoot);

        applySavedLanguage();       // auto apply
        setupLangButtons();

        setupDonate();
        setupButtons();

        log("📱 Device ready", false);
    }

    // =========================================================
    // LANGUAGE SYSTEM (Clean + Unified)
    // =========================================================
    private void setupLangButtons() {
        View bGR = findViewById(R.id.btnLangGR);
        View bEN = findViewById(R.id.btnLangEN);

        if (bGR != null)
            bGR.setOnClickListener(v -> changeLang("el"));

        if (bEN != null)
            bEN.setOnClickListener(v -> changeLang("en"));
    }

    private void changeLang(String code) {
        LocaleHelper.set(this, code);   // save language properly
        recreate();                     // reload UI safely
    }

    private void applySavedLanguage() {
        String code = LocaleHelper.getLang(this);
        LocaleHelper.set(this, code);
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

        // SAFE DEEP CLEAN
        bind(R.id.btnCleanAll,
                () -> GELCleaner.deepClean(this, this));

        // BROWSER CACHE
        bind(R.id.btnBrowserCache,
                this::showBrowserPicker);

        // APP CACHE
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

        // DIAGNOSIS MENU
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
                "com.android.chrome",
                "com.chrome.beta",
                "org.mozilla.firefox",
                "com.opera.browser",
                "com.microsoft.emmx",
                "com.brave.browser",
                "com.vivaldi.browser",
                "com.duckduckgo.mobile.android",
                "com.sec.android.app.sbrowser",
                "com.mi.globalbrowser",
                "com.miui.hybrid",
                "com.android.browser"
        };

        List<String> installed = new ArrayList<>();

        for (String pkg : candidates) {
            try {
                pm.getPackageInfo(pkg, 0);
                installed.add(pkg);
            } catch (Exception ignored) {}
        }

        if (installed.isEmpty()) {
            Toast.makeText(this, "No browsers installed.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (installed.size() == 1) {
            openAppInfo(installed.get(0));
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    // LOGGING
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
}
