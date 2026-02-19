// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppImpactEngine.java — PRODUCTION SAFE (Play Store safe) — ROOT AWARE (optional)
// NOTE: This engine is "best-effort": battery/data per-app is restricted on modern Android.
// It provides: storage footprint + capability-based risk + (optional) usage-stats based "recent activity" hints
// and (where allowed) per-UID Wi-Fi data usage via NetworkStatsManager.
//
// REMINDER (for next chats): Send me the final file you’re editing and I will return full copy-paste, no extra bla bla.

package com.gel.cleaner;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkStats;
import android.net.NetworkStatsManager;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public final class AppImpactEngine {

    private AppImpactEngine() {}

    // ============================================================
    // MODELS
    // ============================================================

    public static final class ImpactApp {
        public String pkg = "unknown";
        public String label = "unknown";
        public boolean isSystem = false;

        // storage (best-effort)
        public long storageBytes = -1;

        // data usage (best-effort, often restricted)
        public long wifiRxBytes = -1;
        public long wifiTxBytes = -1;
        public long mobileRxBytes = -1;
        public long mobileTxBytes = -1;

        // usage activity hint (requires usage access)
        public long lastUsedMs = -1;

        // capability/risk scoring (honest)
        public int dangerPermCount = 0;
        public boolean hasBoot = false;
        public boolean hasLocation = false;
        public boolean hasMic = false;
        public boolean hasCamera = false;
        public boolean hasOverlay = false;
        public boolean hasStorage = false;
        public boolean hasVpnBind = false;
        public boolean hasPostNotif = false;

        public boolean bgCapable = false;
        public int capabilityScore = 0;
        public String tags = "";

        // final composite score (for ranking)
        public int impactScore = 0;
    }

    public static final class ImpactSummary {
        public int totalPkgs;
        public int userApps;
        public int systemApps;

        public int bgCapableUserApps;
        public int permHeavyUserApps;

        public int bootAware;
        public int locationLike;
        public int micLike;
        public int cameraLike;
        public int overlayLike;
        public int vpnLike;
        public int storageLike;
        public int notifLike;

        public boolean usageAccessGranted;
        public boolean canReadStorageStats;
        public boolean canReadNetworkStats;

        public int pctBg;
        public int pctPerm;
    }

    public static final class ImpactResult {
        public ImpactSummary summary = new ImpactSummary();
        public ArrayList<ImpactApp> apps = new ArrayList<>();

        // root-aware leftovers
        public int orphanDirs = 0;
        public long orphanBytes = 0L;
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Scan installed apps and compute an "impact" ranking.
     *
     * @param ctx Android context
     * @param includeSystemApps if true, returns system apps too (still ranks user apps higher)
     * @param rooted if true, enables root-aware leftovers inspection (no root commands used here; caller decides)
     * @param nowMs current time in ms (System.currentTimeMillis())
     * @param windowMs lookback window for usage access (e.g. 7 days)
     */
    public static ImpactResult scan(
            Context ctx,
            boolean includeSystemApps,
            boolean rooted,
            long nowMs,
            long windowMs
    ) {
        ImpactResult out = new ImpactResult();
        if (ctx == null) return out;

        final PackageManager pm = ctx.getPackageManager();
        if (pm == null) return out;

        List<ApplicationInfo> list;
        try {
            list = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        } catch (Throwable t) {
            return out;
        }
        if (list == null) return out;

        out.summary.totalPkgs = list.size();

        final boolean usageGranted = hasUsageAccess(ctx);
        out.summary.usageAccessGranted = usageGranted;

        final boolean canStorageStats = canReadStorageStats(ctx);
        out.summary.canReadStorageStats = canStorageStats;

        final boolean canNetStats = canReadNetworkStats(ctx);
        out.summary.canReadNetworkStats = canNetStats;

        final StorageStatsManager ssm = (StorageStatsManager)
                ctx.getSystemService(Context.STORAGE_STATS_SERVICE);

        final NetworkStatsManager nsm = (NetworkStatsManager)
                ctx.getSystemService(Context.NETWORK_STATS_SERVICE);

        final UsageStatsManager usm = (UsageStatsManager)
                ctx.getSystemService(Context.USAGE_STATS_SERVICE);

        final long fromMs = Math.max(0L, nowMs - Math.max(0L, windowMs));

        // cache package-info perms to reduce cost
        Map<String, String[]> permsCache = new HashMap<>();

        for (ApplicationInfo ai : list) {
            if (ai == null) continue;

            final boolean isSystem =
                    ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ||
                    ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);

            if (!includeSystemApps && isSystem) continue;

            ImpactApp app = new ImpactApp();
            app.isSystem = isSystem;

            String pkg = ai.packageName != null ? ai.packageName : "unknown";
            app.pkg = pkg;

            try {
                CharSequence cs = pm.getApplicationLabel(ai);
                app.label = cs != null ? cs.toString() : pkg;
            } catch (Throwable ignore) {
                app.label = pkg;
            }

            if (isSystem) out.summary.systemApps++;
            else out.summary.userApps++;

            // --------------------------------------------------------
            // PERMISSIONS / CAPABILITIES (honest)
            // --------------------------------------------------------
            String[] reqPerms = permsCache.get(pkg);
            if (reqPerms == null) {
                reqPerms = safeGetRequestedPerms(pm, pkg);
                permsCache.put(pkg, reqPerms);
            }
            applyCapabilityScan(app, reqPerms);

            if (!isSystem) {
                if (app.bgCapable) out.summary.bgCapableUserApps++;
                if (app.dangerPermCount >= 5) out.summary.permHeavyUserApps++;

                if (app.hasBoot) out.summary.bootAware++;
                if (app.hasOverlay) out.summary.overlayLike++;
                if (app.hasVpnBind) out.summary.vpnLike++;
                if (app.hasLocation) out.summary.locationLike++;
                if (app.hasMic) out.summary.micLike++;
                if (app.hasCamera) out.summary.cameraLike++;
                if (app.hasStorage) out.summary.storageLike++;
                if (app.hasPostNotif) out.summary.notifLike++;
            }

            // --------------------------------------------------------
            // STORAGE (best-effort; may require special access on some devices)
            // --------------------------------------------------------
            if (canStorageStats && ssm != null) {
                app.storageBytes = safeStorageBytes(ctx, ssm, ai);
            } else {
                app.storageBytes = -1;
            }

            // --------------------------------------------------------
            // USAGE (best-effort; requires Usage Access)
            // --------------------------------------------------------
            if (usageGranted && usm != null) {
                app.lastUsedMs = safeLastUsed(usm, pkg, fromMs, nowMs);
            } else {
                app.lastUsedMs = -1;
            }

            // --------------------------------------------------------
            // NETWORK DATA (best-effort; restricted on modern Android)
            // Wi-Fi per-UID often works with PACKAGE_USAGE_STATS on many devices.
            // Mobile per-UID usually needs subscriberId (often blocked) => returns -1.
            // --------------------------------------------------------
            if (canNetStats && nsm != null) {
                long uid = ai.uid;
                long[] wifi = safeWifiBytesForUid(nsm, uid, fromMs, nowMs);
                app.wifiRxBytes = wifi[0];
                app.wifiTxBytes = wifi[1];

                // mobile: best-effort only (likely -1)
                long[] mob = safeMobileBytesForUid(nsm, ctx, uid, fromMs, nowMs);
                app.mobileRxBytes = mob[0];
                app.mobileTxBytes = mob[1];
            }

            // --------------------------------------------------------
            // IMPACT SCORE (composite)
            // --------------------------------------------------------
            app.impactScore = computeImpactScore(app, isSystem, nowMs);

            out.apps.add(app);
        }

        // percentages
        out.summary.pctBg =
                (int) Math.round((out.summary.bgCapableUserApps * 100.0) / Math.max(1, out.summary.userApps));
        out.summary.pctPerm =
                (int) Math.round((out.summary.permHeavyUserApps * 100.0) / Math.max(1, out.summary.userApps));

        // sort (high impact first; always prefer user apps)
        Collections.sort(out.apps, (a, b) -> Integer.compare(b.impactScore, a.impactScore));

        // root-aware leftovers (best-effort file scan; caller decides rooted boolean)
        if (rooted) {
            rootAwareLeftovers(out, list);
        }

        return out;
    }

    // ============================================================
    // SCORING
    // ============================================================

    private static int computeImpactScore(ImpactApp app, boolean isSystem, long nowMs) {

        int score = 0;

        // 1) capability pressure (honest)
        score += app.capabilityScore; // already weighted

        // 2) storage pressure
        if (app.storageBytes > 0) {
            if (app.storageBytes >= 2L * 1024L * 1024L * 1024L) score += 14;       // 2GB+
            else if (app.storageBytes >= 1L * 1024L * 1024L * 1024L) score += 10; // 1GB+
            else if (app.storageBytes >= 400L * 1024L * 1024L) score += 7;        // 400MB+
            else if (app.storageBytes >= 150L * 1024L * 1024L) score += 4;        // 150MB+
            else score += 1;
        }

        // 3) data pressure (wifi + mobile)
        long data = 0;
        if (app.wifiRxBytes > 0) data += app.wifiRxBytes;
        if (app.wifiTxBytes > 0) data += app.wifiTxBytes;
        if (app.mobileRxBytes > 0) data += app.mobileRxBytes;
        if (app.mobileTxBytes > 0) data += app.mobileTxBytes;

        if (data > 0) {
            if (data >= 3L * 1024L * 1024L * 1024L) score += 12;       // 3GB+
            else if (data >= 1L * 1024L * 1024L * 1024L) score += 9;   // 1GB+
            else if (data >= 300L * 1024L * 1024L) score += 6;         // 300MB+
            else if (data >= 80L * 1024L * 1024L) score += 3;          // 80MB+
            else score += 1;
        }

        // 4) recently used (if available) => higher relevance (not "bad")
        if (app.lastUsedMs > 0 && nowMs > 0) {
            long age = Math.max(0L, nowMs - app.lastUsedMs);
            if (age <= 24L * 60L * 60L * 1000L) score += 4;           // used in last 24h
            else if (age <= 3L * 24L * 60L * 60L * 1000L) score += 2; // used in 3 days
            else if (age <= 7L * 24L * 60L * 60L * 1000L) score += 1; // used in 7 days
        }

        // system apps: always de-prioritize in cleanup lists
        if (isSystem) score -= 8;

        // clamp
        if (score < 0) score = 0;
        if (score > 100) score = 100;

        return score;
    }

    // ============================================================
    // CAPABILITY SCAN (LAB26 logic-compatible)
    // ============================================================

    private static void applyCapabilityScan(ImpactApp app, String[] reqPerms) {

        int score = 0;
        StringBuilder tags = new StringBuilder();

        int dangerCount = 0;

        boolean hasBoot = false;
        boolean hasLocation = false;
        boolean hasMic = false;
        boolean hasCamera = false;
        boolean hasOverlay = false;
        boolean hasStorage = false;
        boolean hasVpnBind = false;
        boolean hasPostNotif = false;

        if (reqPerms != null) {
            for (String p : reqPerms) {
                if (p == null) continue;

                if ("android.permission.RECEIVE_BOOT_COMPLETED".equals(p)) hasBoot = true;

                if ("android.permission.ACCESS_FINE_LOCATION".equals(p) ||
                    "android.permission.ACCESS_COARSE_LOCATION".equals(p)) hasLocation = true;

                if ("android.permission.RECORD_AUDIO".equals(p)) hasMic = true;
                if ("android.permission.CAMERA".equals(p)) hasCamera = true;

                if ("android.permission.SYSTEM_ALERT_WINDOW".equals(p)) hasOverlay = true;

                if ("android.permission.READ_EXTERNAL_STORAGE".equals(p) ||
                    "android.permission.WRITE_EXTERNAL_STORAGE".equals(p) ||
                    "android.permission.READ_MEDIA_IMAGES".equals(p) ||
                    "android.permission.READ_MEDIA_VIDEO".equals(p) ||
                    "android.permission.READ_MEDIA_AUDIO".equals(p)) hasStorage = true;

                if ("android.permission.BIND_VPN_SERVICE".equals(p)) hasVpnBind = true;

                if ("android.permission.POST_NOTIFICATIONS".equals(p)) hasPostNotif = true;

                if (isDangerish(p)) dangerCount++;
            }
        }

        if (dangerCount >= 8) { score += 12; tags.append("perm-heavy, "); }
        else if (dangerCount >= 5) { score += 8; tags.append("perm-heavy, "); }
        else if (dangerCount >= 3) { score += 4; }

        if (hasBoot) { score += 6; tags.append("boot-aware, "); }
        if (hasLocation) { score += 5; tags.append("location, "); }
        if (hasMic) { score += 5; tags.append("mic, "); }
        if (hasCamera) { score += 4; tags.append("camera, "); }
        if (hasOverlay) { score += 7; tags.append("overlay, "); }
        if (hasStorage) { score += 3; tags.append("storage, "); }
        if (hasVpnBind) { score += 6; tags.append("vpn, "); }
        if (hasPostNotif) { score += 2; tags.append("notifications, "); }

        boolean bg =
                hasBoot || hasLocation || hasVpnBind || hasOverlay || hasPostNotif ||
                dangerCount >= 5;

        if (bg) {
            // tiny bonus to separate "quiet" apps from "background-capable"
            score += 2;
        }

        app.dangerPermCount = dangerCount;
        app.hasBoot = hasBoot;
        app.hasLocation = hasLocation;
        app.hasMic = hasMic;
        app.hasCamera = hasCamera;
        app.hasOverlay = hasOverlay;
        app.hasStorage = hasStorage;
        app.hasVpnBind = hasVpnBind;
        app.hasPostNotif = hasPostNotif;

        app.bgCapable = bg;
        app.capabilityScore = score;

        String t = tags.toString().trim();
        if (t.endsWith(",")) t = t.substring(0, t.length() - 1).trim();
        app.tags = t.length() > 0 ? t : "low-capability";
    }

    private static boolean isDangerish(String p) {
        return
                "android.permission.READ_CONTACTS".equals(p) ||
                "android.permission.WRITE_CONTACTS".equals(p) ||
                "android.permission.READ_CALL_LOG".equals(p) ||
                "android.permission.WRITE_CALL_LOG".equals(p) ||
                "android.permission.READ_SMS".equals(p) ||
                "android.permission.SEND_SMS".equals(p) ||
                "android.permission.RECEIVE_SMS".equals(p) ||
                "android.permission.READ_PHONE_STATE".equals(p) ||
                "android.permission.CALL_PHONE".equals(p) ||
                "android.permission.ACCESS_FINE_LOCATION".equals(p) ||
                "android.permission.RECORD_AUDIO".equals(p) ||
                "android.permission.CAMERA".equals(p) ||
                "android.permission.BODY_SENSORS".equals(p) ||
                "android.permission.USE_SIP".equals(p) ||
                "android.permission.WRITE_SETTINGS".equals(p) ||
                "android.permission.SYSTEM_ALERT_WINDOW".equals(p);
    }

    // ============================================================
    // PERMISSION / ACCESS CHECKS
    // ============================================================

    public static boolean hasUsageAccess(Context ctx) {
        if (ctx == null) return false;
        try {
            AppOpsManager aom = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
            if (aom == null) return false;

            int mode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mode = aom.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        Process.myUid(),
                        ctx.getPackageName()
                );
            } else {
                mode = aom.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        Process.myUid(),
                        ctx.getPackageName()
                );
            }
            return mode == AppOpsManager.MODE_ALLOWED;

        } catch (Throwable ignore) {
            return false;
        }
    }

    private static boolean canReadStorageStats(Context ctx) {
        if (ctx == null) return false;

        // StorageStatsManager often works without a runtime permission,
        // but can throw SecurityException on OEMs / profiles.
        try {
            StorageStatsManager ssm = (StorageStatsManager)
                    ctx.getSystemService(Context.STORAGE_STATS_SERVICE);
            return ssm != null;
        } catch (Throwable ignore) {
            return false;
        }
    }

    private static boolean canReadNetworkStats(Context ctx) {
        if (ctx == null) return false;
        // NetworkStatsManager requires "Usage access" in many cases.
        // There is also android.permission.PACKAGE_USAGE_STATS (special access).
        return hasUsageAccess(ctx);
    }

    // ============================================================
    // REQUESTED PERMS
    // ============================================================

    private static String[] safeGetRequestedPerms(PackageManager pm, String pkg) {
        if (pm == null || pkg == null) return null;

        try {
            PackageInfo pi;
            if (Build.VERSION.SDK_INT >= 33) {
                pi = pm.getPackageInfo(
                        pkg,
                        PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS)
                );
            } else {
                pi = pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);
            }
            return (pi != null ? pi.requestedPermissions : null);

        } catch (Throwable ignore) {
            return null;
        }
    }

    // ============================================================
    // STORAGE STATS
    // ============================================================

    private static long safeStorageBytes(Context ctx, StorageStatsManager ssm, ApplicationInfo ai) {
        if (ctx == null || ssm == null || ai == null) return -1;

        try {
            UUID uuid = (ai.storageUuid != null ? ai.storageUuid : StorageManagerCompat.getDefaultUuid());
            UserHandle uh = UserHandle.getUserHandleForUid(ai.uid);

            StorageStats st = ssm.queryStatsForPackage(uuid, ai.packageName, uh);
            if (st == null) return -1;

            // app + data + cache
            long total = 0;
            total += safeLong(st.getAppBytes());
            total += safeLong(st.getDataBytes());
            total += safeLong(st.getCacheBytes());
            return total;

        } catch (Throwable t) {
            return -1;
        }
    }

    private static long safeLong(long v) {
        return Math.max(0L, v);
    }

    // ============================================================
    // USAGE LAST USED (events)
    // ============================================================

    private static long safeLastUsed(UsageStatsManager usm, String pkg, long fromMs, long toMs) {
        if (usm == null || pkg == null) return -1;

        long last = -1;
        try {
            UsageEvents ev = usm.queryEvents(fromMs, toMs);
            if (ev == null) return -1;

            UsageEvents.Event e = new UsageEvents.Event();
            while (ev.hasNextEvent()) {
                ev.getNextEvent(e);
                if (e == null) continue;
                if (!pkg.equals(e.getPackageName())) continue;

                int type = e.getEventType();
                // best-effort: consider move-to-foreground/resume as "used"
                if (type == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                    type == UsageEvents.Event.ACTIVITY_RESUMED) {
                    last = Math.max(last, e.getTimeStamp());
                }
            }
        } catch (Throwable ignore) {}

        return last;
    }

    // ============================================================
    // NETWORK STATS (best-effort)
    // ============================================================

    private static long[] safeWifiBytesForUid(NetworkStatsManager nsm, long uid, long fromMs, long toMs) {
        long[] out = new long[]{ -1, -1 };
        if (nsm == null) return out;

        try {
            NetworkStats ns = nsm.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    null, // subscriberId not needed for Wi-Fi
                    fromMs,
                    toMs,
                    (int) uid
            );

            if (ns == null) return out;

            long rx = 0, tx = 0;
            NetworkStats.Bucket b = new NetworkStats.Bucket();
            while (ns.hasNextBucket()) {
                ns.getNextBucket(b);
                rx += Math.max(0L, b.getRxBytes());
                tx += Math.max(0L, b.getTxBytes());
            }
            try { ns.close(); } catch (Throwable ignore) {}

            out[0] = rx;
            out[1] = tx;
            return out;

        } catch (Throwable t) {
            return out;
        }
    }

    private static long[] safeMobileBytesForUid(NetworkStatsManager nsm, Context ctx, long uid, long fromMs, long toMs) {
        // Most devices block subscriberId access -> return -1/-1 unless we can safely obtain it.
        long[] out = new long[]{ -1, -1 };
        if (nsm == null || ctx == null) return out;

        try {
            String subId = MobileSubscriberCompat.tryGetSubscriberId(ctx);
            if (TextUtils.isEmpty(subId)) return out;

            NetworkStats ns = nsm.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    subId,
                    fromMs,
                    toMs,
                    (int) uid
            );

            if (ns == null) return out;

            long rx = 0, tx = 0;
            NetworkStats.Bucket b = new NetworkStats.Bucket();
            while (ns.hasNextBucket()) {
                ns.getNextBucket(b);
                rx += Math.max(0L, b.getRxBytes());
                tx += Math.max(0L, b.getTxBytes());
            }
            try { ns.close(); } catch (Throwable ignore) {}

            out[0] = rx;
            out[1] = tx;
            return out;

        } catch (Throwable t) {
            return out;
        }
    }

    // ============================================================
    // ROOT-AWARE LEFTOVERS (NO COMMANDS HERE)
    // ============================================================

    private static void rootAwareLeftovers(ImpactResult out, List<ApplicationInfo> apps) {
        if (out == null || apps == null) return;

        HashSet<String> installed = new HashSet<>();
        for (ApplicationInfo ai : apps) {
            if (ai != null && ai.packageName != null) installed.add(ai.packageName);
        }

        int orphanDirs = 0;
        long orphanBytes = 0L;

        try {
            File base = new File("/data/user/0");
            if (!base.exists() || !base.isDirectory()) base = new File("/data/data");

            File[] dirs = base.listFiles();
            if (dirs != null) {
                for (File d : dirs) {
                    if (d == null || !d.isDirectory()) continue;
                    String name = d.getName();
                    if (name == null || name.length() < 3) continue;

                    if (!installed.contains(name)) {
                        long sz = dirSizeBestEffort(d);
                        if (sz > (3L * 1024L * 1024L)) { // 3MB+
                            orphanDirs++;
                            orphanBytes += sz;
                        }
                    }
                }
            }
        } catch (Throwable ignore) {}

        out.orphanDirs = orphanDirs;
        out.orphanBytes = orphanBytes;
    }

    private static long dirSizeBestEffort(File dir) {
        if (dir == null) return 0L;
        try {
            if (!dir.exists() || !dir.isDirectory()) return 0L;
        } catch (Throwable ignore) { return 0L; }

        long total = 0L;
        File[] files;
        try {
            files = dir.listFiles();
        } catch (Throwable t) {
            return 0L;
        }
        if (files == null) return 0L;

        for (File f : files) {
            if (f == null) continue;
            try {
                if (f.isFile()) total += Math.max(0L, f.length());
                else if (f.isDirectory()) total += dirSizeBestEffort(f);
            } catch (Throwable ignore) {}
        }
        return total;
    }

    // ============================================================
    // SMALL COMPAT HELPERS (no new files needed)
    // ============================================================

    private static final class StorageManagerCompat {
        private StorageManagerCompat() {}
        static UUID getDefaultUuid() {
            // "internal" UUID on Android; best-effort.
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    return android.os.storage.StorageManager.UUID_DEFAULT;
                }
            } catch (Throwable ignore) {}
            // fallback: random UUID (will likely fail queryStats; handled)
            return new UUID(0L, 0L);
        }
    }

    private static final class MobileSubscriberCompat {
        private MobileSubscriberCompat() {}

        static String tryGetSubscriberId(Context ctx) {
            // Play Store safe: only attempt if permission granted AND API allows.
            // On Android 10+ it’s often restricted => return null.
            try {
                if (ctx == null) return null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null;

                if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }

                android.telephony.TelephonyManager tm =
                        (android.telephony.TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm == null) return null;

                return tm.getSubscriberId();

            } catch (Throwable ignore) {
                return null;
            }
        }
    }
}
