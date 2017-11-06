package com.sharpdroid.registroelettronico.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.adapter_absence.view.*


class AbsencesHolder(l: View) : RecyclerView.ViewHolder(l) {
    var date: TextView = l.date
    var hour: TextView = l.hour
    var type_text: TextView = l.type
    var done: ImageView = l.done
    var type_color: CircleImageView = l.type_color
    var layout: View = l.layout
}