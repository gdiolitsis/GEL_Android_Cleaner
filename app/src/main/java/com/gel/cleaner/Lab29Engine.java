// GDiolitsis Engine Lab (GEL) — Author & Developer
// Lab29Engine — Independent Final Summary Module v1.0
// ------------------------------------------------------------
// ✔ ΜΟΝΟ για LAB 29
// ✔ Δεν αγγίζει κανένα άλλο LAB / Activity
// ✔ Compile-safe, pipeline-safe
// ✔ Μπορεί να κληθεί από ManualTestsActivity με 1 γραμμή
// ------------------------------------------------------------

package com.gel.cleaner;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public final class Lab29Engine {

    private Lab29Engine() {}

    // ============================================================
    // SNAPSHOTS
    // ============================================================

    private static class StorageSnapshot {
        long totalBytes, freeBytes, usedBytes;
        int pctFree;
    }

    private static StorageSnapshot readStorageSnapshot(Context ctx) {
        StorageSnapshot s = new StorageSnapshot();
        try {
            StatFs fs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            s.totalBytes = fs.getBlockCountLong() * fs.getBlockSizeLong();
            s.freeBytes  = fs.getAvailableBlocksLong() * fs.getBlockSizeLong();
            s.usedBytes  = s.totalBytes - s.freeBytes;
            s.pctFree = (s.totalBytes > 0) ? (int)((s.freeBytes * 100L) / s.totalBytes) : 0;
        } catch (Throwable ignored) {}
        return s;
    }

    private static class AppsSnapshot {
        int userApps, systemApps, totalApps;
    }

    private static AppsSnapshot readAppsSnapshot(Context ctx) {
        AppsSnapshot a = new AppsSnapshot();
        try {
            PackageManager pm = ctx.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            if (apps != null) {
                a.totalApps = apps.size();
                for (ApplicationInfo ai : apps) {
                    if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) a.systemApps++;
                    else a.userApps++;
                }
            }
        } catch (Throwable ignored) {}
        return a;
    }

    private static class RamSnapshot {
        long totalBytes, freeBytes;
        int pctFree;
    }

    private static RamSnapshot readRamSnapshot(Context ctx) {
        RamSnapshot r = new RamSnapshot();
        try {
            ActivityManager am =
                    (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(mi);
                r.totalBytes = mi.totalMem;
                r.freeBytes  = mi.availMem;
                r.pctFree = (r.totalBytes > 0)
                        ? (int)((r.freeBytes * 100L) / r.totalBytes)
                        : 0;
            }
        } catch (Throwable ignored) {}
        return r;
    }

    private static class SecuritySnapshot {
        boolean lockSecure;
        boolean adbUsbOn;
        boolean adbWifiOn;
        boolean devOptionsOn;
        boolean rootSuspected;
        boolean testKeys;
        String securityPatch;
    }

    private static SecuritySnapshot readSecuritySnapshot(Context ctx) {
        SecuritySnapshot s = new SecuritySnapshot();

        // lock secure
        try {
            KeyguardManager km =
                    (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    s.lockSecure = km.isDeviceSecure();
                else
                    s.lockSecure = km.isKeyguardSecure();
            }
        } catch (Throwable ignored) {}

        // patch level
        try { s.securityPatch = Build.VERSION.SECURITY_PATCH; }
        catch (Throwable ignored) {}

        // ADB / dev options
        try {
            s.adbUsbOn = Settings.Global.getInt(
                    ctx.getContentResolver(),
                    Settings.Global.ADB_ENABLED, 0) == 1;
        } catch (Throwable ignored) {}

        try {
            s.devOptionsOn = Settings.Global.getInt(
                    ctx.getContentResolver(),
                    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;
        } catch (Throwable ignored) {}

        // ADB Wi-Fi (port property)
        try {
            String adbPort = System.getProperty("service.adb.tcp.port", "");
            if (adbPort != null && !adbPort.trim().isEmpty()) {
                int p = Integer.parseInt(adbPort.trim());
                s.adbWifiOn = (p > 0);
            }
        } catch (Throwable ignored) {}

        // Root suspicion (no root needed)
        s.rootSuspected = detectRootFast(ctx);

        // test-keys check
        try {
            String tags = Build.TAGS;
            s.testKeys = (tags != null && tags.contains("test-keys"));
        } catch (Throwable ignored) {}

        return s;
    }

    private static boolean detectRootFast(Context ctx) {
        try {
            // SU paths
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/app/Superuser.apk", "/system/app/Magisk.apk",
                    "/data/adb/magisk", "/vendor/bin/su"
            };
            for (String p : paths)
                if (new File(p).exists()) return true;

            // Magisk / SuperSU packages
            PackageManager pm = ctx.getPackageManager();
            String[] pkgs = {
                    "com.topjohnwu.magisk",
                    "eu.chainfire.supersu",
                    "com.noshufou.android.su",
                    "com.koushikdutta.superuser"
            };
            for (String pkg : pkgs) {
                try {
                    pm.getPackageInfo(pkg, 0);
                    return true;
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
        return false;
    }

    private static class PrivacySnapshot {
        int userAppsWithLocation;
        int userAppsWithMic;
        int userAppsWithCamera;
        int userAppsWithSms;
        int totalUserAppsChecked;
    }

    private static PrivacySnapshot readPrivacySnapshot(Context ctx) {
        PrivacySnapshot p = new PrivacySnapshot();
        try {
            PackageManager pm = ctx.getPackageManager();
            List<PackageInfo> packs =
                    pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

            if (packs == null) return p;

            for (PackageInfo pi : packs) {
                if (pi == null || pi.applicationInfo == null) continue;
                ApplicationInfo ai = pi.applicationInfo;

                // skip system apps
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

                p.totalUserAppsChecked++;

                String[] req = pi.requestedPermissions;
                int[] flags  = pi.requestedPermissionsFlags;
                if (req == null || flags == null) continue;

                boolean loc = false, mic = false, cam = false, sms = false;

                for (int i = 0; i < req.length; i++) {
                    boolean granted =
                            (flags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0;
                    if (!granted) continue;

                    String perm = req[i];
                    if (perm == null) continue;

                    if (perm.contains("ACCESS_FINE_LOCATION")
                            || perm.contains("ACCESS_COARSE_LOCATION"))
                        loc = true;
                    if (perm.contains("RECORD_AUDIO"))
                        mic = true;
                    if (perm.contains("CAMERA"))
                        cam = true;
                    if (perm.contains("READ_SMS")
                            || perm.contains("RECEIVE_SMS")
                            || perm.contains("SEND_SMS"))
                        sms = true;
                }

                if (loc) p.userAppsWithLocation++;
                if (mic) p.userAppsWithMic++;
                if (cam) p.userAppsWithCamera++;
                if (sms) p.userAppsWithSms++;
            }

        } catch (Throwable ignored) {}
        return p;
    }

    // ============================================================
    // SCORING (UNCHANGED LOGIC)
    // ============================================================

    private static int scoreThermals(float maxT, float avgT) {
        int s = 100;
        if (maxT >= 70) s -= 60;
        else if (maxT >= 60) s -= 40;
        else if (maxT >= 50) s -= 20;

        if (avgT >= 55) s -= 25;
        else if (avgT >= 45) s -= 10;

        return clampScore(s);
    }

    private static int scoreBattery(float battTemp, float battPct, boolean charging) {
        int s = 100;

        if (battTemp >= 55) s -= 55;
        else if (battTemp >= 45) s -= 30;
        else if (battTemp >= 40) s -= 15;

        if (!charging && battPct >= 0) {
            if (battPct < 15) s -= 25;
            else if (battPct < 30) s -= 10;
        }

        return clampScore(s);
    }

    private static int scoreStorage(int pctFree, long totalBytes) {
        int s = 100;
        if (pctFree < 5) s -= 60;
        else if (pctFree < 10) s -= 40;
        else if (pctFree < 15) s -= 25;
        else if (pctFree < 20) s -= 10;

        long gb = totalBytes / (1024L * 1024L * 1024L);
        if (gb > 0 && gb < 32) s -= 10;

        return clampScore(s);
    }

    private static int scoreApps(int userApps, int totalApps) {
        int s = 100;
        if (userApps > 140) s -= 50;
        else if (userApps > 110) s -= 35;
        else if (userApps > 80) s -= 20;
        else if (userApps > 60) s -= 10;

        if (totalApps > 220) s -= 10;
        return clampScore(s);
    }

    private static int scoreRam(int pctFree) {
        int s = 100;
        if (pctFree < 8) s -= 60;
        else if (pctFree < 12) s -= 40;
        else if (pctFree < 18) s -= 20;
        else if (pctFree < 25) s -= 10;
        return clampScore(s);
    }

    private static int scoreStability(long upMs) {
        int s = 100;
        if (upMs < 30 * 60 * 1000L) s -= 50;
        else if (upMs < 2 * 60 * 60 * 1000L) s -= 25;
        else if (upMs > 10L * 24L * 60L * 60L * 1000L) s -= 10;
        return clampScore(s);
    }

    private static int scoreSecurity(SecuritySnapshot sec) {
        int s = 100;

        if (!sec.lockSecure) s -= 30;

        if (sec.securityPatch != null && sec.securityPatch.length() >= 4) {
            try {
                int y = Integer.parseInt(sec.securityPatch.substring(0, 4));
                int curY = Calendar.getInstance().get(Calendar.YEAR);
                if (y <= curY - 3) s -= 30;
                else if (y == curY - 2) s -= 15;
            } catch (Throwable ignored) {}
        } else {
            s -= 5;
        }

        if (sec.adbUsbOn) s -= 25;
        if (sec.adbWifiOn) s -= 35;
        if (sec.devOptionsOn) s -= 10;

        if (sec.rootSuspected) s -= 40;
        if (sec.testKeys) s -= 15;

        return clampScore(s);
    }

    private static int scorePrivacy(PrivacySnapshot pr) {
        int s = 100;

        int risk = 0;
        risk += pr.userAppsWithLocation * 2;
        risk += pr.userAppsWithMic      * 3;
        risk += pr.userAppsWithCamera   * 3;
        risk += pr.userAppsWithSms      * 4;

        if (risk > 80) s -= 60;
        else if (risk > 50) s -= 40;
        else if (risk > 25) s -= 20;
        else if (risk > 10) s -= 10;

        return clampScore(s);
    }

    // ============================================================
    // PUBLIC RESULT API (LAB 29 OUTPUT)
    // ============================================================

    public static String buildLab29Summary(@NonNull Context ctx,
                                           @Nullable Float maxThermalC,
                                           @Nullable Float avgThermalC) {

        StorageSnapshot st = readStorageSnapshot(ctx);
        AppsSnapshot ap    = readAppsSnapshot(ctx);
        RamSnapshot rm     = readRamSnapshot(ctx);
        SecuritySnapshot sc= readSecuritySnapshot(ctx);
        PrivacySnapshot pr = readPrivacySnapshot(ctx);

        // Battery snapshot
        float battTemp = -1f;
        float battPct  = -1f;
        boolean charging = false;
        try {
            Intent i = ctx.registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i != null) {
                int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int temp  = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                int status= i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                battPct = (scale > 0 && level >= 0) ? (level * 100f / scale) : -1f;
                battTemp= (temp > 0) ? (temp / 10f) : -1f;
                charging = (status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL);
            }
        } catch (Throwable ignored) {}

        float maxT = (maxThermalC != null) ? maxThermalC : 0f;
        float avgT = (avgThermalC != null) ? avgThermalC : 0f;

        int sTherm = scoreThermals(maxT, avgT);
        int sBatt  = scoreBattery(battTemp, battPct, charging);
        int sStor  = scoreStorage(st.pctFree, st.totalBytes);
        int sApps  = scoreApps(ap.userApps, ap.totalApps);
        int sRam   = scoreRam(rm.pctFree);
        int sStab  = scoreStability(android.os.SystemClock.elapsedRealtime());
        int sSec   = scoreSecurity(sc);
        int sPriv  = scorePrivacy(pr);

        // Health & performance composites (same spirit as your LAB29)
        int health = (sBatt + sTherm + sStor + sRam) / 4;
        int perf   = (sApps + sRam + sStab) / 3;

        StringBuilder sb = new StringBuilder();

        sb.append("🧾 LAB 29 — FINAL SUMMARY\n");
        sb.append("--------------------------------\n");
        sb.append("Thermals: ").append(colorFlagFromScore(sTherm)).append(" ").append(sTherm).append("/100\n");
        sb.append("Battery : ").append(colorFlagFromScore(sBatt)).append(" ").append(sBatt).append("/100\n");
        sb.append("Storage : ").append(colorFlagFromScore(sStor)).append(" ").append(sStor).append("/100\n");
        sb.append("RAM     : ").append(colorFlagFromScore(sRam)).append(" ").append(sRam).append("/100\n");
        sb.append("Apps    : ").append(colorFlagFromScore(sApps)).append(" ").append(sApps).append("/100\n");
        sb.append("Stability: ").append(colorFlagFromScore(sStab)).append(" ").append(sStab).append("/100\n");
        sb.append("Security: ").append(colorFlagFromScore(sSec)).append(" ").append(sSec).append("/100\n");
        sb.append("Privacy : ").append(colorFlagFromScore(sPriv)).append(" ").append(sPriv).append("/100\n\n");

        sb.append("HEALTH  : ").append(colorFlagFromScore(health)).append(" ").append(health).append("/100\n");
        sb.append("SECURITY: ").append(colorFlagFromScore(sSec)).append(" ").append(sSec).append("/100\n");
        sb.append("PRIVACY : ").append(colorFlagFromScore(sPriv)).append(" ").append(sPriv).append("/100\n");
        sb.append("PERF    : ").append(colorFlagFromScore(perf)).append(" ").append(perf).append("/100\n\n");

        sb.append(finalVerdict(health, sSec, sPriv, perf)).append("\n");

        return sb.toString();
    }

    // ============================================================
    // UTIL (UNCHANGED)
    // ============================================================

    private static int clampScore(int s) {
        if (s < 0) return 0;
        if (s > 100) return 100;
        return s;
    }

    private static String colorFlagFromScore(int s) {
        if (s >= 80) return "🟩";
        if (s >= 55) return "🟨";
        return "🟥";
    }

    private static String finalVerdict(int health, int sec, int priv, int perf) {
        int worst = Math.min(Math.min(health, sec), Math.min(priv, perf));
        if (worst >= 80)
            return "🟩 Device is healthy — no critical issues detected.";
        if (worst >= 55)
            return "🟨 Device has moderate risks — recommend service check.";
        return "🟥 Device is NOT healthy — immediate servicing recommended.";
    }

    // Safe read helper (kept for parity)
    @SuppressWarnings("unused")
    private static String safeReadFirstLine(String p) {
        try (BufferedReader br = new BufferedReader(new FileReader(p))) {
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }
}
