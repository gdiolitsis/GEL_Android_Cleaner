// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELBroadcast v2.3 — Boot + Package Events + Foldable Awareness (Safe-Reflective)
// 🔥 Fully Integrated with: GELFoldableOrchestrator + UIManager + Runtime Hooks
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
        // 0) INITIALIZE FOLDABLE RUNTIME (SAFE, REFLECTIVE)
        // ============================================================
        try {
            Class<?> c = Class.forName("com.gel.cleaner.GELFoldableOrchestrator");
            c.getMethod("initIfPossible", Context.class).invoke(null, ctx);
            Log.d(TAG, "🔧 Foldable Orchestrator initialized");
        } catch (Throwable t) {
            Log.w(TAG, "Foldable init skipped: " + t.getMessage());
        }

        // ============================================================
        // 1) BOOT COMPLETED EVENTS
        // ============================================================
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {

            Log.d(TAG, "✅ Boot completed");

            // Future expansion:
            // - GELAutoMaintenance.scheduleDaily(ctx);
            // - Auto-clean modules
            // - Analytics sync
        }

        // ============================================================
        // 2) PACKAGE EVENTS (INSTALL / REMOVE / UPDATE / CHANGE)
        // ============================================================
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_CHANGED.equals(action)
                || Intent.ACTION_PACKAGE_REPLACED.equals(action)) {

            String pkg = "unknown";
            try {
                if (i.getData() != null)
                    pkg = i.getData().getSchemeSpecificPart();
            } catch (Throwable ignore) {}

            Log.d(TAG, "📦 Package event: " + action + " → " + pkg);

            // Notify orchestrator (safe reflection – no hard dependency)
            try {
                Class<?> c = Class.forName("com.gel.cleaner.GELFoldableOrchestrator");
                c.getMethod("notifyPackageEvent", String.class).invoke(null, pkg);
            } catch (Throwable ignore) {}

            // Future extensions:
            // - Automatic junk recalculation
            // - Smart Clean suggestions
            // - GEL Dashboard notifications
        }
    }
}
