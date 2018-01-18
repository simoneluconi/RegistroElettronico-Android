package com.sharpdroid.registroelettronico.activities

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.AllLessonsAdapter
import com.sharpdroid.registroelettronico.database.pojos.LessonMini
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi
import kotlinx.android.synthetic.main.activity_recycler_refresh_scrollbar.*
import kotlinx.android.synthetic.main.app_bar_main.*

class AllLessonsWithDownloadActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mRVAdapter: AllLessonsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_refresh_scrollbar)

        val code = intent.getIntExtra("code", -1)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mRVAdapter = AllLessonsAdapter()

        title = "Lezioni"

        with(recycler) {
            layoutManager = LinearLayoutManager(this@AllLessonsWithDownloadActivity)
            adapter = mRVAdapter
        }

        with(swiperefresh) {
            visibility = View.VISIBLE
            setOnRefreshListener(this@AllLessonsWithDownloadActivity)
            setColorSchemeResources(
                    R.color.bluematerial,
                    R.color.redmaterial,
                    R.color.greenmaterial,
                    R.color.orangematerial)
        }

        addLessons(DatabaseHelper.database.lessonsDao().loadLessons(code.toLong(), Account.with(this).user))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun addLessons(lessons: List<LessonMini>) {
        mRVAdapter.clear()
        mRVAdapter.addAll(lessons)
    }

    override fun onRefresh() {
        Metodi.updateLessons(this)
    }

}
