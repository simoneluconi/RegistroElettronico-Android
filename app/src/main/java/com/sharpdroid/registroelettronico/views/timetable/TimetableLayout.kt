package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.utils.Metodi.dp

class TimetableLayout : ViewGroup {
    private var marginLeft = dp(24)
    private var tileHeight = dp(70)
    private var tileWidth = dp(20)

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
                                layout(vDividers * tileWidth + marginLeft, 0, dp(1) + vDividers * tileWidth + marginLeft, measuredHeight)
                            }
                            Divider.HORIZONTAL -> {
                                if (thick) {
                                    hDividers++
                                    layout(marginLeft, hDividers * tileHeight, measuredWidth + marginLeft, dp(1) + hDividers * tileHeight)
                                } else {
                                    hDividersSecondary++
                                    layout(marginLeft, hDividersSecondary * tileHeight - tileHeight / 2, measuredWidth + marginLeft, hDividersSecondary * tileHeight - tileHeight / 2 + dp(1))
                                }
                            }
                        }
                    }
                    is TextView -> {
                        if (id in 1 until 24) {
                            val height = id * tileHeight
                            val hCenterPadding = (marginLeft - measuredWidth) / 2
                            layout(hCenterPadding, height - (measuredHeight / 2), measuredWidth + hCenterPadding, height + (measuredHeight / 2))
                        }
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
                            Divider.VERTICAL -> measure(MeasureSpec.makeMeasureSpec(dp(1), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY))
                            Divider.HORIZONTAL -> measure(MeasureSpec.makeMeasureSpec(specWidth - marginLeft, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dp(1), MeasureSpec.EXACTLY))
                        }
                    is TextView ->
                        measure(MeasureSpec.getSize(getWidth()), MeasureSpec.getSize(getHeight()))
                }
            }
        }
    }

    init {
        //Vertical dividers
        for (i in 0 until 5) {
            addView(Divider(context, Divider.VERTICAL, true))
        }
        //Primary dividers w/ TextViews
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
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}