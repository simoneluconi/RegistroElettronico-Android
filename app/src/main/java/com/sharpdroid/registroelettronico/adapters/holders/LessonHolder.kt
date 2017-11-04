package com.sharpdroid.registroelettronico.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.sharpdroid.registroelettronico.R

class LessonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var content = itemView.findViewById<TextView>(R.id.content)
    var date = itemView.findViewById<TextView>(R.id.date)
}