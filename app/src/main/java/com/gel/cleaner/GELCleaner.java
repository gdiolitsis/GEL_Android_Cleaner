package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.PowerManager;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

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

    // ======================= DELETE RECURSIVE ===================
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

    // ======================= SAFE CLEAN =========================
    public static void safeClean(Context ctx, LogCallback cb){

        try {
            deleteRecursive(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);

            // temp common folders
            deleteRecursive(new File("/data/local/tmp"));
            deleteRecursive(new File("/data/tmp"));

            addOK(cb, "Safe Clean completed ✅");

        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    // ======================= CLEAN RAM ==========================
    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.clearApplicationUserData();
            }
            addOK(cb, "RAM Cleaned ✅");

        } catch(Exception e){
            addFAIL(cb, "RAM Clean failed");
        }
    }

    // ======================= BATTERY BOOST ======================
    public static void boostBattery(Context ctx, LogCallback cb){
        try {
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                addOK(cb, "Battery Optimizer → OK ✅");
            } else {
                addFAIL(cb, "Battery Boost unavailable");
            }
        } catch (Exception e){
            addFAIL(cb, "Battery Boost failed");
        }
    }

    // ======================= KILL APPS ==========================
    public static void killApps(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null){
                    for (ActivityManager.RunningAppProcessInfo p : procs){
                        am.killBackgroundProcesses(p.processName);
                    }
                }
            }
            addOK(cb, "Killed background apps ✅");

        } catch(Exception e){
            addFAIL(cb, "Kill Apps failed");
        }
    }

    // ======================= MEDIA JUNK =========================
    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/storage/emulated/0/DCIM/.thumbnails"));
            deleteRecursive(new File("/sdcard/DCIM/.thumbnails"));
            addOK(cb, "Media junk cleaned ✅");

        } catch(Exception e){
            addFAIL(cb, "Media Junk failed");
        }
    }

    // ======================= BROWSER CACHE ======================
    public static void browserCache(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/data/data/com.android.chrome/cache"));
            deleteRecursive(new File("/data/data/com.android.chrome/app_chrome"));
            deleteRecursive(new File("/data/data/com.android.chrome/files"));
            addOK(cb, "Browser cache cleaned ✅");

        } catch(Exception e){
            addFAIL(cb, "Browser cache failed");
        }
    }

    // ======================= TEMP ===============================
    public static void tempClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/cache"));
            deleteRecursive(new File("/data/cache"));
            deleteRecursive(new File("/mnt/sdcard/Android/data/com.android.browser/cache"));
            addOK(cb, "Temp cleaned ✅");

        } catch(Exception e){
            addFAIL(cb, "Temp failed");
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
        addFAIL(cb, "Deep Clean → partial (falling to Safe Clean)");
        safeClean(ctx, cb);

        if (hasRoot()){
            deleteRecursive(new File("/data/dalvik-cache"));
            deleteRecursive(new File("/cache/dalvik-cache"));
            addOK(cb, "Dalvik cleaned (root) ✅");
        }
    }

    // ======================= CLEAN ALL ==========================
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
}
