package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.isLessonTest
import kotlinx.android.synthetic.main.adapter_lesson_2.view.*

class LessonCell(context: Context) : FrameLayout(context) {
    var data: Lesson? = null

    init {
        View.inflate(context, R.layout.adapter_lesson_2, this)
    }

    fun bindData(lesson: Lesson) {
        data = lesson
        if (lesson.mArgument.isBlank()) {
            content.visibility = View.GONE
        } else {
            content.text = lesson.mArgument
        }
        val color = if (isLessonTest(lesson)) R.color.deep_orange else R.color.light_green
        circleImageView2.circleBackgroundColor = ContextCompat.getColor(context, color)
        duration.text = "${lesson.mDuration}h"
        date.text = capitalizeEach(lesson.mSubjectDescription)
    }
}