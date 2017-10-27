package com.sharpdroid.registroelettronico.Fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Adapters.CommunicationAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.Communication
import com.sharpdroid.registroelettronico.Databases.Entities.CommunicationInfo
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.*
import com.sharpdroid.registroelettronico.Views.EmptyFragment
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*
import java.io.File

class FragmentCommunications : Fragment(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, NotificationManager.NotificationReceiver, CommunicationAdapter.DownloadListener {
    var snackbar: Snackbar? = null
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_BACHECA_START -> {
                snackbar = Snackbar.make(coordinator_layout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE)
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_BACHECA_OK -> {
                load()
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
            EventType.UPDATE_BACHECA_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
            EventType.DOWNLOAD_FILE_START -> {
                snackbar?.show()
            }
            EventType.DOWNLOAD_FILE_OK -> {
                val file = File(SugarRecord.findById(CommunicationInfo::class.java, args[0] as Long).path)
                with(snackbar!!) {
                    setText(activity.getString(R.string.file_downloaded, file.name))
                    setAction(R.string.open) { openFile(activity, file) }
                    duration = Snackbar.LENGTH_SHORT
                    show()
                }
            }
            EventType.DOWNLOAD_FILE_KO -> {
                with(snackbar!!) {
                    setText("File non scaricato")
                    duration = Snackbar.LENGTH_SHORT
                    show()
                }
            }
        }

    }

    lateinit private var mRVAdapter: CommunicationAdapter
    lateinit private var emptyHolder: EmptyFragment

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater!!.inflate(R.layout.coordinator_swipe_recycler, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessuna comunicazione!", R.drawable.ic_assignment)
        layout.coordinator_layout.addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_BACHECA_START, EventType.UPDATE_BACHECA_OK, EventType.UPDATE_BACHECA_KO, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
        setHasOptionsMenu(true)

        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)
        activity.title = getString(R.string.communications)

        with(recycler) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.divider).size(dp(1)).build())
            itemAnimator = null
            isVerticalScrollBarEnabled = true

            mRVAdapter = CommunicationAdapter(this@FragmentCommunications)
            adapter = mRVAdapter
        }

        load()
        //download()
    }

    override fun onCommunicationClick(communication: Communication) {
        with(SugarRecord.findById(CommunicationInfo::class.java, communication.myId) ?: return) {
            val builder = MaterialDialog.Builder(activity).title(title).content(content.trim())

            if (communication.hasAttachment) {
                builder.neutralText(if (path.isEmpty() || !java.io.File(path).exists()) "SCARICA" else "APRI")
                builder.onNeutral { _, _ ->
                    if (path.isNotEmpty() && java.io.File(path).exists()) {
                        openFile(activity, File(path))
                    } else {
                        downloadAttachment(activity, communication)
                    }
                }
            }

            builder.positiveText("OK")

            builder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.communication_menu, menu)

        val searchView = menu?.getItem(0)?.actionView as SearchView

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(this)

    }

    private fun addCommunications(communications: List<Communication>) {
        mRVAdapter.clear()
        mRVAdapter.addAll(communications)

        emptyHolder.visibility = if (communications.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onRefresh() {
        download()
    }

    private fun load() {
        addCommunications(SugarRecord.find(Communication::class.java, "PROFILE=? order by DATE desc", Account.with(activity).user.toString()))
    }

    private fun download() {
        updateBacheca(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_BACHECA_START, EventType.UPDATE_BACHECA_OK, EventType.UPDATE_BACHECA_KO, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        mRVAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        mRVAdapter.filter.filter(newText)
        return false
    }
}
