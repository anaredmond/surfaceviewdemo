package com.infinut.anar.demos;

import android.content.Context;
import android.util.AttributeSet;

import com.infinut.anar.demos.framework.RenderView;

/**
 * Launches the thread for rendering page turn animation
 */
public class PageTurnRenderView extends RenderView {
    public PageTurnRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screen = new PageTurnScreen(context, touchHandler);
    }
}