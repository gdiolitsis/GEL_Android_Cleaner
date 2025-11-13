package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.documentfile.provider.DocumentFile;

/**
 * SAFCleaner — FINAL v3.3
 * - Safe, silent, ΔΕΝ δημιουργεί φακέλους
 * - Μετράει πόσο SAF space ελευθερώθηκε (MB)
 * GDiolitsis Engine Lab (GEL) — 2025
 *
 * Rule: Πάντα στέλνουμε ολόκληρο το τελικό αρχείο έτοιμο για copy-paste.
 */
public class SAFCleaner {

    /* ===========================================================
     * LOG HELPERS
     * =========================================================== */
    private static void log(GELCleaner.LogCallback cb, String msg) {
        if (cb == null) return;
        new Handler(Looper.getMainLooper()).post(() -> cb.log(msg, false));
    }

    private static void err(GELCleaner.LogCallback cb, String msg) {
        if (cb == null) return;
        new Handler(Looper.getMainLooper()).post(() -> cb.log(msg, true));
    }

    /* ===========================================================
     * SAF STORAGE
     * =========================================================== */
    private static final String PREFS = "gel_prefs";
    private static final String KEY_TREE = "tree_uri";

    /** Αποθήκευση SAF root ΜΟΝΟ την πρώτη φορά */
    public static void saveTreeUri(Context ctx, Uri treeUri) {
        if (treeUri == null) return;

        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (sp.getString(KEY_TREE, null) != null) return; // ήδη υπάρχει

        try {
            ctx.getContentResolver().takePersistableUriPermission(
                    treeUri,
                    IntentFlags.readWrite()
            );
        } catch (Exception ignored) {}

        sp.edit().putString(KEY_TREE, treeUri.toString()).apply();
    }

    public static Uri getTreeUri(Context ctx) {
        String s = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_TREE, null);
        return (s == null) ? null : Uri.parse(s);
    }

    public static boolean hasTree(Context ctx) {
        return getTreeUri(ctx) != null;
    }


    /* ===========================================================
     * PUBLIC CLEAN FUNCTIONS
     * =========================================================== */
    public static void safeClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Safe Clean done");
    }

    public static void deepClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        tempClean(ctx, cb);
        thumbnailScanAndDelete(ctx, cb);
        log(cb, "✅ Deep Clean done");
    }

    public static void mediaJunk(Context ctx, GELCleaner.LogCallback cb) {
        thumbnailScanAndDelete(ctx, cb);
        log(cb, "✅ Media Junk done");
    }

    public static void browserCache(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Browser Cache done");
    }

    public static void tempClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Temp Clean done");
    }

    public static void cleanAll(Context ctx, GELCleaner.LogCallback cb) {
        safeClean(ctx, cb);
        deepClean(ctx, cb);
        mediaJunk(ctx, cb);
        browserCache(ctx, cb);
        tempClean(ctx, cb);

        log(cb, "🔥🔥 ALL CLEAN DONE 🔥🔥");
    }


    /* ===========================================================
     * MAIN KNOWN PATH CLEANER (με freed MB)
     * =========================================================== */
    public static void cleanKnownJunk(Context ctx, GELCleaner.LogCallback cb) {

        Uri root = getTreeUri(ctx);
        if (root == null) {
            err(cb, "❌ SAF not granted");
            return;
        }

        DocumentFile rootDoc = DocumentFile.fromTreeUri(ctx, root);
        if (rootDoc == null) {
            err(cb, "❌ SAF root invalid");
            return;
        }

        String[] junk = new String[]{

                // Browsers
                "Android/data/com.android.chrome/cache",
                "Android/data/com.android.chrome/app_chrome/Default/Cache",
                "Android/data/com.google.android.webview/cache",
                "Android/data/org.mozilla.firefox/cache",
                "Android/data/org.mozilla.firefox_beta/cache",
                "Android/data/com.microsoft.emmx/cache",
                "Android/data/com.opera.browser/cache",
                "Android/data/com.opera.mini.native/cache",
                "Android/data/com.brave.browser/cache",
                "Android/data/com.duckduckgo.mobile.android/cache",

                // WhatsApp / Telegram
                "WhatsApp/Media/.Statuses",
                "WhatsApp/.Shared",
                "Android/media/com.whatsapp/WhatsApp/Media/.Statuses",
                "Android/media/com.whatsapp/WhatsApp/.Shared",
                "Android/data/com.whatsapp/cache",
                "Android/data/com.whatsapp/wallpaper",
                "Android/data/org.telegram.messenger/cache",
                "Android/data/org.telegram.messenger.beta/cache",
                "Telegram/Telegram Images",
                "Telegram/Telegram Video",
                "Telegram/Telegram Documents",
                "Android/media/org.telegram.messenger/Telegram/Telegram Images",
                "Android/media/org.telegram.messenger/Telegram/Telegram Video",
                "Android/media/org.telegram.messenger/Telegram/Telegram Documents",

                // Viber / FB / Insta / TikTok / Snapchat
                "Android/data/com.viber.voip/cache",
                "Android/data/com.facebook.katana/cache",
                "Android/data/com.facebook.orca/cache",
                "Android/data/com.facebook.mlite/cache",
                "Android/data/com.instagram.android/cache",
                "Android/data/com.ss.android.ugc.trill/cache",
                "Android/data/com.snapchat.android/cache",

                // Streaming
                "Android/data/com.google.android.youtube/cache",
                "Android/data/com.google.android.videos/cache",
                "Android/data/com.netflix.mediaclient/cache",
                "Android/data/com.spotify.music/cache",

                // Thumbnails
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "Movies/.thumbnails",
                "WhatsApp/.thumbnails",

                // Temp / Logs
                "Android/data/.tmp",
                "Android/data/.thumbnails",
                "Android/data/.log",
                "Download/.temp",
                "Download/.cache",
                "MIUI/debug_log",
                "MIUI/.cache"
        };

        int deletedFolders = 0;
        long totalFreedBytes = 0L;

        for (String rel : junk) {
            long freed = wipeFolderWithSize(rootDoc, rel);
            if (freed > 0) {
                deletedFolders++;
                totalFreedBytes += freed;
                log(cb, "🗑 " + rel + "  (" + formatMB(freed) + " MB)");
            }
        }

        log(cb, "✅ Cleaned folders: " + deletedFolders);
        if (totalFreedBytes > 0) {
            log(cb, "💾 SAF freed: " + formatMB(totalFreedBytes) + " MB");
        }
    }


    /* ===========================================================
     * THUMBNAILS SCAN
     * =========================================================== */
    private static void thumbnailScanAndDelete(Context ctx, GELCleaner.LogCallback cb) {

        Uri root = getTreeUri(ctx);
        if (root == null) return;

        DocumentFile rootDoc = DocumentFile.fromTreeUri(ctx, root);
        if (rootDoc == null) return;

        String[] rels = {
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "Movies/.thumbnails",
                "WhatsApp/.thumbnails"
        };

        long bytes = 0;
        int count = 0;

        for (String rel : rels) {
            ThumbnailReport r = deleteThumbs(rootDoc, rel);
            count += r.count;
            bytes += r.bytes;
        }

        if (count == 0) {
            log(cb, "ℹ️ No thumbnails found");
        } else {
            log(cb, "📸 Deleted " + count + " thumbnails");
            log(cb, "💾 Freed " + formatMB(bytes) + " MB");
        }
    }

    private static class ThumbnailReport {
        int count = 0;
        long bytes = 0;
    }

    private static ThumbnailReport deleteThumbs(DocumentFile root, String rel) {
        ThumbnailReport rep = new ThumbnailReport();

        DocumentFile folder = traverse(root, rel);
        if (folder == null) return rep;

        for (DocumentFile f : folder.listFiles()) {
            if (f.isFile()) {
                long size = f.length();
                if (f.delete()) {
                    rep.count++;
                    rep.bytes += size;
                }
            }
        }
        return rep;
    }


    /* ===========================================================
     * FS HELPERS — SAFE & SILENT (χωρίς δημιουργία φακέλων)
     * =========================================================== */
    private static DocumentFile traverse(DocumentFile root, String rel) {
        if (root == null || rel == null) return null;

        String[] parts = rel.split("/");
        DocumentFile cur = root;

        for (String p : parts) {
            if (p.isEmpty()) continue;
            cur = findChild(cur, p);
            if (cur == null) return null; // αν λείπει κάτι, σταματάμε ήσυχα
        }
        return cur;
    }

    /**
     * Σβήνει ΟΛΑ τα παιδιά του φακέλου και επιστρέφει τα bytes που ελευθερώθηκαν.
     * Δεν δημιουργεί ποτέ νέο φάκελο.
     */
    private static long wipeFolderWithSize(DocumentFile root, String rel) {
        DocumentFile folder = traverse(root, rel);
        if (folder == null) return 0L;

        long freed = 0L;

        for (DocumentFile f : folder.listFiles()) {
            try {
                long sz = f.length();
                if (f.delete()) {
                    freed += sz;
                }
            } catch (Exception ignored) {}
        }

        try {
            long sz = folder.length(); // συνήθως 0, αλλά το κρατάμε
            if (folder.delete()) {
                freed += sz;
            }
        } catch (Exception ignored) {}

        return freed;
    }

    private static DocumentFile findChild(DocumentFile parent, String name) {
        if (parent == null || name == null) return null;

        for (DocumentFile f : parent.listFiles()) {
            if (name.equalsIgnoreCase(f.getName())) {
                return f;
            }
        }
        return null;
    }

    private static String formatMB(long b) {
        return String.format("%.2f", (b / 1024f / 1024f));
    }

    /* ===========================================================
     * FLAGS
     * =========================================================== */
    private static class IntentFlags {
        static int readWrite() {
            return Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
        }
    }
}
