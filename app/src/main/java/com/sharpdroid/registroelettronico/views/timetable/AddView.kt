package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.sharpdroid.registroelettronico.R

class AddView : View {

    init {
        setBackgroundResource(R.drawable.add_view)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}