// GDiolitsis Engine Lab (GEL) - Author & Developer
// ServiceReportActivity — HTML → PDF FINAL
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import androidx.core.text.HtmlCompat;

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
        txtPreview.setMovementMethod(new ScrollingMovementMethod());
        txtPreview.setPadding(0, 0, 0, dp(12));

        String html = getPreviewText();
        txtPreview.setText(
                HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
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

        // ΜΟΝΟ HTML EXPORT
        // ΜΟΝΟ HTML EXPORT — DEBUG
btnPdf.setOnClickListener(v -> {
    Toast.makeText(this, "BTN CLICKED", Toast.LENGTH_SHORT).show();
    exportPdfFromHtml();
});

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

        String html = GELServiceLog.getHtml();
        return stripTimestamps(html);
    }

    private String stripTimestamps(String log) {
        if (log == null) return "";
        return log.replaceAll(
                "\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}",
                ""
        );
    }

    // ----------------------------------------------------------
    // PDF EXPORT — FROM HTML (FINAL)
    // ----------------------------------------------------------
    
private void exportPdfFromHtml() {

    // 🔥 DEBUG — απόδειξη ότι μπαίνουμε στο HTML export
    Toast.makeText(this, "HTML EXPORT PATH", Toast.LENGTH_LONG).show();

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

        // DEBUG MARK — για επιβεβαίωση HTML flow
        htmlBody += "<br><br><b style='color:red'>HTML EXPORT ACTIVE</b><br><br>";

        String fullHtml =
                "<!DOCTYPE html><html><head>" +
                "<meta charset='utf-8'/>" +
                "<style>" +
                "body{background:#101010;color:#EEEEEE;font-family:monospace;font-size:12px;margin:16px;}" +
                "</style>" +
                "</head><body>" +
                htmlBody +
                "</body></html>";

        WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(false);

        wv.loadDataWithBaseURL(null, fullHtml, "text/html", "utf-8", null);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                printWebViewToPdf(view);
            }
        });
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
