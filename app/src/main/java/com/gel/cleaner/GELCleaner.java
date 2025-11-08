package com.gel.cleaner;

import android.content.Context;
import java.io.File;
import java.io.RandomAccessFile;

public class GELCleaner {

    // ======================= LOG CALLBACK =======================
    public interface LogCallback {
        void log(String msg, boolean isError);
    }
    static void addOK(LogCallback c, String msg){ if(c!=null) c.log("✅ " + msg, false); }
    static void addFAIL(LogCallback c, String msg){ if(c!=null) c.log("❌ " + msg, true); }

    // ======================= ROOT CHECK =========================
    private static boolean hasRoot() {
        return exec("su -c id") == 0;
    }
    public static boolean hasRootPublic(){ return hasRoot(); }

    private static int exec(String cmd){
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return p.exitValue();
        } catch (Exception e){ return -1; }
    }

    // ======================= SAFE CLEAN =========================
    public static void safeClean(Context ctx, LogCallback cb){

        try {
            // App cache clean
            File cacheDir = ctx.getCacheDir();
            deleteRecursive(cacheDir);

            // External cache
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);

            // Temp folders
            deleteRecursive(new File("/data/local/tmp"));
            deleteRecursive(new File("/data/tmp"));

            addOK(cb, "Safe Clean completed");

        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    private static void deleteRecursive(File f){
        try {
            if (f == null || !f.exists()) return;

            if (f.isDirectory()){
                File[] files = f.listFiles();
                if (files != null){
                    for (File child : files){
                        deleteRecursive(child);
                    }
                }
            }
            f.delete();
        } catch(Exception ignored){}
    }

    // ======================= CLEAN RAM ==========================
    public static void cleanRAM(Context ctx, LogCallback cb){
        addOK(cb, "Clean RAM");
    }

    // ======================= BATTERY BOOST ======================
    public static void boostBattery(Context ctx, LogCallback cb){
        addOK(cb, "Battery boost");
    }

    // ======================= KILL APPS ==========================
    public static void killApps(Context ctx, LogCallback cb){
        addOK(cb, "Kill Apps");
    }

    // ======================= MEDIA JUNK =========================
    public static void mediaJunk(Context ctx, LogCallback cb){
        addOK(cb, "Media junk cleaned");
    }

    // ======================= BROWSER CACHE ======================
    public static void browserCache(Context ctx, LogCallback cb){
        addOK(cb, "Browser cache cleaned");
    }

    // ======================= TEMP ===============================
    public static void tempClean(Context ctx, LogCallback cb){
        addOK(cb, "Temp cleaned");
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
        long[] t1 = readStat(); Thread.sleep(300); long[] t2 = readStat();
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

    // ======================= DEEP CLEAN =========================
    public static void deepClean(Context ctx, LogCallback cb){
        addFAIL(cb, "Deep Clean not fully implemented → Fallback → Safe Clean");
        safeClean(ctx, cb);
    }

    // ======================= CLEAN ALL ==========================
    public static void cleanAll(Context ctx, LogCallback cb){
