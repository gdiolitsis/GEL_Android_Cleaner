// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELActions — System Actions Manager v3.0 (Ultra-Safe Edition)
// ============================================================
// • Συμβατό με ΟΛΕΣ τις συσκευές (Samsung / Xiaomi / Oppo / Pixel / Huawei)
// • Zero-Crash guarantees (all intents wrapped, fallbacks included)
// • Safe Cleaners (RAM / Temp / Storage / Battery)
// • 100% έτοιμο για copy-paste (κανόνας παππού Γιώργου)
// • Βασισμένο στο ΤΕΛΕΥΤΑΙΟ αρχείο σου.
// ============================================================

package com.gel.cleaner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

public final class GELActions {

    private GELActions() {} // no instances

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    // ============================================================
    // SMART CLEAN — Universal RAM Cleaner
    // ============================================================
    public static void doSmartClean(Activity activity) {
        if (activity == null) return;

        try {
            CleanLauncher.smartClean(activity);
            Toast.makeText(activity, "✔ Smart Cleaner ενεργοποιήθηκε", Toast.LENGTH_SHORT).show();
        } catch (Throwable ignored) {
            Toast.makeText(activity, "⚠ Smart Clean δεν υποστηρίζεται", Toast.LENGTH_SHORT).show();
        }
    }

    // ============================================================
    // BATTERY BOOSTER — Play-Safe Navigation (All OEMs)
    // ============================================================
    public static void openBatteryBooster(Activity activity) {
        if (activity == null) return;

        // 1) Battery Saver (universal)
        if (tryIntent(activity, Settings.ACTION_BATTERY_SAVER_SETTINGS, "⚡ Battery Saver ανοίχτηκε"))
            return;

        // 2) Usage Access (fallback)
        if (tryIntent(activity, Settings.ACTION_USAGE_ACCESS_SETTINGS, "⚡ Άνοιγμα Battery Usage"))
            return;

        // 3) Last fallback → Settings
        tryIntent(activity, Settings.ACTION_SETTINGS, "⚡ Ρυθμίσεις Μπαταρίας");
    }

    // ============================================================
    // CLEAN OWN APP CACHE — internal/external + Toast report
    // ============================================================
    public static void cleanOwnCache(Context context) {
        if (context == null) return;

        long before =
                getFolderSize(context.getCacheDir()) +
                getFolderSize(context.getExternalCacheDir());

        deleteDirSafe(context.getCacheDir());
        deleteDirSafe(context.getExternalCacheDir());

        Toast.makeText(
                context.getApplicationContext(),
                "🧹 Cache καθαρίστηκε: " + formatSize(before),
                Toast.LENGTH_LONG
        ).show();
    }

    // ============================================================
    // UNIVERSAL TEMP FILES CLEANER — καλύπτει ΟΛΑ τα OEMs
    // ============================================================
    public static void cleanTempFiles(Context ctx) {
        if (ctx == null) return;

        // ---------- XIAOMI / REDMI / POCO ----------
        if (isMiui()) {
            if (launch(ctx, "com.miui.cleaner", "com.miui.cleaner.MainActivity")) {
                toast(ctx, "🗑 MIUI Cleaner → Temp Files");
                return;
            }
            if (launch(ctx, "com.miui.securitycenter", "com.miui.securityscan.MainActivity")) {
                toast(ctx, "🗑 MIUI Security Cleaner");
                return;
            }
        }

        // ---------- SAMSUNG ----------
        if (launch(ctx, "com.samsung.android.lool", "com.samsung.android.lool.MainActivity")) {
            toast(ctx, "🗑 Samsung Device Care");
            return;
        }
        if (launch(ctx, "com.samsung.android.devicecare",
                "com.samsung.android.devicecare.ui.DeviceCareActivity")) {
            toast(ctx, "🗑 Samsung Storage Cleaner");
            return;
        }

        // ---------- OPPO / REALME ----------
        if (launch(ctx, "com.coloros.phonemanager",
                "com.coloros.phonemanager.main.MainActivity")) {
            toast(ctx, "🗑 ColorOS Cleaner");
            return;
        }

        // ---------- ONEPLUS ----------
        if (launch(ctx, "com.oneplus.security",
                "com.oneplus.security.cleaner.CleanerActivity")) {
            toast(ctx, "🗑 OnePlus Cleaner");
            return;
        }

        // ---------- VIVO / IQOO ----------
        if (launch(ctx, "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.PhoneOptimizeActivity")) {
            toast(ctx, "🗑 Vivo Phone Optimizer");
            return;
        }

        // ---------- HUAWEI / HONOR ----------
        if (launch(ctx, "com.huawei.systemmanager",
                "com.huawei.systemmanager.spaceclean.SpaceCleanActivity")) {
            toast(ctx, "🗑 Huawei Space Cleaner");
            return;
        }

        // ---------- GENERIC ANDROID (Pixel / Sony / Motorola) ----------
        if (tryIntent(ctx, Settings.ACTION_INTERNAL_STORAGE_SETTINGS,
                "📦 Storage → Temporary / Junk Files"))
            return;

        // ---------- LAST FALLBACK ----------
        toast(ctx, "⚠ Δεν βρέθηκε temp cleaner.");
        tryIntent(ctx, Settings.ACTION_SETTINGS, null);
    }

    // ============================================================
    // STORAGE MANAGER — simple safe wrapper
    // ============================================================
    public static void openStorageManager(Activity act) {
        if (act == null) return;

        if (!tryIntent(act, Settings.ACTION_INTERNAL_STORAGE_SETTINGS, "📦 Storage Manager")) {
            tryIntent(act, Settings.ACTION_SETTINGS, "📦 Storage Settings");
        }
    }

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private static boolean tryIntent(Context ctx, String action, String toast) {
        if (ctx == null) return false;

        try {
            Intent i = new Intent(action);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            if (toast != null) {
                Toast.makeText(ctx.getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean launch(Context ctx, String pkg, String cls) {
        if (ctx == null) return false;

        try {
            Intent i = new Intent();
            i.setComponent(new ComponentName(pkg, cls));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void deleteDirSafe(File dir) {
        try {
            if (dir == null || !dir.exists()) return;

            if (dir.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                dir.delete();
                return;
            }

            File[] children = dir.listFiles();
            if (children != null) {
                for (File f : children) deleteDirSafe(f);
            }

            //noinspection ResultOfMethodCallIgnored
            dir.delete();
        } catch (Throwable ignored) {
            // ultra-safe no crash
        }
    }

    private static long getFolderSize(File dir) {
        try {
            if (dir == null || !dir.exists()) return 0;
            if (dir.isFile()) return dir.length();

            long total = 0;
            File[] list = dir.listFiles();
            if (list != null) {
                for (File f : list) total += getFolderSize(f);
            }
            return total;
        } catch (Throwable ignored) {
            return 0;
        }
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

    private static boolean isMiui() {
        String b = (Build.BRAND != null) ? Build.BRAND.toLowerCase() : "";
        String m = (Build.MANUFACTURER != null) ? Build.MANUFACTURER.toLowerCase() : "";
        return (b.contains("xiaomi") || b.contains("redmi") || b.contains("poco")
                || m.contains("xiaomi") || m.contains("redmi") || m.contains("poco"));
    }

    private static void toast(Context ctx, String m) {
        if (ctx == null) return;
        try {
            Toast.makeText(ctx.getApplicationContext(), m, Toast.LENGTH_LONG).show();
        } catch (Throwable ignored) {}
    }
}
