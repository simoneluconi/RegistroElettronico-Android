package com.sharpdroid.registroelettronico.Fragments


import android.content.ActivityNotFoundException
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.orm.SugarRecord
import com.sharpdroid.registroelettronico.Adapters.FileAdapter
import com.sharpdroid.registroelettronico.Databases.Entities.File
import com.sharpdroid.registroelettronico.Databases.Entities.Folder
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.EventType
import com.sharpdroid.registroelettronico.Utils.Metodi.*
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration

class FragmentFiles : Fragment(), NotificationManager.NotificationReceiver, FileAdapter.DownloadListener {
    lateinit var layout: CoordinatorLayout
    var snackbar: Snackbar? = null

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.DOWNLOAD_FILE_START -> {
                snackbar = Snackbar.make(layout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE)
                snackbar?.show()
            }
            EventType.DOWNLOAD_FILE_OK -> {
                with(snackbar ?: return) {
                    val file: java.io.File = java.io.File(SugarRecord.findById(File::class.java, args[0] as Long).path)
                    setText(activity.getString(R.string.file_downloaded, file.name))
                    setAction(R.string.open) { openFile(activity, file) }
                    show()
                }
            }
            EventType.DOWNLOAD_FILE_KO -> {

            }
        }
    }

    private var data: Folder? = null
    private lateinit var mRVAdapter: FileAdapter

    fun setData(data: Folder) {
        this.data = data
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layout = CoordinatorLayout(context)
        layout.layoutParams = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val recyclerView = RecyclerView(context)
        recyclerView.id = R.id.recycler
        recyclerView.verticalScrollbarPosition = View.SCROLLBAR_POSITION_RIGHT
        recyclerView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        layout.addView(recyclerView)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
        val mRecyclerView = view!!.findViewById<RecyclerView>(R.id.recycler)
        mRVAdapter = FileAdapter(this)
        addSubjects(data!!)
        setTitle(data?.name?.trim { it <= ' ' }!!)

        with(mRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dpToPx(72), dpToPx(16)).colorResId(R.color.divider).size(dpToPx(1)).build())
            itemAnimator = null
            adapter = mRVAdapter
        }

    }

    override fun onFileClick(file: File) {
        when (file.type) {
            "file" -> {
                if (file.path.isEmpty()) {
                    downloadFile(activity, file)
                } else {
                    try {
                        openFile(activity, java.io.File(file.path))
                    } catch (e: ActivityNotFoundException) {
                        Snackbar.make(layout, activity.resources.getString(R.string.missing_app, java.io.File(file.path).name), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            "link" -> {
                openLink(activity, file.path)
            }
            "text" -> {
                MaterialDialog.Builder(activity).title(file.contentName).content(file.path).positiveText("OK").autoDismiss(true).show()
            }
        }

    }

    private fun setTitle(title: CharSequence) {
        activity.title = title
    }

    private fun addSubjects(folder: Folder) {
        println(folder.toString())
        mRVAdapter.clear()
        val files = fetch(folder)
        mRVAdapter.addAll(files)
    }

    private fun fetch(folder: Folder): List<File> {
        return SugarRecord.find(File::class.java, "FILE.TEACHER='${folder.teacher}' AND FILE.FOLDER='${folder.folderId}'")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
    }

    companion object {

        fun newInstance(data: Folder): FragmentFiles {
            val fragment = FragmentFiles()
            fragment.setData(data)
            return fragment
        }
    }
}
