package com.gel.cleaner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class StorageHelper {

    // Άνοιξε SAF στο Android/data για manual καθαρισμό cache φακέλων
    public static void openAndroidDataSAF(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // Δεν επιτρέπεται επίσημα fixed αρχικός φάκελος στα νέα Android
        // Θα ανοίξει ο chooser και ο χρήστης θα διαλέξει.
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ctx.startActivity(intent);
    }

    // Άνοιξε SAF για DCIM/.thumbnails (ο χρήστης διαλέγει DCIM)
    public static void openThumbnailsSAF(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ctx.startActivity(intent);
    }
}
