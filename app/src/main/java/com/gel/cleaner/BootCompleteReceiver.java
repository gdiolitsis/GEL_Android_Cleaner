package com.gel.cleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null &&
                Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Log.d("GEL", "✅ Boot completed — GEL Cleaner initialized");

            // (Optional) Trigger background maintenance
            // GELCleaner.safeClean(context, null);
        }
    }
}
