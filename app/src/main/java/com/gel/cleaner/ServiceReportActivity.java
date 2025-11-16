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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ============================================================
// ServiceReportActivity
// Export Service Report (TXT + PDF) με GEL ΛΟΓΟΤΥΠΟ & ΥΠΟΓΡΑΦΗ
// ============================================================
public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;

    private TextView txtPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Απλό UI με preview + 2 κουμπιά ---
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
                "Όλα τα ευρήματα διάγνωσης (Auto + Manual) σε ένα αρχείο."
        );
        sub.setTextSize(13f);
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // Preview log
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
        btnTxt.setTextColor(0xFFFFFFFF);
        btnTxt.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        LinearLayout.LayoutParams lp1 =
                new LinearLayout.LayoutParams(0, dp(48), 1f);
        lp1.setMargins(0, dp(8), dp(4), dp(8));
        btnTxt.setLayoutParams(lp1);
        btnTxt.setOnClickListener(v -> exportWithCheck(false));

        Button btnPdf = new Button(this);
        btnPdf.setText("📄 Export PDF");
        btnPdf.setAllCaps(false);
        btnPdf.setTextColor(0xFFFFFFFF);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);
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
    // Πριν γράψουμε, ζητάμε δικαίωμα αν χρειάζεται
    // ------------------------------------------------------------
    private void exportWithCheck(boolean pdf) {
        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, "Δεν υπάρχει διαθέσιμο Service Log για export.", Toast.LENGTH_LONG).show();
            return;
        }

        if (Build.VERSION.SDK_INT <= 29) {
            // Χρειαζόμαστε WRITE_EXTERNAL_STORAGE στα παλιά
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE);
                // Θα ξαναπατήσει ο χρήστης το κουμπί μετά την άδεια
                return;
            }
        }

        if (pdf) {
            exportPdf();
        } else {
            exportTxt();
        }
    }

    // ------------------------------------------------------------
    // TXT EXPORT
    // ------------------------------------------------------------
    private void exportTxt() {
        try {
            File outDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!outDir.exists()) outDir.mkdirs();

            String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(new Date());
            File out = new File(outDir, "GEL_Service_Report_" + time + ".txt");

            String body = buildReportBody();
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(body.getBytes("UTF-8"));
            fos.flush();
            fos.close();

            Toast.makeText(this,
                    "TXT report αποθηκεύτηκε:\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

            // Reset log για επόμενο πελάτη
            GELServiceLog.clear();
            txtPreview.setText(getPreviewText());

        } catch (Exception e) {
            Toast.makeText(this,
                    "Σφάλμα στο TXT export: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // ------------------------------------------------------------
    // PDF EXPORT (με λογότυπο GEL στην κορυφή)
    // ------------------------------------------------------------
    private void exportPdf() {
        try {
            File outDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!outDir.exists()) outDir.mkdirs();

            String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(new Date());
            File out = new File(outDir, "GEL_Service_Report_" + time + ".pdf");

            String body = buildReportBody();

            PdfDocument doc = new PdfDocument();
            Paint paint = new Paint();

            int pageWidth = 595;  // A4 περίπου σε 72dpi
            int pageHeight = 842;

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = doc.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int y = 40;

            // Logo (αν υπάρχει)
            try {
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.gel_logo);
                if (logo != null) {
                    int lw = 64;
                    int lh = 64;
                    Bitmap scaled = Bitmap.createScaledBitmap(logo, lw, lh, true);
                    canvas.drawBitmap(scaled, 40, y, paint);
                }
            } catch (Exception ignored) {}

            // Header κείμενο
            paint.setColor(0xFF000000);
            paint.setTextSize(14f);
            canvas.drawText("GEL Service Report", 120, y + 25, paint);

            paint.setTextSize(10f);
            canvas.drawText("GDiolitsis Engine Lab (GEL) — Author & Developer",
                    120, y + 45, paint);

            y += 80;

            // Κύριο σώμα (πολύ απλό line-wrap)
            paint.setTextSize(9.5f);
            String[] lines = body.split("\n");
            int lineHeight = 12;

            for (String line : lines) {
                // απλό wrap σε ~80 chars
                while (line.length() > 80) {
                    String part = line.substring(0, 80);
                    canvas.drawText(part, 40, y, paint);
                    y += lineHeight;
                    line = line.substring(80);
                    if (y > pageHeight - 40) break;
                }
                if (y > pageHeight - 40) break;
                canvas.drawText(line, 40, y, paint);
                y += lineHeight;
            }

            doc.finishPage(page);

            FileOutputStream fos = new FileOutputStream(out);
            doc.writeTo(fos);
            fos.flush();
            fos.close();
            doc.close();

            Toast.makeText(this,
                    "PDF report αποθηκεύτηκε:\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

            // Reset log για επόμενο πελάτη
            GELServiceLog.clear();
            txtPreview.setText(getPreviewText());

        } catch (Exception e) {
            Toast.makeText(this,
                    "Σφάλμα στο PDF export: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // ------------------------------------------------------------
    // Χτίσιμο σώματος report (πάντα branded GEL)
    // ------------------------------------------------------------
    private String buildReportBody() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("GEL Service Report\n");
        sb.append("GDiolitsis Engine Lab (GEL) — Author & Developer\n");
        sb.append("App: GEL Cleaner (Android)\n");
        sb.append("----------------------------------------\n");
        sb.append("Ημερομηνία: ")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(new Date()))
                .append("\n\n");

        // Device info (όσο μπορούμε χωρίς extra permissions)
        sb.append("Συσκευή: ")
                .append(android.os.Build.MANUFACTURER).append(" ")
                .append(android.os.Build.MODEL).append("\n");
        sb.append("Android: ")
                .append(android.os.Build.VERSION.RELEASE)
                .append(" (API ").append(android.os.Build.VERSION.SDK_INT).append(")\n\n");

        sb.append("=== Service Lab Diagnostics ===\n\n");

        if (GELServiceLog.isEmpty()) {
            sb.append("[Δεν υπάρχουν διαθέσιμα logs — τρέξε Auto/Manual διαγνώσεις πρώτα.]\n");
        } else {
            sb.append(GELServiceLog.getAll()).append("\n");
        }

        sb.append("\n--- Τέλος Report ---\n");
        sb.append("Υπογραφή Τεχνικού: __________________________\n");

        return sb.toString();
    }

    private String getPreviewText() {
        if (GELServiceLog.isEmpty()) {
            return "Δεν υπάρχουν ακόμη καταχωρημένες διαγνώσεις.\n" +
                   "Τρέξε Auto Diagnosis ή Manual Tests,\n" +
                   "και μετά γύρνα εδώ για Export.";
        }
        return GELServiceLog.getAll();
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
