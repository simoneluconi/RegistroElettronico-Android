package com.sharpdroid.registroelettronico.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.sharpdroid.registroelettronico.adapters.holders.CommunicationHolder
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.fragments.FragmentCommunications
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class CommunicationAdapter(fragmentCommunications: FragmentCommunications) : RecyclerView.Adapter<CommunicationHolder>(), Filterable {
    private val CVDataList = CopyOnWriteArrayList<Communication>()
    private val filtered = CopyOnWriteArrayList<Communication>()
    private val formatter = SimpleDateFormat("d MMM", Locale.ITALIAN)
    private val mFilter = ItemFilter()
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


    override fun onBindViewHolder(holder: CommunicationHolder, i: Int) {
        holder.bindTo(filtered[i], formatter, listener)
    }

    override fun getItemCount() = filtered.size


    override fun getFilter() = mFilter

    interface DownloadListener {
        fun onCommunicationClick(communication: Communication)
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val list = ArrayList<Communication>()
            val filterResults = Filter.FilterResults()

            if (!TextUtils.isEmpty(constraint)) {
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
