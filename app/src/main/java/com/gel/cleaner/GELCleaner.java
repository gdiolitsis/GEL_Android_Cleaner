package com.gel.cleaner;

import android.content.Context;
import android.webkit.WebStorage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void ok(LogCallback cb, String s) { if (cb != null) cb.log("✅ " + s, false); }
    private static void fail(LogCallback cb, String s){ if (cb != null) cb.log("❌ " + s, true);  }

    public static void safeClean(Context ctx, LogCallback cb) {
        ok(cb, "Safe Clean started…");
        try { WebStorage.getInstance().deleteAllData(); ok(cb,"Web storage cleared"); } catch (Exception e) { fail(cb,"Web storage"); }
        if (wipe(ctx.getCacheDir())) ok(cb,"Internal cache cleaned"); else fail(cb,"Internal cache");
        File ext = ctx.getExternalCacheDir();
        if (ext != null) {
            if (wipe(ext)) ok(cb,"External cache cleaned"); else fail(cb,"External cache");
        }
        ok(cb, "Safe Clean completed");
    }

    public static void browserCache(Context ctx, LogCallback cb) {
        ok(cb, "Browser Cache (best effort) started…");
        // Δεν αγγίζουμε άλλες εφαρμογές (Play Store policy). Μόνο δικά μας temp.
        ok(cb, "Nothing to clear (policy-safe)");
        ok(cb, "Browser Cache completed");
    }

    public static void mediaJunk(Context ctx, LogCallback cb) {
        ok(cb, "Media Junk (thumbnails) skipped per policy");
        ok(cb, "Media Junk completed");
    }

    public static void tempClean(Context ctx, LogCallback cb) {
        ok(cb, "Temp Clean started…");
        // app-scope temp
        File t = new File(ctx.getCacheDir(), "tmp");
        if (wipe(t)) ok(cb,"Temp removed"); else ok(cb,"No temp found");
        ok(cb, "Temp Clean completed");
    }

    public static void cleanRAM(Context ctx, LogCallback cb) {
        ok(cb, "RAM trim started…");
        try {
            Runtime.getRuntime().gc();
            ok(cb, "Suggested GC()");
            // Αποφεύγουμε clearApplicationUserData (έκλεινε το app).
            ok(cb, "RAM trim completed (policy-safe)");
        } catch (Exception e) {
            fail(cb, "RAM trim");
        }
    }

    public static void cleanAll(Context ctx, LogCallback cb) {
        ok(cb, "Clean-All started");
        try { safeClean(ctx, cb); } catch (Exception ignore) {}
        try { tempClean(ctx, cb); } catch (Exception ignore) {}
        try { browserCache(ctx, cb); } catch (Exception ignore) {}
        try { mediaJunk(ctx, cb); } catch (Exception ignore) {}
        ok(cb, "Clean-All completed");
    }

    public static String cpuInfo() {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
        } catch (Exception e) {
            sb.append("Unavailable");
        }
        return sb.toString();
    }

    public static String cpuLive() {
        long max = Runtime.getRuntime().maxMemory();
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return "Heap used: " + (used/1024/1024) + "MB / " + (max/1024/1024) + "MB";
    }

    private static boolean wipe(File f) {
        if (f == null || !f.exists()) return false;
        if (f.isFile()) return f.delete();
        File[] list = f.listFiles();
        if (list != null) for (File c : list) wipe(c);
        return f.delete() || !f.exists();
    }
}
