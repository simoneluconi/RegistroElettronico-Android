package com.sharpdroid.registroelettronico.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.pojos.SubjectWithLessons
import com.sharpdroid.registroelettronico.fragments.FragmentSubjects
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_subject.view.*

class SubjectsAdapter(fragmentAgenda: FragmentSubjects) : RecyclerView.Adapter<SubjectsAdapter.SubjectHolder>() {
    private val data: MutableList<SubjectWithLessons> = mutableListOf()
    private val subjectListener: SubjectListener = fragmentAgenda

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SubjectHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_subject, parent, false))

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
        val item = data[position]

        val temp = item.lessons.map { it.mAuthorName }.groupBy { it }
        val teachers = if (temp.keys.size >= 2) temp.filter { it.value.size >= 2 }.keys else temp.keys

        holder.subject.text = capitalizeEach(item.getSubjectName())

        holder.prof.visibility = View.VISIBLE
        holder.prof.text = capitalizeEach(TextUtils.join(", ", teachers), true)

        holder.layout.setOnClickListener { view ->
            view.layout.postDelayed({ subjectListener.onSubjectClick(item) }, ViewConfiguration.getTapTimeout().toLong())
        }
        holder.layout.setOnLongClickListener { view ->
            view.layout.postDelayed({ subjectListener.onSubjectLongClick(item) }, 0)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = data.size

    fun addAll(subjects: List<SubjectWithLessons>) {
        data.addAll(subjects)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    interface SubjectListener {
        fun onSubjectClick(subject: SubjectWithLessons)
        fun onSubjectLongClick(subject: SubjectWithLessons)
    }

    inner class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var subject: TextView = itemView.subject
        internal var prof: TextView = itemView.professor
        internal var layout: View = itemView.layout
    }
}
