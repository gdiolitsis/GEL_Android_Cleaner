package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import java.io.File;

// ============================================================
// GDiolitsis Engine Lab (GEL) — System Actions Manager
// Όλες οι "λειτουργίες" σε ένα σημείο
// ============================================================
public class GelActions {

    // --------------------------------------------------------
    // SMART CLEAN (Deep + RAM OEM logic)
    // --------------------------------------------------------
    public static void doSmartClean(Activity activity) {
        CleanLauncher.smartClean(activity);
    }

    // --------------------------------------------------------
    // BATTERY BOOSTER → Ανοίγει τις σωστές ρυθμίσεις μπαταρίας
    // (χωρίς root, Play Store safe)
    // --------------------------------------------------------
    public static void openBatteryBooster(Activity activity) {
        try {
            // Νεότερα Android: Battery Saver / Battery Optimization
            Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e1) {
            try {
                // Εναλλακτικό: γενικές ρυθμίσεις μπαταρίας
                Intent intent = new Intent(Settings.ACTION_POWER_USAGE_SUMMARY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } catch (Exception e2) {
                // Τελικό fallback: απλές ρυθμίσεις
                try {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                } catch (Exception ignored) {
                }
            }
        }
    }

    // --------------------------------------------------------
    // CLEAN APP CACHE (της δικής μας εφαρμογής)
    // Play Store safe: καθαρίζουμε μόνο τα δικά μας cache dirs
    // --------------------------------------------------------
    public static void cleanOwnCache(Context context) {
        deleteDirSafe(context.getCacheDir());
        deleteDirSafe(context.getExternalCacheDir());
    }

    // --------------------------------------------------------
    // TEMP FILES CLEANER (εσωτερικός φάκελος temp της εφαρμογής)
    // --------------------------------------------------------
    public static void cleanTempFiles(Context context) {
        File tempDir = new File(context.getFilesDir(), "temp");
        deleteDirSafe(tempDir);
    }

    // --------------------------------------------------------
    // STORAGE / APP SETTINGS (για χειροκίνητο καθάρισμα cache κ.λπ.)
    // Ανοίγει το επίσημο Storage / Apps panel της συσκευής
    // --------------------------------------------------------
    public static void openStorageManager(Activity activity) {
        try {
            // Γενικές ρυθμίσεις αποθήκευσης
            Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e1) {
            try {
                Intent intent = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } catch (Exception e2) {
                try {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                } catch (Exception ignored) {
                }
            }
        }
    }

    // --------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------
    private static void deleteDirSafe(File dir) {
        if (dir == null || !dir.exists()) return;
        if (dir.isFile()) {
            // best-effort delete
            //noinspection ResultOfMethodCallIgnored
            dir.delete();
            return;
        }
        File[] children = dir.listFiles();
        if (children != null) {
            for (File f : children) {
                deleteDirSafe(f);
            }
        }
        //noinspection ResultOfMethodCallIgnored
        dir.delete();
    }
}
