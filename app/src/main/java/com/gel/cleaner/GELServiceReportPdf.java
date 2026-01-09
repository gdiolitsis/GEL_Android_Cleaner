// GDiolitsis Engine Lab (GEL) — Author & Developer
// GELServiceReportPdf — HTML → PDF ENGINE (COLORED, MULTI-PAGE)
// ============================================================
// • Παίρνει HTML από GELServiceLog
// • Φτιάχνει κανονικό PDF αρχείο
// • Υποστηρίζει χρώματα, sections, headers
// • Δεν μπλέκεται με UI — μόνο export logic
// ============================================================

package com.gel.cleaner;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class GELServiceReportPdf {

    private GELServiceReportPdf() {}

    // ============================================================
    // PUBLIC ENTRY
    // ============================================================
    public static void export(Context ctx) {

        if (ctx == null) return;

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(ctx, "No service data to export.", Toast.LENGTH_LONG).show();
            return;
        }

        WebView engine = new WebView(ctx);
        engine.setVisibility(View.GONE);
        engine.getSettings().setJavaScriptEnabled(false);

        String htmlBody = GELServiceLog.getHtml();

        String fullHtml = buildFullHtml(htmlBody);

        engine.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.postDelayed(() -> {
                    try {
                        createPdfFromWebView(ctx, view);
                    } catch (Throwable t) {
                        Toast.makeText(ctx,
                                "PDF export error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }, 200);
            }
        });

        engine.loadDataWithBaseURL(null, fullHtml, "text/html", "utf-8", null);
    }

    // ============================================================
    // HTML TEMPLATE — FULL REPORT
    // ============================================================
    private static String buildFullHtml(String body) {

        String header =
                "<!DOCTYPE html><html><head>" +
                "<meta charset='utf-8'/>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'/>" +
                "<style>" +
                "body{background:#0F0F0F;color:#EAEAEA;font-family:monospace;font-size:12px;line-height:1.4;margin:24px;}" +
                "h1{color:#FFD700;font-size:22px;margin-bottom:6px;}" +
                "h2{color:#7FC8FF;font-size:15px;margin-top:18px;}" +
                "hr{border:0;border-top:1px solid #333;margin:14px 0;}" +
                "b{color:#FFFFFF;}" +
                ".tech{color:#AAAAAA;font-size:11px;margin-top:4px;}" +
                ".footer{margin-top:32px;font-size:11px;color:#888;}" +
                "</style>" +
                "</head><body>";

        String cover =
                "<h1>Service Diagnostic Report</h1>" +
                "<div class='tech'>GDiolitsis Engine Lab (GEL) — Author & Developer</div>" +
                "<div class='tech'>Generated: " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()) +
                "</div>" +
                "<hr>" +
                "<h2>FINAL TECHNICIAN REPORT</h2>" +
                "<hr>";

        String footer =
                "<div class='footer'>" +
                "<hr>" +
                "Technician Signature: ___________________________<br>" +
                "Company Stamp: _________________________________" +
                "</div>" +
                "</body></html>";

        return header + cover + body + footer;
    }

    // ============================================================
    // CORE: WEBVIEW → PDF (MULTI-PAGE)
    // ============================================================
    private static void createPdfFromWebView(Context ctx, WebView wv) throws Exception {

        final int pageWidth  = 595;  // A4
        final int pageHeight = 842;

        wv.measure(
                View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        wv.layout(0, 0, pageWidth, wv.getMeasuredHeight());

        int contentHeight =
                (int) Math.ceil(wv.getContentHeight() * wv.getScale());

        if (contentHeight <= 0)
            contentHeight = Math.max(wv.getMeasuredHeight(), pageHeight);

        PdfDocument pdf = new PdfDocument();

        int yOffset = 0;
        int pageNum = 1;

        while (yOffset < contentHeight) {

            PdfDocument.PageInfo info =
                    new PdfDocument.PageInfo.Builder(
                            pageWidth, pageHeight, pageNum
                    ).create();

            PdfDocument.Page page = pdf.startPage(info);
            Canvas canvas = page.getCanvas();

            canvas.save();
            canvas.translate(0, -yOffset);
            // DRAW LOGO ON EVERY PAGE (TOP-LEFT)
Bitmap logo = BitmapFactory.decodeResource(
        ctx.getResources(), R.drawable.gel_logo);

if (logo != null) {
    Bitmap scaled = Bitmap.createScaledBitmap(logo, 48, 48, true);
    canvas.drawBitmap(scaled, 24, 20, null);
}

// αφήνουμε χώρο για το header
canvas.translate(0, 80);
            wv.draw(canvas);
            canvas.restore();

            pdf.finishPage(page);

            yOffset += pageHeight;
            pageNum++;
        }

        String ts =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(new Date());

        String fileName = "GEL_Service_Report_" + ts + ".pdf";

        Uri saved = savePdf(ctx, fileName, pdf);

        pdf.close();

        if (saved != null)
            Toast.makeText(ctx, "PDF saved: " + fileName, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(ctx, "PDF saved.", Toast.LENGTH_LONG).show();
    }

    // ============================================================
    // SAVE → DOWNLOADS
    // ============================================================
    @Nullable
    private static Uri savePdf(Context ctx,
                               String fileName,
                               PdfDocument pdf) throws Exception {

        OutputStream os = null;
        Uri uri = null;

        try {
            if (Build.VERSION.SDK_INT >= 29) {

                ContentValues cv = new ContentValues();
                cv.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                cv.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                cv.put(MediaStore.Downloads.IS_PENDING, 1);

                ContentResolver cr = ctx.getContentResolver();
                uri = cr.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);

                if (uri == null)
                    throw new Exception("MediaStore insert failed.");

                os = cr.openOutputStream(uri);
                if (os == null)
                    throw new Exception("OutputStream null.");

                pdf.writeTo(os);

                cv.clear();
                cv.put(MediaStore.Downloads.IS_PENDING, 0);
                cr.update(uri, cv, null, null);

                return uri;

            } else {

                File dir =
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS);

                if (!dir.exists()) dir.mkdirs();

                File out = new File(dir, fileName);

                os = new FileOutputStream(out);
                pdf.writeTo(os);

                return null;
            }

        } finally {
            try { if (os != null) os.close(); } catch (Exception ignore) {}
        }
    }
}
