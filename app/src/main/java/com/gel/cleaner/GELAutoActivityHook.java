package com.gel.cleaner;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class GELAutoActivityHook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GELAutoDP.init(this);
    }

    public int dp(int x) {
        return GELAutoDP.dp(x);
    }

    public float sp(float x) {
        return GELAutoDP.sp(x);
    }
}
