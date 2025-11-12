package com.gel.cleaner;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class AppCacheActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1) Check usage access
        if (!hasUsageAccess(this)) {
            // redirect to permission helper
            Intent i = new Intent(this, PermissionsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return;
        }

        // 2) If the app was requested, open it
        String pkg = getIntent().getStringExtra("target_pkg");
        if (pkg != null) {
            openAppDetails(pkg);
        }

        finish();
    }


    /* ========== Helpers ========== */

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


    private boolean hasUsageAccess(Context ctx) {
        try {
            AppOpsManager appOps = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.unsafeCheckOpNoThrow(
                    "android:get_usage_stats",
                    android.os.Process.myUid(),
                    ctx.getPackageName()
            );
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ignored) { }
        return false;
    }
}
