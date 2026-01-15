package com.gel.cleaner;

import android.os.Bundle;
import android.widget.TextView;

import com.gel.cleaner.base.AppleUiBinder;
import com.gel.cleaner.iphone.AppleDeviceSpec;
import com.gel.cleaner.iphone.AppleSpecProvider;

public class AppleDeviceInfoInternalActivity extends DeviceInfoInternalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Παίρνουμε το TextView που ήδη χρησιμοποιεί το Android activity
        TextView out = findViewById(R.id.txtLogs);

        AppleDeviceSpec spec = AppleSpecProvider.getSelectedDevice(this);

        AppleUiBinder.bindInternal(out, spec);
    }
}
