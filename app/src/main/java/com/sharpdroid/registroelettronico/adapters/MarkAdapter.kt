package com.sharpdroid.registroelettronico.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.utils.Metodi.getMarkColor
import com.sharpdroid.registroelettronico.utils.Metodi.sortMarksByDate
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.adapter_mark.view.*
import java.text.SimpleDateFormat
import java.util.*

class MarkAdapter(private val mContext: Context) : RecyclerView.Adapter<MarkAdapter.MarkHolder>() {
    internal var target: Float = 0f
    private val format = SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN)
    private val data = mutableListOf<Grade>()

    fun addAll(list: List<Grade>) {
        data.addAll(sortMarksByDate(list))
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun setTarget(t: Float) {
        target = t
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MarkHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_mark, parent, false))

    override fun onBindViewHolder(holder: MarkHolder, position: Int) {

        val mark = data[position]
        holder.color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, getMarkColor(mark.mValue, target))))
        holder.mark.text = mark.mStringValue

        holder.content.text = mark.mNotes.trim { it <= ' ' }

        holder.content.visibility = if (TextUtils.isEmpty(holder.content.text)) View.GONE else View.VISIBLE

        holder.type.text = mark.mType
        holder.date.text = format.format(mark.mDate)
    }

    override fun getItemCount() = data.size

    inner class MarkHolder(l: View) : RecyclerView.ViewHolder(l) {
        var color: CircleImageView = l.color
        var mark: TextView = l.mark
        var content: TextView = l.content
        var date: TextView = l.date
        var type: TextView = l.type
    }
}
