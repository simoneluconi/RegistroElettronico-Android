package com.sharpdroid.registroelettronico.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.sharpdroid.registroelettronico.adapters.holders.CommunicationHolder
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.fragments.FragmentCommunications
import java.text.SimpleDateFormat
import java.util.*

class CommunicationAdapter(fragmentCommunications: FragmentCommunications) : RecyclerView.Adapter<CommunicationHolder>(), Filterable {
    private val CVDataList = mutableListOf<Communication>()
    private val filtered = mutableListOf<Communication>()
    private val formatter = SimpleDateFormat("d MMM", Locale.ITALIAN)
    private val listener: DownloadListener

    init {
        listener = fragmentCommunications
    }

    fun addAll(list: Collection<Communication>) {
        CVDataList.addAll(list)
        filtered.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        CVDataList.clear()
        filtered.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommunicationHolder(parent)

    override fun onBindViewHolder(holder: CommunicationHolder, position: Int) {
        holder.bindTo(filtered[position], formatter, listener)
    }

    override fun getItemCount() = filtered.size

    override fun getFilter() = ItemFilter()

    interface DownloadListener {
        fun onCommunicationClick(communication: Communication)
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val list = arrayListOf<Communication>()
            val filterResults = Filter.FilterResults()

            if (constraint.isNotBlank()) {
                CVDataList.filterTo(list) {
                    it.title.toLowerCase().contains(constraint.toString().toLowerCase())
                }
            } else {
                list.addAll(CVDataList)
            }

            filterResults.values = list
            filterResults.count = list.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            filtered.clear()
            filtered.addAll(results.values as Collection<Communication>)
            notifyDataSetChanged()
        }
    }
}
