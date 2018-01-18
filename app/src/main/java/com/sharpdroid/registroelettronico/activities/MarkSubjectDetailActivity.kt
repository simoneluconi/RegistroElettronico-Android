package com.sharpdroid.registroelettronico.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
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
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.SeekBar
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.github.mikephil.charting.data.Entry
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.api.spaggiari.SpaggiariConst
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.LocalGrade
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.Metodi.getMessaggioVoto
import com.sharpdroid.registroelettronico.viewModels.SubjectDetailsViewModel
import com.sharpdroid.registroelettronico.views.subjectDetails.HypotheticalView
import kotlinx.android.synthetic.main.activity_mark_subject_detail.*
import kotlinx.android.synthetic.main.adapter_mark.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_imposta_obiettivo.view.*
import kotlinx.android.synthetic.main.view_dialog_add_grade.view.*
import kotlinx.android.synthetic.main.view_marks.view.*
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
    private lateinit var avg: LiveData<AverageType>
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
        avg = DatabaseHelper.database.subjectsDao().getAverage(subjectId, account)
        p = intent.getIntExtra("period", 0)

        //Do not animate layout on rotation change
        if (savedInstanceState != null) {
            viewModel.animateLocalMarks.value = false
            viewModel.animateTarget.value = false
        }

        avg.observe(this, Observer { avg ->
            if (avg == null) return@Observer

            //chart
            subjectInfo()?.let {
                marks.setupAvgAndTarget(it, avg.avg())
            }
            subjectPojo()?.let {
                target.setTarget(avg.avg(), getTarget(), viewModel.animateTarget.value == true)
                viewModel.animateTarget.value == true
            }

            //hypothetical
            hypothetical.setRealData(avg)
        })

        //DETTAGLI
        viewModel.getSubject(subjectId, Account.with(this).user).observe(this, Observer {
            it?.subject?.teachers = DatabaseHelper.database.subjectsDao().getTeachersOfSubject(it?.subject?.id
                    ?: 0, account)
            title = capitalizeEach(it?.getSubjectName().orEmpty())
            initInfo(it ?: return@Observer)

            //set animateTarget to true for the next observe
            avg.value?.let { avg ->
                initTarget(avg, viewModel.animateTarget.value == true)
            }
            //viewModel.animateTarget.value = true
            //marks.setupAvgAndTarget(it.subjectInfo.getOrNull(0), avg.avg())
            hypothetical.setTarget(getTarget())
        })

        //AVERAGES
        viewModel.getAverages(account, subjectId, p).observe(this, Observer {
            initOverall(it.orEmpty())
        })

        //MARKS - CHART & LIST
        viewModel.getGrades(account, subjectId, p).observe(this, Observer {
            initMarks(it.orEmpty())

            nested_scroll_view.postDelayed({
                //Let em load
                val pref = PreferenceManager.getDefaultSharedPreferences(this)
                if (!pref.getBoolean("has_seen_feature_HIDE_MARKS_", false) && marks.count() > 0 && savedInstanceState == null) {
                    val location = Array(2, { 0 }).toIntArray()
                    marks.header.getLocationOnScreen(location)
                    val animator = ObjectAnimator.ofInt(nested_scroll_view, "scrollY", location[1] - dp(80)).setDuration(1000)
                    animator.interpolator = DecelerateInterpolator(1.4f)
                    animator.start()

                    nested_scroll_view.postDelayed({
                        TapTargetView.showFor(this,
                                TapTarget.forView(marks.recycler.getChildAt(0).relativeLayout, "Nascondi voti", "Cliccando puoi scegliere i voti che desideri escludere dalle medie.")
                                        .transparentTarget(true)
                                , object : TapTargetView.Listener() {})

                        pref.edit().putBoolean("has_seen_feature_HIDE_MARKS_", true).apply()
                    }, animator.duration)
                }
            }, 400)
        })

        with(hypothetical) {
            delegate = this@MarkSubjectDetailActivity
            //setRealData(avg.value)
            //setTarget(getTarget(viewModel.subjectInfo?.value))  ----- ALREADY CALLING IN viewModel.getSubject.observe
        }
        //HYPOTHETICAL MARKS
        viewModel.getLocalGrades(account, subjectId, p).observe(this, Observer { it ->
            hypothetical.setHypoGrades(it.orEmpty(), viewModel.animateLocalMarks.value == true)
            viewModel.animateLocalMarks.value = true
        })

        //LESSONS
        DatabaseHelper.database.lessonsDao().loadLastLessons(subjectId, account).observe(this, Observer {
            lessons.update(it.orEmpty(), subjectId.toInt())
        })

        //STATS
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Materia").putContentType("Dettagli"))

        //RESTORE SCROLL
        if (savedInstanceState != null && savedInstanceState["scrollY"] != null) {
            nested_scroll_view.postDelayed({
                nested_scroll_view.scrollY = savedInstanceState.getInt("scrollY")
            }, 20)
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("scrollY", nested_scroll_view.scrollY)
    }

    private fun initInfo(subject: SubjectPOJO) {
        info.setSubjectDetails(subject)
        info.setEditListener {
            startActivity(Intent(this, EditSubjectDetailsActivity::class.java).putExtra("code", subject.subject.id))
        }
    }

    private fun initOverall(avgTypes: List<AverageType>) {
        avgTypes.filter { it.type.equals(SpaggiariConst.ORALE, false) }.getOrNull(0)?.let { if (it.count > 0) overall.setOrale(it.sum / it.count) }
        avgTypes.filter { it.type.equals(SpaggiariConst.SCRITTO, false) }.getOrNull(0)?.let { if (it.count > 0) overall.setScritto(it.sum / it.count) }
        avgTypes.filter { it.type.equals(SpaggiariConst.PRATICO, false) }.getOrNull(0)?.let { if (it.count > 0) overall.setPratico(it.sum / it.count) }

        overall.visibility = if (avgTypes.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun getTarget(): Float {
        var target = subjectInfo()?.target ?: -1f
        if (target <= 0) {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString("voto_obiettivo", "8")
            target = if (pref == "Auto") Math.ceil(avg.value?.avg()?.toDouble()
                    ?: .0).toFloat() else pref.toFloat()
        }
        return target
    }

    @SuppressLint("InflateParams")
    private fun initTarget(avg: AverageType, animate: Boolean) {

        //update label, progress bar and color
        target.setTarget(avg.avg(), getTarget(), animate)

        //update listeners
        target.setButtonsListener(View.OnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(getString(R.string.obiettivo_summary))

            val v = layoutInflater.inflate(R.layout.fragment_imposta_obiettivo, null)
            v.seekbar.progress = getTarget().toInt()
            v.value.text = String.format(Locale.getDefault(), "%.0f", getTarget())

            alert.setView(v)

            alert.setPositiveButton(android.R.string.ok
            ) { _, _ -> updateTarget(v.seekbar.progress.toFloat(), avg.avg()) }


            v.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    v.value.text = String.format(Locale.getDefault(), "%d", progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

            alert.show()
        }, View.OnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.obiettivo_title))
            alert.setMessage(getMessaggioVoto(target.target, avg.avg(), avg.count))
            alert.show()
        })
    }

    private fun updateTarget(new_target: Float, avg: Float) {
        val subject = subjectInfo() ?: SubjectInfo()
        subject.target = new_target
        subject.profile = Account.with(this).user
        viewModel.subjectInfo?.value?.subject?.id?.let { subject.subject = it }

        DatabaseHelper.database.subjectsDao().insert(subject)

        //Update chart and marks' colors
        marks.setupAvgAndTarget(subject, avg)
        hypothetical.setTarget(getTarget())
    }

    private fun initMarks(data: List<Grade>) {
        marks.setupAvgAndTarget(subjectInfo(), avg.value?.avg() ?: 0f)

        //List -> Adapter load
        marks.addAll(data.reversed())

        //List -> Click listener
        marks.markClickListener = { grade ->
            if (grade.isExcluded()) {
                MaterialDialog.Builder(this)
                        .title("Ripristinare?")
                        .content("Vuoi ripristinare il voto selezionato nelle medie dei voti?")
                        .positiveText("Sì")
                        .neutralText("No")
                        .onPositive { _, _ ->
                            grade.exclude(false)
                            initMarks(viewModel.grades?.value.orEmpty())
                        }.show()
            } else {
                MaterialDialog.Builder(this)
                        .title("Escludere?")
                        .content("Vuoi escludere il voto selezionato dalle medie dei voti?")
                        .positiveText("Sì")
                        .neutralText("No")
                        .onPositive { _, _ ->
                            grade.exclude(true)
                            initMarks(viewModel.grades?.value.orEmpty())
                        }.show()
            }
        }


        //Exclude not significant marks
        val filter = data.filter { it.mValue != 0f && !it.isExcluded() }.map { Entry(it.mDate.time.toFloat(), it.mValue) }.sortedBy { it.x }

        //Setup chart
        marks.setChart(filter)
        marks.setShowChart(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_chart", true) && filter.size > 1)
    }

    /**
     * Listener for "Add" button
     */
    override fun hypotheticalAddListener() {
        val view = LayoutInflater.from(this).inflate(R.layout.view_dialog_add_grade, null)
        val grade = LocalGrade(0f, "", viewModel.subjectInfo?.value?.subject?.id
                ?: 0, p, "Generale", Account.with(this).user, 0)

        view.voto?.setSelection(resources.getStringArray(R.array.marks_list).size / 2)
        view.voto?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    /**
     * Click listener for hypothetical grades
     */
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

    fun subjectInfo() = viewModel.subjectInfo?.value?.subjectInfo?.getOrNull(0)
    private fun subjectPojo() = viewModel.subjectInfo?.value

}
