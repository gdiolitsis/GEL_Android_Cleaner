package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.os.SystemClock;
import android.provider.Settings;

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

    // ======================= EXEC HELPERS =======================
    private static int exec(String cmd){
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            return p.exitValue();
        } catch (Exception e){ return -1; }
    }

    private static boolean hasRoot() {
        return exec("su -c id") == 0;
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
            // ignore result
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        } catch(Exception ignored){}
    }

    // ======================= SAFE CLEAN =========================
    // Καθαρίζει cache της εφαρμογής μας + external cache + κοινά tmp
    public static void safeClean(Context ctx, LogCallback cb){
        try {
            deleteRecursive(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);

            // temp κοινά (αν έχουμε πρόσβαση)
            deleteRecursive(new File("/sdcard/Android/data/" + ctx.getPackageName() + "/cache"));

            addOK(cb, "Safe Clean completed");
        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    // ======================= CLEAN RAM ==========================
    // Play-Store safe: προτείνει App Info για «Clear cache / Force stop» στο app μας
    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                // Μικρό GC hint για εμάς
                Debug.startAllocCounting();
                System.gc();
                SystemClock.sleep(150);
                Debug.stopAllocCounting();
            }
            addOK(cb, "RAM hint done • For deeper effect open App Info");
            // Άνοιγμα App Info της εφαρμογής μας
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + ctx.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch(Exception e){
            addFAIL(cb, "RAM Clean failed");
        }
    }

    // ======================= BATTERY BOOST ======================
    // Στέλνει τον χρήστη στο battery optimization screen για exclusion
    public static void boostBattery(Context ctx, LogCallback cb){
        try {
            addOK(cb, "Open Battery Optimization");
            Intent i = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch (Exception e){
            addFAIL(cb, "Battery Boost failed");
        }
    }

    // ======================= KILL APPS ==========================
    // Play-Store safe: best-effort kill background (δεν σκοτώνει third-party αυθαίρετα στα νέα Android)
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
            addOK(cb, "Requested kill of background processes (best-effort)");
        } catch(Exception e){
            addFAIL(cb, "Kill Apps failed");
        }
    }

    // ======================= MEDIA JUNK =========================
    // Καθαρίζει thumbnails (εκεί που έχουμε πρόσβαση) — πλήρης καθαρισμός μέσω SAF UI
    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            addOK(cb, "Open SAF to clean DCIM/.thumbnails");
            StorageHelper.openThumbnailsSAF(ctx);
        } catch(Exception e){
            addFAIL(cb, "Media Junk failed");
        }
    }

    // ======================= BROWSER CACHE ======================
    // Play-Store safe: ανοίγει App Info του Chrome/Browser για manual Clear cache
    public static void browserCache(Context ctx, LogCallback cb){
        try {
            addOK(cb, "Open Chrome App Info to clear cache");
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:com.android.chrome"));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        } catch(Exception e){
            addFAIL(cb, "Browser cache failed");
        }
    }

    // ======================= TEMP ===============================
    // Χρήση SAF για προσωρινά αρχεία σε κοινά μονοπάτια
    public static void tempClean(Context ctx, LogCallback cb){
        try {
            addOK(cb, "Open SAF for temporary folders (Android/data)");
            StorageHelper.openAndroidDataSAF(ctx);
        } catch(Exception e){
            addFAIL(cb, "Temp failed");
        }
    }

    // ======================= CPU INFO ===========================
    public static void cpuInfo(Context ctx, LogCallback cb){
        try {
            int cores = Runtime.getRuntime().availableProcessors();
            addOK(cb, "CPU Cores: " + cores);
            String freq = readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            if(freq != null) addOK(cb, "CPU Freq: " + freq.trim() + " Hz");
            else addFAIL(cb, "CPU Freq read");
            // RAM snapshot
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.getMemoryInfo(mi);
                addOK(cb, "RAM: total=" + mi.totalMem/1048576 + "MB free=" + mi.availMem/1048576 + "MB");
            }
        } catch (Exception e){
            addFAIL(cb, "CPU/RAM Info failed");
        }
    }

    // ======================= CPU LIVE (10s) =====================
    public static void cpuLive(Context ctx, LogCallback cb){
        new Thread(() -> {
            try {
                final int seconds = 10;
                for (int i=1;i<=seconds;i++){
                    float cpu = readUsage();
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
                    long free = 0, total = 0;
                    if (am != null) {
                        am.getMemoryInfo(mi);
                        free = mi.availMem/1048576;
                        total = mi.totalMem/1048576;
                    }
                    addOK(cb, "LIVE " + i + "s → CPU " + String.format("%.1f", cpu) + "% | RAM " + free + "/" + total + "MB");
                    SystemClock.sleep(1000);
                }
                addOK(cb, "CPU+RAM Live finished (10s)");
            } catch (Exception e){
                addFAIL(cb, "CPU+RAM Live failed");
            }
        }).start();
    }

    private static float readUsage() throws Exception {
        long[] t1 = readStat(); SystemClock.sleep(300); long[] t2 = readStat();
        long idle = t2[0]-t1[0]; long total = t2[1]-t1[1];
        return total <= 0 ? 0f : (float)(100.0 * (total - idle) / total);
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
        addOK(cb, "Deep Clean (safe mode) → opens key settings/SAF");
        // 1) Safe Clean (δικό μας)
        safeClean(ctx, cb);
        // 2) Cache & App Info hubs για manual clear από χρήστη (Play-Store-safe)
        Intent storage = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        storage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(storage);

        addOK(cb, "Tip: Use App Cache screen to clear specific apps");
    }

    // ======================= CLEAN ALL ==========================
    public static void cleanAll(Context ctx, LogCallback cb){
        addOK(cb, "Clean-All started");

        if (hasRoot()) {
            addOK(cb, "Root detected → (root deep not used on Play Store)");
        } else {
            addOK(cb, "No root → Play-Store safe flows");
        }

        try { safeClean(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Safe Clean"); }
        try { tempClean(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Temp"); }
        try { mediaJunk(ctx, cb); }   catch (Exception e){ addFAIL(cb, "Media Junk"); }
        try { browserCache(ctx, cb);} catch (Exception e){ addFAIL(cb, "Browser"); }
        try { cleanRAM(ctx, cb); }    catch (Exception e){ addFAIL(cb, "RAM"); }
        try { killApps(ctx, cb); }    catch (Exception e){ addFAIL(cb, "Kill Apps"); }
        try { boostBattery(ctx, cb);} catch (Exception e){ addFAIL(cb, "Battery"); }

        addOK(cb, "✅ Clean-All completed");
    }
}
