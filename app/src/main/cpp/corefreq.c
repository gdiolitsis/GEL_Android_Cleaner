// GDiolitsis Engine Lab (GEL) — Author & Developer
// corefreq.c — v1.1 HTML <br> Version (Correct Line Breaks for Android)

#include <jni.h>
#include <stdio.h>
#include <string.h>

static long read_long(const char *path) {
    char buf[64];
    FILE *fp = fopen(path, "r");
    if (!fp) return -1;
    if (!fgets(buf, sizeof(buf), fp)) {
        fclose(fp);
        return -1;
    }
    fclose(fp);

    long v = -1;
    sscanf(buf, "%ld", &v);
    return v;
}

JNIEXPORT jstring JNICALL
Java_com_gel_cleaner_CoreMonitorActivity_getCoreInfoNative(JNIEnv *env, jobject thiz) {

    char out[4096];
    out[0] = 0;

    // Title + blank line
    strcat(out, "GEL Core Monitor<br><br>");

    for (int i = 0; i < 16; i++) {

        char pCur[128], pMax[128];
        snprintf(pCur, sizeof(pCur),
                 "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
        snprintf(pMax, sizeof(pMax),
                 "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i);

        long cur = read_long(pCur);
        long max = read_long(pMax);

        if (max <= 0) {
            // No more CPUs — exit cleanly
            break;
        }

        char line[256];

        if (cur <= 0) {
            snprintf(line, sizeof(line),
                     "C%d: 0 MHz   [OFFLINE]<br>", i);
        } else {
            int mhz = (int)(cur / 1000);
            int pct = (int)((cur * 100) / max);

            const char *state = "OK";

            if (pct < 5)
                state = "SUSPECT";
            if (pct > 80)
                state = "BOOST";
            if (cur == max)
                state = "MAX";
            if (cur <= 0)
                state = "OFFLINE";

            snprintf(line, sizeof(line),
                     "C%d: %d MHz   [%s]<br>", i, mhz, state);
        }

        strcat(out, line);
    }

    return (*env)->NewStringUTF(env, out);
}
