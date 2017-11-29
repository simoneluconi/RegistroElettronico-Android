package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.utils.Metodi.dp

class TimetableLayout : ViewGroup {
    private var marginLeft = dp(24)
    private var tileHeight = dp(70)
    private var tileWidth = dp(20)
    private val divider = dp(1)

    private val addView: AddView
    private val addViewMargin = dp(8)

    private val detector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                updateAddView(e.x, e.y)
                return true
            }
        })
    }


    init {
        //Vertical dividers
        for (i in 0 until 5) {
            addView(Divider(context, Divider.VERTICAL, true))
        }
        //Primary dividers w/TextViews
        for (i in 0 until 23) {
            addView(Divider(context, Divider.HORIZONTAL, true))

            val textHour = TextView(context)
            textHour.text = (i + 1).toString()
            textHour.id = i + 1
            textHour.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            textHour.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            addView(textHour)
        }
        //Secondary dividers
        for (i in 0 until 24) {
            addView(Divider(context, Divider.HORIZONTAL, false))
        }

        //AddView
        addView = AddView(context)
        addView.visibility = View.GONE
        addView(addView)

        setOnTouchListener { _, motionEvent ->
            detector.onTouchEvent(motionEvent)
            true
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var hDividers = 0
        var hDividersSecondary = 0
        var vDividers = 0

        for (i in 0 until childCount) {
            with(getChildAt(i)) {
                when (this) {
                    is Divider -> {
                        when (mode) {
                            Divider.VERTICAL -> {
                                vDividers++
                                layout(x(vDividers), 0, x(vDividers) + divider, measuredHeight)
                            }
                            Divider.HORIZONTAL -> {
                                if (thick) {
                                    hDividers++
                                    layout(x(0), y(hDividers), measuredWidth + marginLeft, y(hDividers) + divider)
                                } else {
                                    hDividersSecondary++
                                    layout(x(0), y(hDividersSecondary) - (tileHeight / 2), measuredWidth + marginLeft, y(hDividersSecondary) + divider - (tileHeight / 2))
                                }
                            }
                        }
                    }
                    is TextView -> {
                        if (id in 1 until 24) {
                            val height = y(id)
                            val hCenterPadding = (marginLeft - measuredWidth) / 2
                            layout(hCenterPadding, height - (measuredHeight / 2), measuredWidth + hCenterPadding, height + (measuredHeight / 2))
                        }
                    }
                    is AddView -> {
                        layout(0, 0, getMeasuredWidth(), getMeasuredHeight())
                    }
                }
            }

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeight = 24 * tileHeight
        tileWidth = (specWidth - marginLeft) / 6

        setMeasuredDimension(specWidth, specHeight)

        for (i in 0 until childCount) {
            with(getChildAt(i)) {
                when (this) {
                    is Divider ->
                        when (mode) {
                            Divider.VERTICAL -> measure(MeasureSpec.makeMeasureSpec(divider, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY))
                            Divider.HORIZONTAL -> measure(MeasureSpec.makeMeasureSpec(specWidth - marginLeft, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(divider, MeasureSpec.EXACTLY))
                        }
                    is TextView ->
                        measure(MeasureSpec.getSize(getWidth()), MeasureSpec.getSize(getHeight()))
                    is AddView ->
                        measure(MeasureSpec.makeMeasureSpec(tileWidth - divider - addViewMargin, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(tileHeight - divider - addViewMargin, MeasureSpec.EXACTLY))
                }
            }
        }
    }

    private fun updateAddView(x: Float, y: Float) {
        val column = column(x)
        val row = row(y)

        with(addView) {
            visibility = View.VISIBLE
            translationX = (marginLeft + tileWidth * column + divider + addViewMargin / 2).toFloat()
            translationY = (tileHeight * row + divider + addViewMargin / 2).toFloat()
            setOnClickListener {
                visibility = View.GONE
                //startActivity
            }
        }

        bringChildToFront(addView)
    }

    private fun x(columnIndex: Int): Int = marginLeft + tileWidth * columnIndex
    private fun y(rowIndex: Int): Int = tileHeight * rowIndex
    private fun column(x: Float): Int = Math.floor(Math.max(x - marginLeft, 0f) / tileWidth.toDouble()).toInt()
    private fun row(y: Float): Int = Math.floor(y / tileHeight.toDouble()).toInt()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}