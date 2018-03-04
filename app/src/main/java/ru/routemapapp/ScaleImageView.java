package ru.routemapapp;

//CUSTOM IMAGEVIEW
import java.io.ByteArrayOutputStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.support.v7.widget.AppCompatImageView;


/**
 * Created by Admin on 04.03.2018.
 */
public class ScaleImageView extends AppCompatImageView implements OnTouchListener {
    
    static final float STROKE_WIDTH = 10f;
    static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private float MAX_SCALE = 2f;

    float lastTouchX;
    float lastTouchY;
    final RectF dirtyRect = new RectF();

    private Context context; 

    private static Matrix matrix;
    private final float[] matrixValues = new float[9];

    // display width height.
    private int displayWidth;
    private int displayHeight;

    private int innerWidth;
    private int innerHeight;

    private float scale;
    private float minScale;

    private float prevDistance;
    private boolean isScaling;

    private int prevMoveX;
    private int prevMoveY;
    private GestureDetector gestureDetector;

    private Paint penPaint = new Paint();
    public static Path penPath = new Path();

    public static int imageHeight;
    public static int imageWidth;

    private String TAG = "ScaleImageView";

    public ScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        onInit();
    }

    public ScaleImageView(Context context) {
        super(context);
        this.context = context;
        onInit();
    }    

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        onInit();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        onInit();
    }

    private void onInit() {
        setScaleType(ScaleType.MATRIX);
        matrix = new Matrix();
        Drawable drawable = getDrawable();

        penPaint.setAntiAlias(true);
        penPaint.setColor(Color.RED);
        penPaint.setStyle(Paint.Style.STROKE);
        penPaint.setStrokeJoin(Paint.Join.ROUND);
        penPaint.setStrokeWidth(STROKE_WIDTH);

        if (drawable != null) {
            innerWidth = drawable.getIntrinsicWidth();
            innerHeight = drawable.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        maxZoomTo((int) e.getX(), (int) e.getY());
                        cutting();
                        return super.onDoubleTap(e);
                    }
                });

    }


    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        displayWidth = r - l;
        displayHeight = b - t;

        matrix.reset();
        int r_norm = r - l;
        scale = (float) r_norm / (float) innerWidth;

        int paddingHeight = 0;
        int paddingWidth = 0;

        if (scale * innerHeight > displayHeight) {// scaling vertical
            scale = (float) displayHeight / (float) innerHeight;
            matrix.postScale(scale, scale);
            paddingWidth = (r - displayWidth) / 2;
            paddingHeight = 0;

        } else {// scaling horizontal
            matrix.postScale(scale, scale);
            paddingHeight = (b - displayHeight) / 2;
            paddingWidth = 0;
        }
        matrix.postTranslate(paddingWidth, paddingHeight);

        setImageMatrix(matrix);
        minScale = scale;
        zoomTo(scale, displayWidth / 2, displayHeight / 2);
        cutting();
        return super.setFrame(l, t, r, b);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(matrixValues);
        return matrixValues[whichValue];
    }

    protected float getScale() {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    public float getTranslateX() {
        return getValue(matrix, Matrix.MTRANS_X);
    }

    protected float getTranslateY() {
        return getValue(matrix, Matrix.MTRANS_Y);
    }

    protected void maxZoomTo(int x, int y) {
        if (minScale != getScale() && (getScale() - minScale) > 0.1f) {
            // threshold 0.1f
            float scale = minScale / getScale();
            zoomTo(scale, x, y);
        } else {
            float scale = MAX_SCALE / getScale();
            zoomTo(scale, x, y);
        }
    }

    public void zoomTo(float scale, int x, int y) {
        if (getScale() * scale < minScale) {
            return;
        }
        if (scale >= 1 && getScale() * scale > MAX_SCALE) {
            return;
        }
        matrix.postScale(scale, scale);
        // move to center
        matrix.postTranslate(-(displayWidth * scale - displayWidth) / 2,-(displayHeight * scale - displayHeight) / 2);
        // move x and y distance
        matrix.postTranslate(-(x - (displayWidth / 2)) * scale, 0);
        matrix.postTranslate(0, -(y - (displayHeight / 2)) * scale);
        setImageMatrix(matrix);
    }

    public void cutting() {
        int width = (int) (innerWidth * getScale());
        int height = (int) (innerHeight * getScale());

        imageWidth = width;
        imageHeight = height;

        if (getTranslateX() < -(width - displayWidth)) {
            matrix.postTranslate(-(getTranslateX() + width - displayWidth), 0);
        }
        if (getTranslateX() > 0) {
            matrix.postTranslate(-getTranslateX(), 0);
        }
        if (getTranslateY() < -(height - displayHeight)) {
            matrix.postTranslate(0, -(getTranslateY() + height - displayHeight));
        }
        if (getTranslateY() > 0) {
            matrix.postTranslate(0, -getTranslateY());
        }
        if (width < displayWidth) {
            matrix.postTranslate((displayWidth - width) / 2, 0);
        }
        if (height < displayHeight) {
            matrix.postTranslate(0, (displayHeight - height) / 2);
        }
        setImageMatrix(matrix);
    }

    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float)Math.sqrt(x * x + y * y);
    }

    private float dispDistance() {
        return (float)Math.sqrt(displayWidth * displayWidth + displayHeight * displayHeight);
    }

    public void clear() {
        penPath.reset();
        invalidate();
    }

    public static void save() {

        Bitmap returnedBitmap = Bitmap.createBitmap(
                ScaleImageViewActivity.sivMain.getWidth(),
                ScaleImageViewActivity.sivMain.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);

        Drawable bgDrawable = ScaleImageViewActivity.sivMain.getDrawable();

        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);

        ScaleImageViewActivity.sivMain.draw(canvas);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);

        Bitmap FinalBitmap = BitmapFactory.decodeByteArray(bs.toByteArray(), 0,
                bs.toByteArray().length);

        ScaleImageViewActivity.sivMain.setImageBitmap(FinalBitmap);
        penPath.reset();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!ScaleImageViewActivity.flag) {

            if (gestureDetector.onTouchEvent(event)) {
                return true;
            }
            int touchCount = event.getPointerCount();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_1_DOWN:
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    if (touchCount >= 2) {
                        float distance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                        prevDistance = distance;
                        isScaling = true;
                    } else {
                        prevMoveX = (int) event.getX();
                        prevMoveY = (int) event.getY();
                    }
                case MotionEvent.ACTION_MOVE:
                    if (touchCount >= 2 && isScaling) {
                        float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                        float scale = (dist - prevDistance) / dispDistance();
                        prevDistance = dist;
                        scale += 1;
                        scale = scale * scale;
                        zoomTo(scale, displayWidth / 2, displayHeight / 2);
                        cutting();
                    } else if (!isScaling) {
                        int distanceX = prevMoveX - (int) event.getX();
                        int distanceY = prevMoveY - (int) event.getY();
                        prevMoveX = (int) event.getX();
                        prevMoveY = (int) event.getY();
                        matrix.postTranslate(-distanceX, -distanceY);
                        cutting();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_POINTER_2_UP:
                    if (event.getPointerCount() <= 1) {
                        isScaling = false;
                    }
                    break;
            }
        } else {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    penPath.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        penPath.lineTo(historicalX, historicalY);
                    }
                    penPath.lineTo(eventX, eventY);
                    break;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;
        }
        return true;
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ScaleImageViewActivity.flag)
            canvas.drawPath(penPath, penPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }
}