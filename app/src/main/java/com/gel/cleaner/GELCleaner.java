package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

public class GELCleaner {

    // ========== LOG ==========
    public interface LogCallback { void log(String msg, boolean isError); }
    static void ok(LogCallback c, String m){ if(c!=null) c.log("✅ " + m, false); }
    static void fail(LogCallback c, String m){ if(c!=null) c.log("❌ " + m, true); }

    // ========== Helpers ==========
    private static void deleteRecursive(File f){
        try {
            if (f == null || !f.exists()) return;
            if (f.isDirectory()) {
                File[] list = f.listFiles();
                if (list != null) for (File x : list) deleteRecursive(x);
            }
            // ignore result
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        } catch (Exception ignored) {}
    }

    // ========== BASIC (παντού) ==========
    public static void safeClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);

            // Public / thumbnails / downloads tmp / logs
            deleteRecursive(new File("/storage/emulated/0/DCIM/.thumbnails"));
            deleteRecursive(new File("/storage/emulated/0/Download/.tmp"));
            deleteRecursive(new File("/storage/emulated/0/Download/.cache"));
            deleteRecursive(new File("/storage/emulated/0/MIUI/debug_log"));
            deleteRecursive(new File("/storage/emulated/0/tencent"));

            ok(cb, "Safe Clean completed");
        } catch (Exception e){
            fail(cb, "Safe Clean failed");
        }
    }

    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    // Συμβατός τρόπος: trim background
                    List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                    if (procs != null) for (ActivityManager.RunningAppProcessInfo p : procs)
                        am.killBackgroundProcesses(p.processName);
                }
            }
            ok(cb, "RAM: ζητήθηκε τερματισμός background διεργασιών");
        } catch (Exception e){
            fail(cb, "RAM clean failed");
        }
    }

    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/storage/emulated/0/DCIM/.thumbnails"));
            deleteRecursive(new File("/storage/emulated/0/Pictures/.thumbnails"));
            ok(cb, "Media junk cleaned");
        } catch (Exception e){
            fail(cb, "Media junk failed");
        }
    }

    public static void tempClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/storage/emulated/0/Android/media/.cache"));
            deleteRecursive(new File("/storage/emulated/0/Android/media/.temp"));
            deleteRecursive(new File("/storage/emulated/0/.logs"));
            ok(cb, "Temp cleaned");
        } catch (Exception e){
            fail(cb, "Temp failed");
        }
    }

    public static void browserCache(Context ctx, LogCallback cb){
        // Χωρίς root δεν αγγίζουμε /data/data άλλων apps. 
        // Θα ανοίξεις App Info από το AppListActivity.
        ok(cb, "Άνοιξε τη λίστα εφαρμογών για manual Clear cache.");
    }

    public static void killApps(Context ctx, LogCallback cb){
        cleanRAM(ctx, cb);
    }

    public static void boostBattery(Context ctx, LogCallback cb){
        ok(cb, "Tip: ενεργοποίησε Battery Optimization & Adaptive Battery στα Settings.");
    }

    // ========== CPU Info ==========
    public static void cpuInfo(Context ctx, LogCallback cb){
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            ok(cb, "Cores: " + cores);
            String f = readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            if (f != null) ok(cb, "Freq: " + f.trim() + " Hz");
        } catch (Exception e){ fail(cb, "CPU Info"); }
    }
    public static void cpuLive(Context ctx, LogCallback cb){
        try {
            float u = readUsage();
            ok(cb, "CPU usage: " + u + "%");
        } catch (Exception e){ fail(cb, "CPU Live"); }
    }
    private static float readUsage() throws Exception {
        long[] t1 = readStat(); Thread.sleep(300); long[] t2 = readStat();
        long idle = t2[0]-t1[0]; long total = t2[1]-t1[1];
        return (float)(100.0 * (total - idle) / total);
    }
    private static long[] readStat() throws Exception {
        RandomAccessFile r = new RandomAccessFile("/proc/stat","r");
        String load = r.readLine(); r.close();
        String[] toks = load.split(" +");
        long idle = Long.parseLong(toks[4]);
        long total = 0; for (int i=1;i<toks.length;i++) total += Long.parseLong(toks[i]);
        return new long[]{idle,total};
    }
    private static String readFile(String p){
        try { RandomAccessFile r=new RandomAccessFile(p,"r"); String s=r.readLine(); r.close(); return s; }
        catch(Exception e){ return null; }
    }

    // ========== ENHANCED (SAF) ==========
    public static void enhancedCleanAndroidData(Context ctx, LogCallback cb){
        int n = StorageHelper.cleanAndroidDataCaches(ctx, cb);
        ok(cb, "Enhanced clean finished (" + n + " entries)");
    }

    // ========== CLEAN ALL ==========
    public static void cleanAll(Context ctx, LogCallback cb){
        ok(cb, "Clean-All started");
        safeClean(ctx, cb);
        mediaJunk(ctx, cb);
        tempClean(ctx, cb);
        enhancedCleanAndroidData(ctx, cb);
        cleanRAM(ctx, cb);
        ok(cb, "Clean-All completed");
    }
}
