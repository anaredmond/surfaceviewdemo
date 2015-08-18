package com.infinut.anar.demos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Wrapper for a star shown when rocket bursts.
 */
public class Star {
    Bitmap bitmap;
    RectF bounds;

    //initial values
    float dirX, dirY; //velocity x, y
    float accelX, accelY; //acceleration x, y

    public void draw(Canvas canvas, Matrix matrix) {
        matrix.setTranslate(bounds.left, bounds.top);
        canvas.drawBitmap(bitmap, matrix, null);
    }
    public void update(float deltaTime) {
        dirX = dirX + accelX * deltaTime;
        dirY = dirY + accelY * deltaTime;

        bounds.offset(dirX * deltaTime, dirY * deltaTime);
    }
    public boolean isVisible(float screenWidth, float screenHeight) {
        if (bounds.right < 0 || bounds.left > screenWidth || bounds.top > screenHeight || bounds.bottom < 0) {
            return false;
        }
        return true;
    }
}
