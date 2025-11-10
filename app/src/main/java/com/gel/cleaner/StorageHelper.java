package com.gel.cleaner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class StorageHelper {

    public static final int REQ_SAF = 991;

    /**
     * Ζητάμε SAF για Android/data
     */
    public static void requestAccess(Activity a) {

        try {
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            );

            a.startActivityForResult(intent, REQ_SAF);

        } catch (Exception e) {
            Toast.makeText(a, "SAF request failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    /**
     * Called from Activity.onActivityResult
     */
    public static void onSAFResult(@Nullable Intent data, Activity a) {

        if (data == null) {
            Toast.makeText(a, "Access denied", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri tree = data.getData();
        if (tree == null) {
            Toast.makeText(a, "Invalid URI", Toast.LENGTH_SHORT).show();
            return;
        }

        // Keep permission
        final int flags = data.getFlags() &
                (Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            a.getContentResolver().takePersistableUriPermission(tree, flags);
            Toast.makeText(a, "✅ SAF Granted", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(a, "Permission not granted", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
