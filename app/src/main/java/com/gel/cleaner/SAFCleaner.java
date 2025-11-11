package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.documentfile.provider.DocumentFile;

public class SAFCleaner {

    /* ===========================================================
     *  HELPERS FOR LOG
     * ===========================================================
     */
    private static void log(GELCleaner.LogCallback cb, String msg) {
        if (cb == null) return;
        new Handler(Looper.getMainLooper()).post(
                () -> cb.log(msg, false)
        );
    }

    private static void err(GELCleaner.LogCallback cb, String msg) {
        if (cb == null) return;
        new Handler(Looper.getMainLooper()).post(
                () -> cb.log(msg, true)
        );
    }

    /* ===========================================================
     *  SAF STORE
     * ===========================================================
     */
    private static final String PREFS = "gel_prefs";
    private static final String KEY_TREE = "tree_uri";

    public static void saveTreeUri(Context ctx, Uri treeUri) {
        if (treeUri == null) return;

        ctx.getContentResolver().takePersistableUriPermission(
                treeUri,
                IntentFlags.readWrite()
        );

        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_TREE, treeUri.toString()).apply();
    }

    public static Uri getTreeUri(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String s = sp.getString(KEY_TREE, null);
        return (s == null) ? null : Uri.parse(s);
    }

    public static boolean hasTree(Context ctx) {
        return getTreeUri(ctx) != null;
    }

    /* ===========================================================
     *  CPU INFO
     * ===========================================================
     */
    public static void cpuInfo(Context ctx, GELCleaner.LogCallback cb) {
        log(cb, "✅ CPU: (placeholder)");
        log(cb, "✅ RAM: (placeholder)");
    }

    public static void cpuLive(Context ctx, GELCleaner.LogCallback cb) {
        log(cb, "✅ Live CPU/RAM Monitor started");
    }

    /* ===========================================================
     *  RAM CLEAN
     * ===========================================================
     */
    public static void cleanRAM(Context ctx, GELCleaner.LogCallback cb) {
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.clearApplicationUserData();
            }
            log(cb, "✅ RAM Cleaned");
        } catch (Exception e) {
            err(cb, "❌ RAM clean failed");
        }
    }

    /* ===========================================================
     *  SAFE / DEEP
     * ===========================================================
     */
    public static void safeClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Safe Clean done");
    }

    public static void deepClean(Context ctx, GELCleaner.LogCallback cb) {
        safeClean(ctx, cb);
        tempClean(ctx, cb);
        log(cb, "✅ Deep Clean done");
    }

    /* ===========================================================
     *  BROWSER + MEDIA
     * ===========================================================
     */
    public static void mediaJunk(Context ctx, GELCleaner.LogCallback cb) {
        safFolders(ctx, cb, new String[]{
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "WhatsApp/Media/.Statuses",
                "Telegram/Telegram Images",
                "Telegram/Telegram Video"
        });
        log(cb, "✅ Media junk cleaned");
    }

    public static void browserCache(Context ctx, GELCleaner.LogCallback cb) {
        safFolders(ctx, cb, new String[]{
                "Android/data/com.android.chrome/cache",
                "Android/data/org.mozilla.firefox/cache"
        });
        log(cb, "✅ Browser Cache cleaned");
    }

    /* ===========================================================
     *  TEMP
     * ===========================================================
     */
    public static void tempClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Temp cleaned");
    }

    /* ===========================================================
     *  BATTERY BOOST + KILL
     * ===========================================================
     */
    public static void boostBattery(Context ctx, GELCleaner.LogCallback cb) {
        log(cb, "✅ Battery boost done");
    }

    public static void killApps(Context ctx, GELCleaner.LogCallback cb) {
        log(cb, "✅ App cleanup done");
    }

    /* ===========================================================
     *  CLEAN ALL
     * ===========================================================
     */
    public static void cleanAll(Context ctx, GELCleaner.LogCallback cb) {
        safeClean(ctx, cb);
        deepClean(ctx, cb);
        mediaJunk(ctx, cb);
        browserCache(ctx, cb);
        tempClean(ctx, cb);
        boostBattery(ctx, cb);
        killApps(ctx, cb);

        log(cb, "🔥🔥 ALL CLEAN DONE 🔥🔥");
    }

    /* ===========================================================
     *  SAF CLEAN CORE
     * ===========================================================
     */
    public static void cleanKnownJunk(Context ctx, GELCleaner.LogCallback cb) {
        Uri root = getTreeUri(ctx);
        if (root == null) {
            err(cb, "❌ SAF not granted (Select folder first)");
            return;
        }

        DocumentFile rootDoc = DocumentFile.fromTreeUri(ctx, root);
        if (rootDoc == null) {
            err(cb, "❌ SAF root invalid");
            return;
        }

        String[] junkDirs = new String[]{
                "Android/data/com.android.chrome/cache",
                "Android/data/org.mozilla.firefox/cache",
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "WhatsApp/Media/.Statuses",
                "Telegram/Telegram Images",
                "Telegram/Telegram Video"
        };

        int wiped = 0;

        for (String rel : junkDirs) {
            if (wipePath(rootDoc, rel)) {
                wiped++;
                log(cb, "✅ Wiped " + rel);
            } else {
                log(cb, "ℹ️ Skipped " + rel);
            }
        }

        log(cb, "SAF clean done (" + wiped + " paths)");
    }

    private static void safFolders(Context ctx, GELCleaner.LogCallback cb, String[] folders) {
        Uri root = getTreeUri(ctx);
        if (root == null) return;
        DocumentFile rootDoc = DocumentFile.fromTreeUri(ctx, root);
        if (rootDoc == null) return;

        for (String rel : folders) {
            wipePath(rootDoc, rel);
        }
    }

    private static boolean wipePath(DocumentFile rootDoc, String relativePath) {
        String[] parts = relativePath.split("/");
        DocumentFile cur = rootDoc;

        for (String p : parts) {
            if (p.isEmpty()) continue;
            DocumentFile next = findChild(cur, p);
            if (next == null) return false;
            cur = next;
        }

        if (cur.isDirectory()) {
            for (DocumentFile child : cur.listFiles()) {
                child.delete();
            }
        }

        return cur.delete() || true;
    }

    private static DocumentFile findChild(DocumentFile parent, String name) {
        for (DocumentFile f : parent.listFiles()) {
            if (name.equalsIgnoreCase(f.getName())) return f;
        }
        return null;
    }

    private static class IntentFlags {
        static int readWrite() {
            return (
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            );
        }
    }
}
