// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerMiniScheduler.java — FINAL (Mini Alerts Only)

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
        // Cache Check (>=85%)
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
        // MINI SIGNAL ENGINE (FINAL)
        // Critical triggers:
        //  - Temp ≥45°C (not charging)
        //  - CPU spike + Temp ≥45°C (not charging)
        //  - Cache ≥85%
        // Moderate escalation:
        //  - cpuSpike OR cacheHigh OR thermalHigh (>=43) => 3 hits/24h => 1 notify
        // ==============================

        boolean thermalCritical = (!r.charging && r.temperature >= 45.0);
        boolean cpuThermalCritical = (r.cpuSpike && thermalCritical);
        boolean cacheCritical = r.cacheHigh;

        boolean isCritical = thermalCritical || cpuThermalCritical || cacheCritical;

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
            // Re-arm next day slot (only if enabled)
            rearm(ctx, sp);
            return Result.success();
        }

        // ==============================
        // COOLDOWN (24h) — Thermal ≥45°C bypass only
        // ==============================

        long lastNotify = sp.getLong("last_mini_notify", 0);

        boolean bypassCooldown = thermalCritical || cpuThermalCritical;

        if (!bypassCooldown &&
                nowTime - lastNotify < 24L * 60L * 60L * 1000L) {
            rearm(ctx, sp);
            return Result.success();
        }

        // ==============================
        // NOTIFICATION TEXT (TITLE ALWAYS "GEL iDoctor:")
        // ==============================

        boolean gr = AppLang.isGreek(ctx);

String title;
String body;

if (thermalCritical) {

    double t = r.temperature;

    if (t >= 50.0) {

        title = gr
                ? "GEL iDoctor: ΚΡΙΣΙΜΗ Υπερθέρμανση"
                : "GEL iDoctor: CRITICAL Overheat";

        body = gr
                ? "Θερμοκρασία: " + t + "°C\n\n"
                  + "Η θερμοκρασία βρίσκεται σε επικίνδυνα επίπεδα.\n"
                  + "Διακόψτε άμεσα βαριές λειτουργίες και αποσυνδέστε τον φορτιστή.\n"
                  + "Αφήστε τη συσκευή να κρυώσει.\n\n"
                  + "Θέλεις να γίνει πλήρης διάγνωση τώρα;"
                : "Temperature: " + t + "°C\n\n"
                  + "Temperature is at a critical level.\n"
                  + "Stop heavy activity and unplug the charger immediately.\n"
                  + "Allow the device to cool down.\n\n"
                  + "Run full diagnostics now?";

    }
    else if (t >= 47.0) {

        title = gr
                ? "GEL iDoctor: Πολύ Υψηλή Θερμοκρασία"
                : "GEL iDoctor: Very High Temperature";

        if (r.charging) {

            body = gr
                    ? "Θερμοκρασία: " + t + "°C\n\n"
                      + "Η συσκευή φορτίζει και αυτό μπορεί να συμβάλλει στην άνοδο θερμοκρασίας.\n"
                      + "Αποσυνδέστε τον φορτιστή και αφήστε τη να κρυώσει.\n\n"
                      + "Θέλεις να γίνει διάγνωση τώρα;"
                    : "Temperature: " + t + "°C\n\n"
                      + "Device is charging which may contribute to heat.\n"
                      + "Unplug and allow it to cool.\n\n"
                      + "Run diagnostics now?";

        } else if (r.cpuSpike) {

            body = gr
                    ? "Θερμοκρασία: " + t + "°C\n\n"
                      + "Ανιχνεύθηκε αυξημένη χρήση CPU σε συνδυασμό με θερμοκρασία.\n"
                      + "Μειώστε τη δραστηριότητα και αφήστε τη συσκευή να κρυώσει.\n\n"
                      + "Θέλεις να γίνει διάγνωση τώρα;"
                    : "Temperature: " + t + "°C\n\n"
                      + "High CPU usage detected along with elevated temperature.\n"
                      + "Reduce activity and allow the device to cool.\n\n"
                      + "Run diagnostics now?";

        } else {

            body = gr
                    ? "Θερμοκρασία: " + t + "°C\n\n"
                      + "Η συσκευή δεν φορτίζει και δεν ανιχνεύεται υψηλή χρήση CPU.\n"
                      + "Αν έχει εκτεθεί σε ζεστό περιβάλλον (π.χ. άμεσο ήλιο), απομακρύνετέ την.\n\n"
                      + "Εάν η θερμοκρασία παραμένει υψηλή, συνιστάται διαγνωστικός έλεγχος.\n\n"
                      + "Θέλεις να γίνει διάγνωση τώρα;"
                    : "Temperature: " + t + "°C\n\n"
                      + "Device is not charging and no high CPU usage is detected.\n"
                      + "If exposed to a hot environment (e.g. direct sunlight), move it to a cooler place.\n\n"
                      + "If temperature remains elevated, system diagnostics are recommended.\n\n"
                      + "Run diagnostics now?";
        }

    }
    else {

        title = gr
                ? "GEL iDoctor: Υψηλή Θερμοκρασία"
                : "GEL iDoctor: High Device Temperature";

        body = gr
                ? "Θερμοκρασία: " + t + "°C\n\n"
                  + "Συνιστάται μείωση δραστηριότητας και έλεγχος συστήματος.\n\n"
                  + "Θέλεις να γίνει έλεγχος τώρα;"
                : "Temperature: " + t + "°C\n\n"
                  + "Reduce activity and consider running a system check.\n\n"
                  + "Run a check now?";
    }

} 
else if (cacheCritical) {

    title = gr
            ? "GEL iDoctor: Υψηλή Cache"
            : "GEL iDoctor: High Cache Usage";

    body = gr
            ? "Εντοπίστηκε υψηλή προσωρινή μνήμη (cache) εφαρμογών.\n"
              + "Ο καθαρισμός μπορεί να βελτιώσει την απόδοση.\n\n"
              + "Θέλεις να γίνει έλεγχος και καθαρισμός τώρα;"
            : "High application cache usage detected.\n"
              + "Cleaning may improve performance.\n\n"
              + "Run diagnostics and cleanup now?";
}
else {

    title = gr
            ? "GEL iDoctor: Πιθανή Επιβάρυνση"
            : "GEL iDoctor: Possible Load Detected";

    body = gr
            ? "Το σύστημα εντόπισε επαναλαμβανόμενες ενδείξεις επιβάρυνσης.\n\nΘέλεις να γίνει έλεγχος τώρα;"
            : "Repeated load signals detected.\n\nRun a check now?";
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

        // ==============================
        // RE-ARM SAME SLOT FOR NEXT DAY
        // ==============================
        rearm(ctx, sp);

        return Result.success();
    }

    private void rearm(Context ctx, SharedPreferences sp) {

        try {
            int hour = getInputData().getInt("hour", -1);
            String workName = getInputData().getString("workName");

            if (!sp.getBoolean(KEY_PULSE_ENABLED, false)) return;

            if (hour != -1 && workName != null) {
                OptimizerMiniPulseScheduler.reschedule(
                        ctx,
                        hour,
                        workName
                );
            }
        } catch (Throwable ignore) {}
    }
}
