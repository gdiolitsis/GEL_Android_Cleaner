package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/*
 * GEL Cleaner — Native Java Core
 * Mode: Root-capable • Aggressive • EN/GR ready
 *
 * NOTE:
 * - Με root κάνουμε force-stop και καθαρίζουμε system caches.
 * - Χωρίς root γίνεται ασφαλής καθαρισμός app/external cache + media junk.
 */

public class GELCleaner {

    // ======================= LOG CALLBACK =======================
    public interface LogCallback {
        void log(String msg, boolean isError);
    }
    static void addOK(LogCallback c, String msg){ if(c!=null) c.log("✅ " + msg, false); }
    static void addINFO(LogCallback c, String msg){ if(c!=null) c.log("• " + msg, false); }
    static void addFAIL(LogCallback c, String msg){ if(c!=null) c.log("❌ " + msg, true); }

    // ======================= ROOT CHECK =========================
    private static boolean hasRoot() {
        int rc = exec("su","-c","id");
        return rc == 0;
    }
    public static boolean hasRootPublic(){ return hasRoot(); }

    // ======================= EXEC HELPERS =======================
    private static int exec(String... cmd){
        try {
            Process p = new ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start();
            p.waitFor();
            return p.exitValue();
        } catch (Exception e){ return -1; }
    }
    private static int su(String shellCmd){
        return exec("su","-c", shellCmd);
    }
    private static String suOut(String shellCmd){
        try {
            Process p = new ProcessBuilder("su","-c", shellCmd)
                    .redirectErrorStream(true)
                    .start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
            p.waitFor();
            return sb.toString();
        } catch (Exception e){ return null; }
    }

    // ======================= FILE HELPERS =======================
    private static void deleteRecursive(File f){
        try {
            if (f == null || !f.exists()) return;
            // safety: never allow delete of root or top-level system dirs
            String path = f.getAbsolutePath();
            if ("/".equals(path) || "/system".equals(path) || "/data".equals(path)) return;

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

    private static void deletePath(String path){
        if (path == null) return;
        deleteRecursive(new File(path));
    }

    // ======================= SAFE CLEAN =========================
    public static void safeClean(Context ctx, LogCallback cb){
        try {
            // App internal cache
            deleteRecursive(ctx.getCacheDir());

            // External cache
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);

            // Generic temp
            deletePath("/data/local/tmp");
            deletePath("/data/tmp");

            // /Android/data/*/cache (best-effort, no root = only readable scope on modern Android)
            File storage = new File("/storage");
            if (storage.exists()){
                // Scan visible mount points
                File[] vols = storage.listFiles();
                if (vols != null){
                    for (File vol : vols){
                        File base = new File(vol, "Android/data");
                        File[] pkgs = base.listFiles();
                        if (pkgs != null){
                            for (File pkg : pkgs){
                                deleteRecursive(new File(pkg, "cache"));
                            }
                        }
                    }
                }
            }

            addOK(cb, "Safe Clean completed");
        } catch (Exception e){
            addFAIL(cb, "Safe Clean failed");
        }
    }

    // ======================= DEEP CLEAN (ROOT) ==================
    public static void deepClean(Context ctx, LogCallback cb){
        if (!hasRoot()){
            addFAIL(cb, "Deep Clean: no root → fallback Safe Clean");
            safeClean(ctx, cb);
            return;
        }
        addINFO(cb, "Deep Clean (root) started");

        // Clear dalvik/art cache (only cache files, not critical dirs)
        su("rm -rf /data/dalvik-cache/*");
        su("rm -rf /cache/*");

        // Truncate logcat
        su("logcat -c");

        // System temp
        su("rm -rf /data/system/dropbox/*");
        su("rm -rf /data/system/usagestats/*");
        su("rm -rf /data/anr/*");

        // Chrome/Browser caches
        List<String> browserPkgs = Arrays.asList(
                "com.android.chrome",
                "com.chrome.beta",
                "com.brave.browser",
                "org.mozilla.firefox",
                "org.mozilla.fenix",           // Firefox Nightly
                "com.opera.browser",
                "com.opera.mini.native",
                "com.sec.android.app.sbrowser" // Samsung
        );
        for (String pkg : browserPkgs){
            su("rm -rf /data/data/" + pkg + "/cache/*");
            su("rm -rf /data/data/" + pkg + "/app_webview/*");
        }

        // Media junk
        su("rm -rf /sdcard/DCIM/.thumbnails/*");
        su("rm -rf /sdcard/Pictures/.thumbnails/*");
        su("rm -rf /sdcard/Download/.tmp/*");
        su("rm -rf /sdcard/Movies/.tmp/*");
        su("rm -rf /sdcard/Android/media/*/.tmp/*");

        addOK(cb, "Deep Clean completed");
    }

    // ======================= CLEAN RAM ==========================
    public static void cleanRAM(Context ctx, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null){
                // kill background processes except ourselves
                String self = ctx.getPackageName();
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null){
                    for (ActivityManager.RunningAppProcessInfo p : procs){
                        if (p.pkgList == null) continue;
                        for (String pkg : p.pkgList){
                            if (!pkg.equals(self)){
                                try { am.killBackgroundProcesses(pkg); } catch (Exception ignored){}
                            }
                        }
                    }
                }
            }
            // with root, reclaim more aggressively
            if (hasRoot()){
                su("echo 3 > /proc/sys/vm/drop_caches"); // pagecache,dentries,inodes
            }
            addOK(cb, "Clean RAM done");
        } catch (Exception e){
            addFAIL(cb, "Clean RAM failed");
        }
    }

    // ======================= BATTERY BOOST ======================
    public static void boostBattery(Context ctx, LogCallback cb){
        try {
            // Best-effort: stop common heavy services (root)
            if (hasRoot()){
                List<String> drainers = Arrays.asList(
                        "com.facebook.katana",
                        "com.facebook.orca",
                        "com.instagram.android",
                        "com.netflix.mediaclient",
                        "com.snapchat.android",
                        "com.spotify.music",
                        "com.google.android.youtube"
                );
                for (String p : drainers){
                    su("am force-stop " + p);
                }
            }
            addOK(cb, "Battery boost done");
        } catch (Exception e){
            addFAIL(cb, "Battery boost failed");
        }
    }

    // ======================= KILL APPS ==========================
    public static void killApps(Context ctx, LogCallback cb){
        try {
            String self = ctx.getPackageName();
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            HashSet<String> killed = new HashSet<>();
            if (am != null){
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs != null){
                    for (ActivityManager.RunningAppProcessInfo p : procs){
                        if (p.pkgList == null) continue;
                        for (String pkg : p.pkgList){
                            if (!pkg.equals(self) && !killed.contains(pkg)){
                                try { am.killBackgroundProcesses(pkg); } catch (Exception ignored){}
                                killed.add(pkg);
                            }
                        }
                    }
                }
            }
            if (hasRoot()){
                for (String pkg : killed){
                    su("am force-stop " + pkg);
                }
            }
            addOK(cb, "Kill Apps done (" + killed.size() + ")");
        } catch (Exception e){
            addFAIL(cb, "Kill Apps failed");
        }
    }

    // ======================= MEDIA JUNK =========================
    public static void mediaJunk(Context ctx, LogCallback cb){
        try {
            List<String> targets = new ArrayList<>(Arrays.asList(
                    "/sdcard/DCIM/.thumbnails",
                    "/sdcard/Pictures/.thumbnails",
                    "/sdcard/Download/.tmp",
                    "/sdcard/Movies/.tmp",
                    "/sdcard/Android/media/.thumbnails"
            ));
            for (String t : targets) deletePath(t);
            addOK(cb, "Media junk cleaned");
        } catch (Exception e){
            addFAIL(cb, "Media junk failed");
        }
    }

    // ======================= BROWSER CACHE ======================
    public static void browserCache(Context ctx, LogCallback cb){
        try {
            List<String> browserPkgs = Arrays.asList(
                    "com.android.chrome",
                    "org.mozilla.firefox",
                    "com.opera.browser",
                    "com.sec.android.app.sbrowser",
                    "com.brave.browser"
            );
            for (String pkg : browserPkgs){
                // best-effort non-root (external webview cache is private; needs root)
                deletePath("/sdcard/Android/data/" + pkg + "/cache");
                if (hasRoot()){
                    su("rm -rf /data/data/" + pkg + "/cache/*");
                    su("rm -rf /data/data/" + pkg + "/app_webview/*");
                }
            }
            addOK(cb, "Browser cache cleaned");
        } catch (Exception e){
            addFAIL(cb, "Browser cache failed");
        }
    }

    // ======================= TEMP ===============================
    public static void tempClean(Context ctx, LogCallback cb){
        try {
            deletePath("/sdcard/Download/.temp");
            deletePath("/sdcard/Temp");
            deletePath("/sdcard/tmp");
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
        addOK(cb, "Clean-All completed");
    }
}
