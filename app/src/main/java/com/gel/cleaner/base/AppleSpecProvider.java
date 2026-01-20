// GDiolitsis Engine Lab (GEL) — Author & Developer
// ============================================================
// AppleSpecProvider.java — Apple Hardcoded Bridge (Reflection Safe)
// FINAL • LOCKED • COPY-PASTE
// ============================================================

package com.gel.cleaner.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.gel.cleaner.iphone.AppleDeviceSpec;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public final class AppleSpecProvider {

    private AppleSpecProvider() {}

    // =========================================================
    // SUPPORTED MODELS — OFFICIAL GEL LIST (LOCKED)
    // =========================================================
    private static final String[] SUPPORTED_IPHONES = {
            "iPhone 15", "iPhone 15 Plus", "iPhone 15 Pro", "iPhone 15 Pro Max",
            "iPhone 14", "iPhone 14 Plus", "iPhone 14 Pro", "iPhone 14 Pro Max",
            "iPhone 13", "iPhone 13 mini", "iPhone 13 Pro", "iPhone 13 Pro Max",
            "iPhone 12", "iPhone 12 mini", "iPhone 12 Pro", "iPhone 12 Pro Max",
            "iPhone 11", "iPhone 11 Pro", "iPhone 11 Pro Max"
    };

    private static final String[] SUPPORTED_IPADS = {
            "iPad Pro M2 11", "iPad Pro M2 12.9",
            "iPad Pro M1",
            "iPad Air M2",
            "iPad Air M1",
            "iPad mini 6"
    };

    // =========================================================
    // SELECTION DTO
    // =========================================================
    public static final class Selection {
        public final String type;   // "iphone" | "ipad"
        public final String model;  // e.g. "iPhone 13"

        public Selection(String type, String model) {
            this.type = type;
            this.model = model;
        }
    }

    // =========================================================
    // READ SAVED SELECTION (MULTI-KEY + LEGACY SAFE)
    // =========================================================
    public static Selection getSavedSelection(Context c) {

        SharedPreferences p =
                c.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE);

        String type =
                firstNonEmpty(
                        p.getString("apple_type", null),
                        p.getString("apple_device_type", null),
                        p.getString("apple_selected_type", null),
                        p.getString("device_type", null),
                        p.getString("selected_device_type", null)
                );

        String model =
                firstNonEmpty(
                        p.getString("apple_model", null),
                        p.getString("apple_device_model", null),
                        p.getString("apple_selected_model", null),
                        p.getString("device_model", null),
                        p.getString("selected_device_model", null)
                );

        if (type == null) type = "iphone";
        if (model == null) model = "iPhone 13";

        type = type.toLowerCase(Locale.US).contains("ipad")
                ? "ipad"
                : "iphone";

        // 🔒 HARD FILTER — ONLY SUPPORTED MODELS
        if (!isSupported(type, model)) {
            type  = "iphone";
            model = "iPhone 13";
        }

        return new Selection(type, model);
    }

    // =========================================================
    // SUPPORT CHECK
    // =========================================================
    private static boolean isSupported(String type, String model) {
        if (model == null) return false;

        String[] list =
                "ipad".equals(type)
                        ? SUPPORTED_IPADS
                        : SUPPORTED_IPHONES;

        for (String s : list) {
            if (s.equalsIgnoreCase(model.trim())) {
                return true;
            }
        }
        return false;
    }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.trim().isEmpty())
                return v.trim();
        }
        return null;
    }

    // =========================================================
    // REFLECTION BRIDGE → AppleSpecs (LOCKED API)
    // =========================================================
    public static Object getSpecOrNull(Context c) {
        try {
            Selection s = getSavedSelection(c);

            Class<?> cls =
                    Class.forName("com.gel.cleaner.iphone.AppleSpecs");

            // 🔒 AppleSpecs exposes ONLY: get(String model)
            Method m = cls.getDeclaredMethod("get", String.class);
            m.setAccessible(true);

            return m.invoke(null, s.model);

        } catch (Throwable ignore) {
            return null;
        }
    }

    // =========================================================
    // DIRECT SAFE ACCESS FOR ACTIVITIES
    // =========================================================
    public static AppleDeviceSpec getSelectedDevice(Context ctx) {
        try {
            Object spec = getSpecOrNull(ctx);
            if (spec instanceof AppleDeviceSpec)
                return (AppleDeviceSpec) spec;
        } catch (Throwable ignore) {}

        return AppleDeviceSpec.unknown();
    }

    // =========================================================
    // SAFE FIELD ACCESSORS (NO CRASH)
    // =========================================================
    public static String getStr(Object spec, String... fieldNames) {
        if (spec == null) return null;
        for (String f : fieldNames) {
            try {
                Field ff = spec.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                Object v = ff.get(spec);
                if (v != null) {
                    String s = String.valueOf(v).trim();
                    if (!s.isEmpty()) return s;
                }
            } catch (Throwable ignore) {}
        }
        return null;
    }

    public static int getInt(Object spec, String... fieldNames) {
        if (spec == null) return -1;
        for (String f : fieldNames) {
            try {
                Field ff = spec.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                Object v = ff.get(spec);
                if (v instanceof Number)
                    return ((Number) v).intValue();
                if (v != null) {
                    String s =
                            String.valueOf(v).replaceAll("[^0-9]", "");
                    if (!s.isEmpty())
                        return Integer.parseInt(s);
                }
            } catch (Throwable ignore) {}
        }
        return -1;
    }

    public static double getDouble(Object spec, String... fieldNames) {
        if (spec == null) return -1;
        for (String f : fieldNames) {
            try {
                Field ff = spec.getClass().getDeclaredField(f);
                ff.setAccessible(true);
                Object v = ff.get(spec);
                if (v instanceof Number)
                    return ((Number) v).doubleValue();
                if (v != null) {
                    String s = String.valueOf(v)
                            .replace(',', '.')
                            .replaceAll("[^0-9.]", "");
                    if (!s.isEmpty())
                        return Double.parseDouble(s);
                }
            } catch (Throwable ignore) {}
        }
        return -1;
    }
}
