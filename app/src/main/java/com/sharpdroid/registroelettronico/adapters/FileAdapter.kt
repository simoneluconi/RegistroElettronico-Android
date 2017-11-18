package com.sharpdroid.registroelettronico.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.File
import com.sharpdroid.registroelettronico.fragments.FragmentFiles
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.adapter_file.view.*
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(fragmentFiles: FragmentFiles) : RecyclerView.Adapter<FileAdapter.FileHolder>() {
    private val formatter = SimpleDateFormat("MMM d, yyyy", Locale.ITALIAN)
    private val listener: DownloadListener
    private val mContext: Context
    private val data = mutableListOf<File>()

    init {
        this.mContext = fragmentFiles.activity
        listener = fragmentFiles
    }

    fun addAll(data: List<File>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            FileHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_file, parent, false))

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val file = data[position]

        holder.title.text = if (!TextUtils.isEmpty(file.contentName.trim { it <= ' ' })) file.contentName.trim { it <= ' ' } else String.format("[%1\$s]", mContext.getString(R.string.senza_nome))
        holder.date.text = formatter.format(file.date)

        if (file.type == "link") {
            holder.image.setImageResource(R.drawable.link)
        } else {
            holder.image.setImageResource(R.drawable.file)
        }

        holder.mRelativeLayout.setOnClickListener { v -> v.postDelayed({ listener.onFileClick(file) }, ViewConfiguration.getTapTimeout().toLong()) }
    }

    override fun getItemCount() = data.size

    interface DownloadListener {
        fun onFileClick(file: File)
    }

    inner class FileHolder(layout: View) : RecyclerView.ViewHolder(layout) {
        var title: TextView = layout.title
        var date: TextView = layout.date
        var image: CircleImageView = layout.circleImageView
        var mRelativeLayout: RelativeLayout = layout.relative_layout
    }
}
