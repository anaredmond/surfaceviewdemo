package com.infinut.anar.demos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import com.infinut.anar.demos.framework.ImageUtil;
import com.infinut.anar.demos.framework.PixelUtil;
import com.infinut.anar.demos.framework.Screen;
import com.infinut.anar.demos.framework.Touch;
import com.infinut.anar.demos.framework.TouchHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Is where the work happens. Update and Present(draw) methods.
 */
public class RocketScreen extends Screen {

    private float scale;
    private float screenWidth;
    private float screenHeight;
    private Matrix matrix;

    private Bitmap rocket;
    //rocket location
    private float speedX;
    private float speedY;
    private float accelX;
    private float accelY;
    private float rotate;
    private RectF bounds;
    private boolean burst;

    //burst
    Random random;
    List<Star> stars;
    Bitmap[] starBitmaps;

    public RocketScreen(Context context, TouchHandler touchHandler) {
        super(context, touchHandler);
    }

    public void init() {
        if (initialized.compareAndSet(false, true)) {
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();

            //init variables
            scale = PixelUtil.getScale(dm);
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            matrix = new Matrix();

            //load image
            int imageSize = (int) (80 * scale);
            rocket = ImageUtil.loadImage(res, R.drawable.rocket_effect, imageSize, imageSize);
            bounds = new RectF();
            startRocket();

            //init burst
            random = new Random();
            stars = new ArrayList<Star>();
            loadStars(res);
        }
        if (restartAnimation()) {
            startRocket();
        }
    }

    private void loadStars(Resources res) {
        starBitmaps = new Bitmap[7];
        int bs = Math.round(40 * scale);

        starBitmaps[0] = ImageUtil.loadImage(res, R.drawable.star_blue, bs, bs);
        starBitmaps[1] = ImageUtil.loadImage(res, R.drawable.star_green, bs, bs);
        starBitmaps[2] = ImageUtil.loadImage(res, R.drawable.star_orange, bs, bs);
        starBitmaps[3] = ImageUtil.loadImage(res, R.drawable.star_pink, bs, bs);
        starBitmaps[4] = ImageUtil.loadImage(res, R.drawable.star_purple, bs, bs);
        starBitmaps[5] = ImageUtil.loadImage(res, R.drawable.star_red, bs, bs);
        starBitmaps[6] = ImageUtil.loadImage(res, R.drawable.star_yellow, bs, bs);
    }

    private boolean restartAnimation() {
        //rocket out of screen
        if (!burst) {
            if (bounds.right < 0 || bounds.left > screenWidth || bounds.top > screenHeight || bounds.bottom < 0) {
                return true;
            }
        }
        //all stars out of screen
        if(burst) {
            for (Star star : stars) {
                if (star.isVisible(screenWidth, screenHeight)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private void startRocket() {
        float radians = (float) Math.toRadians(60);
        float speed = 100 * scale;
        speedX = (float) (Math.cos(radians) * speed);
        speedY = (float) (Math.sin(radians) * speed * -1);
        accelX = 0;
        accelY = 20 * scale; //accelY is gravity

        float imageSize = rocket.getHeight();
        bounds.set(0, screenHeight - imageSize, imageSize, screenHeight);
        burst = false;
    }

    public void update(float deltaTime) {
        List<Touch> events = touchHandler.getTouchEvents();
        for (Touch event : events) {
            if (bounds.contains(event.x, event.y)) {
                startBurst();
                break;
            }
        }

        if (!burst) {
            speedX += accelX * deltaTime;
            speedY += accelY * deltaTime;
            bounds.offset(speedX * deltaTime, speedY * deltaTime);

            //rotate on tangent to path
            rotate = (float) Math.toDegrees(Math.atan2(speedY, speedX));
        } else {
            for(Star star: stars) {
                star.update(deltaTime);
            }
        }
    }

    private void startBurst() {
        stars.clear();
        for (int i = 0; i < 10; i++) {
            Star star = new Star();
            Bitmap bitmap = starBitmaps[i % starBitmaps.length];
            int size = bitmap.getWidth();
            star.bounds = new RectF(bounds.centerX() - (size) / 2, bounds.centerY() - (size) / 2, bounds.centerX() + (size) / 2, bounds.centerY() + (size) / 2);
            star.bitmap = bitmap;

            star.dirX = (random.nextInt(200) - 100) * scale; //between -50 and 50
            star.dirY = -(random.nextInt(200) - 100) * scale; //between -50 and 50
            star.accelX = 0.0f;
            star.accelY = 20 * scale; //accelY is gravity
            stars.add(star);
        }
        burst = true;
    }

    public void present(float deltaTime, Canvas canvas) {
        canvas.drawColor(0xFFC6E9AF);
        if (!burst) {
            matrix.setTranslate(bounds.left, bounds.top);
            matrix.postRotate(rotate, bounds.centerX(), bounds.centerY());

            canvas.drawBitmap(rocket, matrix, null);
        } else {
            for (Star star : stars) {
                star.draw(canvas, matrix);
            }
        }
    }
}
