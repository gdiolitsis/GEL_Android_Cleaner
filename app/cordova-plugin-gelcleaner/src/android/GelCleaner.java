package com.gdiolitsis.gelcleaner;

import android.content.Context;
import android.webkit.WebView;
import java.io.File;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class GelCleaner extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = this.cordova.getActivity().getApplicationContext();

        if (action.equals("clearAppCache")) {
            clearAppCache(context);
            callbackContext.success("Cache cleared successfully");
            return true;
        } else if (action.equals("boostRAM")) {
            System.gc();
            callbackContext.success("RAM boost simulated");
            return true;
        } else if (action.equals("clearTemp")) {
            clearTempFiles(context);
            callbackContext.success("Temporary files deleted");
            return true;
        } else if (action.equals("killBackground")) {
            cordova.getActivity().moveTaskToBack(true);
            callbackContext.success("Background processes minimized");
            return true;
        } else {
            callbackContext.error("Invalid action: " + action);
            return false;
        }
    }

    private void clearAppCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearTempFiles(Context context) {
        try {
            File dir = context.getExternalCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
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
