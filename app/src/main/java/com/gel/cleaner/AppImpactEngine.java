// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppImpactEngine.java — FINAL (Play-Store Safe • Root-Aware • Honest • Bilingual-ready consumer)
// ⚠️ Reminder: Always return the final code ready for copy-paste (no extra explanations / no questions).

package com.gel.cleaner;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class AppImpactEngine {

    private AppImpactEngine() {}

    // ============================================================
    // RESULT MODELS
    // ============================================================

    public static final class AppScore {
        public String label;
        public String pkg;
        public int uid;
        public boolean isSystem;

        // capability
        public int capabilityScore;
        public int dangerPermCount;

        public boolean hasBoot;
        public boolean hasLocation;
        public boolean hasMic;
        public boolean hasCamera;
        public boolean hasOverlay;
        public boolean hasStorage;
        public boolean hasVpnBind;
        public boolean hasPostNotif;

        public boolean bgCapable;
        public String tags; // short tag list

        // usage heuristics
        public long fgMs24h;
        public long dataBytesSinceBoot; // honest fallback
        public long estImpactScore;     // combined ranking score (not mAh)
        // root-aware dumpsys fields (advanced)
        public long dumpRxBytes;
        public long dumpTxBytes;

        // helpers
        public String safeLabel() {
            return (label != null && label.trim().length() > 0) ? label : pkg;
        }
    }

    public static final class OrphanResult {
        public int orphanDirs;
        public long orphanBytes;
        public boolean attempted;
    }

    public static final class ImpactResult {
        public int totalPkgs;
        public int userApps;
        public int systemApps;

        public int bgCapable;
        public int permHeavy;

        public int bootAware;
        public int overlayLike;
        public int vpnLike;
        public int locationLike;
        public int micLike;
        public int cameraLike;
        public int storageLike;
        public int notifLike;

        public int cleanersLike;
        public int launchersLike;
        public int antivirusLike;
        public int keyboardsLike;

        public boolean usageAccessOk; // for foreground time (UsageStats)
        public boolean rooted;        // device rooted flag input (no su used)

        public ArrayList<AppScore> topCapabilityHeavy = new ArrayList<>();
        public ArrayList<AppScore> topDataConsumers   = new ArrayList<>();
        public ArrayList<AppScore> topBatteryExposure = new ArrayList<>();

        public OrphanResult orphan = new OrphanResult();

        // verdict points (same logic as before, but stored)
        public int riskPoints;
        public boolean appsImpactHigh;
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    public static ImpactResult analyze(Context c, boolean deviceRooted) {

        ImpactResult out = new ImpactResult();
        out.rooted = deviceRooted;

        if (c == null) return out;

        final PackageManager pm = c.getPackageManager();

        // -----------------------------
        // INSTALLED APPS (SAFE)
        // -----------------------------
        List<ApplicationInfo> apps;
        try {
            apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        } catch (Throwable t) {
            return out;
        }

        if (apps == null) return out;

        out.totalPkgs = apps.size();

        // -----------------------------
        // USAGE ACCESS (for foreground time)
        // -----------------------------
        out.usageAccessOk = hasUsageAccess(c);

        // 24h range
        long end = System.currentTimeMillis();
        long start = end - 24L * 60L * 60L * 1000L;

        Map<String, Long> fgMsMap24h = new HashMap<>();
        if (out.usageAccessOk) {
            try {
                UsageStatsManager usm =
                        (UsageStatsManager) c.getSystemService(Context.USAGE_STATS_SERVICE);
                if (usm != null) {
                    List<UsageStats> stats =
                            usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);
                    if (stats != null) {
                        for (UsageStats u : stats) {
                            if (u == null) continue;
                            String pkg = u.getPackageName();
                            if (pkg == null) continue;
                            long fg = 0L;
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    fg = u.getTotalTimeVisible();
                                    if (fg <= 0) fg = u.getTotalTimeInForeground();
                                } else {
                                    fg = u.getTotalTimeInForeground();
                                }
                            } catch (Throwable ignore) {}
                            if (fg > 0) {
                                Long prev = fgMsMap24h.get(pkg);
                                fgMsMap24h.put(pkg, (prev == null ? fg : (prev + fg)));
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {}
        }

        // -----------------------------
        // SCAN LOOP
        // -----------------------------
        ArrayList<AppScore> allUser = new ArrayList<>();
        HashSet<String> installedPkgs = new HashSet<>();

        for (ApplicationInfo ai : apps) {

            if (ai == null) continue;

            final String pkg = (ai.packageName != null ? ai.packageName : "unknown");
            installedPkgs.add(pkg);

            final boolean isSystem =
                    ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ||
                    ((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);

            if (isSystem) out.systemApps++;
            else out.userApps++;

            // label
            String labelStr;
            try {
                CharSequence cs = pm.getApplicationLabel(ai);
                labelStr = (cs != null ? cs.toString() : pkg);
            } catch (Throwable ignore) {
                labelStr = pkg;
            }

            AppScore s = new AppScore();
            s.pkg = pkg;
            s.label = labelStr;
            s.uid = ai.uid;
            s.isSystem = isSystem;

            // -----------------------------
            // PERMISSIONS (requested) — capability intelligence
            // -----------------------------
            String[] reqPerms = null;
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
                if (pi != null) reqPerms = pi.requestedPermissions;
            } catch (Throwable ignore) {}

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

                    // "danger-ish" set (best-effort / honest)
                    if ("android.permission.READ_CONTACTS".equals(p) ||
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
                        "android.permission.SYSTEM_ALERT_WINDOW".equals(p)) {
                        dangerCount++;
                    }
                }
            }

            s.dangerPermCount = dangerCount;

            s.hasBoot = hasBoot;
            s.hasLocation = hasLocation;
            s.hasMic = hasMic;
            s.hasCamera = hasCamera;
            s.hasOverlay = hasOverlay;
            s.hasStorage = hasStorage;
            s.hasVpnBind = hasVpnBind;
            s.hasPostNotif = hasPostNotif;

            int capScore = 0;
            StringBuilder tags = new StringBuilder();

            if (dangerCount >= 8) { capScore += 12; tags.append("perm-heavy, "); }
            else if (dangerCount >= 5) { capScore += 8; tags.append("perm-heavy, "); }
            else if (dangerCount >= 3) { capScore += 4; }

            if (hasBoot) { capScore += 6; tags.append("boot-aware, "); }
            if (hasLocation) { capScore += 5; tags.append("location, "); }
            if (hasMic) { capScore += 5; tags.append("mic, "); }
            if (hasCamera) { capScore += 4; tags.append("camera, "); }
            if (hasOverlay) { capScore += 7; tags.append("overlay, "); }
            if (hasStorage) { capScore += 3; tags.append("storage, "); }
            if (hasVpnBind) { capScore += 6; tags.append("vpn, "); }
            if (hasPostNotif) { capScore += 2; tags.append("notifications, "); }

            boolean bg =
                    hasBoot || hasLocation || hasVpnBind || hasOverlay || hasPostNotif ||
                    dangerCount >= 5;

            s.bgCapable = bg;
            s.capabilityScore = capScore;

            // trim tags
            String tgs = tags.toString().trim();
            if (tgs.endsWith(",")) tgs = tgs.substring(0, tgs.length() - 1).trim();
            s.tags = (tgs.length() > 0 ? tgs : "capability");

            // counters (user apps only for map)
            if (!isSystem) {
                if (bg) out.bgCapable++;
                if (dangerCount >= 5) out.permHeavy++;

                if (hasBoot) out.bootAware++;
                if (hasOverlay) out.overlayLike++;
                if (hasVpnBind) out.vpnLike++;
                if (hasLocation) out.locationLike++;
                if (hasMic) out.micLike++;
                if (hasCamera) out.cameraLike++;
                if (hasStorage) out.storageLike++;
                if (hasPostNotif) out.notifLike++;

                // redundancy heuristics (pkg name)
                final String lowPkg = pkg.toLowerCase(Locale.US);
                if (lowPkg.contains("clean") || lowPkg.contains("booster") || lowPkg.contains("optimizer"))
                    out.cleanersLike++;
                if (lowPkg.contains("launcher"))
                    out.launchersLike++;
                if (lowPkg.contains("avast") || lowPkg.contains("kaspersky") || lowPkg.contains("avg") ||
                    lowPkg.contains("bitdefender") || lowPkg.contains("eset") || lowPkg.contains("norton"))
                    out.antivirusLike++;
                if (lowPkg.contains("keyboard") || lowPkg.contains("ime"))
                    out.keyboardsLike++;
            }

            // -----------------------------
            // DATA (honest, Play Store safe)
            // TrafficStats gives totals since boot (may be UNSUPPORTED = -1)
            // -----------------------------
            long rx = -1L, tx = -1L;
            try { rx = TrafficStats.getUidRxBytes(ai.uid); } catch (Throwable ignore) {}
            try { tx = TrafficStats.getUidTxBytes(ai.uid); } catch (Throwable ignore) {}

            long data = 0L;
            if (rx >= 0) data += rx;
            if (tx >= 0) data += tx;
            s.dataBytesSinceBoot = Math.max(0L, data);

            // -----------------------------
            // FG TIME 24h (requires usage access)
            // -----------------------------
            Long fg = fgMsMap24h.get(pkg);
            s.fgMs24h = (fg != null ? fg : 0L);

            // -----------------------------
            // BATTERY EXPOSURE SCORE (honest heuristic, NOT mAh)
            // - foreground minutes weight
            // - capability score weight
            // - data weight
            // -----------------------------
            long fgMin = s.fgMs24h / 60000L;
            long dataMb = s.dataBytesSinceBoot / (1024L * 1024L);

            long impact = 0L;
            impact += fgMin * 8L;              // visible usage tends to correlate with drain
            impact += (long) s.capabilityScore * 6L;
            impact += (long) Math.min(500, dataMb) * 2L; // cap influence to avoid extreme dominance
            if (s.bgCapable) impact += 35L;

            s.estImpactScore = impact;

            // keep user apps for top lists
            if (!isSystem) {
                allUser.add(s);
            }
        }

        // -----------------------------
        // TOP LISTS
        // -----------------------------
        ArrayList<AppScore> capSorted = new ArrayList<>(allUser);
        ArrayList<AppScore> dataSorted = new ArrayList<>(allUser);
        ArrayList<AppScore> battSorted = new ArrayList<>(allUser);

        // capability heavy
        Collections.sort(capSorted, (a, b) -> Integer.compare(b.capabilityScore, a.capabilityScore));
        // data heavy
        Collections.sort(dataSorted, (a, b) -> Long.compare(b.dataBytesSinceBoot, a.dataBytesSinceBoot));
        // battery exposure (heuristic)
        Collections.sort(battSorted, (a, b) -> Long.compare(b.estImpactScore, a.estImpactScore));

        // filter: exclude core system + google + play store packages
        out.topCapabilityHeavy = takeTopFiltered(capSorted, 10);
        out.topDataConsumers   = takeTopFiltered(dataSorted, 10);
        out.topBatteryExposure = takeTopFiltered(battSorted, 10);

        // -----------------------------
        // ROOT-AWARE ORPHANS (no su, best-effort)
        // -----------------------------
        out.orphan.attempted = false;
        out.orphan.orphanDirs = 0;
        out.orphan.orphanBytes = 0L;

        if (deviceRooted) {

            out.orphan.attempted = true;

            // try scan typical data roots (will fail safely without access)
            File base = new File("/data/user/0");
            try {
                if (!base.exists() || !base.isDirectory()) base = new File("/data/data");
            } catch (Throwable ignore) {}

            try {
                File[] dirs = base.listFiles();
                if (dirs != null) {
                    for (File d : dirs) {
                        if (d == null) continue;
                        boolean isDir = false;
                        try { isDir = d.isDirectory(); } catch (Throwable ignore) {}
                        if (!isDir) continue;

                        String name = null;
                        try { name = d.getName(); } catch (Throwable ignore) {}
                        if (name == null || name.length() < 3) continue;

                        if (!installedPkgs.contains(name)) {
                            long sz = dirSizeBestEffort(d);
                            if (sz > (3L * 1024L * 1024L)) {
                                out.orphan.orphanDirs++;
                                out.orphan.orphanBytes += sz;
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {}
        }

        out.appsImpactHigh =
                (out.orphan.orphanDirs > 0) || (out.orphan.orphanBytes > (200L * 1024L * 1024L));

        // -----------------------------
        // RISK POINTS (same spirit as your logic)
        // -----------------------------
        int userApps = Math.max(1, out.userApps);

        int pctBg   = (int) Math.round((out.bgCapable * 100.0) / userApps);
        int pctPerm = (int) Math.round((out.permHeavy * 100.0) / userApps);

        boolean countHigh = out.userApps >= 120;
        boolean countMed  = out.userApps >= 85;

        boolean bgHigh = pctBg >= 45 || out.bgCapable >= 45;
        boolean bgMed  = pctBg >= 30 || out.bgCapable >= 30;

        boolean permHigh = pctPerm >= 25 || out.permHeavy >= 25;
        boolean permMed  = pctPerm >= 15 || out.permHeavy >= 15;

        boolean redundancy =
                out.cleanersLike >= 2 ||
                out.launchersLike >= 2 ||
                out.antivirusLike >= 2;

        int risk = 0;

        if (countHigh) risk += 3;
        else if (countMed) risk += 2;

        if (bgHigh) risk += 3;
        else if (bgMed) risk += 2;

        if (permHigh) risk += 3;
        else if (permMed) risk += 2;

        if (redundancy) risk += 1;

        out.riskPoints = risk;

        return out;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private static boolean hasUsageAccess(Context c) {
        try {
            AppOpsManager appOps = (AppOpsManager) c.getSystemService(Context.APP_OPS_SERVICE);
            if (appOps == null) return false;

            int mode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mode = appOps.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(),
                        c.getPackageName()
                );
            } else {
                mode = appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(),
                        c.getPackageName()
                );
            }
            return mode == AppOpsManager.MODE_ALLOWED;

        } catch (Throwable ignore) {
            return false;
        }
    }

    private static ArrayList<AppScore> takeTopFiltered(List<AppScore> src, int limit) {
        ArrayList<AppScore> out = new ArrayList<>();
        if (src == null) return out;

        for (AppScore s : src) {
            if (s == null) continue;

            // strict: user apps only already, but keep safety
            if (s.isSystem) continue;

            String pkg = (s.pkg != null ? s.pkg : "");
            if (pkg.startsWith("com.android.")) continue;
            if (pkg.startsWith("com.google.android.")) continue;
            if ("com.android.vending".equals(pkg)) continue;

            out.add(s);
            if (out.size() >= limit) break;
        }
        return out;
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
                if (f.isFile()) {
                    total += Math.max(0L, f.length());
                } else if (f.isDirectory()) {
                    total += dirSizeBestEffort(f);
                }
            } catch (Throwable ignore) {}
        }
        return total;
    }

// ============================================================
// ROOT-AWARE (ADVANCED) — dumpsys batterystats / netstats
// ⚠️ NOT Play-Store safe. Use ONLY in advanced/root mode.
// ============================================================

// Add inside ImpactResult:
public boolean rootDumpsysOk;
public ArrayList<AppScore> topBatteryByDumpsys = new ArrayList<>();
public ArrayList<AppScore> topNetByDumpsys     = new ArrayList<>();

// Add inside AppScore:
public long dumpEstMah;      // best-effort from batterystats (if present)
public long dumpRxBytes;     // best-effort from netstats
public long dumpTxBytes;     // best-effort from netstats

// ============================================================
// ROOT EXEC — su -c
// ============================================================
private static String execSu(String cmd) {
    if (cmd == null || cmd.trim().isEmpty()) return null;

    java.io.BufferedReader br = null;
    try {
        Process p = new ProcessBuilder("su", "-c", cmd).redirectErrorStream(true).start();
        br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append("\n");
        try { p.waitFor(); } catch (Throwable ignore) {}
        String out = sb.toString();
        return (out.trim().length() > 0 ? out : null);
    } catch (Throwable t) {
        return null;
    } finally {
        try { if (br != null) br.close(); } catch (Throwable ignore) {}
    }
}

// ============================================================
// ROOT CHECK — quick verify su works
// ============================================================
private static boolean canUseSu() {
    String out = execSu("id");
    return out != null && out.contains("uid=0");
}

// ============================================================
// BATTERYSTATS PARSER (best-effort)
// - We try to extract per-UID/app estimated power if ROM exposes it.
// - Many ROMs output sections differently. We parse loosely.
// ============================================================
private static void fillFromBatteryStats(
        Context c,
        PackageManager pm,
        Map<Integer, AppScore> uidToScore,
        ImpactResult out
) {
    if (c == null || pm == null || uidToScore == null || out == null) return;

    // Try common variants (some devices accept --charged, some not)
    String txt = execSu("dumpsys batterystats --charged");
    if (txt == null) txt = execSu("dumpsys batterystats");
    if (txt == null) return;

    // Heuristic:
    // Look for lines that contain "Uid" and "mAh" (vendor dependent).
    // Examples seen in the wild:
    // "Uid u0a123: 12.34 mAh"
    // "UID 10123: ... 12.34 (mAh)"
    java.util.regex.Pattern p1 = java.util.regex.Pattern.compile(
            "(?i)\\bUid\\s+u0a(\\d+)\\b.*?([0-9]+\\.?[0-9]*)\\s*mAh"
    );
    java.util.regex.Pattern p2 = java.util.regex.Pattern.compile(
            "(?i)\\bUID\\s+(\\d+)\\b.*?([0-9]+\\.?[0-9]*)\\s*\\(?mAh\\)?"
    );

    java.util.HashMap<Integer, Double> uidMah = new java.util.HashMap<>();

    java.util.regex.Matcher m = p1.matcher(txt);
    while (m.find()) {
        try {
            int appId = Integer.parseInt(m.group(1));     // u0aXYZ -> appId
            double mah = Double.parseDouble(m.group(2));
            // Convert appId to UID range? best-effort: try both common user offsets
            // We cannot know userId always; so we’ll store appId-only for mapping later.
            // We'll resolve by checking all uidToScore keys ending with that appId.
            uidMah.put(-appId, mah); // negative key means appId placeholder
        } catch (Throwable ignore) {}
    }

    m = p2.matcher(txt);
    while (m.find()) {
        try {
            int uid = Integer.parseInt(m.group(1));
            double mah = Double.parseDouble(m.group(2));
            uidMah.put(uid, mah);
        } catch (Throwable ignore) {}
    }

    if (uidMah.isEmpty()) return;

    // Apply to AppScores:
    for (Map.Entry<Integer, Double> e : uidMah.entrySet()) {
        int key = e.getKey();
        double mah = e.getValue();
        long mahMilli = (long) Math.round(mah * 1000.0); // store as milli-mAh to avoid float

        if (key > 0) {
            AppScore s = uidToScore.get(key);
            if (s != null) s.dumpEstMah = mahMilli;
        } else {
            // appId placeholder: match any UID ending with that appId (best-effort)
            int appId = -key;
            for (Integer uid : uidToScore.keySet()) {
                if (uid == null) continue;
                // typical: UID = 10000 + appId (for primary user)
                if ((uid - 10000) == appId) {
                    AppScore s = uidToScore.get(uid);
                    if (s != null) s.dumpEstMah = mahMilli;
                    break;
                }
            }
        }
    }

    // Build TOP list by dumpsys mAh
    java.util.ArrayList<AppScore> tmp = new java.util.ArrayList<>();
    for (AppScore s : uidToScore.values()) {
        if (s == null) continue;
        if (s.isSystem) continue;
        if (s.dumpEstMah > 0) tmp.add(s);
    }

    java.util.Collections.sort(tmp, (a, b) -> Long.compare(b.dumpEstMah, a.dumpEstMah));

    out.topBatteryByDumpsys.clear();
    int limit = Math.min(10, tmp.size());
    for (int i = 0; i < limit; i++) out.topBatteryByDumpsys.add(tmp.get(i));
}

// ============================================================
// NETSTATS PARSER (best-effort)
// - Output varies a LOT. We try simple UID rx/tx aggregations if present.
// - If no stable pattern: we silently skip.
// ============================================================
private static void fillFromNetStats(
        Map<Integer, AppScore> uidToScore,
        ImpactResult out
) {
    if (uidToScore == null || out == null) return;

    String txt = execSu("dumpsys netstats");
    if (txt == null) return;

    // Heuristic patterns found on some ROMs:
    // "uid=10123 ... rxBytes=12345 txBytes=6789"
    java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "(?i)\\buid\\s*=\\s*(\\d+)\\b.*?\\brxBytes\\s*=\\s*(\\d+)\\b.*?\\btxBytes\\s*=\\s*(\\d+)\\b"
    );

    java.util.regex.Matcher m = p.matcher(txt);
    boolean any = false;

    while (m.find()) {
        try {
            int uid = Integer.parseInt(m.group(1));
            long rx = Long.parseLong(m.group(2));
            long tx = Long.parseLong(m.group(3));
            AppScore s = uidToScore.get(uid);
            if (s != null) {
                s.dumpRxBytes = Math.max(s.dumpRxBytes, rx);
                s.dumpTxBytes = Math.max(s.dumpTxBytes, tx);
                any = true;
            }
        } catch (Throwable ignore) {}
    }

    if (!any) return;

    java.util.ArrayList<AppScore> tmp = new java.util.ArrayList<>();
    for (AppScore s : uidToScore.values()) {
        if (s == null) continue;
        if (s.isSystem) continue;
        long total = Math.max(0L, s.dumpRxBytes) + Math.max(0L, s.dumpTxBytes);
        if (total > 0) tmp.add(s);
    }

    java.util.Collections.sort(tmp, (a, b) -> {
        long ta = Math.max(0L, a.dumpRxBytes) + Math.max(0L, a.dumpTxBytes);
        long tb = Math.max(0L, b.dumpRxBytes) + Math.max(0L, b.dumpTxBytes);
        return Long.compare(tb, ta);
    });

    out.topNetByDumpsys.clear();
    int limit = Math.min(10, tmp.size());
    for (int i = 0; i < limit; i++) out.topNetByDumpsys.add(tmp.get(i));
}
    
}
