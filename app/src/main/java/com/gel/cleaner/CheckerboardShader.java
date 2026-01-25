package com.gel.cleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * CheckerboardShader
 * ------------------------------------------------------------
 * • Used for display uniformity / pixel structure inspection
 * • Helps detect banding, mura, burn-in shadows
 * • Pure Canvas / BitmapShader (no OpenGL)
 * • Safe for low-end devices
 */
public final class CheckerboardShader {

    private CheckerboardShader() {
        // no instances
    }

    /**
     * Create a checkerboard shader.
     *
     * @param cellSize size of each square in pixels (e.g. 20–60)
     */
    public static Shader create(int cellSize) {

        if (cellSize < 2) cellSize = 2;

        int size = cellSize * 2;

        Bitmap bmp = Bitmap.createBitmap(
                size,
                size,
                Bitmap.Config.ARGB_8888
        );

        Canvas c = new Canvas(bmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Dark / light contrast chosen to expose mura & burn-in
        int dark  = Color.rgb(30, 30, 30);
        int light = Color.rgb(225, 225, 225);

        // Top-left
        p.setColor(dark);
        c.drawRect(0, 0, cellSize, cellSize, p);

        // Top-right
        p.setColor(light);
        c.drawRect(cellSize, 0, size, cellSize, p);

        // Bottom-left
        p.setColor(light);
        c.drawRect(0, cellSize, cellSize, size, p);

        // Bottom-right
        p.setColor(dark);
        c.drawRect(cellSize, cellSize, size, size, p);

        return new BitmapShader(
                bmp,
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT
        );
    }
}
