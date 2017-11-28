package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.utils.Metodi.dp

class TimetableLayout : ViewGroup {
    var marginLeft = dp(16)
    var tileHeight = dp(60)
    var tileWidth = dp(20)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        println("layout")
        var hDividers = 0
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
                                hDividers++
                                layout(marginLeft, hDividers * tileHeight, measuredWidth + marginLeft, dp(1) + hDividers * tileHeight)
                            }
                        }

                    }
                }
            }

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("measure")
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeight = 23 * tileHeight

        tileWidth = (specWidth - marginLeft) / 6

        setMeasuredDimension(specWidth, specWidth)

        for (i in 0 until childCount) {
            with(getChildAt(i)) {
                when (this) {
                    is Divider ->
                        when (mode) {
                            Divider.VERTICAL -> measure(MeasureSpec.makeMeasureSpec(dp(1), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY))
                            Divider.HORIZONTAL -> measure(MeasureSpec.makeMeasureSpec(specWidth - marginLeft, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(dp(1), MeasureSpec.EXACTLY))
                        }
                }
            }
        }
    }

    init {
        for (i in 0 until 5) {
            addView(Divider(context, Divider.VERTICAL, true))
        }
        for (i in 0 until 24) {
            addView(Divider(context, Divider.HORIZONTAL, true))
        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}