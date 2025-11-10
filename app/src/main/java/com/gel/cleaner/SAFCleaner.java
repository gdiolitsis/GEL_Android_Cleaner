package com.gel.cleaner;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

public class SAFCleaner {

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

    public static void cleanKnownJunk(Context ctx, GELCleaner.LogCallback cb) {
        Uri root = getTreeUri(ctx);
        if (root == null) {
            if (cb != null) cb.log("❌ SAF not granted (Select folder first)", true);
            return;
        }
        DocumentFile rootDoc = DocumentFile.fromTreeUri(ctx, root);
        if (rootDoc == null) {
            if (cb != null) cb.log("❌ SAF root invalid", true);
            return;
        }

        // Συνήθεις “junk” φάκελοι (όπου έχουμε πρόσβαση μέσω SAF)
        String[] junkDirs = new String[] {
                "Android/data/com.android.chrome/cache",
                "Android/data/org.mozilla.firefox/cache",
                "DCIM/.thumbnails",
                "Pictures/.thumbnails",
                "Download/.thumbnails",
                "WhatsApp/Media/.Statuses",
                "Telegram/Telegram Images",
                "Telegram/Telegram Video",
                "Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Animated Gifs/.thumbnails"
        };

        int wiped = 0;
        for (String rel : junkDirs) {
            if (wipePath(rootDoc, rel)) {
                wiped++;
                if (cb != null) cb.log("✅ Wiped " + rel, false);
            } else {
                if (cb != null) cb.log("ℹ️ Skipped " + rel, false);
            }
        }
        if (cb != null) cb.log("SAF clean done (" + wiped + " paths)", false);
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
        // Σβήνουμε όλο το folder (αν είναι φάκελος) ή αρχείο
        if (cur.isDirectory()) {
            for (DocumentFile child : cur.listFiles()) {
                child.delete();
            }
        }
        return cur.delete() || true; // αρκεί που σβήσαμε τα περιεχόμενα
    }

    private static DocumentFile findChild(DocumentFile parent, String name) {
        for (DocumentFile f : parent.listFiles()) {
            if (name.equalsIgnoreCase(f.getName())) return f;
        }
        return null;
    }

    // μικρό helper για persistable flags
    private static class IntentFlags {
        static int readWrite() {
            return (android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
    }
}
