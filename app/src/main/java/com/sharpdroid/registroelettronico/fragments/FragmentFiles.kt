package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.NotificationManager
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.FileAdapter
import com.sharpdroid.registroelettronico.database.entities.File
import com.sharpdroid.registroelettronico.database.entities.FileInfo
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.database.viewModels.DidatticaViewModel
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.*
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import java.io.File as JavaFile

class FragmentFiles : Fragment(), NotificationManager.NotificationReceiver, FileAdapter.DownloadListener {
    lateinit var layout: CoordinatorLayout

    override fun didReceiveNotification(code: Int, args: Array<in Any>) {
        when (code) {
            EventType.DOWNLOAD_FILE_START -> {
                Snackbar.make(layout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE).show()
            }
            EventType.DOWNLOAD_FILE_OK -> {
                val file = JavaFile(DatabaseHelper.database.foldersDao().getInfo(args[0] as Long)?.path)
                Snackbar.make(layout, String.format(getString(R.string.file_downloaded), file.name), Snackbar.LENGTH_SHORT)
                        .setAction(R.string.open) { openFile(context, file, Snackbar.make(layout, context.resources.getString(R.string.missing_app, file.name), Snackbar.LENGTH_SHORT)) }
                        .show()
            }
            EventType.DOWNLOAD_FILE_KO -> {
                Snackbar.make(layout, R.string.download_failed, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var mRVAdapter: FileAdapter
    private lateinit var viewModel: DidatticaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
        mRVAdapter = FileAdapter(this)

        viewModel = ViewModelProviders.of(activity)[DidatticaViewModel::class.java]

        val mRecyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        with(mRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(dp(72), dp(16)).colorResId(R.color.divider).size(dp(1)).build())
            itemAnimator = null
            adapter = mRVAdapter
        }

        viewModel.selectedFolder.observe(this, Observer {
            setTitle(it?.name?.trim { it <= ' ' } ?: throw NullPointerException("data==null"))

            mRVAdapter.clear()
            mRVAdapter.addAll(it.files)
        })

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Didattica").putContentType("File"))
    }

    override fun onFileClick(file: File) {
        val info = DatabaseHelper.database.foldersDao().getInfo(file.objectId) ?: FileInfo(0, "")
        when (file.type) {
            "file" -> {
                if (info.path.isEmpty() || JavaFile(info.path).exists()) {
                    downloadFile(context, file)
                } else {
                    openFile(context, JavaFile(info.path), Snackbar.make(layout, context.resources.getString(R.string.missing_app, JavaFile(info.path).name), Snackbar.LENGTH_SHORT))
                }
            }
            "link" -> {
                openLink(context, info.path, Snackbar.make(layout, getString(R.string.failed_to_open_link), Snackbar.LENGTH_SHORT))
            }
            "text" -> {
                MaterialDialog.Builder(context).title(file.contentName).content(info.path).positiveText("OK").autoDismiss(true).show()
            }
        }
    }

    private fun setTitle(title: CharSequence) {
        activity.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NotificationManager.instance.removeObserver(this, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
    }
}
