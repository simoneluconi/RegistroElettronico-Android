package com.sharpdroid.registroelettronico.adapters

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.HeaderHolder
import com.sharpdroid.registroelettronico.adapters.holders.LessonHolder
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.month_year
import java.text.SimpleDateFormat
import java.util.*

class AllLessonsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("EEEE d", Locale.getDefault())

    private val types = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.adapter_header)
            HeaderHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
        else
            LessonHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    @LayoutRes
    override fun getItemViewType(position: Int) =
            if (types[position] is String) R.layout.adapter_header else R.layout.adapter_lessons_1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderHolder) {
            holder.content.text = types[position] as String
        } else {
            val lessonHolder = holder as LessonHolder

            val lesson = types[position] as Lesson

            lessonHolder.content.text = lesson.mArgument
            lessonHolder.date.text = capitalizeEach(dateFormat.format(lesson.mDate))
        }
    }

    fun addAll(lessons: List<Lesson>) {
        elaborateList(lessons)
        notifyDataSetChanged()
    }

    private fun elaborateList(lessons: List<Lesson>) {
        val grouped = lessons.groupBy { month_year.format(it.mDate) }
        grouped.keys.forEach {
            types.add(it)
            with(grouped[it]) {
                types.addAll(this.orEmpty())
            }
        }
    }

    fun clear() {
        types.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = types.size
}
