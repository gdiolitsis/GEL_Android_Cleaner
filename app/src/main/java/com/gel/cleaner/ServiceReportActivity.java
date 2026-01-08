// GDiolitsis Engine Lab (GEL) - Author & Developer
// ServiceReportActivity — HTML → PDF (REAL FILE EXPORT) FINAL
// --------------------------------------------------------------
// NOTE: This version exports COLORED HTML to PDF directly (no text fallback).
// --------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import androidx.core.text.HtmlCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;

    private TextView txtPreview;
    private WebView pdfWebView; // hidden engine

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
        title.setText(getString(R.string.export_report_title));
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

        // PREVIEW (HTML)
        txtPreview = new TextView(this);
        txtPreview.setTextSize(sp(13f));
        txtPreview.setTextColor(0xFFEEEEEE);
        txtPreview.setMovementMethod(new ScrollingMovementMethod());
        txtPreview.setPadding(0, 0, 0, dp(12));
        txtPreview.setText(HtmlCompat.fromHtml(getPreviewText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        root.addView(txtPreview);

        // HIDDEN WEBVIEW (EXPORT ENGINE)
        pdfWebView = new WebView(this);
        pdfWebView.setVisibility(View.GONE);
        pdfWebView.getSettings().setJavaScriptEnabled(false);
        pdfWebView.getSettings().setLoadsImagesAutomatically(true);
        root.addView(pdfWebView);

        // EXPORT PDF BUTTON (HTML ONLY)
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

        btnPdf.setOnClickListener(v -> exportPdfFromHtml());
        root.addView(btnPdf);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ----------------------------------------------------------
    // PREVIEW
    // ----------------------------------------------------------
    private String getPreviewText() {
        if (GELServiceLog.isEmpty()) {
            return getString(R.string.preview_empty);
        }
        return stripTimestamps(GELServiceLog.getHtml());
    }

    private String stripTimestamps(String log) {
        if (log == null) return "";
        return log.replaceAll(
                "\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}",
                ""
        );
    }

    // ----------------------------------------------------------
    // EXPORT — HTML → PDF FILE (COLORED)
    // ----------------------------------------------------------
    private void exportPdfFromHtml() {

        // ΑΠΟΔΕΙΞΗ ΟΤΙ ΕΔΩ ΜΠΑΙΝΕΙ
        Toast.makeText(this, "HTML PDF EXPORT MODE", Toast.LENGTH_SHORT).show();

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, getString(R.string.preview_empty), Toast.LENGTH_LONG).show();
            return;
        }

        // legacy permission for <=29 flow
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

        final String htmlBody = stripTimestamps(GELServiceLog.getHtml());

        // FULL HTML (same vibe as live labs)
        final String fullHtml =
                "<!DOCTYPE html><html><head>" +
                        "<meta charset='utf-8'/>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1'/>" +
                        "<style>" +
                        "body{background:#101010;color:#EEEEEE;font-family:monospace;font-size:12px;line-height:1.35;margin:16px;}" +
                        "b{color:#FFFFFF;}" +
                        "hr{border:0;border-top:1px solid #333;margin:10px 0;}" +
                        "</style>" +
                        "</head><body>" +
                        htmlBody +
                        "</body></html>";

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                // μικρό delay για να “κάτσει” το layout / contentHeight
                view.postDelayed(() -> {
                    try {
                        createPdfFromWebView(view);
                    } catch (Throwable t) {
                        Toast.makeText(ServiceReportActivity.this,
                                "PDF export error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }, 200);
            }
        });

        pdfWebView.loadDataWithBaseURL(null, fullHtml, "text/html", "utf-8", null);
    }

    // ----------------------------------------------------------
    // CORE: Render WebView → PdfDocument (MULTI-PAGE) → Save file
    // ----------------------------------------------------------
    private void createPdfFromWebView(WebView wv) throws Exception {

        // A4 in “points-like pixels” (same as your old PdfDocument sizes)
        final int pageWidth = 595;
        final int pageHeight = 842;

        // Force WebView to layout at page width
        wv.measure(
                View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        wv.layout(0, 0, pageWidth, wv.getMeasuredHeight());

        // get full content height
        int contentHeight = (int) Math.ceil(wv.getContentHeight() * wv.getScale());
        if (contentHeight <= 0) {
            // fallback to measured height
            contentHeight = Math.max(wv.getMeasuredHeight(), pageHeight);
        }

        PdfDocument pdf = new PdfDocument();

        int yOffset = 0;
        int pageNumber = 1;

        while (yOffset < contentHeight) {

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();

            PdfDocument.Page page = pdf.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // clip to page and translate
            canvas.save();
            canvas.translate(0, -yOffset);
            wv.draw(canvas);
            canvas.restore();

            pdf.finishPage(page);

            yOffset += pageHeight;
            pageNumber++;
        }

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "GEL_Service_Report_" + time + ".pdf";

        Uri saved = savePdfToDownloads(fileName, pdf);

        pdf.close();

        if (saved != null) {
            Toast.makeText(this, "PDF saved: " + fileName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "PDF saved.", Toast.LENGTH_LONG).show();
        }

        // refresh preview (optional)
        txtPreview.setText(HtmlCompat.fromHtml(getPreviewText(), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    // ----------------------------------------------------------
    // SAVE PDF → Downloads (MediaStore for 29+, File for legacy)
    // ----------------------------------------------------------
    @Nullable
    private Uri savePdfToDownloads(String fileName, PdfDocument pdf) throws Exception {

        OutputStream os = null;
        Uri uri = null;

        try {
            if (Build.VERSION.SDK_INT >= 29) {

                ContentValues cv = new ContentValues();
                cv.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                cv.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                cv.put(MediaStore.Downloads.IS_PENDING, 1);

                ContentResolver cr = getContentResolver();
                uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);
                if (uri == null) throw new Exception("MediaStore insert failed.");

                os = cr.openOutputStream(uri);
                if (os == null) throw new Exception("OutputStream null.");

                pdf.writeTo(os);

                cv.clear();
                cv.put(MediaStore.Downloads.IS_PENDING, 0);
                cr.update(uri, cv, null, null);

                return uri;

            } else {
                File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!outDir.exists()) outDir.mkdirs();
                File out = new File(outDir, fileName);

                os = new FileOutputStream(out);
                pdf.writeTo(os);

                return null; // legacy path
            }

        } finally {
            try { if (os != null) os.close(); } catch (Exception ignore) {}
        }
    }

    // ----------------------------------------------------------
    // PERMISSION RESULT (legacy)
    // ----------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportPdfFromHtml();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ----------------------------------------------------------
    // DP / SP
    // ----------------------------------------------------------
    private int dp(int v) { return GELAutoDP.dp(v); }
    private float sp(float v) { return GELAutoDP.sp(v); }
}
