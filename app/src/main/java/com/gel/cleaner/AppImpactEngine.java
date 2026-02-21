// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppImpactEngine.java — FINAL (Root-Aware • su-based • Best-Effort • Honest)
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
        public long dataBytesSinceBoot; // TrafficStats (honest fallback)
        public long estImpactScore;     // combined ranking score (NOT mAh)

        // ROOT (dumpsys best-effort via su)
        public long dumpRxBytes;        // netstats-derived (best-effort)
        public long dumpTxBytes;        // netstats-derived (best-effort)
        public long dumpEstMah;         // batterystats-derived (best-effort, may be 0)

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
        public boolean rooted;        // device rooted flag input

        // ROOT status
        public boolean rootDumpsysOk;

        public ArrayList<AppScore> topCapabilityHeavy = new ArrayList<>();
        public ArrayList<AppScore> topDataConsumers   = new ArrayList<>();
        public ArrayList<AppScore> topBatteryExposure = new ArrayList<>();

        // ROOT top lists
        public ArrayList<AppScore> topBatteryByDumpsys = new ArrayList<>();
        public ArrayList<AppScore> topNetByDumpsys     = new ArrayList<>();

        public OrphanResult orphan = new OrphanResult();

        // verdict points
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
        HashMap<Integer, AppScore> uidMap = new HashMap<>();

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
                    //noinspection deprecation
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
            // DATA (TrafficStats, honest, since boot)
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
            // BATTERY EXPOSURE SCORE (heuristic, NOT mAh)
            // -----------------------------
            long fgMin = s.fgMs24h / 60000L;
            long dataMb = s.dataBytesSinceBoot / (1024L * 1024L);

            long impact = 0L;
            impact += fgMin * 8L;
            impact += (long) s.capabilityScore * 6L;
            impact += (long) Math.min(500, dataMb) * 2L;
            if (s.bgCapable) impact += 35L;

            s.estImpactScore = impact;

            if (!isSystem) {
                allUser.add(s);
                uidMap.put(s.uid, s);
            }
        }

        // -----------------------------
        // ROOT-AWARE (dumpsys via su) — BEST EFFORT
        // -----------------------------
        out.rootDumpsysOk = false;
        out.topBatteryByDumpsys.clear();
        out.topNetByDumpsys.clear();

        if (deviceRooted && canUseSu()) {
            out.rootDumpsysOk = true;
            try { fillFromBatteryStats(uidMap); } catch (Throwable ignore) {}
            try { fillFromNetStats(uidMap); } catch (Throwable ignore) {}
            try { buildRootTopLists(allUser, out); } catch (Throwable ignore) {}
        }

        // -----------------------------
        // TOP LISTS (non-root)
        // -----------------------------
        ArrayList<AppScore> capSorted = new ArrayList<>(allUser);
        ArrayList<AppScore> dataSorted = new ArrayList<>(allUser);
        ArrayList<AppScore> battSorted = new ArrayList<>(allUser);

        Collections.sort(capSorted, (a, b) -> Integer.compare(b.capabilityScore, a.capabilityScore));
        Collections.sort(dataSorted, (a, b) -> Long.compare(b.dataBytesSinceBoot, a.dataBytesSinceBoot));
        Collections.sort(battSorted, (a, b) -> Long.compare(b.estImpactScore, a.estImpactScore));

        out.topCapabilityHeavy = takeTopFiltered(capSorted, 10);
        out.topDataConsumers   = takeTopFiltered(dataSorted, 10);
        out.topBatteryExposure = takeTopFiltered(battSorted, 10);

        // -----------------------------
        // ROOT-AWARE ORPHANS (no su needed; will fail safely without access)
        // -----------------------------
        out.orphan.attempted = false;
        out.orphan.orphanDirs = 0;
        out.orphan.orphanBytes = 0L;

        if (deviceRooted) {

            out.orphan.attempted = true;

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
                (out.orphan.orphanDirs > 0) ||
                (out.orphan.orphanBytes > (200L * 1024L * 1024L)) ||
                (out.rootDumpsysOk && (!out.topBatteryByDumpsys.isEmpty() || !out.topNetByDumpsys.isEmpty()));

        // -----------------------------
        // RISK POINTS
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

        if (out.rootDumpsysOk) risk += 1;

        out.riskPoints = risk;

        return out;
    }

    // ============================================================
    // HELPERS (Usage Access)
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
                //noinspection deprecation
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

    // ============================================================
    // HELPERS (Top filtered user apps)
    // ============================================================

    private static ArrayList<AppScore> takeTopFiltered(List<AppScore> src, int limit) {
        ArrayList<AppScore> out = new ArrayList<>();
        if (src == null) return out;

        for (AppScore s : src) {
            if (s == null) continue;
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

    // ============================================================
    // ROOT (su)
    // ============================================================

    private static boolean canUseSu() {
        try {
            String out = runSuGetOutput("id");
            if (out == null) return false;
            out = out.toLowerCase(Locale.US);
            return out.contains("uid=0") || out.contains("uid=0(root)");
        } catch (Throwable ignore) {
            return false;
        }
    }

    private static String runSuGetOutput(String cmd) {
        if (cmd == null) return null;
        BufferedReader br = null;
        Process p = null;
        try {
            p = new ProcessBuilder("su", "-c", cmd).redirectErrorStream(true).start();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            int lines = 0;
            while ((line = br.readLine()) != null) {
                if (line.length() > 0) {
                    sb.append(line).append('\n');
                    lines++;
                    if (lines > 40000) break;
                }
            }
            try { p.waitFor(); } catch (Throwable ignore) {}
            return sb.toString();
        } catch (Throwable t) {
            return null;
        } finally {
            try { if (br != null) br.close(); } catch (Throwable ignore) {}
            try { if (p != null) p.destroy(); } catch (Throwable ignore) {}
        }
    }

    // ============================================================
    // ROOT (dumpsys batterystats) — best effort
    // Parses common "Estimated power use (mAh):" uid lines if present
    // ============================================================

    private static void fillFromBatteryStats(HashMap<Integer, AppScore> uidMap) {

        if (uidMap == null || uidMap.isEmpty()) return;

        String txt = runSuGetOutput("dumpsys batterystats");
        if (txt == null || txt.length() < 20) return;

        String[] lines = txt.split("\n");
        boolean inEstimated = false;

        for (String raw : lines) {

            if (raw == null) continue;
            String line = raw.trim();
            if (line.length() == 0) continue;

            // detect section
            String low = line.toLowerCase(Locale.US);
            if (low.startsWith("estimated power use") || low.startsWith("estimated power consumption")) {
                inEstimated = true;
                continue;
            }
            if (inEstimated) {
                // end section heuristics
                if (low.startsWith("per-app") || low.startsWith("per app") || low.startsWith("battery history") ||
                    low.startsWith("statistics") || low.startsWith("discharge step") || low.startsWith("wifi") ||
                    low.startsWith("cellular") || low.startsWith("bluetooth") || low.startsWith("camera") ||
                    low.startsWith("screen") || low.startsWith("uid") && low.contains("cpu")) {
                    // not a strict end; keep going
                }
            }

            if (!inEstimated) {
                // still allow direct uid lines in some formats
                // e.g. "Uid u0a123: 12.34"
                // e.g. "u0a123: 12.34 (mAh)"
            }

            // try patterns that appear in many builds
            // Pattern A: "Uid u0a123: 12.34"
            if (line.startsWith("Uid ")) {
                // "Uid u0a123: 12.34"
                int c1 = line.indexOf(' ');
                int c2 = line.indexOf(':');
                if (c2 > c1) {
                    String uidToken = line.substring(c1 + 1, c2).trim();
                    String rest = line.substring(c2 + 1).trim();

                    long mahMilli = parseMahToMilli(rest);
                    int uid = parseUidTokenToInt(uidToken);
                    if (uid > 0 && mahMilli > 0) {
                        AppScore s = uidMap.get(uid);
                        if (s != null) s.dumpEstMah = Math.max(s.dumpEstMah, mahMilli);
                    }
                }
                continue;
            }

            // Pattern B: "u0a123: 12.34"
            int c = line.indexOf(':');
            if (c > 1 && c < 20) {
                String left = line.substring(0, c).trim();
                String rest = line.substring(c + 1).trim();

                int uid = parseUidTokenToInt(left);
                if (uid > 0) {
                    long mahMilli = parseMahToMilli(rest);
                    if (mahMilli > 0) {
                        AppScore s = uidMap.get(uid);
                        if (s != null) s.dumpEstMah = Math.max(s.dumpEstMah, mahMilli);
                    }
                }
            }
        }
    }

    private static long parseMahToMilli(String s) {
        if (s == null) return 0L;
        s = s.trim();
        if (s.length() == 0) return 0L;

        // extract first number
        StringBuilder num = new StringBuilder();
        boolean dot = false;
        boolean started = false;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= '0' && ch <= '9')) {
                num.append(ch);
                started = true;
                continue;
            }
            if (ch == '.' && !dot) {
                num.append(ch);
                dot = true;
                started = true;
                continue;
            }
            if (started) break;
        }

        if (num.length() == 0) return 0L;

        try {
            double mah = Double.parseDouble(num.toString());
            if (mah <= 0) return 0L;
            return (long) Math.round(mah * 1000.0);
        } catch (Throwable ignore) {
            return 0L;
        }
    }

    private static int parseUidTokenToInt(String token) {
        if (token == null) return -1;
        token = token.trim();
        if (token.length() == 0) return -1;

        // u0a123 => 10000 + 123
        // u10a123 => 10*100000 + 10000 + 123 (best-effort)
        // uid=10123 => 10123
        try {
            if (token.startsWith("uid=")) token = token.substring(4).trim();
        } catch (Throwable ignore) {}

        if (token.startsWith("u")) {
            // u{user}{a|i}{id}
            int aPos = token.indexOf('a');
            int iPos = token.indexOf('i');
            int pos = (aPos > 0 ? aPos : (iPos > 0 ? iPos : -1));
            if (pos > 1 && pos < token.length() - 1) {
                String userStr = token.substring(1, pos).trim();
                String appIdStr = token.substring(pos + 1).trim();
                int user = 0;
                int appId = -1;
                try { user = Integer.parseInt(userStr); } catch (Throwable ignore) { user = 0; }
                try { appId = Integer.parseInt(appIdStr); } catch (Throwable ignore) { appId = -1; }

                if (appId >= 0) {
                    // Android uid formula (best-effort)
                    // base per user: user * 100000
                    // app base: 10000
                    return (user * 100000) + 10000 + appId;
                }
            }
        }

        try {
            return Integer.parseInt(token);
        } catch (Throwable ignore) {
            return -1;
        }
    }

    // ============================================================
    // ROOT (dumpsys netstats) — best effort
    // Parses common "uid=... rxBytes=... txBytes=..." lines if present
    // ============================================================

    private static void fillFromNetStats(HashMap<Integer, AppScore> uidMap) {

        if (uidMap == null || uidMap.isEmpty()) return;

        String txt = runSuGetOutput("dumpsys netstats detail");
        if (txt == null || txt.length() < 20) return;

        String[] lines = txt.split("\n");

        for (String raw : lines) {
            if (raw == null) continue;
            String line = raw.trim();
            if (line.length() == 0) continue;

            int uid = extractIntAfter(line, "uid=");
            if (uid <= 0) continue;

            long rx = extractLongAfter(line, "rxBytes=");
            long tx = extractLongAfter(line, "txBytes=");

            if (rx <= 0 && tx <= 0) continue;

            AppScore s = uidMap.get(uid);
            if (s == null) continue;

            if (rx > 0) s.dumpRxBytes += rx;
            if (tx > 0) s.dumpTxBytes += tx;
        }
    }

    private static int extractIntAfter(String line, String key) {
        if (line == null || key == null) return -1;
        int p = line.indexOf(key);
        if (p < 0) return -1;
        p += key.length();
        int end = p;
        while (end < line.length()) {
            char ch = line.charAt(end);
            if (ch < '0' || ch > '9') break;
            end++;
        }
        if (end <= p) return -1;
        try { return Integer.parseInt(line.substring(p, end)); } catch (Throwable ignore) { return -1; }
    }

    private static long extractLongAfter(String line, String key) {
        if (line == null || key == null) return -1L;
        int p = line.indexOf(key);
        if (p < 0) return -1L;
        p += key.length();
        int end = p;
        while (end < line.length()) {
            char ch = line.charAt(end);
            if (ch < '0' || ch > '9') break;
            end++;
        }
        if (end <= p) return -1L;
        try { return Long.parseLong(line.substring(p, end)); } catch (Throwable ignore) { return -1L; }
    }

    private static void buildRootTopLists(ArrayList<AppScore> allUser, ImpactResult out) {
        if (allUser == null || out == null) return;

        ArrayList<AppScore> batt = new ArrayList<>();
        ArrayList<AppScore> net  = new ArrayList<>();

        for (AppScore s : allUser) {
            if (s == null) continue;
            if (s.isSystem) continue;

            if (s.dumpEstMah > 0) batt.add(s);

            long totalNet = Math.max(0L, s.dumpRxBytes) + Math.max(0L, s.dumpTxBytes);
            if (totalNet > 0) net.add(s);
        }

        Collections.sort(batt, (a, b) -> Long.compare(b.dumpEstMah, a.dumpEstMah));
        Collections.sort(net, (a, b) -> {
            long ta = Math.max(0L, a.dumpRxBytes) + Math.max(0L, a.dumpTxBytes);
            long tb = Math.max(0L, b.dumpRxBytes) + Math.max(0L, b.dumpTxBytes);
            return Long.compare(tb, ta);
        });

        out.topBatteryByDumpsys = takeTopFiltered(batt, 10);
        out.topNetByDumpsys     = takeTopFiltered(net, 10);
    }

    // ============================================================
    // DIR SIZE (safe)
    // ============================================================

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
}
