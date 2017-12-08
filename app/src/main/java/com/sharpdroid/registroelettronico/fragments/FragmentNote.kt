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
import com.sharpdroid.registroelettronico.adapters.NoteAdapter
import com.sharpdroid.registroelettronico.database.entities.Note
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.Metodi.updateNote
import com.sharpdroid.registroelettronico.viewModels.NoteViewModel
import com.sharpdroid.registroelettronico.views.EmptyFragment
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*

class FragmentNote : Fragment(), SwipeRefreshLayout.OnRefreshListener, NotificationManager.NotificationReceiver {

    override fun didReceiveNotification(code: EventType, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_NOTES_START -> {
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_NOTES_OK,
            EventType.UPDATE_NOTES_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
            else -> { // Ignore
            }
        }
    }

    lateinit private var mRVAdapter: NoteAdapter
    lateinit private var emptyHolder: EmptyFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.coordinator_swipe_recycler, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessuna nota", R.drawable.ic_error)
        layout.coordinator_layout.addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_NOTES_START, EventType.UPDATE_NOTES_OK, EventType.UPDATE_NOTES_KO)

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)

        activity.title = getString(R.string.note)
        mRVAdapter = NoteAdapter(context)

        with(recycler) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).size(dp(1)).build())
            itemAnimator = null

            adapter = mRVAdapter
        }


        if (savedInstanceState == null) {
            download()
        }

        ViewModelProviders.of(this)[NoteViewModel::class.java].getNotes(Account.with(context).user).observe(this, Observer {
            addNotes(it ?: emptyList())
        })


        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Note"))
    }

    private fun addNotes(notes: List<Note>) {
        mRVAdapter.clear()
        mRVAdapter.addAll(notes)

        emptyHolder.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onRefresh() {
        download()
    }

    private fun download() {
        updateNote(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_NOTES_START, EventType.UPDATE_NOTES_OK, EventType.UPDATE_NOTES_KO)
    }
}
