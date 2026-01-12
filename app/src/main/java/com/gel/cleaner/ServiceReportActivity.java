// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — TXT → PDF (FINAL HEADER + FOOTER EDITION)
// --------------------------------------------------------------

package com.gel.cleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.File;
import java.io.FileOutputStream;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int PAGE_WIDTH  = 595;  // A4
    private static final int PAGE_HEIGHT = 842;

    private TextView txtPreview;
    private Bitmap gelLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gelLogo = BitmapFactory.decodeResource(getResources(), R.drawable.gel_logo);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setBackgroundColor(0xFF101010);

        txtPreview = new TextView(this);
        txtPreview.setTextColor(0xFFFFFFFF);
        txtPreview.setTextSize(13f);
        txtPreview.setText(GELServiceLog.getAll());
        root.addView(txtPreview);

AppCompatButton btn = new AppCompatButton(this);
btn.setText("EXPORT PDF");
btn.setAllCaps(false);
btn.setTextColor(0xFFFFFFFF); // λευκά γράμματα
btn.setTextSize(14f);

// GEL style (μαύρο φόντο, χρυσό περίγραμμα)
btn.setBackgroundResource(R.drawable.gel_btn_outline_selector);

// layout params
LinearLayout.LayoutParams lpBtn =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
lpBtn.topMargin = 32;
btn.setLayoutParams(lpBtn);

// extra ασφάλεια (να μη χαθεί ποτέ)
btn.setVisibility(View.VISIBLE);
btn.setMinimumHeight(120);

btn.setOnClickListener(v -> exportTxtToPdf());

// πρόσθεσε το στο root
root.addView(btn);
}

    // ==========================================================
    // CORE — TXT → PDF
    // ==========================================================
    private void exportTxtToPdf() {

        if (GELServiceLog.isEmpty()) {
            Toast.makeText(this, "Nothing to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String text = GELServiceLog.getAll();
            String[] lines = text.split("\n");

            PdfDocument pdf = new PdfDocument();

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(12f);
            textPaint.setColor(Color.BLACK);

            Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            emojiPaint.setTextSize(12f);

            Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            titlePaint.setTextSize(14f);
            titlePaint.setColor(Color.BLACK);
            titlePaint.setFakeBoldText(true);

            Paint subtitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            subtitlePaint.setTextSize(11f);
            subtitlePaint.setColor(Color.BLACK);

            Paint footerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            footerPaint.setTextSize(11f);
            footerPaint.setColor(Color.BLACK);
            footerPaint.setFakeBoldText(true);

            int marginX = 32;
            int y;

            int lineHeight = 18;
            int pageNum = 1;

            PdfDocument.Page page = startPage(pdf, pageNum);
            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE);

            // HEADER πρώτη σελίδα
            y = drawReportHeader(canvas, marginX, 40, titlePaint, subtitlePaint, textPaint);

            for (String line : lines) {

                if (y > PAGE_HEIGHT - 80) {
                    pdf.finishPage(page);

                    pageNum++;
                    page = startPage(pdf, pageNum);
                    canvas = page.getCanvas();
                    canvas.drawColor(Color.WHITE);

                    y = drawReportHeader(canvas, marginX, 40, titlePaint, subtitlePaint, textPaint);
                }

                drawLineWithColoredEmoji(canvas, line, marginX, y, textPaint, emojiPaint);
                y += lineHeight;
            }

            // ==================================================
            // FOOTER — END OF REPORT + SIGNATURE
            // ==================================================
            if (y > PAGE_HEIGHT - 120) {
                pdf.finishPage(page);
                pageNum++;
                page = startPage(pdf, pageNum);
                canvas = page.getCanvas();
                canvas.drawColor(Color.WHITE);
                y = 80;
            }

            y += 40;
            canvas.drawText("— End of Report —", marginX, y, footerPaint);
            y += 30;
            canvas.drawText("Technician Signature: ________________________________", marginX, y, textPaint);

            pdf.finishPage(page);

            File outDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!outDir.exists()) outDir.mkdirs();

            String fileName = "GEL_Service_Report.pdf";
            File out = new File(outDir, fileName);

            FileOutputStream fos = new FileOutputStream(out);
            pdf.writeTo(fos);
            fos.close();
            pdf.close();

            Toast.makeText(this,
                    "PDF saved: Downloads/" + fileName,
                    Toast.LENGTH_LONG).show();

        } catch (Throwable t) {
            Toast.makeText(this,
                    "PDF error: " + t.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private PdfDocument.Page startPage(PdfDocument pdf, int num) {
        PdfDocument.PageInfo info =
                new PdfDocument.PageInfo.Builder(
                        PAGE_WIDTH, PAGE_HEIGHT, num).create();
        return pdf.startPage(info);
    }

    // ==========================================================
    // REPORT HEADER — CLEAN EDITION
    // ==========================================================
    private int drawReportHeader(
            Canvas c,
            int x,
            int startY,
            Paint title,
            Paint subtitle,
            Paint text) {

        int y = startY;

        // Logo LEFT (smaller)
        if (gelLogo != null) {
            Bitmap scaled = Bitmap.createScaledBitmap(gelLogo, 52, 52, true);
            c.drawBitmap(scaled, x, y - 8, null);
        }

        int textStartX = x + 70;

        // Title
        c.drawText("GEL Αναφορά Service", textStartX, y + 10, title);
        y += 20;

        // Subtitle
        c.drawText("GDiolitsis Engine Lab (GEL) — Author & Developer",
                textStartX, y + 10, subtitle);
        y += 26;

        // ❌ ΑΦΑΙΡΕΘΗΚΕ η μακριά γραμμή που ακουμπούσε δεξιά
        // (κρατάμε καθαρό header χωρίς border noise)

        return y + 10; // νέο Y για τα labs
    }

    // ==========================================================
    // DRAW LINE WITH COLORED EMOJI
    // ==========================================================
    private void drawLineWithColoredEmoji(
            Canvas canvas,
            String line,
            int x,
            int y,
            Paint textPaint,
            Paint emojiPaint) {

        if (line == null || line.isEmpty()) return;

        String emoji = null;
        String rest  = line;

        if (line.startsWith("ℹ")) { emoji = "ℹ"; emojiPaint.setColor(0xFF1E90FF); }
        else if (line.startsWith("✔")) { emoji = "✔"; emojiPaint.setColor(0xFF00AA00); }
        else if (line.startsWith("⚠")) { emoji = "⚠"; emojiPaint.setColor(0xFFFFA500); }
        else if (line.startsWith("✖")) { emoji = "✖"; emojiPaint.setColor(0xFFCC0000); }

        int dx = x;

        if (emoji != null) {
            canvas.drawText(emoji, dx, y, emojiPaint);
            dx += 18;
            rest = line.substring(1).trim();
        }

        canvas.drawText(rest, dx, y, textPaint);
    }
}
