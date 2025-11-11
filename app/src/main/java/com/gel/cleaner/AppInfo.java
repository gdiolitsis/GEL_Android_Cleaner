package com.gel.cleaner;

import android.content.pm.ResolveInfo;

public class AppInfo {
    public String packageName;
    public ResolveInfo resolveInfo;

    public AppInfo(String packageName, ResolveInfo resolveInfo) {
        this.packageName = packageName;
        this.resolveInfo = resolveInfo;
    }
}
