package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.utils.Metodi.dp

class SingleTimeLayout : TextView {
    private val padding = dp(4)

    init {
        setTextColor(Color.WHITE)
        setPadding(padding, padding, padding, padding)
        text = "Storia"
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this, 4, 18, 1, TypedValue.COMPLEX_UNIT_SP)
        setBackgroundResource(R.drawable.timetable_item)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}