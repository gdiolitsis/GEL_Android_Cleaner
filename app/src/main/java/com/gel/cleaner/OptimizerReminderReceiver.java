// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerReminderReceiver.java — FINAL (Notification • Opens Guided Optimizer)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public final class OptimizerReminderReceiver extends BroadcastReceiver {

    private static final String CH_ID = "gel_optimizer_reminder";
    private static final int NOTIF_ID = 7772;

    @Override
    public void onReceive(Context c, Intent intent) {
        if (c == null) return;

        final boolean gr = AppLang.isGreek(c);

        ensureChannel(c);

        Intent open = new Intent(c, GuidedOptimizerActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(
                c,
                7773,
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = gr ? "Υπενθύμιση ελέγχου υγείας" : "Health check reminder";
        String text = gr
                ? "Όταν βρεις λίγο χρόνο, τρέξε έναν γρήγορο έλεγχο για καλύτερη εικόνα."
                : "When you have a moment, run a quick check for a better overview.";

        NotificationCompat.Builder b = new NotificationCompat.Builder(c, CH_ID)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        try {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.notify(NOTIF_ID, b.build());
        } catch (Throwable ignore) {}

        // keep schedule alive if OEM killed it
        try { OptimizerScheduler.rescheduleIfEnabled(c); } catch (Throwable ignore) {}
    }

    private static void ensureChannel(Context c) {
        if (c == null) return;
        if (Build.VERSION.SDK_INT < 26) return;

        try {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationChannel ch = nm.getNotificationChannel(CH_ID);
            if (ch != null) return;

            NotificationChannel nc = new NotificationChannel(
                    CH_ID,
                    "GEL Optimizer",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            nc.setDescription("GEL health check reminders");
            nm.createNotificationChannel(nc);

        } catch (Throwable ignore) {}
    }
}
