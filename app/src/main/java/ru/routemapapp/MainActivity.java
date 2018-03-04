package ru.routemapapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class MainActivity extends Activity {
    PathView pathView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pathView = (PathView) findViewById(R.id.path_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pathView.setBackground(getResources().getDrawable(R.drawable.scheme));
        }

    }

    public void clearCanvas(View view) {
        pathView.clearCanvas();
    }

    public void setRoute(View view) {
        pathView.setRoutePath();
    }
}
