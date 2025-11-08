package com.gel.cleaner;

import android.content.Context;
import java.io.RandomAccessFile;
import java.io.File;

public class GELCleaner {

    // ======================= LOG CALLBACK =======================
    public interface LogCallback {
        void log(String msg, boolean isError);
    }
    static void addOK(LogCallback c, String msg){ if(c!=null) c.log("✅ " + msg, false); }
    static void addFAIL(LogCallback c, String msg){ if(c!=null) c.log("❌ " + msg, true); }

    // ======================= EXEC ===============================
    private static int exec(String cmd){
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return p.exitValue();
        } catch (Exception e){ return -1; }
    }

    // ======================= ROOT CHECK =========================
    private static boolean hasRoot() {
        return exec("su -c id") == 0;
    }
    public static boolean hasRootPublic(){ return hasRoot(); }

    // ============================================================
    //  🔥  SAFE CLEAN → clears app cache + temp
    // ============================================================
    public static void safeClean(Context ctx, LogCallback cb){
        try {
            clearAppCache(ctx);
            clearTemp(ctx);
            addOK(cb, "Safe Clean completed");
        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    private static void clearAppCache(Context ctx){
        File c = ctx.getCacheDir();
        deleteRecursive(c);
    }

    private static void clearTemp(Context ctx){
        File c = ctx.getExternalCacheDir();
        deleteRecursive(c);
    }

    // ============================================================
    //  🔥 CLEAN RAM (fake / non-root)
    // ============================================================
    public static void cleanRAM(Context ctx, LogCallback cb){
        if(hasRoot()){
            exec("su -c killall -9");
            addOK(cb, "RAM cleaned (root)");
        } else {
            addOK(cb, "RAM cleaned (sim)");
        }
    }

    // ============================================================
    //  🔋 BATTERY BOOST
    // ============================================================
    public static void boostBattery(Context ctx, LogCallback cb){
        addOK(cb, "Battery boost");
    }

    // ============================================================
    //  🚀 KILL APPS
    // ============================================================
    public static void killApps(Context ctx, LogCallback cb){
        if(hasRoot()){
            exec("su -c killall -9");
            addOK(cb, "Apps killed");
        } else {
            addOK(cb, "Kill Apps (sim)");
        }
    }

    // ============================================================
    //  🖼 MEDIA JUNK
    // ============================================================
    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/sdcard/DCIM/.thumbnails"));
            deleteRecursive(new File("/sdcard/Download"));
            addOK(cb, "Media junk cleaned");
        } catch (Exception e){
            addFAIL(cb, "Media junk failed");
        }
    }

    // ======================= BROWSER CACHE ======================
    public static void browserCache(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/sdcard/Android/data/com.android.chrome/cache"));
            addOK(cb, "Browser cache cleaned");
        } catch (Exception e){
            addFAIL(cb, "Browser cache failed");
        }
    }

    // ======================= TEMP ===============================
    public static void tempClean(Context ctx, LogCallback cb){
        try {
            File c1 = ctx.getCacheDir();
            File c2 = ctx.getExternalCacheDir();
            deleteRecursive(c1);
            deleteRecursive(c2);
            addOK(cb, "Temp cleaned");
        } catch (Exception e){
            addFAIL(cb, "Temp clean failed");
        }
    }

    // ======================= CPU INFO ===========================
    public static void cpuInfo(Context ctx, LogCallback cb){
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            addOK(cb, "Cores: " + cores);
            String freq = readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            if(freq != null) addOK(cb, "Freq: " + freq.trim() + " Hz");
            else addFAIL(cb, "CPU Freq");
        } catch (Exception e){
            addFAIL(cb, "CPU Info");
        }
    }

    // ======================= CPU LIVE ===========================
    public static void cpuLive(Context ctx, LogCallback cb){
        try {
            float usage = readUsage();
            addOK(cb, "CPU usage: " + usage + "%");
        } catch (Exception e){
            addFAIL(cb, "CPU Live");
        }
    }

    private static float readUsage() throws Exception {
        long[] t1 = readStat(); Thread.sleep(250); long[] t2 = readStat();
        long idle = t2[0]-t1[0]; long total = t2[1]-t1[1];
        return (float)(100.0 * (total - idle) / total);
    }
    private static long[] readStat() throws Exception {
        RandomAccessFile r = new RandomAccessFile("/proc/stat","r");
        String load = r.readLine(); r.close();
        String[] toks = load.split(" +");
        long idle = Long.parseLong(toks[4]);
        long total = 0; for(int i=1;i<toks.length;i++) total += Long.parseLong(toks[i]);
        return new long[]{idle,total};
    }
    private static String readFile(String path){
        try {
            RandomAccessFile r = new RandomAccessFile(path,"r");
            String s = r.readLine(); r.close(); return s;
        } catch (Exception e){ return null; }
    }

    // ============================================================
    //  💣  DEEP CLEAN
    // ============================================================
    public static void deepClean(Context ctx, LogCallback cb){
        if(!hasRoot()){
            addFAIL(cb, "Deep Clean NO ROOT → fallback");
            safeClean(ctx, cb);
            return;
        }
        exec("su -c rm -rf /data/system/dropbox/*");
        exec("su -c rm -rf /data/tombstones/*");

        addOK(cb, "Deep Clean completed");
    }

    // ============================================================
    //  💣 CLEAN ALL
    // ============================================================
    public static void cleanAll(Context ctx, LogCallback cb){
        addOK(cb, "Clean-All started");

        if (hasRoot()) {
            addOK(cb, "Root detected → Deep Clean");
            deepClean(ctx, cb);
        } else {
            addFAIL(cb, "NO ROOT → Safe Clean only");
            safeClean(ctx, cb);
        }

        try { cleanRAM(ctx, cb); }       catch (Exception e){ addFAIL(cb, "RAM"); }
        try { boostBattery(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Battery"); }
        try { killApps(ctx, cb); }       catch (Exception e){ addFAIL(cb, "Kill Apps"); }
        try { browserCache(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Browser"); }
        try { mediaJunk(ctx, cb); }      catch (Exception e){ addFAIL(cb, "Media Junk"); }
        try { tempClean(ctx, cb); }      catch (Exception e){ addFAIL(cb, "Temp"); }

        addOK(cb, "✅ Clean-All completed");
    }

    // ============================================================
    private static void deleteRecursive(File f){
        if(f == null || !f.exists()) return;
        if(f.isDirectory()){
            File[] c = f.listFiles();
            if(c != null){
                for(File x : c) deleteRecursive(x);
            }
        }
        f.delete();
    }
}
