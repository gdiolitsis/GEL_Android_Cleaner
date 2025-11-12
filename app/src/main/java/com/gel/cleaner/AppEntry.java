package com.gel.cleaner;

import androidx.annotation.NonNull;

public class AppEntry {

    @NonNull
    public String name;

    @NonNull
    public String pkg;

    public long size;

    public AppEntry(@NonNull String name, @NonNull String pkg, long size) {
        this.name = name;
        this.pkg = pkg;
        this.size = size;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " (" + pkg + ") — " + size + " bytes";
    }
}
