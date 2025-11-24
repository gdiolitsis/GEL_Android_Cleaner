// GDiolitsis Engine Lab (GEL) — Author & Developer
// Lab29Engine — Independent Final Summary Module v2.0 (OLD LOGIC MATCH)
// ------------------------------------------------------------
// ✔ ΜΟΝΟ για LAB 29
// ✔ Δεν αγγίζει κανένα άλλο LAB / Activity
// ✔ Compile-safe, pipeline-safe
// ✔ Outputs SAME weights + breakdown as old lab29CombineFindings
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
import android.os.SystemClock;
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
            String[] paths = {
                    "/system/bin/su", "/system/xbin/su", "/sbin/su",
                    "/system/app/Superuser.apk", "/system/app/Magisk.apk",
                    "/data/adb/magisk", "/vendor/bin/su"
            };
            for (String p : paths)
                if (new File(p).exists()) return true;

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
    // SCORING (OLD LOGIC)
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

        float maxT = (maxThermalC != null) ? maxThermalC : battTemp;
        float avgT = (avgThermalC != null) ? avgThermalC : battTemp;

        int thermalScore  = scoreThermals(maxT, avgT);
        int batteryScore  = scoreBattery(battTemp, battPct, charging);
        int storageScore  = scoreStorage(st.pctFree, st.totalBytes);
        int appsScore     = scoreApps(ap.userApps, ap.totalApps);
        int ramScore      = scoreRam(rm.pctFree);
        int stabilityScore= scoreStability(SystemClock.elapsedRealtime());
        int securityScore = scoreSecurity(sc);
        int privacyScore  = scorePrivacy(pr);

        // OLD weighted composites (match old Lab29)
        int performanceScore = Math.round(
                (storageScore * 0.35f) +
                (ramScore     * 0.35f) +
                (appsScore    * 0.15f) +
                (thermalScore * 0.15f)
        );

        int deviceHealthScore = Math.round(
                (thermalScore     * 0.25f) +
                (batteryScore     * 0.25f) +
                (performanceScore * 0.30f) +
                (stabilityScore   * 0.20f)
        );

        StringBuilder sb = new StringBuilder();

        sb.append("LAB 29 — Auto Final Diagnosis Summary (FULL AUTO)\n");
        sb.append("----------------------------------------------\n\n");

        sb.append("AUTO Breakdown:\n");

        sb.append("Thermals: ").append(colorFlagFromScore(thermalScore))
                .append(" ").append(thermalScore).append("%\n");
        sb.append("• max=").append(fmt1(maxT)).append("°C | avg=")
                .append(fmt1(avgT)).append("°C\n");

        sb.append("Battery: ").append(colorFlagFromScore(batteryScore))
                .append(" ").append(batteryScore).append("%\n");
        sb.append("• Level=").append(battPct >= 0 ? fmt1(battPct) + "%" : "Unknown")
                .append(" | Temp=").append(fmt1(battTemp))
                .append("°C | Charging=").append(charging).append("\n");

        sb.append("Storage: ").append(colorFlagFromScore(storageScore))
                .append(" ").append(storageScore).append("%\n");
        sb.append("• Free=").append(st.pctFree).append("% | Used=")
                .append(humanBytes(st.usedBytes)).append(" / ")
                .append(humanBytes(st.totalBytes)).append("\n");

        sb.append("Apps Footprint: ").append(colorFlagFromScore(appsScore))
                .append(" ").append(appsScore).append("%\n");
        sb.append("• User apps=").append(ap.userApps)
                .append(" | System apps=").append(ap.systemApps)
                .append(" | Total=").append(ap.totalApps).append("\n");

        sb.append("RAM: ").append(colorFlagFromScore(ramScore))
                .append(" ").append(ramScore).append("%\n");
        sb.append("• Free=").append(rm.pctFree).append("% (")
                .append(humanBytes(rm.freeBytes)).append(" / ")
                .append(humanBytes(rm.totalBytes)).append(")\n");

        sb.append("Stability/Uptime: ").append(colorFlagFromScore(stabilityScore))
                .append(" ").append(stabilityScore).append("%\n");
        sb.append("• Uptime=").append(formatUptime(SystemClock.elapsedRealtime())).append("\n");

        sb.append("Security: ").append(colorFlagFromScore(securityScore))
                .append(" ").append(securityScore).append("%\n");
        sb.append("• Lock secure=").append(sc.lockSecure).append("\n");
        sb.append("• Patch level=").append(sc.securityPatch != null ? sc.securityPatch : "Unknown").append("\n");
        sb.append("• ADB USB=").append(sc.adbUsbOn)
                .append(" | ADB Wi-Fi=").append(sc.adbWifiOn)
                .append(" | DevOptions=").append(sc.devOptionsOn).append("\n");

        sb.append("Privacy: ").append(colorFlagFromScore(privacyScore))
                .append(" ").append(privacyScore).append("%\n");
        sb.append("• Dangerous perms on user apps: ")
                .append("Location=").append(pr.userAppsWithLocation).append(", ")
                .append("Mic=").append(pr.userAppsWithMic).append(", ")
                .append("Camera=").append(pr.userAppsWithCamera).append(", ")
                .append("SMS=").append(pr.userAppsWithSms).append("\n\n");

        sb.append("FINAL Scores:\n");
        sb.append("Device Health Score: ").append(deviceHealthScore).append("% ")
                .append(colorFlagFromScore(deviceHealthScore)).append("\n");
        sb.append("Performance Score: ").append(performanceScore).append("% ")
                .append(colorFlagFromScore(performanceScore)).append("\n");
        sb.append("Security Score: ").append(securityScore).append("% ")
                .append(colorFlagFromScore(securityScore)).append("\n");
        sb.append("Privacy Score: ").append(privacyScore).append("% ")
                .append(colorFlagFromScore(privacyScore)).append("\n\n");

        sb.append(finalVerdict(deviceHealthScore, securityScore, privacyScore, performanceScore))
                .append("\n");

        return sb.toString();
    }

    // ============================================================
    // UTIL
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

    private static String fmt1(float v) {
        return String.format(Locale.US, "%.1f", v);
    }

    private static String humanBytes(long b) {
        try {
            float kb = b / 1024f;
            float mb = kb / 1024f;
            float gb = mb / 1024f;
            if (gb >= 1f) return String.format(Locale.US, "%.2f GB", gb);
            if (mb >= 1f) return String.format(Locale.US, "%.1f MB", mb);
            if (kb >= 1f) return String.format(Locale.US, "%.0f KB", kb);
            return b + " B";
        } catch (Throwable t) {
            return b + " B";
        }
    }

    private static String formatUptime(long upMs) {
        long s = upMs / 1000L;
        long m = s / 60L; s %= 60L;
        long h = m / 60L; m %= 60L;
        long d = h / 24L; h %= 24L;
        if (d > 0) return d + "d " + h + "h " + m + "m";
        if (h > 0) return h + "h " + m + "m";
        return m + "m " + s + "s";
    }

    @SuppressWarnings("unused")
    private static String safeReadFirstLine(String p) {
        try (BufferedReader br = new BufferedReader(new FileReader(p))) {
            return br.readLine();
        } catch (Exception e) {
            return null;
        }
    }
}
