package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.AllLessonsAdapter
import com.sharpdroid.registroelettronico.database.pojos.LessonMini
import com.sharpdroid.registroelettronico.database.viewModels.LessonsViewModel
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateLessons
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*
import kotlinx.android.synthetic.main.fragment_recycler_refresh_scrollbar.*


class FragmentLessons : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: EventType, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_LESSONS_START -> {
                swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_LESSONS_OK,
            EventType.UPDATE_LESSONS_KO -> {
                swiperefresh.isRefreshing = false
            }
            else -> { // Ignore
            }
        }
    }

    private lateinit var mRVAdapter: AllLessonsAdapter
    private lateinit var emptyHolder: EmptyFragment

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

        mRVAdapter = AllLessonsAdapter()
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = mRVAdapter

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)


        ViewModelProviders.of(activity)[LessonsViewModel::class.java].selected.observe(this, Observer {
            activity.title = it?.getSubjectName().orEmpty()
            addLessons(it?.lessons.orEmpty())
        })

        //onRefresh()
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Lezioni").putContentType("Lezioni"))
    }

    override fun onRefresh() {
        updateLessons(context)
    }

    private fun addLessons(lessons: List<LessonMini>) {
        mRVAdapter.clear()
        mRVAdapter.addAll(lessons.sortedByDescending { it.mDate })

        emptyHolder.visibility = if (lessons.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_LESSONS_KO, EventType.UPDATE_LESSONS_OK, EventType.UPDATE_LESSONS_START)
    }
}
