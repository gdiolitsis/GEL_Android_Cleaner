// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — CLEAN PDF EXPORT (NO PRINT FRAMEWORK)
// --------------------------------------------------------------
// NOTE (GEL rule): πάντα δίνω ολόκληρο έτοιμο αρχείο για copy-paste, χωρίς μπλα μπλα.

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.ValueCallback;
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
    private WebView pdfWebView;

    // ----------------------------------------------------------
    // FOLDABLE ORCHESTRATOR
    // ----------------------------------------------------------
    private final GELFoldableCallback foldableCallback = new GELFoldableCallback() {
        @Override public void onPostureChanged(@NonNull Posture posture) {
            if (txtPreview != null) txtPreview.postInvalidate();
        }
        @Override public void onScreenChanged(boolean isInner) {
            if (txtPreview != null) txtPreview.setTextSize(isInner ? 14f : 13f);
        }
    };

    @Override protected void onStart() {
        super.onStart();
        GELFoldableOrchestrator.register(this, foldableCallback);
    }

    @Override protected void onStop() {
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
        sub.setText(getString(R.string.report_dev_line) + "\n" +
                getString(R.string.export_report_desc).trim());
        sub.setTextSize(sp(13f));
        sub.setTextColor(0xFFCCCCCC);
        sub.setPadding(0, 0, 0, dp(12));
        root.addView(sub);

        // PREVIEW
        txtPreview = new TextView(this);
        txtPreview.setTextSize(sp(13f));
        txtPreview.setTextColor(0xFFEEEEEE);
        txtPreview.setPadding(0, 0, 0, dp(12));
        txtPreview.setText(HtmlCompat.fromHtml(getPreviewHtml(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        root.addView(txtPreview);

        // HIDDEN WEBVIEW (PDF RENDER ENGINE)
        pdfWebView = new WebView(this);
        pdfWebView.setVisibility(View.GONE);
        pdfWebView.getSettings().setJavaScriptEnabled(true); // απαραίτητο για scrollHeight
        pdfWebView.getSettings().setLoadsImagesAutomatically(true);
        root.addView(pdfWebView);

        // EXPORT BUTTON
        AppCompatButton btnPdf = new AppCompatButton(this);
        btnPdf.setText(getString(R.string.export_pdf_button));
        btnPdf.setAllCaps(false);
        btnPdf.setTextSize(15f);
        btnPdf.setTextColor(Color.WHITE);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        btnPdf.setOnClickListener(v -> exportPdf());
        root.addView(btnPdf);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ----------------------------------------------------------
    // PREVIEW HTML (SYNCED)
    // ----------------------------------------------------------
    private String getPreviewHtml() {
        if (GELServiceLog.isEmpty()) return getString(R.string.preview_empty);
        return stripInnerTimestamps(GELServiceLog.getHtml());
    }

    // κρατάμε timestamps ΜΟΝΟ στο header (δηλ. δεν τα πειράζουμε εκεί),
    // αλλά σβήνουμε τα “γραμμή-γραμμή” μέσα στο log.
    private String stripInnerTimestamps(String log) {
        if (log == null) return "";
        return log.replaceAll("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\s*", "");
    }

    // ----------------------------------------------------------
    // EXPORT — ONE CLEAN FLOW
    // ----------------------------------------------------------
    private void exportPdf() {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, getString(R.string.preview_empty), Toast.LENGTH_LONG).show();
            return;
        }
        if (pdfWebView == null) {
            Toast.makeText(this, "PDF engine not ready.", Toast.LENGTH_LONG).show();
            return;
        }

        // legacy permission (<=29)
        if (Build.VERSION.SDK_INT <= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQ_WRITE
                );
                return;
            }
        }

        final String cleanedBody = cleanLogForPdf(GELServiceLog.getAll());

        final String headerHtml = buildPdfHeaderHtml(); // έχει την 1η ημερομηνία μέσα

        final String fullHtml =
                "<!DOCTYPE html><html><head>" +
                        "<meta charset='utf-8'/>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1'/>" +
                        "<style>" +
                        "body{background:#FFFFFF;color:#000000;font-family:monospace;font-size:12px;line-height:1.45;margin:0;padding:0;}" +
                        ".page{max-width:520px;margin:28px auto 36px auto;padding:0 12px;}" +
                        "pre{white-space:pre-wrap;word-wrap:break-word;}" +
                        ".hr{border-top:1px solid #999;margin:10px 0 12px 0;}" +
                        "</style>" +
                        "</head><body>" +
                        "<div class='page'>" +
                        headerHtml +
                        "<div class='hr'></div>" +
                        "<pre>" + escapeHtmlForPre(cleanedBody) + "</pre>" +
                        "</div>" +
                        "</body></html>";

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                // 1) δίνουμε λίγο χρόνο να “κάτσει” layout/fonts
                view.postDelayed(() -> {

                    // 2) παίρνουμε πραγματικό scrollHeight από JS (όχι getMeasuredHeight φάντασμα)
                    evalScrollHeight(view, pxHeight -> {
                        if (pxHeight <= 0) {
                            Toast.makeText(ServiceReportActivity.this,
                                    "PDF render failed: empty content height.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            createPdfFromWebView(view, pxHeight);
                        } catch (Throwable t) {
                            Toast.makeText(ServiceReportActivity.this,
                                    "PDF export error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }, 300);
            }
        });

        pdfWebView.loadDataWithBaseURL(null, fullHtml, "text/html", "utf-8", null);
    }

    // ----------------------------------------------------------
    // HEIGHT FROM JS
    // ----------------------------------------------------------
    private void evalScrollHeight(WebView wv, final HeightCallback cb) {
        // documentElement.scrollHeight -> πιο σταθερό
        String js = "(function(){return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight);})()";
        wv.evaluateJavascript(js, new ValueCallback<String>() {
            @Override public void onReceiveValue(String value) {
                int h = 0;
                try {
                    if (value != null) {
                        value = value.replace("\"", "").trim();
                        h = Integer.parseInt(value);
                    }
                } catch (Throwable ignore) {}
                cb.onHeight(h);
            }
        });
    }

    private interface HeightCallback { void onHeight(int pxHeight); }

    // ----------------------------------------------------------
    // CORE — WebView -> Bitmap -> PdfDocument
    // ----------------------------------------------------------
    private void createPdfFromWebView(WebView wv, int contentHeightPx) throws Exception {

        final int pageWidth  = 595; // A4 @ 72dpi-ish
        final int pageHeight = 842;

        // FIX width
        wv.measure(
                View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(contentHeightPx, View.MeasureSpec.EXACTLY)
        );
        wv.layout(0, 0, pageWidth, contentHeightPx);

        // Bitmap full content
        Bitmap bitmap = Bitmap.createBitmap(pageWidth, contentHeightPx, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.WHITE);
        wv.draw(bitmapCanvas);

        PdfDocument pdf = new PdfDocument();

        int yOffset = 0;
        int pageNum = 1;

        while (yOffset < contentHeightPx) {

            PdfDocument.PageInfo info =
                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create();

            PdfDocument.Page page = pdf.startPage(info);
            Canvas canvas = page.getCanvas();

            canvas.save();
            canvas.translate(0, -yOffset);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.restore();

            pdf.finishPage(page);

            yOffset += pageHeight;
            pageNum++;
        }

        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "GEL_Service_Report_" + ts + ".pdf";

        try {
            savePdfToDownloads(fileName, pdf);
        } finally {
            pdf.close();
        }

        Toast.makeText(this,
                (Build.VERSION.SDK_INT >= 29)
                        ? ("PDF exported\nDownloads/" + fileName)
                        : ("PDF exported\n/storage/emulated/0/Download/" + fileName),
                Toast.LENGTH_LONG).show();
    }

    // ----------------------------------------------------------
    // SAVE PDF → DOWNLOADS
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

                return null;
            }

        } finally {
            try { if (os != null) os.close(); } catch (Exception ignore) {}
        }
    }

    // ----------------------------------------------------------
    // HEADER HTML — όπως στην εικόνα (απλό, χωρίς “legend”)
    // ----------------------------------------------------------
    private String buildPdfHeaderHtml() {

        // Μία ημερομηνία ΜΟΝΟ στην αρχή
        String when = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date());

        // Αν έχεις ήδη header από GELServiceLog, πες μου να το τραβήξουμε αυτούσιο.
        // Εδώ το κάνω minimal & ασφαλές.

        return ""
                + "<div style='display:flex;gap:10px;align-items:flex-start;'>"
                + "  <div style='width:64px;height:64px;border:1px solid #999;display:flex;align-items:center;justify-content:center;font-weight:bold;'>GEL</div>"
                + "  <div style='flex:1;'>"
                + "    <div style='font-size:16px;font-weight:bold;'>GEL Αναφορά Service</div>"
                + "    <div style='font-size:12px;color:#333;'>GDiolitsis Engine Lab (GEL) — Author & Developer</div>"
                + "    <div style='font-size:12px;color:#333;margin-top:6px;'>Ημερομηνία: " + when + "</div>"
                + "  </div>"
                + "</div>";
    }

    // ----------------------------------------------------------
    // LOG CLEANUP FOR PDF
    // - κρατά emoji
    // - σβήνει INFO/WARNING/ERROR λέξεις
    // - σβήνει timestamps μέσα στο log
    // ----------------------------------------------------------
    private String cleanLogForPdf(String raw) {
        if (raw == null) return "";

        String out = stripInnerTimestamps(raw);

        // αφαιρούμε λέξεις severity (κρατάμε emoji/σύμβολα)
        out = out.replaceAll("\\bINFO\\b\\s*", "");
        out = out.replaceAll("\\bWARNING\\b\\s*", "");
        out = out.replaceAll("\\bERROR\\b\\s*", "");

        return out.trim();
    }

    // ----------------------------------------------------------
    // HTML escape μέσα σε <pre>
    // ----------------------------------------------------------
    private String escapeHtmlForPre(String s) {
        if (s == null) return "";
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    // ----------------------------------------------------------
    // PERMISSION RESULT (LEGACY)
    // ----------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportPdf();
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
