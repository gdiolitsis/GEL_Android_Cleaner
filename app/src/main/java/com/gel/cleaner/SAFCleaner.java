package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * SAF cleaner — FINAL (w/ thumbnail report)
 * GEL — 2025
 * Rule: πάντα στέλνουμε ολόκληρο το τελικό αρχείο έτοιμο για copy-paste.
 */
public class SAFCleaner {

    /* ===========================================================
     *  LOG HELPERS
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
     *  SAF STORAGE
     * ===========================================================
     */
    private static final String PREFS   = "gel_prefs";
    private static final String KEY_TREE = "tree_uri";

    /**
     * Αποθηκεύει ΜΟΝΟ την ΠΡΩΤΗ φορά το treeUri.
     * Αν υπάρχει ήδη τιμή στο KEY_TREE, δεν την αντικαθιστά.
     */
    public static void saveTreeUri(Context ctx, Uri treeUri) {
        if (treeUri == null) return;

        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String existing = sp.getString(KEY_TREE, null);
        if (existing != null && !existing.isEmpty()) {
            // Ήδη έχουμε ρίζα → δεν την αλλάζουμε (first-run only)
            return;
        }

        try {
            ctx.getContentResolver().takePersistableUriPermission(
                    treeUri,
                    IntentFlags.readWrite()
            );
        } catch (Exception ignored) {}

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
     *  SAFE / DEEP CLEAN
     * ===========================================================
     */
    public static void safeClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Safe Clean done");
    }

    public static void deepClean(Context ctx, GELCleaner.LogCallback cb) {
        safeClean(ctx, cb);
        tempClean(ctx, cb);
        thumbnailScanAndDelete(ctx, cb);
        log(cb, "✅ Deep Clean done");
    }


    /* ===========================================================
     *  MEDIA JUNK
     * ===========================================================
     */
    public static void mediaJunk(Context ctx, GELCleaner.LogCallback cb) {
        thumbnailScanAndDelete(ctx, cb);
        log(cb, "✅ Media junk finished");
    }


    /* ===========================================================
     *  BROWSER
     * ===========================================================
     */
    public static void browserCache(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Browser cache finished");
    }


    /* ===========================================================
     *  TEMP
     * ===========================================================
     */
    public static void tempClean(Context ctx, GELCleaner.LogCallback cb) {
        cleanKnownJunk(ctx, cb);
        log(cb, "✅ Temp Clean done");
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

        log(cb, "🔥🔥 ALL CLEAN DONE 🔥🔥");
    }


    /* ===========================================================
     *  MAIN — wipe known folders
     * ===========================================================
     */
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

        // Κύριες διαδρομές “σκουπιδιών” (rel paths από τη ρίζα που διάλεξε ο χρήστης)
        String[] junkDirs = new String[]{
                // --- Browsers (Chrome, Firefox, Edge, Opera, Brave, DuckDuckGo, WebView) ---
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

                // --- WhatsApp / Telegram ---
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

                // --- Viber / Messenger / FB / Instagram / TikTok / Snapchat ---
                "Android/data/com.viber.voip/cache",
                "Android/data/com.facebook.katana/cache",
                "Android/data/com.facebook.orca/cache",
                "Android/data/com.facebook.mlite/cache",
                "Android/data/com.instagram.android/cache",
                "Android/data/com.zhiliaoapp.musically/cache",   // παλιό TikTok
                "Android/data/com.ss.android.ugc.trill/cache",   // νέο TikTok
                "Android/data/com.snapchat.android/cache",

                // --- YouTube / streaming ---
                "Android/data/com.google.android.youtube/cache",
                "Android/data/com.google.android.videos/cache",
                "Android/data/com.netflix.mediaclient/cache",
                "Android/data/com.spotify.music/cache",

                // --- Thumbnails / media previews ---
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "Movies/.thumbnails",
                "WhatsApp/.thumbnails",
                "Android/DCIM/.thumbnails",

                // --- Γενικά temp / logs σε γνωστές ρίζες ---
                "Android/data/.tmp",
                "Android/data/.thumbnails",
                "Android/data/.log",
                "Download/.temp",
                "Download/.cache",
                "MIUI/debug_log",
                "MIUI/.cache"
        };

        int okCount = 0;

        for (String rel : junkDirs) {
            if (wipePath(rootDoc, rel)) {
                okCount++;
                log(cb, "✅ Wiped " + rel);
            } else {
                log(cb, "ℹ️ Skip " + rel);
            }
        }

        log(cb, "✅ SAF Clean paths = " + okCount);
    }


    /* ===========================================================
     *  THUMBNAIL SCAN
     * ===========================================================
     */
    private static void thumbnailScanAndDelete(Context ctx, GELCleaner.LogCallback cb) {
        Uri root = getTreeUri(ctx);
        if (root == null) return;

        DocumentFile rootDoc = DocumentFile.fromTreeUri(ctx, root);
        if (rootDoc == null) return;

        String[] paths = {
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "Movies/.thumbnails",
                "WhatsApp/.thumbnails"
        };

        long totalBytes = 0;
        int totalFiles = 0;

        for (String rel : paths) {
            ThumbnailReport rep = deleteThumbs(rootDoc, rel);
            totalBytes += rep.bytes;
            totalFiles += rep.count;
        }

        if (totalFiles > 0) {
            log(cb, "📸 Thumbnails found: " + totalFiles);
            log(cb, "🗑 Deleted: " + formatMB(totalBytes) + " MB");
        } else {
            log(cb, "ℹ️ No thumbnails found");
        }
    }

    private static class ThumbnailReport {
        int count = 0;
        long bytes = 0;
    }

    private static ThumbnailReport deleteThumbs(DocumentFile root, String rel) {
        ThumbnailReport r = new ThumbnailReport();

        DocumentFile folder = traverse(root, rel);
        if (folder == null) return r;

        for (DocumentFile f : folder.listFiles()) {
            if (f.isFile()) {
                long sz = f.length();
                if (f.delete()) {
                    r.count++;
                    r.bytes += sz;
                }
            }
        }
        return r;
    }

    private static String formatMB(long b) {
        return String.format("%.1f", (b / 1024f / 1024f));
    }


    /* ===========================================================
     *  PATH TRAVERSE
     * ===========================================================
     */
    private static DocumentFile traverse(DocumentFile root, String rel) {
        String[] parts = rel.split("/");
        DocumentFile cur = root;
        for (String p : parts) {
            if (p.isEmpty()) continue;
            cur = findChild(cur, p);
            if (cur == null) return null;
        }
        return cur;
    }

    private static boolean wipePath(DocumentFile rootDoc, String relativePath) {
        DocumentFile folder = traverse(rootDoc, relativePath);
        if (folder == null) return false;

        for (DocumentFile child : folder.listFiles()) {
            try {
                child.delete();
            } catch (Throwable ignore) {}
        }
        try {
            folder.delete();
        } catch (Throwable ignore) {}
        return true;
    }

    private static DocumentFile findChild(DocumentFile parent, String name) {
        if (parent == null) return null;
        for (DocumentFile f : parent.listFiles()) {
            if (f.getName() != null &&
                    f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    /* ===========================================================
     *  FLAGS
     * ===========================================================
     */
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
```0
