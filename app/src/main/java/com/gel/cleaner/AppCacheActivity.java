package com.gel.cleaner;

import android.app.Activity;
import android.os.Bundle;

public class AppCacheActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // nothing — legacy compatibility only
        finish();
    }
}
