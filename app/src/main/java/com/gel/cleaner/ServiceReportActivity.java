// GDiolitsis Engine Lab (GEL) — Author & Developer
// ServiceReportActivity — TXT → PDF (STABLE + HEADER + FOOTER + LOGO)
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.File;
import java.io.FileOutputStream;

public class ServiceReportActivity extends AppCompatActivity {

    private static final int PAGE_WIDTH  = 595;  // A4
    private static final int PAGE_HEIGHT = 842;

    private TextView txtPreview;

    // LOGO (βάλε το gel_logo.png στο res/drawable)
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
        btn.setText("Export PDF");
        btn.setOnClickListener(v -> exportTxtToPdf());
        root.addView(btn);

        scroll.addView(root);
        setContentView(scroll);
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
            titlePaint.setTextSize(13f);
            titlePaint.setColor(Color.BLACK);
            titlePaint.setFakeBoldText(true);

            int marginX = 32;
            int y = 40;
            int lineHeight = 18;

            int pageNum = 1;
            PdfDocument.Page page = startPage(pdf, pageNum);
            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE);

            // ─────────────────────────────
            // LOGO σε κάθε σελίδα
            // ─────────────────────────────
            drawLogo(canvas);

            // ─────────────────────────────
            // HEADER μόνο στην 1η σελίδα
            // ─────────────────────────────
            drawChecklistHeader(canvas, marginX, y, titlePaint);
            y += 160;

            for (String line : lines) {

                if (y > PAGE_HEIGHT - 80) {
                    pdf.finishPage(page);
                    pageNum++;
                    page = startPage(pdf, pageNum);
                    canvas = page.getCanvas();
                    canvas.drawColor(Color.WHITE);
                    drawLogo(canvas);
                    y = 40;
                }

                drawLineWithColoredEmoji(canvas, line, marginX, y, textPaint, emojiPaint);
                y += lineHeight;
            }

            // ─────────────────────────────
            // FOOTER στην τελευταία σελίδα
            // ─────────────────────────────
            drawSignatureFooter(canvas, marginX, PAGE_HEIGHT - 120, titlePaint, textPaint);

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
    // LOGO
    // ==========================================================
    private void drawLogo(Canvas canvas) {
        if (gelLogo == null) return;
        Bitmap scaled = Bitmap.createScaledBitmap(gelLogo, 80, 80, true);
        canvas.drawBitmap(scaled, PAGE_WIDTH - 100, 20, null);
    }

    // ==========================================================
    // HEADER — CHECKLIST
    // ==========================================================
    private void drawChecklistHeader(Canvas c, int x, int y, Paint title, Paint text) {

        c.drawText("ΕΛΕΓΧΟΣ ΣΗΜΕΙΩΝ", x, y, title);
        y += 20;

        String[] items = {
                "Σπασμένη οθόνη",
                "Dead Pixels",
                "AMOLED Burn-in",
                "Θύρα φόρτισης",
                "Ήχος / Ακουστικό",
                "Μικρόφωνο",
                "Μπαταρία",
                "Υγρασία / Διάβρωση"
        };

        for (String it : items) {
            c.drawText("[✔] " + it + "     [ ] ΟΧΙ", x, y, text);
            y += 16;
        }

        y += 10;
        c.drawText("--------------------------------------------", x, y, text);
    }

    // ==========================================================
    // FOOTER — SIGNATURE
    // ==========================================================
    private void drawSignatureFooter(Canvas c, int x, int y, Paint title, Paint text) {

        c.drawText("--------------------------------------------", x, y, text);
        y += 20;

        c.drawText("ΤΕΛΙΚΗ ΑΝΑΦΟΡΑ", x, y, title);
        y += 24;

        c.drawText("Τεχνικός:", x, y, text);
        y += 18;
        c.drawText("______________________________", x, y, text);
        y += 24;

        c.drawText("Υπογραφή:", x, y, text);
        y += 18;
        c.drawText("______________________________", x, y, text);
        y += 30;

        c.drawText("GDiolitsis Engine Lab (GEL)", x, y, title);
        y += 18;
        c.drawText("— Author & Developer", x, y, text);
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
