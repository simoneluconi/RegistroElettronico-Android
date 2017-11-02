package com.sharpdroid.registroelettronico.views.subjectDetails

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import butterknife.ButterKnife
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.AllLessonsWithDownloadActivity
import com.sharpdroid.registroelettronico.adapters.LessonsAdapter
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Metodi
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.view_recent_lessons.view.*

class RecentLessonsView : CardView {
    internal var mContext: Context

    internal lateinit var adapter: LessonsAdapter

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        init()
    }

    private fun init() {
        View.inflate(mContext, R.layout.view_recent_lessons, this)
        ButterKnife.bind(this)

        adapter = LessonsAdapter(mContext)
        recycler.layoutManager = LinearLayoutManager(mContext)
        recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(mContext).colorResId(R.color.divider).marginResId(R.dimen.activity_vertical_margin, R.dimen.activity_vertical_margin).size(Metodi.dp(1)).build())
        recycler.adapter = adapter
    }

    fun update(code: Int) {
        adapter.clear()
        adapter.addAll(DatabaseHelper.database.lessonsDao().loadLastLessons(code.toLong()))
        load_more.setOnClickListener { mContext.startActivity(Intent(mContext, AllLessonsWithDownloadActivity::class.java).putExtra("code", code)) }
    }

    fun clear() {
        adapter.clear()
    }
}
