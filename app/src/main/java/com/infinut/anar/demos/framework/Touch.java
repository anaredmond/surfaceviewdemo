package com.infinut.anar.demos.framework;

/**
 * Store and pass the touch event from the render thread to the screen responsible for handling it.
 */
public class Touch {
    public static final int TOUCH_DOWN = 0;
    public static final int TOUCH_UP = 1;
    public static final int TOUCH_MOVE = 2;

    public volatile int type;
    public volatile float x, y;
    public volatile int pointer;

}
