package com.gdiolitsis.gelcleaner;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import java.io.File;
import java.util.List;

public class GelCleaner extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "clearAppCache":
                clearAppCache(callbackContext);
                return true;
            case "boostRAM":
                boostRAM(callbackContext);
                return true;
            case "clearTemp":
                clearTemp(callbackContext);
                return true;
            case "removeJunk":
                removeJunk(callbackContext);
                return true;
            case "optimizeBattery":
                optimizeBattery(callbackContext);
                return true;
            case "killBackground":
                killBackground(callbackContext);
                return true;
            default:
                return false;
        }
    }

    // 🧠 Καθαρισμός Cache
    private void clearAppCache(CallbackContext callback) {
        try {
            Context ctx = cordova.getActivity().getApplicationContext();
            deleteRecursive(ctx.getCacheDir());
            File ext = ctx.getExternalCacheDir();
            if (ext != null) deleteRecursive(ext);
            callback.success("Cache cleared successfully!");
        } catch (Exception e) {
            callback.error("Cache clear failed: " + e.getMessage());
        }
    }

    // ⚡ Ενίσχυση RAM (χωρίς root)
    private void boostRAM(CallbackContext callback) {
        try {
            ActivityManager am = (ActivityManager) cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
            int count = 0;
            for (ActivityManager.RunningAppProcessInfo proc : procs) {
                if (!proc.processName.equals(cordova.getActivity().getPackageName())) {
                    try {
                        am.killBackgroundProcesses(proc.processName);
                        count++;
                    } catch (Exception ignore) {}
                }
            }
            callback.success("RAM optimization completed (" + count + " tasks affected).");
        } catch (Exception e) {
            callback.error("Boost RAM failed: " + e.getMessage());
        }
    }

    // 🔥 Εκκαθάριση προσωρινών αρχείων
    private void clearTemp(CallbackContext callback) {
        try {
            Context ctx = cordova.getActivity().getApplicationContext();
            File tempDir = new File(ctx.getCacheDir(), "temp");
            deleteRecursive(tempDir);
            callback.success("Temporary files cleared!");
        } catch (Exception e) {
            callback.error("Temp clear failed: " + e.getMessage());
        }
    }

    // 🗑️ Αφαίρεση “junk” (μόνο app scope)
    private void removeJunk(CallbackContext callback) {
        try {
            Context ctx = cordova.getActivity().getApplicationContext();
            File junkDir = ctx.getExternalFilesDir(null);
            if (junkDir != null) deleteRecursive(junkDir);
            callback.success("Junk files removed!");
        } catch (Exception e) {
            callback.error("Remove junk failed: " + e.getMessage());
        }
    }

    // 🔋 Βελτιστοποίηση Μπαταρίας (ανοίγει system ρυθμίσεις)
    private void optimizeBattery(CallbackContext callback) {
        try {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cordova.getActivity().startActivity(intent);
            callback.success("Battery optimization triggered!");
        } catch (Exception e) {
            callback.error("Battery optimization failed: " + e.getMessage());
        }
    }

    // 🚀 Τερματισμός διεργασιών στο επιτρεπτό πλαίσιο
    private void killBackground(CallbackContext callback) {
        try {
            ActivityManager am = (ActivityManager) cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
            int killed = 0;
            for (ActivityManager.RunningAppProcessInfo proc : procs) {
                if (!proc.processName.equals(cordova.getActivity().getPackageName())) {
                    try {
                        am.killBackgroundProcesses(proc.processName);
                        killed++;
                    } catch (SecurityException se) {
                        // Android 12+ περιορισμός
                    }
                }
            }
            if (killed == 0)
                callback.success("Kill background executed (limited by Android policy).");
            else
                callback.success("Background processes terminated: " + killed);
        } catch (Exception e) {
            callback.error("Kill background failed: " + e.getMessage());
        }
    }

    // ♻️ Βοηθητική μέθοδος
    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory != null && fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    deleteRecursive(child);
                }
            }
            fileOrDirectory.delete();
        }
    }
}
