package com.sharpdroid.registroelettronico.Adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.Adapters.Holders.AbsencesHolder
import com.sharpdroid.registroelettronico.Adapters.Holders.HeaderHolder
import com.sharpdroid.registroelettronico.Databases.Entities.Absence
import com.sharpdroid.registroelettronico.Interfaces.Client.AbsenceEntry
import com.sharpdroid.registroelettronico.Interfaces.Client.DelayEntry
import com.sharpdroid.registroelettronico.Interfaces.Client.ExitEntry
import com.sharpdroid.registroelettronico.Interfaces.Client.HeaderEntry
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeFirst
import java.text.SimpleDateFormat
import java.util.*

class AllAbsencesAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val long_date_format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN)

    private var CVDataList: MutableList<in Any> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0)
            HeaderHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_header, parent, false))
        else
            AbsencesHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_absence, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = CVDataList[position]
        if (holder is HeaderHolder) {
            holder.content.text = (entry as HeaderEntry).title
        } else {
            val absencesHolder = holder as AbsencesHolder

            if (entry is DelayEntry) {
                val delay = entry.delay

                absencesHolder.date.text = capitalizeFirst(long_date_format.format(delay.day))
                absencesHolder.hour.text = if (delay.hour === 0) mContext.resources.getString(R.string.short_delay) else mContext.resources.getString(R.string.hours, "entrato", delay.hour)
                absencesHolder.done.visibility = if (delay.isDone) View.VISIBLE else View.INVISIBLE
                absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.orangematerial)))
                absencesHolder.type_text.text = if (delay.hour === 0) "RB" else "R"
            } else if (entry is AbsenceEntry) {
                val absence = entry.absence

                absencesHolder.date.text = capitalizeFirst(long_date_format.format(absence.from))
                absencesHolder.hour.text = mContext.resources.getQuantityString(R.plurals.days, absence.days, absence.days)
                absencesHolder.done.visibility = if (absence.isDone) View.VISIBLE else View.INVISIBLE
                absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.redmaterial)))
                absencesHolder.type_text.text = "A"
            } else {
                val exit = (entry as ExitEntry).exit

                absencesHolder.date.text = capitalizeFirst(long_date_format.format(exit.day))
                absencesHolder.hour.text = mContext.resources.getString(R.string.hours, "uscito", exit.hour)
                absencesHolder.done.visibility = if (exit.isDone) View.VISIBLE else View.INVISIBLE
                absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.bluematerial)))
                absencesHolder.type_text.text = "U"
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (CVDataList[position] is HeaderEntry) 0 else 1
    }

    override fun getItemCount(): Int {
        return CVDataList.size
    }

    fun addAll(absences: List<Absence>) {
        if (absences.isEmpty()) return
        val list = absences.sortedWith(kotlin.Comparator { t: Absence, t1: Absence -> t.date.compareTo(t1.date) })

        list.map {

        }



        notifyDataSetChanged()
    }

    fun clear() {
        CVDataList.clear()
        notifyDataSetChanged()
    }
}
