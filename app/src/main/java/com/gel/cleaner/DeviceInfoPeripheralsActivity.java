package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        TextView t = findViewById(R.id.txtDeviceInfo);

        StringBuilder s = new StringBuilder();

        s.append("🔌 **DEVICE PERIPHERALS INFO**\n\n");

        PackageManager pm = getPackageManager();

        // CAMERA
        s.append("📷 CAMERA\n");
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            s.append("• Camera: YES\n");
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
            s.append("• Flash: YES\n");
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))
            s.append("• Front Camera: YES\n\n");

        // SENSORS
        s.append("🧭 SENSORS\n");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensors) {
            s.append("• ").append(sensor.getName()).append("\n");
        }
        s.append("\n");

        // NFC / BLUETOOTH / WIFI
        s.append("📡 CONNECTIVITY\n");
        s.append("• NFC: ").append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO").append("\n");
        s.append("• Bluetooth: YES\n"); // ασφαλές, δεν δείχνει MAC
        s.append("• WiFi: YES\n");

        t.setText(s.toString());
    }
}
