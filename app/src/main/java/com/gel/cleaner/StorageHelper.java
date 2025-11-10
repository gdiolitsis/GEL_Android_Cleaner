package com.gel.cleaner;

import android.app.Activity;
import android.net.Uri;
import android.provider.DocumentsContract;

public class StorageHelper {

    /**
     * Καθαρίζει ό,τι cache μπορούμε μέσω SAF (χωρίς root)
     */
    public static void cleanSystem(Activity act) {
        openSAF(act, "Android/data");
    }

    /**
     * Καθαρίζει media & junk αρχεία
     */
    public static void cleanMedia(Activity act) {
        openSAF(act, "DCIM/.thumbnails");
        openSAF(act, "Pictures");
    }

    /**
     * Καθαρίζει προσωρινά temp αρχεία
     */
    public static void cleanTemp(Activity act) {
        openSAF(act, "Download");
        openSAF(act, "tmp");
    }

    /**
     * Άνοιγμα SAF dialog για συγκεκριμένο φάκελο
     */
    private static void openSAF(Activity act, String relative) {
        try {
            Uri uri = DocumentsContract.buildDocumentUri(
                    "com.android.externalstorage.documents",
                    "primary:" + relative
            );
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            act.startActivity(intent);
        } catch (Exception ignored) { }
    }
}
