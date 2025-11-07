package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView logs;

    private void add(String s, boolean isError) {
        runOnUiThread(() -> {
            String cur = logs.getText() == null ? "" : logs.getText().toString();
            logs.setText(cur + (cur.isEmpty() ? "" : "\n") + (isError ? "❌ " : "") + s);
        });
    }

    private final GELCleaner.LogCallback cb = (msg, err) -> add(msg, err);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logs = findViewById(R.id.txtLogs);

        Button btnCpuInfo = findViewById(R.id.btnCpuInfo);
        Button btnCpuLive = findViewById(R.id.btnCpuLive);
        Button btnSafe    = findViewById(R.id.btnSafe);
        Button btnBrowser = findViewById(R.id.btnBrowser);
        Button btnMedia   = findViewById(R.id.btnMedia);
        Button btnTemp    = findViewById(R.id.btnTemp);
        Button btnRam     = findViewById(R.id.btnRam);
        Button btnAll     = findViewById(R.id.btnAll);
        Button btnDonate  = findViewById(R.id.btnDonate);
        Button langEN     = findViewById(R.id.langEN);
        Button langEL     = findViewById(R.id.langEL);

        btnCpuInfo.setOnClickListener(v -> add(GELCleaner.cpuInfo(), false));
        btnCpuLive.setOnClickListener(v -> add(GELCleaner.cpuLive(), false));
        btnSafe.setOnClickListener(v    -> GELCleaner.safeClean(this, cb));
        btnBrowser.setOnClickListener(v -> GELCleaner.browserCache(this, cb));
        btnMedia.setOnClickListener(v   -> GELCleaner.mediaJunk(this, cb));
        btnTemp.setOnClickListener(v    -> GELCleaner.tempClean(this, cb));
        btnRam.setOnClickListener(v     -> GELCleaner.cleanRAM(this, cb));
        btnAll.setOnClickListener(v     -> GELCleaner.cleanAll(this, cb));

        btnDonate.setOnClickListener(v -> {
            // PayPal donate → email target
            String url = "https://www.paypal.com/donate?business=gdiolitsis@yahoo.com&no_recurring=0&item_name=Support+GEL+Cleaner&currency_code=EUR";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        langEN.setOnClickListener(v -> switchLang("en"));
        langEL.setOnClickListener(v -> switchLang("el"));

        add("🟡 Ready — Dark-Gold UI loaded", false);
    }

    private void switchLang(String code) {
        try {
            Locale locale = new Locale(code);
            Locale.setDefault(locale);
            android.content.res.Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            add("🌐 Language → " + code.toUpperCase(), false);
            recreate();
        } catch (Exception e) {
            add("Language switch failed", true);
        }
    }
}
