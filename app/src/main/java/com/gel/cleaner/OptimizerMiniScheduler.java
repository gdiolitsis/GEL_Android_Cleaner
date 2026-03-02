package com.gel.cleaner;

import android.app.PendingIntent;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class OptimizerMiniScheduler extends Worker {

    private static final String PREFS = "gel_prefs";
    private static final String KEY_PULSE_ENABLED = "pulse_enabled";

    public OptimizerMiniScheduler(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Context ctx = getApplicationContext();

        SharedPreferences sp =
                ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        if (!sp.getBoolean(KEY_PULSE_ENABLED, false)) {
            return Result.success();
        }

        long nowTime = System.currentTimeMillis();

        // ==============================
        // Cache Check
        // ==============================
        boolean cacheHigh = false;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                StorageStatsManager ssm =
                        (StorageStatsManager) ctx.getSystemService(Context.STORAGE_STATS_SERVICE);

                if (ssm != null) {

                    StorageStats st = ssm.queryStatsForPackage(
                            android.os.storage.StorageManager.UUID_DEFAULT,
                            ctx.getPackageName(),
                            android.os.UserHandle.getUserHandleForUid(Process.myUid())
                    );

                    if (st != null) {

                        long appBytes = st.getAppBytes();
                        long dataBytes = st.getDataBytes();
                        long cacheBytes = st.getCacheBytes();

                        long appSize = appBytes + dataBytes;

                        int percent = (appSize > 0)
                                ? (int) Math.round((cacheBytes * 100.0) / appSize)
                                : 0;

                        if (percent >= 85) {
                            cacheHigh = true;
                        }
                    }
                }
            }
        } catch (Throwable ignore) {}

        // ==============================
        // Run Health Probes
        // ==============================
        OptimizerMiniHealthProbes.Result r =
                OptimizerMiniHealthProbes.run(ctx, cacheHigh);

        // ==============================
        // SMART ESCALATION ENGINE
        // ==============================

        boolean isCritical =
                r.crashSignal ||
                (r.thermalHigh && r.temperature >= 45.0);

        boolean isModerate =
                (!isCritical) &&
                (r.cpuSpike || r.cacheHigh || r.thermalHigh);

        if (isCritical) {
            r.critical = true;
        }
        else if (isModerate) {

            int moderateCount = sp.getInt("mini_moderate_count", 0);
            long firstModerateTime = sp.getLong("mini_moderate_first_time", 0);

            if (firstModerateTime == 0 ||
                    nowTime - firstModerateTime > 24L * 60L * 60L * 1000L) {

                moderateCount = 1;
                firstModerateTime = nowTime;

            } else {
                moderateCount++;
            }

            sp.edit()
                    .putInt("mini_moderate_count", moderateCount)
                    .putLong("mini_moderate_first_time", firstModerateTime)
                    .apply();

            if (moderateCount >= 3) {

                r.critical = true;

                sp.edit()
                        .putInt("mini_moderate_count", 0)
                        .putLong("mini_moderate_first_time", 0)
                        .apply();
            }
        }

        if (!r.critical) {
            return Result.success();
        }

        // ==============================
        // COOLDOWN (Crash / 45°C bypass)
        // ==============================

        long lastNotify = sp.getLong("last_mini_notify", 0);

        boolean bypassCooldown =
                r.crashSignal ||
                (r.thermalHigh && r.temperature >= 45.0);

        if (!bypassCooldown &&
                nowTime - lastNotify < 24L * 60L * 60L * 1000L) {
            return Result.success();
        }

        // ==============================
        // DYNAMIC NOTIFICATION TEXT
        // ==============================

        boolean gr = AppLang.isGreek(ctx);

        String title;
        String body;

        if (r.crashSignal) {

            title = gr ? "⚠ Εντοπίστηκε Crash"
                       : "⚠ System Crash Detected";

            body = gr
                    ? "Παρατηρήθηκε πρόσφατο crash ή ANR."
                    : "A recent crash or ANR was detected.";

        }
        else if (r.thermalHigh && r.temperature >= 45.0) {

            title = gr ? "🔥 Υψηλή Θερμοκρασία"
                       : "🔥 High Device Temperature";

            body = gr
                    ? "Θερμοκρασία: " + r.temperature + "°C"
                    : "Temperature: " + r.temperature + "°C";

        }
        else if (r.cpuSpike && r.thermalHigh) {

            title = gr ? "⚠ Υψηλό CPU & Θερμοκρασία"
                       : "⚠ High CPU & Thermal Load";

            body = gr
                    ? "Αυξημένο CPU σε συνδυασμό με θερμοκρασία."
                    : "High CPU load combined with temperature rise.";

        }
        else if (r.cpuSpike) {

            title = gr ? "📈 Υψηλό CPU Load"
                       : "📈 High CPU Usage";

            body = gr
                    ? "Παρατηρήθηκε αυξημένη χρήση επεξεργαστή."
                    : "High processor usage detected.";

        }
        else if (r.cacheHigh) {

            title = gr ? "🧹 Υψηλή Cache Εφαρμογών"
                       : "🧹 High App Cache Usage";

            body = gr
                    ? "Μεγάλη προσωρινή μνήμη εφαρμογών."
                    : "Large application cache usage detected.";

        }
        else {

            title = gr ? "Ένδειξη Επιβάρυνσης"
                       : "Health Signal";

            body = gr
                    ? "Παρατηρήθηκε πιθανή επιβάρυνση."
                    : "Potential load detected.";
        }

        try {

            NotificationCompat.Builder nb =
                    new NotificationCompat.Builder(ctx, "gel_default")
                            .setSmallIcon(android.R.drawable.stat_notify_more)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);

            Intent intent = new Intent(ctx, MainActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            intent.putExtra("mini_cpu", r.cpuSpike);
            intent.putExtra("mini_thermal", r.thermalHigh);
            intent.putExtra("mini_crash", r.crashSignal);
            intent.putExtra("mini_cache", r.cacheHigh);
            intent.putExtra("mini_temp", r.temperature);

            PendingIntent pi = PendingIntent.getActivity(
                    ctx,
                    19001,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            nb.setContentIntent(pi);

            NotificationManagerCompat.from(ctx).notify(19001, nb.build());

            sp.edit().putLong("last_mini_notify", nowTime).apply();

        } catch (Throwable ignore) {}

        return Result.success();
    }
}
