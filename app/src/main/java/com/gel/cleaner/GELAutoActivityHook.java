package com.gel.cleaner;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class GELAutoActivityHook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GELAutoDP.init(this);
    }

    // Rotation / Fold-Unfold / Tablet resize / Locale change etc
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        GELAutoDP.init(this);
    }

    // Split-screen / freeform resize
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        GELAutoDP.init(this);
    }

    // Handy wrappers so δεν αλλάζεις κώδικα
    public int dp(int x) { return GELAutoDP.dp(x); }
    public float sp(float x) { return GELAutoDP.sp(x); }
    public int px(int x) { return GELAutoDP.px(x); }
}
