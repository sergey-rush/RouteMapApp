package ru.routemapapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by CanvasView on 04.03.2018.
 */

public class PathView extends View {

    private float mPathLength;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPenPaint;
    private Path mPenPath;
    Context context;
    private Path mRoutePath;
    private Paint mRoutePaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;



    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mPenPath = new Path();
        mPenPaint = new Paint();
        mPenPaint.setAntiAlias(true);
        mPenPaint.setColor(Color.RED);
        mPenPaint.setStyle(Paint.Style.STROKE);
        mPenPaint.setStrokeJoin(Paint.Join.ROUND);
        mPenPaint.setStrokeWidth(10);

        mRoutePath = new Path();
        mRoutePaint = new Paint();
        mRoutePaint.setColor(Color.BLUE);
        mRoutePaint.setStyle(Paint.Style.STROKE);
        mRoutePaint.setStrokeWidth(10);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }


    public void setRoutePath(){


        mRoutePath = new Path();
        mRoutePath.moveTo(50, 50);
        mRoutePath.lineTo(50, 500);
        mRoutePath.lineTo(200, 500);
        mRoutePath.lineTo(200, 300);
        mRoutePath.lineTo(350, 300);

        // Measure the path
        PathMeasure measure = new PathMeasure(mRoutePath, false);
        mPathLength = measure.getLength();

        //float[] intervals = new float[]{length, length};

        ObjectAnimator animator = ObjectAnimator.ofFloat(PathView.this, "phase", 1.0f, 0.0f);
        animator.setDuration(3000);
        animator.start();
    }

    //is called by animtor object
    public void setPhase(float phase)
    {
        Log.d("pathview","setPhase called with:" + String.valueOf(phase));
        mRoutePaint.setPathEffect(createPathEffect(mPathLength, phase, 0.0f));
        invalidate();//will calll onDraw
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset)
    {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }


    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPenPath with the mPenPaint on the canvas when onDraw
        canvas.drawPath(mPenPath, mPenPaint);
        canvas.drawPath(mRoutePath, mRoutePaint);

    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        mPenPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPenPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearCanvas() {
        mPenPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPenPath.lineTo(mX, mY);
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }
}