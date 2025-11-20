package com.gel.cleaner;

import android.Manifest;
import android.content.Context;
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

public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;
    private TextView txtPreview;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

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

        // TITLE
        TextView title = new TextView(this);
        title.setText("📄 " + getString(R.string.export_report_title));
        title.setTextSize(22f);
        title.setTextColor(0xFFFFD700);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        // SUBTITLE
        TextView sub = new TextView(this);
        sub.setText(
                getString(R.string.report_dev_line) + "\n" +
                getString(R.string.export_report_desc).trim()
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

        Button btnPdf = new Button(this);
        btnPdf.setText(getString(R.string.export_pdf_button));    // FIXED
        btnPdf.setAllCaps(false);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        btnPdf.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams lp2 =
                new LinearLayout.LayoutParams(0, dp(48), 1f);
        lp2.setMargins(dp(4), dp(8), dp(4), dp(8));
        btnPdf.setLayoutParams(lp2);
        btnPdf.setOnClickListener(v -> exportWithCheck(true));

        btnRow.addView(btnPdf);
        root.addView(btnRow);

        scroll.addView(root);
        setContentView(scroll);
    }

    private void exportWithCheck(boolean pdf) {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, getString(R.string.preview_empty), Toast.LENGTH_LONG).show();
            return;
        }

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

        exportPdf();
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

            int pageWidth = 595;
            int pageHeight = 842;
            int margin = 40;
            int y;

            String[] lines = body.split("\n");
            int currentLine = 0;
            int pageNumber = 1;

            while (currentLine < lines.length) {

                PdfDocument.PageInfo pageInfo =
                        new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                PdfDocument.Page page = pdf.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                y = margin;

                try {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.gel_logo);
                    if (logo != null) {
                        Bitmap scaled = Bitmap.createScaledBitmap(logo, 64, 64, true);
                        canvas.drawBitmap(scaled, margin, y, paint);
                    }
                } catch (Exception ignored) {}

                paint.setColor(0xFF000000);
                paint.setTextSize(14f);
                canvas.drawText(getString(R.string.report_title), margin + 80, y + 25, paint);

                paint.setTextSize(10f);
                canvas.drawText(getString(R.string.report_dev_line), margin + 80, y + 45, paint);

                y += 90;
                paint.setTextSize(9f);

                int lineHeight = 12;
                int maxY = pageHeight - margin;

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
                    "PDF " + getString(R.string.toast_done) + "\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

            GELServiceLog.clear();
            txtPreview.setText(getPreviewText());

        } catch (Exception e) {
            Toast.makeText(this,
                    getString(R.string.export_pdf_error) + ": " + e.getMessage(),
                    Toast.LENGTH_LONG).show();      // FIXED
        }
    }

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

    private String buildReportBody() {
        StringBuilder sb = new StringBuilder();

        sb.append(getString(R.string.report_title)).append("\n");
        sb.append(getString(R.string.report_dev_line)).append("\n");
        sb.append("----------------------------------------\n");

        sb.append(getString(R.string.report_date)).append(": ")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(new Date()))
                .append("\n\n");

        sb.append(getString(R.string.report_device)).append(": ")
                .append(Build.MANUFACTURER).append(" ")
                .append(Build.MODEL).append("\n");

        sb.append(getString(R.string.report_android)).append(": ")
                .append(Build.VERSION.RELEASE)
                .append("  (API ").append(Build.VERSION.SDK_INT).append(")\n\n");

        sb.append(getString(R.string.damage_title)).append("\n");
        appendDamageLine(sb, R.string.damage_screen);
        appendDamageLine(sb, R.string.damage_pixels);
        appendDamageLine(sb, R.string.damage_amoled);
        appendDamageLine(sb, R.string.damage_charge_port);
        appendDamageLine(sb, R.string.damage_speaker);
        appendDamageLine(sb, R.string.damage_mic);
        appendDamageLine(sb, R.string.damage_battery);
        appendDamageLine(sb, R.string.damage_water);
        sb.append("\n");

        sb.append(getString(R.string.report_diag_header)).append("\n\n");

        if (GELServiceLog.isEmpty()) {
            sb.append(getString(R.string.report_no_entries)).append("\n");
        } else {
            sb.append(GELServiceLog.getAll()).append("\n");
        }

        sb.append("\n").append(getString(R.string.report_end)).append("\n");
        sb.append(getString(R.string.report_signature))
                .append(" __________________________\n");

        return sb.toString();
    }

    private void appendDamageLine(StringBuilder sb, int labelRes) {
        sb.append("- ")
                .append(getString(labelRes))
                .append(": ")
                .append(getString(R.string.damage_yes_no))
                .append("\n");
    }

    private String getPreviewText() {
        if (GELServiceLog.isEmpty()) {
            return getString(R.string.preview_empty);
        }
        return GELServiceLog.getAll();
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d + 0.5f);
    }
}
