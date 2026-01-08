// GDiolitsis Engine Lab (GEL) - Author & Developer
// ServiceReportActivity — HTML → PDF FINAL (WORKING)
// --------------------------------------------------------------

package com.gel.cleaner;

import com.gel.cleaner.base.*;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;
    private TextView txtPreview;
    private WebView pdfWebView;   // ⬅️ ΚΡΑΤΑΜΕ WebView ΜΟΝΙΜΑ

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
        txtPreview.setPadding(0, 0, 0, dp(12));

        String html = getPreviewText();
        txtPreview.setText(HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY));
        root.addView(txtPreview);

        // HIDDEN WEBVIEW — THE REAL EXPORT ENGINE
        pdfWebView = new WebView(this);
        pdfWebView.setVisibility(View.GONE);
        pdfWebView.getSettings().setJavaScriptEnabled(false);
        root.addView(pdfWebView);

        // EXPORT PDF BUTTON
        AppCompatButton btnPdf = new AppCompatButton(this);
        btnPdf.setText(getString(R.string.export_pdf_button));
        btnPdf.setAllCaps(false);
        btnPdf.setTextSize(15f);
        btnPdf.setTextColor(0xFFFFFFFF);
        btnPdf.setBackgroundResource(R.drawable.gel_btn_outline_selector);

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
    // PDF EXPORT — FROM HTML (REAL)
    // ----------------------------------------------------------
    private void exportPdfFromHtml() {

        Toast.makeText(this, "HTML EXPORT MODE", Toast.LENGTH_SHORT).show();

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

        String htmlBody = GELServiceLog.getHtml();

        String fullHtml =
                "<!DOCTYPE html><html><head>" +
                        "<meta charset='utf-8'/>" +
                        "<style>" +
                        "body{background:#101010;color:#EEEEEE;font-family:monospace;font-size:12px;margin:16px;}" +
                        "</style>" +
                        "</head><body>" +
                        htmlBody +
                        "</body></html>";

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                printWebViewToPdf(view);
            }
        });

        pdfWebView.loadDataWithBaseURL(null, fullHtml, "text/html", "utf-8", null);
    }

    // ----------------------------------------------------------
    // PRINT WEBVIEW TO PDF
    // ----------------------------------------------------------
    private void printWebViewToPdf(WebView webView) {

        try {
            PrintManager printManager =
                    (PrintManager) getSystemService(PRINT_SERVICE);

            if (printManager == null) {
                Toast.makeText(this, "Print service not available.", Toast.LENGTH_LONG).show();
                return;
            }

            PrintAttributes attributes =
                    new PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setResolution(
                                    new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                            .build();

            PrintDocumentAdapter adapter =
                    webView.createPrintDocumentAdapter("GEL_Service_Report");

            printManager.print("GEL_Service_Report", adapter, attributes);

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "PDF error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ----------------------------------------------------------
    // DP / SP
    // ----------------------------------------------------------
    private int dp(int v) { return GELAutoDP.dp(v); }
    private float sp(float v) { return GELAutoDP.sp(v); }
}
