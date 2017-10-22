package com.sharpdroid.registroelettronico.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Adapters.FolderAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.Folder
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.updateFolders
import kotlinx.android.synthetic.main.coordinator_swipe_recycler.*

class FragmentFolders : Fragment(), SwipeRefreshLayout.OnRefreshListener, FolderAdapter.Listener, NotificationManager.NotificationReceiver {
    var selectedFolder: Folder? = null

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.UPDATE_FOLDERS_START -> {
                if (!swiperefresh.isRefreshing) swiperefresh.isRefreshing = true
            }
            EventType.UPDATE_FOLDERS_OK -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
                load()
            }
            EventType.UPDATE_FOLDERS_KO -> {
                if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
            }
        }
    }

    lateinit var mRVAdapter: FolderAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.coordinator_swipe_recycler, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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

        if (savedInstanceState != null) {
            selectedFolder = savedInstanceState.getSerializable("folder") as Folder?
            //if (selectedFolder != null) onFolderClick(selectedFolder!!)
        }
        //update()

    }

    override fun onResume() {
        super.onResume()
        selectedFolder = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (selectedFolder != null) {
            Log.d("FragmentFolders", "SAVE STATE")
            outState?.putSerializable("folder", selectedFolder)
        }
    }

    private fun addFiles(teachers: List<Teacher>, docache: Boolean) {
        if (!teachers.isEmpty()) {
            mRVAdapter.setTeacherFolder(teachers)

            if (docache) {
                // Update cache
                save(teachers)
            }
        }
    }

    override fun onRefresh() {
        update()
    }

    private fun load() {
        val teachers = SugarRecord.findWithQuery(Teacher::class.java, "select * from TEACHER where TEACHER.ID IN (select TEACHER FROM FOLDER WHERE FOLDER.PROFILE=?)", Account.with(activity).user.toString())
        teachers.forEach { it.folders = SugarRecord.find(Folder::class.java, "TEACHER=? AND PROFILE=?", it.id.toString(), Account.with(activity).user.toString()) }

        addFiles(teachers, false)
    }

    private fun save(teachers: List<Teacher>) {
        val list = mutableListOf<Folder>()
        teachers.forEach { list.addAll(it.folders) }

        SugarRecord.updateInTx(list)
    }

    private fun update() {
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
