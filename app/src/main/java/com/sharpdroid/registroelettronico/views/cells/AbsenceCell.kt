package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.Absence
import com.sharpdroid.registroelettronico.utils.Metodi
import kotlinx.android.synthetic.main.adapter_absence.view.*
import java.text.SimpleDateFormat
import java.util.*

class AbsenceCell(context: Context) : FrameLayout(context) {
    var absence: Absence? = null
    private val longDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN)


    init {
        View.inflate(context, R.layout.adapter_absence, this)
    }

    fun bindData(absence: Absence) {
        when (absence.type) {
            "ABR0" -> {
                hour.text = context.resources.getString(R.string.hours, "entrato", absence.hPos)
                type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.orangematerial)))
                type.text = "R"

            }
            "ABR1" -> {
                hour.text = context.resources.getString(R.string.short_delay)
                type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.orangematerial)))
                type.text = "RB"
            }
            "ABU0" -> {
                hour.text = context.resources.getString(R.string.hours, "uscito", absence.hPos)
                type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.bluematerial)))
                type.text = "U"
            }
            "ABA0" -> {
                hour.text = context.resources.getQuantityString(R.plurals.days, 1, 1)
                type_color.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.redmaterial)))
                type.text = "A"
            }
        }

        date.text = Metodi.capitalizeFirst(longDateFormat.format(absence.date))
        done.visibility = if (absence.justified) View.VISIBLE else View.INVISIBLE
    }
}