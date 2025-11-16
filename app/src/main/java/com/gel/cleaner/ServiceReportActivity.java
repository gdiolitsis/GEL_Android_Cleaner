package com.gel.cleaner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ============================================================
// ServiceReportActivity — GEL LAB OFFICIAL EDITION
// TXT + PDF Export with Multi-Page, Unicode Wrap & GEL Logo
// ============================================================
public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;
    private TextView txtPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);
        root.setBackgroundColor(0xFF101010);

        TextView title = new TextView(this);
        title.setText("📄 GEL Service Report");
        title.setTextSize(22f);
        title.setTextColor(0xFFFFD700);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText(
                "GDiolitsis Engine Lab (GEL) — Author & Developer\n" +
                "Τελικό Report για τον πελάτη (Auto + Manual)"
        );
        sub.setTextSize(13f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // PREVIEW
        txtPreview = new TextView(this);
        txtPreview.setTextSize(13f);
        txtPreview.setTextColor(0xFFEEEEEE);
        txtPreview.setMovementMethod(new ScrollingMovementMethod());
        txtPreview.setPadding(0, 0, 0, dp(12));
        txtPreview.setText(getPreviewText());
        root.addView(txtPreview);

        // BUTTON ROW
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER_HORIZONTAL);

        Button btnTxt = new Button(this);
        btnTxt.setText("💾 Export TXT");
        btnTxt.setAllCaps(false);
        btnTxt.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        btnTxt.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams lp1 =
                new LinearLayout.LayoutParams(0, dp(48), 1f);
        lp1.setMargins(0, dp(8), dp(4), dp(8));
        btnTxt.setLayoutParams(lp1);
        btnTxt.setOnClickListener(v -> exportWithCheck(false));

        Button btnPdf = new Button(this);
        btnPdf.setText("📄 Export PDF");
        btnPdf.setAllCaps(false);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        btnPdf.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams lp2 =
                new LinearLayout.LayoutParams(0, dp(48), 1f);
        lp2.setMargins(dp(4), dp(8), 0, dp(8));
        btnPdf.setLayoutParams(lp2);
        btnPdf.setOnClickListener(v -> exportWithCheck(true));

        btnRow.addView(btnTxt);
        btnRow.addView(btnPdf);
        root.addView(btnRow);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ------------------------------------------------------------
    // PERMISSION CHECK
    // ------------------------------------------------------------
    private void exportWithCheck(boolean pdf) {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, "Δεν υπάρχει Service Log για export.", Toast.LENGTH_LONG).show();
            return;
        }

        // Android 10 και πίσω -> χρειάζεται WRITE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT <= 29) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQ_WRITE
                );
                return;
            }
        }

        if (pdf) exportPdf();
        else exportTxt();
    }

    // ------------------------------------------------------------
    // TXT EXPORT
    // ------------------------------------------------------------
    private void exportTxt() {
        try {
            File outDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!outDir.exists()) outDir.mkdirs();

            String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File out = new File(outDir, "GEL_Service_Report_" + time + ".txt");

            String body = buildReportBody();
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(body.getBytes(StandardCharsets.UTF_8));
            fos.close();

            Toast.makeText(this,
                    "TXT αποθηκεύτηκε:\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

            GELServiceLog.clear();
            txtPreview.setText(getPreviewText());

        } catch (Exception e) {
            Toast.makeText(this, "Σφάλμα TXT: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ------------------------------------------------------------
    // PDF EXPORT — MULTI PAGE + LOGO
    // ------------------------------------------------------------
    private void exportPdf() {
        try {
            File outDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!outDir.exists()) outDir.mkdirs();

            String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File out = new File(outDir, "GEL_Service_Report_" + time + ".pdf");

            String body = buildReportBody();

            PdfDocument pdf = new PdfDocument();
            Paint paint = new Paint();

            int pageWidth = 595;  // A4 72dpi
            int pageHeight = 842;
            int margin = 40;
            int y;

            // split text safely
            String[] lines = body.split("\n");

            int currentLine = 0;
            int pageNumber = 1;

            while (currentLine < lines.length) {

                PdfDocument.PageInfo pageInfo =
                        new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                PdfDocument.Page page = pdf.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                y = margin;

                // ----- LOGO -----
                try {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.gel_logo);
                    if (logo != null) {
                        Bitmap scaled = Bitmap.createScaledBitmap(logo, 64, 64, true);
                        canvas.drawBitmap(scaled, margin, y, paint);
                    }
                } catch (Exception ignored) {}

                paint.setColor(0xFF000000);
                paint.setTextSize(14f);
                canvas.drawText("GEL Service Report", margin + 80, y + 25, paint);

                paint.setTextSize(10f);
                canvas.drawText("GDiolitsis Engine Lab (GEL)", margin + 80, y + 45, paint);

                y += 90;
                paint.setTextSize(9f);

                int lineHeight = 12;
                int maxY = pageHeight - margin;

                // write lines
                while (currentLine < lines.length && y < maxY) {
                    String line = unicodeWrap(lines[currentLine], 85);
                    for (String sub : line.split("\n")) {
                        if (y >= maxY) break;
                        canvas.drawText(sub, margin, y, paint);
                        y += lineHeight;
                    }
                    currentLine++;
                }

                pdf.finishPage(page);
                pageNumber++;
            }

            FileOutputStream fos = new FileOutputStream(out);
            pdf.writeTo(fos);
            fos.close();
            pdf.close();

            Toast.makeText(this,
                    "PDF αποθηκεύτηκε:\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

            GELServiceLog.clear();
            txtPreview.setText(getPreviewText());

        } catch (Exception e) {
            Toast.makeText(this, "Σφάλμα PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ------------------------------------------------------------
    // Safe Unicode wrap (χωρίς να κόβει ελληνικά στη μέση)
    // ------------------------------------------------------------
    private String unicodeWrap(String text, int width) {
        if (text.length() <= width) return text;

        StringBuilder sb = new StringBuilder();
        int index = 0;

        while (index < text.length()) {
            int end = Math.min(index + width, text.length());
            sb.append(text, index, end).append("\n");
            index = end;
        }
        return sb.toString();
    }

    // ------------------------------------------------------------
    // BUILD FULL REPORT BODY
    // ------------------------------------------------------------
    private String buildReportBody() {
        StringBuilder sb = new StringBuilder();

        sb.append("GEL Service Report\n");
        sb.append("GDiolitsis Engine Lab (GEL) — Author & Developer\n");
        sb.append("----------------------------------------\n");
        sb.append("Ημερομηνία: ")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(new Date()))
                .append("\n\n");

        sb.append("Συσκευή: ")
                .append(Build.MANUFACTURER).append(" ")
                .append(Build.MODEL).append("\n");

        sb.append("Android: ")
                .append(Build.VERSION.RELEASE)
                .append("  (API ").append(Build.VERSION.SDK_INT).append(")\n\n");

        sb.append("=== Service Lab Diagnostics ===\n\n");

        if (GELServiceLog.isEmpty()) {
            sb.append("[Δεν υπάρχουν καταχωρημένες διαγνώσεις.]\n");
        } else {
            sb.append(GELServiceLog.getAll()).append("\n");
        }

        sb.append("\n--- Τέλος Report ---\n");
        sb.append("Υπογραφή Τεχνικού: __________________________\n");

        return sb.toString();
    }

    private String getPreviewText() {
        if (GELServiceLog.isEmpty()) {
            return "Δεν υπάρχουν ακόμη διαγνώσεις.\n" +
                   "Τρέξε Auto Diagnosis ή Manual Tests\n" +
                   "και μετά κάνε Export.";
        }
        return GELServiceLog.getAll();
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
