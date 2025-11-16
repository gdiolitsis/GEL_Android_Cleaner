package com.gel.cleaner;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServiceReportActivity extends AppCompatActivity {

    private TextView preview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        preview = new TextView(this);
        preview.setTextSize(14f);
        preview.setTextColor(0xFFFFFFFF);
        preview.setPadding(40, 40, 40, 200);

        scroll.addView(preview);
        setContentView(scroll);

        setTitle("📄 Service Report");

        // show preview
        preview.setText(GELServiceLog.getAsString());

        // add export buttons dynamically
        addExportButtons();
    }

    private void addExportButtons() {
        Button exportTxt = new Button(this);
        exportTxt.setText("💾 Export TXT");
        exportTxt.setOnClickListener(v -> exportToTXT());

        Button exportPdf = new Button(this);
        exportPdf.setText("📄 Export PDF");
        exportPdf.setOnClickListener(v -> exportToPDF());

        addContentView(exportTxt,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT, 160));
        addContentView(exportPdf,
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT, 160));
    }

    private File createOutputFile(String extension) {
        File dir = new File(Environment.getExternalStorageDirectory(),
                "Documents/GEL_ServiceReports");
        if (!dir.exists()) dir.mkdirs();

        String ts = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(new Date());
        return new File(dir, "GEL_Report_" + ts + "." + extension);
    }

    private void exportToTXT() {
        try {
            File f = createOutputFile("txt");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(GELServiceLog.getAsString().getBytes());
            fos.close();

            preview.append("\n\n✅ TXT saved:\n" + f.getAbsolutePath());

            // RESET LOGS FOR NEXT CUSTOMER
            GELServiceLog.clear();

        } catch (Exception e) {
            preview.append("\n\n❌ Error exporting TXT: " + e.getMessage());
        }
    }

    private void exportToPDF() {
        try {
            File f = createOutputFile("pdf");
            FileOutputStream fos = new FileOutputStream(f);

            String header = "GDiolitsis Engine Lab — Service Report\n\n";
            fos.write(header.getBytes());
            fos.write(GELServiceLog.getAsString().getBytes());
            fos.close();

            preview.append("\n\n✅ PDF saved:\n" + f.getAbsolutePath());

            // RESET LOGS FOR NEXT CUSTOMER
            GELServiceLog.clear();

        } catch (Exception e) {
            preview.append("\n\n❌ Error exporting PDF: " + e.getMessage());
        }
    }
}
