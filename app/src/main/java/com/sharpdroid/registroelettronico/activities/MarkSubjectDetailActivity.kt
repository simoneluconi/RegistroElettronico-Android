package com.sharpdroid.registroelettronico.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.github.mikephil.charting.data.Entry
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.api.SpiaggiariAPI
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.LocalGrade
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.database.viewModels.SubjectDetailsViewModel
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.MessaggioVoto
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.or
import com.sharpdroid.registroelettronico.views.subjectDetails.HypotheticalView
import io.reactivex.android.schedulers.AndroidSchedulers
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

    var p: Int = 0
    private lateinit var avg: AverageType
    private lateinit var viewModel: SubjectDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_subject_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProviders.of(this)[SubjectDetailsViewModel::class.java]
        val account = Account.with(this).user
        val subjectId = intent.getIntExtra("subject_id", -1).toLong()
        avg = DatabaseHelper.database.subjectsDao().getAverage(subjectId)
        p = intent.getIntExtra("period", 0)


        //Do not animate layout on rotation change
        if (savedInstanceState != null) {
            viewModel.animateLocalMarks.value = false
            viewModel.animateTarget.value = false
        }

        //DETTAGLI
        viewModel.getSubject(subjectId).observe(this, Observer {
            println("observed")
            it?.subject?.teachers = DatabaseHelper.database.subjectsDao().getTeachersOfSubject(it?.subject?.id ?: 0)
            title = capitalizeEach(it?.subjectInfo?.getOrNull(0)?.description.or(it?.subject?.description.or("")))
            initInfo(it ?: return@Observer)

            //set animateTarget to true for the next observe
            initTarget(avg, it, viewModel.animateTarget.value == true)
            viewModel.animateTarget.value = true
            marks.setSubject(it.subjectInfo.getOrNull(0), avg.avg())
            hypothetical.setTarget(getTarget(viewModel.subjectInfo?.value))
        })

        //AVERAGES
        viewModel.getAverages(account, subjectId, p).observe(this, Observer {
            initOverall(it.orEmpty())
        })


        //LESSONS
        DatabaseHelper.database.lessonsDao().loadLastLessons(subjectId).observe(this, Observer {
            lessons.update(it.orEmpty(), subjectId.toInt())
        })

        with(hypothetical) {
            delegate = this@MarkSubjectDetailActivity
            setRealData(avg)
            setTarget(getTarget(viewModel.subjectInfo?.value))
        }

        viewModel.getLocalGrades(account, subjectId, p).observeOn(AndroidSchedulers.mainThread()).subscribe {
            hypothetical.setHypoGrades(it.orEmpty(), viewModel.animateLocalMarks.value == true)
            viewModel.animateLocalMarks.value = true
        }

        viewModel.getGrades(account, subjectId, p).observe(this, Observer {
            initMarks(it.orEmpty())
        })

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

    private fun initInfo(subject: SubjectPOJO) {
        info.setSubjectDetails(subject)
        info.setEditListener { _ -> startActivity(Intent(this, EditSubjectDetailsActivity::class.java).putExtra("code", subject.subject)) }
    }

    private fun initOverall(avgTypes: List<AverageType>) {
        avgTypes.filter { it.type.equals(SpiaggiariAPI.ORALE, false) }.getOrNull(0)?.let { if (it.count > 0) overall.setOrale(it.sum / it.count) }
        avgTypes.filter { it.type.equals(SpiaggiariAPI.SCRITTO, false) }.getOrNull(0)?.let { if (it.count > 0) overall.setScritto(it.sum / it.count) }
        avgTypes.filter { it.type.equals(SpiaggiariAPI.PRATICO, false) }.getOrNull(0)?.let { if (it.count > 0) overall.setPratico(it.sum / it.count) }

        overall.visibility = if (avgTypes.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun getTarget(subject: SubjectPOJO?): Float {
        var target = subject?.subjectInfo?.getOrNull(0)?.target ?: -1f
        if (target <= 0) {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8")
            target = if (pref == "Auto") Math.ceil(avg.avg().toDouble()).toFloat() else pref.toFloat()
        }
        return target
    }

    private fun initTarget(avg: AverageType, subject: SubjectPOJO?, animate: Boolean) {
        target.setTarget(avg.avg(), getTarget(subject), animate)
        target.setButtonsListener(View.OnClickListener { _ ->
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(getString(R.string.obiettivo_summary))

            val v = layoutInflater.inflate(R.layout.fragment_imposta_obiettivo, null)
            val mSeekBar = v.findViewById<SeekBar>(R.id.seekbar)
            val mValueText = v.findViewById<TextView>(R.id.value)
            mSeekBar.progress = getTarget(subject).toInt()
            mValueText.text = String.format(Locale.getDefault(), "%.0f", getTarget(viewModel.subjectInfo?.value))

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
            alert.setMessage(MessaggioVoto(target.target, avg.avg(), avg.count))
            alert.show()
        })
    }

    private fun updateTarget(new_target: Float) {
        val subject = viewModel.subjectInfo?.value?.subjectInfo?.getOrNull(0) ?: SubjectInfo()
        subject.target = new_target
        subject.profile = Account.with(this).user
        viewModel.subjectInfo?.value?.subject?.id?.let { subject.subject = it }

        DatabaseHelper.database.subjectsDao().insert(subject)

        //Update chart and marks' colors
        marks.setSubject(subject, avg.avg())
        hypothetical.setTarget(getTarget(viewModel.subjectInfo?.value))
    }

    private fun initMarks(data: List<Grade>) {
        marks.setSubject(viewModel.subjectInfo?.value?.subjectInfo?.getOrNull(0), avg.avg())

        val filter = data.filter { it.mValue != 0f }.map { Entry(it.mDate.time.toFloat(), it.mValue) }.sortedWith(kotlin.Comparator { n1, n2 ->
            return@Comparator n1.x.compareTo(n2.x)
        }).toMutableList()

        marks.addAll(data)
        marks.setShowChart(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_chart", true) && filter.size > 1)
        marks.setChart(filter)
    }

    override fun hypotheticalAddListener() {
        val view = LayoutInflater.from(this).inflate(R.layout.view_dialog_add_grade, null)
        val grade = LocalGrade(0f, "", viewModel.subjectInfo?.value?.subject?.id ?: 0, p, "Generale", Account.with(this).user, 0)
        val spinner = view.findViewById<Spinner>(R.id.voto)

        spinner.setSelection(resources.getStringArray(R.array.marks_list).size / 2)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(items: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
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
                            grade.id = DatabaseHelper.database.gradesDao().insertGrade(grade)
                            dialog.dismiss()
                            //hypothetical.add(grade)
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
                    DatabaseHelper.database.gradesDao().deleteGrade(grade)
                    //hypothetical.remove(grade)
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
