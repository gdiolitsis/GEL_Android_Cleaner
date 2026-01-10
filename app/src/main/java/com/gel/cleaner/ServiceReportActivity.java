// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — CLEAN PDF EXPORT

package com.gel.cleaner;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.provider.MediaStore;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int REQ_WRITE = 9911;

    private TextView txtPreview;
    private WebView pdfWebView;

    // ----------------------------------------------------------
    // UI
    // ----------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);

        // TITLE
        TextView title = new TextView(this);
        title.setText("GEL Αναφορά Service");
        title.setTextSize(22f);
        root.addView(title);

        // PREVIEW
        txtPreview = new TextView(this);
        txtPreview.setTextSize(13f);
        txtPreview.setText(
                HtmlCompat.fromHtml(getPreviewHtml(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        );
        root.addView(txtPreview);

        // HIDDEN WEBVIEW (PDF ENGINE)
        pdfWebView = new WebView(this);
        pdfWebView.setVisibility(View.GONE);
        pdfWebView.getSettings().setJavaScriptEnabled(false);
        root.addView(pdfWebView);

        // EXPORT BUTTON
        AppCompatButton btn = new AppCompatButton(this);
        btn.setText("Export PDF");
        btn.setOnClickListener(v -> exportPdf());
        root.addView(btn);

        scroll.addView(root);
        setContentView(scroll);
    }

    // ----------------------------------------------------------
    // PREVIEW HTML
    // ----------------------------------------------------------
    private String getPreviewHtml() {
        if (GELServiceLog.isEmpty()) {
            return "No data.";
        }

        String log = GELServiceLog.getAll();

        // ❌ remove timestamps everywhere
        log = log.replaceAll(
                "\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\s+",
                ""
        );

        // ❌ remove words INFO / WARNING / ERROR
        log = log.replaceAll("\\bINFO\\b\\s*", "");
        log = log.replaceAll("\\bWARNING\\b\\s*", "");
        log = log.replaceAll("\\bERROR\\b\\s*", "");

        // --------------------------------------------------
        // HEADER όπως στη φωτογραφία
        // --------------------------------------------------
        String header =
                "<b>GEL Αναφορά Service</b><br>" +
                "GDiolitsis Engine Lab (GEL) — Author & Developer<br><br>" +
                "<b>Ημερομηνία:</b> " +
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(new Date()) +
                "<br><br>";

        return "<pre>" + header + log + "</pre>";
    }

    // ----------------------------------------------------------
    // EXPORT — SIMPLE & STABLE
    // ----------------------------------------------------------
    private void exportPdf() {

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

        final String html =
                "<html><body style='font-family:monospace;font-size:12px;'>" +
                        getPreviewHtml() +
                        "</body></html>";

        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                createPdfFromWebView(view);
            }
        });

        pdfWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    // ----------------------------------------------------------
    // CORE — WEBVIEW → PRINT → PDF
    // ----------------------------------------------------------
    private void createPdfFromWebView(WebView webView) {

        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        String fileName = "GEL_Service_Report_" + ts + ".pdf";

        File outFile =
                new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS
                        ),
                        fileName
                );

        PrintAttributes attrs =
                new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(
                                new PrintAttributes.Resolution(
                                        "pdf", "pdf", 300, 300
                                )
                        )
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build();

        PrintDocumentAdapter adapter =
                webView.createPrintDocumentAdapter("GEL_Report");

        new PdfPrint(attrs).print(adapter, outFile, () -> {
            Toast.makeText(
                    this,
                    "PDF exported\n" + outFile.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    // ----------------------------------------------------------
    // PERMISSION RESULT
    // ----------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_WRITE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportPdf();
            } else {
                Toast.makeText(
                        this,
                        "Storage permission denied.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
