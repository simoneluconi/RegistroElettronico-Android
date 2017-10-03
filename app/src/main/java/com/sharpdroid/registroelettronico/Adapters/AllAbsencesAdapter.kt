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
import com.sharpdroid.registroelettronico.Interfaces.Client.HeaderEntry
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.Utils.Metodi.month_year
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
        if (entry is HeaderEntry) {
            (holder as HeaderHolder).content.text = entry.title
        } else if (entry is Absence) {
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
                "ABA0" -> {
                    //absencesHolder.hour.text = mContext.resources.getQuantityString(R.plurals.days, entry.days, absence.days)
                    absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.redmaterial)))
                    absencesHolder.type_text.text = "A"
                }
                "ABU0" -> {
                    absencesHolder.hour.text = mContext.resources.getString(R.string.hours, "entrato", entry.hPos)
                    absencesHolder.type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(mContext, R.color.bluematerial)))
                    absencesHolder.type_text.text = "U"
                }
            }

            absencesHolder.date.text = capitalizeFirst(long_date_format.format(entry.date))
            absencesHolder.done.visibility = if (entry.justified) View.VISIBLE else View.INVISIBLE
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
        val list = absences.sortedWith(kotlin.Comparator { t: Absence, t1: Absence -> t.date.compareTo(t1.date) }).reversed() //ASC

        val beginOfNextMonth = Calendar.getInstance()
        setDateAddMonth(list[0].date, beginOfNextMonth)

        list.forEach {
            if (it.date.time >= beginOfNextMonth.timeInMillis) {
                CVDataList.add(it)
                CVDataList.add(HeaderEntry(month_year.format(beginOfNextMonth.time)))
                setDateAddMonth(it.date, beginOfNextMonth)
            } else {
                CVDataList.add(it)
            }
        }

        CVDataList.reverse()
        notifyDataSetChanged()
    }

    private fun setDateAddMonth(date: Date, calendar: Calendar) {
        with(calendar) {
            time = date
            add(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun clear() {
        CVDataList.clear()
        notifyDataSetChanged()
    }
}
