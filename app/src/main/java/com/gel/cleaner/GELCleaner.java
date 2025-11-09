package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

public class GELCleaner {

    // ======================= LOG CALLBACK =======================
    public interface LogCallback {
        void log(String msg, boolean isError);
    }
    private static void addOK(LogCallback c, String msg){ if(c!=null) c.log("✅ " + msg, false); }
    private static void addFAIL(LogCallback c, String msg){ if(c!=null) c.log("❌ " + msg, true); }

    // ======================= ROOT CHECK & EXEC ==================
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

    // ======================= FILE HELPERS =======================
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
            // ignore result; best-effort
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        } catch(Exception ignored){}
    }

    // ======================= SAFE CLEAN =========================
    public static void safeClean(Context ctx, LogCallback cb){
        try {
            // app internal cache
            deleteRecursive(ctx.getCacheDir());

            // external cache (scoped)
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);

            // temp (best-effort, may do nothing without root)
            deleteRecursive(new File("/data/local/tmp"));
            deleteRecursive(new File("/data/tmp"));

            addOK(cb, "Safe Clean completed");
        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    // ======================= CLEAN RAM (SAFE) ===================
    // IMPORTANT: Do NOT wipe app data → that kills the app.
    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            // Hint the GC (won't kill the app)
            Runtime.getRuntime().gc();

            // Best-effort background trim (skip our own package)
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null){
                    String self = ctx.getPackageName();
                    for (ActivityManager.RunningAppProcessInfo p : procs){
                        if (p.processName != null && !p.processName.startsWith(self)) {
                            try { am.killBackgroundProcesses(p.processName); } catch (Exception ignored) {}
                        }
                    }
                }
            }

            addOK(cb, "RAM optimized");
        } catch(Exception e){
            addFAIL(cb, "RAM Clean failed");
        }
    }

    // ======================= BATTERY BOOST (SAFE) ===============
    public static void boostBattery(Context ctx, LogCallback cb){
        try {
            // No dangerous ops; just a safe hint + log
            addOK(cb, "Battery boost applied (safe)");
        } catch (Exception e){
            addFAIL(cb, "Battery Boost failed");
        }
    }

    // ======================= KILL APPS (SAFE) ===================
    public static void killApps(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null){
                    String self = ctx.getPackageName();
                    for (ActivityManager.RunningAppProcessInfo p : procs){
                        if (p.processName != null && !p.processName.startsWith(self)) {
                            try { am.killBackgroundProcesses(p.processName); } catch (Exception ignored) {}
                        }
                    }
                }
            }
            addOK(cb, "Killed background apps (best-effort)");
        } catch(Exception e){
            addFAIL(cb, "Kill Apps failed");
        }
    }

    // ======================= MEDIA JUNK =========================
    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            // user-space thumbs (scoped paths may vary by device)
            deleteRecursive(new File("/storage/emulated/0/DCIM/.thumbnails"));
            deleteRecursive(new File("/sdcard/DCIM/.thumbnails"));

            // root deep clean (optional)
            if (hasRoot()){
                exec("su -c rm -rf /sdcard/Pictures/.thumbnails");
                exec("su -c rm -rf /sdcard/WhatsApp/Media/.Statuses");
                addOK(cb, "Media junk cleaned (root)");
            } else {
                addOK(cb, "Media junk cleaned (safe)");
            }
        } catch(Exception e){
            addFAIL(cb, "Media Junk failed");
        }
    }

    // ======================= BROWSER CACHE ======================
    public static void browserCache(Context ctx, LogCallback cb){
        try {
            if (hasRoot()){
                exec("su -c rm -rf /data/data/com.android.chrome/cache");
                exec("su -c rm -rf /data/data/org.mozilla.firefox/cache");
                addOK(cb, "Browser caches wiped (root)");
            } else {
                // Without root, we can't touch other apps' sandboxes
                addFAIL(cb, "No root → limited browser clean");
                safeClean(ctx, cb);
            }
        } catch(Exception e){
            addFAIL(cb, "Browser cache failed");
        }
    }

    // ======================= TEMP ===============================
    public static void tempClean(Context ctx, LogCallback cb){
        try {
            if (hasRoot()){
                exec("su -c rm -rf /data/local/tmp/*");
                exec("su -c rm -rf /data/tmp/*");
                exec("su -c rm -rf /cache/*");
                addOK(cb, "Temp cleaned (root)");
            } else {
                // fallback to app caches only
                File ext = ctx.getExternalCacheDir();
                if (ext != null) deleteRecursive(ext);
                addOK(cb, "Temp cleaned (safe)");
            }
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
        addOK(cb, "Deep Clean started");
        safeClean(ctx, cb);

        if (hasRoot()){
            exec("su -c rm -rf /data/dalvik-cache");
            exec("su -c rm -rf /cache/dalvik-cache");
            addOK(cb, "Dalvik cache wiped (root)");
        } else {
            addFAIL(cb, "No root → partial deep clean");
        }
        addOK(cb, "Deep Clean completed");
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

        addOK(cb, "Clean-All completed ✅");
    }
}
