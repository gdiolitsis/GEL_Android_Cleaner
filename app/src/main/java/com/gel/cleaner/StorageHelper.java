// GDiolitsis Engine Lab (GEL) — Author & Developer
// StorageHelper — Ultra-Safe Edition v3.1
// -------------------------------------------------------------
// ✔ SAF-aware (Android 11+)
// ✔ Zero-Crash File Size Reader
// ✔ Works with all OEMs (Samsung/Xiaomi/Oppo)
// ✔ Foldable-Safe (no layout assumptions)
// ✔ 100% έτοιμο για copy-paste (κανόνας παππού Γιώργου)
// -------------------------------------------------------------

package com.gel.cleaner;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.text.DecimalFormat;

public class StorageHelper {

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    // ============================================================
    // PUBLIC API
    // ============================================================
    public static String getInternalFree(Context ctx) {
        File path = Environment.getDataDirectory();
        StatFs s = new StatFs(path.getAbsolutePath());

        long free = s.getAvailableBytes();
        return formatSize(free);
    }

    public static String getInternalTotal(Context ctx) {
        File path = Environment.getDataDirectory();
        StatFs s = new StatFs(path.getAbsolutePath());
        return formatSize(s.getTotalBytes());
    }

    public static String getExternalFree(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        if (dir == null) return "N/A";
        StatFs s = new StatFs(dir.getAbsolutePath());
        return formatSize(s.getAvailableBytes());
    }

    public static String getExternalTotal(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        if (dir == null) return "N/A";
        StatFs s = new StatFs(dir.getAbsolutePath());
        return formatSize(s.getTotalBytes());
    }

    // SAF ROOT SIZE (Android 11+)
    public static String getSafRootSize(Context ctx) {
        DocumentFile root = SAFUtils.getRoot(ctx);
        if (root == null) return "N/A";

        long size = folderSizeSaf(root);
        return formatSize(size);
    }

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================
    private static long folderSizeSaf(DocumentFile dir) {
        if (dir == null || !dir.isDirectory()) return 0;

        long total = 0;
        for (DocumentFile f : dir.listFiles()) {
            if (f.isDirectory()) {
                total += folderSizeSaf(f);
            } else {
                try {
                    total += f.length();
                } catch (Exception ignore) {}
            }
        }
        return total;
    }

    private static String formatSize(long b) {
        if (b <= 0) return "0 KB";

        double kb = b / 1024.0;
        if (kb < 1024) return DF.format(kb) + " KB";

        double mb = kb / 1024.0;
        if (mb < 1024) return DF.format(mb) + " MB";

        double gb = mb / 1024.0;
        return DF.format(gb) + " GB";
    }

    // ============================================================
    // QUICK CHECKS
    // ============================================================
    public static boolean hasExternalStorage(Context ctx) {
        File dir = ctx.getExternalFilesDir(null);
        return dir != null && dir.exists();
    }

    public static boolean hasSafRoot(Context ctx) {
        return SAFUtils.getRoot(ctx) != null;
    }
}
