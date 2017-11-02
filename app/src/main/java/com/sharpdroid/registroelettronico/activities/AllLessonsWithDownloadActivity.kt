package com.sharpdroid.registroelettronico.activities

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.AllLessonsAdapter
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Metodi
import kotlinx.android.synthetic.main.activity_recycler_refresh_scrollbar.*
import kotlinx.android.synthetic.main.app_bar_main.*

class AllLessonsWithDownloadActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit private var mRVAdapter: AllLessonsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_refresh_scrollbar)

        code = intent.getIntExtra("code", -1)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        title = Metodi.capitalizeEach("")//SugarRecord.findById(Subject::class.java, code).description)

        mRVAdapter = AllLessonsAdapter(this)

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
        //UpdateLessons();
    }

    override fun onResume() {
        super.onResume()

        bindLessonsCache()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun addLessons(lessons: List<Lesson>) {
        if (!lessons.isEmpty()) {
            mRVAdapter.clear()
            mRVAdapter.addAll(lessons)
        }
    }

    private fun bindLessonsCache() {
        addLessons(emptyList())//SugarRecord.findWithQuery(Lesson::class.java, "select * from LESSON where M_SUBJECT_ID=? GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC", code.toString()))
    }

    override fun onRefresh() {
        swiperefresh.isRefreshing = false
    }

    companion object {
        internal var code: Int = 0
    }
}
