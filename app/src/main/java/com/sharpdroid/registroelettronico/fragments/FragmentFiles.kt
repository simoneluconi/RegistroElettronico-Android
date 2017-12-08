package com.sharpdroid.registroelettronico.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
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
import com.sharpdroid.registroelettronico.database.entities.Profile
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.EventType
import com.sharpdroid.registroelettronico.utils.Metodi.downloadFile
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.Metodi.openFile
import com.sharpdroid.registroelettronico.utils.Metodi.openLink
import com.sharpdroid.registroelettronico.viewModels.DidatticaViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.coordinator_recycler.*
import java.io.File as JavaFile

class FragmentFiles : Fragment(), NotificationManager.NotificationReceiver, FileAdapter.DownloadListener {
    override fun didReceiveNotification(code: EventType, args: Array<in Any>) {
        when (code) {
            EventType.DOWNLOAD_FILE_START -> {
                Snackbar.make(coordinator_layout, R.string.download_in_corso, Snackbar.LENGTH_INDEFINITE).show()
            }
            EventType.DOWNLOAD_FILE_OK -> {
                val file = JavaFile(DatabaseHelper.database.foldersDao().getInfo(args[0] as Long)?.path)
                Snackbar.make(coordinator_layout, String.format(getString(R.string.file_downloaded), file.name), Snackbar.LENGTH_SHORT)
                        .setAction(R.string.open) { openFile(context, file, Snackbar.make(coordinator_layout, context.resources.getString(R.string.missing_app, file.name), Snackbar.LENGTH_SHORT)) }
                        .show()
            }
            EventType.DOWNLOAD_FILE_KO -> {
                Snackbar.make(coordinator_layout, R.string.download_failed, Snackbar.LENGTH_SHORT).show()
            }
            else -> { // Ignore
            }
        }
    }

    private lateinit var mRVAdapter: FileAdapter
    private lateinit var viewModel: DidatticaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.coordinator_recycler, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationManager.instance.addObserver(this, EventType.DOWNLOAD_FILE_START, EventType.DOWNLOAD_FILE_OK, EventType.DOWNLOAD_FILE_KO)
        mRVAdapter = FileAdapter(this)

        viewModel = ViewModelProviders.of(activity)[DidatticaViewModel::class.java]

        with(recycler) {
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
                if (info.path.isEmpty() || !JavaFile(info.path).exists()) {
                    downloadFile(file, Profile.getProfile(context))
                } else {
                    openFile(context, JavaFile(info.path), Snackbar.make(coordinator_layout, context.resources.getString(R.string.missing_app, JavaFile(info.path).name), Snackbar.LENGTH_SHORT))
                }
            }
            "link" -> {
                openLink(context, info.path, Snackbar.make(coordinator_layout, getString(R.string.failed_to_open_link), Snackbar.LENGTH_SHORT))
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
