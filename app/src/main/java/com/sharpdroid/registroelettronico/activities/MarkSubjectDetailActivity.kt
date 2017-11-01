package com.sharpdroid.registroelettronico.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewConfiguration
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.github.mikephil.charting.data.Entry
import com.orm.SugarRecord
import com.orm.dsl.Column
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.api.SpiaggiariAPI
import com.sharpdroid.registroelettronico.database.entities.*
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.MessaggioVoto
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.or
import com.sharpdroid.registroelettronico.views.subjectDetails.HypotheticalView
import kotlinx.android.synthetic.main.activity_mark_subject_detail.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

// DONE: 03/12/2016 Dettagli (nome, aula, prof, ora, note, colore)
// DONE: 03/12/2016 Media (scritto, orale, totale)
// DONE: 03/12/2016 Obiettivo
// TODO: 03/12/2016 Orario settimanale (in quali giorni)
// TODO: 03/12/2016 Verifiche prossime
// DONE: 1/11/2017 Media Ipotetica
// DONE: 03/12/2016 Voti recenti
// DONE: 14/12/2016 Lezioni recenti


class MarkSubjectDetailActivity : AppCompatActivity(), HypotheticalView.HypotheticalDelegate {

    data class AverageType(@Column(name = "AVG") val avg: Float, @Column(name = "TYPE") val type: String, @Column(name = "COUNT") val count: Int) {
        constructor() : this(-1f, "", 0)
    }

    lateinit var subject: SubjectInfo
    var p: Int = 0
    private lateinit var avg: AverageType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_subject_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Materia").putContentType("Dettagli"))


        if (savedInstanceState != null && savedInstanceState["scrollY"] != null) {
            nested_scroll_view.scrollY = savedInstanceState.getInt("scrollY")
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("scrollY", nested_scroll_view.scrollY)
    }

    override fun onResume() {
        super.onResume()
        val temp = SugarRecord.findById(Subject::class.java, intent.getIntExtra("subject_id", -1))
        if (temp == null) {
            onBackPressed()
            return
        }

        subject = temp.getInfo(this)
        subject.subject.teachers = SugarRecord.findWithQuery(Teacher::class.java, "SELECT * FROM TEACHER WHERE TEACHER.ID IN (SELECT SUBJECT_TEACHER.TEACHER FROM SUBJECT_TEACHER WHERE SUBJECT_TEACHER.SUBJECT=?)", subject.subject.id.toString())
        p = intent.getIntExtra("period", 0)
        avg = SugarRecord.findWithQuery(AverageType::class.java, "SELECT ID, AVG(M_VALUE) as `AVG` , 'Generale' as `TYPE`, COUNT(M_VALUE) as `COUNT`  FROM GRADE WHERE M_VALUE!=0 AND M_SUBJECT_ID=?", subject.subject.id.toString())[0]

        title = capitalizeEach(subject.description.or(subject.subject.description))

        initInfo(subject)
        initOverall(subject.subject)
        initTarget()
        initLessons(subject.subject.id)
        initMarks(subject)
        initHypothetical()
    }

    private fun initInfo(subject: SubjectInfo) {
        info.setSubjectDetails(subject)
        info.setEditListener { _ -> startActivity(Intent(this, EditSubjectDetailsActivity::class.java).putExtra("code", subject.subject.id)) }
    }

    private fun initOverall(subject: Subject) {
        val avgTypes: List<AverageType> = SugarRecord.findWithQuery(AverageType::class.java, "SELECT ID, AVG(M_VALUE) as `AVG` ,M_TYPE as `TYPE`, COUNT(M_VALUE) as `COUNT`  FROM GRADE WHERE M_VALUE!=0 AND M_SUBJECT_ID=? GROUP BY TYPE", subject.id.toString())
        overall.setOrale(avgTypes.filter { it.type.equals(SpiaggiariAPI.ORALE, false) }.getOrNull(0)?.avg)
        overall.setScritto(avgTypes.filter { it.type.equals(SpiaggiariAPI.SCRITTO, false) }.getOrNull(0)?.avg)
        overall.setPratico(avgTypes.filter { it.type.equals(SpiaggiariAPI.PRATICO, false) }.getOrNull(0)?.avg)

        overall.visibility = if (avgTypes.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun getTarget(subject: SubjectInfo): Float {
        var target = subject.target
        if (target <= 0) {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8")
            target = (if (pref == "Auto") Math.ceil(avg.avg.toDouble()).toFloat() else pref.toFloat())
        }
        return target
    }

    private fun initTarget() {
        target.setTarget(avg.avg, getTarget(subject), false)
        target.setProgress(avg.avg)

        //set listener for button

        target.setButtonsListener(View.OnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(getString(R.string.obiettivo_summary))

            val v = layoutInflater.inflate(R.layout.fragment_imposta_obiettivo, null)
            val mSeekBar = v.findViewById<SeekBar>(R.id.seekbar)
            val mValueText = v.findViewById<TextView>(R.id.value)
            mSeekBar.progress = getTarget(subject).toInt()
            mValueText.text = String.format(Locale.getDefault(), "%.0f", getTarget(subject))

            alert.setView(v)

            alert.setPositiveButton(android.R.string.ok
            ) { _, _ -> updateTarget(mSeekBar.progress.toFloat()) }


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
        }, View.OnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(MessaggioVoto(target.target, avg.avg, avg.count))
            alert.show()
        })
    }

    private fun updateTarget(new_target: Float) {
        subject.target = new_target
        SugarRecord.save(subject)

        target.postDelayed({ target.setTarget(avg.avg, getTarget(subject), true) }, ViewConfiguration.getTapTimeout().toLong())
        marks.setSubject(subject, avg.avg)

        hypothetical.setTarget(getTarget(subject))
    }

    private fun initLessons(code: Long) {
        lessons.update(code.toInt())
    }

    private fun initMarks(subject_info: SubjectInfo) {
        val subject = subject_info.subject
        marks.setSubject(subject_info, avg.avg)

        val data = SugarRecord.find(Grade::class.java, (if (p != -1) "M_PERIOD='$p' AND" else "") + " PROFILE=? AND M_SUBJECT_ID=? ORDER BY M_DATE DESC", Account.with(this).user.toString(), subject.id.toString())!!
        val filter = data.filter { it.mValue != 0f }.map { Entry(it.mDate.time.toFloat(), it.mValue) }.sortedWith(kotlin.Comparator { n1, n2 ->
            return@Comparator n1.x.compareTo(n2.x)
        }).toMutableList()

        marks.addAll(data)
        marks.setShowChart(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_chart", true) && filter.size > 1)
        marks.setChart(filter)
    }

    private fun initHypothetical() {
        with(hypothetical) {
            delegate = this@MarkSubjectDetailActivity
            setRealData(avg)
            setTarget(getTarget(subject))
            setHypoGrades(SugarRecord.find(LocalGrade::class.java, "PROFILE=${Account.with(this@MarkSubjectDetailActivity).user} AND SUBJECT=${subject.subject.id} " + if (p != -1) "AND PERIOD=$p" else ""))
        }
    }

    override fun hypotheticalAddListener() {
        val view = LayoutInflater.from(this).inflate(R.layout.view_dialog_add_grade, null)
        val grade = LocalGrade(0f, "", subject.subject.id, p, "Generale", Account.with(this).user, 0)
        val spinner = view.findViewById<Spinner>(R.id.voto)

        spinner.setSelection(resources.getStringArray(R.array.marks_list).size / 2)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(items: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                //spinner.post { spinner.setSelection(pos, true) }
                grade.value_name = items?.getItemAtPosition(pos).toString()
                grade.value = resources.getIntArray(R.array.marks_list_values)[pos] / 100f
            }
        }

        MaterialDialog.Builder(this).title("Nuovo voto ipotetico")
                .customView(view, true)
                .positiveText("OK")
                .neutralText("Annulla")
                .onPositive { dialog, _ ->
                    with(dialog.customView) {
                        if (grade.value != 0f) {
                            grade.id = SugarRecord.save(grade)
                            dialog.dismiss()
                            hypothetical.add(grade)
                            //hypothetical.setHypoGrades(SugarRecord.find(LocalGrade::class.java, "PROFILE=${Account.with(this@MarkSubjectDetailActivity).user} AND SUBJECT=${subject.subject.id} " + if (p != -1) "AND PERIOD=$p" else ""))
                        }
                    }
                }.show()
    }

    override fun hypotheticalClickListener(grade: LocalGrade, position: Int) {
        MaterialDialog.Builder(this).title("Eliminare?")
                .content("Sei sicuro di voler eliminare il voto ipotetico selezionato?")
                .positiveText("SI")
                .neutralText("Annulla")
                .onPositive { _, _ ->
                    SugarRecord.delete(grade)
                    hypothetical.remove(grade)
                    //hypothetical.setHypoGrades(SugarRecord.find(LocalGrade::class.java, "PROFILE=${Account.with(this@MarkSubjectDetailActivity).user} AND SUBJECT=${subject.subject.id} " + if (p != -1) "AND PERIOD=$p" else ""))
                }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            super.onBackPressed() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }

}
