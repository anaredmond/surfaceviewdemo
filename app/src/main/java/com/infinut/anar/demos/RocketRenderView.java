package com.infinut.anar.demos;

import android.content.Context;
import android.util.AttributeSet;

import com.infinut.anar.demos.framework.RenderView;

/**
 * Renders a rocket and rocket-burst.
 */
public class RocketRenderView extends RenderView {
    public RocketRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screen = new RocketScreen(context, touchHandler);
    }
}
