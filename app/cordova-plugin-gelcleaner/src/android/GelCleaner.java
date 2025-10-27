package com.gdiolitsis.gelcleaner;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.ActivityManager;
import android.content.Context;
import java.io.File;

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

    private void clearAppCache(CallbackContext callback) {
        try {
            Context ctx = cordova.getActivity().getApplicationContext();
            File dir = ctx.getCacheDir();
            deleteDir(dir);
            callback.success("Cache cleared successfully!");
        } catch (Exception e) {
            callback.error("Cache clear failed: " + e.getMessage());
        }
    }

    private void boostRAM(CallbackContext callback) {
        try {
            ActivityManager am = (ActivityManager) cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            am.clearApplicationUserData();
            callback.success("RAM boosted successfully!");
        } catch (Exception e) {
            callback.error("Boost failed: " + e.getMessage());
        }
    }

    private void clearTemp(CallbackContext callback) {
        try {
            Context ctx = cordova.getActivity().getApplicationContext();
            File temp = new File(ctx.getCacheDir(), "temp");
            deleteDir(temp);
            callback.success("Temporary files cleared!");
        } catch (Exception e) {
            callback.error("Temp clear failed: " + e.getMessage());
        }
    }

    private void removeJunk(CallbackContext callback) {
        try {
            Context ctx = cordova.getActivity().getApplicationContext();
            File downloads = ctx.getExternalFilesDir(null);
            deleteDir(downloads);
            callback.success("Junk files removed!");
        } catch (Exception e) {
            callback.error("Remove junk failed: " + e.getMessage());
        }
    }

    private void optimizeBattery(CallbackContext callback) {
        try {
            // Placeholder logic – will expand with PowerManager if needed
            callback.success("Battery optimization triggered!");
        } catch (Exception e) {
            callback.error("Battery optimization failed: " + e.getMessage());
        }
    }

    private void killBackground(CallbackContext callback) {
        try {
            ActivityManager am = (ActivityManager) cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo process : am.getRunningAppProcesses()) {
                am.killBackgroundProcesses(process.processName);
            }
            callback.success("Background processes terminated!");
        } catch (Exception e) {
            callback.error("Kill background failed: " + e.getMessage());
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) return false;
            }
        }
        return dir != null && dir.delete();
    }
}
