// GDiolitsis Engine Lab (GEL) — Author & Developer
// AppleDeviceInfoInternalActivity.java — APPLE INTERNAL PRO v1.0 (CARBON UI)
// ============================================================
// NOTE: Πάντα δίνω ΟΛΟΚΛΗΡΟ το αρχείο έτοιμο για copy-paste (χωρίς μπλα-μπλα / χωρίς ερωτήσεις)

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import android.graphics.drawable.ColorDrawable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AppleDeviceInfoInternalActivity extends GELAutoActivityHook
        implements GELFoldableCallback {

    private static final String NEON_GREEN = "#39FF14";

    private GELFoldableDetector foldDetector;
    private GELFoldableUIManager foldUI;

    private TextView[] allContents;
    private TextView[] allIcons;

    // ============================================================
    // PREFS (SAME AS MainActivity)
    // ============================================================
    private static final String PREFS = "gel_prefs";
    private static final String KEY_PLATFORM = "platform_mode"; // android | apple
    private static final String KEY_APPLE_TYPE  = "apple_device_type";   // iphone | ipad
    private static final String KEY_APPLE_MODEL = "apple_device_model";  // e.g. "iPhone 15"

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        foldUI = new GELFoldableUIManager(this);
        foldDetector = new GELFoldableDetector(this, this);

        // Title
        TextView title = findViewById(R.id.txtTitleDevice);
        if (title != null) {
            // κρατάμε ίδιο string, αλλά Apple context
            title.setText("🍎 Apple — Internal Info");
        }

        // CONTENT (SAME IDs / SAME XML)
        TextView txtSystemContent       = findViewById(R.id.txtSystemContent);
        TextView txtAndroidContent      = findViewById(R.id.txtAndroidContent);
        TextView txtCpuContent          = findViewById(R.id.txtCpuContent);
        TextView txtGpuContent          = findViewById(R.id.txtGpuContent);
        TextView txtThermalContent      = findViewById(R.id.txtThermalContent);
        TextView txtVulkanContent       = findViewById(R.id.txtVulkanContent);
        TextView txtRamContent          = findViewById(R.id.txtRamContent);
        TextView txtStorageContent      = findViewById(R.id.txtStorageContent);
        TextView txtConnectivityContent = findViewById(R.id.txtConnectivityContent);

        // ICONS (SAME IDs / SAME XML)
        TextView iconSystem       = findViewById(R.id.iconSystemToggle);
        TextView iconAndroid      = findViewById(R.id.iconAndroidToggle);
        TextView iconCpu          = findViewById(R.id.iconCpuToggle);
        TextView iconGpu          = findViewById(R.id.iconGpuToggle);
        TextView iconThermal      = findViewById(R.id.iconThermalToggle);
        TextView iconVulkan       = findViewById(R.id.iconVulkanToggle);
        TextView iconRam          = findViewById(R.id.iconRamToggle);
        TextView iconStorage      = findViewById(R.id.iconStorageToggle);
        TextView iconConnectivity = findViewById(R.id.iconConnectivityToggle);

        allContents = new TextView[]{
                txtSystemContent, txtAndroidContent, txtCpuContent, txtGpuContent,
                txtThermalContent, txtVulkanContent, txtRamContent,
                txtStorageContent, txtConnectivityContent
        };

        allIcons = new TextView[]{
                iconSystem, iconAndroid, iconCpu, iconGpu, iconThermal,
                iconVulkan, iconRam, iconStorage, iconConnectivity
        };

        // Default closed
        for (TextView c : allContents) if (c != null) c.setVisibility(View.GONE);
        for (TextView i : allIcons) if (i != null) i.setText("＋");

        // Load selected Apple device spec (hardcoded registry)
        Object spec = getSelectedAppleSpec();

        // Build content (NEON values same engine)
        if (txtSystemContent != null)
            setNeonSectionText(txtSystemContent, buildAppleSystemInfo(spec));
        if (txtAndroidContent != null)
            setNeonSectionText(txtAndroidContent, buildAppleOsInfo(spec));
        if (txtCpuContent != null)
            setNeonSectionText(txtCpuContent, buildAppleCpuInfo(spec));
        if (txtGpuContent != null)
            setNeonSectionText(txtGpuContent, buildAppleGpuInfo(spec));
        if (txtThermalContent != null)
            setNeonSectionText(txtThermalContent, buildAppleThermalInfo(spec));
        if (txtVulkanContent != null)
            setNeonSectionText(txtVulkanContent, buildAppleMetalInfo(spec));
        if (txtRamContent != null)
            setNeonSectionText(txtRamContent, buildAppleRamInfo(spec));
        if (txtStorageContent != null)
            setNeonSectionText(txtStorageContent, buildAppleStorageInfo(spec));
        if (txtConnectivityContent != null)
            setNeonSectionText(txtConnectivityContent, buildAppleConnectivityInfo(spec));

        // Expanders (SAME headers / SAME behavior)
        setupSection(findViewById(R.id.headerSystem), txtSystemContent, iconSystem);
        setupSection(findViewById(R.id.headerAndroid), txtAndroidContent, iconAndroid);
        setupSection(findViewById(R.id.headerCpu), txtCpuContent, iconCpu);
        setupSection(findViewById(R.id.headerGpu), txtGpuContent, iconGpu);
        setupSection(findViewById(R.id.headerThermal), txtThermalContent, iconThermal);
        setupSection(findViewById(R.id.headerVulkan), txtVulkanContent, iconVulkan);
        setupSection(findViewById(R.id.headerRam), txtRamContent, iconRam);
        setupSection(findViewById(R.id.headerStorage), txtStorageContent, iconStorage);
        setupSection(findViewById(R.id.headerConnectivity), txtConnectivityContent, iconConnectivity);
    }

    @Override
    public void onPostureChanged(@NonNull Posture posture) {}

    @Override
    public void onScreenChanged(boolean isInner) {
        if (foldUI != null) foldUI.applyUI(isInner);
    }

    // ============================================================
    // EXPANDER LOGIC WITH ANIMATION (CARBON COPY)
    // ============================================================

    private void setupSection(View header, final TextView content, final TextView icon) {
        if (header == null || content == null || icon == null) return;
        header.setOnClickListener(v -> toggleSection(content, icon));
    }

    private void toggleSection(TextView targetContent, TextView targetIcon) {

        // Close all other sections
        for (int i = 0; i < allContents.length; i++) {
            TextView c = allContents[i];
            TextView ic = allIcons[i];

            if (c == null || ic == null) continue;

            if (c != targetContent && c.getVisibility() == View.VISIBLE) {
                animateCollapse(c);
                ic.setText("＋");
            }
        }

        // Toggle only selected section
        if (targetContent.getVisibility() == View.VISIBLE) {
            animateCollapse(targetContent);
            targetIcon.setText("＋");
        } else {
            animateExpand(targetContent);
            targetIcon.setText("−");
        }
    }

    private void animateExpand(final View v) {
        v.post(() -> {
            v.measure(
                    View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            final int target = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);

            v.animate()
                    .alpha(1f)
                    .setDuration(160)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        v.getLayoutParams().height = target;
                        v.requestLayout();
                    })
                    .start();
        });
    }

    private void animateCollapse(final View v) {
        if (v.getVisibility() != View.VISIBLE) return;

        final int initial = v.getHeight();
        v.setAlpha(1f);

        v.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    v.setVisibility(View.GONE);
                    v.getLayoutParams().height = initial;
                    v.setAlpha(1f);
                    v.requestLayout();
                })
                .start();
    }

    // ============================================================
    // NEON VALUE COLOR ENGINE (CARBON COPY)
    // ============================================================

    private void setNeonSectionText(TextView tv, String text) {
        if (tv == null) return;
        if (text == null) text = "";
        tv.setText(applyNeonToValues(text));
    }

    private CharSequence applyNeonToValues(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        String[] lines = text.split("\n", -1);
        int offset = 0;
        boolean previousLabelOnly = false;

        for (String line : lines) {
            int len = line.length();
            if (len > 0) {
                int colonIdx = line.indexOf(':');
                if (colonIdx >= 0) {
                    if (colonIdx == len - 1) {
                        previousLabelOnly = true;
                    } else {
                        int valueStart = offset + colonIdx + 1;
                        while (valueStart < offset + len &&
                                Character.isWhitespace(line.charAt(valueStart - offset))) {
                            valueStart++;
                        }
                        int valueEnd = offset + len;
                        if (valueStart < valueEnd) {
                            ssb.setSpan(
                                    new ForegroundColorSpan(Color.parseColor(NEON_GREEN)),
                                    valueStart,
                                    valueEnd,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                        previousLabelOnly = false;
                    }
                } else if (previousLabelOnly) {
                    int valueStart = offset;
                    int valueEnd = offset + len;
                    ssb.setSpan(
                            new ForegroundColorSpan(Color.parseColor(NEON_GREEN)),
                            valueStart,
                            valueEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    previousLabelOnly = false;
                } else {
                    previousLabelOnly = false;
                }
            } else {
                previousLabelOnly = false;
            }

            offset += len + 1;
        }

        return ssb;
    }

    // ============================================================
    // APPLE SPEC LOADER (HARD CODED REGISTRY)
    // ============================================================

    private Object getSelectedAppleSpec() {

        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);

        String platform = p.getString(KEY_PLATFORM, "android");
        if (!"apple".equals(platform)) {
            return null;
        }

        String type  = p.getString(KEY_APPLE_TYPE, null);
        String model = p.getString(KEY_APPLE_MODEL, null);

        if (type == null || model == null || model.trim().isEmpty()) return null;

        // Resolve via registry (reflection-safe)
        try {
            Class<?> reg = Class.forName("com.gel.cleaner.iphone.AppleModelRegistry");

            // try common method names
            Object spec;

            spec = tryInvoke(reg, "getSpec", new Class[]{String.class, String.class}, new Object[]{type, model});
            if (spec != null) return spec;

            spec = tryInvoke(reg, "get", new Class[]{String.class, String.class}, new Object[]{type, model});
            if (spec != null) return spec;

            spec = tryInvoke(reg, "resolve", new Class[]{String.class, String.class}, new Object[]{type, model});
            if (spec != null) return spec;

            spec = tryInvoke(reg, "find", new Class[]{String.class, String.class}, new Object[]{type, model});
            if (spec != null) return spec;

            // fallback: single arg model
            spec = tryInvoke(reg, "getSpec", new Class[]{String.class}, new Object[]{model});
            if (spec != null) return spec;

            spec = tryInvoke(reg, "get", new Class[]{String.class}, new Object[]{model});
            if (spec != null) return spec;

        } catch (Throwable ignore) {}

        return null;
    }

    private Object tryInvoke(Class<?> cls, String name, Class<?>[] sig, Object[] args) {
        try {
            Method m = cls.getDeclaredMethod(name, sig);
            m.setAccessible(true);
            return m.invoke(null, args);
        } catch (Throwable ignore) { return null; }
    }

    // ============================================================
    // SAFE FIELD READERS (NO COMPILE DEPENDENCY ON FIELD NAMES)
    // ============================================================

    private String f(Object o, String... names) {
        if (o == null) return "";
        for (String n : names) {
            try {
                Field ff = o.getClass().getDeclaredField(n);
                ff.setAccessible(true);
                Object v = ff.get(o);
                if (v != null) {
                    String s = String.valueOf(v).trim();
                    if (!s.isEmpty()) return s;
                }
            } catch (Throwable ignore) {}
        }
        return "";
    }

    private String orDash(String s) {
        if (s == null) return "—";
        s = s.trim();
        return s.isEmpty() ? "—" : s;
    }

    // ============================================================
    // SECTION BUILDERS — APPLE (HARD CODED FROM SPEC)
    // ============================================================

    private String buildAppleSystemInfo(Object d) {

        if (d == null) {
            return "Device Selected : NONE\n\n" +
                   "Tip : Select an Apple model first (Device Declaration)\n";
        }

        String type  = f(d, "type", "deviceType");
        String model = f(d, "model", "modelName", "name");
        String year  = f(d, "releaseYear", "year", "released");
        String board = f(d, "board", "logicBoard");
        String hw    = f(d, "hardware", "hw");
        String arch  = f(d, "arch", "cpuArch");
        String chip  = f(d, "chip", "soc", "processor");

        StringBuilder sb = new StringBuilder();
        sb.append("Manufacturer : Apple\n");
        sb.append("Device Type  : ").append(orDash(type)).append("\n");
        sb.append("Model        : ").append(orDash(model)).append("\n");
        sb.append("Release Year : ").append(orDash(year)).append("\n");
        sb.append("Chip / SoC   : ").append(orDash(chip)).append("\n");
        sb.append("Arch         : ").append(orDash(arch)).append("\n");
        sb.append("Board        : ").append(orDash(board)).append("\n");
        sb.append("Hardware     : ").append(orDash(hw)).append("\n");

        return sb.toString();
    }

    private String buildAppleOsInfo(Object d) {

        if (d == null) return "OS : —\nBuild : —\n";

        String os   = f(d, "os", "osName", "platform");
        String ver  = f(d, "osVersion", "iosVersion", "version");
        String build= f(d, "build", "osBuild");

        if (os.trim().isEmpty()) os = "iOS / iPadOS";

        StringBuilder sb = new StringBuilder();
        sb.append("OS Name      : ").append(orDash(os)).append("\n");
        sb.append("OS Version   : ").append(orDash(ver)).append("\n");
        sb.append("OS Build     : ").append(orDash(build)).append("\n");

        String kernel = f(d, "kernel", "xnuKernel");
        if (!kernel.trim().isEmpty())
            sb.append("Kernel       : ").append(kernel).append("\n");

        return sb.toString();
    }

    private String buildAppleCpuInfo(Object d) {

        if (d == null) return "Chip : —\nCPU Cores : —\n";

        String chip  = f(d, "chip", "soc", "processor");
        String cores = f(d, "cpuCores", "cores");
        String perf  = f(d, "performanceCores", "pCores");
        String eff   = f(d, "efficiencyCores", "eCores");
        String freq  = f(d, "cpuMaxGHz", "cpuGHz", "cpuFreq");

        StringBuilder sb = new StringBuilder();
        sb.append("Chip         : ").append(orDash(chip)).append("\n");
        sb.append("CPU Cores    : ").append(orDash(cores)).append("\n");

        if (!perf.trim().isEmpty() || !eff.trim().isEmpty())
            sb.append("P / E Cores  : ").append(orDash(perf)).append(" / ").append(orDash(eff)).append("\n");

        if (!freq.trim().isEmpty())
            sb.append("Max Clock    : ").append(freq).append("\n");

        return sb.toString();
    }

    private String buildAppleGpuInfo(Object d) {

        if (d == null) return "GPU : —\n";

        String gpu = f(d, "gpu", "gpuCores", "graphics");
        String gcores = f(d, "gpuCores", "gpuCoreCount");

        StringBuilder sb = new StringBuilder();
        sb.append("GPU          : ").append(orDash(gpu)).append("\n");
        if (!gcores.trim().isEmpty())
            sb.append("GPU Cores    : ").append(gcores).append("\n");

        return sb.toString();
    }

    private String buildAppleThermalInfo(Object d) {

        // Apple hardcoded: we keep section but show spec / notes
        if (d == null) return "Thermal Design : —\n";

        String thermal = f(d, "thermal", "thermalDesign", "cooling");
        String notes   = f(d, "thermalNotes", "notesThermal");

        StringBuilder sb = new StringBuilder();
        sb.append("Thermal Design : ").append(orDash(thermal)).append("\n");
        if (!notes.trim().isEmpty())
            sb.append("\nNotes:\n").append(notes).append("\n");

        return sb.toString();
    }

    private String buildAppleMetalInfo(Object d) {

        // Vulkan section in XML -> we use it for Metal (Apple)
        if (d == null) return "Graphics API : Metal\n";

        String metal = f(d, "metal", "metalSupport", "graphicsApi");
        String api   = (metal.trim().isEmpty()) ? "Metal" : metal;

        StringBuilder sb = new StringBuilder();
        sb.append("Graphics API : ").append(orDash(api)).append("\n");

        String ver = f(d, "metalVersion", "apiVersion");
        if (!ver.trim().isEmpty())
            sb.append("API Version  : ").append(ver).append("\n");

        return sb.toString();
    }

    private String buildAppleRamInfo(Object d) {

        if (d == null) return "RAM : —\n";

        String ram = f(d, "ram", "memory", "memoryGb", "ramGb");
        String type= f(d, "ramType", "memoryType");

        StringBuilder sb = new StringBuilder();
        sb.append("RAM          : ").append(orDash(ram)).append("\n");
        if (!type.trim().isEmpty())
            sb.append("RAM Type     : ").append(type).append("\n");

        return sb.toString();
    }

    private String buildAppleStorageInfo(Object d) {

        if (d == null) return "Storage Options : —\n";

        String storage = f(d, "storage", "storageOptions", "storageGb", "nand");
        String base    = f(d, "baseStorage", "baseGb");

        StringBuilder sb = new StringBuilder();
        sb.append("Storage Options : ").append(orDash(storage)).append("\n");
        if (!base.trim().isEmpty())
            sb.append("Base Storage    : ").append(base).append("\n");

        return sb.toString();
    }

    private String buildAppleConnectivityInfo(Object d) {

        if (d == null) return "Wi-Fi : —\nBluetooth : —\nModem : —\n";

        String wifi  = f(d, "wifi", "wiFi");
        String bt    = f(d, "bluetooth", "bt");
        String modem = f(d, "modem", "cellular");
        String nfc   = f(d, "nfc");
        String gps   = f(d, "gps", "gnss");

        StringBuilder sb = new StringBuilder();
        sb.append("Wi-Fi       : ").append(orDash(wifi)).append("\n");
        sb.append("Bluetooth   : ").append(orDash(bt)).append("\n");
        sb.append("Modem       : ").append(orDash(modem)).append("\n");
        sb.append("NFC         : ").append(orDash(nfc)).append("\n");
        sb.append("GPS / GNSS  : ").append(orDash(gps)).append("\n");

        return sb.toString();
    }

    // ============================================================
    // DIMEN (small helper if you need it later)
    // ============================================================
    private int dp(float v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                v,
                getResources().getDisplayMetrics()
        );
    }
}
