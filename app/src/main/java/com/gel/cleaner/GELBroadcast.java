// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELBroadcast v2.0 — Boot + Package Events Handler
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GELBroadcast extends BroadcastReceiver {

    private static final String TAG = "GEL.BR";

    @Override
    public void onReceive(Context ctx, Intent i) {

        if (ctx == null || i == null) {
            Log.w(TAG, "⚠ Null context/intent");
            return;
        }

        String action = i.getAction();
        Log.d(TAG, "📩 Received: " + action);

        // ============================================================
        // BOOT COMPLETED
        // ============================================================
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {

            Log.d(TAG, "✅ Boot completed");

            // Placeholder for future auto-maintenance (only if user enables)
            // GELCleaner.safeClean(ctx, null);
        }

        // ============================================================
        // PACKAGE EVENTS (install / remove / update)
        // ============================================================
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_CHANGED.equals(action)) {

            String pkg = (i.getData() != null)
                    ? i.getData().getSchemeSpecificPart()
                    : "unknown";

            Log.d(TAG, "📦 Package event: " + action + " → " + pkg);

            // Future extensions:
            // - auto-refresh list
            // - recalc junk size
        }
    }
}
