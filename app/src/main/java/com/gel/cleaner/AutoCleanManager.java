package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Batch opener: ανοίγει διαδοχικά το settings screen κάθε app.
 * Η πρόσβαση/κλικ γίνεται από το GELAccessibilityService.
 */
public class AutoCleanManager {

    private static final Deque<String> QUEUE = new ArrayDeque<>();
    private static boolean running = false;

    public static synchronized void startBatch(Context ctx, List<String> pkgs) {
        if (pkgs == null || pkgs.isEmpty()) return;
        QUEUE.clear();
        QUEUE.addAll(pkgs);
        running = true;
        openNext(ctx);
    }

    public static synchronized void stop() {
        QUEUE.clear();
        running = false;
    }

    static synchronized String peek() { return QUEUE.peekFirst(); }

    static synchronized void onTargetFinished(Context ctx) {
        if (!running) return;
        if (!QUEUE.isEmpty()) QUEUE.removeFirst();
        openNext(ctx);
    }

    private static void openNext(Context ctx) {
        String next = QUEUE.peekFirst();
        if (next == null) { running = false; return; }
        try {
            Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            it.setData(Uri.fromParts("package", next, null));
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(it);
        } catch (Exception ignored) {}
    }

    /** Πρόχειρη λίστα δημοφιλών apps για καθάρισμα. Βάλε δικά σου. */
    public static List<String> commonTargets() {
        ArrayList<String> L = new ArrayList<>();
        L.add("com.android.chrome");
        L.add("org.mozilla.firefox");
        L.add("com.whatsapp");
        L.add("org.telegram.messenger");
        L.add("com.facebook.katana");
        L.add("com.instagram.android");
        L.add("com.tiktok.android");
        return L;
    }
}
