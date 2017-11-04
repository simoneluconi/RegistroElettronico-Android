package com.sharpdroid.registroelettronico.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
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
import com.sharpdroid.registroelettronico.adapters.FolderAdapter
import com.sharpdroid.registroelettronico.database.pojos.FolderPOJO
import com.sharpdroid.registroelettronico.database.pojos.TeacherDidacticPOJO
import com.sharpdroid.registroelettronico.database.viewModels.DidatticaViewModel
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateFolders
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*

class FragmentFolders : Fragment(), SwipeRefreshLayout.OnRefreshListener, FolderAdapter.Listener, NotificationManager.NotificationReceiver {
    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_FOLDERS_START -> {
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_FOLDERS_OK,
            EventType.UPDATE_FOLDERS_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
        }
    }

    private lateinit var mRVAdapter: FolderAdapter
    lateinit private var emptyHolder: EmptyFragment
    lateinit private var viewModel: DidatticaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.coordinator_swipe_recycler, container, false)
        emptyHolder = EmptyFragment(context)
        emptyHolder.visibility = View.GONE
        emptyHolder.setTextAndDrawable("Nessun materiale didattico condiviso", R.drawable.ic_folder)
        layout.coordinator_layout.addView(emptyHolder)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.UPDATE_FOLDERS_START, EventType.UPDATE_FOLDERS_OK, EventType.UPDATE_FOLDERS_KO)
        swiperefresh.setOnRefreshListener(this)
        swiperefresh.setColorSchemeResources(
                R.color.bluematerial,
                R.color.redmaterial,
                R.color.greenmaterial,
                R.color.orangematerial)

        viewModel = ViewModelProviders.of(activity)[DidatticaViewModel::class.java]

        activity.title = getString(R.string.files)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.itemAnimator = null

        mRVAdapter = FolderAdapter(this)
        recycler.adapter = mRVAdapter

        download()

        viewModel.getDidattica(Account.with(context).user).observe(this, Observer {
            println("OBSERVED")
            addFiles(it ?: emptyList())
        })

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Didattica").putContentType("Cartelle"))
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectedFolder.value = null
    }

    private fun addFiles(teachers: List<TeacherDidacticPOJO>) {
        if (!teachers.isEmpty()) {
            mRVAdapter.setTeacherFolder(teachers)
        } else {
            emptyHolder.visibility = View.VISIBLE
        }
    }

    override fun onRefresh() {
        download()
    }

    private fun download() {
        updateFolders(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_FOLDERS_START, EventType.UPDATE_FOLDERS_OK, EventType.UPDATE_FOLDERS_KO)
    }

    override fun onFolderClick(f: FolderPOJO) {
        viewModel.selectedFolder.value = f
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)/*setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)*/.replace(R.id.fragment_container, FragmentFiles()).addToBackStack(null)
        transaction.commit()
    }
}
