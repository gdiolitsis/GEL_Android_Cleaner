package com.gel.cleaner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DeviceInfoPeripheralsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info_peripherals);

        TextView title = findViewById(R.id.txtTitleDevice);
        TextView info  = findViewById(R.id.txtDeviceInfo);

        if (title != null) {
            title.setText(getString(R.string.phone_info_peripherals));
        }

        StringBuilder s = new StringBuilder();
        PackageManager pm = getPackageManager();

        // CAMERA
        s.append("── CAMERA ──\n");
        s.append("Any camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO")
                .append("\n");
        s.append("Flash: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ? "YES" : "NO")
                .append("\n");
        s.append("Front camera: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ? "YES" : "NO")
                .append("\n\n");

        // BIOMETRICS (χωρίς ευαίσθητα IDs)
        s.append("── BIOMETRICS ──\n");
        boolean hasFingerprint =
                pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                        || pm.hasSystemFeature("android.hardware.fingerprint");
        s.append("Fingerprint sensor: ").append(hasFingerprint ? "YES" : "NO").append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            boolean hasFace =
                    pm.hasSystemFeature(PackageManager.FEATURE_FACE)
                            || pm.hasSystemFeature("android.hardware.biometrics.face");
            s.append("Face unlock: ").append(hasFace ? "YES" : "NO").append("\n");
        }
        s.append("\n");

        // SENSORS
        s.append("── SENSORS ──\n");
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) {
            List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
            if (sensors != null && !sensors.isEmpty()) {
                for (Sensor sensor : sensors) {
                    s.append("• ").append(sensor.getName()).append("\n");
                }
            } else {
                s.append("No sensors reported\n");
            }
        } else {
            s.append("SensorManager not available\n");
        }
        s.append("\n");

        // CONNECTIVITY (χωρίς MAC, χωρίς IMEI)
        s.append("── CONNECTIVITY ──\n");
        s.append("NFC: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_NFC) ? "YES" : "NO")
                .append("\n");
        s.append("Bluetooth: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ? "YES" : "NO")
                .append("\n");
        s.append("BLE: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO")
                .append("\n");
        s.append("Wi-Fi: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_WIFI) ? "YES" : "NO")
                .append("\n");
        s.append("5G: ")
                .append(pm.hasSystemFeature("android.hardware.telephony.5g") ? "YES" : "NO")
                .append("\n\n");

        // LOCATION
        s.append("── LOCATION ──\n");
        s.append("GPS: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS) ? "YES" : "NO")
                .append("\n");
        s.append("Network location: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK) ? "YES" : "NO")
                .append("\n\n");

        // OTHER PERIPHERALS
        s.append("── OTHER ──\n");
        s.append("USB host: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? "YES" : "NO")
                .append("\n");
        s.append("USB accessory: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY) ? "YES" : "NO")
                .append("\n");
        s.append("Vibrator: ")
                .append(pm.hasSystemFeature(PackageManager.FEATURE_VIBRATOR) ? "YES" : "NO")
                .append("\n");

        if (info != null) {
            info.setText(s.toString());
        }
    }
}
