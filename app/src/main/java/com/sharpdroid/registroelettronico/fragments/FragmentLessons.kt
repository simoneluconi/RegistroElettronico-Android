package com.sharpdroid.registroelettronico.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.AllLessonsAdapter
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.updateLessons
import com.sharpdroid.registroelettronico.utils.or
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*
import kotlinx.android.synthetic.main.fragment_recycler_refresh_scrollbar.*


class FragmentLessons : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_LESSONS_START -> {
                swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_LESSONS_OK,
            EventType.UPDATE_LESSONS_KO -> {
                swiperefresh.isRefreshing = false
                load()
            }
        }
    }

    private lateinit var mRVAdapter: AllLessonsAdapter
    private lateinit var emptyHolder: EmptyFragment
    var subject: SubjectInfo? = null
    var code: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_recycler_refresh_scrollbar, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessuna lezione", R.drawable.ic_view_agenda)
        layout.coordinator_layout.addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_LESSONS_KO, EventType.UPDATE_LESSONS_OK, EventType.UPDATE_LESSONS_START)
        code = arguments?.getInt("code") ?: savedInstanceState?.getInt("code") ?: -1

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
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Lezioni").putContentType("Lezioni"))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("code", subject?.subject?.id?.toInt()!!)
    }

    override fun onRefresh() {
        updateLessons(context)
    }

    private fun addLessons(lessons: List<Lesson>) {
        mRVAdapter.clear()
        mRVAdapter.addAll(lessons)

        emptyHolder.visibility = if (lessons.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun load() {
        if (subject == null) {
            subject = SugarRecord.findById(Subject::class.java, code)?.getInfo(activity)
            activity.title = capitalizeEach(subject?.description.or(subject?.subject?.description ?: ""))
        }

        addLessons(SugarRecord.findWithQuery(Lesson::class.java, "select * from LESSON where M_SUBJECT_ID=? GROUP BY M_ARGUMENT, M_AUTHOR_NAME, M_DATE ORDER BY M_DATE DESC", code.toString()))
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
