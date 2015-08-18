package com.infinut.anar.demos.framework;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles touch input using a Pooled object queue.
 */
public class TouchHandler {

    Pool<Touch> touchEventPool;
    List<Touch> touchEvents = new ArrayList<Touch>();
    List<Touch> touchEventsBuffer = new ArrayList<Touch>();

    public TouchHandler() {
        Pool.PoolObjectFactory<Touch> factory = new Pool.PoolObjectFactory<Touch>() {

            public Touch createObject() {
                return new Touch();
            }
        };
        touchEventPool = new Pool<Touch>(factory, 100);
    }

    public boolean onTouch(View v, MotionEvent event) {
        //add touch events to the buffer
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                addEvent(Touch.TOUCH_DOWN, pointerId, event.getX(pointerIndex), event.getY(pointerIndex));
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                addEvent(Touch.TOUCH_UP, pointerId, event.getX(pointerIndex), event.getY(pointerIndex));
                break;

            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    pointerIndex = i;
                    pointerId = event.getPointerId(pointerIndex);
                    addEvent(Touch.TOUCH_MOVE, pointerId, event.getX(pointerIndex), event.getY(pointerIndex));
                }
                break;
        }
        return true;

    }

    private void addEvent(int type, int pointerId, float x, float y) {
        synchronized (this) {
            Touch touchEvent;
            touchEvent = touchEventPool.newObject();
            touchEvent.type = type;
            touchEvent.pointer = pointerId;
            touchEvent.x = x;
            touchEvent.y = y;
            touchEventsBuffer.add(touchEvent);
        }
    }

    public List<Touch> getTouchEvents() {
        synchronized (this) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++)
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }
}
