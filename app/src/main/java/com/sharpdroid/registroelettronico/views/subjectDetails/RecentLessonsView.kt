package com.sharpdroid.registroelettronico.views.subjectDetails

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.AllLessonsWithDownloadActivity
import com.sharpdroid.registroelettronico.adapters.LessonsAdapter
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Metodi
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.view_recent_lessons.view.*

class RecentLessonsView : CardView {
    internal val adapter: LessonsAdapter

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        View.inflate(context, R.layout.view_recent_lessons, this)

        adapter = LessonsAdapter(context)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).marginResId(R.dimen.activity_vertical_margin, R.dimen.activity_vertical_margin).size(Metodi.dp(1)).build())
        recycler.adapter = adapter
    }

    fun update(lesson: List<Lesson>, code: Int) {
        if (lesson.isNotEmpty()) {
            adapter.clear()
            adapter.addAll(lesson)
        }

        load_more.setOnClickListener { context.startActivity(Intent(context, AllLessonsWithDownloadActivity::class.java).putExtra("code", code)) }
    }

    fun clear() {
        adapter.clear()
    }
}
