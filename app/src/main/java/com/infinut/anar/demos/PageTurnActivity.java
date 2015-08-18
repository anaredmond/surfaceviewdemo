package com.infinut.anar.demos;

import android.app.Activity;
import android.os.Bundle;

import com.infinut.anar.demos.framework.RenderView;

/**
 * A sample Activity showing the launch of a Surface View for a page turn animation.
 */
public class PageTurnActivity extends Activity {
    RenderView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_page_turn);
        view = (RenderView) findViewById(R.id.mainview);
    }

    protected void onPause() {
        super.onPause();
        view.pause();
    }

    protected void onResume() {
        super.onResume();
        view.resume();
    }
}
