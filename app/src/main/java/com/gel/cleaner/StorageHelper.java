package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import java.util.List;

/**
 * StorageHelper
 * - SAF (Storage Access Framework) άνοιγμα / αποθήκευση άδειας
 * - Καθαρισμός κοινών cache/temporary φακέλων όπου υπάρχει πρόσβαση μέσω SAF
 *
 * Σημείωση:
 * Δεν χρησιμοποιούμε "επικίνδυνα" permissions. Ό,τι σβήνεται, σβήνεται μέσω SAF
 * με επίμονη (persistable) άδεια του χρήστη πάνω στον φάκελο (συνήθως: root του Internal Storage).
 */
public class StorageHelper {

    // Request code για startActivityForResult / Activity Result API
    public static final int REQUEST_CODE_SAF = 0x1001;

    // ---------- Δημόσιες βοηθητικές κλήσεις ----------

    /**
     * Ζήτα από τον χρήστη να διαλέξει έναν ρίζα-φάκελο (συνίσταται το Internal Storage).
     * Συνήθως ο χρήστης επιλέγει "This device" / "Internal storage".
     */
    public static void requestStorageAccess(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // να επιτρέπεται η μόνιμη άδεια (persistable)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);

        activity.startActivityForResult(intent, REQUEST_CODE_SAF);
    }

    /**
     * Κάλεσέ το από το onActivityResult του Activity που έκανε requestStorageAccess(...)
     * για να αποθηκεύσουμε την άδεια SAF μόνιμα (persistable).
     */
    public static boolean handleStorageResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REQUEST_CODE_SAF || resultCode != Activity.RESULT_OK || data == null)
            return false;

        Uri treeUri = data.getData();
        if (treeUri == null) return false;

        // Πάρε persistable rights
        final int takeFlags = (data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));

        try {
            activity.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            return true;
        } catch (SecurityException se) {
            return false;
        }
    }

    /**
     * Έχουμε ήδη άδεια; (έστω για οποιονδήποτε επιλεγμένο tree)
     */
    public static boolean hasPersistedAccess(Context ctx) {
        List<UriPermission> perms = ctx.getContentResolver().getPersistedUriPermissions();
        return perms != null && !perms.isEmpty() && perms.get(0).isWritePermission();
    }

    /**
     * Επιστρέφει το πρώτο persisted tree (αν υπάρχει), αλλιώς null.
     */
    @Nullable
    public static Uri getPersistedTree(Context ctx) {
        List<UriPermission> perms = ctx.getContentResolver().getPersistedUriPermissions();
        if (perms != null) {
            for (UriPermission p : perms) {
                if (p.isWritePermission()) return p.getUri();
            }
        }
        return null;
    }

    // ---------- “Καθάρισμα” φακέλων μέσω SAF ----------

    /**
     * Καθάρισε τυπικούς cache/temporary φακέλους προσβάσιμους μέσω SAF.
     * Δεν απαιτεί root. Χρειάζεται να υπάρχει persisted SAF access (δες hasPersistedAccess).
     *
     * Επιστρέφει true αν έγινε οποιαδήποτε διαγραφή.
     */
    public static boolean cleanCommonCaches(Context ctx, GELCleaner.LogCallback cb) {
        Uri tree = getPersistedTree(ctx);
        if (tree == null) {
            addFail(cb, "No SAF permission. Go to Settings → grant storage access.");
            return false;
        }

        // Ριζικό DocumentFile από το SAF tree
        DocumentFile root = DocumentFile.fromTreeUri(ctx, tree);
        if (root == null || !root.canWrite()) {
            addFail(cb, "SAF root not writable.");
            return false;
        }

        boolean anyDeleted = false;

        // Συνήθεις διαδρομές (relative στο root που επέλεξε ο χρήστης)
        // Προσπαθούμε “ήπιο” καθάρισμα – ΜΟΝΟ cache/thumbnails/tmp φακέλων
        anyDeleted |= deleteIfExists(ctx, root, "Android/data/*/cache", cb);
        anyDeleted |= deleteIfExists(ctx, root, "Android/obb/*/.tmp", cb);
        anyDeleted |= deleteIfExists(ctx, root, "DCIM/.thumbnails", cb);
        anyDeleted |= deleteIfExists(ctx, root, "Pictures/.thumbnails", cb);
        anyDeleted |= deleteIfExists(ctx, root, "Download/.tmp", cb);
        anyDeleted |= deleteIfExists(ctx, root, "Download/.cache", cb);
        anyDeleted |= deleteIfExists(ctx, root, "cache", cb); // root-level "cache" αν υπάρχει

        if (anyDeleted) {
            addOk(cb, "SAF clean finished ✅");
        } else {
            addOk(cb, "Nothing to clean (SAF) • OK");
        }
        return anyDeleted;
    }

    // ---------- Εσωτερικά helpers ----------

    private static void addOk(GELCleaner.LogCallback cb, String msg) {
        if (cb != null) cb.log("✅ " + msg, false);
    }

    private static void addFail(GELCleaner.LogCallback cb, String msg) {
        if (cb != null) cb.log("❌ " + msg, true);
    }

    /**
     * Διαγραφή φακέλων/αρχείων με pattern. Υποστηρίζει “*” σε ένα επίπεδο.
     * Παράδειγμα: "Android/data/*/cache"
     */
    private static boolean deleteIfExists(Context ctx, DocumentFile root, String relativePattern, GELCleaner.LogCallback cb) {
        try {
            String[] parts = relativePattern.split("/");
            return deletePatternRecursive(ctx, root, parts, 0, cb);
        } catch (Throwable t) {
            addFail(cb, "SAF delete failed: " + relativePattern);
            return false;
        }
    }

    private static boolean deletePatternRecursive(Context ctx, DocumentFile current, String[] parts, int idx, GELCleaner.LogCallback cb) {
        if (idx >= parts.length) return false;

        String part = parts[idx];

        if ("*".equals(part)) {
            // Ταιριάζει όλα τα παιδιά του τρέχοντος
            DocumentFile[] children = current.listFiles();
            boolean any = false;
            for (DocumentFile ch : children) {
                any |= deletePatternRecursive(ctx, ch, parts, idx + 1, cb);
            }
            return any;
        } else {
            // Προχωράμε στον επόμενο κόμβο (αν υπάρχει) ή διαγράφουμε αν αυτό ήταν το τελευταίο μέρος
            DocumentFile next = findChild(current, part);
            if (next == null) return false;

            if (idx == parts.length - 1) {
                // Τελικός κόμβος — σβήσε τον
                boolean ok = deleteDocumentTree(next);
                if (ok) addOk(cb, "Deleted: " + getDocPath(next));
                return ok;
            } else {
                return deletePatternRecursive(ctx, next, parts, idx + 1, cb);
            }
        }
    }

    private static DocumentFile findChild(DocumentFile dir, String name) {
        if (!dir.isDirectory()) return null;
        for (DocumentFile f : dir.listFiles()) {
            if (name.equals(f.getName())) return f;
        }
        return null;
    }

    private static boolean deleteDocumentTree(DocumentFile file) {
        // Αν είναι φάκελος: σβήσε αναδρομικά όλα τα παιδιά
        if (file.isDirectory()) {
            for (DocumentFile child : file.listFiles()) {
                deleteDocumentTree(child); // αγνόησε επιμέρους αποτυχίες
            }
        }
        try {
            return file.delete();
        } catch (Throwable t) {
            return false;
        }
    }

    private static String getDocPath(DocumentFile f) {
        try {
            Uri uri = f.getUri();
            String docId = DocumentsContract.getDocumentId(uri);
            return docId != null ? docId : uri.toString();
        } catch (Throwable t) {
            return f.getUri().toString();
        }
    }
}
