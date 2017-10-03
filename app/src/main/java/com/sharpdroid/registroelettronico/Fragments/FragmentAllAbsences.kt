package com.sharpdroid.registroelettronico.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Adapters.AllAbsencesAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.Absence
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.updateAbsence
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*

class FragmentAllAbsences : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {

        when (code) {
            EventType.UPDATE_ABSENCES_START -> {

            }
            EventType.UPDATE_ABSENCES_OK -> {
            }
            EventType.UPDATE_ABSENCES_KO -> {

            }
        }
    }

    internal var adapter: AllAbsencesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.coordinator_swipe_recycler, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_ABSENCES_START, EventType.UPDATE_ABSENCES_OK, EventType.UPDATE_ABSENCES_KO)

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)

        activity.title = getString(R.string.absences)

        adapter = AllAbsencesAdapter(activity)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter

        load()
        download()

    }

    private fun addAbsence(absence: List<Absence>) {
        adapter?.clear()
        adapter?.addAll(absence)
    }

    private fun load() {
        addAbsence(SugarRecord.find(Absence::class.java, "PROFILE=?", Account.with(activity).user.toString()))
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