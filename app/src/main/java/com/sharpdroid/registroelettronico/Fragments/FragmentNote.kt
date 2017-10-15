package com.sharpdroid.registroelettronico.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Adapters.NoteAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.Note
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.dp
import com.sharpdroid.registroelettronico.Utils.Metodi.updateNote
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*

class FragmentNote : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_NOTES_START -> {
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_NOTES_OK -> {
                load()
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
            EventType.UPDATE_NOTES_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
        }
    }

    private var mRVAdapter: NoteAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.coordinator_swipe_recycler, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_NOTES_START, EventType.UPDATE_NOTES_OK, EventType.UPDATE_NOTES_KO)

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)

        activity.title = getString(R.string.note)

        with(recycler) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).size(dp(1)).build())
            itemAnimator = null

            mRVAdapter = NoteAdapter(context)
            adapter = mRVAdapter
        }

        load()
        //download()
    }

    private fun addNotes(notes: List<Note>) {
        if (!notes.isEmpty()) {
            mRVAdapter!!.clear()
            mRVAdapter!!.addAll(notes)
        }
    }

    private fun load() {
        addNotes(SugarRecord.find(Note::class.java, "PROFILE=?", Account.with(activity).user.toString()))
    }


    override fun onRefresh() {
        download()
    }

    private fun download() {
        updateNote(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_NOTES_START, EventType.UPDATE_NOTES_OK, EventType.UPDATE_NOTES_KO)
    }
}
