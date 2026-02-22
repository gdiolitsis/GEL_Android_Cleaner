// GDiolitsis Engine Lab (GEL) — Author & Developer
// OptimizerIntents.java — FINAL (Safe Settings Routing • OEM Friendly)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public final class OptimizerIntents {

    private OptimizerIntents() {}

    public static void openStorageSettings(Context c) {
    if (c == null) return;

    if (!tryStart(c, new Intent("android.settings.INTERNAL_STORAGE_SETTINGS"))) {
        if (!tryStart(c, new Intent("android.settings.MANAGE_STORAGE"))) {
            tryStart(c, new Intent(Settings.ACTION_SETTINGS));
        }
    }
}

    public static void openBatterySettings(Context c) {
        if (c == null) return;

        // Many ROMs support at least one of these.
        if (!tryStart(c, new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))) {
            // Deprecated on some, still works on many.
            if (!tryStart(c, new Intent(Intent.ACTION_POWER_USAGE_SUMMARY))) {
                tryStart(c, new Intent(Settings.ACTION_SETTINGS));
            }
        }
    }

    public static void openDataUsageSettings(Context c) {
        if (c == null) return;
        if (!tryStart(c, new Intent(Settings.ACTION_DATA_USAGE_SETTINGS))) {
            if (!tryStart(c, new Intent(Settings.ACTION_WIRELESS_SETTINGS))) {
                tryStart(c, new Intent(Settings.ACTION_SETTINGS));
            }
        }
    }

    public static void openApplicationSettings(Context c) {
        if (c == null) return;
        if (!tryStart(c, new Intent(Settings.ACTION_APPLICATION_SETTINGS))) {
            tryStart(c, new Intent(Settings.ACTION_SETTINGS));
        }
    }

    public static void openAppDetails(Context c, String pkg) {
        if (c == null) return;
        if (pkg == null) pkg = c.getPackageName();

        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.fromParts("package", pkg, null));

        if (!tryStart(c, i)) {
            openApplicationSettings(c);
        }
    }

    private static boolean tryStart(Context c, Intent i) {
        try {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
