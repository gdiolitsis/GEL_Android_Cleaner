// GDiolitsis Engine Lab (GEL) — Author & Developer
// MainActivity — STABLE FINAL

package com.gel.cleaner;

import com.gel.cleaner.iphone.*;
import com.gel.cleaner.base.*;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private static final String PREF_POPUP_LANG = "popup_lang"; // "EN" | "GR"

    private static final int REQ_PERMISSIONS = 1001;
    private String permissionsLang = "EN";

    private final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private TextView txtLogs;
    private ScrollView scroll;

    private final TextToSpeech[] tts = new TextToSpeech[1];
    private final boolean[] ttsReady = new boolean[1];

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

    // ---------------------------------------------------------
    // TTS INIT
    // ---------------------------------------------------------
    tts[0] = new TextToSpeech(this, status -> {
        ttsReady[0] = (status == TextToSpeech.SUCCESS);
        if (ttsReady[0] && welcomeShown) {
            speakWelcomeTTS();
        }
    });

    // =========================================================
    // ENTRY FLOW (FIXED)
    // =========================================================
    permissionIndex = 0;

    if (hasMissingPermissions()) {
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

// ============================================================
// PERMISSIONS POPUP — GEL STYLE (GLOBAL MUTE + LANG + TTS)
// ============================================================
private void showPermissionsPopup() {

    permissionsLang = getPopupLang();
boolean gr = "GR".equals(permissionsLang);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    b.setCancelable(false);

    // ================= ROOT =================
    LinearLayout box = new LinearLayout(this);
    box.setOrientation(LinearLayout.VERTICAL);
    box.setPadding(dp(24), dp(20), dp(24), dp(18));

    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF101010);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(4), 0xFFFFD700);
    box.setBackground(bg);

    // ================= TITLE =================
    TextView title = new TextView(this);
    title.setText(gr ? "ΑΠΑΙΤΟΥΜΕΝΕΣ ΑΔΕΙΕΣ" : "REQUIRED PERMISSIONS");
    title.setTextColor(Color.WHITE);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
    box.addView(title);

    // ================= MESSAGE =================
    TextView msg = new TextView(this);
    msg.setTextColor(0xFFDDDDDD);
    msg.setTextSize(15f);
    msg.setGravity(Gravity.START);
    msg.setPadding(0, 0, 0, dp(16));
    msg.setText(gr ? getPermissionsTextGR()
                   : getPermissionsTextEN());
    box.addView(msg);

    // ================= GLOBAL MUTE ROW =================
    box.addView(buildMuteRow());

    // ================= LANGUAGE SPINNER =================
    Spinner langSpinner = new Spinner(this);

    ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    new String[]{"EN", "GR"}
            );
    adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
    );
    langSpinner.setAdapter(adapter);

    langSpinner.setSelection(gr ? 1 : 0);

    langSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                        AdapterView<?> parent,
                        View view,
                        int position,
                        long id
                ) {

                    permissionsLang = (position == 0) ? "EN" : "GR";
savePopupLang(permissionsLang);

                    msg.setText(
                            "GR".equals(permissionsLang)
                                    ? getPermissionsTextGR()
                                    : getPermissionsTextEN()
                    );

                    if (!AppTTS.isMuted(MainActivity.this)) {
                        speakPermissionsTTS();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            }
    );

    LinearLayout langBox = new LinearLayout(this);
    langBox.setPadding(dp(12), dp(12), dp(12), dp(12));

    GradientDrawable langBg = new GradientDrawable();
    langBg.setColor(0xFF1A1A1A);
    langBg.setCornerRadius(dp(12));
    langBg.setStroke(dp(2), 0xFFFFD700);
    langBox.setBackground(langBg);

    langBox.addView(langSpinner);

    LinearLayout.LayoutParams lpLang =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
    lpLang.setMargins(0, 0, 0, dp(18));
    langBox.setLayoutParams(lpLang);

    box.addView(langBox);

    // ================= BUTTON ROW =================
    LinearLayout btnRow = new LinearLayout(this);
    btnRow.setOrientation(LinearLayout.HORIZONTAL);
    btnRow.setGravity(Gravity.CENTER);

    LinearLayout.LayoutParams btnLp =
            new LinearLayout.LayoutParams(
                    0,
                    dp(60),
                    1f
            );
    btnLp.setMargins(dp(8), 0, dp(8), 0);

    // SKIP
    Button skipBtn = new Button(this);
    skipBtn.setText(gr ? "ΠΑΡΑΛΕΙΨΗ" : "SKIP");
    skipBtn.setAllCaps(false);
    skipBtn.setTextColor(Color.WHITE);

    GradientDrawable skipBg = new GradientDrawable();
    skipBg.setColor(0xFF8B0000);
    skipBg.setCornerRadius(dp(14));
    skipBg.setStroke(dp(3), 0xFFFFD700);
    skipBtn.setBackground(skipBg);
    skipBtn.setLayoutParams(btnLp);

    // CONTINUE
    Button continueBtn = new Button(this);
    continueBtn.setText(gr ? "ΣΥΝΕΧΕΙΑ" : "CONTINUE");
    continueBtn.setAllCaps(false);
    continueBtn.setTextColor(Color.WHITE);

    GradientDrawable contBg = new GradientDrawable();
    contBg.setColor(0xFF0B5F3B);
    contBg.setCornerRadius(dp(14));
    contBg.setStroke(dp(3), 0xFFFFD700);
    continueBtn.setBackground(contBg);
    continueBtn.setLayoutParams(btnLp);

    btnRow.addView(skipBtn);
    btnRow.addView(continueBtn);
    box.addView(btnRow);

    b.setView(box);

    final AlertDialog d = b.create();

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
    }

    // ================= ACTIONS =================
    continueBtn.setOnClickListener(v -> {
        d.dismiss();
        requestNextPermission();
    });

    skipBtn.setOnClickListener(v -> {
        d.dismiss();
        permissionIndex = REQUIRED_PERMISSIONS.length;
        requestNextPermission();
    });

    if (!isFinishing() && !isDestroyed()) {
        d.show();

        // 🔊 INITIAL TTS
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!AppTTS.isMuted(this)) {
                speakPermissionsTTS();
            }
        }, 150);
    }
}

private void savePopupLang(String lang) {
    getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putString(PREF_POPUP_LANG, lang)
            .apply();
}

private String getPopupLang() {

    SharedPreferences sp =
            getSharedPreferences(PREFS, MODE_PRIVATE);

    String saved = sp.getString(PREF_POPUP_LANG, null);

    if (saved != null) return saved;

    boolean gr = "el".equalsIgnoreCase(LocaleHelper.getLang(this));
    String def = gr ? "GR" : "EN";

    sp.edit().putString(PREF_POPUP_LANG, def).apply();

    return def;
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

    // END FLOW → WELCOME
    if (!consumeSkipWelcomeOnce() && !isWelcomeDisabled()) {
        showWelcomePopup();
    }
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
// TTS — Permissions
// =========================================================
private void speakPermissionsTTS() {

    try {
        if (tts[0] == null || !ttsReady[0]) return;

        // respect GLOBAL mute
        if (AppTTS.isMuted(MainActivity.this)) return;

        try { tts[0].stop(); } catch (Throwable ignore) {}

        if ("GR".equals(permissionsLang)) {
            try { tts[0].setLanguage(new Locale("el", "GR")); } catch (Throwable ignore) {}

            tts[0].speak(
                    getPermissionsTextGR(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "PERMISSIONS_GR"
            );

        } else {
            try { tts[0].setLanguage(Locale.US); } catch (Throwable ignore) {}

            tts[0].speak(
                    getPermissionsTextEN(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "PERMISSIONS_EN"
            );
        }

    } catch (Throwable ignore) {}
}

    // =========================================================
    // TTS — WELCOME
    // =========================================================
  
  private void speakWelcomeTTS() {

    try {
        if (tts[0] == null || !ttsReady[0]) return;
        if (AppTTS.isMuted(MainActivity.this)) return;

        tts[0].stop();

        if ("GR".equals(getPopupLang())) {

            tts[0].setLanguage(new Locale("el", "GR"));
            tts[0].speak(
                    getWelcomeTextGR(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "WELCOME_GR"
            );

        } else {

            tts[0].setLanguage(Locale.US);
            tts[0].speak(
                    getWelcomeTextEN(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "WELCOME_EN"
            );
        }

    } catch (Throwable ignore) {}
}

    // =========================================================
    // WELCOME TEXT
    // =========================================================
    private String getWelcomeTextEN() {
    return
        "Although this is an Android application, " +
        "it is the only tool at the market, that can also help you " +
        "understand problems on Apple devices.\n\n" +
        "By importing panic logs, from your iPhone or iPad, " +
        "we analyze, what really happened inside your device.\n\n" +
        "You will understand:\n" +
        "• what your panic logs mean.\n" +
        "• what caused the issue,\n" +
        "• and how you can solve it.\n\n" +
        "Choose what you want to explore:\n" +
        "your Android device, or an other Apple device?.";
}

private String getWelcomeTextGR() {
    return
        "Παρότι αυτή είναι εφαρμογή Android, " +
        "είναι το μοναδικό εργαλείο στην αγορά, που μπορεί να σε βοηθήσει " +
        "να καταλάβεις προβλήματα, και σε συσκευές Apple.\n\n" +
        "Με την εισαγωγή panic logs, από iPhone ή iPad, " +
        "αναλύουμε τι συνέβη πραγματικά μέσα στη συσκευή σου.\n\n" +
        "Θα καταλάβεις:\n" +
        "• τι σημαίνουν τα panic logs.\n" +
        "• τι προκάλεσε το πρόβλημα,\n" +
        "• και πώς μπορείς να το λύσεις.\n\n" +
        "Διάλεξε τι θέλεις να εξερευνήσεις:\n" +
        "τη συσκευή Android σου, ή μια άλλη συσκευή Apple?.";
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

// ============================================================
// WELCOME POPUP — LAB 28 STYLE (MUTE + LANG + TTS)  ✅FINAL
// ============================================================

// ------------------------------------------------------------
// SHOW POPUP
// ------------------------------------------------------------
private void showWelcomePopup() {
	
	String lang = getPopupLang();
boolean gr = "GR".equals(lang);

        AlertDialog.Builder b =
        new AlertDialog.Builder(MainActivity.this);
        
        b.setCancelable(true);

        // ================= ROOT =================
        LinearLayout box = new LinearLayout(MainActivity.this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        box.setBackground(bg);

        // ================= TITLE =================
        TextView title = new TextView(MainActivity.this);
        title.setText("WELCOME");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(12));
        box.addView(title);

        // ================= MESSAGE =================
        TextView msg = new TextView(MainActivity.this);
        msg.setTextColor(0xFFDDDDDD);
        msg.setTextSize(15f);
        msg.setGravity(Gravity.START);

        // αρχική γλώσσα από σύστημα
        String sys = LocaleHelper.getLang(MainActivity.this); // "el" | "en"
        welcomeLang = getPopupLang();
        msg.setText("GR".equals(welcomeLang) ? getWelcomeTextGR() : getWelcomeTextEN());

        msg.setPadding(0, 0, 0, dp(12));   // λίγο αέρα πριν τα controls
box.addView(msg);

// ==========================
// 🔕 GLOBAL MUTE (AppTTS)
// ==========================
box.addView(buildMuteRow());

        // ==========================
        // 🌐 LANGUAGE SPINNER
        // ==========================
        Spinner langSpinner = new Spinner(MainActivity.this);
        
        langSpinner.setMinimumHeight(dp(48));
langSpinner.setPadding(dp(12), dp(8), dp(12), dp(8));

        ArrayAdapter<String> langAdapter =
                new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"EN", "GR"}
                );
        langAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        langSpinner.setAdapter(langAdapter);

        // αρχική επιλογή
        if ("GR".equals(welcomeLang)) {
            langSpinner.setSelection(1);
            msg.setText(getWelcomeTextGR());
        } else {
            langSpinner.setSelection(0);
            msg.setText(getWelcomeTextEN());
        }

        langSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {

                        welcomeLang = (pos == 0) ? "EN" : "GR";
savePopupLang(welcomeLang);

                        if ("GR".equals(welcomeLang)) {
                            msg.setText(getWelcomeTextGR());
                        } else {
                            msg.setText(getWelcomeTextEN());
                        }

                        speakWelcomeTTS();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> p) { }
                });

// ==========================
// 🌐 LANGUAGE BOX — FINAL
// ==========================
LinearLayout langBox = new LinearLayout(MainActivity.this);
langBox.setOrientation(LinearLayout.HORIZONTAL);
langBox.setGravity(Gravity.CENTER);
langBox.setPadding(dp(12), 0, dp(12), 0);

GradientDrawable langBg = new GradientDrawable();
langBg.setColor(0xFF1A1A1A);
langBg.setCornerRadius(dp(12));
langBg.setStroke(dp(2), 0xFFFFD700);
langBox.setBackground(langBg);

LinearLayout.LayoutParams lpLangBox =
        new LinearLayout.LayoutParams(
                0,
                dp(88),      // ⬆️ ΑΠΟ 50 → 88
                1f
        );
lpLangBox.setMargins(dp(12), 0, 0, 0);
langBox.setLayoutParams(lpLangBox);

// spinner μέσα
LinearLayout.LayoutParams lpSpin =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
langSpinner.setLayoutParams(lpSpin);
langSpinner.setMinimumHeight(0);
langSpinner.setGravity(Gravity.CENTER_VERTICAL);
langSpinner.setPadding(dp(20), 0, dp(20), 0);

        langBox.addView(langSpinner);

        controls.addView(muteBtn);
        controls.addView(langBox);
        box.addView(controls);
        
CheckBox cb = new CheckBox(this);
cb.setText("Να μην εμφανιστεί ξανά");
cb.setTextColor(Color.WHITE);
cb.setPadding(0, dp(8), 0, dp(8));
box.addView(cb);

// ==========================
// OK BUTTON — FINAL (DOUBLE HEIGHT)
// ==========================
Button okBtn = new Button(MainActivity.this);
okBtn.setText("OK");
okBtn.setAllCaps(false);
okBtn.setTextColor(0xFFFFFFFF);
okBtn.setTextSize(20f);              
okBtn.setIncludeFontPadding(false); 
okBtn.setGravity(Gravity.CENTER);

// 🔓 ΞΕΚΛΕΙΔΩΜΑ ANDROID LIMITS
okBtn.setMinimumHeight(0);
okBtn.setMinHeight(0);
okBtn.setMinimumWidth(0);
okBtn.setMinWidth(0);

// ✅ ΟΠΤΙΚΟ ΥΨΟΣ ΑΠΟ PADDING (ΔΙΠΛΑΣΙΟ)
okBtn.setPadding(
        dp(24),   // left
        dp(18),   // top   ⬆️ ΔΙΠΛΑΣΙΟ
        dp(24),   // right
        dp(18)    // bottom ⬆️ ΔΙΠΛΑΣΙΟ
);

GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF0F8A3B);
okBg.setCornerRadius(dp(18));        // ⬆️ λίγο πιο “βαρύ”
okBg.setStroke(dp(3), 0xFFFFD700);
okBtn.setBackground(okBg);

// ✅ PRIMARY ACTION HEIGHT — ΔΙΠΛΑΣΙΟ
LinearLayout.LayoutParams lpOk =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(96)               // ⬅️ ΔΙΠΛΑΣΙΟ, ΤΕΛΟΣ
        );
lpOk.setMargins(0, dp(20), 0, 0);
okBtn.setLayoutParams(lpOk);

box.addView(okBtn);

// ==========================
// DIALOG
// ==========================
b.setView(box);
final AlertDialog d = b.create();

d.setOnDismissListener(dialog -> {
    welcomeShown = false;
    try {
        if (tts != null && tts[0] != null) tts[0].stop();
    } catch (Throwable ignore) {}
});

if (d.getWindow() != null) {
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT));
}

// ▶️ force TTS retry μόλις ανοίξει το dialog
d.show();

// 👇 το popup ΕΙΝΑΙ τώρα ορατό
welcomeShown = true;

// 🔒 φέρε το μπροστά
if (d.getWindow() != null) {
    d.getWindow().clearFlags(
            android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    );
    d.getWindow().addFlags(
            android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
    );
}

// ▶️ force TTS retry μόλις ανοίξει το dialog
new Handler(Looper.getMainLooper()).postDelayed(() -> {
    if (!AppTTS.isMuted(MainActivity.this) && ttsReady[0] && welcomeShown)
        speakWelcomeTTS();
    }
}, 120);

okBtn.setOnClickListener(v -> {

    if (cb.isChecked()) {
        disableWelcomeForever();
    }

    d.dismiss();
    showPlatformSelectPopup();
});
}

// =========================================================
// PLATFORM SELECT — FINAL, CLEAN
// =========================================================
private void showPlatformSelectPopup() {
	
	String lang = getPopupLang();
boolean gr = "GR".equals(lang);

    AlertDialog.Builder b =
            new AlertDialog.Builder(
                    MainActivity.this,
                    android.R.style.Theme_Material_Dialog_NoActionBar
            );

    LinearLayout box = new LinearLayout(this);
box.setOrientation(LinearLayout.VERTICAL);
box.setPadding(dp(24), dp(20), dp(24), dp(18));

box.setLayoutParams(
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        box.setBackground(bg);

        // ---------------- TITLE ----------------
TextView t = new TextView(this);
t.setText("SELECT DEVICE");
t.setTextColor(Color.WHITE);
t.setTextSize(18f);
t.setTypeface(null, Typeface.BOLD);
t.setGravity(Gravity.CENTER);
t.setPadding(
        0,
        dp(4),    // top
        0,
        dp(16)    // bottom
);
box.addView(t);

// =================================================
// 🤖 ANDROID "BUTTON" (TextView)
// =================================================

TextView androidBtn = new TextView(this);
androidBtn.setText(gr
        ? "🤖  Η ANDROID ΣΥΣΚΕΥΗ ΜΟΥ"
        : "🤖  MY ANDROID DEVICE");

androidBtn.setTextColor(0xFF000000); // black text on green
androidBtn.setTextSize(17f);
androidBtn.setTypeface(Typeface.DEFAULT_BOLD);
androidBtn.setGravity(Gravity.CENTER);
androidBtn.setClickable(true);
androidBtn.setFocusable(true);
androidBtn.setLetterSpacing(0.04f);

// badge padding
androidBtn.setPadding(dp(18), dp(20), dp(18), dp(20));

GradientDrawable bgAndroid = new GradientDrawable();
bgAndroid.setColor(0xFF3DDC84);   // official Android green
bgAndroid.setCornerRadius(dp(6)); // badge look
bgAndroid.setStroke(dp(3), 0xFFFFD700); // GEL gold
androidBtn.setBackground(bgAndroid);

LinearLayout.LayoutParams lpBtn =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(92)
        );
lpBtn.setMargins(dp(8), dp(18), dp(8), 0);
androidBtn.setLayoutParams(lpBtn);

// =================================================
// 🍎 APPLE "BUTTON" (TextView)
// =================================================
TextView appleBtn = new TextView(this);
appleBtn.setText(gr
        ? "🍎  ΑΛΛΗ ΣΥΣΚΕΥΗ APPLE"
        : "🍎  OTHER APPLE DEVICE");

appleBtn.setTextColor(Color.WHITE);
appleBtn.setTextSize(17f);
appleBtn.setTypeface(Typeface.DEFAULT_BOLD);
appleBtn.setGravity(Gravity.CENTER);
appleBtn.setClickable(true);
appleBtn.setFocusable(true);
appleBtn.setLetterSpacing(0.04f);

appleBtn.setPadding(dp(18), dp(20), dp(18), dp(20));

GradientDrawable bgApple = new GradientDrawable();
bgApple.setColor(0xFF1C1C1E);     // Apple system graphite
bgApple.setCornerRadius(dp(6));  // badge style
bgApple.setStroke(dp(3), 0xFFFFD700); // GEL gold
appleBtn.setBackground(bgApple);

LinearLayout.LayoutParams lpBtn2 =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(92)
        );
lpBtn2.setMargins(dp(8), dp(18), dp(8), 0);
appleBtn.setLayoutParams(lpBtn2);

    // ---------------- ADD ----------------
    box.addView(androidBtn);
    box.addView(appleBtn);

    b.setView(box);
    final AlertDialog d = b.create();

    if (d.getWindow()!=null)
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

    d.show();
    
    Window w = d.getWindow();
if (w != null) {
    w.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    );

    // ίδιο margin με τα μεγάλα κουμπιά
    w.getDecorView().setPadding(
            dp(16),  // left
            0,
            dp(16),  // right
            0
    );
}

// ---------------- ACTIONS ----------------
androidBtn.setOnClickListener(v -> {
    savePlatform("android");
    setSkipWelcomeOnce(true);
    d.dismiss();
    recreate();
});

appleBtn.setOnClickListener(v -> {
    savePlatform("apple");
    setSkipWelcomeOnce(true);
    d.dismiss();
    recreate();
});
}

    // =========================================================
    // 🍎 APPLE ENTRY POINT
    // =========================================================
    private void openAppleInternalPeripherals() {
    // ΜΗΝ ξανανοίγεις MainActivity
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

    // 🤖 ANDROID DIAGNOSTICS — LOCALIZED + RESET STYLE
View diagBtn = findViewById(R.id.btnDiagnostics);
if (diagBtn instanceof TextView) {
    TextView tv = (TextView) diagBtn;
    tv.setText(R.string.diagnostics_android);
    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f); // ⬆️ μεγαλύτερη
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

    // 🍎 APPLE DIAGNOSTICS — LOCALIZED + EMPHASIZED
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

    // 🔒 SAVE APP LANGUAGE FOR AppLang / TTS / LABS
    getSharedPreferences("gel_prefs", MODE_PRIVATE)
            .edit()
            .putString("app_lang", code)   // "el" | "en"
            .apply();

    setSkipWelcomeOnce(true);
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
                    Toast.makeText(this,"Cannot open browser",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

// =========================================================
// BUTTONS — PLATFORM AWARE
// =========================================================
private void setupButtons() {

    bind(R.id.btnAppleDeviceDeclaration,
            this::showAppleDeviceDeclarationPopup);

    // ==========================
    // 📱 INTERNAL INFO
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
    // 🔌 PERIPHERALS INFO
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
    if(appCache!=null){
        appCache.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, AppListActivity.class));
            } catch (Exception e) {
                Toast.makeText(this,"Cannot open App List",Toast.LENGTH_SHORT).show();
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
    title.setTextSize(20f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(16));
    box.addView(title);

    // ==========================
    // 📱 iPHONE BUTTON
    // ==========================
    Button iphoneBtn = new Button(this);
    iphoneBtn.setIncludeFontPadding(false);
    iphoneBtn.setText("📱  iPHONE");
    iphoneBtn.setAllCaps(false);
    iphoneBtn.setTextColor(Color.WHITE);
    iphoneBtn.setTextSize(16f);

    GradientDrawable iphoneBg = new GradientDrawable();
    iphoneBg.setColor(0xFF000000);
    iphoneBg.setCornerRadius(dp(14));
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
    // 📲 iPAD BUTTON
    // ==========================
    Button ipadBtn = new Button(this);
    ipadBtn.setIncludeFontPadding(false);
    ipadBtn.setText("📲  iPAD");
    ipadBtn.setAllCaps(false);
    ipadBtn.setTextColor(Color.WHITE);
    ipadBtn.setTextSize(16f);

    GradientDrawable ipadBg = new GradientDrawable();
    ipadBg.setColor(0xFF000000);
    ipadBg.setCornerRadius(dp(14));
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
    box.addView(iphoneBtn);
    box.addView(ipadBtn);

    b.setView(box);
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
// 🍎 MODEL PICKER — GEL STYLE (FINAL)
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
    bg.setColor(0xFF000000);
    bg.setCornerRadius(dp(18));
    bg.setStroke(dp(3), 0xFFFFD700);
    root.setBackground(bg);

    TextView title = new TextView(this);
    title.setText("Select Apple Model");
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(12));
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
// NORMALIZE APPLE MODEL — MATCH iPadSpecs / AppleSpecs
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
// BROWSER PICKER — DYNAMIC (REAL BROWSERS ONLY)
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
    // POPUP (ΔΕΝ ΤΟ ΠΕΙΡΑΖΟΥΜΕ)
    // -----------------------------------------------------
    AlertDialog.Builder builder = buildNeonDialog();

    TextView title = new TextView(this);
title.setText("Select Browser");
title.setTextColor(0xFFFFFFFF);
title.setTextSize(18f);
title.setTypeface(null, Typeface.BOLD);
title.setGravity(Gravity.CENTER); // ⬅️ ΚΕΝΤΡΟ
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
        bg.setCornerRadius(dp(18));
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
