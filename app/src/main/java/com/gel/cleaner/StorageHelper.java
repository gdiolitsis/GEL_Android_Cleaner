package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

/**
 * StorageHelper — SAF Bridge
 * Full Play-Store-safe + auto persist + final
 * 2025 — GEL
 */
public class StorageHelper {

    /**
     * Callback: όταν ο χρήστης επιλέξει SAF root → γυρίζει στο Activity result,
     * όπου γίνεται save αυτόματα μέσω SAFCleaner.saveTreeUri(...)
     */
    public static final int REQ_SAF_ANDROID_DATA = 801;
    public static final int REQ_SAF_THUMBNAILS   = 802;

    /* ===========================================================
     *  OPEN: Android/data
     * ===========================================================
     */
    public static void openAndroidDataSAF(Context ctx) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );

        // Προσπαθούμε να ανοίξουμε πάνω στο Android/data
        Uri base = DocumentsContract.buildDocumentUri(
                "com.android.externalstorage.documents",
                "primary:Android/data"
        );
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, base);

        launch(ctx, intent, REQ_SAF_ANDROID_DATA);
    }

    /* ===========================================================
     *  OPEN: DCIM thumbnails
     * ===========================================================
     */
    public static void openThumbnailsSAF(Context ctx) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );

        Uri base = DocumentsContract.buildDocumentUri(
                "com.android.externalstorage.documents",
                "primary:DCIM"
        );
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, base);

        launch(ctx, intent, REQ_SAF_THUMBNAILS);
    }

    /* ===========================================================
     *  INTERNAL: LAUNCH
     * ===========================================================
     */
    private static void launch(Context ctx, Intent intent, int code) {

        if (ctx instanceof Activity) {
            ((Activity) ctx).startActivityForResult(intent, code);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        }
    }

    /* ===========================================================
     *  HANDLE RESULT
     * ===========================================================
     */
    public static boolean handleResult(Context ctx, int requestCode, int resultCode, Intent data) {

        if (data == null) return false;

        Uri tree = data.getData();
        if (tree == null) return false;

        if (requestCode == REQ_SAF_ANDROID_DATA
                || requestCode == REQ_SAF_THUMBNAILS) {

            // SAVE — persist permission + store in prefs
            SAFCleaner.saveTreeUri(ctx, tree);
            return true;
        }
        return false;
    }
}
