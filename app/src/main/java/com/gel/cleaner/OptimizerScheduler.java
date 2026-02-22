// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerScheduler.java — FINAL (Reminder Notifications • No background work)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public final class OptimizerScheduler {

    private static final String PREFS = "gel_optimizer_prefs";
    private static final String K_REMINDER_ENABLED = "reminder_enabled";
    private static final String K_REMINDER_INTERVAL = "reminder_interval"; // 1,7,30

    private OptimizerScheduler() {}

    public static void enableReminder(Context c, int daysInterval) {
        if (c == null) return;
        if (daysInterval <= 0) daysInterval = 7;

        try {
            c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(K_REMINDER_ENABLED, true)
                    .putInt(K_REMINDER_INTERVAL, daysInterval)
                    .apply();
        } catch (Throwable ignore) {}

        schedule(c, daysInterval);
    }

    public static void disableReminder(Context c) {
        if (c == null) return;

        try {
            c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(K_REMINDER_ENABLED, false)
                    .apply();
        } catch (Throwable ignore) {}

        cancel(c);
    }

    public static void rescheduleIfEnabled(Context c) {
        if (c == null) return;
        boolean en = false;
        int days = 7;

        try {
            en = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(K_REMINDER_ENABLED, false);
            days = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(K_REMINDER_INTERVAL, 7);
        } catch (Throwable ignore) {}

        if (en) schedule(c, days);
        else cancel(c);
    }

    private static void schedule(Context c, int daysInterval) {

        long intervalMs = daysInterval * 24L * 60L * 60L * 1000L;

        Intent i = new Intent(c, OptimizerReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                c,
                7771,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        // Start in ~2 hours (inexact, friendly)
        long first = SystemClock.elapsedRealtime() + (2L * 60L * 60L * 1000L);

        try {
            am.cancel(pi);
        } catch (Throwable ignore) {}

        try {
            am.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    first,
                    intervalMs,
                    pi
            );
        } catch (Throwable ignore) {
            // fallback one-shot
            try {
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, first, pi);
            } catch (Throwable ignored) {}
        }
    }

    private static void cancel(Context c) {
        Intent i = new Intent(c, OptimizerReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                c,
                7771,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        try { am.cancel(pi); } catch (Throwable ignore) {}
    }
}
