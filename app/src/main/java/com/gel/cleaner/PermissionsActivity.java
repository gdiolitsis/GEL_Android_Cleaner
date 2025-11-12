package com.gel.cleaner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PermissionsActivity extends Activity {

    TextView txtStatus;
    Button btnUsage, btnAccessibility, btnDone;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_permissions);

        txtStatus = findViewById(R.id.txtPermStatus);
        btnUsage = findViewById(R.id.btnUsageAccess);
        btnAccessibility = findViewById(R.id.btnAccessibility);
        btnDone = findViewById(R.id.btnPermDone);

        refreshStatus();

        btnUsage.setOnClickListener(v -> {
            PermissionHelper.requestUsageAccess(this);
        });

        btnAccessibility.setOnClickListener(v -> {
            PermissionHelper.requestAccessibility(this);
        });

        btnDone.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        boolean u = PermissionHelper.hasUsageAccess(this);
        boolean a = PermissionHelper.hasAccessibility(this);

        String t = "";
        t += "Usage Access: " + (u ? "✅" : "❌") + "\n";
        t += "Accessibility: " + (a ? "✅" : "❌");

        txtStatus.setText(t);
    }
}
