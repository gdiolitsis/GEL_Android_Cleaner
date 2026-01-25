package com.gel.cleaner;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public final class DisplayPatterns {

    public static GradientDrawable makeGradient() {
        GradientDrawable g = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF000000, 0xFFFFFFFF}
        );
        g.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        return g;
    }

    public static ShapeDrawable makeCheckerboard() {
        ShapeDrawable d = new ShapeDrawable(new RectShape());
        d.getPaint().setShader(
                CheckerboardShader.create(40)
        );
        return d;
    }

    public static GradientDrawable makeBurnInCycle() {
        GradientDrawable g = new GradientDrawable();
        g.setColors(new int[]{
                0xFF000000,
                0xFF111111,
                0xFF222222,
                0xFF000000
        });
        g.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        g.setGradientRadius(800f);
        return g;
    }
}
