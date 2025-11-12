package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

public class AppCacheActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String pkg = getIntent().getStringExtra("target_pkg");
        if (pkg != null) {
            openAppDetails(pkg);
        }

        finish();
    }

    /* =========================================================
     * HELPERS
     * ========================================================= */
    private void openAppDetails(String pkg) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.fromParts("package", pkg, null));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
