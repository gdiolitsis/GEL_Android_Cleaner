// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerMiniPulseScheduler.java — FINAL

package com.gel.cleaner;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class OptimizerMiniPulseScheduler {

    private static final String WORK_09 = "mini_pulse_09";
    private static final String WORK_15 = "mini_pulse_15";
    private static final String WORK_21 = "mini_pulse_21";

    // =========================================================
    // PUBLIC API
    // =========================================================

    public static void enable(Context context) {
        Context appCtx = context.getApplicationContext();
        scheduleAt(appCtx, 9, WORK_09);
        scheduleAt(appCtx, 15, WORK_15);
        scheduleAt(appCtx, 21, WORK_21);
    }

    public static void disable(Context context) {
        Context appCtx = context.getApplicationContext();
        WorkManager wm = WorkManager.getInstance(appCtx);
        wm.cancelUniqueWork(WORK_09);
        wm.cancelUniqueWork(WORK_15);
        wm.cancelUniqueWork(WORK_21);
    }

    public static void reschedule(Context context, int hour, String workName) {
        scheduleAt(context.getApplicationContext(), hour, workName);
    }

    // =========================================================
    // INTERNAL SCHEDULING
    // =========================================================

    private static void scheduleAt(Context context, int hour, String workName) {

        long delay = computeInitialDelay(hour);

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(OptimizerMiniScheduler.class)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .setInputData(new Data.Builder()
                                .putInt("hour", hour)
                                .putString("workName", workName)
                                .build())
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(
                        workName,
                        ExistingWorkPolicy.REPLACE,
                        request
                );
    }

    private static long computeInitialDelay(int targetHour) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime nextRun = now.withHour(targetHour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).toMillis();
    }
}
