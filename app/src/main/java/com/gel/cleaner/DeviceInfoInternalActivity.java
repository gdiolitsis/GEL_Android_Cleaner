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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        TextView t = findViewById(R.id.txtDeviceInfo);

        StringBuilder s = new StringBuilder();

        s.append("📱 **DEVICE INTERNAL INFO**\n\n");

        // MODEL / BRAND
        s.append("• Brand: ").append(Build.BRAND).append("\n");
        s.append("• Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        s.append("• Model: ").append(Build.MODEL).append("\n");
        s.append("• Device: ").append(Build.DEVICE).append("\n");
        s.append("• Product: ").append(Build.PRODUCT).append("\n\n");

        // ANDROID
        s.append("• Android Version: ").append(Build.VERSION.RELEASE).append("\n");
        s.append("• API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        s.append("• Security Patch: ").append(Build.VERSION.SECURITY_PATCH).append("\n\n");

        // CPU
        s.append("🧠 CPU\n");
        s.append("• ABI: ").append(Build.SUPPORTED_ABIS[0]).append("\n");
        s.append("• Hardware: ").append(Build.HARDWARE).append("\n");
        s.append("• Board: ").append(Build.BOARD).append("\n\n");

        // RAM
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(mi);

        long totalRam = mi.totalMem / (1024 * 1024);
        s.append("💾 RAM\n");
        s.append("• Total RAM: ").append(totalRam).append(" MB\n\n");

        // STORAGE
        File path = Environment.getDataDirectory();
        long blkSize = path.getTotalSpace() / (1024 * 1024*1024);
        long freeSize = path.getFreeSpace() / (1024 * 1024*1024);

        s.append("💽 INTERNAL STORAGE\n");
        s.append("• Total: ").append(blkSize).append(" GB\n");
        s.append("• Free: ").append(freeSize).append(" GB\n\n");

        // SCREEN
        DisplayMetrics dm = getResources().getDisplayMetrics();
        s.append("📺 SCREEN\n");
        s.append("• Resolution: ").append(dm.widthPixels).append(" x ").append(dm.heightPixels).append("\n");
        s.append("• Density: ").append(dm.densityDpi).append(" dpi\n");

        t.setText(s.toString());
    }
}
