package ru.routemapapp;

import android.animation.Animator;
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
import android.widget.Toast;

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
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void setRoutePath(){

        mRoutePath = new Path();
        mRoutePath.moveTo(220, 620);
        mRoutePath.lineTo(220, 595);
        mRoutePath.lineTo(100, 595);
        mRoutePath.lineTo(100, 495);
        mRoutePath.lineTo(350, 495);
        mRoutePath.lineTo(350, 450);
        mRoutePath.lineTo(100, 450);
        mRoutePath.lineTo(100, 315);
        mRoutePath.lineTo(190, 315);
        mRoutePath.lineTo(190, 270);
        mRoutePath.lineTo(100, 270);
        mRoutePath.lineTo(100, 200);
        mRoutePath.lineTo(100, 150);
        mRoutePath.lineTo(100, 55);
        mRoutePath.lineTo(320, 55);
        mRoutePath.lineTo(320, 270);
        mRoutePath.lineTo(475, 270);
        mRoutePath.lineTo(475, 290);
        mRoutePath.lineTo(530, 290);
        mRoutePath.lineTo(530, 540);
        mRoutePath.lineTo(585, 540);
        mRoutePath.lineTo(585, 290);
        mRoutePath.lineTo(585, 55);
        mRoutePath.lineTo(770, 55);
        mRoutePath.lineTo(770, 560);
        mRoutePath.lineTo(890, 560);
        mRoutePath.lineTo(890, 290);
        mRoutePath.lineTo(940, 290);
        mRoutePath.lineTo(940, 160);
        mRoutePath.lineTo(1110, 160);
        mRoutePath.lineTo(1110, 350);
        mRoutePath.lineTo(960, 350);
        mRoutePath.lineTo(960, 380);
        mRoutePath.lineTo(1110, 380);
        mRoutePath.lineTo(1110, 450);
        mRoutePath.lineTo(960, 450);
        mRoutePath.lineTo(960, 480);
        mRoutePath.lineTo(1110, 480);
        mRoutePath.lineTo(1110, 560);
        mRoutePath.lineTo(940, 560);
        mRoutePath.lineTo(940, 620);

        PathMeasure measure = new PathMeasure(mRoutePath, false);
        mPathLength = measure.getLength();

        ObjectAnimator animator = ObjectAnimator.ofFloat(PathView.this, "phase", 1.0f, 0.0f);
        animator.setDuration(8000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(context, "Маршрут построен", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
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