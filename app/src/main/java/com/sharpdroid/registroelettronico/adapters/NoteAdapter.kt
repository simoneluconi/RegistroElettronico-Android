package com.sharpdroid.registroelettronico.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Note
import kotlinx.android.synthetic.main.adapter_note.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter per RecyclerView con Note disciplinari & Annotazioni
 */
class NoteAdapter(private val mContext: Context) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    private val formatter = SimpleDateFormat("d MMM", Locale.ITALIAN)

    private val data = mutableListOf<Note>()

    fun addAll(note: Collection<Note>) {
        data.addAll(note)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_note, parent, false))

    override fun onBindViewHolder(h: NoteHolder, position: Int) {
        val nota = data[position]
        h.teacher.text = nota.mAuthor
        h.date.text = formatter.format(nota.mDate)
        h.content.text = nota.mText

        if (nota.mType.toLowerCase(Locale.getDefault()).contains("NTST")) {
            h.teacher.setTextColor(ContextCompat.getColor(mContext, R.color.deep_orange))
            h.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_warning_orange))
        } else {
            h.teacher.setTextColor(ContextCompat.getColor(mContext, R.color.grey_middle))
            h.icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_error_grey))
        }
    }

    override fun getItemCount() = data.size


    inner class NoteHolder(l: View) : RecyclerView.ViewHolder(l) {
        var teacher: TextView = l.teacher
        var date: TextView = l.date
        var content: TextView = l.content
        var icon: ImageView = l.icon
    }
}
