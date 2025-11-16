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
// UNIVERSAL EDITION — Works on ALL devices
// ============================================================
public class GelActions {

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    // ============================================================
    // SMART CLEAN (Universal RAM Cleaner)
    // ============================================================
    public static void doSmartClean(Activity activity) {
        CleanLauncher.smartClean(activity);
        Toast.makeText(activity, "✔ Smart Cleaner ενεργοποιήθηκε", Toast.LENGTH_SHORT).show();
    }

    // ============================================================
    // BATTERY BOOSTER — Play-Safe σε όλες τις συσκευές
    // ============================================================
    public static void openBatteryBooster(Activity activity) {

        // 1) Battery Saver (universal)
        try {
            Intent intent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            Toast.makeText(activity, "⚡ Battery Saver ανοίχτηκε", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ignored) {}

        // 2) Usage Access (fallback)
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            Toast.makeText(activity, "⚡ Άνοιγμα Battery Usage", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ignored) {}

        // 3) Last fallback
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        Toast.makeText(activity, "⚡ Ρυθμίσεις Μπαταρίας", Toast.LENGTH_SHORT).show();
    }

    // ============================================================
    // OWN APP CACHE CLEAN — με αναφορά
    // ============================================================
    public static void cleanOwnCache(Context context) {
        long before = getFolderSize(context.getCacheDir()) +
                getFolderSize(context.getExternalCacheDir());

        deleteDirSafe(context.getCacheDir());
        deleteDirSafe(context.getExternalCacheDir());

        long diff = before;

        Toast.makeText(context,
                "🧹 Cache καθαρίστηκε: " + formatSize(diff),
                Toast.LENGTH_LONG).show();
    }

    // ============================================================
    // UNIVERSAL TEMP FILES CLEANER — για ΟΛΕΣ τις συσκευές
    // ============================================================
    public static void cleanTempFiles(Context ctx) {

        // ---------- 1) Xiaomi / Redmi / Poco (MIUI / HyperOS) ----------
        if (isMiui()) {
            if (launch(ctx, "com.miui.cleaner", "com.miui.cleaner.MainActivity")) {
                Toast.makeText(ctx, "🗑 MIUI Cleaner → Temp Files", Toast.LENGTH_LONG).show();
                return;
            }
            if (launch(ctx, "com.miui.securitycenter", "com.miui.securityscan.MainActivity")) {
                Toast.makeText(ctx, "🗑 MIUI Security Cleaner", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // ---------- 2) Samsung ----------
        if (launch(ctx,
                "com.samsung.android.lool",
                "com.samsung.android.lool.MainActivity")) {
            Toast.makeText(ctx, "🗑 Samsung Device Care", Toast.LENGTH_LONG).show();
            return;
        }

        if (launch(ctx,
                "com.samsung.android.devicecare",
                "com.samsung.android.devicecare.ui.DeviceCareActivity")) {
            Toast.makeText(ctx, "🗑 Samsung Storage Cleaner", Toast.LENGTH_LONG).show();
            return;
        }

        // ---------- 3) Oppo / Realme ----------
        if (launch(ctx,
                "com.coloros.phonemanager",
                "com.coloros.phonemanager.main.MainActivity")) {
            Toast.makeText(ctx, "🗑 ColorOS Cleaner", Toast.LENGTH_LONG).show();
            return;
        }

        // ---------- 4) OnePlus ----------
        if (launch(ctx,
                "com.oneplus.security",
                "com.oneplus.security.cleaner.CleanerActivity")) {
            Toast.makeText(ctx, "🗑 OnePlus Cleaner", Toast.LENGTH_LONG).show();
            return;
        }

        // ---------- 5) Vivo / iQOO ----------
        if (launch(ctx,
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity")) {
            Toast.makeText(ctx, "🗑 Vivo Phone Optimizer", Toast.LENGTH_LONG).show();
            return;
        }

        // ---------- 6) Huawei / Honor ----------
        if (launch(ctx,
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.spaceclean.SpaceCleanActivity")) {
            Toast.makeText(ctx, "🗑 Huawei Space Cleaner", Toast.LENGTH_LONG).show();
            return;
        }

        // ---------- 7) Pixel / Motorola / Sony / γενικές συσκευές ----------
        try {
            Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            Toast.makeText(ctx, "📦 Storage → Temporary / Junk Files", Toast.LENGTH_LONG).show();
            return;
        } catch (Exception ignored) {}

        // ---------- 8) Last fallback ----------
        Toast.makeText(ctx, "⚠ Δεν βρέθηκε temp cleaner.", Toast.LENGTH_LONG).show();
        Intent fallback = new Intent(Settings.ACTION_SETTINGS);
        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(fallback);
    }

    // ============================================================
    // STORAGE MANAGER
    // ============================================================
    public static void openStorageManager(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            Toast.makeText(activity, "📦 Storage Manager", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
            Toast.makeText(activity, "📦 Storage Settings", Toast.LENGTH_SHORT).show();
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private static boolean launch(Context ctx, String pkg, String cls) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(pkg, cls));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isMiui() {
        String brand = Build.BRAND.toLowerCase();
        String manu = Build.MANUFACTURER.toLowerCase();
        return (brand.contains("xiaomi") || brand.contains("redmi") || brand.contains("poco")
                || manu.contains("xiaomi") || manu.contains("redmi") || manu.contains("poco"));
    }

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
        if (children != null) for (File f : children) size += getFolderSize(f);
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
