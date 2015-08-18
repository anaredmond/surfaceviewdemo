package com.infinut.anar.demos.framework;

import android.util.DisplayMetrics;

/**
 * Defines a scale factor to use for scaling what's displayed
 */
public class PixelUtil {
    public static float getScale(DisplayMetrics dm) {
        int x = dm.widthPixels;
        int y = dm.heightPixels;
        if (x < y) {
            float xr = dm.widthPixels / 320.0f;
            float yr = dm.heightPixels / 480.0f;
            return Math.min(xr, yr);
        } else {//landscape
            float xr = dm.widthPixels / 480.0f;
            float yr = dm.heightPixels / 320.0f;
            return Math.min(xr, yr);
        }
    }

}
