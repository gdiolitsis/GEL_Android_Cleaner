package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

public class StorageHelper {

    public static void cleanSystem(Activity act) {
        openSAF(act, "Android/data");
    }

    public static void cleanMedia(Activity act) {
        openSAF(act, "DCIM/.thumbnails");
        openSAF(act, "Pictures");
    }

    public static void cleanTemp(Activity act) {
        openSAF(act, "Download");
        openSAF(act, "tmp");
    }

    private static void openSAF(Activity act, String relative) {
        try {
            Uri uri = DocumentsContract.buildDocumentUri(
                    "com.android.externalstorage.documents",
                    "primary:" + relative
            );

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            act.startActivity(intent);

        } catch (Exception ignored) {}
    }
}
