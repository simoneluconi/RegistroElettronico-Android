package com.sharpdroid.registroelettronico.adapters

import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.HeaderHolder
import com.sharpdroid.registroelettronico.database.entities.SuperAgenda
import com.sharpdroid.registroelettronico.database.pojos.LocalAgendaPOJO
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class AgendaAdapter(private val place_holder: View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data = mutableListOf<Any>()
    private val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    private var mClickListener: AgendaClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.adapter_header)
            HeaderHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
        else
            EventHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = data[position]
        when (entry) {
            is String -> (holder as HeaderHolder).content.text = entry
            is SuperAgenda -> {
                val eventHolder = holder as EventHolder
                val title = SpannableString(entry.agenda.notes)
                if (entry.completed) {
                    title.setSpan(StrikethroughSpan(), 0, entry.agenda.notes.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                eventHolder.date.text = dateFormat.format(entry.agenda.start)
                eventHolder.subject.text = capitalizeEach(entry.agenda.author, true)
                eventHolder.subject.text = capitalizeEach(
                        if (entry.agenda.subject.isNullOrEmpty()) entry.agenda.author else entry.agenda.subject!!, true)
                eventHolder.title.text = title

                eventHolder.notes.visibility = View.GONE
                eventHolder.divider.visibility = if (data[position - 1] is String) View.INVISIBLE else View.VISIBLE
                eventHolder.subject.visibility = if (eventHolder.subject.text.isEmpty()) View.GONE else View.VISIBLE

                eventHolder.itemView.setOnClickListener {
                    if (mClickListener != null)
                        mClickListener!!.onAgendaItemClicked(entry)
                }
            }
            is LocalAgendaPOJO -> {
                val eventHolder = holder as EventHolder
                val event = entry.event

                val title = SpannableString(event.title)
                if (event.completed_date != 0L) {
                    title.setSpan(StrikethroughSpan(), 0, event.title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                eventHolder.date.text = dateFormat.format(event.day)
                eventHolder.subject.text = Metodi.capitalizeEach(entry.teacher.getOrNull(0)?.teacherName ?: entry.subjectInfo.getOrNull(0)?.description ?: entry.subject.getOrNull(0)?.description ?: "")
                eventHolder.title.text = title
                eventHolder.notes.text = event.content.trim { it <= ' ' }

                eventHolder.notes.visibility = if (event.content.isEmpty()) View.GONE else View.VISIBLE
                eventHolder.subject.visibility = if (eventHolder.subject.text.isEmpty()) View.GONE else View.VISIBLE
                eventHolder.divider.visibility = if (getItemViewType(position - 1) == R.layout.adapter_header) View.INVISIBLE else View.VISIBLE

                eventHolder.itemView.setOnClickListener {
                    if (mClickListener != null)
                        mClickListener!!.onAgendaItemClicked(event)
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]
        return if (item is String)
            R.layout.adapter_header
        else
            R.layout.adapter_event
    }

    override fun getItemCount() = data.size

    fun setItemClickListener(longClickListener: AgendaClickListener) {
        mClickListener = longClickListener
    }

    fun addAll(events: List<Any>) {
        data.addAll(convert(events))
        if (data.isEmpty()) {
            place_holder.visibility = View.VISIBLE
        } else {
            place_holder.visibility = View.GONE
        }
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    private fun convert(events: List<Any>): List<Any> {
        val organized = events.groupBy {
            if (it is SuperAgenda) {
                if (it.test) "verifiche" else "altri eventi"
            } else {
                (it as LocalAgendaPOJO).event.type
            }
        }.toMutableMap()

        val convert = LinkedList<Any>()

        //Priorità alle verifiche
        if (organized.containsKey("verifiche")) {
            convert.add("verifiche")
            convert.addAll(organized["verifiche"].orEmpty())
            organized.remove("verifiche")
        }

        for (k in organized.keys) {
            convert.add(k)
            convert.addAll(organized[k].orEmpty())
        }

        return convert
    }

    interface AgendaClickListener {
        fun onAgendaItemClicked(e: Any)
    }

    inner class EventHolder(l: View) : RecyclerView.ViewHolder(l) {
        var divider: View = l.divider
        var title: TextView = l.title
        var subject: TextView = l.subject
        var notes: TextView = l.notes
        var date: TextView = l.date
    }
}
