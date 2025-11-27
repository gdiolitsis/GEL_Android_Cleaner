// GDiolitsis Engine Lab (GEL) — Author & Developer
// cpustat.c — v15.0 Triple Engine CPU%
// Priority:
//   1) /proc/stat   RAW kernel CPU%
//   2) /sys/cpu/... FREQ-based universal fallback
//   3) /sys/thermal hybrid fallback
//
// Return:
//   0–100 : valid CPU%
//   -1    : total failure (no source available)

#include <jni.h>
#include <stdio.h>
#include <string.h>

// ------------------------------------------------------
// Shared state for /proc/stat RAW mode
// ------------------------------------------------------
static long lastIdle = -1;
static long lastTotal = -1;

// ------------------------------------------------------
// Small helpers
// ------------------------------------------------------
static long clamp_long(long v, long lo, long hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static int clamp_int(int v, int lo, int hi) {
    if (v < lo) return lo;
    if (v > hi) return hi;
    return v;
}

static int read_line(const char *path, char *buf, size_t bufSize) {
    FILE *fp = fopen(path, "r");
    if (fp == NULL) return -1;
    if (fgets(buf, (int)bufSize, fp) == NULL) {
        fclose(fp);
        return -1;
    }
    fclose(fp);
    return 0;
}

// ======================================================
// 1) RAW /proc/stat engine
// ======================================================
static int read_cpu_procstat_raw(int *outPercent) {
    FILE *fp = fopen("/proc/stat", "r");
    if (fp == NULL) {
        return -1;
    }

    char line[256];
    if (fgets(line, sizeof(line), fp) == NULL) {
        fclose(fp);
        return -1;
    }
    fclose(fp);

    if (strncmp(line, "cpu", 3) != 0) {
        return -1;
    }

    // cpu  user nice system idle iowait irq softirq [steal guest guest_nice]
    char cpuLabel[5];
    long user, nice, system, idle, iowait, irq, softirq;
    int scanned = sscanf(
            line,
            "%4s %ld %ld %ld %ld %ld %ld %ld",
            cpuLabel,
            &user, &nice, &system, &idle, &iowait, &irq, &softirq
    );

    if (scanned < 5) {
        // δεν έχουμε τουλάχιστον user, nice, system, idle
        return -1;
    }

    long idleAll = idle + iowait;
    long total   = user + nice + system + idle + iowait + irq + softirq;

    // Πρώτη φορά: απλά αποθήκευσε και γύρνα 0% (νόμιμο, όχι N/A)
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

    if (diffTotal <= 0) {
        *outPercent = 0;
        return 0;
    }

    long used  = diffTotal - diffIdle;
    long usage = used * 100 / diffTotal;

    usage = clamp_long(usage, 0, 100);
    *outPercent = (int)usage;
    return 0;
}

// ======================================================
// 2) /sys/devices/system/cpu/... freq engine
// ======================================================
static long read_long_file(const char *path) {
    char buf[64];
    if (read_line(path, buf, sizeof(buf)) != 0) {
        return -1;
    }
    // strip newline
    size_t len = strlen(buf);
    if (len > 0 && (buf[len - 1] == '\n' || buf[len - 1] == '\r')) {
        buf[len - 1] = '\0';
    }
    long val = -1;
    if (sscanf(buf, "%ld", &val) != 1) {
        return -1;
    }
    return val;
}

static int detect_cores() {
    // απλό brute force: cpu0..cpu31
    int cores = 0;
    for (int i = 0; i < 32; ++i) {
        char path[128];
        snprintf(path, sizeof(path), "/sys/devices/system/cpu/cpu%d/online", i);
        FILE *fp = fopen(path, "r");
        if (fp) {
            // if "online" file υπάρχει, είναι valid core
            fclose(fp);
            cores++;
            continue;
        }
        // αν δεν υπάρχει online, δοκίμασε αν υπάρχει ο φάκελος cpufreq
        snprintf(path, sizeof(path), "/sys/devices/system/cpu/cpu%d/cpufreq", i);
        fp = fopen(path, "r");
        if (fp) {
            fclose(fp);
            cores++;
            continue;
        }

        // αν δεν βρίσκουμε τίποτα, σταμάτα στο πρώτο συνεχόμενο κενό
        if (cores == i) {
            break;
        }
    }
    if (cores <= 0) cores = 1;
    return cores;
}

static int read_cpu_freq_engine(int *outPercent) {
    int cores = detect_cores();
    if (cores <= 0) return -1;

    long totalPercent = 0;
    int validCores = 0;

    for (int i = 0; i < cores; ++i) {
        char pathCur[160];
        char pathMax[160];

        snprintf(pathCur, sizeof(pathCur),
                 "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
        snprintf(pathMax, sizeof(pathMax),
                 "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i);

        long cur = read_long_file(pathCur);
        long max = read_long_file(pathMax);

        if (cur <= 0 || max <= 0) {
            continue;
        }

        long p = (cur * 100) / max;
        p = clamp_long(p, 0, 100);

        totalPercent += p;
        validCores++;
    }

    if (validCores <= 0) {
        return -1;
    }

    long avg = totalPercent / validCores;
    avg = clamp_long(avg, 0, 100);
    *outPercent = (int)avg;
    return 0;
}

// ======================================================
// 3) /sys/class/thermal hybrid engine
// ======================================================
//
// Προσέγγιση: βρίσκουμε thermal_zoneX με type που περιέχει
// "cpu" ή "soc" ή "ap". Αν βρούμε, παίρνουμε temp (milli-°C)
// και το χαρτογραφούμε σε 0–100% περίπου:
//   30°C -> 0%
//   90°C -> 100%  (clamped)
//
static int read_cpu_thermal_engine(int *outPercent) {
    char typePath[128];
    char tempPath[128];
    char buf[128];
    int found = 0;
    long tempMilli = -1;

    for (int i = 0; i < 32; ++i) {
        snprintf(typePath, sizeof(typePath),
                 "/sys/class/thermal/thermal_zone%d/type", i);
        if (read_line(typePath, buf, sizeof(buf)) != 0) {
            continue;
        }
        // strip newline
        size_t len = strlen(buf);
        if (len > 0 && (buf[len - 1] == '\n' || buf[len - 1] == '\r')) {
            buf[len - 1] = '\0';
        }

        // ψάχνουμε cpu / soc / ap (case-insensitive-ish)
        if (strstr(buf, "cpu") == NULL &&
            strstr(buf, "CPU") == NULL &&
            strstr(buf, "soc") == NULL &&
            strstr(buf, "SOC") == NULL &&
            strstr(buf, "ap")  == NULL &&
            strstr(buf, "AP")  == NULL) {
            continue;
        }

        snprintf(tempPath, sizeof(tempPath),
                 "/sys/class/thermal/thermal_zone%d/temp", i);
        tempMilli = read_long_file(tempPath);
        if (tempMilli <= 0) {
            continue;
        }

        found = 1;
        break;
    }

    if (!found || tempMilli <= 0) {
        return -1;
    }

    // πολλές συσκευές δίνουν milli°C. Αν είναι πολύ μικρό, θεωρούμε ότι είναι ήδη °C
    double tempC;
    if (tempMilli > 1000) {
        tempC = tempMilli / 1000.0;
    } else {
        tempC = (double)tempMilli;
    }

    // απλό linear mapping: 30°C -> 0%, 90°C -> 100%
    double minC = 30.0;
    double maxC = 90.0;
    double pct;

    if (tempC <= minC) {
        pct = 0.0;
    } else if (tempC >= maxC) {
        pct = 100.0;
    } else {
        pct = (tempC - minC) * 100.0 / (maxC - minC);
    }

    int result = (int)(pct + 0.5);
    result = clamp_int(result, 0, 100);
    *outPercent = result;
    return 0;
}

// ======================================================
// JNI entry point — Triple Engine with fallback chain
// ======================================================
JNIEXPORT jint JNICALL
Java_com_gel_cleaner_CpuRamLiveActivity_getCpuUsageNative(JNIEnv *env, jobject thiz) {
    int percent = -1;

    // 1) RAW /proc/stat
    if (read_cpu_procstat_raw(&percent) == 0) {
        return clamp_int(percent, 0, 100);
    }

    // 2) FREQ engine
    if (read_cpu_freq_engine(&percent) == 0) {
        return clamp_int(percent, 0, 100);
    }

    // 3) THERMAL hybrid (last resort)
    if (read_cpu_thermal_engine(&percent) == 0) {
        return clamp_int(percent, 0, 100);
    }

    // όλα απέτυχαν
    return -1;
}
