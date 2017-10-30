package com.sharpdroid.registroelettronico.views.cells

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.SuperAgenda
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import kotlinx.android.synthetic.main.adapter_lesson_2.view.*
import java.util.*

@SuppressLint("ViewConstructor")
class EventCell(context: Context, private val withDateDiff: Boolean) : FrameLayout(context) {
    init {
        inflate(context, R.layout.adapter_lesson_2, this)

        duration.visibility = if (withDateDiff) View.VISIBLE else View.GONE
        circleImageView2.visibility = if (withDateDiff) View.VISIBLE else View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            (linearLayout2.layoutParams as ConstraintLayout.LayoutParams).marginStart = dp(if (circleImageView2.visibility == View.VISIBLE) 72 else 16)
        }
        (linearLayout2.layoutParams as ConstraintLayout.LayoutParams).leftMargin = dp(if (circleImageView2.visibility == View.VISIBLE) 72 else 16)

    }

    @SuppressLint("SetTextI18n")
    fun bindData(event: Any, currentDate: Date) {
        when (event) {
            is SuperAgenda -> {
                val spannableString = SpannableString(event.agenda.notes)
                if (event.completed) {
                    spannableString.setSpan(StrikethroughSpan(), 0, event.agenda.notes.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                if (withDateDiff)
                    duration.text = "${(event.agenda.start.time - currentDate.time) / (24 * 3600000)}g"
                //date.text = dateFormat.format(event.agenda.start)
                date.text = capitalizeEach(event.agenda.author, true)
                content.text = spannableString
                circleImageView2.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, if (event.test) R.color.deep_orange else R.color.light_green)))
            }
            is LocalAgenda -> {
                val spannableString = SpannableString(event.title)
                if (event.completed_date != null) {
                    spannableString.setSpan(StrikethroughSpan(), 0, event.title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                if (withDateDiff)
                    duration.text = "${(event.day.time - currentDate.time) / (24 * 3600000)}g"
                //date.text = dateFormat.format(event.day)
                date.text = capitalizeEach(event.teacher.teacherName, true)
                content.text = spannableString
                //notes.text = event.content.trim({ it <= ' ' })
                circleImageView2.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, if (event.type.equals("verifica", true)) R.color.deep_orange else R.color.light_green)))
            }
            else -> throw IllegalStateException("Allowed data types: SuperAgenda, LocalAgenda\nFound: '${event::class.java.canonicalName}'")
        }
    }

}