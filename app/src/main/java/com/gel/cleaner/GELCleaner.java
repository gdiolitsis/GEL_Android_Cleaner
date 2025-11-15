package com.gel.cleaner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.format.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GELCleaner {

    public interface LogCallback {
        void log(String msg, boolean isError);
    }

    private static void info(LogCallback cb, String m) { if (cb != null) cb.log("ℹ️ " + m, false); }
    private static void ok  (LogCallback cb, String m) { if (cb != null) cb.log("✅ " + m, false); }
    private static void err (LogCallback cb, String m) { if (cb != null) cb.log("❌ " + m, true ); }

    // --------------------------------------------------------------------
    // CPU LIVE
    // --------------------------------------------------------------------
    public static void cpuLive(Context ctx, LogCallback cb) {

        new Thread(() -> {
            try {
                int i = 1;
                while (i <= 10) {

                    long free = Runtime.getRuntime().freeMemory();
                    long total = Runtime.getRuntime().totalMemory();
                    long used = total - free;

                    String msg = String.format(Locale.US,
                            "Live %02d | App RAM used: %s / %s",
                            i,
                            Formatter.formatShortFileSize(ctx, used),
                            Formatter.formatShortFileSize(ctx, total));

                    info(cb, msg);
                    Thread.sleep(1000);
                    i++;
                }

                ok(cb, "CPU+RAM live finished.");

            } catch (Exception e) {
                err(cb, "cpuLive failed: " + e.getMessage());
            }
        }).start();
    }

    // --------------------------------------------------------------------
    // CLEAN RAM
    // --------------------------------------------------------------------
    public static void cleanRAM(Context ctx, LogCallback cb) {
        try {
            boolean launched = CleanLauncher.openMemoryCleaner(ctx);

            if (launched) {
                ok(cb, "Opening device memory cleaner…");
                return;
            }

            try {
                Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening Internal Storage settings…");
                return;
            } catch (Exception ignored) {}

            try {
                Intent i = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening device info…");
                return;
            } catch (Exception ignored2) {}

            err(cb, "No compatible RAM/cleaner screen found on this device.");

        } catch (Exception e) {
            err(cb, "cleanRAM failed: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // DEEP CLEAN
    // --------------------------------------------------------------------
    public static void deepClean(Context ctx, LogCallback cb) {
        try {
            boolean launched = CleanLauncher.openDeepCleaner(ctx);

            if (launched) {
                ok(cb, "Opening device deep cleaner…");
                return;
            }

            try {
                Intent i = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening system cleaner…");
            } catch (Exception e2) {
                err(cb, "deepClean fallback failed: " + e2.getMessage());
            }

        } catch (Exception e) {
            err(cb, "deepClean failed: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // BROWSER CACHE — Only REAL browsers (universal)
    // --------------------------------------------------------------------
    public static void browserCache(Context ctx, LogCallback cb) {
        try {
            PackageManager pm = ctx.getPackageManager();

            String[] browsers = {
                    "com.android.chrome",
                    "org.mozilla.firefox",
                    "com.opera.browser",
                    "com.microsoft.emmx",
                    "com.brave.browser",
                    "com.vivaldi.browser",
                    "com.duckduckgo.mobile.android",
                    "com.sec.android.app.sbrowser"
            };

            List<String> installed = new ArrayList<>();

            for (String pkg : browsers) {
                try {
                    pm.getPackageInfo(pkg, 0);
                    installed.add(pkg);
                } catch (PackageManager.NameNotFoundException ignored) {}
            }

            if (installed.isEmpty()) {
                err(cb, "No browsers found on your device.");
                return;
            }

            if (installed.size() == 1) {
                String pkg = installed.get(0);
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.setData(Uri.parse("package:" + pkg));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
                ok(cb, "Opening browser storage → Clear Cache.");
                return;
            }

            Intent i = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Select a browser → Storage → Clear Cache.");

        } catch (Exception e) {
            err(cb, "browserCache failed: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // TEMP FILES
    // --------------------------------------------------------------------
    public static void tempFiles(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening Temporary Files section…");
        } catch (Exception e) {
            err(cb, "tempFiles failed: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------------
    // RUNNING APPS
    // --------------------------------------------------------------------
    public static void openRunningApps(Context ctx, LogCallback cb) {
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            ok(cb, "Opening Running Apps…");
        } catch (Exception e) {
            err(cb, "openRunningApps failed: " + e.getMessage());
        }
    }
}
