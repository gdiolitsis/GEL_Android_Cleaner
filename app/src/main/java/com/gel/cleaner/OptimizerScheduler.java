// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerScheduler.java — FINAL (Reminder Notifications • No background work • Exact + Doze-safe)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public final class OptimizerScheduler {

    private static final String PREFS = "gel_optimizer_prefs";
    private static final String K_REMINDER_ENABLED = "reminder_enabled";
    private static final String K_REMINDER_INTERVAL = "reminder_interval"; // 1,7,30

    private static final int REQ_CODE = 7771;

    private OptimizerScheduler() {}

    // ============================
    // PUBLIC API
    // ============================

    public static void enableReminder(Context c, int daysInterval) {
        if (c == null) return;

        int safeDays = normalizeDays(daysInterval);

        try {
            c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(K_REMINDER_ENABLED, true)
                    .putInt(K_REMINDER_INTERVAL, safeDays)
                    .apply();
        } catch (Throwable ignore) {}

        scheduleNext(c, safeDays, true);
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

    public static boolean isReminderEnabled(Context c) {
        if (c == null) return false;

        try {
            return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getBoolean(K_REMINDER_ENABLED, false);
        } catch (Throwable ignore) {}

        return false;
    }

    public static int getReminderDays(Context c) {
        if (c == null) return 7;

        try {
            int d = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .getInt(K_REMINDER_INTERVAL, 7);
            return normalizeDays(d);
        } catch (Throwable ignore) {}

        return 7;
    }

    public static void rescheduleIfEnabled(Context c) {
        if (c == null) return;

        boolean en = false;
        int days = 7;

        try {
            SharedPreferences sp = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            en = sp.getBoolean(K_REMINDER_ENABLED, false);
            days = sp.getInt(K_REMINDER_INTERVAL, 7);
        } catch (Throwable ignore) {}

        if (en) scheduleNext(c, normalizeDays(days), true);
        else cancel(c);
    }

    // Called by OptimizerReminderReceiver AFTER showing notification
    public static void scheduleNextFromReceiver(Context c) {
        if (c == null) return;

        boolean en = isReminderEnabled(c);
        if (!en) {
            cancel(c);
            return;
        }

        int days = getReminderDays(c);
        scheduleNext(c, days, false);
    }

    // ============================
    // INTERNAL
    // ============================

    private static int normalizeDays(int d) {
        if (d == 1 || d == 7 || d == 30) return d;
        return 7;
    }

    private static PendingIntent buildPI(Context c) {
        Intent i = new Intent(c, OptimizerReminderReceiver.class);
        return PendingIntent.getBroadcast(
                c,
                REQ_CODE,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static void scheduleNext(Context c, int daysInterval, boolean resetNow) {

        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        PendingIntent pi = buildPI(c);

        try { am.cancel(pi); } catch (Throwable ignore) {}

        long now = System.currentTimeMillis();
        long intervalMs = daysInterval * 24L * 60L * 60L * 1000L;

        // First run: +2 hours (as you had). Next runs: +interval from "now" (receiver will call scheduleNextFromReceiver).
        long triggerAt = resetNow
                ? (now + (2L * 60L * 60L * 1000L))
                : (now + intervalMs);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            }
        } catch (Throwable ignore) {
            try {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } catch (Throwable ignored) {}
        }
    }

    private static void cancel(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        PendingIntent pi = buildPI(c);

        try { am.cancel(pi); } catch (Throwable ignore) {}
    }
}
