package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_lesson_2.view.*

class LessonCell(context: Context) : FrameLayout(context) {
    var data: Lesson? = null

    init {
        View.inflate(context, R.layout.adapter_lesson_2, this)
    }

    fun bindData(lesson: Lesson) {
        data = lesson
        content.text = lesson.mArgument
        circleImageView2.circleBackgroundColor = ContextCompat.getColor(context, R.color.primary)
        duration.text = "${lesson.mDuration}h"
        date.text = capitalizeEach(lesson.mSubjectDescription)
    }
}