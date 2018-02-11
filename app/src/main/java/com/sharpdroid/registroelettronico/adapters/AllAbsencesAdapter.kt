package com.sharpdroid.registroelettronico.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.holders.AbsencesHolder
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.Absence
import com.sharpdroid.registroelettronico.database.entities.MyAbsence
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.utils.Metodi.month_year
import java.text.SimpleDateFormat
import java.util.*

class AllAbsencesAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val longDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN)
    private val data: MutableList<in Any> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0)
            Holder(LayoutInflater.from(mContext).inflate(R.layout.adapter_header, parent, false))
        else
            AbsencesHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_absence, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = data[position]
        when (entry) {
            is String -> (holder.itemView as TextView).text = entry
            is Absence -> {
                val absencesHolder = holder as AbsencesHolder

                when (entry.type) {
                    "ABR0" -> {
                        absencesHolder.hour.text = mContext.resources.getString(R.string.hours, "entrato", entry.hPos)
                        absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.orangematerial)))
                        absencesHolder.type_text.text = "R"

                    }
                    "ABR1" -> {
                        absencesHolder.hour.text = mContext.resources.getString(R.string.short_delay)
                        absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.orangematerial)))
                        absencesHolder.type_text.text = "RB"
                    }
                    "ABU0" -> {
                        absencesHolder.hour.text = mContext.resources.getString(R.string.hours, "uscito", entry.hPos)
                        absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.bluematerial)))
                        absencesHolder.type_text.text = "U"
                    }
                }

                absencesHolder.date.text = capitalizeFirst(longDateFormat.format(entry.date))
                absencesHolder.done.visibility = if (entry.justified) View.VISIBLE else View.INVISIBLE
            }
            is MyAbsence -> {
                val absencesHolder = holder as AbsencesHolder

                absencesHolder.date.text = capitalizeFirst(longDateFormat.format(entry.absence.date))
                absencesHolder.hour.text = mContext.resources.getQuantityString(R.plurals.days, entry.days, entry.days)
                absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.redmaterial)))
                absencesHolder.type_text.text = "A"
            }
        }
    }

    override fun getItemViewType(position: Int) = if (data[position] is String) 0 else 1

    override fun getItemCount() = data.size

    fun addAll(absences: Array<Any>) {
        if (absences.isEmpty()) return

        val list = absences.sortedByDescending {
            (it as? Absence)?.date ?: ((it as? MyAbsence)?.absence?.date ?: Date(0))
        }
        val hashmap = linkedMapOf<String, MutableCollection<Any>>()

        list.forEach {
            val date = (it as? Absence)?.date ?: ((it as? MyAbsence)?.absence?.date ?: Date(0))
            val l = (hashmap[month_year.format(date)] ?: emptyList<Any>()).toMutableList()
            l.add(it)
            hashmap[month_year.format(date)] = l
        }

        val finalList = mutableListOf<Any>()

        hashmap.forEach {
            finalList.add(it.key)
            it.value.toCollection(finalList)
        }

        data.addAll(finalList)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }
}
