package com.sharpdroid.registroelettronico.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.adapters.holders.MedieHolder
import com.sharpdroid.registroelettronico.database.entities.Average
import java.util.*

class MedieAdapter(private val mContext: Context) : RecyclerView.Adapter<MedieHolder>() {
    private val data = ArrayList<Average>()
    private var period: Int = 0

    fun addAll(list: List<Average>, p: Int) {
        data.clear()
        data.addAll(list)
        this.period = p

        notifyDataSetChanged()
    }

    fun getAll() = ArrayList(data)

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MedieHolder(parent)

    override fun onBindViewHolder(ViewHolder: MedieHolder, position: Int) {
        ViewHolder.bindTo(data[position], period, mContext)
    }

    override fun getItemCount() = data.size
}
