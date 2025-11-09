package com.gel.cleaner;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat);
        setContentView(R.layout.activity_main);

        setupLanguageButtons();
        setupCleanerButtons();
    }

    private void setupLanguageButtons() {
        View bEN = findViewById(R.id.btnEN);
        View bGR = findViewById(R.id.btnGR);

        if (bEN != null) {
            bEN.setOnClickListener(v -> {
                LocaleHelper.set(this, "en");
                recreate();
            });
        }

        if (bGR != null) {
            bGR.setOnClickListener(v -> {
                LocaleHelper.set(this, "el");
                recreate();
            });
        }
    }

    private void setupCleanerButtons() {
        // NOT IMPLEMENTS YET
        // Now only logs
    }
}
