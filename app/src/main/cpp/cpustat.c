// GDiolitsis Engine Lab (GEL) — Author & Developer
// cpustat.c — Native RAW /proc/stat CPU% reader for CpuRamLiveActivity (v13.0)

#include <jni.h>
#include <stdio.h>
#include <string.h>

static long lastIdle = -1;
static long lastTotal = -1;

JNIEXPORT jint JNICALL
Java_com_gel_cleaner_CpuRamLiveActivity_getCpuUsageNative(JNIEnv *env, jobject thiz) {
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

    char cpuLabel[5];
    long user, nice, system, idle, iowait, irq, softirq;
    int scanned = sscanf(
        line,
        "%4s %ld %ld %ld %ld %ld %ld %ld",
        cpuLabel,
        &user, &nice, &system, &idle, &iowait, &irq, &softirq
    );

    if (scanned < 5) {
        return -1;
    }

    long idleAll = idle + iowait;
    long total   = user + nice + system + idle + iowait + irq + softirq;

    if (lastIdle < 0 || lastTotal < 0) {
        lastIdle  = idleAll;
        lastTotal = total;
        return 0;
    }

    long diffIdle  = idleAll - lastIdle;
    long diffTotal = total   - lastTotal;

    lastIdle  = idleAll;
    lastTotal = total;

    if (diffTotal <= 0) return 0;

    long used  = diffTotal - diffIdle;
    long usage = used * 100 / diffTotal;

    if (usage < 0)  usage = 0;
    if (usage > 100) usage = 100;

    return (jint) usage;
}
