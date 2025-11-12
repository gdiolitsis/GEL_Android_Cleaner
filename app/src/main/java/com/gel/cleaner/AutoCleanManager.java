package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * GEL — AutoCleanManager v2.1
 * Batch opener → ανοίγει τα app settings screens
 * Το cleaning κλικ γίνεται από:
 *   → GELAccessibilityService
 */
public class AutoCleanManager {

    private static final Deque<String> QUEUE = new ArrayDeque<>();
    private static boolean running = false;

    // μικρό buffer → πιο σταθερό opening
    private static final Handler h = new Handler();

    /**
     * Ξεκινά παρτίδα app-cache cleaning.
     */
    public static synchronized void startBatch(Context ctx, List<String> pkgs) {
        if (pkgs == null || pkgs.isEmpty()) return;

        QUEUE.clear();
        QUEUE.addAll(pkgs);
        running = true;
        openNext(ctx);
    }

    /**
     * Τερματισμός batch.
     */
    public static synchronized void stop() {
        QUEUE.clear();
        running = false;
    }

    /**
     * Το τρέχον target που θέλει καθάρισμα.
     */
    static synchronized String peek() {
        return QUEUE.peekFirst();
    }

    /**
     * Καλείται από Accessibility όταν τελειώσει το clearing.
     */
    static synchronized void onTargetFinished(Context ctx) {
        if (!running) return;

        if (!QUEUE.isEmpty()) {
            QUEUE.removeFirst();
        }

        openNext(ctx);
    }

    /**
     * Άνοιξε settings της επόμενης εφαρμογής.
     */
    private static void openNext(Context ctx) {
        String next = QUEUE.peekFirst();

        if (next == null) {
            running = false;
            return;
        }

        // Μικρή καθυστέρηση → προλαβαίνει να φορτώσει το UI
        h.postDelayed(() -> {
            try {
                Intent it = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                it.setData(Uri.fromParts("package", next, null));
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ctx.startActivity(it);
            } catch (Exception ignored) {
                // Αν κάτι σπάσει → πήδα στο επόμενο
                onTargetFinished(ctx);
            }
        }, 120);
    }

    /**
     * Demo list.
     * Προφανώς σε production → φέρνουμε δυναμικά.
     */
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
