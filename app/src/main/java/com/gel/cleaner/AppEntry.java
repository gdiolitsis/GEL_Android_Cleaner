package com.gel.cleaner;

public class AppEntry {

    public String name;
    public String pkg;
    public long size;

    public AppEntry(String name, String pkg, long size) {
        this.name = name;
        this.pkg = pkg;
        this.size = size;
    }
}
