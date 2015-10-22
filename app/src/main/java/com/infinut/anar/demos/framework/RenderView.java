package com.infinut.anar.demos.framework;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a rendering thread.
 */
public abstract class RenderView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    Thread renderThread = null;
    SurfaceHolder holder;
    protected Screen screen;
    protected TouchHandler touchHandler;
    AtomicBoolean running = new AtomicBoolean();
    AtomicBoolean render = new AtomicBoolean();

    public RenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        touchHandler = new TouchHandler();
        running.set(false);
        render.set(false);
    }

    public void resume() {
        if (renderThread == null) {
            running.set(true);
            renderThread = new Thread(this);
            renderThread.start();
        }
    }

    public void run() {
        while(!render.get()) //check can render first.
            continue;
        //surface has been initialized
        screen.init();
        long startTime = System.nanoTime();
        while (running.get()) {
            if(!render.get())
                continue;
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            screen.update(deltaTime);

            Canvas canvas = holder.lockCanvas();
            canvas.drawRGB(0, 0, 0);
            screen.present(deltaTime, canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        running.set(false);
        if (renderThread != null) {
            boolean retry = true;
            while (retry) {
                try {
                    renderThread.join();
                    renderThread = null;
                    retry = false;
                } catch (InterruptedException e) {
                    //retry
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return touchHandler.onTouch(this, event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        render.set(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        render.compareAndSet(false, true);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        render.set(false);
    }
}