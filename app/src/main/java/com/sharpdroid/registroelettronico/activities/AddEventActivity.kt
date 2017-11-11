package com.sharpdroid.registroelettronico.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.sharpdroid.registroelettronico.BuildConfig
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.Teacher
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Account
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeFirst
import com.sharpdroid.registroelettronico.utils.flat
import com.sharpdroid.registroelettronico.utils.or
import com.sharpdroid.registroelettronico.views.localEvent.OptionView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_add_event.*
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    internal var format = SimpleDateFormat("EEEE d MMMM yyyy", Locale.ITALIAN)
    lateinit private var animShake: Animation
    lateinit private var vibrator: Vibrator

    private var selectedSubject: SubjectPOJO? = null
    private var selectedProfessor: Teacher? = null
    private var selectedDay: Date? = null
    private val subjectList by lazy { DatabaseHelper.database.subjectsDao().getSubjectsWithInfoBlocking(Account.with(this).user).sortedBy { it.subjectInfo.getOrNull(0)?.description?.or(it.subject.description) } }
    private val professorList by lazy { DatabaseHelper.database.subjectsDao().getTeachersOfProfile(Account.with(this).user) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        setSupportActionBar(toolbar)

        supportActionBar?.title = ""
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        selectedDay = Date(intent.getLongExtra("time", 0))
        init(intent.getStringExtra("type"))
        animShake = AnimationUtils.loadAnimation(this, R.anim.shake)
        if (!BuildConfig.DEBUG)
            Answers.getInstance().logContentView(ContentViewEvent().putContentId("Evento").putContentType("Nuovo"))

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private fun init(type: String) {
        title = type
        confirm.setOnClickListener { _ -> handleConfirm(type) }
        initDefault()
    }

    private fun handleConfirm(type: String) {
        when (type.toLowerCase()) {
            "verifica" -> if (handleTitle() && handleSubject() && handleProfessor() && handleDate()) {
                DatabaseHelper.database.eventsDao().insert(LocalAgenda(0L, layout_verifica.editText!!.text.toString(), layout_note.editText!!.text.toString(), type, selectedDay?.flat()!!.time, selectedSubject?.subject?.id ?: 0L, selectedProfessor?.id ?: 0, Date(0), Account.with(this).user, false))
                finish()
            } else {
                vibrate()
            }
            "compiti" -> if (handleTitle() && handleSubject() && handleProfessor() && handleDate()) {
                DatabaseHelper.database.eventsDao().insert(LocalAgenda(0L, layout_verifica.editText!!.text.toString(), layout_note.editText!!.text.toString(), type, selectedDay?.flat()!!.time, selectedSubject?.subject?.id ?: 0L, selectedProfessor?.id ?: 0, Date(0), Account.with(this).user, false))
                finish()
            } else {
                vibrate()
            }
            else -> if (handleTitle() && handleDate()) {
                DatabaseHelper.database.eventsDao().insert(LocalAgenda(0L, layout_verifica.editText!!.text.toString(), layout_note.editText!!.text.toString(), type, selectedDay?.flat()!!.time, selectedSubject?.subject?.id ?: 0L, selectedProfessor?.id ?: 0, Date(0), Account.with(this).user, false))
                finish()
            } else {
                vibrate()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(40)
        }
    }


    private fun initDefault() {
        options.addView(OptionView.Builder(this).title("Materia").content("Non impostata").image(R.drawable.event_subject).onClick { this.subjectDialog(it) }.build())
        options.addView(OptionView.Builder(this).title("Professore").content("Non impostato").image(R.drawable.event_professor).onClick { this.professorDialog(it) }.build())
        options.addView(OptionView.Builder(this).title("Data").content(capitalizeFirst(format.format(selectedDay))).image(R.drawable.event_date).onClick { this.datePicker(it) }.build())
    }

    private fun subjectDialog(v: View) {
        MaterialDialog.Builder(this)
                .title("Seleziona una materia")
                .items(subjectList.map { capitalizeEach(it.subjectInfo.getOrNull(0)?.description.or(it.subject.description), false) })
                .itemsCallbackSingleChoice(subjectList.indexOf(selectedSubject)) { _, _, which, text ->
                    selectedSubject = subjectList[which]
                    v.findViewById<TextView>(R.id.content).text = text
                    true
                }
                .show()
    }

    private fun professorDialog(v: View) {
        MaterialDialog.Builder(this)
                .title("Seleziona un professore")
                .items(professorList.map { capitalizeEach(it.teacherName, true) })
                .itemsCallbackSingleChoice(professorList.indexOf(selectedProfessor)) { _, _, which, text ->
                    selectedProfessor = professorList[which]
                    v.findViewById<TextView>(R.id.content).text = text
                    true
                }.show()
    }

    private fun datePicker(view: View) {
        val now = Calendar.getInstance()
        now.time = selectedDay

        val dpd = DatePickerDialog.newInstance(null,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            if (selectedDay == null) selectedDay = Date()
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            selectedDay!!.time = cal.timeInMillis
            (view.findViewById<View>(R.id.content) as TextView).text = capitalizeFirst(format.format(selectedDay))
        }
        dpd.show(fragmentManager, "Datepickerdialog")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun setTitle(title: CharSequence) {
        this.layout_verifica.hint = title
    }

    private fun handleTitle(): Boolean {
        val ok = layout_verifica.editText != null && !layout_verifica.editText!!.text.toString().isEmpty()
        if (!ok) {
            layout_verifica.startAnimation(animShake)
            layout_verifica.requestFocus()
        }
        return ok
    }

    private fun handleSubject(): Boolean {
        return selectedSubject != null
    }

    private fun handleProfessor(): Boolean {
        return selectedProfessor != null
    }

    private fun handleDate(): Boolean {
        return selectedDay != null
    }
}
