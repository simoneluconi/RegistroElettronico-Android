package com.sharpdroid.registroelettronico.Views

import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.widget.RelativeLayout
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.placeholder_calendar_day.view.*

class EmptyFragment(context: Context) : RelativeLayout(context) {
    init {
        inflate(context, R.layout.placeholder_calendar_day, this)
    }

    fun setTextAndDrawable(text: String, @DrawableRes drawableRes: Int) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        drawable?.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
        place_holder_image.setImageDrawable(drawable)
        place_holder_text.text = text
    }
}