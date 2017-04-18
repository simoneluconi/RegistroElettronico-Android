package com.sharpdroid.registroelettronico.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sharpdroid.registroelettronico.R;

/**
 * A subclass of {@link android.view.View} class for creating a custom circular progressBar
 * <p>
 * Created by Pedram on 2015-01-06.
 * Modified by Luca Stefani on 2017-05-18
 */
public class CircleProgressBar extends View {

    /**
     * ProgressBar's default values.
     */
    private float strokeWidth = 4;
    private float progress = 0;
    private int min = 0;
    private int max = 100;
    private int color = Color.DKGRAY;

    private RectF rectF;
    private Paint mProgressPaint;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        mProgressPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0);
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress);
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color);
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min);
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max);
        } finally {
            typedArray.recycle();
        }

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
            {
                setColor(color);
                setDither(true);
                setStyle(Style.STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeJoin(Paint.Join.ROUND);
                setStrokeWidth(strokeWidth);
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float angle = 360 * progress / max;
        canvas.drawArc(rectF, 270, -angle, false, mProgressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
    }
}
