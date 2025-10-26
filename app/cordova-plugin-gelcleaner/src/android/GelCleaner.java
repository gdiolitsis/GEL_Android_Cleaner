package com.gel.cleaner;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.ActivityManager;
import android.content.Context;
import java.io.File;

public class GelCleaner extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext cb) throws JSONException {
        final Context ctx = this.cordova.getContext();
        final ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        cordova.getThreadPool().execute(() -> {
            try {
                switch (action) {
                    case "cleanCache":
                        deleteDir(ctx.getCacheDir());
                        File ext = ctx.getExternalCacheDir();
                        if (ext != null) deleteDir(ext);
                        cb.success("cache_ok");
                        break;

                    case "clearTemp":
                        File tmp = ctx.getExternalCacheDir();
                        if (tmp != null) deleteDir(tmp);
                        cb.success("temp_ok");
                        break;

                    case "boostRAM":
                        if (am != null && am.getRunningAppProcesses() != null) {
                            for (ActivityManager.RunningAppProcessInfo p : am.getRunningAppProcesses()) {
                                try { am.killBackgroundProcesses(p.processName); } catch (Exception ignored) {}
                            }
                        }
                        cb.success("ram_ok");
                        break;

                    default:
                        cb.error("unknown_action");
                }
            } catch (Exception e) {
                cb.error(e.getMessage());
            }
        });
        return true;
    }

    private static boolean deleteDir(File dir) {
        if (dir == null) return false;
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File c : children) { deleteDir(c); }
            }
        }
        return dir.delete();
    }
}
