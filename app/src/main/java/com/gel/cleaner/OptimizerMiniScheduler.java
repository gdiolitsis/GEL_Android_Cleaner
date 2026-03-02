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

        // ==============================
        // 24h Anti-Spam Cooldown
        // ==============================
        long lastNotify = sp.getLong("last_mini_notify", 0);
        long now = System.currentTimeMillis();

        boolean bypassCooldown =
        r.crashSignal ||
        (r.thermalHigh && r.temperature >= 45.0);

if (!bypassCooldown &&
        now - lastNotify < 24L * 60L * 60L * 1000L) {
    return Result.success();
}

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

long nowTime = System.currentTimeMillis();

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
// NOTIFICATION TEXT
// ==============================

boolean gr = AppLang.isGreek(ctx);

String title = gr
        ? "Εντοπίστηκε ένδειξη επιβάρυνσης"
        : "Device Health Signal";

String body = gr
        ? "Παρατηρήθηκε πιθανή επιβάρυνση συστήματος."
        : "Potential system load detected.";

try {

    NotificationCompat.Builder nb =
            new NotificationCompat.Builder(ctx, "gel_default")
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

    // ==============================
    // CLICK ACTION
    // ==============================

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
