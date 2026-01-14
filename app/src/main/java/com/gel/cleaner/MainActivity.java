// GDiolitsis Engine Lab (GEL) — Author & Developer
// MainActivity — FINAL CLEAN EDITION

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
    // PREFS
    // ================================
    private static final String PREFS = "gel_prefs";
    private static final String KEY_PLATFORM = "platform_mode"; // "android" | "apple"

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

        txtLogs = findViewById(R.id.txtLogs);
        scroll  = findViewById(R.id.scrollRoot);

        applySavedLanguage();
        setupLangButtons();
        setupDonate();
        setupButtons();

        // 🔥 ΠΑΝΤΑ δείχνουμε welcome → platform select
        startPlatformFlow();

        // 🍎 APPLE MODE — UI FILTER
        if (isAppleMode()) {
            applyAppleModeUI();
        }

        log("📱 Device ready", false);
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
    // PLATFORM / WELCOME FLOW — ALWAYS SHOW
    // =========================================================
    private void startPlatformFlow() {
        showWelcomePopup();
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
                    showPlatformSelectPopup();
                })
                .show();
    }

    // =========================================================
    // PLATFORM SELECT POPUP — GEL DARK-GOLD STYLE
    // =========================================================
    private void showPlatformSelectPopup() {

        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        AlertDialog.Builder b =
        new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);

        // ================= ROOT =================
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF000000);          // 🖤 μαύρο φόντο
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(3), 0xFFFFD700);  // ✨ χρυσό περίβλημα
        box.setBackground(bg);

        // ================= TITLE =================
        TextView title = new TextView(this);
        title.setText(getString(R.string.platform_select_title));
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(16));
        box.addView(title);

        // ================= ANDROID BUTTON =================
        Button btnAndroid = new Button(this);
        stylePopupButton(btnAndroid, getString(R.string.platform_android));
        box.addView(btnAndroid);

        // ================= APPLE BUTTON =================
        Button btnApple = new Button(this);
        stylePopupButton(btnApple, getString(R.string.platform_apple));
        box.addView(btnApple);

        b.setView(box);
        final AlertDialog d = b.create();

        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

        d.setCancelable(false);
        d.show();

        // ================= ACTIONS =================
        btnAndroid.setOnClickListener(v -> {
            prefs.edit().putString(KEY_PLATFORM, "android").apply();
            d.dismiss();
            recreate(); // refresh UI
        });

        btnApple.setOnClickListener(v -> {
            prefs.edit().putString(KEY_PLATFORM, "apple").apply();
            d.dismiss();

            // 🔥 ΑΜΕΣΗ ΜΕΤΑΒΑΣΗ ΣΤΟ iPhone UI
            Intent i = new Intent(this, IPhoneLabsActivity.class);
            startActivity(i);
            finish();
        });
    }

    // =========================================================
    // 🍎 APPLE MODE — UI FILTER (FINAL)
    // =========================================================
    private void applyAppleModeUI() {

        // ❌ ANDROID SECTIONS
        hide(R.id.section_system);
        hide(R.id.section_clean);
        hide(R.id.section_junk);
        hide(R.id.section_performance);

        // ❌ ANDROID BUTTONS
        hide(R.id.btnCpuRamLive);
        hide(R.id.btnCleanAll);
        hide(R.id.btnBrowserCache);
        hide(R.id.btnAppCache);

        // ❌ LOGS
        hide(R.id.txtLogs);

        // ✅ KEEP ONLY APPLE FLOW
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
    // POPUP BUTTON STYLE — DARK GOLD
    // =========================================================
    private void stylePopupButton(Button b, String text) {

        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(0xFFFFFFFF);
        b.setTextSize(16f);

        GradientDrawable g = new GradientDrawable();
        g.setColor(0xFF111111);
        g.setCornerRadius(dp(12));
        g.setStroke(dp(2), 0xFFFFD700);

        b.setBackground(g);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lp.setMargins(0, dp(8), 0, dp(8));
        b.setLayoutParams(lp);
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

        // 🍎 DEVICE DECLARATION — πάει στα iPhone LABS
        bind(R.id.btnAppleDeviceDeclaration,
        this::showAppleDeviceDeclarationPopup);

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
// 🍎 APPLE DEVICE DECLARATION
// =========================================================
private void showAppleDeviceDeclarationPopup() {

    AlertDialog.Builder b =
        new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);

    LinearLayout box = new LinearLayout(this);
    box.setOrientation(LinearLayout.VERTICAL);
    box.setPadding(dp(20), dp(20), dp(20), dp(20));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(3), 0xFFFFD700);
    box.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("Select your Apple device");
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(16));
    box.addView(title);

    Button btnIphone = new Button(this);
    stylePopupButton(btnIphone, "iPhone");

    Button btnIpad = new Button(this);
    stylePopupButton(btnIpad, "iPad");

    box.addView(btnIphone);
    box.addView(btnIpad);

    b.setView(box);
    AlertDialog d = b.create();
    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    d.show();

    btnIphone.setOnClickListener(v -> {
        d.dismiss();
        showAppleModelPicker("iphone");
    });

    btnIpad.setOnClickListener(v -> {
        d.dismiss();
        showAppleModelPicker("ipad");
    });
}

private void showAppleModelPicker(String type) {

    String[] models;

    if ("iphone".equals(type)) {
        models = new String[]{
                "iPhone 8", "iPhone X", "iPhone XR",
                "iPhone 11", "iPhone 12", "iPhone 13",
                "iPhone 14", "iPhone 15"
        };
    } else {
        models = new String[]{
                "iPad 7", "iPad 8", "iPad 9",
                "iPad Air 4", "iPad Air 5",
                "iPad Pro 11", "iPad Pro 12.9"
        };
    }

    new AlertDialog.Builder(this)
            .setTitle("Select model")
            .setItems(models, (d, which) -> {

                String model = models[which];
                saveAppleDevice(type, model);

                // 🔥 ΠΑΜΕ ΣΤΑ iPhone LABS
                startActivity(
                        new Intent(this, IPhoneLabsActivity.class)
                );
            })
            .show();
}

private void saveAppleDevice(String type, String model) {
    SharedPreferences prefs =
            getSharedPreferences("gel_prefs", MODE_PRIVATE);

    prefs.edit()
            .putString("apple_device_type", type)   // iphone / ipad
            .putString("apple_device_model", model) // π.χ. iPhone 13
            .apply();
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
}
