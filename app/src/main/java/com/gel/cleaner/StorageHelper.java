package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.documentfile.provider.DocumentFile;

public class StorageHelper {

    private static final String PREFS = "gel_prefs";
    private static final String KEY_TREE_URI = "tree_uri_android_data";

    /** Ζήτα από τον χρήστη πρόσβαση στο Android/data (μία φορά) */
    public static void requestAndroidDataAccess(Activity activity) {
        Uri androidData = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, androidData);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        activity.startActivityForResult(intent, 9911);
    }

    /** Κράτα μόνιμα το permission */
    public static void persistResult(Context ctx, Intent data) {
        if (data == null) return;
        Uri treeUri = data.getData();
        if (treeUri == null) return;
        final int flags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            ctx.getContentResolver().takePersistableUriPermission(treeUri, flags);
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_TREE_URI, treeUri.toString())
                    .apply();
        } catch (Exception ignored) {}
    }

    public static Uri getSavedTreeUri(Context ctx) {
        String s = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TREE_URI, null);
        return s == null ? null : Uri.parse(s);
    }

    /** Καθάρισε όλα τα */cache/* κάτω από Android/data με SAF (όπου έχουμε πρόσβαση) */
    public static int cleanAndroidDataCaches(Context ctx, GELCleaner.LogCallback cb) {
        Uri tree = getSavedTreeUri(ctx);
        if (tree == null) {
            if (cb != null) cb.log("❌ Δεν υπάρχει άδεια για Android/data. Ρύθμιση → Enhanced Clean off.", true);
            return 0;
        }
        DocumentFile root = DocumentFile.fromTreeUri(ctx, tree);
        if (root == null || !root.canRead()) {
            if (cb != null) cb.log("❌ Αδυναμία πρόσβασης στο Android/data", true);
            return 0;
        }
        int deleted = 0;
        for (DocumentFile appDir : root.listFiles()) {
            if (!appDir.isDirectory()) continue;
            DocumentFile cache = appDir.findFile("cache");
            if (cache != null && cache.isDirectory()) {
                deleted += deleteRecursively(cache);
                if (cb != null) cb.log("✅ Cache καθαρίστηκε: " + appDir.getName(), false);
            }
            // Συνήθεις extra φάκελοι temp
            DocumentFile tmp = appDir.findFile("tmp");
            if (tmp != null && tmp.isDirectory()) deleted += deleteRecursively(tmp);
        }
        if (cb != null) cb.log("✅ Enhanced clean (Android/data) ολοκληρώθηκε", false);
        return deleted;
    }

    private static int deleteRecursively(DocumentFile f) {
        int count = 0;
        if (f == null) return 0;
        if (f.isDirectory()) {
            for (DocumentFile c : f.listFiles()) count += deleteRecursively(c);
        }
        if (f.delete()) count++;
        return count;
    }
}
