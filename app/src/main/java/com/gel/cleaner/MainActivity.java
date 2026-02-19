// GDiolitsis Engine Lab (GEL) - Author & Developer
// MainActivity - STABLE FINAL
// NOTE: Always return full file ready for copy-paste (no patch-only replies).

package com.gel.cleaner;

import com.gel.cleaner.iphone.*;
import com.gel.cleaner.base.*;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.Manifest;
import android.content.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class MainActivity extends GELAutoActivityHook
        implements GELCleaner.LogCallback {

    // =========================================================
    // STATE
    // =========================================================
    private boolean welcomeShown = false;
    private int permissionIndex = 0;

private TextView welcomeTitle;
private TextView welcomeMessage;

private TextView permissionsTitle;
private TextView permissionsMsg;

    private static final int REQ_PERMISSIONS = 1001;
    private static final String PREF_PERMISSIONS_DISABLED = "permissions_disabled";
    private boolean permissionsSkippedThisLaunch = false;
    
    private final String[] REQUIRED_PERMISSIONS = new String[]{

        Manifest.permission.RECORD_AUDIO,          // Mic Labs
        Manifest.permission.CAMERA,                // Camera Labs
        Manifest.permission.ACCESS_FINE_LOCATION,  // WiFi / BLE / sensors
        Manifest.permission.BLUETOOTH_CONNECT,     // Modern BT (API 31+)
        Manifest.permission.READ_EXTERNAL_STORAGE, // <= Android 12
        Manifest.permission.READ_MEDIA_IMAGES,     // Android 13+
        Manifest.permission.READ_MEDIA_VIDEO       // Android 13+
};

    private TextView txtLogs;
    private ScrollView scroll;

    // =========================================================
    // PREFS
    // =========================================================
    private static final String PREFS = "gel_prefs";
    private static final String KEY_PLATFORM = "platform_mode";

    // =========================================================
    // LOCALE
    // =========================================================
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

@Override
protected void onResume() {
    super.onResume();
}

    // =========================================================
    // ON CREATE
    // =========================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // BASIC BINDS
        txtLogs = findViewById(R.id.txtLogs);
        scroll  = findViewById(R.id.scrollRoot);

        applySavedLanguage();
        setupLangButtons();
        setupDonate();
        setupButtons();

        // RETURN BUTTON
        Button btnReturnAndroid = findViewById(R.id.btnReturnAndroid);
        if (btnReturnAndroid != null) {
            btnReturnAndroid.setSaveEnabled(false);
            btnReturnAndroid.setOnClickListener(v -> {
                if ("apple".equals(getSavedPlatform())) {
                    savePlatform("android");
                    applyAndroidModeUI();
                } else {
                    savePlatform("apple");
                    applyAppleModeUI();
                }
                syncReturnButtonText();
            });
        }

        Button btnUninstall = findViewById(R.id.btnAppManager);

        if (btnUninstall != null) {
            btnUninstall.setOnClickListener(v -> {

                Intent i = new Intent(this, AppListActivity.class);
                i.putExtra("mode", "uninstall");
                startActivity(i);

            });
        }

        View appManager = findViewById(R.id.btnAppManager);

        if (appManager != null) {

            appManager.setOnClickListener(v -> {
                try {

                    Intent i = new Intent(this, AppListActivity.class);
                    i.putExtra("mode", "uninstall");  
                    startActivity(i);

                } catch (Exception e) {
                    Toast.makeText(this,
                            "Cannot open App Manager",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // =========================================================
        // ENTRY FLOW (FIXED)
        // =========================================================
        permissionIndex = 0;

        boolean skipOnce = consumeSkipWelcomeOnce();

if (hasMissingPermissions()
        && !isPermissionsDisabled()
        && !skipOnce) {

    showPermissionsPopup();

} else {

    requestNextPermission();
}

        // APPLY PLATFORM UI
        if ("apple".equals(getSavedPlatform())) {
            applyAppleModeUI();
        } else {
            applyAndroidModeUI();
        }

        syncReturnButtonText();

        log("📱 Device ready", false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            AppTTS.stop();
        } catch (Throwable ignore) {}
    }

private void updatePermissionsTexts() {

    if (permissionsTitle == null || permissionsMsg == null) return;

    boolean gr = AppLang.isGreek(this);

    permissionsTitle.setText(
            gr ? "ΑΠΑΙΤΟΥΜΕΝΕΣ ΑΔΕΙΕΣ" : "REQUIRED PERMISSIONS"
    );

    permissionsMsg.setText(
            gr ? getPermissionsTextGR() : getPermissionsTextEN()
    );
}

    // ------------------------------------------------------------
    // MUTE ROW (UNIFIED - AppTTS HELPER)
    // ------------------------------------------------------------

    private LinearLayout buildMuteRow() {
        final boolean gr = AppLang.isGreek(this);
        LinearLayout row = new LinearLayout(this);

        row.setOrientation(LinearLayout.HORIZONTAL);

        row.setGravity(Gravity.CENTER_VERTICAL);

        row.setPadding(0, dp(8), 0, dp(16));

        CheckBox muteCheck = new CheckBox(this);
muteCheck.setChecked(AppTTS.isMuted(this));
muteCheck.setPadding(0, 0, dp(6), 0);

TextView label = new TextView(this);
label.setText(
        gr ? "Σίγαση φωνητικών οδηγιών"
           : "Mute voice instructions"
);

        label.setTextColor(Color.WHITE);
        label.setTextSize(14f);

        // --------------------------------------------------------
        // TOGGLE (ROW + LABEL CLICK)
        // --------------------------------------------------------

        View.OnClickListener toggle = v -> {
            boolean newState = !AppTTS.isMuted(this);
            AppTTS.setMuted(this, newState);
            muteCheck.setChecked(newState);

            //  Immediate hard stop when muting
            if (newState) {
                try { AppTTS.stop(); } catch (Throwable ignore) {}
            }
        };

        label.setOnClickListener(toggle);

        // --------------------------------------------------------
        // CHECKBOX DIRECT CHANGE
        // --------------------------------------------------------

        muteCheck.setOnCheckedChangeListener((button, checked) -> {
            if (checked == AppTTS.isMuted(this)) return;
            AppTTS.setMuted(this, checked);
            if (checked) {
                try { AppTTS.stop(); } catch (Throwable ignore) {}
            }
        });

        row.addView(muteCheck);
        row.addView(label);
        return row;
    }

    private boolean isPermissionsDisabled() {
        return getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean(PREF_PERMISSIONS_DISABLED, false);
    }

    private void disablePermissionsForever() {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_PERMISSIONS_DISABLED, true)
                .apply();
    }

    // ============================================================
    // PERMISSIONS POPUP - GEL STYLE (GLOBAL MUTE + LANG + TTS)
    // ============================================================
    private void showPermissionsPopup() {

        boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

        b.setCancelable(true);

    // ================= ROOT =================
LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(22), dp(24), dp(20));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF000000); // Μαύρο background
bg.setCornerRadius(dp(14));
bg.setStroke(dp(4), 0xFFFFD700); // Χρυσό περίγραμμα
root.setBackground(bg);

// ================= TITLE =================
TextView title = new TextView(this);
title.setText(gr ? "ΑΠΑΙΤΟΥΜΕΝΕΣ ΑΔΕΙΕΣ" : "REQUIRED PERMISSIONS");
title.setTextColor(Color.WHITE);
title.setTextSize(19f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER);
title.setPadding(0, 0, 0, dp(14));
root.addView(title);

// ================= MESSAGE =================
TextView msg = new TextView(this);
msg.setText(gr ? getPermissionsTextGR()
        : getPermissionsTextEN());
msg.setTextColor(0xFF00FF9C); // Neon green
msg.setTextSize(15f);
msg.setGravity(Gravity.CENTER);
msg.setLineSpacing(0f, 1.15f);
msg.setPadding(dp(6), 0, dp(6), dp(18));
root.addView(msg);

// 🔥 IMPORTANT
permissionsTitle = title;
permissionsMsg   = msg;

// ================= MUTE ROW =================
root.addView(buildMuteRow());

// ================= LANGUAGE SPINNER =================
Spinner langSpinner = new Spinner(MainActivity.this);

ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                new String[]{"EN", "GR"}
        ) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(Color.WHITE);          // selected text
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTextColor(Color.BLACK);          // 🔥 ΜΑΥΡΑ dropdown
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
                tv.setPadding(dp(14), dp(12), dp(14), dp(12));
                return tv;
            }
        };

adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

langSpinner.setAdapter(adapter);

langSpinner.setSelection(AppLang.isGreek(this) ? 1 : 0, false);

langSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {

                String code = (position == 0) ? "en" : "el";

                if (!code.equals(LocaleHelper.getLang(MainActivity.this))) {

                    LocaleHelper.set(MainActivity.this, code);

                    updatePermissionsTexts();

                    try { AppTTS.stop(); } catch (Throwable ignore) {}

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (!AppTTS.isMuted(MainActivity.this)) {
                            speakPermissionsTTS();
                        }
                    }, 120);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        }
);

// ================= LANGUAGE BOX (GOLD BORDER) =================
LinearLayout langBox = new LinearLayout(this);
langBox.setOrientation(LinearLayout.VERTICAL);
langBox.setGravity(Gravity.CENTER);
langBox.setPadding(dp(12), dp(10), dp(12), dp(10));

GradientDrawable langBg = new GradientDrawable();
langBg.setColor(0xFF111111);          // black-ish
langBg.setCornerRadius(dp(12));
langBg.setStroke(dp(3), 0xFFFFD700);  // gold border
langBox.setBackground(langBg);

langBox.addView(langSpinner);

LinearLayout.LayoutParams lpLang =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
lpLang.gravity = Gravity.CENTER;
lpLang.setMargins(0, 0, 0, dp(18));
langBox.setLayoutParams(lpLang);

root.addView(langBox);

// ================= CHECKBOX =================
CheckBox cb = new CheckBox(this);
cb.setText(gr ? "Να μην εμφανιστεί ξανά"
        : "Do not show again");
cb.setTextColor(Color.WHITE);
cb.setPadding(0, 0, 0, dp(18));
root.addView(cb);

// ================= BUTTON ROW =================
LinearLayout btnRow = new LinearLayout(this);
btnRow.setOrientation(LinearLayout.HORIZONTAL);
btnRow.setGravity(Gravity.CENTER);

LinearLayout.LayoutParams btnLp =
        new LinearLayout.LayoutParams(
                0,
                dp(110),
                1f
        );
btnLp.setMargins(dp(8), 0, dp(8), 0);

// -------- SKIP --------
Button skipBtn = new Button(this);
skipBtn.setText(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP");
skipBtn.setAllCaps(false);
skipBtn.setTextColor(Color.WHITE);
skipBtn.setTextSize(16f);
skipBtn.setTypeface(null, Typeface.BOLD);
skipBtn.setLayoutParams(btnLp);

GradientDrawable skipBg = new GradientDrawable();
skipBg.setColor(0xFFC62828); // Κόκκινο
skipBg.setCornerRadius(dp(12));
skipBg.setStroke(dp(3), 0xFFFFD700);
skipBtn.setBackground(skipBg);

// -------- CONTINUE --------
Button continueBtn = new Button(this);
continueBtn.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
continueBtn.setAllCaps(false);
continueBtn.setTextColor(Color.WHITE);
continueBtn.setTextSize(16f);
continueBtn.setTypeface(null, Typeface.BOLD);
continueBtn.setLayoutParams(btnLp);

GradientDrawable contBg = new GradientDrawable();
contBg.setColor(0xFF00E676); // Neon green
contBg.setCornerRadius(dp(12));
contBg.setStroke(dp(3), 0xFFFFD700);
continueBtn.setBackground(contBg);

btnRow.addView(skipBtn);
btnRow.addView(continueBtn);

root.addView(btnRow);

        b.setView(root);

        final AlertDialog d = b.create();

// 🔊 STOP TTS ON BACK BUTTON
d.setOnKeyListener((dialog, keyCode, event) -> {
    if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getAction() == KeyEvent.ACTION_UP) {

        try { AppTTS.stop(); } catch (Throwable ignore) {}
        dialog.dismiss();
        return true;
    }
    return false;
});

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        // -------------------------------------------------
        // STOP TTS ON DISMISS
        // -------------------------------------------------
        d.setOnDismissListener(dialog -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        });

        // -------------------------------------------------
        // STOP TTS ON BACK / CANCEL
        // -------------------------------------------------
        d.setOnCancelListener(dialog -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
        });

        // ================= ACTIONS =================
        continueBtn.setOnClickListener(v -> {

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    d.dismiss();

    permissionIndex = 0;
    requestNextPermission();
});

        skipBtn.setOnClickListener(v -> {

    permissionsSkippedThisLaunch = true;

    try { AppTTS.stop(); } catch (Throwable ignore) {}

    d.dismiss();

    // 🔥 Continue normal flow
    if (!isWelcomeDisabled() && !consumeSkipWelcomeOnce()) {
        showWelcomePopup();
    }
});

        // -------------------------------------------------
        // SHOW
        // -------------------------------------------------
        if (!isFinishing() && !isDestroyed()) {

            d.setOnShowListener(dialog -> {
                if (!AppTTS.isMuted(MainActivity.this)) {
                    speakPermissionsTTS();
                }
            });

            d.show();

            Window w = d.getWindow();
            if (w != null) {
                w.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                w.getDecorView().setPadding(dp(16), 0, dp(16), 0);
            }
        }
    }

// ============================================================
// PERMISSIONS TEXT - GR
// ============================================================
private String getPermissionsTextGR() {
    return "Η εφαρμογή χρειάζεται άδειες, για να πραγματοποιήσει "
            + "ελέγχους στη συσκευή σου.\n\n"
            + "Οι άδειες θα σου ζητηθούν μία-μία από το σύστημα Android.\n\n"
            + "Το σύστημα ενδέχεται να ζητήσει πρόσβαση σε:\n"
            + "• Τοποθεσία,\n"
            + "• Αποθήκευση,\n"
            + "• Τηλέφωνο,\n"
            + "• Μικρόφωνο.\n\n"
            + "• Κάμερα.\n\n"
            + "Δεν γίνεται καταγραφή ή αποθήκευση προσωπικών δεδομένων.";
}

// ============================================================
// PERMISSIONS TEXT - EN
// ============================================================
private String getPermissionsTextEN() {
    return "The application requires permissions to perform "
            + "diagnostic checks on your device.\n\n"
            + "Permissions will be requested one by one by the Android system.\n\n"
            + "The system may request access to:\n"
            + "• Location,\n"
            + "• Storage,\n"
            + "• Phone,\n"
            + "• Microphone.\n\n"
            + "• Camera.\n\n"
            + "No personal data is recorded or stored.";
}

    // =========================================================
    // REQUEST FLOW
    // =========================================================
    private void requestNextPermission() {

    while (permissionIndex < REQUIRED_PERMISSIONS.length) {

        String p = REQUIRED_PERMISSIONS[permissionIndex];

        if (ContextCompat.checkSelfPermission(this, p)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{p},
                    REQ_PERMISSIONS
            );
            return;
        }

        permissionIndex++;
    }

    if (!isWelcomeDisabled() && !consumeSkipWelcomeOnce()) {
        showWelcomePopup();
        return;
    }

    // Τέλος flow
}

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode != REQ_PERMISSIONS) return;

        permissionIndex++;
        requestNextPermission();
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private boolean hasMissingPermissions() {
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p)
                    != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void syncReturnButtonText() {
        Button b = findViewById(R.id.btnReturnAndroid);
        if (b != null) {
            b.setText("apple".equals(getSavedPlatform())
                    ? "RETURN TO ANDROID MODE"
                    : "RETURN TO APPLE MODE");
        }
    }

    private void setSkipWelcomeOnce(boolean v) {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean("skip_welcome_once", v)
                .apply();
    }

    private boolean consumeSkipWelcomeOnce() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean v = sp.getBoolean("skip_welcome_once", false);
        if (v) sp.edit().remove("skip_welcome_once").apply();
        return v;
    }

    private boolean isWelcomeDisabled() {
        return getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean("welcome_disabled", false);
    }

    private void disableWelcomeForever() {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putBoolean("welcome_disabled", true)
                .apply();
    }

    private void savePlatform(String mode) {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit().putString(KEY_PLATFORM, mode).apply();
    }

    private String getSavedPlatform() {
        return getSharedPreferences(PREFS, MODE_PRIVATE)
                .getString(KEY_PLATFORM, "android");
    }

    private boolean isAppleMode() {
        return "apple".equals(getSavedPlatform());
    }

    private AlertDialog.Builder buildNeonDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        return b;
    }

    private ArrayAdapter<String> neonAdapter(String[] names) {
        return new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                names
        );
    }

    // =========================================================
    // TTS - Permissions
    // =========================================================
    private void speakPermissionsTTS() {

        if (AppTTS.isMuted(this)) return;

        if (AppLang.isGreek(this)) {

            AppTTS.speak(
                    this,
                    getPermissionsTextGR()
            );

        } else {

            AppTTS.speak(
                    this,
                    getPermissionsTextEN()
            );
        }
    }

    // =========================================================
    // TTS - WELCOME
    // =========================================================
    private void speakWelcomeTTS() {

        if (!welcomeShown) return;
        if (AppTTS.isMuted(this)) return;

        if (AppLang.isGreek(this)) {

            AppTTS.speak(
                    this,
                    getWelcomeTextGR()
            );

        } else {

            AppTTS.speak(
                    this,
                    getWelcomeTextEN()
            );
        }
    }

    // =========================================================
    // WELCOME TEXT
    // =========================================================
    private String getWelcomeTextEN() {
    return
            "Although this is an Android application, " +
            "it is the only tool on the market that can also help you " +
            "understand problems on Apple devices.\n\n" +
            "By importing panic logs from your iPhone or iPad, " +
            "we analyze what really happened inside your device.\n\n" +
            "You will understand:\n" +
            "• what your panic logs mean.\n" +
            "• what caused the issue,\n" +
            "• and how you can solve it.\n\n" +
            "Choose what you want to explore:\n" +
            "your Android device or another Apple device.";
}

private String getWelcomeTextGR() {
    return
            "Παρότι αυτή είναι εφαρμογή Android, " +
            "είναι το μοναδικό εργαλείο στην αγορά που μπορεί να σε βοηθήσει " +
            "να καταλάβεις προβλήματα και σε συσκευές Apple.\n\n" +
            "Με την εισαγωγή panic logs από iPhone ή iPad, " +
            "αναλύουμε τι συνέβη πραγματικά μέσα στη συσκευή σου.\n\n" +
            "Θα καταλάβεις:\n" +
            "• τι σημαίνουν τα panic logs.\n" +
            "• τι προκάλεσε το πρόβλημα,\n" +
            "• και πώς μπορείς να το λύσεις.\n\n" +
            "Διάλεξε τι θέλεις να εξερευνήσεις:\n" +
            "τη συσκευή Android σου ή μια άλλη συσκευή Apple.";
}

private void updateWelcomeTexts() {

    boolean gr = AppLang.isGreek(this);

    if (welcomeTitle != null) {
        welcomeTitle.setText(
                gr ? "ΚΑΛΩΣ ΗΡΘΑΤΕ" : "WELCOME"
        );
    }

    if (welcomeMessage != null) {
        welcomeMessage.setText(
                gr ? getWelcomeTextGR() : getWelcomeTextEN()
        );
    }
}

    // =========================================================
    // DIMEN
    // =========================================================
    private int dp(float v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                getResources().getDisplayMetrics()
        );
    }

    // ------------------------------------------------------------
    // SHOW POPUP
    // ------------------------------------------------------------
    private void showWelcomePopup() {

if (welcomeShown) return;
welcomeShown = true;

        boolean gr = AppLang.isGreek(this);

        AlertDialog.Builder b =
                new AlertDialog.Builder(MainActivity.this);

        b.setCancelable(true);
        
  // ================= ROOT =================
LinearLayout root = new LinearLayout(MainActivity.this);
root.setOrientation(LinearLayout.VERTICAL);
root.setPadding(dp(24), dp(22), dp(24), dp(20));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF000000); // Μαύρο
bg.setCornerRadius(dp(14));
bg.setStroke(dp(4), 0xFFFFD700); // Χρυσό περίγραμμα
root.setBackground(bg);

// ================= TITLE =================
welcomeTitle = new TextView(MainActivity.this);
welcomeTitle.setText(
        AppLang.isGreek(this) ? "ΚΑΛΩΣ ΗΡΘΑΤΕ" : "WELCOME"
);
welcomeTitle.setTextColor(Color.WHITE);
welcomeTitle.setTextSize(19f);
welcomeTitle.setTypeface(null, Typeface.BOLD);
welcomeTitle.setGravity(Gravity.CENTER);
welcomeTitle.setPadding(0, 0, 0, dp(14));
root.addView(welcomeTitle);

// ================= MESSAGE =================
welcomeMessage = new TextView(MainActivity.this);
welcomeMessage.setText(
        AppLang.isGreek(this)
                ? getWelcomeTextGR()
                : getWelcomeTextEN()
);
welcomeMessage.setTextColor(0xFF00FF9C); // Neon green
welcomeMessage.setTextSize(15f);
welcomeMessage.setGravity(Gravity.CENTER);
welcomeMessage.setLineSpacing(0f, 1.15f);
welcomeMessage.setPadding(dp(6), 0, dp(6), dp(18));
root.addView(welcomeMessage); 

// ================= MUTE ROW =================
root.addView(buildMuteRow());

// ================= LANGUAGE SPINNER =================
Spinner langSpinner = new Spinner(MainActivity.this);

ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_spinner_item,
                new String[]{"EN", "GR"}
        ) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.WHITE);
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(Color.BLACK);   // dropdown μαύρο
                tv.setPadding(dp(14), dp(12), dp(14), dp(12));
                return tv;
            }
        };

adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

langSpinner.setAdapter(adapter);
langSpinner.setSelection(AppLang.isGreek(this) ? 1 : 0);

langSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {
                String newLang = (position == 0) ? "en" : "el";

                if (!newLang.equals(LocaleHelper.getLang(MainActivity.this))) {

                    LocaleHelper.set(MainActivity.this, newLang);
                    setSkipWelcomeOnce(true);

                    try { AppTTS.stop(); } catch (Throwable ignore) {}

                    // 🔥 Update texts
                    updateWelcomeTexts();

                    // 🔥 Speak again στη νέα γλώσσα
                    if (!AppTTS.isMuted(MainActivity.this)) {
                        speakWelcomeTTS();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        }
);

// ================= LANGUAGE BOX =================
LinearLayout langBox = new LinearLayout(MainActivity.this);
langBox.setOrientation(LinearLayout.VERTICAL);
langBox.setGravity(Gravity.CENTER);
langBox.setPadding(dp(12), dp(12), dp(12), dp(12));

GradientDrawable langBg = new GradientDrawable();
langBg.setColor(0xFF111111); // Σκούρο μαύρο
langBg.setCornerRadius(dp(10));
langBg.setStroke(dp(3), 0xFFFFD700); // Χρυσό
langBox.setBackground(langBg);

langBox.addView(langSpinner);

LinearLayout.LayoutParams lpLang =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
lpLang.gravity = Gravity.CENTER;
lpLang.setMargins(0, 0, 0, dp(18));
langBox.setLayoutParams(lpLang);

root.addView(langBox);

// ================= CHECKBOX =================
CheckBox cb = new CheckBox(this);
cb.setText(AppLang.isGreek(this)
        ? "Να μην εμφανιστεί ξανά"
        : "Do not show again");
cb.setTextColor(Color.WHITE);
cb.setPadding(0, dp(8), 0, dp(16));
root.addView(cb);

// ================= OK BUTTON =================
Button okBtn = new Button(MainActivity.this);
okBtn.setText("OK");
okBtn.setAllCaps(false);
okBtn.setTextColor(Color.WHITE);
okBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
okBtn.setTypeface(null, Typeface.BOLD);

GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF00E676); // Neon green
okBg.setCornerRadius(dp(12));
okBg.setStroke(dp(3), 0xFFFFD700); // Χρυσό περίγραμμα
okBtn.setBackground(okBg);

LinearLayout.LayoutParams okLp =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(140)
        );
okLp.setMargins(dp(6), dp(6), dp(6), 0);
okBtn.setLayoutParams(okLp);

root.addView(okBtn);

        // ================= SET VIEW =================
        b.setView(root);

        final AlertDialog d = b.create();

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        // --------------------------------------------
        // STATE BEFORE SHOW
        // --------------------------------------------
        welcomeShown = true;

        // --------------------------------------------
        // STOP ALWAYS ON DISMISS - CANCEL 
        // --------------------------------------------
        d.setOnDismissListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    welcomeShown = false;
});

d.setOnCancelListener(dialog -> {
    try { AppTTS.stop(); } catch (Throwable ignore) {}
    welcomeShown = false;
});

        // --------------------------------------------
        // SPEAK ONLY WHEN DIALOG IS ACTUALLY SHOWN
        // --------------------------------------------
        d.setOnShowListener(dialog -> {
            if (!AppTTS.isMuted(MainActivity.this)
                    && welcomeShown) {
                speakWelcomeTTS();
            }
        });

        // --------------------------------------------
        // SHOW
        // --------------------------------------------
        d.show();

        // --------------------------------------------
        // OK BUTTON
        // --------------------------------------------
        okBtn.setOnClickListener(v -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}

            welcomeShown = false;

            if (cb.isChecked()) {
                disableWelcomeForever();
            }

            d.dismiss();
            showPlatformSelectPopup();
        });
    }

    // =========================================================
    // PLATFORM SELECT - FINAL, CLEAN
    // =========================================================
    private void showPlatformSelectPopup() {

        boolean gr = AppLang.isGreek(this); 

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        MainActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

LinearLayout root = new LinearLayout(this);
root.setOrientation(LinearLayout.VERTICAL);
root.setGravity(Gravity.CENTER_HORIZONTAL);
root.setPadding(dp(24), dp(20), dp(24), dp(18));

GradientDrawable bg = new GradientDrawable();
bg.setColor(0xFF101010);
bg.setCornerRadius(dp(10));
bg.setStroke(dp(3), 0xFFFFD700);
root.setBackground(bg);

// ================= TITLE =================
TextView t = new TextView(this);
t.setText(gr ? "ΕΠΙΛΟΓΗ ΣΥΣΚΕΥΗΣ" : "SELECT DEVICE");
t.setTextColor(Color.WHITE);
t.setTextSize(20f);
t.setTypeface(null, Typeface.BOLD);
t.setGravity(Gravity.CENTER);
t.setPadding(0, 0, 0, dp(18));
root.addView(t);

// ================= ANDROID BUTTON =================
TextView androidBtn = new TextView(this);
androidBtn.setText(gr
        ? "🤖  Η ANDROID ΣΥΣΚΕΥΗ ΜΟΥ"
        : "🤖  MY ANDROID DEVICE");
androidBtn.setTextColor(Color.WHITE);
androidBtn.setTextSize(18f);
androidBtn.setTypeface(null, Typeface.BOLD);
androidBtn.setGravity(Gravity.CENTER);
androidBtn.setPadding(0, dp(20), 0, dp(20));

GradientDrawable bgAndroid = new GradientDrawable();
bgAndroid.setColor(0xFF3DDC84);
bgAndroid.setCornerRadius(dp(12));
bgAndroid.setStroke(dp(3), 0xFFFFD700);
androidBtn.setBackground(bgAndroid);

LinearLayout.LayoutParams lpBtn =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(110)
        );
lpBtn.setMargins(dp(8), dp(10), dp(8), 0);
androidBtn.setLayoutParams(lpBtn);

// ================= APPLE BUTTON =================
TextView appleBtn = new TextView(this);
appleBtn.setText(gr
        ? "🍎  ΑΛΛΗ ΣΥΣΚΕΥΗ APPLE"
        : "🍎  OTHER APPLE DEVICE");
appleBtn.setTextColor(Color.WHITE);
appleBtn.setTextSize(18f);
appleBtn.setTypeface(null, Typeface.BOLD);
appleBtn.setGravity(Gravity.CENTER);
appleBtn.setPadding(0, dp(20), 0, dp(20));

GradientDrawable bgApple = new GradientDrawable();
bgApple.setColor(0xFF1C1C1E);
bgApple.setCornerRadius(dp(12));
bgApple.setStroke(dp(3), 0xFFFFD700);
appleBtn.setBackground(bgApple);

LinearLayout.LayoutParams lpBtn2 =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(110)
        );
lpBtn2.setMargins(dp(8), dp(14), dp(8), 0);
appleBtn.setLayoutParams(lpBtn2);

root.addView(androidBtn);
root.addView(appleBtn);

        b.setView(root);
        final AlertDialog d = b.create();

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        // --------------------------------------------
        // STATE
        // --------------------------------------------
        welcomeShown = true;

        // --------------------------------------------
        // STOP ON DISMISS
        // --------------------------------------------
        d.setOnDismissListener(dialog -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
            welcomeShown = false;
        });

        // --------------------------------------------
        // STOP ON BACK (CANCEL)
        // --------------------------------------------
        d.setOnCancelListener(dialog -> {
            try { AppTTS.stop(); } catch (Throwable ignore) {}
            welcomeShown = false;
        });

        // --------------------------------------------
        // SHOW
        // --------------------------------------------
        d.show();

        // --------------------------------------------
        // WINDOW LAYOUT AFTER SHOW
        // --------------------------------------------
        Window w = d.getWindow();
        if (w != null) {
            w.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            w.getDecorView().setPadding(dp(16), 0, dp(16), 0);
        }

        // --------------------------------------------
        // ANDROID BUTTON
        // --------------------------------------------
        androidBtn.setOnClickListener(v -> {

            try { AppTTS.stop(); } catch (Throwable ignore) {}

            welcomeShown = false;

            savePlatform("android");

            setSkipWelcomeOnce(true);
            permissionsSkippedThisLaunch = true; 

            d.dismiss();

if ("apple".equals(getSavedPlatform())) {
    applyAppleModeUI();
} else {
    applyAndroidModeUI();
}

syncReturnButtonText();
        });

        // --------------------------------------------
        // APPLE BUTTON
        // --------------------------------------------
        appleBtn.setOnClickListener(v -> {

            try { AppTTS.stop(); } catch (Throwable ignore) {}

            welcomeShown = false;

            savePlatform("apple");

            setSkipWelcomeOnce(true);
            permissionsSkippedThisLaunch = true;  

            d.dismiss();
            recreate();
        });
    }

    // =========================================================
    // APPLE ENTRY POINT
    // =========================================================
    private void openAppleInternalPeripherals() {
    applyAppleModeUI();
}

    // =========================================================
    // ANDROID MODE UI FILTER
    // =========================================================
    private void applyAndroidModeUI() {

        hide(R.id.btnAppleDeviceDeclaration);

        show(R.id.section_system);
        show(R.id.section_clean);
        show(R.id.section_junk);
        show(R.id.section_performance);

        show(R.id.btnCpuRamLive);
        show(R.id.btnCleanAll);
        show(R.id.btnBrowserCache);
        show(R.id.btnAppCache);

        show(R.id.btnDonate);
        show(R.id.btnPhoneInfoInternal);
        show(R.id.btnPhoneInfoPeripherals);
        show(R.id.btnDiagnostics);

        // ANDROID DIAGNOSTICS - LOCALIZED + RESET STYLE
        View diagBtn = findViewById(R.id.btnDiagnostics);
        if (diagBtn instanceof TextView) {
            TextView tv = (TextView) diagBtn;
            tv.setText(R.string.diagnostics_android);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f); 
        }
    }

    // =========================================================
    // APPLE MODE UI FILTER
    // =========================================================
    private void applyAppleModeUI() {

        hide(R.id.section_system);
        hide(R.id.section_clean);
        hide(R.id.section_junk);
        hide(R.id.section_performance);

        hide(R.id.btnCpuRamLive);
        hide(R.id.btnCleanAll);
        hide(R.id.btnBrowserCache);
        hide(R.id.btnAppCache);

        hide(R.id.txtLogs);

        show(R.id.btnDonate);
        show(R.id.btnPhoneInfoInternal);
        show(R.id.btnPhoneInfoPeripherals);
        show(R.id.btnDiagnostics);
        show(R.id.btnAppleDeviceDeclaration);

        // APPLE DIAGNOSTICS - LOCALIZED + EMPHASIZED
        View v = findViewById(R.id.btnDiagnostics);
        if (v instanceof TextView) {
            TextView tv = (TextView) v;
            tv.setText(R.string.diagnostics_apple);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        }
    }

    private void hide(int id){
        View v = findViewById(id);
        if(v!=null) v.setVisibility(View.GONE);
    }

    private void show(int id){
        View v = findViewById(id);
        if(v!=null) v.setVisibility(View.VISIBLE);
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
        if (code.equals(LocaleHelper.getLang(this))) return;
        LocaleHelper.set(this, code);
        setSkipWelcomeOnce(true);
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
                    Toast.makeText(this,"Cannot open browser",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // =========================================================
    // BUTTONS - PLATFORM AWARE
    // =========================================================
    private void setupButtons() {

        bind(R.id.btnAppleDeviceDeclaration,
                this::showAppleDeviceDeclarationPopup);

        // ==========================
        //  INTERNAL INFO
        // ==========================
        bind(R.id.btnPhoneInfoInternal, () -> {
            if (isAppleMode()) {
                startActivity(new Intent(
                        this,
                        AppleDeviceInfoInternalActivity.class
                ));
            } else {
                startActivity(new Intent(
                        this,
                        DeviceInfoInternalActivity.class
                ));
            }
        });

        // ==========================
        //  PERIPHERALS INFO
        // ==========================
        bind(R.id.btnPhoneInfoPeripherals, () -> {
            if (isAppleMode()) {
                startActivity(new Intent(
                        this,
                        AppleDeviceInfoPeripheralsActivity.class
                ));
            } else {
                startActivity(new Intent(
                        this,
                        DeviceInfoPeripheralsActivity.class
                ));
            }
        });

// ==========================
// ⚙️ ΥΠΟΛΟΙΠΑ ΚΟΥΜΠΙΑ
// ==========================
bind(R.id.btnCpuRamLive,
        () -> startActivity(new Intent(this, CpuRamLiveActivity.class)));

        bind(R.id.btnCleanAll,
                () -> GELCleaner.deepClean(this,this));

        bind(R.id.btnBrowserCache,
                this::showBrowserPicker);

        View appCache = findViewById(R.id.btnAppCache);
        if (appCache != null) {

            appCache.setOnClickListener(v -> {
                try {

                    Intent i = new Intent(this, AppListActivity.class);
                    i.putExtra("mode", "cache");   // CACHE MODE
                    startActivity(i);

                } catch (Exception e) {
                    Toast.makeText(this, "Cannot open App List", Toast.LENGTH_SHORT).show();
                }
            });
        }

        bind(R.id.btnDiagnostics, () -> {
            startActivity(new Intent(
                    this,
                    DiagnosisMenuActivity.class
            ));
        });

    }

    // =========================================================
    // BIND HELPER
    // =========================================================
    private void bind(int id, Runnable fn){
        View b = findViewById(id);
        if(b!=null){
            b.setOnClickListener(v -> {
                try { fn.run(); }
                catch(Throwable t){
                    Toast.makeText(this,
                            "Action failed: "+t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // =========================================================
    //  APPLE DEVICE DECLARATION
    // =========================================================
    private void showAppleDeviceDeclarationPopup() {

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_Alert);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF000000);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("Select your Apple device");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(16));
        root.addView(title);

        // ==========================
        // ðŸ“± iPHONE BUTTON
        // ==========================
        Button iphoneBtn = new Button(this);
        iphoneBtn.setIncludeFontPadding(false);
        iphoneBtn.setText("📱  iPHONE");
        iphoneBtn.setAllCaps(false);
        iphoneBtn.setTextColor(Color.WHITE);
        iphoneBtn.setTextSize(16f);

        GradientDrawable iphoneBg = new GradientDrawable();
        iphoneBg.setColor(0xFF000000);
        iphoneBg.setCornerRadius(dp(10));
        iphoneBg.setStroke(dp(3), 0xFFFFD700);
        iphoneBtn.setBackground(iphoneBg);

        LinearLayout.LayoutParams lpIphone =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(72)
                );
        lpIphone.setMargins(0, dp(12), 0, 0);
        iphoneBtn.setLayoutParams(lpIphone);
        iphoneBtn.setPadding(dp(16), dp(14), dp(16), dp(14));

        // ==========================
        // ðŸ“² iPAD BUTTON
        // ==========================
        Button ipadBtn = new Button(this);
        ipadBtn.setIncludeFontPadding(false);
        ipadBtn.setText("📲  iPAD");
        ipadBtn.setAllCaps(false);
        ipadBtn.setTextColor(Color.WHITE);
        ipadBtn.setTextSize(16f);

        GradientDrawable ipadBg = new GradientDrawable();
        ipadBg.setColor(0xFF000000);
        ipadBg.setCornerRadius(dp(10));
        ipadBg.setStroke(dp(3), 0xFFFFD700);
        ipadBtn.setBackground(ipadBg);

        LinearLayout.LayoutParams lpIpad =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(72)
                );
        lpIpad.setMargins(0, dp(12), 0, 0);
        ipadBtn.setLayoutParams(lpIpad);
        ipadBtn.setPadding(dp(16), dp(14), dp(16), dp(14));

        // ==========================
        // ADD TO BOX
        // ==========================
        root.addView(iphoneBtn);
        root.addView(ipadBtn);

        b.setView(root);
        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

        d.show();

        // ==========================
        // ACTIONS
        // ==========================
        iphoneBtn.setOnClickListener(v -> {
            d.dismiss();
            showAppleModelPicker("iphone");
        });

        ipadBtn.setOnClickListener(v -> {
            d.dismiss();
            showAppleModelPicker("ipad");
        });
    }

    // =========================================================
    //  MODEL PICKER - GEL STYLE (FINAL)
    // =========================================================
    private void showAppleModelPicker(String type) {

        String[] models = "iphone".equals(type)
                ? new String[]{
                "iPhone 15",
                "iPhone 15 Pro",
                "iPhone 15 Pro Max",

                "iPhone 14",
                "iPhone 14 Pro",
                "iPhone 14 Pro Max",

                "iPhone 13",
                "iPhone 13 Pro",
                "iPhone 13 Pro Max",

                "iPhone 12",
                "iPhone 12 Pro",
                "iPhone 12 Pro Max",

                "iPhone 11",
                "iPhone 11 Pro",
                "iPhone 11 Pro Max"
        }
                : new String[]{
                "iPad Pro 11 (M2)",
                "iPad Pro 12.9 (M2)",
                "iPad Pro 11 (M1)",
                "iPad Pro 12.9 (M1)",
                "iPad Air 11 (M2)",
                "iPad Air 13 (M2)",
                "iPad Air (M1)",
                "iPad mini 6"
        };

        AlertDialog.Builder b =
                new AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_Alert);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFFFFD700);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(3), 0xFFFFD700);
        root.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("Select Apple Model");
        title.setTextColor(Color.WHITE);
       
        root.addView(title);

        ListView list = new ListView(this);
        list.setDivider(null);
        list.setDividerHeight(0);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        models
                ) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView tv = (TextView) super.getView(position, convertView, parent);
                        tv.setTextColor(0xFF00FF9C);
                        tv.setTextSize(16f);
                        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                        tv.setPadding(dp(14), dp(14), dp(14), dp(14));
                        tv.setBackground(null);
                        return tv;
                    }
                };

        list.setAdapter(adapter);
        root.addView(list);
        b.setView(root);

        AlertDialog d = b.create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

        d.show();

        // =========================
        // ACTION
        // =========================
        list.setOnItemClickListener((parent, view, position, id) -> {

            String rawModel = models[position];
            String normalizedModel = normalizeAppleModel(rawModel);

            saveAppleDevice(type, normalizedModel);

            TextView btn = findViewById(R.id.btnAppleDeviceDeclaration);
if (btn != null) {
    btn.setText("🍎 " + type.toUpperCase(Locale.US)
            + " — " + rawModel);
}

            Toast.makeText(
                    this,
                    "Selected: " + rawModel,
                    Toast.LENGTH_SHORT
            ).show();

            d.dismiss();
        });
    }

    // =========================================================
    // NORMALIZE APPLE MODEL - MATCH iPadSpecs / AppleSpecs
    // =========================================================
    private String normalizeAppleModel(String raw) {

        if (raw == null) return null;

        String m = raw.trim();

        // iPad Pro
        if (m.equals("iPad Pro 11 (M2)"))    return "iPad Pro 11 M2";
        if (m.equals("iPad Pro 12.9 (M2)"))  return "iPad Pro 12.9 M2";
        if (m.equals("iPad Pro 11 (M1)"))    return "iPad Pro 11 M1";
        if (m.equals("iPad Pro 12.9 (M1)"))  return "iPad Pro 12.9 M1";

        // iPad Air
        if (m.equals("iPad Air 11 (M2)"))    return "iPad Air 11 M2";
        if (m.equals("iPad Air 13 (M2)"))    return "iPad Air 13 M2";
        if (m.equals("iPad Air (M1)"))       return "iPad Air M1";

        // iPad mini
        if (m.equals("iPad mini 6"))         return "iPad mini 6";

        // iPhones are already correct
        return m;
    }

    // =========================================================
    // SAVE SELECTION (LOCKED KEYS)
    // =========================================================
    private void saveAppleDevice(String type, String model) {

        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putString("apple_type", type)
                .putString("apple_model", model)
                .apply();
    }

    // =========================================================
    // BROWSER PICKER - DYNAMIC (REAL BROWSERS ONLY)
    // =========================================================
    private void showBrowserPicker() {

        PackageManager pm = getPackageManager();

        // -----------------------------------------------------
        // FIND REAL BROWSERS
        // -----------------------------------------------------
        Map<String, String> apps = new LinkedHashMap<>();

        Intent browserIntent = new Intent(Intent.ACTION_MAIN);
        browserIntent.addCategory(Intent.CATEGORY_APP_BROWSER);

        List<ResolveInfo> browsers =
                pm.queryIntentActivities(browserIntent, 0);

        if (browsers != null) {
            for (ResolveInfo ri : browsers) {

                if (ri.activityInfo == null) continue;

                String pkg = ri.activityInfo.packageName;
                CharSequence label = ri.loadLabel(pm);

                if (pkg == null || label == null) continue;

                // verify http support
                Intent httpTest = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.example.com"));
                httpTest.setPackage(pkg);

                List<ResolveInfo> httpHandlers =
                        pm.queryIntentActivities(httpTest, 0);

                if (httpHandlers == null || httpHandlers.isEmpty())
                    continue;

                apps.put(label.toString(), pkg);
            }
        }

        // -----------------------------------------------------
        // HANDLE RESULTS
        // -----------------------------------------------------
        if (apps.isEmpty()) {
            Toast.makeText(this, "No browsers found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (apps.size() == 1) {
            openAppInfo(apps.values().iterator().next());
            return;
        }

        String[] names = apps.keySet().toArray(new String[0]);

        // -----------------------------------------------------
        // POPUP 
        // -----------------------------------------------------
        AlertDialog.Builder builder = buildNeonDialog();

        TextView title = new TextView(this);
        title.setText("Select Browser");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER); 
        title.setPadding(dp(16), dp(14), dp(16), dp(10));

        title.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        builder.setCustomTitle(title);

        builder.setAdapter(neonAdapter(names), (d, w) -> {
            String pkg = apps.get(names[w]);
            openAppInfo(pkg);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(0xFF000000);
            bg.setCornerRadius(dp(10));
            bg.setStroke(dp(3), 0xFFFFD700);
            window.setBackgroundDrawable(bg);
        }
    }

    // =========================================================
    // LOGGING,
    // =========================================================
    @Override
    public void log(String msg, boolean isError) {
        runOnUiThread(() -> {
            if (txtLogs == null) return;

            String prev = txtLogs.getText()==null ? "" : txtLogs.getText().toString();
            txtLogs.setText(prev.isEmpty()?msg:prev+"\n"+msg);

            if (scroll != null)
                scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    // =========================================================
    // OPEN APP INFO (for Browser Picker)
    // =========================================================
    private void openAppInfo(String pkg) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + pkg));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open App Info", Toast.LENGTH_SHORT).show();
        }
    }

}
