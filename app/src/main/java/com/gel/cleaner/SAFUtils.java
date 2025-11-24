// GDiolitsis Engine Lab (GEL) — Author & Developer
// app/src/main/java/com/gel/cleaner/SAFUtils.java
// SAFUtils — Minimal Root Provider v1.0
// ------------------------------------------------------------
// ✔ Fixes "cannot find symbol SAFUtils"
// ✔ Bridges StorageHelper → SAFCleaner persisted tree
// ✔ No new permissions, zero-crash
// ------------------------------------------------------------

package com.gel.cleaner;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

public final class SAFUtils {

    private SAFUtils(){}

    public static DocumentFile getRoot(Context ctx) {
        try {
            Uri tree = SAFCleaner.getTreeUri(ctx);
            if (tree == null) return null;
            return DocumentFile.fromTreeUri(ctx, tree);
        } catch (Throwable t) {
            return null;
        }
    }
}
