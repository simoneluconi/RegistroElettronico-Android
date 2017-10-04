package com.sharpdroid.registroelettronico.Activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.github.mikephil.charting.data.Entry
import com.orm.SugarRecord
import com.orm.dsl.Column
import com.sharpdroid.registroelettronico.API.V1.SpiaggiariAPI
import com.sharpdroid.registroelettronico.Databases.Entities.Grade
import com.sharpdroid.registroelettronico.Databases.Entities.Subject
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.Utils.Account
import com.sharpdroid.registroelettronico.Utils.Metodi.MessaggioVoto
import com.sharpdroid.registroelettronico.Utils.Metodi.capitalizeEach
import kotlinx.android.synthetic.main.activity_mark_subject_detail.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

// DONE: 03/12/2016 Dettagli (nome, aula, prof, ora, note, colore)
// DONE: 03/12/2016 Media (scritto, orale, totale)
// DONE: 03/12/2016 Obiettivo
// TODO: 03/12/2016 Orario settimanale (in quali giorni)
// TODO: 03/12/2016 Verifiche prossime
// TODO: 19/01/2016 Media Ipotetica
// DONE: 03/12/2016 Voti recenti
// DONE: 14/12/2016 Lezioni recenti


class MarkSubjectDetailActivity : AppCompatActivity() {

    data class AverageType(@Column(name = "ID") val ignore: Long, @Column(name = "AVG") val avg: Float, @Column(name = "TYPE") val type: String, @Column(name = "COUNT") val count: Int) {
        constructor() : this(-1L, -1f, "", 0)
    }

    lateinit var subject: Subject
    var p: Int = 0
    lateinit var avg: AverageType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_subject_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    override fun onResume() {
        super.onResume()

        subject = SugarRecord.findById(Subject::class.java, intent.getIntExtra("subject_id", -1))
        p = intent.getIntExtra("period", 0)
        avg = SugarRecord.findWithQuery(AverageType::class.java, "SELECT ID, AVG(M_VALUE) as AVG , 'Generale' as TYPE, COUNT(M_VALUE) as COUNT  FROM GRADE WHERE M_VALUE!=0 AND M_SUBJECT_ID=?", subject.id.toString())[0]


        title = capitalizeEach(subject.description)

        setInfo(subject)
        setOverall()
        setTarget()
        setLessons(subject.id)
        setMarks()
    }

    private fun setInfo(subject: Subject) {
        info.setSubjectDetails(subject)
        info.setEditListener { _ -> startActivity(Intent(this, EditSubjectDetailsActivity::class.java).putExtra("code", subject.id)) }
    }

    private fun setOverall() {
        val avgTypes = SugarRecord.findWithQuery(AverageType::class.java, "SELECT 0 as ID, AVG(M_VALUE) as AVG, M_TYPE as TYPE FROM GRADE WHERE M_VALUE!=0 AND M_SUBJECT_ID=? GROUP BY TYPE", subject.id.toString())
        overall.setOrale(avgTypes.filter { it.type.equals(SpiaggiariAPI.ORALE, false) }.getOrNull(0)?.avg)
        overall.setScritto(avgTypes.filter { it.type.equals(SpiaggiariAPI.SCRITTO, false) }.getOrNull(0)?.avg)
        overall.setPratico(avgTypes.filter { it.type.equals(SpiaggiariAPI.PRATICO, false) }.getOrNull(0)?.avg)

        overall.visibility = if (avgTypes.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun getTarget(): Float {
        var target = subject.target
        if (target <= 0) {
            target = java.lang.Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8"))
        }
        return target
    }

    private fun setTarget() {

        target.target = getTarget()

        //set progress
        if (avg.avg != 0f) {
            target.setProgress(avg.avg)
        } else {
            target.visibility = View.GONE
        }

        //set listener for button

        target.setListener({ _ ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(getString(R.string.obiettivo_summary))

            val v = layoutInflater.inflate(R.layout.fragment_imposta_obiettivo, null)
            val mSeekBar = v.findViewById<SeekBar>(R.id.seekbar)
            val mValueText = v.findViewById<TextView>(R.id.value)
            mSeekBar.progress = getTarget().toInt()
            mValueText.text = String.format(Locale.getDefault(), "%.0f", getTarget())

            alert.setView(v)

            alert.setPositiveButton(android.R.string.ok
            ) { _, _ -> register(mSeekBar.progress.toFloat()) }


            mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    mValueText.text = String.format(Locale.getDefault(), "%d", progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

            alert.show()
        }) { _ ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(MessaggioVoto(target.target, avg.avg, avg.count))
            alert.show()
        }
    }

    private fun register(new_target: Float) {
        target.target = new_target
        marks!!.setTarget(new_target)

        val values = ContentValues()
        values.put("target", new_target.toInt().toString())
        subject.target = new_target
        SugarRecord.save(subject)
        marks!!.setLimitLines(new_target, avg.avg)
    }

    private fun setLessons(code: Long) {
        lessons.update(code.toInt())
    }

    private fun setMarks() {
        marks.setSubject(subject, avg.avg)
        marks.addAll(SugarRecord.find(Grade::class.java, (if (p != -1) "M_PERIOD='$p' AND" else "") + " PROFILE=? AND M_SUBJECT_ID=? ORDER BY M_DATE DESC", Account.with(this).user.toString(), subject.id.toString()))
        marks.setChart(SugarRecord.findWithQuery(Entry::class.java, "SELECT ID, M_DATE as X, M_VALUE as Y FROM GRADE WHERE M_SUBJECT_ID=? AND M_VALUE!=0", subject.id.toString()))
        marks.setShowChart(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_chart", true) && marks.itemCount > 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }

}
