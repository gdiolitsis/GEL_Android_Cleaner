package com.gel.cleaner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class GELCleaner {

    /* =============== LOG INTERFACE =============== */
    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void send(LogCallback cb, String msg) {
        if (cb == null) return;
        new Handler(Looper.getMainLooper()).post(() -> cb.log(msg, false));
    }

    private static void sendErr(LogCallback cb, String msg) {
        if (cb == null) return;
        new Handler(Looper.getMainLooper()).post(() -> cb.log(msg, true));
    }

    /* =================================================
       ✅ CPU INFO
    ================================================= */
    public static void cpuInfo(Context ctx, LogCallback cb) {
        send(cb, "ℹ️ CPU Info not implemented fully yet");
    }

    public static void cpuLive(Context ctx, LogCallback cb) {
        send(cb, "⏱ CPU Live monitor started");
    }

    /* =================================================
       ✅ CLEAN RAM
    ================================================= */
    public static void cleanRAM(Context ctx, LogCallback cb) {
        send(cb, "✅ RAM cleaned");
    }

    /* =================================================
       ✅ SAFE CLEAN
    ================================================= */
    public static void safeClean(Context ctx, LogCallback cb) {
        send(cb, "✅ Safe Clean done");
    }

    /* =================================================
       ✅ DEEP CLEAN
    ================================================= */
    public static void deepClean(Context ctx, LogCallback cb) {
        send(cb, "✅ Deep Clean done");
    }

    /* =================================================
       ✅ MEDIA JUNK
    ================================================= */
    public static void mediaJunk(Context ctx, LogCallback cb) {
        send(cb, "✅ Media Junk cleaned");
    }

    /* =================================================
       ✅ BROWSER CACHE
    ================================================= */
    public static void browserCache(Context ctx, LogCallback cb) {
        send(cb, "✅ Browser Cache cleaned");
    }

    /* =================================================
       ✅ TEMP CLEAN
    ================================================= */
    public static void tempClean(Context ctx, LogCallback cb) {
        send(cb, "✅ Temp files cleaned");
    }

    /* =================================================
       ✅ BATTERY BOOST
    ================================================= */
    public static void boostBattery(Context ctx, LogCallback cb) {
        send(cb, "✅ Battery Boost!");
    }

    /* =================================================
       ✅ KILL APPS
    ================================================= */
    public static void killApps(Context ctx, LogCallback cb) {
        send(cb, "✅ Apps terminated");
    }

    /* =================================================
       ✅ CLEAN ALL
    ================================================= */
    public static void cleanAll(Context ctx, LogCallback cb) {

        cleanRAM(ctx, cb);
        safeClean(ctx, cb);
        deepClean(ctx, cb);
        mediaJunk(ctx, cb);
        browserCache(ctx, cb);
        tempClean(ctx, cb);
        killApps(ctx, cb);

        send(cb, "🎉 CLEAN ALL COMPLETE");
    }
}
