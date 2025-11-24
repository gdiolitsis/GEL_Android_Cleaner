// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELBroadcast v2.1 — Boot + Package Events + Foldable Awareness
// 🔥 Fully Integrated with: GELFoldableOrchestrator + UIManager + Runtime Hooks
// NOTE: Ολόκληρο αρχείο έτοιμο για copy-paste (κανόνας παππού Γιώργου)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

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
        // 0) INITIALIZE GEL FOLDABLE RUNTIME (if app wakes from BOOT)
        // ============================================================
        try {
            GELFoldableOrchestrator.initIfPossible(ctx);
            Log.d(TAG, "🔧 Foldable Orchestrator initialized");
        } catch (Throwable t) {
            Log.w(TAG, "Foldable init skipped: " + t.getMessage());
        }

        // ============================================================
        // 1) BOOT COMPLETED
        // ============================================================
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {

            Log.d(TAG, "✅ Boot completed");

            // Future: auto-scheduler, background cleanup, analytics sync
            // Example placeholder:
            // GELAutoMaintenance.scheduleDaily(ctx);
        }

        // ============================================================
        // 2) PACKAGE EVENTS (install / remove / update)
        // ============================================================
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_CHANGED.equals(action)) {

            String pkg = (i.getData() != null)
                    ? i.getData().getSchemeSpecificPart()
                    : "unknown";

            Log.d(TAG, "📦 Package event: " + action + " → " + pkg);

            // Trigger optional refresh for foldable UI models
            try {
                GELFoldableOrchestrator.notifyPackageEvent(pkg);
            } catch (Throwable ignore) {}

            // Future extensions:
            // - Clean cache when app removed
            // - Recalculate app junk size
            // - Notify GEL Dashboard
        }
    }
}
