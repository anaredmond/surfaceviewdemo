package com.infinut.anar.demos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.infinut.anar.demos.framework.RenderView;

/**
 * Main page to launch the samples
 */
public class MainActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button rocket = (Button) findViewById(R.id.rocket);
        rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RocketActivity.class));
            }
        });

        Button pageturn = (Button) findViewById(R.id.pageturn);
        pageturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PageTurnActivity.class));
            }
        });
    }
}
