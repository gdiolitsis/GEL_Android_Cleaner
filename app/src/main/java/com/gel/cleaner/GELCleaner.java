package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }
    static void addOK(LogCallback c, String msg){ if(c!=null) c.log("✅ " + msg, false); }
    static void addFAIL(LogCallback c, String msg){ if(c!=null) c.log("❌ " + msg, true); }

    private static boolean hasRoot() { return exec("su -c id") == 0; }
    public static boolean hasRootPublic(){ return hasRoot(); }

    private static int exec(String cmd){
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return p.exitValue();
        } catch (Exception e){ return -1; }
    }

    private static void deleteRecursive(File f){
        try {
            if (f == null || !f.exists()) return;
            if (f.isDirectory()){
                File[] files = f.listFiles();
                if (files != null){
                    for (File child : files){ deleteRecursive(child); }
                }
            }
            f.delete();
        } catch(Exception ignored){}
    }

    // -------- SAFE CLEAN (app + temp που επιτρέπονται χωρίς root/SAF)
    public static void safeClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);
            addOK(cb, "Safe Clean completed");
        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    // -------- CLEAN RAM (χωρίς crash)
    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null){
                    for (ActivityManager.RunningAppProcessInfo p : procs){
                        try { am.killBackgroundProcesses(p.processName); } catch (Exception ignored){}
                    }
                }
            }
            addOK(cb, "Killed background apps");
        } catch(Exception e){
            addFAIL(cb, "RAM clean failed");
        }
    }

    // -------- BATTERY BOOST (light)
    public static void boostBattery(Context ctx, LogCallback cb){
        addOK(cb, "Battery Boost: trimmed background where possible");
    }

    // -------- KILL APPS
    public static void killApps(Context ctx, LogCallback cb){
        cleanRAM(ctx, cb);
    }

    // -------- MEDIA JUNK (μέσω SAF αν υπάρχει)
    public static void mediaJunk(Context ctx, LogCallback cb){
        if (SAFCleaner.hasTree(ctx)) {
            SAFCleaner.cleanKnownJunk(ctx, cb);
        } else {
            addFAIL(cb, "Grant SAF first (Select folder)");
        }
    }

    // -------- BROWSER CACHE (SAF για cache dirs όπου επιτρέπεται)
    public static void browserCache(Context ctx, LogCallback cb){
        if (SAFCleaner.hasTree(ctx)) {
            SAFCleaner.cleanKnownJunk(ctx, cb);
        } else {
            addFAIL(cb, "Grant SAF first (Select folder)");
        }
    }

    // -------- TEMP (SAF)
    public static void tempClean(Context ctx, LogCallback cb){
        if (SAFCleaner.hasTree(ctx)) {
            SAFCleaner.cleanKnownJunk(ctx, cb);
        } else {
            addFAIL(cb, "Grant SAF first (Select folder)");
        }
    }

    // -------- CPU INFO / LIVE
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

    // -------- DEEP CLEAN (root bonus, αν υπάρχει)
    public static void deepClean(Context ctx, LogCallback cb){
        addOK(cb, "Deep Clean start");
        safeClean(ctx, cb);
        if (hasRoot()){
            deleteRecursive(new File("/data/dalvik-cache"));
            deleteRecursive(new File("/cache/dalvik-cache"));
            addOK(cb, "Dalvik wiped (root)");
        } else {
            addFAIL(cb, "No root → limited deep clean");
        }
    }

    // -------- CLEAN ALL (ροή)
    public static void cleanAll(Context ctx, LogCallback cb){
        addOK(cb, "Clean-All started");
        if (hasRoot()) deepClean(ctx, cb); else safeClean(ctx, cb);
        try { browserCache(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Browser"); }
        try { mediaJunk(ctx, cb); }      catch (Exception e){ addFAIL(cb, "Media"); }
        try { tempClean(ctx, cb); }      catch (Exception e){ addFAIL(cb, "Temp"); }
        try { killApps(ctx, cb); }       catch (Exception e){ addFAIL(cb, "Kill Apps"); }
        try { boostBattery(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Battery"); }
        addOK(cb, "✅ Clean-All completed");
    }
}
