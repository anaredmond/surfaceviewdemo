package com.infinut.anar.demos.framework;

import android.content.Context;
import android.graphics.Canvas;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Screens (derived from this class) do the work of showing the animation(s)
 */
public abstract class Screen {
    protected AtomicBoolean initialized;
    protected TouchHandler touchHandler;
    protected Context context;

    public Screen(Context context, TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
        this.context = context;
        initialized = new AtomicBoolean(false);
    }
    public abstract void init();

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime, Canvas canvas);

    public void draw() {

    }
}
