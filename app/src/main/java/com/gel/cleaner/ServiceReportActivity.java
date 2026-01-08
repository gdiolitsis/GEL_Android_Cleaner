// GDiolitsis Engine Lab (GEL) â€” Author & Developer
// ServiceReportActivity â€” Final Stable Layout Edition
// --------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;
    private TextView txtPreview;

    // ----------------------------------------------------------
    // FOLDABLE ORCHESTRATOR
    // ----------------------------------------------------------
    private final GELFoldableCallback foldableCallback = new GELFoldableCallback() {
        @Override
        public void onPostureChanged(@NonNull Posture posture) {
            if (txtPreview != null) txtPreview.postInvalidate();
        }

        @Override
        public void onScreenChanged(boolean isInner) {
            if (txtPreview != null)
                txtPreview.setTextSize(isInner ? 14f : 13f);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        GELFoldableOrchestrator.register(this, foldableCallback);
    }

    @Override
    protected void onStop() {
        GELFoldableOrchestrator.unregister(this, foldableCallback);
        super.onStop();
    }

    // ----------------------------------------------------------
    // LOCALE
    // ----------------------------------------------------------
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.apply(base));
    }

    // ----------------------------------------------------------
    // UI
    // ----------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GELAutoDP.init(this);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);
        root.setBackgroundColor(0xFF101010);

        // TITLE
        TextView title = new TextView(this);
        title.setText("ðŸ“„ " + getString(R.string.export_report_title));
        title.setTextSize(sp(22f));
        title.setTextColor(0xFFFFD700);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        // SUBTITLE
        TextView sub = new TextView(this);
        sub.setText(
                getString(R.string.report_dev_line) + "\n" +
                        getString(R.string.export_report_desc).trim()
        );
        sub.setTextSize(sp(13f));
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // PREVIEW
txtPreview = new TextView(this);
txtPreview.setTextSize(sp(13f));
txtPreview.setTextColor(0xFFEEEEEE);
txtPreview.setMovementMethod(new ScrollingMovementMethod());
txtPreview.setPadding(0, 0, 0, dp(12));
txtPreview.setText(
        Html.fromHtml(getPreviewText(), Html.FROM_HTML_MODE_LEGACY)
);

root.addView(txtPreview);

        // EXPORT PDF BUTTON
        AppCompatButton btnPdf = new AppCompatButton(this);
        btnPdf.setText(getString(R.string.export_pdf_button));
        btnPdf.setAllCaps(false);
        btnPdf.setTextSize(15f);
        btnPdf.setTextColor(0xFFFFFFFF);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (56 * getResources().getDisplayMetrics().density)
                );
        lp.setMargins(
                (int) (8 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (8 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density)
        );
        btnPdf.setLayoutParams(lp);
        btnPdf.setMinimumHeight((int) (56 * getResources().getDisplayMetrics().density));
        btnPdf.setPadding(0,
                (int) (12 * getResources().getDisplayMetrics().density),
                0,
                (int) (12 * getResources().getDisplayMetrics().density)
        );

        btnPdf.setOnClickListener(v -> exportWithCheck(true));
        root.addView(btnPdf);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ----------------------------------------------------------
    // EXPORT CHECK
    // ----------------------------------------------------------
    private void exportWithCheck(boolean pdf) {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, getString(R.string.preview_empty), Toast.LENGTH_LONG).show();
            return;
        }

        if (Build.VERSION.SDK_INT <= 29) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQ_WRITE
                );
                return;
            }
        }

        GELFoldableUIManager.freezeTransitions(this);
        exportPdf();
        GELFoldableUIManager.unfreezeTransitions(this);
    }

    // ----------------------------------------------------------
    // PDF EXPORT
    // ----------------------------------------------------------
    private void exportPdf() {
        try {
            File outDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            );
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
                    String line = unicodeWrap(lines[currentLine], 52);
                    for (String subLine : line.split("\n")) {
                        if (y >= maxY) break;
                        canvas.drawText(subLine, margin, y, paint);
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

            Toast.makeText(
                    this,
                    "PDF " + getString(R.string.toast_done) + "\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();

            GELServiceLog.clear();
            txtPreview.setText(getPreviewText());

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    getString(R.string.export_pdf_error) + ": " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private String unicodeWrap(String text, int width) {
        if (text == null) return "";
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

    // ----------------------------------------------------------
    // REPORT BUILDER
    // ----------------------------------------------------------
    private String buildReportBody() {
        StringBuilder sb = new StringBuilder();

        sb.append(getString(R.string.report_title)).append("\n");
        sb.append(getString(R.string.report_dev_line)).append("\n");
        sb.append("---------------------\n");

        sb.append(getString(R.string.report_date)).append(": ")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()))
                .append("\n\n");

        sb.append(getString(R.string.report_device)).append(": ")
                .append(Build.MANUFACTURER).append(" ")
                .append(Build.MODEL).append("\n");

        sb.append(getString(R.string.report_android)).append(": ")
                .append(Build.VERSION.RELEASE)
                .append("  (API ").append(Build.VERSION.SDK_INT).append(")\n\n");

        sb.append(getString(R.string.report_diag_header)).append("\n\n");

        if (GELServiceLog.isEmpty()) {
            sb.append(getString(R.string.report_no_entries)).append("\n");
        } else {
            sb.append(stripTimestamps(GELServiceLog.getAll())).append("\n");
        }

        sb.append("\n").append(getString(R.string.report_end)).append("\n");
        sb.append(getString(R.string.report_signature))
                .append(" __________________________\n");

        return sb.toString();
    }

// ----------------------------------------------------------
// PREVIEW
// ----------------------------------------------------------
private String getPreviewText() {
    if (GELServiceLog.isEmpty()) {
        return getString(R.string.preview_empty);
    }

    // παίρνουμε το COLORED html log
    String html = GELServiceLog.getHtml();

    // αν θες να φύγουν timestamps ΚΑΙ από το preview
    html = stripTimestamps(html);

    return html;
}

private String stripTimestamps(String log) {
    if (log == null) return "";

    // αφαιρεί patterns τύπου: 2026-01-07 23:21:37
    return log.replaceAll(
            "\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\s*",
            ""
    );
}

    // ----------------------------------------------------------
    // DP / SP
    // ----------------------------------------------------------
    private int dp(int v) { return GELAutoDP.dp(v); }
    private float sp(float v) { return GELAutoDP.sp(v); }
}
