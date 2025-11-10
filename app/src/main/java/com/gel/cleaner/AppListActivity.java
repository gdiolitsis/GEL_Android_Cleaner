package com.gel.cleaner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private static final int REQ_TREE = 9911;

    private List<ResolveInfo> apps;
    private PackageManager pm;

    // Θα καθαρίσουμε cache για αυτό το package μετά το SAF grant
    private String pendingPackage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        pm = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        apps = pm.queryIntentActivities(mainIntent, 0);
        if (apps == null) apps = new ArrayList<>();
        Collections.sort(apps, (a, b) ->
                String.valueOf(a.loadLabel(pm)).compareToIgnoreCase(String.valueOf(b.loadLabel(pm))));

        ListView list = findViewById(R.id.listApps);
        list.setAdapter(new AppAdapter());
        list.setOnItemClickListener(onItemClick);
    }

    private final AdapterView.OnItemClickListener onItemClick = (parent, view, position, id) -> {
        ResolveInfo info = apps.get(position);
        String pkg = info.activityInfo.packageName;
        pendingPackage = pkg;

        // Ανοίγουμε SAF για να δώσει ο χρήστης πρόσβαση στον φάκελο Android/data[/<pkg>]
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);

        // Προσπαθούμε να ξεκινήσουμε μέσα στο Android/data/<pkg> (αν το επιτρέψει η συσκευή)
        try {
            Uri base = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                i.putExtra(DocumentsContract.EXTRA_INITIAL_URI, base);
            }
        } catch (Throwable ignored) {}

        startActivityForResult(i, REQ_TREE);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_TREE || data == null) return;

        Uri treeUri = data.getData();
        if (treeUri == null) {
            Toast.makeText(this, "No folder selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Κρατάμε μόνιμα το permission
        try {
            final int flags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, flags);
        } catch (Exception ignored) {}

        // Καθαρισμός cache για το επιλεγμένο package
        if (pendingPackage == null) {
            Toast.makeText(this, "No package selected", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = cleanAppCacheViaSaf(treeUri, pendingPackage);
        if (ok) {
            Toast.makeText(this, "Cache cleaned for " + pendingPackage, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Could not clean cache. Pick Android/data or the app folder.", Toast.LENGTH_LONG).show();
            // Προτείνουμε App Info για manual "Clear cache"
            try {
                Intent info = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                info.setData(Uri.parse("package:" + pendingPackage));
                startActivity(info);
            } catch (Exception ignored) {}
        }
        pendingPackage = null;
    }

    /**
     * Προσπαθεί να βρει τον φάκελο Android/data/<pkg> από το treeUri που έδωσε ο χρήστης
     * και να σβήσει τα περιεχόμενα των "cache" και "code_cache".
     */
    private boolean cleanAppCacheViaSaf(Uri treeUri, String pkg) {
        try {
            DocumentFile picked = DocumentFile.fromTreeUri(this, treeUri);
            if (picked == null) return false;

            // Μπορεί ο χρήστης να διάλεξε:
            // 1) Android/data
            // 2) Android/data/<pkg>
            // 3) Κάτι άλλο – αποτυγχάνει

            DocumentFile targetAppDir = null;

            String name = picked.getName() != null ? picked.getName() : "";
            if (equalsIgnoreCase(name, "data")) {
                // Είμαστε μέσα στο Android/data
                targetAppDir = findChild(picked, pkg);
            } else if (equalsIgnoreCase(name, pkg)) {
                // Είμαστε ήδη στον φάκελο της εφαρμογής
                targetAppDir = picked;
            } else {
                // Δοκιμή μήπως ο parent είναι το "data"
                DocumentFile parentGuess = picked.getParentFile();
                if (parentGuess != null && equalsIgnoreCase(parentGuess.getName(), "data")) {
                    if (equalsIgnoreCase(name, pkg)) targetAppDir = picked;
                }
            }

            if (targetAppDir == null || !targetAppDir.isDirectory()) return false;

            boolean any = false;
            DocumentFile cache = findChild(targetAppDir, "cache");
            if (cache != null && cache.isDirectory()) {
                any |= deleteContents(cache);
            }
            DocumentFile codeCache = findChild(targetAppDir, "code_cache");
            if (codeCache != null && codeCache.isDirectory()) {
                any |= deleteContents(codeCache);
            }
            return any;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean deleteContents(DocumentFile dir) {
        boolean any = false;
        try {
            for (DocumentFile f : dir.listFiles()) {
                any |= recursiveDelete(f);
            }
        } catch (Exception ignored) {}
        return any;
    }

    private boolean recursiveDelete(DocumentFile df) {
        boolean ok = true;
        try {
            if (df.isDirectory()) {
                for (DocumentFile c : df.listFiles()) {
                    ok &= recursiveDelete(c);
                }
            }
            ok &= df.delete();
        } catch (Exception e) {
            ok = false;
        }
        return ok;
    }

    private DocumentFile findChild(DocumentFile parent, String name) {
        try {
            for (DocumentFile f : parent.listFiles()) {
                if (equalsIgnoreCase(f.getName(), name)) return f;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    // ====== Adapter ======
    private class AppAdapter extends BaseAdapter {
        @Override public int getCount() { return apps.size(); }
        @Override public Object getItem(int i) { return apps.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(AppListActivity.this)
                        .inflate(R.layout.list_item_app, parent, false);
            }
            ResolveInfo info = apps.get(position);
            ImageView icon = v.findViewById(R.id.appIcon);
            TextView name  = v.findViewById(R.id.appName);
            TextView pkg   = v.findViewById(R.id.appPkg);

            icon.setImageDrawable(info.loadIcon(pm));
            name.setText(info.loadLabel(pm));
            pkg.setText(info.activityInfo.packageName);
            return v;
        }
    }
}
