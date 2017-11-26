package com.sharpdroid.registroelettronico.views

import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.placeholder_calendar_day.view.*

class EmptyFragment(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : RelativeLayout(context, attributeSet, defStyleAttr) {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    init {
        inflate(context, R.layout.placeholder_calendar_day, this)
    }

    fun setTextAndDrawable(text: String, @DrawableRes drawableRes: Int) {
        place_holder_image.setImageResource(drawableRes)
        place_holder_image.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
        place_holder_text.text = text
    }
}