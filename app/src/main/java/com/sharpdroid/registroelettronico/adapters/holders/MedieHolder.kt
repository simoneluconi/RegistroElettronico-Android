package com.sharpdroid.registroelettronico.adapters.holders

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.activities.MarkSubjectDetailActivity
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.utils.Metodi.*
import kotlinx.android.synthetic.main.adapter_medie_grid.view.*
import java.util.*

class MedieHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.adapter_medie_grid, parent, false)) {

    fun bindTo(average: Average, period: Int, context: Context) {
        itemView.cardview_medie.setOnClickListener {
            context.startActivity(Intent(context, MarkSubjectDetailActivity::class.java)
                    .putExtra("subject_id", average.code)
                    .putExtra("period", period))
        }
        itemView.materia.text = capitalizeEach(average.name)

        if (average.avg != 0f) {
            itemView.media.text = String.format(Locale.getDefault(), "%.2f", average.avg)

            var target = average.target

            val globalTarget = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("voto_obiettivo", "8")

            if (target <= 0) {
                target = if (globalTarget == "Auto") {
                    getPossibileSubjectTarget(average.avg.toDouble()).toFloat()
                } else
                    globalTarget.toFloat()

            }
            itemView.custom_progressBar.setProgress(average.avg * 10)
            itemView.custom_progressBar.setColor(ContextCompat.getColor(context, getMediaColor(average.avg, target)))

            itemView.descrizione.text = MessaggioVoto(target, average.avg, average.count)
        } else {
            itemView.custom_progressBar.setProgress(100f)
            itemView.custom_progressBar.setColor(ContextCompat.getColor(context, R.color.intro_blue))

            itemView.media.text = "-"
            itemView.descrizione.text = context.getString(R.string.nessun_voto_numerico)
        }
    }
}
