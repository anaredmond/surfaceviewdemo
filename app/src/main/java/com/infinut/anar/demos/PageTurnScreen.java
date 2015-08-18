package com.infinut.anar.demos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import com.infinut.anar.demos.framework.PixelUtil;
import com.infinut.anar.demos.framework.Screen;
import com.infinut.anar.demos.framework.TouchHandler;

/**
 * Page Turn animation present / update
 */
public class PageTurnScreen extends Screen {
    public PageTurnScreen(Context context, TouchHandler touchHandler) {
        super(context, touchHandler);
    }

    private float scale;
    private float screenWidth;
    private float screenHeight;

    Matrix matrix;
    RectF location;

    //draw paper
    Paint paperbg;
    Paint outlinebg;
    Paint dashedbg;
    Paint curlEdgePaint;

    //curl transition
    PointF mA, mB, mC, mD, mE, mF;
    float mMovement, mVelocity;
    Path bgPath, curlPath;

    @Override
    public void init() {
        if (initialized.compareAndSet(false, true)) {
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();

            //init variables
            scale = PixelUtil.getScale(dm);
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;

            paperbg = new Paint();
            paperbg.setStyle(Paint.Style.FILL);
            paperbg.setColor(0xffffcc00);
            paperbg.setAntiAlias(true);
            paperbg.setDither(true);

            outlinebg = new Paint();
            outlinebg.setStyle(Paint.Style.STROKE);
            outlinebg.setColor(0xff555555);
            outlinebg.setStrokeCap(Paint.Cap.ROUND);
            outlinebg.setStrokeWidth(1 * scale);

            dashedbg = new Paint();
            dashedbg.setStyle(Paint.Style.STROKE);
            dashedbg.setColor(0xff555555);
            dashedbg.setStrokeCap(Paint.Cap.ROUND);
            dashedbg.setStrokeWidth(1 * scale);
            dashedbg.setPathEffect(new DashPathEffect(new float[]{4 * scale, 2 * scale}, 0));

            curlEdgePaint = new Paint();
            curlEdgePaint.setColor(0xffffe6d5);
            curlEdgePaint.setAntiAlias(true);
            curlEdgePaint.setStyle(Paint.Style.FILL);
            curlEdgePaint.setShadowLayer(5*scale, 5*scale, -5*scale, 0x66000000);

            bgPath = new Path();
            curlPath = new Path();
            matrix = new Matrix();

            //square page in center of screen
            float padx = (screenWidth - screenHeight) / 2;
            location = new RectF(padx, 0, padx + screenHeight, screenHeight);
        }
        startCurl();
    }

    @Override
    public void update(float deltaTime) {
        mVelocity += 5 * scale * deltaTime;
        mMovement += deltaTime * mVelocity * scale;
        doCurl();
    }

    @Override
    public void present(float deltaTime, Canvas canvas) {
        matrix.reset();
        Path mask = createBackgroundPath();
        // Save current canvas so we do not mess it up
        canvas.save();
        canvas.clipPath(mask);

        matrix.setTranslate(location.left, location.top);
        canvas.drawRect(location, paperbg);
        canvas.drawRect(location, outlinebg);

        for (int i = 1; i < 4; i++) {
            float bgy = (location.height() * i) / 4;
            canvas.drawLine(location.left, bgy, location.right, bgy, dashedbg);
        }

        canvas.restore();

        //draw edge
        Path path = createCurlEdgePath();
        canvas.drawPath(path, curlEdgePaint);
    }

    private Path createBackgroundPath() {
        bgPath.reset();
        bgPath.moveTo(mA.x, mA.y);
        bgPath.lineTo(mB.x, mB.y);
        bgPath.lineTo(mC.x, mC.y);
        bgPath.lineTo(mD.x, mD.y);
        bgPath.lineTo(mA.x, mA.y);
        return bgPath;
    }

    private Path createCurlEdgePath() {
        curlPath.reset();
        curlPath.moveTo(mA.x, mA.y);
        curlPath.lineTo(mF.x, mF.y);
        curlPath.lineTo(mE.x, mE.y);
        curlPath.lineTo(mD.x, mD.y);
        curlPath.lineTo(mA.x, mA.y);
        return curlPath;
    }

    private void startCurl() {
        float width = location.right - location.left;
        float height = location.bottom - location.top;
        mMovement = 0;
        mVelocity = 5 * scale;

        mA = new PointF(0, 0);
        mB = new PointF(0, 0);
        mC = new PointF(0, 0);
        mD = new PointF(0, 0);
        mE = new PointF(0, 0);
        mF = new PointF(0, 0);
        mA.x = location.left + width;
        mA.y = location.top + height;
        mC.x = location.left + 0.0f;
        mC.y = location.top + 0.0f;
        mB.x = location.left + width;
        mB.y = location.top + 0.0f;
        mD.x = location.left + 0;
        mD.y = location.top + height;
        mE.x = mD.x;
        mE.y = mD.y;
        mF.x = mA.x;
        mF.y = mA.y;
    }

    private void doCurl() {
        float width = location.right - location.left;
        float height = location.bottom - location.top;

        if (mMovement > 0.0f) {
            // Calculate point A
            mA.y = height - mMovement;
            mA.x = width;

            // Calculate point D
            mD.x = 0;
            mD.y = height - mMovement / 2;

            mE.x = mD.x;
            mE.y = mD.y;
            mF.x = mA.x;
            mF.y = mA.y;

            // Now calculate E and F. B and C are fixed points.
            double angle = Math.PI - 2*Math.atan(width*2 / mMovement);
            double _cos = Math.cos(angle);
            double _sin = Math.sin(angle);

            // And get F
            mF.x = (float) (mA.x - _sin * mMovement);
            mF.y = (float) (height - mMovement - _cos * mMovement);

            // So get E
            mE.x = (float) (mD.x - _sin * mMovement / 2);
            mE.y = (float) (height - mMovement / 2 - _cos * mMovement / 2);


            //adjust for location
            mA.x += location.left;
            mA.y += location.top;
            mD.x += location.left;
            mD.y += location.top;
            mE.x += location.left;
            mE.y += location.top;
            mF.x += location.left;
            mF.y += location.top;
        }
    }
}
