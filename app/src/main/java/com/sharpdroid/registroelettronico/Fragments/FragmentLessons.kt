package com.sharpdroid.registroelettronico.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Activities.or
import com.sharpdroid.registroelettronico.Adapters.AllLessonsAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.Lesson
import com.sharpdroid.registroelettronico.Databases.Entities.Subject
import com.sharpdroid.registroelettronico.Databases.Entities.SubjectInfo
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.Utils.Metodi.updateLessons
import kotlinx.android.synthetic.main.fragment_recycler_refresh_scrollbar.*


class FragmentLessons : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_LESSONS_START -> {
                swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_LESSONS_OK -> {
                swiperefresh.isRefreshing = false
                load()
            }
            EventType.UPDATE_LESSONS_KO -> {
                swiperefresh.isRefreshing = false
            }
        }
    }

    lateinit var mRVAdapter: AllLessonsAdapter
    var subject: SubjectInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subject = SugarRecord.findById(Subject::class.java, arguments?.getInt("code") ?: savedInstanceState?.getInt("code") ?: -1).getInfo(activity)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recycler_refresh_scrollbar, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_LESSONS_KO, EventType.UPDATE_LESSONS_OK, EventType.UPDATE_LESSONS_START)

        activity.title = capitalizeEach(subject!!.description.or(subject!!.subject.description))

        mRVAdapter = AllLessonsAdapter(context)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = mRVAdapter


        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)
        load()

        //onRefresh()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("code", subject?.subject?.id?.toInt()!!)
    }

    override fun onRefresh() {
        swiperefresh.isRefreshing = true
        updateLessons(activity)
    }

    private fun addLessons(lessons: List<Lesson>) {
        if (!lessons.isEmpty()) {
            mRVAdapter.clear()
            mRVAdapter.addAll(lessons)
        }
    }

    private fun load() {
        addLessons(SugarRecord.findWithQuery(Lesson::class.java, "select * from LESSON where M_SUBJECT_ID=? ORDER BY M_DATE DESC", subject?.subject?.id.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_LESSONS_KO, EventType.UPDATE_LESSONS_OK, EventType.UPDATE_LESSONS_START)
    }

    companion object {
        fun newInstance(code: Int): FragmentLessons {
            val fragment = FragmentLessons()
            val b = Bundle()
            b.putInt("code", code)
            fragment.arguments = b
            return fragment
        }
    }
}
