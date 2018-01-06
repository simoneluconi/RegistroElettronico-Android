package com.sharpdroid.registroelettronico.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.adapters.CommunicationAdapter
import com.sharpdroid.registroelettronico.database.entities.Communication
import kotlinx.android.synthetic.main.adapter_communications.view.*
import java.text.SimpleDateFormat

class CommunicationHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.adapter_communications, parent, false)) {

    fun bindTo(communication: Communication, formatter: SimpleDateFormat,
               listener: CommunicationAdapter.DownloadListener) {
        itemView.title.text = communication.title
        itemView.date.text = formatter.format(communication.date)
        itemView.type.text = communication.category.orEmpty()
        itemView.attachment.visibility = if (communication.hasAttachment) View.VISIBLE else View.GONE
        itemView.relative_layout.setOnClickListener({
            itemView.relative_layout.postDelayed({
                listener.onCommunicationClick(communication)
            }, ViewConfiguration.getTapTimeout().toLong())
        })
    }
}
