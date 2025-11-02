package com.gel.cleaner;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class GelCleaner extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext)
            throws JSONException {

        switch (action) {
            case "ping":
                callbackContext.success("pong");
                return true;

            case "clearCache":
                clearCache(callbackContext);
                return true;

            default:
                return false;
        }
    }

    private void clearCache(CallbackContext callback) {
        try {
            cordova.getThreadPool().execute(() -> {
                try {
                    cordova.getActivity().getCacheDir().delete();
                    callback.success("Cache cleared!");
                } catch (Exception e) {
                    callback.error("FAILED");
                }
            });
        } catch (Exception e) {
            callback.error("FAILED");
        }
    }
}
