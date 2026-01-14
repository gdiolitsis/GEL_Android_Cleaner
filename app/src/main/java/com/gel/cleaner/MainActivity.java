// GDiolitsis Engine Lab (GEL) — Author & Developer
// MainActivity — STABLE FINAL

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends GELAutoActivityHook
        implements GELCleaner.LogCallback {

    // ==========================
    // STATE
    // ==========================
    private boolean startupFlowDone = false;

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
    private static final String KEY_PLATFORM = "platform_mode"; // android | apple

    // ==========================
    // WELCOME STATE
    // ==========================
    private boolean welcomeMuted = false;
    private String  welcomeLang  = "EN";

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

        txtLogs = findViewById(R.id.txtLogs);
        scroll  = findViewById(R.id.scrollRoot);

        applySavedLanguage();
        setupLangButtons();
        setupDonate();
        setupButtons();

        // ==========================
        // TTS INIT (ΠΡΩΤΑ!)
        // ==========================
        tts[0] = new TextToSpeech(this, status ->
                ttsReady[0] = (status == TextToSpeech.SUCCESS)
        );

        // 🔥 ALWAYS SHOW FLOW (once per launch)
        if (!startupFlowDone) {
            startupFlowDone = true;
            startPlatformFlow();
        }

        // 🍎 APPLE MODE FILTER
        if (isAppleMode()) {
            applyAppleModeUI();
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

@Override
public void onBackPressed() {

    // 🔒 Σταμάτα TTS αν παίζει
    try {
        if (tts[0] != null) tts[0].stop();
    } catch (Throwable ignore) {}

    // 🔁 Πήγαινε κατευθείαν στο PLATFORM SELECT popup
    showPlatformSelectPopup();
}

    // =========================================================
    // PLATFORM FLOW
    // =========================================================
    private void startPlatformFlow() {
        showWelcomePopup();
    }

    private boolean isAppleMode() {
        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);
        return "apple".equals(prefs.getString(KEY_PLATFORM, "android"));
    }

    private void savePlatform(String mode) {
        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putString(KEY_PLATFORM, mode).apply();
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

    // =========================================================
    // WELCOME POPUP
    // =========================================================
    private void showWelcomePopup() {

        runOnUiThread(() -> {

            AlertDialog.Builder b =
                    new AlertDialog.Builder(
                            MainActivity.this,
                            android.R.style.Theme_Material_Dialog_NoActionBar
                    );

            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(24), dp(20), dp(24), dp(18));

            GradientDrawable bg = new GradientDrawable();
            bg.setColor(0xFF101010);
            bg.setCornerRadius(dp(18));
            bg.setStroke(dp(4), 0xFFFFD700);
            box.setBackground(bg);

            TextView title = new TextView(this);
            title.setText("WELCOME");
            title.setTextColor(Color.WHITE);
            title.setTextSize(18f);
            title.setTypeface(null, Typeface.BOLD);
            title.setGravity(Gravity.CENTER);
            title.setPadding(0,0,0,dp(12));
            box.addView(title);

            TextView msg = new TextView(this);
            msg.setTextColor(0xFFDDDDDD);
            msg.setTextSize(15f);
            msg.setText(getWelcomeTextEN());
            box.addView(msg);

            // ---------------- CONTROLS ----------------
            LinearLayout controls = new LinearLayout(this);
            controls.setOrientation(LinearLayout.HORIZONTAL);
            controls.setPadding(0,dp(16),0,dp(10));

            // MUTE
            Button muteBtn = new Button(this);
muteBtn.setText("Mute");
muteBtn.setAllCaps(false);
muteBtn.setTextColor(Color.WHITE);

GradientDrawable muteBg = new GradientDrawable();
muteBg.setColor(0xFF444444);
muteBg.setCornerRadius(dp(12));
muteBg.setStroke(dp(2), 0xFFFFD700);
muteBtn.setBackground(muteBg);

LinearLayout.LayoutParams lpMute =
        new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
lpMute.setMargins(0, 0, dp(8), 0);
muteBtn.setLayoutParams(lpMute);

            // LANG
            Spinner langSpinner = new Spinner(this);

ArrayAdapter<String> langAdapter =
        new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"EN", "GR"}
        );
langAdapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item);
langSpinner.setAdapter(langAdapter);

// BOX γύρω από spinner
LinearLayout langBox = new LinearLayout(this);
langBox.setOrientation(LinearLayout.HORIZONTAL);
langBox.setGravity(Gravity.CENTER_VERTICAL);
langBox.setPadding(dp(10), dp(6), dp(10), dp(6));

GradientDrawable langBg = new GradientDrawable();
langBg.setColor(0xFF1A1A1A);
langBg.setCornerRadius(dp(12));
langBg.setStroke(dp(2), 0xFFFFD700);
langBox.setBackground(langBg);

LinearLayout.LayoutParams lpLangBox =
        new LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
        );
lpLangBox.setMargins(dp(8), 0, 0, 0);
langBox.setLayoutParams(lpLangBox);

langSpinner.setLayoutParams(
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
);

langBox.addView(langSpinner);

// ==========================
// OK BUTTON  — SAME AS LAB 28
// ==========================
Button okBtn = new Button(this);
okBtn.setText("OK");
okBtn.setAllCaps(false);
okBtn.setTextColor(Color.WHITE);

GradientDrawable okBg = new GradientDrawable();
okBg.setColor(0xFF0F8A3B);
okBg.setCornerRadius(dp(14));
okBg.setStroke(dp(3), 0xFFFFD700);
okBtn.setBackground(okBg);

LinearLayout.LayoutParams lpOk =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpOk.setMargins(0, dp(16), 0, 0);
okBtn.setLayoutParams(lpOk);

box.addView(okBtn);

// ==========================
// DIALOG
// ==========================
b.setView(box);
final AlertDialog d = b.create();

if (d.getWindow() != null)
    d.getWindow().setBackgroundDrawable(
            new ColorDrawable(Color.TRANSPARENT)
    );

// 🔒 πάντα stop TTS όταν κλείνει το popup
d.setOnDismissListener(dialog -> {
    try {
        if (tts != null && tts[0] != null) tts[0].stop();
    } catch (Throwable ignore) {}
});

d.show();

// ▶️ μίλα αυτόματα όταν ανοίγει το popup
speakWelcomeTTS();

// ==========================
// ▶️ OK ACTION
// ==========================
okBtn.setOnClickListener(v -> {
    try {
        if (tts != null && tts[0] != null) tts[0].stop();
    } catch (Throwable ignore) {}

    d.dismiss();
    showPlatformSelectPopup();
        });

    });   // 👈 κλείνει runOnUiThread
}        // 👈 κλείν

// =========================================================
// PLATFORM SELECT
// =========================================================
private void showPlatformSelectPopup() {

    runOnUiThread(() -> {

        AlertDialog.Builder b =
                new AlertDialog.Builder(
                        MainActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar
                );

        b.setCancelable(true);

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(24), dp(20), dp(24), dp(18));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xFF101010);
        bg.setCornerRadius(dp(18));
        bg.setStroke(dp(4), 0xFFFFD700);
        box.setBackground(bg);

        TextView t = new TextView(this);
        t.setText("SELECT PLATFORM");
        t.setTextColor(Color.WHITE);
        t.setTextSize(18f);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setPadding(0, 0, 0, dp(12));
        box.addView(t);

        // ==========================
        // 🤖 ANDROID BUTTON
        // ==========================
        Button androidBtn = new Button(this);
androidBtn.setText("🤖  ANDROID DEVICE");
androidBtn.setAllCaps(false);
androidBtn.setTextColor(Color.WHITE);
androidBtn.setTextSize(16f);

GradientDrawable bgAndroid = new GradientDrawable();
bgAndroid.setColor(0xFF000000);      // μαύρο φόντο
bgAndroid.setCornerRadius(dp(14));
bgAndroid.setStroke(dp(3), 0xFFFFD700); // χρυσό περίγραμμα
androidBtn.setBackground(bgAndroid);

LinearLayout.LayoutParams lpBtn =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpBtn.setMargins(0, dp(12), 0, 0);
androidBtn.setLayoutParams(lpBtn);

        Button appleBtn = new Button(this);
appleBtn.setText("🍎  APPLE DEVICE");
appleBtn.setAllCaps(false);
appleBtn.setTextColor(Color.WHITE);
appleBtn.setTextSize(16f);

GradientDrawable bgApple = new GradientDrawable();
bgApple.setColor(0xFF000000);      // μαύρο φόντο
bgApple.setCornerRadius(dp(14));
bgApple.setStroke(dp(3), 0xFFFFD700); // χρυσό περίγραμμα
appleBtn.setBackground(bgApple);

LinearLayout.LayoutParams lpBtn2 =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
        );
lpBtn2.setMargins(0, dp(12), 0, 0);
appleBtn.setLayoutParams(lpBtn2);

        // ==========================
        // ADD TO BOX
        // ==========================
        box.addView(androidBtn);
        box.addView(appleBtn);

        b.setView(box);
        final AlertDialog d = b.create();

        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        d.show();

        // ==========================
        // ACTIONS  ✅ FIXED (wrong variable names)
        // ==========================
        androidBtn.setOnClickListener(v -> {
            savePlatform("android");
            d.dismiss();
        });

        appleBtn.setOnClickListener(v -> {
            savePlatform("apple");
            d.dismiss();
            openAppleInternalPeripherals();
        });
    });
}

    // =========================================================
    // 🍎 APPLE ENTRY POINT
    // =========================================================
    private void openAppleInternalPeripherals() {
        // ΠΑΕΙ κατευθείαν στα Apple infos, ΟΧΙ στα iPhone labs
        startActivity(new Intent(this, MainActivity.class));
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
    // POPUP BUTTON STYLE
    // =========================================================
    private void stylePopupButton(Button b, String text) {

        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(16f);

        GradientDrawable g = new GradientDrawable();
        g.setColor(0xFF111111);
        g.setCornerRadius(dp(12));
        g.setStroke(dp(2),0xFFFFD700);
        b.setBackground(g);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(52)
                );
        lp.setMargins(0,dp(8),0,dp(8));
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
                    Toast.makeText(this,"Cannot open browser",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // =========================================================
    // BUTTONS
    // =========================================================
    private void setupButtons() {

        bind(R.id.btnAppleDeviceDeclaration,
                this::showAppleDeviceDeclarationPopup);

        bind(R.id.btnPhoneInfoInternal,
                () -> startActivity(new Intent(this, DeviceInfoInternalActivity.class)));

        bind(R.id.btnPhoneInfoPeripherals,
                () -> startActivity(new Intent(this, DeviceInfoPeripheralsActivity.class)));

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

        bind(R.id.btnDiagnostics,
                () -> startActivity(new Intent(this, DiagnosisMenuActivity.class)));
    }

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
    title.setTextSize(18f);
    title.setTypeface(null, Typeface.BOLD);
    title.setGravity(Gravity.CENTER);
    title.setPadding(0, 0, 0, dp(16));
    box.addView(title);

    // ==========================
    // 📱 iPHONE BUTTON
    // ==========================
    Button iphoneBtn = new Button(this);
    iphoneBtn.setText("📱  iPHONE");
    iphoneBtn.setAllCaps(false);
    iphoneBtn.setTextColor(Color.WHITE);
    iphoneBtn.setTextSize(16f);

    GradientDrawable iphoneBg = new GradientDrawable();
    iphoneBg.setColor(0xFF0F8A3B);
    iphoneBg.setCornerRadius(dp(14));
    iphoneBg.setStroke(dp(3), 0xFFFFD700);
    iphoneBtn.setBackground(iphoneBg);

    LinearLayout.LayoutParams lpIphone =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lpIphone.setMargins(0, dp(12), 0, 0);
    iphoneBtn.setLayoutParams(lpIphone);

    // ==========================
    // 📲 iPAD BUTTON
    // ==========================
    Button ipadBtn = new Button(this);
    ipadBtn.setText("📲  iPAD");
    ipadBtn.setAllCaps(false);
    ipadBtn.setTextColor(Color.WHITE);
    ipadBtn.setTextSize(16f);

    GradientDrawable ipadBg = new GradientDrawable();
    ipadBg.setColor(0xFF444444);
    ipadBg.setCornerRadius(dp(14));
    ipadBg.setStroke(dp(3), 0xFFFFD700);
    ipadBtn.setBackground(ipadBg);

    LinearLayout.LayoutParams lpIpad =
            new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
            );
    lpIpad.setMargins(0, dp(12), 0, 0);
    ipadBtn.setLayoutParams(lpIpad);

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

    private void showAppleModelPicker(String type) {

        String[] models = "iphone".equals(type)
                ? new String[]{"iPhone 8","iPhone X","iPhone XR","iPhone 11",
                "iPhone 12","iPhone 13","iPhone 14","iPhone 15"}
                : new String[]{"iPad 7","iPad 8","iPad 9",
                "iPad Air 4","iPad Air 5","iPad Pro 11","iPad Pro 12.9"};

        new AlertDialog.Builder(this)
                .setTitle("Select model")
                .setItems(models,(d,which)->{
                    saveAppleDevice(type,models[which]);
                    startActivity(new Intent(this,IPhoneLabsActivity.class));
                })
                .show();
    }

    private void saveAppleDevice(String type,String model){
        SharedPreferences prefs =
                getSharedPreferences(PREFS,MODE_PRIVATE);

        prefs.edit()
                .putString("apple_device_type",type)
                .putString("apple_device_model",model)
                .apply();
    }

    // =========================================================
    // BROWSER PICKER
    // =========================================================
    private void showBrowserPicker(){

        PackageManager pm = getPackageManager();

        String[] candidates = {
                "com.android.chrome","org.mozilla.firefox",
                "com.opera.browser","com.microsoft.emmx",
                "com.brave.browser","com.vivaldi.browser",
                "com.duckduckgo.mobile.android"
        };

        List<String> installed = new ArrayList<>();

        for(String pkg:candidates){
            try{ pm.getPackageInfo(pkg,0); installed.add(pkg); }
            catch(Exception ignored){}
        }

        if(installed.isEmpty()){
            Toast.makeText(this,"No browsers installed.",Toast.LENGTH_SHORT).show();
            return;
        }

        if(installed.size()==1){
            openAppInfo(installed.get(0));
            return;
        }

        new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Select Browser")
                .setItems(installed.toArray(new String[0]),
                        (d,w)->openAppInfo(installed.get(w)))
                .show();
    }

    private void openAppInfo(String pkg){
        try{
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:"+pkg));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }catch(Exception e){
            Toast.makeText(this,"Cannot open App Info",Toast.LENGTH_SHORT).show();
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
}
