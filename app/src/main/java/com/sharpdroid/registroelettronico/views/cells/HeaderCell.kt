package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.utils.Metodi

class HeaderCell(context: Context) : FrameLayout(context) {

    private val textView: TextView = TextView(getContext())

    init {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        textView.setTextColor(ContextCompat.getColor(context, R.color.primary))
        textView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        textView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT.toFloat(), Gravity.START or Gravity.TOP, 17f, 15f, 17f, 0f))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(Metodi.dp(38), View.MeasureSpec.EXACTLY))
    }

    fun setText(text: String) {
        textView.text = text
    }
}
