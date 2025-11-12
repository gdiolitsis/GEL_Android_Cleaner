package com.gel.cleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = "GEL.BOOT";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            Log.w(TAG, "⚠ Null intent on BOOT");
            return;
        }

        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {

            Log.d(TAG, "✅ Boot completed — GEL Cleaner initialized");

            // (Optional) Auto optimize (ONLY IF USER ENABLES)
            // GELCleaner.safeClean(context, null);
        }
    }
}
