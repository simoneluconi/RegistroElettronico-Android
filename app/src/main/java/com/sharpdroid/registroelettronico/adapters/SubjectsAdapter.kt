package com.sharpdroid.registroelettronico.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.entities.Teacher
import com.sharpdroid.registroelettronico.fragments.FragmentSubjects
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.or
import kotlinx.android.synthetic.main.adapter_subject.view.*
import java.util.*

class SubjectsAdapter(fragmentAgenda: FragmentSubjects?) : RecyclerView.Adapter<SubjectsAdapter.SubjectHolder>() {
    private var CVDataList: MutableList<SubjectInfo> = mutableListOf()
    private var subjectListener: SubjectListener? = null

    init {
        CVDataList = LinkedList()
        if (fragmentAgenda != null) {
            this.subjectListener = fragmentAgenda
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
        return SubjectHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_subject, parent, false))
    }

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
        val item = CVDataList[position]
        val teachers = item.subject.teachers.map { it.teacherName }
        holder.subject.text = capitalizeEach(item.description.or(item.subject.description))

        holder.prof.visibility = View.VISIBLE
        holder.prof.text = capitalizeEach(TextUtils.join(", ", teachers), true)

        holder.layout.setOnClickListener { view ->
            if (subjectListener != null) {
                view.layout.postDelayed({ subjectListener!!.onSubjectClick(item.subject) }, ViewConfiguration.getTapTimeout().toLong())
            }
        }
        holder.layout.setOnLongClickListener { view ->
            if (subjectListener != null) {
                view.layout.postDelayed({ subjectListener!!.onSubjectLongClick(item.subject) }, 0)
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return CVDataList.size
    }

    fun addAll(subjects: List<SubjectInfo>) {
        CVDataList.addAll(subjects)
        Collections.sort(CVDataList) { subject, t1 -> (subject.description.or(subject.subject.description)).compareTo(t1.description.or(t1.subject.description), true) }
        for (s in subjects) {
            s.subject.teachers = Teacher.professorsOfSubject(s.subject.id)
        }
        notifyDataSetChanged()
    }

    fun clear() {
        CVDataList.clear()
        notifyDataSetChanged()
    }

    interface SubjectListener {
        fun onSubjectClick(subject: Subject)
        fun onSubjectLongClick(subject: Subject)
    }

    inner class SubjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var subject: TextView = itemView.subject
        internal var prof: TextView = itemView.professor
        internal var layout: View = itemView.layout

    }
}
