package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_lesson_2.view.*
import java.text.SimpleDateFormat
import java.util.*

class LessonCell(context: Context) : FrameLayout(context) {
    private val formatter = SimpleDateFormat("d MMM", Locale.ITALIAN)
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec * (data?.mDuration ?: 1))
    }
}