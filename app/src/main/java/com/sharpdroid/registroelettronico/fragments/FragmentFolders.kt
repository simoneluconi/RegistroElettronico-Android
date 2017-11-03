package com.sharpdroid.registroelettronico.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.FolderAdapter
import com.sharpdroid.registroelettronico.database.entities.Folder
import com.sharpdroid.registroelettronico.database.entities.Teacher
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.updateFolders
import com.sharpdroid.registroelettronico.views.EmptyFragment
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.view.*

class FragmentFolders : Fragment(), SwipeRefreshLayout.OnRefreshListener, FolderAdapter.Listener, NotificationManager.NotificationReceiver {
    private var selectedFolder: Folder? = null

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_FOLDERS_START -> {
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_FOLDERS_OK,
            EventType.UPDATE_FOLDERS_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
                load()
            }
        }
    }

    private lateinit var mRVAdapter: FolderAdapter
    lateinit private var emptyHolder: EmptyFragment

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


        activity.title = getString(R.string.files)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.itemAnimator = null

        mRVAdapter = FolderAdapter(this)
        recycler.adapter = mRVAdapter

        load()
        download()

        if (savedInstanceState != null) {
            selectedFolder = savedInstanceState.getSerializable("folder") as Folder?
        }
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Didattica").putContentType("Cartelle"))
    }

    override fun onResume() {
        super.onResume()
        selectedFolder = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (selectedFolder != null) {
            Log.d("FragmentFolders", "SAVE STATE")
            outState.putSerializable("folder", selectedFolder)
        }
    }

    private fun addFiles(teachers: List<Teacher>) {
        if (!teachers.isEmpty()) {
            mRVAdapter.setTeacherFolder(teachers)
        } else {
            emptyHolder.visibility = View.VISIBLE
        }
    }

    override fun onRefresh() {
        download()
    }

    private fun load() {
        val teachers = DatabaseHelper.database.subjectsDao().getTeachers(Account.with(context).user)
        teachers.forEach { it.folders = DatabaseHelper.database.foldersDao().getFolders(it.id, Account.with(activity).user) }
        addFiles(teachers)
    }

    private fun download() {
        updateFolders(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.UPDATE_FOLDERS_START, EventType.UPDATE_FOLDERS_OK, EventType.UPDATE_FOLDERS_KO)
    }

    override fun onFolderClick(f: Folder) {
        selectedFolder = f
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)/*setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)*/.replace(R.id.fragment_container, FragmentFiles.newInstance(f)).addToBackStack(null)
        transaction.commit()
    }
}
