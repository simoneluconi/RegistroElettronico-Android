package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.SuperAgenda
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.adapter_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventCell(context: Context) : FrameLayout(context) {
    private val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())

    init {
        inflate(context, R.layout.adapter_event, this)
        divider.visibility = View.GONE
    }

    fun bindData(event: Any) {
        when (event) {
            is SuperAgenda -> {
                val spannableString = SpannableString(event.agenda.notes)
                if (event.completed) {
                    spannableString.setSpan(StrikethroughSpan(), 0, event.agenda.notes.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                date.text = dateFormat.format(event.agenda.start)
                subject.text = capitalizeEach(event.agenda.author, true)
                title.text = spannableString
                notes.visibility = View.GONE
            }
            is LocalAgenda -> {
                val spannableString = SpannableString(event.title)
                if (event.completed_date != null) {
                    spannableString.setSpan(StrikethroughSpan(), 0, event.title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                date.text = dateFormat.format(event.day)
                subject.text = capitalizeEach(event.teacher.teacherName, true)
                title.text = spannableString
                notes.text = event.content.trim({ it <= ' ' })

                notes.visibility = if (event.content.isEmpty()) View.GONE else View.VISIBLE
            }
            else -> throw IllegalStateException("Allowed data types: SuperAgenda, LocalAgenda\nFound: '${event::class.java.canonicalName}'")
        }
    }

}