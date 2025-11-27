// GDiolitsis Engine Lab (GEL) — Author & Developer
// cpustat.c — v16.0 Triple Engine CPU%
// Priority chain:
//   1) /proc/stat   RAW (only if REAL, not fake)
//   2) /sys/cpu/... FREQ (universal, true hardware Hz)
//   3) /sys/thermal hybrid
//
// Return format:
//   RAW:     0–100
//   FREQ:    1000–1100  (encoded percent = raw+1000)
//   THERMAL: 2000–2100  (encoded percent = raw+2000)
//   FAIL:    -1

#include <jni.h>
#include <stdio.h>
#include <string.h>

// ------------------------------------------------------
// Helpers
// ------------------------------------------------------
static int clamp_int(int v, int lo, int hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static long clamp_long(long v, long lo, long hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static int read_line(const char *path, char *buf, size_t len) {
    FILE *fp = fopen(path, "r");
    if (!fp) return -1;
    if (!fgets(buf, (int)len, fp)) {
        fclose(fp);
        return -1;
    }
    fclose(fp);
    return 0;
}

static long read_long(const char *path) {
    char buf[64];
    if (read_line(path, buf, sizeof(buf)) != 0) return -1;

    size_t L = strlen(buf);
    if (L > 0 && (buf[L-1] == '\n' || buf[L-1] == '\r')) buf[L-1] = 0;

    long v = -1;
    if (sscanf(buf, "%ld", &v) != 1) return -1;
    return v;
}

// ======================================================
// 1) RAW /proc/stat ENGINE (with anti-fake logic)
// ======================================================
static long lastIdle = -1;
static long lastTotal = -1;

static int read_cpu_raw(int *outPercent) {
    char line[256];

    if (read_line("/proc/stat", line, sizeof(line)) != 0)
        return -1;

    if (strncmp(line, "cpu", 3) != 0)
        return -1;

    char cpuLabel[5];
    long user, nice, system, idle, iowait, irq, softirq;

    int s = sscanf(
        line,
        "%4s %ld %ld %ld %ld %ld %ld %ld",
        cpuLabel,
        &user, &nice, &system, &idle, &iowait, &irq, &softirq
    );

    if (s < 5)
        return -1;

    long idleAll = idle + iowait;
    long total   = user + nice + system + idle + iowait + irq + softirq;

    // First call
    if (lastIdle < 0 || lastTotal < 0) {
        lastIdle  = idleAll;
        lastTotal = total;
        *outPercent = 0;
        return 0;
    }

    long diffIdle  = idleAll - lastIdle;
    long diffTotal = total   - lastTotal;

    lastIdle  = idleAll;
    lastTotal = total;

    // --------------------------------------------------
    // Anti-fake #1:
    // RAW must have significant diffTotal. If tiny → fake.
    // --------------------------------------------------
    if (diffTotal < 50) {  // threshold tuned for fake MIUI/HyperOS
        return -1;
    }

    // --------------------------------------------------
    // Anti-fake #2:
    // All diffs zero → fake / static ticks.
    // --------------------------------------------------
    if (diffIdle == 0 && diffTotal == 0) {
        return -1;
    }

    long used = diffTotal - diffIdle;
    if (used < 0) used = 0;

    long pct = (used * 100) / diffTotal;
    pct = clamp_long(pct, 0, 100);

    *outPercent = (int)pct;
    return 0;
}

// ======================================================
// 2) UNIVERSAL FREQ ENGINE
// ======================================================
static int detect_cores() {
    int c = 0;
    for (int i = 0; i < 32; i++) {
        char path[128];
        snprintf(path, sizeof(path),
                 "/sys/devices/system/cpu/cpu%d/cpufreq", i);
        FILE *fp = fopen(path, "r");
        if (fp) {
            fclose(fp);
            c++;
        } else {
            if (c == i) break;
        }
    }
    if (c <= 0) c = 1;
    return c;
}

static int read_cpu_freq(int *outPercent) {
    int cores = detect_cores();
    if (cores <= 0) return -1;

    long acc = 0;
    int valid = 0;

    for (int i = 0; i < cores; i++) {
        char pCur[160], pMax[160];
        snprintf(pCur, sizeof(pCur),
                "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
        snprintf(pMax, sizeof(pMax),
                "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i);

        long cur = read_long(pCur);
        long max = read_long(pMax);

        if (cur <= 0 || max <= 0) continue;

        long pct = (cur * 100) / max;
        pct = clamp_long(pct, 0, 100);

        acc += pct;
        valid++;
    }

    if (valid <= 0) return -1;

    long avg = acc / valid;
    avg = clamp_long(avg, 0, 100);
    *outPercent = (int)avg;
    return 0;
}

// ======================================================
// 3) THERMAL ENGINE
// ======================================================
static int read_cpu_thermal(int *outPercent) {
    char type[128];
    char tpath[128];

    long tempMilli = -1;

    for (int i = 0; i < 32; i++) {
        char typePath[160];
        snprintf(typePath, sizeof(typePath),
                 "/sys/class/thermal/thermal_zone%d/type", i);

        if (read_line(typePath, type, sizeof(type)) != 0)
            continue;

        size_t L = strlen(type);
        if (L > 0 && (type[L-1] == '\n' || type[L-1] == '\r'))
            type[L-1] = 0;

        if (!(strstr(type, "cpu") || strstr(type, "CPU") ||
              strstr(type, "soc") || strstr(type, "SOC") ||
              strstr(type, "ap")  || strstr(type, "AP")))
            continue;

        snprintf(tpath, sizeof(tpath),
                 "/sys/class/thermal/thermal_zone%d/temp", i);

        tempMilli = read_long(tpath);
        if (tempMilli > 0) break;
    }

    if (tempMilli <= 0)
        return -1;

    double C = (tempMilli > 1000 ? tempMilli / 1000.0 : tempMilli);

    double minC = 30.0;
    double maxC = 90.0;
    double pct;

    if (C <= minC) pct = 0;
    else if (C >= maxC) pct = 100;
    else pct = (C - minC) * 100.0 / (maxC - minC);

    int result = (int)(pct + 0.5);
    result = clamp_int(result, 0, 100);

    *outPercent = result;
    return 0;
}

// ======================================================
// JNI Entry — Engine Chain & Encoding
// ======================================================
JNIEXPORT jint JNICALL
Java_com_gel_cleaner_CpuRamLiveActivity_getCpuUsageNative(JNIEnv *env, jobject obj) {

    int p = -1;

    // RAW attempt (with anti-fake)
    if (read_cpu_raw(&p) == 0) {
        return clamp_int(p, 0, 100);  // RAW: 0–100
    }

    // FREQ fallback
    if (read_cpu_freq(&p) == 0) {
        return 1000 + clamp_int(p, 0, 100);  // encoded freq
    }

    // THERMAL fallback
    if (read_cpu_thermal(&p) == 0) {
        return 2000 + clamp_int(p, 0, 100);  // encoded thermal
    }

    return -1; // total fail
}
