package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Lesson
import kotlinx.android.synthetic.main.adapter_lessons_1.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class LessonCellMini : FrameLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val formatter = SimpleDateFormat("d MMM", Locale.getDefault())

    init {
        View.inflate(context, R.layout.adapter_lessons_1, this)
    }

    fun bindData(data: Lesson) {
        content.text = Html.fromHtml(data.mArgument)
        date.text = formatter.format(data.mDate)
    }
}
