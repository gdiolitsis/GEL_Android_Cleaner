// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleSpecProvider.java — Apple Hardcoded Bridge (Reflection Safe)
// NOTE: Copy-paste whole file.

package com.gel.cleaner.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public final class AppleSpecProvider {

    private AppleSpecProvider() {}

    // =========================================================
    // SELECTION
    // =========================================================
    public static final class Selection {
        public final String type;   // "iphone" | "ipad"
        public final String model;  // "iPhone 13" etc

        public Selection(String type, String model) {
            this.type = type;
            this.model = model;
        }
    }

    // ------------------------------------------------------------
    // READ SAVED SELECTION (multi-key fallback)
    // ------------------------------------------------------------
    public static Selection getSavedSelection(Context c) {

        SharedPreferences p = c.getSharedPreferences("gel_prefs", Context.MODE_PRIVATE);

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

        type = type.toLowerCase(Locale.US).contains("ipad") ? "ipad" : "iphone";

        return new Selection(type, model);
    }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String v : vals) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }

    // ------------------------------------------------------------
    // GET AppleDeviceSpec from AppleSpecs (via reflection)
    // ------------------------------------------------------------
    public static Object getSpecOrNull(Context c) {
        try {
            Selection s = getSavedSelection(c);

            Class<?> cls = Class.forName("com.gel.cleaner.iphone.AppleSpecs");

            Method m = null;

            try { m = cls.getDeclaredMethod("get", String.class, String.class); }
            catch (Throwable ignore) {}

            if (m == null) {
                try { m = cls.getDeclaredMethod("getSpec", String.class, String.class); }
                catch (Throwable ignore) {}
            }

            if (m == null) {
                try { m = cls.getDeclaredMethod("find", String.class, String.class); }
                catch (Throwable ignore) {}
            }

            if (m == null) return null;

            m.setAccessible(true);
            return m.invoke(null, s.type, s.model);

        } catch (Throwable ignore) {
            return null;
        }
    }

    // =========================================================
    // 🔥 DIRECT BRIDGE FOR ACTIVITIES
    // =========================================================
    public static com.gel.cleaner.iphone.AppleDeviceSpec getSelectedDevice(Context ctx) {
        try {
            Object spec = getSpecOrNull(ctx);

            if (spec instanceof com.gel.cleaner.iphone.AppleDeviceSpec) {
                return (com.gel.cleaner.iphone.AppleDeviceSpec) spec;
            }
        } catch (Throwable ignore) {}

        // ποτέ null → δεν σκάει UI
        return com.gel.cleaner.iphone.AppleDeviceSpec.unknown();
    }

    // =========================================================
    // SAFE FIELD GETTERS
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
                if (v instanceof Number) return ((Number) v).intValue();
                if (v != null) {
                    String s = String.valueOf(v).replaceAll("[^0-9]", "");
                    if (!s.isEmpty()) return Integer.parseInt(s);
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
                if (v instanceof Number) return ((Number) v).doubleValue();
                if (v != null) {
                    String s = String.valueOf(v)
                            .replace(',', '.')
                            .replaceAll("[^0-9.]", "");
                    if (!s.isEmpty()) return Double.parseDouble(s);
                }
            } catch (Throwable ignore) {}
        }
        return -1;
    }
}
