package com.gel.cleaner;

import android.os.Bundle;
import android.widget.TextView;

import com.gel.cleaner.base.AppleUiBinder;
import com.gel.cleaner.iphone.AppleDeviceSpec;
import com.gel.cleaner.iphone.AppleSpecProvider;

public class AppleDeviceInfoPeripheralsActivity extends DeviceInfoPeripheralsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView out = findViewById(R.id.txtLogs);

        AppleDeviceSpec spec = AppleSpecProvider.getSelectedDevice(this);

        AppleUiBinder.bindPeripherals(out, spec);
    }
}
