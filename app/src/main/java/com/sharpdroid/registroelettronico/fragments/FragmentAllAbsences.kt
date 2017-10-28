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
import com.sharpdroid.registroelettronico.adapters.AllAbsencesAdapter
import com.sharpdroid.registroelettronico.database.entities.Absence
import com.sharpdroid.registroelettronico.database.entities.MyAbsence
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateAbsence
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*

class FragmentAllAbsences : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_ABSENCES_START -> {
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_ABSENCES_OK,
            EventType.UPDATE_ABSENCES_KO -> {
                load()
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false

            }
        }
    }

    lateinit var adapter: AllAbsencesAdapter
    private lateinit var emptyHolder: EmptyFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.coordinator_swipe_recycler, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessuna assenza", R.drawable.ic_supervisor)
        layout.coordinator_layout.addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_ABSENCES_START, EventType.UPDATE_ABSENCES_OK, EventType.UPDATE_ABSENCES_KO)

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)

        activity.title = getString(R.string.absences)

        adapter = AllAbsencesAdapter(context)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        load()
        //download()

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Assenze"))
    }

    private fun addAbsence(absence: Array<in Any>) {
        adapter.clear()
        adapter.addAll(absence)

        emptyHolder.visibility = if (absence.isEmpty()) View.VISIBLE else View.GONE
        //recycler.visibility = if (absence.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun load() {
        val absencesAndDurations = Absence.getAbsences(Profile.getProfile(context)!!)
        val list: MutableList<in Any> = SugarRecord.find(Absence::class.java, "PROFILE=? AND TYPE!='ABA0'", Account.with(context).user.toString()).toMutableList()
        list.addAll(absencesAndDurations.map { MyAbsence(it.key, it.value) })
        addAbsence(list.toTypedArray())
    }

    override fun onRefresh() {
        download()
    }

    private fun download() {
        updateAbsence(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_ABSENCES_START, EventType.UPDATE_ABSENCES_OK, EventType.UPDATE_ABSENCES_KO)
    }
}