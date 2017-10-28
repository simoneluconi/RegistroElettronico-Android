package com.sharpdroid.registroelettronico.Views.Cells

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.Databases.Entities.Lesson
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_lessons_1.view.*
import java.text.SimpleDateFormat
import java.util.*

class LessonCell(context: Context) : FrameLayout(context) {
    private val formatter = SimpleDateFormat("d MMM", Locale.ITALIAN)
    var data: Lesson? = null

    init {
        View.inflate(context, R.layout.adapter_lessons_1, this)
    }

    fun bindData(lesson: Lesson) {
        data = lesson
        content.text = lesson.mArgument
        date.text = capitalizeEach(lesson.mSubjectDescription)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec * (data?.mDuration ?: 1))
    }
}