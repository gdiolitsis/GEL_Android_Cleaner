// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — CLEAN & STABLE PDF EXPORT
// --------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
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
import androidx.core.text.HtmlCompat;

public class ServiceReportActivity extends AppCompatActivity {

    private TextView txtPreview;
    private WebView pdfWebView;

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

        // PREVIEW
        txtPreview = new TextView(this);
        txtPreview.setTextSize(sp(13f));
        txtPreview.setTextColor(0xFFEEEEEE);
        txtPreview.setPadding(0, 0, 0, dp(12));
        txtPreview.setText(
                HtmlCompat.fromHtml(getPreviewHtml(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        );
        root.addView(txtPreview);

        // HIDDEN WEBVIEW (PDF ENGINE)
        pdfWebView = new WebView(this);
        pdfWebView.setVisibility(View.GONE);
        pdfWebView.getSettings().setJavaScriptEnabled(false);
        pdfWebView.getSettings().setLoadsImagesAutomatically(true);
        root.addView(pdfWebView);

        // EXPORT BUTTON
        AppCompatButton btnPdf = new AppCompatButton(this);
        btnPdf.setText(getString(R.string.export_pdf_button));
        btnPdf.setAllCaps(false);
        btnPdf.setTextSize(15f);
        btnPdf.setTextColor(Color.WHITE);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);
        btnPdf.setOnClickListener(v -> exportPdfSimple());
        root.addView(btnPdf);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ----------------------------------------------------------
    // PREVIEW HTML
    // ----------------------------------------------------------
    private String getPreviewHtml() {
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
    // SIMPLE EXPORT — STABLE METHOD
    // WebView -> Android Print Framework -> PDF
    // ----------------------------------------------------------
    private void exportPdfSimple() {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, getString(R.string.preview_empty), Toast.LENGTH_LONG).show();
            return;
        }

        final String body = cleanLogForPdf(GELServiceLog.getAll());

        final String html =
                "<!DOCTYPE html><html><head>" +
                        "<meta charset='utf-8'/>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1'/>" +
                        "<style>" +
                        "body{background:#FFFFFF;color:#000000;font-family:monospace;font-size:12px;line-height:1.45;margin:0;padding:0;}" +
                        ".page{max-width:520px;margin:32px auto 40px auto;padding:0 12px;}" +
                        "pre{white-space:pre-wrap;word-wrap:break-word;}" +
                        ".header{border-bottom:1px solid #999;padding-bottom:8px;margin-bottom:12px;}" +
                        ".title{font-size:16px;font-weight:bold;}" +
                        ".sub{font-size:12px;color:#555;}" +
                        "</style>" +
                        "</head><body>" +
                        "<div class='page'>" +

                        // ---------- HEADER ----------
                        "<div class='header'>" +
                        "<div class='title'>GEL Αναφορά Service</div>" +
                        "<div class='sub'>GDiolitsis Engine Lab (GEL) — Author & Developer</div>" +
                        "</div>" +

                        "<pre>" + body + "</pre>" +
                        "</div>" +
                        "</body></html>";

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                printWebViewToPdf(view);
            }
        });

        pdfWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

// ----------------------------------------------------------
// PRINT VIA ANDROID FRAMEWORK — HARD FIX
// ----------------------------------------------------------
private void printWebViewToPdf(WebView webView) {

    // ΠΑΝΤΑ Activity reference
    final Activity activity = ServiceReportActivity.this;

    if (activity.isFinishing() || activity.isDestroyed()) {
        Toast.makeText(activity, "Activity not ready for printing.", Toast.LENGTH_SHORT).show();
        return;
    }

    webView.post(() -> {

        if (activity.isFinishing() || activity.isDestroyed()) return;

        PrintManager printManager =
                (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);

        if (printManager == null) {
            Toast.makeText(activity, "Print service not available.", Toast.LENGTH_LONG).show();
            return;
        }

        PrintAttributes attrs = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(
                        new PrintAttributes.Resolution("pdf", "pdf", 300, 300)
                )
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        printManager.print(
                "GEL_Service_Report",
                webView.createPrintDocumentAdapter("GEL_Service_Report"),
                attrs
        );
    });
}
    
    // ----------------------------------------------------------
    // LOG CLEANUP FOR PDF
    // - κρατά emoji
    // - σβήνει INFO / WARNING / ERROR λέξεις
    // ----------------------------------------------------------
    private String cleanLogForPdf(String raw) {
        if (raw == null) return "";

        String out = stripTimestamps(raw);

        out = out.replaceAll("\\bINFO\\b\\s*", "");
        out = out.replaceAll("\\bWARNING\\b\\s*", "");
        out = out.replaceAll("\\bERROR\\b\\s*", "");

        return out.trim();
    }

    // ----------------------------------------------------------
    // DP / SP
    // ----------------------------------------------------------
    private int dp(int v) { return GELAutoDP.dp(v); }
    private float sp(float v) { return GELAutoDP.sp(v); }
}
