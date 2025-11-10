package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.PowerManager;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }
    static void addOK(LogCallback c, String msg){ if(c!=null) c.log("✅ " + msg, false); }
    static void addFAIL(LogCallback c, String msg){ if(c!=null) c.log("❌ " + msg, true); }

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

    private static int exec(String cmd){
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return p.exitValue();
        } catch (Exception e){ return -1; }
    }
    private static boolean hasRoot() { return exec("su -c id") == 0; }

    public static void safeClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);
            deleteRecursive(new File("/data/local/tmp"));
            deleteRecursive(new File("/data/tmp"));
            addOK(cb, "Safe Clean completed ✅");
        } catch (Exception e){ addFAIL(cb, "Safe Clean failed"); }
    }

    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) am.clearApplicationUserData();
            addOK(cb, "RAM Cleaned ✅");
        } catch(Exception e){ addFAIL(cb, "RAM Clean failed"); }
    }

    public static void boostBattery(Context ctx, LogCallback cb){
        try {
            PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
            if (pm != null) addOK(cb, "Battery Optimizer → OK ✅");
            else addFAIL(cb, "Battery Boost unavailable");
        } catch (Exception e){ addFAIL(cb, "Battery Boost failed"); }
    }

    public static void killApps(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
                if (list != null){
                    for (ActivityManager.RunningAppProcessInfo p : list){
                        am.killBackgroundProcesses(p.processName);
                    }
                }
            }
            addOK(cb, "Killed background apps ✅");
        } catch(Exception e){ addFAIL(cb, "Kill Apps failed"); }
    }

    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/storage/emulated/0/DCIM/.thumbnails"));
            deleteRecursive(new File("/sdcard/DCIM/.thumbnails"));
            addOK(cb, "Media junk cleaned ✅");
        } catch(Exception e){ addFAIL(cb, "Media Junk failed"); }
    }

    public static void browserCache(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/data/data/com.android.chrome/cache"));
            deleteRecursive(new File("/data/data/com.android.chrome/app_chrome"));
            deleteRecursive(new File("/data/data/com.android.chrome/files"));
            addOK(cb, "Browser cache cleaned ✅");
        } catch(Exception e){ addFAIL(cb, "Browser cache failed"); }
    }

    public static void tempClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(new File("/cache"));
            deleteRecursive(new File("/data/cache"));
            deleteRecursive(new File("/mnt/sdcard/Android/data/com.android.browser/cache"));
            addOK(cb, "Temp cleaned ✅");
        } catch(Exception e){ addFAIL(cb, "Temp failed"); }
    }

    /* ================= CPU / RAM ================= */

    public static void cpuRamInfo(Context ctx, LogCallback cb){
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            float cpu = readUsage();
            long[] mem = readRAM(ctx);
            addOK(cb, "Cores: " + cores);
            addOK(cb, "CPU: " + cpu + "%");
            addOK(cb, "RAM: " + mem[0] + "GB / " + mem[1] + "GB (" + mem[2] + "% used)");
        } catch (Exception e){ addFAIL(cb, "CPU/RAM Info error"); }
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

    private static long[] readRAM(Context ctx){
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(info);
        long total = info.totalMem;
        long free  = info.availMem;
        long used  = total - free;
        long pct   = used * 100 / total;
        return new long[]{
                used/(1024*1024*1024),
                total/(1024*1024*1024),
                pct
        };
    }

    public static void deepClean(Context ctx, LogCallback cb){
        addFAIL(cb, "Deep Clean → partial (fallback Safe Clean)");
        safeClean(ctx, cb);
        if (hasRoot()){
            deleteRecursive(new File("/data/dalvik-cache"));
            deleteRecursive(new File("/cache/dalvik-cache"));
            addOK(cb, "Dalvik cleaned (root) ✅");
        }
    }

    public static void cleanAll(Context ctx, LogCallback cb){
        addOK(cb, "Clean-All started...");
        if (hasRoot()) {
            addOK(cb, "Root detected → Deep Clean");
            deepClean(ctx, cb);
        } else {
            addFAIL(cb, "NO ROOT → Safe Clean only");
            safeClean(ctx, cb);
        }
        try { cleanRAM(ctx, cb); }       catch (Exception ignored){}
        try { boostBattery(ctx, cb);}    catch (Exception ignored){}
        try { killApps(ctx, cb);}        catch (Exception ignored){}
        try { browserCache(ctx, cb);}    catch (Exception ignored){}
        try { mediaJunk(ctx, cb);}       catch (Exception ignored){}
        try { tempClean(ctx, cb);}       catch (Exception ignored){}

        addOK(cb, "✅ Clean-All completed");
    }
}
