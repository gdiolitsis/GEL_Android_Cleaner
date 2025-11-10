package com.gel.cleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GELBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent i) {
        String action = i.getAction();
        Log.d("GELBroadcast", "Received: " + action);

        // Boot completed
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Could auto-schedule maintenance
            Log.d("GEL", "Boot-Completed");
        }

        // App package added/removed
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_CHANGED.equals(action)) {

            Log.d("GEL", "Package event: " + action);
        }
    }
}
