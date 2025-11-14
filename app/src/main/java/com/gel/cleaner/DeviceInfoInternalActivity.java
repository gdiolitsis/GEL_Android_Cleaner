package com.gel.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DeviceInfoInternalActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_internal);

        TextView title = findViewById(R.id.txtTitleDevice);
        TextView info  = findViewById(R.id.txtDeviceInfo);

        if (title != null) {
            title.setText(getString(R.string.phone_info_internal));
        }

        StringBuilder s = new StringBuilder();

        // SYSTEM
        s.append("── SYSTEM ──\n");
        s.append("Brand: ").append(Build.BRAND).append("\n");
        s.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        s.append("Model: ").append(Build.MODEL).append("\n");
        s.append("Device: ").append(Build.DEVICE).append("\n");
        s.append("Product: ").append(Build.PRODUCT).append("\n");
        s.append("Board: ").append(Build.BOARD).append("\n");
        s.append("Hardware: ").append(Build.HARDWARE).append("\n\n");

        // ANDROID
        s.append("── ANDROID ──\n");
        s.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        s.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            s.append("Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n");
        }
        s.append("Build ID: ").append(Build.ID).append("\n");
        s.append("Build Type: ").append(Build.TYPE).append("\n");
        s.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");
        s.append("Kernel: ").append(System.getProperty("os.version")).append("\n\n");

        // CPU
        s.append("── CPU ──\n");
        String[] abis = Build.SUPPORTED_ABIS;
        if (abis != null && abis.length > 0) {
            s.append("Primary ABI: ").append(abis[0]).append("\n");
        }
        s.append("CPU ABIs: ");
        if (abis != null) {
            for (int i = 0; i < abis.length; i++) {
                s.append(abis[i]);
                if (i < abis.length - 1) s.append(", ");
            }
        }
        s.append("\n\n");

        // RAM
        s.append("── RAM ──\n");
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            long totalMb = mi.totalMem / (1024L * 1024L);
            long availMb = mi.availMem / (1024L * 1024L);
            long usedMb  = totalMb - availMb;

            s.append("Total RAM: ").append(totalMb).append(" MB\n");
            s.append("Used RAM: ").append(usedMb).append(" MB\n");
            s.append("Free RAM: ").append(availMb).append(" MB\n");
            s.append("Low RAM device: ").append(mi.lowMemory ? "YES" : "NO").append("\n\n");
        }

        // INTERNAL STORAGE
        s.append("── INTERNAL STORAGE ──\n");
        try {
            File dataDir = Environment.getDataDirectory();
            long totalBytes = dataDir.getTotalSpace();
            long freeBytes  = dataDir.getFreeSpace();
            long totalGb = totalBytes / (1024L * 1024L * 1024L);
            long freeGb  = freeBytes / (1024L * 1024L * 1024L);
            long usedGb  = totalGb - freeGb;

            s.append("Total: ").append(totalGb).append(" GB\n");
            s.append("Used: ").append(usedGb).append(" GB\n");
            s.append("Free: ").append(freeGb).append(" GB\n\n");
        } catch (Throwable t) {
            s.append("Storage info: N/A (").append(t.getMessage()).append(")\n\n");
        }

        // SCREEN
        s.append("── SCREEN ──\n");
        DisplayMetrics dm = getResources().getDisplayMetrics();
        s.append("Resolution: ").append(dm.widthPixels)
                .append(" x ").append(dm.heightPixels).append(" px\n");
        s.append("Density: ").append(dm.densityDpi).append(" dpi\n");
        s.append("Scaled density: ").append(dm.scaledDensity).append("\n");

        if (info != null) {
            info.setText(s.toString());
        }
    }
}
