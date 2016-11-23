package net.ganin.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class TestPathRenderer extends View {

    public static final int RENDER_MODE_WO_BITMAP = 0;
    public static final int RENDER_MODE_WITH_BITMAP = 1;

    private static final float DISTORTION_FACTOR = 7f;

    private final Paint paint = new Paint();

    private final Path path = new Path();

    @Nullable
    private Bitmap bitmap;

    private int renderMode = RENDER_MODE_WO_BITMAP;

    public TestPathRenderer(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TestPathRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TestPathRenderer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TestPathRenderer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TestPathRenderer, defStyleAttr, defStyleRes);
            renderMode = ta.getInt(R.styleable.TestPathRenderer_render_mode, renderMode);
            ta.recycle();
        }

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(50f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        float distortedWidth = getDistortedWidth();
        float distortedHeight = getDistortedHeight();

        path.reset();
        path.addCircle(distortedWidth/2f, distortedHeight/2f, Math.min(distortedWidth/2f, distortedHeight/2f), Path.Direction.CW);

        bitmap = assembleNewBitmap(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (renderMode) {
            case RENDER_MODE_WO_BITMAP:
                drawOnCanvas(canvas, getDistortedWidth(), getDistortedHeight());
                break;
            case RENDER_MODE_WITH_BITMAP:
                if (bitmap == null) {
                    throw new IllegalStateException("Bitmap cannot be null");
                }
                canvas.drawBitmap(bitmap, 0f, 0f, paint);
                break;
            default:
                throw new UnsupportedOperationException("Undefined render mode: " + renderMode);
        }
    }

    private Bitmap assembleNewBitmap(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawOnCanvas(canvas, w, h);
        return bitmap;
    }

    private void drawOnCanvas(@NonNull Canvas canvas, float w, float h) {
        canvas.save();
        canvas.scale(DISTORTION_FACTOR, DISTORTION_FACTOR);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    private float getDistortedWidth() {
        return getMeasuredWidth() / DISTORTION_FACTOR;
    }

    private float getDistortedHeight() {
        return getMeasuredHeight() / DISTORTION_FACTOR;
    }
}
