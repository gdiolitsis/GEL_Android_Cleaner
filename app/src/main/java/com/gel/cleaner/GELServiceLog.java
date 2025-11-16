package com.gel.cleaner;

import java.util.ArrayList;
import java.util.List;

public class GELServiceLog {

    private static final List<String> logs = new ArrayList<>();

    public static void add(String txt) {
        logs.add("ℹ️  " + txt);
    }

    public static void addOK(String txt) {
        logs.add("✅  " + txt);
    }

    public static void addWarn(String txt) {
        logs.add("⚠️  " + txt);
    }

    public static void addError(String txt) {
        logs.add("❌  " + txt);
    }

    public static List<String> getAll() {
        return new ArrayList<>(logs);
    }

    public static String getAsString() {
        StringBuilder sb = new StringBuilder();
        for (String s : logs) sb.append(s).append("\n");
        return sb.toString();
    }

    public static void clear() {
        logs.clear();
    }
}
