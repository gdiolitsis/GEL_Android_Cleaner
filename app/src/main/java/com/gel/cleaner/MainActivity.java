// GDiolitsis Engine Lab (GEL) — Author & Developer
// MainActivity — STABLE FINAL

package com.gel.cleaner;

import com.gel.cleaner.iphone.AppleDeviceInfoInternalActivity;
import com.gel.cleaner.iphone.AppleDeviceInfoPeripheralsActivity;
import com.gel.cleaner.base.*;

import android.content.pm.ResolveInfo;

import java.util.LinkedHashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.Manifest;
import android.net.Uri;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.util.TypedValue;
import android.widget.*;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends GELAutoActivityHook
        implements GELCleaner.LogCallback {
        	
        private boolean welcomeShown = false;
                
  private static final int REQ_PERMISSIONS = 1001;

private final String[] REQUIRED_PERMISSIONS = new String[] {
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT
};

    // ==========================
    // STATE
    // ==========================

    private TextView txtLogs;
    private ScrollView scroll;

    // ==========================
    // TTS
    // ==========================
    private final TextToSpeech[] tts = new TextToSpeech[1];
    private final boolean[] ttsReady = new boolean[1];

// ==========================
// PREFS
// ==========================
private static final String PREFS = "gel_prefs";
private static final String KEY_PLATFORM = "platform_mode"; 

// ==========================
// WELCOME SKIP (ONE SHOT)
// ==========================

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

    // =====================================================
    // BASIC BINDS
    // =====================================================
    txtLogs = findViewById(R.id.txtLogs);
    scroll  = findViewById(R.id.scrollRoot);

    applySavedLanguage();
    setupLangButtons();
    setupDonate();
    setupButtons();
    
    if (!hasAllRequiredPermissions()) {
    showPermissionsGate();
}

    // =====================================================
    // RETURN BUTTON — TEXT + ACTION (IN-PLACE, LOCKED)
    // =====================================================
    Button btnReturnAndroid = findViewById(R.id.btnReturnAndroid);
    
    if (btnReturnAndroid != null) {    
}
    
    if (btnReturnAndroid != null) {
    btnReturnAndroid.setSaveEnabled(false);   // 🔒 ΜΗΝ αποθηκεύεις/επαναφέρεις text state
}

    if (btnReturnAndroid != null) {
        btnReturnAndroid.setOnClickListener(v -> {

            String currentMode = getSavedPlatform();

            if ("apple".equals(currentMode)) {
                savePlatform("android");
                applyAndroidModeUI();
            } else {
                savePlatform("apple");
                applyAppleModeUI();
            }

            syncReturnButtonText();
        });
    }

    // =====================================================
    // TTS INIT
    // =====================================================
    tts[0] = new TextToSpeech(this, status -> {
        ttsReady[0] = (status == TextToSpeech.SUCCESS);
        if (ttsReady[0] && welcomeShown) {
            speakWelcomeTTS();
        }
    });

// =====================================================
// APPLY SAVED PLATFORM UI + RETURN TEXT (FIRST DRAW)
// =====================================================
String mode = getSavedPlatform();

if ("apple".equals(mode)) {
    applyAppleModeUI();
} else {
    applyAndroidModeUI();
}

syncReturnButtonText();

boolean skipWelcome = consumeSkipWelcomeOnce();

if (!skipWelcome && !isWelcomeDisabled()) {
    showWelcomePopup();
}

    log("📱 Device ready", false);
}
 
   @Override
    protected void onDestroy() {
        try {
            if (tts[0] != null) tts[0].shutdown();
        } catch (Throwable ignore) {}
        super.onDestroy();
    }

private void syncReturnButtonText() {
    Button btnReturnAndroid = findViewById(R.id.btnReturnAndroid);
    if (btnReturnAndroid == null) {
        log("❌ btnReturnAndroid = NULL", true);
        return;
    }
    
    

    String mode = getSavedPlatform(); // "android" | "apple"
    String txt = "apple".equals(mode)
            ? "RETURN TO ANDROID MODE"
            : "RETURN TO APPLE MODE";

    btnReturnAndroid.setText(txt);
}

@Override
public void onBackPressed() {
    try {
        if (tts[0] != null) tts[0].stop();
    } catch (Throwable ignore) {}

    showPlatformSelectPopup();
}

// =========================================================
// PLATFORM FLOW — ALWAYS SHOW WELCOME
// =========================================================

private boolean isAppleMode() {
    return "apple".equals(getSavedPlatform());
}

private AlertDialog.Builder buildNeonDialog() {

    AlertDialog.Builder b =
            new AlertDialog.Builder(this);

    AlertDialog d = b.create();

    // -------- background drawable --------
    GradientDrawable bg = new GradientDrawable();
    bg.setColor(0xFF000000);          // μαύρο φόντο
    bg.setCornerRadius(dp(14));
    bg.setStroke(dp(3), 0xFFFFD700);  // χρυσό περίγραμμα

    d.setOnShowListener(x -> {
        Window w = d.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(bg);
        }
    });

    return b;
}

private ArrayAdapter<String> neonAdapter(String[] names) {
    return new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            names
    ) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) super.getView(position, convertView, parent);

            tv.setTextColor(0xFF39FF14);   // 💚 neon green
            tv.setTextSize(16f);
            tv.setPadding(dp(12), dp(10), dp(12), dp(10));

            return tv;
        }
    };
}

// =========================================================
// PLATFORM STORAGE — FINAL (NO LEGACY)
// =========================================================
private void savePlatform(String mode) {
    getSharedPreferences(PREFS, MODE_PRIVATE)
            .edit()
            .putString(KEY_PLATFORM, mode)   // "android" | "apple"
            .apply();
}

private String getSavedPlatform() {
    return getSharedPreferences(PREFS, MODE_PRIVATE)
            .getString(KEY_PLATFORM, "android"); // default
}

private void showMissingPermissionsToast() {

    StringBuilder sb = new StringBuilder();
    boolean gr = "el".equalsIgnoreCase(LocaleHelper.getLang(this));

    for (String p : REQUIRED_PERMISSIONS) {

        if (ContextCompat.checkSelfPermission(this, p)
                != PackageManager.PERMISSION_GRANTED) {

            if (sb.length() > 0) sb.append("\n");

            if (Manifest.permission.CAMERA.equals(p)) {
                sb.append(gr ? "Λείπει άδεια Κάμερας"
                             : "Camera permission is missing");
            } else if (Manifest.permission.RECORD_AUDIO.equals(p)) {
                sb.append(gr ? "Λείπει άδεια Μικροφώνου"
                             : "Microphone permission is missing");
            } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(p)) {
                sb.append(gr ? "Λείπει άδεια Τοποθεσίας"
                             : "Location permission is missing");
            } else if (Manifest.permission.BLUETOOTH_CONNECT.equals(p)) {
                sb.append(gr ? "Λείπει άδεια Bluetooth"
                             : "Bluetooth permission is missing");
            } else {
                sb.append(gr ? "Λείπει άδεια: " : "Missing permission: ")
                  .append(p);
            }
        }
    }

    if (sb.length() > 0) {
        Toast.makeText(
                this,
                sb.toString(),
                Toast.LENGTH_LONG
        ).show();
    }
}

private void showMissingPermissionsDialog() {

    boolean gr = "el".equalsIgnoreCase(LocaleHelper.getLang(this));

    StringBuilder missing = new StringBuilder();

    for (String p : REQUIRED_PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(this, p)
                != PackageManager.PERMISSION_GRANTED) {

            if (missing.length() > 0) missing.append(", ");

            if (Manifest.permission.CAMERA.equals(p)) {
                missing.append(gr ? "Κάμερα" : "Camera");
            } else if (Manifest.permission.BLUETOOTH_CONNECT.equals(p)) {
                missing.append(gr ? "Bluetooth" : "Bluetooth");
            } else if (Manifest.permission.RECORD_AUDIO.equals(p)) {
                missing.append(gr ? "Μικρόφωνο" : "Microphone");
            } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(p)) {
                missing.append(gr ? "Τοποθεσία" : "Location");
            }
        }
    }

    if (missing.length() == 0) return;

    String title = gr
            ? "Λείπουν άδειες"
            : "Missing permissions";

    String message = gr
            ? "Λείπουν οι άδειες:\n\n"
              + missing
              + "\n\nΘέλεις να ανοίξουν τώρα οι ρυθμίσεις για να τις ενεργοποιήσεις;"
            : "The following permissions are missing:\n\n"
              + missing
              + "\n\nDo you want to open settings now to enable them?";

    new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                    gr ? "ΝΑΙ" : "YES",
                    (d, w) -> {
                        Intent i = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null)
                        );
                        startActivity(i);
                    }
            )
            .setNegativeButton(
                    gr ? "ΟΧΙ" : "NO",
                    (d, w) -> d.dismiss()
            )
            .show();
}

// =========================================================
// PERMISSIONS — ENTRY GATE (MANDATORY)
// ---------------------------------------------------------
// This dialog blocks application entry until the user
// explicitly decides how to handle required permissions.
// No labs should request permissions before this gate.
// =========================================================

private void showPermissionsGate() {

    AlertDialog.Builder b =
            new AlertDialog.Builder(MainActivity.this);
    b.setCancelable(false);

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
    title.setText("REQUIRED PERMISSIONS");
    title.setTextColor(Color.WHITE);
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
    msg.setPadding(0, 0, 0, dp(12));
    box.addView(msg);

    // αρχική γλώσσα από σύστημα
    String sys = LocaleHelper.getLang(this); // "el" | "en"
    final String[] lang = new String[1];
    lang[0] = ("el".equalsIgnoreCase(sys)) ? "GR" : "EN";

    Runnable updateText = () -> {
        if ("GR".equals(lang[0])) {
            title.setText("ΑΠΑΙΤΟΥΜΕΝΕΣ ΑΔΕΙΕΣ");
            msg.setText(
                    "Για να λειτουργήσει σωστά η εφαρμογή, απαιτούνται ορισμένες άδειες.\n\n" +
                    "Θα σας ζητηθούν μία-μία.\n\n" +
                    "Μπορείτε να συνεχίσετε χωρίς αυτές, αλλά ορισμένα εργαστήρια δεν θα λειτουργούν."
            );
        } else {
            title.setText("REQUIRED PERMISSIONS");
            msg.setText(
                    "To function properly, the app requires certain permissions.\n\n" +
                    "You will be asked for them one by one.\n\n" +
                    "You may continue without them, but some labs will not work."
            );
        }
    };
    updateText.run();

    // ============================================================
    // CONTROLS ROW — LANGUAGE SPINNER
    // ============================================================
    LinearLayout controls = new LinearLayout(MainActivity.this);
    controls.setOrientation(LinearLayout.HORIZONTAL);
    controls.setGravity(Gravity.CENTER_VERTICAL);
    controls.setPadding(0, dp(14), 0, dp(10));

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
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(72)
            );
    langBox.setLayoutParams(lpLangBox);

    Spinner langSpinner = new Spinner(MainActivity.this);
    ArrayAdapter<String> langAdapter =
            new ArrayAdapter<>(
                    MainActivity.this,
                    android.R.layout.simple_spinner_item,
                    new String[]{"EN", "GR"}
            );
    langAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
    langSpinner.setAdapter(langAdapter);

    langSpinner.setSelection("GR".equals(lang[0]) ? 1 : 0);

    langSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                        AdapterView<?> p, View v, int pos, long id) {
                    lang[0] = (pos == 0) ? "EN" : "GR";
                    updateText.run();
                }
                @Override public void onNothingSelected(AdapterView<?> p) {}
            });

    langBox.addView(langSpinner);
    controls.addView(langBox);
    box.addView(controls);

    // ================= BUTTONS =================
    LinearLayout buttons = new LinearLayout(MainActivity.this);
    buttons.setOrientation(LinearLayout.HORIZONTAL);
    buttons.setGravity(Gravity.CENTER);
    buttons.setPadding(0, dp(12), 0, 0);

    // ---------- CONTINUE ----------
    Button btnContinue = new Button(MainActivity.this);
    btnContinue.setText("CONTINUE");
    btnContinue.setAllCaps(false);
    btnContinue.setTextColor(Color.WHITE);
    btnContinue.setTextSize(18f);
    btnContinue.setGravity(Gravity.CENTER);
    btnContinue.setPadding(dp(20), dp(18), dp(20), dp(18));

    GradientDrawable contBg = new GradientDrawable();
    contBg.setColor(0xFF0F8A3B);
    contBg.setCornerRadius(dp(18));
    contBg.setStroke(dp(3), 0xFFFFD700);
    btnContinue.setBackground(contBg);

    LinearLayout.LayoutParams lpC =
            new LinearLayout.LayoutParams(0, dp(88), 1f);
    lpC.setMargins(0, 0, dp(8), 0);
    btnContinue.setLayoutParams(lpC);

    // ---------- SKIP ----------
    Button btnSkip = new Button(MainActivity.this);
    btnSkip.setText("SKIP");
    btnSkip.setAllCaps(false);
    btnSkip.setTextColor(Color.WHITE);
    btnSkip.setTextSize(18f);
    btnSkip.setGravity(Gravity.CENTER);
    btnSkip.setPadding(dp(20), dp(18), dp(20), dp(18));

    GradientDrawable skipBg = new GradientDrawable();
    skipBg.setColor(0xFF444444);
    skipBg.setCornerRadius(dp(18));
    skipBg.setStroke(dp(3), 0xFFFFD700);
    btnSkip.setBackground(skipBg);

    LinearLayout.LayoutParams lpS =
            new LinearLayout.LayoutParams(0, dp(88), 1f);
    btnSkip.setLayoutParams(lpS);

    buttons.addView(btnContinue);
    buttons.addView(btnSkip);
    box.addView(buttons);

    // ================= DIALOG =================
    b.setView(box);
    final AlertDialog d = b.create();

    if (d.getWindow() != null) {
        d.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
    }

    btnContinue.setOnClickListener(v -> {
        d.dismiss();
        requestNextPermission();
    });

btnSkip.setOnClickListener(v -> {
    d.dismiss();
    showMissingPermissionsDialog();
});

    if (!isFinishing() && !isDestroyed()) d.show();
}

private int permissionIndex = 0;

private void requestNextPermission() {

    while (permissionIndex < REQUIRED_PERMISSIONS.length) {

        String p = REQUIRED_PERMISSIONS[permissionIndex];

        if (ContextCompat.checkSelfPermission(this, p)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ p },
                    REQ_PERMISSIONS
            );
            return;
        }

        permissionIndex++;
    }

    // ✅ Όλες οι άδειες οκ
    onAllPermissionsGranted();
}

@Override
public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode != REQ_PERMISSIONS) return;

    if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        // ✅ Πήρε την άδεια → πάμε στην επόμενη
        permissionIndex++;
        requestNextPermission();

    } else {

        // ❌ Άρνηση
        appendHtml("<br>Permissions not fully granted.");
        onPermissionsDenied();
    }
}

    // =========================================================
    // TTS — WELCOME
    // =========================================================
    private void speakWelcomeTTS() {

        if (welcomeMuted) return;

        try {
            if (tts[0] == null || !ttsReady[0]) return;

            tts[0].stop();

            if ("GR".equals(welcomeLang)) {
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

private boolean welcomeMuted = false;
private String  welcomeLang  = "EN";

// ------------------------------------------------------------
// SHOW POPUP
// ------------------------------------------------------------
private void showWelcomePopup() {

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
        welcomeLang = ("el".equalsIgnoreCase(sys)) ? "GR" : "EN";
        msg.setText("GR".equals(welcomeLang) ? getWelcomeTextGR() : getWelcomeTextEN());

        msg.setPadding(0, 0, 0, dp(12));   // λίγο αέρα πριν τα controls
box.addView(msg);

        // ============================================================
        // CONTROLS ROW — MUTE (LEFT) + LANG (RIGHT)
        // ============================================================
        LinearLayout controls = new LinearLayout(MainActivity.this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(16), 0, dp(10));

// ==========================
// 🔕 MUTE BUTTON — FINAL
// ==========================
Button muteBtn = new Button(MainActivity.this);
muteBtn.setText(welcomeMuted ? "Unmute" : "Mute");
muteBtn.setAllCaps(false);
muteBtn.setTextColor(0xFFFFFFFF);
muteBtn.setTextSize(18f);
muteBtn.setGravity(Gravity.CENTER);

// 🔓 ΞΕΚΛΕΙΔΩΜΑ ANDROID LIMITS (ΑΥΤΟ ΕΛΕΙΠΕ)
muteBtn.setMinHeight(0);
muteBtn.setMinimumHeight(0);
muteBtn.setMinWidth(0);
muteBtn.setMinimumWidth(0);

// ✅ ΤΟ ΥΨΟΣ ΤΟ ΔΙΝΕΙ ΤΟ PADDING
muteBtn.setPadding(
        dp(20),
        dp(18),   // ⬅️ ΠΡΟΣΘΗΚΗ
        dp(20),
        dp(18)    // ⬅️ ΠΡΟΣΘΗΚΗ
);

GradientDrawable muteBg = new GradientDrawable();
muteBg.setColor(0xFF444444);
muteBg.setCornerRadius(dp(12));
muteBg.setStroke(dp(2), 0xFFFFD700);
muteBtn.setBackground(muteBg);

// ✅ ΤΩΡΑ ΤΟ HEIGHT ΠΙΑΝΕΙ
LinearLayout.LayoutParams lpMute =
        new LinearLayout.LayoutParams(
                0,
                dp(88),      // ⬆️ ΑΠΟ 50 → 88
                1f
        );
lpMute.setMargins(0, 0, dp(8), 0);
muteBtn.setLayoutParams(lpMute);

        muteBtn.setOnClickListener(v -> {
            welcomeMuted = !welcomeMuted;
            muteBtn.setText(welcomeMuted ? "Unmute" : "Mute");
            try {
                if (welcomeMuted && tts != null && tts[0] != null) tts[0].stop();
            } catch (Throwable ignore) {}
        });

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
    if (!welcomeMuted && ttsReady[0] && welcomeShown) {
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
androidBtn.setText("🤖  MY ANDROID DEVICE");
androidBtn.setTextColor(Color.WHITE);
androidBtn.setTextSize(16f);
androidBtn.setTypeface(null, Typeface.BOLD);
androidBtn.setGravity(Gravity.CENTER);
androidBtn.setClickable(true);
androidBtn.setFocusable(true);

// 🔥 λιγότερο padding = πιο φαρδύ οπτικά
androidBtn.setPadding(dp(10), dp(14), dp(10), dp(14));

GradientDrawable bgAndroid = new GradientDrawable();
bgAndroid.setColor(0xFF000000);
bgAndroid.setCornerRadius(dp(14));
bgAndroid.setStroke(dp(3), 0xFFFFD700);
androidBtn.setBackground(bgAndroid);

// 🔥 ίδιο ύψος, πιο “γεμάτο” πλάτος
LinearLayout.LayoutParams lpBtn =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(84)
            );
    lpBtn.setMargins(dp(6), dp(14), dp(6), 0);
    androidBtn.setLayoutParams(lpBtn);

// 🔒 μην κόβεται ποτέ το text
androidBtn.setSingleLine(false);
androidBtn.setMaxLines(2);
androidBtn.setEllipsize(null);

// =================================================
// 🍎 APPLE "BUTTON" (TextView)
// =================================================
TextView appleBtn = new TextView(this);
appleBtn.setText("🍎 OTHER APPLE DEVICE");
appleBtn.setTextColor(Color.WHITE);
appleBtn.setTextSize(16f);
appleBtn.setTypeface(null, Typeface.BOLD);
appleBtn.setGravity(Gravity.CENTER);
appleBtn.setClickable(true);
appleBtn.setFocusable(true);

// 🔥 ίδιο padding με Android
appleBtn.setPadding(dp(10), dp(14), dp(10), dp(14));

GradientDrawable bgApple = new GradientDrawable();
bgApple.setColor(0xFF000000);
bgApple.setCornerRadius(dp(14));
bgApple.setStroke(dp(3), 0xFFFFD700);
appleBtn.setBackground(bgApple);

LinearLayout.LayoutParams lpBtn2 =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(84)
            );
    lpBtn2.setMargins(dp(6), dp(14), dp(6), 0);
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
    // LOGGING
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
 
