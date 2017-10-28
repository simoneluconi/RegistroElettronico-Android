package com.sharpdroid.registroelettronico.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.sharpdroid.registroelettronico.R

/**
 * A subclass of [android.view.View] class for creating a custom circular progressBar
 *
 *
 * Created by Pedram on 2015-01-06.
 * Modified by Luca Stefani on 2017-05-18
 */
class CircleProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    /**
     * ProgressBar's default values.
     */
    private var strokeWidth = 4f
    private var progress = 0f
    private var min = 0
    private var max = 100
    private var color = Color.DKGRAY

    private val rectF = RectF()
    lateinit private var mProgressPaint: Paint

    init {
        init(context, attrs)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun setColor(color: Int) {
        this.color = color
        mProgressPaint.color = color
        invalidate()
        requestLayout()
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CircleProgressBar,
                0, 0)
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth)
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress)
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color)
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min)
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max)
        } finally {
            typedArray.recycle()
        }

        mProgressPaint = object : Paint(Paint.ANTI_ALIAS_FLAG) {
            init {
                color = this@CircleProgressBar.color
                isDither = true
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                strokeWidth = this@CircleProgressBar.strokeWidth
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val angle = 360 * progress / max
        canvas.drawArc(rectF, 270f, -angle, false, mProgressPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2)
    }
}
