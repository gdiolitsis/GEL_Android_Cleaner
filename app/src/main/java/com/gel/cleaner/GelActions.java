package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

// ============================================================
// GDiolitsis Engine Lab (GEL) — System Actions Manager
// Με αναφορές τι καθαρίστηκε
// ============================================================
public class GelActions {

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    // ============================================================
    // SMART CLEAN
    // ============================================================
    public static void doSmartClean(Activity activity) {
        CleanLauncher.smartClean(activity);
        Toast.makeText(activity, "✔ Smart Cleaner ενεργοποιήθηκε", Toast.LENGTH_SHORT).show();
    }

    // ============================================================
    // BATTERY BOOSTER
    // ============================================================
    public static void openBatteryBooster(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            Toast.makeText(activity, "⚡ Άνοιγμα Battery Saver", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ignored) {}

        try {
            Intent intent = new Intent(Settings.ACTION_POWER_USAGE_SUMMARY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            Toast.makeText(activity, "⚡ Άνοιγμα Battery Usage", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ignored) {}

        Toast.makeText(activity, "⚡ Άνοιγμα Ρυθμίσεων Μπαταρίας", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    // ============================================================
    // CLEAN OWN CACHE (με αναφορά)
    // ============================================================
    public static void cleanOwnCache(Context context) {
        long before = getFolderSize(context.getCacheDir()) +
                      getFolderSize(context.getExternalCacheDir());

        deleteDirSafe(context.getCacheDir());
        deleteDirSafe(context.getExternalCacheDir());

        long after = 0;
        long diff = before - after;

        Toast.makeText(context,
                "🧹 Cache καθαρίστηκε: " + formatSize(diff),
                Toast.LENGTH_LONG).show();
    }

    // ============================================================
    // CLEAN TEMP FILES (με αναφορά)
    // ============================================================
    public static void cleanTempFiles(Context context) {
        File tempDir = new File(context.getFilesDir(), "temp");
        long before = getFolderSize(tempDir);

        deleteDirSafe(tempDir);

        long diff = before;

        Toast.makeText(context,
                "🗑 Temp files διαγράφηκαν: " + formatSize(diff),
                Toast.LENGTH_LONG).show();
    }

    // ============================================================
    // OPEN STORAGE MANAGER
    // ============================================================
    public static void openStorageManager(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            Toast.makeText(activity, "📦 Άνοιγμα Storage Manager", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(activity, "📦 Άνοιγμα Ρυθμίσεων Αποθήκευσης", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private static void deleteDirSafe(File dir) {
        if (dir == null || !dir.exists()) return;
        if (dir.isFile()) {
            dir.delete();
            return;
        }
        File[] children = dir.listFiles();
        if (children != null) {
            for (File f : children) deleteDirSafe(f);
        }
        dir.delete();
    }

    private static long getFolderSize(File dir) {
        if (dir == null || !dir.exists()) return 0;
        if (dir.isFile()) return dir.length();
        long size = 0;
        File[] children = dir.listFiles();
        if (children != null) {
            for (File f : children) size += getFolderSize(f);
        }
        return size;
    }

    private static String formatSize(long bytes) {
        if (bytes <= 0) return "0 KB";
        double kb = bytes / 1024.0;
        if (kb < 1024) return DF.format(kb) + " KB";
        double mb = kb / 1024.0;
        if (mb < 1024) return DF.format(mb) + " MB";
        double gb = mb / 1024.0;
        return DF.format(gb) + " GB";
    }
}
