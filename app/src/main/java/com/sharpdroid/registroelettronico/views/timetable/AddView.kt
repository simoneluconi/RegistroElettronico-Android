package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

class AddView : View {

    init {
        setBackgroundColor(Color.RED)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}