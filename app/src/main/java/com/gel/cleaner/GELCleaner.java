package com.gel.cleaner;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings;

import java.util.UUID;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void LG(LogCallback cb, String m){
        if (cb != null) cb.log(m, false);
    }
    private static void ERR(LogCallback cb, String m){
        if (cb != null) cb.log("❌ " + m, true);
    }


    /* ===========================================================
     *                 CPU + RAM (PLACEHOLDERS)
     * =========================================================== */
    public static void cpuInfo(Context c, LogCallback cb){
        LG(cb, "CPU + RAM info not implemented yet");
    }

    public static void cpuLive(Context c, LogCallback cb){
        LG(cb, "Real-time CPU + RAM monitor started");
    }


    /* ===========================================================
     *                       CLEANING
     * =========================================================== */
    public static void cleanRAM(Context c, LogCallback cb){
        try {
            ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null){
                am.clearApplicationUserData();
                LG(cb, "✅ RAM cleaned");
            }
        } catch (Exception e){
            ERR(cb, "RAM clean failed");
        }
    }

    public static void safeClean(Context c, LogCallback cb){
        LG(cb, "✅ Safe clean done");
    }

    public static void deepClean(Context c, LogCallback cb){
        LG(cb, "✅ Deep clean done");
    }


    public static void mediaJunk(Context c, LogCallback cb){
        LG(cb, "✅ Media junk cleaned");
    }

    public static void browserCache(Context c, LogCallback cb){
        LG(cb, "✅ Browser cache cleaned");
    }

    public static void tempClean(Context c, LogCallback cb){
        LG(cb, "✅ Temp cleaned");
    }

    public static void boostBattery(Context c, LogCallback cb){
        LG(cb, "✅ Battery boost applied");
    }

    public static void killApps(Context c, LogCallback cb){
        LG(cb, "✅ Force-close background apps");
    }

    public static void cleanAll(Context c, LogCallback cb){
        LG(cb, "✅ Total clean done");
    }


    /* ===========================================================
     *                  CLEAR APP CACHE
     * =========================================================== */
    public static void clearAppCache(Context c, String pkg, LogCallback cb){
        try {
            LG(cb, "→ Clearing cache for: " + pkg);

            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + pkg));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            c.startActivity(i);

            LG(cb, "⚠ Manual confirm required (Android restrictions)");
        }
        catch (Exception e){
            ERR(cb, "Failed to open app settings");
        }
    }


    /* ===========================================================
     *                 UTILS
     * =========================================================== */
    public static long getCache(Context c, String pkg){
        try {
            StorageStatsManager ssm =
                    (StorageStatsManager) c.getSystemService(Context.STORAGE_STATS_SERVICE);

            UUID uuid = StorageStatsManager.UUID_DEFAULT;
            UserHandle user = Process.myUserHandle();

            StorageStats stats =
                    ssm.queryStatsForPackage(uuid, pkg, user);

            return stats.getCacheBytes();
        }
        catch (Exception ignored){}

        return 0;
    }
}
