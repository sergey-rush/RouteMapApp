package ru.routemapapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ScaleImageViewActivity extends Activity implements OnClickListener {

    ImageButton ibZoom;
    ImageButton ibDraw;
    ImageButton ibSave;

    public static ScaleImageView sivMain;
    public static boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_image_view);

        sivMain = (ScaleImageView) findViewById(R.id.sivMain);

        ibZoom =(ImageButton)findViewById(R.id.ibZoom);
        ibZoom.setOnClickListener(this);

        ibDraw =(ImageButton)findViewById(R.id.ibDraw);
        ibDraw.setOnClickListener(this);

        ibSave =(ImageButton)findViewById(R.id.ibSave);
        ibSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibZoom:
                onZoom(view);
                break;
            case R.id.ibDraw:
                onDraw(view);
                break;
            case R.id.ibSave:
                onSave(view);
                break;
        }
    }

    private void onZoom(View view){
        flag = false;
    }

    private void onDraw(View view){
        flag = true;
    }

    private void onSave(View view){
        ScaleImageView.save();
    }
}