package com.sharpdroid.registroelettronico.Views.Cells

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.View

import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Metodi

class ShadowCell(context: Context) : View(context) {

    private var size = 12

    init {
        setBackgroundDrawable(getThemedDrawable(context, R.drawable.greydivider))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(Metodi.dp(size), View.MeasureSpec.EXACTLY))
    }

    companion object {
        fun getThemedDrawable(context: Context, resId: Int): Drawable {
            val drawable = context.resources.getDrawable(resId).mutate()
            drawable.colorFilter = PorterDuffColorFilter(-0x1000000, PorterDuff.Mode.MULTIPLY)
            return drawable
        }
    }
}

