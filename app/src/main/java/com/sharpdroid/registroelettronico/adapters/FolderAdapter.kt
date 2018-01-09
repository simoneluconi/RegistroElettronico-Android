package com.sharpdroid.registroelettronico.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.pojos.FolderPOJO
import com.sharpdroid.registroelettronico.database.pojos.TeacherDidacticPOJO
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_folder.view.*
import kotlinx.android.synthetic.main.adapter_header.view.*
import java.text.SimpleDateFormat
import java.util.*

class FolderAdapter(private val listener: Listener?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val formatter = SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN)
    private val list = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                1 -> FileTeacherHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_folder, parent, false))
                0 -> SubHeaderHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_header, parent, false))
                else -> throw IllegalStateException("Cannot create ViewHolder")
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val fe = list[position]

        when (holder) {
            is FileTeacherHolder -> {
                val f = fe as FolderPOJO
                holder.layout.setOnClickListener { v ->
                    if (listener != null)
                        v.postDelayed({ listener.onFolderClick(f) }, ViewConfiguration.getTapTimeout().toLong())
                }

                holder.title.text = f.name.trim { it <= ' ' }
                holder.date.text = formatter.format(f.lastUpdate)
            }
            is SubHeaderHolder -> {
                holder.teacher.text = capitalizeEach(fe as String, true)
            }
        }

    }

    override fun getItemViewType(position: Int) = when (list[position]) {
        is String -> 0
        is FolderPOJO -> 1
        else -> -1
    }

    override fun getItemCount() = list.size

    fun setTeacherFolder(teachers: List<TeacherDidacticPOJO>) {
        list.clear()
        for (teacher in teachers) {
            list.add(teacher.teacher.teacherName)
            list.addAll(teacher.folders.sortedByDescending { it.name })
        }
        notifyDataSetChanged()
    }

    interface Listener {
        fun onFolderClick(f: FolderPOJO)
    }

    internal inner class SubHeaderHolder(layout: View) : RecyclerView.ViewHolder(layout) {
        var teacher: TextView = layout.content
    }

    internal inner class FileTeacherHolder(layout: View) : RecyclerView.ViewHolder(layout) {
        var title: TextView = layout.title
        var date: TextView = layout.date
        var layout: RelativeLayout = layout.relative_layout
    }
}
